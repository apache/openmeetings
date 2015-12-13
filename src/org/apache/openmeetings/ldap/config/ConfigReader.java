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
package org.apache.openmeetings.ldap.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;



/**
 * Reading ConfigFile (key=value pairs)
 * 
 * @author o.becherer
 *
 */
public class ConfigReader {
	
	/** Divider between key - value - pairs*/
	private String _divider = "=";
	
	/** Sign for comments */
	private String _commentSign = "#";
	
	/** FilePath*/
	private File _file;
	
	/** result hashmap */
	private HashMap<String, String> _configMap = new HashMap<String, String>();
	
	/** Timestamp, config was read*/
	private Timestamp _configRead;
	
	
	/**
	 * Defaultconstructor
	 */
	public ConfigReader(){
		
	}
	
	/**
	 * Constructor with params
	 * @param divider
	 * @param commentSign
	 */
	public ConfigReader(String comment, String divider){
		this._commentSign = comment;
		this._divider = divider;
	}
	
	
	/**
	 * Function for reading ConfigFile
	 * @throws Exception
	 */
	//---------------------------------------------------------------------------------
	public HashMap<String, String> readConfig(File config) throws Exception{
		
		_file = config;
		
		// Reading File into Vector
		Vector<String> contentVec = readFileIntoVector(config);
		
		// Filtering Comments
		contentVec = filterCommentsFromInput(contentVec);
		
		// Splitting data into hashmap
		splitLinesIntoHashMap(contentVec);
		
		_configRead = new Timestamp(System.currentTimeMillis());
		
		return getConfigMap();
		
	}
	//---------------------------------------------------------------------------------
	
	
	/**
	 * Filter Comments
	 * @author o.becherer
	 */
	//---------------------------------------------------------------------------------
	public Vector<String> filterCommentsFromInput(Vector<String> input){
		
		Vector<String> filtered = new Vector<String>(); 
		
		for(int i = 0; i < input.size(); i++){
			String val = input.get(i);
			
			if(val.startsWith(_commentSign))
				continue;
			
			filtered.add(val);
		}
		
		return filtered;
		
	}
	//---------------------------------------------------------------------------------
	
	
	/**
	 * Splitting lines into hashmap by divider
	 * @author o.becherer
	 */
	//---------------------------------------------------------------------------------
	public void splitLinesIntoHashMap(Vector<String> content) throws Exception{
		
		for(int i = 0; i < content.size(); i++){
			String line = content.get(i);
			
			if(line==null|| line.length() < 1)
				continue;
			
			if(!line.contains(_divider))
				throw new Exception("ConfigReader: ConfigFile " + _file + " contains invalid line(" + i+1 + ") : " + line + ". No Divider " + _divider + " found");
			
			String[] splitted =  line.split(_divider);
			
			String key = splitted[0];
			String val = splitted[1];
			
			_configMap.put(key, val);
		}
			
	}
	//---------------------------------------------------------------------------------
	
	/**
	 * getting ConfigVal for key
	 * @author o.becherer
	 */
	//---------------------------------------------------------------------------------
	public String getConfigVal(String key, boolean forcereload) throws Exception{
		
		String notFound = "Config " + _file + "( read at " + _configRead.toString() + ", Servertime) doesnt contain key '" + key + "'";
		
		if(!getConfigMap().containsKey(key)){
			if(!forcereload)
				throw new Exception(notFound);
			else{
				readConfig(_file);
				if(!getConfigMap().containsKey(key))
					throw new Exception(notFound);
			}
				
		
		}
		
		return getConfigMap().get(key);
	}
	//---------------------------------------------------------------------------------
	
	
	/**
	 * Dumping Config to stdout
	 * @author o.becherer
	 */
	//---------------------------------------------------------------------------------
	public void dumpConfig(){
		
		Iterator<String> miter = getConfigMap().keySet().iterator();
		
		System.out.println("---Dumping configFile '" + _file + "' without comments---");
		System.out.println("---------------------------------------------");
		while(miter.hasNext()){
			String key = miter.next();
			System.out.println(key + _divider + getConfigMap().get(key));
		}
		

		System.out.println("---------------------------------------------");
		
	}
	//---------------------------------------------------------------------------------
	
	
	/**
	 * reads File into Vector
	 * @author becherer
	 * @param filePath
	 */
	//--------------------------------------------------------------------------------------
	public static Vector<String> readFileIntoVector(File f) throws Exception{
		
		Vector<String> result = new Vector<String>();
		
		// File exists?
        if (!f.exists())
        	throw new Exception("Reader : File" + f.getCanonicalPath() + " not valid!");
       

        BufferedReader br = new BufferedReader(new FileReader(f));
        
        String line = "";
        
        while((line = br.readLine()) != null) {
        	result.add(line);
        }
        br.close();
        return result;
	}
	//--------------------------------------------------------------------------------------
	
	public String getDivider() {
		return _divider;
	}

	public void setDivider(String divider) {
		this._divider = divider;
	}

	public String getCommentSign() {
		return _commentSign;
	}

	public void setCommentSign(String commentSign) {
		this._commentSign = commentSign;
	}
	
	public HashMap<String, String> getConfigMap(){
		return _configMap;
	}

}
