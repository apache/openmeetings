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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Class to cast any LinkedHashMap to its JavaBean repraesentant
 * the idiom is that the attribute name in the LinkedHashMap is the same as in the JavaBean/Pojo
 * 
 * if the attribute's of the Bean are private (meaning it IS a Bean) then it will use the getters and setters
 * if the attribute's are public it will assign directly
 * if the attribute is final it will show an error in log
 * 
 * if the HashMap contains an null for a primitive attribute it will not assign that value
 * 
 * if the HashMap contains subelments nested as LinkedHashMap's it will add these Sub-Elements to the Main-Object
 * for an exmaple see:
 * http://openmeetings.googlecode.com/svn/branches/dev/xmlcrm/java/src/test/org/xmlcrm/utils/TestReflectionApi.java
 * 
 * TODO:
 * If the Sub Item is not an Object but a Set (meaning a List of Object) this List must be 
 * cast to Objects of the Bean too
 * 
 * @author swagner
 * 
 *
 */

public class CastMapToObject {
	
	private static final Logger log = Red5LoggerFactory.getLogger(CastMapToObject.class, OpenmeetingsVariables.webAppRootKey);
	
	private CastMapToObject() {}

	private static CastMapToObject instance = null;

	public static synchronized CastMapToObject getInstance() {
		if (instance == null) {
			instance = new CastMapToObject();
		}
		return instance;
	}	
	
	public Object castByGivenObject(Map<String, ?> values, Class<?> targetClass){
		try {
//			if (valuesObj.getClass().getClass().getName().equals(ObjectMap.class.getName())){
//				ObjectMap values = (ObjectMap) valuesObj;
//			} else if (valuesObj.getClass().getClass().getName().equals(LinkedHashMap.class.getName())){
//				LinkedHashMap values = (LinkedHashMap) valuesObj;
//			}
			Object returnObject = targetClass.newInstance();
//			log.error("returnObject");
//			log.error(returnObject);
//			log.error( "class " + targetClass.getName() ); 
//			log.error (" number of declared fields: " + targetClass.getDeclaredFields().length );
			LinkedHashMap<String,LinkedHashMap<String,Object>> structuredMethodMap = StructureMethodList.getInstance().parseClassToMethodList(targetClass);
			
			for ( Field anyField : targetClass.getDeclaredFields() )  { 
				String fieldName = anyField.getName(); 
				Class<?> fieldType = anyField.getType();
				String fieldTypeName = anyField.getType().getName(); 

				if (this.compareTypeNameToBasicTypes(fieldTypeName)) {
					//log.info("Found Type: " + fieldName);
					//Get value from  set 
					Object t = values.get(fieldName);
					//log.info("fieldName Value: "+t);
					//log.info("fieldName Value: "+anyField.getModifiers());
					int mod = anyField.getModifiers();
					
					if (Modifier.isPrivate(mod) && !Modifier.isFinal(mod)){
						
						//log.info("is private so get setter method "+fieldName);
						LinkedHashMap<String,Object> methodSummery = structuredMethodMap.get(fieldName);
						
						if (methodSummery!=null) {
							if (methodSummery.get("setter")!=null) {
	
								String methodSetterName = methodSummery.get("setter").toString();
								Class<?>[] paramTypes = (Class[]) methodSummery.get("setterParamTypes");
								Method m = targetClass.getMethod(methodSetterName, paramTypes);
								
								Class<?> paramType = paramTypes[0];
								
								//try to cast the Given Object to the necessary Object
								if (t!=null && !paramType.getName().equals(t.getClass().getName())){
									for (Constructor<?> crt : paramType.getConstructors()) {
										if (crt.getParameterTypes()[0].getName().equals("java.lang.String")){
											t = crt.newInstance(t.toString());	
										}
									}
								}
								if (paramType.isPrimitive() && t==null){
									//cannot cast null to primitve
								} else {
									Object[] arguments = new Object[]{ t }; 
									m.invoke(returnObject,arguments);
								}
							
							} else {
								log.error("could not find a setter-method from Structured table. Is there a setter-method for " + fieldName + " in Class " + targetClass.getName());
							}
						} else {
							log.error("could not find a method from Structured table. Is there a method for " + fieldName + " in Class " + targetClass.getName());
						}
						
					} else if (Modifier.isPublic(mod) && !Modifier.isFinal(mod)){
						if (t!=null && !anyField.getType().getName().equals(t.getClass().getName())){
							for (Constructor<?> crt : anyField.getType().getConstructors()) {
								if (crt.getParameterTypes()[0].getName().equals("java.lang.String")){
									t = crt.newInstance(t.toString());
								}
							}

							//Is public attribute so set it directly
							anyField.set(returnObject, t);
						}
						
					} else if (Modifier.isFinal(mod)) {
						log.error("Final attributes cannot be changed ");
					} else {
						log.error("Unhandled Modifier Type: " + mod);
					}
					
				} else {
					
					//This will cast nested Object to the current Object
					//it does not matter how deep it is nested
					
//					log.error("fieldType "+fieldType.getName());
					
					//Check if the Attribute in the bean is a List
					if (fieldType.getName().equals("java.util.Set")) {
						
						//Todo: Cast Set to Object
						
//						log.error("compareBeanTypeToAllowedListTypes true " + fieldType.getName());
//						log.error("compareBeanTypeToAllowedListTypes true " + fieldName);
						
						Object valueOfHashMap = values.get(fieldName);
						
						if (valueOfHashMap!=null){
//							log.error("compareBeanTypeToAllowedListTypes true " + valueOfHashMap.getClass().getName());
							String valueTypeOfHashMap = valueOfHashMap.getClass().getName();
							
							if (this.compareTypeNameToAllowedListTypes(valueTypeOfHashMap)) {
								Map<?, ?> m = (Map<?, ?>) valueOfHashMap;
								for (Iterator<?> it = m.keySet().iterator();it.hasNext();) {
									String key = it.next().toString();
//									log.error("key: "+key);
									@SuppressWarnings("unused")
									Object listObject = m.get(key);
//									log.error("listObject: "+listObject);
//									log.error("listObject: "+listObject.getClass().getName());
									
								}
								
							}
						}
						
					//otherwise do it as Object
					} else {
						
//						log.error("otherwise do it as Object "+fieldType.getName());
					
						Object valueOfHashMap = values.get(fieldName);
						if (valueOfHashMap!=null){
							String valueTypeOfHashMap = valueOfHashMap.getClass().getName();
							
							if (this.compareTypeNameToAllowedListTypes(valueTypeOfHashMap)) {
								
								log.error(valueTypeOfHashMap);
								log.error(fieldType.getName());
								
								//Get value from  set 
								@SuppressWarnings("unchecked")
								Object t = this.castByGivenObject((Map<String, ?>)valueOfHashMap, fieldType);
								int mod = anyField.getModifiers();
								
								if (Modifier.isPrivate(mod) && !Modifier.isFinal(mod)){
									
									//log.info("is private so get setter method "+fieldName);
									LinkedHashMap<String,Object> methodSummery = structuredMethodMap.get(fieldName);
									
									if (methodSummery!=null) {
										if (methodSummery.get("setter")!=null) {
				
											String methodSetterName = methodSummery.get("setter").toString();
											Class<?>[] paramTypes = (Class[]) methodSummery.get("setterParamTypes");
											Method m = targetClass.getMethod(methodSetterName, paramTypes);
											
											Class<?> paramType = paramTypes[0];
											//log.error("paramType: "+paramType.getName());
											if (paramType.isPrimitive() && t==null){
												//cannot cast null to primitve
											} else {
												Object[] arguments = new Object[]{ t }; 
												m.invoke(returnObject,arguments);
											}
										
										} else {
											log.error("could not find a setter-method from Structured table. Is there a setter-method for " + fieldName + " in Class " + targetClass.getName());
										}
									} else {
										log.error("could not find a method from Structured table. Is there a method for " + fieldName + " in Class " + targetClass.getName());
									}
								} else if (Modifier.isPublic(mod) && !Modifier.isFinal(mod)){
									
									//Is public attribute so set it directly
									anyField.set(returnObject, t);
									
								} else if (Modifier.isFinal(mod)) {
									log.error("Final attributes cannot be changed ");
								} else {
									log.error("Unhandled Modifier Type: " + mod);
								}
								
							}
						} else {
							//There is no nested Object for that given
							log.error("There is no nested Object for that given: Attribute: " + fieldName + " Class " + targetClass.getName());
						}
					}
					
				} 
				
				
			} 

			return returnObject;
		} catch (Exception ex) {
			log.error("[castByGivenObject]: " ,ex);
		}
		return null;
	}
	
	private boolean compareTypeNameToBasicTypes(String fieldTypeName) {
		try {
			
			for (Iterator<String> it = CastBasicTypes.getCompareTypesSimple().iterator();it.hasNext();) {
				if (fieldTypeName.equals(it.next())) return true;
			}
			
			return false;
		} catch (Exception ex) {
			log.error("[compareTypeNameToBasicTypes]",ex);
			return false;
		}
	}
	
	private boolean compareTypeNameToAllowedListTypes(String fieldTypeName) {
		try {
			//log.error("compareTypeNameToAllowedListTypes"+ fieldTypeName);
			for (Iterator<String> it = CastBasicTypes.getAllowedListTypes().iterator();it.hasNext();) {
				if (fieldTypeName.equals(it.next())) return true;
			}
			
			return false;
		} catch (Exception ex) {
			log.error("[compareTypeNameToBasicTypes]",ex);
			return false;
		}
	}

}
