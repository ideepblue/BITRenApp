package org.bitren.app.database;

import java.io.IOException;
import java.io.InputStream;

import org.bitren.app.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "bitren.sqlite";
	private Context context;
	
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
        	InputStream is = context.getResources().openRawResource (R.raw.contacts);

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
        String[] str = text.split(";");
        for (String s : str) {

            db.execSQL(s + ";");
           
        }
		/*
		File file = null;
		File dir = null;
	
		dir = new File("data/data/" + context.getPackageName() + "/databases");
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		
		file = new File(dir, DATABASE_NAME);
		if(file.exists()) {
			file.delete();
		}
		if(!file.exists()) {
			Log.i(DATABASE_NAME, "Uploading DatabaseFile");
			
			InputStream dbInputStream = context.getResources().openRawResource(R.raw.bitren);
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(file);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = dbInputStream.read(bytes)) > 0) {
					fos.write(bytes, 0, length);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fos.flush();
					fos.close();
					dbInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
		else {
			Log.i(DATABASE_NAME, "Database Exists");
		}
		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
