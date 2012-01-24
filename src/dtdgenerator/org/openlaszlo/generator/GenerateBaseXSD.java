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
package org.openlaszlo.generator;


public class GenerateBaseXSD extends GenerateSchema {
	
	public static void main(String... args) {
		
		GenerateBaseXSD generateBaseXSD = new GenerateBaseXSD();
		
		generateBaseXSD.getXsdUtil().setNameSpace("http://localhost/openlaszlo/lzx");
		generateBaseXSD.getXsdUtil().setXsdProjectPrefix("lzx");
		
		generateBaseXSD.scanFolder("openlaszlo/lps/");
		generateBaseXSD.scanFolder("test/core/lfc/");
		
		generateBaseXSD.printXsd("WebContent/lzx.xsd","test/core/static_simpleTypes.txt");
		
	}
	
}
