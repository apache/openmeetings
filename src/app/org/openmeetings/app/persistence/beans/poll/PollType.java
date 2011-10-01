package org.openmeetings.app.persistence.beans.poll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmeetings.app.persistence.beans.lang.Fieldvalues;

@Entity
@Table(name = "poll_types")
public class PollType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "poll_type_id")
	private Long pollTypesId;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fieldvalues_id")
	private Fieldvalues label;
	@Column(name = "numeric_answer")
	private Boolean isNumericAnswer;
	
	/**
	 * @return the pollTypesId
	 */
	public Long getPollTypesId() {
		return pollTypesId;
	}
	/**
	 * @param pollTypesId the pollTypesId to set
	 */
	public void setPollTypesId(Long pollTypesId) {
		this.pollTypesId = pollTypesId;
	}
	/**
	 * @return the isNumericAnswer
	 */
	public Boolean getIsNumericAnswer() {
		return isNumericAnswer;
	}
	/**
	 * @param isNumericAnswer the isNumericAnswer to set
	 */
	public void setIsNumericAnswer(Boolean isNumericAnswer) {
		this.isNumericAnswer = isNumericAnswer;
	}
	/**
	 * @return the pollTypeLabelid
	 */
	public Fieldvalues getLabel() {
		return label;
	}
	/**
	 * @param pollTypeLabelid the pollTypeLabelid to set
	 */
	public void setLabel(Fieldvalues label) {
		this.label = label;
	}
}
