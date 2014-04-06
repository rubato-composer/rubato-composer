package org.rubato.rubettes.bigbang.view.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynScore {
	
	private boolean isPlayedInTime;
	private TreeSet<JSynObject> objects;
	private Map<BigBangObject,JSynObject> objectMap;
	private int satelliteType;
	
	public JSynScore(Set<BigBangObject> objects, Form baseForm, boolean isPlayedInTime) {
		this.isPlayedInTime = isPlayedInTime;
		this.objects = new TreeSet<JSynObject>();
		this.objectMap = new HashMap<BigBangObject,JSynObject>();
		if (baseForm.equals(CoolFormRegistrant.FM_SET_FORM)) {
			this.satelliteType = JSynObject.FREQUENCY_MODULATION;
		}
		if (objects != null) {
			this.addObjects(objects);
		}
	}
	
	/**
	 * Creates a score with just one object with the given pitch and velocity
	 */
	public JSynScore(int channel, int pitch, int velocity) {
		this.isPlayedInTime = true;
		this.objects = new TreeSet<JSynObject>();
		this.objects.add(new JSynMonitorObject(channel, pitch, velocity));
	}
	
	public void addObjects(Set<BigBangObject> newObjects) {
		for (BigBangObject currentBBObject : newObjects) {
			if (!this.objectMap.containsKey(currentBBObject)
					&& currentBBObject.getTopDenotatorPath() != null) {
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
	
	public boolean isPlayedInTime() {
		return this.isPlayedInTime;
	}
	
	public boolean contains(BigBangObject bbObject) {
		return this.objectMap.containsKey(bbObject);
	}
	
	public String toString() {
		return this.objects.toString();
	}

}
