package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.LeapObjectSelectionAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectSelectionAdapter;

public class SelectionModeAdapter extends DisplayModeAdapter {

	public SelectionModeAdapter(ViewController controller) {
		super(controller);
		//left mouse button (on main area)
		this.mouseInputListeners.add(new ObjectSelectionAdapter(controller));
		this.leapListeners.add(new LeapObjectSelectionAdapter(controller));
		//left mouse button (on a selected note)
		//USE OWN MODE ADAPTER (deselection causes translation at the same time)
		//this.mouseInputListeners.add(new ObjectTranslationAdapter(controller));
	}

}