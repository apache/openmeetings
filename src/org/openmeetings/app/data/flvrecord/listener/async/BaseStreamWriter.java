package org.openmeetings.app.data.flvrecord.listener.async;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITagWriter;
import org.red5.io.StreamableFileFactory;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.slf4j.Logger;

public abstract class BaseStreamWriter implements Runnable {

	private static final Logger log = Red5LoggerFactory.getLogger(
			BaseStreamWriter.class, OpenmeetingsVariables.webAppRootKey);

	// thread is running
	private boolean running = false;
	// thread is stopped
	private boolean stopping = false;
	// thread will be stopped as soon as the queue is empty
	private boolean dostopping = false;

	protected ITagWriter writer = null;

	protected Long flvRecordingMetaDataId = null;

	protected Date startedSessionTimeDate = null;

	protected File file;

	protected IScope scope;

	protected boolean isScreenData = false;

	protected String streamName = "";


	private final BlockingQueue<CachedEvent> queue = new LinkedBlockingQueue<CachedEvent>();

	public BaseStreamWriter(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData) {
		this.startedSessionTimeDate = new Date();
		this.isScreenData = isScreenData;
		this.streamName = streamName;
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
		this.scope = scope;
		try {
			init();
		} catch (IOException ex) {
			log.error("[StreamAudioWriter] Could not start Thread", ex);
		}
		open();
	}

	/**
	 * Initialization
	 * 
	 * @throws IOException
	 *             I/O exception
	 */
	private void init() throws IOException {

		File folder = new File(ScopeApplicationAdapter.webAppPath
				+ File.separatorChar + OpenmeetingsVariables.STREAMS_DIR + File.separatorChar
				+ this.scope.getName());

		if (!folder.exists()) {
			folder.mkdir();
		}

		String flvName = ScopeApplicationAdapter.webAppPath
				+ File.separatorChar + OpenmeetingsVariables.STREAMS_DIR + File.separatorChar
				+ this.scope.getName() + File.separatorChar + this.streamName
				+ ".flv";

		file = new File(flvName);

		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils
				.getScopeService(this.scope, IStreamableFileFactory.class,
						StreamableFileFactory.class);

		// File folder = file.getParentFile();

		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				throw new IOException("Could not create parent folder");
			}
		}

		if (!this.file.isFile()) {

			// Maybe the (previously existing) file has been deleted
			this.file.createNewFile();

		} else if (!file.canWrite()) {
			throw new IOException("The file is read-only");
		}

		IStreamableFileService service = factory.getService(this.file);
		IStreamableFile flv = service.getStreamableFile(this.file);
		this.writer = flv.getWriter();

	}

	private void open() {
		running = true;
		new Thread(this).start();
	}

	public void stop() {
		dostopping = true;
	}

	public void run() {
		while (!stopping) {
			try {
				CachedEvent item = queue.poll(100, TimeUnit.MICROSECONDS);
				if (item != null) {

					if (dostopping) {
						log.debug("Recording stopped but still packets to write to file!");
					}

					packetReceived(item);
				} else if (dostopping) {
					stopping = true;
					closeStream();
				}
			} catch (InterruptedException e) {
				log.error("[run]", e);
			}
		}
	}

	/**
	 * Write the actual packet data to the disk and do calculate any needed
	 * additional information
	 * 
	 * @param streampacket
	 */
	public abstract void packetReceived(CachedEvent streampacket);

	/**
	 * called when the stream is finished written on the disk
	 */
	public abstract void closeStream();

	public void append(CachedEvent streampacket) {
		if (!running) {
			throw new IllegalStateException(
					"Append called before the Thread was started!");
		}
		try {
			queue.put(streampacket);
		} catch (InterruptedException ignored) {
			log.error("[append]", ignored);
		}
	}

}
