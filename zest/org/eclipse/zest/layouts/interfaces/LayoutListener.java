/*******************************************************************************
 * Copyright (c) 2009-2010, 2024 Mateusz Matela and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.layouts.interfaces;

import org.eclipse.zest.layouts.LayoutAlgorithm;

/**
 *
 * @author irbull
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface LayoutListener {

	/**
	 * This method is called whenever location of a particular node is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(LayoutContext, NodeLayout)} event will be fired). If true
	 * is returned, it means that the receiving listener has intercepted this event.
	 * Intercepted events will not be passed to the rest of the listeners. If the
	 * event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout(boolean) applyLayout(boolean)} will be
	 * called on the context's main algorithm.LayoutListener
	 *
	 * @param context the layout context that fired the event
	 * @param node    the node that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeMoved(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever size of a particular node is changed within
	 * observed context. This usually implicates change of position (the center of
	 * the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(LayoutContext, NodeLayout)} event will be fired).
	 *
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout(boolean) applyLayout(boolean)} will be
	 * called on the context's main algorithm.
	 *
	 * @param context the layout context that fired the event
	 * @param node    the node that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeResized(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever location of a particular subgraph is changed
	 * within observed context. If true is returned, it means that the receiving
	 * listener has intercepted this event. Intercepted events will not be passed to
	 * the rest of the listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout(boolean) applyLayout(boolean)} will be
	 * called on the context's main algorithm.
	 *
	 * @param context  the layout context that fired the event
	 * @param subgraph the subgraph that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphMoved(LayoutContext context, SubgraphLayout subgraph);

	/**
	 * This method is called whenever size of a particular subgraph is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(LayoutContext, NodeLayout)} event will be fired).
	 *
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout(boolean) applyLayout(boolean)} will be
	 * called on the context's main algorithm.
	 *
	 * @param context  the layout context that fired the event
	 * @param subgraph the subgraph that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphResized(LayoutContext context, SubgraphLayout subgraph);

}
