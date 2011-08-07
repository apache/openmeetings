package org.openmeetings.backup;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

public class TestBackupString {

	
	@Test
	public void stringReplace() {
		
		String str = "<![CDATA[alvaro&#3;@gmail.com]]>";
		
		System.out.println("str "+str);
		
		String result = this.unformatString(str);
		
		System.out.println("result "+result);
		
		assertNotSame(str, result);
		
	}
	
	private String unformatString(String str) {
		str = str.replaceAll(Pattern.quote("<![CDATA["),"");
		str = str.replaceAll(Pattern.quote("]]>"),"");
		return str;
	}

}
