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
package org.apache.jackrabbit.webdav;

/**
 * <code>DavSession</code> allows to pass session information between request,
 * response and resource(s).
 */
public interface DavSession {

    /**
     * Adds a reference to this <code>DavSession</code> indicating that this
     * session must not be discarded after completion of the current request.
     *
     * @param reference to be added.
     */
    public void addReference(Object reference);

    /**
     * Releasing a reference to this <code>DavSession</code>. If no more
     * references are present, this session may be discarded.
     *
     * @param reference to be removed.
     */
    public void removeReference(Object reference);

    /**
     * Adds a lock token to this <code>DavSession</code>.
     *
     * @param token
     */
    public void addLockToken(String token);

    /**
     * Returns the lock tokens of this <code>DavSession</code>.
     *
     * @return
     */
    public String[] getLockTokens();

    /**
     * Removes a lock token from this <code>DavSession</code>.
     *
     * @param token
     */
    public void removeLockToken(String token);

}