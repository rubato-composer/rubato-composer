package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.model.SelectedPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangScoreManager extends Model {
	
	private BigBangScore score, actualScore;
	private BigBangWallpaper wallpaper;
	private BigBangAlteration alteration;
	
	public BigBangScoreManager(BigBangController controller) {
		controller.addModel(this);
		this.score = new BigBangScore(BigBangRubette.STANDARD_FORM);
		this.setForm(BigBangRubette.STANDARD_FORM);
		this.alteration = new BigBangAlteration(controller);
	}
	
	public void newWindowAdded(SelectedPaths paths) {
		this.fireCompositionChange(this.score, paths.getNodePaths(), paths.getAnchorPath(), false);
		this.alteration.fireState();
	}
	
	public void setForm(Form form) {
		this.score.setForm(form);
		this.fireCompositionChange(this.score, null, null, false);
	}
	
	public boolean setComposition(Denotator newComposition) {
		boolean valid = this.score.setComposition(newComposition);
		if (valid) {
			this.fireCompositionChange();
			this.resetFactualScore();
		}
		return valid;
	}
	
	public Denotator getComposition() {
		if (this.actualScore != null) {
			return this.actualScore.getComposition();
		}
		return this.score.getComposition();
	}
	
	public void startWallpaper(ArrayList<DenotatorPath> nodePaths) {
		this.wallpaper = new BigBangWallpaper(nodePaths);
	}
	
	public void addWallpaperDimensionS(int rangeFrom, int rangeTo) {
		this.wallpaper.addDimension(rangeFrom, rangeTo);
	}
	
	public void removeLastWallpaperDimension() {
		this.wallpaper.removeLastDimension();
	}
	
	public List<DenotatorPath> addWallpaperTransformation(BigBangTransformation transformation, boolean inPreviewMode) {
		this.wallpaper.addTransformationToLastDimension(transformation);
		List<DenotatorPath> newPaths = this.createWallpaper(true, !inPreviewMode);
		if (inPreviewMode) {
			this.wallpaper.removeLastTransformation();
		}
		return newPaths;
	}
	
	public void removeLastWallpaperTransformation() {
		this.wallpaper.removeLastTransformation();
	}
	
	private List<DenotatorPath> createWallpaper(boolean inPreviewMode, boolean selectMotif) {
		List<DenotatorPath> motifPaths = this.wallpaper.getMotif();
		this.updateActualScore(inPreviewMode);
		List<List<Denotator>> motifNodes = this.actualScore.extractObjects(motifPaths);
		//CREATE ACTUAL WALLPAPER (to be selected when wallpaper finished)
		this.wallpaper.applyTo(this.actualScore);
		DenotatorPath lastAnchorPath = this.wallpaper.getLastAnchorPath();
		List<DenotatorPath> newMotifPaths = this.actualScore.addObjects(motifNodes);
		if (inPreviewMode) {
			if (selectMotif) {
				this.fireCompositionChange(this.actualScore, new TreeSet<DenotatorPath>(newMotifPaths), lastAnchorPath, true);
			} else {
				this.firePreviewCompositionChange(new TreeSet<DenotatorPath>(newMotifPaths), lastAnchorPath);
			}
		} else {
			this.fireCompositionChange(this.actualScore, new TreeSet<DenotatorPath>(newMotifPaths), lastAnchorPath, true);
		}
		return newMotifPaths;
	}
	
	public void updateWallpaper(ArrayList<Integer> ranges) {
		this.wallpaper.updateRanges(ranges);
		this.createWallpaper(true, true);
	}
	
	public void endWallpaper() {
		this.createWallpaper(false, false);
	}
	
	public void fireAlterationComposition(Integer index) {
		this.alteration.resetDegrees();
		Set<DenotatorPath> selectedNodesPaths = this.alteration.getComposition(index);
		this.fireCompositionChange(this.score, selectedNodesPaths, null, true);
		this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, index);
	}
	
	public void setAlterationStartDegree(Double value) {
		this.alteration.setStartDegree(value);
		this.alter(true);
	}
	
	public void setAlterationEndDegree(Double value) {
		this.alteration.setEndDegree(value);
		this.alter(true);
	}
	
	public void endAlteration() {
		this.alter(false);
		this.alteration.reset();
	}
	
	private void alter(boolean inPreviewMode) {
		this.updateActualScore(inPreviewMode);
		//List<DenotatorPath> composition0Paths = new ArrayList<DenotatorPath>(this.alteration.getComposition(0));
		//List<List<LimitDenotator>> composition0Nodes = this.actualScore.extractNodes(composition0Paths);
		this.alteration.alter(this.actualScore);
		//List<DenotatorPath> newComposition0Paths = this.actualScore.findPaths(composition0Nodes);
		if (inPreviewMode) {
			this.firePreviewCompositionChange(new TreeSet<DenotatorPath>(), null);
		} else {
			this.fireCompositionChange(this.actualScore, new TreeSet<DenotatorPath>(), null, true);
		}
	}
	
	public List<DenotatorPath> mapNodes(List<DenotatorPath> nodePaths, BigBangTransformation transformation, boolean inPreviewMode, boolean sendCompositionChange) {
		//PerformanceCheck.startTask("prepare");
		//this.updateActualScore(inPreviewMode);
		//PerformanceCheck.startTask("map");
		BigBangMapper mapper = new BigBangMapper(this.actualScore, transformation);
		List<DenotatorPath> newPaths = mapper.mapObjects(nodePaths);
		//PerformanceCheck.startTask("fire");
		if (sendCompositionChange) {
			if (inPreviewMode) {
				this.firePreviewCompositionChange(new TreeSet<DenotatorPath>(nodePaths), transformation.getAnchorNodePath());
			} else {
				this.fireCompositionChange(this.actualScore, new TreeSet<DenotatorPath>(newPaths), transformation.getAnchorNodePath(), true);
			}
		}
		//PerformanceCheck.startTask("draw");
		return newPaths;
	}
	
	public void resetFactualScore() {
		this.actualScore = (BigBangScore) this.score.clone();
	}
	
	
	//TODO: REMOVE!!!
	private void updateActualScore(boolean inPreviewMode) {
		//if (inPreviewMode) {
			this.actualScore = (BigBangScore) this.score.clone();
		//}
		/*towards implementation of dynamic score
		 * else {
			this.actualScore = this.score;
		}*/
	}
	
	public Map<DenotatorPath,Double> shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.updateActualScore(properties.inPreviewMode());
		BigBangShaper shaper = new BigBangShaper(this.actualScore, properties, shapingLocations);
		Map<DenotatorPath,Double> newPathsAndOldYValues = shaper.shapeObjects();
		
		if (properties.inPreviewMode()) {
			this.firePreviewCompositionChange(new TreeSet<DenotatorPath>(properties.getNodePaths()), null);
		} else {
			this.fireCompositionChange(this.actualScore, newPathsAndOldYValues.keySet(), null, true);
		}
		return newPathsAndOldYValues;
	}
	
	public void undoShapeObjects(Map<DenotatorPath,Double> newPathsAndOldYValues) {
		
	}
	
	public DenotatorPath addObject(TreeMap<DenotatorPath,Double> pathsWithValues) {
		DenotatorPath newPath = this.score.addObject(pathsWithValues);
		this.fireCompositionChange();
		this.firePropertyChange(BigBangController.ADD_OBJECT, null, this.score.getObject(newPath));
		return newPath;
	}
	
	public List<DenotatorPath> addObjects(List<Denotator> objects, List<DenotatorPath> parentPaths) {
		List<DenotatorPath> newPaths = this.score.addObjects(objects, parentPaths);
		this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
		return newPaths;
	}
	
	/**
	 * 
	 * @param nodePaths the list of node paths has to be sorted from small to big
	 */
	public List<Denotator> removeObjects(Set<DenotatorPath> objectPaths) {
		List<Denotator> removedObjects = this.score.removeObjects(new ArrayList<DenotatorPath>(objectPaths));
		this.fireCompositionChange();
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
	
	public List<DenotatorPath> moveObjectsToParent(List<DenotatorPath> objectPaths, DenotatorPath parentPath, int powersetIndex) {
		List<DenotatorPath> newPaths = this.score.moveObjectsToParent(objectPaths, parentPath, powersetIndex);
		this.fireCompositionChange(new TreeSet<DenotatorPath>(newPaths));
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
	
	private void fireCompositionChange() {
		this.fireCompositionChange(new TreeSet<DenotatorPath>());
	}
	
	private void fireCompositionChange(Set<DenotatorPath> selectedNodesPaths) {
		this.resetFactualScore();
		this.fireCompositionChange(this.actualScore, selectedNodesPaths, null, false);
	}
	
	private void fireCompositionChange(BigBangScore score, Set<DenotatorPath> selectedNodesPaths, DenotatorPath selectedAnchorPath, boolean playback) {
		Denotator changedComposition = score.getLayeredComposition();
		ScoreChangedNotification notification = new ScoreChangedNotification(changedComposition, selectedNodesPaths, selectedAnchorPath, playback);
		this.firePropertyChange(BigBangController.COMPOSITION, null, notification);
	}
	
	private void firePreviewCompositionChange(Set<DenotatorPath> selectedNodesPaths, DenotatorPath selectedAnchorPath) {
		Denotator changedComposition = this.actualScore.getLayeredComposition();
		ScoreChangedNotification notification = new ScoreChangedNotification(changedComposition, selectedNodesPaths, selectedAnchorPath, true);
		this.firePropertyChange(BigBangController.PREVIEW, null, notification);
	}

}
