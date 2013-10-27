package com.eventshero.api.model;

public class GeoLocation {
	private double latitude;
	private double longitude;
	private double accuracy;
	private double altitude;
	private double altitudeAccuracy;
	private double heading;
	
	public GeoLocation (double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getAltitudeAccuracy() {
		return altitudeAccuracy;
	}
	public void setAltitudeAccuracy(double altitudeAccuracy) {
		this.altitudeAccuracy = altitudeAccuracy;
	}
	public double getHeading() {
		return heading;
	}
	public void setHeading(double heading) {
		this.heading = heading;
	}
}
