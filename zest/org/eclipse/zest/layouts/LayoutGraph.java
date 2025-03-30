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

public interface LayoutGraph {

	/**
	 * Adds a node to this graph.
	 *
	 * @param node The new node.
	 */
	public void addEntity(LayoutEntity node);

	/**
	 * Adds the given relationship.
	 *
	 * @param relationship
	 */
	public void addRelationship(LayoutRelationship relationship);

	/**
	 * Returns a list of LayoutEntity objects that represent the objects added to
	 * this graph using addNode.
	 *
	 * @return List A List of LayoutEntity objects.
	 */
	public List getEntities();

	/**
	 * Returns a list of LayoutRelationship objects that represent the objects added
	 * to this graph using addRelationship.
	 *
	 * @return List A List of LayoutRelationship objects.
	 */
	public List getRelationships();

	/**
	 * Determines if the graph is bidirectional.
	 *
	 * @return boolean If the graph is bidirectional.
	 */
	public boolean isBidirectional();

}
