package org.bitren.app;

import java.util.ArrayList;
import java.util.List;

import org.bitren.app.control.UtilControl;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class ContactActivity extends Activity {
	
	private Toast toast;
	private TextView textViewTitle;
	private ListView listViewContact;
	
	private List<ContactEntity> listContact;
	private ContactAdapter adapterContact;
	
	private DatabaseOperator mDBO;
	
	private int currentPid;
	// 读秒用
	private long lastHitMillis;
	
	private static final int MENU_REFRESH_ID = 0;
	private static final int MENU_FEEDBACK_ID = 1;
	private static final int MENU_EXIT_ID = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        
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
    
    private void initUI() {
    	toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
    	
    	textViewTitle = (TextView)findViewById(R.id.textView_Contact_Title);
    	
    	listViewContact = (ListView)findViewById(R.id.listView_Contact);
    	listViewContact.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						ContactEntity contact = ContactActivity.this.listContact.get(position);
						
						if (contact.isIspeople()) {
							final String phoneNumber = contact.getPhone_number();
						
							AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
							builder.setMessage(ContactActivity.this.getString(R.string.Contact_DialPhoneNumber, phoneNumber))
			        	       	.setCancelable(false)
			        	       	.setPositiveButton(
			        	       			ContactActivity.this.getText(R.string.ok), 
			        	       				new DialogInterface.OnClickListener() {

			        	       					public void onClick(DialogInterface dialog, int id) {
											
			        	       						String tel = "tel:" + phoneNumber;
											
			        	       						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
			        	       						ContactActivity.this.startActivity(intent);
			        	       					}

			        	    		   }
			        	       	)
			        	       	.setNegativeButton(
			        	    		   ContactActivity.this.getText(R.string.cancel),
			        	    		   null
			        	       	);
			        	
							AlertDialog dialog = builder.create();
							dialog.show();
							
						} else {
							
							ContactActivity.this.changeCurrentPid(contact.getSid());
						}
					}
				}
			);
    	
    }
    
    private void initData() {
    	mDBO = new DatabaseOperator(this);
    	
    	listContact = new ArrayList<ContactEntity>();
    	
    	adapterContact = new ContactAdapter();
    	listViewContact.setAdapter(adapterContact);
    	
    	changeCurrentPid(0);
    }
    
    private void changeCurrentPid(int pid) {
    	currentPid = pid;
    	listContact.clear();
    	listContact.addAll( mDBO.querySchoolCalendarByPid(currentPid) );
    	adapterContact.notifyDataSetChanged();
    	listViewContact.setSelection(0);
    	if (pid == 0) {
    		textViewTitle.setText(R.string.Contact_BIT);
    	} else {
    		ContactEntity contact = mDBO.querySchoolCalendarBySid(currentPid);
    		textViewTitle.setText(contact.getDepartment());
    	}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (currentPid == 0) {	
				long currentMillis = System.currentTimeMillis();
			
				if ((currentMillis - lastHitMillis) > 2000) {
				
					toast.setText(this.getText(R.string.Contact_MorePressExit));
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
					lastHitMillis = currentMillis;
				
				} else {
					this.finish();  
				}
			} else {
				ContactEntity contact = mDBO.querySchoolCalendarBySid(currentPid);
				changeCurrentPid(contact.getPid());
			}
			
			return true;
				
		}
		
		return super.onKeyDown(keyCode, event);

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//    	menu.add(0, MENU_REFRESH_ID, Menu.NONE, R.string.Contact_Menu_Refresh);
    	menu.add(0, MENU_FEEDBACK_ID, Menu.NONE, R.string.Contact_Menu_About);
    	menu.add(0, MENU_EXIT_ID, Menu.NONE, R.string.Contact_Menu_Exit);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
        switch (item.getItemId()) {
        case MENU_REFRESH_ID:
        	QueryContactListTask asyncTask = new QueryContactListTask();
	    	asyncTask.execute();
            break;     
        case 1:
        	Intent intent = new Intent();
    		intent.setClass(this, AboutActivity.class);
    		startActivity(intent);
            break;
        case 2:
        	this.finish();
            break;
        }
        return true;
    }
    
    private class ContactAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return ContactActivity.this.listContact.size();
		}

		@Override
		public Object getItem(int position) {
			return ContactActivity.this.listContact.get(position);
		}

		@Override
		public long getItemId(int position) {
			return ContactActivity.this.listContact.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			
			if(convertView == null)	{	
				
				convertView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.layout_contact_listitem, null);
				
				viewHolder = new ViewHolder();
				
				viewHolder.big = (TextView)convertView.findViewById(R.id.textView_Big);
				viewHolder.email = (TextView)convertView.findViewById(R.id.textView_Email);
				viewHolder.location = (TextView)convertView.findViewById(R.id.textView_Location);
				viewHolder.phone_number = (TextView)convertView.findViewById(R.id.textView_PhoneNumber);
				viewHolder.indicator = (TextView)convertView.findViewById(R.id.textView_Indicator);
				
				convertView.setTag(viewHolder);
				
			} else {
			
				viewHolder = (ViewHolder) convertView.getTag();
				
			}
			
			ContactEntity mEntity = (ContactEntity)getItem(position);
			
			if (mEntity.isIspeople()) {
				viewHolder.big.setText(mEntity.getPeople());
				viewHolder.indicator.setVisibility(View.GONE);
			} else {
				viewHolder.big.setText(mEntity.getDepartment());
				viewHolder.indicator.setVisibility(View.VISIBLE);
			}
			
			if (mEntity.getLocation().length() == 0) {
				viewHolder.location.setVisibility(View.GONE);
			} else {
				viewHolder.location.setVisibility(View.VISIBLE);
				viewHolder.location.setText(
						ContactActivity.this.getString(R.string.Contact_Location) + mEntity.getLocation());
			}
			
			if (mEntity.getPhone_number().length() == 0) {
				viewHolder.phone_number.setVisibility(View.GONE);
			} else {
				viewHolder.phone_number.setVisibility(View.VISIBLE);
				viewHolder.phone_number.setText(
						ContactActivity.this.getString(R.string.Contact_Phone) + mEntity.getPhone_number());
			}
			
			if (mEntity.getEmail().length() == 0) {
				viewHolder.email.setVisibility(View.GONE);
			} else {
				viewHolder.email.setVisibility(View.VISIBLE);
				viewHolder.email.setText(
						ContactActivity.this.getString(R.string.Contact_Email) + mEntity.getEmail());
			}
			
			return convertView;
		}
    	
		class ViewHolder {
			TextView big;
			TextView phone_number;
			TextView location;
			TextView email;
			TextView indicator;
		}
    }
    
    private class QueryContactListTask extends AsyncTask<Void, Void, NetworkStateEntity> {
		private UtilControl userInfoControl;
		private NetworkStateEntity networkState;
		private ProgressDialog progressDialog;
		private List<ContactEntity> list;
		
		@Override
		protected void onPreExecute() {
			userInfoControl = new UtilControl(ContactActivity.this);
			networkState = new NetworkStateEntity();
			
			this.progressDialog = ProgressDialog.show(
					ContactActivity.this,
					null,
					ContactActivity.this.getText(R.string.Contact_QueryContactList),
					true, 
					false
					);
			MobclickAgent.onEvent(ContactActivity.this, GlobalConstant.UMENG_CONTACT_REFRESH);
		}
		
		@Override
		protected NetworkStateEntity doInBackground(Void... arg) {
			
			list = userInfoControl.queryContactAll(networkState);
			
			return networkState;
		}
		
		protected void onPostExecute(NetworkStateEntity networkState) {
			
			if(networkState.getState().equals(NetworkStateEntity.OK)) {
				
				mDBO.deleteContactAll();
				
				for (int i = 0; i < list.size(); i++) {
					mDBO.insertContact(list.get(i));
				}
				
				listContact.clear();
				listContact.addAll(list);

				adapterContact.notifyDataSetChanged();
				
			} else if (networkState.getState().equals(NetworkStateEntity.SERVER_ERROR)) {
				
				toast.setText(ContactActivity.this.getString(R.string.ServerError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
					
			} else if (networkState.getState().equals(NetworkStateEntity.HTTP_ERROR)) {
				
				toast.setText(ContactActivity.this.getString(R.string.NetworkConnectionFailed));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			} else if (networkState.getState().equals(NetworkStateEntity.JSON_ERROR)) {
				
				toast.setText(ContactActivity.this.getText(R.string.JsonError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
				
			} else {
				toast.setText(ContactActivity.this.getString(R.string.UnknownError));
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
			
			this.progressDialog.dismiss();
		}
	}
}