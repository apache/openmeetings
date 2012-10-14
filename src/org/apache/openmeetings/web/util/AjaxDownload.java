package org.apache.openmeetings.web.util;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * see: <a href="https://cwiki.apache.org/confluence/display/WICKET/AJAX+update+and+file+download+in+one+blow">
 * https://cwiki.apache.org/confluence/display/WICKET/AJAX+update+and+file+download+in+one+blow</href>
 * 
 */
public class AjaxDownload extends AbstractAjaxBehavior {

	private static final long serialVersionUID = 1L;

	private boolean addAntiCache;

	private String fileName;
	private IResourceStream resourceStream;

	public AjaxDownload() {
		this(true);
	}

	public AjaxDownload(boolean addAntiCache) {
		super();
		this.addAntiCache = addAntiCache;
	}

	/**
	 * Call this method to initiate the download.
	 */
	public void initiate(AjaxRequestTarget target) {
		String url = getCallbackUrl().toString();

		if (addAntiCache) {
			url = url + (url.contains("?") ? "&" : "?");
			url = url + "antiCache=" + System.currentTimeMillis();
		}

		// the timeout is needed to let Wicket release the channel
		target.appendJavaScript("setTimeout(\"window.location.href='" + url
				+ "'\", 100);");
	}

	public void onRequest() {
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(
				getResourceStream(), getFileName());
		handler.setContentDisposition(ContentDisposition.ATTACHMENT);
		getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(
				handler);
	}

	/**
	 * Override this method for a file name which will let the browser prompt
	 * with a save/open dialog.
	 * 
	 * @see ResourceStreamRequestTarget#getFileName()
	 */
	protected String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Hook method providing the actual resource stream.
	 */
	protected IResourceStream getResourceStream() {
		return resourceStream;

	}

	public void setResourceStream(IResourceStream resourceStream) {
		this.resourceStream = resourceStream;
	}

}
