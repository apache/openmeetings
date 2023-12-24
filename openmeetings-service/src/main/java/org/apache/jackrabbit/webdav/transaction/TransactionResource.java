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
import org.apache.jackrabbit.webdav.DavResource;

/**
 * <code>TransactionResource</code> extends the {@link DavResource} interface by
 * transaction relevant METHODS.
 */
public interface TransactionResource extends DavResource {

    public static final String METHODS = "";

    /**
     * Initializes the <code>TransactionResource</code>.
     *
     * @param txMgr
     * @param transactionId
     */
    public void init(TxLockManager txMgr, String transactionId);

    /**
     * The TransactionId or <code>null</code> according to the value of the
     * corresponding request {@link TransactionConstants#HEADER_TRANSACTIONID header}
     * field.
     *
     * @return TransactionId header or <code>null</code>
     */
    public String getTransactionId();

    /**
     * Overloads the {@link DavResource#unlock unlock} method of the <code>DavResource</code>
     * interface.
     *
     * @param lockToken lock token as present in the request header.
     * @param info transaction info object as present in the UNLOCK request body.
     * @throws DavException if an error occurs
     * @see DavResource#unlock(String)
     * @see TransactionDavServletRequest#getTransactionId()
     * @see org.apache.jackrabbit.webdav.DavServletRequest#getLockToken()
     */
    public void unlock(String lockToken, TransactionInfo info) throws DavException;
}