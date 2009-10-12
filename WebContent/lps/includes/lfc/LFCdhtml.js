var $dhtml=true;var $as3=false;var $js1=true;var $swf7=false;var $swf8=false;var $svg=false;var $as2=false;var $swf9=false;var $profile=false;var $runtime="dhtml";var $swf10=false;var $debug=false;var $j2me=false;var _Copyright="Portions of this file are copyright (c) 2001-2008 by Laszlo Systems, Inc.  All rights reserved.";var $modules={};$modules.runtime=this;$modules.lz=$modules.runtime;$modules.user=$modules.lz;var global=$modules.user;var __ES3Globals={Array:Array,Boolean:Boolean,Date:Date,Function:Function,Math:Math,Number:Number,Object:Object,RegExp:RegExp,String:String,Error:Error,EvalError:EvalError,RangeError:RangeError,ReferenceError:ReferenceError,SyntaxError:SyntaxError,TypeError:TypeError,URIError:URIError};var globalValue=function($1){
if($1=="lz"){
return lz
};if($1=="LzMouseKernel"){
return LzMouseKernel
};if($1.charAt(0)=="<"&&$1.charAt($1.length-1)==">"){
return lz[$1.substring(1,$1.length-1)]
};return this[$1]||global[$1]||__ES3Globals[$1]
};var Instance=function(){
this.constructor=arguments.callee;this.$lzsc$initialize.apply(this,arguments)
};Instance.prototype.constructor=Instance;Instance.classname="Instance";Instance.prototype.classname="Object";(function(){
var $1=function($1){
for(var $2=$1.length-1;$2>=1;$2-=2){
var $3=$1[$2];var $4=$1[$2-1];if($3!==void 0||!($4 in this)){
this[$4]=$3
};if(!($3 instanceof Function)){
continue
};var $5=this.constructor;if($3.hasOwnProperty("$superclasses")){
var $6=$3.$superclasses,$7=false;for(var $8=$6.length-1;$8>=0;$8--){
if($6[$8]===$5){
$7=true;break
}};if(!$7){
$3.$superclasses.push($5)
}}else{
if($3.hasOwnProperty("$superclass")&&$3.$superclass!==$5){
var $9=$3.$superclass;delete $3.$superclass;$3.$superclasses=[$9,$5]
}else{
$3.$superclass=$5
}}}};$1.call(Instance.prototype,["addProperties",$1])
})();Instance.prototype.addProperties(["addProperty",function($1,$2){
this.addProperties([$1,$2])
}]);Instance.prototype.addProperty("nextMethod",function(currentMethod,nextMethodName){
var $1;if(currentMethod.hasOwnProperty("$superclass")){
$1=currentMethod.$superclass.prototype[nextMethodName]
}else{
if(currentMethod.hasOwnProperty("$superclasses")){
var $2=currentMethod.$superclasses;for(var $3=$2.length-1;$3>=0;$3--){
var $4=$2[$3];if(this instanceof $4){
$1=$4.prototype[nextMethodName];break
}}}};if(!$1){
$1=function(){

}};return $1
});Instance.prototype.addProperty("$lzsc$initialize",function(){

});var Class={prototype:new Instance(),addProperty:Instance.prototype.addProperty,addProperties:function($1){
this.prototype.addProperties($1)
},addStaticProperty:function($1,$2){
this[$1]=$2
},allClasses:{Instance:Instance},make:function($1,$2,$3,$4){
var superclass=null;if($2 instanceof Array){
for(var $9=$2.length-1;$9>=0;$9--){
var $10=$2[$9];if($10 instanceof Function){
$2.splice($9,1);superclass=$10
}}}else{
if($2 instanceof Function){
superclass=$2;$2=null
}else{

}};if(!superclass){
superclass=Instance
};var $11=function(){
this.constructor=arguments.callee;if(this.$lzsc$initialize!==Instance.prototype.$lzsc$initialize){
this.$lzsc$initialize.apply(this,arguments)
}};$11.constructor=this;$11.classname=$1;var $12=function(){
this.constructor=superclass
};$12.prototype=superclass.prototype;var $13=new $12();if($2 instanceof Array){
for(var $6=$2.length-1;$6>=0;$6--){
var $14=$2[$6];$13=$14.makeInterstitial($13,$6>0?$2[$6-1]:$11)
}};$11.prototype=$13;this.addStaticProperty.call($11,"addStaticProperty",this.addStaticProperty);$11.addStaticProperty("addProperty",this.addProperty);$11.addStaticProperty("addProperties",this.addProperties);if($4){
for(var $6=$4.length-1;$6>=1;$6-=2){
var $7=$4[$6];var $8=$4[$6-1];$11.addStaticProperty($8,$7)
}};if($3){
$11.addProperties($3)
};global[$1]=this.allClasses[$1]=$11;return $11
}};var Mixin={prototype:new Instance(),allMixins:{},addProperty:function($1,$2){
this.prototype[$1]=$2;this.instanceProperties.push($1,$2);var $3=this.implementations;for(var $4 in $3){
var $5=$3[$4];$5.addProperty($1,$2)
}},addStaticProperty:function($1,$2){
this[$1]=$2
},makeInterstitial:function($1,$2){
var $3=this.implementations;var $4=this.classname+"+"+$1.constructor.classname;var $5=$2.classname+"|"+$4;if($3[$5]){
return $3[$5]
};$1.addProperties(this.instanceProperties);var $6=function(){
this.constructor=arguments.callee
};$6.prototype=$1;$6.classname=$4;var $7=new $6();$3[$5]=$7;return $7
},$lzsc$isa:function($1){
var $2=this.implementations;for(var $3 in $2){
if($1 instanceof $2[$3].constructor){
return true
}};return false
},make:function($1,$2,$3,$4){
var $5={constructor:this,classname:$1,_dbg_typename:this._dbg_name,_dbg_name:$1,prototype:$2?$2.make():new Object(),instanceProperties:$2?$2.instanceProperties.slice(0):new Array(),implementations:{}};this.addStaticProperty.call($5,"addStaticProperty",this.addStaticProperty);$5.addStaticProperty("addProperty",this.addProperty);$5.addStaticProperty("makeInterstitial",this.makeInterstitial);$5.addStaticProperty("$lzsc$isa",this.$lzsc$isa);if($4){
for(var $6=$4.length-1;$6>=1;$6-=2){
var $7=$4[$6];var $8=$4[$6-1];$5.addStaticProperty($8,$7)
}};if($3){
for(var $6=$3.length-1;$6>=1;$6-=2){
var $7=$3[$6];var $8=$3[$6-1];$5.addProperty($8,$7)
}};global[$1]=this.allMixins[$1]=$5;return $5
}};Class.make("LzBootstrapMessage",null,["message","","length",0,"$lzsc$initialize",function($1){
switch(arguments.length){
case 0:
$1=null;

};if($1!=null){
this.appendInternal(""+$1,$1)
}},"appendInternal",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};this.message+=$1;this.length=this.message.length
},"append",function(){
var $1=Array.prototype.slice.call(arguments,0);var $2=$1.length;for(var $3=0;$3<$2;$3++){
this.appendInternal(String($1[$3]))
}},"charAt",function($1){
return this.message.charAt($1)
},"charCodeAt",function($1){
return this.message.charCodeAt($1)
},"indexOf",function($1){
return this.message.indexOf($1)
},"lastIndexOf",function($1){
return this.message.lastIndexOf($1)
},"toLowerCase",function(){
return new LzMessage(this.message.toLowerCase())
},"toUpperCase",function(){
return new LzMessage(this.message.toUpperCase())
},"toString",function(){
return this.message||""
},"valueOf",function(){
return this.message||""
},"concat",function(){
var $1=Array.prototype.slice.call(arguments,0);return new LzMessage(this.message.concat.apply(this.message,$1))
},"slice",function(){
var $1=Array.prototype.slice.call(arguments,0);return this.message.slice.apply(this.message,$1)
},"split",function(){
var $1=Array.prototype.slice.call(arguments,0);return this.message.split.apply(this.message,$1)
},"substr",function(){
var $1=Array.prototype.slice.call(arguments,0);return this.message.substr.apply(this.message,$1)
},"substring",function(){
var $1=Array.prototype.slice.call(arguments,0);return this.message.substring.apply(this.message,$1)
},"toHTML",function(){
return this["toString"]().toHTML()
}],["xmlEscapeChars",{"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&apos;"},"xmlEscape",function($1){
if($1&&(typeof $1=="string"||(String["$lzsc$isa"]?String.$lzsc$isa($1):$1 instanceof String))){
var $2=LzBootstrapMessage.xmlEscapeChars;var $3=$1.length;var $4="";for(var $5=0;$5<$3;$5++){
var $6=$1.charCodeAt($5);if($6<32){
$4+="&#x"+$6.toString(16)+";"
}else{
var $7=$1.charAt($5);$4+=$2[$7]||$7
}};return $4
}else{
return $1
}}]);(function($1){
with($1){
with($1.prototype){

}}})(LzBootstrapMessage);var LzMessage=LzBootstrapMessage;String.prototype.toHTML=function(){
return LzMessage.xmlEscape(this)
};Mixin.make("LzFormatter",null,["toNumber",function($1){
return Number($1)
},"pad",function($1,$2,$3,$4,$5,$6,$7){
switch(arguments.length){
case 0:
$1="";
case 1:
$2=0;
case 2:
$3=null;
case 3:
$4=" ";
case 4:
$5="-";
case 5:
$6=10;
case 6:
$7=false;

};var $8=typeof $1=="number";if($8){
if($3!=null){
var $9=Math.pow(10,-$3);$1=Math.round($1/$9)*$9
};$1=Number($1).toString($6);if($5!="-"){
if($1.indexOf("-")!=0){
if($1!=0){
$1=$5+$1
}else{
$1=" "+$1
}}}}else{
$1=""+$1
};var $10=$1.length;if($3!=null){
if($8){
var $11=$1.lastIndexOf(".");if($11==-1){
var $12=0;if($7||$3>0){
$1+="."
}}else{
var $12=$10-($11+1)
};if($12>$3){
$1=$1.substring(0,$10-($12-$3))
}else{
for(var $13=$12;$13<$3;$13++){
$1+="0"
}}}else{
$1=$1.substring(0,$3)
}};$10=$1.length;var $14=false;if($2<0){
$2=-$2;$14=true
};if($10>=$2){
return $1
};if($14){
for(var $13=$10;$13<$2;$13++){
$1=$1+" "
}}else{
$5=null;if($4!=" "){
if(" +-".indexOf($1.substring(0,1))>=0){
$5=$1.substring(0,1);$1=$1.substring(1)
}};for(var $13=$10;$13<$2;$13++){
$1=$4+$1
};if($5!=null){
$1=$5+$1
}};return $1
},"formatToString",function(control){
var $8;var $9;$8=function($1){
if($1>=al){
return null
};return args[$1]
};$9=function($1){

};switch(arguments.length){
case 0:
control="";

};var args=Array.prototype.slice.call(arguments,1);var al=args.length;if(!(typeof control=="string"||(String["$lzsc$isa"]?String.$lzsc$isa(control):control instanceof String))||al>0!=control.indexOf("%")>=0){
args=[control].concat(args);al++;var $1=new LzMessage();for(var $2=0;$2<al;$2++){
var $3=args[$2];var $4=$2==al-1?"\n":" ";$1.append($3);$1.appendInternal($4)
};return $1
};var $5=""+control;var $6=0;var $7=0;var $10=0,$11=$5.length;var $12=0,$13=0;var $1=new LzMessage();while($12<$11){
$13=$5.indexOf("%");if($13==-1){
$1.append($5.substring($12,$11));break
};$1.append($5.substring($12,$13));$10=$13;$12=$13+1;$13=$13+2;var $14="-";var $15=" ";var $16=false;var $17="";var $18=null;var $19=null;var $20=null;while($12<$11&&$19==null){
var $21=$5.substring($12,$13);$12=$13++;switch($21){
case "-":
$17=$21;break;
case "+":
case " ":
$14=$21;break;
case "#":
$16=true;break;
case "0":
if($17===""&&$18===null){
$15=$21;break
};
case "1":
case "2":
case "3":
case "4":
case "5":
case "6":
case "7":
case "8":
case "9":
if($18!==null){
$18+=$21
}else{
$17+=$21
};break;
case "$":
$6=$17-1;$17="";break;
case "*":
if($18!==null){
$18=$8($6);$9($6++)
}else{
$17=$8($6);$9($6++)
};break;
case ".":
$18="";break;
case "h":
case "l":
break;
case "=":
$20=$8($6);$9($6++);break;
default:
$19=$21;break;

}};var $22=$8($6);if($20==null){
$20=$22
};var $23=null;var $24=false;if($18!==null){
$23=1*$18
}else{
switch($19){
case "F":
case "E":
case "G":
case "f":
case "e":
case "g":
$23=6;$24=$16;break;
case "O":
case "o":
if($16&&$22!=0){
$1.append("0")
};break;
case "X":
case "x":
if($16&&$22!=0){
$1.append("0"+$19)
};break;

}};var $25=10;switch($19){
case "o":
case "O":
$25=8;break;
case "x":
case "X":
$25=16;break;

};switch($19){
case "U":
case "O":
case "X":
case "u":
case "o":
case "x":
if($22<0){
$22=-$22;var $26=Math.abs(parseInt($17,10));if(isNaN($26)){
$26=this.toNumber($22).toString($25).length
};var $27=Math.pow($25,$26);$22=$27-$22
};break;

};switch($19){
case "D":
case "U":
case "I":
case "O":
case "X":
case "F":
case "E":
case "G":
$22=this.toNumber($22);$1.appendInternal(this.pad($22,$17,$23,$15,$14,$25,$24).toUpperCase(),$20);$9($6++);break;
case "c":
$22=String.fromCharCode($22);
case "w":

case "s":
var $29;if(Function["$lzsc$isa"]?Function.$lzsc$isa($22):$22 instanceof Function){
if(!$29){
$29="function () {\u2026}"
}}else{
if(typeof $22=="number"){
$29=Number($22).toString($25)
}else{
$29=""+$22
}};$1.appendInternal(this.pad($29,$17,$23,$15,$14,$25,$24),$20);$9($6++);break;
case "d":
case "u":
case "i":
case "o":
case "x":
case "f":
case "e":
case "g":
$22=this.toNumber($22);$1.appendInternal(this.pad($22,$17,$23,$15,$14,$25,$24),$20);$9($6++);break;
case "%":
$1.append("%");break;
default:
$1.append($5.substring($10,$12));break;

};$5=$5.substring($12,$11);$10=0,$11=$5.length;$12=0,$13=0;if($6>$7){
$7=$6
}};if($7<al){
$1.appendInternal(" ");for(;$7<al;$7++){
var $3=$8($7);var $4=$7==al-1?"\n":" ";$1.append($3);$1.appendInternal($4)
}};return $1
}],null);Debug=global["Debug"]||{};Debug.write=function($1){

};Debug.trace=function($1){

};Debug.monitor=function($1){

};Debug.warn=function($1){

};Debug.error=function($1){

};Debug.info=function($1){

};Debug.debug=function($1){

};trace=function(){
console.info.apply(console,arguments)
};Class.make("LzDeclaredEventClass",null,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"ready",false,"sendEvent",function($1){
switch(arguments.length){
case 0:
$1=null;

}},"clearDelegates",function(){

},"removeDelegate",function($1){
switch(arguments.length){
case 0:
$1=null;

}},"getDelegateCount",function(){
return 0
},"toString",function(){
return "LzDeclaredEvent"
}],null);(function($1){
with($1){
with($1.prototype){

}}})(LzDeclaredEventClass);var LzDeclaredEvent=new LzDeclaredEventClass();Class.make("LzValueExpr",null,null,null);Class.make("LzInitExpr",LzValueExpr,null,null);Class.make("LzOnceExpr",LzInitExpr,["methodName",void 0,"$lzsc$initialize",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};this.methodName=$1
}],null);Class.make("LzConstraintExpr",LzOnceExpr,["$lzsc$initialize",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2)
}],null);Class.make("LzAlwaysExpr",LzConstraintExpr,["dependenciesName",void 0,"$lzsc$initialize",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$3);this.dependenciesName=$2
}],null);Class.make("LzStyleExpr",LzValueExpr,["$lzsc$initialize",function(){

}],null);Class.make("LzStyleAttr",LzStyleExpr,["sourceAttributeName",void 0,"$lzsc$initialize",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.sourceAttributeName=$1
}],null);Class.make("LzStyleIdent",LzStyleExpr,["sourceValueID",void 0,"$lzsc$initialize",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.sourceValueID=$1
}],null);function LzInheritedHash($1){
if($1){
for(var $2 in $1){
this[$2]=$1[$2]
}}};var lz;(function(){
if(Object["$lzsc$isa"]?Object.$lzsc$isa(lz):lz instanceof Object){

}else{
if(!lz){
lz=new LzInheritedHash()
}else{

}}})();lz.DeclaredEventClass=LzDeclaredEventClass;lz.Formatter=LzFormatter;lz.colors={aliceblue:15792383,antiquewhite:16444375,aqua:65535,aquamarine:8388564,azure:15794175,beige:16119260,bisque:16770244,black:0,blanchedalmond:16772045,blue:255,blueviolet:9055202,brown:10824234,burlywood:14596231,cadetblue:6266528,chartreuse:8388352,chocolate:13789470,coral:16744272,cornflowerblue:6591981,cornsilk:16775388,crimson:14423100,cyan:65535,darkblue:139,darkcyan:35723,darkgoldenrod:12092939,darkgray:11119017,darkgreen:25600,darkgrey:11119017,darkkhaki:12433259,darkmagenta:9109643,darkolivegreen:5597999,darkorange:16747520,darkorchid:10040012,darkred:9109504,darksalmon:15308410,darkseagreen:9419919,darkslateblue:4734347,darkslategray:3100495,darkslategrey:3100495,darkturquoise:52945,darkviolet:9699539,deeppink:16716947,deepskyblue:49151,dimgray:6908265,dimgrey:6908265,dodgerblue:2003199,firebrick:11674146,floralwhite:16775920,forestgreen:2263842,fuchsia:16711935,gainsboro:14474460,ghostwhite:16316671,gold:16766720,goldenrod:14329120,gray:8421504,green:32768,greenyellow:11403055,grey:8421504,honeydew:15794160,hotpink:16738740,indianred:13458524,indigo:4915330,ivory:16777200,khaki:15787660,lavender:15132410,lavenderblush:16773365,lawngreen:8190976,lemonchiffon:16775885,lightblue:11393254,lightcoral:15761536,lightcyan:14745599,lightgoldenrodyellow:16448210,lightgray:13882323,lightgreen:9498256,lightgrey:13882323,lightpink:16758465,lightsalmon:16752762,lightseagreen:2142890,lightskyblue:8900346,lightslategray:7833753,lightslategrey:7833753,lightsteelblue:11584734,lightyellow:16777184,lime:65280,limegreen:3329330,linen:16445670,magenta:16711935,maroon:8388608,mediumaquamarine:6737322,mediumblue:205,mediumorchid:12211667,mediumpurple:9662683,mediumseagreen:3978097,mediumslateblue:8087790,mediumspringgreen:64154,mediumturquoise:4772300,mediumvioletred:13047173,midnightblue:1644912,mintcream:16121850,mistyrose:16770273,moccasin:16770229,navajowhite:16768685,navy:128,oldlace:16643558,olive:8421376,olivedrab:7048739,orange:16753920,orangered:16729344,orchid:14315734,palegoldenrod:15657130,palegreen:10025880,paleturquoise:11529966,palevioletred:14381203,papayawhip:16773077,peachpuff:16767673,peru:13468991,pink:16761035,plum:14524637,powderblue:11591910,purple:8388736,red:16711680,rosybrown:12357519,royalblue:4286945,saddlebrown:9127187,salmon:16416882,sandybrown:16032864,seagreen:3050327,seashell:16774638,sienna:10506797,silver:12632256,skyblue:8900331,slateblue:6970061,slategray:7372944,slategrey:7372944,snow:16775930,springgreen:65407,steelblue:4620980,tan:13808780,teal:32896,thistle:14204888,tomato:16737095,turquoise:4251856,violet:15631086,wheat:16113331,white:16777215,whitesmoke:16119285,yellow:16776960,yellowgreen:10145074};Class.make("LzCache",null,["size",void 0,"slots",void 0,"destroyable",void 0,"capacity",void 0,"curslot",void 0,"data",null,"$lzsc$initialize",function($1,$2,$3){
switch(arguments.length){
case 0:
$1=16;
case 1:
$2=2;
case 2:
$3=true;

};this.size=$1;this.slots=$2;this.destroyable=$3;this.clear()
},"clear",function(){
this.curslot=0;this.capacity=0;var $1=this.slots;if(!this.data){
this.data=new Array($1)
};var $2=this.data;for(var $3=0;$3<$1;++$3){
if(this.destroyable){
var $4=$2[$3];for(var $5 in $4){
$4[$5].destroy()
}};$2[$3]={}}},"ensureSlot",function(){
if(++this.capacity>this.size){
var $1=(this.curslot+1)%this.slots;var $2=this.data;if(this.destroyable){
var $3=$2[$1];for(var $4 in $3){
$3[$4].destroy()
}};$2[$1]={};this.curslot=$1;this.capacity=1
}},"put",function($1,$2){
var $3=this.get($1);if($3===void 0){
this.ensureSlot()
};this.data[this.curslot][$1]=$2;return $3
},"get",function($1){
var $2=this.slots;var $3=this.curslot;var $4=this.data;for(var $5=0;$5<$2;++$5){
var $6=($3+$5)%$2;var $7=$4[$6][$1];if($7!==void 0){
if($6!=$3){
delete $4[$6][$1];this.ensureSlot();$4[this.curslot][$1]=$7
};return $7
}};return void 0
}],null);Class.make("LzEventable",null,["$lzsc$initialize",function(){

},"__LZdeleted",false,"_events",null,"ondestroy",LzDeclaredEvent,"destroy",function(){
if(this.ondestroy.ready){
this.ondestroy.sendEvent(this)
};this.__LZdeleted=true;this.__LZdelegatesQueue=null;this.__LZdeferDelegates=false;if(this._events!=null){
for(var $1=this._events.length-1;$1>=0;$1--){
this._events[$1].clearDelegates()
}};this._events=null
},"__LZdeferDelegates",false,"__LZdelegatesQueue",null,"childOf",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};return false
},"setAttribute",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=null;

};if(this.__LZdeleted){
return
};var $4="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$4]):this[$4] instanceof Function){
this[$4]($2)
}else{
this[$1]=$2;var $5=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($5):$5 instanceof LzEvent){
if($5.ready){
$5.sendEvent($2)
}}}}],null);lz.Eventable=LzEventable;Class.make("LzStyleAttrBinder",LzEventable,["target",void 0,"dest",void 0,"source",void 0,"$lzsc$initialize",function($1,$2,$3){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.target=$1;this.dest=$2;this.source=$3
},"bind",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.target;var $3=this.dest;var $4=$2[$3];var $5=$2[this.source];if($5!==$4||!$2.inited){
if(!$2.__LZdeleted){
var $lzsc$1014130390="$lzc$set_"+$3;if(Function["$lzsc$isa"]?Function.$lzsc$isa($2[$lzsc$1014130390]):$2[$lzsc$1014130390] instanceof Function){
$2[$lzsc$1014130390]($5)
}else{
$2[$3]=$5;var $lzsc$1735546769=$2["on"+$3];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1735546769):$lzsc$1735546769 instanceof LzEvent){
if($lzsc$1735546769.ready){
$lzsc$1735546769.sendEvent($5)
}}}}}}],null);Class.make("PresentationType",null,null,["accept",function($1){
return $1
},"present",function($1){
return String($1)
}]);Class.make("DefaultPresentationType",PresentationType,null,["accept",PresentationType.accept,"present",function($1){
return $1
}]);Class.make("StringPresentationType",PresentationType,null,["accept",function($1){
return String($1)
},"present",PresentationType.present]);Class.make("BooleanPresentationType",PresentationType,null,["accept",function($1){
switch($1.toLowerCase()){
case "":
case "0":
case "false":
return false;
default:
return true;

}},"present",PresentationType.present]);Class.make("NumberPresentationType",PresentationType,null,["accept",function($1){
return Number($1)
},"present",PresentationType.present]);Class.make("ColorPresentationType",PresentationType,null,["accept",function($1){
return LzColorUtils.hextoint($1)
},"present",function($1){
var $2=lz.colors;for(var $3 in $2){
if($2[$3]===$1){
return $3
}};return LzColorUtils.inttohex($1)
}]);Class.make("ExpressionPresentationType",PresentationType,null,["accept",function($1){
switch($1){
case "undefined":
return void 0;
case "null":
return null;
case "false":
return false;
case "true":
return true;
case "NaN":
return 0/0;
case "Infinity":
return Infinity;
case "-Infinity":
return -Infinity;
case "":
return "";

};if(!isNaN($1)){
return Number($1)
};return String($1)
},"present",PresentationType.present]);Class.make("SizePresentationType",PresentationType,null,["accept",function($1){
if($1=="null"){
return null
};return Number($1)
},"present",PresentationType.present]);Class.make("LzNode",LzEventable,["__LZisnew",false,"__LZdeferredcarr",null,"classChildren",null,"animators",null,"_instanceAttrs",null,"_instanceChildren",null,"__LzValueExprs",null,"__LZhasConstraint",function($1){
return $1 in this.__LzValueExprs
},"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.__LZUID="__U"+ ++LzNode.__UIDs;this.__LZdeferDelegates=true;if($2){
if($2["$lzc$bind_id"]){
this.$lzc$bind_id=$2.$lzc$bind_id;delete $2.$lzc$bind_id
};if($2["$lzc$bind_name"]){
this.$lzc$bind_name=$2.$lzc$bind_name;delete $2.$lzc$bind_name
}};var $5=this.$lzc$bind_id;if($5){
$5.call(null,this)
};var $6=this.$lzc$bind_name;if($6){
$6.call(null,this)
};this._instanceAttrs=$2;this._instanceChildren=$3;var $7=new LzInheritedHash(this["constructor"].attributes);if(!(LzState["$lzsc$isa"]?LzState.$lzsc$isa(this):this instanceof LzState)){
for(var $8 in $7){
var $9=$7[$8];if(!(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($9):$9 instanceof LzInitExpr)){
var $10="$lzc$set_"+$8;if(!this[$10]){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($9):$9 instanceof Function){
this.addProperty($8,$9)
}else{
if($9!==void 0){
this[$8]=$9
}else{
if(this[$8]===void 0){
this[$8]=void 0
}}};delete $7[$8]
}else{
if(this[$8]===void 0){
this[$8]=null
}}}}};if($2){
LzNode.mergeAttributes($2,$7)
};this.__LZisnew=!$4;var $11=this["constructor"]["children"];if(Array["$lzsc$isa"]?Array.$lzsc$isa($11):$11 instanceof Array){
$3=LzNode.mergeChildren($3,$11)
};if($7["datapath"]!=null){
delete $7["$datapath"]
};var $12=this.__LzValueExprs={};for(var $8 in $7){
var $9=$7[$8];if(LzValueExpr["$lzsc$isa"]?LzValueExpr.$lzsc$isa($9):$9 instanceof LzValueExpr){
$12[$8]=$9;delete $7[$8]
}};this.construct($1,$7);if(this.__LZdeleted){
return
};for(var $8 in $12){
$7[$8]=$12[$8]
};this.__LzValueExprs=null;this.__LZapplyArgs($7,true);if(this.__LZdeleted){
return
};this.__LZdeferDelegates=false;var $13=this.__LZdelegatesQueue;if($13){
LzDelegate.__LZdrainDelegatesQueue($13);this.__LZdelegatesQueue=null
};if(this.onconstruct.ready){
this.onconstruct.sendEvent(this)
};if($3&&$3.length){
this.createChildren($3)
}else{
this.__LZinstantiationDone()
}},"oninit",LzDeclaredEvent,"onconstruct",LzDeclaredEvent,"ondata",LzDeclaredEvent,"clonenumber",null,"onclonenumber",LzDeclaredEvent,"__LZinstantiated",false,"__LZpreventSubInit",null,"__LZresolveDict",null,"__LZsourceLocation",null,"__LZUID",null,"__LZPropertyCache",null,"__LZRuleCache",null,"__LZdelegates",null,"isinited",false,"inited",false,"oninited",LzDeclaredEvent,"subnodes",null,"datapath",null,"$lzc$set_datapath",function($1){
if(null!=this.datapath&&$1!==LzNode._ignoreAttribute){
this.datapath.setXPath($1)
}else{
new LzDatapath(this,{xpath:$1})
}},"initstage",null,"$isstate",false,"doneClassRoot",false,"parent",void 0,"cloneManager",null,"name",null,"$lzc$bind_name",null,"id",null,"$lzc$set_id",-1,"$lzc$bind_id",null,"defaultplacement",null,"placement",null,"$lzc$set_placement",-1,"$cfn",0,"immediateparent",null,"dependencies",null,"classroot",void 0,"nodeLevel",0,"__LZstyleBindAttribute",function($1,$2,$3){
var $4=this["__LZPropertyCache"]||LzCSSStyle.getPropertyCache(this);var $6=$4[$2];if(typeof $6=="string"&&$6.length>2&&$6.indexOf("0x")==0&&!isNaN($6)){
$6=Number($6)
};if(LzStyleExpr["$lzsc$isa"]?LzStyleExpr.$lzsc$isa($6):$6 instanceof LzStyleExpr){
if(LzStyleAttr["$lzsc$isa"]?LzStyleAttr.$lzsc$isa($6):$6 instanceof LzStyleAttr){
var $7=$6;var $8=$7.sourceAttributeName;var $9=new LzStyleAttrBinder(this,$1,$8);if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDelegate($9,"bind",this,"on"+$8));$9.bind()
}else{
if(LzStyleIdent["$lzsc$isa"]?LzStyleIdent.$lzsc$isa($6):$6 instanceof LzStyleIdent){
var $10=$6;this.acceptAttribute($1,$3,$10.sourceValueID)
}else{

}}}else{
if(!this.__LZdeleted){
var $lzsc$1537710390="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1537710390]):this[$lzsc$1537710390] instanceof Function){
this[$lzsc$1537710390]($6)
}else{
this[$1]=$6;var $lzsc$1131507241=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1131507241):$lzsc$1131507241 instanceof LzEvent){
if($lzsc$1131507241.ready){
$lzsc$1131507241.sendEvent($6)
}}}}}},"construct",function($1,$2){
this.__LZdelayedSetters=LzNode.__LZdelayedSetters;this.earlySetters=LzNode.earlySetters;var $3=$1;this.parent=$3;if($3){
var $4=$3;if($2["ignoreplacement"]||this.ignoreplacement){
this.placement=null
}else{
var $5=$2["placement"]||$3.defaultplacement;while($5!=null){
if($4.determinePlacement==LzNode.prototype.determinePlacement){
var $6=$4.searchSubnodes("name",$5);if($6==null){
$6=$4
}}else{
var $6=$4.determinePlacement(this,$5,$2)
};$5=$6!=$4?$6.defaultplacement:null;$4=$6
};this.placement=$5
};if(!this.__LZdeleted){
var $7=$4.subnodes;if($7==null){
$4.subnodes=[this]
}else{
$7[$7.length]=this
}};var $8=$4.nodeLevel;this.nodeLevel=$8?$8+1:1;this.immediateparent=$4
}else{
this.nodeLevel=1
}},"init",function(){
return
},"__LZinstantiationDone",function(){
this.__LZinstantiated=true;if(!this.__LZdeleted&&(!this.immediateparent||this.immediateparent.isinited||this.initstage=="early"||this.__LZisnew&&lz.Instantiator.syncNew)){
this.__LZcallInit()
}},"__LZsetPreventInit",function(){
this.__LZpreventSubInit=[]
},"__LZclearPreventInit",function(){
var $1=this.__LZpreventSubInit;this.__LZpreventSubInit=null;var $2=$1.length;for(var $3=0;$3<$2;$3++){
$1[$3].__LZcallInit()
}},"__LZcallInit",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(this.parent&&this.parent.__LZpreventSubInit){
this.parent.__LZpreventSubInit.push(this);return
};this.isinited=true;this.__LZresolveReferences();if(this.__LZdeleted){
return
};var $2=this.subnodes;if($2){
for(var $3=0;$3<$2.length;){
var $4=$2[$3++];var $5=$2[$3];if($4.isinited||!$4.__LZinstantiated){
continue
};$4.__LZcallInit();if(this.__LZdeleted){
return
};if($5!=$2[$3]){
while($3>0){
if($5==$2[--$3]){
break
}}}}};this.init();if(this.oninit.ready){
this.oninit.sendEvent(this)
};if(this.datapath&&this.datapath.__LZApplyDataOnInit){
this.datapath.__LZApplyDataOnInit()
};this.inited=true;if(this.oninited.ready){
this.oninited.sendEvent(true)
}},"completeInstantiation",function(){
if(!this.isinited){
var $1=this.initstage;this.initstage="early";if($1=="defer"){
lz.Instantiator.createImmediate(this,this.__LZdeferredcarr)
}else{
lz.Instantiator.completeTrickle(this)
}}},"ignoreplacement",false,"__LZapplyArgs",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};var $3={};var $4=null;var $5=null;var $6=null;var $7=null;for(var $8 in $1){
var $9=$1[$8];var $10="$lzc$set_"+$8;if($3[$8]||$1[$8]===LzNode._ignoreAttribute){
continue
};$3[$8]=true;if(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($9):$9 instanceof LzInitExpr){
if($9 instanceof LzConstraintExpr){
if($7==null){
$7=[]
};$7.push($9)
}else{
if($9 instanceof LzOnceExpr){
if($6==null){
$6=[]
};$6.push($9)
}else{

}};if(this[$8]===void 0){
this[$8]=null
}}else{
if(!this[$10]){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($9):$9 instanceof Function){
this.addProperty($8,$9)
}else{
if($9!==void 0){
this[$8]=$9
}else{
if(this[$8]===void 0){
this[$8]=void 0
}}};if(!$2){
var $11="on"+$8;if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa(this[$11]):this[$11] instanceof LzEvent){
if(this[$11].ready){
this[$11].sendEvent($1[$8])
}}}}else{
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$10]):this[$10] instanceof Function){
if($8 in this.earlySetters){
if($5==null){
$5=[]
};$5[this.earlySetters[$8]]=$8
}else{
if($4==null){
$4=[]
};$4.push($8)
}}}}};if($5){
for(var $12=1,$13=$5.length;$12<$13;$12++){
if($12 in $5){
if(this.__LZdeleted){
return
};var $8=$5[$12];var $10="$lzc$set_"+$8;this[$10]($1[$8])
}}};if($4){
for(var $12=$4.length-1;$12>=0;$12--){
if(this.__LZdeleted){
return
};var $8=$4[$12];var $10="$lzc$set_"+$8;this[$10]($1[$8])
}};if($6!=null){
this.__LZstoreAttr($6,"$inits")
};if($7!=null){
this.__LZstoreAttr($7,"$constraints")
}},"createChildren",function($1){
if(this.__LZdeleted){
return
};if("defer"==this.initstage){
this.__LZdeferredcarr=$1
}else{
if("late"==this.initstage){
lz.Instantiator.trickleInstantiate(this,$1)
}else{
if(this.__LZisnew&&lz.Instantiator.syncNew||"immediate"==this.initstage){
lz.Instantiator.createImmediate(this,$1)
}else{
lz.Instantiator.requestInstantiation(this,$1)
}}}},"getExpectedAttribute",function($1){
var $2="e_"+$1;if(!this[$2]){
this[$2]={}};if(this[$2].v==null){
return this[$1]
};return this[$2].v
},"setExpectedAttribute",function($1,$2){
var $3="e_"+$1;if(!this[$3]){
this[$3]={}};this[$3].v=$2
},"addToExpectedAttribute",function($1,$2){
var $3="e_"+$1;if(!this[$3]){
this[$3]={}};if(this[$3].v==null){
this[$3].v=this[$1]
};this[$3].v+=$2
},"__LZincrementCounter",function($1){
var $2="e_"+$1;var $3=this[$2];if(!$3){
$3=this[$2]={}};if(!$3.c){
$3.c=0
};$3.c+=1
},"makeChild",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if(this.__LZdeleted){
return
};var $4=$1["class"];if(!$4){
if($1["tag"]){
$4=lz[$1.tag]
};if(!$4&&$1["name"]){
$4=lz[$1.name]
}};var $7;if($4){
$7=new $4(this,$1.attrs,("children" in $1)?$1.children:null,$2)
};return $7
},"$lzc$set_$setters",-1,"$lzc$set_$classrootdepth",function($1){
if(!$1){
return
};var $2=this.parent;while(--$1>0){
$2=$2.parent
};this.classroot=$2
},"dataBindAttribute",function($1,$2,$3){
if(!this.datapath){
this.$lzc$set_datapath(".")
};if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDataAttrBind(this.datapath,$1,$2,$3))
},"__LZdelayedSetters",void 0,"earlySetters",void 0,"$lzc$set_$delegates",function($1){
var $2=[];var $3=$1.length;for(var $4=0;$4<$3;$4+=3){
if($1[$4+2]){
$2.push($1[$4],$1[$4+1],$1[$4+2])
}else{
var $5=$1[$4+1];if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDelegate(this,$5,this,$1[$4]))
}};if($2.length){
this.__LZstoreAttr($2,"$delegates")
}},"__LZstoreAttr",function($1,$2){
if(this.__LZresolveDict==null){
this.__LZresolveDict={}};this.__LZresolveDict[$2]=$1
},"__LZresolveReferences",function(){
var $1=this.__LZresolveDict;if($1!=null){
this.__LZresolveDict=null;var $2=$1["$inits"];if($2!=null){
for(var $3=0,$4=$2.length;$3<$4;$3++){
this[$2[$3].methodName](null);if(this.__LZdeleted){
return
}}};var $5=$1["$constraints"];if($5!=null){
for(var $3=0,$4=$5.length;$3<$4;$3++){
this.applyConstraintExpr($5[$3]);if(this.__LZdeleted){
return
}}};for(var $6 in $1){
if($6=="$inits"||$6=="$constraints"||$6=="$delegates"){
continue
};if($6 in this.__LZdelayedSetters){
this[this.__LZdelayedSetters[$6]]($1[$6])
}else{

}};if($1["$delegates"]){
this.__LZsetDelegates($1.$delegates)
}}},"__LZsetDelegates",function($1){
if($1.length&&!this.__LZdelegates){
this.__LZdelegates=[]
};var $2=$1.length;for(var $3=0;$3<$2;$3+=3){
var $4=$1[$3+2];var $5=$4!=null?this[$4]():null;if($5==null){
$5=this
};var $6=$1[$3+1];this.__LZdelegates.push(new LzDelegate(this,$6,$5,$1[$3]))
}},"applyConstraint",function($1,$2,$3){
var $4="$cf"+this.$cfn++;this.addProperty($4,$2);return this.applyConstraintMethod($4,$3)
},"applyConstraintMethod",function($1,$2){
if($2&&$2.length>0){
if(!this.__LZdelegates){
this.__LZdelegates=[]
};var $3;for(var $4=0,$5=$2.length;$4<$5;$4+=2){
$3=$2[$4];if($3){
var $6=new LzDelegate(this,$1,$3,"on"+$2[$4+1]);this.__LZdelegates.push($6)
}}};this[$1](null)
},"applyConstraintExpr",function($1){
var $2=$1.methodName;var $3=null;if($1 instanceof LzAlwaysExpr){
var $4=$1;var $5=$4.dependenciesName;$3=this[$5]()
};this.applyConstraintMethod($2,$3)
},"releaseConstraint",function($1){
if(this._instanceAttrs!=null){
var $2=this._instanceAttrs[$1];if($2 instanceof LzConstraintExpr){
var $3=$2.methodName;return this.releaseConstraintMethod($3)
}};return false
},"releaseConstraintMethod",function($1){
var $2=false;var $3=this.__LZdelegates;if($3){
for(var $4=0;$4<$3.length;){
var $5=$3[$4];if((LzDelegate["$lzsc$isa"]?LzDelegate.$lzsc$isa($5):$5 instanceof LzDelegate)&&$5.c===this&&$5.m===this[$1]){
$5.unregisterAll();$3.splice($4,1);$2=true
}else{
$4++
}}};return $2
},"$lzc$set_name",function($1){
if(!($1===null||typeof $1=="string")){
return
};var $2=this.name;var $3=this.parent;var $4=this.immediateparent;if($2&&$2!=$1){
if(this.$lzc$bind_name){
if(globalValue($2)===this){
this.$lzc$bind_name.call(null,this,false)
}};if($3){
if($2&&$3[$2]===this){
$3[$2]=null
}};if($4){
if($2&&$4[$2]===this){
$4[$2]=null
}}};if($1&&$1.length){
if($3){
$3[$1]=this
};if($4){
$4[$1]=this
}};this.name=$1
},"defaultSet",function($1,$2){
if($1!=null){
this[$2]=$1
}},"setDatapath",function($1){
this.$lzc$set_datapath($1)
},"data",null,"$lzc$set_data",function($1){
this.data=$1;if(LzDataNodeMixin["$lzsc$isa"]?LzDataNodeMixin.$lzsc$isa($1):$1 instanceof LzDataNodeMixin){
var $2=this.datapath||new LzDatapath(this);$2.setPointer($1)
};if(this.ondata.ready){
this.ondata.sendEvent($1)
}},"setData",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};this.$lzc$set_data($1)
},"applyData",function($1){

},"updateData",function(){
return void 0
},"setSelected",function($1){

},"options",{},"$lzc$set_options",function($1){
if(this.options===this["constructor"].prototype.options){
this.options=new LzInheritedHash(this.options)
};for(var $2 in $1){
this.options[$2]=$1[$2]
}},"getOption",function($1){
return this.options[$1]
},"setOption",function($1,$2){
if(this.options===this["constructor"].prototype.options){
this.options=new LzInheritedHash(this.options)
};this.options[$1]=$2
},"determinePlacement",function($1,$2,$3){
if($2==null){
var $4=null
}else{
var $4=this.searchSubnodes("name",$2)
};return $4==null?this:$4
},"searchImmediateSubnodes",function($1,$2){
var $3=this.subnodes;if($3==null){
return null
};for(var $4=$3.length-1;$4>=0;$4--){
var $5=$3[$4];if($5[$1]==$2){
return $5
}};return null
},"searchSubnodes",function($1,$2){
var $3=this.subnodes?this.subnodes.concat():[];while($3.length>0){
var $4=$3;$3=new Array();for(var $5=$4.length-1;$5>=0;$5--){
var $6=$4[$5];if($6[$1]==$2){
return $6
};var $7=$6.subnodes;if($7){
for(var $8=$7.length-1;$8>=0;$8--){
$3.push($7[$8])
}}}};return null
},"searchParents",function($1){
var $2=this;do{
$2=$2.immediateparent;if($2[$1]!=null){
return $2
}}while($2!=canvas)
},"getUID",function(){
return this.__LZUID
},"childOf",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($1==null){
return false
};var $3=this;while($3.nodeLevel>=$1.nodeLevel){
if($3==$1){
return true
};$3=$3.immediateparent
};return false
},"destroy",function(){
if(this.__LZdeleted==true){
return
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this);if(this.subnodes!=null){
for(var $1=this.subnodes.length-1;$1>=0;$1--){
this.subnodes[$1].destroy()
}};if(this.$lzc$bind_id){
this.$lzc$bind_id.call(null,this,false)
};if(this.$lzc$bind_name){
this.$lzc$bind_name.call(null,this,false)
};var $2=this.parent;var $3=this.name;if($2!=null&&$3!=null){
if($2[$3]===this){
$2[$3]=null
};if(this.immediateparent[$3]===this){
this.immediateparent[$3]==null
}};if(this.__LZdelegates!=null){
for(var $1=this.__LZdelegates.length-1;$1>=0;$1--){
this.__LZdelegates[$1].unregisterAll()
}};this.__LZdelegates=null;if(this.immediateparent&&this.immediateparent.subnodes){
for(var $1=this.immediateparent.subnodes.length-1;$1>=0;$1--){
if(this.immediateparent.subnodes[$1]==this){
this.immediateparent.subnodes.splice($1,1);break
}}};this.data=null
},"animate",function($1,$2,$3,$4,$5){
switch(arguments.length){
case 3:
$4=null;
case 4:
$5=null;

};if($3==0){
var $6=$4?this[$1]+$2:$2;if(!this.__LZdeleted){
var $lzsc$434939085="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$434939085]):this[$lzsc$434939085] instanceof Function){
this[$lzsc$434939085]($6)
}else{
this[$1]=$6;var $lzsc$803441960=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$803441960):$lzsc$803441960 instanceof LzEvent){
if($lzsc$803441960.ready){
$lzsc$803441960.sendEvent($6)
}}}};return null
};var $7={attribute:$1,to:$2,duration:$3,start:true,relative:$4,target:this};for(var $8 in $5){
$7[$8]=$5[$8]
};var $9=new LzAnimator(null,$7);return $9
},"toString",function(){
return this.getDebugIdentification()
},"getDebugIdentification",function(){
var $1=this["constructor"].tagname;if(this["name"]!=null){
$1+=" name: "+this.name
};if(this["id"]!=null){
$1+=" id: "+this.id
};return $1
},"$lzc$set_$datapath",function($1){
if($1===LzNode._ignoreAttribute){
return
}else{
if(!($1 instanceof Object)){

}};this.makeChild($1,true)
},"acceptTypeValue",function($1,$2){
var $3=$1?LzNode.presentationtypes[$1]:null;if($2!=null){
if($3!=null){
$2=$3.accept($2)
}else{
$2=DefaultPresentationType.accept($2)
}};return $2
},"acceptAttribute",function($1,$2,$3){
$3=this.acceptTypeValue($2,$3);if(this[$1]!==$3){
if(!this.__LZdeleted){
var $lzsc$338266500="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$338266500]):this[$lzsc$338266500] instanceof Function){
this[$lzsc$338266500]($3)
}else{
this[$1]=$3;var $lzsc$1573756483=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1573756483):$lzsc$1573756483 instanceof LzEvent){
if($lzsc$1573756483.ready){
$lzsc$1573756483.sendEvent($3)
}}}}}},"presentTypeValue",function($1,$2){
var $3=LzNode.presentationtypes[$1];if($3!=null&&$3["present"]){
$2=$3.present($2)
}else{
$2=PresentationType.present($2)
};return $2
},"presentAttribute",function($1,$2){
return this.presentTypeValue($2,this[$1])
},"$lzc$presentAttribute_dependencies",function($1,$2,$3,$4){
return [$2,$3]
}],["tagname","node","attributes",new LzInheritedHash(),"mergeAttributes",function($1,$2){
for(var $3 in $1){
var $4=$1[$3];if($4===LzNode._ignoreAttribute){
delete $2[$3]
}else{
if(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($4):$4 instanceof LzInitExpr){
$2[$3]=$4
}else{
if(Object["$lzsc$isa"]?Object.$lzsc$isa($4):$4 instanceof Object){
var $5=$2[$3];if(Object["$lzsc$isa"]?Object.$lzsc$isa($5):$5 instanceof Object){
if((Array["$lzsc$isa"]?Array.$lzsc$isa($4):$4 instanceof Array)&&(Array["$lzsc$isa"]?Array.$lzsc$isa($5):$5 instanceof Array)){
$2[$3]=$4.concat($5);continue
}else{
if(($4.constructor===Object||(LzInheritedHash["$lzsc$isa"]?LzInheritedHash.$lzsc$isa($4):$4 instanceof LzInheritedHash))&&($5.constructor===Object||(LzInheritedHash["$lzsc$isa"]?LzInheritedHash.$lzsc$isa($5):$5 instanceof LzInheritedHash))){
var $6=new LzInheritedHash($5);for(var $7 in $4){
$6[$7]=$4[$7]
};$2[$3]=$6;continue
}}}};$2[$3]=$4
}}}},"mergeChildren",function($1,$2){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($2):$2 instanceof Array){
$1=$2.concat((Array["$lzsc$isa"]?Array.$lzsc$isa($1):$1 instanceof Array)?$1:[])
};return $1
},"_ignoreAttribute",{toString:function(){
return "_ignoreAttribute"
}},"__LZdelayedSetters",new LzInheritedHash(),"earlySetters",new LzInheritedHash({name:1,$events:2,$delegates:3,$classrootdepth:4,$datapath:5}),"__UIDs",0,"presentationtypes",{string:StringPresentationType,number:NumberPresentationType,numberExpression:NumberPresentationType,color:ColorPresentationType,"boolean":BooleanPresentationType,inheritableBoolean:BooleanPresentationType,expression:ExpressionPresentationType,size:SizePresentationType}]);lz[LzNode.tagname]=LzNode;Class.make("$lzc$class_userClassPlacement",null,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};$1.defaultplacement=$2
}],null);Class.make("LzDelegate",null,["__delegateID",0,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);if(LzEventable["$lzsc$isa"]?LzEventable.$lzsc$isa($1):$1 instanceof LzEventable){

}else{
if(!(Object["$lzsc$isa"]?Object.$lzsc$isa($1):$1 instanceof Object)){
return
}else{

}};this.c=$1;var m=$1[$2];if(Function["$lzsc$isa"]?Function.$lzsc$isa(m):m instanceof Function){
this.m=m;if(m.length!=1){

}}else{

};if($3!=null){
this.register($3,$4)
};this.__delegateID=LzDelegate.__nextID++
},"c",void 0,"m",void 0,"lastevent",0,"enabled",true,"event_called",false,"execute",function($1){
var $2=this.c;if(this.enabled&&$2){
if($2["__LZdeleted"]){
return
};var $3=this.m;return $3&&$3.call($2,$1)
}},"register",function($1,$2){
if(LzEventable["$lzsc$isa"]?LzEventable.$lzsc$isa($1):$1 instanceof LzEventable){

}else{
if(!(Object["$lzsc$isa"]?Object.$lzsc$isa($1):$1 instanceof Object)){
return
}else{

}};if(this.c["__LZdeleted"]){
return
};var $3=$1[$2];if(!(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($3):$3 instanceof LzEvent)){
$3=new LzEvent($1,$2,this)
}else{
$3.addDelegate(this)
};this[this.lastevent++]=$3
},"unregisterAll",function(){
for(var $1=0;$1<this.lastevent;$1++){
this[$1].removeDelegate(this);this[$1]=null
};this.lastevent=0
},"unregisterFrom",function($1){
var $2=[];for(var $3=0;$3<this.lastevent;$3++){
var $4=this[$3];if($4===$1){
$4.removeDelegate(this)
}else{
$2.push($4)
};this[$3]=null
};this.lastevent=0;var $5=$2.length;for(var $3=0;$3<$5;$3++){
this[this.lastevent++]=$2[$3]
}},"disable",function(){
this.enabled=false
},"enable",function(){
this.enabled=true
},"toString",function(){
return "Delegate for "+this.c+" calls "+this.m+" "+this.__delegateID
}],["__nextID",1,"__LZdrainDelegatesQueue",function($1){
var $2=$1.length;var $3=0;if($3<$2){
var $4=new Array();var $5=new Array();while($3<$2){
var $6=$1[$3];var $7=$1[$3+1];var $8=$1[$3+2];$6.locked=true;$6.ready=false;$5.push($6);if(!$7.event_called){
$7.event_called=true;$4.push($7);if($7.c&&!$7.c.__LZdeleted&&$7.m){
$7.m.call($7.c,$8)
}};$3+=3
};while($7=$4.pop()){
$7.event_called=false
};while($6=$5.pop()){
$6.locked=false;$6.ready=$6.delegateList.length!=0
}};$1.length=0
}]);lz.Delegate=LzDelegate;Class.make("LzEvent",LzDeclaredEventClass,["delegateList",null,"$lzsc$initialize",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);var $4=$1["_events"];if($4==null){
$1._events=[this]
}else{
$4.push(this)
};$1[$2]=this;if($3){
this.delegateList=[$3];this.ready=true
}else{
this.delegateList=[]
}},"locked",false,"addDelegate",function($1){
this.ready=true;this.delegateList.push($1)
},"sendEvent",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(this.locked||!this.ready){
return
};this.locked=true;this.ready=false;var $3=this.delegateList;var $4=$3.length;var $5=new Array();var $6;for(var $7=$4;$7>=0;$7--){
$6=$3[$7];if($6&&$6.enabled&&!$6.event_called){
$6.event_called=true;$5.push($6);var $8=$6.c;if($8&&!$8.__LZdeleted){
if($8.__LZdeferDelegates){
var $9=$8.__LZdelegatesQueue;if(!$9){
$9=$8.__LZdelegatesQueue=new Array()
};$9.push(this,$6,$1)
}else{
if($6.m){
$6.m.call($8,$1)
}}}}};while($6=$5.pop()){
$6.event_called=false
};this.locked=false;this.ready=$3.length!=0
},"removeDelegate",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.delegateList;var $3=$2.length;for(var $4=0;$4<$3;$4++){
if($2[$4]===$1){
$2.splice($4,1);break
}};this.ready=$2.length!=0
},"clearDelegates",function(){
var $1=this.delegateList;while($1.length){
$1[0].unregisterFrom(this)
};this.ready=false
},"getDelegateCount",function(){
return this.delegateList.length
},"toString",function(){
return "LzEvent"
}],null);lz.Event=LzEvent;Class.make("LzKernelUtils",null,null,["CSSDimension",function($1,$2){
switch(arguments.length){
case 1:
$2="px";

};var $3=$1;if(isNaN($1)){
if(typeof $1=="string"&&$1.indexOf("%")==$1.length-1&&!isNaN($1.substring(0,$1.length-1))){
return $1
}else{
$3=0
}}else{
if($1===Infinity){
$3=~0>>>1
}else{
if($1===-Infinity){
$3=~(~0>>>1)
}}};return Math.round($3)+$2
}]);var LzIdleKernel={__callbacks:[],__update:function(){
var $1=LzIdleKernel;var $2=$1.__callbacks;var $3=LzTimeKernel.getTimer();for(var $4=$2.length-2;$4>=0;$4-=2){
var $5=$2[$4];var $6=$2[$4+1];$5[$6]($3)
}},__intervalID:null,addCallback:function($1,$2){
var $3=LzIdleKernel;var $4=$3.__callbacks.slice(0);for(var $5=$4.length-2;$5>=0;$5-=2){
if($4[$5]===$1&&$4[$5+1]==$2){
return
}};$4.push($1,$2);$3.__callbacks=$4;if($4.length>0&&$3.__intervalID==null){
$3.__intervalID=setInterval(LzIdleKernel.__update,1000/$3.__fps)
}},removeCallback:function($1,$2){
var $3=LzIdleKernel;var $4=$3.__callbacks.slice(0);for(var $5=$4.length-2;$5>=0;$5-=2){
if($4[$5]===$1&&$4[$5+1]==$2){
var $6=$4.splice($5,2)
}};$3.__callbacks=$4;if($4.length==0&&$3.__intervalID!=null){
clearInterval($3.__intervalID);$3.__intervalID=null
};return $6
},__fps:30,setFrameRate:function($1){
LzIdleKernel.__fps=$1;if(LzIdleKernel.__intervalID!=null){
clearInterval(LzIdleKernel.__intervalID);LzIdleKernel.__intervalID=setInterval(LzIdleKernel.__update,1000/$1)
}}};Class.make("LzLibraryCleanup",LzNode,["lib",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"$lzc$set_libname",function($1){
this.lib=LzLibrary.findLibrary($1);this.lib.loadfinished()
}],["attributes",new LzInheritedHash(LzNode.attributes)]);var LzResourceLibrary={};var getTimer=function(){
return LzTimeKernel.getTimer()
};global=window;lz.BrowserUtils={__scopeid:0,__scopes:[],getcallbackstr:function($1,$2){
var $3=lz.BrowserUtils.__scopeid++;if($1.__callbacks==null){
$1.__callbacks={sc:$3}}else{
$1.__callbacks[$3]=$3
};lz.BrowserUtils.__scopes[$3]=$1;return "if (lz.BrowserUtils.__scopes["+$3+"]) lz.BrowserUtils.__scopes["+$3+"]."+$2+".apply(lz.BrowserUtils.__scopes["+$3+"], [])"
},getcallbackfunc:function($1,name,args){
var sc=lz.BrowserUtils.__scopeid++;if($1.__callbacks==null){
$1.__callbacks={sc:sc}}else{
$1.__callbacks[sc]=sc
};lz.BrowserUtils.__scopes[sc]=$1;return( function(){
var $1=lz.BrowserUtils.__scopes[sc];if($1){
return $1[name].apply($1,args)
}})
},removecallback:function($1){
if($1.__callbacks!=null){
for(var $2 in $1.__callbacks){
var $3=$1.__callbacks[$2];delete lz.BrowserUtils.__scopes[$3]
};delete $1.__callbacks
}},hasFeature:function($1,$2){
return document.implementation&&document.implementation.hasFeature&&document.implementation.hasFeature($1,$2)
}};var LzPool=function($1,$2,$3,$4){
this.cache={};if(typeof $1=="function"){
this.getter=$1
};if(typeof $2=="function"){
this.cacheHit=$2
};if(typeof $3=="function"){
this.destroyer=$3
};if($4){
this.owner=$4
}};LzPool.prototype.cache=null;LzPool.prototype.get=function($1,$2){
var $3=Array.prototype.slice.call(arguments,2);var $4=this.cache[$1];if($2||$4==null){
$3.unshift($1);$4=this.getter.apply(this,$3);if(!$2){
this.cache[$1]=$4
}}else{
if(this.cacheHit){
$3.unshift($1,$4);this.cacheHit.apply(this,$3)
}};if(this.owner){
$4.owner=this.owner
};return $4
};LzPool.prototype.flush=function($1){
if(this.destroyer){
this.destroyer($1,this.cache[$1])
};delete this.cache[$1]
};LzPool.prototype.destroy=function(){
for(var $1 in this.cache){
this.flush($1)
};this.owner=null;this.cache=null
};LzPool.prototype.getter=null;LzPool.prototype.destroyer=null;LzPool.prototype.cacheHit=null;var LzKeyboardKernel={__downKeysHash:{},__keyboardEvent:function($1){
if(!$1){
$1=window.event
};var $2={};var $3=false;var $4=$1["keyCode"];var $5=$1.type;var $6=LzKeyboardKernel.__downKeysHash;if($4>=0&&$4!=16&&$4!=17&&$4!=18&&$4!=224){
var $7=String.fromCharCode($4).toLowerCase();if($5=="keyup"){
if($6[$7]!=null){
$2[$7]=false;$3=true
};$6[$7]=null
}else{
if($5=="keydown"){
if($6[$7]==null){
$2[$7]=true;$3=true
};$6[$7]=$4
}}};if(LzKeyboardKernel.__updateControlKeys($1,$2)){
$3=true
};if($3){
var $8=LzKeyboardKernel.__scope;var $9=LzKeyboardKernel.__callback;if($8&&$8[$9]){
$8[$9]($2,$4,"on"+$5)
}};if($4>=0){
if($4==9){
$1.cancelBubble=true;return false
}else{
if(LzKeyboardKernel.__cancelKeys&&($4==13||$4==0||$4==37||$4==38||$4==39||$4==40||$4==8)){
$1.cancelBubble=true;return false
}}}},__updateControlKeys:function($1,$2){
var $3=LzSprite.quirks;var $4=LzKeyboardKernel.__downKeysHash;var $5=false;if($2){
var $6=false
}else{
$2={};var $6=true
};var $7=$1["altKey"];if($4["alt"]!=null!=$7){
$4["alt"]=$7?18:null;$2["alt"]=$7;$5=true;if($3["alt_key_sends_control"]){
$2["control"]=$2["alt"]
}};var $8=$1["ctrlKey"];if($4["control"]!=null!=$8){
$4["control"]=$8?17:null;$2["control"]=$8;$5=true
};var $9=$1["shiftKey"];if($4["shift"]!=null!=$9){
$4["shift"]=$9?16:null;$2["shift"]=$9;$5=true
};if($3["hasmetakey"]){
var $10=$1["metaKey"];if($4["meta"]!=null!=$10){
$4["meta"]=$10?224:null;$2["meta"]=$10;$5=true;$2["control"]=$10;if(!$10){
LzKeyboardKernel.__allKeysUp();$5=false
}}};if($5&&$6){
var $11=LzKeyboardKernel.__scope;var $12=LzKeyboardKernel.__callback;if($11&&$11[$12]){
$11[$12]($2,0,"on"+$1.type)
}};return $5
},__allKeysUp:function(){
var $1=null;var $2=false;var $3=null;var $4=LzKeyboardKernel.__downKeysHash;for(var $5 in $4){
if($4[$5]!=null){
$2=true;if(!$1){
$1={}};$1[$5]=false;if($5.length==1){
if(!$3){
$3=[]
};$3.push($4[$5])
};$4[$5]=null
}};var $6=LzKeyboardKernel.__scope;var $7=LzKeyboardKernel.__callback;if($2&&$6&&$6[$7]){
if(!$3){
$6[$7]($1,0,"onkeyup")
}else{
for(var $8=0,$9=$3.length;$8<$9;$8++){
$6[$7]($1,$3[$8],"onkeyup")
}}};LzKeyboardKernel.__downKeysHash={}},__callback:null,__scope:null,__cancelKeys:true,__lockFocus:null,setCallback:function($1,$2){
this.__scope=$1;this.__callback=$2
},setKeyboardControl:function($1,$2){
if(!$2&&LzKeyboardKernel.__lockFocus){
$1=true
};var $3=null;var $4=lz&&lz.embed&&lz.embed.options&&lz.embed.options.cancelkeyboardcontrol!=true||true;if($4&&$1){
$3=LzKeyboardKernel.__keyboardEvent
};if(LzSprite.quirks.keyboardlistentotop){
var $5=window.top.document
}else{
var $5=document
};$5.onkeydown=$3;$5.onkeyup=$3;$5.onkeypress=$3
},gotLastFocus:function(){
if(!LzSprite.__mouseActivationDiv.mouseisover){
LzKeyboardKernel.setKeyboardControl(false)
}},setGlobalFocusTrap:function($1){
LzKeyboardKernel.__lockFocus=$1;if(LzSprite.quirks.activate_on_mouseover){
var $2=LzSprite.__mouseActivationDiv;if($1){
$2.onmouseover()
}else{
$2.onmouseout()
}}}};var LzMouseKernel={__lastMouseDown:null,__lastMouseOver:null,__x:0,__y:0,owner:null,__showncontextmenu:null,__mouseEvent:function($1){
if(!$1){
$1=window.event
};var $2=$1["target"]?$1.target:$1["srcElement"];var $3="on"+$1.type;if(window["LzKeyboardKernel"]&&LzKeyboardKernel["__updateControlKeys"]){
LzKeyboardKernel.__updateControlKeys($1)
};var $4=window["LzInputTextSprite"]&&LzInputTextSprite.prototype;if($4&&$4.__lastshown!=null){
if(LzSprite.quirks.fix_ie_clickable){
$4.__hideIfNotFocused($3,$2)
}else{
if($3!="onmousemove"){
$4.__hideIfNotFocused()
}}};if($3=="onmousemove"){
LzMouseKernel.__sendMouseMove($1);if($4&&$4.__lastshown!=null){
if($2&&$2.owner&&!($2.owner instanceof LzInputTextSprite)){
if(!$4.__lastshown.__isMouseOver()){
$4.__lastshown.__hide()
}}}}else{
if($3=="oncontextmenu"||$1.button==2){
LzMouseKernel.__sendMouseMove($1);if(LzSprite.prototype.quirks.has_dom2_mouseevents){
if($3=="oncontextmenu"){
var $5=LzMouseKernel.__findContextMenu($1);if($5!=null){
return false
}else{
return true
}}else{
if($3=="onmousedown"){
if($2){
return LzMouseKernel.__showContextMenu($1)
}}}}else{
if($3=="oncontextmenu"){
if($2){
return LzMouseKernel.__showContextMenu($1)
}}}}else{
LzMouseKernel.__sendEvent($3)
}}},__sendEvent:function($1,$2){
if($1=="onclick"||$1=="onmousedown"||$1=="onmouseup"){
if(LzMouseKernel.__showncontextmenu){
LzMouseKernel.__showncontextmenu.__hide()
}};if($1=="onclick"&&$2==null){
return
};if(LzMouseKernel.__callback){
LzMouseKernel.__scope[LzMouseKernel.__callback]($1,$2)
}},__callback:null,__scope:null,__mouseupEvent:function($1){
if(LzMouseKernel.__lastMouseDown!=null){
LzMouseKernel.__lastMouseDown.__globalmouseup($1)
}else{
if(!$1){
$1=window.event
};var $2=$1["target"]?$1.target:$1["srcElement"];if($2&&$2.owner!==LzSprite.__rootSprite){
return
};LzMouseKernel.__mouseEvent($1)
}},setCallback:function($1,$2){
this.__scope=$1;this.__callback=$2
},__mousecontrol:false,setMouseControl:function($1){
if($1==LzMouseKernel.__mousecontrol){
return
};LzMouseKernel.__mousecontrol=$1;if($1){
lz.embed.attachEventHandler(document,"mousemove",LzMouseKernel,"__mouseEvent");lz.embed.attachEventHandler(document,"mousedown",LzMouseKernel,"__mouseEvent");lz.embed.attachEventHandler(document,"mouseup",LzMouseKernel,"__mouseupEvent");lz.embed.attachEventHandler(document,"click",LzMouseKernel,"__mouseEvent");try{
if(window.top!=window){
lz.embed.attachEventHandler(window.top.document,"mouseup",LzMouseKernel,"__mouseupEvent")
}}
catch(e){

}}else{
lz.embed.removeEventHandler(document,"mousemove",LzMouseKernel,"__mouseEvent");lz.embed.removeEventHandler(document,"mousedown",LzMouseKernel,"__mouseEvent");lz.embed.removeEventHandler(document,"mouseup",LzMouseKernel,"__mouseupEvent");lz.embed.removeEventHandler(document,"click",LzMouseKernel,"__mouseEvent");try{
if(window.top!=window){
lz.embed.removeEventHandler(window.top.document,"mouseup",LzMouseKernel,"__mouseupEvent")
}}
catch(e){

}};document.oncontextmenu=$1?LzMouseKernel.__mouseEvent:null
},__showhand:"pointer",showHandCursor:function($1){
var $2=$1==true?"pointer":"default";this.__showhand=$2;LzMouseKernel.setCursorGlobal($2)
},setCursorGlobal:function($1){
if(LzSprite.quirks.no_cursor_colresize){
return
};var $1=LzSprite.__defaultStyles.hyphenate($1);LzSprite.prototype.__setCSSClassProperty(".lzclickdiv","cursor",$1);LzSprite.prototype.__setCSSClassProperty(".lzdiv","cursor",$1);LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv","cursor",$1);LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv","cursor",$1)
},restoreCursor:function(){
if(LzSprite.quirks.no_cursor_colresize){
return
};if(LzMouseKernel.__amLocked){
return
};LzSprite.prototype.__setCSSClassProperty(".lzclickdiv","cursor",LzMouseKernel.__showhand);LzSprite.prototype.__setCSSClassProperty(".lzdiv","cursor","default");LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv","cursor","default");LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv","cursor","default")
},lock:function(){
LzMouseKernel.__amLocked=true
},unlock:function(){
LzMouseKernel.__amLocked=false;LzMouseKernel.restoreCursor()
},disableMouseTemporarily:function(){
this.setGlobalClickable(false);this.__resetonmouseover=true
},__resetonmouseover:false,__resetMouse:function(){
if(this.__resetonmouseover){
this.__resetonmouseover=false;this.setGlobalClickable(true);var $1=this.__cachedSelection;if($1){
var $2=$1.s;$2.setSelection($1.st,$1.st+$1.sz);$1=null
}}},__cachedSelection:null,__globalClickable:true,setGlobalClickable:function($1){
var $2=document.getElementById("lzcanvasclickdiv");this.__globalClickable=$1;$2.style.display=$1?"":"none"
},__sendMouseMove:function($1,$2,$3){
if($1.pageX||$1.pageY){
LzMouseKernel.__x=$1.pageX;LzMouseKernel.__y=$1.pageY
}else{
if($1.clientX||$1.clientY){
var $4=document.body,$5=document.documentElement;LzMouseKernel.__x=$1.clientX+$4.scrollLeft+$5.scrollLeft;LzMouseKernel.__y=$1.clientY+$4.scrollTop+$5.scrollTop
}};if($2){
LzMouseKernel.__x+=$2
};if($3){
LzMouseKernel.__y+=$3
};if($1.type=="mousemove"){
LzMouseKernel.__sendEvent("onmousemove")
}},__showContextMenu:function($1){
var $2=LzMouseKernel.__findContextMenu($1);if($2){
$2.kernel.__show();return $2.kernel.showbuiltins
}},__findContextMenu:function($1){
var $2=LzSprite.__rootSprite.__contextmenu;var $3=LzSprite.quirks;if(document.elementFromPoint){
var $4=$3.swf8_contextmenu;var $5=LzMouseKernel.__x;var $6=LzMouseKernel.__y;var $7=LzSprite.__rootSprite.__LZdiv;var $8=[];if($3.fix_contextmenu){
$8.push($7,$7.style.display);var $9=$7.style.zIndex;$7.style.zIndex=-1000;var $10=LzSprite.__rootSprite.__LZclickcontainerdiv;var $11=$10.style.zIndex;$8.push($10,$10.style.display);$10.style.zIndex=-9999
};do{
var $12=document.elementFromPoint($5,$6);if(!$12){
break
}else{
var $13=$12.owner;if(!$13){

}else{
if($13.__contextmenu){
$2=$13.__contextmenu;break
}else{
if($3.ie_elementfrompoint&&$13.scrolldiv===$12){

}else{
if($4&&($13.__LZdiv===$12&&$13.bgcolor!=null||$13 instanceof LzTextSprite)){
break
}}}};$8.push($12,$12.style.display);$12.style.display="none"
}}while($12!==$7&&$12.tagName!="HTML");for(var $14=$8.length-1;$14>=0;$14-=2){
$8[$14-1].style.display=$8[$14]
};if($3.fix_contextmenu){
$7.style.zIndex=$9;$10.style.zIndex=$11
}}else{
var $15=($1.srcElement||$1.target).owner;if($15){
while($15.__parent){
if($15.__contextmenu){
var $16=$15.getMouse();if($16.x>=0&&$16.x<$15.width&&$16.y>=0&&$16.y<$15.height){
$2=$15.__contextmenu;break
}};$15=$15.__parent
}}};return $2
}};Class.make("LzBrowserKernel",null,null,["loadURL",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;

};if($2!=null){
if($3!=null){
window.open($1,$2,$3)
}else{
window.open($1,$2)
}}else{
window.location=$1
}},"loadJS",function($1,$2){
LzBrowserKernel.loadURL("javascript:"+$1+";void(0);",$2)
},"callJS",function($1,$2){
var $3=null;var $4=eval($1);var $5=$1.split(".");if($5.length>1){
$5.pop();$3=eval($5.join("."))
};var $6=Array.prototype.slice.call(arguments,2);if($4){
var $7=$4.apply($3,$6)
};if($2&&typeof $2=="function"){
$2($7)
};return $7
},"setHistory",function($1){
lz.embed.history.set($1)
},"callMethod",function($1){
return eval($1)
},"getVersion",function(){
return navigator.userAgent
},"getOS",function(){
return navigator.platform
},"getLoadURL",function(){
if(LzSprite.__rootSprite&&LzSprite.__rootSprite._url){
var $1=LzSprite.__rootSprite._url
}else{
var $1=lz.embed.__propcache.url
};if(!$1){
$1=new String(window.location)
};var $2=$1.indexOf(":");var $3=$1.indexOf("/");if($2>-1){
if($1.indexOf("://")==$2){
return $1
}else{
if($1.charAt($2+1)=="/"){
$1=$1.substring(0,$2+1)+"/"+$1.substring($2+1);return $1
}else{
var $4=new LzURL(new String(window.location));$1=$1.substring(0,$2+1)+"/"+$4.path+$1.substring($2+1);return $1
}}}else{
if($3==0){
return $1
}else{
var $5=new String(window.location);var $6=$5.lastIndexOf("/");$5=$5.substring(0,$6+1);return $5+$1
}}},"getInitArg",function($1){
return global[$1]
},"getAppID",function(){
return LzSprite.__rootSprite._id
},"isAAActive",function(){
return false
}]);var LzSprite=function($1,$2){
if($1==null){
return
};this.constructor=arguments.callee;this.owner=$1;this.uid=LzSprite.prototype.uid++;this.aadescriptionDiv=null;var $3=this.quirks;if($2){
this.isroot=true;this.__initdone=false;LzSprite.__rootSprite=this;var div=document.createElement("div");div.className="lzcanvasdiv";if($3.ie6_improve_memory_performance){
try{
document.execCommand("BackgroundImageCache",false,true)
}
catch(err){

}};var $4=lz.embed.__propcache;var $5=LzSprite.__rootSpriteContainer=$4.appenddiv;var $6=$5;$5.style.margin=0;$5.style.padding=0;$5.style.border="0 none";$5.style.overflow="hidden";if($3["container_divs_require_overflow"]){
$6=document.createElement("div");$6.className="lzappoverflow";$5.appendChild($6);$6.owner=this;LzSprite.__rootSpriteOverflowContainer=$6
};if($3.fix_contextmenu){
var $7=document.createElement("div");$7.className="lzcanvascontextdiv";$7.id="lzcanvascontextdiv";$6.appendChild($7);$7.owner=this;this.__LZcontextcontainerdiv=$7
};if($4.bgcolor){
div.style.backgroundColor=$4.bgcolor;this.bgcolor=$4.bgcolor
};if($4.id){
this._id=$4.id
};if($4.url){
this._url=$4.url
};if($4.cancelkeyboardcontrol){
lz.embed.options.cancelkeyboardcontrol=$4.cancelkeyboardcontrol
};if($4.serverroot){
lz.embed.options.serverroot=$4.serverroot
};lz.embed.options.approot=typeof $4.approot=="string"?$4.approot:"";$6.appendChild(div);this.__LZdiv=div;if($3.fix_clickable){
var $8=document.createElement("div");$8.className="lzcanvasclickdiv";$8.id="lzcanvasclickdiv";$6.appendChild($8);this.__LZclickcontainerdiv=$8
};if($3["css_hide_canvas_during_init"]){
var $9="display";var $10="none";if($3["safari_visibility_instead_of_display"]){
$9="visibility";$10="hidden"
};this.__LZdiv.style[$9]=$10;if($3["fix_clickable"]){
this.__LZclickcontainerdiv.style[$9]=$10
};if($3["fix_contextmenu"]){
this.__LZcontextcontainerdiv.style[$9]=$10
}};if($3.activate_on_mouseover){
div.mouseisover=false;div.onmouseover=function($1){
if(LzSprite.quirks.focus_on_mouseover){
if(LzSprite.prototype.getSelectedText()==""){
div.focus()
}};if(LzInputTextSprite.prototype.__focusedSprite==null){
LzKeyboardKernel.setKeyboardControl(true)
};LzMouseKernel.setMouseControl(true);this.mouseisover=true
};div.onmouseout=function($1){
if(!$1){
$1=window.event;var $2=$1.toElement
}else{
var $2=$1.relatedTarget
};var $3=LzSprite.quirks;if($3.inputtext_anonymous_div){
try{
$2&&$2.parentNode
}
catch($1){
return
}};var $4=false;if($2){
var $5=LzContextMenuKernel.lzcontextmenu;if($2.owner&&$2.className.indexOf("lz")==0){
$4=true
}else{
if($5&&($2===$5||$2.parentNode===$5)){
$4=true
}}};if($4){
var $6=LzMouseKernel.__globalClickable;if($3.fix_ie_clickable){
LzMouseKernel.setGlobalClickable(true)
};if($3.focus_on_mouseover){
if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.prototype.getSelectedText()==""){
div.focus()
}}};LzKeyboardKernel.setKeyboardControl(true);LzMouseKernel.setMouseControl(true);LzMouseKernel.__resetMouse();this.mouseisover=true;if($3.fix_clickable&&!$6&&LzMouseKernel.__globalClickable){
var $7=$1["target"]?$1.target:$1["srcElement"];if($7){
var $8=$7["owner"];if(LzSprite["$lzsc$isa"]?LzSprite.$lzsc$isa($8):$8 instanceof LzSprite){
$8=$8["owner"]
};if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($8):$8 instanceof LzView){
LzMouseKernel.__sendEvent("onmouseout",$8)
}}}}else{
if($3.focus_on_mouseover){
if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.prototype.getSelectedText()==""){
div.blur()
}}};LzKeyboardKernel.setKeyboardControl(false);LzMouseKernel.setMouseControl(false);this.mouseisover=false
}};LzSprite.__mouseActivationDiv=div
}}else{
this.__LZdiv=document.createElement("div");this.__LZdiv.className="lzdiv";if($3.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lzdiv"
}};this.__LZdiv.owner=this;if($3.fix_clickable){
this.__LZclickcontainerdiv.owner=this
};if($3.ie_leak_prevention){
this.__sprites[this.uid]=this
}};LzSprite.__defaultStyles={lzdiv:{position:"absolute"},lzclickdiv:{position:"absolute"},lzcanvasdiv:{position:"absolute"},lzcanvasclickdiv:{zIndex:100000,position:"absolute"},lzcanvascontextdiv:{position:"absolute"},lzappoverflow:{position:"absolute",overflow:"hidden"},lztextcontainer:{position:"absolute",overflow:"hidden",paddingTop:"2px",paddingRight:"2px",paddingBottom:"2px",paddingLeft:"2px",cursor:"default"},lztextcontainer_click:{position:"absolute",paddingTop:"2px",paddingRight:"2px",paddingBottom:"2px",paddingLeft:"2px",cursor:"default"},lzinputtextcontainer:{position:"absolute",overflow:"hidden",paddingTop:"0px",paddingRight:"3px",paddingBottom:"4px",paddingLeft:"1px"},lzinputtextcontainer_click:{position:"absolute",paddingTop:"0px",paddingRight:"3px",paddingBottom:"4px",paddingLeft:"1px"},lzinputtextmultilinecontainer:{position:"absolute",overflow:"hidden",paddingTop:"1px",paddingRight:"3px",paddingBottom:"3px",paddingLeft:"1px"},lzinputtextmultilinecontainer_click:{position:"absolute",paddingTop:"1px",paddingRight:"3px",paddingBottom:"3px",paddingLeft:"1px"},lztext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",whiteSpace:"normal",position:"absolute",overflow:"hidden",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none"},lzswftext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",whiteSpace:"normal",position:"absolute",overflow:"hidden",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0.025em",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all"},lzinputtext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none"},lzswfinputtext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0.025em",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all",outline:"none",resize:"none"},lzswfinputtextmultiline:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",overflow:"hidden",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0.025em",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all",outline:"none",resize:"none"},lztextlink:{cursor:"pointer"},lzaccessibilitydiv:{display:"none"},lzcontext:{position:"absolute"},lzimg:{position:"absolute",backgroundRepeat:"no-repeat"},"#lzcontextmenu div.separator":{borderTop:"1px solid #808080",borderLeft:"none",borderRight:"none",borderBottom:"1px solid #d4d0c8",margin:"7px 0px"},"#lzTextSizeCache":{zoom:1},"#lzcontextmenu a":{color:"#000",display:"block",textDecoration:"none",cursor:"default"},"#lzcontextmenu a:hover":{color:"#FFF",backgroundColor:"#333"},"#lzcontextmenu a.disabled":{color:"#999 !important"},"#lzcontextmenu":{position:"absolute",zIndex:10000000,backgroundColor:"#CCC",border:"1px outset #999",padding:"4px",fontFamily:"Verdana,Vera,sans-serif",fontSize:"13px","float":"left",margin:"2px",minWidth:"100px"},writeCSS:function(){
var $1=[];var $2="";for(var $3 in this){
if($3=="writeCSS"||$3=="hyphenate"||$3=="__replace"||$3=="__re"){
continue
};$2+=$3.indexOf("#")==-1?".":"";$2+=$3+"{";for(var $4 in this[$3]){
var $5=this[$3][$4];$2+=this.hyphenate($4)+":"+$5+";"
};$2+="}"
};document.write('<style type="text/css">'+$2+"</style>")
},__re:new RegExp("[A-Z]","g"),hyphenate:function($1){
return $1.replace(this.__re,this.__replace)
},__replace:function($1){
return "-"+$1.toLowerCase()
}};LzSprite.prototype.uid=0;LzSprite.quirks={fix_clickable:true,fix_ie_background_height:false,fix_ie_clickable:false,ie_alpha_image_loader:false,ie_leak_prevention:false,prevent_selection:false,ie_elementfrompoint:false,invisible_parent_image_sizing_fix:false,emulate_flash_font_metrics:true,inner_html_strips_newlines:true,inner_html_no_entity_apos:false,css_hide_canvas_during_init:true,firefox_autocomplete_bug:false,hand_pointer_for_clickable:true,alt_key_sends_control:false,safari_textarea_subtract_scrollbar_height:false,no_cursor_colresize:false,safari_visibility_instead_of_display:false,preload_images_only_once:false,absolute_position_accounts_for_offset:false,canvas_div_cannot_be_clipped:false,inputtext_parents_cannot_contain_clip:false,set_height_for_multiline_inputtext:false,ie_opacity:false,text_measurement_use_insertadjacenthtml:false,text_content_use_inner_text:false,text_selection_use_range:false,document_size_use_offsetheight:false,text_ie_carriagereturn:false,ie_paste_event:false,safari_paste_event:false,text_event_charcode:true,keypress_function_keys:true,ie_timer_closure:false,keyboardlistentotop:false,document_size_compute_correct_height:false,ie_mouse_events:false,fix_inputtext_with_parent_resource:false,activate_on_mouseover:true,ie6_improve_memory_performance:false,text_height_includes_padding:false,inputtext_size_includes_margin:false,listen_for_mouseover_out:true,focus_on_mouseover:true,textstyle_on_textdiv:false,textdeco_on_textdiv:false,use_css_sprites:true,preload_images:true,scrollbar_width:15,inputtext_strips_newlines:false,swf8_contextmenu:true,inputtext_anonymous_div:false,clipped_scrollbar_causes_display_turd:false,hasmetakey:false,textgrabsinputtextfocus:false,input_highlight_bug:false,autoscroll_textarea:false,fix_contextmenu:true,size_blank_to_zero:true,has_dom2_mouseevents:false,container_divs_require_overflow:false};LzSprite.prototype.capabilities={rotation:false,scalecanvastopercentage:false,readcanvassizefromsprite:true,opacity:true,colortransform:false,audio:false,accessibility:true,htmlinputtext:false,advancedfonts:false,bitmapcaching:false,persistence:false,clickmasking:false,minimize_opacity_changes:false,history:true,runtimemenus:false,setclipboard:false,proxypolicy:false,linescrolling:false,disableglobalfocustrap:true,allowfullscreen:false,setid:true};LzSprite.__updateQuirks=function(){
var $1=LzSprite.quirks;if(window["lz"]&&lz.embed&&lz.embed.browser){
var $2=lz.embed.browser;if($2.isIE){
if($2.version<7){
$1["ie_alpha_image_loader"]=true;$1["document_size_compute_correct_height"]=true;$1["ie6_improve_memory_performance"]=true
}else{
$1["prevent_selection"]=true;$1["invisible_parent_image_sizing_fix"]=true;if($2.osversion>=6){
$1["ie_alpha_image_loader"]=true
}};$1["ie_opacity"]=true;$1["ie_timer_closure"]=true;$1["ie_leak_prevention"]=true;$1["fix_ie_clickable"]=true;$1["fix_ie_background_height"]=true;$1["inner_html_no_entity_apos"]=true;LzSprite.prototype.capabilities["minimize_opacity_changes"]=true;$1["set_height_for_multiline_inputtext"]=true;$1["text_measurement_use_insertadjacenthtml"]=true;$1["text_content_use_inner_text"]=true;$1["text_selection_use_range"]=true;$1["text_ie_carriagereturn"]=true;$1["ie_paste_event"]=true;$1["keypress_function_keys"]=false;$1["text_event_charcode"]=false;$1["ie_mouse_events"]=true;$1["fix_inputtext_with_parent_resource"]=true;$1["inputtext_size_includes_margin"]=true;$1["focus_on_mouseover"]=false;$1["textstyle_on_textdiv"]=true;$1["scrollbar_width"]=17;$1["use_css_sprites"]=!$1["ie_alpha_image_loader"];$1["textgrabsinputtextfocus"]=true;$1["ie_elementfrompoint"]=true
}else{
if($2.isSafari){
$1["alt_key_sends_control"]=true;$1["safari_visibility_instead_of_display"]=true;$1["absolute_position_accounts_for_offset"]=true;if($2.version<525.18){
$1["canvas_div_cannot_be_clipped"]=true;$1["invisible_parent_image_sizing_fix"]=true;$1["safari_textarea_subtract_scrollbar_height"]=true
};$1["document_size_use_offsetheight"]=true;if($2.version>523.1){
LzSprite.prototype.capabilities["rotation"]=true;LzSprite.__defaultStyles.lzdiv.WebkitTransformOrigin="0 0"
};$1["safari_paste_event"]=true;$1["keypress_function_keys"]=false;if($2.version<523.15){
$1["keyboardlistentotop"]=true
};if($2.version>=530.19){
LzSprite.prototype.capabilities["threedtransform"]=true
};if($2.isIphone){
$1["canvas_div_cannot_be_clipped"]=true
};$1["inputtext_strips_newlines"]=true;$1["prevent_selection"]=true;$1["container_divs_require_overflow"]=true
}else{
if($2.isOpera){
$1["invisible_parent_image_sizing_fix"]=true;$1["no_cursor_colresize"]=true;$1["absolute_position_accounts_for_offset"]=true;$1["canvas_div_cannot_be_clipped"]=true;$1["document_size_use_offsetheight"]=true;$1["text_event_charcode"]=false;$1["textdeco_on_textdiv"]=true;$1["text_ie_carriagereturn"]=true
}else{
if($2.isFirefox){
$1["inputtext_anonymous_div"]=true;if($2.OS=="Windows"){
$1["clipped_scrollbar_causes_display_turd"]=true;$1["input_highlight_bug"]=true
};if($2.version<2){
$1["firefox_autocomplete_bug"]=true
}else{
if($2.version<3){
LzSprite.__defaultStyles.lzswftext.lineHeight="119%";LzSprite.__defaultStyles.lzswfinputtext.lineHeight="119%";LzSprite.__defaultStyles.lzswfinputtextmultiline.lineHeight="119%"
}else{
if($2.version<4){
if($2.subversion<6){
$1["text_height_includes_padding"]=true
};if($2.version<3.5){
$1["container_divs_require_overflow"]=true
}}}};$1["autoscroll_textarea"]=true;if($2.version>=3.5){
LzSprite.prototype.capabilities["rotation"]=true;LzSprite.__defaultStyles.lzdiv.MozTransformOrigin="0 0"
}}}}};if($2.OS=="Mac"){
$1["detectstuckkeys"]=true
};var $3=LzSprite.__defaultStyles;var $4=$3.lztextcontainer;var $5=$3.lzinputtextcontainer;var $6=$3.lzinputtextmultilinecontainer;if($1["hand_pointer_for_clickable"]){
$3.lzclickdiv.cursor="pointer"
};if($1["inner_html_strips_newlines"]==true){
LzSprite.prototype.inner_html_strips_newlines_re=RegExp("$","mg")
};if($2.isFirefox){
$3.lzimg["MozUserSelect"]="none"
}else{
if($2.isSafari){
$3.lzimg["WebkitUserSelect"]="none"
}else{
$3.lzimg["UserSelect"]="none"
}};LzSprite.prototype.br_to_newline_re=RegExp("<br/>","mg");if(lz.BrowserUtils.hasFeature("mouseevents","2.0")){
$1["has_dom2_mouseevents"]=true
}};LzSprite.prototype.quirks=$1
};LzSprite.__updateQuirks();LzSprite.__defaultStyles.writeCSS();LzSprite.setRootX=function($1){
var $2=LzSprite.__rootSpriteContainer;$2.style.position="absolute";$2.style.left=LzSprite.prototype.CSSDimension($1);LzScreenKernel.__resizeEvent()
};LzSprite.setRootWidth=function($1){
LzSprite.__rootSpriteContainer.style.width=LzSprite.prototype.CSSDimension($1);LzScreenKernel.__resizeEvent()
};LzSprite.setRootY=function($1){
var $2=LzSprite.__rootSpriteContainer;$2.style.position="absolute";$2.style.top=LzSprite.prototype.CSSDimension($1);LzScreenKernel.__resizeEvent()
};LzSprite.setRootHeight=function($1){
LzSprite.__rootSpriteContainer.style.height=LzSprite.prototype.CSSDimension($1);LzScreenKernel.__resizeEvent()
};LzSprite.prototype.__LZdiv=null;LzSprite.prototype.__LZimg=null;LzSprite.prototype.__LZclick=null;LzSprite.prototype.x=null;LzSprite.prototype.y=null;LzSprite.prototype.opacity=null;LzSprite.prototype.width=null;LzSprite.prototype.height=null;LzSprite.prototype.playing=false;LzSprite.prototype.clickable=false;LzSprite.prototype.frame=1;LzSprite.prototype.frames=null;LzSprite.prototype.blankimage="lps/includes/blank.gif";LzSprite.prototype.resource=null;LzSprite.prototype.source=null;LzSprite.prototype.visible=null;LzSprite.prototype.text=null;LzSprite.prototype.clip=null;LzSprite.prototype.stretches=null;LzSprite.prototype.resourceWidth=null;LzSprite.prototype.resourceHeight=null;LzSprite.prototype.cursor=null;LzSprite.prototype._w="0pt";LzSprite.prototype._h="0pt";LzSprite.prototype.__LZcontext=null;LzSprite.prototype.init=function($1){
this.setVisible($1);if(this.isroot){
if(this.quirks["css_hide_canvas_during_init"]){
var $2="display";if(this.quirks["safari_visibility_instead_of_display"]){
$2="visibility"
};this.__LZdiv.style[$2]="";if(this.quirks["fix_clickable"]){
this.__LZclickcontainerdiv.style[$2]=""
};if(this.quirks["fix_contextmenu"]){
this.__LZcontextcontainerdiv.style[$2]=""
}};if(this._id){
lz.embed[this._id]._ready(this.owner)
};this.__initdone=true
}};LzSprite.prototype.__topZ=1;LzSprite.prototype.__parent=null;LzSprite.prototype.__children=null;LzSprite.prototype.addChildSprite=function($1){
if($1.__parent!=null){
return
};$1.__parent=this;if(this.__children){
this.__children.push($1)
}else{
this.__children=[$1]
};this.__LZdiv.appendChild($1.__LZdiv);if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.appendChild($1.__LZclickcontainerdiv)
};$1.__setZ(++this.__topZ)
};LzSprite.prototype.setResource=function($1){
if(this.resource==$1){
return
};this.resource=$1;if($1.indexOf("http:")==0||$1.indexOf("https:")==0){
this.skiponload=false;this.setSource($1);return
};var $2=LzResourceLibrary[$1];if($2){
this.resourceWidth=$2.width;this.resourceHeight=$2.height;if(this.quirks.use_css_sprites&&$2.sprite){
var $3=this.getBaseUrl($2);this.__csssprite=$3+$2.sprite
}else{
this.__csssprite=null;if(this.__bgimage){
this.__setBGImage(null)
}}};var $4=this.getResourceUrls($1);this.owner.resourceevent("totalframes",$4.length);this.frames=$4;if(this.quirks.preload_images&&!(this.stretches==null&&this.__csssprite)){
this.__preloadFrames()
};this.skiponload=true;this.setSource($4[0],true)
};LzSprite.prototype.getResourceUrls=function($1){
var $2=[];var $3=LzResourceLibrary[$1];if(!$3){
return $2
};var $4=this.getBaseUrl($3);for(var $5=0;$5<$3.frames.length;$5++){
$2[$5]=$4+$3.frames[$5]
};return $2
};LzSprite.prototype.getBaseUrl=function($1){
if($1.ptype=="sr"){
return lz.embed.options.serverroot
}else{
return lz.embed.options.approot
}};LzSprite.prototype.CSSDimension=LzKernelUtils.CSSDimension;LzSprite.prototype.loading=false;LzSprite.prototype.setSource=function($1,$2){
if($1==null||$1=="null"){
this.unload();return
};if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$1!=null){
this.__sizedtozero=false;this.__LZdiv.style.width=this._w;this.__LZdiv.style.height=this._h
}};if($2!=true){
this.skiponload=false;this.resource=$1;if(this.playing){
this.stop()
};this.__updateLoadStatus(0);this.__csssprite=null;if(this.__bgimage){
this.__setBGImage(null)
}};if($2=="memorycache"){
$2=true
};if(this.loading){
if(this.__ImgPool&&this.source){
this.__ImgPool.flush(this.source)
};this.__destroyImage(null,this.__LZimg);this.__LZimg=null
};this.source=$1;if(this.stretches==null&&this.__csssprite){
if(!this.__LZimg){
var $3=document.createElement("img");$3.className="lzimg";$3.owner=this;$3.src=lz.embed.options.serverroot+LzSprite.prototype.blankimage;this.__bindImage($3)
};this.__updateStretches();var $4=this.__csssprite?this.__csssprite:$1;this.__setBGImage($4);this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload});return
};if(!this.quirks.preload_images){
this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload})
};this.loading=true;if(!this.__ImgPool){
this.__ImgPool=new LzPool(LzSprite.prototype.__getImage,LzSprite.prototype.__gotImage,LzSprite.prototype.__destroyImage,this)
};var $3=this.__ImgPool.get($1,$2!=true);this.__bindImage($3);if(this.loading){
if(this.skiponload&&this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha($3)
}}else{
if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha($3)
}else{
if(this.stretches){
this.__updateStretches()
}}};if(this.clickable){
this.setClickable(true)
}};LzSprite.prototype.__bindImage=function($1){
if(this.__LZimg&&this.__LZimg.owner){
this.__LZdiv.replaceChild($1,this.__LZimg);this.__LZimg=$1
}else{
this.__LZimg=$1;this.__LZdiv.appendChild(this.__LZimg)
}};LzSprite.prototype.__setBGImage=function($1){
var $2=$1?"url('"+$1+"')":null;this.__bgimage=this.__LZimg.style.backgroundImage=$2
};if(LzSprite.quirks.ie_alpha_image_loader){
LzSprite.prototype.__updateIEAlpha=function($1){
var $2=this.resourceWidth;var $3=this.resourceHeight;if(this.stretches=="both"){
$2="100%";$3="100%"
}else{
if(this.stretches=="width"){
$2="100%"
}else{
if(this.stretches=="height"){
$3="100%"
}}};if($2==null){
$2=this.width==null?"100%":this.CSSDimension(this.width)
};if($3==null){
$3=this.height==null?"100%":this.CSSDimension(this.height)
};$1.style.width=$2;$1.style.height=$3
}};LzSprite.prototype.setClickable=function($1){
$1=$1==true;if(this.clickable==$1){
return
};if(this.__LZimg!=null){
if(this.__LZdiv._clickable){
this.__setClickable(false,this.__LZdiv)
};if(!this.__LZclick){
if(this.quirks.fix_ie_clickable){
this.__LZclick=document.createElement("img");this.__LZclick.src=lz.embed.options.serverroot+LzSprite.prototype.blankimage
}else{
this.__LZclick=document.createElement("div")
};this.__LZclick.owner=this;this.__LZclick.className="lzclickdiv";this.__LZclick.style.width=this._w;this.__LZclick.style.height=this._h;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.appendChild(this.__LZclick)
}else{
this.__LZdiv.appendChild(this.__LZclick)
}};this.__setClickable($1,this.__LZclick);if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable){
this.__LZclickcontainerdiv.style.display=$1&&this.visible?"":"none";this.__LZclick.style.display=$1&&this.visible?"":"none"
}else{
this.__LZclick.style.display=$1?"":"none"
}}}else{
if(this.quirks.fix_clickable){
if(!this.__LZclick){
if(this.quirks.fix_ie_clickable){
this.__LZclick=document.createElement("img");this.__LZclick.src=lz.embed.options.serverroot+LzSprite.prototype.blankimage
}else{
this.__LZclick=document.createElement("div")
};this.__LZclick.owner=this;this.__LZclick.className="lzclickdiv";this.__LZclick.style.width=this._w;this.__LZclick.style.height=this._h;this.__LZclickcontainerdiv.appendChild(this.__LZclick)
};this.__setClickable($1,this.__LZclick);if(this.quirks.fix_ie_clickable){
this.__LZclick.style.display=$1&&this.visible?"":"none"
}else{
this.__LZclick.style.display=$1?"":"none"
}}else{
this.__setClickable($1,this.__LZdiv)
}};this.clickable=$1
};LzSprite.prototype.__setClickable=function($1,$2){
if($2._clickable==$1){
return
};$2._clickable=$1;var $3=$1?LzSprite.prototype.__clickDispatcher:null;$2.onclick=$3;$2.onmousedown=$3;$2.onmouseup=$3;$2.onmousemove=$3;if(this.quirks.ie_mouse_events){
$2.ondrag=$3;$2.ondblclick=$3;$2.onmouseover=$3;$2.onmouseout=$3
}else{
if(this.quirks.listen_for_mouseover_out){
$2.onmouseover=$3;$2.onmouseout=$3
}}};LzSprite.prototype.__clickDispatcher=function($1){
if(!$1){
$1=window.event
};if($1.button==2){
return false
};this.owner.__mouseEvent($1);return false
};LzSprite.prototype.__mouseisdown=false;LzSprite.prototype.__mouseEvent=function($1,$2){
if($2){
var $3=$1;$1={}}else{
var $3="on"+$1.type;if(LzKeyboardKernel&&LzKeyboardKernel["__updateControlKeys"]){
LzKeyboardKernel.__updateControlKeys($1);if(LzKeyboardKernel.__cancelKeys){
$1.cancelBubble=true
}}};if(this.quirks.ie_mouse_events){
if($3=="onmouseenter"){
$3="onmouseover"
}else{
if($3=="onmouseleave"){
$3="onmouseout"
}else{
if($3=="ondblclick"){
this.__mouseEvent("onmousedown",true);this.__mouseEvent("onmouseup",true);this.__mouseEvent("onclick",true);return
}else{
if($3=="ondrag"){
return
}}}}};LzMouseKernel.__sendMouseMove($1);if($3=="onmousemove"){
return
}else{
if($3=="onmousedown"){
this.__mouseisdown=true;LzMouseKernel.__lastMouseDown=this
}else{
if($3=="onmouseup"){
$1.cancelBubble=false;if(LzMouseKernel.__lastMouseDown!==this){
return
}else{
if(this.quirks.ie_mouse_events){
if(this.__isMouseOver()){
this.__mouseisdown=false
}}else{
this.__mouseisdown=false
};if(this.__mouseisdown==false){
LzMouseKernel.__lastMouseDown=null
}}}else{
if($3=="onmouseupoutside"){
this.__mouseisdown=false
}else{
if($3=="onmouseover"){
LzMouseKernel.__lastMouseOver=this;if(this.quirks.activate_on_mouseover){
var $4=LzSprite.__mouseActivationDiv;if(!$4.mouseisover){
$4.onmouseover()
}}}}}}};if(this.owner.mouseevent){
if(LzMouseKernel.__lastMouseDown){
if($3=="onmouseover"||$3=="onmouseout"){
var $5=false;if(this.quirks.ie_mouse_events){
var $6=this.__isMouseOver();if($6&&$3=="onmouseover"||!$6&&$3=="onmouseout"){
$5=true
}}else{
if(LzMouseKernel.__lastMouseDown===this){
$5=true
}};if($3=="onmouseover"){
LzMouseKernel.__lastMouseOver=this
}else{
if($5&&LzMouseKernel.__lastMouseOver===this){
LzMouseKernel.__lastMouseOver=null
}};if($5){
LzMouseKernel.__sendEvent($3,this.owner);var $7=$3=="onmouseover"?"onmousedragin":"onmousedragout";LzMouseKernel.__sendEvent($7,this.owner)
};return
}};if(this.quirks.fix_clickable&&!LzMouseKernel.__globalClickable){
if($3=="onmouseout"||$3=="onmouseover"){
return
}};LzMouseKernel.__sendEvent($3,this.owner)
}};LzSprite.prototype.__isMouseOver=function($1){
var $2=this.getMouse();var $3=this.__findParents("visible",false);if($3.length){
return false
};return $2.x>=0&&$2.y>=0&&$2.x<this.width&&$2.y<this.height
};LzSprite.prototype.__globalmouseup=function($1){
if(this.__mouseisdown){
if(!this.quirks.ie_mouse_events){
this.__mouseEvent($1)
};this.__mouseEvent("onmouseupoutside",true)
};LzMouseKernel.__lastMouseDown=null;if(LzMouseKernel.__lastMouseOver){
LzMouseKernel.__lastMouseOver.__mouseEvent("onmouseover",true);LzMouseKernel.__lastMouseOver=null
}};LzSprite.prototype.setX=function($1){
if($1==null||$1==this.x){
return
};this.__poscacheid=-1;this.x=$1;$1=this.CSSDimension($1);if(this._x!=$1){
this._x=$1;this.__LZdiv.style.left=$1;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.style.left=$1
};if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.left=$1
}}};LzSprite.prototype.setWidth=function($1){
if($1==null||$1<0||this.width==$1){
return
};this.width=$1;$1=this.CSSDimension($1);if(this._w!=$1){
this._w=$1;var $2=$1;var $3=this.quirks;if($3.size_blank_to_zero){
if(this.bgcolor==null&&this.source==null&&!this.clip&&!(this instanceof LzTextSprite)){
this.__sizedtozero=true;$2="0px"
}};this.__LZdiv.style.width=$2;if(this.clip){
this.__updateClip()
};if(this.stretches){
this.__updateStretches()
};if(this.__LZclick){
this.__LZclick.style.width=$1
};if(this.__LZcontext){
this.__LZcontext.style.width=$1
};if(this.isroot&&$3.container_divs_require_overflow){
LzSprite.__rootSpriteOverflowContainer.style.width=$1
};return $1
}};LzSprite.prototype.setY=function($1){
if($1==null||$1==this.y){
return
};this.__poscacheid=-1;this.y=$1;$1=this.CSSDimension($1);if(this._y!=$1){
this._y=$1;this.__LZdiv.style.top=$1;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.style.top=$1
};if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.top=$1
}}};LzSprite.prototype.setHeight=function($1){
if($1==null||$1<0||this.height==$1){
return
};this.height=$1;$1=this.CSSDimension($1);if(this._h!=$1){
this._h=$1;var $2=$1;var $3=this.quirks;if($3.size_blank_to_zero){
if(this.bgcolor==null&&this.source==null&&!this.clip&&!(this instanceof LzTextSprite)){
this.__sizedtozero=true;$2="0px"
}};this.__LZdiv.style.height=$2;if(this.clip){
this.__updateClip()
};if(this.stretches){
this.__updateStretches()
};if(this.__LZclick){
this.__LZclick.style.height=$1
};if(this.__LZcontext){
this.__LZcontext.style.height=$1
};if(this.isroot&&$3.container_divs_require_overflow){
LzSprite.__rootSpriteOverflowContainer.style.height=$1
};return $1
}};LzSprite.prototype.setMaxLength=function($1){

};LzSprite.prototype.setPattern=function($1){

};LzSprite.prototype.setVisible=function($1){
if(this.visible==$1){
return
};this.visible=$1;this.__LZdiv.style.display=$1&&this.opacity!=0?"":"none";if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable&&this.__LZclick){
this.__LZclick.style.display=$1&&this.clickable?"":"none"
};var $2=$1?"":"none";this.__LZclickcontainerdiv.style.display=$2;if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.display=$2
}}};LzSprite.prototype.setColor=function($1){
if(this.color==$1){
return
};this.color=$1;this.__LZdiv.style.color=LzColorUtils.inttohex($1)
};LzSprite.prototype.setBGColor=function($1){
if(this.bgcolor==$1){
return
};this.bgcolor=$1;if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$1!=null){
this.__sizedtozero=false;this.__LZdiv.style.width=this._w;this.__LZdiv.style.height=this._h
}};this.__LZdiv.style.backgroundColor=$1==null?"transparent":LzColorUtils.inttohex($1);if(this.quirks.fix_ie_background_height){
if(this.height!=null&&this.height<2){
this.setSource(lz.embed.options.serverroot+LzSprite.prototype.blankimage,true)
}else{
if(!this._fontSize){
this.__LZdiv.style.fontSize="0px"
}}}};LzSprite.prototype.setOpacity=function($1){
if(this.opacity==$1||$1<0){
return
};this.opacity=$1;var $2=100;if(this.capabilities.minimize_opacity_changes){
$2=10
};$1=parseInt($1*$2)/$2;if($1!=this._opacity){
this._opacity=$1;this.__LZdiv.style.display=this.visible&&$1!=0?"":"none";if(this.quirks.ie_opacity){
if($1==1){
this.__LZdiv.style.filter=""
}else{
this.__LZdiv.style.filter="alpha(opacity="+parseInt($1*100)+")"
}}else{
if($1==1){
this.__LZdiv.style.opacity=""
}else{
this.__LZdiv.style.opacity=$1
}}}};LzSprite.prototype.play=function($1){
if(!this.frames||this.frames.length<2){
return
};$1=parseInt($1);if(!isNaN($1)){
this.__setFrame($1)
};if(this.playing==true){
return
};this.playing=true;this.owner.resourceevent("play",null,true);LzIdleKernel.addCallback(this,"__incrementFrame")
};LzSprite.prototype.stop=function($1){
if(!this.frames||this.frames.length<2){
return
};if(this.playing==true){
this.playing=false;this.owner.resourceevent("stop",null,true);LzIdleKernel.removeCallback(this,"__incrementFrame")
};$1=parseInt($1);if(!isNaN($1)){
this.__setFrame($1)
}};LzSprite.prototype.__incrementFrame=function(){
var $1=this.frame+1>this.frames.length?1:this.frame+1;this.__setFrame($1)
};if(LzSprite.quirks.preload_images_only_once){
LzSprite.prototype.__preloadurls={}};LzSprite.prototype.__preloadFrames=function(){
if(!this.__ImgPool){
this.__ImgPool=new LzPool(LzSprite.prototype.__getImage,LzSprite.prototype.__gotImage,LzSprite.prototype.__destroyImage,this)
};var $1=this.frames.length;for(var $2=0;$2<$1;$2++){
var $3=this.frames[$2];if(this.quirks.preload_images_only_once){
if($2>0&&LzSprite.prototype.__preloadurls[$3]){
continue
};LzSprite.prototype.__preloadurls[$3]=true
};var $4=this.__ImgPool.get($3,false,true);if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha($4)
}}};LzSprite.prototype.__findParents=function($1,$2){
var $3=[];var $4=LzSprite.__rootSprite;var $5=this;while($5!==$4){
if($5[$1]==$2){
$3.push($5)
};$5=$5.__parent
};return $3
};LzSprite.prototype.__imgonload=function($1,$2){
if(this.loading!=true){
return
};if(this.__imgtimoutid!=null){
clearTimeout(this.__imgtimoutid);this.__imgtimoutid=null
};this.loading=false;if(!$2){
if(this.quirks.ie_alpha_image_loader){
$1._parent.style.display=""
}else{
$1.style.display=""
}};this.resourceWidth=$2&&$1["__LZreswidth"]?$1.__LZreswidth:$1.width;this.resourceHeight=$2&&$1["__LZresheight"]?$1.__LZresheight:$1.height;if(!$2){
if(this.quirks.invisible_parent_image_sizing_fix&&this.resourceWidth==0){
var $3=function($1){
this.resourceWidth=$1.width;this.resourceHeight=$1.height
};this.__processHiddenParents($3,$1)
};if(this.quirks.ie_alpha_image_loader){
$1._parent.__lastcondition="__imgonload"
}else{
$1.__lastcondition="__imgonload";$1.__LZreswidth=this.resourceWidth;$1.__LZresheight=this.resourceHeight
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else{
if(this.stretches){
this.__updateStretches()
}}};this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload});if(this.skiponload!=true){
this.__updateLoadStatus(1)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($1)
}};LzSprite.prototype.__processHiddenParents=function($1){
var $2=this.__findParents("visible",false);var $3=[];var $4=$2.length;for(var $5=0;$5<$4;$5++){
var $6=$2[$5];$3[$5]=$6.__LZdiv.style.display;$6.__LZdiv.style.display=""
};var $7=Array.prototype.slice.call(arguments,1);var $8=$1.apply(this,$7);for(var $5=0;$5<$4;$5++){
var $6=$2[$5];$6.__LZdiv.style.display=$3[$5]
};return $8
};LzSprite.prototype.__imgonerror=function($1,$2){
if(this.loading!=true){
return
};if(this.__imgtimoutid!=null){
clearTimeout(this.__imgtimoutid);this.__imgtimoutid=null
};this.loading=false;this.resourceWidth=1;this.resourceHeight=1;if(!$2){
if(this.quirks.ie_alpha_image_loader){
$1._parent.__lastcondition="__imgonerror"
}else{
$1.__lastcondition="__imgonerror"
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else{
if(this.stretches){
this.__updateStretches()
}}};this.owner.resourceloaderror();if(this.skiponload!=true){
this.__updateLoadStatus(0)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($1)
}};LzSprite.prototype.__imgontimeout=function($1,$2){
if(this.loading!=true){
return
};this.__imgtimoutid=null;this.loading=false;this.resourceWidth=1;this.resourceHeight=1;if(!$2){
if(this.quirks.ie_alpha_image_loader){
$1._parent.__lastcondition="__imgontimeout"
}else{
$1.__lastcondition="__imgontimeout"
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else{
if(this.stretches){
this.__updateStretches()
}}};this.owner.resourceloadtimeout();if(this.skiponload!=true){
this.__updateLoadStatus(0)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($1)
}};LzSprite.prototype.__updateLoadStatus=function($1){
this.owner.resourceevent("loadratio",$1);this.owner.resourceevent("framesloadratio",$1)
};LzSprite.prototype.__destroyImage=function($1,$2){
if($2){
if($2.owner){
var $3=$2.owner;if($3.__imgtimoutid!=null){
clearTimeout($3.__imgtimoutid);$3.__imgtimoutid=null
};lz.BrowserUtils.removecallback($3)
};LzSprite.prototype.__clearImageEvents($2);LzSprite.prototype.__discardElement($2)
};if(LzSprite.quirks.preload_images_only_once){
LzSprite.prototype.__preloadurls[$1]=null
}};LzSprite.prototype.__clearImageEvents=function($1){
if(!$1||$1.__cleared){
return
};if(LzSprite.quirks.ie_alpha_image_loader){
var $2=$1.sizer;if($2){
if($2.tId){
clearTimeout($2.tId)
};$2.onerror=null;$2.onload=null;$2.onloadforeal=null;$2._parent=null;var $3={width:$2.width,height:$2.height,src:$2.src};LzSprite.prototype.__discardElement($2);$1.sizer=$3
}}else{
$1.onerror=null;$1.onload=null
};$1.__cleared=true
};LzSprite.prototype.__gotImage=function($1,$2,$3){
if(this.owner.skiponload||$3==true){
this.owner[$2.__lastcondition]({width:this.owner.resourceWidth,height:this.owner.resourceHeight},true)
}else{
if(LzSprite.quirks.ie_alpha_image_loader){
this.owner[$2.__lastcondition]($2.sizer,true)
}else{
this.owner[$2.__lastcondition]($2,true)
}}};LzSprite.prototype.__getImage=function($1,$2){
if(LzSprite.quirks.ie_alpha_image_loader){
var im=document.createElement("div");im.style.overflow="hidden";if(this.owner&&$2!=true){
im.owner=this.owner;if(!im.sizer){
im.sizer=document.createElement("img");im.sizer._parent=im
};im.sizer.onload=function(){
im.sizer.tId=setTimeout(this.onloadforeal,1)
};im.sizer.onloadforeal=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonload",[im.sizer]);im.sizer.onerror=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonerror",[im.sizer]);var $3=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgontimeout",[im.sizer]);this.owner.__imgtimoutid=setTimeout($3,canvas.medialoadtimeout);im.sizer.src=$1
};if(!$2){
im.style.display="none"
};if(this.owner.stretches){
im.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+$1+"',sizingMethod='scale')"
}else{
im.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+$1+"')"
}}else{
var im=document.createElement("img");im.className="lzimg";if(!$2){
im.style.display="none"
};if(this.owner&&$2!=true){
im.owner=this.owner;im.onload=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonload",[im]);im.onerror=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonerror",[im]);var $3=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgontimeout",[im]);this.owner.__imgtimoutid=setTimeout($3,canvas.medialoadtimeout)
};im.src=$1
};if(im){
im.__lastcondition="__imgonload"
};return im
};LzSprite.prototype.setClip=function($1){
if(this.clip==$1){
return
};this.clip=$1;if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$1){
this.__sizedtozero=false;this.__LZdiv.style.width=this._w;this.__LZdiv.style.height=this._h
}};this.__updateClip()
};LzSprite.prototype.__updateClip=function(){
if(this.isroot&&this.quirks.canvas_div_cannot_be_clipped){
return
};if(this.clip&&this.width!=null&&this.width>=0&&this.height!=null&&this.height>=0){
var $1="rect(0px "+this._w+" "+this._h+" 0px)";this.__LZdiv.style.clip=$1
}else{
if(this.__LZdiv.style.clip){
var $1="";this.__LZdiv.style.clip=$1
}else{
return
}};var $2=this.quirks;if($2.fix_clickable){
this.__LZclickcontainerdiv.style.clip=$1
};if($2.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.clip=$1
}};LzSprite.prototype.stretchResource=function($1){
$1=$1!="none"?$1:null;if(this.stretches==$1){
return
};this.stretches=$1;if(!($1==null&&this.__csssprite)&&this.__bgimage){
if(this.quirks.preload_images){
this.__preloadFrames()
};this.__setBGImage(null);this.__setFrame(this.frame,true)
};this.__updateStretches()
};LzSprite.prototype.__updateStretches=function(){
if(this.loading){
return
};if(this.quirks.ie_alpha_image_loader){
return
};if(this.__LZimg){
var $1=this.__LZimg.style.display;this.__LZimg.style.display="none";if(this.stretches=="both"){
this.__LZimg.width=this.width;this.__LZimg.height=this.height
}else{
if(this.stretches=="height"){
this.__LZimg.width=this.resourceWidth;this.__LZimg.height=this.height
}else{
if(this.stretches=="width"){
this.__LZimg.width=this.width;this.__LZimg.height=this.resourceHeight
}else{
this.__LZimg.width=this.resourceWidth;this.__LZimg.height=this.resourceHeight
}}};this.__LZimg.style.display=$1
}};LzSprite.prototype.predestroy=function(){

};LzSprite.prototype.destroy=function(){
if(this.__LZdeleted==true){
return
};this.__LZdeleted=true;if(this.__parent&&!this.__parent.__LZdeleted){
var $1=this.__parent.__children;for(var $2=$1.length-1;$2>=0;$2--){
if($1[$2]===this){
$1.splice($2,1);break
}}};if(this.__ImgPool){
this.__ImgPool.destroy()
};if(this.__LZimg){
this.__discardElement(this.__LZimg)
};if(this.__LZclick){
this.__setClickable(false,this.__LZclick);this.__discardElement(this.__LZclick)
};if(this.__LzInputDiv){
this.__setTextEvents(false);this.__discardElement(this.__LzInputDiv)
};if(this.__LZdiv){
if(this.isroot){
if(this.quirks.activate_on_mouseover){
this.__LZdiv.onmouseover=null;this.__LZdiv.onmouseout=null
}};this.__LZdiv.onselectstart=null;this.__setClickable(false,this.__LZdiv);this.__discardElement(this.__LZdiv)
};if(this.__LZinputclickdiv){
if(this.quirks.ie_mouse_events){
this.__LZinputclickdiv.onmouseenter=null
}else{
this.__LZinputclickdiv.onmouseover=null
};this.__discardElement(this.__LZinputclickdiv)
};if(this.__LZclickcontainerdiv){
this.__discardElement(this.__LZclickcontainerdiv)
};if(this.__LZcontextcontainerdiv){
this.__discardElement(this.__LZcontextcontainerdiv)
};if(this.__LZcontext){
this.__discardElement(this.__LZcontext)
};if(this.__LZtextdiv){
this.__discardElement(this.__LZtextdiv)
};if(this.__LZcanvas){
this.__discardElement(this.__LZcanvas)
};this.__ImgPool=null;if(this.quirks.ie_leak_prevention){
delete this.__sprites[this.uid]
};if(this.isroot){
lz.BrowserUtils.scopes=null
}};LzSprite.prototype.getMouse=function(){
var $1=this.__getPos();return {x:LzMouseKernel.__x-$1.x,y:LzMouseKernel.__y-$1.y}};LzSprite.prototype.__poscache=null;LzSprite.prototype.__poscacheid=0;LzSprite.__poscachecnt=0;LzSprite.prototype.__getPos=function(){
if(!LzSprite.__rootSprite.__initdone){
return lz.embed.getAbsolutePosition(this.__LZdiv)
};var $1=false;var $2=true;var $3=LzSprite.__rootSprite;var $4,$5;for(var $6=this;$6!==$3;$6=$4){
$4=$6.__parent;if($4){
if($6.__poscacheid<$4.__poscacheid){
$1=true;$5=$4
}}else{
$2=false;break
}};if($1&&$2){
var $7=++LzSprite.__poscachecnt;for(var $6=this;$6!==$5;$6=$6.__parent){
$6.__poscache=null;$6.__poscacheid=$7
}};var $8=this.__poscache;if(!$8){
$8=this.__processHiddenParents(lz.embed.getAbsolutePosition,this.__LZdiv);if($2){
this.__poscache=$8
}};return $8
};LzSprite.prototype.getWidth=function(){
var $1=this.__LZdiv.clientWidth;return $1==0?this.width:$1
};LzSprite.prototype.getHeight=function(){
var $1=this.__LZdiv.clientHeight;return $1==0?this.height:$1
};LzSprite.prototype.setCursor=function($1){
if(this.quirks.no_cursor_colresize){
return
};if($1==this.cursor){
return
};if(this.clickable!=true){
this.setClickable(true)
};this.cursor=$1;var $1=LzSprite.__defaultStyles.hyphenate($1);this.__LZclick.style.cursor=$1
};LzSprite.prototype.setShowHandCursor=function($1){
if($1==true){
this.setCursor("pointer")
}else{
this.setCursor("default")
}};LzSprite.prototype.getMCRef=function(){
return this.__LZdiv
};LzSprite.prototype.getContext=function(){
return this.getMCRef()
};LzSprite.prototype.bringToFront=function(){
if(!this.__parent){
return
};if(this.__parent.__children.length<2){
return
};this.__setZ(++this.__parent.__topZ)
};LzSprite.prototype.__setZ=function($1){
this.__LZdiv.style.zIndex=$1;var $2=this.quirks;if($2.fix_clickable){
this.__LZclickcontainerdiv.style.zIndex=$1
};if($2.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.zIndex=$1
};this.__z=$1
};LzSprite.prototype.__zCompare=function($1,$2){
if($1.__z<$2.__z){
return -1
};if($1.__z>$2.__z){
return 1
};return 0
};LzSprite.prototype.sendToBack=function(){
if(!this.__parent){
return
};var $1=this.__parent.__children;if($1.length<2){
return
};$1.sort(LzSprite.prototype.__zCompare);this.sendBehind($1[0])
};LzSprite.prototype.sendBehind=function($1){
if(!$1){
return
};if(!this.__parent){
return
};var $2=this.__parent.__children;if($2.length<2){
return
};$2.sort(LzSprite.prototype.__zCompare);var $3=false;for(var $4=0;$4<$2.length;$4++){
var $5=$2[$4];if($5==$1){
$3=$1.__z
};if($3!=false){
$5.__setZ(++$5.__z)
}};this.__setZ($3)
};LzSprite.prototype.sendInFrontOf=function($1){
if(!$1){
return
};if(!this.__parent){
return
};var $2=this.__parent.__children;if($2.length<2){
return
};$2.sort(LzSprite.prototype.__zCompare);var $3=false;for(var $4=0;$4<$2.length;$4++){
var $5=$2[$4];if($3!=false){
$5.__setZ(++$5.__z)
};if($5==$1){
$3=$1.__z+1
}};this.__setZ($3)
};LzSprite.prototype.__setFrame=function($1,$2){
if($1<1){
$1=1
}else{
if($1>this.frames.length){
$1=this.frames.length
}};var $3=false;if($2){
$3=$1==this.frame
}else{
if($1==this.frame){
return
}};this.frame=$1;if(this.stretches==null&&this.__csssprite){
if(!this.__bgimage){
this.__LZimg.src=lz.embed.options.resourceroot+LzSprite.prototype.blankimage;this.__setBGImage(this.__csssprite)
};var $4=(this.frame-1)*-this.resourceWidth;var $5=0;this.__LZimg.style.backgroundPosition=$4+"px "+$5+"px"
}else{
var $6=this.frames[this.frame-1];this.setSource($6,true)
};if($3){
return
};this.owner.resourceevent("frame",this.frame);if(this.frames.length==this.frame){
this.owner.resourceevent("lastframe",null,true)
}};LzSprite.prototype.__discardElement=function($1){
if(LzSprite.quirks.ie_leak_prevention){
if(!$1||!$1.nodeType){
return
};if($1.nodeType>=1&&$1.nodeType<13){
if($1.owner){
$1.owner=null
};var $2=document.getElementById("__LZIELeakGarbageBin");if(!$2){
$2=document.createElement("DIV");$2.id="__LZIELeakGarbageBin";$2.style.display="none";document.body.appendChild($2)
};$2.appendChild($1);$2.innerHTML=""
}}else{
if($1.parentNode){
$1.parentNode.removeChild($1)
}}};LzSprite.prototype.getZ=function(){
return this.__z
};LzSprite.prototype.updateResourceSize=function(){
this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:true})
};LzSprite.prototype.unload=function(){
this.resource=null;this.source=null;this.resourceWidth=null;this.resourceHeight=null;if(this.__ImgPool){
this.__ImgPool.destroy();this.__ImgPool=null
};if(this.__LZimg){
this.__destroyImage(null,this.__LZimg);this.__LZimg=null
};this.__updateLoadStatus(0)
};LzSprite.prototype.__setCSSClassProperty=function($1,$2,$3){
var $4=document.all?"rules":"cssRules";var $5=document.styleSheets;var $6=$5.length-1;for(var $7=$6;$7>=0;$7--){
var $8=$5[$7][$4];var $9=$8.length-1;for(var $10=$9;$10>=0;$10--){
if($8[$10].selectorText==$1){
$8[$10].style[$2]=$3
}}}};LzSprite.prototype.setDefaultContextMenu=function($1){
LzSprite.__rootSprite.__contextmenu=$1
};LzSprite.prototype.setContextMenu=function($1){
this.__contextmenu=$1;if(!this.quirks.fix_contextmenu||this.__LZcontext){
return
};var $2=this.__findParents("__LZcontextcontainerdiv",null);for(var $3=$2.length-1;$3>=0;$3--){
var $4=$2[$3];var $5=$4.__parent.__LZcontextcontainerdiv;var $6=document.createElement("div");$6.className="lzdiv";$5.appendChild($6);this.__copystyles($4.__LZdiv,$6);if($4._id&&!$6.id){
$6.id="context"+$4._id
};$6.owner=$4;$4.__LZcontextcontainerdiv=$6
};var $6=document.createElement("div");$6.className="lzcontext";this.__LZcontextcontainerdiv.appendChild($6);this.__LZcontext=$6;$6.style.width=this._w;$6.style.height=this._h;$6.owner=this
};LzSprite.prototype.__copystyles=function($1,$2){
$2.style.left=$1.style.left;$2.style.top=$1.style.top;$2.style.display=$1.style.display;$2.style.clip=$1.style.clip;$2.style.zIndex=$1.style.zIndex
};LzSprite.prototype.getContextMenu=function(){
return this.__contextmenu
};LzSprite.prototype.setRotation=function($1){
var $2=lz.embed.browser;if($2.isSafari){
this.__LZdiv.style["WebkitTransform"]="rotate("+$1+"deg)"
}else{
if($2.isFirefox){
this.__LZdiv.style["MozTransform"]="rotate("+$1+"deg)"
}}};if(LzSprite.quirks.ie_leak_prevention){
LzSprite.prototype.__sprites={};function __cleanUpForIE(){
LzTextSprite.prototype.__cleanupdivs();LzTextSprite.prototype._sizecache={};var $1=LzSprite.prototype.__sprites;for(var $2 in $1){
$1[$2].destroy();$1[$2]=null
};LzSprite.prototype.__sprites={}};lz.embed.attachEventHandler(window,"beforeunload",window,"__cleanUpForIE")
};LzSprite.prototype.getSelectedText=function(){
if(window.getSelection){
return window.getSelection().toString()
}else{
if(document.selection){
return document.selection.createRange().text.toString()
}else{
if(document.getSelection){
return document.getSelection()
}}}};LzSprite.prototype.setAADescription=function($1){
var $2=this.aadescriptionDiv;if($2==null){
this.aadescriptionDiv=$2=document.createElement("LABEL");$2.className="lzaccessibilitydiv";if(!this.__LZdiv.id){
this.__LZdiv.id="sprite_"+this.uid
};var $lzsc$1874841173=this.__LZdiv.id;if(!$2.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($2["$lzc$set_for"]):$2["$lzc$set_for"] instanceof Function){
$2["$lzc$set_for"]($lzsc$1874841173)
}else{
$2["for"]=$lzsc$1874841173;var $lzsc$24058606=$2["onfor"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$24058606):$lzsc$24058606 instanceof LzEvent){
if($lzsc$24058606.ready){
$lzsc$24058606.sendEvent($lzsc$1874841173)
}}}};this.__LZdiv.appendChild($2)
};$2.innerHTML=$1
};LzSprite.prototype.setAccessible=function($1){
LzSprite.__rootSprite.accessible=$1
};LzSprite.prototype._accProps=null;LzSprite.prototype.setAAActive=function($1){
this.__LzAccessibilityActive=$1
};LzSprite.prototype.setAASilent=function($1){

};LzSprite.prototype.setAAName=function($1){

};LzSprite.prototype.aafocus=function(){
try{
if(this.__LZdiv!=null){
this.__LZdiv.blur();this.__LZdiv.focus()
}}
catch(e){

}};LzSprite.prototype.setAATabIndex=function($1){

};LzSprite.prototype.sendAAEvent=function($1,$2,$3){
try{
if(this.__LZdiv!=null){
this.__LZdiv.focus()
}}
catch(e){

}};LzSprite.prototype.setID=function($1){
if(!this._id){
this._id=$1
};if(!this.__LZdiv.id){
this.__LZdiv.id=this._dbg_typename+$1
};if(!this.__LZclickcontainerdiv.id){
this.__LZclickcontainerdiv.id="click"+$1
};if(this.__LZcontextcontainerdiv&&!this.__LZcontextcontainerdiv.id){
this.__LZcontextcontainerdiv.id=this.__LZcontextcontainerdiv.id="context"+$1
}};Class.make("LzLibrary",LzNode,["loaded",false,"loading",false,"sprite",null,"href",void 0,"stage","late","onload",LzDeclaredEvent,"construct",function($1,$2){
this.stage=$2.stage;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.sprite=new LzSprite(this,false,$2);LzLibrary.libraries[$2.name]=this
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(this.stage=="late"){
this.load()
}},"destroy",function(){
if(this.sprite){
this.sprite.destroy();this.sprite=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"toString",function(){
return "Library "+this.href+" named "+this.name
},"load",function(){
if(this.loading||this.loaded){
return
};this.loading=true;lz.embed.__dhtmlLoadLibrary(this.href)
},"loadfinished",function(){
this.loading=false;if(this.onload.ready){
this.onload.sendEvent(true)
}}],["tagname","import","attributes",new LzInheritedHash(LzNode.attributes),"libraries",{},"findLibrary",function($1){
return LzLibrary.libraries[$1]
},"stripQueryString",function($1){
if($1.indexOf("?")>0){
$1=$1.substring(0,$1.indexOf("?"))
};return $1
},"__LZsnippetLoaded",function($1){
$1=LzLibrary.stripQueryString($1);var $2=null;var $3=LzLibrary.libraries;for(var $4 in $3){
var $5=LzLibrary.stripQueryString($3[$4].href);if($5==$1){
$2=$3[$4];break
}};if($2==null){

}else{
$2.loaded=true;canvas.initiatorAddNode({attrs:{libname:$2.name},"class":LzLibraryCleanup},1);canvas.initDone()
}}]);lz[LzLibrary.tagname]=LzLibrary;var LzTextSprite=function($1){
if($1==null){
return
};this.constructor=arguments.callee;this.owner=$1;this.uid=LzSprite.prototype.uid++;this.__LZdiv=document.createElement("div");this.__LZdiv.className="lztextcontainer";this.scrolldiv=this.__LZtextdiv=document.createElement("div");this.scrolldiv.owner=this;this.scrolldiv.className="lztext";this.__LZdiv.appendChild(this.scrolldiv);if(this.quirks.emulate_flash_font_metrics){
this.scrolldiv.className="lzswftext"
};this.__LZdiv.owner=this;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lztextcontainer_click";this.__LZclickcontainerdiv.owner=this
};if(this.quirks.ie_leak_prevention){
this.__sprites[this.uid]=this
}};LzTextSprite.prototype=new LzSprite(null);LzTextSprite.prototype.__initTextProperties=function($1){
this.setFontName($1.font);this.setFontStyle($1.fontstyle);this.setFontSize($1.fontsize)
};LzTextSprite.prototype._fontStyle="normal";LzTextSprite.prototype._fontWeight="normal";LzTextSprite.prototype._fontSize="11px";LzTextSprite.prototype._fontFamily="Verdana,Vera,sans-serif";LzTextSprite.prototype._whiteSpace="normal";LzTextSprite.prototype._textAlign="left";LzTextSprite.prototype._textIndent="0px";LzTextSprite.prototype.__LZtextIndent=0;LzTextSprite.prototype._letterSpacing="0px";LzTextSprite.prototype._textDecoration="none";LzTextSprite.prototype.__wpadding=4;LzTextSprite.prototype.__hpadding=4;LzTextSprite.prototype.__sizecacheupperbound=1000;LzTextSprite.prototype.selectable=true;LzTextSprite.prototype.text="";LzTextSprite.prototype.resize=true;LzTextSprite.prototype.restrict=null;LzTextSprite.prototype.setFontSize=function($1){
if($1==null||$1<0){
return
};var $2=this.CSSDimension($1);if(this._fontSize!=$2){
this._fontSize=$2;this.scrolldiv.style.fontSize=$2;if(this.quirks["emulate_flash_font_metrics"]){
var $3=Math.round($1*1.2);this.scrolldiv.style.lineHeight=this.CSSDimension($3);this._lineHeight=$3
};this.__updatefieldsize()
}};LzTextSprite.prototype.setFontStyle=function($1){
var $2;if($1=="plain"){
$2="normal";$1="normal"
}else{
if($1=="bold"){
$2="bold";$1="normal"
}else{
if($1=="italic"){
$2="normal";$1="italic"
}else{
if($1=="bold italic"||$1=="bolditalic"){
$2="bold";$1="italic"
}}}};if($2!=this._fontWeight){
this._fontWeight=$2;this.scrolldiv.style.fontWeight=$2;this.__updatefieldsize()
};if($1!=this._fontStyle){
this._fontStyle=$1;this.scrolldiv.style.fontStyle=$1;this.__updatefieldsize()
}};LzTextSprite.prototype.setFontName=function($1){
if($1!=this._fontFamily){
this._fontFamily=$1;this.scrolldiv.style.fontFamily=$1;this.__updatefieldsize()
}};LzTextSprite.prototype.setTextColor=LzSprite.prototype.setColor;LzTextSprite.prototype.lineHeight=null;LzTextSprite.prototype.scrollTop=null;LzTextSprite.prototype.scrollHeight=null;LzTextSprite.prototype.scrollLeft=null;LzTextSprite.prototype.scrollWidth=null;LzTextSprite.prototype.scrollevents=false;LzTextSprite.prototype.setScrollEvents=function($1){
var $2=this.scrolldiv;var $3=$2.className;if($3=="lzswftext"||$3=="lzswfinputtextmultiline"){
var $4=this.height;var $5=this.width;var $6=this.CSSDimension;if($1){
this.scrollevents=true;$2.style.overflow="scroll";$4+=this.quirks.scrollbar_width;$5+=this.quirks.scrollbar_width
}else{
this.scrollevents=false;$2.style.overflow="hidden"
};var $7=$6($4);var $8=$6($5);if($1){
$2.style.clip="rect(0 "+$8+" "+$7+" 0)"
}else{
if($2.style.clip){
$2.style.clip=""
}};$2.style.height=$7;$2.style.width=$8
}};LzTextSprite.prototype.__updatefieldsize=function(){
var $1=this.getLineHeight();if(this.lineHeight!==$1){
this.lineHeight=$1;this.owner.scrollevent("lineHeight",$1)
};if(!this.scrollevents){
return
};this.__updatefieldprop("scrollHeight");this.__updatefieldprop("scrollTop");this.__updatefieldprop("scrollWidth");this.__updatefieldprop("scrollLeft")
};LzTextSprite.prototype.__updatefieldprop=function($1){
var $2=this.scrolldiv[$1];if(this[$1]!==$2){
this[$1]=$2;this.owner.scrollevent($1,$2)
}};LzTextSprite.prototype.setText=function($1,$2){
if(this.multiline&&$1&&$1.indexOf("\n")>=0){
if(this.quirks["inner_html_strips_newlines"]){
$1=$1.replace(this.inner_html_strips_newlines_re,"<br/>")
}};if($1&&this.quirks["inner_html_no_entity_apos"]){
$1=$1.replace(RegExp("&apos;","mg"),"&#39;")
};if($2!=true&&this.text==$1){
return
};this.text=$1;this.scrolldiv.innerHTML=$1;this.__updatefieldsize();if(this.resize&&this.multiline==false){
this.setWidth(this.getTextWidth())
}};LzTextSprite.prototype.setMultiline=function($1){
$1=$1==true;if(this.multiline==$1){
return
};this.multiline=$1;if($1){
if(this._whiteSpace!="normal"){
this._whiteSpace="normal";this.scrolldiv.style.whiteSpace="normal"
}}else{
if(this._whiteSpace!="nowrap"){
this._whiteSpace="nowrap";this.scrolldiv.style.whiteSpace="nowrap"
}};if(this.quirks["text_height_includes_padding"]){
this.__hpadding=$1?3:4
};this.setText(this.text,true)
};LzTextSprite.prototype.setPattern=function($1){
if($1==null||$1==""){
this.restrict=null
}else{
if(RegExp("^\\[.*\\]\\*$").test($1)){
this.restrict=RegExp($1.substring(0,$1.length-1)+"|[\\r\\n]","g")
}else{

}}};LzTextSprite.prototype.getTextWidth=function(){
var $1;var $2=this.scrolldiv;var $3=$2.className;var $4=$2.style.cssText;var $5=$3+"/"+$4;var $6=this._cachevalue;if(this._cacheStyleKey==$5&&this._cacheTextKey==this.text&&("width" in $6)){
$1=$6.width
}else{
$1=this.getTextDimension("width")
};if($1!=0&&this.quirks["emulate_flash_font_metrics"]){
$1+=this.__wpadding
};return $1
};LzTextSprite.prototype.getLineHeight=function(){
if(this._lineHeight){
return this._lineHeight
};var $1=this.scrolldiv;var $2=$1.className;var $3=$1.style.cssText;var $4=$2+"/"+$3;var $5=this._cachevalue;if(this._cacheStyleKey==$4&&("lineheight" in $5)){
var $6=$5.lineheight
}else{
var $6=this.getTextDimension("lineheight")
};return $6
};LzTextSprite.prototype.getTextHeight=LzTextSprite.prototype.getLineHeight;LzTextSprite.prototype.getTextfieldHeight=function(){
var $1=null;if(this.multiline&&this.text!=""){
var $2=this.scrolldiv;var $3=$2.className;var $4=$2.style.cssText;var $5=$3+"/"+$4;var $6=this._cachevalue;if(this._cacheStyleKey==$5&&this._cacheTextKey==this.text&&("height" in $6)){
$1=$6.height
}else{
$1=this.getTextDimension("height")
};if(this.quirks["safari_textarea_subtract_scrollbar_height"]){
$1+=24
}}else{
$1=this.getLineHeight()
};if(this.quirks["emulate_flash_font_metrics"]){
$1+=this.__hpadding
};return $1
};LzTextSprite.prototype._sizecache={counter:0};if(LzSprite.quirks.ie_leak_prevention){
LzTextSprite.prototype.__divstocleanup=[];LzTextSprite.prototype.__cleanupdivs=function(){
var $1=LzTextSprite.prototype.__divstocleanup;var $2=LzSprite.prototype.__discardElement;var $3=$1.length;for(var $4=0;$4<$3;$4++){
$2($1[$4])
};LzTextSprite.prototype.__divstocleanup=[]
}};LzTextSprite.prototype._cacheStyleKey=null;LzTextSprite.prototype._cacheTextKey=null;LzTextSprite.prototype._cachevalue=null;LzTextSprite.prototype.getTextDimension=function($1){
var $2=this.text;var $3="auto";switch($1){
case "lineheight":
if(this._lineHeight){
return this._lineHeight
};$2='Yq_gy"9;';break;
case "height":
$3=this.CSSDimension(this.width);break;
case "width":
if(this.text==""){
return 0
};break;
default:


};var $4=this.scrolldiv;var $5=$4.className;var $6=$4.style.cssText;var $7=$5+"/"+$6;var $8=this._cachevalue;if(this._cacheStyleKey==$7&&($1=="lineheight"||this._cacheTextKey==$2)&&($1 in $8)){
return $8[$1]
};this._cacheStyleKey=$7;if($1!="lineheight"){
this._cacheTextKey=$2
};var $9=$4.style;$6="overflow: visible; width: "+$3+"; height: auto; "+($9.fontSize?"font-size: "+$9.fontSize+"; ":"")+($9.fontWeight?"font-weight: "+$9.fontWeight+"; ":"")+($9.fontStyle?"font-style: "+$9.fontStyle+"; ":"")+($9.fontFamily?"font-family: "+$9.fontFamily+"; ":"")+($9.lineHeight?"line-height: "+$9.lineHeight+"; ":"")+($9.letterSpacing?"letter-spacing: "+$9.letterSpacing+"; ":"")+($9.whiteSpace?"white-space: "+$9.whiteSpace+"; ":"");var $10=$5+"/"+$6+"{"+$2+"}";var $11=LzTextSprite.prototype;var $12=$11._sizecache;var $8=this._cachevalue=$12[$10];if($8&&($1 in $8)){
return $8[$1]
};var $13=document.getElementById("lzTextSizeCache");if($12.counter>0&&$12.counter%this.__sizecacheupperbound==0){
$11._sizecache=$12={counter:0};$8=null;if(LzSprite.quirks.ie_leak_prevention){
$11.__cleanupdivs()
};if($13){
$13.innerHTML=""
}};if(!$8){
$8=this._cachevalue=$12[$10]={}};if(!$13){
$13=document.createElement("div");lz.embed.__setAttr($13,"id","lzTextSizeCache");document.body.appendChild($13)
};var $14="div";var $15=$5+"/"+$6+"div";var $16=$12[$15];if($16){
this.__setTextContent($16,$4.tagName,$2)
}else{
if(this.quirks["text_measurement_use_insertadjacenthtml"]){
var $17="<"+$14+' id="testSpan'+$12.counter+'"';$17+=' class="'+$5+'"';$17+=' style="'+$6+'">';$17+=$2;$17+="</"+$14+">";$13.insertAdjacentHTML("beforeEnd",$17);var $16=document.all["testSpan"+$12.counter];if(this.quirks.ie_leak_prevention){
$11.__divstocleanup.push($16)
}}else{
var $16=document.createElement($14);lz.embed.__setAttr($16,"class",$5);lz.embed.__setAttr($16,"style",$6);this.__setTextContent($16,$4.tagName,$2);$13.appendChild($16)
};$12[$15]=$16
};$16.style.display="inline";$8[$1]=$1=="width"?$16.clientWidth:$16.clientHeight;$16.style.display="none";$12.counter++;return $8[$1]
};LzTextSprite.prototype.__setTextContent=function($1,$2,$3){
switch($2){
case "DIV":
$1.innerHTML=$3;break;
case "INPUT":
case "TEXTAREA":
if(this.quirks["text_content_use_inner_text"]){
$1.innerText=$3
}else{
$1.textContent=$3
};break;
default:


}};LzTextSprite.prototype.setSelectable=function($1){
this.selectable=$1;var $2=lz.embed.browser;if($1){
this.__LZdiv.style["cursor"]="auto";if($2.isIE){
this.__LZdiv.onselectstart=null
}else{
if($2.isFirefox){
this.__LZdiv.style["MozUserSelect"]="text"
}else{
if($2.isSafari){
this.__LZdiv.style["WebkitUserSelect"]="text"
}else{
this.__LZdiv.style["UserSelect"]="text"
}}}}else{
this.__LZdiv.style["cursor"]="";if($2.isIE){
this.__LZdiv.onselectstart=LzTextSprite.prototype.__cancelhandler
}else{
if($2.isFirefox){
this.__LZdiv.style["MozUserSelect"]="none"
}else{
if($2.isSafari){
this.__LZdiv.style["WebkitUserSelect"]="none"
}else{
this.__LZdiv.style["UserSelect"]="none"
}}}}};LzTextSprite.prototype.__cancelhandler=function(){
return false
};LzTextSprite.prototype.setResize=function($1){
this.resize=$1==true
};LzTextSprite.prototype.setSelection=function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($2==null){
$2=$1
};if(this.quirks["text_selection_use_range"]){
var $3=document.body.createTextRange();$3.moveToElementText(this.scrolldiv);if($1>$2){
var $4=$1;$1=$2;$2=$4
};var $4=$1;var $5=$2-$3.text.length;$3.moveStart("character",$4);$3.moveEnd("character",$5);$3.select()
}else{
var $3=document.createRange();var $6=0;$3.setStart(this.scrolldiv.childNodes[0],$1);$3.setEnd(this.scrolldiv.childNodes[0],$2);var $7=window.getSelection();$7.removeAllRanges();$7.addRange($3)
}};LzTextSprite.prototype.__findNodeByOffset=function($1){
var $2=this.scrolldiv.childNodes[0];var $3=0;while($2){
if($2.nodeType==3){
$1+=$2.textContent.length
}else{
if($2.nodeType==1&&$2.nodeName=="BR"){
$1+=1
}};if($3>=$1){
return $2
};$2=$2.nextSibling
}};LzTextSprite.prototype.__getGlobalRange=function(){
var $1=lz.embed.browser;var $2;if(this.quirks["text_selection_use_range"]){
$2=document.selection.createRange()
}else{
if(window.getSelection){
$2=window.getSelection()
}};try{
if($2){
if(this.quirks["text_selection_use_range"]){
return $2
}else{
if($2.getRangeAt){
return $2.getRangeAt(0)
}else{
var $3=document.createRange();$3.setStart($2.anchorNode,$2.anchorOffset);$3.setEnd($2.focusNode,$2.focusOffset);return $3
}}}}
catch(e){

}};LzTextSprite.prototype.__textareaToRange=function($1){
var $2=$1.getBookmark();var $3,$4;$4=$3=this.scrolldiv.innerHTML;var $5=this.__getRangeOwner($1);if(!($5 instanceof LzTextSprite)){
return
};do{
var $6="~~~"+Math.random()+"~~~"
}while($3.indexOf($6)!=-1);$1.text=$6+$1.text+$6;$3=this.scrolldiv.innerHTML;$3=$3.replace("<BR>"," ");var $7={};$7.startOffset=$3.indexOf($6);$3=$3.replace($6,"");$7.endOffset=$3.indexOf($6);this.scrolldiv.innerHTML=$4;$1.moveToBookmark($2);$1.select();return $7
};LzTextSprite.prototype.__getRangeOwner=function($1){
if(!$1){
return
};if(this.quirks["text_selection_use_range"]){
var $1=$1.duplicate();$1.collapse();return $1.parentElement().owner
}else{
if($1.startContainer.parentNode==$1.endContainer.parentNode){
return $1.startContainer.parentNode.owner
}}};LzTextSprite.prototype.__getOffset=function($1){
var $2=0;while($1=$1.previousSibling){
if($1.nodeType==3){
$2+=$1.textContent.length
}else{
if($1.nodeType==1&&$1.nodeName=="BR"){
$2+=1
}}};return $2
};LzTextSprite.prototype.getSelectionPosition=function(){
var $1=this.__getGlobalRange();if(this.__getRangeOwner($1)===this){
if(this.quirks["text_selection_use_range"]){
$1=this.__textareaToRange($1);return $1.startOffset
}else{
var $2=0;if(this.multiline){
$2=this.__getOffset($1.startContainer)
};return $1.startOffset+$2
}}else{
return -1
}};LzTextSprite.prototype.getSelectionSize=function(){
var $1=this.__getGlobalRange();if(this.__getRangeOwner($1)===this){
if(this.quirks["text_selection_use_range"]){
$1=this.__textareaToRange($1)
}else{
if(this.multiline){
var $2=this.__getOffset($1.startContainer);var $3=this.__getOffset($1.endContainer);return $1.endOffset+$3-($1.startOffset+$2)
}};return $1.endOffset-$1.startOffset
}else{
return -1
}};LzTextSprite.prototype.getScroll=function(){

};LzTextSprite.prototype.getMaxScroll=function(){

};LzTextSprite.prototype.setScroll=function(){

};LzTextSprite.prototype.setYScroll=function($1){
this.scrolldiv.scrollTop=this.scrollTop=-$1
};LzTextSprite.prototype.setXScroll=function($1){
this.scrolldiv.scrollLeft=this.scrollLeft=-$1
};LzTextSprite.prototype.setX=function($1){
var $2=this.scrollevents;var $3=$2&&this.quirks["clipped_scrollbar_causes_display_turd"];if($2){
var $4=this.scrolldiv;var $5=$4.scrollLeft;var $6=$4.scrollTop;if($3){
$4.style.overflow="hidden";$4.style.paddingRight=$4.style.paddingBottom=this.quirks.scrollbar_width
}};LzSprite.prototype.setX.call(this,$1);if($2){
if($3){
$4.style.overflow="scroll";$4.style.paddingRight=$4.style.paddingBottom="0"
};$4.scrollLeft=$5;$4.scrollTop=$6
}};LzTextSprite.prototype.setY=function($1){
var $2=this.scrollevents;var $3=$2&&this.quirks["clipped_scrollbar_causes_display_turd"];if($2){
var $4=this.scrolldiv;var $5=$4.scrollLeft;var $6=$4.scrollTop;if($3){
$4.style.overflow="hidden";$4.style.paddingRight=$4.style.paddingBottom=this.quirks.scrollbar_width
}};LzSprite.prototype.setY.call(this,$1);if($2){
if($3){
$4.style.overflow="scroll";$4.style.paddingRight=$4.style.paddingBottom="0"
};$4.scrollLeft=$5;$4.scrollTop=$6
}};LzTextSprite.prototype.setWidth=function($1,$2){
if($1==null||$1<0||isNaN($1)){
return
};var $3=LzSprite.prototype.setWidth.call(this,$1);var $4=$1;var $5=this.scrolldiv;var $6=$5.style;var $7=this.CSSDimension;var $8=$7($4);var $9=this.height;var $10=$7($9!=null?$9:0);if(this.scrollevents){
$4+=this.quirks.scrollbar_width
};var $11=this.__LZtextIndent<0?-1*this.__LZtextIndent:0;if($4>=$11){
$4-=$11
};$8=$7($4);if($6.width!=$8){
$5.style.width=$8;if(this.scrollevents){
$5.style.clip="rect(0 "+$8+" "+$10+" 0)"
};this.__updatefieldsize()
};return $3
};LzTextSprite.prototype.setHeight=function($1){
if($1==null||$1<0||isNaN($1)){
return
};var $2=LzSprite.prototype.setHeight.call(this,$1);var $3=$1;var $4=this.scrolldiv;var $5=$4.style;var $6=this.CSSDimension;var $7=this.width;var $8=$6($7!=null?$7:0);var $9=$6($3);if(this.scrollevents){
$3+=this.quirks.scrollbar_width
};$9=$6($3);if($5.height!=$9){
$4.style.height=$6($3);if(this.scrollevents){
$4.style.clip="rect(0 "+$8+" "+$9+" 0)"
};this.__updatefieldsize()
};return $2
};LzTextSprite.prototype.enableClickableLinks=function($1){

};LzTextSprite.prototype.makeTextLink=function($1,$2){
LzTextSprite.addLinkID(this.owner);var $3=this.owner.getUID();return '<span class="lztextlink" onclick="javascript:$modules.lz.__callTextLink(\''+$3+"', '"+$2+"')\">"+$1+"</span>"
};$modules.lz.__callTextLink=function($1,$2){
var $3=LzTextSprite.linkIDMap[$1];if($3!=null){
$3.ontextlink.sendEvent($2)
}};LzTextSprite.linkIDMap=[];LzTextSprite.addLinkID=function($1){
LzTextSprite.linkIDMap[$1.getUID()]=$1
};LzTextSprite.deleteLinkID=function($1){
delete LzTextSprite.linkIDMap[$1]
};LzTextSprite.prototype._viewdestroy=LzSprite.prototype.destroy;LzTextSprite.prototype.destroy=function(){
LzTextSprite.deleteLinkID(this.owner.getUID());this._viewdestroy()
};LzTextSprite.prototype.setTextAlign=function($1){
if(this._textAlign!=$1){
this._textAlign=$1;this.scrolldiv.style.textAlign=$1
}};LzTextSprite.prototype.setTextIndent=function($1){
var $2=this.CSSDimension($1);if(this._textIndent!=$2){
var $3=$1<0||this.__LZtextIndent<0;this._textIndent=$2;this.__LZtextIndent=$1;this.scrolldiv.style.textIndent=$2;if($3){
this.scrolldiv.style.paddingLeft=$1>=0?"":$2.substr(1);this.setWidth(this.width,true)
}}};LzTextSprite.prototype.setLetterSpacing=function($1){
$1=this.CSSDimension($1);if(this._letterSpacing!=$1){
this._letterSpacing=$1;this.scrolldiv.style.letterSpacing=$1
}};LzTextSprite.prototype.setTextDecoration=function($1){
if(this._textDecoration!=$1){
this._textDecoration=$1;this.scrolldiv.style.textDecoration=$1
}};var LzInputTextSprite=function($1){
if($1==null){
return
};this.constructor=arguments.callee;this.owner=$1;this.uid=LzSprite.prototype.uid++;this.__LZdiv=document.createElement("div");this.__LZdiv.className="lzinputtextcontainer";this.__LZdiv.owner=this;this.dragging=false;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lzinputtextcontainer_click";this.__LZclickcontainerdiv.owner=this
};if(this.quirks.ie_leak_prevention){
this.__sprites[this.uid]=this
};this.__createInputText()
};LzInputTextSprite.prototype=new LzTextSprite(null);LzInputTextSprite.prototype.__lastshown=null;LzInputTextSprite.prototype.__focusedSprite=null;LzInputTextSprite.prototype.__lastfocus=null;LzInputTextSprite.prototype._cancelfocus=false;LzInputTextSprite.prototype._cancelblur=false;LzInputTextSprite.prototype.____crregexp=new RegExp("\\r\\n","g");LzInputTextSprite.prototype.__createInputText=function($1){
if(this.__LzInputDiv){
return
};var $2="";if(this.owner){
if(this.owner.password){
$2="password"
}else{
if(this.owner.multiline){
$2="multiline"
}}};this.__createInputDiv($2);if($1==null){
$1=""
};lz.embed.__setAttr(this.__LzInputDiv,"value",$1);if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable){
this.__LZinputclickdiv=document.createElement("img");this.__LZinputclickdiv.src=lz.embed.options.serverroot+LzSprite.prototype.blankimage
}else{
this.__LZinputclickdiv=document.createElement("div")
};this.__LZinputclickdiv.className="lzclickdiv";this.__LZinputclickdiv.owner=this;if(this.quirks.ie_mouse_events){
this.__LZinputclickdiv.onmouseenter=this.__handlemouse
}else{
this.__LZinputclickdiv.onmouseover=this.__handlemouse
};if(this.quirks.input_highlight_bug){
var $3=document.createElement("div");$3.style.backgroundColor="white";$3.style.width="0px";this.__LZclickcontainerdiv.appendChild($3);$3.appendChild(this.__LZinputclickdiv)
}else{
this.__LZclickcontainerdiv.appendChild(this.__LZinputclickdiv)
}};this.__LZdiv.appendChild(this.__LzInputDiv);this.__setTextEvents(true)
};LzInputTextSprite.prototype.__createInputDiv=function($1){
if($1==="password"){
this.multiline=false;this.__LzInputDiv=document.createElement("input");this.__LZdiv.className="lzinputtextcontainer";lz.embed.__setAttr(this.__LzInputDiv,"type","password")
}else{
if($1==="multiline"){
this.multiline=true;this.__LzInputDiv=document.createElement("textarea");this.__LZdiv.className="lzinputtextmultilinecontainer"
}else{
this.multiline=false;this.__LzInputDiv=document.createElement("input");this.__LZdiv.className="lzinputtextcontainer";lz.embed.__setAttr(this.__LzInputDiv,"type","text")
}};if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.className=this.__LZdiv.className+"_click"
};if(this.quirks.firefox_autocomplete_bug){
lz.embed.__setAttr(this.__LzInputDiv,"autocomplete","off")
};this.__LzInputDiv.owner=this;if(this.quirks.emulate_flash_font_metrics){
if(this.owner&&this.owner.multiline){
this.__LzInputDiv.className="lzswfinputtextmultiline"
}else{
this.__LzInputDiv.className="lzswfinputtext"
}}else{
this.__LzInputDiv.className="lzinputtext"
};if(this.owner){
lz.embed.__setAttr(this.__LzInputDiv,"name",this.owner.name)
};this.scrolldiv=this.__LzInputDiv;this.scrolldiv.owner=this
};LzInputTextSprite.prototype.setMultiline=function($1){
var $2=this.multiline;this.multiline=$1==true;if($2!=null&&this.multiline!=$2){
var $3=this.__LzInputDiv;this.__setTextEvents(false);this.__createInputDiv($1?"multiline":"");var $4=this.__LzInputDiv;lz.embed.__setAttr($4,"style",$3.style.cssText);var $5=$3.scrollLeft;var $6=$3.scrollTop;this.__discardElement($3);this.__LZdiv.appendChild($4);this.setScrollEvents(this.owner.scrollevents);$4.scrollLeft=$5;$4.scrollTop=$6;this.__setTextEvents(true);this.setText(this.text,true)
}};LzInputTextSprite.prototype.__handlemouse=function($1){
var $2=this.owner;if(!$2||!$2.owner||$2.selectable!=true){
return
};if($2.__fix_inputtext_with_parent_resource){
if(!this.__shown){
$2.setClickable(true);$2.select()
}}else{
$2.__show()
}};LzInputTextSprite.prototype.init=function($1){
this.setVisible($1);if(this.quirks["fix_inputtext_with_parent_resource"]){
var $2=this.__findParents("clickable",true);var $3=$2.length;if($3){
for(var $4=0;$4<$3;$4++){
var $1=$2[$4];if($1.resource!=null){
this.setClickable(true);this.__fix_inputtext_with_parent_resource=true
}}}}};LzInputTextSprite.prototype.__show=function(){
if(this.__shown==true||this.disabled==true){
return
};this.__hideIfNotFocused();LzInputTextSprite.prototype.__lastshown=this;this.__shown=true;if(this.quirks["inputtext_parents_cannot_contain_clip"]){
var $1=this.__findParents("clip",true);var $2=$1.length;if($2>1){
if(this._shownclipvals==null){
this._shownclipvals=[];this._shownclippedsprites=$1;for(var $3=0;$3<$2;$3++){
var $4=$1[$3];this._shownclipvals[$3]=$4.__LZclickcontainerdiv.style.clip;$4.__LZclickcontainerdiv.style.clip="rect(auto auto auto auto)"
}}}};LzMouseKernel.setGlobalClickable(false);if(LzSprite.quirks.prevent_selection){
this.__LZdiv.onselectstart=null
}};LzInputTextSprite.prototype.__hideIfNotFocused=function($1,$2){
var $3=LzInputTextSprite.prototype;if($3.__lastshown==null){
return
};var $4=LzSprite.quirks;if($4.textgrabsinputtextfocus){
var $5=window.event;if($5&&$5.srcElement&&$5.srcElement.owner&&$5.srcElement.owner instanceof LzTextSprite){
if($1=="onmousedown"){
$3.__lastshown.gotFocus()
};return
}};if($3.__focusedSprite!=$3.__lastshown){
$3.__lastshown.__hide()
}};LzInputTextSprite.prototype.__hide=function($1){
if(this.__shown!=true||this.disabled==true){
return
};if(LzInputTextSprite.prototype.__lastshown==this){
LzInputTextSprite.prototype.__lastshown=null
};this.__shown=false;if(this.quirks["inputtext_parents_cannot_contain_clip"]){
if(this._shownclipvals!=null){
for(var $2=0;$2<this._shownclipvals.length;$2++){
var $3=this._shownclippedsprites[$2];$3.__LZclickcontainerdiv.style.clip=this._shownclipvals[$2]
};this._shownclipvals=null;this._shownclippedsprites=null
}};LzMouseKernel.setGlobalClickable(true);if(this.__fix_inputtext_with_parent_resource){
this.setClickable(false)
};if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.quirks.prevent_selection){
this.__LZdiv.onselectstart=LzTextSprite.prototype.__cancelhandler
}}};LzInputTextSprite.prototype.gotBlur=function(){
if(LzInputTextSprite.prototype.__focusedSprite!=this){
return
};this.deselect()
};LzInputTextSprite.prototype.gotFocus=function(){
if(LzInputTextSprite.prototype.__focusedSprite==this){
return
};this.select()
};LzInputTextSprite.prototype.setText=function($1){
if(this.capabilities["htmlinputtext"]){
if($1.indexOf("<br/>")!=-1){
$1=$1.replace(this.br_to_newline_re,"\r")
}};this.text=$1;this.__createInputText($1);this.__LzInputDiv.value=$1;this.__updatefieldsize()
};LzInputTextSprite.prototype.__setTextEvents=function($1){
var $2=this.__LzInputDiv;var $3=$1?this.__textEvent:null;$2.onblur=$3;$2.onfocus=$3;if(this.quirks.ie_mouse_events){
$2.ondrag=$3;$2.ondblclick=$3;$2.onmouseenter=$3;$2.onmouseleave=$3
}else{
$2.onmouseover=$3;$2.onmouseout=$3
};$2.onmousemove=$3;$2.onmousedown=$3;$2.onmouseup=$3;$2.onclick=$3;$2.onkeyup=$3;$2.onkeydown=$3;$2.onkeypress=$3;$2.onchange=$3;if(this.quirks.ie_paste_event||this.quirks.safari_paste_event){
$2.onpaste=$1?(function($1){
this.owner.__pasteHandlerEx($1)
}):null
}};LzInputTextSprite.prototype.__pasteHandlerEx=function($1){
var $2=!(!this.restrict);var $3=this.multiline&&this.owner.maxlength>0;if($2||$3){
$1=$1?$1:window.event;if(this.quirks.safari_paste_event){
var $4=$1.clipboardData.getData("text/plain")
}else{
var $4=window.clipboardData.getData("TEXT");$4=$4.replace(this.____crregexp,"\n")
};var $5=false;var $6=this.getSelectionSize();if($6<0){
$6=0
};if($2){
var $7=$4.match(this.restrict);if($7==null){
var $8=""
}else{
var $8=$7.join("")
};$5=$8!=$4;$4=$8
};if($3){
var $9=this.owner.maxlength+$6;if(this.quirks.text_ie_carriagereturn){
var $10=this.__LzInputDiv.value.replace(this.____crregexp,"\n").length
}else{
var $10=this.__LzInputDiv.value.length
};var $11=$9-$10;if($11>0){
if($4.length>$11){
$4=$4.substring(0,$11);$5=true
}}else{
$4="";$5=true
}};if($5){
$1.returnValue=false;if($1.preventDefault){
$1.preventDefault()
};if($4.length>0){
if(this.quirks.safari_paste_event){
var $12=this.__LzInputDiv.value;var $13=this.getSelectionPosition();this.__LzInputDiv.value=$12.substring(0,$13)+$4+$12.substring($13+$6);$13+=$4.length;this.__LzInputDiv.setSelectionRange($13,$13)
}else{
var $14=document.selection.createRange();$14.text=$4
}}}}};LzInputTextSprite.prototype.__pasteHandler=function(){
var selpos=this.getSelectionPosition();var selsize=this.getSelectionSize();var val=this.__LzInputDiv.value;var that=this;setTimeout(function(){
var $1=!(!that.restrict);var $2=that.multiline&&that.owner.maxlength>0;var $3=that.__LzInputDiv.value;var $4=$3.length;var $5=that.owner.maxlength;if($1||$2&&$4>$5){
var $6=val.length;var $7=$3.substr(selpos,$4-$6+selsize);if($1){
var $8=$7.match(that.restrict);$7=$8!=null?$8.join(""):""
};if($2){
var $9=$5+selsize-$6;$7=$7.substring(0,$9)
};that.__LzInputDiv.value=val.substring(0,selpos)+$7+val.substring(selpos+selsize);selpos+=$7.length;that.__LzInputDiv.setSelectionRange(selpos,selpos)
}},1)
};LzInputTextSprite.prototype.__textEvent=function($1){
if(!$1){
$1=window.event
};var $2=this.owner;var $3=this.owner.owner;if($2.__LZdeleted==true){
return
};if($2.__skipevent){
$2.__skipevent=false;return
};var $4="on"+$1.type;var $5=$2.quirks;LzMouseKernel.__sendMouseMove($1);if($5.ie_mouse_events){
if($4=="onmouseenter"){
$4="onmouseover"
}else{
if($4=="onmouseleave"){
$4="onmouseout"
}else{
if($4=="ondblclick"){
if($2.clickable){
$2.__mouseEvent("onmousedown",true);$2.__mouseEvent("onmouseup",true);$2.__mouseEvent("onclick",true)
};return false
}else{
if($4=="ondrag"){
return false
}}}}};if($5.autoscroll_textarea){
if($4=="onmousedown"){
$2.dragging=true
}else{
if($4=="onmouseup"||$4=="onmouseout"){
$2.dragging=false
}}};if($2.__shown!=true){
if($4=="onfocus"){
$2.__skipevent=true;$2.__show();$2.__LzInputDiv.blur();LzInputTextSprite.prototype.__lastfocus=$2;LzKeyboardKernel.setKeyboardControl(true)
}};if($4=="onfocus"||$4=="onmousedown"){
if($4=="onfocus"){
LzMouseKernel.setGlobalClickable(false)
};LzInputTextSprite.prototype.__focusedSprite=$2;$2.__show();if($4=="onfocus"&&$2._cancelfocus){
$2._cancelfocus=false;return
};if(window["LzKeyboardKernel"]){
LzKeyboardKernel.__cancelKeys=false
}}else{
if($4=="onblur"){
if(window["LzKeyboardKernel"]){
LzKeyboardKernel.__cancelKeys=true
};if(LzInputTextSprite.prototype.__focusedSprite===$2){
LzInputTextSprite.prototype.__focusedSprite=null
};if($2.__fix_inputtext_with_parent_resource&&$2.__isMouseOver()){
$2.select();return
};$2.__hide();if($2._cancelblur){
$2._cancelblur=false;return
}}else{
if($4=="onmousemove"){
if($5.autoscroll_textarea&&$2.dragging){
var $6=$2.__LzInputDiv;var $7=$1.pageY-$6.offsetTop;if($7<=3){
$6.scrollTop-=$2.lineHeight?$2.lineHeight:10
};if($7>=$6.clientHeight-3){
$6.scrollTop+=$2.lineHeight?$2.lineHeight:10
}};return
}else{
if($4=="onkeypress"){
if($2.restrict||$2.multiline&&$3.maxlength&&$3.maxlength<Infinity){
var $8=$1.keyCode;var $9=$5.text_event_charcode?$1.charCode:$1.keyCode;var $10=!($1.ctrlKey||$1.altKey)&&($9>=32||$8==13);if($10){
var $11=false;if($8!=13&&$2.restrict){
$11=0>String.fromCharCode($9).search($2.restrict)
};if(!$11){
var $12=$2.getSelectionSize();if($12<=0){
if($5.text_ie_carriagereturn){
var $13=$2.__LzInputDiv.value.replace($2.____crregexp,"\n")
}else{
var $13=$2.__LzInputDiv.value
};var $14=$13.length,$15=$3.maxlength;if($14>=$15){
$11=true
}}};if($11){
$1.returnValue=false;if($1.preventDefault){
$1.preventDefault()
}}}else{
if($5.keypress_function_keys){
var $16=false;if($1.ctrlKey&&!$1.altKey&&!$1.shiftKey){
var $17=String.fromCharCode($9);$16=$17=="v"||$17=="V"
}else{
if($1.shiftKey&&!$1.altKey&&!$1.ctrlKey){
$16=$8==45
}};if($16){
if($2.restrict){
$2.__pasteHandler()
}else{
var $14=$2.__LzInputDiv.value.length,$15=$3.maxlength;if($14<$15||$2.getSelectionSize()>0){
$2.__pasteHandler()
}else{
$1.returnValue=false;if($1.preventDefault){
$1.preventDefault()
}}}}}};$2.__updatefieldsize()
};return
}}}};if($3){
if($4=="onkeydown"||$4=="onkeyup"){
var $6=$2.__LzInputDiv;var $18=$6.value;if($18!=$2.text){
$2.text=$18;$2.__updatefieldsize();$3.inputtextevent("onchange",$18)
};if($5.autoscroll_textarea&&$4=="onkeydown"&&$6.selectionStart==$18.length){
$6.scrollTop=$6.scrollHeight-$6.clientHeight+20
}}else{
if($4=="onmousedown"||$4=="onmouseup"||$4=="onmouseover"||$4=="onmouseout"||$4=="onclick"){
$2.__mouseEvent($1);$1.cancelBubble=true;if($4=="onmouseout"){
if(!$2.__isMouseOver()){
$2.__hide()
}};return
};$3.inputtextevent($4)
}}};LzInputTextSprite.prototype.setClickable=function($1){
this.clickable=$1
};LzInputTextSprite.prototype.setEnabled=function($1){
this.disabled=!$1;this.__LzInputDiv.disabled=this.disabled
};LzInputTextSprite.prototype.setMaxLength=function($1){
if($1==Infinity){
$1=~0>>>1
};this.__LzInputDiv.maxLength=$1
};LzInputTextSprite.prototype.select=function(){
this.__show();try{
this.__LzInputDiv.focus()
}
catch(err){

};LzInputTextSprite.prototype.__lastfocus=this;setTimeout(LzInputTextSprite.prototype.__selectLastFocused,50);if(window["LzKeyboardKernel"]){
LzKeyboardKernel.__cancelKeys=false
}};LzInputTextSprite.prototype.__selectLastFocused=function(){
if(LzInputTextSprite.prototype.__lastfocus!=null){
LzInputTextSprite.prototype.__lastfocus.__LzInputDiv.select()
}};LzInputTextSprite.prototype.setSelection=function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($2==null){
$2=$1
};this._cancelblur=true;this.__show();LzInputTextSprite.prototype.__lastfocus=this;if(this.quirks["text_selection_use_range"]){
var $3=this.__LzInputDiv.createTextRange();var $4=this.__LzInputDiv.value;if($1>$2){
var $5=$1;$1=$2;$2=$5
};if(this.multiline){
var $6=0;var $7=0;while($6<$1){
$6=$4.indexOf("\r\n",$6+2);if($6==-1){
break
};$7++
};var $8=0;while($6<$2){
$6=$4.indexOf("\r\n",$6+2);if($6==-1){
break
};$8++
};var $9=0;while($6<$4.length){
$6=$4.indexOf("\r\n",$6+2);if($6==-1){
break
};$9++
};var $10=$3.text.length;var $5=$1;var $11=$2-$4.length+$7+$8+$9+1
}else{
var $5=$1;var $11=$2-$3.text.length
};$3.moveStart("character",$5);$3.moveEnd("character",$11);$3.select()
}else{
this.__LzInputDiv.setSelectionRange($1,$2)
};this.__LzInputDiv.focus();if(window["LzKeyboardKernel"]){
LzKeyboardKernel.__cancelKeys=false
}};LzInputTextSprite.prototype.getSelectionPosition=function(){
if(!this.__shown||this.disabled==true){
return -1
};if(this.quirks["text_selection_use_range"]){
if(this.multiline){
var $1=this._getTextareaSelection()
}else{
var $1=this._getTextSelection()
};if($1){
return $1.start
}else{
return -1
}}else{
return this.__LzInputDiv.selectionStart
}};LzInputTextSprite.prototype.getSelectionSize=function(){
if(!this.__shown||this.disabled==true){
return -1
};if(this.quirks["text_selection_use_range"]){
if(this.multiline){
var $1=this._getTextareaSelection()
}else{
var $1=this._getTextSelection()
};if($1){
return $1.end-$1.start
}else{
return -1
}}else{
return this.__LzInputDiv.selectionEnd-this.__LzInputDiv.selectionStart
}};if(LzSprite.quirks["text_selection_use_range"]){
LzInputTextSprite.prototype._getTextSelection=function(){
this.__LzInputDiv.focus();var $1=document.selection.createRange();var $2=$1.getBookmark();var $3=contents=this.__LzInputDiv.value;do{
var $4="~~~"+Math.random()+"~~~"
}while(contents.indexOf($4)!=-1);var $5=$1.parentElement();if($5==null||!($5.type=="text"||$5.type=="textarea")){
return
};$1.text=$4+$1.text+$4;contents=this.__LzInputDiv.value;var $6={};$6.start=contents.indexOf($4);contents=contents.replace($4,"");$6.end=contents.indexOf($4);this.__LzInputDiv.value=$3;$1.moveToBookmark($2);$1.select();return $6
};LzInputTextSprite.prototype._getTextareaSelection=function(){
var $1=this.__LzInputDiv;var $2=document.selection.createRange().duplicate();if($2.parentElement()==$1){
var $3=document.body.createTextRange();$3.moveToElementText($1);$3.setEndPoint("EndToStart",$2);var $4=document.body.createTextRange();$4.moveToElementText($1);$4.setEndPoint("StartToEnd",$2);var $5=false,$6=false,$7=false;var $8,$9,$10,$11,$12,$13;$8=$9=$3.text;$10=$11=$2.text;$12=$13=$4.text;do{
if(!$5){
if($3.compareEndPoints("StartToEnd",$3)==0){
$5=true
}else{
$3.moveEnd("character",-1);if($3.text==$8){
$9+="\r\n"
}else{
$5=true
}}};if(!$6){
if($2.compareEndPoints("StartToEnd",$2)==0){
$6=true
}else{
$2.moveEnd("character",-1);if($2.text==$10){
$11+="\r\n"
}else{
$6=true
}}};if(!$7){
if($4.compareEndPoints("StartToEnd",$4)==0){
$7=true
}else{
$4.moveEnd("character",-1);if($4.text==$12){
$13+="\r\n"
}else{
$7=true
}}}}while(!$5||!$6||!$7);var $14=$9+$11+$13;var $15=false;if($1.value==$14){
$15=true
};var $16=$9.length;var $17=$16+$11.length;var $18=$11;var $19=this.__LzInputDiv.value;var $20=0;var $21=0;while($20<$16){
$20=$19.indexOf("\r\n",$20+2);if($20==-1){
break
};$21++
};var $22=0;while($20<$17){
$20=$19.indexOf("\r\n",$20+2);if($20==-1){
break
};$22++
};var $23=0;while($20<$19.length){
$20=$19.indexOf("\r\n",$20+2);if($20==-1){
break
};$23++
};$16-=$21;$17-=$22+$21;return {start:$16,end:$17}}}};LzInputTextSprite.prototype.deselect=function(){
this.__hide();if(this.__LzInputDiv&&this.__LzInputDiv.blur){
this.__LzInputDiv.blur()
};if(window["LzKeyboardKernel"]){
LzKeyboardKernel.__cancelKeys=true
}};LzInputTextSprite.prototype.__fontStyle="normal";LzInputTextSprite.prototype.__fontWeight="normal";LzInputTextSprite.prototype.__fontSize="11px";LzInputTextSprite.prototype.__fontFamily="Verdana,Vera,sans-serif";LzInputTextSprite.prototype.__setFontSize=LzTextSprite.prototype.setFontSize;LzInputTextSprite.prototype.setFontSize=function($1){
this.__setFontSize($1);if(this.__fontSize!=this._fontSize){
this.__fontSize=this._fontSize;this.__LzInputDiv.style.fontSize=this._fontSize
}};LzInputTextSprite.prototype.__setFontStyle=LzTextSprite.prototype.setFontStyle;LzInputTextSprite.prototype.setFontStyle=function($1){
this.__setFontStyle($1);if(this.__fontStyle!=this._fontStyle){
this.__fontStyle=this._fontStyle;this.__LzInputDiv.style.fontStyle=this._fontStyle
};if(this.__fontWeight!=this._fontWeight){
this.__fontWeight=this._fontWeight;this.__LzInputDiv.style.fontWeight=this._fontWeight
}};LzInputTextSprite.prototype.__setFontName=LzTextSprite.prototype.setFontName;LzInputTextSprite.prototype.setFontName=function($1){
this.__setFontName($1);if(this.__fontFamily!=this._fontFamily){
this.__fontFamily=this._fontFamily;this.__LzInputDiv.style.fontFamily=this._fontFamily
}};LzInputTextSprite.prototype.setWidth=function($1){
if($1==null||$1<0||isNaN($1)){
return
};var $2=LzTextSprite.prototype.setWidth.call(this,$1);if(this.quirks.fix_clickable&&$2!=null){
this.__LZinputclickdiv.style.width=$2
}};LzInputTextSprite.prototype.setHeight=function($1){
if($1==null||$1<0||isNaN($1)){
return
};var $2=LzTextSprite.prototype.setHeight.call(this,$1);if(this.quirks.fix_clickable&&$2!=null){
this.__LZinputclickdiv.style.height=$2
}};LzInputTextSprite.prototype.setColor=function($1){
if(this.color==$1){
return
};this.color=$1;this.__LzInputDiv.style.color=LzColorUtils.inttohex($1)
};LzInputTextSprite.prototype.getText=function(){
if(this.multiline&&this.quirks.text_ie_carriagereturn){
return this.__LzInputDiv.value.replace(this.____crregexp,"\n")
}else{
return this.__LzInputDiv.value
}};LzInputTextSprite.findSelection=function(){
if(LzInputTextSprite.__focusedSprite&&LzInputTextSprite.__focusedSprite.owner){
return LzInputTextSprite.__focusedSprite.owner
}};if(LzSprite.quirks.prevent_selection){
document.onselectstart=function($1){
if(!$1){
$1=window.event;var $2=$1.srcElement
}else{
var $2=$1.srcElement.parentNode
};if($2.owner instanceof LzTextSprite){
if(!$2.owner.selectable){
return false
}}else{
return false
}}};var LzXMLParser={parseXML:function($1,$2,$3){
var $4=new DOMParser();var $5=$4.parseFromString($1,"text/xml");var $6=this.getParserError($5);if($6){
throw new Error($6)
}else{
return $5.firstChild
}},getParserError:function($1){
var $2=lz.embed.browser;if($2.isIE){
return this.__checkIE($1)
}else{
if($2.isFirefox||$2.isOpera){
return this.__checkFirefox($1)
}else{
if($2.isSafari){
return this.__checkSafari($1)
}}}},__checkIE:function($1){
var $2=$1.parseError;if($2.errorCode!=0){
return $2.reason
}},__checkFirefox:function($1){
var $2=$1.documentElement;if($2&&$2.nodeName=="parsererror"){
var $3=$2.firstChild.nodeValue;return $3.match(".*")[0]
}},__checkSafari:function($1){
var $2=$1.documentElement;if($2 instanceof HTMLElement){
($2=$2.firstChild)&&($2=$2.firstChild)
}else{
$2=$2.firstChild
};if($2&&$2.nodeName=="parsererror"){
var $3=$2.childNodes[1].textContent;return $3.match("[^:]*: (.*)")[1]
}}};if(typeof DOMParser=="undefined"){
var DOMParser=function(){

};DOMParser.prototype.parseFromString=function($1,$2){
if(typeof window.ActiveXObject!="undefined"){
var $3=["Msxml2.DOMDocument.6.0","Msxml2.DOMDocument.3.0","MSXML.DomDocument"];var $4=null;for(var $5=0;$5<$3.length;$5++){
try{
$4=new ActiveXObject($3[$5]);break
}
catch(ex){

}};$4.loadXML($1);return $4
}else{
if(typeof XMLHttpRequest!="undefined"){
$2=$2||"application/xml";var $6=new XMLHttpRequest();$6.open("GET","data:"+$2+";charset=utf-8,"+encodeURIComponent($1),false);if($6.overrideMimeType){
$6.overrideMimeType($2)
};$6.send(null);return $6.responseXML
}}}};var LzXMLTranslator={whitespacePat:new RegExp("^\\s*$"),stringTrimPat:new RegExp("^\\s+|\\s+$","g"),copyXML:function($1,$2,$3){
var $4=this.copyBrowserXML($1,true,$2,$3);if($4 instanceof LzDataElement){
return $4
}else{
return null
}},copyBrowserNode:function($1,$2,$3,$4){
var $5=$1.nodeType;if($5==3||$5==4){
var $6=$1.nodeValue;if(!($2&&this.whitespacePat.test($6))){
if($3){
$6=$6.replace(this.stringTrimPat,"")
};return new LzDataText($6)
}}else{
if($5==1||$5==9){
var $7=!$4&&($1.localName||$1.baseName)||$1.nodeName;var $8={};var $9=$1.attributes;if($9){
for(var $10=0,$11=$9.length;$10<$11;$10++){
var $12=$9[$10];if($12){
var $13=!$4&&($12.localName||$12.baseName)||$12.name;$8[$13]=$12.value
}}};var $14=new LzDataElement($7);$14.attributes=$8;return $14
}else{

}}},copyBrowserXML:function($1,$2,$3,$4){
var $5=new LzDataElement(null);if(!$1.firstChild){
return $5.appendChild(this.copyBrowserNode($1,$2,$3,$4))
};var $6=this.whitespacePat;var $7=this.stringTrimPat;var $8=$5;var $9,$10=$1;for(;;){
var $11=$10.nodeType;if($11==3||$11==4){
var $12=$10.nodeValue;if(!($2&&$6.test($12))){
if($3){
$12=$12.replace($7,"")
};var $13=$8.childNodes;var $14=$13[$13.length-1];if($14 instanceof LzDataText){
$14.data+=$12
}else{
var $15=new LzDataText($12);$15.parentNode=$8;$15.ownerDocument=$5;$15.__LZo=$13.push($15)-1
}}}else{
if($11==1||$11==9){
var $16=!$4&&($10.localName||$10.baseName)||$10.nodeName;var $17={};var $18=$10.attributes;if($18){
for(var $19=0,$20=$18.length;$19<$20;$19++){
var $21=$18[$19];if($21){
var $22=!$4&&($21.localName||$21.baseName)||$21.name;$17[$22]=$21.value
}}};var $15=new LzDataElement($16);$15.attributes=$17;$15.parentNode=$8;$15.ownerDocument=$5;$15.__LZo=$8.childNodes.push($15)-1;if($9=$10.firstChild){
$8=$15;$10=$9;continue
}}else{

}};while(!($9=$10.nextSibling)){
$10=$10.parentNode;$8=$8.parentNode;if($10===$1){
return $5.childNodes[0]
}};$10=$9
}}};var LzHTTPLoader=function($1,$2){
this.owner=$1;this.options={parsexml:true,serverproxyargs:null};this.requestheaders={};this.requestmethod=LzHTTPLoader.GET_METHOD;this.__loaderid=LzHTTPLoader.loaderIDCounter++
};LzHTTPLoader.GET_METHOD="GET";LzHTTPLoader.POST_METHOD="POST";LzHTTPLoader.PUT_METHOD="PUT";LzHTTPLoader.DELETE_METHOD="DELETE";LzHTTPLoader.activeRequests={};LzHTTPLoader.loaderIDCounter=0;LzHTTPLoader.prototype.loadSuccess=function($1,$2){

};LzHTTPLoader.prototype.loadError=function($1,$2){

};LzHTTPLoader.prototype.loadTimeout=function($1,$2){

};LzHTTPLoader.prototype.loadContent=function($1,$2){
if(this.options["parsexml"]){
this.translateXML()
}else{
this.loadSuccess(this,$2)
}};LzHTTPLoader.prototype.translateXML=function(){
var $1=this.responseXML;if($1==null||$1.childNodes.length==0||lz.embed.browser.isFirefox&&LzXMLParser.getParserError($1)!=null){
this.loadError(this,null)
}else{
var $2;var $3=$1.childNodes;for(var $4=0;$4<$3.length;$4++){
var $5=$3.item($4);if($5.nodeType==1){
$2=$5;break
}};if($2!=null){
var $6=LzXMLTranslator.copyXML($2,this.options.trimwhitespace,this.options.nsprefix);this.loadSuccess(this,$6)
}else{
this.loadError(this,null)
}}};LzHTTPLoader.prototype.getResponse=function(){
return this.responseText
};LzHTTPLoader.prototype.getResponseStatus=function(){
return this.responseStatus
};LzHTTPLoader.prototype.getResponseHeaders=function(){
return this.responseHeaders
};LzHTTPLoader.prototype.getResponseHeader=function($1){
return this.responseHeaders[$1]
};LzHTTPLoader.prototype.setRequestHeaders=function($1){
this.requestheaders=$1
};LzHTTPLoader.prototype.setRequestHeader=function($1,$2){
this.requestheaders[$1]=$2
};LzHTTPLoader.prototype.setOption=function($1,$2){
this.options[$1]=$2
};LzHTTPLoader.prototype.getOption=function($1){
return this.options[$1]
};LzHTTPLoader.prototype.setProxied=function($1){
this.setOption("proxied",$1)
};LzHTTPLoader.prototype.setQueryParams=function($1){
this.queryparams=$1
};LzHTTPLoader.prototype.setQueryString=function($1){
this.querystring=$1
};LzHTTPLoader.prototype.setQueueing=function($1){
this.setOption("queuing",$1)
};LzHTTPLoader.prototype.abort=function(){
if(this.req){
this.__abort=true;this.req.abort();this.req=null;this.removeTimeout(this)
}};LzHTTPLoader.prototype.open=function($1,$2,$3,$4){
if(this.req){
this.abort()
};this.req=window.XMLHttpRequest?new XMLHttpRequest():new ActiveXObject("Microsoft.XMLHTTP");this.responseStatus=0;this.responseHeaders=null;this.responseText=null;this.responseXML=null;this.__abort=false;this.__timeout=false;this.requesturl=$2;this.requestmethod=$1
};LzHTTPLoader.prototype.send=function($1){
this.loadXMLDoc(this.requestmethod,this.requesturl,this.requestheaders,$1,true)
};LzHTTPLoader.prototype.makeProxiedURL=function($1,$2,$3,$4,$5,$6){
var $7={serverproxyargs:this.options.serverproxyargs,sendheaders:this.options.sendheaders,trimwhitespace:this.options.trimwhitespace,nsprefix:this.options.nsprefix,timeout:this.timeout,cache:this.options.cacheable,ccache:this.options.ccache,proxyurl:$1,url:$2,secure:this.secure,postbody:$6,headers:$5,httpmethod:$3,service:$4};return lz.Browser.makeProxiedURL($7)
};LzHTTPLoader.prototype.timeout=Infinity;LzHTTPLoader.prototype.setTimeout=function($1){
this.timeout=$1
};LzHTTPLoader.prototype.setupTimeout=function($1,$2){
var $3=new Date().getTime()+$2;var $4=$1.__loaderid;LzHTTPLoader.activeRequests[$4]=[$1,$3];var $5=setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts("+$4+")",$2);LzHTTPLoader.activeRequests[$4][2]=$5
};LzHTTPLoader.prototype.removeTimeout=function($1){
var $2=$1.__loaderid;if($2!=null){
var $3=LzHTTPLoader.activeRequests[$2];if($3&&$3[0]===$1){
clearTimeout($3[2]);delete LzHTTPLoader.activeRequests[$2]
}}};LzHTTPLoader.__LZcheckXMLHTTPTimeouts=function($1){
var $2=LzHTTPLoader.activeRequests[$1];if($2){
var $3=new Date().getTime();var $4=$2[0];var $5=$2[1];if($3>=$5){
delete LzHTTPLoader.activeRequests[$1];$4.__timeout=true;if($4.req){
$4.req.abort()
};$4.req=null;$4.loadTimeout($4,null)
}else{
var $6=setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts("+$1+")",$3-$5);$2[2]=$6
}}};LzHTTPLoader.prototype.getElapsedTime=function(){
return new Date().getTime()-this.gstart
};LzHTTPLoader.prototype.__setRequestHeaders=function($1,$2){
if($2!=null){
for(var $3 in $2){
var $4=$2[$3];$1.setRequestHeader($3,$4)
}}};LzHTTPLoader.prototype.__getAllResponseHeaders=function($1){
var $2=new RegExp("^([-\\w]+):\\s*(\\S(?:.*\\S)?)\\s*$","mg");var $3=$1.getAllResponseHeaders();var $4={};var $5;while(($5=$2.exec($3))!=null){
$4[$5[1]]=$5[2]
};return $4
};LzHTTPLoader.prototype.loadXMLDoc=function($1,$2,$3,$4,$5){
if(this.req){
var self=this;this.req.onreadystatechange=function(){
var $1=self.req;if($1==null){
return
};if($1.readyState==4){
if(self.__timeout){

}else{
if(self.__abort){

}else{
self.removeTimeout(self);self.req=null;var $2=-1;try{
$2=$1.status
}
catch(e){

};self.responseStatus=$2;if($2==200||$2==304){
self.responseXML=$1.responseXML;self.responseText=$1.responseText;self.responseHeaders=self.__getAllResponseHeaders($1);self.loadContent(self,self.responseText)
}else{
self.loadError(self,null)
}}}}};try{
this.req.open($1,$2,true)
}
catch(e){
this.req=null;this.loadError(this,null);return
};if($1=="POST"&&$3["content-type"]==null){
$3["content-type"]="application/x-www-form-urlencoded"
};this.__setRequestHeaders(this.req,$3);this.gstart=new Date().getTime();try{
this.req.send($4)
}
catch(e){
this.req=null;this.loadError(this,null);return
};if(isFinite(this.timeout)){
this.setupTimeout(this,this.timeout)
}}};var LzScreenKernel={width:null,height:null,__resizeEvent:function(){
var $1=LzSprite.__rootSpriteContainer;LzScreenKernel.width=$1.offsetWidth;LzScreenKernel.height=$1.offsetHeight;if(LzScreenKernel.__callback){
LzScreenKernel.__scope[LzScreenKernel.__callback]({width:LzScreenKernel.width,height:LzScreenKernel.height})
}},__init:function(){
lz.embed.attachEventHandler(window.top,"resize",LzScreenKernel,"__resizeEvent")
},__callback:null,__scope:null,setCallback:function($1,$2){
this.__scope=$1;this.__callback=$2;this.__init();this.__resizeEvent()
}};Class.make("LzContextMenuKernel",null,["$lzsc$initialize",function($1){
this.owner=$1
},"owner",null,"showbuiltins",false,"_delegate",null,"setDelegate",function($1){
this._delegate=$1
},"addItem",function($1){

},"hideBuiltInItems",function(){
this.showbuiltins=false
},"showBuiltInItems",function(){
this.showbuiltins=true
},"clearItems",function(){

},"__show",function(){
var $1=this.owner;var $2=this._delegate;if($2!=null){
$2.execute($1)
};if($1.onmenuopen.ready){
$1.onmenuopen.sendEvent($1)
};var $3="";var $4=0;var $5=$1.getItems();var $6={};for(var $7=0;$7<$5.length;$7++){
var $8=$5[$7].kernel.cmenuitem;var $9=$8.caption;if($8.visible!=true||($9 in $6)){
continue
};$6[$9]=true;$4+=1;if($8.separatorBefore){
$3+='<div class="separator"></div>'
};if($8.enabled){
$3+='<a href="" onmouseup="LzMouseKernel.__showncontextmenu.__select(arguments[0],'+$7+');">'
}else{
$3+='<a href="" class="disabled">'
};$3+=$9+"</a>"
};LzMouseKernel.__showncontextmenu=this;var $10=LzContextMenuKernel.lzcontextmenu||LzContextMenuKernel.__create();$10.innerHTML=$3;var $11=$10.offsetWidth;var $12=LzMouseKernel.__x;if($12+$11>LzScreenKernel.width){
$12+=LzScreenKernel.width-($12+$11)
};var $13=$10.offsetHeight;var $14=LzMouseKernel.__y;if($14+$13>LzScreenKernel.height){
$14+=LzScreenKernel.height-($14+$13)
};$10.style.left=$12+"px";$10.style.top=$14+"px";if($4&&!this.showbuiltins){
$10.style.display="block"
}},"__hide",function(){
var $1=LzContextMenuKernel.lzcontextmenu;if($1){
$1.style.display="none"
};LzMouseKernel.__showncontextmenu=null
},"__select",function($1,$2){
$1=$1||window.event;var $3=LzSprite.quirks.ie_mouse_events?1:0;if($1.button==$3){
this.__hide();var $4=this.owner.getItems();if($4[$2]){
$4[$2].kernel.__select()
}}}],["lzcontextmenu",null,"__create",function(){
var $1=document.getElementById("lzcontextmenu");if(!$1){
$1=document.createElement("div");lz.embed.__setAttr($1,"id","lzcontextmenu");lz.embed.__setAttr($1,"style","display: none");var $2=function($1){
if($1){
$1.preventDefault();$1.stopPropagation()
}else{
$1=window.event;$1.returnValue=false;$1.cancelBubble=true
};return false
};$1.onmousedown=$2;$1.onmouseup=$2;$1.onclick=$2;document.body.appendChild($1);LzContextMenuKernel.lzcontextmenu=$1
};return $1
}]);Class.make("LzContextMenuItemKernel",null,["$lzsc$initialize",function($1,$2,$3){
this.owner=$1;this.cmenuitem={visible:true,enabled:true,separatorBefore:false,caption:$2};this.setDelegate($3)
},"owner",null,"cmenuitem",null,"_delegate",null,"setDelegate",function($1){
this._delegate=$1
},"setCaption",function($1){
this.cmenuitem.caption=$1
},"getCaption",function(){
return this.cmenuitem.caption
},"setEnabled",function($1){
this.cmenuitem.enabled=$1
},"setSeparatorBefore",function($1){
this.cmenuitem.separatorBefore=$1
},"setVisible",function($1){
this.cmenuitem.visible=$1
},"__select",function(){
var $1=this.owner;var $2=this._delegate;if($2!=null){
if($2 instanceof LzDelegate){
$2.execute($1)
}else{
if(typeof $2=="function"){
$2()
}else{

}}};if($1.onselect.ready){
$1.onselect.sendEvent($1)
}}],null);if(LzSprite.quirks.ie_timer_closure){
(function($1){
window.setTimeout=$1(window.setTimeout);window.setInterval=$1(window.setInterval)
})(function(f){
return( function(c,$1){
var a=Array.prototype.slice.call(arguments,2);if(typeof c!="function"){
c=new Function(c)
};return( f(function(){
c.apply(this,a)
},$1))
})
})
};var LzTimeKernel={setTimeout:function(){
return window.setTimeout.apply(window,arguments)
},setInterval:function(){
return window.setInterval.apply(window,arguments)
},clearTimeout:function($1){
return window.clearTimeout($1)
},clearInterval:function($1){
return window.clearInterval($1)
},startTime:new Date().valueOf(),getTimer:function(){
return new Date().valueOf()-LzTimeKernel.startTime
}};Class.make("LzView",LzNode,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"__LZlayout",void 0,"__LZstoredbounds",void 0,"__movecounter",0,"__mousecache",null,"playing",false,"_visible",void 0,"$lzc$set_visible",function($1){
if(this._visible==$1){
return
};this._visible=$1;if($1){
var $2="visible"
}else{
if($1==null){
var $2="collapse"
}else{
var $2="hidden"
}};this.visibility=$2;if(this.onvisibility.ready){
this.onvisibility.sendEvent(this.visibility)
};this.__LZupdateShown()
},"onaddsubview",LzDeclaredEvent,"onblur",LzDeclaredEvent,"onclick",LzDeclaredEvent,"onclickable",LzDeclaredEvent,"onfocus",LzDeclaredEvent,"onframe",LzDeclaredEvent,"onheight",LzDeclaredEvent,"onkeyup",LzDeclaredEvent,"onkeydown",LzDeclaredEvent,"onlastframe",LzDeclaredEvent,"onload",LzDeclaredEvent,"onframesloadratio",LzDeclaredEvent,"onloadratio",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"onmousedown",LzDeclaredEvent,"onmouseout",LzDeclaredEvent,"onmouseover",LzDeclaredEvent,"onmousetrackover",LzDeclaredEvent,"onmousetrackup",LzDeclaredEvent,"onmousetrackout",LzDeclaredEvent,"onmouseup",LzDeclaredEvent,"onmousedragin",LzDeclaredEvent,"onmousedragout",LzDeclaredEvent,"onmouseupoutside",LzDeclaredEvent,"onopacity",LzDeclaredEvent,"onplay",LzDeclaredEvent,"onremovesubview",LzDeclaredEvent,"onresource",LzDeclaredEvent,"onresourceheight",LzDeclaredEvent,"onresourcewidth",LzDeclaredEvent,"onrotation",LzDeclaredEvent,"onstop",LzDeclaredEvent,"ontotalframes",LzDeclaredEvent,"onunstretchedheight",LzDeclaredEvent,"onunstretchedwidth",LzDeclaredEvent,"onvisible",LzDeclaredEvent,"onvisibility",LzDeclaredEvent,"onwidth",LzDeclaredEvent,"onx",LzDeclaredEvent,"onxoffset",LzDeclaredEvent,"ony",LzDeclaredEvent,"onyoffset",LzDeclaredEvent,"onfont",LzDeclaredEvent,"onfontsize",LzDeclaredEvent,"onfontstyle",LzDeclaredEvent,"ondblclick",LzDeclaredEvent,"DOUBLE_CLICK_TIME",500,"capabilities",void 0,"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1?$1:canvas,$2);this.__LZdelayedSetters=LzView.__LZdelayedSetters;this.earlySetters=LzView.earlySetters;this.mask=this.immediateparent.mask;this.__makeSprite($2);this.capabilities=this.sprite.capabilities;if($2["width"]!=null||this.__LZhasConstraint("width")){
this.hassetwidth=true;this.__LZcheckwidth=false
};if($2["height"]!=null||this.__LZhasConstraint("height")){
this.hassetheight=true;this.__LZcheckheight=false
};if($2["clip"]){
this.clip=$2.clip;this.makeMasked()
};if($2["stretches"]!=null){
this.$lzc$set_stretches($2.stretches);$2.stretches=LzNode._ignoreAttribute
};if($2["resource"]!=null){
this.$lzc$set_resource($2.resource);$2.resource=LzNode._ignoreAttribute
};if($2["fgcolor"]!=null){
this.hasfgcolor=true
}},"__spriteAttribute",function($1,$2){
if(this[$1]){
if(!this.__LZdeleted){
var $lzsc$494894518="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$494894518]):this[$lzsc$494894518] instanceof Function){
this[$lzsc$494894518]($2)
}else{
this[$1]=$2;var $lzsc$857362692=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$857362692):$lzsc$857362692 instanceof LzEvent){
if($lzsc$857362692.ready){
$lzsc$857362692.sendEvent($2)
}}}}}},"__makeSprite",function($1){
this.sprite=new LzSprite(this,false)
},"init",function(){
if(this.sprite){
this.sprite.init(this.visible)
}},"addSubview",function($1){
if($1.addedToParent){
return
};if(this.sprite){
this.sprite.addChildSprite($1.sprite)
};if(this.subviews.length==0){
this.subviews=[]
};this.subviews.push($1);$1.addedToParent=true;if(this.__LZcheckwidth){
this.__LZcheckwidthFunction($1)
};if(this.__LZcheckheight){
this.__LZcheckheightFunction($1)
};if(this.onaddsubview.ready){
this.onaddsubview.sendEvent($1)
}},"__LZinstantiationDone",function(){
var $1=this.immediateparent;if($1){
$1.addSubview(this)
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZinstantiationDone"]||this.nextMethod(arguments.callee,"__LZinstantiationDone")).call(this)
},"mask",void 0,"focusable",false,"focustrap",void 0,"clip",false,"$lzc$set_clip",-1,"align","left","$lzc$set_align",function($1){
var $2;$2=function($1){
switch($1){
case "center":
return "__LZalignCenter";
case "right":
return "__LZalignRight";
case "left":
return null;

}};if(this.align==$1){
return
};var $3=$2(this.align);var $4=$2($1);if($3!=null){
this.releaseConstraintMethod($3)
};if($4!=null){
this.applyConstraintMethod($4,[this.immediateparent,"width",this,"width"])
}else{
this.$lzc$set_x(0)
};this.align=$1
},"valign","top","$lzc$set_valign",function(valign){
var $1;$1=function($1){
switch($1){
case "middle":
return "__LZvalignMiddle";
case "bottom":
return "__LZvalignBottom";
case "top":
return null;

}};if(this.valign==valign){
return
};var $2=$1(this.valign);var $3=$1(valign);if($2!=null){
this.releaseConstraintMethod($2)
};if($3!=null){
this.applyConstraintMethod($3,[this.immediateparent,"height",this,"height"])
}else{
this.$lzc$set_y(0)
};this.valign=valign
},"source",void 0,"$lzc$set_source",function($1){
this.setSource($1)
},"clickregion",void 0,"$lzc$set_clickregion",function($1){
if(this.capabilities.clickregion){
this.sprite.setClickRegion($1)
}else{

};this.clickregion=$1
},"cursor",void 0,"fgcolor",0,"hasfgcolor",false,"onfgcolor",LzDeclaredEvent,"$lzc$set_fgcolor",function($1){
if($1!=null&&isNaN($1)){
$1=this.acceptTypeValue("color",$1)
};this.sprite.setColor($1);this.fgcolor=$1;if(this.onfgcolor.ready){
this.onfgcolor.sendEvent($1)
}},"font",void 0,"fontname",void 0,"$lzc$set_font",function($1){
this.font=$1;this.fontname=$1;if(this.onfont.ready){
this.onfont.sendEvent(this.font)
}},"fontstyle",void 0,"$lzc$set_fontstyle",function($1){
if($1=="plain"||$1=="bold"||$1=="italic"||$1=="bolditalic"||$1=="bold italic"){
this.fontstyle=$1;if(this.onfontstyle.ready){
this.onfontstyle.sendEvent(this.fontstyle)
}}else{

}},"fontsize",void 0,"$lzc$set_fontsize",function($1){
if(!($1<=0||isNaN($1))){
this.fontsize=$1;if(this.onfontsize.ready){
this.onfontsize.sendEvent(this.fontsize)
}}else{

}},"stretches","none","$lzc$set_stretches",function($1){
if(!($1=="none"||$1=="both"||$1=="width"||$1=="height")){
var $2=$1==null?"both":($1=="x"?"width":($1=="y"?"height":"none"));$1=$2
}else{
if(this.stretches==$1){
return
}};this.stretches=$1;this.sprite.stretchResource($1);if($1=="width"||$1=="both"){
this._setrescwidth=true;this.__LZcheckwidth=true;this.reevaluateSize("width")
};if($1=="height"||$1=="both"){
this._setrescheight=true;this.__LZcheckheight=true;this.reevaluateSize("height")
}},"layout",void 0,"$lzc$set_layout",function($1){
this.layout=$1;if(!this.isinited){
this.__LZstoreAttr($1,"layout");return
};var $2=$1["class"];if($2==null){
$2="simplelayout"
};if(this.__LZlayout){
this.__LZlayout.destroy()
};if($2!="none"){
var $3={};for(var $4 in $1){
if($4!="class"){
$3[$4]=$1[$4]
}};if($2=="null"){
this.__LZlayout=null;return
};this.__LZlayout=new (lz[$2])(this,$3)
}},"aaactive",void 0,"$lzc$set_aaactive",function($1){
if(this.capabilities.accessibility){
this.aaactive=$1;this.sprite.setAAActive($1)
}else{

}},"aaname",void 0,"$lzc$set_aaname",function($1){
if(this.capabilities.accessibility){
this.aaname=$1;this.sprite.setAAName($1)
}else{

}},"aadescription",void 0,"$lzc$set_aadescription",function($1){
if(this.capabilities.accessibility){
this.aadescription=$1;this.sprite.setAADescription($1)
}else{

}},"aatabindex",void 0,"$lzc$set_aatabindex",function($1){
if(this.capabilities.accessibility){
this.aatabindex=$1;this.sprite.setAATabIndex($1)
}else{

}},"aasilent",void 0,"$lzc$set_aasilent",function($1){
if(this.capabilities.accessibility){
this.aasilent=$1;this.sprite.setAASilent($1)
}else{

}},"sendAAEvent",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=false;

};if(this.capabilities.accessibility){
this.sprite.sendAAEvent($1,$2,$3)
}else{

}},"sprite",null,"visible",true,"visibility","collapse","$lzc$set_visibility",function($1){
if(this.visibility==$1){
return
};this.visibility=$1;if(this.onvisibility.ready){
this.onvisibility.sendEvent($1)
};this.__LZupdateShown()
},"__LZvizO",true,"__LZvizLoad",true,"__LZvizDat",true,"opacity",1,"$lzc$set_opacity",function($1){
if(this.capabilities.opacity){
this.sprite.setOpacity($1)
}else{

};this.opacity=$1;if(this.onopacity.ready){
this.onopacity.sendEvent($1)
};var $2=this.__LZvizO;var $3=$1!=0;if($2!=$3){
this.__LZvizO=$3;this.__LZupdateShown()
}},"$lzc$set_alpha",function($1){
this.$lzc$set_opacity($1)
},"bgcolor",null,"onbgcolor",LzDeclaredEvent,"$lzc$set_bgcolor",function($1){
if($1!=null&&isNaN($1)){
$1=this.acceptTypeValue("color",$1)
};this.sprite.setBGColor($1);this.bgcolor=$1;if(this.onbgcolor.ready){
this.onbgcolor.sendEvent($1)
}},"x",0,"__set_x_memo",void 0,"$lzc$set_x",function($1){
this.x=$1;if(this.__set_x_memo===$1){
if(this.onx.ready){
this.onx.sendEvent(this.x)
};return
};this.__set_x_memo=$1;if(this.__LZhasoffset){
if(this.capabilities.rotation){
$1-=this.xoffset*this.__LZrcos-this.yoffset*this.__LZrsin
}else{
$1-=this.xoffset
}};if(this.pixellock){
$1=Math.floor($1)
};this.sprite.setX($1);var $2=this.immediateparent;if($2.__LZcheckwidth){
$2.__LZcheckwidthFunction(this)
};if(this.onx.ready){
this.onx.sendEvent(this.x)
}},"y",0,"__set_y_memo",void 0,"$lzc$set_y",function($1){
this.y=$1;if(this.__set_y_memo===$1){
if(this.ony.ready){
this.ony.sendEvent(this.y)
};return
};this.__set_y_memo=$1;if(this.__LZhasoffset){
if(this.capabilities.rotation){
$1-=this.xoffset*this.__LZrsin+this.yoffset*this.__LZrcos
}else{
$1-=this.yoffset
}};if(this.pixellock){
$1=Math.floor($1)
};this.sprite.setY($1);var $2=this.immediateparent;if($2.__LZcheckheight){
$2.__LZcheckheightFunction(this)
};if(this.ony.ready){
this.ony.sendEvent(this.y)
}},"rotation",0,"$lzc$set_rotation",function($1){
if(this.capabilities.rotation){
this.sprite.setRotation($1)
}else{

};this.rotation=$1;var $2=Math.PI/180*this.rotation;this.__LZrsin=Math.sin($2);this.__LZrcos=Math.cos($2);if(this.onrotation.ready){
this.onrotation.sendEvent($1)
};if(this.__LZhasoffset){
this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y)
};var $3=this.immediateparent;if($3.__LZcheckwidth){
$3.__LZcheckwidthFunction(this)
};if($3.__LZcheckheight){
$3.__LZcheckheightFunction(this)
}},"width",0,"__set_width_memo",void 0,"$lzc$set_width",function($1){
if($1!=null){
this.hassetwidth=true;this.width=$1
}else{
this.hassetwidth=false
};if(this.__set_width_memo===$1){
if(this.onwidth.ready){
this.onwidth.sendEvent(this.width)
};return
};this.__set_width_memo=$1;if($1==null){
this.__LZcheckwidth=true;if(this._setrescwidth){
this.unstretchedwidth=null;this._xscale=1
};this.reevaluateSize("width");return
};if(this.pixellock){
$1=Math.floor($1)
};if(this._setrescwidth){
var $2=this.unstretchedwidth==0?100:$1/this.unstretchedwidth;this._xscale=$2
}else{
this.__LZcheckwidth=false
};this.sprite.setWidth($1);var $3=this.immediateparent;if($3&&$3.__LZcheckwidth){
$3.__LZcheckwidthFunction(this)
};if(this.onwidth.ready){
this.onwidth.sendEvent(this.width)
}},"height",0,"__set_height_memo",void 0,"$lzc$set_height",function($1){
if($1!=null){
this.hassetheight=true;this.height=$1
}else{
this.hassetheight=false
};if(this.__set_height_memo===$1){
if(this.onheight.ready){
this.onheight.sendEvent(this.height)
};return
};this.__set_height_memo=$1;if($1==null){
this.__LZcheckheight=true;if(this._setrescheight){
this.unstretchedheight=null;this._yscale=1
};this.reevaluateSize("height");return
};if(this.pixellock){
$1=Math.floor($1)
};if(this._setrescheight){
this._yscale=this.unstretchedheight==0?100:$1/this.unstretchedheight
}else{
this.__LZcheckheight=false
};this.sprite.setHeight($1);var $2=this.immediateparent;if($2&&$2.__LZcheckheight){
$2.__LZcheckheightFunction(this)
};if(this.onheight.ready){
this.onheight.sendEvent(this.height)
}},"unstretchedwidth",0,"unstretchedheight",0,"subviews",[],"xoffset",0,"$lzc$set_xoffset",function($1){
this.__LZhasoffset=$1!=0;this.xoffset=$1;this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y);if(this.onxoffset.ready){
this.onxoffset.sendEvent($1)
}},"yoffset",0,"$lzc$set_yoffset",function($1){
this.__LZhasoffset=$1!=0;this.yoffset=$1;this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y);if(this.onyoffset.ready){
this.onyoffset.sendEvent($1)
}},"__LZrsin",0,"__LZrcos",1,"_xscale",1,"_yscale",1,"totalframes",1,"frame",1,"$lzc$set_frame",function($1){
this.frame=$1;this.stop($1);if(this.onframe.ready){
this.onframe.sendEvent($1)
}},"framesloadratio",0,"loadratio",0,"hassetheight",false,"hassetwidth",false,"addedToParent",null,"masked",false,"pixellock",null,"clickable",false,"$lzc$set_clickable",function($1){
this.sprite.setClickable($1);this.clickable=$1;if(this.onclickable.ready){
this.onclickable.sendEvent($1)
}},"showhandcursor",null,"$lzc$set_showhandcursor",function($1){
this.showhandcursor=$1;this.sprite.setShowHandCursor($1)
},"resource",null,"$lzc$set_resource",function($1){
if($1==null||$1==this._resource){
return
};this.resource=this._resource=$1;this.sprite.setResource($1)
},"resourcewidth",0,"resourceheight",0,"__LZcheckwidth",true,"__LZcheckheight",true,"__LZhasoffset",null,"__LZoutlieheight",null,"__LZoutliewidth",null,"setLayout",function($1){
this.$lzc$set_layout($1)
},"setFontName",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};this.$lzc$set_font($1)
},"_setrescwidth",false,"_setrescheight",false,"searchSubviews",function($1,$2){
var $3=this.subviews.concat();while($3.length>0){
var $4=$3;$3=new Array();for(var $5=$4.length-1;$5>=0;$5--){
var $6=$4[$5];if($6[$1]==$2){
return $6
};var $7=$6.subviews;for(var $8=$7.length-1;$8>=0;$8--){
$3.push($7[$8])
}}};return null
},"layouts",null,"releaseLayouts",function(){
if(this.layouts){
for(var $1=this.layouts.length-1;$1>=0;$1--){
this.layouts[$1].releaseLayout()
}}},"_resource",null,"setResource",function($1){
this.$lzc$set_resource($1)
},"resourceload",function($1){
if("resource" in $1){
this.resource=$1.resource;if(this.onresource.ready){
this.onresource.sendEvent($1.resource)
}};if(this.resourcewidth!=$1.width){
if("width" in $1){
this.resourcewidth=$1.width;if(this.onresourcewidth.ready){
this.onresourcewidth.sendEvent($1.width)
}};if(!this.hassetwidth&&this.resourcewidth!=this.width||this._setrescwidth&&this.unstretchedwidth!=this.resourcewidth){
this.updateWidth(this.resourcewidth)
}};if(this.resourceheight!=$1.height){
if("height" in $1){
this.resourceheight=$1.height;if(this.onresourceheight.ready){
this.onresourceheight.sendEvent($1.height)
}};if(!this.hassetheight&&this.resourceheight!=this.height||this._setrescheight&&this.unstretchedheight!=this.resourceheight){
this.updateHeight(this.resourceheight)
}};if($1.skiponload!=true){
if(this.onload.ready){
this.onload.sendEvent(this)
}}},"resourceloaderror",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.resourcewidth=0;this.resourceheight=0;if(this.onresourcewidth.ready){
this.onresourcewidth.sendEvent(0)
};if(this.onresourceheight.ready){
this.onresourceheight.sendEvent(0)
};this.reevaluateSize();if(this.onerror.ready){
this.onerror.sendEvent($1)
}},"resourceloadtimeout",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.resourcewidth=0;this.resourceheight=0;if(this.onresourcewidth.ready){
this.onresourcewidth.sendEvent(0)
};if(this.onresourceheight.ready){
this.onresourceheight.sendEvent(0)
};this.reevaluateSize();if(this.ontimeout.ready){
this.ontimeout.sendEvent($1)
}},"resourceevent",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=false;
case 3:
$4=false;

};var $5=$4==true||$3==true||this[$1]!=$2;if($3!=true){
this[$1]=$2
};if($5){
var $6=this["on"+$1];if($6.ready){
$6.sendEvent($2)
}}},"destroy",function(){
if(this.__LZdeleted){
return
};if(this.sprite){
this.sprite.predestroy()
};var $1=this.immediateparent;if(this.addedToParent){
var $2=$1.subviews;if($2!=null){
for(var $3=$2.length-1;$3>=0;$3--){
if($2[$3]==this){
$2.splice($3,1);break
}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this);if(this.sprite){
this.sprite.destroy()
};this.$lzc$set_visible(false);var $1=this.immediateparent;if(this.addedToParent){
if($1["__LZoutliewidth"]==this){
$1.__LZoutliewidth=null
};if($1["__LZoutlieheight"]==this){
$1.__LZoutlieheight=null
};if($1.onremovesubview.ready){
$1.onremovesubview.sendEvent(this)
}}},"setVisible",function($1){
this.$lzc$set_visible($1)
},"setVisibility",function($1){
this.$lzc$set_visibility($1)
},"__LZupdateShown",function(){
if(this.visibility=="collapse"){
var $1=this.__LZvizO&&this.__LZvizDat&&this.__LZvizLoad
}else{
var $1=this.visibility=="visible"
};if($1!=this.visible){
this.visible=$1;if(this.sprite){
this.sprite.setVisible($1)
};var $2=this.immediateparent;if($2&&$2.__LZcheckwidth){
$2.__LZcheckwidthFunction(this)
};if($2&&$2.__LZcheckheight){
$2.__LZcheckheightFunction(this)
};if(this.onvisible.ready){
this.onvisible.sendEvent($1)
}}},"setWidth",function($1){
this.$lzc$set_width($1)
},"setHeight",function($1){
this.$lzc$set_height($1)
},"setOpacity",function($1){
this.$lzc$set_opacity($1)
},"setX",function($1){
this.$lzc$set_x($1)
},"setY",function($1){
this.$lzc$set_y($1)
},"setRotation",function($1){
this.$lzc$set_rotation($1)
},"setAlign",function($1){
this.$lzc$set_align($1)
},"__LZalignCenter",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.immediateparent;this.$lzc$set_x($2.width/2-this.width/2)
},"__LZalignRight",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.immediateparent;this.$lzc$set_x($2.width-this.width)
},"setXOffset",function($1){
this.$lzc$set_xoffset($1)
},"setYOffset",function($1){
this.$lzc$set_yoffset($1)
},"getBounds",function(){
var $1=[-this.xoffset,-this.yoffset,this.width-this.xoffset,-this.yoffset,-this.xoffset,this.height-this.yoffset,this.width-this.xoffset,this.height-this.yoffset,this.rotation,this.x,this.y];if(this.__LZstoredbounds){
var $2=$1.length-1;while($1[$2]==LzView.__LZlastmtrix[$2]){
if($2--==0){
return this.__LZstoredbounds
}}};var $3={};for(var $2=0;$2<8;$2+=2){
var $4=$1[$2];var $5=$1[$2+1];var $6=$4*this.__LZrcos-$5*this.__LZrsin;var $7=$4*this.__LZrsin+$5*this.__LZrcos;if($3.xoffset==null||$3.xoffset>$6){
$3.xoffset=$6
};if($3.yoffset==null||$3.yoffset>$7){
$3.yoffset=$7
};if($3.width==null||$3.width<$6){
$3.width=$6
};if($3.height==null||$3.height<$7){
$3.height=$7
}};$3.width-=$3.xoffset;$3.height-=$3.yoffset;$3.x=this.x+$3.xoffset;$3.y=this.y+$3.yoffset;this.__LZstoredbounds=$3;LzView.__LZlastmtrix=$1;return $3
},"$lzc$getBounds_dependencies",function($1,$2){
return [$2,"rotation",$2,"x",$2,"y",$2,"width",$2,"height"]
},"setValign",function($1){
this.$lzc$set_valign($1)
},"__LZvalignMiddle",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.immediateparent;this.$lzc$set_y($2.height/2-this.height/2)
},"__LZvalignBottom",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.immediateparent;this.$lzc$set_y($2.height-this.height)
},"setColor",function($1){
this.$lzc$set_fgcolor($1)
},"getColor",function(){
return this.sprite.getColor()
},"setColorTransform",function($1){
if(this.capabilities.colortransform){
this.sprite.setColorTransform($1)
}else{

}},"getColorTransform",function(){
if(this.capabilities.colortransform){
return this.sprite.getColorTransform()
}else{

}},"__LZcheckSize",function($1,$2,$3){
if($1.addedToParent){
if($1.__LZhasoffset||$1.rotation!=0){
var $4=$1.getBounds()
}else{
var $4=$1
};var $5=$4[$3]+$4[$2];var $6=this["_setresc"+$2]?this["unstretched"+$2]:this[$2];if($5>$6&&$1.visible){
this["__LZoutlie"+$2]=$1;if($2=="width"){
this.updateWidth($5)
}else{
this.updateHeight($5)
}}else{
if(this["__LZoutlie"+$2]==$1&&($5<$6||!$1.visible)){
this.reevaluateSize($2)
}}}},"__LZcheckwidthFunction",function($1){
this.__LZcheckSize($1,"width","x")
},"__LZcheckheightFunction",function($1){
this.__LZcheckSize($1,"height","y")
},"measureSize",function($1){
var $2=this["resource"+$1];for(var $3=this.subviews.length-1;$3>=0;$3--){
var $4=this.subviews[$3];if($4.visible){
if($4.__LZhasoffset||$4.rotation!=0){
var $5=$4.getBounds()
}else{
var $5=$4
};var $6=$5[$1=="width"?"x":"y"]+$5[$1];if($6>$2){
$2=$6
}}};return $2
},"measureWidth",function(){
return this.measureSize("width")
},"measureHeight",function(){
return this.measureSize("height")
},"updateSize",function($1,$2){
if($1=="width"){
this.updateWidth($2)
}else{
this.updateHeight($2)
}},"updateWidth",function($1){
if(this._setrescwidth){
this.unstretchedwidth=$1;if(this.hassetwidth){
var $2=this.width/$1;this._xscale=$2
};if(this.onunstretchedwidth.ready){
this.onunstretchedwidth.sendEvent($1)
}};if(!this.hassetwidth){
this.width=$1;this.sprite.setWidth($1);if(this.onwidth.ready){
this.onwidth.sendEvent($1)
};var $3=this.immediateparent;if($3.__LZcheckwidth){
$3.__LZcheckwidthFunction(this)
}}},"updateHeight",function($1){
if(this._setrescheight){
this.unstretchedheight=$1;if(this.hassetheight){
var $2=this.height/$1;this._yscale=$2
};if(this.onunstretchedheight){
if(this.onunstretchedheight.ready){
this.onunstretchedheight.sendEvent($1)
}}};if(!this.hassetheight){
this.height=$1;this.sprite.setHeight($1);if(this.onheight.ready){
this.onheight.sendEvent($1)
};var $3=this.immediateparent;if($3.__LZcheckheight){
$3.__LZcheckheightFunction(this)
}}},"reevaluateSize",function($1){
switch(arguments.length){
case 0:
$1=null;

};if($1==null){
var $2="height";this.reevaluateSize("width")
}else{
var $2=$1
};if(this["hasset"+$2]&&!this["_setresc"+$2]){
return
};var $3=this["_setresc"+$2]?this["unstretched"+$2]:this[$2];var $4=this["resource"+$2]||0;this["__LZoutlie"+$2]=this;for(var $5=this.subviews.length-1;$5>=0;$5--){
var $6=this.subviews[$5];if($6.__LZhasoffset||$6.rotation!=0){
var $7=$6.getBounds();var $8=$7[$2=="width"?"x":"y"]+$7[$2]
}else{
var $8=$6[$2=="width"?"x":"y"]+$6[$2]
};if($6.visible&&$8>$4){
$4=$8;this["__LZoutlie"+$2]=$6
}};if($3!=$4){
if($2=="width"){
this.updateWidth($4)
}else{
this.updateHeight($4)
}}},"updateResourceSize",function(){
this.sprite.updateResourceSize();this.reevaluateSize()
},"setAttributeRelative",function($1,$2){
var $3=this.getLinkage($2);var $4=$2[$1];if($1=="x"||$1=="y"){
$3.update($1);var $lzsc$1612969044=($4-$3.offset[$1])/$3.scale[$1];if(!this.__LZdeleted){
var $lzsc$1844974558="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1844974558]):this[$lzsc$1844974558] instanceof Function){
this[$lzsc$1844974558]($lzsc$1612969044)
}else{
this[$1]=$lzsc$1612969044;var $lzsc$1121561436=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1121561436):$lzsc$1121561436 instanceof LzEvent){
if($lzsc$1121561436.ready){
$lzsc$1121561436.sendEvent($lzsc$1612969044)
}}}}}else{
if($1=="width"||$1=="height"){
var $5=$1=="width"?"x":"y";$3.update($5);var $lzsc$1809375779=$4/$3.scale[$5];if(!this.__LZdeleted){
var $lzsc$1396429330="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1396429330]):this[$lzsc$1396429330] instanceof Function){
this[$lzsc$1396429330]($lzsc$1809375779)
}else{
this[$1]=$lzsc$1809375779;var $lzsc$1450034296=this["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1450034296):$lzsc$1450034296 instanceof LzEvent){
if($lzsc$1450034296.ready){
$lzsc$1450034296.sendEvent($lzsc$1809375779)
}}}}}else{

}}},"$lzc$setAttributeRelative_dependencies",function($1,$2,$3,$4){
var $5=$1.getLinkage($4);var $6=2;var $7=[];if($3=="width"){
var $8="x"
}else{
if($3=="height"){
var $8="y"
}else{
var $8=$3
}};var $9=$8=="x"?"width":"height";while($6){
if($6==2){
var $10=$5.uplinkArray
}else{
var $10=$5.downlinkArray
};$6--;for(var $11=$10.length-1;$11>=0;$11--){
$7.push($10[$11],$8);if($7["_setresc"+$9]){
$7.push([$10[$11],$9])
}}};return $7
},"getAttributeRelative",function($1,$2){
var $3=this.getLinkage($2);if($1=="x"||$1=="y"){
$3.update($1);return $3.offset[$1]+$3.scale[$1]*this[$1]
}else{
if($1=="width"||$1=="height"){
var $4=$1=="width"?"x":"y";$3.update($4);return $3.scale[$4]*this[$1]
}else{

}}},"$lzc$getAttributeRelative_dependencies",function($1,$2,$3,$4){
var $5=$2.getLinkage($4);var $6=2;var $7=[$2,$3];if($3=="width"){
var $8="x"
}else{
if($3=="height"){
var $8="y"
}else{
var $8=$3
}};var $9=$8=="x"?"width":"height";while($6){
if($6==2){
var $10=$5.uplinkArray
}else{
var $10=$5.downlinkArray
};$6--;for(var $11=$10.length-1;$11>=0;$11--){
var $12=$10[$11];$7.push($12,$8);if($12["_setresc"+$9]){
$7.push($12,$9)
}}};return $7
},"__LZviewLinks",null,"getLinkage",function($1){
if(this.__LZviewLinks==null){
this.__LZviewLinks=new Object()
};var $2=$1.getUID();if(this.__LZviewLinks[$2]==null){
this.__LZviewLinks[$2]=new LzViewLinkage(this,$1)
};return this.__LZviewLinks[$2]
},"mouseevent",function($1){
if(this[$1]&&this[$1].ready){
this[$1].sendEvent(this)
}},"getMouse",function($1){
if(this.__movecounter!=lz.GlobalMouse.__movecounter||this.__mousecache==null){
this.__movecounter=lz.GlobalMouse.__movecounter;this.__mousecache=this.sprite.getMouse($1)
};if($1==null){
return this.__mousecache
};return this.__mousecache[$1]
},"$lzc$getMouse_dependencies",function(){
var $1=Array.prototype.slice.call(arguments,0);return [lz.Idle,"idle"]
},"containsPt",function($1,$2){
var $3=0;var $4=0;var $5=this;do{
if(!$5.visible){
return false
};if($5.masked||$5===this){
var $6=$1-$3;var $7=$2-$4;if($6<0||$6>=$5.width||$7<0||$7>=$5.height){
return false
}};$3-=$5.x;$4-=$5.y;$5=$5.immediateparent
}while($5!==canvas);return true
},"bringToFront",function(){
if(!this.sprite){
return
};this.sprite.bringToFront()
},"getDepthList",function(){
var $1=this.subviews.concat();$1.sort(this.__zCompare);return $1
},"__zCompare",function($1,$2){
var $3=$1.sprite.getZ();var $4=$2.sprite.getZ();if($3<$4){
return -1
};if($3>$4){
return 1
};return 0
},"sendBehind",function($1){
return $1?this.sprite.sendBehind($1.sprite):false
},"sendInFrontOf",function($1){
return $1?this.sprite.sendInFrontOf($1.sprite):false
},"sendToBack",function(){
this.sprite.sendToBack()
},"setResourceNumber",function($1){
this.$lzc$set_frame($1)
},"stretchResource",function($1){
this.$lzc$set_stretches($1)
},"setBGColor",function($1){
this.$lzc$set_bgcolor($1)
},"setSource",function($1,$2,$3,$4){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};this.sprite.setSource($1,$2,$3,$4)
},"unload",function(){
this._resource=null;this.sprite.unload()
},"makeMasked",function(){
this.sprite.setClip(true);this.masked=true;this.mask=this
},"removeMask",function(){
this.sprite.setClip(false);this.masked=false;this.mask=null
},"setClickable",function($1){
this.$lzc$set_clickable($1)
},"$lzc$set_cursor",function($1){
this.sprite.setCursor($1)
},"setCursor",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.$lzc$set_cursor($1)
},"$lzc$set_play",function($1){
if($1){
this.play()
}else{
this.stop()
}},"setPlay",function($1){
this.$lzc$set_play($1)
},"getMCRef",function(){
return this.sprite.getMCRef()
},"play",function($1,$2){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=false;

};this.sprite.play($1,$2)
},"stop",function($1,$2){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=false;

};this.sprite.stop($1,$2)
},"setVolume",function($1){
if(this.capabilities.audio){
this.sprite.setVolume($1)
}else{

}},"getVolume",function(){
if(this.capabilities.audio){
return this.sprite.getVolume()
}else{

};return NaN
},"setPan",function($1){
if(this.capabilities.audio){
this.sprite.setPan($1)
}else{

}},"getPan",function(){
if(this.capabilities.audio){
return this.sprite.getPan()
}else{

};return NaN
},"getZ",function(){
return this.sprite.getZ()
},"seek",function($1){
var $2=this.getMCRef();if($2.isaudio==true){
$2.seek($1,this.playing)
}else{
var $3=$1*canvas.framerate;if(this.playing){
this.play($3,true)
}else{
this.stop($3,true)
}}},"getCurrentTime",function(){
var $1=this.getMCRef();if($1.isaudio==true){
return $1.getCurrentTime()
}else{
return this.frame/canvas.framerate
}},"$lzc$getCurrentTime_dependencies",function($1,$2){
return [$2,"frame"]
},"getTotalTime",function(){
var $1=this.getMCRef();if($1.isaudio==true){
return $1.getTotalTime()
}else{
return this.totalframes/canvas.framerate
}},"$lzc$getTotalTime_dependencies",function($1,$2){
return [$2,"load"]
},"getID3",function(){
var $1=this.getMCRef();if($1.isaudio==true){
return $1.getID3()
};return null
},"setShowHandCursor",function($1){
this.$lzc$set_showhandcursor($1)
},"setAccessible",function($1){
if(this.capabilities.accessibility){
this.sprite.setAccessible($1)
}else{

}},"setAAActive",function($1){
this.$lzc$set_aaactive($1)
},"setAAName",function($1){
this.$lzc$set_aaname($1)
},"setAADescription",function($1){
this.$lzc$set_aadescription($1)
},"setAATabIndex",function($1){
this.$lzc$set_aatabindex($1)
},"setAASilent",function($1){
this.$lzc$set_aasilent($1)
},"shouldYieldFocus",function(){
return true
},"blurring",false,"getProxyURL",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.proxyurl;if($2==null){
return null
}else{
if(typeof $2=="string"){
return $2
}else{
if(typeof $2=="function"){
return $2($1)
}else{

}}}},"__LZcheckProxyPolicy",function($1){
if(this.__proxypolicy!=null){
return this.__proxypolicy($1)
};var $2=LzView.__LZproxypolicies;for(var $3=$2.length-1;$3>=0;$3--){
var $4=$2[$3]($1);if($4!=null){
return $4
}};return canvas.proxied
},"setProxyPolicy",function($1){
this.__proxypolicy=$1
},"__proxypolicy",null,"setProxyURL",function($1){
this.$lzc$set_proxyurl($1)
},"proxyurl",function($1){
return canvas.getProxyURL($1)
},"$lzc$set_proxyurl",function($1){
this.proxyurl=$1
},"contextmenu",null,"$lzc$set_contextmenu",function($1){
this.contextmenu=$1;this.sprite.setContextMenu($1)
},"setContextMenu",function($1){
this.$lzc$set_contextmenu($1)
},"getContextMenu",function(){
return this.contextmenu
},"getNextSelection",function(){

},"getPrevSelection",function(){

},"cachebitmap",false,"$lzc$set_cachebitmap",function($1){
if($1!=this.cachebitmap){
this.cachebitmap=$1;if(this.capabilities.bitmapcaching){
this.sprite.setBitmapCache($1)
}else{

}}}],["tagname","view","attributes",new LzInheritedHash(LzNode.attributes),"__LZdelayedSetters",new LzInheritedHash(LzNode.__LZdelayedSetters),"earlySetters",new LzInheritedHash(LzNode.earlySetters),"__LZlastmtrix",[0,0,0,0,0,0,0,0,0,0,0],"__LZproxypolicies",[],"addProxyPolicy",function($1){
LzView.__LZproxypolicies.push($1)
},"removeProxyPolicy",function($1){
var $2=LzView.__LZproxypolicies;for(var $3=0;$3<$2.length;$3++){
if($2[$3]==$1){
LzView.__LZproxypolicies=$2.splice($3,1);return true
}};return false
},"__warnCapability",function($1,$2){
switch(arguments.length){
case 1:
$2="";

}}]);(function($1){
with($1){
with($1.prototype){
LzView.__LZdelayedSetters.layout="$lzc$set_layout";LzView.earlySetters.clickregion=7;LzView.earlySetters.stretches=8
}}})(LzView);lz[LzView.tagname]=LzView;Class.make("LzText",[LzFormatter,LzView],["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"maxlines",1,"selectable",false,"onselectable",LzDeclaredEvent,"$lzc$set_selectable",function($1){
this.selectable=$1;this.tsprite.setSelectable($1);if(this.onselectable.ready){
this.onselectable.sendEvent($1)
}},"antiAliasType","advanced","$lzc$set_antiAliasType",function($1){
if(this.capabilities.advancedfonts){
if($1=="normal"||$1=="advanced"){
this.antiAliasType=$1;this.tsprite.setAntiAliasType($1)
}else{

}}else{

}},"gridFit","pixel","$lzc$set_gridFit",function($1){
if(this.capabilities.advancedfonts){
if($1=="none"||$1=="pixel"||$1=="subpixel"){
this.gridFit=$1;this.tsprite.setGridFit($1)
}else{

}}else{

}},"sharpness",0,"$lzc$set_sharpness",function($1){
if(this.capabilities.advancedfonts){
if($1>=-400&&$1<=400){
this.sharpness=$1;this.tsprite.setSharpness($1)
}else{

}}else{

}},"thickness",0,"$lzc$set_thickness",function($1){
if(this.capabilities.advancedfonts){
if($1>=-200&&$1<=200){
this.thickness=$1;this.tsprite.setThickness($1)
}else{

}}else{

}},"sizeToHeight",void 0,"$lzc$set_width",function($1){
var $2=this.tsprite;$2.setWidth($1);(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_width"]||this.nextMethod(arguments.callee,"$lzc$set_width")).call(this,$1);if(this.scrollwidth<this.width){
this.scrollwidth=this.width
};this.updateAttribute("maxhscroll",this.scrollwidth-this.width);if(this.sizeToHeight){
var $3=$2.getTextfieldHeight();if($3>0&&$3!=this.height){
this.$lzc$set_height($3)
}}},"getDefaultWidth",function(){
return 0
},"updateAttribute",function($1,$2){
this[$1]=$2;var $3=this["on"+$1];if($3.ready){
$3.sendEvent($2)
}},"updateLineAttribute",function($1,$2){
var $3=this.tsprite;var $4;if(this.capabilities.linescrolling){
$4=$3.pixelToLineNo($2)
}else{
$4=Math.round($2/this.lineheight)+1
};this.updateAttribute($1,$4)
},"lineheight",0,"$lzc$set_lineheight",function($1){

},"onlineheight",LzDeclaredEvent,"scrollevents",false,"$lzc$set_scrollevents",function($1){
this.scrollevents=$1;this.tsprite.setScrollEvents($1);if(this.onscrollevents.ready){
this.onscrollevents.sendEvent($1)
}},"onscrollevents",LzDeclaredEvent,"yscroll",0,"$lzc$set_yscroll",function($1){
if($1>0){
$1=0
};this.tsprite.setYScroll($1);this.updateAttribute("yscroll",$1);this.updateLineAttribute("scroll",-$1)
},"onyscroll",LzDeclaredEvent,"scrollheight",0,"$lzc$set_scrollheight",function($1){

},"onscrollheight",LzDeclaredEvent,"xscroll",0,"$lzc$set_xscroll",function($1){
if($1>0){
$1=0
};this.tsprite.setXScroll($1);this.updateAttribute("xscroll",$1);this.updateAttribute("hscroll",-$1)
},"onxscroll",LzDeclaredEvent,"scrollwidth",0,"$lzc$set_scrollwidth",function($1){

},"onscrollwidth",LzDeclaredEvent,"scroll",1,"$lzc$set_scroll",function($1){
if($1<1||$1>this.maxscroll){
$1=$1<1?1:this.maxscroll
};var $2=this.tsprite;var $3;if(this.capabilities.linescrolling){
$3=$2.lineNoToPixel($1)
}else{
$3=($1-1)*this.lineheight
};this.$lzc$set_yscroll(-$3)
},"onscroll",LzDeclaredEvent,"maxscroll",1,"$lzc$set_maxscroll",function($1){

},"onmaxscroll",LzDeclaredEvent,"hscroll",0,"$lzc$set_hscroll",function($1){
if($1<0||$1>this.maxhscroll){
$1=$1<1?1:this.maxhscroll
};this.$lzc$set_xscroll(-$1)
},"onhscroll",LzDeclaredEvent,"maxhscroll",0,"$lzc$set_maxhscroll",function($1){

},"onmaxhscroll",LzDeclaredEvent,"scrollevent",function($1,$2){
switch($1){
case "scrollTop":
this.updateAttribute("yscroll",-$2);this.updateLineAttribute("scroll",$2);break;
case "scrollLeft":
this.updateAttribute("xscroll",-$2);this.updateAttribute("hscroll",$2);break;
case "scrollHeight":
this.updateAttribute("scrollheight",$2);this.updateLineAttribute("maxscroll",Math.max(0,$2-this.height));break;
case "scrollWidth":
this.updateAttribute("scrollwidth",$2);this.updateAttribute("maxhscroll",Math.max(0,$2-this.width));break;
case "lineHeight":
this.updateAttribute("lineheight",$2);if(this.inited){
this.updateLineAttribute("scroll",-this.yscroll);this.updateLineAttribute("maxscroll",Math.max(0,this.scrollheight-this.height))
};break;
default:


}},"multiline",void 0,"$lzc$set_multiline",function($1){
this.multiline=$1=!(!$1);this.tsprite.setMultiline($1);this._updateSize()
},"resize",true,"$lzc$set_resize",function($1){
this.resize=$1;this.tsprite.setResize($1);this._updateSize()
},"text","","$lzc$set_text",function($1){
$1=String($1);if($1==this.getText()){
if(this.ontext.ready){
this.ontext.sendEvent($1)
};return
};var $2=this.tsprite;if(this.visible){
$2.setVisible(this.visible)
};if($1.length>this.maxlength){
$1=$1.substring(0,this.maxlength)
};$2.setText($1);this.text=$1;this._updateSize();if(this.ontext.ready){
this.ontext.sendEvent($1)
}},"_updateSize",function(){
if(!this.isinited){
return
};if(this.width==0||this.resize&&this.multiline==false){
var $1=this.getTextWidth();if($1!=this.width){
this.$lzc$set_width($1)
}};if(this.sizeToHeight){
var $2=this.tsprite.getTextfieldHeight();if($2>0&&$2!=this.height){
this.$lzc$set_height($2)
}}},"ontext",LzDeclaredEvent,"ontextlink",LzDeclaredEvent,"maxlength",Infinity,"$lzc$set_maxlength",function($1){
if($1==null){
$1=Infinity
};if(isNaN($1)){
return
};this.maxlength=$1;this.tsprite.setMaxLength($1);if(this.onmaxlength.ready){
this.onmaxlength.sendEvent($1)
};var $2=this.getText();if($2&&$2.length>this.maxlength){
this._updateSize()
}},"onmaxlength",LzDeclaredEvent,"pattern",void 0,"$lzc$set_pattern",function($1){
if($1==null||$1==""){
return
};this.pattern=$1;this.tsprite.setPattern($1);if(this.onpattern.ready){
this.onpattern.sendEvent($1)
}},"onpattern",LzDeclaredEvent,"$lzc$set_fontstyle",function($1){
if($1=="plain"||$1=="bold"||$1=="italic"||$1=="bolditalic"||$1=="bold italic"){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_fontstyle"]||this.nextMethod(arguments.callee,"$lzc$set_fontstyle")).call(this,$1);this.tsprite.setFontStyle($1);this._updateSize()
}else{

}},"$lzc$set_font",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_font"]||this.nextMethod(arguments.callee,"$lzc$set_font")).call(this,$1);this.tsprite.setFontName($1);this._updateSize()
},"$lzc$set_fontsize",function($1){
if($1<=0||isNaN($1)){

}else{
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_fontsize"]||this.nextMethod(arguments.callee,"$lzc$set_fontsize")).call(this,$1);this.tsprite.setFontSize($1);this._updateSize()
}},"textalign","left","$lzc$set_textalign",function($1){
$1=$1?$1.toLowerCase():"left";if(!($1=="left"||$1=="right"||$1=="center"||$1=="justify")){
$1="left"
};this.textalign=$1;this.tsprite.setTextAlign($1);this._updateSize()
},"textindent",0,"$lzc$set_textindent",function($1){
if($1<0||isNaN($1)){

}else{
this.textindent=$1;this.tsprite.setTextIndent($1);this._updateSize()
}},"letterspacing",0,"$lzc$set_letterspacing",function($1){
if($1<0||isNaN($1)){

}else{
this.letterspacing=$1;this.tsprite.setLetterSpacing($1);this._updateSize()
}},"textdecoration","none","$lzc$set_textdecoration",function($1){
$1=$1?$1.toLowerCase():"none";if(!($1=="none"||$1=="underline")){
$1="none"
};this.textdecoration=$1;this.tsprite.setTextDecoration($1);this._updateSize()
},"construct",function($1,$2){
this.multiline=("multiline" in $2)?$2.multiline:null;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.sizeToHeight=false;for(var $3 in LzText.fontArgToAttr){
var $4=LzText.fontArgToAttr[$3];if(!($3 in $2)){
$2[$3]=this.searchParents($4)[$4]
};this[$4]=$2[$3]
};if(!("fgcolor" in $2)){
var $5=this;do{
$5=$5.immediateparent;var $6=$5["hasfgcolor"];if($6!=null&&$6){
$2["fgcolor"]=$5["fgcolor"];break
}}while($5!=canvas)
};var $7=this.tsprite;$7.__initTextProperties($2);for(var $3 in LzText.fontArgToAttr){
delete $2[$3]
};this.yscroll=0;this.xscroll=0;this.resize=("resize" in $2)?!(!$2.resize):this.resize;this.$lzc$set_resize(this.resize);if($2["maxlength"]!=null){
this.$lzc$set_maxlength($2.maxlength)
};this.text=$2["text"]!=null?String($2.text):"";if(this.text.length>this.maxlength){
this.text=this.text.substring(0,this.maxlength)
};this.$lzc$set_multiline(this.multiline);$7.setText(this.text);if(!this.hassetwidth){
if(this.multiline){
$2.width=this.parent.width
}else{
if(this.text!=null&&this.text!=""&&this.text.length>0){
$2.width=this.getTextWidth()
}else{
$2.width=this.getDefaultWidth()
}}}else{
this.$lzc$set_resize(false)
};if(!this.hassetheight){
this.sizeToHeight=true
}else{
if($2["height"]!=null){
this.$lzc$set_height($2.height)
}};if($2["pattern"]!=null){
this.$lzc$set_pattern($2.pattern)
};if(this.capabilities.advancedfonts){
if(!("antiAliasType" in $2)){
this.$lzc$set_antiAliasType("advanced")
};if(!("gridFit" in $2)){
this.$lzc$set_gridFit("subpixel")
}};this._updateSize()
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);this._updateSize()
},"tsprite",void 0,"__makeSprite",function($1){
this.sprite=this.tsprite=new LzTextSprite(this,$1)
},"getMCRef",function(){
return this.tsprite.getMCRef()
},"setResize",function($1){
this.$lzc$set_resize($1)
},"addText",function($1){
this.$lzc$set_text(this.getText()+$1)
},"clearText",function(){
this.$lzc$set_text("")
},"setMaxLength",function($1){
this.$lzc$set_maxlength($1)
},"setPattern",function($1){
this.$lzc$set_pattern($1)
},"getTextWidth",function(){
return this.tsprite.getTextWidth()
},"$lzc$getTextWidth_dependencies",function($1,$2){
return [$2,"text"]
},"getTextHeight",function(){
return this.tsprite.getTextfieldHeight()
},"$lzc$getTextHeight_dependencies",function($1,$2){
return [$2,"text"]
},"applyData",function($1){
if(null==$1){
this.clearText()
}else{
this.$lzc$set_text($1)
}},"setScroll",function($1){
this.$lzc$set_scroll($1)
},"getScroll",function(){
return this.scroll
},"getMaxScroll",function(){
return this.maxscroll
},"$lzc$getMaxScroll_dependencies",function($1,$2){
return [$2,"maxscroll"]
},"getBottomScroll",function(){
return this.scroll+this.height/this.lineheight
},"setXScroll",function($1){
this.$lzc$set_xscroll($1)
},"setYScroll",function($1){
this.$lzc$set_yscroll($1)
},"setHScroll",function($1){
this.$lzc$set_hscroll($1)
},"annotateAAimg",function($1){
if(typeof $1=="undefined"){
return
};if($1.length==0){
return
};var $2="";var $3=0;var $4=0;var $5;var $6="<img ";while(true){
$5=$1.indexOf($6,$3);if($5<0){
$2+=$1.substring($3);break
};$2+=$1.substring($3,$5+$6.length);$3=$5+$6.length;var $7={};$4=$3+this.parseImgAttributes($7,$1.substring($3));$2+=$1.substring($3,$4+1);if($7["alt"]!=null){
var $8=$7["alt"];$2+="[image "+$8+"]"
};$3=$4+1
};return $2
},"parseImgAttributes",function($1,$2){
var $3;var $4=0;var $5="attrname";var $6="attrval";var $7="whitespace";var $8="whitespace2";var $9=$7;var $10=$2.length;var $11;var $12;var $13;for($3=0;$3<$10;$3++){
$4=$3;var $14=$2.charAt($3);if($14==">"){
break
};if($9==$7){
if($14!=" "){
$9=$5;$11=$14
}}else{
if($9==$5){
if($14==" "||$14=="="){
$9=$8
}else{
$11+=$14
}}else{
if($9==$8){
if($14==" "||$14=="="){
continue
}else{
$9=$6;$13=$14;$12=""
}}else{
if($9==$6){
if($14!=$13){
$12+=$14
}else{
$9=$7;$1[$11]=$12
}}}}}};return $4
},"setText",function($1){
this.$lzc$set_text($1)
},"format",function($1){
var $2=Array.prototype.slice.call(arguments,1);this.$lzc$set_text(this.formatToString.apply(this,[$1].concat($2)))
},"addFormat",function($1){
var $2=Array.prototype.slice.call(arguments,1);this.$lzc$set_text(this.getText()+this.formatToString.apply(this,[$1].concat($2)))
},"updateMaxLines",function(){
var $1=Math.floor(this.height/(this.font.height-1));if($1!=this.maxlines){
this.maxlines=$1
}},"getText",function(){
return this.text
},"$lzc$getText_dependencies",function($1,$2){
return [$2,"text"]
},"escapeText",function($1){
var $2=$1==null?this.text:$1;var $3;for(var $4 in LzText.escapeChars){
while($2.indexOf($4)>-1){
$3=$2.indexOf($4);$2=$2.substring(0,$3)+LzText.escapeChars[$4]+$2.substring($3+1)
}};return $2
},"setSelectable",function($1){
this.$lzc$set_selectable($1)
},"setFontSize",function($1){
this.$lzc$set_fontsize($1)
},"setFontStyle",function($1){
this.$lzc$set_fontstyle($1)
},"setMultiline",function($1){
this.$lzc$set_multiline($1)
},"setBorder",function($1){
this.tsprite.setBorder($1)
},"setWordWrap",function($1){
this.tsprite.setWordWrap($1)
},"setEmbedFonts",function($1){
this.tsprite.setEmbedFonts($1)
},"setAntiAliasType",function($1){
this.$lzc$set_antiAliasType($1)
},"getAntiAliasType",function(){
if(this.capabilities.advancedfonts){
return this.antiAliasType
}else{

}},"setGridFit",function($1){
this.$lzc$set_gridFit($1)
},"getGridFit",function(){
if(this.capabilities.advancedfonts){
return this.gridFit
}else{

}},"setSharpness",function($1){
this.$lzc$set_sharpness($1)
},"getSharpness",function(){
if(this.capabilities.advancedfonts){
return this.sharpness
}else{

}},"setThickness",function($1){
this.$lzc$set_thickness($1)
},"getThickness",function(){
if(this.capabilities.advancedfonts){
return this.thickness
}else{

}},"setSelection",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($2==null){
$2=$1
};this.tsprite.setSelection($1,$2)
},"getSelectionPosition",function(){
return this.tsprite.getSelectionPosition()
},"getSelectionSize",function(){
return this.tsprite.getSelectionSize()
},"makeTextLink",function($1,$2){
return this.tsprite.makeTextLink($1,$2)
},"toString",function(){
return "LzText: "+this.getText()
}],["tagname","text","attributes",new LzInheritedHash(LzView.attributes),"fontArgToAttr",{font:"fontname",fontsize:"fontsize",fontstyle:"fontstyle"},"escapeChars",{">":"&gt;","<":"&lt;"}]);(function($1){
with($1){
with($1.prototype){
LzText.attributes.pixellock=true;LzText.attributes.clip=true;LzText.attributes.selectable=false
}}})(LzText);lz[LzText.tagname]=LzText;Class.make("LzInputText",LzText,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"password",void 0,"onenabled",LzDeclaredEvent,"getDefaultWidth",function(){
return 100
},"_onfocusDel",null,"_onblurDel",null,"_modemanagerDel",null,"construct",function($1,$2){
this.password=("password" in $2)?!(!$2.password):false;this.resize=("resize" in $2)?!(!$2.resize):false;this.focusable=true;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this._onfocusDel=new LzDelegate(this,"_gotFocusEvent",this,"onfocus");this._onblurDel=new LzDelegate(this,"_gotBlurEvent",this,"onblur");this._modemanagerDel=new LzDelegate(this,"_modechanged",lz.ModeManager,"onmode")
},"destroy",function(){
if(this._onfocusDel){
this._onfocusDel.unregisterAll();this._onfocusDel=null
};if(this._onblurDel){
this._onblurDel.unregisterAll();this._onblurDel=null
};if(this._modemanagerDel){
this._modemanagerDel.unregisterAll();this._modemanagerDel=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"isprite",void 0,"__makeSprite",function($1){
this.sprite=this.tsprite=this.isprite=new LzInputTextSprite(this,$1)
},"_focused",false,"_gotFocusEvent",function($1){
switch(arguments.length){
case 0:
$1=null;

};this._focused=true;var $2=this.sprite;$2.gotFocus()
},"_gotBlurEvent",function($1){
switch(arguments.length){
case 0:
$1=null;

};this._focused=false;var $2=this.sprite;$2.gotBlur()
},"inputtextevent",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($1=="onfocus"&&this._focused){
return
};if($1=="onblur"&&!this._focused){
return
};if($1=="onfocus"){
this._focused=true;if(lz.Focus.getFocus()!==this){
var $3=lz.Keys.isKeyDown("tab");lz.Focus.setFocus(this,$3)
}}else{
if($1=="onchange"){
var $4=this.sprite;this.text=$4.getText();if(this.multiline&&this.sizeToHeight&&this.height!=$4.getTextfieldHeight()){
this.$lzc$set_height($4.getTextfieldHeight())
};if(this.ontext.ready){
this.ontext.sendEvent($2)
}}else{
if($1=="onblur"){
this._focused=false;if(lz.Focus.getFocus()===this){
lz.Focus.clearFocus()
}}else{

}}}},"updateData",function(){
var $1=this.sprite;return $1.getText()
},"enabled",true,"$lzc$set_enabled",function($1){
this.focusable=true;this.enabled=$1;var $2=this.sprite;$2.setEnabled($1);if(this.onenabled.ready){
this.onenabled.sendEvent($1)
}},"setEnabled",function($1){
this.$lzc$set_enabled($1)
},"setHTML",function($1){
if(this.capabilities["htmlinputtext"]){
var $2=this.sprite;$2.setHTML($1)
}else{

}},"getText",function(){
return this.sprite.getText()
},"_allowselectable",true,"_selectable",void 0,"_modechanged",function($1){
this._setallowselectable(!$1||lz.ModeManager.__LZallowInput($1,this))
},"_setallowselectable",function($1){
this._allowselectable=$1;this.$lzc$set_selectable(this._selectable)
},"$lzc$set_selectable",function($1){
this._selectable=$1;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_selectable"]||this.nextMethod(arguments.callee,"$lzc$set_selectable")).call(this,this._allowselectable?$1:false)
}],["tagname","inputtext","attributes",new LzInheritedHash(LzText.attributes)]);(function($1){
with($1){
with($1.prototype){
LzNode.mergeAttributes({selectable:true,enabled:true},LzInputText.attributes)
}}})(LzInputText);lz[LzInputText.tagname]=LzInputText;Class.make("LzViewLinkage",null,["scale",1,"offset",0,"uplinkArray",null,"downlinkArray",null,"$lzsc$initialize",function($1,$2){
this.scale=new Object();this.offset=new Object();if($1==$2){
return
};this.uplinkArray=[];var $3=$1;do{
$3=$3.immediateparent;this.uplinkArray.push($3)
}while($3!=$2&&$3!=canvas);this.downlinkArray=[];if($3==$2){
return
};var $3=$2;do{
$3=$3.immediateparent;this.downlinkArray.push($3)
}while($3!=canvas);while(this.uplinkArray.length>1&&this.downlinkArray[this.downlinkArray.length-1]==this.uplinkArray[this.uplinkArray.length-1]&&this.downlinkArray[this.downlinkArray.length-2]==this.uplinkArray[this.uplinkArray.length-2]){
this.downlinkArray.pop();this.uplinkArray.pop()
}},"update",function($1){
var $2=1;var $3=0;var $4="_"+$1+"scale";if(this.uplinkArray){
var $5=this.uplinkArray.length;for(var $6=0;$6<$5;$6++){
var $7=this.uplinkArray[$6];$2*=$7[$4];$3+=$7[$1]/$2
}};if(this.downlinkArray){
for(var $6=this.downlinkArray.length-1;$6>=0;$6--){
var $7=this.downlinkArray[$6];$3-=$7[$1]/$2;$2/=$7[$4]
}};this.scale[$1]=$2;this.offset[$1]=$3
}],null);Class.make("LzCanvas",LzView,["updatePercentCreatedEnabled",true,"resourcetable",void 0,"_lzinitialsubviews",[],"totalnodes",void 0,"framerate",30,"onframerate",LzDeclaredEvent,"creatednodes",void 0,"__LZproxied",void 0,"embedfonts",void 0,"lpsbuild",void 0,"lpsbuilddate",void 0,"appbuilddate",void 0,"runtime",void 0,"allowfullscreen",void 0,"fullscreen",void 0,"onfullscreen",LzDeclaredEvent,"__LZmouseupDel",void 0,"__LZmousedownDel",void 0,"__LZmousemoveDel",void 0,"__LZDefaultCanvasMenu",void 0,"httpdataprovider",null,"defaultdataprovider",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4);this.datasets={};this.__LZcheckwidth=null;this.__LZcheckheight=null;this.hassetwidth=true;this.hassetheight=true
},"construct",function($1,args){
var $3;$3=function($1,$2){
var $3=args[$1];delete args[$1];if($3!=null){
return !(!$3)
}else{
if($2!=null){
var $4=lz.Browser.getInitArg($2);if($4!=null){
return $4=="true"
}}};return void 0
};this.__makeSprite(null);var $2=this.sprite.capabilities;this.capabilities=$2;this.immediateparent=this;this.datapath=new LzDatapath(this);this.mask=null;this.accessible=$3("accessible",null);if($2.accessibility==true){
this.sprite.setAccessible(this.accessible);if(this.accessible){
this.sprite.setAAActive(true);this.sprite.setAASilent(false)
}}else{
if(this.accessible){
this.accessible=false
}};this.history=$3("history","history");if(this.history&&$2.history!=true){
this.history=false
};this.allowfullscreen=$3("allowfullscreen","allowfullscreen");if(this.allowfullscreen&&$2.allowfullscreen!=true){
this.allowfullscreen=false
};this.fullscreen=false;this.viewLevel=0;this.resourcetable={};this.totalnodes=0;this.creatednodes=0;this.percentcreated=0;if(!args.framerate){
args.framerate=30
};this.proxied=$3("proxied","lzproxied");if(this.proxied==null){
this.proxied=args.__LZproxied=="true"
};if(typeof args.proxyurl=="undefined"){
this.proxyurl=lz.Browser.getBaseURL().toString()
};if(args.focustrap){
if($2.disableglobalfocustrap!=true){
delete args.focustrap
}};LzScreenKernel.setCallback(this,"__windowResize");delete args.width;delete args.height;if(args["fgcolor"]!=null){
this.hasfgcolor=true
};this.lpsversion=args.lpsversion+"."+this.__LZlfcversion;delete args.lpsversion;this.__LZdelayedSetters=LzView.__LZdelayedSetters;this.earlySetters=LzView.earlySetters;if(!this.version){
this.version=this.lpsversion
};this.isinited=false;this._lzinitialsubviews=[];this.datasets={};global.canvas=this;this.parent=this;this.makeMasked();this.__LZmouseupDel=new LzDelegate(this,"__LZmouseup",lz.GlobalMouse,"onmouseup");this.__LZmousedownDel=new LzDelegate(this,"__LZmousedown",lz.GlobalMouse,"onmousedown");this.__LZmousemoveDel=new LzDelegate(this,"__LZmousemove",lz.GlobalMouse,"onmousemove");this.defaultdataprovider=this.httpdataprovider=new LzHTTPDataProvider();this.id=lz.Browser.getAppID()
},"__LZmouseup",function($1){
if(this.onmouseup.ready){
this.onmouseup.sendEvent()
}},"__LZmousemove",function($1){
if(this.onmousemove.ready){
this.onmousemove.sendEvent()
}},"__LZmousedown",function($1){
if(this.onmousedown.ready){
this.onmousedown.sendEvent()
}},"__makeSprite",function($1){
this.sprite=new LzSprite(this,true)
},"onmouseleave",LzDeclaredEvent,"onmouseenter",LzDeclaredEvent,"onpercentcreated",LzDeclaredEvent,"onmousemove",LzDeclaredEvent,"onafterinit",LzDeclaredEvent,"lpsversion",void 0,"lpsrelease",void 0,"version",null,"__LZlfcversion","0","proxied",true,"dataloadtimeout",30000,"medialoadtimeout",30000,"mediaerrortimeout",4500,"percentcreated",void 0,"datasets",null,"compareVersion",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($2==null){
$2=this.lpsversion
};if($1==$2){
return 0
};var $3=$1.split(".");var $4=$2.split(".");var $5=0;while($5<$3.length||$5<$4.length){
var $6=Number($3[$5])||0;var $7=Number($4[$5++])||0;if($6<$7){
return -1
}else{
if($6>$7){
return 1
}}};return 0
},"$lzc$set_resource",function($1){

},"$lzc$set_focustrap",function($1){
lz.Keys.setGlobalFocusTrap($1)
},"toString",function(){
return "This is the canvas"
},"$lzc$set_framerate",function($1){
$1*=1;if($1<1){
$1=1
}else{
if($1>1000){
$1=1000
}};this.framerate=$1;lz.Idle.setFrameRate($1);if(this.onframerate.ready){
this.onframerate.sendEvent($1)
}},"$lzc$set_fullscreen",function($1){
switch(arguments.length){
case 0:
$1=true;

};if(this.sprite.capabilities.allowfullscreen==true){
LzScreenKernel.showFullScreen($1)
}else{

}},"__fullscreenEventCallback",function($1,$2){
this.fullscreen=$2;this.onfullscreen.sendEvent($1)
},"__fullscreenErrorCallback",function($1){

},"$lzc$set_allowfullscreen",function($1){
this.allowfullscreen=$1
},"initDone",function(){
var $1=[];var $2=[];var $3=this._lzinitialsubviews;for(var $4=0,$5=$3.length;$4<$5;++$4){
var $6=$3[$4];if($6["attrs"]&&$6.attrs["initimmediate"]){
$1.push($6)
}else{
$2.push($6)
}};$1.push.apply($1,$2);this._lzinitialsubviews=[];lz.Instantiator.requestInstantiation(this,$1)
},"init",function(){
this.sprite.init(true);if(this.history==true){
lz.History.__start(this.id)
};if(this.contextmenu==null){
this.buildDefaultMenu()
}},"deferInit",true,"__LZinstantiationDone",function(){
this.__LZinstantiated=true;if(this.deferInit){
this.deferInit=false;return
};this.percentcreated=1;this.updatePercentCreatedEnabled=false;if(this.onpercentcreated.ready){
this.onpercentcreated.sendEvent(this.percentcreated)
};lz.Instantiator.resume();this.__LZcallInit()
},"updatePercentCreated",function(){
this.percentcreated=Math.max(this.percentcreated,this.creatednodes/this.totalnodes);this.percentcreated=Math.min(0.99,this.percentcreated);if(this.onpercentcreated.ready){
this.onpercentcreated.sendEvent(this.percentcreated)
}},"initiatorAddNode",function($1,$2){
this.totalnodes+=$2;this._lzinitialsubviews.push($1)
},"__LZcallInit",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(this.isinited){
return
};this.isinited=true;this.__LZresolveReferences();var $2=this.subnodes;if($2){
for(var $3=0;$3<$2.length;){
var $4=$2[$3++];var $5=$2[$3];if($4.isinited||!$4.__LZinstantiated){
continue
};$4.__LZcallInit();if($5!=$2[$3]){
while($3>0){
if($5==$2[--$3]){
break
}}}}};this.init();if(this.oninit.ready){
this.oninit.sendEvent(this)
};if(this.onafterinit.ready){
this.onafterinit.sendEvent(this)
};if(this.datapath&&this.datapath.__LZApplyDataOnInit){
this.datapath.__LZApplyDataOnInit()
};this.inited=true;if(this.oninited.ready){
this.oninited.sendEvent(true)
}},"isProxied",function(){
return this.proxied
},"$lzc$set_width",function($1){
LzSprite.setRootWidth($1)
},"$lzc$set_x",function($1){
LzSprite.setRootX($1)
},"$lzc$set_height",function($1){
LzSprite.setRootHeight($1)
},"$lzc$set_y",function($1){
LzSprite.setRootY($1)
},"setDefaultContextMenu",function($1){
this.$lzc$set_contextmenu($1);this.sprite.setDefaultContextMenu($1)
},"buildDefaultMenu",function(){
this.__LZDefaultCanvasMenu=new LzContextMenu();this.__LZDefaultCanvasMenu.hideBuiltInItems();var $1=new LzContextMenuItem("About OpenLaszlo...",new LzDelegate(this,"__LZdefaultMenuItemHandler"));this.__LZDefaultCanvasMenu.addItem($1);if(this.proxied){
var $2=new LzContextMenuItem("View Source",new LzDelegate(this,"__LZviewSourceMenuItemHandler"));this.__LZDefaultCanvasMenu.addItem($2)
};this.setDefaultContextMenu(this.__LZDefaultCanvasMenu)
},"__LZdefaultMenuItemHandler",function($1){
lz.Browser.loadURL("http://www.openlaszlo.org","lz_about")
},"__LZviewSourceMenuItemHandler",function($1){
var $2=lz.Browser.getBaseURL()+"?lzt=source";lz.Browser.loadURL($2,"lz_source")
},"__windowResize",function($1){
this.width=$1.width;if(this.onwidth.ready){
this.onwidth.sendEvent(this.width)
};this.sprite.setWidth(this.width);this.height=$1.height;if(this.onheight.ready){
this.onheight.sendEvent(this.height)
};this.sprite.setHeight(this.height)
},"LzInstantiateView",function($1,$2){
switch(arguments.length){
case 1:
$2=1;

};canvas.initiatorAddNode($1,$2)
},"lzAddLocalData",function($1,$2,$3,$4){
switch(arguments.length){
case 3:
$4=false;

};return new LzDataset(canvas,{name:$1,initialdata:$2,trimwhitespace:$3,nsprefix:$4})
}],["tagname","canvas","attributes",new LzInheritedHash(LzView.attributes),"versionInfoString",function(){
return "URL: "+lz.Browser.getLoadURL()+"\n"+"LPS\n"+"  Version: "+canvas.lpsversion+"\n"+"  Release: "+canvas.lpsrelease+"\n"+"  Build: "+canvas.lpsbuild+"\n"+"  Date: "+canvas.lpsbuilddate+"\n"+"Application\n"+"  Date: "+canvas.appbuilddate+"\n"+"Target: "+canvas.runtime+"\n"+"Runtime: "+lz.Browser.getVersion()+"\n"+"OS: "+lz.Browser.getOS()+"\n"
}]);lz[LzCanvas.tagname]=LzCanvas;var canvas;Class.make("LzScript",LzNode,["src",void 0,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4);$2.script()
}],["tagname","script","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzScript.tagname]=LzScript;Class.make("LzAnimatorGroup",LzNode,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"updateDel",void 0,"crepeat",void 0,"startTime",void 0,"__LZpauseTime",void 0,"actAnim",void 0,"notstarted",void 0,"needsrestart",void 0,"attribute",void 0,"start",true,"from",void 0,"to",void 0,"duration",void 0,"indirect",false,"relative",false,"motion","easeboth","repeat",1,"$lzc$set_repeat",function($1){
if($1<=0){
$1=Infinity
};this.repeat=$1
},"paused",false,"$lzc$set_paused",function($1){
this.pause($1)
},"started",void 0,"target",void 0,"process","sequential","isactive",false,"ontarget",LzDeclaredEvent,"onduration",LzDeclaredEvent,"onstarted",LzDeclaredEvent,"onstart",LzDeclaredEvent,"onpaused",LzDeclaredEvent,"onstop",LzDeclaredEvent,"onrepeat",LzDeclaredEvent,"animatorProps",{attribute:true,from:true,duration:true,to:true,relative:true,target:true},"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);var $3=this.immediateparent;if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($3):$3 instanceof LzAnimatorGroup){
for(var $4 in this.animatorProps){
if($2[$4]==null){
$2[$4]=$3[$4]
}};if($3.animators==null){
$3.animators=[this]
}else{
$3.animators.push(this)
};$2.start=LzNode._ignoreAttribute
}else{
this.target=$3
};if(!this.updateDel){
this.updateDel=new LzDelegate(this,"update")
}},"init",function(){
if(!this.target){
this.target=this.immediateparent
};if(this.started){
this.doStart()
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this)
},"$lzc$set_target",function($1){
this.target=$1;var $2=this.subnodes;if($2){
for(var $3=0;$3<$2.length;$3++){
if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($2[$3]):$2[$3] instanceof LzAnimatorGroup){
$2[$3].$lzc$set_target($1)
}}};if(this.ontarget.ready){
this.ontarget.sendEvent($1)
}},"setTarget",function($1){
this.$lzc$set_target($1)
},"$lzc$set_start",function($1){
this.started=$1;if(this.onstarted.ready){
this.onstarted.sendEvent($1)
};if(!this.isinited){
return
};if($1){
this.doStart()
}else{
this.stop()
}},"doStart",function(){
if(this.isactive){
return false
};if(this.onstart.ready){
this.onstart.sendEvent(new Date().getTime())
};this.isactive=true;this.prepareStart();this.updateDel.register(lz.Idle,"onidle");return true
},"prepareStart",function(){
this.crepeat=this.repeat;for(var $1=this.animators.length-1;$1>=0;$1--){
this.animators[$1].notstarted=true
};this.actAnim=this.animators.concat()
},"resetAnimator",function(){
this.actAnim=this.animators.concat();for(var $1=this.animators.length-1;$1>=0;$1--){
this.animators[$1].needsrestart=true
}},"update",function($1){
if(this.paused){
return false
};var $2=this.actAnim.length-1;if($2>0&&this.process=="sequential"){
$2=0
};for(var $3=$2;$3>=0;$3--){
var $4=this.actAnim[$3];if($4.notstarted){
$4.isactive=true;$4.prepareStart();$4.notstarted=false
}else{
if($4.needsrestart){
$4.resetAnimator();$4.needsrestart=false
}};if($4.update($1)){
this.actAnim.splice($3,1)
}};if(!this.actAnim.length){
return this.checkRepeat()
};return false
},"pause",function($1){
switch(arguments.length){
case 0:
$1=null;

};if($1==null){
$1=!this.paused
};if(this.paused&&!$1){
this.__LZaddToStartTime(new Date().getTime()-this.__LZpauseTime)
}else{
if(!this.paused&&$1){
this.__LZpauseTime=new Date().getTime()
}};this.paused=$1;if(this.onpaused.ready){
this.onpaused.sendEvent($1)
}},"__LZaddToStartTime",function($1){
this.startTime+=$1;if(this.actAnim){
for(var $2=0;$2<this.actAnim.length;$2++){
this.actAnim[$2].__LZaddToStartTime($1)
}}},"stop",function(){
if(this.actAnim){
var $1=this.actAnim.length-1;if($1>0&&this.process=="sequential"){
$1=0
};for(var $2=$1;$2>=0;$2--){
this.actAnim[$2].stop()
}};this.__LZhalt()
},"setDuration",function($1){
this.$lzc$set_duration($1)
},"$lzc$set_duration",function($1){
if(isNaN($1)){
$1=0
}else{
$1=Number($1)
};this.duration=$1;var $2=this.subnodes;if($2){
for(var $3=0;$3<$2.length;++$3){
if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($2[$3]):$2[$3] instanceof LzAnimatorGroup){
$2[$3].$lzc$set_duration($1)
}}};this.onduration.sendEvent($1)
},"__LZfinalizeAnim",function(){
this.__LZhalt()
},"__LZhalt",function(){
this.isactive=false;this.updateDel.unregisterAll();if(this.onstop.ready){
this.onstop.sendEvent(new Date().getTime())
}},"checkRepeat",function(){
if(this.crepeat==1){
this.__LZfinalizeAnim();return true
}else{
this.crepeat--;if(this.onrepeat.ready){
this.onrepeat.sendEvent(new Date().getTime())
};this.resetAnimator();return false
}},"destroy",function(){
this.stop();this.updateDel.unregisterAll();this.animators=null;this.actAnim=null;var $1=this.immediateparent;var $2=$1.animators;if($2&&$2.length){
for(var $3=0;$3<$2.length;$3++){
if($2[$3]==this){
$2.splice($3,1);break
}};if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($1):$1 instanceof LzAnimatorGroup){
var $4=$1.actAnim;if($4&&$4.length){
for(var $3=0;$3<$4.length;$3++){
if($4[$3]==this){
$4.splice($3,1);break
}}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"toString",function(){
if(this.animators){
return "Group of "+this.animators.length
};return "Empty group"
}],["tagname","animatorgroup","attributes",new LzInheritedHash(LzNode.attributes)]);(function($1){
with($1){
with($1.prototype){
LzAnimatorGroup.attributes.start=true;LzAnimatorGroup.attributes.ignoreplacement=true
}}})(LzAnimatorGroup);lz[LzAnimatorGroup.tagname]=LzAnimatorGroup;Class.make("LzAnimator",LzAnimatorGroup,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"calcMethod",void 0,"lastIterationTime",void 0,"currentValue",void 0,"doBegin",void 0,"beginPoleDelta",0.25,"endPoleDelta",0.25,"primary_K",void 0,"origto",void 0,"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.calcMethod=this.calcNextValue;this.primary_K=1
},"$lzc$set_motion",function($1){
this.motion=$1;if($1=="linear"){
this.calcMethod=this.calcNextValueLinear
}else{
this.calcMethod=this.calcNextValue;if($1=="easeout"){
this.beginPoleDelta=100
}else{
if($1=="easein"){
this.endPoleDelta=15
}}}},"setMotion",function($1){
this.$lzc$set_motion($1)
},"$lzc$set_to",function($1){
this.origto=Number($1)
},"setTo",function($1){
this.$lzc$set_to($1)
},"calcControlValues",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.currentValue=$1||0;var $2=this.indirect?-1:1;if(this.currentValue<this.to){
this.beginPole=this.currentValue-$2*this.beginPoleDelta;this.endPole=this.to+$2*this.endPoleDelta
}else{
this.beginPole=this.currentValue+$2*this.beginPoleDelta;this.endPole=this.to-$2*this.endPoleDelta
};this.primary_K=1;var $3=1*(this.beginPole-this.to)*(this.currentValue-this.endPole);var $4=1*(this.beginPole-this.currentValue)*(this.to-this.endPole);if($4!=0){
this.primary_K=Math.abs($3/$4)
}},"doStart",function(){
if(this.isactive){
return
};this.isactive=true;this.prepareStart();this.updateDel.register(lz.Idle,"onidle")
},"prepareStart",function(){
this.crepeat=this.repeat;var $1=this.target;var $2=this.attribute;if(this.from!=null){
var $lzsc$797907640=Number(this.from);if(!$1.__LZdeleted){
var $lzsc$1829347371="$lzc$set_"+$2;if(Function["$lzsc$isa"]?Function.$lzsc$isa($1[$lzsc$1829347371]):$1[$lzsc$1829347371] instanceof Function){
$1[$lzsc$1829347371]($lzsc$797907640)
}else{
$1[$2]=$lzsc$797907640;var $lzsc$1523508926=$1["on"+$2];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1523508926):$lzsc$1523508926 instanceof LzEvent){
if($lzsc$1523508926.ready){
$lzsc$1523508926.sendEvent($lzsc$797907640)
}}}}};if(this.relative){
this.to=this.origto
}else{
this.to=this.origto-$1.getExpectedAttribute($2)
};$1.addToExpectedAttribute($2,this.to);$1.__LZincrementCounter($2);this.currentValue=0;this.calcControlValues();this.doBegin=true
},"resetAnimator",function(){
var $1=this.target;var $2=this.attribute;var $3=this.from;if($3!=null){
if(!$1.__LZdeleted){
var $lzsc$630787167="$lzc$set_"+$2;if(Function["$lzsc$isa"]?Function.$lzsc$isa($1[$lzsc$630787167]):$1[$lzsc$630787167] instanceof Function){
$1[$lzsc$630787167]($3)
}else{
$1[$2]=$3;var $lzsc$1147027649=$1["on"+$2];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1147027649):$lzsc$1147027649 instanceof LzEvent){
if($lzsc$1147027649.ready){
$lzsc$1147027649.sendEvent($3)
}}}};var $4=$3-$1.getExpectedAttribute($2);$1.addToExpectedAttribute($2,$4)
};if(!this.relative){
this.to=this.origto-$1.getExpectedAttribute($2);this.calcControlValues()
};$1.addToExpectedAttribute($2,this.to);$1.__LZincrementCounter($2);this.currentValue=0;this.doBegin=true
},"beginAnimator",function($1){
this.startTime=$1;this.lastIterationTime=$1;if(this.onstart.ready){
this.onstart.sendEvent($1)
};this.doBegin=false
},"stop",function(){
if(!this.isactive){
return
};var $1=this.target;var $2="e_"+this.attribute;if(!$1[$2].c){
$1[$2].c=0
};$1[$2].c-=1;if($1[$2].c<=0){
$1[$2].c=0;$1[$2].v=null
}else{
$1[$2].v-=this.to-this.currentValue
};this.__LZhalt()
},"__LZfinalizeAnim",function(){
var $1=this.target;var $2=this.attribute;var $3="e_"+$2;if(!$1[$3].c){
$1[$3].c=0
};$1[$3].c-=1;if($1[$3].c<=0){
$1[$3].c=0;var $lzsc$2051742267=$1[$3].v;if(!$1.__LZdeleted){
var $lzsc$1440183177="$lzc$set_"+$2;if(Function["$lzsc$isa"]?Function.$lzsc$isa($1[$lzsc$1440183177]):$1[$lzsc$1440183177] instanceof Function){
$1[$lzsc$1440183177]($lzsc$2051742267)
}else{
$1[$2]=$lzsc$2051742267;var $lzsc$564137395=$1["on"+$2];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$564137395):$lzsc$564137395 instanceof LzEvent){
if($lzsc$564137395.ready){
$lzsc$564137395.sendEvent($lzsc$2051742267)
}}}};$1[$3].v=null
};this.__LZhalt()
},"calcNextValue",function($1){
var $2=this.currentValue;var $3=this.endPole;var $4=this.beginPole;var $5=Math.exp($1*1/this.duration*Math.log(this.primary_K));if($5!=1){
var $6=$4*$3*(1-$5);var $7=$3-$5*$4;if($7!=0){
$2=$6/$7
}};return $2
},"calcNextValueLinear",function($1){
var $2=$1/this.duration;return $2*this.to
},"update",function($1){
if(this.doBegin){
this.beginAnimator($1)
}else{
if(!this.paused){
var $2=$1-this.startTime;if($2<this.duration){
this.setValue(this.calcMethod($2));this.lastIterationTime=$1
}else{
this.setValue(this.to);return this.checkRepeat()
}}};return false
},"setValue",function($1){
var $2=this.target;var $3=this.attribute;var $4=$1-this.currentValue;var $lzsc$315366182=$2[$3]+$4;if(!$2.__LZdeleted){
var $lzsc$928935729="$lzc$set_"+$3;if(Function["$lzsc$isa"]?Function.$lzsc$isa($2[$lzsc$928935729]):$2[$lzsc$928935729] instanceof Function){
$2[$lzsc$928935729]($lzsc$315366182)
}else{
$2[$3]=$lzsc$315366182;var $lzsc$922363367=$2["on"+$3];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$922363367):$lzsc$922363367 instanceof LzEvent){
if($lzsc$922363367.ready){
$lzsc$922363367.sendEvent($lzsc$315366182)
}}}};this.currentValue=$1
},"toString",function(){
return "Animator for "+this.target+" attribute:"+this.attribute+" to:"+this.to
}],["tagname","animator","attributes",new LzInheritedHash(LzAnimatorGroup.attributes)]);lz[LzAnimator.tagname]=LzAnimator;Class.make("LzLayout",LzNode,["vip",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"initDelegate",void 0,"locked",2,"$lzc$set_locked",function($1){
if(this.locked==$1){
return
};if($1){
this.lock()
}else{
this.unlock()
}},"subviews",null,"updateDelegate",void 0,"delegates",void 0,"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).apply(this,arguments);this.subviews=new Array();this.vip=this.immediateparent;if(this.vip.layouts==null){
this.vip.layouts=[this]
}else{
this.vip.layouts.push(this)
};this.updateDelegate=new LzDelegate(this,"update");this.delegates=[this.updateDelegate];if(this.immediateparent.isinited){
this.__parentInit()
}else{
this.initDelegate=new LzDelegate(this,"__parentInit",this.immediateparent,"oninit");this.delegates.push(this.initDelegate)
}},"__LZapplyArgs",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$1,$2);if(this.__LZdeleted){
return
};this.delegates.push(new LzDelegate(this,"gotNewSubview",this.immediateparent,"onaddsubview"));this.delegates.push(new LzDelegate(this,"removeSubview",this.immediateparent,"onremovesubview"));var $3=this.vip.subviews.length;for(var $4=0;$4<$3;$4++){
this.gotNewSubview(this.vip.subviews[$4])
}},"destroy",function(){
this.releaseLayout();(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"reset",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(this.locked){
return
};this.update($1)
},"addSubview",function($1){
if($1.getOption("layoutAfter")){
this.__LZinsertAfter($1,$1.getOption("layoutAfter"))
}else{
this.subviews.push($1)
}},"gotNewSubview",function($1){
if(!$1.getOption("ignorelayout")){
this.addSubview($1)
}},"removeSubview",function($1){
for(var $2=this.subviews.length-1;$2>=0;$2--){
if(this.subviews[$2]==$1){
this.subviews.splice($2,1);break
}};this.reset()
},"ignore",function($1){
for(var $2=this.subviews.length-1;$2>=0;$2--){
if(this.subviews[$2]==$1){
this.subviews.splice($2,1);break
}};this.reset()
},"lock",function(){
this.locked=true
},"unlock",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.locked=false;this.reset()
},"__parentInit",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(this.locked==2){
if(this.isinited){
this.unlock()
}else{
new LzDelegate(this,"unlock",this,"oninit")
}}},"releaseLayout",function(){
if(this.delegates){
for(var $1=this.delegates.length-1;$1>=0;$1--){
this.delegates[$1].unregisterAll()
}};if(this.immediateparent&&this.vip.layouts){
for(var $1=this.vip.layouts.length-1;$1>=0;$1--){
if(this.vip.layouts[$1]==this){
this.vip.layouts.splice($1,1)
}}}},"setLayoutOrder",function($1,$2){
for(var $3=this.subviews.length-1;$3>=0;$3--){
if(this.subviews[$3]===$2){
this.subviews.splice($3,1);break
}};if($3==-1){
return
};if($1=="first"){
this.subviews.unshift($2)
}else{
if($1=="last"){
this.subviews.push($2)
}else{
for(var $4=this.subviews.length-1;$4>=0;$4--){
if(this.subviews[$4]===$1){
this.subviews.splice($4+1,0,$2);break
}};if($4==-1){
this.subviews.splice($3,0,$2)
}}};this.reset();return
},"swapSubviewOrder",function($1,$2){
var $3=-1;var $4=-1;for(var $5=this.subviews.length-1;$5>=0&&($3<0||$4<0);$5--){
if(this.subviews[$5]===$1){
$3=$5
};if(this.subviews[$5]===$2){
$4=$5
}};if($3>=0&&$4>=0){
this.subviews[$4]=$1;this.subviews[$3]=$2
}else{

};this.reset();return
},"__LZinsertAfter",function($1,$2){
for(var $3=this.subviews.length-1;$3>=0;$3--){
if(this.subviews[$3]==$2){
this.subviews.splice($3,0,$1)
}}},"update",function($1){
switch(arguments.length){
case 0:
$1=null;

}},"toString",function(){
return "LzLayout for view "+this.immediateparent
}],["tagname","layout","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzLayout.tagname]=LzLayout;Class.make("LzFont",null,["style",void 0,"name",void 0,"height",void 0,"ascent",void 0,"descent",void 0,"advancetable",void 0,"lsbtable",void 0,"rsbtable",void 0,"fontobject",void 0,"$lzsc$initialize",function($1,$2,$3){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.name=$1.name;this.style=$3;this.fontobject=$1;$1[$3]=this;for(var $4 in $2){
if($4=="leading"){
continue
};this[$4]=$2[$4]
};this.height=this.ascent+this.descent;this.advancetable[13]=this.advancetable[32];this.advancetable[160]=0
},"leading",2,"toString",function(){
return "Font style "+this.style+" of name "+this.name
}],null);lz.Font=LzFont;Class.make("LzSelectionManager",LzNode,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"sel","setSelected","selectedHash",void 0,"selected",void 0,"toggle",void 0,"lastRangeStart",void 0,"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.toggle=$2.toggle==true;if($2.sel!=null){
this.sel=$2.sel
};this.selected=[];this.selectedHash={};this.lastRangeStart=null
},"select",function($1){
if(this.isSelected($1)&&(this.toggle||this.isMultiSelect($1))){
this.unselect($1)
}else{
if(this.selected.length>0&&this.isRangeSelect($1)){
var $2=this.lastRangeStart||this.selected[0];this.selectRange($2,$1)
}else{
if(!this.isMultiSelect($1)){
this.clearSelection()
};this.makeSelected($1)
}}},"isSelected",function($1){
return this.selectedHash[$1.__LZUID]==true
},"makeSelected",function($1){
if(!this.selectedHash[$1.__LZUID]){
this.selectedHash[$1.__LZUID]=true;this.selected.push($1);$1[this.sel](true)
}},"unselect",function($1){
var $2=this.selectedHash;var $3=this.selected;if($2[$1.__LZUID]){
for(var $4=$3.length-1;$4>=0;$4--){
if($3[$4]===$1){
delete $2[$1.__LZUID];$3.splice($4,1);$1[this.sel](false);return
}}}},"clearSelection",function(){
var $1=this.selected;this.selected=[];this.selectedHash={};this.lastRangeStart=null;var $2;while($2=$1.pop()){
$2[this.sel](false)
}},"getSelection",function(){
return this.selected.concat()
},"selectRange",function($1,$2){
var $3=$1.immediateparent;var $4=$3.subviews;var $5=-1;var $6=-1;for(var $7=0;$7<$4.length&&($5==-1||$6==-1);$7++){
if($4[$7]===$1){
$5=$7
};if($4[$7]===$2){
$6=$7
}};var $8=$5>$6?-1:1;this.clearSelection();this.lastRangeStart=$1;if($5!=-1&&$6!=-1){
for(var $7=$5;$7!=$6+$8;$7+=$8){
this.makeSelected($4[$7])
}}},"isMultiSelect",function($1){
return lz.Keys.isKeyDown("control")
},"isRangeSelect",function($1){
return lz.Keys.isKeyDown("shift")
},"toString",function(){
return "LzSelectionManager"
}],["tagname","selectionmanager","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzSelectionManager.tagname]=LzSelectionManager;Class.make("LzDataSelectionManager",LzSelectionManager,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"manager",void 0,"singleClone",void 0,"makeSelected",function($1){
if(this.manager==null){
this.manager=$1.cloneManager
};var $2=$1.datapath.p;if($2&&!$2.sel){
$2.sel=true;this.selected.push($2);if(this.manager==null){
this.singleClone=$1
};$1.datapath.setSelected(true)
}},"unselect",function($1){
if(this.manager==null){
this.manager=$1.cloneManager
};var $2=this.selected;var $3=$1.datapath.p;for(var $4=$2.length-1;$4>=0;$4--){
if($2[$4]===$3){
$3.sel=false;$2.splice($4,1);if($1==this.singleClone){
this.singleClone=null
};$1.datapath.setSelected(false);return
}}},"selectRange",function($1,$2){
if(this.manager==null){
this.manager=$2.cloneManager;if(this.manager==null){
return
}};var $3=this.manager.nodes;var $4=-1;var $5=-1;var $6=$2.datapath.p;for(var $7=0;$7<$3.length&&($4==-1||$5==-1);$7++){
if($3[$7]===$1){
$4=$7
};if($3[$7]===$6){
$5=$7
}};var $8=$4>$5?-1:1;this.clearSelection();this.lastRangeStart=$1;if($4!=-1&&$5!=-1){
for(var $7=$4;$7!=$5+$8;$7+=$8){
var $9=$3[$7];$9.sel=true;this.selected.push($9);this.__LZsetSelected($9,true)
}}},"getSelection",function(){
var $1=this.selected;var $2=[];for(var $3=0;$3<$1.length;$3++){
$2.push(new LzDatapointer(null,{pointer:$1[$3]}))
};return $2
},"clearSelection",function(){
var $1=this.selected;this.selected=[];this.lastRangeStart=null;var $2;while($2=$1.pop()){
$2.sel=false;this.__LZsetSelected($2,false)
}},"isSelected",function($1){
if(this.manager==null){
this.manager=$1.cloneManager
};var $2=$1.datapath.p;return $2&&$2.sel
},"__LZsetSelected",function($1,$2){
if(this.manager!=null){
var $3=this.manager.getCloneForNode($1,true);if($3){
$3.datapath.setSelected($2)
}else{

}}else{
if(!$2){
var $4=this.singleClone;if($4!=null&&$4.datapath.p===$1){
this.singleClone=null;$4.datapath.setSelected($2)
}}}}],["tagname","dataselectionmanager","attributes",new LzInheritedHash(LzSelectionManager.attributes)]);lz[LzDataSelectionManager.tagname]=LzDataSelectionManager;Class.make("LzCommand",LzNode,["active",true,"keys",null,"$lzc$set_key",function($1){
var $2=this.keys;if($2){
lz.Keys.removeKeyComboCall(this,$2)
};this.keys=$1;lz.Keys.callOnKeyCombo(this,$1)
},"destroy",function(){
var $1=this.keys;if($1){
lz.Keys.removeKeyComboCall(this,$1)
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"onselect",LzDeclaredEvent,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"setKeys",function($1){
this.$lzc$set_key($1)
},"execute",function($1){
if(this.active){
if(this.onselect.ready){
this.onselect.sendEvent($1)
}}},"keysToString",function(){
var $1="";var $2=this.keys;if($2){
var $3=LzCommand.DisplayKeys;var $4="";var $5=$2.length-1;for(var $6=0;$6<$5;$6++){
$4=$2[$6];if($4 in $3){
$4=$3[$4]
};$1=$1+$4+"+"
};$4=$2[$6];if($4 in $3){
$4=$3[$4]
};$1=$1+$4
};return $1
}],["tagname","command","attributes",new LzInheritedHash(LzNode.attributes),"DisplayKeys",{control:"Ctrl",shift:"Shift",alt:"Alt"}]);lz[LzCommand.tagname]=LzCommand;Class.make("LzState",LzNode,["$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"__LZpool",[],"__LZstatedelegates",void 0,"onapply",LzDeclaredEvent,"onremove",LzDeclaredEvent,"onapplied",LzDeclaredEvent,"applied",false,"$lzc$set_applied",function($1){
if($1){
if(this.isinited){
this.apply()
}else{
this.applyOnInit=true
}}else{
if(this.isinited){
this.remove()
}}},"isapplied",false,"$lzc$set_apply",function($1){
this.setApply($1)
},"asyncnew",false,"subh",null,"pooling",false,"$lzc$set_asyncnew",function($1){
this.__LZsetProperty($1,"asyncnew")
},"$lzc$set_pooling",function($1){
this.__LZsetProperty($1,"pooling")
},"$lzc$set___LZsourceLocation",function($1){
this.__LZsetProperty($1,"__LZsourceLocation")
},"heldArgs",void 0,"handlerMethodNames",void 0,"releasedconstraints",void 0,"appliedChildren",void 0,"applyOnInit",false,"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.heldArgs={};this.handlerMethodNames={};this.appliedChildren=[]
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(this.applyOnInit){
this.apply()
}},"createChildren",function($1){
this.subh=$1;this.__LZinstantiationDone()
},"setApply",function($1){
if(typeof $1=="function"){
this.addProperty("apply",$1);return
};this.$lzc$set_applied($1)
},"apply",function(){
if(this.applied){
return
};var $1=this.parent;this.applied=this.isapplied=true;var $2=$1._instanceAttrs;if($2){
for(var $3 in this.heldArgs){
if(LzConstraintExpr["$lzsc$isa"]?LzConstraintExpr.$lzsc$isa($2[$3]):$2[$3] instanceof LzConstraintExpr){
if(this.releasedconstraints==null){
this.releasedconstraints=[]
};var $4=$2[$3].methodName;if($1.releaseConstraintMethod($4)){
this.releasedconstraints.push($4)
}}}};var $5=$1.__LZdelegates;$1.__LZdelegates=null;$1.__LZapplyArgs(this.heldArgs);if(this.subh){
var $6=this.subh.length
};$1.__LZsetPreventInit();for(var $7=0;$7<$6;$7++){
if(this.__LZpool&&this.__LZpool[$7]){
this.appliedChildren.push(this.__LZretach(this.__LZpool[$7]))
}else{
this.appliedChildren.push($1.makeChild(this.subh[$7],this.asyncnew))
}};$1.__LZclearPreventInit();$1.__LZresolveReferences();this.__LZstatedelegates=$1.__LZdelegates;$1.__LZdelegates=$5;if(this.onapply.ready){
this.onapply.sendEvent(this)
};if(this.onapplied.ready){
this.onapplied.sendEvent(true)
}},"remove",function(){
if(!this.applied){
return
};this.applied=this.isapplied=false;if(this.onremove.ready){
this.onremove.sendEvent(this)
};if(this.onapplied.ready){
this.onapplied.sendEvent(false)
};if(this.__LZstatedelegates){
for(var $1=0;$1<this.__LZstatedelegates.length;$1++){
this.__LZstatedelegates[$1].unregisterAll()
}};if(this.pooling&&this.appliedChildren.length){
this.__LZpool=[]
};for(var $1=0;$1<this.appliedChildren.length;$1++){
var $2=this.appliedChildren[$1];if(this.pooling){
if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($2):$2 instanceof LzView){
this.__LZpool.push(this.__LZdetach($2))
}else{
$2.destroy();this.__LZpool.push(null)
}}else{
$2.destroy()
}};this.appliedChildren=[];if(this.releasedconstraints!=null){
this.releasedconstraints=null
}},"destroy",function(){
this.pooling=false;this.remove();(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"__LZapplyArgs",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};var $3={};var $4=this.heldArgs;var $5=this.handlerMethodNames;for(var $6 in $1){
var $7=$1[$6];var $8="$lzc$set_"+$6;if((Function["$lzsc$isa"]?Function.$lzsc$isa(this[$8]):this[$8] instanceof Function)||($6 in $5)){
$3[$6]=$7
}else{
$4[$6]=$7
}};for(var $6 in $3){
var $7=$3[$6];if(LzOnceExpr["$lzsc$isa"]?LzOnceExpr.$lzsc$isa($7):$7 instanceof LzOnceExpr){
var $9=$7.methodName;if($9 in $4){
$3[$9]=$4[$9];delete $4[$9]
}else{

};if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($7):$7 instanceof LzAlwaysExpr){
var $10=$7.dependenciesName;if($10 in $4){
$3[$10]=$4[$10];delete $4[$10]
}else{

}}}};var $11=null;for(var $6 in $4){
var $7=$4[$6];if(LzOnceExpr["$lzsc$isa"]?LzOnceExpr.$lzsc$isa($7):$7 instanceof LzOnceExpr){
if($11==null){
$11=[]
};$11.push($6,$7)
}};if($11!=null){
for(var $12=0,$13=$11.length;$12<$13;$12+=2){
var $6=$11[$12];var $14=$11[$12+1];var $9=$14.methodName;var $15=$9+this.__LZUID;var $16=null;if(Function["$lzsc$isa"]?Function.$lzsc$isa($4[$9]):$4[$9] instanceof Function){
$4[$15]=$4[$9];delete $4[$9]
}else{
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$9]):this[$9] instanceof Function){
$4[$15]=this[$9]
}};if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($14):$14 instanceof LzAlwaysExpr){
var $10=$14.dependenciesName;var $17=$10+this.__LZUID;if(Function["$lzsc$isa"]?Function.$lzsc$isa($4[$10]):$4[$10] instanceof Function){
$4[$17]=$4[$10];delete $4[$10]
}else{
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$10]):this[$10] instanceof Function){
$4[$17]=this[$10]
}};$4[$6]=new ($14.constructor)($15,$17,$16)
}else{
$4[$6]=new ($14.constructor)($15,$16)
}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$3)
},"$lzc$set_$delegates",function($1){
var $2=[];var $3=[];for(var $4=0;$4<$1.length;$4+=3){
if(LzState.events[$1[$4]]&&!$1[$4+2]){
var $5=$3;var $6=$1[$4+1];if(this.heldArgs[$6]){
this.addProperty($6,this.heldArgs[$6]);delete this.heldArgs[$6]
};this.handlerMethodNames[$6]=true
}else{
var $5=$2
};$5.push($1[$4],$1[$4+1],$1[$4+2])
};if($3.length){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_$delegates"]||this.nextMethod(arguments.callee,"$lzc$set_$delegates")).call(this,$3)
};if($2.length){
this.heldArgs.$delegates=$2
}},"__LZsetProperty",function($1,$2){
this[$2]=$1
},"__LZdetach",function($1){
$1.$lzc$set_visible(false);return $1
},"__LZretach",function($1){
$1.$lzc$set_visible(true);return $1
}],["tagname","state","attributes",new LzInheritedHash(LzNode.attributes),"props",{apply:true},"events",{onremove:true,onapply:true,onapplied:true}]);(function($1){
with($1){
with($1.prototype){
prototype.$isstate=true
}}})(LzState);lz[LzState.tagname]=LzState;Mixin.make("LzDataNodeMixin",null,["onownerDocument",LzDeclaredEvent,"onDocumentChange",LzDeclaredEvent,"onparentNode",LzDeclaredEvent,"onchildNode",LzDeclaredEvent,"onchildNodes",LzDeclaredEvent,"onattributes",LzDeclaredEvent,"onnodeName",LzDeclaredEvent,"nodeType",void 0,"parentNode",null,"ownerDocument",void 0,"childNodes",null,"__LZo",-1,"__LZcoDirty",true,"sel",false,"__LZuserData",null,"__LZuserHandler",null,"getParent",function(){
return this.parentNode
},"getOffset",function(){
if(!this.parentNode){
return 0
};if(this.parentNode.__LZcoDirty){
this.parentNode.__LZupdateCO()
};return this.__LZo
},"getPreviousSibling",function(){
if(!this.parentNode){
return null
};if(this.parentNode.__LZcoDirty){
this.parentNode.__LZupdateCO()
};return this.parentNode.childNodes[this.__LZo-1]
},"$lzc$getPreviousSibling_dependencies",function($1,$2){
return [this.parentNode,"childNodes",this,"parentNode"]
},"getNextSibling",function(){
if(!this.parentNode){
return null
};if(this.parentNode.__LZcoDirty){
this.parentNode.__LZupdateCO()
};return this.parentNode.childNodes[this.__LZo+1]
},"$lzc$getNextSibling_dependencies",function($1,$2){
return [this.parentNode,"childNodes",this,"parentNode"]
},"childOfNode",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};var $3=$2?this:this.parentNode;while($3){
if($3===$1){
return true
};$3=$3.parentNode
};return false
},"childOf",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};return this.childOfNode($1,$2)
},"$lzc$set_ownerDocument",function($1){
this.ownerDocument=$1;if(this.childNodes){
for(var $2=0;$2<this.childNodes.length;$2++){
this.childNodes[$2].$lzc$set_ownerDocument($1)
}};if(this.onownerDocument.ready){
this.onownerDocument.sendEvent($1)
}},"setOwnerDocument",function($1){
this.$lzc$set_ownerDocument($1)
},"cloneNode",function($1){
switch(arguments.length){
case 0:
$1=false;

};return undefined
},"serialize",function(){
return undefined
},"__LZlockFromUpdate",function($1){
this.ownerDocument.__LZdoLock($1)
},"__LZunlockFromUpdate",function($1){
this.ownerDocument.__LZdoUnlock($1)
},"setUserData",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=null;

};if(this.__LZuserData==null){
this.__LZuserData={}};var $4=this.__LZuserData[$1];if($2!=null){
this.__LZuserData[$1]=$2
}else{
if($4!=null){
delete this.__LZuserData[$1]
}};return $4!=null?$4:null
},"getUserData",function($1){
if(this.__LZuserData==null){
return null
}else{
var $2=this.__LZuserData[$1];return $2!=null?$2:null
}}],null);lz.DataNodeMixin=LzDataNodeMixin;Class.make("LzDataNode",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"toString",function(){
return "lz.DataNode"
}],["ELEMENT_NODE",1,"TEXT_NODE",3,"DOCUMENT_NODE",9,"stringToLzData",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=false;
case 2:
$3=false;

};return LzDataElement.stringToLzData($1,$2,$3)
}]);lz.DataNode=LzDataNode;Mixin.make("LzDataElementMixin",null,["__LZchangeQ",null,"__LZlocker",null,"nodeName",null,"attributes",null,"insertBefore",function($1,$2){
if($1==null){
return null
}else{
if($2==null){
return this.appendChild($1)
}else{
var $3=this.__LZgetCO($2);if($3>=0){
var $4=$1===$2;if($1.parentNode!=null){
if($1.parentNode===this){
if(!$4){
var $5=this.__LZremoveChild($1);if($5!=-1&&$5<$3){
$3-=1
}}}else{
$1.parentNode.removeChild($1)
}};if(!$4){
this.__LZcoDirty=true;this.childNodes.splice($3,0,$1)
};$1.$lzc$set_ownerDocument(this.ownerDocument);$1.parentNode=this;if($1.onparentNode.ready){
$1.onparentNode.sendEvent(this)
};if(this.onchildNodes.ready){
this.onchildNodes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("insertBefore",this,0);return $1
};return null
}}},"replaceChild",function($1,$2){
if($1==null){
return null
}else{
var $3=this.__LZgetCO($2);if($3>=0){
var $4=$1===$2;if($1.parentNode!=null){
if($1.parentNode===this){
if(!$4){
var $5=this.__LZremoveChild($1);if($5!=-1&&$5<$3){
$3-=1
}}}else{
$1.parentNode.removeChild($1)
}};if(!$4){
$1.__LZo=$3;this.childNodes[$3]=$1
};$1.$lzc$set_ownerDocument(this.ownerDocument);$1.parentNode=this;if($1.onparentNode.ready){
$1.onparentNode.sendEvent(this)
};if(this.onchildNodes.ready){
this.onchildNodes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("childNodes",this,0,$1);return $1
};return null
}},"removeChild",function($1){
var $2=this.__LZgetCO($1);if($2>=0){
this.__LZcoDirty=true;this.childNodes.splice($2,1);if(this.onchildNodes.ready){
this.onchildNodes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("removeChild",this,0,$1);return $1
};return null
},"appendChild",function($1){
if($1==null){
return null
}else{
if($1.parentNode!=null){
if($1.parentNode===this){
this.__LZremoveChild($1)
}else{
$1.parentNode.removeChild($1)
}};this.childNodes.push($1);$1.__LZo=this.childNodes.length-1;$1.$lzc$set_ownerDocument(this.ownerDocument);$1.parentNode=this;if($1.onparentNode.ready){
$1.onparentNode.sendEvent(this)
};if(this.onchildNodes.ready){
this.onchildNodes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("appendChild",this,0,$1);return $1
}},"hasChildNodes",function(){
return this.childNodes.length>0
},"cloneNode",function($1){
switch(arguments.length){
case 0:
$1=false;

};var $2=new LzDataElement(this.nodeName,this.attributes);if($1){
var $3=this.childNodes;var $4=[];for(var $5=$3.length-1;$5>=0;--$5){
$4[$5]=$3[$5].cloneNode(true)
};$2.$lzc$set_childNodes($4)
};return $2
},"getAttr",function($1){
return this.attributes[$1]
},"$lzc$getAttr_dependencies",function($1,$2){
return [$2,"attributes"]
},"setAttr",function($1,$2){
$2=String($2);this.attributes[$1]=$2;if(this.onattributes.ready){
this.onattributes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("attributes",this,1,{name:$1,value:$2,type:"set"});return $2
},"removeAttr",function($1){
var $2=this.attributes[$1];delete this.attributes[$1];if(this.onattributes.ready){
this.onattributes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("attributes",this,1,{name:$1,value:$2,type:"remove"});return $2
},"hasAttr",function($1){
return this.attributes[$1]!=null
},"$lzc$hasAttr_dependencies",function($1,$2){
return [$2,"attributes"]
},"getFirstChild",function(){
return this.childNodes[0]
},"$lzc$getFirstChild_dependencies",function($1,$2){
return [this,"childNodes"]
},"getLastChild",function(){
return this.childNodes[this.childNodes.length-1]
},"$lzc$getLastChild_dependencies",function($1,$2){
return [this,"childNodes"]
},"__LZgetCO",function($1){
if($1!=null){
var $2=this.childNodes;if(!this.__LZcoDirty){
var $3=$1.__LZo;if($2[$3]===$1){
return $3
}}else{
for(var $3=$2.length-1;$3>=0;--$3){
if($2[$3]===$1){
return $3
}}}};return -1
},"__LZremoveChild",function($1){
var $2=this.__LZgetCO($1);if($2>=0){
this.__LZcoDirty=true;this.childNodes.splice($2,1)
};return $2
},"__LZupdateCO",function(){
var $1=this.childNodes;for(var $2=0,$3=$1.length;$2<$3;$2++){
$1[$2].__LZo=$2
};this.__LZcoDirty=false
},"$lzc$set_attributes",function($1){
var $2={};for(var $3 in $1){
$2[$3]=$1[$3]
};this.attributes=$2;if(this.onattributes.ready){
this.onattributes.sendEvent($2)
};this.ownerDocument.handleDocumentChange("attributes",this,1)
},"setAttrs",function($1){
this.$lzc$set_attributes($1)
},"$lzc$set_childNodes",function($1){
if(!$1){
$1=[]
};this.childNodes=$1;if($1.length>0){
var $2=true;var $3=$1[0].parentNode;if($3!=null&&$3!==this&&$3.childNodes===$1){
$2=false;$3.$lzc$set_childNodes([])
};for(var $4=0;$4<$1.length;$4++){
var $5=$1[$4];if($5){
if($2&&$5.parentNode!=null){
if($5.parentNode!==this){
$5.parentNode.removeChild($5)
}};$5.$lzc$set_ownerDocument(this.ownerDocument);$5.parentNode=this;if($5.onparentNode.ready){
$5.onparentNode.sendEvent(this)
};$5.__LZo=$4
}}};this.__LZcoDirty=false;if(this.onchildNodes.ready){
this.onchildNodes.sendEvent($1)
};this.ownerDocument.handleDocumentChange("childNodes",this,0)
},"setChildNodes",function($1){
this.$lzc$set_childNodes($1)
},"$lzc$set_nodeName",function($1){
this.nodeName=$1;if(this.onnodeName.ready){
this.onnodeName.sendEvent($1)
};if(this.parentNode){
if(this.parentNode.onchildNodes.ready){
this.parentNode.onchildNodes.sendEvent(this)
};if(this.parentNode.onchildNode.ready){
this.parentNode.onchildNode.sendEvent(this)
}};this.ownerDocument.handleDocumentChange("childNodeName",this.parentNode,0);this.ownerDocument.handleDocumentChange("nodeName",this,1)
},"setNodeName",function($1){
this.$lzc$set_nodeName($1)
},"__LZgetText",function(){
var $1="";var $2=this.childNodes;for(var $3=0,$4=$2.length;$3<$4;$3++){
var $5=$2[$3];if($5.nodeType==LzDataElement.TEXT_NODE){
$1+=$5.data
}};return $1
},"getElementsByTagName",function($1){
var $2=[];var $3=this.childNodes;for(var $4=0,$5=$3.length;$4<$5;$4++){
if($3[$4].nodeName==$1){
$2.push($3[$4])
}};return $2
},"__LZlt","<","__LZgt",">","serialize",function(){
return this.serializeInternal()
},"serializeInternal",function($1){
switch(arguments.length){
case 0:
$1=Infinity;

};var $2=this.__LZlt+this.nodeName;var $3=this.attributes;for(var $4 in $3){
$2+=" "+$4+'="'+LzDataElement.__LZXMLescape($3[$4])+'"';if($2.length>$1){
break
}};var $5=this.childNodes;if($2.length<=$1&&$5.length){
$2+=this.__LZgt;for(var $6=0,$7=$5.length;$6<$7;$6++){
$2+=$5[$6].serialize();if($2.length>$1){
break
}};$2+=this.__LZlt+"/"+this.nodeName+this.__LZgt
}else{
$2+="/"+this.__LZgt
};return $2
},"handleDocumentChange",function($1,$2,$3,$4){
switch(arguments.length){
case 3:
$4=null;

};var $5={who:$2,what:$1,type:$3};if($4){
$5.cobj=$4
};if(this.__LZchangeQ){
this.__LZchangeQ.push($5)
}else{
if(this.onDocumentChange.ready){
this.onDocumentChange.sendEvent($5)
}}},"toString",function(){
return this.serialize()
},"__LZdoLock",function($1){
if(!this.__LZchangeQ){
this.__LZchangeQ=[];this.__LZlocker=$1
}},"__LZdoUnlock",function($1){
if(this.__LZlocker!=$1){
return
};var $2=this.__LZchangeQ;this.__LZchangeQ=null;this.__LZlocker=null;if($2!=null){
for(var $3=0,$4=$2.length;$3<$4;$3++){
var $5=true;var $6=$2[$3];for(var $7=0;$7<$3;$7++){
var $8=$2[$7];if($6.who==$8.who&&$6.what==$8.what&&$6.type==$8.type){
$5=false;break
}};if($5){
this.handleDocumentChange($6.what,$6.who,$6.type)
}}}}],null);lz.DataElementMixin=LzDataElementMixin;Class.make("LzDataElement",[LzDataElementMixin,LzDataNodeMixin,LzDataNode],["$lzsc$initialize",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.nodeName=$1;this.nodeType=LzDataElement.ELEMENT_NODE;this.ownerDocument=this;if($2){
this.$lzc$set_attributes($2)
}else{
this.attributes={}};if($3){
this.$lzc$set_childNodes($3)
}else{
this.childNodes=[];this.__LZcoDirty=false
}}],["NODE_CLONED",1,"NODE_IMPORTED",2,"NODE_DELETED",3,"NODE_RENAMED",4,"NODE_ADOPTED",5,"makeNodeList",function($1,$2){
var $3=[];for(var $4=0;$4<$1;$4++){
$3[$4]=new LzDataElement($2)
};return $3
},"valueToElement",function($1){
return new LzDataElement("element",{},LzDataElement.__LZv2E($1))
},"__LZv2E",function($1){
var $2=[];if(typeof $1=="object"){
if((LzDataElement["$lzsc$isa"]?LzDataElement.$lzsc$isa($1):$1 instanceof LzDataElement)||(LzDataText["$lzsc$isa"]?LzDataText.$lzsc$isa($1):$1 instanceof LzDataText)){
$2[0]=$1
}else{
if(Date["$lzsc$isa"]?Date.$lzsc$isa($1):$1 instanceof Date){

}else{
if(Array["$lzsc$isa"]?Array.$lzsc$isa($1):$1 instanceof Array){
var $3=$1.__LZtag!=null?$1.__LZtag:"item";for(var $4=0;$4<$1.length;$4++){
var $5=LzDataElement.__LZv2E($1[$4]);$2[$4]=new LzDataElement($3,null,$5)
}}else{
var $4=0;for(var $6 in $1){
if($6.indexOf("__LZ")==0){
continue
};$2[$4++]=new LzDataElement($6,null,LzDataElement.__LZv2E($1[$6]))
}}}}}else{
if($1!=null){
$2[0]=new LzDataText($1)
}};return $2.length!=0?$2:null
},"ELEMENT_NODE",1,"TEXT_NODE",3,"DOCUMENT_NODE",9,"__LZescapechars",{"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&apos;"},"__LZXMLescape",function($1){
if(typeof $1!="string"){
return $1
};var $2=LzDataElement.__LZescapechars;var $3=$1.length;var $4="";for(var $5=0;$5<$3;$5++){
var $6=$1.charCodeAt($5);if($6<32){
$4+="&#x"+$6.toString(16)+";"
}else{
var $7=$1.charAt($5);$4+=$2[$7]||$7
}};return $4
},"stringToLzData",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=false;
case 2:
$3=false;

};if($1!=null&&$1!=""){
var $4;try{
$4=LzXMLParser.parseXML($1,$2,$3)
}
catch(e){

};if($4!=null){
var $5=LzXMLTranslator.copyXML($4,$2,$3);return $5
}};return null
},"whitespaceChars",{" ":true,"\r":true,"\n":true,"\t":true},"trim",function($1){
var $2=LzDataElement.whitespaceChars;var $3=$1.length;var $4=0;var $5=$3-1;var $6;while($4<$3){
$6=$1.charAt($4);if($2[$6]!=true){
break
};$4++
};while($5>$4){
$6=$1.charAt($5);if($2[$6]!=true){
break
};$5--
};return $1.slice($4,$5+1)
}]);lz.DataElement=LzDataElement;Class.make("LzDataText",[LzDataNodeMixin,LzDataNode],["$lzsc$initialize",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.nodeType=LzDataElement.TEXT_NODE;this.data=$1
},"ondata",LzDeclaredEvent,"nodeName","#text","data","","$lzc$set_data",function($1){
this.data=$1;if(this.ondata.ready){
this.ondata.sendEvent($1)
};if(this.ownerDocument){
this.ownerDocument.handleDocumentChange("data",this,1)
}},"setData",function($1){
this.$lzc$set_data($1)
},"cloneNode",function($1){
switch(arguments.length){
case 0:
$1=false;

};var $2=new LzDataText(this.data);return $2
},"serialize",function(){
return LzDataElement.__LZXMLescape(this.data)
},"toString",function(){
return this.data
}],null);lz.DataText=LzDataText;Class.make("LzDataRequest",LzEventable,["requestor",null,"src",null,"timeout",Infinity,"status",null,"rawdata",null,"error",null,"onstatus",LzDeclaredEvent,"$lzsc$initialize",function($1){
switch(arguments.length){
case 0:
$1=null;

};this.requestor=$1
}],["SUCCESS","success","TIMEOUT","timeout","ERROR","error","READY","ready","LOADING","loading"]);lz.DataRequest=LzDataRequest;Class.make("LzDataProvider",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"doRequest",function($1){

},"abortLoadForRequest",function($1){

}],null);lz.DataProvider=LzDataProvider;Class.make("LzHTTPDataRequest",LzDataRequest,["method","GET","postbody",void 0,"proxied",void 0,"proxyurl",void 0,"multirequest",false,"queuerequests",false,"queryparams",null,"requestheaders",null,"getresponseheaders",false,"responseheaders",void 0,"cacheable",false,"clientcacheable",false,"trimwhitespace",false,"nsprefix",false,"serverproxyargs",null,"xmldata",null,"loadtime",0,"loadstarttime",void 0,"secure",false,"secureport",void 0,"parsexml",true,"loader",null,"$lzsc$initialize",function($1){
switch(arguments.length){
case 0:
$1=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1)
}],null);lz.HTTPDataRequest=LzHTTPDataRequest;Class.make("LzHTTPDataProvider",LzDataProvider,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"makeLoader",function($1){
var $2=$1.proxied;var $3=new LzHTTPLoader(this,$2);$1.loader=$3;$3.loadSuccess=this.loadSuccess;$3.loadError=this.loadError;$3.loadTimeout=this.loadTimeout;$3.setProxied($2);var $4=$1.secure;if($4==null){
if($1.src.substring(0,5)=="https"){
$4=true
}};$3.secure=$4;if($4){
$3.baserequest=lz.Browser.getBaseURL($4,$1.secureport);$3.secureport=$1.secureport
};return $3
},"abortLoadForRequest",function($1){
$1.loader.abort()
},"doRequest",function($1){
var $2=$1;if(!$2.src){
return
};var $3=$2.proxied;var $4=$2.loader;if($4==null||$2.multirequest==true||$2.queuerequests==true){
$4=this.makeLoader($2)
};$4.dataRequest=$2;$4.setQueueing($2.queuerequests);$4.setTimeout($2.timeout);$4.setOption("serverproxyargs",$2.serverproxyargs);$4.setOption("cacheable",$2.cacheable==true);$4.setOption("timeout",$2.timeout);$4.setOption("trimwhitespace",$2.trimwhitespace==true);$4.setOption("nsprefix",$2.nsprefix==true);$4.setOption("sendheaders",$2.getresponseheaders==true);$4.setOption("parsexml",$2.parsexml);if($2.clientcacheable!=null){
$4.setOption("ccache",$2.clientcacheable)
};var $5={};var $6=$2.requestheaders;if($6!=null){
var $7=$6.getNames();for(var $8=0;$8<$7.length;$8++){
var $9=$7[$8];var $10=$6.getValue($9);if($3){
$5[$9]=$10
}else{
$4.setRequestHeader($9,$10)
}}};var $11=$2.queryparams;var $12=true;var $13=$2.postbody;if($13==null&&$11!=null){
$13=$11.serialize("=","&",true)
}else{
$12=false
};$4.setOption("hasquerydata",$12);var $14=new LzURL($2.src);if($2.method=="GET"){
if($14.query==null){
$14.query=$13
}else{
if($13!=null){
$14.query+="&"+$13
}};$13=null
};var $15="__lzbc__="+new Date().getTime();if(!$3&&$2.method=="POST"&&($13==null||$13=="")){
$13=$15
};var $16;if($3){
$16=$4.makeProxiedURL($2.proxyurl,$14.toString(),$2.method,"xmldata",$5,$13);var $17=$16.indexOf("?");var $18=$16.substring($17+1,$16.length);var $19=$16.substring(0,$17);$16=$19+"?"+$15;$13=$18
}else{
if(!$2.clientcacheable){
if($2.method=="GET"){
if($14.query==null){
$14.query=$15
}else{
$14.query+="&"+$15
}}};$16=$14.toString()
};$2.loadstarttime=new Date().getTime();$2.status=LzDataRequest.LOADING;$4.open($3?"POST":$2.method,$16,null,null);$4.send($13)
},"loadSuccess",function($1,$2){
var $3=$1.dataRequest;$3.status=LzDataRequest.SUCCESS;$1.owner.loadResponse($3,$2)
},"loadError",function($1,$2){
var $3=$1.dataRequest;$3.status=LzDataRequest.ERROR;$1.owner.loadResponse($3,$2)
},"loadTimeout",function($1,$2){
var $3=$1.dataRequest;$3.loadtime=new Date().getTime()-$3.loadstarttime;$3.status=LzDataRequest.TIMEOUT;$3.onstatus.sendEvent($3)
},"setRequestError",function($1,$2){
$1.error=$2;$1.status=LzDataRequest.ERROR
},"loadResponse",function($1,$2){
$1.loadtime=new Date().getTime()-$1.loadstarttime;$1.rawdata=$1.loader.getResponse();if($2==null){
this.setRequestError($1,"client could not parse XML from server");$1.onstatus.sendEvent($1);return
};var $3=$1.proxied;if(!$1.parsexml){
$1.onstatus.sendEvent($1);return
}else{
if($3&&$2.childNodes[0].nodeName=="error"){
this.setRequestError($1,$2.childNodes[0].attributes["msg"]);$1.onstatus.sendEvent($1);return
}};var $4=new (lz.Param)();var $5=null;if($3){
var $6=$2.childNodes.length>1&&$2.childNodes[1].childNodes?$2.childNodes[1].childNodes:null;if($6!=null){
for(var $7=0;$7<$6.length;$7++){
var $8=$6[$7];if($8.attributes){
$4.addValue($8.attributes.name,$8.attributes.value)
}}};if($2.childNodes[0].childNodes){
$5=$2.childNodes[0].childNodes[0]
}}else{
var $6=$1.loader.getResponseHeaders();if($6){
$4.addObject($6)
};$5=$2
};$1.xmldata=$5;$1.responseheaders=$4;$1.onstatus.sendEvent($1)
}],null);lz.HTTPDataProvider=LzHTTPDataProvider;Class.make("LzDataset",[LzDataElementMixin,LzDataNodeMixin,LzNode],["rawdata",null,"dataprovider",void 0,"multirequest",false,"dataRequest",null,"dataRequestClass",LzHTTPDataRequest,"dsloadDel",null,"errorstring",void 0,"reqOnInitDel",void 0,"secureport",void 0,"proxyurl",null,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"timeout",60000,"$lzc$set_timeout",function($1){
this.timeout=$1
},"postbody",null,"$lzc$set_postbody",function($1){
this.postbody=$1
},"acceptencodings",false,"type",null,"params",null,"nsprefix",false,"getresponseheaders",false,"querytype","GET","$lzc$set_querytype",function($1){
this.querytype=$1.toUpperCase()
},"trimwhitespace",false,"cacheable",false,"clientcacheable",false,"querystring",null,"src",null,"$lzc$set_src",function($1){
this.src=$1;if(this.autorequest){
this.doRequest()
}},"autorequest",false,"$lzc$set_autorequest",function($1){
this.autorequest=$1
},"request",false,"$lzc$set_request",function($1){
this.request=$1;if($1&&!this.isinited){
this.reqOnInitDel=new LzDelegate(this,"doRequest",this,"oninit")
}},"headers",null,"proxied",null,"$lzc$set_proxied",function($1){
var $2={"true":true,"false":false,"null":null,inherit:null}[$1];if($2!==void 0){
this.proxied=$2
}else{

}},"isProxied",function(){
return this.proxied!=null?this.proxied:canvas.proxied
},"responseheaders",null,"queuerequests",false,"oncanvas",void 0,"$lzc$set_initialdata",function($1){
if($1!=null){
var $2=LzDataElement.stringToLzData($1,this.trimwhitespace,this.nsprefix);if($2!=null){
this.$lzc$set_data($2.childNodes)
}}},"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"construct",function($1,$2){
this.nodeType=LzDataElement.DOCUMENT_NODE;this.ownerDocument=this;this.attributes={};this.childNodes=[];this.queuerequests=false;this.oncanvas=$1==canvas||$1==null;if(!("proxyurl" in $2)){
this.proxyurl=canvas.getProxyURL()
};if(("timeout" in $2)&&$2.timeout){
this.timeout=$2.timeout
}else{
this.timeout=canvas.dataloadtimeout
};if(("dataprovider" in $2)&&$2.dataprovider){
this.dataprovider=$2.dataprovider
}else{
this.dataprovider=canvas.defaultdataprovider
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2)
},"$lzc$set_name",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_name"]||this.nextMethod(arguments.callee,"$lzc$set_name")).call(this,$1);if($1!=null){
this.nodeName=$1;if(this.oncanvas){
canvas[$1]=this
}else{
$1=this.parent.getUID()+"."+$1
};canvas.datasets[$1]=this
}},"destroy",function(){
this.$lzc$set_childNodes([]);this.dataRequest=null;if(this.dsloadDel){
this.dsloadDel.unregisterAll()
};var $1=this.name;if(this.oncanvas){
if(canvas[$1]===this){
delete canvas[$1]
}}else{
$1=this.parent.getUID()+"."+$1
};if(canvas.datasets[$1]===this){
delete canvas.datasets[$1]
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"getErrorString",function(){
return this.errorstring
},"getLoadTime",function(){
var $1=this.dataRequest;return $1?$1.loadtime:0
},"getSrc",function(){
return this.src
},"getQueryString",function(){
if(typeof this.querystring=="undefined"){
return ""
}else{
return this.querystring
}},"getParams",function(){
if(this.params==null){
this.params=new (lz.Param)()
};return this.params
},"$lzc$set_data",function($1){
if($1==null){
return
}else{
if($1 instanceof Array){
this.$lzc$set_childNodes($1)
}else{
this.$lzc$set_childNodes([$1])
}};this.data=$1;if(this.ondata.ready){
this.ondata.sendEvent(this)
}},"gotError",function($1){
this.errorstring=$1;if(this.onerror.ready){
this.onerror.sendEvent(this)
}},"gotTimeout",function(){
if(this.ontimeout.ready){
this.ontimeout.sendEvent(this)
}},"getContext",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this
},"getDataset",function(){
return this
},"getPointer",function(){
var $1=new LzDatapointer(null);$1.p=this.getContext();return $1
},"setQueryString",function($1){
this.params=null;if(typeof $1=="object"){
if($1 instanceof lz.Param){
this.querystring=$1.toString()
}else{
var $2=new (lz.Param)();for(var $3 in $1){
$2.setValue($3,$1[$3],true)
};this.querystring=$2.toString();$2.destroy()
}}else{
this.querystring=$1
};if(this.autorequest){
this.doRequest()
}},"setQueryParam",function($1,$2){
this.querystring=null;if(!this.params){
this.params=new (lz.Param)()
};this.params.setValue($1,$2);if(this.autorequest){
this.doRequest()
}},"setQueryParams",function($1){
this.querystring=null;if(!this.params){
this.params=new (lz.Param)()
};if($1){
this.params.addObject($1)
}else{
if($1==null){
this.params.remove()
}};if($1&&this.autorequest){
this.doRequest()
}},"setSrc",function($1){
this.$lzc$set_src($1)
},"setProxyRequests",function($1){
this.$lzc$set_proxied($1)
},"setRequest",function($1){
this.$lzc$set_request($1)
},"setAutorequest",function($1){
this.$lzc$set_autorequest($1)
},"setPostBody",function($1){
this.$lzc$set_postbody($1)
},"setQueryType",function($1){
this.$lzc$set_querytype($1)
},"setInitialData",function($1){
this.$lzc$set_initialdata($1)
},"abort",function(){
var $1=this.dataRequest;if($1){
this.dataprovider.abortLoadForRequest($1)
}},"doRequest",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(!this.src){
return
};if(this.multirequest||this.dataRequest==null||this.queuerequests){
this.dataRequest=new (this.dataRequestClass)(this)
};var $2=this.dataRequest;$2.src=this.src;$2.timeout=this.timeout;$2.status=LzDataRequest.READY;$2.method=this.querytype;$2.postbody=null;if(this.querystring){
$2.queryparams=new (lz.Param)();$2.queryparams.addObject(lz.Param.parseQueryString(this.querystring))
}else{
$2.queryparams=this.params
};if(this.querytype.toUpperCase()=="POST"){
$2.postbody=this.postbody;if($2.queryparams){
var $3=$2.queryparams.getValue("lzpostbody");if($3!=null){
$2.queryparams.remove("lzpostbody");$2.postbody=$3
}}};$2.proxied=this.isProxied();$2.proxyurl=this.proxyurl;$2.queuerequests=this.queuerequests;$2.requestheaders=this.headers;$2.getresponseheaders=this.getresponseheaders;$2.secureport=this.secureport;$2.cacheable=this.cacheable;$2.clientcacheable=this.clientcacheable;$2.trimwhitespace=this.trimwhitespace;$2.nsprefix=this.nsprefix;if(this.dsloadDel==null){
this.dsloadDel=new LzDelegate(this,"handleDataResponse",$2,"onstatus")
}else{
this.dsloadDel.register($2,"onstatus")
};this.dataprovider.doRequest($2)
},"handleDataResponse",function($1){
if(this.dsloadDel!=null){
this.dsloadDel.unregisterFrom($1.onstatus)
};this.rawdata=$1.rawdata;this.errorstring=null;if($1.status==LzDataRequest.SUCCESS){
if(this.responseheaders!=null){
this.responseheaders.destroy()
};this.responseheaders=$1.responseheaders;this.$lzc$set_data($1.xmldata)
}else{
if($1.status==LzDataRequest.ERROR){
this.gotError($1.error)
}else{
if($1.status==LzDataRequest.TIMEOUT){
this.gotTimeout()
}}}},"setHeader",function($1,$2){
if(!this.headers){
this.headers=new (lz.Param)()
};this.headers.setValue($1,$2)
},"getRequestHeaderParams",function(){
return this.headers
},"clearRequestHeaderParams",function(){
if(this.headers){
this.headers.remove()
}},"getResponseHeader",function($1){
var $2=this.responseheaders;if($2){
var $3=$2.getValues($1);if($3&&$3.length==1){
return $3[0]
}else{
return $3
}};return void 0
},"getAllResponseHeaders",function(){
return this.responseheaders
},"toString",function(){
return "LzDataset "+":"+this.name
}],["tagname","dataset","attributes",new LzInheritedHash(LzNode.attributes),"slashPat","/","queryStringToTable",function($1){
var $2={};var $3=$1.split("&");for(var $4=0;$4<$3.length;++$4){
var $5=$3[$4];var $6="";var $7=$5.indexOf("=");if($7>0){
$6=unescape($5.substring($7+1));$5=$5.substring(0,$7)
};if($5 in $2){
var $8=$2[$5];if($8 instanceof Array){
$8.push($6)
}else{
$2[$5]=[$8,$6]
}}else{
$2[$5]=$6
}};return $2
}]);(function($1){
with($1){
with($1.prototype){
LzDataset.attributes.name="localdata"
}}})(LzDataset);lz[LzDataset.tagname]=LzDataset;Class.make("__LzHttpDatasetPoolClass",null,["_uid",0,"_p",[],"get",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};var $5;if(this._p.length>0){
$5=this._p.pop()
}else{
$5=new LzDataset(null,{name:"LzHttpDatasetPool"+this._uid,type:"http",acceptencodings:$4});this._uid++
};if($1!=null){
$1.register($5,"ondata")
};if($2!=null){
$2.register($5,"onerror")
};if($3!=null){
$3.register($5,"ontimeout")
};return $5
},"recycle",function($1){
$1.setQueryParams(null);$1.$lzc$set_postbody(null);$1.clearRequestHeaderParams();$1.ondata.clearDelegates();$1.ontimeout.clearDelegates();$1.onerror.clearDelegates();$1.$lzc$set_data([]);this._p.push($1)
}],null);var LzHttpDatasetPool=new __LzHttpDatasetPoolClass();Class.make("LzDatapointer",LzNode,["$lzc$set_xpath",function($1){
this.setXPath($1)
},"$lzc$set_context",function($1){
this.setDataContext($1)
},"$lzc$set_pointer",function($1){
this.setPointer($1)
},"$lzc$set_p",function($1){
this.setPointer($1)
},"p",null,"context",null,"__LZtracking",null,"__LZtrackDel",null,"xpath",null,"parsedPath",null,"__LZlastdotdot",null,"__LZspecialDotDot",false,"__LZdotdotCheckDel",null,"errorDel",null,"timeoutDel",null,"rerunxpath",false,"onp",LzDeclaredEvent,"onDocumentChange",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"onrerunxpath",LzDeclaredEvent,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"gotError",function($1){
if(this.onerror.ready){
this.onerror.sendEvent($1)
}},"gotTimeout",function($1){
if(this.ontimeout.ready){
this.ontimeout.sendEvent($1)
}},"xpathQuery",function($1){
var $2=this.parsePath($1);var $3=$2.getContext(this);var $4=this.__LZgetNodes($2,$3?$3:this.p);if($4==null){
return null
};if($2.aggOperator!=null){
if($2.aggOperator=="last"){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($4):$4 instanceof Array){
return $4.length
}else{
if(!$3&&$4===this.p){
if($2.selectors&&$2.selectors.length>0){
var $5=$2.selectors;var $6=0;while($5[$6]=="."&&$6<$5.length){
++$6
};return $6!=$5.length?1:this.__LZgetLast()
}else{
return this.__LZgetLast()
}}else{
return 1
}}}else{
if($2.aggOperator=="position"){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($4):$4 instanceof Array){
var $7=[];for(var $6=0;$6<$4.length;$6++){
$7.push($6+1)
};return $7
}else{
if(!$3&&$4===this.p){
if($2.selectors&&$2.selectors.length>0){
var $5=$2.selectors;var $6=0;while($5[$6]=="."&&$6<$5.length){
++$6
};return $6!=$5.length?1:this.__LZgetPosition()
}else{
return this.__LZgetPosition()
}}else{
return 1
}}}}}else{
if($2.operator!=null){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($4):$4 instanceof Array){
var $8=[];for(var $6=0;$6<$4.length;$6++){
$8.push(this.__LZprocessOperator($4[$6],$2))
};return $8
}else{
return this.__LZprocessOperator($4,$2)
}}else{
return $4
}}},"$lzc$xpathQuery_dependencies",function($1,$2,$3){
if(this["parsePath"]){
var $4=this.parsePath($3);return [$4.hasDotDot?$2.context.getContext().ownerDocument:$2,"DocumentChange"]
}else{
return [$2,"DocumentChange"]
}},"setPointer",function($1){
this.setXPath(null);if($1!=null){
this.setDataContext($1.ownerDocument)
}else{
this.__LZsetTracking(null)
};var $2=this.data!=$1;var $3=this.p!=$1;this.p=$1;this.data=$1;this.__LZsendUpdate($2,$3);return $1!=null
},"getDataset",function(){
if(this.p==null){
if(this.context===this){
return null
}else{
return this.context.getDataset()
}}else{
return this.p.ownerDocument
}},"setXPath",function($1){
if(!$1){
this.xpath=null;this.parsedPath=null;if(this.p){
this.__LZsetTracking(this.p.ownerDocument)
};return false
};this.xpath=$1;this.parsedPath=this.parsePath($1);var $2=this.parsedPath.getContext(this);if(this.rerunxpath&&this.parsedPath.hasDotDot&&!$2){
this.__LZspecialDotDot=true
}else{
if(this.__LZdotdotCheckDel){
this.__LZdotdotCheckDel.unregisterAll()
}};this.setDataContext($2);return this.runXPath()
},"runXPath",function(){
if(!this.parsedPath){
return false
};var $1=null;if(this.context&&((LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa(this.context):this.context instanceof LzDatapointer)||(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa(this.context):this.context instanceof LzDataset)||(AnonDatasetGenerator["$lzsc$isa"]?AnonDatasetGenerator.$lzsc$isa(this.context):this.context instanceof AnonDatasetGenerator))){
$1=this.context.getContext()
};if($1){
var $2=this.__LZgetNodes(this.parsedPath,$1,0)
}else{
var $2=null
};if($2==null){
this.__LZHandleNoNodes();return false
}else{
if(Array["$lzsc$isa"]?Array.$lzsc$isa($2):$2 instanceof Array){
this.__LZHandleMultiNodes($2);return false
}else{
this.__LZHandleSingleNode($2);return true
}}},"__LZsetupDotDot",function($1){
if(this.__LZlastdotdot!=$1.ownerDocument){
if(this.__LZdotdotCheckDel==null){
this.__LZdotdotCheckDel=new LzDelegate(this,"__LZcheckDotDot")
}else{
this.__LZdotdotCheckDel.unregisterAll()
};this.__LZlastdotdot=$1.ownerDocument;this.__LZdotdotCheckDel.register(this.__LZlastdotdot,"onDocumentChange")
}},"__LZHandleSingleNode",function($1){
if(this.__LZspecialDotDot){
this.__LZsetupDotDot($1)
};this.__LZupdateLocked=true;this.__LZpchanged=$1!=this.p;this.p=$1;this.__LZsetData();this.__LZupdateLocked=false;this.__LZsendUpdate()
},"__LZHandleNoNodes",function(){
var $1=this.p!=null;var $2=this.data!=null;this.p=null;this.data=null;this.__LZsendUpdate($2,$1)
},"__LZHandleMultiNodes",function($1){
this.__LZHandleNoNodes();return null
},"__LZsetData",function(){
if(this.parsedPath&&this.parsedPath.aggOperator!=null){
if(this.parsedPath.aggOperator=="last"){
this.data=this.__LZgetLast();this.__LZsendUpdate(true)
}else{
if(this.parsedPath.aggOperator=="position"){
this.data=this.__LZgetPosition();this.__LZsendUpdate(true)
}}}else{
if(this.parsedPath&&this.parsedPath.operator!=null){
this.__LZsimpleOperatorUpdate()
}else{
if(this.data!=this.p){
this.data=this.p;this.__LZsendUpdate(true)
}}}},"__LZgetLast",function(){
var $1=this.context;if($1==null||$1===this||!(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($1):$1 instanceof LzDatapointer)){
return 1
}else{
return $1.__LZgetLast()||1
}},"__LZgetPosition",function(){
var $1=this.context;if($1==null||$1===this||!(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($1):$1 instanceof LzDatapointer)){
return 1
}else{
return $1.__LZgetPosition()||1
}},"__LZupdateLocked",false,"__LZpchanged",false,"__LZdchanged",false,"__LZsendUpdate",function($1,$2){
switch(arguments.length){
case 0:
$1=false;
case 1:
$2=false;

};this.__LZdchanged=$1||this.__LZdchanged;this.__LZpchanged=$2||this.__LZpchanged;if(this.__LZupdateLocked){
return false
};if(this.__LZdchanged){
if(this.ondata.ready){
this.ondata.sendEvent(this.data)
};this.__LZdchanged=false
};if(this.__LZpchanged){
if(this.onp.ready){
this.onp.sendEvent(this.p)
};this.__LZpchanged=false;if(this.onDocumentChange.ready){
this.onDocumentChange.sendEvent({who:this.p,type:2,what:"context"})
}};return true
},"isValid",function(){
return this.p!=null
},"__LZsimpleOperatorUpdate",function(){
var $1=this.p!=null?this.__LZprocessOperator(this.p,this.parsedPath):void 0;var $2=false;if(this.data!=$1||this.parsedPath.operator=="attributes"){
this.data=$1;$2=true
};this.__LZsendUpdate($2)
},"parsePath",function($1){
if(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa($1):$1 instanceof LzDatapath){
var $2=$1.xpath
}else{
var $2=$1
};var $3=LzDatapointer.ppcache;var $4=$3[$2];if($4==null){
$4=new LzParsedPath($2,this);$3[$2]=$4
};return $4
},"getLocalDataContext",function($1){
var $2=this.parent;if($1){
var $3=$1;for(var $4=0;$4<$3.length&&$2!=null;$4++){
$2=$2[$3[$4]]
};if($2!=null&&!(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($2):$2 instanceof LzDataset)&&(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($2["localdata"]):$2["localdata"] instanceof LzDataset)){
$2=$2["localdata"];return $2
}};if($2!=null&&(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($2):$2 instanceof LzDataset)){
return $2
}else{
return null
}},"__LZgetNodes",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=0;

};if($2==null){
return null
};if($1.selectors!=null){
var $4=$1.selectors.length;for(var $5=$3;$5<$4;$5++){
var $6=$1.selectors[$5];var $7=LzDatapointer.pathSymbols[$6]||0;var $8=$1.selectors[$5+1];if($8&&!(String["$lzsc$isa"]?String.$lzsc$isa($8):$8 instanceof String)&&$8["pred"]=="range"){
var $9=$1.selectors[++$5]
}else{
var $9=null
};var $10=null;if((Object["$lzsc$isa"]?Object.$lzsc$isa($6):$6 instanceof Object)&&("pred" in $6)&&null!=$6.pred){
if($6.pred=="hasattr"){
$2=$2.hasAttr($6.attr)?$2:null
}else{
if($6.pred=="attrval"){
if($2.attributes!=null){
$2=$2.attributes[$6.attr]==$6.val?$2:null
}else{
$2=null
}}}}else{
if($7==0){
$10=this.nodeByName($6,$9,$2)
}else{
if($7==1){
$2=$2.ownerDocument
}else{
if($7==2){
$2=$2.parentNode
}else{
if($7==3){
$10=[];if($2.childNodes){
var $11=$2.childNodes;var $12=$11.length;var $13=$9!=null?$9[0]:0;var $14=$9!=null?$9[1]:$12;var $15=0;for(var $16=0;$16<$12;$16++){
var $17=$11[$16];if($17.nodeType==LzDataElement.ELEMENT_NODE){
$15++;if($15>=$13){
$10.push($17)
};if($15==$14){
break
}}}}}}}}};if($10!=null){
if($10.length>1){
if($5==$4-1){
return $10
};var $18=[];for(var $16=0;$16<$10.length;$16++){
var $19=this.__LZgetNodes($1,$10[$16],$5+1);if($19!=null){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($19):$19 instanceof Array){
for(var $20=0;$20<$19.length;$20++){
$18.push($19[$20])
}}else{
$18.push($19)
}}};if($18.length==0){
return null
}else{
if($18.length==1){
return $18[0]
}else{
return $18
}}}else{
$2=$10[0]
}};if($2==null){
return null
}}};return $2
},"getContext",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.p
},"nodeByName",function($1,$2,$3){
if(!$3){
$3=this.p;if(!this.p){
return null
}};var $4=[];if($3.childNodes!=null){
var $5=$3.childNodes;var $6=$5.length;var $7=$2!=null?$2[0]:0;var $8=$2!=null?$2[1]:$6;var $9=0;for(var $10=0;$10<$6;$10++){
var $11=$5[$10];if($11.nodeName==$1){
$9++;if($9>=$7){
$4.push($11)
};if($9==$8){
break
}}}};return $4
},"$lzc$set_rerunxpath",function($1){
this.rerunxpath=$1;if(this.onrerunxpath.ready){
this.onrerunxpath.sendEvent($1)
}},"dupePointer",function(){
var $1=new LzDatapointer();$1.setFromPointer(this);return $1
},"__LZdoSelect",function($1,$2){
var $3=this.p;for(;$3!=null&&$2>0;$2--){
if($3.nodeType==LzDataNode.TEXT_NODE){
if($1=="getFirstChild"){
break
}};$3=$3[$1]()
};if($3!=null&&$2==0){
this.setPointer($3);return true
};return false
},"selectNext",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.__LZdoSelect("getNextSibling",$1?$1:1)
},"selectPrev",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.__LZdoSelect("getPreviousSibling",$1?$1:1)
},"selectChild",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.__LZdoSelect("getFirstChild",$1?$1:1)
},"selectParent",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.__LZdoSelect("getParent",$1?$1:1)
},"selectNextParent",function(){
var $1=this.p;if(this.selectParent()&&this.selectNext()){
return true
}else{
this.setPointer($1);return false
}},"getNodeOffset",function(){
return this.getXPathIndex()
},"getXPathIndex",function(){
if(!this.p){
return 0
};return this.p.getOffset()+1
},"getNodeType",function(){
if(!this.p){
return
};return this.p.nodeType
},"getNodeName",function(){
if(!this.p){
return null
};return this.p.nodeName
},"setNodeName",function($1){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.$lzc$set_nodeName($1)
}},"getNodeAttributes",function(){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.attributes
};return null
},"getNodeAttribute",function($1){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.attributes[$1]
};return null
},"setNodeAttribute",function($1,$2){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.setAttr($1,$2)
}},"deleteNodeAttribute",function($1){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.removeAttr($1)
}},"getNodeText",function(){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.__LZgetText()
};return null
},"setNodeText",function($1){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
var $2=false;var $3=this.p.childNodes;for(var $4=0;$4<$3.length;$4++){
var $5=$3[$4];if($5.nodeType==LzDataElement.TEXT_NODE){
$5.$lzc$set_data($1);$2=true;break
}};if(!$2){
this.p.appendChild(new LzDataText($1))
}}},"getNodeCount",function(){
if(!this.p||this.p.nodeType==LzDataElement.TEXT_NODE){
return 0
};return this.p.childNodes.length||0
},"addNode",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;

};if(!this.p){
return null
};var $4=new LzDataElement($1,$3);if($2!=null){
$4.appendChild(new LzDataText($2))
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.appendChild($4)
};return $4
},"deleteNode",function(){
if(!this.p){
return
};var $1=this.p;if(!this.rerunxpath){
if(!(this.selectNext()||this.selectPrev())){
this.__LZHandleNoNodes()
}};$1.parentNode.removeChild($1);return $1
},"sendDataChange",function($1){
this.getDataset().sendDataChange($1)
},"comparePointer",function($1){
return this.p==$1.p
},"addNodeFromPointer",function($1){
if(!$1.p){
return null
};if(!this.p){
return null
};var $2=$1.p.cloneNode(true);if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.appendChild($2)
};return new LzDatapointer(null,{pointer:$2})
},"setFromPointer",function($1){
this.setPointer($1.p)
},"__LZprocessOperator",function($1,$2){
if($1==null){
return
};var $3=$2.operator;switch($3){
case "serialize":
return $1.serialize();
case "text":
return $1.nodeType!=LzDataElement.TEXT_NODE?$1.__LZgetText():void 0;
case "name":
return $1.nodeName;
default:
if($2.hasAttrOper){
if($1.nodeType!=LzDataElement.TEXT_NODE&&$1["attributes"]){
if($3=="attributes"){
return $1.attributes
}else{
return $1.attributes[$3.substr(11)]
}}else{
return
}}else{

};

}},"makeRootNode",function(){
return new LzDataElement("root")
},"finishRootNode",function($1){
return $1.childNodes[0]
},"makeElementNode",function($1,$2,$3){
var $4=new LzDataElement($2,$1);$3.appendChild($4);return $4
},"makeTextNode",function($1,$2){
var $3=new LzDataText($1);$2.appendChild($3);return $3
},"serialize",function(){
if(this.p==null){
return null
};return this.p.serialize()
},"setDataContext",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};if($1==null){
this.context=this;if(this.p){
this.__LZsetTracking(this.p.ownerDocument)
}}else{
if(this.context!=$1){
this.context=$1;if(this.errorDel!=null){
this.errorDel.unregisterAll();this.timeoutDel.unregisterAll()
};this.__LZsetTracking($1);var $3=this.xpath!=null;if($3){
if(this.errorDel==null){
this.errorDel=new LzDelegate(this,"gotError");this.timeoutDel=new LzDelegate(this,"gotTimeout")
};this.errorDel.register($1,"onerror");this.timeoutDel.register($1,"ontimeout")
}}}},"__LZcheckChange",function($1){
if(!this.rerunxpath){
if(!this.p||$1.who==this.context){
this.runXPath()
}else{
if(this.__LZneedsOpUpdate($1)){
this.__LZsimpleOperatorUpdate()
}};return false
}else{
if($1.type==2||($1.type==0||$1.type==1&&this.parsedPath&&this.parsedPath.hasOpSelector)&&(this.parsedPath&&this.parsedPath.hasDotDot||this.p==null||this.p.childOfNode($1.who))){
this.runXPath();return true
}else{
if(this.__LZneedsOpUpdate($1)){
this.__LZsimpleOperatorUpdate()
}};return false
}},"__LZneedsOpUpdate",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=this.parsedPath;if($2!=null&&$2.operator!=null){
var $3=$1.who;var $4=$1.type;if($2.operator!="text"){
return $4==1&&$3==this.p
}else{
return $4==0&&$3==this.p||$3.parentNode==this.p&&$3.nodeType==LzDataElement.TEXT_NODE
}}else{
return false
}},"__LZcheckDotDot",function($1){
var $2=$1.who;var $3=$1.type;if(($3==0||$3==1&&this.parsedPath.hasOpSelector)&&this.context.getContext().childOfNode($2)){
this.runXPath()
}},"destroy",function(){
this.__LZsetTracking(null);if(this.errorDel){
this.errorDel.unregisterAll()
};if(this.timeoutDel){
this.timeoutDel.unregisterAll()
};if(this.__LZdotdotCheckDel){
this.__LZdotdotCheckDel.unregisterAll()
};this.p=null;this.data=null;this.__LZlastdotdot=null;this.context=null;this.__LZtracking=null;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).apply(this,arguments)
},"__LZsetTracking",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=false;
case 2:
$3=false;

};var $4=this.__LZtracking;var $5=this.__LZtrackDel;if($1){
if($4!=null&&$4.length==1&&$4[0]===$1){
return
};if($5){
$5.unregisterAll()
};var $6=$2||this.xpath;if($6){
if(!$5){
this.__LZtrackDel=$5=new LzDelegate(this,"__LZcheckChange")
};this.__LZtracking=$4=[$1];$5.register($1,"onDocumentChange")
}}else{
this.__LZtracking=[];if($5){
this.__LZtrackDel.unregisterAll()
}}}],["tagname","datapointer","attributes",{ignoreplacement:true},"ppcache",{},"pathSymbols",{"/":1,"..":2,"*":3,".":4}]);lz[LzDatapointer.tagname]=LzDatapointer;Class.make("LzParam",LzEventable,["d",null,"delimiter","&","$lzc$set_delimiter",function($1){
this.setDelimiter($1)
},"separator","=","$lzc$set_separator",function($1){
this.setSeparator($1)
},"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.d={}},"parseQueryString",function($1){
return lz.Param.parseQueryString($1)
},"addObject",function($1){
for(var $2 in $1){
this.setValue($2,$1[$2])
}},"clone",function($1){
switch(arguments.length){
case 0:
$1=null;

};var $2=new (lz.Param)();for(var $3 in this.d){
$2.d[$3]=this.d[$3].concat()
};return $2
},"remove",function($1){
switch(arguments.length){
case 0:
$1=null;

};if($1==null){
this.d={}}else{
var $2=this.d[$1];if($2!=null){
$2.shift();if(!$2.length){
delete this.d[$1]
}}}},"setValue",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=false;

};if($3){
$2=encodeURIComponent($2)
};this.d[$1]=[$2]
},"addValue",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=false;

};if($3){
$2=encodeURIComponent($2)
};var $4=this.d[$1];if($4==null){
this.d[$1]=[$2]
}else{
$4.push($2)
}},"getValue",function($1){
var $2=this.d[$1];if($2!=null){
return $2[0]
};return null
},"getValues",function($1){
var $2=this.d[$1];if($2!=null){
return $2.concat()
};return null
},"getValueNoCase",function($1){
var $2=this.getValues($1);return $2!=null&&$2.length==1?$2[0]:$2
},"getNames",function(){
var $1=[];for(var $2 in this.d){
$1.push($2)
};return $1
},"setDelimiter",function($1){
var $2=this.delimiter;this.delimiter=$1;return $2
},"setSeparator",function($1){
var $2=this.separator;this.separator=$1;return $2
},"toString",function(){
return this.serialize()
},"serialize",function($1,$2,$3){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=false;

};var $1=$1==null?this.separator:$1;var $4=$2==null?this.delimiter:$2;var $5="";var $6=false;for(var $7 in this.d){
var $8=this.d[$7];if($8!=null){
for(var $9=0;$9<$8.length;++$9){
if($6){
$5+=$4
};$5+=$7+$1+($3?encodeURIComponent($8[$9]):$8[$9]);$6=true
}}};return $5
}],["parseQueryString",function($1){
var $2=$1.split("&");var $3={};for(var $4=0;$4<$2.length;++$4){
var $5=$2[$4];var $6="";var $7=$5.indexOf("=");if($7>0){
$6=unescape($5.substring($7+1));$5=$5.substring(0,$7)
};$3[$5]=$6
};return $3
}]);lz.Param=LzParam;Class.make("LzParsedPath",null,["path",null,"selectors",null,"context",null,"dsetname",null,"dsrcname",null,"islocaldata",null,"operator",null,"aggOperator",null,"hasAttrOper",false,"hasOpSelector",false,"hasDotDot",false,"getContext",function($1){
if(this.context!=null){
return this.context
}else{
if(this.islocaldata!=null){
return $1.getLocalDataContext(this.islocaldata)
}else{
if(this.dsrcname!=null){
return canvas[this.dsrcname][this.dsetname]
}else{
if(this.dsetname!=null){
return canvas.datasets[this.dsetname]
}}}};return null
},"$lzsc$initialize",function($1,$2){
this.path=$1;this.selectors=[];var $3=$1.indexOf(":/");if($3>-1){
var $4=$1.substring(0,$3).split(":");if($4.length>1){
var $5=LzParsedPath.trim($4[0]);var $6=LzParsedPath.trim($4[1]);if($5=="local"){
this.islocaldata=$6.split(".")
}else{
this.dsrcname=$5;this.dsetname=$6
}}else{
var $7=LzParsedPath.trim($4[0]);if($7=="new"){
this.context=new AnonDatasetGenerator(this)
}else{
this.dsetname=$7
}};var $8=$1.substring($3+2)
}else{
var $8=$1
};var $9=[];var $10="";var $11=false;var $12=false;for(var $13=0;$13<$8.length;$13++){
var $14=$8.charAt($13);if($14=="\\"&&$12==false){
$12=true;continue
}else{
if($12==true){
$12=false;$10+=$14;continue
}else{
if($11==false&&$14=="/"){
$9.push($10);$10="";continue
}else{
if($14=="'"){
$11=$11?false:true
}}}};$10+=$14
};$9.push($10);if($9!=null){
for(var $13=0;$13<$9.length;$13++){
var $15=LzParsedPath.trim($9[$13]);if($13==$9.length-1){
if($15.charAt(0)=="@"){
this.hasAttrOper=true;if($15.charAt(1)=="*"){
this.operator="attributes"
}else{
this.operator="attributes."+$15.substring(1,$15.length)
};continue
}else{
if($15.charAt($15.length-1)==")"){
if($15.indexOf("last")>-1){
this.aggOperator="last"
}else{
if($15.indexOf("position")>-1){
this.aggOperator="position"
}else{
if($15.indexOf("name")>-1){
this.operator="name"
}else{
if($15.indexOf("text")>-1){
this.operator="text"
}else{
if($15.indexOf("serialize")>-1){
this.operator="serialize"
}else{

}}}}};continue
}else{
if($15==""){
continue
}}}};var $16=$15.split("[");var $17=LzParsedPath.trim($16[0]);this.selectors.push($17==""?"/":$17);if($17==""||$17==".."){
this.hasDotDot=true
};if($16!=null){
for(var $18=1;$18<$16.length;$18++){
var $19=LzParsedPath.trim($16[$18]);$19=$19.substring(0,$19.length-1);if(LzParsedPath.trim($19).charAt(0)=="@"){
var $20=$19.split("=");var $21;var $22=$20.shift().substring(1);if($20.length>0){
var $23=LzParsedPath.trim($20.join("="));$23=$23.substring(1,$23.length-1);$21={pred:"attrval",attr:LzParsedPath.trim($22),val:LzParsedPath.trim($23)}}else{
$21={pred:"hasattr",attr:LzParsedPath.trim($22)}};this.selectors.push($21);this.hasOpSelector=true
}else{
var $21=$19.split("-");$21[0]=LzParsedPath.trim($21[0]);if($21[0]==""){
$21[0]=1
};if($21[1]!=null){
$21[1]=LzParsedPath.trim($21[1])
};if($21[1]==""){
$21[1]=Infinity
}else{
if($21.length==1){
$21[1]=$21[0]
}};$21.pred="range";this.selectors.push($21)
}}}}}},"toString",function(){
return "Parsed path for path: "+this.path
},"debugWrite",function(){

}],["trim",function($1){
var $2=0;var $3=false;while($1.charAt($2)==" "){
$2++;$3=true
};var $4=$1.length-$2;while($1.charAt($2+$4-1)==" "){
$4--;$3=true
};return $3?$1.substr($2,$4):$1
}]);Class.make("AnonDatasetGenerator",LzEventable,["pp",null,"__LZdepChildren",null,"onDocumentChange",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"noncontext",true,"$lzsc$initialize",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.pp=$1
},"getContext",function(){
var $1=new LzDataset(null,{name:null});var $2=this.pp.selectors;if($2!=null){
var $3=$1.getPointer();for(var $4=0;$4<$2.length;$4++){
if($2[$4]=="/"){
continue
};$3.addNode($2[$4]);$3.selectChild()
}};return $1
},"getDataset",function(){
return null
}],null);Class.make("LzDatapath",LzDatapointer,["datacontrolsvisibility",true,"$lzc$set_datacontrolsvisibility",function($1){
this.datacontrolsvisibility=$1
},"__LZtakeDPSlot",true,"storednodes",null,"__LZneedsUpdateAfterInit",false,"__LZdepChildren",null,"sel",false,"__LZisclone",false,"pooling",false,"replication",void 0,"axis","y","spacing",0,"sortpath",void 0,"$lzc$set_sortpath",function($1){
this.setOrder($1)
},"setOrder",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if(this.__LZisclone){
this.immediateparent.cloneManager.setOrder($1,$2)
}else{
this.sortpath=$1;if($2!=null){
this.sortorder=$2
}}},"sortorder","ascending","$lzc$set_sortorder",function($1){
this.setComparator($1)
},"setComparator",function($1){
if(this.__LZisclone){
this.immediateparent.cloneManager.setComparator($1)
}else{
this.sortorder=$1
}},"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"construct",function($1,$2){
this.rerunxpath=true;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);if($2.datacontrolsvisibility!=null){
this.datacontrolsvisibility=$2.datacontrolsvisibility
};if(this.__LZtakeDPSlot){
this.immediateparent.datapath=this;var $3=this.immediateparent.searchParents("datapath").datapath;if($3!=null){
var $4=$3.__LZdepChildren;if($4!=null){
$3.__LZdepChildren=[];for(var $5=$4.length-1;$5>=0;$5--){
var $6=$4[$5];if($6!==this&&!(LzDataAttrBind["$lzsc$isa"]?LzDataAttrBind.$lzsc$isa($6):$6 instanceof LzDataAttrBind)&&$6.immediateparent!=this.immediateparent&&$6.immediateparent.childOf(this.immediateparent)){
$6.setDataContext(this,true)
}else{
$3.__LZdepChildren.push($6)
}}}}else{

}}},"__LZHandleMultiNodes",function($1){
var $2;if(this.replication=="lazy"){
$2=LzLazyReplicationManager
}else{
if(this.replication=="resize"){
$2=LzResizeReplicationManager
}else{
$2=LzReplicationManager
}};this.storednodes=$1;var $3=new $2(this,this._instanceAttrs);this.storednodes=null;return $3
},"setNodes",function($1){
var $2=this.__LZHandleMultiNodes($1);if(!$2){
$2=this
};$2.__LZsetTracking(null);if($1){
for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3];var $5=$4.ownerDocument;$2.__LZsetTracking($5,true,$4!=$5)
}}},"__LZsendUpdate",function($1,$2){
switch(arguments.length){
case 0:
$1=false;
case 1:
$2=false;

};var $3=this.__LZpchanged;if(!(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsendUpdate"]||this.nextMethod(arguments.callee,"__LZsendUpdate")).call(this,$1,$2)){
return false
};if(this.immediateparent.isinited){
this.__LZApplyData($3)
}else{
this.__LZneedsUpdateAfterInit=true
};return true
},"__LZApplyDataOnInit",function(){
if(this.__LZneedsUpdateAfterInit){
this.__LZApplyData();this.__LZneedsUpdateAfterInit=false
}},"__LZApplyData",function($1){
switch(arguments.length){
case 0:
$1=false;

};var $2=this.immediateparent;if(this.datacontrolsvisibility){
if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($2):$2 instanceof LzView){
var $3=$2;$3.__LZvizDat=this.p!=null;$3.__LZupdateShown()
}};var $4=$1||$2.data!=this.data||this.parsedPath&&this.parsedPath.operator=="attributes";this.data=this.data==null?null:this.data;$2.data=this.data;if($4){
if($2.ondata.ready){
$2.ondata.sendEvent(this.data)
};var $5=this.parsedPath;if($5&&($5.operator!=null||$5.aggOperator!=null)){
$2.applyData(this.data)
}}},"setDataContext",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};if($1==null&&this.immediateparent!=null){
$1=this.immediateparent.searchParents("datapath").datapath;$2=true
}else{

};if($1==this.context){
return
};if($2){
if($1.__LZdepChildren==null){
$1.__LZdepChildren=[this]
}else{
$1.__LZdepChildren.push(this)
}}else{
if(this.context&&(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa(this.context):this.context instanceof LzDatapath)){
var $3=this.context.__LZdepChildren;if($3){
for(var $4=0;$4<$3.length;$4++){
if($3[$4]===this){
$3.splice($4,1);break
}}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$1)
},"destroy",function(){
this.__LZupdateLocked=true;var $1=this.context;if($1&&!$1.__LZdeleted&&(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa($1):$1 instanceof LzDatapath)){
var $2=$1.__LZdepChildren;if($2!=null){
for(var $3=0;$3<$2.length;$3++){
if($2[$3]===this){
$2.splice($3,1);break
}}}};var $4=this.immediateparent;if(!$4.__LZdeleted){
var $5=this.__LZdepChildren;if($5!=null&&$5.length>0){
var $6=$4.searchParents("datapath").datapath;for(var $3=0;$3<$5.length;$3++){
$5[$3].setDataContext($6,true)
};this.__LZdepChildren=null
}};if($4.datapath===this){
$4.datapath=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).apply(this,arguments)
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($1){
switch(arguments.length){
case 0:
$1=false;

};if(!$1&&this.p){
this.p.__LZlockFromUpdate(this)
};var $2=this.parsedPath?this.parsedPath.operator:null;if($2!=null){
var $3=this.immediateparent.updateData();if($3!==void 0){
if($2=="name"){
this.setNodeName($3)
}else{
if($2=="text"){
this.setNodeText($3)
}else{
if($2=="attributes"){
this.p.$lzc$set_attributes($3)
}else{
this.setNodeAttribute($2.substring(11),$3)
}}}}};var $4=this.__LZdepChildren;if($4!=null){
for(var $5=0;$5<$4.length;$5++){
$4[$5].__LZupdateData(true)
}};if(!$1&&this.p){
this.p.__LZunlockFromUpdate(this)
}},"toString",function(){
return "Datapath for "+this.immediateparent
},"__LZcheckChange",function($1){
if(!(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZcheckChange"]||this.nextMethod(arguments.callee,"__LZcheckChange")).call(this,$1)){
if($1.who.childOfNode(this.p,true)&&this.onDocumentChange.ready){
this.onDocumentChange.sendEvent($1)
}};return false
},"__LZsetTracking",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=false;
case 2:
$3=false;

};if(!$1||!$2){
return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsetTracking"]||this.nextMethod(arguments.callee,"__LZsetTracking")).call(this,$1,true)
};var $4=this.__LZtracking;var $5=this.__LZtrackDel;if($3){
var $6=$4.length;for(var $7=0;$7<$6;$7++){
if($4[$7]===$1){
return
}}};if(!$5){
this.__LZtrackDel=$5=new LzDelegate(this,"__LZcheckChange")
};$4.push($1);$5.register($1,"onDocumentChange")
},"$lzc$set___LZmanager",function($1){
this.__LZisclone=true;this.immediateparent.cloneManager=$1;this.parsedPath=$1.parsedPath;this.xpath=$1.xpath;this.setDataContext($1)
},"setClonePointer",function($1){
var $2=this.p!=$1;this.p=$1;if($2){
if($1&&this.sel!=$1.sel){
this.sel=$1.sel||false;this.immediateparent.setSelected(this.sel)
};this.__LZpchanged=true;this.__LZsetData()
}},"setSelected",function($1){
this.p.sel=$1;this.sel=$1;this.immediateparent.setSelected($1)
},"__LZgetLast",function(){
var $1=this.context;if(this.__LZisclone){
return $1.nodes.length
}else{
if(this.p==$1.getContext()&&(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($1):$1 instanceof LzDatapointer)){
return $1.__LZgetLast()
}else{
return 1
}}},"__LZgetPosition",function(){
if(this.__LZisclone){
return this.immediateparent.clonenumber+1
}else{
var $1=this.context;if(this.p==$1.getContext()&&(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($1):$1 instanceof LzDatapointer)){
return $1.__LZgetPosition()
}else{
return 1
}}}],["tagname","datapath","attributes",new LzInheritedHash(LzDatapointer.attributes)]);lz[LzDatapath.tagname]=LzDatapath;Class.make("LzReplicationManager",LzDatapath,["asyncnew",true,"initialnodes",void 0,"clonePool",void 0,"cloneClass",void 0,"cloneParent",void 0,"cloneAttrs",void 0,"cloneChildren",void 0,"hasdata",void 0,"orderpath",void 0,"comparator",void 0,"__LZxpathconstr",null,"__LZxpathdepend",null,"visible",true,"__LZpreventXPathUpdate",false,"nodes",void 0,"clones",void 0,"__LZdataoffset",0,"onnodes",LzDeclaredEvent,"onclones",LzDeclaredEvent,"onvisible",LzDeclaredEvent,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"getDefaultPooling",function(){
return false
},"construct",function($1,$2){
this.pooling=this.getDefaultPooling();this.__LZtakeDPSlot=false;this.datacontrolsvisibility=false;var $3=$1.immediateparent;this.classroot=$3.classroot;if($3===canvas){
this.nodes=[];this.clones=[];this.clonePool=[];return
};this.datapath=this;var $4=$3.name;if($4!=null){
$2.name=$4;$3.immediateparent[$4]=null;$3.parent[$4]=null
};var $5=$3.$lzc$bind_id;if($5!=null){
$5.call(null,$3,false);$3.$lzc$bind_id=null;this.$lzc$bind_id=$5;$5.call(null,this)
};var $6=$3.$lzc$bind_name;if($6!=null){
$6.call(null,$3,false);$3.$lzc$bind_name=null;this.$lzc$bind_name=$6;$6.call(null,this)
};$2.xpath=LzNode._ignoreAttribute;if($1.sortpath!=null){
$2.sortpath=$1.sortpath
};if($1.sortorder!=null||$1.sortorder){
$2.sortorder=$1.sortorder
};this.initialnodes=$1.storednodes;if($1.__LZspecialDotDot){
this.__LZspecialDotDot=true;if($1.__LZdotdotCheckDel){
$1.__LZdotdotCheckDel.unregisterAll()
};$1.__LZspecialDotDot=false
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$3.parent,$2);if($2.name!=null&&$3.parent!=$3.immediateparent){
$3.immediateparent[$2.name]=this
};this.xpath=$1.xpath;this.parsedPath=$1.parsedPath;this.cloneClass=$3.constructor;this.cloneParent=$3.parent;var $7=new LzInheritedHash($3._instanceAttrs);$7.datapath=LzNode._ignoreAttribute;$7.$datapath={"class":lz.datapath};$7.$datapath.attrs={datacontrolsvisibility:$1.datacontrolsvisibility,__LZmanager:this};delete $7.id;delete $7.name;delete $7.$lzc$bind_id;delete $7.$lzc$bind_name;this.cloneAttrs=$7;if($1.datacontrolsvisibility){
this.visible=true
}else{
if(!$3.isinited){
var $8=this.__LZgetInstanceAttr($3,"visible");if(typeof $8=="boolean"||(Boolean["$lzsc$isa"]?Boolean.$lzsc$isa($8):$8 instanceof Boolean)){
this.visible=$8
}else{
this.visible=$3.visible
}}else{
this.visible=$3.visible
}};if($2.pooling!=null){
this.pooling=$2.pooling
};var $9=this.__LZgetInstanceAttr($3,"datapath");if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($9):$9 instanceof LzAlwaysExpr){
var $10=$9;this.__LZxpathconstr=$3[$10.methodName];this.__LZxpathdepend=$3[$10.dependenciesName];this.__LZpreventXPathUpdate=true;this.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr","__LZxpathdepend"));this.__LZpreventXPathUpdate=false;if(this.pooling){
$3.releaseConstraintMethod($10.methodName)
}}else{
var $11=this.__LZgetInstanceAttr($1,"xpath");if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($11):$11 instanceof LzAlwaysExpr){
var $12=new LzRefNode(this);var $13=$11;$12.__LZxpathconstr=$1[$13.methodName];$12.__LZxpathdepend=$1[$13.dependenciesName];this.__LZpreventXPathUpdate=true;$12.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr","__LZxpathdepend"));this.__LZpreventXPathUpdate=false;if(this.pooling){
$1.releaseConstraintMethod($13.methodName)
}}};this.__LZsetCloneAttrs();if($3._instanceChildren){
this.cloneChildren=$3._instanceChildren.concat()
}else{
this.cloneChildren=[]
};var $14=$1.context;this.clones=[];this.clonePool=[];if(this.pooling){
$1.$lzc$set___LZmanager(this);this.clones.push($3);$3.immediateparent.addSubview($3)
}else{
this.destroyClone($3)
};this.setDataContext($14,$14 instanceof LzDatapointer)
},"__LZgetInstanceAttr",function($1,$2){
var $3=$1._instanceAttrs;if($3&&($2 in $3)){
return $3[$2]
}else{
var $4=$1["constructor"].attributes;if($4&&($2 in $4)){
return $4[$2]
}};return void 0
},"__LZsetCloneAttrs",function(){

},"__LZapplyArgs",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$1,$2);if(this.__LZdeleted){
return
};this.__LZHandleMultiNodes(this.initialnodes);this.initialnodes=null;if(this.visible==false){
this.$lzc$set_visible(false)
}},"setDataContext",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};if($1==null&&this.immediateparent!=null&&this.immediateparent["datapath"]!=null){
$1=this.immediateparent.datapath;$2=true
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$1,$2)
},"getCloneNumber",function($1){
return this.clones[$1]
},"__LZHandleNoNodes",function(){
this.nodes=[];var $1=this.clones;while($1.length){
if(this.pooling){
this.poolClone()
}else{
var $2=$1.pop();this.destroyClone($2)
}}},"__LZHandleSingleNode",function($1){
this.__LZHandleMultiNodes([$1])
},"__LZHandleMultiNodes",function($1){
var $2=this.parent&&this.parent.layouts?this.parent.layouts:[];for(var $3=0;$3<$2.length;++$3){
$2[$3].lock()
};this.hasdata=true;var $4=this.nodes;this.nodes=$1;if(this.onnodes.ready){
this.onnodes.sendEvent(this.nodes)
};if(this.__LZspecialDotDot){
this.__LZsetupDotDot($1[0])
};if(this.orderpath!=null&&this.nodes){
this.nodes=this.mergesort(this.nodes,0,this.nodes.length-1)
};this.__LZadjustVisibleClones($4,true);var $5=this.clones.length;for(var $3=0;$3<$5;$3++){
var $6=this.clones[$3];var $7=$3+this.__LZdataoffset;$6.clonenumber=$7;if(this.nodes){
$6.datapath.setClonePointer(this.nodes[$7])
};if($6.onclonenumber.ready){
$6.onclonenumber.sendEvent($7)
}};if(this.onclones.ready){
this.onclones.sendEvent(this.clones)
};for(var $3=0;$3<$2.length;++$3){
$2[$3].unlock()
};return null
},"__LZadjustVisibleClones",function($1,$2){
var $3=this.__LZdiffArrays($1,this.nodes);if(!this.pooling){
while(this.clones.length>$3){
var $4=this.clones.pop();this.destroyClone($4)
}};lz.Instantiator.enableDataReplicationQueuing(this);while(this.nodes&&this.nodes.length>this.clones.length){
var $5=this.getNewClone();if(!$5){
break
};this.clones.push($5)
};lz.Instantiator.clearDataReplicationQueue(this);while(this.nodes&&this.nodes.length<this.clones.length){
this.poolClone()
}},"mergesort",function($1,$2,$3){
if($2<$3){
var $4=$2+Math.floor(($3-$2)/2);var $5=this.mergesort($1,$2,$4);var $6=this.mergesort($1,$4+1,$3)
}else{
if($1.length==0){
return []
}else{
return [$1[$2]]
}};var $7=[];var $8=0;var $9=0;var $10=$5.length;var $11=$6.length;while($8<$10&&$9<$11){
if(this.orderf($6[$9],$5[$8])==1){
$7.push($6[$9++])
}else{
$7.push($5[$8++])
}};while($8<$10){
$7.push($5[$8++])
};while($9<$11){
$7.push($6[$9++])
};return $7
},"orderf",function($1,$2){
var $3=this.orderpath;this.p=$1;var $4=this.xpathQuery($3);this.p=$2;var $5=this.xpathQuery($3);this.p=null;if($4==null||$4==""){
$4="\n"
};if($5==null||$5==""){
$5="\n"
};return this.comparator($4,$5)
},"ascDict",function($1,$2){
if($1.toLowerCase()<$2.toLowerCase()){
return 1
}else{
return 0
}},"descDict",function($1,$2){
if($1.toLowerCase()>$2.toLowerCase()){
return 1
}else{
return 0
}},"setOrder",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};this.orderpath=null;if($2!=null){
this.setComparator($2)
};this.orderpath=$1;if(this.nodes&&this.nodes.length){
this.__LZHandleMultiNodes(this.nodes)
}},"setComparator",function($1){
if($1=="descending"){
$1=this.descDict
}else{
if($1=="ascending"){
$1=this.ascDict
}else{
if(Function["$lzsc$isa"]?Function.$lzsc$isa($1):$1 instanceof Function){

}else{

}}};this.comparator=$1;if(this.orderpath!=null&&this.nodes&&this.nodes.length){
this.__LZHandleMultiNodes(this.nodes)
}},"getNewClone",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(!this.cloneParent){
return null
};if(this.clonePool.length){
var $2=this.reattachClone(this.clonePool.pop())
}else{
var $2=new (this.cloneClass)(this.cloneParent,this.cloneAttrs,this.cloneChildren,$1==null?this.asyncnew:!$1)
};if(this.visible==false){
$2.$lzc$set_visible(false)
};return $2
},"poolClone",function(){
var $1=this.clones.pop();this.detachClone($1);this.clonePool.push($1)
},"destroyClone",function($1){
$1.destroy()
},"$lzc$set_datapath",function($1){
this.setXPath($1)
},"setXPath",function($1){
if(this.__LZpreventXPathUpdate){
return false
};return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setXPath"]||this.nextMethod(arguments.callee,"setXPath")).apply(this,arguments)
},"handleDeletedNode",function($1){
var $2=this.clones[$1];if(this.pooling){
this.detachClone($2);this.clonePool.push($2)
}else{
this.destroyClone($2)
};this.nodes.splice($1,1);this.clones.splice($1,1)
},"getCloneForNode",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};var $3=this.clones;var $4=$3.length;for(var $5=0;$5<$4;$5++){
if($3[$5].datapath.p==$1){
return $3[$5]
}};return null
},"toString",function(){
return "ReplicationManager in "+this.immediateparent
},"setVisible",function($1){
this.$lzc$set_visible($1)
},"$lzc$set_visible",function($1){
this.visible=$1;var $2=this.clones;var $3=$2.length;for(var $4=0;$4<$3;$4++){
$2[$4].$lzc$set_visible($1)
};if(this.onvisible.ready){
this.onvisible.sendEvent($1)
}},"__LZcheckChange",function($1){
this.p=this.nodes[0];var $2=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZcheckChange"]||this.nextMethod(arguments.callee,"__LZcheckChange")).call(this,$1);this.p=null;if(!$2){
var $3=$1.who;var $4=this.clones;var $5=$4.length;for(var $6=0;$6<$5;$6++){
var $7=$4[$6];var $8=$7.datapath;if($8.__LZneedsOpUpdate($1)){
$8.__LZsetData()
};if($3.childOfNode($8.p,true)){
if($8.onDocumentChange.ready){
$8.onDocumentChange.sendEvent($1)
}}}};return false
},"__LZneedsOpUpdate",function($1){
switch(arguments.length){
case 0:
$1=null;

};return false
},"getContext",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.nodes[0]
},"detachClone",function($1){
if($1.isdetatchedclone){
return
};$1.$lzc$set_visible(false);$1.addedToParent=false;var $2=$1.immediateparent.subviews;for(var $3=$2.length-1;$3>=0;$3--){
if($2[$3]==$1){
$2.splice($3,1);break
}};$1.datapath.__LZtrackDel.unregisterAll();var $4=$1.immediateparent.onremovesubview;if($4.ready){
$4.sendEvent($1)
};$1.isdetatchedclone=true;$1.datapath.p=null
},"reattachClone",function($1){
if(!$1.isdetatchedclone){
return $1
};$1.immediateparent.addSubview($1);$1.$lzc$set_visible(this.visible);$1.isdetatchedclone=false;return $1
},"__LZdiffArrays",function($1,$2){
var $3=0;var $4=$1?$1.length:0;var $5=$2?$2.length:0;while($3<$4&&$3<$5){
if($1[$3]!=$2[$3]){
return $3
};$3++
};return $3
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($1){
switch(arguments.length){
case 0:
$1=false;

};var $2=this.clones;var $3=$2.length;for(var $4=0;$4<$3;$4++){
$2[$4].datapath.updateData()
}}],null);lz.ReplicationManager=LzReplicationManager;Class.make("LzRefNode",LzNode,["__LZxpathconstr",null,"__LZxpathdepend",null,"xpath",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"$lzc$set_xpath",function($1){
this.parent.$lzc$set_xpath($1)
}],null);Class.make("LzDataAttrBind",LzDatapointer,["$lzsc$initialize",function($1,$2,$3,$4){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1);this.type=$4;this.setAttr=$2;this.pathparent=$1;this.node=$1.immediateparent;this.setXPath($3);this.rerunxpath=true;if($1.__LZdepChildren==null){
$1.__LZdepChildren=[this]
}else{
$1.__LZdepChildren.push(this)
}},"$pathbinding",true,"setAttr",void 0,"pathparent",void 0,"node",void 0,"type",void 0,"__LZsendUpdate",function($1,$2){
switch(arguments.length){
case 0:
$1=false;
case 1:
$2=false;

};var $3=this.__LZpchanged;var $4=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsendUpdate"]||this.nextMethod(arguments.callee,"__LZsendUpdate")).call(this,$1,$2);if($4){
var $5=this.data;var $6=this.node;var $7=this.setAttr;if($5==null){
$5=null
};var $8=$6.acceptTypeValue(this.type,$5);if($3||$6[$7]!==$8||!$6.inited||this.parsedPath.operator=="attributes"){
if(!$6.__LZdeleted){
var $lzsc$481170464="$lzc$set_"+$7;if(Function["$lzsc$isa"]?Function.$lzsc$isa($6[$lzsc$481170464]):$6[$lzsc$481170464] instanceof Function){
$6[$lzsc$481170464]($8)
}else{
$6[$7]=$8;var $lzsc$1661374603=$6["on"+$7];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1661374603):$lzsc$1661374603 instanceof LzEvent){
if($lzsc$1661374603.ready){
$lzsc$1661374603.sendEvent($8)
}}}}}};return $4
},"unregisterAll",function(){
var $1=this.pathparent.__LZdepChildren;if($1!=null){
for(var $2=0;$2<$1.length;$2++){
if($1[$2]===this){
$1.splice($2,1);break
}}};this.destroy()
},"setDataContext",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$1||this.pathparent,$2)
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($1){
switch(arguments.length){
case 0:
$1=false;

};var $2=this.parsedPath.operator;if($2!=null){
var $3=this.node.presentAttribute(this.setAttr,this.type);if(this.data!=$3){
if($2=="name"){
this.setNodeName($3)
}else{
if($2=="text"){
this.setNodeText($3)
}else{
if($2=="attributes"){
this.p.$lzc$set_attributes($3)
}else{
this.setNodeAttribute($2.substring(11),$3)
}}}}}},"toString",function(){
return "binder "+this.xpath
}],null);Class.make("LzLazyReplicationManager",LzReplicationManager,["sizeAxis",void 0,"cloneimmediateparent",void 0,"updateDel",void 0,"__LZoldnodelen",void 0,"viewsize",0,"totalsize",0,"mask",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"getDefaultPooling",function(){
return true
},"construct",function($1,$2){
if($2.pooling!=null){
$2.pooling=true
};if($2.axis!=null){
this.axis=$2.axis
};this.sizeAxis=this.axis=="x"?"width":"height";(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.mask=$1.immediateparent.immediateparent.mask;var $3;if(this.cloneAttrs.options!=null){
$3=new LzInheritedHash(this.cloneAttrs.options);$3["ignorelayout"]=true
}else{
$3={ignorelayout:true}};var $4=this.clones[0];if($4){
$4.setOption("ignorelayout",true);var $5=$4.immediateparent.layouts;if($5!=null){
for(var $6=0;$6<$5.length;$6++){
$5[$6].removeSubview($4)
}}};this.cloneAttrs.options=$3;var $7=this.getNewClone(true);this.cloneimmediateparent=$7.immediateparent;if(this.initialnodes){
$7.datapath.setClonePointer(this.initialnodes[1])
};this.viewsize=$7[this.sizeAxis];$7.datapath.setClonePointer(null);this.clones.push($7);if($2.spacing==null){
$2.spacing=0
};this.totalsize=this.viewsize+$2.spacing;var $lzsc$1674242144=this.axis;var $lzsc$500916818=this.totalsize;if(!$7.__LZdeleted){
var $lzsc$1122054867="$lzc$set_"+$lzsc$1674242144;if(Function["$lzsc$isa"]?Function.$lzsc$isa($7[$lzsc$1122054867]):$7[$lzsc$1122054867] instanceof Function){
$7[$lzsc$1122054867]($lzsc$500916818)
}else{
$7[$lzsc$1674242144]=$lzsc$500916818;var $lzsc$1627592785=$7["on"+$lzsc$1674242144];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1627592785):$lzsc$1627592785 instanceof LzEvent){
if($lzsc$1627592785.ready){
$lzsc$1627592785.sendEvent($lzsc$500916818)
}}}};this.__LZdataoffset=0;this.updateDel=new LzDelegate(this,"__LZhandleUpdate");this.updateDel.register(this.cloneimmediateparent,"on"+this.axis);this.updateDel.register(this.mask,"on"+this.sizeAxis)
},"__LZhandleUpdate",function($1){
this.__LZadjustVisibleClones(null,null)
},"__LZsetCloneAttrs",function(){
var $1;if(this.cloneAttrs.options!=null){
$1=new LzInheritedHash(this.cloneAttrs.options);$1["ignorelayout"]=true
}else{
$1={ignorelayout:true}};this.cloneAttrs.options=$1
},"__LZHandleNoNodes",function(){
this.__LZHandleMultiNodes([])
},"__LZadjustVisibleClones",function($1,$2){
var $3=this.cloneimmediateparent;var $4=this.nodes;var $5=this.axis;var $6=this.sizeAxis;var $7=this.totalsize;if($4){
var $8=$4.length;if(this.__LZoldnodelen!=$8){
var $lzsc$749533527=$8*$7-this.spacing;if(!$3.__LZdeleted){
var $lzsc$980710559="$lzc$set_"+$6;if(Function["$lzsc$isa"]?Function.$lzsc$isa($3[$lzsc$980710559]):$3[$lzsc$980710559] instanceof Function){
$3[$lzsc$980710559]($lzsc$749533527)
}else{
$3[$6]=$lzsc$749533527;var $lzsc$334320472=$3["on"+$6];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$334320472):$lzsc$334320472 instanceof LzEvent){
if($lzsc$334320472.ready){
$lzsc$334320472.sendEvent($lzsc$749533527)
}}}};this.__LZoldnodelen=$8
}};if(!(this.mask&&this.mask["hasset"+$6])){
return
};var $9=0;if($7!=0){
$9=Math.floor(-$3[$5]/$7);if(0>$9){
$9=0
}};var $10=0;var $11=this.clones.length;var $12=$9-this.__LZdataoffset;var $13=$9*$7+$3[$5];var $14=0;if(typeof $13=="number"){
$14=1+Math.floor((this.mask[$6]-$13)/$7)
};if($4!=null){
var $8=$4.length;if($14+$9>$8){
$14=$8-$9
}};if($12==0&&$14==$11){
return
};lz.Instantiator.enableDataReplicationQueuing(this);var $15=this.clones;this.clones=[];for(var $16=0;$16<$14;$16++){
var $17=null;if($16+$12<0){
if($14+$12<$11&&$11>0){
$17=$15[--$11]
}else{
$17=this.getNewClone()
}}else{
if($16+$12>=$11){
if($10<$12&&$10<$11){
$17=$15[$10++]
}else{
$17=this.getNewClone()
}}};if($17){
this.clones[$16]=$17;var $lzsc$2098665431=($16+$9)*$7;if(!$17.__LZdeleted){
var $lzsc$1623567863="$lzc$set_"+$5;if(Function["$lzsc$isa"]?Function.$lzsc$isa($17[$lzsc$1623567863]):$17[$lzsc$1623567863] instanceof Function){
$17[$lzsc$1623567863]($lzsc$2098665431)
}else{
$17[$5]=$lzsc$2098665431;var $lzsc$767505711=$17["on"+$5];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$767505711):$lzsc$767505711 instanceof LzEvent){
if($lzsc$767505711.ready){
$lzsc$767505711.sendEvent($lzsc$2098665431)
}}}};$17.clonenumber=$9+$16;if($4){
$17.datapath.setClonePointer($4[$9+$16])
};if($17.onclonenumber.ready){
$17.onclonenumber.sendEvent($16)
}}else{
this.clones[$16]=$15[$16+$12]
}};var $18=this.clonePool;while($10<$12&&$10<$11){
var $19=$15[$10++];this.detachClone($19);$18.push($19)
};while($11>$14+$12&&$11>0){
var $19=$15[--$11];this.detachClone($19);$18.push($19)
};this.__LZdataoffset=$9;lz.Instantiator.clearDataReplicationQueue(this)
},"toString",function(){
return "Lazy clone manager in "+this.cloneimmediateparent
},"getCloneForNode",function($1,$2){
switch(arguments.length){
case 1:
$2=false;

};var $3=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["getCloneForNode"]||this.nextMethod(arguments.callee,"getCloneForNode")).call(this,$1)||null;if(!$3&&!$2){
$3=this.getNewClone();$3.datapath.setClonePointer($1);this.detachClone($3);this.clonePool.push($3)
};return $3
},"getCloneNumber",function($1){
return this.getCloneForNode(this.nodes[$1])
}],null);lz.LazyReplicationManager=LzLazyReplicationManager;Class.make("LzResizeReplicationManager",LzLazyReplicationManager,["datasizevar",void 0,"__LZresizeupdating",void 0,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4)
},"getDefaultPooling",function(){
return false
},"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.datasizevar="$"+this.getUID()+"track"
},"__LZHandleCloneResize",function($1){
var $2=this.datapath.p;if($2){
var $3=this.cloneManager;var $4=$3.datasizevar;var $5=$2.getUserData($4)||$3.viewsize;if($1!=$5){
$2.setUserData($4,$1);$3.__LZadjustVisibleClones(null,false)
}}},"__LZsetCloneAttrs",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsetCloneAttrs"]||this.nextMethod(arguments.callee,"__LZsetCloneAttrs")).call(this);var $1=this.cloneAttrs;$1.__LZHandleCloneResize=this.__LZHandleCloneResize;if(!$1["$delegates"]){
$1.$delegates=[]
};$1.$delegates.push("on"+(this.axis=="y"?"height":"width"),"__LZHandleCloneResize",null)
},"getPositionByNode",function($1){
var $2=-this.spacing;var $3;if(this.nodes!=null){
for(var $4=0;$4<this.nodes.length;$4++){
$3=this.nodes[$4];if($1==this.nodes[$4]){
return $2+this.spacing
};$2+=this.spacing+($3.getUserData(this.datasizevar)||this.viewsize)
}};return undefined
},"__LZreleaseClone",function($1){
this.detachClone($1);this.clonePool.push($1)
},"__LZadjustVisibleClones",function($1,$2){
if(!this.mask["hasset"+this.sizeAxis]){
return
};if(this.__LZresizeupdating){
return
};this.__LZresizeupdating=true;var $3=this.nodes!=null?this.nodes.length:0;var $4=Math.floor(-this.cloneimmediateparent[this.axis]);if(0>$4){
$4=0
};var $5=this.mask[this.sizeAxis];var $6=-1;var $7=this.__LZdataoffset;if($2){
while(this.clones.length){
this.poolClone()
};var $8=null;var $9=0
}else{
var $8=this.clones;var $9=$8.length
};this.clones=[];var $10=-this.spacing;var $11=false;var $12=-1;var $13;var $14=true;for(var $15=0;$15<$3;$15++){
var $16=this.nodes[$15];var $17=$16.getUserData(this.datasizevar);var $18=$17==null?this.viewsize:$17;$10+=this.spacing;if(!$11&&$6==-1&&$10-$4+$18>=0){
$14=$15!=0;$11=true;$13=$10;$6=$15;var $19=$15-$7;$19=$19>$9?$9:$19;if($19>0){
for(var $20=0;$20<$19;$20++){
var $21=$8[$20];this.__LZreleaseClone($21)
}}}else{
if($11&&$10-$4>$5){
$11=false;$12=$15-$6;var $22=$15-$7;$22=$22<0?0:$22;if($22<$9){
for(var $20=$22;$20<$9;$20++){
var $21=$8[$20];this.__LZreleaseClone($21)
}}}};if($11){
if($15>=$7&&$15<$7+$9){
var $23=$8[$15-$7]
}else{
var $23=null
};this.clones[$15-$6]=$23
};$10+=$18
};var $24=$13;if($14){
$24+=this.spacing
};for(var $15=0;$15<this.clones.length;$15++){
var $16=this.nodes[$15+$6];var $23=this.clones[$15];if(!$23){
$23=this.getNewClone();$23.clonenumber=$15+$6;$23.datapath.setClonePointer($16);if($23.onclonenumber.ready){
$23.onclonenumber.sendEvent($15+$6)
};this.clones[$15]=$23
};this.clones[$15]=$23;var $lzsc$736454=this.axis;if(!$23.__LZdeleted){
var $lzsc$1473405217="$lzc$set_"+$lzsc$736454;if(Function["$lzsc$isa"]?Function.$lzsc$isa($23[$lzsc$1473405217]):$23[$lzsc$1473405217] instanceof Function){
$23[$lzsc$1473405217]($24)
}else{
$23[$lzsc$736454]=$24;var $lzsc$179648444=$23["on"+$lzsc$736454];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$179648444):$lzsc$179648444 instanceof LzEvent){
if($lzsc$179648444.ready){
$lzsc$179648444.sendEvent($24)
}}}};var $17=$16.getUserData(this.datasizevar);var $18=$17==null?this.viewsize:$17;if($23[this.sizeAxis]!=$18){
var $lzsc$274864851=this.sizeAxis;if(!$23.__LZdeleted){
var $lzsc$1404506979="$lzc$set_"+$lzsc$274864851;if(Function["$lzsc$isa"]?Function.$lzsc$isa($23[$lzsc$1404506979]):$23[$lzsc$1404506979] instanceof Function){
$23[$lzsc$1404506979]($18)
}else{
$23[$lzsc$274864851]=$18;var $lzsc$1859110098=$23["on"+$lzsc$274864851];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1859110098):$lzsc$1859110098 instanceof LzEvent){
if($lzsc$1859110098.ready){
$lzsc$1859110098.sendEvent($18)
}}}}};$24+=$18+this.spacing
};this.__LZdataoffset=$6;var $lzsc$1344305178=this.cloneimmediateparent;var $lzsc$1452687814=this.sizeAxis;if(!$lzsc$1344305178.__LZdeleted){
var $lzsc$1983663651="$lzc$set_"+$lzsc$1452687814;if(Function["$lzsc$isa"]?Function.$lzsc$isa($lzsc$1344305178[$lzsc$1983663651]):$lzsc$1344305178[$lzsc$1983663651] instanceof Function){
$lzsc$1344305178[$lzsc$1983663651]($10)
}else{
$lzsc$1344305178[$lzsc$1452687814]=$10;var $lzsc$1548683164=$lzsc$1344305178["on"+$lzsc$1452687814];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1548683164):$lzsc$1548683164 instanceof LzEvent){
if($lzsc$1548683164.ready){
$lzsc$1548683164.sendEvent($10)
}}}};this.__LZresizeupdating=false
}],null);lz.ResizeReplicationManager=LzResizeReplicationManager;Class.make("LzColorUtils",null,null,["stringToColor",function($1){
if(typeof $1!="string"){
return $1
};if(lz.colors[$1]!=null){
return lz.colors[$1]
};if(global[$1]!=null){
return global[$1]
};if($1.indexOf("rgb")!=-1){
return LzColorUtils.fromrgb($1)
};var $2=Number($1);if(isNaN($2)){
return $1
}else{
return $2
}},"fromrgb",function($1){
if(typeof $1!="string"){
return $1
};if($1.indexOf("rgb")==-1){
return LzColorUtils.stringToColor($1)
};var $2=$1.substring($1.indexOf("(")+1,$1.indexOf(")")).split(",");var $3=($2[0]<<16)+($2[1]<<8)+$2[2]*1;if($2.length>3){
$3+=$2[3]*0.01
};if(typeof $3=="number"){
return $3
};return 0
},"dectohex",function($1,$2){
switch(arguments.length){
case 1:
$2=0;

};if(typeof $1!="number"){
return $1
};$1=$1&16777215;var $3=$1.toString(16);var $4=$2-$3.length;while($4>0){
$3="0"+$3;$4--
};return $3
},"torgb",function($1){
if(typeof $1=="string"&&$1.indexOf("rgb")!=-1){
return $1
};var $2=LzColorUtils.inttohex($1);if(typeof $2!="string"){
return $2
};if(typeof $1=="number"||lz.colors[$1]!=null){
$1=$2
};if($1.length<6){
$1="#"+$1.charAt(1)+$1.charAt(1)+$1.charAt(2)+$1.charAt(2)+$1.charAt(3)+$1.charAt(3)+($1.length>4?$1.charAt(4)+$1.charAt(4):"")
};return($1.length>7?"rgba(":"rgb(")+parseInt($1.substring(1,3),16)+","+parseInt($1.substring(3,5),16)+","+parseInt($1.substring(5,7),16)+($1.length>7?","+parseInt($1.substring(7),16):"")+")"
},"tohsv",function($1){
var $2=($1>>16&255)/255,$3=($1>>8&255)/255,$4=($1&255)/255;var $5=Math.min($2,Math.min($3,$4)),$6=Math.max($2,Math.max($3,$4));var $7=$6;var $8=$6-$5;if($8==0){
return {h:0,s:0,v:$7}};var $9=$8/$6;if($2==$6){
var $10=($3-$4)/$8
}else{
if($3==$6){
var $10=2+($4-$2)/$8
}else{
var $10=4+($2-$3)/$8
}};$10*=60;if($10<0){
$10+=360
};return {h:$10,s:$9,v:$7}},"fromhsv",function($1,$2,$3){
var $4=$1/60;var $5=Math.floor($4);var $6=$5%6;var $7=$4-$5;var $8=$3*(1-$2);var $9=$3*(1-$7*$2);var $4=$3*(1-(1-$7)*$2);var $10,$11,$12;switch($6){
case 0:
$10=$3;$11=$4;$12=$8;break;
case 1:
$10=$9;$11=$3;$12=$8;break;
case 2:
$10=$8;$11=$3;$12=$4;break;
case 3:
$10=$8;$11=$9;$12=$3;break;
case 4:
$10=$4;$11=$8;$12=$3;break;
case 5:
$10=$3;$11=$8;$12=$9;break;

};return $10*255<<16|$11*255<<8|$12*255
},"convertColor",function($1){
if($1=="null"||$1==null){
return null
};return LzColorUtils.hextoint($1)
},"hextoint",function($1){
var $2=LzColorUtils.stringToColor($1);if(typeof $2!="string"){
return $2
};var $3=$1;$3=$3.slice(1);var $4=0;if($3.length>6){
$4=parseInt($3.slice(6),16)/25500;$3=$3.slice(0,6)
};var $2=parseInt($3,16);switch($3.length){
case 3:
return(($2&3840)<<8|($2&240)<<4|$2&15)*17+$4;
case 6:
return $2+$4;
default:
break;

};return 0
},"inttohex",function($1,$2){
switch(arguments.length){
case 1:
$2=6;

};var $3=LzColorUtils.stringToColor($1);if(typeof $3!="number"){
return $3
};return "#"+LzColorUtils.dectohex($3,$2)
}]);Class.make("LzUtilsClass",null,["__SimpleExprPattern",void 0,"__ElementPattern",void 0,"$lzsc$initialize",function(){
this.__SimpleExprPattern=new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$");this.__ElementPattern=new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)","g")
},"color",{hextoint:function($1){
return LzColorUtils.hextoint($1)
},inttohex:function($1){
return LzColorUtils.inttohex($1)
},torgb:function($1){
return LzColorUtils.torgb($1)
}},"hextoint",function($1){
return LzColorUtils.hextoint($1)
},"inttohex",function($1,$2){
switch(arguments.length){
case 1:
$2=6;

};return LzColorUtils.inttohex($1,$2)
},"dectohex",function($1,$2){
switch(arguments.length){
case 1:
$2=0;

};return LzColorUtils.dectohex($1,$2)
},"stringToColor",function($1){
return LzColorUtils.stringToColor($1)
},"torgb",function($1){
return LzColorUtils.torgb($1)
},"fromrgb",function($1){
return LzColorUtils.fromrgb($1)
},"colornames",lz.colors,"__unpackList",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if($1==""){
return []
};if($2==null){
$2=canvas
};var $3=$1.split(",");for(var $4=0;$4<$3.length;$4++){
var $5=$3[$4];if($5==""){
continue
};while($5.charAt(0)==" "){
$5=$5.substring(1,$5.length)
};var $6=parseFloat($5);if(!isNaN($6)){
$3[$4]=$6
}else{
if($5.indexOf("'")!=-1){
var $7=$5.indexOf("'")+1;var $8=$5.lastIndexOf("'");$3[$4]=$5.substring($7,$8)
}else{
if($5=="true"||$5=="false"){
$3[$4]=$5=="true"
}else{
if($2[$5]){
$3[$4]=$2[$5]
}}}}};return $3
},"safeEval",function($1){
if($1.indexOf("new ")==0){
return this.safeNew($1)
};var $2=$1.indexOf("(");var $3=null;if($2!=-1){
var $4=$1.lastIndexOf(")");$3=$1.substring($2+1,$4);$1=$1.substring(0,$2)
};var $5=null,$6;if($1.match(this.__SimpleExprPattern)){
var $7=$1.match(this.__ElementPattern);$6=globalValue($7[0]);for(var $8=1,$9=$7.length;$8<$9;$8++){
$5=$6;$6=$6[$7[$8]]
}};if($3==null){
return $6
};var $10=lz.Utils.__unpackList($3,$5);if($6){
var $11=$6.apply($5,$10);return $11
}},"safeNew",function($1){
var $2=$1;var $3=$1.indexOf("new ");if($3==-1){
return $1
};$1=$1.substring($3+4);var $4=$1.indexOf("(");if($4!=-1){
var $5=$1.indexOf(")");var $6=$1.substring($4+1,$5);$1=$1.substring(0,$4)
};var $7=globalValue($1);if(!$7){
return
};var $6=lz.Utils.__unpackList($6);var $8=$6.length;if($8==0){
return new $7()
}else{
if($8==1){
return new $7($6[0])
}else{
if($8==2){
return new $7($6[0],$6[1])
}else{
if($8==3){
return new $7($6[0],$6[1],$6[2])
}else{
if($8==4){
return new $7($6[0],$6[1],$6[2],$6[3])
}else{
if($8==5){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4])
}else{
if($8==6){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5])
}else{
if($8==7){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5],$6[6])
}else{
if($8==8){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5],$6[6],$6[7])
}else{
if($8==9){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5],$6[6],$6[7],$6[8])
}else{
if($8==10){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5],$6[6],$6[7],$6[8],$6[9])
}else{
if($8==11){
return new $7($6[0],$6[1],$6[2],$6[3],$6[4],$6[5],$6[6],$6[7],$6[8],$6[9],$6[10])
}else{

}}}}}}}}}}}}}],null);lz.Utils=new LzUtilsClass();var LzUtils=lz.Utils;Class.make("LzInstantiatorService",LzEventable,["checkQDel",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.checkQDel=new LzDelegate(this,"checkQ")
},"halted",false,"isimmediate",false,"isdatareplicating",false,"istrickling",false,"isUpdating",false,"safe",true,"timeout",500,"makeQ",[],"trickleQ",[],"tricklingQ",[],"datareplQ",null,"dataQ",[],"syncNew",true,"trickletime",10,"setSafeInstantiation",function($1){
this.safe=$1;if(this.instanceQ.length){
this.timeout=Infinity
}},"requestInstantiation",function($1,$2){
if(this.isimmediate){
this.createImmediate($1,$2.concat())
}else{
var $3=this.newReverseArray($2);if(this.isdatareplicating){
this.datareplQ.push($3,$1)
}else{
if(this.istrickling){
this.tricklingQ.push($1,$3)
}else{
this.makeQ.push($1,$3);this.checkUpdate()
}}}},"enableDataReplicationQueuing",function($1){
if(this.isdatareplicating){
this.dataQ.push(this.datareplQ)
}else{
this.isdatareplicating=true
};this.datareplQ=[]
},"clearDataReplicationQueue",function($1){
var $2=this.datareplQ;if(this.dataQ.length>0){
this.datareplQ=this.dataQ.pop()
}else{
this.isdatareplicating=false;this.datareplQ=null
};var $3=$1.cloneParent;var $4=this.makeQ;var $5=$4.length;var $6=$5;for(var $7=0;$7<$5;$7+=2){
if($4[$7].parent===$3){
$6=$7;break
}};$2.push(0,$6);$2.reverse();$4.splice.apply($4,$2);this.checkUpdate()
},"newReverseArray",function($1){
var $2=$1.length;var $3=Array($2);for(var $4=0,$5=$2-1;$4<$2;){
$3[$4++]=$1[$5--]
};return $3
},"checkUpdate",function(){
if(!(this.isUpdating||this.halted)){
this.checkQDel.register(lz.Idle,"onidle");this.isUpdating=true
}},"checkQ",function($1){
switch(arguments.length){
case 0:
$1=null;

};if(!this.makeQ.length){
if(!this.tricklingQ.length){
if(!this.trickleQ.length){
this.checkQDel.unregisterAll();this.isUpdating=false;return
}else{
var $2=this.trickleQ.shift();var $3=this.trickleQ.shift();this.tricklingQ.push($2,this.newReverseArray($3))
}};this.istrickling=true;this.makeSomeViews(this.tricklingQ,this.trickletime);this.istrickling=false
}else{
canvas.creatednodes+=this.makeSomeViews(this.makeQ,this.timeout);if(canvas.updatePercentCreatedEnabled){
canvas.updatePercentCreated()
}}},"makeSomeViews",function($1,$2){
var $3=new Date().getTime();var $4=0;while(new Date().getTime()-$3<$2&&$1.length){
var $5=$1.length;var $6=$1[$5-1];var $7=$1[$5-2];var $8=false;if($7["__LZdeleted"]||$6[0]&&$6[0]["__LZdeleted"]){
$1.length-=2;continue
};try{
while(new Date().getTime()-$3<$2){
if($5!=$1.length){
break
};if(!$6.length){
$8=true;break
};var $12=$6.pop();if($12){
$7.makeChild($12,true);$4++
}}}
finally{

};if($8){
$1.length=$5-2;$7.__LZinstantiationDone()
}};return $4
},"trickleInstantiate",function($1,$2){
this.trickleQ.push($1,$2);this.checkUpdate()
},"createImmediate",function($1,$2){
var $3=this.newReverseArray($2);var $4=this.isimmediate;this.isimmediate=true;this.makeSomeViews([$1,$3],Infinity);this.isimmediate=$4
},"completeTrickle",function($1){
if(this.tricklingQ[0]==$1){
var $2=this.isimmediate;this.isimmediate=true;this.makeSomeViews(this.tricklingQ,Infinity);this.isimmediate=$2;this.tricklingQ=[]
}else{
var $3=this.trickleQ;var $4=$3.length;for(var $5=0;$5<$4;$5+=2){
if($3[$5]==$1){
var $6=$3[$5+1];$3.splice($5,2);this.createImmediate($1,$6);return
}}}},"halt",function(){
this.isUpdating=false;this.halted=true;this.checkQDel.unregisterAll()
},"resume",function(){
this.halted=false;this.checkUpdate()
},"drainQ",function($1){
var $2=this.timeout;var $3=this.trickletime;var $4=this.halted;this.timeout=$1;this.trickletime=$1;this.halted=false;this.isUpdating=true;this.checkQ();this.halted=$4;this.timeout=$2;this.trickletime=$3;return !this.isUpdating
}],["LzInstantiator",void 0]);(function($1){
with($1){
with($1.prototype){
LzInstantiatorService.LzInstantiator=new LzInstantiatorService()
}}})(LzInstantiatorService);lz.Instantiator=LzInstantiatorService.LzInstantiator;Class.make("LzGlobalMouseService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"onmousemove",LzDeclaredEvent,"onmouseup",LzDeclaredEvent,"onmouseupoutside",LzDeclaredEvent,"onmouseover",LzDeclaredEvent,"onmouseout",LzDeclaredEvent,"onmousedown",LzDeclaredEvent,"onmousedragin",LzDeclaredEvent,"onmousedragout",LzDeclaredEvent,"onmouseleave",LzDeclaredEvent,"onmouseenter",LzDeclaredEvent,"onclick",LzDeclaredEvent,"ondblclick",LzDeclaredEvent,"__movecounter",0,"__mouseEvent",function($1,$2){
if($1=="onmouseleave"){
canvas.onmouseleave.sendEvent()
}else{
if($1=="onmousemove"){
this.__movecounter++
}};var $3=this[$1];if($3){
if($3.ready){
$3.sendEvent($2)
}}else{

}}],["LzGlobalMouse",void 0]);(function($1){
with($1){
with($1.prototype){
LzGlobalMouseService.LzGlobalMouse=new LzGlobalMouseService()
}}})(LzGlobalMouseService);lz.GlobalMouseService=LzGlobalMouseService;lz.GlobalMouse=LzGlobalMouseService.LzGlobalMouse;Class.make("LzBrowserService",null,["capabilities",LzSprite.prototype.capabilities,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"loadURL",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;

};LzBrowserKernel.loadURL($1,$2,$3)
},"loadJS",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};LzBrowserKernel.loadJS.apply(null,arguments)
},"callJS",function($1,$2,$3){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;

};try{
return LzBrowserKernel.callJS.apply(null,arguments)
}
catch(e){
return null
}},"getVersion",function(){
return LzBrowserKernel.getVersion()
},"getOS",function(){
return LzBrowserKernel.getOS()
},"getLoadURL",function(){
return LzBrowserKernel.getLoadURL()
},"getInitArg",function($1){
return LzBrowserKernel.getInitArg($1)
},"getAppID",function(){
return LzBrowserKernel.getAppID()
},"showMenu",function($1){
if(this.capabilities.runtimemenus){
LzBrowserKernel.showMenu($1)
}else{

}},"setClipboard",function($1){
if(this.capabilities.setclipboard){
LzBrowserKernel.setClipboard($1)
}else{

}},"isAAActive",function(){
if(this.capabilities.accessibility){
return LzBrowserKernel.isAAActive()
}else{
return false
}},"updateAccessibility",function(){
if(this.capabilities.accessibility){
LzBrowserKernel.updateAccessibility()
}else{

}},"loadProxyPolicy",function($1){
if(this.capabilities.proxypolicy){
LzBrowserKernel.loadProxyPolicy($1)
}else{

}},"postToLps",true,"parsedloadurl",null,"defaultPortNums",{http:80,https:443},"getBaseURL",function($1,$2){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;

};var $3=this.getLoadURLAsLzURL();if($1){
$3.protocol="https"
};if($2){
$3.port=$2
}else{
if($1&&$2==null){
$3.port=this.defaultPortNums[$3.protocol]
}};$3.query=null;return $3
},"getLoadURLAsLzURL",function(){
if(!this.parsedloadurl){
this.parsedloadurl=new LzURL(this.getLoadURL())
};return this.parsedloadurl.dupe()
},"toAbsoluteURL",function($1,$2){
if($1.indexOf("://")>-1||$1.indexOf("/@WEBAPP@/")==0||$1.indexOf("file:")==0){
return $1
};var $3=this.getLoadURLAsLzURL();$3.query=null;if($1.indexOf(":")>-1){
var $4=$1.indexOf(":");var $5=$3.protocol=="https";$3.protocol=$1.substring(0,$4);if($2||$5){
if($3.protocol=="http"){
$3.protocol="https"
}};var $6=$1.substring($4+1,$1.length);if($6.charAt(0)=="/"){
$3.path=$1.substring($4+1);$3.file=null
}else{
$3.file=$1.substring($4+1)
}}else{
if($1.charAt(0)=="/"){
$3.path=$1;$3.file=null
}else{
$3.file=$1
}};return $3.toString()
},"xmlEscape",function($1){
return LzDataElement.__LZXMLescape($1)
},"urlEscape",function($1){
return encodeURIComponent($1)
},"urlUnescape",function($1){
return decodeURIComponent($1)
},"usePost",function(){
return this.postToLps&&this.supportsPost()
},"supportsPost",function(){
return true
},"makeProxiedURL",function($1){
var $2=$1.headers;var $3=$1.postbody;var $4=$1.proxyurl;var $5=$1.serverproxyargs;var $6;if($5){
$6={url:this.toAbsoluteURL($1.url,$1.secure),lzt:$1.service,reqtype:$1.httpmethod.toUpperCase()};for(var $7 in $5){
$6[$7]=$5[$7]
}}else{
$6={url:this.toAbsoluteURL($1.url,$1.secure),lzt:$1.service,reqtype:$1.httpmethod.toUpperCase(),sendheaders:$1.sendheaders,trimwhitespace:$1.trimwhitespace,nsprefix:$1.trimwhitespace,timeout:$1.timeout,cache:$1.cacheable,ccache:$1.ccache}};if($3!=null){
$6.lzpostbody=$3
};$6.lzr=$runtime;if($2!=null){
var $8="";for(var $9 in $2){
$8+=$9+": "+$2[$9]+"\n"
};if($8!=""){
$6["headers"]=$8
}};if(!$1.ccache){
$6.__lzbc__=new Date().getTime()
};var $10="?";for(var $11 in $6){
var $12=$6[$11];if(typeof $12=="string"){
$12=encodeURIComponent($12);$12=$12.replace(LzDataset.slashPat,"%2F")
};$4+=$10+$11+"="+$12;$10="&"
};return $4
}],["LzBrowser",void 0]);(function($1){
with($1){
with($1.prototype){
LzBrowserService.LzBrowser=new LzBrowserService()
}}})(LzBrowserService);lz.BrowserService=LzBrowserService;lz.Browser=LzBrowserService.LzBrowser;Class.make("LzContextMenu",LzNode,["onmenuopen",LzDeclaredEvent,"kernel",null,"items",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 0:
$1=null;
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($1):$1 instanceof LzNode)?$1:null,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($1):$1 instanceof LzNode)?$2:{delegate:$1},$3,$4)
},"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);this.kernel=new LzContextMenuKernel(this);this.items=[];var $3=$2&&$2["delegate"]||null;delete $2["delegate"];this.$lzc$set_delegate($3)
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);var $1=this.immediateparent;if($1&&(LzView["$lzsc$isa"]?LzView.$lzsc$isa($1):$1 instanceof LzView)){
$1.$lzc$set_contextmenu(this)
}},"$lzc$set_delegate",function($1){
this.kernel.setDelegate($1)
},"setDelegate",function($1){
this.$lzc$set_delegate($1)
},"addItem",function($1){
this.items.push($1);this.kernel.addItem($1)
},"hideBuiltInItems",function(){
this.kernel.hideBuiltInItems()
},"showBuiltInItems",function(){
this.kernel.showBuiltInItems()
},"clearItems",function(){
this.items=[];this.kernel.clearItems()
},"getItems",function(){
return this.items
},"makeMenuItem",function($1,$2){
var $3=new LzContextMenuItem($1,$2);return $3
}],["tagname","contextmenu","attributes",new LzInheritedHash(LzNode.attributes)]);(function($1){
with($1){
with($1.prototype){
LzContextMenu.attributes.ignoreplacement=true
}}})(LzContextMenu);lz[LzContextMenu.tagname]=LzContextMenu;Class.make("LzContextMenuItem",LzNode,["onselect",LzDeclaredEvent,"kernel",null,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 1:
$2=null;
case 2:
$3=null;
case 3:
$4=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($1):$1 instanceof LzNode)?$1:null,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($1):$1 instanceof LzNode)?$2:{title:$1,delegate:$2},$3,$4)
},"construct",function($1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$1,$2);var $3=$2&&$2["title"]||"";delete $2["title"];var $4=$2&&$2["delegate"]||null;delete $2["delegate"];this.kernel=new LzContextMenuItemKernel(this,$3,$4);var $5=this.immediateparent;if($5&&(LzContextMenu["$lzsc$isa"]?LzContextMenu.$lzsc$isa($5):$5 instanceof LzContextMenu)){
$5.addItem(this)
}},"$lzc$set_delegate",function($1){
this.kernel.setDelegate($1)
},"$lzc$set_caption",function($1){
this.kernel.setCaption($1)
},"$lzc$set_enabled",function($1){
this.kernel.setEnabled($1)
},"$lzc$set_separatorbefore",function($1){
this.kernel.setSeparatorBefore($1)
},"$lzc$set_visible",function($1){
this.kernel.setVisible($1)
},"setDelegate",function($1){
this.$lzc$set_delegate($1)
},"setCaption",function($1){
this.$lzc$set_caption($1)
},"getCaption",function(){
return this.kernel.getCaption()
},"setEnabled",function($1){
this.$lzc$set_enabled($1)
},"setSeparatorBefore",function($1){
this.$lzc$set_separatorbefore($1)
},"setVisible",function($1){
this.$lzc$set_visible($1)
}],["tagname","contextmenuitem","attributes",new LzInheritedHash(LzNode.attributes)]);(function($1){
with($1){
with($1.prototype){

}}})(LzContextMenuItem);lz[LzContextMenuItem.tagname]=LzContextMenuItem;Class.make("LzModeManagerService",LzEventable,["onmode",LzDeclaredEvent,"__LZlastclick",null,"__LZlastClickTime",0,"willCall",false,"eventsLocked",false,"modeArray",new Array(),"remotedebug",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);LzMouseKernel.setCallback(this,"rawMouseEvent")
},"makeModal",function($1){
if($1&&(this.modeArray.length==0||!this.hasMode($1))){
this.modeArray.push($1);if(this.onmode.ready){
this.onmode.sendEvent($1)
};var $2=lz.Focus.getFocus();if($2&&!$2.childOf($1)){
lz.Focus.clearFocus()
}}},"release",function($1){
var $2=this.modeArray;for(var $3=$2.length-1;$3>=0;$3--){
if($2[$3]===$1){
$2.splice($3,$2.length-$3);var $4=$2[$3-1];if(this.onmode.ready){
this.onmode.sendEvent($4||null)
};var $5=lz.Focus.getFocus();if($4&&$5&&!$5.childOf($4)){
lz.Focus.clearFocus()
};return
}}},"releaseAll",function(){
this.modeArray=new Array();if(this.onmode.ready){
this.onmode.sendEvent(null)
}},"handleMouseEvent",function($1,$2){
if($2=="onmouseup"){
lz.Track.__LZmouseup(null)
};if($1==null){
$1=this.__findInputtextSelection()
};lz.GlobalMouse.__mouseEvent($2,$1);if($1==null||this.eventsLocked){
return
};var $3=true;for(var $4=this.modeArray.length-1;$3&&$4>=0;--$4){
var $5=this.modeArray[$4];if(!$5){
continue
};if($1.childOf($5)){
break
}else{
$3=$5.passModeEvent?$5.passModeEvent($2,$1):false
}};if($3){
if($2=="onclick"){
if(this.__LZlastclick===$1&&$1.ondblclick.ready&&LzTimeKernel.getTimer()-this.__LZlastClickTime<$1.DOUBLE_CLICK_TIME){
$2="ondblclick";lz.GlobalMouse.__mouseEvent($2,$1);this.__LZlastclick=null
}else{
this.__LZlastclick=$1;this.__LZlastClickTime=LzTimeKernel.getTimer()
}};$1.mouseevent($2);if($2=="onmousedown"){
lz.Focus.__LZcheckFocusChange($1)
}}},"__LZallowInput",function($1,$2){
return $2.childOf($1)
},"__LZallowFocus",function($1){
var $2=this.modeArray.length;return $2==0||$1.childOf(this.modeArray[$2-1])
},"globalLockMouseEvents",function(){
this.eventsLocked=true
},"globalUnlockMouseEvents",function(){
this.eventsLocked=false
},"hasMode",function($1){
var $2=this.modeArray;for(var $3=$2.length-1;$3>=0;$3--){
if($1===$2[$3]){
return true
}};return false
},"getModalView",function(){
return this.modeArray[this.modeArray.length-1]||null
},"__findInputtextSelection",function(){
return LzInputTextSprite.findSelection()
},"rawMouseEvent",function($1,$2){
if($1=="onmousemove"){
lz.GlobalMouse.__mouseEvent("onmousemove",null)
}else{
this.handleMouseEvent($2,$1)
}}],["LzModeManager",void 0]);(function($1){
with($1){
with($1.prototype){
LzModeManagerService.LzModeManager=new LzModeManagerService()
}}})(LzModeManagerService);lz.ModeManagerService=LzModeManagerService;lz.ModeManager=LzModeManagerService.LzModeManager;Class.make("LzCursorService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"showHandCursor",function($1){
LzMouseKernel.showHandCursor($1)
},"setCursorGlobal",function($1){
LzMouseKernel.setCursorGlobal($1)
},"lock",function(){
LzMouseKernel.lock()
},"unlock",function(){
LzMouseKernel.unlock()
},"restoreCursor",function(){
LzMouseKernel.restoreCursor()
}],["LzCursor",void 0]);(function($1){
with($1){
with($1.prototype){
LzCursorService.LzCursor=new LzCursorService()
}}})(LzCursorService);lz.CursorService=LzCursorService;lz.Cursor=LzCursorService.LzCursor;Class.make("LzURL",null,["protocol",null,"host",null,"port",null,"path",null,"file",null,"query",null,"fragment",null,"_parsed",null,"$lzsc$initialize",function($1){
switch(arguments.length){
case 0:
$1=null;

};if($1!=null){
this.parseURL($1)
}},"parseURL",function($1){
if(this._parsed==$1){
return
};this._parsed=$1;var $2=0;var $3=$1.indexOf(":");var $4=$1.indexOf("?",$2);var $5=$1.indexOf("#",$2);var $6=$1.length;if($5!=-1){
$6=$5
};if($4!=-1){
$6=$4
};if($3!=-1){
this.protocol=$1.substring($2,$3);if($1.substring($3+1,$3+3)=="//"){
$2=$3+3;$3=$1.indexOf("/",$2);if($3==-1){
$3=$6
};var $7=$1.substring($2,$3);var $8=$7.indexOf(":");if($8==-1){
this.host=$7;this.port=null
}else{
this.host=$7.substring(0,$8);this.port=$7.substring($8+1)
}}else{
$3++
};$2=$3
};$3=$6;this._splitPath($1.substring($2,$3));if($5!=-1){
this.fragment=$1.substring($5+1,$1.length)
}else{
$5=$1.length
};if($4!=-1){
this.query=$1.substring($4+1,$5)
}},"_splitPath",function($1){
if($1==""){
return
};var $2=$1.lastIndexOf("/");if($2!=-1){
this.path=$1.substring(0,$2+1);this.file=$1.substring($2+1,$1.length);if(this.file==""){
this.file=null
};return
};this.path=null;this.file=$1
},"dupe",function(){
var $1=new LzURL();$1.protocol=this.protocol;$1.host=this.host;$1.port=this.port;$1.path=this.path;$1.file=this.file;$1.query=this.query;$1.fragment=this.fragment;return $1
},"toString",function(){
var $1="";if(this.protocol!=null){
$1+=this.protocol+":";if(this.host!=null){
$1+="//"+this.host;if(null!=this.port&&lz.Browser.defaultPortNums[this.protocol]!=this.port){
$1+=":"+this.port
}}};if(this.path!=null){
$1+=this.path
};if(null!=this.file){
$1+=this.file
};if(null!=this.query){
$1+="?"+this.query
};if(null!=this.fragment){
$1+="#"+this.fragment
};return $1
}],["merge",function($1,$2){
var $3=new LzURL();var $4={protocol:true,host:true,port:true,path:true,file:true,query:true,fragment:true};for(var $5 in $4){
$3[$5]=$1[$5]!=null?$1[$5]:$2[$5]
};return $3
}]);lz.URL=LzURL;Class.make("LzKeysService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);LzKeyboardKernel.setCallback(this,"__keyEvent");if(lz.embed["mousewheel"]){
lz.embed.mousewheel.setCallback(this,"__mousewheelEvent")
}},"downKeysHash",{},"downKeysArray",[],"keycombos",{},"onkeydown",LzDeclaredEvent,"onkeyup",LzDeclaredEvent,"onmousewheeldelta",LzDeclaredEvent,"codemap",{shift:16,control:17,alt:18},"ctrlKey",false,"__keyEvent",function($1,$2,$3,$4){
switch(arguments.length){
case 3:
$4=false;

};this.ctrlKey=$4;var $5=this.codemap;for(var $6 in $1){
var $7=$1[$6];if($5[$6]!=null){
$2=$5[$6]
};if($7){
this.gotKeyDown($2)
}else{
this.gotKeyUp($2)
}}},"__allKeysUp",function(){
LzKeyboardKernel.__allKeysUp()
},"gotKeyDown",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};var $3=this.downKeysHash;var $4=this.downKeysArray;var $5=!$3[$1];if($5){
$3[$1]=true;$4.push($1);$4.sort()
};if($5||$2!="extra"){
if($3[229]!=true){
if(this.onkeydown.ready){
this.onkeydown.sendEvent($1)
}}};if($5){
var $6=this.keycombos;for(var $7=0;$7<$4.length&&$6!=null;$7++){
$6=$6[$4[$7]]
};if($6!=null&&("delegates" in $6)){
var $8=$6.delegates;for(var $7=0;$7<$8.length;$7++){
$8[$7].execute($4)
}}}},"gotKeyUp",function($1){
var $2=this.downKeysHash;var $3=$2[$1];delete $2[$1];var $4=this.downKeysArray;$4.length=0;for(var $5 in $2){
$4.push($5)
};if($3&&this.onkeyup.ready){
this.onkeyup.sendEvent($1)
}},"isKeyDown",function($1){
if(typeof $1=="string"){
return this.downKeysHash[this.keyCodes[$1.toLowerCase()]]==true
}else{
var $2=true;var $3=this.downKeysHash;var $4=this.keyCodes;for(var $5=0;$5<$1.length;$5++){
$2=$2&&$3[$4[$1[$5].toLowerCase()]]==true
};return $2
}},"callOnKeyCombo",function($1,$2){
var $3=this.keyCodes;var $4=[];for(var $5=0;$5<$2.length;$5++){
$4.push($3[$2[$5].toLowerCase()])
};$4.sort();var $6=this.keycombos;for(var $5=0;$5<$4.length;$5++){
var $7=$6[$4[$5]];if($7==null){
$6[$4[$5]]=$7={delegates:[]}};$6=$7
};$6.delegates.push($1)
},"removeKeyComboCall",function($1,$2){
var $3=this.keyCodes;var $4=[];for(var $5=0;$5<$2.length;$5++){
$4.push($3[$2[$5].toLowerCase()])
};$4.sort();var $6=this.keycombos;for(var $5=0;$5<$4.length;$5++){
$6=$6[$4[$5]];if($6==null){
return false
}};for(var $5=$6.delegates.length-1;$5>=0;$5--){
if($6.delegates[$5]==$1){
$6.delegates.splice($5,1)
}}},"enableEnter",function($1){

},"mousewheeldelta",0,"__mousewheelEvent",function($1){
this.mousewheeldelta=$1;if(this.onmousewheeldelta.ready){
this.onmousewheeldelta.sendEvent($1)
}},"gotLastFocus",function($1){
LzKeyboardKernel.gotLastFocus()
},"setGlobalFocusTrap",function($1){
LzKeyboardKernel.setGlobalFocusTrap($1)
},"keyCodes",{"0":48,")":48,";":186,":":186,"1":49,"!":49,"=":187,"+":187,"2":50,"@":50,"<":188,",":188,"3":51,"#":51,"-":189,"_":189,"4":52,"$":52,">":190,".":190,"5":53,"%":53,"/":191,"?":191,"6":54,"^":54,"`":192,"~":192,"7":55,"&":55,"[":219,"{":219,"8":56,"*":56,"\\":220,"|":220,"9":57,"(":57,"]":221,"}":221,'"':222,"'":222,a:65,b:66,c:67,d:68,e:69,f:70,g:71,h:72,i:73,j:74,k:75,l:76,m:77,n:78,o:79,p:80,q:81,r:82,s:83,t:84,u:85,v:86,w:87,x:88,y:89,z:90,numbpad0:96,numbpad1:97,numbpad2:98,numbpad3:99,numbpad4:100,numbpad5:101,numbpad6:102,numbpad7:103,numbpad8:104,numbpad9:105,multiply:106,"add":107,subtract:109,decimal:110,divide:111,f1:112,f2:113,f3:114,f4:115,f5:116,f6:117,f7:118,f8:119,f9:120,f10:121,f11:122,f12:123,backspace:8,tab:9,clear:12,enter:13,shift:16,control:17,alt:18,"pause":19,"break":19,capslock:20,esc:27,spacebar:32,pageup:33,pagedown:34,end:35,home:36,leftarrow:37,uparrow:38,rightarrow:39,downarrow:40,insert:45,"delete":46,help:47,numlock:144,screenlock:145,"IME":229}],["LzKeys",void 0]);(function($1){
with($1){
with($1.prototype){
LzKeysService.LzKeys=new LzKeysService()
}}})(LzKeysService);lz.KeysService=LzKeysService;lz.Keys=LzKeysService.LzKeys;Class.make("LzAudioService",null,["capabilities",LzSprite.prototype.capabilities,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"playSound",function($1){
if(this.capabilities.audio){
LzAudioKernel.playSound($1)
}else{

}},"stopSound",function(){
if(this.capabilities.audio){
LzAudioKernel.stopSound()
}else{

}},"startSound",function(){
if(this.capabilities.audio){
LzAudioKernel.startSound()
}else{

}},"getVolume",function(){
if(this.capabilities.audio){
return LzAudioKernel.getVolume()
}else{

};return NaN
},"setVolume",function($1){
if(this.capabilities.audio){
LzAudioKernel.setVolume($1)
}else{

}},"getPan",function(){
if(this.capabilities.audio){
return LzAudioKernel.getPan()
}else{

};return NaN
},"setPan",function($1){
if(this.capabilities.audio){
LzAudioKernel.setPan($1)
}else{

}}],["LzAudio",void 0]);(function($1){
with($1){
with($1.prototype){
LzAudioService.LzAudio=new LzAudioService()
}}})(LzAudioService);lz.AudioService=LzAudioService;lz.Audio=LzAudioService.LzAudio;Class.make("LzHistoryService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"isReady",false,"ready",false,"onready",LzDeclaredEvent,"persist",false,"_persistso",null,"offset",0,"__lzdirty",false,"__lzhistq",[],"__lzcurrstate",{},"capabilities",LzSprite.prototype.capabilities,"onoffset",LzDeclaredEvent,"receiveHistory",function($1){
if(this.persist&&!this._persistso){
this.__initPersist()
};var $2=this.__lzhistq.length;var $3=$1*1;if(!$3){
$3=0
}else{
if($3>$2-1){
$3=$2
}};var $4=this.__lzhistq[$3];for(var $5 in $4){
var $6=$4[$5];var $lzsc$1170482869=global[$6.c];var $lzsc$983831048=$6.n;var $lzsc$1952750469=$6.v;if(!$lzsc$1170482869.__LZdeleted){
var $lzsc$2001989128="$lzc$set_"+$lzsc$983831048;if(Function["$lzsc$isa"]?Function.$lzsc$isa($lzsc$1170482869[$lzsc$2001989128]):$lzsc$1170482869[$lzsc$2001989128] instanceof Function){
$lzsc$1170482869[$lzsc$2001989128]($lzsc$1952750469)
}else{
$lzsc$1170482869[$lzsc$983831048]=$lzsc$1952750469;var $lzsc$300212863=$lzsc$1170482869["on"+$lzsc$983831048];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$300212863):$lzsc$300212863 instanceof LzEvent){
if($lzsc$300212863.ready){
$lzsc$300212863.sendEvent($lzsc$1952750469)
}}}}};if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_offset"]):this["$lzc$set_offset"] instanceof Function){
this["$lzc$set_offset"]($3)
}else{
this["offset"]=$3;var $lzsc$522534640=this["onoffset"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$522534640):$lzsc$522534640 instanceof LzEvent){
if($lzsc$522534640.ready){
$lzsc$522534640.sendEvent($3)
}}}};return $3
},"receiveEvent",function($1,$2){
if(!canvas.__LZdeleted){
var $lzsc$1736243724="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa(canvas[$lzsc$1736243724]):canvas[$lzsc$1736243724] instanceof Function){
canvas[$lzsc$1736243724]($2)
}else{
canvas[$1]=$2;var $lzsc$2027237372=canvas["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$2027237372):$lzsc$2027237372 instanceof LzEvent){
if($lzsc$2027237372.ready){
$lzsc$2027237372.sendEvent($2)
}}}}},"getCanvasAttribute",function($1){
return canvas[$1]
},"setCanvasAttribute",function($1,$2){
this.receiveEvent($1,$2)
},"callMethod",function($1){
return LzBrowserKernel.callMethod($1)
},"save",function($1,$2,$3){
if(typeof $1!="string"){
if($1["id"]){
$1=$1["id"]
};if(!$1){
return
}};if($3==null){
$3=global[$1][$2]
};this.__lzcurrstate[$1]={c:$1,n:$2,v:$3};this.__lzdirty=true
},"commit",function(){
if(!this.__lzdirty){
return
};this.__lzhistq[this.offset]=this.__lzcurrstate;this.__lzhistq.length=this.offset+1;if(this.persist){
if(!this._persistso){
this.__initPersist()
};this._persistso.data[this.offset]=this.__lzcurrstate
};this.__lzcurrstate={};this.__lzdirty=false
},"move",function($1){
switch(arguments.length){
case 0:
$1=1;

};this.commit();var $2=this.offset+$1;if(0>=$2){
$2=0
};if(this.__lzhistq.length>=$2){
LzBrowserKernel.setHistory($2)
}},"next",function(){
this.move(1)
},"prev",function(){
this.move(-1)
},"__initPersist",function(){
if(this.persist){
if(!this._persistso){
this._persistso=LzBrowserKernel.getPersistedObject("historystate")
};if(this._persistso&&this._persistso.data){
var $1=this._persistso.data;this.__lzhistq=[];for(var $2 in $1){
this.__lzhistq[$2]=$1[$2]
}}}else{
if(this._persistso){
this._persistso=null
}}},"clear",function(){
if(this.persist){
if(!this._persistso){
this._persistso=LzBrowserKernel.getPersistedObject("historystate")
};this._persistso.clear()
};this.__lzhistq=[];this.offset=0;if(this.onoffset.ready){
this.onoffset.sendEvent(0)
}},"setPersist",function($1){
if(this.capabilities.persistence){
this.persist=$1
}else{

}},"__start",function($1){
lz.Browser.callJS("lz.embed.history.listen('"+$1+"')");this.isReady=true;this.ready=true;if(this.onready.ready){
this.onready.sendEvent(true)
}}],["LzHistory",void 0]);(function($1){
with($1){
with($1.prototype){
LzHistoryService.LzHistory=new LzHistoryService()
}}})(LzHistoryService);lz.HistoryService=LzHistoryService;lz.History=LzHistoryService.LzHistory;Class.make("LzTrackService",LzEventable,["__LZreg",new Object(),"__LZactivegroups",null,"__LZtrackDel",null,"__LZmouseupDel",null,"__LZdestroydel",null,"__LZlastmouseup",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.__LZtrackDel=new LzDelegate(this,"__LZtrack");this.__LZmouseupDel=new LzDelegate(this,"__LZmouseup",lz.GlobalMouse,"onmouseup");this.__LZdestroydel=new LzDelegate(this,"__LZdestroyitem");this.__LZactivegroups=[]
},"register",function($1,$2){
if($1==null||$2==null){
return
};var $3=this.__LZreg[$2];if(!$3){
this.__LZreg[$2]=$3=[];$3.__LZlasthit=null;$3.__LZactive=false
};$3.push($1);this.__LZdestroydel.register($1,"ondestroy")
},"unregister",function($1,$2){
if($1==null||$2==null){
return
};var $3=this.__LZreg[$2];if($3){
for(var $4=0;$4<$3.length;$4++){
if($3[$4]==$1){
if($3.__LZlasthit==$1){
if(this.__LZlastmouseup==$1){
this.__LZlastmouseup=null
};$3.__LZlasthit=null
};$3.splice($4,1)
}};if($3.length==0){
if($3.__LZactive){
this.deactivate($2)
};delete this.__LZreg[$2]
}};this.__LZdestroydel.unregisterFrom($1.ondestroy)
},"__LZdestroyitem",function($1){
for(var $2 in this.__LZreg){
this.unregister($1,$2)
}},"activate",function($1){
var $2=this.__LZreg[$1];if($2&&!$2.__LZactive){
$2.__LZactive=true;var $3=this.__LZactivegroups;if($3.length==0){
this.__LZtrackDel.register(lz.Idle,"onidle")
};$3.push($2)
}},"deactivate",function($1){
var $2=this.__LZreg[$1];if($2&&$2.__LZactive){
var $3=this.__LZactivegroups;for(var $4=0;$4<$3.length;++$4){
if($3[$4]==$2){
$3.splice($4,1);break
}};if($3.length==0){
this.__LZtrackDel.unregisterAll()
};$2.__LZactive=false;if(this.__LZlastmouseup==$2.__LZlasthit){
this.__LZlastmouseup=null
};$2.__LZlasthit=null
}},"__LZtopview",function($1,$2){
var $3=$1;var $4=$2;while($3.nodeLevel<$4.nodeLevel){
$4=$4.immediateparent;if($4==$1){
return $2
}};while($4.nodeLevel<$3.nodeLevel){
$3=$3.immediateparent;if($3==$2){
return $1
}};while($3.immediateparent!=$4.immediateparent){
$3=$3.immediateparent;$4=$4.immediateparent
};return $3.getZ()>$4.getZ()?$1:$2
},"__LZfindTopmost",function($1){
var $2=$1[0];for(var $3=1;$3<$1.length;$3++){
$2=this.__LZtopview($2,$1[$3])
};return $2
},"__LZtrackgroup",function($1,$2){
for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3];if($4.visible){
var $5=$4.getMouse(null);if($4.containsPt($5.x,$5.y)){
$2.push($4)
}}}},"__LZtrack",function($1){
var $2=[];var $3=this.__LZactivegroups;for(var $4=0;$4<$3.length;++$4){
var $5=$3[$4];var $6=[];this.__LZtrackgroup($5,$6);var $7=$5.__LZlasthit;if($6.length){
var $8=this.__LZfindTopmost($6);if($8==$7){
continue
};$2.push($8)
}else{
var $8=null
};if($7){
var $9=$7.onmousetrackout;if($9.ready){
$9.sendEvent($7)
}};$5.__LZlasthit=$8
};for(var $4=0,$10=$2.length;$4<$10;++$4){
var $11=$2[$4];if($11.onmousetrackover.ready){
$11.onmousetrackover.sendEvent($11)
}}},"__LZmouseup",function($1){
var $2=this.__LZactivegroups.slice();for(var $3=0;$3<$2.length;++$3){
var $4=$2[$3].__LZlasthit;if($4){
var $5=$4.onmousetrackup;if($5.ready){
if(this.__LZlastmouseup==$4){
this.__LZlastmouseup=null
}else{
this.__LZlastmouseup=$4;$5.sendEvent($4)
}}}}}],["LzTrack",void 0]);(function($1){
with($1){
with($1.prototype){
LzTrackService.LzTrack=new LzTrackService()
}}})(LzTrackService);lz.TrackService=LzTrackService;lz.Track=LzTrackService.LzTrack;Class.make("LzIdleEvent",LzEvent,["registered",false,"$lzsc$initialize",function($1,$2,$3){
switch(arguments.length){
case 2:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3);if(this.ready&&!this.registered){
this.registered=true;LzIdleKernel.addCallback(this,"sendEvent")
}},"addDelegate",function($1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["addDelegate"]||this.nextMethod(arguments.callee,"addDelegate")).call(this,$1);if(this.ready&&!this.registered){
this.registered=true;LzIdleKernel.addCallback(this,"sendEvent")
}},"removeDelegate",function($1){
switch(arguments.length){
case 0:
$1=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["removeDelegate"]||this.nextMethod(arguments.callee,"removeDelegate")).call(this,$1);if(!this.ready&&this.registered){
LzIdleKernel.removeCallback(this,"sendEvent");this.registered=false
}},"clearDelegates",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["clearDelegates"]||this.nextMethod(arguments.callee,"clearDelegates")).call(this);if(!this.ready&&this.registered){
LzIdleKernel.removeCallback(this,"sendEvent");this.registered=false
}}],null);Class.make("LzIdleService",LzEventable,["coi",void 0,"regNext",false,"removeCOI",null,"onidle",void 0,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.coi=new Array();this.removeCOI=new LzDelegate(this,"removeCallIdleDelegates");this.onidle=new LzIdleEvent(this,"onidle")
},"callOnIdle",function($1){
this.coi.push($1);if(!this.regNext){
this.regNext=true;this.removeCOI.register(this,"onidle")
}},"removeCallIdleDelegates",function($1){
var $2=this.coi.slice(0);this.coi.length=0;for(var $3=0;$3<$2.length;$3++){
$2[$3].execute($1)
};if(this.coi.length==0){
this.removeCOI.unregisterFrom(this.onidle);this.regNext=false
}},"setFrameRate",function($1){
switch(arguments.length){
case 0:
$1=30;

};LzIdleKernel.setFrameRate($1)
}],["LzIdle",void 0]);(function($1){
with($1){
with($1.prototype){
LzIdleService.LzIdle=new LzIdleService()
}}})(LzIdleService);lz.IdleService=LzIdleService;lz.Idle=LzIdleService.LzIdle;Class.make("LzCSSStyleRule",null,["selector",void 0,"properties",void 0,"$lzsc$initialize",function($1,$2,$3,$4){
switch(arguments.length){
case 2:
$3=null;
case 3:
$4=null;

};this.selector=$1;this.properties=$2
},"specificity",0,"parsed",null,"_lexorder",void 0,"getSpecificity",function(){
if(!this.specificity){
var $1=0;var $2=this.parsed;if($2.type==LzCSSStyle._sel_compound){
for(var $3=0,$4=$2.length;$3<$4;$3++){
$1+=LzCSSStyle.getSelectorSpecificity($2[$3])
}}else{
$1=LzCSSStyle.getSelectorSpecificity($2)
};this.specificity=$1
};return this.specificity
},"_dbg_name",function(){
var $1;$1=function($1){
var $2=$1["tagname"];var $3=$1["id"];var $4=$1["attrname"];if(!($2||$3||$4)){
return "*"
};return($2?$2:"")+($3?"#"+$3:"")+($4?"["+$4+"="+$1.attrvalue+"]":"")
};var $2=this.parsed;if($2["length"]){
var $3="";for(var $4=0;$4<$2.length;$4++){
$3+=$1($2[$4])+" "
};$3=$3.substring(0,$3.length-1)
}else{
var $3=$1($2)
};return $3
},"equal",function($1){
var $2;$2=function($1,$2){
return $1["tagname"]==$2["tagname"]&&$1["id"]==$2["id"]&&$1["attrname"]==$2["attrname"]&&$1["attrvalue"]==$2["attrvalue"]
};var $3=this.parsed;var $4=$1.parsed;if($3["length"]!=$4["length"]){
return false
};if($3["length"]){
for(var $5=$3.length-1;$5>=0;$5--){
if(!$2($3[$5],$4[$5])){
return false
}}};if(!$2($3,$4)){
return false
};var $6=this.properties;var $7=$1.properties;for(var $8 in $6){
if($6[$8]!==$7[$8]){
return false
}};for(var $9 in $7){
if($6[$9]!==$7[$9]){
return false
}};return true
}],null);(function($1){
with($1){
with($1.prototype){

}}})(LzCSSStyleRule);lz.CSSStyleRule=LzCSSStyleRule;Class.make("LzCSSStyleClass",null,["$lzsc$initialize",function(){

},"getComputedStyle",function($1){
var $2=new LzCSSStyleDeclaration();$2.setNode($1);return $2
},"getPropertyValueFor",function($1,$2){
var $3=$1["__LZPropertyCache"];if(!$3){
$3=this.getPropertyCache($1)
};if($2 in $3){
return $3[$2]
};return $3[$2]=void 0
},"getPropertyCache",function($1){
if(!$1||$1===canvas){
return {}};var $2=$1["__LZPropertyCache"];if($2){
return $2
};var $3=$1.immediateparent;if(!$3||$3===canvas){
var $4={}}else{
var $4=$3["__LZPropertyCache"]||this.getPropertyCache($3)
};var $5=$1["__LZRuleCache"];if(!$5){
$5=this.getRulesCache($1)
};if($5.length==0){
return $1.__LZPropertyCache=$4
};var $2={};for(var $6 in $4){
$2[$6]=$4[$6]
};for(var $7=$5.length-1;$7>=0;$7--){
var $8=$5[$7].properties;for(var $9 in $8){
$2[$9]=$8[$9]
}};return $1.__LZPropertyCache=$2
},"getRulesCache",function($1){
var $2=$1["__LZRuleCache"];if($2){
return $2
};$2=new Array();var $3=new Array();if(this._rulenum!=this._lastSort){
this._sortRules()
};var $4=$1["id"];if($4){
var $5=this._idRules;if($4 in $5){
$3=$3.concat($5[$4])
}};var $6=this._attrRules;for(var $7 in $6){
if($1[$7]){
var $8=$6[$7][$1[$7]];if(Array["$lzsc$isa"]?Array.$lzsc$isa($8):$8 instanceof Array){
$3=$3.concat($8)
}}};var $9=this._tagRules;for(var $10 in $9){
var $11=lz[$10];if($11&&($11["$lzsc$isa"]?$11.$lzsc$isa($1):$1 instanceof $11)){
$3=$3.concat($9[$10])
}};var $12=this._rules;for(var $13=$12.length-1;$13>=0;$13--){
$3.push($12[$13])
};var $14=false;var $15=Infinity;for(var $13=0,$16=$3.length;$13<$16;$13++){
var $17=$3[$13];if(!$14){
var $8=$17.specificity;if(!$8||$8>=$15){
$14=true
}else{
$15=$8
}};var $18=$17.parsed;var $19=$18.type;var $20=$19==this._sel_compound;if($20){
$18=$18[$18.length-1];$19=$18.type
};var $21=$18.tagname;var $22=$21?lz[$21]:null;var $23=$18["id"];var $24=$18["attrname"];if((!$21||$22&&($22["$lzsc$isa"]?$22.$lzsc$isa($1):$1 instanceof $22))&&(!$23||$1["id"]==$23)&&(!$24||$1[$24]==$18.attrvalue)){
if(!$20){
$2.push($17)
}else{
if(this._compoundSelectorApplies($17.parsed,$1)){
$2.push($17)
}}}};if($14){
$2.sort(this.__compareSpecificity)
};$1.__LZRuleCache=$2;return $2
},"getSelectorSpecificity",function($1){
switch($1.type){
case this._sel_tag:
case this._sel_star:
return 1;
case this._sel_id:
return 100;
case this._sel_attribute:
return 10;
case this._sel_tagAndAttr:
return 11;

}},"__compareSpecificity",function($1,$2){
var $3=$1.specificity;var $4=$2.specificity;if($3!=$4){
return $3<$4?1:-1
};var $5=$1.parsed;var $6=$2.parsed;var $7=$1._lexorder<$2._lexorder?1:-1;if(!$5["length"]&&!$6["length"]){
var $8=$5["tagname"];var $9=$6["tagname"];if(!$8||!$9||$8==$9){
return $7
};var $10=lz[$8];var $11=lz[$9];if($10&&$11){
if($11.prototype.isPrototypeOf($10.prototype)){
return -1
};if($10.prototype.isPrototypeOf($11.prototype)){
return 1
}};return $7
};for(var $12=0;$12<$5.length;$12++){
var $13=$5[$12];var $14=$6[$12];if(!$13||!$14){
break
};var $8=$13["tagname"];var $9=$14["tagname"];if($8&&$9&&$8!=$9){
var $10=lz[$8];var $11=lz[$9];if($10&&$11){
if($11.prototype.isPrototypeOf($10.prototype)){
return -1
};if($10.prototype.isPrototypeOf($11.prototype)){
return 1
}}}};return $7
},"_printRuleArray",function($1){

},"_compoundSelectorApplies",function($1,$2){
for(var $3=$2,$4=$1.length-1;$4>=0&&$3!==canvas;$4--,$3=$3.parent){
var $5=$1[$4];var $6=$5.tagname;var $7=$6?lz[$6]:null;var $8=$5["id"];var $9=$5["attrname"];while($3!==canvas){
if((!$6||$7&&($7["$lzsc$isa"]?$7.$lzsc$isa($3):$3 instanceof $7))&&(!$8||$3.id==$8)&&(!$9||$3[$9]==$5.attrvalue)){
if($4==0){
return true
}else{
break
}}else{
if($3===$2){
return false
}};$3=$3.parent
}};return false
},"_sel_unknown",0,"_sel_star",1,"_sel_id",2,"_sel_tag",3,"_sel_compound",4,"_sel_attribute",5,"_sel_tagAndAttr",6,"_rules",new Array(),"_attrRules",{},"_idRules",{},"_tagRules",{},"_rulenum",0,"_lastSort",-1,"_sortRules",function(){
var $1;$1=function($1){
for(var $2=$1.length-2;$2>=0;$2--){
if($1[$2].equal($1[$2+1])){
$1.splice($2+1,1)
}};return $1
};if(this._rulenum!=this._lastSort){
this._rules.sort(this.__compareSpecificity);$1(this._rules);for(var $2 in this._attrRules){
var $3=this._attrRules[$2];for(var $4 in $3){
$3[$4].sort(this.__compareSpecificity);$1($3[$4])
}};for(var $2 in this._idRules){
this._idRules[$2].sort(this.__compareSpecificity);$1(this._idRules[$2])
};for(var $2 in this._tagRules){
this._tagRules[$2].sort(this.__compareSpecificity);$1(this._tagRules[$2])
};this._lastSort=this._rulenum
}},"_addRule",function($1){
$1._lexorder=this._rulenum++;var $2=$1.selector;$1.parsed=null;var $3;if(Array["$lzsc$isa"]?Array.$lzsc$isa($2):$2 instanceof Array){
$1.parsed=[];$1.parsed.type=this._sel_compound;for(var $4=0;$4<$2.length;$4++){
$1.parsed.push(this._parseSelector($2[$4]))
};$3=$1.parsed[$1.parsed.length-1]
}else{
$1.parsed=this._parseSelector($2);$3=$1.parsed
};$1.getSpecificity();if($3.type==this._sel_attribute||$3.type==this._sel_tagAndAttr){
var $5=$3.attrname;var $6=this._attrRules[$5];if(!$6){
$6=this._attrRules[$5]={}};var $7=$3.attrvalue;var $8=$6[$7];if(!$8){
$8=$6[$7]=[]
};$8.push($1)
}else{
if($3.type==this._sel_id){
var $9=$3.id;if(!this._idRules[$9]){
this._idRules[$9]=[]
};this._idRules[$9].push($1)
}else{
if($3.type==this._sel_tag){
var $10=$3.tagname;if(!this._tagRules[$10]){
this._tagRules[$10]=[]
};this._tagRules[$10].push($1)
}else{
this._rules.push($1)
}}}},"_parseSelector",function($1){
switch(typeof $1){
case "object":
if($1.simpleselector){
$1.type=this._sel_tagAndAttr;$1.tagname=$1.simpleselector
}else{
$1.type=this._sel_attribute
};return $1;break;
case "string":
return this._parseStringSelector($1);break;

}},"_parseStringSelector",function($1){
var $2={};if($1=="*"){
$2.type=this._sel_star
}else{
var $3=$1.indexOf("#");if($3>=0){
$2.id=$1.substring($3+1);$2.type=this._sel_id
}else{
$2.type=this._sel_tag;$2.tagname=$1
}};return $2
}],null);(function($1){
with($1){
with($1.prototype){

}}})(LzCSSStyleClass);var LzCSSStyle=new LzCSSStyleClass();lz.CSSStyle=LzCSSStyleClass;Class.make("LzCSSStyleDeclaration",null,["$lzsc$initialize",function(){

},"_node",null,"getPropertyValue",function($1){
return LzCSSStyle.getPropertyValueFor(this._node,$1)
},"setNode",function($1){
this._node=$1
}],null);(function($1){
with($1){
with($1.prototype){

}}})(LzCSSStyleDeclaration);lz.CSSStyleDeclaration=LzCSSStyleDeclaration;Class.make("LzStyleSheet",null,["$lzsc$initialize",function($1,$2,$3,$4){
this.type=$4;this.disabled=false;this.ownerNode=null;this.parentStyleSheet=null;this.href=$2;this.title=$1;this.media=$3
},"type",null,"disabled",null,"ownerNode",null,"parentStyleSheet",null,"href",null,"title",null,"media",null],null);Class.make("LzCSSStyleSheet",LzStyleSheet,["$lzsc$initialize",function($1,$2,$3,$4,$5,$6){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$1,$2,$3,$4);this.ownerRule=$5;this.cssRules=$6
},"ownerRule",null,"cssRules",null,"insertRule",function($1,$2){
if(!this.cssRules){
this.cssRules=[]
};if($2<0){
return null
};if($2<this.cssRules.length){
this.cssRules.splice($2,0,$1);return $2
};if($2==this.cssRules.length){
return this.cssRules.push($1)-1
};return null
}],null);lz.CSSStyleSheet=LzCSSStyleSheet;Class.make("LzFocusService",LzEventable,["onfocus",LzDeclaredEvent,"onescapefocus",LzDeclaredEvent,"lastfocus",null,"csel",null,"cseldest",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.upDel=new LzDelegate(this,"gotKeyUp",lz.Keys,"onkeyup");this.downDel=new LzDelegate(this,"gotKeyDown",lz.Keys,"onkeydown");this.lastfocusDel=new LzDelegate(lz.Keys,"gotLastFocus",this,"onescapefocus")
},"upDel",void 0,"downDel",void 0,"lastfocusDel",void 0,"focuswithkey",false,"__LZskipblur",false,"__LZsfnextfocus",-1,"__LZsfrunning",false,"gotKeyUp",function($1){
if(this.csel&&this.csel.onkeyup.ready){
this.csel.onkeyup.sendEvent($1)
}},"gotKeyDown",function($1){
if(this.csel&&this.csel.onkeydown.ready){
this.csel.onkeydown.sendEvent($1)
};if($1==lz.Keys.keyCodes.tab){
if(lz.Keys.isKeyDown("shift")){
this.prev()
}else{
this.next()
}}},"__LZcheckFocusChange",function($1){
if($1.focusable){
this.setFocus($1,false)
}},"setFocus",function($1,$2){
switch(arguments.length){
case 1:
$2=null;

};if(this.__LZsfrunning){
this.__LZsfnextfocus=$1;return
};if(this.cseldest==$1){
return
};var $3=this.csel;if($3&&!$3.shouldYieldFocus()){
return
};if($1&&!$1.focusable){
$1=this.getNext($1);if(this.cseldest==$1){
return
}};if($3){
$3.blurring=true
};this.__LZsfnextfocus=-1;this.__LZsfrunning=true;this.cseldest=$1;if($2!=null){
this.focuswithkey=!(!$2)
};if(!this.__LZskipblur){
this.__LZskipblur=true;if($3&&$3.onblur.ready){
$3.onblur.sendEvent($1);var $4=this.__LZsfnextfocus;if($4!=-1){
if($4&&!$4.focusable){
$4=this.getNext($4)
};if($4!=$1){
this.__LZsfrunning=false;this.setFocus($4);return
}}}};this.lastfocus=$3;this.csel=$1;this.__LZskipblur=false;if($3){
$3.blurring=false
};if($dhtml&&canvas.accessible){
if($1&&$1.sprite!=null){
$1.sprite.aafocus()
}};if($1&&$1.onfocus.ready){
$1.onfocus.sendEvent($1);var $4=this.__LZsfnextfocus;if($4!=-1){
if($4&&!$4.focusable){
$4=this.getNext($4)
};if($4!=$1){
this.__LZsfrunning=false;this.setFocus($4);return
}}};if(this.onfocus.ready){
this.onfocus.sendEvent($1);var $4=this.__LZsfnextfocus;if($4!=-1){
if($4&&!$4.focusable){
$4=this.getNext($4)
};if($4!=$1){
this.__LZsfrunning=false;this.setFocus($4);return
}}};this.__LZsfrunning=false
},"clearFocus",function(){
this.setFocus(null)
},"getFocus",function(){
return this.csel
},"next",function(){
this.genMoveSelection(1)
},"prev",function(){
this.genMoveSelection(-1)
},"getNext",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.moveSelSubview($1||this.csel,1,false)
},"getPrev",function($1){
switch(arguments.length){
case 0:
$1=null;

};return this.moveSelSubview($1||this.csel,-1,false)
},"genMoveSelection",function($1){
var $2=this.csel;var $3=$2;while($2&&$3!=canvas){
if(!$3.visible){
$2=null
};$3=$3.immediateparent
};if($2==null){
$2=lz.ModeManager.getModalView()
};var $4="get"+($1==1?"Next":"Prev")+"Selection";var $5=$2?$2[$4]():null;if($5==null){
$5=this.moveSelSubview($2,$1,true)
};if(lz.ModeManager.__LZallowFocus($5)){
this.setFocus($5,true)
}},"accumulateSubviews",function($1,$2,$3,$4){
if($2==$3||$2.focusable&&$2.visible){
$1.push($2)
};if($4||!$2.focustrap&&$2.visible){
for(var $5=0;$5<$2.subviews.length;$5++){
this.accumulateSubviews($1,$2.subviews[$5],$3,false)
}}},"moveSelSubview",function($1,$2,$3){
var $4=$1||canvas;while(!$4.focustrap&&$4.immediateparent&&$4!=$4.immediateparent){
$4=$4.immediateparent
};var $5=[];this.accumulateSubviews($5,$4,$1,true);var $6=-1;var $7=$5.length;var $8=false;for(var $9=0;$9<$7;++$9){
if($5[$9]===$1){
$8=$2==-1&&$9==0||$2==1&&$9==$7-1;$6=$9;break
}};if($3&&$8){
this.onescapefocus.sendEvent()
};if($6==-1&&$2==-1){
$6=0
};$6=($6+$2+$7)%$7;return $5[$6]
}],["LzFocus",void 0]);(function($1){
with($1){
with($1.prototype){
LzFocusService.LzFocus=new LzFocusService()
}}})(LzFocusService);lz.FocusService=LzFocusService;lz.Focus=LzFocusService.LzFocus;Class.make("LzTimerService",null,["timerList",new Object(),"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"execDelegate",function($1){
var $2=$1.delegate;lz.Timer.removeTimerWithID($2,$1.id);if($2.enabled&&$2.c){
$2.execute(new Date().getTime())
}},"removeTimerWithID",function($1,$2){
var $3=$1.__delegateID;var $4=this.timerList[$3];if($4!=null){
if($4 instanceof Array){
for(var $5=0;$5<$4.length;$5++){
if($4[$5]==$2){
$4.splice($5,1);if($4.length==0){
delete this.timerList[$3]
};break
}}}else{
if($4==$2){
delete this.timerList[$3]
}}}},"addTimer",function($1,$2){
if(!$2||$2<1){
$2=1
};var $3={delegate:$1};var $4=LzTimeKernel.setTimeout(this.execDelegate,$2,$3);$3.id=$4;var $5=$1.__delegateID;var $6=this.timerList[$5];if($6==null){
this.timerList[$5]=$4
}else{
if(!($6 instanceof Array)){
this.timerList[$5]=[$6,$4]
}else{
$6.push($4)
}};return $4
},"removeTimer",function($1){
var $2=$1.__delegateID;var $3=this.timerList[$2];var $4=null;if($3!=null){
if($3 instanceof Array){
$4=$3.shift();LzTimeKernel.clearTimeout($4);if($3.length==0){
delete this.timerList[$2]
}}else{
$4=$3;LzTimeKernel.clearTimeout($4);delete this.timerList[$2]
}};return $4
},"resetTimer",function($1,$2){
this.removeTimer($1);return this.addTimer($1,$2)
}],["LzTimer",void 0]);(function($1){
with($1){
with($1.prototype){
LzTimerService.LzTimer=new LzTimerService()
}}})(LzTimerService);lz.TimerService=LzTimerService;lz.Timer=LzTimerService.LzTimer;