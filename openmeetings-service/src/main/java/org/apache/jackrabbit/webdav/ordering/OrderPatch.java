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
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>OrderPatch</code> represents the mandatory request body of an
 * ORDERPATCH request. RFC 3648 defines the following structure for it:<br>
 * <pre>
 * &lt;!ELEMENT orderpatch (ordering-type?, order-member*) &gt;
 * &lt;!ELEMENT order-member (segment, position) &gt;
 * &lt;!ELEMENT position (first | last | before | after) &gt;
 * &lt;!ELEMENT segment (#PCDATA) &gt;
 * &lt;!ELEMENT first EMPTY &gt;
 * &lt;!ELEMENT last EMPTY &gt;
 * &lt;!ELEMENT before segment &gt;
 * &lt;!ELEMENT after segment &gt;
 * </pre>
 */
public class OrderPatch implements OrderingConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(OrderPatch.class);

    private Member[] instructions;
    private String orderingType;

    /**
     * Create a new <code>OrderPath</code> object.
     *
     * @param orderingType
     * @param instruction
     */
    public OrderPatch(String orderingType, Member instruction) {
        this(orderingType, new Member[] {instruction});
    }

    /**
     * Create a new <code>OrderPath</code> object.
     *
     * @param orderingType
     * @param instructions
     */
    public OrderPatch(String orderingType, Member[] instructions) {
        if (orderingType == null || instructions == null) {
            throw new IllegalArgumentException("ordering type and instructions cannot be null.");
        }
        this.orderingType = orderingType;
        this.instructions = instructions;
    }

    /**
     * Return the ordering type.
     *
     * @return ordering type
     */
    public String getOrderingType() {
        return orderingType;
    }

    /**
     * Return an array of {@link Member} objects defining the re-ordering
     * instructions to be applied to the requested resource.
     *
     * @return ordering instructions.
     */
    public Member[] getOrderInstructions() {
        return instructions;
    }

    //------------------------------------------< XmlSerializable interface >---
    /**
     *
     * @return
     * @param document
     */
    public Element toXml(Document document) {
        Element orderPatch = DomUtil.createElement(document, XML_ORDERPATCH, NAMESPACE);
        // add DAV:ordering-type below DAV:orderpatch
        Element otype = DomUtil.addChildElement(orderPatch, XML_ORDERING_TYPE, NAMESPACE);
        otype.appendChild(DomUtil.hrefToXml(orderingType, document));
        // add DAV:member elements below DAV:orderpatch
        for (Member instruction : instructions) {
            orderPatch.appendChild(instruction.toXml(document));
        }
        return orderPatch;
    }

    //------------------------------------------------------< static method >---
    /**
     * Create a new <code>OrderPath</code> object.
     *
     * @param orderPatchElement
     * @throws IllegalArgumentException if the specified Xml element was not valid.
     */
    public static OrderPatch createFromXml(Element orderPatchElement) throws DavException {
        if (!DomUtil.matches(orderPatchElement, XML_ORDERPATCH, NAMESPACE)) {
            log.warn("ORDERPATH request body must start with an 'orderpatch' element.");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }

        // retrieve the href of the orderingtype element
        String orderingType;
        Element otype = DomUtil.getChildElement(orderPatchElement, XML_ORDERING_TYPE, NAMESPACE);
        if (otype != null) {
            orderingType = DomUtil.getChildText(otype, DavConstants.XML_HREF, DavConstants.NAMESPACE);
        } else {
            log.warn("ORDERPATH request body must contain an 'ordering-type' child element.");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }

        // set build the list of ordering instructions
        List<Member> tmpList = new ArrayList<Member>();
        ElementIterator it = DomUtil.getChildren(orderPatchElement, XML_ORDER_MEMBER, NAMESPACE);
        while (it.hasNext()) {
            Element el = it.nextElement();
            try {
                // retrieve text 'DAV:segment' child of this DAV:order-member element
                String segment = DomUtil.getChildText(el, XML_SEGMENT, NAMESPACE);
                // retrieve the 'DAV:position' child element
                Position pos = Position.createFromXml(DomUtil.getChildElement(el, XML_POSITION, NAMESPACE));
                Member om = new Member(segment, pos);
                tmpList.add(om);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid element in 'orderpatch' request body: " + e.getMessage());
                throw new DavException(DavServletResponse.SC_BAD_REQUEST);
            }
        }
        Member[] instructions = tmpList.toArray(new Member[tmpList.size()]);
        return new OrderPatch(orderingType, instructions);
    }

    //--------------------------------------------------------------------------
    /**
     * Internal class <code>Member</code> represents the 'Order-Member' children
     * elements of an 'OrderPatch' request body present in the ORDERPATCH request.
     */
    public static class Member implements XmlSerializable {

        private String memberHandle;
        private Position position;

        /**
         * Create a new <code>Member</code> object.
         *
         * @param memberHandle
         * @param position
         */
        public Member(String memberHandle, Position position) {
            this.memberHandle = memberHandle;
            this.position = position;
        }

        /**
         * Return the handle of the internal member to be reordered.
         *
         * @return handle of the internal member.
         */
        public String getMemberHandle() {
            return memberHandle;
        }

        /**
         * Return the position where the internal member identified by the
         * member handle should be placed.
         *
         * @return position for the member after the request.
         * @see #getMemberHandle()
         */
        public Position getPosition() {
            return position;
        }

        //--------------------------------------< XmlSerializable interface >---
        /**
         * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
         */
        public Element toXml(Document document) {
            Element memberElem = DomUtil.createElement(document, XML_ORDER_MEMBER, NAMESPACE);
            DomUtil.addChildElement(memberElem, XML_SEGMENT, NAMESPACE, memberHandle);
            memberElem.appendChild(position.toXml(document));
            return memberElem;
        }

    }
}
