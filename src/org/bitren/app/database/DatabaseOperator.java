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
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				null
				);
		
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		
		while (cur.moveToNext()) {
			ContactEntity contact = new ContactEntity();
			
			contact.setId(cur.getInt(cur.getColumnIndex(ContactColumns._ID)));
			contact.setSid(cur.getInt(cur.getColumnIndex(ContactColumns.SID)));
			contact.setPid(cur.getInt(cur.getColumnIndex(ContactColumns.PID)));
			contact.setIspeople(cur.getInt(cur.getColumnIndex(ContactColumns.ISPEOPLE)) > 0);
			contact.setDepartment(cur.getString(cur.getColumnIndex(ContactColumns.DEPARTMENT)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactColumns.EMAIL)));
			contact.setPhone_number(cur.getString(cur.getColumnIndex(ContactColumns.PHONE)));
			contact.setPeople(cur.getString(cur.getColumnIndex(ContactColumns.PEOPLE)));
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}

	public ContactEntity querySchoolCalendarBySid(int sid) {
		Cursor cur;
		String whereClause = ContactColumns.SID + " = ?";
		String[] whereArgs = new String[] { Integer.toString(sid) };
		String sortOrder = null;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				sortOrder
				);
		
		if (cur.moveToFirst()) {
			
			ContactEntity result = new ContactEntity();
			
			result.setId(cur.getInt(cur.getColumnIndex(ContactColumns._ID)));
			result.setSid(cur.getInt(cur.getColumnIndex(ContactColumns.SID)));
			result.setPid(cur.getInt(cur.getColumnIndex(ContactColumns.PID)));
			result.setIspeople(cur.getInt(cur.getColumnIndex(ContactColumns.ISPEOPLE)) > 0);
			result.setDepartment(cur.getString(cur.getColumnIndex(ContactColumns.DEPARTMENT)));
			result.setLocation(cur.getString(cur.getColumnIndex(ContactColumns.LOCATION)));
			result.setEmail(cur.getString(cur.getColumnIndex(ContactColumns.EMAIL)));
			result.setPhone_number(cur.getString(cur.getColumnIndex(ContactColumns.PHONE)));
			result.setPeople(cur.getString(cur.getColumnIndex(ContactColumns.PEOPLE)));
			
			cur.close();

			return result;
			
		} else {
			
			cur.close();
			
			return null;
			
		}
	}
	public List<ContactEntity> querySchoolCalendarByPid(int pid) {
		Cursor cur;
		String whereClause = ContactColumns.PID + " = ?";
		String[] whereArgs = new String[] { Integer.toString(pid) };
		String sortOrder = ContactColumns.SID;
		
		cur = resolver.query(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
				null, 
				whereClause, 
				whereArgs, 
				sortOrder
				);
		
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		
		while (cur.moveToNext()) {
			ContactEntity contact = new ContactEntity();
			
			contact.setId(cur.getInt(cur.getColumnIndex(ContactColumns._ID)));
			contact.setSid(cur.getInt(cur.getColumnIndex(ContactColumns.SID)));
			contact.setPid(cur.getInt(cur.getColumnIndex(ContactColumns.PID)));
			contact.setIspeople(cur.getInt(cur.getColumnIndex(ContactColumns.ISPEOPLE)) > 0);
			contact.setDepartment(cur.getString(cur.getColumnIndex(ContactColumns.DEPARTMENT)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactColumns.LOCATION)));
			contact.setEmail(cur.getString(cur.getColumnIndex(ContactColumns.EMAIL)));
			contact.setPhone_number(cur.getString(cur.getColumnIndex(ContactColumns.PHONE)));
			contact.setPeople(cur.getString(cur.getColumnIndex(ContactColumns.PEOPLE)));
			
			result.add(contact);
		}
		cur.close();
		
		return result;
	}
	
	public boolean insertContact(ContactEntity contact) {

		values = new ContentValues();

		values.put(ContactColumns.SID, 
				contact.getSid());
		values.put(ContactColumns.PID, 
				contact.getPid());
		values.put(ContactColumns.ISPEOPLE, 
				contact.isIspeople());
		values.put(ContactColumns.DEPARTMENT,
				contact.getDepartment());
		values.put(ContactColumns.LOCATION,
				contact.getLocation());
		values.put(ContactColumns.EMAIL,
				contact.getEmail());
		values.put(ContactColumns.PEOPLE,
				contact.getPeople());
		values.put(ContactColumns.PHONE,
				contact.getPhone_number());

		Uri uri = resolver.insert(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
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
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
				whereClause, 
				whereArgs
				);
		
		return affected;
	}
}
