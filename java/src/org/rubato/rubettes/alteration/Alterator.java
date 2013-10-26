package org.rubato.rubettes.alteration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.morphism.CanonicalMorphism;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.util.DenotatorPath;

public class Alterator {
	
	private final int[][] COORDINATE_PATHS = new int[][]{{0,0},{0,1},{0,2},{0,3},{0,4}};
	private final int[][] ELEMENT_PATHS = new int[][]{{0,0,0},{0,1,0},{0,2,0},{0,3,0},{0,4,0}};
	private final int[][] RELATIVE_TO_PATHS = new int[][]{{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
	
	private NameDenotator emptyName = NameDenotator.make("");
	private JAlterationDimensionsTable dimensionsTable;
	private NearestNeighborFinder neighborFinder;
	private int[][] coordinatePaths, elementPaths, allPaths;
	
	public Alterator() {
	}
	
	public Alterator(JAlterationDimensionsTable dimensionsTable) {
		this.dimensionsTable = dimensionsTable;
	}
	
	public void addNeighbor(Denotator denotator) {
		this.neighborFinder.addNeighbor(denotator);
	}
	
	public void setCoordinates(List<DenotatorPath> selectedCoordinates) {
		this.coordinatePaths = new int[selectedCoordinates.size()][];
		this.elementPaths = new int[selectedCoordinates.size()][];
		this.allPaths = new int[selectedCoordinates.size()][];
		for (int i = 0; i < selectedCoordinates.size(); i++) {
			this.coordinatePaths[i] = selectedCoordinates.get(i).toIntArray();
			this.elementPaths[i] = selectedCoordinates.get(i).getChildPath(0).toIntArray();
			//TODO right now just first selected dimension!!
			this.allPaths[i] = selectedCoordinates.get(0).getChildPath(0).toIntArray();
		}
		this.neighborFinder = new NearestNeighborFinder(this.elementPaths);
	}
	
	/*TODO REMOVE!!! CRAP
	public List<LimitDenotator> getStandardAlteration(List<Denotator> input0, double startDegree, double endDegree) {
		this.neighborFinder.fillKDTree();
		List<LimitDenotator> alteredDenotators = new ArrayList<LimitDenotator>();
		Iterator<Denotator> input0Coordinates = input0.iterator();
		int[][] paths = this.coordinatePaths;
		int[][] differentPaths = new int[][]{{0,0,0}};
		int[][] allPaths = this.RELATIVE_TO_PATHS;
		int[] pathIndices = this.getIndicesOf(allPaths, differentPaths);
		double[] startDegrees = new double[]{startDegree,startDegree,startDegree,startDegree,startDegree};
		double[] endDegrees = new double[]{endDegree,endDegree,endDegree,endDegree,endDegree};
		try {
			double[][] minAndMax = this.getMinAndMaxDouble(input0.iterator(), differentPaths);
			while (input0Coordinates.hasNext()) {
				Denotator currentDenotator = input0Coordinates.next();
				Denotator nearestNeighbour = this.neighborFinder.findNearestNeighbor(currentDenotator);
				Denotator morphedDenotator;
				//need to copy both denotators, since copy makes an address change so sum fails
				morphedDenotator = this.morphDenotator(currentDenotator.copy(), nearestNeighbour.copy(), pathIndices, paths, differentPaths, minAndMax, startDegrees, endDegrees);
				alteredDenotators.add((LimitDenotator)morphedDenotator);
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return alteredDenotators;
	}*/
	
	//TODO REMOVE!!! CRAP-... BUT GENERALIZE HIERARCHICAL ALTERATION!!!!
	public List<Denotator> getSoundScoreAlteration(List<Denotator> input0, double startDegree, double endDegree, boolean onlyModulators) {
		this.neighborFinder.fillKDTree();
		List<Denotator> alteredDenotators = new ArrayList<Denotator>();
		Iterator<Denotator> input0Coordinates = input0.iterator();
		int[][] paths = this.coordinatePaths;
		//TODO make controllable! (direction along which start/end degrees work)
		int[][] differentPaths = new int[][]{this.allPaths[0]};
		int[][] allPaths = this.allPaths;
		int[] pathIndices = this.getIndicesOf(allPaths, differentPaths);
		double[] startDegrees = new double[]{startDegree,startDegree,startDegree,startDegree,startDegree};
		double[] endDegrees = new double[]{endDegree,endDegree,endDegree,endDegree,endDegree};
		try {
			double[][] minAndMax = this.getMinAndMaxDouble(input0.iterator(), differentPaths);
			while (input0Coordinates.hasNext()) {
				Denotator currentDenotator = input0Coordinates.next();
				Denotator nearestNeighbour = this.neighborFinder.findNearestNeighbor(currentDenotator);
				Denotator morphedDenotator;
				//need to copy both denotators, since copy makes an address change so sum fails
				morphedDenotator = this.morphSoundDenotator(currentDenotator.copy(), nearestNeighbour.copy(), pathIndices, paths, differentPaths, minAndMax, startDegrees, endDegrees, onlyModulators);
				alteredDenotators.add(morphedDenotator);
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return alteredDenotators;
	}
	
	public List<Denotator> getBigBangAlteration(List<Denotator> input0, double startDegree, double endDegree) {
		this.neighborFinder.fillKDTree();
		List<Denotator> alteredDenotators = new ArrayList<Denotator>();
		Iterator<Denotator> input0Coordinates = input0.iterator();
		int[][] paths = this.coordinatePaths;
		//TODO make controllable at some point! (direction along which start/end degrees work)
		int[][] differentPaths = new int[][]{this.allPaths[0]};
		int[][] allPaths = this.allPaths;
		int[] pathIndices = this.getIndicesOf(allPaths, differentPaths);
		double[] startDegrees = new double[paths.length];
		double[] endDegrees = new double[paths.length];
		for (int i = 0; i < paths.length; i++) {
			startDegrees[i] = startDegree;
			endDegrees[i] = endDegree;
		}
		try {
			double[][] minAndMax = this.getMinAndMaxDouble(input0.iterator(), differentPaths);
			while (input0Coordinates.hasNext()) {
				Denotator currentDenotator = input0Coordinates.next();
				Denotator nearestNeighbour = this.neighborFinder.findNearestNeighbor(currentDenotator);
				Denotator morphedDenotator;
				//need to copy both denotators, since copy makes an address change so sum fails
				morphedDenotator = this.morphDenotator(currentDenotator.copy(), nearestNeighbour.copy(), pathIndices, paths, differentPaths, minAndMax, startDegrees, endDegrees);
				alteredDenotators.add(morphedDenotator);
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return alteredDenotators;
	}
	
	public PowerDenotator getAlteration(PowerDenotator input0, PowerDenotator input1) throws RubatoException {
		List<Denotator> morphedFactors = new ArrayList<Denotator>();
		Iterator<Denotator> input0Coordinates = input0.iterator();
		int[][] paths = this.dimensionsTable.getPaths();
		int[][] differentPaths = this.dimensionsTable.getDifferentRelativeToFormPaths();
		int[][] allPaths = this.dimensionsTable.getRelativeToFormPaths();
		int[] pathIndices = this.getIndicesOf(allPaths, differentPaths);
		double[] startDegrees = this.dimensionsTable.getStartDegrees();
		double[] endDegrees = this.dimensionsTable.getEndDegrees();
		double[][] minAndMax = this.getMinAndMaxDouble(input0.iterator(), differentPaths);
		this.neighborFinder = new NearestNeighborFinder(input1, this.dimensionsTable.getElementPaths());
		while (input0Coordinates.hasNext()) {
			Denotator currentDenotator = input0Coordinates.next();
			Denotator nearestNeighbour = this.neighborFinder.findNearestNeighbor(currentDenotator);
			Denotator morphedDenotator;
			//need to copy both denotators, since copy makes an address change so sum fails
			morphedDenotator = this.morphDenotator(currentDenotator.copy(), nearestNeighbour.copy(), pathIndices, paths, differentPaths, minAndMax, startDegrees, endDegrees);
			morphedFactors.add(morphedDenotator);
		}
		return new PowerDenotator(this.emptyName, input0.getPowerForm(), morphedFactors);
	}
	
	protected int[] getIndicesOf(int[][] array1, int[][] array2) {
		int[] indices = new int[array1.length];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = -1;
			for (int j = 0; j < array2.length; j++) {
				if (Arrays.equals(array2[j], array1[i])) {
					indices[i] = j;
				}
			}
		}
		return indices;
	}
	
	protected double[][] getMinAndMaxDouble(Iterator<Denotator> denotators, int[][] paths) throws RubatoException {
		double[] minima = new double[paths.length];
		double[] maxima = new double[paths.length];
		//set starting values
		Denotator currentDenotator = denotators.next();
		for (int i = 0; i < paths.length; i++) {
			double currentDouble = ((RElement)currentDenotator.getElement(paths[i]).cast(RRing.ring)).getValue();
			minima[i] = currentDouble;
			maxima[i] = currentDouble;
		}
		//calculate mins and maxes
		while (denotators.hasNext()) {
			currentDenotator = denotators.next();
			for (int i = 0; i < paths.length; i++) {
				double currentDouble = ((RElement)currentDenotator.getElement(paths[i]).cast(RRing.ring)).getValue();
				if (currentDouble > maxima[i]) {
					maxima[i] = currentDouble;
				}
				if (currentDouble < minima[i]) {
					minima[i] = currentDouble;
				}
			}
		}
		return new double[][]{minima, maxima};
	}
	
	protected Denotator morphDenotator(Denotator morphed, Denotator pole, int[] indices, int[][] allPaths, int[][] differentPaths, double[][] minAndMax, double[] startDegrees, double[] endDegrees) throws RubatoException {
		double[] doubleValues = this.getCastDoubleValues(morphed, differentPaths);
		double[] degrees = this.getInterpolatedDegrees(startDegrees, endDegrees, doubleValues, minAndMax, indices);
		
		return this.alterDenotator(morphed, pole, allPaths, degrees);
	}
	
	protected Denotator morphSoundDenotator(Denotator morphed, Denotator pole, int[] indices, int[][] allPaths, int[][] differentPaths, double[][] minAndMax, double[] startDegrees, double[] endDegrees, boolean onlyModulators) throws RubatoException {
		double[] doubleValues = this.getCastDoubleValues(morphed, differentPaths);
		double[] degrees = this.getInterpolatedDegrees(startDegrees, endDegrees, doubleValues, minAndMax, indices);
		
		Denotator morphedDenotator;
		if (onlyModulators) {
			morphedDenotator = morphed.copy();
		} else {
			morphedDenotator = this.alterDenotator(morphed, pole, allPaths, degrees);
		}
		
		int[][] shortPaths = new int[allPaths.length][allPaths[0].length-1];
		for (int i = 0; i < allPaths.length; i++) {
			System.arraycopy(allPaths[i], 1, shortPaths[i], 0, allPaths[i].length-1);
		}
		
		this.alterModulators(morphedDenotator.get(new int[]{0}), pole.get(new int[]{0}), new DenotatorPath(Repository.systemRepository().getForm("SoundScore")), shortPaths, degrees);
		
		
		
		return morphedDenotator;
	}
	
	private void alterModulators(Denotator note, Denotator pole, DenotatorPath currentPath, int[][] paths, double[] degrees) throws RubatoException {
		DenotatorPath currentModulatorsPath = currentPath.getPowersetPath(1);
		PowerDenotator modulators = (PowerDenotator)note.get(currentModulatorsPath.toIntArray());
		PowerDenotator poleModulators = (PowerDenotator)pole.get(currentModulatorsPath.toIntArray());
		
		int minFactors = Math.min(modulators.getFactorCount(), poleModulators.getFactorCount());
		if (minFactors > 0) {
			for (int i = 0; i < minFactors; i++) {
				DenotatorPath currentSubPath = currentPath.getSatellitePath(i, 1);
				Denotator currentModulator = note.get(currentSubPath.toIntArray());
				Denotator currentPole = pole.get(currentSubPath.toIntArray());
				
				Denotator currentMorphedModulator = this.alterDenotator(currentModulator, currentPole, paths, degrees);
				modulators.setFactor(i, currentMorphedModulator);
				
				this.alterModulators(note, pole, currentSubPath, paths, degrees);
			}
		}
	}
	
	private double[] getCastDoubleValues(Denotator denotator, int[][] paths) throws RubatoException{
		double[] values = new double[paths.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = ((RElement)denotator.getElement(paths[i]).cast(RRing.ring)).getValue();
		}
		return values;
	}
	
	protected double[] getInterpolatedDegrees(double[] startDegrees, double[] endDegrees, double[] values, double[][] minAndMax, int[] indices) {
		double[] interpolated = new double[startDegrees.length];
		for (int i = 0; i < startDegrees.length; i++) {
			int j = indices[i];
			interpolated[i] = this.getInterpolatedDegree(startDegrees[i], endDegrees[i], values[j], minAndMax[0][j], minAndMax[1][j]);
		}
		return interpolated;
	}
	
	protected double getInterpolatedDegree(double startDegree, double endDegree, double value, double min, double max) {
		double relativeValue = (value-min)/(max-min);
		return relativeValue*(endDegree-startDegree)+startDegree;
	}
	
	
	
	public Denotator alter(Denotator d1, Denotator d2, double degree, int[][] paths) throws RubatoException {
		for (int i = 0; i < paths.length; i++) {
			int[] currentPath = paths[i];
			d1 = this.replaceSimpleDenotatorWithAltered(d1, d2, currentPath, degree);
		}
		return d1;
	}
	
	protected Denotator alterDenotator(Denotator d1, Denotator d2, int[][] paths, double[] degrees) throws RubatoException {
		for (int i = 0; i < paths.length; i++) {
			int[] currentPath = paths[i];
			d1 = this.replaceSimpleDenotatorWithAltered(d1, d2, currentPath, degrees[i]);
		}
		return d1;
	}
	
	protected Denotator replaceSimpleDenotatorWithAltered(Denotator d1, Denotator d2, int[] path, double degree) throws RubatoException {
		SimpleDenotator simple1 = (SimpleDenotator) d1.get(path);
		ModuleMorphism morphism0 = simple1.getModuleMorphism();
		ModuleMorphism morphism1 = ((SimpleDenotator) d2.get(path)).getModuleMorphism();
		ModuleMorphism newMorphism = this.makeAlteredMorphism(morphism0, morphism1, degree);
		
		
		//System.out.println(morphism1);
		SimpleForm form = (SimpleForm)simple1.getForm();
		Denotator newSimple = new SimpleDenotator(NameDenotator.make(""), form, newMorphism);
		return d1.replace(path, newSimple);
	}
	
	protected ModuleMorphism makeAlteredMorphism(ModuleMorphism m0, ModuleMorphism m1, double percentage) {
		if (percentage == 0) {
			return m0;
		} else if (percentage == 1) {
			return m1;
		} else {
			try {
				Module module = m0.getCodomain();
				m0 = this.getCastedMorphism(m0, RRing.ring);
				m1 = this.getCastedMorphism(m1, RRing.ring);
				ModuleMorphism scaled0 = m0.scaled(new RElement(1-percentage));
				ModuleMorphism scaled1 = m1.scaled(new RElement(percentage));
				ModuleMorphism result = scaled0.sum(scaled1);
				result = this.getCastedMorphism(result, module);
				return result;
			} catch (CompositionException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/*
	 * composes the input morphism with a casting morphism for the specified codomain.
	 * example: m:Q->Q, c:R, then return m:Q->R 
	 */
	private ModuleMorphism getCastedMorphism(ModuleMorphism morphism, Module newCodomain) throws CompositionException {
		Module oldCodomain = morphism.getCodomain();
		if (!newCodomain.equals(oldCodomain)) {
			ModuleMorphism castingMorphism = CanonicalMorphism.make(oldCodomain, newCodomain);
			morphism = castingMorphism.compose(morphism);
		}
		return morphism;
	}

}
