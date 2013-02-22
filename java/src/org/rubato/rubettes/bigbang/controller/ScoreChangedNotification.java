package org.rubato.rubettes.bigbang.controller;

import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.DenotatorPath;

public class ScoreChangedNotification {
	
	private Denotator score;
	private Set<DenotatorPath> notesToBeSelected;
	private DenotatorPath anchorToBeSelected;
	
	public ScoreChangedNotification(Denotator score, Set<DenotatorPath> notesToBeSelected, DenotatorPath anchorToBeSelected) {
		this.score = score;
		this.notesToBeSelected = notesToBeSelected;
		this.anchorToBeSelected = anchorToBeSelected;
	}
	
	public ScoreChangedNotification(PowerDenotator score) {
		this.score = score;
		this.notesToBeSelected = new TreeSet<DenotatorPath>();
	}
	
	public Denotator getScore() {
		return this.score;
	}
	
	public Set<DenotatorPath> getNotesToBeSelected() {
		return this.notesToBeSelected;
	}
	
	public DenotatorPath getAnchorToBeSelected() {
		return this.anchorToBeSelected;
	}

}
