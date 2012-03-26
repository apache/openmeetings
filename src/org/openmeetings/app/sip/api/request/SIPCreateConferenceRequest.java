package org.openmeetings.app.sip.api.request;

/**
 * Create SIP conferece request
 */
public class SIPCreateConferenceRequest implements ISIPRequest {
    private String sipNumber;

    public SIPCreateConferenceRequest() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SIPCreateConferenceRequest(String sipNumber) {
        super();
        this.sipNumber = sipNumber;
    }
}