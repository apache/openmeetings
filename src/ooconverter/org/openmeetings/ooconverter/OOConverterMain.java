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
package org.openmeetings.ooconverter;

public class OOConverterMain {

	/**
	 * 
	 * The OpenOffice conversion should always happen in a separated Java Task
	 * from the Application server
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		
		if (args.length != 2) {
			new Exception("Wrong number of arguments, requires input and output path");
		}
		
		String inputPath = args[0];
		String outputPath = args[1];

		String[] commandArgs = {
		// command line arguments
		};

		ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);

	}

}
