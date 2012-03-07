package org.bitren.app;

import java.util.List;

import org.bitren.app.control.UtilControl;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;

import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ContactActivity extends Activity {
	
	private Toast toast;
	private ListView listViewContact;
	
	private List<ContactEntity> listContact;
	private ContactAdapter adapterContact;
	
	private DatabaseOperator mDBO;
	
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
    	
    	listViewContact = (ListView)findViewById(R.id.listView_Contact);
    	listViewContact.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						final String phoneNumber = ContactActivity.this.listContact.get(position).getPhone_number();
						
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
					}
				}
			);
    	
    }
    
    private void initData() {
    	mDBO = new DatabaseOperator(this);
    	listContact = mDBO.querySchoolCalendarAll();
    	adapterContact = new ContactAdapter();
    	listViewContact.setAdapter(adapterContact);
    	
    	if (listContact.size() == 0) {
	    	QueryContactListTask asyncTask = new QueryContactListTask();
	    	asyncTask.execute();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_REFRESH_ID, Menu.NONE, R.string.Contact_Menu_Refresh);
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
				
				viewHolder.department = (TextView)convertView.findViewById(R.id.textView_Department);
				viewHolder.people = (TextView)convertView.findViewById(R.id.textView_People);
				viewHolder.phone_number = (TextView)convertView.findViewById(R.id.textView_PhoneNumber);
				viewHolder.location = (TextView)convertView.findViewById(R.id.textView_Location);
				
				convertView.setTag(viewHolder);
				
			} else {
				
				viewHolder = (ViewHolder) convertView.getTag();
				
			}
			
			ContactEntity mEntity = (ContactEntity)getItem(position);
			
			viewHolder.department.setText(mEntity.getDepartment());
			viewHolder.people.setText(mEntity.getPeople());
			viewHolder.phone_number.setText(mEntity.getPhone_number());
			viewHolder.location.setText(mEntity.getLocation());
			
			return convertView;
		}
    	
		class ViewHolder {
			TextView department;
			TextView people;
			TextView phone_number;
			TextView location;
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