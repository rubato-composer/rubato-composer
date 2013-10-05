package org.rubato.rubettes.bigbang.controller;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class ScoreChangedNotification {
	
	private Denotator score;
	private SelectedObjectsPaths selectedObjectsPaths;
	//private DenotatorPath anchorToBeSelected;
	private boolean preview;
	
	public ScoreChangedNotification(Denotator score, SelectedObjectsPaths selectedObjectsPaths, boolean preview) {
		this.score = score;
		this.selectedObjectsPaths = selectedObjectsPaths;
		this.preview = preview;
	}
	
	public ScoreChangedNotification(PowerDenotator score) {
		this.score = score;
		this.selectedObjectsPaths = new SelectedObjectsPaths(new ArrayList<List<DenotatorPath>>(), null);
	}
	
	public Denotator getScore() {
		return this.score;
	}
	
	public SelectedObjectsPaths getSelectedObjectsPaths() {
		return this.selectedObjectsPaths;
	}
	
	/*public DenotatorPath getAnchorToBeSelected() {
		return this.anchorToBeSelected;
	}*/
	
	public boolean preview() {
		return this.preview;
	}

}
