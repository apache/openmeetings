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
	 * The NCManagerNative class is a subclass of the NCManager class and supports 
	 * native bandwidth detection, which some Flash Video Streaming Service providers
	 * may support. Check with your FVSS provider to see whether they support native bandwidth
	 * detection. Native bandwidth detection means that the bandwidth detection is built in
	 * to the streaming server and performs better.
	 * 
	 * <p>When an NCManagerNative object is
	 * used, the main.asc file is not required on the server. If bandwidth detection is not required, 
	 * the NCManagerNative object allows
	 * connection to any version of the Flash Media Server (FMS) without the main.asc file.</p>
	 *
	 * <p>To use this instead of the default fl.video.NCManager, put the following code
	 * in Frame 1 of your FLA file:</p>
	 *
     * <listing version="3.0">
     * import fl.video.~~;
     * VideoPlayer.iNCManagerClass = fl.video.NCManagerNative;
     * </listing>
	 *
     * @see NCManager
     * @tiptext NCManagerNative class
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	public class NCManagerNative extends NCManager implements INCManager {

		include "ComponentVersion.as"

		// for new bandwidth detection

        /**
         * Creates a new NCManagerNative instance.
         * @tiptext NCManagerNative  constructor
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		public function NCManagerNative():void {
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
	 * Overridden to create ConnectClientNative instead of ConnectClient.
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
				_tryNC[i].client = new ConnectClientNative(this, _tryNC[i], i);
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
				cleanConns();
				// start bandwidth detection if necessary, otherwise start
				if (_autoSenseBW) {
					_nc.call("_checkbw", null);
				} else {
					onConnected(_nc, 0);
				}
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

	} // class NCManagerNative

} // package fl.video
