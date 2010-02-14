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

	// version info for fl.video classes
	// this file is included by several classes in that package so that
	// this static is a member of each of those classes.
	
   /**
	* State variable indicating the long version number of the component.  
	* The version number is useful when you have a FLA file and need to know the component version.
	* To determine the component version, type the following code trace into the FLA file:
	* 
	* <listing>FLVPlayback.VERSION</listing>
	* 
	* <p>The <code>VERSION</code> variable includes 
	* the major and minor version numbers as well as the revision and build numbers, 
	* for example, 2.0.0.11. The <code>SHORT_VERSION</code> variable includes only the major 
	* and minor version numbers, for example, 2.0. </p>
	* 
    * @see #SHORT_VERSION SHORT_VERSION variable
    *
    * @langversion 3.0
    * @playerversion Flash 9.0.28.0
 	*/ 
	public static const VERSION:String = "2.5.0.11";
	
	/**
	* State variable indicating the short version number of the component.  
	* The version number is useful when you have a FLA file and need to know the component version.
	* To determine the component version, type the following code trace into the FLA file:
	* 
	* <listing>FLVPlayback.SHORT_VERSION</listing>
	* 
	* <p>The <code>SHORT_VERSION</code> variable includes only the major 
	* and minor version numbers, for example, 2.0. The <code>VERSION</code> variable includes 
	* the major and minor version numbers as well as the revision and build numbers, 
	* for example, 2.0.0.11.</p>
	* 
    * @see #VERSION VERSION variable
    *
    * @langversion 3.0
    * @playerversion Flash 9.0.28.0
	*/
	public static const SHORT_VERSION:String = "2.5";
