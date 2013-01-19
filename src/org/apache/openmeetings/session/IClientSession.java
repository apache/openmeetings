package org.apache.openmeetings.session;

import java.util.Date;
import java.util.List;


public interface IClientSession {

	public abstract void setUserObject(Long user_id, String username,
			String firstname, String lastname);

	public abstract void setUserObject(String username, String firstname,
			String lastname);

	public abstract Date getConnectedSince();

	public abstract void setConnectedSince(Date connectedSince);

	public abstract Boolean getIsMod();

	public abstract void setIsMod(Boolean isMod);

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract String getStreamid();

	public abstract void setStreamid(String streamid);

	public abstract String getScope();

	public abstract void setScope(String scope);

	public abstract String getFormatedDate();

	public abstract void setFormatedDate(String formatedDate);

	public abstract String getUsercolor();

	public abstract void setUsercolor(String usercolor);

	public abstract Integer getUserpos();

	public abstract void setUserpos(Integer userpos);

	public abstract String getUserip();

	public abstract void setUserip(String userip);

	public abstract String getSwfurl();

	public abstract void setSwfurl(String swfurl);

	public abstract int getUserport();

	public abstract void setUserport(int userport);

	public abstract String getFirstname();

	public abstract void setFirstname(String firstname);

	public abstract String getLanguage();

	public abstract void setLanguage(String language);

	public abstract String getLastLogin();

	public abstract void setLastLogin(String lastLogin);

	public abstract String getLastname();

	public abstract void setLastname(String lastname);

	public abstract String getMail();

	public abstract void setMail(String mail);

	public abstract String getOfficial_code();

	public abstract void setOfficial_code(String official_code);

	public abstract String getPicture_uri();

	public abstract void setPicture_uri(String picture_uri);

	public abstract Long getUser_id();

	public abstract void setUser_id(Long user_id);

	public abstract Long getRoom_id();

	public abstract void setRoom_id(Long room_id);

	public abstract Date getRoomEnter();

	public abstract void setRoomEnter(Date roomEnter);

	public abstract Boolean getIsRecording();

	public abstract void setIsRecording(Boolean isRecording);

	public abstract String getRoomRecordingName();

	public abstract void setRoomRecordingName(String roomRecordingName);

	public abstract String getAvsettings();

	public abstract void setAvsettings(String avsettings);

	public abstract long getBroadCastID();

	public abstract void setBroadCastID(long broadCastID);

	public abstract String getPublicSID();

	public abstract void setPublicSID(String publicSID);

	public abstract Boolean getZombieCheckFlag();

	public abstract void setZombieCheckFlag(Boolean zombieCheckFlag);

	public abstract Boolean getMicMuted();

	public abstract void setMicMuted(Boolean micMuted);

	public abstract Boolean getCanDraw();

	public abstract void setCanDraw(Boolean canDraw);

	public abstract Boolean getIsBroadcasting();

	public abstract void setIsBroadcasting(Boolean isBroadcasting);

	public abstract Boolean getCanShare();

	public abstract void setCanShare(Boolean canShare);

	public abstract String getExternalUserId();

	public abstract void setExternalUserId(String externalUserId);

	public abstract String getExternalUserType();

	public abstract void setExternalUserType(String externalUserType);

	public abstract List<String> getSharerSIDs();

	public abstract void setSharerSIDs(List<String> sharerSIDs);

	public abstract Boolean getIsSuperModerator();

	public abstract void setIsSuperModerator(Boolean isSuperModerator);

	public abstract Boolean getIsScreenClient();

	public abstract void setIsScreenClient(Boolean isScreenClient);

	public abstract int getVWidth();

	public abstract void setVWidth(int width);

	public abstract int getVHeight();

	public abstract void setVHeight(int height);

	public abstract int getVX();

	public abstract void setVX(int vx);

	public abstract int getVY();

	public abstract void setVY(int vy);

	public abstract String getStreamPublishName();

	public abstract void setStreamPublishName(String streamPublishName);

	public abstract Long getFlvRecordingId();

	public abstract void setFlvRecordingId(Long flvRecordingId);

	public abstract Long getFlvRecordingMetaDataId();

	public abstract void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId);

	public abstract boolean isScreenPublishStarted();

	public abstract void setScreenPublishStarted(boolean screenPublishStarted);

	public abstract Long getOrganization_id();

	public abstract void setOrganization_id(Long organization_id);

	public abstract boolean isStartRecording();

	public abstract void setStartRecording(boolean startRecording);

	public abstract boolean isStartStreaming();

	public abstract void setStartStreaming(boolean startStreaming);

	public abstract Integer getInterviewPodId();

	public abstract void setInterviewPodId(Integer interviewPodId);

	public abstract Boolean getCanRemote();

	public abstract void setCanRemote(Boolean canRemote);

	public abstract Boolean getCanGiveAudio();

	public abstract void setCanGiveAudio(Boolean canGiveAudio);

	public abstract Boolean getAllowRecording();

	public abstract void setAllowRecording(Boolean allowRecording);

	/**
	 * @see Client#isAVClient
	 * @return
	 */
	public abstract boolean getIsAVClient();

	public abstract void setIsAVClient(boolean isAVClient);

	public abstract boolean isStreamPublishStarted();

	public abstract void setStreamPublishStarted(boolean streamPublishStarted);

	/**
	 * To improve our trace log
	 */
	public abstract String toString();

	public abstract boolean isSipTransport();

	public abstract void setSipTransport(boolean sipTransport);

}