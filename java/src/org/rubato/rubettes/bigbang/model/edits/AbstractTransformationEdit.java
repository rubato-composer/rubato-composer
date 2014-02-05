package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangTransformation;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractTransformationEdit extends AbstractOperationEdit {
	
	private Set<BigBangObject> objects;
	private BigBangObject anchor;
	private List<TransformationPaths> transformationPaths;
	protected double[] center;
	protected double[] endingPoint;
	private boolean copyAndTransform;
	private ModuleMorphism transformation;
	
	//used for cloning
	protected AbstractTransformationEdit(BigBangDenotatorManager denotatorManager) {
		super(denotatorManager);
		this.isAnimatable = true;
		this.isSplittable = true;
	}
	
	public AbstractTransformationEdit(BigBangDenotatorManager denotatorManager, TransformationProperties properties) {
		super(denotatorManager);
		this.setProperties(properties);
		this.isAnimatable = true;
		this.isSplittable = true;
	}
	
	private void setProperties(TransformationProperties properties) {
		this.objects = properties.getObjects();
		this.anchor = properties.getAnchor();
		this.transformationPaths = properties.getTransformationPaths();
		this.copyAndTransform = properties.copyAndTransform();
		this.center = properties.getCenter();
		this.endingPoint = properties.getEndPoint();
	}
	
	public void updateProperties(TransformationProperties properties) {
		this.setProperties(properties);
		this.updateOperation();
	}
	
	public void modifyCenter(double[] newValues) {
		this.center = newValues;
		this.updateOperation();
	}
	
	public void modify(double[] newValues) {
		this.endingPoint = newValues;
		this.updateOperation();
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
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths(this.objects);
		DenotatorPath anchorPath = null;
		if (this.anchor != null) {
			anchorPath = this.anchor.getTopDenotatorPathAt(this);
		}
		BigBangTransformation transformation = new BigBangTransformation(this.transformation, this.transformationPaths, this.copyAndTransform, anchorPath);
		return this.denotatorManager.addTransformation(objectPaths, anchorPath, transformation);
	}
	
	public int[] getXYViewParameters() {
		return this.transformationPaths.get(0).getXYCoordinates();
	}
	
	public double[] getCenter() {
		return this.center;
	}
	
	public abstract double[] getEndingPoint();
	
	protected double round(double number) {
		return ((double)Math.round(number*100))/100;
	}
	
	public AbstractTransformationEdit clone() {
		AbstractTransformationEdit clone;
		try {
			clone = this.getClass().getDeclaredConstructor(BigBangDenotatorManager.class).newInstance(this.denotatorManager);
			clone.objects = this.objects;
			clone.transformationPaths = this.transformationPaths;
			clone.anchor = this.anchor;
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
