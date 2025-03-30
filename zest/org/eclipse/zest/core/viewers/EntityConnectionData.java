/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC,
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

/**
 * A simple object that is used as the "external connection" in content
 * providers that don't ask the user to create their own external connection.
 *
 * This is used whenever users don't specify a connection
 *
 * @author Del Myers
 */
public final class EntityConnectionData {
	public final Object source;
	public final Object dest;

	/**
	 * Creates a new entity connection data. The source and dest are users nodes.
	 */
	public EntityConnectionData(Object source, Object dest) {
		/*
		 * if (source == null) { throw new
		 * RuntimeException("Creating relationship with null source object"); } if (dest
		 * == null) { throw new
		 * RuntimeException("Creating relationship with null dest object"); }
		 */
		this.source = source;
		this.dest = dest;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EntityConnectionData)) {
			return false;
		}
		return (this.source.equals(((EntityConnectionData)obj).source) && this.dest.equals(((EntityConnectionData)obj).dest));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.source.hashCode() + this.dest.hashCode();
	}
}
