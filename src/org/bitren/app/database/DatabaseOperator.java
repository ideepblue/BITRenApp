package org.bitren.app.database;

import java.util.ArrayList;
import java.util.List;

import org.bitren.app.entities.ContactEntity;
import org.bitren.app.entities.FavoriteContactEntity;
import org.bitren.app.provider.DatabaseProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DatabaseOperator {
	private static final String TAG = "Database.Operator";

	private Context context;
	private ContentResolver resolver;
	private ContentValues values;

	public DatabaseOperator(Context context) {
		this.context = context;
		resolver = this.context.getContentResolver();
	}
	
	public List<ContactEntity> queryContactAll() {
		Cursor cur;
		String whereClause = null;
		String[] whereArgs = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				null
				);
		
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		
		while (cur.moveToNext()) {
			ContactEntity contact = new ContactEntity();
			
			contact.setId(cur.getInt(cur.getColumnIndex(ContactsColumns._ID)));
			contact.setSid(cur.getInt(cur.getColumnIndex(ContactsColumns.SID)));
			contact.setPid(cur.getInt(cur.getColumnIndex(ContactsColumns.PID)));
			contact.setIspeople(cur.getInt(cur.getColumnIndex(ContactsColumns.ISPEOPLE)) > 0);
			contact.setDescription(cur.getString(cur.getColumnIndex(ContactsColumns.DESCRIPTION)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			contact.setComment(cur.getString(cur.getColumnIndex(ContactsColumns.COMMENT)));
			contact.setPhone(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}

	public ContactEntity queryContactBySid(int sid) {
		Cursor cur;
		String whereClause = ContactsColumns.SID + " = ?";
		String[] whereArgs = new String[] { Integer.toString(sid) };
		String sortOrder = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				sortOrder
				);
		
		if (cur.moveToFirst()) {
			
			ContactEntity result = new ContactEntity();
			
			result.setId(cur.getInt(cur.getColumnIndex(ContactsColumns._ID)));
			result.setSid(cur.getInt(cur.getColumnIndex(ContactsColumns.SID)));
			result.setPid(cur.getInt(cur.getColumnIndex(ContactsColumns.PID)));
			result.setIspeople(cur.getInt(cur.getColumnIndex(ContactsColumns.ISPEOPLE)) > 0);
			result.setDescription(cur.getString(cur.getColumnIndex(ContactsColumns.DESCRIPTION)));
			result.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			result.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			result.setComment(cur.getString(cur.getColumnIndex(ContactsColumns.COMMENT)));
			result.setPhone(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			
			cur.close();

			return result;
			
		} else {
			
			cur.close();
			
			return null;
			
		}
	}
	
	public List<ContactEntity> queryContactByPid(int pid) {
		Cursor cur;
		String whereClause = ContactsColumns.PID + " = ?";
		String[] whereArgs = new String[] { Integer.toString(pid) };
		String sortOrder = ContactsColumns.SID;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				sortOrder
				);
		
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		
		while (cur.moveToNext()) {
			ContactEntity contact = new ContactEntity();
			
			contact.setId(cur.getInt(cur.getColumnIndex(ContactsColumns._ID)));
			contact.setSid(cur.getInt(cur.getColumnIndex(ContactsColumns.SID)));
			contact.setPid(cur.getInt(cur.getColumnIndex(ContactsColumns.PID)));
			contact.setIspeople(cur.getInt(cur.getColumnIndex(ContactsColumns.ISPEOPLE)) > 0);
			contact.setDescription(cur.getString(cur.getColumnIndex(ContactsColumns.DESCRIPTION)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			contact.setComment(cur.getString(cur.getColumnIndex(ContactsColumns.COMMENT)));
			contact.setPhone(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}
	
	public boolean insertContact(ContactEntity contact) {

		values = new ContentValues();

		values.put(ContactsColumns.SID, 
				contact.getSid());
		values.put(ContactsColumns.PID, 
				contact.getPid());
		values.put(ContactsColumns.ISPEOPLE, 
				contact.isIspeople());
		values.put(ContactsColumns.DESCRIPTION,
				contact.getDescription());
		values.put(ContactsColumns.LOCATION,
				contact.getLocation());
		values.put(ContactsColumns.EMAIL,
				contact.getEmail());
		values.put(ContactsColumns.COMMENT,
				contact.getComment());
		values.put(ContactsColumns.PHONE,
				contact.getPhone());

		Uri uri = resolver.insert(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				values
				);

		if (!uri.equals(Uri.EMPTY)) {
			
			Log.v(TAG, "insert school_calendar into database succeed");
			contact.setId(Integer.parseInt(uri.getLastPathSegment()));
			return true;
			
		} else {
			
			Log.e(TAG, "insert school_calendar into database failed");
			return false;
			
		}
	}
	
	public int updateContactBySid(ContactEntity contact) {
		
		values = new ContentValues();

		values.put(ContactsColumns.PID, 
				contact.getPid());
		values.put(ContactsColumns.ISPEOPLE, 
				contact.isIspeople());
		values.put(ContactsColumns.DESCRIPTION,
				contact.getDescription());
		values.put(ContactsColumns.LOCATION,
				contact.getLocation());
		values.put(ContactsColumns.EMAIL,
				contact.getEmail());
		values.put(ContactsColumns.COMMENT,
				contact.getComment());
		values.put(ContactsColumns.PHONE,
				contact.getPhone());

		String whereClause = ContactsColumns.SID + " = ?";
		String[] whereArgs = new String[]{ Long.toString(contact.getSid()) };

		int affected = resolver.update(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				values, 
				whereClause,
				whereArgs
				);

		return affected;
	}
	
	public int deleteContactAll() {
		String whereClause = null;
		String[] whereArgs = null;
		
		int affected = resolver.delete(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactsColumns.TABLE_NAME), 
				whereClause, 
				whereArgs
				);
		
		return affected;
	}
	
	public List<FavoriteContactEntity> queryFavoriteContactAll() {
		Cursor cur;
		String whereClause = null;
		String[] whereArgs = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				null
				);
		
		List<FavoriteContactEntity> result = new ArrayList<FavoriteContactEntity>();
		
		while (cur.moveToNext()) {
			FavoriteContactEntity favoriteContact = new FavoriteContactEntity();
			
			favoriteContact.setId(cur.getInt(cur.getColumnIndex(FavoriteContactsColumns._ID)));
			favoriteContact.setContact_sid(cur.getInt(cur.getColumnIndex(FavoriteContactsColumns.CONTACT_SID)));
			favoriteContact.setName(cur.getString(cur.getColumnIndex(FavoriteContactsColumns.NAME)));
			favoriteContact.setContact(this.queryContactBySid(favoriteContact.getContact_sid()));
			
			result.add(favoriteContact);
		}
		cur.close();
		
		return result;
	}
	
	public List<ContactEntity> queryContactByFavorite() {
		Cursor cur;
		String whereClause = null;
		String[] whereArgs = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				null
				);
		
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		
		while (cur.moveToNext()) {
			
			ContactEntity contact = this.queryContactBySid(
					cur.getInt(cur.getColumnIndex(FavoriteContactsColumns.CONTACT_SID)));
			contact.setDescription(cur.getString(cur.getColumnIndex(FavoriteContactsColumns.NAME)));
			
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}
	
	public FavoriteContactEntity queryFavoriteContactByContactSid(int contact_sid) {
		Cursor cur;
		String whereClause = FavoriteContactsColumns.CONTACT_SID + " = ?";
		String[] whereArgs = { Integer.toString(contact_sid) };
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				null
				);
		
		FavoriteContactEntity result = new FavoriteContactEntity();
		
		if (cur.moveToFirst()) {
			
			result.setId(cur.getInt(cur.getColumnIndex(FavoriteContactsColumns._ID)));
			result.setContact_sid(cur.getInt(cur.getColumnIndex(FavoriteContactsColumns.CONTACT_SID)));
			result.setName(cur.getString(cur.getColumnIndex(FavoriteContactsColumns.NAME)));
			result.setContact(this.queryContactBySid(result.getContact_sid()));
			
			cur.close();
			
			return result;
			
			
		} else {
			
			cur.close();
			
			return null;
			
		}

	}

	public boolean insertFavoriteContact(FavoriteContactEntity favoriteContact) {

		values = new ContentValues();

		values.put(FavoriteContactsColumns.CONTACT_SID, 
				favoriteContact.getContact_sid());
		values.put(FavoriteContactsColumns.NAME, 
				favoriteContact.getName());

		Uri uri = resolver.insert(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				values
				);

		if (!uri.equals(Uri.EMPTY)) {
			
			favoriteContact.setId(Integer.parseInt(uri.getLastPathSegment()));
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	public int updateFavoriteContactById(FavoriteContactEntity favoriteContact) {
		
		values = new ContentValues();

		values.put(FavoriteContactsColumns.CONTACT_SID, 
			favoriteContact.getContact_sid());
		values.put(FavoriteContactsColumns.NAME, 
			favoriteContact.getName());

		String whereClause = FavoriteContactsColumns._ID + " = ?";
		String[] whereArgs = new String[]{ Long.toString(favoriteContact.getId()) };

		int affected = resolver.update(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				values, 
				whereClause,
				whereArgs
				);

		return affected;
	}

	public int deleteFavoriteContactByContactSid(int contact_sid) {
		String whereClause = FavoriteContactsColumns.CONTACT_SID + " = ?";
		String[] whereArgs = new String[]{ Integer.toString(contact_sid) };
		
		int affected = resolver.delete(
				Uri.parse(DatabaseProvider.CONTENT_URI + FavoriteContactsColumns.TABLE_NAME), 
				whereClause, 
				whereArgs
				);
		
		return affected;
	}
	
	public String queryTableTimestampByName(String name) {
		Cursor cur;
		String whereClause = TableTimestampColumns.NAME + " = ?";
		String[] whereArgs = new String[] { name };
		String sortOrder = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + TableTimestampColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				sortOrder
				);
		
		if (cur.moveToFirst()) {
			
			String result = cur.getString(cur.getColumnIndex(TableTimestampColumns.TIMESTAMP));
			
			cur.close();

			return result;
			
		} else {
			
			cur.close();
			
			return null;
			
		}
	}
	
	public int updateTableTimestampByName(String name, String timestamp) {
		
		values = new ContentValues();

		values.put(TableTimestampColumns.TIMESTAMP, timestamp);

		String whereClause = TableTimestampColumns.NAME + " = ?";
		String[] whereArgs = new String[]{ name };

		int affected = resolver.update(
				Uri.parse(DatabaseProvider.CONTENT_URI + TableTimestampColumns.TABLE_NAME), 
				values, 
				whereClause,
				whereArgs
				);

		return affected;
	} 

}
