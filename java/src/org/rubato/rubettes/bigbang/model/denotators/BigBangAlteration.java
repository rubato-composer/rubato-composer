package org.rubato.rubettes.bigbang.model.denotators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.alteration.Alterator;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangAlteration {
	
	private List<DenotatorPath> composition0;
	private List<DenotatorPath> composition1;
	private List<DenotatorPath> alterationCoordinates;
	private double startDegree, endDegree;
	private DenotatorPath degreesDimensionPath;
	private Alterator alterator;
	
	public BigBangAlteration() {
		this.reset();
		this.alterator = new Alterator();
	}
	
	public void reset() {
		this.composition0 = new ArrayList<DenotatorPath>();
		this.composition1 = new ArrayList<DenotatorPath>();
		this.alterationCoordinates = new ArrayList<DenotatorPath>();
	}
	
	public Set<DenotatorPath> getComposition(int index) {
		if (index == 0) {
			return new TreeSet<DenotatorPath>(this.composition0);
		}
		return new TreeSet<DenotatorPath>(this.composition1);
	}
	
	public void setAlterationComposition(Set<DenotatorPath> nodePaths, Integer index) {
		if (index == 0) {
			this.composition0 = new ArrayList<DenotatorPath>(nodePaths);
		} else {
			this.composition1 = new ArrayList<DenotatorPath>(nodePaths);
		}
		this.resetDegrees();
	}
	
	public void setAlterationCoordinates(List<DenotatorPath> selectedCoordinates) {
		this.resetDegrees();
		this.alterationCoordinates = selectedCoordinates;
		//this.firePropertyChange(BigBangController.ALTERATION_COORDINATES, null, this.selectedCoordinates);
	}
	
	public void setDegreesDimensionPath(DenotatorPath dimensionPath) {
		this.degreesDimensionPath = dimensionPath;
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
	
	public void alter(BigBangDenotatorManager denotatorManager) {
		if (this.composition0.size() > 0 && this.composition1.size() > 0 && this.alterationCoordinates.size() > 0) {
			this.alterator.setCoordinates(this.alterationCoordinates, this.degreesDimensionPath.getChildPath(0).toIntArray());
			List<Denotator> composition0 = denotatorManager.getAbsoluteObjects(this.composition0);
			List<Denotator> composition1 = denotatorManager.getAbsoluteObjects(this.composition1);
			if (composition1.size() > 0) {
				for (Denotator currentNeighbor: composition1) {
					this.alterator.addNeighbor(currentNeighbor);
				}
				List<Denotator> alteredObjects = this.alterator.getBigBangAlteration(composition0, this.startDegree, this.endDegree, this.degreesDimensionPath.getChildPath(0).toIntArray());
				denotatorManager.replaceObjects(alteredObjects, this.composition0);
			}
		}
	}

}
