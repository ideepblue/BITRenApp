package org.bitren.app.provider;

import org.bitren.app.database.ContactsColumns;
import org.bitren.app.database.DatabaseHelper;
import org.bitren.app.database.FavoriteContactsColumns;
import org.bitren.app.database.TableTimestampColumns;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {

	public static final String AUTHORITY = "org.bitren.app";
	public static final String CONTENT_URI = "content://" + DatabaseProvider.AUTHORITY + "/";
	
	private static final int CONTACTS = 0;
	private static final int FAVORITE_CONTACTS = 1;
	private static final int TABLE_TIMESTAMP = 2;
	
	private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);;
	
	static {
		//DatabaseProvider.URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		DatabaseProvider.URI_MATCHER.addURI(AUTHORITY, ContactsColumns.TABLE_NAME, CONTACTS);
		DatabaseProvider.URI_MATCHER.addURI(AUTHORITY, FavoriteContactsColumns.TABLE_NAME, FAVORITE_CONTACTS);
		DatabaseProvider.URI_MATCHER.addURI(AUTHORITY, TableTimestampColumns.TABLE_NAME, TABLE_TIMESTAMP);
	}
	
	SQLiteDatabase database;

	@Override
	public boolean onCreate() {
		DatabaseHelper dbHelper = new DatabaseHelper(this.getContext());
		this.database = dbHelper.getWritableDatabase();
		
		if (this.database == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		String table = null;
		int affected = 0;
		
		switch (DatabaseProvider.URI_MATCHER.match(uri)) {
		case CONTACTS:
		case FAVORITE_CONTACTS:
		case TABLE_TIMESTAMP:
			table = uri.getLastPathSegment();
			affected =  database.delete(table, whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return affected;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = null;
		Long id = new Long(0);
		
		switch (DatabaseProvider.URI_MATCHER.match(uri)) {
		case CONTACTS:
		case FAVORITE_CONTACTS:
		case TABLE_TIMESTAMP:
			table = uri.getLastPathSegment();
			id = database.insert(table, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}		
		
		if (id != -1) {
			return Uri.parse(uri + "/" + id.toString());
		} else {
			return Uri.EMPTY;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		String table = null;
		Cursor cursor = null;
		
		switch (DatabaseProvider.URI_MATCHER.match(uri)) {
		case CONTACTS:
		case FAVORITE_CONTACTS:
		case TABLE_TIMESTAMP:
			table = uri.getLastPathSegment();
			cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
		String table = null;
		int affected = 0;
		
		switch (DatabaseProvider.URI_MATCHER.match(uri)) {
		case CONTACTS:
		case FAVORITE_CONTACTS:
		case TABLE_TIMESTAMP:
			table = uri.getLastPathSegment();
			affected =  database.update(table, values, whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return affected;
	}

}
