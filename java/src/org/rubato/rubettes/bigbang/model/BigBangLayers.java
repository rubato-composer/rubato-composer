package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class BigBangLayers extends Model {
	
	private List<BigBangLayer> layers;
	
	public BigBangLayers(BigBangController controller) {
		controller.addModel(this);
		this.layers = new ArrayList<BigBangLayer>();
		this.addLayer();
	}
	
	public void addLayer() {
		this.layers.add(new BigBangLayer(this.layers.size()));
		this.firePropertyChange();
	}
	
	public void setLayerName(Integer index, String name) {
		this.layers.get(index).setName(name);
		this.firePropertyChange();
	}
	
	public void setLayerActive(Integer index, Boolean active) {
		this.layers.get(index).setActive(active);
		this.firePropertyChange();
	}
	
	public void setLayerAudible(Integer index, Boolean audible) {
		this.layers.get(index).setAudible(audible);
		this.firePropertyChange();
	}
	
	public void setLayerVisible(Integer index, Boolean visible) {
		this.layers.get(index).setVisible(visible);
		this.firePropertyChange();
	}
	
	public void addObjectsToNewLayer(TreeSet<BigBangObject> objects) {
		this.addLayer();
		this.addObjectsToLayer(this.size()-1, objects);
	}
	
	public void addObjectsToLayer(Integer index, TreeSet<BigBangObject> objects) {
		for (BigBangObject currentObject : objects) {
			currentObject.addLayer(this.get(index));
		}
	}
	
	public void moveObjectsToNewLayer(TreeSet<BigBangObject> objects) {
		this.addLayer();
		this.moveObjectsToLayer(this.size()-1, objects);
	}
	
	public void moveObjectsToLayer(Integer index, TreeSet<BigBangObject> objects) {
		for (BigBangObject currentObject : objects) {
			currentObject.setLayer(this.get(index));
		}
	}
	
	public BigBangLayer get(int index) {
		return this.layers.get(index);
	}
	
	public int size() {
		return this.layers.size();
	}
	
	private void firePropertyChange() {
		this.firePropertyChange(BigBangController.LAYERS, null, this);
	}

}
