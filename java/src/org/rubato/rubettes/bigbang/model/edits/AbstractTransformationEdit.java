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
	
	private BigBangScoreManager score;
	private TransformationProperties properties;
	private Set<NotePath> copyPaths;
	
	public AbstractTransformationEdit(BigBangScoreManager score, TransformationProperties properties) {
		this.score = score;
		this.properties = properties;
	}
	
	public abstract void execute();
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		if (this.properties.copyAndTransform()) {
			this.score.removeNotes(this.copyPaths);
		}
		if (this.properties.inWallpaperMode()) {
			this.score.removeLastWallpaperTransformation();
		}
	}
	
	protected void map(RMatrix matrix, double[] shift) {
		List<RMatrix> matrices = new ArrayList<RMatrix>();
		matrices.add(matrix);
		List<double[]> shifts = new ArrayList<double[]>();
		shifts.add(shift);
		this.map(matrices, shifts);
	}
	
	protected void map(List<RMatrix> matrices, List<double[]> shifts) {
		ModuleMorphism morphism = RFreeAffineMorphism.make(matrices.get(0), shifts.get(0));
		for (int i = 1; i < matrices.size(); i++) {
			try {
				morphism = RFreeAffineMorphism.make(matrices.get(i), shifts.get(i)).compose(morphism);
			} catch (CompositionException e) { e.printStackTrace(); }
		}
		this.map(morphism);
	}
	
	private void map(ModuleMorphism morphism) {
		Set<NotePath> nodePaths = this.properties.getNodePaths();
		int[][] elementPaths = this.properties.getElementPaths();
		NotePath anchorNodePath = this.properties.getAnchorNodePath();
		boolean inPreviewMode = this.properties.inPreviewMode();
		boolean copyAndTransform = this.properties.copyAndTransform();
		boolean inWallpaperMode = this.properties.inWallpaperMode();
		BigBangTransformation transformation = new BigBangTransformation(morphism, elementPaths, copyAndTransform, anchorNodePath);
		Set<NotePath> resultPaths;
		if (inWallpaperMode) {
			resultPaths = this.score.addWallpaperTransformation(transformation, inPreviewMode);
		} else {
			resultPaths = this.score.mapNodes(nodePaths, transformation, inPreviewMode);
		}
		if (copyAndTransform || inWallpaperMode) {
			this.copyPaths = resultPaths;
		} else {
			this.properties.setNodePaths(resultPaths);
		}
	}
	
	public int[][] getElementPaths() {
		return this.properties.getElementPaths();
	}
	
	public String toString() {
		return this.getPresentationName();
	}

}
