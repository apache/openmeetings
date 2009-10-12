var $dhtml=true;var $as3=false;var $js1=true;var $swf7=false;var $swf8=false;var $svg=false;var $as2=false;var $swf9=false;var $profile=false;var $runtime="dhtml";var $swf10=false;var $debug=false;var $j2me=false;if(!document.createElement("canvas").getContext){
(function(){
var getContext;var bind;var onPropertyChange;var onResize;var createMatrixIdentity;var matrixMultiply;var copyState;var processStyle;var processLineCap;var CanvasRenderingContext2D_;var bezierCurveTo;var CanvasGradient_;var CanvasPattern_;getContext=function(){
return this.context_||(this.context_=new CanvasRenderingContext2D_(this))
};bind=function(f,obj,$1){
var a=slice.call(arguments,2);return( function(){
return f.apply(obj,a.concat(slice.call(arguments)))
})
};onPropertyChange=function($1){
var $2=$1.srcElement;switch($1.propertyName){
case "width":
$2.style.width=$2.attributes.width.nodeValue+"px";$2.getContext().clearRect();break;
case "height":
$2.style.height=$2.attributes.height.nodeValue+"px";$2.getContext().clearRect();break;

}};onResize=function($1){
var $2=$1.srcElement;if($2.firstChild){
$2.firstChild.style.width=$2.clientWidth+"px";$2.firstChild.style.height=$2.clientHeight+"px"
}};createMatrixIdentity=function(){
return [[1,0,0],[0,1,0],[0,0,1]]
};matrixMultiply=function($1,$2){
var $3=createMatrixIdentity();for(var $4=0;$4<3;$4++){
for(var $5=0;$5<3;$5++){
var $6=0;for(var $7=0;$7<3;$7++){
$6+=$1[$4][$7]*$2[$7][$5]
};$3[$4][$5]=$6
}};return $3
};copyState=function($1,$2){
$2.fillStyle=$1.fillStyle;$2.lineCap=$1.lineCap;$2.lineJoin=$1.lineJoin;$2.lineWidth=$1.lineWidth;$2.miterLimit=$1.miterLimit;$2.shadowBlur=$1.shadowBlur;$2.shadowColor=$1.shadowColor;$2.shadowOffsetX=$1.shadowOffsetX;$2.shadowOffsetY=$1.shadowOffsetY;$2.strokeStyle=$1.strokeStyle;$2.globalAlpha=$1.globalAlpha;$2.arcScaleX_=$1.arcScaleX_;$2.arcScaleY_=$1.arcScaleY_;$2.lineScale_=$1.lineScale_
};processStyle=function($1){
var $2,$3=1;$1=String($1);if($1.substring(0,3)=="rgb"){
var $4=$1.indexOf("(",3);var $5=$1.indexOf(")",$4+1);var $6=$1.substring($4+1,$5).split(",");$2="#";for(var $7=0;$7<3;$7++){
$2+=dec2hex[Number($6[$7])]
};if($6.length==4&&$1.substr(3,1)=="a"){
$3=$6[3]
}}else{
$2=$1
};return {color:$2,alpha:$3}};processLineCap=function($1){
switch($1){
case "butt":
return "flat";
case "round":
return "round";
case "square":
default:
return "square";

}};CanvasRenderingContext2D_=function($1){
this.m_=createMatrixIdentity();this.mStack_=[];this.aStack_=[];this.currentPath_=[];this.strokeStyle="#000";this.fillStyle="#000";this.lineWidth=1;this.lineJoin="miter";this.lineCap="butt";this.miterLimit=Z*1;this.globalAlpha=1;this.canvas=$1;var $2=$1.ownerDocument.createElement("div");$2.style.width=$1.clientWidth+"px";$2.style.height=$1.clientHeight+"px";$2.style.overflow="hidden";$2.style.position="absolute";$1.appendChild($2);this.element_=$2;this.arcScaleX_=1;this.arcScaleY_=1;this.lineScale_=1
};bezierCurveTo=function($1,$2,$3,$4){
$1.currentPath_.push({type:"bezierCurveTo",cp1x:$2.x,cp1y:$2.y,cp2x:$3.x,cp2y:$3.y,x:$4.x,y:$4.y});$1.currentX_=$4.x;$1.currentY_=$4.y
};CanvasGradient_=function($1){
this.type_=$1;this.x0_=0;this.y0_=0;this.r0_=0;this.x1_=0;this.y1_=0;this.r1_=0;this.colors_=[]
};CanvasPattern_=function(){

};var m=Math;var mr=m.round;var ms=m.sin;var mc=m.cos;var abs=m.abs;var sqrt=m.sqrt;var Z=10;var Z2=Z/2;var slice=Array.prototype.slice;var $1={init:function($1){
if(lz.embed.browser.isIE){
var $2=$1||document;$2.createElement("canvas");$2.attachEvent("onreadystatechange",bind(this.init_,this,$2))
}},init_:function($1){
var $2=lz.embed.browser.version;if(!$1.namespaces["g_vml_"]){
if($2<8){
$1.namespaces.add("g_vml_","urn:schemas-microsoft-com:vml")
}else{
$1.namespaces.add("g_vml_","urn:schemas-microsoft-com:vml","#default#VML")
}};if(!$1.namespaces["g_o_"]){
if($2<8){
$1.namespaces.add("g_o_","urn:schemas-microsoft-com:office:office")
}else{
$1.namespaces.add("g_o_","urn:schemas-microsoft-com:office:office","#default#VML")
}};if(!$1.styleSheets["ex_canvas_"]){
var $3=$1.createStyleSheet();$3.owningElement.id="ex_canvas_";$3.cssText="canvas{display:inline-block;overflow:hidden;"+"position:absolute;"+"text-align:left;width:300px;height:150px}"+"g_vml_\\:*{behavior:url(#default#VML)}"+"g_o_\\:*{behavior:url(#default#VML)}"
};var $4=$1.getElementsByTagName("canvas");for(var $5=0;$5<$4.length;$5++){
this.initElement($4[$5])
}},initElement:function($1){
if(!$1.getContext){
$1.getContext=getContext;$1.innerHTML="";$1.attachEvent("onpropertychange",onPropertyChange);$1.attachEvent("onresize",onResize);var $2=$1.attributes;if($2.width&&$2.width.specified){
$1.style.width=$2.width.nodeValue+"px"
}else{
$1.width=$1.clientWidth
};if($2.height&&$2.height.specified){
$1.style.height=$2.height.nodeValue+"px"
}else{
$1.height=$1.clientHeight
}};return $1
}};$1.init();var dec2hex=[];for(var $2=0;$2<16;$2++){
for(var $3=0;$3<16;$3++){
dec2hex[$2*16+$3]=$2.toString(16)+$3.toString(16)
}};var $4=CanvasRenderingContext2D_.prototype;$4.clearRect=function(){
this.element_.innerHTML=""
};$4.beginPath=function(){
this.currentPath_=[]
};$4.moveTo=function($1,$2){
var $3=this.getCoords_($1,$2);this.currentPath_.push({type:"moveTo",x:$3.x,y:$3.y});this.currentX_=$3.x;this.currentY_=$3.y
};$4.lineTo=function($1,$2){
var $3=this.getCoords_($1,$2);this.currentPath_.push({type:"lineTo",x:$3.x,y:$3.y});this.currentX_=$3.x;this.currentY_=$3.y
};$4.bezierCurveTo=function($1,$2,$3,$4,$5,$6){
var $7=this.getCoords_($5,$6);var $8=this.getCoords_($1,$2);var $9=this.getCoords_($3,$4);bezierCurveTo(this,$8,$9,$7)
};$4.quadraticCurveTo=function($1,$2,$3,$4){
var $5=this.getCoords_($1,$2);var $6=this.getCoords_($3,$4);var $7={x:this.currentX_+2/3*($5.x-this.currentX_),y:this.currentY_+2/3*($5.y-this.currentY_)};var $8={x:$7.x+($6.x-this.currentX_)/3,y:$7.y+($6.y-this.currentY_)/3};bezierCurveTo(this,$7,$8,$6)
};$4.arc=function($1,$2,$3,$4,$5,$6){
$3*=Z;var $7=$6?"at":"wa";var $8=$1+mc($4)*$3-Z2;var $9=$2+ms($4)*$3-Z2;var $10=$1+mc($5)*$3-Z2;var $11=$2+ms($5)*$3-Z2;if($8==$10&&!$6){
$8+=0.125
};var $12=this.getCoords_($1,$2);var $13=this.getCoords_($8,$9);var $14=this.getCoords_($10,$11);this.currentPath_.push({type:$7,x:$12.x,y:$12.y,radius:$3,xStart:$13.x,yStart:$13.y,xEnd:$14.x,yEnd:$14.y})
};$4.rect=function($1,$2,$3,$4){
this.moveTo($1,$2);this.lineTo($1+$3,$2);this.lineTo($1+$3,$2+$4);this.lineTo($1,$2+$4);this.closePath()
};$4.strokeRect=function($1,$2,$3,$4){
var $5=this.currentPath_;this.beginPath();this.moveTo($1,$2);this.lineTo($1+$3,$2);this.lineTo($1+$3,$2+$4);this.lineTo($1,$2+$4);this.closePath();this.stroke();this.currentPath_=$5
};$4.fillRect=function($1,$2,$3,$4){
var $5=this.currentPath_;this.beginPath();this.moveTo($1,$2);this.lineTo($1+$3,$2);this.lineTo($1+$3,$2+$4);this.lineTo($1,$2+$4);this.closePath();this.fill();this.currentPath_=$5
};$4.createLinearGradient=function($1,$2,$3,$4){
var $5=new CanvasGradient_("gradient");$5.x0_=$1;$5.y0_=$2;$5.x1_=$3;$5.y1_=$4;return $5
};$4.createRadialGradient=function($1,$2,$3,$4,$5,$6){
var $7=new CanvasGradient_("gradientradial");$7.x0_=$1;$7.y0_=$2;$7.r0_=$3;$7.x1_=$4;$7.y1_=$5;$7.r1_=$6;return $7
};$4.drawImage=function($1,$2){
var $3,$4,$5,$6,$7,$8,$9,$10;var $11=$1.runtimeStyle.width;var $12=$1.runtimeStyle.height;$1.runtimeStyle.width="auto";$1.runtimeStyle.height="auto";var $13=$1.width;var $14=$1.height;$1.runtimeStyle.width=$11;$1.runtimeStyle.height=$12;if(arguments.length==3){
$3=arguments[1];$4=arguments[2];$7=$8=0;$9=$5=$13;$10=$6=$14
}else{
if(arguments.length==5){
$3=arguments[1];$4=arguments[2];$5=arguments[3];$6=arguments[4];$7=$8=0;$9=$13;$10=$14
}else{
if(arguments.length==9){
$7=arguments[1];$8=arguments[2];$9=arguments[3];$10=arguments[4];$3=arguments[5];$4=arguments[6];$5=arguments[7];$6=arguments[8]
}else{
throw Error("Invalid number of arguments")
}}};var $15=this.getCoords_($3,$4);var $16=$9/2;var $17=$10/2;var $18=[];var $19=10;var $20=10;$18.push(" <g_vml_:group",' coordsize="',Z*$19,",",Z*$20,'"',' coordorigin="0,0"',' style="width:',$19,"px;height:",$20,"px;position:absolute;");if(this.m_[0][0]!=1||this.m_[0][1]){
var $21=[];$21.push("M11=",this.m_[0][0],",","M12=",this.m_[1][0],",","M21=",this.m_[0][1],",","M22=",this.m_[1][1],",","Dx=",mr($15.x/Z),",","Dy=",mr($15.y/Z),"");var $22=$15;var $23=this.getCoords_($3+$5,$4);var $24=this.getCoords_($3,$4+$6);var $25=this.getCoords_($3+$5,$4+$6);$22.x=m.max($22.x,$23.x,$24.x,$25.x);$22.y=m.max($22.y,$23.y,$24.y,$25.y);$18.push("padding:0 ",mr($22.x/Z),"px ",mr($22.y/Z),"px 0;filter:progid:DXImageTransform.Microsoft.Matrix(",$21.join(""),", sizingmethod='clip');")
}else{
$18.push("top:",mr($15.y/Z),"px;left:",mr($15.x/Z),"px;")
};$18.push(' ">','<g_vml_:image src="',$1.src,'"',' style="width:',Z*$5,"px;"," height:",Z*$6,'px;"',' cropleft="',$7/$13,'"',' croptop="',$8/$14,'"',' cropright="',($13-$7-$9)/$13,'"',' cropbottom="',($14-$8-$10)/$14,'"'," />","</g_vml_:group>");this.element_.insertAdjacentHTML("BeforeEnd",$18.join(""))
};$4.stroke=function($1){
var $2=[];var $3=false;var $4=processStyle($1?this.fillStyle:this.strokeStyle);var $5=$4.color;var $6=$4.alpha*this.globalAlpha;var $7=10;var $8=10;$2.push("<g_vml_:shape",' filled="',!(!$1),'"',' style="position:absolute;width:',$7,"px;height:",$8,'px;"',' coordorigin="0 0" coordsize="',Z*$7," ",Z*$8,'"',' stroked="',!$1,'"',' path="');var $9=false;var $10={x:null,y:null};var $11={x:null,y:null};for(var $12=0;$12<this.currentPath_.length;$12++){
var $13=this.currentPath_[$12];var $14;switch($13.type){
case "moveTo":
$14=$13;$2.push(" m ",mr($13.x),",",mr($13.y));break;
case "lineTo":
$2.push(" l ",mr($13.x),",",mr($13.y));break;
case "close":
$2.push(" x ");$13=null;break;
case "bezierCurveTo":
$2.push(" c ",mr($13.cp1x),",",mr($13.cp1y),",",mr($13.cp2x),",",mr($13.cp2y),",",mr($13.x),",",mr($13.y));break;
case "at":
case "wa":
$2.push(" ",$13.type," ",mr($13.x-this.arcScaleX_*$13.radius),",",mr($13.y-this.arcScaleY_*$13.radius)," ",mr($13.x+this.arcScaleX_*$13.radius),",",mr($13.y+this.arcScaleY_*$13.radius)," ",mr($13.xStart),",",mr($13.yStart)," ",mr($13.xEnd),",",mr($13.yEnd));break;

};if($13){
if($10.x==null||$13.x<$10.x){
$10.x=$13.x
};if($11.x==null||$13.x>$11.x){
$11.x=$13.x
};if($10.y==null||$13.y<$10.y){
$10.y=$13.y
};if($11.y==null||$13.y>$11.y){
$11.y=$13.y
}}};$2.push(' ">');if(!$1){
var $15=this.lineScale_*this.lineWidth;if($15<1){
$6*=$15
};$2.push("<g_vml_:stroke",' opacity="',$6,'"',' joinstyle="',this.lineJoin,'"',' miterlimit="',this.miterLimit,'"',' endcap="',processLineCap(this.lineCap),'"',' weight="',$15,'px"',' color="',$5,'" />')
}else{
if(typeof this.fillStyle=="object"){
var $16=this.fillStyle;var $17=0;var $18={x:0,y:0};var $19=0;var $20=1;if($16.type_=="gradient"){
var $21=$16.x0_/this.arcScaleX_;var $22=$16.y0_/this.arcScaleY_;var $23=$16.x1_/this.arcScaleX_;var $24=$16.y1_/this.arcScaleY_;var $25=this.getCoords_($21,$22);var $26=this.getCoords_($23,$24);var $27=$26.x-$25.x;var $28=$26.y-$25.y;$17=Math.atan2($27,$28)*180/Math.PI;if($17<0){
$17+=360
};if($17<1.0E-6){
$17=0
}}else{
var $25=this.getCoords_($16.x0_,$16.y0_);var $29=$11.x-$10.x;var $30=$11.y-$10.y;$18={x:($25.x-$10.x)/$29,y:($25.y-$10.y)/$30};$29/=this.arcScaleX_*Z;$30/=this.arcScaleY_*Z;var $31=m.max($29,$30);$19=2*$16.r0_/$31;$20=2*$16.r1_/$31-$19
};var $32=$16.colors_;$32.sort(function($1,$2){
return $1.offset-$2.offset
});var $33=$32.length;var $34=$32[0].color;var $35=$32[$33-1].color;var $36=$32[0].alpha*this.globalAlpha;var $37=$32[$33-1].alpha*this.globalAlpha;var $38=[];for(var $12=0;$12<$33;$12++){
var $39=$32[$12];$38.push($39.offset*$20+$19+" "+$39.color)
};$2.push('<g_vml_:fill type="',$16.type_,'"',' method="none" focus="100%"',' color="',$34,'"',' color2="',$35,'"',' colors="',$38.join(","),'"',' opacity="',$37,'"',' g_o_:opacity2="',$36,'"',' angle="',$17,'"',' focusposition="',$18.x,",",$18.y,'" />')
}else{
$2.push('<g_vml_:fill color="',$5,'" opacity="',$6,'" />')
}};$2.push("</g_vml_:shape>");this.element_.insertAdjacentHTML("beforeEnd",$2.join(""))
};$4.fill=function(){
this.stroke(true)
};$4.closePath=function(){
this.currentPath_.push({type:"close"})
};$4.getCoords_=function($1,$2){
var $3=this.m_;return {x:Z*($1*$3[0][0]+$2*$3[1][0]+$3[2][0])-Z2,y:Z*($1*$3[0][1]+$2*$3[1][1]+$3[2][1])-Z2}};$4.save=function(){
var $1={};copyState(this,$1);this.aStack_.push($1);this.mStack_.push(this.m_);this.m_=matrixMultiply(createMatrixIdentity(),this.m_)
};$4.restore=function(){
copyState(this.aStack_.pop(),this);this.m_=this.mStack_.pop()
};$4.translate=function($1,$2){
var $3=[[1,0,0],[0,1,0],[$1,$2,1]];this.m_=matrixMultiply($3,this.m_)
};$4.rotate=function($1){
var $2=mc($1);var $3=ms($1);var $4=[[$2,$3,0],[-$3,$2,0],[0,0,1]];this.m_=matrixMultiply($4,this.m_)
};$4.scale=function($1,$2){
this.arcScaleX_*=$1;this.arcScaleY_*=$2;var $3=[[$1,0,0],[0,$2,0],[0,0,1]];var $4=this.m_=matrixMultiply($3,this.m_);var $5=$4[0][0]*$4[1][1]-$4[0][1]*$4[1][0];this.lineScale_=sqrt(abs($5))
};$4.clip=function(){

};$4.arcTo=function(){

};$4.createPattern=function(){
return new CanvasPattern_()
};CanvasGradient_.prototype.addColorStop=function($1,$2){
$2=processStyle($2);this.colors_.push({offset:$1,color:$2.color,alpha:$2.alpha})
};G_vmlCanvasManager=$1;CanvasRenderingContext2D=CanvasRenderingContext2D_;CanvasGradient=CanvasGradient_;CanvasPattern=CanvasPattern_
})()
}