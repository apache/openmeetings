package org.openmeetings.utils.mappings;

import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.apache.commons.lang.StringUtils;

public class StructureMethodList {
	
	private static final Logger log = Red5LoggerFactory.getLogger(StructureMethodList.class, ScopeApplicationAdapter.webAppRootKey);
	
	private StructureMethodList() {}

	private static StructureMethodList instance = null;

	public static synchronized StructureMethodList getInstance() {
		if (instance == null) {
			instance = new StructureMethodList();
		}
		return instance;
	}	
	
	/*
	 * 
	 */

	public LinkedHashMap<String,LinkedHashMap<String,Object>> parseClassToMethodList(Class targetClass){
		try {
			LinkedHashMap<String,LinkedHashMap<String,Object>> returnMap = new LinkedHashMap<String,LinkedHashMap<String,Object>>();
			
			for (Field field : targetClass.getDeclaredFields()) {
				String fieldName = field.getName();
				Class fieldTypeClass = field.getType();
				//log.error("fieldTypeClass Name " + fieldTypeClass.getName() );
				String capitalizedFieldName = StringUtils.capitalize(fieldName);
				String setterPre = "set";
				
				for (Method method : targetClass.getMethods()) {
					// check that method is declared in current class. 
					if(method.getName().equals(setterPre + capitalizedFieldName)){
						String methodName = method.getName();
						
						Class[] paramTypes = method.getParameterTypes();
						//log.error("parseClassToMethodList methodName: "+methodName);
						if (methodName.startsWith("set")) {
							//Found setter get Attribute name
							if (returnMap.get(fieldName)!=null) {
								LinkedHashMap<String,Object> methodListMap = returnMap.get(fieldName);
								methodListMap.put("setter", methodName);
								methodListMap.put("setterParamTypes", paramTypes);
							} else {
								LinkedHashMap<String,Object> methodListMap = new LinkedHashMap<String,Object>();
								methodListMap.put("setter", methodName);
								returnMap.put(fieldName, methodListMap);
								methodListMap.put("setterParamTypes", paramTypes);
							}
						} else if (methodName.startsWith("is")) {
							//Found setter(boolean) get Attribute name
							if (returnMap.get(fieldName)!=null) {
								LinkedHashMap<String,Object> methodListMap = returnMap.get(fieldName);
								methodListMap.put("getter", methodName);
							} else {
								LinkedHashMap<String,Object> methodListMap = new LinkedHashMap<String,Object>();
								methodListMap.put("getter", methodName);
								returnMap.put(fieldName, methodListMap);
							}
						} else if (methodName.startsWith("get")) {
							//Found setter(boolean) get Attribute name
							if (returnMap.get(fieldName)!=null) {
								LinkedHashMap<String,Object> methodListMap = returnMap.get(fieldName);
								methodListMap.put("getter", methodName);
							} else {
								LinkedHashMap<String,Object> methodListMap = new LinkedHashMap<String,Object>();
								methodListMap.put("getter", methodName);
								returnMap.put(fieldName, methodListMap);
							}
						}
						break;
					}
				}
				
				
			}			
			
			return returnMap;
		} catch (Exception ex) {
			log.error("[parseClassToMethodList]",ex);
			return new LinkedHashMap<String,LinkedHashMap<String,Object>>();
		}
		
	}
	
}
