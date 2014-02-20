package com.maestromob.dublinbeergardens.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class DownloadStoreImage {
	Bitmap bitmap;
 
	public DownloadStoreImage (String url) {  
		InputStream in = null;        
		try {
			bitmap = null;
			in = OpenHttpConnection(url);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e1) {
			Log.d("NetworkingActivity", e1.getLocalizedMessage());            
		}
		             
	}
    
    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
        	Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;     
    }
    
    public Bitmap getImage(){
    	return bitmap;
    }
    
    
    public void StoreImage(Bitmap image, String title){
    	File pictureFile = getOutputMediaFile(title);
        if (pictureFile == null) {
            Log.d("DownloadStoreImage", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        } 
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("DownloadStoreImage", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("DownloadStoreImage", "Error accessing file: " + e.getMessage());
        }
    }
    	
    private File getOutputMediaFile(String title){
    	 
    		File mediaStorageDir = new File((Environment.getExternalStorageDirectory())
    				.getAbsoluteFile()+"/BeerGardens");
    		Log.d("DownloadStoreImage", "mediaStorageDir created " );
    		

    	    // This location works best if you want the created images to be shared
    	    // between applications and persist after your app has been uninstalled.

    	    // Create the storage directory if it does not exist
    	    if (! mediaStorageDir.exists()){
    	        if (! mediaStorageDir.mkdirs()){
    	        	Log.d("DownloadStoreImage", "Can't make directory");
    	            return null;
    	        	}
    	    	} 
    	    // Create a media file name
    	    String timeStamp = new SimpleDateFormat("ddMMyyyy").format(new Date());
    	    File mediaFile;
    	    //String mImageName="MI_"+ timeStamp +".jpg";
    	    String imageName=title+"_"+timeStamp+".jpg";
    	    Log.d("DownloadStoreImage", "ImageName created "+ imageName);
    	    mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageName);  
    	    return mediaFile;
    }
    
}
