package com.eventshero.api.dao.impl;

import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.dao.CalendarItemDao;

public class CalendarItemDaoImpl extends GenericDaoImpl<CalendarItem> implements CalendarItemDao {

	public CalendarItemDaoImpl() {
		super(CalendarItem.class);
	}

}
