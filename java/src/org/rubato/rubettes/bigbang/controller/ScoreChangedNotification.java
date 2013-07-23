package org.rubato.rubettes.bigbang.controller;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPathss;
import org.rubato.rubettes.util.DenotatorPath;

public class ScoreChangedNotification {
	
	private Denotator score;
	private SelectedObjectsPathss selectedObjectsPaths;
	//private DenotatorPath anchorToBeSelected;
	private boolean preview;
	private boolean playback;
	
	public ScoreChangedNotification(Denotator score, SelectedObjectsPathss selectedObjectsPaths, boolean preview, boolean playback) {
		this.score = score;
		this.selectedObjectsPaths = selectedObjectsPaths;
		this.preview = preview;
		this.playback = playback;
	}
	
	public ScoreChangedNotification(PowerDenotator score) {
		this.score = score;
		this.selectedObjectsPaths = new SelectedObjectsPathss(new ArrayList<List<DenotatorPath>>(), null);
	}
	
	public Denotator getScore() {
		return this.score;
	}
	
	public SelectedObjectsPathss getSelectedObjectsPaths() {
		return this.selectedObjectsPaths;
	}
	
	/*public DenotatorPath getAnchorToBeSelected() {
		return this.anchorToBeSelected;
	}*/
	
	public boolean preview() {
		return this.preview;
	}
	
	public boolean playback() {
		return this.playback;
	}

}
