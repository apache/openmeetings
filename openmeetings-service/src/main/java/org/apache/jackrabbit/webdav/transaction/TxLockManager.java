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
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

/**
 * <code>TxLockManager</code> manages locks with locktype
 * '{@link TransactionConstants#TRANSACTION dcr:transaction}'.
 *
 * todo: removing all expired locks
 * todo: 'local' and 'global' are not accurate terms in the given context &gt; replace
 * todo: the usage of the 'global' transaction is not according to the JTA specification,
 * which explicitly requires any transaction present on a servlet to be completed before
 * the service method returns. Starting/completing transactions on the session object,
 * which is possible with the jackrabbit implementation is a hack.
 * todo: review of this transaction part is therefore required. Is there a use-case
 * for those 'global' transactions at all...
 */
public interface TxLockManager extends LockManager {


    /**
     * Release the lock identified by the given lock token.
     *
     * @param lockInfo
     * @param lockToken
     * @param resource
     * @throws org.apache.jackrabbit.webdav.DavException
     */
    public void releaseLock(TransactionInfo lockInfo, String lockToken,
			    TransactionResource resource) throws DavException;


    /**
     * Return the lock applied to the given resource or <code>null</code>
     *
     * @param type
     * @param scope
     * @param resource
     * @return lock applied to the given resource or <code>null</code>
     * @see org.apache.jackrabbit.webdav.lock.LockManager#getLock(org.apache.jackrabbit.webdav.lock.Type, org.apache.jackrabbit.webdav.lock.Scope, org.apache.jackrabbit.webdav.DavResource)
     */
    public ActiveLock getLock(Type type, Scope scope, TransactionResource resource);


}
