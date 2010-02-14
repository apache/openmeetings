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

	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.events.*;

	use namespace flvplayback_internal;

	/**
	 * Handles downloading and parsing SMIL xml format for
	 * fl.video.NCManager.
	 *
	 * <p>NCManager supports a subset of SMIL to handle multiple streams
	 * for multiple bandwidths.  NCManager assumes any URL that does not
	 * being with "rtmp://" and does not end with ".flv" is a SMIL url.</p>
	 *
	 * <p>SMIL looks like this:</p>
	 *
	 * <pre>
	 * &lt;smil&gt;
	 *     &lt;head&gt;
	 *         &lt;meta base="rtmp://myserver/mypgm/" /&gt;
	 *         &lt;layout&gt;
	 *             &lt;root-layout width="240" height="180" /&gt;
	 *         &lt;/layout&gt;
	 *     &lt;/head&gt;
	 *     &lt;body&gt;
	 *         &lt;switch&gt;
	 *             &lt;ref src="myvideo_cable.flv" system-bitrate="128000" dur="3:00.1"/&gt;
	 *             &lt;video src="myvideo_isdn.flv" system-bitrate="56000" dur="3:00.1"/&gt;
	 *             &lt;video src="myvideo_mdm.flv" dur="3:00.1"/&gt;
	 *         &lt;/switch&gt;
	 *     &lt;/body&gt;
	 * &lt;/smil&gt;
	 * </pre>
	 *
	 * <p>The meta tag's base attribute has the URL for the Flash Media Server (FMS).
	 * The video tags give the stream names and the minimum
	 * bandwidth required. The ref tag is synonomous with the video
	 * tag.</p>
	 *
	 * <p>A similar format can be used for progressive http download
	 * without using bandwidth detection.</p>
	 *
	 * <pre>
	 * &lt;smil&gt;
	 *     &lt;head&gt;
	 *         &lt;layout&gt;
	 *             &lt;root-layout width="240" height="180" /&gt;
	 *         &lt;/layout&gt;
	 *     &lt;/head&gt;
	 *     &lt;body&gt;
	 *         &lt;video src="http://myserver/myvideo.flv" dur="3:00.1"/&gt;
	 *     &lt;/body&gt;
	 * &lt;/smil&gt;
	 * </pre>
	 * 
	 * Precise subset of SMIL supported
	 *
	 * <ul>
	 *     <li>smil tag - top level tag</li>
	 *     <ul>
	 *         <li>head tag</li>
	 *         <ul>
	 *             <li>meta tag</li>
	 *             <ul>
	 *                 <li>Only base attribute supported</li>
	 *                 <li>Two instances are supported for FMS.  First is primary server, second is backup.</li>
	 *             </ul>
	 *             <li>layout tag</li>
	 *             <ul>
	 *                 <li>Only first instance is used, rest ignored.</li>
	 *                 <li>root-layout tag</li>
	 *                 <ul>
	 *                     <li>Only width and height attributes supported.</li>
	 *                     <li>Width and height only supported in absolute pixel values.</li>
	 *                 </ul>
	 *             </ul>
	 *         </ul>
	 *         <li>body tag</li>
	 *         <ul>
	 *             <li>Only one tag allowed in body (either switch, video or ref)</li>
	 *             <li>switch tag supported</li>
	 *             <li>video tag supported</li>
	 *             <ul>
	 *                  <li>At top level and within switch tag.</li>
	 *                  <li>Only src, system-bitrate and dur attributes supported.</li>
	 *                  <li>system-bitrate attribute only supported within switch tag.</li>
	 *                  <li>dur attribute we only support full clock format (e.g. 00:03:00.1) and partial clock format (e.g. 03:00.1).</li>
	 *             </ul>
	 *             <li>ref tag - synonym for video tag</li>
	 *         </ul>
	 *     </ul>
	 * </ul>
	 *
     * @private
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */

	/*
	 * XML examples from above without xml entitiy substitution:
	 *
	 * <smil>
	 *     <head>
	 *         <meta base="rtmp://myserver/mypgm/" />
	 *         <layout>
	 *             <root-layout width="240" height="180" />
	 *         </layout>
	 *     </head>
	 *     <body>
	 *         <switch>
	 *             <ref src="myvideo_cable.flv" system-bitrate="128000" dur="3:00.1"/>
	 *             <video src="myvideo_isdn.flv" system-bitrate="56000" dur="3:00.1"/>
	 *             <video src="myvideo_mdm.flv" dur="3:00.1"/>
	 *         </switch>
	 *     </body>
	 * </smil>
	 *
	 * <smil>
	 *     <head>
	 *         <layout>
	 *             <root-layout width="240" height="180" />
	 *         </layout>
	 *     </head>
	 *     <body>
	 *         <video src="http://myserver/myvideo.flv" dur="3:00.1"/>
	 *     </body>
	 *
	 * Precise subset of SMIL supported (more readable format):
	 *
	 * * smil tag - top level tag
	 *     o head tag
	 *         + meta tag
	 *             # Only base attribute supported
	 *             * Two instances are supported for Flash Media Server (FMS).  First is primary server, second is backup.
	 *         + layout tag
	 *             # Only first instance is used, rest ignored.
	 *             # root-layout tag
	 *                 * Only width and height attributes supported.
	 *                 * Width and height only supported in absolute pixel values .
	 *     o body tag
	 *         + Only one tag allowed in body (either switch, video or ref)
	 *         + switch tag supported
	 *         + video tag supported
	 *              # At top level and within switch tag.
	 *              # Only src, system-bitrate and dur attributes supported.
	 *              # system-bitrate attribute only supported within switch tag.
	 *              # dur attribute we only support full clock format (e.g. 00:03:00.1) and partial clock format (e.g. 03:00.1).
	 *         + ref tag - synonym for video tag
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	public class SMILManager {

		include "ComponentVersion.as"

		// INCManager to ping when done
		private var _owner:INCManager;

		// smil support
		flvplayback_internal var xml:XML;
		flvplayback_internal var xmlLoader:URLLoader;
		flvplayback_internal var baseURLAttr:Array;
		flvplayback_internal var width:int;
		flvplayback_internal var height:int;
		flvplayback_internal var videoTags:Array;

		private var _url:String;

		/**
         * constructor
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function SMILManager(owner:INCManager) {
			_owner = owner;
			width = -1;
			height = -1;
		} 

		/**
		 * <p>Starts download of XML file.  Will be parsed and based
		 * on that we will decide how to connect.</p>
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function connectXML(url:String):Boolean {
			_url = fixURL(url);
			xmlLoader = new URLLoader();
			xmlLoader.addEventListener(Event.COMPLETE, xmlLoadEventHandler);
			xmlLoader.addEventListener(IOErrorEvent.IO_ERROR, xmlLoadEventHandler);
			xmlLoader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, xmlLoadEventHandler);
			xmlLoader.load(new URLRequest(_url));
			return false;
		}

		/**
		 * <p>Append version parameter to URL.</p>
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function fixURL(origURL:String):String {
			if (/^(http:|https:)/i.test(origURL)) {
				var sep:String = (origURL.indexOf("?") >= 0) ? "&" : "?";
				return origURL + sep + "FLVPlaybackVersion=" + SHORT_VERSION;
			}
			return origURL;
		}

		/**
		 * <p>Handles load of XML.
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function xmlLoadEventHandler(e:Event):void {
			try {
				if (e.type != Event.COMPLETE) {
					// todo: handle security and io errors differently?
					// signal failure
					_owner.helperDone(this, false);
				} else {
					baseURLAttr = new Array();
					videoTags = new Array();
					xml = new XML(xmlLoader.data);

					// this line enables us to handle smil regardless of whether user specified xmlns
					// correctly, incorrectly or not at all.  A little lazy on our part perhaps...
					default xml namespace = xml.namespace();

					if (xml == null || xml.localName() == null) {
						throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" No root node found; if url is for an flv it must have .flv extension and take no parameters");
					} else if (xml.localName() != "smil") {
						throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Root node not smil");
					}
					checkForIllegalNodes(xml, "element", ["head", "body"]);
					//ifdef DEBUG
					//if (xml.head.length() > 1) {
					//	throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Multiple head tags in " + xml.localName() + " tag.");
					//} else
					//endif
					if (xml.head.length() > 0) {
						parseHead(xml.head[0]);
					}
					if (xml.body.length() < 1) {
						throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Tag body is required.");
					} else
					//ifdef DEBUG
					//if (xml.body.length() > 1) {
					//	throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Multiple body tags in " + xml.localName() + " tag.");
					//} else
					//endif
					{
						parseBody(xml.body[0]);
					}
					_owner.helperDone(this, true);
				}
			} catch (err:Error) {
				_owner.helperDone(this, false);
				throw err;
			} finally {
				xmlLoader.removeEventListener(Event.COMPLETE, xmlLoadEventHandler);
				xmlLoader.removeEventListener(IOErrorEvent.IO_ERROR, xmlLoadEventHandler);
				xmlLoader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, xmlLoadEventHandler);
				xmlLoader = null;
			}
		}

		/**
		 * parse head node of smil
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseHead(parentNode:XML):void {
			checkForIllegalNodes(parentNode, "element", ["meta", "layout"]);
			//ifdef DEBUG
			//if (parentNode.meta.length() > 1) {
			//	throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Multiple meta tags in " + parentNode.localName() + " tag.");
			//} else
			//endif
			if (parentNode.meta.length() > 0) {
				checkForIllegalNodes(parentNode.meta[0], "element", []);
				checkForIllegalNodes(parentNode.meta[0], "attribute", ["base"]);
				if (parentNode.meta.@base.length() > 0) {
					baseURLAttr.push(parentNode.meta.@base.toString());
				}
			}
			//ifdef DEBUG
			//if (parentNode.layout.length() > 1) {
			//	throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Multiple layout tags in " + parentNode.localName() + " tag.");
			//} else
			//endif
			if (parentNode.layout.length() > 0) {
				parseLayout(parentNode.layout[0]);
			}
		}

		/**
		 * parse layout node of smil
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseLayout(parentNode:XML):void {
			checkForIllegalNodes(parentNode, "element", ["root-layout"]);
			if (parentNode["root-layout"].length() > 1) {
				throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Only one base attribute supported in meta tag.");
			} else if (parentNode["root-layout"].length() > 0) {
				var rootLayoutNode:XML = parentNode["root-layout"][0];
				var widthNum:Number;
				var heightNum:Number;
				//ifdef DEBUG
				//checkForIllegalNodes(rootLayoutNode, "attribute", ["width", "height"]);
				//endif
				if (rootLayoutNode.@width.length() > 0) {
					widthNum = Number(rootLayoutNode.@width[0]);
				}
				if (rootLayoutNode.@height.length() > 0) {
					heightNum = Number(rootLayoutNode.@height[0]);
				}
				if ( isNaN(widthNum) || widthNum < 0 || isNaN(heightNum) || heightNum < 0 ) {
					throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Tag " + parentNode.localName() + " requires attributes width and height.  Width and height must be numbers greater than or equal to 0.");
				}
				width = Math.round(widthNum);
				height = Math.round(heightNum);
			}
		}

		/**
		 * parse body node of smil
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseBody(parentNode:XML):void {
			if (parentNode.*.length() != 1 || parentNode.*[0].nodeKind() != "element") {
				throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Tag " + parentNode.localName() + " is required to contain exactly one tag.");
			}
			var child:XML = parentNode.*[0];
			var childName:String = child.localName();
			switch (childName) {
			case "switch":
				parseSwitch(child);
				break;
			case "video":
			case "ref":
				var videoObj:Object = parseVideo(child);
				videoTags.push(videoObj);
				break;
			default:
				throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Tag " + childName + " not supported in " + parentNode.localName() + " tag.");
				break;
			} // switch (parentNode[0])
			if (videoTags.length < 1) {
				throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" At least one video of ref tag is required.");
			}
		}

		/**
		 * parse switch node of smil
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseSwitch(parentNode:XML):void {
			for (var i:String in parentNode.*) {
				var child:XML = parentNode.*[i];
				if (child.nodeKind() != "element") continue;
				switch (child.localName()) {
				case "video":
				case "ref":
					videoTags.push(parseVideo(child));
					break;
				default:
					//ifdef DEBUG
					//throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" Tag " + child.localName() + " not supported in " + parentNode.localName() + " tag.");
					//endif
					break;
				} // switch (child.localName())
			}
		}

		/**
		 * parse video or ref node of smil.  Returns object with
		 * attribute info.
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseVideo(node:XML):Object {
			//ifdef DEBUG
			//checkForIllegalNodes(node, "attribute", ["src", "system-bitrate", "dur"]);
			//endif
			var obj:Object = new Object();
			if (node.@src.length() > 0) {
				obj.src = node.@src.toString();
			}
			if (node.@["system-bitrate"].length() > 0) {
				obj.bitrate = int(node.@["system-bitrate"].toString());
			}
			if (node.@dur.length() > 0) {
				obj.dur = parseTime(node.@dur.toString());
			}
			return obj;
		}

		/**
		 * parse time in hh:mm:ss.s or mm:ss.s format.
		 * Also accepts a bare number of seconds with
		 * no colons.  Returns a number of seconds.
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function parseTime(timeStr:String):Number {
			var results:Object = /^((\d+):)?(\d+):((\d+)(.\d+)?)$/.exec(timeStr);
			if (results == null) {
				var numSecs:Number = Number(timeStr);
				if (isNaN(numSecs) || numSecs < 0) {
					throw new VideoError(VideoError.INVALID_XML, "Invalid dur value: " + timeStr);
				}
				return numSecs;
			} else {
				var t:Number = 0;
				t += (uint(results[2]) * 60 * 60);
				t += (uint(results[3]) * 60);
				t += (Number(results[4]));
				return t;
			}
		}

		/**
		 * checks for extra, unrecognized elements of the given kind
		 * in parentNode and throws VideoError if any are found.
		 * Ignores any nodes with different nodeKind().  Takes the
		 * list of recognized elements as a parameter.
		 *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		flvplayback_internal function checkForIllegalNodes(parentNode:XML, kind:String, legalNodes:Array):void {
			//ifdef DEBUG
			//debugTrace("checkForIllegalNodes( " + parentNode.localName() + ", " + kind + ", " + legalNodes + " )");
			//endif
			for (var i:String in parentNode.*) {
				var isLegal:Boolean = false;
				var child:XML = parentNode.*[i];
				//ifdef DEBUG
				//debugTrace("Found node of type " + child.nodeKind());
				//endif
				if (child.nodeKind() != kind) continue;
				var childName:String = child.localName();
				//ifdef DEBUG
				//debugTrace("Child element name " + childName);
				//endif
				for (var j:String in legalNodes) {
					if (legalNodes[j] == childName) {
						isLegal = true;
						break;
					}
				}
				if (!isLegal) {
					throw new VideoError(VideoError.INVALID_XML, "URL: \"" + _url + "\" " + kind + " " + childName + " not supported in " + parentNode.localName() + " tag.");
				}
			}
		}

		//ifdef DEBUG
		//public function debugTrace(s:*):void {
		//	if (_owner != null) {
		//		Object(_owner).debugTrace(s);
		//	}
		//}
		//endif

	} // class SMILManager

} // package fl.video

