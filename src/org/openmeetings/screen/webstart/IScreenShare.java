package org.openmeetings.screen.webstart;

import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.messaging.IMessage;
import org.red5.server.net.rtmp.INetStreamEventHandler;

public interface IScreenShare extends IPendingServiceCallback, INetStreamEventHandler {
	void invoke(String method, Object[] params, IPendingServiceCallback callback);
	void connect(String server, int port, String application, IPendingServiceCallback connectCallback);
	void disconnect();
	void createStream(IPendingServiceCallback callback);
	void publish(int streamId, String name, String mode, INetStreamEventHandler handler);
	void publishStreamData(int streamId, IMessage message);
}
