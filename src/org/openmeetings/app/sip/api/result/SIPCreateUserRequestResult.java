package org.openmeetings.app.sip.api.result;

import org.openmeetings.app.persistence.beans.user.UserSipData;

/**
 * TODO
 */
public abstract class SIPCreateUserRequestResult implements ISIPRequestResult {

    abstract public UserSipData getUserSipData();

}
