/*******************************************************************************
 * Copyright 2005, 2024 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
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

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class HorizontalTreeLayoutAlgorithm extends TreeLayoutAlgorithm.Zest1 {

	/**
	 * Creates a horizontal tree layout with no style
	 */
	public HorizontalTreeLayoutAlgorithm() {
		this(LayoutStyles.NONE);
	}

	/**
	 *
	 */
	public HorizontalTreeLayoutAlgorithm(int styles) {
		super(styles);
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		// NOTE: width and height are swtiched here when calling super method
		super.preLayoutAlgorithm(entitiesToLayout, relationshipsToConsider, x, y, height, width);
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
		// swap x->y and width->height
		for (InternalNode entity : entitiesToLayout) {
			entity.setInternalLocation(entity.getInternalY(), entity.getInternalX());
			entity.setInternalSize(entity.getInternalWidth(), entity.getInternalHeight());
		}
		super.postLayoutAlgorithm(entitiesToLayout, relationshipsToConsider);
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
		if (asynchronous && continueous) {
			return false;
		} else if (asynchronous && !continueous) {
			return true;
		} else if (!asynchronous && continueous) {
			return false;
		} else if (!asynchronous && !continueous) {
			return true;
		}

		return false;
	}

}
