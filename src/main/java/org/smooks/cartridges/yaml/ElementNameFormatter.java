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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans up or replaces names mend for XML elements.
 *
 * @author maurice_zeijen
 */
public class ElementNameFormatter {

	private static final Pattern ILLEGAL_ELEMENT_NAME_PATTERN = Pattern.compile("^[.]|[^a-zA-Z0-9_.-]");

    private final Map<String, String> keyMap;

    private final String keyWhitspaceReplacement;

    private final String keyPrefixOnNumeric;

    private final String illegalElementNameCharReplacement;

    private final boolean doKeyReplacement ;

    private final boolean doKeyWhitspaceReplacement;

    private final boolean doPrefixOnNumericKey;

    private final boolean doIllegalElementNameCharReplacement;

	public ElementNameFormatter(Map<String, String> keyMap, String keyWhitspaceReplacement, String keyPrefixOnNumeric, String illegalElementNameCharReplacement) {
		this.keyMap = keyMap;
		this.keyWhitspaceReplacement = keyWhitspaceReplacement;
		this.keyPrefixOnNumeric = keyPrefixOnNumeric;
		this.illegalElementNameCharReplacement = illegalElementNameCharReplacement;

		doKeyReplacement = !keyMap.isEmpty();
		doKeyWhitspaceReplacement = keyWhitspaceReplacement != null;
		doPrefixOnNumericKey = keyPrefixOnNumeric != null;
		doIllegalElementNameCharReplacement = illegalElementNameCharReplacement != null;
	}

	/**
	 * @param text
	 * @return
	 */
	public String format(String text) {

		boolean replacedKey = false;
		if(doKeyReplacement) {

			String mappedKey = keyMap.get(text);

			replacedKey = mappedKey != null;
			if(replacedKey) {
				text = mappedKey;
			}

		}

		if(!replacedKey) {
			if(doKeyWhitspaceReplacement) {
				text = text.replace(" ", keyWhitspaceReplacement);
			}

			if(doPrefixOnNumericKey && Character.isDigit(text.charAt(0))) {
				text = keyPrefixOnNumeric + text;
			}

			if(doIllegalElementNameCharReplacement) {
				Matcher matcher = ILLEGAL_ELEMENT_NAME_PATTERN.matcher(text);
				text = matcher.replaceAll(illegalElementNameCharReplacement);
			}
		}
		return text;
	}

}
