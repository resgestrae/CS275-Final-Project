package com.android.nest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
public class TrainInfo extends Activity
{
	protected static String startStation;//source station
	protected static String endStation;//destination station
	
	private ListView listView;//ListView for the output
	private ArrayAdapter<String> adapter;//Adapter to interact with the ListView
    private ArrayList<String> trainInfo;//details of the train
	private ArrayList<String> trainID;//only the ID of the train
	
	
	public void getMap(View v)//when the map view button is called
	{
		Intent getStaticMap = new Intent(TrainInfo.this,mapView.class);//New Intent
		for(int i = 0;i<trainID.size();i++)
		{
			System.out.println(trainID.get(i));
		}//DEBUGGING
		getStaticMap.putStringArrayListExtra("trainNum", trainID);//passing the trainID arrayList
		TrainInfo.this.startActivity(getStaticMap);//calling the activity that displays the map
	}
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);//calling parent constructor
		setContentView(R.layout.train_infolayout);//setting the layout
		
		
			startStation = this.getIntent().getExtras().getString("fromStation");//getting the source station from the passed string in the intent
			endStation = this.getIntent().getExtras().getString("toStation");//getting the destination station from the passed string in the intent
		
			/*ArrayList<String>*/ trainInfo = new ArrayList<String>();//initializing the arrayList that hold the train and their info
		
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainInfo);//initializing the ArrayAdapter
		
			listView = (ListView)findViewById(R.id.train_listView);//getting the listview
			listView.setAdapter(adapter);//Linking the adapter   	
			adapter.setNotifyOnChange(true);//This ensures that the changes in data so the UI components can refresh themselves		
    	
    	

					scheduleData trainSchedule = new scheduleData();
					trainSchedule.execute();//launching the new Asyn task that gets all the train info for the route from start to end stations
					
					listView.setOnItemLongClickListener(new OnItemLongClickListener()//setting up the long click listener for the listview
					{
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View v,int position, long id) 
						{
							//TODO Auto-generated method stub
							String trainNumber = trainInfo.get(position).split("\n")[0].split(" ")[1];//getting the train number
							Intent trainStatusUpdate = new Intent(TrainInfo.this,TrainStatus.class);//new intent
							trainStatusUpdate.putStringArrayListExtra("TrainsList",trainInfo);//adding the trainInfo arrayList to pass by intent
							trainStatusUpdate.putExtra("chosenTrain",position);//passing the index of the chosen listview index
							TrainInfo.this.startActivity(trainStatusUpdate);//starting another activity
							return false;
						}
					});
			
		
	}//end of onCreate
	
		
	public class scheduleData extends AsyncTask<Void,Void,Void>//gets the available train from startStation and endStation
	{
		ArrayList<String> nextTrains;//ArrayList that hold all the available trains

		protected void onPostExecute(Void arg0)//This method is called after the background process is complete 
		{
			adapter.clear();
			for(int i = 0;i<nextTrains.size();i++)
			{
				System.out.println(nextTrains.get(i));
				adapter.add(nextTrains.get(i));
			}
			//adapter.add("Hello World");
			TextView updateMessage = (TextView) findViewById(R.id.train_list_info);
			updateMessage.setText("Here are the available trains for your trip from " + startStation + " to " + endStation);
			adapter.notifyDataSetChanged();//This send the signal that the data is changes and allows the UI components like the ListView to get updated. 			

		}
		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			try
			{
				
				//startStation = Uri.encode(startStation);
				//endStation = Uri.encode(endStation);
				String sURL = "http://www3.septa.org/hackathon/NextToArrive/"+ Uri.encode(startStation) + "/" + Uri.encode(endStation) + "/10";//string to get the next to arrive station
				
				//String sURL = "http://www3.septa.org/hackathon/NextToArrive/Strafford/30th Street Station/10";DEBUGGING
				System.out.println(sURL);//DEUGGING
				URL url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();//making a request
				
				JsonParser jp = new JsonParser();//Json parsing tool
				System.out.println("getcontent\n" + new InputStreamReader((InputStream) request.getContent()));//DEBUGGING
				JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));//Getting content in Json
				System.out.println("root:\n" + root);//DEBUGGING
				JsonArray rootArray = root.getAsJsonArray();//This array holds all the upcoming train info
				
				trainID = new ArrayList<String>();//Initializing the trainID that hold only the train number of the available trains
				
				nextTrains = new ArrayList<String>();//Initializing the nextTrains arraylist that holds the info of all upcoming train
				if(rootArray.size() > 0)//if there are available trains
				{
					
					for(int i = 0;i<rootArray.size();i++)//going through the json array
					{
						JsonObject train = rootArray.get(i).getAsJsonObject();//Getting the original/starting train
						String departureTime = train.get("orig_departure_time").getAsString();//the departure time
						String arrivalTime = train.get("orig_arrival_time").getAsString();//the arrival time
						String delayTime = train.get("orig_delay").getAsString();//delay
						String trainNumber = train.get("orig_train").getAsString();//original/starting train number
						String direct = train.get("isdirect").getAsString();//String that indicates if the path is direct or not
						
						String result = "";//This is the train info added to the nextTrains arraylist
					
						trainID.add(trainNumber);//adding the train number
						if(direct.equals("false"))//if connecting trains are required, additional info is added for result
						{
							String termTrainNumber = train.get("term_train").getAsString();//connecting train number
							String connection = train.get("Connection").getAsString();//Station of the connecting train
							String term_delay = train.get("term_delay").getAsString();//connecting train delay
							String term_depart_time = train.get("term_depart_time").getAsString();//connecting train departure time
							String term_arrival_time = train.get("term_arrival_time").getAsString();//connecting train arrival time to destination
							
							result = "Train " + trainNumber + "\nDeparts from " + startStation + ": " + departureTime 
									+ "\nArrives at " + connection + ": " + arrivalTime + "\nDelay: " + delayTime
									+ "\nConnection Train " + termTrainNumber +"\nDeparts from " + connection + ": " + term_depart_time
									+ "\nArrives at " + endStation + ": " + term_arrival_time + "\nDelay: " + term_delay;//Setting up the result
							nextTrains.add(result);//adding the train info to the array list
							trainID.add(termTrainNumber);//adding the number of the connecting train		
						}
						else//if no connecting train, the result is much simpler
						{
							result = "Train " + trainNumber + "\nDeparts from " + startStation + ": " + departureTime
										+ "\nArrives at " + endStation + ": " + arrivalTime;//setting up
							if(!delayTime.equals("On Time"))
								result = result + "\nDelayed: " + delayTime;
							nextTrains.add(result);//adding the result to nextTrain arraylist
						}
					}
				}else//if no trains ar available
				{
					nextTrains.add("No trains available from " + startStation + " to " + endStation);
				}
					
				
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			return null;
		}
		
	}
}
