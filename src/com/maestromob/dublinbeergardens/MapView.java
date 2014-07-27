package com.maestromob.dublinbeergardens;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maestromob.dublinbeergardens.PubDetailsFragmentInfo.OnDataTransferedInfo;
import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;


/**
 * Zoom to current location or centre on O'Connell Bridge
 * pubPins hashMap - key is pubID, value is pubName object
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */

public class MapView extends FragmentActivity implements 
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,LocationListener{
	
	private static final int GPS_ERRORDIALOG_REQUEST = 9000;
	String wifi;
	Boolean wiFi;
	DatabaseAdapter db;	
	GoogleMap mMap;
	Cursor cursor;
	LocationClient mLocationClient;
	Location currentLocation;
	Location pubLocation;
	LatLng latlng;
	double currentLatitude;
	double currentLongitude;
	String pubClicked;
	Typeface typeFace;
	Typeface boldTypeFace;

	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //typeFace = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
	    //boldTypeFace = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf");
	    typeFace = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
		boldTypeFace = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
	    
	    if(getIntent().getBooleanExtra("wifi", false)){
		    	wifi = "TRUE";
		    	wiFi = true;
	    	} else{
		    	 wifi = "FALSE";
		    	 wiFi = false;
		    	 noWifiExitApp();
	    	}
	    
	    Log.d("Mapview", "wifi is "+wifi); // for testing
	   
	    if (wiFi) {
			if (GoogleServicesOK()) {
			setContentView(R.layout.activity_mapview);

				if (initMap()) {
					mMap.setMyLocationEnabled(true);// shows my Location icon on screen
					mLocationClient = new LocationClient(this, this, this);
					mLocationClient.connect();
					db = new DatabaseAdapter(this);
					db.open(); // for testing  	
					cursor = db.getAllBeerGardens(); // for testing
					
					} else {
						Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show(); // for testing
					}
			} else {
			setContentView(R.layout.activity_splash);
			}
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
						HashMap <String, Integer> mMarkers = new HashMap<String, Integer>();
						
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
						
						textViewPubName.setTypeface(boldTypeFace);
						textViewDistance.setTypeface(typeFace);
						
						
						mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			                 
			                public void onInfoWindowClick(Marker marker) {
			                	Log.d("Mapview", "In Click Window"); // for testing 
			                	Intent i = new Intent(getBaseContext(),PubDetails.class);
			                	pubClicked = marker.getTitle();
			                	i.putExtra("pubClicked", pubClicked);
			                	startActivity(i);
			                }
			            });
						
						
						 
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
	
	
		
	
	
	public void LoadMarkers (){
		cursor.moveToFirst();
		do {
			String markerName = cursor.getString(1);
			double easting = cursor.getDouble(3);
			double northing = cursor.getDouble(4);
			LatLng latlng = new LatLng(easting, northing);
			Log.d("Mapview", "4 currentLocation is "+currentLocation); // for testing

			Marker marker = mMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title(markerName)
					.snippet("Distance: "+CalculatePubDistance(easting,northing) + "km")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pub_pin1)));
		} while (cursor.moveToNext());
	}
	
	
	
	
	public String CalculatePubDistance (double pubLatitude, double pubLongitude){
		pubLocation = new Location("pub");
		pubLocation.setLatitude(pubLatitude);
		pubLocation.setLongitude(pubLongitude);
		Log.d("Mapview", "1 currentLocation is "+currentLocation); // for testing
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
	
	
	

	
	protected void gotoCurrentLocation(){
		currentLocation =  mLocationClient.getLastLocation();
		Log.d("Mapview", "2 currentLocation is "+currentLocation); // for testing
		if (currentLocation==null){
			Toast.makeText(this, "Current Location is not available", Toast.LENGTH_SHORT).show();
		} else{
			latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,13);
			mMap.animateCamera(update);
		}
	}
	
	

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Could not connect to location service", Toast.LENGTH_SHORT).show();
	}



	@Override
	public void onConnected(Bundle arg0) {
		//Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
		gotoCurrentLocation();
		LocationRequest request = LocationRequest.create();
		request.setInterval(60000);
		request.setFastestInterval(60000);
		mLocationClient.requestLocationUpdates(request,this);
		currentLocation =  mLocationClient.getLastLocation();
		Log.d("Mapview", "3 currentLocation is "+currentLocation); // for testing
		LoadMarkers();
	}



	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	

	@Override
	public void onLocationChanged(Location location) {
		String msg = location.getLatitude()+", "+location.getLongitude();
		//Toast.makeText(this, "Lat and Long is "+msg, Toast.LENGTH_LONG).show();
	}



	public void onPause(){
		try {
			mLocationClient.removeLocationUpdates(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPause();
	} 
	

	
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d("LocationProvider","Status changed");
	}

	
	
	
}

