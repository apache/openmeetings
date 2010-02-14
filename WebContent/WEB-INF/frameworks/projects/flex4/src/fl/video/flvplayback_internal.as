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
	 * Almost all var, const and function definitions in the fl.video
	 * package are not made private or protected or package internal,
	 * but are instead put into the flvplayback_internal namespace.
	 * (There are SOME privates, mostly variables that are accessible
	 * via public get/set methods.) The reasoning behind this is that
	 * in the past there have been unforeseen use cases which were
	 * only achievable by hacking into the private and protected
	 * methods, which was possible with AS2 but completely impossible
	 * with AS3. So if a user wants to hack, use namespace
	 * flvplayback_internal and hack away!
	 * 
	 * @private
	 */
	public namespace flvplayback_internal = "http://www.adobe.com/2007/flash/flvplayback/internal";

}
