package com.eventshero.api.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class User extends ModelBase {
	
	@Expose private String firstName;
	@Expose private String lastName;
	@Expose private String userName;
	@Expose private String email;
	@Expose private String profileUrl;
	@Expose private String profilePicture;
	private String googleAuthToken;
	private int googleAuthTokenExpiration;
	@Expose private String googleUserId;
	private String twitterAuthToken;
	private String twitterAuthTokenSecret;
	@Expose private String twitterUserId;
	private String facebookAuthToken;
	private int facebookAuthTokenExpiration;
	@Expose private String facebookUserId;
	private boolean administrator;
	private Set<EventCalendar> calendars;
	private Set<UserCalendarMap> calendarMaps;
	
	public boolean addCalendar(EventCalendar cal) {
		UserCalendarMap map = new UserCalendarMap();
		map.setCalendar(cal);
		return calendarMaps.add(map);
	}
	
	public void removeCalendar(EventCalendar cal) {
		for (UserCalendarMap calMap : calendarMaps) {
			if (calMap.getCalendarKey().equals(cal.getId())) {
				calendarMaps.remove(calMap);
				break;
			}
		}		
	}
	
	public Set<String> getCalendarKeys() {
		Set<String> cals = new HashSet<String>();
		for (UserCalendarMap map : calendarMaps) {
			cals.add(map.getCalendarKey());
		}
		for (EventCalendar cal : this.getOwnedCalendars()) {
			cals.add(cal.getId());
		}
		return cals;
	}
	
	public User() {
		this.calendarMaps = new HashSet<UserCalendarMap>();
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public Set<UserCalendarMap> getCalendarMaps() {
		if (calendarMaps == null) 
				calendarMaps = new HashSet<UserCalendarMap>();
		return calendarMaps;
	}

	public String getGoogleAuthToken() {
		return googleAuthToken;
	}

	public void setGoogleAuthToken(String googleAuthToken) {
		this.googleAuthToken = googleAuthToken;
	}

	public int getGoogleAuthTokenExpiration() {
		return googleAuthTokenExpiration;
	}

	public void setGoogleAuthTokenExpiration(int googleAuthTokenExpiration) {
		this.googleAuthTokenExpiration = googleAuthTokenExpiration;
	}

	public String getGoogleUserId() {
		return googleUserId;
	}

	public void setGoogleUserId(String googleAuthUserId) {
		this.googleUserId = googleAuthUserId;
	}

	public String getTwitterAuthToken() {
		return twitterAuthToken;
	}

	public void setTwitterAuthToken(String twitterAuthToken) {
		this.twitterAuthToken = twitterAuthToken;
	}

	public String getTwitterAuthTokenSecret() {
		return twitterAuthTokenSecret;
	}

	public void setTwitterAuthTokenSecret(String twitterAuthTokenSecret) {
		this.twitterAuthTokenSecret = twitterAuthTokenSecret;
	}

	public String getTwitterUserId() {
		return twitterUserId;
	}

	public void setTwitterUserId(String twitterUserId) {
		this.twitterUserId = twitterUserId;
	}

	public String getFacebookAuthToken() {
		return facebookAuthToken;
	}

	public void setFacebookAuthToken(String facebookAuthToken) {
		this.facebookAuthToken = facebookAuthToken;
	}

	public int getFacebookAuthTokenExpiration() {
		return facebookAuthTokenExpiration;
	}

	public void setFacebookAuthTokenExpiration(int facebookAuthTokenExpiration) {
		this.facebookAuthTokenExpiration = facebookAuthTokenExpiration;
	}

	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getAuthToken() {
		String token;
		if (googleAuthToken != null)
			token = googleAuthToken;
		else if (twitterAuthToken != null)
			token = twitterAuthToken;
		else 
			token = facebookAuthToken;
		return token;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	public Set<EventCalendar> getOwnedCalendars() {
		if (calendars == null) 
			calendars = new HashSet<EventCalendar>();
		return calendars;
	}
	public EventCalendar addOwnedCalendar(EventCalendar cal) {
		if (calendars == null) 
			calendars = new HashSet<EventCalendar>();
		calendars.add(cal);
		return cal;
	}
	public void removeOwnedCalendar(EventCalendar cal) {
		calendars.remove(cal);
	}
	public boolean ownsCalendar(String id) {
		Set<EventCalendar> cals = this.getOwnedCalendars();
		for (EventCalendar cal : cals) {
			if (cal.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
}