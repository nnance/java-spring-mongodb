package com.eventshero.api.dao.impl;

import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.dao.CalendarTagDao;

public class CalendarTagDaoImpl extends GenericDaoImpl<CalendarTag> implements CalendarTagDao {

	public CalendarTagDaoImpl() {
		super(CalendarTag.class);
	}
}
