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
	import flash.system.Capabilities;
		
	public dynamic class FPMediaCapabilities extends Object
	{
		private var _version:String;
		private var _majorVersion:int;
		private var _minorVersion:int;
		private var _build:int;
		private var _revision:int;
		
		// base capabilities of Flash Player that interact with FMS
		private var _swfVerification:Boolean = false;
		private var _rtmfp:Boolean = false;
		private var _rtmpe:Boolean = false;
		private var _dynamicStreaming:Boolean = false;
		private var _dataStreamAccess:Boolean = false;
		private var _qosStats:Boolean = false;
		
		// supported codecs
		private var _codecs:Object;

		/**
		 * Creates the <code>FPMediaCapabilities</code> object that represents supported FMS features 
		 * in the current version of the Flash Player.
		 * 
		 * @langversion 3.0
		 * @playerversion Flash 9.0.28.0
		 */		
		public function FPMediaCapabilities()
		{
			
			// temporary version array
			var va:Array = getVersion();
			
			_version = va.join(",");
			
			_majorVersion = isNaN(va[0]) ?  2 : va[0];
			_minorVersion = isNaN(va[1]) ? 0 : va[1];
			_build = isNaN(va[2]) ? 0 : va[2];
			_revision = isNaN(va[3]) ? 0 : va[3];

			createCodecs();
			parseVersion();	
		}
		
		/**
		 * Processes the version numbers and enables flags. 
		 * 
		 */		
		private function parseVersion ():void
		{
		
			if( checkVersion(_version, "9,0,115,0") ){
				_dataStreamAccess = true;
				_swfVerification = true;
				_rtmpe = true;
				_codecs.mp4 = true;
				_codecs.aac = true;
			}
		
			if( checkVersion(_version, "10,0,0,0") ){
				_dynamicStreaming = true;
				_qosStats = true;
				_codecs.speex = true;
				_rtmfp = true;
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
		private function checkVersion(vers:String, minvers:String):Boolean{
				
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
				
				// compare builds
				if(_a[2] > _b[2]){ return true; }
				if(_a[2] < _b[2]){ return false; }
				
				// compare revisions
				if(_a[3] > _b[3]){ return true; }
				if(_a[3] < _b[3]){ return false; }

				// destroy arrays
				_a = null;
				_b = null;
				
				// they are equal
				return true;
		}
		
		/**
		 * Obtains the Flash Player version from the Capabilities class and performs a regular expression parse
		 * to extract the major, minor, build and revision from the string. 
		 * @return 
		 * 
		 */		
		private function getVersion():Array
		{
			
			var versionString:String = Capabilities.version;
			var pattern:RegExp = /(\d*),(\d*),(\d*),(\d*)$/;
			var result:Object = pattern.exec(versionString);
			
			if (result != null){
				return new Array(int(result[1]),int(result[2]),int(result[3]),int(result[4]));
			}else{
				return new Array(09,00,28,00);
			}
		}
		
		/**
		 * Initializes the codecs object based on availability in FMS 2.0 and Flash Player 9,0,28,0
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
		 * Returns the available codecs object. 
		 * @return 
		 * 
		 */		
		public function get codecs():Object
		{
			return _codecs;
		}
		
		/**
		 * Returns Data Stream Access availability. 
		 * @return 
		 * 
		 */		
		public function get dataStreamAccess():Boolean
		{
			return _dataStreamAccess;
		}
		
		/**
		 * Returns Dynamic Streaming availability. 
		 * @return 
		 * 
		 */		
		public function get dynamicStreaming():Boolean
		{
			return _dynamicStreaming;
		}
		
		/**
		 * Returns RTMPE availability 
		 * @return 
		 * 
		 */		
		public function get rtmpe():Boolean
		{
			return _rtmpe;
		}
		
		/**
		 * Returns RTMFP availability 
		 * @return 
		 * 
		 */		
		public function get rtmfp():Boolean
		{
			return _rtmfp;
		}
		
		/**
		 * Returns SWF Verification availability 
		 * @return 
		 * 
		 */		
		public function get swfVerification():Boolean
		{
			return _swfVerification;
		}
		
		/**
		 * Returns availability of NetStream Info QoS statistics 
		 * @return 
		 * 
		 */		
		public function get qosSupport():Boolean
		{
			return _qosStats;
		}
				
		/**
		 * Returns the version string of the Flash Player in the form of xx,xx,xx,xx. 
		 * @return 
		 * 
		 */		
		public function get version():String
		{
			return _version;
		}
	}
}