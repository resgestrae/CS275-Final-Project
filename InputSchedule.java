package com.android.nest;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.rest.callbacks.ObjectModificationResponseCallback;
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class InputSchedule extends Activity 
{
	/**CloudMine Info*/
	private static final String APP_ID = "bb4c36f4e19d4fe28f855d293e6e11bb";
	private static final String API_KEY = "600977a2294c4f5ebde8dbf56f017dc6";
	
	
	
	/** Widgets - GUI**/
    private EditText classNameTv;
	private Spinner spBuildingName;
    private EditText roomNumberTv;
	private TextView tvStartOut;
    private TextView tvEndOut;
    private Button btnChStart;
    private Button btnChEnd;
    
    
    static final int TIME_DIALOG_ID = 1111;
       
    /**Class Info inputs**/
    private String ownerName = "owner";
    private String className;
    private String buildingName;
    private ArrayList<String> daysList;
    private String roomNumber;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    
    
    
    /**Adapter and HashTable**/
    private ArrayAdapter<String> adptBuildingName;
    private Hashtable<String,ArrayList<Event>> classList;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_schedule_layout);//Setting the layout
        
        /**Set ArrayAdapter adptBuildingName to Spinner spBuildingName**/
        String[] buildingNames = getResources().getStringArray(R.array.buildingNames);//Get String Array of Building Names from resources
        spBuildingName = (Spinner) findViewById(R.id.buildingSpinner);//Get the spinner
        adptBuildingName = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildingNames);
        spBuildingName.setAdapter(adptBuildingName);
        
        // Building Name Item selected Listener        
        spBuildingName.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	@Override
        	public void onItemSelected(AdapterView<?> adapter, View v, int pos, long id)
        	{
        		//On selecting a spinner item...
        		String item = adapter.getItemAtPosition(pos).toString();
        		buildingName = item;
        	}
        	@Override
            public void onNothingSelected(AdapterView<?> arg0) 
        	{
        		// TODO Auto-generated method stub
                 }
        });
        
        //Instantiating the hashtable
        classList = new Hashtable<String,ArrayList<Event>>();
        
        
        //Adding Listeners for the Change Time Button
        addStartButtonClickListener();
        addEndButtonClickListener();
               
        
        // Display current time on screen Start
        final Calendar c = Calendar.getInstance();
        // Current Hour
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        int minute = c.get(Calendar.MINUTE);
        
        // Set current time into start time text view
        tvStartOut = (TextView) findViewById(R.id.startOutTextView);
        tvStartOut.setText(UpdateTime(hour, minute));
        tvEndOut = (TextView) findViewById(R.id.endOutTextView);
        tvEndOut.setText(UpdateTime(hour,minute));
        
                
        /**Welcome Dialog**/
        final Dialog dialog = new Dialog(this);
        final Dialog nameDialog = new Dialog(this);
        dialog.setContentView(R.layout.init_toast);
        nameDialog.setContentView(R.layout.name_dialog);
        dialog.setTitle("Hello!");
        nameDialog.setTitle("NEST");
        
        TextView tv2 = (TextView) nameDialog.findViewById(R.id.initAlertNameTextView);
        TextView tv = (TextView) dialog.findViewById(R.id.initAlertTextView);
  
        tv2.setText("Please enter you name: ");
        tv.setText("Before you start using NEST, please take a few short minutes to set up your class schedule.");
        
        //Listener for the dialog ok buttons
        Button btnInit2 = (Button) nameDialog.findViewById(R.id.initAlertNameButton);
        btnInit2.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v){
        		EditText temp = (EditText) nameDialog.findViewById(R.id.alertNameEditText); 
        				//(EditText)findViewById(R.id.alertNameEditText);
        		
        		ownerName = temp.getText().toString();
        		nameDialog.dismiss();
        	}
        });
        
        Button btnInit = (Button) dialog.findViewById(R.id.initAlertButton);
        btnInit.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v){
        		dialog.dismiss();
        	}
        });
        
        dialog.show();
        nameDialog.show();
    }//END OF onCreate Method
    
    
    /***Adds Listeners to the ChangeStartTime Button***/
    public void addStartButtonClickListener()
    {
    	btnChStart = (Button) findViewById(R.id.chStartTimeButton);
    	btnChStart.setOnClickListener(new OnClickListener()
    	{
    		

			@Override
    		public void onClick(View v)
			{
    			//showDialog(TIME_DIALOG_ID);
				new TimePickerDialog(InputSchedule.this, timePickerListener, startHour, startMinute, false).show();
    		}
    	});
    }
    /***Adds Listeners to the ChangeEndTime button***/
    public void addEndButtonClickListener()
    {
        	btnChEnd = (Button) findViewById(R.id.chEndTimeButton);
        	btnChEnd.setOnClickListener(new OnClickListener()
        	{
        		
    				@Override
        			public void onClick(View v)
    				{
        				//showDialog(TIME_DIALOG_ID);
    					new TimePickerDialog(InputSchedule.this, timePickerListener2, endHour, endMinute, false).show();
    				}
        	});
    }
    
    /***Listener for the EndTime picker**/
    private TimePickerDialog.OnTimeSetListener timePickerListener2 = new TimePickerDialog.OnTimeSetListener() 
    {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) 
		{
			// TODO Auto-generated method stub
			endHour = hourOfDay;
			endMinute = minutes;

			String time = UpdateTime(endHour, endMinute);
			tvEndOut.setText(time);

		}
	};
    /***Listener for the StartTime picker**/
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() 
    {
		@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minutes) 
			{
				// TODO Auto-generated method stub
				startHour = hourOfDay;
				startMinute = minutes;

				String time = UpdateTime(startHour, startMinute);
				tvStartOut.setText(time);
			}
    };

	/*private static String UtilTime(int value)
	{
		if(value<10)
			return"0" + String.valueOf(value);
		else
			return String.valueOf(value);
	}*/

	/***Used to convert 24hr format to 12 hr format***/
	private String UpdateTime(int hours, int mins)
	{

		String timeSet="";
		if (hours> 12){
			hours -= 12;
			timeSet = "PM";
		}else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		}else if (hours == 12){
			timeSet="PM";
		}else
			timeSet="AM";

		String minutes = "";
		if (mins<10)
			minutes = "0" +mins;
		else
			minutes =String.valueOf(mins);

		// Append in a string builder
		String aTime = new StringBuilder().append(hours).append(":").append(minutes).append(" ").append(timeSet).toString();
		return aTime;

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**Checkbox listener - POSSIBLE USE IN THE FUTURE**/
	public void getCheckedItem(View v)
	{
	}
	/***Method called when AddClass button is clicked***/
	public void addClass(View v)
	{
		    
		//GETTING CLASSNAME FROM USER INPUT
		classNameTv = (EditText)findViewById(R.id.classEditText);
		className = classNameTv.getText().toString();
		classNameTv.setText("");
		
		/***buildingName already set in Spinner Listener***/
		spBuildingName.setSelection(0);
		
	    //THIS STORES ALL THE DAYS OF THE EVENT
		daysList = new ArrayList<String>();
	    CheckBox checkBox = (CheckBox)findViewById(R.id.monCheckBox);
	    if(checkBox.isChecked())
	    {
	    	String day = checkBox.getText().toString();
			daysList.add(day);
			checkBox.setChecked(false);
	    }
	    
	    checkBox = (CheckBox)findViewById(R.id.tuesCheckBox);
	    if(checkBox.isChecked())
	    {
	    	String day = checkBox.getText().toString();
			daysList.add(day);
			checkBox.setChecked(false);
	    }
	    
	    checkBox = (CheckBox)findViewById(R.id.wedsCheckBox);
	    if(checkBox.isChecked())
	    {
	    	String day = checkBox.getText().toString();
			daysList.add(day);
			checkBox.setChecked(false);
	    }
	    
	    checkBox = (CheckBox)findViewById(R.id.thursCheckBox);
	    if(checkBox.isChecked())
	    {
	    	String day = checkBox.getText().toString();
			daysList.add(day);
			checkBox.setChecked(false);
	    }
	    
	    checkBox = (CheckBox)findViewById(R.id.friCheckBox);
	    if(checkBox.isChecked())
	    {
	    	String day = checkBox.getText().toString();
			daysList.add(day);
			checkBox.setChecked(false);
	    }
	    
	    //GETTING ROOM NUMBER
	    roomNumberTv = (EditText)findViewById(R.id.roomNumEditText);
	    roomNumber = roomNumberTv.getText().toString();
	    roomNumberTv.setText("");
	    
	    //Time time = new Time(startHour,startMinute,0);
	    long startTimeInSeconds = startHour*3600 + startMinute*60; 
		tvStartOut.setText("");
	    //time = new Time(endHour,endMinute,0);
	    long endTimeInSeconds = endHour*3600 + endMinute*60;
		tvEndOut.setText("");
	    
		System.out.println("Class Name: " + className);
		System.out.println("Building Name: " + buildingName);
		System.out.println("List of Days: " + daysList);
		System.out.println("Room Number: " + roomNumber);
		System.out.println("StartTime: " + startTimeInSeconds);
		System.out.println("End Time: " + endTimeInSeconds);//START HERE
		
		Event collegeClass = new Event();
	    collegeClass.setName(className);
	    collegeClass.setBuildingName(buildingName);
	    collegeClass.setRoomNumber(roomNumber);
	    collegeClass.setClassDays(daysList);
	    collegeClass.setStartTime(startTimeInSeconds);
	    collegeClass.setEndTime(endTimeInSeconds);
	    
	        
	    for(int i = 0;i<daysList.size();i++)
	    {
	    	String dayKey = daysList.get(i);
	    	if(classList.containsKey(dayKey))
	    	{
	    		classList.get(dayKey).add(collegeClass);
	    	}
	    	else
	    	{
	    		ArrayList<Event> newKeyList = new ArrayList<Event>();
	    		newKeyList.add(collegeClass);
	    		classList.put(dayKey, newKeyList);
	    	}
	    }
	       
	}
	/**METHOD WHEN USER PRESSES DONE BUTTON**/
	public void finishSchedule(View v)
	{
				
		/**Checking the Hashtable to see that all of the classes are in it***/
		Set<String> set = classList.keySet();
		
		for(String s: set)
		{
			System.out.println(classList.get(s));//Prints the arrayList associated with this key
		}
		
		 // initialize CloudMine library
	    CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
	    //Toast.makeText(InputSchedule.this, "text", Toast.LENGTH_LONG).show();DEBUGGER
	    
	    MyObject academicClass = new MyObject();
	    academicClass.setOwnerName(ownerName);
	    academicClass.setSchedule(classList);
	    academicClass.setName("MyObject");
	   
	    final String objectId = academicClass.getObjectId();  // object id automatically generated
	    
	    academicClass.save(new ObjectModificationResponseCallback()
	    {
	        public void onCompletion(ObjectModificationResponse response) 
	        {
	            System.out.println("Response was a success? " + response.wasSuccess());
	            System.out.println("We: " + response.getKeyResponse(objectId) + " the simple object");
	    
	            //STORING THE OBJECTID IN SHARED PREFERENCES. IT WILL BE USED FOR QUERYING//
	            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(InputSchedule.this);
	    		SharedPreferences.Editor editor = p.edit();//editor to update the shared preference
	    		editor.putString("firstTime","No");//putting the string in the editor
	    		editor.putString("Username",ownerName);
	    		editor.putString("ownerScheduleId",objectId);
	    		editor.commit();//updating the preference 
	    		
	    		final String firstTime = p.getString("firstTime", "");//Checking if the preference is set
	    		System.out.println("SharedPreferenced: " + firstTime);
	            Intent intent = new Intent(InputSchedule.this,MainMenuActivity.class);
	            InputSchedule.this.startActivity(intent);
	            
	        }
	     
	        public void onFailure(Throwable e, String msg) 
	        {
	            System.out.println("We failed: " + e.getMessage());
	        }
	    });
	}
}
