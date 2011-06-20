package org.rubato.rubettes.bigbang.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.view.View;

public class Controller implements PropertyChangeListener {

    protected ArrayList<View> registeredViews;
    protected ArrayList<Model> registeredModels;

    public Controller() {
        registeredViews = new ArrayList<View>();
        registeredModels = new ArrayList<Model>();
    }


    public void addModel(Model model) {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

    public void removeModel(Model model) {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }

    public void addView(View view) {
        registeredViews.add(view);
    }

    public void removeView(View view) {
        registeredViews.remove(view);
    }
    
    public int getViewCount(Class<?> c) {
    	int count = 0;
    	for (View currentView: this.registeredViews) {
    		if (currentView.getClass().equals(c)) {
    			count++;
    		}
    	}
    	return count;
    }

    /**
     * 
     * Use this to observe property changes from registered models
     * and propagate them on to all the views.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        for (View view: registeredViews) {
            view.modelPropertyChange(evt);
        }
    }


    /**
     * This is a convenience method that subclasses can call upon
     * to fire property changes back to the models. This method
     * uses reflection to inspect each of the model classes
     * to determine whether it is the owner of the property
     * in question. If it isn't, a NoSuchMethodException is thrown,
     * which the method ignores.
     *
     * @param propertyName = The name of the property.
     * @param newValue = An object that represents the new value
     * of the property.
     */
    protected void setModelProperty(String propertyName, Object newValue) {
        for (Model model: registeredModels) {
            try {
            	Method method = model.getClass()
                	.getMethod("set"+propertyName, new Class[] { newValue.getClass() });
                method.invoke(model, newValue);
            } catch (Exception e) { }
        }
    }
    
    protected void callModelMethod(String methodName) {
    	List<Exception> exceptions = new ArrayList<Exception>();
    	for (Model model: registeredModels) {
            try {
                Method method = model.getClass()
                	.getMethod(methodName);
                method.invoke(model);
            } catch (Exception e) {
            	exceptions.add(e);
            	if (exceptions.size() >= this.registeredModels.size()) {
            		for (Exception currentException: exceptions) {
            			if (!(currentException instanceof NoSuchMethodException)) {
            				currentException.printStackTrace();
            			}
            		}
            	}
            }
        }
    }
    
    protected void callModelMethod(String methodName, Object... arguments) {
    	Class<?>[] classes = new Class[arguments.length];
    	for (int i = 0; i < arguments.length; i++) {
    		classes[i] = arguments[i].getClass();
    	}
    	boolean methodFound = false;
    	for (Model model: registeredModels) {
    		try {
    			methodFound |= this.invokeMethod(model, methodName, classes.clone(), arguments);
    		} catch (Exception e) {
            	e.printStackTrace();
            }
        }
		if (!methodFound) {
			new NoSuchMethodException(methodName+"("+classes[0]+"...)").printStackTrace();
		}
    }
    
    /*
     * RECURSIVE: Tries to call a method demanding any superclass of the first argument.
     */
    protected boolean invokeMethod(Model model, String methodName, Class<?>[] classes, Object... arguments) throws IllegalAccessException, InvocationTargetException {
    	try {
    		Method method = model.getClass().getMethod(methodName, classes);
    		method.invoke(model, arguments);
    		return true;
    	} catch (NoSuchMethodException e) {
    		classes[0] = classes[0].getSuperclass();
    		if (classes[0] != null) {
    			return this.invokeMethod(model, methodName, classes, arguments);
    		}
    		return false;
    	}
    }
	
}
