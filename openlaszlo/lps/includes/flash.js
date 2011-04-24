var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;lz.embed.dojo=function(){};lz.embed.dojo={defaults:{flash8:null,ready:false,visible:true,width:500,height:400,bgcolor:"#ffffff",allowfullscreen:false,wmode:"window",flashvars:"",minimumVersion:8,id:"flashObject",appenddiv:null},obj:{},comm:{},_loadedListeners:[],_loadedListenerScopes:[],_installingListeners:[],_installingListenerScopes:[],setSwf:function($0,$1){
if($0==null){
return
};var $2={};for(var $3 in this.defaults){
var $4=$0[$3];if($4!=null){
$2[$3]=$4
}else{
$2[$3]=this.defaults[$3]
}};if($1!=null){
this.minimumVersion=$1
};this._initialize($2)
},addLoadedListener:function($0,$1){
this._loadedListeners.push($0);this._loadedListenerScopes.push($1)
},addInstallingListener:function($0,$1){
this._installingListeners.push($0);this._installingListenerScopes.push($1)
},loaded:function($0){
var $1=lz.embed.dojo;if($1._isinstaller){
top.location=top.location+""
};$1.info.installing=false;$1.ready=true;if($1._loadedListeners.length>0){
for(var $2=0;$2<$1._loadedListeners.length;$2++){
var $3=$1._loadedListenerScopes[$2];if($0!=$3._id)continue;lz.embed.dojo._loadedListeners[$2].apply($3,[$3._id])
}}},installing:function(){
var $0=lz.embed.dojo;if($0._installingListeners.length>0){
for(var $1=0;$1<$0._installingListeners.length;$1++){
var $2=$0._installingListenerScopes[$1];$0._installingListeners[$1].apply($2,[$2._id])
}}},_initialize:function($0){
var $1=lz.embed.dojo;var $2=new ($1.Install)($0.id);$1.installer=$2;var $3=new ($1.Embed)($0);$1.obj[$0.id]=$3;if($2.needed()==true){
$2.install()
}else{
$3.write($1.info.commVersion);$1.comm[$0.id]=new ($1.Communicator)($0.id)
}},__unescapestring:function($0){
var $1=arguments[0];var $2=eval($1);var $3=null;var $4=$1.lastIndexOf(".");if($4>-1){
$3=eval($1.substring(0,$4))
};if(!$2||(function($0,$1){
return $1["$lzsc$isa"]?$1.$lzsc$isa($0):$0 instanceof $1
})(!$2,Function)){
return
};var $5=[];for(var $6=1,$7=arguments.length;$6<$7;$6++){
var $8=arguments[$6];if(typeof $8==="object"){
for(var $9 in $8){
if($8[$9]==="__#lznull"){
$8[$9]=""
}}}else if(typeof $8==="string"){
if($8==="__#lznull"){
$8=""
}};$5[$6-1]=$8
};var $a=$2.apply($3,$5);if($a===""){
return "__#lznull"
}else{
return $a
}}};lz.embed.dojo.Info=function(){
this._detectVersion();this._detectCommunicationVersion()
};lz.embed.dojo.Info.prototype={version:-1,versionMajor:-1,versionMinor:-1,versionRevision:-1,capable:false,commVersion:8,installing:false,isVersionOrAbove:function($0,$1,$2){
if(this.versionMajor<9){
$2=parseFloat("."+$2)
};if(this.versionMajor>=$0&&this.versionMinor>=$1&&this.versionRevision>=$2){
return true
}else{
return false
}},_detectVersion:function(){
var $0;var $1=lz.embed.browser.isIE;for(var $2=25;$2>0;$2--){
if($1){
$0=VBGetSwfVer($2)
}else{
$0=this._JSFlashInfo($2)
};if($0==-1){
this.capable=false;return
}else if($0!=0){
var $3;if($1){
var $4=$0.split(" ");var $5=$4[1];$3=$5.split(",")
}else{
$3=$0.split(".")
};this.versionMajor=$3[0];this.versionMinor=$3[1];this.versionRevision=$3[2];var $6=this.versionMajor+"."+this.versionMinor;this.version=parseFloat($6);this.capable=true;break
}}},_JSFlashInfo:function($0){
if(navigator.plugins!=null&&navigator.plugins.length>0){
if(navigator.plugins["Shockwave Flash 2.0"]||navigator.plugins["Shockwave Flash"]){
var $1=navigator.plugins["Shockwave Flash 2.0"]?" 2.0":"";var $2=navigator.plugins["Shockwave Flash"+$1].description;var $3=$2.split(" ");var $4=$3[2].split(".");var $5=$4[0];var $6=$4[1];if($3[3]!=""){
var $7=$3[3].split("r")
}else{
var $7=$3[4].split("r")
};var $8=$7[1]>0?$7[1]:0;var $9=$5+"."+$6+"."+$8;return $9
}};return -1
},_detectCommunicationVersion:function(){
if(this.capable==false){
this.commVersion=null;return
}}};lz.embed.dojo.Embed=function($0){
this.properties=$0;if(!this.properties.width)this.properties.width="100%";if(!this.properties.height)this.properties.height="100%";if(!this.properties.bgcolor)this.properties.bgcolor="#ffffff";if(!this.properties.visible)this.properties.visible=true
};lz.embed.dojo.Embed.prototype={protocol:function(){
switch(window.location.protocol){
case "https:":
return "https";break;
default:
return "http";break;

}},__getCSSValue:function($0){
if($0&&$0.length&&$0.indexOf("%")!=-1){
return "100%"
}else{
return $0+"px"
}},write:function($0,$1){
var $2="";$2+="width: "+this.__getCSSValue(this.properties.width)+";";$2+="height: "+this.__getCSSValue(this.properties.height)+";";if(this.properties.visible==false){
$2+="position: absolute; ";$2+="z-index: 10000; ";$2+="top: -1000px; ";$2+="left: -1000px; "
};var $3;var $4;var $5=lz.embed;if($0>$5.dojo.version)$1=true;$4=this.properties.flash8;var $6=this.properties.flashvars;var $7=this.properties.flashvars;if($1){
var $8=escape(window.location);document.title=document.title.slice(0,47)+" - Flash Player Installation";var $9=escape(document.title);$6+="&MMredirectURL="+$8+"&MMplayerType=ActiveX"+"&MMdoctitle="+$9;$7+="&MMredirectURL="+$8+"&MMplayerType=PlugIn"
};if($5.browser.isIE){
$3='<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" '+'codebase="'+this.protocol()+"://fpdownload.macromedia.com/pub/shockwave/cabs/flash/"+'swflash.cab#version=8,0,0,0" '+'width="'+this.properties.width+'" '+'height="'+this.properties.height+'" '+'id="'+this.properties.id+'" '+'align="middle"> '+'<param name="allowScriptAccess" value="sameDomain" /> '+'<param name="movie" value="'+$4+'" /> '+'<param name="quality" value="high" /> '+'<param name="FlashVars" value="'+$6+'" /> '+'<param name="bgcolor" value="'+this.properties.bgcolor+'" /> '+'<param name="wmode" value="'+this.properties.wmode+'" /> '+'<param name="allowFullScreen" value="'+this.properties.allowfullscreen+'" /> '+"</object>"
}else{
$3='<embed src="'+$4+'" '+'quality="high" '+'bgcolor="'+this.properties.bgcolor+'" '+'wmode="'+this.properties.wmode+'" '+'allowFullScreen="'+this.properties.allowfullscreen+'" '+'width="'+this.properties.width+'" '+'height="'+this.properties.height+'" '+'id="'+this.properties.id+'" '+'name="'+this.properties.id+'" '+'FlashVars="'+$7+'" '+'swLiveConnect="true" '+'align="middle" '+'allowScriptAccess="sameDomain" '+'type="application/x-shockwave-flash" '+'pluginspage="'+this.protocol()+'://www.macromedia.com/go/getflashplayer" />'
};var $a=this.properties.id+"Container";var $b=this.properties.appenddiv;if($b){
$b.innerHTML=$3;$b.setAttribute("style",$2)
}else{
$3='<div id="'+$a+'" style="'+$2+'"> '+$3+"</div>";document.writeln($3)
}},get:function(){
try{
var $0=document.getElementById(this.properties.id+"")
}
catch($1){};return $0
},setVisible:function($0){
var $1=document.getElementById(this.properties.id+"Container");if($0==true){
$1.style.visibility="visible"
}else{
$1.style.position="absolute";$1.style.x="-1000px";$1.style.y="-1000px";$1.style.visibility="hidden"
}},center:function(){
var $0=this.properties.width;var $1=this.properties.height;var $2=0;var $3=0;var $4=document.getElementById(this.properties.id+"Container");$4.style.top=$3+"px";$4.style.left=$2+"px"
}};lz.embed.dojo.Communicator=function($0){
this._id=$0
};lz.embed.dojo.Communicator.prototype={_addExternalInterfaceCallback:function(methodName,id){
var dojo=lz.embed.dojo;var $0=function(){
var $0=[];for(var $1=0;$1<arguments.length;$1++){
$0[$1]=arguments[$1]
};$0.length=arguments.length;return dojo.comm[id]._execFlash(methodName,$0,id)
};dojo.comm[id][methodName]=$0
},_encodeData:function($0){
var $1=RegExp("\\&([^;]*)\\;","g");$0=$0.replace($1,"&amp;$1;");$0=$0.replace(RegExp("<","g"),"&lt;");$0=$0.replace(RegExp(">","g"),"&gt;");$0=$0.replace("\\","&custom_backslash;");$0=$0.replace(RegExp("\\n","g"),"\\n");$0=$0.replace(RegExp("\\r","g"),"\\r");$0=$0.replace(RegExp("\\f","g"),"\\f");$0=$0.replace(RegExp("\\0","g"),"\\0");$0=$0.replace(RegExp("\\'","g"),"\\'");$0=$0.replace(RegExp('\\"',"g"),'\\"');return $0
},_decodeData:function($0){
if($0==null||typeof $0=="undefined"){
return $0
};$0=$0.replace(RegExp("\\&custom_lt\\;","g"),"<");$0=$0.replace(RegExp("\\&custom_gt\\;","g"),">");$0=eval('"'+$0+'"');return $0
},_chunkArgumentData:function($0,$1,$2){
var $3=lz.embed.dojo.obj[$2].get();var $4=Math.ceil($0.length/1024);for(var $5=0;$5<$4;$5++){
var $6=$5*1024;var $7=$5*1024+1024;if($5==$4-1){
$7=$5*1024+$0.length
};var $8=$0.substring($6,$7);$8=this._encodeData($8);$3.CallFunction('<invoke name="chunkArgumentData" '+'returntype="javascript">'+"<arguments>"+"<string>"+$8+"</string>"+"<number>"+$1+"</number>"+"</arguments>"+"</invoke>")
}},_chunkReturnData:function($0){
var $1=lz.embed.dojo.obj[$0].get();var $2=$1.getReturnLength();var $3=[];for(var $4=0;$4<$2;$4++){
var $5=$1.CallFunction('<invoke name="chunkReturnData" '+'returntype="javascript">'+"<arguments>"+"<number>"+$4+"</number>"+"</arguments>"+"</invoke>");if($5=='""'||$5=="''"){
$5=""
}else{
$5=$5.substring(1,$5.length-1)
};$3.push($5)
};var $6=$3.join("");return $6
},_execFlash:function($0,$1,$2){
var $3=lz.embed.dojo.obj[$2].get();$3.startExec();$3.setNumberArguments($1.length);for(var $4=0;$4<$1.length;$4++){
this._chunkArgumentData($1[$4],$4,$2)
};$3.exec($0);var $5=this._chunkReturnData($2);$5=this._decodeData($5);$3.endExec();return $5
}};lz.embed.dojo.Install=function($0){
this._id=$0
};lz.embed.dojo.Install.prototype={needed:function(){
var $0=lz.embed.dojo;if($0.info.capable==false){
return true
};if(lz.embed.browser.isSafari==true&&!$0.info.isVersionOrAbove(8,0,0)){
return true
};if($0.minimumVersion>$0.info.version){
return true
};if(!$0.info.isVersionOrAbove(8,0,0)){
return true
};return false
},install:function(){
var $0=lz.embed.dojo;$0.info.installing=true;$0.installing();var $1=$0.obj[this._id].properties;var $2=$1.flash8;var $3=$2.indexOf("swf7");if($3!=-1){
$0._tempurl=$2;$2=$2.substring(0,$3+3)+"8"+$2.substring($3+4,$2.length);$1.flash8=$2
};var $3=$2.indexOf("swf9");if($3!=-1){
$0._tempurl=$2;$2=$2.substring(0,$3+3)+"8"+$2.substring($3+4,$2.length);$1.flash8=$2
};$0.ready=false;if($0.info.capable==false){
$0._isinstaller=true;var $4=new ($0.Embed)($1);$4.write($0.minimumVersion)
}else if($0.info.isVersionOrAbove(6,0,65)){
var $4=new ($0.Embed)($1);$4.write($0.minimumVersion,true);$4.setVisible(true);$4.center()
}else{
alert("This content requires a more recent version of the Macromedia "+" Flash Player.");window.location="http://www.macromedia.com/go/getflashplayer"
}},_onInstallStatus:function($0){
if($0=="Download.Complete"){
if(lz.embed.browser.isIE){
top.location=top.location+""
}}else if($0=="Download.Cancelled"){
alert("This content requires a more recent version of the Macromedia "+" Flash Player.");window.location="http://www.macromedia.com/go/getflashplayer"
}else if($0=="Download.Failed"){
alert("There was an error downloading the Flash Player update. "+"Please try again later, or visit macromedia.com to download "+"the latest version of the Flash plugin.");window.location="http://www.macromedia.com/go/getflashplayer"
}}};lz.embed.dojo.info=new (lz.embed.dojo.Info)();lz.embed.regex={cache:{},create:function($0,$1,$2){
try{
var $3=lz.embed.regex;$3.cache[$0]=new RegExp($3.unmask($1),$3.unmask($2));return true
}
catch($4){
return $4.name+": "+$4.message
}},test:function($0,$1,$2){
var $3=lz.embed.regex;var $4=$3.cache[$0];$4.lastIndex=$2;return [$4.test($3.unmask($1)),$4.lastIndex]
},exec:function($0,$1,$2){
var $3=lz.embed.regex;var $4=$3.cache[$0];$4.lastIndex=$2;var $5=$4.exec($3.unmask($1));if($5){
($5=$3.maskArr($5)).push($5.index,$4.lastIndex);return $5
}else{
return null
}},match:function($0,$1){
var $2=lz.embed.regex;var $3=$2.unmask($1).match($2.cache[$0]);return $3?$2.maskArr($3):null
},replace:function($0,$1,$2){
var $3=lz.embed.regex;return $3.mask($3.unmask($1).replace($3.cache[$0],$3.unmask($2)))
},search:function($0,$1){
var $2=lz.embed.regex;return $2.unmask($1).search($2.cache[$0])
},split:function($0,$1,$2){
var $3=lz.embed.regex;return $3.maskArr($3.unmask($1).split($3.cache[$0],$2))
},remove:function($0){
delete lz.embed.regex.cache[$0]
},mask:function($0){

var re = /^\s*$/;
var re2 = /\s/g;
;return( $0==null||!re.test($0)?$0:"__#lznull"+$0.replace(re2,function($0){
switch($0){
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
},unmask:function($0){
return $0=="__#lznull"?"":$0
},maskArr:function($0){
var $1=lz.embed.regex;for(var $2=0;$2<$0.length;++$2)$0[$2]=$1.mask($0[$2]);return $0
}};