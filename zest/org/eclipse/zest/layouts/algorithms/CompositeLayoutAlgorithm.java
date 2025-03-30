/*******************************************************************************
 * Copyright 2005-2010, 2024 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria - initial API and implementation
 *               Mateusz Matela
 *               Ian Bull
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

public class CompositeLayoutAlgorithm implements LayoutAlgorithm {

	public static class Zest1 extends AbstractLayoutAlgorithm.Zest1 {

		LayoutAlgorithm.Zest1[] algorithms = null;

		public Zest1(int styles, LayoutAlgorithm.Zest1[] algoirthms) {
			super(styles);
			this.algorithms = algoirthms;
		}

		public Zest1(LayoutAlgorithm.Zest1[] algoirthms) {
			this(0, algoirthms);
		}

		@Override
		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double boundsX, double boundsY, double boundsWidth, double boundsHeight) {

			for (LayoutAlgorithm.Zest1 algorithm : algorithms) {
				try {
					algorithm.applyLayout(entitiesToLayout, relationshipsToConsider, boundsX, boundsY, boundsWidth,
							boundsHeight, this.internalAsynchronous, this.internalContinuous);
				} catch (InvalidLayoutConfiguration e) {
					e.printStackTrace();
				}
			}
			for (InternalNode element : entitiesToLayout) {
				element.getLayoutEntity().setLocationInLayout(element.getXInLayout(), element.getYInLayout());
			}

			// updateLayoutLocations(entitiesToLayout);
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

	private LayoutAlgorithm[] algorithms = null;

	public CompositeLayoutAlgorithm(LayoutAlgorithm[] algorithms) {
		this.algorithms = algorithms;
	}

	/**
	 * @deprecated Since Zest 2.0, use
	 *             {@link #CompositeLayoutAlgorithm(LayoutAlgorithm[])}
	 */
	@Deprecated
	public CompositeLayoutAlgorithm(int style, LayoutAlgorithm[] layoutAlgorithms) {
		this(layoutAlgorithms);
	}

	@Override
	public void applyLayout(boolean clean) {
		for (LayoutAlgorithm algorithm : algorithms) {
			algorithm.applyLayout(clean);
		}
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		for (LayoutAlgorithm algorithm : algorithms) {
			algorithm.setLayoutContext(context);
		}
	}
}
