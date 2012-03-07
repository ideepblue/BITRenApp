package org.bitren.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "course365";
	
	private static final String[] TABLE_CREATE = {
		
		"create table if not exists "
		+ ContactColumns.TABLE_NAME + "("
		+ ContactColumns._ID + " integer primary key autoincrement, "
		+ ContactColumns.SID + " integer, "
		+ ContactColumns.DEPARTMENT + " text, "
		+ ContactColumns.LOCATION + " text, "
		+ ContactColumns.PEOPLE + " text, "
		+ ContactColumns.PHONE_NUMBER + " text "
		+ ");"
		
	};
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (int i = 0; i < TABLE_CREATE.length; i++) {
			db.execSQL(TABLE_CREATE[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
