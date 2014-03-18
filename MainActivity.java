package com.android.nest;

import com.cloudmine.api.persistance.ClassNameRegistry;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private SharedPreferences p;
	static {
	   
		try
		{
			//Cloudmine Custom Class Registration
			ClassNameRegistry.register(MyObject.CLASS_NAME, MyObject.class);
			ClassNameRegistry.register(Event.CLASS_NAME, Event.class);
			ClassNameRegistry.register(CollegeClass.CLASS_NAME, CollegeClass.class);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//removing the title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//removing the notification bar
		
		setContentView(R.layout.activity_main);
		
		p = PreferenceManager.getDefaultSharedPreferences(this);//getting the default preferences
		final String firstTime = p.getString("firstTime", "");//This string tells if the app is being opened for the first time
		System.out.println("FIRSTTIME: " + firstTime);//DEBUGGING
		Thread splashThread = new Thread()//THIS CREATEs a SPLASH SCREEN
		{
			public void run()
			{
				try
				{
					sleep(3000);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				if(firstTime.equals(""))//IF FIRST TIME THE USER MUST ENTER SCHEDULE
				{
					finish();
					Intent inputSchedule = new Intent(MainActivity.this,InputSchedule.class);//inputSchedule class needs to be created
					startActivity(inputSchedule);
				}
				else//GOES TO MAIN MENU
				{

					Intent mainMenu = new Intent(MainActivity.this,MainMenuActivity.class);
					MainActivity.this.startActivity(mainMenu);
				}
			}
		};
		splashThread.start();
		
			
	}
	
	
	
}
