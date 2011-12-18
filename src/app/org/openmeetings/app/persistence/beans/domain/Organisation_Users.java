package org.openmeetings.app.persistence.beans.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name="selectMaxUsersByOrganisationId",
		query="SELECT COUNT(c.organisation_users_id) FROM Organisation_Users c WHERE c.deleted = 'false' AND c.organisation.organisation_id = :organisation_id")
	, @NamedQuery(name="getOrganisation_UserByUserAndOrganisation",
		query="SELECT ou FROM Users u, IN(u.organisation_users) ou WHERE u.deleted = 'false' AND u.user_id = :user_id AND ou.organisation.organisation_id = :organisation_id")
})
@Table(name = "organisation_users")
public class Organisation_Users implements Serializable {

	private static final long serialVersionUID = 7206870465903375817L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organisation_users_id")
	private Long organisation_users_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organisation_id", insertable = true, updatable = true)
	private Organisation organisation;

	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	private String deleted;
	
	@Column(name = "is_moderator")
	private Boolean isModerator;
	
	@Column(name = "comment_field")
	private String comment;

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Long getOrganisation_users_id() {
		return organisation_users_id;
	}

	public void setOrganisation_users_id(Long organisation_users_id) {
		this.organisation_users_id = organisation_users_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsModerator() {
		return isModerator;
	}

	public void setIsModerator(Boolean isModerator) {
		this.isModerator = isModerator;
	}
}
