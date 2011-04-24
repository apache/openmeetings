var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;pmrpc=self.pmrpc=(function(){
var generateUUID;var convertWildcardToRegex;var checkACL;var invokeProcedure;var encode;var decode;var createJSONRpcBaseObject;var createJSONRpcRequestObject;var createJSONRpcErrorObject;var createJSONRpcResponseObject;var register;var unregister;var fetchRegisteredService;var processPmrpcMessage;var processJSONRpcRequest;var processJSONRpcResponse;var call;var sendPmrpcMessage;var waitAndSendRequest;var addCrossBrowserEventListerner;var createHandler;var $2;var $3;var $4;var findAllWindows;var findAllWorkers;var findAllReachableContexts;var $5;var $6;generateUUID=function(){
var $0=[],$1="89AB",$2="0123456789ABCDEF";for(var $3=0;$3<36;$3++){
$0[$3]=$2[Math.floor(Math.random()*16)]
};$0[14]="4";$0[19]=$1[Math.floor(Math.random()*4)];$0[8]=$0[13]=$0[18]=$0[23]="-";return $0.join("")
};convertWildcardToRegex=function($0){
var $1=$0.replace(__wildcardRegexp,"\\$1");$1="^"+$1.replace(__starRegexp,".*")+"$";return $1
};checkACL=function($0,$1){
var $2=$0.whitelist;var $3=$0.blacklist;var $4=false;var $5=false;for(var $6=0;$6<$2.length;++$6){
var $7=convertWildcardToRegex($2[$6]);if($1.match($7)){
$4=true;break
}};for(var $8=0;$6<$3.length;++$8){
var $7=convertWildcardToRegex($3[$8]);if($1.match($7)){
$5=true;break
}};return $4&&!$5
};invokeProcedure=function($0,$1,$2,$3){
if(!($2 instanceof Array)){
var $4=$0.toString();var $5=$4.substring($4.indexOf("(")+1,$4.indexOf(")"));$5=$5===""?[]:$5.split(", ");var $6={};for(var $7=0;$7<$5.length;$7++){
$6[$5[$7]]=$7
};var $8=[];for(var $9 in $2){
if(typeof $6[$9]!=="undefined"){
$8[$6[$9]]=$2[$9]
}else{
throw "No such param!"
}};$2=$8
};if(typeof $3!=="undefined"){
$2=$2.concat($3)
};return $0.apply($1,$2)
};encode=function($0){
return "pmrpc."+JSON.stringify($0)
};decode=function($0){
return JSON.parse($0.substring("pmrpc.".length))
};createJSONRpcBaseObject=function(){
var $0={};$0.jsonrpc="2.0";return $0
};createJSONRpcRequestObject=function($0,$1,$2){
var $3=createJSONRpcBaseObject();$3.method=$0;$3.params=$1;if(typeof $2!=="undefined"){
$3.id=$2
};return $3
};createJSONRpcErrorObject=function($0,$1,$2){
var $3={};$3.code=$0;$3.message=$1;$3.data=$2;return $3
};createJSONRpcResponseObject=function($0,$1,$2){
var $3=createJSONRpcBaseObject();$3.id=$2;if(typeof $0==="undefined"||$0===null){
$3.result=$1==="undefined"?null:$1
}else{
$3.error=$0
};return $3
};register=function($0){
if($0.publicProcedureName in reservedProcedureNames){
return false
}else{
registeredServices[$0.publicProcedureName]={"publicProcedureName":$0.publicProcedureName,"procedure":$0.procedure,"context":$0.procedure.context,"isAsync":typeof $0.isAsynchronous!=="undefined"?$0.isAsynchronous:false,"acl":typeof $0.acl!=="undefined"?$0.acl:{whitelist:["*"],blacklist:[]}};return true
}};unregister=function($0){
if($0 in reservedProcedureNames){
return false
}else{
delete registeredServices[$0];return true
}};fetchRegisteredService=function($0){
return registeredServices[$0]
};processPmrpcMessage=function($0){
var $1=$0.event;var $2=$0.source;var $3=typeof $2!=="undefined"&&$2!==null;if($1.data.indexOf("pmrpc.")!==0){
return
}else{
var $4=decode($1.data);if(typeof $4.method!=="undefined"){
var $5={data:$1.data,source:$3?$2:$1.source,origin:$3?"*":$1.origin,shouldCheckACL:!$3};response=processJSONRpcRequest($4,$5);if(response!==null){
sendPmrpcMessage($5.source,response,$5.origin)
}}else{
processJSONRpcResponse($4)
}}};processJSONRpcRequest=function($0,serviceCallEvent,$1){
if($0.jsonrpc!=="2.0"){
return createJSONRpcResponseObject(createJSONRpcErrorObject(-32600,"Invalid request.","The recived JSON is not a valid JSON-RPC 2.0 request."),null,null)
};var id=$0.id;var $2=fetchRegisteredService($0.method);if(typeof $2!=="undefined"){
if(!serviceCallEvent.shouldCheckACL||checkACL($2.acl,serviceCallEvent.origin)){
try{
if($2.isAsync){
var $3=function($0){
sendPmrpcMessage(serviceCallEvent.source,createJSONRpcResponseObject(null,$0,id),serviceCallEvent.origin)
};invokeProcedure($2.procedure,$2.context,$0.params,[$3,serviceCallEvent]);return null
}else{
var $4=invokeProcedure($2.procedure,$2.context,$0.params,[serviceCallEvent]);return typeof id==="undefined"?null:createJSONRpcResponseObject(null,$4,id)
}}
catch($5){
if(typeof id==="undefined"){
return null
};if($5==="No such param!"){
return createJSONRpcResponseObject(createJSONRpcErrorObject(-32602,"Invalid params.",$5.description),null,id)
};return createJSONRpcResponseObject(createJSONRpcErrorObject(-1,"Application error.",$5.description),null,id)
}}else{
return typeof id==="undefined"?null:createJSONRpcResponseObject(createJSONRpcErrorObject(-2,"Application error.","Access denied on server."),null,id)
}}else{
return typeof id==="undefined"?null:createJSONRpcResponseObject(createJSONRpcErrorObject(-32601,"Method not found.","The requestd remote procedure does not exist or is not available."),null,id)
}};processJSONRpcResponse=function($0){
var $1=$0.id;var $2=callQueue[$1];if(typeof $2==="undefined"||$2===null){
return
}else{
delete callQueue[$1]
};if(typeof $0.error==="undefined"){
$2.onSuccess({"destination":$2.destination,"publicProcedureName":$2.publicProcedureName,"params":$2.params,"status":"success","returnValue":$0.result})
}else{
$2.onError({"destination":$2.destination,"publicProcedureName":$2.publicProcedureName,"params":$2.params,"status":"error","description":$0.error.message+" "+$0.error.data})
}};call=function($0){
if($0.retries&&$0.retries<0){
throw new Exception("number of retries must be 0 or higher")
};var $1=[];if(typeof $0.destination==="undefined"||$0.destination===null||$0.destination==="workerParent"){
$1=[{context:null,type:"workerParent"}]
}else if($0.destination==="publish"){
$1=findAllReachableContexts()
}else if($0.destination instanceof Array){
for(var $2=0;$2<$0.destination.length;$2++){
if($0.destination[$2]==="workerParent"){
$1.push({context:null,type:"workerParent"})
}else if(typeof $0.destination[$2].frames!=="undefined"){
$1.push({context:$0.destination[$2],type:"window"})
}else{
$1.push({context:$0.destination[$2],type:"worker"})
}}}else{
if(typeof $0.destination.frames!=="undefined"){
$1.push({context:$0.destination,type:"window"})
}else{
$1.push({context:$0.destination,type:"worker"})
}};for(var $2=0;$2<$1.length;$2++){
var $3={destination:$1[$2].context,destinationDomain:typeof $0.destinationDomain==="undefined"?["*"]:(typeof $0.destinationDomain==="string"?[$0.destinationDomain]:$0.destinationDomain),publicProcedureName:$0.publicProcedureName,onSuccess:typeof $0.onSuccess!=="undefined"?$0.onSuccess:(function(){}),onError:typeof $0.onError!=="undefined"?$0.onError:(function(){}),retries:typeof $0.retries!=="undefined"?$0.retries:5,timeout:typeof $0.timeout!=="undefined"?$0.timeout:500,status:"requestNotSent"};isNotification=typeof $0.onError==="undefined"&&typeof $0.onSuccess==="undefined";params=typeof $0.params!=="undefined"?$0.params:[];callId=generateUUID();callQueue[callId]=$3;if(isNotification){
$3.message=createJSONRpcRequestObject($0.publicProcedureName,params)
}else{
$3.message=createJSONRpcRequestObject($0.publicProcedureName,params,callId)
};waitAndSendRequest(callId)
}};sendPmrpcMessage=function($0,$1,$2){
if(typeof $0==="undefined"||$0===null){
self.postMessage(encode($1))
}else if(typeof $0.frames!=="undefined"){
return $0.postMessage(encode($1),$2)
}else if($0&&$0.postMessage){
$0.postMessage(encode($1))
}};waitAndSendRequest=function(callId){
var $0=callQueue[callId];if(typeof $0==="undefined"){
return
}else if($0.retries<=-1){
processJSONRpcResponse(createJSONRpcResponseObject(createJSONRpcErrorObject(-4,"Application error.","Destination unavailable."),null,callId))
}else if($0.status==="requestSent"){
return
}else if($0.retries===0||$0.status==="available"){
$0.status="requestSent";$0.retries=-1;callQueue[callId]=$0;for(var $1=0;$1<$0.destinationDomain.length;$1++){
sendPmrpcMessage($0.destination,$0.message,$0.destinationDomain[$1],$0);self.setTimeout(function(){
waitAndSendRequest(callId)
},$0.timeout)
}}else{
$0.status="pinging";$0.retries=$0.retries-1;call({"destination":$0.destination,"publicProcedureName":"receivePingRequest","onSuccess":function($0){
if($0.returnValue===true&&typeof callQueue[callId]!=="undefined"){
callQueue[callId].status="available";waitAndSendRequest(callId)
}},"params":[$0.publicProcedureName],"retries":0,"destinationDomain":$0.destinationDomain});callQueue[callId]=$0;self.setTimeout(function(){
waitAndSendRequest(callId)
},$0.timeout/$0.retries)
}};addCrossBrowserEventListerner=function($0,$1,$2,$3){
if("addEventListener" in $0){
$0.addEventListener($1,$2,$3)
}else{
$0.attachEvent("on"+$1,$2)
}};createHandler=function(method,source,destinationType){
return( function($0){
var $1={event:$0,source:source,destinationType:destinationType};method($1)
})
};$2=function($0){
return typeof fetchRegisteredService($0)!=="undefined"
};findAllWindows=function(){
var $0=[];if(typeof window!=="undefined"){
$0.push({context:window.top,type:"window"});for(var $1=0;typeof $0[$1]!=="undefined";$1++){
var $2=$0[$1];for(var $3=0;$3<$2.context.frames.length;$3++){
$0.push({context:$2.context.frames[$3],type:"window"})
}}}else{
$0.push({context:this,type:"workerParent"})
};return $0
};findAllWorkers=function(){
return allWorkers
};findAllReachableContexts=function(){
var $0=findAllWindows();var $1=findAllWorkers();var $2=$0.concat($1);return $2
};$5=function(){
var $0=[];var $1=typeof this.frames!=="undefined"?window.location.protocol+"//"+window.location.host+(window.location.port!==""?":"+window.location.port:""):"";for(publicProcedureName in registeredServices){
if(publicProcedureName in reservedProcedureNames){
continue
}else{
$0.push({"publicProcedureName":registeredServices[publicProcedureName].publicProcedureName,"acl":registeredServices[publicProcedureName].acl,"origin":$1})
}};return $0
};$6=function(params){
var addToDiscoveredMethods;addToDiscoveredMethods=function($0,$1){
for(var $2=0;$2<$0.length;$2++){
if($0[$2].origin.match(originRegex)&&$0[$2].publicProcedureName.match(nameRegex)){
discoveredMethods.push({publicProcedureName:$0[$2].publicProcedureName,destination:$1,procedureACL:$0[$2].acl,destinationOrigin:$0[$2].origin})
}}};var $0=null;if(typeof params.destination==="undefined"){
$0=findAllReachableContexts();for(var $1=0;$1<$0.length;$1++){
$0[$1]=$0[$1].context
}}else{
$0=params.destination
};var originRegex=typeof params.origin==="undefined"?".*":params.origin;var nameRegex=typeof params.publicProcedureName==="undefined"?".*":params.publicProcedureName;var counter=$0.length;var discoveredMethods=[];pmrpc.call({destination:$0,destinationDomain:"*",publicProcedureName:"getRegisteredProcedures",onSuccess:function($0){
counter--;addToDiscoveredMethods($0.returnValue,$0.destination);if(counter===0){
params.callback(discoveredMethods)
}},onError:function($0){
counter--;if(counter===0){
params.callback(discoveredMethods)
}}})
};if(typeof JSON==="undefined"||typeof JSON.stringify==="undefined"||typeof JSON.parse==="undefined"){
window.console&&console.error&&console.error("pmrpc requires the JSON library")
};if(typeof this.postMessage==="undefined"&&typeof this.onconnect==="undefined"){
window.console&&console.error&&console.error("pmrpc requires the HTML5 cross-document messaging and worker APIs")
};var __wildcardRegexp=new RegExp("([\\^\\$\\.\\+\\?\\=\\!\\:\\|\\\\/\\(\\)\\[\\]\\{\\}])","g");var __starRegexp=new RegExp("\\*","g");var registeredServices={};var callQueue={};var reservedProcedureNames={};if("window" in this){
var $0=createHandler(processPmrpcMessage,null,"window");addCrossBrowserEventListerner(this,"message",$0,false)
}else if("onmessage" in this){
var $0=createHandler(processPmrpcMessage,this,"worker");addCrossBrowserEventListerner(this,"message",$0,false)
}else if("onconnect" in this){
var $1=function($0){
var $1=createHandler(processPmrpcMessage,$0.ports[0],"sharedWorker");addCrossBrowserEventListerner($0.ports[0],"message",$1,false);$0.ports[0].start()
};addCrossBrowserEventListerner(this,"connect",$1,false)
}else{
throw "Pmrpc must be loaded within a browser window or web worker."
};var createDedicatedWorker=this.Worker;this.nonPmrpcWorker=createDedicatedWorker;var createSharedWorker=this.SharedWorker;this.nonPmrpcSharedWorker=createSharedWorker;var allWorkers=[];this.Worker=function($0){
var $1=new createDedicatedWorker($0);allWorkers.push({context:$1,type:"worker"});var $2=createHandler(processPmrpcMessage,$1,"worker");addCrossBrowserEventListerner($1,"message",$2,false);return $1
};this.SharedWorker=function($0,$1){
var newWorker=new createSharedWorker($0,$1);allWorkers.push({context:newWorker,type:"sharedWorker"});var $2=createHandler(processPmrpcMessage,newWorker.port,"sharedWorker");addCrossBrowserEventListerner(newWorker.port,"message",$2,false);newWorker.postMessage=function($0,$1){
return newWorker.port.postMessage($0,$1)
};newWorker.port.start();return newWorker
};register({"publicProcedureName":"receivePingRequest","procedure":$2});register({"publicProcedureName":"getRegisteredProcedures","procedure":$5});reservedProcedureNames={"getRegisteredProcedures":null,"receivePingRequest":null};return {register:register,unregister:unregister,call:call,discover:$6}})();lz.embed.iframemanager={__counter:0,__frames:{},__ownerbyid:{},__loading:{},__callqueue:{},__calljsqueue:{},__sendmouseevents:{},__hidenativecontextmenu:{},__sendmouseevents:{},__selectionbookmarks:{},__srcbyid:{},create:function($0,$1,$2,$3,$4,$5){
var $6="__lz"+lz.embed.iframemanager.__counter++;if(typeof $0=="string"){
lz.embed.iframemanager.__ownerbyid[$6]=$0
};var $7='javascript:""';var $8='lz.embed.iframemanager.__gotload("'+$6+'")';if($1==null||$1=="null"||$1=="")$1=$6;if($3==null||$3=="undefined"){
$3=document.body
};if(document.all){
var $9="<iframe name='"+$1+"' id='"+$6+"' src='"+$7+"' onload='"+$8+"' frameBorder='0'";if($2!=true)$9+=" scrolling='no'";$9+="></iframe>";var $a=document.createElement("div");lz.embed.__setAttr($a,"id",$6+"Container");$3.appendChild($a);$a.style.position="absolute";$a.style.display="none";$a.style.top="0px";$a.style.left="0px";$a.innerHTML=$9;var $b=document.getElementById($6)
}else{
var $b=document.createElement("iframe");lz.embed.__setAttr($b,"name",$1);lz.embed.__setAttr($b,"src",$7);lz.embed.__setAttr($b,"id",$6);lz.embed.__setAttr($b,"onload",$8);lz.embed.__setAttr($b,"tabindex","-1");if($2!=true)lz.embed.__setAttr($b,"scrolling","no");this.appendTo($b,$3)
};if($b){
this.__finishCreate($6,$0,$1,$2,$3,$4,$5)
}else{
this.__callqueue[$6]=[["__finishCreate",$6,$0,$1,$2,$3,$4,$5]];setTimeout('lz.embed.iframemanager.__checkiframe("'+$6+'")',10)
};return $6+""
},__checkiframe:function($0){
var $1=document.getElementById($0);if($1){
var $2=lz.embed.iframemanager.__callqueue[$0];delete lz.embed.iframemanager.__callqueue[$0];lz.embed.iframemanager.__playQueue($2)
}else{
setTimeout('lz.embed.iframemanager.__checkiframe("'+$0+'")',10)
}},__playQueue:function($0){
var $1=lz.embed.iframemanager;for(var $2=0;$2<$0.length;$2++){
var $3=$0[$2];var $4=$3.splice(0,1);$1[$4].apply($1,$3)
}},__finishCreate:function($0,$1,$2,$3,$4,$5,$6){
var $7=document.getElementById($0);if(typeof $1=="string"){
$7.appcontainer=lz.embed.applications[$1]._getSWFDiv()
};var $8=lz.embed.iframemanager;$7.__owner=$1;$8.__frames[$0]=$7;var $9=$8.getFrame($0);$9.__gotload=$8.__gotload;$9.__zoffset=$5!=null?$5:this.__bottomz;if(document.getElementById&&!document.all){
$9.style.border="0"
}else if(document.all){
lz.embed.__setAttr($9,"allowtransparency","true");var $a=lz.embed[$9.owner];if($a&&$a.runtime=="swf"){
var $b=$a._getSWFDiv();$b.onfocus=$8.__refresh
}};$9.style.position="absolute";if(typeof $1=="string"){
setTimeout("lz.embed.applications."+$1+".callMethod('lz.embed.iframemanager.__setiframeid(\""+$0+"\")')",0)
}else{
$1.__setiframeid($0)
}},appendTo:function($0,$1){
if($1.__appended==$1)return;if($0.__appended){
old=$0.__appended.removeChild($0);$1.appendChild(old)
}else{
$1.appendChild($0)
};$0.__appended=$1
},getFrame:function($0){
return lz.embed.iframemanager.__frames[$0]
},getFrameWindow:function($0){
if(!this["framesColl"]){
if(document.frames){
this.framesColl=document.frames
}else{
this.framesColl=window.frames
}};return this.framesColl[$0]
},setHTML:function($0,$1){
if($1&&$1!=""){
var $2=lz.embed.iframemanager.getFrameWindow($0);if($2){
$2.document.body.innerHTML=$1
}}},setSrc:function($0,$1,$2){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["setSrc",$0,$1,$2]);return
};this.__setSendMouseEvents($0,false);if($2){
var $3=lz.embed.iframemanager.getFrame($0);if(!$3)return;lz.embed.__setAttr($3,"src",$1)
}else{
var $3=this.getFrameWindow($0);if(!$3)return;$3.location.replace($1)
};this.__srcbyid[$0]=$1;this.__loading[$0]=true
},setPosition:function($0,$1,$2,$3,$4,$5,$6){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["setPosition",$0,$1,$2,$3,$4,$5,$6]);return
};var $7=lz.embed.iframemanager.getFrame($0);if(!$7)return;if($7.appcontainer){
var $8=lz.embed.getAbsolutePosition($7.appcontainer)
}else{
var $8={x:0,y:0}};if($1!=null&&!isNaN($1))$7.style.left=$1+$8.x+"px";if($2!=null&&!isNaN($2))$7.style.top=$2+$8.y+"px";if($3!=null&&!isNaN($3))$7.style.width=$3+"px";if($4!=null&&!isNaN($4))$7.style.height=$4+"px";if($5!=null){
if(typeof $5=="string"){
$5=$5=="true"
};$7.style.display=$5?"block":"none"
};if($6!=null){
this.setZ($0,$6)
}},setVisible:function($0,$1){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["setVisible",$0,$1]);return
};if(typeof $1=="string"){
$1=$1=="true"
};var $2=lz.embed.iframemanager.getFrame($0);if(!$2)return;$2.style.display=$1?"block":"none"
},__topz:100000,bringToFront:function($0,$1){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["bringToFront",$0,$1]);return
};var $2=lz.embed.iframemanager.getFrame($0);if(!$2)return;if($0!==lz.embed.iframemanager.__front){
$2.__zoffset=++this.__topz;lz.embed.iframemanager.__front=$0;this.setZ($0,$1)
}},__bottomz:99000,sendToBack:function($0,$1){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["sendToBack",$0,$1]);return
};var $2=lz.embed.iframemanager.getFrame($0);if(!$2)return;if($0!==lz.embed.iframemanager.__back){
$2.__zoffset=--this.__bottomz;lz.embed.iframemanager.__back=$0;this.setZ($0,$1)
}},setStyle:function($0,$1,$2,$3){
var $4=lz.embed.iframemanager.getFrameWindow($0);if(!$4)return;var $5=$4.document.getElementById($1);if($5){
try{
$5.style[$2]=$3
}
catch($6){}}},asyncCallback:function($0,$1,$2,$3){
var $4=lz.embed.iframemanager.getFrame($0);if(!$4||!$4.__owner)return;if($4.__owner.__iframecallback){
if(typeof $2==="string"){
try{
$2=JSON.parse($2)||$2
}
catch($5){}};$4.__owner.__iframecallback($1,$2)
}else{
if(lz.embed[$4.__owner]){
$2=$2!=null?",'"+$2+"'":"";$2+=$3!=null?","+$3+"":"";lz.embed[$4.__owner].callMethod("lz.embed.iframemanager.__iframecallback('"+$0+"','"+$1+"'"+$2+")")
}else{
return
}}},__gotload:function($0){
var $1=lz.embed.iframemanager.getFrame($0);if(!$1||!$1.__owner)return;if(this.__loading[$0]==true){
this.__loading[$0]=false;if(document.all){
if($1.parentElement){
$1.parentElement.style.display=""
}};if(this.__sendmouseevents[$0]){
this.__setSendMouseEvents($0,true)
};if(this.__calljsqueue[$0]){
this.__playQueue(this.__calljsqueue[$0]);delete this.__calljsqueue[$0]
}};var $2=this.getFrameWindow($0);var $3=$2&&$2.location&&$2.location.href||$1.src;if($3&&this.__srcbyid[$0]!==$3&&$3==='javascript:""'){
return
};setTimeout("lz.embed.iframemanager.asyncCallback('"+$0+"', 'load')",1)
},__refresh:function(){
var $0=lz.embed.iframemanager.__frames;for(var $1 in $0){
var $2=$0[$1];if($2&&$2.style.display=="block"){
$2.style.display="none";$2.style.display="block"
}}},setZ:function($0,$1){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["setZ",$0,$1]);return
};var $2=lz.embed.iframemanager.getFrame($0);if(!$2)return;$1+=$2.__zoffset;$2.style.zIndex=$1
},scrollBy:function($0,$1,$2){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["scrollBy",$0,$1,$2]);return
};var $3=this.getFrameWindow($0);if(!$3)return;$3.scrollBy($1,$2)
},__destroy:function($0){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["__destroy",$0]);return
};var $1=lz.embed.iframemanager.__frames[$0];if($1){
if(this.__sendmouseevents[$0]){
this.__setSendMouseEvents($0,false)
};$1.__owner=null;$1.appcontainer=null;if(document.all){
var $2=document.getElementById($0+"Container");if($2.parentNode){
$2.parentNode.removeChild($2)
}}else if($1.parentNode){
$1.parentNode.removeChild($1)
};delete lz.embed.iframemanager.__frames[$0];delete lz.embed.iframemanager.__srcbyid[$0];delete lz.embed.iframemanager.__ownerbyid[$0]
}},callJavascript:function($0,$1,$2,$3){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["callJavascript",$0,$1,$2,$3]);return
};if(this.__loading[$0]){
if(!this.__calljsqueue[$0]){
this.__calljsqueue[$0]=[]
};this.__calljsqueue[$0].push(["callJavascript",$0,$1,$2,$3]);return
};var $4=lz.embed.iframemanager.getFrameWindow($0);if(!$3)$3=[];try{
var $5=$4.eval($1);if($5){
var $6=$5.apply($4,$3);if($2)$2.execute($6);return $6
}}
catch($7){
window.console&&console.error&&console.error("callJavascript() caught error:",$7)
}},callRPC:function(id,$0,callback,$1){
var $2=lz.embed.iframemanager.getFrameWindow(id);var $3={destination:$2,publicProcedureName:$0,params:$1};if(callback!=null){
if(typeof callback=="number"){
$3.onSuccess=function($0){
lz.embed.iframemanager.asyncCallback(id,"__lzcallback",JSON.stringify($0.returnValue),callback)
}}else{
$3.onSuccess=function($0){
callback($0.returnValue)
}}};pmrpc.call($3)
},__getRPCMethods:function(id){
var iframe=lz.embed.iframemanager.getFrameWindow(id);var $0=function($0){
var $1=[];for(var $2=0,$3=$0.length;$2<$3;$2++){
var $4=$0[$2];if($4.destination===iframe){
$1.push($4.publicProcedureName)
}};var $5=JSON.stringify($1);lz.embed.iframemanager.asyncCallback(id,"__rpcmethods",$5)
};pmrpc.discover({callback:$0})
},__mouseEvent:function($0,$1){
var $2=lz.embed;var $3=$2.iframemanager.getFrame($1);if(!$3)return;if(!$0){
$0=window.event
};var $4="on"+$0.type;if($3.__owner&&$3.__owner.sprite&&$3.__owner.sprite.__mouseEvent){
if($4=="oncontextmenu"){
if(!$2.iframemanager.__hidenativecontextmenu[$1]){
return
}else{
var $5=$2.getAbsolutePosition($3);LzMouseKernel.__sendMouseMove($0,$5.x,$5.y);return LzMouseKernel.__showContextMenu($0)
}};$3.__owner.sprite.__mouseEvent($0);if($4=="onmouseup"){
if(LzMouseKernel.__lastMouseDown==$3.__owner.sprite){
LzMouseKernel.__lastMouseDown=null
}}}else{
if($4=="onmouseleave"){
$4="onmouseout"
}else if($4=="onmouseenter"){
$4="onmouseover"
}else if($4=="oncontextmenu"){
return
};$2.iframemanager.asyncCallback($1,"__mouseevent",$4)
}},setSendMouseEvents:function($0,$1){
if(this.__callqueue[$0]){
this.__callqueue[$0].push(["setSendMouseEvents",$0,$1]);return
};this.__sendmouseevents[$0]=$1
},__setSendMouseEvents:function(id,$0){
var iframemanager=lz.embed.iframemanager;var $1=iframemanager.__sendmouseevents[id]||false;if($0===$1)return;iframemanager.__sendmouseevents[id]=$0;try{
var $2=iframemanager.getFrameWindow(id).document
}
catch($3){
return
};var $4=lz.embed[$0?"attachEventHandler":"removeEventHandler"];$4($2,"mousedown",iframemanager,"__mouseEvent",id);$4($2,"mouseup",iframemanager,"__mouseEvent",id);$4($2,"click",iframemanager,"__mouseEvent",id);$2.oncontextmenu=function($0){
if(!$0)$0=iframe.event;return iframemanager.__mouseEvent($0,id)
};if(lz.embed.browser.isIE){
$4($2,"mouseenter",iframemanager,"__mouseEvent",id);$4($2,"mouseleave",iframemanager,"__mouseEvent",id)
}else{
$4($2,"mouseover",iframemanager,"__mouseEvent",id);$4($2,"mouseout",iframemanager,"__mouseEvent",id)
}},setShowNativeContextMenu:function($0,$1){
this.__hidenativecontextmenu[$0]=!$1
},storeSelection:function($0){
var $1=lz.embed.iframemanager;var $2=$1.getFrameWindow($0);if($2&&$2.document&&$2.document.selection&&$2.document.selection.type=="Text"){
$1.__selectionbookmarks[$0]=$2.document.selection.createRange().getBookmark()
}},restoreSelection:function($0){
var $1=lz.embed.iframemanager;var $2=$1.getFrameWindow($0);if($1.__selectionbookmarks[$0]&&$2){
var $3=$1.__selectionbookmarks[$0];var $4=$2.document.body.createTextRange();$4.moveToBookmark($3);$4.select()
}},__reset:function($0){
if(lz.embed.iframemanager.__counter){
var $1=lz.embed.iframemanager.__ownerbyid;for(var $2 in $1){
if($0===$1[$2]){
lz.embed.iframemanager.__destroy($2)
}}}}};pmrpc.register({publicProcedureName:"asyncCallback",procedure:lz.embed.iframemanager.asyncCallback});