package com.maestromob.dublinbeergardens;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;

public class PubDetailsFragmentImage extends Fragment{
	Typeface typeFace;
	Typeface boldTypeFace;
	String pubClicked="";
	OnDataTransferedImage dataListener;
	DatabaseAdapter db;	
	Cursor cursor;
	int width;
	int height;
	String image;
	private ImageView beerGardenViewImage;
	
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
        try {
        	dataListener = (OnDataTransferedImage) activity;
        } catch (Exception e) {
        	throw new ClassCastException(activity.toString() + " must implement OnDataTransferred");
        }
        pubClicked = dataListener.onDataTransferImage();
        Log.d("PubDetailsFragmentImage", "pubclicked is "+pubClicked); // for testing}
        db = new DatabaseAdapter(activity);
        db.open(); // for testing 
        cursor = db.getSingleBeerGarden(pubClicked); 
        //db.DebugValues(cursor);
        
        WindowManager wm = (WindowManager) activity.getSystemService
        					(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    width = metrics.widthPixels;
	    height = metrics.heightPixels;
	    Log.d("PubDetailsFragmentImage", "width is "+width); // for testing
	    Log.d("PubDetailsFragmentImage", "height is "+height); // for testing
	    image = cursor.getString(9);//*****************
	    							//********Get link to imagelink not imagelogolink
	    image = getImageFileName(image);
	    Log.d("PubDetailsFragmentImage", "imageLink  clicked is "+image); // for testing	
    }
	
	public View onCreateView (LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		View inflatedView = inflater.inflate(R.layout.pubdetails_image_fragment,
				container,false);
		
		String pathToImageFile = Environment.getExternalStorageDirectory()
				.getAbsoluteFile()+"/Beergardens/"+image;

		Log.d("PubDetailsFragmentImage", "pathToImageFile is "+pathToImageFile); // for testing

		beerGardenViewImage = (ImageView) inflatedView.findViewById(R.id.beergardenImageLarge);
		beerGardenViewImage.setImageBitmap(BitmapFactory.decodeFile(pathToImageFile));
		beerGardenViewImage.setScaleType(ScaleType.FIT_XY);
		
		//Log.d("PubDetailsFragmentImage", "imageHeight is "+imageHeight); // for testing
		//beerGardenViewImage.getLayoutParams().height = height;//*******Change this size here
		
		
		return inflatedView;
	}
	
	
	public interface OnDataTransferedImage{
		public String onDataTransferImage();
	}
	
	
	private String getImageFileName(String imageLinkFull) {
		int slashStart = imageLinkFull.lastIndexOf("/")+1;
		String imageName = imageLinkFull.substring(slashStart, imageLinkFull.length());
		Log.d("PubDetailsFragmentImage ", "imageName = "+imageName);// for testing	
		return imageName;
	}
}
