package org.openmeetings.app.persistence.beans.adresses;

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
import javax.persistence.Table;

@Entity
@Table(name = "adresses")
public class Adresses implements Serializable {

	private static final long serialVersionUID = 1387576041912128161L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adresses_id")
	private Long adresses_id;
	@Column(name = "additionalname")
	private String additionalname;
	@Column(name = "comment_field")
	private String comment;
	@Column(name = "fax")
	private String fax;
	@Column(name = "starttime")
	private Date starttime;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "state_id")
	private States states;
	@Column(name = "street")
	private String street;
	@Column(name = "town")
	private String town;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "zip")
	private String zip;
	@Column(name = "deleted")
	private String deleted;

	@Column(name = "email")
	private String email;
	@Column(name = "phone")
	private String phone;

	public Adresses() {
		// TODO Auto-generated constructor stub
	}

	public String getAdditionalname() {
		return additionalname;
	}

	public void setAdditionalname(String additionalname) {
		this.additionalname = additionalname;
	}

	public Long getAdresses_id() {
		return adresses_id;
	}

	public void setAdresses_id(Long adresses_id) {
		this.adresses_id = adresses_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public States getStates() {
		return states;
	}

	public void setStates(States states) {
		this.states = states;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
