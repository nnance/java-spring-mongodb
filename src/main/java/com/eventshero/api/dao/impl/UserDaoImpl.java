package com.eventshero.api.dao.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;
import com.eventshero.api.model.GeoLocation;
import com.eventshero.api.model.User;
import com.eventshero.api.model.parsing.MeetUpEventsJson;
import com.eventshero.api.model.parsing.ZVentsJson;
import com.eventshero.api.dao.EventCalendarDao;
import com.eventshero.api.dao.UserDao;

public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {
	private static final Logger log = Logger.getLogger(UserDaoImpl.class.getName());

	public UserDaoImpl() {
		super(User.class);
	}

	public List<EventCalendar> getItems(String id, String zoneName) throws Exception {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    FetchPlan fp = pm.getFetchPlan().addGroup("userGroup");
	    fp.setMaxFetchDepth(1);
	    User user = pm.detachCopy(pm.getObjectById(User.class, id));
	    pm.close();
	    EventCalendarDaoImpl eventDao = new EventCalendarDaoImpl();
	    List<EventCalendar> result = eventDao.getCalendarsById(user.getCalendarKeys(),0);
//	    addGeoLocatedCals(result, geoLoc, zoneName);
	    return result;
		
	}
	public EventCalendar addItem(String id, String itemId){
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    User user = pm.getObjectById(User.class, id);
		EventCalendarDao calDao = new EventCalendarDaoImpl();
		EventCalendar cal = calDao.get(itemId);
		user.addCalendar(cal);
		pm.makePersistent(user);
		pm.close();
		return cal;		
	}
	public EventCalendar removeItem(String id, String itemId){
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    User user = pm.getObjectById(User.class, id);
		EventCalendarDao calDao = new EventCalendarDaoImpl();
		EventCalendar cal = calDao.get(itemId);
		user.removeCalendar(cal);	
		pm.makePersistent(user);
		pm.close();
		return cal;				
	}
	@Override
	public Set<EventCalendar> getOwnedItems(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    User user = (User) pm.getObjectById(User.class, id);
	    Set<EventCalendar> result = user.getOwnedCalendars();
	    pm.close();
	    return result;
	}
	public EventCalendar addOwnedItem(String id, EventCalendar cal){
	    PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			User user = pm.getObjectById(User.class, id);
			user.addOwnedCalendar(cal);
			pm.makePersistent(cal);
			pm.makePersistent(user);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();			
		}
		return cal;		
	}
	public void removeOwnedItem(String id, String itemId){
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    User user = pm.getObjectById(User.class, id);
		EventCalendar cal = null;
		Set<EventCalendar> items = user.getOwnedCalendars();
		for (EventCalendar item : items) {
			if (item.getId().equals(itemId)) {
				cal = item;
				pm.deletePersistent(cal);
			}
		}
		user.removeOwnedCalendar(cal);	
		pm.makePersistent(user);
		pm.close();
	}
	public void updateOwnedItem(String id, EventCalendar cal){
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    User user = pm.getObjectById(User.class, id);
		Set<EventCalendar> items = user.getOwnedCalendars();
		for (EventCalendar item : items) {
			if (item.getId().equals(cal.getId())) {
				try {
					item.merge(cal);
					item.setDateLastUpdated(new Date());
					pm.makePersistent(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		pm.close();
	}


	
	public User getUserByEmail(String emailAddress) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    
	    Query q = pm.newQuery(User.class, "email == emailParam");
	    q.declareParameters("String emailParam");
	    
	    @SuppressWarnings("unchecked")
		List<User> users = (List<User>)q.execute(emailAddress);
	    pm.close();
	    
	    if (users.size() == 0) 
	    	return null;
	    else 
	    	return users.get(0);
	}
	
	@Override
	public User update(User obj) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();	    
	    User result = this.getComplete(obj.getId(), pm);
		if (result != null) {
	    	obj.setAdministrator(result.isAdministrator());
			try {
				result.merge(obj);
				result.setDateLastUpdated(new Date());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pm.makePersistent(result);
	    pm.close();
		return result;
	}
	
	public User setAdministrator(User obj) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();	    
	    User result = this.getComplete(obj.getId(), pm);
		if (result != null) {
	    	result.setAdministrator(obj.isAdministrator());
		}
		pm.makePersistent(result);
	    pm.close();
		return result;		
	}

	private List<EventCalendar> getFollowingCals(String userId) throws Exception {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    FetchPlan fp = pm.getFetchPlan().addGroup("userGroup");
	    fp.setMaxFetchDepth(2);
	    User user = pm.detachCopy(pm.getObjectById(User.class, userId));
	    pm.close();
	    
	    log.info("Loading feed");
	    EventCalendarDaoImpl eventDao = new EventCalendarDaoImpl();
	    List<EventCalendar> cals = eventDao.getCalendarsById(user.getCalendarKeys(),1);
	    log.info("Loading feed: following cals count: " + cals.size());		
	    for (EventCalendar cal : cals)
	    	cal.setSource("following");
	    return cals;
	}

	public List<EventFeed> getEventFeed(String userId, String zoneName) throws Exception {
		List<EventCalendar> cals = this.getFollowingCals(userId);
	    List<EventFeed> feeds = EventFeed.getFeed(cals, zoneName, false);
	    return feeds;
	}
	
	public List<EventFeed> getEventFeed(String userId, String zoneName, GeoLocation geoLoc, String position) throws Exception {
		List<EventCalendar> cals = this.getFollowingCals(userId);
	    addGeoLocatedCals(cals, geoLoc, position, zoneName);
	    List<EventFeed> feeds = EventFeed.getFeed(cals, zoneName, false);
	    return feeds;
	}
	

	public List<EventFeed> getEventFeedAtDate(String userId, String zoneName, String startDate) throws Exception {
		List<EventCalendar> cals = this.getFollowingCals(userId);
	    List<EventFeed> feeds = EventFeed.getFeedAtStartDate(cals, zoneName, startDate);
	    return feeds;
	}

	public List<EventFeed> getEventFeedAtDate(String userId, String zoneName, String startDate, GeoLocation geoLoc, String position) throws Exception {
		List<EventCalendar> cals = this.getFollowingCals(userId);
	    addGeoLocatedCals(cals, geoLoc, position, zoneName);
	    List<EventFeed> feeds = EventFeed.getFeedAtStartDate(cals, zoneName, startDate);
	    return feeds;
	}

	public void addGeoLocatedCals(List<EventCalendar> cals, GeoLocation geoLoc, String position, String zoneName) throws ParseException {
		log.info("Loading geolocated zvents cals");
    	ZVentsJson zvents = new ZVentsJson();
    	List<EventCalendar> zventsCals = zvents.getCalendarList(geoLoc,position,null,zoneName, 50, 30,"nearby");
    	log.info("Loading geolocated zvents cals size: " + zventsCals.size());
    	cals.addAll(zventsCals);

    	log.info("Loading geolocated meetup cals");
    	MeetUpEventsJson muEvents = new MeetUpEventsJson();
    	List<EventCalendar> muEventsCals = muEvents.getCalendarList(geoLoc,position,null,zoneName, 25, 30,"nearby");
    	log.info("Loading geolocated meetup cals size: " + muEventsCals.size());
    	cals.addAll(muEventsCals);
	}
	
	public User getUserProfile(String emailAddress) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();

	    Query q = pm.newQuery(User.class, "email == emailParam");
	    q.declareParameters("String emailParam");
	    
	    @SuppressWarnings("unchecked")
		List<User> users = (List<User>)q.execute(emailAddress);
	    pm.close();
	    
	    if (users.size() == 0)
	    	return null;
	    else 
	    	return users.get(0);
	}
	
	public User getUserByAuthUserId(String property, String userId){		
	    PersistenceManager pm = PMF.get().getPersistenceManager();

	    Query q = pm.newQuery(User.class, property + " == userIdParam");
	    q.declareParameters("String userIdParam");
	    
	    @SuppressWarnings("unchecked")
		List<User> users = (List<User>)q.execute(userId);
	    pm.close();
	    
	    if (users.size() == 0) 
	    	return null;
	    else 
	    	return users.get(0);
	}

}
