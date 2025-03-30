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

public class LayoutIterationEvent {
	private List relationshipsToLayout, entitiesToLayout;
	private int iterationCompleted;

	/**
	 * Return the relationships used in this layout.
	 */
	public List getRelationshipsToLayout() {
		return relationshipsToLayout;
	}

	/**
	 * Return the entities used in this layout.
	 */
	public List getEntitiesToLayout() {
		return entitiesToLayout;
	}

	/**
	 * Return the iteration of the layout algorithm that was just completed.
	 */
	public int getIterationCompleted() {
		return iterationCompleted;
	}
}
