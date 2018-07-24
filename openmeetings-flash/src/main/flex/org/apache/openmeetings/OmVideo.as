/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings {
import flash.display.Graphics;
import flash.events.AsyncErrorEvent;
import flash.events.NetStatusEvent;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.external.ExternalInterface;
import flash.media.Camera;
import flash.media.H264Level;
import flash.media.H264Profile;
import flash.media.H264VideoStreamSettings;
import flash.media.Microphone;
import flash.media.Video;
import flash.media.VideoStreamSettings;
import flash.media.SoundTransform;
import flash.net.NetConnection;
import flash.net.NetStream;
import flash.net.Responder;
import mx.core.UIComponent;

public class OmVideo {
	public static const CODEC_H264:String = "h264";
	public static const PLAY:String = "play";
	public static const BROADCAST:String = "broadcast";
	public static const RECORD:String = "record";
	public static const LIVE:String = "live";
	private var vid:Video;
	private var ui:UIComponent;
	private var nc:NetConnection;
	private var ns:NetStream;
	private var mic:Microphone;
	private var cam:Camera;
	public var width:int;
	public var height:int;
	private var mode:String;
	private var params:Object;
	private var url:String;
	private var fallback:Boolean;
	private var volume:int = 50;
	private var lastVolume:int = 50;
	private var cursorCbk:Function = null;
	private var vidRect:Object = {x: 0, y: 0, width: 0, height: 0};

	public function OmVideo(ui:UIComponent, params:Object, cursorCbk:Function = null) {
		this.ui = ui;
		this.params = params;
		this.cursorCbk = cursorCbk;
	}

	private function getVideo():Video {
		if (vid === null) {
			vid = new Video();
			vid.width = vidRect.width || width;
			vid.height = vidRect.height || height;
			vid.x = vidRect.x || 0;
			vid.y = vidRect.y || 0;
			ui.addChild(vid);
		}
		return vid;
	}

	public function getMic():Microphone {
		return mic;
	}

	private function setBg():void {
		var g:Graphics = ui.graphics;
		if ((PLAY === mode && hasVideo(params.av == null ? [] : params.av.split(','))) || (!!cam && !cam.muted)) {
			g.beginFill(0x000000, 1);
			g.drawRect(0, 0, this.width, this.height);
			g.endFill();
		} else {
			g.clear();
		}
	}

	public function resize(_width:int, _height:int):void {
		debug("resize:: " + _width + "x" + _height);
		this.width = ui.width = _width;
		this.height = ui.height = _height;
		setBg();
	}

	public function vidResize(_width:int, _height:int):void {
		debug("vidResize:: " + _width + "x" + _height);
		vidRect.width = _width;
		vidRect.height = _height;
		if (_width < this.width) {
			vidRect.x = (this.width - _width) / 2;
		} else {
			vidRect.x = 0;
		}
		if (_height < this.height) {
			vidRect.y = (this.height - _height) / 2;
		} else {
			vidRect.y = 0;
		}
		if (!!vid) {
			vid.width = vidRect.width;
			vid.height = vidRect.height;
			vid.x = vidRect.x;
			vid.y = vidRect.y;
		}
	}

	public function attachCamera(_cam:Camera):void {
		this.cam = _cam;
		getVideo().attachCamera(_cam);
	}

	public function attachStream(_ns:NetStream):void {
		this.ns = _ns;
		getVideo().attachNetStream(_ns);
	}

	private function clear():void {
		if (!vid) {
			return;
		}
		vid.attachNetStream(null);
		vid.attachCamera(null);
		vid.clear();
		ui.removeChild(vid);
		ui.graphics.clear();
		vid = null;
		cam = null;
		mic = null;
	}

	public function setVolume(vol:int):void {
		volume = vol;
		_setVolume(volume);
	}

	public function resetVolume():void {
		_setVolume(volume);
	}

	public function resetStreamVolume():void {
		setStreamVolume(volume);
	}

	/**
	 * This method to set volume of other stream
	 * @param vol - new volume
	 */
	public function setStreamVolume(vol:int):void {
		volume = vol;
		if (ns !== null) {
			ns.soundTransform = new SoundTransform(vol / 100.0);
		}
	}
	private function _setVolume(vol:int):void {
		if (mic !== null) {
			mic.gain = vol;
		}
	}

	public function mute():void {
		debug("mute");
		lastVolume = volume;
		setVolume(0);
	}

	public function unmute():void {
		debug("unmute");
		setVolume(lastVolume);
	}

	private function debug(... rest):void {
		if ('true' === params.debug) {
			ExternalInterface.call("console.log", rest);
		}
	}

	private function createStream():void {
		debug("createStream: ");
		ns = new NetStream(nc);
		ns.client = {
			onMetaData: function(metadata:Object):void {
				debug("onMetaData: ", metadata);
			}
			, onPlayStatus: function(metadata:Object):void {
				debug("onPlayStatus: ", metadata);
			}
			, onCuePoint: function(metadata:Object):void {
				debug("onCuePoint: ", metadata);
			}
			, ioError: function(e:IOErrorEvent):void {
				debug("ioError: ", e);
			}
			, netStatus: function(e:NetStatusEvent):void {
				debug("netStatus: ", e);
			}
			, asyncError: function(e:AsyncErrorEvent):void {
				debug("asyncError: ", e);
			}
		};
		//this is a workaround, attaching the event to the client object does not work
		ns.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus2);
	}

	private function onNetStatus2(e:NetStatusEvent):void {
		debug("netStream_onNetStatus: ", e.info.code);
	}

	private function _publish(_mode:String, name:String, cam:Camera, _mic:Microphone, f:Function):void {
		if (ns !== null){
			reset();
		}
		this.mode = _mode;
		this.mic = _mic;
		createStream();

		ns.publish(name, (mode === BROADCAST) ? LIVE : mode);
		ns.attachCamera(cam);
		attachCamera(cam);
		if (cam !== null) {
			var videoStreamSettings:VideoStreamSettings = null;
			debug("codec = " + params.videoCodec);
			if (params.videoCodec === CODEC_H264) {
				var vss:H264VideoStreamSettings = new H264VideoStreamSettings();
				vss.setProfileLevel(H264Profile.BASELINE, H264Level.LEVEL_5_1);
				videoStreamSettings = vss;
			} else {
				videoStreamSettings = new VideoStreamSettings();
			}
			videoStreamSettings.setQuality(cam.bandwidth, cam.quality);
			videoStreamSettings.setKeyFrameInterval(cam.keyFrameInterval);
			debug("::camera settings ", cam.keyFrameInterval, cam.width, cam.height, cam.fps);
			videoStreamSettings.setMode(cam.width, cam.height, cam.fps);
			ns.videoStreamSettings = videoStreamSettings;
			setBg();
		}
		ns.attachAudio(mic);
		_setVolume(volume);

		if (f !== null) {
			f.call();
		}
	}

	private function _connect(_url:String, callback:Function):void {
		nc = new NetConnection();
		nc.addEventListener(NetStatusEvent.NET_STATUS, function (e:NetStatusEvent):void {
			debug("ConnectionStatus: " + e.info.code + ", fallback ? " + fallback);
			switch (e.info.code) {
				case 'NetConnection.Connect.Failed':
				case 'NetConnection.Connect.Rejected':
					if (!fallback) {
						fallback = true;
						url = params.fallback;
						_connect(url, callback);
					}
					break;
				case 'NetConnection.Connect.Success':
					callback();
					break;
			}
		});
		nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR, function (event:AsyncErrorEvent):void {
			debug("OmVideo Async error" + event);
		});
		nc.addEventListener(SecurityErrorEvent.SECURITY_ERROR, function (event:SecurityErrorEvent):void {
			debug("OmVideo Security error" + event);
		});
		nc.client = {
			onMetaData: function (infoObject:Object):void {
				debug("onMetaData::", infoObject);
			}
			, onBWDone: function(...rest):void {
				debug("onBWDone");
			}
			, onBWCheck: function(...rest):Number {
				debug("onBWCheck");
				return 0;
			}
			, setId: function (id:Number):void {
				debug("id: " + id); //TODO save connection id
			}
			, setUid: function (_uid:String):void {
				params.selfUid = _uid;
				debug("setUid :: ", params);
			}
			, newScreenCursor: function(arr:Array):void {
				if (arr.length > 2 && params.uid === arr[0]) {
					cursorCbk(arr[1] * zoomX(), arr[2] * zoomY());
				}
			}
			, sendVarsToMessageWithClient: function(obj:Object):void {
				debug("sendVarsToMessageWithClient :: ", obj);
				if ("copiedText" === obj[0]) {
					ExternalInterface.call("Room.showClipboard", obj[1]);
				} else if ("quit" === obj["message"] || "kick" === obj["message"]) {
					reset();
				}
			}
		};
		var nativeSsl:Boolean = 'true' === params.native;
		debug("native ? " + nativeSsl + " " + _url);
		nc.proxyType = nativeSsl ? 'best' : 'none';
		nc.connect(_url, {
			sid: params.sid
			, roomClient: true
			, nativeSsl: nativeSsl
		});
	}

	public function connect(callback:Function):void {
		if (nc === null || !nc.connected) {
			debug("NetConnection is not connected", url);
			url = params.url;
			_connect(url, callback);
		} else {
			callback();
		}
	}

	public function zoomX():Number {
		return width / params.width;
	}

	public function zoomY():Number {
		return height / params.height;
	}

	public function broadcast(name:String, cam:Camera, _mic:Microphone):void {
		connect(function():void {
			nc.call("resize", new Responder(function ():void {
				_publish(BROADCAST, name, cam, _mic, null);
			}), cam === null ? 0 : cam.width, cam === null ? 0 : cam.height);
		});
	}

	public function record(name:String, cam:Camera, _mic:Microphone, f:Function):void {
		connect(function():void {
			_publish(RECORD, name, cam, _mic, f);
		});
	}

	public function play(name:String):void {
		connect(function():void {
			debug("PLAY::", name);
			if (ns !== null){
				reset();
			}
			mode = PLAY;
			createStream();
			//invokes Method in baseVideoView which shows the stream
			getVideo().attachNetStream(ns);
			ns.play(name);
			setBg();
		});
	}

	public function reset():void {
		debug("reset:: ns ?== null " + (ns === null));
		if (ns !== null) {
			switch (mode) {
				case PLAY:
					ns.pause();
					break;
				case BROADCAST:
				case RECORD:
					ns.publish(null);
				default:
					break;
			}
			clear();
			ns.dispose();
		} else {
			clear();
		}
		mode = null;
		ns = null;
	}

	public function getNc():NetConnection {
		return nc;
	}

	public static function hasVideo(act:Array):Boolean {
		return act.indexOf('broadcastV') > -1;
	}

	public static function hasAudio(act:Array):Boolean {
		return act.indexOf('broadcastA') > -1;
	}
}
}
