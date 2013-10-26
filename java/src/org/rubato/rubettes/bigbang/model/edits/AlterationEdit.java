package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AlterationEdit extends AbstractOperationEdit {
	
	private BigBangScoreManager scoreManager;
	private List<DenotatorPath> foregroundComposition;
	private List<DenotatorPath> backgroundComposition;
	private List<DenotatorPath> alterationCoordinates;
	private double startDegree, endDegree;
	
	public AlterationEdit(BigBangScoreManager manager) {
		super(manager);
		this.scoreManager = manager;
		this.foregroundComposition = new ArrayList<DenotatorPath>();
		this.backgroundComposition = new ArrayList<DenotatorPath>();
		this.alterationCoordinates = new ArrayList<DenotatorPath>();
		this.startDegree = 0;
		this.endDegree = 0;
	}
	
	public void fireAlterationComposition(int index) {
		List<DenotatorPath> composition = this.foregroundComposition;
		if (index == 1) {
			composition = this.backgroundComposition;
		}
		this.scoreManager.fireAlterationComposition(index, composition);
	}
	
	public void setForegroundComposition(List<DenotatorPath> foregroundComposition) {
		this.foregroundComposition = foregroundComposition;
	}
	
	public void setBackgroundComposition(List<DenotatorPath> backgroundComposition) {
		this.backgroundComposition = backgroundComposition;
	}
	
	public void setAlterationCoordinates(List<DenotatorPath> alterationCoordinates) {
		this.alterationCoordinates = alterationCoordinates;
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
	public List<Map<DenotatorPath, DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean sendCompositionChange) {
		double modifiedStartDegree = this.modificationRatio*this.startDegree;
		double modifiedEndDegree = this.modificationRatio*this.endDegree;
		this.scoreManager.addAlteration(this.foregroundComposition, this.backgroundComposition, this.alterationCoordinates, modifiedStartDegree, modifiedEndDegree, sendCompositionChange);
		return pathDifferences;
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing
	}

}
