package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rubato.base.RubatoException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.ZElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.player.JSynModulator;
import org.rubato.rubettes.bigbang.model.player.JSynNote;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.DisplayNoteList;
import org.rubato.rubettes.util.NotePath;
import org.rubato.rubettes.util.PerformanceCheck;

public class DenotatorValueExtractor {
	
	private double[] minValues, maxValues;
	private int maxLayer;
	private DisplayNoteList notes;
	//TreeSet in future for faster searching!!!
	private boolean selectNotes;
	private Set<NotePath> selectedPaths;
	private NotePath selectedAnchor;
	private LayerStates layerStates;
	
	public static final String[] VALUE_NAMES = new String[] {
			"Onset", "Pitch", "Loudness", "Duration", "Voice", "Modulator level",
			"Satellite level", "Sibling number"};
	public final int[][] ELEMENT_PATHS = new int[][] {
			{0,0},{1,0},{2,0},{3,0},{4,0},{5,0}};
	public static final int[][] DENOTATOR_PATHS = new int[][] {
			{0},{1},{2},{3},{4},{5}};
	
	
	public DenotatorValueExtractor() {
		double max = Double.MAX_VALUE;
		double min = Double.MIN_VALUE;
		//duration should alway be shown absolutely -> min 0
		this.minValues = new double[]{max,max,max,0,max,0,0,0};
		this.maxValues = new double[]{min,min,min,min,min,min,min,min};
	}
	
	public DisplayNoteList extractDisplayNotes(ViewController controller, ScoreChangedNotification notification, boolean selectNotes, LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.notes = new DisplayNoteList(controller);
		this.selectNotes = selectNotes;
		this.selectedPaths = notification.getNotesToBeSelected();
		this.selectedAnchor = notification.getAnchorToBeSelected();
		this.layerStates = layerStates;
		try {
			this.extractDisplayNotes(notification.getScore(), null, NotePath.ANCHOR, 0, 0, new NotePath());
		} catch (RubatoException e) { e.printStackTrace(); }
		this.layerStates.removeLayers(this.maxLayer);
		return this.notes;
	}
	
	//recursive method!!
	private List<DisplayNote> extractDisplayNotes(PowerDenotator soundScore, DisplayNote parent, int relation, int modLevel, int satLevel, NotePath currentPath) throws RubatoException {
		List<DisplayNote> siblings = new ArrayList<DisplayNote>();
		PowerDenotator currentPowerset = (PowerDenotator)soundScore.get(currentPath.toIntArray());
		for (int i = 0; i < currentPowerset.getFactorCount(); i++) {
			//create display note
			NotePath currentSubPath = currentPath.getPowersetChildPath(i);
			Denotator currentNote = soundScore.get(currentSubPath.toIntArray());
			DisplayNote currentDisplayNote = this.addNote(currentNote, parent, relation, modLevel, satLevel, i, currentSubPath);
			//recursive call for the note's satellites
			NotePath currentSatellitesPath = currentSubPath.getSatellitesPath();
			if (currentSatellitesPath != null) {
				currentDisplayNote.setChildren(this.extractDisplayNotes(soundScore, currentDisplayNote, NotePath.SATELLITE, modLevel, satLevel+1, currentSatellitesPath));
			}
			//recursive call for the note's modulators
			NotePath currentModulatorsPath = currentSubPath.getModulatorsPath();
			currentDisplayNote.setChildren(this.extractDisplayNotes(soundScore, currentDisplayNote, NotePath.MODULATOR, modLevel+1, satLevel, currentModulatorsPath));
			siblings.add(currentDisplayNote);
		}
		return siblings;
	}
	
	private DisplayNote addNote(Denotator note, DisplayNote parent, int relation, int modLevel, int satLevel, int siblingNumber, NotePath nodePath) throws RubatoException {
		double[] noteValues = this.extractValues(note, parent, modLevel, satLevel, siblingNumber);
		ModuleElement e = note.getElement(this.ELEMENT_PATHS[5]);
		int layer = ((ZElement)e).getValue();
		this.maxLayer = Math.max(layer, this.maxLayer);
		DisplayNote displayNote = new DisplayNote(noteValues, parent, relation, new NotePath(nodePath), layer);
		displayNote.setVisibility(this.layerStates.get(layer));
		this.notes.add(displayNote);
		if (this.selectNotes) {
			if (this.selectedPaths.contains(nodePath)) {
				this.notes.selectNote(displayNote);
			}
		}
		if (this.selectedAnchor != null && this.selectedAnchor.equals(nodePath)) {
			this.notes.setSelectedAnchorNote(displayNote);
		}
		return displayNote;
	}
	
	private double[] extractValues(Denotator note, DisplayNote parent, int modLevel, int satLevel, int siblingNumber) throws RubatoException {
		double[] values = this.extractValues(note, parent);
		this.setValueAndRecordMinAndMax(values, 5, modLevel);
		this.setValueAndRecordMinAndMax(values, 6, satLevel);
		this.setValueAndRecordMinAndMax(values, 7, siblingNumber);
		return values;
	}
	
	public JSynNote extractValues(Denotator node, int bpm) {
		try {
			Denotator note = node.get(new int[]{0});
			double[] noteValues = this.extractValues(note, new double[8]);
			JSynNote jSynNote = new JSynNote(noteValues, bpm);
			this.extractModulators(jSynNote, note, noteValues);
			return jSynNote;
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void extractModulators(JSynNote jSynNote, Denotator note, double[] parentValues) throws RubatoException {
		PowerDenotator modulators = (PowerDenotator)note.get(new int[]{6});
		for (Denotator currentModulator: modulators.getFactors()) {
			double[] modulatorValues = this.extractValues(currentModulator, parentValues);
			JSynModulator jSynMod = jSynNote.addModulator(modulatorValues);
			this.extractModulators(jSynMod, currentModulator, modulatorValues);
		}
	}
	
	private double[] extractValues(Denotator note, DisplayNote parent) throws RubatoException {
		double[] values = new double[8];
		for (int i = 0; i < values.length-3; i++) {
			ModuleElement e = note.getElement(this.ELEMENT_PATHS[i]).cast(RRing.ring);
			double value = ((RElement)e).getValue();
			if (parent != null) {
				value += parent.getValue(i);
			}
			this.setValueAndRecordMinAndMax(values, i, value);
		}
		return values;
	}
	
	private double[] extractValues(Denotator note, double[] parentValues) throws RubatoException {
		double[] values = new double[8];
		for (int i = 0; i < values.length-3; i++) {
			ModuleElement e = note.getElement(this.ELEMENT_PATHS[i]).cast(RRing.ring);
			double value = ((RElement)e).getValue();
			value += parentValues[i];
			this.setValueAndRecordMinAndMax(values, i, value);
		}
		return values;
	}
	
	public double[] getMinValues() {
		return this.minValues;
	}
	
	public double[] getMaxValues() {
		return this.maxValues;
	}
	
	private void setValueAndRecordMinAndMax(double[] values, int i, double value) {
		values[i] = value;
		this.minValues[i] = Math.min(value, this.minValues[i]);
		this.maxValues[i] = Math.max(value, this.maxValues[i]);
	}

}
