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
package org.apache.openmeetings.utils.stringhandlers;

public class StringComparer {
	
	private static StringComparer instance = null;
	
	private StringComparer() {}
	
	public static synchronized StringComparer getInstance(){
		if (instance == null){
			instance = new StringComparer();
		}
		return instance;
	}
	
	public String compareForRealPaths(String inputString) throws Exception{

		String t = "";
		for (int i=0;i<inputString.length();i++){
			char c = inputString.charAt(i);
			if (compareChars(c)) {
				t += c;
			} else {
				t += "_";
			}
			
		}		
		return t;
	}
	
	private boolean compareChars(char inputChar){		
		return Character.isLetterOrDigit(inputChar);
	}

}
