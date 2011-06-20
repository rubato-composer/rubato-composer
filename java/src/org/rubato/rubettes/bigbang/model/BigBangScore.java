package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.NotePath;
import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class BigBangScore implements Cloneable {
	
	protected SoundNoteGenerator noteGenerator;
	protected PowerDenotator score;
	
	public BigBangScore(SoundNoteGenerator noteGenerator) {
		this.setNoteGenerator(noteGenerator);
		this.score = this.noteGenerator.createEmptyScore();
	}
	
	public Object clone() {
		BigBangScore clone = new BigBangScore(this.noteGenerator);
		clone.score = this.getComposition().copy();
		return clone;
	}
	
	public void setNoteGenerator(SoundNoteGenerator noteGenerator) {
		this.noteGenerator = noteGenerator;
		//reset composition so that it converts if necessary!!
		this.setComposition(this.score);
	}
	
	public boolean setComposition(PowerDenotator newComposition) {
		boolean valid = false;
		if (newComposition != null) {
			this.score = this.noteGenerator.convertScore(newComposition);
			if (this.score != null) {
				valid = true;
			}
		}
		return valid;
	}
	
	public PowerDenotator getLayeredComposition() {
		return this.score;
	}
	
	public PowerDenotator getComposition() {
		//return this.noteGenerator.convertMacroScoreToScore(this.score);
		return this.score;
	}
	
	public NoteGenerator getNoteGenerator() {
		return this.noteGenerator;
	}
	
	/**
	 * Adds a new note in a new node to the top macro score of the composition.
	 * @param values the note to be added
	 * @param layerIndex the layer on which it is to be added
	 * @return the new path of the note
	 */
	public NotePath addNote(double[] values) {
		NotePath topSoundScorePath = new NotePath();
		LimitDenotator newNode = this.noteGenerator.createFMNodeDenotator(values);
		return this.addNote(newNode, topSoundScorePath);
	}
	
	private NotePath addNote(LimitDenotator note, NotePath powersetPath) {
		note = this.internalAddNote(note, powersetPath);
		return this.findPath(note, powersetPath);
	}
	
	/**
	 * Adds copies of the specified nodes to the macro scores of the nodes corresponding to the
	 * specified paths, i.e. they become their satellites and are automatically transformed to
	 * be relative to their anchor node.
	 * @param nodes the nodes to be added
	 * @param anchorPaths
	 * @return the new paths of the added nodes 
	 */
	public List<NotePath> addNotes(List<LimitDenotator> notes, List<NotePath> parentPaths) {
		notes = this.makeNotesRelative(notes, parentPaths);
		List<NotePath> childrenPaths = new ArrayList<NotePath>();
		for (int i = 0; i < notes.size(); i++) {
			NotePath currentParentPath = parentPaths.get(i);
			NotePath currentChildrenPath = null;
			if (currentParentPath != null && currentParentPath.size() > 0) {
				currentChildrenPath = currentParentPath.getChildrenPath(); 
			}
			notes.set(i, this.internalAddNote(notes.get(i), currentChildrenPath));
			childrenPaths.add(currentChildrenPath);
		}
		List<NotePath> newPaths = this.findPaths(notes, childrenPaths);
		return newPaths;
	}
	
	public List<NotePath> addNotes(List<LimitDenotator> notes, List<NotePath> parentPaths, int[] noteFunctions) {
		notes = this.makeNotesRelative(notes, parentPaths);
		List<NotePath> powersetPaths = new ArrayList<NotePath>();
		for (int i = 0; i < notes.size(); i++) {
			NotePath currentParentPath = parentPaths.get(i);
			NotePath currentPowersetPath = null;
			if (currentParentPath != null) {
				if (noteFunctions[i] == NotePath.MODULATOR) {
					currentPowersetPath = currentParentPath.getModulatorsPath();
				} else {
					currentPowersetPath = currentParentPath.getChildrenPath();
				}
			}
			notes.set(i, this.internalAddNote(notes.get(i), currentPowersetPath));
			powersetPaths.add(currentPowersetPath);
		}
		List<NotePath> newPaths = this.findPaths(notes, powersetPaths);
		return newPaths;
	}
	
	public List<NotePath> copyNotesToLayer(List<NotePath> notePaths, int layerIndex) {
		List<NotePath> parentPaths = NotePath.getParentPaths(notePaths);
		List<LimitDenotator> newNotes = new ArrayList<LimitDenotator>();
		for (NotePath currentPath: notePaths) {
			newNotes.add(this.noteGenerator.copyAndSetLayer(this.extractNote(currentPath), layerIndex));
		}
		List<NotePath> newPaths = this.addNotes(newNotes, parentPaths);
		return newPaths;
	}
	
	public Map<NotePath,Integer> moveNotesToLayer(List<NotePath> notePaths, int layerIndex) {
		//TODO: WHY NOT ONLY SET LAYER???
		List<NotePath> parentPaths = NotePath.getParentPaths(notePaths);
		List<Integer> oldLayers = new ArrayList<Integer>();
		List<LimitDenotator> newNotes = new ArrayList<LimitDenotator>();
		for (NotePath currentPath: notePaths) {
			LimitDenotator currentNote = this.extractNote(currentPath);
			oldLayers.add(this.noteGenerator.getLayer(currentNote));
			newNotes.add(this.noteGenerator.copyAndSetLayer(currentNote, layerIndex));
		}
		this.removeNotes(notePaths);
		List<NotePath> newPaths = this.addNotes(newNotes, parentPaths);
		return this.generateMap(newPaths, oldLayers);
	}
	
	private Map<NotePath,Integer> generateMap(List<NotePath> paths, List<Integer> ints) {
		Map<NotePath,Integer> map = new TreeMap<NotePath,Integer>();
		for (int i = 0; i < paths.size(); i++) {
			map.put(paths.get(i), ints.get(i));
		}
		return map;
	}
	
	public List<NotePath> moveNotesToLayers(Map<NotePath,Integer> pathsAndNewLayers) {
		//TODO: WHY NOT ONLY SET LAYER???
		List<NotePath> notePaths = new ArrayList<NotePath>(pathsAndNewLayers.keySet());
		List<NotePath> parentPaths = NotePath.getParentPaths(notePaths);
		List<LimitDenotator> newNotes = new ArrayList<LimitDenotator>();
		for (NotePath currentPath: pathsAndNewLayers.keySet()) {
			newNotes.add(this.noteGenerator.copyAndSetLayer(this.extractNote(currentPath), pathsAndNewLayers.get(currentPath)));
		}
		this.removeNotes(notePaths);
		List<NotePath> newPaths = this.addNotes(newNotes, parentPaths);
		return newPaths;
	}
	
	/**
	 * Adds the notes at the given paths to a children set of the given parent, either
	 * the modulator set or the satellite set
	 * @param notePaths
	 * @param parentPath
	 * @param asModulators if true then the notes are added as modulators
	 * @return
	 */
	public List<NotePath> moveNotesToParent(List<NotePath> notePaths, NotePath parentPath, boolean asModulators) {
		LimitDenotator parentNote = this.extractNote(parentPath);
		List<LimitDenotator> newNotes = this.removeNotes(notePaths);
		NotePath newParentPath = this.findPath(parentNote, parentPath.getPowersetPath());
		return this.addNotesToParent(newNotes, newParentPath, asModulators);
	} 
	
	public List<NotePath> addNotesToParent(List<LimitDenotator> notes, NotePath parentPath, boolean asModulators) {
		NotePath childrenSetPath;
		if (parentPath != null) {
			if (asModulators) {
				childrenSetPath = parentPath.getModulatorsPath();
			} else {
				childrenSetPath = parentPath.getChildrenPath();
			}
			notes = this.makeNotesRelative(notes, parentPath);
		} else {
			childrenSetPath = new NotePath();
		}
		for (int i = 0; i < notes.size(); i++) {
			notes.set(i, this.internalAddNote(notes.get(i), childrenSetPath));
		}
		List<NotePath> newPaths = this.findPaths(notes, childrenSetPath);
		return newPaths;
	}
	
	public List<NotePath> addNotes(List<List<LimitDenotator>> noteLists) {
		for (List<LimitDenotator> notes: noteLists) {
			NotePath newPath = null;
			NotePath childrenPath = null;
			//PowerDenotator currentMacroScore = this.getMacroScore(macroScorePath);
			for (int i = 0; i < notes.size()-1; i++) {
				LimitDenotator currentNote = notes.get(i);
				newPath = this.findPath(currentNote, childrenPath);
				childrenPath = newPath.getChildrenPath();
				//currentMacroScore = this.getMacroScore(macroScorePath);
			}
			this.addNote(notes.get(notes.size()-1), childrenPath);
		}
		return this.findPaths(noteLists);
	}
	
	/*
	 * returns the added element, which might have been converted to another type (node, note)
	 */
	private LimitDenotator internalAddNote(LimitDenotator note, NotePath powersetPath) {
		try {
			note = this.convertNote(note, powersetPath);
			PowerDenotator powerset = this.getPowerset(powersetPath);
			powerset.appendFactor(note);
		} catch (RubatoException e) { e.printStackTrace(); }
		return note;
	}
	
	private LimitDenotator convertNote(LimitDenotator note, NotePath powersetPath) {
		boolean isModulatorPath = powersetPath != null && powersetPath.isModulatorPath();
		if (isModulatorPath && note.getForm().getNameString().equals("SoundNode")) {
			return this.noteGenerator.convertNodeToModulator(note);
		} else if (!isModulatorPath && note.getForm().getNameString().equals("SoundNote")) {
			return this.noteGenerator.convertModulatorToNode(note);
		}
		return note;
	}
	
	public List<NotePath> findPaths(List<List<LimitDenotator>> noteLists) {
		List<NotePath> paths = new ArrayList<NotePath>();
		for (List<LimitDenotator> currentNoteList: noteLists) {
			paths.add(this.findPath(currentNoteList));
		}
		return paths;
	}
	
	/*
	 * finds the path along the given anchors, carriers, satellites and modulators
	 */
	private NotePath findPath(List<LimitDenotator> notes) {
		NotePath path = new NotePath();
		PowerDenotator currentPowerset = this.getPowerset(path);
		LimitDenotator currentNote = this.convertNoteIfNecessary(notes.get(0), path);
		int currentIndex = currentPowerset.indexOf(currentNote);
		path = path.getPowersetChildPath(currentIndex);
		for (int i = 1; i < notes.size(); i++) {
			NotePath currentPath;
			currentIndex = -1;
			if (!path.isModulatorPath()) {
				currentPath = path.getChildrenPath();
				PowerDenotator currentSatellites = this.getPowerset(currentPath);
				currentNote = this.convertNoteIfNecessary(notes.get(i), currentPath);
				currentIndex = currentSatellites.indexOf(currentNote);
			}
			if (currentIndex >= 0) {
				path = path.getChildPath(currentIndex, false);
			} else {
				currentPath = path.getModulatorsPath();
				PowerDenotator currentModulators = this.getPowerset(currentPath);
				currentNote = this.convertNoteIfNecessary(notes.get(i), currentPath);
				currentIndex = currentModulators.indexOf(currentNote);
				if (currentIndex >= 0) {
					path = path.getChildPath(currentIndex, true);
				} else {
					return null;
				}
			}
		}
		return path; 
	}
	
	private List<NotePath> findPaths(List<LimitDenotator> notes, List<NotePath> powersetPaths) {
		List<NotePath> paths = new ArrayList<NotePath>();
		for (int i = 0; i < notes.size(); i++) {
			paths.add(this.findPath(notes.get(i), powersetPaths.get(i)));
		}
		return paths;
	}
	
	private List<NotePath> findPaths(List<LimitDenotator> notes, NotePath powersetPath) {
		List<NotePath> paths = new ArrayList<NotePath>();
		for (int i = 0; i < notes.size(); i++) {
			paths.add(this.findPath(notes.get(i), powersetPath));
		}
		return paths;
	}
	
	/*
	 * Finds the path of a note if it is present in the given powerset. Works for both, sets of
	 * nodes and notes (automatically converts a note into a node when necessary)
	 */
	private NotePath findPath(LimitDenotator note, NotePath powersetPath) {
		note = this.convertNoteIfNecessary(note, powersetPath);
		PowerDenotator powerset = this.getPowerset(powersetPath);
		int index = powerset.indexOf(note);
		if (powersetPath != null) {
			return powersetPath.getPowersetChildPath(index);
		}
		return new NotePath(new int[]{index,0});
	}
	
	private LimitDenotator convertNoteIfNecessary(LimitDenotator note, NotePath powersetPath) {
		if (powersetPath != null && !powersetPath.isModulatorPath()
				&& !note.getForm().equals(this.noteGenerator.SOUND_NODE_FORM)) {
			note = this.noteGenerator.createNodeDenotator(note);
		}
		return note;
	}
	
	private PowerDenotator getPowerset(NotePath powersetPath) {
		if (powersetPath != null && powersetPath.size() > 0) {
			try {
				return (PowerDenotator)this.score.get(powersetPath.toIntArray());
			} catch (RubatoException e) { e.printStackTrace(); } 
		}
		return this.score;
	}
	
	public List<LimitDenotator> getAbsoluteNodes(List<NotePath> nodePaths) {
		return this.getAbsoluteNotes(nodePaths, false);
	}
	
	/* WHATTHE?? WHY SO COMPLICATED AND NOT JUST REMOVENOTES()...
	public void removeNodes(List<NotePath> notePaths, NotePath parentPath) {
		notePaths = this.reverseSort(notePaths);
		NotePath childrenPath;
		if (parentPath != null) {
			childrenPath = parentPath.getChildrenPath();
		} else {
			childrenPath = new NotePath();
		}
		PowerDenotator macroScore = this.getMacroScore(macroScorePath);
		for (NotePath currentNodePath: nodePaths) {
			int lastIndex = currentNodePath.size()-1;
			int nodeIndex = currentNodePath.get(lastIndex);
			macroScore.removeFactor(nodeIndex);
		}
	}*/
	
	/**
	 * removes the SoundNotes of modulators and the SoundNodes of general notes
	 * @param nodePaths the paths of the notes to be removed
	 * @return the removed SoundNotes and SoundNodes (in the order of the note paths)
	 */
	public List<LimitDenotator> removeNotes(List<NotePath> notePaths) {
		return this.getAbsoluteNotes(notePaths, true);
	}
	
	public LimitDenotator removeNote(NotePath notePath) {
		LimitDenotator absoluteNote = this.getAbsoluteNote(notePath);
		this.internalRemoveNote(notePath);
		return absoluteNote;
	}
	
	/**
	 * returns the SoundNotes of modulators and the SoundNodes of general notes
	 * @param nodePaths
	 * @param remove true removes the notes from the score
	 * @return
	 */
	private List<LimitDenotator> getAbsoluteNotes(List<NotePath> notePaths, boolean remove) {
		List<LimitDenotator> notesAndNodes = new ArrayList<LimitDenotator>();
		notePaths = this.reverseSort(notePaths);
		Iterator<NotePath> notePathsIterator = notePaths.iterator();
		if (notePathsIterator.hasNext()) {
			NotePath currentNotePath = notePathsIterator.next();
			while (currentNotePath != null) {
				currentNotePath = this.addNextAbsoluteSiblingsTo(notesAndNodes, currentNotePath, notePathsIterator, remove);
			}
		}
		Collections.reverse(notesAndNodes);
		return notesAndNodes;
	}
	
	private NotePath addNextAbsoluteSiblingsTo(List<LimitDenotator> absoluteNotes, NotePath currentNotePath, Iterator<NotePath> notePathsIterator, boolean remove) {
		List<LimitDenotator> currentSiblings = new ArrayList<LimitDenotator>();
		
		NotePath parentPath = currentNotePath.getParentPath();
		LimitDenotator currentNote = this.getNote(currentNotePath, remove);
		currentSiblings.add(currentNote);
		
		while (notePathsIterator.hasNext()) {
			currentNotePath = notePathsIterator.next();
			if (currentNotePath.isChildOf(parentPath)) {
				currentSiblings.add(this.getNote(currentNotePath, remove));
			} else {
				absoluteNotes.addAll(this.makeNotesAbsolute(currentSiblings, parentPath));
				return currentNotePath;
			}
		}
		absoluteNotes.addAll(this.makeNotesAbsolute(currentSiblings, parentPath));
		return null;
	}
	
	private List<LimitDenotator> makeNotesAbsolute(List<LimitDenotator> noteList, NotePath parentPath) {
		if (parentPath == null) {
			return noteList;
		}
		LimitDenotator absoluteParent = this.getAbsoluteNote(parentPath);
		List<LimitDenotator> absoluteNotes = new ArrayList<LimitDenotator>();
		for (LimitDenotator currentNote: noteList) {
			absoluteNotes.add(this.noteGenerator.makeNoteAbsolute(currentNote, absoluteParent));
		}
		return absoluteNotes;
	}
	
	public LimitDenotator getNote(NotePath notePath, boolean remove) {
		if (remove) {
			return this.internalRemoveNote(notePath);
		}
		return this.extractNote(notePath);
	}
	
	/**
	 * Removes a specific node from its containing soundscore or
	 * a note from its modulator powerset
	 * @param notePath the path to a note
	 */
	private LimitDenotator internalRemoveNote(NotePath notePath) {
		try {
			int[] powersetPath = notePath.getPowersetPath().toIntArray();
			int noteIndex = notePath.getNoteIndex();
			PowerDenotator powerset = (PowerDenotator)this.score.get(powersetPath);
			return (LimitDenotator) powerset.removeFactor(noteIndex);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*??DOESNT REALLY DO ANYTHING DIFFERENT FROM THE NEXT METHOD...
	public List<List<LimitDenotator>> extractNodes(List<NotePath> notePaths, NotePath anchorPath) {
		List<List<LimitDenotator>> anchorNodes = new ArrayList<List<LimitDenotator>>();
		for (NotePath currentNodePath: nodePaths) {
			anchorNodes.add(this.extractNodes(currentNodePath));
		}
		return anchorNodes;
	}*/
	
	/**
	 * extracts the series of parent notes for all given notes
	 * @param nodePaths
	 * @return
	 */
	public List<List<LimitDenotator>> extractNotes(List<NotePath> notePaths) {
		//PerformanceCheck.startTask("..e1");
		List<List<LimitDenotator>> anchorNotes = new ArrayList<List<LimitDenotator>>();
		for (NotePath currentNodePath: notePaths) {
			anchorNotes.add(this.extractNotes(currentNodePath));
		}
		return anchorNotes;
	}
	
	/**
	 * extracts the series of parent notes for the given note
	 * @param notePath
	 * @return
	 */
	public List<LimitDenotator> extractNotes(NotePath notePath) {
		//PerformanceCheck.startTask("..e2");
		List<LimitDenotator> parentNotes = new ArrayList<LimitDenotator>();
		while (notePath != null && notePath.size() > 0) {
			parentNotes.add(0, this.extractNote(notePath));
			notePath = notePath.getParentPath(); 
		}
		return parentNotes;
	}
	
	/**
	 * extracts the node of a satellite or the note of a modulator
	 * @param notePath
	 * @return
	 */
	private LimitDenotator extractNote(NotePath notePath) {
		//PerformanceCheck.startTask("..e3");
		try {
			//EXTRACT NODE OR NOTE!!!!
			return (LimitDenotator) this.score.get(notePath.getElementPath().toIntArray());
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<LimitDenotator> makeNotesRelative(List<LimitDenotator> noteList, List<NotePath> parentPaths) {
		List<LimitDenotator> relativeNotes = new ArrayList<LimitDenotator>();
		for (int i = 0; i < noteList.size(); i++) {
			LimitDenotator currentNote = noteList.get(i);
			NotePath currentParentPath = parentPaths.get(i);
			if (currentParentPath != null && currentParentPath.size() > 0) {
				LimitDenotator absoluteParentNote = this.getAbsoluteNote(currentParentPath);
				relativeNotes.add(this.noteGenerator.makeNoteRelative(noteList.get(i), absoluteParentNote));
			} else {
				relativeNotes.add(currentNote);
			}
		}
		return relativeNotes;
	}
	
	private List<LimitDenotator> makeNotesRelative(List<LimitDenotator> noteList, NotePath parentPath) {
		LimitDenotator absoluteParentNote = this.getAbsoluteNote(parentPath);
		List<LimitDenotator> relativeNotes = new ArrayList<LimitDenotator>();
		for (LimitDenotator currentNote: noteList) {
			relativeNotes.add(this.noteGenerator.makeNoteRelative(currentNote, absoluteParentNote));
		}
		return relativeNotes;
	}
	
	public LimitDenotator getAbsoluteNote(NotePath notePath) {
		List<NotePath> parentPaths = notePath.getParentPaths();
		if (parentPaths.size() <= 0) {
			return this.extractNote(notePath);
		}
		Collections.reverse(parentPaths);
		LimitDenotator currentAbsoluteParent = this.extractNote(parentPaths.get(0));
		for (int i = 1; i < parentPaths.size(); i++) {
			currentAbsoluteParent = this.noteGenerator.makeNoteAbsolute(this.extractNote(parentPaths.get(i)), currentAbsoluteParent);
		}
		return this.noteGenerator.makeNoteAbsolute(this.extractNote(notePath), currentAbsoluteParent);
	}
	
	public LimitDenotator getNote(NotePath notePath) {
		return this.getNote(notePath, false);
	}
	
	public List<NotePath> reverseSort(List<NotePath> paths) {
		List<NotePath> sortedPaths = this.sort(paths); 
		Collections.reverse(sortedPaths);
		return sortedPaths;
	}
	
	public List<NotePath> sort(List<NotePath> paths) {
		return new ArrayList<NotePath>(new TreeSet<NotePath>(paths));
	}
	
	
	// SOUND SCORE FUNCTIONALITY
	
	
	/*public List<DenotatorPath> moveNotesToCarrier(List<DenotatorPath> paths, DenotatorPath carrierPath) {
		//List<LimitDenotator> anchorNodes = this.extractNodes(anchorPath);
		
		List<LimitDenotator> removedNotes = this.removeSatellitesAndModulators(nodePaths);
		removedNotes = this.convertToModulators(removedNotes);
		DenotatorPath newAnchorPath = this.findPath(anchorNodes);
		return this.addNodes(newNodes, newAnchorPath);
	}
	
	/*
	 * converts a modulator note to an anchor node (the note keeps its modulators)
	 *
	private PowerDenotator convertModulatorToAnchor(PowerDenotator modulator) {
		
	}
	
	/*
	 * converts an anchor node to a modulator note (the node's satellites are flattened) 
	 *
	private PowerDenotator convertAnchorToModulator(PowerDenotator anchor) {
		
	}*/
	

}