package org.bitren.app;

import java.util.ArrayList;
import java.util.List;

import org.bitren.app.control.UtilControl;
import org.bitren.app.customview.MenuDialog;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.NetworkStateEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class ContactActivity extends Activity {
	
	private Toast toast;
	private TextView textViewDir;
	private ListView listViewContact;
	private ImageView imageViewBack;
	private ImageView imageViewMenu;
	
	private List<ContactEntity> listContact;
	private ContactAdapter adapterContact;
	
	private DatabaseOperator mDBO;
	
	private int currentPid;
	private static final int TAG_DAILPHONE = 0;
	private static final int TAG_SENDEMAIL = 1;
	private static final int TAG_ADDFAVORITE = 2;
	private static final int TAG_ADDTOCONTACTLIST = 3;
	private static final int TAG_FIXINFOMATION = 4;
	private static final int TAG_SENDBYMESSAGE = 5;
	
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
    	
    	textViewDir = (TextView)findViewById(R.id.textView_Contact_Dir);
    	
    	imageViewBack = (ImageView)findViewById(R.id.imageView_Contact_Back);
    	imageViewBack.setOnClickListener(
    			new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (currentPid == 0) {	
							// 是顶层目录 再按就退出了
							AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
							builder.setMessage(ContactActivity.this.getString(R.string.ExitApp))
			        	       	.setCancelable(false)
			        	       	.setPositiveButton(
			        	       			ContactActivity.this.getText(R.string.ok), 
			        	       				new DialogInterface.OnClickListener() {

			        	       					public void onClick(DialogInterface dialog, int id) {
			        	       						System.exit(0);
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
							// 非顶层目录 返回上一层目录
							ContactEntity contact = mDBO.querySchoolCalendarBySid(currentPid);
							changeCurrentPid(contact.getPid());
						}
						
					}
				}
    		);
    	
    	imageViewMenu = (ImageView)findViewById(R.id.imageView_Contact_Menu);
    	imageViewMenu.setOnClickListener(
    			new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MenuDialog menuDialog = new MenuDialog(ContactActivity.this);
						Dialog dialog = new Dialog(ContactActivity.this);
						View content = menuDialog.getView();

						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(content);
						dialog.setCancelable(true);
						dialog.setCanceledOnTouchOutside(true);
						WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
						int windowWidth = ContactActivity.this.getWindowManager().getDefaultDisplay().getWidth();
						int windowHeight = ContactActivity.this.getWindowManager().getDefaultDisplay().getHeight();
//						int statusbarHeight = ContactActivity.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
//						Rect outRect = new Rect();
//						ContactActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
//						int statusbarHeight = outRect.top;
						int anchorBottom = imageViewMenu.getBottom();
						int anchorRight = imageViewMenu.getRight();
						DisplayMetrics displayMetrics = ContactActivity.this.getApplicationContext().getResources().getDisplayMetrics();;
						float density = displayMetrics.density;
						
						int width = windowWidth / 2 - (windowWidth - anchorRight) - (int)(200 * density) / 2;
						int height = windowHeight / 2 - (anchorBottom) - (int)(100 * density) / 2 - (int)(20 * density);
						lp.x = width;
						lp.y = -height;
						
						menuDialog.setDialog(dialog);
						dialog.show();
					}
				}
    		);
    	
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
											
			        	       						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tel));
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

    	listViewContact.setOnItemLongClickListener(
    			new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						final ContactEntity contact = listContact.get(position);
						String title;
						StringBuilder item = new StringBuilder();
						final List<Integer> itemTag = new ArrayList<Integer>(); // 用于判断这个item的点击类型
						
						//根据是否为叶子 来添加长按时候的菜单
						if (contact.isIspeople()) {
							title = contact.getPeople();
							
							item.append(ContactActivity.this.getString(R.string.Contact_DailPhone) + ";");
							itemTag.add(TAG_DAILPHONE);
							
							if (contact.getEmail().length() != 0) {
								item.append(ContactActivity.this.getString(R.string.Contact_SendEmail) + ";");
								itemTag.add(TAG_SENDEMAIL);
							}
							
							item.append(ContactActivity.this.getString(R.string.Contact_AddFavorite) + ";");
							itemTag.add(TAG_ADDFAVORITE);
							
							item.append(ContactActivity.this.getString(R.string.Contact_AddToContactList) + ";");
							itemTag.add(TAG_ADDTOCONTACTLIST);
							
							item.append(ContactActivity.this.getString(R.string.Contact_SendByMessage) + ";");
							itemTag.add(TAG_SENDBYMESSAGE);
							
							item.append(ContactActivity.this.getString(R.string.Contact_FixInfomation) + ";");
							itemTag.add(TAG_FIXINFOMATION);
							
						} else {
							title = contact.getDepartment();
							
							if (contact.getEmail().length() != 0) {
								item.append(ContactActivity.this.getString(R.string.Contact_SendEmail) + ";");
								itemTag.add(TAG_SENDEMAIL);
							}
							
							item.append(ContactActivity.this.getString(R.string.Contact_FixInfomation) + ";");
							itemTag.add(TAG_FIXINFOMATION);
						}
						
						AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
						builder.setTitle(title);
						builder.setItems(
								item.toString().split(";"), 
								new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent;
										ContactEntity parent = mDBO.querySchoolCalendarBySid(currentPid);
										switch (itemTag.get(which)) {
										case TAG_DAILPHONE:
			        	       				String tel = "tel:" + contact.getPhone_number();
	        	       						intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tel));
	        	       						ContactActivity.this.startActivity(intent);
											break;
										case TAG_SENDEMAIL:
											String[] tos = { contact.getEmail() };
											intent = new Intent(Intent.ACTION_SEND);
											intent.putExtra(Intent.EXTRA_EMAIL, tos);
											intent.setType("message/rfc882");
											ContactActivity.this.startActivity(intent);
											break;
										case TAG_ADDFAVORITE:
											break;
										case TAG_ADDTOCONTACTLIST:
											intent = new Intent(Intent.ACTION_INSERT);
					                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					                        intent.putExtra(ContactsContract.Intents.Insert.NAME, parent.getDepartment() + " " + contact.getPeople());
					                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhone_number());
					                        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
					                        if (contact.getEmail().length() != 0) {
					                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
					                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
					                        }
					                        ContactActivity.this.startActivity(intent);
											break;
										case TAG_FIXINFOMATION:
											break;
										case TAG_SENDBYMESSAGE:
	        	       						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"));
	        	       						String body = parent.getDepartment() + " " + contact.getPeople() + "\n"
	        	       										+ ContactActivity.this.getString(R.string.Contact_Phone) + contact.getPhone_number();
	        	       						if (contact.getEmail().length() != 0) {
	        	       							body = body + ContactActivity.this.getString(R.string.Contact_Email) + contact.getEmail();
	        	       						}
	        	       						if (contact.getLocation().length() != 0) {
	        	       							body = body + ContactActivity.this.getString(R.string.Contact_Location) + contact.getLocation();
	        	       						}
	        	       						intent.putExtra("sms_body", body);
	        	       						ContactActivity.this.startActivity(intent);
											break;
										}
										
									}
								}
							);
						
						AlertDialog dialog = builder.create();
						dialog.show();
						
						return true;
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
    		textViewDir.setText("> " + this.getString(R.string.Contact_BIT));
    	} else {
    		ContactEntity contact = mDBO.querySchoolCalendarBySid(currentPid);
    		textViewDir.setText("> " + contact.getDepartment());
    	}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			
			imageViewBack.performClick();
			return true;
				
		}
		
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
			imageViewMenu.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);

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