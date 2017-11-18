/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.net {
/**
 * Take a fileName, byteArray, and parameters object as input and return ByteArray post data suitable for a UrlRequest as output
 *
 * @see http://www.marstonstudio.com/?p=36
 * @see http://www.w3.org/TR/html4/interact/forms.html
 * @see http://www.jooce.com/blog/?p=143
 * @see http://www.jooce.com/blog/wp%2Dcontent/uploads/2007/06/uploadFile.txt
 * @see http://blog.je2050.de/2006/05/01/save-bytearray-to-file-with-php/
 *
 * @author Jonathan Marston
 * @version 2007.08.19
 *
 * This work is licensed under a Creative Commons Attribution NonCommercial ShareAlike 3.0 License.
 * @see http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
public class UploadPostHelper {
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	/**
	 * Boundary used to break up different parts of the http POST body
	 */
	private static var _boundary:String = "";

	/**
	 * Get the boundary for the post.
	 * Must be passed as part of the contentType of the UrlRequest
	 */
	public static function getBoundary():String {
		if (_boundary.length === 0) {
			for (var i:int = 0; i < 0x20; ++i) {
				_boundary += String.fromCharCode(int(97 + Math.random() * 25));
			}
		}
		return _boundary;
	}

	private static function addContentType(postData:ByteArray, type:String):void {
		var bytes:String = 'Content-Type: ' + type;
		for (var i:int = 0; i < bytes.length; ++i) {
			postData.writeByte(bytes.charCodeAt(i));
		}
		postData = LINEBREAK(postData);
		postData = LINEBREAK(postData);
	}

	/**
	 * Create post data to send in a UrlRequest
	 */
	public static function getPostData(fileName:String, byteArray:ByteArray, parameters:Array = null):ByteArray {
		var i:int;
		var bytes:String;

		var postData:ByteArray = new ByteArray();
		postData.endian = Endian.BIG_ENDIAN;

		//add Filename to parameters
		if(parameters === null) {
			parameters = [];
		}

		//add parameters to postData
		for (var j:int = 0; j < parameters.length; ++j) {
			postData = BOUNDARY(postData);
			postData = LINEBREAK(postData);
			bytes = 'Content-Disposition: form-data; name="' + parameters[j].name + '"';
			for (i = 0; i < bytes.length; ++i) {
				postData.writeByte(bytes.charCodeAt(i));
			}
			postData = LINEBREAK(postData);
			if (parameters[j].type) {
				addContentType(postData, parameters[j].type);
			} else {
				postData = LINEBREAK(postData);
			}
			postData.writeUTFBytes(parameters[j].value);
			postData = LINEBREAK(postData);
		}

		if (fileName && byteArray) {
			//add Filedata to postData
			postData = BOUNDARY(postData);
			postData = LINEBREAK(postData);
			bytes = 'Content-Disposition: form-data; name="stream"; filename="';
			for (i = 0; i < bytes.length; ++i) {
				postData.writeByte( bytes.charCodeAt(i) );
			}
			postData.writeUTFBytes(fileName);
			postData = QUOTATIONMARK(postData);
			postData = LINEBREAK(postData);
			addContentType(postData, "application/octet-stream");
			postData.writeBytes(byteArray, 0, byteArray.length);
			postData = LINEBREAK(postData);

			//closing boundary
			postData = BOUNDARY(postData);
			postData = DOUBLEDASH(postData);
		}
		return postData;
	}

	/**
	 * Add a boundary to the PostData with leading doubledash
	 */
	private static function BOUNDARY(p:ByteArray):ByteArray {
		var l:int = UploadPostHelper.getBoundary().length;

		p = DOUBLEDASH(p);
		for (var i:int = 0; i < l; ++i) {
			p.writeByte(_boundary.charCodeAt(i));
		}
		return p;
	}

	/**
	 * Add one linebreak
	 */
	private static function LINEBREAK(p:ByteArray):ByteArray {
		p.writeShort(0x0d0a);
		return p;
	}

	/**
	 * Add quotation mark
	 */
	private static function QUOTATIONMARK(p:ByteArray):ByteArray {
		p.writeByte(0x22);
		return p;
	}

	/**
	 * Add Double Dash
	 */
	private static function DOUBLEDASH(p:ByteArray):ByteArray {
		p.writeShort(0x2d2d);
		return p;
	}
}
}
