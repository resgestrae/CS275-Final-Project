package com.android.nest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class TrainStatus extends Activity
{
	private ArrayList<String> availableTrainList;//List that has info about the available trains
	private int selectedIndex;//index of the chosen listView from the previous activity
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.train_statuslayout);
		
		availableTrainList = this.getIntent().getStringArrayListExtra("TrainsList");//Getting the arraylist passed by the previous activity, containing the available train info
		selectedIndex = this.getIntent().getIntExtra("chosenTrain", -1);//getting the index of the listView selected
		TextView status = (TextView) findViewById(R.id.trainStatus);//Status textView which is updated after the async task
		
		CheckTrainStatus statusUpdate = new CheckTrainStatus();
		statusUpdate.execute();//executing the async task that gets the train 
		//DEBUGGING
		/*for(int i = 0;i<availableTrainList.size();i++)
		{
			System.out.println(availableTrainList.get(i));
		}
		status.setText(availableTrainList.get(0) +"\n" + selectedIndex );*/
		
	}
	public class CheckTrainStatus extends AsyncTask<Void,Void,Void>//gets the status of the particular train
	{
		private String lastArrivedStation;
		private String scheduleTime;
		private String actualTime;
		private String result;
		protected void onPostExecute(Void arg0)
		{
			TextView status = (TextView) findViewById(R.id.trainStatus);//getting the status
			status.setText(result);//setting it with the values of the result
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			try
			{
				int x = 0;
				result = "";
				while((x = availableTrainList.get(selectedIndex).indexOf("Train",x)) != -1)//goes through the loop that has train info and gets the train info
				{	
					System.out.println("xOld: "+x);//DEBUGGING
					//String trainNumber = availableTrainList.get(selectedIndex).split("\n")[0].split(" ")[1];
					String trainNumber = availableTrainList.get(selectedIndex).substring(x).split("\n")[0].split(" ")[1];//Getting the train number
					String wholeTrainDetail [];
					wholeTrainDetail = availableTrainList.get(selectedIndex).substring(x).split("\n");//contains all of the path info

					
					result += wholeTrainDetail[0] + "\n" + wholeTrainDetail[1] + "\n" + wholeTrainDetail[2] + "\n" + wholeTrainDetail[3] + "\n";//updating the result
					
					System.out.println("trainNumber: " + trainNumber);//DEBUGGING
				
					String sURL = "http://www3.septa.org/hackathon/RRSchedules/" + trainNumber;//URL to get the train info
					System.out.println(sURL);//DEBUGGING
				
					URL url = new URL(sURL);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.connect();//Making the connection
			
					JsonParser jp = new JsonParser();
					JsonElement root = jp.parse(new InputStreamReader((InputStream)connection.getContent()));
					JsonArray rootArray = root.getAsJsonArray();//getting the array that has the train info
					
			
					for(int i = 0; i<rootArray.size();i++)//goes through the json array
					{
						System.out.println("INSIDE FOR LOOP");//DEBUGGING
						if(rootArray.get(i).getAsJsonObject().get("act_tm").getAsString().equals("na"))//If the station actual arrival time is na, means that the latest stop is the one before
						{
							if(i == 0)//if na is for the first stop of the train, it means the train has not started
							{
								result += "Status Update\n\tTrain not started yet\n\n";//result to be added to the status textview
								break;
							}
							System.out.println("INSIDE IF");//DEBUGGING
							lastArrivedStation = rootArray.get(i-1).getAsJsonObject().get("station").getAsString();//the latest stop
							scheduleTime = rootArray.get(i-1).getAsJsonObject().get("sched_tm").getAsString();//latest schedule time
							actualTime = rootArray.get(i-1).getAsJsonObject().get("act_tm").getAsString();//latest actual time
							System.out.println("actualTime: "+ actualTime);//DEBUGGING
							result = result + "Status Update\n\tLatest Stop: " + lastArrivedStation + "\n\tScheduled Arrival: " + scheduleTime
											+ "\n\tActual Arrival: " + actualTime + "\n\n";//updating the result
							
							break;//break because the last stop has been found
						}
					
					};
					x = x + "Train".length();//increasing the x, to start for another train, takes care of cases with connecting trains
					System.out.println("xNew: " +x);//DEBUGGING
				System.out.println("Success " + trainNumber);//DEBUGGING
				};
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
}
