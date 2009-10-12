<library>
<script when="immediate">
<![CDATA[

/* LZ_COPYRIGHT_BEGIN */
/****************************************************************************
 * Copyright (c) 2001-2008 Laszlo Systems, Inc.  All Rights Reserved.       *
 * Use is subject to license terms                                          *
 ****************************************************************************/
/* LZ_COPYRIGHT_END */

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

//======================================================================
// DEFINE OBJECT: LzNamespace
//======================================================================

dynamic class LzNamespace {

var namespace:String;

    function LzNamespace (namespace) {
    this.namespace = namespace;
    }

//------------------------------------------------------------------------------
//
//------------------------------------------------------------------------------
function toString () {
    return "LzNamespace " + this.namespace;
}

//------------------------------------------------------------------------------
// @param ctqn: a complex type QName.
// @return prototype of complex type, if one exists, else null.
//------------------------------------------------------------------------------
static function getType  (ctqn) {
    var ns = LzNamespace.ns[ctqn.getNamespaceURI()];
    return ns ? ns[ctqn.getLocalPart()] : null;
}

public static var ns = {};

//------------------------------------------------------------------------------
// Statics from org.apache.axis.Constants.
//------------------------------------------------------------------------------

// SOAP encoding namespace URIs
public static var URI_SOAP11_ENC = "http://schemas.xmlsoap.org/soap/encoding/";
public static var URI_SOAP12_ENC = "http://www.w3.org/2002/12/soap-encoding";
public static var URI_SOAP12_NOENC =
    "http://www.w3.org/2002/12/soap-envelope/encoding/none";
public static var URI_DEFAULT_SOAP_ENC = "http://schemas.xmlsoap.org/soap/encoding/";

// SOAP envelope namespace URIs
public static var URI_SOAP11_ENV = "http://schemas.xmlsoap.org/soap/envelope/" ;
public static var URI_SOAP12_ENV = "http://www.w3.org/2002/12/soap-envelope";
public static var URI_DEFAULT_SOAP_ENV = "http://schemas.xmlsoap.org/soap/envelope/" ;

// Schema definition namespace URIs
public static var URI_1999_SCHEMA_XSD = "http://www.w3.org/1999/XMLSchema";
public static var URI_2000_SCHEMA_XSD = "http://www.w3.org/2000/10/XMLSchema";
public static var URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
public static var URI_DEFAULT_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

public static var URIS_SCHEMA_XSD = 
    [ "http://www.w3.org/2001/XMLSchema",
      "http://www.w3.org/2000/XMLSchema",
      "http://www.w3.org/1999/XMLSchema"];


// Schema instance namespace URIs
public static var URI_1999_SCHEMA_XSI = "http://www.w3.org/1999/XMLSchema-instance";
public static var URI_2000_SCHEMA_XSI =
    "http://www.w3.org/2000/10/XMLSchema-instance";
public static var URI_2001_SCHEMA_XSI = "http://www.w3.org/2001/XMLSchema-instance";
public static var URI_DEFAULT_SCHEMA_XSI = "http://www.w3.org/2001/XMLSchema-instance";

public static var URIS_SCHEMA_XSI =
    [ "http://www.w3.org/1999/XMLSchema-instance",
      "http://www.w3.org/2000/10/XMLSchema-instance",
      "http://www.w3.org/2001/10/XMLSchema-instance"];
}



]]>
</script>
</library>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->
