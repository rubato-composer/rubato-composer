package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class LayerStates extends Model {
	
	private List<LayerState> states;
	
	public LayerStates(ViewController controller) {
		controller.addModel(this);
		this.states = new ArrayList<LayerState>();
		this.addLayer();
	}
	
	private void addLayer() {
		this.states.add(LayerState.active);
		this.firePropertyChange(ViewController.LAYERS, null, this);
	}
	
	public LayerState get(int layerIndex) {
		while (this.states.size()-1 < layerIndex) {
			this.addLayer();
		}
		return this.states.get(layerIndex);
	}
	
	public int size() {
		return this.states.size();
	}
	
	public void removeLayers(int maxLayer) {
		while (this.states.size()-1 > maxLayer) {
			this.states.remove(this.states.size()-1);
		}
		this.firePropertyChange(ViewController.LAYERS, null, this);
	}
	
	public void changeLayerState(Integer layerIndex) {
		LayerState currentState = this.states.get(layerIndex);
		LayerState nextState = LayerState.invisible;
		if (currentState.equals(LayerState.active)) {
			nextState = LayerState.inactive;
		} else if (currentState.equals(LayerState.invisible)) {
			nextState = LayerState.active;
		}
		this.states.set(layerIndex, nextState);
		this.firePropertyChange(ViewController.LAYERS, null, this);
	}
	
	/**
	 * Returns the smallest index representing an active layer. If no layer is active, the
	 * smallest unused index is returned.
	 *  
	 * @return the index of the smallest active layer
	 */
	public int getSmallestActiveLayerIndex() {
		for (int i = 0; i < this.states.size(); i++) {
			if (this.states.get(i).equals(LayerState.active)) {
				return i;
			}
		}
		return this.states.size();
	}
	
}
