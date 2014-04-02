package org.rubato.rubettes.bigbang.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Model {
	
	protected PropertyChangeSupport support;
	
	public Model() {
		this.support = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.support.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.support.removePropertyChangeListener(listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		this.support.firePropertyChange(propertyName, oldValue, newValue);
	}

}
