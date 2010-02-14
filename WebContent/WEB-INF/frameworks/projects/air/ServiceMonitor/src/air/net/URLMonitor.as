/*
ADOBE SYSTEMS INCORPORATED
Copyright � 2008 Adobe Systems Incorporated. All Rights Reserved.
 
NOTICE:   Adobe permits you to modify and distribute this file only in accordance with
the terms of Adobe AIR SDK license agreement.  You may have received this file from a
source other than Adobe.  Nonetheless, you may modify or distribute this file only in 
accordance with such agreement.
*/

package air.net
{
	import flash.events.TimerEvent;
	import flash.events.Event;
	import flash.events.SecurityErrorEvent;
	import flash.events.IOErrorEvent;
	import flash.events.HTTPStatusEvent;
	import flash.net.URLRequest;
	import flash.net.URLStream;
	import flash.errors.IOError;
    import flash.errors.IOError;
    
/**
 * The URLMonitor class monitors availablity of an HTTP- or HTTPS-based service.
 * 
 * <p product="flex">This class is included in the ServiceMonitor.swc file. 
 * Adobe<sup>&#xAE;</sup> Flex<sup>&#8482;</sup> Builder loads this class automatically when you create a project for 
 * Adobe<sup>&#xAE;</sup> AIR<sup>&#8482;</sup>. The Adobe<sup>&#xAE;</sup> Flex<sup>&#8482;</sup> SDK also 
 * includes this servicemonitor.swc file, which you should include when compiling the application if you are using 
 * the Flex SDK.
 * </p>
 * 
 * <p product="flash">In Adobe<sup>&#xAE;</sup> Flash<sup>&#xAE;</sup> CS3 Professional,
 * this class is included in the ServiceMonitorShim.swc file. To use classes in the air.net package , 
 * you must first drag the ServiceMonitorShim component from the Components panel to the 
 * Library and then add the following <code>import</code> statement to your ActionScript 3.0 code:
 * </p>
 * 
 * <listing product="flash">import air.net.~~;</listing>
 *
 * <p product="flash">To use air.net package in Adobe<sup>&#xAE;</sup> Flash<sup>&#xAE;</sup> CS4 Professional: </p>
 *
 * <ol product="flash">
 *	<li>Select the File &gt; Publish Settings command.</li>
 *	<li>In the Flash panel, click the Settings button for ActionScript 3.0. Select Library Path.</li>
 *	<li>Click the Browse to SWC File button. Browse to Adobe Flash CS4/AIK1.1/frameworks/libs/air/servicemoniter.swc
 		file in the Adobe Flash CS4 installation folder.</li>
 *	<li>Click the OK button.</li>
 *	<li>Add the following <code>import</code> statement to your ActionScript 3.0 code: <code>import air.net.~~;</code></li>
 * </ol>
 * 
 * <p platform="javascript">To use this class in JavaScript code, load the ServiceMonitor.swf 
 * file, as in the following:</p>
 * 
 * <listing platform="javascript">&lt;script src="ServiceMonitor.swf" type="application/x-shockwave-flash"&gt;</listing>
 * 
 * @playerversion AIR 1.0
 */
public class URLMonitor extends ServiceMonitor
{
    private static const kNullPointerError:uint = 2007;

    /**
     * Creates a URLMonitor Object for a specified HTTP- or HTTPS-based service.
     *
     * <p>After creating a URLMonitor, the caller should call the <code>start()</code>
     * method to begin monitoring the status of the service.</p>
     *
     * <p>As with the Timer object, the caller should maintain a reference to the URLMonitor
     * object. Otherwise the runtime could delete the object, thereby ending the monitoring.</p>
     *
     * <p>A URLRequest parameter specifies the probe request for polling the server.
     * Typically, the request method will be either <code>"GET"</code> or <code>"HEAD"</code>.</p>
     *
     * @param urlRequest The URLRequest object representing a probe request for polling the server.
     * 
     * @param acceptableStatusCodes An array of numeric status codes listing the codes that represent a successful result.
     * 
     * <p>If you do not specify a value for the <code>acceptableStatusCodes</code> property, the following status
     * codes will be recognized as successful responses:</p>
     * 
     * <ul>
     *  <li>200 (OK) </li>
     *  <li>202 (Accepted) </li>
     *  <li>204 (No content </li>
     *  <li>205 (Reset content) </li>
     *  <li>206 (Partial content, in response to request with a Range header) </li>
     * </ul>
     * 
     * @playerversion AIR 1.0
     */
    public function URLMonitor(urlRequest:URLRequest, acceptableStatusCodes:Array = null)
    {
    	super();
        if (!urlRequest)
            Error.throwError(ArgumentError, kNullPointerError);

        // Keep original value to return from getter
        _urlRequestOriginal = urlRequest;
        
        // Make copy to use
        //   quite unfortunate that URLRequest doesn't have a clone method.
        //   also unfortunate that you can't clone the loginCredentials.
        _urlRequest = new URLRequest(urlRequest.url);
        _urlRequest.method = urlRequest.method;
        _urlRequest.contentType = urlRequest.contentType;
        _urlRequest.data = urlRequest.data; // not deep-copied
        _urlRequest.followRedirects = urlRequest.followRedirects;
        _urlRequest.authenticate = urlRequest.authenticate;
        _urlRequest.cacheResponse = urlRequest.cacheResponse;
        _urlRequest.manageCookies = urlRequest.manageCookies;
        _urlRequest.requestHeaders = urlRequest.requestHeaders.concat(); // copies array
        _urlRequest.userAgent = urlRequest.userAgent;
        
        // Never use cache!
        _urlRequest.useCache = false;

        if (acceptableStatusCodes)
            this.acceptableStatusCodes = acceptableStatusCodes;
            
        function cool(event:HTTPStatusEvent):void {
            // trace(event);
            // XXX what about FTP?
            available = _acceptableStatusCodes.indexOf(Number(event.status)) >= 0;
            try
            {
            	_urlStream.close();
            }
            catch(e:IOError)
            {
            	// if the url stream was never open...*shrug* we don't care
            }
        }
        
        function uncool(event:Event):void
        {
            // trace(event);
            available = false;
            try
            {
            	_urlStream.close();
            }
            catch(e:IOError)
            {
            	// if the url stream was never open...*shrug* we don't care
            }
        }
        
        _urlStream.addEventListener(HTTPStatusEvent.HTTP_RESPONSE_STATUS, cool);
        _urlStream.addEventListener(SecurityErrorEvent.SECURITY_ERROR, uncool);
        _urlStream.addEventListener(IOErrorEvent.IO_ERROR, uncool);
    }
    
    /**
     * The URLRequest object representing the probe request.
     * 
     * @playerversion AIR 1.0
     */
    public function get urlRequest():URLRequest
    {
        return _urlRequestOriginal;
    }
    
    /**
    * Attempts to load content from a URL in the background, to check for a 
    * returned HTTP status code. 
    * <p>
    * If it receives a status code that is listed in the <code>acceptableStatusCodes</code> 
    * property, the <code>available</code> property will be set to <code>true</code>. 
    * If it receives a status code that is not in the <code>acceptableStatusCodes</code> 
    * list, or if there is a security error or I/O error, the <code>available</code> 
    * property will be set to <code>false</code>.
    * </p>
    * 
    * @playerversion AIR 1.0
    */
    protected override function checkStatus():void
    {
        // trace('load', _urlRequest);
        _urlStream.load(_urlRequest);
    }
    
    /**
     * The numeric status codes representing a successful result.
     * 
     * @playerversion AIR 1.0
     */
    public function get acceptableStatusCodes():Array
    {
        return _acceptableStatusCodesOriginal;
    }
    
    public function set acceptableStatusCodes(value:Array):void
    {
        if (value == null)
            Error.throwError(ArgumentError, kNullPointerError);

        // value to return from getter
        _acceptableStatusCodesOriginal = value;
        
        // value to use
        _acceptableStatusCodes = value.concat();  // copy the array
    }
    
    /**
     * @inheritDoc
     * 
     * @playerversion AIR 1.0
     */
    public override function toString():String
    {
        if (!_urlRequest)
            return '[URLMonitor available="' + available + '"]';
        return '[URLMonitor method="' + _urlRequest.method + '" url="' + _urlRequest.url + 
            '" available="' + available + '"]';         
    }
    
    private static const _initStatusCodes:Array = [
        200,  // 200 OK
        202,  // 202 Accepted
        204,  // 204 No content
        205,  // 205 Reset Content -- eh, why not?
        206,  // 206 Partial Content (in response to req with Range header)
        
        // NOT:
        //   201 Created -- seems not to be repeatable by definition
        //   203 Non-Authoritative Information -- returned by intermediate proxy
        //   3xx redirection -- Because I don't want to ignore SHOULD clauses of RFC 2616, section 10
        //   4xx client errors -- Ditto and also to keep server admins from going crazy
        //   5xx server errors -- Not particularly indicative of server health
    ];
    private var _acceptableStatusCodesOriginal:Array = _initStatusCodes.concat();  // copies the array
    private var _acceptableStatusCodes:Array = _acceptableStatusCodesOriginal.concat();  // copies the array
    
    private var _urlRequestOriginal:URLRequest;
    private var _urlRequest:URLRequest;

    private var _urlStream:URLStream = new URLStream();
}
}
