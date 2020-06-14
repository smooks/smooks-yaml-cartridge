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

import org.xml.sax.SAXException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;

/**
 * Adds a 'id' attribute to the element with the anchor and the 'ref' attribute
 * to the elements with the alias. The value of these attributes is the name of
 * the anchor. The reference needs to be handled within the Smooks config. The
 * attribute names can be set via the 'anchorAttributeName' and
 * 'aliasAttributeName' properties.
 *
 * @author maurice_zeijen
 *
 */
public class AliasReferencingEventHandler implements EventHandler {

	private final YamlToSaxHandler contentHandler;

	public AliasReferencingEventHandler(YamlToSaxHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void addValueEvent(ScalarEvent event, String name, String value) throws SAXException {
		contentHandler.addContentElement(name, value, event.getAnchor(), true);
	}

	public void startStructureEvent(CollectionStartEvent event, String name) throws SAXException {
		contentHandler.startElementStructure(name, event.getAnchor(), true);
	}

	public void endStructureEvent(Event event, String name) throws SAXException {
		contentHandler.endElementStructure(name);
	}

	public void addAliasEvent(AliasEvent event, String name) throws SAXException {
		contentHandler.addContentElement(name, null, event.getAnchor(), false);
	}

	public void addNameEvent(ScalarEvent event, String name) throws SAXException {
		// Nothing to do here because we are not interrested in these events. The
		// names are provided to the methods directly and the event object has no use
		// here
	}
}
