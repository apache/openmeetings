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
package org.openmeetings.server.rtmp;


//import org.apache.mina.common.ByteBuffer;
import java.io.IOException;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.io.utils.ObjectMap;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.ClientExceptionHandler;
import org.red5.server.net.rtmp.INetStreamEventHandler;
import org.red5.server.net.rtmp.RTMPClient;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.stream.AbstractClientStream;
import org.red5.server.stream.IStreamData;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ScreenClient extends RTMPClient implements INetStreamEventHandler, 
		ClientExceptionHandler, IPendingServiceCallback {

	private static final Logger logger = Red5LoggerFactory.getLogger(ScreenClient.class, ScopeApplicationAdapter.webAppRootKey);
    //private static final Logger logger = Red5LoggerFactory.getLogger( RTMPUser.class );

    public boolean createdPlayStream = false;

    public boolean startPublish = false;

    public Integer playStreamId;

    public Integer publishStreamId;

    private String publishName;

    private String playName;

    private RTMPConnection conn;

    //private ByteBuffer buffer;


    // ------------------------------------------------------------------------
    //
    // Overide
    //
    // ------------------------------------------------------------------------

    @Override
    public void connectionOpened( RTMPConnection conn, RTMP state ) {

        logger.debug( "connection opened" );
        super.connectionOpened( conn, state );
        this.conn = conn;
    }


    @Override
    public void connectionClosed( RTMPConnection conn, RTMP state ) {

        logger.debug( "connection closed" );
        super.connectionClosed( conn, state );
    }


    @Override
    protected void onInvoke( RTMPConnection conn, Channel channel, Header header, Notify notify, RTMP rtmp ) {

        super.onInvoke( conn, channel, header, notify, rtmp );

        try {
            @SuppressWarnings("unchecked")
			ObjectMap< String, String > map = (ObjectMap<String, String>) notify.getCall().getArguments()[ 0 ];
            String code = map.get( "code" );

            if ( StatusCodes.NS_PLAY_STOP.equals( code ) ) {
                logger.debug( "onInvoke, code == NetStream.Play.Stop, disconnecting" );
                disconnect();
            }
        }
        catch ( Exception e ) {

        }

    }


    // ------------------------------------------------------------------------
    //
    // Public
    //
    // ------------------------------------------------------------------------

    public void startStream( String host, String app, int port, String publishName, String playName ) {

        System.out.println( "RTMPUser startStream" );

        this.publishName = publishName;
        this.playName = playName;

        createdPlayStream = false;
        startPublish = false;

        try {
            connect( host, port, app, this );

            while ( !startPublish ) {
                Thread.yield();
            }
        }
        catch ( Exception e ) {
            logger.error( "RTMPUser startStream exception " + e );
        }

    }


    public void stopStream() {

        System.out.println( "RTMPUser stopStream" );

        try {
            disconnect();
        }
        catch ( Exception e ) {
            logger.error( "RTMPUser stopStream exception " + e );
        }

    }


    // ------------------------------------------------------------------------
    //
    // Implementations
    //
    // ------------------------------------------------------------------------

        public void handleException(Throwable throwable)
        {
                        logger.error("{}"+new Object[]{throwable.getCause()});
                        System.out.println( throwable.getCause() );
        }


    public void onStreamEvent( Notify notify ) {

        logger.debug( "onStreamEvent " + notify );

        @SuppressWarnings("unchecked")
		ObjectMap< String, String > map = (ObjectMap< String, String >) notify.getCall().getArguments()[ 0 ];
        String code = map.get( "code" );

        if ( StatusCodes.NS_PUBLISH_START.equals( code ) ) {
            logger.debug( "onStreamEvent Publish start" );
            startPublish = true;
        }
    }


    public void resultReceived( IPendingServiceCall call ) {

        logger.debug( "service call result: " + call );

        if ( "connect".equals( call.getServiceMethodName() ) ) {
            createPlayStream( this );

        }
        else if ( "createStream".equals( call.getServiceMethodName() ) ) {

            if ( createdPlayStream ) {
                publishStreamId = (Integer) call.getResult();
                logger.debug( "createPublishStream result stream id: " + publishStreamId );
                logger.debug( "publishing video by name: " + publishName );
                publish( publishStreamId, publishName, "live", this );

            }
            else {
                playStreamId = (Integer) call.getResult();
                logger.debug( "createPlayStream result stream id: " + playStreamId );
                logger.debug( "playing video by name: " + playName );
                play( playStreamId, playName, -2000, -1000 );

                createdPlayStream = true;
                createStream( this );
            }
        }
    }


    public void pushVideo() {
    	
    }
    
    public void pushAudio( int len, byte[] audio, long ts, int codec ) throws IOException {

//        if ( buffer == null ) {
//            buffer = ByteBuffer.allocate( 1024 );
//            buffer.setAutoExpand( true );
//        }
//
//        buffer.clear();
//
//        buffer.put( (byte) codec ); // first byte 2 mono 5500; 6 mono 11025; 22
//        // mono 11025 adpcm 82 nellymoser 8000 178
//        // speex 8000
//        buffer.put( audio );
//
//        buffer.flip();
//
//        AudioData audioData = new AudioData( buffer );
//        audioData.setTimestamp( (int) ts );
//
//        kt++;
//        if ( kt < 10 ) {
//            logger.debug( "+++ " + audioData );
//            System.out.println( "+++ " + audioData  );
//        }
//
//        RTMPMessage rtmpMsg = new RTMPMessage();
//        rtmpMsg.setBody( audioData );
//        publishStreamData( publishStreamId, rtmpMsg );
    }


    // ------------------------------------------------------------------------
    //
    // Privates
    //
    // ------------------------------------------------------------------------

    private void createPlayStream( IPendingServiceCallback callback ) {

        logger.debug( "create play stream" );
        IPendingServiceCallback wrapper = new CreatePlayStreamCallBack( callback );
        invoke( "createStream", null, wrapper );
    }

    private class CreatePlayStreamCallBack implements IPendingServiceCallback {

        private IPendingServiceCallback wrapped;


        public CreatePlayStreamCallBack( IPendingServiceCallback wrapped ) {

            this.wrapped = wrapped;
        }


        public void resultReceived( IPendingServiceCall call ) {

            Integer streamIdInt = (Integer) call.getResult();

            if ( conn != null && streamIdInt != null ) {
                PlayNetStream stream = new PlayNetStream();
                stream.setConnection( conn );
                stream.setStreamId( streamIdInt.intValue() );
                conn.addClientStream( stream );
            }
            wrapped.resultReceived( call );
        }

    }

    private class PlayNetStream extends AbstractClientStream implements IEventDispatcher {

        public void close() {

        }


        public void start() {

        }


        public void stop() {

        }


        public void dispatchEvent( IEvent event ) {

            if ( !( event instanceof IRTMPEvent ) ) {
                logger.debug( "skipping non rtmp event: " + event );
                return;
            }

            IRTMPEvent rtmpEvent = (IRTMPEvent) event;

            if ( logger.isDebugEnabled() ) {
                // logger.debug("rtmp event: " + rtmpEvent.getHeader() + ", " +
                // rtmpEvent.getClass().getSimpleName());
            }

            if ( !( rtmpEvent instanceof IStreamData ) ) {
                logger.debug( "skipping non stream data" );
                return;
            }

            if ( rtmpEvent.getHeader().getSize() == 0 ) {
                logger.debug( "skipping event where size == 0" );
                return;
            }

            if ( rtmpEvent instanceof VideoData ) {
                // videoTs += rtmpEvent.getTimestamp();
                // tag.setTimestamp(videoTs);

            }
            else if ( rtmpEvent instanceof AudioData ) {
            	
            	//I don't need to send any data out, we just need to publish
            	
//                audioTs += rtmpEvent.getTimestamp();
//
//                ByteBuffer audioData = ( (IStreamData) rtmpEvent ).getData().asReadOnlyBuffer();
//                byte[] data = SerializeUtils.ByteBufferToByteArray( audioData );
//
//                //System.out.println( "RTMPUser.dispatchEvent() - AudioData -> length = " + data.length + ".");
//
//                kt2++;
//
//                if ( kt2 < 10 ) {
//                    logger.debug( "*** " + data.length );
//                    System.out.println( "*** " + data.length );
//                }
//
//                try {
//                    if ( rtpStreamSender != null ) {
//                        rtpStreamSender.send( data, 1, data.length - 1 );
//                    }
//                }
//                catch ( Exception e ) {
//                    System.out.println( "PlayNetStream dispatchEvent exception " + e );
//                }

            }
        }
    }

	


}
