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

    /**
	 * The IVPEvent interface is implemented by video events
	 * that apply to a specific VideoPlayer object within the
	 * FLVPlayback component. When multiple VideoPlayer objects
	 * are used within the FLVPlayback component, each one is assigned
	 * a different index, and these indices are used with
	 * the <code>visibleVideoPlayerIndex</code> property, the <code>activeVideoPlayerIndex</code>
	 * property, and the <code>getVideoPlayer()</code> method.  When an event is triggered that 
	 * is specific to a single VideoPlayer object, the event
	 * class implements the IVPEvent interface and the <code>vp</code>
	 * property is equal to the index of the VideoPlayer object
	 * involved in the event.
     * 
     * @tiptext IVPEvent interface
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
     */
	public interface IVPEvent {


        /**
         * The type of event. The type is case-sensitive.
         * @tiptext type property
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		function get type():String;
        
        /**
         * The index of the VideoPlayer object involved in this event.
         *
         * @tiptext vp property
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		function get vp():uint;

        /**
         * @private (setter)
         *
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
         */
		function set vp(n:uint):void;

	} // interface IVPEvent

} // package fl.video
