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
import flash.events.AsyncErrorEvent;
import flash.events.NetStatusEvent;
import flash.media.Camera;
import flash.media.H264Level;
import flash.media.H264Profile;
import flash.media.H264VideoStreamSettings;
import flash.media.Microphone;
import flash.media.Video;
import flash.media.VideoStreamSettings;
import flash.net.NetConnection;
import flash.net.NetStream;
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
	public var width:int;
	public var height:int;
	private var mode:String;
	private var codec:String;
	private var url:String;

	public function OmVideo(ui:UIComponent, codec:String, url:String) {
		this.ui = ui;
		this.codec = codec;
		this.url = url;
	}

	private function getVideo():Video {
		if (vid == null) {
			vid = new Video();
			vid.width = width;
			vid.height = height;
			ui.addChild(vid);
		}
		return vid;
	}

	public function resize(width:int, height:int):void {
		this.width = ui.width = width;
		this.height = ui.height = height;
	}

	public function attachCamera(cam:Camera):void {
		getVideo().attachCamera(cam);
	}

	public function attachStream(ns:NetStream):void {
		this.ns = ns;
		getVideo().attachNetStream(ns);
	}

	private function clear():void {
		vid.attachNetStream(null);
		vid.attachCamera(null);
		vid.clear();
		ui.removeChild(vid);
		vid = null;
	}

	private function createStream():void {
		ns = new NetStream(nc);
		//see: http://livedocs.adobe.com/flash/9.0_de/ActionScriptLangRefV3/flash/net/NetStream.html
		//according to the docs the construct to catch event has to be implemented like this.
		//var t = this;
		var clientObject:Object = new Object();
		clientObject.onMetaData = function(metadata:Object):void {
			//t.onMetaData(metadata);
			trace("onMetaData: ", metadata);
		};
		clientObject.onPlayStatus = function(metadata:Object):void {
			//t.onPlayStatus(metadata);
			trace("onPlayStatus: ", metadata);
		};
		clientObject.onCuePoint = function(metadata:Object):void {
			//t.onCuePoint(metadata);
			trace("onCuePoint: ", metadata);
		};
		clientObject.ioError = function(error:Object):void {
			//t.ioError(error);
			trace("ioError: ", error);
		};
		clientObject.netStatus = function(status:Object):void {
			//t.netStatus(status);
			trace("netStatus: ", status);
		};
		clientObject.asyncError = function(error:Object):void {
			//t.asyncError(error);
			trace("asyncError: ", error);
		};
		ns.client = clientObject;
		//this is a workaround, attaching the event to the client object does not work
		ns.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus2);
	}

	private function onNetStatus2(evt:Object):void {
		trace("netStream_onNetStatus: ", evt.info.code, evt.target);
		//this.onNetStatus.sendEvent(evt.info);
	}

	private function _publish(mode:String, name:String, cam:Camera, mic:Microphone, f:Function):void {
		if (ns != null){
			reset();
		}
		this.mode = mode;
		createStream();

		if (cam != null) {
			ns.attachCamera(cam);
			//invokes Method in baseVideoView which shows the cam
			attachCamera(cam);

			var videoStreamSettings:VideoStreamSettings = null;
			trace("codec = " + codec);
			if (codec === CODEC_H264) {
				var vss:H264VideoStreamSettings = new H264VideoStreamSettings();
				vss.setProfileLevel(H264Profile.BASELINE, H264Level.LEVEL_5_1);
				videoStreamSettings = vss;
			} else {
				videoStreamSettings = new VideoStreamSettings();
			}
			videoStreamSettings.setQuality(cam.bandwidth, cam.quality);
			videoStreamSettings.setKeyFrameInterval(cam.keyFrameInterval);
			trace("::keyFrameInterval " + cam.keyFrameInterval);
			videoStreamSettings.setMode(cam.width, cam.height, cam.fps);
			ns.videoStreamSettings = videoStreamSettings;
		}
		if (mic != null) {
			ns.attachAudio(mic);
			//FIXME !!! no mute !!! muteMicro(this.micMuted);
		}

		ns.publish(name, (mode == BROADCAST) ? LIVE : mode);
		if (f != null) {
			f.call();
		}
	}

	private function publish(mode:String, name:String, cam:Camera, mic:Microphone, f:Function):void {
		if (nc == null || !nc.connected) {
			trace("NetConnection is not connected");
			nc = new NetConnection();
			nc.addEventListener(NetStatusEvent.NET_STATUS, function onConnectionStatus(e:NetStatusEvent):void {
				trace("ConnectionStatus: " + e.info.code);
				if (e.info.code == "NetConnection.Connect.Success") {
					_publish(mode, name, cam, mic, f);
				} else {
					//TODO
				}
			});
			nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR, function (event:AsyncErrorEvent):void {
				trace("login Async error" + event);
			});
			nc.client = {
				onMetaData: function (infoObject:Object):void {
					for (var propName:String in infoObject) {
						trace(propName + " = " + infoObject[propName]);
					}
				}
				, onBWDone: function(...rest):void {
					trace("onBWDone");
				}
				, onBWCheck: function(...rest):Number {
					trace("onBWCheck");
					return 0;
				}
				, setId: function (id:Number):void {
					trace("id: " + id); //TODO save connection id
				}
			};
			nc.connect(url);
		} else {
			_publish(mode, name, cam, mic, f);
		}
	}

	public function record(name:String, cam:Camera, mic:Microphone, f:Function):void {
		publish(RECORD, name, cam, mic, f);
	}

	public function play(name:String):void {
		if (ns != null){
			reset();
		}
		mode = PLAY;
		createStream();
		//invokes Method in baseVideoView which shows the stream
		getVideo().attachNetStream(ns);
		//FIXME: Commented out, cause this leads to Buffer-Full/Buffer-Empty Events
		//after re-syncing the stream
		//this.setBuffer(0.1);
		ns.play(name);
	}

	public function reset():void {
		var vid:Video = getVideo();
		if (ns != null) {
			switch (mode) {
				case PLAY:
					ns.pause();
					ns.close();
					clear();
					break;
				case BROADCAST:
				case RECORD:
					ns.publish(null); //false in original code
				default:
					clear();
					ns.close();
					break;
			}
		} else {
			clear();
		}
		ns = null;
	}
}
}
