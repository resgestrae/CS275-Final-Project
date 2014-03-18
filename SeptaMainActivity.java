package com.android.nest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.Menu;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Intent;
import android.content.SharedPreferences;

public class SeptaMainActivity extends Activity 
{
private ArrayList<String> stationList;
private SharedPreferences p;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);//Calling Parent Constructor
		setContentView(R.layout.activity_septa_main);//Setting the layout
		
		p = PreferenceManager.getDefaultSharedPreferences(this);//This gets the preference. The preference hold all the train station
		final String check_stationList = p.getString("stations", "");
		//Thread splashThread = new Thread()//New Thread to get the Splash Screen
		//{
			//public void run()//method called when thread is executed
			//{
				
                
				if(check_stationList.equals(""))//If the shared preference is empty an Async task is called to get the station name
				{
					System.out.println("PARSING CSV!");//Debugging
					new getStationList().execute();	//Launching the Async task that gets the station name 	
				}
				else//if the station names are already there in the preferences, then just go to the next activity
				{
					finish();//End the thread
				    Intent cv=new Intent(SeptaMainActivity.this,userSelection.class);//The intent to be passed to the next activity
                    startActivity(cv);//Starts another activity
				}
			/*}
		};
	    splashThread.start();//Starts the Thread*/
	    
	
	}

	public class getStationList extends AsyncTask<Void, Void, Void>
	{
		
		protected void onPostExecute(Void arg0)
		{
			
			finish();
			Intent cv=new Intent(SeptaMainActivity.this,userSelection.class);
			//cv.putStringArrayListExtra("stationNames",stationList);
			startActivity(cv);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("http://www3.septa.org/hackathon/Arrivals/station_id_name.csv");//Getting the csv file
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);
				BufferedReader reader = new BufferedReader(
					    new InputStreamReader(
					      response.getEntity().getContent()
					    ));
				String line = "";//csv file line handle
				int i = 0;
				
				String stations = "";//string that holds all the station
				while((line = reader.readLine()) != null)//reading the csv file
				{
					if(i>=1)//skipping the first line of csv file because they are headers
					{
						//System.out.println(line.split(",")[1]);DEBUGGING
						String stationsArray[] = line.split(",");//[1];//
						stations = stations + stationsArray[1] + ",";//updating the station names string
						
					}
					i++;
					
				}
				SharedPreferences.Editor editor = p.edit();//editor to update the shared preference
				editor.putString("stations",stations);//putting the string in the editor
				editor.clear();//clearing the preference
				editor.commit();//updating the preference with the station names
				System.out.println(stations);//DEBUGGING
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			return null;
		}
		
	}
	

}
