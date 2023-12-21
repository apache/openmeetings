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
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>TransactionInfo</code> class encapsulates the information present
 * in the {@link #XML_TRANSACTIONINFO} element that forms the request body of
 * the UNLOCk request for a transaction lock.
 *
 * @see TransactionConstants#XML_TRANSACTIONINFO
 * @see TransactionConstants#XML_TRANSACTION
 */
public class TransactionInfo implements TransactionConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(TransactionInfo.class);

    private final boolean isCommit;

    /**
     * Creates a <code>TransactionInfo</code> object
     *
     * @param isCommit
     */
    public TransactionInfo(boolean isCommit) {
        this.isCommit = isCommit;
    }

    /**
     * Creates a <code>TransactionInfo</code> object from the given 'transactionInfo'
     * element. The 'transactionInfo' must have the following form:
     * <pre>
     *
     *  &lt;!ELEMENT transactioninfo (transactionstatus) &gt;
     *  &lt;!ELEMENT transactionstatus ( commit | rollback ) &gt;
     *  &lt;!ELEMENT commit EMPTY &gt;
     *  &lt;!ELEMENT rollback EMPTY &gt;
     * </pre>
     * @param transactionInfo as present in the UNLOCK request body.
     * @throws IllegalArgumentException if the given transactionInfo element
     * is not valid.
     */
    public TransactionInfo(Element transactionInfo) throws DavException {
        if (transactionInfo == null || !XML_TRANSACTIONINFO.equals(transactionInfo.getLocalName())) {
            log.warn("'transactionInfo' element expected.");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        Element txStatus = DomUtil.getChildElement(transactionInfo, XML_TRANSACTIONSTATUS, NAMESPACE);
        if (txStatus != null) {
            // retrieve status: commit or rollback
            isCommit = DomUtil.hasChildElement(txStatus, XML_COMMIT, NAMESPACE);
        } else {
            log.warn("transactionInfo must contain a single 'transactionstatus' element.");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Returns true, if this info requires a 'commit' action, false otherwise
     * (i.e. 'rollback' is requested).
     *
     * @return true if a 'commit' element was present. false otherwise.
     * @see #XML_COMMIT
     * @see #XML_ROLLBACK
     */
    public boolean isCommit() {
        return isCommit;
    }

    //------------------------------------------< XmlSerializable interface >---
    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element elem = DomUtil.createElement(document, XML_TRANSACTIONINFO, NAMESPACE);
        Element st = DomUtil.addChildElement(elem, XML_TRANSACTIONSTATUS, NAMESPACE);
        String lName = (isCommit) ? XML_COMMIT : XML_ROLLBACK;
        DomUtil.addChildElement(st, lName, NAMESPACE);
        return elem;
    }

}