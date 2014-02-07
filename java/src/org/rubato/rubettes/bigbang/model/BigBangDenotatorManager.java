package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.ListDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.ObjectGenerator;
import org.rubato.rubettes.util.PerformanceCheck;

/**
 * This class allows for the manipulation of a Denotator by adding and moving objects and making them absolute or
 * relative as necessary. It keeps track of any changing or new paths. Whenever the paths are requested using the
 * getPathResults method, tracking starts anew. This is useful for more complex operations that consist in several
 * steps. 
 *   
 * @author florian thalmann
 */
public class BigBangDenotatorManager {
	
	private ObjectGenerator objectGenerator;
	private Denotator composition;
	private OperationPathResults currentPathResults;
	
	private BigBangWallpaper currentWallpaper;
	private BigBangAlteration currentAlteration;
	
	public BigBangDenotatorManager() {
		this(Repository.systemRepository().getForm(BigBangRubette.STANDARD_FORM_NAME));
	}
	
	public BigBangDenotatorManager(Form form) {
		this.objectGenerator = new ObjectGenerator();
		this.setForm(form);
	}
	
	private BigBangDenotatorManager(Denotator composition) {
		this(composition.getForm());
		this.composition = composition;
	}
	
	public void setForm(Form baseForm) {
		this.objectGenerator.setBaseForm(baseForm);
		this.reset();
	}
	
	public Form getForm() {
		return this.objectGenerator.getBaseForm();
	}
	
	public boolean isFormCompatibleWithCurrentForm(Form form) {
		return this.objectGenerator.isFormCompatibleWithBaseForm(form);
	}
	
	public ObjectGenerator getObjectGenerator() {
		return this.objectGenerator;
	}
	
	public void reset() {
		this.setComposition(this.objectGenerator.createEmptyScore());
	}
	
	private void resetCurrentPaths() {
		this.currentPathResults = new OperationPathResults();
	}
	
	public OperationPathResults getPathResults() {
		OperationPathResults pathResults = this.currentPathResults;
		this.resetCurrentPaths();
		return pathResults;
	}
	
	public OperationPathResults setOrAddComposition(Denotator composition) {
		PerformanceCheck.startTask("convert");
		Denotator convertedComposition = this.objectGenerator.convertDenotatorIfCompatible(composition);
		if (convertedComposition != null) {
			if (convertedComposition.getForm().getType() == Form.POWER || convertedComposition.getForm().getType() == Form.LIST) {
				return this.addComposition(convertedComposition);
			}
			this.setComposition(convertedComposition);
			return this.getPathResults();
		}
		this.setComposition(composition);
		return this.getPathResults();
	}
	
	//add all elements of the given factordenotator to the present composition
	private OperationPathResults addComposition(Denotator composition) {
		Form baseForm = this.objectGenerator.getBaseForm();
		if (baseForm.getType() == Form.POWER || baseForm.getType() == Form.LIST) {
			DenotatorPath topPowersetPath = new DenotatorPath(baseForm, new int[]{});
			this.addObjectsToParent(((FactorDenotator)composition).getFactors(), topPowersetPath);
			//find new paths since added factors may have several levels
			ObjectPathFinder finder = new ObjectPathFinder();
			Set<DenotatorPath> finderNewPaths = finder.findPaths(composition, new DenotatorPath(composition.getForm()));
			List<DenotatorPath> newPathsList = new ArrayList<DenotatorPath>(this.currentPathResults.getNewPaths());
			//replace new paths with all found ones
			Set<DenotatorPath> newPaths = new TreeSet<DenotatorPath>();
			for (DenotatorPath currentNewPath : finderNewPaths) {
				int currentNewFirstIndex = newPathsList.get(currentNewPath.getFirstIndex()).getFirstIndex();
				newPaths.add(currentNewPath.setIndex(0, currentNewFirstIndex));
			}
			this.currentPathResults.setNewPaths(newPaths);
		}
		return this.getPathResults();
	}
	
	//replace the present composition with the given one
	public void setComposition(Denotator composition) {
		PerformanceCheck.startTask("set");
		if (this.composition != null && !composition.getForm().equals(this.objectGenerator.getBaseForm())) {
			this.setForm(composition.getForm());
		}
		if (composition != null) {
			this.composition = composition;
			this.resetCurrentPaths();
			this.currentWallpaper = null;
			ObjectPathFinder finder = new ObjectPathFinder();
			PerformanceCheck.startTask("find");
			Set<DenotatorPath> newPaths = finder.findPaths(this.composition, new DenotatorPath(composition.getForm()));
			this.currentPathResults.getNewPaths().addAll(newPaths);
			//if no objects were found, the top level denotator is one and has to be added
			if (this.currentPathResults.getNewPaths().size() == 0 && !finder.powersetOrListFound()) {
				this.currentPathResults.getNewPaths().add(new DenotatorPath(composition.getForm()));
			}
		}
	}
	
	public Denotator getComposition() {
		return this.composition;
	}
	
	public OperationPathResults addTransformation(Set<DenotatorPath> objectPaths, DenotatorPath anchorPath, BigBangTransformation transformation) {
		//if there is currently a wallpaper being made, add transformation to wallpaper
		if (this.currentWallpaper != null) {
			//add transformation
			this.currentWallpaper.addTransformationToLastDimension(transformation);
			//reset composition
			this.composition = this.currentWallpaper.getCompositionBeforeWallpaper().copy();
			//returns pathresults of last transformation of wallpaper
			return this.currentWallpaper.update();
			//List<DenotatorPath> newMotifPaths = this.addObjects(motifNodes);
		}
		//otherwise map directly
		return new BigBangMapper(this, transformation).mapCategorizedObjects(objectPaths);
		//System.out.println("TRANS " +this.currentNewPaths + " " + this.currentChangedPaths);
		//consists in pathresults of transformation or last transformation of wallpaper
	}
	
	public void addWallpaperDimension(Set<DenotatorPath> objectPaths, int rangeFrom, int rangeTo) {
		if (this.currentWallpaper == null) {
			this.currentWallpaper = new BigBangWallpaper(this, objectPaths);
		}
		this.currentWallpaper.addDimension(rangeFrom, rangeTo);
		//this.createWallpaper();
	}
	
	public void endWallpaper() {
		//this.createWallpaper();
		this.currentWallpaper = null;
	}
	
	public OperationPathResults addAlteration(Set<DenotatorPath> foregroundComposition, Set<DenotatorPath> backgroundComposition, List<DenotatorPath> alterationCoordinates, double startDegree, double endDegree) {
		this.currentAlteration = new BigBangAlteration();
		this.currentAlteration.setAlterationComposition(foregroundComposition, 0);
		this.currentAlteration.setAlterationComposition(backgroundComposition, 1);
		this.currentAlteration.setAlterationCoordinates(alterationCoordinates);
		this.currentAlteration.setStartDegree(startDegree);
		this.currentAlteration.setEndDegree(endDegree);
		this.currentAlteration.alter(this);
		return this.getPathResults();
	}
	
	public OperationPathResults shapeObjects(Set<DenotatorPath> objectPaths, TreeMap<Double,Double> shapingLocations, List<TransformationPaths> shapingPaths, boolean copyAndShape) {
		BigBangShaper shaper = new BigBangShaper(this, objectPaths, shapingLocations, shapingPaths, copyAndShape);
		return shaper.shapeCategorizedObjects();
	}
	
	public OperationPathResults addObjects(DenotatorPath powersetPath, List<Map<DenotatorPath,Double>> pathsWithValues) {
		Form objectForm;
		if (powersetPath != null) {
			objectForm = powersetPath.getChildPath(0).getEndForm();
		} else {
			objectForm = this.composition.getForm();
		}
		
		List<Denotator> newObjects = this.objectGenerator.createObjects(objectForm, pathsWithValues);
		this.addObjectsToParent(newObjects, powersetPath);
		return this.getPathResults();
	}
	
	public OperationPathResults flattenObjects(Set<DenotatorPath> objectPaths) {
		List<DenotatorPath> objectPathList = new ArrayList<DenotatorPath>(objectPaths);
		//List<DenotatorPath> oldParentPaths = DenotatorPath.getAnchorPaths(notePathsList);
		List<DenotatorPath> oldGrandParentPaths = DenotatorPath.getGrandAnchorPowersetPaths(objectPathList);
		int[] powersetIndices = DenotatorPath.getPowersetIndices(objectPathList);
		List<Denotator> movedObjects = this.removeObjects(objectPathList);
		movedObjects = this.addObjects(movedObjects, oldGrandParentPaths, powersetIndices);
		//remove the newPaths and removedPaths added by the used methods and replace them by changedPaths
		List<DenotatorPath> newPaths = this.findPaths(movedObjects, oldGrandParentPaths);
		this.currentPathResults.getNewPaths().removeAll(newPaths);
		this.currentPathResults.getRemovedPaths().removeAll(objectPaths);
		this.currentPathResults.updatePaths(objectPathList, newPaths, null);
		return this.getPathResults();
	}
	
	/**
	 * Adds the notes at the given paths to a children set of the given parent, either
	 * the modulator set or the satellite set
	 * @param notePaths
	 * @param parentPath
	 * @param asModulators if true then the notes are added as modulators
	 * @return
	 */
	public OperationPathResults buildSatelliteObjects(Set<DenotatorPath> objectPaths, DenotatorPath parentPath, int powersetIndex) {
		List<DenotatorPath> objectPathList = new ArrayList<DenotatorPath>(objectPaths);
		List<Denotator> parentObjects = this.extractObjects(parentPath);
		if (parentObjects != null && !parentObjects.contains(null)) {
			//remove objects and add at new spot
			List<Denotator> movedObjects = this.removeObjects(objectPathList);
			DenotatorPath newPowersetPath = this.findPath(parentObjects).getPowersetPath(powersetIndex);
			movedObjects = this.addObjectsToParent(movedObjects, newPowersetPath);
			//remove the newPaths and removedPaths added by the used methods and replace them by changedPaths
			List<DenotatorPath> newPaths = this.findPaths(movedObjects, newPowersetPath);
			this.currentPathResults.getNewPaths().removeAll(newPaths);
			this.currentPathResults.getRemovedPaths().removeAll(objectPathList);
			this.currentPathResults.updatePaths(objectPathList, newPaths, null);
		}
		return this.getPathResults();
	}
	
	/**
	 * Adds the given objects to the powersets at the given powersetIndices found in the given parentObjects.
	 * @return the added objects (made relative)
	 */
	public List<Denotator> addObjects(List<Denotator> objects, List<DenotatorPath> parentPaths, int[] powersetIndices) {
		List<Denotator> addedObjects = new ArrayList<Denotator>();
		DenotatorPath previousPowersetPath = null;
		List<Denotator> currentSiblings = new ArrayList<Denotator>();
		for (int i = 0; i < parentPaths.size(); i++) {
			DenotatorPath currentObjectPowersetPath = parentPaths.get(i).getPowersetPath(powersetIndices[i]);
			if (previousPowersetPath == null || !previousPowersetPath.equals(currentObjectPowersetPath)) {
				if (currentSiblings.size() > 0) {
					addedObjects.addAll(this.addObjectsToParent(currentSiblings, previousPowersetPath));
					currentSiblings = new ArrayList<Denotator>();
				}
				previousPowersetPath = currentObjectPowersetPath;
			}
			currentSiblings.add(objects.get(i));
		}
		addedObjects.addAll(this.addObjectsToParent(currentSiblings, previousPowersetPath));
		return addedObjects;
	}
	
	/**
	 * Adds the given objects to the powerset at the given powersetPath while making them relative.
	 */
	public List<Denotator> addObjectsToParent(List<Denotator> newObjects, DenotatorPath powersetPath) {
		if (powersetPath != null) {
			List<Denotator> oldObjects = this.getPowersetOrList(powersetPath).getFactors();
			List<DenotatorPath> oldObjectPaths = this.findPaths(oldObjects, powersetPath);
			newObjects = this.internalAddObjectsAsRelative(newObjects, powersetPath);
			if (newObjects != null) {
				//System.out.print("......."+this.findPaths(newObjects, powersetPath) + "...");
				//try {this.composition.get(powersetPath.toIntArray()).display();} catch(RubatoException e) {};
				List<DenotatorPath> newPaths = this.findPaths(newObjects, powersetPath);
				this.currentPathResults.updatePaths(oldObjectPaths, this.findPaths(oldObjects, powersetPath), newPaths);
			}
			return newObjects;
		}
		this.setComposition(newObjects.get(0));
		return null;
	}
	
	public void replaceSiblingObjects(List<Denotator> newObjects, List<DenotatorPath> replacedObjectsPaths) {
		DenotatorPath powersetPath = replacedObjectsPaths.get(0).getParentPath();
		if (powersetPath != null) {
			//find all objects that won't be replaced
			List<Denotator> nonReplacedObjects = this.getPowersetOrList(powersetPath).getFactors();
			List<Integer> replacedObjectsIndices = new ArrayList<Integer>();
			for (DenotatorPath currentPath : replacedObjectsPaths) {
				replacedObjectsIndices.add(currentPath.getLastIndex());
			}
			Collections.sort(replacedObjectsIndices);
			Collections.reverse(replacedObjectsIndices);
			for (int currentIndex : replacedObjectsIndices) {
				nonReplacedObjects.remove(currentIndex);
			}
			//see where the ones that are not replaced are before replacing the other ones
			List<DenotatorPath> nonReplacedObjectsPaths = this.findPaths(nonReplacedObjects, powersetPath);
			//remove the replaced objects
			this.internalRemoveObjects(replacedObjectsPaths);
			//replace them by the new ones
			newObjects = this.internalAddObjectsAsRelative(newObjects, powersetPath);
			if (newObjects != null) {
				//see where all objects ended up after replacing
				List<DenotatorPath> oldPaths = new ArrayList<DenotatorPath>(replacedObjectsPaths);
				oldPaths.addAll(nonReplacedObjectsPaths);
				List<DenotatorPath> newPaths = new ArrayList<DenotatorPath>(this.findPaths(newObjects, powersetPath));
				newPaths.addAll(this.findPaths(nonReplacedObjects, powersetPath));
				this.currentPathResults.updatePaths(oldPaths, newPaths, null);
			}
		} else {
			this.setComposition(newObjects.get(0));
		}
	}
	
	public List<Denotator> removeObjects(List<DenotatorPath> removedObjectsPaths) {
		List<Denotator> removedObjects = new ArrayList<Denotator>(); 
		Collections.sort(removedObjectsPaths);
		Collections.reverse(removedObjectsPaths);
		DenotatorPath previousPowersetPath = null;
		List<DenotatorPath> currentSiblings = new ArrayList<DenotatorPath>();
		for (DenotatorPath currentObjectPath : removedObjectsPaths) {
			DenotatorPath currentObjectPowersetPath = currentObjectPath.getParentPath();
			if (previousPowersetPath == null || !previousPowersetPath.equals(currentObjectPowersetPath)) {
				if (currentSiblings.size() > 0) {
					removedObjects.addAll(this.removeSiblingObjects(currentSiblings));
					currentSiblings = new ArrayList<DenotatorPath>();
				}
				previousPowersetPath = currentObjectPowersetPath;
			}
			currentSiblings.add(currentObjectPath);
		}
		if (currentSiblings.size() > 0) {
			removedObjects.addAll(this.removeSiblingObjects(currentSiblings));
		}
		this.currentPathResults.getRemovedPaths().addAll(removedObjectsPaths);
		return removedObjects;
	}
	
	private List<Denotator> removeSiblingObjects(List<DenotatorPath> removedObjectsPaths) {
		DenotatorPath powersetPath = removedObjectsPaths.get(0).getParentPath();
		
		if (powersetPath != null) {
			//find all objects that won't be removed
			List<Denotator> nonRemovedObjects = this.getPowersetOrList(powersetPath).getFactors();
			List<Integer> removedObjectsIndices = new ArrayList<Integer>();
			for (DenotatorPath currentPath : removedObjectsPaths) {
				removedObjectsIndices.add(currentPath.getLastIndex());
			}
			Collections.sort(removedObjectsIndices);
			Collections.reverse(removedObjectsIndices);
			for (int currentIndex : removedObjectsIndices) {
				nonRemovedObjects.remove(currentIndex);
			}
			//see where the ones that are not removed are before removing
			List<DenotatorPath> nonRemovedObjectsPaths = this.findPaths(nonRemovedObjects, powersetPath);
			//remove the removed objects
			List<Denotator> removedObjects = this.internalRemoveObjects(removedObjectsPaths);
			
			//see where all objects ended up after removing
			this.currentPathResults.getRemovedPaths().addAll(removedObjectsPaths);
			this.currentPathResults.updatePaths(nonRemovedObjectsPaths, this.findPaths(nonRemovedObjects, powersetPath), null);
			
			return removedObjects;
		}
		return null;
	}
	
	public List<Denotator> getAbsoluteObjects(List<DenotatorPath> objectPaths) {
		return this.internalGetAbsoluteObjects(objectPaths, false);
	}
	
	/*
	 * removes the SoundNotes of modulators and the SoundNodes of general notes
	 * @param nodePaths the paths of the notes to be removed
	 * @return the removed SoundNotes and SoundNodes (in the order of the note paths)
	 */
	private List<Denotator> internalRemoveObjects(List<DenotatorPath> objectPaths) {
		return this.internalGetAbsoluteObjects(objectPaths, true);
	}
	
	/*
	 * returns the SoundNotes of modulators and the SoundNodes of general notes
	 * @param nodePaths
	 * @param remove true removes the notes from the score
	 * @return
	 */
	private List<Denotator> internalGetAbsoluteObjects(List<DenotatorPath> objectPaths, boolean remove) {
		List<Denotator> absoluteObjects = new ArrayList<Denotator>();
		
		objectPaths = this.sortAndReverse(objectPaths);
		Iterator<DenotatorPath> objectPathsIterator = objectPaths.iterator();
		DenotatorPath previousAnchorPath = null;
		Denotator previousAbsoluteAnchor = null;
		while (objectPathsIterator.hasNext()) {
			DenotatorPath currentPath = objectPathsIterator.next();
			Denotator currentObject;
			if (remove) {
				currentObject = this.internalRemoveObject(currentPath);
			} else {
				currentObject = this.extractObject(currentPath);
			}
			if (currentObject != null) {
				DenotatorPath currentAnchorPath = currentPath.getAnchorPath();
				if (previousAnchorPath != null && currentAnchorPath.equals(previousAnchorPath)) {
					absoluteObjects.add(this.objectGenerator.makeObjectAbsolute(currentObject, previousAbsoluteAnchor));
				} else {
					previousAnchorPath = currentAnchorPath;
					previousAbsoluteAnchor = this.getAbsoluteObject(currentAnchorPath);
					absoluteObjects.add(this.objectGenerator.makeObjectAbsolute(currentObject, previousAbsoluteAnchor));
				}
			}
		}
		Collections.reverse(absoluteObjects);
		return absoluteObjects;
	}
	
	public Denotator getAbsoluteObject(DenotatorPath objectPath) {
		List<DenotatorPath> parentPaths = objectPath.getAnchorPaths();
		if (parentPaths.isEmpty()) {
			return this.extractObject(objectPath);
		}
		Collections.reverse(parentPaths);
		Denotator currentAbsoluteParent = this.extractObject(parentPaths.get(0));
		for (int i = 1; i < parentPaths.size(); i++) {
			currentAbsoluteParent = this.objectGenerator.makeObjectAbsolute(this.extractObject(parentPaths.get(i)), currentAbsoluteParent);
		}
		Denotator extractedObject = this.extractObject(objectPath);
		if (extractedObject != null) {
			return this.objectGenerator.makeObjectAbsolute(extractedObject, currentAbsoluteParent);
		}
		return null;
	}
	
	private List<Denotator> internalAddObjectsAsRelative(List<Denotator> objects, DenotatorPath powersetPath) {
		objects = this.makeObjectsRelative(objects, powersetPath.getTopPath());
		if (objects != null) {
			List<Denotator> addedObjects = new ArrayList<Denotator>();
			for (int i = 0; i < objects.size(); i++) {
				Denotator addedObject = this.internalAddObject(objects.get(i), powersetPath);
				if (addedObject != null) {
					addedObjects.add(addedObject);
				}
			}
			return addedObjects;
		}
		return null;
	}
	
	private List<Denotator> makeObjectsRelative(List<Denotator> objectList, DenotatorPath parentPath) {
		Denotator absoluteParentObject = this.getAbsoluteObject(parentPath);
		if (absoluteParentObject != null) {
			List<Denotator> relativeObjects = new ArrayList<Denotator>();
			for (Denotator currentObject: objectList) {
				relativeObjects.add(this.objectGenerator.makeObjectRelative(currentObject, absoluteParentObject));
			}
			return relativeObjects;
		}
		return null;
	}
	
	/*
	 * returns the added element, which is a new denotator that might even be converted to another type. returns null
	 * if an equal denotator already existed and was merely replaced by the object
	 */
	private Denotator internalAddObject(Denotator object, DenotatorPath powersetPath) {
		FactorDenotator powerset = this.getPowersetOrList(powersetPath);
		int previousFactorCount = powerset.getFactorCount();
		object = this.objectGenerator.convertDenotatorIfNecessary(object, ((Denotator)powerset).getForm().getForms().get(0));
		try {
			powerset.appendFactor(object);
		} catch (RubatoException e) { e.printStackTrace(); }
		if (powerset.getFactorCount() > previousFactorCount) {
			return object;
		}
		//if the object merely replaced an precisely equal one, it has not really been added. return null
		return null;
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
	
	/*
	 * returns the PowerDenotator at powersetPath in this score. null if it is none
	 * TODO: well now it is kinda dumb, this method...
	 */
	private FactorDenotator getPowersetOrList(DenotatorPath powersetPath) {
		//System.out.println(powersetPath);
		try {
			return (FactorDenotator)this.composition.get(powersetPath.toIntArray());
		} catch (RubatoException e) { e.printStackTrace(); }
		return null;
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
				FactorDenotator powersetOrList = (FactorDenotator)this.composition.get(powersetPath.toIntArray());
				if (objectIndex < powersetOrList.getFactorCount()) {
					if (powersetOrList instanceof PowerDenotator) {
						return ((PowerDenotator)powersetOrList).removeFactor(objectIndex);
					} else if (powersetOrList instanceof ListDenotator) {
						return ((ListDenotator)powersetOrList).removeFactor(objectIndex);
					}
				}
			}
		} catch (RubatoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * extracts the series of anchor notes for the given note
	 * @param notePath
	 * @return
	 */
	private List<Denotator> extractObjects(DenotatorPath objectPath) {
		//PerformanceCheck.startTask("..e2");
		List<Denotator> parentObjects = new ArrayList<Denotator>();
		while (objectPath != null && objectPath.size() > 0) {
			Denotator currentObject = this.extractObject(objectPath);
			if (currentObject != null) {
				parentObjects.add(0, currentObject);
				objectPath = objectPath.getAnchorPath();
			} else {
				return null;
			}
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
				return this.composition.get(intPath);
			}
			return this.composition;
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<DenotatorPath> sortAndReverse(List<DenotatorPath> paths) {
		List<DenotatorPath> sortedPaths = new ArrayList<DenotatorPath>(new TreeSet<DenotatorPath>(paths));
		Collections.reverse(sortedPaths);
		return sortedPaths;
	}
	
	/**
	 * does not clone current wallpaper or alteration 
	 */
	public BigBangDenotatorManager clone() {
		return new BigBangDenotatorManager(this.composition);
	}

}