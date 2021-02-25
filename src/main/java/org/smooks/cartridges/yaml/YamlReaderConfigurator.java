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

import org.smooks.api.resource.config.Parameter;
import org.smooks.api.resource.config.ReaderConfigurator;
import org.smooks.api.resource.config.ResourceConfig;
import org.smooks.assertion.AssertArgument;
import org.smooks.engine.resource.config.DefaultParameter;
import org.smooks.engine.resource.config.GenericReaderConfigurator;

import java.util.List;
import java.util.Map;

/**
 * YAML Reader configurator.
 * <p/>
 * Supports programmatic {@link YamlReader} configuration on a {@link org.smooks.Smooks#setReaderConfig(org.smooks.ReaderConfigurator) Smooks} instance.
 *
 * @author <a href="mailto:maurice@zeijen.net">maurice@zeijen.net</a>
 */
public class YamlReaderConfigurator implements ReaderConfigurator {

    private String rootName = YamlReader.XML_ROOT;
    private String documentName = YamlReader.XML_DOCUMENT;
    private String arrayElementName = YamlReader.XML_ARRAY_ELEMENT_NAME;
    private String keyWhitspaceReplacement;
    private String keyPrefixOnNumeric;
    private String illegalElementNameCharReplacement;
    private Map<String, String> keyMap;
    private String targetProfile;
    private AliasStrategy aliasStrategy = AliasStrategy.REFER;
    private String anchorAttributeName = YamlReader.DEFAULT_ANCHOR_NAME;
    private String aliasAttributeName = YamlReader.DEFAULT_ALIAS_NAME;
    private boolean indent = false;

    /**
     * The element name of the document root.
     *
     * Default: yaml
     *
     * @param rootName
     * @return This configurator (for chain calls)
     */
	public YamlReaderConfigurator setRootName(String rootName) {
        AssertArgument.isNotNull(rootName, "rootName");
        this.rootName = rootName;
        return this;
    }

	/**
	 * The element name of the yaml document declaration.
	 *
	 * Default: document
	 *
	 * @param documentName
	 * @return This configurator (for chain calls)
	 */
	public YamlReaderConfigurator setDocumentName(String documentName) {
        AssertArgument.isNotNull(documentName, "documentName");
        this.documentName = documentName;
        return this;
    }

	/**
	 * The element name of a array element.
	 *
	 * Default: element
	 *
	 * @param arrayElementName
	 * @return This configurator (for chain calls)
	 */
    public YamlReaderConfigurator setArrayElementName(String arrayElementName) {
        AssertArgument.isNotNull(arrayElementName, "arrayElementName");
        this.arrayElementName = arrayElementName;
        return this;
    }

	/**
	 * The replacement character for whitespaces in a YAML map key. By default
	 * this not defined, so that the reader doesn't search for whitespaces.
	 *
	 * @param keyWhitspaceReplacement
	 * @return This configurator (for chain calls)
	 */
    public YamlReaderConfigurator setKeyWhitspaceReplacement(String keyWhitspaceReplacement) {
        AssertArgument.isNotNull(keyWhitspaceReplacement, "keyWhitspaceReplacement");
        this.keyWhitspaceReplacement = keyWhitspaceReplacement;
        return this;
    }

	/**
	 * The prefix character to add if the YAML node name starts with a number.
	 * By default this is not defined, so that the reader doesn't search for
	 * element names that start with a number.
	 *
	 * @param keyPrefixOnNumeric
	 * @return This configurator (for chain calls)
	 */
    public YamlReaderConfigurator setKeyPrefixOnNumeric(String keyPrefixOnNumeric) {
        AssertArgument.isNotNull(keyPrefixOnNumeric, "keyPrefixOnNumeric");
        this.keyPrefixOnNumeric = keyPrefixOnNumeric;
        return this;
    }

	/**
	 * If illegal characters are encountered in a YAML element name then they
	 * are replaced with this value. By default this is not defined, so that the
	 * reader doesn't doesn't search for illegal characters.
	 *
	 * @param illegalElementNameCharReplacement
	 * @return This configurator (for chain calls)
	 */
    public YamlReaderConfigurator setIllegalElementNameCharReplacement(String illegalElementNameCharReplacement) {
        AssertArgument.isNotNull(illegalElementNameCharReplacement, "illegalElementNameCharReplacement");
        this.illegalElementNameCharReplacement = illegalElementNameCharReplacement;
        return this;
    }

	/**
	 * Defines a YAML element name mapping.
	 *
	 * @param keyMap
	 * @return This configurator (for chain calls)
	 */
    public YamlReaderConfigurator setKeyMap(Map<String, String> keyMap) {
        AssertArgument.isNotNull(keyMap, "keyMap");
        this.keyMap = keyMap;
        return this;
    }

    /**
     * The profile on which this reader is used
     *
     * @param targetProfile
     * @return This configurator (for chain calls)
     */
    public YamlReaderConfigurator setTargetProfile(String targetProfile) {
        AssertArgument.isNotNullAndNotEmpty(targetProfile, "targetProfile");
        this.targetProfile = targetProfile;
        return this;
    }

    /**
     * The strategy how to handle YAML anchors and aliases.
     *
     * Default: REFER
     *
     * @see AliasStrategy
     * @param aliasStrategy The strategy to use
     * @return This configurator (for chain calls)
     */
    public YamlReaderConfigurator setAliasStrategy(AliasStrategy aliasStrategy) {
    	AssertArgument.isNotNull(aliasStrategy, "aliasStrategy");

		this.aliasStrategy = aliasStrategy;

		return this;
	}

    /**
     * The name of the anchor attribute when the aliasStrategy is REFER or REFER_RESOLVER.
     *
     * Default: id
     *
     * @param anchorAttributeName
     * @return This configurator (for chain calls)
     */
    public YamlReaderConfigurator setAnchorAttributeName(String anchorAttributeName) {
    	AssertArgument.isNotNull(anchorAttributeName, "anchorAttributeName");

    	this.anchorAttributeName = anchorAttributeName;

		return this;
	}

    /**
     * The name of the alias attribute when the aliasStrategy is REFER or REFER_RESOLVER.
     *
     * Default: ref
     *
     * @param aliasAttributeName
     * @return This configurator (for chain calls)
     */
	public YamlReaderConfigurator setAliasAttributeName(String aliasAttributeName) {
		AssertArgument.isNotNull(aliasAttributeName, "aliasAttributeName");

		this.aliasAttributeName = aliasAttributeName;

		return this;
	}

	/**
	 * Add indentation character data to the generated event stream. This simply
	 * makes the generated event stream easier to read in its serialized form.
	 * Useful for testing etc.
	 *
	 * Default: false
	 *
	 * @param indent
	 * @return This configurator (for chain calls)
	 */
	public YamlReaderConfigurator setIndent(boolean indent) {
		this.indent = indent;

		return this;
	}

	/**
	 * Creates the SmooksResourceConfiguration from the defined settings.
	 *
	 * @return The SmooksResourceConfiguration
	 */
	public List<ResourceConfig> toConfig() {
        GenericReaderConfigurator configurator = new GenericReaderConfigurator(YamlReader.class);

        configurator.getParameters().setProperty("aliasStrategy", aliasStrategy.toString());
        configurator.getParameters().setProperty("anchorAttributeName", anchorAttributeName);
        configurator.getParameters().setProperty("aliasAttributeName", aliasAttributeName);
        configurator.getParameters().setProperty("indent", Boolean.toString(indent));
        configurator.getParameters().setProperty("rootName", rootName);
        configurator.getParameters().setProperty("documentName", documentName);
        configurator.getParameters().setProperty("arrayElementName", arrayElementName);
        if(keyWhitspaceReplacement != null) {
            configurator.getParameters().setProperty("keyWhitspaceReplacement", keyWhitspaceReplacement);
        }
        if(keyPrefixOnNumeric != null) {
            configurator.getParameters().setProperty("keyPrefixOnNumeric", keyPrefixOnNumeric);
        }
        if(illegalElementNameCharReplacement != null) {
            configurator.getParameters().setProperty("illegalElementNameCharReplacement", illegalElementNameCharReplacement);
        }
        configurator.setTargetProfile(targetProfile);

        List<ResourceConfig> configList = configurator.toConfig();
        ResourceConfig config = configList.get(0);

        if(keyMap != null) {
            Parameter<Map<String, String>> keyMapParam = new DefaultParameter<>(YamlReader.CONFIG_PARAM_KEY_MAP, keyMap);
            config.setParameter(keyMapParam);
        }

        return configList;
    }
}
