package org.kmbtech.tri.server.android;

import java.sql.Timestamp;

public class ObjInfo {
	private String latitude;
	private String longitude;
    private Timestamp timeStamp;
    private String description;

    public ObjInfo(String lati, String longi, Timestamp t, String s){
            this.latitude=lati;
            this.longitude=longi;
            this.timeStamp=t;
            this.description=s;
    }
    public ObjInfo(String lati, String longi, String t, String s){
        this.latitude=lati;
        this.longitude=longi;
        this.description=s;
        timeStamp=new Timestamp(Long.parseLong(t));
}
    public ObjInfo(String lati, String longi, String s){
        this.latitude=lati;
        this.longitude=longi;
        this.description=s;
}
    public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	public String getDescription() {
		return description;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public void setTimeStampString(String t){
		this.timeStamp=new Timestamp(Long.parseLong(t));
	}
}
