/*******************************************************************************
 * Copyright (c) 2007, 2024 IBM Corporation and others.
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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;

import org.eclipse.draw2d.geometry.Dimension;

/**
 * Provides miscellaneous text operations. Clients may subclass this class if
 * necessary.
 *
 * @author crevells
 * @since 3.4
 */
public class TextUtilities {

	/**
	 * a singleton default instance
	 */
	public static TextUtilities INSTANCE = new TextUtilities();

	/**
	 * Returns the Dimensions of <i>s</i> in Font <i>f</i>.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the dimensions of the given string
	 */
	@SuppressWarnings("static-method")
	public Dimension getStringExtents(String s, Font f) {
		return FigureUtilities.getStringExtents(s, f);
	}

	/**
	 * Returns the Dimensions of the given text, converting newlines and tabs
	 * appropriately.
	 *
	 * @param s the text
	 * @param f the font
	 * @return the dimensions of the given text
	 */
	@SuppressWarnings("static-method")
	public Dimension getTextExtents(String s, Font f) {
		return FigureUtilities.getTextExtents(s, f);
	}

	/**
	 * Gets the font's ascent.
	 *
	 * @param font
	 * @return the font's ascent
	 */
	@SuppressWarnings("static-method")
	public int getAscent(Font font) {
		FontMetrics fm = FigureUtilities.getFontMetrics(font);
		return fm.getHeight() - fm.getDescent();
	}

	/**
	 * Gets the font's descent.
	 *
	 * @param font
	 * @return the font's descent
	 */
	@SuppressWarnings("static-method")
	public int getDescent(Font font) {
		return FigureUtilities.getFontMetrics(font).getDescent();
	}

	/**
	 * Returns the largest substring of <i>s</i> in Font <i>f</i> that can be
	 * confined to the number of pixels in <i>availableWidth</i>.
	 *
	 * @param s              the original string
	 * @param f              the font
	 * @param availableWidth the available width
	 * @return the largest substring that fits in the given width
	 */
	public int getLargestSubstringConfinedTo(String s, Font f, int availableWidth) {
		FontMetrics metrics = FigureUtilities.getFontMetrics(f);
		int min = 0;
		int max = s.length() + 1;
		double avg = metrics.getAverageCharacterWidth();

		// The size of the current guess
		int guess = 0;
		int guessSize = 0;
		while ((max - min) > 1) {
			// Pick a new guess size
			// New guess is the last guess plus the missing width in pixels
			// divided by the average character size in pixels
			guess = guess + (int) ((availableWidth - guessSize) / avg);

			if (guess >= max) {
				guess = max - 1;
			}
			if (guess <= min) {
				guess = min + 1;
			}

			// Measure the current guess
			guessSize = getTextExtents(s.substring(0, guess), f).width;

			if (guessSize < availableWidth) {
				// We did not use the available width
				min = guess;
			} else {
				// We exceeded the available width
				max = guess;
			}
		}
		return min;
	}
}
