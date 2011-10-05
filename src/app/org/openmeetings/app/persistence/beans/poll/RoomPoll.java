package org.openmeetings.app.persistence.beans.poll;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.beans.rooms.Rooms;

@Entity
@Table(name = "room_polls")
public class RoomPoll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_poll_id")
	private Long roomPollId;
	@Column(name = "poll_question")
	private String pollQuestion;
	@Column(name = "created")
	private Date created;
	@Column(name = "archived")
	private boolean archived;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "poll_type_id")
	private PollType pollType;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomclient_id")
	private RoomClient createdBy;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rooms_id")
	private Rooms room;
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "room_poll_id")
	private List<RoomPollAnswers> roomPollAnswerList;

	/**
	 * @return the createdBy
	 */
	public RoomClient getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(RoomClient createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the pollQuestion
	 */
	public String getPollQuestion() {
		return pollQuestion;
	}

	/**
	 * @param pollQuestion
	 *            the pollQuestion to set
	 */
	public void setPollQuestion(String pollQuestion) {
		this.pollQuestion = pollQuestion;
	}

	/**
	 * @return the roomPollAnswerList
	 */
	public List<RoomPollAnswers> getRoomPollAnswerList() {
		return (roomPollAnswerList == null) ? new LinkedList<RoomPollAnswers>() : roomPollAnswerList;
	}

	/**
	 * @param roomPollAnswerList
	 *            the roomPollAnswerList to set
	 */
	public void setRoomPollAnswerList(List<RoomPollAnswers> roomPollAnswerList) {
		this.roomPollAnswerList = roomPollAnswerList;
	}

	/**
	 * @return the roomPollId
	 */
	public Long getRoomPollId() {
		return roomPollId;
	}

	/**
	 * @param roomPollId
	 *            the roomPollId to set
	 */
	public void setRoomPollId(Long roomPollId) {
		this.roomPollId = roomPollId;
	}

	/**
	 * @return the pollTypeId
	 */
	public PollType getPollType() {
		return pollType;
	}

	public void setPollType(PollType pollType) {
		this.pollType = pollType;
	}

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	/**
	 * @return the archived
	 */
	public boolean isArchived() {
		return archived;
	}

	/**
	 * @param archived the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

}
