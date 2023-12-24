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

import org.apache.jackrabbit.webdav.DavConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>DomUtil</code> provides some common utility methods related to w3c-DOM.
 */
public class DomUtil {

    private static Logger log = LoggerFactory.getLogger(DomUtil.class);

    /**
     * Constant for <code>DavDocumentBuilderFactory</code> which is used
     * to create and parse DOM documents.
     */
    private static final DavDocumentBuilderFactory BUILDER_FACTORY = new DavDocumentBuilderFactory();

    /**
     * Support the replacement of {@link #BUILDER_FACTORY}. This is useful
     * for injecting a customized BuilderFactory, for example with one that
     * uses a local catalog resolver. This is one technique for addressing
     * this issue:
     * http://www.w3.org/blog/systeam/2008/02/08/w3c_s_excessive_dtd_traffic
     *
     * @param documentBuilderFactory
     */
    public static void setBuilderFactory(
            DocumentBuilderFactory documentBuilderFactory) {
        BUILDER_FACTORY.setFactory(documentBuilderFactory);
    }

    /**
     * Transformer factory
     */
    private static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    /**
     * Creates and returns a new empty DOM document.
     *
     * @return new DOM document
     * @throws ParserConfigurationException if the document can not be created
     */
    public static Document createDocument()
            throws ParserConfigurationException {
        return BUILDER_FACTORY.newDocumentBuilder().newDocument();
    }

    /**
     * Parses the given input stream and returns the resulting DOM document.
     *
     * @param stream XML input stream
     * @return parsed DOM document
     * @throws ParserConfigurationException if the document can not be created
     * @throws SAXException if the document can not be parsed
     * @throws IOException if the input stream can not be read
     */
    public static Document parseDocument(InputStream stream)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder docBuilder = BUILDER_FACTORY.newDocumentBuilder();
        return docBuilder.parse(stream);
    }

    /**
     * Returns the value of the named attribute of the current element.
     *
     * @param parent
     * @param localName attribute local name or 'nodeName' if no namespace is
     * specified.
     * @param  namespace or <code>null</code>
     * @return attribute value, or <code>null</code> if not found
     */
    public static String getAttribute(Element parent, String localName, Namespace namespace) {
        if (parent == null) {
            return null;
        }
        Attr attribute;
        if (namespace == null) {
            attribute = parent.getAttributeNode(localName);
        } else {
            attribute = parent.getAttributeNodeNS(namespace.getURI(), localName);
        }
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    /**
     * Returns the namespace attributes of the given element.
     *
     * @param element
     * @return the namespace attributes.
     */
    public static Attr[] getNamespaceAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        List<Attr> nsAttr = new ArrayList<Attr>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            if (Namespace.XMLNS_NAMESPACE.getURI().equals(attr.getNamespaceURI())) {
                nsAttr.add(attr);
            }
        }
        return nsAttr.toArray(new Attr[nsAttr.size()]);
    }

    /**
     * Concatenates the values of all child nodes of type 'Text' or 'CDATA'/
     *
     * @param element
     * @return String representing the value of all Text and CDATA child nodes or
     * <code>null</code> if the length of the resulting String is 0.
     * @see #isText(Node)
     */
    public static String getText(Element element) {
        StringBuffer content = new StringBuffer();
        if (element != null) {
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node child = nodes.item(i);
                if (isText(child)) {
                    // cast to super class that contains Text and CData
                    content.append(((CharacterData) child).getData());
                }
            }
        }
        return (content.length()==0) ? null : content.toString();
    }

    /**
     * Same as {@link #getText(Element)} except that 'defaultValue' is returned
     * instead of <code>null</code>, if the element does not contain any text.
     *
     * @param element
     * @param defaultValue
     * @return the text contained in the specified element or
     * <code>defaultValue</code> if the element does not contain any text.
     */
    public static String getText(Element element, String defaultValue) {
        String txt = getText(element);
        return (txt == null) ? defaultValue : txt;
    }

    /**
     * Removes leading and trailing whitespace after calling {@link #getText(Element)}.
     *
     * @param element
     * @return Trimmed text or <code>null</code>
     */
    public static String getTextTrim(Element element) {
        String txt = getText(element);
        return (txt == null) ? txt : txt.trim();
    }

    /**
     * Calls {@link #getText(Element)} on the first child element that matches
     * the given local name and namespace.
     *
     * @param parent
     * @param childLocalName
     * @param childNamespace
     * @return text contained in the first child that matches the given local name
     * and namespace or <code>null</code>.
     * @see #getText(Element)
     */
    public static String getChildText(Element parent, String childLocalName, Namespace childNamespace) {
        Element child = getChildElement(parent, childLocalName, childNamespace);
        return (child == null) ? null : getText(child);
    }

    /**
     * Calls {@link #getTextTrim(Element)} on the first child element that matches
     * the given local name and namespace.
     *
     * @param parent
     * @param childLocalName
     * @param childNamespace
     * @return text contained in the first child that matches the given local name
     * and namespace or <code>null</code>. Note, that leading and trailing whitespace
     * is removed from the text.
     * @see #getTextTrim(Element)
     */
    public static String getChildTextTrim(Element parent, String childLocalName, Namespace childNamespace) {
        Element child = getChildElement(parent, childLocalName, childNamespace);
        return (child == null) ? null : getTextTrim(child);
    }

    /**
     * Calls {@link #getTextTrim(Element)} on the first child element that matches
     * the given name.
     *
     * @param parent
     * @param childName
     * @return text contained in the first child that matches the given name
     * or <code>null</code>. Note, that leading and trailing whitespace
     * is removed from the text.
     * @see #getTextTrim(Element)
     */
    public static String getChildTextTrim(Element parent, QName childName) {
        Element child = getChildElement(parent, childName);
        return (child == null) ? null : getTextTrim(child);
    }

    /**
     * Returns true if the given parent node has a child element that matches
     * the specified local name and namespace.
     *
     * @param parent
     * @param childLocalName
     * @param childNamespace
     * @return returns true if  a child element exists that matches the specified
     * local name and namespace.
     */
    public static boolean hasChildElement(Node parent, String childLocalName, Namespace childNamespace) {
        return getChildElement(parent, childLocalName, childNamespace) != null;
    }

    /**
     * Returns the first child element that matches the given local name and
     * namespace. If no child element is present or no child element matches,
     * <code>null</code> is returned.
     *
     * @param parent
     * @param childLocalName
     * @param childNamespace
     * @return first child element matching the specified names or <code>null</code>.
     */
    public static Element getChildElement(Node parent, String childLocalName, Namespace childNamespace) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (isElement(child) && matches(child, childLocalName, childNamespace)) {
                    return (Element)child;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first child element that matches the given {@link QName}.
     * If no child element is present or no child element matches,
     * <code>null</code> is returned.
     *
     * @param parent
     * @param childName
     * @return first child element matching the specified name or <code>null</code>.
     */
    public static Element getChildElement(Node parent, QName childName) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (isElement(child) && matches(child, childName)) {
                    return (Element)child;
                }
            }
        }
        return null;
    }

    /**
     * Returns a <code>ElementIterator</code> containing all child elements of
     * the given parent node that match the given local name and namespace.
     * If the namespace is <code>null</code> only the localName is compared.
     *
     * @param parent the node the children elements should be retrieved from
     * @param childLocalName
     * @param childNamespace
     * @return an <code>ElementIterator</code> giving access to all child elements
     * that match the specified localName and namespace.
     */
    public static ElementIterator getChildren(Element parent, String childLocalName, Namespace childNamespace) {
        return new ElementIterator(parent, childLocalName, childNamespace);
    }

    /**
     * Returns a <code>ElementIterator</code> containing all child elements of
     * the given parent node that match the given {@link QName}.
     * 
     * @param parent
     *            the node the children elements should be retrieved from
     * @param childName
     * @return an <code>ElementIterator</code> giving access to all child
     *         elements that match the specified name.
     */
    public static ElementIterator getChildren(Element parent, QName childName) {
        return new ElementIterator(parent, childName);
    }

    /**
     * Return an <code>ElementIterator</code> over all child elements.
     *
     * @param parent
     * @return
     * @see #getChildren(Element, String, Namespace) for a method that only
     * retrieves child elements that match a specific local name and namespace.
     */
    public static ElementIterator getChildren(Element parent) {
        return new ElementIterator(parent);
    }

    /**
     * Return the first child element
     *
     * @return the first child element or <code>null</code> if the given node has no
     * child elements.
     */
    public static Element getFirstChildElement(Node parent) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (isElement(child)) {
                    return (Element)child;
                }
            }
        }
        return null;
    }

    /**
     * Return <code>true</code> if the given parent contains any child that is
     * either an Element, Text or CDATA.
     *
     * @param parent
     * @return <code>true</code> if the given parent contains any child that is
     * either an Element, Text or CDATA.
     */
    public static boolean hasContent(Node parent) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (isAcceptedNode(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return a list of all child nodes that are either Element, Text or CDATA.
     *
     * @param parent
     * @return a list of all child nodes that are either Element, Text or CDATA.
     */
    public static List<Node> getContent(Node parent) {
        List<Node> content = new ArrayList<Node>();
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (isAcceptedNode(child)) {
                    content.add(child);
                }
            }
        }
        return content;
    }

    /**
     * Build a Namespace from the prefix and uri retrieved from the given element.
     *
     * @return the <code>Namespace</code> of the given element.
     */
    public static Namespace getNamespace(Element element) {
        String uri = element.getNamespaceURI();
        String prefix = element.getPrefix();
        if (uri == null) {
            return Namespace.EMPTY_NAMESPACE;
        } else {
            return Namespace.getNamespace(prefix, uri);
        }
    }

    /**
     * Returns true if the specified node matches the required names. Note, that
     * that tests return true if the required name is <code>null</code>.
     *
     * @param node
     * @param requiredLocalName
     * @param requiredNamespace
     * @return true if local name and namespace match the corresponding properties
     * of the given DOM node.
     */
    public static boolean matches(Node node, String requiredLocalName, Namespace requiredNamespace) {
        if (node == null) {
            return false;
        }
        boolean matchingNamespace = matchingNamespace(node, requiredNamespace);
        return matchingNamespace && matchingLocalName(node, requiredLocalName);
    }

    /**
     * Returns true if the specified node matches the required {@link QName}.
     *
     * @param node
     * @param requiredName
     * @return true if local name and namespace match the corresponding properties
     * of the given DOM node.
     */
    public static boolean matches(Node node, QName requiredName) {
        if (node == null) {
            return false;
        } else {
            String nodens = node.getNamespaceURI() != null ? node.getNamespaceURI() : "";
            return nodens.equals(requiredName.getNamespaceURI())
                    && node.getLocalName().equals(requiredName.getLocalPart());
        }
    }

    /**
     * @param node
     * @param requiredNamespace
     * @return true if the required namespace is <code>null</code> or matches
     * the namespace of the specified node.
     */
    private static boolean matchingNamespace(Node node, Namespace requiredNamespace) {
        if (requiredNamespace == null) {
            return true;
        } else {
            return requiredNamespace.isSame(node.getNamespaceURI());
        }
    }

    /**
     * @param node
     * @param requiredLocalName
     * @return true if the required local name is <code>null</code> or if the
     * nodes local name matches.
     */
    private static boolean matchingLocalName(Node node, String requiredLocalName) {
        if (requiredLocalName == null) {
            return true;
        } else {
            String localName = node.getLocalName();
            return requiredLocalName.equals(localName);
        }
    }

    /**
     * @param node
     * @return true if the specified node is either an element or Text or CDATA
     */
    private static boolean isAcceptedNode(Node node) {
        return isElement(node) || isText(node);
    }

    /**
     * @param node
     * @return true if the given node is of type element.
     */
    static boolean isElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * @param node
     * @return true if the given node is of type text or CDATA.
     */
    static boolean isText(Node node) {
        int ntype = node.getNodeType();
        return ntype == Node.TEXT_NODE || ntype == Node.CDATA_SECTION_NODE;
    }

    //----------------------------------------------------< factory methods >---
    /**
     * Create a new DOM element with the specified local name and namespace.
     *
     * @param factory
     * @param localName
     * @param namespace
     * @return a new DOM element
     * @see Document#createElement(String)
     * @see Document#createElementNS(String, String)
     */
    public static Element createElement(Document factory, String localName, Namespace namespace) {
        if (namespace != null) {
            return factory.createElementNS(namespace.getURI(), getPrefixedName(localName, namespace));
        } else {
            return factory.createElement(localName);
        }
    }

    /**
     * Create a new DOM element with the specified local name and namespace.
     *
     * @param factory
     * @param elementName
     * @return a new DOM element
     * @see Document#createElement(String)
     * @see Document#createElementNS(String, String)
     */
    public static Element createElement(Document factory, QName elementName) {
        return factory.createElementNS(elementName.getNamespaceURI(), getPrefixedName(elementName));
    }

    /**
     * Create a new DOM element with the specified local name and namespace and
     * add the specified text as Text node to it.
     *
     * @param factory
     * @param localName
     * @param namespace
     * @param text
     * @return a new DOM element
     * @see Document#createElement(String)
     * @see Document#createElementNS(String, String)
     * @see Document#createTextNode(String)
     * @see Node#appendChild(org.w3c.dom.Node)
     */
    public static Element createElement(Document factory, String localName, Namespace namespace, String text) {
        Element elem = createElement(factory, localName, namespace);
        setText(elem, text);
        return elem;
    }

    /**
     * Add a new child element with the given local name and namespace to the
     * specified parent.
     *
     * @param parent
     * @param localName
     * @param namespace
     * @return the new element that was attached to the given parent.
     */
    public static Element addChildElement(Element parent, String localName, Namespace namespace) {
        Element elem = createElement(parent.getOwnerDocument(), localName, namespace);
        parent.appendChild(elem);
        return elem;
    }

    /**
     * Add a new child element with the given local name and namespace to the
     * specified parent.
     *
     * @param parent
     * @param localName
     * @param namespace
     * @return the new element that was attached to the given parent.
     */
    public static Element addChildElement(Node parent, String localName, Namespace namespace) {
        Document doc = parent.getOwnerDocument();
        if (parent instanceof Document) {
            doc = (Document) parent;
        }
        Element elem = createElement(doc, localName, namespace);
        parent.appendChild(elem);
        return elem;
    }

    /**
     * Add a new child element with the given local name and namespace to the
     * specified parent. The specified text is added as Text node to the created
     * child element.
     *
     * @param parent
     * @param localName
     * @param namespace
     * @param text
     * @return child element that was added to the specified parent
     * @see Document#createElement(String)
     * @see Document#createElementNS(String, String)
     * @see Document#createTextNode(String)
     * @see Node#appendChild(org.w3c.dom.Node)
     */
    public static Element addChildElement(Element parent, String localName, Namespace namespace, String text) {
        Element elem = createElement(parent.getOwnerDocument(), localName, namespace, text);
        parent.appendChild(elem);
        return elem;
    }

    /**
     * Create a new text node and add it as child to the given element.
     *
     * @param element
     * @param text
     */
    public static void setText(Element element, String text) {
        if (text == null || "".equals(text)) {
            // ignore null/empty string text
            return;
        }
        Text txt = element.getOwnerDocument().createTextNode(text);
        element.appendChild(txt);
    }

    /**
     * Add an attribute node to the given element.
     *
     * @param element
     * @param attrLocalName
     * @param attrNamespace
     * @param attrValue
     */
    public static void setAttribute(Element element, String attrLocalName, Namespace attrNamespace, String attrValue) {
        if (attrNamespace == null) {
            Attr attr = element.getOwnerDocument().createAttribute(attrLocalName);
            attr.setValue(attrValue);
            element.setAttributeNode(attr);
        } else {
            Attr attr = element.getOwnerDocument().createAttributeNS(attrNamespace.getURI(), getPrefixedName(attrLocalName, attrNamespace));
            attr.setValue(attrValue);
            element.setAttributeNodeNS(attr);
        }
    }

    /**
     * Adds a namespace attribute on the given element.
     *
     * @param element
     * @param prefix
     * @param uri
     */
    public static void setNamespaceAttribute(Element element, String prefix, String uri) {
        if (Namespace.EMPTY_NAMESPACE.equals(Namespace.getNamespace(prefix, uri))) {
            /**
             * don't try to set the empty namespace which will fail
             * see {@link org.w3c.dom.DOMException#NAMESPACE_ERR}
             * TODO: correct?
             */
            log.debug("Empty namespace -> omit attribute setting.");
            return;
        }
        setAttribute(element, prefix, Namespace.XMLNS_NAMESPACE, uri);
    }

    /**
     * Converts the given timeout (long value defining the number of milli-
     * second until timeout is reached) to its Xml representation as defined
     * by RFC 4918.<br>
     *
     * @param timeout number of milli-seconds until timeout is reached.
     * @return 'timeout' Xml element
     */
    public static Element timeoutToXml(long timeout, Document factory) {
        boolean infinite = timeout / 1000 > Integer.MAX_VALUE || timeout == DavConstants.INFINITE_TIMEOUT;
        String expString = infinite ? DavConstants.TIMEOUT_INFINITE : "Second-" + timeout / 1000;
        return createElement(factory, DavConstants.XML_TIMEOUT, DavConstants.NAMESPACE, expString);
    }

    /**
     * Returns the Xml representation of a boolean isDeep, where false
     * presents a depth value of '0', true a depth value of 'infinity'.
     *
     * @param isDeep
     * @return Xml representation
     */
    public static Element depthToXml(boolean isDeep, Document factory) {
        return depthToXml(isDeep? "infinity" : "0", factory);
    }

    /**
     * Returns the Xml representation of a depth String. Webdav defines the
     * following valid values for depths: 0, 1, infinity
     *
     * @param depth
     * @return 'deep' XML element
     */
    public static Element depthToXml(String depth, Document factory) {
        return createElement(factory, DavConstants.XML_DEPTH, DavConstants.NAMESPACE, depth);
    }

    /**
     * Builds a 'DAV:href' Xml element from the given href.
     * <p>
     * Note that the path present needs to be a valid URI or URI reference.
     *
     * @param href String representing the text of the 'href' Xml element
     * @param factory the Document used as factory
     * @return Xml representation of a 'href' according to RFC 2518.
     */
    public static Element hrefToXml(String href, Document factory) {
        return createElement(factory, DavConstants.XML_HREF, DavConstants.NAMESPACE, href);
    }

    /**
     * Same as {@link #getExpandedName(String, Namespace)}.
     *
     * @param localName
     * @param namespace
     * @return the expanded name of a DOM node consisting of "{" + namespace uri + "}"
     * + localName. If the specified namespace is <code>null</code> or represents
     * the empty namespace, the local name is returned.
     * @deprecated As of 2.0. Please use {@link #getExpandedName(String, Namespace)}
     * instead. This method was named according to usage of 'qualified name' in
     * JSR 170 that conflicts with the terminology used in XMLNS. As of JCR 2.0
     * the String consisting of <code>"{" + namespace uri + "}" + localName</code>
     * is called <code>Expanded Name</code>.
     *
     */
    public static String getQualifiedName(String localName, Namespace namespace) {
        return getExpandedName(localName, namespace);
    }

    /**
     * Returns a string representation of the name of a DOM node consisting
     * of "{" + namespace uri + "}" + localName. If the specified namespace is
     * <code>null</code> or represents the empty namespace, the local name is
     * returned.
     *
     * @param localName
     * @param namespace
     * @return String representation of the name of a DOM node consisting of "{" + namespace uri + "}"
     * + localName. If the specified namespace is <code>null</code> or represents
     * the empty namespace, the local name is returned.
     * @since 2.0 Replaces the deprecated method {@link #getQualifiedName(String, Namespace)}.
     */
    public static String getExpandedName(String localName, Namespace namespace) {
        if (namespace == null || namespace.equals(Namespace.EMPTY_NAMESPACE)) {
            return localName;
        }
        StringBuffer b = new StringBuffer("{");
        b.append(namespace.getURI()).append("}");
        b.append(localName);
        return b.toString();
    }

    /**
     * Return the qualified name of a DOM node consisting of
     * namespace prefix + ":" + local name. If the specified namespace is <code>null</code>
     * or contains an empty prefix, the local name is returned.<br>
     * NOTE, that this is the value to be used for the 'qualified Name' parameter
     * expected with the namespace sensitive factory methods.
     *
     * @param localName
     * @param namespace
     * @return qualified name consisting of prefix, ':' and local name.
     * @see Document#createAttributeNS(String, String)
     * @see Document#createElementNS(String, String)
     */
    public static String getPrefixedName(String localName, Namespace namespace) {
        return getPrefixName(namespace.getURI(), namespace.getPrefix(), localName);
    }

    /**
     * Return the qualified name of a DOM node consisting of
     * namespace prefix + ":" + local name. If the specified namespace is <code>null</code>
     * or contains an empty prefix, the local name is returned.<br>
     * NOTE, that this is the value to be used for the 'qualified Name' parameter
     * expected with the namespace sensitive factory methods.
     *
     * @param name
     * @return qualified name consisting of prefix, ':' and local name.
     * @see Document#createAttributeNS(String, String)
     * @see Document#createElementNS(String, String)
     */
    public static String getPrefixedName(QName name) {
        return getPrefixName(name.getNamespaceURI(), name.getPrefix(), name.getLocalPart());
    }

    private static String getPrefixName(String namespaceURI, String prefix, String localName) {
        if (namespaceURI == null || prefix == null || "".equals(namespaceURI) || "".equals(prefix)) {
            return localName;
        } else {
            StringBuffer buf = new StringBuffer(prefix);
            buf.append(":");
            buf.append(localName);
            return buf.toString();
        }
    }

    /**
     * Uses a new Transformer instance to transform the specified xml document
     * to the specified writer output target.
     *
     * @param xmlDoc XML document to create the transformation
     * <code>Source</code> for.
     * @param writer The writer used to create a new transformation
     * <code>Result </code>for.
     * @throws TransformerException
     */
    public static void transformDocument(Document xmlDoc, Writer writer) throws TransformerException, SAXException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.transform(new DOMSource(xmlDoc), ResultHelper.getResult(new StreamResult(writer)));
    }

    /**
     * Uses a new Transformer instance to transform the specified xml document
     * to the specified writer output target.
     *
     * @param xmlDoc XML document to create the transformation
     * <code>Source</code> for.
     * @param out The stream used to create a new transformation
     * <code>Result </code>for.
     * @throws TransformerException
     */
    public static void transformDocument(Document xmlDoc, OutputStream out) throws TransformerException, SAXException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.transform(new DOMSource(xmlDoc), ResultHelper.getResult(new StreamResult(out)));
    }
}
