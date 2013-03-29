package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
	
	public BigBangMapper(BigBangScore score, BigBangTransformation transformation) {
		super(score, transformation.getValuePaths());
		this.morphism = transformation.getModuleMorphism();
		this.copyAndMap = transformation.isCopyAndMap();
		this.relative = transformation.getAnchorNodePath() != null;
	}
	
	public List<DenotatorPath> mapObjects(List<DenotatorPath> objectPaths) {
		//PerformanceCheck.startTask(".pre");
		List<List<Denotator>> newObjects = new ArrayList<List<Denotator>>();
		objectPaths = this.score.reverseSort(objectPaths);
		
		Iterator<DenotatorPath> objectPathsIterator = objectPaths.iterator();
		if (objectPathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = objectPathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.mapAndAddNextSiblings(newObjects, firstOfNextSiblings, objectPathsIterator);
			}
		}
		//PerformanceCheck.startTask(".find");
		List<DenotatorPath> newPaths = this.score.findPaths(newObjects);
		//TODO: WHY DID WE NOT HAVE TO REVERSE BEFORE??
		Collections.reverse(newPaths);
		return newPaths; 
	}
	
	private DenotatorPath mapAndAddNextSiblings(List<List<Denotator>> newObjects, DenotatorPath firstSiblingPath, Iterator<DenotatorPath> objectPathsIterator) {
		//PerformanceCheck.startTask(".first_sib");
		List<Denotator> siblings = new ArrayList<Denotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		siblingsPaths.add(firstSiblingPath);
		siblings.add(this.score.getAbsoluteObject(firstSiblingPath));
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getAnchorPath();
		ModuleMorphism siblingsMorphism = this.morphism;
		if (this.relative) {
			Denotator siblingsAnchor = this.score.getAbsoluteObject(siblingsAnchorPath);
			siblingsMorphism = this.generateRelativeMorphism(this.extractValues(siblingsAnchor));
		}
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (objectPathsIterator.hasNext()) {
			currentSiblingPath = objectPathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isChildOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.score.getAbsoluteObject(currentSiblingPath));
			} else {
				this.removeMapAndAdd(newObjects, siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism);
				return currentSiblingPath;
			}
		}
		this.removeMapAndAdd(newObjects, siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism);
		return null;
	}
	
	private void removeMapAndAdd(List<List<Denotator>> newNodes, List<Denotator> objects, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths, ModuleMorphism morphism) {
		//PerformanceCheck.startTask(".remove");
		if (!this.copyAndMap) {
			this.score.removeObjects(siblingsPaths);
		}
		newNodes.addAll(this.mapAndAddObjects(objects, anchorPath, morphism));
	}
	
	private List<List<Denotator>> mapAndAddObjects(List<Denotator> objects, DenotatorPath anchorPath, ModuleMorphism morphism) {
		List<Denotator> mappedObjects = new ArrayList<Denotator>();
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(morphism, this.valuePaths);
		//boolean modulators = objects.get(0).getForm().equals(this.score.objectGenerator.SOUND_NOTE_FORM);
		for (int i = 0; i < objects.size(); i++) {
			//PerformanceCheck.startTask(".map");
			Denotator currentObject = objects.get(i);
			try {
				mappedObjects.add(mapper.getMappedDenotator(currentObject));
			} catch (RubatoException e) { e.printStackTrace(); }
		}
		//PerformanceCheck.startTask(".add");
		//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE  
		List<DenotatorPath> newPaths = this.score.addObjectsToParent(mappedObjects, anchorPath, 0);
		//PerformanceCheck.startTask(".extract");
		return this.score.extractObjects(newPaths);
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
	private double[] extractValues(Denotator object) {
		double v1 = 0, v2 = 0;
		v1 = this.score.objectGenerator.getDoubleValue(object, this.valuePaths.get(0));
		v2 = this.score.objectGenerator.getDoubleValue(object, this.valuePaths.get(1));
		return new double[] {v1, v2};
	}

}
