package org.rubato.rubettes.bigbang.view.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynScore {
	
	private TreeSet<JSynObject> objects;
	private Map<BigBangObject,JSynObject> objectMap;
	private int satelliteType;
	
	public JSynScore(BigBangObjects objects) {
		this.objects = new TreeSet<JSynObject>();
		this.objectMap = new HashMap<BigBangObject,JSynObject>();
		if (objects.getBaseForm().equals(CoolFormRegistrant.FM_SET_FORM)) {
			this.satelliteType = JSynObject.FREQUENCY_MODULATION;
		}
		this.addObjects(objects.getObjects());
	}
	
	/**
	 * Creates a score with just one object with the given pitch and velocity
	 */
	public JSynScore(int pitch, int velocity) {
		this.objects = new TreeSet<JSynObject>();
		this.objects.add(new JSynMonitorObject(pitch, velocity));
	}
	
	public void addObjects(Set<BigBangObject> newObjects) {
		for (BigBangObject currentBBObject : newObjects) {
			if (!this.objectMap.containsKey(currentBBObject) && currentBBObject.getTopDenotatorPath() != null) {
				JSynObject parent = this.objectMap.get(currentBBObject.getParent());
				JSynObject newJSynObject = new JSynObject(parent, currentBBObject, this.satelliteType);
				//only add top level objects to score. others work as satellites
				if (parent == null) {
					this.objects.add(newJSynObject);
				}
				this.objectMap.put(currentBBObject, newJSynObject);
			}
		}
	}
	
	/**
	 * @return a sorted set containing the objects of this score.
	 */
	public TreeSet<JSynObject> getObjects() {
		return this.objects;
	}
	
	public String toString() {
		return this.objects.toString();
	}

}
