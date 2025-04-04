/*******************************************************************************
 * Copyright 2005-2011, 2024 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *               Mateusz Matela
 *               <mateusz.matela@gmail.com> - Adapt Zest to changes in layout -
 *               https://bugs.eclipse.org/bugs/show_bug.cgi?id=283179
 ******************************************************************************/
package org.eclipse.zest.core.viewers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.zest.core.viewers.internal.IStylingGraphModelFactory;
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.ConstraintAdapter;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutRelationship;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * Abstraction of graph viewers to implement functionality used by all of them.
 * Not intended to be implemented by clients. Use one of the provided children
 * instead.
 *
 * @author Del Myers
 * @since 1.14
 * @noextend This class is not intended to be subclassed by clients.
 */
@SuppressWarnings("removal")
public abstract class AbstractStructuredGraphViewer extends AbstractZoomableViewer {
	/**
	 * Contains top-level styles for the entire graph. Set in the constructor. *
	 */
	private final int graphStyle;

	/**
	 * Contains node-level styles for the graph. Set in setNodeStyle(). Defaults are
	 * used in the constructor.
	 */
	private int nodeStyle;

	/**
	 * Contains arc-level styles for the graph. Set in setConnectionStyle().
	 * Defaults are used in the constructor.
	 */
	private int connectionStyle;

	private Map<Object, GraphNode> nodesMap = new HashMap<>();
	private Map<Object, GraphConnection> connectionsMap = new HashMap<>();

	/**
	 * The constraint adapters
	 */
	private final List<ConstraintAdapter> constraintAdapters = new ArrayList<>();

	/**
	 * A simple graph comparator that orders graph elements based on their type
	 * (connection or node), and their unique object identification.
	 */
	private class SimpleGraphComparator implements Comparator<GraphItem> {
		Set<String> storedStrings;

		/**
		 *
		 */
		public SimpleGraphComparator() {
			this.storedStrings = new TreeSet<>();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GraphItem arg0, GraphItem arg1) {
			if (arg0 instanceof GraphNode && arg1 instanceof GraphConnection) {
				return 1;
			}
			if (arg0 instanceof GraphConnection && arg1 instanceof GraphNode) {
				return -1;
			}
			if (arg0.equals(arg1)) {
				return 0;
			}
			return getObjectString(arg0).compareTo(getObjectString(arg1));
		}

		private String getObjectString(Object o) {
			String s = o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
			while (storedStrings.contains(s)) {
				s = s + 'X';
			}
			return s;
		}
	}

	protected AbstractStructuredGraphViewer(int graphStyle) {
		this.graphStyle = graphStyle;
		this.connectionStyle = SWT.NONE;
		this.nodeStyle = SWT.NONE;

	}

	/**
	 * Sets the default style for nodes in this graph. Note: if an input is set on
	 * the viewer, a ZestException will be thrown.
	 *
	 * @param nodeStyle the style for the nodes.
	 * @see ZestStyles
	 */
	public void setNodeStyle(int nodeStyle) {
		if (getInput() != null) {
			throw new SWTError(SWT.ERROR_UNSPECIFIED);
		}
		this.nodeStyle = nodeStyle;
	}

	/**
	 * Sets the default style for connections in this graph. Note: if an input is
	 * set on the viewer, a ZestException will be thrown.
	 *
	 * @param connectionStyle the style for the connections.
	 * @see ZestStyles
	 */
	public void setConnectionStyle(int connectionStyle) {
		if (getInput() != null) {
			throw new SWTError(SWT.ERROR_UNSPECIFIED);
		}
		if (!ZestStyles.validateConnectionStyle(connectionStyle)) {
			throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.connectionStyle = connectionStyle;
	}

	/**
	 * Returns the style set for the graph
	 *
	 * @return The style set of the graph
	 */
	public int getGraphStyle() {
		return graphStyle;
	}

	/**
	 * Returns the style set for the nodes.
	 *
	 * @return the style set for the nodes.
	 */
	public int getNodeStyle() {
		return nodeStyle;
	}

	public Graph getGraphControl() {
		return (Graph) getControl();
	}

	/**
	 * @return the connection style.
	 */
	public int getConnectionStyle() {
		return connectionStyle;
	}

	/**
	 * Adds a new constraint adapter to the list of constraints
	 *
	 * @param constraintAdapter
	 */
	public void addConstraintAdapter(ConstraintAdapter constraintAdapter) {
		this.constraintAdapters.add(constraintAdapter);
	}

	/**
	 * Gets all the constraint adapters currently on the viewer
	 */
	public List<? extends ConstraintAdapter> getConstraintAdapters() {
		return this.constraintAdapters;
	}

	/**
	 * Sets the layout algorithm for this viewer. Subclasses may place restrictions
	 * on the algorithms that it accepts.
	 *
	 * @param algorithm the layout algorithm
	 * @param run       true if the layout algorithm should be run immediately. This
	 *                  is a hint.
	 */
	public abstract void setLayoutAlgorithm(LayoutAlgorithm algorithm, boolean run);

	/**
	 * Gets the current layout algorithm.
	 *
	 * @return the current layout algorithm.
	 */
	protected abstract LayoutAlgorithm getLayoutAlgorithm();

	/**
	 * Equivalent to setLayoutAlgorithm(algorithm, false).
	 *
	 * @param algorithm
	 */
	public void setLayoutAlgorithm(LayoutAlgorithm algorithm) {
		setLayoutAlgorithm(algorithm, false);
	}

	public Object[] getNodeElements() {
		return this.nodesMap.keySet().toArray();
	}

	public Object[] getConnectionElements() {
		return this.connectionsMap.keySet().toArray();
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public Map<Object, GraphNode> getNodesMap() {
		return this.nodesMap;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphNode addGraphModelContainer(Object element) {
		GraphNode node = this.getGraphModelNode(element);
		if (node == null) {
			node = new GraphContainer((Graph) getControl(), SWT.NONE);
			this.nodesMap.put(element, node);
			node.setData(element);
		}
		return node;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphNode addGraphModelNode(IContainer container, Object element) {
		GraphNode node = this.getGraphModelNode(element);
		if (node == null) {
			node = new GraphNode(container, SWT.NONE);
			this.nodesMap.put(element, node);
			node.setData(element);
		}
		return node;
	}

	/**
	 * Implement and return the new node object, enables to define custom graph
	 * nodes
	 *
	 * @param graphModel where the created nodes gets added to
	 * @param figure     of the created node object
	 * @return instance of a {@link GraphNode}
	 *
	 * @since 1.7
	 */
	@SuppressWarnings("static-method")
	protected GraphNode createNodeObject(final Graph graphModel, IFigure figure) {
		return new CGraphNode(graphModel, SWT.NONE, figure);
	}

	/**
	 * Implement and return the new connection object, enables to define custom
	 * graph connections
	 *
	 * @param graphModel  where the created nodes gets added to
	 * @param source      {@link GraphNode}
	 * @param destination {@link GraphNode}
	 * @return instance of a {@link GraphConnection}
	 *
	 * @since 1.7
	 */
	@SuppressWarnings("static-method")
	protected GraphConnection createConnectionObject(final Graph graphModel, final GraphNode source,
			final GraphNode destination) {
		return new GraphConnection(graphModel, SWT.NONE, source, destination);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphNode addGraphModelNode(Object element, IFigure figure) {
		GraphNode node = this.getGraphModelNode(element);
		if (node == null) {
			if (figure != null) {
				node = createNodeObject((Graph) getControl(), figure);
				this.nodesMap.put(element, node);
				node.setData(element);
			} else {
				node = new GraphNode((Graph) getControl(), SWT.NONE);
				this.nodesMap.put(element, node);
				node.setData(element);
			}
		}
		return node;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphConnection addGraphModelConnection(Object element, GraphNode source, GraphNode target) {
		GraphConnection connection = this.getGraphModelConnection(element);
		if (connection == null) {
			connection = createConnectionObject(getGraphControl(), source, target);
			this.connectionsMap.put(element, connection);
			connection.setData(element);
		}
		return connection;

	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphConnection getGraphModelConnection(Object obj) {
		return this.connectionsMap.get(obj);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public GraphNode getGraphModelNode(Object obj) {
		return this.nodesMap.get(obj);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void removeGraphModelConnection(Object obj) {
		GraphConnection connection = connectionsMap.get(obj);
		if (connection != null) {
			connectionsMap.remove(obj);
			if (!connection.isDisposed()) {
				connection.dispose();
			}
		}
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void removeGraphModelNode(Object obj) {
		GraphNode node = nodesMap.get(obj);
		if (node != null) {
			nodesMap.remove(obj);
			if (!node.isDisposed()) {
				node.dispose();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.
	 * Object)
	 */
	@Override
	protected void internalRefresh(Object element) {
		if (getInput() == null) {
			return;
		}
		if (element == getInput()) {
			getFactory().refreshGraph(getGraphControl());
		} else {
			getFactory().refresh(getGraphControl(), element);
		}
		// After all the items are loaded, we call update to ensure drawing.
		// This way the damaged area does not get too big if we start
		// adding and removing more nodes
		getGraphControl().getLightweightSystem().getUpdateManager().performUpdate();
	}

	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		if (item == getGraphControl()) {
			getFactory().update(getNodesArray(getGraphControl()));
			getFactory().update(getConnectionsArray(getGraphControl()));
		} else if (item instanceof GraphItem) {
			getFactory().update((GraphItem) item);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindInputItem(java.lang.
	 * Object)
	 */
	@Override
	protected Widget doFindInputItem(Object element) {

		if (element == getInput() && element instanceof Widget) {
			return (Widget) element;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindItem(java.lang.Object)
	 */
	@Override
	protected GraphItem doFindItem(Object element) {
		GraphItem node = nodesMap.get(element);
		GraphItem connection = connectionsMap.get(element);
		return (node != null) ? node : connection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	@Override
	protected List<Object> getSelectionFromWidget() {
		List<? extends GraphItem> internalSelection = getWidgetSelection();
		List<Object> externalSelection = new LinkedList<>();
		for (GraphItem element : internalSelection) {
			Object data = element.getData();
			if (data != null) {
				externalSelection.add(data);
			}
		}
		return externalSelection;
	}

	protected GraphItem[] findItems(List<Object> l) {
		if (l == null) {
			return new GraphItem[0];
		}

		return l.stream().map(this::findItem).toArray(GraphItem[]::new);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.
	 * util.List, boolean)
	 */
	@Override
	protected void setSelectionToWidget(@SuppressWarnings("rawtypes") List l, boolean reveal) {
		Graph control = (Graph) getControl();
		List<GraphItem> selection = new LinkedList<>();
		for (Object obj : l) {
			GraphNode node = nodesMap.get(obj);
			GraphConnection conn = connectionsMap.get(obj);
			if (node != null) {
				selection.add(node);
			}
			if (conn != null) {
				selection.add(conn);
			}
		}
		control.setSelection(selection.toArray(new GraphItem[selection.size()]));
	}

	/**
	 * Gets the internal model elements that are selected.
	 */
	protected List<? extends GraphItem> getWidgetSelection() {
		Graph control = (Graph) getControl();
		return control.getSelection();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected void inputChanged(Object input, Object oldInput) {
		IStylingGraphModelFactory factory = getFactory();
		factory.setConnectionStyle(getConnectionStyle());
		factory.setNodeStyle(getNodeStyle());

		// Save the old map so we can set the size and position of any nodes
		// that are the same
		Map<Object, GraphNode> oldNodesMap = nodesMap;
		Graph graph = (Graph) getControl();
		graph.setSelection(new GraphNode[0]);

		for (GraphNode node : nodesMap.values()) {
			if (!node.isDisposed()) {
				node.dispose();
			}
		}

		for (GraphConnection connection : connectionsMap.values()) {
			if (!connection.isDisposed()) {
				connection.dispose();
			}
		}

		nodesMap = new HashMap<>();
		connectionsMap = new HashMap<>();

		graph = factory.createGraphModel(graph);

		((Graph) getControl()).setNodeStyle(getNodeStyle());
		((Graph) getControl()).setConnectionStyle(getConnectionStyle());

		// check if any of the pre-existing nodes are still present
		// in this case we want them to keep the same location & size
		for (Object data : oldNodesMap.keySet()) {
			GraphNode newNode = nodesMap.get(data);
			if (newNode != null) {
				GraphNode oldNode = oldNodesMap.get(data);
				newNode.setLocation(oldNode.getLocation().x, oldNode.getLocation().y);
				if (oldNode.isSizeFixed()) {
					newNode.setSize(oldNode.getSize().width, oldNode.getSize().height);
				}
			}
		}

		applyLayout();
	}

	/**
	 * Returns the factory used to create the model. This must not be called before
	 * the content provider is set.
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 * @nooverride This method is not intended to be re-implemented or extended by
	 *             clients.
	 */
	protected abstract IStylingGraphModelFactory getFactory();

	protected void filterVisuals() {
		if (getGraphControl() == null) {
			return;
		}
		Object[] filtered = getFilteredChildren(getInput());
		SimpleGraphComparator comparator = new SimpleGraphComparator();
		TreeSet<GraphItem> filteredElements = new TreeSet<>(comparator);
		TreeSet<GraphItem> unfilteredElements = new TreeSet<>(comparator);
		List<? extends GraphConnection> connections = getGraphControl().getConnections();
		List<? extends GraphNode> nodes = getGraphControl().getNodes();
		if (filtered.length == 0) {
			// set everything to invisible.
			// @tag zest.bug.156528-Filters.check : should we only filter out
			// the nodes?
			for (GraphConnection c : connections) {
				c.setVisible(false);
			}
			for (GraphNode n : nodes) {
				n.setVisible(false);
			}
			return;
		}
		for (GraphConnection c : connections) {
			if (c.getExternalConnection() != null) {
				unfilteredElements.add(c);
			}
		}
		for (GraphNode n : nodes) {
			if (n.getData() != null) {
				unfilteredElements.add(n);
			}
		}
		for (Object element : filtered) {
			GraphItem modelElement = connectionsMap.get(element);
			if (modelElement == null) {
				modelElement = nodesMap.get(element);
			}
			if (modelElement != null) {
				filteredElements.add(modelElement);
			}
		}
		unfilteredElements.removeAll(filteredElements);
		// set all the elements that did not pass the filters to invisible, and
		// all the elements that passed to visible.
		while (!unfilteredElements.isEmpty()) {
			GraphItem i = unfilteredElements.first();
			i.setVisible(false);
			unfilteredElements.remove(i);
		}
		while (!filteredElements.isEmpty()) {
			GraphItem i = filteredElements.first();
			i.setVisible(true);
			filteredElements.remove(i);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.viewers.StructuredViewer#getRawChildren(java.lang.Object )
	 */
	@Override
	protected Object[] getRawChildren(Object parent) {
		if (parent == getInput()) {
			// get the children from the model.
			List<Object> children = new LinkedList<>();
			if (getGraphControl() != null) {
				for (GraphConnection c : getGraphControl().getConnections()) {
					if (c.getExternalConnection() != null) {
						children.add(c.getExternalConnection());
					}
				}
				for (GraphNode n : getGraphControl().getNodes()) {
					if (n.getData() != null) {
						children.add(n.getData());
					}
				}
				return children.toArray();
			}
		}
		return super.getRawChildren(parent);
	}

	/**
	 *
	 */
	@Override
	public void reveal(Object element) {
		Widget[] items = this.findItems(element);

		Point location = null;
		for (Widget item : items) {
			if (item instanceof GraphNode) {
				GraphNode graphModelNode = (GraphNode)item;
				graphModelNode.highlight();
				location = getTopLeftBoundary(graphModelNode.getLocation(), location);
			} else if (item instanceof GraphConnection) {
				GraphConnection graphModelConnection = (GraphConnection)item;
				graphModelConnection.highlight();
				location = getTopLeftBoundary(graphModelConnection.getSource().getLocation(), location);
				location = getTopLeftBoundary(graphModelConnection.getDestination().getLocation(), location);
			}
		}
		// Scrolling to new location
		if (location != null) {
			getGraphControl().scrollSmoothTo(location.x, location.y);
		}
	}

	/**
	 * Calculates the top left corner of the boundary of a single point or two
	 * points.
	 *
	 * @param point1 must not be null
	 * @param point2 may be null - in that case, point1 is returned
	 */
	private static Point getTopLeftBoundary(Point point1, Point point2) {
		if (point2 != null) {
			return new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y));
		}
		return point1;
	}

	public void unReveal(Object element) {
		Widget[] items = this.findItems(element);
		for (Widget item : items) {
			if (item instanceof GraphNode) {
				((GraphNode)item).unhighlight();
			} else if (item instanceof GraphConnection) {
				((GraphConnection)item).unhighlight();
			}
		}
	}

	/**
	 * Applies the viewers layouts.
	 *
	 */
	public abstract void applyLayout();

	/**
	 * Removes the given connection object from the layout algorithm and the model.
	 *
	 * @param connection
	 */
	public void removeRelationship(Object connection) {
		GraphConnection relationship = connectionsMap.get(connection);

		if (relationship != null) {
			// remove the relationship from the layout algorithm
			if (getLayoutAlgorithm() instanceof LayoutAlgorithm.Zest1) {
				((LayoutAlgorithm.Zest1)getLayoutAlgorithm()).removeRelationship(relationship.getLayoutRelationship());
			}
			// remove the relationship from the model
			relationship.dispose();
		}
	}

	/**
	 * Creates a new node and adds it to the graph. If it already exists nothing
	 * happens.
	 *
	 * @param element
	 */
	public void addNode(Object element) {
		if (nodesMap.get(element) == null) {
			// create the new node
			getFactory().createNode(getGraphControl(), element);

		}
	}

	/**
	 * Removes the given element from the layout algorithm and the model.
	 *
	 * @param element The node element to remove.
	 */
	public void removeNode(Object element) {
		GraphNode node = nodesMap.get(element);

		if (node != null) {
			// remove the node from the layout algorithm and all the connections
			if (getLayoutAlgorithm() instanceof LayoutAlgorithm.Zest1) {
				LayoutAlgorithm.Zest1 zest1 = (LayoutAlgorithm.Zest1)getLayoutAlgorithm();
				zest1.removeEntity(node.getLayoutEntity());
				List<LayoutRelationship> relationships = new ArrayList<>();
				for (GraphConnection connection : node.getSourceConnections()) {
					relationships.add(connection.getLayoutRelationship());
				}
				for (GraphConnection connection : node.getTargetConnections()) {
					relationships.add(connection.getLayoutRelationship());
				}
				zest1.removeRelationships(relationships);
			}
			// remove the node and it's connections from the model
			node.dispose();
		}
	}

	/**
	 * Creates a new relationship between the source node and the destination node.
	 * If either node doesn't exist then it will be created.
	 *
	 * @param connection The connection data object.
	 * @param srcNode    The source node data object.
	 * @param destNode   The destination node data object.
	 */
	public void addRelationship(Object connection, Object srcNode, Object destNode) {
		// create the new relationship
		IStylingGraphModelFactory modelFactory = getFactory();
		modelFactory.createConnection(getGraphControl(), connection, srcNode, destNode);

	}

	/**
	 * Adds a new relationship given the connection. It will use the content
	 * provider to determine the source and destination nodes.
	 *
	 * @param connection The connection data object.
	 */
	public void addRelationship(Object connection) {
		IStylingGraphModelFactory modelFactory = getFactory();
		if (connectionsMap.get(connection) == null) {
			if (!(modelFactory.getContentProvider() instanceof IGraphContentProvider)) {
				throw new UnsupportedOperationException();
			}
			IGraphContentProvider content = ((IGraphContentProvider) modelFactory.getContentProvider());
			Object source = content.getSource(connection);
			Object dest = content.getDestination(connection);
			// create the new relationship
			modelFactory.createConnection(getGraphControl(), connection, source, dest);
		}
	}

	/**
	 * Converts the list of GraphModelConnection objects into an array and returns
	 * it.
	 *
	 * @return GraphModelConnection[]
	 */
	@SuppressWarnings("static-method")
	protected GraphConnection[] getConnectionsArray(Graph graph) {
		GraphConnection[] connsArray = new GraphConnection[graph.getConnections().size()];
		return graph.getConnections().toArray(connsArray);
	}

	/**
	 * Converts the list of GraphModelNode objects into an array an returns it.
	 *
	 * @return GraphModelNode[]
	 */
	@SuppressWarnings("static-method")
	protected GraphNode[] getNodesArray(Graph graph) {
		GraphNode[] nodesArray = new GraphNode[graph.getNodes().size()];
		return graph.getNodes().toArray(nodesArray);
	}

}
