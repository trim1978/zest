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

import java.util.List;

import org.eclipse.zest.layouts.interfaces.SubgraphLayout;

public interface NestedLayoutEntity extends LayoutEntity {

	/** Returns the parent entity. */
	NestedLayoutEntity getParent();

	/** Returns the list of children. */
	List getChildren();

	/** Returns true if this entity has children. */
	boolean hasChildren();

}
