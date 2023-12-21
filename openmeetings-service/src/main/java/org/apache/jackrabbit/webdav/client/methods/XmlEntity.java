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
package org.apache.jackrabbit.webdav.client.methods;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility methods for creating request entities from {@link Document}s or {@link XmlSerializable}s.
 */
public class XmlEntity {

    private static Logger LOG = LoggerFactory.getLogger(XmlEntity.class);

    private static ContentType CT = ContentType.create("application/xml", "UTF-8");

    public static HttpEntity create(Document doc) throws IOException {
        try {
            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DomUtil.transformDocument(doc, xml);
            return new ByteArrayEntity(xml.toByteArray(), CT);
        } catch (TransformerException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        } catch (SAXException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        }
    }

    public static HttpEntity create(XmlSerializable payload) throws IOException {
        try {
            Document doc = DomUtil.createDocument();
            doc.appendChild(payload.toXml(doc));
            return create(doc);
        } catch (ParserConfigurationException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        }
    }
}
