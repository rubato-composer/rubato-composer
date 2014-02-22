package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.util.ArbitraryDenotatorMapper;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangMapper extends BigBangScoreManipulator {
	
	private ModuleMorphism morphism;
	private boolean copyAndMap;
	private boolean relative;
	
	public BigBangMapper(BigBangDenotatorManager denotatorManager, BigBangTransformation transformation) {
		super(denotatorManager, transformation.getTransformationPaths());
		this.morphism = transformation.getModuleMorphism();
		this.copyAndMap = transformation.isCopyAndMap();
		this.relative = transformation.getAnchorNodePath() != null;
	}
	
	/**
	 * Maps the objects at the given paths
	 * @return the resulting newPaths
	 */
	public OperationPathResults mapCategorizedObjects(Set<DenotatorPath> objectPaths) {
		//TODO REALLY CHECK WHAT HAPPENS WITHOUT CATEGORIZED OBJECTS
		//for (int i = 0; i < objectPaths.size(); i++) {
		this.mapObjects(new ArrayList<DenotatorPath>(objectPaths), this.transformationPaths.get(0));
		//}
		//return this.denotatorManager.getCurrentNewPaths();
		return this.denotatorManager.getPathResults();
	}
	
	private void mapObjects(List<DenotatorPath> objectPaths, TransformationPaths transformationPaths) {
		//TODO check if still necessary
		objectPaths = this.denotatorManager.sortAndReverse(objectPaths);
		
		Iterator<DenotatorPath> objectPathsIterator = objectPaths.iterator();
		if (objectPathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = objectPathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.mapAndAddNextSiblings(firstOfNextSiblings, objectPathsIterator, transformationPaths);
			}
		}
	}
	
	private DenotatorPath mapAndAddNextSiblings(DenotatorPath firstSiblingPath, Iterator<DenotatorPath> objectPathsIterator, TransformationPaths transformationPaths) {
		//PerformanceCheck.startTask(".first_sib");
		List<Denotator> siblings = new ArrayList<Denotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		Denotator firstSibling = this.denotatorManager.getAbsoluteObject(firstSiblingPath);
		if (firstSibling != null) {
			siblingsPaths.add(firstSiblingPath);
			siblings.add(firstSibling);
		}
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getAnchorPath();
		ModuleMorphism siblingsMorphism = this.morphism;
		if (this.relative) {
			Denotator siblingsAnchor = this.denotatorManager.getAbsoluteObject(siblingsAnchorPath);
			siblingsMorphism = this.generateRelativeMorphism(this.extractValues(siblingsAnchor, transformationPaths));
		}
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (objectPathsIterator.hasNext()) {
			currentSiblingPath = objectPathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isDirectSatelliteOf(siblingsAnchorPath)) {
				Denotator currentSibling = this.denotatorManager.getAbsoluteObject(currentSiblingPath);
				if (currentSibling != null) {
					siblingsPaths.add(currentSiblingPath);
					siblings.add(currentSibling);
				}
			} else {
				this.mapAndReplaceOrAdd(siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism, transformationPaths);
				return currentSiblingPath;
			}
		}
		this.mapAndReplaceOrAdd(siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism, transformationPaths);
		return null;
	}
	
	/*
	 * Maps the given objects and adds them to the given anchorPath (they should thus originally be siblings).
	 * Returns a list with all the 
	 */
	private void mapAndReplaceOrAdd(List<Denotator> objects, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths, ModuleMorphism morphism, TransformationPaths transformationPaths) {
		if (objects.size() > 0) {
			List<Denotator> mappedObjects = new ArrayList<Denotator>();
			ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(morphism, transformationPaths);
			for (int i = 0; i < objects.size(); i++) {
				Denotator currentObject = objects.get(i);
				try {
					mappedObjects.add(mapper.getMappedDenotator(currentObject));
				} catch (RubatoException e) {
					e.printStackTrace();
				}
			}
			if (!this.copyAndMap) {
				this.denotatorManager.replaceSiblingObjects(mappedObjects, siblingsPaths);
			} else {
				//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE! FIGURE OUT POWERSET INDEX!!
				DenotatorPath powersetPath = anchorPath.getPowersetPath(0);
				this.denotatorManager.addObjectsToParent(mappedObjects, powersetPath);
			}
		}
	}
	
	private ModuleMorphism generateRelativeMorphism(double[] anchorLocation) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		double[] shift1 = new double[]{-1*anchorLocation[0],-1*anchorLocation[1]};
		double[] shift2 = new double[]{anchorLocation[0],anchorLocation[1]};
		ModuleMorphism relativeMorphism = this.morphism;
		try {
			relativeMorphism = relativeMorphism.compose(RFreeAffineMorphism.make(identity, shift1));
			relativeMorphism = RFreeAffineMorphism.make(identity, shift2).compose(relativeMorphism);
		} catch (CompositionException e) { e.printStackTrace(); }
		return relativeMorphism;
	}
	
	//TODO: WOOOO REMOVE THIS AND MAKE OBJECT GENERATOR WORK!!!
	private double[] extractValues(Denotator object, TransformationPaths transformationPaths) {
		double v1 = 0, v2 = 0;
		v1 = this.denotatorManager.getObjectGenerator().getDoubleValue(object, transformationPaths.getDomainPath(0, object));
		v2 = this.denotatorManager.getObjectGenerator().getDoubleValue(object, transformationPaths.getDomainPath(1, object));
		return new double[] {v1, v2};
	}

}
