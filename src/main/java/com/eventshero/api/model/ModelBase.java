package com.eventshero.api.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;

import com.google.gson.annotations.Expose;

public class ModelBase {
	@Expose private String id;
	@Expose private String name;
	@Expose private Date dateCreated;
	@Expose private Date dateLastUpdated;
	@Expose private String createdBy;
	@Expose private String lastUpdatedBy;

	public ModelBase() {
		this.dateCreated = new Date();
		this.dateLastUpdated = new Date();
	}

	public void merge(final ModelBase source) throws Exception {
	    BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());

	    // Iterate over all the attributes
	    for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

	      // Only copy writable attributes
	      if (descriptor.getWriteMethod() != null) {
	        Object newValue = descriptor.getReadMethod().invoke(source);
	        Object origValue = descriptor.getReadMethod().invoke(this);

	        // don't update the date created if it is already set
	        if (descriptor.getName().equalsIgnoreCase("datecreated")) {
	        	if ((origValue == null) && (newValue != null)) {
	        		descriptor.getWriteMethod().invoke(this, newValue);
	        	}
	        }
	        else if (newValue != null) {
	        	descriptor.getWriteMethod().invoke(this, newValue);
		    }

	      }
	    }
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}