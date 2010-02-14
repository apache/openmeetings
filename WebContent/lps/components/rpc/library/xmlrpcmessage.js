<library>
<include href="rpc.js" />
<script when="immediate">
<![CDATA[

/* LZ_COPYRIGHT_BEGIN */
/****************************************************************************
 * Copyright (c) 2001-2009 Laszlo Systems, Inc.  All Rights Reserved.       *
 * Use is subject to license terms                                          *
 ****************************************************************************/
/* LZ_COPYRIGHT_END */

/*
xmlrpc.js beta version 1
Tool for creating XML-RPC formatted requests in JavaScript

Copyright 2001 Scott Andrew LePera
scott@scottandrew.com
http://www.scottandrew.com/xml-rpc

License: 
You are granted the right to use and/or redistribute this 
code only if this license and the copyright notice are included 
and you accept that no warranty of any kind is made or implied 
by the author.
*/

class XMLRPCMessage {

    var method:String;

    var params:Array;

    function XMLRPCMessage (methodname){
        this.method = methodname;
        this.params = [];
    }

    function setMethod(methodName){
        if (!methodName) return;
        this.method = methodName;
    }

    function addParameter (data){
        if (arguments.length==0) return;
        this.params[this.params.length] = data;
    }

    function xml () {

        var method = this.method;
  
        // assemble the XML message header
        var xml = "";
  
        xml += '<?xml version="1.0"?>\n';
        xml += "<methodCall>\n";
        xml += "<methodName>" + method+ "</methodName>\n";
        xml += "<params>\n";
  
        // do individual parameters
        for (var i = 0; i < this.params.length; i++){
            var data = this.params[i];

            xml += "<param>\n";

            var dtype = XMLRPCMessage.dataTypeOf(data);

            xml += "<value>" + XMLRPCMessage.getParamXML(dtype,data) + "</value>\n";
    
            xml += "</param>\n";
        }
  
        xml += "</params>\n";
        xml += "</methodCall>";
  
        return xml; // for now
    }

    static function dataTypeOf (o){
        // identifies the data type
        if ( o == null) {
            return "string";
        }
        var type = typeof(o);
        type = type.toLowerCase();
        switch(type){
          case "number":
            if (Math.round(o) == o) type = "i4";
            else type = "double";
            break;
          case "object":
            // Number and Date have the same prototype
            if ( o is  LzRPCDoubleWrapper ) {
                type = "doublewrapper";
            } else if ( o is Date && o.getMilliseconds != null ) {
                type = "date";
            } else if ( o is Number ) {
                if (Math.round(o) == o) type = "i4";
                else type = "double";
            } else if ( o is Array ) {
                type = "array";
            } else {
                type = "struct";
            }

            break;
        }
        return type;
    }


    static function doValueXML(type,data){
        var xml = "<" + type + ">" + ((typeof(data) == 'string') ? lz.Browser.xmlEscape(data) : data )+ "</" + type + ">";
        return xml;
    }

    static function doDoubleWrapperXML(data){
        var xml = "<double>" + data.num + "</double>";
        return xml;
    }

    static function doBooleanXML(data){
        var value = (data==true)?1:0;
        var xml = "<boolean>" + value + "</boolean>";
        return xml;
    }

    static function doDateXML(data){
        var xml = "<dateTime.iso8601>";
        xml += XMLRPCMessage.dateToISO8601(data);
        xml += "</dateTime.iso8601>";
        return xml;
    }

    static function doArrayXML(data){
        var xml = "<array><data>\n";
        for (var i = 0; i < data.length; i++){
            xml += "<value>" + XMLRPCMessage.getParamXML(XMLRPCMessage.dataTypeOf(data[i]),data[i]) + "</value>\n";
        }
        xml += "</data></array>\n";
        return xml;
    }

    static function doStructXML(data){
        var xml = "<struct>\n";
        for (var i in data){
            xml += "<member>\n";
            xml += "<name>" + i + "</name>\n";
            xml += "<value>" + XMLRPCMessage.getParamXML(XMLRPCMessage.dataTypeOf(data[i]),data[i]) + "</value>\n";
            xml += "</member>\n";
        }
        xml += "</struct>\n";
        return xml;
    }

    static function getParamXML(type,data){
        var xml;
        switch (type){
          case "date":
            xml = XMLRPCMessage.doDateXML(data);
            break;
          case "array":
            xml = XMLRPCMessage.doArrayXML(data);
            break;
          case "struct":
            xml = XMLRPCMessage.doStructXML(data);
            break;
          case "boolean":
            xml = XMLRPCMessage.doBooleanXML(data);
            break;
          case "doublewrapper":
            xml = XMLRPCMessage.doDoubleWrapperXML(data);
            break;
          default:
            xml = XMLRPCMessage.doValueXML(type,data);
            break;
        }
        return xml;
    }

    static function dateToISO8601(date){
        // wow I hate working with the Date object
        var year = new String(date.getFullYear());
        var month = XMLRPCMessage.leadingZero(new String(date.getMonth()+1));
        var day = XMLRPCMessage.leadingZero(new String(date.getDate()));
        var time = XMLRPCMessage.leadingZero(new String(date.getHours())) + ":" +
                               XMLRPCMessage.leadingZero(new String(date.getMinutes())) + ":" +
                               XMLRPCMessage.leadingZero(new String(date.getSeconds()));

        var converted = year+month+day+"T"+time;
        return converted;
    } 
  
    static function leadingZero(n){
        // pads a single number with a leading zero. Heh.
        if (n.length==1) n = "0" + n;
        return n;
    }
}
]]>
</script>
</library>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->
