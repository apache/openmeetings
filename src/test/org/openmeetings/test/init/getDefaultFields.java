package org.openmeetings.test.init;

import java.util.List;

import org.openmeetings.app.data.basic.Fieldmanagment;

import junit.framework.TestCase;

public class getDefaultFields extends TestCase {

	public getDefaultFields(String testname){
		super(testname);		
	}
	
	public void testGetDefaultField(){
		
		List ll = Fieldmanagment.getInstance().getAllFieldsByLanguage(new Long(1));
//		
//		System.out.println("testGetDefaultField: "+ll.size());
//		
//		for (Iterator it2 = ll.iterator(); it2.hasNext();) {
//			Fieldvalues f = (Fieldvalues) it2.next();
//			System.out.println("Fieldvalues: "+f.getName());
//			System.out.println("Fieldvalues size: "+f.getFieldlanguagesvalues().size());
//		}
//		
//		List ll2 = Fieldmanagment.getInstance().getAllFieldsByLanguage(1);
//		
//		for (Iterator it1 = ll2.iterator(); it1.hasNext();) {
//			Fieldlanguagesvalues f = (Fieldlanguagesvalues) it1.next();
//			System.out.println("Fieldlanguagesvalues: "+f.getValue());
//		}
//		
//		Fieldlanguagesvalues fl = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1, 1);
//		System.out.println("Fieldlanguagesvalues single: "+fl.getValue());	
//		
//		MainService mService = new MainService();
//		
//		List l = mService.getLanguages();
		
	}
	
}
