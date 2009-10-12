var $dhtml=true;var $as3=false;var $js1=true;var $swf7=false;var $swf8=false;var $svg=false;var $as2=false;var $swf9=false;var $profile=false;var $runtime="dhtml";var $swf10=false;var $debug=false;var $j2me=false;try{
if(lz){

}}
catch(e){
lz={}};lz.embed={options:{},swf:function($1,$2){
if($2==null){
$2=8
};var $3=$1.url;var $4=this.__getqueryurl($3);if($1.accessible&&$1.accessible!="false"){
$4.flashvars+="&accessible=true"
};if($1.history){
$4.flashvars+="&history=true"
};if($1.bgcolor!=null){
$4.flashvars+="&bgcolor="+escape($1.bgcolor)
};$4.flashvars+="&width="+escape($1.width);$4.flashvars+="&height="+escape($1.height);$4.flashvars+="&__lzurl="+escape($3);$4.flashvars+="&__lzminimumversion="+escape($2);$4.flashvars+="&id="+escape($1.id);var $3=$4.url+"?"+$4.query;var $5=lz.embed._getAppendDiv($1.id,$1.appenddivid);var $6={width:"100%",height:"100%",id:$1.id,bgcolor:$1.bgcolor,wmode:$1.wmode,flashvars:$4.flashvars,allowfullscreen:$1.allowfullscreen,flash8:$3,appenddiv:$5};if(lz.embed[$1.id]){
alert("Warning: an app with the id: "+$1.id+" already exists.")
};var app=lz.embed[$1.id]=lz.embed.applications[$1.id]={runtime:"swf",_id:$1.id,appenddiv:$5,setCanvasAttribute:lz.embed._setCanvasAttributeSWF,getCanvasAttribute:lz.embed._getCanvasAttributeSWF,callMethod:lz.embed._callMethodSWF,_ready:lz.embed._ready,_onload:[],_getSWFDiv:lz.embed._getSWFDiv,loaded:false,_sendMouseWheel:lz.embed._sendMouseWheel,_sendAllKeysUp:lz.embed._sendAllKeysUpSWF,_setCanvasAttributeDequeue:lz.embed._setCanvasAttributeDequeue,_sendPercLoad:lz.embed._sendPercLoad};if($1.history==false){
lz.embed.history.active=false
};lz.embed.dojo.addLoadedListener(lz.embed._loaded,app);lz.embed.dojo.setSwf($6,$2);$5.style.height=lz.embed.CSSDimension($1.height);$5.style.width=lz.embed.CSSDimension($1.width);if($1.cancelmousewheel!=true&&(lz.embed.browser.OS=="Mac"||($6.wmode=="transparent"||$6.wmode=="opaque")&&lz.embed.browser.OS=="Windows"&&(lz.embed.browser.isOpera||lz.embed.browser.isFirefox))){
if(lz.embed["mousewheel"]){
lz.embed.mousewheel.setCallback(app,"_sendMouseWheel")
}};if(($6.wmode=="transparent"||$6.wmode=="opaque")&&lz.embed.browser.OS=="Windows"&&(lz.embed.browser.isOpera||lz.embed.browser.isFirefox)){
var div=$6.appenddiv;div.onmouseout=function($1){
div.mouseisoutside=true
};div.onmouseover=function($1){
div.mouseisoutside=false
};div._gotmouseup=document.onmouseup=function($1){
if(div.mouseisoutside){
app.callMethod("LzMouseKernel.__mouseUpOutsideHandler()")
}}}},__swfSetAppAppendDivStyle:function($1,$2,$3){
var $4=lz.embed.applications[$1].appenddiv;return $4.style[$2]=$3
},lfc:function($1,$2){
if($2==""){
$2="."
}else{
if(!$2||typeof $2!="string"){
alert("WARNING: lz.embed.lfc() requires a valid serverroot to be specified.");return
}};lz.embed.options.serverroot=$2;if(lz.embed.browser.isIE){
var $3=$2+"lps/includes/excanvas.js";this.__dhtmlLoadScript($3)
};this.__dhtmlLoadScript($1)
},dhtml:function($1){
var $2=this.__getqueryurl($1.url,true);var $3=$2.url+"?lzt=object&"+$2.query;var $4=lz.embed._getAppendDiv($1.id,$1.appenddivid);$4.style.height=lz.embed.CSSDimension($1.height);$4.style.width=lz.embed.CSSDimension($1.width);lz.embed.__propcache={bgcolor:$1.bgcolor,width:$1.width,height:$1.height,id:$1.id,appenddiv:lz.embed._getAppendDiv($1.id,$1.appenddivid),url:$3,cancelkeyboardcontrol:$1.cancelkeyboardcontrol,serverroot:$1.serverroot,approot:$1.approot!=null?$1.approot:""};if(lz.embed[$1.id]){
alert("Warning: an app with the id: "+$1.id+" already exists.")
};var $5=lz.embed[$1.id]=lz.embed.applications[$1.id]={runtime:"dhtml",_id:$1.id,_ready:lz.embed._ready,_onload:[],loaded:false,setCanvasAttribute:lz.embed._setCanvasAttributeDHTML,getCanvasAttribute:lz.embed._getCanvasAttributeDHTML,_sendAllKeysUp:lz.embed._sendAllKeysUpDHTML};if($1.history==false){
lz.embed.history.active=false
};this.__dhtmlLoadScript($3);if(lz.embed.browser.OS=="Windows"&&lz.embed.browser.isFirefox){
window.focus()
}},applications:{},__dhtmlLoadScript:function($1){
var $2='<script type="text/javascript" language="JavaScript1.5" src="'+$1+'"></script>';document.writeln($2);return $2
},__dhtmlLoadLibrary:function($1){
var $2=document.createElement("script");this.__setAttr($2,"type","text/javascript");this.__setAttr($2,"src",$1);document.getElementsByTagName("head")[0].appendChild($2);return $2
},__getqueryurl:function($1,$2){
var $3=$1.split("?");$1=$3[0];if($3.length==1){
return {url:$1,flashvars:"",query:""}};var $4=this.__parseQuery($3[1]);var $5="";var $6="";var $7=new RegExp("\\+","g");for(var $8 in $4){
if($8==""||$8==null){
continue
};var $9=$4[$8];if($8=="lzr"||$8=="lzt"||$8=="debug"||$8=="profile"||$8=="lzbacktrace"||$8=="lzconsoledebug"||$8=="lzdebug"||$8=="lzkrank"||$8=="lzprofile"||$8=="lzcopyresources"||$8=="fb"||$8=="sourcelocators"||$8=="_canvas_debug"||$8=="lzsourceannotations"){
$5+=$8+"="+$9+"&"
};if($2){
if(window[$8]==null){
window[$8]=unescape($9.replace($7," "))
}};$6+=$8+"="+$9+"&"
};$5=$5.substr(0,$5.length-1);$6=$6.substr(0,$6.length-1);return {url:$1,flashvars:$6,query:$5}},__parseQuery:function($1){
if($1.indexOf("=")==-1){
return
};var $2=$1.split("&");var $3={};for(var $4=0;$4<$2.length;$4++){
var $5=$2[$4].split("=");if($5.length==1){
continue
};var $6=$5[0];var $7=$5[1];$3[$6]=$7
};return $3
},__setAttr:function($1,$2,$3){
$1.setAttribute($2,$3)
},_setCanvasAttributeSWF:function($1,$2,$3){
if(this.loaded&&lz.embed.dojo.comm[this._id]&&lz.embed.dojo.comm[this._id]["callMethod"]){
if($3){
lz.embed.history._store($1,$2)
}else{
lz.embed.dojo.comm[this._id].setCanvasAttribute($1,$2+"")
}}else{
if(this._setCanvasAttributeQ==null){
this._setCanvasAttributeQ=[[$1,$2,$3]]
}else{
this._setCanvasAttributeQ.push([$1,$2,$3])
}}},_setCanvasAttributeDHTML:function($1,$2,$3){
if($3){
lz.embed.history._store($1,$2)
}else{
if(canvas){
if(!canvas.__LZdeleted){
var $lzsc$1329266606="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(canvas[$lzsc$1329266606]):canvas[$lzsc$1329266606] instanceof Function){
canvas[$lzsc$1329266606]($2)
}else{
canvas[$1]=$2;var $lzsc$825273665=canvas["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$825273665):$lzsc$825273665 instanceof LzEvent){
if($lzsc$825273665.ready){
$lzsc$825273665.sendEvent($2)
}}}}}}},_loaded:function($1){
if(lz.embed[$1].loaded){
return
};if(lz.embed.dojo.info.commVersion==8){
setTimeout('lz.embed["'+$1+'"]._ready.call(lz.embed["'+$1+'"])',100)
}else{
lz.embed[$1]._ready.call(lz.embed[$1])
}},_setCanvasAttributeDequeue:function(){
while(this._setCanvasAttributeQ.length>0){
var $1=this._setCanvasAttributeQ.pop();this.setCanvasAttribute($1[0],$1[1],$1[2])
}},_ready:function($1){
this.loaded=true;if(this._callmethod){
for(var $2=0;$2<this._callmethod.length;$2++){
this.callMethod(this._callmethod[$2])
};this._callmethod=null
};if(this._setCanvasAttributeQ){
this._setCanvasAttributeDequeue()
};if($1){
this.canvas=$1
};for(var $2=0;$2<this._onload.length;$2++){
var $3=this._onload[$2];if(typeof $3=="function"){
$3(this)
}};if(this.onload&&typeof this.onload=="function"){
this.onload(this)
}},_getCanvasAttributeSWF:function($1){
if(this.loaded){
return lz.embed.dojo.comm[this._id].getCanvasAttribute($1)
}else{
alert("Flash is not ready: getCanvasAttribute"+$1)
}},_getCanvasAttributeDHTML:function($1){
return canvas[$1]
},browser:{init:function(){
if(this.initted){
return
};this.browser=this.searchString(this.dataBrowser)||"An unknown browser";this.version=this.searchVersion(navigator.userAgent)||this.searchVersion(navigator.appVersion)||"an unknown version";this.osversion=this.searchOSVersion(navigator.userAgent)||"an unknown osversion";this.subversion=this.searchSubVersion(navigator.userAgent);this.OS=this.searchString(this.dataOS)||"an unknown OS";this.initted=true;this.isNetscape=this.isSafari=this.isOpera=this.isFirefox=this.isIE=this.isIphone=false;if(this.browser=="Netscape"){
this.isNetscape=true
}else{
if(this.browser=="Safari"){
this.isSafari=true
}else{
if(this.browser=="Opera"){
this.isOpera=true
}else{
if(this.browser=="Firefox"){
this.isFirefox=true
}else{
if(this.browser=="Explorer"){
this.isIE=true
}else{
if(this.browser=="iPhone"){
this.isSafari=true;this.isIphone=true
}}}}}}},searchString:function($1){
for(var $2=0;$2<$1.length;$2++){
var $3=$1[$2].string;var $4=$1[$2].prop;this.versionSearchString=$1[$2].versionSearch||$1[$2].identity;this.osversionSearchString=$1[$2].osversionSearch||"";if($3){
if($3.indexOf($1[$2].subString)!=-1){
return $1[$2].identity
}}else{
if($4){
return $1[$2].identity
}}}},searchVersion:function($1){
var $2=$1.indexOf(this.versionSearchString);if($2==-1){
return
};return parseFloat($1.substring($2+this.versionSearchString.length+1))
},searchSubVersion:function($1){
var $2=new RegExp(this.versionSearchString+".\\d+\\.\\d+\\.([\\d.]+)");var $3=$2.exec($1);if($3&&$3.length>1){
return parseFloat($3[1])
}},searchOSVersion:function($1){
var $2=$1.indexOf(this.osversionSearchString);if($2==-1){
return
};return parseFloat($1.substring($2+this.osversionSearchString.length+1))
},dataBrowser:[{string:navigator.userAgent,subString:"iPhone",identity:"iPhone",versionSearch:"WebKit"},{string:navigator.userAgent,subString:"Chrome",identity:"Chrome"},{string:navigator.userAgent,subString:"OmniWeb",versionSearch:"OmniWeb/",identity:"OmniWeb"},{string:navigator.vendor,subString:"Apple",identity:"Safari",versionSearch:"WebKit"},{prop:window.opera,identity:"Opera"},{string:navigator.vendor,subString:"iCab",identity:"iCab"},{string:navigator.vendor,subString:"KDE",identity:"Konqueror"},{string:navigator.userAgent,subString:"Firefox",identity:"Firefox"},{string:navigator.userAgent,subString:"Iceweasel",versionSearch:"Iceweasel",identity:"Firefox"},{string:navigator.vendor,subString:"Camino",identity:"Camino"},{string:navigator.userAgent,subString:"Netscape",identity:"Netscape"},{string:navigator.userAgent,subString:"MSIE",identity:"Explorer",versionSearch:"MSIE",osversionSearch:"Windows NT"},{string:navigator.userAgent,subString:"Gecko",identity:"Mozilla",versionSearch:"rv"},{string:navigator.userAgent,subString:"Mozilla",identity:"Netscape",versionSearch:"Mozilla"}],dataOS:[{string:navigator.platform,subString:"Win",identity:"Windows"},{string:navigator.platform,subString:"Mac",identity:"Mac"},{string:navigator.userAgent,subString:"iPhone",identity:"iPhone/iPod"},{string:navigator.platform,subString:"Linux",identity:"Linux"}]},_callMethodSWF:function($1){
if(this.loaded){
return lz.embed.dojo.comm[this._id].callMethod($1)
}else{
if(!this._callmethod){
this._callmethod=[]
};this._callmethod.push($1)
}},_broadcastMethod:function($1){
var $2=[].slice.call(arguments,1);for(var $3 in lz.embed.applications){
var $4=lz.embed.applications[$3];if($4[$1]){
$4[$1].apply($4,$2)
}}},setCanvasAttribute:function($1,$2,$3){
lz.embed._broadcastMethod("setCanvasAttribute",$1,$2,$3)
},callMethod:function($1){
lz.embed._broadcastMethod("callMethod",$1)
},_getAppendDiv:function($1,$2){
var $3=$2?$2:$1+"Container";var $4=document.getElementById($3);if(!$4){
document.writeln('<div id="'+$3+'"></div>');$4=document.getElementById($3)
};return $4
},_getSWFDiv:function(){
return lz.embed.dojo.obj[this._id].get()
},_sendMouseWheel:function($1){
if($1!=null){
this.callMethod("lz.Keys.__mousewheelEvent("+$1+")")
}},_gotFocus:function(){
lz.embed._broadcastMethod("_sendAllKeysUp")
},_sendAllKeysUpSWF:function(){
this.callMethod("lz.Keys.__allKeysUp()")
},_sendAllKeysUpDHTML:function(){
if(lz["Keys"]&&lz.Keys["__allKeysUp"]){
lz.Keys.__allKeysUp()
}},_sendPercLoad:function($1){
if(this.onloadstatus&&typeof this.onloadstatus=="function"){
this.onloadstatus($1)
}},attachEventHandler:function($1,$2,callbackscope,callbackname,closure){
if(!(callbackscope&&callbackname&&typeof callbackscope[callbackname]=="function")){
return
};var $3=$1+$2+callbackscope+callbackname;var $4=this._handlers[$3];if($4!=null){
if($4 instanceof Array){
for(var $5=$4.length-1;$5>=0;--$5){
if($4[$5].$e===$1&&$4[$5].$c===callbackscope){
return
}}}else{
if($4.$e===$1&&$4.$c===callbackscope){
return
}}};var $6=function(){
var $1=window.event?[window.event]:[].slice.call(arguments,0);if(closure){
$1.push(closure)
};callbackscope[callbackname].apply(callbackscope,$1)
};$6.$e=$1;$6.$c=callbackscope;if($4!=null){
if($4 instanceof Array){
$4.push($6)
}else{
$4=[$4,$6]
}}else{
$4=$6
};this._handlers[$3]=$4;if($1["addEventListener"]){
$1.addEventListener($2,$6,false);return true
}else{
if($1["attachEvent"]){
return $1.attachEvent("on"+$2,$6)
}}},removeEventHandler:function($1,$2,$3,$4){
var $5=$1+$2+$3+$4;var $6,$7=this._handlers[$5];if($7!=null){
if($7 instanceof Array){
for(var $8=$7.length-1;$8>=0;--$8){
if($7[$8].$e===$1&&$7[$8].$c===$3){
$6=$7[$8];$7.splice($8,1);if($7.length==0){
delete this._handlers[$5]
}}}}else{
if($7.$e===$1&&$7.$c===$3){
$6=$7;delete this._handlers[$5]
}}};if(!$6){
return
};if($1["removeEventListener"]){
$1.removeEventListener($2,$6,false);return true
}else{
if($1["detachEvent"]){
return $1.detachEvent("on"+$2,$6)
}}},_handlers:{},_cleanupHandlers:function(){
lz.embed._handlers={}},getAbsolutePosition:function($1){
var $2=null;var $3={};var $4;if(!(lz.embed.browser.isFirefox&&$1==document.body)&&$1.getBoundingClientRect){
$4=$1.getBoundingClientRect();var $5=document.documentElement.scrollTop||document.body.scrollTop;var $6=document.documentElement.scrollLeft||document.body.scrollLeft;return {x:Math.floor($4.left+$6),y:Math.floor($4.top+$5)}}else{
if(document.getBoxObjectFor){
$4=document.getBoxObjectFor($1);$3={x:$4.x,y:$4.y}}else{
$3={x:$1.offsetLeft,y:$1.offsetTop};$2=$1.offsetParent;if($2!=$1){
while($2){
$3.x+=$2.offsetLeft;$3.y+=$2.offsetTop;$2=$2.offsetParent
}};if(lz.embed.browser.isSafari&&document.defaultView&&document.defaultView.getComputedStyle){
var $7=document.defaultView.getComputedStyle($1,"")
};if(lz.embed.browser.isOpera||lz.embed.browser.isSafari&&$7&&$7["position"]=="absolute"){
$3.y-=document.body.offsetTop
}}};if($1.parentNode){
$2=$1.parentNode
}else{
return $3
};while($2&&$2.tagName!="BODY"&&$2.tagName!="HTML"){
$3.x-=$2.scrollLeft;$3.y-=$2.scrollTop;if($2.parentNode){
$2=$2.parentNode
}else{
return $3
}};return $3
},CSSDimension:function($1,$2){
var $3=$1;if(isNaN($1)){
if($1.indexOf("%")==$1.length-1&&!isNaN($1.substring(0,$1.length-1))){
return $1
}else{
$3=0
}}else{
if($1===Infinity){
$3=~0>>>1
}else{
if($1===-Infinity){
$3=~(~0>>>1)
}}};return $3+($2?$2:"px")
}};lz.embed.browser.init();lz.embed.attachEventHandler(window,"beforeunload",lz.embed,"_cleanupHandlers");lz.embed.attachEventHandler(window,"focus",lz.embed,"_gotFocus");if(lz.embed.browser.isIE){
lz.embed.attachEventHandler(window,"activate",lz.embed,"_gotFocus")
};try{
if(lzOptions){
if(lzOptions.dhtmlKeyboardControl){
alert("WARNING: this page uses lzOptions.dhtmlKeyboardControl.  Please use the cancelkeyboardcontrol embed argument for lz.embed.dhtml() instead.")
};if(lzOptions.ServerRoot){
alert("WARNING: this page uses lzOptions.ServerRoot.  Please use the second argument of lz.embed.lfc() instead.")
}}}
catch(e){

};lz.embed.dojo=function(){

};lz.embed.dojo={defaults:{flash8:null,ready:false,visible:true,width:500,height:400,bgcolor:"#ffffff",allowfullscreen:false,wmode:"window",flashvars:"",minimumVersion:8,id:"flashObject",appenddiv:null},obj:{},comm:{},_loadedListeners:[],_loadedListenerScopes:[],_installingListeners:[],_installingListenerScopes:[],setSwf:function($1,$2){
if($1==null){
return
};var $3={};for(var $4 in this.defaults){
var $5=$1[$4];if($5!=null){
$3[$4]=$5
}else{
$3[$4]=this.defaults[$4]
}};if($2!=null){
this.minimumVersion=$2
};this._initialize($3)
},addLoadedListener:function($1,$2){
this._loadedListeners.push($1);this._loadedListenerScopes.push($2)
},addInstallingListener:function($1,$2){
this._installingListeners.push($1);this._installingListenerScopes.push($2)
},loaded:function($1){
var $2=lz.embed.dojo;if($2._isinstaller){
top.location=top.location+""
};$2.info.installing=false;$2.ready=true;if($2._loadedListeners.length>0){
for(var $3=0;$3<$2._loadedListeners.length;$3++){
var $4=$2._loadedListenerScopes[$3];if($1!=$4._id){
continue
};lz.embed.dojo._loadedListeners[$3].apply($4,[$4._id])
}}},installing:function(){
var $1=lz.embed.dojo;if($1._installingListeners.length>0){
for(var $2=0;$2<$1._installingListeners.length;$2++){
var $3=$1._installingListenerScopes[$2];$1._installingListeners[$2].apply($3,[$3._id])
}}},_initialize:function($1){
var $2=lz.embed.dojo;var $3=new ($2.Install)($1.id);$2.installer=$3;var $4=new ($2.Embed)($1);$2.obj[$1.id]=$4;if($3.needed()==true){
$3.install()
}else{
$4.write($2.info.commVersion);$2.comm[$1.id]=new ($2.Communicator)($1.id)
}}};lz.embed.dojo.Info=function(){
if(lz.embed.browser.isIE){
document.writeln('<script language="VBScript" type="text/vbscript">');document.writeln("Function VBGetSwfVer(i)");document.writeln("  on error resume next");document.writeln("  Dim swControl, swVersion");document.writeln("  swVersion = 0");document.writeln('  set swControl = CreateObject("ShockwaveFlash.ShockwaveFlash." + CStr(i))');document.writeln("  if (IsObject(swControl)) then");document.writeln('    swVersion = swControl.GetVariable("$version")');document.writeln("  end if");document.writeln("  VBGetSwfVer = swVersion");document.writeln("End Function");document.writeln("</script>")
};this._detectVersion();this._detectCommunicationVersion()
};lz.embed.dojo.Info.prototype={version:-1,versionMajor:-1,versionMinor:-1,versionRevision:-1,capable:false,commVersion:8,installing:false,isVersionOrAbove:function($1,$2,$3){
$3=parseFloat("."+$3);if(this.versionMajor>=$1&&this.versionMinor>=$2&&this.versionRevision>=$3){
return true
}else{
return false
}},_detectVersion:function(){
var $1;var $2=lz.embed.browser.isIE;for(var $3=25;$3>0;$3--){
if($2){
$1=VBGetSwfVer($3)
}else{
$1=this._JSFlashInfo($3)
};if($1==-1){
this.capable=false;return
}else{
if($1!=0){
var $4;if($2){
var $5=$1.split(" ");var $6=$5[1];$4=$6.split(",")
}else{
$4=$1.split(".")
};this.versionMajor=$4[0];this.versionMinor=$4[1];this.versionRevision=$4[2];var $7=this.versionMajor+"."+this.versionRevision;this.version=parseFloat($7);this.capable=true;break
}}}},_JSFlashInfo:function($1){
if(navigator.plugins!=null&&navigator.plugins.length>0){
if(navigator.plugins["Shockwave Flash 2.0"]||navigator.plugins["Shockwave Flash"]){
var $2=navigator.plugins["Shockwave Flash 2.0"]?" 2.0":"";var $3=navigator.plugins["Shockwave Flash"+$2].description;var $4=$3.split(" ");var $5=$4[2].split(".");var $6=$5[0];var $7=$5[1];if($4[3]!=""){
var $8=$4[3].split("r")
}else{
var $8=$4[4].split("r")
};var $9=$8[1]>0?$8[1]:0;var $10=$6+"."+$7+"."+$9;return $10
}};return -1
},_detectCommunicationVersion:function(){
if(this.capable==false){
this.commVersion=null;return
}}};lz.embed.dojo.Embed=function($1){
this.properties=$1;if(!this.properties.width){
this.properties.width="100%"
};if(!this.properties.height){
this.properties.height="100%"
};if(!this.properties.bgcolor){
this.properties.bgcolor="#ffffff"
};if(!this.properties.visible){
this.properties.visible=true
}};lz.embed.dojo.Embed.prototype={protocol:function(){
switch(window.location.protocol){
case "https:":
return "https";break;
default:
return "http";break;

}},__getCSSValue:function($1){
if($1&&$1.length&&$1.indexOf("%")!=-1){
return "100%"
}else{
return $1+"px"
}},write:function($1,$2){
var $3="";$3+="width: "+this.__getCSSValue(this.properties.width)+";";$3+="height: "+this.__getCSSValue(this.properties.height)+";";if(this.properties.visible==false){
$3+="position: absolute; ";$3+="z-index: 10000; ";$3+="top: -1000px; ";$3+="left: -1000px; "
};var $4;var $5;var $6=lz.embed;if($1>$6.dojo.version){
$2=true
};$5=this.properties.flash8;var $7=this.properties.flashvars;var $8=this.properties.flashvars;if($2){
var $9=escape(window.location);document.title=document.title.slice(0,47)+" - Flash Player Installation";var $10=escape(document.title);$7+="&MMredirectURL="+$9+"&MMplayerType=ActiveX"+"&MMdoctitle="+$10;$8+="&MMredirectURL="+$9+"&MMplayerType=PlugIn"
};if($6.browser.isIE){
$4='<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" '+'codebase="'+this.protocol()+"://fpdownload.macromedia.com/pub/shockwave/cabs/flash/"+'swflash.cab#version=8,0,0,0" '+'width="'+this.properties.width+'" '+'height="'+this.properties.height+'" '+'id="'+this.properties.id+'" '+'align="middle"> '+'<param name="allowScriptAccess" value="sameDomain" /> '+'<param name="movie" value="'+$5+'" /> '+'<param name="quality" value="high" /> '+'<param name="FlashVars" value="'+$7+'" /> '+'<param name="bgcolor" value="'+this.properties.bgcolor+'" /> '+'<param name="wmode" value="'+this.properties.wmode+'" /> '+'<param name="allowFullScreen" value="'+this.properties.allowfullscreen+'" /> '+"</object>"
}else{
$4='<embed src="'+$5+'" '+'quality="high" '+'bgcolor="'+this.properties.bgcolor+'" '+'wmode="'+this.properties.wmode+'" '+'allowFullScreen="'+this.properties.allowfullscreen+'" '+'width="'+this.properties.width+'" '+'height="'+this.properties.height+'" '+'id="'+this.properties.id+'" '+'name="'+this.properties.id+'" '+'FlashVars="'+$8+'" '+'swLiveConnect="true" '+'align="middle" '+'allowScriptAccess="sameDomain" '+'type="application/x-shockwave-flash" '+'pluginspage="'+this.protocol()+'://www.macromedia.com/go/getflashplayer" />'
};var $11=this.properties.id+"Container";var $12=this.properties.appenddiv;if($12){
$12.innerHTML=$4;$12.setAttribute("style",$3)
}else{
$4='<div id="'+$11+'" style="'+$3+'"> '+$4+"</div>";document.writeln($4)
}},get:function(){
try{
var $1=document.getElementById(this.properties.id+"")
}
catch(e){

};return $1
},setVisible:function($1){
var $2=document.getElementById(this.properties.id+"Container");if($1==true){
$2.style.visibility="visible"
}else{
$2.style.position="absolute";$2.style.x="-1000px";$2.style.y="-1000px";$2.style.visibility="hidden"
}},center:function(){
var $1=this.properties.width;var $2=this.properties.height;var $3=0;var $4=0;var $5=document.getElementById(this.properties.id+"Container");$5.style.top=$4+"px";$5.style.left=$3+"px"
}};lz.embed.dojo.Communicator=function($1){
this._id=$1
};lz.embed.dojo.Communicator.prototype={_addExternalInterfaceCallback:function(methodName,id){
var dojo=lz.embed.dojo;var $1=function(){
var $1=[];for(var $2=0;$2<arguments.length;$2++){
$1[$2]=arguments[$2]
};$1.length=arguments.length;return dojo.comm[id]._execFlash(methodName,$1,id)
};dojo.comm[id][methodName]=$1
},_encodeData:function($1){
var $2=RegExp("\\&([^;]*)\\;","g");$1=$1.replace($2,"&amp;$1;");$1=$1.replace(RegExp("<","g"),"&lt;");$1=$1.replace(RegExp(">","g"),"&gt;");$1=$1.replace("\\","&custom_backslash;&custom_backslash;");$1=$1.replace(RegExp("\\n","g"),"\\n");$1=$1.replace(RegExp("\\r","g"),"\\r");$1=$1.replace(RegExp("\\f","g"),"\\f");$1=$1.replace(RegExp("\\0","g"),"\\0");$1=$1.replace(RegExp("\\'","g"),"\\'");$1=$1.replace(RegExp('\\"',"g"),'\\"');return $1
},_decodeData:function($1){
if($1==null||typeof $1=="undefined"){
return $1
};$1=$1.replace(RegExp("\\&custom_lt\\;","g"),"<");$1=$1.replace(RegExp("\\&custom_gt\\;","g"),">");$1=eval('"'+$1+'"');return $1
},_chunkArgumentData:function($1,$2,$3){
var $4=lz.embed.dojo.obj[$3].get();var $5=Math.ceil($1.length/1024);for(var $6=0;$6<$5;$6++){
var $7=$6*1024;var $8=$6*1024+1024;if($6==$5-1){
$8=$6*1024+$1.length
};var $9=$1.substring($7,$8);$9=this._encodeData($9);$4.CallFunction('<invoke name="chunkArgumentData" '+'returntype="javascript">'+"<arguments>"+"<string>"+$9+"</string>"+"<number>"+$2+"</number>"+"</arguments>"+"</invoke>")
}},_chunkReturnData:function($1){
var $2=lz.embed.dojo.obj[$1].get();var $3=$2.getReturnLength();var $4=[];for(var $5=0;$5<$3;$5++){
var $6=$2.CallFunction('<invoke name="chunkReturnData" '+'returntype="javascript">'+"<arguments>"+"<number>"+$5+"</number>"+"</arguments>"+"</invoke>");if($6=='""'||$6=="''"){
$6=""
}else{
$6=$6.substring(1,$6.length-1)
};$4.push($6)
};var $7=$4.join("");return $7
},_execFlash:function($1,$2,$3){
var $4=lz.embed.dojo.obj[$3].get();$4.startExec();$4.setNumberArguments($2.length);for(var $5=0;$5<$2.length;$5++){
this._chunkArgumentData($2[$5],$5,$3)
};$4.exec($1);var $6=this._chunkReturnData($3);$6=this._decodeData($6);$4.endExec();return $6
}};lz.embed.dojo.Install=function($1){
this._id=$1
};lz.embed.dojo.Install.prototype={needed:function(){
var $1=lz.embed.dojo;if($1.info.capable==false){
return true
};if(lz.embed.browser.isSafari==true&&!$1.info.isVersionOrAbove(8,0,0)){
return true
};if($1.minimumVersion>$1.info.versionMajor){
return true
};if(!$1.info.isVersionOrAbove(8,0,0)){
return true
};return false
},install:function(){
var $1=lz.embed.dojo;$1.info.installing=true;$1.installing();var $2=$1.obj[this._id].properties;var $3=$2.flash8;var $4=$3.indexOf("swf7");if($4!=-1){
$1._tempurl=$3;$3=$3.substring(0,$4+3)+"8"+$3.substring($4+4,$3.length);$2.flash8=$3
};var $4=$3.indexOf("swf9");if($4!=-1){
$1._tempurl=$3;$3=$3.substring(0,$4+3)+"8"+$3.substring($4+4,$3.length);$2.flash8=$3
};$1.ready=false;if($1.info.capable==false){
$1._isinstaller=true;var $5=new ($1.Embed)($2);$5.write($1.minimumVersion)
}else{
if($1.info.isVersionOrAbove(6,0,65)){
var $5=new ($1.Embed)($2);$5.write($1.minimumVersion,true);$5.setVisible(true);$5.center()
}else{
alert("This content requires a more recent version of the Macromedia "+" Flash Player.");window.location="http://www.macromedia.com/go/getflashplayer"
}}},_onInstallStatus:function($1){
if($1=="Download.Complete"){
if(lz.embed.browser.isIE){
top.location=top.location+""
}}else{
if($1=="Download.Cancelled"){
alert("This content requires a more recent version of the Macromedia "+" Flash Player.");window.location="http://www.macromedia.com/go/getflashplayer"
}else{
if($1=="Download.Failed"){
alert("There was an error downloading the Flash Player update. "+"Please try again later, or visit macromedia.com to download "+"the latest version of the Flash plugin.");window.location="http://www.macromedia.com/go/getflashplayer"
}}}}};lz.embed.dojo.info=new (lz.embed.dojo.Info)();lz.embed.iframemanager={__counter:0,__frames:{},__namebyid:{},__loading:{},__callqueue:{},__calljsqueue:{},__sendmouseevents:{},__hidenativecontextmenu:{},__selectionbookmarks:{},create:function($1,$2,$3,$4,$5,$6){
var $7="__lz"+lz.embed.iframemanager.__counter++;var $8='javascript:""';var $9='lz.embed.iframemanager.__gotload("'+$7+'")';if($2==null||$2=="null"||$2==""){
$2=$7
};lz.embed.iframemanager.__namebyid[$7]=$2;if($4==null||$4=="undefined"){
$4=document.body
};if(document.all){
var $10="<iframe name='"+$2+"' id='"+$7+"' src='"+$8+"' onload='"+$9+"' frameBorder='0'";if($3!=true){
$10+=" scrolling='no'"
};$10+="></iframe>";var $11=document.createElement("div");lz.embed.__setAttr($11,"id",$7+"Container");$4.appendChild($11);$11.innerHTML=$10;var $12=document.getElementById($7)
}else{
var $12=document.createElement("iframe");lz.embed.__setAttr($12,"name",$2);lz.embed.__setAttr($12,"src",$8);lz.embed.__setAttr($12,"id",$7);lz.embed.__setAttr($12,"onload",$9);if($3!=true){
lz.embed.__setAttr($12,"scrolling","no")
};this.appendTo($12,$4)
};if($12){
this.__finishCreate($7,$1,$2,$3,$4,$5,$6)
}else{
this.__callqueue[$7]=[["__finishCreate",$7,$1,$2,$3,$4,$5,$6]];setTimeout('lz.embed.iframemanager.__checkiframe("'+$7+'")',10)
};return $7+""
},__checkiframe:function($1){
var $2=document.getElementById($1);if($2){
var $3=lz.embed.iframemanager.__callqueue[$1];delete lz.embed.iframemanager.__callqueue[$1];lz.embed.iframemanager.__playQueue($3)
}else{
setTimeout('lz.embed.iframemanager.__checkiframe("'+$1+'")',10)
}},__playQueue:function($1){
var $2=lz.embed.iframemanager;for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3];var $5=$4.splice(0,1);$2[$5].apply($2,$4)
}},__finishCreate:function($1,$2,$3,$4,$5,$6,$7){
var $8=document.getElementById($1);if(typeof $2=="string"){
$8.appcontainer=lz.embed.applications[$2]._getSWFDiv()
};$8.owner=$2;lz.embed.iframemanager.__frames[$1]=$8;this.__namebyid[$1]=$3;var $9=lz.embed.iframemanager.getFrame($1);$9.__gotload=lz.embed.iframemanager.__gotload;$9._defaultz=$6?$6:99900;this.setZ($1,$9._defaultz);lz.embed.iframemanager.__topiframe=$1;if(document.getElementById&&!document.all){
$9.style.border="0"
}else{
if(document.all){
lz.embed.__setAttr($9,"allowtransparency","true");var $10=lz.embed[$9.owner];if($10&&$10.runtime=="swf"){
var $11=$10._getSWFDiv();$11.onfocus=lz.embed.iframemanager.__refresh
}}};$9.style.position="absolute"
},appendTo:function($1,$2){
if($2.__appended==$2){
return
};if($1.__appended){
old=$1.__appended.removeChild($1);$2.appendChild(old)
}else{
$2.appendChild($1)
};$1.__appended=$2
},getFrame:function($1){
return lz.embed.iframemanager.__frames[$1]
},getFrameWindow:function($1){
if(!this["framesColl"]){
if(document.frames){
this.framesColl=document.frames
}else{
this.framesColl=window.frames
}};return this.framesColl[$1]
},setSrc:function($1,$2,$3){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["setSrc",$1,$2,$3]);return
};this.__setSendMouseEvents($1,false);if($3){
var $4=lz.embed.iframemanager.getFrame($1);if(!$4){
return
};lz.embed.__setAttr($4,"src",$2)
}else{
var $1=lz.embed.iframemanager.__namebyid[$1];var $4=window[$1];if(!$4){
return
};$4.location.replace($2)
};this.__loading[$1]=true
},setPosition:function($1,$2,$3,$4,$5,$6,$7){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["setPosition",$1,$2,$3,$4,$5,$6,$7]);return
};var $8=lz.embed.iframemanager.getFrame($1);if(!$8){
return
};if($8.appcontainer){
var $9=lz.embed.getAbsolutePosition($8.appcontainer)
}else{
var $9={x:0,y:0}};if($2!=null&&!isNaN($2)){
$8.style.left=$2+$9.x+"px"
};if($3!=null&&!isNaN($3)){
$8.style.top=$3+$9.y+"px"
};if($4!=null&&!isNaN($4)){
$8.style.width=$4+"px"
};if($5!=null&&!isNaN($5)){
$8.style.height=$5+"px"
};if($6!=null){
if(typeof $6=="string"){
$6=$6=="true"
};$8.style.display=$6?"block":"none"
};if($7!=null){
this.setZ($1,$7+$8._defaultz)
}},setVisible:function($1,$2){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["setVisible",$1,$2]);return
};if(typeof $2=="string"){
$2=$2=="true"
};var $3=lz.embed.iframemanager.getFrame($1);if(!$3){
return
};$3.style.display=$2?"block":"none"
},bringToFront:function($1){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["bringToFront",$1]);return
};var $2=lz.embed.iframemanager.getFrame($1);if(!$2){
return
};$2._defaultz=100000;this.setZ($1,$2._defaultz);lz.embed.iframemanager.__topiframe=$1
},sendToBack:function($1){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["sendToBack",$1]);return
};var $2=lz.embed.iframemanager.getFrame($1);if(!$2){
return
};$2._defaultz=99900;this.setZ($1,$2._defaultz)
},__gotload:function($1){
var $2=lz.embed.iframemanager.getFrame($1);if(!$2){
return
};if($2.owner&&$2.owner.__gotload){
$2.owner.__gotload()
}else{
lz.embed[$2.owner].callMethod("lz.embed.iframemanager.__gotload('"+$1+"')")
};this.__loading[$1]=false;if(this.__sendmouseevents[$1]){
this.__setSendMouseEvents($1,true)
};if(this.__calljsqueue[$1]){
this.__playQueue(this.__calljsqueue[$1]);delete this.__calljsqueue[$1]
}},__refresh:function(){
if(lz.embed.iframemanager.__topiframe){
var $1=lz.embed.iframemanager.getFrame(lz.embed.iframemanager.__topiframe);if($1.style.display=="block"){
$1.style.display="none";$1.style.display="block"
}}},setZ:function($1,$2){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["setZ",$1,$2]);return
};var $3=lz.embed.iframemanager.getFrame($1);if(!$3){
return
};$3.style.zIndex=$2
},scrollBy:function($1,$2,$3){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["scrollBy",$1,$2,$3]);return
};var $1=lz.embed.iframemanager.__namebyid[$1];var $4=window.frames[$1];if(!$4){
return
};$4.scrollBy($2,$3)
},__destroy:function($1){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["__destroy",$1]);return
};var $2=lz.embed.iframemanager.__frames[$1];if($2){
this.__setSendMouseEvents($1,false);$2.owner=null;$2.appcontainer=null;LzSprite.prototype.__discardElement($2);delete lz.embed.iframemanager.__frames[$1];delete lz.embed.iframemanager.__namebyid[$1]
}},callJavascript:function($1,$2,$3,$4){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["callJavascript",$1,$2,$3,$4]);return
};if(this.__loading[$1]){
if(!this.__calljsqueue[$1]){
this.__calljsqueue[$1]=[]
};this.__calljsqueue[$1].push(["callJavascript",$1,$2,$3,$4]);return
};var $5=lz.embed.iframemanager.getFrameWindow($1);if(!$4){
$4=[]
};try{
var $6=$5.eval($2);if($6){
var $7=$6.apply($5,$4);if($3){
$3.execute($7)
};return $7
}}
catch(e){

}},__globalMouseover:function($1,$2){
var $3=lz.embed.iframemanager.getFrame($2);if(!$3){
return
};$1=window.event;if($3.owner&&$3.owner.sprite){
if($1.toElement&&$1.toElement.nodeName!="IFRAME"){
LzMouseKernel.__resetMouse()
}}},__mouseEvent:function($1,$2){
var $3=lz.embed;var $4=$3.iframemanager.getFrame($2);if(!$4){
return
};if(!$1){
$1=window.event
};var $5="on"+$1.type;if($4.owner&&$4.owner.sprite&&$4.owner.sprite.__mouseEvent){
if($5=="oncontextmenu"){
if(!$3.iframemanager.__hidenativecontextmenu[$2]){
return
}else{
var $6=$3.getAbsolutePosition($4);LzMouseKernel.__sendMouseMove($1,$6.x,$6.y);return LzMouseKernel.__showContextMenu($1)
}};$4.owner.sprite.__mouseEvent($1);if($5=="onmouseup"){
if(LzMouseKernel.__lastMouseDown==$4.owner.sprite){
LzMouseKernel.__lastMouseDown=null
}}}else{
if($5=="onmouseleave"){
$5="onmouseout"
}else{
if($5=="onmouseenter"){
$5="onmouseover"
}else{
if($5=="oncontextmenu"){
return
}}};$3[$4.owner].callMethod("lz.embed.iframemanager.__gotMouseEvent('"+$2+"','"+$5+"')")
}},setSendMouseEvents:function($1,$2){
if(this.__callqueue[$1]){
this.__callqueue[$1].push(["setSendMouseEvents",$1,$2]);return
};this.__sendmouseevents[$1]=$2
},__setSendMouseEvents:function(id,$1){
var iframe=lz.embed.iframemanager.getFrameWindow(id);if(!iframe){
return
};if($1){
if(lz.embed.browser.isIE){
lz.embed.attachEventHandler(document,"mouseover",lz.embed.iframemanager,"__globalMouseover",id)
};try{
lz.embed.attachEventHandler(iframe.document,"mousedown",lz.embed.iframemanager,"__mouseEvent",id);lz.embed.attachEventHandler(iframe.document,"mouseup",lz.embed.iframemanager,"__mouseEvent",id);lz.embed.attachEventHandler(iframe.document,"click",lz.embed.iframemanager,"__mouseEvent",id);iframe.document.oncontextmenu=function($1){
if(!$1){
$1=iframe.event
};return lz.embed.iframemanager.__mouseEvent($1,id)
};if(lz.embed.browser.isIE){
lz.embed.attachEventHandler(iframe.document,"mouseenter",lz.embed.iframemanager,"__mouseEvent",id);lz.embed.attachEventHandler(iframe.document,"mouseleave",lz.embed.iframemanager,"__mouseEvent",id)
}else{
lz.embed.attachEventHandler(iframe.document,"mouseover",lz.embed.iframemanager,"__mouseEvent",id);lz.embed.attachEventHandler(iframe.document,"mouseout",lz.embed.iframemanager,"__mouseEvent",id)
}}
catch(e){

}}else{
if(lz.embed.browser.isIE){
lz.embed.removeEventHandler(document,"mouseover",lz.embed.iframemanager,"__globalMouseover")
};try{
lz.embed.removeEventHandler(iframe.document,"mousedown",lz.embed.iframemanager,"__mouseEvent");lz.embed.removeEventHandler(iframe.document,"mouseup",lz.embed.iframemanager,"__mouseEvent");lz.embed.removeEventHandler(iframe.document,"click",lz.embed.iframemanager,"__mouseEvent");iframe.document.oncontextmenu=null;if(lz.embed.browser.isIE){
lz.embed.removeEventHandler(iframe.document,"mouseenter",lz.embed.iframemanager,"__mouseEvent");lz.embed.removeEventHandler(iframe.document,"mouseleave",lz.embed.iframemanager,"__mouseEvent")
}else{
lz.embed.removeEventHandler(iframe.document,"mouseover",lz.embed.iframemanager,"__mouseEvent");lz.embed.removeEventHandler(iframe.document,"mouseout",lz.embed.iframemanager,"__mouseEvent")
}}
catch(e){

}}},setShowNativeContextMenu:function($1,$2){
this.__hidenativecontextmenu[$1]=!$2
},storeSelection:function($1){
var $2=lz.embed.iframemanager;var $3=$2.getFrameWindow($1);if($3&&$3.document&&$3.document.selection&&$3.document.selection.type=="Text"){
$2.__selectionbookmarks[$1]=$3.document.selection.createRange().getBookmark()
}},restoreSelection:function($1){
var $2=lz.embed.iframemanager;var $3=$2.getFrameWindow($1);if($2.__selectionbookmarks[$1]&&$3){
var $4=$2.__selectionbookmarks[$1];var $5=$3.document.body.createTextRange();$5.moveToBookmark($4);$5.select()
}}};lz.embed.mousewheel={__mousewheelEvent:function($1){
if(!$1){
$1=window.event
};var $2=0;if($1.wheelDelta){
$2=$1.wheelDelta/120;if(lz.embed.browser.isOpera){
$2=-$2
}}else{
if($1.detail){
$2=-$1.detail/3
}};if($1.preventDefault){
$1.preventDefault()
};$1.returnValue=false;var $3=lz.embed.mousewheel.__callbacks.length;if($2!=null&&$3>0){
for(var $4=0;$4<$3;$4+=2){
var $5=lz.embed.mousewheel.__callbacks[$4];var $6=lz.embed.mousewheel.__callbacks[$4+1];if($5&&$5[$6]){
$5[$6]($2)
}}}},__callbacks:[],setCallback:function($1,$2){
var $3=lz&&lz.embed&&lz.embed.options&&lz.embed.options.cancelkeyboardcontrol!=true||true;if(lz.embed.mousewheel.__callbacks.length==0&&$3){
if(window.addEventListener){
lz.embed.attachEventHandler(window,"DOMMouseScroll",lz.embed.mousewheel,"__mousewheelEvent")
};lz.embed.attachEventHandler(document,"mousewheel",lz.embed.mousewheel,"__mousewheelEvent")
};lz.embed.mousewheel.__callbacks.push($1,$2)
}};lz.embed.history={active:null,_currentstate:null,_apps:[],_intervalID:null,_registeredapps:{},intervaltime:200,init:function(){
var $1=lz.embed.history;if($1.active||$1.active==false){
return
};$1.active=true;$1._title=top.document.title;var $2=$1.get();if(lz.embed.browser.isSafari){
$1._historylength=history.length;$1._history=[];for(var $3=1;$3<$1._historylength;$3++){
$1._history.push("")
};$1._history.push($2);var $4=document.createElement("form");$4.method="get";document.body.appendChild($4);$1._form=$4;if(!top.document.location.lzaddr){
top.document.location.lzaddr={}};if(top.document.location.lzaddr.history){
$1._history=top.document.location.lzaddr.history.split(",")
};if($2!=""){
$1.set($2)
}}else{
if(lz.embed.browser.isIE){
var $2=top.location.hash;if($2){
$2=$2.substring(1)
};var $3=document.createElement("iframe");lz.embed.__setAttr($3,"id","lzHistory");lz.embed.__setAttr($3,"frameborder","no");lz.embed.__setAttr($3,"scrolling","no");lz.embed.__setAttr($3,"width","0");lz.embed.__setAttr($3,"height","0");lz.embed.__setAttr($3,"src",'javascript:""');document.body.appendChild($3);$3=document.getElementById("lzHistory");$1._iframe=$3;$3.style.display="none";$3.style.position="absolute";$3.style.left="-999px";var $5=$3.contentDocument||$3.contentWindow.document;$5.open();$5.close();if($2!=""){
$5.location.hash="#"+$2;$1._parse($2)
}}else{
if($2!=""){
$1._parse($2);$1._currentstate=$2
}}};if($1._intervalID!=null){
clearInterval($1._intervalID)
};if($1.intervaltime>0){
$1._intervalID=setInterval("lz.embed.history._checklocationhash()",$1.intervaltime)
}},listen:function($1){
if(typeof $1=="string"){
$1=lz.embed.applications[$1];if(!$1||!$1.runtime){
return
}};if(!$1){
return
};var $2=lz.embed.history;if($2._registeredapps[$1._id]){
return
};$2._registeredapps[$1.id]=true;$2._apps.push($1);$2.init()
},_checklocationhash:function(){
if(lz.embed.dojo&&lz.embed.dojo.info&&lz.embed.dojo.info.installing){
return
};if(lz.embed.browser.isSafari){
var $1=this._history[this._historylength-1];if($1==""||$1=="#"){
$1="#0"
};if(!this._skip&&this._historylength!=history.length){
this._historylength=history.length;if(typeof $1!="undefined"){
$1=$1.substring(1);this._currentstate=$1;this._parse($1)
}}else{
this._parse($1.substring(1))
}}else{
var $1=lz.embed.history.get();if($1==""){
$1="0"
};if(lz.embed.browser.isIE){
if($1!=this._currentstate){
top.location.hash=$1=="0"?"":"#"+$1;this._currentstate=$1;this._parse($1)
};if(top.document.title!=this._title){
top.document.title=this._title
}}else{
this._currentstate=$1;this._parse($1)
}}},set:function($1){
if(lz.embed.history.active==false){
return
};if($1==null){
$1=""
};if(lz.embed.history._currentstate==$1){
return
};lz.embed.history._currentstate=$1;var $2="#"+$1;if(lz.embed.browser.isIE){
top.location.hash=$2=="#0"?"":$2;var $3=lz.embed.history._iframe.contentDocument||lz.embed.history._iframe.contentWindow.document;$3.open();$3.close();$3.location.hash=$2;lz.embed.history._parse($1+"")
}else{
if(lz.embed.browser.isSafari){
lz.embed.history._history[history.length]=$2;lz.embed.history._historylength=history.length+1;if(lz.embed.browser.version<412){
if(top.location.search==""){
lz.embed.history._form.action=$2;top.document.location.lzaddr.history=lz.embed.history._history.toString();lz.embed.history._skip=true;lz.embed.history._form.submit();lz.embed.history._skip=false
}}else{
var $4=document.createEvent("MouseEvents");$4.initEvent("click",true,true);var $5=document.createElement("a");$5.href=$2;$5.dispatchEvent($4)
}}else{
top.location.hash=$2;lz.embed.history._parse($1+"")
}};return true
},get:function(){
var $1="";if(lz.embed.browser.isIE){
if(lz.embed.history._iframe){
var $2=lz.embed.history._iframe.contentDocument||lz.embed.history._iframe.contentWindow.document;$1=$2.location.hash
}}else{
$1=top.location.href
};var $3=$1.indexOf("#");if($3!=-1){
return $1.substring($3+1)
};return ""
},_parse:function($1){
var $2=lz.embed.history;if($1.length==0){
return
};for(var $3=0,$4=lz.embed.history._apps.length;$3<$4;$3++){
var $5=lz.embed.history._apps[$3];if(!$5.loaded||$5._lasthash==$1){
continue
};$5._lasthash=$1;if($1.indexOf("_lz")!=-1){
$1=$1.substring(3);var $6=$1.split(",");for(var $7=0;$7<$6.length;$7++){
var $8=$6[$7];var $9=$8.indexOf("=");var $10=unescape($8.substring(0,$9));var $11=unescape($8.substring($9+1));lz.embed.setCanvasAttribute($10,$11);if(window["canvas"]){
canvas.setAttribute($10,$11)
}}}else{
if($5.runtime=="swf"){
$2.__setFlash($1,$5._id)
}else{
if(window["lz"]&&lz["History"]&&lz.History["isReady"]&&lz.History["receiveHistory"]){
lz.History.receiveHistory($1)
}}}}},_store:function($1,$2){
if($1 instanceof Object){
var $3="";for(var $4 in $1){
if($3!=""){
$3+=","
};$3+=escape($4)+"="+escape($1[$4])
}}else{
var $3=escape($1)+"="+escape($2)
};this.set("_lz"+$3)
},__setFlash:function($1,$2){
var $3=lz.embed[$2];if($3&&$3.loaded&&$3.runtime=="swf"){
var $4=$3._getSWFDiv();if($4){
var $5=$3.callMethod("lz.History.receiveHistory("+$1+")");$3._lasthash=$1
}}}};if(lz.embed.browser.isFirefox){
window.onunload=function(){

}};lz.embed.regex={cache:{},create:function($1,$2,$3){
try{
var $4=lz.embed.regex;$4.cache[$1]=new RegExp($4.unmask($2),$4.unmask($3));return true
}
catch(e){
return e.name+": "+e.message
}},test:function($1,$2,$3){
var $4=lz.embed.regex;var $5=$4.cache[$1];$5.lastIndex=$3;return [$5.test($4.unmask($2)),$5.lastIndex]
},exec:function($1,$2,$3){
var $4=lz.embed.regex;var $5=$4.cache[$1];$5.lastIndex=$3;var $6=$5.exec($4.unmask($2));if($6){
($6=$4.maskArr($6)).push($6.index,$5.lastIndex);return $6
}else{
return null
}},match:function($1,$2){
var $3=lz.embed.regex;var $4=$3.unmask($2).match($3.cache[$1]);return $4?$3.maskArr($4):null
},replace:function($1,$2,$3){
var $4=lz.embed.regex;return $4.mask($4.unmask($2).replace($4.cache[$1],$4.unmask($3)))
},search:function($1,$2){
var $3=lz.embed.regex;return $3.unmask($2).search($3.cache[$1])
},split:function($1,$2,$3){
var $4=lz.embed.regex;return $4.maskArr($4.unmask($2).split($4.cache[$1],$3))
},remove:function($1){
delete lz.embed.regex.cache[$1]
},mask:function($1){

var re = /^\s*$/;
var re2 = /\s/g;
;return( $1==null||!re.test($1)?$1:"__#lznull"+$1.replace(re2,function($1){
switch($1){
case " ":
return "w";
case "\f":
return "f";
case "\n":
return "n";
case "\r":
return "r";
case "\t":
return "t";
case "\xA0":
return "s";
case "\u2028":
return "l";
case "\u2029":
return "p";

}}))
},unmask:function($1){
return $1=="__#lznull"?"":$1
},maskArr:function($1){
var $2=lz.embed.regex;for(var $3=0;$3<$1.length;++$3){
$1[$3]=$2.mask($1[$3])
};return $1
}};