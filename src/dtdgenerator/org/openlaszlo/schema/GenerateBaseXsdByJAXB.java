/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
