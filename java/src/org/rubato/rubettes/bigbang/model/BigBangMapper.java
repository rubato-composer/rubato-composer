package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.ArbitraryDenotatorMapper;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangMapper extends BigBangScoreManipulator {
	
	private ModuleMorphism morphism;
	private boolean copyAndMap;
	private boolean relative;
	
	public BigBangMapper(BigBangScore score, BigBangTransformation transformation) {
		super(score, transformation.getCoordinatePaths());
		this.morphism = transformation.getModuleMorphism();
		this.copyAndMap = transformation.isCopyAndMap();
		this.relative = transformation.getAnchorNodePath() != null;
	}
	
	public List<DenotatorPath> mapNodes(List<DenotatorPath> nodePaths) {
		//PerformanceCheck.startTask(".pre");
		List<List<LimitDenotator>> newNodes = new ArrayList<List<LimitDenotator>>();
		nodePaths = this.score.reverseSort(nodePaths);
		
		Iterator<DenotatorPath> nodePathsIterator = nodePaths.iterator();
		if (nodePathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = nodePathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.mapAndAddNextSiblings(newNodes, firstOfNextSiblings, nodePathsIterator);
			}
		}
		//PerformanceCheck.startTask(".find");
		List<DenotatorPath> newPaths = this.score.findPaths(newNodes);
		return newPaths; 
	}
	
	private DenotatorPath mapAndAddNextSiblings(List<List<LimitDenotator>> newNodes, DenotatorPath firstSiblingPath, Iterator<DenotatorPath> nodePathsIterator) {
		//PerformanceCheck.startTask(".first_sib");
		List<LimitDenotator> siblings = new ArrayList<LimitDenotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		siblingsPaths.add(firstSiblingPath);
		siblings.add(this.getNode(firstSiblingPath));
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getParentPath();
		ModuleMorphism siblingsMorphism = this.morphism;
		if (this.relative) {
			LimitDenotator siblingsAnchor = this.score.getAbsoluteNote(siblingsAnchorPath);
			siblingsMorphism = this.generateRelativeMorphism(this.extractValues(siblingsAnchor));
		}
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (nodePathsIterator.hasNext()) {
			currentSiblingPath = nodePathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isChildOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.getNode(currentSiblingPath));
			} else {
				this.removeMapAndAdd(newNodes, siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism);
				return currentSiblingPath;
			}
		}
		this.removeMapAndAdd(newNodes, siblings, siblingsAnchorPath, siblingsPaths, siblingsMorphism);
		return null;
	}
	
	private void removeMapAndAdd(List<List<LimitDenotator>> newNodes, List<LimitDenotator> nodes, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths, ModuleMorphism morphism) {
		//PerformanceCheck.startTask(".remove");
		if (!this.copyAndMap) {
			this.score.removeNotes(siblingsPaths);
		}
		newNodes.addAll(this.mapAndAddNodes(nodes, anchorPath, morphism));
	}
	
	private List<List<LimitDenotator>> mapAndAddNodes(List<LimitDenotator> nodesAndNotes, DenotatorPath anchorPath, ModuleMorphism morphism) {
		List<LimitDenotator> mappedNodesOrNotes = new ArrayList<LimitDenotator>();
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(morphism, this.coordinatePaths);
		boolean modulators = nodesAndNotes.get(0).getForm().equals(this.score.noteGenerator.SOUND_NOTE_FORM);
		for (int i = 0; i < nodesAndNotes.size(); i++) {
			//PerformanceCheck.startTask(".map");
			LimitDenotator currentNote = nodesAndNotes.get(i);
			Denotator satellites = null;
			if (!modulators) {
				satellites = currentNote.getFactor(1).copy();
				currentNote = (LimitDenotator) currentNote.getFactor(0);
			}
			try {
				LimitDenotator mappedNote = (LimitDenotator)mapper.getMappedDenotator(currentNote);
				if (!modulators) {
					mappedNote = this.score.getNoteGenerator().createNodeDenotator(mappedNote, satellites);
				}
				mappedNodesOrNotes.add(mappedNote);
			} catch (RubatoException e) { e.printStackTrace(); }
		}
		//PerformanceCheck.startTask(".add");
		//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE  
		List<DenotatorPath> newPaths = this.score.addNotesToParent(mappedNodesOrNotes, anchorPath, modulators);
		//PerformanceCheck.startTask(".extract");
		return this.score.extractNotes(newPaths);
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
	
	private double[] extractValues(LimitDenotator node) {
		double v1 = 0, v2 = 0;
		try {
			v1 = ((RElement)node.getFactor(0).get(this.coordinatePaths[0]).getElement(new int[]{0}).cast(RRing.ring)).getValue();
			v2 = ((RElement)node.getFactor(0).get(this.coordinatePaths[1]).getElement(new int[]{0}).cast(RRing.ring)).getValue();
		} catch (RubatoException e) { e.printStackTrace(); }
		return new double[] {v1, v2};
	}

}
