var $runtime="dhtml";var $dhtml=true;var $as3=false;var $as2=false;var $swf10=false;var $j2me=false;var $debug=false;var $js1=true;var $backtrace=false;var $swf7=false;var $swf9=false;var $swf8=false;var $svg=false;var $profile=false;if(!document.createElement("canvas").getContext){
(function(){
var getContext;var bind;var addDeclarations;var onPropertyChange;var onResize;var createMatrixIdentity;var matrixMultiply;var copyState;var processStyle;var processLineCap;var CanvasRenderingContext2D_;var bezierCurveTo;var CanvasGradient_;var CanvasPattern_;getContext=function(){
return this.context_||(this.context_=new CanvasRenderingContext2D_(this))
};bind=function(f,obj,$0){
var a=slice.call(arguments,2);return( function(){
return f.apply(obj,a.concat(slice.call(arguments)))
})
};addDeclarations=function($0){
var $1=lz.embed.browser.version;if(!$0.namespaces["g_vml_"]){
if($1<8){
$0.namespaces.add("g_vml_","urn:schemas-microsoft-com:vml")
}else{
$0.namespaces.add("g_vml_","urn:schemas-microsoft-com:vml","#default#VML")
}};if(!$0.namespaces["g_o_"]){
if($1<8){
$0.namespaces.add("g_o_","urn:schemas-microsoft-com:office:office")
}else{
$0.namespaces.add("g_o_","urn:schemas-microsoft-com:office:office","#default#VML")
}};if(!$0.styleSheets["ex_canvas_"]){
var $2=$0.createStyleSheet();$2.owningElement.id="ex_canvas_";$2.cssText="canvas{display:inline-block;overflow:hidden;"+"position:absolute;"+"text-align:left;width:300px;height:150px}"+"g_vml_\\:*{behavior:url(#default#VML)}"+"g_o_\\:*{behavior:url(#default#VML)}"
}};onPropertyChange=function($0){
var $1=$0.srcElement;switch($0.propertyName){
case "width":
$1.style.width=$1.attributes.width.nodeValue+"px";$1.getContext().clearRect();break;
case "height":
$1.style.height=$1.attributes.height.nodeValue+"px";$1.getContext().clearRect();break;

}};onResize=function($0){
var $1=$0.srcElement;if($1.firstChild){
$1.firstChild.style.width=$1.clientWidth+"px";$1.firstChild.style.height=$1.clientHeight+"px"
}};createMatrixIdentity=function(){
return [[1,0,0],[0,1,0],[0,0,1]]
};matrixMultiply=function($0,$1){
var $2=createMatrixIdentity();for(var $3=0;$3<3;$3++){
for(var $4=0;$4<3;$4++){
var $5=0;for(var $6=0;$6<3;$6++){
$5+=$0[$3][$6]*$1[$6][$4]
};$2[$3][$4]=$5
}};return $2
};copyState=function($0,$1){
$1.fillStyle=$0.fillStyle;$1.lineCap=$0.lineCap;$1.lineJoin=$0.lineJoin;$1.lineWidth=$0.lineWidth;$1.miterLimit=$0.miterLimit;$1.shadowBlur=$0.shadowBlur;$1.shadowColor=$0.shadowColor;$1.shadowOffsetX=$0.shadowOffsetX;$1.shadowOffsetY=$0.shadowOffsetY;$1.strokeStyle=$0.strokeStyle;$1.globalAlpha=$0.globalAlpha;$1.arcScaleX_=$0.arcScaleX_;$1.arcScaleY_=$0.arcScaleY_;$1.lineScale_=$0.lineScale_
};processStyle=function($0){
var $1,$2=1;$0=String($0);if($0.substring(0,3)=="rgb"){
var $3=$0.indexOf("(",3);var $4=$0.indexOf(")",$3+1);var $5=$0.substring($3+1,$4).split(",");$1="#";for(var $6=0;$6<3;$6++){
$1+=dec2hex[Number($5[$6])]
};if($5.length==4&&$0.substr(3,1)=="a"){
$2=$5[3]
}}else{
$1=$0
};return {color:$1,alpha:$2}};processLineCap=function($0){
switch($0){
case "butt":
return "flat";
case "round":
return "round";
case "square":
default:
return "square";

}};CanvasRenderingContext2D_=function($0){
this.m_=createMatrixIdentity();this.mStack_=[];this.aStack_=[];this.currentPath_=[];this.strokeStyle="#000";this.fillStyle="#000";this.lineWidth=1;this.lineJoin="miter";this.lineCap="butt";this.miterLimit=Z*1;this.globalAlpha=1;this.canvas=$0;var $1=$0.ownerDocument.createElement("div");$1.style.width=$0.clientWidth+"px";$1.style.height=$0.clientHeight+"px";$1.style.overflow="hidden";$1.style.position="absolute";$0.appendChild($1);this.element_=$1;this.arcScaleX_=1;this.arcScaleY_=1;this.lineScale_=1
};bezierCurveTo=function($0,$1,$2,$3){
$0.currentPath_.push({type:"bezierCurveTo",cp1x:$1.x,cp1y:$1.y,cp2x:$2.x,cp2y:$2.y,x:$3.x,y:$3.y});$0.currentX_=$3.x;$0.currentY_=$3.y
};CanvasGradient_=function($0){
this.type_=$0;this.x0_=0;this.y0_=0;this.r0_=0;this.x1_=0;this.y1_=0;this.r1_=0;this.colors_=[]
};CanvasPattern_=function(){};var m=Math;var mr=m.round;var ms=m.sin;var mc=m.cos;var abs=m.abs;var sqrt=m.sqrt;var Z=10;var Z2=Z/2;var slice=Array.prototype.slice;var $0={init:function($0){
if(lz.embed.browser.isIE){
var $1=$0||document;$1.createElement("canvas");$1.attachEvent("onreadystatechange",bind(this.init_,this,$1));if(lz.embed.browser.version>=8){
addDeclarations($1)
}}},init_:function($0){
if(lz.embed.browser.version<8){
addDeclarations($0)
};var $1=$0.getElementsByTagName("canvas");for(var $2=0;$2<$1.length;$2++){
this.initElement($1[$2])
}},initElement:function($0){
if(!$0.getContext){
$0.getContext=getContext;$0.innerHTML="";$0.attachEvent("onpropertychange",onPropertyChange);$0.attachEvent("onresize",onResize);var $1=$0.attributes;if($1.width&&$1.width.specified){
$0.style.width=$1.width.nodeValue+"px"
}else{
$0.width=$0.clientWidth
};if($1.height&&$1.height.specified){
$0.style.height=$1.height.nodeValue+"px"
}else{
$0.height=$0.clientHeight
}};return $0
}};$0.init();var dec2hex=[];for(var $1=0;$1<16;$1++){
for(var $2=0;$2<16;$2++){
dec2hex[$1*16+$2]=$1.toString(16)+$2.toString(16)
}};var $3=CanvasRenderingContext2D_.prototype;$3.clearRect=function(){
this.element_.innerHTML=""
};$3.beginPath=function(){
this.currentPath_=[]
};$3.moveTo=function($0,$1){
var $2=this.getCoords_($0,$1);this.currentPath_.push({type:"moveTo",x:$2.x,y:$2.y});this.currentX_=$2.x;this.currentY_=$2.y
};$3.lineTo=function($0,$1){
var $2=this.getCoords_($0,$1);this.currentPath_.push({type:"lineTo",x:$2.x,y:$2.y});this.currentX_=$2.x;this.currentY_=$2.y
};$3.bezierCurveTo=function($0,$1,$2,$3,$4,$5){
var $6=this.getCoords_($4,$5);var $7=this.getCoords_($0,$1);var $8=this.getCoords_($2,$3);bezierCurveTo(this,$7,$8,$6)
};$3.quadraticCurveTo=function($0,$1,$2,$3){
var $4=this.getCoords_($0,$1);var $5=this.getCoords_($2,$3);var $6={x:this.currentX_+2/3*($4.x-this.currentX_),y:this.currentY_+2/3*($4.y-this.currentY_)};var $7={x:$6.x+($5.x-this.currentX_)/3,y:$6.y+($5.y-this.currentY_)/3};bezierCurveTo(this,$6,$7,$5)
};$3.arc=function($0,$1,$2,$3,$4,$5){
$2*=Z;var $6=$5?"at":"wa";var $7=$0+mc($3)*$2-Z2;var $8=$1+ms($3)*$2-Z2;var $9=$0+mc($4)*$2-Z2;var $a=$1+ms($4)*$2-Z2;if($7==$9&&!$5){
$7+=0.125
};var $b=this.getCoords_($0,$1);var $c=this.getCoords_($7,$8);var $d=this.getCoords_($9,$a);this.currentPath_.push({type:$6,x:$b.x,y:$b.y,radius:$2,xStart:$c.x,yStart:$c.y,xEnd:$d.x,yEnd:$d.y})
};$3.rect=function($0,$1,$2,$3){
this.moveTo($0,$1);this.lineTo($0+$2,$1);this.lineTo($0+$2,$1+$3);this.lineTo($0,$1+$3);this.closePath()
};$3.strokeRect=function($0,$1,$2,$3){
var $4=this.currentPath_;this.beginPath();this.moveTo($0,$1);this.lineTo($0+$2,$1);this.lineTo($0+$2,$1+$3);this.lineTo($0,$1+$3);this.closePath();this.stroke();this.currentPath_=$4
};$3.fillRect=function($0,$1,$2,$3){
var $4=this.currentPath_;this.beginPath();this.moveTo($0,$1);this.lineTo($0+$2,$1);this.lineTo($0+$2,$1+$3);this.lineTo($0,$1+$3);this.closePath();this.fill();this.currentPath_=$4
};$3.createLinearGradient=function($0,$1,$2,$3){
var $4=new CanvasGradient_("gradient");$4.x0_=$0;$4.y0_=$1;$4.x1_=$2;$4.y1_=$3;return $4
};$3.createRadialGradient=function($0,$1,$2,$3,$4,$5){
var $6=new CanvasGradient_("gradientradial");$6.x0_=$0;$6.y0_=$1;$6.r0_=$2;$6.x1_=$3;$6.y1_=$4;$6.r1_=$5;return $6
};$3.drawImage=function($0,$1){
var $2,$3,$4,$5,$6,$7,$8,$9;var $a=$0.runtimeStyle.width;var $b=$0.runtimeStyle.height;$0.runtimeStyle.width="auto";$0.runtimeStyle.height="auto";var $c=$0.width;var $d=$0.height;$0.runtimeStyle.width=$a;$0.runtimeStyle.height=$b;if(arguments.length==3){
$2=arguments[1];$3=arguments[2];$6=$7=0;$8=$4=$c;$9=$5=$d
}else if(arguments.length==5){
$2=arguments[1];$3=arguments[2];$4=arguments[3];$5=arguments[4];$6=$7=0;$8=$c;$9=$d
}else if(arguments.length==9){
$6=arguments[1];$7=arguments[2];$8=arguments[3];$9=arguments[4];$2=arguments[5];$3=arguments[6];$4=arguments[7];$5=arguments[8]
}else{
throw Error("Invalid number of arguments")
};var $e=this.getCoords_($2,$3);var $f=$8/2;var $g=$9/2;var $h=[];var $i=10;var $j=10;$h.push(" <g_vml_:group",' coordsize="',Z*$i,",",Z*$j,'"',' coordorigin="0,0"',' style="width:',$i,"px;height:",$j,"px;position:absolute;");if(this.m_[0][0]!=1||this.m_[0][1]){
var $k=[];$k.push("M11=",this.m_[0][0],",","M12=",this.m_[1][0],",","M21=",this.m_[0][1],",","M22=",this.m_[1][1],",","Dx=",mr($e.x/Z),",","Dy=",mr($e.y/Z),"");var $l=$e;var $m=this.getCoords_($2+$4,$3);var $n=this.getCoords_($2,$3+$5);var $o=this.getCoords_($2+$4,$3+$5);$l.x=m.max($l.x,$m.x,$n.x,$o.x);$l.y=m.max($l.y,$m.y,$n.y,$o.y);$h.push("padding:0 ",mr($l.x/Z),"px ",mr($l.y/Z),"px 0;filter:progid:DXImageTransform.Microsoft.Matrix(",$k.join(""),", sizingmethod='clip');")
}else{
$h.push("top:",mr($e.y/Z),"px;left:",mr($e.x/Z),"px;")
};$h.push(' ">','<g_vml_:image src="',$0.src,'"',' style="width:',Z*$4,"px;"," height:",Z*$5,'px;"',' cropleft="',$6/$c,'"',' croptop="',$7/$d,'"',' cropright="',($c-$6-$8)/$c,'"',' cropbottom="',($d-$7-$9)/$d,'"'," />","</g_vml_:group>");this.element_.insertAdjacentHTML("BeforeEnd",$h.join(""))
};$3.stroke=function($0){
var $1=[];var $2=false;var $3=processStyle($0?this.fillStyle:this.strokeStyle);var $4=$3.color;var $5=$3.alpha*this.globalAlpha;var $6=10;var $7=10;$1.push("<g_vml_:shape",' filled="',!(!$0),'"',' style="position:absolute;width:',$6,"px;height:",$7,'px;"',' coordorigin="0 0" coordsize="',Z*$6," ",Z*$7,'"',' stroked="',!$0,'"',' path="');var $8=false;var $9={x:null,y:null};var $a={x:null,y:null};for(var $b=0;$b<this.currentPath_.length;$b++){
var $c=this.currentPath_[$b];var $d;switch($c.type){
case "moveTo":
$d=$c;$1.push(" m ",mr($c.x),",",mr($c.y));break;
case "lineTo":
$1.push(" l ",mr($c.x),",",mr($c.y));break;
case "close":
$1.push(" x ");$c=null;break;
case "bezierCurveTo":
$1.push(" c ",mr($c.cp1x),",",mr($c.cp1y),",",mr($c.cp2x),",",mr($c.cp2y),",",mr($c.x),",",mr($c.y));break;
case "at":
case "wa":
$1.push(" ",$c.type," ",mr($c.x-this.arcScaleX_*$c.radius),",",mr($c.y-this.arcScaleY_*$c.radius)," ",mr($c.x+this.arcScaleX_*$c.radius),",",mr($c.y+this.arcScaleY_*$c.radius)," ",mr($c.xStart),",",mr($c.yStart)," ",mr($c.xEnd),",",mr($c.yEnd));break;

};if($c){
if($9.x==null||$c.x<$9.x){
$9.x=$c.x
};if($a.x==null||$c.x>$a.x){
$a.x=$c.x
};if($9.y==null||$c.y<$9.y){
$9.y=$c.y
};if($a.y==null||$c.y>$a.y){
$a.y=$c.y
}}};$1.push(' ">');if(!$0){
var $e=this.lineScale_*this.lineWidth;if($e<1){
$5*=$e
};$1.push("<g_vml_:stroke",' opacity="',$5,'"',' joinstyle="',this.lineJoin,'"',' miterlimit="',this.miterLimit,'"',' endcap="',processLineCap(this.lineCap),'"',' weight="',$e,'px"',' color="',$4,'" />')
}else if(typeof this.fillStyle=="object"){
var $f=this.fillStyle;var $g=0;var $h={x:0,y:0};var $i=0;var $j=1;if($f.type_=="gradient"){
var $k=$f.x0_/this.arcScaleX_;var $l=$f.y0_/this.arcScaleY_;var $m=$f.x1_/this.arcScaleX_;var $n=$f.y1_/this.arcScaleY_;var $o=this.getCoords_($k,$l);var $p=this.getCoords_($m,$n);var $q=$p.x-$o.x;var $r=$p.y-$o.y;$g=Math.atan2($q,$r)*180/Math.PI;if($g<0){
$g+=360
};if($g<1.0E-6){
$g=0
}}else{
var $o=this.getCoords_($f.x0_,$f.y0_);var $s=$a.x-$9.x;var $t=$a.y-$9.y;$h={x:($o.x-$9.x)/$s,y:($o.y-$9.y)/$t};$s/=this.arcScaleX_*Z;$t/=this.arcScaleY_*Z;var $u=m.max($s,$t);$i=2*$f.r0_/$u;$j=2*$f.r1_/$u-$i
};var $v=$f.colors_;$v.sort(function($0,$1){
return $0.offset-$1.offset
});var $w=$v.length;var $x=$v[0].color;var $y=$v[$w-1].color;var $z=$v[0].alpha*this.globalAlpha;var $10=$v[$w-1].alpha*this.globalAlpha;var $11=[];for(var $b=0;$b<$w;$b++){
var $12=$v[$b];$11.push($12.offset*$j+$i+" "+$12.color)
};$1.push('<g_vml_:fill type="',$f.type_,'"',' method="none" focus="100%"',' color="',$x,'"',' color2="',$y,'"',' colors="',$11.join(","),'"',' opacity="',$10,'"',' g_o_:opacity2="',$z,'"',' angle="',$g,'"',' focusposition="',$h.x,",",$h.y,'" />')
}else{
$1.push('<g_vml_:fill color="',$4,'" opacity="',$5,'" />')
};$1.push("</g_vml_:shape>");this.element_.insertAdjacentHTML("beforeEnd",$1.join(""))
};$3.fill=function(){
this.stroke(true)
};$3.closePath=function(){
this.currentPath_.push({type:"close"})
};$3.getCoords_=function($0,$1){
var $2=this.m_;return {x:Z*($0*$2[0][0]+$1*$2[1][0]+$2[2][0])-Z2,y:Z*($0*$2[0][1]+$1*$2[1][1]+$2[2][1])-Z2}};$3.save=function(){
var $0={};copyState(this,$0);this.aStack_.push($0);this.mStack_.push(this.m_);this.m_=matrixMultiply(createMatrixIdentity(),this.m_)
};$3.restore=function(){
copyState(this.aStack_.pop(),this);this.m_=this.mStack_.pop()
};$3.translate=function($0,$1){
var $2=[[1,0,0],[0,1,0],[$0,$1,1]];this.m_=matrixMultiply($2,this.m_)
};$3.rotate=function($0){
var $1=mc($0);var $2=ms($0);var $3=[[$1,$2,0],[-$2,$1,0],[0,0,1]];this.m_=matrixMultiply($3,this.m_)
};$3.scale=function($0,$1){
this.arcScaleX_*=$0;this.arcScaleY_*=$1;var $2=[[$0,0,0],[0,$1,0],[0,0,1]];var $3=this.m_=matrixMultiply($2,this.m_);var $4=$3[0][0]*$3[1][1]-$3[0][1]*$3[1][0];this.lineScale_=sqrt(abs($4))
};$3.clip=function(){};$3.arcTo=function(){};$3.createPattern=function(){
return new CanvasPattern_()
};CanvasGradient_.prototype.addColorStop=function($0,$1){
$1=processStyle($1);this.colors_.push({offset:$0,color:$1.color,alpha:$1.alpha})
};G_vmlCanvasManager=$0;CanvasRenderingContext2D=CanvasRenderingContext2D_;CanvasGradient=CanvasGradient_;CanvasPattern=CanvasPattern_
})()
}