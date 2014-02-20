package com.maestromob.dublinbeergardens;

import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;


/**
 * Zoom to current location or centre on O'Connell Bridge
 * pubPins hashMap - key is pubID, value is pubName object
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */

public class MapView extends FragmentActivity implements LocationListener{
	private static final int GPS_ERRORDIALOG_REQUEST = 9000;
	public PubName pubName;
	public Set<PubName> pubName2;
	public PubView pubView;
	String wifi;
	DatabaseAdapter db;	
	GoogleMap mMap;
	LocationManager locationManager;
	LocationListener locationListener;
	double currentLatitude;
	double currentLongitude;
	Location mCurrentLocation;
	LocationClient mLocationClient;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    if(getIntent().getBooleanExtra("wifi", false)){
	    	 wifi = "TRUE";
	    	}else{
	    	 wifi = "FALSE";
	    	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	 builder.setTitle("Wi Fi");
	    	 builder.setMessage("Cannot connect to web-services, please try again later");
	    	 builder.setPositiveButton("OK",
	    			 new DialogInterface.OnClickListener() {
	    		 @Override
	    		 public void onClick(DialogInterface arg0, int arg1) {
	    			 // TODO Auto-generated method stub
	    			 Toast.makeText(getApplicationContext(),
	    					 "Exiting App", Toast.LENGTH_LONG).show();
	    			 //Exit App
	    			 finish();          
	    	         moveTaskToBack(true);
	    		 }
	    	 });
	    	 builder.show(); //To show the AlertDialog
	    	}
	    
	    Log.d("Mapview", "wifi is "+wifi);
	   
	    if(GoogleServicesOK()){
	    	setContentView(R.layout.activity_mapview);
	    	
	    	if (initMap()){
	    		//Toast.makeText(this, "Ready to Map!", Toast.LENGTH_SHORT).show(); // for testing
	    		mMap.setMyLocationEnabled(true);
	    		} else {
	    		//Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show(); // for testing
	    		}
	    } else {
	    		setContentView(R.layout.activity_main);
	    }
	   
	    
	    db = new DatabaseAdapter(this);
    	db.open();      // for testing  	
        Cursor c = db.getAllBeerGardens(); // for testing
        
        c.moveToFirst();
	    do {
	    	String markerName = c.getString(1);
	    	double easting = c.getDouble(3);
	    	double northing = c.getDouble(4);
	    	
	    			
	    	LatLng latlng = new LatLng(easting,northing);
	    	Marker marker = mMap.addMarker(new MarkerOptions()
	    						.position(latlng)
	    						.title(markerName)
	    						.snippet("Distance: 1.0 km")
	    						.icon(BitmapDescriptorFactory.fromResource
	    								(R.drawable.pub_pin1)));
	    } while (c.moveToNext());
        	            
        //db.DisplayValues(c);// for testing
	}

	 
	
	
	public boolean GoogleServicesOK(){
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	
		if (isAvailable==ConnectionResult.SUCCESS){
			return true;
			} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, 
						this, GPS_ERRORDIALOG_REQUEST);
				dialog.show();
			} else {
				Toast.makeText(this, "Can't connect to Google Play Services", Toast.LENGTH_SHORT).show();
			}
		return false;
		}


	
	
	
	private boolean initMap(){ //Makes sure the Map object is available 
		if (mMap==null){
			SupportMapFragment mapFrag
				=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
			mMap = mapFrag.getMap();
			if(mMap!=null){
				mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
					
					@Override
					public View getInfoWindow(Marker marker) {
						View v = getLayoutInflater().inflate(R.layout.map_info_window, null);
						TextView tvPubName = (TextView)v.findViewById(R.id.pubname);
						TextView tvDistance = (TextView)v.findViewById(R.id.distance);
						
						LatLng ll = marker.getPosition();
						
						tvPubName.setText(marker.getTitle());
						tvDistance.setText(marker.getSnippet());
						return v;
						
					}
					
					@Override
					public View getInfoContents(Marker Arg0) {
						return null;
					}
				});
			}
			
			
		}
		return (mMap!=null);
	}
	
	
	public void GetCurrentLocation(){
		locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}




	
	public void onLocationChanged(Location location) {
		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();
		Log.d("LocationProvider","currentLatitude is "+currentLatitude);
		Log.d("LocationProvider","currentLongitude is "+currentLongitude);
		}




	
	public void onProviderDisabled(String arg0) {
		Log.d("LocationProvider","disabled");
		}




	
	public void onProviderEnabled(String arg0) {
		Log.d("LocationProvider","enabled");
		}




	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d("LocationProvider","Status changed");
		}




	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}

