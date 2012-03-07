package org.bitren.app.entities;

import java.io.Serializable;

public class NetworkStateEntity implements Serializable {

	private static final long serialVersionUID = 7550942946506833011L;
	public static final String OK = "ok";
	public static final String JSON_ERROR = "json_error";
	public static final String HTTP_ERROR = "http_error";
	public static final String SERVER_ERROR = "server_error";

	private String state;
	private String info;
	
	public NetworkStateEntity () {
		state = "";
		info = "";
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
