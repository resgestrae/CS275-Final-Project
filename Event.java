package com.android.nest;



import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import com.cloudmine.api.CMObject;

public class Event extends CMObject implements Comparable<Event>
{
	public static final String CLASS_NAME = "Event";
	private String name;
	//private Time startTime;
	//private Time endTime;
	private long startTime;
	private long endTime;
	private String location;
	private ArrayList<String> days;
	
	private String buildingName;
	private String roomNumber;
	
	private Date date;

	//constructors
	 public Event() 
	 {
	        super();
	 }
	   
	public Event(String Name, long startTime,long endTime,String location)
	{
		super();
		this.name = Name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
	}
	
	@Override
    public String getClassName() 
    {
        return CLASS_NAME;
    }	
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	public void setEndTime(long endTime)
	{
		this.endTime = endTime;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public void setClassDays(ArrayList<String> day)
	{
		this.days = day;
	}
	public void setBuildingName(String buildingName)
	{
		this.buildingName = buildingName;
	}
	public void setRoomNumber(String roomNumber)
	{
		this.roomNumber = roomNumber;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	
	
	public String getName()
	{
		return this.name;
	}
	public long getStartTime()
	{
		return this.startTime;
	}
	public long getEndTime()
	{
		return this.endTime;
	}
	public String getLocation()
	{
		return this.location;
	}
	public ArrayList<String> getDays()
	{
		return this.days;
	}
	public String getBuildingName()
	{
		return this.buildingName;
	}
	public String getRoomNumber()
	{
		return this.roomNumber;
	}
	public Date getDate()
	{
		return this.date;
	}
	
	
	public String toString()
	{
		String result;
		if(!getBuildingName().equals(""))
		{
			result = "ClassName: " + getName() +"\nBuildingName: " + getBuildingName() + "\nRoomNumber: " + getRoomNumber()	
					+ "\nDays: " + getDays() + "\nStartTime: " + getStartTime() + "\nEndTime: " + getEndTime();
		}
		else
		{
			result = "EventName: " + getName() + "\nLocation: " + getLocation() + "\nDays: " + getDays() + "\nDate: " + getDate()
					+ "\nStart Time: " + getStartTime() + "\nEnd Time: " + getEndTime();
		}
			
		return result;
	}

	@Override
	public int compareTo(Event event) 
	{
		// TODO Auto-generated method stub
		return(int)(this.getStartTime() - event.getStartTime());
	}
	
}
