package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

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
	public JSynObject addNewObject(JSynObject parent) {
		JSynObject object = new JSynObject(parent);
		this.objects.add(object);
		return object;
	}

}
