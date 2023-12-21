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
import org.apache.jackrabbit.webdav.DavServletRequest;

/**
 * <code>TransactionDavServletRequest</code> provides extensions to the
 * {@link DavServletRequest} interface used for dealing with transaction lock
 * requests.
 */
public interface TransactionDavServletRequest extends DavServletRequest {

    /**
     * Retrieve the 'transactioninfo' request body that must be included with
     * the UNLOCK request of a transaction lock. If the request body is does not
     * provide the information required (either because it is missing or the
     * Xml is not valid) <code>null</code> is returned.
     *
     * @return <code>TransactionInfo</code> object encapsulating the 'transactioninfo'
     * Xml element present in the request body or <code>null</code> if no
     * body is present or if it could not be parsed.
     * @throws DavException if an invalid request body is present.
     */
    public TransactionInfo getTransactionInfo() throws DavException;


    /**
     * Retrieve the transaction id from the
     * {@link TransactionConstants#HEADER_TRANSACTIONID TransactionId header}.
     *
     * @return transaction id as present in the {@link TransactionConstants#HEADER_TRANSACTIONID TransactionId header}
     * or <code>null</code>.
     */
    public String getTransactionId();
}