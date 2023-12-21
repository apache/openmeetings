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
package org.apache.jackrabbit.webdav.server;

import org.apache.jackrabbit.webdav.ContentCodingAwareRequest;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestImpl;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.WebdavResponseImpl;
import org.apache.jackrabbit.webdav.bind.RebindInfo;
import org.apache.jackrabbit.webdav.bind.UnbindInfo;
import org.apache.jackrabbit.webdav.bind.BindableResource;
import org.apache.jackrabbit.webdav.bind.BindInfo;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.InputContextImpl;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.io.OutputContextImpl;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;
import org.apache.jackrabbit.webdav.ordering.OrderingResource;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.search.SearchConstants;
import org.apache.jackrabbit.webdav.search.SearchInfo;
import org.apache.jackrabbit.webdav.search.SearchResource;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.AclResource;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.util.CSRFUtil;
import org.apache.jackrabbit.webdav.util.HttpDateTimeFormatter;
import org.apache.jackrabbit.webdav.version.ActivityResource;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * <code>AbstractWebdavServlet</code>
 * <p>
 */
abstract public class AbstractWebdavServlet extends HttpServlet implements DavConstants {

    // todo respect Position header
    /**
     * default logger
     */
    private static Logger log = LoggerFactory.getLogger(AbstractWebdavServlet.class);

    /** the 'missing-auth-mapping' init parameter */
    public final static String INIT_PARAM_MISSING_AUTH_MAPPING = "missing-auth-mapping";

    /**
     * Name of the optional init parameter that defines the value of the
     * 'WWW-Authenticate' header.
     * <p>
     * If the parameter is omitted the default value
     * {@link #DEFAULT_AUTHENTICATE_HEADER "Basic Realm=Jackrabbit Webdav Server"}
     * is used.
     *
     * @see #getAuthenticateHeaderValue()
     */
    public static final String INIT_PARAM_AUTHENTICATE_HEADER = "authenticate-header";

    /**
     * Default value for the 'WWW-Authenticate' header, that is set, if request
     * results in a {@link DavServletResponse#SC_UNAUTHORIZED 401 (Unauthorized)}
     * error.
     *
     * @see #getAuthenticateHeaderValue()
     */
    public static final String DEFAULT_AUTHENTICATE_HEADER = "Basic realm=\"Jackrabbit Webdav Server\"";

    /**
     * Name of the parameter that specifies the configuration of the CSRF protection.
     * May contain a comma-separated list of allowed referrer hosts.
     * If the parameter is omitted or left empty the behaviour is to only allow requests which have an empty referrer
     * or a referrer host equal to the server host.
     * If the parameter is set to 'disabled' no referrer checks will be performed at all.
     */
    public static final String INIT_PARAM_CSRF_PROTECTION = "csrf-protection";

    /**
     * Name of the 'createAbsoluteURI' init parameter that defines whether hrefs
     * should be created with a absolute URI or as absolute Path (ContextPath).
     * The value should be 'true' or 'false'. The default value if not set is true.
     */
    public final static String INIT_PARAM_CREATE_ABSOLUTE_URI = "createAbsoluteURI";


    /**
     * Header value as specified in the {@link #INIT_PARAM_AUTHENTICATE_HEADER} parameter.
     */
    private String authenticate_header;

    /**
     * CSRF protection utility
     */
    private CSRFUtil csrfUtil;

    /**
     * Create per default absolute URI hrefs
     */
    private boolean createAbsoluteURI = true;

    @Override
    public void init() throws ServletException {
        super.init();

        // authenticate header
        authenticate_header = getInitParameter(INIT_PARAM_AUTHENTICATE_HEADER);
        if (authenticate_header == null) {
            authenticate_header = DEFAULT_AUTHENTICATE_HEADER;
        }
        log.info(INIT_PARAM_AUTHENTICATE_HEADER + " = " + authenticate_header);

        // read csrf protection params
        String csrfParam = getInitParameter(INIT_PARAM_CSRF_PROTECTION);
        csrfUtil = new CSRFUtil(csrfParam);
        log.info(INIT_PARAM_CSRF_PROTECTION + " = " + csrfParam);

        //create absolute URI hrefs..
        String param = getInitParameter(INIT_PARAM_CREATE_ABSOLUTE_URI);
        if (param != null) {
            createAbsoluteURI = Boolean.parseBoolean(param);
        }
        log.info(INIT_PARAM_CREATE_ABSOLUTE_URI + " = " + createAbsoluteURI);
    }

    /**
     * Checks if the precondition for this request and resource is valid.
     *
     * @param request
     * @param resource
     * @return
     */
    abstract protected boolean isPreconditionValid(WebdavRequest request, DavResource resource);

    /**
     * Returns the <code>DavSessionProvider</code>.
     *
     * @return the session provider
     */
    abstract public DavSessionProvider getDavSessionProvider();

    /**
     * Returns the <code>DavSessionProvider</code>.
     *
     * @param davSessionProvider
     */
    abstract public void setDavSessionProvider(DavSessionProvider davSessionProvider);

    /**
     * Returns the <code>DavLocatorFactory</code>.
     *
     * @return the locator factory
     */
    abstract public DavLocatorFactory getLocatorFactory();

    /**
     * Sets the <code>DavLocatorFactory</code>.
     *
     * @param locatorFactory
     */
    abstract public void setLocatorFactory(DavLocatorFactory locatorFactory);

    /**
     * Returns the <code>DavResourceFactory</code>.
     *
     * @return the resource factory
     */
    abstract public DavResourceFactory getResourceFactory();

    /**
     * Sets the <code>DavResourceFactory</code>.
     *
     * @param resourceFactory
     */
    abstract public void setResourceFactory(DavResourceFactory resourceFactory);

    /**
     * Returns the value of the 'WWW-Authenticate' header, that is returned in
     * case of 401 error: the value is retrireved from the corresponding init
     * param or defaults to {@link #DEFAULT_AUTHENTICATE_HEADER}.
     *
     * @return corresponding init parameter or {@link #DEFAULT_AUTHENTICATE_HEADER}.
     * @see #INIT_PARAM_AUTHENTICATE_HEADER
     */
    public String getAuthenticateHeaderValue() {
        return authenticate_header;
    }

    /**
     * Returns if a absolute URI should be created for hrefs.
     *
     * @return absolute URI hrefs
     */
    protected boolean isCreateAbsoluteURI() {
        return createAbsoluteURI;
    }

    /**
     * Service the given request.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WebdavRequest webdavRequest = new WebdavRequestImpl(request, getLocatorFactory(), isCreateAbsoluteURI());
        // DeltaV requires 'Cache-Control' header for all methods except 'VERSION-CONTROL' and 'REPORT'.
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        boolean noCache = DavMethods.isDeltaVMethod(webdavRequest) && !(DavMethods.DAV_VERSION_CONTROL == methodCode || DavMethods.DAV_REPORT == methodCode);
        WebdavResponse webdavResponse = new WebdavResponseImpl(response, noCache);

        try {
            WebdavRequestContextHolder.setContext(new WebdavRequestContextImpl(webdavRequest));

            // make sure there is a authenticated user
            if (!getDavSessionProvider().attachSession(webdavRequest)) {
                return;
            }

            // perform referrer host checks if CSRF protection is enabled
            if (!csrfUtil.isValidRequest(webdavRequest)) {
                webdavResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // JCR-4165: reject any content-coding in request until we can
            // support it (see JCR-4166)
            if (!(webdavRequest instanceof ContentCodingAwareRequest)) {
                List<String> ces = getContentCodings(request);
                if (!ces.isEmpty()) {
                    webdavResponse.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    webdavResponse.setHeader("Accept-Encoding", "identity");
                    webdavResponse.setContentType("text/plain; charset=UTF-8");
                    webdavResponse.getWriter().println("Content-Encodings not supported, but received: " + ces);
                    webdavResponse.getWriter().flush();
                }
            }

            // check matching if=header for lock-token relevant operations
            DavResource resource = getResourceFactory().createResource(webdavRequest.getRequestLocator(), webdavRequest, webdavResponse);
            if (!isPreconditionValid(webdavRequest, resource)) {
                webdavResponse.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            if (!execute(webdavRequest, webdavResponse, methodCode, resource)) {
                super.service(request, response);
            }
        } catch (DavException e) {
            handleDavException(webdavRequest, webdavResponse, e);
        } catch (IOException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof DavException) {
                handleDavException(webdavRequest, webdavResponse, (DavException) cause);
            } else {
                throw ex;
            }
        } finally {
            WebdavRequestContextHolder.clearContext();
            getDavSessionProvider().releaseSession(webdavRequest);
        }
    }

    private void handleDavException(WebdavRequest webdavRequest, WebdavResponse webdavResponse, DavException ex)
            throws IOException {
        if (ex.getErrorCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            sendUnauthorized(webdavRequest, webdavResponse, ex);
        } else {
            Element condition = ex.getErrorCondition();
            if (DomUtil.matches(condition, ContentCodingAwareRequest.PRECONDITION_SUPPORTED)) {
                if (webdavRequest instanceof ContentCodingAwareRequest) {
                    webdavResponse.setHeader("Accept-Encoding", ((ContentCodingAwareRequest) webdavRequest).getAcceptableCodings());
                }
            }
            webdavResponse.sendError(ex);
        }
    }

    /**
     * If request payload was uncompressed, hint about acceptable content codings (RFC 7694)
     */
    private void addHintAboutPotentialRequestEncodings(WebdavRequest webdavRequest, WebdavResponse webdavResponse) {
        if (webdavRequest instanceof ContentCodingAwareRequest) {
            ContentCodingAwareRequest ccr = (ContentCodingAwareRequest)webdavRequest;
            List<String> ces = ccr.getRequestContentCodings();
            if (ces.isEmpty()) {
                webdavResponse.setHeader("Accept-Encoding", ccr.getAcceptableCodings());
            }
        }
    }

    /**
     * Sets the "WWW-Authenticate" header and writes the appropriate error
     * to the given webdav response.
     *
     * @param request The webdav request.
     * @param response The webdav response.
     * @param error The DavException that leads to the unauthorized response.
     * @throws IOException
     */
    protected void sendUnauthorized(WebdavRequest request,
                                    WebdavResponse response, DavException error) throws IOException {
        response.setHeader("WWW-Authenticate", getAuthenticateHeaderValue());
        if (error == null || error.getErrorCode() != HttpServletResponse.SC_UNAUTHORIZED) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.sendError(error.getErrorCode(), error.getStatusPhrase());
        }
    }

    /**
     * Executes the respective method in the given webdav context
     *
     * @param request
     * @param response
     * @param method
     * @param resource
     * @throws ServletException
     * @throws IOException
     * @throws DavException
     */
    protected boolean execute(WebdavRequest request, WebdavResponse response,
                              int method, DavResource resource)
            throws ServletException, IOException, DavException {

        switch (method) {
            case DavMethods.DAV_GET:
                doGet(request, response, resource);
                break;
            case DavMethods.DAV_HEAD:
                doHead(request, response, resource);
                break;
            case DavMethods.DAV_PROPFIND:
                doPropFind(request, response, resource);
                break;
            case DavMethods.DAV_PROPPATCH:
                doPropPatch(request, response, resource);
                break;
            case DavMethods.DAV_POST:
                doPost(request, response, resource);
                break;
            case DavMethods.DAV_PUT:
                doPut(request, response, resource);
                break;
            case DavMethods.DAV_DELETE:
                doDelete(request, response, resource);
                break;
            case DavMethods.DAV_COPY:
                doCopy(request, response, resource);
                break;
            case DavMethods.DAV_MOVE:
                doMove(request, response, resource);
                break;
            case DavMethods.DAV_MKCOL:
                doMkCol(request, response, resource);
                break;
            case DavMethods.DAV_OPTIONS:
                doOptions(request, response, resource);
                break;
            case DavMethods.DAV_LOCK:
                doLock(request, response, resource);
                break;
            case DavMethods.DAV_UNLOCK:
                doUnlock(request, response, resource);
                break;
            case DavMethods.DAV_ORDERPATCH:
                doOrderPatch(request, response, resource);
                break;
            case DavMethods.DAV_SUBSCRIBE:
                doSubscribe(request, response, resource);
                break;
            case DavMethods.DAV_UNSUBSCRIBE:
                doUnsubscribe(request, response, resource);
                break;
            case DavMethods.DAV_POLL:
                doPoll(request, response, resource);
                break;
            case DavMethods.DAV_SEARCH:
                doSearch(request, response, resource);
                break;
            case DavMethods.DAV_VERSION_CONTROL:
                doVersionControl(request, response, resource);
                break;
            case DavMethods.DAV_LABEL:
                doLabel(request, response, resource);
                break;
            case DavMethods.DAV_REPORT:
                doReport(request, response, resource);
                break;
            case DavMethods.DAV_CHECKIN:
                doCheckin(request, response, resource);
                break;
            case DavMethods.DAV_CHECKOUT:
                doCheckout(request, response, resource);
                break;
            case DavMethods.DAV_UNCHECKOUT:
                doUncheckout(request, response, resource);
                break;
            case DavMethods.DAV_MERGE:
                doMerge(request, response, resource);
                break;
            case DavMethods.DAV_UPDATE:
                doUpdate(request, response, resource);
                break;
            case DavMethods.DAV_MKWORKSPACE:
                doMkWorkspace(request, response, resource);
                break;
            case DavMethods.DAV_MKACTIVITY:
                doMkActivity(request, response, resource);
                break;
            case DavMethods.DAV_BASELINE_CONTROL:
                doBaselineControl(request, response, resource);
                break;
            case DavMethods.DAV_ACL:
                doAcl(request, response, resource);
                break;
            case DavMethods.DAV_REBIND:
                doRebind(request, response, resource);
                break;
            case DavMethods.DAV_UNBIND:
                doUnbind(request, response, resource);
                break;
            case DavMethods.DAV_BIND:
                doBind(request, response, resource);
                break;
            default:
                // any other method
                return false;
        }
        return true;
    }

    /**
     * The OPTION method
     *
     * @param request
     * @param response
     * @param resource
     */
    protected void doOptions(WebdavRequest request, WebdavResponse response,
                             DavResource resource) throws IOException, DavException {
        response.addHeader(DavConstants.HEADER_DAV, resource.getComplianceClass());
        response.addHeader("Allow", resource.getSupportedMethods());
        response.addHeader("MS-Author-Via", DavConstants.HEADER_DAV);
        if (resource instanceof SearchResource) {
            String[] langs = ((SearchResource) resource).getQueryGrammerSet().getQueryLanguages();
            for (String lang : langs) {
                response.addHeader(SearchConstants.HEADER_DASL, "<" + lang + ">");
            }
        }
        // with DeltaV the OPTIONS request may contain a Xml body.
        OptionsResponse oR = null;
        OptionsInfo oInfo = request.getOptionsInfo();
        if (oInfo != null && resource instanceof DeltaVResource) {
            oR = ((DeltaVResource) resource).getOptionResponse(oInfo);
        }
        if (oR == null) {
            response.setStatus(DavServletResponse.SC_OK);
        } else {
            response.sendXmlResponse(oR, DavServletResponse.SC_OK);
        }
    }

    /**
     * The HEAD method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doHead(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException {
        spoolResource(request, response, resource, false);
    }

    /**
     * The GET method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doGet(WebdavRequest request, WebdavResponse response,
                         DavResource resource) throws IOException, DavException {
        spoolResource(request, response, resource, true);
    }

    /**
     * @param request
     * @param response
     * @param resource
     * @param sendContent
     * @throws IOException
     */
    private void spoolResource(WebdavRequest request, WebdavResponse response,
                               DavResource resource, boolean sendContent)
            throws IOException {

        if (!resource.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long modSince = UNDEFINED_TIME;
        try {
            // will throw if multiple field lines present
            String value = getSingletonField(request, "If-Modified-Since");
            if (value != null) {
                modSince = HttpDateTimeFormatter.parse(value);
            }
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            log.debug("illegal value for if-modified-since ignored: " + ex.getMessage());
        }

        if (modSince > UNDEFINED_TIME) {
            long modTime = resource.getModificationTime();
            // test if resource has been modified. note that formatted modification
            // time lost the milli-second precision
            if (modTime != UNDEFINED_TIME && (modTime / 1000 * 1000) <= modSince) {
                // resource has not been modified since the time indicated in the
                // 'If-Modified-Since' header.

                DavProperty<?> etagProp = resource.getProperty(DavPropertyName.GETETAG);
                if (etagProp != null) {
                    // 304 response MUST contain Etag when available
                    response.setHeader("etag", etagProp.getValue().toString());
                }
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        // spool resource properties and eventually resource content.
        OutputStream out = (sendContent) ? response.getOutputStream() : null;
        resource.spool(getOutputContext(response, out));
        response.flushBuffer();
    }

    /**
     * The PROPFIND method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doPropFind(WebdavRequest request, WebdavResponse response,
                              DavResource resource) throws IOException, DavException {

        if (!resource.exists()) {
            response.sendError(DavServletResponse.SC_NOT_FOUND);
            return;
        }

        int depth = request.getDepth(DEPTH_INFINITY);
        DavPropertyNameSet requestProperties = request.getPropFindProperties();
        int propfindType = request.getPropFindType();

        MultiStatus mstatus = new MultiStatus();
        mstatus.addResourceProperties(resource, requestProperties, propfindType, depth);

        addHintAboutPotentialRequestEncodings(request, response);

        response.sendMultiStatus(mstatus,
                acceptsGzipEncoding(request) ? Collections.singletonList("gzip") : Collections.emptyList());
    }

    /**
     * The PROPPATCH method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doPropPatch(WebdavRequest request, WebdavResponse response,
                               DavResource resource)
            throws IOException, DavException {

        List<? extends PropEntry> changeList = request.getPropPatchChangeList();
        if (changeList.isEmpty()) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST);
            return;
        }

        MultiStatus ms = new MultiStatus();
        MultiStatusResponse msr = resource.alterProperties(changeList);
        ms.addResponse(msr);

        addHintAboutPotentialRequestEncodings(request, response);

        response.sendMultiStatus(ms);
    }

    /**
     * The POST method. Delegate to PUT
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doPost(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * The PUT method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doPut(WebdavRequest request, WebdavResponse response,
                         DavResource resource) throws IOException, DavException {

        if (request.getHeader("Content-Range") != null) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST, "Content-Range in PUT request not supported");
            return;
        }

        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists()) {
            // parent does not exist
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }

        int status;
        // test if resource already exists
        if (resource.exists()) {
            status = DavServletResponse.SC_NO_CONTENT;
        } else {
            status = DavServletResponse.SC_CREATED;
        }

        parentResource.addMember(resource, getInputContext(request, request.getInputStream()));
        response.setStatus(status);
    }

    /**
     * The MKCOL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doMkCol(WebdavRequest request, WebdavResponse response,
                           DavResource resource) throws IOException, DavException {

        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            // parent does not exist or is not a collection
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }
        // shortcut: mkcol is only allowed on deleted/non-existing resources
        if (resource.exists()) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if (request.getContentLength() > 0 || request.getHeader("Transfer-Encoding") != null) {
            parentResource.addMember(resource, getInputContext(request, request.getInputStream()));
        } else {
            parentResource.addMember(resource, getInputContext(request, null));
        }
        response.setStatus(DavServletResponse.SC_CREATED);
    }

    /**
     * The DELETE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doDelete(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws IOException, DavException {
        DavResource parent = resource.getCollection();
        if (parent != null) {
            parent.removeMember(resource);
            response.setStatus(DavServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(DavServletResponse.SC_FORBIDDEN, "Cannot remove the root resource.");
        }
    }

    /**
     * The COPY method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doCopy(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        // only depth 0 and infinity is allowed
        int depth = request.getDepth(DEPTH_INFINITY);
        if (!(depth == DEPTH_0 || depth == DEPTH_INFINITY)) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST);
            return;
        }

        DavResource destResource = getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = validateDestination(destResource, request, true);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }

        resource.copy(destResource, depth == DEPTH_0);
        response.setStatus(status);
    }

    /**
     * The MOVE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doMove(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        DavResource destResource = getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = validateDestination(destResource, request, true);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }

        resource.move(destResource);
        response.setStatus(status);
    }

    /**
     * The BIND method
     *
     * @param request
     * @param response
     * @param resource the collection resource to which a new member will be added
     * @throws IOException
     * @throws DavException
     */
    protected void doBind(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        if (!resource.exists()) {
            response.sendError(DavServletResponse.SC_NOT_FOUND);
        }
        BindInfo bindInfo = request.getBindInfo();
        DavResource oldBinding = getResourceFactory().createResource(request.getHrefLocator(bindInfo.getHref()), request, response);
        if (!(oldBinding instanceof BindableResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        DavResource newBinding = getResourceFactory().createResource(request.getMemberLocator(bindInfo.getSegment()), request, response);
        int status = validateDestination(newBinding, request, false);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }
        ((BindableResource) oldBinding).bind(resource, newBinding);
        response.setStatus(status);
    }

    /**
     * The REBIND method
     *
     * @param request
     * @param response
     * @param resource the collection resource to which a new member will be added
     * @throws IOException
     * @throws DavException
     */
    protected void doRebind(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws IOException, DavException {

        if (!resource.exists()) {
            response.sendError(DavServletResponse.SC_NOT_FOUND);
        }
        RebindInfo rebindInfo = request.getRebindInfo();
        DavResource oldBinding = getResourceFactory().createResource(request.getHrefLocator(rebindInfo.getHref()), request, response);
        if (!(oldBinding instanceof BindableResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        DavResource newBinding = getResourceFactory().createResource(request.getMemberLocator(rebindInfo.getSegment()), request, response);
        int status = validateDestination(newBinding, request, false);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }
        ((BindableResource) oldBinding).rebind(resource, newBinding);
        response.setStatus(status);
    }

    /**
     * The UNBIND method
     *
     * @param request
     * @param response
     * @param resource the collection resource from which a member will be removed
     * @throws IOException
     * @throws DavException
     */
    protected void doUnbind(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws IOException, DavException {

        UnbindInfo unbindInfo = request.getUnbindInfo();
        DavResource srcResource = getResourceFactory().createResource(request.getMemberLocator(unbindInfo.getSegment()), request, response);
        resource.removeMember(srcResource);
    }

    /**
     * Validate the given destination resource and return the proper status
     * code: Any return value greater/equal than {@link DavServletResponse#SC_NO_CONTENT}
     * indicates an error.
     *
     * @param destResource destination resource to be validated.
     * @param request
     * @param checkHeader flag indicating if the destination header must be present.
     * @return status code indicating whether the destination is valid.
     */
    protected int validateDestination(DavResource destResource, WebdavRequest request, boolean checkHeader)
            throws DavException {

        if (checkHeader) {
            String destHeader = request.getHeader(HEADER_DESTINATION);
            if (destHeader == null || "".equals(destHeader)) {
                return DavServletResponse.SC_BAD_REQUEST;
            }
        }
        if (destResource.getLocator().equals(request.getRequestLocator())) {
            return DavServletResponse.SC_FORBIDDEN;
        }

        int status;
        if (destResource.exists()) {
            if (request.isOverwrite()) {
                // matching if-header required for existing resources
                if (!request.matchesIfHeader(destResource)) {
                    return DavServletResponse.SC_PRECONDITION_FAILED;
                } else {
                    // overwrite existing resource
                    DavResource col;
                    try {
                        col = destResource.getCollection();
                    }
                    catch (IllegalArgumentException ex) {
                        return DavServletResponse.SC_BAD_GATEWAY;
                    }
                    col.removeMember(destResource);
                    status = DavServletResponse.SC_NO_CONTENT;
                }
            } else {
                // cannot copy/move to an existing item, if overwrite is not forced
                return DavServletResponse.SC_PRECONDITION_FAILED;
            }
        } else {
            // destination does not exist >> copy/move can be performed
            status = DavServletResponse.SC_CREATED;
        }
        return status;
    }

    /**
     * The LOCK method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doLock(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        LockInfo lockInfo = request.getLockInfo();
        if (lockInfo.isRefreshLock()) {
            // refresh any matching existing locks
            ActiveLock[] activeLocks = resource.getLocks();
            List<ActiveLock> lList = new ArrayList<ActiveLock>();
            for (ActiveLock activeLock : activeLocks) {
                // adjust lockinfo with type/scope retrieved from the lock.
                lockInfo.setType(activeLock.getType());
                lockInfo.setScope(activeLock.getScope());

                DavProperty<?> etagProp = resource.getProperty(DavPropertyName.GETETAG);
                String etag = etagProp != null ? String.valueOf(etagProp.getValue()) : "";
                if (request.matchesIfHeader(resource.getHref(), activeLock.getToken(), etag)) {
                    lList.add(resource.refreshLock(lockInfo, activeLock.getToken()));
                }
            }
            if (lList.isEmpty()) {
                throw new DavException(DavServletResponse.SC_PRECONDITION_FAILED);
            }
            ActiveLock[] refreshedLocks = lList.toArray(new ActiveLock[lList.size()]);
            response.sendRefreshLockResponse(refreshedLocks);
        } else {
            int status = HttpServletResponse.SC_OK;
            if (!resource.exists()) {
                // lock-empty requires status code 201 (Created)
                status = HttpServletResponse.SC_CREATED;
            }

            // create a new lock
            ActiveLock lock = resource.lock(lockInfo);

            CodedUrlHeader header = new CodedUrlHeader(
                    DavConstants.HEADER_LOCK_TOKEN, lock.getToken());
            response.setHeader(header.getHeaderName(), header.getHeaderValue());

            DavPropertySet propSet = new DavPropertySet();
            propSet.add(new LockDiscovery(lock));
            response.sendXmlResponse(propSet, status);
        }
    }

    /**
     * The UNLOCK method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     */
    protected void doUnlock(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws DavException {
        // get lock token from header
        String lockToken = request.getLockToken();
        TransactionInfo tInfo = request.getTransactionInfo();
        if (tInfo != null) {
            ((TransactionResource) resource).unlock(lockToken, tInfo);
        } else {
            resource.unlock(lockToken);
        }
        response.setStatus(DavServletResponse.SC_NO_CONTENT);
    }

    /**
     * The ORDERPATCH method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doOrderPatch(WebdavRequest request,
                                WebdavResponse response,
                                DavResource resource)
            throws IOException, DavException {

        if (!(resource instanceof OrderingResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        OrderPatch op = request.getOrderPatch();
        if (op == null) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST);
            return;
        }
        // perform reordering of internal members
        ((OrderingResource) resource).orderMembers(op);
        response.setStatus(DavServletResponse.SC_OK);
    }

    /**
     * The SUBSCRIBE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doSubscribe(WebdavRequest request,
                               WebdavResponse response,
                               DavResource resource)
            throws IOException, DavException {

        if (!(resource instanceof ObservationResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        SubscriptionInfo info = request.getSubscriptionInfo();
        if (info == null) {
            response.sendError(DavServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        Subscription subs = ((ObservationResource) resource).subscribe(info, request.getSubscriptionId());
        response.sendSubscriptionResponse(subs);
    }

    /**
     * The UNSUBSCRIBE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doUnsubscribe(WebdavRequest request,
                                 WebdavResponse response,
                                 DavResource resource)
            throws IOException, DavException {

        if (!(resource instanceof ObservationResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        ((ObservationResource) resource).unsubscribe(request.getSubscriptionId());
        response.setStatus(DavServletResponse.SC_NO_CONTENT);
    }

    /**
     * The POLL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doPoll(WebdavRequest request,
                          WebdavResponse response,
                          DavResource resource)
            throws IOException, DavException {

        if (!(resource instanceof ObservationResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        EventDiscovery ed = ((ObservationResource) resource).poll(
                request.getSubscriptionId(), request.getPollTimeout());
        response.sendPollResponse(ed);
    }

    /**
     * The VERSION-CONTROL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doVersionControl(WebdavRequest request, WebdavResponse response,
                                    DavResource resource)
            throws DavException, IOException {
        if (!(resource instanceof VersionableResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        ((VersionableResource) resource).addVersionControl();
    }

    /**
     * The LABEL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doLabel(WebdavRequest request, WebdavResponse response,
                           DavResource resource)
            throws DavException, IOException {

        LabelInfo labelInfo = request.getLabelInfo();
        if (resource instanceof VersionResource) {
            ((VersionResource) resource).label(labelInfo);
        } else if (resource instanceof VersionControlledResource) {
            ((VersionControlledResource) resource).label(labelInfo);
        } else {
            // any other resource type that does not support a LABEL request
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    /**
     * The REPORT method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doReport(WebdavRequest request, WebdavResponse response,
                            DavResource resource)
            throws DavException, IOException {
        ReportInfo info = request.getReportInfo();
        Report report;
        if (resource instanceof DeltaVResource) {
            report = ((DeltaVResource) resource).getReport(info);
        } else if (resource instanceof AclResource) {
            report = ((AclResource) resource).getReport(info);
        } else {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        int statusCode = (report.isMultiStatusReport()) ? DavServletResponse.SC_MULTI_STATUS : DavServletResponse.SC_OK;
        addHintAboutPotentialRequestEncodings(request, response);
        response.sendXmlResponse(report, statusCode, acceptsGzipEncoding(request) ? Collections.singletonList("gzip") : Collections.emptyList());
    }

    /**
     * The CHECKIN method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doCheckin(WebdavRequest request, WebdavResponse response,
                             DavResource resource)
            throws DavException, IOException {

        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String versionHref = ((VersionControlledResource) resource).checkin();
        response.setHeader(DeltaVConstants.HEADER_LOCATION, versionHref);
        response.setStatus(DavServletResponse.SC_CREATED);
    }

    /**
     * The CHECKOUT method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doCheckout(WebdavRequest request, WebdavResponse response,
                              DavResource resource)
            throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        ((VersionControlledResource) resource).checkout();
    }

    /**
     * The UNCHECKOUT method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doUncheckout(WebdavRequest request, WebdavResponse response,
                                DavResource resource)
            throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        ((VersionControlledResource) resource).uncheckout();
    }

    /**
     * The MERGE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doMerge(WebdavRequest request, WebdavResponse response,
                           DavResource resource) throws DavException, IOException {

        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        MergeInfo info = request.getMergeInfo();
        MultiStatus ms = ((VersionControlledResource) resource).merge(info);
        response.sendMultiStatus(ms);
    }

    /**
     * The UPDATE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doUpdate(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws DavException, IOException {

        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        UpdateInfo info = request.getUpdateInfo();
        MultiStatus ms = ((VersionControlledResource) resource).update(info);
        response.sendMultiStatus(ms);
    }

    /**
     * The MKWORKSPACE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doMkWorkspace(WebdavRequest request, WebdavResponse response,
                                 DavResource resource) throws DavException, IOException {
        if (resource.exists()) {
            AbstractWebdavServlet.log.warn("Cannot create a new workspace. Resource already exists.");
            response.sendError(DavServletResponse.SC_FORBIDDEN);
            return;
        }

        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            // parent does not exist or is not a collection
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }
        if (!(parentResource instanceof DeltaVResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        ((DeltaVResource) parentResource).addWorkspace(resource);
        response.setStatus(DavServletResponse.SC_CREATED);
    }

    /**
     * The MKACTIVITY method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doMkActivity(WebdavRequest request, WebdavResponse response,
                                DavResource resource) throws DavException, IOException {
        if (resource.exists()) {
            AbstractWebdavServlet.log.warn("Unable to create activity: A resource already exists at the request-URL " + request.getRequestURL());
            response.sendError(DavServletResponse.SC_FORBIDDEN);
            return;
        }

        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            // parent does not exist or is not a collection
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }
        // TODO: improve. see http://issues.apache.org/jira/browse/JCR-394
        if (!parentResource.getComplianceClass().contains(DavCompliance.ACTIVITY)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if (!(resource instanceof ActivityResource)) {
            AbstractWebdavServlet.log.error("Unable to create activity: ActivityResource expected");
            response.sendError(DavServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // try to add the new activity resource
        parentResource.addMember(resource, getInputContext(request, request.getInputStream()));

        // Note: mandatory cache control header has already been set upon response creation.
        response.setStatus(DavServletResponse.SC_CREATED);
    }

    /**
     * The BASELINECONTROL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doBaselineControl(WebdavRequest request, WebdavResponse response,
                                     DavResource resource)
        throws DavException, IOException {

        if (!resource.exists()) {
            AbstractWebdavServlet.log.warn("Unable to add baseline control. Resource does not exist " + resource.getHref());
            response.sendError(DavServletResponse.SC_NOT_FOUND);
            return;
        }
        // TODO: improve. see http://issues.apache.org/jira/browse/JCR-394
        if (!(resource instanceof VersionControlledResource) || !resource.isCollection()) {
            AbstractWebdavServlet.log.warn("BaselineControl is not supported by resource " + resource.getHref());
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // TODO : missing method on VersionControlledResource
        throw new DavException(DavServletResponse.SC_NOT_IMPLEMENTED);
        /*
        ((VersionControlledResource) resource).addBaselineControl(request.getRequestDocument());
        // Note: mandatory cache control header has already been set upon response creation.
        response.setStatus(DavServletResponse.SC_OK);
        */
    }

    /**
     * The SEARCH method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doSearch(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws DavException, IOException {

        if (!(resource instanceof SearchResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Document doc = request.getRequestDocument();
        if (doc != null) {
            SearchInfo sR = SearchInfo.createFromXml(doc.getDocumentElement());
            response.sendMultiStatus(((SearchResource) resource).search(sR));
        } else {
            // request without request body is valid if requested resource
            // is a 'query' resource.
            response.sendMultiStatus(((SearchResource) resource).search(null));
        }
    }

    /**
     * The ACL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws DavException
     * @throws IOException
     */
    protected void doAcl(WebdavRequest request, WebdavResponse response,
                         DavResource resource) throws DavException, IOException {
        if (!(resource instanceof AclResource)) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Document doc = request.getRequestDocument();
        if (doc == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "ACL request requires a DAV:acl body.");
        }
        AclProperty acl = AclProperty.createFromXml(doc.getDocumentElement());
        ((AclResource)resource).alterAcl(acl);
    }

    /**
     * Return a new <code>InputContext</code> used for adding resource members
     *
     * @param request
     * @param in
     * @return
     * @see #spoolResource(WebdavRequest, WebdavResponse, DavResource, boolean)
     */
    protected InputContext getInputContext(DavServletRequest request, InputStream in) {
        return new InputContextImpl(request, in);
    }

    /**
     * Return a new <code>OutputContext</code> used for spooling resource properties and
     * the resource content
     *
     * @param response
     * @param out
     * @return
     * @see #doPut(WebdavRequest, WebdavResponse, DavResource)
     * @see #doMkCol(WebdavRequest, WebdavResponse, DavResource)
     */
    protected OutputContext getOutputContext(DavServletResponse response, OutputStream out) {
        return new OutputContextImpl(response, out);
    }

    /**
     * Obtain the (ordered!) list of content codings that have been used in the
     * request
     */
    public static List<String> getContentCodings(HttpServletRequest request) {
        return getListElementsFromHeaderField(request, "Content-Encoding");
    }

    /**
     * Check whether recipient accepts GZIP content coding
     */
    private static boolean acceptsGzipEncoding(HttpServletRequest request) {
        List<String> result = getListElementsFromHeaderField(request, "Accept-Encoding");
        for (String s : result) {
            s = s.replace(" ", "");
            int semi = s.indexOf(';');
            if ("gzip".equals(s)) {
                return true;
            } else if (semi > 0) {
                String enc = s.substring(0, semi);
                String parm = s.substring(semi + 1);
                if ("gzip".equals(enc) && parm.startsWith("q=")) {
                    float q = Float.valueOf(parm.substring(2));
                    return q > 0;
                }
            }
        }
        return false;
    }

    private static List<String> getListElementsFromHeaderField(HttpServletRequest request, String fieldName) {
        List<String> result = Collections.emptyList();
        for (Enumeration<String> ceh = request.getHeaders(fieldName); ceh.hasMoreElements();) {
            for (String h : ceh.nextElement().split(",")) {
                if (!h.trim().isEmpty()) {
                    if (result.isEmpty()) {
                        result = new ArrayList<String>();
                    }
                    result.add(h.trim().toLowerCase(Locale.ENGLISH));
                }
            }
        }

        return result;
    }

    /**
     * Get field value of a singleton field
     * @param request HTTP request
     * @param fieldName field name
     * @return the field value (when there is indeed a single field line) or {@code null} when field not present
     * @throws IllegalArgumentException when multiple field lines present
     */
    protected static String getSingletonField(HttpServletRequest request, String fieldName) {
        Enumeration<String> lines = request.getHeaders(fieldName);
        if (!lines.hasMoreElements()) {
            return null;
        } else {
            String value = lines.nextElement();
            if (!lines.hasMoreElements()) {
                return value;
            } else {
                List<String> v = new ArrayList<>();
                v.add(value);
                while (lines.hasMoreElements()) {
                    v.add(lines.nextElement());
                }
                throw new IllegalArgumentException("Multiple field lines for '" + fieldName + "' header field: " + v);
            }
        }
    }
}

