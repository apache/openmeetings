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
    import flash.net.Socket;
    import flash.events.EventDispatcher;
    import flash.events.Event;
    import flash.events.StatusEvent;
    import flash.events.IOErrorEvent;
    import flash.events.TimerEvent;
    import flash.errors.IOError;
    import flash.utils.Timer;
    import flash.desktop.NativeApplication;

/**
 * Indicates that the service status has changed.
 *
 * <p>The value of the <code>code</code> property is either <code>"Service.available"</code> or <code>"Service.unavailable"</code>, 
 * but best practice is to check the value of the <code>ServiceMonitor.available</code> property.</p>
 * 
 * @eventType flash.events.StatusEvent.STATUS
 * 
 * @playerversion AIR 1.0
 */
[Event(name="status", type="flash.events.StatusEvent")]

/**
 * The ServiceMonitor class implements the framework for monitoring the status and availability of network services. 
 * The ServiceMonitor class acts as the base class for all other service monitors.
 * 
 * <p product="flex">This class is included in the ServiceMonitor.swc file. 
 * Adobe<sup>&#xAE;</sup> Flex&#8482; Builder&#8482; loads this class automatically 
 * when you create a project for Adobe<sup>&#xAE;</sup> AIR&#8482;.
 * Adobe<sup>&#xAE;</sup> Flex&#8482; SDK also includes this servicemonitor.swc file, 
 * which you should include when compiling the application if you are using Flex SDK.
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
public dynamic class ServiceMonitor extends EventDispatcher
{
    private static const kInvalidParamError:uint = 2004;
    private static const kDelayRangeError:uint = 2066;
    
    /**
     * Creates a ServiceMonitor object. 
     *
     * <p platform="actionscript">This class is typically subclassed to monitor specific service types.</p>
     * 
     * <p platform="javascript">The class can be specialized in JavaScript (from HTML application content), as described in
     * the description of the <code>makeJavascriptSubclass()</code> method.</p>
     *
     * <p>After creating a ServiceMonitor object (or a subclass object), call the <code>start()</code> method
     * to begin monitoring the status of the service.</p>
     *
     * <p>As with the Timer object, the caller should maintain a reference to the ServiceMonitor
     * object. Otherwise, the runtime deletes the object and monitoring ends.</p>
     * 
     * @playerversion AIR 1.0
     */
    public function ServiceMonitor()
    {
    	super();
        _timer.addEventListener(TimerEvent.TIMER, onTimer);
    }
    
    
    /**
     * Starts the service monitor.
     * 
     * @playerversion AIR 1.0
     */
    public function start():void
    {
        if (_running)
            return;
        _running = true;
            
        _notifyRegardless = true;
            
        if (_delay > 0)
            _timer.start();
            
        //
        // Important that this is a weak referencing listener because
        // otherwise the monitor could be lost to all references but still
        // be kept alive for no reason by the event reference.
        //
        NativeApplication.nativeApplication.addEventListener(Event.NETWORK_CHANGE, onNetworkChange,
            false /*use capture */, 0 /* priority */, true /* useWeakReference */);
        
        // initial test
        checkStatus();
    }
    
    /**
     * Stops monitoring the service.
     * 
     * @playerversion AIR 1.0
     */
    public function stop():void
    {
        if (!_running)
            return;
        _running = false;
        
        _timer.stop();
        NativeApplication.nativeApplication.removeEventListener(Event.NETWORK_CHANGE, onNetworkChange);
    }
    
    private var _running:Boolean = false;
    
    /**
     * The interval, in milliseconds, for polling the server.
     *
     * <p>If zero, the server is not polled periodically, but only immediately after <code>start()</code> is called
     * and when the network status changes.</p>
     * 
     * <p>The ServiceMonitor object only dispatches a <code>status</code> event if service
     * status has changed (not on every poll interval). The object also dispatches a <code>status</code> 
     * event as a result of network connectivity changes (regardles of the poll interval).</p>
     * 
     * @default 0
     * 
     * @playerversion AIR 1.0
     */
    public function get pollInterval():Number
    {
        return _delay;
    }
    
    public function set pollInterval(value:Number):void
    {
        if (value < 0 || !isFinite(value))
            Error.throwError(RangeError, kDelayRangeError);
        
        _delay = value;
        _timer.stop();
        
        // Weirdness: timer can be initialized to zero, but not set to zero.
        if (_delay > 0)
        {
            _timer.delay = _delay;
            if (_running)
                _timer.start();
        }
    }
    
    /**
     * Whether the monitor has been started.
     * 
     * @playerversion AIR 1.0
     */
    public function get running():Boolean
    {
        return _running;
    }
    
    private function onTimer(event:TimerEvent):void
    {
        // trace(event);
        checkStatus();
    }
    
    private function onNetworkChange(event:Event):void
    {
        if (!_running)
            return;
            
        // push timer back
        if (_delay > 0)
        {
            _timer.stop();
            _timer.start();
        }
        checkStatus();
    }
    
    /**
     * Checks the status of the service.
     *
     * <p>A subclass override method for checking the status of the service.</p>
     *
     * <p>Typically, this method will initiate a network operation whose completion or failure will result in
     * setting the <code>available</code> property.</p>
     *
     * <p>JavaScript code can specialize this method by defining a <code>checkStatus()</code> method 
     * in the "specializer" object.</p>
     * 
     * @playerversion AIR 1.0
     */
    protected function checkStatus():void
    {
        // Delegate to JavaScript implementation if present
        if (_specializer && _specializer.checkStatus)
            _specializer.checkStatus();
    }
    
    /**
     * Whether the service is currently considered "available."
     *
     * <p>The initial value is <code>false</code> until either a status check sets the
     * property to <code>true</code> or the property is initialized to <code>true</code> explicitly.</p>
     *
     * <p>Typically, this property is set by the <code>checkStatus()</code> implementation in a subclass or specializer,
     * but if the application has independent information about a service's availability (for example, a request just succeeded
     * or failed), the property can be set explicitly.</p>
     * 
     * @playerversion AIR 1.0
     */
    public function get available():Boolean
    {
        return _available;
    }
    
    public function set available(value:Boolean):void
    {
        var prevAvailable:Boolean = _available;
        _available = value;
        _statusTime = new Date();
        if ((prevAvailable != _available) || _notifyRegardless)
        {
            var code:String = _available ? "Service.available" : "Service.unavailable";
            var level:String = "status";
            dispatchEvent(new StatusEvent(StatusEvent.STATUS, false, false, code, level));
        }
        _notifyRegardless = false;
    }
    
    /**
     * The time of the last status update.
     * 
     * @playerversion AIR 1.0
     */
    public function get lastStatusUpdate():Date
    {
        return new Date(_statusTime.time);
    }
    
    /**
     * @inheritDoc
     * 
     * @playerversion AIR 1.0
     */
    public override function toString():String
    {
        // Delegate to JavaScript implementation if present
        if (_specializer && _specializer.toString)
            return _specializer.toString();
        return _toString();
    }
    
    private function _toString():String
    {
        return '[ServiceMonitor available="' + available + '"]';            
    }
    
    private var _delay:Number = 0; 
    private var _timer:Timer = new Timer(_delay);
    
    private var _available:Boolean = false;
    private var _notifyRegardless:Boolean = false;  // true after each start call
    
    private var _statusTime:Date = new Date();
    
    private static function makeForwarder(name:String):Function
    {
        return function forwarder(... args):* {
            // trace('delegating', name, this.__monitor__, args);
            return this.__monitor__[name].apply(this.__monitor__, args);
        }
    }
    
    private static function _initServiceMonitor(specializer:Object):ServiceMonitor
    {
        var svcMon:ServiceMonitor = new ServiceMonitor();
        specializer.__monitor__ = svcMon;
        svcMon._specializer = specializer;
        return svcMon;
    }
    
    private var _specializer:Object = null;
    
    /**
     * Adds public ServiceMonitor methods to a JavaScript constructor function's prototype.
     *
     * <p>Adds functions to the JavaScript constructor function's prototype that forward public
     * ServiceMonitor functions to the ServiceMonitor object. This approximates
     * a normal JavaScript subclass of the ActionScript base class.</p>
     *
     * <p>A JavaScript class specializing a ServiceMonitor would look like this:</p>
     * 
     * <listing>
     * // JavaScript Constructor function
     * function MyHTTPMonitor(url, method)
     * {
     *     // "that" variable makes "this" available in closures below
     *     var that = this;
     *     // Required initialization of the service monitor, returns the actual ServiceMonitor object.
     *     this.monitor = this.initServiceMonitor();
     *     // Initializes URLStream and event handlers.
     *     this._urlStream = new air.URLStream();
     *     this._urlRequest = new air.URLRequest(url);
     *     if (method)
     *     {
     *         this._urlRequest.method = method;
     *     }
     *     else
     *     {
     *         this._urlRequest.method = "GET";
     *     }
     *     function onStatus(event) {
     *         that.monitor.available = Number(event.status) == 200;
     *         that._urlStream.close();
     *     }
     *     function onError(event)
     *     {
     *         that.monitor.available = false;
     *         that._urlStream.close();
     *     }
     *     this._urlStream.addEventListener(air.HTTPStatusEvent.HTTP_RESPONSE_STATUS, onStatus);
     *     this._urlStream.addEventListener(air.SecurityErrorEvent.SECURITY_ERROR, onError);
     *     this._urlStream.addEventListener(air.IOErrorEvent.IO_ERROR, onError);
     * }
     *
     * // Augment JavaScript prototype with public methods from ServiceMonitor
     * air.ServiceMonitor.makeJavascriptSubclass(MyHTTPMonitor);
     *
     * // Implement specializer functions, just as you would when subclassing a JavaScript class
     * MyHTTPMonitor.prototype.checkStatus = function()
     * {
     *     air.trace('OVERRIDDEN checkStatus!', this);
     *     this._urlStream.load(this._urlRequest);
     * }
     * </listing>
     *
     * <p>To use the JavaScript class:</p>
     *
     * <listing>
     * var httpMon = new MyHTTPMonitor('http://www.adobe.com')
     * </listing>
     * 
     * <p>Be sure to load the AIRAliases.js and ServiceMonitor.swf files with <code>script</code> tags.</p>
     * 
     * @param proto The JavaScript object's <code>prototype</code> property. For example, if the JavaScript 
     * object that you are using to serve as a specializer object is named MyHTTPMonitor, pass 
     * <code>MyHTTPMonitor.prototype</code> as the value for this parameter.
     * 
     * @playerversion AIR 1.0
     */
    public static function makeJavascriptSubclass(constructorFunction:Object):void
    {
        var proto:Object = constructorFunction.prototype;
        var names:Array = [ 'start', 'stop', 
            'willTrigger', 'removeEventListener', 'addEventListener', 'dispatchEvent', 'hasEventListener' ];
        for each (var name:String in names)
        {
            proto[name] = makeForwarder(name);
        }
        
        proto.setAvailable = function(value:Boolean):void
        {
            this.__monitor__.available = value;
        }
        
        proto.getAvailable = function():Boolean
        {
            return this.__monitor__.available;
        }
        
        proto.toString = makeForwarder('_toString');
        
        proto.initServiceMonitor = function():*
        {
            return _initServiceMonitor(this);
        }
    }
    
}
}
