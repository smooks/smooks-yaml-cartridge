/*-
 * ========================LICENSE_START=================================
 * smooks-yaml-cartridge
 * %%
 * Copyright (C) 2020 Smooks
 * %%
 * Licensed under the terms of the Apache License Version 2.0, or
 * the GNU Lesser General Public License version 3.0 or later.
 * 
 * SPDX-License-Identifier: Apache-2.0 OR LGPL-3.0-or-later
 * 
 * ======================================================================
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ======================================================================
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * =========================LICENSE_END==================================
 */
package org.smooks.cartridges.yaml;

import org.smooks.javabean.DataDecodeException;

/**
 * Defines the strategy how to handle anchors and aliasses.
 *
 * @author maurice_zeijen
 */
public enum AliasStrategy {
	/**
	 * Adds a 'id' attribute to the element with the anchor and the 'ref'
	 * attribute to the elements with the alias. The value of these attributes
	 * is the name of the anchor. The reference needs to be handled within the
	 * Smooks config. The attribute names can be set via the
	 * 'anchorAttributeName' and 'aliasAttributeName' properties.
	 */
	REFER,

	/**
	 * The elements or value from the anchor are resolved (copied) under the
	 * element with the alias. Smooks doesn't see that there was a reference.
	 */
	RESOLVE,

	/**
	 * A combination of REFER and RESOLVE. The element of the anchor are
	 * resolved and the attributes are set. You should use this if you want to
	 * resolve the element but also need the alias name because it has a
	 * business meaning.
	 */
	REFER_RESOLVE;

	public static final String REFER_STR = "REFER";
	public static final String RESOLVE_STR = "RESOLVE";
	public static final String REFER_RESOLVE_STR = "REFER_RESOLVE";

	/**
	 * A Data decoder for this Enum
	 *
	 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
	 *
	 */
	public static class DataDecoder implements org.smooks.javabean.DataDecoder {

		/* (non-Javadoc)
		 * @see org.smooks.javabean.DataDecoder#decode(java.lang.String)
		 */
		public Object decode(final String data) throws DataDecodeException {
			final String value = data.toUpperCase();

			return valueOf(value);
		}

	}
}
