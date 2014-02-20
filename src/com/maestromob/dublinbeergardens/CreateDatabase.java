package com.maestromob.dublinbeergardens;

import com.maestromob.dublinbeergardens.helpers.DatabaseAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.Context;
import android.util.Log;

public class CreateDatabase {
	
	DatabaseAdapter db;	
	
	 public CreateDatabase(Context context){
	
	 try {
		String destPath = "/data/data/" + context.getPackageName() + "/databases";
        File f = new File(destPath); 
         if (!f.exists()) //if directory does not exist
         	{            	
         	f.mkdirs(); // make the directory
             f.createNewFile();   // create a new file in the directory            
             db = new DatabaseAdapter(context); // create new DatabaseAdapter
             Log.d("CreateDatabase",""+f.getName()+" was created");// for testing
        	 db.open();
         	 }
         else {
        	 Log.d("CreateDatabase","No database file created");// for testing
         	}
       	 } 
     	catch (FileNotFoundException e) 
 		 		{
 		        e.printStackTrace();
 		 		} 
     	catch (IOException e) 
     			{
 		        e.printStackTrace();
 		    	}
	 
	 }

}
