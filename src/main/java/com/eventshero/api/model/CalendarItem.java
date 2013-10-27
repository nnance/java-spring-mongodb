package com.eventshero.api.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.Expose;

public class CalendarItem extends ModelBase {

	@Expose private String description;
	@Expose private String location;
	@Expose private String location2;
	@Expose private String info;
	@Expose private String startDate;
	@Expose private String startTime;
	@Expose private String startDateTime;
	@Expose private String calId;
	@Expose private int duration;
	@Expose private boolean allDayEvent = false;
	@Expose private String teamName;
	@Expose private String opponentName;
	@Expose private String teamScore;
	@Expose private String opponentScore;
	@Expose private String recapUrl;
	@Expose private String boxScoreUrl;
	@Expose private String ticketUrl;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation2() {
		return location2;
	}
	public void setLocation2(String location2) {
		this.location2 = location2;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public boolean isAllDayEvent() {
		return allDayEvent;
	}
	public void setAllDayEvent(boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}
	public String getCalId() {
		return calId;
	}
	public void setCalId(String calId) {
		this.calId = calId;
	}
	public Calendar getDateTime(java.util.TimeZone timeZone) {
		Calendar dateTime = null;
		SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat myTimeFormat = new SimpleDateFormat("hh:mm a");

		try {
			Date scheduleDay;
			scheduleDay = myFormat.parse(this.startDate);
			Date scheduleTime;
			if (this.allDayEvent)
				scheduleTime = myTimeFormat.parse("12:00 AM");
			else
				scheduleTime = myTimeFormat.parse(this.startTime);

			Calendar jCalDay = Calendar.getInstance();
			jCalDay.setTime(scheduleDay);
			int date = jCalDay.get(Calendar.DATE);
			int month = jCalDay.get(Calendar.MONTH);
			int year = jCalDay.get(Calendar.YEAR);
			
			Calendar jCalTime = Calendar.getInstance();
			jCalTime.setTime(scheduleTime);
			int hourOfDay = jCalTime.get(Calendar.HOUR_OF_DAY);
			int minute = jCalTime.get(Calendar.MINUTE);

			dateTime = Calendar.getInstance();
			dateTime.setTimeZone(timeZone);
			dateTime.set(year, month, date, hourOfDay, minute, 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateTime;
	}
	
	public String getStartDateTime(Calendar dateTime, java.util.TimeZone formatZone) {
		SimpleDateFormat sortFormat = new SimpleDateFormat("yyyyMMdd kk:mm");
		sortFormat.setTimeZone(formatZone);
		// this is here so that the field on the object is set to allow GSON to serialize it
//		Calendar dateTime = this.getDateTime(timeZone);
		if (dateTime != null)
			this.startDateTime = sortFormat.format(dateTime.getTime());
		return this.startDateTime;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getOpponentName() {
		return opponentName;
	}
	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}
	public String getTeamScore() {
		return teamScore;
	}
	public void setTeamScore(String teamScore) {
		this.teamScore = teamScore;
	}
	public String getOpponentScore() {
		return opponentScore;
	}
	public void setOpponentScore(String opponentScore) {
		this.opponentScore = opponentScore;
	}
	public String getRecapUrl() {
		return recapUrl;
	}
	public void setRecapUrl(String recapUrl) {
		this.recapUrl = recapUrl;
	}
	public String getBoxScoreUrl() {
		return boxScoreUrl;
	}
	public void setBoxScoreUrl(String boxScoreUrl) {
		this.boxScoreUrl = boxScoreUrl;
	}
	public String getTicketUrl() {
		return ticketUrl;
	}
	public void setTicketUrl(String ticketUrl) {
		this.ticketUrl = ticketUrl;
	}	
}