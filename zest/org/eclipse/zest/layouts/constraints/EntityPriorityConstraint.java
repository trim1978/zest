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
package org.eclipse.zest.layouts.constraints;

public class EntityPriorityConstraint implements LayoutConstraint {

	// A priority that can be set for nodes. This could be used
	// for a treemap layout
	public double priority = 1.0;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.layouts.constraints.LayoutConstraint#clear()
	 */
	@Override
	public void clear() {
		this.priority = 1.0;
	}

}
