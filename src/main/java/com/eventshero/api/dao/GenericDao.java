package com.eventshero.api.dao;

import java.util.List;
import java.util.Set;
import com.eventshero.api.model.ModelBase;

public interface GenericDao<T extends ModelBase> {

	Set<T> getAll();	
	T get(String id);
	List<T> getByName(String name);
	T getComplete(String id);
	int getCount();
	void store(T obj);
	T update(T obj);
	T remove(String id);
}
