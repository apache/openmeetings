package org.openmeetings.app.data.file.dto;

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
