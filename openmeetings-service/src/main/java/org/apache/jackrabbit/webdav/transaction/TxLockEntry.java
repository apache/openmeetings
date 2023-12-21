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

import org.apache.jackrabbit.webdav.lock.AbstractLockEntry;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>TxLockEntry</code> represents the lock entry objects allowed for
 * a transaction lock.
 */
public final class TxLockEntry extends AbstractLockEntry implements TransactionConstants {

    private static Logger log = LoggerFactory.getLogger(TxLockEntry.class);

    private final Scope scope;

    /**
     * Create a lock entry that identifies transaction lock.
     *
     * @param isLocal boolean value indicating whether this is a local or a global
     * lock entry.
     */
    public TxLockEntry(boolean isLocal) {
        if (isLocal) {
            scope = LOCAL;
        } else {
            scope = GLOBAL;
        }
    }

    /**
     * Returns the {@link #TRANSACTION 'transaction'} lock type.
     *
     * @return always returns the 'transaction' type.
     * @see org.apache.jackrabbit.webdav.lock.LockEntry#getType()
     * @see #TRANSACTION
     */
    public Type getType() {
        return TRANSACTION;
    }

    /**
     * Returns either {@link #LOCAL local} or {@link #GLOBAL global} scope
     * depending on the initial constructor value.
     *
     * @return returns 'global' or 'local' scope.
     * @see org.apache.jackrabbit.webdav.lock.LockEntry#getScope()
     * @see #GLOBAL
     * @see #LOCAL
     */
    public Scope getScope() {
        return scope;
    }
}