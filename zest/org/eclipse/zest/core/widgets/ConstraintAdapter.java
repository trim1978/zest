/*******************************************************************************
 * Copyright 2005-2006, 2024 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.core.widgets;

import org.eclipse.zest.layouts.constraints.LayoutConstraint;

/**
 *
 * This interface is used to populate layout constraints on Zest nodes.
 * Constraint will be a instance of LayoutConstraint (look at the heirarchy for
 * an up-to-date list).
 *
 * @author Ian Bull
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ConstraintAdapter {

	/**
	 *
	 * @param object
	 * @param constraint
	 */
	public void populateConstraint(Object object, LayoutConstraint constraint);

}
