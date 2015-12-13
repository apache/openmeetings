/**
 * HoverAccordion - jQuery plugin for intuitively opening accordions and menus
 * 
 * http://berndmatzner.de/jquery/hoveraccordion/
 * 
 * Copyright (c) 2008-2010 Bernd Matzner
 * 
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Version: 0.9.1
 * 
 * Requires jQuery 1.4.4 or higher
 */
(function(h){h.fn.hoverAccordion=function(a){function m(d,f,b){var e=h(o).find("."+a.classOpen).closest("li").find("ul:first");if(false===e.is(":animated")){if(a.keepHeight==true)b=k;if(f.hasClass(a.classOpen)==false){d.children().show();d.animate({height:b},{step:function(c){d.height(b-c)},duration:a.speed});e.animate({height:0},{step:function(c){d.height(b-c)},duration:a.speed}).children().hide();f.addClass(a.classOpen).removeClass(a.classClosed);e.closest("li").removeClass(a.classActive).find("a:first").addClass(a.classClosed).removeClass(a.classOpen)}}} a=jQuery.extend({speed:"fast",activateItem:true,keepHeight:false,onClickOnly:false,classActive:"active",classHeader:"header",classHover:"hover",classOpen:"opened",classClosed:"closed"},a);var o=this,g=window.location.href,l=0,n=0,k=0;h(this).children("li").each(function(){var d=h(this),f=false;n++;var b=d.find("a:first").addClass(a.classHeader);if(b.length>0){b.hover(function(){b.addClass(a.classHover)},function(){b.removeClass(a.classHover)});var e=b.attr("href");if(e=="#")b.click(function(){this.blur(); return false});else if(a.activateItem==true&&g.indexOf(e)>0&&g.length-g.lastIndexOf(e)==e.length){f=true;d.addClass(a.classActive);b.removeClass(a.classClosed).addClass(a.classOpen)}}var c=d.find("ul:first");if(c.length>0){var i=c.height();if(k<i)k=i;a.onClickOnly==true?b.click(function(){m(c,b,i)}):b.hover(function(){l=setInterval(function(){m(c,b,i);clearInterval(l)},400)},function(){clearInterval(l)});if(a.activateItem===true)c.children("li").each(function(){var j=h(this).find("a").attr("href"); if(j)if(g.indexOf(j)>0&&g.length-g.lastIndexOf(j)==j.length){f=true;d.addClass(a.classActive);b.removeClass(a.classClosed).addClass(a.classOpen)}});else if(parseInt(a.activateItem,10)==n){f=true;d.addClass(a.classActive);b.removeClass(a.classClosed).addClass(a.classOpen)}}if(!f){b.removeClass(a.classOpen);if(c.length>0){c.children().hide();b.addClass(a.classClosed)}}});return this}})(jQuery);