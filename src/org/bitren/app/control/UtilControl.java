package org.bitren.app.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bitren.app.GlobalConstant;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class UtilControl {

	private Context context;
	private HttpConnection httpConnection;
	
	public UtilControl(Context context) {
		this.context = context;
		httpConnection = new HttpConnection(this.context);
	}
	
	public List<ContactEntity> queryContactAll(NetworkStateEntity networkState) {

		String result = httpConnection.execGet(GlobalConstant.HOST_URL + "bit_contacts/index", null);

		if (result.startsWith("http_error")) {
			networkState.setState(NetworkStateEntity.HTTP_ERROR);
			networkState.setInfo(result.split(":")[1]);
			return null;
		}

		try {
			JSONArray jsonArray = new JSONArray(result);
				
			List<ContactEntity> list = new ArrayList<ContactEntity>();
			
			for (int i = 0; i < jsonArray.length(); i++) {
				ContactEntity contact = new ContactEntity();
				JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("BitContact");
				
				contact.setSid(jsonObject.getInt("id"));
				contact.setDepartment(jsonObject.getString("department"));
				contact.setLocation(jsonObject.getString("location"));
				contact.setPhone_number(jsonObject.getString("phone_number"));
				contact.setPeople(jsonObject.getString("people"));
				
				list.add(contact);
			}
			
			networkState.setState(NetworkStateEntity.OK);
			networkState.setInfo(NetworkStateEntity.OK);
		
			return list;
			
		} catch (JSONException e) {
			
			networkState.setState(NetworkStateEntity.JSON_ERROR);
			networkState.setInfo("ParseJSON-" + e.getMessage());
			
			return null;
		}
	}
	
	public void uploadFeedback(NetworkStateEntity networkState, String version, String platform, String channel, String description) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("version", version);
		params.put("platform", platform);
		params.put("channel", channel);
		params.put("description", description);
		
		String result = httpConnection.execPost(GlobalConstant.HOST_URL + "feedbacks/add", params);
		
		if (result.startsWith("http_error")) {
			networkState.setState(NetworkStateEntity.HTTP_ERROR);
			networkState.setInfo(result.split(":")[1]);
			return ;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			
			if (jsonObject.getString("state").equals(NetworkStateEntity.OK)) {
			
				networkState.setState(NetworkStateEntity.OK);
				networkState.setInfo(NetworkStateEntity.OK);
			
				return ;
				
			} else {
				
				networkState.setState(NetworkStateEntity.SERVER_ERROR);
				networkState.setInfo(jsonObject.getString("state"));
				
				return ;
			}
			
		} catch (JSONException e) {
			
			networkState.setState(NetworkStateEntity.JSON_ERROR);
			networkState.setInfo("ParsePost-" + e.getMessage());
			
			return ;
		}		
	}
}
