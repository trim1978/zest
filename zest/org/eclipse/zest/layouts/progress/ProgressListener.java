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
package org.eclipse.zest.layouts.progress;

public interface ProgressListener {
	public static class Stub implements ProgressListener {
		@Override
		public void progressStarted(ProgressEvent e) {
		}

		@Override
		public void progressUpdated(ProgressEvent e) {
		}

		@Override
		public void progressEnded(ProgressEvent e) {
		}
	}

	/**
	 *
	 * @param e
	 */
	public void progressStarted(ProgressEvent e);

	/**
	 * Called when the progress of a layout changes
	 */
	public void progressUpdated(ProgressEvent e);

	/**
	 *
	 * @param e
	 */
	public void progressEnded(ProgressEvent e);
}
