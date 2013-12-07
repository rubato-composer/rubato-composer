package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.BigBangTransformation;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractTransformationEdit extends AbstractOperationEdit {
	
	private SelectedObjectsPaths objectsPaths;
	private List<TransformationPaths> transformationPaths;
	private DenotatorPath anchorNodePath;
	protected double[] center;
	protected double[] endingPoint;
	private boolean inPreviewMode;
	private boolean copyAndTransform;
	private SelectedObjectsPaths previousResultPaths;
	private ModuleMorphism transformation;
	
	//used for cloning
	protected AbstractTransformationEdit(BigBangScoreManager scoreManager) {
		super(scoreManager);
		this.isAnimatable = true;
		this.isSplittable = true;
	}
	
	public AbstractTransformationEdit(BigBangScoreManager scoreManager, TransformationProperties properties) {
		super(scoreManager);
		this.objectsPaths = properties.getObjectsPaths();
		this.transformationPaths = properties.getTransformationPaths();
		this.anchorNodePath = properties.getAnchorNodePath();
		this.inPreviewMode = properties.inPreviewMode();
		this.copyAndTransform = properties.copyAndTransform();
		this.center = properties.getCenter();
		this.endingPoint = properties.getEndPoint();
		this.isAnimatable = true;
		this.isSplittable = true;
	}
	
	public void modifyCenter(double[] newValues) {
		this.center = newValues;
		this.updateOperation();
	}
	
	public void modify(double[] newValues) {
		this.endingPoint = newValues;
		this.updateOperation();
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		this.inPreviewMode = inPreviewMode;
	}
	
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
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean sendCompositionChange) {
		return this.map(pathDifferences, sendCompositionChange);
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> map(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean sendCompositionChange) {
		this.updateObjectPaths(pathDifferences);
		BigBangTransformation transformation = new BigBangTransformation(this.transformation, this.transformationPaths, this.copyAndTransform, this.anchorNodePath);
		SelectedObjectsPaths resultPaths = this.scoreManager.addTransformation(this.objectsPaths, transformation, this.inPreviewMode, sendCompositionChange);
		List<Map<DenotatorPath,DenotatorPath>> newDifferences = this.getPathDifferences(this.previousResultPaths, resultPaths);
		this.previousResultPaths = resultPaths;
		return newDifferences;
	}
	
	public void updateObjectPaths(List<Map<DenotatorPath,DenotatorPath>> pathDifferences) {
		for (int i = 0; i < pathDifferences.size(); i++) {
			Map<DenotatorPath,DenotatorPath> currentObjectDifferences = pathDifferences.get(i);
			if (i < this.objectsPaths.size()) {
				List<DenotatorPath> currentObjectPaths = this.objectsPaths.get(i);
				for (DenotatorPath currentPath : currentObjectDifferences.keySet()) {
					if (currentObjectPaths.contains(currentPath)) {
						DenotatorPath newPath = currentObjectDifferences.get(currentPath);
						currentObjectPaths.add(newPath);
						currentObjectPaths.remove(currentPath);
					}
				}
			}
		}
	}
	
	public int[] getXYViewParameters() {
		return this.transformationPaths.get(0).getXYCoordinates();
	}
	
	public abstract double[] getEndingPoint();
	
	protected double round(double number) {
		return ((double)Math.round(number*100))/100;
	}
	
	public AbstractTransformationEdit clone() {
		AbstractTransformationEdit clone;
		try {
			clone = this.getClass().getDeclaredConstructor(BigBangScoreManager.class).newInstance(this.scoreManager);
			clone.objectsPaths = this.objectsPaths;
			clone.transformationPaths = this.transformationPaths;
			clone.anchorNodePath = this.anchorNodePath;
			clone.inPreviewMode = this.inPreviewMode;
			clone.copyAndTransform = this.copyAndTransform;
			clone.center = this.center;
			clone.endingPoint = this.endingPoint;
			//clone.updateOperation();
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
