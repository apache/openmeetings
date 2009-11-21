package org.openmeetings.server.socket;

import java.net.SocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerFrameCursorStatus;
import org.openmeetings.server.beans.ServerStatusBean;
import org.openmeetings.server.beans.ServerViewerRegisterBean;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerPacketMinaHandler extends IoHandlerAdapter {

	private static final Logger log = Red5LoggerFactory.getLogger(ServerPacketMinaHandler.class, "openmeetings");
	
    private ServerSocketMinaProcess server;

	public ServerPacketMinaHandler(ServerSocketMinaProcess server) {
		this.server = server;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		log.error("[exceptionCaught]"+cause.getMessage());
		
		cause.printStackTrace();

		session.close();

	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		log.debug("messageReceived"+message.getClass().getName());
		
		if (message instanceof ServerFrameBean) {
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			server.recvServerFrameBean(remoteAddress, (ServerFrameBean) message);
			
		}
		
		if (message instanceof ServerStatusBean) {
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			server.recvServerStatusBean(remoteAddress, session.getId(), (ServerStatusBean) message);
			
		}
		
		if (message instanceof ServerViewerRegisterBean) {
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			server.recvServerViewerRegisterBean(remoteAddress, session.getId(), (ServerViewerRegisterBean) message);
			
		}
		
		if (message instanceof ServerFrameCursorStatus) {
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			//log.debug("incoming ServerFrameCursorStatus");
			server.updateClientCursor(remoteAddress, (ServerFrameCursorStatus) message);
			
		}
		
		if (message instanceof IoBuffer) {
			
			throw new Exception("Received Plain Buffer ?!");
			
//			//ByteBuffer buffer = ByteBuffer.allocate( bytes.length, false ); 
//			
//			IoBuffer buffer = (IoBuffer) message;
//			
//			//buffer.flip();
//			
//			log.debug("Incoming Receiving Buffer Length -1- " +buffer.array().length);
//			log.debug("Incoming Receiving Buffer Length -2- " +buffer.buf().array().length);
//
//			
//			SocketAddress remoteAddress = session.getRemoteAddress();
//
//			server.recvUpdate(remoteAddress, buffer.array());

		}

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

		log.debug("Session closed...");

		SocketAddress remoteAddress = session.getRemoteAddress();

		server.removeClient(remoteAddress, session.getId());

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {

		log.debug("Session created... ################ ");

//		session.getConfig().setMinReadBufferSize(49152);
//		session.getConfig().setMaxReadBufferSize(65535);
//		session.getConfig().setReadBufferSize(49152);
		
//		
//		if (session.getConfig() instanceof SocketSessionConfig) {
//			
//			log.debug("CONFIG SET TO new Buffer Size ... ################ ");
//			
//            ((SocketSessionConfig) session.getConfig()).setReceiveBufferSize(49152);
//            
//            
//        }
		
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		
		SocketAddress remoteAddress = session.getRemoteAddress();

		server.addClient(remoteAddress);
		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

		log.debug("Session idle...");

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {

		log.debug("Session Opened...");

	}
}
