/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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

package org.eclipse.draw2d;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Animates the layout of a figure's children. The animator will capture the
 * effects of a layout manager, and then play back the placement of children
 * using linear interpolation for each child's start and end locations.
 * <P>
 * To use an animator, hook it as a layout listener for the figure whose layout
 * is to be animated, by calling
 * {@link IFigure#addLayoutListener(LayoutListener)}. It is not necessary to
 * have an animator for every figure in a composition that is undergoing
 * animation. For example, if a figure without an animator moves during the
 * animation, it will continue to move and layout its children normally during
 * each step of the animation.
 * <P>
 * Animator must be used in conjunction with layouts. If figures are placed
 * manually using <code>setBounds()</code>, the animator may not be able to
 * track and playback the changes that occur.
 *
 * @since 3.2
 */
public class LayoutAnimator extends Animator implements LayoutListener {

	static final LayoutAnimator INSTANCE = new LayoutAnimator();

	/**
	 * Constructs a new Animator. The default instance ({@link #getDefault()}) can
	 * be used on all figures being animated.
	 *
	 * @since 3.2
	 */
	protected LayoutAnimator() {
	}

	/**
	 * Returns an object encapsulating the placement of children in a container.
	 * This method is called to capture both the initial and final states.
	 *
	 * @param container the container figure
	 * @return the current state
	 * @since 3.2
	 */
	@Override
	protected Object getCurrentState(IFigure container) {
		Map<IFigure, Rectangle> locations = new HashMap<>();
		container.getChildren().forEach(child -> locations.put(child, child.getBounds().getCopy()));
		return locations;
	}

	/**
	 * Returns the default instance.
	 *
	 * @return the default instance
	 * @since 3.2
	 */
	public static LayoutAnimator getDefault() {
		return INSTANCE;
	}

	/**
	 * Hooks invalidation in case animation is in progress.
	 *
	 * @see LayoutListener#invalidate(IFigure)
	 */
	@Override
	public final void invalidate(IFigure container) {
		if (Animation.isInitialRecording()) {
			Animation.hookAnimator(container, this);
		}
	}

	/**
	 * Hooks layout in case animation is in progress.
	 *
	 * @see org.eclipse.draw2d.LayoutListener#layout(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public final boolean layout(IFigure container) {
		if (Animation.isAnimating()) {
			return Animation.hookPlayback(container, this);
		}
		return false;
	}

	/**
	 * Plays back the animated layout.
	 *
	 * @see Animator#playback(IFigure)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean playback(IFigure container) {
		Map<IFigure, Rectangle> initial = (Map<IFigure, Rectangle>) Animation.getInitialState(this, container);
		Map<IFigure, Rectangle> ending = (Map<IFigure, Rectangle>) Animation.getFinalState(this, container);
		if (initial == null) {
			return false;
		}

		float progress = Animation.getProgress();
		float ssergorp = 1 - progress;

		for (IFigure child : container.getChildren()) {
			Rectangle rect1 = initial.get(child);
			Rectangle rect2 = ending.get(child);

			// TODO need to change this to hide the figure until the end.
			if (rect1 == null) {
				continue;
			}
			child.setBounds(new Rectangle(Math.round(progress * rect2.x + ssergorp * rect1.x),
					Math.round(progress * rect2.y + ssergorp * rect1.y),
					Math.round(progress * rect2.width + ssergorp * rect1.width),
					Math.round(progress * rect2.height + ssergorp * rect1.height)));
		}
		return true;
	}

	/**
	 * Hooks post layout in case animation is in progress.
	 *
	 * @see LayoutListener#postLayout(IFigure)
	 */
	@Override
	public final void postLayout(IFigure container) {
		if (Animation.isFinalRecording()) {
			Animation.hookNeedsCapture(container, this);
		}
	}

	/**
	 * This callback is unused. Reserved for possible future use.
	 *
	 * @see LayoutListener#remove(IFigure)
	 */
	@Override
	public final void remove(IFigure child) {
		// unused
	}

	/**
	 * This callback is unused. Reserved for possible future use.
	 *
	 * @see LayoutListener#setConstraint(IFigure, Object)
	 */
	@Override
	public final void setConstraint(IFigure child, Object constraint) {
		// unused
	}

}
