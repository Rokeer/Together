package com.quardroiders;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataAccess {
	private static final String TAG = "Together_db";

	private static final String DATABASE_NAME = "tdb.db";
	SQLiteDatabase db;
	Context context;
	Cursor cur;

	DataAccess(Context _context) {
		context = _context;

		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		CreateTable();
		Log.v(TAG, "db path=" + db.getPath());
		close();
	}

	public void CreateTable() {
		try {
			db.execSQL("CREATE TABLE friendlist (" + "FNAME TEXT," + "STAT TEXT," + "ULNG TEXT," + "ULAT TEXT" + ");");
			Log.v(TAG, "Create Table friendlist ok");
			db.execSQL("CREATE TABLE blocklist (" + "BNAME TEXT" + ");");
			Log.v(TAG, "Create Table blocklist ok");
			db.execSQL("CREATE TABLE tuser (" + "UNAME TEXT," + "UPWD TEXT"
					+ ");");
			Log.v(TAG, "Create Table tuser ok");
		} catch (Exception e) {
			Log.v(TAG, "Create Table tuser err,table exists.");
		}
	}

	public void setDefault(String uname, String upwd) {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		String sql = "";
		try {
			// sql="delete * from tuser";
			db.delete("tuser", null, null);
			// db.execSQL(sql);
			sql = "insert into tuser values('" + uname + "', '" + upwd + "')";
			db.execSQL(sql);
			Log.v(TAG, "insert Table tuser ok");
			close();

		} catch (Exception e) {
			Log.v(TAG, "insert Table tuser err ,sql: " + sql);
			close();
		}
	}

	public void clearFriend() {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			// sql="delete * from tuser";
			db.delete("friendlist", null, null);
			Log.v(TAG, "clear Table friendlist ok");
			close();

		} catch (Exception e) {
			Log.v(TAG, "clear Table friendlist err");
			close();
		}
	}

	public void clearBlock() {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			// sql="delete * from tuser";
			db.delete("blocklist", null, null);
			Log.v(TAG, "clear Table blocklist ok");
			close();

		} catch (Exception e) {
			Log.v(TAG, "clear Table blocklist err");
			close();
		}
	}
	
	public void addFriend(String mStrMSG) {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		String[] tmp = new String[6];
		tmp=mStrMSG.split(",");
		String sql = "";
		try {
			cur = null;
			db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
					null);
			if (tmp[1].equals("0")){
				sql = "insert into friendlist values('"+tmp[0]+"', '"+tmp[1]+"', '"+tmp[2]+"', '"+tmp[3]+"')";
				db.execSQL(sql);
				Log.v(TAG, "insert Table friendlist ok");
				
			} else {
				sql = "insert into friendlist values('"+tmp[0]+"', '"+tmp[1]+"', '0', '0')";
				db.execSQL(sql);
			}
			
			close();

		} catch (Exception e) {
			Log.v(TAG, e.toString());
			close();
		}
	}

	public void addBlock(String bName) {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		String sql = "";
		try {
			// sql="delete * from tuser";
			// db.delete("tuser", null, null);
			// db.execSQL(sql);
			cur = null;
			db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
					null);
			cur = db.query("blocklist", new String[] { "BNAME" }, "BNAME = '"+bName+"'", null, null, null, null);
			if (!cur.moveToFirst()){
				sql = "insert into blocklist values('" + bName + "')";
				db.execSQL(sql);
				Log.v(TAG, "insert Table blocklist ok");
			} else {
				Log.v(TAG, "insert Table blocklist err, it already in the list");
			}
			
			close();

		} catch (Exception e) {
			Log.v(TAG, "insert Table blocklist err ,sql: " + sql);
			close();
		}
	}
	
	public void delBlock(String bName) {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.delete("blocklist", "BNAME = '"+bName+"'", null);
			Log.v(TAG, "delete block user ok");
			close();

		} catch (Exception e) {
			Log.v(TAG, "delete block user err, but i don't know why. It doesn't make any sence");
			close();
		}
	}
	
	public void delFriend(String fName) {
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.delete("friendlist", "FNAME = '"+fName+"'", null);
			Log.v(TAG, "delete friend ok");
			close();

		} catch (Exception e) {
			Log.v(TAG, "delete friend user err, but i don't know why. It doesn't make any sence");
			close();
		}
	}

	public Cursor getDefault() {
		try {
			cur = null;
			db = context.openOrCreateDatabase(DATABASE_NAME,
					Context.MODE_PRIVATE, null);
			cur = db.query("tuser", new String[] { "UNAME", "UPWD" }, null,
					null, null, null, null);
			// cur = db.query("ewvo" , new String[]
			// {"MNAME","LATITUDE","LONGITUDE"}, null, null, null, null, null);

			return cur;
		} catch (Exception e) {
			Log.v(TAG, e.toString());
			close();
			return cur;
		}

	}
	
    public Cursor getBlock(){  
    	db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null); 
        Cursor cur=db.query("blocklist", new String[]{"BNAME"}, null,null, null, null, null);  
        return cur;  
    }  
    
    public Cursor getFriend(){  
    	db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null); 
        Cursor cur=db.query("friendlist", new String[]{"FNAME","STAT","ULNG","ULAT"}, null,null, null, null, "STAT"); 
        return cur;  
    }  

    public String getLat(String fName){
    	db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		String lat = "";
		try {
			// sql="delete * from tuser";
			// db.delete("tuser", null, null);
			// db.execSQL(sql);
			cur = null;
			db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
					null);
			cur = db.query("friendlist", new String[] { "FNAME" , "STAT" , "ULNG", "ULAT" }, "FNAME = '"+fName+"'", null, null, null, null);
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				lat = cur.getString(3);
				cur.moveToNext();
			}
			
			
			close();
			return lat;
		} catch (Exception e) {
			Log.v(TAG, e.toString());
			close();
			return "0";
		}
    }
    
    public String getLng(String fName){
    	db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		String lng = "";
		try {
			// sql="delete * from tuser";
			// db.delete("tuser", null, null);
			// db.execSQL(sql);
			cur = null;
			db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
					null);
			cur = db.query("friendlist", new String[] { "FNAME" , "STAT" , "ULNG", "ULAT" }, "FNAME = '"+fName+"'", null, null, null, null);
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				lng = cur.getString(2);
				cur.moveToNext();
			}
			
			
			close();
			return lng;
		} catch (Exception e) {
			Log.v(TAG, e.toString());
			close();
			return "0";
		}
    }
	

	public void close() {
		db.close();
	}

}
