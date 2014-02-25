package com.maestromob.dublinbeergardens;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Criteria;
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
	Boolean wiFi;
	DatabaseAdapter db;	
	GoogleMap mMap;
	Cursor cursor;
	private LocationManager locationManager;
	private String provider;
	Location currentLocation;
	Location pubLocation;
	double currentLatitude;
	double currentLongitude;

	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    if(getIntent().getBooleanExtra("wifi", false)){
		    	wifi = "TRUE";
		    	wiFi = true;
	    	}else{
		    	 wifi = "FALSE";
		    	 wiFi = false;
		    	 noWifiExitApp();
	    	}
	    
	    Log.d("Mapview", "wifi is "+wifi);
	   
	    if (wiFi) {
			if (GoogleServicesOK()) {
				setContentView(R.layout.activity_mapview);

				if (initMap()) {
					mMap.setMyLocationEnabled(true);// shows my Location icon on screen

					// Get the location manager
					locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

					// Define the criteria how to select the location provider -> use default
					Criteria criteria = new Criteria();
					provider = locationManager.getBestProvider(criteria, false);
					currentLocation = locationManager.getLastKnownLocation(provider);
					locationManager.requestLocationUpdates(provider, 5000, 5, this);
					onLocationChanged(currentLocation);

				} else {
					Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show(); // for testing
				}
			} else {
				setContentView(R.layout.activity_main);
			}
			db = new DatabaseAdapter(this);
			db.open(); // for testing  	
			cursor = db.getAllBeerGardens(); // for testing
			ShowMarkers();
		}
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
					
					public View getInfoWindow(Marker marker) {
						View v;
						if (marker.getTitle().length()<12){
							v = getLayoutInflater().inflate(R.layout.map_info_window, null);
							} else {
							v = getLayoutInflater().inflate(R.layout.map_info_window_large, null);	
							}
						TextView textViewPubName = (TextView)v.findViewById(R.id.pubname);
						TextView textViewDistance = (TextView)v.findViewById(R.id.distance);
						
						LatLng ll = marker.getPosition();
						
						if (marker.getTitle().length()<19){
							textViewPubName.setText(marker.getTitle());
							textViewDistance.setText(marker.getSnippet());
							}else {
							String shortName = marker.getTitle().substring(0, 16)+"..";
							textViewPubName.setText(shortName);
							textViewDistance.setText(marker.getSnippet());	
							}
						
						return v;
					}
					
					public View getInfoContents(Marker Arg0) {
						return null;
					}
				});
			}
		}
		return (mMap!=null);
	}
	





	
	public void onLocationChanged(Location location) {
		currentLatitude =  location.getLatitude();
		currentLongitude =  location.getLongitude();
		Toast.makeText(this, "Current Location is " +currentLatitude+" "+currentLongitude ,
			        Toast.LENGTH_LONG).show();
		Log.d("LocationProvider","currentLatitude is "+currentLatitude);
		Log.d("LocationProvider","currentLongitude is "+currentLongitude);
		}
	

	 
	
	// Request updates at startup */
	protected void onResume() {
		super.onResume();
		if (wiFi){
			locationManager.requestLocationUpdates(provider, 60000, 5, this);
			Toast.makeText(this, "Location is "+currentLatitude+", "+currentLongitude, Toast.LENGTH_LONG).show();
			}
	  	}
	
	

	
	// Remove the locationlistener updates when Activity is paused */
	protected void onPause() {
	    super.onPause();
	    if (wiFi){
	    	locationManager.removeUpdates(this);
	    	}
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

	
	public void ShowMarkers (){
		cursor.moveToFirst();
		do {
			String markerName = cursor.getString(1);
			double easting = cursor.getDouble(3);
			double northing = cursor.getDouble(4);
			LatLng latlng = new LatLng(easting, northing);

			Marker marker = mMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title(markerName)
					.snippet(
							"Distance: "
									+ CalculatePubDistance(easting,
											northing) + "km")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pub_pin1)));
		} while (cursor.moveToNext());
	}
	
	
	
	
	
	public String CalculatePubDistance (double pubLatitude, double pubLongitude){
		
		pubLocation = new Location("pub");
		pubLocation.setLatitude(pubLatitude);
		pubLocation.setLongitude(pubLongitude);
		float dist = currentLocation.distanceTo(pubLocation);
		double distance = (double) dist/1000;
		distance = Math.round(distance*100.0)/100.0;
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(distance);
	}
	
	
	
	
	private void noWifiExitApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Wi Fi");
		builder.setMessage("Cannot connect to web-services, please try again later");
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				Toast.makeText(getApplicationContext(),
						"Exiting App", Toast.LENGTH_LONG).show();
				//Exit App
				finish();          
				moveTaskToBack(true);
			}
		});
		builder.show(); //To show the AlertDialog
	}
}

