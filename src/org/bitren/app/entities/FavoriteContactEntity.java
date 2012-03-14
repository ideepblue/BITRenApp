package org.bitren.app.entities;

import java.io.Serializable;

public class FavoriteContactEntity implements Serializable  {

	private static final long serialVersionUID = -4158299752103927403L;
	private int id;
	private int contact_sid;
	private String name;
	private ContactEntity contact;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getContact_sid() {
		return contact_sid;
	}

	public void setContact_sid(int contact_sid) {
		this.contact_sid = contact_sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ContactEntity getContact() {
		return contact;
	}

	public void setContact(ContactEntity contact) {
		this.contact = contact;
	}
}
