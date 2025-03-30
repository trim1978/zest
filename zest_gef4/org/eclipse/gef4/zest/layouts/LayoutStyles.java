/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.gef4.zest.layouts;

import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * @author Ian Bull
 * @deprecated Since Zest 2.0, layout styles are set on the individual layout
 *             algorithms (e.g. {@link TreeLayoutAlgorithm#isResizing()})
 */
public interface LayoutStyles {

	/** Default layout style constant. */
	public final static int NONE = 0x00;

	/**
	 * Layout constant indicating that the layout algorithm should NOT resize
	 * any of the nodes.
	 */
	public final static int NO_LAYOUT_NODE_RESIZING = 0x01;

	/**
	 * Some layouts may prefer to expand their bounds beyond those of the
	 * requested bounds. This flag asks the layout not to do so.
	 */
	public static final int ENFORCE_BOUNDS = 0X02;

}
