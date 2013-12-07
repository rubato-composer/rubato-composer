package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.alteration.Alterator;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangAlteration {
	
	private List<Set<DenotatorPath>> compositions;
	private List<DenotatorPath> alterationCoordinates;
	private double startDegree, endDegree;
	private Alterator alterator;
	
	public BigBangAlteration() {
		this.reset();
		this.alterator = new Alterator();
	}
	
	public void reset() {
		this.compositions = new ArrayList<Set<DenotatorPath>>();
		this.compositions.add(new TreeSet<DenotatorPath>());
		this.compositions.add(new TreeSet<DenotatorPath>());
		this.alterationCoordinates = new ArrayList<DenotatorPath>();
	}
	
	public Set<DenotatorPath> getComposition(int index) {
		try {
			return this.compositions.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return new TreeSet<DenotatorPath>();
		}
	}
	
	public void setAlterationComposition(Set<DenotatorPath> nodePaths, Integer index) {
		this.compositions.set(index, nodePaths);
		this.resetDegrees();
	}
	
	public void setAlterationCoordinates(List<DenotatorPath> selectedCoordinates) {
		this.resetDegrees();
		this.alterationCoordinates = selectedCoordinates;
		//this.firePropertyChange(BigBangController.ALTERATION_COORDINATES, null, this.selectedCoordinates);
	}
	
	public void resetDegrees() {
		this.setStartDegree(new Double(0));
		this.setEndDegree(new Double(0));
	}
	
	public void setStartDegree(double value) {
		this.startDegree = value;
		/*this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, -1);
		this.firePropertyChange(BigBangController.ALTERATION_START_DEGREE, null, value);*/
	}
	
	public void setEndDegree(double value) {
		this.endDegree = value;
		/*this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, -1);
		this.firePropertyChange(BigBangController.ALTERATION_END_DEGREE, null, value);*/
	}
	
	public void alter(BigBangComposition score) {
		if (this.compositions.get(0).size()>0 && this.compositions.get(1).size()>0 && this.alterationCoordinates.size() > 0) {
			/*boolean onlyModulators = this.selectedCoordinates.contains(5);
			List<Integer> coordinates = this.selectedCoordinates;
			if (onlyModulators) {
				coordinates = this.selectedCoordinates.subList(0, this.selectedCoordinates.size()-1);
			}*/
			//System.out.println(onlyModulators);
			this.alterator.setCoordinates(this.alterationCoordinates);
			List<DenotatorPath> c1 = new ArrayList<DenotatorPath>(this.compositions.get(1));
			List<Denotator> composition1 = score.getAbsoluteNodes(c1);
			if (composition1.size() > 0) {
				for (Denotator currentNeighbor: composition1) {
					this.alterator.addNeighbor(currentNeighbor);
				}
				List<DenotatorPath> anchorPaths = DenotatorPath.getAnchorPaths(new ArrayList<DenotatorPath>(this.compositions.get(0)));
				List<Denotator> composition0 = this.toDenotatorList(score.removeObjects(new ArrayList<DenotatorPath>(this.compositions.get(0))));
				if (composition0.size() > 0) {
					List<Denotator> alteredNodes = this.alterator.getBigBangAlteration(composition0, this.startDegree, this.endDegree);
					score.addObjects(alteredNodes, anchorPaths);
				} else {
					score.addObjects(composition0, anchorPaths);
				}
			}
		}
	}
	
	private List<Denotator> toDenotatorList(List<Denotator> limitDenotators) {
		List<Denotator> denotators = new ArrayList<Denotator>();
		for (Denotator currentDenotator: limitDenotators) {
			denotators.add(currentDenotator);
		}
		return denotators;
	}
	
	/*public void fireState() {
		this.fireActivity();
	}
	
	private void fireActivity() {
		if (this.active) {
			this.firePropertyChange(BigBangController.ENTER_ALTERATION_MODE, null, null);
		} else {
			this.firePropertyChange(BigBangController.EXIT_ALTERATION_MODE, null, null);
		}
	}*/

}
