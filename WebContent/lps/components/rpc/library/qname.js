<library>
<include href="namespace.js" />
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

//=============================================================================
// DEFINE OBJECT: LzQName
//
// Create a qualified name object.
//
// @param local: local part of the LzQName.
// @param namespaceURI: namespace URI for the LzQName.
//==============================================================================
var QNAME_SUPPORTED_TYPES = {};

public class LzQName {

var __LZns;
var __LZlocal;

function LzQName(local, namespaceURI)
{
    this.__LZlocal = local;
    this.__LZns = namespaceURI;
    QNAME_SUPPORTED_TYPES[this.toString()] = true;
}

//-----------------------------------------------------------------------------
// String representation of this LzQName.
//-----------------------------------------------------------------------------
function toString () {
    return "LzQName {" + this.__LZns + "}" + this.__LZlocal;
}

//-----------------------------------------------------------------------------
// LzQName simple XSD type constants
//-----------------------------------------------------------------------------
    static var XSD_STRING;
    XSD_STRING =
    new LzQName("string", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_BOOLEAN;
    XSD_BOOLEAN =
    new LzQName("boolean", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_DOUBLE;
    XSD_DOUBLE =
    new LzQName("double", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_FLOAT;
    XSD_FLOAT =
    new LzQName("float", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_INT;
    XSD_INT =
    new LzQName("int", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_INTEGER;
    XSD_INTEGER =
    new LzQName("integer", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_LONG;
    XSD_LONG =
    new LzQName("long", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_SHORT;
    XSD_SHORT =
    new LzQName("short", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_BYTE;
    XSD_BYTE =
    new LzQName("byte", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_DECIMAL;
    XSD_DECIMAL =
    new LzQName("decimal", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_BASE64;
    XSD_BASE64 =
    new LzQName("base64Binary", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_HEXBIN;
    XSD_HEXBIN =
    new LzQName("hexBinary", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_ANYTYPE;
    XSD_ANYTYPE =
    new LzQName("anyType", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_ANY;
    XSD_ANY =
    new LzQName("any", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_QNAME;
    XSD_QNAME =
    new LzQName("LzQName", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_DATETIME;
    XSD_DATETIME =
    new LzQName("dateTime", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_DATE;
    XSD_DATE =
    new LzQName("date", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_TIME;
    XSD_TIME =
    new LzQName("time", LzNamespace.URI_DEFAULT_SCHEMA_XSD);
    static var XSD_TIMEINSTANT1999;
    XSD_TIMEINSTANT1999 =
    new LzQName("timeInstant", LzNamespace.URI_1999_SCHEMA_XSD);
    static var XSD_TIMEINSTANT2000;
    XSD_TIMEINSTANT2000 =
    new LzQName("timeInstant", LzNamespace.URI_2000_SCHEMA_XSD);

    static var XSD_NORMALIZEDSTRING;
    XSD_NORMALIZEDSTRING = 
    new LzQName("normalizedString", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_TOKEN;
    XSD_TOKEN = 
    new LzQName("token", LzNamespace.URI_2001_SCHEMA_XSD);

    static var XSD_UNSIGNEDLONG;
    XSD_UNSIGNEDLONG = 
    new LzQName("unsignedLong", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_UNSIGNEDINT;
    XSD_UNSIGNEDINT = 
    new LzQName("unsignedInt", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_UNSIGNEDSHORT;
    XSD_UNSIGNEDSHORT = 
    new LzQName("unsignedShort", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_UNSIGNEDBYTE;
    XSD_UNSIGNEDBYTE = 
    new LzQName("unsignedByte", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_POSITIVEINTEGER;
    XSD_POSITIVEINTEGER = 
    new LzQName("positiveInteger", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NEGATIVEINTEGER;
    XSD_NEGATIVEINTEGER = 
    new LzQName("negativeInteger", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NONNEGATIVEINTEGER;
    XSD_NONNEGATIVEINTEGER = 
    new LzQName("nonNegativeInteger", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NONPOSITIVEINTEGER;
    XSD_NONPOSITIVEINTEGER = 
    new LzQName("nonPositiveInteger", LzNamespace.URI_2001_SCHEMA_XSD);

    static var XSD_YEARMONTH;
    XSD_YEARMONTH = 
    new LzQName("gYearMonth", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_MONTHDAY;
    XSD_MONTHDAY = 
    new LzQName("gMonthDay", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_YEAR;
    XSD_YEAR = 
    new LzQName("gYear", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_MONTH;
    XSD_MONTH = 
    new LzQName("gMonth", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_DAY;
    XSD_DAY = 
    new LzQName("gDay", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_DURATION;
    XSD_DURATION = 
    new LzQName("duration", LzNamespace.URI_2001_SCHEMA_XSD);

    static var XSD_NAME;
    XSD_NAME = 
    new LzQName("Name", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NCNAME;
    XSD_NCNAME = 
    new LzQName("NCName", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NMTOKEN;
    XSD_NMTOKEN = 
    new LzQName("NMTOKEN", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NMTOKENS;
    XSD_NMTOKENS = 
    new LzQName("NMTOKENS", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_NOTATION;
    XSD_NOTATION = 
    new LzQName("NOTATION", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_ENTITY;
    XSD_ENTITY = 
    new LzQName("ENTITY", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_ENTITIES;
    XSD_ENTITIES = 
    new LzQName("ENTITIES", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_IDREF;
    XSD_IDREF = 
    new LzQName("IDREF", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_IDREFS;
    XSD_IDREFS = 
    new LzQName("IDREFS", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_ANYURI;
    XSD_ANYURI = 
    new LzQName("anyURI", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_LANGUAGE;
    XSD_LANGUAGE = 
    new LzQName("language", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_ID;
    XSD_ID = 
    new LzQName("ID", LzNamespace.URI_2001_SCHEMA_XSD);
    static var XSD_SCHEMA;
    XSD_SCHEMA = 
    new LzQName("schema", LzNamespace.URI_2001_SCHEMA_XSD);

//-----------------------------------------------------------------------------
// LzQName SOAP encoding constants
//-----------------------------------------------------------------------------
    static var SOAP_BASE64;
    SOAP_BASE64 = 
    new LzQName("base64", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_BASE64BINARY;
    SOAP_BASE64BINARY = 
    new LzQName("base64Binary", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_STRING;
    SOAP_STRING = 
    new LzQName("string", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_BOOLEAN;
    SOAP_BOOLEAN = 
    new LzQName("boolean", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_DOUBLE;
    SOAP_DOUBLE = 
    new LzQName("double", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_FLOAT;
    SOAP_FLOAT = 
    new LzQName("float", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_INT;
    SOAP_INT = 
    new LzQName("int", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_LONG;
    SOAP_LONG = 
    new LzQName("long", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_SHORT;
    SOAP_SHORT = 
    new LzQName("short", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_BYTE;
    SOAP_BYTE = 
    new LzQName("byte", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_INTEGER;
    SOAP_INTEGER = 
    new LzQName("integer", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_DECIMAL;
    SOAP_DECIMAL = 
    new LzQName("decimal", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_ARRAY;
    SOAP_ARRAY = 
    new LzQName("Array", LzNamespace.URI_DEFAULT_SOAP_ENC);
    static var SOAP_ARRAY12;
    SOAP_ARRAY12 = 
    new LzQName("Array", LzNamespace.URI_SOAP12_ENC);

//------------------------------------------------------------------------------
// Get the local part of this LzQName.
//------------------------------------------------------------------------------
function getLocalPart () {
    return this.__LZlocal;
}

//------------------------------------------------------------------------------
// Get the namespace for this LzQName.
//------------------------------------------------------------------------------
function getNamespaceURI () {
    return this.__LZns;
}

//------------------------------------------------------------------------------
// Compare this LzQName with another LzQName object.
// @param qname: LzQName to compare against.
// @return true if qame's local part and namespace URI are the same as this
// LzQName's.
//------------------------------------------------------------------------------
function equals (qname) {
    return this.__LZlocal == qname.__LZlocal &&
    this.__LZns == qname.__LZns;
}

//------------------------------------------------------------------------------
// Lifted from Apache AXIS org.apache.axis.Constants.equals(LzQName, LzQName).
//
// The first LzQName is the current version of the name. The second LzQName is
// compared with the first considering all namespace uri versions.
//
// @param first: currently supported LzQName
// @param second: any LzQName
//------------------------------------------------------------------------------
static function equals2 (first, second) {
    if (first == second) {
        return true;
    }
    if (first == null || second == null) {
        return false;
    }
    if (first.equals(second)) {
        return true;
    }
    if ( first.getLocalPart() != second.getLocalPart() ) {
        return false;
    }

    var namespaceURI = first.getNamespaceURI();
    var search = null;
    if (namespaceURI == LzNamespace.URI_DEFAULT_SOAP_ENC) {
        search = LzNamespace.URI_DEFAULT_SOAP_ENC;
    } else if (namespaceURI == LzNamespace.URI_DEFAULT_SOAP_ENV) {
        search = LzNamespace.URI_DEFAULT_SOAP_ENV;
    } else if (namespaceURI == LzNamespace.URI_DEFAULT_SCHEMA_XSD) {
        search = LzNamespace.URIS_SCHEMA_XSD;
    } else if (namespaceURI == LzNamespace.URI_DEFAULT_SCHEMA_XSI) {
        search = LzNamespace.URIS_SCHEMA_XSI;
    } else {
        search = [ namespaceURI ];
    }

    for (var i=0; i < search.length; i++) {
        if ( search[i] == second.getNamespaceURI() ) {
            return true;
        }
    }
    return false;
}


//------------------------------------------------------------------------------
// Check to see if qname is a supported primitive type. 
//
// @param LzQName qn: qname to check to see if it's a supported primitive type.
// @return true if supported, else false.
//------------------------------------------------------------------------------
static function isSupported (qn:LzQName) {
    return (!!QNAME_SUPPORTED_TYPES[qn.toString()]);
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
