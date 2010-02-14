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
	import flash.utils.getTimer;

	use namespace flvplayback_internal;

	/**
	 * The NCManagerDynamicStream class is a subclass of the NCManager class designed to 
	 * support the Dynamic Streaming feature of Flash Media Server.  Please check with your 
	 * service provider or Content Delivery Network (CDN) to see if they support this feature.
	 * 
	 * <p>To use this instead of the default fl.video.NCManager interface, put the following code
	 * in Frame 1 of your FLA file:</p>
	 *
     * <listing version="3.0">
     * import fl.video.*;
     * VideoPlayer.iNCManagerClass = fl.video.NCManagerDynamicStream;
     * </listing>
	 *
     * @see NCManager
     * @tiptext NCManagerDynamicStream class
     * @langversion 3.0
     * @playerversion Flash 10
     * @playerversion AIR 1.5
     * @productversion FLVPlayback 2.5
	 */
	public class NCManagerDynamicStream extends NCManager implements INCManager {

		include "ComponentVersion.as"

		// for new bandwidth detection

        /**
         * Creates a new NCManagerDynamicStream instance.
         * 
         * @tiptext NCManagerDynamicStream  constructor
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		public function NCManagerDynamicStream():void {
		}

		/**
		 * Length of the stream, in milliseconds. After the <code>VideoPlayer.ncConnected()</code> method
		 * is called, if it returns undefined, <code>null</code> or less than 0, 
		 * then the VideoPlayer object knows that there is no stream length information. 
		 * If stream length information is returned, it overrides any existing steam length information 
		 * including information set by the <code>totalTime</code> parameter of the 
		 * <code>VideoPlayer.play()</code> method, the
		 * <code>VideoPlayer.load()</code> method or information received from the FLV file's metadata.
		 * 
         * @see INCManager#streamLength
         * @tiptext streamLength property
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		public override function get streamLength():Number {
			return NaN;
		}

		/**
		 * Overridden to create ConnectClientDynamicStream instead of ConnectClient.
		 * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
	 */
		flvplayback_internal override function connectRTMP():Boolean {
			//ifdef DEBUG
			//debugTrace("connectRTMP()");
			//endif

			// setup timeout
			_timeoutTimer.reset();
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
				_tryNC[i].client = new ConnectClientDynamicStream(this, _tryNC[i], i);
				_tryNC[i].addEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
			}

			nextConnect();
			return false;
		}

    /**
     * Overridden to avoid call to getStreamLength
     * @private
     * @tiptext onConnected method
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
     */
		flvplayback_internal override function onConnected(p_nc:NetConnection, p_bw:Number):void
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
			if(_fpcaps.dynamicStreaming && _fmscaps.dynamicStreaming && _streams != null){
				_isDynamicStream = true;				
			}
			
			_owner.ncConnected();
			
		}

        /**
         * overriden to call run() when _autoSenseBW is on, and to immediately
         * call onConnected() if it is not, instead of waiting for a call to
         * onBWDone from the server, like NCManager does.
         * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */

		flvplayback_internal override function connectOnStatus(e:NetStatusEvent):void {
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
				
				// attempt to get the FMS capabilities 
				var fmsvers:String;
				try{
					fmsvers = e.info.data.version;
				} catch(e:Error){
					fmsvers = "2,0,0,0";
				}
				
				_fmscaps = new FMSCapabilities(fmsvers);
				cleanConns();
				onConnected(_nc, 0);
				
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
         * netStatus event listener when reconnecting
         * 
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal override function reconnectOnStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("reconnectOnStatus: " + e.info.code);
			//endif
			if (e.info.code == "NetConnection.Connect.Success") {
				onReconnected();
			} else if ( (e.info.code == "NetConnection.Connect.Failed") ||
				        (e.info.code == "NetConnection.Connect.Rejected") ) {
				_nc = null;
				_ncConnected = false;
				_owner.ncReconnected();
			}
		}
		
		/**
		 * Builds the stream list required by the DynamicStream class in order to differentiate
		 * which streams it can use for switching.
		 *  
		 * @param dsi The DynamicStreamItem object to be passed in for playing.
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5
		 * 
		 */		
		public override function connectDynamicStream(dsi:*):void {
								
			var parseResults:ParseResults;
			var url:String;
			var protocolDetermined:Boolean = false;
	
			
			// get correct streamname
			url = dsi.uri;
			
			if (url != null && url != "") {
				if (url.charAt(url.length - 1) != "/") {
					url += "/";
				}
				parseResults = parseURL(url);
				_isRTMP = parseResults.isRTMP;
				protocolDetermined = true;
				_streamName = dsi.streams[0].name;
				if (_isRTMP) {
					_protocol = parseResults.protocol;
					_serverName = parseResults.serverName;
					_portNumber = parseResults.portNumber;
					_wrappedURL = parseResults.wrappedURL;
					_appName = parseResults.appName;
					if (_appName == null || _appName == "") {
						throw new VideoError(VideoError.INVALID_SOURCE, "Base RTMP URL must include application name: " + url);
					}
				}
			}
			
			_streams = new Array();
			
			for(var i:int=0; i< dsi.streams.length; i++){
				_streams[i] = {src:dsi.streams[i].name, bitrate:dsi.streams[i].rate * 1000};
			}
			
			connectRTMP();

		}

	} // class NCManagerDynamicStream

} // package fl.video
