package com.eventshero.api.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mongodb.*;

import com.eventshero.api.dao.GenericDao;
import com.eventshero.api.model.ModelBase;

public class GenericDaoImpl<T extends ModelBase> implements GenericDao<T> {
	protected Class<T> clazz;

	public GenericDaoImpl(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public Set<T> getAll() {
	    DB db = PMF.getDB();	    
	    DBCollection table = db.getCollection(this.clazz.getSimpleName());
	    Set<T> results = new HashSet<T>();
//	    @SuppressWarnings("unchecked")
//		Collection<T> items = (Collection<T>) query.execute();
//	    Set<T> results = new HashSet<T>(items);
//	    pm.close();
		return results;
	}

	@Override
	public T get(String id) {
	    DB db = PMF.getDB();	    
	    T result = this.get(id, db);
		return result;
	}
	
	public List<T> getByName(String name) {
	    DB db = PMF.getDB();

	    Query q = pm.newQuery(this.clazz, "name == nameParam");
	    q.declareParameters("String nameParam");
	    
	    @SuppressWarnings("unchecked")
	    List<T> result = (List<T>)q.execute(name);
	    pm.close();
	    return result;		
	}


	protected T get(String id, PersistenceManager pm) {
	    T result = pm.getObjectById(this.clazz, id);
	    return result;
	}
	
	public T getComplete(String id) {
		return this.get(id);
	}
	
	protected T getComplete(String id, PersistenceManager pm) {
		return this.get(id, pm);
	}

	@Override
	public int getCount() {
		return getAll().size();
	}
	
	@Override
	public void store(T obj) {
	    DB db = PMF.getDB();	    
	    DBCollection table = db.getCollection(this.clazz.getSimpleName());
		BasicDBObject document = new BasicDBObject();
		document.put("name", "mkyong");
		document.put("age", 30);
		document.put("createdDate", new Date());
		table.insert(document);	    
	}
	
	@Override
	public T update(T obj) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();	    
	    T result = this.getComplete(obj.getId(), pm);
		if (result != null) {
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
	
	@Override
	public T remove(String id) {
	    PersistenceManager pm = PMF.get().getPersistenceManager();	    
	    T result = this.get(id, pm);
	    pm.deletePersistent(result);
	    pm.close();
		return result;
	}


}
