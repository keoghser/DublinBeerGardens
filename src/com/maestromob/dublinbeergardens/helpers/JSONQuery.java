package com.maestromob.dublinbeergardens.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


public class JSONQuery {
	String url;
	JSONObject jsonObject;
	JSONArray jsonArray;
	
	public JSONQuery (String url) {
		   this.url = url;
		   HttpClient httpclient = new DefaultHttpClient();

		   // Prepare a request object
		   HttpGet httpget = new HttpGet(url); 

		   // Execute the request
		   HttpResponse response;
		   
		   try {
		        response = httpclient.execute(httpget);
		        // Examine the response status
		        Log.d("JSONQuery","statusCode "+response.getStatusLine().toString());

		        // Get hold of the response entity
		        HttpEntity entity = response.getEntity();

		        if (entity != null) {
		            	
		            // A Simple JSON Response Read
		            InputStream instream = entity.getContent();
		            String result= convertStreamToString(instream);
		           		            
		           if (result.contains("[")){
			            int startOfString = result.indexOf("[");
			            int endOfString = result.indexOf("]");
			            result = result.substring(startOfString+1, endOfString+1);
			            Log.i("JSONQuery", "Result is: "+result);// for testing
			            jsonArray = new JSONArray(result);
			            jsonObject = jsonArray.getJSONObject(0);
			           }else{
			        	   jsonObject = new JSONObject(result);
			           }
			        	   
		           // Closing the input stream will trigger connection release
		            instream.close();
		            
		        }


		        } catch (ClientProtocolException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		           e.printStackTrace();
		        } catch (JSONException e) {
		           e.printStackTrace();
		        }
		   
	}
	
	
	private static String convertStreamToString(InputStream is) {
        
        // To convert the InputStream to String we use the BufferedReader.readLine()
        // method. We iterate until the BufferedReader return null which means
        // there's no more data to read. Each line will appended to a StringBuilder
        // and returned as String.
        
       BufferedReader reader = new BufferedReader(new InputStreamReader(is));
       StringBuilder sb = new StringBuilder();

       String line = null;
       try {
           while ((line = reader.readLine()) != null) {
               sb.append(line + "\n");
           }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               is.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return sb.toString();
   }


	public JSONObject getJSON() {
		return jsonObject;
	}


	public JSONArray getJSONArray() {
		return jsonArray;
	}
	

}


