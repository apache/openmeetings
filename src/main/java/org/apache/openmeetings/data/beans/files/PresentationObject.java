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
package org.apache.openmeetings.data.beans.files;

import java.util.LinkedList;
/**
 * Represents a Parsed Version of a Library-File-Converted Object
 * @author sebastianwagner
 *
 */
public class PresentationObject {
	
	private FilesObject originalDocument;
	private FilesObject pdfDocument;
	private FilesObject swfDocument;
	LinkedList<FilesObject> thumbs;

	public FilesObject getOriginalDocument() {
		return originalDocument;
	}
	public void setOriginalDocument(FilesObject originalDocument) {
		this.originalDocument = originalDocument;
	}
	public FilesObject getPdfDocument() {
		return pdfDocument;
	}
	public void setPdfDocument(FilesObject pdfDocument) {
		this.pdfDocument = pdfDocument;
	}
	public FilesObject getSwfDocument() {
		return swfDocument;
	}
	public void setSwfDocument(FilesObject swfDocument) {
		this.swfDocument = swfDocument;
	}
	public LinkedList<FilesObject> getThumbs() {
		return thumbs;
	}
	public void setThumbs(LinkedList<FilesObject> thumbs) {
		this.thumbs = thumbs;
	}
	
	

}
