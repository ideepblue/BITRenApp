package org.bitren.app.database;

import java.io.IOException;
import java.io.InputStream;

import org.bitren.app.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "bitren.sqlite";
	private Context context;
	
	private static final String[] TABLE_CREATE = {
	};
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.v("dbHelper", "create");
		String text;
        
        	//InputStream is = this.getResources().openRawResource (R.raw.sql);
        try { 
        	InputStream is = context.getResources().openRawResource (R.raw.database);

        	// We guarantee that the available method returns the total
        	// size of the asset... of course, this does mean that a single
        	// asset can't be more than 2 gigs.
        	int size = is.available();

        	// Read the entire asset into a local byte buffer.
        	byte[] buffer = new byte[size];
        	is.read(buffer);
        	is.close();

        	// Convert the buffer into a string.
        	text = new String(buffer);
       
   
        } catch (IOException e) {
          // Should never happen!
          throw new RuntimeException(e);
        }
        
        text = text.replaceAll("\\n", "");
        text = text.replaceAll("\\r", "");
        String[] str = text.split(";");
        for (String s : str) {

            db.execSQL(s + ";");
           
        }
        
//        for (String s : TABLE_CREATE) {

//            db.execSQL(s);
           
//        }
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion < 3) {
			onCreate(db);
		}
	}

}
