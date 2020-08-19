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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smooks.SmooksException;
import org.smooks.cartridges.yaml.handler.*;
import org.smooks.cdr.Parameter;
import org.smooks.cdr.SmooksResourceConfiguration;
import org.smooks.container.ExecutionContext;
import org.smooks.xml.SmooksXMLReader;
import org.w3c.dom.Element;
import org.xml.sax.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * YAML to SAX event reader.
 * <p/>
 * This YAML Reader can be plugged into Smooks in order to convert a
 * YAML based message stream into a stream of SAX events to be consumed by the other
 * Smooks resources.
 *
 * <h3>Configuration</h3>
 * <pre>
 * &lt;resource-config selector="org.xml.sax.driver"&gt;
 *  &lt;resource&gt;org.smooks.yaml.YamlReader&lt;/resource&gt;
 *  &lt;!--
 *      (Optional) The element name of the SAX document root. Default of 'yaml'.
 *  --&gt;
 *  &lt;param name="<b>rootName</b>"&gt;<i>&lt;root-name&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The element name of a array element. Default of 'element'.
 *  --&gt;
 *  &lt;param name="<b>arrayElementName</b>"&gt;<i>&lt;array-element-name&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The replacement string for YAML NULL values. Default is an empty string.
 *  --&gt;
 *  &lt;param name="<b>nullValueReplacement</b>"&gt;<i>&lt;null-value-replacement&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The replacement character for whitespaces in a YAML map key. By default this not defined, so that the reader doesn't search for whitespaces.
 *  --&gt;
 *  &lt;param name="<b>keyWhitspaceReplacement</b>"&gt;<i>&lt;key-whitspace-replacement&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The prefix character to add if the YAML node name starts with a number. By default this is not defined, so that the reader doesn't search for element names that start with a number.
 *  --&gt;
 *  &lt;param name="<b>keyPrefixOnNumeric</b>"&gt;<i>&lt;key-prefix-on-numeric&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) If illegal characters are encountered in a YAML element name then they are replaced with this value. By default this is not defined, so that the reader doesn't doesn't search for illegal characters.
 *  --&gt;
 *  &lt;param name="<b>illegalElementNameCharReplacement</b>"&gt;<i>&lt;illegal-element-name-char-replacement&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) Defines a map of keys and there replacement. The from key will be replaced with the to key or the contents of the element.
 *  --&gt;
 *  &lt;param name="<b>keyMap</b>"&gt;
 *   &lt;key from="fromKey" to="toKey" /&gt;
 *   &lt;key from="fromKey"&gt;&lt;to&gt;&lt;/key&gt;
 *  &lt;/param&gt;
 *  &lt;!--
 *      (Optional) The strategy how to handle YAML anchors and aliases. Possible values:
 *          - REFER: Adds a 'id' attribute to the element with the anchor and the 'ref' attribute to the elements with the alias.
 *                   The value of these attributes is the name of the anchor. The reference needs to be handled within the Smooks config.
 *                   The attribute names can be set via the 'anchorAttributeName' and 'aliasAttributeName' properties
 *          - RESOLVE: The elements or value from the anchor are resolved (copied) under the element with the alias.
 *                     Smooks doesn't see that there was a reference.
 *          - REFER_RESOLVE: A combination of REFER and RESOLVE. The element of the anchor are resolved and the attributes are set.
 *                           You should use this if you want to resolve the element but also need the alias name because it has a
 *                           business meaning.
 *
 *  	 Default: 'REFER'
 *  --&gt;
 *  &lt;param name="<b>aliasStrategy</b>"&gt;<i>&lt;alias-strategy&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The name of the anchor attribute when the aliasStrategy is REFER or REFER_RESOLVER. Default of 'id'
 *  --&gt;
 *  &lt;param name="<b>anchorAttributeName</b>"&gt;<i>&lt;anchor-attribute-name&gt;</i>&lt;/param&gt;
 *  &lt;!--
 *      (Optional) The name of the alias attribute when the aliasStrategy is REFER or REFER_RESOLVER. Default of 'ref'
 *  --&gt;
 *  &lt;param name="<b>aliasAttributeName</b>"&gt;<i>&lt;alias-attribute-name&gt;</i>&lt;/param&gt;
 * &lt;/resource-config&gt;
 * </pre>
 *
 * @author <a href="mailto:maurice@zeijen.net">maurice@zeijen.net</a>
 */
public class YamlReader implements SmooksXMLReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(YamlReader.class);

	public static final String CONFIG_PARAM_KEY_MAP = "keyMap";

	public static final String XML_ROOT = "yaml";

	public static final String XML_DOCUMENT = "document";

	public static final String XML_ARRAY_ELEMENT_NAME = "element";

	public static final String DEFAULT_ANCHOR_NAME = "id";

    public static final String DEFAULT_ALIAS_NAME = "ref";

    private ContentHandler contentHandler;

	private ExecutionContext executionContext;

	@Inject
    private String rootName = XML_ROOT;

	@Inject
    private String documentName = XML_DOCUMENT;

	@Inject
    private String arrayElementName = XML_ARRAY_ELEMENT_NAME;

	@Inject
    private Optional<String> keyWhitspaceReplacement;

	@Inject
    private Optional<String> keyPrefixOnNumeric;

	@Inject
    private Optional<String> illegalElementNameCharReplacement;

	@Inject
    private String anchorAttributeName = DEFAULT_ANCHOR_NAME;

	@Inject
    private String aliasAttributeName = DEFAULT_ALIAS_NAME;

    @Inject
    private Boolean indent = false;

    @Inject
    private AliasStrategy aliasStrategy = AliasStrategy.REFER;

    @Inject
    private SmooksResourceConfiguration config;

    private final Yaml yaml = new Yaml();

	private YamlEventStreamHandler yamlEventStreamParser;

    @PostConstruct
    public void initialize() {
    	ElementNameFormatter elementNameFormatter = new ElementNameFormatter(initKeyMap(), keyWhitspaceReplacement.orElse(null), keyPrefixOnNumeric.orElse(null), illegalElementNameCharReplacement.orElse(null));
    	yamlEventStreamParser = new YamlEventStreamHandler(elementNameFormatter, documentName, arrayElementName);
    }
    /*
     * (non-Javadoc)
     * @see org.smooks.xml.SmooksXMLReader#setExecutionContext(org.smooks.container.ExecutionContext)
     */
	public void setExecutionContext(ExecutionContext request) {
		this.executionContext = request;
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	public void parse(InputSource yamlInputSource) throws IOException, SAXException {
        if(contentHandler == null) {
            throw new IllegalStateException("'contentHandler' not set.  Cannot parse YAML stream.");
        }
        if(executionContext == null) {
            throw new IllegalStateException("Smooks container 'executionContext' not set.  Cannot parse YAML stream.");
        }

        try {
			// Get a reader for the YAML source...
	        Reader yamlStreamReader = yamlInputSource.getCharacterStream();
	        if(yamlStreamReader == null) {
	            throw new SmooksException("The InputSource doesn't provide a Reader character stream. Make sure that you supply a reader to the Smooks.filterSource method.");
	        }
	        YamlToSaxHandler yamlToSaxHandler = new YamlToSaxHandler(contentHandler, anchorAttributeName, aliasAttributeName, indent);

	        EventHandler eventHandler;
	        if(aliasStrategy == AliasStrategy.REFER) {
	        	eventHandler = new AliasReferencingEventHandler(yamlToSaxHandler);
	        } else {
	        	eventHandler = new AliasResolvingEventHandler(yamlEventStreamParser, yamlToSaxHandler, aliasStrategy == AliasStrategy.REFER_RESOLVE);
	        }

	        if(LOGGER.isTraceEnabled()) {
	        	LOGGER.trace("Starting YAML parsing");
	        }

	        Iterable<Event> yamlEventStream = yaml.parse(yamlStreamReader);

	        // Start the document and add the root  element...
	        contentHandler.startDocument();

	        yamlToSaxHandler.startElementStructure(rootName, null, false);

	        yamlEventStreamParser.handle(eventHandler, yamlEventStream);

	        yamlToSaxHandler.endElementStructure(rootName);

	        contentHandler.endDocument();

        } finally {
        	contentHandler = null;
        	executionContext = null;
        }
	}



	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> initKeyMap() {
		Parameter<?> keyMapParam = config.getParameter(CONFIG_PARAM_KEY_MAP, Object.class);

       if (keyMapParam != null) {
           Object objValue = keyMapParam.getValue();

           if(objValue instanceof Map<?, ?>) {
               return (HashMap<String, String>) objValue;
           } else {
               Element keyMapParamElement = keyMapParam.getXml();

               if(keyMapParamElement != null) {
                   return KeyMapDigester.digest(keyMapParamElement);
               } else {
            	   throw new SmooksException("Sorry, the key properties must be available as XML DOM. Please configure using XML.");
               }
           }
       }
       return Collections.emptyMap();
	}

	public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

	/**
	 * @return the rootName
	 */
	public String getRootName() {
		return rootName;
	}


	/**
	 * @param rootName the rootName to set
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}


	/**
	 * @return the arrayElementName
	 */
	public String getArrayElementName() {
		return arrayElementName;
	}


	/**
	 * @param arrayElementName the arrayElementName to set
	 */
	public void setArrayElementName(String arrayElementName) {
		this.arrayElementName = arrayElementName;
	}


	/**
	 * @return the keyWhitspaceReplacement
	 */
	public String getKeyWhitspaceReplacement() {
		return keyWhitspaceReplacement.orElse(null);
	}


	/**
	 * @param keyWhitspaceReplacement the keyWhitspaceReplacement to set
	 */
	public void setKeyWhitspaceReplacement(String keyWhitspaceReplacement) {
		this.keyWhitspaceReplacement = Optional.ofNullable(keyWhitspaceReplacement);
	}


	/**
	 * @return the keyPrefixOnNumeric
	 */
	public String getKeyPrefixOnNumeric() {
		return keyPrefixOnNumeric.orElse(null);
	}


	/**
	 * @param keyPrefixOnNumeric the keyPrefixOnNumeric to set
	 */
	public void setKeyPrefixOnNumeric(String keyPrefixOnNumeric) {
		this.keyPrefixOnNumeric = Optional.ofNullable(keyPrefixOnNumeric);
	}


	/**
	 * @return the illegalElementNameCharReplacement
	 */
	public String getIllegalElementNameCharReplacement() {
		return illegalElementNameCharReplacement.orElse(null);
	}


	/**
	 * @param illegalElementNameCharReplacement the illegalElementNameCharReplacement to set
	 */
	public void setIllegalElementNameCharReplacement(
			String illegalElementNameCharReplacement) {
		this.illegalElementNameCharReplacement = Optional.ofNullable(illegalElementNameCharReplacement);
	}

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

	/****************************************************************************
     *
     * The following methods are currently unimplemented...
     *
     ****************************************************************************/

    public void parse(String systemId) throws IOException, SAXException {
        throw new UnsupportedOperationException("Operation not supports by this reader.");
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return false;
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public void setDTDHandler(DTDHandler arg0) {
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public void setEntityResolver(EntityResolver arg0) {
    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void setErrorHandler(ErrorHandler arg0) {
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return null;
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }
}
