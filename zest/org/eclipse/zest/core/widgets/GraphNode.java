/*******************************************************************************
 * Copyright 2005, 2025, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *               Sebastian Hollersbacher
 *               Mateusz Matela
 ******************************************************************************/
package org.eclipse.zest.core.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Simple node class which has the following properties: color, size, location,
 * and a label. It also has a list of connections and anchors.
 *
 * @author Chris Callendar
 *
 * @author Del Myers
 *
 * @author Ian Bull
 *
 * @author Sebastian Hollersbacher
 */
public class GraphNode extends GraphItem {
	public static final int HIGHLIGHT_NONE = 0;
	public static final int HIGHLIGHT_ON = 1;
	// @tag ADJACENT : Removed highlight adjacent
	// public static final int HIGHLIGHT_ADJACENT = 2;

	private int nodeStyle;

	private List<GraphConnection> sourceConnections;
	private List<GraphConnection> targetConnections;

	private Color foreColor;
	private Color backColor;
	private Color highlightColor;
	// @tag ADJACENT : Removed highlight adjacent
	// private Color highlightAdjacentColor;
	private Color borderColor;
	private Color borderHighlightColor;
	private int borderWidth;
	private PrecisionPoint currentLocation;
	protected Dimension size;
	private Font font;
	private boolean cacheLabel;
	private boolean visible = true;
	/**
	 * @deprecated Not used in Zest 2.x. This class will be removed in a future
	 *             release.
	 */
	private LayoutEntity layoutEntity;

	protected Graph graph;
	protected IContainer parent;

	/** The internal node. */
	protected Object internalNode;
	private boolean selected;
	protected int highlighted = HIGHLIGHT_NONE;
	private IFigure tooltip;
	protected IFigure nodeFigure;

	/**
	 * @since 1.8
	 */
	private IFigure modelFigure;

	/**
	 * @since 1.8
	 */
	private HideNodeHelper hideNodeHelper;

	private boolean isDisposed = false;
	private boolean hasCustomTooltip;

	public GraphNode(IContainer graphModel, int style) {
		this(graphModel, style, (Object) null);
	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #GraphNode(IContainer, int, IFigure)}
	 *             instead.
	 */
	public GraphNode(IContainer graphModel, int style, Object data) {
		this(graphModel, style, "" /* text */, null /* image */, data); //$NON-NLS-1$
	}

	/**
	 * @since 1.14
	 */
	public GraphNode(IContainer graphModel, int style, IFigure data) {
		this(graphModel, style, "" /* text */, null /* image */, data); //$NON-NLS-1$
	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #GraphNode(IContainer, int)} and
	 *             {@link #setText(String)}
	 */
	public GraphNode(IContainer graphModel, int style, String text) {
		this(graphModel, style, text, null);
	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #GraphNode(IContainer, int, IFigure)}
	 *             and {@link #setText(String)}
	 */
	public GraphNode(IContainer graphModel, int style, String text, Object data) {
		this(graphModel, style, text, null /* image */, data);
	}

	public GraphNode(IContainer graphModel, int style, String text, Image image) {
		this(graphModel, style, text, image, null);
	}

	public GraphNode(IContainer graphModel, int style, String text, Image image, Object data) {
		super(graphModel.getGraph(), style, data);
		initModel(graphModel, text, image);
		if (modelFigure == null) {
			initFigure();
		}
		if (this.parent instanceof IContainer2) {
			((IContainer2)this.parent).addNode(this);
		}
		this.parent.getGraph().registerItem(this);
	}

	protected void initFigure() {
		modelFigure = createFigureForModel();
		if (graph.getHideNodesEnabled() && !checkStyle(ZestStyles.NODES_FISHEYE)) {
			nodeFigure = new ContainerFigure();
			nodeFigure.add(modelFigure);
			hideNodeHelper = new HideNodeHelper(this);
			nodeFigure.setToolTip(modelFigure.getToolTip());
		} else {
			nodeFigure = modelFigure;
		}
	}

	static int count = 0;

	protected void initModel(IContainer parent, String text, Image image) {
		this.nodeStyle |= parent.getGraph().getNodeStyle();
		this.parent = parent;
		this.sourceConnections = new ArrayList<>();
		this.targetConnections = new ArrayList<>();
		this.foreColor = parent.getGraph().DARK_BLUE;
		this.backColor = parent.getGraph().LIGHT_BLUE;
		this.highlightColor = parent.getGraph().HIGHLIGHT_COLOR;
		// @tag ADJACENT : Removed highlight adjacent
		// this.highlightAdjacentColor = ColorConstants.orange;
		this.nodeStyle = SWT.NONE;
		this.borderColor = ColorConstants.lightGray;
		this.borderHighlightColor = ColorConstants.blue;
		this.borderWidth = 1;
		this.currentLocation = new PrecisionPoint(0, 0);
		this.size = new Dimension(-1, -1);
		this.font = Display.getDefault().getSystemFont();
		this.graph = parent.getGraph();
		this.cacheLabel = false;
		this.setText(text);
		this.layoutEntity = new LayoutGraphNode();
		if (image != null) {
			this.setImage(image);
		}

		if (font == null) {
			font = Display.getDefault().getSystemFont();
		}

	}

	/**
	 * A simple toString that we can use for debugging
	 */
	@Override
	public String toString() {
		return "GraphModelNode: " + getText(); //$NON-NLS-1$
	}

	/**
	 * @deprecated Not used in Zest 2.x. This class will be removed in a future
	 *             release.
	 * @nooverride This method is not intended to be re-implemented or extended by
	 *             clients.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public LayoutEntity getLayoutEntity() {
		return layoutEntity;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.widgets.GraphItem#dispose()
	 */
	@Override
	public void dispose() {
		if (isFisheyeEnabled) {
			this.fishEye(false, false);
		}
		super.dispose();
		this.isDisposed = true;
		while (!getSourceConnections().isEmpty()) {
			GraphConnection connection = getSourceConnections().get(0);
			if (!connection.isDisposed()) {
				connection.dispose();
			} else {
				removeSourceConnection(connection);
			}
		}
		while (!getTargetConnections().isEmpty()) {
			GraphConnection connection = getTargetConnections().get(0);
			if (!connection.isDisposed()) {
				connection.dispose();
			} else {
				removeTargetConnection(connection);
			}
		}
		graph.removeNode(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.widgets.Widget#isDisposed()
	 */
	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	/**
	 * Determines if this node has a fixed size or if it is packed to the size of
	 * its contents. To set a node to pack, set its size (-1, -1)
	 */
	public boolean isSizeFixed() {
		return !(this.size.width < 0 && this.size.height < 0);
	}

	/**
	 * Returns a new list of the source connections (GraphModelConnection objects).
	 *
	 * @return List a new list of GraphModelConnect objects
	 */
	public List<? extends GraphConnection> getSourceConnections() {
		return new ArrayList<>(sourceConnections);
	}

	/**
	 * Returns a new list of the target connections (GraphModelConnection objects).
	 *
	 * @return List a new list of GraphModelConnect objects
	 */
	public List<? extends GraphConnection> getTargetConnections() {
		return new ArrayList<>(targetConnections);
	}

	/**
	 * Returns the bounds of this node. It is just the combination of the location
	 * and the size.
	 *
	 * @return Rectangle
	 */
	Rectangle getBounds() {
		return new Rectangle(getLocation(), getSize());
	}

	/**
	 * Returns a copy of the node's location.
	 *
	 * @return Point
	 */
	public Point getLocation() {
		return currentLocation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.mylar.zest.core.internal.graphmodel.IGraphModelNode#isSelected ()
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the current location for this node.
	 */
	public void setLocation(double x, double y) {
		if (currentLocation.preciseX() != x || currentLocation.preciseY() != y) {
			currentLocation.setPreciseX(x);
			currentLocation.setPreciseY(y);
			refreshLocation();
			if (getGraphModel().isDynamicLayoutEnabled() && parent instanceof IContainer2) {
				((InternalLayoutContext)((IContainer2)parent).getLayoutContext()).fireNodeMovedEvent(this.getLayout());
			}
		}
	}

	/**
	 * Returns a copy of the node's size.
	 *
	 * @return Dimension
	 */
	public Dimension getSize() {
		if (size.height < 0 && size.width < 0 && modelFigure != null) {
			// return size of node calculated from the model
			Dimension modelSize = modelFigure.getSize();
			if (hideNodeHelper != null) {
				if (modelSize.equals(-1, -1)) {
					modelSize = modelFigure.getPreferredSize();
				}
				modelSize.width = modelSize.width + 2 * HideNodeHelper.MARGIN;
				modelSize.height = modelSize.height + 2 * HideNodeHelper.MARGIN;
			}
			return modelSize;
		}
		return size.getCopy();
	}

	/**
	 * Get the foreground colour for this node
	 */
	public Color getForegroundColor() {
		return foreColor;
	}

	/**
	 * Set the foreground colour for this node
	 */
	public void setForegroundColor(Color c) {
		this.foreColor = c;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Get the background colour for this node. This is the color the node will be
	 * if it is not currently highlighted. This color is meaningless if a custom
	 * figure has been set.
	 */
	public Color getBackgroundColor() {
		return backColor;
	}

	/**
	 * Permanently sets the background color (unhighlighted). This color has no
	 * effect if a custom figure has been set.
	 *
	 * @param c
	 */
	public void setBackgroundColor(Color c) {
		backColor = c;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Sets the tooltip on this node. This tooltip will display if the mouse hovers
	 * over the node. Setting the tooltip has no effect if a custom figure has been
	 * set.
	 */
	public void setTooltip(IFigure tooltip) {
		hasCustomTooltip = true;
		this.tooltip = tooltip;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Gets the current tooltip for this node. The tooltip returned is meaningless
	 * if a custom figure has been set.
	 */
	public IFigure getTooltip() {
		return this.tooltip;
	}

	/**
	 * Sets the border color.
	 *
	 * @param c the border color.
	 */
	public void setBorderColor(Color c) {
		borderColor = c;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Sets the highlighted border color.
	 *
	 * @param c the highlighted border color.
	 */
	public void setBorderHighlightColor(Color c) {
		this.borderHighlightColor = c;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Get the highlight colour for this node
	 */
	public Color getHighlightColor() {
		return highlightColor;
	}

	/**
	 * Set the highlight colour for this node
	 */
	public void setHighlightColor(Color c) {
		this.highlightColor = c;
	}

	/**
	 * Get the highlight adjacent colour for this node. This is the colour that
	 * adjacent nodes will get
	 */
	// @tag ADJACENT : Removed highlight adjacent
	/*
	 * public Color getHighlightAdjacentColor() { return highlightAdjacentColor; }
	 */

	/**
	 * Set the highlight adjacent colour for this node. This is the colour that
	 * adjacent node will get.
	 */
	// @tag ADJACENT : Removed highlight adjacent
	/*
	 * public void setHighlightAdjacentColor(Color c) { this.highlightAdjacentColor
	 * = c; }
	 */

	/**
	 * Highlights the node changing the background color and border color. The
	 * source and destination connections are also highlighted, and the adjacent
	 * nodes are highlighted too in a different color.
	 */
	@Override
	public void highlight() {
		if (highlighted == HIGHLIGHT_ON) {
			return;
		}
		// @tag ADJACENT : Removed highlight adjacent
		/*
		 * if (ZestStyles.checkStyle(getNodeStyle(),
		 * ZestStyles.NODES_HIGHLIGHT_ADJACENT)) { for (Iterator iter =
		 * sourceConnections.iterator(); iter.hasNext();) { GraphConnection conn =
		 * (GraphConnection) iter.next(); conn.highlight();
		 * conn.getDestination().highlightAdjacent(); } for (Iterator iter =
		 * targetConnections.iterator(); iter.hasNext();) { GraphConnection conn =
		 * (GraphConnection) iter.next(); conn.highlight();
		 * conn.getSource().highlightAdjacent(); } }
		 */
		if (nodeFigure.getParent() instanceof ZestRootLayer) {
			((ZestRootLayer)nodeFigure.getParent()).highlightNode(nodeFigure);
		}
		highlighted = HIGHLIGHT_ON;
		updateFigureForModel(modelFigure);
	}

	/**
	 * Restores the nodes original background color and border width.
	 */
	@Override
	public void unhighlight() {

		// @tag ADJACENT : Removed highlight adjacent
		// boolean highlightedAdjacently = (highlighted == HIGHLIGHT_ADJACENT);
		if (highlighted == HIGHLIGHT_NONE) {
			return;
		}
		// @tag ADJACENT : Removed highlight adjacent
		/*
		 * if (!highlightedAdjacently) { // IF we are highlighted as an adjacent node,
		 * we don't need to deal // with our connections. if
		 * (ZestStyles.checkStyle(getNodeStyle(), ZestStyles.NODES_HIGHLIGHT_ADJACENT))
		 * { // unhighlight the adjacent edges for (Iterator iter =
		 * sourceConnections.iterator(); iter.hasNext();) { GraphConnection conn =
		 * (GraphConnection) iter.next(); conn.unhighlight(); if (conn.getDestination()
		 * != this) { conn.getDestination().unhighlight(); } } for (Iterator iter =
		 * targetConnections.iterator(); iter.hasNext();) { GraphConnection conn =
		 * (GraphConnection) iter.next(); conn.unhighlight(); if (conn.getSource() !=
		 * this) { conn.getSource().unhighlight(); } } } }
		 */
		if (nodeFigure.getParent() instanceof ZestRootLayer) {
			((ZestRootLayer)nodeFigure.getParent()).unHighlightNode(nodeFigure);
		}
		highlighted = HIGHLIGHT_NONE;
		updateFigureForModel(modelFigure);

	}

	protected void refreshLocation() {
		Point loc = getLocation();
		Dimension nodeSize = getSize();
		Rectangle bounds = new Rectangle(loc, nodeSize);

		if (nodeFigure == null || nodeFigure.getParent() == null) {
			return; // node figure has not been created yet
		}

		if (hideNodeHelper != null) {
			hideNodeHelper.updateNodeBounds(bounds);
		}
		nodeFigure.getParent().setConstraint(nodeFigure, bounds);

		if (isFisheyeEnabled) {
			Rectangle fishEyeBounds = calculateFishEyeBounds();
			if (fishEyeBounds != null) {
				fishEyeFigure.getParent().translateToRelative(fishEyeBounds);
				fishEyeFigure.getParent().translateFromParent(fishEyeBounds);
				fishEyeFigure.getParent().setConstraint(fishEyeFigure, fishEyeBounds);
			}
		}
	}

	/**
	 * Highlights this node using the adjacent highlight color. This only does
	 * something if highlighAdjacentNodes is set to true and if the node isn't
	 * already highlighted.
	 *
	 * @see #setHighlightAdjacentNodes(boolean)
	 */
	// @tag ADJACENT : removed highlight adjacent
	/*
	 * public void highlightAdjacent() { if (highlighted > 0) { return; }
	 * highlighted = HIGHLIGHT_ADJACENT; updateFigureForModel(nodeFigure); if
	 * (parent.getItemType() == GraphItem.CONTAINER) { ((GraphContainer)
	 * parent).highlightNode(this); } else { ((Graph) parent).highlightNode(this); }
	 * }
	 */

	/**
	 * Returns if the nodes adjacent to this node will be highlighted when this node
	 * is selected.
	 *
	 * @return GraphModelNode
	 */
	// @tag ADJACENT : Removed highlight adjacent
	/*
	 * public boolean isHighlightAdjacentNodes() { return
	 * ZestStyles.checkStyle(nodeStyle, ZestStyles.NODES_HIGHLIGHT_ADJACENT); }
	 */

	public Color getBorderColor() {
		return borderColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int width) {
		this.borderWidth = width;
		updateFigureForModel(modelFigure);
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		updateFigureForModel(modelFigure);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.widgets.Item#setText(java.lang.String)
	 */
	@Override
	public void setText(String string) {
		if (string == null) {
			string = ""; //$NON-NLS-1$
		}
		super.setText(string);

		if (nodeFigure != null) {
			updateFigureForModel(modelFigure);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.widgets.Item#setImage(org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(Image image) {
		super.setImage(image);
		if (nodeFigure != null) {
			updateFigureForModel(modelFigure);
		}
	}

	/**
	 * Gets the graphModel that this node is contained in
	 *
	 * @return The graph model that this node is contained in
	 */
	@Override
	public Graph getGraphModel() {
		return this.graph;
	}

	/**
	 * @return the nodeStyle
	 */
	public int getNodeStyle() {
		return nodeStyle;
	}

	/**
	 * @param nodeStyle the nodeStyle to set
	 */
	public void setNodeStyle(int nodeStyle) {
		this.nodeStyle = nodeStyle;
		this.cacheLabel = ((this.nodeStyle & ZestStyles.NODES_CACHE_LABEL) > 0) ? true : false;
		updateFigureForModel(modelFigure);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.internal.graphmodel.IGraphModelNode#setSize
	 * (double, double)
	 */
	public void setSize(double width, double height) {
		if ((width != size.width) || (height != size.height)) {
			size.width = (int) width;
			size.height = (int) height;
			refreshLocation();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.internal.graphmodel.IGraphModelNode#
	 * getBorderHighlightColor()
	 */
	public Color getBorderHighlightColor() {
		return borderHighlightColor;
	}

	public boolean cacheLabel() {
		return this.cacheLabel;
	}

	public void setCacheLabel(boolean cacheLabel) {
		this.cacheLabel = cacheLabel;
	}

	/**
	 * Returns the figure of the whole node.
	 *
	 * @return nodeFigure
	 */
	public IFigure getNodeFigure() {
		return this.nodeFigure;
	}

	@Override
	public void setVisible(boolean visible) {
		// graph.addRemoveFigure(this, visible);
		this.visible = visible;
		this.getNodeFigure().setVisible(visible);
		for (GraphConnection connection : getSourceConnections()) {
			connection.setVisible(visible);
		}

		for (GraphConnection connection : getTargetConnections()) {
			connection.setVisible(visible);
		}
	}

	/**
	 * @since 1.8
	 */
	public void setModelFigure(IFigure figure) {
		this.modelFigure = figure;
	}

	/**
	 * @since 1.8
	 */
	public IFigure getModelFigure() {
		return this.modelFigure;
	}

	/**
	 * @since 1.8
	 */
	public void setHideNodeHelper(HideNodeHelper hideNodeHelper) {
		this.hideNodeHelper = hideNodeHelper;
	}

	/**
	 * @since 1.8
	 */
	public HideNodeHelper getHideNodeHelper() {
		return this.hideNodeHelper;
	}

	@Override
	public int getStyle() {
		return super.getStyle() | this.getNodeStyle();
	}

	/**
	 * @since 1.8
	 */
	protected Rectangle getHideContainerBounds() {
		return nodeFigure.getBounds();
	}

	/***************************************************************************
	 * PRIVATE MEMBERS
	 **************************************************************************/

	private IFigure fishEyeFigure = null;
	private Font fishEyeFont = null;
	private boolean isFisheyeEnabled;

	protected IFigure fishEye(boolean enable, boolean animate) {
		if (isDisposed) {
			// If a fisheyed figure is still left on the canvas, we could get
			// called once more after the dispose is called. Since we cleaned
			// up everything on dispose, we can just return null here.
			return null;
		}
		if (!checkStyle(ZestStyles.NODES_FISHEYE)) {
			return null;
		}
		if (enable) {
			// Create the fish eye label
			fishEyeFigure = createFishEyeFigure();

			// Get the current Bounds
			Rectangle rectangle = calculateFishEyeBounds();

			FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
			fontData.setHeight(12);
			fishEyeFont = new Font(Display.getCurrent(), fontData);
			fishEyeFigure.setFont(fishEyeFont);

			if (rectangle == null) {
				return null;
			}

			// Add the fisheye
			this.getGraphModel().fishEye(nodeFigure, fishEyeFigure, rectangle, true);
			if (fishEyeFigure != null) {
				isFisheyeEnabled = true;
			}
			return fishEyeFigure;

		}
		// Remove the fisheye and dispose the font
		this.getGraphModel().removeFishEye(fishEyeFigure, nodeFigure, animate);
		if (fishEyeFont != null) {
			this.fishEyeFont.dispose();
			this.fishEyeFont = null;
		}
		isFisheyeEnabled = false;
		this.getGraphModel().removeFishEye(fishEyeFigure, nodeFigure, animate);
		return null;
	}

	IContainer getParent() {
		return parent;
	}

	/**
	 * returns true if node is highlighted, false otherwise
	 *
	 * @return state of highlight
	 * @since 1.9
	 */
	@Override
	public boolean isHighlighted() {
		return highlighted > 0;
	}

	@SuppressWarnings("removal")
	void invokeLayoutListeners(LayoutConstraint constraint) {
		graph.invokeConstraintAdapters(this, constraint);
	}

	protected void updateFigureForModel(IFigure currentFigure) {
		if (currentFigure == null) {
			return;
		}

		IFigure figure = currentFigure;
		IFigure toolTip;

		if (figure instanceof ILabeledFigure) {
			ILabeledFigure labeledFigure = (ILabeledFigure)figure;
			// update label text/icon for figures implementing ILabeledFigure
			if (!checkStyle(ZestStyles.NODES_HIDE_TEXT) && !labeledFigure.getText().equals(this.getText())) {
				labeledFigure.setText(this.getText());
			}
			if (labeledFigure.getIcon() != getImage()) {
				labeledFigure.setIcon(getImage());
			}
		}

		if (figure instanceof IStyleableFigure) {
			IStyleableFigure styleableFigure = (IStyleableFigure)figure;
			// update styles (colors, border) for figures implementing
			// IStyleableFigure
			if (highlighted == HIGHLIGHT_ON) {
				styleableFigure.setForegroundColor(getForegroundColor());
				styleableFigure.setBackgroundColor(getHighlightColor());
				styleableFigure.setBorderColor(getBorderHighlightColor());
			} else {
				styleableFigure.setForegroundColor(getForegroundColor());
				styleableFigure.setBackgroundColor(getBackgroundColor());
				styleableFigure.setBorderColor(getBorderColor());
			}

			styleableFigure.setBorderWidth(getBorderWidth());

			if (figure.getFont() != getFont()) {
				figure.setFont(getFont());
			}
		}

		if (this.getTooltip() == null && hasCustomTooltip == false) {
			// if we have a custom tooltip, don't try and create our own.
			toolTip = new Label();
			((Label) toolTip).setText(getText());
		} else {
			toolTip = this.getTooltip();
		}
		figure.setToolTip(toolTip);

		refreshLocation();

		if (isFisheyeEnabled) {
			IFigure newFisheyeFigure = createFishEyeFigure();
			if (graph.replaceFishFigure(this.fishEyeFigure, newFisheyeFigure)) {
				this.fishEyeFigure = newFisheyeFigure;
			}
		}
	}

	protected IFigure createFigureForModel() {
		GraphNode node = this;
		boolean cacheLabel = (this).cacheLabel();
		GraphLabel label = new GraphLabel(node.getText(), node.getImage(), cacheLabel);
		label.setFont(this.font);
		if (checkStyle(ZestStyles.NODES_HIDE_TEXT)) {
			label.setText(""); //$NON-NLS-1$
		}
		updateFigureForModel(label);
		label.addFigureListener(new FigureListener() {
			private Dimension previousSize = label.getBounds().getSize();

			@Override
			public void figureMoved(IFigure source) {
				if (Animation.isAnimating() || getLayout().isMinimized()) {
					return;
				}
				Rectangle newBounds = nodeFigure.getBounds();
				if (!newBounds.getSize().equals(previousSize)) {
					previousSize = newBounds.getSize();
					if (size.width >= 0 && size.height >= 0) {
						size = newBounds.getSize();
					}
					currentLocation = new PrecisionPoint(nodeFigure.getBounds().getTopLeft());
					if (parent instanceof IContainer2) {
						((InternalLayoutContext)((IContainer2)parent).getLayoutContext()).fireNodeResizedEvent(getLayout());
					}
				} else if (currentLocation.x != newBounds.x || currentLocation.y != newBounds.y) {
					currentLocation = new PrecisionPoint(nodeFigure.getBounds().getTopLeft());
					if (parent instanceof IContainer2) {
						((InternalLayoutContext)((IContainer2)parent).getLayoutContext()).fireNodeMovedEvent(getLayout());
					}
				}
			}
		});
		return label;
	}

	private IFigure createFishEyeFigure() {
		GraphNode node = this;
		boolean cacheLabel = this.cacheLabel();
		GraphLabel label = new GraphLabel(node.getText(), node.getImage(), cacheLabel);

		if (!checkStyle(ZestStyles.NODES_HIDE_TEXT)) {
			label.setText(this.getText());
		}
		label.setIcon(getImage());

		// @tag TODO: Add border and foreground colours to highlight
		// (this.borderColor)
		if (highlighted == HIGHLIGHT_ON) {
			label.setForegroundColor(getForegroundColor());
			label.setBackgroundColor(getHighlightColor());
			label.setBorderColor(getBorderHighlightColor());
		} else {
			label.setForegroundColor(getForegroundColor());
			label.setBackgroundColor(getBackgroundColor());
			label.setBorderColor(getBorderColor());
		}

		label.setBorderWidth(getBorderWidth());
		label.setFont(getFont());
		return label;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	private Rectangle calculateFishEyeBounds() {
		// Get the current Bounds
		Rectangle rectangle = nodeFigure.getBounds().getCopy();

		// Calculate how much we have to expand the current bounds to get to the
		// new bounds
		Dimension newSize = fishEyeFigure.getPreferredSize();
		Rectangle currentSize = rectangle.getCopy();
		nodeFigure.translateToAbsolute(currentSize);
		int expandedH = Math.max((newSize.height - currentSize.height) / 2 + 1, 0);
		int expandedW = Math.max((newSize.width - currentSize.width) / 2 + 1, 0);
		Dimension expandAmount = new Dimension(expandedW, expandedH);
		nodeFigure.translateToAbsolute(rectangle);
		rectangle.expand(new Insets(expandAmount.height, expandAmount.width, expandAmount.height, expandAmount.width));
		if (expandedH <= 0 && expandedW <= 0) {
			return null;
		}
		return rectangle;
	}

	void addSourceConnection(GraphConnection connection) {
		this.sourceConnections.add(connection);
		if (hideNodeHelper != null) {
			hideNodeHelper.addHideNodeListener(connection.getDestination().hideNodeHelper.getHideNodesListener());
		}
	}

	void addTargetConnection(GraphConnection connection) {
		this.targetConnections.add(connection);
		if (hideNodeHelper != null) {
			hideNodeHelper.addHideNodeListener(connection.getSource().hideNodeHelper.getHideNodesListener());
		}
	}

	void removeSourceConnection(GraphConnection connection) {
		this.sourceConnections.remove(connection);
		if (hideNodeHelper != null) {
			hideNodeHelper.removeHideNodeListener(connection.getDestination().hideNodeHelper.getHideNodesListener());
		}
	}

	void removeTargetConnection(GraphConnection connection) {
		this.targetConnections.remove(connection);
		if (hideNodeHelper != null) {
			hideNodeHelper.removeHideNodeListener(connection.getSource().hideNodeHelper.getHideNodesListener());
		}
	}

	/**
	 * Sets the node as selected.
	 */
	void setSelected(boolean selected) {
		if (selected == isSelected()) {
			return;
		}
		if (selected) {
			highlight();
		} else {
			unhighlight();
		}
		this.selected = selected;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.widgets.IGraphItem#getItemType()
	 */
	@Override
	public int getItemType() {
		return NODE;
	}

	@SuppressWarnings("removal")
	class LayoutGraphNode implements LayoutEntity {
		Object layoutInformation = null;

		@Override
		public double getHeightInLayout() {
			return getSize().height;
		}

		@Override
		public Object getLayoutInformation() {
			return layoutInformation;
		}

		@Override
		public String toString() {
			return getText();
		}

		@Override
		public double getWidthInLayout() {
			return getSize().width;
		}

		@Override
		public double getXInLayout() {
			return getLocation().x;
		}

		@Override
		public double getYInLayout() {
			return getLocation().y;
		}

		@Override
		public void populateLayoutConstraint(LayoutConstraint constraint) {
			invokeLayoutListeners(constraint);
		}

		@Override
		public void setLayoutInformation(Object internalEntity) {
			this.layoutInformation = internalEntity;

		}

		@Override
		public void setLocationInLayout(double x, double y) {
			setLocation(x, y);
		}

		@Override
		public void setSizeInLayout(double width, double height) {
			setSize(width, height);
		}

		/**
		 * Compares two nodes.
		 */
		@Override
		public int compareTo(Object otherNode) {
			int rv = 0;
			if (otherNode instanceof GraphNode) {
				if (getText() != null) {
					rv = getText().compareTo(((GraphNode)otherNode).getText());
				}
			}
			return rv;
		}

		@Override
		public Object getGraphData() {
			return GraphNode.this;
		}

		@Override
		public void setGraphData(Object o) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Returns the figure of the model in the node, initialises it, if it doesn't
	 * exist yet.
	 *
	 * @return modelFigure.
	 */
	@Override
	IFigure getFigure() {
		if (this.modelFigure == null) {
			initFigure();
		}
		return this.modelFigure;
	}

	void paint() {

	}

	private InternalNodeLayout layout;

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public InternalNodeLayout getLayout() {
		if (layout == null) {
			layout = new InternalNodeLayout(this);
		}
		return layout;
	}

	void applyLayoutChanges() {
		if (layout != null) {
			layout.applyLayout();
		}
	}
}
