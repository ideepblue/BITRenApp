package org.bitren.app.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.bitren.app.R;
import org.bitren.app.entities.NetworkStateEntity;

import android.content.Context;
import android.util.Log;

public class HttpConnection {
	private static final String TAG = "Control.HttpConnection";

	private Context context;
	private HttpClient httpClient;
	private HttpParams httpParams;
	private HttpResponse httpResponse;
	private HttpPost httpPost;
	private HttpGet httpGet;

	private static final int TIMEOUT = 5000;

	public HttpConnection(Context context) {
		this.context = context;

		httpParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);

		String userAgent = this.context.getString(R.string.user_agent) + "/"
				+ this.context.getString(R.string.channel) + " "
				+ this.context.getString(R.string.version);
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		httpClient = new DefaultHttpClient(httpParams);
	}

	public String execPost(String url, Map<String, Object> rawParams) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (String key : rawParams.keySet()) {
			params.add(new BasicNameValuePair(key, rawParams.get(key)
					.toString()));
		}

		httpPost = new HttpPost(url);

		String result = "http_error:exec post error";

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (httpResponse.getEntity() != null) {
					
					result = EntityUtils.toString(httpResponse.getEntity(),
							HTTP.UTF_8);
					Log.v(TAG, result);

					return result;
				} else {
					result = "http_error:HttpClientExec-no result";
					return result;
				}
			} else {
				result = "http_error:HttpClientExec-"
						+ httpResponse.getStatusLine();
				return result;
			}
		} catch (ClientProtocolException e) {
			result = "http_error:ClientProtocolException-" + e.getMessage();
			
			return result;
		} catch (IOException e) {
			
			// Socket is not connected
			// Network unreachable
			// No route to host
			// Connection to HOST_URL refused
			// Host is unresolved
			// Connect to HOST_URL timed out
			result = "http_error:IOException-" + e.getMessage();
			return result;
		}
	}

	public String execGet(String url, Map<String, Object> rawParams) {
		String params = "";

		if (rawParams != null) {
			for (String key : rawParams.keySet()) {
				params += "&" + key + "=" + rawParams.get(key);
			}
		}

		if (!params.equals("")) {
			params = params.replaceFirst("&", "?");
		}

		httpGet = new HttpGet(url + params);

		String result = "http_error:exec get error";

		try {
			httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (httpResponse.getEntity() != null) {
					result = EntityUtils.toString(httpResponse.getEntity(),
							HTTP.UTF_8);
					Log.v(TAG, result);

					return result;
				} else {
					result = "http_error:HttpClientExec-no result";
					return result;
				}
			} else {
				result = "http_error:HttpClientExec-"
						+ httpResponse.getStatusLine();
				return result;
			}
		} catch (ClientProtocolException e) {

			result = "http_error:ClientProtocolException-" + e.getMessage();
			
			return result;
		} catch (IOException e) {
						
			// Socket is not connected
			// Network unreachable
			// No route to host
			// Connection to HOST_URL refused
			// Host is unresolved
			// Connect to HOST_URL timed out
			result = "http_error:IOException-" + e.getMessage();
			return result;
		}		
	}
	
	public HttpEntity execGetFile(String url, NetworkStateEntity networkState) {

		httpGet = new HttpGet(url);

		try {
			
			httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (httpResponse.getEntity() != null) {
					
					networkState.setState(NetworkStateEntity.OK);
					networkState.setInfo(NetworkStateEntity.OK);
					
					return httpResponse.getEntity();

				} else {
					
					networkState.setState(NetworkStateEntity.HTTP_ERROR);
					networkState.setInfo("HttpClientExec-no result");
					
					return null;
				}
			} else {
				networkState.setState(NetworkStateEntity.HTTP_ERROR);
				networkState.setInfo("HttpClientExec-" + httpResponse.getStatusLine());
				return null;
			}
			
		} catch (ClientProtocolException e) {
			
			networkState.setState(NetworkStateEntity.HTTP_ERROR);
			networkState.setInfo("ClientProtocolException-" + e.getMessage());
			
			return null;
			
		} catch (IOException e) {
			
			// Socket is not connected
			// Network unreachable
			// No route to host
			// Connection to HOST_URL refused
			// Host is unresolved
			// Connect to HOST_URL timed out
			networkState.setState(NetworkStateEntity.HTTP_ERROR);
			networkState.setInfo("IOException-" + e.getMessage());
			return null;
		}		
	}
}
