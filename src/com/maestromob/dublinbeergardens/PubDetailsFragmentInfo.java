package com.maestromob.dublinbeergardens;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.telephony.gsm.SmsManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;


public class PubDetailsFragmentInfo extends Fragment{
	
	Typeface typeFace;
	Typeface boldTypeFace;
	private ImageView beerGardenView;
	String pubClicked="";
	OnDataTransferedInfo dataListener;
	DatabaseAdapter db;	
	Cursor cursor;
	int width;
	int height;
	String image;
	String text ="";
	private UiLifecycleHelper uiHelper;


	public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	dataListener = (OnDataTransferedInfo) activity;
        } catch (Exception e) {
        	throw new ClassCastException(activity.toString() + 
        			" must implement OnDataTransferred");
        }
        pubClicked = dataListener.onDataTransferInfo();
        Log.d("PubDetailsFragmentInfo", "pubclicked is "+pubClicked); // for testing}
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
	    Log.d("PubDetailsFragmentInfo", "width is "+width); // for testing
	    Log.d("PubDetailsFragmentInfo", "height is "+height); // for testing
	    image = cursor.getString(10);
	    image = getImageFileName(image);
	    Log.d("PubDetailsFragmentInfo", "imageLink  clicked is "+image); // for testing		
	    
	}
	
	public View onCreateView (LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		View inflatedView = inflater.inflate
				(R.layout.pubdetails_info_fragment,container,false);
		typeFace = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Regular.ttf");
		boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Bold.ttf");
		
		text = "Join me for a drink in the beergarden at " +
				""+cursor.getString(1)+" pub, "+cursor.getString(2)+".";
				
		String pathToImageFile = Environment.getExternalStorageDirectory()
						.getAbsoluteFile()+"/Beergardens/"+image;
		
		Log.d("PubDetailsFragmentInfo", "pathToImageFile is "+pathToImageFile); // for testing
		
		LinearLayout parentView = (LinearLayout)inflatedView.findViewById(R.id.parent);
		parentView.getBackground().setAlpha(0);
		
		beerGardenView = (ImageView) inflatedView.findViewById(R.id.beergardenImage);
		beerGardenView.setImageBitmap(BitmapFactory.decodeFile(pathToImageFile));
		beerGardenView.setScaleType(ScaleType.FIT_XY);
		
		beerGardenView.getLayoutParams().height = height/3;
		beerGardenView.getLayoutParams().width = width;
		
		
		
		TextView pubTitle = (TextView) inflatedView.findViewById(R.id.beergardenTitle);
		pubTitle.setTypeface(boldTypeFace);
		pubTitle.getLayoutParams().height = (int) (height*0.04);
		pubTitle.setPadding((int) (width*0.1),0,0,0);
		pubTitle.setText(cursor.getString(1)+"");
				
		TextView pubAddress = (TextView) inflatedView.findViewById(R.id.beergardenAddress);
		pubAddress.setTypeface(typeFace);
		pubAddress.getLayoutParams().height = (int) (height*0.03);
		pubAddress.setPadding((int) (width*0.1),0,0,0);
		pubAddress.setText(Html.fromHtml("<u>Address:</u>"));
		pubAddress.append(" "+cursor.getString(2));
		
		LinearLayout blur = (LinearLayout)inflatedView.findViewById(R.id.Blur);
		blur.getBackground().setAlpha(220);
		blur.getLayoutParams().height = (int) (height*0.6);
		blur.getLayoutParams().width = (int) (width);
		LinearLayout.LayoutParams blurParams = (LinearLayout.LayoutParams)
				blur.getLayoutParams();
		blur.setLayoutParams(blurParams);
		
		
		RelativeLayout infoBlur = (RelativeLayout)inflatedView.findViewById(R.id.infoBlur);
		infoBlur.getLayoutParams().height = (int) (height*0.415);
		infoBlur.getLayoutParams().width = (int) (width*0.89);
		LinearLayout.LayoutParams infoBlurParams = (LinearLayout.LayoutParams)
				infoBlur.getLayoutParams();
		infoBlurParams.setMargins((int) (width*0.05555), (int) (height*0.03), 0, 0);
		infoBlur.getBackground().setAlpha(220);
		
		
		
		TextView pubSize = (TextView) inflatedView.findViewById(R.id.beergardenSize);
		pubSize.setTypeface(typeFace);
		pubSize.setPadding((int)(width*0.07),(int)(height*0.01),0,0);
		pubSize.setText(Html.fromHtml("<u>Garden Size:</u>"));
		pubSize.append(" "+cursor.getString(7)+" sqm");
		
		
		TextView pubSeating = (TextView) inflatedView.findViewById(R.id.beergardenSeating);
		pubSeating.setTypeface(typeFace);
		pubSeating.setPadding((int) (width*0.07),(int)(height*0.01),0,0);
		pubSeating.setText(Html.fromHtml("<u>Capacity:</u>"));
		pubSeating.append(" "+cursor.getString(5)+" persons");
		
		TextView pubPhone = (TextView) inflatedView.findViewById(R.id.beergardenPhone);
		pubPhone.setTypeface(typeFace);
		pubPhone.setPadding((int) (width*0.07),(int)(height*0.01),0,0);
		pubPhone.setText(Html.fromHtml("<u>Phone:</u>"));
		pubPhone.append(" "+"01 "+cursor.getString(6).substring(2));
		pubPhone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
            	Uri phone = Uri.parse("tel:"+cursor.getString(6));
            	Intent i = new Intent(android.content.Intent.ACTION_DIAL,phone);
				startActivity(i);
            	}
			});
		
		
		
		TextView pubWeblink = (TextView) inflatedView.findViewById(R.id.beergardenWeb);
		pubWeblink.setTypeface(typeFace);
		pubWeblink.setPadding((int) (width*0.07),(int)(height*0.01),0,0);
		pubWeblink.setText(Html.fromHtml("<u>Web:</u>"));
		pubWeblink.append(" "+cursor.getString(8));
		pubWeblink.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
            	Uri website = Uri.parse("http://"+cursor.getString(8));
            	Intent i = new Intent(android.content.Intent.ACTION_VIEW,website);
				startActivity(i);
            	}
			});
		
		
		
		TextView pubDescription = (TextView) inflatedView.findViewById(R.id.beergardenDescription);
		pubDescription.setTypeface(typeFace);
		pubDescription.setPadding((int) (width*0.07),(int)(height*0.01),(int)(height*0.03),0);
		pubDescription.setText(Html.fromHtml("<u>Description:</u>"));
		pubDescription.append(" "+cursor.getString(11));
		
		RelativeLayout social = (RelativeLayout)inflatedView.findViewById(R.id.social);
		social.getLayoutParams().height = (int) (height*0.09);
		social.getLayoutParams().width = (int) (width*0.89);
		LinearLayout.LayoutParams socialParams = (LinearLayout.LayoutParams)
				social.getLayoutParams();
		socialParams.setMargins((int) (width*0.05555), 0, 0, 0);
		social.getBackground().setAlpha(220);//set to 0
		
			ImageButton facebook = (ImageButton) inflatedView.findViewById(R.id.facebook);
			facebook.setPadding((int) (width*0.07),0,(int)(width*0.07),(int)(height*0.01));
			facebook.getBackground().setAlpha(0);
			facebook.setScaleType(ScaleType.FIT_XY);
			facebook.setOnClickListener(new View.OnClickListener() { // Set onClickListener
	        	public void onClick(View view) { // Create onClick Method, what is done when button is clicked
		        	 Log.d("PubDetailsFragmentInfo", "facebook button clicked"); // for testing
		        	Intent i = new Intent(view.getContext(), FacebookActivity.class);
		        	i.putExtra("pub", cursor.getString(1));
		        	i.putExtra("address", cursor.getString(2));
		        	startActivity(i);
		        	Toast.makeText(getActivity(), "Posting to Facebook, please wait...", Toast.LENGTH_LONG).show();
		       }});
			
			
			
			ImageButton twitter = (ImageButton) inflatedView.findViewById(R.id.twitter);
			twitter.setPadding(0,0,(int)(width*0.07),(int)(height*0.01));
			twitter.getBackground().setAlpha(0);
			twitter.setScaleType(ScaleType.FIT_XY);
			twitter.setOnClickListener(new View.OnClickListener() { // Set onClickListener
	        	public void onClick(View view) { // Create onClick Method, what is done when button is clicked
		        	 Log.d("PubDetailsFragmentInfo", "twitter button clicked"); // for testing
		        	 String tweetUrl = "https://twitter.com/intent/tweet?text="+"DUBLIN BEER GARDENS: "+text;
		        	 Uri uri = Uri.parse(tweetUrl);
		        	 
		        	 try {
		        		 startActivity(new Intent(Intent.ACTION_VIEW, uri));
		        		 Toast.makeText(getActivity(), "Posting to Twitter, please wait...", 
		        				 Toast.LENGTH_LONG).show();	
		            } catch (android.content.ActivityNotFoundException e) {
		                 Toast.makeText(getActivity(), "Can't send tweet! Please install twitter...", 2).show();
		             }
	        	}});
			
			
			ImageButton message = (ImageButton) inflatedView.findViewById(R.id.message);
			message.setPadding(0,0,(int)(width*0.07),(int)(height*0.01));
			message.getBackground().setAlpha(0);
			message.setScaleType(ScaleType.FIT_XY);
			message.setOnClickListener(new View.OnClickListener() { // Set onClickListener
	        	public void onClick(View view) { // Create onClick Method, what is done when button is clicked
		        	 Log.d("PubDetailsFragmentInfo", "message button clicked"); // for testing
		        	 Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		        	 i.putExtra("sms_body", text);
		        	 i.setType("vnd.android-dir/mms-sms");
		        	 startActivity(i);
	        		}});
			
			
			ImageButton mail = (ImageButton) inflatedView.findViewById(R.id.mail);
			mail.setPadding(0,0,(int)(width*0.06),(int)(height*0.01));
			mail.getBackground().setAlpha(0);
			mail.setScaleType(ScaleType.FIT_XY);
			mail.setOnClickListener(new View.OnClickListener() { // Set onClickListener
	        	public void onClick(View view) { // Create onClick Method, what is done when button is clicked
		        	 Log.d("PubDetailsFragmentInfo", "mail button clicked"); // for testing
		        	 Intent i = new Intent(Intent.ACTION_SEND);
		        	 i.setType("message/rfc822");
		        	// i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
		        	 i.putExtra(Intent.EXTRA_SUBJECT, "Beer-garden");
		        	 i.putExtra(Intent.EXTRA_TEXT   , text);
		        	 try {
		        	     startActivity(Intent.createChooser(i, "Send mail..."));
		        	 } catch (android.content.ActivityNotFoundException ex) {
		        	     //Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		        	 }
	        	
	        	
	        	}});
		
		return inflatedView;
	}
	
	
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
	
	public interface OnDataTransferedInfo{
		public String onDataTransferInfo();
	}
	
	
	private String getImageFileName(String imageLinkLogo) {
		int slashStart = imageLinkLogo.lastIndexOf("/")+1;
		String imageName = imageLinkLogo.substring(slashStart, imageLinkLogo.length());
		Log.d("PubDetailsFragmentInfo ", "imageName = "+imageName);// for testing	
		return imageName;
	}
	
	
	@Override
	public void onResume() {
	        super.onResume();
	    }

 	@Override
	public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        
	    }

	@Override
	public void onPause() {
	        super.onPause();
	       
	    }

	@Override
	public void onDestroy() {
	        super.onDestroy();
	        
	    }
	

	
	
	
}
