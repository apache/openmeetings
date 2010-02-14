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
     * @private
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
	 */
	public class ParseResults {
		public var isRelative:Boolean;
		public var isRTMP:Boolean;
		public var protocol:String;
		public var serverName:String;
		public var portNumber:String;
		public var appName:String;
		public var streamName:String;
		public var wrappedURL:String;

		public function ParseResults() {
		}
	} // class ParseResults

} // package fl.video
