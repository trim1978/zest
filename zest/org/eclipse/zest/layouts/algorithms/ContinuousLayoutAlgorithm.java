/*******************************************************************************
 * Copyright 2005, 2024 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public abstract class ContinuousLayoutAlgorithm extends AbstractLayoutAlgorithm.Zest1 {

	double x, y, widht, height;

	public ContinuousLayoutAlgorithm(int styles) {
		super(styles);
	}

	/**
	 * The logic to determine if a layout should continue running or not
	 */
	protected abstract boolean performAnotherNonContinuousIteration();

	/**
	 * Computes a single iteration of the layout algorithm
	 */
	protected abstract void computeOneIteration(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height);

	private boolean continueRunning() {
		if (layoutStopped) {
			return false;
		}
		if (this.internalContinuous) {
			return true;
		}
		return performAnotherNonContinuousIteration();
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		this.setBounds(x, y, width, height);

	}

	public synchronized DisplayIndependentRectangle getBounds() {
		return new DisplayIndependentRectangle(this.x, this.y, this.widht, this.height);
	}

	public synchronized void setBounds(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.widht = width;
		this.height = height;
	}

	/**
	 * Calculates and applies the positions of the given entities based on a spring
	 * layout using the given relationships.
	 */
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {

		this.setBounds(x, y, width, height);

		while (continueRunning()) {
			// check for entities and relationships to add or remove
			entitiesToLayout = updateEntities(entitiesToLayout);
			relationshipsToConsider = updateRelationships(relationshipsToConsider);
			DisplayIndependentRectangle bounds = this.getBounds();
			double localX = bounds.x;
			double localY = bounds.y;
			double localWidth = bounds.width;
			double localHeight = bounds.height;

			computeOneIteration(entitiesToLayout, relationshipsToConsider, localX, localY, localWidth, localHeight);

			updateLayoutLocations(entitiesToLayout);

			if (this.internalContinuous) {
				fireProgressEvent(1, 1);
			} else {
				fireProgressEvent(getCurrentLayoutStep(), getTotalNumberOfLayoutSteps());
			}

		}
	}

}
