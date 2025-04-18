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
package org.eclipse.zest.layouts;

import org.eclipse.zest.layouts.constraints.LayoutConstraint;

public interface LayoutRelationship extends LayoutItem {

	/**
	 * Gets the sourceEntity of this SimpleRelation whether the relation is
	 * exchangeable or not.
	 *
	 * @return The sourceEntity.
	 */
	public LayoutEntity getSourceInLayout();

	/**
	 * Gets the destinationEntity of this SimpleRelation whether the relation is
	 * exchangeable or not.
	 *
	 * @return The destinationEntity of this SimpleRelation.
	 */
	public LayoutEntity getDestinationInLayout();

	/**
	 * Sets the internal relationship object.
	 *
	 * @param layoutInformation
	 */
	public void setLayoutInformation(Object layoutInformation);

	/**
	 * Returns the internal relationship object.
	 *
	 * @return Object
	 */
	public Object getLayoutInformation();

	/**
	 * Specify a set of bend points. The layout algorithm using this will pass in an
	 * empty array of bendPoints, or not even call this method, if there are no bend
	 * points associated with this edge.
	 *
	 * If you are updating an existing application you can just implement this
	 * method to do nothing.
	 *
	 * @param bendPoints A list of bend points. All bendpoint locations are
	 *                   expressed as percentages of the bounds (0,0 to 1,1).The
	 *                   first bendpoint in the list must be the source point of
	 *                   this relationship and the last bendpoint the destination
	 *                   point for this relationship. This allows the correct
	 *                   positioning of bendpoints relative to the source and
	 *                   destination points when drawing the graph.
	 */
	public void setBendPoints(LayoutBendPoint[] bendPoints);

	/**
	 * Clear bend points and related bounds If you are updating an existing
	 * application you can just implement this method to do nothing.
	 */
	public void clearBendPoints();

	/**
	 * Classes should update the specified layout constraint if recognized
	 */
	public void populateLayoutConstraint(LayoutConstraint constraint);
}
