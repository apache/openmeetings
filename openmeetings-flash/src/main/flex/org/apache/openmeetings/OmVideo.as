/**
 * Created by solomax on 9/17/2014.
 */
package org.apache.openmeetings {
import flash.media.Camera;
import flash.media.Microphone;
import flash.media.Video;
import flash.net.NetStream;
import mx.core.UIComponent;

public class OmVideo {
	public static const PLAY:String = "play";
	public static const BROADCAST:String = "broadcast";
	public static const RECORD:String = "record";
	private var vid:Video;
	private var ui:UIComponent;
	private var ns:NetStream;
	public var width:int;
	public var height:int;
	private var mode:String;

	public function OmVideo(ui:UIComponent) {
		this.ui = ui;
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
