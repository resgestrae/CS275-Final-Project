package com.android.nest;

public class CollegeClass extends Event 
{
	private String buildingName;
	private String roomNumber;
	public static final String CLASS_NAME = "CollegeClass";
	
	public CollegeClass()
	{
		super();
	}
	public CollegeClass(String buildingName,String roomNumber)
	{
		super();
		this.buildingName = buildingName;
		this.roomNumber = roomNumber;
	}
	
	@Override
    public String getClassName() 
    {
        return CLASS_NAME;
    }	
	
	
	public void setBuildingName(String buildingName)
	{
		this.buildingName = buildingName;
	}
	public void setRoomNumber(String roomNumber)
	{
		this.roomNumber = roomNumber;
	}
	
	public String getBuildingName()
	{
		return this.buildingName;
	}
	public String getRoomNumber()
	{
		return this.roomNumber;
	}
	public String toString()
	{
		String result;
		result = "ClassName: " + getName() +"\nBuildingName: " + getBuildingName() + "\nRoomNumber: " + getRoomNumber()
				  + "\nDays: " + getDays() + "\nStartTime: " + getStartTime() + "\nEndTime: " + getEndTime();
		return result;
	}
}
