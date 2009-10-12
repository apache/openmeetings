package org.openmeetings.utils.stringhandlers;

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
