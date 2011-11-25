package org.openlaszlo.schema;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.openlaszlo.schema.sample.MyView;
import org.openlaszlo.schema.sample.View;


public class GenerateSampleXsdByJAXB {

	public static void main(String... args) {
		new GenerateSampleXsdByJAXB();
	}
	
	public GenerateSampleXsdByJAXB() {
		
		try {
			
			final File baseDir = new File("./test/");

			class MySchemaOutputResolver extends SchemaOutputResolver {
			    public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
			        return new StreamResult(new File(baseDir,suggestedFileName));
			    }
			}
			JAXBContext context = JAXBContext.newInstance(View.class, MyView.class);
			
			context.generateSchema(new MySchemaOutputResolver());


		} catch (Exception err) {
			
			err.printStackTrace();
			
		}
	}
	
}
