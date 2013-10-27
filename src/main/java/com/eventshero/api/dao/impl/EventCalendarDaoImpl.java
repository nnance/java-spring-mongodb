package com.eventshero.api.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.eventshero.api.dao.CalendarItemDao;
import com.eventshero.api.dao.CalendarTagDao;
import com.eventshero.api.dao.EventCalendarDao;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;

public class EventCalendarDaoImpl extends GenericDaoImpl<EventCalendar> implements EventCalendarDao {

	@Override
	protected EventCalendar getComplete(String id, PersistenceManager pm) {
	    FetchPlan fp = pm.getFetchPlan().addGroup("calendarGroup");
	    fp.setMaxFetchDepth(1);
	    EventCalendar result = this.get(id,pm);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<EventCalendar> getCalendarsById(Set<String> keys, int fetchDepth) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    if (fetchDepth > 0) {
		    FetchPlan fp = pm.getFetchPlan().addGroup("calendarGroup");
		    fp.setMaxFetchDepth(fetchDepth);
	    }
	    List<EventCalendar> result = new ArrayList<EventCalendar>();
	    if ((keys != null) && (keys.size() > 0)) {
		    Query q = pm.newQuery(EventCalendar.class, ":p.contains(id)");
		    List<EventCalendar> resultSet = (List<EventCalendar>) q.execute(keys);
			result = (List<EventCalendar>) pm.detachCopyAll(resultSet);
	    }
	    pm.close();
		return result;		
	}
	
	public List<EventFeed> getEventFeed(String feedId, String zoneName) throws Exception {
	    Set<String> feedIds = new HashSet<String>();
	    feedIds.add(feedId);
	    List<EventCalendar> cals = getCalendarsById(feedIds,1);
	    
	    List<EventFeed> feeds = EventFeed.getFeed(cals, zoneName, true);
	    return feeds;
	}
	

	@SuppressWarnings("unchecked")
	public List<EventCalendar> getCalendarsByTag(String tag) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    
		CalendarTagDao tagDao = new CalendarTagDaoImpl();
	    
		List<EventCalendar> result = null;
		
	    Query q = pm.newQuery(EventCalendar.class);
	    String[] tags = tag.split(",");
	    if (tags.length > 0) {
	    	String filter = "";
	    	for (int i = 0; i < tags.length; i++) {
			    String tagId = tagDao.getByName(tags[i]).iterator().next().getId();		    
			    filter = filter + "&& tagKeys == '" + tagId + "' ";		    		    		    		
	    	}
	    	q.setFilter(filter.substring(3));
	    }
	    else {
		    String tagId = tagDao.getByName(tag).iterator().next().getId();		    
		    q.setFilter("tagKeys == '" + tagId + "'");		    	
	    }
	    result = (List<EventCalendar>) q.execute();

	    pm.close();
		return result;				
	}
	
	public EventCalendar sync(String id) throws Exception {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.getComplete(id, pm);
		cal.sync();
	    pm.makePersistent(cal);
		pm.close();
		return cal;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CalendarTag> getTags(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.get(id,pm);
	    List<CalendarTag> result = new ArrayList<CalendarTag>();
	    Set<String> keys = cal.getTagKeys();
	    if ((keys != null)  && (keys.size() > 0)) {
		    Query q = pm.newQuery(CalendarTag.class, ":p.contains(id)");
			result = (List<CalendarTag>) q.execute(cal.getTagKeys());
	    }
	    pm.close();
	    return result;
	}

	@Override
	public CalendarTag addTag(String id, String tagId) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = pm.getObjectById(EventCalendar.class, id);
		CalendarTagDao tagDao = new CalendarTagDaoImpl();
		CalendarTag tag = tagDao.get(tagId);
		cal.addTag(tag);	
	    pm.makePersistent(cal);
		pm.close();
		return tag;
	}
	
	@Override
	public CalendarTag removeTag(String id, String tagId) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.get(id, pm);
		CalendarTagDao tagDao = new CalendarTagDaoImpl();
		CalendarTag tag = tagDao.get(tagId);
		cal.removeTag(tag);	
		pm.makePersistent(cal);
		pm.close();
		return tag;
	}

	@Override
	public Set<CalendarItem> getItems(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.getComplete(id, pm);
	    Set<CalendarItem> result = new HashSet<CalendarItem>(pm.detachCopyAll(cal.getItems()));
	    pm.close();
	    // this is required so that the field for the sort date is set for GSON processing
	    for (CalendarItem item : result) {
	    	java.util.TimeZone timeZone = java.util.TimeZone.getTimeZone(cal.getTimeZone());
	    	item.getStartDateTime(item.getDateTime(timeZone),timeZone);
	    }
	    return result;
	}

	@Override
	public CalendarItem addItem(String id, CalendarItem item) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.get(id, pm);
		cal.addItem(item);	
	    pm.makePersistent(cal);
		pm.close();
		return item;
	}
	
	@Override
	public CalendarItem removeItem(String id, String itemId) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    EventCalendar cal = this.getComplete(id, pm);
		CalendarItemDao itemDao = new CalendarItemDaoImpl();
		CalendarItem item = itemDao.get(itemId);
	    cal.removeItem(item);
	    pm.makePersistent(cal);
		pm.close();
		return item;
	}

	public EventCalendarDaoImpl() {
		super(EventCalendar.class);
	}

}
