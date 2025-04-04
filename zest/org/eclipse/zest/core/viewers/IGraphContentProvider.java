/*******************************************************************************
 * Copyright 2005 CHISEL Group, University of Victoria, Victoria, BC,
 *                      Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.core.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * A graph content provider.
 *
 * @author Ian Bull
 */
public interface IGraphContentProvider extends IStructuredContentProvider {

	/**
	 * Gets the source Object for the given relationship. Note, at least one of the
	 * source or destination must not be null. If both are null, then nothing can be
	 * displayed in the graph (a relationship cannot exist without nodes to be
	 * connected to). However, if one of getSource() or getDestination() returns
	 * null, then the resulting graph will contain an unconnected node for the
	 * non-null object returned from the other method.
	 *
	 * @param rel the relationship.
	 * @return the source, or null for an unconnected destination.
	 */
	public Object getSource(Object rel);

	/**
	 * Gets the target Object for the given relationship. Note, at least one of the
	 * source or destination must not be null. If both are null, then nothing can be
	 * displayed in the graph (a relationship cannot exist without nodes to be
	 * connected to). However, if one of getSource() or getDestination() returns
	 * null, then the resulting graph will contain an unconnected node for the
	 * non-null object returned from the other method.
	 *
	 * @param rel the relationship.
	 * @return the destination, or null for an unconnected source.
	 */
	public Object getDestination(Object rel);

	/**
	 * Returns all the relationships in the graph for the given input.
	 *
	 * @param input the input model object.
	 * @return all the relationships in the graph for the given input.
	 */
	@Override
	public Object[] getElements(Object input);

}
