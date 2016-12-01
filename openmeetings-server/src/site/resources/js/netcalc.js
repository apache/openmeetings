//Based on sample: http://www.speakingofcode.com/index.php/developer/9-interactive-jquery-price-slider-calculator
//Copyright:
// "The content on this site is free and you will never be asked to pay for any code or demo. We are huge fans of the Open Source world and sharing is caring."
// See at: http://www.speakingofcode.com/index.php/about

/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

$(document).ready(function() {
	$("#sib").val("392");
	$("#sob").val("392");
	$("#uib").val("392");
	$("#uob").val("196");

	$("#amount_cam_res").val(
			"Cam resolution: 120x90 [4:3 (~196 kbit/s)]");
	$("#amount_pwv").val("2 participants with audio/video");
	$("#amount_pnv").val("Participants without audio/video?");

	$(function() {
		var p = {
			0 : "None (Only audio ~44 kbit/s)",
			1 : "Cam resolution: 40x30 [4:3 (~83 kbit/s)]",
			2 : "Cam resolution: 80x60 [4:3 (~129 kbit/s)]",
			3 : "Cam resolution: 120x90 [4:3 (~196 kbit/s)]",
			4 : "Cam resolution: 160x120 [QQVGA 4:3 (~246 kbit/s)]",
			5 : "Cam resolution: 240x180 [4:3 (~251 kbit/s)]",
			6 : "Cam resolution: 320x240 [HVGA 4:3 (~252 kbit/s)]",
			7 : "Cam resolution: 480x360 [4:3  (~469 kbit/s)]",
			8 : "Cam resolution: 640x480 [4:3 (~904 kbit/s)]",
			9 : "Cam resolution: 1024x768 [XGA 4:3 (~2090 kbit/s)]",
			10 : "Cam resolution: 256x150 [16:9 (~252 kbit/s)]",
			11 : "Cam resolution: 432x240 [WQVGA 9:5 (~283 kbit/s)]",
			12 : "Cam resolution: 480x234 [pseudo 16:9 (~302 kbit/s)]",
			13 : "Cam resolution: 512x300 [16:9 (~411 kbit/s)]",
			14 : "Cam resolution: 640x360 [nHD 16:9 (~615 kbit/s)]",
			15 : "Cam resolution: 1024x600 [16:9 (~1624 kbit/s)] ",
		};
		var t = {
			0 : "44",
			1 : "83",
			2 : "129",
			3 : "196",
			4 : "246",
			5 : "251",
			6 : "252",
			7 : "469",
			8 : "904",
			9 : "2090",
			10 : "252",
			11 : "283",
			12 : "302",
			13 : "411",
			14 : "615",
			15 : "1624",
		}

		$("#slider-cam-res").slider({
			value : "3",
			min : 0,
			max : 15,
			step : 1,
			slide : function(event, ui) {
				$("#cam_res").val(t[ui.value]);
				$("#amount_cam_res").val(p[ui.value]);
				var camRes = $("#cam_res").val();
				var numWithCam = $("#pwv").val();
				var numWithoutCam = $("#pnv").val();
				$("#sib").val(+camRes * +numWithCam);
				$("#sob").val(+camRes * +numWithCam * (+numWithCam + +numWithoutCam - 1));
				$("#uib").val(+camRes * +numWithCam);
				$("#uob").val(+camRes);
			}
		});

		$("#slider-num-with-cam").slider({
			value : "2",
			min : 0,
			max : 100,
			step : 1,
			slide : function(event, ui) {
				$("#pwv").val(ui.value);
				$("#amount_pwv").val(ui.value + " participant(s) with audio/video");
				var camRes = $("#cam_res").val();
				var numWithCam = $("#pwv").val();
				var numWithoutCam = $("#pnv").val();
				$("#sib").val(+camRes * +numWithCam);
				$("#sob").val(+camRes * +numWithCam * (+numWithCam + +numWithoutCam - 1));
				$("#uib").val(+camRes * +numWithCam);
				$("#uob").val(+camRes);
			}
		});
		$("#slider-num-without-cam").slider({
			value : "0",
			min : 0,
			max : 100,
			step : 1,
			slide : function(event, ui) {
				$("#pnv").val(ui.value);
				$("#amount_pnv").val(ui.value + " participant(s) without audio/video");
				var camRes = $("#cam_res").val();
				var numWithCam = $("#pwv").val();
				var numWithoutCam = $("#pnv").val();
				$("#sib").val(+camRes * +numWithCam);
				$("#sob").val(+camRes * +numWithCam * (+numWithCam + +numWithoutCam - 1));
				$("#uib").val(+camRes * +numWithCam);
				$("#uob").val(+camRes);
			}
		});
		$("#cam_res").val('$' + $("#slider-cam-res").slider("value"));
		$("#pwv").val('$' + $("#slider-num-with-cam").slider("value"));
		$("#pnv").val('$' + $("#slider-num-without-cam").slider("value"));

		$("#cam_res").val("196");
		$("#pwv").val("2");
		$("#pnv").val("0");
	});
});
