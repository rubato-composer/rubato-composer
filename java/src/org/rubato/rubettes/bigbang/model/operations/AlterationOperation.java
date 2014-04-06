package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class AlterationOperation extends AbstractOperation {
	
	private Set<BigBangObject> foregroundComposition;
	private Set<BigBangObject> backgroundComposition;
	private List<DenotatorPath> alterationCoordinates;
	private double startDegree, endDegree;
	
	public AlterationOperation(BigBangModel model) {
		super(model);
		this.model = model;
		this.foregroundComposition = new TreeSet<BigBangObject>();
		this.backgroundComposition = new TreeSet<BigBangObject>();
		this.alterationCoordinates = new ArrayList<DenotatorPath>();
		this.startDegree = 0;
		this.endDegree = 0;
	}
	
	public void setForegroundComposition(Set<BigBangObject> foregroundComposition) {
		this.foregroundComposition = foregroundComposition;
	}
	
	public void setBackgroundComposition(Set<BigBangObject> backgroundComposition) {
		this.backgroundComposition = backgroundComposition;
	}
	
	public void setAlterationCoordinates(List<DenotatorPath> alterationCoordinates) {
		this.alterationCoordinates = alterationCoordinates;
	}
	
	public Set<BigBangObject> getAlterationComposition(int index) {
		if (index == 0) {
			return this.foregroundComposition;
		}
		return this.backgroundComposition;
	}
	
	public List<DenotatorPath> getAlterationCoordinates() {
		return this.alterationCoordinates;
	}
	
	public void setStartDegree(double startDegree) {
		this.startDegree = startDegree;
	}
	
	public double getStartDegree() {
		return this.startDegree;
	}
	
	public void setEndDegree(double endDegree) {
		this.endDegree = endDegree;
	}
	
	public double getEndDegree() {
		return this.endDegree;
	}
	
	//modified degrees directly calculated in execute...
	protected void updateOperation() { }
	
	@Override
	protected String getSpecificPresentationName() {
		return "Alteration (" + this.startDegree + "," + this.endDegree + ")";
	}

	@Override
	public OperationPathResults execute() {
		double modifiedStartDegree = this.modificationRatio*this.startDegree;
		double modifiedEndDegree = this.modificationRatio*this.endDegree;
		Set<DenotatorPath> foregroundCompositionPaths = this.getObjectPaths(this.foregroundComposition);
		Set<DenotatorPath> backgroundCompositionPaths = this.getObjectPaths(this.backgroundComposition);
		return this.model.getDenotatorManager().addAlteration(foregroundCompositionPaths, backgroundCompositionPaths, this.alterationCoordinates, modifiedStartDegree, modifiedEndDegree);
	}

}
