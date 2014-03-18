package com.android.nest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class mapView extends Activity 
{
	private ArrayList<String> trainID;//holds all the train numbers that are suggested to the users
	private MapController mapController;
	private ImageView mapView;//ImageView in the layout
	private HashMap<String, String[]> runningTrain_status;//hashmap for the info parsed from the trainView feed
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);//calling the parent constructor
		setContentView(R.layout.mapview_layout);//setting the layout
		
		mapView = (ImageView) findViewById(R.id.mapView);//getting the mapView
		trainID = this.getIntent().getStringArrayListExtra("trainNum");//getting the trainId arraylist
		new trainViewCompare().execute();//executing the async task for trainview, which shows the current running trains
		
		
		
	}
	private class getMap extends AsyncTask<String, Void, Bitmap>//this async draws the map
	{
		Bitmap bmp;
		@Override
		protected Bitmap doInBackground(String... params) {
			
			bmp = null;
			// Connect to the URL
			URL url;
			try {
				String locations = params[0];
				
				//getting the map dimensions
				int mapWidth = mapView.getWidth();
				int mapHeight = mapView.getHeight();
				
				String sURL = "http://maps.googleapis.com/maps/api/staticmap?size=" + mapWidth +"x" + mapHeight + "&markers=color:blue" + "%7C" + locations
								+"&sensor=false&type=roadmap&key=AIzaSyCQJjfP2LdTspb2x7idxZ3Lt6fUOGYp2nI";//URL for the google api static map
				System.out.println(sURL);//DEBUGGING
				url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.setRequestProperty("Content-Type", "application/octet-stream");//setting the request property
//				request.setRequestProperty("Expect", "100-continue");
				request.connect();//connecting
				
				bmp = BitmapFactory.decodeStream(((InputStream) request.getContent()));//decoding the map content to an image
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return bmp;
		}
		
		@Override
		protected void onPostExecute(Bitmap result)
		{
			mapView.setImageBitmap(bmp);//setting the mapView with the map image
		}
	}
	public class trainViewCompare extends AsyncTask<Void,Void,Void>
	{
		
		
		protected void onPostExecute(Void arg0)
		{
			String location = "";//train location
			for(int i = 0;i<trainID.size();i++)//goes through the list of train ID
			{
				String trainKey = trainID.get(i);//Getting the train ID
				System.out.println("Train Key: " + trainKey);//DEBIGGING
				System.out.println(runningTrain_status);
				
				if(runningTrain_status.containsKey(trainKey))//if the trainID num is in the hashMap then, the train is runnng. Therefore get location
				{
					String[] lat_lon = runningTrain_status.get(trainKey);//Getting the location info from the hashmap
					String latitude = lat_lon[0];
					String longitude = lat_lon[1];
					location = location + latitude +"," + longitude +"%7C";//Location formated for the url in the next async task 
					System.out.println("LOCATION: "+location);//DEBUGGING
				}
			}
			
			new getMap().execute(location);			//executing 
		}
		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			try
			{
			String sURL = "http://www3.septa.org/hackathon/TrainView/";//trainview URL
			URL url = new URL(sURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();//making the connection
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream)connection.getContent()));
			JsonArray jsonArray = root.getAsJsonArray();//getting the json array
			
			runningTrain_status = new HashMap<String,String[]>();//initializing the hashmap of train and their info
			for(int i = 0;i<jsonArray.size();i++)//goes through the array
			{
				String key = jsonArray.get(i).getAsJsonObject().get("trainno").getAsString();//getting the train number
				String latitude = jsonArray.get(i).getAsJsonObject().get("lat").getAsString();//getting latitude
				String longitude = jsonArray.get(i).getAsJsonObject().get("lon").getAsString();//longitude
				String[] lat_lon = {latitude,longitude};
				runningTrain_status.put(key, lat_lon);//adding to the hashmap
			}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
