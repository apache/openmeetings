package org.openlaszlo.schema;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.openlaszlo.generator.lfc.Canvas;
import org.openlaszlo.generator.lfc.View;
import org.openlaszlo.generator.lfc.Node;
import org.openlaszlo.generator.lfc.Eventable;


public class GenerateBaseXsdByJAXB {

	public static void main(String... args) {
		new GenerateBaseXsdByJAXB();
	}
	
	public GenerateBaseXsdByJAXB() {
		
		try {
			
			final File baseDir = new File("./test/");

			class MySchemaOutputResolver extends SchemaOutputResolver {
			    public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
			        
			    	suggestedFileName = "lzx_base.xsd";
			    	
			    	return new StreamResult(new File(baseDir,suggestedFileName));
			    }
			}
			
			JAXBContext context = JAXBContext.newInstance(Canvas.class, View.class, Node.class, Eventable.class);
			
			context.generateSchema(new MySchemaOutputResolver());


		} catch (Exception err) {
			
			err.printStackTrace();
			
		}
	}
	
}
