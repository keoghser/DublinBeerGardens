package com.maestromob.dublinbeergardens.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;

public class DatabaseAdapter {

	static final String KEY_ROWID = "pubID";
    static final String KEY_NAME = "name";
    static final String KEY_ADDRESS = "address";
    static final String KEY_LOCATION_EAST = "locationEast";
    static final String KEY_LOCATION_NORTH = "locationNorth";
    static final String KEY_SEATING_CAPACITY = "seatingCapacity";
    static final String KEY_PHONE = "phone";
    static final String KEY_GARDEN_SIZE = "gardenSize";
    static final String KEY_URL = "url";
    static final String KEY_IMAGE_LINK = "imageLink";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_UPDATE_DATE = "updatedate";
    static final String TAG = "beergarden";
    static final String DATABASE_NAME = "beergardens";
    static final String PUBDETAILS_TABLE = "pubdetails";
    static int DATABASE_VERSION; //This is the version number to control the entire DB
    //static int DBVersion = 0;//This is the version number to be used in SQL Table initially
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    Cursor mCursor;
    static int sharedPrefVersion=0;
    
    static final String PUBDETAILS_TABLE_CREATE =
        "create table pubdetails ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY_NAME+" text not null, "+KEY_ADDRESS+" text not null, "+KEY_LOCATION_EAST+" double not null, "
        + KEY_LOCATION_NORTH+" double not null, "+KEY_SEATING_CAPACITY+" int not null, "+KEY_PHONE+" text not null, "
        + KEY_GARDEN_SIZE+" int not null, "+KEY_URL+" text not null, "+KEY_IMAGE_LINK+" text not null, "
        + KEY_DESCRIPTION+" text not null, "+KEY_UPDATE_DATE+" text not null);"; 

    /*static final String DB_VERSION_TABLE_CREATE =
            "create table databaseVersion (versionID integer primary key " +
            "autoincrement, databaseVersionValue integer);"; */
        
    
    public DatabaseAdapter (Context ctx){
        this.context = ctx;
        DATABASE_VERSION = getSharedPrefVersion();
        sharedPrefVersion = getSharedPrefVersion();
        Log.d("DatabaseAdapter", "In DatabaseAdapter Constructor sharedPrefVersion is "+sharedPrefVersion);// for testing
        if (DATABASE_VERSION==0){
        	DATABASE_VERSION=1;
        	}
        DBHelper = new DatabaseHelper(context);
        Log.d("DatabaseAdapter", "New DatabaseAdapter Constructed");// for testing
        
       //for testing
        //SharedPreferences dbActivity = context.getSharedPreferences("sharedPrefVersion", 0);
        //sharedPrefVersion = dbActivity.getInt("sharedPrefVersion", 0); 
	  	//Log.d("DatabaseAdapter", "versionNumber in Sharedpreference file = "+sharedPrefVersion);
       }
    

       
   
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseAdapter", "Created DatabaseHelper, DATABASE_VERSION is "
            				+DATABASE_VERSION);// for testing
        }
    
        

    public void onCreate(SQLiteDatabase db) {
          try {
                db.execSQL(PUBDETAILS_TABLE_CREATE);
                //db.execSQL(DB_VERSION_TABLE_CREATE);
                Log.d("DatabaseAdapter", "New Tables Created");// for testing
                //ContentValues args = new ContentValues();
               // args.put("databaseVersionValue", sharedPrefVersion);
                //args.put("databaseVersionValue", DBVersion);//Set value of version in database to 0 initially, before DB is populated
               // db.insert("databaseVersion", null, args);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
    
    

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+PUBDETAILS_TABLE);
            onCreate(db);
            DATABASE_VERSION = newVersion;
    		}
    	}

    
    
    
    //---opens the database---
    public DatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    	}

    
    
    
    //---closes the database---
    public void close() {
        DBHelper.close();
    	}
    
    
    

    //---retrieves all the Beergardens---
    public Cursor getAllBeerGardens(){
        return db.query(PUBDETAILS_TABLE, new String[] {KEY_ROWID, KEY_NAME,
        	    KEY_ADDRESS,KEY_LOCATION_EAST, KEY_LOCATION_NORTH,KEY_SEATING_CAPACITY,KEY_PHONE,
        	    KEY_GARDEN_SIZE,KEY_URL,KEY_IMAGE_LINK,KEY_DESCRIPTION,
        	    KEY_UPDATE_DATE }, null, null, null, null, KEY_NAME  + " ASC" );
        }
    
    
    

    //---retrieves a particular Beergarden with a known ID---
    public Cursor getSingleBeerGarden(String name) throws SQLException {
        mCursor = db.query(true, PUBDETAILS_TABLE, new String[] {KEY_ROWID,
                KEY_NAME, KEY_ADDRESS,KEY_LOCATION_EAST, KEY_LOCATION_NORTH,KEY_SEATING_CAPACITY,
                KEY_PHONE,KEY_GARDEN_SIZE,KEY_URL,KEY_IMAGE_LINK,KEY_DESCRIPTION,
        	    KEY_UPDATE_DATE}, KEY_NAME + "=" + name, null,
                null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        	}
        return mCursor;
    	}
    
    
    
    
    
    public Cursor getUpdateDates(){
    	Log.d("DatabaseAdapter", "In getUpdateDates");// for testing
        return db.query(PUBDETAILS_TABLE, new String[] {KEY_NAME,KEY_UPDATE_DATE}, 
        		null, null, null, null, null );
        }
    
    
    
    
    
    //---retrieves the version Number of Database saved in SQL Table---
   /* public int getDBVersionNumber(){
    		
    	open();
    	mCursor=db.rawQuery("SELECT * FROM databaseVersion", null);
          	
        if (mCursor != null) {        	
        	mCursor.moveToFirst();
        	//DBVersion = mCursor.getInt(1); 
        	sharedPrefVersion = mCursor.getInt(1); 
        	Log.d("DatabaseAdapter", "in getVersionNumber, " +
        			//"version number in DB table is "+DBVersion);// for testing
        			"sharedPrefVersion is "+sharedPrefVersion);// for testing
        	}
        //return DBVersion;
        return sharedPrefVersion;
        }*/
    
    
    
  //---retrieves the version Number of entire SQL Database---
    public int getDATABASE_VERSION(){ 
    	return DATABASE_VERSION;
    	}
    
    
    
    
  //---retrieves the version Number of entire SQL Database---
    public void setDATABASE_VERSION(int version){ 
    	DATABASE_VERSION = version;
    	}
    
    
    

	/*public long setVersionNumber(int newVersionNumber) {
		ContentValues updateVersion = new ContentValues();
		updateVersion.put("databaseVersionValue", newVersionNumber);
		Log.d("DatabaseAdapter", "in setVersionNumber, " +
    			"version number in DB table is changed to "+newVersionNumber);// for testing
		return db.update("databaseVersion", updateVersion,null,null);
		}*/
	
	
	
	
	//---insert a pub into the database---
    public long insertPubDetails(String name, String address, double locationEast, 
    		double locationNorth, int seating, String phone, String garden,
    		String url, String imageLink, String description, String updatedate) {
    	
       //String apostropheName = name.replace("'", "€"); 
       
       	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name); 
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_LOCATION_EAST, locationEast);
        initialValues.put(KEY_LOCATION_NORTH, locationNorth);
        initialValues.put(KEY_SEATING_CAPACITY, seating);
        initialValues.put(KEY_PHONE, phone);
        initialValues.put(KEY_GARDEN_SIZE, garden);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_IMAGE_LINK, imageLink);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_UPDATE_DATE, updatedate);
     
        Long id = db.insert(PUBDETAILS_TABLE, null, initialValues);
        Log.d("DatabaseAdapter", "Near end of insertPubDetails");// for testing
        return id;
    	}
    
   
    
    /* 
    //---retrieves a particular beergarden at a known row (not ID)---
    public int getBeerGardenIdAtRow(int row) throws SQLException {
    	int idRow=0;
    	mCursor = getAllBeerGardens();    
        
        if (mCursor != null) {        	
        	mCursor.moveToFirst();
        	mCursor.moveToPosition(row);
        	idRow = mCursor.getInt(0);      	            
        	}
            
        return  idRow;             
    	}
    */
   


   
    
 //Displays the details of each beergarden in the Database, for testing  
    
    public void DisplayValues(Cursor c)
		{
    	 c.moveToFirst();
    	    do {Toast.makeText(context,
    	                "Name: " + c.getString(1),Toast.LENGTH_SHORT).show();
    	    } while (c.moveToNext());
        }


    public void DebugValues(Cursor c)
		{
	 c.moveToFirst();
	    do {Log.d("DatabaseAdapter", 
	    		"Debug Values "+c.getString(1));// for testing
	    	} while (c.moveToNext());
		}


	public void setSharedPrefVersion(int newVersionNumber) {
		SharedPreferences dbActivity = context.getSharedPreferences("com.maestromob.dublinbeergardens_preferences", 0);
	  	SharedPreferences.Editor dbActivityUpdater = dbActivity.edit();
	  	dbActivityUpdater.putInt("sharedPrefVersion", newVersionNumber);	  
	  	dbActivityUpdater.commit();
	  	sharedPrefVersion=newVersionNumber;
	  	Log.d("DatabaseAdapter", "In setSharedPrefVersion "+sharedPrefVersion);// for testing
		}
	
	

	public int getSharedPrefVersion(){
		SharedPreferences dbActivity = context.getSharedPreferences("com.maestromob.dublinbeergardens_preferences", 0);
        sharedPrefVersion = dbActivity.getInt("sharedPrefVersion", 0);
        Log.d("DatabaseAdapter", "In getSharedPrefVersion "+sharedPrefVersion);// for testing
        Log.d("DatabaseAdapter", "In getSharedPrefVersion DATABASE_VERSION "+DATABASE_VERSION);// for testing
        return sharedPrefVersion;
		}

 

}// end class
	


