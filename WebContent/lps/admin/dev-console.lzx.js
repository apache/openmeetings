var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;var _Copyright="Portions of this file are copyright (c) 2001-2009 by Laszlo Systems, Inc.  All rights reserved.";var $modules={};$modules.runtime=this;$modules.lz=$modules.runtime;$modules.user=$modules.lz;var global=$modules.user;var __ES3Globals={Array:Array,Boolean:Boolean,Date:Date,Function:Function,Math:Math,Number:Number,Object:Object,RegExp:RegExp,String:String,Error:Error,EvalError:EvalError,RangeError:RangeError,ReferenceError:ReferenceError,SyntaxError:SyntaxError,TypeError:TypeError,URIError:URIError};var globalValue=function($0){
if($0.charAt(0)=="<"&&$0.charAt($0.length-1)==">"){
return lz[$0.substring(1,$0.length-1)]
}else if($0 in this){
return this[$0]
}else if($0 in global){
return global[$0]
}else if($0 in __ES3Globals){
return __ES3Globals[$0]
};return void 0
};var Instance=function(){
this.constructor=arguments.callee;this.$lzsc$initialize.apply(this,arguments)
};Instance.prototype.constructor=Instance;Instance.classname="Instance";Instance.prototype.classname="Object";(function(){
var $0=function($0){
for(var $1=$0.length-1;$1>=1;$1-=2){
var $2=$0[$1];var $3=$0[$1-1];if($2!==void 0||!($3 in this)){
this[$3]=$2
};if(!($2 instanceof Function))continue;var $4=this.constructor;if($2.hasOwnProperty("$superclasses")){
var $5=$2.$superclasses,$6=false;for(var $7=$5.length-1;$7>=0;$7--){
if($5[$7]===$4){
$6=true;break
}};if(!$6){
$2.$superclasses.push($4)
}}else if($2.hasOwnProperty("$superclass")&&$2.$superclass!==$4){
var $8=$2.$superclass;delete $2.$superclass;$2.$superclasses=[$8,$4]
}else{
$2.$superclass=$4
}}};$0.call(Instance.prototype,["addProperties",$0])
})();Instance.prototype.addProperties(["addProperty",function($0,$1){
this.addProperties([$0,$1])
}]);Instance.prototype.addProperty("nextMethod",function($0,$1){
var $2;if($0.hasOwnProperty("$superclass")){
$2=$0.$superclass.prototype[$1]
}else if($0.hasOwnProperty("$superclasses")){
var $3=$0.$superclasses;for(var $4=$3.length-1;$4>=0;$4--){
var $5=$3[$4];if(this instanceof $5){
$2=$5.prototype[$1];break
}}};if(!$2){
$2=function(){}};return $2
});Instance.prototype.addProperty("$lzsc$initialize",function(){});if(lz.embed.browser.isSafari&&lz.embed.browser.version=="531.21"){
Instance.prototype.addProperty("$lzsc$safarikludge",function(){})
};var Class={prototype:new Instance(),addProperty:Instance.prototype.addProperty,addProperties:function($0){
this.prototype.addProperties($0)
},addStaticProperty:function($0,$1){
this[$0]=$1
},allClasses:{Instance:Instance},make:function($0,$1,instanceProperties,$2){
var superclass=null;if($1 instanceof Array){
for(var $3=$1.length-1;$3>=0;$3--){
var $4=$1[$3];if($4 instanceof Function){
$1.splice($3,1);superclass=$4
}}}else if($1 instanceof Function){
superclass=$1;$1=null
};if(!superclass){
superclass=Instance
};var $5=function(){
this.constructor=arguments.callee;if(this["$lzsc$safarikludge"]&&this.$lzsc$safarikludge!==Instance.prototype.$lzsc$safarikludge){
this.$lzsc$safarikludge()
};if(this.$lzsc$initialize!==Instance.prototype.$lzsc$initialize){
this.$lzsc$initialize.apply(this,arguments)
}};$5.constructor=this;$5.classname=$0;var $6=function(){
this.constructor=superclass
};$6.prototype=superclass.prototype;var $7=new $6();if($1 instanceof Array){
for(var $8=$1.length-1;$8>=0;$8--){
var $9=$1[$8];$7=$9.makeInterstitial($7,$8>0?$1[$8-1]:$5)
}};$5.prototype=$7;this.addStaticProperty.call($5,"addStaticProperty",this.addStaticProperty);$5.addStaticProperty("addProperty",this.addProperty);$5.addStaticProperty("addProperties",this.addProperties);if($2){
for(var $8=$2.length-1;$8>=1;$8-=2){
var $a=$2[$8];var $b=$2[$8-1];$5.addStaticProperty($b,$a)
}};if(instanceProperties){
$5.addProperties(instanceProperties);if(lz.embed.browser.isSafari&&lz.embed.browser.version=="531.21"){
$5.addProperty("$lzsc$safarikludge",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$safarikludge"]||this.nextMethod(arguments.callee,"$lzsc$safarikludge")).call(this);for(var $0=instanceProperties.length-1;$0>=1;$0-=2){
var $1=instanceProperties[$0];if(Function["$lzsc$isa"]?Function.$lzsc$isa($1):$1 instanceof Function)continue;var $2=instanceProperties[$0-1];if($1!==void 0||!($2 in this)){
this[$2]=$1
}}})
}};global[$0]=this.allClasses[$0]=$5;return $5
}};var Mixin={prototype:new Instance(),allMixins:{},addProperty:function($0,$1){
this.prototype[$0]=$1;this.instanceProperties.push($0,$1);var $2=this.implementations;for(var $3 in $2){
var $4=$2[$3];$4.addProperty($0,$1)
}},addStaticProperty:function($0,$1){
this[$0]=$1
},makeInterstitial:function($0,$1){
var $2=this.implementations;var $3=this.classname+"+"+$0.constructor.classname;var $4=$1.classname+"|"+$3;if($2[$4]){
return $2[$4]
};$0.addProperties(this.instanceProperties);var $5=function(){
this.constructor=arguments.callee
};$5.prototype=$0;$5.classname=$3;var $6=new $5();$2[$4]=$6;return $6
},$lzsc$isa:function($0){
var $1=this.implementations;for(var $2 in $1){
if($0 instanceof $1[$2].constructor){
return true
}};return false
},make:function($0,$1,$2,$3){
var $4={constructor:this,classname:$0,_dbg_typename:this._dbg_name,_dbg_name:$0,prototype:$1?$1.make():new Object(),instanceProperties:$1?$1.instanceProperties.slice(0):new Array(),implementations:{}};this.addStaticProperty.call($4,"addStaticProperty",this.addStaticProperty);$4.addStaticProperty("addProperty",this.addProperty);$4.addStaticProperty("makeInterstitial",this.makeInterstitial);$4.addStaticProperty("$lzsc$isa",this.$lzsc$isa);if($3){
for(var $5=$3.length-1;$5>=1;$5-=2){
var $6=$3[$5];var $7=$3[$5-1];$4.addStaticProperty($7,$6)
}};if($2){
for(var $5=$2.length-1;$5>=1;$5-=2){
var $6=$2[$5];var $7=$2[$5-1];$4.addProperty($7,$6)
}};global[$0]=this.allMixins[$0]=$4;return $4
}};Class.make("LzBootstrapMessage",null,["message","","length",0,"$lzsc$initialize",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0!=null){
this.appendInternal(""+$0,$0)
}},"appendInternal",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.message+=$0;this.length=this.message.length
},"append",function(){
var $0=Array.prototype.slice.call(arguments,0);var $1=$0.length;for(var $2=0;$2<$1;$2++){
this.appendInternal(String($0[$2]))
}},"charAt",function($0){
return this.message.charAt($0)
},"charCodeAt",function($0){
return this.message.charCodeAt($0)
},"indexOf",function($0){
return this.message.indexOf($0)
},"lastIndexOf",function($0){
return this.message.lastIndexOf($0)
},"toLowerCase",function(){
return new LzMessage(this.message.toLowerCase())
},"toUpperCase",function(){
return new LzMessage(this.message.toUpperCase())
},"toString",function(){
return this.message||""
},"valueOf",function(){
return this.message||""
},"concat",function(){
var $0=Array.prototype.slice.call(arguments,0);return new LzMessage(this.message.concat.apply(this.message,$0))
},"slice",function(){
var $0=Array.prototype.slice.call(arguments,0);return this.message.slice.apply(this.message,$0)
},"split",function(){
var $0=Array.prototype.slice.call(arguments,0);return this.message.split.apply(this.message,$0)
},"substr",function(){
var $0=Array.prototype.slice.call(arguments,0);return this.message.substr.apply(this.message,$0)
},"substring",function(){
var $0=Array.prototype.slice.call(arguments,0);return this.message.substring.apply(this.message,$0)
},"toHTML",function(){
return this["toString"]().toHTML()
}],["xmlEscape",function($0){
if($0&&(typeof $0=="string"||(String["$lzsc$isa"]?String.$lzsc$isa($0):$0 instanceof String))){
var $1=$0.length;var $2="";for(var $3=0;$3<$1;$3++){
var $4=$0.charAt($3);switch($4){
case "<":
$2+="&lt;";break;
case ">":
$2+="&gt;";break;
default:
$2+=$4;

}};return $2
}else{
return $0
}}]);(function($0){
with($0)with($0.prototype){}})(LzBootstrapMessage);var LzMessage=LzBootstrapMessage;String.prototype.toHTML=function(){
return LzMessage.xmlEscape(this)
};Mixin.make("LzFormatter",null,["toNumber",function($0){
return Number($0)
},"pad",function($0,$1,$2,$3,$4,$5,$6){
switch(arguments.length){
case 0:
$0="";
case 1:
$1=0;
case 2:
$2=null;
case 3:
$3=" ";
case 4:
$4="-";
case 5:
$5=10;
case 6:
$6=false;

};var $7=typeof $0=="number";if($7){
if($2!=null){
var $8=Math.pow(10,-$2);$0=Math.round($0/$8)*$8
};$0=Number($0).toString($5);if($4!="-"){
if($0.indexOf("-")!=0){
if($0!=0){
$0=$4+$0
}else{
$0=" "+$0
}}}}else{
$0=""+$0
};var $9=$0.length;if($2!=null){
if($7){
var $a=$0.lastIndexOf(".");if($a==-1){
var $b=0;if($6||$2>0){
$0+="."
}}else{
var $b=$9-($a+1)
};if($b>$2){
$0=$0.substring(0,$9-($b-$2))
}else{
for(var $c=$b;$c<$2;$c++)$0+="0"
}}else{
$0=$0.substring(0,$2)
}};$9=$0.length;var $d=false;if($1<0){
$1=-$1;$d=true
};if($9>=$1){
return $0
};if($d){
for(var $c=$9;$c<$1;$c++)$0=$0+" "
}else{
$4=null;if($3!=" "){
if(" +-".indexOf($0.substring(0,1))>=0){
$4=$0.substring(0,1);$0=$0.substring(1)
}};for(var $c=$9;$c<$1;$c++)$0=$3+$0;if($4!=null){
$0=$4+$0
}};return $0
},"abbreviate",function($0,$1){
switch(arguments.length){
case 1:
$1=Infinity;

};var $2="\u2026";if($0.length>$1-$2.length){
$0=$0.substring(0,$1-$2.length)+$2
};return $0
},"stringEscape",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};$0=$0.split("\\").join("\\\\");var $2=LzFormatter.singleEscapeCharacters;var $3='"';var $4="";var $5="'";if($1){
$5="";var $6=$0.split("'").length;var $7=$0.split('"').length;if($6>$7){
$3="'";$4='"'
}else{
$3='"';$4="'"
}};for(var $8=0,$9=$2.length;$8<$9;$8+=2){
var $a=$2[$8];var $b=$2[$8+1];if($b!="\\"&&$b!=$3&&$b!=$5){
$0=$0.split($b).join($a)
}};return $4+$0+$4
},"formatToString",function($0){
var $8;var $9;$8=function($0){
if($0>=al){
return null
};return args[$0]
};$9=function($0){};switch(arguments.length){
case 0:
$0="";

};var args=Array.prototype.slice.call(arguments,1);var al=args.length;if(!(typeof $0=="string"||(String["$lzsc$isa"]?String.$lzsc$isa($0):$0 instanceof String))||al>0!=$0.indexOf("%")>=0){
args=[$0].concat(args);al++;var $1=new LzMessage();for(var $2=0;$2<al;$2++){
var $3=args[$2];var $4=$2==al-1?"\n":" ";$1.append($3);$1.appendInternal($4)
};return $1
};var $5=""+$0;var $6=0;var $7=0;var $a=0,$b=$5.length;var $c=0,$d=0;var $1=new LzMessage();while($c<$b){
$d=$5.indexOf("%");if($d==-1){
$1.append($5.substring($c,$b));break
};$1.append($5.substring($c,$d));$a=$d;$c=$d+1;$d=$d+2;var $e="-";var $f=" ";var $g=false;var $h="";var $i=null;var $j=null;var $k=null;while($c<$b&&$j==null){
var $l=$5.substring($c,$d);$c=$d++;switch($l){
case "-":
$h=$l;break;
case "+":
case " ":
$e=$l;break;
case "#":
$g=true;break;
case "0":
if($h===""&&$i===null){
$f=$l;break
}
case "1":
case "2":
case "3":
case "4":
case "5":
case "6":
case "7":
case "8":
case "9":
if($i!==null){
$i+=$l
}else{
$h+=$l
}break;
case "$":
$6=$h-1;$h="";break;
case "*":
if($i!==null){
$i=$8($6);$9($6++)
}else{
$h=$8($6);$9($6++)
}break;
case ".":
$i="";break;
case "h":
case "l":
break;
case "=":
$k=$8($6);$9($6++);break;
default:
$j=$l;break;

}};var $m=$8($6);if($k==null){
$k=$m
};var $n=null;var $o=false;if($i!==null){
$n=1*$i
}else{
switch($j){
case "F":
case "E":
case "G":
case "f":
case "e":
case "g":
$n=6;$o=$g;break;
case "O":
case "o":
if($g&&$m!=0){
$1.append("0")
}break;
case "X":
case "x":
if($g&&$m!=0){
$1.append("0"+$j)
}break;

}};var $p=10;switch($j){
case "o":
case "O":
$p=8;break;
case "x":
case "X":
$p=16;break;

};switch($j){
case "U":
case "O":
case "X":
case "u":
case "o":
case "x":
if($m<0){
$m=-$m;var $q=Math.abs(parseInt($h,10));if(isNaN($q)){
$q=this.toNumber($m).toString($p).length
};var $r=Math.pow($p,$q);$m=$r-$m
}break;

};switch($j){
case "D":
case "U":
case "I":
case "O":
case "X":
case "F":
case "E":
case "G":
$m=this.toNumber($m);$1.appendInternal(this.pad($m,$h,$n,$f,$e,$p,$o).toUpperCase(),$k);$9($6++);break;
case "c":
$m=String.fromCharCode($m);
case "w":

case "s":
var $s;if(Function["$lzsc$isa"]?Function.$lzsc$isa($m):$m instanceof Function){
if(!$s){
$s="function () {\u2026}"
}}else if(typeof $m=="number"){
$s=Number($m).toString($p)
}else if($j=="w"&&typeof $m=="string"){
$s=this.stringEscape($m,true)
}else{
$s=""+$m
}if($g){
var $t=$n;if($t){
$s=this.abbreviate($s,$t);$n=null
}}$1.appendInternal(this.pad($s,$h,$n,$f,$e,$p,$o),$k);$9($6++);break;
case "d":
case "u":
case "i":
case "o":
case "x":
case "f":
case "e":
case "g":
$m=this.toNumber($m);$1.appendInternal(this.pad($m,$h,$n,$f,$e,$p,$o),$k);$9($6++);break;
case "%":
$1.append("%");break;
default:
$1.append($5.substring($a,$c));break;

};$5=$5.substring($c,$b);$a=0,$b=$5.length;$c=0,$d=0;if($6>$7){
$7=$6
}};if($7<al){
$1.appendInternal(" ");for(;$7<al;$7++){
var $3=$8($7);var $4=$7==al-1?"\n":" ";$1.append($3);$1.appendInternal($4)
}};return $1
}],["singleEscapeCharacters",["\\b","\b","\\t","\t","\\n","\n","\\v",String.fromCharCode(11),"\\f","\f","\\r","\r",'\\"','"',"\\'","'","\\\\","\\"]]);(function($0){
with($0)with($0.prototype){}})(LzFormatter);Debug=global["Debug"]||{};(function(){
var $0;$0=function(){};Debug.write=$0;Debug.trace=$0;Debug.monitor=$0;Debug.warn=$0;Debug.error=$0;Debug.info=$0;Debug.debug=$0;Debug.deprecated=$0;Debug.inspect=$0
})();trace=function(){
console.info.apply(console,arguments)
};Class.make("LzDeclaredEventClass",null,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"ready",false,"sendEvent",function($0){
switch(arguments.length){
case 0:
$0=null;

}},"clearDelegates",function(){},"removeDelegate",function($0){
switch(arguments.length){
case 0:
$0=null;

}},"getDelegateCount",function(){
return 0
},"toString",function(){
return "LzDeclaredEvent"
}],null);(function($0){
with($0)with($0.prototype){}})(LzDeclaredEventClass);var LzDeclaredEvent=new LzDeclaredEventClass();Class.make("LzValueExpr",null,null,null);Class.make("LzInitExpr",LzValueExpr,null,null);Class.make("LzOnceExpr",LzInitExpr,["methodName",void 0,"$lzsc$initialize",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.methodName=$0
}],null);Class.make("LzConstraintExpr",LzOnceExpr,["$lzsc$initialize",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1)
}],null);Class.make("LzStyleConstraintExpr",LzConstraintExpr,["attribute",void 0,"property",void 0,"type",void 0,"fallback",void 0,"warn",void 0,"$lzsc$initialize",function($0,$1,$2,$3,$4){
switch(arguments.length){
case 3:
$3=void 0;
case 4:
$4=true;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,null);this.attribute=$0;this.property=$1;this.type=$2;this.fallback=$3;this.warn=$4
}],null);Class.make("LzAlwaysExpr",LzConstraintExpr,["dependenciesName",void 0,"$lzsc$initialize",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$2);this.dependenciesName=$1
}],null);Class.make("LzStyleExpr",LzValueExpr,["$lzsc$initialize",function(){}],null);Class.make("LzStyleAttr",LzStyleExpr,["sourceAttributeName",void 0,"$lzsc$initialize",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.sourceAttributeName=$0
}],null);Class.make("LzStyleIdent",LzStyleExpr,["sourceValueID",void 0,"$lzsc$initialize",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.sourceValueID=$0
}],null);function LzInheritedHash($0){
if($0){
for(var $1 in $0){
this[$1]=$0[$1]
}}};var lz;(function(){
if(Object["$lzsc$isa"]?Object.$lzsc$isa(lz):lz instanceof Object){

}else if(!lz){
lz=new LzInheritedHash()
}})();lz.DeclaredEventClass=LzDeclaredEventClass;lz.Formatter=LzFormatter;lz.colors={aliceblue:15792383,antiquewhite:16444375,aqua:65535,aquamarine:8388564,azure:15794175,beige:16119260,bisque:16770244,black:0,blanchedalmond:16772045,blue:255,blueviolet:9055202,brown:10824234,burlywood:14596231,cadetblue:6266528,chartreuse:8388352,chocolate:13789470,coral:16744272,cornflowerblue:6591981,cornsilk:16775388,crimson:14423100,cyan:65535,darkblue:139,darkcyan:35723,darkgoldenrod:12092939,darkgray:11119017,darkgreen:25600,darkgrey:11119017,darkkhaki:12433259,darkmagenta:9109643,darkolivegreen:5597999,darkorange:16747520,darkorchid:10040012,darkred:9109504,darksalmon:15308410,darkseagreen:9419919,darkslateblue:4734347,darkslategray:3100495,darkslategrey:3100495,darkturquoise:52945,darkviolet:9699539,deeppink:16716947,deepskyblue:49151,dimgray:6908265,dimgrey:6908265,dodgerblue:2003199,firebrick:11674146,floralwhite:16775920,forestgreen:2263842,fuchsia:16711935,gainsboro:14474460,ghostwhite:16316671,gold:16766720,goldenrod:14329120,gray:8421504,green:32768,greenyellow:11403055,grey:8421504,honeydew:15794160,hotpink:16738740,indianred:13458524,indigo:4915330,ivory:16777200,khaki:15787660,lavender:15132410,lavenderblush:16773365,lawngreen:8190976,lemonchiffon:16775885,lightblue:11393254,lightcoral:15761536,lightcyan:14745599,lightgoldenrodyellow:16448210,lightgray:13882323,lightgreen:9498256,lightgrey:13882323,lightpink:16758465,lightsalmon:16752762,lightseagreen:2142890,lightskyblue:8900346,lightslategray:7833753,lightslategrey:7833753,lightsteelblue:11584734,lightyellow:16777184,lime:65280,limegreen:3329330,linen:16445670,magenta:16711935,maroon:8388608,mediumaquamarine:6737322,mediumblue:205,mediumorchid:12211667,mediumpurple:9662683,mediumseagreen:3978097,mediumslateblue:8087790,mediumspringgreen:64154,mediumturquoise:4772300,mediumvioletred:13047173,midnightblue:1644912,mintcream:16121850,mistyrose:16770273,moccasin:16770229,navajowhite:16768685,navy:128,oldlace:16643558,olive:8421376,olivedrab:7048739,orange:16753920,orangered:16729344,orchid:14315734,palegoldenrod:15657130,palegreen:10025880,paleturquoise:11529966,palevioletred:14381203,papayawhip:16773077,peachpuff:16767673,peru:13468991,pink:16761035,plum:14524637,powderblue:11591910,purple:8388736,red:16711680,rosybrown:12357519,royalblue:4286945,saddlebrown:9127187,salmon:16416882,sandybrown:16032864,seagreen:3050327,seashell:16774638,sienna:10506797,silver:12632256,skyblue:8900331,slateblue:6970061,slategray:7372944,slategrey:7372944,snow:16775930,springgreen:65407,steelblue:4620980,tan:13808780,teal:32896,thistle:14204888,tomato:16737095,turquoise:4251856,violet:15631086,wheat:16113331,white:16777215,whitesmoke:16119285,yellow:16776960,yellowgreen:10145074,transparent:null};Class.make("LzCache",null,["size",void 0,"slots",void 0,"destroyable",void 0,"capacity",void 0,"curslot",void 0,"data",null,"$lzsc$initialize",function($0,$1,$2){
switch(arguments.length){
case 0:
$0=16;
case 1:
$1=2;
case 2:
$2=true;

};this.size=$0;this.slots=$1;this.destroyable=$2;this.clear()
},"clear",function(){
this.curslot=0;this.capacity=0;var $0=this.slots;if(!this.data)this.data=new Array($0);var $1=this.data;for(var $2=0;$2<$0;++$2){
if(this.destroyable){
var $3=$1[$2];for(var $4 in $3){
$3[$4].destroy()
}};$1[$2]={}}},"ensureSlot",function(){
if(++this.capacity>this.size){
var $0=(this.curslot+1)%this.slots;var $1=this.data;if(this.destroyable){
var $2=$1[$0];for(var $3 in $2){
$2[$3].destroy()
}};$1[$0]={};this.curslot=$0;this.capacity=1
}},"put",function($0,$1){
var $2=this.get($0);if($2===void 0){
this.ensureSlot()
};this.data[this.curslot][$0]=$1;return $2
},"get",function($0){
var $1=this.slots;var $2=this.curslot;var $3=this.data;for(var $4=0;$4<$1;++$4){
var $5=($2+$4)%$1;var $6=$3[$5][$0];if($6!==void 0){
if($5!=$2){
delete $3[$5][$0];this.ensureSlot();$3[this.curslot][$0]=$6
};return $6
}};return void 0
}],null);Class.make("LzEventable",null,["$lzsc$initialize",function(){},"__LZdeleted",false,"_events",null,"ondestroy",LzDeclaredEvent,"destroy",function(){
if(this.ondestroy.ready)this.ondestroy.sendEvent(this);this.__LZdeleted=true;this.__LZdelegatesQueue=null;this.__LZdeferDelegates=false;if(this._events!=null){
for(var $0=this._events.length-1;$0>=0;$0--){
this._events[$0].clearDelegates()
}};this._events=null
},"__LZdeferDelegates",false,"__LZdelegatesQueue",null,"childOf",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};return false
},"setAttribute",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};if(this.__LZdeleted){
return
};var $3="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$3]):this[$3] instanceof Function){
this[$3]($1)
}else{
this[$0]=$1;var $4=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($4):$4 instanceof LzEvent){
if($4.ready)$4.sendEvent($1)
}}}],null);lz.Eventable=LzEventable;Class.make("LzStyleAttrBinder",LzEventable,["target",void 0,"dest",void 0,"source",void 0,"$lzsc$initialize",function($0,$1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.target=$0;this.dest=$1;this.source=$2
},"bind",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.target;var $2=this.dest;var $3=$1[$2];var $4=$1[this.source];if($4!==$3||!$1.inited){
{
if(!$1.__LZdeleted){
var $lzsc$1594091806="$lzc$set_"+$2;if(Function["$lzsc$isa"]?Function.$lzsc$isa($1[$lzsc$1594091806]):$1[$lzsc$1594091806] instanceof Function){
$1[$lzsc$1594091806]($4)
}else{
$1[$2]=$4;var $lzsc$1065742462=$1["on"+$2];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1065742462):$lzsc$1065742462 instanceof LzEvent){
if($lzsc$1065742462.ready){
$lzsc$1065742462.sendEvent($4)
}}}}}}}],null);Class.make("PresentationType",null,null,["accept",function($0){
return $0
},"present",function($0){
return String($0)
}]);Class.make("DefaultPresentationType",PresentationType,null,["accept",PresentationType.accept,"present",function($0){
return $0
}]);Class.make("StringPresentationType",PresentationType,null,["accept",function($0){
return String($0)
},"present",PresentationType.present]);Class.make("BooleanPresentationType",PresentationType,null,["accept",function($0){
switch($0.toLowerCase()){
case "":
case "0":
case "false":
return false;
default:
return true;

}},"present",PresentationType.present]);Class.make("NumberPresentationType",PresentationType,null,["accept",function($0){
return Number($0)
},"present",PresentationType.present]);Class.make("ColorPresentationType",PresentationType,null,["accept",function($0){
if($0==-1){
return null
};return LzColorUtils.hextoint($0)
},"present",function($0){
var $1=lz.colors;for(var $2 in $1){
if($1[$2]===$0){
return $2
}};return LzColorUtils.inttohex($0)
}]);Class.make("ExpressionPresentationType",PresentationType,null,["accept",function($0){
switch($0){
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

};if(!isNaN($0)){
return Number($0)
};return String($0)
},"present",PresentationType.present]);Class.make("SizePresentationType",PresentationType,null,["accept",function($0){
if($0=="null"){
return null
};return Number($0)
},"present",PresentationType.present]);Class.make("LzNode",LzEventable,["__LZisnew",false,"__LZdeferredcarr",null,"classChildren",null,"animators",null,"_instanceAttrs",null,"_instanceChildren",null,"__LzValueExprs",null,"__LZhasConstraint",function($0){
return($0 in this.__LzValueExprs)&&!(LzStyleConstraintExpr["$lzsc$isa"]?LzStyleConstraintExpr.$lzsc$isa(this.__LzValueExprs[$0]):this.__LzValueExprs[$0] instanceof LzStyleConstraintExpr)
},"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.__LZUID="__U"+ ++LzNode.__UIDs;this.__LZdeferDelegates=true;if($1){
if($1["$lzc$bind_id"]){
this.$lzc$bind_id=$1.$lzc$bind_id
};if($1["$lzc$bind_name"]){
this.$lzc$bind_name=$1.$lzc$bind_name
}};var $4=this.$lzc$bind_id;if($4){
$4.call(null,this)
};var $5=this.$lzc$bind_name;if($5){
$5.call(null,this)
};this._instanceAttrs=$1;this._instanceChildren=$2;var $6=new LzInheritedHash(this["constructor"].attributes);if(!(LzState["$lzsc$isa"]?LzState.$lzsc$isa(this):this instanceof LzState)){
for(var $7 in $6){
var $8=$6[$7];if(!(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($8):$8 instanceof LzInitExpr)){
var $9="$lzc$set_"+$7;if(!this[$9]){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($8):$8 instanceof Function){
this.addProperty($7,$8)
}else if($8!==void 0){
this[$7]=$8
}else if(this[$7]===void 0){
this[$7]=void 0
};delete $6[$7]
}else{
if(this[$7]===void 0){
this[$7]=null
}}}}};if($1){
LzNode.mergeAttributes($1,$6)
};if($4){
$6.$lzc$bind_id=LzNode._ignoreAttribute
};if($5){
$6.$lzc$bind_name=LzNode._ignoreAttribute
};this.__LZisnew=!$3;var $a=this["constructor"]["children"];if(Array["$lzsc$isa"]?Array.$lzsc$isa($a):$a instanceof Array){
$2=LzNode.mergeChildren($2,$a)
};if($6["datapath"]!=null){
delete $6["$datapath"]
};var $b=this.__LzValueExprs={};for(var $7 in $6){
var $8=$6[$7];if(LzValueExpr["$lzsc$isa"]?LzValueExpr.$lzsc$isa($8):$8 instanceof LzValueExpr){
$b[$7]=$8;delete $6[$7]
}};this.construct($0,$6);if(this.__LZdeleted){
return
};for(var $7 in $b){
$6[$7]=$b[$7]
};this.__LzValueExprs=null;this.__LZapplyArgs($6,true);if(this.__LZdeleted){
return
};this.__LZdeferDelegates=false;var $c=this.__LZdelegatesQueue;if($c){
LzDelegate.__LZdrainDelegatesQueue($c);this.__LZdelegatesQueue=null
};if(this.onconstruct.ready)this.onconstruct.sendEvent(this);if($2&&$2.length){
this.createChildren($2)
}else{
this.__LZinstantiationDone()
}},"oninit",LzDeclaredEvent,"onconstruct",LzDeclaredEvent,"ondata",LzDeclaredEvent,"clonenumber",null,"onclonenumber",LzDeclaredEvent,"__LZinstantiated",false,"__LZpreventSubInit",null,"__LZresolveDict",null,"__LZsourceLocation",null,"__LZUID",null,"__LZPropertyCache",null,"__LZRuleCache",null,"__LZdelegates",null,"isinited",false,"inited",false,"oninited",LzDeclaredEvent,"subnodes",null,"datapath",null,"$lzc$set_datapath",function($0){
if(null!=this.datapath&&$0!==LzNode._ignoreAttribute){
this.datapath.setXPath($0)
}else{
new LzDatapath(this,{xpath:$0})
}},"initstage",null,"$isstate",false,"doneClassRoot",false,"parent",void 0,"cloneManager",null,"name",null,"$lzc$bind_name",null,"id",null,"$lzc$set_id",-1,"$lzc$bind_id",null,"defaultplacement",null,"placement",null,"$lzc$set_placement",-1,"$cfn",0,"immediateparent",null,"dependencies",null,"classroot",void 0,"nodeLevel",0,"__LZstyleBindAttribute",function($0,$1,$2,$3,$4){
switch(arguments.length){
case 3:
$3=void 0;
case 4:
$4=true;

};var $5=this["__LZPropertyCache"]||LzCSSStyle.getPropertyCache(this);var $6=$5[$1];if(typeof $6=="string"&&$6.length>2&&$6.indexOf("0x")==0&&!isNaN($6)){
$6=Number($6)
};if(LzStyleExpr["$lzsc$isa"]?LzStyleExpr.$lzsc$isa($6):$6 instanceof LzStyleExpr){
if(LzStyleAttr["$lzsc$isa"]?LzStyleAttr.$lzsc$isa($6):$6 instanceof LzStyleAttr){
var $7=$6;var $8=$7.sourceAttributeName;var $9=new LzStyleAttrBinder(this,$0,$8);if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDelegate($9,"bind",this,"on"+$8));$9.bind()
}else if(LzStyleIdent["$lzsc$isa"]?LzStyleIdent.$lzsc$isa($6):$6 instanceof LzStyleIdent){
var $a=$6;this.acceptAttribute($0,$2,$a.sourceValueID)
}}else if($6!==void 0){
{
if(!this.__LZdeleted){
var $lzsc$940554007="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$940554007]):this[$lzsc$940554007] instanceof Function){
this[$lzsc$940554007]($6)
}else{
this[$0]=$6;var $lzsc$6611750=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$6611750):$lzsc$6611750 instanceof LzEvent){
if($lzsc$6611750.ready){
$lzsc$6611750.sendEvent($6)
}}}}}}else if(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($3):$3 instanceof LzInitExpr){
this.applyConstraintExpr($3)
}else{
{
if(!this.__LZdeleted){
var $lzsc$1210129010="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1210129010]):this[$lzsc$1210129010] instanceof Function){
this[$lzsc$1210129010]($3)
}else{
this[$0]=$3;var $lzsc$333857321=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$333857321):$lzsc$333857321 instanceof LzEvent){
if($lzsc$333857321.ready){
$lzsc$333857321.sendEvent($3)
}}}}}}},"construct",function($0,$1){
this.__LZdelayedSetters=LzNode.__LZdelayedSetters;this.earlySetters=LzNode.earlySetters;var $2=$0;this.parent=$2;if($2){
var $3=$2;if($1["ignoreplacement"]||this.ignoreplacement){
this.placement=null
}else{
var $4=$1["placement"]||$2.defaultplacement;while($4!=null){
if($3.determinePlacement==LzNode.prototype.determinePlacement){
var $5=$3.searchSubnodes("name",$4);if($5==null)$5=$3
}else{
var $5=$3.determinePlacement(this,$4,$1)
};$4=$5!=$3?$5.defaultplacement:null;$3=$5
};this.placement=$4
};if(!this.__LZdeleted){
var $6=$3.subnodes;if($6==null){
$3.subnodes=[this]
}else{
$6[$6.length]=this
}};var $7=$3.nodeLevel;this.nodeLevel=$7?$7+1:1;this.immediateparent=$3
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
var $0=this.__LZpreventSubInit;this.__LZpreventSubInit=null;var $1=$0.length;for(var $2=0;$2<$1;$2++){
$0[$2].__LZcallInit()
}},"__LZcallInit",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.parent&&this.parent.__LZpreventSubInit){
this.parent.__LZpreventSubInit.push(this);return
};this.isinited=true;this.__LZresolveReferences();if(this.__LZdeleted)return;var $1=this.subnodes;if($1){
for(var $2=0;$2<$1.length;){
var $3=$1[$2++];var $4=$1[$2];if($3.isinited||!$3.__LZinstantiated)continue;$3.__LZcallInit();if(this.__LZdeleted)return;if($4!=$1[$2]){
while($2>0){
if($4==$1[--$2])break
}}}};this.init();if(this.oninit.ready)this.oninit.sendEvent(this);if(this.datapath&&this.datapath.__LZApplyDataOnInit){
this.datapath.__LZApplyDataOnInit()
};this.inited=true;if(this.oninited.ready){
this.oninited.sendEvent(true)
}},"completeInstantiation",function(){
if(!this.isinited){
var $0=this.initstage;this.initstage="early";if($0=="defer"){
lz.Instantiator.createImmediate(this,this.__LZdeferredcarr)
}else{
lz.Instantiator.completeTrickle(this)
}}},"ignoreplacement",false,"__LZapplyArgs",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};var $2={};var $3=null;var $4=null;var $5=null;var $6=null;for(var $7 in $0){
var $8=$0[$7];var $9="$lzc$set_"+$7;if($2[$7]||$0[$7]===LzNode._ignoreAttribute)continue;$2[$7]=true;if(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($8):$8 instanceof LzInitExpr){
if($8 instanceof LzConstraintExpr){
if($6==null){
$6=[]
};$6.push($8)
}else if($8 instanceof LzOnceExpr){
if($5==null){
$5=[]
};$5.push($8)
};if(this[$7]===void 0){
this[$7]=null
}}else if(!this[$9]){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($8):$8 instanceof Function){
this.addProperty($7,$8)
}else if($8!==void 0){
this[$7]=$8
}else if(this[$7]===void 0){
this[$7]=void 0
};if(!$1){
var $a="on"+$7;if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa(this[$a]):this[$a] instanceof LzEvent){
if(this[$a].ready)this[$a].sendEvent($0[$7])
}}}else if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$9]):this[$9] instanceof Function){
if($7 in this.earlySetters){
if($4==null){
$4=[]
};$4[this.earlySetters[$7]]=$7
}else{
if($3==null){
$3=[]
};$3.push($7)
}}};if($4){
for(var $b=1,$c=$4.length;$b<$c;$b++){
if($b in $4){
if(this.__LZdeleted)return;var $7=$4[$b];var $9="$lzc$set_"+$7;this[$9]($0[$7])
}}};if($3){
for(var $b=$3.length-1;$b>=0;$b--){
if(this.__LZdeleted)return;var $7=$3[$b];var $9="$lzc$set_"+$7;this[$9]($0[$7])
}};if($5!=null){
this.__LZstoreAttr($5,"$inits")
};if($6!=null){
this.__LZstoreAttr($6,"$constraints")
}},"createChildren",function($0){
if(this.__LZdeleted)return;if("defer"==this.initstage){
this.__LZdeferredcarr=$0
}else if("late"==this.initstage){
lz.Instantiator.trickleInstantiate(this,$0)
}else if(this.__LZisnew&&lz.Instantiator.syncNew||"immediate"==this.initstage){
lz.Instantiator.createImmediate(this,$0)
}else{
lz.Instantiator.requestInstantiation(this,$0)
}},"getExpectedAttribute",function($0){
var $1="e_"+$0;if(!this[$1])this[$1]={};if(this[$1].v==null)return this[$0];return this[$1].v
},"setExpectedAttribute",function($0,$1){
var $2="e_"+$0;if(!this[$2])this[$2]={};this[$2].v=$1
},"addToExpectedAttribute",function($0,$1){
var $2="e_"+$0;if(!this[$2])this[$2]={};if(this[$2].v==null)this[$2].v=this[$0];this[$2].v+=$1
},"__LZincrementCounter",function($0){
var $1="e_"+$0;var $2=this[$1];if(!$2){
$2=this[$1]={}};if(!$2.c){
$2.c=0
};$2.c+=1
},"makeChild",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if(this.__LZdeleted){
return
};var $2=$0["class"];if(!$2){
if($0["tag"]){
$2=lz[$0.tag]
};if(!$2&&$0["name"]){
$2=lz[$0.name]
}};var $3;if($2){
$3=new $2(this,$0.attrs,("children" in $0)?$0.children:null,$1)
};return $3
},"$lzc$set_$setters",-1,"$lzc$set_$classrootdepth",function($0){
if(!$0)return;var $1=this.parent;while(--$0>0){
$1=$1.parent
};this.classroot=$1
},"dataBindAttribute",function($0,$1,$2){
if(!this.datapath){
this.$lzc$set_datapath(".")
};if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDataAttrBind(this.datapath,$0,$1,$2))
},"__LZdelayedSetters",void 0,"earlySetters",void 0,"$lzc$set_$delegates",function($0){
var $1=[];var $2=$0.length;for(var $3=0;$3<$2;$3+=3){
if($0[$3+2]){
$1.push($0[$3],$0[$3+1],$0[$3+2])
}else{
var $4=$0[$3+1];if(!this.__LZdelegates){
this.__LZdelegates=[]
};this.__LZdelegates.push(new LzDelegate(this,$4,this,$0[$3]))
}};if($1.length){
this.__LZstoreAttr($1,"$delegates")
}},"__LZstoreAttr",function($0,$1){
if(this.__LZresolveDict==null){
this.__LZresolveDict={}};this.__LZresolveDict[$1]=$0
},"__LZresolveReferences",function(){
var $0=this.__LZresolveDict;if($0!=null){
this.__LZresolveDict=null;var $1=$0["$inits"];if($1!=null){
for(var $2=0,$3=$1.length;$2<$3;$2++){
this[$1[$2].methodName](null);if(this.__LZdeleted)return
}};var $4=$0["$constraints"];if($4!=null){
for(var $2=0,$3=$4.length;$2<$3;$2++){
this.applyConstraintExpr($4[$2]);if(this.__LZdeleted)return
}};for(var $5 in $0){
if($5=="$inits"||$5=="$constraints"||$5=="$delegates")continue;if($5 in this.__LZdelayedSetters){
this[this.__LZdelayedSetters[$5]]($0[$5])
}};if($0["$delegates"])this.__LZsetDelegates($0.$delegates)
}},"__LZsetDelegates",function($0){
if($0.length&&!this.__LZdelegates){
this.__LZdelegates=[]
};var $1=$0.length;for(var $2=0;$2<$1;$2+=3){
var $3=$0[$2+2];var $4=$3!=null?this[$3]():null;if($4==null)$4=this;var $5=$0[$2+1];this.__LZdelegates.push(new LzDelegate(this,$5,$4,$0[$2]))
}},"applyConstraint",function($0,$1,$2){
var $3="$cf"+this.$cfn++;this.addProperty($3,$1);return this.applyConstraintMethod($3,$2)
},"applyConstraintMethod",function($0,$1){
if($1&&$1.length>0){
if(!this.__LZdelegates){
this.__LZdelegates=[]
};var $2;for(var $3=0,$4=$1.length;$3<$4;$3+=2){
$2=$1[$3];if($2){
var $5=new LzDelegate(this,$0,$2,"on"+$1[$3+1]);this.__LZdelegates.push($5)
}}};this[$0](null)
},"applyConstraintExpr",function($0){
if($0 instanceof LzStyleConstraintExpr){
var $1=$0;this.__LZstyleBindAttribute($1.attribute,$1.property,$1.type,$1.fallback,$1.warn);return
};var $2=$0.methodName;var $3=null;if($0 instanceof LzAlwaysExpr){
var $4=$0;var $5=$4.dependenciesName;$3=this[$5]()
};this.applyConstraintMethod($2,$3)
},"releaseConstraint",function($0){
if(this._instanceAttrs!=null){
var $1=this._instanceAttrs[$0];if($1 instanceof LzConstraintExpr){
var $2=$1.methodName;return this.releaseConstraintMethod($2)
}};return false
},"releaseConstraintMethod",function($0){
var $1=false;var $2=this.__LZdelegates;if($2){
for(var $3=0;$3<$2.length;){
var $4=$2[$3];if((LzDelegate["$lzsc$isa"]?LzDelegate.$lzsc$isa($4):$4 instanceof LzDelegate)&&$4.c===this&&$4.m===this[$0]){
$4.unregisterAll();$2.splice($3,1);$1=true
}else{
$3++
}}};return $1
},"$lzc$set_name",function($0){
if(!($0===null||typeof $0=="string")){
return
};var $1=this.name;var $2=this.parent;var $3=this.immediateparent;if($1&&$1!=$0){
if(this.$lzc$bind_name){
if(globalValue($1)===this){
this.$lzc$bind_name.call(null,this,false)
}};if($2){
if($1&&$2[$1]===this){
$2[$1]=null
}};if($3){
if($1&&$3[$1]===this){
$3[$1]=null
}}};if($0&&$0.length){
if($2){
$2[$0]=this
};if($3){
$3[$0]=this
}};this.name=$0
},"defaultSet",function($0,$1){
if($0!=null){
this[$1]=$0
}},"setDatapath",function($0){
this.$lzc$set_datapath($0)
},"data",null,"$lzc$set_data",function($0){
this.data=$0;if(LzDataNodeMixin["$lzsc$isa"]?LzDataNodeMixin.$lzsc$isa($0):$0 instanceof LzDataNodeMixin){
var $1=this.datapath||new LzDatapath(this);$1.setPointer($0)
};if(this.ondata.ready)this.ondata.sendEvent($0)
},"setData",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.$lzc$set_data($0)
},"applyData",function($0){},"updateData",function(){
return void 0
},"setSelected",function($0){},"options",{},"$lzc$set_options",function($0){
if(this.options===this["constructor"].prototype.options){
this.options=new LzInheritedHash(this.options)
};for(var $1 in $0){
this.options[$1]=$0[$1]
}},"getOption",function($0){
return this.options[$0]
},"setOption",function($0,$1){
if(this.options===this["constructor"].prototype.options){
this.options=new LzInheritedHash(this.options)
};this.options[$0]=$1
},"determinePlacement",function($0,$1,$2){
if($1==null){
var $3=null
}else{
var $3=this.searchSubnodes("name",$1)
};return $3==null?this:$3
},"searchImmediateSubnodes",function($0,$1){
var $2=this.subnodes;if($2==null)return null;for(var $3=$2.length-1;$3>=0;$3--){
var $4=$2[$3];if($4[$0]==$1){
return $4
}};return null
},"searchSubnodes",function($0,$1){
var $2=this.subnodes?this.subnodes.concat():[];while($2.length>0){
var $3=$2;$2=new Array();for(var $4=$3.length-1;$4>=0;$4--){
var $5=$3[$4];if($5[$0]==$1){
return $5
};var $6=$5.subnodes;if($6){
for(var $7=$6.length-1;$7>=0;$7--){
$2.push($6[$7])
}}}};return null
},"searchParents",function($0){
var $1=this;do{
$1=$1.immediateparent;if($1[$0]!=null){
return $1
}}while($1!=canvas)
},"getUID",function(){
return this.__LZUID
},"childOf",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($0==null){
return false
};var $2=this;while($2.nodeLevel>=$0.nodeLevel){
if($2==$0){
return true
};$2=$2.immediateparent
};return false
},"destroy",function(){
if(this.__LZdeleted==true){
return
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this);if(this.subnodes!=null){
for(var $0=this.subnodes.length-1;$0>=0;$0--){
this.subnodes[$0].destroy()
}};if(this.$lzc$bind_id){
this.$lzc$bind_id.call(null,this,false)
};if(this.$lzc$bind_name){
this.$lzc$bind_name.call(null,this,false)
};var $1=this.parent;var $2=this.name;if($1!=null&&$2!=null){
if($1[$2]===this){
$1[$2]=null
};if(this.immediateparent[$2]===this){
this.immediateparent[$2]==null
}};if(this.__LZdelegates!=null){
for(var $0=this.__LZdelegates.length-1;$0>=0;$0--){
this.__LZdelegates[$0].unregisterAll()
}};this.__LZdelegates=null;if(this.immediateparent&&this.immediateparent.subnodes){
for(var $0=this.immediateparent.subnodes.length-1;$0>=0;$0--){
if(this.immediateparent.subnodes[$0]==this){
this.immediateparent.subnodes.splice($0,1);break
}}};this.data=null
},"animate",function($0,$1,$2,$3,$4){
switch(arguments.length){
case 3:
$3=null;
case 4:
$4=null;

};if($2==0){
var $5=$3?this[$0]+$1:$1;{
if(!this.__LZdeleted){
var $lzsc$1649055282="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1649055282]):this[$lzsc$1649055282] instanceof Function){
this[$lzsc$1649055282]($5)
}else{
this[$0]=$5;var $lzsc$1167464790=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1167464790):$lzsc$1167464790 instanceof LzEvent){
if($lzsc$1167464790.ready){
$lzsc$1167464790.sendEvent($5)
}}}}};return null
};var $6={attribute:$0,to:$1,duration:$2,start:true,relative:$3,target:this};for(var $7 in $4)$6[$7]=$4[$7];var $8=new LzAnimator(null,$6);return $8
},"toString",function(){
return this.getDebugIdentification()
},"getDebugIdentification",function(){
var $0=this["constructor"].tagname;if(this["name"]!=null){
$0+=" name: "+this.name
};if(this["id"]!=null){
$0+=" id: "+this.id
};return $0
},"$lzc$set_$datapath",function($0){
if($0===LzNode._ignoreAttribute){
return
}else if(!($0 instanceof Object)){};this.makeChild($0,true)
},"acceptTypeValue",function($0,$1){
var $2=$0?LzNode.presentationtypes[$0]:null;if($1!=null){
if($2!=null){
$1=$2.accept($1)
}else{
$1=DefaultPresentationType.accept($1)
}};return $1
},"acceptAttribute",function($0,$1,$2){
$2=this.acceptTypeValue($1,$2);if(this[$0]!==$2){
{
if(!this.__LZdeleted){
var $lzsc$556892863="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$556892863]):this[$lzsc$556892863] instanceof Function){
this[$lzsc$556892863]($2)
}else{
this[$0]=$2;var $lzsc$897703563=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$897703563):$lzsc$897703563 instanceof LzEvent){
if($lzsc$897703563.ready){
$lzsc$897703563.sendEvent($2)
}}}}}}},"presentTypeValue",function($0,$1){
var $2=LzNode.presentationtypes[$0];if($2!=null&&$2["present"]){
$1=$2.present($1)
}else{
$1=PresentationType.present($1)
};return $1
},"presentAttribute",function($0,$1){
return this.presentTypeValue($1,this[$0])
},"$lzc$presentAttribute_dependencies",function($0,$1,$2,$3){
return [$1,$2]
}],["tagname","node","attributes",new LzInheritedHash(),"mergeAttributes",function($0,$1){
for(var $2 in $0){
var $3=$0[$2];if($3===LzNode._ignoreAttribute){
delete $1[$2]
}else if(LzInitExpr["$lzsc$isa"]?LzInitExpr.$lzsc$isa($3):$3 instanceof LzInitExpr){
$1[$2]=$3
}else{
if(Object["$lzsc$isa"]?Object.$lzsc$isa($3):$3 instanceof Object){
var $4=$1[$2];if(Object["$lzsc$isa"]?Object.$lzsc$isa($4):$4 instanceof Object){
if((Array["$lzsc$isa"]?Array.$lzsc$isa($3):$3 instanceof Array)&&(Array["$lzsc$isa"]?Array.$lzsc$isa($4):$4 instanceof Array)){
$1[$2]=$3.concat($4);continue
}else if(($3.constructor===Object||(LzInheritedHash["$lzsc$isa"]?LzInheritedHash.$lzsc$isa($3):$3 instanceof LzInheritedHash))&&($4.constructor===Object||(LzInheritedHash["$lzsc$isa"]?LzInheritedHash.$lzsc$isa($4):$4 instanceof LzInheritedHash))){
var $5=new LzInheritedHash($4);for(var $6 in $3){
$5[$6]=$3[$6]
};$1[$2]=$5;continue
}}};$1[$2]=$3
}}},"mergeChildren",function($0,$1){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($1):$1 instanceof Array){
$0=$1.concat((Array["$lzsc$isa"]?Array.$lzsc$isa($0):$0 instanceof Array)?$0:[])
};return $0
},"_ignoreAttribute",{toString:function(){
return "_ignoreAttribute"
}},"__LZdelayedSetters",new LzInheritedHash(),"earlySetters",new LzInheritedHash({name:1,$events:2,$delegates:3,$classrootdepth:4,$datapath:5}),"__UIDs",0,"presentationtypes",{string:StringPresentationType,number:NumberPresentationType,numberExpression:NumberPresentationType,color:ColorPresentationType,"boolean":BooleanPresentationType,inheritableBoolean:BooleanPresentationType,expression:ExpressionPresentationType,size:SizePresentationType}]);lz[LzNode.tagname]=LzNode;Class.make("$lzc$class_userClassPlacement",null,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};$0.defaultplacement=$1
}],null);Class.make("LzDelegate",null,["__delegateID",0,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);if(LzEventable["$lzsc$isa"]?LzEventable.$lzsc$isa($0):$0 instanceof LzEventable){

}else if(!(Object["$lzsc$isa"]?Object.$lzsc$isa($0):$0 instanceof Object)){
return
};this.c=$0;var $4=$0[$1];if(Function["$lzsc$isa"]?Function.$lzsc$isa($4):$4 instanceof Function){
this.m=$4;if($4.length!=1){}};if($2!=null){
this.register($2,$3)
};this.__delegateID=LzDelegate.__nextID++
},"c",void 0,"m",void 0,"lastevent",0,"enabled",true,"event_called",false,"execute",function($0){
var $1=this.c;if(this.enabled&&$1){
if($1["__LZdeleted"]){
return
};var $2=this.m;return $2&&$2.call($1,$0)
}},"register",function($0,$1){
if(LzEventable["$lzsc$isa"]?LzEventable.$lzsc$isa($0):$0 instanceof LzEventable){

}else if(!(Object["$lzsc$isa"]?Object.$lzsc$isa($0):$0 instanceof Object)){
return
};if(this.c["__LZdeleted"]){
return
};var $2=$0[$1];if(!(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($2):$2 instanceof LzEvent)){
$2=new LzEvent($0,$1,this)
}else{
$2.addDelegate(this)
};this[this.lastevent++]=$2
},"unregisterAll",function(){
for(var $0=0;$0<this.lastevent;$0++){
this[$0].removeDelegate(this);this[$0]=null
};this.lastevent=0
},"unregisterFrom",function($0){
var $1=[];for(var $2=0;$2<this.lastevent;$2++){
var $3=this[$2];if($3===$0){
$3.removeDelegate(this)
}else{
$1.push($3)
};this[$2]=null
};this.lastevent=0;var $4=$1.length;for(var $2=0;$2<$4;$2++){
this[this.lastevent++]=$1[$2]
}},"disable",function(){
this.enabled=false
},"enable",function(){
this.enabled=true
},"toString",function(){
return "Delegate for "+this.c+" calls "+this.m+" "+this.__delegateID
}],["__nextID",1,"__LZdrainDelegatesQueue",function($0){
var $1=$0.length;var $2=0;if($2<$1){
var $3=new Array();var $4=new Array();while($2<$1){
var $5=$0[$2];var $6=$0[$2+1];var $7=$0[$2+2];$5.locked=true;$5.ready=false;$4.push($5);if(!$6.event_called){
$6.event_called=true;$3.push($6);if($6.c&&!$6.c.__LZdeleted&&$6.m){
$6.m.call($6.c,$7)
}};$2+=3
};while($6=$3.pop()){
$6.event_called=false
};while($5=$4.pop()){
$5.locked=false;$5.ready=$5.delegateList.length!=0
}};$0.length=0
}]);lz.Delegate=LzDelegate;Class.make("LzEvent",LzDeclaredEventClass,["delegateList",null,"$lzsc$initialize",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);var $3=$0["_events"];if($3==null){
$0._events=[this]
}else{
$3.push(this)
};$0[$1]=this;if($2){
this.delegateList=[$2];this.ready=true
}else{
this.delegateList=[]
}},"locked",false,"addDelegate",function($0){
this.ready=true;this.delegateList.push($0)
},"sendEvent",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.locked||!this.ready){
return
};this.locked=true;this.ready=false;var $1=this.delegateList;var $2=new Array();var $3;for(var $4=$1.length-1;$4>=0;$4--){
$3=$1[$4];if($3&&$3.enabled&&!$3.event_called){
$3.event_called=true;$2.push($3);var $5=$3.c;if($5&&!$5.__LZdeleted){
if($5.__LZdeferDelegates){
var $6=$5.__LZdelegatesQueue;if(!$6){
$6=$5.__LZdelegatesQueue=new Array()
};$6.push(this,$3,$0)
}else if($3.m){
$3.m.call($5,$0)
}}}};while($3=$2.pop()){
$3.event_called=false
};this.locked=false;this.ready=$1.length!=0
},"removeDelegate",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.delegateList;var $2=$1.length;for(var $3=0;$3<$2;$3++){
if($1[$3]===$0){
$1.splice($3,1);break
}};this.ready=$1.length!=0
},"clearDelegates",function(){
var $0=this.delegateList;while($0.length){
$0[0].unregisterFrom(this)
};this.ready=false
},"getDelegateCount",function(){
return this.delegateList.length
},"toString",function(){
return "LzEvent"
}],null);lz.Event=LzEvent;Class.make("LzKernelUtils",null,null,["CSSDimension",function($0,$1){
switch(arguments.length){
case 1:
$1="px";

};var $2=$0;if(isNaN($0)){
if(typeof $0=="string"&&$0.indexOf("%")==$0.length-1&&!isNaN($0.substring(0,$0.length-1))){
return $0
}else{
$2=0
}}else if($0===Infinity){
$2=~0>>>1
}else if($0===-Infinity){
$2=~(~0>>>1)
};return Math.round($2)+$1
},"__rrfactors",null,"getRoundrectFactors",function(){
var $0=Math.PI/4;var $1=$0/2;var $2=Math.SQRT2/2;var $3=$2;var $4=$2;var $5=Math.sin($1);var $6=Math.cos($1);var $7=[0,$2,1,$2];var $8=[1,$2,0,-$2];var $9=[];for(var $a=0;$a<4;++$a){
var $b=$7[$a];var $c=$8[$a];$9.push(($b*$6+$c*$5)/$6,(-$c*$6+$b*$5)/$6,$b*$4+$c*$3,-$c*$4+$b*$3)
};for(var $a=0;$a<4*4;++$a){
$9[4*4+$a]=-$9[$a]
};return $9
},"rect",function(context,$0,$1,$2,$3,cornerRadius){
var $4;$4=function($0,$1,$2){
var $3=$1+factors[4*$0+0]*cornerRadius;var $4=$2+factors[4*$0+1]*cornerRadius;var $5=$1+factors[4*$0+2]*cornerRadius;var $6=$2+factors[4*$0+3]*cornerRadius;context[curvemethod]($3,$4,$5,$6)
};switch(arguments.length){
case 5:
cornerRadius=0;

};if(cornerRadius>0){
if(cornerRadius>Math.min($2,$3)/2){
cornerRadius=Math.min($2,$3)/2
};var factors=LzKernelUtils.__rrfactors;if(!factors){
factors=LzKernelUtils.__rrfactors=LzKernelUtils.getRoundrectFactors()
};var curvemethod=context["curveTo"]?"curveTo":"quadraticCurveTo";context.moveTo($0+cornerRadius,$1);context.lineTo($0+$2-cornerRadius,$1);$4(0,$0+$2-cornerRadius,$1+cornerRadius);$4(1,$0+$2-cornerRadius,$1+cornerRadius);context.lineTo($0+$2,$1+$3-cornerRadius);$4(2,$0+$2-cornerRadius,$1+$3-cornerRadius);$4(3,$0+$2-cornerRadius,$1+$3-cornerRadius);context.lineTo($0+cornerRadius,$1+$3);$4(4,$0+cornerRadius,$1+$3-cornerRadius);$4(5,$0+cornerRadius,$1+$3-cornerRadius);context.lineTo($0,$1+cornerRadius);$4(6,$0+cornerRadius,$1+cornerRadius);$4(7,$0+cornerRadius,$1+cornerRadius)
}else{
context.moveTo($0,$1);context.lineTo($0+$2,$1);context.lineTo($0+$2,$1+$3);context.lineTo($0,$1+$3);context.lineTo($0,$1)
}}]);var LzIdleKernel={__callbacks:[],__update:function(){
var $0=LzIdleKernel;var $1=$0.__callbacks;var $2=LzTimeKernel.getTimer();for(var $3=$1.length-2;$3>=0;$3-=2){
var $4=$1[$3];var $5=$1[$3+1];$4[$5]($2)
}},__intervalID:null,addCallback:function($0,$1){
var $2=LzIdleKernel;var $3=$2.__callbacks.slice(0);for(var $4=$3.length-2;$4>=0;$4-=2){
if($3[$4]===$0&&$3[$4+1]==$1){
return
}};$3.push($0,$1);$2.__callbacks=$3;if($3.length>0&&$2.__intervalID==null){
$2.__intervalID=setInterval(LzIdleKernel.__update,1000/$2.__fps)
}},removeCallback:function($0,$1){
var $2=LzIdleKernel;var $3=$2.__callbacks.slice(0);for(var $4=$3.length-2;$4>=0;$4-=2){
if($3[$4]===$0&&$3[$4+1]==$1){
var $5=$3.splice($4,2)
}};$2.__callbacks=$3;if($3.length==0&&$2.__intervalID!=null){
clearInterval($2.__intervalID);$2.__intervalID=null
};return $5
},__fps:30,setFrameRate:function($0){
LzIdleKernel.__fps=$0;if(LzIdleKernel.__intervalID!=null){
clearInterval(LzIdleKernel.__intervalID);LzIdleKernel.__intervalID=setInterval(LzIdleKernel.__update,1000/$0)
}}};Class.make("LzLibraryCleanup",LzNode,["lib",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"$lzc$set_libname",function($0){
this.lib=LzLibrary.findLibrary($0);this.lib.loadfinished()
}],["attributes",new LzInheritedHash(LzNode.attributes)]);var LzResourceLibrary={};var getTimer=function(){
return LzTimeKernel.getTimer()
};global=window;lz.BrowserUtils={__scopeid:0,__scopes:[],getcallbackstr:function($0,$1){
var $2=lz.BrowserUtils.__scopeid++;if($0.__callbacks==null){
$0.__callbacks={sc:$2}}else{
$0.__callbacks[$2]=$2
};lz.BrowserUtils.__scopes[$2]=$0;return "if (lz.BrowserUtils.__scopes["+$2+"]) lz.BrowserUtils.__scopes["+$2+"]."+$1+".apply(lz.BrowserUtils.__scopes["+$2+"], [])"
},getcallbackfunc:function($0,name,args){
var sc=lz.BrowserUtils.__scopeid++;if($0.__callbacks==null){
$0.__callbacks={sc:sc}}else{
$0.__callbacks[sc]=sc
};lz.BrowserUtils.__scopes[sc]=$0;return( function(){
var $0=lz.BrowserUtils.__scopes[sc];if($0)return $0[name].apply($0,args)
})
},removecallback:function($0){
if($0.__callbacks!=null){
for(var $1 in $0.__callbacks){
var $2=$0.__callbacks[$1];delete lz.BrowserUtils.__scopes[$2]
};delete $0.__callbacks
}},hasFeature:function($0,$1){
return document.implementation&&document.implementation.hasFeature&&document.implementation.hasFeature($0,$1)
}};var LzPool=function($0,$1,$2,$3){
this.cache={};if(typeof $0=="function")this.getter=$0;if(typeof $1=="function")this.cacheHit=$1;if(typeof $2=="function")this.destroyer=$2;if($3)this.owner=$3
};LzPool.prototype.cache=null;LzPool.prototype.get=function($0,$1){
var $2=Array.prototype.slice.call(arguments,2);var $3=this.cache[$0];if($1||$3==null){
$2.unshift($0);$3=this.getter.apply(this,$2);if(!$1)this.cache[$0]=$3
}else if(this.cacheHit){
$2.unshift($0,$3);this.cacheHit.apply(this,$2)
};if(this.owner)$3.owner=this.owner;return $3
};LzPool.prototype.flush=function($0){
if(this.destroyer)this.destroyer($0,this.cache[$0]);delete this.cache[$0]
};LzPool.prototype.destroy=function(){
for(var $0 in this.cache){
this.flush($0)
};this.owner=null;this.cache=null
};LzPool.prototype.getter=null;LzPool.prototype.destroyer=null;LzPool.prototype.cacheHit=null;var LzKeyboardKernel={__downKeysHash:{},__keyboardEvent:function($0){
if(!$0)$0=window.event;var $1={};var $2=false;var $3=$0["keyCode"];var $4=$0.type;var $5=LzKeyboardKernel.__downKeysHash;if($3>=0&&$3!=16&&$3!=17&&$3!=18&&$3!=224){
var $6=String.fromCharCode($3).toLowerCase();if($4=="keyup"){
if($5[$6]!=null){
$1[$6]=false;$2=true
};$5[$6]=null
}else if($4=="keydown"){
if($5[$6]==null){
$1[$6]=true;$2=true
};$5[$6]=$3
}};if(LzKeyboardKernel.__updateControlKeys($0,$1)){
$2=true
};if($2){
var $7=LzKeyboardKernel.__scope;var $8=LzKeyboardKernel.__callback;if($7&&$7[$8]){
$7[$8]($1,$3,"on"+$4)
}};if($3>=0){
if($3==9){
$0.cancelBubble=true;return false
}else if(LzKeyboardKernel.__cancelKeys&&($3==13||$3==0||$3==37||$3==38||$3==39||$3==40||$3==8)){
$0.cancelBubble=true;return false
}}},__updateControlKeys:function($0,$1){
var $2=LzSprite.quirks;var $3=LzKeyboardKernel.__downKeysHash;var $4=false;if($1){
var $5=false
}else{
$1={};var $5=true
};var $6=$0["altKey"];if($3["alt"]!=null!=$6){
$3["alt"]=$6?18:null;$1["alt"]=$6;$4=true;if($2["alt_key_sends_control"]){
$1["control"]=$1["alt"]
}};var $7=$0["ctrlKey"];if($3["control"]!=null!=$7){
$3["control"]=$7?17:null;$1["control"]=$7;$4=true
};var $8=$0["shiftKey"];if($3["shift"]!=null!=$8){
$3["shift"]=$8?16:null;$1["shift"]=$8;$4=true
};if($2["hasmetakey"]){
var $9=$0["metaKey"];if($3["meta"]!=null!=$9){
$3["meta"]=$9?224:null;$1["meta"]=$9;$4=true;$1["control"]=$9
}};if($4&&$5){
var $a=LzKeyboardKernel.__scope;var $b=LzKeyboardKernel.__callback;if($a&&$a[$b]){
$a[$b]($1,0,"on"+$0.type)
}};return $4
},__allKeysUp:function(){
var $0=null;var $1=false;var $2=null;var $3=LzKeyboardKernel.__downKeysHash;for(var $4 in $3){
if($3[$4]!=null){
$1=true;if(!$0){
$0={}};$0[$4]=false;if($4.length==1){
if(!$2){
$2=[]
};$2.push($3[$4])
};$3[$4]=null
}};var $5=LzKeyboardKernel.__scope;var $6=LzKeyboardKernel.__callback;if($1&&$5&&$5[$6]){
if(!$2){
$5[$6]($0,0,"onkeyup")
}else for(var $7=0,$8=$2.length;$7<$8;$7++){
$5[$6]($0,$2[$7],"onkeyup")
}};LzKeyboardKernel.__downKeysHash={}},__callback:null,__scope:null,__cancelKeys:true,__lockFocus:null,setCallback:function($0,$1){
this.__scope=$0;this.__callback=$1
},setKeyboardControl:function($0,$1){
if(!$1&&LzKeyboardKernel.__lockFocus){
$0=true
};var $2=null;var $3=LzSprite.__rootSprite.options.cancelkeyboardcontrol!=true||true;if($3&&$0){
$2=LzKeyboardKernel.__keyboardEvent
};if(LzSprite.quirks.keyboardlistentotop){
var $4=window.top.document
}else{
var $4=document
};$4.onkeydown=$2;$4.onkeyup=$2;$4.onkeypress=$2
},gotLastFocus:function(){
if(!LzSprite.__mouseActivationDiv.mouseisover)LzKeyboardKernel.setKeyboardControl(false)
},setGlobalFocusTrap:function($0){
LzKeyboardKernel.__lockFocus=$0;if(LzSprite.quirks.activate_on_mouseover){
var $1=LzSprite.__mouseActivationDiv;if($0){
$1.onmouseover()
}else{
$1.onmouseout()
}}}};var LzMouseKernel={__lastMouseDown:null,__lastMouseOver:null,__x:0,__y:0,owner:null,__showncontextmenu:null,__mouseEvent:function($0){
if(!$0){
$0=window.event
};var $1=$0["target"]?$0.target:$0["srcElement"];var $2="on"+$0.type;if(window["LzKeyboardKernel"]&&LzKeyboardKernel["__updateControlKeys"]){
LzKeyboardKernel.__updateControlKeys($0)
};var $3=window["LzInputTextSprite"]&&LzInputTextSprite.prototype;if($3&&$3.__lastshown!=null){
if(LzSprite.quirks.fix_ie_clickable){
$3.__hideIfNotFocused($2,$1)
}else if($2!="onmousemove"){
$3.__hideIfNotFocused()
}};if($2=="onmousemove"){
LzMouseKernel.__sendMouseMove($0);if($3&&$3.__lastshown!=null){
if($1&&$1.owner&&!($1.owner instanceof LzInputTextSprite)){
if(!$3.__lastshown.__isMouseOver()){
$3.__lastshown.__hide()
}}}}else if($2=="oncontextmenu"||$0.button==2){
LzMouseKernel.__sendMouseMove($0);if(LzSprite.prototype.quirks.has_dom2_mouseevents){
if($2=="oncontextmenu"){
var $4=LzMouseKernel.__findContextMenu($0);if($4!=null){
return false
}else{
return true
}}else if($2=="onmousedown"){
if($1){
return LzMouseKernel.__showContextMenu($0)
}}}else if($2=="oncontextmenu"){
if($1){
return LzMouseKernel.__showContextMenu($0)
}}}else{
LzMouseKernel.__sendEvent($2)
}},__sendEvent:function($0,$1){
if($0=="onclick"&&$1==null){
return
};if(LzMouseKernel.__callback){
LzMouseKernel.__scope[LzMouseKernel.__callback]($0,$1)
}},__callback:null,__scope:null,__mouseupEvent:function($0){
if(LzMouseKernel.__lastMouseDown!=null){
LzMouseKernel.__lastMouseDown.__globalmouseup($0)
}else{
if(!$0){
$0=window.event
};var $1=$0["target"]?$0.target:$0["srcElement"];if($1&&$1.owner!==LzSprite.__rootSprite)return;LzMouseKernel.__mouseEvent($0)
}},setCallback:function($0,$1){
this.__scope=$0;this.__callback=$1
},__mousecontrol:false,setMouseControl:function($0){
if($0==LzMouseKernel.__mousecontrol)return;LzMouseKernel.__mousecontrol=$0;var $1=lz.embed;if($0){
$1.attachEventHandler(document,"mousemove",LzMouseKernel,"__mouseEvent");$1.attachEventHandler(document,"mousedown",LzMouseKernel,"__mouseEvent");$1.attachEventHandler(document,"mouseup",LzMouseKernel,"__mouseupEvent");$1.attachEventHandler(document,"click",LzMouseKernel,"__mouseEvent");try{
if(window.top!=window)$1.attachEventHandler(window.top.document,"mouseup",LzMouseKernel,"__mouseupEvent")
}
catch($2){}}else{
$1.removeEventHandler(document,"mousemove",LzMouseKernel,"__mouseEvent");$1.removeEventHandler(document,"mousedown",LzMouseKernel,"__mouseEvent");$1.removeEventHandler(document,"mouseup",LzMouseKernel,"__mouseupEvent");$1.removeEventHandler(document,"click",LzMouseKernel,"__mouseEvent");try{
if(window.top!=window)$1.removeEventHandler(window.top.document,"mouseup",LzMouseKernel,"__mouseupEvent")
}
catch($2){}};document.oncontextmenu=$0?LzMouseKernel.__mouseEvent:null
},__showhand:"pointer",showHandCursor:function($0){
var $1=$0==true?"pointer":"default";this.__showhand=$1;LzMouseKernel.setCursorGlobal($1)
},setCursorGlobal:function($0){
if(LzSprite.quirks.no_cursor_colresize){
return
};var $0=LzSprite.__defaultStyles.hyphenate($0);LzSprite.prototype.__setCSSClassProperty(".lzclickdiv","cursor",$0);LzSprite.prototype.__setCSSClassProperty(".lzdiv","cursor",$0);LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv","cursor",$0);LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv","cursor",$0)
},restoreCursor:function(){
if(LzSprite.quirks.no_cursor_colresize){
return
};if(LzMouseKernel.__amLocked)return;LzSprite.prototype.__setCSSClassProperty(".lzclickdiv","cursor",LzMouseKernel.__showhand);LzSprite.prototype.__setCSSClassProperty(".lzdiv","cursor","default");LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv","cursor","default");LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv","cursor","default")
},lock:function(){
LzMouseKernel.__amLocked=true
},unlock:function(){
LzMouseKernel.__amLocked=false;LzMouseKernel.restoreCursor()
},disableMouseTemporarily:function(){
this.setGlobalClickable(false);this.__resetonmouseover=true
},__resetonmouseover:false,__resetMouse:function(){
if(this.__resetonmouseover){
this.__resetonmouseover=false;this.setGlobalClickable(true)
}},__globalClickable:true,setGlobalClickable:function($0){
if($0==this.__globalClickable)return;this.__globalClickable=$0;var $1=document.getElementById("lzcanvasclickdiv");if(LzSprite.quirks.fix_ie_clickable){
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv","display",$0?"":"none")
};if($1)$1.style.display=$0?"":"none"
},__sendMouseMove:function($0,$1,$2){
if($0.pageX||$0.pageY){
LzMouseKernel.__x=$0.pageX;LzMouseKernel.__y=$0.pageY
}else if($0.clientX||$0.clientY){
var $3=document.body,$4=document.documentElement;LzMouseKernel.__x=$0.clientX+$3.scrollLeft+$4.scrollLeft;LzMouseKernel.__y=$0.clientY+$3.scrollTop+$4.scrollTop
};if($1){
LzMouseKernel.__x+=$1
};if($2){
LzMouseKernel.__y+=$2
};if($0.type=="mousemove"){
LzMouseKernel.__sendEvent("onmousemove")
}},__showContextMenu:function($0){
var $1=LzMouseKernel.__findContextMenu($0);if($1){
$1.kernel.__show();return $1.kernel.showbuiltins
}},__findContextMenu:function($0){
var $1=LzSprite.__rootSprite.__contextmenu;var $2=LzSprite.quirks;if(document.elementFromPoint){
var $3=$2.swf8_contextmenu;var $4=LzMouseKernel.__x;var $5=LzMouseKernel.__y;var $6=LzSprite.__rootSprite.__LZdiv;var $7=[];if($2.fix_contextmenu){
$7.push($6,$6.style.display);var $8=$6.style.zIndex;$6.style.zIndex=-1000;var $9=LzSprite.__rootSprite.__LZclickcontainerdiv;var $a=$9.style.zIndex;$7.push($9,$9.style.display);$9.style.zIndex=-9999
};do{
var $b=document.elementFromPoint($4,$5);if(!$b){
break
}else{
var $c=$b.owner;if(!$c){

}else if($c.__contextmenu){
$1=$c.__contextmenu;break
}else if($2.ie_elementfrompoint&&$c.scrolldiv===$b){

}else if($3&&($c.__LZdiv===$b&&$c.bgcolor!=null||$c instanceof LzTextSprite)){
break
};$7.push($b,$b.style.display);$b.style.display="none"
}}while($b!==$6&&$b.tagName!="HTML");for(var $d=$7.length-1;$d>=0;$d-=2){
$7[$d-1].style.display=$7[$d]
};if($2.fix_contextmenu){
$6.style.zIndex=$8;$9.style.zIndex=$a
}}else{
var $e=($0.srcElement||$0.target).owner;if($e){
while($e.__parent){
if($e.__contextmenu){
var $f=$e.getMouse();if($f.x>=0&&$f.x<$e.width&&$f.y>=0&&$f.y<$e.height){
$1=$e.__contextmenu;break
}};$e=$e.__parent
}}};return $1
}};Class.make("LzBrowserKernel",null,null,["loadURL",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;

};if($1!=null){
if($2!=null){
window.open($0,$1,$2)
}else{
window.open($0,$1)
}}else{
window.location=$0
}},"loadJS",function($0,$1){
LzBrowserKernel.loadURL("javascript:"+$0+";void(0);",$1)
},"callJS",function($0,$1){
var $2=null;var $3=eval($0);var $4=$0.split(".");if($4.length>1){
$4.pop();$2=eval($4.join("."))
};var $5=Array.prototype.slice.call(arguments,2);if($3){
var $6=$3.apply($2,$5)
};if($1&&typeof $1=="function")$1($6);return $6
},"setHistory",function($0){
lz.embed.history.set($0)
},"callMethod",function($0){
return eval($0)
},"getVersion",function(){
return navigator.userAgent
},"getOS",function(){
return navigator.platform
},"getLoadURL",function(){
if(LzSprite.__rootSprite&&LzSprite.__rootSprite._url){
var $0=LzSprite.__rootSprite._url
}else{
var $0=lz.embed.__propcache.url
};if(!$0)$0=new String(window.location);var $1=$0.indexOf(":");var $2=$0.indexOf("/");if($1>-1){
if($0.indexOf("://")==$1){
return $0
}else if($0.charAt($1+1)=="/"){
$0=$0.substring(0,$1+1)+"/"+$0.substring($1+1);return $0
}else{
var $3=new LzURL(new String(window.location));$0=$0.substring(0,$1+1)+"/"+$3.path+$0.substring($1+1);return $0
}}else{
if($2==0){
return $0
}else{
var $4=new String(window.location);var $5=$4.lastIndexOf("/");$4=$4.substring(0,$5+1);return $4+$0
}}},"getInitArg",function($0){
return global[$0]
},"getAppID",function(){
return LzSprite.__rootSprite._id
},"isAAActive",function(){
return false
}]);var LzSprite=function($0,$1){
if($0==null)return;this.constructor=arguments.callee;this.owner=$0;this.uid=LzSprite.prototype.uid++;this.aadescriptionDiv=null;var $2=this.quirks;if($1){
this.isroot=true;this.__initdone=false;LzSprite.__rootSprite=this;var div=document.createElement("div");div.className="lzcanvasdiv";$2["scrollbar_width"]=LzSprite._getScrollbarWidth();if($2.ie6_improve_memory_performance){
try{
document.execCommand("BackgroundImageCache",false,true)
}
catch($3){}};var $4=lz.embed.__propcache;var $5=LzSprite.__rootSpriteContainer=$4.appenddiv;var $6=$5;$5.style.margin=0;$5.style.padding=0;$5.style.border="0 none";$5.style.overflow="hidden";if($2["container_divs_require_overflow"]){
$6=document.createElement("div");$6.className="lzappoverflow";$5.appendChild($6);$6.owner=this;LzSprite.__rootSpriteOverflowContainer=$6
};if($2.fix_contextmenu){
var $7=document.createElement("div");$7.className="lzcanvascontextdiv";$7.id="lzcanvascontextdiv";$6.appendChild($7);$7.owner=this;this.__LZcontextcontainerdiv=$7
};if($4.bgcolor){
div.style.backgroundColor=$4.bgcolor;this.bgcolor=$4.bgcolor
};if($4.id){
this._id=$4.id
};if($4.url){
this._url=$4.url
};var $8=$4.options;if($8){
this.options=$8
};LzSprite.blankimage=$8.serverroot+LzSprite.blankimage;if($2.use_css_sprites&&$8.usemastersprite){
$2.use_css_master_sprite=$8.usemastersprite;var $9=LzResourceLibrary&&LzResourceLibrary.__allcss&&LzResourceLibrary.__allcss.path;if($9){
LzSprite.__masterspriteurl=$9;var $a=new Image();$a.src=$9
}};LzSprite.__defaultStyles.writeCSS($2.write_css_with_createstylesheet);$6.appendChild(div);this.__LZdiv=div;if($2.fix_clickable){
var $b=document.createElement("div");$b.className="lzcanvasclickdiv";$b.id="lzcanvasclickdiv";$6.appendChild($b);this.__LZclickcontainerdiv=$b
};if($2["css_hide_canvas_during_init"]){
var $c="display";var $d="none";if($2["safari_visibility_instead_of_display"]){
$c="visibility";$d="hidden"
};this.__LZdiv.style[$c]=$d;if($2["fix_clickable"])this.__LZclickcontainerdiv.style[$c]=$d;if($2["fix_contextmenu"])this.__LZcontextcontainerdiv.style[$c]=$d
};if($2.activate_on_mouseover){
div.mouseisover=false;div.onmouseover=function($0){
if(LzSprite.quirks.focus_on_mouseover){
if(LzSprite.prototype.getSelectedText()==""){
div.focus()
}};if(LzInputTextSprite.prototype.__focusedSprite==null)LzKeyboardKernel.setKeyboardControl(true);LzMouseKernel.setMouseControl(true);this.mouseisover=true
};div.onmouseout=function($0){
if(!$0){
$0=window.event;var $1=$0.toElement
}else{
var $1=$0.relatedTarget
};var $2=LzSprite.quirks;if($2.inputtext_anonymous_div){
try{
$1&&$1.parentNode
}
catch($0){
return
}};var $3=false;if($1){
var $4=LzContextMenuKernel.lzcontextmenu;if($1.owner&&$1.className.indexOf("lz")==0){
$3=true
}else if($4&&($1===$4||$1.parentNode===$4)){
$3=true
}};if($3){
var $5=LzMouseKernel.__globalClickable;if($2.fix_ie_clickable){
LzMouseKernel.setGlobalClickable(true)
};if($2.focus_on_mouseover){
if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.prototype.getSelectedText()==""){
div.focus()
}}};LzKeyboardKernel.setKeyboardControl(true);LzMouseKernel.setMouseControl(true);LzMouseKernel.__resetMouse();this.mouseisover=true;if($2.fix_clickable&&!$5&&LzMouseKernel.__globalClickable){
var $6=$0["target"]?$0.target:$0["srcElement"];if($6){
var $7=$6["owner"];if(LzSprite["$lzsc$isa"]?LzSprite.$lzsc$isa($7):$7 instanceof LzSprite){
$7=$7["owner"]
};if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($7):$7 instanceof LzView){
LzMouseKernel.__sendEvent("onmouseout",$7)
}}}}else{
if($2.focus_on_mouseover){
if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.prototype.getSelectedText()==""){
div.blur()
}}};LzKeyboardKernel.setKeyboardControl(false);LzMouseKernel.setMouseControl(false);this.mouseisover=false
}};LzSprite.__mouseActivationDiv=div
};var $e=document.createElement("div");lz.embed.__setAttr($e,"id","lzTextSizeCache");document.body.appendChild($e)
}else{
this.__LZdiv=document.createElement("div");this.__LZdiv.className="lzdiv";if($2.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lzdiv"
}};this.__LZdiv.owner=this;if($2.fix_clickable){
this.__LZclickcontainerdiv.owner=this
};if($2.ie_leak_prevention){
this.__sprites[this.uid]=this
}};LzSprite.__defaultStyles={lzdiv:{position:"absolute"},lzclickdiv:{position:"absolute"},lzcanvasdiv:{position:"absolute"},lzcanvasclickdiv:{zIndex:100000,position:"absolute"},lzcanvascontextdiv:{position:"absolute"},lzappoverflow:{position:"absolute",overflow:"hidden"},lztextcontainer:{position:"absolute",paddingTop:"2px",paddingRight:"2px",paddingBottom:"2px",paddingLeft:"2px",cursor:"default"},lzinputtextcontainer:{position:"absolute",overflow:"hidden",paddingTop:"0px",paddingRight:"3px",paddingBottom:"4px",paddingLeft:"1px"},lzinputtextcontainer_click:{position:"absolute",paddingTop:"0px",paddingRight:"3px",paddingBottom:"4px",paddingLeft:"1px"},lzinputtextmultilinecontainer:{position:"absolute",overflow:"hidden",paddingTop:"1px",paddingRight:"3px",paddingBottom:"3px",paddingLeft:"1px"},lzinputtextmultilinecontainer_click:{position:"absolute",paddingTop:"1px",paddingRight:"3px",paddingBottom:"3px",paddingLeft:"1px"},lztext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",whiteSpace:"normal",position:"absolute",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none"},lzswftext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",whiteSpace:"normal",position:"absolute",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all"},lzinputtext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none"},lzswfinputtext:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all",outline:"none",resize:"none"},lzswfinputtextmultiline:{fontFamily:"Verdana,Vera,sans-serif",fontStyle:"normal",fontWeight:"normal",fontSize:"11px",width:"100%",height:"100%",borderWidth:0,backgroundColor:"transparent",position:"absolute",overflow:"hidden",lineHeight:"1.2em",textAlign:"left",textIndent:"0px",letterSpacing:"0px",textDecoration:"none",wordWrap:"break-word",MsWordBreak:"break-all",outline:"none",resize:"none"},lztextlink:{cursor:"pointer"},lzaccessibilitydiv:{display:"none"},lzcontext:{position:"absolute"},lzimg:{position:"absolute",backgroundRepeat:"no-repeat"},"#lzTextSizeCache":{zoom:1},lzgraphicscanvas:{position:"absolute"},writeCSS:function($0){
var $1=[];var $2="";for(var $3 in this){
if($3=="writeCSS"||$3=="hyphenate"||$3=="__replace"||$3=="__re")continue;$2+=$3.indexOf("#")==-1?".":"";$2+=$3+"{";for(var $4 in this[$3]){
var $5=this[$3][$4];$2+=this.hyphenate($4)+":"+$5+";"
};$2+="}"
};$2+=LzFontManager.generateCSS();if($0){
if(!document.styleSheets["lzstyles"]){
var $6=document.createStyleSheet();$6.owningElement.id="lzstyles";$6.cssText=$2
}}else{
var $7=document.createElement("style");lz.embed.__setAttr($7,"type","text/css");$7.appendChild(document.createTextNode($2));var $8=document.getElementsByTagName("head");$8[0].appendChild($7)
}},__re:new RegExp("[A-Z]","g"),hyphenate:function($0){
return $0.replace(this.__re,this.__replace)
},__replace:function($0){
return "-"+$0.toLowerCase()
}};LzSprite.prototype.uid=0;LzSprite.quirks={fix_clickable:true,fix_ie_background_height:false,fix_ie_clickable:false,ie_alpha_image_loader:false,ie_leak_prevention:false,prevent_selection:false,ie_elementfrompoint:false,invisible_parent_image_sizing_fix:false,emulate_flash_font_metrics:true,inner_html_strips_newlines:true,inner_html_no_entity_apos:false,css_hide_canvas_during_init:true,firefox_autocomplete_bug:false,hand_pointer_for_clickable:true,alt_key_sends_control:false,safari_textarea_subtract_scrollbar_height:false,no_cursor_colresize:false,safari_visibility_instead_of_display:false,preload_images_only_once:false,absolute_position_accounts_for_offset:false,canvas_div_cannot_be_clipped:false,inputtext_parents_cannot_contain_clip:false,set_height_for_multiline_inputtext:false,ie_opacity:false,text_measurement_use_insertadjacenthtml:false,text_content_use_inner_text:false,text_selection_use_range:false,document_size_use_offsetheight:false,text_ie_carriagereturn:false,ie_paste_event:false,safari_paste_event:false,text_event_charcode:true,keypress_function_keys:true,ie_timer_closure:false,keyboardlistentotop:false,document_size_compute_correct_height:false,ie_mouse_events:false,fix_inputtext_with_parent_resource:false,activate_on_mouseover:true,ie6_improve_memory_performance:false,text_height_includes_padding:false,inputtext_size_includes_margin:false,listen_for_mouseover_out:true,focus_on_mouseover:true,textstyle_on_textdiv:false,textdeco_on_textdiv:false,use_css_sprites:true,preload_images:true,scrollbar_width:15,inputtext_strips_newlines:false,swf8_contextmenu:true,inputtext_anonymous_div:false,clipped_scrollbar_causes_display_turd:false,hasmetakey:true,textgrabsinputtextfocus:false,input_highlight_bug:false,autoscroll_textarea:false,fix_contextmenu:true,size_blank_to_zero:true,has_dom2_mouseevents:false,container_divs_require_overflow:false,fix_ie_css_syntax:false,match_swf_letter_spacing:false,use_css_master_sprite:false,write_css_with_createstylesheet:false};LzSprite.prototype.capabilities={rotation:false,scalecanvastopercentage:false,readcanvassizefromsprite:true,opacity:true,colortransform:false,audio:false,accessibility:true,htmlinputtext:false,advancedfonts:false,bitmapcaching:false,persistence:false,clickmasking:false,minimize_opacity_changes:false,history:true,runtimemenus:false,setclipboard:false,proxypolicy:false,linescrolling:false,allowfullscreen:false,setid:true,globalfocustrap:false,"2dcanvas":true,dropshadows:false,cornerradius:false,rgba:false,css2boxmodel:true};LzSprite.__updateQuirks=function(){
var $0=LzSprite.quirks;var $1=LzSprite.prototype.capabilities;var $2=LzSprite.__defaultStyles;if(window["lz"]&&lz.embed&&lz.embed.browser){
var $3=lz.embed.browser;if($3.isIE){
if($3.version<7){
$0["ie_alpha_image_loader"]=true;$0["document_size_compute_correct_height"]=true;$0["ie6_improve_memory_performance"]=true
}else{
$0["prevent_selection"]=true;$0["invisible_parent_image_sizing_fix"]=true;if($3.osversion>=6){
$0["ie_alpha_image_loader"]=true
}};$0["ie_opacity"]=true;$0["ie_timer_closure"]=true;$0["ie_leak_prevention"]=true;$0["fix_ie_clickable"]=true;$0["fix_ie_background_height"]=true;$0["inner_html_no_entity_apos"]=true;$1["minimize_opacity_changes"]=true;$0["set_height_for_multiline_inputtext"]=true;$0["text_measurement_use_insertadjacenthtml"]=true;$0["text_content_use_inner_text"]=true;$0["text_selection_use_range"]=true;$0["text_ie_carriagereturn"]=true;$0["ie_paste_event"]=true;$0["keypress_function_keys"]=false;$0["text_event_charcode"]=false;$0["ie_mouse_events"]=true;$0["fix_inputtext_with_parent_resource"]=true;$0["inputtext_size_includes_margin"]=true;$0["focus_on_mouseover"]=false;$0["textstyle_on_textdiv"]=true;$0["use_css_sprites"]=!$0["ie_alpha_image_loader"];$0["textgrabsinputtextfocus"]=true;$0["ie_elementfrompoint"]=true;$0["fix_ie_css_syntax"]=true;$0["write_css_with_createstylesheet"]=true;$0["hasmetakey"]=false
}else if($3.isSafari||$3.isChrome){
$0["safari_visibility_instead_of_display"]=true;$0["absolute_position_accounts_for_offset"]=true;if($3.version<525.18){
$0["canvas_div_cannot_be_clipped"]=true;$0["invisible_parent_image_sizing_fix"]=true;$0["safari_textarea_subtract_scrollbar_height"]=true
};$0["document_size_use_offsetheight"]=true;if($3.version>523.1){
$1["rotation"]=true;$2.lzdiv.WebkitTransformOrigin="0 0";$1["dropshadows"]=true;$1["cornerradius"]=true;$1["rgba"]=true
};$0["safari_paste_event"]=true;$0["keypress_function_keys"]=false;if($3.version<523.15){
$0["keyboardlistentotop"]=true
};if($3.version>=530.19){
$1["threedtransform"]=true
};if($3.isIphone){
$0["canvas_div_cannot_be_clipped"]=true
};$0["inputtext_strips_newlines"]=true;$0["prevent_selection"]=true;$0["container_divs_require_overflow"]=true
}else if($3.isOpera){
$0["invisible_parent_image_sizing_fix"]=true;$0["no_cursor_colresize"]=true;$0["absolute_position_accounts_for_offset"]=true;$0["canvas_div_cannot_be_clipped"]=true;$0["document_size_use_offsetheight"]=true;$0["text_event_charcode"]=false;$0["textdeco_on_textdiv"]=true;$0["text_ie_carriagereturn"]=true
}else if($3.isFirefox){
$0["inputtext_anonymous_div"]=true;if($3.OS=="Windows"){
$0["clipped_scrollbar_causes_display_turd"]=true;$0["input_highlight_bug"]=true
};if($3.version<2){
$0["firefox_autocomplete_bug"]=true
}else if($3.version<3){
$2.lzswftext.lineHeight="119%";$2.lzswfinputtext.lineHeight="119%";$2.lzswfinputtextmultiline.lineHeight="119%"
}else if($3.version<4){
if($3.subversion<6){
$0["text_height_includes_padding"]=true
};if($3.version<3.5){
$0["container_divs_require_overflow"]=true
}};$0["autoscroll_textarea"]=true;if($3.version>=3.5){
$1["rotation"]=true;$2.lzdiv.MozTransformOrigin="0 0"
};if($3.version>=3.1){
$1["dropshadows"]=true;$1["cornerradius"]=true;$1["rgba"]=true
}};if($3.OS=="Mac"){
$0["detectstuckkeys"]=true;$0["alt_key_sends_control"]=true;$0["match_swf_letter_spacing"]=true
};if($0["hand_pointer_for_clickable"]){
$2.lzclickdiv.cursor="pointer"
};if($0["inner_html_strips_newlines"]==true){
LzSprite.prototype.inner_html_strips_newlines_re=RegExp("$","mg")
};if($3.isFirefox){
$2.lzimg["MozUserSelect"]="none"
}else if($3.isSafari){
$2.lzimg["WebkitUserSelect"]="none"
}else{
$2.lzimg["UserSelect"]="none"
};LzSprite.prototype.br_to_newline_re=RegExp("<br/>","mg");if(lz.BrowserUtils.hasFeature("mouseevents","2.0")){
$0["has_dom2_mouseevents"]=true
};if($0["match_swf_letter_spacing"]){
$2.lzswftext.letterSpacing=$2.lzswfinputtext.letterSpacing=$2.lzswfinputtextmultiline.letterSpacing="0.025em"
}};LzSprite.prototype.quirks=$0
};LzSprite._getScrollbarWidth=function(){
var $0=document.createElement("div");$0.style.width="50px";$0.style.height="50px";$0.style.overflow="hidden";$0.style.position="absolute";$0.style.top="-200px";$0.style.left="-200px";var $1=document.createElement("div");$1.style.height="100px";$0.appendChild($1);var $2=document.body;$2.appendChild($0);var $3=$0.clientWidth;$0.style.overflowY="scroll";var $4=$0.clientWidth;LzSprite.prototype.__discardElement($0);return $3-$4
};LzSprite.__updateQuirks();LzSprite.setRootX=function($0){
var $1=LzSprite.__rootSpriteContainer;$1.style.position="absolute";$1.style.left=LzSprite.prototype.CSSDimension($0);LzScreenKernel.__resizeEvent()
};LzSprite.setRootWidth=function($0){
LzSprite.__rootSpriteContainer.style.width=LzSprite.prototype.CSSDimension($0);LzScreenKernel.__resizeEvent()
};LzSprite.setRootY=function($0){
var $1=LzSprite.__rootSpriteContainer;$1.style.position="absolute";$1.style.top=LzSprite.prototype.CSSDimension($0);LzScreenKernel.__resizeEvent()
};LzSprite.setRootHeight=function($0){
LzSprite.__rootSpriteContainer.style.height=LzSprite.prototype.CSSDimension($0);LzScreenKernel.__resizeEvent()
};LzSprite.prototype.__LZdiv=null;LzSprite.prototype.__LZimg=null;LzSprite.prototype.__LZclick=null;LzSprite.prototype.x=null;LzSprite.prototype.y=null;LzSprite.prototype.opacity=null;LzSprite.prototype.width=null;LzSprite.prototype.height=null;LzSprite.prototype.playing=false;LzSprite.prototype.clickable=false;LzSprite.prototype.frame=1;LzSprite.prototype.frames=null;LzSprite.blankimage="lps/includes/blank.gif";LzSprite.prototype.resource=null;LzSprite.prototype.source=null;LzSprite.prototype.visible=null;LzSprite.prototype.text=null;LzSprite.prototype.clip=null;LzSprite.prototype.stretches=null;LzSprite.prototype.resourceWidth=null;LzSprite.prototype.resourceHeight=null;LzSprite.prototype.cursor=null;LzSprite.prototype._w="0pt";LzSprite.prototype._h="0pt";LzSprite.prototype.__LZcontext=null;LzSprite.prototype.init=function($0){
this.setVisible($0);if(this.isroot){
if(this.quirks["css_hide_canvas_during_init"]){
var $1="display";if(this.quirks["safari_visibility_instead_of_display"]){
$1="visibility"
};this.__LZdiv.style[$1]="";if(this.quirks["fix_clickable"])this.__LZclickcontainerdiv.style[$1]="";if(this.quirks["fix_contextmenu"])this.__LZcontextcontainerdiv.style[$1]=""
};if(this._id){
lz.embed[this._id]._ready(this.owner)
};this.__initdone=true
}};LzSprite.prototype.__topZ=1;LzSprite.prototype.__parent=null;LzSprite.prototype.__children=null;LzSprite.prototype.addChildSprite=function($0){
if($0.__parent!=null)return;$0.__parent=this;if(this.__children){
this.__children.push($0)
}else{
this.__children=[$0]
};this.__LZdiv.appendChild($0.__LZdiv);if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.appendChild($0.__LZclickcontainerdiv)
};$0.__setZ(++this.__topZ)
};LzSprite.prototype.setResource=function($0){
if(this.resource==$0)return;this.resource=$0;if($0.indexOf("http:")==0||$0.indexOf("https:")==0){
this.skiponload=false;this.setSource($0);return
};var $1=LzResourceLibrary[$0];if($1){
this.resourceWidth=$1.width;this.resourceHeight=$1.height;if(this.quirks.use_css_sprites){
if(this.quirks.use_css_master_sprite&&$1.spriteoffset){
this.__csssprite=LzSprite.__masterspriteurl;this.__cssspriteoffset=$1.spriteoffset
}else if($1.sprite){
this.__csssprite=this.getBaseUrl($1)+$1.sprite;this.__cssspriteoffset=0
}}else{
this.__csssprite=null;if(this.__bgimage)this.__setBGImage(null)
}};var $2=this.getResourceUrls($0);this.owner.resourceevent("totalframes",$2.length);this.frames=$2;if(this.quirks.preload_images&&!(this.stretches==null&&this.__csssprite)){
this.__preloadFrames()
};this.skiponload=true;this.setSource($2[0],true)
};LzSprite.prototype.getResourceUrls=function($0){
var $1=[];var $2=LzResourceLibrary[$0];if(!$2){
return $1
};var $3=this.getBaseUrl($2);for(var $4=0;$4<$2.frames.length;$4++){
$1[$4]=$3+$2.frames[$4]
};return $1
};LzSprite.prototype.getBaseUrl=function($0){
return LzSprite.__rootSprite.options[$0.ptype=="sr"?"serverroot":"approot"]
};LzSprite.prototype.CSSDimension=LzKernelUtils.CSSDimension;LzSprite.prototype.loading=false;LzSprite.prototype.setSource=function($0,$1){
if($0==null||$0=="null"){
this.unload();return
};if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$0!=null){
this.__restoreSize()
}};if($1!=true){
this.skiponload=false;this.resource=$0;if(this.playing)this.stop();this.__updateLoadStatus(0);this.__csssprite=null;if(this.__bgimage)this.__setBGImage(null)
};if($1=="memorycache"){
$1=true
};if(this.loading){
if(this.__ImgPool&&this.source){
this.__ImgPool.flush(this.source)
};this.__destroyImage(null,this.__LZimg);this.__LZimg=null
};this.source=$0;if(this.stretches==null&&this.__csssprite){
if(!this.__LZimg){
var $2=document.createElement("img");$2.className="lzimg";$2.owner=this;$2.src=LzSprite.blankimage;this.__bindImage($2)
};this.__updateStretches();var $3=this.__csssprite?this.__csssprite:$0;this.__setBGImage($3);this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload});return
};if(!this.quirks.preload_images){
this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload})
};this.loading=true;if(!this.__ImgPool){
this.__ImgPool=new LzPool(LzSprite.prototype.__getImage,LzSprite.prototype.__gotImage,LzSprite.prototype.__destroyImage,this)
};var $2=this.__ImgPool.get($0,$1!=true);this.__bindImage($2);if(this.loading){
if(this.skiponload&&this.quirks.ie_alpha_image_loader)this.__updateIEAlpha($2)
}else{
if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha($2)
}else if(this.stretches){
this.__updateStretches()
}};if(this.clickable)this.setClickable(true)
};LzSprite.prototype.__bindImage=function($0){
if(this.__LZimg&&this.__LZimg.owner){
this.__LZdiv.replaceChild($0,this.__LZimg);this.__LZimg=$0
}else{
this.__LZimg=$0;this.__LZdiv.appendChild(this.__LZimg)
}};LzSprite.prototype.__setBGImage=function($0){
var $1=$0?"url('"+$0+"')":null;this.__bgimage=this.__LZimg.style.backgroundImage=$1;if($1!=null){
var $2=-this.__cssspriteoffset||0;this.__LZimg.style.backgroundPosition="0px "+$2+"px"
}};if(LzSprite.quirks.ie_alpha_image_loader){
LzSprite.prototype.__updateIEAlpha=function($0){
var $1=this.resourceWidth;var $2=this.resourceHeight;if(this.stretches=="both"){
$1="100%";$2="100%"
}else if(this.stretches=="width"){
$1="100%"
}else if(this.stretches=="height"){
$2="100%"
};if($1==null)$1=this.width==null?"100%":this.CSSDimension(this.width);if($2==null)$2=this.height==null?"100%":this.CSSDimension(this.height);$0.style.width=$1;$0.style.height=$2
}};LzSprite.prototype.setClickable=function($0){
$0=$0==true;if(this.clickable==$0)return;if(this.__LZimg!=null){
if(this.__LZdiv._clickable){
this.__setClickable(false,this.__LZdiv)
};if(!this.__LZclick){
if(this.quirks.fix_ie_clickable){
this.__LZclick=document.createElement("img");this.__LZclick.src=LzSprite.blankimage
}else{
this.__LZclick=document.createElement("div")
};this.__LZclick.owner=this;this.__LZclick.className="lzclickdiv";this.__LZclick.style.width=this._w;this.__LZclick.style.height=this._h;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.appendChild(this.__LZclick)
}else{
this.__LZdiv.appendChild(this.__LZclick)
}};this.__setClickable($0,this.__LZclick);if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable){
this.__LZclickcontainerdiv.style.display=$0&&this.visible?"":"none";this.__LZclick.style.display=$0&&this.visible?"":"none"
}else{
this.__LZclick.style.display=$0?"":"none"
}}}else{
if(this.quirks.fix_clickable){
if(!this.__LZclick){
if(this.quirks.fix_ie_clickable){
this.__LZclick=document.createElement("img");this.__LZclick.src=LzSprite.blankimage
}else{
this.__LZclick=document.createElement("div")
};this.__LZclick.owner=this;this.__LZclick.className="lzclickdiv";this.__LZclick.style.width=this._w;this.__LZclick.style.height=this._h;this.__LZclickcontainerdiv.appendChild(this.__LZclick)
};this.__setClickable($0,this.__LZclick);if(this.quirks.fix_ie_clickable){
this.__LZclick.style.display=$0&&this.visible?"":"none"
}else{
this.__LZclick.style.display=$0?"":"none"
}}else{
this.__setClickable($0,this.__LZdiv)
}};this.clickable=$0
};LzSprite.prototype.__setClickable=function($0,$1){
if($1._clickable==$0)return;$1._clickable=$0;var $2=$0?LzSprite.prototype.__clickDispatcher:null;$1.onclick=$2;$1.onmousedown=$2;$1.onmouseup=$2;$1.onmousemove=$2;if(this.quirks.ie_mouse_events){
$1.ondrag=$2;$1.ondblclick=$2;$1.onmouseover=$2;$1.onmouseout=$2
}else if(this.quirks.listen_for_mouseover_out){
$1.onmouseover=$2;$1.onmouseout=$2
}};LzSprite.prototype.__clickDispatcher=function($0){
if(!$0)$0=window.event;if($0.button==2)return false;this.owner.__mouseEvent($0);return false
};LzSprite.prototype.__mouseisdown=false;LzSprite.prototype.__mouseEvent=function($0,$1){
if($1){
var $2=$0;$0={}}else{
var $2="on"+$0.type;if(LzKeyboardKernel&&LzKeyboardKernel["__updateControlKeys"]){
LzKeyboardKernel.__updateControlKeys($0);if(LzKeyboardKernel.__cancelKeys){
$0.cancelBubble=true
}}};if(this.quirks.ie_mouse_events){
if($2=="onmouseenter"){
$2="onmouseover"
}else if($2=="onmouseleave"){
$2="onmouseout"
}else if($2=="ondblclick"){
this.__mouseEvent("onmousedown",true);this.__mouseEvent("onmouseup",true);this.__mouseEvent("onclick",true);return
}else if($2=="ondrag"){
return
}};LzMouseKernel.__sendMouseMove($0);if($2=="onmousemove"){
return
}else if($2=="onmousedown"){
this.__mouseisdown=true;LzMouseKernel.__lastMouseDown=this;var $3=LzInputTextSprite.prototype.__focusedSprite;if($3&&$3!=this){
$3.deselect()
}}else if($2=="onmouseup"){
$0.cancelBubble=false;if(LzMouseKernel.__lastMouseDown!==this){
return
}else{
if(this.quirks.ie_mouse_events){
if(this.__isMouseOver()){
this.__mouseisdown=false
}}else{
this.__mouseisdown=false
};if(this.__mouseisdown==false){
LzMouseKernel.__lastMouseDown=null
}}}else if($2=="onmouseupoutside"){
this.__mouseisdown=false
}else if($2=="onmouseover"){
LzMouseKernel.__lastMouseOver=this;if(this.quirks.activate_on_mouseover){
var $4=LzSprite.__mouseActivationDiv;if(!$4.mouseisover){
$4.onmouseover()
}}};if(this.owner.mouseevent){
if(LzMouseKernel.__lastMouseDown){
if($2=="onmouseover"||$2=="onmouseout"){
var $5=false;if(this.quirks.ie_mouse_events){
var $6=this.__isMouseOver();if($6&&$2=="onmouseover"||!$6&&$2=="onmouseout"){
$5=true
}}else{
if(LzMouseKernel.__lastMouseDown===this){
$5=true
}};if($2=="onmouseover"){
LzMouseKernel.__lastMouseOver=this
}else if($5&&LzMouseKernel.__lastMouseOver===this){
LzMouseKernel.__lastMouseOver=null
};if($5){
LzMouseKernel.__sendEvent($2,this.owner);var $7=$2=="onmouseover"?"onmousedragin":"onmousedragout";LzMouseKernel.__sendEvent($7,this.owner)
};return
}};if(this.quirks.fix_clickable&&!LzMouseKernel.__globalClickable){
if(lz["html"]&&this.owner&&(lz.html["$lzsc$isa"]?lz.html.$lzsc$isa(this.owner):this.owner instanceof lz.html)&&($2=="onmouseout"||$2=="onmouseover")){
return
}};LzMouseKernel.__sendEvent($2,this.owner)
}};LzSprite.prototype.__isMouseOver=function($0){
var $1=this.getMouse();var $2=this.__findParents("visible",false);if($2.length)return false;return $1.x>=0&&$1.y>=0&&$1.x<this.width&&$1.y<this.height
};LzSprite.prototype.__globalmouseup=function($0){
if(this.__mouseisdown){
if(!this.quirks.ie_mouse_events){
this.__mouseEvent($0)
};this.__mouseEvent("onmouseupoutside",true)
};LzMouseKernel.__lastMouseDown=null;if(LzMouseKernel.__lastMouseOver){
LzMouseKernel.__lastMouseOver.__mouseEvent("onmouseover",true);LzMouseKernel.__lastMouseOver=null
}};LzSprite.prototype.setX=function($0){
if($0==null||$0==this.x)return;this.__poscacheid=-1;this.x=$0;$0=this.CSSDimension($0);if(this._x!=$0){
this._x=$0;this.__LZdiv.style.left=$0;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.style.left=$0
};if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.left=$0
}}};LzSprite.prototype.setWidth=function($0){
if($0==null||$0<0||this.width==$0)return;this.width=$0;$0=this.CSSDimension($0);if(this._w!=$0){
this._w=$0;var $1=$0;var $2=this.quirks;if($2.size_blank_to_zero){
if(this.bgcolor==null&&this.source==null&&!this.clip&&!(this instanceof LzTextSprite)&&!this.shadow){
this.__sizedtozero=true;$1="0px"
}};this.__LZdiv.style.width=$1;if(this.clip)this.__updateClip();if(this.stretches)this.__updateStretches();if(this.__LZclick)this.__LZclick.style.width=$0;if(this.__LZcontext)this.__LZcontext.style.width=$0;if(this.__LZcanvas)this.__resizecanvas();if(this.isroot&&$2.container_divs_require_overflow){
LzSprite.__rootSpriteOverflowContainer.style.width=$0
};return $0
}};LzSprite.prototype.setY=function($0){
if($0==null||$0==this.y)return;this.__poscacheid=-1;this.y=$0;$0=this.CSSDimension($0);if(this._y!=$0){
this._y=$0;this.__LZdiv.style.top=$0;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.style.top=$0
};if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.top=$0
}}};LzSprite.prototype.setHeight=function($0){
if($0==null||$0<0||this.height==$0)return;this.height=$0;$0=this.CSSDimension($0);if(this._h!=$0){
this._h=$0;var $1=$0;var $2=this.quirks;if($2.size_blank_to_zero){
if(this.bgcolor==null&&this.source==null&&!this.clip&&!(this instanceof LzTextSprite)&&!this.shadow){
this.__sizedtozero=true;$1="0px"
}};this.__LZdiv.style.height=$1;if(this.clip)this.__updateClip();if(this.stretches)this.__updateStretches();if(this.__LZclick)this.__LZclick.style.height=$0;if(this.__LZcontext)this.__LZcontext.style.height=$0;if(this.__LZcanvas)this.__resizecanvas();if(this.isroot&&$2.container_divs_require_overflow){
LzSprite.__rootSpriteOverflowContainer.style.height=$0
};return $0
}};LzSprite.prototype.setMaxLength=function($0){};LzSprite.prototype.setPattern=function($0){};LzSprite.prototype.setVisible=function($0){
if(this.visible==$0)return;this.visible=$0;this.__LZdiv.style.display=$0&&this.opacity!=0?"":"none";if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable&&this.__LZclick){
this.__LZclick.style.display=$0&&this.clickable?"":"none"
};var $1=$0?"":"none";this.__LZclickcontainerdiv.style.display=$1;if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.display=$1
}}};LzSprite.prototype.setColor=function($0){
if(this.color==$0)return;this.color=$0;this.__LZdiv.style.color=LzColorUtils.inttohex($0)
};LzSprite.prototype.setBGColor=function($0){
if($0!=null&&!this.capabilities.rgba){
$0=Math.floor($0)
};if(this.bgcolor==$0)return;this.bgcolor=$0;if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$0!=null){
this.__restoreSize()
}};this.__LZdiv.style.backgroundColor=$0==null?"transparent":LzColorUtils.torgb($0);if(this.quirks.fix_ie_background_height){
if(this.height!=null&&this.height<2){
this.setSource(LzSprite.blankimage,true)
}else if(!this._fontSize){
this.__LZdiv.style.fontSize="0px"
}}};LzSprite.prototype.__restoreSize=function(){
if(this.__sizedtozero){
this.__sizedtozero=false;this.__LZdiv.style.width=this._w;this.__LZdiv.style.height=this._h
}};LzSprite.prototype.setOpacity=function($0){
if(this.opacity==$0||$0<0)return;this.opacity=$0;var $1=100;if(this.capabilities.minimize_opacity_changes){
$1=10
};$0=parseInt($0*$1)/$1;if($0!=this._opacity){
this._opacity=$0;this.__LZdiv.style.display=this.visible&&$0!=0?"":"none";if(this.quirks.ie_opacity){
if($0==1){
this.__LZdiv.style.filter=""
}else{
this.__LZdiv.style.filter="alpha(opacity="+parseInt($0*100)+")"
}}else{
if($0==1){
this.__LZdiv.style.opacity=""
}else{
this.__LZdiv.style.opacity=$0
}}}};LzSprite.prototype.play=function($0){
if(!this.frames||this.frames.length<2)return;$0=parseInt($0);if(!isNaN($0)){
this.__setFrame($0)
};if(this.playing==true)return;this.playing=true;this.owner.resourceevent("play",null,true);LzIdleKernel.addCallback(this,"__incrementFrame")
};LzSprite.prototype.stop=function($0){
if(!this.frames||this.frames.length<2)return;if(this.playing==true){
this.playing=false;this.owner.resourceevent("stop",null,true);LzIdleKernel.removeCallback(this,"__incrementFrame")
};$0=parseInt($0);if(!isNaN($0)){
this.__setFrame($0)
}};LzSprite.prototype.__incrementFrame=function(){
var $0=this.frame+1>this.frames.length?1:this.frame+1;this.__setFrame($0)
};if(LzSprite.quirks.preload_images_only_once){
LzSprite.prototype.__preloadurls={}};LzSprite.prototype.__preloadFrames=function(){
if(!this.__ImgPool){
this.__ImgPool=new LzPool(LzSprite.prototype.__getImage,LzSprite.prototype.__gotImage,LzSprite.prototype.__destroyImage,this)
};var $0=this.frames.length;for(var $1=0;$1<$0;$1++){
var $2=this.frames[$1];if(this.quirks.preload_images_only_once){
if($1>0&&LzSprite.prototype.__preloadurls[$2]){
continue
};LzSprite.prototype.__preloadurls[$2]=true
};var $3=this.__ImgPool.get($2,false,true);if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha($3)
}}};LzSprite.prototype.__findParents=function($0,$1){
var $2=[];var $3=LzSprite.__rootSprite;var $4=this;while($4&&$4!==$3){
if($4[$0]==$1)$2.push($4);$4=$4.__parent
};return $2
};LzSprite.prototype.__imgonload=function($0,$1){
if(this.loading!=true)return;if(this.__imgtimoutid!=null){
clearTimeout(this.__imgtimoutid);this.__imgtimoutid=null
};this.loading=false;if(!$1){
if(this.quirks.ie_alpha_image_loader){
$0._parent.style.display=""
}else{
$0.style.display=""
}};this.resourceWidth=$1&&$0["__LZreswidth"]?$0.__LZreswidth:$0.width;this.resourceHeight=$1&&$0["__LZresheight"]?$0.__LZresheight:$0.height;if(!$1){
if(this.quirks.invisible_parent_image_sizing_fix&&this.resourceWidth==0){
var $2=function($0){
this.resourceWidth=$0.width;this.resourceHeight=$0.height
};this.__processHiddenParents($2,$0)
};if(this.quirks.ie_alpha_image_loader){
$0._parent.__lastcondition="__imgonload"
}else{
$0.__lastcondition="__imgonload";$0.__LZreswidth=this.resourceWidth;$0.__LZresheight=this.resourceHeight
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else if(this.stretches){
this.__updateStretches()
}};this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:this.skiponload});if(this.skiponload!=true){
this.__updateLoadStatus(1)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($0)
}};LzSprite.prototype.__processHiddenParents=function($0){
var $1=this.__findParents("visible",false);var $2=[];var $3=$1.length;for(var $4=0;$4<$3;$4++){
var $5=$1[$4];$2[$4]=$5.__LZdiv.style.display;$5.__LZdiv.style.display=""
};var $6=Array.prototype.slice.call(arguments,1);var $7=$0.apply(this,$6);for(var $4=0;$4<$3;$4++){
var $5=$1[$4];$5.__LZdiv.style.display=$2[$4]
};return $7
};LzSprite.prototype.__imgonerror=function($0,$1){
if(this.loading!=true)return;if(this.__imgtimoutid!=null){
clearTimeout(this.__imgtimoutid);this.__imgtimoutid=null
};this.loading=false;this.resourceWidth=1;this.resourceHeight=1;if(!$1){
if(this.quirks.ie_alpha_image_loader){
$0._parent.__lastcondition="__imgonerror"
}else{
$0.__lastcondition="__imgonerror"
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else if(this.stretches){
this.__updateStretches()
}};this.owner.resourceloaderror();if(this.skiponload!=true){
this.__updateLoadStatus(0)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($0)
}};LzSprite.prototype.__imgontimeout=function($0,$1){
if(this.loading!=true)return;this.__imgtimoutid=null;this.loading=false;this.resourceWidth=1;this.resourceHeight=1;if(!$1){
if(this.quirks.ie_alpha_image_loader){
$0._parent.__lastcondition="__imgontimeout"
}else{
$0.__lastcondition="__imgontimeout"
};if(this.quirks.ie_alpha_image_loader){
this.__updateIEAlpha(this.__LZimg)
}else if(this.stretches){
this.__updateStretches()
}};this.owner.resourceloadtimeout();if(this.skiponload!=true){
this.__updateLoadStatus(0)
};if(this.quirks.ie_alpha_image_loader){
this.__clearImageEvents(this.__LZimg)
}else{
this.__clearImageEvents($0)
}};LzSprite.prototype.__updateLoadStatus=function($0){
this.owner.resourceevent("loadratio",$0);this.owner.resourceevent("framesloadratio",$0)
};LzSprite.prototype.__destroyImage=function($0,$1){
if($1){
if($1.owner){
var $2=$1.owner;if($2.__imgtimoutid!=null){
clearTimeout($2.__imgtimoutid);$2.__imgtimoutid=null
};lz.BrowserUtils.removecallback($2)
};LzSprite.prototype.__clearImageEvents($1);LzSprite.prototype.__discardElement($1)
};if(LzSprite.quirks.preload_images_only_once){
LzSprite.prototype.__preloadurls[$0]=null
}};LzSprite.prototype.__clearImageEvents=function($0){
if(!$0||$0.__cleared)return;if(LzSprite.quirks.ie_alpha_image_loader){
var $1=$0.sizer;if($1){
if($1.tId)clearTimeout($1.tId);$1.onerror=null;$1.onload=null;$1.onloadforeal=null;$1._parent=null;var $2={width:$1.width,height:$1.height,src:$1.src};LzSprite.prototype.__discardElement($1);$0.sizer=$2
}}else{
$0.onerror=null;$0.onload=null
};$0.__cleared=true
};LzSprite.prototype.__gotImage=function($0,$1,$2){
if(this.owner.skiponload||$2==true){
this.owner[$1.__lastcondition]({width:this.owner.resourceWidth,height:this.owner.resourceHeight},true)
}else{
if(LzSprite.quirks.ie_alpha_image_loader){
this.owner[$1.__lastcondition]($1.sizer,true)
}else{
this.owner[$1.__lastcondition]($1,true)
}}};LzSprite.prototype.__getImage=function($0,$1){
if(LzSprite.quirks.ie_alpha_image_loader){
var im=document.createElement("div");im.style.overflow="hidden";if(this.owner&&$1!=true){
im.owner=this.owner;if(!im.sizer){
im.sizer=document.createElement("img");im.sizer._parent=im
};im.sizer.onload=function(){
im.sizer.tId=setTimeout(this.onloadforeal,1)
};im.sizer.onloadforeal=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonload",[im.sizer]);im.sizer.onerror=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonerror",[im.sizer]);var $2=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgontimeout",[im.sizer]);this.owner.__imgtimoutid=setTimeout($2,canvas.medialoadtimeout);im.sizer.src=$0
};if(!$1)im.style.display="none";if(this.owner.stretches){
im.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+$0+"',sizingMethod='scale')"
}else{
im.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+$0+"')"
}}else{
var im=document.createElement("img");im.className="lzimg";if(!$1)im.style.display="none";if(this.owner&&$1!=true){
im.owner=this.owner;im.onload=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonload",[im]);im.onerror=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgonerror",[im]);var $2=lz.BrowserUtils.getcallbackfunc(this.owner,"__imgontimeout",[im]);this.owner.__imgtimoutid=setTimeout($2,canvas.medialoadtimeout)
};im.src=$0
};if(im)im.__lastcondition="__imgonload";return im
};LzSprite.prototype.setClip=function($0){
if(this.clip==$0)return;this.clip=$0;if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero&&$0){
this.__restoreSize()
}};this.__updateClip()
};LzSprite.prototype.__updateClip=function(){
var $0=this.quirks;if(this.isroot&&this.quirks.canvas_div_cannot_be_clipped)return;if(this.clip&&this.width!=null&&this.width>=0&&this.height!=null&&this.height>=0){
var $1="rect(0px "+this._w+" "+this._h+" 0px)";this.__LZdiv.style.clip=$1
}else if(this.__LZdiv.style.clip){
var $1=$0.fix_ie_css_syntax?"rect(auto auto auto auto)":"";this.__LZdiv.style.clip=$1
}else{
return
};if($0.fix_clickable){
this.__LZclickcontainerdiv.style.clip=$1
};if($0.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.clip=$1
}};LzSprite.prototype.stretchResource=function($0){
$0=$0!="none"?$0:null;if(this.stretches==$0)return;this.stretches=$0;if(!($0==null&&this.__csssprite)&&this.__bgimage){
if(this.quirks.preload_images)this.__preloadFrames();this.__setBGImage(null);this.__setFrame(this.frame,true)
};this.__updateStretches()
};LzSprite.prototype.__updateStretches=function(){
if(this.loading)return;if(this.quirks.ie_alpha_image_loader)return;if(this.__LZimg){
var $0=this.__LZimg.style.display;this.__LZimg.style.display="none";if(this.stretches=="both"){
this.__LZimg.width=this.width;this.__LZimg.height=this.height
}else if(this.stretches=="height"){
this.__LZimg.width=this.resourceWidth;this.__LZimg.height=this.height
}else if(this.stretches=="width"){
this.__LZimg.width=this.width;this.__LZimg.height=this.resourceHeight
}else{
this.__LZimg.width=this.resourceWidth;this.__LZimg.height=this.resourceHeight
};this.__LZimg.style.display=$0
}};LzSprite.prototype.predestroy=function(){};LzSprite.prototype.destroy=function($0){
switch(arguments.length){
case 0:
$0=true;

};if(this.__LZdeleted==true)return;this.__LZdeleted=true;if($0){
if(this.__parent){
var $1=this.__parent.__children;for(var $2=$1.length-1;$2>=0;$2--){
if($1[$2]===this){
$1.splice($2,1);break
}}}};if(this.__ImgPool)this.__ImgPool.destroy();if(this.__LZimg)this.__discardElement(this.__LZimg);this.__skipdiscards=$0!=true;if(this.__LZclick){
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
if(this.quirks.ie_leak_prevention){
this.__LZcanvas.owner=null;this.__LZcanvas.getContext=null
};this.__discardElement(this.__LZcanvas)
};this.__ImgPool=null;if(this.quirks.ie_leak_prevention){
delete this.__sprites[this.uid]
};if(this.isroot){
lz.BrowserUtils.scopes=null
}};LzSprite.prototype.getMouse=function(){
var $0=this.__getPos();return {x:LzMouseKernel.__x-$0.x,y:LzMouseKernel.__y-$0.y}};LzSprite.prototype.__poscache=null;LzSprite.prototype.__poscacheid=0;LzSprite.__poscachecnt=0;LzSprite.prototype.__getPos=function(){
if(!LzSprite.__rootSprite.__initdone){
return lz.embed.getAbsolutePosition(this.__LZdiv)
};var $0=false;var $1=true;var $2=LzSprite.__rootSprite;var $3,$4;for(var $5=this;$5!==$2;$5=$3){
$3=$5.__parent;if($3){
if($5.__poscacheid<$3.__poscacheid){
$0=true;$4=$3
}}else{
$1=false;break
}};if($0&&$1){
var $6=++LzSprite.__poscachecnt;for(var $5=this;$5!==$4;$5=$5.__parent){
$5.__poscache=null;$5.__poscacheid=$6
}};var $7=this.__poscache;if(!$7){
$7=this.__processHiddenParents(lz.embed.getAbsolutePosition,this.__LZdiv);if($1){
this.__poscache=$7
}};return $7
};LzSprite.prototype.getWidth=function(){
var $0=this.__LZdiv.clientWidth;return $0==0?this.width:$0
};LzSprite.prototype.getHeight=function(){
var $0=this.__LZdiv.clientHeight;return $0==0?this.height:$0
};LzSprite.prototype.setCursor=function($0){
if(this.quirks.no_cursor_colresize){
return
};if($0==this.cursor)return;if(this.clickable!=true)this.setClickable(true);this.cursor=$0;var $0=LzSprite.__defaultStyles.hyphenate($0);this.__LZclick.style.cursor=$0
};LzSprite.prototype.setShowHandCursor=function($0){
if($0==true){
this.setCursor("pointer")
}else{
this.setCursor("default")
}};LzSprite.prototype.getDisplayObject=function(){
return this.__LZdiv
};LzSprite.prototype.__LZcanvas=null;LzSprite.prototype.getContext=function(){
if(this.__LZcanvas&&this.__LZcanvas.getContext){
return this.__LZcanvas.getContext("2d")
};var $0=document.createElement("canvas");$0.owner=this;this.__LZcanvas=$0;$0.className="lzgraphicscanvas";this.__LZdiv.appendChild($0);lz.embed.__setAttr(this.__LZcanvas,"width",this.width);lz.embed.__setAttr(this.__LZcanvas,"height",this.height);if(lz.embed.browser.isIE){
this.__maxTries=10;this.__initcanvasie()
}else{
return this.__LZcanvas.getContext("2d")
}};LzSprite.prototype.setContextCallback=function($0,$1){
this.__canvascallbackscope=$0;this.__canvascallbackname=$1
};LzSprite.prototype.bringToFront=function(){
if(!this.__parent){
return
};var $0=this.__parent.__children;if($0.length<2)return;$0.sort(LzSprite.prototype.__zCompare);this.sendInFrontOf($0[$0.length-1])
};LzSprite.prototype.__setZ=function($0){
this.__LZdiv.style.zIndex=$0;var $1=this.quirks;if($1.fix_clickable){
this.__LZclickcontainerdiv.style.zIndex=$0
};if($1.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style.zIndex=$0
};this.__z=$0
};LzSprite.prototype.__zCompare=function($0,$1){
if($0.__z<$1.__z)return -1;if($0.__z>$1.__z)return 1;return 0
};LzSprite.prototype.sendToBack=function(){
if(!this.__parent){
return
};var $0=this.__parent.__children;if($0.length<2)return;$0.sort(LzSprite.prototype.__zCompare);this.sendBehind($0[0])
};LzSprite.prototype.sendBehind=function($0){
if(!$0||$0===this)return;if(!this.__parent){
return
};var $1=this.__parent.__children;if($1.length<2)return;$1.sort(LzSprite.prototype.__zCompare);var $2=false;for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3];if($4==$0)$2=$0.__z;if($2!=false){
$4.__setZ(++$4.__z)
}};this.__setZ($2)
};LzSprite.prototype.sendInFrontOf=function($0){
if(!$0||$0===this)return;if(!this.__parent){
return
};var $1=this.__parent.__children;if($1.length<2)return;$1.sort(LzSprite.prototype.__zCompare);var $2=false;for(var $3=0;$3<$1.length;$3++){
var $4=$1[$3];if($2!=false){
$4.__setZ(++$4.__z)
};if($4==$0)$2=$0.__z+1
};this.__setZ($2)
};LzSprite.prototype.__setFrame=function($0,$1){
if($0<1){
$0=1
}else if($0>this.frames.length){
$0=this.frames.length
};var $2=false;if($1){
$2=$0==this.frame
}else if($0==this.frame){
return
};this.frame=$0;if(this.stretches==null&&this.__csssprite){
if(!this.__bgimage){
this.__LZimg.src=LzSprite.blankimage;this.__setBGImage(this.__csssprite)
};var $3=(this.frame-1)*-this.resourceWidth;var $4=-this.__cssspriteoffset||0;this.__LZimg.style.backgroundPosition=$3+"px "+$4+"px"
}else{
var $5=this.frames[this.frame-1];this.setSource($5,true)
};if($2)return;this.owner.resourceevent("frame",this.frame);if(this.frames.length==this.frame)this.owner.resourceevent("lastframe",null,true)
};LzSprite.prototype.__discardElement=function($0){
if(this.__skipdiscards)return;if($0.parentNode)$0.parentNode.removeChild($0)
};LzSprite.prototype.getZ=function(){
return this.__z
};LzSprite.prototype.updateResourceSize=function(){
this.owner.resourceload({width:this.resourceWidth,height:this.resourceHeight,resource:this.resource,skiponload:true})
};LzSprite.prototype.unload=function(){
this.resource=null;this.source=null;this.resourceWidth=null;this.resourceHeight=null;if(this.__ImgPool){
this.__ImgPool.destroy();this.__ImgPool=null
};if(this.__LZimg){
this.__destroyImage(null,this.__LZimg);this.__LZimg=null
};this.__updateLoadStatus(0)
};LzSprite.prototype.__setCSSClassProperty=function($0,$1,$2){
var $3=document.all?"rules":"cssRules";var $4=document.styleSheets;var $5=$4.length-1;for(var $6=$5;$6>=0;$6--){
var $7=$4[$6][$3];var $8=$7.length-1;for(var $9=$8;$9>=0;$9--){
if($7[$9].selectorText==$0){
$7[$9].style[$1]=$2
}}}};LzSprite.prototype.setDefaultContextMenu=function($0){
LzSprite.__rootSprite.__contextmenu=$0
};LzSprite.prototype.setContextMenu=function($0){
this.__contextmenu=$0;if(!this.quirks.fix_contextmenu||this.__LZcontext)return;var $1=this.__findParents("__LZcontextcontainerdiv",null);for(var $2=$1.length-1;$2>=0;$2--){
var $3=$1[$2];var $4=$3.__parent.__LZcontextcontainerdiv;var $5=document.createElement("div");$5.className="lzdiv";$4.appendChild($5);this.__copystyles($3.__LZdiv,$5);if($3._id&&!$5.id){
$5.id="context"+$3._id
};$5.owner=$3;$3.__LZcontextcontainerdiv=$5
};var $5=document.createElement("div");$5.className="lzcontext";this.__LZcontextcontainerdiv.appendChild($5);this.__LZcontext=$5;$5.style.width=this._w;$5.style.height=this._h;$5.owner=this
};LzSprite.prototype.__copystyles=function($0,$1){
$1.style.left=$0.style.left;$1.style.top=$0.style.top;$1.style.display=$0.style.display;$1.style.clip=$0.style.clip;$1.style.zIndex=$0.style.zIndex
};LzSprite.prototype.getContextMenu=function(){
return this.__contextmenu
};LzSprite.prototype.setRotation=function($0){
var $1=lz.embed.browser;if($1.isSafari){
this.__LZdiv.style["WebkitTransform"]="rotate("+$0+"deg)"
}else if($1.isFirefox){
this.__LZdiv.style["MozTransform"]="rotate("+$0+"deg)"
}};if(LzSprite.quirks.ie_leak_prevention){
LzSprite.prototype.__sprites={};function __cleanUpForIE(){
LzTextSprite.prototype.__cleanupdivs();LzTextSprite.prototype._sizecache={};var $0=LzSprite.prototype.__sprites;for(var $1 in $0){
$0[$1].destroy();$0[$1]=null
};LzSprite.prototype.__sprites={}};lz.embed.attachEventHandler(window,"beforeunload",window,"__cleanUpForIE");LzSprite.prototype.__discardElement=function($0){
if(!$0||!$0.nodeType)return;if($0.nodeType>=1&&$0.nodeType<13){
if($0.owner)$0.owner=null;var $1=document.getElementById("__LZIELeakGarbageBin");if(!$1){
$1=document.createElement("DIV");$1.id="__LZIELeakGarbageBin";$1.style.display="none";document.body.appendChild($1)
};$1.appendChild($0);$1.innerHTML=""
}}};LzSprite.prototype.getSelectedText=function(){
if(window.getSelection){
return window.getSelection().toString()
}else if(document.selection){
return document.selection.createRange().text.toString()
}else if(document.getSelection){
return document.getSelection()
}};LzSprite.prototype.setAADescription=function($0){
var $1=this.aadescriptionDiv;if($1==null){
this.aadescriptionDiv=$1=document.createElement("LABEL");$1.className="lzaccessibilitydiv";if(!this.__LZdiv.id)this.__LZdiv.id="sprite_"+this.uid;lz.embed.__setAttr($1,"for",this.__LZdiv.id);this.__LZdiv.appendChild($1)
};$1.innerHTML=$0
};LzSprite.prototype.setAccessible=function($0){
LzSprite.__rootSprite.accessible=$0
};LzSprite.prototype._accProps=null;LzSprite.prototype.setAAActive=function($0){
this.__LzAccessibilityActive=$0
};LzSprite.prototype.setAASilent=function($0){};LzSprite.prototype.setAAName=function($0){};LzSprite.prototype.aafocus=function(){
try{
if(this.__LZdiv!=null){
this.__LZdiv.blur();this.__LZdiv.focus()
}}
catch($0){}};LzSprite.prototype.setAATabIndex=function($0){};LzSprite.prototype.sendAAEvent=function($0,$1,$2){
try{
if(this.__LZdiv!=null){
this.__LZdiv.focus()
}}
catch($3){}};LzSprite.prototype.setID=function($0){
if(!this._id)this._id=$0;if(!this.__LZdiv.id)this.__LZdiv.id=this._dbg_typename+$0;if(!this.__LZclickcontainerdiv.id)this.__LZclickcontainerdiv.id="click"+$0;if(this.__LZcontextcontainerdiv&&!this.__LZcontextcontainerdiv.id)this.__LZcontextcontainerdiv.id=this.__LZcontextcontainerdiv.id="context"+$0
};LzSprite.prototype.__resizecanvas=function(){
if(this.width>0&&this.height>0){
if(this.__LZcanvas){
lz.embed.__setAttr(this.__LZcanvas,"width",this.width);lz.embed.__setAttr(this.__LZcanvas,"height",this.height);this.__docanvascallback()
};if(this.__LZcanvas&&this["_canvashidden"]){
this._canvashidden=false;this.__LZcanvas.style.display=""
}}else if(this.__LZcanvas&&this["_canvashidden"]!=true){
this._canvashidden=true;this.__LZcanvas.style.display="none"
}};LzSprite.prototype.__docanvascallback=function(){
var $0=this.__canvascallbackscope[this.__canvascallbackname];if($0){
$0.call(this.__canvascallbackscope,this.__LZcanvas.getContext("2d"))
}};LzSprite.prototype.__initcanvasie=function(){
if(this.__canvasTId)clearTimeout(this.__canvasTId);try{
if(this.__LZcanvas&&this.__LZcanvas.parentNode!=null){
this.__LZcanvas=G_vmlCanvasManager.initElement(this.__LZcanvas);this.__docanvascallback();return
}}
catch($0){};if(--this.__maxTries>0){
var $1=lz.BrowserUtils.getcallbackstr(this,"__initcanvasie");this.__canvasTId=setTimeout($1,50)
}};LzSprite.prototype.__getShadowCSS=function($0,$1,$2,$3){
if($0==null){
return null
};var $4=$2*Math.PI/180;var $5=this.CSSDimension(Math.cos($4)*$1);var $6=this.CSSDimension(Math.sin($4)*$1);var $7=LzColorUtils.torgb($0);return $7+" "+$5+" "+$6+" "+this.CSSDimension($3)
};LzSprite.prototype.updateShadow=function($0,$1,$2,$3){
this.shadow=this.__getShadowCSS($0,$1,$2,$3);var $4=this.__LZdiv;$4.style.webkitBoxShadow=$4.style.MozBoxShadow=$4.style.boxShadow=this.shadow;if(this.quirks.size_blank_to_zero){
if(this.__sizedtozero){
this.__restoreSize()
}}};LzSprite.prototype.setCornerRadius=function($0){
this.__LZdiv.style.MozBorderRadius=this.__LZdiv.style.webkitBorderRadius=this.__LZdiv.style.borderRadius=this.CSSDimension($0)
};LzSprite.prototype.__csscache;LzSprite.prototype.setCSS=function($0,$1,$2){
if(!this.__csscache){
this.__csscache={}}else if(this.__csscache[$0]===$1){
return
};this.__csscache[$0]=$1;if($2)$1=this.CSSDimension($1);var $3=this["set_"+$0];if($3){
$3.call(this,$1)
}else{
this.__LZdiv.style[$0]=$1;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.style[$0]=$1
};if(this.quirks.fix_contextmenu&&this.__LZcontextcontainerdiv){
this.__LZcontextcontainerdiv.style[$0]=$1
}}};LzSprite.prototype.set_borderWidth=function($0){
this.__LZdiv.style.borderWidth=$0;this.__LZdiv.style.borderStyle="solid";if(this.__LZclick){
this.__LZclick.style.borderWidth=$0;this.__LZclick.style.borderStyle="solid";this.__LZclick.style.borderColor=this.__csscache.borderColor
};if(this.__LZcontext){
this.__LZcontext.style.borderWidth=$0;this.__LZcontext.style.borderStyle="solid";this.__LZcontext.style.borderColor=this.__csscache.borderColor
}};Class.make("LzLibrary",LzNode,["loaded",false,"loading",false,"sprite",null,"href",void 0,"stage","late","onload",LzDeclaredEvent,"construct",function($0,$1){
this.stage=$1.stage;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.sprite=new LzSprite(this,false,$1);LzLibrary.libraries[$1.name]=this
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
this.loading=false;if(this.onload.ready)this.onload.sendEvent(true)
}],["tagname","import","attributes",new LzInheritedHash(LzNode.attributes),"libraries",{},"findLibrary",function($0){
return LzLibrary.libraries[$0]
},"stripQueryString",function($0){
if($0.indexOf("?")>0){
$0=$0.substring(0,$0.indexOf("?"))
};return $0
},"__LZsnippetLoaded",function($0){
$0=LzLibrary.stripQueryString($0);var $1=null;var $2=LzLibrary.libraries;for(var $3 in $2){
var $4=LzLibrary.stripQueryString($2[$3].href);if($4==$0){
$1=$2[$3];break
}};if($1==null){

}else{
$1.loaded=true;canvas.initiatorAddNode({attrs:{libname:$1.name},"class":LzLibraryCleanup},1);canvas.initDone()
}}]);lz[LzLibrary.tagname]=LzLibrary;var LzTextSprite=function($0){
if($0==null)return;this.constructor=arguments.callee;this.owner=$0;this.uid=LzSprite.prototype.uid++;this.__LZdiv=document.createElement("div");this.__LZdiv.className="lztextcontainer";this.scrolldiv=this.__LZtextdiv=document.createElement("div");this.scrolldiv.owner=this;this.scrolldiv.className="lztext";this.__LZdiv.appendChild(this.scrolldiv);if(this.quirks.emulate_flash_font_metrics){
this.scrolldiv.className="lzswftext"
};this.__LZdiv.owner=this;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lztextcontainer";this.__LZclickcontainerdiv.owner=this
};if(this.quirks.ie_leak_prevention){
this.__sprites[this.uid]=this
}};LzTextSprite.prototype=new LzSprite(null);LzTextSprite.prototype.__initTextProperties=function($0){
this.setFontName($0.font);this.setFontStyle($0.fontstyle);this.setFontSize($0.fontsize);this.initted=true
};LzTextSprite.prototype._fontStyle="normal";LzTextSprite.prototype._fontWeight="normal";LzTextSprite.prototype._fontSize="11px";LzTextSprite.prototype._fontFamily="Verdana,Vera,sans-serif";LzTextSprite.prototype._whiteSpace="normal";LzTextSprite.prototype._textAlign="left";LzTextSprite.prototype._textIndent="0px";LzTextSprite.prototype.__LZtextIndent=0;LzTextSprite.prototype._letterSpacing="0px";LzTextSprite.prototype._textDecoration="none";LzTextSprite.prototype.__wpadding=4;LzTextSprite.prototype.__hpadding=4;LzTextSprite.prototype.__sizecacheupperbound=1000;LzTextSprite.prototype.selectable=true;LzTextSprite.prototype.text="";LzTextSprite.prototype.resize=true;LzTextSprite.prototype.restrict=null;LzTextSprite.prototype.setFontSize=function($0){
if($0==null||$0<0)return;var $1=this.CSSDimension($0);if(this._fontSize!=$1){
this._fontSize=$1;this.scrolldiv.style.fontSize=$1;if(this.quirks["emulate_flash_font_metrics"]){
var $2=Math.round($0*1.2);this.scrolldiv.style.lineHeight=this.CSSDimension($2);this._lineHeight=$2
};this.__updatefieldsize()
}};LzTextSprite.prototype.setFontStyle=function($0){
var $1;if($0=="plain"){
$1="normal";$0="normal"
}else if($0=="bold"){
$1="bold";$0="normal"
}else if($0=="italic"){
$1="normal";$0="italic"
}else if($0=="bold italic"||$0=="bolditalic"){
$1="bold";$0="italic"
};if($1!=this._fontWeight){
this._fontWeight=$1;this.scrolldiv.style.fontWeight=$1;this.__updatefieldsize()
};if($0!=this._fontStyle){
this._fontStyle=$0;this.scrolldiv.style.fontStyle=$0;this.__updatefieldsize()
}};LzTextSprite.prototype.setFontName=function($0){
if($0!=this._fontFamily){
this._fontFamily=$0;this.scrolldiv.style.fontFamily=$0;this.__updatefieldsize()
}};LzTextSprite.prototype.setTextColor=LzSprite.prototype.setColor;LzTextSprite.prototype.lineHeight=null;LzTextSprite.prototype.scrollTop=null;LzTextSprite.prototype.scrollHeight=null;LzTextSprite.prototype.scrollLeft=null;LzTextSprite.prototype.scrollWidth=null;LzTextSprite.prototype.scrolling=false;LzTextSprite.prototype.setScrolling=function($0){
var $1=this.scrolldiv;var $2=$1.className;if($2=="lzswftext"||$2=="lzswfinputtextmultiline"){
var $3=this.height;var $4=this.width;var $5=this.CSSDimension;if($0||$2=="lzswfinputtextmultiline"){
this.scrolling=$0;$1.style.overflow="scroll";$3+=this.quirks.scrollbar_width;$4+=this.quirks.scrollbar_width
}else{
this.scrolling=false;$1.style.overflow=""
};var $6=$5($3);var $7=$5($4);if($0){
$1.style.clip="rect(0 "+$7+" "+$6+" 0)"
}else if($1.style.clip){
$1.style.clip=this.quirks["fix_ie_css_syntax"]?"rect(auto auto auto auto)":""
};$1.style.height=$6;$1.style.width=$7
};return $0&&this.scrolling
};LzTextSprite.prototype.scrollevents=false;LzTextSprite.prototype.setScrollEvents=function($0){
this.scrollevents=this.setScrolling($0);this.__updatefieldsize()
};LzTextSprite.prototype.initted=false;LzTextSprite.prototype.__loadedfonts={counter:0};LzTextSprite.prototype.__loadedfontscallback={};LzTextSprite.prototype.__isExternalFontLoaded=function($0){
var $1=LzFontManager.getFont(this._fontFamily,this._fontStyle,this._fontWeight);if(!$1||!this.initted)return true;var $0=LzFontManager.getURL($1);var $2=this.__loadedfonts[$0];if($2==2){
return true
}else if($2==1){
var $3=this.__loadedfontscallback;$3[this.uid]=this;return false
};this.__loadedfonts[$0]=1;this.__loadedfonts.counter++;var $4="font-family:"+this._fontFamily+";font-style:"+this._fontStyle+";font-weight:"+this._fontWeight+";width:auto;height:auto;";var $5=this.__createMeasureDiv("lzswftext",$4,'Yq_gy"9;ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789-=abcdefghijklmnopqrstuvwxyz');$5.style.display="inline";var $6=$5.clientWidth;var $7=$5.clientHeight;$5.style.display="none";this.__measurefontdiv($5,$6,$7,$0)
};LzTextSprite.prototype.__measurefontdiv=function($0,$1,$2,$3){
$0.style.display="inline";var $4=$0.clientWidth;var $5=$0.clientHeight;$0.style.display="none";if($4==$1&&$5==$2){
var $6=lz.BrowserUtils.getcallbackfunc(this,"__measurefontdiv",[$0,$1,$2,$3]);setTimeout($6,0)
}else{
this.__loadedfonts.counter--;this.__loadedfonts[$3]=2;if(this.__loadedfonts.counter==0){
var $7=this.__loadedfontscallback;this.__clearMeasureCache();for(var $8 in $7){
var $9=$7[$8];$9._cachevalue=$9._cacheStyleKey=$9._cacheTextKey=null;$9.setWidth($9.getTextWidth());$9.setHeight($9.getTextHeight());$9.__updatefieldsize()
};delete $7[$8]
}}};LzTextSprite.prototype.__updatefieldsize=function(){
if(!this.__isExternalFontLoaded())return;var $0=lz.BrowserUtils.getcallbackfunc(this,"__updatefieldsizeCallback",[]);setTimeout($0,0)
};LzTextSprite.prototype.__updatefieldsizeCallback=function(){
var $0=this.getLineHeight();if(this.lineHeight!==$0){
this.lineHeight=$0;this.owner.scrollevent("lineHeight",$0)
};if(!this.scrollevents)return;this.__updatefieldprop("scrollHeight");this.__updatefieldprop("scrollTop");this.__updatefieldprop("scrollWidth");this.__updatefieldprop("scrollLeft")
};LzTextSprite.prototype.__updatefieldprop=function($0){
var $1=this.scrolldiv[$0];if(this[$0]!==$1||$0=="scrollHeight"){
this[$0]=$1;this.owner.scrollevent($0,$1)
}};LzTextSprite.prototype.setText=function($0,$1){
if(this.multiline&&$0&&$0.indexOf("\n")>=0){
if(this.quirks["inner_html_strips_newlines"]){
$0=$0.replace(this.inner_html_strips_newlines_re,"<br/>")
}};if($0&&this.quirks["inner_html_no_entity_apos"]){
$0=$0.replace(RegExp("&apos;","mg"),"&#39;")
};if($1!=true&&this.text==$0)return;this.text=$0;this.scrolldiv.innerHTML=$0;this.__updatefieldsize();if(this.resize&&this.multiline==false){
this.setWidth(this.getTextWidth())
}};LzTextSprite.prototype.setMultiline=function($0){
$0=$0==true;if(this.multiline==$0)return;this.multiline=$0;if($0){
if(this._whiteSpace!="normal"){
this._whiteSpace="normal";this.scrolldiv.style.whiteSpace="normal"
};this.scrolldiv.style.overflow="hidden"
}else{
if(this._whiteSpace!="nowrap"){
this._whiteSpace="nowrap";this.scrolldiv.style.whiteSpace="nowrap"
};this.scrolldiv.style.overflow=""
};if(this.quirks["text_height_includes_padding"]){
this.__hpadding=$0?3:4
};this.setText(this.text,true)
};LzTextSprite.prototype.setPattern=function($0){
if($0==null||$0==""){
this.restrict=null
}else if(RegExp("^\\[.*\\]\\*$").test($0)){
this.restrict=RegExp($0.substring(0,$0.length-1)+"|[\\r\\n]","g")
}};LzTextSprite.prototype.getTextWidth=function(){
var $0;var $1=this.scrolldiv;var $2=$1.className;var $3=$1.style.cssText;var $4=$2+"/"+$3;var $5=this._cachevalue;if(this._cacheStyleKey==$4&&this._cacheTextKey==this.text&&("width" in $5)){
$0=$5.width
}else{
$0=this.getTextDimension("width")
};if($0!=0&&this.quirks["emulate_flash_font_metrics"]){
$0+=this.__wpadding
};return $0
};LzTextSprite.prototype.getLineHeight=function(){
if(this._lineHeight)return this._lineHeight;var $0=this.scrolldiv;var $1=$0.className;var $2=$0.style.cssText;var $3=$1+"/"+$2;var $4=this._cachevalue;if(this._cacheStyleKey==$3&&("lineheight" in $4)){
var $5=$4.lineheight
}else{
var $5=this.getTextDimension("lineheight")
};return $5
};LzTextSprite.prototype.getTextHeight=LzTextSprite.prototype.getLineHeight;LzTextSprite.prototype.getTextfieldHeight=function(){
var $0=null;if(this.multiline&&this.text!=""){
var $1=this.scrolldiv;var $2=$1.className;var $3=$1.style.cssText;var $4=$2+"/"+$3;var $5=this._cachevalue;if(this._cacheStyleKey==$4&&this._cacheTextKey==this.text&&("height" in $5)){
$0=$5.height
}else{
$0=this.getTextDimension("height")
};if(this.quirks["safari_textarea_subtract_scrollbar_height"]){
$0+=24
}}else{
$0=this.getLineHeight()
};if(this.quirks["emulate_flash_font_metrics"]){
$0+=this.__hpadding
};return $0
};LzTextSprite.prototype._sizecache={counter:0};if(LzSprite.quirks.ie_leak_prevention){
LzTextSprite.prototype.__divstocleanup=[];LzTextSprite.prototype.__cleanupdivs=function(){
var $0=LzTextSprite.prototype.__divstocleanup;var $1=LzSprite.prototype.__discardElement;var $2=$0.length;for(var $3=0;$3<$2;$3++){
$1($0[$3])
};LzTextSprite.prototype.__divstocleanup=[]
}};LzTextSprite.prototype._cacheStyleKey=null;LzTextSprite.prototype._cacheTextKey=null;LzTextSprite.prototype._cachevalue=null;LzTextSprite.prototype.getTextDimension=function($0){
var $1=this.text;var $2="auto";switch($0){
case "lineheight":
if(this._lineHeight){
return this._lineHeight
}$1='Yq_gy"9;';break;
case "height":
$2=this.CSSDimension(this.width);break;
case "width":
if(this.text==""){
return 0
}break;
default:


};var $3=this.scrolldiv;var $4=$3.className;var $5=$3.style.cssText;var $6=$4+"/"+$5;var $7=this._cachevalue;if(this._cacheStyleKey==$6&&($0=="lineheight"||this._cacheTextKey==$1)&&($0 in $7)){
return $7[$0]
};this._cacheStyleKey=$6;if($0!="lineheight"){
this._cacheTextKey=$1
};var $8=$3.style;$5="overflow: visible; width: "+$2+"; height: auto; "+($8.fontSize?"font-size: "+$8.fontSize+"; ":"")+($8.fontWeight?"font-weight: "+$8.fontWeight+"; ":"")+($8.fontStyle?"font-style: "+$8.fontStyle+"; ":"")+($8.fontFamily?"font-family: "+$8.fontFamily+"; ":"")+($8.lineHeight?"line-height: "+$8.lineHeight+"; ":"")+($8.letterSpacing?"letter-spacing: "+$8.letterSpacing+"; ":"")+($8.whiteSpace?"white-space: "+$8.whiteSpace+"; ":"");var $9=$4+"/"+$5+"{"+$1+"}";var $a=LzTextSprite.prototype;var $b=$a._sizecache;var $7=this._cachevalue=$b[$9];if($7&&($0 in $7)){
return $7[$0]
};var $c=document.getElementById("lzTextSizeCache");if($b.counter>0&&$b.counter%this.__sizecacheupperbound==0){
this.__clearMeasureCache();$7=null
};if(!$7){
$7=this._cachevalue=$b[$9]={}};var $d=$4+"/"+$5+"div";var $e=$b[$d];if($e){
this.__setTextContent($e,$3.tagName,$1)
}else{
var $e=this.__createMeasureDiv($4,$5,$1);$b[$d]=$e
};$e.style.display="inline";$7[$0]=$0=="width"?$e.clientWidth:$e.clientHeight;$e.style.display="none";return $7[$0]
};LzTextSprite.prototype.__clearMeasureCache=function(){
var $0=document.getElementById("lzTextSizeCache");LzTextSprite.prototype._sizecache={counter:0};if(LzSprite.quirks.ie_leak_prevention){
LzTextSprite.prototype.__cleanupdivs()
};if($0){
$0.innerHTML=""
}};LzTextSprite.prototype.__createMeasureDiv=function($0,$1,$2){
var $3=document.getElementById("lzTextSizeCache");var $4="div";var $5=LzTextSprite.prototype;var $6=$5._sizecache;if(this.quirks["text_measurement_use_insertadjacenthtml"]){
var $7="<"+$4+' id="testSpan'+$6.counter+'"';$7+=' class="'+$0+'"';$7+=' style="'+$1+'">';$7+=$2;$7+="</"+$4+">";$3.insertAdjacentHTML("beforeEnd",$7);var $8=document.all["testSpan"+$6.counter];if(this.quirks.ie_leak_prevention){
$5.__divstocleanup.push($8)
}}else{
var $8=document.createElement($4);lz.embed.__setAttr($8,"class",$0);lz.embed.__setAttr($8,"style",$1);this.__setTextContent($8,this.scrolldiv.tagName,$2);$3.appendChild($8)
};$6.counter++;return $8
};LzTextSprite.prototype.__setTextContent=function($0,$1,$2){
switch($1){
case "DIV":
$0.innerHTML=$2;break;
case "INPUT":
case "TEXTAREA":
if(this.quirks["text_content_use_inner_text"]){
$0.innerText=$2
}else{
$0.textContent=$2
}break;
default:


}};LzTextSprite.prototype.setSelectable=function($0){
this.selectable=$0;var $1=lz.embed.browser;if($0){
this.__LZdiv.style["cursor"]="auto";if($1.isIE){
this.__LZdiv.onselectstart=null
}else if($1.isFirefox){
this.__LZdiv.style["MozUserSelect"]="text"
}else if($1.isSafari){
this.__LZdiv.style["WebkitUserSelect"]="text"
}else{
this.__LZdiv.style["UserSelect"]="text"
}}else{
this.__LZdiv.style["cursor"]="";if($1.isIE){
this.__LZdiv.onselectstart=LzTextSprite.prototype.__cancelhandler
}else if($1.isFirefox){
this.__LZdiv.style["MozUserSelect"]="none"
}else if($1.isSafari){
this.__LZdiv.style["WebkitUserSelect"]="none"
}else{
this.__LZdiv.style["UserSelect"]="none"
}}};LzTextSprite.prototype.__cancelhandler=function(){
return false
};LzTextSprite.prototype.setResize=function($0){
this.resize=$0==true;this.scrolldiv.style.overflow=this.resize?"":"hidden"
};LzTextSprite.prototype.setSelection=function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null){
$1=$0
};if(this.quirks["text_selection_use_range"]){
var $2=document.body.createTextRange();$2.moveToElementText(this.scrolldiv);if($0>$1){
var $3=$0;$0=$1;$1=$3
};var $3=$0;var $4=$1-$2.text.length;$2.moveStart("character",$3);$2.moveEnd("character",$4);$2.select()
}else{
var $2=document.createRange();var $5=0;$2.setStart(this.scrolldiv.childNodes[0],$0);$2.setEnd(this.scrolldiv.childNodes[0],$1);var $6=window.getSelection();$6.removeAllRanges();$6.addRange($2)
}};LzTextSprite.prototype.__findNodeByOffset=function($0){
var $1=this.scrolldiv.childNodes[0];var $2=0;while($1){
if($1.nodeType==3){
$0+=$1.textContent.length
}else if($1.nodeType==1&&$1.nodeName=="BR"){
$0+=1
};if($2>=$0){
return $1
};$1=$1.nextSibling
}};LzTextSprite.prototype.__getGlobalRange=function(){
var $0=lz.embed.browser;var $1;if(this.quirks["text_selection_use_range"]){
$1=document.selection.createRange()
}else if(window.getSelection){
$1=window.getSelection()
};try{
if($1){
if(this.quirks["text_selection_use_range"]){
return $1
}else if($1.getRangeAt){
return $1.getRangeAt(0)
}else{
var $2=document.createRange();$2.setStart($1.anchorNode,$1.anchorOffset);$2.setEnd($1.focusNode,$1.focusOffset);return $2
}}}
catch($3){}};LzTextSprite.prototype.__textareaToRange=function($0){
var $1=$0.getBookmark();var $2,$3;$3=$2=this.scrolldiv.innerHTML;var $4=this.__getRangeOwner($0);if(!($4 instanceof LzTextSprite)){
return
};do{
var $5="~~~"+Math.random()+"~~~"
}while($2.indexOf($5)!=-1);$0.text=$5+$0.text+$5;$2=this.scrolldiv.innerHTML;$2=$2.replace("<BR>"," ");var $6={};$6.startOffset=$2.indexOf($5);$2=$2.replace($5,"");$6.endOffset=$2.indexOf($5);this.scrolldiv.innerHTML=$3;$0.moveToBookmark($1);$0.select();return $6
};LzTextSprite.prototype.__getRangeOwner=function($0){
if(!$0)return;if(this.quirks["text_selection_use_range"]){
var $0=$0.duplicate();$0.collapse();return $0.parentElement().owner
}else{
if($0.startContainer.parentNode==$0.endContainer.parentNode)return $0.startContainer.parentNode.owner
}};LzTextSprite.prototype.__getOffset=function($0){
var $1=0;while($0=$0.previousSibling){
if($0.nodeType==3){
$1+=$0.textContent.length
}else if($0.nodeType==1&&$0.nodeName=="BR"){
$1+=1
}};return $1
};LzTextSprite.prototype.getSelectionPosition=function(){
var $0=this.__getGlobalRange();if(this.__getRangeOwner($0)===this){
if(this.quirks["text_selection_use_range"]){
$0=this.__textareaToRange($0);return $0.startOffset
}else{
var $1=0;if(this.multiline){
$1=this.__getOffset($0.startContainer)
};return $0.startOffset+$1
}}else{
return -1
}};LzTextSprite.prototype.getSelectionSize=function(){
var $0=this.__getGlobalRange();if(this.__getRangeOwner($0)===this){
if(this.quirks["text_selection_use_range"]){
$0=this.__textareaToRange($0)
}else{
if(this.multiline){
var $1=this.__getOffset($0.startContainer);var $2=this.__getOffset($0.endContainer);return $0.endOffset+$2-($0.startOffset+$1)
}};return $0.endOffset-$0.startOffset
}else{
return -1
}};LzTextSprite.prototype.getScroll=function(){};LzTextSprite.prototype.getMaxScroll=function(){};LzTextSprite.prototype.setScroll=function(){};LzTextSprite.prototype.setYScroll=function($0){
this.scrolldiv.scrollTop=this.scrollTop=-$0
};LzTextSprite.prototype.setXScroll=function($0){
this.scrolldiv.scrollLeft=this.scrollLeft=-$0
};LzTextSprite.prototype.setX=function($0){
var $1=this.scrolling;var $2=$1&&this.quirks["clipped_scrollbar_causes_display_turd"];if($1){
var $3=this.scrolldiv;var $4=$3.scrollLeft;var $5=$3.scrollTop;if($2){
$3.style.overflow="hidden";$3.style.paddingRight=$3.style.paddingBottom=this.quirks.scrollbar_width
}};LzSprite.prototype.setX.call(this,$0);if($1){
if($2){
$3.style.overflow="scroll";$3.style.paddingRight=$3.style.paddingBottom="0"
};$3.scrollLeft=$4;$3.scrollTop=$5
}};LzTextSprite.prototype.setY=function($0){
var $1=this.scrolling;var $2=$1&&this.quirks["clipped_scrollbar_causes_display_turd"];if($1){
var $3=this.scrolldiv;var $4=$3.scrollLeft;var $5=$3.scrollTop;if($2){
$3.style.overflow="hidden";$3.style.paddingRight=$3.style.paddingBottom=this.quirks.scrollbar_width
}};LzSprite.prototype.setY.call(this,$0);if($1){
if($2){
$3.style.overflow="scroll";$3.style.paddingRight=$3.style.paddingBottom="0"
};$3.scrollLeft=$4;$3.scrollTop=$5
}};LzTextSprite.prototype.setWidth=function($0,$1){
if($0==null||$0<0||isNaN($0))return;var $2=LzSprite.prototype.setWidth.call(this,$0);var $3=$0>=this.__wpadding?$0-this.__wpadding:0;var $4=this.scrolldiv;var $5=$4.style;var $6=this.CSSDimension;var $7=$6($3);var $8=this.height;var $9=$6($8!=null?$8:0);if(this.scrolling){
$3+=this.quirks.scrollbar_width
};var $a=this.__LZtextIndent<0?-1*this.__LZtextIndent:0;if($3>=$a){
$3-=$a
};$7=$6($3);if($5.width!=$7){
$4.style.width=$7;if(this.scrolling){
$4.style.clip="rect(0 "+$7+" "+$9+" 0)"
};this.__updatefieldsize()
};return $2
};LzTextSprite.prototype.setHeight=function($0){
if($0==null||$0<0||isNaN($0))return;var $1=LzSprite.prototype.setHeight.call(this,$0);var $2=$0;var $3=this.scrolldiv;var $4=$3.className;var $5=$3.style;var $6=this.CSSDimension;var $7=this.width;var $8=$6($7!=null?$7:0);var $9=$6($2);if(this.scrolling||$4=="lzswfinputtextmultiline"){
$2+=this.quirks.scrollbar_width
};$9=$6($2);if($5.height!=$9){
$3.style.height=$6($2);if(this.scrolling){
$3.style.clip="rect(0 "+$8+" "+$9+" 0)"
};this.__updatefieldsize()
};return $1
};LzTextSprite.prototype.enableClickableLinks=function($0){};LzTextSprite.prototype.makeTextLink=function($0,$1){
LzTextSprite.addLinkID(this);var $2=this.uid;return '<span class="lztextlink" onclick="javascript:$modules.lz.__callTextLink(\''+$2+"', '"+$1+"')\">"+$0+"</span>"
};$modules.lz.__callTextLink=function($0,$1){
var $2=LzTextSprite.linkIDMap[$0];if($2!=null){
$2.owner.ontextlink.sendEvent($1)
}};LzTextSprite.linkIDMap=[];LzTextSprite.addLinkID=function($0){
LzTextSprite.linkIDMap[$0.uid]=$0
};LzTextSprite.deleteLinkID=function($0){
delete LzTextSprite.linkIDMap[$0]
};LzTextSprite.prototype.destroy=function(){
LzTextSprite.deleteLinkID(this.owner.getUID());LzSprite.prototype.destroy.call(this)
};LzTextSprite.prototype.setTextAlign=function($0){
if(this._textAlign!=$0){
this._textAlign=$0;this.scrolldiv.style.textAlign=$0
}};LzTextSprite.prototype.setTextIndent=function($0){
var $1=this.CSSDimension($0);if(this._textIndent!=$1){
var $2=$0<0||this.__LZtextIndent<0;this._textIndent=$1;this.__LZtextIndent=$0;this.scrolldiv.style.textIndent=$1;if($2){
this.scrolldiv.style.paddingLeft=$0>=0?"":$1.substr(1);this.setWidth(this.width,true)
}}};LzTextSprite.prototype.setLetterSpacing=function($0){
$0=this.CSSDimension($0);if(this._letterSpacing!=$0){
this._letterSpacing=$0;this.scrolldiv.style.letterSpacing=$0
}};LzTextSprite.prototype.setTextDecoration=function($0){
if(this._textDecoration!=$0){
this._textDecoration=$0;this.scrolldiv.style.textDecoration=$0
}};LzTextSprite.prototype.getDisplayObject=function(){
return this.scrolldiv
};LzTextSprite.prototype.updateShadow=function($0,$1,$2,$3){
var $4=this.__getShadowCSS($0,$1,$2,$3);this.scrolldiv.style.textShadow=$4;this.shadow=$4;this.scrolldiv.style.overflow=""
};var LzInputTextSprite=function($0){
if($0==null)return;this.constructor=arguments.callee;this.owner=$0;this.uid=LzSprite.prototype.uid++;this.__LZdiv=document.createElement("div");this.__LZdiv.className="lzinputtextcontainer";this.__LZdiv.owner=this;this.dragging=false;if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv=document.createElement("div");this.__LZclickcontainerdiv.className="lzinputtextcontainer_click";this.__LZclickcontainerdiv.owner=this
};if(this.quirks.ie_leak_prevention){
this.__sprites[this.uid]=this
};this.__createInputText()
};LzInputTextSprite.prototype=new LzTextSprite(null);LzInputTextSprite.prototype.__lastshown=null;LzInputTextSprite.prototype.__focusedSprite=null;LzInputTextSprite.prototype.__lastfocus=null;LzInputTextSprite.prototype._cancelfocus=false;LzInputTextSprite.prototype._cancelblur=false;LzInputTextSprite.prototype.____crregexp=new RegExp("\\r\\n","g");LzInputTextSprite.prototype.__createInputText=function($0){
if(this.__LzInputDiv)return;var $1="";if(this.owner){
if(this.owner.password){
$1="password"
}else if(this.owner.multiline){
$1="multiline"
}};this.__createInputDiv($1);if($0==null)$0="";lz.embed.__setAttr(this.__LzInputDiv,"value",$0);if(this.quirks.fix_clickable){
if(this.quirks.fix_ie_clickable){
this.__LZinputclickdiv=document.createElement("img");this.__LZinputclickdiv.src=LzSprite.blankimage
}else{
this.__LZinputclickdiv=document.createElement("div")
};this.__LZinputclickdiv.className="lzclickdiv";this.__LZinputclickdiv.owner=this;if(this.quirks.ie_mouse_events){
this.__LZinputclickdiv.onmouseenter=this.__handlemouse
}else{
this.__LZinputclickdiv.onmouseover=this.__handlemouse
};if(this.quirks.input_highlight_bug){
var $2=document.createElement("div");$2.style.backgroundColor="white";$2.style.width="0px";this.__LZclickcontainerdiv.appendChild($2);$2.appendChild(this.__LZinputclickdiv)
}else{
this.__LZclickcontainerdiv.appendChild(this.__LZinputclickdiv)
}};this.__LZdiv.appendChild(this.__LzInputDiv);this.__setTextEvents(true)
};LzInputTextSprite.prototype.__createInputDiv=function($0){
if($0==="password"){
this.multiline=false;this.__LzInputDiv=document.createElement("input");this.__LZdiv.className="lzinputtextcontainer";lz.embed.__setAttr(this.__LzInputDiv,"type","password")
}else if($0==="multiline"){
this.multiline=true;this.__LzInputDiv=document.createElement("textarea");this.__LZdiv.className="lzinputtextmultilinecontainer"
}else{
this.multiline=false;this.__LzInputDiv=document.createElement("input");this.__LZdiv.className="lzinputtextcontainer";lz.embed.__setAttr(this.__LzInputDiv,"type","text")
};if(this.quirks.fix_clickable){
this.__LZclickcontainerdiv.className=this.__LZdiv.className+"_click"
};if(this.quirks.firefox_autocomplete_bug){
lz.embed.__setAttr(this.__LzInputDiv,"autocomplete","off")
};this.__LzInputDiv.owner=this;if(this.quirks.emulate_flash_font_metrics){
if(this.multiline){
this.__LzInputDiv.className="lzswfinputtextmultiline"
}else{
this.__LzInputDiv.className="lzswfinputtext"
}}else{
this.__LzInputDiv.className="lzinputtext"
};if(this.owner){
lz.embed.__setAttr(this.__LzInputDiv,"name",this.owner.name)
};this.scrolldiv=this.__LzInputDiv;this.scrolldiv.owner=this;this.setScrolling(this.multiline)
};LzInputTextSprite.prototype.setMultiline=function($0){
var $1=this.multiline;this.multiline=$0==true;if($1!=null&&this.multiline!=$1){
var $2=this.__LzInputDiv;this.__setTextEvents(false);this.__createInputDiv($0?"multiline":"");var $3=this.__LzInputDiv;lz.embed.__setAttr($3,"style",$2.style.cssText);if(this.quirks["fix_ie_css_syntax"]){
$3.style.fontStyle=$2.style.fontStyle;$3.style.fontWeight=$2.style.fontWeight;$3.style.fontSize=$2.style.fontSize;$3.style.fontFamily=$2.style.fontFamily
};var $4=$2.scrollLeft;var $5=$2.scrollTop;this.__discardElement($2);this.__LZdiv.appendChild($3);this.setScrollEvents(this.owner.scrollevents);$3.scrollLeft=$4;$3.scrollTop=$5;this.__setTextEvents(true);this.setText(this.text,true)
}};LzInputTextSprite.prototype.__handlemouse=function($0){
var $1=this.owner;if(!$1||!$1.owner||$1.selectable!=true)return;if($1.__fix_inputtext_with_parent_resource){
if(!this.__shown){
$1.setClickable(true);$1.select()
}}else{
$1.__show()
}};LzInputTextSprite.prototype.init=function($0){
this.setVisible($0);if(this.quirks["fix_inputtext_with_parent_resource"]){
var $1=this.__findParents("clickable",true);var $2=$1.length;if($2){
for(var $3=0;$3<$2;$3++){
var $0=$1[$3];if($0.resource!=null){
this.setClickable(true);this.__fix_inputtext_with_parent_resource=true
}}}}};LzInputTextSprite.prototype.__show=function(){
if(this.__shown==true||this.disabled==true)return;this.__hideIfNotFocused();LzInputTextSprite.prototype.__lastshown=this;this.__shown=true;if(this.quirks["inputtext_parents_cannot_contain_clip"]){
var $0=this.__findParents("clip",true);var $1=$0.length;if($1>1){
if(this._shownclipvals==null){
this._shownclipvals=[];this._shownclippedsprites=$0;for(var $2=0;$2<$1;$2++){
var $3=$0[$2];this._shownclipvals[$2]=$3.__LZclickcontainerdiv.style.clip;var $4=this.quirks["fix_ie_css_syntax"]?"rect(auto auto auto auto)":"";$3.__LZclickcontainerdiv.style.clip=$4
}}}};LzMouseKernel.setGlobalClickable(false);if(LzSprite.quirks.prevent_selection){
this.__LZdiv.onselectstart=null
}};LzInputTextSprite.prototype.__hideIfNotFocused=function($0,$1){
var $2=LzInputTextSprite.prototype;if($2.__lastshown==null)return;var $3=LzSprite.quirks;if($3.textgrabsinputtextfocus){
var $4=window.event;if($4&&$4.srcElement&&$4.srcElement.owner&&$4.srcElement.owner instanceof LzTextSprite){
if($0=="onmousedown"){
$2.__lastshown.gotFocus()
};return
}};if($2.__focusedSprite!=$2.__lastshown){
$2.__lastshown.__hide()
}};LzInputTextSprite.prototype.__hide=function($0){
if(this.__shown!=true||this.disabled==true)return;if(LzInputTextSprite.prototype.__lastshown==this){
LzInputTextSprite.prototype.__lastshown=null
};this.__shown=false;if(this.quirks["inputtext_parents_cannot_contain_clip"]){
if(this._shownclipvals!=null){
for(var $1=0;$1<this._shownclipvals.length;$1++){
var $2=this._shownclippedsprites[$1];$2.__LZclickcontainerdiv.style.clip=this._shownclipvals[$1]
};this._shownclipvals=null;this._shownclippedsprites=null
}};LzMouseKernel.setGlobalClickable(true);if(this.__fix_inputtext_with_parent_resource){
this.setClickable(false)
};if(LzInputTextSprite.prototype.__lastshown==null){
if(LzSprite.quirks.prevent_selection){
this.__LZdiv.onselectstart=LzTextSprite.prototype.__cancelhandler
}}};LzInputTextSprite.prototype.gotBlur=function(){
if(LzInputTextSprite.prototype.__focusedSprite!=this)return;this.deselect()
};LzInputTextSprite.prototype.gotFocus=function(){
if(LzInputTextSprite.prototype.__focusedSprite==this)return;this.select()
};LzInputTextSprite.prototype.setText=function($0){
if(this.capabilities["htmlinputtext"]){
if($0.indexOf("<br/>")!=-1){
$0=$0.replace(this.br_to_newline_re,"\r")
}};this.text=$0;this.__createInputText($0);this.__LzInputDiv.value=$0;this.__updatefieldsize()
};LzInputTextSprite.prototype.__setTextEvents=function($0){
var $1=this.__LzInputDiv;var $2=$0?this.__textEvent:null;$1.onblur=$2;$1.onfocus=$2;if(this.quirks.ie_mouse_events){
$1.ondrag=$2;$1.ondblclick=$2;$1.onmouseenter=$2;$1.onmouseleave=$2
}else{
$1.onmouseover=$2;$1.onmouseout=$2
};$1.onmousemove=$2;$1.onmousedown=$2;$1.onmouseup=$2;$1.onclick=$2;$1.onkeyup=$2;$1.onkeydown=$2;$1.onkeypress=$2;$1.onchange=$2;if(this.quirks.ie_paste_event||this.quirks.safari_paste_event){
$1.onpaste=$0?(function($0){
this.owner.__pasteHandlerEx($0)
}):null
}};LzInputTextSprite.prototype.__pasteHandlerEx=function($0){
var $1=!(!this.restrict);var $2=this.multiline&&this.owner.maxlength>0;if($1||$2){
$0=$0?$0:window.event;if(this.quirks.safari_paste_event){
var $3=$0.clipboardData.getData("text/plain")
}else{
var $3=window.clipboardData.getData("TEXT");$3=$3.replace(this.____crregexp,"\n")
};var $4=false;var $5=this.getSelectionSize();if($5<0)$5=0;if($1){
var $6=$3.match(this.restrict);if($6==null){
var $7=""
}else{
var $7=$6.join("")
};$4=$7!=$3;$3=$7
};if($2){
var $8=this.owner.maxlength+$5;if(this.quirks.text_ie_carriagereturn){
var $9=this.__LzInputDiv.value.replace(this.____crregexp,"\n").length
}else{
var $9=this.__LzInputDiv.value.length
};var $a=$8-$9;if($a>0){
if($3.length>$a){
$3=$3.substring(0,$a);$4=true
}}else{
$3="";$4=true
}};if($4){
$0.returnValue=false;if($0.preventDefault){
$0.preventDefault()
};if($3.length>0){
if(this.quirks.safari_paste_event){
var $b=this.__LzInputDiv.value;var $c=this.getSelectionPosition();this.__LzInputDiv.value=$b.substring(0,$c)+$3+$b.substring($c+$5);$c+=$3.length;this.__LzInputDiv.setSelectionRange($c,$c)
}else{
var $d=document.selection.createRange();$d.text=$3
}}}}};LzInputTextSprite.prototype.__pasteHandler=function(){
var selpos=this.getSelectionPosition();var selsize=this.getSelectionSize();var val=this.__LzInputDiv.value;var that=this;setTimeout(function(){
var $0=!(!that.restrict);var $1=that.multiline&&that.owner.maxlength>0;var $2=that.__LzInputDiv.value;var $3=$2.length;var $4=that.owner.maxlength;if($0||$1&&$3>$4){
var $5=val.length;var $6=$2.substr(selpos,$3-$5+selsize);if($0){
var $7=$6.match(that.restrict);$6=$7!=null?$7.join(""):""
};if($1){
var $8=$4+selsize-$5;$6=$6.substring(0,$8)
};that.__LzInputDiv.value=val.substring(0,selpos)+$6+val.substring(selpos+selsize);selpos+=$6.length;that.__LzInputDiv.setSelectionRange(selpos,selpos)
}},1)
};LzInputTextSprite.prototype.__textEvent=function($0){
if(!$0)$0=window.event;var $1=this.owner;var $2=this.owner.owner;if($1.__LZdeleted==true)return;if($1.__skipevent){
$1.__skipevent=false;return
};var $3="on"+$0.type;var $4=$1.quirks;LzMouseKernel.__sendMouseMove($0);if($4.ie_mouse_events){
if($3=="onmouseenter"){
$3="onmouseover"
}else if($3=="onmouseleave"){
$3="onmouseout"
}else if($3=="ondblclick"){
if($1.clickable){
$1.__mouseEvent("onmousedown",true);$1.__mouseEvent("onmouseup",true);$1.__mouseEvent("onclick",true)
};return false
}else if($3=="ondrag"){
return false
}};if($4.autoscroll_textarea){
if($3=="onmousedown"){
$1.dragging=true
}else if($3=="onmouseup"||$3=="onmouseout"){
$1.dragging=false
}};if($1.__shown!=true){
if($3=="onfocus"){
$1.__skipevent=true;$1.__show();$1.__LzInputDiv.blur();LzInputTextSprite.prototype.__lastfocus=$1;LzKeyboardKernel.setKeyboardControl(true)
}};if($3=="onfocus"||$3=="onmousedown"){
if($3=="onfocus"){
LzMouseKernel.setGlobalClickable(false)
};LzInputTextSprite.prototype.__focusedSprite=$1;$1.__show();if($3=="onfocus"&&$1._cancelfocus){
$1._cancelfocus=false;return
};if(window["LzKeyboardKernel"])LzKeyboardKernel.__cancelKeys=false
}else if($3=="onblur"){
if(window["LzKeyboardKernel"])LzKeyboardKernel.__cancelKeys=true;if(LzInputTextSprite.prototype.__focusedSprite===$1){
LzInputTextSprite.prototype.__focusedSprite=null
};if($1.__fix_inputtext_with_parent_resource&&$1.__isMouseOver()){
$1.select();return
};$1.__hide();if($1._cancelblur){
$1._cancelblur=false;return
}}else if($3=="onmousemove"){
if($4.autoscroll_textarea&&$1.dragging){
var $5=$1.__LzInputDiv;var $6=$0.pageY-$5.offsetTop;if($6<=3){
$5.scrollTop-=$1.lineHeight?$1.lineHeight:10
};if($6>=$5.clientHeight-3){
$5.scrollTop+=$1.lineHeight?$1.lineHeight:10
}};return
}else if($3=="onkeypress"){
if($1.restrict||$1.multiline&&$2.maxlength&&$2.maxlength<Infinity){
var $7=$0.keyCode;var $8=$4.text_event_charcode?$0.charCode:$0.keyCode;var $9=!($0.ctrlKey||$0.altKey)&&($8>=32||$7==13);if($9){
var $a=false;if($7!=13&&$1.restrict){
$a=0>String.fromCharCode($8).search($1.restrict)
};if(!$a){
var $b=$1.getSelectionSize();if($b<=0){
if($4.text_ie_carriagereturn){
var $c=$1.__LzInputDiv.value.replace($1.____crregexp,"\n")
}else{
var $c=$1.__LzInputDiv.value
};var $d=$c.length,$e=$2.maxlength;if($d>=$e){
$a=true
}}};if($a){
$0.returnValue=false;if($0.preventDefault){
$0.preventDefault()
}}}else{
if($4.keypress_function_keys){
var $f=false;if($0.ctrlKey&&!$0.altKey&&!$0.shiftKey){
var $g=String.fromCharCode($8);$f=$g=="v"||$g=="V"
}else if($0.shiftKey&&!$0.altKey&&!$0.ctrlKey){
$f=$7==45
};if($f){
if($1.restrict){
$1.__pasteHandler()
}else{
var $d=$1.__LzInputDiv.value.length,$e=$2.maxlength;if($d<$e||$1.getSelectionSize()>0){
$1.__pasteHandler()
}else{
$0.returnValue=false;if($0.preventDefault){
$0.preventDefault()
}}}}}};$1.__updatefieldsize()
};return
};if($2){
if($3=="onkeydown"||$3=="onkeyup"){
var $5=$1.__LzInputDiv;var $h=$5.value;if($h!=$1.text){
$1.text=$h;$1.__updatefieldsize();$2.inputtextevent("onchange",$h)
};if($4.autoscroll_textarea&&$3=="onkeydown"&&$5.selectionStart==$h.length){
$5.scrollTop=$5.scrollHeight-$5.clientHeight+20
}}else{
if($3=="onmousedown"||$3=="onmouseup"||$3=="onmouseover"||$3=="onmouseout"||$3=="onclick"){
$1.__mouseEvent($0);$0.cancelBubble=true;if($3=="onmouseout"){
if(!$1.__isMouseOver()){
$1.__hide()
}};return
};$2.inputtextevent($3)
}}};LzInputTextSprite.prototype.setClickable=function($0){
this.clickable=$0
};LzInputTextSprite.prototype.setEnabled=function($0){
this.disabled=!$0;this.__LzInputDiv.disabled=this.disabled
};LzInputTextSprite.prototype.setMaxLength=function($0){
if($0==Infinity){
$0=~0>>>1
};this.__LzInputDiv.maxLength=$0
};LzInputTextSprite.prototype.select=function(){
this.__show();try{
this.__LzInputDiv.focus()
}
catch($0){};LzInputTextSprite.prototype.__lastfocus=this;setTimeout(LzInputTextSprite.prototype.__selectLastFocused,50);if(window["LzKeyboardKernel"])LzKeyboardKernel.__cancelKeys=false
};LzInputTextSprite.prototype.__selectLastFocused=function(){
if(LzInputTextSprite.prototype.__lastfocus!=null){
LzInputTextSprite.prototype.__lastfocus.__LzInputDiv.select()
}};LzInputTextSprite.prototype.setSelection=function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null){
$1=$0
};this._cancelblur=true;this.__show();LzInputTextSprite.prototype.__lastfocus=this;if(this.quirks["text_selection_use_range"]){
var $2=this.__LzInputDiv.createTextRange();var $3=this.__LzInputDiv.value;if($0>$1){
var $4=$0;$0=$1;$1=$4
};if(this.multiline){
var $5=0;var $6=0;while($5<$0){
$5=$3.indexOf("\r\n",$5+2);if($5==-1)break;$6++
};var $7=0;while($5<$1){
$5=$3.indexOf("\r\n",$5+2);if($5==-1)break;$7++
};var $8=0;while($5<$3.length){
$5=$3.indexOf("\r\n",$5+2);if($5==-1)break;$8++
};var $9=$2.text.length;var $4=$0;var $a=$1-$3.length+$6+$7+$8+1
}else{
var $4=$0;var $a=$1-$2.text.length
};$2.moveStart("character",$4);$2.moveEnd("character",$a);$2.select()
}else{
this.__LzInputDiv.setSelectionRange($0,$1)
};this.__LzInputDiv.focus();if(window["LzKeyboardKernel"])LzKeyboardKernel.__cancelKeys=false
};LzInputTextSprite.prototype.getSelectionPosition=function(){
if(!this.__shown||this.disabled==true)return -1;if(this.quirks["text_selection_use_range"]){
if(this.multiline){
var $0=this._getTextareaSelection()
}else{
var $0=this._getTextSelection()
};if($0){
return $0.start
}else{
return -1
}}else{
return this.__LzInputDiv.selectionStart
}};LzInputTextSprite.prototype.getSelectionSize=function(){
if(!this.__shown||this.disabled==true)return -1;if(this.quirks["text_selection_use_range"]){
if(this.multiline){
var $0=this._getTextareaSelection()
}else{
var $0=this._getTextSelection()
};if($0){
return $0.end-$0.start
}else{
return -1
}}else{
return this.__LzInputDiv.selectionEnd-this.__LzInputDiv.selectionStart
}};if(LzSprite.quirks["text_selection_use_range"]){
LzInputTextSprite.prototype._getTextSelection=function(){
this.__LzInputDiv.focus();var $0=document.selection.createRange();var $1=$0.getBookmark();var $2=contents=this.__LzInputDiv.value;do{
var $3="~~~"+Math.random()+"~~~"
}while(contents.indexOf($3)!=-1);var $4=$0.parentElement();if($4==null||!($4.type=="text"||$4.type=="textarea")){
return
};$0.text=$3+$0.text+$3;contents=this.__LzInputDiv.value;var $5={};$5.start=contents.indexOf($3);contents=contents.replace($3,"");$5.end=contents.indexOf($3);this.__LzInputDiv.value=$2;$0.moveToBookmark($1);$0.select();return $5
};LzInputTextSprite.prototype._getTextareaSelection=function(){
var $0=this.__LzInputDiv;var $1=document.selection.createRange().duplicate();if($1.parentElement()==$0){
var $2=document.body.createTextRange();$2.moveToElementText($0);$2.setEndPoint("EndToStart",$1);var $3=document.body.createTextRange();$3.moveToElementText($0);$3.setEndPoint("StartToEnd",$1);var $4=false,$5=false,$6=false;var $7,$8,$9,$a,$b,$c;$7=$8=$2.text;$9=$a=$1.text;$b=$c=$3.text;do{
if(!$4){
if($2.compareEndPoints("StartToEnd",$2)==0){
$4=true
}else{
$2.moveEnd("character",-1);if($2.text==$7){
$8+="\r\n"
}else{
$4=true
}}};if(!$5){
if($1.compareEndPoints("StartToEnd",$1)==0){
$5=true
}else{
$1.moveEnd("character",-1);if($1.text==$9){
$a+="\r\n"
}else{
$5=true
}}};if(!$6){
if($3.compareEndPoints("StartToEnd",$3)==0){
$6=true
}else{
$3.moveEnd("character",-1);if($3.text==$b){
$c+="\r\n"
}else{
$6=true
}}}}while(!$4||!$5||!$6);var $d=$8+$a+$c;var $e=false;if($0.value==$d){
$e=true
};var $f=$8.length;var $g=$f+$a.length;var $h=$a;var $i=this.__LzInputDiv.value;var $j=0;var $k=0;while($j<$f){
$j=$i.indexOf("\r\n",$j+2);if($j==-1)break;$k++
};var $l=0;while($j<$g){
$j=$i.indexOf("\r\n",$j+2);if($j==-1)break;$l++
};var $m=0;while($j<$i.length){
$j=$i.indexOf("\r\n",$j+2);if($j==-1)break;$m++
};$f-=$k;$g-=$l+$k;return {start:$f,end:$g}}}};LzInputTextSprite.prototype.deselect=function(){
this.__hide();if(this.__LzInputDiv&&this.__LzInputDiv.blur)this.__LzInputDiv.blur();if(window["LzKeyboardKernel"])LzKeyboardKernel.__cancelKeys=true
};LzInputTextSprite.prototype.__fontStyle="normal";LzInputTextSprite.prototype.__fontWeight="normal";LzInputTextSprite.prototype.__fontSize="11px";LzInputTextSprite.prototype.__fontFamily="Verdana,Vera,sans-serif";LzInputTextSprite.prototype.__setFontSize=LzTextSprite.prototype.setFontSize;LzInputTextSprite.prototype.setFontSize=function($0){
this.__setFontSize($0);if(this.__fontSize!=this._fontSize){
this.__fontSize=this._fontSize;this.__LzInputDiv.style.fontSize=this._fontSize
}};LzInputTextSprite.prototype.__setFontStyle=LzTextSprite.prototype.setFontStyle;LzInputTextSprite.prototype.setFontStyle=function($0){
this.__setFontStyle($0);if(this.__fontStyle!=this._fontStyle){
this.__fontStyle=this._fontStyle;this.__LzInputDiv.style.fontStyle=this._fontStyle
};if(this.__fontWeight!=this._fontWeight){
this.__fontWeight=this._fontWeight;this.__LzInputDiv.style.fontWeight=this._fontWeight
}};LzInputTextSprite.prototype.__setFontName=LzTextSprite.prototype.setFontName;LzInputTextSprite.prototype.setFontName=function($0){
this.__setFontName($0);if(this.__fontFamily!=this._fontFamily){
this.__fontFamily=this._fontFamily;this.__LzInputDiv.style.fontFamily=this._fontFamily
}};LzInputTextSprite.prototype.setWidth=function($0){
if($0==null||$0<0||isNaN($0))return;var $1=LzTextSprite.prototype.setWidth.call(this,$0);if(this.quirks.fix_clickable&&$1!=null){
this.__LZinputclickdiv.style.width=$1
}};LzInputTextSprite.prototype.setHeight=function($0){
if($0==null||$0<0||isNaN($0))return;var $1=LzTextSprite.prototype.setHeight.call(this,$0);if(this.quirks.fix_clickable&&$1!=null){
this.__LZinputclickdiv.style.height=$1
}};LzInputTextSprite.prototype.setColor=function($0){
if(this.color==$0)return;this.color=$0;this.__LzInputDiv.style.color=LzColorUtils.inttohex($0)
};LzInputTextSprite.prototype.getText=function(){
if(this.multiline&&this.quirks.text_ie_carriagereturn){
return this.__LzInputDiv.value.replace(this.____crregexp,"\n")
}else{
return this.__LzInputDiv.value
}};LzInputTextSprite.findSelection=function(){
if(LzInputTextSprite.__focusedSprite&&LzInputTextSprite.__focusedSprite.owner){
return LzInputTextSprite.__focusedSprite.owner
}};if(LzSprite.quirks.prevent_selection){
document.onselectstart=function($0){
if(!$0){
$0=window.event;var $1=$0.srcElement
}else{
var $1=$0.srcElement.parentNode
};if($1.owner instanceof LzTextSprite){
if(!$1.owner.selectable){
return false
}}else{
return false
}}};var LzXMLParser={parseXML:function($0,$1,$2){
try{
var $3=new DOMParser();var $4=$3.parseFromString($0,"text/xml");var $5=this.getParserError($4);if($5){
throw new Error($5)
}else{
return $4.firstChild
}}
catch($lzsc$e){
if(Error["$lzsc$isa"]?Error.$lzsc$isa($lzsc$e):$lzsc$e instanceof Error){
lz.$lzsc$thrownError=$lzsc$e
};throw $lzsc$e
}},getParserError:function($0){
var $1=lz.embed.browser;if($1.isIE){
return this.__checkIE($0)
}else if($1.isFirefox||$1.isOpera){
return this.__checkFirefox($0)
}else if($1.isSafari){
return this.__checkSafari($0)
}},__checkIE:function($0){
var $1=$0.parseError;if($1.errorCode!=0){
return $1.reason
}},__checkFirefox:function($0){
var $1=$0.documentElement;if($1&&$1.nodeName=="parsererror"){
var $2=$1.firstChild.nodeValue;return $2.match(".*")[0]
}},__checkSafari:function($0){
var $1=$0.documentElement;if($1 instanceof HTMLElement){
($1=$1.firstChild)&&($1=$1.firstChild)
}else{
$1=$1.firstChild
};if($1&&$1.nodeName=="parsererror"){
var $2=$1.childNodes[1].textContent;return $2.match("[^:]*: (.*)")[1]
}}};if(typeof DOMParser=="undefined"){
var DOMParser=function(){};DOMParser.prototype.parseFromString=function($0,$1){
if(typeof window.ActiveXObject!="undefined"){
var $2=["Msxml2.DOMDocument.6.0","Msxml2.DOMDocument.3.0","MSXML.DomDocument"];var $3=null;for(var $4=0;$4<$2.length;$4++){
try{
$3=new ActiveXObject($2[$4]);break
}
catch($5){}};$3.loadXML($0);return $3
}else if(typeof XMLHttpRequest!="undefined"){
$1=$1||"application/xml";var $6=new XMLHttpRequest();$6.open("GET","data:"+$1+";charset=utf-8,"+encodeURIComponent($0),false);if($6.overrideMimeType){
$6.overrideMimeType($1)
};$6.send(null);return $6.responseXML
}}};var LzXMLTranslator={whitespacePat:new RegExp("^\\s*$"),stringTrimPat:new RegExp("^\\s+|\\s+$","g"),copyXML:function($0,$1,$2){
var $3=this.copyBrowserXML($0,true,$1,$2);if($3 instanceof LzDataElement){
return $3
}else{
return null
}},copyBrowserNode:function($0,$1,$2,$3){
var $4=$0.nodeType;if($4==3||$4==4){
var $5=$0.nodeValue;if(!($1&&this.whitespacePat.test($5))){
if($2){
$5=$5.replace(this.stringTrimPat,"")
};return new LzDataText($5)
}}else if($4==1||$4==9){
var $6=!$3&&($0.localName||$0.baseName)||$0.nodeName;var $7={};var $8=$0.attributes;if($8){
for(var $9=0,$a=$8.length;$9<$a;$9++){
var $b=$8[$9];if($b){
var $c=!$3&&($b.localName||$b.baseName)||$b.name;$7[$c]=$b.value
}}};var $d=new LzDataElement($6);$d.attributes=$7;return $d
}},copyBrowserXML:function($0,$1,$2,$3){
var $4=new LzDataElement(null);if(!$0.firstChild){
return $4.appendChild(this.copyBrowserNode($0,$1,$2,$3))
};var $5=this.whitespacePat;var $6=this.stringTrimPat;var $7=$4;var $8,$9=$0;for(;;){
var $a=$9.nodeType;if($a==3||$a==4){
var $b=$9.nodeValue;if(!($1&&$5.test($b))){
if($2){
$b=$b.replace($6,"")
};var $c=$7.childNodes;var $d=$c[$c.length-1];if($d instanceof LzDataText){
$d.data+=$b
}else{
var $e=new LzDataText($b);$e.parentNode=$7;$e.ownerDocument=$4;$e.__LZo=$c.push($e)-1
}}}else if($a==1||$a==9){
var $f=!$3&&($9.localName||$9.baseName)||$9.nodeName;var $g={};var $h=$9.attributes;if($h){
for(var $i=0,$j=$h.length;$i<$j;$i++){
var $k=$h[$i];if($k){
var $l=!$3&&($k.localName||$k.baseName)||$k.name;$g[$l]=$k.value
}}};var $e=new LzDataElement($f);$e.attributes=$g;$e.parentNode=$7;$e.ownerDocument=$4;$e.__LZo=$7.childNodes.push($e)-1;if($8=$9.firstChild){
$7=$e;$9=$8;continue
}};while(!($8=$9.nextSibling)){
$9=$9.parentNode;$7=$7.parentNode;if($9===$0){
return $4.childNodes[0]
}};$9=$8
}}};var LzHTTPLoader=function($0,$1){
this.owner=$0;this.options={parsexml:true,serverproxyargs:null};this.requestheaders={};this.requestmethod=LzHTTPLoader.GET_METHOD;this.__loaderid=LzHTTPLoader.loaderIDCounter++
};LzHTTPLoader.GET_METHOD="GET";LzHTTPLoader.POST_METHOD="POST";LzHTTPLoader.PUT_METHOD="PUT";LzHTTPLoader.DELETE_METHOD="DELETE";LzHTTPLoader.activeRequests={};LzHTTPLoader.loaderIDCounter=0;LzHTTPLoader.prototype.loadSuccess=function($0,$1){};LzHTTPLoader.prototype.loadError=function($0,$1){};LzHTTPLoader.prototype.loadTimeout=function($0,$1){};LzHTTPLoader.prototype.loadContent=function($0,$1){
if(this.options["parsexml"]){
this.translateXML()
}else{
this.loadSuccess(this,$1)
}};LzHTTPLoader.prototype.translateXML=function(){
var $0=this.responseXML;if($0==null||$0.childNodes.length==0||lz.embed.browser.isFirefox&&LzXMLParser.getParserError($0)!=null){
this.loadError(this,null)
}else{
var $1;var $2=$0.childNodes;for(var $3=0;$3<$2.length;$3++){
var $4=$2.item($3);if($4.nodeType==1){
$1=$4;break
}};if($1!=null){
var $5=LzXMLTranslator.copyXML($1,this.options.trimwhitespace,this.options.nsprefix);this.loadSuccess(this,$5)
}else{
this.loadError(this,null)
}}};LzHTTPLoader.prototype.getResponse=function(){
return this.responseText
};LzHTTPLoader.prototype.getResponseStatus=function(){
return this.responseStatus
};LzHTTPLoader.prototype.getResponseHeaders=function(){
return this.responseHeaders
};LzHTTPLoader.prototype.getResponseHeader=function($0){
return this.responseHeaders[$0]
};LzHTTPLoader.prototype.setRequestHeaders=function($0){
this.requestheaders=$0
};LzHTTPLoader.prototype.setRequestHeader=function($0,$1){
this.requestheaders[$0]=$1
};LzHTTPLoader.prototype.setOption=function($0,$1){
this.options[$0]=$1
};LzHTTPLoader.prototype.getOption=function($0){
return this.options[$0]
};LzHTTPLoader.prototype.setProxied=function($0){
this.setOption("proxied",$0)
};LzHTTPLoader.prototype.setQueryParams=function($0){
this.queryparams=$0
};LzHTTPLoader.prototype.setQueryString=function($0){
this.querystring=$0
};LzHTTPLoader.prototype.setQueueing=function($0){
this.setOption("queuing",$0)
};LzHTTPLoader.prototype.abort=function(){
if(this.req){
this.__abort=true;this.req.abort();this.req=null;this.removeTimeout(this)
}};LzHTTPLoader.prototype.open=function($0,$1,$2,$3){
if(this.req){
this.abort()
};this.req=window.XMLHttpRequest?new XMLHttpRequest():new ActiveXObject("Microsoft.XMLHTTP");this.responseStatus=0;this.responseHeaders=null;this.responseText=null;this.responseXML=null;this.__abort=false;this.__timeout=false;this.requesturl=$1;this.requestmethod=$0
};LzHTTPLoader.prototype.send=function($0){
this.loadXMLDoc(this.requestmethod,this.requesturl,this.requestheaders,$0,true)
};LzHTTPLoader.prototype.makeProxiedURL=function($0,$1,$2,$3,$4,$5){
var $6={serverproxyargs:this.options.serverproxyargs,sendheaders:this.options.sendheaders,trimwhitespace:this.options.trimwhitespace,nsprefix:this.options.nsprefix,timeout:this.timeout,cache:this.options.cacheable,ccache:this.options.ccache,proxyurl:$0,url:$1,secure:this.secure,postbody:$5,headers:$4,httpmethod:$2,service:$3};return lz.Browser.makeProxiedURL($6)
};LzHTTPLoader.prototype.timeout=Infinity;LzHTTPLoader.prototype.setTimeout=function($0){
this.timeout=$0
};LzHTTPLoader.prototype.setupTimeout=function($0,$1){
var $2=new Date().getTime()+$1;var $3=$0.__loaderid;LzHTTPLoader.activeRequests[$3]=[$0,$2];var $4=setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts("+$3+")",$1);LzHTTPLoader.activeRequests[$3][2]=$4
};LzHTTPLoader.prototype.removeTimeout=function($0){
var $1=$0.__loaderid;if($1!=null){
var $2=LzHTTPLoader.activeRequests[$1];if($2&&$2[0]===$0){
clearTimeout($2[2]);delete LzHTTPLoader.activeRequests[$1]
}}};LzHTTPLoader.__LZcheckXMLHTTPTimeouts=function($0){
var $1=LzHTTPLoader.activeRequests[$0];if($1){
var $2=new Date().getTime();var $3=$1[0];var $4=$1[1];if($2>=$4){
delete LzHTTPLoader.activeRequests[$0];$3.__timeout=true;if($3.req){
$3.req.abort()
};$3.req=null;$3.loadTimeout($3,null)
}else{
var $5=setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts("+$0+")",$2-$4);$1[2]=$5
}}};LzHTTPLoader.prototype.getElapsedTime=function(){
return new Date().getTime()-this.gstart
};LzHTTPLoader.prototype.__setRequestHeaders=function($0,$1){
if($1!=null){
for(var $2 in $1){
var $3=$1[$2];$0.setRequestHeader($2,$3)
}}};LzHTTPLoader.prototype.__getAllResponseHeaders=function($0){
var $1=new RegExp("^([-\\w]+):\\s*(\\S(?:.*\\S)?)\\s*$","mg");var $2=$0.getAllResponseHeaders();var $3={};var $4;while(($4=$1.exec($2))!=null){
$3[$4[1]]=$4[2]
};return $3
};LzHTTPLoader.prototype.loadXMLDoc=function($0,$1,$2,$3,$4){
if(this.req){
var self=this;this.req.onreadystatechange=function(){
var $0=self.req;if($0==null){
return
};if($0.readyState==4){
if(self.__timeout){

}else if(self.__abort){

}else{
self.removeTimeout(self);self.req=null;var $1=-1;try{
$1=$0.status
}
catch($2){};self.responseStatus=$1;if($1==200||$1==304){
self.responseXML=$0.responseXML;self.responseText=$0.responseText;self.responseHeaders=self.__getAllResponseHeaders($0);self.loadContent(self,self.responseText)
}else{
self.loadError(self,null)
}}}};try{
this.req.open($0,$1,true)
}
catch($5){
this.req=null;this.loadError(this,null);return
};if($0=="POST"&&$2["content-type"]==null){
$2["content-type"]="application/x-www-form-urlencoded"
};this.__setRequestHeaders(this.req,$2);this.gstart=new Date().getTime();try{
this.req.send($3)
}
catch($5){
this.req=null;this.loadError(this,null);return
};if(isFinite(this.timeout)){
this.setupTimeout(this,this.timeout)
}}};var LzScreenKernel={width:null,height:null,__resizeEvent:function(){
var $0=LzSprite.__rootSpriteContainer;LzScreenKernel.width=$0.offsetWidth;LzScreenKernel.height=$0.offsetHeight;if(LzScreenKernel.__callback)LzScreenKernel.__scope[LzScreenKernel.__callback]({width:LzScreenKernel.width,height:LzScreenKernel.height})
},__init:function(){
lz.embed.attachEventHandler(window.top,"resize",LzScreenKernel,"__resizeEvent")
},__callback:null,__scope:null,setCallback:function($0,$1){
this.__scope=$0;this.__callback=$1;this.__init();this.__resizeEvent()
}};Class.make("LzContextMenuKernel",null,["$lzsc$initialize",function($0){
this.owner=$0
},"owner",null,"showbuiltins",false,"_delegate",null,"setDelegate",function($0){
this._delegate=$0
},"addItem",function($0){},"hideBuiltInItems",function(){
this.showbuiltins=false
},"showBuiltInItems",function(){
this.showbuiltins=true
},"clearItems",function(){},"__show",function(){
var $0=this.owner;var $1=this._delegate;if($1!=null)$1.execute($0);if($0.onmenuopen.ready)$0.onmenuopen.sendEvent($0);var $2=[];var $3=$0.getItems();var $4={};for(var $5=0;$5<$3.length;$5++){
var $6=$3[$5].kernel.cmenuitem;var $7=$6.caption;if($6.visible!=true||($7 in $4)){
continue
};$4[$7]=true;if($6.separatorBefore){
$2.push({type:"separator"})
};if($6.enabled){
$2.push({type:"text",label:$7,offset:$5})
}else{
$2.push({type:"disabled",label:$7,offset:$5})
}};LzMouseKernel.__showncontextmenu=this;var $8=LzContextMenuKernel.lzcontextmenu||LzContextMenuKernel.__create();$8.setItems($2);$8.show()
},"__hide",function(){
LzMouseKernel.__showncontextmenu=null
},"__select",function($0){
var $1=this.owner.getItems();if($1&&$1[$0])$1[$0].kernel.__select()
}],["lzcontextmenu",null,"__create",function(){
var $0=LzContextMenuKernel.lzcontextmenu;if(!$0){
LzContextMenuKernel.lzcontextmenu=$0=new (lz.lzcontextmenu)(canvas)
};return $0
}]);Class.make("LzContextMenuItemKernel",null,["$lzsc$initialize",function($0,$1,$2){
this.owner=$0;this.cmenuitem={visible:true,enabled:true,separatorBefore:false,caption:$1};this.setDelegate($2)
},"owner",null,"cmenuitem",null,"_delegate",null,"setDelegate",function($0){
this._delegate=$0
},"setCaption",function($0){
this.cmenuitem.caption=$0
},"getCaption",function(){
return this.cmenuitem.caption
},"setEnabled",function($0){
this.cmenuitem.enabled=$0
},"setSeparatorBefore",function($0){
this.cmenuitem.separatorBefore=$0
},"setVisible",function($0){
this.cmenuitem.visible=$0
},"__select",function(){
var $0=this.owner;var $1=this._delegate;if($1!=null){
if($1 instanceof LzDelegate){
$1.execute($0)
}else if(typeof $1=="function"){
$1()
}};if($0.onselect.ready)$0.onselect.sendEvent($0)
}],null);if(LzSprite.quirks.ie_timer_closure){
(function($0){
window.setTimeout=$0(window.setTimeout);window.setInterval=$0(window.setInterval)
})(function(f){
return( function(c,$0){
var a=Array.prototype.slice.call(arguments,2);if(typeof c!="function")c=new Function(c);return( f(function(){
c.apply(this,a)
},$0))
})
})
};var LzTimeKernel={setTimeout:function(){
return window.setTimeout.apply(window,arguments)
},setInterval:function(){
return window.setInterval.apply(window,arguments)
},clearTimeout:function($0){
return window.clearTimeout($0)
},clearInterval:function($0){
return window.clearInterval($0)
},startTime:new Date().valueOf(),getTimer:function(){
return new Date().valueOf()-LzTimeKernel.startTime
}};var LzFontManager=new Object();LzFontManager.fonts={};LzFontManager.addFont=function($0,$1,$2,$3,$4){
var $5={name:$0,style:$1,weight:$2,url:$3,ptype:$4};LzFontManager.fonts[$0+"_"+$1+"_"+$2]=$5
};LzFontManager.generateCSS=function(){
var $0=LzFontManager.fonts;var $1="";for(var $2 in $0){
var $3=$0[$2];var $4=LzFontManager.getURL($3);var $2=$4.lastIndexOf(".ttf");var $5=$4.substring(0,$2)+".eot";$1+="@font-face{font-family:"+$3.name+";src:url("+$5+');src:local("'+$3.name+'"), url('+$4+') format("truetype");font-weight:'+$3.weight+";font-style:"+$3.style+";}"
};return $1
};LzFontManager.getURL=function($0){
var $1=LzSprite.prototype.getBaseUrl($0);return $1+$0.url
};LzFontManager.getFont=function($0,$1,$2){
return this.fonts[$0+"_"+$1+"_"+$2]
};Class.make("LzView",LzNode,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"__LZlayout",void 0,"__LZstoredbounds",void 0,"__movecounter",0,"__mousecache",null,"playing",false,"_visible",void 0,"$lzc$set_visible",function($0){
if(this._visible==$0)return;this._visible=$0;if($0){
var $1="visible"
}else if($0==null){
var $1="collapse"
}else{
var $1="hidden"
};this.visibility=$1;if(this.onvisibility.ready)this.onvisibility.sendEvent(this.visibility);this.__LZupdateShown()
},"onaddsubview",LzDeclaredEvent,"onblur",LzDeclaredEvent,"onclick",LzDeclaredEvent,"onclickable",LzDeclaredEvent,"onfocus",LzDeclaredEvent,"onframe",LzDeclaredEvent,"onheight",LzDeclaredEvent,"onkeyup",LzDeclaredEvent,"onkeydown",LzDeclaredEvent,"onlastframe",LzDeclaredEvent,"onload",LzDeclaredEvent,"onframesloadratio",LzDeclaredEvent,"onloadratio",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"onmousedown",LzDeclaredEvent,"onmouseout",LzDeclaredEvent,"onmouseover",LzDeclaredEvent,"onmousetrackover",LzDeclaredEvent,"onmousetrackup",LzDeclaredEvent,"onmousetrackout",LzDeclaredEvent,"onmouseup",LzDeclaredEvent,"onmousedragin",LzDeclaredEvent,"onmousedragout",LzDeclaredEvent,"onmouseupoutside",LzDeclaredEvent,"onopacity",LzDeclaredEvent,"onplay",LzDeclaredEvent,"onremovesubview",LzDeclaredEvent,"onresource",LzDeclaredEvent,"onresourceheight",LzDeclaredEvent,"onresourcewidth",LzDeclaredEvent,"onrotation",LzDeclaredEvent,"onstop",LzDeclaredEvent,"ontotalframes",LzDeclaredEvent,"onunstretchedheight",LzDeclaredEvent,"onunstretchedwidth",LzDeclaredEvent,"onvisible",LzDeclaredEvent,"onvisibility",LzDeclaredEvent,"onwidth",LzDeclaredEvent,"onx",LzDeclaredEvent,"onxoffset",LzDeclaredEvent,"ony",LzDeclaredEvent,"onyoffset",LzDeclaredEvent,"onfont",LzDeclaredEvent,"onfontsize",LzDeclaredEvent,"onfontstyle",LzDeclaredEvent,"ondblclick",LzDeclaredEvent,"DOUBLE_CLICK_TIME",500,"onclip",LzDeclaredEvent,"capabilities",void 0,"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0?$0:canvas,$1);this.__LZdelayedSetters=LzView.__LZdelayedSetters;this.earlySetters=LzView.earlySetters;this.mask=this.immediateparent.mask;this.__makeSprite($1);this.capabilities=this.sprite.capabilities;if($1["width"]!=null||this.__LZhasConstraint("width")){
this.hassetwidth=true;this.__LZcheckwidth=false
};if($1["height"]!=null||this.__LZhasConstraint("height")){
this.hassetheight=true;this.__LZcheckheight=false
};if($1["clip"]){
this.clip=$1.clip;this.makeMasked()
};if($1["stretches"]!=null){
this.$lzc$set_stretches($1.stretches);$1.stretches=LzNode._ignoreAttribute
};if($1["resource"]!=null){
this.$lzc$set_resource($1.resource);$1.resource=LzNode._ignoreAttribute
};if($1["fgcolor"]!=null){
this.hasfgcolor=true
}},"__spriteAttribute",function($0,$1){
if(this[$0]){
if(!this.__LZdeleted){
var $lzsc$2087666469="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$2087666469]):this[$lzsc$2087666469] instanceof Function){
this[$lzsc$2087666469]($1)
}else{
this[$0]=$1;var $lzsc$1269141965=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1269141965):$lzsc$1269141965 instanceof LzEvent){
if($lzsc$1269141965.ready){
$lzsc$1269141965.sendEvent($1)
}}}}}},"__makeSprite",function($0){
this.sprite=new LzSprite(this,false)
},"init",function(){
if(this.__updateshadowoninit){
delete this.__updateshadowoninit;this.__updateShadow()
};if(this.sprite){
this.sprite.init(this.visible)
}},"addSubview",function($0){
if($0.addedToParent)return;if(this.sprite){
this.sprite.addChildSprite($0.sprite)
};if(this.subviews.length==0){
this.subviews=[]
};this.subviews.push($0);$0.addedToParent=true;if(this.__LZcheckwidth)this.__LZcheckwidthFunction($0);if(this.__LZcheckheight)this.__LZcheckheightFunction($0);if(this.onaddsubview.ready)this.onaddsubview.sendEvent($0)
},"__LZinstantiationDone",function(){
var $0=this.immediateparent;if($0){
$0.addSubview(this)
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZinstantiationDone"]||this.nextMethod(arguments.callee,"__LZinstantiationDone")).call(this)
},"mask",void 0,"focusable",false,"focustrap",void 0,"clip",false,"$lzc$set_clip",function($0){
this.clip=$0;if($0){
this.makeMasked()
}else{
this.removeMask()
};if(this.onclip.ready)this.onclip.sendEvent(this.clip)
},"align","left","$lzc$set_align",function($0){
var $1;$1=function($0){
switch($0){
case "center":
return "__LZalignCenter";
case "right":
return "__LZalignRight";
case "left":
return null;

}};if(this.align==$0)return;var $2=$1(this.align);var $3=$1($0);if($2!=null){
this.releaseConstraintMethod($2)
};if($3!=null){
this.applyConstraintMethod($3,[this.immediateparent,"width",this,"width"])
}else{
this.$lzc$set_x(0)
};this.align=$0
},"valign","top","$lzc$set_valign",function($0){
var $1;$1=function($0){
switch($0){
case "middle":
return "__LZvalignMiddle";
case "bottom":
return "__LZvalignBottom";
case "top":
return null;

}};if(this.valign==$0)return;var $2=$1(this.valign);var $3=$1($0);if($2!=null){
this.releaseConstraintMethod($2)
};if($3!=null){
this.applyConstraintMethod($3,[this.immediateparent,"height",this,"height"])
}else{
this.$lzc$set_y(0)
};this.valign=$0
},"source",void 0,"$lzc$set_source",function($0){
this.setSource($0)
},"clickregion",void 0,"$lzc$set_clickregion",function($0){
if(this.capabilities.clickregion){
this.sprite.setClickRegion($0)
};this.clickregion=$0
},"cursor",void 0,"fgcolor",0,"hasfgcolor",false,"onfgcolor",LzDeclaredEvent,"$lzc$set_fgcolor",function($0){
if($0!=null&&isNaN($0)){
$0=this.acceptTypeValue("color",$0)
};this.sprite.setColor($0);this.fgcolor=$0;if(this.onfgcolor.ready)this.onfgcolor.sendEvent($0)
},"font",void 0,"fontname",void 0,"$lzc$set_font",function($0){
this.font=$0;this.fontname=$0;if(this.onfont.ready){
this.onfont.sendEvent(this.font)
}},"fontstyle",void 0,"$lzc$set_fontstyle",function($0){
if($0=="plain"||$0=="bold"||$0=="italic"||$0=="bolditalic"||$0=="bold italic"){
this.fontstyle=$0;if(this.onfontstyle.ready){
this.onfontstyle.sendEvent(this.fontstyle)
}}},"fontsize",void 0,"$lzc$set_fontsize",function($0){
if(!($0<=0||isNaN($0))){
this.fontsize=$0;if(this.onfontsize.ready){
this.onfontsize.sendEvent(this.fontsize)
}}},"stretches","none","$lzc$set_stretches",function($0){
if(!($0=="none"||$0=="both"||$0=="width"||$0=="height")){
var $1=$0==null?"both":($0=="x"?"width":($0=="y"?"height":"none"));$0=$1
}else if(this.stretches==$0){
return
};this.stretches=$0;this.sprite.stretchResource($0);if($0=="width"||$0=="both"){
this._setrescwidth=true;this.__LZcheckwidth=true;this.reevaluateSize("width")
};if($0=="height"||$0=="both"){
this._setrescheight=true;this.__LZcheckheight=true;this.reevaluateSize("height")
}},"layout",void 0,"$lzc$set_layout",function($0){
this.layout=$0;if(!this.isinited){
this.__LZstoreAttr($0,"layout");return
};var $1=$0["class"];if($1==null){
$1="simplelayout"
};if(this.__LZlayout){
this.__LZlayout.destroy()
};if($1!="none"){
var $2={};for(var $3 in $0){
if($3!="class"){
$2[$3]=$0[$3]
}};if($1=="null"){
this.__LZlayout=null;return
};this.__LZlayout=new (lz[$1])(this,$2)
}},"aaactive",void 0,"$lzc$set_aaactive",function($0){
if(this.capabilities.accessibility){
this.aaactive=$0;this.sprite.setAAActive($0)
}},"aaname",void 0,"$lzc$set_aaname",function($0){
if(this.capabilities.accessibility){
this.aaname=$0;this.sprite.setAAName($0)
}},"aadescription",void 0,"$lzc$set_aadescription",function($0){
if(this.capabilities.accessibility){
this.aadescription=$0;this.sprite.setAADescription($0)
}},"aatabindex",void 0,"$lzc$set_aatabindex",function($0){
if(this.capabilities.accessibility){
this.aatabindex=$0;this.sprite.setAATabIndex($0)
}},"aasilent",void 0,"$lzc$set_aasilent",function($0){
if(this.capabilities.accessibility){
this.aasilent=$0;this.sprite.setAASilent($0)
}},"sendAAEvent",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=false;

};if(this.capabilities.accessibility){
this.sprite.sendAAEvent($0,$1,$2)
}},"sprite",null,"visible",true,"visibility","collapse","$lzc$set_visibility",function($0){
if(this.visibility==$0)return;this.visibility=$0;if(this.onvisibility.ready)this.onvisibility.sendEvent($0);this.__LZupdateShown()
},"__LZvizO",true,"__LZvizLoad",true,"__LZvizDat",true,"opacity",1,"$lzc$set_opacity",function($0){
if(this.capabilities.opacity){
this.sprite.setOpacity($0)
};this.opacity=$0;if(this.onopacity.ready)this.onopacity.sendEvent($0);var $1=this.__LZvizO;var $2=$0!=0;if($1!=$2){
this.__LZvizO=$2;this.__LZupdateShown()
}},"$lzc$set_alpha",function($0){
this.$lzc$set_opacity($0)
},"bgcolor",null,"onbgcolor",LzDeclaredEvent,"$lzc$set_bgcolor",function($0){
if($0!=null&&isNaN($0)){
$0=this.acceptTypeValue("color",$0)
}else if($0<0){
$0=null
};this.sprite.setBGColor($0);this.bgcolor=$0;if(this.onbgcolor.ready)this.onbgcolor.sendEvent($0)
},"x",0,"__set_x_memo",void 0,"$lzc$set_x",function($0){
this.x=$0;if(this.__set_x_memo===$0){
if(this.onx.ready){
this.onx.sendEvent(this.x)
};return
};this.__set_x_memo=$0;this.__mousecache=null;if(this.__LZhasoffset){
if(this.capabilities.rotation){
$0-=this.xoffset*this.__LZrcos-this.yoffset*this.__LZrsin
}else{
$0-=this.xoffset
}};if(this.pixellock)$0=Math.floor($0);this.sprite.setX($0);var $1=this.immediateparent;if($1.__LZcheckwidth){
$1.__LZcheckwidthFunction(this)
};if(this.onx.ready){
this.onx.sendEvent(this.x)
}},"y",0,"__set_y_memo",void 0,"$lzc$set_y",function($0){
this.y=$0;if(this.__set_y_memo===$0){
if(this.ony.ready){
this.ony.sendEvent(this.y)
};return
};this.__set_y_memo=$0;this.__mousecache=null;if(this.__LZhasoffset){
if(this.capabilities.rotation){
$0-=this.xoffset*this.__LZrsin+this.yoffset*this.__LZrcos
}else{
$0-=this.yoffset
}};if(this.pixellock)$0=Math.floor($0);this.sprite.setY($0);var $1=this.immediateparent;if($1.__LZcheckheight){
$1.__LZcheckheightFunction(this)
};if(this.ony.ready){
this.ony.sendEvent(this.y)
}},"rotation",0,"$lzc$set_rotation",function($0){
if(this.capabilities.rotation){
this.sprite.setRotation($0)
};this.rotation=$0;this.usegetbounds=this.__LZhasoffset||this.rotation!=0;var $1=Math.PI/180*this.rotation;this.__LZrsin=Math.sin($1);this.__LZrcos=Math.cos($1);if(this.onrotation.ready)this.onrotation.sendEvent($0);if(this.__LZhasoffset){
this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y)
};var $2=this.immediateparent;if($2.__LZcheckwidth)$2.__LZcheckwidthFunction(this);if($2.__LZcheckheight)$2.__LZcheckheightFunction(this)
},"width",0,"__set_width_memo",void 0,"$lzc$set_width",function($0){
if($0!=null){
this.hassetwidth=true;this.width=$0
}else{
this.hassetwidth=false
};if(this.__set_width_memo===$0){
if(this.onwidth.ready){
this.onwidth.sendEvent(this.width)
};return
};this.__set_width_memo=$0;if($0==null){
this.__LZcheckwidth=true;if(this._setrescwidth){
this.unstretchedwidth=null;this._xscale=1
};this.reevaluateSize("width");return
};if(this.pixellock){
$0=Math.floor($0)
};if(this._setrescwidth){
var $1=this.unstretchedwidth==0?100:$0/this.unstretchedwidth;this._xscale=$1
}else{
this.__LZcheckwidth=false
};this.sprite.setWidth($0);var $2=this.immediateparent;if($2&&$2.__LZcheckwidth){
$2.__LZcheckwidthFunction(this)
};if(this.onwidth.ready){
this.onwidth.sendEvent(this.width)
}},"height",0,"__set_height_memo",void 0,"$lzc$set_height",function($0){
if($0!=null){
this.hassetheight=true;this.height=$0
}else{
this.hassetheight=false
};if(this.__set_height_memo===$0){
if(this.onheight.ready){
this.onheight.sendEvent(this.height)
};return
};this.__set_height_memo=$0;if($0==null){
this.__LZcheckheight=true;if(this._setrescheight){
this.unstretchedheight=null;this._yscale=1
};this.reevaluateSize("height");return
};if(this.pixellock){
$0=Math.floor($0)
};if(this._setrescheight){
this._yscale=this.unstretchedheight==0?100:$0/this.unstretchedheight
}else{
this.__LZcheckheight=false
};this.sprite.setHeight($0);var $1=this.immediateparent;if($1&&$1.__LZcheckheight){
$1.__LZcheckheightFunction(this)
};if(this.onheight.ready){
this.onheight.sendEvent(this.height)
}},"unstretchedwidth",0,"unstretchedheight",0,"subviews",[],"xoffset",0,"$lzc$set_xoffset",function($0){
this.xoffset=$0;this.__LZhasoffset=this.xoffset!=0||this.yoffset!=0||this.__widthoffset!=0||this.__heightoffset!=0;this.usegetbounds=this.__LZhasoffset||this.rotation!=0;this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y);if(this.onxoffset.ready)this.onxoffset.sendEvent($0)
},"yoffset",0,"$lzc$set_yoffset",function($0){
this.yoffset=$0;this.__LZhasoffset=this.xoffset!=0||this.yoffset!=0||this.__widthoffset!=0||this.__heightoffset!=0;this.usegetbounds=this.__LZhasoffset||this.rotation!=0;this.__set_x_memo=void 0;this.$lzc$set_x(this.x);this.__set_y_memo=void 0;this.$lzc$set_y(this.y);if(this.onyoffset.ready)this.onyoffset.sendEvent($0)
},"__LZrsin",0,"__LZrcos",1,"_xscale",1,"_yscale",1,"totalframes",1,"frame",1,"$lzc$set_frame",function($0){
this.frame=$0;this.stop($0);if(this.onframe.ready)this.onframe.sendEvent($0)
},"framesloadratio",0,"loadratio",0,"hassetheight",false,"hassetwidth",false,"addedToParent",null,"masked",false,"pixellock",null,"clickable",false,"$lzc$set_clickable",function($0){
this.sprite.setClickable($0);this.clickable=$0;if(this.onclickable.ready)this.onclickable.sendEvent($0)
},"showhandcursor",null,"$lzc$set_showhandcursor",function($0){
this.showhandcursor=$0;this.sprite.setShowHandCursor($0)
},"resource",null,"$lzc$set_resource",function($0){
if($0==null||$0==this._resource)return;this.resource=this._resource=$0;this.sprite.setResource($0)
},"resourcewidth",0,"resourceheight",0,"__LZcheckwidth",true,"__LZcheckheight",true,"__LZhasoffset",null,"__LZoutlieheight",null,"__LZoutliewidth",null,"setLayout",function($0){
this.$lzc$set_layout($0)
},"setFontName",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.$lzc$set_font($0)
},"_setrescwidth",false,"_setrescheight",false,"searchSubviews",function($0,$1){
var $2=this.subviews.concat();while($2.length>0){
var $3=$2;$2=new Array();for(var $4=$3.length-1;$4>=0;$4--){
var $5=$3[$4];if($5[$0]==$1){
return $5
};var $6=$5.subviews;for(var $7=$6.length-1;$7>=0;$7--){
$2.push($6[$7])
}}};return null
},"layouts",null,"releaseLayouts",function(){
if(this.layouts){
for(var $0=this.layouts.length-1;$0>=0;$0--){
this.layouts[$0].releaseLayout()
}}},"_resource",null,"setResource",function($0){
this.$lzc$set_resource($0)
},"resourceload",function($0){
if("resource" in $0){
this.resource=$0.resource;if(this.onresource.ready)this.onresource.sendEvent($0.resource)
};if(this.resourcewidth!=$0.width){
if("width" in $0){
this.resourcewidth=$0.width;if(this.onresourcewidth.ready)this.onresourcewidth.sendEvent($0.width)
};if(!this.hassetwidth&&this.resourcewidth!=this.width||this._setrescwidth&&this.unstretchedwidth!=this.resourcewidth){
this.updateWidth(this.resourcewidth)
}};if(this.resourceheight!=$0.height){
if("height" in $0){
this.resourceheight=$0.height;if(this.onresourceheight.ready)this.onresourceheight.sendEvent($0.height)
};if(!this.hassetheight&&this.resourceheight!=this.height||this._setrescheight&&this.unstretchedheight!=this.resourceheight){
this.updateHeight(this.resourceheight)
}};if($0.skiponload!=true){
if(this.onload.ready)this.onload.sendEvent(this)
}},"resourceloaderror",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.resourcewidth=0;this.resourceheight=0;if(this.onresourcewidth.ready)this.onresourcewidth.sendEvent(0);if(this.onresourceheight.ready)this.onresourceheight.sendEvent(0);this.reevaluateSize();if(this.onerror.ready)this.onerror.sendEvent($0)
},"resourceloadtimeout",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.resourcewidth=0;this.resourceheight=0;if(this.onresourcewidth.ready)this.onresourcewidth.sendEvent(0);if(this.onresourceheight.ready)this.onresourceheight.sendEvent(0);this.reevaluateSize();if(this.ontimeout.ready)this.ontimeout.sendEvent($0)
},"resourceevent",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=false;
case 3:
$3=false;

};var $4=$3==true||$2==true||this[$0]!=$1;if($2!=true)this[$0]=$1;if($4){
var $5=this["on"+$0];if($5.ready)$5.sendEvent($1)
}},"destroy",function(){
if(this.__LZdeleted)return;var $0=this.immediateparent;var $1=$0&&!$0.__LZdeleted;if($1){
if(this.sprite)this.sprite.predestroy();if(this.addedToParent){
var $2=$0.subviews;if($2!=null){
for(var $3=$2.length-1;$3>=0;$3--){
if($2[$3]==this){
$2.splice($3,1);break
}}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this);if(this.sprite){
this.sprite.destroy($1)
};if($1){
this.$lzc$set_visible(false);if(this.addedToParent){
if($0["__LZoutliewidth"]==this){
$0.__LZoutliewidth=null
};if($0["__LZoutlieheight"]==this){
$0.__LZoutlieheight=null
};if($0.onremovesubview.ready)$0.onremovesubview.sendEvent(this)
}}},"setVisible",function($0){
this.$lzc$set_visible($0)
},"setVisibility",function($0){
this.$lzc$set_visibility($0)
},"__LZupdateShown",function(){
if(this.visibility=="collapse"){
var $0=this.__LZvizO&&this.__LZvizDat&&this.__LZvizLoad
}else{
var $0=this.visibility=="visible"
};if($0!=this.visible){
this.visible=$0;if(this.sprite){
this.sprite.setVisible($0)
};var $1=this.immediateparent;if($1&&$1.__LZcheckwidth)$1.__LZcheckwidthFunction(this);if($1&&$1.__LZcheckheight)$1.__LZcheckheightFunction(this);if(this.onvisible.ready)this.onvisible.sendEvent($0)
}},"setWidth",function($0){
this.$lzc$set_width($0)
},"setHeight",function($0){
this.$lzc$set_height($0)
},"setOpacity",function($0){
this.$lzc$set_opacity($0)
},"setX",function($0){
this.$lzc$set_x($0)
},"setY",function($0){
this.$lzc$set_y($0)
},"setRotation",function($0){
this.$lzc$set_rotation($0)
},"setAlign",function($0){
this.$lzc$set_align($0)
},"__LZalignCenter",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.immediateparent;this.$lzc$set_x($1.width/2-this.width/2)
},"__LZalignRight",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.immediateparent;this.$lzc$set_x($1.width-this.width)
},"setXOffset",function($0){
this.$lzc$set_xoffset($0)
},"setYOffset",function($0){
this.$lzc$set_yoffset($0)
},"getBounds",function(){
var $0=this.width+this.__widthoffset;var $1=this.height+this.__heightoffset;var $2=[-this.xoffset,-this.yoffset,$0-this.xoffset,-this.yoffset,-this.xoffset,$1-this.yoffset,$0-this.xoffset,$1-this.yoffset,this.rotation,this.x,this.y];if(this.__LZstoredbounds){
var $3=$2.length-1;while($2[$3]==LzView.__LZlastmtrix[$3]){
if($3--==0){
return this.__LZstoredbounds
}}};var $4={};for(var $3=0;$3<8;$3+=2){
var $5=$2[$3];var $6=$2[$3+1];var $7=$5*this.__LZrcos-$6*this.__LZrsin;var $8=$5*this.__LZrsin+$6*this.__LZrcos;if($4.xoffset==null||$4.xoffset>$7){
$4.xoffset=$7
};if($4.yoffset==null||$4.yoffset>$8){
$4.yoffset=$8
};if($4.width==null||$4.width<$7){
$4.width=$7
};if($4.height==null||$4.height<$8){
$4.height=$8
}};$4.width-=$4.xoffset;$4.height-=$4.yoffset;$4.x=this.x+$4.xoffset;$4.y=this.y+$4.yoffset;this.__LZstoredbounds=$4;LzView.__LZlastmtrix=$2;return $4
},"$lzc$getBounds_dependencies",function($0,$1){
return [$1,"rotation",$1,"x",$1,"y",$1,"width",$1,"height"]
},"setValign",function($0){
this.$lzc$set_valign($0)
},"__LZvalignMiddle",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.immediateparent;this.$lzc$set_y($1.height/2-this.height/2)
},"__LZvalignBottom",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.immediateparent;this.$lzc$set_y($1.height-this.height)
},"setColor",function($0){
this.$lzc$set_fgcolor($0)
},"getColor",function(){
return this.sprite.getColor()
},"setColorTransform",function($0){
if(this.capabilities.colortransform){
this.sprite.setColorTransform($0);var $1="rgba("+$0.rb+","+$0.gb+","+$0.bb+","+$0.ab/255+")";this.tintcolor=LzColorUtils.inttohex(LzColorUtils.fromrgb($1));if(this.ontintcolor.ready)this.ontintcolor.sendEvent(this.tintcolor)
}},"getColorTransform",function(){
if(this.capabilities.colortransform){
return this.sprite.getColorTransform()
}},"__LZcheckSize",function($0,$1,$2){
if($0.addedToParent){
if($0.usegetbounds){
var $3=$0.getBounds()
}else{
var $3=$0
};var $4=$3[$2]+$3[$1];var $5=this["_setresc"+$1]?this["unstretched"+$1]:this[$1];if($4>$5&&$0.visible){
this["__LZoutlie"+$1]=$0;if($1=="width"){
this.updateWidth($4)
}else this.updateHeight($4)
}else if(this["__LZoutlie"+$1]==$0&&($4<$5||!$0.visible)){
this.reevaluateSize($1)
}}},"__LZcheckwidthFunction",function($0){
this.__LZcheckSize($0,"width","x")
},"__LZcheckheightFunction",function($0){
this.__LZcheckSize($0,"height","y")
},"measureSize",function($0){
var $1=this["resource"+$0];for(var $2=this.subviews.length-1;$2>=0;$2--){
var $3=this.subviews[$2];if($3.visible){
if($3.usegetbounds){
var $4=$3.getBounds()
}else{
var $4=$3
};var $5=$4[$0=="width"?"x":"y"]+$4[$0];if($5>$1){
$1=$5
}}};return $1
},"measureWidth",function(){
return this.measureSize("width")
},"measureHeight",function(){
return this.measureSize("height")
},"updateSize",function($0,$1){
if($0=="width"){
this.updateWidth($1)
}else this.updateHeight($1)
},"updateWidth",function($0){
if(this._setrescwidth){
this.unstretchedwidth=$0;if(this.hassetwidth){
var $1=this.width/$0;this._xscale=$1
};if(this.onunstretchedwidth.ready)this.onunstretchedwidth.sendEvent($0)
};if(!this.hassetwidth){
this.width=$0;this.sprite.setWidth($0);if(this.onwidth.ready)this.onwidth.sendEvent($0);var $2=this.immediateparent;if($2.__LZcheckwidth)$2.__LZcheckwidthFunction(this)
}},"updateHeight",function($0){
if(this._setrescheight){
this.unstretchedheight=$0;if(this.hassetheight){
var $1=this.height/$0;this._yscale=$1
};if(this.onunstretchedheight)if(this.onunstretchedheight.ready)this.onunstretchedheight.sendEvent($0)
};if(!this.hassetheight){
this.height=$0;this.sprite.setHeight($0);if(this.onheight.ready)this.onheight.sendEvent($0);var $2=this.immediateparent;if($2.__LZcheckheight)$2.__LZcheckheightFunction(this)
}},"reevaluateSize",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0==null){
var $1="height";this.reevaluateSize("width")
}else{
var $1=$0
};if(this["hasset"+$1]&&!this["_setresc"+$1])return;var $2=this["_setresc"+$1]?this["unstretched"+$1]:this[$1];var $3=this["resource"+$1]||0;this["__LZoutlie"+$1]=this;for(var $4=this.subviews.length-1;$4>=0;$4--){
var $5=this.subviews[$4];if($5.usegetbounds){
var $6=$5.getBounds();var $7=$6[$1=="width"?"x":"y"]+$6[$1]
}else{
var $7=$5[$1=="width"?"x":"y"]+$5[$1]
};if($5.visible&&$7>$3){
$3=$7;this["__LZoutlie"+$1]=$5
}};if($2!=$3){
if($1=="width"){
this.updateWidth($3)
}else this.updateHeight($3)
}},"updateResourceSize",function(){
this.sprite.updateResourceSize();this.reevaluateSize()
},"setAttributeRelative",function($0,$1){
var $2=this.getLinkage($1);var $3=$1[$0];if($0=="x"||$0=="y"){
$2.update($0);{
var $lzsc$1090607404=($3-$2.offset[$0])/$2.scale[$0];if(!this.__LZdeleted){
var $lzsc$1192000904="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$1192000904]):this[$lzsc$1192000904] instanceof Function){
this[$lzsc$1192000904]($lzsc$1090607404)
}else{
this[$0]=$lzsc$1090607404;var $lzsc$878085648=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$878085648):$lzsc$878085648 instanceof LzEvent){
if($lzsc$878085648.ready){
$lzsc$878085648.sendEvent($lzsc$1090607404)
}}}}}}else if($0=="width"||$0=="height"){
var $4=$0=="width"?"x":"y";$2.update($4);{
var $lzsc$1913971610=$3/$2.scale[$4];if(!this.__LZdeleted){
var $lzsc$994261949="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$lzsc$994261949]):this[$lzsc$994261949] instanceof Function){
this[$lzsc$994261949]($lzsc$1913971610)
}else{
this[$0]=$lzsc$1913971610;var $lzsc$899119293=this["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$899119293):$lzsc$899119293 instanceof LzEvent){
if($lzsc$899119293.ready){
$lzsc$899119293.sendEvent($lzsc$1913971610)
}}}}}}},"$lzc$setAttributeRelative_dependencies",function($0,$1,$2,$3){
var $4=$0.getLinkage($3);var $5=2;var $6=[];if($2=="width"){
var $7="x"
}else if($2=="height"){
var $7="y"
}else{
var $7=$2
};var $8=$7=="x"?"width":"height";while($5){
if($5==2){
var $9=$4.uplinkArray
}else{
var $9=$4.downlinkArray
};$5--;for(var $a=$9.length-1;$a>=0;$a--){
$6.push($9[$a],$7);if($6["_setresc"+$8]){
$6.push([$9[$a],$8])
}}};return $6
},"getAttributeRelative",function($0,$1){
var $2=this.getLinkage($1);if($0=="x"||$0=="y"){
$2.update($0);return $2.offset[$0]+$2.scale[$0]*this[$0]
}else if($0=="width"||$0=="height"){
var $3=$0=="width"?"x":"y";$2.update($3);return $2.scale[$3]*this[$0]
}},"$lzc$getAttributeRelative_dependencies",function($0,$1,$2,$3){
var $4=$1.getLinkage($3);var $5=2;var $6=[$1,$2];if($2=="width"){
var $7="x"
}else if($2=="height"){
var $7="y"
}else{
var $7=$2
};var $8=$7=="x"?"width":"height";while($5){
if($5==2){
var $9=$4.uplinkArray
}else{
var $9=$4.downlinkArray
};$5--;for(var $a=$9.length-1;$a>=0;$a--){
var $b=$9[$a];$6.push($b,$7);if($b["_setresc"+$8]){
$6.push($b,$8)
}}};return $6
},"__LZviewLinks",null,"getLinkage",function($0){
if(this.__LZviewLinks==null){
this.__LZviewLinks=new Object()
};var $1=$0.getUID();if(this.__LZviewLinks[$1]==null){
this.__LZviewLinks[$1]=new LzViewLinkage(this,$0)
};return this.__LZviewLinks[$1]
},"mouseevent",function($0){
if(this[$0]&&this[$0].ready)this[$0].sendEvent(this)
},"getMouse",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.__mousecache==null||this.__movecounter!==lz.GlobalMouse.__movecounter){
this.__movecounter=lz.GlobalMouse.__movecounter;this.__mousecache=this.sprite.getMouse()
};if($0==null)return this.__mousecache;return this.__mousecache[$0]
},"$lzc$getMouse_dependencies",function(){
var $0=Array.prototype.slice.call(arguments,0);return [lz.Idle,"idle"]
},"containsPt",function($0,$1){
var $2=0;var $3=0;var $4=this;do{
if(!$4.visible)return false;if($4.masked||$4===this){
var $5=$0-$2;var $6=$1-$3;if($5<0||$5>=$4.width||$6<0||$6>=$4.height){
return false
}};$2-=$4.x;$3-=$4.y;$4=$4.immediateparent
}while($4!==canvas);return true
},"bringToFront",function(){
if(!this.sprite){
return
};this.sprite.bringToFront()
},"getDepthList",function(){
var $0=this.subviews.concat();$0.sort(this.__zCompare);return $0
},"__zCompare",function($0,$1){
var $2=$0.sprite.getZ();var $3=$1.sprite.getZ();if($2<$3)return -1;if($2>$3)return 1;return 0
},"sendBehind",function($0){
if($0===this)return;return $0?this.sprite.sendBehind($0.sprite):false
},"sendInFrontOf",function($0){
if($0===this)return;return $0?this.sprite.sendInFrontOf($0.sprite):false
},"sendToBack",function(){
this.sprite.sendToBack()
},"setResourceNumber",function($0){
this.$lzc$set_frame($0)
},"stretchResource",function($0){
this.$lzc$set_stretches($0)
},"setBGColor",function($0){
this.$lzc$set_bgcolor($0)
},"setSource",function($0,$1,$2,$3){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};this.sprite.setSource($0,$1,$2,$3)
},"unload",function(){
this._resource=null;this.sprite.unload()
},"makeMasked",function(){
this.sprite.setClip(true);this.masked=true;this.mask=this
},"removeMask",function(){
this.sprite.setClip(false);this.masked=false;this.mask=null
},"setClickable",function($0){
this.$lzc$set_clickable($0)
},"$lzc$set_cursor",function($0){
this.sprite.setCursor($0)
},"setCursor",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.$lzc$set_cursor($0)
},"$lzc$set_play",function($0){
if($0){
this.play()
}else{
this.stop()
}},"setPlay",function($0){
this.$lzc$set_play($0)
},"getMCRef",function(){
return this.getDisplayObject()
},"getDisplayObject",function(){
return this.sprite.getDisplayObject()
},"play",function($0,$1){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=false;

};this.sprite.play($0,$1)
},"stop",function($0,$1){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=false;

};this.sprite.stop($0,$1)
},"setVolume",function($0){
if(this.capabilities.audio){
this.sprite.setVolume($0)
}},"getVolume",function(){
if(this.capabilities.audio){
return this.sprite.getVolume()
};return NaN
},"setPan",function($0){
if(this.capabilities.audio){
this.sprite.setPan($0)
}},"getPan",function(){
if(this.capabilities.audio){
return this.sprite.getPan()
};return NaN
},"getZ",function(){
return this.sprite.getZ()
},"seek",function($0){
if(this.capabilities.audio){
if(this.sprite.isaudio){
this.sprite.seek($0,this.playing);return
}};var $1=$0*canvas.framerate;if(this.playing){
this.play($1,true)
}else{
this.stop($1,true)
}},"getCurrentTime",function(){
if(this.capabilities.audio){
if(this.sprite.isaudio){
return this.sprite.getCurrentTime()
}};return this.frame/canvas.framerate
},"$lzc$getCurrentTime_dependencies",function($0,$1){
return [$1,"frame"]
},"getTotalTime",function(){
if(this.capabilities.audio){
if(this.sprite.isaudio){
return this.sprite.getTotalTime()
}};return this.totalframes/canvas.framerate
},"$lzc$getTotalTime_dependencies",function($0,$1){
return [$1,"load"]
},"getID3",function(){
if(this.capabilities.audio){
if(this.sprite.isaudio){
return this.sprite.getID3()
}};return null
},"setShowHandCursor",function($0){
this.$lzc$set_showhandcursor($0)
},"setAccessible",function($0){
if(this.capabilities.accessibility){
this.sprite.setAccessible($0)
}},"setAAActive",function($0){
this.$lzc$set_aaactive($0)
},"setAAName",function($0){
this.$lzc$set_aaname($0)
},"setAADescription",function($0){
this.$lzc$set_aadescription($0)
},"setAATabIndex",function($0){
this.$lzc$set_aatabindex($0)
},"setAASilent",function($0){
this.$lzc$set_aasilent($0)
},"shouldYieldFocus",function(){
return true
},"blurring",false,"getProxyURL",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.proxyurl;if($1==null){
return null
}else if(typeof $1=="string"){
return $1
}else if(typeof $1=="function"){
return $1($0)
}},"__LZcheckProxyPolicy",function($0){
if(this.__proxypolicy!=null){
return this.__proxypolicy($0)
};var $1=LzView.__LZproxypolicies;for(var $2=$1.length-1;$2>=0;$2--){
var $3=$1[$2]($0);if($3!=null)return $3
};return canvas.proxied
},"setProxyPolicy",function($0){
this.__proxypolicy=$0
},"__proxypolicy",null,"setProxyURL",function($0){
this.$lzc$set_proxyurl($0)
},"proxyurl",function($0){
return canvas.getProxyURL($0)
},"$lzc$set_proxyurl",function($0){
this.proxyurl=$0
},"contextmenu",null,"$lzc$set_contextmenu",function($0){
this.contextmenu=$0;this.sprite.setContextMenu($0)
},"setContextMenu",function($0){
this.$lzc$set_contextmenu($0)
},"getContextMenu",function(){
return this.contextmenu
},"getNextSelection",function(){},"getPrevSelection",function(){},"cachebitmap",false,"$lzc$set_cachebitmap",function($0){
if($0!=this.cachebitmap){
this.cachebitmap=$0;if(this.capabilities.bitmapcaching){
this.sprite.setBitmapCache($0)
}}},"oncontext",LzDeclaredEvent,"context",null,"$lzc$set_context",function($0){
this.context=$0;if(this.oncontext.ready){
this.oncontext.sendEvent($0)
}},"createContext",function(){
if(this.capabilities["2dcanvas"]){
this.sprite.setContextCallback(this,"$lzc$set_context");var $0=this.sprite.getContext();if($0){
{
if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_context"]):this["$lzc$set_context"] instanceof Function){
this["$lzc$set_context"]($0)
}else{
this["context"]=$0;var $lzsc$2084795461=this["oncontext"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$2084795461):$lzsc$2084795461 instanceof LzEvent){
if($lzsc$2084795461.ready){
$lzsc$2084795461.sendEvent($0)
}}}}}}}},"onshadowangle",LzDeclaredEvent,"shadowangle",0,"$lzc$set_shadowangle",function($0){
if(this.capabilities["dropshadows"]){
this.shadowangle=$0;this.__updateShadow();if(this.onshadowangle.ready){
this.onshadowangle.sendEvent($0)
}}},"onshadowdistance",LzDeclaredEvent,"shadowdistance",10,"$lzc$set_shadowdistance",function($0){
if(this.capabilities["dropshadows"]){
this.shadowdistance=$0;this.__updateShadow();if(this.onshadowdistance.ready){
this.onshadowdistance.sendEvent($0)
}}},"onshadowcolor",LzDeclaredEvent,"shadowcolor",0,"$lzc$set_shadowcolor",function($0){
if(this.capabilities["dropshadows"]){
this.shadowcolor=LzColorUtils.torgb($0);this.__updateShadow();if(this.onshadowcolor.ready){
this.onshadowcolor.sendEvent($0)
}}},"onshadowblurradius",LzDeclaredEvent,"shadowblurradius",4,"$lzc$set_shadowblurradius",function($0){
if(this.capabilities["dropshadows"]){
this.shadowblurradius=$0;this.__updateShadow();if(this.onshadowblurradius.ready){
this.onshadowblurradius.sendEvent($0)
}}},"__updateShadow",function(){
if(!this.isinited){
this.__updateshadowoninit=true
}else{
this.sprite.updateShadow(this.shadowcolor,this.shadowdistance,this.shadowangle,this.shadowblurradius)
}},"ontintcolor",LzDeclaredEvent,"tintcolor","","$lzc$set_tintcolor",function($0){
if(this.capabilities.colortransform){
if($0==null||$0==""){
var $1={rb:0,gb:0,bb:0,ab:0}}else{
if(isNaN($0)){
var $0=this.acceptTypeValue("color",$0)
};var $0=LzColorUtils.inttohex($0);var $1={rb:parseInt($0.substring(1,3),16),gb:parseInt($0.substring(3,5),16),bb:parseInt($0.substring(5,7),16)};if($0.length>7){
$1.ab=parseInt($0.substring(7),16)
}};this.sprite.setColorTransform($1)
};this.tintcolor=$0;if(this.ontintcolor.ready)this.ontintcolor.sendEvent($0)
},"oncornerradius",LzDeclaredEvent,"cornerradius",4,"$lzc$set_cornerradius",function($0){
if(this.capabilities["cornerradius"]){
this.sprite.setCornerRadius($0)
};this.cornerradius=$0;if(this.oncornerradius.ready)this.oncornerradius.sendEvent($0)
},"isMouseOver",function(){
var $0=this.getMouse();return this.containsPt($0.x,$0.y)
},"$lzc$isMouseOver_dependencies",function(){
var $0=Array.prototype.slice.call(arguments,0);return [lz.Idle,"idle"]
},"isInFrontOf",function($0){
if(!$0||$0.parent!==this.parent)return null;return this.sprite.getZ()>$0.sprite.getZ()
},"isBehind",function($0){
if(!$0||$0.parent!==this.parent)return null;return this.sprite.getZ()<$0.sprite.getZ()
},"__widthoffset",0,"__heightoffset",0,"__styleinfo",{},"setCSS",function($0,$1){
var $2=this.__styleinfo[$0];if(!$2){
return
};if(this.capabilities[$2.capability]){
this.sprite.setCSS($0,$1,$2.isdimension)
};if($2.affectsoffset){
this.__LZhasoffset=this.xoffset!=0||this.yoffset!=0||this.__widthoffset!=0||this.__heightoffset!=0;this.usegetbounds=this.__LZhasoffset||this.rotation!=0;var $3=this.immediateparent;if($3.__LZcheckwidth)$3.__LZcheckwidthFunction(this);if($3.__LZcheckheight)$3.__LZcheckheightFunction(this)
}},"usegetbounds",false],["tagname","view","attributes",new LzInheritedHash(LzNode.attributes),"__LZdelayedSetters",new LzInheritedHash(LzNode.__LZdelayedSetters),"earlySetters",new LzInheritedHash(LzNode.earlySetters),"__LZlastmtrix",[0,0,0,0,0,0,0,0,0,0,0],"__LZproxypolicies",[],"addProxyPolicy",function($0){
LzView.__LZproxypolicies.push($0)
},"removeProxyPolicy",function($0){
var $1=LzView.__LZproxypolicies;for(var $2=0;$2<$1.length;$2++){
if($1[$2]==$0){
LzView.__LZproxypolicies=$1.splice($2,1);return true
}};return false
},"__warnCapability",function($0,$1){
switch(arguments.length){
case 1:
$1="";

}}]);(function($0){
with($0)with($0.prototype){
{
LzView.__LZdelayedSetters.layout="$lzc$set_layout";LzView.earlySetters.clickregion=7;LzView.earlySetters.stretches=8
}}})(LzView);lz[LzView.tagname]=LzView;Class.make("LzText",[LzFormatter,LzView],["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"maxlines",1,"selectable",false,"onselectable",LzDeclaredEvent,"$lzc$set_selectable",function($0){
this.selectable=$0;this.tsprite.setSelectable($0);if(this.onselectable.ready)this.onselectable.sendEvent($0)
},"antiAliasType","advanced","$lzc$set_antiAliasType",function($0){
if(this.capabilities.advancedfonts){
if($0=="normal"||$0=="advanced"){
this.antiAliasType=$0;this.tsprite.setAntiAliasType($0)
}}},"gridFit","pixel","$lzc$set_gridFit",function($0){
if(this.capabilities.advancedfonts){
if($0=="none"||$0=="pixel"||$0=="subpixel"){
this.gridFit=$0;this.tsprite.setGridFit($0)
}}},"sharpness",0,"$lzc$set_sharpness",function($0){
if(this.capabilities.advancedfonts){
if($0>=-400&&$0<=400){
this.sharpness=$0;this.tsprite.setSharpness($0)
}}},"thickness",0,"$lzc$set_thickness",function($0){
if(this.capabilities.advancedfonts){
if($0>=-200&&$0<=200){
this.thickness=$0;this.tsprite.setThickness($0)
}}},"$lzc$set_clip",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_clip"]||this.nextMethod(arguments.callee,"$lzc$set_clip")).call(this,$0);if(this.isinited&&this.scrollevents&&!this.clip){
this.$lzc$set_scrollevents(false)
}},"$lzc$set_width",function($0){
var $1=this.tsprite;$1.setWidth($0);(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_width"]||this.nextMethod(arguments.callee,"$lzc$set_width")).call(this,$0);if(this.scrollwidth<this.width){
this.scrollwidth=this.width
};this.updateAttribute("maxhscroll",this.scrollwidth-this.width);if(!this.hassetheight){
var $2=$1.getTextfieldHeight();if($2>0&&$2!=this.height){
this.$lzc$set_height($2);this.hassetheight=false
}}},"getDefaultWidth",function(){
return 0
},"updateAttribute",function($0,$1){
this[$0]=$1;var $2=this["on"+$0];if($2.ready){
$2.sendEvent($1)
}},"updateLineAttribute",function($0,$1){
var $2=this.tsprite;var $3;if(this.capabilities.linescrolling){
$3=$2.pixelToLineNo($1)
}else{
$3=Math.ceil($1/this.lineheight)+1
};this.updateAttribute($0,$3)
},"lineheight",0,"$lzc$set_lineheight",function($0){},"onlineheight",LzDeclaredEvent,"scrollevents",false,"$lzc$set_scrollevents",function($0){
this.scrollevents=$0;if(this.isinited&&this.scrollevents&&!this.clip){};this.tsprite.setScrollEvents($0);if(this.onscrollevents.ready)this.onscrollevents.sendEvent($0)
},"onscrollevents",LzDeclaredEvent,"yscroll",0,"$lzc$set_yscroll",function($0){
if($0>0){
$0=0
};this.tsprite.setYScroll($0);this.updateAttribute("yscroll",$0);this.updateLineAttribute("scroll",-$0)
},"onyscroll",LzDeclaredEvent,"scrollheight",0,"$lzc$set_scrollheight",function($0){},"onscrollheight",LzDeclaredEvent,"xscroll",0,"$lzc$set_xscroll",function($0){
if($0>0){
$0=0
};this.tsprite.setXScroll($0);this.updateAttribute("xscroll",$0);this.updateAttribute("hscroll",-$0)
},"onxscroll",LzDeclaredEvent,"scrollwidth",0,"$lzc$set_scrollwidth",function($0){},"onscrollwidth",LzDeclaredEvent,"scroll",1,"$lzc$set_scroll",function($0){
if($0<1||$0>this.maxscroll){
$0=$0<1?1:this.maxscroll
};var $1=this.tsprite;var $2;if(this.capabilities.linescrolling){
$2=$1.lineNoToPixel($0)
}else{
$2=($0-1)*this.lineheight
};this.$lzc$set_yscroll(-$2)
},"onscroll",LzDeclaredEvent,"maxscroll",1,"$lzc$set_maxscroll",function($0){},"onmaxscroll",LzDeclaredEvent,"hscroll",0,"$lzc$set_hscroll",function($0){
if($0<0||$0>this.maxhscroll){
$0=$0<1?1:this.maxhscroll
};this.$lzc$set_xscroll(-$0)
},"onhscroll",LzDeclaredEvent,"maxhscroll",0,"$lzc$set_maxhscroll",function($0){},"onmaxhscroll",LzDeclaredEvent,"scrollevent",function($0,$1){
switch($0){
case "scrollTop":
this.updateAttribute("yscroll",-$1);this.updateLineAttribute("scroll",$1);break;
case "scrollLeft":
this.updateAttribute("xscroll",-$1);this.updateAttribute("hscroll",$1);break;
case "scrollHeight":
this.updateAttribute("scrollheight",$1);this.updateLineAttribute("maxscroll",Math.max(0,$1-this.height));break;
case "scrollWidth":
this.updateAttribute("scrollwidth",$1);this.updateAttribute("maxhscroll",Math.max(0,$1-this.width));break;
case "lineHeight":
this.updateAttribute("lineheight",$1);if(this.inited){
this.updateLineAttribute("scroll",-this.yscroll)
}break;
default:


}},"multiline",void 0,"$lzc$set_multiline",function($0){
this.multiline=$0=!(!$0);this.tsprite.setMultiline($0);this._updateSize()
},"resize",true,"$lzc$set_resize",function($0){
this.resize=$0;this.tsprite.setResize($0);this._updateSize()
},"text","","$lzc$set_text",function($0){
$0=String($0);if($0==this.getText()){
if(this.ontext.ready)this.ontext.sendEvent($0);return
};var $1=this.tsprite;if(this.visible)$1.setVisible(this.visible);if($0.length>this.maxlength){
$0=$0.substring(0,this.maxlength)
};$1.setText($0);this.text=$0;this._updateSize();if(this.ontext.ready)this.ontext.sendEvent($0)
},"_updateSize",function(){
if(!this.isinited){
return
};if(this.width==0||this.resize&&this.multiline==false){
var $0=this.getTextWidth();if($0!=this.width){
this.$lzc$set_width($0)
}};if(!this.hassetheight){
var $1=this.tsprite.getTextfieldHeight();if($1>0&&$1!=this.height){
this.$lzc$set_height($1);this.hassetheight=false
}}},"ontext",LzDeclaredEvent,"ontextlink",LzDeclaredEvent,"maxlength",Infinity,"$lzc$set_maxlength",function($0){
if($0==null){
$0=Infinity
};if(isNaN($0)){
return
};this.maxlength=$0;this.tsprite.setMaxLength($0);if(this.onmaxlength.ready)this.onmaxlength.sendEvent($0);var $1=this.getText();if($1&&$1.length>this.maxlength){
this._updateSize()
}},"onmaxlength",LzDeclaredEvent,"pattern",void 0,"$lzc$set_pattern",function($0){
if($0==null||$0=="")return;this.pattern=$0;this.tsprite.setPattern($0);if(this.onpattern.ready)this.onpattern.sendEvent($0)
},"onpattern",LzDeclaredEvent,"$lzc$set_fontstyle",function($0){
if($0=="plain"||$0=="bold"||$0=="italic"||$0=="bolditalic"||$0=="bold italic"){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_fontstyle"]||this.nextMethod(arguments.callee,"$lzc$set_fontstyle")).call(this,$0);this.tsprite.setFontStyle($0);this._updateSize()
}},"$lzc$set_font",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_font"]||this.nextMethod(arguments.callee,"$lzc$set_font")).call(this,$0);this.tsprite.setFontName($0);this._updateSize()
},"$lzc$set_fontsize",function($0){
if($0<=0||isNaN($0)){

}else{
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_fontsize"]||this.nextMethod(arguments.callee,"$lzc$set_fontsize")).call(this,$0);this.tsprite.setFontSize($0);this._updateSize()
}},"textalign","left","$lzc$set_textalign",function($0){
$0=$0?$0.toLowerCase():"left";if(!($0=="left"||$0=="right"||$0=="center"||$0=="justify")){
$0="left"
};this.textalign=$0;this.tsprite.setTextAlign($0);this._updateSize()
},"textindent",0,"$lzc$set_textindent",function($0){
if($0<0||isNaN($0)){

}else{
this.textindent=$0;this.tsprite.setTextIndent($0);this._updateSize()
}},"letterspacing",0,"$lzc$set_letterspacing",function($0){
if($0<0||isNaN($0)){

}else{
this.letterspacing=$0;this.tsprite.setLetterSpacing($0);this._updateSize()
}},"textdecoration","none","$lzc$set_textdecoration",function($0){
$0=$0?$0.toLowerCase():"none";if(!($0=="none"||$0=="underline")){
$0="none"
};this.textdecoration=$0;this.tsprite.setTextDecoration($0);this._updateSize()
},"construct",function($0,$1){
this.multiline=("multiline" in $1)?$1.multiline:null;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);for(var $2 in LzText.fontArgToAttr){
var $3=LzText.fontArgToAttr[$2];if(!($2 in $1)){
$1[$2]=this.searchParents($3)[$3]
};this[$3]=$1[$2]
};if(!("fgcolor" in $1)){
var $4=this;do{
$4=$4.immediateparent;var $5=$4["hasfgcolor"];if($5!=null&&$5){
$1["fgcolor"]=$4["fgcolor"];break
}}while($4!=canvas)
};var $6=this.tsprite;$6.__initTextProperties($1);for(var $2 in LzText.fontArgToAttr){
delete $1[$2]
};this.yscroll=0;this.xscroll=0;this.resize=("resize" in $1)?!(!$1.resize):this.resize;this.$lzc$set_resize(this.resize);if($1["maxlength"]!=null){
this.$lzc$set_maxlength($1.maxlength)
};this.text=$1["text"]!=null?String($1.text):"";if(this.text.length>this.maxlength){
this.text=this.text.substring(0,this.maxlength)
};this.$lzc$set_multiline(this.multiline);$6.setText(this.text);if(!this.hassetwidth){
if(this.multiline){
$1.width=this.parent.width
}else{
if(this.text!=null&&this.text!=""&&this.text.length>0){
$1.width=this.getTextWidth()
}else{
$1.width=this.getDefaultWidth()
}}}else{
this.$lzc$set_resize(false)
};if(this.hassetheight){
if($1["height"]!=null){
this.$lzc$set_height($1.height)
}};if($1["pattern"]!=null){
this.$lzc$set_pattern($1.pattern)
};if(this.capabilities.advancedfonts){
if(!("antiAliasType" in $1)){
this.$lzc$set_antiAliasType("advanced")
};if(!("gridFit" in $1)){
this.$lzc$set_gridFit("subpixel")
}};this._updateSize()
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(this.scrollevents&&!this.clip){};this._updateSize()
},"tsprite",void 0,"__makeSprite",function($0){
this.sprite=this.tsprite=new LzTextSprite(this,$0)
},"setResize",function($0){
this.$lzc$set_resize($0)
},"addText",function($0){
this.$lzc$set_text(this.getText()+$0)
},"clearText",function(){
this.$lzc$set_text("")
},"setMaxLength",function($0){
this.$lzc$set_maxlength($0)
},"setPattern",function($0){
this.$lzc$set_pattern($0)
},"getTextWidth",function(){
return this.tsprite.getTextWidth()
},"$lzc$getTextWidth_dependencies",function($0,$1){
return [$1,"text"]
},"getTextHeight",function(){
return this.tsprite.getTextfieldHeight()
},"$lzc$getTextHeight_dependencies",function($0,$1){
return [$1,"text"]
},"applyData",function($0){
if(null==$0){
this.clearText()
}else{
this.$lzc$set_text($0)
}},"setScroll",function($0){
this.$lzc$set_scroll($0)
},"getScroll",function(){
return this.scroll
},"getMaxScroll",function(){
return this.maxscroll
},"$lzc$getMaxScroll_dependencies",function($0,$1){
return [$1,"maxscroll"]
},"getBottomScroll",function(){
return this.scroll+this.height/this.lineheight
},"setXScroll",function($0){
this.$lzc$set_xscroll($0)
},"setYScroll",function($0){
this.$lzc$set_yscroll($0)
},"setHScroll",function($0){
this.$lzc$set_hscroll($0)
},"annotateAAimg",function($0){
if(typeof $0=="undefined"){
return
};if($0.length==0){
return
};var $1="";var $2=0;var $3=0;var $4;var $5="<img ";while(true){
$4=$0.indexOf($5,$2);if($4<0){
$1+=$0.substring($2);break
};$1+=$0.substring($2,$4+$5.length);$2=$4+$5.length;var $6={};$3=$2+this.parseImgAttributes($6,$0.substring($2));$1+=$0.substring($2,$3+1);if($6["alt"]!=null){
var $7=$6["alt"];$1+="[image "+$7+"]"
};$2=$3+1
};return $1
},"parseImgAttributes",function($0,$1){
var $2;var $3=0;var $4="attrname";var $5="attrval";var $6="whitespace";var $7="whitespace2";var $8=$6;var $9=$1.length;var $a;var $b;var $c;for($2=0;$2<$9;$2++){
$3=$2;var $d=$1.charAt($2);if($d==">"){
break
};if($8==$6){
if($d!=" "){
$8=$4;$a=$d
}}else if($8==$4){
if($d==" "||$d=="="){
$8=$7
}else{
$a+=$d
}}else if($8==$7){
if($d==" "||$d=="="){
continue
}else{
$8=$5;$c=$d;$b=""
}}else if($8==$5){
if($d!=$c){
$b+=$d
}else{
$8=$6;$0[$a]=$b
}}};return $3
},"setText",function($0){
this.$lzc$set_text($0)
},"format",function($0){
var $1=Array.prototype.slice.call(arguments,1);this.$lzc$set_text(this.formatToString.apply(this,[$0].concat($1)).toString().toHTML())
},"addFormat",function($0){
var $1=Array.prototype.slice.call(arguments,1);this.$lzc$set_text(this.getText()+this.formatToString.apply(this,[$0].concat($1)).toString().toHTML())
},"updateMaxLines",function(){
var $0=Math.floor(this.height/(this.font.height-1));if($0!=this.maxlines){
this.maxlines=$0
}},"getText",function(){
return this.text
},"$lzc$getText_dependencies",function($0,$1){
return [$1,"text"]
},"escapeText",function($0){
var $1=$0==null?this.text:$0;var $2;for(var $3 in LzText.escapeChars){
while($1.indexOf($3)>-1){
$2=$1.indexOf($3);$1=$1.substring(0,$2)+LzText.escapeChars[$3]+$1.substring($2+1)
}};return $1
},"setSelectable",function($0){
this.$lzc$set_selectable($0)
},"setFontSize",function($0){
this.$lzc$set_fontsize($0)
},"setFontStyle",function($0){
this.$lzc$set_fontstyle($0)
},"setMultiline",function($0){
this.$lzc$set_multiline($0)
},"setBorder",function($0){
this.tsprite.setBorder($0)
},"setWordWrap",function($0){
this.tsprite.setWordWrap($0)
},"setEmbedFonts",function($0){
this.tsprite.setEmbedFonts($0)
},"setAntiAliasType",function($0){
this.$lzc$set_antiAliasType($0)
},"getAntiAliasType",function(){
if(this.capabilities.advancedfonts){
return this.antiAliasType
}},"setGridFit",function($0){
this.$lzc$set_gridFit($0)
},"getGridFit",function(){
if(this.capabilities.advancedfonts){
return this.gridFit
}},"setSharpness",function($0){
this.$lzc$set_sharpness($0)
},"getSharpness",function(){
if(this.capabilities.advancedfonts){
return this.sharpness
}},"setThickness",function($0){
this.$lzc$set_thickness($0)
},"getThickness",function(){
if(this.capabilities.advancedfonts){
return this.thickness
}},"setSelection",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null){
$1=$0
};this.tsprite.setSelection($0,$1)
},"getSelectionPosition",function(){
return this.tsprite.getSelectionPosition()
},"getSelectionSize",function(){
return this.tsprite.getSelectionSize()
},"makeTextLink",function($0,$1){
return this.tsprite.makeTextLink($0,$1)
},"toString",function(){
return "LzText: "+this.getText()
}],["tagname","text","attributes",new LzInheritedHash(LzView.attributes),"fontArgToAttr",{font:"fontname",fontsize:"fontsize",fontstyle:"fontstyle"},"escapeChars",{">":"&gt;","<":"&lt;"}]);(function($0){
with($0)with($0.prototype){
{
LzText.attributes.pixellock=true;LzText.attributes.selectable=false
}}})(LzText);lz[LzText.tagname]=LzText;Class.make("LzInputText",LzText,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"password",void 0,"onenabled",LzDeclaredEvent,"getDefaultWidth",function(){
return 100
},"_onfocusDel",null,"_onblurDel",null,"_modemanagerDel",null,"construct",function($0,$1){
this.password=("password" in $1)?!(!$1.password):false;this.resize=("resize" in $1)?!(!$1.resize):false;this.focusable=true;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this._onfocusDel=new LzDelegate(this,"_gotFocusEvent",this,"onfocus");this._onblurDel=new LzDelegate(this,"_gotBlurEvent",this,"onblur");this._modemanagerDel=new LzDelegate(this,"_modechanged",lz.ModeManager,"onmode")
},"destroy",function(){
if(this._onfocusDel){
this._onfocusDel.unregisterAll();this._onfocusDel=null
};if(this._onblurDel){
this._onblurDel.unregisterAll();this._onblurDel=null
};if(this._modemanagerDel){
this._modemanagerDel.unregisterAll();this._modemanagerDel=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"isprite",void 0,"__makeSprite",function($0){
this.sprite=this.tsprite=this.isprite=new LzInputTextSprite(this,$0)
},"_focused",false,"_gotFocusEvent",function($0){
switch(arguments.length){
case 0:
$0=null;

};this._focused=true;var $1=this.sprite;$1.gotFocus()
},"_gotBlurEvent",function($0){
switch(arguments.length){
case 0:
$0=null;

};this._focused=false;var $1=this.sprite;$1.gotBlur()
},"inputtextevent",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($0=="onfocus"&&this._focused)return;if($0=="onblur"&&!this._focused)return;if($0=="onfocus"){
this._focused=true;if(lz.Focus.getFocus()!==this){
var $2=lz.Keys.isKeyDown("tab");lz.Focus.setFocus(this,$2)
}}else if($0=="onchange"){
var $3=this.sprite;this.text=$3.getText();if(this.multiline&&!this.hassetheight&&this.height!=$3.getTextfieldHeight()){
this.$lzc$set_height($3.getTextfieldHeight())
};if(this.ontext.ready)this.ontext.sendEvent($1)
}else if($0=="onblur"){
this._focused=false;if(lz.Focus.getFocus()===this){
lz.Focus.clearFocus()
}}},"updateData",function(){
var $0=this.sprite;return $0.getText()
},"enabled",true,"$lzc$set_enabled",function($0){
this.focusable=true;this.enabled=$0;var $1=this.sprite;$1.setEnabled($0);if(this.onenabled.ready)this.onenabled.sendEvent($0)
},"setEnabled",function($0){
this.$lzc$set_enabled($0)
},"setHTML",function($0){
if(this.capabilities["htmlinputtext"]){
var $1=this.sprite;$1.setHTML($0)
}},"getText",function(){
return this.sprite.getText()
},"_allowselectable",true,"_selectable",void 0,"_modechanged",function($0){
this._setallowselectable(!$0||lz.ModeManager.__LZallowInput($0,this))
},"_setallowselectable",function($0){
this._allowselectable=$0;this.$lzc$set_selectable(this._selectable)
},"$lzc$set_selectable",function($0){
this._selectable=$0;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_selectable"]||this.nextMethod(arguments.callee,"$lzc$set_selectable")).call(this,this._allowselectable?$0:false)
}],["tagname","inputtext","attributes",new LzInheritedHash(LzText.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({selectable:true,enabled:true,clip:true},LzInputText.attributes)
}}})(LzInputText);lz[LzInputText.tagname]=LzInputText;Class.make("LzViewLinkage",null,["scale",1,"offset",0,"uplinkArray",null,"downlinkArray",null,"$lzsc$initialize",function($0,$1){
this.scale=new Object();this.offset=new Object();if($0==$1){
return
};this.uplinkArray=[];var $2=$0;do{
$2=$2.immediateparent;this.uplinkArray.push($2)
}while($2!=$1&&$2!=canvas);this.downlinkArray=[];if($2==$1)return;var $2=$1;do{
$2=$2.immediateparent;this.downlinkArray.push($2)
}while($2!=canvas);while(this.uplinkArray.length>1&&this.downlinkArray[this.downlinkArray.length-1]==this.uplinkArray[this.uplinkArray.length-1]&&this.downlinkArray[this.downlinkArray.length-2]==this.uplinkArray[this.uplinkArray.length-2]){
this.downlinkArray.pop();this.uplinkArray.pop()
}},"update",function($0){
var $1=1;var $2=0;var $3="_"+$0+"scale";if(this.uplinkArray){
var $4=this.uplinkArray.length;for(var $5=0;$5<$4;$5++){
var $6=this.uplinkArray[$5];$1*=$6[$3];$2+=$6[$0]/$1
}};if(this.downlinkArray){
for(var $5=this.downlinkArray.length-1;$5>=0;$5--){
var $6=this.downlinkArray[$5];$2-=$6[$0]/$1;$1/=$6[$3]
}};this.scale[$0]=$1;this.offset[$0]=$2
}],null);Class.make("LzCanvas",LzView,["updatePercentCreatedEnabled",true,"resourcetable",void 0,"_lzinitialsubviews",[],"totalnodes",void 0,"framerate",30,"onframerate",LzDeclaredEvent,"creatednodes",void 0,"__LZproxied",void 0,"embedfonts",void 0,"lpsbuild",void 0,"lpsbuilddate",void 0,"appbuilddate",void 0,"runtime",void 0,"allowfullscreen",void 0,"fullscreen",void 0,"onfullscreen",LzDeclaredEvent,"__LZmouseupDel",void 0,"__LZmousedownDel",void 0,"__LZmousemoveDel",void 0,"__LZDefaultCanvasMenu",void 0,"httpdataprovider",null,"defaultdataprovider",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3);this.datasets={};this.__LZcheckwidth=null;this.__LZcheckheight=null;this.hassetwidth=true;this.hassetheight=true
},"construct",function($0,args){
var $2;$2=function($0,$1){
var $2=args[$0];delete args[$0];if($2!=null){
return !(!$2)
}else if($1!=null){
var $3=lz.Browser.getInitArg($1);if($3!=null){
return $3=="true"
}};return void 0
};this.__makeSprite(null);var $1=this.sprite.capabilities;this.capabilities=$1;this.immediateparent=this;this.datapath=new LzDatapath(this);this.mask=null;this.accessible=$2("accessible",null);if($1.accessibility==true){
this.sprite.setAccessible(this.accessible);if(this.accessible){
this.sprite.setAAActive(true);this.sprite.setAASilent(false)
}}else if(this.accessible){
this.accessible=false
};this.history=$2("history","history");if(this.history&&$1.history!=true){
this.history=false
};this.allowfullscreen=$2("allowfullscreen","allowfullscreen");if(this.allowfullscreen&&$1.allowfullscreen!=true){
this.allowfullscreen=false
};this.fullscreen=false;this.viewLevel=0;this.resourcetable={};this.totalnodes=0;this.creatednodes=0;this.percentcreated=0;if(!args.framerate){
args.framerate=30
};this.proxied=$2("proxied","lzproxied");if(this.proxied==null){
this.proxied=args.__LZproxied=="true"
};if(typeof args.proxyurl=="undefined"){
this.proxyurl=lz.Browser.getBaseURL().toString()
};if(args.focustrap!=null){
if($1.globalfocustrap!=true){
delete args.focustrap
}};LzScreenKernel.setCallback(this,"__windowResize");delete args.width;delete args.height;if(args["fgcolor"]!=null){
this.hasfgcolor=true
};this.lpsversion=args.lpsversion+"."+this.__LZlfcversion;delete args.lpsversion;this.__LZdelayedSetters=LzView.__LZdelayedSetters;this.earlySetters=LzView.earlySetters;if(!this.version){
this.version=this.lpsversion
};this.isinited=false;this._lzinitialsubviews=[];this.datasets={};global.canvas=this;this.parent=this;this.makeMasked();this.__LZmouseupDel=new LzDelegate(this,"__LZmouseup",lz.GlobalMouse,"onmouseup");this.__LZmousedownDel=new LzDelegate(this,"__LZmousedown",lz.GlobalMouse,"onmousedown");this.__LZmousemoveDel=new LzDelegate(this,"__LZmousemove",lz.GlobalMouse,"onmousemove");this.defaultdataprovider=this.httpdataprovider=new LzHTTPDataProvider();this.id=lz.Browser.getAppID()
},"__LZmouseup",function($0){
if(this.onmouseup.ready)this.onmouseup.sendEvent()
},"__LZmousemove",function($0){
if(this.onmousemove.ready)this.onmousemove.sendEvent()
},"__LZmousedown",function($0){
if(this.onmousedown.ready)this.onmousedown.sendEvent()
},"__makeSprite",function($0){
this.sprite=new LzSprite(this,true)
},"onmouseleave",LzDeclaredEvent,"onmouseenter",LzDeclaredEvent,"onpercentcreated",LzDeclaredEvent,"onmousemove",LzDeclaredEvent,"onafterinit",LzDeclaredEvent,"lpsversion",void 0,"lpsrelease",void 0,"version",null,"__LZlfcversion","0","proxied",true,"dataloadtimeout",30000,"medialoadtimeout",30000,"mediaerrortimeout",4500,"percentcreated",void 0,"datasets",null,"compareVersion",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null){
$1=this.lpsversion
};if($0==$1)return 0;var $2=$0.split(".");var $3=$1.split(".");var $4=0;while($4<$2.length||$4<$3.length){
var $5=Number($2[$4])||0;var $6=Number($3[$4++])||0;if($5<$6){
return -1
}else if($5>$6){
return 1
}};return 0
},"$lzc$set_resource",function($0){},"$lzc$set_focustrap",function($0){
lz.Keys.setGlobalFocusTrap($0)
},"toString",function(){
return "This is the canvas"
},"$lzc$set_framerate",function($0){
$0*=1;if($0<1){
$0=1
}else if($0>1000){
$0=1000
};this.framerate=$0;lz.Idle.setFrameRate($0);if(this.onframerate.ready)this.onframerate.sendEvent($0)
},"$lzc$set_fullscreen",function($0){
switch(arguments.length){
case 0:
$0=true;

};if(this.sprite.capabilities.allowfullscreen==true){
LzScreenKernel.showFullScreen($0)
}},"__fullscreenEventCallback",function($0,$1){
this.fullscreen=$1;this.onfullscreen.sendEvent($0)
},"__fullscreenErrorCallback",function($0){},"$lzc$set_allowfullscreen",function($0){
this.allowfullscreen=$0
},"initDone",function(){
var $0=[];var $1=[];var $2=this._lzinitialsubviews;for(var $3=0,$4=$2.length;$3<$4;++$3){
var $5=$2[$3];if($5["attrs"]&&$5.attrs["initimmediate"]){
$0.push($5)
}else{
$1.push($5)
}};$0.push.apply($0,$1);this._lzinitialsubviews=[];lz.Instantiator.requestInstantiation(this,$0)
},"init",function(){
this.sprite.init(true);if(this.history==true){
lz.History.__start(this.id)
};if(this.contextmenu==null){
this.buildDefaultMenu()
}},"deferInit",true,"__LZinstantiationDone",function(){
this.__LZinstantiated=true;if(this.deferInit){
this.deferInit=false;return
};this.percentcreated=1;this.updatePercentCreatedEnabled=false;if(this.onpercentcreated.ready)this.onpercentcreated.sendEvent(this.percentcreated);lz.Instantiator.resume();this.__LZcallInit()
},"updatePercentCreated",function(){
this.percentcreated=Math.max(this.percentcreated,this.creatednodes/this.totalnodes);this.percentcreated=Math.min(0.99,this.percentcreated);if(this.onpercentcreated.ready)this.onpercentcreated.sendEvent(this.percentcreated)
},"initiatorAddNode",function($0,$1){
this.totalnodes+=$1;this._lzinitialsubviews.push($0)
},"__LZcallInit",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.isinited)return;this.isinited=true;this.__LZresolveReferences();var $1=this.subnodes;if($1){
for(var $2=0;$2<$1.length;){
var $3=$1[$2++];var $4=$1[$2];if($3.isinited||!$3.__LZinstantiated)continue;$3.__LZcallInit();if($4!=$1[$2]){
while($2>0){
if($4==$1[--$2])break
}}}};this.init();if(this.oninit.ready)this.oninit.sendEvent(this);if(this.onafterinit.ready)this.onafterinit.sendEvent(this);if(this.datapath&&this.datapath.__LZApplyDataOnInit){
this.datapath.__LZApplyDataOnInit()
};this.inited=true;if(this.oninited.ready){
this.oninited.sendEvent(true)
}},"isProxied",function(){
return this.proxied
},"$lzc$set_width",function($0){
LzSprite.setRootWidth($0)
},"$lzc$set_x",function($0){
LzSprite.setRootX($0)
},"$lzc$set_height",function($0){
LzSprite.setRootHeight($0)
},"$lzc$set_y",function($0){
LzSprite.setRootY($0)
},"setDefaultContextMenu",function($0){
this.$lzc$set_contextmenu($0);this.sprite.setDefaultContextMenu($0)
},"buildDefaultMenu",function(){
this.__LZDefaultCanvasMenu=new LzContextMenu();this.__LZDefaultCanvasMenu.hideBuiltInItems();var $0=new LzContextMenuItem("About OpenLaszlo...",new LzDelegate(this,"__LZdefaultMenuItemHandler"));this.__LZDefaultCanvasMenu.addItem($0);if(this.proxied){
var $1=new LzContextMenuItem("View Source",new LzDelegate(this,"__LZviewSourceMenuItemHandler"));this.__LZDefaultCanvasMenu.addItem($1)
};this.setDefaultContextMenu(this.__LZDefaultCanvasMenu)
},"__LZdefaultMenuItemHandler",function($0){
lz.Browser.loadURL("http://www.openlaszlo.org","lz_about")
},"__LZviewSourceMenuItemHandler",function($0){
var $1=lz.Browser.getBaseURL()+"?lzt=source";lz.Browser.loadURL($1,"lz_source")
},"__windowResize",function($0){
this.width=$0.width;if(this.onwidth.ready)this.onwidth.sendEvent(this.width);this.sprite.setWidth(this.width);this.height=$0.height;if(this.onheight.ready)this.onheight.sendEvent(this.height);this.sprite.setHeight(this.height)
},"LzInstantiateView",function($0,$1){
switch(arguments.length){
case 1:
$1=1;

};canvas.initiatorAddNode($0,$1)
},"lzAddLocalData",function($0,$1,$2,$3){
switch(arguments.length){
case 3:
$3=false;

};return new LzDataset(canvas,{name:$0,initialdata:$1,trimwhitespace:$2,nsprefix:$3})
}],["tagname","canvas","attributes",new LzInheritedHash(LzView.attributes),"versionInfoString",function(){
return "URL: "+lz.Browser.getLoadURL()+"\n"+"LPS\n"+"  Version: "+canvas.lpsversion+"\n"+"  Release: "+canvas.lpsrelease+"\n"+"  Build: "+canvas.lpsbuild+"\n"+"  Date: "+canvas.lpsbuilddate+"\n"+"Application\n"+"  Date: "+canvas.appbuilddate+"\n"+"Target: "+canvas.runtime+"\n"+"Runtime: "+lz.Browser.getVersion()+"\n"+"OS: "+lz.Browser.getOS()+"\n"
}]);lz[LzCanvas.tagname]=LzCanvas;var canvas;Class.make("LzScript",LzNode,["src",void 0,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3);$1.script()
}],["tagname","script","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzScript.tagname]=LzScript;Class.make("LzAnimatorGroup",LzNode,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"updateDel",void 0,"crepeat",void 0,"startTime",void 0,"__LZpauseTime",void 0,"actAnim",void 0,"notstarted",void 0,"needsrestart",void 0,"attribute",void 0,"start",true,"from",void 0,"to",void 0,"duration",void 0,"indirect",false,"relative",false,"motion","easeboth","repeat",1,"$lzc$set_repeat",function($0){
if($0<=0){
$0=Infinity
};this.repeat=$0
},"paused",false,"$lzc$set_paused",function($0){
this.pause($0)
},"started",void 0,"target",void 0,"process","sequential","isactive",false,"ontarget",LzDeclaredEvent,"onduration",LzDeclaredEvent,"onstarted",LzDeclaredEvent,"onstart",LzDeclaredEvent,"onpaused",LzDeclaredEvent,"onstop",LzDeclaredEvent,"onrepeat",LzDeclaredEvent,"animatorProps",{attribute:true,from:true,duration:true,to:true,relative:true,target:true},"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);var $2=this.immediateparent;if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($2):$2 instanceof LzAnimatorGroup){
for(var $3 in this.animatorProps){
if($1[$3]==null){
$1[$3]=$2[$3]
}};if($2.animators==null){
$2.animators=[this]
}else{
$2.animators.push(this)
};$1.start=LzNode._ignoreAttribute
}else{
this.target=$2
};if(!this.updateDel)this.updateDel=new LzDelegate(this,"update")
},"init",function(){
if(!this.target)this.target=this.immediateparent;if(this.started)this.doStart();(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this)
},"$lzc$set_target",function($0){
this.target=$0;var $1=this.subnodes;if($1){
for(var $2=0;$2<$1.length;$2++){
if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($1[$2]):$1[$2] instanceof LzAnimatorGroup){
$1[$2].$lzc$set_target($0)
}}};if(this.ontarget.ready)this.ontarget.sendEvent($0)
},"setTarget",function($0){
this.$lzc$set_target($0)
},"$lzc$set_start",function($0){
this.started=$0;if(this.onstarted.ready)this.onstarted.sendEvent($0);if(!this.isinited){
return
};if($0){
this.doStart()
}else{
this.stop()
}},"doStart",function(){
if(this.isactive)return false;if(this.onstart.ready)this.onstart.sendEvent(new Date().getTime());this.isactive=true;this.prepareStart();this.updateDel.register(lz.Idle,"onidle");return true
},"prepareStart",function(){
this.crepeat=this.repeat;for(var $0=this.animators.length-1;$0>=0;$0--){
this.animators[$0].notstarted=true
};this.actAnim=this.animators.concat()
},"resetAnimator",function(){
this.actAnim=this.animators.concat();for(var $0=this.animators.length-1;$0>=0;$0--){
this.animators[$0].needsrestart=true
}},"update",function($0){
if(this.paused){
return false
};var $1=this.actAnim.length-1;if($1>0&&this.process=="sequential")$1=0;for(var $2=$1;$2>=0;$2--){
var $3=this.actAnim[$2];if($3.notstarted){
$3.isactive=true;$3.prepareStart();$3.notstarted=false
}else if($3.needsrestart){
$3.resetAnimator();$3.needsrestart=false
};if($3.update($0)){
this.actAnim.splice($2,1)
}};if(!this.actAnim.length){
return this.checkRepeat()
};return false
},"pause",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0==null){
$0=!this.paused
};if(this.paused&&!$0){
this.__LZaddToStartTime(new Date().getTime()-this.__LZpauseTime)
}else if(!this.paused&&$0){
this.__LZpauseTime=new Date().getTime()
};this.paused=$0;if(this.onpaused.ready)this.onpaused.sendEvent($0)
},"__LZaddToStartTime",function($0){
this.startTime+=$0;if(this.actAnim){
for(var $1=0;$1<this.actAnim.length;$1++){
this.actAnim[$1].__LZaddToStartTime($0)
}}},"stop",function(){
if(this.actAnim){
var $0=this.actAnim.length-1;if($0>0&&this.process=="sequential")$0=0;for(var $1=$0;$1>=0;$1--){
this.actAnim[$1].stop()
}};this.__LZhalt()
},"setDuration",function($0){
this.$lzc$set_duration($0)
},"$lzc$set_duration",function($0){
if(isNaN($0)){
$0=0
}else{
$0=Number($0)
};this.duration=$0;var $1=this.subnodes;if($1){
for(var $2=0;$2<$1.length;++$2){
if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($1[$2]):$1[$2] instanceof LzAnimatorGroup){
$1[$2].$lzc$set_duration($0)
}}};this.onduration.sendEvent($0)
},"__LZfinalizeAnim",function(){
this.__LZhalt()
},"__LZhalt",function(){
this.isactive=false;this.updateDel.unregisterAll();if(this.onstop.ready)this.onstop.sendEvent(new Date().getTime())
},"checkRepeat",function(){
if(this.crepeat==1){
this.__LZfinalizeAnim();return true
}else{
this.crepeat--;if(this.onrepeat.ready)this.onrepeat.sendEvent(new Date().getTime());this.resetAnimator();return false
}},"destroy",function(){
this.stop();this.updateDel.unregisterAll();this.animators=null;this.actAnim=null;var $0=this.immediateparent;var $1=$0.animators;if($1&&$1.length){
for(var $2=0;$2<$1.length;$2++){
if($1[$2]==this){
$1.splice($2,1);break
}};if(LzAnimatorGroup["$lzsc$isa"]?LzAnimatorGroup.$lzsc$isa($0):$0 instanceof LzAnimatorGroup){
var $3=$0.actAnim;if($3&&$3.length){
for(var $2=0;$2<$3.length;$2++){
if($3[$2]==this){
$3.splice($2,1);break
}}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"toString",function(){
if(this.animators){
return "Group of "+this.animators.length
};return "Empty group"
}],["tagname","animatorgroup","attributes",new LzInheritedHash(LzNode.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzAnimatorGroup.attributes.start=true;LzAnimatorGroup.attributes.ignoreplacement=true
}}})(LzAnimatorGroup);lz[LzAnimatorGroup.tagname]=LzAnimatorGroup;Class.make("LzAnimator",LzAnimatorGroup,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"calcMethod",void 0,"lastIterationTime",void 0,"currentValue",void 0,"doBegin",void 0,"beginPoleDelta",0.25,"endPoleDelta",0.25,"primary_K",void 0,"origto",void 0,"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.calcMethod=this.calcNextValue;this.primary_K=1
},"$lzc$set_motion",function($0){
this.motion=$0;if($0=="linear"){
this.calcMethod=this.calcNextValueLinear
}else{
this.calcMethod=this.calcNextValue;if($0=="easeout"){
this.beginPoleDelta=100
}else if($0=="easein"){
this.endPoleDelta=15
}}},"setMotion",function($0){
this.$lzc$set_motion($0)
},"$lzc$set_to",function($0){
this.origto=Number($0)
},"setTo",function($0){
this.$lzc$set_to($0)
},"calcControlValues",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.currentValue=$0||0;var $1=this.indirect?-1:1;if(this.currentValue<this.to){
this.beginPole=this.currentValue-$1*this.beginPoleDelta;this.endPole=this.to+$1*this.endPoleDelta
}else{
this.beginPole=this.currentValue+$1*this.beginPoleDelta;this.endPole=this.to-$1*this.endPoleDelta
};this.primary_K=1;var $2=1*(this.beginPole-this.to)*(this.currentValue-this.endPole);var $3=1*(this.beginPole-this.currentValue)*(this.to-this.endPole);if($3!=0)this.primary_K=Math.abs($2/$3)
},"doStart",function(){
if(this.isactive)return;this.isactive=true;this.prepareStart();this.updateDel.register(lz.Idle,"onidle")
},"prepareStart",function(){
this.crepeat=this.repeat;var $0=this.target;var $1=this.attribute;if(this.from!=null){
{
var $lzsc$336200393=Number(this.from);if(!$0.__LZdeleted){
var $lzsc$1657275236="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa($0[$lzsc$1657275236]):$0[$lzsc$1657275236] instanceof Function){
$0[$lzsc$1657275236]($lzsc$336200393)
}else{
$0[$1]=$lzsc$336200393;var $lzsc$1066131144=$0["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1066131144):$lzsc$1066131144 instanceof LzEvent){
if($lzsc$1066131144.ready){
$lzsc$1066131144.sendEvent($lzsc$336200393)
}}}}}};if(this.relative){
this.to=this.origto
}else{
this.to=this.origto-$0.getExpectedAttribute($1)
};$0.addToExpectedAttribute($1,this.to);$0.__LZincrementCounter($1);this.currentValue=0;this.calcControlValues();this.doBegin=true
},"resetAnimator",function(){
var $0=this.target;var $1=this.attribute;var $2=this.from;if($2!=null){
{
if(!$0.__LZdeleted){
var $lzsc$2012185567="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa($0[$lzsc$2012185567]):$0[$lzsc$2012185567] instanceof Function){
$0[$lzsc$2012185567]($2)
}else{
$0[$1]=$2;var $lzsc$1001130874=$0["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1001130874):$lzsc$1001130874 instanceof LzEvent){
if($lzsc$1001130874.ready){
$lzsc$1001130874.sendEvent($2)
}}}}};var $3=$2-$0.getExpectedAttribute($1);$0.addToExpectedAttribute($1,$3)
};if(!this.relative){
this.to=this.origto-$0.getExpectedAttribute($1);this.calcControlValues()
};$0.addToExpectedAttribute($1,this.to);$0.__LZincrementCounter($1);this.currentValue=0;this.doBegin=true
},"beginAnimator",function($0){
this.startTime=$0;this.lastIterationTime=$0;if(this.onstart.ready)this.onstart.sendEvent($0);this.doBegin=false
},"stop",function(){
if(!this.isactive)return;var $0=this.target;var $1="e_"+this.attribute;if(!$0[$1].c){
$0[$1].c=0
};$0[$1].c-=1;if($0[$1].c<=0){
$0[$1].c=0;$0[$1].v=null
}else{
$0[$1].v-=this.to-this.currentValue
};this.__LZhalt()
},"__LZfinalizeAnim",function(){
var $0=this.target;var $1=this.attribute;var $2="e_"+$1;if(!$0[$2].c){
$0[$2].c=0
};$0[$2].c-=1;if($0[$2].c<=0){
$0[$2].c=0;{
var $lzsc$254819999=$0[$2].v;if(!$0.__LZdeleted){
var $lzsc$177345327="$lzc$set_"+$1;if(Function["$lzsc$isa"]?Function.$lzsc$isa($0[$lzsc$177345327]):$0[$lzsc$177345327] instanceof Function){
$0[$lzsc$177345327]($lzsc$254819999)
}else{
$0[$1]=$lzsc$254819999;var $lzsc$1733413644=$0["on"+$1];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1733413644):$lzsc$1733413644 instanceof LzEvent){
if($lzsc$1733413644.ready){
$lzsc$1733413644.sendEvent($lzsc$254819999)
}}}}};$0[$2].v=null
};this.__LZhalt()
},"calcNextValue",function($0){
var $1=this.currentValue;var $2=this.endPole;var $3=this.beginPole;var $4=Math.exp($0*1/this.duration*Math.log(this.primary_K));if($4!=1){
var $5=$3*$2*(1-$4);var $6=$2-$4*$3;if($6!=0)$1=$5/$6
};return $1
},"calcNextValueLinear",function($0){
var $1=$0/this.duration;return $1*this.to
},"update",function($0){
if(this.doBegin){
this.beginAnimator($0)
}else{
if(!this.paused){
var $1=$0-this.startTime;if($1<this.duration){
this.setValue(this.calcMethod($1));this.lastIterationTime=$0
}else{
this.setValue(this.to);return this.checkRepeat()
}}};return false
},"setValue",function($0){
var $1=this.target;var $2=this.attribute;var $3=$0-this.currentValue;{
var $lzsc$1149244761=$1[$2]+$3;if(!$1.__LZdeleted){
var $lzsc$527823322="$lzc$set_"+$2;if(Function["$lzsc$isa"]?Function.$lzsc$isa($1[$lzsc$527823322]):$1[$lzsc$527823322] instanceof Function){
$1[$lzsc$527823322]($lzsc$1149244761)
}else{
$1[$2]=$lzsc$1149244761;var $lzsc$1411226834=$1["on"+$2];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1411226834):$lzsc$1411226834 instanceof LzEvent){
if($lzsc$1411226834.ready){
$lzsc$1411226834.sendEvent($lzsc$1149244761)
}}}}};this.currentValue=$0
},"toString",function(){
return "Animator for "+this.target+" attribute:"+this.attribute+" to:"+this.to
}],["tagname","animator","attributes",new LzInheritedHash(LzAnimatorGroup.attributes)]);lz[LzAnimator.tagname]=LzAnimator;Class.make("LzLayout",LzNode,["vip",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"initDelegate",void 0,"locked",2,"$lzc$set_locked",function($0){
if(this.locked==$0)return;if($0){
this.lock()
}else{
this.unlock()
}},"subviews",null,"updateDelegate",void 0,"delegates",void 0,"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).apply(this,arguments);this.subviews=new Array();this.vip=this.immediateparent;if(this.vip.layouts==null){
this.vip.layouts=[this]
}else{
this.vip.layouts.push(this)
};this.updateDelegate=new LzDelegate(this,"update");this.delegates=[this.updateDelegate];if(this.immediateparent.isinited){
this.__parentInit()
}else{
this.initDelegate=new LzDelegate(this,"__parentInit",this.immediateparent,"oninit");this.delegates.push(this.initDelegate)
}},"__LZapplyArgs",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$0,$1);if(this.__LZdeleted){
return
};this.delegates.push(new LzDelegate(this,"gotNewSubview",this.immediateparent,"onaddsubview"));this.delegates.push(new LzDelegate(this,"removeSubview",this.immediateparent,"onremovesubview"));var $2=this.vip.subviews.length;for(var $3=0;$3<$2;$3++){
this.gotNewSubview(this.vip.subviews[$3])
}},"destroy",function(){
this.releaseLayout();(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"reset",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.locked){
return
};this.update($0)
},"addSubview",function($0){
if($0.getOption("layoutAfter")){
this.__LZinsertAfter($0,$0.getOption("layoutAfter"))
}else{
this.subviews.push($0)
}},"gotNewSubview",function($0){
if(!$0.getOption("ignorelayout")){
this.addSubview($0)
}},"removeSubview",function($0){
for(var $1=this.subviews.length-1;$1>=0;$1--){
if(this.subviews[$1]==$0){
this.subviews.splice($1,1);break
}};this.reset()
},"ignore",function($0){
for(var $1=this.subviews.length-1;$1>=0;$1--){
if(this.subviews[$1]==$0){
this.subviews.splice($1,1);break
}};this.reset()
},"lock",function(){
this.locked=true
},"unlock",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.locked=false;this.reset()
},"__parentInit",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.locked==2){
if(this.isinited){
this.unlock()
}else{
new LzDelegate(this,"unlock",this,"oninit")
}}},"releaseLayout",function(){
if(this.delegates){
for(var $0=this.delegates.length-1;$0>=0;$0--){
this.delegates[$0].unregisterAll()
}};if(this.immediateparent&&this.vip.layouts){
for(var $0=this.vip.layouts.length-1;$0>=0;$0--){
if(this.vip.layouts[$0]==this){
this.vip.layouts.splice($0,1)
}}}},"setLayoutOrder",function($0,$1){
for(var $2=this.subviews.length-1;$2>=0;$2--){
if(this.subviews[$2]===$1){
this.subviews.splice($2,1);break
}};if($2==-1){
return
};if($0=="first"){
this.subviews.unshift($1)
}else if($0=="last"){
this.subviews.push($1)
}else{
for(var $3=this.subviews.length-1;$3>=0;$3--){
if(this.subviews[$3]===$0){
this.subviews.splice($3+1,0,$1);break
}};if($3==-1){
this.subviews.splice($2,0,$1)
}};this.reset();return
},"swapSubviewOrder",function($0,$1){
var $2=-1;var $3=-1;for(var $4=this.subviews.length-1;$4>=0&&($2<0||$3<0);$4--){
if(this.subviews[$4]===$0){
$2=$4
};if(this.subviews[$4]===$1){
$3=$4
}};if($2>=0&&$3>=0){
this.subviews[$3]=$0;this.subviews[$2]=$1
};this.reset();return
},"__LZinsertAfter",function($0,$1){
for(var $2=this.subviews.length-1;$2>=0;$2--){
if(this.subviews[$2]==$1){
this.subviews.splice($2,0,$0)
}}},"update",function($0){
switch(arguments.length){
case 0:
$0=null;

}},"toString",function(){
return "LzLayout for view "+this.immediateparent
}],["tagname","layout","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzLayout.tagname]=LzLayout;Class.make("LzFont",null,["style",void 0,"name",void 0,"height",void 0,"ascent",void 0,"descent",void 0,"advancetable",void 0,"lsbtable",void 0,"rsbtable",void 0,"fontobject",void 0,"$lzsc$initialize",function($0,$1,$2){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.name=$0.name;this.style=$2;this.fontobject=$0;$0[$2]=this;for(var $3 in $1){
if($3=="leading")continue;this[$3]=$1[$3]
};this.height=this.ascent+this.descent;this.advancetable[13]=this.advancetable[32];this.advancetable[160]=0
},"leading",2,"toString",function(){
return "Font style "+this.style+" of name "+this.name
}],null);lz.Font=LzFont;Class.make("LzSelectionManager",LzNode,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"sel","setSelected","selectedHash",void 0,"selected",void 0,"toggle",void 0,"lastRangeStart",void 0,"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.__LZsetSelection([])
},"destroy",function(){
this.__LZsetSelection([]);(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"__LZaddToSelection",function($0,$1){
if($0!=null&&!this.__LZisSelected($0)){
this.selected.push($0);this.__LZsetSelected($0,$1,true)
}},"__LZremoveFromSelection",function($0,$1){
var $2=this.__LZindexOf($0);if($2!=-1){
this.selected.splice($2,1);this.__LZsetSelected($0,$1,false)
}},"__LZindexOf",function($0){
var $1=this.selected;for(var $2=$1.length-1;$2>=0;--$2){
if($1[$2]===$0)return $2
};return -1
},"__LZupdateSelection",function($0,$1){
this.__LZsetSelection($0);for(var $2=$1.length-1;$2>=0;--$2){
var $3=$1[$2];this.__LZsetSelected($3,this.__LZgetView($3),false)
}},"__LZselectRange",function($0,$1){
var $2=this.createRange($0,$1);if($2!=null){
var $3=this.__LZsplitRange($2);this.__LZupdateSelection($3.unchanged,$3.removed);this.lastRangeStart=$0;var $4=$3.added;for(var $5=$4.length-1;$5>=0;--$5){
var $6=$4[$5];this.__LZaddToSelection($6,this.__LZgetView($6))
}}else{
this.clearSelection();this.lastRangeStart=$0
}},"__LZgetSubList",function($0,$1,$2){
var $3=-1;var $4=-1;for(var $5=$0.length-1;$5>=0&&($3==-1||$4==-1);--$5){
if($0[$5]===$1)$3=$5;if($0[$5]===$2)$4=$5
};var $6=null;if($3!=-1&&$4!=-1){
if($4<$3){
$6=$0.slice($4,$3+1);$6.reverse()
}else{
$6=$0.slice($3,$4+1)
}};return $6
},"__LZgetObject",function($0){
return $0
},"__LZgetView",function($0){
return $0
},"__LZsetSelection",function($0){
var $1={};for(var $2=$0.length-1;$2>=0;--$2){
$1[$0[$2].__LZUID]=true
};this.selectedHash=$1;this.selected=$0;this.lastRangeStart=null
},"__LZisSelected",function($0){
return this.selectedHash[$0.__LZUID]==true
},"__LZsetSelected",function($0,$1,$2){
if($2){
this.selectedHash[$1.__LZUID]=true
}else{
delete this.selectedHash[$1.__LZUID]
};$1[this.sel]($2)
},"__LZsplitRange",function($0){
var $1=this.selected;var $2=this.selectedHash;var $3={};var $4=[],$5=[],$6=[];for(var $7=$0.length-1;$7>=0;--$7){
var $8=$0[$7];if($2[$8.__LZUID]){
$4.push($8);$3[$8.__LZUID]=true
}else{
$5.push($8)
}};for(var $7=$1.length-1;$7>=0;--$7){
var $8=$1[$7];if(!$3[$8.__LZUID]){
$6.push($8)
}};return {unchanged:$4,added:$5,removed:$6}},"createRange",function($0,$1){
return this.__LZgetSubList($0.immediateparent.subviews,$0,$1)
},"isSelected",function($0){
return this.__LZisSelected(this.__LZgetObject($0))
},"select",function($0){
var $1=this.__LZgetObject($0);var $2=this.__LZisSelected($1);if($2&&(this.toggle||this.isMultiSelect($0))){
this.unselect($0)
}else{
if(this.selected.length>0&&this.isRangeSelect($0)){
var $3=this.lastRangeStart||this.selected[0];this.__LZselectRange($3,$0)
}else{
if(!this.isMultiSelect($0)){
var $4=$2?this.__LZindexOf($1):-1;var $5=this.selected;this.__LZupdateSelection($4!=-1?$5.splice($4,1):[],$5)
};this.__LZaddToSelection($1,$0)
}}},"unselect",function($0){
this.__LZremoveFromSelection(this.__LZgetObject($0),$0)
},"clearSelection",function(){
this.__LZupdateSelection([],this.selected)
},"getSelection",function(){
return this.selected.concat()
},"isMultiSelect",function($0){
return lz.Keys.isKeyDown("control")
},"isRangeSelect",function($0){
return lz.Keys.isKeyDown("shift")
}],["tagname","selectionmanager","attributes",new LzInheritedHash(LzNode.attributes)]);lz[LzSelectionManager.tagname]=LzSelectionManager;Class.make("LzDataSelectionManager",LzSelectionManager,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"manager",void 0,"__LZsingleClone",void 0,"destroy",function(){
this.manager=null;this.__LZsingleClone=null;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"__LZgetObject",function($0){
return $0.datapath.p
},"__LZgetView",function($0){
if(this.manager!=null){
return this.manager.getCloneForNode($0,true)
}else{
var $1=this.__LZsingleClone;if($1&&$1.datapath.p===$0){
return $1
}else{
return null
}}},"__LZsetSelection",function($0){
this.selected=$0;this.lastRangeStart=null
},"__LZisSelected",function($0){
return $0&&$0.sel||false
},"__LZsetSelected",function($0,$1,$2){
if(this.manager==null&&$1!=null){
this.manager=$1.cloneManager;this.__LZsingleClone=$2&&this.manager==null?$1:null
};$0.sel=$2;if($1!=null){
$1.datapath.setSelected($2)
}},"__LZsplitRange",function($0){
var $1="$lzselkey";var $2=this.selected;var $3=[],$4=[],$5=[];for(var $6=$0.length-1;$6>=0;--$6){
var $7=$0[$6];if($7.sel){
$3.push($7);$7.setUserData($1,true)
}else{
$4.push($7)
}};for(var $6=$2.length-1;$6>=0;--$6){
var $7=$2[$6];if(!$7.setUserData($1,null)){
$5.push($7)
}};return {unchanged:$3,added:$4,removed:$5}},"createRange",function($0,$1){
if(this.manager==null){
this.manager=$1.cloneManager;if(this.manager==null){
return null
}else{
this.__LZsingleClone=null
}};return this.__LZgetSubList(this.manager.nodes,$0,$1.datapath.p)
},"getSelection",function(){
var $0=this.selected;var $1=[];for(var $2=0,$3=$0.length;$2<$3;++$2){
$1[$2]=new LzDatapointer(null,{p:$0[$2]})
};return $1
}],["tagname","dataselectionmanager","attributes",new LzInheritedHash(LzSelectionManager.attributes)]);lz[LzDataSelectionManager.tagname]=LzDataSelectionManager;Class.make("LzCommand",LzNode,["active",true,"keys",null,"$lzc$set_key",function($0){
var $1=this.keys;if($1){
lz.Keys.removeKeyComboCall(this,$1)
};this.keys=$0;lz.Keys.callOnKeyCombo(this,$0)
},"destroy",function(){
var $0=this.keys;if($0){
lz.Keys.removeKeyComboCall(this,$0)
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"onselect",LzDeclaredEvent,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"setKeys",function($0){
this.$lzc$set_key($0)
},"execute",function($0){
if(this.active){
if(this.onselect.ready)this.onselect.sendEvent($0)
}},"keysToString",function(){
var $0="";var $1=this.keys;if($1){
var $2=LzCommand.DisplayKeys;var $3="";var $4=$1.length-1;for(var $5=0;$5<$4;$5++){
$3=$1[$5];if($3 in $2)$3=$2[$3];$0=$0+$3+"+"
};$3=$1[$5];if($3 in $2)$3=$2[$3];$0=$0+$3
};return $0
}],["tagname","command","attributes",new LzInheritedHash(LzNode.attributes),"DisplayKeys",{control:"Ctrl",shift:"Shift",alt:"Alt"}]);lz[LzCommand.tagname]=LzCommand;Class.make("LzState",LzNode,["$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"__LZpool",[],"__LZstatedelegates",void 0,"onapply",LzDeclaredEvent,"onremove",LzDeclaredEvent,"onapplied",LzDeclaredEvent,"applied",false,"$lzc$set_applied",function($0){
if($0){
if(this.isinited){
this.apply()
}else{
this.applyOnInit=true
}}else{
if(this.isinited){
this.remove()
}}},"isapplied",false,"$lzc$set_apply",function($0){
this.setApply($0)
},"asyncnew",false,"subh",null,"pooling",false,"$lzc$set_asyncnew",function($0){
this.__LZsetProperty($0,"asyncnew")
},"$lzc$set_pooling",function($0){
this.__LZsetProperty($0,"pooling")
},"$lzc$set___LZsourceLocation",function($0){
this.__LZsetProperty($0,"__LZsourceLocation")
},"heldArgs",void 0,"handlerMethodNames",void 0,"releasedconstraints",void 0,"appliedChildren",void 0,"applyOnInit",false,"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.heldArgs={};this.handlerMethodNames={};this.appliedChildren=[]
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(this.applyOnInit){
this.apply()
}},"createChildren",function($0){
this.subh=$0;this.__LZinstantiationDone()
},"setApply",function($0){
if(typeof $0=="function"){
this.addProperty("apply",$0);return
};this.$lzc$set_applied($0)
},"apply",function(){
if(this.applied){
return
};var $0=this.parent;this.applied=this.isapplied=true;var $1=$0._instanceAttrs;if($1){
for(var $2 in this.heldArgs){
if(LzConstraintExpr["$lzsc$isa"]?LzConstraintExpr.$lzsc$isa($1[$2]):$1[$2] instanceof LzConstraintExpr){
if(this.releasedconstraints==null){
this.releasedconstraints=[]
};var $3=$1[$2].methodName;if($0.releaseConstraintMethod($3)){
this.releasedconstraints.push($3)
}}}};var $4=$0.__LZdelegates;$0.__LZdelegates=null;$0.__LZapplyArgs(this.heldArgs);if(this.subh)var $5=this.subh.length;$0.__LZsetPreventInit();for(var $6=0;$6<$5;$6++){
if(this.__LZpool&&this.__LZpool[$6]){
this.appliedChildren.push(this.__LZretach(this.__LZpool[$6]))
}else{
this.appliedChildren.push($0.makeChild(this.subh[$6],this.asyncnew))
}};$0.__LZclearPreventInit();$0.__LZresolveReferences();this.__LZstatedelegates=$0.__LZdelegates;$0.__LZdelegates=$4;if(this.onapply.ready)this.onapply.sendEvent(this);if(this.onapplied.ready)this.onapplied.sendEvent(true)
},"remove",function(){
if(!this.applied){
return
};this.applied=this.isapplied=false;if(this.onremove.ready)this.onremove.sendEvent(this);if(this.onapplied.ready)this.onapplied.sendEvent(false);if(this.__LZstatedelegates){
for(var $0=0;$0<this.__LZstatedelegates.length;$0++){
this.__LZstatedelegates[$0].unregisterAll()
}};if(this.pooling&&this.appliedChildren.length){
this.__LZpool=[]
};for(var $0=0;$0<this.appliedChildren.length;$0++){
var $1=this.appliedChildren[$0];if(this.pooling){
if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($1):$1 instanceof LzView){
this.__LZpool.push(this.__LZdetach($1))
}else{
$1.destroy();this.__LZpool.push(null)
}}else{
$1.destroy()
}};this.appliedChildren=[];if(this.releasedconstraints!=null){
this.releasedconstraints=null
}},"destroy",function(){
this.pooling=false;this.remove();(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"__LZapplyArgs",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};var $2={};var $3=this.heldArgs;var $4=this.handlerMethodNames;for(var $5 in $0){
var $6=$0[$5];var $7="$lzc$set_"+$5;if((Function["$lzsc$isa"]?Function.$lzsc$isa(this[$7]):this[$7] instanceof Function)||($5 in $4)){
$2[$5]=$6
}else{
$3[$5]=$6
}};for(var $5 in $2){
var $6=$2[$5];if(LzOnceExpr["$lzsc$isa"]?LzOnceExpr.$lzsc$isa($6):$6 instanceof LzOnceExpr){
var $8=$6.methodName;if($8 in $3){
$2[$8]=$3[$8];delete $3[$8]
};if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($6):$6 instanceof LzAlwaysExpr){
var $9=$6.dependenciesName;if($9 in $3){
$2[$9]=$3[$9];delete $3[$9]
}}}};var $a=null;for(var $5 in $3){
var $6=$3[$5];if(LzOnceExpr["$lzsc$isa"]?LzOnceExpr.$lzsc$isa($6):$6 instanceof LzOnceExpr){
if($a==null){
$a=[]
};$a.push($5,$6)
}};if($a!=null){
for(var $b=0,$c=$a.length;$b<$c;$b+=2){
var $5=$a[$b];var $d=$a[$b+1];var $8=$d.methodName;var $e=$8+this.__LZUID;var $f=null;if(Function["$lzsc$isa"]?Function.$lzsc$isa($3[$8]):$3[$8] instanceof Function){
$3[$e]=$3[$8];delete $3[$8]
}else if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$8]):this[$8] instanceof Function){
$3[$e]=this[$8]
};if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($d):$d instanceof LzAlwaysExpr){
var $9=$d.dependenciesName;var $g=$9+this.__LZUID;if(Function["$lzsc$isa"]?Function.$lzsc$isa($3[$9]):$3[$9] instanceof Function){
$3[$g]=$3[$9];delete $3[$9]
}else if(Function["$lzsc$isa"]?Function.$lzsc$isa(this[$9]):this[$9] instanceof Function){
$3[$g]=this[$9]
};$3[$5]=new ($d.constructor)($e,$g,$f)
}else{
$3[$5]=new ($d.constructor)($e,$f)
}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$2)
},"$lzc$set_$delegates",function($0){
var $1=[];var $2=[];for(var $3=0;$3<$0.length;$3+=3){
if(LzState.events[$0[$3]]&&!$0[$3+2]){
var $4=$2;var $5=$0[$3+1];if(this.heldArgs[$5]){
this.addProperty($5,this.heldArgs[$5]);delete this.heldArgs[$5]
};this.handlerMethodNames[$5]=true
}else{
var $4=$1
};$4.push($0[$3],$0[$3+1],$0[$3+2])
};if($2.length){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_$delegates"]||this.nextMethod(arguments.callee,"$lzc$set_$delegates")).call(this,$2)
};if($1.length){
this.heldArgs.$delegates=$1
}},"__LZsetProperty",function($0,$1){
this[$1]=$0
},"__LZdetach",function($0){
$0.$lzc$set_visible(false);return $0
},"__LZretach",function($0){
$0.$lzc$set_visible(true);return $0
}],["tagname","state","attributes",new LzInheritedHash(LzNode.attributes),"props",{apply:true},"events",{onremove:true,onapply:true,onapplied:true}]);(function($0){
with($0)with($0.prototype){
{
prototype.$isstate=true
}}})(LzState);lz[LzState.tagname]=LzState;Mixin.make("LzDataNodeMixin",null,["onownerDocument",LzDeclaredEvent,"onDocumentChange",LzDeclaredEvent,"onparentNode",LzDeclaredEvent,"onchildNode",LzDeclaredEvent,"onchildNodes",LzDeclaredEvent,"onattributes",LzDeclaredEvent,"onnodeName",LzDeclaredEvent,"nodeType",void 0,"parentNode",null,"ownerDocument",void 0,"childNodes",null,"__LZo",-1,"__LZcoDirty",true,"sel",false,"__LZuserData",null,"__LZuserHandler",null,"getParent",function(){
return this.parentNode
},"getOffset",function(){
if(!this.parentNode)return 0;if(this.parentNode.__LZcoDirty)this.parentNode.__LZupdateCO();return this.__LZo
},"getPreviousSibling",function(){
if(!this.parentNode)return null;if(this.parentNode.__LZcoDirty)this.parentNode.__LZupdateCO();return this.parentNode.childNodes[this.__LZo-1]
},"$lzc$getPreviousSibling_dependencies",function($0,$1){
return [this.parentNode,"childNodes",this,"parentNode"]
},"getNextSibling",function(){
if(!this.parentNode)return null;if(this.parentNode.__LZcoDirty)this.parentNode.__LZupdateCO();return this.parentNode.childNodes[this.__LZo+1]
},"$lzc$getNextSibling_dependencies",function($0,$1){
return [this.parentNode,"childNodes",this,"parentNode"]
},"childOfNode",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};var $2=$1?this:this.parentNode;while($2){
if($2===$0)return true;$2=$2.parentNode
};return false
},"childOf",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};return this.childOfNode($0,$1)
},"$lzc$set_ownerDocument",function($0){
this.ownerDocument=$0;if(this.childNodes){
for(var $1=0;$1<this.childNodes.length;$1++){
this.childNodes[$1].$lzc$set_ownerDocument($0)
}};if(this.onownerDocument.ready){
this.onownerDocument.sendEvent($0)
}},"setOwnerDocument",function($0){
this.$lzc$set_ownerDocument($0)
},"cloneNode",function($0){
switch(arguments.length){
case 0:
$0=false;

};return undefined
},"serialize",function(){
return undefined
},"__LZlockFromUpdate",function($0){
this.ownerDocument.__LZdoLock($0)
},"__LZunlockFromUpdate",function($0){
this.ownerDocument.__LZdoUnlock($0)
},"setUserData",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};if(this.__LZuserData==null){
this.__LZuserData={}};var $3=this.__LZuserData[$0];if($1!=null){
this.__LZuserData[$0]=$1
}else if($3!=null){
delete this.__LZuserData[$0]
};return $3!=null?$3:null
},"getUserData",function($0){
if(this.__LZuserData==null){
return null
}else{
var $1=this.__LZuserData[$0];return $1!=null?$1:null
}}],null);lz.DataNodeMixin=LzDataNodeMixin;Class.make("LzDataNode",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"toString",function(){
return "lz.DataNode"
}],["ELEMENT_NODE",1,"TEXT_NODE",3,"DOCUMENT_NODE",9,"stringToLzData",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=false;
case 2:
$2=false;

};return LzDataElement.stringToLzData($0,$1,$2)
}]);lz.DataNode=LzDataNode;Mixin.make("LzDataElementMixin",null,["__LZchangeQ",null,"__LZlocker",null,"nodeName",null,"attributes",null,"insertBefore",function($0,$1){
if($0==null){
return null
}else if($1==null){
return this.appendChild($0)
}else{
var $2=this.__LZgetCO($1);if($2>=0){
var $3=$0===$1;if($0.parentNode!=null){
if($0.parentNode===this){
if(!$3){
var $4=this.__LZremoveChild($0);if($4!=-1&&$4<$2){
$2-=1
}}}else{
$0.parentNode.removeChild($0)
}};if(!$3){
this.__LZcoDirty=true;this.childNodes.splice($2,0,$0)
};$0.$lzc$set_ownerDocument(this.ownerDocument);$0.parentNode=this;if($0.onparentNode.ready)$0.onparentNode.sendEvent(this);if(this.onchildNodes.ready)this.onchildNodes.sendEvent($0);this.ownerDocument.handleDocumentChange("insertBefore",this,0);return $0
};return null
}},"replaceChild",function($0,$1){
if($0==null){
return null
}else{
var $2=this.__LZgetCO($1);if($2>=0){
var $3=$0===$1;if($0.parentNode!=null){
if($0.parentNode===this){
if(!$3){
var $4=this.__LZremoveChild($0);if($4!=-1&&$4<$2){
$2-=1
}}}else{
$0.parentNode.removeChild($0)
}};if(!$3){
$0.__LZo=$2;this.childNodes[$2]=$0
};$0.$lzc$set_ownerDocument(this.ownerDocument);$0.parentNode=this;if($0.onparentNode.ready)$0.onparentNode.sendEvent(this);if(this.onchildNodes.ready)this.onchildNodes.sendEvent($0);this.ownerDocument.handleDocumentChange("childNodes",this,0,$0);return $0
};return null
}},"removeChild",function($0){
var $1=this.__LZgetCO($0);if($1>=0){
this.__LZcoDirty=true;this.childNodes.splice($1,1);if(this.onchildNodes.ready)this.onchildNodes.sendEvent($0);this.ownerDocument.handleDocumentChange("removeChild",this,0,$0);return $0
};return null
},"appendChild",function($0){
if($0==null){
return null
}else{
if($0.parentNode!=null){
if($0.parentNode===this){
this.__LZremoveChild($0)
}else{
$0.parentNode.removeChild($0)
}};this.childNodes.push($0);$0.__LZo=this.childNodes.length-1;$0.$lzc$set_ownerDocument(this.ownerDocument);$0.parentNode=this;if($0.onparentNode.ready)$0.onparentNode.sendEvent(this);if(this.onchildNodes.ready)this.onchildNodes.sendEvent($0);this.ownerDocument.handleDocumentChange("appendChild",this,0,$0);return $0
}},"hasChildNodes",function(){
return this.childNodes.length>0
},"cloneNode",function($0){
switch(arguments.length){
case 0:
$0=false;

};var $1=new LzDataElement(this.nodeName,this.attributes);if($0){
var $2=this.childNodes;var $3=[];for(var $4=$2.length-1;$4>=0;--$4){
$3[$4]=$2[$4].cloneNode(true)
};$1.$lzc$set_childNodes($3)
};return $1
},"getAttr",function($0){
return this.attributes[$0]
},"$lzc$getAttr_dependencies",function($0,$1){
return [$1,"attributes"]
},"setAttr",function($0,$1){
$1=String($1);this.attributes[$0]=$1;if(this.onattributes.ready)this.onattributes.sendEvent($0);this.ownerDocument.handleDocumentChange("attributes",this,1,{name:$0,value:$1,type:"set"});return $1
},"removeAttr",function($0){
var $1=this.attributes[$0];delete this.attributes[$0];if(this.onattributes.ready)this.onattributes.sendEvent($0);this.ownerDocument.handleDocumentChange("attributes",this,1,{name:$0,value:$1,type:"remove"});return $1
},"hasAttr",function($0){
return this.attributes[$0]!=null
},"$lzc$hasAttr_dependencies",function($0,$1){
return [$1,"attributes"]
},"getFirstChild",function(){
return this.childNodes[0]
},"$lzc$getFirstChild_dependencies",function($0,$1){
return [this,"childNodes"]
},"getLastChild",function(){
return this.childNodes[this.childNodes.length-1]
},"$lzc$getLastChild_dependencies",function($0,$1){
return [this,"childNodes"]
},"__LZgetCO",function($0){
if($0!=null){
var $1=this.childNodes;if(!this.__LZcoDirty){
var $2=$0.__LZo;if($1[$2]===$0){
return $2
}}else{
for(var $2=$1.length-1;$2>=0;--$2){
if($1[$2]===$0){
return $2
}}}};return -1
},"__LZremoveChild",function($0){
var $1=this.__LZgetCO($0);if($1>=0){
this.__LZcoDirty=true;this.childNodes.splice($1,1)
};return $1
},"__LZupdateCO",function(){
var $0=this.childNodes;for(var $1=0,$2=$0.length;$1<$2;$1++){
$0[$1].__LZo=$1
};this.__LZcoDirty=false
},"$lzc$set_attributes",function($0){
var $1={};for(var $2 in $0){
$1[$2]=$0[$2]
};this.attributes=$1;if(this.onattributes.ready)this.onattributes.sendEvent($1);this.ownerDocument.handleDocumentChange("attributes",this,1)
},"setAttrs",function($0){
this.$lzc$set_attributes($0)
},"$lzc$set_childNodes",function($0){
if(!$0)$0=[];this.childNodes=$0;if($0.length>0){
var $1=true;var $2=$0[0].parentNode;if($2!=null&&$2!==this&&$2.childNodes===$0){
$1=false;$2.$lzc$set_childNodes([])
};for(var $3=0;$3<$0.length;$3++){
var $4=$0[$3];if($4){
if($1&&$4.parentNode!=null){
if($4.parentNode!==this){
$4.parentNode.removeChild($4)
}};$4.$lzc$set_ownerDocument(this.ownerDocument);$4.parentNode=this;if($4.onparentNode.ready)$4.onparentNode.sendEvent(this);$4.__LZo=$3
}}};this.__LZcoDirty=false;if(this.onchildNodes.ready)this.onchildNodes.sendEvent($0);this.ownerDocument.handleDocumentChange("childNodes",this,0)
},"setChildNodes",function($0){
this.$lzc$set_childNodes($0)
},"$lzc$set_nodeName",function($0){
this.nodeName=$0;if(this.onnodeName.ready)this.onnodeName.sendEvent($0);if(this.parentNode){
if(this.parentNode.onchildNodes.ready)this.parentNode.onchildNodes.sendEvent(this);if(this.parentNode.onchildNode.ready)this.parentNode.onchildNode.sendEvent(this)
};this.ownerDocument.handleDocumentChange("childNodeName",this.parentNode,0);this.ownerDocument.handleDocumentChange("nodeName",this,1)
},"setNodeName",function($0){
this.$lzc$set_nodeName($0)
},"__LZgetText",function(){
var $0="";var $1=this.childNodes;for(var $2=0,$3=$1.length;$2<$3;$2++){
var $4=$1[$2];if($4.nodeType==LzDataElement.TEXT_NODE){
$0+=$4.data
}};return $0
},"getElementsByTagName",function($0){
var $1=[];var $2=this.childNodes;for(var $3=0,$4=$2.length;$3<$4;$3++){
if($2[$3].nodeName==$0){
$1.push($2[$3])
}};return $1
},"__LZlt","<","__LZgt",">","serialize",function(){
return this.serializeInternal()
},"serializeInternal",function($0){
switch(arguments.length){
case 0:
$0=Infinity;

};var $1=this.__LZlt+this.nodeName;var $2=this.attributes;for(var $3 in $2){
$1+=" "+$3+'="'+LzDataElement.__LZXMLescape($2[$3])+'"';if($1.length>$0){
break
}};var $4=this.childNodes;if($1.length<=$0&&$4.length){
$1+=this.__LZgt;for(var $5=0,$6=$4.length;$5<$6;$5++){
$1+=$4[$5].serialize();if($1.length>$0){
break
}};$1+=this.__LZlt+"/"+this.nodeName+this.__LZgt
}else{
$1+="/"+this.__LZgt
};return $1
},"handleDocumentChange",function($0,$1,$2,$3){
switch(arguments.length){
case 3:
$3=null;

};var $4={who:$1,what:$0,type:$2};if($3)$4.cobj=$3;if(this.__LZchangeQ){
this.__LZchangeQ.push($4)
}else{
if(this.onDocumentChange.ready)this.onDocumentChange.sendEvent($4)
}},"toString",function(){
return this.serialize()
},"__LZdoLock",function($0){
if(!this.__LZchangeQ){
this.__LZchangeQ=[];this.__LZlocker=$0
}},"__LZdoUnlock",function($0){
if(this.__LZlocker!=$0){
return
};var $1=this.__LZchangeQ;this.__LZchangeQ=null;this.__LZlocker=null;if($1!=null){
for(var $2=0,$3=$1.length;$2<$3;$2++){
var $4=true;var $5=$1[$2];for(var $6=0;$6<$2;$6++){
var $7=$1[$6];if($5.who==$7.who&&$5.what==$7.what&&$5.type==$7.type){
$4=false;break
}};if($4){
this.handleDocumentChange($5.what,$5.who,$5.type)
}}}}],null);lz.DataElementMixin=LzDataElementMixin;Class.make("LzDataElement",[LzDataElementMixin,LzDataNodeMixin,LzDataNode],["$lzsc$initialize",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.nodeName=$0;this.nodeType=LzDataElement.ELEMENT_NODE;this.ownerDocument=this;if($1){
this.$lzc$set_attributes($1)
}else{
this.attributes={}};if($2){
this.$lzc$set_childNodes($2)
}else{
this.childNodes=[];this.__LZcoDirty=false
}}],["NODE_CLONED",1,"NODE_IMPORTED",2,"NODE_DELETED",3,"NODE_RENAMED",4,"NODE_ADOPTED",5,"makeNodeList",function($0,$1){
var $2=[];for(var $3=0;$3<$0;$3++){
$2[$3]=new LzDataElement($1)
};return $2
},"valueToElement",function($0){
return new LzDataElement("element",{},LzDataElement.__LZv2E($0))
},"__LZdateToJSON",function($0){
var $1;var $2;$1=function($0){
return($0<10?"0":"")+$0
};$2=function($0){
return($0<10?"00":($0<100?"0":""))+$0
};if(isFinite($0.valueOf())){
return $0.getUTCFullYear()+"-"+$1($0.getUTCMonth()+1)+"-"+$1($0.getUTCDate())+"T"+$1($0.getUTCHours())+":"+$1($0.getUTCMinutes())+":"+$1($0.getUTCSeconds())+"."+$2($0.getUTCMilliseconds())+"Z"
}else{
return null
}},"__LZv2E",function($0){
var $1=[];if(typeof $0=="object"){
if((LzDataElement["$lzsc$isa"]?LzDataElement.$lzsc$isa($0):$0 instanceof LzDataElement)||(LzDataText["$lzsc$isa"]?LzDataText.$lzsc$isa($0):$0 instanceof LzDataText)){
$1[0]=$0
}else if(Date["$lzsc$isa"]?Date.$lzsc$isa($0):$0 instanceof Date){
var $2=LzDataElement.__LZdateToJSON($0);if($2!=null){
$1[0]=new LzDataText($2)
}}else if(Array["$lzsc$isa"]?Array.$lzsc$isa($0):$0 instanceof Array){
var $3=$0.__LZtag!=null?$0.__LZtag:"item";for(var $4=0;$4<$0.length;$4++){
var $5=LzDataElement.__LZv2E($0[$4]);$1[$4]=new LzDataElement($3,null,$5)
}}else{
var $4=0;for(var $6 in $0){
if($6.indexOf("__LZ")==0)continue;$1[$4++]=new LzDataElement($6,null,LzDataElement.__LZv2E($0[$6]))
}}}else if($0!=null){
$1[0]=new LzDataText(String($0))
};return $1.length!=0?$1:null
},"ELEMENT_NODE",1,"TEXT_NODE",3,"DOCUMENT_NODE",9,"__LZescapechars",{"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&apos;"},"__LZXMLescape",function($0){
if(typeof $0!="string")return $0;var $1=LzDataElement.__LZescapechars;var $2=$0.length;var $3="";for(var $4=0;$4<$2;$4++){
var $5=$0.charCodeAt($4);if($5<32){
$3+="&#x"+$5.toString(16)+";"
}else{
var $6=$0.charAt($4);$3+=$1[$6]||$6
}};return $3
},"stringToLzData",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=false;
case 2:
$2=false;

};if($0!=null&&$0!=""){
var $3;try{
$3=LzXMLParser.parseXML($0,$1,$2)
}
catch($4){};if($3!=null){
var $5=LzXMLTranslator.copyXML($3,$1,$2);return $5
}};return null
},"whitespaceChars",{" ":true,"\r":true,"\n":true,"\t":true},"trim",function($0){
var $1=LzDataElement.whitespaceChars;var $2=$0.length;var $3=0;var $4=$2-1;var $5;while($3<$2){
$5=$0.charAt($3);if($1[$5]!=true)break;$3++
};while($4>$3){
$5=$0.charAt($4);if($1[$5]!=true)break;$4--
};return $0.slice($3,$4+1)
}]);lz.DataElement=LzDataElement;Class.make("LzDataText",[LzDataNodeMixin,LzDataNode],["$lzsc$initialize",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.nodeType=LzDataElement.TEXT_NODE;this.data=$0
},"ondata",LzDeclaredEvent,"nodeName","#text","data","","$lzc$set_data",function($0){
this.data=$0;if(this.ondata.ready){
this.ondata.sendEvent($0)
};if(this.ownerDocument){
this.ownerDocument.handleDocumentChange("data",this,1)
}},"setData",function($0){
this.$lzc$set_data($0)
},"cloneNode",function($0){
switch(arguments.length){
case 0:
$0=false;

};var $1=new LzDataText(this.data);return $1
},"serialize",function(){
return LzDataElement.__LZXMLescape(this.data)
},"toString",function(){
return this.data
}],null);lz.DataText=LzDataText;Class.make("LzDataRequest",LzEventable,["requestor",null,"src",null,"timeout",Infinity,"status",null,"rawdata",null,"error",null,"onstatus",LzDeclaredEvent,"$lzsc$initialize",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.requestor=$0
}],["SUCCESS","success","TIMEOUT","timeout","ERROR","error","READY","ready","LOADING","loading"]);lz.DataRequest=LzDataRequest;Class.make("LzDataProvider",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"doRequest",function($0){},"abortLoadForRequest",function($0){}],null);lz.DataProvider=LzDataProvider;Class.make("LzHTTPDataRequest",LzDataRequest,["method","GET","postbody",void 0,"proxied",void 0,"proxyurl",void 0,"multirequest",false,"queuerequests",false,"queryparams",null,"requestheaders",null,"getresponseheaders",false,"responseheaders",void 0,"cacheable",false,"clientcacheable",false,"trimwhitespace",false,"nsprefix",false,"serverproxyargs",null,"xmldata",null,"loadtime",0,"loadstarttime",void 0,"secure",false,"secureport",void 0,"parsexml",true,"loader",null,"$lzsc$initialize",function($0){
switch(arguments.length){
case 0:
$0=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0)
}],null);lz.HTTPDataRequest=LzHTTPDataRequest;Class.make("LzHTTPDataProvider",LzDataProvider,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"makeLoader",function($0){
var $1=$0.proxied;var $2=new LzHTTPLoader(this,$1);$0.loader=$2;$2.loadSuccess=this.loadSuccess;$2.loadError=this.loadError;$2.loadTimeout=this.loadTimeout;$2.setProxied($1);var $3=$0.secure;if($3==null){
if($0.src.substring(0,5)=="https"){
$3=true
}};$2.secure=$3;if($3){
$2.baserequest=lz.Browser.getBaseURL($3,$0.secureport);$2.secureport=$0.secureport
};return $2
},"abortLoadForRequest",function($0){
$0.loader.abort()
},"doRequest",function($0){
var $1=$0;if(!$1.src)return;var $2=$1.proxied;var $3=$1.loader;if($3==null||$1.multirequest==true||$1.queuerequests==true){
$3=this.makeLoader($1)
};$3.dataRequest=$1;$3.setQueueing($1.queuerequests);$3.setTimeout($1.timeout);$3.setOption("serverproxyargs",$1.serverproxyargs);$3.setOption("cacheable",$1.cacheable==true);$3.setOption("timeout",$1.timeout);$3.setOption("trimwhitespace",$1.trimwhitespace==true);$3.setOption("nsprefix",$1.nsprefix==true);$3.setOption("sendheaders",$1.getresponseheaders==true);$3.setOption("parsexml",$1.parsexml);if($1.clientcacheable!=null){
$3.setOption("ccache",$1.clientcacheable)
};var $4={};var $5=$1.requestheaders;if($5!=null){
var $6=$5.getNames();for(var $7=0;$7<$6.length;$7++){
var $8=$6[$7];var $9=$5.getValue($8);if($2){
$4[$8]=$9
}else{
$3.setRequestHeader($8,$9)
}}};var $a=$1.queryparams;var $b=true;var $c=$1.postbody;if($c==null&&$a!=null){
$c=$a.serialize("=","&",true)
}else{
$b=false
};$3.setOption("hasquerydata",$b);var $d=new LzURL($1.src);if($1.method=="GET"){
if($d.query==null){
$d.query=$c
}else{
if($c!=null){
$d.query+="&"+$c
}};$c=null
};var $e="__lzbc__="+new Date().getTime();if(!$2&&$1.method=="POST"&&($c==null||$c=="")){
$c=$e
};var $f;if($2){
$f=$3.makeProxiedURL($1.proxyurl,$d.toString(),$1.method,"xmldata",$4,$c);var $g=$f.indexOf("?");var $h=$f.substring($g+1,$f.length);var $i=$f.substring(0,$g);$f=$i+"?"+$e;$c=$h
}else{
if(!$1.clientcacheable){
if($1.method=="GET"){
if($d.query==null){
$d.query=$e
}else{
$d.query+="&"+$e
}}};$f=$d.toString()
};$1.loadstarttime=new Date().getTime();$1.status=LzDataRequest.LOADING;$3.open($2?"POST":$1.method,$f,null,null);$3.send($c)
},"loadSuccess",function($0,$1){
var $2=$0.dataRequest;$2.status=LzDataRequest.SUCCESS;$0.owner.loadResponse($2,$1)
},"loadError",function($0,$1){
var $2=$0.dataRequest;$2.status=LzDataRequest.ERROR;$0.owner.loadResponse($2,$1)
},"loadTimeout",function($0,$1){
var $2=$0.dataRequest;$2.loadtime=new Date().getTime()-$2.loadstarttime;$2.status=LzDataRequest.TIMEOUT;$2.onstatus.sendEvent($2)
},"setRequestError",function($0,$1){
$0.error=$1;$0.status=LzDataRequest.ERROR
},"loadResponse",function($0,$1){
$0.loadtime=new Date().getTime()-$0.loadstarttime;$0.rawdata=$0.loader.getResponse();if($1==null){
this.setRequestError($0,"client could not parse XML from server");$0.onstatus.sendEvent($0);return
};var $2=$0.proxied;if(!$0.parsexml){
$0.onstatus.sendEvent($0);return
}else if($2&&$1.childNodes[0].nodeName=="error"){
this.setRequestError($0,$1.childNodes[0].attributes["msg"]);$0.onstatus.sendEvent($0);return
};var $3=new (lz.Param)();var $4=null;if($2){
var $5=$1.childNodes.length>1&&$1.childNodes[1].childNodes?$1.childNodes[1].childNodes:null;if($5!=null){
for(var $6=0;$6<$5.length;$6++){
var $7=$5[$6];if($7.attributes)$3.addValue($7.attributes.name,$7.attributes.value)
}};if($1.childNodes[0].childNodes)$4=$1.childNodes[0].childNodes[0]
}else{
var $5=$0.loader.getResponseHeaders();if($5){
$3.addObject($5)
};$4=$1
};$0.xmldata=$4;$0.responseheaders=$3;$0.onstatus.sendEvent($0)
}],null);lz.HTTPDataProvider=LzHTTPDataProvider;Class.make("LzDataset",[LzDataElementMixin,LzDataNodeMixin,LzNode],["rawdata",null,"dataprovider",void 0,"multirequest",false,"dataRequest",null,"dataRequestClass",LzHTTPDataRequest,"dsloadDel",null,"errorstring",void 0,"reqOnInitDel",void 0,"secureport",void 0,"proxyurl",null,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"timeout",60000,"$lzc$set_timeout",function($0){
this.timeout=$0
},"postbody",null,"$lzc$set_postbody",function($0){
this.postbody=$0
},"acceptencodings",false,"type",null,"params",null,"nsprefix",false,"getresponseheaders",false,"querytype","GET","$lzc$set_querytype",function($0){
this.querytype=$0.toUpperCase()
},"trimwhitespace",false,"cacheable",false,"clientcacheable",false,"querystring",null,"src",null,"$lzc$set_src",function($0){
this.src=$0;if(this.autorequest){
this.doRequest()
}},"autorequest",false,"$lzc$set_autorequest",function($0){
this.autorequest=$0
},"request",false,"$lzc$set_request",function($0){
this.request=$0;if($0&&!this.isinited){
this.reqOnInitDel=new LzDelegate(this,"doRequest",this,"oninit")
}},"headers",null,"proxied",null,"$lzc$set_proxied",function($0){
var $1={"true":true,"false":false,"null":null,inherit:null}[$0];if($1!==void 0){
this.proxied=$1
}},"isProxied",function(){
return this.proxied!=null?this.proxied:canvas.proxied
},"responseheaders",null,"queuerequests",false,"oncanvas",void 0,"$lzc$set_initialdata",function($0){
if($0!=null){
var $1=LzDataElement.stringToLzData($0,this.trimwhitespace,this.nsprefix);if($1!=null){
this.$lzc$set_data($1.childNodes)
}}},"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"construct",function($0,$1){
this.nodeType=LzDataElement.DOCUMENT_NODE;this.ownerDocument=this;this.attributes={};this.childNodes=[];this.queuerequests=false;this.oncanvas=$0==canvas||$0==null;if(!("proxyurl" in $1)){
this.proxyurl=canvas.getProxyURL()
};if(("timeout" in $1)&&$1.timeout){
this.timeout=$1.timeout
}else{
this.timeout=canvas.dataloadtimeout
};if(("dataprovider" in $1)&&$1.dataprovider){
this.dataprovider=$1.dataprovider
}else{
this.dataprovider=canvas.defaultdataprovider
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1)
},"$lzc$set_name",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_name"]||this.nextMethod(arguments.callee,"$lzc$set_name")).call(this,$0);if($0!=null){
this.nodeName=$0;if(this.oncanvas){
canvas[$0]=this
}else{
$0=this.parent.getUID()+"."+$0
};canvas.datasets[$0]=this
}},"destroy",function(){
this.$lzc$set_childNodes([]);this.dataRequest=null;if(this.dsloadDel){
this.dsloadDel.unregisterAll()
};var $0=this.name;if(this.oncanvas){
if(canvas[$0]===this){
delete canvas[$0]
}}else{
$0=this.parent.getUID()+"."+$0
};if(canvas.datasets[$0]===this){
delete canvas.datasets[$0]
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"getErrorString",function(){
return this.errorstring
},"getLoadTime",function(){
var $0=this.dataRequest;return $0?$0.loadtime:0
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
},"$lzc$set_data",function($0){
if($0==null){
return
}else if($0 instanceof Array){
this.$lzc$set_childNodes($0)
}else{
this.$lzc$set_childNodes([$0])
};this.data=$0;if(this.ondata.ready)this.ondata.sendEvent(this)
},"gotError",function($0){
this.errorstring=$0;if(this.onerror.ready)this.onerror.sendEvent(this)
},"gotTimeout",function(){
if(this.ontimeout.ready)this.ontimeout.sendEvent(this)
},"getContext",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this
},"getDataset",function(){
return this
},"getPointer",function(){
var $0=new LzDatapointer(null);$0.p=this.getContext();return $0
},"setQueryString",function($0){
this.params=null;if(typeof $0=="object"){
if($0 instanceof lz.Param){
this.querystring=$0.toString()
}else{
var $1=new (lz.Param)();for(var $2 in $0){
$1.setValue($2,$0[$2],true)
};this.querystring=$1.toString();$1.destroy()
}}else{
this.querystring=$0
};if(this.autorequest){
this.doRequest()
}},"setQueryParam",function($0,$1){
this.querystring=null;if(!this.params){
this.params=new (lz.Param)()
};this.params.setValue($0,$1);if(this.autorequest){
this.doRequest()
}},"setQueryParams",function($0){
this.querystring=null;if(!this.params){
this.params=new (lz.Param)()
};if($0){
this.params.addObject($0)
}else if($0==null){
this.params.remove()
};if($0&&this.autorequest){
this.doRequest()
}},"setSrc",function($0){
this.$lzc$set_src($0)
},"setProxyRequests",function($0){
this.$lzc$set_proxied($0)
},"setRequest",function($0){
this.$lzc$set_request($0)
},"setAutorequest",function($0){
this.$lzc$set_autorequest($0)
},"setPostBody",function($0){
this.$lzc$set_postbody($0)
},"setQueryType",function($0){
this.$lzc$set_querytype($0)
},"setInitialData",function($0){
this.$lzc$set_initialdata($0)
},"abort",function(){
var $0=this.dataRequest;if($0){
this.dataprovider.abortLoadForRequest($0)
}},"doRequest",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(!this.src)return;if(this.multirequest||this.dataRequest==null||this.queuerequests){
this.dataRequest=new (this.dataRequestClass)(this)
};var $1=this.dataRequest;$1.src=this.src;$1.timeout=this.timeout;$1.status=LzDataRequest.READY;$1.method=this.querytype;$1.postbody=null;if(this.querystring){
$1.queryparams=new (lz.Param)();$1.queryparams.addObject(lz.Param.parseQueryString(this.querystring))
}else{
$1.queryparams=this.params
};if(this.querytype.toUpperCase()=="POST"){
$1.postbody=this.postbody;if($1.queryparams){
var $2=$1.queryparams.getValue("lzpostbody");if($2!=null){
$1.queryparams.remove("lzpostbody");$1.postbody=$2
}}};$1.proxied=this.isProxied();$1.proxyurl=this.proxyurl;$1.queuerequests=this.queuerequests;$1.requestheaders=this.headers;$1.getresponseheaders=this.getresponseheaders;$1.secureport=this.secureport;$1.cacheable=this.cacheable;$1.clientcacheable=this.clientcacheable;$1.trimwhitespace=this.trimwhitespace;$1.nsprefix=this.nsprefix;if(this.dsloadDel==null){
this.dsloadDel=new LzDelegate(this,"handleDataResponse",$1,"onstatus")
}else{
this.dsloadDel.register($1,"onstatus")
};this.dataprovider.doRequest($1)
},"handleDataResponse",function($0){
if(this.dsloadDel!=null){
this.dsloadDel.unregisterFrom($0.onstatus)
};this.rawdata=$0.rawdata;this.errorstring=null;if($0.status==LzDataRequest.SUCCESS){
if(this.responseheaders!=null){
this.responseheaders.destroy()
};this.responseheaders=$0.responseheaders;this.$lzc$set_data($0.xmldata)
}else if($0.status==LzDataRequest.ERROR){
this.gotError($0.error)
}else if($0.status==LzDataRequest.TIMEOUT){
this.gotTimeout()
}},"setHeader",function($0,$1){
if(!this.headers){
this.headers=new (lz.Param)()
};this.headers.setValue($0,$1)
},"getRequestHeaderParams",function(){
return this.headers
},"clearRequestHeaderParams",function(){
if(this.headers){
this.headers.remove()
}},"getResponseHeader",function($0){
var $1=this.responseheaders;if($1){
var $2=$1.getValues($0);if($2&&$2.length==1){
return $2[0]
}else{
return $2
}};return void 0
},"getAllResponseHeaders",function(){
return this.responseheaders
},"toString",function(){
return "LzDataset "+":"+this.name
}],["tagname","dataset","attributes",new LzInheritedHash(LzNode.attributes),"slashPat","/","queryStringToTable",function($0){
var $1={};var $2=$0.split("&");for(var $3=0;$3<$2.length;++$3){
var $4=$2[$3];var $5="";var $6=$4.indexOf("=");if($6>0){
$5=unescape($4.substring($6+1));$4=$4.substring(0,$6)
};if($4 in $1){
var $7=$1[$4];if($7 instanceof Array){
$7.push($5)
}else{
$1[$4]=[$7,$5]
}}else{
$1[$4]=$5
}};return $1
}]);(function($0){
with($0)with($0.prototype){
{
LzDataset.attributes.name="localdata"
}}})(LzDataset);lz[LzDataset.tagname]=LzDataset;Class.make("__LzHttpDatasetPoolClass",null,["_uid",0,"_p",[],"get",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};var $4;if(this._p.length>0){
$4=this._p.pop()
}else{
$4=new LzDataset(null,{name:"LzHttpDatasetPool"+this._uid,type:"http",acceptencodings:$3});this._uid++
};if($0!=null){
$0.register($4,"ondata")
};if($1!=null){
$1.register($4,"onerror")
};if($2!=null){
$2.register($4,"ontimeout")
};return $4
},"recycle",function($0){
$0.setQueryParams(null);$0.$lzc$set_postbody(null);$0.clearRequestHeaderParams();$0.ondata.clearDelegates();$0.ontimeout.clearDelegates();$0.onerror.clearDelegates();$0.$lzc$set_data([]);this._p.push($0)
}],null);var LzHttpDatasetPool=new __LzHttpDatasetPoolClass();Class.make("LzDatapointer",LzNode,["$lzc$set_xpath",function($0){
this.setXPath($0)
},"$lzc$set_context",function($0){
this.setDataContext($0)
},"$lzc$set_pointer",function($0){
this.setPointer($0)
},"$lzc$set_p",function($0){
this.setPointer($0)
},"p",null,"context",null,"__LZtracking",null,"__LZtrackDel",null,"xpath",null,"parsedPath",null,"__LZlastdotdot",null,"__LZspecialDotDot",false,"__LZdotdotCheckDel",null,"errorDel",null,"timeoutDel",null,"rerunxpath",false,"onp",LzDeclaredEvent,"onDocumentChange",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"onrerunxpath",LzDeclaredEvent,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"gotError",function($0){
if(this.onerror.ready)this.onerror.sendEvent($0)
},"gotTimeout",function($0){
if(this.ontimeout.ready)this.ontimeout.sendEvent($0)
},"xpathQuery",function($0){
var $1=this.parsePath($0);var $2=$1.getContext(this);var $3=this.__LZgetNodes($1,$2?$2:this.p);if($3==null)return null;if($1.aggOperator!=null){
if($1.aggOperator=="last"){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($3):$3 instanceof Array){
return $3.length
}else{
if(!$2&&$3===this.p){
if($1.selectors&&$1.selectors.length>0){
var $4=$1.selectors;var $5=0;while($4[$5]=="."&&$5<$4.length){
++$5
};return $5!=$4.length?1:this.__LZgetLast()
}else{
return this.__LZgetLast()
}}else{
return 1
}}}else if($1.aggOperator=="position"){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($3):$3 instanceof Array){
var $6=[];for(var $5=0;$5<$3.length;$5++){
$6.push($5+1)
};return $6
}else{
if(!$2&&$3===this.p){
if($1.selectors&&$1.selectors.length>0){
var $4=$1.selectors;var $5=0;while($4[$5]=="."&&$5<$4.length){
++$5
};return $5!=$4.length?1:this.__LZgetPosition()
}else{
return this.__LZgetPosition()
}}else{
return 1
}}}}else if($1.operator!=null){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($3):$3 instanceof Array){
var $7=[];for(var $5=0;$5<$3.length;$5++){
$7.push(this.__LZprocessOperator($3[$5],$1))
};return $7
}else{
return this.__LZprocessOperator($3,$1)
}}else{
return $3
}},"$lzc$xpathQuery_dependencies",function($0,$1,$2){
if(this["parsePath"]){
var $3=this.parsePath($2);return [$3.hasDotDot?$1.context.getContext().ownerDocument:$1,"DocumentChange"]
}else{
return [$1,"DocumentChange"]
}},"setPointer",function($0){
this.setXPath(null);if($0!=null){
this.setDataContext($0.ownerDocument)
}else{
this.__LZsetTracking(null)
};var $1=this.data!=$0;var $2=this.p!=$0;this.p=$0;this.data=$0;this.__LZsendUpdate($1,$2);return $0!=null
},"getDataset",function(){
if(this.p==null){
if(this.context===this){
return null
}else{
return this.context.getDataset()
}}else{
return this.p.ownerDocument
}},"setXPath",function($0){
if(!$0){
this.xpath=null;this.parsedPath=null;if(this.p)this.__LZsetTracking(this.p.ownerDocument);return false
};this.xpath=$0;this.parsedPath=this.parsePath($0);var $1=this.parsedPath.getContext(this);if(this.rerunxpath&&this.parsedPath.hasDotDot&&!$1){
this.__LZspecialDotDot=true
}else{
if(this.__LZdotdotCheckDel){
this.__LZdotdotCheckDel.unregisterAll()
}};this.setDataContext($1);return this.runXPath()
},"runXPath",function(){
if(!this.parsedPath){
return false
};var $0=null;if(this.context&&((LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa(this.context):this.context instanceof LzDatapointer)||(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa(this.context):this.context instanceof LzDataset)||(AnonDatasetGenerator["$lzsc$isa"]?AnonDatasetGenerator.$lzsc$isa(this.context):this.context instanceof AnonDatasetGenerator))){
$0=this.context.getContext()
};if($0){
var $1=this.__LZgetNodes(this.parsedPath,$0,0)
}else{
var $1=null
};if($1==null){
this.__LZHandleNoNodes();return false
}else if(Array["$lzsc$isa"]?Array.$lzsc$isa($1):$1 instanceof Array){
this.__LZHandleMultiNodes($1);return false
}else{
this.__LZHandleSingleNode($1);return true
}},"__LZsetupDotDot",function($0){
if(this.__LZlastdotdot!=$0.ownerDocument){
if(this.__LZdotdotCheckDel==null){
this.__LZdotdotCheckDel=new LzDelegate(this,"__LZcheckDotDot")
}else{
this.__LZdotdotCheckDel.unregisterAll()
};this.__LZlastdotdot=$0.ownerDocument;this.__LZdotdotCheckDel.register(this.__LZlastdotdot,"onDocumentChange")
}},"__LZHandleSingleNode",function($0){
if(this.__LZspecialDotDot)this.__LZsetupDotDot($0);this.__LZupdateLocked=true;this.__LZpchanged=$0!=this.p;this.p=$0;this.__LZsetData();this.__LZupdateLocked=false;this.__LZsendUpdate()
},"__LZHandleNoNodes",function(){
var $0=this.p!=null;var $1=this.data!=null;this.p=null;this.data=null;this.__LZsendUpdate($1,$0)
},"__LZHandleMultiNodes",function($0){
this.__LZHandleNoNodes();return null
},"__LZsetData",function(){
if(this.parsedPath&&this.parsedPath.aggOperator!=null){
if(this.parsedPath.aggOperator=="last"){
this.data=this.__LZgetLast();this.__LZsendUpdate(true)
}else if(this.parsedPath.aggOperator=="position"){
this.data=this.__LZgetPosition();this.__LZsendUpdate(true)
}}else if(this.parsedPath&&this.parsedPath.operator!=null){
this.__LZsimpleOperatorUpdate()
}else{
if(this.data!=this.p){
this.data=this.p;this.__LZsendUpdate(true)
}}},"__LZgetLast",function(){
var $0=this.context;if($0==null||$0===this||!(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($0):$0 instanceof LzDatapointer)){
return 1
}else{
return $0.__LZgetLast()||1
}},"__LZgetPosition",function(){
var $0=this.context;if($0==null||$0===this||!(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($0):$0 instanceof LzDatapointer)){
return 1
}else{
return $0.__LZgetPosition()||1
}},"__LZupdateLocked",false,"__LZpchanged",false,"__LZdchanged",false,"__LZsendUpdate",function($0,$1){
switch(arguments.length){
case 0:
$0=false;
case 1:
$1=false;

};this.__LZdchanged=$0||this.__LZdchanged;this.__LZpchanged=$1||this.__LZpchanged;if(this.__LZupdateLocked){
return false
};if(this.__LZdchanged){
if(this.ondata.ready)this.ondata.sendEvent(this.data);this.__LZdchanged=false
};if(this.__LZpchanged){
if(this.onp.ready)this.onp.sendEvent(this.p);this.__LZpchanged=false;if(this.onDocumentChange.ready)this.onDocumentChange.sendEvent({who:this.p,type:2,what:"context"})
};return true
},"isValid",function(){
return this.p!=null
},"__LZsimpleOperatorUpdate",function(){
var $0=this.p!=null?this.__LZprocessOperator(this.p,this.parsedPath):void 0;var $1=false;if(this.data!=$0||this.parsedPath.operator=="attributes"){
this.data=$0;$1=true
};this.__LZsendUpdate($1)
},"parsePath",function($0){
if(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa($0):$0 instanceof LzDatapath){
var $1=$0.xpath
}else{
var $1=$0
};var $2=LzDatapointer.ppcache;var $3=$2[$1];if($3==null){
$3=new LzParsedPath($1,this);$2[$1]=$3
};return $3
},"getLocalDataContext",function($0){
var $1=this.parent;if($0){
var $2=$0;for(var $3=0;$3<$2.length&&$1!=null;$3++){
$1=$1[$2[$3]]
};if($1!=null&&!(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($1):$1 instanceof LzDataset)&&(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($1["localdata"]):$1["localdata"] instanceof LzDataset)){
$1=$1["localdata"];return $1
}};if($1!=null&&(LzDataset["$lzsc$isa"]?LzDataset.$lzsc$isa($1):$1 instanceof LzDataset)){
return $1
}else{
return null
}},"__LZgetNodes",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=0;

};if($1==null){
return null
};if($0.selectors!=null){
var $3=$0.selectors.length;for(var $4=$2;$4<$3;$4++){
var $5=$0.selectors[$4];var $6=LzDatapointer.pathSymbols[$5]||0;var $7=$0.selectors[$4+1];if($7&&!(String["$lzsc$isa"]?String.$lzsc$isa($7):$7 instanceof String)&&$7["pred"]=="range"){
var $8=$0.selectors[++$4]
}else{
var $8=null
};var $9=null;if((Object["$lzsc$isa"]?Object.$lzsc$isa($5):$5 instanceof Object)&&("pred" in $5)&&null!=$5.pred){
if($5.pred=="hasattr"){
$1=$1.hasAttr($5.attr)?$1:null
}else if($5.pred=="attrval"){
if($1.attributes!=null){
$1=$1.attributes[$5.attr]==$5.val?$1:null
}else{
$1=null
}}}else if($6==0){
$9=this.nodeByName($5,$8,$1)
}else if($6==1){
$1=$1.ownerDocument
}else if($6==2){
$1=$1.parentNode
}else if($6==3){
$9=[];if($1.childNodes){
var $a=$1.childNodes;var $b=$a.length;var $c=$8!=null?$8[0]:0;var $d=$8!=null?$8[1]:$b;var $e=0;for(var $f=0;$f<$b;$f++){
var $g=$a[$f];if($g.nodeType==LzDataElement.ELEMENT_NODE){
$e++;if($e>=$c){
$9.push($g)
};if($e==$d){
break
}}}}};if($9!=null){
if($9.length>1){
if($4==$3-1){
return $9
};var $h=[];for(var $f=0;$f<$9.length;$f++){
var $i=this.__LZgetNodes($0,$9[$f],$4+1);if($i!=null){
if(Array["$lzsc$isa"]?Array.$lzsc$isa($i):$i instanceof Array){
for(var $j=0;$j<$i.length;$j++){
$h.push($i[$j])
}}else{
$h.push($i)
}}};if($h.length==0){
return null
}else if($h.length==1){
return $h[0]
}else{
return $h
}}else{
$1=$9[0]
}};if($1==null){
return null
}}};return $1
},"getContext",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.p
},"nodeByName",function($0,$1,$2){
if(!$2){
$2=this.p;if(!this.p)return null
};var $3=[];if($2.childNodes!=null){
var $4=$2.childNodes;var $5=$4.length;var $6=$1!=null?$1[0]:0;var $7=$1!=null?$1[1]:$5;var $8=0;for(var $9=0;$9<$5;$9++){
var $a=$4[$9];if($a.nodeName==$0){
$8++;if($8>=$6){
$3.push($a)
};if($8==$7){
break
}}}};return $3
},"$lzc$set_rerunxpath",function($0){
this.rerunxpath=$0;if(this.onrerunxpath.ready)this.onrerunxpath.sendEvent($0)
},"dupePointer",function(){
var $0=new LzDatapointer();$0.setFromPointer(this);return $0
},"__LZdoSelect",function($0,$1){
var $2=this.p;for(;$2!=null&&$1>0;$1--){
if($2.nodeType==LzDataNode.TEXT_NODE){
if($0=="getFirstChild")break
};$2=$2[$0]()
};if($2!=null&&$1==0){
this.setPointer($2);return true
};return false
},"selectNext",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.__LZdoSelect("getNextSibling",$0?$0:1)
},"selectPrev",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.__LZdoSelect("getPreviousSibling",$0?$0:1)
},"selectChild",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.__LZdoSelect("getFirstChild",$0?$0:1)
},"selectParent",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.__LZdoSelect("getParent",$0?$0:1)
},"selectNextParent",function(){
var $0=this.p;if(this.selectParent()&&this.selectNext()){
return true
}else{
this.setPointer($0);return false
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
},"setNodeName",function($0){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.$lzc$set_nodeName($0)
}},"getNodeAttributes",function(){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.attributes
};return null
},"getNodeAttribute",function($0){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.attributes[$0]
};return null
},"setNodeAttribute",function($0,$1){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.setAttr($0,$1)
}},"deleteNodeAttribute",function($0){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.removeAttr($0)
}},"getNodeText",function(){
if(!this.p){
return null
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
return this.p.__LZgetText()
};return null
},"setNodeText",function($0){
if(!this.p){
return
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
var $1=false;var $2=this.p.childNodes;for(var $3=0;$3<$2.length;$3++){
var $4=$2[$3];if($4.nodeType==LzDataElement.TEXT_NODE){
$4.$lzc$set_data($0);$1=true;break
}};if(!$1){
this.p.appendChild(new LzDataText($0))
}}},"getNodeCount",function(){
if(!this.p||this.p.nodeType==LzDataElement.TEXT_NODE)return 0;return this.p.childNodes.length||0
},"addNode",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;

};if(!this.p){
return null
};var $3=new LzDataElement($0,$2);if($1!=null){
$3.appendChild(new LzDataText($1))
};if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.appendChild($3)
};return $3
},"deleteNode",function(){
if(!this.p){
return
};var $0=this.p;if(!this.rerunxpath){
if(!(this.selectNext()||this.selectPrev())){
this.__LZHandleNoNodes()
}};$0.parentNode.removeChild($0);return $0
},"sendDataChange",function($0){
this.getDataset().sendDataChange($0)
},"comparePointer",function($0){
return this.p==$0.p
},"addNodeFromPointer",function($0){
if(!$0.p)return null;if(!this.p){
return null
};var $1=$0.p.cloneNode(true);if(this.p.nodeType!=LzDataElement.TEXT_NODE){
this.p.appendChild($1)
};return new LzDatapointer(null,{pointer:$1})
},"setFromPointer",function($0){
this.setPointer($0.p)
},"__LZprocessOperator",function($0,$1){
if($0==null){
return
};var $2=$1.operator;switch($2){
case "serialize":
return $0.serialize();
case "text":
return $0.nodeType!=LzDataElement.TEXT_NODE?$0.__LZgetText():void 0;
case "name":
return $0.nodeName;
default:
if($1.hasAttrOper){
if($0.nodeType!=LzDataElement.TEXT_NODE&&$0["attributes"]){
if($2=="attributes"){
return $0.attributes
}else{
return $0.attributes[$2.substr(11)]
}}else{
return
}}

}},"makeRootNode",function(){
return new LzDataElement("root")
},"finishRootNode",function($0){
return $0.childNodes[0]
},"makeElementNode",function($0,$1,$2){
var $3=new LzDataElement($1,$0);$2.appendChild($3);return $3
},"makeTextNode",function($0,$1){
var $2=new LzDataText($0);$1.appendChild($2);return $2
},"serialize",function(){
if(this.p==null){
return null
};return this.p.serialize()
},"setDataContext",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};if($0==null){
this.context=this;if(this.p){
this.__LZsetTracking(this.p.ownerDocument)
}}else if(this.context!=$0){
this.context=$0;if(this.errorDel!=null){
this.errorDel.unregisterAll();this.timeoutDel.unregisterAll()
};this.__LZsetTracking($0);var $2=this.xpath!=null;if($2){
if(this.errorDel==null){
this.errorDel=new LzDelegate(this,"gotError");this.timeoutDel=new LzDelegate(this,"gotTimeout")
};this.errorDel.register($0,"onerror");this.timeoutDel.register($0,"ontimeout")
}}},"__LZcheckChange",function($0){
if(!this.rerunxpath){
if(!this.p||$0.who==this.context){
this.runXPath()
}else if(this.__LZneedsOpUpdate($0)){
this.__LZsimpleOperatorUpdate()
};return false
}else{
if($0.type==2||($0.type==0||$0.type==1&&this.parsedPath&&this.parsedPath.hasOpSelector)&&(this.parsedPath&&this.parsedPath.hasDotDot||this.p==null||this.p.childOfNode($0.who))){
this.runXPath();return true
}else if(this.__LZneedsOpUpdate($0)){
this.__LZsimpleOperatorUpdate()
};return false
}},"__LZneedsOpUpdate",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=this.parsedPath;if($1!=null&&$1.operator!=null){
var $2=$0.who;var $3=$0.type;if($1.operator!="text"){
return $3==1&&$2==this.p
}else{
return $3==0&&$2==this.p||$2.parentNode==this.p&&$2.nodeType==LzDataElement.TEXT_NODE
}}else{
return false
}},"__LZcheckDotDot",function($0){
var $1=$0.who;var $2=$0.type;if(($2==0||$2==1&&this.parsedPath.hasOpSelector)&&this.context.getContext().childOfNode($1)){
this.runXPath()
}},"destroy",function(){
this.__LZsetTracking(null);if(this.errorDel)this.errorDel.unregisterAll();if(this.timeoutDel)this.timeoutDel.unregisterAll();if(this.__LZdotdotCheckDel){
this.__LZdotdotCheckDel.unregisterAll()
};this.p=null;this.data=null;this.__LZlastdotdot=null;this.context=null;this.__LZtracking=null;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"__LZsetTracking",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=false;
case 2:
$2=false;

};var $3=this.__LZtracking;var $4=this.__LZtrackDel;if($0){
if($3!=null&&$3.length==1&&$3[0]===$0){
return
};if($4){
$4.unregisterAll()
};var $5=$1||this.xpath;if($5){
if(!$4){
this.__LZtrackDel=$4=new LzDelegate(this,"__LZcheckChange")
};this.__LZtracking=$3=[$0];$4.register($0,"onDocumentChange")
}}else{
this.__LZtracking=[];if($4){
this.__LZtrackDel.unregisterAll()
}}}],["tagname","datapointer","attributes",{ignoreplacement:true},"ppcache",{},"pathSymbols",{"/":1,"..":2,"*":3,".":4}]);lz[LzDatapointer.tagname]=LzDatapointer;Class.make("LzParam",LzEventable,["d",null,"delimiter","&","$lzc$set_delimiter",function($0){
this.setDelimiter($0)
},"separator","=","$lzc$set_separator",function($0){
this.setSeparator($0)
},"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.d={}},"parseQueryString",function($0){
return lz.Param.parseQueryString($0)
},"addObject",function($0){
for(var $1 in $0){
this.setValue($1,$0[$1])
}},"clone",function($0){
switch(arguments.length){
case 0:
$0=null;

};var $1=new (lz.Param)();for(var $2 in this.d){
$1.d[$2]=this.d[$2].concat()
};return $1
},"remove",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0==null){
this.d={}}else{
var $1=this.d[$0];if($1!=null){
$1.shift();if(!$1.length){
delete this.d[$0]
}}}},"setValue",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=false;

};if($2)$1=encodeURIComponent($1);this.d[$0]=[$1]
},"addValue",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=false;

};if($2)$1=encodeURIComponent($1);var $3=this.d[$0];if($3==null){
this.d[$0]=[$1]
}else{
$3.push($1)
}},"getValue",function($0){
var $1=this.d[$0];if($1!=null){
return $1[0]
};return null
},"getValues",function($0){
var $1=this.d[$0];if($1!=null){
return $1.concat()
};return null
},"getValueNoCase",function($0){
var $1=this.getValues($0);return $1!=null&&$1.length==1?$1[0]:$1
},"getNames",function(){
var $0=[];for(var $1 in this.d){
$0.push($1)
};return $0
},"setDelimiter",function($0){
var $1=this.delimiter;this.delimiter=$0;return $1
},"setSeparator",function($0){
var $1=this.separator;this.separator=$0;return $1
},"toString",function(){
return this.serialize()
},"serialize",function($0,$1,$2){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=false;

};var $0=$0==null?this.separator:$0;var $3=$1==null?this.delimiter:$1;var $4="";var $5=false;for(var $6 in this.d){
var $7=this.d[$6];if($7!=null){
for(var $8=0;$8<$7.length;++$8){
if($5)$4+=$3;$4+=$6+$0+($2?encodeURIComponent($7[$8]):$7[$8]);$5=true
}}};return $4
}],["parseQueryString",function($0){
var $1=$0.split("&");var $2={};for(var $3=0;$3<$1.length;++$3){
var $4=$1[$3];var $5="";var $6=$4.indexOf("=");if($6>0){
$5=unescape($4.substring($6+1));$4=$4.substring(0,$6)
};$2[$4]=$5
};return $2
}]);lz.Param=LzParam;Class.make("LzParsedPath",null,["path",null,"selectors",null,"context",null,"dsetname",null,"dsrcname",null,"islocaldata",null,"operator",null,"aggOperator",null,"hasAttrOper",false,"hasOpSelector",false,"hasDotDot",false,"getContext",function($0){
if(this.context!=null){
return this.context
}else{
if(this.islocaldata!=null){
return $0.getLocalDataContext(this.islocaldata)
}else{
if(this.dsrcname!=null){
return canvas[this.dsrcname][this.dsetname]
}else{
if(this.dsetname!=null){
return canvas.datasets[this.dsetname]
}}}};return null
},"$lzsc$initialize",function($0,$1){
this.path=$0;this.selectors=[];var $2=$0.indexOf(":/");if($2>-1){
var $3=$0.substring(0,$2).split(":");if($3.length>1){
var $4=LzParsedPath.trim($3[0]);var $5=LzParsedPath.trim($3[1]);if($4=="local"){
this.islocaldata=$5.split(".")
}else{
this.dsrcname=$4;this.dsetname=$5
}}else{
var $6=LzParsedPath.trim($3[0]);if($6=="new"){
this.context=new AnonDatasetGenerator(this)
}else{
this.dsetname=$6
}};var $7=$0.substring($2+2)
}else{
var $7=$0
};var $8=[];var $9="";var $a=false;var $b=false;for(var $c=0;$c<$7.length;$c++){
var $d=$7.charAt($c);if($d=="\\"&&$b==false){
$b=true;continue
}else if($b==true){
$b=false;$9+=$d;continue
}else if($a==false&&$d=="/"){
$8.push($9);$9="";continue
}else if($d=="'"){
$a=$a?false:true
};$9+=$d
};$8.push($9);if($8!=null){
for(var $c=0;$c<$8.length;$c++){
var $e=LzParsedPath.trim($8[$c]);if($c==$8.length-1){
if($e.charAt(0)=="@"){
this.hasAttrOper=true;if($e.charAt(1)=="*"){
this.operator="attributes"
}else{
this.operator="attributes."+$e.substring(1,$e.length)
};continue
}else if($e.charAt($e.length-1)==")"){
if($e.indexOf("last")>-1){
this.aggOperator="last"
}else if($e.indexOf("position")>-1){
this.aggOperator="position"
}else if($e.indexOf("name")>-1){
this.operator="name"
}else if($e.indexOf("text")>-1){
this.operator="text"
}else if($e.indexOf("serialize")>-1){
this.operator="serialize"
};continue
}else if($e==""){
continue
}};var $f=$e.split("[");var $g=LzParsedPath.trim($f[0]);this.selectors.push($g==""?"/":$g);if($g==""||$g==".."){
this.hasDotDot=true
};if($f!=null){
for(var $h=1;$h<$f.length;$h++){
var $i=LzParsedPath.trim($f[$h]);$i=$i.substring(0,$i.length-1);if(LzParsedPath.trim($i).charAt(0)=="@"){
var $j=$i.split("=");var $k;var $l=$j.shift().substring(1);if($j.length>0){
var $m=LzParsedPath.trim($j.join("="));$m=$m.substring(1,$m.length-1);$k={pred:"attrval",attr:LzParsedPath.trim($l),val:LzParsedPath.trim($m)}}else{
$k={pred:"hasattr",attr:LzParsedPath.trim($l)}};this.selectors.push($k);this.hasOpSelector=true
}else{
var $k=$i.split("-");$k[0]=LzParsedPath.trim($k[0]);if($k[0]==""){
$k[0]=1
};if($k[1]!=null){
$k[1]=LzParsedPath.trim($k[1])
};if($k[1]==""){
$k[1]=Infinity
}else if($k.length==1){
$k[1]=$k[0]
};$k.pred="range";this.selectors.push($k)
}}}}}},"toString",function(){
return "Parsed path for path: "+this.path
},"debugWrite",function(){}],["trim",function($0){
var $1=0;var $2=false;while($0.charAt($1)==" "){
$1++;$2=true
};var $3=$0.length-$1;while($0.charAt($1+$3-1)==" "){
$3--;$2=true
};return $2?$0.substr($1,$3):$0
}]);Class.make("AnonDatasetGenerator",LzEventable,["pp",null,"__LZdepChildren",null,"onDocumentChange",LzDeclaredEvent,"onerror",LzDeclaredEvent,"ontimeout",LzDeclaredEvent,"noncontext",true,"$lzsc$initialize",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.pp=$0
},"getContext",function(){
var $0=new LzDataset(null,{name:null});var $1=this.pp.selectors;if($1!=null){
var $2=$0.getPointer();for(var $3=0;$3<$1.length;$3++){
if($1[$3]=="/")continue;$2.addNode($1[$3]);$2.selectChild()
}};return $0
},"getDataset",function(){
return null
}],null);Class.make("LzDatapath",LzDatapointer,["datacontrolsvisibility",true,"$lzc$set_datacontrolsvisibility",function($0){
this.datacontrolsvisibility=$0
},"__LZtakeDPSlot",true,"storednodes",null,"__LZneedsUpdateAfterInit",false,"__LZdepChildren",null,"sel",false,"__LZisclone",false,"pooling",false,"replication",void 0,"axis","y","spacing",0,"sortpath",void 0,"$lzc$set_sortpath",function($0){
this.setOrder($0)
},"setOrder",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if(this.__LZisclone){
this.immediateparent.cloneManager.setOrder($0,$1)
}else{
this.sortpath=$0;if($1!=null){
this.sortorder=$1
}}},"sortorder","ascending","$lzc$set_sortorder",function($0){
this.setComparator($0)
},"setComparator",function($0){
if(this.__LZisclone){
this.immediateparent.cloneManager.setComparator($0)
}else{
this.sortorder=$0
}},"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"construct",function($0,$1){
this.rerunxpath=true;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);if($1.datacontrolsvisibility!=null){
this.datacontrolsvisibility=$1.datacontrolsvisibility
};if(this.__LZtakeDPSlot){
this.immediateparent.datapath=this;var $2=this.immediateparent.searchParents("datapath").datapath;if($2!=null){
var $3=$2.__LZdepChildren;if($3!=null){
$2.__LZdepChildren=[];for(var $4=$3.length-1;$4>=0;$4--){
var $5=$3[$4];if($5!==this&&!(LzDataAttrBind["$lzsc$isa"]?LzDataAttrBind.$lzsc$isa($5):$5 instanceof LzDataAttrBind)&&$5.immediateparent!=this.immediateparent&&$5.immediateparent.childOf(this.immediateparent)){
$5.setDataContext(this,true)
}else{
$2.__LZdepChildren.push($5)
}}}}}},"__LZHandleMultiNodes",function($0){
var $1;if(this.replication=="lazy"){
$1=LzLazyReplicationManager
}else if(this.replication=="resize"){
$1=LzResizeReplicationManager
}else{
$1=LzReplicationManager
};this.storednodes=$0;var $2=new $1(this,this._instanceAttrs);this.storednodes=null;return $2
},"setNodes",function($0){
var $1=this.__LZHandleMultiNodes($0);if(!$1)$1=this;$1.__LZsetTracking(null);if($0){
for(var $2=0;$2<$0.length;$2++){
var $3=$0[$2];var $4=$3.ownerDocument;$1.__LZsetTracking($4,true,$3!=$4)
}}},"__LZsendUpdate",function($0,$1){
switch(arguments.length){
case 0:
$0=false;
case 1:
$1=false;

};var $2=this.__LZpchanged;if(!(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsendUpdate"]||this.nextMethod(arguments.callee,"__LZsendUpdate")).call(this,$0,$1)){
return false
};if(this.immediateparent.isinited){
this.__LZApplyData($2)
}else{
this.__LZneedsUpdateAfterInit=true
};return true
},"__LZApplyDataOnInit",function(){
if(this.__LZneedsUpdateAfterInit){
this.__LZApplyData();this.__LZneedsUpdateAfterInit=false
}},"__LZApplyData",function($0){
switch(arguments.length){
case 0:
$0=false;

};var $1=this.immediateparent;if(this.datacontrolsvisibility){
if(LzView["$lzsc$isa"]?LzView.$lzsc$isa($1):$1 instanceof LzView){
var $2=$1;$2.__LZvizDat=this.p!=null;$2.__LZupdateShown()
}};var $3=$0||$1.data!=this.data||this.parsedPath&&this.parsedPath.operator=="attributes";this.data=this.data==null?null:this.data;$1.data=this.data;if($3){
if($1.ondata.ready)$1.ondata.sendEvent(this.data);var $4=this.parsedPath;if($4&&($4.operator!=null||$4.aggOperator!=null)){
$1.applyData(this.data)
}}},"setDataContext",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};if($0==null&&this.immediateparent!=null){
$0=this.immediateparent.searchParents("datapath").datapath;$1=true
};if($0==this.context)return;if($1){
if($0.__LZdepChildren==null){
$0.__LZdepChildren=[this]
}else{
$0.__LZdepChildren.push(this)
}}else{
if(this.context&&(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa(this.context):this.context instanceof LzDatapath)){
var $2=this.context.__LZdepChildren;if($2){
for(var $3=0;$3<$2.length;$3++){
if($2[$3]===this){
$2.splice($3,1);break
}}}}};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$0)
},"destroy",function(){
this.__LZupdateLocked=true;var $0=this.context;if($0&&!$0.__LZdeleted&&(LzDatapath["$lzsc$isa"]?LzDatapath.$lzsc$isa($0):$0 instanceof LzDatapath)){
var $1=$0.__LZdepChildren;if($1!=null){
for(var $2=0;$2<$1.length;$2++){
if($1[$2]===this){
$1.splice($2,1);break
}}}};var $3=this.immediateparent;if(!$3.__LZdeleted){
var $4=this.__LZdepChildren;if($4!=null&&$4.length>0){
var $5=$3.searchParents("datapath").datapath;for(var $2=0;$2<$4.length;$2++){
$4[$2].setDataContext($5,true)
};this.__LZdepChildren=null
}};if($3.datapath===this){
$3.datapath=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).apply(this,arguments)
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($0){
switch(arguments.length){
case 0:
$0=false;

};if(!$0&&this.p){
this.p.__LZlockFromUpdate(this)
};var $1=this.parsedPath?this.parsedPath.operator:null;if($1!=null){
var $2=this.immediateparent.updateData();if($2!==void 0){
if($1=="name"){
this.setNodeName($2)
}else if($1=="text"){
this.setNodeText($2)
}else if($1=="attributes"){
this.p.$lzc$set_attributes($2)
}else{
this.setNodeAttribute($1.substring(11),$2)
}}};var $3=this.__LZdepChildren;if($3!=null){
for(var $4=0;$4<$3.length;$4++){
$3[$4].__LZupdateData(true)
}};if(!$0&&this.p){
this.p.__LZunlockFromUpdate(this)
}},"toString",function(){
return "Datapath for "+this.immediateparent
},"__LZcheckChange",function($0){
if(!(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZcheckChange"]||this.nextMethod(arguments.callee,"__LZcheckChange")).call(this,$0)){
if($0.who.childOfNode(this.p,true)&&this.onDocumentChange.ready){
this.onDocumentChange.sendEvent($0)
}};return false
},"__LZsetTracking",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=false;
case 2:
$2=false;

};if(!$0||!$1){
return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsetTracking"]||this.nextMethod(arguments.callee,"__LZsetTracking")).call(this,$0,true)
};var $3=this.__LZtracking;var $4=this.__LZtrackDel;if($2){
var $5=$3.length;for(var $6=0;$6<$5;$6++){
if($3[$6]===$0){
return
}}};if(!$4){
this.__LZtrackDel=$4=new LzDelegate(this,"__LZcheckChange")
};$3.push($0);$4.register($0,"onDocumentChange")
},"$lzc$set___LZmanager",function($0){
this.__LZisclone=true;this.immediateparent.cloneManager=$0;this.parsedPath=$0.parsedPath;this.xpath=$0.xpath;this.setDataContext($0)
},"setClonePointer",function($0){
var $1=this.p!=$0;this.p=$0;if($1){
if($0&&this.sel!=$0.sel){
this.sel=$0.sel||false;this.immediateparent.setSelected(this.sel)
};this.__LZpchanged=true;this.__LZsetData()
}},"setSelected",function($0){
this.p.sel=$0;this.sel=$0;this.immediateparent.setSelected($0)
},"__LZgetLast",function(){
var $0=this.context;if(this.__LZisclone){
return $0.nodes.length
}else if(this.p==$0.getContext()&&(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($0):$0 instanceof LzDatapointer)){
return $0.__LZgetLast()
}else{
return 1
}},"__LZgetPosition",function(){
if(this.__LZisclone){
return this.immediateparent.clonenumber+1
}else{
var $0=this.context;if(this.p==$0.getContext()&&(LzDatapointer["$lzsc$isa"]?LzDatapointer.$lzsc$isa($0):$0 instanceof LzDatapointer)){
return $0.__LZgetPosition()
}else{
return 1
}}}],["tagname","datapath","attributes",new LzInheritedHash(LzDatapointer.attributes)]);lz[LzDatapath.tagname]=LzDatapath;Class.make("LzReplicationManager",LzDatapath,["asyncnew",true,"initialnodes",void 0,"clonePool",void 0,"cloneClass",void 0,"cloneParent",void 0,"cloneAttrs",void 0,"cloneChildren",void 0,"hasdata",void 0,"orderpath",void 0,"comparator",void 0,"__LZxpathconstr",null,"__LZxpathdepend",null,"visible",true,"__LZpreventXPathUpdate",false,"nodes",void 0,"clones",void 0,"__LZdataoffset",0,"onnodes",LzDeclaredEvent,"onclones",LzDeclaredEvent,"onvisible",LzDeclaredEvent,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"getDefaultPooling",function(){
return false
},"construct",function($0,$1){
this.pooling=this.getDefaultPooling();this.__LZtakeDPSlot=false;this.datacontrolsvisibility=false;var $2=$0.immediateparent;this.classroot=$2.classroot;if($2===canvas){
this.nodes=[];this.clones=[];this.clonePool=[];return
};this.datapath=this;var $3=$2.name;if($3!=null){
$1.name=$3;$2.immediateparent[$3]=null;$2.parent[$3]=null
};var $4=$2.$lzc$bind_id;if($4!=null){
$4.call(null,$2,false);$2.$lzc$bind_id=null;this.$lzc$bind_id=$4;$4.call(null,this)
};var $5=$2.$lzc$bind_name;if($5!=null){
$5.call(null,$2,false);$2.$lzc$bind_name=null;this.$lzc$bind_name=$5;$5.call(null,this)
};$1.xpath=LzNode._ignoreAttribute;if($0.sortpath!=null){
$1.sortpath=$0.sortpath
};if($0.sortorder!=null||$0.sortorder){
$1.sortorder=$0.sortorder
};this.initialnodes=$0.storednodes;if($0.__LZspecialDotDot){
this.__LZspecialDotDot=true;if($0.__LZdotdotCheckDel){
$0.__LZdotdotCheckDel.unregisterAll()
};$0.__LZspecialDotDot=false
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$2.parent,$1);if($1.name!=null&&$2.parent!=$2.immediateparent){
$2.immediateparent[$1.name]=this
};this.xpath=$0.xpath;this.parsedPath=$0.parsedPath;this.cloneClass=$2.constructor;this.cloneParent=$2.parent;var $6=new LzInheritedHash($2._instanceAttrs);$6.datapath=LzNode._ignoreAttribute;$6.$datapath={"class":lz.datapath};$6.$datapath.attrs={datacontrolsvisibility:$0.datacontrolsvisibility,__LZmanager:this};delete $6.id;delete $6.name;delete $6.$lzc$bind_id;delete $6.$lzc$bind_name;this.cloneAttrs=$6;if($0.datacontrolsvisibility){
this.visible=true
}else{
if(!$2.isinited){
var $7=this.__LZgetInstanceAttr($2,"visible");if(typeof $7=="boolean"||(Boolean["$lzsc$isa"]?Boolean.$lzsc$isa($7):$7 instanceof Boolean)){
this.visible=$7
}else{
this.visible=$2.visible
}}else{
this.visible=$2.visible
}};if($1.pooling!=null){
this.pooling=$1.pooling
};var $8=this.__LZgetInstanceAttr($2,"datapath");if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($8):$8 instanceof LzAlwaysExpr){
var $9=$8;this.__LZxpathconstr=$2[$9.methodName];this.__LZxpathdepend=$2[$9.dependenciesName];this.__LZpreventXPathUpdate=true;this.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr","__LZxpathdepend"));this.__LZpreventXPathUpdate=false;if(this.pooling){
$2.releaseConstraintMethod($9.methodName)
}}else{
var $a=this.__LZgetInstanceAttr($0,"xpath");if(LzAlwaysExpr["$lzsc$isa"]?LzAlwaysExpr.$lzsc$isa($a):$a instanceof LzAlwaysExpr){
var $b=new LzRefNode(this);var $c=$a;$b.__LZxpathconstr=$0[$c.methodName];$b.__LZxpathdepend=$0[$c.dependenciesName];this.__LZpreventXPathUpdate=true;$b.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr","__LZxpathdepend"));this.__LZpreventXPathUpdate=false;if(this.pooling){
$0.releaseConstraintMethod($c.methodName)
}}};this.__LZsetCloneAttrs();if($2._instanceChildren){
this.cloneChildren=$2._instanceChildren.concat()
}else{
this.cloneChildren=[]
};var $d=$0.context;this.clones=[];this.clonePool=[];if(this.pooling){
$0.$lzc$set___LZmanager(this);this.clones.push($2);$2.immediateparent.addSubview($2)
}else{
this.destroyClone($2)
};this.setDataContext($d,$d instanceof LzDatapointer)
},"__LZgetInstanceAttr",function($0,$1){
var $2=$0._instanceAttrs;if($2&&($1 in $2)){
return $2[$1]
}else{
var $3=$0["constructor"].attributes;if($3&&($1 in $3)){
return $3[$1]
}};return void 0
},"__LZsetCloneAttrs",function(){},"__LZapplyArgs",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZapplyArgs"]||this.nextMethod(arguments.callee,"__LZapplyArgs")).call(this,$0,$1);if(this.__LZdeleted){
return
};this.__LZHandleMultiNodes(this.initialnodes);this.initialnodes=null;if(this.visible==false){
this.$lzc$set_visible(false)
}},"setDataContext",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};if($0==null&&this.immediateparent!=null&&this.immediateparent["datapath"]!=null){
$0=this.immediateparent.datapath;$1=true
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$0,$1)
},"getCloneNumber",function($0){
return this.clones[$0]
},"__LZHandleNoNodes",function(){
this.nodes=[];var $0=this.clones;while($0.length){
if(this.pooling){
this.poolClone()
}else{
var $1=$0.pop();this.destroyClone($1)
}}},"__LZHandleSingleNode",function($0){
this.__LZHandleMultiNodes([$0])
},"__LZHandleMultiNodes",function($0){
var $1=this.parent&&this.parent.layouts?this.parent.layouts:[];for(var $2=0;$2<$1.length;++$2){
$1[$2].lock()
};this.hasdata=true;var $3=this.nodes;this.nodes=$0;if(this.onnodes.ready)this.onnodes.sendEvent(this.nodes);if(this.__LZspecialDotDot)this.__LZsetupDotDot($0[0]);if(this.orderpath!=null&&this.nodes){
this.nodes=this.mergesort(this.nodes,0,this.nodes.length-1)
};this.__LZadjustVisibleClones($3,true);var $4=this.clones.length;for(var $2=0;$2<$4;$2++){
var $5=this.clones[$2];var $6=$2+this.__LZdataoffset;$5.clonenumber=$6;if(this.nodes){
$5.datapath.setClonePointer(this.nodes[$6])
};if($5.onclonenumber.ready)$5.onclonenumber.sendEvent($6)
};if(this.onclones.ready)this.onclones.sendEvent(this.clones);for(var $2=0;$2<$1.length;++$2){
$1[$2].unlock()
};return null
},"__LZadjustVisibleClones",function($0,$1){
var $2=this.__LZdiffArrays($0,this.nodes);if(!this.pooling){
while(this.clones.length>$2){
var $3=this.clones.pop();this.destroyClone($3)
}};lz.Instantiator.enableDataReplicationQueuing(this);while(this.nodes&&this.nodes.length>this.clones.length){
var $4=this.getNewClone();if(!$4)break;this.clones.push($4)
};lz.Instantiator.clearDataReplicationQueue(this);while(this.nodes&&this.nodes.length<this.clones.length){
this.poolClone()
}},"mergesort",function($0,$1,$2){
if($1<$2){
var $3=$1+Math.floor(($2-$1)/2);var $4=this.mergesort($0,$1,$3);var $5=this.mergesort($0,$3+1,$2)
}else if($0.length==0){
return []
}else{
return [$0[$1]]
};var $6=[];var $7=0;var $8=0;var $9=$4.length;var $a=$5.length;while($7<$9&&$8<$a){
if(this.orderf($5[$8],$4[$7])==1){
$6.push($5[$8++])
}else{
$6.push($4[$7++])
}};while($7<$9)$6.push($4[$7++]);while($8<$a)$6.push($5[$8++]);return $6
},"orderf",function($0,$1){
var $2=this.orderpath;this.p=$0;var $3=this.xpathQuery($2);this.p=$1;var $4=this.xpathQuery($2);this.p=null;if($3==null||$3=="")$3="\n";if($4==null||$4=="")$4="\n";return this.comparator($3,$4)
},"ascDict",function($0,$1){
if($0.toLowerCase()<$1.toLowerCase()){
return 1
}else{
return 0
}},"descDict",function($0,$1){
if($0.toLowerCase()>$1.toLowerCase()){
return 1
}else{
return 0
}},"setOrder",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.orderpath=null;if($1!=null){
this.setComparator($1)
};this.orderpath=$0;if(this.nodes&&this.nodes.length){
this.__LZHandleMultiNodes(this.nodes)
}},"setComparator",function($0){
if($0=="descending"){
$0=this.descDict
}else if($0=="ascending"){
$0=this.ascDict
}else if(Function["$lzsc$isa"]?Function.$lzsc$isa($0):$0 instanceof Function){};this.comparator=$0;if(this.orderpath!=null&&this.nodes&&this.nodes.length){
this.__LZHandleMultiNodes(this.nodes)
}},"getNewClone",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(!this.cloneParent){
return null
};if(this.clonePool.length){
var $1=this.reattachClone(this.clonePool.pop())
}else{
var $1=new (this.cloneClass)(this.cloneParent,this.cloneAttrs,this.cloneChildren,$0==null?this.asyncnew:!$0)
};if(this.visible==false)$1.$lzc$set_visible(false);return $1
},"poolClone",function(){
var $0=this.clones.pop();this.detachClone($0);this.clonePool.push($0)
},"destroyClone",function($0){
$0.destroy()
},"$lzc$set_datapath",function($0){
this.setXPath($0)
},"setXPath",function($0){
if(this.__LZpreventXPathUpdate)return false;return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setXPath"]||this.nextMethod(arguments.callee,"setXPath")).apply(this,arguments)
},"handleDeletedNode",function($0){
var $1=this.clones[$0];if(this.pooling){
this.detachClone($1);this.clonePool.push($1)
}else{
this.destroyClone($1)
};this.nodes.splice($0,1);this.clones.splice($0,1)
},"getCloneForNode",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};var $2=this.clones;var $3=$2.length;for(var $4=0;$4<$3;$4++){
if($2[$4].datapath.p==$0){
return $2[$4]
}};return null
},"toString",function(){
return "ReplicationManager in "+this.immediateparent
},"setVisible",function($0){
this.$lzc$set_visible($0)
},"$lzc$set_visible",function($0){
this.visible=$0;var $1=this.clones;var $2=$1.length;for(var $3=0;$3<$2;$3++){
$1[$3].$lzc$set_visible($0)
};if(this.onvisible.ready)this.onvisible.sendEvent($0)
},"__LZcheckChange",function($0){
this.p=this.nodes[0];var $1=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZcheckChange"]||this.nextMethod(arguments.callee,"__LZcheckChange")).call(this,$0);this.p=null;if(!$1){
var $2=$0.who;var $3=this.clones;var $4=$3.length;for(var $5=0;$5<$4;$5++){
var $6=$3[$5];var $7=$6.datapath;if($7.__LZneedsOpUpdate($0)){
$7.__LZsetData()
};if($2.childOfNode($7.p,true)){
if($7.onDocumentChange.ready)$7.onDocumentChange.sendEvent($0)
}}};return false
},"__LZneedsOpUpdate",function($0){
switch(arguments.length){
case 0:
$0=null;

};return false
},"getContext",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.nodes[0]
},"detachClone",function($0){
if($0.isdetatchedclone)return;$0.$lzc$set_visible(false);$0.addedToParent=false;var $1=$0.immediateparent.subviews;for(var $2=$1.length-1;$2>=0;$2--){
if($1[$2]==$0){
$1.splice($2,1);break
}};$0.datapath.__LZtrackDel.unregisterAll();var $3=$0.immediateparent.onremovesubview;if($3.ready)$3.sendEvent($0);$0.isdetatchedclone=true;$0.datapath.p=null
},"reattachClone",function($0){
if(!$0.isdetatchedclone)return $0;$0.immediateparent.addSubview($0);$0.$lzc$set_visible(this.visible);$0.isdetatchedclone=false;return $0
},"__LZdiffArrays",function($0,$1){
var $2=0;var $3=$0?$0.length:0;var $4=$1?$1.length:0;while($2<$3&&$2<$4){
if($0[$2]!=$1[$2]){
return $2
};$2++
};return $2
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($0){
switch(arguments.length){
case 0:
$0=false;

};var $1=this.clones;var $2=$1.length;for(var $3=0;$3<$2;$3++){
$1[$3].datapath.updateData()
}}],null);lz.ReplicationManager=LzReplicationManager;Class.make("LzRefNode",LzNode,["__LZxpathconstr",null,"__LZxpathdepend",null,"xpath",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"$lzc$set_xpath",function($0){
this.parent.$lzc$set_xpath($0)
}],null);Class.make("LzDataAttrBind",LzDatapointer,["$lzsc$initialize",function($0,$1,$2,$3){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0);this.type=$3;this.setAttr=$1;this.pathparent=$0;this.node=$0.immediateparent;this.setXPath($2);this.rerunxpath=true;if($0.__LZdepChildren==null){
$0.__LZdepChildren=[this]
}else{
$0.__LZdepChildren.push(this)
}},"$pathbinding",true,"setAttr",void 0,"pathparent",void 0,"node",void 0,"type",void 0,"__LZlast",-1,"__LZsendUpdate",function($0,$1){
switch(arguments.length){
case 0:
$0=false;
case 1:
$1=false;

};var $2=this.__LZpchanged;var $3=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsendUpdate"]||this.nextMethod(arguments.callee,"__LZsendUpdate")).call(this,$0,$1);if($3){
var $4=this.data;var $5=this.node;var $6=this.setAttr;if($4==null){
$4=null
};var $7=$5.acceptTypeValue(this.type,$4);if($2||$5[$6]!==$7||!$5.inited||this.parsedPath.operator=="attributes"){
{
if(!$5.__LZdeleted){
var $lzsc$989640260="$lzc$set_"+$6;if(Function["$lzsc$isa"]?Function.$lzsc$isa($5[$lzsc$989640260]):$5[$lzsc$989640260] instanceof Function){
$5[$lzsc$989640260]($7)
}else{
$5[$6]=$7;var $lzsc$1387753880=$5["on"+$6];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1387753880):$lzsc$1387753880 instanceof LzEvent){
if($lzsc$1387753880.ready){
$lzsc$1387753880.sendEvent($7)
}}}}}}};return $3
},"unregisterAll",function(){
var $0=this.pathparent.__LZdepChildren;if($0!=null){
for(var $1=0;$1<$0.length;$1++){
if($0[$1]===this){
$0.splice($1,1);break
}}};this.destroy()
},"setDataContext",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setDataContext"]||this.nextMethod(arguments.callee,"setDataContext")).call(this,$0||this.pathparent,$1)
},"updateData",function(){
this.__LZupdateData()
},"__LZupdateData",function($0){
switch(arguments.length){
case 0:
$0=false;

};var $1=this.parsedPath.operator;if($1!=null){
var $2=this.node.presentAttribute(this.setAttr,this.type);if(this.data!=$2){
if($1=="name"){
this.setNodeName($2)
}else if($1=="text"){
this.setNodeText($2)
}else if($1=="attributes"){
this.p.$lzc$set_attributes($2)
}else{
this.setNodeAttribute($1.substring(11),$2)
}}}},"__LZHandleMultiNodes",function($0){
var $1=this.parsedPath;if($1&&$1.aggOperator=="last"){
this.__LZlast=$0.length;this.__LZHandleSingleNode($0[0]);return null
}else{
return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZHandleMultiNodes"]||this.nextMethod(arguments.callee,"__LZHandleMultiNodes")).call(this,$0)
}},"__LZgetLast",function(){
return this.__LZlast!=-1?this.__LZlast:(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZgetLast"]||this.nextMethod(arguments.callee,"__LZgetLast")).call(this)
},"runXPath",function(){
this.__LZlast=-1;return(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["runXPath"]||this.nextMethod(arguments.callee,"runXPath")).call(this)
},"toString",function(){
return "binder "+this.xpath
}],null);Class.make("LzLazyReplicationManager",LzReplicationManager,["sizeAxis",void 0,"cloneimmediateparent",void 0,"updateDel",void 0,"__LZoldnodelen",void 0,"viewsize",0,"totalsize",0,"mask",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"getDefaultPooling",function(){
return true
},"construct",function($0,$1){
if($1.pooling!=null){
$1.pooling=true
};if($1.axis!=null){
this.axis=$1.axis
};this.sizeAxis=this.axis=="x"?"width":"height";(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.mask=$0.immediateparent.immediateparent.mask;var $2;if(this.cloneAttrs.options!=null){
$2=new LzInheritedHash(this.cloneAttrs.options);$2["ignorelayout"]=true
}else{
$2={ignorelayout:true}};var $3=this.clones[0];if($3){
$3.setOption("ignorelayout",true);var $4=$3.immediateparent.layouts;if($4!=null){
for(var $5=0;$5<$4.length;$5++){
$4[$5].removeSubview($3)
}}};this.cloneAttrs.options=$2;var $6=this.getNewClone(true);this.cloneimmediateparent=$6.immediateparent;if(this.initialnodes){
$6.datapath.setClonePointer(this.initialnodes[1])
};this.viewsize=$6[this.sizeAxis];$6.datapath.setClonePointer(null);this.clones.push($6);if($1.spacing==null){
$1.spacing=0
};this.totalsize=this.viewsize+$1.spacing;{
var $lzsc$1398458233=this.axis;var $lzsc$939296608=this.totalsize;if(!$6.__LZdeleted){
var $lzsc$1555670498="$lzc$set_"+$lzsc$1398458233;if(Function["$lzsc$isa"]?Function.$lzsc$isa($6[$lzsc$1555670498]):$6[$lzsc$1555670498] instanceof Function){
$6[$lzsc$1555670498]($lzsc$939296608)
}else{
$6[$lzsc$1398458233]=$lzsc$939296608;var $lzsc$400604704=$6["on"+$lzsc$1398458233];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$400604704):$lzsc$400604704 instanceof LzEvent){
if($lzsc$400604704.ready){
$lzsc$400604704.sendEvent($lzsc$939296608)
}}}}};this.__LZdataoffset=0;this.updateDel=new LzDelegate(this,"__LZhandleUpdate");this.updateDel.register(this.cloneimmediateparent,"on"+this.axis);this.updateDel.register(this.mask,"on"+this.sizeAxis)
},"__LZhandleUpdate",function($0){
this.__LZadjustVisibleClones(null,null)
},"__LZsetCloneAttrs",function(){
var $0;if(this.cloneAttrs.options!=null){
$0=new LzInheritedHash(this.cloneAttrs.options);$0["ignorelayout"]=true
}else{
$0={ignorelayout:true}};this.cloneAttrs.options=$0
},"__LZHandleNoNodes",function(){
this.__LZHandleMultiNodes([])
},"__LZadjustVisibleClones",function($0,$1){
var $2=this.cloneimmediateparent;var $3=this.nodes;var $4=this.axis;var $5=this.sizeAxis;var $6=this.totalsize;if($3){
var $7=$3.length;if(this.__LZoldnodelen!=$7){
{
var $lzsc$881258457=$7*$6-this.spacing;if(!$2.__LZdeleted){
var $lzsc$45377333="$lzc$set_"+$5;if(Function["$lzsc$isa"]?Function.$lzsc$isa($2[$lzsc$45377333]):$2[$lzsc$45377333] instanceof Function){
$2[$lzsc$45377333]($lzsc$881258457)
}else{
$2[$5]=$lzsc$881258457;var $lzsc$1139119856=$2["on"+$5];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1139119856):$lzsc$1139119856 instanceof LzEvent){
if($lzsc$1139119856.ready){
$lzsc$1139119856.sendEvent($lzsc$881258457)
}}}}};this.__LZoldnodelen=$7
}};if(!(this.mask&&this.mask["hasset"+$5]))return;var $8=0;if($6!=0){
$8=Math.floor(-$2[$4]/$6);if(0>$8)$8=0
};var $9=0;var $a=this.clones.length;var $b=$8-this.__LZdataoffset;var $c=$8*$6+$2[$4];var $d=0;if(typeof $c=="number"){
$d=1+Math.floor((this.mask[$5]-$c)/$6)
};if($3!=null){
var $7=$3.length;if($d+$8>$7){
$d=$7-$8
}};if($b==0&&$d==$a)return;lz.Instantiator.enableDataReplicationQueuing(this);var $e=this.clones;this.clones=[];for(var $f=0;$f<$d;$f++){
var $g=null;if($f+$b<0){
if($d+$b<$a&&$a>0){
$g=$e[--$a]
}else{
$g=this.getNewClone()
}}else if($f+$b>=$a){
if($9<$b&&$9<$a){
$g=$e[$9++]
}else{
$g=this.getNewClone()
}};if($g){
this.clones[$f]=$g;{
var $lzsc$337434952=($f+$8)*$6;if(!$g.__LZdeleted){
var $lzsc$2062762159="$lzc$set_"+$4;if(Function["$lzsc$isa"]?Function.$lzsc$isa($g[$lzsc$2062762159]):$g[$lzsc$2062762159] instanceof Function){
$g[$lzsc$2062762159]($lzsc$337434952)
}else{
$g[$4]=$lzsc$337434952;var $lzsc$1426930883=$g["on"+$4];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1426930883):$lzsc$1426930883 instanceof LzEvent){
if($lzsc$1426930883.ready){
$lzsc$1426930883.sendEvent($lzsc$337434952)
}}}}};$g.clonenumber=$8+$f;if($3){
$g.datapath.setClonePointer($3[$8+$f])
};if($g.onclonenumber.ready)$g.onclonenumber.sendEvent($f)
}else{
this.clones[$f]=$e[$f+$b]
}};var $h=this.clonePool;while($9<$b&&$9<$a){
var $i=$e[$9++];this.detachClone($i);$h.push($i)
};while($a>$d+$b&&$a>0){
var $i=$e[--$a];this.detachClone($i);$h.push($i)
};this.__LZdataoffset=$8;lz.Instantiator.clearDataReplicationQueue(this)
},"toString",function(){
return "Lazy clone manager in "+this.cloneimmediateparent
},"getCloneForNode",function($0,$1){
switch(arguments.length){
case 1:
$1=false;

};var $2=(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["getCloneForNode"]||this.nextMethod(arguments.callee,"getCloneForNode")).call(this,$0)||null;if(!$2&&!$1){
$2=this.getNewClone();$2.datapath.setClonePointer($0);this.detachClone($2);this.clonePool.push($2)
};return $2
},"getCloneNumber",function($0){
return this.getCloneForNode(this.nodes[$0])
}],null);lz.LazyReplicationManager=LzLazyReplicationManager;Class.make("LzResizeReplicationManager",LzLazyReplicationManager,["datasizevar",void 0,"__LZresizeupdating",void 0,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 2:
$2=null;
case 3:
$3=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3)
},"getDefaultPooling",function(){
return false
},"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.datasizevar="$"+this.getUID()+"track"
},"__LZHandleCloneResize",function($0){
var $1=this.datapath.p;if($1){
var $2=this.cloneManager;var $3=$2.datasizevar;var $4=$1.getUserData($3)||$2.viewsize;if($0!=$4){
$1.setUserData($3,$0);$2.__LZadjustVisibleClones(null,false)
}}},"__LZsetCloneAttrs",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["__LZsetCloneAttrs"]||this.nextMethod(arguments.callee,"__LZsetCloneAttrs")).call(this);var $0=this.cloneAttrs;$0.__LZHandleCloneResize=this.__LZHandleCloneResize;if(!$0["$delegates"]){
$0.$delegates=[]
};$0.$delegates.push("on"+(this.axis=="y"?"height":"width"),"__LZHandleCloneResize",null)
},"getPositionByNode",function($0){
var $1=-this.spacing;var $2;if(this.nodes!=null){
for(var $3=0;$3<this.nodes.length;$3++){
$2=this.nodes[$3];if($0==this.nodes[$3]){
return $1+this.spacing
};$1+=this.spacing+($2.getUserData(this.datasizevar)||this.viewsize)
}};return undefined
},"__LZreleaseClone",function($0){
this.detachClone($0);this.clonePool.push($0)
},"__LZadjustVisibleClones",function($0,$1){
if(!this.mask["hasset"+this.sizeAxis])return;if(this.__LZresizeupdating)return;this.__LZresizeupdating=true;var $2=this.nodes!=null?this.nodes.length:0;var $3=Math.floor(-this.cloneimmediateparent[this.axis]);if(0>$3)$3=0;var $4=this.mask[this.sizeAxis];var $5=-1;var $6=this.__LZdataoffset;if($1){
while(this.clones.length)this.poolClone();var $7=null;var $8=0
}else{
var $7=this.clones;var $8=$7.length
};this.clones=[];var $9=-this.spacing;var $a=false;var $b=-1;var $c;var $d=true;for(var $e=0;$e<$2;$e++){
var $f=this.nodes[$e];var $g=$f.getUserData(this.datasizevar);var $h=$g==null?this.viewsize:$g;$9+=this.spacing;if(!$a&&$5==-1&&$9-$3+$h>=0){
$d=$e!=0;$a=true;$c=$9;$5=$e;var $i=$e-$6;$i=$i>$8?$8:$i;if($i>0){
for(var $j=0;$j<$i;$j++){
var $k=$7[$j];this.__LZreleaseClone($k)
}}}else if($a&&$9-$3>$4){
$a=false;$b=$e-$5;var $l=$e-$6;$l=$l<0?0:$l;if($l<$8){
for(var $j=$l;$j<$8;$j++){
var $k=$7[$j];this.__LZreleaseClone($k)
}}};if($a){
if($e>=$6&&$e<$6+$8){
var $m=$7[$e-$6]
}else{
var $m=null
};this.clones[$e-$5]=$m
};$9+=$h
};var $n=$c;if($d)$n+=this.spacing;for(var $e=0;$e<this.clones.length;$e++){
var $f=this.nodes[$e+$5];var $m=this.clones[$e];if(!$m){
$m=this.getNewClone();$m.clonenumber=$e+$5;$m.datapath.setClonePointer($f);if($m.onclonenumber.ready)$m.onclonenumber.sendEvent($e+$5);this.clones[$e]=$m
};this.clones[$e]=$m;{
var $lzsc$144334502=this.axis;if(!$m.__LZdeleted){
var $lzsc$11016893="$lzc$set_"+$lzsc$144334502;if(Function["$lzsc$isa"]?Function.$lzsc$isa($m[$lzsc$11016893]):$m[$lzsc$11016893] instanceof Function){
$m[$lzsc$11016893]($n)
}else{
$m[$lzsc$144334502]=$n;var $lzsc$1703393383=$m["on"+$lzsc$144334502];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1703393383):$lzsc$1703393383 instanceof LzEvent){
if($lzsc$1703393383.ready){
$lzsc$1703393383.sendEvent($n)
}}}}};var $g=$f.getUserData(this.datasizevar);var $h=$g==null?this.viewsize:$g;if($m[this.sizeAxis]!=$h){
{
var $lzsc$1580831369=this.sizeAxis;if(!$m.__LZdeleted){
var $lzsc$1846498257="$lzc$set_"+$lzsc$1580831369;if(Function["$lzsc$isa"]?Function.$lzsc$isa($m[$lzsc$1846498257]):$m[$lzsc$1846498257] instanceof Function){
$m[$lzsc$1846498257]($h)
}else{
$m[$lzsc$1580831369]=$h;var $lzsc$12113683=$m["on"+$lzsc$1580831369];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$12113683):$lzsc$12113683 instanceof LzEvent){
if($lzsc$12113683.ready){
$lzsc$12113683.sendEvent($h)
}}}}}};$n+=$h+this.spacing
};this.__LZdataoffset=$5;{
var $lzsc$1212375822=this.cloneimmediateparent;var $lzsc$65588763=this.sizeAxis;if(!$lzsc$1212375822.__LZdeleted){
var $lzsc$753273024="$lzc$set_"+$lzsc$65588763;if(Function["$lzsc$isa"]?Function.$lzsc$isa($lzsc$1212375822[$lzsc$753273024]):$lzsc$1212375822[$lzsc$753273024] instanceof Function){
$lzsc$1212375822[$lzsc$753273024]($9)
}else{
$lzsc$1212375822[$lzsc$65588763]=$9;var $lzsc$2086529331=$lzsc$1212375822["on"+$lzsc$65588763];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$2086529331):$lzsc$2086529331 instanceof LzEvent){
if($lzsc$2086529331.ready){
$lzsc$2086529331.sendEvent($9)
}}}}};this.__LZresizeupdating=false
}],null);lz.ResizeReplicationManager=LzResizeReplicationManager;Class.make("LzColorUtils",null,null,["__cache",{counter:0},"stringToColor",function($0){
if(typeof $0!="string")return $0;if($0 in lz.colors)return lz.colors[$0];if($0 in global)return global[$0];if($0.indexOf("rgb")!=-1)return LzColorUtils.fromrgb($0);var $1=Number($0);if(isNaN($1)){
return $0
}else{
return $1
}},"fromrgb",function($0){
if(typeof $0!="string")return $0;if($0.indexOf("rgb")==-1)return LzColorUtils.stringToColor($0);var $1=$0.substring($0.indexOf("(")+1,$0.indexOf(")")).split(",");var $2=($1[0]<<16)+($1[1]<<8)+$1[2]*1;if($1.length>3){
$2+=$1[3]*0.01
};if(typeof $2=="number")return $2;return 0
},"dectohex",function($0,$1){
switch(arguments.length){
case 1:
$1=0;

};if(typeof $0!="number")return $0;$0=$0&16777215;var $2=$0.toString(16);var $3=$1-$2.length;while($3>0){
$2="0"+$2;$3--
};return $2
},"torgb",function($0){
if(typeof $0=="string"&&$0.indexOf("rgb")!=-1)return $0;var $1=LzColorUtils.inttohex($0);if(typeof $1!="string")return $1;if(typeof $0=="number"||lz.colors[$0]!=null)$0=$1;var $2=LzColorUtils.__cache;var $3="torgb"+$1;if(!$2[$3]){
if($2.counter++>1000){
$2={counter:0}};if($0.length<6){
$0="#"+$0.charAt(1)+$0.charAt(1)+$0.charAt(2)+$0.charAt(2)+$0.charAt(3)+$0.charAt(3)+($0.length>4?$0.charAt(4)+$0.charAt(4):"")
};$2[$3]=($0.length>7?"rgba(":"rgb(")+parseInt($0.substring(1,3),16)+","+parseInt($0.substring(3,5),16)+","+parseInt($0.substring(5,7),16)+($0.length>7?","+parseInt($0.substring(7),16)/255:"")+")"
};return $2[$3]
},"tohsv",function($0){
var $1=($0>>16&255)/255,$2=($0>>8&255)/255,$3=($0&255)/255;var $4=Math.min($1,Math.min($2,$3)),$5=Math.max($1,Math.max($2,$3));var $6=$5;var $7=$5-$4;if($7==0){
return {h:0,s:0,v:$6}};var $8=$7/$5;if($1==$5){
var $9=($2-$3)/$7
}else if($2==$5){
var $9=2+($3-$1)/$7
}else{
var $9=4+($1-$2)/$7
};$9*=60;if($9<0){
$9+=360
};return {h:$9,s:$8,v:$6}},"fromhsv",function($0,$1,$2){
var $3=$0/60;var $4=Math.floor($3);var $5=$4%6;var $6=$3-$4;var $7=$2*(1-$1);var $8=$2*(1-$6*$1);var $3=$2*(1-(1-$6)*$1);var $9,$a,$b;switch($5){
case 0:
$9=$2;$a=$3;$b=$7;break;
case 1:
$9=$8;$a=$2;$b=$7;break;
case 2:
$9=$7;$a=$2;$b=$3;break;
case 3:
$9=$7;$a=$8;$b=$2;break;
case 4:
$9=$3;$a=$7;$b=$2;break;
case 5:
$9=$2;$a=$7;$b=$8;break;

};return $9*255<<16|$a*255<<8|$b*255
},"convertColor",function($0){
if($0=="null"||$0==null)return null;return LzColorUtils.hextoint($0)
},"hextoint",function($0){
var $1=LzColorUtils.stringToColor($0);if(typeof $1!="string")return $1;var $2=$0;$2=$2.slice(1);var $3=0;if($2.length>6){
$3=parseInt($2.slice(6),16)/25500;$2=$2.slice(0,6)
};var $1=parseInt($2,16);switch($2.length){
case 3:
return(($1&3840)<<8|($1&240)<<4|$1&15)*17+$3;
case 6:
return $1+$3;
default:
break;

};return 0
},"inttohex",function($0,$1){
switch(arguments.length){
case 1:
$1=6;

};var $2=LzColorUtils.stringToColor($0);if(typeof $2!="number")return $2;var $3=LzColorUtils.__cache;var $4="inttohex"+$2;if(!$3[$4]){
if($3.counter++>1000){
$3={counter:0}};var $5=$2-Math.floor($2);var $6="";if($5!=0){
$5=Math.floor($5*25600);$6=$5.toString(16);if($6.length==1){
$6="0"+$6
}};$3[$4]="#"+LzColorUtils.dectohex($2,$1)+$6
};return $3[$4]
},"inttocolorobj",function($0){
var $1=LzColorUtils.hextoint($0);var $2=LzColorUtils.__cache;var $3="inttocolorobj"+$1;if(!$2[$3]){
if($2.counter++>1000){
$2={counter:0}};var $4=$1|0;var $5=$1-$4;if($5==0){
$5=null
}else{
$5*=100
};$2[$3]={color:$4,alpha:$5}};return $2[$3]
},"rgbatoint",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==0){
return null
}}]);Class.make("LzUtilsClass",null,["__SimpleExprPattern",void 0,"__ElementPattern",void 0,"$lzsc$initialize",function(){
this.__SimpleExprPattern=new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$");this.__ElementPattern=new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)","g")
},"color",{hextoint:function($0){
return LzColorUtils.hextoint($0)
},inttohex:function($0){
return LzColorUtils.inttohex($0)
},torgb:function($0){
return LzColorUtils.torgb($0)
}},"hextoint",function($0){
return LzColorUtils.hextoint($0)
},"inttohex",function($0,$1){
switch(arguments.length){
case 1:
$1=6;

};return LzColorUtils.inttohex($0,$1)
},"dectohex",function($0,$1){
switch(arguments.length){
case 1:
$1=0;

};return LzColorUtils.dectohex($0,$1)
},"stringToColor",function($0){
return LzColorUtils.stringToColor($0)
},"torgb",function($0){
return LzColorUtils.torgb($0)
},"fromrgb",function($0){
return LzColorUtils.fromrgb($0)
},"colornames",lz.colors,"__unpackList",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($0=="")return [];if($1==null)$1=canvas;var $2=$0.split(",");for(var $3=0;$3<$2.length;$3++){
var $4=$2[$3];if($4=="")continue;while($4.charAt(0)==" "){
$4=$4.substring(1,$4.length)
};var $5=parseFloat($4);if(!isNaN($5)){
$2[$3]=$5
}else if($4.indexOf("'")!=-1){
var $6=$4.indexOf("'")+1;var $7=$4.lastIndexOf("'");$2[$3]=$4.substring($6,$7)
}else if($4=="true"||$4=="false"){
$2[$3]=$4=="true"
}else if($1[$4]){
$2[$3]=$1[$4]
}};return $2
},"safeEval",function($0){
if($0.indexOf("new ")==0)return this.safeNew($0);var $1=$0.indexOf("(");var $2=null;if($1!=-1){
var $3=$0.lastIndexOf(")");$2=$0.substring($1+1,$3);$0=$0.substring(0,$1)
};var $4=null,$5;if($0.match(this.__SimpleExprPattern)){
var $6=$0.match(this.__ElementPattern);$5=globalValue($6[0]);for(var $7=1,$8=$6.length;$7<$8;$7++){
$4=$5;$5=$5[$6[$7]]
}};if($2==null){
return $5
};var $9=lz.Utils.__unpackList($2,$4);if($5){
var $a=$5.apply($4,$9);return $a
}},"safeNew",function($0){
var $1=$0;var $2=$0.indexOf("new ");if($2==-1)return $0;$0=$0.substring($2+4);var $3=$0.indexOf("(");if($3!=-1){
var $4=$0.indexOf(")");var $5=$0.substring($3+1,$4);$0=$0.substring(0,$3)
};var $6=globalValue($0);if(!$6)return;var $5=lz.Utils.__unpackList($5);var $7=$5.length;if($7==0){
return new $6()
}else if($7==1){
return new $6($5[0])
}else if($7==2){
return new $6($5[0],$5[1])
}else if($7==3){
return new $6($5[0],$5[1],$5[2])
}else if($7==4){
return new $6($5[0],$5[1],$5[2],$5[3])
}else if($7==5){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4])
}else if($7==6){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5])
}else if($7==7){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5],$5[6])
}else if($7==8){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5],$5[6],$5[7])
}else if($7==9){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5],$5[6],$5[7],$5[8])
}else if($7==10){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5],$5[6],$5[7],$5[8],$5[9])
}else if($7==11){
return new $6($5[0],$5[1],$5[2],$5[3],$5[4],$5[5],$5[6],$5[7],$5[8],$5[9],$5[10])
}}],null);lz.Utils=new LzUtilsClass();var LzUtils=lz.Utils;Class.make("LzInstantiatorService",LzEventable,["checkQDel",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.checkQDel=new LzDelegate(this,"checkQ")
},"halted",false,"isimmediate",false,"isdatareplicating",false,"istrickling",false,"isUpdating",false,"safe",true,"timeout",500,"makeQ",[],"trickleQ",[],"tricklingQ",[],"datareplQ",null,"dataQ",[],"syncNew",true,"trickletime",10,"setSafeInstantiation",function($0){
this.safe=$0;if(this.instanceQ.length){
this.timeout=Infinity
}},"requestInstantiation",function($0,$1){
if(this.isimmediate){
this.createImmediate($0,$1.concat())
}else{
var $2=this.newReverseArray($1);if(this.isdatareplicating){
this.datareplQ.push($2,$0)
}else if(this.istrickling){
this.tricklingQ.push($0,$2)
}else{
this.makeQ.push($0,$2);this.checkUpdate()
}}},"enableDataReplicationQueuing",function($0){
if(this.isdatareplicating){
this.dataQ.push(this.datareplQ)
}else{
this.isdatareplicating=true
};this.datareplQ=[]
},"clearDataReplicationQueue",function($0){
var $1=this.datareplQ;if(this.dataQ.length>0){
this.datareplQ=this.dataQ.pop()
}else{
this.isdatareplicating=false;this.datareplQ=null
};var $2=$0.cloneParent;var $3=this.makeQ;var $4=$3.length;var $5=$4;for(var $6=0;$6<$4;$6+=2){
if($3[$6].parent===$2){
$5=$6;break
}};$1.push(0,$5);$1.reverse();$3.splice.apply($3,$1);this.checkUpdate()
},"newReverseArray",function($0){
var $1=$0.length;var $2=Array($1);for(var $3=0,$4=$1-1;$3<$1;){
$2[$3++]=$0[$4--]
};return $2
},"checkUpdate",function(){
if(!(this.isUpdating||this.halted)){
this.checkQDel.register(lz.Idle,"onidle");this.isUpdating=true
}},"checkQ",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(!this.makeQ.length){
if(!this.tricklingQ.length){
if(!this.trickleQ.length){
this.checkQDel.unregisterAll();this.isUpdating=false;return
}else{
var $1=this.trickleQ.shift();var $2=this.trickleQ.shift();this.tricklingQ.push($1,this.newReverseArray($2))
}};this.istrickling=true;this.makeSomeViews(this.tricklingQ,this.trickletime);this.istrickling=false
}else{
canvas.creatednodes+=this.makeSomeViews(this.makeQ,this.timeout);if(canvas.updatePercentCreatedEnabled){
canvas.updatePercentCreated()
}}},"makeSomeViews",function($0,$1){
var $2=new Date().getTime();var $3=0;while(new Date().getTime()-$2<$1&&$0.length){
var $4=$0.length;var $5=$0[$4-1];var $6=$0[$4-2];var $7=false;if($6["__LZdeleted"]||$5[0]&&$5[0]["__LZdeleted"]){
$0.length-=2;continue
};try{
while(new Date().getTime()-$2<$1){
if($4!=$0.length){
break
};if(!$5.length){
$7=true;break
};var $8=$5.pop();if($8){
$6.makeChild($8,true);$3++
}}}
finally{};if($7){
$0.length=$4-2;$6.__LZinstantiationDone()
}};return $3
},"trickleInstantiate",function($0,$1){
this.trickleQ.push($0,$1);this.checkUpdate()
},"createImmediate",function($0,$1){
var $2=this.newReverseArray($1);var $3=this.isimmediate;this.isimmediate=true;this.makeSomeViews([$0,$2],Infinity);this.isimmediate=$3
},"completeTrickle",function($0){
if(this.tricklingQ[0]==$0){
var $1=this.isimmediate;this.isimmediate=true;this.makeSomeViews(this.tricklingQ,Infinity);this.isimmediate=$1;this.tricklingQ=[]
}else{
var $2=this.trickleQ;var $3=$2.length;for(var $4=0;$4<$3;$4+=2){
if($2[$4]==$0){
var $5=$2[$4+1];$2.splice($4,2);this.createImmediate($0,$5);return
}}}},"halt",function(){
this.isUpdating=false;this.halted=true;this.checkQDel.unregisterAll()
},"resume",function(){
this.halted=false;this.checkUpdate()
},"drainQ",function($0){
var $1=this.timeout;var $2=this.trickletime;var $3=this.halted;this.timeout=$0;this.trickletime=$0;this.halted=false;this.isUpdating=true;this.checkQ();this.halted=$3;this.timeout=$1;this.trickletime=$2;return !this.isUpdating
}],["LzInstantiator",void 0]);(function($0){
with($0)with($0.prototype){
{
LzInstantiatorService.LzInstantiator=new LzInstantiatorService()
}}})(LzInstantiatorService);lz.Instantiator=LzInstantiatorService.LzInstantiator;Class.make("LzGlobalMouseService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"onmousemove",LzDeclaredEvent,"onmouseup",LzDeclaredEvent,"onmouseupoutside",LzDeclaredEvent,"onmouseover",LzDeclaredEvent,"onmouseout",LzDeclaredEvent,"onmousedown",LzDeclaredEvent,"onmousedragin",LzDeclaredEvent,"onmousedragout",LzDeclaredEvent,"onmouseleave",LzDeclaredEvent,"onmouseenter",LzDeclaredEvent,"onclick",LzDeclaredEvent,"ondblclick",LzDeclaredEvent,"__movecounter",0,"__mouseEvent",function($0,$1){
if($0=="onmouseleave"){
canvas.onmouseleave.sendEvent()
}else if($0=="onmousemove"){
this.__movecounter++
};var $2=this[$0];if($2){
if($2.ready){
$2.sendEvent($1)
}}}],["LzGlobalMouse",void 0]);(function($0){
with($0)with($0.prototype){
{
LzGlobalMouseService.LzGlobalMouse=new LzGlobalMouseService()
}}})(LzGlobalMouseService);lz.GlobalMouseService=LzGlobalMouseService;lz.GlobalMouse=LzGlobalMouseService.LzGlobalMouse;Class.make("LzBrowserService",null,["capabilities",LzSprite.prototype.capabilities,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"loadURL",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;

};LzBrowserKernel.loadURL($0,$1,$2)
},"loadJS",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};LzBrowserKernel.loadJS.apply(null,arguments)
},"callJS",function($0,$1,$2){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;

};try{
return LzBrowserKernel.callJS.apply(null,arguments)
}
catch($3){
return null
}},"getVersion",function(){
return LzBrowserKernel.getVersion()
},"getOS",function(){
return LzBrowserKernel.getOS()
},"getLoadURL",function(){
return LzBrowserKernel.getLoadURL()
},"getInitArg",function($0){
return LzBrowserKernel.getInitArg($0)
},"getAppID",function(){
return LzBrowserKernel.getAppID()
},"showMenu",function($0){
if(this.capabilities.runtimemenus){
LzBrowserKernel.showMenu($0)
}},"setClipboard",function($0){
if(this.capabilities.setclipboard){
LzBrowserKernel.setClipboard($0)
}},"isAAActive",function(){
if(this.capabilities.accessibility){
return LzBrowserKernel.isAAActive()
}else{
return false
}},"updateAccessibility",function(){
if(this.capabilities.accessibility){
LzBrowserKernel.updateAccessibility()
}},"loadProxyPolicy",function($0){
if(this.capabilities.proxypolicy){
LzBrowserKernel.loadProxyPolicy($0)
}},"postToLps",true,"parsedloadurl",null,"defaultPortNums",{http:80,https:443},"getBaseURL",function($0,$1){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;

};var $2=this.getLoadURLAsLzURL();if($0){
$2.protocol="https"
};if($1){
$2.port=$1
}else if($0&&$1==null){
$2.port=this.defaultPortNums[$2.protocol]
};$2.query=null;return $2
},"getLoadURLAsLzURL",function(){
if(!this.parsedloadurl){
this.parsedloadurl=new LzURL(this.getLoadURL())
};return this.parsedloadurl.dupe()
},"toAbsoluteURL",function($0,$1){
if($0.indexOf("://")>-1||$0.indexOf("/@WEBAPP@/")==0||$0.indexOf("file:")==0){
return $0
};var $2=this.getLoadURLAsLzURL();$2.query=null;if($0.indexOf(":")>-1){
var $3=$0.indexOf(":");var $4=$2.protocol=="https";$2.protocol=$0.substring(0,$3);if($1||$4){
if($2.protocol=="http"){
$2.protocol="https"
}};var $5=$0.substring($3+1,$0.length);if($5.charAt(0)=="/"){
$2.path=$0.substring($3+1);$2.file=null
}else{
$2.file=$0.substring($3+1)
}}else{
if($0.charAt(0)=="/"){
$2.path=$0;$2.file=null
}else{
$2.file=$0
}};return $2.toString()
},"xmlEscape",function($0){
return LzDataElement.__LZXMLescape($0)
},"urlEscape",function($0){
return encodeURIComponent($0)
},"urlUnescape",function($0){
return decodeURIComponent($0)
},"usePost",function(){
return this.postToLps&&this.supportsPost()
},"supportsPost",function(){
return true
},"makeProxiedURL",function($0){
var $1=$0.headers;var $2=$0.postbody;var $3=$0.proxyurl;var $4=$0.serverproxyargs;var $5;if($4){
$5={url:this.toAbsoluteURL($0.url,$0.secure),lzt:$0.service,reqtype:$0.httpmethod.toUpperCase()};for(var $6 in $4){
$5[$6]=$4[$6]
}}else{
$5={url:this.toAbsoluteURL($0.url,$0.secure),lzt:$0.service,reqtype:$0.httpmethod.toUpperCase(),sendheaders:$0.sendheaders,trimwhitespace:$0.trimwhitespace,nsprefix:$0.trimwhitespace,timeout:$0.timeout,cache:$0.cacheable,ccache:$0.ccache}};if($2!=null){
$5.lzpostbody=$2
};$5.lzr=$runtime;if($1!=null){
var $7="";for(var $8 in $1){
$7+=$8+": "+$1[$8]+"\n"
};if($7!=""){
$5["headers"]=$7
}};if(!$0.ccache){
$5.__lzbc__=new Date().getTime()
};var $9="?";for(var $a in $5){
var $b=$5[$a];if(typeof $b=="string"){
$b=encodeURIComponent($b);$b=$b.replace(LzDataset.slashPat,"%2F")
};$3+=$9+$a+"="+$b;$9="&"
};return $3
}],["LzBrowser",void 0]);(function($0){
with($0)with($0.prototype){
{
LzBrowserService.LzBrowser=new LzBrowserService()
}}})(LzBrowserService);lz.BrowserService=LzBrowserService;lz.Browser=LzBrowserService.LzBrowser;Class.make("LzContextMenu",LzNode,["onmenuopen",LzDeclaredEvent,"kernel",null,"items",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 0:
$0=null;
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($0):$0 instanceof LzNode)?$0:null,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($0):$0 instanceof LzNode)?$1:{delegate:$0},$2,$3)
},"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.kernel=new LzContextMenuKernel(this);this.items=[];var $2=$1&&$1["delegate"]||null;delete $1["delegate"];this.$lzc$set_delegate($2)
},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);var $0=this.immediateparent;if($0&&(LzView["$lzsc$isa"]?LzView.$lzsc$isa($0):$0 instanceof LzView)){
$0.$lzc$set_contextmenu(this)
}},"$lzc$set_delegate",function($0){
this.kernel.setDelegate($0)
},"setDelegate",function($0){
this.$lzc$set_delegate($0)
},"addItem",function($0){
this.items.push($0);this.kernel.addItem($0)
},"hideBuiltInItems",function(){
this.kernel.hideBuiltInItems()
},"showBuiltInItems",function(){
this.kernel.showBuiltInItems()
},"clearItems",function(){
this.items=[];this.kernel.clearItems()
},"getItems",function(){
return this.items
},"makeMenuItem",function($0,$1){
var $2=new LzContextMenuItem($0,$1);return $2
}],["tagname","contextmenu","attributes",new LzInheritedHash(LzNode.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzContextMenu.attributes.ignoreplacement=true
}}})(LzContextMenu);lz[LzContextMenu.tagname]=LzContextMenu;Class.make("LzContextMenuItem",LzNode,["onselect",LzDeclaredEvent,"kernel",null,"$lzsc$initialize",function($0,$1,$2,$3){
switch(arguments.length){
case 1:
$1=null;
case 2:
$2=null;
case 3:
$3=false;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($0):$0 instanceof LzNode)?$0:null,(LzNode["$lzsc$isa"]?LzNode.$lzsc$isa($0):$0 instanceof LzNode)?$1:{title:$0,delegate:$1},$2,$3)
},"construct",function($0,$1){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);var $2=$1&&$1["title"]||"";delete $1["title"];var $3=$1&&$1["delegate"]||null;delete $1["delegate"];this.kernel=new LzContextMenuItemKernel(this,$2,$3);var $4=this.immediateparent;if($4&&(LzContextMenu["$lzsc$isa"]?LzContextMenu.$lzsc$isa($4):$4 instanceof LzContextMenu)){
$4.addItem(this)
}},"$lzc$set_delegate",function($0){
this.kernel.setDelegate($0)
},"$lzc$set_caption",function($0){
this.kernel.setCaption($0)
},"$lzc$set_enabled",function($0){
this.kernel.setEnabled($0)
},"$lzc$set_separatorbefore",function($0){
this.kernel.setSeparatorBefore($0)
},"$lzc$set_visible",function($0){
this.kernel.setVisible($0)
},"setDelegate",function($0){
this.$lzc$set_delegate($0)
},"setCaption",function($0){
this.$lzc$set_caption($0)
},"getCaption",function(){
return this.kernel.getCaption()
},"setEnabled",function($0){
this.$lzc$set_enabled($0)
},"setSeparatorBefore",function($0){
this.$lzc$set_separatorbefore($0)
},"setVisible",function($0){
this.$lzc$set_visible($0)
}],["tagname","contextmenuitem","attributes",new LzInheritedHash(LzNode.attributes)]);(function($0){
with($0)with($0.prototype){}})(LzContextMenuItem);lz[LzContextMenuItem.tagname]=LzContextMenuItem;Class.make("LzModeManagerService",LzEventable,["onmode",LzDeclaredEvent,"__LZlastclick",null,"__LZlastClickTime",0,"willCall",false,"eventsLocked",false,"modeArray",new Array(),"remotedebug",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);LzMouseKernel.setCallback(this,"rawMouseEvent")
},"makeModal",function($0){
if($0&&(this.modeArray.length==0||!this.hasMode($0))){
this.modeArray.push($0);if(this.onmode.ready)this.onmode.sendEvent($0);var $1=lz.Focus.getFocus();if($1&&!$1.childOf($0)){
lz.Focus.clearFocus()
}}},"release",function($0){
var $1=this.modeArray;for(var $2=$1.length-1;$2>=0;$2--){
if($1[$2]===$0){
$1.splice($2,$1.length-$2);var $3=$1[$2-1];if(this.onmode.ready)this.onmode.sendEvent($3||null);var $4=lz.Focus.getFocus();if($3&&$4&&!$4.childOf($3)){
lz.Focus.clearFocus()
};return
}}},"releaseAll",function(){
this.modeArray=new Array();if(this.onmode.ready)this.onmode.sendEvent(null)
},"handleMouseEvent",function($0,$1){
if($1=="onmouseup")lz.Track.__LZmouseup(null);if($0==null){
$0=this.__findInputtextSelection()
};lz.GlobalMouse.__mouseEvent($1,$0);if($0==null||this.eventsLocked){
return
};var $2=true;for(var $3=this.modeArray.length-1;$2&&$3>=0;--$3){
var $4=this.modeArray[$3];if(!$4){
continue
};if($0.childOf($4)){
break
}else{
$2=$4.passModeEvent?$4.passModeEvent($1,$0):false
}};if($2){
if($1=="onclick"){
if(this.__LZlastclick===$0&&$0.ondblclick.ready&&LzTimeKernel.getTimer()-this.__LZlastClickTime<$0.DOUBLE_CLICK_TIME){
$1="ondblclick";lz.GlobalMouse.__mouseEvent($1,$0);this.__LZlastclick=null
}else{
this.__LZlastclick=$0;this.__LZlastClickTime=LzTimeKernel.getTimer()
}};$0.mouseevent($1);if($1=="onmousedown"){
lz.Focus.__LZcheckFocusChange($0)
}}},"__LZallowInput",function($0,$1){
return $1.childOf($0)
},"__LZallowFocus",function($0){
var $1=this.modeArray.length;return $1==0||$0.childOf(this.modeArray[$1-1])
},"globalLockMouseEvents",function(){
this.eventsLocked=true
},"globalUnlockMouseEvents",function(){
this.eventsLocked=false
},"hasMode",function($0){
var $1=this.modeArray;for(var $2=$1.length-1;$2>=0;$2--){
if($0===$1[$2]){
return true
}};return false
},"getModalView",function(){
return this.modeArray[this.modeArray.length-1]||null
},"__findInputtextSelection",function(){
return LzInputTextSprite.findSelection()
},"rawMouseEvent",function($0,$1){
if($0=="onmousemove"){
lz.GlobalMouse.__mouseEvent("onmousemove",null)
}else{
this.handleMouseEvent($1,$0)
}}],["LzModeManager",void 0]);(function($0){
with($0)with($0.prototype){
{
LzModeManagerService.LzModeManager=new LzModeManagerService()
}}})(LzModeManagerService);lz.ModeManagerService=LzModeManagerService;lz.ModeManager=LzModeManagerService.LzModeManager;Class.make("LzCursorService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"showHandCursor",function($0){
LzMouseKernel.showHandCursor($0)
},"setCursorGlobal",function($0){
LzMouseKernel.setCursorGlobal($0)
},"lock",function(){
LzMouseKernel.lock()
},"unlock",function(){
LzMouseKernel.unlock()
},"restoreCursor",function(){
LzMouseKernel.restoreCursor()
}],["LzCursor",void 0]);(function($0){
with($0)with($0.prototype){
{
LzCursorService.LzCursor=new LzCursorService()
}}})(LzCursorService);lz.CursorService=LzCursorService;lz.Cursor=LzCursorService.LzCursor;Class.make("LzURL",null,["protocol",null,"host",null,"port",null,"path",null,"file",null,"query",null,"fragment",null,"_parsed",null,"$lzsc$initialize",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0!=null){
this.parseURL($0)
}},"parseURL",function($0){
if(this._parsed==$0)return;this._parsed=$0;var $1=0;var $2=$0.indexOf(":");var $3=$0.indexOf("?",$1);var $4=$0.indexOf("#",$1);var $5=$0.length;if($4!=-1){
$5=$4
};if($3!=-1){
$5=$3
};if($2!=-1){
this.protocol=$0.substring($1,$2);if($0.substring($2+1,$2+3)=="//"){
$1=$2+3;$2=$0.indexOf("/",$1);if($2==-1){
$2=$5
};var $6=$0.substring($1,$2);var $7=$6.indexOf(":");if($7==-1){
this.host=$6;this.port=null
}else{
this.host=$6.substring(0,$7);this.port=$6.substring($7+1)
}}else{
$2++
};$1=$2
};$2=$5;this._splitPath($0.substring($1,$2));if($4!=-1){
this.fragment=$0.substring($4+1,$0.length)
}else{
$4=$0.length
};if($3!=-1){
this.query=$0.substring($3+1,$4)
}},"_splitPath",function($0){
if($0==""){
return
};var $1=$0.lastIndexOf("/");if($1!=-1){
this.path=$0.substring(0,$1+1);this.file=$0.substring($1+1,$0.length);if(this.file==""){
this.file=null
};return
};this.path=null;this.file=$0
},"dupe",function(){
var $0=new LzURL();$0.protocol=this.protocol;$0.host=this.host;$0.port=this.port;$0.path=this.path;$0.file=this.file;$0.query=this.query;$0.fragment=this.fragment;return $0
},"toString",function(){
var $0="";if(this.protocol!=null){
$0+=this.protocol+":";if(this.host!=null){
$0+="//"+this.host;if(null!=this.port&&lz.Browser.defaultPortNums[this.protocol]!=this.port){
$0+=":"+this.port
}}};if(this.path!=null){
$0+=this.path
};if(null!=this.file){
$0+=this.file
};if(null!=this.query){
$0+="?"+this.query
};if(null!=this.fragment){
$0+="#"+this.fragment
};return $0
}],["merge",function($0,$1){
var $2=new LzURL();var $3={protocol:true,host:true,port:true,path:true,file:true,query:true,fragment:true};for(var $4 in $3){
$2[$4]=$0[$4]!=null?$0[$4]:$1[$4]
};return $2
}]);lz.URL=LzURL;Class.make("LzKeysService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);LzKeyboardKernel.setCallback(this,"__keyEvent");if(lz.embed["mousewheel"]){
lz.embed.mousewheel.setCallback(this,"__mousewheelEvent")
}},"downKeysHash",{},"downKeysArray",[],"keycombos",{},"onkeydown",LzDeclaredEvent,"onkeyup",LzDeclaredEvent,"onmousewheeldelta",LzDeclaredEvent,"codemap",{shift:16,control:17,alt:18},"ctrlKey",false,"__keyEvent",function($0,$1,$2,$3){
switch(arguments.length){
case 3:
$3=false;

};this.ctrlKey=$3;var $4=this.codemap;for(var $5 in $0){
var $6=$0[$5];if($4[$5]!=null)$1=$4[$5];if($6){
this.gotKeyDown($1)
}else{
this.gotKeyUp($1)
}}},"__allKeysUp",function(){
LzKeyboardKernel.__allKeysUp()
},"gotKeyDown",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};var $2=this.downKeysHash;var $3=this.downKeysArray;var $4=!$2[$0];if($4){
$2[$0]=true;$3.push($0);$3.sort()
};if($4||$1!="extra"){
if($2[229]!=true){
if(this.onkeydown.ready)this.onkeydown.sendEvent($0)
}};if($4){
var $5=this.keycombos;for(var $6=0;$6<$3.length&&$5!=null;$6++){
$5=$5[$3[$6]]
};if($5!=null&&("delegates" in $5)){
var $7=$5.delegates;for(var $6=0;$6<$7.length;$6++){
$7[$6].execute($3)
}}}},"gotKeyUp",function($0){
var $1=this.downKeysHash;var $2=$1[$0];delete $1[$0];var $3=this.downKeysArray;$3.length=0;for(var $4 in $1){
$3.push($4)
};if($2&&this.onkeyup.ready)this.onkeyup.sendEvent($0)
},"isKeyDown",function($0){
if(typeof $0=="string"){
return this.downKeysHash[this.keyCodes[$0.toLowerCase()]]==true
}else{
var $1=true;var $2=this.downKeysHash;var $3=this.keyCodes;for(var $4=0;$4<$0.length;$4++){
$1=$1&&$2[$3[$0[$4].toLowerCase()]]==true
};return $1
}},"callOnKeyCombo",function($0,$1){
var $2=this.keyCodes;var $3=[];for(var $4=0;$4<$1.length;$4++){
$3.push($2[$1[$4].toLowerCase()])
};$3.sort();var $5=this.keycombos;for(var $4=0;$4<$3.length;$4++){
var $6=$5[$3[$4]];if($6==null){
$5[$3[$4]]=$6={delegates:[]}};$5=$6
};$5.delegates.push($0)
},"removeKeyComboCall",function($0,$1){
var $2=this.keyCodes;var $3=[];for(var $4=0;$4<$1.length;$4++){
$3.push($2[$1[$4].toLowerCase()])
};$3.sort();var $5=this.keycombos;for(var $4=0;$4<$3.length;$4++){
$5=$5[$3[$4]];if($5==null){
return false
}};for(var $4=$5.delegates.length-1;$4>=0;$4--){
if($5.delegates[$4]==$0){
$5.delegates.splice($4,1)
}}},"enableEnter",function($0){},"mousewheeldelta",0,"__mousewheelEvent",function($0){
this.mousewheeldelta=$0;if(this.onmousewheeldelta.ready)this.onmousewheeldelta.sendEvent($0)
},"gotLastFocus",function($0){
LzKeyboardKernel.gotLastFocus()
},"setGlobalFocusTrap",function($0){
if(canvas.capabilities.globalfocustrap){
LzKeyboardKernel.setGlobalFocusTrap($0)
}},"keyCodes",{"0":48,")":48,";":186,":":186,"1":49,"!":49,"=":187,"+":187,"2":50,"@":50,"<":188,",":188,"3":51,"#":51,"-":189,"_":189,"4":52,"$":52,">":190,".":190,"5":53,"%":53,"/":191,"?":191,"6":54,"^":54,"`":192,"~":192,"7":55,"&":55,"[":219,"{":219,"8":56,"*":56,"\\":220,"|":220,"9":57,"(":57,"]":221,"}":221,'"':222,"'":222,a:65,b:66,c:67,d:68,e:69,f:70,g:71,h:72,i:73,j:74,k:75,l:76,m:77,n:78,o:79,p:80,q:81,r:82,s:83,t:84,u:85,v:86,w:87,x:88,y:89,z:90,numbpad0:96,numbpad1:97,numbpad2:98,numbpad3:99,numbpad4:100,numbpad5:101,numbpad6:102,numbpad7:103,numbpad8:104,numbpad9:105,multiply:106,"add":107,subtract:109,decimal:110,divide:111,f1:112,f2:113,f3:114,f4:115,f5:116,f6:117,f7:118,f8:119,f9:120,f10:121,f11:122,f12:123,backspace:8,tab:9,clear:12,enter:13,shift:16,control:17,alt:18,"pause":19,"break":19,capslock:20,esc:27,spacebar:32,pageup:33,pagedown:34,end:35,home:36,leftarrow:37,uparrow:38,rightarrow:39,downarrow:40,insert:45,"delete":46,help:47,numlock:144,screenlock:145,"IME":229}],["LzKeys",void 0]);(function($0){
with($0)with($0.prototype){
{
LzKeysService.LzKeys=new LzKeysService()
}}})(LzKeysService);lz.KeysService=LzKeysService;lz.Keys=LzKeysService.LzKeys;Class.make("LzAudioService",null,["capabilities",LzSprite.prototype.capabilities,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"playSound",function($0){
if(this.capabilities.audio){
LzAudioKernel.playSound($0)
}},"stopSound",function(){
if(this.capabilities.audio){
LzAudioKernel.stopSound()
}},"startSound",function(){
if(this.capabilities.audio){
LzAudioKernel.startSound()
}},"getVolume",function(){
if(this.capabilities.audio){
return LzAudioKernel.getVolume()
};return NaN
},"setVolume",function($0){
if(this.capabilities.audio){
LzAudioKernel.setVolume($0)
}},"getPan",function(){
if(this.capabilities.audio){
return LzAudioKernel.getPan()
};return NaN
},"setPan",function($0){
if(this.capabilities.audio){
LzAudioKernel.setPan($0)
}}],["LzAudio",void 0]);(function($0){
with($0)with($0.prototype){
{
LzAudioService.LzAudio=new LzAudioService()
}}})(LzAudioService);lz.AudioService=LzAudioService;lz.Audio=LzAudioService.LzAudio;Class.make("LzHistoryService",LzEventable,["$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"isReady",false,"ready",false,"onready",LzDeclaredEvent,"persist",false,"_persistso",null,"offset",0,"__lzdirty",false,"__lzhistq",[],"__lzcurrstate",{},"capabilities",LzSprite.prototype.capabilities,"onoffset",LzDeclaredEvent,"receiveHistory",function($0){
if(this.persist&&!this._persistso){
this.__initPersist()
};var $1=this.__lzhistq.length;var $2=$0*1;if(!$2){
$2=0
}else if($2>$1-1){
$2=$1
};var $3=this.__lzhistq[$2];for(var $4 in $3){
var $5=$3[$4];{
var $lzsc$1063379912=global[$5.c];var $lzsc$965956998=$5.n;var $lzsc$718501025=$5.v;if(!$lzsc$1063379912.__LZdeleted){
var $lzsc$243839838="$lzc$set_"+$lzsc$965956998;if(Function["$lzsc$isa"]?Function.$lzsc$isa($lzsc$1063379912[$lzsc$243839838]):$lzsc$1063379912[$lzsc$243839838] instanceof Function){
$lzsc$1063379912[$lzsc$243839838]($lzsc$718501025)
}else{
$lzsc$1063379912[$lzsc$965956998]=$lzsc$718501025;var $lzsc$961865175=$lzsc$1063379912["on"+$lzsc$965956998];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$961865175):$lzsc$961865175 instanceof LzEvent){
if($lzsc$961865175.ready){
$lzsc$961865175.sendEvent($lzsc$718501025)
}}}}}};this.offset=$2;if(this.onoffset.ready)this.onoffset.sendEvent($2);return $2
},"receiveEvent",function($0,$1){
{
if(!canvas.__LZdeleted){
var $lzsc$713088557="$lzc$set_"+$0;if(Function["$lzsc$isa"]?Function.$lzsc$isa(canvas[$lzsc$713088557]):canvas[$lzsc$713088557] instanceof Function){
canvas[$lzsc$713088557]($1)
}else{
canvas[$0]=$1;var $lzsc$532233531=canvas["on"+$0];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$532233531):$lzsc$532233531 instanceof LzEvent){
if($lzsc$532233531.ready){
$lzsc$532233531.sendEvent($1)
}}}}}},"getCanvasAttribute",function($0){
return canvas[$0]
},"setCanvasAttribute",function($0,$1){
this.receiveEvent($0,$1)
},"callMethod",function($0){
return LzBrowserKernel.callMethod($0)
},"save",function($0,$1,$2){
if(typeof $0!="string"){
if($0["id"])$0=$0["id"];if(!$0)return
};if($2==null)$2=global[$0][$1];this.__lzcurrstate[$0]={c:$0,n:$1,v:$2};this.__lzdirty=true
},"commit",function(){
if(!this.__lzdirty)return;this.__lzhistq[this.offset]=this.__lzcurrstate;this.__lzhistq.length=this.offset+1;if(this.persist){
if(!this._persistso){
this.__initPersist()
};this._persistso.data[this.offset]=this.__lzcurrstate
};this.__lzcurrstate={};this.__lzdirty=false
},"move",function($0){
switch(arguments.length){
case 0:
$0=1;

};this.commit();var $1=this.offset+$0;if(0>=$1)$1=0;if(this.__lzhistq.length>=$1){
LzBrowserKernel.setHistory($1)
}},"next",function(){
this.move(1)
},"prev",function(){
this.move(-1)
},"__initPersist",function(){
if(this.persist){
if(!this._persistso){
this._persistso=LzBrowserKernel.getPersistedObject("historystate")
};if(this._persistso&&this._persistso.data){
var $0=this._persistso.data;this.__lzhistq=[];for(var $1 in $0){
this.__lzhistq[$1]=$0[$1]
}}}else{
if(this._persistso)this._persistso=null
}},"clear",function(){
if(this.persist){
if(!this._persistso){
this._persistso=LzBrowserKernel.getPersistedObject("historystate")
};this._persistso.clear()
};this.__lzhistq=[];this.offset=0;if(this.onoffset.ready)this.onoffset.sendEvent(0)
},"setPersist",function($0){
if(this.capabilities.persistence){
this.persist=$0
}},"__start",function($0){
lz.Browser.callJS("lz.embed.history.listen('"+$0+"')");this.isReady=true;this.ready=true;if(this.onready.ready)this.onready.sendEvent(true)
}],["LzHistory",void 0]);(function($0){
with($0)with($0.prototype){
{
LzHistoryService.LzHistory=new LzHistoryService()
}}})(LzHistoryService);lz.HistoryService=LzHistoryService;lz.History=LzHistoryService.LzHistory;Class.make("LzTrackService",LzEventable,["__LZreg",new Object(),"__LZactivegroups",null,"__LZtrackDel",null,"__LZmouseupDel",null,"__LZdestroydel",null,"__LZlastmouseup",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.__LZtrackDel=new LzDelegate(this,"__LZtrack");this.__LZmouseupDel=new LzDelegate(this,"__LZmouseup",lz.GlobalMouse,"onmouseup");this.__LZdestroydel=new LzDelegate(this,"__LZdestroyitem");this.__LZactivegroups=[]
},"register",function($0,$1){
if($0==null||$1==null)return;var $2=this.__LZreg[$1];if(!$2){
this.__LZreg[$1]=$2=[];$2.__LZlasthit=null;$2.__LZactive=false
};$2.push($0);this.__LZdestroydel.register($0,"ondestroy")
},"unregister",function($0,$1){
if($0==null||$1==null)return;var $2=this.__LZreg[$1];if($2){
for(var $3=0;$3<$2.length;$3++){
if($2[$3]==$0){
if($2.__LZlasthit==$0){
if(this.__LZlastmouseup==$0){
this.__LZlastmouseup=null
};$2.__LZlasthit=null
};$2.splice($3,1)
}};if($2.length==0){
if($2.__LZactive){
this.deactivate($1)
};delete this.__LZreg[$1]
}};this.__LZdestroydel.unregisterFrom($0.ondestroy)
},"__LZdestroyitem",function($0){
for(var $1 in this.__LZreg){
this.unregister($0,$1)
}},"activate",function($0){
var $1=this.__LZreg[$0];if($1&&!$1.__LZactive){
$1.__LZactive=true;var $2=this.__LZactivegroups;if($2.length==0){
this.__LZtrackDel.register(lz.Idle,"onidle")
};$2.push($1)
}},"deactivate",function($0){
var $1=this.__LZreg[$0];if($1&&$1.__LZactive){
var $2=this.__LZactivegroups;for(var $3=0;$3<$2.length;++$3){
if($2[$3]==$1){
$2.splice($3,1);break
}};if($2.length==0){
this.__LZtrackDel.unregisterAll()
};$1.__LZactive=false;if(this.__LZlastmouseup==$1.__LZlasthit){
this.__LZlastmouseup=null
};$1.__LZlasthit=null
}},"__LZtopview",function($0,$1){
var $2=$0;var $3=$1;while($2.nodeLevel<$3.nodeLevel){
$3=$3.immediateparent;if($3==$0)return $1
};while($3.nodeLevel<$2.nodeLevel){
$2=$2.immediateparent;if($2==$1)return $0
};while($2.immediateparent!=$3.immediateparent){
$2=$2.immediateparent;$3=$3.immediateparent
};return $2.getZ()>$3.getZ()?$0:$1
},"__LZfindTopmost",function($0){
var $1=$0[0];for(var $2=1;$2<$0.length;$2++){
$1=this.__LZtopview($1,$0[$2])
};return $1
},"__LZtrackgroup",function($0,$1){
for(var $2=0;$2<$0.length;$2++){
var $3=$0[$2];if($3.visible){
var $4=$3.getMouse(null);if($3.containsPt($4.x,$4.y)){
$1.push($3)
}}}},"__LZtrack",function($0){
var $1=[];var $2=this.__LZactivegroups;for(var $3=0;$3<$2.length;++$3){
var $4=$2[$3];var $5=[];this.__LZtrackgroup($4,$5);var $6=$4.__LZlasthit;if($5.length){
var $7=this.__LZfindTopmost($5);if($7==$6)continue;$1.push($7)
}else{
var $7=null
};if($6){
var $8=$6.onmousetrackout;if($8.ready)$8.sendEvent($6)
};$4.__LZlasthit=$7
};for(var $3=0,$9=$1.length;$3<$9;++$3){
var $a=$1[$3];if($a.onmousetrackover.ready)$a.onmousetrackover.sendEvent($a)
}},"__LZmouseup",function($0){
var $1=this.__LZactivegroups.slice();for(var $2=0;$2<$1.length;++$2){
var $3=$1[$2].__LZlasthit;if($3){
var $4=$3.onmousetrackup;if($4.ready){
if(this.__LZlastmouseup==$3){
this.__LZlastmouseup=null
}else{
this.__LZlastmouseup=$3;$4.sendEvent($3)
}}}}}],["LzTrack",void 0]);(function($0){
with($0)with($0.prototype){
{
LzTrackService.LzTrack=new LzTrackService()
}}})(LzTrackService);lz.TrackService=LzTrackService;lz.Track=LzTrackService.LzTrack;Class.make("LzIdleEvent",LzEvent,["registered",false,"$lzsc$initialize",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2);if(this.ready&&!this.registered){
this.registered=true;LzIdleKernel.addCallback(this,"sendEvent")
}},"addDelegate",function($0){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["addDelegate"]||this.nextMethod(arguments.callee,"addDelegate")).call(this,$0);if(this.ready&&!this.registered){
this.registered=true;LzIdleKernel.addCallback(this,"sendEvent")
}},"removeDelegate",function($0){
switch(arguments.length){
case 0:
$0=null;

};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["removeDelegate"]||this.nextMethod(arguments.callee,"removeDelegate")).call(this,$0);if(!this.ready&&this.registered){
LzIdleKernel.removeCallback(this,"sendEvent");this.registered=false
}},"clearDelegates",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["clearDelegates"]||this.nextMethod(arguments.callee,"clearDelegates")).call(this);if(!this.ready&&this.registered){
LzIdleKernel.removeCallback(this,"sendEvent");this.registered=false
}}],null);Class.make("LzIdleService",LzEventable,["coi",void 0,"regNext",false,"removeCOI",null,"onidle",void 0,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.coi=new Array();this.removeCOI=new LzDelegate(this,"removeCallIdleDelegates");this.onidle=new LzIdleEvent(this,"onidle")
},"callOnIdle",function($0){
this.coi.push($0);if(!this.regNext){
this.regNext=true;this.removeCOI.register(this,"onidle")
}},"removeCallIdleDelegates",function($0){
var $1=this.coi.slice(0);this.coi.length=0;for(var $2=0;$2<$1.length;$2++){
$1[$2].execute($0)
};if(this.coi.length==0){
this.removeCOI.unregisterFrom(this.onidle);this.regNext=false
}},"setFrameRate",function($0){
switch(arguments.length){
case 0:
$0=30;

};LzIdleKernel.setFrameRate($0)
}],["LzIdle",void 0]);(function($0){
with($0)with($0.prototype){
{
LzIdleService.LzIdle=new LzIdleService()
}}})(LzIdleService);lz.IdleService=LzIdleService;lz.Idle=LzIdleService.LzIdle;Class.make("LzCSSStyleRule",null,["selector",void 0,"properties",void 0,"$lzsc$initialize",function($0,$1){
this.selector=$0;this.properties=$1
},"specificity",0,"parsed",null,"_lexorder",void 0,"getSpecificity",function(){
if(!this.specificity){
var $0=0;var $1=this.parsed;if($1.type==LzCSSStyle._sel_compound){
for(var $2=0,$3=$1.length;$2<$3;$2++){
$0+=LzCSSStyle.getSelectorSpecificity($1[$2])
}}else{
$0=LzCSSStyle.getSelectorSpecificity($1)
};this.specificity=$0
};return this.specificity
},"_dbg_name",function(){
var $0;$0=function($0){
var $1=$0["tagname"];var $2=$0["id"];var $3=$0["attrname"];if(!($1||$2||$3)){
return "*"
};return($1?$1:"")+($2?"#"+$2:"")+($3?"["+$3+"="+$0.attrvalue+"]":"")
};var $1=this.parsed;if($1["length"]){
var $2="";for(var $3=0;$3<$1.length;$3++){
$2+=$0($1[$3])+" "
};$2=$2.substring(0,$2.length-1)
}else{
var $2=$0($1)
};return $2
},"equal",function($0){
var $1;$1=function($0,$1){
return $0["tagname"]==$1["tagname"]&&$0["id"]==$1["id"]&&$0["attrname"]==$1["attrname"]&&$0["attrvalue"]==$1["attrvalue"]
};var $2=this.parsed;var $3=$0.parsed;if($2["length"]!=$3["length"]){
return false
};if($2["length"]){
for(var $4=$2.length-1;$4>=0;$4--){
if(!$1($2[$4],$3[$4])){
return false
}}};if(!$1($2,$3)){
return false
};var $5=this.properties;var $6=$0.properties;for(var $7 in $5){
if($5[$7]!==$6[$7]){
return false
}};for(var $8 in $6){
if($5[$8]!==$6[$8]){
return false
}};return true
}],null);(function($0){
with($0)with($0.prototype){}})(LzCSSStyleRule);lz.CSSStyleRule=LzCSSStyleRule;Class.make("LzCSSStyleClass",null,["$lzsc$initialize",function(){},"getComputedStyle",function($0){
var $1=new LzCSSStyleDeclaration();$1.setNode($0);return $1
},"getPropertyValueFor",function($0,$1){
var $2=$0["__LZPropertyCache"];if(!$2){
$2=this.getPropertyCache($0)
};if($1 in $2){
return $2[$1]
};return $2[$1]=void 0
},"getPropertyCache",function($0){
if(!$0||$0===canvas)return {};var $1=$0["__LZPropertyCache"];if($1){
return $1
};var $2=$0.immediateparent;if(!$2||$2===canvas){
var $3={}}else{
var $3=$2["__LZPropertyCache"]||this.getPropertyCache($2)
};var $4=$0["__LZRuleCache"];if(!$4){
$4=this.getRulesCache($0)
};if($4.length==0){
return $0.__LZPropertyCache=$3
};var $1={};for(var $5 in $3){
$1[$5]=$3[$5]
};for(var $6=$4.length-1;$6>=0;$6--){
var $7=$4[$6].properties;for(var $8 in $7){
$1[$8]=$7[$8]
}};return $0.__LZPropertyCache=$1
},"getRulesCache",function($0){
var $1=$0["__LZRuleCache"];if($1){
return $1
};$1=new Array();var $2=new Array();if(this._rulenum!=this._lastSort){
this._sortRules()
};var $3=$0["id"];if($3){
var $4=this._idRules;if($3 in $4){
$2=$2.concat($4[$3])
}};var $5=this._attrRules;for(var $6 in $5){
if($0[$6]){
var $7=$5[$6][$0[$6]];if(Array["$lzsc$isa"]?Array.$lzsc$isa($7):$7 instanceof Array){
$2=$2.concat($7)
}}};var $8=this._tagRules;for(var $9 in $8){
var $a=lz[$9];if($a&&($a["$lzsc$isa"]?$a.$lzsc$isa($0):$0 instanceof $a)){
$2=$2.concat($8[$9])
}};var $b=this._rules;for(var $c=$b.length-1;$c>=0;$c--){
$2.push($b[$c])
};var $d=false;var $e=Infinity;for(var $c=0,$f=$2.length;$c<$f;$c++){
var $g=$2[$c];if(!$d){
var $7=$g.specificity;if(!$7||$7>=$e){
$d=true
}else{
$e=$7
}};var $h=$g.parsed;var $i=$h.type;var $j=$i==this._sel_compound;if($j){
$h=$h[$h.length-1];$i=$h.type
};var $k=$h.tagname;var $l=$k?lz[$k]:null;var $m=$h["id"];var $n=$h["attrname"];if((!$k||$l&&($l["$lzsc$isa"]?$l.$lzsc$isa($0):$0 instanceof $l))&&(!$m||$0["id"]==$m)&&(!$n||$0[$n]==$h.attrvalue)){
if(!$j){
$1.push($g)
}else{
if(this._compoundSelectorApplies($g.parsed,$0)){
$1.push($g)
}}}};if($d){
$1.sort(this.__compareSpecificity)
};$0.__LZRuleCache=$1;return $1
},"getSelectorSpecificity",function($0){
switch($0.type){
case this._sel_tag:
case this._sel_star:
return 1;
case this._sel_id:
return 100;
case this._sel_attribute:
return 10;
case this._sel_tagAndAttr:
return 11;

}},"__compareSpecificity",function($0,$1){
var $2=$0.specificity;var $3=$1.specificity;if($2!=$3){
return $2<$3?1:-1
};var $4=$0.parsed;var $5=$1.parsed;var $6=$0._lexorder<$1._lexorder?1:-1;if(!$4["length"]&&!$5["length"]){
var $7=$4["tagname"];var $8=$5["tagname"];if(!$7||!$8||$7==$8){
return $6
};var $9=lz[$7];var $a=lz[$8];if($9&&$a){
if($a.prototype.isPrototypeOf($9.prototype)){
return -1
};if($9.prototype.isPrototypeOf($a.prototype)){
return 1
}};return $6
};for(var $b=0;$b<$4.length;$b++){
var $c=$4[$b];var $d=$5[$b];if(!$c||!$d){
break
};var $7=$c["tagname"];var $8=$d["tagname"];if($7&&$8&&$7!=$8){
var $9=lz[$7];var $a=lz[$8];if($9&&$a){
if($a.prototype.isPrototypeOf($9.prototype)){
return -1
};if($9.prototype.isPrototypeOf($a.prototype)){
return 1
}}}};return $6
},"_printRuleArray",function($0){},"_compoundSelectorApplies",function($0,$1){
for(var $2=$1,$3=$0.length-1;$3>=0&&$2!==canvas;$3--,$2=$2.parent){
var $4=$0[$3];var $5=$4.tagname;var $6=$5?lz[$5]:null;var $7=$4["id"];var $8=$4["attrname"];while($2!==canvas){
if((!$5||$6&&($6["$lzsc$isa"]?$6.$lzsc$isa($2):$2 instanceof $6))&&(!$7||$2.id==$7)&&(!$8||$2[$8]==$4.attrvalue)){
if($3==0){
return true
}else{
break
}}else{
if($2===$1){
return false
}};$2=$2.parent
}};return false
},"_sel_unknown",0,"_sel_star",1,"_sel_id",2,"_sel_tag",3,"_sel_compound",4,"_sel_attribute",5,"_sel_tagAndAttr",6,"_rules",new Array(),"_attrRules",{},"_idRules",{},"_tagRules",{},"_rulenum",0,"_lastSort",-1,"_sortRules",function(){
var $0;$0=function($0){
for(var $1=$0.length-2;$1>=0;$1--){
if($0[$1].equal($0[$1+1])){
$0.splice($1+1,1)
}};return $0
};if(this._rulenum!=this._lastSort){
this._rules.sort(this.__compareSpecificity);$0(this._rules);for(var $1 in this._attrRules){
var $2=this._attrRules[$1];for(var $3 in $2){
$2[$3].sort(this.__compareSpecificity);$0($2[$3])
}};for(var $1 in this._idRules){
this._idRules[$1].sort(this.__compareSpecificity);$0(this._idRules[$1])
};for(var $1 in this._tagRules){
this._tagRules[$1].sort(this.__compareSpecificity);$0(this._tagRules[$1])
};this._lastSort=this._rulenum
}},"_addRule",function($0){
$0._lexorder=this._rulenum++;var $1=$0.selector;$0.parsed=null;var $2;if(Array["$lzsc$isa"]?Array.$lzsc$isa($1):$1 instanceof Array){
$0.parsed=[];$0.parsed.type=this._sel_compound;for(var $3=0;$3<$1.length;$3++){
$0.parsed.push(this._parseSelector($1[$3]))
};$2=$0.parsed[$0.parsed.length-1]
}else{
$0.parsed=this._parseSelector($1);$2=$0.parsed
};$0.getSpecificity();if($2.type==this._sel_attribute||$2.type==this._sel_tagAndAttr){
var $4=$2.attrname;var $5=this._attrRules[$4];if(!$5){
$5=this._attrRules[$4]={}};var $6=$2.attrvalue;var $7=$5[$6];if(!$7){
$7=$5[$6]=[]
};$7.push($0)
}else if($2.type==this._sel_id){
var $8=$2.id;if(!this._idRules[$8]){
this._idRules[$8]=[]
};this._idRules[$8].push($0)
}else if($2.type==this._sel_tag){
var $9=$2.tagname;if(!this._tagRules[$9]){
this._tagRules[$9]=[]
};this._tagRules[$9].push($0)
}else{
this._rules.push($0)
}},"_parseSelector",function($0){
switch(typeof $0){
case "object":
if($0.simpleselector){
$0.type=this._sel_tagAndAttr;$0.tagname=$0.simpleselector
}else $0.type=this._sel_attribute;return $0;break;
case "string":
return this._parseStringSelector($0);break;

}},"_parseStringSelector",function($0){
var $1={};if($0=="*"){
$1.type=this._sel_star
}else{
var $2=$0.indexOf("#");if($2>=0){
$1.id=$0.substring($2+1);$1.type=this._sel_id
}else{
$1.type=this._sel_tag;$1.tagname=$0
}};return $1
}],null);(function($0){
with($0)with($0.prototype){}})(LzCSSStyleClass);var LzCSSStyle=new LzCSSStyleClass();lz.CSSStyle=LzCSSStyleClass;Class.make("LzCSSStyleDeclaration",null,["$lzsc$initialize",function(){},"_node",null,"getPropertyValue",function($0){
return LzCSSStyle.getPropertyValueFor(this._node,$0)
},"setNode",function($0){
this._node=$0
}],null);(function($0){
with($0)with($0.prototype){}})(LzCSSStyleDeclaration);lz.CSSStyleDeclaration=LzCSSStyleDeclaration;Class.make("LzStyleSheet",null,["$lzsc$initialize",function($0,$1,$2,$3){
this.type=$3;this.disabled=false;this.ownerNode=null;this.parentStyleSheet=null;this.href=$1;this.title=$0;this.media=$2
},"type",null,"disabled",null,"ownerNode",null,"parentStyleSheet",null,"href",null,"title",null,"media",null],null);Class.make("LzCSSStyleSheet",LzStyleSheet,["$lzsc$initialize",function($0,$1,$2,$3,$4,$5){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this,$0,$1,$2,$3);this.ownerRule=$4;this.cssRules=$5
},"ownerRule",null,"cssRules",null,"insertRule",function($0,$1){
if(!this.cssRules){
this.cssRules=[]
};if($1<0){
return null
};if($1<this.cssRules.length){
this.cssRules.splice($1,0,$0);return $1
};if($1==this.cssRules.length){
return this.cssRules.push($0)-1
};return null
}],null);lz.CSSStyleSheet=LzCSSStyleSheet;Class.make("LzFocusService",LzEventable,["onfocus",LzDeclaredEvent,"onescapefocus",LzDeclaredEvent,"lastfocus",null,"csel",null,"cseldest",null,"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this);this.upDel=new LzDelegate(this,"gotKeyUp",lz.Keys,"onkeyup");this.downDel=new LzDelegate(this,"gotKeyDown",lz.Keys,"onkeydown");this.lastfocusDel=new LzDelegate(lz.Keys,"gotLastFocus",this,"onescapefocus")
},"upDel",void 0,"downDel",void 0,"lastfocusDel",void 0,"focuswithkey",false,"__LZskipblur",false,"__LZsfnextfocus",-1,"__LZsfrunning",false,"gotKeyUp",function($0){
if(this.csel&&this.csel.onkeyup.ready)this.csel.onkeyup.sendEvent($0)
},"gotKeyDown",function($0){
if(this.csel&&this.csel.onkeydown.ready)this.csel.onkeydown.sendEvent($0);if($0==lz.Keys.keyCodes.tab){
if(lz.Keys.isKeyDown("shift")){
this.prev()
}else{
this.next()
}}},"__LZcheckFocusChange",function($0){
if($0.focusable){
this.setFocus($0,false)
}},"setFocus",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if(this.__LZsfrunning){
this.__LZsfnextfocus=$0;return
};if(this.cseldest==$0){
return
};var $2=this.csel;if($2&&!$2.shouldYieldFocus()){
return
};if($0&&!$0.focusable){
$0=this.getNext($0);if(this.cseldest==$0){
return
}};if($2){
$2.blurring=true
};this.__LZsfnextfocus=-1;this.__LZsfrunning=true;this.cseldest=$0;if($1!=null){
this.focuswithkey=!(!$1)
};if(!this.__LZskipblur){
this.__LZskipblur=true;if($2&&$2.onblur.ready){
$2.onblur.sendEvent($0);var $3=this.__LZsfnextfocus;if($3!=-1){
if($3&&!$3.focusable){
$3=this.getNext($3)
};if($3!=$0){
this.__LZsfrunning=false;this.setFocus($3);return
}}}};this.lastfocus=$2;this.csel=$0;this.__LZskipblur=false;if($2){
$2.blurring=false
};if($dhtml&&canvas.accessible){
if($0&&$0.sprite!=null){
$0.sprite.aafocus()
}};if($0&&$0.onfocus.ready){
$0.onfocus.sendEvent($0);var $3=this.__LZsfnextfocus;if($3!=-1){
if($3&&!$3.focusable){
$3=this.getNext($3)
};if($3!=$0){
this.__LZsfrunning=false;this.setFocus($3);return
}}};if(this.onfocus.ready){
this.onfocus.sendEvent($0);var $3=this.__LZsfnextfocus;if($3!=-1){
if($3&&!$3.focusable){
$3=this.getNext($3)
};if($3!=$0){
this.__LZsfrunning=false;this.setFocus($3);return
}}};this.__LZsfrunning=false
},"clearFocus",function(){
this.setFocus(null)
},"getFocus",function(){
return this.csel
},"next",function(){
this.genMoveSelection(1)
},"prev",function(){
this.genMoveSelection(-1)
},"getNext",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.moveSelSubview($0||this.csel,1,false)
},"getPrev",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.moveSelSubview($0||this.csel,-1,false)
},"genMoveSelection",function($0){
var $1=this.csel;var $2=$1;while($1&&$2!=canvas){
if(!$2.visible){
$1=null
};$2=$2.immediateparent
};if($1==null){
$1=lz.ModeManager.getModalView()
};var $3="get"+($0==1?"Next":"Prev")+"Selection";var $4=$1?$1[$3]():null;if($4==null){
$4=this.moveSelSubview($1,$0,true)
};if(lz.ModeManager.__LZallowFocus($4)){
this.setFocus($4,true)
}},"accumulateSubviews",function($0,$1,$2,$3){
if($1==$2||$1.focusable&&$1.visible)$0.push($1);if($3||!$1.focustrap&&$1.visible)for(var $4=0;$4<$1.subviews.length;$4++)this.accumulateSubviews($0,$1.subviews[$4],$2,false)
},"moveSelSubview",function($0,$1,$2){
var $3=$0||canvas;while(!$3.focustrap&&$3.immediateparent&&$3!=$3.immediateparent)$3=$3.immediateparent;var $4=[];this.accumulateSubviews($4,$3,$0,true);var $5=-1;var $6=$4.length;var $7=false;for(var $8=0;$8<$6;++$8){
if($4[$8]===$0){
$7=$1==-1&&$8==0||$1==1&&$8==$6-1;$5=$8;break
}};if($2&&$7){
this.onescapefocus.sendEvent()
};if($5==-1&&$1==-1)$5=0;$5=($5+$1+$6)%$6;return $4[$5]
}],["LzFocus",void 0]);(function($0){
with($0)with($0.prototype){
{
LzFocusService.LzFocus=new LzFocusService()
}}})(LzFocusService);lz.FocusService=LzFocusService;lz.Focus=LzFocusService.LzFocus;Class.make("LzTimerService",null,["timerList",new Object(),"$lzsc$initialize",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzsc$initialize"]||this.nextMethod(arguments.callee,"$lzsc$initialize")).call(this)
},"execDelegate",function($0){
var $1=$0.delegate;lz.Timer.removeTimerWithID($1,$0.id);if($1.enabled&&$1.c){
$1.execute(new Date().getTime())
}},"removeTimerWithID",function($0,$1){
var $2=$0.__delegateID;var $3=this.timerList[$2];if($3!=null){
if($3 instanceof Array){
for(var $4=0;$4<$3.length;$4++){
if($3[$4]==$1){
$3.splice($4,1);if($3.length==0)delete this.timerList[$2];break
}}}else if($3==$1){
delete this.timerList[$2]
}}},"addTimer",function($0,$1){
if(!$1||$1<1)$1=1;var $2={delegate:$0};var $3=LzTimeKernel.setTimeout(this.execDelegate,$1,$2);$2.id=$3;var $4=$0.__delegateID;var $5=this.timerList[$4];if($5==null){
this.timerList[$4]=$3
}else if(!($5 instanceof Array)){
this.timerList[$4]=[$5,$3]
}else{
$5.push($3)
};return $3
},"removeTimer",function($0){
var $1=$0.__delegateID;var $2=this.timerList[$1];var $3=null;if($2!=null){
if($2 instanceof Array){
$3=$2.shift();LzTimeKernel.clearTimeout($3);if($2.length==0)delete this.timerList[$1]
}else{
$3=$2;LzTimeKernel.clearTimeout($3);delete this.timerList[$1]
}};return $3
},"resetTimer",function($0,$1){
this.removeTimer($0);return this.addTimer($0,$1)
}],["LzTimer",void 0]);(function($0){
with($0)with($0.prototype){
{
LzTimerService.LzTimer=new LzTimerService()
}}})(LzTimerService);lz.TimerService=LzTimerService;lz.Timer=LzTimerService.LzTimer;Class.make("$lzc$class_lzcontextmenuseparator",LzView,["$m2",function($0){
this.parent.registerRedraw(this)
},"$m4",function($0){
this.parent.unregisterRedraw(this)
},"redraw",function($0){
$0.moveTo(0,this.y+9);$0.lineTo(this.parent.width-3,this.y+9);$0.strokeStyle="#E5E5E5";$0.stroke()
}],["tagname","lzcontextmenuseparator","attributes",new LzInheritedHash(LzView.attributes)]);(function($0){
with($0){
with($0.prototype){
LzNode.mergeAttributes({$delegates:["oninit","$m2",null,"ondestroy","$m4",null],height:10},$lzc$class_lzcontextmenuseparator.attributes)
}}})($lzc$class_lzcontextmenuseparator);Class.make("$lzc$class_lzcontextmenutext",LzText,["$m6",function($0){
this.parent.__overnow=this.data;this.parent.registerRedraw(this)
},"$m8",function($0){
this.parent.__overnow=null;this.parent.unregisterRedraw(this)
},"$m10",function($0){
this.parent.select(this.data);this.parent.unregisterRedraw(this)
},"redraw",function($0){
$0.rect(this.x,this.y+3,this.parent.width-3,this.height);$0.fillStyle="#CCCCCC";$0.fill()
}],["tagname","lzcontextmenutext","attributes",new LzInheritedHash(LzText.attributes)]);(function($0){
with($0){
with($0.prototype){
LzNode.mergeAttributes({$delegates:["onmouseover","$m6",null,"onmouseout","$m8",null,"onmousedown","$m10",null],clickable:true},$lzc$class_lzcontextmenutext.attributes)
}}})($lzc$class_lzcontextmenutext);Class.make("$lzc$class_lzcontextmenudisabled",LzText,null,["tagname","lzcontextmenudisabled","attributes",new LzInheritedHash(LzText.attributes)]);(function($0){
with($0){
with($0.prototype){
LzNode.mergeAttributes({fgcolor:LzColorUtils.convertColor("0xcccccc")},$lzc$class_lzcontextmenudisabled.attributes)
}}})($lzc$class_lzcontextmenudisabled);Class.make("$lzc$class_$2Fcanvas$2F$40lzcontextmenu$2F$40background",LzView,["$m13",function($0){
with(this){
var $1=parent.container.width+9;if($1!==this["width"]||!this.inited){
{
if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_width"]):this["$lzc$set_width"] instanceof Function){
this["$lzc$set_width"]($1)
}else{
this["width"]=$1;var $lzsc$1133882257=this["onwidth"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1133882257):$lzsc$1133882257 instanceof LzEvent){
if($lzsc$1133882257.ready){
$lzsc$1133882257.sendEvent($1)
}}}}}}}},"$m14",function(){
with(this){
return [parent.container,"width"]
}},"$m15",function($0){
with(this){
var $1=parent.container.height+9;if($1!==this["height"]||!this.inited){
{
if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_height"]):this["$lzc$set_height"] instanceof Function){
this["$lzc$set_height"]($1)
}else{
this["height"]=$1;var $lzsc$407575751=this["onheight"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$407575751):$lzsc$407575751 instanceof LzEvent){
if($lzsc$407575751.ready){
$lzsc$407575751.sendEvent($1)
}}}}}}}},"$m16",function(){
with(this){
return [parent.container,"height"]
}},"$m18",function($0){
this.createContext()
},"__doredraw",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.visible&&this.width&&this.height&&this.context){
this.redraw(this.context)
}},"redraw",function($0){
with(this){
switch(arguments.length){
case 0:
$0=this.context;

};$0.beginPath();$0.clearRect(0,0,this.width,this.height);LzKernelUtils.rect($0,2.5,3.5,this.width-3,this.height-3,classroot.inset);$0.fillStyle="#000000";$0.globalAlpha=0.2;$0.fill();$0.beginPath();LzKernelUtils.rect($0,0,0,this.width-3,this.height-3,classroot.inset);$0.globalAlpha=0.9;$0.fillStyle="#FFFFFF";$0.fill();$0.globalAlpha=1;$0.strokeStyle="#CCCCCC";$0.stroke();for(var $1 in parent.__drawnitems){
$0.beginPath();parent.__drawnitems[$1].redraw($0)
}}},"$classrootdepth",void 0],["tagname","view","attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_lzcontextmenu",LzView,["inset",void 0,"__drawnitems",void 0,"__overnow",void 0,"$m12",function($0){
with(this){
this.__globalmousedel=new (lz.Delegate)(this,"__handlemouse")
}},"select",function($0){
with(this){
var $1=LzMouseKernel.__showncontextmenu;if($1){
$1.__select($0)
};this.hide()
}},"show",function(){
with(this){
if(this.visible&&this.__overnow!=null){
this.select(this.__overnow);this.__overnow=null;return
};this.__overnow=null;var $0=canvas.getMouse();if($0.x>canvas.width-this.width){
$0.x=canvas.width-this.width
};if($0.y>canvas.height-this.height){
$0.y=canvas.height-this.height
};this.bringToFront();{
var $lzsc$8517328=$0.x;if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_x"]):this["$lzc$set_x"] instanceof Function){
this["$lzc$set_x"]($lzsc$8517328)
}else{
this["x"]=$lzsc$8517328;var $lzsc$1478492509=this["onx"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1478492509):$lzsc$1478492509 instanceof LzEvent){
if($lzsc$1478492509.ready){
$lzsc$1478492509.sendEvent($lzsc$8517328)
}}}}};{
var $lzsc$1631809301=$0.y;if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_y"]):this["$lzc$set_y"] instanceof Function){
this["$lzc$set_y"]($lzsc$1631809301)
}else{
this["y"]=$lzsc$1631809301;var $lzsc$979517269=this["ony"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$979517269):$lzsc$979517269 instanceof LzEvent){
if($lzsc$979517269.ready){
$lzsc$979517269.sendEvent($lzsc$1631809301)
}}}}};{
if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_visible"]):this["$lzc$set_visible"] instanceof Function){
this["$lzc$set_visible"](true)
}else{
this["visible"]=true;var $lzsc$406042483=this["onvisible"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$406042483):$lzsc$406042483 instanceof LzEvent){
if($lzsc$406042483.ready){
$lzsc$406042483.sendEvent(true)
}}}}};this.__globalmousedel.register(lz.GlobalMouse,"onmousedown")
}},"hide",function(){
with(this){
this.__globalmousedel.unregisterAll();var $0=LzMouseKernel.__showncontextmenu;if($0){
$0.__hide()
};{
if(!this.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa(this["$lzc$set_visible"]):this["$lzc$set_visible"] instanceof Function){
this["$lzc$set_visible"](false)
}else{
this["visible"]=false;var $lzsc$264427506=this["onvisible"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$264427506):$lzsc$264427506 instanceof LzEvent){
if($lzsc$264427506.ready){
$lzsc$264427506.sendEvent(false)
}}}}}}},"setItems",function($0){
with(this){
this.__drawnitems={};var $1=this.container.subviews;for(var $2=$1.length;$2>=0;$2--){
var $3=$1[$2];if(!$3||(LzLayout["$lzsc$isa"]?LzLayout.$lzsc$isa($3):$3 instanceof LzLayout)){
continue
};$3.destroy()
};var $4=$0.length;var $5=0;for(var $2=0;$2<$4;$2++){
var $6=$0[$2];var $7=lz["lzcontextmenu"+$6.type];if($7){
var $8=new $7(this,{data:$6.offset,text:$6.label});{
if(!$8.__LZdeleted){
if(Function["$lzsc$isa"]?Function.$lzsc$isa($8["$lzc$set_y"]):$8["$lzc$set_y"] instanceof Function){
$8["$lzc$set_y"]($5)
}else{
$8["y"]=$5;var $lzsc$1799070518=$8["ony"];if(LzEvent["$lzsc$isa"]?LzEvent.$lzsc$isa($lzsc$1799070518):$lzsc$1799070518 instanceof LzEvent){
if($lzsc$1799070518.ready){
$lzsc$1799070518.sendEvent($5)
}}}}};$5+=$8.height
}};this.items=$0;this.background.redraw()
}},"registerRedraw",function($0){
this.__drawnitems[$0.getUID()]=$0;this.background.redraw()
},"unregisterRedraw",function($0){
delete this.__drawnitems[$0.getUID()];this.background.redraw()
},"__handlemouse",function($0){
with(this){
if(!$0){
this.hide();return
};do{
if(lz.lzcontextmenu["$lzsc$isa"]?lz.lzcontextmenu.$lzsc$isa($0):$0 instanceof lz.lzcontextmenu){
return
};$0=$0.immediateparent
}while($0!==canvas);this.hide()
}},"background",void 0,"container",void 0],["tagname","lzcontextmenu","children",[{attrs:{$classrootdepth:1,$delegates:["oninit","$m18",null,"onwidth","__doredraw",null,"onheight","__doredraw",null,"oncontext","__doredraw",null,"onvisible","__doredraw",null],height:new LzAlwaysExpr("$m15","$m16"),name:"background",width:new LzAlwaysExpr("$m13","$m14")},"class":$lzc$class_$2Fcanvas$2F$40lzcontextmenu$2F$40background},{attrs:{$classrootdepth:1,name:"container",x:3,y:3},"class":LzView},{attrs:"container","class":$lzc$class_userClassPlacement}],"attributes",new LzInheritedHash(LzView.attributes)]);(function($0){
with($0){
with($0.prototype){
LzNode.mergeAttributes({$delegates:["oninit","$m12",null],__drawnitems:{},__overnow:null,inset:5,options:{ignorelayout:true},visible:false},$lzc$class_lzcontextmenu.attributes)
}}})($lzc$class_lzcontextmenu);lz["lzcontextmenuseparator"]=$lzc$class_lzcontextmenuseparator;lz["lzcontextmenutext"]=$lzc$class_lzcontextmenutext;lz["lzcontextmenudisabled"]=$lzc$class_lzcontextmenudisabled;lz["lzcontextmenu"]=$lzc$class_lzcontextmenu;LzResourceLibrary.lzfocusbracket_rsrc={ptype:"sr",frames:["lps/components/lz/resources/focus/focus_top_lft.png","lps/components/lz/resources/focus/focus_top_rt.png","lps/components/lz/resources/focus/focus_bot_lft.png","lps/components/lz/resources/focus/focus_bot_rt.png"],width:7,height:7,sprite:"lps/components/lz/resources/focus/focus_top_lft.sprite.png",spriteoffset:0};LzResourceLibrary.lzfocusbracket_shdw={ptype:"sr",frames:["lps/components/lz/resources/focus/focus_top_lft_shdw.png","lps/components/lz/resources/focus/focus_top_rt_shdw.png","lps/components/lz/resources/focus/focus_bot_lft_shdw.png","lps/components/lz/resources/focus/focus_bot_rt_shdw.png"],width:9,height:9,sprite:"lps/components/lz/resources/focus/focus_top_lft_shdw.sprite.png",spriteoffset:7};LzResourceLibrary.lzbutton_face_rsc={ptype:"sr",frames:["lps/components/lz/resources/button/simpleface_up.png","lps/components/lz/resources/button/simpleface_mo.png","lps/components/lz/resources/button/simpleface_dn.png","lps/components/lz/resources/button/autoPng/simpleface_dsbl.png"],width:2,height:18,sprite:"lps/components/lz/resources/button/simpleface_up.sprite.png",spriteoffset:16};LzResourceLibrary.lzbutton_bezel_inner_rsc={ptype:"sr",frames:["lps/components/lz/resources/autoPng/bezel_inner_up.png","lps/components/lz/resources/autoPng/bezel_inner_up.png","lps/components/lz/resources/autoPng/bezel_inner_dn.png","lps/components/lz/resources/autoPng/outline_dsbl.png"],width:500,height:500,sprite:"lps/components/lz/resources/autoPng/bezel_inner_up.sprite.png",spriteoffset:34};LzResourceLibrary.lzbutton_bezel_outer_rsc={ptype:"sr",frames:["lps/components/lz/resources/autoPng/bezel_outer_up.png","lps/components/lz/resources/autoPng/bezel_outer_up.png","lps/components/lz/resources/autoPng/bezel_outer_dn.png","lps/components/lz/resources/autoPng/transparent.png","lps/components/lz/resources/autoPng/default_outline.png"],width:500,height:500,sprite:"lps/components/lz/resources/autoPng/bezel_outer_up.sprite.png",spriteoffset:534};LzResourceLibrary.lzcheckbox_rsrc={ptype:"sr",frames:["lps/components/lz/resources/checkbox/autoPng/checkbox_off.png","lps/components/lz/resources/checkbox/autoPng/checkbox_off_mo.png","lps/components/lz/resources/checkbox/autoPng/checkbox_on.png","lps/components/lz/resources/checkbox/autoPng/checkbox_disable_off.png","lps/components/lz/resources/checkbox/autoPng/checkbox_on.png","lps/components/lz/resources/checkbox/autoPng/checkbox_on_mo.png","lps/components/lz/resources/checkbox/autoPng/checkbox_off.png","lps/components/lz/resources/checkbox/autoPng/checkbox_disable_on.png"],width:15,height:14,sprite:"lps/components/lz/resources/checkbox/autoPng/checkbox_off.sprite.png",spriteoffset:1034};LzResourceLibrary.lzradio_rsrc={ptype:"sr",frames:["lps/components/lz/resources/radio/autoPng/radiobtn_up.png","lps/components/lz/resources/radio/autoPng/radiobtn_mo.png","lps/components/lz/resources/radio/autoPng/radiobtn_dn.png","lps/components/lz/resources/radio/autoPng/radiobtn_dsbl_up.png","lps/components/lz/resources/radio/autoPng/radiobtn_dn.png","lps/components/lz/resources/radio/autoPng/radiobtn_dn.png","lps/components/lz/resources/radio/autoPng/radiobtn_dn.png","lps/components/lz/resources/radio/autoPng/radiobtn_dsbl_dn.png"],width:14,height:14,sprite:"lps/components/lz/resources/radio/autoPng/radiobtn_up.sprite.png",spriteoffset:1048};LzResourceLibrary.footer_logo={ptype:"sr",frames:["lps/assets/logo_laszlo_footer.gif"],width:69.92950439453125,height:69.92950439453125,spriteoffset:1062};LzResourceLibrary.__allcss={path:"dev-console.sprite.png"};var appdata=null;var main=null;var rg_runtime=null;var rb8=null;var rb9=null;var rb10=null;var rbdhtml=null;var cb_debug=null;var cb_backtrace=null;var cb_usemastersprite=null;var cb_remotedebug=null;Class.make("$lzc$class_m2",LzCanvas,["copyright",void 0,"app_runtime",void 0,"runtime_dhtml_enabled",void 0,"runtime_swf8_enabled",void 0,"runtime_swf9_enabled",void 0,"runtime_swf10_enabled",void 0,"app_debug",void 0,"app_backtrace",void 0,"app_console_debug",void 0,"app_floating_window",void 0,"app_fullpath",void 0,"app_query",void 0,"app_opt_url",void 0,"app_unopt_url",void 0,"app_url",void 0,"app_opt_exists",void 0,"app_lps_root",void 0,"app_lzt",void 0,"app_uid",void 0,"app_usemastersprite",void 0,"$m1",function($0){
with(this){
appdata.setAttribute("initialdata",lz.Browser.getInitArg("appinfo"));setAttribute("app_runtime",appdata.getPointer().xpathQuery("/info/@runtime"));this.setupRuntimes();if(app_runtime==null){
setAttribute("app_runtime",appdata.getPointer().xpathQuery("/request/param[@name = 'lzr']/@value"))
};setAttribute("app_debug",appdata.getPointer().xpathQuery("/request/param[@name = 'debug']/@value"));setAttribute("app_backtrace",appdata.getPointer().xpathQuery("/request/param[@name = 'lzbacktrace']/@value"));if(app_debug=="y")app_debug="true";setAttribute("app_console_debug",appdata.getPointer().xpathQuery("/request/@console-remote-debug")=="true");setAttribute("app_floating_window",appdata.getPointer().xpathQuery("/request/@console-floating-window")=="true");setAttribute("app_fullpath",appdata.getPointer().xpathQuery("/request/@fullpath"));setAttribute("app_query",appdata.getPointer().xpathQuery("/request/@query_args"));setAttribute("app_opt_url",appdata.getPointer().xpathQuery("/request/@opt-url"));setAttribute("app_unopt_url",appdata.getPointer().xpathQuery("/request/@unopt-url"));setAttribute("app_url",appdata.getPointer().xpathQuery("/request/@url"));setAttribute("app_opt_exists",appdata.getPointer().xpathQuery("/request/@opt-exists"));setAttribute("app_lps_root",appdata.getPointer().xpathQuery("/request/@lps"));setAttribute("app_lzt",null);setAttribute("app_uid",app_fullpath);setAttribute("lzr","dhtml");var $1=LzParam.parseQueryString(app_query);setAttribute("app_usemastersprite",$1.lzusemastersprite);canvas.width=document.body.clientWidth
}},"setupRuntimes",function(){
with(this){
var $0=lz.Browser.getInitArg("runtimes").split(",");for(var $1 in $0){
if($0[$1]=="dhtml")canvas.setAttribute("runtime_dhtml_enabled",true);if($0[$1]=="swf8")canvas.setAttribute("runtime_swf8_enabled",true);if($0[$1]=="swf9")canvas.setAttribute("runtime_swf9_enabled",true);if($0[$1]=="swf10")canvas.setAttribute("runtime_swf10_enabled",true)
}}},"deploySOLO",function(){
with(this){
var $0=escape(app_fullpath.substring(app_lps_root.length));if(app_runtime=="dhtml"){
var $1=app_lps_root+"/lps/admin/solo-dhtml-deploy.jsp?appurl="+$0
}else{
var $1=app_lps_root+"/lps/admin/solo-deploy.jsp?appurl="+$0+"&runtime="+app_runtime
};this.loadURL($1,"_blank")
}},"debugApp",function(){
with(this){
cb_debug.setValue(true);canvas.reloadApp()
}},"viewSource",function(){
with(this){
setAttribute("app_lzt","source");canvas.reloadApp()
}},"viewWrapper",function(){
with(this){
setAttribute("app_lzt","deployment");canvas.reloadApp()
}},"viewDocs",function(){
with(this){
var $0=app_lps_root+"/index.html";this.loadURL($0)
}},"viewDev",function(){
var $0="http://www.openlaszlo.org/community";this.loadURL($0)
},"viewForums",function(){
var $0="http://forum.openlaszlo.org/";this.loadURL($0)
},"loadURL",function($0,$1){
with(this){
switch(arguments.length){
case 1:
$1="_top";

};lz.Browser.loadURL($0,$1)
}},"gotoApp",function(){
with(this){
var $0=app_fullpath.substring(0,app_fullpath.length-4)+".lzx";setAttribute("app_fullpath",$0);this.reloadApp()
}},"reloadApp",function(){
with(this){
var $0=rg_runtime.value;var $1=cb_debug.value;var $2=cb_backtrace.value;var $3=cb_remotedebug.value;var $4=new LzParam();var $5=LzParam.parseQueryString(app_query);$5["debug"]=$1;$5["lzbacktrace"]=$2;$5["lzr"]=$0;$5["lzusemastersprite"]=cb_usemastersprite.value;if($0!="dhtml"){
delete $5["lzusemastersprite"]
};if($3){
$5["lzconsoledebug"]=true;$5["debug"]=true
}else{
delete $5.lzconsoledebug;delete $5.remotedebug
};if(app_lzt!=null){
$5["lzt"]=app_lzt
};if($5.debug+""=="false"){
delete $5["debug"]
}else{
$5.debug="true"
};if($5.lzbacktrace+""=="false"){
delete $5["lzbacktrace"]
}else{
$5.lzbacktrace="true"
};var $6={};for(var $7 in $5){
if($7=="")continue;if($7.indexOf("#38;")!=-1){
var $8=$7.indexOf("#38;");$7=$7.substring($8+4,$7.length)
};if($6[$7])continue;$4.setValue($7,$5[$7]);$6[$7]=true
};var $9=app_fullpath+"?"+$4.serialize();this.loadURL($9)
}},"displayObjectByID",function($0){
with(this){
receivingLC.send(appListenerName,"displayObj",$0)
}},"showLogMessage",function($0){
with(this){
console.writeRaw(lz.Browser.xmlEscape($0)+"<br>")
}},"showWarning",function($0,$1,$2){
with(this){
console.writeRaw($2)
}},"sendConsoleAlive",function(){
with(this){
if(receivingLC==null){
var $0=new remoteConnectionListener();canvas.setAttribute("consoleListenerName","lc_consoledebug"+app_uid);canvas.setAttribute("appListenerName","lc_appdebug"+app_uid);canvas.setAttribute("receivingLC",$0.setupConnection());receivingLC.send(appListenerName,"consoleAlive",true)
}else{
console.writeRaw('<font color="#FF0000">Connection is already open to app.</font><br/>')
}}},"remoteEval",function($0){
with(this){
receivingLC.send(appListenerName,"evalExpr",$0)
}},"receivingLC",void 0,"consolelistenerName",void 0,"appListenerName",void 0],["attributes",new LzInheritedHash(LzCanvas.attributes)]);canvas=new $lzc$class_m2(null,{$delegates:["oninit","$m1",null],__LZproxied:"true",appListenerName:void 0,app_backtrace:void 0,app_console_debug:void 0,app_debug:void 0,app_floating_window:void 0,app_fullpath:void 0,app_lps_root:void 0,app_lzt:void 0,app_opt_exists:void 0,app_opt_url:void 0,app_query:void 0,app_runtime:void 0,app_uid:void 0,app_unopt_url:void 0,app_url:void 0,app_usemastersprite:void 0,appbuilddate:"2010-01-14T21:21:33Z",bgcolor:8750489,consolelistenerName:void 0,copyright:"Copyright 2001-2010 Laszlo Systems, Inc.  All Rights Reserved.  Use is subject to license terms.",embedfonts:true,fontname:"Verdana,Vera,sans-serif",fontsize:11,fontstyle:"plain",height:370,lpsbuild:"15464 /Users/hqm/openlaszlo/trunk",lpsbuilddate:"2010-01-14T20:33:26Z",lpsrelease:"Latest",lpsversion:"4.8.x",proxied:false,receivingLC:void 0,runtime:"dhtml",runtime_dhtml_enabled:false,runtime_swf10_enabled:false,runtime_swf8_enabled:false,runtime_swf9_enabled:false,width:"100%"});lz.colors.offwhite=15921906;lz.colors.gray10=1710618;lz.colors.gray20=3355443;lz.colors.gray30=5066061;lz.colors.gray40=6710886;lz.colors.gray50=8355711;lz.colors.gray60=10066329;lz.colors.gray70=11776947;lz.colors.gray80=13421772;lz.colors.gray90=15066597;lz.colors.iceblue1=3298963;lz.colors.iceblue2=5472718;lz.colors.iceblue3=12240085;lz.colors.iceblue4=14017779;lz.colors.iceblue5=15659509;lz.colors.palegreen1=4290113;lz.colors.palegreen2=11785139;lz.colors.palegreen3=12637341;lz.colors.palegreen4=13888170;lz.colors.palegreen5=15725032;lz.colors.gold1=9331721;lz.colors.gold2=13349195;lz.colors.gold3=15126388;lz.colors.gold4=16311446;lz.colors.sand1=13944481;lz.colors.sand2=14276546;lz.colors.sand3=15920859;lz.colors.sand4=15986401;lz.colors.ltpurple1=6575768;lz.colors.ltpurple2=12038353;lz.colors.ltpurple3=13353453;lz.colors.ltpurple4=15329264;lz.colors.grayblue=12501704;lz.colors.graygreen=12635328;lz.colors.graypurple=10460593;lz.colors.ltblue=14540287;lz.colors.ltgreen=14548957;Class.make("$lzc$class_basefocusview",LzView,["active",void 0,"$lzc$set_active",function($0){
with(this){
setActive($0)
}},"target",void 0,"$lzc$set_target",function($0){
with(this){
setTarget($0)
}},"duration",void 0,"_animatorcounter",void 0,"ontarget",void 0,"_nexttarget",void 0,"onactive",void 0,"_xydelegate",void 0,"_widthdel",void 0,"_heightdel",void 0,"_delayfadeoutDL",void 0,"_dofadeout",void 0,"_onstopdel",void 0,"reset",function(){
with(this){
this.setAttribute("x",0);this.setAttribute("y",0);this.setAttribute("width",canvas.width);this.setAttribute("height",canvas.height);setTarget(null)
}},"setActive",function($0){
this.active=$0;if(this.onactive)this.onactive.sendEvent($0)
},"doFocus",function($0){
with(this){
this._dofadeout=false;this.bringToFront();if(this.target)this.setTarget(null);this.setAttribute("visibility",this.active?"visible":"hidden");this._nexttarget=$0;if(visible){
this._animatorcounter+=1;var $1=null;var $2;var $3;var $4;var $5;if($0["getFocusRect"])$1=$0.getFocusRect();if($1){
$2=$1[0];$3=$1[1];$4=$1[2];$5=$1[3]
}else{
$2=$0.getAttributeRelative("x",canvas);$3=$0.getAttributeRelative("y",canvas);$4=$0.getAttributeRelative("width",canvas);$5=$0.getAttributeRelative("height",canvas)
};var $6=this.animate("x",$2,duration);this.animate("y",$3,duration);this.animate("width",$4,duration);this.animate("height",$5,duration);if(this.capabilities["minimize_opacity_changes"]){
this.setAttribute("visibility","visible")
}else{
this.animate("opacity",1,500)
};if(!this._onstopdel)this._onstopdel=new LzDelegate(this,"stopanim");this._onstopdel.register($6,"onstop")
};if(this._animatorcounter<1){
this.setTarget(this._nexttarget);var $1=null;var $2;var $3;var $4;var $5;if($0["getFocusRect"])$1=$0.getFocusRect();if($1){
$2=$1[0];$3=$1[1];$4=$1[2];$5=$1[3]
}else{
$2=$0.getAttributeRelative("x",canvas);$3=$0.getAttributeRelative("y",canvas);$4=$0.getAttributeRelative("width",canvas);$5=$0.getAttributeRelative("height",canvas)
};this.setAttribute("x",$2);this.setAttribute("y",$3);this.setAttribute("width",$4);this.setAttribute("height",$5)
}}},"stopanim",function($0){
with(this){
this._animatorcounter-=1;if(this._animatorcounter<1){
this._dofadeout=true;if(!this._delayfadeoutDL)this._delayfadeoutDL=new LzDelegate(this,"fadeout");lz.Timer.addTimer(this._delayfadeoutDL,1000);this.setTarget(_nexttarget);this._onstopdel.unregisterAll()
}}},"fadeout",function($0){
with(this){
if(_dofadeout){
if(this.capabilities["minimize_opacity_changes"]){
this.setAttribute("visibility","hidden")
}else{
this.animate("opacity",0,500)
}};this._delayfadeoutDL.unregisterAll()
}},"setTarget",function($0){
with(this){
this.target=$0;if(!this._xydelegate){
this._xydelegate=new LzDelegate(this,"followXY")
}else{
this._xydelegate.unregisterAll()
};if(!this._widthdel){
this._widthdel=new LzDelegate(this,"followWidth")
}else{
this._widthdel.unregisterAll()
};if(!this._heightdel){
this._heightdel=new LzDelegate(this,"followHeight")
}else{
this._heightdel.unregisterAll()
};if(this.target==null)return;var $1=$0;var $2=0;while($1!=canvas){
this._xydelegate.register($1,"onx");this._xydelegate.register($1,"ony");$1=$1.immediateparent;$2++
};this._widthdel.register($0,"onwidth");this._heightdel.register($0,"onheight");followXY(null);followWidth(null);followHeight(null)
}},"followXY",function($0){
with(this){
var $1=null;if(target["getFocusRect"])$1=target.getFocusRect();if($1){
this.setAttribute("x",$1[0]);this.setAttribute("y",$1[1])
}else{
this.setAttribute("x",this.target.getAttributeRelative("x",canvas));this.setAttribute("y",this.target.getAttributeRelative("y",canvas))
}}},"followWidth",function($0){
with(this){
var $1=null;if(target["getFocusRect"])$1=target.getFocusRect();if($1){
this.setAttribute("width",$1[2])
}else{
this.setAttribute("width",this.target.width)
}}},"followHeight",function($0){
with(this){
var $1=null;if(target["getFocusRect"])$1=target.getFocusRect();if($1){
this.setAttribute("height",$1[3])
}else{
this.setAttribute("height",this.target.height)
}}},"$m3",function(){
with(this){
var $0=lz.Focus;return $0
}},"$m4",function($0){
with(this){
this.setActive(lz.Focus.focuswithkey);if($0){
this.doFocus($0)
}else{
this.reset();if(this.active){
this.setActive(false)
}}}}],["tagname","basefocusview","attributes",new LzInheritedHash(LzView.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onstop","stopanim",null,"onfocus","$m4","$m3"],_animatorcounter:0,_delayfadeoutDL:null,_dofadeout:false,_heightdel:null,_nexttarget:null,_onstopdel:null,_widthdel:null,_xydelegate:null,active:false,duration:400,initstage:"late",onactive:LzDeclaredEvent,ontarget:LzDeclaredEvent,options:{ignorelayout:true},target:null,visible:false},$lzc$class_basefocusview.attributes)
}}})($lzc$class_basefocusview);Class.make("$lzc$class_m21",LzView,["$m5",function($0){
with(this){
var $1=-classroot.offset;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m6",function(){
with(this){
return [classroot,"offset"]
}},"$m7",function($0){
with(this){
var $1=-classroot.offset;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m8",function(){
with(this){
return [classroot,"offset"]
}},"$classrootdepth",void 0],["children",[{attrs:{$classrootdepth:2,opacity:0.25,resource:"lzfocusbracket_shdw",x:1,y:1},"class":LzView},{attrs:{$classrootdepth:2,resource:"lzfocusbracket_rsrc"},"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m22",LzView,["$m9",function($0){
with(this){
var $1=parent.width-width+classroot.offset;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m10",function(){
with(this){
return [parent,"width",this,"width",classroot,"offset"]
}},"$m11",function($0){
with(this){
var $1=-classroot.offset;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m12",function(){
with(this){
return [classroot,"offset"]
}},"$classrootdepth",void 0],["children",[{attrs:{$classrootdepth:2,frame:2,opacity:0.25,resource:"lzfocusbracket_shdw",x:1,y:1},"class":LzView},{attrs:{$classrootdepth:2,frame:2,resource:"lzfocusbracket_rsrc"},"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m23",LzView,["$m13",function($0){
with(this){
var $1=-classroot.offset;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m14",function(){
with(this){
return [classroot,"offset"]
}},"$m15",function($0){
with(this){
var $1=parent.height-height+classroot.offset;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m16",function(){
with(this){
return [parent,"height",this,"height",classroot,"offset"]
}},"$classrootdepth",void 0],["children",[{attrs:{$classrootdepth:2,frame:3,opacity:0.25,resource:"lzfocusbracket_shdw",x:1,y:1},"class":LzView},{attrs:{$classrootdepth:2,frame:3,resource:"lzfocusbracket_rsrc"},"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m24",LzView,["$m17",function($0){
with(this){
var $1=parent.width-width+classroot.offset;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m18",function(){
with(this){
return [parent,"width",this,"width",classroot,"offset"]
}},"$m19",function($0){
with(this){
var $1=parent.height-height+classroot.offset;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m20",function(){
with(this){
return [parent,"height",this,"height",classroot,"offset"]
}},"$classrootdepth",void 0],["children",[{attrs:{$classrootdepth:2,frame:4,opacity:0.25,resource:"lzfocusbracket_shdw",x:1,y:1},"class":LzView},{attrs:{$classrootdepth:2,frame:4,resource:"lzfocusbracket_rsrc"},"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_focusoverlay",$lzc$class_basefocusview,["offset",void 0,"topleft",void 0,"topright",void 0,"bottomleft",void 0,"bottomright",void 0,"doFocus",function($0){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["doFocus"]||this.nextMethod(arguments.callee,"doFocus")).call(this,$0);if(visible)this.bounce()
}},"bounce",function(){
with(this){
this.animate("offset",12,duration/2);this.animate("offset",5,duration)
}}],["tagname","focusoverlay","children",[{attrs:{$classrootdepth:1,name:"topleft",x:new LzAlwaysExpr("$m5","$m6"),y:new LzAlwaysExpr("$m7","$m8")},"class":$lzc$class_m21},{attrs:{$classrootdepth:1,name:"topright",x:new LzAlwaysExpr("$m9","$m10"),y:new LzAlwaysExpr("$m11","$m12")},"class":$lzc$class_m22},{attrs:{$classrootdepth:1,name:"bottomleft",x:new LzAlwaysExpr("$m13","$m14"),y:new LzAlwaysExpr("$m15","$m16")},"class":$lzc$class_m23},{attrs:{$classrootdepth:1,name:"bottomright",x:new LzAlwaysExpr("$m17","$m18"),y:new LzAlwaysExpr("$m19","$m20")},"class":$lzc$class_m24}],"attributes",new LzInheritedHash($lzc$class_basefocusview.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({offset:5},$lzc$class_focusoverlay.attributes)
}}})($lzc$class_focusoverlay);Class.make("$lzc$class__componentmanager",LzNode,["focusclass",void 0,"keyhandlers",void 0,"lastsdown",void 0,"lastedown",void 0,"defaults",void 0,"currentdefault",void 0,"defaultstyle",void 0,"ondefaultstyle",void 0,"init",function(){
with(this){
var $0=this.focusclass;if(typeof canvas.focusclass!="undefined"){
$0=canvas.focusclass
};if($0!=null){
canvas.__focus=new (lz[$0])(canvas);canvas.__focus.reset()
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this)
}},"_lastkeydown",void 0,"upkeydel",void 0,"$m25",function(){
with(this){
var $0=lz.Keys;return $0
}},"dispatchKeyDown",function($0){
with(this){
var $1=false;if($0==32){
this.lastsdown=null;var $2=lz.Focus.getFocus();if($2 instanceof lz.basecomponent){
$2.doSpaceDown();this.lastsdown=$2
};$1=true
}else if($0==13&&this.currentdefault){
this.lastedown=this.currentdefault;this.currentdefault.doEnterDown();$1=true
};if($1){
if(!this.upkeydel)this.upkeydel=new LzDelegate(this,"dispatchKeyTimer");this._lastkeydown=$0;lz.Timer.addTimer(this.upkeydel,50)
}}},"dispatchKeyTimer",function($0){
if(this._lastkeydown==32&&this.lastsdown!=null){
this.lastsdown.doSpaceUp();this.lastsdown=null
}else if(this._lastkeydown==13&&this.currentdefault&&this.currentdefault==this.lastedown){
this.currentdefault.doEnterUp()
}},"findClosestDefault",function($0){
with(this){
if(!this.defaults){
return null
};var $1=null;var $2=null;var $3=this.defaults;$0=$0||canvas;var $4=lz.ModeManager.getModalView();for(var $5=0;$5<$3.length;$5++){
var $6=$3[$5];if($4&&!$6.childOf($4)){
continue
};var $7=this.findCommonParent($6,$0);if($7&&(!$1||$7.nodeLevel>$1.nodeLevel)){
$1=$7;$2=$6
}};return $2
}},"findCommonParent",function($0,$1){
while($0.nodeLevel>$1.nodeLevel){
$0=$0.immediateparent;if(!$0.visible)return null
};while($1.nodeLevel>$0.nodeLevel){
$1=$1.immediateparent;if(!$1.visible)return null
};while($0!=$1){
$0=$0.immediateparent;$1=$1.immediateparent;if(!$0.visible||!$1.visible)return null
};return $0
},"makeDefault",function($0){
with(this){
if(!this.defaults)this.defaults=[];this.defaults.push($0);this.checkDefault(lz.Focus.getFocus())
}},"unmakeDefault",function($0){
with(this){
if(!this.defaults)return;for(var $1=0;$1<this.defaults.length;$1++){
if(this.defaults[$1]==$0){
this.defaults.splice($1,1);this.checkDefault(lz.Focus.getFocus());return
}}}},"$m26",function(){
with(this){
var $0=lz.Focus;return $0
}},"checkDefault",function($0){
with(this){
if(!($0 instanceof lz.basecomponent)||!$0.doesenter){
if($0 instanceof lz.inputtext&&$0.multiline){
$0=null
}else{
$0=this.findClosestDefault($0)
}};if($0==this.currentdefault)return;if(this.currentdefault){
this.currentdefault.setAttribute("hasdefault",false)
};this.currentdefault=$0;if($0){
$0.setAttribute("hasdefault",true)
}}},"$m27",function(){
with(this){
var $0=lz.ModeManager;return $0
}},"$m28",function($0){
with(this){
switch(arguments.length){
case 0:
$0=null;

};if(lz.Focus.getFocus()==null){
this.checkDefault(null)
}}},"setDefaultStyle",function($0){
this.defaultstyle=$0;if(this.ondefaultstyle)this.ondefaultstyle.sendEvent($0)
},"getDefaultStyle",function(){
with(this){
if(this.defaultstyle==null){
this.defaultstyle=new (lz.style)(canvas,{isdefault:true})
};return this.defaultstyle
}}],["tagname","_componentmanager","attributes",new LzInheritedHash(LzNode.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onkeydown","dispatchKeyDown","$m25","onfocus","checkDefault","$m26","onmode","$m28","$m27"],_lastkeydown:0,currentdefault:null,defaults:null,defaultstyle:null,focusclass:"focusoverlay",keyhandlers:null,lastedown:null,lastsdown:null,ondefaultstyle:LzDeclaredEvent,upkeydel:null},$lzc$class__componentmanager.attributes)
}}})($lzc$class__componentmanager);Class.make("$lzc$class_style",LzNode,["isstyle",void 0,"$m29",function($0){
with(this){
this.setAttribute("canvascolor",LzColorUtils.convertColor("null"))
}},"canvascolor",void 0,"$lzc$set_canvascolor",function($0){
with(this){
setCanvasColor($0)
}},"$m30",function($0){
with(this){
this.setAttribute("textcolor",LzColorUtils.convertColor("gray10"))
}},"textcolor",void 0,"$lzc$set_textcolor",function($0){
with(this){
setStyleAttr($0,"textcolor")
}},"$m31",function($0){
with(this){
this.setAttribute("textfieldcolor",LzColorUtils.convertColor("white"))
}},"textfieldcolor",void 0,"$lzc$set_textfieldcolor",function($0){
with(this){
setStyleAttr($0,"textfieldcolor")
}},"$m32",function($0){
with(this){
this.setAttribute("texthilitecolor",LzColorUtils.convertColor("iceblue1"))
}},"texthilitecolor",void 0,"$lzc$set_texthilitecolor",function($0){
with(this){
setStyleAttr($0,"texthilitecolor")
}},"$m33",function($0){
with(this){
this.setAttribute("textselectedcolor",LzColorUtils.convertColor("black"))
}},"textselectedcolor",void 0,"$lzc$set_textselectedcolor",function($0){
with(this){
setStyleAttr($0,"textselectedcolor")
}},"$m34",function($0){
with(this){
this.setAttribute("textdisabledcolor",LzColorUtils.convertColor("gray60"))
}},"textdisabledcolor",void 0,"$lzc$set_textdisabledcolor",function($0){
with(this){
setStyleAttr($0,"textdisabledcolor")
}},"$m35",function($0){
with(this){
this.setAttribute("basecolor",LzColorUtils.convertColor("offwhite"))
}},"basecolor",void 0,"$lzc$set_basecolor",function($0){
with(this){
setStyleAttr($0,"basecolor")
}},"$m36",function($0){
with(this){
this.setAttribute("bgcolor",LzColorUtils.convertColor("white"))
}},"bgcolor",void 0,"$lzc$set_bgcolor",function($0){
with(this){
setStyleAttr($0,"bgcolor")
}},"$m37",function($0){
with(this){
this.setAttribute("hilitecolor",LzColorUtils.convertColor("iceblue4"))
}},"hilitecolor",void 0,"$lzc$set_hilitecolor",function($0){
with(this){
setStyleAttr($0,"hilitecolor")
}},"$m38",function($0){
with(this){
this.setAttribute("selectedcolor",LzColorUtils.convertColor("iceblue3"))
}},"selectedcolor",void 0,"$lzc$set_selectedcolor",function($0){
with(this){
setStyleAttr($0,"selectedcolor")
}},"$m39",function($0){
with(this){
this.setAttribute("disabledcolor",LzColorUtils.convertColor("gray30"))
}},"disabledcolor",void 0,"$lzc$set_disabledcolor",function($0){
with(this){
setStyleAttr($0,"disabledcolor")
}},"$m40",function($0){
with(this){
this.setAttribute("bordercolor",LzColorUtils.convertColor("gray40"))
}},"bordercolor",void 0,"$lzc$set_bordercolor",function($0){
with(this){
setStyleAttr($0,"bordercolor")
}},"$m41",function($0){
this.setAttribute("bordersize",1)
},"bordersize",void 0,"$lzc$set_bordersize",function($0){
with(this){
setStyleAttr($0,"bordersize")
}},"$m42",function($0){
with(this){
this.setAttribute("menuitembgcolor",LzColorUtils.convertColor("textfieldcolor"))
}},"menuitembgcolor",void 0,"isdefault",void 0,"$lzc$set_isdefault",function($0){
with(this){
_setdefault($0)
}},"onisdefault",void 0,"_setdefault",function($0){
with(this){
this.isdefault=$0;if(isdefault){
lz._componentmanager.service.setDefaultStyle(this);if(this["canvascolor"]!=null){
canvas.setAttribute("bgcolor",this.canvascolor)
}};if(this.onisdefault)this.onisdefault.sendEvent(this)
}},"onstylechanged",void 0,"setStyleAttr",function($0,$1){
this[$1]=$0;if(this["on"+$1])this["on"+$1].sendEvent($1);if(this.onstylechanged)this.onstylechanged.sendEvent(this)
},"setCanvasColor",function($0){
with(this){
if(this.isdefault&&$0!=null){
canvas.setAttribute("bgcolor",$0)
};this.canvascolor=$0;if(this.onstylechanged)this.onstylechanged.sendEvent(this)
}},"extend",function($0){
with(this){
var $1=new (lz.style)();$1.canvascolor=this.canvascolor;$1.textcolor=this.textcolor;$1.textfieldcolor=this.textfieldcolor;$1.texthilitecolor=this.texthilitecolor;$1.textselectedcolor=this.textselectedcolor;$1.textdisabledcolor=this.textdisabledcolor;$1.basecolor=this.basecolor;$1.bgcolor=this.bgcolor;$1.hilitecolor=this.hilitecolor;$1.selectedcolor=this.selectedcolor;$1.disabledcolor=this.disabledcolor;$1.bordercolor=this.bordercolor;$1.bordersize=this.bordersize;$1.menuitembgcolor=this.menuitembgcolor;$1.isdefault=this.isdefault;for(var $2 in $0){
$1[$2]=$0[$2]
};new LzDelegate($1,"_forwardstylechanged",this,"onstylechanged");return $1
}},"_forwardstylechanged",function($0){
if(this.onstylechanged)this.onstylechanged.sendEvent(this)
}],["tagname","style","attributes",new LzInheritedHash(LzNode.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({basecolor:new LzOnceExpr("$m35"),bgcolor:new LzOnceExpr("$m36"),bordercolor:new LzOnceExpr("$m40"),bordersize:new LzOnceExpr("$m41"),canvascolor:new LzOnceExpr("$m29"),disabledcolor:new LzOnceExpr("$m39"),hilitecolor:new LzOnceExpr("$m37"),isdefault:false,isstyle:true,menuitembgcolor:new LzOnceExpr("$m42"),onisdefault:LzDeclaredEvent,onstylechanged:LzDeclaredEvent,selectedcolor:new LzOnceExpr("$m38"),textcolor:new LzOnceExpr("$m30"),textdisabledcolor:new LzOnceExpr("$m34"),textfieldcolor:new LzOnceExpr("$m31"),texthilitecolor:new LzOnceExpr("$m32"),textselectedcolor:new LzOnceExpr("$m33")},$lzc$class_style.attributes)
}}})($lzc$class_style);canvas.LzInstantiateView({"class":lz.script,attrs:{script:function(){
lz._componentmanager.service=new (lz._componentmanager)(canvas,null,null,true)
}}},1);Class.make("$lzc$class_statictext",LzText,null,["tagname","statictext","attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_basecomponent",LzView,["enabled",void 0,"$lzc$set_focusable",function($0){
with(this){
_setFocusable($0)
}},"_focusable",void 0,"onfocusable",void 0,"text",void 0,"doesenter",void 0,"$lzc$set_doesenter",function($0){
this._setDoesEnter($0)
},"$m43",function($0){
var $1=this.enabled&&(this._parentcomponent?this._parentcomponent._enabled:true);if($1!==this["_enabled"]||!this.inited){
this.setAttribute("_enabled",$1)
}},"$m44",function(){
return [this,"enabled",this,"_parentcomponent",this._parentcomponent,"_enabled"]
},"_enabled",void 0,"$lzc$set__enabled",function($0){
this._setEnabled($0)
},"_parentcomponent",void 0,"_initcomplete",void 0,"isdefault",void 0,"$lzc$set_isdefault",function($0){
this._setIsDefault($0)
},"onisdefault",void 0,"hasdefault",void 0,"_setEnabled",function($0){
with(this){
this._enabled=$0;var $1=this._enabled&&this._focusable;if($1!=this.focusable){
this.focusable=$1;if(this.onfocusable.ready)this.onfocusable.sendEvent()
};if(_initcomplete)_showEnabled();if(this.on_enabled.ready)this.on_enabled.sendEvent()
}},"_setFocusable",function($0){
this._focusable=$0;if(this.enabled){
this.focusable=this._focusable;if(this.onfocusable.ready)this.onfocusable.sendEvent()
}else{
this.focusable=false
}},"construct",function($0,$1){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);var $2=this.immediateparent;while($2!=canvas){
if(lz.basecomponent["$lzsc$isa"]?lz.basecomponent.$lzsc$isa($2):$2 instanceof lz.basecomponent){
this._parentcomponent=$2;break
};$2=$2.immediateparent
}}},"init",function(){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);this._initcomplete=true;this._mousedownDel=new LzDelegate(this,"_doMousedown",this,"onmousedown");if(this.styleable){
_usestyle()
};if(!this["_enabled"])_showEnabled()
}},"_doMousedown",function($0){},"doSpaceDown",function(){
return false
},"doSpaceUp",function(){
return false
},"doEnterDown",function(){
return false
},"doEnterUp",function(){
return false
},"_setIsDefault",function($0){
with(this){
this.isdefault=this["isdefault"]==true;if(this.isdefault==$0)return;if($0){
lz._componentmanager.service.makeDefault(this)
}else{
lz._componentmanager.service.unmakeDefault(this)
};this.isdefault=$0;if(this.onisdefault.ready){
this.onisdefault.sendEvent($0)
}}},"_setDoesEnter",function($0){
with(this){
this.doesenter=$0;if(lz.Focus.getFocus()==this){
lz._componentmanager.service.checkDefault(this)
}}},"updateDefault",function(){
with(this){
lz._componentmanager.service.checkDefault(lz.Focus.getFocus())
}},"$m45",function($0){
this.setAttribute("style",null)
},"style",void 0,"$lzc$set_style",function($0){
with(this){
styleable?setStyle($0):(this.style=null)
}},"styleable",void 0,"_style",void 0,"onstyle",void 0,"_styledel",void 0,"_otherstyledel",void 0,"setStyle",function($0){
with(this){
if(!styleable)return;if($0!=null&&!$0["isstyle"]){
var $1=this._style;if(!$1){
if(this._parentcomponent){
$1=this._parentcomponent.style
}else $1=lz._componentmanager.service.getDefaultStyle()
};$0=$1.extend($0)
};this._style=$0;if($0==null){
if(!this._otherstyledel){
this._otherstyledel=new LzDelegate(this,"_setstyle")
}else{
this._otherstyledel.unregisterAll()
};if(this._parentcomponent&&this._parentcomponent.styleable){
this._otherstyledel.register(this._parentcomponent,"onstyle");$0=this._parentcomponent.style
}else{
this._otherstyledel.register(lz._componentmanager.service,"ondefaultstyle");$0=lz._componentmanager.service.getDefaultStyle()
}}else if(this._otherstyledel){
this._otherstyledel.unregisterAll();this._otherstyledel=null
};_setstyle($0)
}},"_usestyle",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this._initcomplete&&this["style"]&&this.style.isinited){
this._applystyle(this.style)
}},"_setstyle",function($0){
with(this){
if(!this._styledel){
this._styledel=new LzDelegate(this,"_usestyle")
}else{
_styledel.unregisterAll()
};if($0){
_styledel.register($0,"onstylechanged")
};this.style=$0;_usestyle();if(this.onstyle.ready)this.onstyle.sendEvent(this.style)
}},"_applystyle",function($0){},"setTint",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=0;

};if($0.capabilities.colortransform){
if($1!=""&&$1!=null){
var $3=$1;var $4=$3>>16&255;var $5=$3>>8&255;var $6=$3&255;$4+=51;$5+=51;$6+=51;$4=$4/255*100;$5=$5/255*100;$6=$6/255*100;$0.setColorTransform({ra:$4,ga:$5,ba:$6,rb:$2,gb:$2,bb:$2})
}}},"on_enabled",void 0,"_showEnabled",function(){},"acceptValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};this.setAttribute("text",$0)
},"presentValue",function($0){
switch(arguments.length){
case 0:
$0=null;

};return this.text
},"$lzc$presentValue_dependencies",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};return [this,"text"]
},"applyData",function($0){
this.acceptValue($0)
},"updateData",function(){
return this.presentValue()
},"destroy",function(){
with(this){
if(this["isdefault"]&&this.isdefault){
lz._componentmanager.service.unmakeDefault(this)
};if(this._otherstyledel){
this._otherstyledel.unregisterAll();this._otherstyledel=null
};if(this._styledel){
this._styledel.unregisterAll();this._styledel=null
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
}},"toString",function(){
var $0="";var $1="";var $2="";if(this["id"]!=null)$0="  id="+this.id;if(this["name"]!=null)$1=' named "'+this.name+'"';if(this["text"]&&this.text!="")$2="  text="+this.text;return this.constructor.tagname+$1+$0+$2
}],["tagname","basecomponent","attributes",new LzInheritedHash(LzView.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({_enabled:new LzAlwaysExpr("$m43","$m44"),_focusable:true,_initcomplete:false,_otherstyledel:null,_parentcomponent:null,_style:null,_styledel:null,doesenter:false,enabled:true,focusable:true,hasdefault:false,on_enabled:LzDeclaredEvent,onfocusable:LzDeclaredEvent,onisdefault:LzDeclaredEvent,onstyle:LzDeclaredEvent,style:new LzOnceExpr("$m45"),styleable:true,text:""},$lzc$class_basecomponent.attributes)
}}})($lzc$class_basecomponent);Class.make("$lzc$class_basebutton",$lzc$class_basecomponent,["normalResourceNumber",void 0,"overResourceNumber",void 0,"downResourceNumber",void 0,"disabledResourceNumber",void 0,"$m46",function($0){
this.setAttribute("maxframes",this.totalframes)
},"maxframes",void 0,"resourceviewcount",void 0,"$lzc$set_resourceviewcount",function($0){
this.setResourceViewCount($0)
},"respondtomouseout",void 0,"$m47",function($0){
this.setAttribute("reference",this)
},"reference",void 0,"$lzc$set_reference",function($0){
with(this){
setreference($0)
}},"onresourceviewcount",void 0,"_msdown",void 0,"_msin",void 0,"setResourceViewCount",function($0){
this.resourceviewcount=$0;if(this._initcomplete){
if($0>0){
if(this.subviews){
this.maxframes=this.subviews[0].totalframes;if(this.onresourceviewcount){
this.onresourceviewcount.sendEvent()
}}}}},"_callShow",function(){
if(this._msdown&&this._msin&&this.maxframes>=this.downResourceNumber){
this.showDown()
}else if(this._msin&&this.maxframes>=this.overResourceNumber){
this.showOver()
}else this.showUp()
},"$m48",function(){
with(this){
var $0=lz.ModeManager;return $0
}},"$m49",function($0){
if($0&&(this._msdown||this._msin)&&!this.childOf($0)){
this._msdown=false;this._msin=false;this._callShow()
}},"$lzc$set_frame",function($0){
with(this){
if(this.resourceviewcount>0){
for(var $1=0;$1<resourceviewcount;$1++){
this.subviews[$1].setAttribute("frame",$0)
}}else{
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_frame"]||this.nextMethod(arguments.callee,"$lzc$set_frame")).call(this,$0)
}}},"doSpaceDown",function(){
if(this._enabled){
this.showDown()
}},"doSpaceUp",function(){
if(this._enabled){
this.onclick.sendEvent();this.showUp()
}},"doEnterDown",function(){
if(this._enabled){
this.showDown()
}},"doEnterUp",function(){
if(this._enabled){
if(this.onclick){
this.onclick.sendEvent()
};this.showUp()
}},"$m50",function($0){
if(this.isinited){
this.maxframes=this.totalframes;this._callShow()
}},"init",function(){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);this.setResourceViewCount(this.resourceviewcount);this._callShow()
},"$m51",function($0){
this.setAttribute("_msin",true);this._callShow()
},"$m52",function($0){
this.setAttribute("_msin",false);this._callShow()
},"$m53",function($0){
this.setAttribute("_msdown",true);this._callShow()
},"$m54",function($0){
this.setAttribute("_msdown",false);this._callShow()
},"_showEnabled",function(){
with(this){
reference.setAttribute("clickable",this._enabled);showUp()
}},"showDown",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.setAttribute("frame",this.downResourceNumber)
},"showUp",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(!this._enabled&&this.disabledResourceNumber){
this.setAttribute("frame",this.disabledResourceNumber)
}else{
this.setAttribute("frame",this.normalResourceNumber)
}},"showOver",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.setAttribute("frame",this.overResourceNumber)
},"setreference",function($0){
this.reference=$0;if($0!=this)this.setAttribute("clickable",false)
},"_applystyle",function($0){
with(this){
setTint(this,$0.basecolor)
}}],["tagname","basebutton","attributes",new LzInheritedHash($lzc$class_basecomponent.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onmode","$m49","$m48","ontotalframes","$m50",null,"onmouseover","$m51",null,"onmouseout","$m52",null,"onmousedown","$m53",null,"onmouseup","$m54",null],_msdown:false,_msin:false,clickable:true,disabledResourceNumber:4,downResourceNumber:3,focusable:false,maxframes:new LzOnceExpr("$m46"),normalResourceNumber:1,onclick:LzDeclaredEvent,onresourceviewcount:LzDeclaredEvent,overResourceNumber:2,reference:new LzOnceExpr("$m47"),resourceviewcount:0,respondtomouseout:true,styleable:false},$lzc$class_basebutton.attributes)
}}})($lzc$class_basebutton);Class.make("$lzc$class_swatchview",LzView,["ctransform",void 0,"color",void 0,"construct",function($0,$1){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["construct"]||this.nextMethod(arguments.callee,"construct")).call(this,$0,$1);this.capabilities=new LzInheritedHash(this.capabilities);this.capabilities.colortransform=true;if($1["width"]==null){
$1["width"]=this.immediateparent.width
};if($1["height"]==null){
$1["height"]=this.immediateparent.height
};if($1["fgcolor"]==null&&$1["bgcolor"]==null){
$1["fgcolor"]=16777215
}}},"$lzc$set_fgcolor",function($0){
this.setAttribute("bgcolor",$0)
},"$lzc$set_bgcolor",function($0){
with(this){
this.color=$0;if(this.ctransform!=null){
var $1=$0>>16&255;var $2=$0>>8&255;var $3=$0&255;$1=$1*ctransform["ra"]/100+ctransform["rb"];$1=Math.min($1,255);$2=$2*ctransform["ga"]/100+ctransform["gb"];$2=Math.min($2,255);$3=$3*ctransform["ba"]/100+ctransform["bb"];$3=Math.min($3,255);$0=Math.floor($3+($2<<8)+($1<<16))
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["$lzc$set_bgcolor"]||this.nextMethod(arguments.callee,"$lzc$set_bgcolor")).call(this,$0)
}},"setColorTransform",function($0){
this.ctransform=$0;this.setAttribute("bgcolor",this.color)
}],["tagname","swatchview","attributes",new LzInheritedHash(LzView.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({color:16777215,ctransform:null},$lzc$class_swatchview.attributes)
}}})($lzc$class_swatchview);Class.make("$lzc$class_m100",LzView,["$m66",function($0){
with(this){
var $1=parent.width-1;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m67",function(){
with(this){
return [parent,"width"]
}},"$m68",function($0){
with(this){
var $1=parent.height-1;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m69",function(){
with(this){
return [parent,"height"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m101",LzView,["$m70",function($0){
with(this){
var $1=parent.width-3;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m71",function(){
with(this){
return [parent,"width"]
}},"$m72",function($0){
with(this){
var $1=parent.height-3;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m73",function(){
with(this){
return [parent,"height"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m102",LzView,["$m74",function($0){
with(this){
var $1=parent.width-4;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m75",function(){
with(this){
return [parent,"width"]
}},"$m76",function($0){
with(this){
var $1=parent.height-4;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m77",function(){
with(this){
return [parent,"height"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m103",LzView,["$m78",function($0){
with(this){
var $1=parent.parent.width-2;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m79",function(){
with(this){
return [parent.parent,"width"]
}},"$m80",function($0){
with(this){
var $1=parent.parent.height-2;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m81",function(){
with(this){
return [parent.parent,"height"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m104",LzView,["$m82",function($0){
with(this){
var $1=parent.parent.height-2;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m83",function(){
with(this){
return [parent.parent,"height"]
}},"$m84",function($0){
with(this){
var $1=parent.parent.width-3;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m85",function(){
with(this){
return [parent.parent,"width"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m105",LzView,["$m86",function($0){
with(this){
var $1=parent.parent.width-1;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m87",function(){
with(this){
return [parent.parent,"width"]
}},"$m88",function($0){
with(this){
var $1=parent.parent.height;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m89",function(){
with(this){
return [parent.parent,"height"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m106",LzView,["$m90",function($0){
with(this){
var $1=parent.parent.height-1;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m91",function(){
with(this){
return [parent.parent,"height"]
}},"$m92",function($0){
with(this){
var $1=parent.parent.width-1;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m93",function(){
with(this){
return [parent.parent,"width"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m107",LzText,["$m94",function($0){
with(this){
var $1=parent.text_x+parent.titleshift;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m95",function(){
with(this){
return [parent,"text_x",parent,"titleshift"]
}},"$m96",function($0){
with(this){
var $1=parent.text_y+parent.titleshift;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m97",function(){
with(this){
return [parent,"text_y",parent,"titleshift"]
}},"$m98",function($0){
with(this){
var $1=parent.text;if($1!==this["text"]||!this.inited){
this.setAttribute("text",$1)
}}},"$m99",function(){
with(this){
return [parent,"text"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_button",$lzc$class_basebutton,["text_padding_x",void 0,"text_padding_y",void 0,"$m55",function($0){
var $1=this.width/2-this._title.width/2;if($1!==this["text_x"]||!this.inited){
this.setAttribute("text_x",$1)
}},"$m56",function(){
return [this,"width",this._title,"width"]
},"text_x",void 0,"$m57",function($0){
var $1=this.height/2-this._title.height/2;if($1!==this["text_y"]||!this.inited){
this.setAttribute("text_y",$1)
}},"$m58",function(){
return [this,"height",this._title,"height"]
},"text_y",void 0,"$m59",function($0){
var $1=this._title.width+2*this.text_padding_x;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}},"$m60",function(){
return [this._title,"width",this,"text_padding_x"]
},"$m61",function($0){
var $1=this._title.height+2*this.text_padding_y;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}},"$m62",function(){
return [this._title,"height",this,"text_padding_y"]
},"buttonstate",void 0,"$m63",function($0){
var $1=this.buttonstate==1?0:1;if($1!==this["titleshift"]||!this.inited){
this.setAttribute("titleshift",$1)
}},"$m64",function(){
return [this,"buttonstate"]
},"titleshift",void 0,"leftalign",void 0,"_showEnabled",function(){
with(this){
showUp();setAttribute("clickable",_enabled)
}},"showDown",function($0){
with(this){
switch(arguments.length){
case 0:
$0=null;

};if(this.hasdefault){
this._outerbezel.setAttribute("frame",5)
}else{
this._outerbezel.setAttribute("frame",this.downResourceNumber)
};this._face.setAttribute("frame",this.downResourceNumber);this._innerbezel.setAttribute("frame",this.downResourceNumber);setAttribute("buttonstate",2)
}},"showUp",function($0){
with(this){
switch(arguments.length){
case 0:
$0=null;

};if(_enabled){
if(this.hasdefault){
this._outerbezel.setAttribute("frame",5)
}else{
this._outerbezel.setAttribute("frame",this.normalResourceNumber)
};this._face.setAttribute("frame",this.normalResourceNumber);this._innerbezel.setAttribute("frame",this.normalResourceNumber);if(this.style)this._title.setAttribute("fgcolor",this.style.textcolor)
}else{
if(this.style)this._title.setAttribute("fgcolor",this.style.textdisabledcolor);this._face.setAttribute("frame",this.disabledResourceNumber);this._outerbezel.setAttribute("frame",this.disabledResourceNumber);this._innerbezel.setAttribute("frame",this.disabledResourceNumber)
};setAttribute("buttonstate",1)
}},"showOver",function($0){
with(this){
switch(arguments.length){
case 0:
$0=null;

};if(this.hasdefault){
this._outerbezel.setAttribute("frame",5)
}else{
this._outerbezel.setAttribute("frame",this.overResourceNumber)
};this._face.setAttribute("frame",this.overResourceNumber);this._innerbezel.setAttribute("frame",this.overResourceNumber);setAttribute("buttonstate",1)
}},"$m65",function($0){
with(this){
if(this._initcomplete){
if(this.buttonstate==1)showUp()
}}},"_applystyle",function($0){
with(this){
if(this.style!=null){
this.textcolor=$0.textcolor;this.textdisabledcolor=$0.textdisabledcolor;if(enabled){
_title.setAttribute("fgcolor",$0.textcolor)
}else{
_title.setAttribute("fgcolor",$0.textdisabledcolor)
};setTint(_outerbezel,$0.basecolor);setTint(_innerbezel,$0.basecolor);setTint(_face,$0.basecolor)
}}},"_outerbezel",void 0,"_innerbezel",void 0,"_face",void 0,"_innerbezelbottom",void 0,"_outerbezelbottom",void 0,"_title",void 0],["tagname","button","children",[{attrs:{$classrootdepth:1,bgcolor:LzColorUtils.convertColor("0x919191"),height:new LzAlwaysExpr("$m68","$m69"),name:"_outerbezel",width:new LzAlwaysExpr("$m66","$m67"),x:0,y:0},"class":$lzc$class_m100},{attrs:{$classrootdepth:1,bgcolor:LzColorUtils.convertColor("0xffffff"),height:new LzAlwaysExpr("$m72","$m73"),name:"_innerbezel",width:new LzAlwaysExpr("$m70","$m71"),x:1,y:1},"class":$lzc$class_m101},{attrs:{$classrootdepth:1,height:new LzAlwaysExpr("$m76","$m77"),name:"_face",resource:"lzbutton_face_rsc",stretches:"both",width:new LzAlwaysExpr("$m74","$m75"),x:2,y:2},"class":$lzc$class_m102},{attrs:{$classrootdepth:1,name:"_innerbezelbottom"},children:[{attrs:{$classrootdepth:2,bgcolor:LzColorUtils.convertColor("0x585858"),height:new LzAlwaysExpr("$m80","$m81"),width:1,x:new LzAlwaysExpr("$m78","$m79"),y:1},"class":$lzc$class_m103},{attrs:{$classrootdepth:2,bgcolor:LzColorUtils.convertColor("0x585858"),height:1,width:new LzAlwaysExpr("$m84","$m85"),x:1,y:new LzAlwaysExpr("$m82","$m83")},"class":$lzc$class_m104}],"class":LzView},{attrs:{$classrootdepth:1,name:"_outerbezelbottom"},children:[{attrs:{$classrootdepth:2,bgcolor:LzColorUtils.convertColor("0xffffff"),height:new LzAlwaysExpr("$m88","$m89"),opacity:0.7,width:1,x:new LzAlwaysExpr("$m86","$m87"),y:0},"class":$lzc$class_m105},{attrs:{$classrootdepth:2,bgcolor:LzColorUtils.convertColor("0xffffff"),height:1,opacity:0.7,width:new LzAlwaysExpr("$m92","$m93"),x:0,y:new LzAlwaysExpr("$m90","$m91")},"class":$lzc$class_m106}],"class":LzView},{attrs:{$classrootdepth:1,name:"_title",resize:true,text:new LzAlwaysExpr("$m98","$m99"),x:new LzAlwaysExpr("$m94","$m95"),y:new LzAlwaysExpr("$m96","$m97")},"class":$lzc$class_m107}],"attributes",new LzInheritedHash($lzc$class_basebutton.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onhasdefault","$m65",null],buttonstate:1,clickable:true,doesenter:true,focusable:true,height:new LzAlwaysExpr("$m61","$m62"),leftalign:false,maxframes:4,pixellock:true,styleable:true,text_padding_x:11,text_padding_y:4,text_x:new LzAlwaysExpr("$m55","$m56"),text_y:new LzAlwaysExpr("$m57","$m58"),titleshift:new LzAlwaysExpr("$m63","$m64"),width:new LzAlwaysExpr("$m59","$m60")},$lzc$class_button.attributes)
}}})($lzc$class_button);Class.make("$lzc$class_basevaluecomponent",$lzc$class_basecomponent,["value",void 0,"type",void 0,"getValue",function(){
return this.value==null?this.text:this.value
},"$lzc$getValue_dependencies",function($0,$1){
return [this,"value",this,"text"]
},"acceptValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null)$1=this.type;(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["acceptValue"]||this.nextMethod(arguments.callee,"acceptValue")).call(this,$0,$1);this.acceptAttribute("value",$1,$0)
},"presentValue",function($0){
switch(arguments.length){
case 0:
$0=null;

};if($0==null)$0=this.type;return this.presentTypeValue($0,this.getValue())
},"$lzc$presentValue_dependencies",function($0,$1,$2){
switch(arguments.length){
case 2:
$2=null;

};return [this,"type"].concat(this.$lzc$getValue_dependencies($0,$1))
}],["tagname","basevaluecomponent","attributes",new LzInheritedHash($lzc$class_basecomponent.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({type:"",value:null},$lzc$class_basevaluecomponent.attributes)
}}})($lzc$class_basevaluecomponent);Class.make("$lzc$class_baseformitem",$lzc$class_basevaluecomponent,["_parentform",void 0,"submitname",void 0,"$m108",function($0){
with(this){
var $1=enabled;if($1!==this["submit"]||!this.inited){
this.setAttribute("submit",$1)
}}},"$m109",function(){
return [this,"enabled"]
},"submit",void 0,"changed",void 0,"$lzc$set_changed",function($0){
this.setChanged($0)
},"$lzc$set_value",function($0){
this.setValue($0,false)
},"onchanged",void 0,"onvalue",void 0,"rollbackvalue",void 0,"ignoreform",void 0,"init",function(){
if(this.submitname=="")this.submitname=this.name;if(this.submitname==""){};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);var $0=this.findForm();if($0!=null){
$0.addFormItem(this);this._parentform=$0
}},"destroy",function(){
if(this._parentform)this._parentform.removeFormItem(this);(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["destroy"]||this.nextMethod(arguments.callee,"destroy")).call(this)
},"setChanged",function($0,$1){
with(this){
switch(arguments.length){
case 1:
$1=null;

};if(!this._initcomplete){
this.changed=false;return
};var $2=this.changed;this.changed=$0;if(this.changed!=$2){
if(this.onchanged)this.onchanged.sendEvent(this.changed)
};if(!$1&&this.changed&&!ignoreform){
if(this["_parentform"]&&this._parentform["changed"]!=undefined&&!this._parentform.changed){
this._parentform.setChanged($0,false)
}};if(!$1&&!this.changed&&!ignoreform){
if(this["_parentform"]&&this._parentform["changed"]!=undefined&&this._parentform.changed){
this._parentform.setChanged($0,true)
}}}},"rollback",function(){
if(this.rollbackvalue!=this["value"]){
this.setAttribute("value",this.rollbackvalue)
};this.setAttribute("changed",false)
},"commit",function(){
this.rollbackvalue=this.value;this.setAttribute("changed",false)
},"setValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};var $2=this.value!=$0;this.value=$0;if($1||!this._initcomplete){
this.rollbackvalue=$0
};this.setChanged($2&&!$1&&this.rollbackvalue!=$0);if(this["onvalue"])this.onvalue.sendEvent($0)
},"acceptValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null)$1=this.type;this.setValue(this.acceptTypeValue($1,$0),true)
},"findForm",function(){
with(this){
if(_parentform!=null){
return _parentform
}else{
var $0=this.immediateparent;var $1=null;while($0!=canvas){
if($0["formdata"]){
$1=$0;break
};$0=$0.immediateparent
};return $1
}}},"toXML",function($0){
with(this){
var $1=this.value;if($0){
if(typeof $1=="boolean")$1=$1-0
};return lz.Browser.xmlEscape(this.submitname)+'="'+lz.Browser.xmlEscape($1)+'"'
}}],["tagname","baseformitem","attributes",new LzInheritedHash($lzc$class_basevaluecomponent.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({_parentform:null,changed:false,ignoreform:false,onchanged:LzDeclaredEvent,onvalue:LzDeclaredEvent,rollbackvalue:null,submit:new LzAlwaysExpr("$m108","$m109"),submitname:"",value:null},$lzc$class_baseformitem.attributes)
}}})($lzc$class_baseformitem);Class.make("$lzc$class_multistatebutton",$lzc$class_basebutton,["statenum",void 0,"$lzc$set_statenum",function($0){
this.setStateNum($0)
},"statelength",void 0,"$lzc$set_statelength",function($0){
this.setStateLength($0)
},"maxstate",void 0,"lastres",void 0,"$m110",function($0){
var $1=this.lastres+this.statenum*this.statelength;if($1!==this["frame"]||!this.inited){
this.setAttribute("frame",$1)
}},"$m111",function(){
return [this,"lastres",this,"statenum",this,"statelength"]
},"onstatenum",void 0,"onstatelength",void 0,"showDown",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.setAttribute("lastres",this.downResourceNumber)
},"showUp",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(!this._enabled&&this.disabledResourceNumber){
this.setAttribute("lastres",this.disabledResourceNumber)
}else{
this.setAttribute("lastres",this.normalResourceNumber)
}},"showOver",function($0){
switch(arguments.length){
case 0:
$0=null;

};this.setAttribute("lastres",this.overResourceNumber)
},"setStateNum",function($0){
if($0>this.maxstate){
return
};this.statenum=$0;if(this.onstatenum)this.onstatenum.sendEvent($0)
},"setStateLength",function($0){
this.statelength=$0;if(this.statelength==2){
this.overResourceNumber=this.normalResourceNumber;if(this.downResourceNumber==3){
this.downResourceNumber=2
}}else if(this.statelength==1){
this.overResourceNumber=1;this.downResourceNumber=1
};if(this.onstatelength)this.onstatelength.sendEvent($0)
},"_showEnabled",function(){
with(this){
reference.setAttribute("clickable",this._enabled);this.showUp()
}}],["tagname","multistatebutton","attributes",new LzInheritedHash($lzc$class_basebutton.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({clickable:true,frame:new LzAlwaysExpr("$m110","$m111"),lastres:1,maxstate:0,onstatelength:LzDeclaredEvent,onstatenum:LzDeclaredEvent,statelength:3,statenum:0},$lzc$class_multistatebutton.attributes)
}}})($lzc$class_multistatebutton);Class.make("$lzc$class_m122",LzText,["$m112",function($0){
with(this){
var $1=classroot.text_y;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m113",function(){
with(this){
return [classroot,"text_y"]
}},"$m114",function($0){
with(this){
var $1=parent.text;if($1!==this["text"]||!this.inited){
this.setAttribute("text",$1)
}}},"$m115",function(){
with(this){
return [parent,"text"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_m123",$lzc$class_multistatebutton,["$m118",function($0){
with(this){
var $1=parent.value?1:0;if($1!==this["statenum"]||!this.inited){
this.setAttribute("statenum",$1)
}}},"$m119",function(){
with(this){
return [parent,"value"]
}},"$m120",function($0){
with(this){
this.setAttribute("reference",parent)
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash($lzc$class_multistatebutton.attributes)]);Class.make("$lzc$class_checkbox",$lzc$class_baseformitem,["_title",void 0,"$m116",function($0){
var $1=this.cb.height/2-this._title.height/2+1;if($1!==this["text_y"]||!this.inited){
this.setAttribute("text_y",$1)
}},"$m117",function(){
return [this.cb,"height",this._title,"height"]
},"text_y",void 0,"$lzc$set_value",function($0){
with(this){
setValue($0)
}},"cb",void 0,"doSpaceUp",function(){
if(this._enabled){
this.setAttribute("value",!this.value)
}},"$m121",function($0){
if(this._enabled)this.setAttribute("value",!this.value)
},"_applystyle",function($0){
with(this){
if(this.style!=null){
if(_enabled){
_title.setAttribute("fgcolor",$0.textcolor)
}else{
_title.setAttribute("fgcolor",$0.textdisabledcolor)
};setTint(this.cb,$0.basecolor)
}}},"_showEnabled",function(){
with(this){
_applystyle(this.style)
}},"setValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($0=="false"){
$0=false
}else if($0=="true"){
$0=true
}else $0=!(!$0);(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["setValue"]||this.nextMethod(arguments.callee,"setValue")).call(this,$0,$1)
}],["tagname","checkbox","children",[{attrs:{$classrootdepth:1,name:"_title",resize:true,text:new LzAlwaysExpr("$m114","$m115"),x:16,y:new LzAlwaysExpr("$m112","$m113")},"class":$lzc$class_m122},{attrs:{$classrootdepth:1,maxstate:1,name:"cb",reference:new LzOnceExpr("$m120"),resource:"lzcheckbox_rsrc",statelength:4,statenum:new LzAlwaysExpr("$m118","$m119"),text:""},"class":$lzc$class_m123}],"attributes",new LzInheritedHash($lzc$class_baseformitem.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onclick","$m121",null],clickable:true,pixellock:true,text_y:new LzAlwaysExpr("$m116","$m117"),value:false},$lzc$class_checkbox.attributes)
}}})($lzc$class_checkbox);Class.make("$lzc$class_listselector",LzSelectionManager,["multiselect",void 0,"_forcemulti",void 0,"isRangeSelect",function($0){
return this.multiselect&&(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["isRangeSelect"]||this.nextMethod(arguments.callee,"isRangeSelect")).call(this,$0)
},"isMultiSelect",function($0){
return this._forcemulti||this.multiselect&&(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["isMultiSelect"]||this.nextMethod(arguments.callee,"isMultiSelect")).call(this,$0)
},"select",function($0){
with(this){
if(this.multiselect&&(Array["$lzsc$isa"]?Array.$lzsc$isa($0):$0 instanceof Array)){
this._forcemulti=true;for(var $1=0;$1<$0.length;$1++){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["select"]||this.nextMethod(arguments.callee,"select")).call(this,$0[$1])
};this._forcemulti=false
}else{
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["select"]||this.nextMethod(arguments.callee,"select")).call(this,$0)
}}},"getValue",function(){
with(this){
var $0=this.getSelection();if($0.length==0)return null;if($0.length==1&&!multiselect){
return $0[0].getValue()
};var $1=[];for(var $2=0;$2<$0.length;$2++){
$1[$2]=$0[$2].getValue()
};return $1
}},"getText",function(){
with(this){
var $0=this.getSelection();if($0.length==0)return null;if($0.length==1&&!multiselect){
return $0[0].text
};var $1=[];for(var $2=0;$2<$0.length;$2++){
$1[$2]=$0[$2].text
};return $1
}},"getNumItems",function(){
if(!this.immediateparent.subviews)return 0;return this.immediateparent.subviews.length
},"getNextSubview",function($0,$1){
with(this){
switch(arguments.length){
case 1:
$1=1;

};if(typeof $1=="undefined")$1=1;var $2=this.immediateparent.subviews;if(!$0){
if($1>0){
return $2[0]
}else{
return $2[$2.length-1]
}};var $3;var $4=$2.length;for(var $5=0;$5<$4;$5++){
var $6=$2[$5];if($6==$0){
var $7=$5+$1;if($7<0){
$3=$2[0]
}else if($7>=$4){
$3=$2[$4-1]
}else{
$3=$2[$7]
};break
}};ensureItemInView($3);return $3
}},"ensureItemInView",function($0){
with(this){
if(!$0){
return
};var $1=immediateparent.parent;var $2=false;if($0.y+$0.height>$1.height-immediateparent.y){
var $3=$1.height-immediateparent.y-($0.y+$0.height);var $4=Math.max($1.height-immediateparent.height,immediateparent.y+$3);immediateparent.setAttribute("y",$4);$2=true
}else if(immediateparent.y*-1>$0.y){
var $3=immediateparent.y*-1-$0.y;var $4=Math.min(0,immediateparent.y+$3);immediateparent.setAttribute("y",$4);$2=true
};if($2){
this._updatefromscrolling=true
}}},"_updatefromscrolling",void 0,"allowhilite",function($0){
if(this._updatefromscrolling){
if($0!=null)this._updatefromscrolling=false;return false
};return true
},"getItemByIndex",function($0){
return this.parent._contentview.subviews[$0]
}],["tagname","listselector","attributes",new LzInheritedHash(LzSelectionManager.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({_forcemulti:false,_updatefromscrolling:false,multiselect:false},$lzc$class_listselector.attributes)
}}})($lzc$class_listselector);Class.make("$lzc$class_datalistselector",LzDataSelectionManager,["multiselect",void 0,"_forcemulti",void 0,"isRangeSelect",function($0){
return this.multiselect&&(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["isRangeSelect"]||this.nextMethod(arguments.callee,"isRangeSelect")).call(this,$0)
},"isMultiSelect",function($0){
return this._forcemulti||this.multiselect&&(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["isMultiSelect"]||this.nextMethod(arguments.callee,"isMultiSelect")).call(this,$0)
},"select",function($0){
with(this){
if(this.multiselect&&(Array["$lzsc$isa"]?Array.$lzsc$isa($0):$0 instanceof Array)){
this._forcemulti=true;for(var $1=0;$1<$0.length;$1++){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["select"]||this.nextMethod(arguments.callee,"select")).call(this,$0[$1])
};this._forcemulti=false
}else{
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["select"]||this.nextMethod(arguments.callee,"select")).call(this,$0)
}}},"getValue",function(){
with(this){
var $0=this.getSelection();if($0.length==0)return null;var $1=this.immediateparent.subviews[0]._valuedatapath;if(!$1)$1=this.immediateparent.subviews[0]._textdatapath;if(!$1)$1="text()";if($0.length==1&&!multiselect){
return $0[0].xpathQuery($1)
}else{
var $2=[];for(var $3=0;$3<$0.length;$3++){
$2[$3]=$0[$3].xpathQuery($1)
};return $2
}}},"getText",function(){
with(this){
var $0=this.getSelection();if($0.length==0)return null;var $1=this.immediateparent.subviews[0]._textdatapath;if(!$1)$1="text()";if($0.length==1&&!multiselect){
return $0[0].xpathQuery($1)
}else{
var $2=[];for(var $3=0;$3<$0.length;$3++){
$2[$3]=$0[$3].xpathQuery($1)
};return $2
}}},"getNumItems",function(){
with(this){
if(!this.cloneManager){
var $0=immediateparent.subviews;if($0==null||$0.length==0){
return 0
}else{
this.cloneManager=$0[0].cloneManager
}};if(this.cloneManager!=null){
if(!this.cloneManager["nodes"]){
return 0
}else{
return this.cloneManager.nodes.length
}}else if($0[0].data){
return 1
}else{
return 0
}}},"getNextSubview",function($0,$1){
with(this){
switch(arguments.length){
case 1:
$1=1;

};var $2=immediateparent.subviews[0].cloneManager["clones"];if($0==null){
var $3=$1==-1&&parent.shownitems!=-1?parent.shownitems-1:0;return $2[$3]
};var $4=findIndex($0);if($4==-1)return null;var $5=immediateparent.subviews[0].cloneManager.nodes;if(!$1)$1=1;$4+=$1;if($4==-1)$4=0;if($4==$5.length)$4=$5.length-1;_ensureItemInViewByIndex($4);var $6=$5[$4];var $7=immediateparent.subviews;for(var $8=0;$8<$7.length;$8++){
if($7[$8].datapath.p==$6){
return $7[$8]
}}}},"findIndex",function($0){
with(this){
if(!immediateparent.subviews[0].cloneManager){
if($0 instanceof lz.view){
return immediateparent.subviews[0]==$0?0:-1
}else{
return immediateparent.subviews[0].datapath.p==$0.p?0:-1
}};var $1;if($0 instanceof lz.view){
$1=$0.datapath.p
}else{
$1=$0.p
};var $2=immediateparent.subviews[0].cloneManager.nodes;var $3=-1;if($2!=null){
for(var $4=0;$4<$2.length;$4++){
if($2[$4]==$1){
$3=$4;break
}}};return $3
}},"ensureItemInView",function($0){
with(this){
if(!$0)return;var $1=findIndex($0);if($1!=-1)_ensureItemInViewByIndex($1)
}},"_ensureItemInViewByIndex",function($0){
with(this){
var $1=this.immediateparent;var $2=$1.subviews;if(!$2||$2.length==0){
return
};var $3=$2[0].height;var $4=$0*$3;var $5=0;if($0>0){
var $6=$2[0].cloneManager;if(parent["spacing"]){
$5=parent.spacing
}else if($6&&$6["spacing"]){
$5=$6.spacing
};$4+=$5*($0-1)
};var $7=false;var $8=$1.parent.height;var $9=$1.y;if($4+$3>$8-$9){
var $a=$8-$9-($4+$3+$5);var $b=Math.max($8-$1.height,$9+$a);$1.setAttribute("y",$b);$7=true
}else if($9*-1>$4){
var $a=$9*-1-$4-$5;var $b=Math.min(0,$9+$a);$1.setAttribute("y",$b);$7=true
};if($7){
this._updatefromscrolling=true
}}},"getItemByIndex",function($0){
with(this){
var $1=immediateparent.subviews;if(!$1||$1.length==0)return null;this._ensureItemInViewByIndex($0);var $2=$1[0].cloneManager;if($2==null){
return $0==0?$1[0]:undefined
};var $3=$2.clones[0].datapath.xpathQuery("position()")-1;return $2.clones[$0-$3]
}},"getItemByData",function($0){
with(this){
return $0?getItemByIndex(this.getItemIndexByData($0)):null
}},"getItemIndexByData",function($0){
with(this){
if($0){
var $1=immediateparent.subviews;if($1[0].cloneManager){
var $2=$1[0].cloneManager["nodes"];if($2!=null){
for(var $3=0;$3<$2.length;$3++){
if($2[$3]==$0){
return $3
}}}}else if($1[0].datapath.p==$0){
return 0
}};return null
}},"_updatefromscrolling",void 0,"allowhilite",function($0){
if(this._updatefromscrolling){
if($0!=null)this._updatefromscrolling=false;return false
};return true
}],["tagname","datalistselector","attributes",new LzInheritedHash(LzDataSelectionManager.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({_forcemulti:false,_updatefromscrolling:false,multiselect:false},$lzc$class_datalistselector.attributes)
}}})($lzc$class_datalistselector);Class.make("$lzc$class_baselist",$lzc$class_baseformitem,["itemclassname",void 0,"__itemclass",void 0,"defaultselection",void 0,"multiselect",void 0,"toggleselected",void 0,"dataoption",void 0,"_hiliteview",void 0,"_contentview",void 0,"_initialselection",void 0,"_selector",void 0,"__focusfromchild",void 0,"onselect",void 0,"onitemclassname",void 0,"doEnterDown",function(){
with(this){
if((lz.view["$lzsc$isa"]?lz.view.$lzsc$isa(this._hiliteview):this._hiliteview instanceof lz.view)&&this._hiliteview.enabled){
this._hiliteview.setAttribute("selected",true)
}}},"doEnterUp",function(){
return
},"init",function(){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(this._contentview==null){
if(this.defaultplacement!=null){
this._contentview=this.searchSubnodes("name",this.defaultplacement)
}else{
this._contentview=this
}};if(this.dataoption=="lazy"||this.dataoption=="resize"){
this._selector=new (lz.datalistselector)(this,{multiselect:this.multiselect,toggle:toggleselected})
}else{
this._selector=new (lz.listselector)(this,{multiselect:this.multiselect,toggle:toggleselected})
};if(this._initialselection!=null){
this.select(this._initialselection)
}else if(this.defaultselection!=null){
selectItemAt(defaultselection)
}}},"_doFocus",function($0){
with(this){
if(this["_selector"]!=null){
var $1=this._selector.getSelection();if($1&&$1.length>0){
if(lz.view["$lzsc$isa"]?lz.view.$lzsc$isa($1[0]):$1[0] instanceof lz.view){
this._hiliteview=$1[0];this._hiliteview.setHilite(true)
}}}}},"_doblur",function($0){
with(this){
if(lz.view["$lzsc$isa"]?lz.view.$lzsc$isa(this._hiliteview):this._hiliteview instanceof lz.view){
this._hiliteview.setHilite(false)
};this._hiliteview=null
}},"setHilite",function($0){
with(this){
if(this._selector.allowhilite($0)){
if(lz.view["$lzsc$isa"]?lz.view.$lzsc$isa(this._hiliteview):this._hiliteview instanceof lz.view)this._hiliteview.setHilite(false);this._hiliteview=$0;if(lz.view["$lzsc$isa"]?lz.view.$lzsc$isa($0):$0 instanceof lz.view){
$0.setHilite(true)
}}}},"_dokeydown",function($0){
with(this){
var $1=this._hiliteview;if($1==null){
$1=getSelection();if(this.multiselect)$1=$1[$1.length-1]
};if(this.focusable&&$0==32){
if((lz.view["$lzsc$isa"]?lz.view.$lzsc$isa($1):$1 instanceof lz.view)&&$1.enabled){
$1.setAttribute("selected",true);$1.setHilite(true);this._hiliteview=$1
};return
};if(this.focusable&&$0>=37&&$0<=40){
this.setAttribute("doesenter",true);var $2;if($0==39||$0==40){
$2=_selector.getNextSubview($1)
};if($0==37||$0==38){
$2=_selector.getNextSubview($1,-1)
};if(lz.view["$lzsc$isa"]?lz.view.$lzsc$isa($1):$1 instanceof lz.view){
$1.setHilite(false)
};if($2.enabled&&_selector.isRangeSelect($2)){
$2.setAttribute("selected",true)
};$2.setHilite(true);this._hiliteview=$2
}}},"getValue",function(){
with(this){
return _selector.getValue()
}},"getText",function(){
with(this){
if(_initcomplete)return _selector.getText();return null
}},"getSelection",function(){
with(this){
if(this._initcomplete){
var $0=this._selector.getSelection();if(multiselect){
return $0
}else{
if($0.length==0){
return null
}else{
return $0[0]
}}}else{
return this._initialselection
}}},"selectNext",function(){
with(this){
moveSelection(1)
}},"selectPrev",function(){
with(this){
moveSelection(-1)
}},"moveSelection",function($0){
with(this){
if(!$0)$0=1;var $1=this._selector.getSelection();var $2;if($1.length==0){
$2=this._contentview.subviews[0]
}else{
var $3=$1[0];$2=this._selector.getNextSubview($3,$0)
};var $4=lz.Focus.getFocus();select($2);if($3&&$4&&$4.childOf($3)){
lz.Focus.setFocus($2)
}}},"getNumItems",function(){
if(this["_selector"]==null)return 0;return this._selector.getNumItems()
},"getItemAt",function($0){
with(this){
if(_contentview.subviews[$0]){
return getItem(_contentview.subviews[$0].getValue())
};return null
}},"getItem",function($0){
with(this){
if(_contentview!=null&&_contentview.subviews!=null){
for(var $1=0;$1<_contentview.subviews.length;$1++){
var $2=_contentview.subviews[$1];if($2.getValue()==$0){
return $2
}}};return null
}},"addItem",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};new (this.__itemclass)(this,{text:$0,value:$1})
},"$lzc$set_itemclassname",function($0){
with(this){
this.itemclassname=$0;this.__itemclass=lz[$0];if(onitemclassname.ready){
this.onitemclassname.sendEvent($0)
}}},"removeItem",function($0){
with(this){
var $1=getItem($0);_removeitem($1)
}},"removeItemAt",function($0){
with(this){
var $1=_contentview.subviews[$0];_removeitem($1)
}},"removeAllItems",function(){
with(this){
while(_contentview.subviews.length!=0){
for(var $0=0;$0<_contentview.subviews.length;$0++){
_removeitem(_contentview.subviews[$0])
}}}},"_removeitem",function($0){
if($0){
if($0.selected)this._selector.unselect($0);$0.destroy()
}},"selectItem",function($0){
with(this){
var $1=getItem($0);if($1){
select($1)
}}},"selectItemAt",function($0){
with(this){
if(this._selector!=null){
var $1=this._selector.getItemByIndex($0);select($1)
}}},"clearSelection",function(){
if(this._initcomplete){
this._selector.clearSelection()
}else{
this._initialselection=null;this.defaultselection=null
}},"select",function($0){
with(this){
if($0==null){

}else if(this._initcomplete){
this._selector.select($0);if(!this.multiselect){
this.setAttribute("value",$0.getValue())
}}else{
if(multiselect){
if(this._initialselection==null)this._initialselection=[];this._initialselection.push($0)
}else{
this._initialselection=$0
}};if((lz.view["$lzsc$isa"]?lz.view.$lzsc$isa(this._hiliteview):this._hiliteview instanceof lz.view)&&this._hiliteview["enabled"]){
this._hiliteview.setHilite(false);this._hiliteview=null
};this.setAttribute("doesenter",false);if(this.onselect)this.onselect.sendEvent($0)
}}],["tagname","baselist","attributes",new LzInheritedHash($lzc$class_baseformitem.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({$delegates:["onfocus","_doFocus",null,"onblur","_doblur",null,"onkeydown","_dokeydown",null],__focusfromchild:false,__itemclass:null,_contentview:null,_hiliteview:null,_initialselection:null,_selector:null,dataoption:"none",defaultselection:null,itemclassname:"",multiselect:false,onitemclassname:LzDeclaredEvent,onselect:LzDeclaredEvent,toggleselected:false},$lzc$class_baselist.attributes)
}}})($lzc$class_baselist);Class.make("$lzc$class_baselistitem",$lzc$class_basevaluecomponent,["selected",void 0,"$lzc$set_selected",function($0){
this._setSelected($0)
},"onselected",void 0,"onselect",void 0,"_selectonevent",void 0,"$lzc$set__selectonevent",function($0){
this.setSelectOnEvent($0)
},"$lzc$set_datapath",function($0){
with(this){
if(null!=this.datapath){
this.datapath.setXPath($0)
}else{
var $1={xpath:$0};if(this._parentcomponent.dataoption=="lazy"||this._parentcomponent.dataoption=="resize"){
$1.replication=_parentcomponent.dataoption;if(parent["spacing"])$1.spacing=parent.spacing
}else if(this._parentcomponent.dataoption=="pooling"){
$1.pooling=true
};new (lz.datapath)(this,$1)
}}},"_valuedatapath",void 0,"_textdatapath",void 0,"dataBindAttribute",function($0,$1,$2){
if(this._parentcomponent.dataoption=="lazy"||this._parentcomponent.dataoption=="resize"){
if($0=="text"){
this._textdatapath=$1
}else if($0=="value")this._valuedatapath=$1
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["dataBindAttribute"]||this.nextMethod(arguments.callee,"dataBindAttribute")).call(this,$0,$1,$2)
},"setSelectOnEvent",function($0){
with(this){
this._selectDL=new LzDelegate(this,"doClick",this,$0)
}},"doClick",function($0){
if(this._parentcomponent){
this._parentcomponent.select(this)
}},"_doMousedown",function($0){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["_doMousedown"]||this.nextMethod(arguments.callee,"_doMousedown")).call(this,$0);var $1=this._parentcomponent;if(!this.focusable&&$1&&$1.focusable){
$1.__focusfromchild=true;lz.Focus.setFocus($1,false);$1.__focusfromchild=false
}}},"setSelected",function($0){
this.selected=$0;if(this.onselect.ready)this.onselect.sendEvent(this);if(this.onselected.ready)this.onselected.sendEvent(this)
},"_setSelected",function($0){
with(this){
this.selected=$0;if($0){
parent.select(this)
}}},"setHilite",function($0){}],["tagname","baselistitem","attributes",new LzInheritedHash($lzc$class_basevaluecomponent.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({_selectonevent:"onclick",_textdatapath:null,_valuedatapath:null,clickable:true,focusable:false,onselect:LzDeclaredEvent,onselected:LzDeclaredEvent,selected:false},$lzc$class_baselistitem.attributes)
}}})($lzc$class_baselistitem);Class.make("$lzc$class_simplelayout",LzLayout,["axis",void 0,"$lzc$set_axis",function($0){
this.setAxis($0)
},"inset",void 0,"$lzc$set_inset",function($0){
this.inset=$0;if(this.subviews&&this.subviews.length)this.update();if(this["oninset"])this.oninset.sendEvent(this.inset)
},"spacing",void 0,"$lzc$set_spacing",function($0){
this.spacing=$0;if(this.subviews&&this.subviews.length)this.update();if(this["onspacing"])this.onspacing.sendEvent(this.spacing)
},"setAxis",function($0){
if(this["axis"]==null||this.axis!=$0){
this.axis=$0;this.sizeAxis=$0=="x"?"width":"height";if(this.subviews.length)this.update();if(this["onaxis"])this.onaxis.sendEvent(this.axis)
}},"addSubview",function($0){
this.updateDelegate.register($0,"on"+this.sizeAxis);this.updateDelegate.register($0,"onvisible");if(!this.locked){
var $1=null;var $2=this.subviews;for(var $3=$2.length-1;$3>=0;--$3){
if($2[$3].visible){
$1=$2[$3];break
}};if($1){
var $4=$1[this.axis]+$1[this.sizeAxis]+this.spacing
}else{
var $4=this.inset
};$0.setAttribute(this.axis,$4)
};(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["addSubview"]||this.nextMethod(arguments.callee,"addSubview")).call(this,$0)
},"update",function($0){
switch(arguments.length){
case 0:
$0=null;

};if(this.locked)return;var $1=this.subviews.length;var $2=this.inset;for(var $3=0;$3<$1;$3++){
var $4=this.subviews[$3];if(!$4.visible)continue;if($4[this.axis]!=$2){
$4.setAttribute(this.axis,$2)
};if($4.usegetbounds){
$4=$4.getBounds()
};$2+=this.spacing+$4[this.sizeAxis]
}}],["tagname","simplelayout","attributes",new LzInheritedHash(LzLayout.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({axis:"y",inset:0,spacing:0},$lzc$class_simplelayout.attributes)
}}})($lzc$class_simplelayout);Class.make("$lzc$class_radiogroup",$lzc$class_baselist,["$lzc$set_value",function($0){
with(this){
_setvalue($0)
}},"init",function(){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(canvas["accessible"]){
var $0=this.getNumItems();for(var $1=1;$1<=$0;$1++){
var $2=this.getItemAt($1-1);if(!$2["aaactive"]){
var $3=$1+" of "+$0;$2.setAttribute("aadescription",$3);$2.setAttribute("aaactive","true")
}}}}},"acceptValue",function($0,$1){
switch(arguments.length){
case 1:
$1=null;

};if($1==null)$1=this.type;var $2=this.acceptTypeValue($1,$0);this._setvalue($2);var $3=null;if($2!=null){
$3=this.getItem($2)
};if($3){
this.select($3)
}else this.clearSelection()
},"_setvalue",function($0){
if($0==this.value)return;if(this._initcomplete){
var $1=null;if($0!=null){
$1=this.getItem($0)
};this.value=$0
}else{
this.value=$0
};if(this.onvalue)this.onvalue.sendEvent($0)
}],["tagname","radiogroup","attributes",new LzInheritedHash($lzc$class_baselist.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({defaultselection:0,itemclassname:"radiobutton",layout:{axis:"y","class":"simplelayout",spacing:5},onvalue:LzDeclaredEvent,value:null},$lzc$class_radiogroup.attributes)
}}})($lzc$class_radiogroup);Class.make("$lzc$class_m135",$lzc$class_multistatebutton,["$m128",function($0){
with(this){
var $1=parent.selected?1:0;if($1!==this["statenum"]||!this.inited){
this.setAttribute("statenum",$1)
}}},"$m129",function(){
with(this){
return [parent,"selected"]
}},"$m130",function($0){
with(this){
this.setAttribute("reference",parent)
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash($lzc$class_multistatebutton.attributes)]);Class.make("$lzc$class_m136",LzText,["$m131",function($0){
with(this){
var $1=classroot.text_y;if($1!==this["y"]||!this.inited){
this.setAttribute("y",$1)
}}},"$m132",function(){
with(this){
return [classroot,"text_y"]
}},"$m133",function($0){
with(this){
var $1=parent.text;if($1!==this["text"]||!this.inited){
this.setAttribute("text",$1)
}}},"$m134",function(){
with(this){
return [parent,"text"]
}},"$classrootdepth",void 0],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_radiobutton",$lzc$class_baselistitem,["init",function(){
with(this){
(arguments.callee["$superclass"]&&arguments.callee.$superclass.prototype["init"]||this.nextMethod(arguments.callee,"init")).call(this);if(canvas["accessible"]){
this.accessible.setAttribute("applied",true);var $0=this.getDisplayObject();$0._accImpl={};$0._accImpl.stub=false;$0._accImpl.master=this;$0._accImpl.get_accRole=function(){
return 45
};$0._accImpl.get_accName=function(){
return this.master.text
};$0._accImpl.get_accState=function(){
var $0=0;if(this.master.parent.focusable)$0|=1048576;if(this.master.parent==lz.Focus.getFocus())$0|=4;if(this.master.selected)$0|=16;return $0
};$0._accImpl.get_accDefaultAction=function($0){
return "Check"
};$0._accImpl.accDoDefaultAction=function($0){
this.master.parent.select(this.master)
}}}},"accessible",void 0,"$m126",function($0){
var $1=this.rb.height/2-this._title.height/2;if($1!==this["text_y"]||!this.inited){
this.setAttribute("text_y",$1)
}},"$m127",function(){
return [this.rb,"height",this._title,"height"]
},"text_y",void 0,"initcomplete",void 0,"rb",void 0,"_applystyle",function($0){
with(this){
if(this.style!=null){
if(_enabled){
_title.setAttribute("fgcolor",this.style.textcolor)
}else{
_title.setAttribute("fgcolor",this.style.textdisabledcolor)
};setTint(this.rb,$0.basecolor)
}}},"_showEnabled",function(){
with(this){
if(this.style!=null){
if(_enabled){
_title.setAttribute("fgcolor",style.textcolor)
}else{
_title.setAttribute("fgcolor",style.textdisabledcolor)
}}}},"setHilite",function($0){
with(this){
_title.setAttribute("fgcolor",$0?style.texthilitecolor:style.textcolor);if($0)this.setAttribute("selected",true);if(canvas["accessible"])this.updateFocus($0)
}},"_title",void 0],["tagname","radiobutton","children",[{attrs:{$classrootdepth:1,$delegates:["onselected","$m124",null,"ontext","$m125",null],$m124:function($0){
with(this){
if(!this.isinited){
return
};if(this.selected){
this.sendAAEvent(0,EVENT_OBJECT_FOCUS)
};this.sendAAEvent(0,EVENT_OBJECT_STATECHANGE,true)
}},$m125:function($0){
with(this){
this.sendAAEvent(0,EVENT_OBJECT_NAMECHANGE)
}},EVENT_OBJECT_FOCUS:32773,EVENT_OBJECT_NAMECHANGE:32780,EVENT_OBJECT_SELECTION:32774,EVENT_OBJECT_STATECHANGE:32778,name:"accessible",updateFocus:function($0){
with(this){
if($0){
this.sendAAEvent(0,EVENT_OBJECT_SELECTION);this.sendAAEvent(0,EVENT_OBJECT_FOCUS)
};this.sendAAEvent(0,EVENT_OBJECT_STATECHANGE,true)
}}},"class":LzState},{attrs:{$classrootdepth:1,maxstate:1,name:"rb",reference:new LzOnceExpr("$m130"),resource:"lzradio_rsrc",statelength:4,statenum:new LzAlwaysExpr("$m128","$m129"),text:""},"class":$lzc$class_m135},{attrs:{$classrootdepth:1,name:"_title",resize:true,text:new LzAlwaysExpr("$m133","$m134"),x:17,y:new LzAlwaysExpr("$m131","$m132")},"class":$lzc$class_m136}],"attributes",new LzInheritedHash($lzc$class_baselistitem.attributes)]);(function($0){
with($0)with($0.prototype){
{
LzNode.mergeAttributes({clickable:true,focusable:false,initcomplete:0,text_y:new LzAlwaysExpr("$m126","$m127")},$lzc$class_radiobutton.attributes)
}}})($lzc$class_radiobutton);appdata=canvas.lzAddLocalData("appdata","<data />",false,false);appdata==true;Class.make("$lzc$class_m194",LzView,["$m139",function($0){
with(this){
var $1=canvas.app_console_debug?370:71;if($1!==this["height"]||!this.inited){
this.setAttribute("height",$1)
}}},"$m140",function(){
with(this){
return [canvas,"app_console_debug"]
}}],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m196",LzView,["$m143",function($0){
with(this){
var $1=parent.width-2;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m144",function(){
with(this){
return [parent,"width"]
}}],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m195",LzView,["$m141",function($0){
with(this){
var $1=parent.compilecontrols.width+5;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m142",function(){
with(this){
return [parent.compilecontrols,"width"]
}}],["children",[{attrs:{bgcolor:LzColorUtils.convertColor("0x7a7a8c"),height:30,width:new LzAlwaysExpr("$m143","$m144"),x:1,y:1},"class":$lzc$class_m196}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m197",$lzc$class_radiobutton,["$m145",function($0){
with(this){
var $1=canvas.runtime_swf8_enabled;if($1!==this["enabled"]||!this.inited){
this.setAttribute("enabled",$1)
}}},"$m146",function(){
with(this){
return [canvas,"runtime_swf8_enabled"]
}},"$m147",function($0){
with(this){
var $1=canvas.app_runtime=="swf8";if($1!==this["selected"]||!this.inited){
this.setAttribute("selected",$1)
}}},"$m148",function(){
with(this){
return [canvas,"app_runtime"]
}}],["children",LzNode.mergeChildren([],$lzc$class_radiobutton["children"]),"attributes",new LzInheritedHash($lzc$class_radiobutton.attributes)]);Class.make("$lzc$class_m198",$lzc$class_radiobutton,["$m149",function($0){
with(this){
var $1=canvas.runtime_swf9_enabled;if($1!==this["enabled"]||!this.inited){
this.setAttribute("enabled",$1)
}}},"$m150",function(){
with(this){
return [canvas,"runtime_swf9_enabled"]
}},"$m151",function($0){
with(this){
var $1=canvas.app_runtime=="swf9";if($1!==this["selected"]||!this.inited){
this.setAttribute("selected",$1)
}}},"$m152",function(){
with(this){
return [canvas,"app_runtime"]
}}],["children",LzNode.mergeChildren([],$lzc$class_radiobutton["children"]),"attributes",new LzInheritedHash($lzc$class_radiobutton.attributes)]);Class.make("$lzc$class_m199",$lzc$class_radiobutton,["$m153",function($0){
with(this){
var $1=canvas.runtime_swf10_enabled;if($1!==this["enabled"]||!this.inited){
this.setAttribute("enabled",$1)
}}},"$m154",function(){
with(this){
return [canvas,"runtime_swf10_enabled"]
}},"$m155",function($0){
with(this){
var $1=canvas.app_runtime=="swf10";if($1!==this["selected"]||!this.inited){
this.setAttribute("selected",$1)
}}},"$m156",function(){
with(this){
return [canvas,"app_runtime"]
}}],["children",LzNode.mergeChildren([],$lzc$class_radiobutton["children"]),"attributes",new LzInheritedHash($lzc$class_radiobutton.attributes)]);Class.make("$lzc$class_m200",$lzc$class_radiobutton,["$m157",function($0){
with(this){
var $1=canvas.runtime_dhtml_enabled;if($1!==this["enabled"]||!this.inited){
this.setAttribute("enabled",$1)
}}},"$m158",function(){
with(this){
return [canvas,"runtime_dhtml_enabled"]
}},"$m159",function($0){
with(this){
var $1=canvas.app_runtime=="dhtml";if($1!==this["selected"]||!this.inited){
this.setAttribute("selected",$1)
}}},"$m160",function(){
with(this){
return [canvas,"app_runtime"]
}}],["children",LzNode.mergeChildren([],$lzc$class_radiobutton["children"]),"attributes",new LzInheritedHash($lzc$class_radiobutton.attributes)]);Class.make("$lzc$class_m202",$lzc$class_checkbox,["$m162",function($0){
with(this){
var $1=canvas.app_debug=="true";if($1!==this["value"]||!this.inited){
this.setAttribute("value",$1)
}}},"$m163",function(){
with(this){
return [canvas,"app_debug"]
}}],["children",LzNode.mergeChildren([],$lzc$class_checkbox["children"]),"attributes",new LzInheritedHash($lzc$class_checkbox.attributes)]);Class.make("$lzc$class_m203",$lzc$class_checkbox,["$m164",function($0){
with(this){
var $1=canvas.app_backtrace=="true";if($1!==this["value"]||!this.inited){
this.setAttribute("value",$1)
}}},"$m165",function(){
with(this){
return [canvas,"app_backtrace"]
}}],["children",LzNode.mergeChildren([],$lzc$class_checkbox["children"]),"attributes",new LzInheritedHash($lzc$class_checkbox.attributes)]);Class.make("$lzc$class_m204",$lzc$class_checkbox,["$m166",function($0){
with(this){
var $1=canvas.app_usemastersprite=="true";if($1!==this["value"]||!this.inited){
this.setAttribute("value",$1)
}}},"$m167",function(){
with(this){
return [canvas,"app_usemastersprite"]
}}],["children",LzNode.mergeChildren([],$lzc$class_checkbox["children"]),"attributes",new LzInheritedHash($lzc$class_checkbox.attributes)]);Class.make("$lzc$class_m205",$lzc$class_button,["$m168",function($0){
with(this){
canvas.gotoApp()
}},"$m169",function($0){
with(this){
var $1=canvas.lzr=="dhtml"?300:168;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m170",function(){
with(this){
return [canvas,"lzr"]
}}],["children",LzNode.mergeChildren([],$lzc$class_button["children"]),"attributes",new LzInheritedHash($lzc$class_button.attributes)]);Class.make("$lzc$class_m201",LzView,["$m161",function($0){
with(this){
this.setAttribute("x",rg_runtime.x+rg_runtime.width+14)
}},"$m171",function(){
with(this){
var $0=cb_backtrace;return $0
}},"$m172",function($0){
with(this){
if(cb_backtrace.value){
cb_debug.setAttribute("value",true)
}}},"$m173",function(){
with(this){
var $0=cb_debug;return $0
}},"$m174",function($0){
with(this){
if(cb_debug.value==false){
cb_backtrace.setAttribute("value",false)
}}}],["children",[{attrs:{bgcolor:LzColorUtils.convertColor("0x0"),height:14,width:1,y:9},"class":LzView},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="cb_debug";cb_debug=$0
}else if(cb_debug===$0){
cb_debug=null;$0.id=null
}},id:"cb_debug",text:"Debug",value:new LzAlwaysExpr("$m162","$m163"),x:16,y:8},"class":$lzc$class_m202},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="cb_backtrace";cb_backtrace=$0
}else if(cb_backtrace===$0){
cb_backtrace=null;$0.id=null
}},id:"cb_backtrace",text:"Backtrace",value:new LzAlwaysExpr("$m164","$m165"),x:82,y:8},"class":$lzc$class_m203},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="cb_usemastersprite";cb_usemastersprite=$0
}else if(cb_usemastersprite===$0){
cb_usemastersprite=null;$0.id=null
}},id:"cb_usemastersprite",text:"Use master sprite",value:new LzAlwaysExpr("$m166","$m167"),x:168,y:8},"class":$lzc$class_m204},{attrs:{$delegates:["onclick","$m168",null],clickable:true,text:"Compile",x:new LzAlwaysExpr("$m169","$m170"),y:3},"class":$lzc$class_m205}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m207",$lzc$class_button,["$m177",function($0){
with(this){
canvas.viewSource()
}}],["children",LzNode.mergeChildren([],$lzc$class_button["children"]),"attributes",new LzInheritedHash($lzc$class_button.attributes)]);Class.make("$lzc$class_m206",LzView,["$m175",function($0){
with(this){
var $1=parent.compilecontrols.width+parent.compilecontrols.x+32;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m176",function(){
with(this){
return [parent.compilecontrols,"width",parent.compilecontrols,"x"]
}}],["children",[{attrs:{$delegates:["onclick","$m177",null],clickable:true,text:"View Source",y:3},"class":$lzc$class_m207}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m208",LzView,["$m178",function($0){
with(this){
var $1=canvas.width-70;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m179",function(){
with(this){
return [canvas,"width"]
}}],["attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m210",$lzc$class_button,["$m182",function($0){
with(this){
canvas.viewWrapper()
}}],["children",LzNode.mergeChildren([],$lzc$class_button["children"]),"attributes",new LzInheritedHash($lzc$class_button.attributes)]);Class.make("$lzc$class_m211",$lzc$class_button,["$m183",function($0){
with(this){
canvas.deploySOLO()
}}],["children",LzNode.mergeChildren([],$lzc$class_button["children"]),"attributes",new LzInheritedHash($lzc$class_button.attributes)]);Class.make("$lzc$class_m212",LzText,["$m184",function($0){
with(this){
canvas.viewDocs()
}}],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_m213",LzText,["$m185",function($0){
with(this){
canvas.viewDev()
}}],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_m214",LzText,["$m186",function($0){
with(this){
canvas.viewForums()
}}],["attributes",new LzInheritedHash(LzText.attributes)]);Class.make("$lzc$class_m209",LzView,["$m180",function($0){
with(this){
var $1=parent.firstrow.width;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m181",function(){
with(this){
return [parent.firstrow,"width"]
}}],["children",[{attrs:{fontsize:11,text:"<b>Deploy:</b>",x:8,y:4},"class":LzText},{attrs:{$delegates:["onclick","$m182",null],clickable:true,text:"Server",x:65},"class":$lzc$class_m210},{attrs:{$delegates:["onclick","$m183",null],clickable:true,text:"SOLO",x:134},"class":$lzc$class_m211},{attrs:{align:"right",fgcolor:LzColorUtils.convertColor("0x15159b"),layout:{axis:"x",spacing:3},options:{ignorelayout:true},y:3},children:[{attrs:{$delegates:["onclick","$m184",null],clickable:true,text:"<u>Documentation</u>"},"class":$lzc$class_m212},{attrs:{$delegates:["onclick","$m185",null],clickable:true,text:"<u>Developer Community</u>"},"class":$lzc$class_m213},{attrs:{$delegates:["onclick","$m186",null],clickable:true,text:"<u>Developer Forums</u>"},"class":$lzc$class_m214}],"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);Class.make("$lzc$class_m215",$lzc$class_checkbox,["$m187",function($0){
with(this){
var $1=parent.firstrow.width+20;if($1!==this["x"]||!this.inited){
this.setAttribute("x",$1)
}}},"$m188",function(){
with(this){
return [parent.firstrow,"width"]
}},"$m189",function($0){
with(this){
var $1=canvas.app_runtime!="dhtml";if($1!==this["visible"]||!this.inited){
this.setAttribute("visible",$1)
}}},"$m190",function(){
with(this){
return [canvas,"app_runtime"]
}},"$m191",function($0){
with(this){
var $1=canvas.app_console_debug;if($1!==this["value"]||!this.inited){
this.setAttribute("value",$1)
}}},"$m192",function(){
with(this){
return [canvas,"app_console_debug"]
}}],["children",LzNode.mergeChildren([],$lzc$class_checkbox["children"]),"attributes",new LzInheritedHash($lzc$class_checkbox.attributes)]);Class.make("$lzc$class_m193",LzView,["$m137",function($0){
with(this){
var $1=parent.width;if($1!==this["width"]||!this.inited){
this.setAttribute("width",$1)
}}},"$m138",function(){
with(this){
return [parent,"width"]
}},"logo",void 0,"controls",void 0],["children",[{attrs:{bgcolor:LzColorUtils.convertColor("0x4c4c4c"),height:new LzAlwaysExpr("$m139","$m140"),name:"logo",resource:"footer_logo"},"class":$lzc$class_m194},{attrs:{firstrow:void 0,name:"controls",x:70},children:[{attrs:{compilecontrols:void 0,name:"firstrow",y:4},children:[{attrs:{fontsize:11,text:"<b>Compile Options:</b>",x:8,y:7},"class":LzText},{attrs:{bgcolor:LzColorUtils.convertColor("0x0"),height:32,width:new LzAlwaysExpr("$m141","$m142"),x:125},"class":$lzc$class_m195},{attrs:{name:"compilecontrols",x:125},children:[{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="rg_runtime";rg_runtime=$0
}else if(rg_runtime===$0){
rg_runtime=null;$0.id=null
}},id:"rg_runtime",layout:{axis:"x","class":"simplelayout",spacing:12},x:10,y:9},children:[{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="rb8";rb8=$0
}else if(rb8===$0){
rb8=null;$0.id=null
}},enabled:new LzAlwaysExpr("$m145","$m146"),id:"rb8",selected:new LzAlwaysExpr("$m147","$m148"),text:"swf8",value:"swf8"},"class":$lzc$class_m197},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="rb9";rb9=$0
}else if(rb9===$0){
rb9=null;$0.id=null
}},enabled:new LzAlwaysExpr("$m149","$m150"),id:"rb9",selected:new LzAlwaysExpr("$m151","$m152"),text:"swf9",value:"swf9"},"class":$lzc$class_m198},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="rb10";rb10=$0
}else if(rb10===$0){
rb10=null;$0.id=null
}},enabled:new LzAlwaysExpr("$m153","$m154"),id:"rb10",selected:new LzAlwaysExpr("$m155","$m156"),text:"swf10",value:"swf10"},"class":$lzc$class_m199},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="rbdhtml";rbdhtml=$0
}else if(rbdhtml===$0){
rbdhtml=null;$0.id=null
}},enabled:new LzAlwaysExpr("$m157","$m158"),id:"rbdhtml",selected:new LzAlwaysExpr("$m159","$m160"),text:"DHTML",value:"dhtml"},"class":$lzc$class_m200}],"class":$lzc$class_radiogroup},{attrs:{$delegates:["onvalue","$m172","$m171","onvalue","$m174","$m173"],x:new LzOnceExpr("$m161")},"class":$lzc$class_m201}],"class":LzView},{attrs:{x:new LzAlwaysExpr("$m175","$m176")},"class":$lzc$class_m206}],"class":LzView},{attrs:{bgcolor:LzColorUtils.convertColor("0x0"),height:1,width:new LzAlwaysExpr("$m178","$m179"),y:39},"class":$lzc$class_m208},{attrs:{width:new LzAlwaysExpr("$m180","$m181"),y:43},"class":$lzc$class_m209},{attrs:{$lzc$bind_id:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
$0.id="cb_remotedebug";cb_remotedebug=$0
}else if(cb_remotedebug===$0){
cb_remotedebug=null;$0.id=null
}},id:"cb_remotedebug",text:"Console Remote Debug",value:new LzAlwaysExpr("$m191","$m192"),visible:new LzAlwaysExpr("$m189","$m190"),x:new LzAlwaysExpr("$m187","$m188"),y:10},"class":$lzc$class_m215}],"class":LzView}],"attributes",new LzInheritedHash(LzView.attributes)]);canvas.LzInstantiateView({attrs:{$lzc$bind_name:function($0,$1){
switch(arguments.length){
case 1:
$1=true;

};if($1){
main=$0
}else if(main===$0){
main=null
}},controls:void 0,logo:void 0,name:"main",width:new LzAlwaysExpr("$m137","$m138")},"class":$lzc$class_m193},91);lz["basefocusview"]=$lzc$class_basefocusview;lz["focusoverlay"]=$lzc$class_focusoverlay;lz["_componentmanager"]=$lzc$class__componentmanager;lz["style"]=$lzc$class_style;lz["statictext"]=$lzc$class_statictext;lz["basecomponent"]=$lzc$class_basecomponent;lz["basebutton"]=$lzc$class_basebutton;lz["swatchview"]=$lzc$class_swatchview;lz["button"]=$lzc$class_button;lz["basevaluecomponent"]=$lzc$class_basevaluecomponent;lz["baseformitem"]=$lzc$class_baseformitem;lz["multistatebutton"]=$lzc$class_multistatebutton;lz["checkbox"]=$lzc$class_checkbox;lz["listselector"]=$lzc$class_listselector;lz["datalistselector"]=$lzc$class_datalistselector;lz["baselist"]=$lzc$class_baselist;lz["baselistitem"]=$lzc$class_baselistitem;lz["simplelayout"]=$lzc$class_simplelayout;lz["radiogroup"]=$lzc$class_radiogroup;lz["radiobutton"]=$lzc$class_radiobutton;canvas.initDone();