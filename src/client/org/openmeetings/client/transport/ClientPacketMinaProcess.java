package org.openmeetings.client.transport;

import org.apache.log4j.Logger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientCursorStatus;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.beans.ClientStatusBean;
import org.openmeetings.client.codec.ClientDesktopCodecSharingFactory;
import org.openmeetings.client.gui.ClientShowStatusMessage;
import org.openmeetings.client.gui.ClientStartScreen;
import org.openmeetings.client.gui.ClientViewerScreen;

/**
 * @author sebastianwagner
 *
 */
public class ClientPacketMinaProcess extends IoHandlerAdapter {
	
	private static Logger log = Logger.getLogger(ClientPacketMinaProcess.class);

    private IoSession session;
    //private NioDatagramConnector connector;
    private NioSocketConnector connector;
    
    public boolean isConnected = false;
    
    private int sessionOpenId = 0;
    
	public ClientPacketMinaProcess(int port, int sessionOpenId) {
		try {
			
			this.sessionOpenId = sessionOpenId;
			
			log.debug("UDPClient::UDPClient "+this.sessionOpenId);
			log.debug("Created a datagram connector");
			//connector = new NioDatagramConnector();
			connector = new NioSocketConnector();
			
//			connector.getSessionConfig().setMinReadBufferSize(8192);
//			connector.getSessionConfig().setMaxReadBufferSize(8192);
//			connector.getSessionConfig().setReadBufferSize(8192);
//			connector.getSessionConfig().setReceiveBufferSize(8192);
//			
			
			log.debug("Default Send Buffer Size "+connector.getSessionConfig().getSendBufferSize());
			
			connector.getSessionConfig().setSendBufferSize(8192*8);
			connector.getSessionConfig().setTcpNoDelay(true);
			
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientDesktopCodecSharingFactory()));
			
			//StreamWriteFilter.setWriteBufferSize
			//connector.getSessionConfig().set
			
			log.debug("Setting the handler");
			
			connector.setHandler(this);
			
			
			log.debug("About to connect to the server... HOST: "+ClientConnectionBean.host);
			log.debug("About to connect to the server... PORT: "+ClientConnectionBean.host);
			
			ConnectFuture connFuture = connector.connect(new InetSocketAddress(
													ClientConnectionBean.host, port));
			log.debug("About to wait.");
			connFuture.awaitUninterruptibly();
			log.debug("Adding a future listener.");
			connFuture.addListener(new IoFutureListener<ConnectFuture>() {
				public void operationComplete(ConnectFuture future) {
					if (future.isConnected()) {
						log.debug("...connected");
						session = future.getSession();
						ClientShowStatusMessage.showStatusMessage("Connected ... Ready for Action");
						isConnected = true;
					} else {
						ClientShowStatusMessage.showStatusMessage("Could not connect to Server "+ClientConnectionBean.host+":"+ClientConnectionBean.port);
						log.error("Not connected...exiting");
					}
				}
			});
		} catch (Exception err) {
			log.error("[ClientPacketMinaProcess]",err);
		}
	}
	
	public void sendData(Object clientBean) throws Exception {

        //IoBuffer buffer = IoBuffer.allocate(data.length);

//		IoBuffer buffer = IoBuffer.wrap(data);
//
//		buffer.put(data);
//        //buffer.putLong(free);
//		
//        buffer.flip();
        
		
        session.write(clientBean);

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new InterruptedException(e.getMessage());
//        }

    }
	
	
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	log.error("[exceptionCaught]",cause);
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	
        log.debug("Message recv...");
        
        if (message instanceof ClientFrameBean) {
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			log.debug("Recv Frame Bean ");
			
			ClientViewerScreen.instance.addClientFrameBean((ClientFrameBean) message);
			
		}
		
		if (message instanceof ClientStatusBean) {
			
			ClientStatusBean cBean = (ClientStatusBean) message;
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			log.debug("Recv Status Bean "+cBean.getMode());
			
			if (cBean.getMode() == 4) {
				if (ClientViewerScreen.instance != null) {
					ClientViewerScreen.instance.showWarningPopUp(ClientViewerScreen.instance.label736);
					ClientViewerScreen.instance.alreadyClosedWarning = true;
					ClientTransportMinaPool.closeSession();
				}
				
			}
			
		}
		
		if (message instanceof ClientCursorStatus) {
			
			ClientCursorStatus clientCursorStatus = (ClientCursorStatus) message;
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			log.debug("Recv Status Bean "+clientCursorStatus.getMode());
			
			ClientViewerScreen.instance.updateCursor(clientCursorStatus);
			
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
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("Message sent...");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("Session closed...");
        if (ClientViewerScreen.instance != null) {
	        if (!ClientViewerScreen.instance.alreadyClosedWarning){
	        	ClientViewerScreen.instance.showWarningPopUp(ClientViewerScreen.instance.label742);
	        }
        } else if (ClientStartScreen.instance != null){
        	ClientStartScreen.instance.showWarningPopUp(ClientViewerScreen.instance.label742);
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("Session created...");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.debug("Session idle...");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.debug("Session opened...");
    }

	/**
	 * 
	 */
	public void closeSession() throws Exception {
		// TODO Auto-generated method stub
		session.close(false);
	}

}
