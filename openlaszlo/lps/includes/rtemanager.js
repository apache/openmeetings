var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;if(!this.JSON){
this.JSON={}};(function(){
var f;var quote;var str;f=function($0){
return $0<10?"0"+$0:$0
};quote=function($0){
escapable.lastIndex=0;return( escapable.test($0)?'"'+$0.replace(escapable,function($0){
var $1=meta[$0];return typeof $1==="string"?$1:"\\u"+("0000"+$0.charCodeAt(0).toString(16)).slice(-4)
})+'"':'"'+$0+'"')
};str=function($0,$1){
var $2,$3,$4,$5,$6=gap,$7,$8=$1[$0];if($8&&typeof $8==="object"&&typeof $8.toJSON==="function"){
$8=$8.toJSON($0)
};if(typeof rep==="function"){
$8=rep.call($1,$0,$8)
};switch(typeof $8){
case "string":
return quote($8);
case "number":
return isFinite($8)?String($8):"null";
case "boolean":
case "null":
return String($8);
case "object":
if(!$8){
return "null"
}gap+=indent;$7=[];if(Object.prototype.toString.apply($8)==="[object Array]"){
$5=$8.length;for($2=0;$2<$5;$2+=1){
$7[$2]=str($2,$8)||"null"
};$4=$7.length===0?"[]":(gap?"[\n"+gap+$7.join(",\n"+gap)+"\n"+$6+"]":"["+$7.join(",")+"]");gap=$6;return $4
}if(rep&&typeof rep==="object"){
$5=rep.length;for($2=0;$2<$5;$2+=1){
$3=rep[$2];if(typeof $3==="string"){
$4=str($3,$8);if($4){
$7.push(quote($3)+(gap?": ":":")+$4)
}}}}else{
for($3 in $8){
if(Object.hasOwnProperty.call($8,$3)){
$4=str($3,$8);if($4){
$7.push(quote($3)+(gap?": ":":")+$4)
}}}}$4=$7.length===0?"{}":(gap?"{\n"+gap+$7.join(",\n"+gap)+"\n"+$6+"}":"{"+$7.join(",")+"}");gap=$6;return $4;

}};if(typeof Date.prototype.toJSON!=="function"){
Date.prototype.toJSON=function($0){
return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null
};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function($0){
return this.valueOf()
}};var cx=new RegExp("[\\u0000\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]","g"),escapable=new RegExp('[\\\\\\"\\x00-\\x1f\\x7f-\\x9f\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]',"g"),gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;if(typeof JSON.stringify!=="function"){
JSON.stringify=function($0,$1,$2){
var $3;gap="";indent="";if(typeof $2==="number"){
for($3=0;$3<$2;$3+=1){
indent+=" "
}}else if(typeof $2==="string"){
indent=$2
};rep=$1;if($1&&typeof $1!=="function"&&(typeof $1!=="object"||typeof $1.length!=="number")){
throw new Error("JSON.stringify")
};return str("",{"":$0})
}};if(typeof JSON.parse!=="function"){
JSON.parse=function($0,reviver){
var walk;walk=function($0,$1){
var $2,$3,$4=$0[$1];if($4&&typeof $4==="object"){
for($2 in $4){
if(Object.hasOwnProperty.call($4,$2)){
$3=walk($4,$2);if($3!==undefined){
$4[$2]=$3
}else{
delete $4[$2]
}}}};return reviver.call($0,$1,$4)
};var $1;$0=String($0);cx.lastIndex=0;if(cx.test($0)){
$0=$0.replace(cx,function($0){
return "\\u"+("0000"+$0.charCodeAt(0).toString(16)).slice(-4)
})
};if(new RegExp("^[\\],:{}\\s]*$").test($0.replace(new RegExp('\\\\(?:["\\\\\\/bfnrt]|u[0-9a-fA-F]{4})',"g"),"@").replace(new RegExp('"[^"\\\\\\n\\r]*"|true|false|null|-?\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?',"g"),"]").replace(new RegExp("(?:^|:|,)(?:\\s*\\[)+","g"),""))){
$1=eval("("+$0+")");return typeof reviver==="function"?walk({"":$1},""):$1
};throw new SyntaxError("JSON.parse")
}}})();pmrpc=self.pmrpc=(function(){
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
};register({"publicProcedureName":"receivePingRequest","procedure":$2});register({"publicProcedureName":"getRegisteredProcedures","procedure":$5});reservedProcedureNames={"getRegisteredProcedures":null,"receivePingRequest":null};return {register:register,unregister:unregister,call:call,discover:$6}})();try{
if(lz){}}
catch(e){
lz={}};lz.sendEvent=function(name,value){
if(value){
value=JSON.stringify(value)
};try{
window.parent.lz.embed.iframemanager.asyncCallback(window.name,name,value)
}
catch($0){
(function(){
var callobj={destination:window.parent,publicProcedureName:"asyncCallback",params:[window.name,name,value]};if(window.console&&console.error){
callobj.onError=function($0){
console.error("sendEvent error: ",$0,"with call",callobj)
}};pmrpc.call(callobj)
})()
}};var djConfig={afterOnLoad:true,require:["dojo.parser","dijit.Editor","dijit.layout.ContentPane","dijit.layout.BorderContainer","dijit.form.Form","dijit._editor.range","dijit._editor.plugins.AlwaysShowToolbar","dijit._editor.plugins.LinkDialog","dijit._editor.plugins.Print","dijit._editor.plugins.TextColor","dijit._editor.plugins.FontChoice","dojox.editor.plugins.Smiley","dojox.editor.plugins.ToolbarLineBreak"],debugAtAllCosts:false,addOnLoad:function(){
lz.rte.loader.editor_loaded()
}};lz.rte={};lz.rte.util={loadJavascript:function($0,$1,$2){
var $3=$0.createElement("script");$3.src=$1;$3.type="text/javascript";if($2){
$3.onload=$2;$3.onreadystatechange=$2
};$0.getElementsByTagName("head")[0].appendChild($3)
},loadCSS:function($0,$1){
var $2=$0.createElement("link");$2.rel="stylesheet";$2.type="text/css";$2.href=$1;$0.getElementsByTagName("head")[0].appendChild($2)
}};lz.rte.loader={__loading:false,__loaded:false,__callbacks:[],__dojoroot:"http://ajax.googleapis.com/ajax/libs/dojo/1.5/",__csspath:"dijit/themes/",__jspath:"dojo/dojo.xd.js",loadDojoCSS:function($0,$1){
lz.rte.util.loadCSS($0,lz.rte.loader.__dojoroot+lz.rte.loader.__csspath+$1+"/"+$1+".css");lz.rte.util.loadCSS($0,lz.rte.loader.__dojoroot+"/dojox/editor/plugins/resources/css/Smiley.css");document.body.className+=document.body.className?" "+$1:$1
},loadDojo:function($0,$1,$2){
if(lz.rte.loader.__loading){
lz.rte.loader.__callbacks.push($1);return
}else if(lz.rte.loader.__loaded){
$1.call();return
}else{
lz.rte.loader.__loading=true;if($2){
for(var $3 in $2)djConfig.require.push($2[$3])
};lz.rte.loader.__callbacks.push($1);lz.rte.util.loadJavascript($0,lz.rte.loader.__dojoroot+lz.rte.loader.__jspath)
}},editor_loaded:function(){
dojo.parser.parse();lz.rte.loader.__loaded=true;lz.rte.loader.__loading=false;while(lz.rte.loader.__callbacks.length>0){
var $0=lz.rte.loader.__callbacks.shift();$0.call()
}}};lz.rte.manager={__id:null,__loading:false,__loaded:false,__text:"",__onclick:null,__editing:false,__editor:null,button_counter:0,__theme:"tundra",__locale:null,__plugins:["undo","redo","|","cut","copy","paste","|","bold","italic","underline","strikethrough","|","insertOrderedList","insertUnorderedList","indent","outdent","|","justifyLeft","justifyRight","justifyCenter","justifyFull","|","foreColor","hiliteColor","|","createLink","unlink","insertImage","|","print","smiley","||","fontName","fontSize"],__extraplugins:[],create:function($0){
lz.rte.manager.__id=$0
},__destroy:function(){
lz.rte.manager.rte_stop();if(lz.rte.manager.__editor){
lz.rte.manager.__editor.destroyRecursive();lz.rte.manager.__editor=null;lz.rte.manager.__editing=false;lz.rte.manager.removeAllButtons()
}},__rte_start:function($0){
lz.rte.manager.initialize();lz.rte.manager.__editing=true;var $1=dojo.byId(lz.rte.manager.__id);if($0&&$1)$1.innerHTML=$0;if(lz.rte.manager.__editor){
lz.rte.manager.__editor.open()
}else{
var $2=lz.rte.manager.__plugins;var $3=lz.rte.manager.__extraplugins;$3.push("dijit._editor.plugins.AlwaysShowToolbar");lz.rte.manager.__editor=new (dijit.Editor)({height:"100%",plugins:$2,extraPlugins:$3},$1);dijit.byId("rte_div").resize()
};dojo.connect(lz.rte.manager.__editor,"onChange",lz.rte.manager.onchange);dojo.connect(lz.rte.manager.__editor,"onClick",lz.rte.manager.onchange);dojo.connect(lz.rte.manager.__editor,"onKeyUp",lz.rte.manager.onchange)
},rte_start:function($0){
if(lz.rte.manager.isEditing()||lz.rte.manager.__loading)return;if(lz.rte.manager.isLoaded()){
lz.rte.manager.__rte_start($0)
}else{
lz.rte.manager.__text=$0;lz.rte.manager.initialize()
}},rte_stop:function(){
var $0;if(lz.rte.manager.__editor){
$0=lz.rte.manager.getText();if(lz.rte.manager.isEditing())lz.rte.manager.__editor.close()
};return $0
},setText:function($0){
if(lz.rte.manager.__editor){
var $1=lz.rte.manager.__editor.get("value").length==0;if($0.indexOf("<p")!=0&&$0.indexOf("<P")!=0){
$0="<p>"+$0+"</p>"
};if($1){
lz.rte.manager.__editor.set("value",$0)
}else{
if(lz.rte.manager.__editor.window&&lz.rte.manager.__editor.window.getSelection&&lz.rte.manager.__editor.window.getSelection()==null){
lz.rte.manager.__editor.set("value",$0)
}else lz.rte.manager.__editor.replaceValue($0)
}}},insertHtml:function($0){
if(lz.rte.manager.__editor){
lz.rte.manager.__editor.execCommand("inserthtml",$0)
}},execCommand:function($0,$1){
if(lz.rte.manager.__editor){
if(lz.rte.manager.__editor.queryCommandAvailable($0))lz.rte.manager.__editor.execCommand($0,$1)
}},getText:function(){
var $0;if(lz.rte.manager.__editor){
$0=lz.rte.manager.__editor.get("value");if(($0.indexOf("<p>")==0||$0.indexOf("<P>")==0)&&($0.lastIndexOf("</p>")==$0.length-4||$0.lastIndexOf("</P>")==$0.length-4)){
$0=$0.substr(3,$0.length-7)
}};return $0
},onchange:function($0){
var $1=lz.rte.manager.getText();if($1!=lz.rte.manager.__text){
lz.rte.manager.__text=$1;lz.sendEvent("_text",$1)
}},rte_loaded:function(){
lz.sendEvent("_rte_loaded")
},editor_loaded:function(){
if(lz.rte.manager.isLoaded())return;lz.rte.manager.__loaded=true;lz.rte.manager.__loading=false;lz.rte.manager.__rte_start(lz.rte.manager.__text);lz.sendEvent("_editorready")
},isLoaded:function(){
return lz.rte.manager.__loaded
},isEditing:function(){
return lz.rte.manager.__editing
},set_onclick:function($0){
lz.rte.manager.__onclick=$0
},initialize:function(){
if(lz.rte.manager.isLoaded())return;lz.rte.loader.loadDojoCSS(document,lz.rte.manager.__theme);lz.rte.loader.loadDojo(document,lz.rte.manager.editor_loaded,lz.rte.manager.__extraplugins)
},setDojoPath:function($0,$1,$2){
lz.rte.loader.__dojoroot=$0;lz.rte.loader.__jspath=$1;lz.rte.loader.__csspath=$2;djConfig.debugAtAllCosts=true
},setDojoTheme:function($0){
lz.rte.manager.__theme=$0
},setDojoLocale:function($0){
lz.rte.manager.__locale=$0;djConfig.locale=lz.rte.manager.__locale
},setExtraPlugins:function($0){
var $1=$0.split(",");lz.rte.manager.__extraplugins=$1
},setPlugins:function($0){
var $1=$0.split(",");lz.rte.manager.__plugins=$1
},removeAllButtons:function(){
dojo.empty("rte_buttons");dijit.byId("rte_div").resize()
},addButton:function($0){
var id=window.name+"_rte_button_"+lz.rte.manager.button_counter++;if(dojo.byId(id))dojo.destroy(id);var $1={id:id,type:"button",onclick:function(){
lz.sendEvent("buttonclick",id)
}};for(var $2 in $0){
$1[$2]=$0[$2]
};dojo.create("input",$1,dojo.byId("rte_buttons"),"last");dijit.byId("rte_div").resize();return id
}};