package org.bitren.app.customview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.bitren.app.AboutActivity;
import org.bitren.app.BitrenActions;
import org.bitren.app.R;
import org.bitren.app.control.UtilControl;
import org.bitren.app.database.ContactsColumns;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MenuDialog {
	
	private static final String TAG = "MenuDialog";

	private Context context;
	private Dialog mDialog;
	private Toast toast;
	
	public MenuDialog(Context context) {
		this.context = context;
	}
	
	public void setDialog(Dialog dialog) {
		mDialog = dialog;
	}
	
	public View getView() {
		toast = Toast.makeText(context, "toast", Toast.LENGTH_SHORT);
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutMenu = inflater.inflate(R.layout.layout_menu_dialog, null);
		layoutMenu.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		RelativeLayout relativeLayoutAbout;
		RelativeLayout relativeLayoutUpdate;
		RelativeLayout relativeLayoutAppUpdate;
		RelativeLayout relativeLayoutExit;
		
		relativeLayoutAbout = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_About);
		relativeLayoutAbout.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
			    		intent.setClass(context, AboutActivity.class);
			    		context.startActivity(intent);					
			    		mDialog.dismiss();
					}
				}
			);
		
		relativeLayoutUpdate = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_Update);
		relativeLayoutUpdate.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
			    		
						mDialog.dismiss();
			    		UpdateContactTask asyncTask = new UpdateContactTask();
			    		asyncTask.execute();
			    		
					}
				}
			);
		
		relativeLayoutAppUpdate = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_AppUpdate);
		relativeLayoutAppUpdate.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						
						final ProgressDialog progressDialog = ProgressDialog.show(
								context, 
								null, 
								context.getText(R.string.App_CheckingUpdate),
								true,
								false
								);
						MobclickAgent.setUpdateListener(
								new UmengUpdateListener(){
							 
									@Override
									public void onUpdateReturned(int arg) {
										progressDialog.dismiss();
//										Log.v(TAG, MobclickAgent.getUpdateInfo().toString());
										Log.v(TAG, "arg = " + arg);
										switch(arg){
										case 0:                 //has update
											Log.i(TAG, "show dialog");
											break;
										case 1:                 //has no update
											toast.setText(context.getString(R.string.App_NoNeedUpdate));
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.show();
											break;
										case 2:                 //none wifi
											break;
										case 3:                 //time out
											toast.setText(context.getString(R.string.HttpError));
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.show();
											break;
										}
									}
								}
							);
						new Handler().postDelayed(new Runnable() {

							public void run() {
								MobclickAgent.update(context);
							}
						}, 500);
			    		
					}
				}
			);
		
		relativeLayoutExit = (RelativeLayout) layoutMenu.findViewById(R.id.relativeLayout_Menu_About);
		relativeLayoutExit.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						System.exit(0);
					}
				}
			);
		
		return layoutMenu;
	}
	
	private class UpdateContactTask extends AsyncTask<Void, Integer, Void> {
		private UtilControl utilControl;
		private NetworkStateEntity networkState;
		private ProgressDialog progressDialog;
		private DatabaseOperator dbo;
		private boolean isDownloading;
		private String strQuery, strDownload, strUpdate;
		
		@Override
		protected void onPreExecute() {
			utilControl = new UtilControl(context);
			networkState = new NetworkStateEntity();
			dbo = new DatabaseOperator(context);
			isDownloading = true;
			strQuery = context.getString(R.string.Contact_QueryData);
			strDownload = context.getString(R.string.Contact_DownloadData);
			strUpdate = context.getString(R.string.Contact_UpdateData);

			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(strQuery);
			progressDialog.setProgress(0);
			progressDialog.show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... params) {
			int progress = params[0];
			super.onProgressUpdate(params);
			if (isDownloading) {
				progressDialog.setMessage(strDownload);
			} else {
				progressDialog.setMessage(strUpdate);
			}
			progressDialog.setProgress(progress);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			String timestamp = dbo.queryTableTimestampByName(ContactsColumns.TABLE_NAME);
			Log.v(TAG, "local timestamp = " + timestamp);
			HttpEntity httpEntity = utilControl.queryContactByTimestamp(networkState, timestamp);
			
			if (! networkState.getState().equals(NetworkStateEntity.OK)) {
				return null;
			}
			
			progressDialog.setIndeterminate(false);
			
    		long contentLength = 0;
    		contentLength = httpEntity.getContentLength();
    		byte buffer[] = new byte[4096];
    		int readLength = 0;
    		long totalLength = 0;
    		
			isDownloading = true;
    		progressDialog.setMax((int)contentLength);
			publishProgress(0);
    		
    		Log.v(TAG, "contentLengt = " + contentLength);
    		
    		try {
				InputStream inputStream = httpEntity.getContent();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				
				while ((readLength = inputStream.read(buffer)) > 0) {
					
					totalLength += readLength;
					os.write(buffer, 0, readLength);
					
					publishProgress((int)totalLength);
					
				}
				
				if (totalLength != contentLength) {
					networkState.setState(NetworkStateEntity.DOWNLOAD_ERROR);
					networkState.setInfo(context.getString(R.string.DownloadError));
					networkState.setDetail("download failed");
					return null;
				}
				
				Thread.sleep(1000);
				
				JSONArray jsonArray = new JSONArray(os.toString());
				
				int jsonLength = jsonArray.length();
				
				if (jsonLength == 1) {
					networkState.setInfo(context.getString(R.string.Contact_NoNeedUpdate));
					return null;
				}
				
				isDownloading = false;
				progressDialog.setMax(jsonLength - 1);
				publishProgress(0);
				
				for (int i = 1; i < jsonLength; i++) {
					ContactEntity contact = new ContactEntity();
					JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("BitContact");
					
					contact.setSid(jsonObject.getInt("id"));
					contact.setPid(jsonObject.getInt("pid"));
					contact.setIspeople(jsonObject.getInt("ispeople") > 0);
					contact.setDescription(jsonObject.getString("description"));
					contact.setLocation(jsonObject.getString("location"));
					contact.setEmail(jsonObject.getString("email"));
					contact.setComment(jsonObject.getString("comment"));
					contact.setPhone(jsonObject.getString("phone"));
					
					if (dbo.updateContactBySid(contact) == 0) {
						dbo.insertContact(contact);
					}
					
					publishProgress(i);
				}
				
				timestamp = jsonArray.getJSONObject(0).getString("timestamp");
				Log.v(TAG, "server timestamp = " + timestamp);
				dbo.updateTableTimestampByName(ContactsColumns.TABLE_NAME, timestamp);
				
				Thread.sleep(1000);
				networkState.setInfo(context.getString(R.string.Contact_UpdateDataSucceed));
				return null;
				
			} catch (IllegalStateException e) {
				networkState.setState(NetworkStateEntity.HTTP_ERROR);
				networkState.setInfo(context.getString(R.string.HttpError));
				networkState.setDetail(e.getMessage());
				return null;
			} catch (IOException e) {
				networkState.setState(NetworkStateEntity.HTTP_ERROR);
				networkState.setInfo(context.getString(R.string.HttpError));
				networkState.setDetail(e.getMessage());
				return null;
			} catch (JSONException e) {
				networkState.setState(NetworkStateEntity.JSON_ERROR);
				networkState.setInfo(context.getString(R.string.JsonError));
				networkState.setDetail(e.getMessage());
				return null;
			} catch (InterruptedException e) {
				networkState.setState(NetworkStateEntity.JSON_ERROR);
				networkState.setInfo(context.getString(R.string.JsonError));
				networkState.setDetail(e.getMessage());
				return null;
			}	
		}
		
		@Override
		protected void onPostExecute(Void params) {
			progressDialog.dismiss();
			toast.setText(networkState.getInfo());
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
			
			if (networkState.getInfo().equals(context.getString(R.string.Contact_UpdateDataSucceed))) {
				// 更新成功后需要刷新界面
				Intent intent = new Intent(BitrenActions.ACTION_REFRESH_CONTACT);
				context.sendBroadcast(intent);
			}
		}
		
	}
}
