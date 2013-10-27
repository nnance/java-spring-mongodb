package com.eventshero.api.dao;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;
import com.eventshero.api.model.GeoLocation;
import com.eventshero.api.model.User;

public interface UserDao extends GenericDao<User> {
	public List<EventFeed> getEventFeed(String userId, String zoneName) throws Exception;
	public List<EventFeed> getEventFeed(String userId, String zoneName, GeoLocation geoLoc, String position) throws Exception;
	public List<EventFeed> getEventFeedAtDate(String userId, String zoneName, String startDate) throws Exception;
	public List<EventFeed> getEventFeedAtDate(String userId, String zoneName, String startDate, GeoLocation geoLoc, String position) throws Exception;
	public void addGeoLocatedCals(List<EventCalendar> cals, GeoLocation geoLoc, String position, String zoneName) throws ParseException;
	public List<EventCalendar> getItems(String id, String zoneName) throws Exception;
	public EventCalendar addItem(String id, String itemId);
	public EventCalendar removeItem(String id, String itemId);
	public Set<EventCalendar> getOwnedItems(String id);
	public EventCalendar addOwnedItem(String id, EventCalendar calendar);
	public void removeOwnedItem(String id, String itemId);
	public void updateOwnedItem(String id, EventCalendar cal);
	public User getUserByEmail(String emailAddress);
	public User getUserByAuthUserId(String property, String userId);
	public User getUserProfile(String emailAddress);
	public User setAdministrator(User user);
}
