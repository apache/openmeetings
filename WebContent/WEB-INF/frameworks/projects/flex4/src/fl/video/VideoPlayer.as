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

	import flash.events.*;
	import flash.geom.Rectangle;
	import flash.media.*;
	import flash.net.*;
	import flash.utils.*;

	use namespace flvplayback_internal;

	/**
	 * Dispatched by the VideoPlayer instance when it closes the NetConnection  
	 * by timing out or through a call to the <code>close()</code> method, or when 
	 * you call the <code>load()</code> or <code>play()</code> methods or set the 
	 * <code>source</code> property and cause the RTMP connection 
	 * to close as a result. The FLVPlayback instance dispatches this event only when 
	 * streaming from Flash Media Server (FMS) or other Flash Video Streaming Service (FVSS). 
	 * 
	 * <p>The <code>close</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.CLOSE</code>.</p>
	 * 
 	 * @see VideoEvent#CLOSE 
	 * @see #close() 
	 * @see #source 
	 * @see #load() 
	 * @see #play() 
	 * 
	 * @tiptext close event
     * @eventType fl.video.VideoEvent.CLOSE
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("close", type="fl.video.VideoEvent")]

	/**
	 * Dispatched when playing completes because the player reached the end of the FLV file. 
	 * The component does not dispatch the event if you call the <code>stop()</code> or 
	 * <code>pause()</code> methods 
	 * or click the corresponding controls. 
	 * 
	 * <p>When the application uses progressive download, it does not set the 
	 * <code>totalTime</code>
	 * property explicitly, and it downloads an FLV file that does not specify the duration 
	 * in the metadata. The video player sets the <code>totalTime</code> property to an approximate 
	 * total value before it dispatches this event.</p>
	 * 
	 * <p>The video player also dispatches the <code>stateChange</code> and <code>stopped</code>
	 * events.</p>
	 * 
	 * <p>The <code>complete</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.COMPLETE</code>.</p>
	 * 
	 * @see #event:stateChange stateChange event
	 * @see #stop() 
	 * @see #pause() 
	 * @see #totalTime 
	 * 
	 * @tiptext complete event
     * @eventType fl.video.VideoEvent.COMPLETE
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 *
	 */
	[Event("complete", type="fl.video.VideoEvent")]

	/**
	 * Dispatched when a cue point is reached. The event object has an 
	 * <code>info</code> property that contains the info object received by the 
	 * <code>NetStream.onCuePoint</code> event callback for FLV file cue points. 
	 * For ActionScript cue points, it contains the object that was passed 
	 * into the ActionScript cue point methods or properties.
	 * 
	 * <p>The <code>cuePoint</code> event is of type MetadataEvent and has the constant 
	 * <code>MetadataEvent.CUE_POINT</code>.</p>
	 * 
	 * @see MetadataEvent#CUE_POINT 
	 * @see flash.net.NetStream#event:onCuePoint NetStream.onCuePoint event
	 * 
	 * @tiptext cuePoint event
     * @eventType fl.video.MetadataEvent.CUE_POINT
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 *
	 */
	[Event("cuePoint", type="fl.video.MetadataEvent")]

	/**
	 * Dispatched the first time the FLV file's metadata is reached.
	 * The event object has an <code>info</code> property that contains the 
	 * info object received by the <code>NetStream.onMetaData</code> event callback.
	 *
	 * <p>The <code>metadataReceived</code> event is of type MetadataEvent and has the constant 
	 * <code>MetadataEvent.METADATA_RECEIVED</code>.</p>
	 * 
	 * @see MetadataEvent#METADATA_RECEIVED 
	 * @see flash.net.NetStream#event:onMetaData NetStream.onMetaData event
	 * 
     * @tiptext metadataReceived event
	 * @eventType fl.video.MetadataEvent.METADATA_RECEIVED
	 * 
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 *
	 */
	[Event("metadataReceived", type="fl.video.MetadataEvent")]

	/**
	 * Dispatched while the FLV file is playing at the frequency specified by the 
	 * <code>playheadUpdateInterval</code> property or when rewinding starts. 
	 * The component does not dispatch this event when the video player is paused or stopped 
	 * unless a seek occurs. 
	 * 
	 * <p>The <code>playheadUpdate</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.PLAYHEAD_UPDATE</code>.</p> 
     * 
     * @default 0.25
	 * 
	 * @see VideoEvent#PLAYHEAD_UPDATE 
	 * @see #playheadUpdateInterval 
	 * 
	 * @tiptext change event
	 * @eventType fl.video.VideoEvent.PLAYHEAD_UPDATE
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("playheadUpdate", type="fl.video.VideoEvent")]

	/**
	 * Indicates progress made in number of bytes downloaded. Dispatched at the frequency 
	 * specified by the <code>progressInterval</code> property, starting 
	 * when the load begins and ending when all bytes are loaded or there is a network error. 
	 * The default is every 0.25 seconds starting when load is called and ending
	 * when all bytes are loaded or if there is a network error. Use this event to check 
	 * bytes loaded or number of bytes in the buffer. 
	 *
	 * <p>Dispatched only for a progressive HTTP download. Indicates progress in number of 
	 * downloaded bytes. The event object has the <code>bytesLoaded</code> and <code>bytesTotal</code>
	 * properties, which are the same as the FLVPlayback properties of the same names.</p>
	 * 
	 * <p>The <code>progress</code> event is of type VideoProgressEvent and has the constant 
	 * <code>VideoProgressEvent.PROGRESS</code>.</p> 
	 * 
	 * @see #bytesLoaded 
	 * @see #bytesTotal 
	 * @see VideoProgressEvent#PROGRESS 
	 * @see #progressInterval 
	 * 
	 * @tiptext progress event
	 * @eventType fl.video.VideoProgressEvent.PROGRESS
     * 
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("progress", type="fl.video.VideoProgressEvent")]

	/**
	 * Event dispatched when an FLV file is loaded and ready to display. It starts the first 
	 * time you enter a responsive state after you load a new FLV file with the <code>play()</code>
	 * or <code>load()</code> method. It starts only once for each FLV file that is loaded.
	 * 
	 * <p>The <code>ready</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.READY</code>.</p> 
	 * 
	 * @see #load() 
	 * @see #play() 
     * @see VideoEvent#READY 
     *
	 * @tiptext ready event
	 * @eventType fl.video.VideoEvent.READY
	 * 
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 *
	 */
	[Event("ready", type="fl.video.VideoEvent")]

	/**
	 * Dispatched when the video player is resized or laid out. Here are two layout scenarios:<br/>
	 * <ul><li>If the video player is laid out by either using the <code>autoLayout</code> 
	 * event or calling the <code>setScale()</code> or 
	 * <code>setSize()</code> methods or changing the <code>width</code>, <code>height</code>,
	 * <code>scaleX</code>, and <code>scaleY</code> properties.</li>
	 * <li>If there are two video players of different sizes and the 
	 * <code>visibleVideoPlayerIndex</code> property is switched from one video player to another.</li>
	 * </ul>  
	 *
	 * <p>The <code>layout</code> event is of type LayoutEvent and has the constant 
	 * <code>LayoutEvent.LAYOUT</code>.</p> 
	 * 
	 * @see FLVPlayback#event:autoLayout autoLayout event
 	 * @see LayoutEvent#LAYOUT 
	 * @see FLVPlayback#visibleVideoPlayerIndex 
     *
	 * @tiptext layout event
	 * @eventType fl.video.LayoutEvent.LAYOUT
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("layout", type="fl.video.LayoutEvent")]

	/**
	 * Dispatched when the playhead is moved to the start of the video player because the 
	 * <code>autoRewind</code> property is set to <code>true</code>.
	 * 
	 * <p>The <code>autoRewound</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.AUTO_REWOUND</code>.</p>
	 * 
	 * @see VideoEvent#AUTO_REWOUND 
	 * @see #autoRewind 
	 * @see VideoState#REWINDING 
	 * 
	 * @tiptext autoRewound event
     * @eventType fl.video.VideoEvent.AUTO_REWOUND
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("autoRewound", type="fl.video.VideoEvent")]

	/**
	 * Dispatched when the playback state changes. When an <code>autoRewind</code> call is 
	 * completed the <code>stateChange</code> event is dispatched with the rewinding
	 * state. The <code>stateChange</code> event does not 
	 * start until rewinding has completed. 
	 * 
	 * <p>This event can be used to track when playback enters or leaves unresponsive 
	 * states such as in the middle of connecting, resizing, or rewinding. The  
	 * <code>play()</code>, <code>pause()</code>, <code>stop()</code> and <code>seek()</code> 
	 * methods queue the requests to be executed when the player enters
	 * a responsive state.</p>
	 *
	 * <p>The <code>stateChange</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.STATE_CHANGE</code>.</p>
	 * 
     * @see VideoEvent#STATE_CHANGE 
     *
	 * @tiptext stateChange event
	 * @eventType fl.video.VideoEvent.STATE_CHANGE
	 * 
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("stateChange", type="fl.video.VideoEvent")]

	/**
	 * Dispatched when the playback encounters an unsupported player feature. When a connection is 
	 * attempted and fails due to an unsupported player feature, the <code>unsupportedPlayerVersion</code> 
	 * event is dispatched.
	 *
	 * <p>The <code>unsupportedPlayerVersion</code> event is of type VideoEvent and has the constant 
	 * <code>VideoEvent.UNSUPPORTED_PLAYER_VERSION</code>.</p>
	 * 
     * @see VideoEvent#UNSUPPORTED_PLAYER_VERSION
     *
	 * @tiptext unsupportedPlayerVersion event
	 * @eventType fl.video.VideoEvent.UNSUPPORTED_PLAYER_VERSION
	 * 
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	[Event("unsupportedPlayerVersion", type="fl.video.VideoEvent")]

	/**
	 * The VideoPlayer class lets you create a video player with a slightly smaller SWF file
	 * than if you used the FLVPlayback component. Unlike the FLVPlayback component, the 
	 * VideoPlayer class does not let you include a skin or playback controls, and although 
	 * you cannot find or seek to cue points, the <code>cuePoint</code> events occur. 
	 * 
     * <p>The FLVPlayback class wraps the VideoPlayer class. Use the FLVPlayback class in almost all cases because there is no 
	 * functionality in the VideoPlayer class that cannot be accessed using the 
	 * FLVPlayback class.</p>
	 * 
	 * <p>In version 2.5, the FLVPlayback class implements the default NCManager 
	 * interface to access streaming FLV files on Flash Media Server. You interact with 
	 * NCManager when you set the <code>contentPath</code> property and when you pass 
	 * a URL to the <code>play()</code> and <code>load()</code> methods. If you use the 
	 * VideoPlayer class by itself, however, 
	 * you must include the following statement in your ActionScript code   :</p>
	 * 
	 * <listing>var _forceNCManager:fl.video.NCManager;</listing>
	 * 
	 * <p>The NCManager class implements an interface, INCManager, and it can be replaced
	 * with a custom class for managing network communications that implements this interface.
	 * If you do that, you also need to include the following statement, replacing NCManager 
	 * with the name of the class you have provided:</p>
	 * 
	 * <listing>fl.video.VideoPlayer.iNCManagerClass = fl.video.NCManager;</listing>
	 * 
	 * <p>You do not need to add this statement if you are using the default NCManager class.</p>
	 * 
     * <p><strong>Note</strong>: You also can set <code>iNCManagerClass</code> to replace the default 
	 * fl.video.NCManager when using the FLVPlayback component.</p>
	 * 
	 * <p>To handle multiple streams for multiple bandwidths, NCManager supports a subset of 
	 * SMIL.</p>
	 *
	 * @includeExample examples/VideoPlayerExample.as -noswf
	 * 
	 * @see NCManager 
	 * @see FLVPlayback 
	 * 
	 * @tiptext	VideoPlayer class
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */

	public class VideoPlayer extends Video {

		include "ComponentVersion.as"

		// buffer states
		/**
		 * @private
		 */
		flvplayback_internal static var BUFFER_EMPTY:String = "bufferEmpty";
		/**
		 * @private
		 */
		flvplayback_internal static var BUFFER_FULL:String = "bufferFull";
		/**
		 * @private
		 */
		flvplayback_internal static var BUFFER_FLUSH:String = "bufferFlush";

		// state
		/**
		 * @private
		 */
		protected var _state:String;
		/**
		 * @private
		 */
		flvplayback_internal var _cachedState:String;
		/**
		 * @private
		 */
		flvplayback_internal var _bufferState:String;
		/**
		 * @private
		 */
		flvplayback_internal var _sawPlayStop:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _cachedPlayheadTime:Number;
		/**
		 * @private
		 */
		protected var _metadata:Object;
		/**
		 * @private
		 */
		protected var _registrationX:Number;
		/**
		 * @private
		 */
		protected var _registrationY:Number;
		/**
		 * @private
		 */
		protected var _registrationWidth:Number;
		/**
		 * @private
		 */
		protected var _registrationHeight:Number;
		/**
		 * @private
		 */
		flvplayback_internal var _startingPlay:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _lastSeekTime:Number;
		/**
		 * @private
		 */
		flvplayback_internal var _invalidSeekTime:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _invalidSeekRecovery:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _readyDispatched:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _autoResizeDone:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _lastUpdateTime:Number;
		/**
		 * @private
		 */
		flvplayback_internal var lastUpdateTimeStuckCount:Number;
		/**
		 * @private
		 */
		flvplayback_internal var _sawSeekNotify:Boolean;

		// INCManager
		/**
		 * @private
		 */
		protected var _ncMgr:INCManager;

		/**
		 * To make all VideoPlayer objects use your 
		 * custom class as the default INCManager implementation, set the 
		 * <code>iNCManagerClass</code> property to the class object or string name
		 * of your custom class.
		 * The FLVPlayback class includes the definition
		 * of the custom class; the video player does not.
		 * 
		 * @default "fl.video.NCManager" as a string
		 * 
		 * @tiptext	VideoPlayer class
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public static var iNCManagerClass:Object = "fl.video.NCManagerDynamicStream";

/**
	* Registers a custom class for the NetStream's client property. 
	* By default, <code>fl.video.VideoPlayerClient</code> is used; this class handles the <code>onMetaData()</code> and <code>onCuePoint()</code> messages. 
	* To add custom handling for other messages, such as <code>onImageData()</code> and <code>onTextData()</code>, 
	* you can create your own class and set this property to the class.
	*
	* <p>The constructor for the class must take a <code>VideoPlayer</code> instance as its only parameter, and it must have a Boolean ready property. 
	* This property should be <code>false</code> while the Client is still waiting for any data messages expected at the beginning of the file. 
	* If the VideoPlayer does its resize autorewind before these messages are received, they may never be received. 
	* By default, <code>fl.video.VideoPlayerClient</code> will return <code>true</code> from the ready property as soon as <code>onMetaData()</code> is called.</p>
	* 
	* <p>You can set the property to the class object itself (as in the example below) or the string name of the class:</p>
	* <listing>
        * import fl.video.*;
        * VideoPlayer.netStreamClientClass = MyCustomClient;
        * </listing>
        * <p>Note that setting it to the string name of the class is not enough by itself to force the class to be compiled into the SWF. 
	* The default value is the class object <code>fl.video.VideoPlayerClient</code>, not the string name.</p>
	* <p>It is strongly recommended that any custom implementation should subclass <code>fl.video.VideoPlayer</code>. 
	* To do otherwise risks breaking the metadata and cue point handling built into the VideoPlayer and the FLVPlayback component.</p>
	*
        *
   * @throws fl.video.VideoError If this property is set to an invalid value, <code>VideoError.NETSTREAM_CLIENT_CLASS_UNSET</code> will be thrown.
   * @langversion 3.0
   * @playerversion Flash 9.0.115.0
   * @default fl.video.VideoPlayerClient
   */
   public static var netStreamClientClass:Object = fl.video.VideoPlayerClient;
		
		
		

		// info about NetStream
		/**
		 * @private
		 */
		protected var _ns:NetStream;
		/**
		 * @private
		 */
		flvplayback_internal var _currentPos:Number;
		/**
		 * @private
		 */
		flvplayback_internal var _atEnd:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _atEndCheckPlayhead:Number;
		/**
		 * @private
		 */
		protected var _streamLength:Number;

		// store properties
		/**
		 * @private
		 */
		protected var _align:String;
		/**
		 * @private
		 */
		protected var _scaleMode:String;

		/**
		 * @private 
		 * 
		 * <p>If true, then video plays immediately, if false waits for
		 * <code>play</code> to be called.  Set to true if stream is
		 * loaded with call to <code>play()</code>, false if loaded
		 * by call to <code>load()</code>.</p>
		 *
		 * <p>Even if <code>_autoPlay</code> is set to false, we will start
		 * start loading the video after <code>initialize()</code> is
		 * called.  In the case of Flash Media Server (FMS), this means creating the stream
		 * and loading the first frame to display (and loading more if
		 * <code>scaleMode</code> is
		 * <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code> or
		 * <code>VideoScaleMode.NO_SCALE</code>).  In the case of HTTP
		 * download, we will start downloading the stream and show the
		 * first frame.</p>
		 */
		protected var _autoPlay:Boolean;

		/**
		 * @private
		 */
		protected var _autoRewind:Boolean;
		/**
		 * @private
		 */
		protected var _contentPath:String;
		/**
		 * @private
		 */
		protected var _bufferTime:Number;
		/**
		 * @private
		 */
		protected var _isLive:Boolean;
		/**
		 * @private 
		 */		
		flvplayback_internal var _dvrMgr:DVRManager;
		/**
		 * @private 
		 */		
		protected var _isDVR:Boolean;
		/**
		 * @private
		 */
		protected var _dvrPlaying:Boolean;
		/**
		 * @private 
		 */		
		protected var _dvrSnapToLive:Boolean;
		/**
		 * @private
		 */
		protected var _dvrStart:Boolean;
		/**
		 * @private
		 */
		protected var _dvrIncrement:Number;
		/**
		 * @private
		 */
		protected var _dvrIncrementVariance:Number;
		/**
		 * @private
		 */		
		protected var _dvrFixedDuration:Boolean;
		/**
		 * @private
		 */
		protected var _volume:Number;
		/**
		 * @private
		 */
		protected var _soundTransform:SoundTransform;
		/**
		 * @private
		 */
		protected var __visible:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _hiddenForResize:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _hiddenForResizeMetadataDelay:Number;
		/**
		 * @private
		 */
		flvplayback_internal var _resizeImmediatelyOnMetadata:Boolean;
		/**
		 * @private
		 */
		flvplayback_internal var _hiddenRewindPlayheadTime:Number;
		/**
		 * @private
		 */
		protected var _videoWidth:int;
		/**
		 * @private
		 */
		protected var _videoHeight:int;
		/**
		 * @private
		 */
		flvplayback_internal var _prevVideoWidth:int;
		/**
		 * @private
		 */
		flvplayback_internal var _prevVideoHeight:int;
		/**
		 * @private
		 */
		flvplayback_internal var oldBounds:Rectangle;
		/**
		 * @private
		 */
		flvplayback_internal var oldRegistrationBounds:Rectangle;

		// intervals
		/**
		 * @private
		 */
		flvplayback_internal var _updateTimeTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _updateProgressTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _idleTimeoutTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _autoResizeTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _rtmpDoStopAtEndTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _rtmpDoSeekTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _httpDoSeekTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _httpDoSeekCount:Number
		/**
		 * @private
		 */
		flvplayback_internal var _finishAutoResizeTimer:Timer;
		/**
		 * @private
		 */
		flvplayback_internal var _delayedBufferingTimer:Timer;

		/**
		 * for progressive download auto start bandwidth detection
		 * @private
		 */
		flvplayback_internal var waitingForEnough:Boolean;
		/**
		 * for progressive download auto start bandwidth detection
		 * @private
		 */
		flvplayback_internal var baselineProgressTime:Number;
		/**
		 * for progressive download auto start bandwidth detection
		 * @private
		 */
		flvplayback_internal var startProgressTime:Number;
		/**
		 * for progressive download auto start bandwidth detection
		 * @private
		 */
		flvplayback_internal var totalDownloadTime:Number
		/**
		 * for progressive download auto start bandwidth detection
		 * @private
		 */
		flvplayback_internal var totalProgressTime:Number

		// default times for intervals
        /**
         * @private
         * 
		 * The default update-time interval is .25 seconds.
		 * 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
        public static const DEFAULT_UPDATE_TIME_INTERVAL:Number = 250;   // .25 seconds
        /**
         * @private
         *
		 * The default update-progress interval is .25 seconds.
		 * 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
        public static const DEFAULT_UPDATE_PROGRESS_INTERVAL:Number = 250;   // .25 seconds
        /**
         * @private
         *
		 * The default idle-timeout interval is five minutes.
		 * 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		public static const DEFAULT_IDLE_TIMEOUT_INTERVAL:Number = 300000; // five minutes
		/**
		 * @private
		 */
		flvplayback_internal static const AUTO_RESIZE_INTERVAL:Number = 100;        // .1 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const DEFAULT_AUTO_RESIZE_PLAYHEAD_TIMEOUT:Number = .5;       // .5 seconds
		/**
		 * @private
		 */
		flvplayback_internal var autoResizePlayheadTimeout:Number = DEFAULT_AUTO_RESIZE_PLAYHEAD_TIMEOUT;
		/**
		 * @private
		 */
		flvplayback_internal static const DEFAULT_AUTO_RESIZE_METADATA_DELAY_MAX:Number = 5;        // .5 seconds
		/**
		 * @private
		 */
		flvplayback_internal var autoResizeMetadataDelayMax:Number = DEFAULT_AUTO_RESIZE_METADATA_DELAY_MAX;
		/**
		 * @private
		 */
		flvplayback_internal static const FINISH_AUTO_RESIZE_INTERVAL:Number = 250;  // .25 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const RTMP_DO_STOP_AT_END_INTERVAL:Number = 500; // .5 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const RTMP_DO_SEEK_INTERVAL:Number = 100; // .1 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const HTTP_DO_SEEK_INTERVAL:Number = 250; // .25 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const DEFAULT_HTTP_DO_SEEK_MAX_COUNT:Number = 4; // 4 times * .25 seconds = 1 second
		/**
		 * @private
		 */
		flvplayback_internal var httpDoSeekMaxCount:Number = DEFAULT_HTTP_DO_SEEK_MAX_COUNT;
		/**
		 * @private
		 */
		flvplayback_internal static const HTTP_DELAYED_BUFFERING_INTERVAL:Number = 100; // .1 seconds
		/**
		 * @private
		 */
		flvplayback_internal static const DEFAULT_LAST_UPDATE_TIME_STUCK_COUNT_MAX:int = 10;
		/**
		 * @private
		 */
		flvplayback_internal var lastUpdateTimeStuckCountMax:int = DEFAULT_LAST_UPDATE_TIME_STUCK_COUNT_MAX;

		// queues up Objects describing queued commands to be run later
		/**
		 * @private
		 */
		flvplayback_internal var _cmdQueue:Array;

		//ifdef DEBUG
		//protected static var _debugSingleton:VideoPlayer;
		//endif

		//
		// public APIs
		//

		/**
		 * Creates a VideoPlayer object with a specified width and height.
		 * 
		 * @param width The width of the video player in pixels.
		 * @param height The height of the video player in pixels.
		 *
		 * @see INCManager 
		 * @see NCManager 
		 * 
		 * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function VideoPlayer(width:int = 320, height:int = 240) {
			super(width, height);

			// init registration
			_registrationX = x;
			_registrationY = y;
			_registrationWidth = width;
			_registrationHeight = height;

			// init state variables
			_state = VideoState.DISCONNECTED;
			_cachedState = _state;
			_bufferState = BUFFER_EMPTY;
			_sawPlayStop = false;
			_cachedPlayheadTime = 0;
			_metadata = null;
			_startingPlay = false;
			_invalidSeekTime = false;
			_invalidSeekRecovery = false;
			_currentPos = 0;
			_atEnd = false;
			_streamLength = 0;
			_cmdQueue = new Array();
			_readyDispatched = false;
			_autoResizeDone = false;
			_lastUpdateTime = NaN;
			lastUpdateTimeStuckCount = 0;
			_sawSeekNotify = false;
			_hiddenForResize = false;
			_hiddenForResizeMetadataDelay = 0;
			_resizeImmediatelyOnMetadata = false;
			_videoWidth = -1;
			_videoHeight = -1;
			_prevVideoWidth = 0;
			_prevVideoHeight = 0;

			// put off creation of INCManager until last minute to
			// give time to customize iNCManagerClass

			// setup intervals
			_updateTimeTimer = new Timer(DEFAULT_UPDATE_TIME_INTERVAL);
			_updateTimeTimer.addEventListener(TimerEvent.TIMER, doUpdateTime);
			_updateProgressTimer = new Timer(DEFAULT_UPDATE_PROGRESS_INTERVAL);
			_updateProgressTimer.addEventListener(TimerEvent.TIMER, doUpdateProgress);
			_idleTimeoutTimer = new Timer(DEFAULT_IDLE_TIMEOUT_INTERVAL, 1);
			_idleTimeoutTimer.addEventListener(TimerEvent.TIMER, doIdleTimeout);
			_autoResizeTimer = new Timer(AUTO_RESIZE_INTERVAL);
			_autoResizeTimer.addEventListener(TimerEvent.TIMER, doAutoResize);
			_rtmpDoStopAtEndTimer = new Timer(RTMP_DO_STOP_AT_END_INTERVAL);
			_rtmpDoStopAtEndTimer.addEventListener(TimerEvent.TIMER, rtmpDoStopAtEnd);
			_rtmpDoSeekTimer = new Timer(RTMP_DO_SEEK_INTERVAL);
			_rtmpDoSeekTimer.addEventListener(TimerEvent.TIMER, rtmpDoSeek);
			_httpDoSeekTimer = new Timer(HTTP_DO_SEEK_INTERVAL);
			_httpDoSeekTimer.addEventListener(TimerEvent.TIMER, httpDoSeek);
			_httpDoSeekCount = 0;
			_finishAutoResizeTimer = new Timer(FINISH_AUTO_RESIZE_INTERVAL, 1);
			_finishAutoResizeTimer.addEventListener(TimerEvent.TIMER, finishAutoResize);
			_delayedBufferingTimer = new Timer(HTTP_DELAYED_BUFFERING_INTERVAL);
			_delayedBufferingTimer.addEventListener(TimerEvent.TIMER, doDelayedBuffering);

			// init get/set properties
			_isLive = false;
			_align = VideoAlign.CENTER;
			_scaleMode = VideoScaleMode.MAINTAIN_ASPECT_RATIO;
			_autoPlay = true;
			_autoRewind = false;
			_bufferTime = 0.1;
			_soundTransform = new SoundTransform();
			_volume = _soundTransform.volume;
			__visible = true;
			_contentPath = "";
			_isDVR = false;
			_dvrPlaying = false;
			_dvrSnapToLive = true;
			_dvrStart = false;
			_dvrIncrement = 1800;
			_dvrIncrementVariance = 300;
			_dvrFixedDuration = false;

			// init vars for progressive download auto start bandwidth detection
			waitingForEnough = false;
			baselineProgressTime = NaN;
			startProgressTime = NaN;
			totalDownloadTime = NaN;
			totalProgressTime = NaN;

			//ifdef DEBUG
			//_debugSingleton = this;
			//endif
		}

		/**
		 * Sets the <code>width</code> and <code>height</code> properties simultaneously.  
                 * Setting the <code>width</code> or <code>height</code> individually
                 * triggers two <code>autolayout</code> events whereas calling the 
                 * <code>setSize()</code> method causes only one <code>autolayout</code> event. 
		 *
		 * <p>If the <code>scaleMode</code> property is
		 * <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code> or
		 * <code>VideoScaleMode.NO_SCALE</code> then calling this method
		 * triggers an immediate <code>autolayout</code> event.</p>
		 *
		 * @param width The width of the video player.
		 * @param height The height of the video player.
		 * @see #width 
		 * @see #height 
		 * @see VideoScaleMode#MAINTAIN_ASPECT_RATIO
		 * @see VideoScaleMode#NO_SCALE
		 * 
		 * @langversion 3.0
                 * @playerversion Flash 9.0.28.0
		 */
		public function setSize(width:Number, height:Number):void {
			super.width = _registrationWidth = width;
			super.height = _registrationHeight = height;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			default:
				super.x = _registrationX;
				super.y = _registrationY;
				break;
			}
		}

		/**
		 * Sets the <code>scaleX</code> and <code>scaleY</code> properties simultaneously.  
                 * Setting the <code>scaleX</code> or <code>scaleY</code> individually
                 * triggers two <code>autolayout</code> events whereas calling the 
                 * <code>setScale()</code> method causes only one <code>autolayout</code> event. 
                 * 
		 * <p>If the <code>scaleMode</code> property is
		 * <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code> or
		 * <code>VideoScaleMode.NO_SCALE</code>, calling this method
		 * causes an immediate <code>autolayout</code> event.</p>
		 *
		 * @param scaleX A number that represents the horizontal scale.
		 * @param scaleY A number that represents the vertical scale.
		 * 
		 * @see #scaleX 
		 * @see #scaleY 
		 * @see VideoScaleMode#MAINTAIN_ASPECT_RATIO
	         * @see VideoScaleMode#NO_SCALE
		 * 
		 * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function setScale(scaleX:Number, scaleY:Number):void {
			super.scaleX = scaleX;
			super.scaleY = scaleY;
			_registrationWidth = width;
			_registrationHeight = height;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			default:
				super.x = _registrationX;
				super.y = _registrationY;
				break;
			}
		}

		/**
		 * Causes the video to play. Can be called while the video is
		 * paused or stopped, or while the video is already playing.  Call this
		 * method with no arguments to play an already loaded video or pass
		 * in a URL to load a new stream.
		 *
		 * <p>If the player is in an unresponsive state, queues the request.</p>
		 *
		 * <p>Throws an exception if called with no arguments at a time when no stream
		 * is connected. Use the <code>stateChange</code> event and the
		 * <code>connected</code> property to determine when it is
		 * safe to call this method.</p>
		 *
		 * @param url Pass in a URL string if you want to load and play a
		 * new FLV file. If you have already loaded an FLV file and want to continue
		 * playing it, pass in <code>null</code>. 
		 * 
		 * @param totalTime Pass in the length of the FLV file. Pass in <code>0</code> or <code>NaN</code>
		 * to automatically detect the length from metadata, server, or
		 * XML. If the <code>INCManager.streamLength</code> property is not <code>0</code>, 
		 * <code>null</code>, or undefined when the <code>ncConnected</code> property is called,
		 * that value takes precedence over this one.  
		 * 
		 * @param isLive Pass in <code>true</code> if streaming a live feed from Flash Media Server (FMS). 
		 * 
		 * @tiptext play method
		 * 
		 * @see #stateResponsive 
		 * @see #load() 
		 * 
		 * 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function play(url:String=null, totalTime:Number=NaN, isLive:Boolean=false):void {
			//ifdef DEBUG
			//debugTrace("play(" + url + ")");
			//endif

			// if new url passed, ask the INCManager to reconnect for us
			if (url != null) {
				if (_state == VideoState.EXEC_QUEUED_CMD) {
					_state = _cachedState;
				} else if (!stateResponsive && _state != VideoState.DISCONNECTED && _state != VideoState.CONNECTION_ERROR) {
					queueCmd(QueuedCommand.PLAY, url, isLive, totalTime);
					return;
				} else {
					execQueuedCmds();
				}
				_autoPlay = true;
				_load(url, totalTime, isLive);
				// playing will start automatically once stream is setup, so return.
				return;
			}

			if (!isXnOK()) {
				if ( _state == VideoState.CONNECTION_ERROR ||
					 _ncMgr == null || _ncMgr.netConnection == null ) {
					throw new VideoError(VideoError.NO_CONNECTION);
				} else {
					//ifdef DEBUG
					//debugTrace("RECONNECTING!!!");
					//endif
					flushQueuedCmds();
					queueCmd(QueuedCommand.PLAY);
					setState(VideoState.LOADING);
					_cachedState = VideoState.LOADING;
					_ncMgr.reconnect();
					// playing will start automatically once stream is setup, so return.
					return;
				}
			} else if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive) {
				queueCmd(QueuedCommand.PLAY);
				return;
			} else {
				execQueuedCmds();
			}

			// recreate stream if necessary (this will never happen with
			// http download, just rtmp)
			if (_ns == null) {
				_createStream();
			}

			switch (_state) {
			case VideoState.BUFFERING:
				if (_ncMgr.isRTMP) {
					_play(0);
					if (_atEnd) {
						_atEnd = false;
						_currentPos = 0;
						setState(VideoState.REWINDING);
					} else if (_currentPos > 0) {
						_seek(_currentPos);
						_currentPos = 0;
					}
				}
				// no break
			case VideoState.PLAYING:
				// already playing
				return;
			case VideoState.STOPPED:
				if (_ncMgr.isRTMP) {
					if (_isLive) {
						_play(-1);
						setState(VideoState.BUFFERING);
					} else {
						_play(0);
						if (_atEnd) {
							_atEnd = false;
							_currentPos = 0;
							_state = VideoState.BUFFERING;
							setState(VideoState.REWINDING);
						} else if (_currentPos > 0) {
							_seek(_currentPos);
							_currentPos = 0;
							setState(VideoState.BUFFERING);
						} else {
							setState(VideoState.BUFFERING);
						}
					}
				} else {
					_pause(false);
					if (_atEnd) {
						_atEnd = false;
						_seek(0);
						_state = VideoState.BUFFERING;
						setState(VideoState.REWINDING);
					} else {
						if (_bufferState == BUFFER_EMPTY) {
							setState(VideoState.BUFFERING);
						} else {
							setState(VideoState.PLAYING);
						}
					}
				}
				break;
			case VideoState.PAUSED:
				_pause(false);
				if (!_ncMgr.isRTMP) {
					if (_bufferState == BUFFER_EMPTY) {
						setState(VideoState.BUFFERING);
					} else {
						setState(VideoState.PLAYING);
					}
				} else {
					setState(VideoState.BUFFERING);
				}
				break;
			} // switch
		}

		/**
		 * Plays a stream using the Dynamic Streaming feature.  You will need to create a new
		 * DynamicStreamItem and pass that as an argument in play2.  The component will then
		 * automatically switch between the available bit rates passed in with the streams to 
		 * play the most appropriate stream bit rate for the users available bandwidth at that time.
		 * 
		 * @param dsi A DynamicStreamItem object containing the URI, streams and stream bit rates.
		 * 
		 */
		public function play2(dsi:DynamicStreamItem):void {
			var nc:INCManager = this.ncMgr;
			
			_prevVideoWidth = super.videoWidth;
			_prevVideoHeight = super.videoHeight;

			// reset state
			_autoResizeDone = false;
			_cachedPlayheadTime = 0;
			_bufferState = BUFFER_EMPTY;
			_sawPlayStop = false;
			_metadata = null;
			_startingPlay = false;
			_invalidSeekTime = false;
			_invalidSeekRecovery = false;
			_contentPath = "";
			_currentPos = 0;
			_streamLength = NaN;
			_atEnd = false;
			_readyDispatched = false;
			_lastUpdateTime = NaN;
			lastUpdateTimeStuckCount = 0;
			_sawSeekNotify = false;
			waitingForEnough = false;
			baselineProgressTime = NaN;
			startProgressTime = NaN;
			totalDownloadTime = NaN;
			totalProgressTime = NaN;
			_httpDoSeekCount = 0;

			// must stop ALL intervals here
			_updateTimeTimer.reset();
			_updateProgressTimer.reset();
			_idleTimeoutTimer.reset();
			_autoResizeTimer.reset();
			_rtmpDoStopAtEndTimer.reset();
			_rtmpDoSeekTimer.reset();
			_httpDoSeekTimer.reset();
			_finishAutoResizeTimer.reset();
			_delayedBufferingTimer.reset();

			// close netstream
			closeNS(false);
			
			if(dsi.start == -1){
				_autoPlay = false;
				_isLive = true;
				_updateProgressTimer.start();
			}
			nc.connectDynamicStream(dsi);
			_streamLength = NaN;
			setState(VideoState.LOADING);
			_cachedState = VideoState.LOADING;
		}


		/**
		 * Plays the FLV file when enough of it has downloaded. If the FLV file has downloaded 
		 * or you are streaming from Flash Media Server (FMS), then calling the <code>playWhenEnoughDownloaded()</code>
		 * method is identical to the <code>play()</code> method with no parameters. Calling 
		 * this method does not pause playback, so in many cases, you may want to call 
		 * the <code>pause()</code> method before you call this method.
		 * 
		 * @tiptext playWhenEnoughDownloaded method
		 * 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function playWhenEnoughDownloaded():void {
			if (_ncMgr != null && _ncMgr.isRTMP) {
				play();
				return;
			}

			if (!isXnOK()) {
				throw new VideoError(VideoError.NO_CONNECTION);
			} else if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive) {
				queueCmd(QueuedCommand.PLAY_WHEN_ENOUGH);
				return;
			} else {
				execQueuedCmds();
			}

			waitingForEnough = true;
			checkReadyForPlay(bytesLoaded, bytesTotal);
		}

		/**
		 * Similar to the <code>play()</code> method, but causes the FLV file 
		 * to load without playing. Autoresizing occurs if appropriate, and the first
		 * frame of the FLV file is shown. 
		 * After initial loading and autolayout, the state is <code>VideoState.PAUSED</code>.
		 *
		 * <p>This method takes the same parameters as the <code>play()</code> method, 
		 * but you cannot call the <code>load()</code> method without a URL.
		 * If you do, an error is thrown. If the video player is in an unresponsive state, 
		 * the <code>load()</code> method queues the request.</p>
		 *
		 * @param url A URL string for the FLV file that you want to load. If no value is 
		 * passed for URL, an error is thrown with the message 
		 * <code>null URL sent to VideoPlayer.load</code>.
		 * 
		 * @param totalTime The length of an FLV file. Pass in 0, <code>null</code>, or
		 * undefined to automatically detect length from metadata, server, or XML. 
		 *  
		 * @param isLive The value is <code>true</code> if you stream a live feed from Flash Media Server (FMS). 
		 * 
		 * @see #play() 
		 * 
		 * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function load(url:String, totalTime:Number=NaN, isLive:Boolean=false):void {
			if (url == null) {
				throw new VideoError(VideoError.NULL_URL_LOAD);
			}

			//ifdef DEBUG
			//debugTrace("load(" + url + ")");
			//endif

			if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive && _state != VideoState.DISCONNECTED && _state != VideoState.CONNECTION_ERROR) {
				queueCmd(QueuedCommand.LOAD, url, isLive, totalTime);
				return;
			} else {
				execQueuedCmds();
			}
			_autoPlay = false;
			_load(url, totalTime, isLive);
		}

		/**
		 * Does loading work for play and load.
		 *
		 * @private
		 */
		flvplayback_internal function _load(url:String, totalTime:Number, isLive:Boolean):void {
			//ifdef DEBUG
			//debugTrace("_load(" + url + ", " + isLive + ", " + totalTime + ")");
			//endif
			_prevVideoWidth = super.videoWidth;
			_prevVideoHeight = super.videoHeight;

			// reset state
			_autoResizeDone = false;
			_cachedPlayheadTime = 0;
			_bufferState = BUFFER_EMPTY;
			_sawPlayStop = false;
			_metadata = null;
			_startingPlay = false;
			_invalidSeekTime = false;
			_invalidSeekRecovery = false;
			_isLive = isLive;
			_contentPath = url;
			_currentPos = 0;
			_streamLength = (isNaN(totalTime) || totalTime <= 0) ? NaN : totalTime;
			_atEnd = false;
			_readyDispatched = false;
			_lastUpdateTime = NaN;
			lastUpdateTimeStuckCount = 0;
			_sawSeekNotify = false;
			waitingForEnough = false;
			baselineProgressTime = NaN;
			startProgressTime = NaN;
			totalDownloadTime = NaN;
			totalProgressTime = NaN;
			_httpDoSeekCount = 0;

			// must stop ALL intervals here
			_updateTimeTimer.reset();
			_updateProgressTimer.reset();
			_idleTimeoutTimer.reset();
			_autoResizeTimer.reset();
			_rtmpDoStopAtEndTimer.reset();
			_rtmpDoSeekTimer.reset();
			_httpDoSeekTimer.reset();
			_finishAutoResizeTimer.reset();
			_delayedBufferingTimer.reset();

			// close netstream
			closeNS(false);

			// if returns false, wait for a "connected" message and
			// then do these things
			if (_ncMgr == null) {
				createINCManager();
			}
			var instantConnect:Boolean = _ncMgr.connectToURL(_contentPath);
			setState(VideoState.LOADING);
			_cachedState = VideoState.LOADING;
			if (instantConnect) {
				_createStream();
				_setUpStream();
			}
			if (!_ncMgr.isRTMP) {
				_updateProgressTimer.start();
			}
		}

		/**
		 * Pauses video playback.  If video is paused or stopped, has
		 * no effect.  To start playback again, call <code>play()</code>.
		 * Takes no parameters.
		 *
		 * <p>If player is in an unresponsive state, the <code>pause()</code> method
		 * queues the request.</p>
		 *
		 * <p>Throws an exception if called when no stream is
         * connected.  Use the <code>stateChange</code> event and
		 * <code>connected</code> property to determine when it is
		 * safe to call this method.</p>
		 *
         * <p>If the player is in a stopped state, a call to the <code>pause()</code> 
		 * method has no effect and the player remains in a stopped state.</p>
		 *
		 * @see #stateResponsive
         * @see #play()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function pause():void {
			//ifdef DEBUG
			//debugTrace("pause()");
			//endif

			if (!isXnOK()) {
				if ( _state == VideoState.CONNECTION_ERROR ||
					 _ncMgr == null || _ncMgr.netConnection == null ) {
					throw new VideoError(VideoError.NO_CONNECTION);
				} else {
					return;
				}
			} else if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive) {
				queueCmd(QueuedCommand.PAUSE);
				return;
			} else {
				execQueuedCmds();
			}
			if (_state == VideoState.PAUSED || _state == VideoState.STOPPED || _ns == null) return;
			_pause(true);
			setState(VideoState.PAUSED);
		}

		/**
		 * Stops video playback.  If <code>autoRewind</code> is set to
		 * <code>true</code>, rewinds to first frame.  If video is already
		 * stopped, has no effect.  To start playback again, call
		 * <code>play()</code>.  Takes no parameters.
		 *
		 * <p>If player is in an unresponsive state, queues the request.</p>
		 *
		 * <p>Throws an exception if called when no stream is
         * connected.  Use the <code>stateChange</code> event and
		 * <code>connected</code> property to determine when it is
		 * safe to call this method.</p>
		 *
		 * @see #stateResponsive
		 * @see #autoRewind
         * @see #play()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function stop():void {
			//ifdef DEBUG
			//debugTrace("stop()");
			//endif

			if (!isXnOK()) {
				if ( _state == VideoState.CONNECTION_ERROR ||
					 _ncMgr == null || _ncMgr.netConnection == null ) {
					throw new VideoError(VideoError.NO_CONNECTION);
				} else {
					return;
				}
			} else if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive) {
				queueCmd(QueuedCommand.STOP);
				return;
			} else {
				execQueuedCmds();
			}
			if (_state == VideoState.STOPPED || _ns == null) return;
			if (_ncMgr.isRTMP) {
				if (_autoRewind && !_isLive) {
					_currentPos = 0;
					_play(0, 0);
					_state = VideoState.STOPPED;
					setState(VideoState.REWINDING);
				} else {
					closeNS(true);
					setState(VideoState.STOPPED);
				}
			} else {
				_pause(true);
				if (_autoRewind) {
					_seek(0);
					_state = VideoState.STOPPED;
					setState(VideoState.REWINDING);
				} else {
					setState(VideoState.STOPPED);
				}
			}
		}

		/**
		 * Seeks to a given time in the file, specified in seconds, with a precision of three
		 * decimal places (milliseconds). If a video is playing, the video continues to play
		 * from that point. If a video is paused, the video seeks to that point and remains paused. 
		 * If a video is stopped, the video seeks 
		 * to that point and enters the paused state. Has no effect with live streams.
		 *
         * <p>The <code>playheadTime</code> property might not have the expected value 
		 * immediately after you call one of the seek methods or set  
		 * <code>playheadTime</code> to cause seeking. For a progressive download,
		 * you can seek only to a keyframe; therefore, a seek takes you to the 
		 * time of the first keyframe after the specified time.</p>
		 * 
		 * <p><strong>Note</strong>: When streaming, a seek always goes to the precise specified 
		 * time even if the source FLV file doesn't have a keyframe there.</p>
		 *
		 * <p>Seeking is asynchronous, so if you call a seek method or set the 
		 * <code>playheadTime</code> property, <code>playheadTime</code> does not update immediately. 
		 * To obtain the time after the seek is complete, listen for the <code>seek</code> event, 
		 * which does not start until the <code>playheadTime</code> property is updated.</p>
		 *
		 * <p>Throws an exception if called when no stream is
         * connected.  Use the <code>stateChange</code> event and
		 * the <code>connected</code> property to determine when it is
		 * safe to call this method.</p>
		 *
		 * @param time A number that specifies the time, in seconds, at which to place the 
		 * playhead.
		 * @throws fl.video.VideoError If time is &lt; 0 or <code>NaN</code>.
         * @see #stateResponsive
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function seek(time:Number):void {
			//ifdef DEBUG
			//debugTrace("seek:"+time);
			//endif
			// we do not allow more seeks until we are out of an invalid seek time state
			if (_invalidSeekTime) return;
			if (isNaN(time) || time < 0) throw new VideoError(VideoError.INVALID_SEEK);
			if (!isXnOK()) {
				if ( _state == VideoState.CONNECTION_ERROR ||
					 _ncMgr == null || _ncMgr.netConnection == null ) {
					throw new VideoError(VideoError.NO_CONNECTION);
				} else {
					//ifdef DEBUG
					//debugTrace("RECONNECTING!!!");
					//endif
					flushQueuedCmds();
					queueCmd(QueuedCommand.SEEK, null, false, time);
					setState(VideoState.LOADING);
					_cachedState = VideoState.LOADING;
					_ncMgr.reconnect();
					// playing will start automatically once stream is setup, so return.
					return;
				}
			} else if (_state == VideoState.EXEC_QUEUED_CMD) {
				_state = _cachedState;
			} else if (!stateResponsive) {
				queueCmd(QueuedCommand.SEEK, null, false, time);
				return;
			} else {
				execQueuedCmds();
			}

			// recreate stream if necessary (this will never happen with
			// http download, just rtmp)
			if (_ns == null) {
				_createStream();
			}

			if (_atEnd && time < playheadTime) {
				_atEnd = false;
			}

			switch (_state) {
			case VideoState.PLAYING:
				_state = VideoState.BUFFERING;
				// no break;
			case VideoState.BUFFERING:
			case VideoState.PAUSED:
				_seek(time);
				setState(VideoState.SEEKING);
				break;
			case VideoState.STOPPED:
				if (_ncMgr.isRTMP) {
					_play(0);
					_pause(true);
				}
				_seek(time);
				_state = VideoState.PAUSED;
				setState(VideoState.SEEKING);
				break;
			}
		}

		/**
		 * Forces the video stream and Flash Media Server connection to close. This
		 * method triggers the <code>close</code> event. You usually do not need to call this 
		 * method directly, because the idle timeout functionality takes care of closing the stream.
		 *
         * @see #idleTimeout
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function close():void {
			//ifdef DEBUG
			//debugTrace("close()");
			//endif
			closeNS(true);
			// never makes sense to close an http NetConnection, it doesn't really maintain
			// any kind of network connection!
			if (_ncMgr != null && _ncMgr.isRTMP) {
				_ncMgr.close();
			}
			setState(VideoState.DISCONNECTED);
			dispatchEvent(new VideoEvent(VideoEvent.CLOSE, false, false, _state, playheadTime));
		}


		//
		// public getters, setters
		//


		/**
		 * A number that is the horizontal scale. 
		 *
		 * @default 1
         * @see #setScale()
		 * @see #scaleY
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set scaleX(xs:Number):void {
			super.scaleX = xs;
			_registrationWidth = width;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			}
		}

		/**
		 * A number that is the vertical scale. 
		 *
		 * @default 1
         * @see #setScale()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set scaleY(ys:Number):void {
			super.scaleY = ys;
			_registrationHeight = height;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			}
		}

		/**
		 * A number that specifies the horizontal position (in pixels) of the video player.
		 * 
		 * <p>Setting the <code>x</code> property also affects the <code>registrationX</code> property. 
		 * When either the <code>x</code> or <code>registrationX</code> property is set,
		 * the second property is changed to maintain its offset from the
		 * first. For example, if <code>x</code> = 10 and <code>registrationX</code> = 20, setting 
		 * <code>x</code> = 110 also sets <code>registrationX</code> = 120.</p>
		 *
         * @see #registrationX
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set x(x:Number):void {
			if (this.x != x) {
				var delta:Number = x - this.x;
				super.x = x;
				_registrationX += delta;
			}
		}

		/**
		 * A number that specifies the vertical position (in pixels) of the video player.
		 * 
		 * <p>Setting the <code>y</code> property also affects the <code>registrationY</code> property. 
		 * When either the <code>y</code> or <code>registrationY</code> property is set,
		 * the second property is changed to maintain its offset from the
		 * first. For example, if <code>y</code> = 10 and <code>registrationY</code> = 20, setting 
		 * <code>y</code> = 110 also sets <code>registrationY</code> = 120.</p>
		 *
         * @see #registrationY
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set y(y:Number):void {
			if (this.y != y) {
				var delta:Number = y - this.y;
				super.y = y;
				_registrationY += delta;
			}
		}

		/**
		 * A number that specifies the width of the VideoPlayer instance on the Stage.
		 * 
		 * <p><strong>Note</strong>: The <code>flash.media.Video.width</code> property is similar to the 
		 *  <code>fl.video.VideoPlayer.videoWidth</code> property.</p>
         *
		 *
		 * @see #setSize()
         * @see flash.media.Video#videoWidth Video.videoWidth
		 * @see #videoWidth VideoPlayer.videoWidth
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set width(w:Number):void {
			super.width = _registrationWidth = w;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			default:
				super.width = w;
				break;
			}
		}

		/**
		 * A number that specifies the height of the VideoPlayer instance (in pixels). 
		 * 
		 * <p><strong>Note</strong>: Do not confuse this property with the 
		 * <code>flash.media.Video.height</code> property which is similar to the 
		 * <code>fl.video.VideoPlayer.videoHeight</code> property.</p>
         *
		 *
		 * @see #setSize()
         * @see flash.media.Video#videoHeight Video.videoHeight
		 * @see #videoHeight VideoPlayer.videoHeight
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function set height(h:Number):void {
			super.height = _registrationHeight = h;
			switch (_scaleMode) {
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			case VideoScaleMode.NO_SCALE:
				startAutoResize();
				break;
			default:
				super.height = h;
				break;
			}
		}

		/**
		 * The x coordinate used to align the video content when 
		 * autoresizing. Do not confuse with the <code>x</code> property,
		 * which reflects the actual location of the video content.
		 *
		 * <p>Example 1, load an 80x80 FLV file with the following settings:
		 * <ul>
		 *     <li><code>registrationX</code> = 100</li> 
		 *     <li><code>registrationY</code> = 100</li>
		 *     <li><code>registrationWidth</code> = 60</li> 
		 *     <li><code>registrationHeight</code> = 40</li>
		 *     <li><code>align</code> = <code>VideoAlign.CENTER</code></li> 
		 *     <li><code>scaleMode</code> = <code>VideoScaleMode.NO_SCALE</code></li>
		 * </ul>
		 * 
		 * 
		 * Then, after automatic resizing, you get the following:
		 * <ul>
         *     <li><code>x</code> = 90</li>
         *     <li><code>y</code> = 80</li>
         *     <li><code>width</code> = 80</li>
         *     <li><code>height</code> = 80</li>  
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 2, load the same scenario as example 1 but with the following settings:
		 * <ul>
		 *     <li><code>scaleMode</code> = <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code></li>
		 * </ul>
		 * 
		 * You get the following:
		 * <ul>
		 * <li><code>x</code> = 110</li>
		 * <li><code>y</code> = 100</li> 
		 * <li><code>width</code> = 40</li> 
		 * <li><code>height</code> = 80</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 3, load the same scenario as example 2 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.EXACT_FIT</code></li>
		 * </ul>
		 * 
		 * You get all the same values as the registration values:
		 * <ul>
		 * <li><code>x</code> = 100</li> 
		 * <li><code>y</code> = 100</li>
		 * <li><code>width</code> = 60</li> 
		 * <li><code>height</code> = 40</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>When either the <code>x</code> or <code>registrationX</code>
		 * property is set, the second property is changed to maintain its
		 * offset from the first.  For example, if <code>x</code> = 10 and 
		 * <code>registrationX</code> = 20, setting <code>x</code> = 110 also 
		 * sets <code>registrationX</code> = 120.</p>
		 *
		 * @see #registrationY
		 * @see #registrationWidth
		 * @see #registrationHeight
         * @see VideoAlign
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get registrationX():Number {
			return _registrationX;
		}
        /**
         * @private (setter)
         */
		public function set registrationX(x:Number):void {
			if (_registrationX != x) {
				var delta:Number = x - _registrationX;
				_registrationX = x;
				this.x += delta;
			}
		}

		/**
		 * The y coordinate used to align the video content when 
		 * autoresizing. Do not confuse with the <code>y</code> property,
		 * which reflects the actual location of the video content.
		 *
		 * <p>Example 1, load an 80x80 FLV file with the following settings:
		 * <ul>
		 * <li><code>registrationX</code> = 100</li> 
		 * <li><code>registrationY</code> = 100</li>
		 * <li><code>registrationWidth</code> = 60</li> 
		 * <li><code>registrationHeight</code> = 40</li>
		 * <li><code>align</code> = <code>VideoAlign.CENTER</code></li> 
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.NO_SCALE</code></li>
		 * </ul>
		 * 
		 * 
		 * Then, after automatic resizing, you get the following:
		 * <ul>
		 * <li><code>x</code> = 90</li>
		 * <li><code>y</code> = 80</li>
		 * <li><code>width</code> = 80</li>
		 * <li><code>height</code> = 80</li>  
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 2, load the same scenario as example 1 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code></li>
		 * </ul>
		 * 
		 * You get the following:
		 * <ul>
		 * <li><code>x</code> = 110</li>
		 * <li><code>y</code> = 100</li> 
		 * <li><code>width</code> = 40</li> 
		 * <li><code>height</code> = 80</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 3, load the same scenario as example 2 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.EXACT_FIT</code></li>
		 * </ul>
		 * 
		 * You get all the same values as the registration values:
		 * <ul>
		 * <li><code>x</code> = 100</li> 
		 * <li><code>y</code> = 100</li>
		 * <li><code>width</code> = 60</li> 
		 * <li><code>height</code> = 40</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>When either the <code>y</code> or <code>registrationY</code>
		 * property is set, the second property is changed to maintain its
		 * offset from the first.  For example, if <code>y</code> = 10 and 
		 * <code>registrationY</code> = 20, setting <code>y</code> = 110 also 
		 * sets <code>registrationY</code> = 120.</p>
		 *
		 * @see #registrationX
		 * @see #registrationWidth
		 * @see #registrationHeight
         * @see VideoAlign
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get registrationY():Number {
			return _registrationY;
		}
        /**
         * @private (setter)
         */
		public function set registrationY(y:Number):void {
			if (_registrationY != y) {
				var delta:Number = y - _registrationY;
				_registrationY = y;
				this.y += delta;
			}
		}

		/**
		 * The width used to align the video content when autoresizing.
		 * Do not confuse the <code>registrationWidth</code> property 
		 * with the <code>width</code> property. The <code>width</code>
		 * property reflects the actual width of the video content.
		 *
		 * <p>Example 1, load an 80x80 FLV file with the following settings:
		 * <ul>
		 * <li><code>registrationX</code> = 100</li> 
		 * <li><code>registrationY</code> = 100</li>
		 * <li><code>registrationWidth</code> = 60</li> 
		 * <li><code>registrationHeight</code> = 40</li>
		 * <li><code>align</code> = <code>VideoAlign.CENTER</code></li> 
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.NO_SCALE</code></li>
		 * </ul>
		 * 
		 * 
		 * Then, after automatic resizing, you get the following:
		 * <ul>
		 * <li><code>x</code> = 90</li>
		 * <li><code>y</code> = 80</li>
		 * <li><code>width</code> = 80</li>
		 * <li><code>height</code> = 80</li>  
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 2, load the same scenario as example 1 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code></li>
		 * </ul>
		 * 
		 * You get the following:
		 * <ul>
		 * <li><code>x</code> = 110</li>
		 * <li><code>y</code> = 100</li> 
		 * <li><code>width</code> = 40</li> 
		 * <li><code>height</code> = 80</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 3, load the same scenario as example 2 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.EXACT_FIT</code></li>
		 * </ul>
		 * 
		 * You get all the same values as the registration values:
		 * <ul>
		 * <li><code>x</code> = 100</li> 
		 * <li><code>y</code> = 100</li>
		 * <li><code>width</code> = 60</li> 
		 * <li><code>height</code> = 40</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Setting the <code>registrationWidth</code> property is
		 * equivalent to setting the <code>width</code> property and vice
		 * versa. Setting any other property or calling any method that
		 * alters the width, such as <code>scaleX</code>,
		 * <code>setSize</code>, and <code>setScale</code>, also keeps
		 * <code>width</code> and <code>registrationWidth</code> in sync.</p>
		 *
		 * @see #registrationX
		 * @see #registrationY
		 * @see #registrationHeight
         * @see VideoAlign
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get registrationWidth():Number {
			return _registrationWidth;
		}
        /**
         * @private (setter)
         */
		public function set registrationWidth(w:Number):void {
			width = w;
		}

		/**
		 * The height used to align the video content when autoresizing.
		 * Do not confuse the <code>registrationHeight</code> property 
		 * with the <code>height</code> property. The <code>height</code>
		 * property reflects the actual height of the video content.
		 *
		 * <p>Example 1, load an 80x80 FLV file with the following settings:
		 * <ul>
		 * <li><code>registrationX</code> = 100</li> 
		 * <li><code>registrationY</code> = 100</li>
		 * <li><code>registrationWidth</code> = 60</li> 
		 * <li><code>registrationHeight</code> = 40</li>
		 * <li><code>align</code> = <code>VideoAlign.CENTER</code></li> 
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.NO_SCALE</code></li>
		 * </ul>
		 * 
		 * 
		 * Then, after automatic resizing, you get the following:
		 * <ul>
		 * <li><code>x</code> = 90</li>
		 * <li><code>y</code> = 80</li>
		 * <li><code>width</code> = 80</li>
		 * <li><code>height</code> = 80</li>  
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 2, load the same scenario as example 1 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code></li>
		 * </ul>
		 * 
		 * You get the following:
		 * <ul>
		 * <li><code>x</code> = 110</li>
		 * <li><code>y</code> = 100</li> 
		 * <li><code>width</code> = 40</li> 
		 * <li><code>height</code> = 80</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Example 3, load the same scenario as example 2 but with the following settings:
		 * <ul>
		 * <li><code>scaleMode</code> = <code>VideoScaleMode.EXACT_FIT</code></li>
		 * </ul>
		 * 
		 * You get all the same values as the registration values:
		 * <ul>
		 * <li><code>x</code> = 100</li> 
		 * <li><code>y</code> = 100</li>
		 * <li><code>width</code> = 60</li> 
		 * <li><code>height</code> = 40</li>
		 * </ul>
		 * </p>
		 * 
		 * <p>Setting the <code>registrationHeight</code> property is
		 * equivalent to setting the <code>height</code> property and vice
		 * versa. Setting any other property or calling any method that
		 * alters the height, such as <code>scaleX</code>,
		 * <code>setSize</code>, and <code>setScale</code>, setting keeps
		 * <code>height</code> and <code>registrationHeight</code> in sync.</p>
		 *
		 * @see #registrationX
		 * @see #registrationY
		 * @see #registrationWidth
         * @see VideoAlign
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get registrationHeight():Number {
			return _registrationHeight;
		}
        /**
         * @private (setter)
         */
		public function set registrationHeight(h:Number):void {
			height = h;
		}

		/**
		 * The source width of the loaded FLV file. This property returns
		 * -1 if no information is available yet.
		 * 
		 *
         * @see #width
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function get videoWidth():int {
			// _videoWidth and _videoHeight come from the NCManager, which would normally mean they
			// came from the SMIL and they get top priority if they are non-negative
			if (_videoWidth > 0) return _videoWidth;
			// Next priority is the metadata height and width.  If the metadata height and width are the same,
			// then it might be buggy metadata from an older version of the sorenson encoder, so we ignore it
			// and use the super.videoWidth and super.videoHeight instead ONLY if ready has been dispatched.
			// this is because we never consider the super.videoWidth and super.videoHeight to be ready
			// until ready is dispatched--it could still be 0 or still match the last video loaded
			if (_metadata != null && !isNaN(_metadata.width) && !isNaN(_metadata.height)) {
				if (_metadata.width == _metadata.height && _readyDispatched) {
					return super.videoWidth;
				} else {
					return int(_metadata.width);
				}
			}
			// last priority is the super.videoWidth and the super.videoHeight, which is
			// only used if ready has been dispatched, otherwise return -1
			if (_readyDispatched) return super.videoWidth;
			return -1;
		}

		/**
		 * The source width of the loaded FLV file. This property returns
		 * -1 if no information is available yet.
		 *
         * @see #height
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function get videoHeight():int {
			// _videoWidth and _videoHeight come from the NCManager, which would normally mean they
			// came from the SMIL and they get top priority if they are non-negative
			if (_videoHeight > 0) return _videoHeight;
			// Next priority is the metadata height and width.  If the metadata height and width are the same,
			// then it might be buggy metadata from an older version of the sorenson encoder, so we ignore it
			// and use the super.videoWidth and super.videoHeight instead ONLY if ready has been dispatched.
			// this is because we never consider the super.videoWidth and super.videoHeight to be ready
			// until ready is dispatched--it could still be 0 or still match the last video loaded
			if (_metadata != null && !isNaN(_metadata.width) && !isNaN(_metadata.height)) {
				if (_metadata.width == _metadata.height && _readyDispatched) {
					return super.videoHeight;
				} else {
					return int(_metadata.height);
				}
			}
			// last priority is the super.videoWidth and the super.videoHeight, which is
			// only used if ready has been dispatched, otherwise return -1
			if (_readyDispatched) return super.videoHeight;
			return -1;
  		}

		/**
		 * A Boolean value that, if <code>true</code>, makes the VideoPlayer instance visible. 
		 * If <code>false</code>, it makes the instance invisible. 
		 * 
		 * @default true
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public override function get visible():Boolean {
			if (!_hiddenForResize) __visible = super.visible;
			return __visible;
		}
        /**
         * @private (setter)
         */
		public override function set visible(v:Boolean):void {
			__visible = v;
			if (!_hiddenForResize) super.visible = __visible;
		}

		/**
		 * Specifies how the video is displayed relative to the
		 * <code>registrationX</code>, <code>registrationY</code>,
		 * <code>registrationWidth</code> and
		 * <code>registrationHeight</code> properties. The <code>align</code> property does 
		 * this autolayout when the <code>scaleMode</code> property is set to
		 * <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code> or
		 * <code>VideoScaleMode.NO_SCALE</code>. Changing this property
		 * after an FLV file is loaded causes an automatic layout to
		 * start immediately.
		 * Values come
		 * from the VideoAlign class.
         *
         * @default VideoAlign.CENTER
		 *
		 * @see #registrationX
		 * @see #registrationY
		 * @see #registrationWidth
		 * @see #registrationHeight
		 * @see #scaleMode
         * @see VideoAlign
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get align():String {
			return _align;
		}
        /**
         * @private (setter)
         */
		public function set align(s:String):void {
			if (_align != s) {
				// check for valid value
				switch (s) {
				case VideoAlign.CENTER:
				case VideoAlign.TOP:
				case VideoAlign.LEFT:
				case VideoAlign.BOTTOM:
				case VideoAlign.RIGHT:
				case VideoAlign.TOP_LEFT:
				case VideoAlign.TOP_RIGHT:
				case VideoAlign.BOTTOM_LEFT:
				case VideoAlign.BOTTOM_RIGHT:
					break;
				default:
					// just ignore bad values, no error
					return;
				}
				_align = s;
				switch (_scaleMode) {
				case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
				case VideoScaleMode.NO_SCALE:
					startAutoResize();
					break;
				}
			}
		}

		/**
		 * Specifies how the video resizes after loading.  If set to
		 * <code>VideoScaleMode.MAINTAIN_ASPECT_RATIO</code>, maintains the
		 * video aspect ratio within the rectangle defined by
		 * <code>registrationX</code>, <code>registrationY</code>,
		 * <code>registrationWidth</code> and
		 * <code>registrationHeight</code>.  If set to
		 * <code>VideoScaleMode.NO_SCALE</code>, causes the video to size automatically
		 * to the dimensions of the source FLV file.  If set to
		 * <code>VideoScaleMode.EXACT_FIT</code>, causes the dimensions of
		 * the source FLV file to be ignored and the video is stretched to
		 * fit the rectangle defined by
		 * <code>registrationX</code>, <code>registrationY</code>,
		 * <code>registrationWidth</code> and
		 * <code>registrationHeight</code>. If this is set
		 * after an FLV file has been loaded an automatic layout will start
		 * immediately.  Values come from
		 * <code>VideoScaleMode</code>.
		 *
		 * @see VideoScaleMode
		 * @default VideoScaleMode.MAINTAIN_ASPECT_RATIO
                 *
                 * @langversion 3.0
                 * @playerversion Flash 9.0.28.0
		 */
		public function get scaleMode():String {
			return _scaleMode;
        }
        /**
         * @private (setter)
         */
		public function set scaleMode(s:String):void {
			if (_scaleMode != s) {
				// check for valid value
				switch (s) {
				case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
				case VideoScaleMode.NO_SCALE:
				case VideoScaleMode.EXACT_FIT:
					break;
				default:
					// just ignore bad values, no error
					return;
				}
				if ( _scaleMode == VideoScaleMode.EXACT_FIT && _resizeImmediatelyOnMetadata &&
				     (_videoWidth < 0 || _videoHeight < 0 ) ) {
					_resizeImmediatelyOnMetadata = false;
				}
				_scaleMode = s;
				startAutoResize();
			}
		}

		/**
		 * A Boolean value that, if <code>true</code>, causes the FLV file to rewind to 
		 * Frame 1 when play stops, either because the player reached the end of the 
		 * stream or the <code>stop()</code> method was called. This property is 
		 * meaningless for live streams.
         * 
         * @default false
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get autoRewind():Boolean {
			return _autoRewind;
        }
        /**
         * @private (setter)
         */
		public function set autoRewind(flag:Boolean):void {
			_autoRewind = flag;
		}

		/**
		 * A number that is the current playhead time or position, measured in seconds, 
		 * which can be a fractional value. Setting this property triggers a seek and 
		 * has all the restrictions of a seek.
		 * 
		 * <p>When the playhead time changes, which includes once every .25 seconds 
		 * while the FLV file plays, the component dispatches the <code>playheadUpdate</code>
		 * event.</p>
		 * 
		 * <p>For several reasons, the <code>playheadTime</code> property might not have the expected 
		 * value immediately after calling one of the seek methods or setting <code>playheadTime</code> 
		 * to cause seeking. First, for a progressive download, you can seek only to a 
		 * keyframe, so a seek takes you to the time of the first keyframe after the 
		 * specified time. (When streaming, a seek always goes to the precise specified 
		 * time even if the source FLV file doesn't have a keyframe there.) Second, 
		 * seeking is asynchronous, so if you call a seek method or set the 
		 * playheadTime property, <code>playheadTime</code> does not update immediately. 
		 * To obtain the time after the seek is complete, listen for the <code>seek</code> event, 
		 * which does not fire until the <code>playheadTime</code> property has updated.</p>
		 *
		 * @tiptext Current position of the playhead in seconds
		 * @see #seek()
         * @see FLVPlayback#playheadTime
		 * 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get playheadTime():Number {
			var nowTime:Number = (_ns == null) ? _currentPos : _ns.time;
			if (_metadata != null && _metadata.audiodelay != undefined) {
				nowTime -= _metadata.audiodelay;
				if (nowTime < 0) nowTime = 0;
			}
			return nowTime;
        }
        /**
         * @private (setter)
         */
		public function set playheadTime(position:Number):void {
			seek(position);
		}

		/**
		 * A string that specifies the URL of the FLV file to stream and how to stream it.
		 * The URL can be an HTTP URL to an FLV file, an RTMP URL to a stream, or an 
		 * HTTP URL to an XML file.
		 *
		 * <p>If you set this property through the Component inspector or the Property inspector, 
		 * the FLV file begins loading and playing at the next <code>enterFrame</code> event.
		 * The delay provides time to set the <code>isLive</code>, <code>autoPlay</code>, 
		 * and <code>cuePoints</code> properties, 
		 * among others, which affect loading. It also allows ActionScript that is placed 
		 * on the first frame to affect the FLVPlayback component before it starts playing.</p>
		 *
		 * <p>If you set this property through ActionScript, it immediately calls the
		 * <code>VideoPlayer.load()</code> method when the <code>autoPlay</code> property is
		 * set to <code>false</code>. Alternatively, it calls the <code>VideoPlayer.play()</code> method when
		 * the <code>autoPlay</code> property is set to <code>true</code>.  The <code>autoPlay</code>, 
		 * <code>totalTime</code>, and <code>isLive</code> properties affect how the new FLV file is 
		 * loaded, so if you set these properties, you must set them before setting the
		 * <code>source</code> property.</p>
		 * 
		 * <p>Set the <code>autoPlay</code> property to <code>false</code> to prevent the new 
		 * FLV file from playing automatically.</p>
		 * 
		 * @see FLVPlayback#autoPlay 
		 * @see #isLive
		 * @see #totalTime
		 * @see #load()
		 * @see #play()
		 * @see FLVPlayback#load()
         * @see FLVPlayback#play()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get source():String {
			return _contentPath;
		}

		/**
		 * A number in the range of 0 to 1 that indicates the volume control setting. 
		 * @default 1
		 *
		 * @tiptext The volume setting in value range from 0 to 1.
		 * 
         * @see #soundTransform
         *
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get volume():Number {
			return soundTransform.volume;
        }
        /**
         * @private (setter)
         */
		public function set volume(aVol:Number):void {
			var st:SoundTransform = soundTransform;
			st.volume = aVol;
			soundTransform = st;
		}

		/**
		 * Provides direct access to the
		 * <code>NetStream.soundTransform</code> property to expose
		 * more sound control. Set the property to change the settings;
		 * use the getter accessor method of the property to retrieve 
		 * the current settings.
		 *
         * @see #volume
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get soundTransform():SoundTransform {
			if (_ns != null) _soundTransform = _ns.soundTransform;
			var st:SoundTransform = new SoundTransform();
			st.volume = (_hiddenForResize) ? _volume : _soundTransform.volume;
			st.leftToLeft = _soundTransform.leftToLeft;
			st.leftToRight = _soundTransform.leftToRight;
			st.rightToLeft = _soundTransform.rightToLeft;
			st.rightToRight = _soundTransform.rightToRight;
			return st;
        }
        /**
         * @private (setter)
         */
		public function set soundTransform(st:SoundTransform):void {
			if (st == null) return;

			if (_hiddenForResize) {
				_volume = st.volume;
			}

			_soundTransform = new SoundTransform();
			_soundTransform.volume = (_hiddenForResize) ? 0 : st.volume;
			_soundTransform.leftToLeft = st.leftToLeft;
			_soundTransform.leftToRight = st.leftToRight;
			_soundTransform.rightToLeft = st.rightToLeft;
			_soundTransform.rightToRight = st.rightToRight;

			if (_ns != null) _ns.soundTransform = _soundTransform;
		}

		/**
		 * A Boolean value that is <code>true</code> if the FLV file is streaming from Flash Media Server (FMS) using RTMP. 
		 * Its value is <code>false</code> for any other FLV file source. 
		 *
         * @see FLVPlayback#isRTMP
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get isRTMP():Boolean {
			if (_ncMgr == null) return false;
			return _ncMgr.isRTMP;
		}

		/**
		 * A Boolean value that is <code>true</code> if the video stream is live. This property 
		 * is effective only when streaming from a video server, such as Flash Media Server or other Flash Video Streaming Service. The value of this 
		 * property is ignored for an HTTP download.
		 * 
		 * <p>Set the <code>isLive</code> property to <code>false</code> when sending a prerecorded video 
		 * stream to the video player and to <code>true</code> when sending real-time data 
		 * such as a live broadcast. For better performance when you set 
		 * the <code>isLive</code> property to <code>false</code>, do not set the 
		 * <code>bufferTime</code> property to <code>0</code>.</p>
		 * 
		 * @see #bufferTime  
         * @see FLVPlayback#isLive 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get isLive():Boolean {
			return _isLive;
		}

		/**
		 * A string that specifies the state of the component. This property is set by the 
		 * <code>load()</code>, <code>play()</code>, <code>stop()</code>, <code>pause()</code>, 
		 * and <code>seek()</code> methods. 
		 * 
		 * <p>The possible values for the state property are: <code>buffering</code>, 
		 * <code>connectionError</code>, <code>disconnected</code>, <code>loading</code>, 
		 * <code>paused</code>, <code>playing</code>, <code>rewinding</code>, <code>seeking</code>, 
		 * and <code>stopped</code>. You can use the FLVPlayback class properties to test for 
		 * these states. </p>
		 *
		 * @see VideoState#DISCONNECTED
		 * @see VideoState#STOPPED
		 * @see VideoState#PLAYING
		 * @see VideoState#PAUSED
		 * @see VideoState#BUFFERING
		 * @see VideoState#LOADING
		 * @see VideoState#CONNECTION_ERROR
		 * @see VideoState#REWINDING
         * @see VideoState#SEEKING
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get state():String {
			return _state;
		}

		/**
		 * A Boolean value that is <code>true</code> if the state is responsive. If the state is 
		 * unresponsive, calls to the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
		 * <code>pause()</code>, and <code>seek()</code>
		 * methods are queued and executed later, when the state changes to a 
		 * responsive one. Because these calls are queued and executed later, 
		 * it is usually not necessary to track the value of the <code>stateResponsive </code>
		 * property. The responsive states are: 
		 * <code>stopped</code>, <code>playing</code>, <code>paused</code>, and <code>buffering</code>. 
		 *
         * @see FLVPlayback#stateResponsive
		 * @see VideoState#DISCONNECTED
		 * @see VideoState#STOPPED
		 * @see VideoState#PLAYING
		 * @see VideoState#PAUSED
		 * @see VideoState#LOADING
		 * @see VideoState#RESIZING
		 * @see VideoState#CONNECTION_ERROR
         * @see VideoState#REWINDING
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get stateResponsive():Boolean {
			switch (_state) {
			case VideoState.STOPPED:
			case VideoState.PLAYING:
			case VideoState.PAUSED:
			case VideoState.BUFFERING:
				return true;
			default:
				return false;
			}
		}

		/**
		 * A number that indicates the extent of downloading, in number of bytes, for an 
		 * HTTP download.  Returns 0 when there
		 * is no stream, when the stream is from Flash Media Server (FMS), or if the information
		 * is not yet available. The returned value is useful only for an HTTP download.
		 *
		 * @tiptext Number of bytes already loaded
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get bytesLoaded():uint {
			if (_ns == null || _ncMgr.isRTMP) return uint.MIN_VALUE;
			return _ns.bytesLoaded;
		}

		/**
		 * A number that specifies the total number of bytes downloaded for an HTTP download.  
		 * Returns -1 when there is no stream, when the stream is from Flash Media Server (FMS), or if 
		 * the information is not yet available. The returned value is useful only 
		 * for an HTTP download. 
		 *
		 * @tiptext Number of bytes to be loaded
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get bytesTotal():uint {
			if (_ns == null || _ncMgr.isRTMP) return uint.MAX_VALUE;
			return _ns.bytesTotal;
		}

		/**
		 * A number that is the total playing time for the video in seconds.
		 *
		 * <p>When streaming from Flash Media Server (FMS) and using the default
		 * <code>NCManager</code>, this value is determined
		 * automatically by server-side application programming interfaces (APIs), and that value 
		 * overrides anything set through this property or gathered
		 * from metadata. The property is ready for reading when the
		 * <code>stopped</code> or <code>playing</code> state is reached after setting the
		 * <code>source</code> property. This property is meaningless for live streams
		 * from an FMS.</p>
		 *
		 * <p>With an HTTP download, the value is determined
		 * automatically if the FLV file has metadata embedded; otherwise,
		 * set it explicitly, or it will be NaN.  If you set it
		 * explicitly, the metadata value in the stream is
		 * ignored.</p>
		 *
		 * <p>When you set this property, the value takes effect for the next
		 * FLV file that is loaded by setting <code>source</code>. It has no effect
		 * on an FLV file that has already loaded.  Also, this property does not return 
		 * the new value passed in until an FLV file is loaded.</p>
		 *
		 * <p>Playback still works if this property is never set (either
		 * explicitly or automatically), but it can cause problems
		 * with seek controls.</p>
		 *
		 * <p>Unless set explicitly, the value will be NaN until it is set to a valid value from metadata.</p>
		 *
		 * @see #source
		 * @tiptext The total length of the FLV in seconds
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get totalTime():Number {
			return _streamLength;
		}

		/**
		 * A number that specifies the number of seconds to buffer in memory before 
		 * beginning to play a video stream. For FLV files streaming over RTMP, 
		 * which are not downloaded and buffer only in memory, it can be important 
		 * to increase this setting from the default value of 0.1. For a progressively 
		 * downloaded FLV file over HTTP, there is little benefit to increasing this 
		 * value although it could improve viewing a high-quality video on an older, 
		 * slower computer.
		 * 
		 * <p>For prerecorded (not live) video, do not set the <code>bufferTime</code> 
         * property to <code>0</code>; use the default buffer time or increase the buffer 
         * time.</p>
		 * 
		 * <p>This property does not specify the amount of the FLV file to download before 
		 * starting playback.</p>
		 * 
         * @see FLVPlayback#bufferTime
		 * @see #isLive
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get bufferTime():Number {
			if (_ns != null) _bufferTime = _ns.bufferTime;
			return _bufferTime;
		}
        /**
         * @private (setter)
         */
		public function set bufferTime(aTime:Number):void {
			_bufferTime = aTime;
			if (_ns != null) {
				_ns.bufferTime = _bufferTime;
			}
		}

		/**
		 * This property is the amount of seconds that the VideoPlayer will append
		 * to an ongoing live stream when it is being watched in DVR mode.  If the total
		 * duration of the DVR stream is not made available from the server, the 
		 * VideoPlayer will continually append this value in minutes when the current 
		 * recorded duration is within the dvrIncrementVariance of the perpetual total
		 * duration of the stream.
		 *  
		 * @see #isDVR
		 * @see #dvrIncrementVariance
		 * 
		 */
		public function get dvrIncrement():Number {
			return _dvrIncrement;
		}
        /**
         * @private (setter)
         */
		public function set dvrIncrement(increment:Number):void {
			if(increment > 60) {
				_dvrIncrement = increment;
				if(_dvrIncrementVariance > _dvrIncrement) {
					_dvrIncrementVariance = increment/2;
				}
			}
		}

		/**
		 * The difference threshold, in seconds, between the current duration
		 * and total duration of a DVR stream.  When this value is reached, it will 
		 * trigger a call to Flash Media Server to return the current and total
		 * duration of a stream.  If the total duration of the stream is unknown,
		 * the component will append the value of the dvrIncrement to the current
		 * duration and call FMS again when the difference threshold is reached again.
		 * 
		 * @see #isDVR
		 * @see #dvrIncrement 
		 * 
		 */			
		public function get dvrIncrementVariance():Number {
			return _dvrIncrementVariance;
		}
        /**
         * @private (setter)
         */
		public function set dvrIncrementVariance(variance:Number):void {
			if(variance > 0 && variance < _dvrIncrement-60) {
				_dvrIncrementVariance = variance;
			}
		}

		/**
		 * In the event a DVR stream may run beyond the total duration defined
		 * by Flash Media Server, enabling the dvrFixedDuration will force it to
		 * stop playing when the total duration is reached.  If disabled, the value
		 * of dvrIncrement will be appended to the end of the stream.  This property
		 * is ignored when the total duration is not given by Flash Media Server.
		 * 
		 * @see #isDVR
		 * @see #dvrIncrement
		 * @see #dvrIncrementVariance 
		 * 
		 */			
		public function get dvrFixedDuration():Boolean {
			return _dvrFixedDuration;
		}
        /**
         * @private (setter)
         */
		public function set dvrFixedDuration(fixed:Boolean):void {
			_dvrFixedDuration = fixed;
		}

		/**
		 * When a DVR stream is played it will by default begin playing at the beginning
		 * of the stream recording.  Enabling this will move the playhead of the stream
		 * to the latest live data produced by the server.
		 *  
		 * @see #isDVR 
		 * 
		 */			
		public function get dvrSnapToLive():Boolean {
			return _dvrSnapToLive;
		}
        /**
         * @private (setter)
         */
		public function set dvrSnapToLive(snapToLive:Boolean):void {
			_dvrSnapToLive = snapToLive;
		}

		/**
		 * The amount of time, in milliseconds, before Flash terminates an idle connection 
		 * to a video server, such as Flash Media Server, because playing paused or stopped. 
		 * This property has no effect on an 
		 * FLV file downloading over HTTP.
		 * 
		 * <p>If this property is set when a video stream is already idle, it restarts the 
		 * timeout period with the new value.</p>
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get idleTimeout():Number {
			return _idleTimeoutTimer.delay;
        }
        /**
         * @private (setter)
         */
		public function set idleTimeout(aTime:Number):void {
			_idleTimeoutTimer.delay = aTime;
		}

		/**
		 * A Boolean value that enables the DVR functionality when a live stream is
		 * being recorded by Flash Media Server.
		 *
         * @see VideoPlayer#isDVR
         *
         * @langversion 3.0
         * @playerversion Flash 10.0.0.0
		 */
		public function get isDVR():Boolean {
			return _isDVR;
		}
        /**
         * @private (setter)
         */		
		public function set isDVR(dvr:Boolean):void {
			if(dvr && _isLive) {
				_isLive = false;
			}
			_isDVR = dvr;
		}
		
			
		/**
		 * A number that is the amount of time, in milliseconds, between each 
		 * <code>playheadUpdate</code> event. Setting this property while the FLV file is 
		 * playing restarts the timer. 
		 * 
		 * <p>Because ActionScript cue points start on playhead updates, lowering 
		 * the value of the <code>playheadUpdateInterval</code> property can increase the accuracy 
		 * of ActionScript cue points.</p>
		 * 
		 * <p>Because the playhead update interval is set by a call to the global 
		 * <code>setInterval()</code> method, the update cannot fire more frequently than the 
		 * SWF file frame rate, as with any interval that is set this way. 
		 * So, as an example, for the default frame rate of 12 frames per second, 
		 * the lowest effective interval that you can create is approximately 
		 * 83 milliseconds, or one second (1000 milliseconds) divided by 12.</p>
		 *
         * @see FLVPlayback#playheadUpdateInterval
         * @default 250
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get playheadUpdateInterval():Number {
			return _updateTimeTimer.delay;
        }
        /**
         * @private (setter)
         */
		public function set playheadUpdateInterval(aTime:Number):void {
			_updateTimeTimer.delay = aTime;
		}

		/**
		 * A number that is the amount of time, in milliseconds, between each 
		 * <code>progress</code> event. If you set this property while the video 
		 * stream is playing, the timer restarts. 
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get progressInterval():Number {
			return _updateProgressTimer.delay;
        }
        /**
         * @private (setter)
         */
		public function set progressInterval(aTime:Number):void {
			_updateProgressTimer.delay = aTime;
		}

		/**
		 * An INCManager object that provides access to an instance of the class implementing 
		 * <code>INCManager</code>, which is an interface to the NCManager class.
		 * 
		 * <p>You can use this property to implement a custom INCManager that requires 
		 * custom initialization.</p>
		 *
         * @see FLVPlayback#ncMgr
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get ncMgr():INCManager {
			if (_ncMgr == null) createINCManager();
			return _ncMgr;
		}

		/**
		 * Allows direct access to the NetConnection instance created by the video player.  
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get netConnection():NetConnection {
			if (_ncMgr != null) return _ncMgr.netConnection;
			return null;
		}

		/**
		 * Allows direct access to the NetStream instance created by the video player.
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get netStream():NetStream {
			return _ns;
		}

		/**
         * * An object that is a metadata information packet that is received from a call to 
		 * the <code>NetSteam.onMetaData()</code> callback method, if available.  
		 * Ready when the <code>metadataReceived</code> event is dispatched.
		 * 
		 * <p>If the FLV file is encoded with the Flash 8 encoder, the <code>metadata</code> 
		 * property contains the following information. Older FLV files contain 
		 * only the <code>height</code>, <code>width</code>, and <code>duration</code> values.</p>
		 * 
		 * <table class="innertable" width="100%">
		 * 	<tr><th><b>Parameter</b></th><th><b>Description</b></th></tr>
		 * 		<tr><td><code>canSeekToEnd</code></td><td>A Boolean value that is <code>true</code> if the FLV file is encoded with a keyframe on the last frame that allows seeking to the end of a progressive download movie clip. It is <code>false</code> if the FLV file is not encoded with a keyframe on the last frame.</td></tr>
		 * 		<tr><td><code>cuePoints</code></td><td>An array of objects, one for each cue point embedded in the FLV file. Value is undefined if the FLV file does not contain any cue points. Each object has the following properties:
	     *   	
		 * 			<ul>
		 * 				<li><code>type</code>&#x2014;A string that specifies the type of cue point as either "navigation" or "event".</li>
		 * 				<li><code>name</code>&#x2014;A string that is the name of the cue point.</li>
		 * 				<li><code>time</code>&#x2014;A number that is the time of the cue point in seconds with a precision of three decimal places (milliseconds).</li>
		 * 				<li><code>parameters</code>&#x2014;An optional object that has name-value pairs that are designated by the user when creating the cue points.</li>
		 * 			</ul>
		 * 		</td></tr>
		 * <tr><td><code>audiocodecid</code></td><td>A number that indicates the audio codec (code/decode technique) that was used.</td></tr>
		 * <tr><td><code>audiodelay</code></td><td> A number that represents time <code>0</code> in the source file from which the FLV file was encoded. 
		 * <p>Video content is delayed for the short period of time that is required to synchronize the audio. For example, if the <code>audiodelay</code> value is <code>.038</code>, the video that started at time <code>0</code> in the source file starts at time <code>.038</code> in the FLV file.</p> 
		 * <p>Note that the FLVPlayback and VideoPlayer classes compensate for this delay in their time settings. This means that you can continue to use the time settings that you used in your the source file.</p>
</td></tr>
 		 * <tr><td><code>audiodatarate</code></td><td>A number that is the kilobytes per second of audio.</td></tr>
 		 * <tr><td><code>videocodecid</code></td><td>A number that is the codec version that was used to encode the video.</td></tr>
 		 * <tr><td><code>framerate</code></td><td>A number that is the frame rate of the FLV file.</td></tr>
 		 * <tr><td><code>videodatarate</code></td><td>A number that is the video data rate of the FLV file.</td></tr>
		 * <tr><td><code>height</code></td><td>A number that is the height of the FLV file.</td></tr>
 		 * <tr><td><code>width</code></td><td>A number that is the width of the FLV file.</td></tr>
 		 * <tr><td><code>duration</code></td><td>A number that specifies the duration of the FLV file in seconds.</td></tr>
		 * </table>
		 *
         * @see FLVPlayback#metadata
         *
		 *
		 * @see #load()
         * @see #play()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function get metadata():Object {
			return _metadata;
		}


		//
		// public callbacks, not really APIs
		//


		/**
		 * Called on interval determined by
         * <code>playheadUpdateInterval</code> to send <code>playheadUpdate</code>
		 * events.  Events only sent when playhead is moving, sent every
		 * 0.25 seconds by default.
		 *
		 * @private
		 */
		flvplayback_internal function doUpdateTime(e:TimerEvent=null):void {
			//ifdef DEBUG
			////debugTrace("doUpdateTime()");
			//endif
			var theTime:Number = playheadTime;

			if (theTime != _atEndCheckPlayhead) {
				_atEndCheckPlayhead = NaN;
			}

			// stop interval if we are stopped or paused
			switch (_state) {
			case VideoState.STOPPED:
			case VideoState.DISCONNECTED:
			case VideoState.CONNECTION_ERROR:
				_updateTimeTimer.stop();
				break;
			case VideoState.PAUSED:
				if(!_isDVR) {
					_updateTimeTimer.stop();
				}
				break;
			case VideoState.PLAYING:
			case VideoState.BUFFERING:
				// if all is downloaded and we are playing and we are stalled,
				// then we might have gotten stopped without getting a
				// NetStream.Play.Stop NetStatus event
				if (_ncMgr != null && !_ncMgr.isRTMP && _lastUpdateTime == theTime &&
				    _ns != null && _ns.bytesLoaded == _ns.bytesTotal ) {
					if (lastUpdateTimeStuckCount > lastUpdateTimeStuckCountMax) {
						lastUpdateTimeStuckCount = 0;
						httpDoStopAtEnd();
					} else {
						lastUpdateTimeStuckCount++;
					}
				}
			}
			
			if(_isDVR && _dvrMgr != null && _dvrPlaying){
				
				if((_streamLength - _dvrMgr.currentDuration < _dvrIncrementVariance) && _dvrMgr.isRec && !_dvrMgr.offline && _dvrMgr.checkInterval(_dvrIncrementVariance)){
					_dvrMgr.getStreamDuration(_ncMgr.streamName);
				}
				
				if(_ns.time > _dvrMgr.totalDuration && _dvrMgr.totalDuration != 0) {
					if(_dvrFixedDuration == true){
						_ns.play(false);
					}
				}
				
				if(_dvrMgr.currentDuration >= 0) {
					var _currDuration:Number = (_dvrMgr.currentDuration <= _streamLength) ? _dvrMgr.currentDuration : _streamLength;
					dispatchEvent(new VideoProgressEvent(VideoProgressEvent.PROGRESS, false, false, _currDuration, _streamLength));
				}
			}

			if (_lastUpdateTime != theTime) {
				dispatchEvent(new VideoEvent(VideoEvent.PLAYHEAD_UPDATE, false, false, _state, theTime));
				_lastUpdateTime = theTime;
				lastUpdateTimeStuckCount = 0;
			}
		}

		/**
		 * Called at interval determined by
         * <code>progressInterval</code> to send <code>progress</code> events.
		 * Object dispatch starts when <code>_load</code> is called, ends
		 * when all bytes downloaded or a network error of some kind
		 * occurs.
         *
         * @default 0.25
		 *
		 * @private
		 */
		flvplayback_internal function doUpdateProgress(e:TimerEvent):void {
			if (_ns == null) return;
			//ifdef DEBUG
			////debugTrace("doUpdateProgress()");
			////debugTrace("_ns.bytesLoaded = " + _ns.bytesLoaded);
			////debugTrace("_ns.bytesTotal = " + _ns.bytesTotal);
			//endif

			var curBytesLoaded:uint = _ns.bytesLoaded;
			var curBytesTotal:uint = _ns.bytesTotal;

			if (curBytesTotal < uint.MAX_VALUE) {
				dispatchEvent(new VideoProgressEvent(VideoProgressEvent.PROGRESS, false, false, curBytesLoaded, curBytesTotal))
			}
			if ( _state == VideoState.DISCONNECTED || _state == VideoState.CONNECTION_ERROR ||
				 curBytesLoaded >= curBytesTotal ) {
				_updateProgressTimer.stop();
			}

			checkEnoughDownloaded(curBytesLoaded, curBytesTotal);
		}

		/**
		 * @private
		 */
		flvplayback_internal function updateDVRDuration(e:Event):void
		{

			var currentDuration:int = e.target.currentDuration;
			if(!isNaN(e.target.totalDuration)){
				_streamLength = e.target.totalDuration;
			}

				if(_dvrMgr.totalDuration > 0) {
					if(!_dvrFixedDuration && _dvrMgr.isRec && (_dvrMgr.totalDuration - _dvrMgr.currentDuration < _dvrIncrementVariance)) {
					 	_streamLength = _dvrMgr.currentDuration + _dvrIncrement;
					} else {
						_streamLength = _dvrMgr.totalDuration;
					}
				} else {
					_streamLength = _dvrMgr.currentDuration + _dvrIncrement;
				}
						
			if(_dvrStart && _dvrMgr.offline != true) {
				_dvrStart = false;
				_createStream();
				_setUpStream();
			} else if(_dvrStart && _dvrMgr.offline == true) {
				return;
			}
			
			if(currentDuration > _streamLength && _dvrFixedDuration && _dvrMgr.totalDuration > 0) {
				currentDuration = _streamLength;
			}
			
			dispatchEvent(new VideoProgressEvent(VideoProgressEvent.PROGRESS, false, false, currentDuration, _streamLength));
		}

		flvplayback_internal function handleDVRError(e:Event):void
		{
			// dvr stream is not available
			/*
			this._isLive = true;
			if(_dvrStart) {
				_dvrStart = false;
				_isDVR = false;
				_createStream();
				_setUpStream();
			}
			*/
		}
		
		/**
		 * @private
		 */
		flvplayback_internal function checkEnoughDownloaded(curBytesLoaded:uint, curBytesTotal:uint):void {
			//ifdef DEBUG
			//debugTrace("checkEnoughDownloaded(" + curBytesLoaded + ", " + curBytesTotal + ")");
			//endif
			// wait until we have some bytes loaded and total data to start our checks
			if (curBytesLoaded == 0 || curBytesTotal == uint.MAX_VALUE) {
				return;
			}

			// if we have no totalTime set from metadata or by other method,
			// then we can't do any of our calculations.  Before starting play,
			// we check for stateResponsive to make sure we are not still waiting for metadata
			if (isNaN(totalTime) || totalTime <= 0) {
				if (waitingForEnough && stateResponsive) {
					waitingForEnough = false;
					_cachedState = _state;
					_state = VideoState.EXEC_QUEUED_CMD;
					play();
					execQueuedCmds();
				}
				return;
			}

			// if it is all loaded, we need look no further!
			if (curBytesLoaded >= curBytesTotal) {
				// if we are waiting, then we should play
				if (waitingForEnough) {
					waitingForEnough = false;
					_cachedState = _state;
					_state = VideoState.EXEC_QUEUED_CMD;
					play();
					execQueuedCmds();
				}
				return;
			}

			// if first time handling a progress event for this vp, we stash
			// the baseline progress time.  This is because it is possible
			// that the user started downloading this FLV earlier so has a
			// portion of it cached, but not the entire thing.  If we do not
			// note this, it could skew our download rate calculations badly
			if (isNaN(baselineProgressTime)) {
				baselineProgressTime = (curBytesLoaded / curBytesTotal) * totalTime;
			}
			if (isNaN(startProgressTime)) {
				// first time we hit a progress event with more than 0 bytes
				// loaded but less than bytesTotal bytes lodaed, we just grab
				// the start time in seconds to use in our download rate
				// calculations
				startProgressTime = getTimer();
			} else {
				// if we have a start time grabbed...
				// calculate total time spent downloading, in seconds
				totalDownloadTime = (getTimer() - startProgressTime) / 1000;

				// calculate total playing time of FLV downloaded, in seconds.
				// This approximate value assumes that the bytes / second of
				// playing time in the FLV is linear, which is not an accurate
				// assumption but it will usually be good enough when averaged
				// over long spans of the FLV.
				totalProgressTime = (curBytesLoaded / curBytesTotal) * totalTime;

				// check if it we are ready to start playing
				if (waitingForEnough) {
					checkReadyForPlay(curBytesLoaded, curBytesTotal);
				}
			}
		}

		/**
		 * @private
		 */
		flvplayback_internal function checkReadyForPlay(curBytesLoaded:uint, curBytesTotal:uint):void {
			//ifdef DEBUG
			//debugTrace("checkReadyForPlay()");
			//endif
			// play if downloaded all the way
			if (curBytesLoaded >= curBytesTotal) {
				waitingForEnough = false;
				_cachedState = _state;
				_state = VideoState.EXEC_QUEUED_CMD;
				play();
				execQueuedCmds();
				return;
			}

			// if no baseline progress time set we are definitely not ready to
			// play because have not received any progress events yet
			if (isNaN(baselineProgressTime)) return;

			// if totalTime is undefined or not set to a valid value, then
			// none of our calculations are valid.  The progress event
			// listener will take care of just starting an FLV if we do not
			// know the totalTime
			if (isNaN(totalTime) || totalTime < 0) {
				waitingForEnough = false;
				_cachedState = _state;
				_state = VideoState.EXEC_QUEUED_CMD;
				play();
				execQueuedCmds();
			} else {
				// we don't want to make download estimate rates to early because
				// our calculations are very approximate, so wait until at least
				// 1.5 seconds of downloading has occurred (not so long of a wait)
				// remember that if the WHOLE FLV downloads in less than 1.5
				// seconds, that will still trigger a start outside of this function,
				// since we don't call this function when bytesLoaded == bytesTotal
				if (totalDownloadTime > 1.5) {
					// download rate calculation as (approximate FLV playing time) / (time spent downloading FLV)
					var downloadRate:Number = (totalProgressTime - baselineProgressTime) / totalDownloadTime;

					// if we estimate that we will be done downloading the FLV by the time
					// we get to the end of it
					if (totalTime - playheadTime > ((totalTime - totalProgressTime) / downloadRate)) {
						waitingForEnough = false;
						_cachedState = _state;
						_state = VideoState.EXEC_QUEUED_CMD;
						play();
						execQueuedCmds();
					}
				} // if (totalDownloadTime > 1.5)

			} // if (!isNaN(totalTime) && totalTime > 0)
		}

		/**
		 * <code>NetStatusEvent.NET_STATUS</code> event listener
		 * for rtmp.  Handles automatic resizing, autorewind and
		 * buffering messaging.
		 *
		 * @private
		 */
		flvplayback_internal function rtmpNetStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("rtmpNetStatus:"+e.info.code);
			//debugTrace("_state == " + _state);
			//debugTrace("_cachedState == " + _cachedState);
			//debugTrace("_bufferState == " + _bufferState);
			//debugTrace("_sawPlayStop == " + _sawPlayStop);
			//debugTrace("_cachedPlayheadTime == " + _cachedPlayheadTime);
			//debugTrace("playheadTime == " + playheadTime);
			//debugTrace("_ns.bufferLength = " + _ns.bufferLength);
			//debugTrace("_startingPlay = " + _startingPlay);
			//endif

			if (_state == VideoState.CONNECTION_ERROR) {
				// always do nothing
				return;
			}

			switch (e.info.code) {
			case "NetStream.Play.Stop":
				if (_startingPlay) return;
				
				if(_isDVR && _dvrMgr != null && _dvrMgr.checkInterval(1)){
					_dvrPlaying = false;
					if(_ncMgr.streamName == "" && _ncMgr.streams.length > 0) {
						_dvrMgr.getStreamDuration(_ncMgr.streams[0].src);
					} else {
						_dvrMgr.getStreamDuration(_ncMgr.streamName);
					}					
				}
				
				switch (_state) {
				case VideoState.RESIZING:
					if (_hiddenForResize) finishAutoResize();
					break;
				case VideoState.LOADING:
				case VideoState.STOPPED:
				case VideoState.PAUSED:
					// yes we are stopped, we already know this
					break;
				default:
					_sawPlayStop = true;
					// this code used to only be in the flush handler, checking for _sawPlayStop,
					// but there can be timing issues where the flush comes just before the stop, instead
					// of just after
					if ( !_rtmpDoStopAtEndTimer.running &&
						 ( _bufferState == BUFFER_FLUSH || (_ns.bufferTime <= 0.1 && _ns.bufferLength <= 0.1) ) ) {
						// if we did a seek toward the end of the file so that
						// there is less file left to show than we have
						// buffer, than we will get a NetStream.Play.Stop when
						// the buffer loads rest of the file, but never get
						// a NetStream.Buffer.Full, since it won't fill, so
						// we check if we are done on a timer
						_cachedPlayheadTime = playheadTime;
						_rtmpDoStopAtEndTimer.reset();
						_rtmpDoStopAtEndTimer.start();
					}
					break;
				} // switch (_state)
				break;
			case "NetStream.Buffer.Empty":
				switch (_bufferState) {
				case BUFFER_FULL:
					if (_sawPlayStop) {
						rtmpDoStopAtEnd();
					} else if (_state == VideoState.PLAYING) {
						setState(VideoState.BUFFERING);
					}
					break;
				}
				_bufferState = BUFFER_EMPTY;
				_sawPlayStop = false;
				break;
			case "NetStream.Buffer.Flush":
				if (_sawSeekNotify && _state == VideoState.SEEKING) {
					_bufferState = BUFFER_EMPTY;
					_sawPlayStop = false;
					setStateFromCachedState(false);
					doUpdateTime();
					execQueuedCmds();
				}
				if ( !_rtmpDoStopAtEndTimer.running && _sawPlayStop &&
					 ( _bufferState == BUFFER_EMPTY || (_ns.bufferTime <= 0.1 && _ns.bufferLength <= 0.1) ) ) {
					// if we did a seek toward the end of the file so that
					// there is less file left to show than we have
					// buffer, than we will get a NetStream.Play.Stop when
					// the buffer loads rest of the file, but never get
					// a NetStream.Buffer.Full, since it won't fill, so
					// we check if we are done on a timer
					_cachedPlayheadTime = playheadTime;
					_rtmpDoStopAtEndTimer.reset();
					_rtmpDoStopAtEndTimer.start();
				}
				switch (_bufferState) {
				case BUFFER_EMPTY:
					if ( !_hiddenForResize ) {
						if ((_state == VideoState.LOADING && _cachedState == VideoState.PLAYING) || _state == VideoState.BUFFERING) {
							setState(VideoState.PLAYING);
						} else if (_cachedState == VideoState.BUFFERING) {
							_cachedState = VideoState.PLAYING;
						}
					}
					_bufferState = BUFFER_FLUSH;
					break;
				default:
					if (_state == VideoState.BUFFERING) {
						setStateFromCachedState();
					}
					break;
				} // switch (_bufferState)
				break;
			case "NetStream.Buffer.Full":
				if (_sawSeekNotify && _state == VideoState.SEEKING) {
					_bufferState = BUFFER_EMPTY;
					_sawPlayStop = false;
					setStateFromCachedState(false);
					doUpdateTime();
					execQueuedCmds();
				}
				switch (_bufferState) {
				case BUFFER_EMPTY:
					_bufferState = BUFFER_FULL;
					if ( !_hiddenForResize ) {
						if ((_state == VideoState.LOADING && _cachedState == VideoState.PLAYING) || _state == VideoState.BUFFERING) {
							setState(VideoState.PLAYING);
						} else if (_cachedState == VideoState.BUFFERING) {
							_cachedState = VideoState.PLAYING;
						}
						if (_rtmpDoStopAtEndTimer.running) {
							_sawPlayStop = true;
							_rtmpDoStopAtEndTimer.reset();
						}
					}
					break;
				case BUFFER_FLUSH:
					_bufferState = BUFFER_FULL;
					if ( _rtmpDoStopAtEndTimer.running) {
						_sawPlayStop = true;
						_rtmpDoStopAtEndTimer.reset();
					}
					break;
				} // switch (_bufferState)
				if (_state == VideoState.BUFFERING) {
					setStateFromCachedState();
				}
				break;
			case "NetStream.Pause.Notify":
				if (_state == VideoState.RESIZING && _hiddenForResize) {
					finishAutoResize();
				}
				break;
			case "NetStream.Unpause.Notify":
				if (_state == VideoState.PAUSED) {
					_state = VideoState.PLAYING;
					setState(VideoState.BUFFERING);
				} else {
					_cachedState = VideoState.PLAYING;
				}
				break;
			case "NetStream.Play.Start":
				_rtmpDoStopAtEndTimer.reset();
				_bufferState = BUFFER_EMPTY;
				_sawPlayStop = false;
				
				if (_isDVR && _dvrMgr != null) {
					_dvrPlaying = true;
				}
				if (_startingPlay && _dvrMgr != null) {
					if(_dvrFixedDuration && _dvrMgr.totalDuration > 0 && _dvrMgr.currentDuration >= _dvrMgr.totalDuration) {
						_dvrSnapToLive = false;
					}
					
					if(_dvrSnapToLive && _isDVR && _dvrMgr.isRec) {
						_seek(_dvrMgr.currentDuration);
					}
					_startingPlay = false;
					_cachedPlayheadTime = playheadTime;
				} else if (_startingPlay) {
					_startingPlay = false;
					_cachedPlayheadTime = playheadTime;
				} else if (_state == VideoState.PLAYING) {
					setState(VideoState.BUFFERING);
				}
				break;
			case "NetStream.Play.Reset":
				_rtmpDoStopAtEndTimer.reset();
				if (_state == VideoState.REWINDING) {
					_rtmpDoSeekTimer.reset();
					if (playheadTime == 0 || playheadTime < _cachedPlayheadTime) {
						setStateFromCachedState();
					} else {
						_cachedPlayheadTime = playheadTime;
						_rtmpDoSeekTimer.start();
					}
				}
				break;
			case "NetStream.Seek.Notify":
				if (playheadTime != _cachedPlayheadTime) {
					setStateFromCachedState(false);
					doUpdateTime();
					execQueuedCmds();
				} else {
					_sawSeekNotify = true;
					_rtmpDoSeekTimer.start();
				}
				break;
			case "Netstream.Play.UnpublishNotify":
				break;
			case "Netstream.Play.PublishNotify":
				break;
			case "NetStream.Play.StreamNotFound":
				closeNS(false);
				if (!_ncMgr.connectAgain()) {
					setState(VideoState.CONNECTION_ERROR);
				}
				break;
			case "NetStream.Play.Failed":	
			case "NetStream.Failed":
			case "NetStream.Play.FileStructureInvalid":
			case "NetStream.Play.NoSupportedTrackFound":
				setState(VideoState.CONNECTION_ERROR);
				break;
			} // switch (e.info.code)
		}

		/**
		 * <code>NetStatusEvent.NET_STATUS</code> event listener
		 * for http.  Handles autorewind.
		 *
		 * @private
		 */
		flvplayback_internal function httpNetStatus(e:NetStatusEvent):void {
			//ifdef DEBUG
			//debugTrace("httpNetStatus:"+e.info.code);
			//debugTrace("_state == " + _state);
			//debugTrace("playheadTime == " + playheadTime);
			//debugTrace("_bufferState = " + _bufferState);
			//endif

			switch (e.info.code) {
			case "NetStream.Play.Stop":
				_delayedBufferingTimer.reset();
				if (_invalidSeekTime) {
					_invalidSeekTime = false;
					_invalidSeekRecovery = true;
					setState(_cachedState);
					seek(playheadTime);
				} else {
					switch (_state) {
					case VideoState.SEEKING:
						httpDoSeek(null);
						// no break
					case VideoState.PLAYING:
					case VideoState.BUFFERING:
						httpDoStopAtEnd();
						break;
					}
				}
				break;
			case "NetStream.Seek.InvalidTime":
				if (_invalidSeekRecovery) {
					_invalidSeekTime = false;
					_invalidSeekRecovery = false;
					setState(_cachedState);
					seek(0);
				} else {
					_invalidSeekTime = true;
					_httpDoSeekCount = 0;
					_httpDoSeekTimer.start();
				}
				break;
			case "NetStream.Buffer.Empty":
				_bufferState = BUFFER_EMPTY;
				if (_state == VideoState.PLAYING) {
					_delayedBufferingTimer.reset();
					_delayedBufferingTimer.start();
				}
				break;
			case "NetStream.Buffer.Full":
			case "NetStream.Buffer.Flush":
				_delayedBufferingTimer.reset();
				_bufferState = BUFFER_FULL;
				if ( !_hiddenForResize ) {
					if ((_state == VideoState.LOADING && _cachedState == VideoState.PLAYING) || _state == VideoState.BUFFERING) {
						setState(VideoState.PLAYING);
					} else if (_cachedState == VideoState.BUFFERING) {
						_cachedState = VideoState.PLAYING;
					}
				}
				break;
			case "NetStream.Seek.Notify":
				_invalidSeekRecovery = false;
				switch (_state) {
				case VideoState.SEEKING:
				case VideoState.REWINDING:
					_httpDoSeekCount = 0;
					_httpDoSeekTimer.start();
					break;
				} // switch (_state)
				break;
			case "NetStream.Play.StreamNotFound":
			case "NetStream.Play.FileStructureInvalid":
			case "NetStream.Play.NoSupportedTrackFound":
				setState(VideoState.CONNECTION_ERROR);
				break;
			} // switch (e.info.code)
		}

		/**
		 * Called by INCManager after the connection is complete or failed after a call to the
		 * <code>INCManager.connectToURL()</code> method. If the connection failed, set 
		 * the <code>INCManager.netConnection</code> property to <code>null</code> or
		 * undefined before calling.
		 *
		 * @see #ncReconnected()
		 * @see INCManager#connectToURL()
         * @see NCManager#connectToURL()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function ncConnected():void	{
			//ifdef DEBUG
			//debugTrace("ncConnected()");
			//endif

			if (_ncMgr == null || _ncMgr.netConnection == null) {
				setState(VideoState.CONNECTION_ERROR);
				var fpcaps:FPMediaCapabilities = new FPMediaCapabilities();
				if( fpcaps.rtmpe == false ) {
					dispatchEvent(new VideoEvent(VideoEvent.UNSUPPORTED_PLAYER_VERSION, false, false, VideoState.CONNECTION_ERROR, playheadTime));
				}
			} else if (_ns == null) {
				
				if (_isDVR && _dvrMgr == null) {
					_dvrStart = true;
					_dvrMgr = new DVRManager(_ncMgr.netConnection);
					_dvrMgr.addEventListener(DVRManager.DVR_EVENT, updateDVRDuration);
					_dvrMgr.addEventListener(DVRManager.DVR_ERROR, handleDVRError);
					if(_ncMgr.streams != null) {
						if(_ncMgr.streams.length > 0) {
							if(_ncMgr.streams.length > 1) {
								for(var i:int=1; i<_ncMgr.streams.length; i++) {
									try {
										_ncMgr.netConnection.call("DVRSubscribe", null, _ncMgr.streams[i].src);
									} catch(e:Error) {}
								}
							}
							_dvrMgr.getStreamDuration(_ncMgr.streams[0].src);
						}
					} else {
						_dvrMgr.getStreamDuration(_ncMgr.streamName);
					}
					
					return;
				} else {
					_createStream();
					_setUpStream();
				}
			}
		}

		/**
		 * Called by INCManager after the reconnection is complete or has failed after a call to
		 * the <code>INCManager.reconnect()</code> method. If the connection fails, 
		 * set the <code>INCManager.netconnection</code> property to <code>null</code>
		 * before you call it.
		 *
		 * @see #ncConnected()
		 * @see INCManager#reconnect()
         * @see NCManager#reconnect()
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */
		public function ncReconnected():void {
			//ifdef DEBUG
			//debugTrace("reconnected called!");
			//endif
			if (_ncMgr == null || _ncMgr.netConnection == null) {
				setState(VideoState.CONNECTION_ERROR);
			} else {
				_ns = null;
				_state = VideoState.STOPPED;
				execQueuedCmds();
			}
		}


		/**
		 * handles NetStream.onMetaData callback
		 *
		 * @private
		 */
		flvplayback_internal function onMetaData(info:Object):void {
			if (_metadata != null) return;
			_metadata = info;
			if (isNaN(_streamLength)){
				_streamLength = info["duration"];
			} 
			if (_resizeImmediatelyOnMetadata && _ns.client.ready) {
				_resizeImmediatelyOnMetadata = false;
				_autoResizeTimer.reset();
				_autoResizeDone = false;
				doAutoResize();
			}
			dispatchEvent(new MetadataEvent(MetadataEvent.METADATA_RECEIVED, false, false, info));
		}

		/**
		 * handles NetStream.onCuePoint callback
		 *
		 * @private
		 */
		flvplayback_internal function onCuePoint(info:Object):void {
			if ( !_hiddenForResize ||
			     (!isNaN(_hiddenRewindPlayheadTime) && playheadTime < _hiddenRewindPlayheadTime) ) {
				dispatchEvent(new MetadataEvent(MetadataEvent.CUE_POINT, false, false, info));
			}
		}


		//
		// private functions
		//


		/**
		 * sets state, dispatches event, execs queued commands.  Always try to call
		 * this AFTER you do your work, because the state might change again after
		 * you call this if you set it to a responsive state becasue of the call
		 * to exec queued commands.  If you set this to a responsive state and
		 * then do more state based logic, check _state to make sure it did not
		 * change out from under you.
		 * 
		 * @private
		 */
		flvplayback_internal function setState(s:String, execQueued:Boolean=true):void {
			if (s == _state) return;
			_hiddenRewindPlayheadTime = NaN;
			_cachedState = _state;
			_cachedPlayheadTime = playheadTime;
			_state = s;
			var newState:String = _state;
			//ifdef DEBUG
			//debugTrace("state = " + newState);
			//debugTrace("_cachedState == " + _cachedState);
			//debugTrace("_cachedPlayheadTime == " + _cachedPlayheadTime);
			//endif
			dispatchEvent(new VideoEvent(VideoEvent.STATE_CHANGE, false, false, newState, playheadTime));
			if (!_readyDispatched) {
				switch (newState) {
				case VideoState.STOPPED:
				case VideoState.PLAYING:
				case VideoState.PAUSED:
				case VideoState.BUFFERING:
					_readyDispatched = true;
					dispatchEvent(new VideoEvent(VideoEvent.READY, false, false, newState, playheadTime));
					break;
				} // switch
			}
			switch (_cachedState) {
			case VideoState.REWINDING:
				dispatchEvent(new VideoEvent(VideoEvent.AUTO_REWOUND, false, false, newState, playheadTime));
				if (_ncMgr.isRTMP && newState == VideoState.STOPPED) {
					closeNS();
				}
				break;
			} // switch
			switch (newState) {
			case VideoState.STOPPED:
			case VideoState.PAUSED:
				if (_ncMgr.isRTMP) {
					_idleTimeoutTimer.reset();
					_idleTimeoutTimer.start();
				}
				break;
			case VideoState.SEEKING:
			case VideoState.REWINDING:
				_bufferState = BUFFER_EMPTY;
				_sawPlayStop = false;
				_idleTimeoutTimer.reset();
				break;
			case VideoState.PLAYING:
			case VideoState.BUFFERING:
				_updateTimeTimer.start();
				_idleTimeoutTimer.reset();
				break;
			case VideoState.LOADING:
			case VideoState.RESIZING:
				_idleTimeoutTimer.reset();
				break;
			} // switch
			if (execQueued) {
				execQueuedCmds();
			}
		}

		/**
		 * Sets state to _cachedState if the _cachedState is VideoState.PLAYING,
		 * VideoState.PAUSED or VideoState.BUFFERING, otherwise sets state to VideoState.STOPPED.
		 *
		 * @private
		 */
		flvplayback_internal function setStateFromCachedState(execQueued:Boolean=true):void {
			switch (_cachedState) {
			case VideoState.PLAYING:
			case VideoState.PAUSED:
			case VideoState.BUFFERING:
				setState(_cachedState, execQueued);
				break;
			default:
				setState(VideoState.STOPPED, execQueued);
				break;
			}
		}

		/**
		 * creates our implementatino of the <code>INCManager</code>.
		 * We put this off until we need to do it to give time for the
		 * user to customize the <code>iNCManagerClass</code>
		 * static variable.
		 *
		 * @private
		 */
		flvplayback_internal function createINCManager():void {
			var theClass:Class = null;
			try {
				if (iNCManagerClass is String) {
					theClass = Class(getDefinitionByName(String(iNCManagerClass)));
				} else if (iNCManagerClass is Class) {
					theClass = Class(iNCManagerClass);
				}
			} catch (e:Error) {
				theClass = null;
			}
			if (theClass == null) {
				throw new VideoError(VideoError.INCMANAGER_CLASS_UNSET, (iNCManagerClass == null) ? "null" : iNCManagerClass.toString());
			}
			_ncMgr = new theClass();
			_ncMgr.videoPlayer = this;
		}

		/**
		 * creates an instance of the class specified by the
		 * <code>netStreamClientClass</code> static property.
		 *
		 * @private
		 */
		flvplayback_internal function createNetStreamClient():Object {
			var theClass:Class = null;
			var theInst:Object = null;
			try {
				if (netStreamClientClass is String) {
					theClass = Class(getDefinitionByName(String(netStreamClientClass)));
				} else if (netStreamClientClass is Class) {
					theClass = Class(netStreamClientClass);
				}
				if (theClass != null) {
					theInst = new theClass(this);
				}
			} catch (e:Error) {
				theClass = null;
				theInst = null;
			}
			if (theInst == null) {
				throw new VideoError(VideoError.NETSTREAM_CLIENT_CLASS_UNSET, (netStreamClientClass == null) ? "null" : netStreamClientClass.toString());
			}
			return theInst;
		}

		/**
		 * <p>ONLY CALL THIS WITH RTMP STREAMING</p>
		 *
		 * <p>Has the logic for what to do when we decide we have come to
		 * a stop by coming to the end of an rtmp stream.  There are a few
		 * different ways we decide this has happened, and we sometimes
		 * even set an interval that calls this function repeatedly to
		 * check if the time is still changing, which is why it has its
		 * own special function.</p>
		 *
		 * @private
		 */
		flvplayback_internal function rtmpDoStopAtEnd(e:TimerEvent=null):void {
			//ifdef DEBUG
			//debugTrace("rtmpDoStopAtEnd(" + e + ")");
			//endif
			// check if we really want to stop if this was triggered on an
			// interval.  If we are running this on an interval (see
			// rtmpNetStatus) we do a stop when the playhead hasn't moved
			// since last time we checked, we check every 0.25 seconds.
			if (_rtmpDoStopAtEndTimer.running) {
				switch (_state) {
				case VideoState.DISCONNECTED:
				case VideoState.CONNECTION_ERROR:
					_rtmpDoStopAtEndTimer.reset();
					return;
				}
				if (e == null || _cachedPlayheadTime == playheadTime) {
					_rtmpDoStopAtEndTimer.reset();
				} else {
					_cachedPlayheadTime = playheadTime;
					return;
				}
			}
			// special check to see if we started at end, so our _atEnd variable was WRONG
			if (_atEndCheckPlayhead == playheadTime && _atEndCheckPlayhead != _lastSeekTime && !_isLive && playheadTime != 0) {
				_atEnd = false;
				_currentPos = 0;
				_play(0);
				return;
			}
			_atEndCheckPlayhead = NaN;
			_bufferState = BUFFER_EMPTY;
			_sawPlayStop = false;
			_atEnd = true;
			// all this triggers callbacks, so need to keep checking if
			// _state == STOPPED--if no longer, then we bail
			setState(VideoState.STOPPED);
			if (_state != VideoState.STOPPED) return;
			doUpdateTime();
			if (_state != VideoState.STOPPED) return;
			dispatchEvent(new VideoEvent(VideoEvent.COMPLETE, false, false, _state, playheadTime));
			if (_state != VideoState.STOPPED) return;
			if (_autoRewind && !_isLive && playheadTime != 0) {
				_atEnd = false;
				_currentPos = 0;
				_play(0, 0);
				setState(VideoState.REWINDING);
			} else {
				closeNS();
			}
		}

		/**
		 * <p>ONLY CALL THIS WITH RTMP STREAMING</p>
		 *
		 * <p>Wait until time goes back to zero to leave rewinding state.</p>
		 *
		 * @private
		 */
		flvplayback_internal function rtmpDoSeek(e:TimerEvent):void {
			//ifdef DEBUG
			//debugTrace("rtmpDoSeek()");
			//endif
			if (_state != VideoState.REWINDING && _state != VideoState.SEEKING) {
				_rtmpDoSeekTimer.reset();
				_sawSeekNotify = false;
			} else if (playheadTime != _cachedPlayheadTime) {
				_rtmpDoSeekTimer.reset();
				_sawSeekNotify = false;
				setStateFromCachedState(false);
				doUpdateTime();
				_lastSeekTime = playheadTime;
				execQueuedCmds();
			}
		}

		/**
		 * <p>ONLY CALL THIS WITH HTTP PROGRESSIVE DOWNLOAD</p>
		 *
		 * <p>Call this when playing stops by hitting the end.</p>
		 *
		 * @private
		 */
		flvplayback_internal function httpDoStopAtEnd():void {
			//ifdef DEBUG
			//debugTrace("httpDoStopAtEnd()");
			//endif
			if (_atEndCheckPlayhead == playheadTime && _atEndCheckPlayhead != _lastUpdateTime && playheadTime != 0) {
				_atEnd = false;
				_seek(0);
				return;
			}
			_atEndCheckPlayhead = NaN;
			_atEnd = true;
			if (isNaN(_streamLength)) {
				_streamLength = _ns.time;
			}
			_pause(true);
			setState(VideoState.STOPPED);
			if (_state != VideoState.STOPPED) return;
			doUpdateTime();
			if (_state != VideoState.STOPPED) return;
			dispatchEvent(new VideoEvent(VideoEvent.COMPLETE, false, false, _state, playheadTime));
			if (_state != VideoState.STOPPED) return;
			if (_autoRewind) {
				_atEnd = false;
				_pause(true);
				_seek(0);
				setState(VideoState.REWINDING);
			}
		}

		/**
		 * <p>ONLY CALL THIS WITH HTTP PROGRESSIVE DOWNLOAD</p>
		 *
		 * <p>If we get an onStatus callback indicating a seek is over,
		 * but the playheadTime has not updated yet, then we wait on a
		 * timer before moving forward.</p>
		 *
		 * @private
		 */
		flvplayback_internal function httpDoSeek(e:TimerEvent):void {
			//ifdef DEBUG
			//debugTrace("httpDoSeek()");
			//debugTrace("playheadTime = " + playheadTime);
			//debugTrace("_cachedPlayheadTime = " + _cachedPlayheadTime);
			//endif
			var seekState:Boolean = (_state == VideoState.REWINDING || _state == VideoState.SEEKING);
			// if seeking or rewinding, then need to wait for playhead time to
			// change or for timeout
			if ( seekState && _httpDoSeekCount < httpDoSeekMaxCount &&
				 (_cachedPlayheadTime == playheadTime || _invalidSeekTime) ) {
				_httpDoSeekCount++;
				return;
			}

			// reset
			_httpDoSeekCount = 0;
			_httpDoSeekTimer.reset();

			// only do the rest if were seeking or rewinding to start with
			if (!seekState) return;

			setStateFromCachedState(false);
			if (_invalidSeekTime) {
				_invalidSeekTime = false;
				_invalidSeekRecovery = true;
				seek(playheadTime);
			} else {
				doUpdateTime();
				_lastSeekTime = playheadTime;
				execQueuedCmds();
			}
		}

		/**
		 * <p>Wrapper for <code>NetStream.close()</code>.  Never call
		 * <code>NetStream.close()</code> directly, always call this
		 * method because it does some other housekeeping.</p>
		 *
		 * @private
		 */
		flvplayback_internal function closeNS(updateCurrentPos:Boolean=false):void {
			//ifdef DEBUG
			//debugTrace("closeNS()");
			//endif
			if (_ns != null) {
				// do one last time update if updateCurrentPos is true
				if (updateCurrentPos) {
					doUpdateTime();
					_currentPos = _ns.time;
				}

				// shut down all the timers
				_updateTimeTimer.reset();
				_updateProgressTimer.reset();
				_idleTimeoutTimer.reset();
				_autoResizeTimer.reset();
				_rtmpDoStopAtEndTimer.reset();
				_rtmpDoSeekTimer.reset();
				_httpDoSeekTimer.reset();
				_finishAutoResizeTimer.reset();
				_delayedBufferingTimer.reset();

				// remove listeners from NetStream
				_ns.removeEventListener(NetStatusEvent.NET_STATUS, rtmpNetStatus);
				_ns.removeEventListener(NetStatusEvent.NET_STATUS, httpNetStatus);

				// close and delete NetStream
				_ns.close();
				_ns = null;
			}
		}

		/**
		 * <p>We do a brief timer before entering VideoState.BUFFERING state to avoid
		 * quick switches from VideoState.BUFFERING to VideoState.PLAYING and back.</p>
		 *
		 * @private
		 */
		flvplayback_internal function doDelayedBuffering(e:TimerEvent):void {
			//ifdef DEBUG
			//debugTrace("doDelayedBuffering()");
			//endif
			switch (_state) {
			case VideoState.LOADING:
			case VideoState.RESIZING:
				// if loading or resizing, still at beginning so keep whirring, might go into buffering state
				break;
			case VideoState.PLAYING:
				_delayedBufferingTimer.reset();
				// when we hit buffering, we pause and start waiting
				// for enough to download IF we have a totalTime
				// and IF we haven't already downloaded everything
				if ( !isNaN(totalTime) && totalTime > 0 &&
				     bytesLoaded > 0 && bytesLoaded < uint.MAX_VALUE && bytesLoaded < bytesTotal ) {
					pause();
					if (_state == VideoState.PAUSED) {
						waitingForEnough = true;
						playWhenEnoughDownloaded();
					}
				} else {
					setState(VideoState.BUFFERING);
				}
				break;
			default:
				// any other state, bail and kill timer
				_delayedBufferingTimer.reset();
				break;
			}
		}

		/**
		 * Wrapper for <code>NetStream.pause()</code>.  Never call
		 * <code>NetStream.pause()</code> directly, always call this
		 * method because it does some other housekeeping.
		 *
		 * @private
		 */
		flvplayback_internal function _pause(doPause:Boolean):void {
			//ifdef DEBUG
			//debugTrace("_pause(" + doPause + ")");
			//endif
			_atEndCheckPlayhead = playheadTime;
			_rtmpDoStopAtEndTimer.reset();
			if (doPause) {
				_ns.pause();
			} else {
				_ns.resume();
			}
		}

		/**
		 * Wrapper for <code>NetStream.play()</code>.  Never call
		 * <code>NetStream.play()</code> directly, always call this
		 * method because it does some other housekeeping.
		 *
		 * @private
		 */
		flvplayback_internal function _play(startTime:int=0, endTime:int=-1):void {
			//ifdef DEBUG
			//var debugString:String = "_play("
			//if (arguments.length > 0) {
			//	debugString += arguments[0];
			//	if (arguments.length > 1) {
			//		debugString += ", " + arguments[1];
			//	}
			//}
			//debugString += ")";
			//debugTrace(debugString);
			//debugTrace("_ncMgr.streamName = " + _ncMgr.streamName);
			//endif
			waitingForEnough = false;
			_rtmpDoStopAtEndTimer.reset();
			_startingPlay = true;
			
			if(_isDVR && _dvrMgr != null){
				if(_dvrMgr.offset > 0){
					startTime = _dvrMgr.offset;
				}
			}
			
			if(_ncMgr.isDynamicStream == true) {
				// Flash10 Code
				_playDynamicStream(startTime, endTime);
			} else {
				_ns.play(_ncMgr.streamName, (_isLive) ? -1 : startTime, endTime);
			}
		}
		
		/**
		 * Extension of _play to handle the necessary elements required for Dynamic Streaming.
		 *  
		 * @param startTime
		 * @param endTime
		 * 
		 */		
		flvplayback_internal function _playDynamicStream(startTime:int=0, endTime:int=-1):void {
			
				var ts:Array = _ncMgr.streams;
				var dsi:DynamicStreamItem = new DynamicStreamItem();
					dsi.start = (_isLive) ? -1 : startTime;
					dsi.len = endTime;
				for(var i:int=0; i< ts.length; i++){
					dsi.addStream(ts[i].src, ts[i].bitrate/1000);
				}
				
				_ns.play(dsi);
		}

		/**
		 * Wrapper for <code>NetStream.seek()</code>.  Never call
		 * <code>NetStream.seek()</code> directly, always call
		 * this method because it does some other housekeeping.
		 *
		 * @private
		 */
		flvplayback_internal function _seek(time:Number):void {
			//ifdef DEBUG
			//debugTrace("_seek(" + time + ")");
			//endif
			_rtmpDoStopAtEndTimer.reset();
			if ( _metadata != null &&
			     _metadata.audiodelay != undefined &&
				 (isNaN(_streamLength) || time + _metadata.audiodelay < _streamLength) ) {
				time += _metadata.audiodelay;
			}
			
			if(_isDVR && _dvrMgr != null){
				if(time > _dvrMgr.currentDuration - _ns.bufferTime) {
					// cannot go below 0
					time = (_dvrMgr.currentDuration - _ns.bufferTime >= 0) ? _dvrMgr.currentDuration - _ns.bufferTime : 0;
				}
			} 
			
			_ns.seek(time);
			_lastSeekTime = time;
			_invalidSeekTime = false;
			_bufferState = BUFFER_EMPTY;
			_sawPlayStop = false;
			_sawSeekNotify = false;
		}

		/**
		 * Gets whether connected to a stream.  If not, then calls to APIs
		 * <code>play() with no args</code>, <code>stop()</code>,
		 * <code>pause()</code> and <code>seek()</code> will throw
		 * exceptions.
		 *
		 * @see #stateResponsive
		 * @private
		 */
		flvplayback_internal function isXnOK():Boolean {
			if (_state == VideoState.LOADING) return true;
			if (_state == VideoState.CONNECTION_ERROR) return false;
			if (_state != VideoState.DISCONNECTED) {
				if ( _ncMgr == null ||
				     _ncMgr.netConnection == null ||
					 (_ncMgr.isRTMP && !_ncMgr.netConnection.connected) ) {
					setState(VideoState.DISCONNECTED);
					return false;
				}
				return true;
			}
			return false;
		}

		/**
		 * Kicks off autoresize process
		 *
		 * @private
		 */
		flvplayback_internal function startAutoResize():void {
			switch (_state) {
			case VideoState.DISCONNECTED:
			case VideoState.CONNECTION_ERROR:
				// autoresize will happen later automatically
				return;
			default:
				// starting a resize when there is no NetStream can cause problems!
				if (_ns == null) {
					return;
				}
				_autoResizeDone = false;
				if ( stateResponsive &&
				     ( super.videoWidth != 0 || super.videoHeight != 0 ||
					   _bufferState == BUFFER_FULL || _bufferState == BUFFER_FLUSH ||
				       _ns.time > autoResizePlayheadTimeout ) ) {
					// do it now!
					doAutoResize();
				} else {
					// do it on an interval, it won't happen until we are
					// back in a responsive state
					_autoResizeTimer.reset();
					_autoResizeTimer.start();
				}
				break;
			}
		}

		/**
		 * <p>Does the actual work of resetting the width and height.</p>
		 *
		 * <p>Called on an interval which is stopped when width and height
		 * of the <code>Video</code> object are not zero.  Finishing the
		 * resize is done in another method which is either called on a
		 * interval set up here for live streams or on a
		 * NetStream.Play.Stop event in <code>rtmpNetStatus</code> after
		 * stream is rewound if it is not a live stream.  Still need to
		 * get a http solution.</p>
		 *
		 * @private
		 */
		flvplayback_internal function doAutoResize(e:TimerEvent=null):void {
			//ifdef DEBUG
			//debugTrace("doAutoResize(), super.videoWidth = " + super.videoWidth + ", super.videoHeight = " + super.videoHeight);
			//endif

			if (_autoResizeTimer.running) {
				switch (_state) {
				case VideoState.RESIZING:
				case VideoState.LOADING:
					break;
				case VideoState.DISCONNECTED:
				case VideoState.CONNECTION_ERROR:
					// autoresize will happen later automatically
					_autoResizeTimer.reset();
					return;
				default:
					if (!stateResponsive) {
						// keep trying until we get into a responsive state
						return;
					}
				}
				if ( super.videoWidth != _prevVideoWidth || super.videoHeight != _prevVideoHeight ||
					 _bufferState == BUFFER_FULL || _bufferState == BUFFER_FLUSH ||
					 _ns.time > autoResizePlayheadTimeout ) {
					// if have not received metadata yet, slight delay to avoid race condition in player
					// but there may not be any metadata, so cannot wait forever
					if (_hiddenForResize && !_ns.client.ready && _hiddenForResizeMetadataDelay < autoResizeMetadataDelayMax) {
						//ifdef DEBUG
						//debugTrace("Delaying for metadata: " + _hiddenForResizeMetadataDelay);
						//endif
						_hiddenForResizeMetadataDelay++;
						return;
					}
					_autoResizeTimer.reset();
				} else {
					// keep trying until our size is set
					return;
				}
			}
			// do not need to do autoresize, but DO need to signal readyness
			if (_autoResizeDone) {
				setState(_cachedState);
				return;
			}
			//ifdef DEBUG
			//debugTrace("Actually doing autoResize, _videoWidth = " + _videoWidth + ", _videoHeight = " + _videoHeight + ", super.videoWidth = " + super.videoWidth + ", super.videoHeight = " + super.videoHeight);
			//endif
			oldBounds = new Rectangle(x, y, width, height);
			oldRegistrationBounds = new Rectangle(registrationX, registrationY, registrationWidth, registrationHeight);
			_autoResizeDone = true;

			// The getters for videoWidth and videoHeight have priorities built-in to them as to whether we
			// trust a value that came from the NCManager, from the metadata or the super.videoWidth and
			// super.videoHeight.  We will never use super.videoWidth and super.videoHeight unless _readyDispatched
			// is true, so we cache the present value and set it to true temporarily
			var cacheReadyDispatched:Boolean = _readyDispatched;
			_readyDispatched = true;
			var theVideoWidth:int = videoWidth;
			var theVideoHeight:int = videoHeight;
			_readyDispatched = cacheReadyDispatched;

			switch (_scaleMode) {
			case VideoScaleMode.NO_SCALE:
				super.width = Math.round(theVideoWidth);
				super.height = Math.round(theVideoHeight);
				break;
			case VideoScaleMode.EXACT_FIT:
				super.width = registrationWidth;
				super.height = registrationHeight;
				break;
			case VideoScaleMode.MAINTAIN_ASPECT_RATIO:
			default:
				var newWidth:Number = (theVideoWidth * _registrationHeight / theVideoHeight);
				var newHeight:Number = (theVideoHeight * _registrationWidth / theVideoWidth);
				if (newHeight < _registrationHeight) {
					super.width = _registrationWidth;
					super.height = newHeight;
				} else if (newWidth < _registrationWidth) {
					super.width = newWidth;
					super.height = _registrationHeight;
				} else {
					super.width = _registrationWidth;
					super.height = _registrationHeight;
				}
			}
			// use registration points and align value to layout
			switch (_align) {
			case VideoAlign.CENTER:
			case VideoAlign.TOP:
			case VideoAlign.BOTTOM:
			default:
				super.x = Math.round(_registrationX + ((_registrationWidth - width) / 2));
				break;
			case VideoAlign.LEFT:
			case VideoAlign.TOP_LEFT:
			case VideoAlign.BOTTOM_LEFT:
				super.x = Math.round(_registrationX);
				break;
			case VideoAlign.RIGHT:
			case VideoAlign.TOP_RIGHT:
			case VideoAlign.BOTTOM_RIGHT:
				super.x = Math.round(_registrationX + (_registrationWidth - width));
				break;
			}
			switch (_align) {
			case VideoAlign.CENTER:
			case VideoAlign.LEFT:
			case VideoAlign.RIGHT:
			default:
				super.y = Math.round(_registrationY + ((_registrationHeight - height) / 2));
				break;
			case VideoAlign.TOP:
			case VideoAlign.TOP_LEFT:
			case VideoAlign.TOP_RIGHT:
				super.y = Math.round(_registrationY);
				break;
			case VideoAlign.BOTTOM:
			case VideoAlign.BOTTOM_LEFT:
			case VideoAlign.BOTTOM_RIGHT:
				super.y = Math.round(_registrationY + (_registrationHeight - height));
				break;
			}
			if (_hiddenForResize) {
				_hiddenRewindPlayheadTime = playheadTime;
				if (_state == VideoState.LOADING) {
					_cachedState = VideoState.PLAYING;
				}
				if (!_ncMgr.isRTMP) {
					_pause(true);
					_seek(0);
					_finishAutoResizeTimer.reset();
					_finishAutoResizeTimer.start();
				} else if (!_isLive) {
					_currentPos = 0;
					_play(0, 0);
					setState(VideoState.RESIZING)
				} else if (_autoPlay) {
					_finishAutoResizeTimer.reset();
					_finishAutoResizeTimer.start();
				} else {
					finishAutoResize();
				}
			} else {
				dispatchEvent(new AutoLayoutEvent(AutoLayoutEvent.AUTO_LAYOUT, false, false, oldBounds, oldRegistrationBounds));
			}
		}

		/**
		 * <p>Makes video visible, turns on sound and starts
		 * playing if live or autoplay.</p>
		 *
		 * @private
		 */
		flvplayback_internal function finishAutoResize(e:TimerEvent=null):void {
			//ifdef DEBUG
			//debugTrace("finishAutoResize()");
			//endif
			if (stateResponsive) return;
			_hiddenForResize = false;
			super.visible = __visible;
			volume = _volume;
			//ifdef DEBUG
			//debugTrace("_autoPlay = " + _autoPlay);
			//endif
			dispatchEvent(new AutoLayoutEvent(AutoLayoutEvent.AUTO_LAYOUT, false, false, oldBounds, oldRegistrationBounds));
			if (_autoPlay) {
				if (_ncMgr.isRTMP) {
					if (!_isLive) {
						_currentPos = 0;
						_play(0);
					}
					if (_state == VideoState.RESIZING) {
						setState(VideoState.LOADING);
						_cachedState = VideoState.PLAYING;
					}
				} else {
					waitingForEnough = true;
					_cachedState = _state;
					_state = VideoState.PAUSED;
					checkReadyForPlay(bytesLoaded, bytesTotal);
					if (waitingForEnough) {
						_state = _cachedState;
						setState(VideoState.PAUSED);
					} else {
						_cachedState = VideoState.PLAYING;
					}
				}
			} else {
				setState(VideoState.STOPPED);
			}
		}

		/**
		 * <p>Creates <code>NetStream</code> and does some basic
		 * initialization.</p>
		 *
		 * @private
		 */
		flvplayback_internal function _createStream():void {
			//ifdef DEBUG
			//debugTrace("_createStream()");
			//endif
			_ns = null;
			
			var theNS:NetStream;
			if(_ncMgr.isDynamicStream == true){
				theNS = new DynamicStream(_ncMgr.netConnection);
			} else {
				theNS = new NetStream(_ncMgr.netConnection);
			}
			
			if (_ncMgr.isRTMP) {
				theNS.addEventListener(NetStatusEvent.NET_STATUS, rtmpNetStatus);
			} else {
				theNS.addEventListener(NetStatusEvent.NET_STATUS, httpNetStatus);
			}
			theNS.client = createNetStreamClient();
			theNS.bufferTime = _bufferTime;
			theNS.soundTransform = soundTransform
			_ns = theNS;
			attachNetStream(_ns);
		}

		/**
		 * Does initialization after first connecting to the server
		 * and creating the stream.  Will get the stream duration from
		 * the <code>INCManager</code> if it has it for us.
		 *
		 * <p>Starts resize if necessary, otherwise starts playing if
		 * necessary, otherwise loads first frame of video.  In http case,
		 * starts progressive download in any case.</p>
		 *
		 * @private
		 */
		flvplayback_internal function _setUpStream():void {
			//ifdef DEBUG
			//debugTrace("_setUpStream()");
			//endif

			// INCManager MIGHT have gotten the stream length, width and height for
			// us.  If its length is less than 0, then it did not.
			if ( !isNaN(_ncMgr.streamLength) && _ncMgr.streamLength >= 0 ) {
				_streamLength = _ncMgr.streamLength;
			}
			_videoWidth = (_ncMgr.streamWidth >= 0) ? _ncMgr.streamWidth : -1;
			_videoHeight = (_ncMgr.streamHeight >= 0) ? _ncMgr.streamHeight : -1;
			_resizeImmediatelyOnMetadata = ((_videoWidth >= 0 && _videoHeight >= 0) || _scaleMode == VideoScaleMode.EXACT_FIT);

			if (!_hiddenForResize) {
				__visible = super.visible;
				super.visible = false;
				_volume = volume;
				volume = 0;
				_hiddenForResize = true;
			}
			_hiddenForResizeMetadataDelay = 0;
			_play(0);
			if (_currentPos > 0) {
				_seek(_currentPos);
				_currentPos = 0;
			}

			_autoResizeTimer.reset();
			_autoResizeTimer.start();
		}

		/**
		 * <p>ONLY CALL THIS WITH RTMP STREAMING</p>
		 *
		 * <p>Only used for rtmp connections.  When we pause or stop,
		 * setup an interval to call this after a delay (see property
		 * <code>idleTimeout</code>).  We do this to spare the server from
		 * having a bunch of extra xns hanging around, although this needs
		 * to be balanced with the load that creating connections puts on
		 * the server, and keep in mind that Flash Media Server (FMS) can be configured to
		 * terminate idle connections on its own, which is a better way to
		 * manage the issue.</p>
		 *
		 * @private
		 */
		flvplayback_internal function doIdleTimeout(e:TimerEvent):void
		{
			//ifdef DEBUG
			//debugTrace("Closing NetConnection NOW");
			//endif
			close();
		}

		/**
		 * Dumps all queued commands without executing them
		 *
		 * @private
		 */
		flvplayback_internal function flushQueuedCmds():void {
			//ifdef DEBUG
			//debugTrace("flushQueuedCmds()");
			//endif
			while (_cmdQueue.length > 0) _cmdQueue.pop();
		}

		/**
		 * Executes as many queued commands as possible, obviously
		 * stopping when state becomes unresponsive.
		 *
		 * @private
		 */
		flvplayback_internal function execQueuedCmds():void {
			//ifdef DEBUG
			//debugTrace("execQueuedCmds()");
			//endif
			while ( _cmdQueue.length > 0 && (stateResponsive || _state == VideoState.DISCONNECTED || _state == VideoState.CONNECTION_ERROR) &&
					( _cmdQueue[0].url != null ||
					  (_state != VideoState.DISCONNECTED && _state != VideoState.CONNECTION_ERROR) ) ) {
				//ifdef DEBUG
				//debugTrace("Exec Queued Command!");
				//endif
				try {
					var nextCmd:Object = _cmdQueue.shift();
					_cachedState = _state;
					_state = VideoState.EXEC_QUEUED_CMD;
					switch (nextCmd.type) {
					case QueuedCommand.PLAY:
						play(nextCmd.url, nextCmd.time, nextCmd.isLive);
						break;
					case QueuedCommand.LOAD:
						load(nextCmd.url, nextCmd.time, nextCmd.isLive);
						break;
					case QueuedCommand.PAUSE:
						pause();
						break;
					case QueuedCommand.STOP:
						stop();
						break;
					case QueuedCommand.SEEK:
						seek(nextCmd.time);
						break;
					case QueuedCommand.PLAY_WHEN_ENOUGH:
						playWhenEnoughDownloaded();
						break;
					} // switch
				} finally {
					if (_state == VideoState.EXEC_QUEUED_CMD) {
						_state = _cachedState;
					}
				}
			}
		}

		/**
		 * @private
		 */
		flvplayback_internal function queueCmd(type:Number, url:String=null, isLive:Boolean=false, time:Number=NaN):void {
			//ifdef DEBUG
			//debugTrace("queueCmd(" + type + ", " + url + ", " + isLive + ", " + time + ")");
			//endif
			_cmdQueue.push(new QueuedCommand(type, url, isLive, time));
		}

		//ifdef DEBUG
		//public function debugTrace(s:String):void {
		//	if (parent != null && parent is fl.video.FLVPlayback) {
		//		var flvParent:FLVPlayback = FLVPlayback(parent);
		//		flvParent.debugTrace(s);
		//	} else {
		//		trace(s);
		//	}
		//}
		//endif

	} // class VideoPlayer

} // package fl.video
