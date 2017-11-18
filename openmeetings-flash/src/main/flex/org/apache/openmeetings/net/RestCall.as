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
import flash.utils.ByteArray;

public class RestCall {
	import flash.events.*;
	import flash.net.*;

	private var url:String = null;
	private var method:String = URLRequestMethod.GET;
	private var contentType:String = null;
	private var dataFormat:String = "text";
	private var errorCallback:Function;
	private var successCallback:Function;
	private var hdrs:Array = [];
	private var requestor:URLLoader = null;

	public function RestCall(options:Object) {
		if (options["url"]) {
			this.url = options.url;
		}
		if (options["method"]) {
			this.method = options.method;
		}
		if (options["contentType"]) {
			this.contentType = options.contentType;
		}
		if (options["dataFormat"]) {
			this.dataFormat = options.dataFormat;
		}
		if (options["errorCallback"]) {
			this.errorCallback = options.errorCallback;
		}
		if (options["successCallback"]) {
			this.successCallback = options.successCallback;
		}
		if (options["hdrs"]) {
			this.hdrs = options.hdrs;
		}
		requestor = new URLLoader();
		requestor.dataFormat = dataFormat;

		//Initiate the transaction
		if (this.successCallback !== null) {
			requestor.addEventListener(Event.COMPLETE, successCallback);
		}
		if (this.errorCallback !== null) {
			requestor.addEventListener(IOErrorEvent.IO_ERROR, errorCallback); //IOErrorEvent.IOERROR
			requestor.addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorCallback);
		}
	}

	public function load(sid:String, vars:ByteArray):URLRequest {
		var request:URLRequest = new URLRequest(url + '?sid=' + sid); //FIXME TODO
		request.method = method;
		if (contentType) {
			var header:URLRequestHeader = new URLRequestHeader('Content-type', contentType);
			request.requestHeaders.push(header);
		}
		for (var i:int = 0; i < hdrs.length; ++i) {
			request.requestHeaders.push(hdrs[i]);
		}
		//Add the URL vars
		request.data = vars;

		try {
			requestor.load(request);
		} catch (e:Error) {
			errorCallback(e);
		}
		return request;
	}

	public function close():void {
		requestor.close();
	}
}
}
