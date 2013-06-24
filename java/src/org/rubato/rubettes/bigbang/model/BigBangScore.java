package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.ListDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.ObjectGenerator;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class BigBangScore implements Cloneable {
	
	protected ObjectGenerator objectGenerator;
	protected Denotator score;
	protected Denotator initialComposition;
	
	public BigBangScore(Form baseForm) {
		this.objectGenerator = new ObjectGenerator();
		this.setForm(baseForm);
	}
	
	public Object clone() {
		BigBangScore clone = new BigBangScore(this.objectGenerator.getBaseForm());
		clone.score = this.getComposition().copy();
		return clone;
	}
	
	public boolean setForm(Form baseForm) {
		boolean validForm = this.objectGenerator.setBaseForm(baseForm);
		if (validForm) {
			this.setInitialComposition(this.objectGenerator.createEmptyScore());
		}
		return validForm;
	}
	
	public void resetScore() {
		this.score = this.initialComposition.copy();
	}
	
	/**
	 * @return true if newComposition is of an allowed form type TODO: refactor and make more general!!
	 */
	public boolean setInitialComposition(Denotator initialComposition) {
		if (initialComposition != null && this.objectGenerator.formIsSoundScoreCompatible(initialComposition.getForm())) {
			initialComposition = new SoundNoteGenerator().convertScore(initialComposition);
			this.initialComposition = initialComposition;
			this.resetScore();
			return true;
		} else if (initialComposition != null && this.objectGenerator.setBaseForm(initialComposition.getForm())) {
			//this.score = this.noteGenerator.convertScore(newComposition);
			this.initialComposition = initialComposition;
			this.resetScore();
			return true;
		}
		return false;
	}
	
	public void setScore(Denotator score) {
		if (score.getForm().equals(this.score.getForm())) {
			this.score = score;
		}
	}
	
	public Denotator getLayeredComposition() {
		return this.score;
	}
	
	public Denotator getComposition() {
		//return this.noteGenerator.convertMacroScoreToScore(this.score);
		return this.score;
	}
	
	public ObjectGenerator getObjectGenerator() {
		return this.objectGenerator;
	}
	
	/**
	 * Adds a new object with the given values to the top powerset of the score in case there is one
	 * @param values the values of the object to be added
	 * @param layerIndex the layer on which it is to be added
	 * @return the new path of the object
	 */
	public DenotatorPath addObject(DenotatorPath powersetPath, Map<DenotatorPath,Double> pathsWithValues) {
		if (powersetPath != null) {
			Denotator newObject = this.objectGenerator.createObject(powersetPath.getChildPath(0).getEndForm(), pathsWithValues);
			
			return this.addObject(newObject, powersetPath);
		}
		//no powerset specified to add it in, try to replace the whole score
		Denotator newObject = this.objectGenerator.createObject(this.score.getForm(), pathsWithValues);
		this.setScore(newObject);
		return new DenotatorPath(this.score.getForm());
	}
	
	private DenotatorPath addObject(Denotator object, DenotatorPath powersetPath) {
		object = this.internalAddObject(object, powersetPath);
		return this.findPath(object, powersetPath);
	}
	
	public List<DenotatorPath> addObjects(DenotatorPath powersetPath, List<TreeMap<DenotatorPath,Double>> pathsWithValues) {
		List<Denotator> newObjects = new ArrayList<Denotator>();
		Form objectForm;
		if (powersetPath != null) {
			objectForm = powersetPath.getChildPath(0).getEndForm();
		} else {
			objectForm = this.score.getForm();
		}
		for (TreeMap<DenotatorPath,Double> currentPathsWithValues : pathsWithValues) {
			newObjects.add(this.objectGenerator.createObject(objectForm, currentPathsWithValues));
		}
		return this.addObjectsToParent(newObjects, powersetPath);
	}
	
	/**
	 * Adds copies of the specified nodes to the macro scores of the nodes corresponding to the
	 * specified paths, i.e. they become their satellites and are automatically transformed to
	 * be relative to their anchor node.
	 * @param nodes the nodes to be added
	 * @param anchorPaths
	 * @return the new paths of the added nodes
	 * 
	 * TODO: why this method????? the one with the powersetIndices should be used!!!! 
	 */
	public List<DenotatorPath> addObjects(List<Denotator> notes, List<DenotatorPath> parentPaths) {
		notes = this.makeObjectsRelative(notes, parentPaths);
		List<DenotatorPath> childrenPaths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < notes.size(); i++) {
			DenotatorPath currentParentPath = parentPaths.get(i);
			DenotatorPath currentChildrenPath = null;
			if (currentParentPath != null) {
				currentChildrenPath = currentParentPath.getFirstPowersetPath(); 
			}
			notes.set(i, this.internalAddObject(notes.get(i), currentChildrenPath));
			childrenPaths.add(currentChildrenPath);
		}
		List<DenotatorPath> newPaths = this.findPaths(notes, childrenPaths);
		return newPaths;
	}
	
	public List<DenotatorPath> addObjects(List<Denotator> objects, List<DenotatorPath> parentPaths, int[] powersetIndices) {
		objects = this.makeObjectsRelative(objects, parentPaths);
		List<DenotatorPath> powersetPaths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < objects.size(); i++) {
			DenotatorPath currentParentPath = parentPaths.get(i);
			DenotatorPath currentPowersetPath = null;
			if (currentParentPath != null) {
				currentPowersetPath = currentParentPath.getPowersetPath(powersetIndices[i]);
			}
			objects.set(i, this.internalAddObject(objects.get(i), currentPowersetPath));
			powersetPaths.add(currentPowersetPath);
		}
		List<DenotatorPath> newPaths = this.findPaths(objects, powersetPaths);
		return newPaths;
	}
	
	public List<DenotatorPath> copyObjects(List<DenotatorPath> objectPaths) {
		List<DenotatorPath> parentPaths = DenotatorPath.getAnchorPaths(objectPaths);
		List<Denotator> newObjects = new ArrayList<Denotator>();
		for (DenotatorPath currentPath: objectPaths) {
			newObjects.add(this.extractObject(currentPath).copy());
		}
		List<DenotatorPath> newPaths = this.addObjects(newObjects, parentPaths);
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
	public List<DenotatorPath> moveObjectsToParent(List<DenotatorPath> objectPaths, DenotatorPath parentPath, int powersetIndex) {
		//System.out.println(objectPaths+ " "+parentPath + " " +powersetIndex);
		List<Denotator> parentObjects = this.extractObjects(parentPath);
		List<Denotator> newNotes = this.removeObjects(objectPaths);
		DenotatorPath newParentPath = this.findPath(parentObjects);
		return this.addObjectsToParent(newNotes, newParentPath, powersetIndex);
	}
	
	/**
	 * adds the given objects to the powersetIndex-th powerset of the object at parentPath
	 * @return the new paths of the added objects
	 */
	public List<DenotatorPath> addObjectsToParent(List<Denotator> objects, DenotatorPath parentPath, int powersetIndex) {
		if (parentPath != null) {
			return this.addObjectsToParent(objects, parentPath, parentPath.getPowersetPath(powersetIndex));
		}
		return this.setComposition(objects); 
	}
	
	public List<DenotatorPath> addObjectsToParent(List<Denotator> objects, DenotatorPath powersetPath) {
		if (powersetPath != null) {
			return this.addObjectsToParent(objects, powersetPath.getTopPath(), powersetPath);
		}
		return this.setComposition(objects);
	}
	
	public List<DenotatorPath> addObjectsToParent(List<Denotator> objects, DenotatorPath parentPath, DenotatorPath powersetPath) {
		//if (parentPath.size() > 0) {
			objects = this.makeObjectsRelative(objects, parentPath);
		//}
		for (int i = 0; i < objects.size(); i++) {
			objects.set(i, this.internalAddObject(objects.get(i), powersetPath));
		}
		return this.findPaths(objects, powersetPath);
	}
	
	//TODO: so bad, really gotta refactor it all
	private List<DenotatorPath> setComposition(List<Denotator> objects) {
		if (objects.size() == 1) {
			this.setScore(objects.get(0));
			List<DenotatorPath> topPath = new ArrayList<DenotatorPath>(); 
			topPath.add(new DenotatorPath(objects.get(0).getForm(), new int[]{}));
			return topPath;
		}
		return null;
	}
	
	public List<DenotatorPath> addObjects(List<List<Denotator>> objectLists) {
		for (List<Denotator> currentObjects: objectLists) {
			DenotatorPath newPath = null;
			DenotatorPath childrenPath = new DenotatorPath(this.score.getForm());
			//PowerDenotator currentMacroScore = this.getMacroScore(macroScorePath);
			for (int i = 0; i < currentObjects.size()-1; i++) {
				Denotator currentNote = currentObjects.get(i);
				newPath = this.findPath(currentNote, childrenPath);
				childrenPath = newPath.getFirstPowersetPath();
				//currentMacroScore = this.getMacroScore(macroScorePath);
			}
			this.addObject(currentObjects.get(currentObjects.size()-1), childrenPath);
		}
		return this.findPaths(objectLists);
	}
	
	/*
	 * returns the added element, which might have been converted to another type (node, note)
	 */
	private Denotator internalAddObject(Denotator object, DenotatorPath powersetPath) {
		try {
			FactorDenotator powerset = this.getPowersetOrList(powersetPath);
			object = this.objectGenerator.convertDenotatorIfNecessary(object, ((Denotator)powerset).getForm().getForms().get(0));
			powerset.appendFactor(object);
		} catch (RubatoException e) { e.printStackTrace(); }
		return object;
	}
	
	/*private LimitDenotator convertNote(LimitDenotator note, DenotatorPath powersetPath) {
		boolean isModulatorPath = powersetPath != null && powersetPath.isModulatorPath();
		if (isModulatorPath && note.getForm().getNameString().equals("SoundNode")) {
			return this.noteGenerator.convertNodeToModulator(note);
		} else if (!isModulatorPath && note.getForm().getNameString().equals("SoundNote")) {
			return this.noteGenerator.convertModulatorToNode(note);
		}
		return note;
	}*/
	
	public List<DenotatorPath> findPaths(List<List<Denotator>> noteLists) {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (List<Denotator> currentNoteList: noteLists) {
			paths.add(this.findPath(currentNoteList));
		}
		return paths;
	}
	
	/*
	 * finds the path along the given objects, which are all satellites of each other
	 */
	private DenotatorPath findPath(List<Denotator> objects) {
		DenotatorPath currentPath = new DenotatorPath(this.objectGenerator.getBaseForm());
		for (Denotator currentObject: objects) {
			currentPath = this.findObjectInNextPowersets(currentObject, currentPath);
		}
		return currentPath; 
	}
	
	private DenotatorPath findObjectInNextPowersets(Denotator object, DenotatorPath path) {
		int currentPowersetIndex = 0;
		while (true) {
			//TODO: could be optimized....
			DenotatorPath currentPowersetPath = path.getPowersetPath(currentPowersetIndex);
			FactorDenotator currentPowersetOrList = this.getPowersetOrList(currentPowersetPath);
			int objectIndex = this.getIndexOf(currentPowersetOrList, object);
			if (objectIndex >= 0) {
				return currentPowersetPath.getChildPath(objectIndex);
			}
			currentPowersetIndex++;
		}
	}
	
	private List<DenotatorPath> findPaths(List<Denotator> objects, List<DenotatorPath> powersetPaths) {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < objects.size(); i++) {
			paths.add(this.findPath(objects.get(i), powersetPaths.get(i)));
		}
		return paths;
	}
	
	private List<DenotatorPath> findPaths(List<Denotator> objects, DenotatorPath powersetPath) {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < objects.size(); i++) {
			paths.add(this.findPath(objects.get(i), powersetPath));
		}
		return paths;
	}
	
	/*
	 * Finds the path of an object if it is present in the given powerset
	 * TODO these should not really be used anymore, if possible
	 */
	private DenotatorPath findPath(Denotator object, DenotatorPath powersetPath) {
		//System.out.println(object+ " "+powersetPath);
		FactorDenotator powersetOrList = this.getPowersetOrList(powersetPath);
		int objectIndex = this.getIndexOf(powersetOrList, object);
		return powersetPath.getChildPath(objectIndex);
		//return new DenotatorPath(this.objectGenerator.getBaseForm(), new int[]{index,0});
	}
	
	private int getIndexOf(FactorDenotator powersetOrList, Denotator object) {
		if (powersetOrList instanceof PowerDenotator) {
			return ((PowerDenotator)powersetOrList).indexOf(object);
		}
		return ((ListDenotator)powersetOrList).indexOf(object);
	}
	
	/*private LimitDenotator convertNoteIfNecessary(LimitDenotator note, DenotatorPath powersetPath) {
		if (powersetPath != null && !powersetPath.isModulatorPath()
				&& !note.getForm().equals(this.noteGenerator.SOUND_NODE_FORM)) {
			note = this.noteGenerator.createNodeDenotator(note);
		}
		return note;
	}*/
	
	/*
	 * returns the PowerDenotator at powersetPath in this score. null if it is none
	 * TODO: well now it is kinda dumb, this method...
	 */
	private FactorDenotator getPowersetOrList(DenotatorPath powersetPath) {
		try {
			return (FactorDenotator)this.score.get(powersetPath.toIntArray());
		} catch (RubatoException e) { e.printStackTrace(); }
		return null;
	}
	
	public List<Denotator> getAbsoluteNodes(List<DenotatorPath> objectPaths) {
		return this.getAbsoluteObjects(objectPaths, false);
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
	public List<Denotator> removeObjects(List<DenotatorPath> objectPaths) {
		return this.getAbsoluteObjects(objectPaths, true);
	}
	
	public Denotator removeObject(DenotatorPath objectPath) {
		Denotator absoluteObject = this.getAbsoluteObject(objectPath);
		this.internalRemoveObject(objectPath);
		return absoluteObject;
	}
	
	/**
	 * returns the SoundNotes of modulators and the SoundNodes of general notes
	 * @param nodePaths
	 * @param remove true removes the notes from the score
	 * @return
	 */
	private List<Denotator> getAbsoluteObjects(List<DenotatorPath> notePaths, boolean remove) {
		List<Denotator> notesAndNodes = new ArrayList<Denotator>();
		notePaths = this.reverseSort(notePaths);
		Iterator<DenotatorPath> notePathsIterator = notePaths.iterator();
		if (notePathsIterator.hasNext()) {
			DenotatorPath currentNotePath = notePathsIterator.next();
			while (currentNotePath != null) {
				currentNotePath = this.addNextAbsoluteSiblingsTo(notesAndNodes, currentNotePath, notePathsIterator, remove);
			}
		}
		Collections.reverse(notesAndNodes);
		return notesAndNodes;
	}
	
	private DenotatorPath addNextAbsoluteSiblingsTo(List<Denotator> absoluteObjects, DenotatorPath currentObjectPath, Iterator<DenotatorPath> objectPathsIterator, boolean remove) {
		List<Denotator> currentSiblings = new ArrayList<Denotator>();
		
		DenotatorPath parentPath = currentObjectPath.getAnchorPath();
		Denotator currentNote = this.getObject(currentObjectPath, remove);
		currentSiblings.add(currentNote);
		
		while (objectPathsIterator.hasNext()) {
			currentObjectPath = objectPathsIterator.next();
			if (currentObjectPath.isSatelliteOf(parentPath)) {
				currentSiblings.add(this.getObject(currentObjectPath, remove));
			} else {
				absoluteObjects.addAll(this.makeObjectsAbsolute(currentSiblings, parentPath));
				return currentObjectPath;
			}
		}
		absoluteObjects.addAll(this.makeObjectsAbsolute(currentSiblings, parentPath));
		return null;
	}
	
	private List<Denotator> makeObjectsAbsolute(List<Denotator> noteList, DenotatorPath parentPath) {
		if (parentPath == null) {
			return noteList;
		}
		Denotator absoluteParent = this.getAbsoluteObject(parentPath);
		List<Denotator> absoluteObjects = new ArrayList<Denotator>();
		for (Denotator currentObject: noteList) {
			absoluteObjects.add(this.objectGenerator.makeObjectAbsolute(currentObject, absoluteParent));
		}
		return absoluteObjects;
	}
	
	private Denotator getObject(DenotatorPath notePath, boolean remove) {
		if (remove) {
			return this.internalRemoveObject(notePath);
		}
		return this.extractObject(notePath);
	}
	
	/**
	 * Removes a specific node from its containing soundscore or
	 * a note from its modulator powerset
	 * @param notePath the path to a note
	 */
	private Denotator internalRemoveObject(DenotatorPath objectPath) {
		try {
			DenotatorPath powersetPath = objectPath.getAnchorPowersetPath();
			if (powersetPath != null) {
				int objectIndex = objectPath.getObjectIndex();
				PowerDenotator powerset = (PowerDenotator)this.score.get(powersetPath.toIntArray());
				return powerset.removeFactor(objectIndex);
			}
		} catch (RubatoException e) {
			e.printStackTrace();
		}
		return null;
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
	public List<List<Denotator>> extractObjects(List<DenotatorPath> objectPaths) {
		//PerformanceCheck.startTask("..e1");
		List<List<Denotator>> anchorObjects = new ArrayList<List<Denotator>>();
		for (DenotatorPath currentNodePath: objectPaths) {
			anchorObjects.add(this.extractObjects(currentNodePath));
		}
		return anchorObjects;
	}
	
	/**
	 * extracts the series of anchor notes for the given note
	 * @param notePath
	 * @return
	 */
	public List<Denotator> extractObjects(DenotatorPath objectPath) {
		//PerformanceCheck.startTask("..e2");
		List<Denotator> parentObjects = new ArrayList<Denotator>();
		while (objectPath != null && objectPath.size() > 0) {
			parentObjects.add(0, this.extractObject(objectPath));
			objectPath = objectPath.getAnchorPath(); 
		}
		return parentObjects;
	}
	
	/*
	 * extracts the node of a satellite or the note of a modulator
	 */
	private Denotator extractObject(DenotatorPath objectPath) {
		//PerformanceCheck.startTask("..e3");
		try {
			int[] intPath = objectPath.toIntArray();
			if (intPath.length > 0) {
				return this.score.get(intPath);
			}
			return this.score;
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Denotator> makeObjectsRelative(List<Denotator> noteList, List<DenotatorPath> parentPaths) {
		List<Denotator> relativeNotes = new ArrayList<Denotator>();
		for (int i = 0; i < noteList.size(); i++) {
			Denotator currentNote = noteList.get(i);
			DenotatorPath currentParentPath = parentPaths.get(i);
			if (currentParentPath != null && currentParentPath.size() > 0) {
				Denotator absoluteParentNote = this.getAbsoluteObject(currentParentPath);
				relativeNotes.add(this.objectGenerator.makeObjectRelative(noteList.get(i), absoluteParentNote));
			} else {
				relativeNotes.add(currentNote);
			}
		}
		return relativeNotes;
	}
	
	private List<Denotator> makeObjectsRelative(List<Denotator> objectList, DenotatorPath parentPath) {
		Denotator absoluteParentObject = this.getAbsoluteObject(parentPath);
		List<Denotator> relativeObjects = new ArrayList<Denotator>();
		for (Denotator currentObject: objectList) {
			relativeObjects.add(this.objectGenerator.makeObjectRelative(currentObject, absoluteParentObject));
		}
		return relativeObjects;
	}
	
	public Denotator getAbsoluteObject(DenotatorPath notePath) {
		List<DenotatorPath> parentPaths = notePath.getAnchorPaths();
		if (parentPaths.size() <= 0) {
			return this.extractObject(notePath);
		}
		Collections.reverse(parentPaths);
		Denotator currentAbsoluteParent = this.extractObject(parentPaths.get(0));
		for (int i = 1; i < parentPaths.size(); i++) {
			currentAbsoluteParent = this.objectGenerator.makeObjectAbsolute(this.extractObject(parentPaths.get(i)), currentAbsoluteParent);
		}
		return this.objectGenerator.makeObjectAbsolute(this.extractObject(notePath), currentAbsoluteParent);
	}
	
	public Denotator getObject(DenotatorPath objectPath) {
		return this.getObject(objectPath, false);
	}
	
	public List<DenotatorPath> reverseSort(List<DenotatorPath> paths) {
		List<DenotatorPath> sortedPaths = this.sort(paths); 
		Collections.reverse(sortedPaths);
		return sortedPaths;
	}
	
	public List<DenotatorPath> sort(List<DenotatorPath> paths) {
		return new ArrayList<DenotatorPath>(new TreeSet<DenotatorPath>(paths));
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