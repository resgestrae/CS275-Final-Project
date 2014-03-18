package com.android.nest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

public class userSelection extends Activity
{
	protected static String fromStation = "";//String holding the source station
	protected static String toStation = "";//String holding the destination
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);//Calling parent constructor
		setContentView(R.layout.user_selectionlayout);//Setting the layout
		
		
				//Getting the Spinners
				Spinner fromSpinner = (Spinner) findViewById(R.id.spinnerFrom);
				Spinner toSpinner = (Spinner) findViewById(R.id.spinnerTo);
			
				//getting the shared preferences that has all the stations
				SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
				final String stationList[] = p.getString("stations", "").split(",");//This puts all the stations in a string array
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,stationList);//adapter to populate the spinner
				fromSpinner.setAdapter(adapter);
				toSpinner.setAdapter(adapter);
				
				//getting the Button. The Listener is added in the XML
				Button findButton = (Button)findViewById(R.id.findButton);
				
				//listeners for the spinners
				toSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent, View v, int position,long id)
					{
						toStation = stationList[position];//Setting the station name clicked
					}
					public void onNothingSelected(AdapterView<?> parent)
					{
						toStation = "";//when nothing is clicked
					}
				});

				fromSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent, View v, int position,long id)
					{
						fromStation = stationList[position];//setting the station name clicked
					
					}
					public void onNothingSelected(AdapterView<?> parent)
					{
						fromStation = "";//when nothing is clicked
					}
				});
			
				System.out.println(getResources().getString(R.string.fromSelection));//DEBUGGING
				
			
	}
	public void getData(View v)//This the the function that is called when the button is clicked
	{
				Intent newIntent = new Intent(userSelection.this,TrainInfo.class);//New intent
				newIntent.putExtra("fromStation",fromStation);//passing the source station
				newIntent.putExtra("toStation",toStation);//passing the destination station
				userSelection.this.startActivity(newIntent);//calling the activity that gives the possible routes
				
	}

	
}
