package com.maestromob.dublinbeergardens;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


public class FacebookActivity extends Activity{
	
	private UiLifecycleHelper uiHelper;
	String pub = "";
	String address = "";

	
/*
protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	  
	    if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
	    	// Publish the post using the Share Dialog
	    	
	    	FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
	    	.setLink("https://developers.facebook.com/android")
	    	.build();
	    	uiHelper.trackPendingDialogCall(shareDialog.present());
	    	} else {
	    		// Fallback. For example, publish the post using the Feed Dialog
	    		//publishFeedDialog();
	    	}
	    
	    
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, 
	    		new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, 
	        		Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, 
	        		Bundle data) {
	            Log.i("Activity", "Success!");
	            boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
	            String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
	            String postId = FacebookDialog.getNativeDialogPostId(data);
	        }
	    });
	    finish();
	    
	}
	
	@Override
	protected void onResume() {
	        super.onResume();
	        uiHelper.onResume();
	    }

 	@Override
 	protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        uiHelper.onSaveInstanceState(outState);
	    }

	@Override
	public void onPause() {
	        super.onPause();
	        uiHelper.onPause();
	    }

	@Override
	public void onDestroy() {
	        super.onDestroy();
	        uiHelper.onDestroy();
	    }	
	
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
	            Session.getActiveSession(),params)).setOnCompleteListener(
	            		new OnCompleteListener() {
	            	public void onComplete(Bundle values,FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                      
	                    } else {
	                        
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    
	                } else {
	                    
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	    
}*/
	
	
 private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private void onSessionStateChange(Session session, SessionState state,
            Exception exception) {
        if (state.isOpened()) {
            // System.out.println("Logged in...");
        } else if (state.isClosed()) {
            // System.out.println("Logged out...");
        }
    }
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        pub = getIntent().getStringExtra("pub")+" pub";
        address = getIntent().getStringExtra("address");
       
        
        //Code to get KeyHash value.
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.maestromob.dublinbeergardens", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash : "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        	} catch (NameNotFoundException e) {
	        } catch (NoSuchAlgorithmException e) {
	        }
        facebook();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {
                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                            Exception error, Bundle data) {
                        Log.e("Activity",
                                String.format("Error: %s", error.toString()));
                    }
                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Log.i("Activity", "Success!");
                        boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
        	            String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
        	            String postId = FacebookDialog.getNativeDialogPostId(data);
                    }
                });
        finish();
    }
    
    
  
    
    
    public void facebook() {
       
        if (!checkFbInstalled()) {
            Toast.makeText(getApplicationContext(),
                    "Facebook app not installed!", Toast.LENGTH_LONG).show();
            Intent facebookBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
            startActivity(facebookBrowser);
            finish();
           // return;
            
        }
        
        if (FacebookDialog.canPresentShareDialog(this,
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
                    this).setName(pub)
                    .setLink("https://developers.facebook.com/android")
                    .setPicture("http://beergarden.keoghser.com/imgs/facebook_share.png")
                    .setDescription(address).build(); 
            uiHelper.trackPendingDialogCall(shareDialog.present());
            Toast.makeText(getApplicationContext(), "Loading post...",
                    Toast.LENGTH_SHORT).show();
        } 
    }
    
    
    
    public Boolean checkFbInstalled() {
        PackageManager pm = getPackageManager();
        boolean flag = false;
        try {
            pm.getPackageInfo("com.facebook.katana",PackageManager.GET_ACTIVITIES);
            flag = true;
        } catch (PackageManager.NameNotFoundException e) {
            flag = false;
        }
        return flag;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	uiHelper.onResume();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	uiHelper.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onDestroy() {
    	uiHelper.onDestroy();
    	super.onDestroy();
    }
       
    @Override
    public void onPause() {
    	super.onPause();
    	uiHelper.onPause();
    }
	    
}
    

