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

import org.apache.jackrabbit.webdav.lock.DefaultActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

/**
 * <code>TxActiveLock</code> represents the transaction lock present on a
 * {@link TransactionResource}.
 */
public class TxActiveLock extends DefaultActiveLock implements TransactionConstants {

    public static final long DEFAULT_TIMEOUT = 300000; // 5 minutes

    private final Scope scope;

    /**
     * Create a new transaction lock.<br>
     * If the lockInfo element is <code>null</code> the timeout defaults to
     * half and hour. The default scope is 'local'.
     *
     * @param lockInfo
     * @throws IllegalArgumentException if either scope or type is invalid or if
     * a depth other than infinity is requested.
     */
    public TxActiveLock(LockInfo lockInfo) {
        if (lockInfo != null) {
            if (!TRANSACTION.equals(lockInfo.getType())) {
               throw new IllegalArgumentException("Only 'transaction' type is allowed for a transaction-activelock object.");
            }
            if (!(LOCAL.equals(lockInfo.getScope()) || GLOBAL.equals(lockInfo.getScope()))) {
               throw new IllegalArgumentException("Only 'global' or 'local' are valid scopes within a transaction-activelock element.");
            }
            if (!lockInfo.isDeep()) {
               throw new IllegalArgumentException("Only transaction locks can only be deep.");
            }
            setOwner(lockInfo.getOwner());
            setTimeout(lockInfo.getTimeout());
            scope = lockInfo.getScope();
        } else {
            setTimeout(DEFAULT_TIMEOUT);
            // local scope by default
            scope = LOCAL;
        }
    }

    /**
     * Always returns true.
     *
     * @return true
     */
    @Override
    public boolean isDeep() {
        return true;
    }

    /**
     * Always returns the {@link #TRANSACTION} type.
     *
     * @return {@link #TRANSACTION}
     */
    @Override
    public Type getType() {
        return TRANSACTION;
    }

    /**
     * Returns the scope of this lock which is either {@link #LOCAL} or {@link #GLOBAL}.
     *
     * @return {@link #LOCAL} or {@link #GLOBAL}
     */
    @Override
    public Scope getScope() {
        return scope;
    }
}
