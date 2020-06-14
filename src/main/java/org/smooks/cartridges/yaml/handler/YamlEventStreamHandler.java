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

import org.smooks.cartridges.yaml.ElementNameFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;
import org.yaml.snakeyaml.events.ScalarEvent;

import java.util.Stack;

/**
 * Takes a iterable yaml event stream and handles the events of the stream.
 *
 * @author maurice_zeijen
 */
public class YamlEventStreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(YamlEventStreamHandler.class);

    private static enum Type {
    	MAPPING,
    	SEQUENCE
    }

	private final ElementNameFormatter nameFormatter;

	private final String arrayElementName;

	private String documentName;

	public YamlEventStreamHandler(ElementNameFormatter nameFormatter, String documentName, String arrayElementName) {
		this.nameFormatter = nameFormatter;
		this.arrayElementName = arrayElementName;
		this.documentName = documentName;
	}

	public void handle(EventHandler eventHandler, Iterable<Event> yamlEventStream) throws SAXException {

		Stack<String> elementNameStack = new Stack<String>();
		Stack<Type> typeStack = new Stack<Type>();

		boolean isNextElementName = true;
		boolean outputStructAsElement = false;
		for (Event e : yamlEventStream) {

			if(LOGGER.isTraceEnabled()) {
				LOGGER.trace("Event: " + e);
			}

			if(e.is(ID.DocumentStart)) {
				elementNameStack.push(documentName);

				outputStructAsElement = true;
			} else if(e.is(ID.Scalar)) {
				ScalarEvent es = (ScalarEvent) e;

				if(isNextElementName && !lastTypeIsArray(typeStack)) {
					String name = nameFormatter.format(es.getValue());

					if(LOGGER.isTraceEnabled()) {
		        		LOGGER.trace("Element name: " + name);
		        	}

					elementNameStack.push(name);

					eventHandler.addNameEvent(es, name);

					isNextElementName = false;
				} else {
					String elementName = typeStack.peek() == Type.SEQUENCE ? arrayElementName : elementNameStack.pop();

		    		eventHandler.addValueEvent(es, elementName, es.getValue());

		    		isNextElementName = true;
				}
			} else if(e.is(ID.MappingStart) || e.is(ID.SequenceStart)) {
				CollectionStartEvent cse = (CollectionStartEvent) e;

				if(outputStructAsElement) {
					String elementName = lastTypeIsArray(typeStack) ? arrayElementName : elementNameStack.peek();
					eventHandler.startStructureEvent(cse, elementName);
				}

				typeStack.push(e.is(ID.SequenceStart) ? Type.SEQUENCE : Type.MAPPING);

				outputStructAsElement = true;
				isNextElementName = true;
			} else if(e.is(ID.MappingEnd) || e.is(ID.SequenceEnd)) {
				typeStack.pop();

				boolean typeStackPeekIsArray = lastTypeIsArray(typeStack);

				if(!elementNameStack.empty() && !typeStackPeekIsArray) {
					eventHandler.endStructureEvent(e, elementNameStack.pop());
				}

				if(typeStackPeekIsArray) {
					eventHandler.endStructureEvent(e, arrayElementName);
				}

			} else if(e.is(ID.Alias)) {
				String elementName = lastTypeIsArray(typeStack) ? arrayElementName : elementNameStack.pop();

				eventHandler.addAliasEvent((AliasEvent) e, elementName);

				isNextElementName = true;
			}

		}
	}

    private boolean lastTypeIsArray(Stack<Type> typeStack) {
    	return !typeStack.empty() && typeStack.peek() == Type.SEQUENCE;
    }


}
