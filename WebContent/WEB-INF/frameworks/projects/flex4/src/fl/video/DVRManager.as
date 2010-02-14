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

	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.TimerEvent;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.net.Responder;
	import flash.utils.Timer;
	import flash.utils.getTimer;
	
	/**
	 *
	 * Use the DVRManager class to manage queries issued to Flash Media Server for information
	 * about DVR streams.  This class is optimized for use with the Flash Media Server
	 * DVRCast application available from Adobe or from Flash Video Streaming Service providers.
     *
     * @langversion 3.0
     * @playerversion Flash 10
     * @playerversion AIR 1.5
     * @productversion FLVPlayback 2.5           
	 * 
	 */
	public class DVRManager extends EventDispatcher
	{
		private var _streamName:String; // the stream name the object is to query for
		private var _nc:NetConnection;
		private var _infoResponder:Responder;
		private var _currentDuration:Number;
		private var _intervalTimer:Number;
		private var _totalDuration:Number;
		private var _durationStart:Number;
		private var _durationNow:Number;
		private var _durationTimer:Timer = new Timer(1000, 0);
		private var _offset:Number;
		private var _offline:Boolean;
		private var _isRec:Boolean;
		private var _retryTimer:Timer;
		private var _dvrStreamName:String;
		private var _dvrNS:NetStream;
		private var _retryCount:int;
		private var _offsetSet:Boolean;
		
		// set this to -1 to retry infinitely
		// default is 5 to prevent server app from becoming inundated with calls when in stream does not exist
		private const RETRY_LIMIT:int = 5; 
		
		public static const DVR_EVENT:String = "dvrEvent";
		public static const DVR_ERROR:String = "dvrError";
		public static const DVR_STOP:String = "dvrStop";
		
		/**
		 * Constructor function.  Pass the NetConnection object
		 * for the stream as an argument.
         *
		 * @param nc The NetConnection object for the DVR stream.
		 * 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5           
		 */		
		public function DVRManager(nc:NetConnection)
		{
			super();
			_nc = nc;
			_currentDuration = 0;
			_totalDuration = 0;
			_intervalTimer = 0;
			_retryCount = 0;
			_infoResponder = new Responder(infoResponderResult, infoResponderStatus);
			_retryTimer = new Timer(5000, 1);
			_retryTimer.addEventListener(TimerEvent.TIMER, handleRetryTimer);
			_offsetSet = false;
			
		}
		
		/**
		 * Response success handler for calls to the server-side <code>DVRGetStreamInfo</code> function.  This function is called
		 * when a successful call has been made to the server to retrieve information about a DVR stream. 
		 * If the call was successful, the handler processes the object returned
		 * from the server.  If the server issued a retry, the handler sets up the retry
		 * attempt and queries again after the elapsed time.  Any response that is not successful
		 * or that failed dispatches an error event.
         *
		 * @param info
		 * 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 */		
		private function infoResponderResult(info:Object):void
		{	

			if(info.code == "NetStream.DVRStreamInfo.Success") {
			
				_currentDuration = (info.data.currLen >= 0) ? info.data.currLen : 0;
				_totalDuration = (info.data.maxLen > 0) ? info.data.maxLen : 0;
				
				var begOffset:Number = (!isNaN(info.data.begOffset)) ? info.data.begOffset : 0;
				var endOffset:Number = (!isNaN(info.data.endOffset)) ? info.data.endOffset : 0;
				
				_offline = info.data.offline;
				_isRec = info.data.isRec;
				_retryCount = 0;
							
				// mark this time in the system clock
				_durationStart = getTimer();
				
				// calculate offset
				if(!_offsetSet) {
					if(endOffset != 0) {
						if(_currentDuration > endOffset) {
							// i.e. if duration is 300 and end offset is 20, then offset becomes 280
							_offset = _currentDuration - endOffset;
						} else {
							// current duration should be larger than beg offset
							if(_currentDuration > begOffset) {
								_offset = begOffset;
							} else {
								// default to current duration
								_offset = _currentDuration;
							}
							
						}
					
					} else if(begOffset != 0) {
						if(_currentDuration > begOffset) {
							_offset = begOffset;
						} else {
							_offset = _currentDuration;
						}	
					} else {
						_offset = 0;
					}
					
					_offsetSet = true;
				}
				
				if(!_isRec && !_offline && _totalDuration == 0) {
					_totalDuration = _currentDuration;
				}
				
				if(_offset > 0 && _totalDuration > 0) {
					_totalDuration = _totalDuration - _offset;
				}
				
				dispatchEvent(new Event(DVR_EVENT, false, false));
				
			} else if( info.code == "NetStream.DVRStreamInfo.Retry" && ( _retryCount < RETRY_LIMIT || RETRY_LIMIT == -1) ) {
				
				// increments retry count so it does not infinitely call the the server
				_retryCount++;
				
				// if retry is not greater than 0 in this case, default to 3 seconds
				// a value less than 1 would be too quick to retry
				var retry:Number = (info.data.retry > 0) ? info.data.retry : 3;
			
					_retryTimer.delay = retry * 1000;
					_retryTimer.reset();
					_retryTimer.start();
				 
			} else { 
				// we have encountered too many retry attempts
				// or a NetStream.DVRStreamInfo.Failed
				// or a code we cannot process to set up the component for DVR streaming
				
				dispatchEvent(new Event(DVR_ERROR, false, false));
			}
			
			
		}
		
		/**
		 * Response fault handler for calls to the server-side <code>DVRGetStreamInfo</code> method.
         *
		 * @param info
		 * @private
		 */		
		private function infoResponderStatus(info:Object):void
		{
			dispatchEvent(new Event(DVR_ERROR, false, false));
		}
		
		/**
		 * Timer event handler for retry attempts issued by the server
		 * @param te
		 * @private
		 */		
		private function handleRetryTimer(te:TimerEvent):void
		{
			_retryTimer.stop();
			this.getStreamDuration(_dvrStreamName);
			
		}
		
		/**
         * Calls the server-side <code>DVRGetStreamInfo</code> function to retrieve information about a DVR stream.
		 * If a new stream call is detected, it also calls the server-side <code>DVRSubscribe</code> function. 
         *
		 * @param streamName
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 * 
		 */		
         
		public function getStreamDuration(streamName:String):void
		{
			if(_dvrStreamName != streamName){
				_nc.call("DVRSubscribe", null, streamName);
			}
			
			_dvrStreamName = streamName;
			
			if(!_retryTimer.running) {
				_nc.call("DVRGetStreamInfo", _infoResponder, streamName);
			}
			_intervalTimer = getTimer();
		}
		
		/**
		 * Determines whether or not a query has been 
		 * issued to the server within a certain amount of time (<code>true</code>), or not (<code>false</code>). 
         *
		 * @param interval The amount of time within which to check.
		 * @return 
		 * 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                        
		 */		
         
		public function checkInterval(interval:Number):Boolean
		{
			if((getTimer() - _intervalTimer)/1000 >= interval) {
				return true;
			} else {
				return false;
			}

		}
		
		/**
		 * The duration of the stream that has been recorded to disk on the server.
		 * This value is estimated after each query to the server to minimize the number of queries.
		 * 
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                
		 */	
         
		public function get currentDuration():Number {
			_durationNow = (getTimer() - _durationStart)/1000 + _currentDuration;
			if(!_isRec && _totalDuration > 0){
				return _totalDuration;
			}
			
			if(_offset > _durationNow){
				return _durationNow;
			} else {
				return _durationNow - offset;
			}
		}
		
		/**
         * The length of a stream if a publisher specifies a duration for a live event.
         * This property requires the Adobe Flash Media Server DVRCast application. 
		 *  
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 * 
		 */	
         
		public function get totalDuration():Number {

			return _totalDuration;
		}
		
		/**
		 * The value, in seconds, that a DVR stream waits before starting to play. 
		 * Use this value to trim streaming data that may have been 
		 * recorded to disk before the beginning of an event.
         *
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                        
		 * 
		 */		
         
		public function get offset():Number {
			
			if(_offset > _currentDuration) {
				return _currentDuration;
			} else {
				return _offset;
			}
		}
		
		/**
		 * Whether or not the stream is offline and not available for DVR playback. 
         *
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 * 
		 */		
         
		public function get offline():Boolean
		{
			return _offline;
		}
		
		/**
		 * Whether or not the stream is currently recording to disk.  A stream that is 
		 * no longer live may be available for DVR playback. 
         *
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 * 
		 */	
         
		public function get isRec():Boolean
		{
			return _isRec;
		}		

		/**
		 * The NetConnection object on which the DVRManager issues queries. 
         *
		 * @return 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5               
		 * 
		 */	
         
		public function get nc():NetConnection
		{
			return _nc;
		}
	
	}
}
