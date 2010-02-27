package org.openmeetings.app.sip.xmlrpc;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.ws.commons.serialize.CharSetXMLWriter;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.BaseXmlWriterFactory;
import org.apache.xmlrpc.serializer.XmlWriterFactory;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.xmlrpc.XmlRpcException;
import org.xml.sax.ContentHandler;



import org.apache.ws.commons.serialize.XMLWriter;
import org.apache.ws.commons.serialize.XMLWriterImpl;



public class OpenXGXmlWriterFactory implements XmlWriterFactory {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGXmlWriterFactory.class, "openmeetings");
	
	protected XMLWriter newXmlWriter() {
		return new OpenXgXmlWriter();
	}

	public ContentHandler getXmlWriter(XmlRpcStreamConfig pConfig, OutputStream pStream)
			throws XmlRpcException {
		XMLWriter xw = newXmlWriter();
		xw.setDeclarating(true);
		String enc = pConfig.getEncoding();
		if (enc == null) {
			enc = XmlRpcStreamConfig.UTF8_ENCODING;
		}
		xw.setEncoding(enc);
		xw.setIndenting(false);
		xw.setFlushing(true);
		
		log.debug(":: getXmlWriter ::");
		
		try {
			xw.setWriter(new BufferedWriter(new OutputStreamWriter(pStream, enc)));
		} catch (UnsupportedEncodingException e) {
			throw new XmlRpcException(0, "Unsupported encoding: " + enc + e);
		}
		return xw;
	}


}
