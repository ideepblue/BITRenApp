package org.bitren.app;

import org.bitren.app.control.UtilControl;
import org.bitren.app.entities.NetworkStateEntity;

import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
	
	private Toast toast;
	private TextView textViewVersion;
	private Button buttonSubmit;
	private EditText editTextFeedback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_about);
		super.onCreate(savedInstanceState);
		
		initUI();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
	public void initUI() {
		
		toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		
		textViewVersion = (TextView)findViewById(R.id.textView_About_Version);
		textViewVersion.setText(this.getString(R.string.About_Version) + this.getString(R.string.version));
		
		editTextFeedback = (EditText)findViewById(R.id.editText_About_InputFeedbackField);
		
		buttonSubmit = (Button)findViewById(R.id.button_About_Submit);
		buttonSubmit.setOnClickListener(
				new View.OnClickListener(){
					public void onClick(View view) {
						
						String description = editTextFeedback.getText().toString();
						if (description.length() == 0) {
							toast.setText(AboutActivity.this.getString(R.string.About_NoInput));
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.show();
						} else {
							UploadFeedbackTask asyncTask = new UploadFeedbackTask();
							asyncTask.execute(description);
						}
					}
				}
			);
	}
	
	private class UploadFeedbackTask extends AsyncTask<String, Void, NetworkStateEntity> {
		private UtilControl utilControl;
		private NetworkStateEntity networkState;
		private ProgressDialog progressDialog;
		String version, platform, channel;
		
		@Override
		protected void onPreExecute() {
			networkState = new NetworkStateEntity();
			utilControl = new UtilControl(AboutActivity.this);
			version = AboutActivity.this.getString(R.string.version);
			platform = AboutActivity.this.getString(R.string.platform);
			channel = AboutActivity.this.getString(R.string.channel);
			
			this.progressDialog = ProgressDialog.show(
					AboutActivity.this,
					null,
					AboutActivity.this.getText(R.string.About_DoUploadFeedback),
					true, 
					false
					);
		}

		@Override
		protected NetworkStateEntity doInBackground(String... arg) {
			
			String description = arg[0];
			
			utilControl.uploadFeedback(networkState, version, platform, channel, description);
			
			return networkState;
		}
		
		protected void onPostExecute(NetworkStateEntity state) {
			
			this.progressDialog.dismiss();
			
			if (state.getState().equals(NetworkStateEntity.OK)) {
				
				toast.setText(AboutActivity.this.getText(R.string.About_UploadFeedbackSucceed));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				AboutActivity.this.finish();
				
			} else if (state.getState().equals(NetworkStateEntity.SERVER_ERROR)) {
				
				toast.setText(AboutActivity.this.getText(R.string.ServerError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			} else if (state.getState().equals(NetworkStateEntity.HTTP_ERROR)) {
				
				toast.setText(AboutActivity.this.getText(R.string.NetworkConnectionFailed));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			} else if (state.getState().equals(NetworkStateEntity.JSON_ERROR)) {
				
				toast.setText(AboutActivity.this.getText(R.string.JsonError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			} else {
				
				toast.setText(AboutActivity.this.getString(R.string.UnknownError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			}	
		}		
	}
}
