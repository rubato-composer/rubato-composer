package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynScore {
	
	private List<JSynObject> objects;
	
	public JSynScore() {
		this.objects = new ArrayList<JSynObject>();
		//this.notes.add(extractor.extractValues(currentNode, bpm));
	}
	
	public List<JSynObject> getObjects() {
		return this.objects;
	}
	
	/*
	 * Adds a new object to this score and returns it so that it can be edited
	 */
	public JSynObject addNewObject(JSynObject parent, Form form) {
		if (form.equals(CoolFormRegistrant.FM_NODE_FORM) || form.equals(CoolFormRegistrant.SOUND_NOTE_FORM)) {
			if (parent != null) {
				return parent.addModulator();
			}
		}
		JSynObject object = new JSynObject(parent);
		this.objects.add(object);
		return object;
	}
	
	public String toString() {
		return this.objects.toString();
	}

}
