package org.openmeetings.utils;

import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.openmeetings.utils.mappings.CastMapToObject;


public class TestReflectionApi extends TestCase {

	
	public TestReflectionApi(String testname){
		super(testname);
	}
	
	public void testReflectionApi(){
		try {
			LinkedHashMap values = new LinkedHashMap();
			values.put("attribute1", true);
			values.put("attribute2", null);
			values.put("attribute3", 1);
			values.put("attribute10", 1);
			values.put("attribute6", 1);
			values.put("attribute9", 1);
			values.put("attribute7", 1);
			values.put("attribute8", 1);
			values.put("mypublic6", 1);
			values.put("mypublic7", 1);
			values.put("attribute13", new java.util.Date());
			values.put("attribute12", "Stringeling");
			
			LinkedHashMap subBeanForReflection = new LinkedHashMap();
			subBeanForReflection.put("attribute12", "subBeanForReflection String");
			
			values.put("subBeanForReflection", subBeanForReflection);
			TestBeanForReflection myObject = (TestBeanForReflection) CastMapToObject.getInstance().castByGivenObject(values, TestBeanForReflection.class);
			
			System.out.println("testReflectionApi "+myObject);
			
			//test if cast has been successful
			System.out.println("attribute12: " + myObject.getAttribute12());
			System.out.println("attribute9: " + myObject.getAttribute9());
			System.out.println("attribute10: " + myObject.getAttribute10());
			System.out.println("attribute6: " + myObject.getAttribute6());
			System.out.println("attribute7: " + myObject.getAttribute7());
			System.out.println("attribute8: " + myObject.getAttribute8());
			System.out.println("mypublic6: " + myObject.getMypublic6());
			System.out.println("mypublic7: " + myObject.getMypublic7());
			System.out.println("subBeanForReflection attribute12: " + myObject.getSubBeanForReflection().getAttribute12());
			
			
		} catch (Exception ex) {
			System.out.println("TestBeanForReflection ex" + ex);
			ex.printStackTrace();
		}
	}

}
