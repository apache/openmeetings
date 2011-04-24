<library>
<include href="xmlrpcmessage.js" />
<include href="rpc.js" />
<script when="immediate">
<![CDATA[


/* LZ_COPYRIGHT_BEGIN */
/****************************************************************************
 * Copyright (c) 2001-2008 Laszlo Systems, Inc.  All Rights Reserved.       *
 * Use is subject to license terms                                          *
 ****************************************************************************/
/* LZ_COPYRIGHT_END */

//======================================================================
// DEFINE OBJECT: LzXMLRPC
//
// Implements an object to make remote java direct and XMLRPC calls.
//======================================================================
class LzXMLRPC extends LzRPC {

//------------------------------------------------------------------------------
// Make remote request. Data will be the return value of the remote method
// call, e.g., string, integer, array, struct, etc.  Errors are returned like:
//
//     { status: STATUS, message: MESSAGE }
//
// where STATUS is one of 'ok' or 'error'.
//
// @param LzDelegate delegate: delegate to call when object is returned.
// @param String handler: can be a java classname or URL. If classname, does
// java direct. If it's an URL, opts is ignored.
// @param String methodname: name of method to invoke in server, like
// "CLASSNAME.METHOD".
// @param Object opts: options to add to request: 
//
//     opts['oname'] unique name of server object for invoking a session or
//         webapp object.
//     opts['scope'] scope of object; one of 'session', 'webapp', or 'static'.
//     opts['doreq'] if value is 1, adds http request to remote method call. 
//     opts['dores'] if value is 1, adds http response to remote method call, 
//
// @param Array args: array of argument parameters for remote function.
// @param Boolean secure: if true, make secure request.
// @param Integer secureport: port to use for secure request. This is ignored if
// secure is false.
// @return sequence number of request.
//------------------------------------------------------------------------------
function invoke (delegate, args, opts, secure, secureport) {

    if ( ! (delegate instanceof LzDelegate )) {
        Debug.write("ERROR: LzDelegate is required, got:", delegate);
        return;
    }

    var service = opts['service'];
    if (service == null) {
        Debug.write("ERROR: service not defined");
        return;
    }

    var index = service.indexOf("http:");
    if (index == -1 || index != 0) {
        Debug.write("xmlrpc.js ERROR: only HTTP XML-RPC services supported");
        return;
    }

    var handler = service;

    var methodname = opts['methodname'];
    var mesg = new XMLRPCMessage(methodname);
    for (var i=0; i < args.length; i++) {
        mesg.addParameter(args[i]);
    }

    var requestObj = new LzRPCDataRequest(this);

    requestObj.src = handler;
    requestObj.method = "POST";
    requestObj.protocol = LzRPC.XMLRPC_PROTOCOL;
    requestObj.requestheaders = new LzParam();
    requestObj.requestheaders.addValue('Content-Type', 'text/xml');
    
    requestObj.proxied = canvas.proxied;
    requestObj.timeout = canvas.dataloadtimeout;
    requestObj.postbody = mesg.xml();
    requestObj.opinfo = { service: service, methodname: methodname };

    return this.request( requestObj, delegate, secure, secureport ); 
}

}

var LzXMLRPCService:LzXMLRPC = new LzXMLRPC();

]]>
</script>
</library>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->
