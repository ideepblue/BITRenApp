package org.bitren.app;

import org.bitren.app.control.UtilControl;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class FeedbackActivity extends Activity {
	
	private Toast toast;
	private EditText editTextName;
	private EditText editTextPhone;
	private EditText editTextLocation;
	private EditText editTextEmail;
	private EditText editTextComment;
	private RadioGroup radioGroupType;
	private RadioButton radioButtonModify;
	private RadioButton radioButtonAdd;
	private RadioButton radioButtonDelete;
	
	private TextView textViewSubmit;
	private EditText editTextFeedback;
	private ImageView imageViewBack;
	
	private ContactEntity mContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_feedback);
		super.onCreate(savedInstanceState);
		
		initUI();
		initData();
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
		
    	imageViewBack = (ImageView)findViewById(R.id.imageView_Back);
    	imageViewBack.setOnClickListener(
    			new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FeedbackActivity.this.finish();
					}
				}
    		);
		
		editTextFeedback = (EditText)findViewById(R.id.editText_InputFeedbackField);
		editTextName = (EditText)findViewById(R.id.editText_Name);
		editTextPhone = (EditText)findViewById(R.id.editText_Phone);
		editTextLocation = (EditText)findViewById(R.id.editText_Location);
		editTextEmail = (EditText)findViewById(R.id.editText_Email);
		editTextComment = (EditText)findViewById(R.id.editText_Comment);
		
		radioGroupType = (RadioGroup)findViewById(R.id.radioGroup);
		radioGroupType.setOnCheckedChangeListener(
				new RadioGroup.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == radioButtonModify.getId()) {
							editTextName.setEnabled(true);
							editTextPhone.setEnabled(true);
							editTextLocation.setEnabled(true);
							editTextEmail.setEnabled(true);
							editTextComment.setEnabled(true);
							editTextName.setText(mContact.getDescription());
							editTextPhone.setText(mContact.getPhone());
							editTextLocation.setText(mContact.getLocation());
							editTextEmail.setText(mContact.getEmail());
							editTextComment.setText(mContact.getComment());
						} else if (checkedId == radioButtonDelete.getId()){
							editTextName.setEnabled(false);
							editTextPhone.setEnabled(false);
							editTextLocation.setEnabled(false);
							editTextEmail.setEnabled(false);
							editTextComment.setEnabled(false);
						} else {
							editTextName.setEnabled(true);
							editTextPhone.setEnabled(true);
							editTextLocation.setEnabled(true);
							editTextEmail.setEnabled(true);
							editTextComment.setEnabled(true);
							editTextName.setText("");
							editTextPhone.setText("");
							editTextLocation.setText("");
							editTextEmail.setText("");
							editTextComment.setText("");
						}
					}
				}
			);
		radioButtonModify = (RadioButton)findViewById(R.id.radioButton_Modifiy);
		radioButtonAdd = (RadioButton)findViewById(R.id.radioButton_Add);
		radioButtonDelete = (RadioButton)findViewById(R.id.radioButton_Delete);
		
		textViewSubmit = (TextView)findViewById(R.id.textView_Submit);
		textViewSubmit.setOnClickListener(
				new View.OnClickListener(){
					public void onClick(View view) {
						
						String description;
						if (radioButtonModify.isChecked()) {
							description = 
								"type = update, " +
								"sid = " + mContact.getSid() + ", " +
								"description = " + editTextName.getText().toString() + ", " +
								"phone = " + editTextPhone.getText().toString() + ", " +
								"location = " + editTextLocation.getText().toString() + ", " +
								"email = " + editTextEmail.getText().toString() + ", " +
								"comment = " + editTextComment.getText().toString() + ", " +
								"last = " + editTextFeedback.getText().toString();
						} else if (radioButtonDelete.isChecked()){
							description = 
								"type = delete, " +
								"sid = " + mContact.getSid();
						} else if (radioButtonAdd.isChecked()) {
							description = 
									"type = insert, " +
									"sid = " + mContact.getSid() + ", " +
									"description = " + editTextName.getText().toString() + ", " +
									"phone = " + editTextPhone.getText().toString() + ", " +
									"location = " + editTextLocation.getText().toString() + ", " +
									"email = " + editTextEmail.getText().toString() + ", " +
									"comment = " + editTextComment.getText().toString() + ", " +
									"last = " + editTextFeedback.getText().toString();
						} else {
							description = "impossible";
						}
						
						UploadFeedbackTask asyncTask = new UploadFeedbackTask();
						asyncTask.execute(description);
					}
				}
			);
	}
	
	private void initData() {
		DatabaseOperator dbo = new DatabaseOperator(this);
		int contact_sid = this.getIntent().getIntExtra("sid", 0);
		mContact = dbo.queryContactBySid(contact_sid);
		if (mContact.isIspeople()) {
			radioButtonAdd.setVisibility(View.GONE);
		} else {
			radioButtonAdd.setVisibility(View.VISIBLE);
		}
		radioButtonModify.setChecked(true);
	}
	
	private class UploadFeedbackTask extends AsyncTask<String, Void, NetworkStateEntity> {
		private UtilControl utilControl;
		private NetworkStateEntity networkState;
		private ProgressDialog progressDialog;
		String version, platform, channel;
		
		@Override
		protected void onPreExecute() {
			networkState = new NetworkStateEntity();
			utilControl = new UtilControl(FeedbackActivity.this);
			version = FeedbackActivity.this.getString(R.string.version);
			platform = FeedbackActivity.this.getString(R.string.platform);
			channel = FeedbackActivity.this.getString(R.string.channel);
			
			this.progressDialog = ProgressDialog.show(
					FeedbackActivity.this,
					null,
					FeedbackActivity.this.getText(R.string.About_DoUploadFeedback),
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
				
				toast.setText(FeedbackActivity.this.getText(R.string.About_UploadFeedbackSucceed));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				FeedbackActivity.this.finish();
				
			} else {
				
				toast.setText(state.getInfo());
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			}
		}		
	}
}
