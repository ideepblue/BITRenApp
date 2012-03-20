package org.bitren.app;

import java.util.ArrayList;
import java.util.List;

import org.bitren.app.customview.MenuDialog;
import org.bitren.app.database.DatabaseOperator;
import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.FavoriteContactEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class ContactActivity extends Activity {
	
	private final static String TAG = "ContactActivity";
	
	private Toast toast;
	private TextView textViewDir;
	private ListView listViewContact;
	private ImageView imageViewBack;
	private ImageView imageViewMenu;
	
	private List<ContactEntity> listContact;
	private ContactAdapter adapterContact;
	
	private DatabaseOperator mDBO;
	
	private long lastHitMillis;
	
	private int currentPid;
	private static final int PID_FAVORITE = 2;
	private static final int TAG_DAILPHONE = 0;
	private static final int TAG_SENDEMAIL = 1;
	private static final int TAG_ADDFAVORITE = 2;
	private static final int TAG_ADDTOCONTACTLIST = 3;
	private static final int TAG_FIXINFOMATION = 4;
	private static final int TAG_SENDBYMESSAGE = 5;
	private static final int TAG_CHANGENAME = 6;
	private static final int TAG_REMOVEFAVORITE = 7;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	MobclickAgent.setUpdateOnlyWifi(false);
		MobclickAgent.update(this);
//		MobclickAgent.updateAutoPopup = false; 自动更新是否弹出框
//		MobclickAgent.setUpdateListener(
//				new UmengUpdateListener() {
//
//					@Override
//					public void onUpdateReturned(int updateStatus) {
//						Log.v(TAG, "UpdateStatus = " + updateStatus);
//						switch(updateStatus) {
//						case 0://有更新
//						case 1://无更新
//						case 2://非wifi状态
//						case 3://请求超时
//						}
//					}
//					
//				}
//			);
		
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
    	
    	textViewDir = (TextView)findViewById(R.id.textView_Dir);
    	
    	imageViewBack = (ImageView)findViewById(R.id.imageView_Back);
    	imageViewBack.setOnClickListener(
    			new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (currentPid == 1) {	
							// 是顶层目录 再按就退出了
							long currentMillis = System.currentTimeMillis();
			
							if ((currentMillis - lastHitMillis) > 2000) {
				
								toast.setText(ContactActivity.this.getText(R.string.MorePressExit));
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
								lastHitMillis = currentMillis;
				
							} else {
								ContactActivity.this.finish();  
							}
							
						} else {
							// 非顶层目录 返回上一层目录
							ContactEntity contact = mDBO.queryContactBySid(currentPid);
							changeCurrentPid(contact.getPid());
						}
						
					}
				}
    		);
    	
    	imageViewMenu = (ImageView)findViewById(R.id.imageView_Menu);
    	imageViewMenu.setOnClickListener(
    			new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MenuDialog menuDialog = new MenuDialog(ContactActivity.this);
						Dialog dialog = new Dialog(ContactActivity.this, R.style.MenuDialog);
						View content = menuDialog.getView();

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
						int anchorLeft = imageViewMenu.getLeft();
						DisplayMetrics displayMetrics = ContactActivity.this.getApplicationContext().getResources().getDisplayMetrics();;
						float density = displayMetrics.density;
						
						int width = windowWidth / 2 - anchorLeft - (int)(200 * density) / 2;
						int height = windowHeight / 2 - (anchorBottom) - (int)((100 + 20) * density) / 2;
						lp.x = -width;
						lp.y = -height;
						
						menuDialog.setDialog(dialog);
						dialog.show();
					}
				}
    		);
    	
    	listViewContact = (ListView)findViewById(R.id.listView);
    	/*listViewContact.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						ContactEntity contact = ContactActivity.this.listContact.get(position);
						
						if (contact.isIspeople()) {
							final String phoneNumber = contact.getPhone();
						
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
							title = contact.getDescription();
							
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
							title = contact.getDescription();
							
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
										ContactEntity parent = mDBO.queryContactBySid(currentPid);
										switch (itemTag.get(which)) {
										case TAG_DAILPHONE:
			        	       				String tel = "tel:" + contact.getPhone();
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
											ContactActivity.this.addFavorite(contact, parent);
											break;
										case TAG_ADDTOCONTACTLIST:
											intent = new Intent(Intent.ACTION_INSERT);
					                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					                        intent.putExtra(ContactsContract.Intents.Insert.NAME, parent.getDescription() + " " + contact.getDescription());
					                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhone());
					                        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
					                        if (contact.getEmail().length() != 0) {
					                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
					                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
					                        }
					                        intent.putExtra(ContactsContract.Intents.Insert.NOTES, contact.getComment());
					                        ContactActivity.this.startActivity(intent);
											break;
										case TAG_FIXINFOMATION:
											break;
										case TAG_SENDBYMESSAGE:
	        	       						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"));
	        	       						String body = parent.getDescription() + " " + contact.getDescription() + "\n"
	        	       										+ ContactActivity.this.getString(R.string.Contact_Phone) + contact.getPhone() + "\n";
	        	       						if (contact.getEmail().length() != 0) {
	        	       							body = body + ContactActivity.this.getString(R.string.Contact_Email) + contact.getEmail() + "\n";
	        	       						}
	        	       						if (contact.getLocation().length() != 0) {
	        	       							body = body + ContactActivity.this.getString(R.string.Contact_Location) + contact.getLocation() + "\n";
	        	       						}
	        	       						if (contact.getComment().length() != 0) {
	        	       							body = body + ContactActivity.this.getString(R.string.Contact_Comment) + contact.getComment() + "\n";
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
    		);*/
    }
    
    private void initData() {
    	mDBO = new DatabaseOperator(this);
    	
    	listContact = new ArrayList<ContactEntity>();
    	
    	adapterContact = new ContactAdapter();
    	listViewContact.setAdapter(adapterContact);
    	
    	changeCurrentPid(1);
    }
    
    private void changeCurrentPid(int pid) {
    	currentPid = pid;
    	listContact.clear();
    	if (pid != PID_FAVORITE) {
    		listContact.addAll( mDBO.queryContactByPid(currentPid) );
    	} else {
    		listContact.addAll( mDBO.queryContactByFavorite() );
    	}
    	adapterContact.notifyDataSetChanged();
    	if (listContact.size() != 0) {
    		listViewContact.setSelection(0);
    	}
    	ContactEntity contact = mDBO.queryContactBySid(currentPid);
    	textViewDir.setText(contact.getDescription());
    }
    
    private void addFavorite(final ContactEntity contact, ContactEntity parent) {
    	FavoriteContactEntity favoriteContact = mDBO.queryFavoriteContactByContactSid(contact.getSid());
		if (favoriteContact != null) {
			toast.setText(ContactActivity.this.getString(R.string.FavoriteContact_HasAdd));
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		} else {
			// 输入显示名称
			final EditText editTextName = new EditText(this);
			editTextName.setText(parent.getDescription() + "-" + contact.getDescription());
			Selection.setSelection(editTextName.getEditableText(), editTextName.getEditableText().toString().length());
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getText(R.string.FavoriteContact_DisplayName))
				   .setView(editTextName)
				   .setCancelable(false)
				   .setPositiveButton(
        	    		   this.getText(R.string.ok), 
        	    		   new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   FavoriteContactEntity favoriteContact = new FavoriteContactEntity();
		        	        	   favoriteContact.setContact_sid(contact.getSid());
		        	        	   favoriteContact.setName(editTextName.getEditableText().toString());
		        	        	   favoriteContact.setContact(contact);
		        	        	   mDBO.insertFavoriteContact(favoriteContact);
		        	        	   if (favoriteContact.getId() != 0) {
		        	        		   toast.setText(ContactActivity.this.getString(R.string.FavoriteContact_AddSucceed));
		        	        		   toast.setDuration(Toast.LENGTH_SHORT);
		        	        		   toast.show();
		        	        	   } else {
		        	        		   toast.setText(ContactActivity.this.getString(R.string.FavoriteContact_AddFailed));	
		        	        		   toast.setDuration(Toast.LENGTH_SHORT);
		        	        		   toast.show();
		        	        	   }
		        	           }
        	    		   }
        	           )
        	       .setNegativeButton(
        	    		   this.getText(R.string.cancel),
        	    		   null
        	           );
			AlertDialog dialog = builder.create();
			dialog.show();
		}
    }
    
    private void deleteFavoriteContact(final ContactEntity contact) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getText(R.string.FavoriteContact_RemoveConfirm))
			   .setCancelable(false)
			   .setPositiveButton(
    	    		   this.getText(R.string.ok), 
    	    		   new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   mDBO.deleteFavoriteContactByContactSid(contact.getSid());
	        	        	   listContact.remove(contact);
	        	        	   adapterContact.notifyDataSetChanged();
	        	           }
    	    		   }
    	           )
    	       .setNegativeButton(
    	    		   this.getText(R.string.cancel),
    	    		   null
    	           );
		AlertDialog dialog = builder.create();
		dialog.show();
    }
    
    private void changeNameOfFavoriteContact(final ContactEntity contact) {
    	final FavoriteContactEntity favoriteContact = mDBO.queryFavoriteContactByContactSid(contact.getSid());
    	final EditText editTextName = new EditText(this);
		editTextName.setText(favoriteContact.getName());
		Selection.setSelection(editTextName.getEditableText(), editTextName.getEditableText().toString().length());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getText(R.string.FavoriteContact_DisplayName))
			   .setView(editTextName)
			   .setCancelable(false)
			   .setPositiveButton(
    	    		   this.getText(R.string.ok), 
    	    		   new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   contact.setDescription(editTextName.getEditableText().toString());
	        	        	   favoriteContact.setName(contact.getDescription());
	        	        	   mDBO.updateFavoriteContactById(favoriteContact);
	        	        	   adapterContact.notifyDataSetChanged();
	        	           }
    	    		   }
    	           )
    	       .setNegativeButton(
    	    		   this.getText(R.string.cancel),
    	    		   null
    	           );
		AlertDialog dialog = builder.create();
		dialog.show();
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
				
				viewHolder.information = (RelativeLayout)convertView.findViewById(R.id.relativeLayout_Information);
				viewHolder.information.setOnClickListener(onClickListenerInfomation);
				viewHolder.description = (TextView)convertView.findViewById(R.id.textView_Description);
				viewHolder.email = (TextView)convertView.findViewById(R.id.textView_Email);
				viewHolder.location = (TextView)convertView.findViewById(R.id.textView_Location);
				viewHolder.comment = (TextView)convertView.findViewById(R.id.textView_Comment);
				viewHolder.phone = (TextView)convertView.findViewById(R.id.textView_Phone);
				viewHolder.indicator = (ImageView)convertView.findViewById(R.id.imageView_Indicator);
				viewHolder.indicator.setOnClickListener(onClickListenerIndicator);
				
				convertView.setTag(viewHolder);
				
			} else {
			
				viewHolder = (ViewHolder) convertView.getTag();
				
			}
			
			ContactEntity mEntity = (ContactEntity)getItem(position);
			
			viewHolder.indicator.setTag((Integer)position);
			viewHolder.information.setTag((Integer)position);
			viewHolder.description.setText(mEntity.getDescription());
			
			if (mEntity.isIspeople()) {
				viewHolder.indicator.setImageDrawable(ContactActivity.this.getResources().getDrawable(R.drawable.button_phone));
			} else {
				viewHolder.indicator.setImageDrawable(ContactActivity.this.getResources().getDrawable(R.drawable.triangle_right));
			}
			
			if (mEntity.getLocation().length() == 0) {
				viewHolder.location.setVisibility(View.GONE);
			} else {
				viewHolder.location.setVisibility(View.VISIBLE);
				viewHolder.location.setText(
						ContactActivity.this.getString(R.string.Contact_Location) + mEntity.getLocation());
			}
			
			if (mEntity.getEmail().length() == 0) {
				viewHolder.email.setVisibility(View.GONE);
			} else {
				viewHolder.email.setVisibility(View.VISIBLE);
				viewHolder.email.setText(
						ContactActivity.this.getString(R.string.Contact_Email) + mEntity.getEmail());
			}
			
			if (mEntity.getComment().length() == 0) {
				viewHolder.comment.setVisibility(View.GONE);
			} else {
				viewHolder.comment.setVisibility(View.VISIBLE);
				viewHolder.comment.setText(
						ContactActivity.this.getString(R.string.Contact_Comment) + mEntity.getComment());
			}
			
			if (mEntity.getPhone().length() == 0) {
				viewHolder.phone.setVisibility(View.GONE);
			} else {
				viewHolder.phone.setVisibility(View.VISIBLE);
				viewHolder.phone.setText(
						ContactActivity.this.getString(R.string.Contact_Phone) + mEntity.getPhone());
			}
			
			return convertView;
		}
    	
		class ViewHolder {
			RelativeLayout information;
			TextView description;
			TextView comment;
			TextView phone;
			TextView location;
			TextView email;
			ImageView indicator;
		}
    }
    
    private View.OnClickListener onClickListenerIndicator = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			ContactEntity contact = ContactActivity.this.listContact.get(position);
			
			if (contact.isIspeople()) {
				final String phoneNumber = contact.getPhone();
			
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
	};
	
	private View.OnClickListener onClickListenerInfomation = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			final ContactEntity contact = listContact.get(position);
			String title;
			StringBuilder item = new StringBuilder();
			final List<Integer> itemTag = new ArrayList<Integer>(); // 用于判断这个item的点击类型
			
			//根据是否为叶子 来添加长按时候的菜单
			if (contact.isIspeople()) {
				title = contact.getDescription();
				
				item.append(ContactActivity.this.getString(R.string.Contact_DailPhone) + ";");
				itemTag.add(TAG_DAILPHONE);
				
				if (contact.getEmail().length() != 0) {
					item.append(ContactActivity.this.getString(R.string.Contact_SendEmail) + ";");
					itemTag.add(TAG_SENDEMAIL);
				}
				
				if (currentPid != PID_FAVORITE) {
					
					item.append(ContactActivity.this.getString(R.string.Contact_AddFavorite) + ";");
					itemTag.add(TAG_ADDFAVORITE);
					
				} else {
					
					item.append(ContactActivity.this.getString(R.string.FavoriteContact_ChangeName) + ";");
					itemTag.add(TAG_CHANGENAME);
				
					item.append(ContactActivity.this.getString(R.string.FavoriteContact_RemoveFavorite) + ";");
					itemTag.add(TAG_REMOVEFAVORITE);
				}
				
				item.append(ContactActivity.this.getString(R.string.Contact_AddToContactList) + ";");
				itemTag.add(TAG_ADDTOCONTACTLIST);
				
				item.append(ContactActivity.this.getString(R.string.Contact_SendByMessage) + ";");
				itemTag.add(TAG_SENDBYMESSAGE);
				
				if (currentPid != PID_FAVORITE) {
					item.append(ContactActivity.this.getString(R.string.Contact_FixInfomation) + ";");
					itemTag.add(TAG_FIXINFOMATION);
				}
				
			} else {
				title = contact.getDescription();
				
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
							ContactEntity parent = mDBO.queryContactBySid(currentPid);
							switch (itemTag.get(which)) {
							case TAG_DAILPHONE:
        	       				String tel = "tel:" + contact.getPhone();
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
								ContactActivity.this.addFavorite(contact, parent);
								break;
							case TAG_ADDTOCONTACTLIST:
								intent = new Intent(Intent.ACTION_INSERT);
		                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		                        intent.putExtra(ContactsContract.Intents.Insert.NAME, parent.getDescription() + " " + contact.getDescription());
		                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhone());
		                        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
		                        if (contact.getEmail().length() != 0) {
		                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
		                        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
		                        }
		                        intent.putExtra(ContactsContract.Intents.Insert.NOTES, contact.getComment());
		                        ContactActivity.this.startActivity(intent);
								break;
							case TAG_FIXINFOMATION:
								intent = new Intent();
								intent.setClass(ContactActivity.this, FeedbackActivity.class);
								intent.putExtra("sid", contact.getSid());
		                        ContactActivity.this.startActivity(intent);
								break;
							case TAG_SENDBYMESSAGE:
	       						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"));
	       						String body;
	       						if (currentPid != PID_FAVORITE) {
	       							body = parent.getDescription() + "-" + contact.getDescription() + "\n"
	       										+ ContactActivity.this.getString(R.string.Contact_Phone) + contact.getPhone() + "\n";
	       						} else {
	       							body = contact.getDescription() + "\n"
	       										+ ContactActivity.this.getString(R.string.Contact_Phone) + contact.getPhone() + "\n";
	       						}
	       						if (contact.getEmail().length() != 0) {
	       							body = body + ContactActivity.this.getString(R.string.Contact_Email) + contact.getEmail() + "\n";
	       						}
	       						if (contact.getLocation().length() != 0) {
	       							body = body + ContactActivity.this.getString(R.string.Contact_Location) + contact.getLocation() + "\n";
	       						}
	       						if (contact.getComment().length() != 0) {
	       							body = body + ContactActivity.this.getString(R.string.Contact_Comment) + contact.getComment() + "\n";
	       						}
	       						intent.putExtra("sms_body", body);
	       						ContactActivity.this.startActivity(intent);
								break;
							case TAG_CHANGENAME:
								ContactActivity.this.changeNameOfFavoriteContact(contact);
								break;
							case TAG_REMOVEFAVORITE:
								ContactActivity.this.deleteFavoriteContact(contact);
								break;
							}
							
						}
					}
				);
			
			AlertDialog dialog = builder.create();
			dialog.show();			
		}
	};
    /*
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
			MobclickAgent.onEvent(ContactActivity.this, GlobalConstant.UMENG_REFRESH);
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
	}*/
}