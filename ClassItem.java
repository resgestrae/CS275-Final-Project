package com.android.nest;

public class ClassItem {
	private String className;
	private String buildingName;
	private String roomNum;
	private String day;
	private String hours;

	// Constructors
	public ClassItem(){

	}
	public ClassItem(String c, String b, String n, String d, String h){
		className = c;
		buildingName = b;
		roomNum = n;
		day = d;
		hours = h;
	}

	// Set methods
	public void setClassName(String c){
		className = c;
	}
	public void setBuildingName(String b){
		buildingName = b;
	}
	public void setRoomNum(String n){
		roomNum = n;
	}
	public void setDay(String d){
		day = d;
	}
	public void setHours(String h){
		hours = h;
	}

	// Get methods
	public String getClassName(){
		return className;
	}
	public String getBuildingName(){
		return buildingName;
	}
	public String getRoomNum(){
		return roomNum;
	}
	public String getDay(){
		return day;
	}
	public String getHours(){
		return hours;
	}
}
