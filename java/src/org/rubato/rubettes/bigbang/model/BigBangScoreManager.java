package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.model.SelectedPaths;
import org.rubato.rubettes.util.NotePath;
import org.rubato.rubettes.util.PerformanceCheck;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class BigBangScoreManager extends Model {
	
	private BigBangScore score, actualScore;
	private BigBangWallpaper wallpaper;
	private BigBangAlteration alteration;
	private BigBangPlayer player;
	private boolean playingActive;
	
	public BigBangScoreManager(BigBangController controller) {
		controller.addModel(this);
		this.score = new BigBangScore(new SoundNoteGenerator());
		this.alteration = new BigBangAlteration(controller);
		this.player = new BigBangPlayer();
		this.playingActive = false;
		this.setTempo(BigBangPlayer.INITIAL_BPM);
		this.score.setNoteGenerator(new SoundNoteGenerator());
	}
	
	public void togglePlayMode() {
		this.playingActive = !this.playingActive;
		if (this.playingActive) {
			this.playCompositionImmediately();
		} else {
			this.stopPlayer();
		}
		this.firePropertyChange(BigBangController.PLAY_MODE, null, this.playingActive);
	}
	
	public void newWindowAdded(SelectedPaths paths) {
		this.fireCompositionChange(this.score, paths.getNodePaths(), paths.getAnchorPath(), false);
		this.alteration.fireState();
	}
	
	public boolean setComposition(PowerDenotator newComposition) {
		boolean valid = this.score.setComposition(newComposition);
		if (valid) {
			this.fireCompositionChange();
		}
		return valid;
	}
	
	public PowerDenotator getComposition() {
		if (this.actualScore != null) {
			return this.actualScore.getComposition();
		}
		return this.score.getComposition();
	}
	
	public void startWallpaper(ArrayList<NotePath> nodePaths) {
		this.wallpaper = new BigBangWallpaper(nodePaths);
	}
	
	public void addWallpaperDimensionS(int rangeFrom, int rangeTo) {
		this.wallpaper.addDimension(rangeFrom, rangeTo);
	}
	
	public void removeLastWallpaperDimension() {
		this.wallpaper.removeLastDimension();
	}
	
	public Set<NotePath> addWallpaperTransformation(BigBangTransformation transformation, boolean inPreviewMode) {
		this.wallpaper.addTransformationToLastDimension(transformation);
		Set<NotePath> newPaths = this.createWallpaper(true, !inPreviewMode);
		if (inPreviewMode) {
			this.wallpaper.removeLastTransformation();
		}
		return newPaths;
	}
	
	public void removeLastWallpaperTransformation() {
		this.wallpaper.removeLastTransformation();
	}
	
	private Set<NotePath> createWallpaper(boolean inPreviewMode, boolean selectMotif) {
		List<NotePath> motifPaths = this.wallpaper.getMotif();
		this.updateActualScore(inPreviewMode);
		List<List<LimitDenotator>> motifNodes = this.actualScore.extractNotes(motifPaths);
		//CREATE ACTUAL WALLPAPER (to be selected when wallpaper finished)
		this.wallpaper.applyTo(this.actualScore);
		NotePath lastAnchorPath = this.wallpaper.getLastAnchorPath();
		Set<NotePath> newMotifPaths = new TreeSet<NotePath>(this.actualScore.addNotes(motifNodes));
		if (inPreviewMode) {
			if (selectMotif) {
				this.fireCompositionChange(this.actualScore, newMotifPaths, lastAnchorPath, true);
			} else {
				this.firePreviewCompositionChange(newMotifPaths, lastAnchorPath);
			}
		} else {
			this.fireCompositionChange(this.actualScore, newMotifPaths, lastAnchorPath, true);
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
		Set<NotePath> selectedNodesPaths = this.alteration.getComposition(index);
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
			this.firePreviewCompositionChange(new TreeSet<NotePath>(), null);
		} else {
			this.fireCompositionChange(this.actualScore, new TreeSet<NotePath>(), null, true);
		}
	}
	
	public Set<NotePath> mapNodes(Set<NotePath> nodePaths, BigBangTransformation transformation, boolean inPreviewMode) {
		PerformanceCheck.startTask("prepare");
		this.updateActualScore(inPreviewMode);
		PerformanceCheck.startTask("map");
		BigBangMapper mapper = new BigBangMapper(this.actualScore, transformation);
		Set<NotePath> newPaths = new TreeSet<NotePath>(mapper.mapNodes(new ArrayList<NotePath>(nodePaths)));
		PerformanceCheck.startTask("fire");
		if (inPreviewMode) {
			this.firePreviewCompositionChange(new TreeSet<NotePath>(nodePaths), transformation.getAnchorNodePath());
		} else {
			this.fireCompositionChange(this.actualScore, newPaths, transformation.getAnchorNodePath(), true);
		}
		PerformanceCheck.startTask("draw");
		return newPaths;
	}
	
	private void updateActualScore(boolean inPreviewMode) {
		if (inPreviewMode) {
			this.actualScore = (BigBangScore) this.score.clone();
		} else {
			this.actualScore = this.score;
		}
	}
	
	public Map<NotePath,Double> shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.updateActualScore(properties.inPreviewMode());
		BigBangShaper shaper = new BigBangShaper(this.actualScore, properties, shapingLocations);
		Map<NotePath,Double> newPathsAndOldYValues = shaper.shapeNotes();
		
		if (properties.inPreviewMode()) {
			this.firePreviewCompositionChange(new TreeSet<NotePath>(properties.getNodePaths()), null);
		} else {
			this.fireCompositionChange(this.actualScore, newPathsAndOldYValues.keySet(), null, true);
		}
		return newPathsAndOldYValues;
	}
	
	public void undoShapeNotes(Map<NotePath,Double> newPathsAndOldYValues) {
		
	}
	
	public NotePath addNote(double[] values) {
		NotePath newPath = this.score.addNote(values);
		this.fireCompositionChange();
		this.playNote(this.score.getNote(newPath));
		return newPath;
	}
	
	public List<NotePath> addNotes(List<LimitDenotator> notes, List<NotePath> parentPaths) {
		List<NotePath> newPaths = this.score.addNotes(notes, parentPaths);
		this.fireCompositionChange(new TreeSet<NotePath>(newPaths));
		return newPaths;
	}
	
	/**
	 * 
	 * @param nodePaths the list of node paths has to be sorted from small to big
	 */
	public List<LimitDenotator> removeNotes(Set<NotePath> notePaths) {
		List<LimitDenotator> notes = this.score.removeNotes(new ArrayList<NotePath>(notePaths));
		this.fireCompositionChange();
		return notes;
	}
	
	public LimitDenotator removeNote(NotePath nodePath) {
		LimitDenotator removedNode = this.score.removeNote(nodePath);
		this.fireCompositionChange();
		return removedNode;
	}
	
	public List<NotePath> copyNotesToLayer(Set<NotePath> notePaths, int layerIndex) {
		List<NotePath> newPaths = this.score.copyNotesToLayer(new ArrayList<NotePath>(notePaths), layerIndex);
		this.fireCompositionChange(new TreeSet<NotePath>(newPaths));
		return newPaths;
	}
	
	public Map<NotePath,Integer> moveNotesToLayer(Set<NotePath> notePaths, int layerIndex) {
		Map<NotePath,Integer> newPathsAndOldLayer = this.score.moveNotesToLayer(new ArrayList<NotePath>(notePaths), layerIndex);
		this.fireCompositionChange(newPathsAndOldLayer.keySet());
		return newPathsAndOldLayer;
	}
	
	public Set<NotePath> moveNotesToLayers(Map<NotePath,Integer> newPathsAndOldLayer) {
		List<NotePath> newPaths = this.score.moveNotesToLayers(newPathsAndOldLayer);
		this.fireCompositionChange(newPathsAndOldLayer.keySet());
		return new TreeSet<NotePath>(newPaths);
	}
	
	public List<NotePath> moveNotesToParent(List<NotePath> notePaths, NotePath parentPath, boolean asModulators) {
		List<NotePath> newPaths = this.score.moveNotesToParent(notePaths, parentPath, asModulators);
		this.fireCompositionChange(new TreeSet<NotePath>(newPaths));
		return newPaths;
	}
	
	/**
	 * All satellites have to have the same anchor node.
	 * @param satellitePaths
	 * @param goalPaths
	 * @param anchorNode
	 */
	public List<NotePath> undoMoveToParent(List<NotePath> actualPaths, List<NotePath> oldPaths) {
		List<LimitDenotator> newNotes = this.score.removeNotes(actualPaths);
		List<NotePath> newPaths = this.score.addNotes(newNotes, NotePath.getParentPaths(oldPaths));
		this.fireCompositionChange(new TreeSet<NotePath>(newPaths));
		return newPaths;
	}
	
	/**
	 * Flattens the given notes so that they end up in the next higher hierarchical level,
	 * while maintaining their function as satellites or modulators.
	 * @param notePaths
	 * @return
	 */
	public TreeMap<NotePath,NotePath> flattenNotes(Set<NotePath> notePaths) {
		List<NotePath> notePathsList = new ArrayList<NotePath>(notePaths);
		List<NotePath> oldParentPaths = NotePath.getParentPaths(notePathsList);
		List<NotePath> oldGrandParentPaths = NotePath.getGrandParentPaths(notePathsList);
		int[] noteFunctions = NotePath.getFunctions(notePathsList);
		List<LimitDenotator> notes = this.score.removeNotes(notePathsList);
		List<NotePath> newPaths = this.score.addNotes(notes, oldGrandParentPaths, noteFunctions);
		TreeMap<NotePath,NotePath> newPathsAndOldParentPaths = this.getPathMap(newPaths, oldParentPaths);
		this.fireCompositionChange(newPathsAndOldParentPaths.keySet());
		return newPathsAndOldParentPaths;
	}
	
	private TreeMap<NotePath,NotePath> getPathMap(List<NotePath> keys, List<NotePath> values) {
		TreeMap<NotePath,NotePath> newPathsAndOldMSPaths = new TreeMap<NotePath,NotePath>();
		for (int i = 0; i < keys.size(); i++) {
			newPathsAndOldMSPaths.put(keys.get(i), values.get(i));
		}
		return newPathsAndOldMSPaths;
	}
	
	public Set<NotePath> unflattenNotes(Map<NotePath,NotePath> newAndOldPaths) {
		List<NotePath> keys = new ArrayList<NotePath>(newAndOldPaths.keySet());
		List<LimitDenotator> notes = this.score.removeNotes(keys);
		Set<NotePath> newPaths = new TreeSet<NotePath>(this.score.addNotes(notes, new ArrayList<NotePath>(newAndOldPaths.values())));
		this.fireCompositionChange(newPaths);
		return newPaths;
	}
	
	private void fireCompositionChange() {
		this.fireCompositionChange(new TreeSet<NotePath>());
	}
	
	private void fireCompositionChange(Set<NotePath> selectedNodesPaths) {
		this.fireCompositionChange(this.score, selectedNodesPaths, null, false);
	}
	
	private void fireCompositionChange(BigBangScore score, Set<NotePath> selectedNodesPaths, NotePath selectedAnchorPath, boolean playback) {
		PowerDenotator changedComposition = score.getLayeredComposition();
		ScoreChangedNotification notification = new ScoreChangedNotification(changedComposition, selectedNodesPaths, selectedAnchorPath);
		this.firePropertyChange(BigBangController.COMPOSITION, null, notification);
		if (playback) {
			this.playCompositionImmediately(changedComposition);
		}
	}
	
	private void firePreviewCompositionChange(Set<NotePath> selectedNodesPaths, NotePath selectedAnchorPath) {
		PowerDenotator changedComposition = this.actualScore.getLayeredComposition();
		ScoreChangedNotification notification = new ScoreChangedNotification(changedComposition, selectedNodesPaths, selectedAnchorPath);
		this.firePropertyChange(BigBangController.PREVIEW, null, notification);
		this.playComposition(changedComposition);
	}
	
	private void playCompositionImmediately() {
		this.playCompositionImmediately(this.score.getLayeredComposition());
	}
	
	private void playCompositionImmediately(PowerDenotator composition) {
		if (this.playingActive) {
			this.player.playCompositionImmediately(composition);
		}
	}
	
	private void playComposition(PowerDenotator composition) {
		if (this.playingActive) {
			this.player.playComposition(composition);
		}
	}
	
	private void playNote(Denotator node) {
		if (this.playingActive) {
			this.player.playSingleNote(node);
		}
	}
	
	private void stopPlayer() {
		this.player.stopPlaying();
		//System.gc();
	}
	
	public void setTempo(Integer tempo) {
		this.player.setTempo(tempo);
		if (this.playingActive) {
			this.togglePlayMode();
		}
		this.firePropertyChange(BigBangController.TEMPO, null, tempo);
	}
	
	public void setFMModel(String fmModel) {
		this.score.noteGenerator.setFMModel(fmModel);
		this.firePropertyChange(BigBangController.FM_MODEL, null, fmModel);
	}
	
	public void setWaveform(String waveform) {
		this.player.setWaveform(waveform);
		this.firePropertyChange(BigBangController.WAVEFORM, null, waveform);
	}

}
