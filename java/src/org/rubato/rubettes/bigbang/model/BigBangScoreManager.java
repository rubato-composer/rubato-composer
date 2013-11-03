package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangScoreManager extends Model {
	
	private BigBangScore score;
	private BigBangWallpaper wallpaper;
	private BigBangAlteration alteration;
	
	public BigBangScoreManager(BigBangController controller) {
		controller.addModel(this);
		Form standardForm = Repository.systemRepository().getForm(BigBangRubette.STANDARD_FORM_NAME);
		this.score = new BigBangScore(standardForm);
		this.setForm(standardForm);
	}
	
	public void newWindowAdded(SelectedObjectsPaths paths) {
		this.fireCompositionChange(paths);
		//this.alteration.fireState();
	}
	
	public void setForm(Form form) {
		this.score.setForm(form);
		this.fireCompositionChange();
	}
	
	public void resetScore() {
		this.score.resetScore();
		this.wallpaper = null;
	}
	
	public boolean setInitialComposition(Denotator newComposition) {
		boolean valid = this.score.setInitialComposition(newComposition);
		if (valid) {
			this.fireCompositionChange();
		}
		return valid;
	}
	
	public Denotator getComposition() {
		return this.score.getComposition();
	}
	
	public SelectedObjectsPaths addTransformation(SelectedObjectsPaths objectPaths, BigBangTransformation transformation, boolean inPreviewMode, boolean fireCompositionChange) {
		if (this.wallpaper != null) {
			return this.addWallpaperTransformation(transformation, inPreviewMode, fireCompositionChange);
		}
		return this.mapObjects(objectPaths, transformation, inPreviewMode, fireCompositionChange);
	}
	
	private SelectedObjectsPaths mapObjects(SelectedObjectsPaths objectPaths, BigBangTransformation transformation, boolean inPreviewMode, boolean fireCompositionChange) {
		//PerformanceCheck.startTask("prepare");
		//this.updateActualScore(inPreviewMode);
		//PerformanceCheck.startTask("map");
		BigBangMapper mapper = new BigBangMapper(this.score, transformation);
		SelectedObjectsPaths newPaths = mapper.mapCategorizedObjects(objectPaths);
		//PerformanceCheck.startTask("fire");
		if (fireCompositionChange) {
			if (inPreviewMode) {
				this.firePreviewCompositionChange(objectPaths);
			} else {
				this.fireCompositionChange(newPaths);
			}
		}
		//PerformanceCheck.startTask("draw");
		return newPaths;
	}
	
	public void startWallpaper(SelectedObjectsPaths objectPaths) {
		this.wallpaper = new BigBangWallpaper(objectPaths);
	}
	
	public void addWallpaperDimensionS(SelectedObjectsPaths objectPaths, int rangeFrom, int rangeTo, boolean fireCompositionChange) {
		if (this.wallpaper == null) {
			this.startWallpaper(objectPaths);
		}
		this.wallpaper.addDimension(rangeFrom, rangeTo);
		//this.firePropertyChange(BigBangController.WALLPAPER, null, this.wallpaper);
		this.createWallpaper(false, fireCompositionChange);
	}
	
	private SelectedObjectsPaths addWallpaperTransformation(BigBangTransformation transformation, boolean inPreviewMode, boolean fireCompositionChange) {
		this.wallpaper.addTransformationToLastDimension(transformation);
		if (fireCompositionChange) {
			List<DenotatorPath> newPaths = this.createWallpaper(inPreviewMode, fireCompositionChange);
			if (inPreviewMode) {
				this.wallpaper.removeLastTransformation();
			}
			return new SelectedObjectsPaths(newPaths, transformation.getAnchorNodePath());
		}
		//this.firePropertyChange(BigBangController.WALLPAPER, null, this.wallpaper);
		//TODO STUPID just test
		return this.wallpaper.getMotif();
	}
	
	private List<DenotatorPath> createWallpaper(boolean inPreviewMode, boolean fireCompositionChange) {
		SelectedObjectsPaths motifPaths = this.wallpaper.getMotif();
		List<List<Denotator>> motifNodes = this.score.extractObjects(motifPaths.get(0));
		//CREATE ACTUAL WALLPAPER (to be selected when wallpaper finished)
		this.wallpaper.applyTo(this.score);
		DenotatorPath lastAnchorPath = this.wallpaper.getLastAnchorPath();
		List<DenotatorPath> newMotifPaths = this.score.addObjects(motifNodes);
		SelectedObjectsPaths newPaths = new SelectedObjectsPaths(newMotifPaths, lastAnchorPath);
		if (fireCompositionChange) {
			if (inPreviewMode) {
				/*if (selectMotif) {
					this.firePreviewCompositionChange(newPaths, true);
				} else {
				 */this.firePreviewCompositionChange(newPaths);
				 //}
			} else {
				//select the original motif. TODO IS THIS RIGHT?
				this.fireCompositionChange(newPaths);
			}
		}
		return newMotifPaths;
	}
	
	public void setWallpaperRange(Integer dimension, Boolean rangeTo, Integer value) {
		this.wallpaper.setRange(dimension, rangeTo, value);
		this.createWallpaper(false, true);
		//this.firePropertyChange(BigBangController.WALLPAPER, null, this.wallpaper);
	}
	
	public void endWallpaper(boolean fireCompositionChange) {
		this.createWallpaper(false, fireCompositionChange);
		this.wallpaper = null;
		this.firePropertyChange(BigBangController.END_WALLPAPER, null, null);
	}
	
	public void addAlteration(List<DenotatorPath> foregroundComposition, List<DenotatorPath> backgroundComposition, List<DenotatorPath> alterationCoordinates, double startDegree, double endDegree, boolean sendCompositionChange) {
		this.alteration = new BigBangAlteration();
		this.alteration.setAlterationComposition(new TreeSet<DenotatorPath>(foregroundComposition), 0);
		this.alteration.setAlterationComposition(new TreeSet<DenotatorPath>(backgroundComposition), 1);
		this.alteration.setAlterationCoordinates(alterationCoordinates);
		this.alteration.setStartDegree(startDegree);
		this.alteration.setEndDegree(endDegree);
		this.alter(false, sendCompositionChange);
	}
	
	public void fireAlterationComposition(Integer index, List<DenotatorPath> paths) {
		//this.alteration.resetDegrees();
		this.fireCompositionChange(new SelectedObjectsPaths(paths, null));
		this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, index);
	}
	
	private void alter(boolean inPreviewMode, boolean sendCompositionChange) {
		//List<DenotatorPath> composition0Paths = new ArrayList<DenotatorPath>(this.alteration.getComposition(0));
		//List<List<LimitDenotator>> composition0Nodes = this.actualScore.extractNodes(composition0Paths);
		this.alteration.alter(this.score);
		//List<DenotatorPath> newComposition0Paths = this.actualScore.findPaths(composition0Nodes);
		if (sendCompositionChange) {
			if (inPreviewMode) {
				this.firePreviewCompositionChange(new SelectedObjectsPaths(new TreeSet<DenotatorPath>(), null));
			} else {
				this.fireCompositionChange(new SelectedObjectsPaths(new TreeSet<DenotatorPath>(), null));
			}
		}
	}
	
	public List<Map<DenotatorPath,Double>> shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		BigBangShaper shaper = new BigBangShaper(this.score, properties, shapingLocations);
		List<Map<DenotatorPath,Double>> newPathsAndOldYValues = shaper.shapeCategorizedObjects();
		
		if (properties.inPreviewMode()) {
			this.firePreviewCompositionChange(properties.getObjectsPaths());
		} else {
			List<List<DenotatorPath>> newPaths = new ArrayList<List<DenotatorPath>>();
			for (Map<DenotatorPath,Double> currentMap : newPathsAndOldYValues) {
				newPaths.add(new ArrayList<DenotatorPath>(currentMap.keySet()));
			}
			this.fireCompositionChange(new SelectedObjectsPaths(newPaths, null));
		}
		return newPathsAndOldYValues;
	}
	
	public void undoShapeObjects(Map<DenotatorPath,Double> newPathsAndOldYValues) {
		
	}
	
	public List<DenotatorPath> addObjects(DenotatorPath powersetPath, List<Map<DenotatorPath,Double>> pathsWithValues, boolean fireCompositionChange) {
		List<DenotatorPath> newPaths = this.score.addObjects(powersetPath, pathsWithValues);
		/*for (TreeMap<DenotatorPath,Double> currentPathsWithValues : pathsWithValues) {
			//TODO: paths probably not right, use addObjects! refactor entire thing upon refactoring wallpaper etc
			//newPaths.add(this.score.addObject(powersetPath, currentPathsWithValues));
		}*/
		
		if (newPaths != null && fireCompositionChange) {
			//select the objects just added
			this.fireCompositionChange(new SelectedObjectsPaths(newPaths, null));
		}
		return newPaths;
	}
	
	//TODO: remove, only used in tests...
	public List<DenotatorPath> addObjects(List<Denotator> objects, List<DenotatorPath> parentPaths, boolean fireCompositionChange) {
		List<DenotatorPath> newPaths = this.score.addObjects(objects, parentPaths);
		if (fireCompositionChange) {
			this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
		}
		return newPaths;
	}
	
	/**
	 * 
	 * @param nodePaths the list of node paths has to be sorted from small to big
	 */
	public List<Denotator> removeObjects(Set<DenotatorPath> objectPaths, boolean fireCompositionChange) {
		List<Denotator> removedObjects = this.score.removeObjects(new ArrayList<DenotatorPath>(objectPaths));
		if (fireCompositionChange) {
			this.fireCompositionChange();
		}
		return removedObjects;
	}
	
	public Denotator removeObject(DenotatorPath objectPath) {
		Denotator removedObject = this.score.removeObject(objectPath);
		this.fireCompositionChange();
		return removedObject;
	}
	
	public List<DenotatorPath> copyObjects(Set<DenotatorPath> objectPaths) {
		List<DenotatorPath> newPaths = this.score.copyObjects(new ArrayList<DenotatorPath>(objectPaths));
		this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
		return newPaths;
	}
	
	/*public Map<DenotatorPath,Integer> moveNotesToLayer(Set<DenotatorPath> notePaths, int layerIndex) {
		Map<DenotatorPath,Integer> newPathsAndOldLayer = this.score.moveNotesToLayer(new ArrayList<DenotatorPath>(notePaths), layerIndex);
		this.fireCompositionChange(newPathsAndOldLayer.keySet());
		return newPathsAndOldLayer;
	}
	
	public Set<DenotatorPath> moveNotesToLayers(Map<DenotatorPath,Integer> newPathsAndOldLayer) {
		List<DenotatorPath> newPaths = this.score.moveNotesToLayers(newPathsAndOldLayer);
		this.fireCompositionChange(newPathsAndOldLayer.keySet());
		return new TreeSet<DenotatorPath>(newPaths);
	}*/
	
	public List<DenotatorPath> moveObjectsToParent(List<DenotatorPath> objectPaths, DenotatorPath parentPath, int powersetIndex, boolean fireCompositionChange) {
		List<DenotatorPath> newPaths = this.score.moveObjectsToParent(objectPaths, parentPath, powersetIndex);
		if (fireCompositionChange) {
			this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
		}
		return newPaths;
	}
	
	/**
	 * All satellites have to have the same anchor node.
	 * @param satellitePaths
	 * @param goalPaths
	 * @param anchorNode
	 */
	public List<DenotatorPath> undoMoveToParent(List<DenotatorPath> actualPaths, List<DenotatorPath> oldPaths) {
		List<Denotator> newObjects = this.score.removeObjects(actualPaths);
		List<DenotatorPath> newPaths = this.score.addObjects(newObjects, DenotatorPath.getAnchorPaths(oldPaths));
		this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
		return newPaths;
	}
	
	/**
	 * Flattens the given notes so that they end up in the next higher hierarchical level,
	 * while maintaining their function as satellites or modulators.
	 * @param notePaths
	 * @return
	 */
	public TreeMap<DenotatorPath,DenotatorPath> flattenNotes(Set<DenotatorPath> notePaths) {
		List<DenotatorPath> notePathsList = new ArrayList<DenotatorPath>(notePaths);
		List<DenotatorPath> oldParentPaths = DenotatorPath.getAnchorPaths(notePathsList);
		List<DenotatorPath> oldGrandParentPaths = DenotatorPath.getGrandAnchorPowersetPaths(notePathsList);
		int[] powersetIndices = DenotatorPath.getPowersetIndices(notePathsList);
		List<Denotator> notes = this.score.removeObjects(notePathsList);
		List<DenotatorPath> newPaths = this.score.addObjects(notes, oldGrandParentPaths, powersetIndices);
		TreeMap<DenotatorPath,DenotatorPath> newPathsAndOldParentPaths = this.getPathMap(newPaths, oldParentPaths);
		this.fireCompositionChange(newPathsAndOldParentPaths.keySet());
		return newPathsAndOldParentPaths;
	}
	
	private TreeMap<DenotatorPath,DenotatorPath> getPathMap(List<DenotatorPath> keys, List<DenotatorPath> values) {
		TreeMap<DenotatorPath,DenotatorPath> newPathsAndOldMSPaths = new TreeMap<DenotatorPath,DenotatorPath>();
		for (int i = 0; i < keys.size(); i++) {
			newPathsAndOldMSPaths.put(keys.get(i), values.get(i));
		}
		return newPathsAndOldMSPaths;
	}
	
	public Set<DenotatorPath> unflattenNotes(Map<DenotatorPath,DenotatorPath> newAndOldPaths) {
		List<DenotatorPath> keys = new ArrayList<DenotatorPath>(newAndOldPaths.keySet());
		List<Denotator> objects = this.score.removeObjects(keys);
		Set<DenotatorPath> newPaths = new TreeSet<DenotatorPath>(this.score.addObjects(objects, new ArrayList<DenotatorPath>(newAndOldPaths.values())));
		this.fireCompositionChange(newPaths);
		return newPaths;
	}
	
	public void fireCompositionChange() {
		this.fireCompositionChange(new TreeSet<DenotatorPath>());
	}
	
	private void fireCompositionChange(Set<DenotatorPath> selectedNodesPaths) {
		this.fireCompositionChange(new SelectedObjectsPaths(selectedNodesPaths, null));
	}
	
	private void firePreviewCompositionChange(SelectedObjectsPaths paths) {
		this.fireCompositionChange(paths, true);
	}
	
	private void fireCompositionChange(SelectedObjectsPaths paths) {
		this.fireCompositionChange(paths, false);
	}
	
	private void fireCompositionChange(SelectedObjectsPaths paths, boolean preview) {
		Denotator changedComposition = this.score.getComposition();
		ScoreChangedNotification notification = new ScoreChangedNotification(changedComposition, paths, preview);
		this.firePropertyChange(BigBangController.COMPOSITION, null, notification);
	}

}
