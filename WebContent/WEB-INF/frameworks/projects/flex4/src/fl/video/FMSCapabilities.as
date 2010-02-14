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

    /*
     * This class represents the features supported by versions of Flash Media Server.
     *
     *
     * @langversion 3.0
     * @playerversion Flash 9.0.28.0
     */

	public dynamic class FMSCapabilities extends Object
	{

		private var _version:String;
		private var _majorVersion:int;
		private var _minorVersion:int;
		private var _updateVersion:int;
		private var _build:int;
		
		// base capabilities of FMS
		private var _dynamicStreaming:Boolean = false;
		private var _dvr:Boolean = false;
		private var _dataStreamAccess:Boolean = false;
		private var _http:Boolean = false;
		
		// supported codecs
		private var _codecs:Object;
		
		/**
		 * Creates an <code>FMSCapabilities</code> object. Pass a Flash Media Server version number to the function. The object represents the features supported by that server version number.
		 * 
		 * @param version
		 *
		 * @langversion 3.0
		 * @playerversion Flash 9.0.28.0
		 */
		public function FMSCapabilities( version:String = "2,0,0,0" )
		{
			// temporary version array
			var va:Array = version.split(",");
			
			_version = version;
			
			// if not specified default to FMS 2 capabilities
			if(version != "2,0,0,0")
				va = _version.split(",");
									
			_majorVersion = isNaN(va[0]) ?  2 : va[0];
			_minorVersion = isNaN(va[1]) ? 0 : va[1];
			_updateVersion = isNaN(va[2]) ? 0 : va[2];
			_build = isNaN(va[3]) ? 0 : va[3];

			createCodecs();
			parseVersion();			
		}
		
		/**
		 * Processes the version numbers and enables flags. 
		 * 
		 */		
		private function parseVersion ():void
		{
		
			if( checkVersion(_version, "3,0,0,0") ){
				_dataStreamAccess = true;
				_codecs.mp4 = true;
				_codecs.aac = true;
			}
		
			if( checkVersion(_version, "3,5,0,0") ){
				_dynamicStreaming = true;
				_codecs.speex = true;
			}
			
			if( checkVersion(_version, "3,5,0,300") ){
		
				_dvr = true;
		   }
		
		}
		
		/**
		 * Compares two version numbers in the form of xx,xx,xx,xx and 
		 * returns true if the version is equal or exceeds the minimum version. 
		 * @param vers
		 * @param minvers
		 * @return 
		 * 
		 */
		private function checkVersion(vers:String, minvers:String):Boolean
		{
				
				var _a:Array = new Array();
				var _b:Array = new Array();
				_a = vers.split(",");
				_b = minvers.split(",");
				
				// cast values as ints
				for(var i:int=0; i<4; i++){
					_a[i] = int(_a[i]);
					_b[i] = int(_b[i]);
				}
				
				// compare major versions
				if(_a[0] > _b[0]){ return true; }
				if(_a[0] < _b[0]){ return false; }
				
				// compare minor  versions
				if(_a[1] > _b[1]){ return true; }
				if(_a[1] < _b[1]){ return false; }
				
				// compare update versions
				if(_a[2] > _b[2]){ return true; }
				if(_a[2] < _b[2]){ return false; }
				
				// compare builds
				if(_a[3] > _b[3]){ return true; }
				if(_a[3] < _b[3]){ return false; }

				// destroy arrays
				_a = null;
				_b = null;
				
				// they are equal
				return true;
		}
		
		/**
		 * Initializes the codecs object based on availability in FMS 2.0 
		 * 
		 */
		private function createCodecs():void
		{
			_codecs = new Object();
			// assign default codecs found in FMS 2.x
			_codecs.spark = true;
			_codecs.vp6 = true;
			_codecs.mp4 = false;
			_codecs.nellyMoser = true;
			_codecs.mp3 = true;
			_codecs.aac = false;
			_codecs.speex = false;
			
		}
		
		/**
		 * An object whose properties are the available codecs. 
		 * @return 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0
		 */		
		public function get codecs():Object
		{
			return _codecs;
		}
		
		/**
		 * A Boolean value indicating whether the data stream is accessible or not. 
		 * @return 
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0 
		 */		
		public function get dataStreamAccess():Boolean
		{
			return _dataStreamAccess;
		}
		
		/**
		 * A Boolean value indicating whether the dynamic streaming feature is available or not. 
		 * @return 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5
		 * 
		 */		
		public function get dynamicStreaming():Boolean
		{
			return _dynamicStreaming;
		}
		
		/**
		 * A Boolean value indicating whether you can turn on DVR features or not. 
		 * @return 
         * @langversion 3.0
         * @playerversion Flash 10
         * @playerversion AIR 1.5
         * @productversion FLVPlayback 2.5
         */     
		public function get dvr():Boolean
		{
			return _dvr;
		}
		
		/**
		 * @private 
		 * 
		 */
		public function set progressive(enabled:Boolean):void
		{
			_http = enabled;
		}
        
        /**
         * A Boolean value indicating whether a stream can download progressively over HTTP.  This value is true by default. 
         * @return
         * @langversion 3.0
         * @playerversion Flash 9.0.28.0 
         */
		
		public function get progressive():Boolean
		{
			return _http;
		}
		
		/**
         * The Flash Media Server version string in the form of xx,xx,xx,xx. 
		 * @return 
		 * 
		 */		
		public function get version():String
		{
			return _version;
		}
	}
}
