package com.android.nest;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.CMStore;
import com.cloudmine.api.rest.callbacks.CMObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.ObjectModificationResponseCallback;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ScheduleActivity extends Activity 
{
	// Find this in your developer console
	private static final String APP_ID = "bb4c36f4e19d4fe28f855d293e6e11bb";
	// Find this in your developer console
	private static final String API_KEY = "600977a2294c4f5ebde8dbf56f017dc6";
	private ArrayList<ClassItem> classItemList;
	private Hashtable<String,ArrayList<Event>> eventTable;
	private ArrayList<Event> totalEventList;
	private MyObject obj;
	
	private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    
	private TextView tvStartOut;
    private TextView tvEndOut;
    
    private ArrayList<Integer> indexList;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		
		//INITIALIZING CLOUDMINE SESSION
		CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
		 CMStore store = CMStore.getStore();
		  
		 //GETTING THE OBJECTID
		 SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(ScheduleActivity.this);
		 final String objectId = p.getString("ownerScheduleId", "");
		 System.out.println("objectId: " +objectId);
		 if(objectId.equals(""))
			 Toast.makeText(ScheduleActivity.this, "You have not added any schedule.", Toast.LENGTH_LONG).show();
		 
		 store.loadApplicationObjectWithObjectId(objectId, new CMObjectResponseCallback() 
		 {
			 /***Adds Listeners to the ChangeStartTime Button***/
				public void addStartButtonClickListener()
			    {
			    	Button btnChStart = (Button) editDialog.findViewById(R.id.chStartTimeButton);
			    	btnChStart.setOnClickListener(new OnClickListener()
			    	{
			    		

						@Override
			    		public void onClick(View v)
						{
			    			//showDialog(TIME_DIALOG_ID);
							new TimePickerDialog(ScheduleActivity.this, timePickerListener, startHour, startMinute, false).show();
			    		}
			    	});
			    }
				/***Adds Listeners to the ChangeEndTime button***/
			    public void addEndButtonClickListener()
			    {
			        	Button btnChEnd = (Button) editDialog.findViewById(R.id.chEndTimeButton);
			        	btnChEnd.setOnClickListener(new OnClickListener()
			        	{
			        		
			    				@SuppressWarnings("deprecation")
			    				@Override
			        			public void onClick(View v)
			    				{
			        				//showDialog(TIME_DIALOG_ID);
			    					new TimePickerDialog(ScheduleActivity.this, timePickerListener2, endHour, endMinute, false).show();
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
			    
			 private Dialog editDialog;
			    
			 public void onCompletion(CMObjectResponse response) 
		     {

			    	classItemList = new ArrayList<ClassItem>();//STORES ALL THE CLASSITEMS
			    	totalEventList = new ArrayList<Event>();//STORES ALLL THE EVENTS
			    	
			    		//GETTING THE RESPONSE
			    		CMObject object = response.getCMObject(objectId);
			    		
			    		obj = (MyObject)object;//INSTANTIATING MyObject obj
			    		//System.out.println(obj);
			    		eventTable = obj.getSchedule();//Instantiating HashTable<String,ArrayList<Event>> eventTable
			    		//System.out.println(eventTable);
			    		Set<String> keySet = eventTable.keySet();
			    		String daysOfWeek[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
			    		for(String s : daysOfWeek)//For each day of the week
			    		{
			    			if(eventTable.containsKey(s))
			    			{
			    				ArrayList<Event> eventList = eventTable.get(s);
			    				Collections.sort(eventList);//This makes the events for each day in chronological order in the arrayList of that day
			    				//System.out.println(eventList);
			    				Iterator<Event> it = eventList.iterator();
			    				while(it.hasNext())
			    				{
			    					Event event = it.next();
			    					//System.out.println(event);
			    					//EXTRACTING NECCESSARRY INFO
			    					String className = event.getName();
			    					String buildingName = event.getBuildingName();
			    					String roomNumber = event.getRoomNumber();
			    					String day = s;
			    				
			    					String startTime = secToTime(event.getStartTime());
			    					String endTime = secToTime(event.getEndTime());
			    					String timePeriod = startTime + " - " + endTime;
			    				
			    					//CREATING CLASSITEM
			    					ClassItem classItem = new ClassItem();
			    					classItem.setClassName(className);
			    					classItem.setBuildingName(buildingName);
			    					classItem.setRoomNum(roomNumber);
			    					classItem.setDay(day);
			    					classItem.setHours(timePeriod);
			    					
			    					//CONSOLE OUTPUT
			    					System.out.println("ClassName: " + classItem.getClassName());
			    					System.out.println("Building Name: " + classItem.getBuildingName());
			    					System.out.println("Room Number:" + classItem.getRoomNum());
			    					System.out.println("hour: " +classItem.getHours());
			    					System.out.println("Day: " + classItem.getDay());
			    					System.out.println("*************");
			    					classItemList.add(classItem);//ADDING CLASSITEM TO THE LIST
			    					totalEventList.add(event);//ADDING EVENT TO THE LIST
			    				}
			    			}
			    			else//THE KEY IS NOT IN THE TABLE
		    				{
			    				System.out.println(s + " not in table");
			    				continue;
		    				}
			    		}
			        
			    	//POPULATING THE LISTVIEW
			    	ClassItemAdapter classItemAdapter = new ClassItemAdapter(ScheduleActivity.this,android.R.layout.simple_list_item_1,classItemList);
			    	System.out.println(classItemList);
			    	for(int i = 0;i<classItemList.size();i++)
			    	{
			    		System.out.println(classItemList.get(i).getClassName());
			    	}
			    	
			    	ListView listView = (ListView) findViewById(R.id.schedLV);
			    	listView.setAdapter(classItemAdapter);//POPULATES THE LISTVIEW
			    	listView.setOnItemClickListener(new OnItemClickListener()//setting up the long click listener for the listview
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View v,int position, long id) 
						{
							final int positionTotalEventList = position;
							ClassItem selectedItem = classItemList.get(position);
							final String day = selectedItem.getDay();
							String name = selectedItem.getClassName();
							String time = selectedItem.getHours();
							System.out.println("ClassItem:\nName: " + name +"\nDay: " + day + "\ntime: " + time +"\n*************");
							
							//GETTING THE EVENT
							final Event selectedEvent = totalEventList.get(position);
							System.out.println("Event:\n " + selectedEvent);
							
							ArrayList<Event> tempList = eventTable.get(day);
							final int a = tempList.indexOf(selectedEvent);//THE INDEX WHERE THIS EVENT IS 
							System.out.println("Index: " + a);
														
							//EDIT DIALOG POP-UP
							editDialog = new Dialog(ScheduleActivity.this);
                            editDialog.setContentView(R.layout.edit_class_dialog);
                            editDialog.setTitle("Edit Class");
                            
                            /**PRESETTING THE EDIT POP-UP**/
                            
                            EditText tempEditText = (EditText) editDialog.findViewById(R.id.classEditText);
                            tempEditText.setText(selectedEvent.getName());
                            
                            String buildingNamesArray[] = getResources().getStringArray(R.array.buildingNames);
							ArrayList<String> buildingNamesList = new ArrayList<String>(Arrays.asList(buildingNamesArray));
							int b = buildingNamesList.indexOf(selectedEvent.getBuildingName());
							System.out.println("b: " + b);
                            Spinner buildingNameSpinner = (Spinner) editDialog.findViewById(R.id.buildingSpinner);
                            buildingNameSpinner.setSelection(b);
                            
                            tempEditText = (EditText) editDialog.findViewById(R.id.roomNumEditText);
                            tempEditText.setText(selectedEvent.getRoomNumber());
                            
                            
                            for(int i =0;i<selectedEvent.getDays().size();i++)
                            {
                            	System.out.println("selectedEvent Day: " + selectedEvent.getDays().get(i));
                            	if(selectedEvent.getDays().get(i).equals("Monday"))
                            	{
                            		 CheckBox daysCheckBox = (CheckBox)editDialog.findViewById(R.id.monCheckBox);
                            		 daysCheckBox.setChecked(true);
                            	}
                            	if(selectedEvent.getDays().get(i).equals("Tuesday"))
                            	{
                            		 CheckBox daysCheckBox = (CheckBox)editDialog.findViewById(R.id.tuesCheckBox);
                            		 daysCheckBox.setChecked(true);
                            	}
                            	if(selectedEvent.getDays().get(i).equals("Wednesday"))
                            	{
                            		 CheckBox daysCheckBox = (CheckBox)editDialog.findViewById(R.id.wedsCheckBox);
                            		 daysCheckBox.setChecked(true);
                            	}
                            	if(selectedEvent.getDays().get(i).equals("Thursday"))
                            	{
                            		 CheckBox daysCheckBox = (CheckBox)editDialog.findViewById(R.id.thursCheckBox);
                            		 daysCheckBox.setChecked(true);
                            	}
                            	if(selectedEvent.getDays().get(i).equals("Friday"))
                            	{
                            		 CheckBox daysCheckBox = (CheckBox)editDialog.findViewById(R.id.friCheckBox);
                            		 daysCheckBox.setChecked(true);
                            	}
                            	//DISABLING THE CHECKBOXES SO THE USER CAN CHANGE EVRYTHING ELSE BUT DAY FOR THE EVENT
                            	CheckBox checkBox = (CheckBox) editDialog.findViewById(R.id.monCheckBox);
                            	checkBox.setEnabled(false);
                            	checkBox = (CheckBox) editDialog.findViewById(R.id.tuesCheckBox);
                            	checkBox.setEnabled(false);
                            	checkBox = (CheckBox) editDialog.findViewById(R.id.wedsCheckBox);
                            	checkBox.setEnabled(false);
                            	checkBox = (CheckBox) editDialog.findViewById(R.id.thursCheckBox);
                            	checkBox.setEnabled(false);
                            	checkBox = (CheckBox) editDialog.findViewById(R.id.friCheckBox);
                            	checkBox.setEnabled(false);
                            }
                           
                            
                            Button btnSave = (Button) editDialog.findViewById(R.id.saveButton);
                            
                            String startTime = secToTime(selectedEvent.getStartTime());
                            tvStartOut = (TextView) editDialog.findViewById(R.id.startOutTextView);
                            tvStartOut.setText(startTime);
                            
                            String endTime = secToTime(selectedEvent.getEndTime());
                            tvEndOut = (TextView) editDialog.findViewById(R.id.endOutTextView);                            
                            tvEndOut.setText(endTime);
                            
                         	
                            addStartButtonClickListener();
                            addEndButtonClickListener();
                            
                            btnSave.setOnClickListener(new OnClickListener()
                            {
                                    @Override
                                    public void onClick(View v)
                                    {
                                            // TODO Update info to cloudmine
                                    	//Updating Class Name
                                    	EditText input = (EditText) editDialog.findViewById(R.id.classEditText);
                                    	Event editedEvent = totalEventList.get(positionTotalEventList);
                                    	editedEvent.setName(input.getText().toString());
                                    	
                                    	//Updating Building Name
                                    	Spinner buildingNameSpinner = (Spinner) editDialog.findViewById(R.id.buildingSpinner);
                                    	editedEvent.setBuildingName((String)buildingNameSpinner.getSelectedItem());
                                    	
                                    	//Updating Room Number
                                    	input = (EditText) editDialog.findViewById(R.id.roomNumEditText);
                                    	editedEvent.setRoomNumber(input.getText().toString());
                                    	
                                    	/**EDITING DAY FEATURE IS NOT AVAILABLE NOW, BUT WILL BE IN THE FUTURE**/
                                    	/*ArrayList<String> daysList = new ArrayList<String>();
                                    	CheckBox checkBox = (CheckBox)editDialog.findViewById(R.id.monCheckBox);
                                	    if(checkBox.isChecked())
                                	    {
                                	    	String day = checkBox.getText().toString();
                                			daysList.add(day);
                                		}
                                	    
                                	    checkBox = (CheckBox)editDialog.findViewById(R.id.tuesCheckBox);
                                	    if(checkBox.isChecked())
                                	    {
                                	    	String day = checkBox.getText().toString();
                                			daysList.add(day);
                                	    }
                                	    
                                	    checkBox = (CheckBox)editDialog.findViewById(R.id.wedsCheckBox);
                                	    if(checkBox.isChecked())
                                	    {
                                	    	String day = checkBox.getText().toString();
                                			daysList.add(day);
                                	    }
                                	    
                                	    checkBox = (CheckBox)editDialog.findViewById(R.id.thursCheckBox);
                                	    if(checkBox.isChecked())
                                	    {
                                	    	String day = checkBox.getText().toString();
                                			daysList.add(day);
                                	    }
                                	    
                                	    checkBox = (CheckBox)editDialog.findViewById(R.id.friCheckBox);
                                	    if(checkBox.isChecked())
                                	    {
                                	    	String day = checkBox.getText().toString();
                                			daysList.add(day);
                                	    }
                                    	
                                    	editedEvent.setClassDays(daysList);//Updating the days*/
                                    	
                                    	
                                    	System.out.println("StartHour: " + startHour);
                                        long startTimeInSeconds = startHour*3600 + startMinute*60; 
                                        editedEvent.setStartTime(startTimeInSeconds);
                                        
                                        long endTimeInSeconds = endHour*3600 + endMinute*60;
                                	    editedEvent.setEndTime(endTimeInSeconds);
                                	    
                                	    System.out.println("Edited Event:\n " + editedEvent);
                                	    
                                	    
                                	    ArrayList<Event> tempList = eventTable.get(day);
                                	    System.out.println("Index in onclic: " + tempList.indexOf(selectedEvent));
                                	    tempList.set(a, editedEvent);//UPDATED ARRAYLIST<EVENT>
            							eventTable.put(day,tempList);//UPDATED HASHTABLE
            							obj.setSchedule(eventTable);//UPDATED MYOBJECT
            							//SAVING THE OBJECT
            							obj.save(new ObjectModificationResponseCallback()
            						    {
            						        public void onCompletion(ObjectModificationResponse response) 
            						        {
            						        	Toast.makeText(ScheduleActivity.this, "Class Updated", Toast.LENGTH_LONG);
            						        	finish();
            						        	startActivity(getIntent());//refresh activity
            						        }
            						    });
                                    }
                            });
                           
                            Button btnCancel = (Button) editDialog.findViewById(R.id.cancelButton);
                            btnCancel.setOnClickListener(new OnClickListener()
                            {
                                    @Override
                                    public void onClick(View v){
                                            editDialog.dismiss();
                                    }
                            });
                            
                            Button btnDelete = (Button) editDialog.findViewById(R.id.deleteButton);
                            btnDelete.setOnClickListener(new OnClickListener()
                            {
                                    @Override
                                    public void onClick(View v)
                                    {
                                    	ArrayList<Event> tempEventList = eventTable.get(day);
                                    	tempEventList.remove(a);//DELETING FROM THE ARRAYLIST
                                    	eventTable.put(day,tempEventList);//UPDATING THE HASHTABLE
                                    	obj.setSchedule(eventTable);//UPDATING THE MYOBJECT
                                    	obj.save(new ObjectModificationResponseCallback()
            						    {
            						        public void onCompletion(ObjectModificationResponse response) 
            						        {
            						        	Toast.makeText(ScheduleActivity.this, "Class Deleted", Toast.LENGTH_LONG).show();
            						        	System.out.println("You pressed delete");
            						        	finish();
            						        	startActivity(getIntent());
            						        }
            						    });
                                    
                            }
                            });
                            
                           
                            editDialog.show();
                                                	    

						}
					});
					
		     }
		 });
		 
		 
		 /*store.loadApplicationObjectsOfClass(MyObject.CLASS_NAME, new CMObjectResponseCallback() 
		 {
			    public void onCompletion(CMObjectResponse response)
			    {
			        //System.out.println(response);
			    	ArrayList<ClassItem> classItemList = new ArrayList<ClassItem>();
			    	for(CMObject object : response.getObjects())
			        {
			    		MyObject obj = (MyObject)object;
			    		//System.out.println(obj);
			    		Hashtable<String,ArrayList<Event>> eventTable = obj.getSchedule();
			    		//System.out.println(eventTable);
			    		Set<String> keySet = eventTable.keySet();
			    		for(String s : keySet)
			    		{
			    			ArrayList<Event> eventList = eventTable.get(s);
			    			//System.out.println(eventList);
			    			Iterator<Event> it = eventList.iterator();
			    			while(it.hasNext())
			    			{
			    				Event event = it.next();
			    				//System.out.println(event);
			    				String className = event.getName();
			    				String buildingName = event.getBuildingName();
			    				String roomNumber = event.getRoomNumber();
			    				String day = s;
			    				
			    				String startTime = secToTime(event.getStartTime());
			    				String endTime = secToTime(event.getEndTime());
			    				String timePeriod = startTime + " - " + endTime;
			    				
			    				System.out.println("Class Name: " + className);
			    				System.out.println("Building Name: " + buildingName);
			    				System.out.println("Room Number: " + roomNumber);
			    				System.out.println("Day: " + day);
			    				System.out.println("Hours: " + startTime + " - " + endTime);
			    				
			    				ClassItem classItem = new ClassItem();
			    				classItem.setClassName(className);
			    				classItem.setBuildingName(buildingName);
			    				classItem.setRoomNum(roomNumber);
			    				classItem.setDay(day);
			    				classItem.setHours(timePeriod);
			    				classItemList.add(classItem);
			    			}
			    		}
			        }
			    	
			    	ClassItemAdapter classItemAdapter = new ClassItemAdapter(ScheduleActivity.this,android.R.layout.simple_list_item_1,classItemList);
			    	ListView listView = (ListView) findViewById(R.id.schedLV);
			    	listView.setAdapter(classItemAdapter);
			    }
			});*/
		  
		  	
	}//END OF onCreate()
	


	public String secToTime(long sec)
	{
		int hour = (int) (sec/3600);
		int min =(int) ((sec%3600)/60);
		String result = UpdateTime(hour,min);
		
		return result;
	}
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
	/**Listener Method for addClass Button)**/
	public void addClass(View v)
	{
		//finish();
		Intent intent = new Intent(ScheduleActivity.this,AddNewClass.class);
		ScheduleActivity.this.startActivity(intent);
	}
}
