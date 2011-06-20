package org.rubato.rubettes.bigbang.controller;

import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.NotePath;

public class ScoreChangedNotification {
	
	private PowerDenotator score;
	private Set<NotePath> notesToBeSelected;
	private NotePath anchorToBeSelected;
	
	public ScoreChangedNotification(PowerDenotator score, Set<NotePath> notesToBeSelected, NotePath anchorToBeSelected) {
		this.score = score;
		this.notesToBeSelected = notesToBeSelected;
		this.anchorToBeSelected = anchorToBeSelected;
	}
	
	public ScoreChangedNotification(PowerDenotator score) {
		this.score = score;
		this.notesToBeSelected = new TreeSet<NotePath>();
	}
	
	public PowerDenotator getScore() {
		return this.score;
	}
	
	public Set<NotePath> getNotesToBeSelected() {
		return this.notesToBeSelected;
	}
	
	public NotePath getAnchorToBeSelected() {
		return this.anchorToBeSelected;
	}

}
