package org.openmeetings.app.sip.api.result;

/**
 * TODO
 */
public abstract class SipCreateConferenceRequestResult implements ISIPRequestResult {

    protected String conferenceNumber;
    protected String conferencePin;

    protected SipCreateConferenceRequestResult() {
    }

    protected SipCreateConferenceRequestResult(String conferenceNumber, String conferencePin) {
        this.conferenceNumber = conferenceNumber;
        this.conferencePin = conferencePin;
    }

    public String getConferenceNumber() {
        return conferenceNumber;
    }

    public String getConferencePin() {
        return conferencePin;
    }
}
