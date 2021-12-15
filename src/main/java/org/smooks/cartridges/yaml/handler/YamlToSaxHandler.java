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
package org.smooks.cartridges.yaml.handler;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Converts yaml events into sax events.
 *
 * @author maurice_zeijen
 *
 */
public class YamlToSaxHandler {

	private static final AttributesImpl EMPTY_ATTRIBS = new AttributesImpl();

	private static final String ATTRIBUTE_IDREF = "IDREF";

	private static final String ATTRIBUTE_ID = "ID";

	private final ContentHandler contentHandler;

	private final String anchorAttributeName;

	private final String aliasAttributeName;

	private final boolean indent;

	private int elementLevel = 0;

	private static char[] INDENT = new String("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t").toCharArray();

	public YamlToSaxHandler(ContentHandler contentHandler, String anchorAttributeName, String aliasAttributeName, boolean indent) {
		super();
		this.contentHandler = contentHandler;
		this.anchorAttributeName = anchorAttributeName;
		this.aliasAttributeName = aliasAttributeName;
		this.indent = indent;
	}

	public void startElementStructure(String name, String anchorName, boolean addAnchorAttribute) throws SAXException {

		indent();

		startElement(name, anchorName, addAnchorAttribute);

		elementLevel++;

	}

	public void endElementStructure(String name) throws SAXException {
		elementLevel--;

		indent();

		endElement(name);
	}


	public void addContentElement(String name, String value, String anchorName, boolean addAnchorAttribute) throws SAXException {
		indent();

		startElement(name, anchorName, addAnchorAttribute);

		if (value != null && value.length() > 0) {
			contentHandler.characters(value.toCharArray(), 0, value.length());
		}

		endElement(name);
	}

	private void startElement(String name, String anchorName, boolean addAnchorAttribute) throws SAXException {
		AttributesImpl attributes;
		if (anchorName == null) {
			attributes = EMPTY_ATTRIBS;
		} else {
			attributes = new AttributesImpl();

			String attributeName = addAnchorAttribute ? anchorAttributeName : aliasAttributeName;
			String attributeType = addAnchorAttribute ? ATTRIBUTE_ID : ATTRIBUTE_IDREF;
			if (addAnchorAttribute) {

			}
			attributes.addAttribute(XMLConstants.NULL_NS_URI,
					attributeName, attributeName, attributeType,
					anchorName);
		}
		contentHandler.startElement(XMLConstants.NULL_NS_URI, name, "", attributes);
	}

	private void endElement(String name) throws SAXException {
		contentHandler.endElement(XMLConstants.NULL_NS_URI, name, "");
	}

	private void indent() throws SAXException {
		if (indent) {
			contentHandler.characters(INDENT, 0, elementLevel + 1);
		}
	}

}
