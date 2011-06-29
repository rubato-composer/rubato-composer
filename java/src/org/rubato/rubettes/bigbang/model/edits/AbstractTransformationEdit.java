package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.BigBangTransformation;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.NotePath;

public abstract class AbstractTransformationEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreManager;
	protected TransformationProperties properties;
	private Set<NotePath> copyPaths;
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
	
	public void map() {
		Set<NotePath> nodePaths = this.properties.getNodePaths();
		int[][] elementPaths = this.properties.getElementPaths();
		NotePath anchorNodePath = this.properties.getAnchorNodePath();
		boolean inPreviewMode = this.properties.inPreviewMode();
		boolean copyAndTransform = this.properties.copyAndTransform();
		boolean inWallpaperMode = this.properties.inWallpaperMode();
		BigBangTransformation transformation = new BigBangTransformation(this.transformation, elementPaths, copyAndTransform, anchorNodePath);
		Set<NotePath> resultPaths;
		if (inWallpaperMode) {
			resultPaths = this.scoreManager.addWallpaperTransformation(transformation, inPreviewMode);
		} else {
			resultPaths = this.scoreManager.mapNodes(nodePaths, transformation, inPreviewMode);
		}
		if (copyAndTransform || inWallpaperMode) {
			this.copyPaths = resultPaths;
		} else {
			this.properties.setNodePaths(resultPaths);
		}
	}
	
	public BigBangScoreManager getScoreManager() {
		return this.scoreManager;
	}
	
	public int[][] getElementPaths() {
		return this.properties.getElementPaths();
	}
	
	public String toString() {
		return this.getPresentationName();
	}
	
	@Override
	public String getPresentationName() {
		return this.properties.getEndPoint()[0] + "," + this.properties.getEndPoint()[1];
	}

}
