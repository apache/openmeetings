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
package org.apache.openmeetings.utils.mappings;

import java.util.LinkedList;


public class CastBasicTypes {

	/**
	 * Returns all allowed Types which can be mapped directly to fields
	 * @return
	 */
	public static synchronized LinkedList<String> getCompareTypesSimple() {
		LinkedList<String> typeList = new LinkedList<String>();
		
		//Primitives
		typeList.add("String");
		typeList.add("byte");
		typeList.add("int");
		typeList.add("boolean");
		typeList.add("float");
		typeList.add("long");
		typeList.add("double");
		
		
		//Object
		typeList.add("java.lang.String");
		typeList.add("java.lang.Number");
		typeList.add("java.lang.Byte");
		typeList.add("java.lang.Integer");
		typeList.add("java.lang.Boolean");
		typeList.add("java.lang.Float");
		typeList.add("java.lang.Long");
		typeList.add("java.lang.Double");
		typeList.add("java.util.Date");
		
		return typeList;
	}
	
	/**
	 * Returns the List-Types which can be mapped to Objects
	 * @return
	 */
	public static synchronized LinkedList<String> getAllowedListTypes(){
		LinkedList<String> typeList = new LinkedList<String>();
		typeList.add("java.util.LinkedHashMap");
		typeList.add("java.util.HashMap");
		typeList.add("java.util.Map");
		typeList.add("org.red5.io.utils.ObjectMap");
		return typeList;
	}
	
	/**
	 * Returns the List-Types which won't be mapped
	 * LinkedHashSet is the type of Object if the Client send an Array with numeric index
	 * this kind of List cannot be mapped to an Object as it is not clear which field 
	 * is equivalent to which index-number
	 * @return
	 */
	public static synchronized LinkedList<String> getForbiddenListTypes(){
		LinkedList<String> typeList = new LinkedList<String>();
		typeList.add("java.util.LinkedHashSet");
		return typeList;
	}	
	
}
