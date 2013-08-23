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
package org.apache.openmeetings.data.file.dto;

public class LibraryPresentation {

	private LibraryPresentationFile originalDocument = null;
	private LibraryPresentationFile pdfDocument = null;
	private LibraryPresentationFile swfDocument = null;
	private LibraryPresenationThumbs thumbs = null;
	
	public LibraryPresentationFile getOriginalDocument() {
		return originalDocument;
	}
	public void setOriginalDocument(LibraryPresentationFile originalDocument) {
		this.originalDocument = originalDocument;
	}
	public LibraryPresentationFile getPdfDocument() {
		return pdfDocument;
	}
	public void setPdfDocument(LibraryPresentationFile pdfDocument) {
		this.pdfDocument = pdfDocument;
	}
	public LibraryPresentationFile getSwfDocument() {
		return swfDocument;
	}
	public void setSwfDocument(LibraryPresentationFile swfDocument) {
		this.swfDocument = swfDocument;
	}
	public LibraryPresenationThumbs getThumbs() {
		return thumbs;
	}
	public void setThumbs(LibraryPresenationThumbs thumbs) {
		this.thumbs = thumbs;
	}
	
}
