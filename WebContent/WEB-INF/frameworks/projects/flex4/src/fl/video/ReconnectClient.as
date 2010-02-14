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

	use namespace flvplayback_internal;
	
	/**
	 * <p>Holds client-side functions for remote procedure calls (rpc)
	 * from the FMS during reconnection.  One of these objects is created
	 * and passed to the <code>NetConnection.client</code> property.</p>
	 *
     * @private
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	public class ReconnectClient {
		public var owner:NCManager;
		public function ReconnectClient(owner:NCManager) {
			this.owner = owner;
		}

		public function close():void {
			// do nothing, just need this implemented so that when
			// server calls it we do not get an exception
		}

		public function onBWDone(... rest):void {
			//ifdef DEBUG
			//owner.debugTrace("ReconnectClient.onBWDone()");
			//endif
			owner.onReconnected();
		}
	} // class ReconnectClient

} // package fl.video
