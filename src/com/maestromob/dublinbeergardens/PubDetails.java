package com.maestromob.dublinbeergardens;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class PubDetails extends FragmentActivity {

	private ImageView beerGardenView;
	Typeface typeFace;
	Typeface boldTypeFace;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pub_details);
		
		//typeFace = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
		//boldTypeFace = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf");
		typeFace = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
		boldTypeFace = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
		
		String image = "Stags_head.png";
		String pathToImageFile = Environment.getExternalStorageDirectory()
				.getAbsoluteFile()+"/BeerGardens/"+image;
		
		beerGardenView = (ImageView) findViewById(R.id.beergardenImage);
		beerGardenView.setImageBitmap(BitmapFactory.decodeFile(pathToImageFile));
		
		TextView pubTitle = (TextView) findViewById(R.id.beergardenTitle);
		pubTitle.setTypeface(boldTypeFace);
		pubTitle.setText("Croke Park Hotel"+"");
		
		TextView pubAddress = (TextView) findViewById(R.id.beergardenAddress);
		pubAddress.setTypeface(typeFace);
		pubAddress.setText("Address: "+"");
		
		/*TextView pubDescription = (TextView) findViewById(R.id.beergardenDescription);
		pubDescription.setTypeface(typeFace);
		pubDescription.setText("Address: "+"");
		
		TextView pubSize = (TextView) findViewById(R.id.beergardenSize);
		pubSize.setTypeface(typeFace);
		pubSize.setText("Address: "+"");
		
		TextView pubSeating = (TextView) findViewById(R.id.beergardenSeating);
		pubSeating.setTypeface(typeFace);
		pubSeating.setText("Address: "+"");
		
		TextView pubPhone = (TextView) findViewById(R.id.beergardenPhone);
		pubPhone.setTypeface(typeFace);
		pubPhone.setText("Address: "+"");
		
		TextView pubWeblink = (TextView) findViewById(R.id.beergardenWeb);
		pubWeblink.setTypeface(typeFace);
		pubWeblink.setText("Address: "+"");*/
		
		
		
	}

}
