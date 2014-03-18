package com.android.nest;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.cloudmine.api.CMObject;

public class MyObject extends CMObject
{
    public static final String CLASS_NAME = "MyObject";
    private String name;
 
    private String ownerName;
    private Hashtable<String,ArrayList<Event>> schedule;
    
    // A no-args constructor is required for deserialization
    public MyObject() 
    {
        super();
    }
    public MyObject(String name) 
    {
        this();
        this.name = name;
    }
     
    // This changes the __class__ naming scheme, for interoperability
    // with existing data from another platform such as iOS
    @Override
    public String getClassName() 
    {
        return CLASS_NAME;
    }
 
    // Getters and Setters are required for each serialized field
    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }
    //public void setSchedule(HashMap<String,HashMap<Time,Event>> schedule)
    public void setSchedule(Hashtable<String,ArrayList<Event>> schedule)
    {
    	this.schedule = schedule;
    }
    public void setOwnerName(String ownerName)
    {
    	this.ownerName = ownerName;
    }
    
    
    public Hashtable<String,ArrayList<Event>> getSchedule()
    {
    	return this.schedule;
    }
    public String getOwnerName()
    {
    	return this.ownerName;
    }
   }
