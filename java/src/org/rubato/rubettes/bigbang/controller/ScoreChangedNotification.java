package org.rubato.rubettes.bigbang.controller;

import java.util.ArrayList;
import java.util.Set;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class ScoreChangedNotification {
	
	private Denotator score;
	private SelectedObjectsPaths selectedObjectsPaths;
	private DenotatorPath anchorToBeSelected;
	private boolean preview;
	private boolean playback;
	
	public ScoreChangedNotification(Denotator score, SelectedObjectsPaths selectedObjectsPaths, boolean preview, boolean playback) {
		this.score = score;
		this.selectedObjectsPaths = selectedObjectsPaths;
		this.preview = preview;
		this.playback = playback;
	}
	
	public ScoreChangedNotification(PowerDenotator score) {
		this.score = score;
		this.selectedObjectsPaths = new SelectedObjectsPaths(new ArrayList<Set<DenotatorPath>>(), null);
	}
	
	public Denotator getScore() {
		return this.score;
	}
	
	public SelectedObjectsPaths getSelectedObjectsPaths() {
		return this.selectedObjectsPaths;
	}
	
	public DenotatorPath getAnchorToBeSelected() {
		return this.anchorToBeSelected;
	}
	
	public boolean preview() {
		return this.preview;
	}
	
	public boolean playback() {
		return this.playback;
	}

}
