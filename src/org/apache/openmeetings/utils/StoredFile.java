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
package org.apache.openmeetings.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StoredFile {
    private static final Set<String> convertExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "ppt", "odp", "odt", "sxw", "wpd",
                    "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx",
                    "docx", "xlsx" }));

    private static final Set<String> pdfExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "pdf", "ps" }));

    private static final Set<String> imageExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "png", "gif", "svg", "dpx", "exr",
                    "pcd", // PhotoCD
                    "pcds", // PhotoCD
                    "psd", // Adobe Photoshop
                    "tiff", // Tagged Image File Format
                    "ttf", // TrueType font
                    "xcf", // GIMP image
                    "wpg", // Word Perfect Graphics
                    "bmp", "ico", // Microsoft Icon
                    "tga", // Truevision Targa
                    "jpg", "jpeg" }));

    private static final Set<String> chartExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "xchart" }));

    private static final Set<String> videoExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "avi", "mov", "flv", "mp4" }));

    private static final Set<String> asIsExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "jpg", "xchart" }));

    private final String name;
	private final String ext;

    public StoredFile(String name, String ext) {
        this.name = name;
        this.ext = ext;
    }
    
    public static String[] getExtensions(){
    	
    	Set<String> extensions = new HashSet<String>();
    	extensions.addAll(convertExtensions);
    	extensions.addAll(pdfExtensions);
    	extensions.addAll(imageExtensions);
    	extensions.addAll(chartExtensions);
    	extensions.addAll(videoExtensions);
    	extensions.addAll(asIsExtensions);
    	
    	Object[] returnObj = extensions.toArray();
    	
    	String[] returnStr = new String[returnObj.length];
    	
    	int i=0;
    	for (Object obj : returnObj) {
    		returnStr[i] = obj.toString();
    		i++;
    	}
    	
    	return returnStr;
    }

    public boolean isConvertable() {
        return convertExtensions.contains(ext);
    }

    public boolean isPresentation() {
        return isConvertable() || isPdf();
    }

    public boolean isPdf() {
        return pdfExtensions.contains(ext);
    }

    public boolean isImage() {
        return imageExtensions.contains(ext);
    }

    public boolean isVideo() {
        return videoExtensions.contains(ext);
    }

    public boolean isChart() {
        return chartExtensions.contains(ext);
    }

    public boolean isAsIs() {
        return asIsExtensions.contains(ext);
    }

    public String getName() {
		return name;
	}
}
