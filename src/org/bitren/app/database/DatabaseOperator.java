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
			
			contact.setId(cur.getLong(cur.getColumnIndex(ContactColumns._ID)));
			contact.setSid(cur.getLong(cur.getColumnIndex(ContactColumns.SID)));
			contact.setDepartment(cur.getString(cur.getColumnIndex(ContactColumns.DEPARTMENT)));
			contact.setLocation(cur.getString(cur.getColumnIndex(ContactColumns.LOCATION)));
			contact.setPhone_number(cur.getString(cur.getColumnIndex(ContactColumns.PHONE_NUMBER)));
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
		values.put(ContactColumns.DEPARTMENT,
				contact.getDepartment());
		values.put(ContactColumns.LOCATION,
				contact.getLocation());
		values.put(ContactColumns.PEOPLE,
				contact.getPeople());
		values.put(ContactColumns.PHONE_NUMBER,
				contact.getPhone_number());

		Uri uri = resolver.insert(
				Uri.parse(DatabaseProvider.CONTENT_URI + ContactColumns.TABLE_NAME), 
				values
				);

		if (!uri.equals(Uri.EMPTY)) {
			
			Log.v(TAG, "insert school_calendar into database succeed");
			contact.setId(Long.parseLong(uri.getLastPathSegment()));
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
