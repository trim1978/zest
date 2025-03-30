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

public class ProgressEvent {
	int stepsCompleted;
	int totalSteps;

	/**
	 * Creates a progress event.
	 *
	 * @param stepsCompleted     The current progress out of the total
	 * @param totalNumberOfSteps The number used to indicate when the algorithm will
	 *                           finish
	 */
	public ProgressEvent(int stepsCompleted, int totalNumberOfSteps) {
		this.stepsCompleted = stepsCompleted;
		this.totalSteps = totalNumberOfSteps;
	}

	/**
	 * Returns the number of steps already completed.
	 */
	public int getStepsCompleted() {
		return stepsCompleted;
	}

	/**
	 * Returns the total number of steps to complete.
	 */
	public int getTotalNumberOfSteps() {
		return totalSteps;
	}
}
