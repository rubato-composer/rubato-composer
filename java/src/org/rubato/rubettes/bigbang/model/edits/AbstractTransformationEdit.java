package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.BigBangTransformation;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractTransformationEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreManager;
	protected TransformationProperties properties;
	private List<DenotatorPath> copyPaths;
	private List<DenotatorPath> previousResultPaths;
	private ModuleMorphism transformation;
	
	public AbstractTransformationEdit(BigBangScoreManager scoreManager, TransformationProperties properties) {
		this.scoreManager = scoreManager;
		this.properties = properties;
	}
	
	protected abstract void initTransformation();
	
	public void modify(double[] newValues) {
		this.properties.setEndPoint(newValues);
		this.initTransformation();
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		this.properties.setInPreviewMode(inPreviewMode);
	}
	
	/*public void redo() {
		super.redo();
		this.map();
	}
	
	public void undo() {
		//REMOVE ALL UNDO METHODS
		super.undo();
		if (this.properties.copyAndTransform()) {
			this.score.removeNotes(this.copyPaths);
		}
		if (this.properties.inWallpaperMode()) {
			this.score.removeLastWallpaperTransformation();
		}
	}*/
	
	protected void initTransformation(RMatrix matrix, double[] shift) {
		List<RMatrix> matrices = new ArrayList<RMatrix>();
		matrices.add(matrix);
		List<double[]> shifts = new ArrayList<double[]>();
		shifts.add(shift);
		this.initTransformation(matrices, shifts);
	}
	
	protected void initTransformation(List<RMatrix> matrices, List<double[]> shifts) {
		ModuleMorphism morphism = RFreeAffineMorphism.make(matrices.get(0), shifts.get(0));
		for (int i = 1; i < matrices.size(); i++) {
			try {
				morphism = RFreeAffineMorphism.make(matrices.get(i), shifts.get(i)).compose(morphism);
			} catch (CompositionException e) { e.printStackTrace(); }
		}
		this.transformation = morphism;
	}
	
	//TODO: return changes in paths!!!
	public Map<DenotatorPath,DenotatorPath> map(Map<DenotatorPath,DenotatorPath> pathDifferences) {
		this.properties.updateNodePaths(pathDifferences);
		List<DenotatorPath> notePaths = new ArrayList<DenotatorPath>(this.properties.getNodePaths());
		List<DenotatorPath> valuePaths = this.properties.getValuePaths();
		DenotatorPath anchorNodePath = this.properties.getAnchorNodePath();
		boolean inPreviewMode = this.properties.inPreviewMode();
		boolean copyAndTransform = this.properties.copyAndTransform();
		boolean inWallpaperMode = this.properties.inWallpaperMode();
		BigBangTransformation transformation = new BigBangTransformation(this.transformation, valuePaths, copyAndTransform, anchorNodePath);
		List<DenotatorPath> resultPaths;
		if (inWallpaperMode) {
			resultPaths = this.scoreManager.addWallpaperTransformation(transformation, inPreviewMode);
		} else {
			resultPaths = this.scoreManager.mapNodes(notePaths, transformation, inPreviewMode);
		}
		if (copyAndTransform || inWallpaperMode) {
			this.copyPaths = resultPaths;
		} else {
			//WOW not at all compatible with dynamic score mapping!!
			//this.properties.setNodePaths(resultPaths);
		}
		Map<DenotatorPath,DenotatorPath> newDifferences = this.getPathDifferences(this.previousResultPaths, resultPaths);
		this.previousResultPaths = resultPaths;
		return newDifferences;
	}
	
	private Map<DenotatorPath,DenotatorPath> getPathDifferences(List<DenotatorPath> oldPaths, List<DenotatorPath> newPaths) {
		Map<DenotatorPath,DenotatorPath> pathDifferences = new TreeMap<DenotatorPath,DenotatorPath>();
		if (oldPaths != null) {
			if (oldPaths.size() != newPaths.size()) {
				return pathDifferences;
			}
			for (int i = 0; i < newPaths.size(); i++) {
				if (!oldPaths.get(i).equals(newPaths.get(i))) {
					pathDifferences.put(oldPaths.get(i), newPaths.get(i));
				}
			}
		}
		return pathDifferences;
	}
	
	public BigBangScoreManager getScoreManager() {
		return this.scoreManager;
	}
	
	public List<DenotatorPath> getValuePaths() {
		return this.properties.getValuePaths();
	}
	
	public String toString() {
		return this.getPresentationName();
	}
	
	@Override
	public String getPresentationName() {
		return this.properties.getEndPoint()[0] + "," + this.properties.getEndPoint()[1];
	}

}
