var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;try{
if(lz){}}
catch(e){
lz={}};lz.embed={options:{cancelkeyboardcontrol:false,serverroot:null,approot:"",usemastersprite:false},__getlzoptions:function(){
var $0=lz.embed.__getqueryurl(document.location.search).options;return $0
},swf:function(properties,minimumVersion){
var $0=lz.embed;if(minimumVersion==null)minimumVersion=10.1;if(!properties.id){
properties.id="lzapp"+Math.round(Math.random()*1000000)
};var url=properties.url;var $1=$0.__getqueryurl(url);for(var $2 in $1.options){
var $3=$1.options[$2];if($3!=null){
properties[$2]=$3
}};if(properties.accessible&&properties.accessible!="false"){
$1.flashvars+="&accessible=true"
};if(properties.history){
$1.flashvars+="&history=true"
};if(properties.bgcolor!=null){
$1.flashvars+="&bgcolor="+escape(properties.bgcolor)
};var $4=$0.options;if(properties.cancelkeyboardcontrol){
$4.cancelkeyboardcontrol=properties.cancelkeyboardcontrol
};$1.flashvars+="&width="+escape(properties.width);$1.flashvars+="&height="+escape(properties.height);$1.flashvars+="&__lzurl="+escape(url);$1.flashvars+="&__lzminimumversion="+escape(minimumVersion);$1.flashvars+="&id="+escape(properties.id);var url=$1.url+"?"+$1.query;var $5=$0._getAppendDiv(properties.id,properties.appenddivid);var swfargs={width:"100%",height:"100%",id:properties.id,bgcolor:properties.bgcolor,wmode:properties.wmode,flashvars:$1.flashvars,allowfullscreen:properties.allowfullscreen,flash8:url,appenddiv:$5};var app={runtime:"swf",_id:properties.id,appenddiv:$5,setCanvasAttribute:$0._setCanvasAttributeSWF,getCanvasAttribute:$0._getCanvasAttributeSWF,callMethod:$0._callMethodSWF,_ready:$0._ready,_onload:[],_getSWFDiv:$0._getSWFDiv,loaded:false,_sendMouseWheel:$0._sendMouseWheel,_sendAllKeysUp:$0._sendAllKeysUpSWF,_setCanvasAttributeDequeue:$0._setCanvasAttributeDequeue,_setCanvasAttributeQ:[],_sendPercLoad:$0._sendPercLoad,setGlobalFocusTrap:$0.__setGlobalFocusTrapSWF,initargs:$1.initargs,options:$1.options};if($0.applications[properties.id])alert("Warning: an app with the id: "+properties.id+" already exists.");$0[properties.id]=$0.applications[properties.id]=app;if(properties.history==false){
$0.history.active=false
};var $6=$0.getServerRoot()+"flash.js";if(!$0.jsloaded[$6]){
var $7=function(){
lz.embed._setSWF(url,app,swfargs,properties,minimumVersion)
};$0.loadJSLib($6,$7)
}else{
$0._setSWF(url,app,swfargs,properties,minimumVersion)
}},_setSWF:function($0,app,$1,$2,$3){
var $4=lz.embed;var $5=$4.options;var appenddiv=$1.appenddiv;$4.dojo.addLoadedListener($4._loaded,app);$4.dojo.setSwf($1,$3);appenddiv.style.height=$4.CSSDimension($2.height);appenddiv.style.width=$4.CSSDimension($2.width);if($2.cancelmousewheel!=true){
if($4["mousewheel"]){
$4.mousewheel.setCallback(app,"_sendMouseWheel");$4.mousewheel.setEnabled(!$2.cancelmousewheel)
}};if(($1.wmode=="transparent"||$1.wmode=="opaque")&&$4.browser.OS=="Windows"&&($4.browser.isOpera||$4.browser.isFirefox)){
appenddiv.onmouseout=function($0){
appenddiv.mouseisoutside=true
};appenddiv.onmouseover=function($0){
appenddiv.mouseisoutside=false
};appenddiv._gotmouseup=document.onmouseup=function($0){
if(appenddiv.mouseisoutside){
app.callMethod("lz.GlobalMouse.__mouseUpOutsideHandler()")
}}};if($4.browser.isIE&&$0.indexOf("swf8")==-1&&!$5.cancelkeyboardcontrol){
document.onkeydown=function($0){
if(!$0)$0=window.event;if($0.keyCode==9){
app.callMethod("lz.Keys.__browserTabEvent("+$0.shiftKey+")");return false
}}}},__swfSetAppAppendDivStyle:function($0,$1,$2){
var $3=lz.embed.applications[$0].appenddiv;return $3.style[$1]=$2
},lfc:function($0,$1){
if($1==""){
$1="."
}else if(!$1||typeof $1!="string"){
alert("WARNING: lz.embed.lfc() requires a valid serverroot to be specified.");return
};var $2=lz.embed;$2.options.serverroot=$1;if($2.browser.isIE){
if($2.browser.version<9){
if(!window["G_vmlCanvasManager"]){
alert('WARNING: excanvas.js was not loaded, and is required for IE DHTML.  Please ensure your HTML wrapper has a script include in the <head></head>, e.g. <!--[if IE]><script type="text/javascript" src="'+$1+'lps/includes/excanvas.js"></script><![endif]-->"')
}};if($2.browser.version<7){
$2.loadJSLib("http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js")
}};if(!$2.jsloaded[$0]){
var $3=function(){
var $0=lz.embed;$0.lfcloaded=true;var $1=$0.__appqueue;$0.__appqueue=[];if($1.length){
for(var $2=0,$3=$1.length;$2<$3;$2++){
$0.loadJSLib($1[$2])
}}};$2.loadJSLib($0,$3)
}else{
alert("WARNING: lz.embed.lfc() should only be called once.")
}},dhtml:function($0){
var $1=lz.embed;if($1.dhtmlapploaded){
alert("Warning: skipping lz.embed.dhtml() call for "+$0.url+". Only one DHTML application can be loaded per window.  Use iframes to load more than one DHTML application.");return
};if(!$0.id){
$0.id="lzapp"+Math.round(Math.random()*1000000)
};var $2=$1.__getqueryurl($0.url);for(var $3 in $2.options){
var $4=$2.options[$3];if($4!=null){
$0[$3]=$4
}};var $5=$2.url+"?"+$2.query;var appenddiv=$1._getAppendDiv($0.id,$0.appenddivid);if(!$0.skipchromeinstall&&$1.browser.isIE&&$1.browser.version<7){
if(window["CFInstall"]){
CFInstall.check({onmissing:function(){
appenddiv.style.display="none"
},oninstall:function(){
window.location=window.location
}})
}};appenddiv.style.height=$1.CSSDimension($0.height);appenddiv.style.width=$1.CSSDimension($0.width);var $6=$1.options;if($0.cancelkeyboardcontrol){
$6.cancelkeyboardcontrol=$0.cancelkeyboardcontrol
};if($0.serverroot!=null){
$6.serverroot=$0.serverroot
};if($0.approot!=null&&typeof $0.approot=="string"){
$6.approot=$0.approot
};if($0.usemastersprite!=null){
$6.usemastersprite=$0.usemastersprite
};$1.__propcache={bgcolor:$0.bgcolor,width:$0.width,height:$0.height,id:$0.id,appenddiv:$1._getAppendDiv($0.id,$0.appenddivid),url:$5,options:$6};if($1[$0.id])alert("Warning: an app with the id: "+$0.id+" already exists.");var $7=$1[$0.id]=$1.applications[$0.id]={runtime:"dhtml",_id:$0.id,_ready:$1._ready,_onload:[],loaded:false,setCanvasAttribute:$1._setCanvasAttributeDHTML,getCanvasAttribute:$1._getCanvasAttributeDHTML,_setCanvasAttributeDequeue:$1._setCanvasAttributeDequeue,_setCanvasAttributeQ:[],callMethod:$1._callMethodDHTML,_sendAllKeysUp:$1._sendAllKeysUpDHTML,initargs:$2.initargs,options:$2.options};if($0.history==false){
$1.history.active=false
};$1.mousewheel.setEnabled(!$0.cancelmousewheel);if($1.browser.OS=="Windows"&&$1.browser.isFirefox){
window.focus()
};if(!$1.lfcloaded){
$1.__appqueue.push($5);if($0.lfcurl){
$1.lfc($0.lfcurl,$6.serverroot)
}else if($1.lfcloaded!=null){}}else{
$1.dhtmlapploaded=true;$1.loadJSLib($5)
}},applications:{},__dhtmlLoadScript:function($0){
var $1='<script type="text/javascript" language="JavaScript1.5" src="'+$0+'"></script>';document.writeln($1);return $1
},jsloaded:{},jscallbacks:{},loadJSLibHandler:function($0){
var $1=lz.embed;$1.jsloaded[$0]=true;var $2=$1.jscallbacks[$0]||[];delete $1.jscallbacks[$0];for(var $3=0,$4=$2.length;$3<$4;++$3){
$2[$3]()
}},loadJSLib:function(url,$0){
var embed=lz.embed;if($0){
(embed.jscallbacks[url]||(embed.jscallbacks[url]=[])).push($0)
};if(embed.jsloaded[url]!==void 0)return;embed.jsloaded[url]=false;var script=document.createElement("script");embed.__setAttr(script,"type","text/javascript");embed.__setAttr(script,"defer","defer");var addto=document.getElementsByTagName("body")[0]||document.getElementsByTagName("head")[0];if(script.readyState){
script.onreadystatechange=function(){
if(script.readyState=="loaded"||script.readyState=="complete"){
script.onreadystatechange=null;embed.loadJSLibHandler(url);addto.removeChild(script)
}}}else{
script.onload=function(){
script.onload=null;embed.loadJSLibHandler(url)
}};embed.__setAttr(script,"src",url);addto.appendChild(script)
},getServerRoot:function(){
if(lz.embed.__serverroot)return lz.embed.__serverroot;var $0=document.getElementsByTagName("script");var $1;for(var $2=0,$3=$0.length;$2<$3;$2++){
var $4=$0[$2].src;var $5=$4&&$4.indexOf("embed-compressed.js");if($5&&$5>-1){
$1=$4.substring(0,$5);break
}};lz.embed.__serverroot=$1;return $1
},__parselzoptions:function($0){
var $1=$0.split(new RegExp("([,()])"));var $2=1;var $3=2;var $4={};var $5=$2;var $6=null;var $7=null;var $8=0;while($1.length>0){
var $9=$1[0];var $1=$1.slice(1);if($9=="")continue;switch($5){
case $2:
if($9==","){
if($7!=null&&$8==0){
$4[$7]="true"
}}else if($9=="("){
$5=$3;$6=""
}else{
$7=$9
}break;
case $3:
if($9==")"){
$4[$7]=$6;$7=null;$5=$2;$8=0
}else if($9==","){
$6+=","
}else{
$6+=$9;$8++
}break;

}};if($7!=null&&$8==0){
$4[$7]="true"
};return $4
},__getqueryurl:function($0){
var $1=$0.split("?");$0=$1[0];if($1.length==1)return {url:$0,flashvars:"",query:"",initargs:{}};var $2=lz.embed.__parseQuery($1[1]);var $3="";var $4="";var $5={};var $6={};var $7=new RegExp("\\+","g");for(var $8 in $2){
if($8==""||$8==null)continue;var $9=$2[$8];if($8=="lzr"||$8=="lzt"||$8=="debug"||$8=="profile"||$8=="lzbacktrace"||$8=="lzconsoledebug"||$8=="lzdebug"||$8=="lzkrank"||$8=="lzprofile"||$8=="lzcopyresources"||$8=="fb"||$8=="sourcelocators"||$8=="_canvas_debug"||$8=="flexversion"||$8=="lzoptions"||$8=="lzsourceannotations"){
$3+=$8+"="+$9+"&"
};if($8=="lzusemastersprite"||$8=="lzskipchromeinstall"||$8=="lzcancelkeyboardcontrol"||$8=="lzcancelmousewheel"||$8=="lzhistory"||$8=="lzaccessible"){
$5[$8.substring(2)]=$9=="true"
};if($8=="lzapproot"||$8=="lzserverroot"||$8=="lzwmode"){
$5[$8.substring(2)]=$9
};if($6[$8]==null){
$6[$8]=unescape($9.replace($7," "))
};$4+=$8+"="+$9+"&"
};$3=$3.substr(0,$3.length-1);$4=$4.substr(0,$4.length-1);var $a=$2["lzoptions"];if($a!=null){
$a=unescape($a.replace($7," "))
};if($a!=null){
var $b=lz.embed.__parselzoptions($a);for(var $c in $b){
var $d=$b[$c];if($c==="usemastersprite"||$c==="skipchromeinstall"||$c==="cancelkeyboardcontrol"||$c==="cancelmousewheel"||$c==="history"||$c==="accessible"){
$d=$d=="true"
};$5[$c]=$d
}};return {url:$0,flashvars:$4,query:$3,options:$5,initargs:$6}},__parseQuery:function($0){
if($0.indexOf("=")==-1)return;var $1=$0.split("&");var $2={};for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3].split("=");if($4.length==1)continue;var $5=$4[0];var $6=$4[1];$2[$5]=$6
};return $2
},__setAttr:function($0,$1,$2){
$0.setAttribute($1,$2)
},_setCanvasAttributeSWF:function($0,$1,$2){
var $3=lz.embed;if(this.loaded&&$3.dojo.comm[this._id]&&$3.dojo.comm[this._id]["callMethod"]){
if($2){
$3.history._store($0,$1)
}else{
$3.dojo.comm[this._id].setCanvasAttribute($0,$1+"")
}}else{
this._setCanvasAttributeQ.push([$0,$1,$2])
}},_setCanvasAttributeDHTML:function($0,$1,$2){
if(this.loaded&&canvas){
if($2){
lz.embed.history._store($0,$1)
}else if(canvas){
canvas.setAttribute($0,$1)
}}else{
this._setCanvasAttributeQ.push([$0,$1,$2])
}},_loaded:function($0){
var $1=lz.embed;if($1[$0].loaded)return;if($1.dojo.info.commVersion==8){
setTimeout('lz.embed["'+$0+'"]._ready.call(lz.embed["'+$0+'"])',100)
}else{
$1[$0]._ready.call($1[$0])
}},_setCanvasAttributeDequeue:function(){
while(this._setCanvasAttributeQ.length>0){
var $0=this._setCanvasAttributeQ.pop();this.setCanvasAttribute($0[0],$0[1],$0[2])
}},_ready:function($0){
this.loaded=true;if(this._callmethod){
for(var $1=0;$1<this._callmethod.length;$1++){
this.callMethod(this._callmethod[$1])
};this._callmethod=null
};if(this._setCanvasAttributeQ.length>0){
this._setCanvasAttributeDequeue()
};if($0)this.canvas=$0;for(var $1=0;$1<this._onload.length;$1++){
var $2=this._onload[$1];if(typeof $2=="function")$2(this)
};if(this.onload&&typeof this.onload=="function"){
this.onload(this)
}},_getCanvasAttributeSWF:function($0){
if(this.loaded){
return lz.embed.dojo.comm[this._id].getCanvasAttribute($0)
}else{
alert("Flash is not ready: getCanvasAttribute"+$0)
}},_getCanvasAttributeDHTML:function($0){
return canvas[$0]
},browser:{init:function(){
if(this.initted)return;this.browser=this.searchString(this.dataBrowser)||"An unknown browser";this.version=this.searchVersion(navigator.userAgent)||this.searchVersion(navigator.appVersion)||"an unknown version";this.osversion=this.searchOSVersion(navigator.userAgent)||"an unknown osversion";this.subversion=this.searchSubVersion(navigator.userAgent);this.OS=this.searchString(this.dataOS)||"an unknown OS";this.initted=true;this.isNetscape=this.isSafari=this.isOpera=this.isFirefox=this.isIE=this.isIphone=this.isChrome=false;if(this.browser=="Netscape"){
this.isNetscape=true
}else if(this.browser=="Safari"){
this.isSafari=true
}else if(this.browser=="Opera"){
this.isOpera=true
}else if(this.browser=="Firefox"){
this.isFirefox=true
}else if(this.browser=="Explorer"){
this.isIE=true
}else if(this.browser=="iPhone"||this.browser=="iPad"){
this.isSafari=true;this.isIphone=true
}else if(this.OS=="Android"){
this.isSafari=true
}else if(this.browser=="Chrome"){
this.isChrome=true
}},searchString:function($0){
for(var $1=0;$1<$0.length;$1++){
var $2=$0[$1].string;var $3=$0[$1].prop;this.versionSearchString=$0[$1].versionSearch||$0[$1].identity;this.osversionSearchString=$0[$1].osversionSearch||"";if($2){
if($2.indexOf($0[$1].subString)!=-1)return $0[$1].identity
}else if($3)return $0[$1].identity
}},searchVersion:function($0){
var $1=$0.indexOf(this.versionSearchString);if($1==-1)return;return parseFloat($0.substring($1+this.versionSearchString.length+1))
},searchSubVersion:function($0){
var $1=new RegExp(this.versionSearchString+".\\d+\\.\\d+\\.([\\d.]+)");var $2=$1.exec($0);if($2&&$2.length>1)return parseFloat($2[1])
},searchOSVersion:function($0){
var $1=$0.indexOf(this.osversionSearchString);if($1==-1)return;return parseFloat($0.substring($1+this.osversionSearchString.length+1))
},dataBrowser:[{string:navigator.userAgent,subString:"iPhone",identity:"iPhone",versionSearch:"WebKit"},{string:navigator.userAgent,subString:"iPad",identity:"iPad",versionSearch:"WebKit"},{string:navigator.userAgent,subString:"Android",identity:"Android",versionSearch:"WebKit"},{string:navigator.userAgent,subString:"Chrome",identity:"Chrome",versionSearch:"WebKit"},{string:navigator.userAgent,subString:"OmniWeb",versionSearch:"OmniWeb/",identity:"OmniWeb"},{string:navigator.vendor,subString:"Apple",identity:"Safari",versionSearch:"WebKit"},{prop:window.opera,identity:"Opera",versionSearch:"Version"},{string:navigator.vendor,subString:"iCab",identity:"iCab"},{string:navigator.vendor,subString:"KDE",identity:"Konqueror"},{string:navigator.userAgent,subString:"Firefox",identity:"Firefox"},{string:navigator.userAgent,subString:"Iceweasel",versionSearch:"Iceweasel",identity:"Firefox"},{string:navigator.vendor,subString:"Camino",identity:"Camino"},{string:navigator.userAgent,subString:"Netscape",identity:"Netscape"},{string:navigator.userAgent,subString:"MSIE",identity:"Explorer",versionSearch:"MSIE",osversionSearch:"Windows NT"},{string:navigator.userAgent,subString:"Gecko",identity:"Mozilla",versionSearch:"rv"},{string:navigator.userAgent,subString:"Mozilla",identity:"Netscape",versionSearch:"Mozilla"}],dataOS:[{string:navigator.platform,subString:"Win",identity:"Windows"},{string:navigator.platform,subString:"Mac",identity:"Mac"},{string:navigator.userAgent,subString:"iPhone",identity:"iPhone/iPod/iPad"},{string:navigator.userAgent,subString:"iPad",identity:"iPhone/iPod/iPad"},{string:navigator.userAgent,subString:"Android",identity:"Android"},{string:navigator.platform,subString:"Linux",identity:"Linux"}]},_callMethodSWF:function($0){
if(this.loaded){
return lz.embed.dojo.comm[this._id].callMethod($0)
}else{
if(!this._callmethod)this._callmethod=[];this._callmethod.push($0)
}},_callMethodDHTML:function($0){
if(this.loaded){
return eval($0)
}else{
if(!this._callmethod)this._callmethod=[];this._callmethod.push($0)
}},_broadcastMethod:function($0){
var $1=lz.embed;var $2=[].slice.call(arguments,1);for(var $3 in $1.applications){
var $4=$1.applications[$3];if($4[$0]){
$4[$0].apply($4,$2)
}}},setCanvasAttribute:function($0,$1,$2){
lz.embed._broadcastMethod("setCanvasAttribute",$0,$1,$2)
},callMethod:function($0){
lz.embed._broadcastMethod("callMethod",$0)
},_getAppendDiv:function($0,$1){
var $2=$1?$1:$0+"Container";var $3=document.getElementById($2);if(!$3){
$3=document.createElement("div");this.__setAttr($3,"id",$2);var $4=document.body.getElementsByTagName("script");for(var $5=$4.length-1;$5>=0;--$5){
var $6=$4[$5];if(!$6.defer){
$6.parentNode.insertBefore($3,$6.nextSibling);break
}};if(!$3.parentNode){
document.body.appendChild($3)
}}else{
$3.innerHTML=""
};return $3
},_getSWFDiv:function(){
return lz.embed.dojo.obj[this._id].get()
},_sendMouseWheel:function($0){
if($0!=null)this.callMethod("lz.Keys.__mousewheelEvent("+$0+")")
},_gotFocus:function(){
setTimeout("lz.embed._broadcastMethod('_sendAllKeysUp')",1000)
},_sendAllKeysUpSWF:function(){
this.callMethod("lz.Keys.__allKeysUp()")
},_sendAllKeysUpDHTML:function(){
if(lz["Keys"]&&lz.Keys["__allKeysUp"]){
lz.Keys.__allKeysUp()
}},_sendPercLoad:function($0){
if($0<100&&this.loaded){
this.loaded=false;lz.embed.resetloaded(this._id)
};if(this.onloadstatus&&typeof this.onloadstatus=="function"){
this.onloadstatus($0)
}},attachEventHandler:function($0,$1,callbackscope,callbackname,closure){
if(!(callbackscope&&callbackname&&typeof callbackscope[callbackname]=="function")){
return
};var $2=$0+$1+callbackscope+callbackname;var $3=lz.embed._handlers;var $4=$3[$2];if($4!=null){
if($4 instanceof Array){
for(var $5=$4.length-1;$5>=0;--$5){
if($4[$5].$e===$0&&$4[$5].$c===callbackscope){
return
}}}else{
if($4.$e===$0&&$4.$c===callbackscope){
return
}}};var $6=function($0){
callbackscope[callbackname].apply(callbackscope,[$0||window.event,closure])
};$6.$e=$0;$6.$c=callbackscope;if($4!=null){
if($4 instanceof Array){
$4.push($6)
}else{
$4=[$4,$6]
}}else{
$4=$6
};$3[$2]=$4;if($0["addEventListener"]){
$0.addEventListener($1,$6,false);return true
}else if($0["attachEvent"]){
return $0.attachEvent("on"+$1,$6)
}},removeEventHandler:function($0,$1,$2,$3){
var $4=$0+$1+$2+$3;var $5=lz.embed._handlers;var $6,$7=$5[$4];if($7!=null){
if($7 instanceof Array){
for(var $8=$7.length-1;$8>=0;--$8){
if($7[$8].$e===$0&&$7[$8].$c===$2){
$6=$7[$8];$7.splice($8,1);if($7.length==0){
delete $5[$4]
}}}}else if($7.$e===$0&&$7.$c===$2){
$6=$7;delete $5[$4]
}};if(!$6){
return
};if($0["removeEventListener"]){
$0.removeEventListener($1,$6,false);return true
}else if($0["detachEvent"]){
return $0.detachEvent("on"+$1,$6)
}},_handlers:{},_cleanupHandlers:function(){
lz.embed._handlers={}},getAbsolutePosition:function($0){
var $1=null;if($0!==document.body&&$0.getBoundingClientRect){
if(!$0.parentNode){
return {x:0,y:0}};var $2=$0.ownerDocument,$3=$0.getBoundingClientRect(),$4=$2.body,$5=$2.documentElement,$6=$5.clientTop||$4.clientTop||0,$7=$5.clientLeft||$4.clientLeft||0,$8=$5.scrollTop||$4.scrollTop,$9=$5.scrollLeft||$4.scrollLeft;return {x:Math.floor($3.left+$9-$7),y:Math.floor($3.top+$8-$6)}}else if(document.getBoxObjectFor){
var $3=document.getBoxObjectFor($0),$a={x:$3.x,y:$3.y}}else{
var $a={x:$0.offsetLeft,y:$0.offsetTop};$1=$0.offsetParent;if($1!=$0){
while($1){
$a.x+=$1.offsetLeft;$a.y+=$1.offsetTop;$1=$1.offsetParent
}};var $b=lz.embed.browser;if($b.isSafari&&document.defaultView&&document.defaultView.getComputedStyle){
var $c=document.defaultView.getComputedStyle($0,"")
};if($b.isOpera||$b.isSafari){
$a.y-=document.body.offsetTop
}};if($0.parentNode){
$1=$0.parentNode
}else{
return $a
};while($1&&$1.tagName!="BODY"&&$1.tagName!="HTML"){
$a.x-=$1.scrollLeft;$a.y-=$1.scrollTop;if($1.parentNode){
$1=$1.parentNode
}else{
return $a
}};return $a
},CSSDimension:function($0,$1){
var $2=$0;if(isNaN($0)){
if($0.indexOf("%")==$0.length-1&&!isNaN($0.substring(0,$0.length-1))){
return $0
}else{
$2=0
}}else if($0===Infinity){
$2=~0>>>1
}else if($0===-Infinity){
$2=~(~0>>>1)
};return $2+($1?$1:"px")
},__setGlobalFocusTrapSWF:function($0){
var div=this._getSWFDiv();if($0){
div.onblur=function(){
div.focus()
}}else{
div.onblur=null
}},__appqueue:[],resizeWindow:function($0,$1){
if($0.indexOf("%")>-1){
$0=null
}else{
$0=parseInt($0)
};if($1.indexOf("%")>-1){
$1=null
}else{
$1=parseInt($1)
};if(window.innerHeight){
window.resizeTo($0?$0+window.outerWidth-window.innerWidth:window.outerWidth,$1?$1+window.outerHeight-window.innerHeight:window.outerHeight)
}},resetloaded:function($0){
if(lz.embed.iframemanager&&lz.embed.iframemanager.__reset){
lz.embed.iframemanager.__reset($0)
}}};lz.embed.browser.init();if(lz.embed.browser.isIE){
document.writeln('<script language="VBScript" type="text/vbscript">');document.writeln("Function VBGetSwfVer(i)");document.writeln("  on error resume next");document.writeln("  Dim swControl, swVersion");document.writeln("  swVersion = 0");document.writeln('  set swControl = CreateObject("ShockwaveFlash.ShockwaveFlash." + CStr(i))');document.writeln("  if (IsObject(swControl)) then");document.writeln('    swVersion = swControl.GetVariable("$version")');document.writeln("  end if");document.writeln("  VBGetSwfVer = swVersion");document.writeln("End Function");document.writeln("</script>");if(lz.embed.browser.version<9){
lz.embed.loadJSLib(lz.embed.getServerRoot()+"json2.js")
};lz.embed.attachEventHandler(window,"beforeunload",lz.embed,"_cleanupHandlers");lz.embed.attachEventHandler(window,"activate",lz.embed,"_gotFocus")
};lz.embed.attachEventHandler(window,"focus",lz.embed,"_gotFocus");lz.embed.__iframemanager_callbacks=[];lz.embed.iframemanager={create:function($0,$1,$2,$3,$4,$5){
var $6=lz.embed;var frames=$6.__iframemanager_callbacks;frames.push([].slice.call(arguments,0));var $7="__lz"+(frames.length-1);var $8=$6.getServerRoot()+"iframemanager.js";if(!$6.jscallbacks[$8]){
var $9=function(){
var $0=lz.embed;for(var $1=0,$2=frames.length;$1<$2;$1++){
var $3=frames[$1];var $4=$0.iframemanager.create.apply($0.iframemanager,$3)
};delete $0.__iframemanager_callbacks
};$6.loadJSLib($8,$9)
};return $7
}};lz.embed.mousewheel={__mousewheelEvent:function($0){
$0=$0||window.event;var $1=lz.embed;var $2=0;if($0.wheelDelta){
var $3=120;if($1.browser.isSafari){
$3=480
};$2=$0.wheelDelta/$3;if($1.browser.isOpera){
$2=-$2
}}else if($0.detail){
$2=-$0.detail
};if(!$2)return;if($0.preventDefault)$0.preventDefault();$0.returnValue=false;var $4=$1.mousewheel.__callbacks.length;if($2!=null&&$4>0){
for(var $5=0;$5<$4;$5+=2){
var $6=$1.mousewheel.__callbacks[$5];var $7=$1.mousewheel.__callbacks[$5+1];if($6&&$6[$7])$6[$7]($2)
}}},__callbacks:[],setCallback:function($0,$1){
var $2=lz.embed.mousewheel;if($2.__callbacks.length==0)$2.setEnabled(this.__enabled);$2.__callbacks.push($0,$1)
},__enabled:false,setEnabled:function($0){
var $1=lz.embed;if($1.mousewheel.__enabled==$0)return;$1.mousewheel.__enabled=$0;if($0&&$1.options&&$1.options.cancelkeyboardcontrol==true){
return
};var $2=$0?"attachEventHandler":"removeEventHandler";if(window.addEventListener){
$1[$2](window,"DOMMouseScroll",$1.mousewheel,"__mousewheelEvent")
};$1[$2](document,"mousewheel",$1.mousewheel,"__mousewheelEvent")
}};lz.embed.history={active:null,_currentstate:null,_apps:[],_intervalID:null,_registeredapps:{},intervaltime:200,init:function(){
var $0=lz.embed.history;if($0.active||$0.active==false)return;$0.active=true;var $1=$0.get();var $2=lz.embed.browser;if($2.isSafari&&$2.version<523.1){
$0._historylength=history.length;$0._history=[];for(var $3=1;$3<$0._historylength;$3++){
$0._history.push("")
};$0._history.push($1);var $4=document.createElement("form");$4.method="get";document.body.appendChild($4);$4.style.display="none";$0._form=$4;if(!top.document.location.lzaddr){
top.document.location.lzaddr={}};if(top.document.location.lzaddr.history){
$0._history=top.document.location.lzaddr.history.split(",")
};if($1!=""){
$0.set($1)
}}else if($2.isIE){
var $1=top.location.hash;if($1)$1=$1.substring(1);var $3=document.createElement("iframe");lz.embed.__setAttr($3,"id","lzHistory");lz.embed.__setAttr($3,"frameborder","no");lz.embed.__setAttr($3,"scrolling","no");lz.embed.__setAttr($3,"width","0");lz.embed.__setAttr($3,"height","0");lz.embed.__setAttr($3,"src",'javascript:""');document.body.appendChild($3);$3=document.getElementById("lzHistory");$0._iframe=$3;$3.style.display="none";$3.style.position="absolute";$3.style.left="-999px";var $5=$3.contentDocument||$3.contentWindow.document;$5.open();$5.close();if($1!=""){
$5.location.hash="#"+$1;$0._parse($1)
}}else{
if($1!=""){
$0._parse($1);$0._currentstate=$1
}};if($0._intervalID!=null){
clearInterval($0._intervalID)
};if($0.intervaltime>0){
$0._intervalID=setInterval("lz.embed.history._checklocationhash()",$0.intervaltime)
}},listen:function($0){
if(typeof $0=="string"){
$0=lz.embed.applications[$0];if(!$0||!$0.runtime){
return
}};if(!$0)return;var $1=lz.embed.history;if($1._registeredapps[$0._id]){
return
};$1._registeredapps[$0.id]=true;$1._apps.push($0);$1.init()
},_checklocationhash:function(){
var $0=lz.embed;if($0.dojo&&$0.dojo.info&&$0.dojo.info.installing)return;if($0.browser.isSafari&&$0.browser.version<523.1){
var $1=this._history[this._historylength-1];if($1==""||$1=="#")$1="#0";if(!this._skip&&this._historylength!=history.length){
this._historylength=history.length;if(typeof $1!="undefined"){
$1=$1.substring(1);this._currentstate=$1;this._parse($1)
}}else{
this._parse($1.substring(1))
}}else{
var $1=$0.history.get();if($1=="")$1="0";if($0.browser.isIE){
if($1!=this._currentstate){
top.location.hash=$1=="0"?"":"#"+$1;this._currentstate=$1;this._parse($1)
}}else{
this._currentstate=$1;this._parse($1)
}}},set:function($0){
var $1=lz.embed;if($1.history.active==false)return;if($0==null)$0="";if($1.history._currentstate==$0)return;$1.history._currentstate=$0;var $2="#"+$0;if($1.browser.isIE){
top.location.hash=$2=="#0"?"":$2;var $3=$1.history._iframe.contentDocument||$1.history._iframe.contentWindow.document;$3.open();$3.close();$3.location.hash=$2;$1.history._parse($0+"")
}else if($1.browser.isSafari&&$1.browser.version<523.1){
$1.history._history[history.length]=$2;$1.history._historylength=history.length+1;if($1.browser.version<412){
if(top.location.search==""){
$1.history._form.action=$2;top.document.location.lzaddr.history=$1.history._history.toString();$1.history._skip=true;$1.history._form.submit();$1.history._skip=false
}}else{
var $4=document.createEvent("MouseEvents");$4.initEvent("click",true,true);var $5=document.createElement("a");$5.href=$2;$5.dispatchEvent($4)
}}else{
top.location.hash=$2;$1.history._parse($0+"")
};return true
},get:function(){
var $0="";if(lz.embed.browser.isIE){
if(lz.embed.history._iframe){
var $1=lz.embed.history._iframe.contentDocument||lz.embed.history._iframe.contentWindow.document;$0=$1.location.hash
}}else{
$0=top.location.href
};var $2=$0.indexOf("#");if($2!=-1){
return $0.substring($2+1)
};return ""
},_parse:function($0){
var $1=lz.embed.history;if($0.length==0)return;for(var $2=0,$3=lz.embed.history._apps.length;$2<$3;$2++){
var $4=lz.embed.history._apps[$2];if(!$4.loaded||$4._lasthash==$0)continue;$4._lasthash=$0;if($0.indexOf("_lz")!=-1){
$0=$0.substring(3);var $5=$0.split(",");for(var $6=0;$6<$5.length;$6++){
var $7=$5[$6];var $8=$7.indexOf("=");var $9=unescape($7.substring(0,$8));var $a=unescape($7.substring($8+1));lz.embed.setCanvasAttribute($9,$a);if(window["canvas"])canvas.setAttribute($9,$a)
}}else{
if($4.runtime=="swf"){
$1.__setFlash($0,$4._id)
}else if(window["lz"]&&lz["History"]&&lz.History["isReady"]&&lz.History["receiveHistory"]){
lz.History.receiveHistory($0)
}}}},_store:function($0,$1){
if($0 instanceof Object){
var $2="";for(var $3 in $0){
if($2!="")$2+=",";$2+=escape($3)+"="+escape($0[$3])
}}else{
var $2=escape($0)+"="+escape($1)
};this.set("_lz"+$2)
},__setFlash:function($0,$1){
var $2=lz.embed[$1];if($2&&$2.loaded&&$2.runtime=="swf"){
var $3=$2._getSWFDiv();if($3){
var $4=$2.callMethod("lz.History.receiveHistory("+$0+")");$2._lasthash=$0
}}}};if(lz.embed.browser.isFirefox){
window.onunload=function(){}}