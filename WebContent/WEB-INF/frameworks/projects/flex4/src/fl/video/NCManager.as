/*************************************************************************
*                       
* ADOBE SYSTEMS INCORPORATED
* Copyright 2004-2008 Adobe Systems Incorporated
* All Rights Reserved.
*
* NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the 
* terms of the Adobe license agreement accompanying it.  If you have received this file from a 
* source other than Adobe, then your use, modification, or distribution of it requires the prior 
* written permission of Adobe.
*
**************************************************************************/

package fl.video {

	import flash.net.*;
	import flash.events.TimerEvent;
	import flash.events.NetStatusEvent;
	import flash.utils.Timer;

	use namespace flvplayback_internal;

	/**
	 * Creates the <code>NetConnection</code> object for the VideoPlayer class. The NCManager class is a
	 * helper class for the user-facing VideoPlayer class.
	 *
	 * <p>The NCManager class searches a URL and assumes the following:</p>
	 * <ul>
	 * <li>If the URL string host starts with a valid Flash Media Server streaming protocol (such as rtmp://, rtmps://, or
	 * rtmpt://) it infers that the URL is streaming from Flash Media Server.</li>
	 * <li>If it does not stream from Flash Media Server and if the URL contains a question mark (?), it infers that the URL points to an SMIL file.</li>
	 * <li>If it does not stream from Flash Media Server and if it does not contain a question mark (?), the NCManager class
	 * checks to see whether it ends in .flv.  If it ends in .flv, it infers it is for a progressive download FLV. Otherwise,
	 * it is an SMIL file to download and parse.</li>
	 * </ul>
	 *
	 * @includeExample examples/NCManagerExample.as -noswf
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	public class NCManager implements INCManager {

		include "ComponentVersion.as"

		// my VideoPlayer
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _owner:VideoPlayer;

		// server connection info
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _contentPath:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _protocol:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _serverName:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _portNumber:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _wrappedURL:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _appName:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _streamName:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _streamLength:Number;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _streamWidth:int;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _streamHeight:int;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _streams:Array;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _isRTMP:Boolean;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _smilMgr:SMILManager;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _fpadMgr:FPADManager;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _fpadZone:Number;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _objectEncoding:uint;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _proxyType:String;
		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal var _bitrate:Number;

	/**
	 * Exposes the <code>fallbackServerName</code> property indirectly or directly.
	 * 
	 * <ul>
	 * <li>Indirectly&#8212;Supply a second &lt;meta base&gt; in SMIL.
	 * The <code>fallbackServerName</code> property uses that base attribute.
	 * (The <code>fallbackServerName</code> property uses the server name only, 
	 * nothing else.)</li>
	 *
	 * <li>Directly&#8212;Access the <code>ncMgr</code> property in 
	 * FLVPlayback or VideoPlayer and set the
         * <code>fallbackServerName</code> property or use
         * the <code>setProperty()</code> method.</li>
         * </ul>
         *
		 * @see #setProperty()
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public var fallbackServerName:String;

		// interval for xn timeout
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _timeoutTimer:Timer;

	/**
	 * The default timeout in milliseconds.
         * @see INCManager#timeout 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public const DEFAULT_TIMEOUT:uint = 60000;

		// bandwidth detection stuff
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _payload:Number;
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _autoSenseBW:Boolean;

		// info on successful xn
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _nc:NetConnection;
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _ncUri:String;
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _ncConnected:Boolean;

		// info on mult xns we try
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _tryNC:Array;
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _tryNCTimer:Timer;

		// Counter that tracks the next connection to use in _tryNC array
	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal var _connTypeCounter:uint;

		/**
		 * @private
		 * 
		 * @langversion 3.0
		 * @playerversion Flash 10.0.0 
		 */
		flvplayback_internal var _isDynamicStream:Boolean = false;
		
		/**
		 * @private
		 * 
		 * @langversion 3.0
		 * @playerversion Flash 9.0.28.0 
		 */		
		flvplayback_internal var _fpcaps:FPMediaCapabilities;
		
		/**
		 * @private
		 * 
		 * @langversion 3.0
		 * @playerversion Flash 9.0.28.0 
		 */		
		flvplayback_internal var _fmscaps:FMSCapabilities;

        /**
         * Creates a new NCManager instance.
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		public function NCManager()	{
			// user settable properties
			_fpadZone = NaN;
			_objectEncoding = ObjectEncoding.AMF0;
			_proxyType = "best";

			// intervals
			_timeoutTimer = new Timer(DEFAULT_TIMEOUT);
			_timeoutTimer.addEventListener(TimerEvent.TIMER, this._onFMSConnectTimeOut);
			_tryNCTimer = new Timer(1500, 1);
			_tryNCTimer.addEventListener(TimerEvent.TIMER, this.nextConnect);

			initNCInfo();
			initOtherInfo();

			_nc = null;
			_ncConnected = false;
			
			_fpcaps = new FPMediaCapabilities();

		}

	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal function initNCInfo():void {
			_isRTMP = false;
			_serverName = null;
			_wrappedURL = null;
			_portNumber = null;
			_appName = null;
		}

	/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal function initOtherInfo():void {
			_contentPath = null;
			_streamName = null;
			_streamWidth = -1;
			_streamHeight = -1;
			_streamLength = NaN;
			_streams = null;
			_autoSenseBW = false;

			_payload = 0;
			_connTypeCounter = 0;
			cleanConns();
		}

	/**
	 * @copy INCManager#timeout
         * @see INCManager#timeout 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function get timeout():uint {
			return _timeoutTimer.delay;
		}

		/**
         * @private (setter)
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function set timeout(t:uint):void {
			_timeoutTimer.delay = t;
		}

		/**
		 * When streaming from Flash Media Server (FMS), the <code>bitrate</code> property
		 * returns the value calculated from autodetection,
		 * not the value set through the <code>bitrate()</code> property.
		 *
         * @see INCManager#bitrate 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get bitrate():Number {
			return _bitrate;
		}

		/**
		 * @private
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function set bitrate(b:Number):void {
			if (!_isRTMP) {
				_bitrate = b;
			}
		}

	/**
	 * @copy INCManager#videoPlayer
         * @see INCManager#videoPlayer 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get videoPlayer():VideoPlayer {
			return _owner;
		}

		/**
         * @private (setter)
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function set videoPlayer(v:VideoPlayer):void {
			_owner = v;
		}

	/**
	 * @copy INCManager#netConnection
         * @see INCManager#netConnection 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
	public function get netConnection():NetConnection {
			return _nc;
		}

	/**
	 * @copy INCManager#streamName
         * @see INCManager#streamName 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get streamName():String {
			return _streamName;
		}

		/**
		 * @copy INCManager#streams
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get streams():Array {
			return _streams;
		}

	/**
	 * @copy INCManager#isRTMP
     * @see INCManager#isRTMP  
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
     */
		public function get isRTMP():Boolean {
			return _isRTMP;
		}
		
	/**
	 * @copy INCManager#streamLength
         * @see INCManager#streamLength  
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get streamLength():Number {
			return _streamLength;
		}

	/**
	 * @copy INCManager#streamWidth
         * @see INCManager#streamWidth 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function get streamWidth():int {
			return _streamWidth;
		}

	/**
	 * @copy INCManager#streamHeight
         * @see INCManager#streamHeight 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function get streamHeight():int {
			return _streamHeight;
		}

		/**
		 * Allows getting of the <code>fallbackServerName</code>, <code>fpadZone</code>, <code>objectEncoding</code>,
		 * and <code>proxyType</code> properties.  See <code>setProperty()</code> for
		 * an explanation of these properties.
		 *
		 * @param propertyName The name of the property that the <code>getProperty</code> 
		 * method is calling.
		 * 
		 * @see #setProperty()
		 * @see #fallbackServerName
         * @return String representation of the fallbackServerName, fpadZone, objectEncoding, and proxyType properties.
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function getProperty(propertyName:String):* {
			switch (propertyName) {
			case "fallbackServerName":
				return fallbackServerName;
			case "fpadZone":
				return _fpadZone;
			case "objectEncoding":
				return _objectEncoding;
			case "proxyType":
				return _proxyType;
			default:
				throw new VideoError(VideoError.UNSUPPORTED_PROPERTY, propertyName);
			}
		}
		
		/**
		 * A Boolean value indicating whether dynamic streaming is enabled or not
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5.
		 * 
		 */		
		public function get isDynamicStream():Boolean {
			return _isDynamicStream;
		}

		/**
		 * Allows setting of the <code>fallbackServerName</code>, <code>fpadZone</code>, <code>objectEncoding</code>,
		 * and <code>proxyType</code> properties.
		 *
		 * <p>The <code>fallbackServerName</code> property specifies a Flash Media Server (FMS) URL
		 * to be used if the primary server address derived from the URL passed
		 * into <code>connectToURL()</code> cannot be reached.  This property can also be set
		 * directly through the <code>fallbackServerName</code> property.</p>
		 *
	 * <p>The <code>fpadZone</code> property specifies the <code>fpadZone</code> property for Flash Media Server (FMS).
	 * If the returned value is not a number (NaN),
         * then no zone is set.
         * The <code>fpadZone</code> property must be set before 
	 * the connection process begins to have effect.
         * If you do not want to set a zone, set the <code>fpadZone</code> property to <code>NaN</code>.</p>
		 * Default is <code>NaN</code>.
		 *
		 * <p>The <code>objectEncoding</code> property specifies the value to be used for the <code>objectEncoding</code>
		 * property on <code>NetConnection</code> instances.  Default is <code>flash.net.ObjectEncoding.AMF0</code>.</p>
		 *
		 * <p>The <code>proxyType</code> property specifies the value to be used for the <code>proxyType</code>
		 * property on <code>NetConnection</code> instances.  Default is "best".</p>
		 *
		 * @param propertyName The name of the property that the <code>setProperty</code> 
		 * method is calling.
		 *
		 * @param value The property value.
		 *
		 * @see #getProperty()
		 * @see #fallbackServerName
		 *
		 * @langversion 3.0
	 	 * @playerversion Flash 9.0.28.0
		 */
		public function setProperty(propertyName:String, value:*):void {
			switch (propertyName) {
			case "fallbackServerName":
				fallbackServerName = String(value);
				break;
			case "fpadZone":
				_fpadZone = Number(value);
				break;
			case "objectEncoding":
				_objectEncoding = uint(value);
				break;
			case "proxyType":
				_proxyType = String(value);
				break;
			default:
				throw new VideoError(VideoError.UNSUPPORTED_PROPERTY, propertyName);
			}
		}


	/**
	 * @copy INCManager#connectToURL()
         * @see INCManager#connectToURL() 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function connectToURL(url:String):Boolean {
			//ifdef DEBUG
			//debugTrace("connectToURL(" + url + ")");
			//endif

			// init
			initOtherInfo();
			_contentPath = url;
			if (_contentPath == null || _contentPath == "") {
				throw new VideoError(VideoError.INVALID_SOURCE);
			}

			// parse URL to determine what to do with it
			var parseResults:ParseResults = parseURL(_contentPath);
			if (parseResults.streamName == null || parseResults.streamName == "") {
				throw new VideoError(VideoError.INVALID_SOURCE, url);
			}

			// connect to either rtmp or http or download and parse smil
			var canReuse:Boolean;
			if (parseResults.isRTMP) {
				canReuse = canReuseOldConnection(parseResults);
				_isRTMP = true;
				_protocol = parseResults.protocol;
				_streamName = parseResults.streamName;
				_serverName = parseResults.serverName;
				_wrappedURL = parseResults.wrappedURL;
				_portNumber = parseResults.portNumber;
				_appName = parseResults.appName;
				if ( _appName == null || _appName == "" ||
				     _streamName == null || _streamName == "" ) {
					throw new VideoError(VideoError.INVALID_SOURCE, url);
				}
				_autoSenseBW = (_streamName.indexOf(",") >= 0);
				return (canReuse || connectRTMP());
			} else {
				var name:String = parseResults.streamName;
				if ( name.indexOf("?") < 0 &&
				     (name.length < 4 || name.slice(-4).toLowerCase() != ".txt") &&
				     (name.length < 4 || name.slice(-4).toLowerCase() != ".xml") &&
				     (name.length < 5 || name.slice(-5).toLowerCase() != ".smil") ) {
					canReuse = canReuseOldConnection(parseResults);
					_isRTMP = false;
					_streamName = name;
					return (canReuse || connectHTTP());
				}
				if (name.indexOf("/fms/fpad") >= 0) {
					try {
						return connectFPAD(name);
					} catch (err:Error) {
						// just use SMILManager if there is any error
						//ifdef DEBUG
						//debugTrace("fpad error: " + err);
						//endif
					}
				}
				_smilMgr = new SMILManager(this);
				return _smilMgr.connectXML(name);
			}
		}

		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5
		 */
		public function connectDynamicStream(dsi:*):void {
			return;
		}

		/**
		 * @copy INCManager#connectAgain()
         * @see INCManager#connectAgain() 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function connectAgain():Boolean {
			//ifdef DEBUG
			//debugTrace("connectAgain()");
			//endif

			var slashIndex:int = _appName.indexOf("/");
			if (slashIndex < 0) {
				// return the appName and streamName back to original form
				// so we can start this process all over again with the
				// fallback server if necessary
				slashIndex = _streamName.indexOf("/");
				if (slashIndex >= 0) {
					_appName += "/";
					_appName += _streamName.slice(0, slashIndex);
					_streamName = _streamName.slice(slashIndex + 1);
				}
				return false;
			}

			var newStreamName:String = _appName.slice(slashIndex + 1);
			newStreamName += "/";
			newStreamName += _streamName;
			_streamName = newStreamName;
			_appName = _appName.slice(0, slashIndex);
			close();
			_payload = 0;
			_connTypeCounter = 0;
			cleanConns();
			connectRTMP();
			return true;
		}

	/**
	 * @copy INCManager#reconnect()
         * @see INCManager#reconnect() 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function reconnect():void {
			//ifdef DEBUG
			//debugTrace("reconnect()");
			//endif

			if (!_isRTMP) {
				throw new Error("Cannot call reconnect on an http connection");
			}
			_nc.client = new ReconnectClient(this);
			_nc.addEventListener(NetStatusEvent.NET_STATUS, reconnectOnStatus);
			//ifdef DEBUG
			//debugTrace("_ncUri = " + _ncUri);
			//endif
			_nc.connect(_ncUri, false);
		}

	/**
	 * Dispatches reconnect event, called by internal class method
	 * <code>ReconnectClient.onBWDone()</code>
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal function onReconnected():void {
			_ncConnected = true;
			_owner.ncReconnected();
		}

	/**
	 * @copy INCManager#close()
         * @see INCManager#close() 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function close():void {
			if (_nc) {
				_nc.close();
				_ncConnected = false;
			}
		}

	/**
	 *
	 * @copy INCManager#helperDone()
	 *
         * @see INCManager#helperDone() 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		public function helperDone(helper:Object, success:Boolean):void {
			if (!success) {
				_nc = null;
				_ncConnected = false;
				_owner.ncConnected();
				_smilMgr = null;
				_fpadMgr = null;
				return;
			}

			var parseResults:ParseResults;
			var url:String;
			var protocolDetermined:Boolean = false;

			if (helper == _fpadMgr) {
				url = _fpadMgr.rtmpURL;
				_fpadMgr = null;
				parseResults = parseURL(url);
				_isRTMP = parseResults.isRTMP;
				_protocol = parseResults.protocol;
				_serverName = parseResults.serverName;
				_portNumber = parseResults.portNumber;
				_wrappedURL = parseResults.wrappedURL;
				_appName = parseResults.appName;
				_streamName = parseResults.streamName;

				// if fpad autodetect is set up and we used the fpad
				// xml instead, we need to set fpadZone to NaN or we
				// will autodetect on top of our xml detection and
				// things will not work!
				var fpadZoneCached:Number = _fpadZone;
				_fpadZone = NaN;
				connectRTMP();
				// after connecting, set fpadZone back to previous
				// value
				_fpadZone = fpadZoneCached;

				return;
			}

			if (helper != _smilMgr) return;
			
			// success!

			// grab width and height
			_streamWidth = _smilMgr.width;
			_streamHeight = _smilMgr.height;
			
			// get correct streamname
			url = _smilMgr.baseURLAttr[0];
			
			if (url != null && url != "") {
				if (url.charAt(url.length - 1) != "/") {
					url += "/";
				}
				parseResults = parseURL(url);
				_isRTMP = parseResults.isRTMP;
				protocolDetermined = true;
				_streamName = parseResults.streamName;
				if (_isRTMP) {
					_protocol = parseResults.protocol;
					_serverName = parseResults.serverName;
					_portNumber = parseResults.portNumber;
					_wrappedURL = parseResults.wrappedURL;
					_appName = parseResults.appName;
					if (_appName == null || _appName == "") {
						_smilMgr = null;
						throw new VideoError(VideoError.INVALID_XML, "Base RTMP URL must include application name: " + url);
					}
					if (_smilMgr.baseURLAttr.length > 1) {
						parseResults = parseURL(_smilMgr.baseURLAttr[1]);
						if (parseResults.serverName != null) {
							fallbackServerName = parseResults.serverName;
						}
					}
				}
			}
			_streams = _smilMgr.videoTags;
			_smilMgr = null;
			for (var i:uint = 0; i < _streams.length; i++) {
				url = _streams[i].src;
				parseResults = parseURL(url);
				if (!protocolDetermined) {
					_isRTMP = parseResults.isRTMP;
					protocolDetermined = true;
					if (_isRTMP) {
						_protocol = parseResults.protocol;
						if (_streams.length > 1) {
							throw new VideoError(VideoError.INVALID_XML, "Cannot switch between multiple absolute RTMP URLs, must use meta tag base attribute.");
						}
						_serverName = parseResults.serverName;
						_portNumber = parseResults.portNumber;
						_wrappedURL = parseResults.wrappedURL;
						_appName = parseResults.appName;
						if (_appName == null || _appName == "") {
							throw new VideoError(VideoError.INVALID_XML, "Base RTMP URL must include application name: " + url);
						}
					} else if (parseResults.streamName.indexOf("/fms/fpad") >= 0 && _streams.length > 1) {
						throw new VideoError(VideoError.INVALID_XML, "Cannot switch between multiple absolute fpad URLs, must use meta tag base attribute.");
					}
				} else if ( _streamName != null && _streamName != "" &&
				            !parseResults.isRelative && _streams.length > 1 ) {
					throw new VideoError(VideoError.INVALID_XML, "When using meta tag base attribute, cannot use absolute URLs for video or ref tag src attributes.");
				}
				_streams[i].parseResults = parseResults;
			}
			_autoSenseBW = _streams.length > 1;

			if (!_autoSenseBW) {
				if (_streamName != null) {
					_streamName += _streams[0].parseResults.streamName;
				} else {
					_streamName = _streams[0].parseResults.streamName;
				}
				if (_isRTMP && _streamName.substr(-4).toLowerCase() == ".flv") {
					_streamName = _streamName.substr(0, _streamName.length - 4);
				}
				_streamLength = _streams[0].dur;
			}
			if (_isRTMP) {
				connectRTMP();
			} else if (_streamName != null && _streamName.indexOf("/fms/fpad") >= 0) {
				connectFPAD(_streamName);
			} else {
				if (_autoSenseBW) bitrateMatch();
				connectHTTP();
				_owner.ncConnected();
			}
		}

	/**
	 * Matches bitrate with stream.
	 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function bitrateMatch():void {
			var checkBitrate:Number = _bitrate;
			if (isNaN(checkBitrate)) {
				checkBitrate = 0;
			}
			var whichStream:uint = _streams.length;
			for (var j:uint = 0; j < _streams.length; j++) {
				if (isNaN(_streams[j].bitrate) || checkBitrate >= _streams[j].bitrate) {
					whichStream = j;
					break;
				}
			}
			if (whichStream == _streams.length) {
				throw new VideoError(VideoError.NO_BITRATE_MATCH);
			}
			if (_streamName != null) {
				_streamName += _streams[whichStream].src;
			} else {
				_streamName = _streams[whichStream].src;
			}
			if (_isRTMP && _streamName.substr(-4).toLowerCase() == ".flv") {
				_streamName = _streamName.substr(0, _streamName.length - 4);
			}
			_streamLength = _streams[whichStream].dur;
		}

		/**
		 * Parses URL to determine if it is http or rtmp.  If it is rtmp,
		 * breaks it into pieces to extract server URL and port, application
		 * name and stream name.  If .flv is at the end of an rtmp URL, it
		 * will be stripped off.
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseURL(url:String):ParseResults {
			//ifdef DEBUG
			//debugTrace("parseURL()");
			//endif

			var parseResults:ParseResults = new ParseResults();
			
			// get protocol
			var startIndex:int = 0;
			var endIndex:int = url.indexOf(":/", startIndex);
			if (endIndex >= 0) {
				endIndex += 2;
				parseResults.protocol = url.slice(startIndex, endIndex).toLowerCase();
				parseResults.isRelative = false;
			} else {
				parseResults.isRelative = true;
			}
			
			if ( parseResults.protocol != null &&
			     ( parseResults.protocol == "rtmp:/" ||
			       parseResults.protocol == "rtmpt:/" ||
			       parseResults.protocol == "rtmps:/" ||
			       parseResults.protocol == "rtmpe:/" ||
			       parseResults.protocol == "rtmpte:/" ||
				   parseResults.protocol == "rtmfp:/" ) ) {
				parseResults.isRTMP = true;
				
				startIndex = endIndex;

				if (url.charAt(startIndex) == '/') {
					startIndex++;
					// get server (and maybe port)
					var colonIndex:int = url.indexOf(":", startIndex);
					var slashIndex:int = url.indexOf("/", startIndex);
					if (slashIndex < 0) {
						if (colonIndex < 0) {
							parseResults.serverName = url.slice(startIndex);
						} else {
							endIndex = colonIndex;
							parseResults.portNumber = url.slice(startIndex, endIndex);
							startIndex = endIndex + 1;
							parseResults.serverName = url.slice(startIndex);
						}
						return parseResults;
					}
					if (colonIndex >= 0 && colonIndex < slashIndex) {
						endIndex = colonIndex;
						parseResults.serverName = url.slice(startIndex, endIndex);
						startIndex = endIndex + 1;
						endIndex = slashIndex;
						parseResults.portNumber = url.slice(startIndex, endIndex);
					} else {
						endIndex = slashIndex;
						parseResults.serverName = url.slice(startIndex, endIndex);
					}
					startIndex = endIndex + 1;
				}

				// handle wrapped RTMP servers bit recursively, if it is there
				if (url.charAt(startIndex) == '?') {
					var subURL:String = url.slice(startIndex + 1);
					var subParseResults:ParseResults = parseURL(subURL);
					if (subParseResults.protocol == null || !subParseResults.isRTMP) {
						throw new VideoError(VideoError.INVALID_SOURCE, url);
					}
					parseResults.wrappedURL = "?";
					parseResults.wrappedURL += subParseResults.protocol;
					if (subParseResults.serverName != null) {
						parseResults.wrappedURL += "/";
						parseResults.wrappedURL +=  subParseResults.serverName;
					}
					if (subParseResults.portNumber != null) {
						parseResults.wrappedURL += ":" + subParseResults.portNumber;
					}
					if (subParseResults.wrappedURL != null) {
						parseResults.wrappedURL += "/";
						parseResults.wrappedURL +=  subParseResults.wrappedURL;
					}
					parseResults.appName = subParseResults.appName;
					parseResults.streamName = subParseResults.streamName;
					return parseResults;
				}
				
				// get application name
				endIndex = url.indexOf("/", startIndex);
				if (endIndex < 0) {
					parseResults.appName = url.slice(startIndex);
					return parseResults;
				}
				parseResults.appName = url.slice(startIndex, endIndex);
				startIndex = endIndex + 1;

				// check for instance name to be added to application name
				endIndex = url.indexOf("/", startIndex);
				if (endIndex < 0) {
					parseResults.streamName = url.slice(startIndex);
					// strip off .flv if included
					if (parseResults.streamName.slice(-4).toLowerCase() == ".flv") {
						parseResults.streamName = parseResults.streamName.slice(0, -4);
					}
					return parseResults;
				}
				parseResults.appName += "/";
				parseResults.appName += url.slice(startIndex, endIndex);
				startIndex = endIndex + 1;
					
				// get flv name
				parseResults.streamName = url.slice(startIndex);
				// strip off .flv if included
				if (parseResults.streamName.slice(-4).toLowerCase() == ".flv") {
					parseResults.streamName = parseResults.streamName.slice(0, -4);
				}
				
			} else {
				// is http, just return the full url received as streamName
				parseResults.isRTMP = false;
				parseResults.streamName = url;
			}
			return parseResults;
		}

		/**
		 * <p>Compares connection info with previous NetConnection,
		 * will reuse existing connection if possible.</p>
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function canReuseOldConnection(parseResults:ParseResults):Boolean {
			// no reuse if no prior connection
			if (_nc == null || !_ncConnected) return false;

			// http connection
			if (!parseResults.isRTMP) {
				// can reuse if prev connection was http
				if (!_isRTMP) return true;
				// cannot reuse if was rtmp--close
				_owner.close();
				_nc = null;
				_ncConnected = false;
				initNCInfo();
				return false;
			}

			// rtmp connection
			if (_isRTMP) {
				if ( parseResults.serverName == _serverName && parseResults.appName == _appName &&
				     parseResults.protocol == _protocol && parseResults.portNumber == _portNumber &&
				     parseResults.wrappedURL == _wrappedURL ) {
					return true;
				}
				// cannot reuse this rtmp--close
				_owner.close();
				_nc = null;
				_ncConnected = false;
			}

			initNCInfo();
			return false;
		}

		/**
		 * <p>Handles creating <code>NetConnection</code> instance for
		 * progressive download of FLV via http.</p>
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function connectHTTP():Boolean {
			//ifdef DEBUG
			//debugTrace("connectHTTP()");
			//endif

			_nc = new NetConnection();
			_nc.connect(null);
			_ncConnected = true;
			return true;
		}

		/**
		 * <p>Top level function for creating <code>NetConnection</code>
		 * instance for streaming playback of FLV via rtmp.  Actually
		 * tries to create several different connections using different
		 * protocols and ports in a pipeline, so multiple connection
		 * attempts may be occurring simultaneously, and will use the
		 * first one that connects successfully.</p>
		 * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function connectRTMP():Boolean {
			//ifdef DEBUG
			//debugTrace("connectRTMP()");
			//endif

			// setup timeout
			_timeoutTimer.stop();
			_timeoutTimer.start();

			_tryNC = new Array();
			var numXns:int = (_protocol == "rtmp:/" || _protocol == "rtmpe:/") ? 2 : 1;
			for (var i:uint = 0; i < numXns; i++) {
				//ifdef DEBUG
				//debugTrace("Creating connection " + i);
				//endif
				_tryNC[i] = new NetConnection();
				_tryNC[i].objectEncoding = _objectEncoding;
				_tryNC[i].proxyType = _proxyType;
				if (!isNaN(_fpadZone)) {
					_tryNC[i].fpadZone = _fpadZone;
				}
				_tryNC[i].client = new ConnectClient(this, _tryNC[i], i);
				_tryNC[i].addEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
			}

			nextConnect();
			return false;
		}

		/**
		 * <p>Top level function for downloading fpad XML from FMS 2.0
		 * server.  Creates and kicks off a FPADManager instance
		 * which does all the work.</p>
		 * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function connectFPAD(url:String):Boolean {
			//ifdef DEBUG
			//debugTrace("connectFPAD( " + url + ")");
			//endif

			// extract uri from requesting url
			var results:Object = /^(.+)(\?|\&)(uri=)([^&]+)(\&.*)?$/.exec(url);
			if (results == null) {
				throw new VideoError(VideoError.INVALID_SOURCE, "fpad url must include uri parameter: " + url);
			}
			var urlPrefix:String = results[1] + results[2];
			var uriParam:String = results[4];
			var urlSuffix:String = (results[5] == undefined) ? "" : results[5];

			var uriParamParseResults:ParseResults = parseURL(uriParam);
			if (!uriParamParseResults.isRTMP) {
				throw new VideoError(VideoError.INVALID_SOURCE, "fpad url uri parameter must be rtmp url: " + url);
			}

			_fpadMgr = new FPADManager(this);
			return _fpadMgr.connectXML(urlPrefix, uriParam, urlSuffix, uriParamParseResults);
		}


		/**
		 * <p>Does work of trying to open rtmp connections.  Called either
		 * by <code>connectRTMP</code> or on an interval set up in
		 * that method.</p>
		 *
		 * <p>For creating rtmp connections.</p>
		 *
		 * @see #connectRTMP() 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function nextConnect(e:TimerEvent=null):void {
			//ifdef DEBUG
			//debugTrace("nextConnect()");
			//endif

			var protocol:String;
			var port:String;
			if (_connTypeCounter == 0) {
				protocol = _protocol;
				port = _portNumber;
			} else {
				port = null;
				if (_protocol == "rtmp:/") {
					protocol = "rtmpt:/"
				} else if (_protocol == "rtmpe:/") {
					protocol = "rtmpte:/"
				} else {
					_tryNC.pop();
					return;
				}
			}
			var xnURL:String = protocol + ((_serverName == null) ? "" : "/" + _serverName + ((port == null) ? "" : (":" + port)) + "/") + ((_wrappedURL == null) ? "" : _wrappedURL + "/") + _appName;
			//ifdef DEBUG
			//debugTrace( "_tryNC[" + _connTypeCounter + "] connecting to room: " + xnURL );
			//endif
			_tryNC[_connTypeCounter].client.pending = true;
			_tryNC[_connTypeCounter].connect( xnURL, _autoSenseBW);
			if (_connTypeCounter < (_tryNC.length-1)) {
				_connTypeCounter++;
				_tryNCTimer.reset();
				_tryNCTimer.start();
			}
		}

		/**
		 * <p>Stops all intervals, closes all unneeded connections, and other
		 * cleanup related to the <code>connectRTMP</code> strategy of
		 * pipelining connection attempts to different protocols and
		 * ports.</p>
		 *
		 * <p>For creating rtmp connections.</p>
		 *
		 * @see #connectRTMP()
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function cleanConns():void {
			//ifdef DEBUG
			//debugTrace("cleanConns()");
			//endif

			_tryNCTimer.reset();
			if (_tryNC != null) {
				for (var i:uint = 0; i < _tryNC.length; i++) {
					if (_tryNC[i] != null) {
						//ifdef DEBUG
						//debugTrace("_tryNC[" + i + "] = " + _tryNC[i]);
						//endif
						_tryNC[i].removeEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
						if (_tryNC[i].client.pending) {
							_tryNC[i].addEventListener(NetStatusEvent.NET_STATUS, disconnectOnStatus);
						} else {
							_tryNC[i].close();
						}
					}
					_tryNC[i] = null;
				}
				_tryNC = null;
			}
		}

		/**
		 * <p>Starts another pipelined connection attempt with
		 * <code>connectRTMP</code> with the fallback server.</p>
		 *
		 * <p>For creating rtmp connections.</p>
		 *
		 * @see #connectRTMP()
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function tryFallBack():void {
			//ifdef DEBUG
			//debugTrace("tryFallBack()");
			//endif

			if (_serverName == fallbackServerName || fallbackServerName == null) {
				//ifdef DEBUG
				//debugTrace("Already tried to fall back!");
				//endif
				//it's not connected
				_nc = null;
				_ncConnected = false;
				_owner.ncConnected();
			} else {
				_connTypeCounter = 0;
				cleanConns();
				_serverName = fallbackServerName;
				//ifdef DEBUG
				//debugTrace("connect: " + _serverName);
				//endif
				connectRTMP();
			}
		}

		/**
		 * <p>Starts another pipelined connection attempt with
		 * <code>connectRTMP</code> with the fallback server.</p>
		 *
		 * <p>For creating rtmp connections.</p>
		 *
		 * @see #connectRTMP()
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function onConnected(p_nc:NetConnection, p_bw:Number):void
		{
			//ifdef DEBUG
			//debugTrace("onConnected()");
			//endif

			// avoid timeout
			_timeoutTimer.stop();

			// ditch these now unneeded functions and listeners
			p_nc.removeEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);

			// store pointers to the successful xn and uri
			_nc = p_nc;
			_ncUri = _nc.uri;
			_ncConnected = true;

			if (_autoSenseBW) {
				_bitrate = p_bw * 1024;
				if (_streams != null) {
					bitrateMatch();
				} else {
					var sSplit:Array = _streamName.split(",");
					// remove leading and trailing whitespace from string
					for (var i:uint = 0; i < sSplit.length; i+=2) {
						var sName:String = stripFrontAndBackWhiteSpace(sSplit[i]);
						if (i + 1 < sSplit.length) {
							// If we have less bw than the next threshold or if
							// there isn't another threshold (last string)
							if (p_bw <= Number(sSplit[i+1])) {
								_streamName = sName;
								break;
							}
						} else {
							_streamName = sName;
							break;
						}
					} // for
					// strip off .flv if included
					if (_streamName.slice(-4).toLowerCase() == ".flv") {
						_streamName = _streamName.slice(0, -4);
					}
				}
			}

			// if we need to get the stream length from the server, do it here
			if (!_owner.isLive && isNaN(_streamLength)) {
				_nc.call("getStreamLength", new Responder(getStreamLengthResult), _streamName);
			} else {
				_owner.ncConnected();
			}
		}

		/**
		 * netStatus event listener when connecting
		 * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function connectOnStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("_tryNC["+e.target.client.connIndex+"].onStatus: " + e.info.code);
			//var stuff;
			//for (stuff in e.info) {
			//	debugTrace("e.info[" + stuff + "] = " + e.info[stuff]);
			//}
			//endif
			e.target.client.pending = false;
			
			if (e.info.code == "NetConnection.Connect.Success") {
				//ifdef DEBUG
				//debugTrace( "Connection " + e.target.uri + " succeeded!" );
				//endif
				_nc = _tryNC[e.target.client.connIndex];	
				cleanConns();
			} else if (e.info.code == "NetConnection.Connect.Rejected" && e.info.ex != null && e.info.ex.code == 302) {
				_connTypeCounter = 0;
				cleanConns();
				var parseResults:ParseResults = parseURL(e.info.ex.redirect);
				if (parseResults.isRTMP) {
					_protocol = parseResults.protocol;
					_serverName = parseResults.serverName;
					_wrappedURL = parseResults.wrappedURL;
					_portNumber = parseResults.portNumber;
					_appName = parseResults.appName;
					if (parseResults.streamName != null) {
						_appName += ("/" + parseResults.streamName);
					}
					connectRTMP();
				} else {
					tryFallBack();
				}
			} else if ( ( (e.info.code == "NetConnection.Connect.Failed") ||
						  (e.info.code == "NetConnection.Connect.Rejected") ) &&
						( e.target.client.connIndex == (_tryNC.length - 1) ) ) {
				// Try rearranging the app URL, then the fallbackServer
				if (!connectAgain()) {
					tryFallBack();
				}
			} else {
				//ifdef DEBUG
				//debugTrace( "Connection " + e.target.uri + " onStatus:" + e.info.code);
				//endif
			}
		}

        /**
         * @private
         * netStatus event listener when reconnecting
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function reconnectOnStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("reconnectOnStatus: " + e.info.code);
			//endif
			if ( (e.info.code == "NetConnection.Connect.Failed") ||
				 (e.info.code == "NetConnection.Connect.Rejected") ) {
				// Try the fallbackServer
				_nc = null;
				_ncConnected = false;
				_owner.ncReconnected();
			}
		}

        /**
         * @private
         *
		 * netStatus event listener for disconnecting extra
         * NetConnections that were opened in parallel
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function disconnectOnStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("disconnectOnStatus: " + e.info.code);
			//endif
			if (e.info.code == "NetConnection.Connect.Success") {
				e.target.removeEventListener(NetStatusEvent.NET_STATUS, disconnectOnStatus);
				//ifdef DEBUG
				//debugTrace("Closing myself");
				//endif
				e.target.close();
			}
		}

        /**
         * @private
         *
		 * Responder function to receive streamLength result from
         * server after making rpc
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function getStreamLengthResult(length:Number):void {
			//ifdef DEBUG
			//debugTrace("getStreamLengthResult(" + length + ")");
			//endif
			if (length > 0) _streamLength = length;
			_owner.ncConnected();
		}

        /**
         * @private
         *
		 * Called on interval to timeout all connection attempts.
		 *
		 * <p>For creating rtmp connections.</p>
		 *
		 * @see #connectRTMP()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function _onFMSConnectTimeOut(e:TimerEvent=null):void
		{
			//ifdef DEBUG
			//debugTrace("_onFMSConnectTimeOut()");
			//endif
			cleanConns();
			_nc = null;
			_ncConnected = false;
			if (!connectAgain()) {
				_owner.ncConnected();
			}
		}

		/**
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal static function stripFrontAndBackWhiteSpace(p_str:String):String
		{
			var i:uint;
			var l:uint = p_str.length;
			var startIndex:int = 0
			var endIndex:int = l;
			for (i = 0; i < l; i++) {
				switch (p_str.charCodeAt(i)) {
				case 9: // tab
				case 10: // new line
				case 13: // carriage return
				case 32: // space
					continue;
				}
				startIndex = i;
				break;
			}

			for (i = l; i >= 0; i--) {
				switch (p_str.charCodeAt(i)) {
				case 9: // tab
				case 10: // new line
				case 13: // carriage return
				case 32: // space
					continue;
				}
				endIndex = i + 1;
				break;
			}

			if (endIndex <= startIndex) {
				return "";
			}
			return p_str.slice(startIndex, endIndex);
		}

		//ifdef DEBUG
		//public function debugTrace(s:*):void
		//{
		//	if (_owner != null) {
		//		_owner.debugTrace("#NCManager# " + s);
		//	}
		//}
		//endif
	} // class NCManager

} // package fl.video
