package org.openmeetings.server.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author sebastianwagner
 *
 */
public class ServerDesktopCodecSharingFactory implements ProtocolCodecFactory {
			
    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public ServerDesktopCodecSharingFactory() {

        encoder = new ServerDesktopRequestEncoder();
        decoder = new ServerDesktopRequestDecoder();

    }
    
    
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}

}
