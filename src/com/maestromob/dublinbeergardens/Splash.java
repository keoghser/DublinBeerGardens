package com.maestromob.dublinbeergardens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;


import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;
import com.maestromob.dublinbeergardens.helpers.WebServicesAdapter;


public class Splash extends Activity {
	
	DatabaseAdapter db;	
	WebServicesAdapter web;
	ProgressBar progressBar;
	Boolean wiFiOk = true;
	HashMap <String, String> updateDatesDB = new HashMap <String,String>();
	HashMap <String, String> updateDatesWeb = new HashMap <String,String>();
	//int versionNumber;
	int sharedPrefVersion;
	int splashLength=5000;
	ArrayList <String> outOfDatePubs = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_splash);
	    	
	    progressBar = (ProgressBar)findViewById(R.id.progressbar);
	  	new AsyncWebServices().execute();
	  	    
	  	SharedPreferences dbActivity = getSharedPreferences(
				  "com.maestromob.dublinbeergardens_preferences", MODE_PRIVATE); 
	  	sharedPrefVersion = dbActivity.getInt("sharedPrefVersion", 0);
	  	if(sharedPrefVersion==0){
		  	SharedPreferences.Editor dbActivityUpdater = dbActivity.edit();
		  	dbActivityUpdater.putInt("sharedPrefVersion", 0);	  
		  	dbActivityUpdater.commit();
	  	}
	  	  
	  	sharedPrefVersion = dbActivity.getInt("com.maestromob.dublinbeergardens_preferences", 0); 
	  	Log.d("Splash", "OnCreate versionNumber in Sharedpreference file = "+sharedPrefVersion);
	  	
	  	    
	  	    // Obtains an instance of the SharedPreference
			// class by specifying the name of the xml file
			// sharedpreferences object is only accessible 
	  	  	// to this package
	}
	    
	     
	     
	  private class AsyncWebServices extends AsyncTask <Void, Void, Void>{
	    	  
	    	 
	    	 protected Void doInBackground(Void... voids){
	               // load your JSON feed asynchronously
	        	 if(createDatabase()){
	 	    		db = new DatabaseAdapter(getApplicationContext());
	 	    		web = new WebServicesAdapter(getApplicationContext());
	 	    		
	 	    		sharedPrefVersion=db.getSharedPrefVersion();
	 	    		//versionNumber = db.getDBVersionNumber();
	 	    		//Log.d("Splash", "versionNumber = "+versionNumber);// for testing
	 	    		Log.d("Splash", "doInBackground sharedPrefVersion = "+sharedPrefVersion);// for testing
	 	    		Log.d("Splash", "doInBackground DATABASE_VERSION = "+db.getDATABASE_VERSION());// for testing
	 	    		
	 	    			if(sharedPrefVersion==0){
	 			    	//if(versionNumber==0){// Although DATABASE_VERSION is 1, 
	 			    						//This is set to version 0 when DB first created
	 			    						//Activated the first time the webservices is run
	 			    		if(!web.getAllPubDetails()){
	 			    			Log.d("Splash", "In if(sharedPrefVersion) "+sharedPrefVersion);// for testing
	 			    			Log.d("Splash", "getAllPubDetails is not ok");// for testing
	 			    			wiFiOk = false;
	 			    			}
	 			    		}
	 			    	else {
	 			    		int JSONVersion = web.checkJSONVersion();
	 			    			 			    		
	 			    		if(JSONVersion>sharedPrefVersion){
	 			    		//if (JSONVersion>versionNumber){
	 			    			Log.d("Splash", "In jsonvERSION>sharedPrefVersion "+sharedPrefVersion);// for testing
	 			    			Log.d("Splash", "JSONVersion is greater than sharedPrefVersion");// for testing
	 			    			//Update entire DB
	 			    			db.setDATABASE_VERSION(JSONVersion);
	 			    			//db.setVersionNumber(JSONVersion);
	 			    			db.setSharedPrefVersion(JSONVersion);
	 			    			//Get new pubDetails
	 			    				if(!web.getAllPubDetails()){
	 			    					Log.d("Splash", "getAllPubDetails is not ok");// for testing
	 			    					try {
											Thread.sleep(splashLength);
											} catch (InterruptedException e) {
											e.printStackTrace();
											}
	 			    					}
			 			    } else if (JSONVersion!=0) {
									if (web.checkPubUpdate()) {
										updateDatesWeb = web.getUpdateDatesWeb();
										db.open();
										Cursor c = db.getUpdateDates();
										c.moveToFirst();
										do {
											AddToUpdateDatesDB(c.getString(0),c.getString(1));
										} while (c.moveToNext());
										//updateDatesWeb = web.getUpdateDatesWeb();
										Log.d("Splash", "updateDatesWeb is "
												+ updateDatesWeb.get("Gibneys"));// for testing 
										Log.d("Splash", "updateDatesDB is "
												+ updateDatesDB.get("Gibneys"));// for testing
										CompareUpdateDates();
										if (outOfDatePubs.size() > 0) {
											for (int i = 0; i < outOfDatePubs.size(); i++) {
												if (!web.getSinglePubDetails((String) outOfDatePubs.get(i))) {
													Log.d("Splash",
															"getSinglePubDetails is not ok");// for testing
													wiFiOk = false;
												}
											}
										}
									} else {
										wiFiOk = false;
									}
								}//end if
			 			    else{
			 			    	try {
									Thread.sleep(splashLength);
									} catch (InterruptedException e) {
									e.printStackTrace();
									}
			 			    }
					    
	 			    }//end else
	        	 }//end createdatabase
	        	return null;
	         }//end doInBackground

	         
	         protected void onPostExecute(Void params){
	        	  // launch your  activity
	        	 Intent i = new Intent(Splash.this,MapView.class);
	        	 i.putExtra("wifi", wiFiOk);
	        	 finish();
	        	 overridePendingTransition(R.anim.fadeout, R.anim.fadein);
	        	 startActivity(i);
			 }

	      }
	
	    
	public boolean createDatabase() {
		Log.d("Splash", "in createDatabase");// for testing	
		new CreateDatabase(getApplicationContext());
		Log.d("Splash", "createDatabase complete");// for testing
		return true;
		}
	
	
	
	public void AddToUpdateDatesDB(String key, String value){
		updateDatesDB.put(key, value);
		}
	
	
	
	
	public void CompareUpdateDates(){
		Iterator it = updateDatesDB.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Log.d("Splash", "updateDatesDB key, value "+pairs.getKey() + ", " 
	        		+ pairs.getValue());
	         String date = updateDatesWeb.get(pairs.getKey());
	         Log.d("Splash", "updateDatesWeb value "+date);
	         
	         if(!date.equalsIgnoreCase((String) pairs.getValue())){
	        	 outOfDatePubs.add((String) pairs.getKey());
	        	 }
	        }
	    	for (int i = 0; i < outOfDatePubs.size(); i++) {
	    		Log.d("Splash", "Out of date PubDetails are "+outOfDatePubs.get(i));
	    		}
	    	}
			
}
	
	

