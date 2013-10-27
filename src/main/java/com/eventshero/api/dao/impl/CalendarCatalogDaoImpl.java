package com.eventshero.api.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.dao.CalendarCatalogDao;
import com.eventshero.api.dao.CalendarTagDao;

public class CalendarCatalogDaoImpl extends GenericDaoImpl<CalendarCatalog> implements CalendarCatalogDao {

	public CalendarCatalogDaoImpl() {
		super(CalendarCatalog.class);
	}

	public EventCalendar syncCalendarInList(String id, EventCalendar cal) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
		EventCalendar result = null;
		try {
		    CalendarCatalog cat = pm.getObjectById(CalendarCatalog.class, id);
			result = cat.syncCalendarInList(cal);
//			int tagCount = result.getTagKeys().size();
			pm.makePersistent(result);
			pm.makePersistent(cat);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();			
		}
		return result;		
	}
	
	public Set<EventCalendar> syncRemove(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    Set<EventCalendar> cals;
		try {
			CalendarCatalog obj = pm.getObjectById(CalendarCatalog.class, id);
			cals = obj.getCalendars();
			for (EventCalendar cal : cals) {
				pm.deletePersistent(cal);
			}
			obj.syncRemove();
			pm.makePersistent(obj);
		} finally {
			pm.close();
		}
		return cals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CalendarTag> getTags(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
//	    pm.getFetchPlan().addGroup("calendarcatalog_items");
	    CalendarCatalog cat = (CalendarCatalog) pm.getObjectById(CalendarCatalog.class, id);
	    List<CalendarTag> result = new ArrayList<CalendarTag>();
	    Set<String> keys = cat.getTagKeys();
	    if ((keys != null) && (keys.size() > 0)) {
		    Query q = pm.newQuery(CalendarTag.class, ":p.contains(id)");
			result = (List<CalendarTag>) q.execute(cat.getTagKeys());
	    }
	    pm.close();
	    return result;
	}

	@Override
	public CalendarTag addTag(String id, String tagId) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    CalendarCatalog cal = pm.getObjectById(CalendarCatalog.class, id);
		CalendarTagDao tagDao = new CalendarTagDaoImpl();
		CalendarTag tag = tagDao.get(tagId);
		cal.addTag(tag);	
		pm.close();
		return tag;
	}
	
	@Override
	public CalendarTag removeTag(String id, String tagId) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    CalendarCatalog cal = pm.getObjectById(CalendarCatalog.class, id);
		CalendarTagDao tagDao = new CalendarTagDaoImpl();
		CalendarTag tag = tagDao.get(tagId);
		cal.removeTag(tag);		
		// not sure why this is required.  According to the docs it shouldn't be
		pm.makePersistent(cal);
		pm.close();
		return tag;
	}

	@Override
	public Set<EventCalendar> getItems(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    CalendarCatalog cat = (CalendarCatalog) pm.getObjectById(CalendarCatalog.class, id);
	    Set<EventCalendar> result = cat.getCalendars();
	    pm.close();
	    return result;
	}


}
