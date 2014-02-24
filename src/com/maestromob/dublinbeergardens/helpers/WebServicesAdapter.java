package com.maestromob.dublinbeergardens.helpers;


import java.net.URL;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class WebServicesAdapter {
	Context mContext;
	DatabaseAdapter db;
	URL url;
	JSONObject json;
	JSONArray jsonArray;
	IntentFilter intentFilter;
	int JSONVersion=0;
	HashMap <String, String> updateDatesWeb = new HashMap <String,String>();;
		
	
	public WebServicesAdapter(Context context){
		       mContext = context;
		}
	
		
		
	public int checkJSONVersion(){
	
		Log.d("WebServicesAdapter","In checkJSONVersion", null);// for testing	
	
		//Check wifi connection
		ConnectivityManager connManager = (ConnectivityManager) 
			mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			Log.d("WebServicesAdapter", "checkJSONVersion-WiFi IS connected");// for testing	
			//Connect to web-services 
			//Check web-services version number
			JSONQuery jsonQuery = new JSONQuery(
				"http://beergarden.keoghser.com/getversion.php?version");
			json = jsonQuery.getJSON();
			Log.i("WebServicesAdapter", "JSONVersion is "+json);// for testing
			try {
				JSONVersion = json.getInt("version");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return JSONVersion;	
	}
	
	
	
	
	public Boolean checkPubUpdate() {
		
		Log.d("WebServicesAdapter","In checkPubUpdate", null);// for testing	
		Boolean wiFiOK = false;
		//Check wifi connection
		ConnectivityManager connManager = (ConnectivityManager) 
				mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			Log.d("WebServicesAdapter", "checkPubUpdate-WiFi IS connected");// for testing
			
			JSONQuery jsonQuery = new JSONQuery(
					"http://beergarden.keoghser.com/getupdatedates.php?updatedates");
			jsonArray = jsonQuery.getJSONArray();
			
			for (int i=0; i<jsonArray.length();i++){
				 try {
					JSONObject json = jsonArray.getJSONObject(i);
					AddToUpdateDatesWeb(json.getString("name"),json.getString("updatedate"));
					} catch (JSONException e) {
					e.printStackTrace();
					}	
				}
			wiFiOK = true;
			}
		else{
			Log.d("WebServicesAdapter", "checkPubUpdate-WiFi NOT connected");// for testing
			wiFiOK = false;
		}
		return wiFiOK;
	}
	
	
	
	
	
	public Boolean getAllPubDetails() {
	
		Log.d("WebServicesAdapter", "getAllPubDetails, Database not populated yet");// for testing
		Boolean wiFiOK = false;
		
		//Check wifi connection
		ConnectivityManager connManager = (ConnectivityManager) 
				mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			Log.d("WebServicesAdapter", "getAllPubDetails, WiFi IS connected");// for testing
			//Connect to webservices
			
			JSONQuery jsonQuery = new JSONQuery("http://beergarden.keoghser.com/" +
					"getpubdetails.php?pubdetails");
			json = jsonQuery.getJSON();
			jsonArray = jsonQuery.getJSONArray();
			Log.i("WebServicesAdapter", "json is "+json);// for testing
			
           db = new DatabaseAdapter(mContext);
            
			
			for (int i=0; i<jsonArray.length();i++){
				JSONObject json = new JSONObject();
				try {
					json = jsonArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					//Install all details from webservices
					db.open();
					long id = db.insertPubDetails(json.getString("name"), json.getString("address"), 
							json.getDouble("locationEast"), json.getDouble("locationNorth"), 
							json.getInt("seatingCapacity"), json.getString("phone"), 
							json.getString("gardenSize"),json.getString("url"), 
							json.getString("imageLink"), json.getString("description"), 
							json.getString("updatedate"));
					} catch (JSONException e) {
					e.printStackTrace();
					}
				try {
					DownloadStoreImage bitmapImage = new DownloadStoreImage
										(json.getString("imageLink"));
					Bitmap image = bitmapImage.getImage();
					Log.d("WebServicesAdapter", "Bitmap image downloaded "+json.getString("imageLink"));// for testing
					bitmapImage.StoreImage(image,json.getString("name"));
					Log.d("WebServicesAdapter", "Bitmap image stored");// for testing
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
						
			Log.d("WebServicesAdapter", "Inserted JSON into DB");// for testing	
			wiFiOK = true;
			
			if (db.getSharedPrefVersion()==0){
				//if (db.getDBVersionNumber()==0){
				//db.setVersionNumber(1);//********************************* 
				Log.d("WebServicesAdapter", "In if(db.getSharedPrefVersion()) "+db.getSharedPrefVersion());// for testing
				db.setSharedPrefVersion(1);//*****************************
				Log.d("WebServicesAdapter", "In if(db.getSharedPrefVersion()) DATABASE_VERSION = "+db.getDATABASE_VERSION());// for testing
				Log.d("WebServicesAdapter", "In if(db.getSharedPrefVersion()) sharedPrefVersion = "+db.getSharedPrefVersion());// for testing
				// Cursor c = db.getAllBeerGardens(); // for testing	
				//db.DebugValues(c);
				}
			//Log.d("WebServicesAdapter", "VersionNumber is now "
					//+db.getDBVersionNumber());// for testing	
			
			}
		else{
			Log.d("WebServicesAdapter", "getAllPubDetails-WiFi NOT connected");// for testing	
			wiFiOK = false;
		}
		return wiFiOK;
	}
	
	
	
	
	
	public Boolean getSinglePubDetails(String pub) {
		
		Log.d("WebServicesAdapter", "In getSinglePubDetails");// for testing
		Boolean wiFiOK = false;
		
		//Check wifi connection
		ConnectivityManager connManager = (ConnectivityManager) 
				mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			Log.d("WebServicesAdapter", "getSinglePubDetails, WiFi IS connected");// for testing
			//Connect to webservices
			
			JSONQuery jsonQuery = new JSONQuery("http://beergarden.keoghser.com/" +
					"getsinglepubdetails.php?singlepubdetails='"+pub+"'");
			json = jsonQuery.getJSON();
			jsonArray = jsonQuery.getJSONArray();
			Log.i("WebServicesAdapter", "json is "+json);// for testing
			
           db = new DatabaseAdapter(mContext);
           
           for (int i=0; i<jsonArray.length();i++){
				JSONObject json = new JSONObject();
				try {
					json = jsonArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					//Install all details from webservices
					db.open();
					long id = db.insertPubDetails(json.getString("name"), json.getString("address"), 
							json.getDouble("locationEast"), json.getDouble("locationNorth"), 
							json.getInt("seatingCapacity"), json.getString("phone"), 
							json.getString("gardenSize"),json.getString("url"), 
							json.getString("imageLink"), json.getString("description"), 
							json.getString("updatedate"));
					} catch (JSONException e) {
					e.printStackTrace();
					}
				try {
					DownloadStoreImage bitmapImage = new DownloadStoreImage
										(json.getString("imageLink"));
					Bitmap image = bitmapImage.getImage();
					Log.d("WebServicesAdapter", "Bitmap image downloaded "+json.getString("imageLink"));// for testing
					bitmapImage.StoreImage(image,json.getString("name"));
					Log.d("WebServicesAdapter", "Bitmap image stored");// for testing
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
						
			Log.d("WebServicesAdapter", "Inserted singlePUbDetails JSON into DB");// for testing	
			wiFiOK = true;
			
			}
		else{
			Log.d("WebServicesAdapter", "getSinglePubDetails-WiFi NOT connected");// for testing	
			wiFiOK = false;
			 
		}
		return wiFiOK;
	}	
	
	
	
	
	
	
	
	
	
	public void AddToUpdateDatesWeb(String key, String value){
		updateDatesWeb.put(key, value);
		}
	
	
	public HashMap<String, String> getUpdateDatesWeb(){
		return updateDatesWeb;
		}
	
	
}
