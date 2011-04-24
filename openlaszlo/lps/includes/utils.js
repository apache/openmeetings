//* A_LZ_COPYRIGHT_BEGIN ******************************************************
//* Copyright 2007-2010 Laszlo Systems, Inc.  All Rights Reserved.            *
//* Use is subject to license terms.                                          *
//* A_LZ_COPYRIGHT_END ********************************************************

function getQueryVariable(variable, defval) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if (pair[0] == variable) {
      return pair[1];
    }
  } 
  return defval;
}

function writeOpenLaszloApp (loc, base, lzr, w, h) {
  loc.innerHTML =
        "<iframe src='" + base + "?lzt=html&amp;lzr=" + lzr + "' style='width: " + w + "; height: " + h + ";  margin: 0; padding: 0; border: 0 none;' frameborder='0' scrolling='no'/>";
}

var __tagmap = { "swf7" : "Flash 7", "swf8" : "Flash 8", "dhtml" : "DHTML", "swf9" : "Flash 9", "swf10" : "Flash 10" };

function lzRuntimeTagToRuntimeName (tag) {
    if (tag in __tagmap)
        return __tagmap[tag];
    else
        return tag;
}
