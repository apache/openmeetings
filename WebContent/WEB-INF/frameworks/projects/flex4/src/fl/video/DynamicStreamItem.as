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

package fl.video
{

    /**
     *
     * Use the DynamicStreamItem class to manage streams in an application that uses dynamic streaming. 
     * A DynamicStreamItem object contains items that are stream/bitrate pairs. Pass the DynamicStreamItem object to the <code>DynamicStream.startPlay()</code> method. 
     *
     * For more information, see the DynamicStream class.
     *
     * @author Adobe Systems Incorporated
     *
     * @langversion 3.0
     * @playerversion Flash 10
     * @playerversion AIR 1.5
     * @productversion FLVPlayback 2.5
     * 
     */
     
	public class DynamicStreamItem extends Object
	{
		
		/**
		 * An array of streams and bitrates
		 * 
		 */
         
		private var streamArray:Array;
		
		/**
		 * The start time for the stream. The default value is 0.
         *
         * @private
		 * 
		 * <p>This value is not currently supported by the FLVPlayback 2.5 component.</p>
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
		 */		
         
		public var start:Number;
		
		/**
         * The length of playback, in seconds. The default value is -1, which plays the entire stream. 
         * A value of 0 plays a single frame. Any value greater than 0 plays the stream for that number of seconds.
         *
         * @private
         * 
		 * <p>This value is not currently supported by the FLVPlayback 2.5 component.</p>
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
		 */		
         
		public var len:Number;
		
		/**
		 *
         * Clears any previous play calls and plays the specified stream immediately.
		 * The default value is <code>true</code>.
         *
         * @private
         * 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
		 */		
         
		public var reset:Boolean;
		
		/**
         * Overrides the length of the array contained in the <code>streams</code> property. If you do not specify a value, 
         * the player calculates the value when you call the <code>DynamicStream.startPlay(dsi:DynamicStreamItem)</code> method.
         *
         *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                  
		 */		
         
		public var streamCount:int;

		/**
         * The bitrate of the stream to play first. The default value is -1. The default value plays the 
         * lowest bitrate stream and increments to the highest bitrate that plays smoothly. To start playback at a 
         * higher bitrate, set this value to one of the <code>bitRate</code> arguments you passed to the <code>addStream()</code> method.
         *
         *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                  
		 */
         
		public var startRate:Number;
		
		/**
		 * The URI of the Flash Media Server application to which the client attempts to connect. 
         *
         *
         * @private
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5                  
		 */
         
		public var uri:String;
		
		/**
         * Constructor function. Creates an object of stream names and bit rates (and optionally, other properties) to pass to a DynamicStream object. 
		 * <p> The following code creates a DynamicStreamItem object:</p>
         * <p><code>var dsi:DynamicStreamItem = new DynamicStreamItem();</code></p>
         * 
         *
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5         
		 * 
		 */
         
		public function DynamicStreamItem() {
			
			streamArray = new Array();	
			streamCount = NaN;
			start = 0;
			len = -1;
			reset = true;
			startRate = -1;
			
		}
		
		/**
		 * Adds a stream and bitrate pair to the DynamicStreamItem object 
         *
		 * @param streamName The name of the stream. This method does not know the encoding format of the stream. 
         * Specify the format in the stream name to ensure the server can play the file. For H.264/AAC files, prefix
         * the stream name with <code>mp4:</code>, for example, <code>"mp4:sample1_150kbps.f4v"</code>. If the file 
         * on the server has a filename extension, specify it. For FLV files, do not use a prefix of filename extension,
         * for example, <code>"mystream_150kbps"</code>.
         *
         * @param bitRate The bitrate of the stream in kilobits per second. The application uses this value to determine which 
         * higher or lower bitrate stream to switch to. Specify the value at which the stream was encoded. The <code>addStream()</code> method 
         * does not inspect the stream to verify that this value is accurate.
		 * 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5         
		 */	
         
		public function addStream(streamName:String, bitRate:Number):void {
		
			if(!isNaN(bitRate)) {
				streamArray.push({name:streamName, rate:bitRate});
			}
			streamArray.sortOn("rate", Array.NUMERIC);
		
		}
		
		/**
		 * The array of objects passed to the <code>addStream()</code> method. Each object
         * has a <code>name</code> and a <code>rate</code> property that contain the <code>streamName</code>
         * and <code>bitRate</code> arguments passed to the <code>addStream()</code> method. 
         *
		 * @return 
		 * 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5         
		 */		
         
		public function get streams():Array {
			return streamArray;
		}
		
	}
}