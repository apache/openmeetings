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

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>TransactionConstants</code> interface provide constants for request
 * and response headers, Xml elements and property names used for handling
 * transactions over WebDAV. There exists no public standard for this functionality.
 *
 * todo: 'local' and 'global' are not accurate terms in the given context &gt; replace
 */
public interface TransactionConstants {

    /**
     * Namespace for transaction related xml elements
     */
    public static final Namespace NAMESPACE = Namespace.getNamespace("dcr", "http://www.day.com/jcr/webdav/1.0");

    //---< Headers >------------------------------------------------------------
    /**
     * TransactionId Header
     */
    public static final String HEADER_TRANSACTIONID = "TransactionId";

    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * transaction XML element<br>
     * Used as element inside the {@link DavConstants#XML_LOCKTYPE locktype}
     * element.
     * @see DavConstants#XML_LOCKTYPE
     */
    public static final String XML_TRANSACTION = "transaction";

    /**
     * global XML element<br>
     * Used as element inside of the {@link DavConstants#XML_LOCKSCOPE lockscope} element.
     *
     * @see DavConstants#XML_LOCKSCOPE
     */
    public static final String XML_GLOBAL = "global";

    /**
     * local XML element<br>
     * Used as element inside of the {@link DavConstants#XML_LOCKSCOPE lockscope} element.
     * It indicates the transaction to be local (e.g. transient changes to
     * a repository).
     *
     * @see DavConstants#XML_LOCKSCOPE
     */
    public static final String XML_LOCAL = "local";

    /**
     * transactioninfo XML element<br>
     * Mandatory element of the UNLOCK request body, if the unlock request
     * is intended to complete a transaction.
     */
    public static final String XML_TRANSACTIONINFO = "transactioninfo";

    /**
     * transactionstatus XML element<br>
     * Mandatory element inside the {@link #XML_TRANSACTIONINFO transactioninfo}
     * element indicating how the transaction should be completed.
     * @see #XML_TRANSACTIONINFO
     */
    public static final String XML_TRANSACTIONSTATUS = "transactionstatus";

    /**
     * commit XML element<br>
     * Used as element inside of the {@link #XML_TRANSACTIONSTATUS transactionstatus}
     * element. It indicates a completion by committing the transaction.
     * @see #XML_TRANSACTIONSTATUS
     */
    public static final String XML_COMMIT = "commit";

    /**
     * rollback XML element<br>
     * Used as element inside of the {@link #XML_TRANSACTIONSTATUS transactionstatus}
     * element. It indicates a completion by roll backing the transaction.
     * @see #XML_TRANSACTIONSTATUS
     */
    public static final String XML_ROLLBACK = "rollback";

    //---< Lock Type, Lock Scope >----------------------------------------------
    /**
     * "transaction" lock type constant.
     * @see #XML_TRANSACTION
     * @see Type#create(String, Namespace)
     */
    public static final Type TRANSACTION = Type.create(XML_TRANSACTION, TransactionConstants.NAMESPACE);

    /**
     * "local" lock scope constant.
     *
     * @see #XML_LOCAL
     * @see Scope#create(String, Namespace)
     */
    public static final Scope LOCAL = Scope.create(XML_LOCAL, TransactionConstants.NAMESPACE);

    /**
     * "global" lock scope constant.
     *
     * @see #XML_GLOBAL
     * @see Scope#create(String, Namespace)
     */
    public static final Scope GLOBAL = Scope.create(XML_GLOBAL, TransactionConstants.NAMESPACE);
}