package org.bitren.app.database;

import java.util.ArrayList;
import java.util.List;

import org.bitren.app.entities.ContactEntity;
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
	
	public List<ContactEntity> querySchoolCalendarAll() {
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
			contact.setDepartment(cur.getString(cur.getColumnIndex(ContactsColumns.DEPARTMENT)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			contact.setPhone_number(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			contact.setPeople(cur.getString(cur.getColumnIndex(ContactsColumns.PEOPLE)));
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}

	public ContactEntity querySchoolCalendarBySid(int sid) {
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
			result.setDepartment(cur.getString(cur.getColumnIndex(ContactsColumns.DEPARTMENT)));
			result.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			result.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			result.setPhone_number(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			result.setPeople(cur.getString(cur.getColumnIndex(ContactsColumns.PEOPLE)));
			
			cur.close();

			return result;
			
		} else {
			
			cur.close();
			
			return null;
			
		}
	}
	public List<ContactEntity> querySchoolCalendarByPid(int pid) {
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
			contact.setDepartment(cur.getString(cur.getColumnIndex(ContactsColumns.DEPARTMENT)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactsColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactsColumns.EMAIL)));
			contact.setPhone_number(cur.getString(cur.getColumnIndex(ContactsColumns.PHONE)));
			contact.setPeople(cur.getString(cur.getColumnIndex(ContactsColumns.PEOPLE)));
			
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
		values.put(ContactsColumns.DEPARTMENT,
				contact.getDepartment());
		values.put(ContactsColumns.LOCATION,
				contact.getLocation());
		values.put(ContactsColumns.EMAIL,
				contact.getEmail());
		values.put(ContactsColumns.PEOPLE,
				contact.getPeople());
		values.put(ContactsColumns.PHONE,
				contact.getPhone_number());

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
}
