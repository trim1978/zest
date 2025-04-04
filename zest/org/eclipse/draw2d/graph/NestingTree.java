/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NestingTree {

	List<Object /* Node or NestingTree */> contents = new ArrayList<>();
	boolean isLeaf = true;
	int size;
	double sortValue;
	Node subgraph;

	private static void addToNestingTree(Map<Subgraph, NestingTree> map, Node child) {
		Subgraph subgraph = child.getParent();
		NestingTree parent = map.get(subgraph);
		if (parent == null) {
			parent = new NestingTree();
			parent.subgraph = subgraph;
			map.put(subgraph, parent);
			if (subgraph != null) {
				addToNestingTree(map, parent);
			}
		}
		parent.contents.add(child);
	}

	private static void addToNestingTree(Map<Subgraph, NestingTree> map, NestingTree branch) {
		Subgraph subgraph = branch.subgraph.getParent();
		NestingTree parent = map.get(subgraph);
		if (parent == null) {
			parent = new NestingTree();
			parent.subgraph = subgraph;
			map.put(subgraph, parent);
			if (subgraph != null) {
				addToNestingTree(map, parent);
			}
		}
		parent.contents.add(branch);
	}

	static NestingTree buildNestingTreeForRank(Rank rank) {
		Map<Subgraph, NestingTree> nestingMap = new HashMap<>();

		for (int j = 0; j < rank.count(); j++) {
			Node node = rank.get(j);
			addToNestingTree(nestingMap, node);
		}

		return nestingMap.get(null);
	}

	void calculateSortValues() {
		int total = 0;
		for (Object o : contents) {
			if (o instanceof NestingTree) {
				NestingTree e = (NestingTree)o;
				isLeaf = false;
				e.calculateSortValues();
				total += (int) (e.sortValue * e.size);
				size += e.size;
			} else {
				Node n = (Node) o;
				n.sortValue = n.index;
				total += n.index;
				size++;
			}
		}
		sortValue = (double) total / size;
	}

	void getSortValueFromSubgraph() {
		if (subgraph != null) {
			sortValue = subgraph.sortValue;
		}
		for (Object o : contents) {
			if (o instanceof NestingTree) {
				((NestingTree) o).getSortValueFromSubgraph();
			}
		}
	}

	void recursiveSort(boolean sortLeaves) {
		if (isLeaf && !sortLeaves) {
			return;
		}
		boolean change = false;
		// Use modified bubble sort for almost-sorted lists.
		do {
			change = false;
			for (int i = 0; i < contents.size() - 1; i++) {
				change |= swap(i);
			}
			if (!change) {
				break;
			}
			change = false;
			for (int i = contents.size() - 2; i >= 0; i--) {
				change |= swap(i);
			}
		} while (change);
		for (Object o : contents) {
			if (o instanceof NestingTree) {
				((NestingTree)o).recursiveSort(sortLeaves);
			}
		}
	}

	void repopulateRank(Rank r) {
		for (Object o : contents) {
			if (o instanceof Node) {
				r.add((Node)o);
			} else {
				((NestingTree) o).repopulateRank(r);
			}
		}
	}

	boolean swap(int index) {
		Object left = contents.get(index);
		Object right = contents.get(index + 1);
		double iL = left instanceof Node ? ((Node)left).sortValue : ((NestingTree) left).sortValue;
		double iR = right instanceof Node ? ((Node)right).sortValue : ((NestingTree) right).sortValue;
		if (iL <= iR) {
			return false;
		}
		contents.set(index, right);
		contents.set(index + 1, left);
		return true;
	}

	@Override
	public String toString() {
		return "Nesting:" + subgraph; //$NON-NLS-1$
	}

}