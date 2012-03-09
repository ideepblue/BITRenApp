package org.bitren.app.entities;

import java.io.Serializable;

public class ContactEntity implements Serializable  {

	private static final long serialVersionUID = 5401286085331660799L;
	
	private int id;
	private int sid;
	private int pid;
	private boolean ispeople;
	private String department;
	private String phone_number;
	private String email;
	private String location;
	private String people;
	
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

	public String getDepartment() {
		return department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getPhone_number() {
		return phone_number;
	}
	
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPeople() {
		return people;
	}
	public void setPeople(String people) {
		this.people = people;
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
}
