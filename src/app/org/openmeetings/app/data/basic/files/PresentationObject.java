package org.openmeetings.app.data.basic.files;

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
