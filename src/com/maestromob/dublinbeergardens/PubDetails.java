package com.maestromob.dublinbeergardens;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.maestromob.dublinbeergardens.PubDetailsFragmentImage.OnDataTransferedImage;
import com.maestromob.dublinbeergardens.PubDetailsFragmentInfo.OnDataTransferedInfo;


public class PubDetails extends FragmentActivity 
			implements OnDataTransferedInfo, OnDataTransferedImage {

	Typeface typeFace;
	Typeface boldTypeFace;
	String pubClicked = "";
	FragmentManager fm;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pubClicked = getIntent().getStringExtra("pubClicked");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pub_details);
		
		fm = getSupportFragmentManager();
		AddHideListener(R.id.beergardenImage, fm.findFragmentById(R.id.info_fragment));
		AddShowListener(R.id.beergardenImageLarge, fm.findFragmentById(R.id.info_fragment));
	}
	
	public String onDataTransferInfo() {
		return pubClicked;
	}
	
	public String onDataTransferImage() {
		return pubClicked;
	}
	
	
	
	public void AddShowListener(int imageClicked, final Fragment fragment) {
		final ImageView v1 = (ImageView) findViewById(imageClicked);
		v1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        ft.setCustomAnimations(R.anim.translate_in, R.anim.translate_out);
		        ft.show(fragment);
		        fragment.getView().findViewById(R.id.beergardenWeb).setClickable(true);
		        fragment.getView().findViewById(R.id.beergardenPhone).setClickable(true);
		        ft.commit();
		        }
			});
		}
	
	
	public void AddHideListener(int imageClicked, final Fragment fragment) {
		final ImageView v2 = (ImageView) findViewById(imageClicked);
		v2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        ft.setCustomAnimations(R.anim.translate_in, R.anim.translate_out);
		        ft.hide(fragment);
		        fragment.getView().findViewById(R.id.beergardenWeb).setClickable(false);
		        fragment.getView().findViewById(R.id.beergardenPhone).setClickable(false);
		        ft.commit();
				
		     }
			});
		}
}
