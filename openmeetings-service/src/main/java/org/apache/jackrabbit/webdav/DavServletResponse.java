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

import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <code>WebdavResponse</code> extends the HttpServletResponse by
 * Webdav specific status codes and METHODS.
 */
public interface DavServletResponse extends HttpServletResponse {

    /**
     * The 102 (Processing) status code is an interim response used to
     * inform the client that the server has accepted the complete request,
     * but has not yet completed it.
     */
    int SC_PROCESSING = 102;

    /**
     * Status code (207) indicating that the response requires
     * providing status for multiple independent operations.
     */
    int SC_MULTI_STATUS = 207;

    /**
     * The 422 (Unprocessable Entity) status code means the server understands
     * the content type of the request entity (hence a 415(Unsupported Media Type)
     * status code is inappropriate), and the syntax of the request entity is
     * correct (thus a 400 (Bad Request) status code is inappropriate) but was
     * unable to process the contained instructions. For example, this error
     * condition may occur if an XML request body contains well-formed (i.e.,
     * syntactically correct), but semantically erroneous XML instructions.
     */
    int SC_UNPROCESSABLE_ENTITY = 422;

    /**
     * Status code (423) indicating the destination resource of a
     * method is locked, and either the request did not contain a
     * valid Lock-Info header, or the Lock-Info header identifies
     * a lock held by another principal.
     */
    int SC_LOCKED = 423;

    /**
     * Status code (424) indicating that the method could not be
     * performed on the resource, because the requested action depended
     * on another action which failed.
     */
    int SC_FAILED_DEPENDENCY = 424;

    /**
     * Status code (507) indicating that the resource does not have
     * sufficient space to record the state of the resource after the
     * execution of this method.
     */
    int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 507;

    /**
     * Send a response body given more detailed information about the error
     * occurred.
     *
     * @param error
     * @throws IOException
     */
    public void sendError(DavException error) throws IOException;

    /**
     * Send the multistatus response to the client. A multistatus response
     * is returned in response to a successful PROPFIND and PROPPATCH request.
     * In addition multistatus response is required response in case a COPY,
     * MOVE, DELETE, LOCK or PROPPATCH request fails.
     *
     * @param multistatus
     * @throws IOException
     * @see #SC_MULTI_STATUS
     */
    public void sendMultiStatus(MultiStatus multistatus) throws IOException;

    /**
     * Send the multistatus response to the client. A multistatus response
     * is returned in response to a successful PROPFIND and PROPPATCH request.
     * In addition multistatus response is required response in case a COPY,
     * MOVE, DELETE, LOCK or PROPPATCH request fails.
     *
     * @param multistatus
     * @param acceptableContentCodings content codings accepted by the client
     * @throws IOException
     * @see #SC_MULTI_STATUS
     */
    default void sendMultiStatus(MultiStatus multistatus, List<String> acceptableContentCodings) throws IOException {
        sendMultiStatus(multistatus);
    }

    /**
     * Send the lock response for a successful LOCK request, that was intended
     * to refresh an existing lock. The locks array must contain at least
     * a single element; the <code>ActiveLock</code> objects are then
     * included in the lockdiscovery property of the response body as required
     * by RFC 2518.
     *
     * @param locks
     * @throws IOException
     * @see DavConstants#PROPERTY_LOCKDISCOVERY
     */
    public void sendRefreshLockResponse(ActiveLock[] locks) throws IOException;

    /**
     * Generic method to return an Xml response body.
     *
     * @param serializable object that can be converted to the root Xml element
     * of the document to be sent as response body.
     * @param status Status code to be used with {@link #setStatus(int)}.
     * @throws IOException
     */
    public void sendXmlResponse(XmlSerializable serializable, int status) throws IOException;

    /**
     * Generic method to return an Xml response body.
     *
     * @param serializable object that can be converted to the root Xml element
     * of the document to be sent as response body.
     * @param status Status code to be used with {@link #setStatus(int)}.
     * @param acceptableContentCodings content codings accepted by the client
     * @throws IOException
     */
    default void sendXmlResponse(XmlSerializable serializable, int status, List<String> acceptableContentCodings) throws IOException {
        sendXmlResponse(serializable, status);
    }
}