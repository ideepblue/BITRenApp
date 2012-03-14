package org.bitren.app.entities;

import java.io.Serializable;

public class ContactEntity implements Serializable  {

	private static final long serialVersionUID = 5401286085331660799L;
	
	private int id;
	private int sid;
	private int pid;
	private boolean ispeople;
	private String description;
	private String email;
	private String location;
	private String comment;
	private String phone;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public boolean isIspeople() {
		return ispeople;
	}

	public void setIspeople(boolean ispeople) {
		this.ispeople = ispeople;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
