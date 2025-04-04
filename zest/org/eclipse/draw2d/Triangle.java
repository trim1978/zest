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
package org.eclipse.draw2d;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A triangular graphical figure.
 */
public final class Triangle extends Shape implements Orientable {

	/**
	 * The direction this triangle will face. Possible values are
	 * {@link PositionConstants#NORTH}, {@link PositionConstants#SOUTH},
	 * {@link PositionConstants#EAST} and {@link PositionConstants#WEST}.
	 */
	protected int direction = NORTH;
	/**
	 * The orientation of this triangle. Possible values are
	 * {@link Orientable#VERTICAL} and {@link Orientable#HORIZONTAL}.
	 */
	protected int orientation = VERTICAL;

	/** The points of the triangle. */
	protected PointList triangle = new PointList(3);

	/**
	 * @see Shape#fillShape(Graphics)
	 */
	@Override
	protected void fillShape(Graphics g) {
		g.fillPolygon(triangle);
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	@Override
	protected void outlineShape(Graphics g) {
		g.drawPolygon(triangle);
	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	@Override
	public void primTranslate(int dx, int dy) {
		super.primTranslate(dx, dy);
		triangle.translate(dx, dy);
	}

	/**
	 * @see Orientable#setDirection(int)
	 */
	@Override
	public void setDirection(int value) {
		if ((value & (NORTH | SOUTH)) != 0) {
			orientation = VERTICAL;
		} else {
			orientation = HORIZONTAL;
		}
		direction = value;
		revalidate();
		repaint();
	}

	/**
	 * @see Orientable#setOrientation(int)
	 */
	@Override
	public void setOrientation(int value) {
		if (orientation == VERTICAL && value == HORIZONTAL) {
			if (direction == NORTH) {
				setDirection(WEST);
			} else {
				setDirection(EAST);
			}
		}
		if (orientation == HORIZONTAL && value == VERTICAL) {
			if (direction == WEST) {
				setDirection(NORTH);
			} else {
				setDirection(SOUTH);
			}
		}
	}

	/**
	 * @see IFigure#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		Rectangle r = new Rectangle();
		r.setBounds(getBounds());
		r.shrink(getInsets());
		r.resize(-1, -1);
		int size;
		if ((direction & (NORTH | SOUTH)) != 0) {
			// North or south
			size = Math.min(r.height, r.width / 2);
			r.y += (r.height - size) / 2;
		} else {
			// East or west.
			size = Math.min(r.height / 2, r.width);
			r.x += (r.width - size) / 2;
		}

		size = Math.max(size, 1); // Size cannot be negative

		Point head, p2, p3;

		if (direction == NORTH){
			head = new Point(r.x + r.width / 2, r.y);
			p2 = new Point(head.x - size, head.y + size);
			p3 = new Point(head.x + size, head.y + size);
		}
		else if (direction == SOUTH){
			head = new Point(r.x + r.width / 2, r.y + size);
			p2 = new Point(head.x - size, head.y - size);
			p3 = new Point(head.x + size, head.y - size);
		}
		else if (direction == WEST){
			head = new Point(r.x, r.y + r.height / 2);
			p2 = new Point(head.x + size, head.y - size);
			p3 = new Point(head.x + size, head.y + size);
		}
		else{
			head = new Point(r.x + size, r.y + r.height / 2);
			p2 = new Point(head.x - size, head.y - size);
			p3 = new Point(head.x - size, head.y + size);
		}
		triangle.removeAllPoints();
		triangle.addPoint(head);
		triangle.addPoint(p2);
		triangle.addPoint(p3);
	}

}
