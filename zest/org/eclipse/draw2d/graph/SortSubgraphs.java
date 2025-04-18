/*******************************************************************************
 * Copyright (c) 2003, 2023 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs a topological sort from left to right of the subgraphs in a compound
 * directed graph. This ensures that subgraphs do not intertwine.
 *
 * @author Randy Hudson
 * @since 2.1.2
 */
class SortSubgraphs extends GraphVisitor {

	CompoundDirectedGraph g;

	NestingTree nestingTrees[];

	Set<NodePair> orderingGraphEdges = new HashSet<>();
	Set<Node> orderingGraphNodes = new HashSet<>();
	NodePair pair = new NodePair();

	private void breakSubgraphCycles() {
		// The stack of nodes which have no unmarked incoming edges
		List<Node> noLefts = new ArrayList<>();

		int index = 1;
		// Identify all initial nodes for removal
		for (Object orderingGraphNode : orderingGraphNodes) {
			Node node = (Node) orderingGraphNode;
			if (node.x == 0) {
				sortedInsert(noLefts, node);
			}
		}

		Node cycleRoot;
		do {
			// Remove all leftmost nodes, updating the nodes to their right
			while (!noLefts.isEmpty()) {
				Node node = noLefts.remove(noLefts.size() - 1);
				node.sortValue = index;
				index++;
				orderingGraphNodes.remove(node);
				// System.out.println("removed:" + node);
				NodeList rightOf = rightOf(node);
				if (rightOf == null) {
					continue;
				}
				for (Node right : rightOf) {
					right.x--;
					if (right.x == 0) {
						sortedInsert(noLefts, right);
					}
				}
			}
			cycleRoot = null;
			double min = Double.MAX_VALUE;
			for (Object orderingGraphNode : orderingGraphNodes) {
				Node node = (Node) orderingGraphNode;
				if (node.sortValue < min) {
					cycleRoot = node;
					min = node.sortValue;
				}
			}
			if (cycleRoot != null) {
				// break the cycle;
				sortedInsert(noLefts, cycleRoot);
				// System.out.println("breaking cycle with:" + cycleRoot);
				// Display.getCurrent().beep();
				cycleRoot.x = -1; // prevent x from ever reaching 0
			} // else if (OGmembers.size() > 0)
				// System.out.println("FAILED TO FIND CYCLE ROOT"); //$NON-NLS-1$
		} while (cycleRoot != null);
	}

	private void buildSubgraphOrderingGraph() {
		RankList ranks = g.ranks;
		nestingTrees = new NestingTree[ranks.size()];
		for (int r = 0; r < ranks.size(); r++) {
			NestingTree entry = NestingTree.buildNestingTreeForRank(ranks.getRank(r));
			nestingTrees[r] = entry;
			entry.calculateSortValues();
			entry.recursiveSort(false);
		}

		for (NestingTree entry : nestingTrees) {
			buildSubgraphOrderingGraph(entry);
		}
	}

	private void buildSubgraphOrderingGraph(NestingTree entry) {
		NodePair pair = new NodePair();
		if (entry.isLeaf) {
			return;
		}
		for (Object right : entry.contents) {
			if (right instanceof Node) {
				pair.n2 = (Node)right;
			} else {
				pair.n2 = ((NestingTree) right).subgraph;
				buildSubgraphOrderingGraph((NestingTree) right);
			}
			if (pair.n1 != null && !orderingGraphEdges.contains(pair)) {
				orderingGraphEdges.add(pair);
				leftToRight(pair.n1, pair.n2);
				orderingGraphNodes.add(pair.n1);
				orderingGraphNodes.add(pair.n2);
				pair.n2.x++; // Using x field to count predecessors.
				pair = new NodePair(pair.n2, null);
			} else {
				pair.n1 = pair.n2;
			}
		}
	}

	/**
	 * Calculates the average position P for each node and subgraph. The average
	 * position is stored in the sortValue for each node or subgraph.
	 *
	 * Runs in approximately linear time with respect to the number of nodes,
	 * including virtual nodes.
	 */
	private void calculateSortValues() {
		RankList ranks = g.ranks;

		g.subgraphs.resetSortValues();
		g.subgraphs.resetIndices();

		/*
		 * For subgraphs, the sum of all positions is kept, along with the number of
		 * contributions, which is tracked in the subgraph's index field.
		 */
		ranks.forEach(rank -> rank.forEach(node -> {
			node.sortValue = node.index;
			Subgraph parent = node.getParent();
			while (parent != null) {
				parent.sortValue += node.sortValue;
				parent.index++;
				parent = parent.getParent();
			}
		}));

		/*
		 * For each subgraph, divide the sum of the positions by the number of
		 * contributions, to give the average position.
		 */
		for (Node element : g.subgraphs) {
			Subgraph subgraph = (Subgraph) element;
			subgraph.sortValue /= subgraph.index;
		}
	}

	private void repopulateRanks() {
		for (int i = 0; i < nestingTrees.length; i++) {
			Rank rank = g.ranks.getRank(i);
			rank.clear();
			nestingTrees[i].repopulateRank(rank);
		}
	}

	private static NodeList rightOf(Node left) {
		return (NodeList) left.workingData[0];
	}

	private static void leftToRight(Node left, Node right) {
		rightOf(left).add(right);
	}

	static void sortedInsert(List<Node> list, Node node) {
		int insert = 0;
		while (insert < list.size() && list.get(insert).sortValue > node.sortValue) {
			insert++;
		}
		list.add(insert, node);
	}

	private void topologicalSort() {
		for (NestingTree nestingTree : nestingTrees) {
			nestingTree.getSortValueFromSubgraph();
			nestingTree.recursiveSort(false);
		}
	}

	void init() {
		g.ranks.forEach(rank -> rank.forEach(n -> n.workingData[0] = new NodeList()));
		for (Node element : g.subgraphs) {
			Subgraph s = (Subgraph) element;
			s.workingData[0] = new NodeList();
		}
	}

	@Override
	public void visit(DirectedGraph dg) {
		g = (CompoundDirectedGraph) dg;

		init();
		buildSubgraphOrderingGraph();
		calculateSortValues();
		breakSubgraphCycles();
		topologicalSort();
		repopulateRanks();
	}

}
