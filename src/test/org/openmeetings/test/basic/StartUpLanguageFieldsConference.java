package org.openmeetings.test.basic;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;

import junit.framework.TestCase;

import javax.xml.parsers.*; 

import org.w3c.dom.*; 
import java.io.*; 
import java.lang.Long;

public class StartUpLanguageFieldsConference extends TestCase {
	
	public StartUpLanguageFieldsConference(String testname){
		super(testname);
	}
	
	public static NodeList getLanguageXmlFile(String fileLanguage) throws Exception{
		File xml;
		
        try{
        	xml = new File("src/languages/" + fileLanguage + ".xml");
        }catch(Exception e){
	        throw e;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        DocumentBuilder construct = factory.newDocumentBuilder();
        Document document = construct.parse(xml);
        
        Element root = document.getDocumentElement();
        
        return root.getElementsByTagName("string");
        }
	
	public void testStartUpLanguageFieldsConference() throws Exception{
		String listLanguages[] = {"deutsch", "english", "french", "spanish"};
		NodeList nodeListLanguages[] = new NodeList[4];
		
		// TODO empty tables before launch unit test
		//Languagemanagement.getInstance().emptyFieldLanguage();
		
		/** Read all languages files */
		for (int i = 0; i < listLanguages.length ; i ++)
		{
			FieldLanguageDaoImpl.getInstance().addLanguage(listLanguages[i],false);
			nodeListLanguages[i] = getLanguageXmlFile(listLanguages[i]);
		}	
		
		/** Insert all languages strings into database 
		 * 
		 * This code can-not work 
		 * swagner 15.05.2007*/
//		for (int item_id = 0; item_id < nodeListLanguages[0].getLength(); item_id++)
//		{
//			Fieldmanagment.getInstance().addField(nodeListLanguages[0].item(item_id).getAttributes().getNamedItem("name").getNodeValue());
//		
//			for (int language_id = 0; language_id < listLanguages.length; language_id ++)
//			{
//				Fieldmanagment.getInstance().addFieldValueByFieldAndLanguage(Long.valueOf(nodeListLanguages[language_id].item(item_id).getAttributes().getNamedItem("id").getNodeValue()), (long) (language_id + 1), nodeListLanguages[language_id].item(item_id).getTextContent());
//			}	
//		}
	}
	

}
