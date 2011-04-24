<library>
<include href="rpc.js" />
<include href="qname.js" />
<script when="immediate">
<![CDATA[

/* LZ_COPYRIGHT_BEGIN */
/****************************************************************************
 * Copyright (c) 2001-2008 Laszlo Systems, Inc.  All Rights Reserved.       *
 * Use is subject to license terms                                          *
 ****************************************************************************/
/* LZ_COPYRIGHT_END */

//======================================================================
// DEFINE OBJECT: LzSOAP
//
// Implements an object to make remote java direct and XMLRPC calls.
//======================================================================
class LzSOAP extends LzRPC {


//------------------------------------------------------------------------------
// Map to store object references. Used by server to place references to other
// objects. For example, an object may contain two arrays, where objects in one
// array are the same objects as the other (multiRef).
//
// @keywords private
//------------------------------------------------------------------------------
var _m = {}

//------------------------------------------------------------------------------
// Create a SOAP object and return an object through delegate.
//
// @param LzDelegate delegate: delegate to call when object is returned.
//
// @param Object opts:
//      String wsdl: href for WSDL describing SOAP service. 
//      String service: SOAP service to use. If null, use first service
// encountered in WSDL.
//      String port: SOAP port to use. If null, use first SOAP port
// encountered in SOAP service
//
// @param Boolean secure: if true, make secure connection between client and the
// LPS. Default: false.
// @param Integer secureport: port to use for secure connection between client
// and the LPS. This is ignored if secure is false.
// @return sequence number of request to load object.
// ------------------------------------------------------------------------------
function loadObject (delegate, opts, secure, secureport){

    var dreq = new LzSOAPDataRequest(this);
    
    var service = opts['service']
    var port = opts['port']

    // We want treat response as JSON, not XML
    dreq.parsexml = false;
    dreq.method = "GET";
    var params:* = {};
    // Add in LPS Server proxy javarpc-protocol-specific query args
    dreq.serverproxyargs = params;

    params.request = 'load';
    params.wsdl = opts['wsdl'];
    params.url = 'soap://soap';

    var service = opts['service']
    var port = opts['port']
    if (service != null && service != "") params['service'] = service;
    if (port != null && port != "") params['port'] = port;

    dreq.opinfo = {opstyle: 'rpc'};

    dreq.proxied = true;
    dreq.src = 'soap://soap';
    return this.request( dreq, delegate, secure, secureport );
}


//------------------------------------------------------------------------------
// invoke RPC style function
// @param LzDelegate delegate: delegate to call for returned value.
// @param Array args: array of parameter values.
// @param String header: header to pass with request.
// @param Array opts: 
//      String wsdl: WSDL for SOAP service.
//      String service: name of SOAP service to use.
//      String port: name of SOAP port.
//      String operation: name of operation to invoke.
//      Array parts: array of LzQName representing type of each arg.
// @param Boolean secure: if true, call between client and LPS will be secure.
// @param Number secureport: secure port to use for invocation, if secure is
// true.
// @return sequence number of request.
//------------------------------------------------------------------------------
function invoke (delegate, args, header, opts, secure, secureport) {

    if ( ! (delegate instanceof LzDelegate) ) {
        Debug.write("ERROR: LzDelegate is required, got:", delegate);
        return;
    }

    var parts = opts['parts'];
    if (args.length != parts.length) {
        Debug.write("wrong number of parameters; need " + 
                          parts.length + ", passed in " + args.length);
        return;
    }

    var dreq:LzSOAPDataRequest = new LzSOAPDataRequest(this);
    // Response will be JSON, not XML
    dreq.parsexml = false;
    dreq.method = "POST";
    dreq.proxied = true;
    dreq.src     = 'soap://soap';
    var params:* = {}
    dreq.serverproxyargs = params;

    params.request   = 'invoke';
    params.wsdl      = opts['wsdl'];
    params.service   = opts['service'];
    params.port      = opts['port'];
    params.operation = opts['operation'];
    params.opstyle   = opts['opstyle'];


    var body;
    if (opts['opstyle'] == "rpc") {
        body = this.__LZencSerializeParams(args, parts);
    } else {
        body = this.__LZdocumentArgsToXML(args);
    }

    var h = '';
    if (typeof(header) == 'string') { 
        h = header;
    } else if ( header instanceof LzDataset  ) {
        h = this.__LZserialize(header.childNodes)
    }

    dreq.postbody = '<e><h>' + h + '</h><b>' + body + '</b></e>';

    // this information will be passed back up ondata
    dreq.opinfo = { operation: opts['operation'], opstyle: opts['opstyle'] }

    return this.request( dreq, delegate, secure, secureport );
}

//------------------------------------------------------------------------------
// @param Array elArray: an array of elements
//------------------------------------------------------------------------------
function __LZserialize (elArr) {
    var ser;
    for (var i=0; i < elArr.length; i++) {
        ser += elArr[i].serialize();
    }
    return ser;
}


//------------------------------------------------------------------------------
// Serialize parameters using encoding.
// @keywords private
//------------------------------------------------------------------------------
function __LZencSerializeParams (args, parts) {

    // FIXME [2005-05-15 pkang]: server should return default namespaces for xsi
    // and soapenc. can't assume these defaults will hold true for all services.
    var xsiNS = LzNamespace.URI_DEFAULT_SCHEMA_XSI;
    var soapencNS = LzNamespace.URI_DEFAULT_SOAP_ENC;

    // counter reference to use for namespaces
    var cr = [ 0 ];
    var xml = "<params xmlns:xsi=\"" + xsiNS
        + "\" xmlns:soapenc=\"" + soapencNS + "\">\n";

    for (var i=0; i < args.length; i++) {
        var qname = new LzQName(parts[i][1][0], parts[i][1][1]);
        xml += this.__LZencSerialize(args[i], parts[i][0], qname, cr);
    }
    xml += "</params>\n";
    return xml;
}


//------------------------------------------------------------------------------
// Serialize value using encoding.
//
// @param value: simple type value.
// @param element: element name.
// @param typeQ: LzQName for type of value.
// @param cr: counter reference for namespaces.
// @keywords private
//------------------------------------------------------------------------------
    function __LZencSerialize (value, element, typeQ:*, cr:Array) {

    if (typeQ is Array) {
        typeQ = new LzQName(typeQ[0], typeQ[1]);
    }
    var ct = LzNamespace.getType(typeQ);

    // _root.Debug.write('xxx', ct, typeQ, _root.LzQName.isSupported(typeQ));

    // if not an array or object
    if ( ! ct['arraytype'] && ! ct['members'] ) {
        // see if simple type is supported
        if (LzQName.isSupported(typeQ)) {
            return this.__LZencSerializeSimple(value, element, typeQ, cr);
        }
    }

    // function == null is true in javascript, so we check for type instead.
    // if we don't find a prototype, it means that this type doesn't exist.
    if (typeof(ct) != 'function') {
        Debug.write("skipping unsupported type", typeQ);
        return "";
    } 

    if (ct['arraytype'] != null) {
        return this.__LZencSerializeArray(value, element, ct.arraytype, cr);
    } else { // it's an object
        return this.__LZencSerializeStruct(value, element, typeQ, cr);
    }

}

//------------------------------------------------------------------------------
// Serialize a simple type value using encoding.
//
// @param simplevalue: simple type value.
// @param element: element name.
// @param typeQ: LzQName for type of value.
// @param cr: counter reference for namespaces.
// @keywords private
//------------------------------------------------------------------------------
    function __LZencSerializeSimple (simplevalue, element:String, typeQ:LzQName, cr:Array) {
   var ns = "ns" + (cr[0]++);
   var xml = "<" + element + " " + ns + ":type=\""
       + ns + ":" + typeQ.getLocalPart() + "\""
       + " xmlns:" + ns + "=\"" + typeQ.getNamespaceURI() + "\">"
       + lz.Browser.xmlEscape(simplevalue) + "</" + element + ">\n";
   return xml;
}


//------------------------------------------------------------------------------
// Serialize a struct using encoding.
//
// @param obj: object value.
// @param element: element name.
// @param typeQ: LzQName for type of value.
// @param cr: counter reference for namespaces.
// @keywords private
//------------------------------------------------------------------------------
function __LZencSerializeStruct (obj, element, typeQ, cr) {
    if (typeQ is Array) {
        typeQ = new LzQName(typeQ[0], typeQ[1]);
    }
    var ns = "ns" + (cr[0]++);
    var ct = LzNamespace.getType(typeQ);
    var members = ct.members;

    var typeLocal = typeQ.getLocalPart();
    var xml = "<" + element + " xsi:type=\"" + ns + ":" + typeLocal + "\"" 
        + " xmlns:" + ns + "=\"" + typeQ.getNamespaceURI() + "\">\n";
    for (var k in members) {
        if (typeof(obj[k]) != 'undefined') {
            xml += this.__LZencSerialize(obj[k], k, members[k], cr);
        }
    }
    xml += "</" + element + ">\n";

    return xml;
}


//------------------------------------------------------------------------------
// Serialize an array using encoding.
//
// @param arr: array value.
// @param element: element name.
// @param typeQ: LzQName for type of value.
// @param cr: counter reference for namespaces.
// @keywords private
//------------------------------------------------------------------------------
function __LZencSerializeArray (arr, element, typeQ, cr) {
// _root.Debug.write('xxx LZencSerializeArray');

    var ns = "ns" + (cr[0]++);
    var size = (arr == null ? 0 : arr.length);
    var xml = "<" + element + " xsi:type=\"soapenc:Array\" soapenc:arrayType=\"" 
        + ns + ":" + typeQ.getLocalPart() + "[" + size + "]\" xmlns:" 
        + ns + "=\"" + typeQ.getNamespaceURI() + "\">\n";

    for (var i=0; i < size; i++) {
// _root.Debug.write('    ', i, arr[i], typeQ, cr );
        xml += this.__LZencSerialize(arr[i], "item", typeQ, cr);
    }

    xml += "</" + element + ">\n";

    return xml;
}


//------------------------------------------------------------------------------
// Create XML for documents. Assume all args are XML strings.
// @keywords private
//------------------------------------------------------------------------------
function __LZdocumentArgsToXML (args) {
    var xml = "<params>";
    for (var i=0; i < args.length; i++) {
        var a = args[i];
        if ( a instanceof LzDataset ) {
            a = this.__LZserialize(a.childNodes);
        }
        xml += "<param>" + encodeURIComponent(a) + "</param>";
    }
    return xml += "</params>";
}

//------------------------------------------------------------------------------
// Create prototypes and add it to stubinfo
//
// @keywords private
//------------------------------------------------------------------------------
override function __LZloadHook (stubinfo) {

    // service object has been received
    //    var proto = this.__LZcreatePrototypes(data);

    var typeinfo = stubinfo.__LZctypes;

    if (typeinfo == null) return null;

    var nsMap = {};

    // Namespace stuff can be done more efficient, but don't have time :( -pk
    for (var ct in typeinfo) {
        var ti = typeinfo[ct];

        // WSDLs can have multiple schemas with different namespaces
        var nsURI = ti.ns;
        if ( LzNamespace.ns[nsURI] == null ) {
            LzNamespace.ns[nsURI] = new LzNamespace(nsURI);
        }
        var ns = LzNamespace.ns[nsURI];

        ns[ct] = function () {};
        ns[ct].name = ct; // name to give the function
        ns[ct].ns = ns;

        nsMap[nsURI] = ns;

        if (ti.type == "struct") {
            ns[ct].members = ti['members'];
        } else if (ti.type == "array") {
            ns[ct].arraytype = ti['typeQ'];
        }
    }

    // add proto to stubinfo
    stubinfo.proto = ns;
    stubinfo.protoMap = nsMap;
}


override function handleResponse (dreq:LzRPCDataRequest) {
    // The setup of the proxy object comes back as JSON
    if (dreq.rpcinfo.opinfo && dreq.rpcinfo.opinfo['opstyle'] =='rpc') {
        this.handleJSONRPCresponse(dreq);
    } else {
        // the response to a SOAP operation comes back as XML
        this.handleSOAPXMLresponse(dreq);
    }
}


function handleSOAPXMLresponse (dreq:LzRPCDataRequest) {
    var data:* = LzDataElement.stringToLzData(dreq.rawdata);
    var delegate = null;
    var opinfo   =  {};
    var seqnum   =  -1;

    var rpcinfo = dreq.rpcinfo;

    if (rpcinfo) {
        delegate = rpcinfo.delegate;
        opinfo   = (typeof rpcinfo['opinfo'] != "undefined" ) ? rpcinfo['opinfo'] : opinfo;
        seqnum   = rpcinfo.seqnum;
    } else {
        Debug.error('handleSOAPXMLresponse, no rpcinfo', dreq);
    }
 
    // TODO [hqm 2007-03-15] we need to pass these in if they in fact come from server
    opinfo.responseheaders = null;

    if (data && (data instanceof LzDataElement) && data.childNodes[0].nodeName == 'error') {
        var error = data.childNodes[0].attributes['msg'];
        // check if whitelist/blacklist is in effect
        {
            var check = 'Forbidden url: ';
            var index = error.indexOf(check);
            if (index != -1 && index == 0) {
                error = 'Forbidden: ' + error.substring(check.length);
            }
        }

        delegate.execute({ status: 'error', errortype: 'servererror',
                           message: error, opinfo: opinfo,
                           seqnum: seqnum });

    } else if (typeof(data) == "undefined") {
        delegate.execute({ status: 'error', errortype: 'timeout',
                           message: 'timed out', opinfo: opinfo,
                           seqnum: seqnum });

    } else {
        if (delegate['dataobject'] != null) {
            if ( delegate.dataobject instanceof LzDataset ) {
                var element = LzDataElement.valueToElement(data);
                // the child nodes of element will be placed in datasets childNodes
                delegate.dataobject.setAttribute("data", element.childNodes);
            } else if ( delegate.dataobject instanceof LzDataElement ) {
                var element = LzDataElement.valueToElement(data);
                // xpath: element/value
                delegate.dataobject.appendChild( element );
            } else {
                Debug.warn('dataobject is not LzDataset or LzDataElement:', 
                           delegate.dataobject);
            }
        }

        delegate.execute({ status: 'ok', message: 'ok', data: data, opinfo: opinfo,
                           seqnum: seqnum });
    }

}

//------------------------------------------------------------------------------
//The SOAP deserializer on the server will call this to tag arrays with a "item"
//property, used by LzDataElement.__LZv2E
//------------------------------------------------------------------------------
function __LZarray (arr, tag) {
    arr.__LZtag = tag;
    return arr;
}



}




// global SOAP service
var LzSOAPService:LzSOAP = new LzSOAP();

/*
 *  DataRequest which encodes a SOAP request, to be passed to the DataProvider
 */
class LzSOAPDataRequest extends LzRPCDataRequest {

    function LzSOAPDataRequest (requestor = null) { 
        super(requestor);
        this.protocol = LzRPC.SOAP_PROTOCOL;

    } 
    // soap fields
    // TODO hqm 2008-10 do we need any soap-specific stuff here
    // or is everything we need already in LzRPCDataRequest
}


]]>
</script>
</library>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->
