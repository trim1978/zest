/*******************************************************************************
 * Copyright 2005-2010 CHISEL Group, University of Victoria, Victoria,
 *                     BC, Canada and others.
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
package org.eclipse.zest.layouts;

import java.util.Comparator;
import java.util.List;

import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.progress.ProgressListener;

/**
 * A simple interface used by all layouts.
 *
 *
 * @author Casey Best
 * @author Ian Bull
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings("rawtypes")
public interface LayoutAlgorithm {

	static interface Zest1 extends LayoutAlgorithm {

		/**
		 * Apply the layout to the given entities. The entities will be moved and
		 * resized based on the algorithm.
		 *
		 * @param entitiesToLayout        Apply the algorithm to these entities
		 * @param relationshipsToConsider Only consider these relationships when
		 *                                applying the algorithm.
		 * @param x                       The left side of the bounds in which the
		 *                                layout can place the entities.
		 * @param y                       The top side of the bounds in which the layout
		 *                                can place the entities.
		 * @param width                   The width of the bounds in which the layout
		 *                                can place the entities.
		 * @param height                  The height of the bounds in which the layout
		 *                                can place the entities.
		 * @param asynchronous            Should the algorithm run Asynchronously
		 */
		public void applyLayout(LayoutEntity[] entitiesToLayout, LayoutRelationship[] relationshipsToConsider, double x,
				double y, double width, double height, boolean asynchronous, boolean continuous)
				throws InvalidLayoutConfiguration;

		/**
		 * Returns whether or not the algorithm is currenly running
		 *
		 * @return True if a layout algorithm is currenly running, false otherwise
		 */
		public boolean isRunning();

		/**
		 * Determines the order in which the objects should be displayed. Note: Some
		 * algorithms force a specific order, in which case this comparator will be
		 * ignored.
		 */
		public void setComparator(Comparator comparator);

		/**
		 * Filters the entities and relationships to apply the layout on
		 */
		public void setFilter(Filter filter);

		/**
		 * Set the width to height ratio you want the entities to use Note: Each layout
		 * is responsible for ensuring this ratio is used. Note: By default the layout
		 * will use a ratio of 1.0 for each entity.
		 */
		public void setEntityAspectRatio(double ratio);

		/**
		 * Returns the width to height ratio this layout will use to set the size of the
		 * entities. Note: By default the layout will use a ratio of 1.0 for each
		 * entity.
		 */
		public double getEntityAspectRatio();

		/**
		 * A layout algorithm could take an uncomfortable amout of time to complete. To
		 * relieve some of the mystery, the layout algorithm will notify each
		 * ProgressListener of its progress.
		 */
		public void addProgressListener(ProgressListener listener);

		/**
		 * Removes the given progress listener, preventing it from receiving any more
		 * updates.
		 */
		public void removeProgressListener(ProgressListener listener);

		/**
		 * Makes a request to this layout algorithm to stop running.
		 */
		public void stop();

		/**
		 * Sets the style for this layout algorithm. This will overwrite any other style
		 * set.
		 *
		 * @param style
		 */
		public void setStyle(int style);

		public int getStyle();

		public void addEntity(LayoutEntity entity);

		public void addRelationship(LayoutRelationship relationship);

		public void removeEntity(LayoutEntity entity);

		public void removeRelationship(LayoutRelationship relationship);

		public void removeRelationships(List<? extends LayoutRelationship> relationships);
	}

	/**
	 * Sets the layout context for this algorithm. The receiver will unregister from
	 * its previous layout context and register to the new one (registration means
	 * for example adding listeners). After a call to this method, the receiving
	 * algorithm can compute and cache internal data related to given context and
	 * perform an initial layout.
	 *
	 * @param context a new layout context or null if this algorithm should not
	 *                perform any layout
	 * @since 2.0
	 */
	public void setLayoutContext(LayoutContext context);

	/**
	 * Makes this algorithm perform layout computation and apply it to its context.
	 *
	 * @param clean if true the receiver should assume that the layout context has
	 *              changed significantly and recompute the whole layout even if it
	 *              keeps track of changes with listeners. False can be used after
	 *              dynamic layout in a context is turned back on so that layout
	 *              algorithm working in background can apply accumulated changes.
	 *              Static layout algorithm can ignore this call entirely if clean
	 *              is false.
	 * @since 2.0
	 */
	public void applyLayout(boolean clean);
}
