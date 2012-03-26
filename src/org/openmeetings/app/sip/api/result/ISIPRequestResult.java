package org.openmeetings.app.sip.api.result;

/**
 * * Base interface for SIP API request's resuslt
 */
public interface ISIPRequestResult {

    boolean hasError();

    String getErrorMessage();

}
