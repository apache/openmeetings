<library>
<include href="xmlrpcdecoder.lzx"/>
<script src="json.js"/>
<script when="immediate">
<![CDATA[


/* LZ_COPYRIGHT_BEGIN */
/****************************************************************************
 * Copyright (c) 2001-2009 Laszlo Systems, Inc.  All Rights Reserved.       *
 * Use is subject to license terms                                          *
 ****************************************************************************/
/* LZ_COPYRIGHT_END */

//======================================================================
// DEFINE OBJECT: LzRPC
//
// Implements an object to make remote java direct and XMLRPC calls.
//======================================================================
class LzRPC extends LzEventable {

    static const XMLRPC_PROTOCOL  = "xmlrpc";
    static const JAVARPC_PROTOCOL = "javarpc";
    static const SOAP_PROTOCOL    = "soap";


// object used for void return types
static var t_void = { type: 'void' };

// @keywords private
static var __LZloaderpool = [];

var dsloadDel:LzDelegate = null;

//------------------------------------------------------------------------------
// @param Number num: number to pass down to explicitly cast to a double
//------------------------------------------------------------------------------

    
static function DoubleWrapper (num) {
  return new LzRPCDoubleWrapper(num);
};
       

//------------------------------------------------------------
// Global RPC sequence number for server requests.
//------------------------------------------------------------
static var __LZseqnum = 0;


/*
 XMLRPC responses are of the format

      <methodResponse>
       <params>
          <param>
             <value><string>South Dakota</string></value>
             </param>
          </params>
       </methodResponse>

       <methodResponse>
           <fault>
              <value>
                 <struct>
                    <member>
                       <name>faultCode</name>
                       <value><int>4</int></value>
                       </member>
                    <member>
                       <name>faultString</name>
                       <value><string>Too many parameters.</string></value>
                       </member>
                    </struct>
                 </value>
              </fault>
        </methodResponse>
     */


/* 
   Dispatch on the protocl found in dreq.opinfo

   If it's XMLRPC , use the XMLRPC decoder

   otherwise, assume it's JSON

   */
function handleResponse (dreq:LzRPCDataRequest) {
    if (dreq.protocol == LzRPC.XMLRPC_PROTOCOL) {
        this.handleXMLRPCresponse(dreq);
    } else if (dreq.protocol == LzRPC.JAVARPC_PROTOCOL) {
        this.handleJSONRPCresponse(dreq);
    } else {
        Debug.error('LzRPC.handleResponse unknown protocol ', dreq.protocol);
    }
}


/**
   timeout of requests, in millseconds
*/

var timeout:Number = canvas.dataloadtimeout;

function setTimeout (msec) {
    this.timeout = msec;
}

function handleJSONRPCresponse (dreq:LzRPCDataRequest) {
    var data = JSON.parse(dreq.rawdata);
    var delegate = null;
    var opinfo   =  {};
    var seqnum   =  -1;

    var rpcinfo = dreq.rpcinfo;

    if (rpcinfo) {
        delegate = rpcinfo.delegate;
        opinfo   = (typeof rpcinfo['opinfo'] != "undefined" ) ? rpcinfo['opinfo'] : opinfo;
        seqnum   = rpcinfo.seqnum;
    } else {
        Debug.error('handleXMLRPCresponse, no rpcinfo', dreq);
    }

 
    // responseheaders is specific to SOAP -pk
    // opinfo.responseheaders = (responseheaders!=null ? responseheaders : null);


    // TODO [hqm 2007-03-15] we need to pass these in if they in fact come from server
    opinfo.responseheaders = null;

    if (data && typeof(data) == 'object' && data['__LZstubload'] ) {
        // check to see if the data is the stub 
        var stub = data.stub;
        var stubinfo = data.stubinfo;
        
        this.__LZloadHook(stubinfo);

        delegate.execute( { status: 'ok', message: 'ok', 
                    stub: stub, stubinfo: stubinfo,
                    seqnum: seqnum } );

    } else if (data && (data instanceof LzDataElement) && data.childNodes[0].nodeName == 'error') {
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

    } else if (data && typeof(data) == 'object' && data['faultCode'] != null) {
        // JavaRPC or XMLRPC error style
        // TODO: come up with a single way of returning RPC errors from server
        if (data.faultCode == 0 && data.faultString == 'void') {

            delegate.execute({ status: 'ok', message: 'void', 
                               data: LzRPC.t_void, opinfo: opinfo,
                               seqnum: seqnum });

        } else {

            delegate.execute({ status: 'error', 
                               errortype: 'fault',
                               message: data.faultString,
                               opinfo: opinfo,
                               error: data,
                               seqnum: seqnum });
        }

    } else if (data && typeof(data) == 'object' && data['errortype'] != null) {
        // SOAP error style
        // TODO: come up with a single way of returning RPC errors from server
        delegate.execute({ status: 'error', errortype: data.errortype,
                           message: data.faultstring, error: data,
                           opinfo: opinfo,
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
// This function is a callback handler which is passed to an
// LzHTTPLoader. It is called not as a method, but as an ordinary
// function (at least, in swf8 and dhtml)
//
// Therefore, don't try to reference 'this', it will probably not be
// what you expect.
//
// Override loader's returnData function. Call appropriate delegate.
// @keywords private
//------------------------------------------------------------------------------
    function handleXMLRPCresponse (dreq:LzRPCDataRequest) {
        var timeout = false;
        var unknownerror = false;
        var rpcerror = null;
        var data = null;

        var delegate = null;
        var opinfo   =  {};
        var seqnum   =  -1;
        var rpcinfo = dreq.rpcinfo;

        //Debug.write('rpcinfo = ', rpcinfo);
        if (rpcinfo) {
            delegate = rpcinfo.delegate;
            opinfo   = (typeof rpcinfo['opinfo'] != "undefined" ) ? rpcinfo['opinfo'] : opinfo;
            seqnum   = rpcinfo.seqnum;
        } else {
            Debug.error('handleXMLRPCresponse, no rpcinfo', dreq);
        }

        if (this.dsloadDel != null) {
            this.dsloadDel.unregisterFrom(dreq.onstatus);
        }
    
        // Check for low level loader failures
        if (dreq.status == LzDataRequest.ERROR) {
            unknownerror = true;
        } else if (dreq.status == LzDataRequest.TIMEOUT) {
            if (delegate) {
                delegate.execute({ status: 'error', errortype: 'timeout',
                            message: 'timed out', opinfo: opinfo,
                            seqnum: seqnum });
            }
            return;
        }


        //Debug.write("dreq.status", dreq.status);

        // Now examine the server response
        if (dreq.status == LzDataRequest.SUCCESS) {
            var xdata = dreq.xmldata;
            var headers = dreq.responseheaders;
            // If there is no XML data, try eval'ing the raw text to get JSON data
            if (xdata == null) {
                unknownerror = true;
            } else {
                //Debug.write('XDATA', xdata);
                // Parse XML response (see http://www.xmlrpc.com/spec)
                var methodresponse = xdata.childNodes[0];
                var valuenode = null;
                if (methodresponse.nodeName == "params") {
                    var params = methodresponse.childNodes[0];
                    var param = params.childNodes[0];
                    valuenode = param.childNodes[0];
                    data = LzXMLRPCDecoder.xmlrpc2jsobj(valuenode);
                } else if (methodresponse.nodeName == "fault") {
                    valuenode = methodresponse.childNodes[0];
                    rpcerror = LzXMLRPCDecoder.xmlrpc2jsobj(valuenode);
                }
            }
            if (typeof(data) == 'object' && data['__LZstubload'] ) {
                // check to see if the data is the stub 
                var stub = data.stub;
                var stubinfo = data.stubinfo;
        
                this.__LZloadHook(stubinfo);

                delegate.execute( { status: 'ok', message: 'ok', 
                            stub: stub, stubinfo: stubinfo,
                            seqnum: seqnum } );
                
            } else if (rpcerror != null && rpcerror['faultCode'] != null) {
                // TODO: [hqm 2008-07-22] SOAP-specific response handling
                // is not in here, we need to put it back someday.

                var error = rpcerror['faultString'];
                // check if whitelist/blacklist is in effect

                delegate.execute({ status: 'error',
                            errortype: 'servererror',
                            message: error, 
                            data: rpcerror,
                            opinfo: opinfo,
                            seqnum: seqnum });

            } else if (unknownerror) {
                delegate.execute({ status: 'error',
                            message: 'void', 
                            data: LzRPC.t_void,
                            opinfo: opinfo,
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
    }


// override this in soap implementation
function __LZloadHook (stubinfo:*) { }

//------------------------------------------------------------------------------
// @return sequence number of request.
// @keywords private
//------------------------------------------------------------------------------
    function request ( dreq:LzRPCDataRequest, delegate, secure, secureport ) {

    var seqnum = LzRPC.__LZseqnum++;

    // Info to use for later when the loader has gotten data from server
    dreq.rpcinfo = { delegate: delegate, 
                     opinfo:   dreq.opinfo, 
                     seqnum:   seqnum};

    dreq.opinfo = null;

    dreq.status  = LzDataRequest.READY;

    dreq.proxyurl = canvas.getProxyURL();

    dreq.timeout = this.timeout;
    dreq.queuerequests      = true;
    dreq.getresponseheaders = true;
    dreq.secureport = secureport;
    dreq.cacheable       =  false;
    dreq.clientcacheable =  false;
    dreq.trimwhitespace  =  false;
    dreq.nsprefix        =  true;

    // NB: You had better set the onstatus event handler *before* issuing request
    if (this.dsloadDel == null) {
        // TODO [hqm 2008-07-24] We may wish to pick a handler based
        // on the protocol (e.g., native SOAP).  Right now XMLRPC is
        // the default protocol.
        this.dsloadDel = new LzDelegate( this , "handleResponse" , dreq, "onstatus");
    } else {
        this.dsloadDel.register(dreq, "onstatus");
    }
    canvas.httpdataprovider.doRequest( dreq );

    return seqnum;
}

}

class LzRPCDataRequest extends LzHTTPDataRequest {

    function LzRPCDataRequest (requestor = null) { 
        super(requestor);
    } 

    // xmlrpc fields
    var protocol:String = LzRPC.XMLRPC_PROTOCOL;
    var opinfo:* = null;
    var rpcinfo:* = null;

}

class LzRPCDoubleWrapper {
    function LzRPCDoubleWrapper (n) {
        this.num = n;
    }
    var num;
}

]]>
</script>
</library>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->
