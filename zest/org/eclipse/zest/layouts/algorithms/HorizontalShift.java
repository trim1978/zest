/*******************************************************************************
 * Copyright 2006, 2024 CHISEL Group, University of Victoria, Victoria, BC,
 *                      Canada, Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria, Alois Zoitl
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class HorizontalShift extends AbstractLayoutAlgorithm.Zest1 {

	private static final double DELTA = 10;
	private static final double VSPACING = 2;

	public HorizontalShift(int styles) {
		super(styles);
	}

	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {

		List<List<InternalNode>> row = new ArrayList<>();
		for (InternalNode element : entitiesToLayout) {
			addToRowList(element, row);
		}

		int heightSoFar = 0;

		Collections.sort(row, (arg0, arg1) -> {
			LayoutEntity node0 = arg0.get(0).getLayoutEntity();
			LayoutEntity node1 = arg1.get(0).getLayoutEntity();
			return (int) (node0.getYInLayout() - (node1.getYInLayout()));
		});

		for (List<InternalNode> currentRow : row) {
			Collections.sort(currentRow, (arg0,
					arg1) -> (int) (arg1.getLayoutEntity().getYInLayout() - arg0.getLayoutEntity().getYInLayout()));
			int i = 0;
			int width = (int) ((boundsWidth / 2) - currentRow.size() * 75);

			heightSoFar += currentRow.get(0).getLayoutEntity().getHeightInLayout() + VSPACING * 8;
			for (InternalNode currentNode : currentRow) {
				i++;
				double location = width + 10 * i;
				currentNode.setLocation(location, heightSoFar);
				width += currentNode.getLayoutEntity().getWidthInLayout();
			}
		}
	}

	private static void addToRowList(InternalNode node, List<List<InternalNode>> list) {
		double layoutY = node.getLayoutEntity().getYInLayout();

		for (List<InternalNode> currentRow : list) {
			InternalNode currentRowNode = currentRow.get(0);
			double currentRowY = currentRowNode.getLayoutEntity().getYInLayout();
			// double currentRowHeight =
			// currentRowNode.getLayoutEntity().getHeightInLayout();
			if (layoutY >= (currentRowY - DELTA) && layoutY <= currentRowY + DELTA) {
				currentRow.add(node);
				// list.add(i, currentRow);
				return;
			}
		}
		List<InternalNode> newRow = new ArrayList<>();
		newRow.add(node);
		list.add(newRow);
	}

	@Override
	protected int getCurrentLayoutStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
	}
}
