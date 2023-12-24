/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.webdav.xml;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Custom {@link DocumentBuilderFactory} extended for use in WebDAV.
 */
public class DavDocumentBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DavDocumentBuilderFactory.class);

    private final DocumentBuilderFactory DEFAULT_FACTORY = createFactory();

    private DocumentBuilderFactory BUILDER_FACTORY = DEFAULT_FACTORY;

    private DocumentBuilderFactory createFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setCoalescing(true);
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            LOG.warn("Secure XML processing is not supported", e);
        } catch (AbstractMethodError e) {
            LOG.warn("Secure XML processing is not supported", e);
        }
        return factory;
    }

    public void setFactory(DocumentBuilderFactory documentBuilderFactory) {
        LOG.debug("DocumentBuilderFactory changed to: " + documentBuilderFactory);
        BUILDER_FACTORY = documentBuilderFactory != null ? documentBuilderFactory : DEFAULT_FACTORY;
    }

    /**
     * An entity resolver that does not allow external entity resolution. See
     * RFC 4918, Section 20.6
     */
    private static final EntityResolver DEFAULT_ENTITY_RESOLVER = new EntityResolver() {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            LOG.debug("Resolution of external entities in XML payload not supported - publicId: " + publicId + ", systemId: "
                    + systemId);
            throw new IOException("This parser does not support resolution of external entities (publicId: " + publicId
                    + ", systemId: " + systemId + ")");
        }
    };

    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder db = BUILDER_FACTORY.newDocumentBuilder();
        if (BUILDER_FACTORY == DEFAULT_FACTORY) {
            // if this is the default factory: set the default entity resolver as well
            db.setEntityResolver(DEFAULT_ENTITY_RESOLVER);
        }
        db.setErrorHandler(new DefaultHandler());
        return db;
    }
}
