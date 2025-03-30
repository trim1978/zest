/*******************************************************************************
 * Copyright 2005, 2024 CHISEL Group, University of Victoria, Victoria, BC,
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
package org.eclipse.zest.layouts.algorithms.internal;

import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

public class DynamicScreen {

	private final TreeSet<InternalNode> xCoords = new TreeSet<>(new XComparator());
	private final TreeSet<InternalNode> yCoords = new TreeSet<>(new YComparator());

	private DisplayIndependentRectangle screenBounds = null;

	double minX = 0.0;
	double minY = 0.0;
	double maxX = 0.0;
	double maxY = 0.0;

	public void cleanScreen() {
		minX = 0.0;
		minY = 0.0;
		maxX = 0.0;
		maxY = 0.0;
	}

	class XComparator implements Comparator<InternalNode> {
		@Override
		public int compare(InternalNode n1, InternalNode n2) {
			if (n1.getInternalX() > n2.getInternalX()) {
				return +1;
			}
			if (n1.getInternalX() < n2.getInternalX()) {
				return -1;
			}
			return n1.toString().compareTo(n2.toString());
		}
	}

	class YComparator implements Comparator<InternalNode> {
		@Override
		public int compare(InternalNode n1, InternalNode n2) {
			if (n1.getInternalY() > n2.getInternalY()) {
				return +1;
			}
			if (n1.getInternalY() < n2.getInternalY()) {
				return -1;
			}
			return n1.toString().compareTo(n2.toString());

		}
	}

	public DynamicScreen(int x, int y, int width, int height) {
		this.screenBounds = new DisplayIndependentRectangle(x, y, width, height);
	}

	public void removeNode(InternalNode node) {
		xCoords.remove(node);
		yCoords.remove(node);
	}

	public void addNode(InternalNode node) {
		xCoords.add(node);
		yCoords.add(node);
	}

	public DisplayIndependentPoint getScreenLocation(InternalNode node) {

		DisplayIndependentRectangle layoutBounds = calculateBounds();

		double x = (layoutBounds.width == 0) ? 0 : (node.getInternalX() - layoutBounds.x) / layoutBounds.width;
		double y = (layoutBounds.height == 0) ? 0 : (node.getInternalY() - layoutBounds.y) / layoutBounds.height;

		x = screenBounds.x + x * screenBounds.width;
		y = screenBounds.y + y * screenBounds.height;

		return new DisplayIndependentPoint(x, y);
	}

	public DisplayIndependentPoint getVirtualLocation(DisplayIndependentPoint point) {

		DisplayIndependentRectangle layoutBounds = calculateBounds();

		double x = (point.x / screenBounds.width) * layoutBounds.width + layoutBounds.x;
		double y = (point.y / screenBounds.height) * layoutBounds.height + layoutBounds.y;

		return new DisplayIndependentPoint(x, y);
	}

	private DisplayIndependentRectangle calculateBounds() {
		InternalNode n1 = xCoords.first();
		InternalNode n2 = xCoords.last();
		InternalNode n3 = yCoords.first();
		InternalNode n4 = yCoords.last();
		double x = n1.getInternalX();
		double width = n2.getInternalX();
		double y = n3.getInternalY();
		double height = n4.getInternalY();
		return new DisplayIndependentRectangle(x, y, width - x, height - y);
	}

}
