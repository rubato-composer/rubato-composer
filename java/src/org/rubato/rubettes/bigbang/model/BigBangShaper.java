package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.base.RubatoException;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangShaper extends BigBangScoreManipulator {
	
	private List<DenotatorPath> notePaths;
	private TreeMap<Double,Double> shapingLocations;
	private boolean copyAndShape;
	private double shapingRange;
	
	public BigBangShaper(BigBangScore score, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(score, properties.getElementPaths());
		this.notePaths = new ArrayList<DenotatorPath>(properties.getNodePaths());
		this.shapingLocations = shapingLocations;
		this.shapingRange = 0.5;
		this.copyAndShape = properties.copyAndTransform();
	}
	
	public Map<DenotatorPath,Double> shapeNotes() {
		//PerformanceCheck.startTask(".pre");
		Map<List<LimitDenotator>,Double> newNoteTracesAndOldYValues = new HashMap<List<LimitDenotator>,Double>();
		
		this.notePaths = this.score.reverseSort(this.notePaths);
		
		Iterator<DenotatorPath> notePathsIterator = this.notePaths.iterator();
		if (notePathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = notePathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.shapeAndAddNextSiblings(newNoteTracesAndOldYValues, firstOfNextSiblings, notePathsIterator);
			}
		}
		//PerformanceCheck.startTask(".find");
		Map<DenotatorPath,Double> newPathsAndOldYValues = new TreeMap<DenotatorPath,Double>();
		this.findPaths(newNoteTracesAndOldYValues, newPathsAndOldYValues);
		return newPathsAndOldYValues; 
	}
	
	private DenotatorPath shapeAndAddNextSiblings(Map<List<LimitDenotator>,Double> newNoteTracesAndOldYValues, DenotatorPath firstSiblingPath, Iterator<DenotatorPath> nodePathsIterator) {
		//PerformanceCheck.startTask(".first_sib");
		List<LimitDenotator> siblings = new ArrayList<LimitDenotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		siblingsPaths.add(firstSiblingPath);
		siblings.add(this.getNode(firstSiblingPath));
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getParentPath();
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (nodePathsIterator.hasNext()) {
			currentSiblingPath = nodePathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isChildOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.getNode(currentSiblingPath));
			} else {
				this.removeShapeAndAdd(newNoteTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths);
				return currentSiblingPath;
			}
		}
		this.removeShapeAndAdd(newNoteTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths);
		return null;
	}
	
	private void removeShapeAndAdd(Map<List<LimitDenotator>,Double> newNoteTracesAndOldYValues, List<LimitDenotator> notes, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths) {
		//PerformanceCheck.startTask(".remove");
		if (!this.copyAndShape) {
			this.score.removeNotes(siblingsPaths);
		}
		this.shapeAndAddNodes(notes, anchorPath, newNoteTracesAndOldYValues);
	}
	
	private void shapeAndAddNodes(List<LimitDenotator> nodesAndNotes, DenotatorPath anchorPath, Map<List<LimitDenotator>,Double> newNoteTracesAndOldYValues) {
		Map<LimitDenotator,Double> newNotesAndOldYValues = new HashMap<LimitDenotator,Double>();
		boolean modulators = nodesAndNotes.get(0).getForm().equals(this.score.noteGenerator.SOUND_NOTE_FORM);
		for (int i = 0; i < nodesAndNotes.size(); i++) {
			//PerformanceCheck.startTask(".map");
			LimitDenotator currentNote = nodesAndNotes.get(i);
			Denotator satellites = null;
			if (!modulators) {
				satellites = currentNote.getFactor(1).copy();
				currentNote = (LimitDenotator) currentNote.getFactor(0);
			}
			LimitDenotator shapedNote = this.shapeNote(currentNote, newNotesAndOldYValues);
			if (!modulators) {
				shapedNote = this.score.getNoteGenerator().createNodeDenotator(shapedNote, satellites);
			}
		}
		//PerformanceCheck.startTask(".add");
		//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE
		List<LimitDenotator> newNotes = new ArrayList<LimitDenotator>(newNotesAndOldYValues.keySet());
		List<DenotatorPath> newPaths = this.score.addNotesToParent(newNotes, anchorPath, modulators);
		List<List<LimitDenotator>> newNoteTraces = this.score.extractNotes(newPaths);
		//PerformanceCheck.startTask(".extract");
		for (int i = 0; i < newNoteTraces.size(); i++) {
			newNoteTracesAndOldYValues.put(newNoteTraces.get(i), newNotesAndOldYValues.get(newNotes.get(i)));
		}
	}
	
	private LimitDenotator shapeNote(LimitDenotator note, Map<LimitDenotator,Double> newNotesAndOldYValues) {
		Double newValue = this.getValueOfClosestLocation(note);
		int[] elementPath = new int[]{this.coordinatePaths[1][0], 0};
		double oldValue = this.score.noteGenerator.getDoubleValue(note, elementPath);
		if (newValue != null) {
			this.score.noteGenerator.modifyNoteDenotator(note, elementPath, newValue);
		}
		note = note.copy();
		newNotesAndOldYValues.put(note, oldValue);
		return note;
	}
	
	private void findPaths(Map<List<LimitDenotator>,Double> newNotesAndOldYValues, Map<DenotatorPath,Double> newPathsAndOldYValues) {
		List<List<LimitDenotator>> notes = new ArrayList<List<LimitDenotator>>(newNotesAndOldYValues.keySet());
		List<DenotatorPath> paths = this.score.findPaths(notes);
		for (int i = 0; i < notes.size(); i++) {
			newPathsAndOldYValues.put(paths.get(i), newNotesAndOldYValues.get(notes.get(i)));
		}
	}

	private Double getValueOfClosestLocation(Denotator note) {
		try {
			int[] elementPath = new int[]{this.coordinatePaths[0][0], 0};
			double xPosition = ((RElement)note.getElement(elementPath).cast(RRing.ring)).getValue();
			Map<Double,Double> subMap = this.shapingLocations.subMap(xPosition-this.shapingRange, xPosition+this.shapingRange);
			Double closestPosition = null;
			double minDistance = Double.MAX_VALUE;
			for (double currentPosition: subMap.keySet()) {
				double currentDistance = Math.abs(currentPosition - xPosition);
				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					closestPosition = currentPosition;
				}
			}
			if (closestPosition != null) {
				return subMap.get(closestPosition);
			}
		} catch (RubatoException e) { 
			e.printStackTrace();
		}
		return null;
	}

}
