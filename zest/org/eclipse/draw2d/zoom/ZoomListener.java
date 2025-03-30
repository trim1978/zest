/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.zoom;

/**
 * Listens to zoom level changes.
 *
 * @author Eric Bordeau
 * @since 3.13
 */
public interface ZoomListener {

	/**
	 * Called whenever the ZoomManager's zoom level changes.
	 *
	 * @param zoom the new zoom level.
	 */
	void zoomChanged(double zoom);

}
