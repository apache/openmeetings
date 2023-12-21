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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <code>ResultHelper</code> is a utility to assert proper namespace handling
 * due to misbehavior of certain implementations with respect to xmlns attributes.
 * The code is copied and slightly modified from jcr-commons
 * <code>SerializingContentHandler</code>
 *
 * @see <a href="https://issues.apache.org/jira/browse/JCR-2897">JCR-2897</a>.
 */
public final class ResultHelper {

    /**
     * logger instance
     */
    private static final Logger log = LoggerFactory.getLogger(ResultHelper.class);

    /** The URI for xml namespaces */
    private static final String XML = "http://www.w3.org/XML/1998/namespace";

    /**
     * The factory used to create serializing SAX transformers.
     */
    private static final SAXTransformerFactory FACTORY =
            // Note that the cast from below is strictly speaking only valid when
            // the factory instance supports the SAXTransformerFactory.FEATURE
            // feature. But since this class would be useless without this feature,
            // it's no problem to fail with a ClassCastException here and prevent
            // this class from even being loaded. AFAIK all common JAXP
            // implementations do support this feature.
            (SAXTransformerFactory) TransformerFactory.newInstance();

    /**
     * Flag that indicates whether we need to work around the issue of
     * the serializer not automatically generating the required xmlns
     * attributes for the namespaces used in the document.
     */
    private static final boolean NEEDS_XMLNS_ATTRIBUTES =
            needsXmlnsAttributes();

    /**
     * Probes the available XML serializer for xmlns support. Used to set
     * the value of the {@link #NEEDS_XMLNS_ATTRIBUTES} flag.
     *
     * @return whether the XML serializer needs explicit xmlns attributes
     */
    private static boolean needsXmlnsAttributes() {
        try {
            StringWriter writer = new StringWriter();
            TransformerHandler probe = FACTORY.newTransformerHandler();
            probe.setResult(new StreamResult(writer));
            probe.startDocument();
            probe.startPrefixMapping("p", "uri");
            probe.startElement("uri", "e", "p:e", new AttributesImpl());
            probe.endElement("uri", "e", "p:e");
            probe.endPrefixMapping("p");
            probe.endDocument();
            return writer.toString().indexOf("xmlns") == -1;
        } catch (Exception e) {
            throw new UnsupportedOperationException("XML serialization fails");
        }
    }

    /**
     * In case the underlying XML library doesn't properly handle xmlns attributes
     * this method creates new content handler dealing with the misbehavior and
     * returns an new instance of SAXResult. Otherwise the passed result
     * is returned back.
     *
     * @param result
     * @return A instance of <code>Result</code> that properly handles xmlns attributes.
     * @throws SAXException
     */
    public static Result getResult(Result result) throws SAXException {
        try {
            TransformerHandler handler = FACTORY.newTransformerHandler();
            handler.setResult(result);

            // Specify the output properties to avoid surprises especially in
            // character encoding or the output method (might be html for some
            // documents!)
            Transformer transformer = handler.getTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            if (NEEDS_XMLNS_ATTRIBUTES) {
                // The serializer does not output xmlns declarations,
                // so we need to do it explicitly with this wrapper
                return new SAXResult(new SerializingContentHandler(handler));
            } else {
                return result;
            }
        } catch (TransformerConfigurationException e) {
            throw new SAXException("Failed to initialize XML serializer", e);
        }
    }

    /**
     * private constructor to avoid instantiation
     */
    private ResultHelper() {
    }

    /**
     * Special content handler fixing issues with xmlns attraibutes handling.
     */
    private static final class SerializingContentHandler extends DefaultHandler {
        /**
         * The prefixes of startPrefixMapping() declarations for the coming element.
         */
        private List prefixList = new ArrayList();

        /**
         * The URIs of startPrefixMapping() declarations for the coming element.
         */
        private List uriList = new ArrayList();

        /**
         * Maps of URI<->prefix mappings. Used to work around a bug in the Xalan
         * serializer.
         */
        private Map uriToPrefixMap = new HashMap();
        private Map prefixToUriMap = new HashMap();

        /**
         * True if there has been some startPrefixMapping() for the coming element.
         */
        private boolean hasMappings = false;

        /**
         * Stack of the prefixes of explicitly generated prefix mapping calls
         * per each element level. An entry is appended at the beginning of each
         * {@link #startElement(String, String, String, org.xml.sax.Attributes)} call and
         * removed at the end of each {@link #endElement(String, String, String)}
         * call. By default the entry for each element is <code>null</code> to
         * avoid losing performance, but whenever the code detects a new prefix
         * mapping that needs to be registered, the <code>null</code> entry is
         * replaced with a list of explicitly registered prefixes for that node.
         * When that element is closed, the listed prefixes get unmapped.
         *
         * @see #checkPrefixMapping(String, String)
         * @see <a href="https://issues.apache.org/jira/browse/JCR-1767">JCR-1767</a>
         */
        private final List addedPrefixMappings = new ArrayList();

        /**
         * The adapted content handler instance.
         */
        private final ContentHandler handler;

        private SerializingContentHandler(ContentHandler handler) {
            this.handler = handler;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            handler.characters(ch, start, length);
        }

        @Override        
        public void startDocument() throws SAXException {
            // Cleanup
            this.uriToPrefixMap.clear();
            this.prefixToUriMap.clear();
            clearMappings();
            
            handler.startDocument();
        }

        /**
         * Track mappings to be able to add <code>xmlns:</code> attributes
         * in <code>startElement()</code>.
         */
        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            // Store the mappings to reconstitute xmlns:attributes
            // except prefixes starting with "xml": these are reserved
            // VG: (uri != null) fixes NPE in startElement
            if (uri != null && !prefix.startsWith("xml")) {
                this.hasMappings = true;
                this.prefixList.add(prefix);
                this.uriList.add(uri);

                // append the prefix colon now, in order to save concatenations later, but
                // only for non-empty prefixes.
                if (prefix.length() > 0) {
                    this.uriToPrefixMap.put(uri, prefix + ":");
                } else {
                    this.uriToPrefixMap.put(uri, prefix);
                }

                this.prefixToUriMap.put(prefix, uri);
            }
            
            handler.startPrefixMapping(prefix, uri);
        }

        /**
         * Checks whether a prefix mapping already exists for the given namespace
         * and generates the required {@link #startPrefixMapping(String, String)}
         * call if the mapping is not found. By default the registered prefix
         * is taken from the given qualified name, but a different prefix is
         * automatically selected if that prefix is already used.
         *
         * @see <a href="https://issues.apache.org/jira/browse/JCR-1767">JCR-1767</a>
         * @param uri namespace URI
         * @param qname element name with the prefix, or <code>null</code>
         * @throws SAXException if the prefix mapping can not be added
         */
        private void checkPrefixMapping(String uri, String qname)
                throws SAXException {
            // Only add the prefix mapping if the URI is not already known
            if (uri != null && uri.length() > 0 && !uri.startsWith("xml")
                    && !uriToPrefixMap.containsKey(uri)) {
                // Get the prefix
                String prefix = "ns";
                if (qname != null && qname.length() > 0) {
                    int colon = qname.indexOf(':');
                    if (colon != -1) {
                        prefix = qname.substring(0, colon);
                    }
                }

                // Make sure that the prefix is unique
                String base = prefix;
                for (int i = 2; prefixToUriMap.containsKey(prefix); i++) {
                    prefix = base + i;
                }

                int last = addedPrefixMappings.size() - 1;
                List prefixes = (List) addedPrefixMappings.get(last);
                if (prefixes == null) {
                    prefixes = new ArrayList();
                    addedPrefixMappings.set(last, prefixes);
                }
                prefixes.add(prefix);

                startPrefixMapping(prefix, uri);
            }
        }

        /**
         * Ensure all namespace declarations are present as <code>xmlns:</code> attributes
         * and add those needed before delegating the startElement method on the
         * specified <code>handler</code>. This is a workaround for a Xalan bug
         * (at least in version 2.0.1) : <code>org.apache.xalan.serialize.SerializerToXML</code>
         * ignores <code>start/endPrefixMapping()</code>.
         */
        @Override
        public void startElement(
                String eltUri, String eltLocalName, String eltQName, Attributes attrs)
                throws SAXException {
            // JCR-1767: Generate extra prefix mapping calls where needed
            addedPrefixMappings.add(null);
            checkPrefixMapping(eltUri, eltQName);
            for (int i = 0; i < attrs.getLength(); i++) {
                checkPrefixMapping(attrs.getURI(i), attrs.getQName(i));
            }

            // try to restore the qName. The map already contains the colon
            if (null != eltUri && eltUri.length() != 0 && this.uriToPrefixMap.containsKey(eltUri)) {
                eltQName = this.uriToPrefixMap.get(eltUri) + eltLocalName;
            }
            if (this.hasMappings) {
                // Add xmlns* attributes where needed

                // New Attributes if we have to add some.
                AttributesImpl newAttrs = null;

                int mappingCount = this.prefixList.size();
                int attrCount = attrs.getLength();

                for (int mapping = 0; mapping < mappingCount; mapping++) {

                    // Build infos for this namespace
                    String uri = (String) this.uriList.get(mapping);
                    String prefix = (String) this.prefixList.get(mapping);
                    String qName = prefix.equals("") ? "xmlns" : ("xmlns:" + prefix);

                    // Search for the corresponding xmlns* attribute
                    boolean found = false;
                    for (int attr = 0; attr < attrCount; attr++) {
                        if (qName.equals(attrs.getQName(attr))) {
                            // Check if mapping and attribute URI match
                            if (!uri.equals(attrs.getValue(attr))) {
                                throw new SAXException("URI in prefix mapping and attribute do not match");
                            }
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // Need to add this namespace
                        if (newAttrs == null) {
                            // Need to test if attrs is empty or we go into an infinite loop...
                            // Well know SAX bug which I spent 3 hours to remind of :-(
                            if (attrCount == 0) {
                                newAttrs = new AttributesImpl();
                            } else {
                                newAttrs = new AttributesImpl(attrs);
                            }
                        }

                        if (prefix.equals("")) {
                            newAttrs.addAttribute(XML, qName, qName, "CDATA", uri);
                        } else {
                            newAttrs.addAttribute(XML, prefix, qName, "CDATA", uri);
                        }
                    }
                } // end for mapping

                // Cleanup for the next element
                clearMappings();

                // Start element with new attributes, if any
                handler.startElement(eltUri, eltLocalName, eltQName, newAttrs == null ? attrs : newAttrs);                
            } else {
                // Normal job
                handler.startElement(eltUri, eltLocalName, eltQName, attrs);
            }
        }


        /**
         * Receive notification of the end of an element.
         * Try to restore the element qName.
         */
        @Override
        public void endElement(String eltUri, String eltLocalName, String eltQName) throws SAXException {
            // try to restore the qName. The map already contains the colon
            if (null != eltUri && eltUri.length() != 0 && this.uriToPrefixMap.containsKey(eltUri)) {
                eltQName = this.uriToPrefixMap.get(eltUri) + eltLocalName;
            }

            handler.endElement(eltUri, eltLocalName, eltQName);
            
            // JCR-1767: Generate extra prefix un-mapping calls where needed
            int last = addedPrefixMappings.size() - 1;
            List prefixes = (List) addedPrefixMappings.remove(last);
            if (prefixes != null) {
                Iterator iterator = prefixes.iterator();
                while (iterator.hasNext()) {
                    endPrefixMapping((String) iterator.next());
                }
            }
        }

        /**
         * End the scope of a prefix-URI mapping:
         * remove entry from mapping tables.
         */
        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            // remove mappings for xalan-bug-workaround.
            // Unfortunately, we're not passed the uri, but the prefix here,
            // so we need to maintain maps in both directions.
            if (this.prefixToUriMap.containsKey(prefix)) {
                this.uriToPrefixMap.remove(this.prefixToUriMap.get(prefix));
                this.prefixToUriMap.remove(prefix);
            }

            if (hasMappings) {
                // most of the time, start/endPrefixMapping calls have an element event between them,
                // which will clear the hasMapping flag and so this code will only be executed in the
                // rather rare occasion when there are start/endPrefixMapping calls with no element
                // event in between. If we wouldn't remove the items from the prefixList and uriList here,
                // the namespace would be incorrectly declared on the next element following the
                // endPrefixMapping call.
                int pos = prefixList.lastIndexOf(prefix);
                if (pos != -1) {
                    prefixList.remove(pos);
                    uriList.remove(pos);
                }
            }

            handler.endPrefixMapping(prefix);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            handler.ignorableWhitespace(ch, start, length);            
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            handler.processingInstruction(target, data);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            handler.setDocumentLocator(locator);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            handler.skippedEntity(name);            
        }

        @Override
        public void endDocument() throws SAXException {
            // Cleanup
            this.uriToPrefixMap.clear();
            this.prefixToUriMap.clear();
            clearMappings();
            
            handler.endDocument();
        }

        private void clearMappings() {
            this.hasMappings = false;
            this.prefixList.clear();
            this.uriList.clear();
        }
    }
}