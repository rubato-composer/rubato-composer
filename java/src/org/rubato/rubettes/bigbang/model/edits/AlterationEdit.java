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
	private List<Integer> alterationCoordinates;
	private double startDegree, endDegree;
	
	public AlterationEdit(BigBangScoreManager manager) {
		super(manager);
		this.scoreManager = manager;
		this.foregroundComposition = new ArrayList<DenotatorPath>();
		this.backgroundComposition = new ArrayList<DenotatorPath>();
		this.alterationCoordinates = new ArrayList<Integer>();
		this.startDegree = 0;
		this.endDegree = 0;
	}
	
	public void setForegroundComposition(List<DenotatorPath> foregroundComposition) {
		this.foregroundComposition = foregroundComposition;
	}
	
	public void setBackgroundComposition(List<DenotatorPath> backgroundComposition) {
		this.backgroundComposition = backgroundComposition;
	}
	
	public void setAlterationCoordinates(List<Integer> alterationCoordinates) {
		this.alterationCoordinates = alterationCoordinates;
	}
	
	public List<Integer> getAlterationCoordinates() {
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
	
	//not changed by modification!!
	protected void updateOperation() { }
	
	public String getPresentationName() {
		return "Alteration (" + this.startDegree + "," + this.endDegree + ")";
	}

	@Override
	public List<Map<DenotatorPath, DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean sendCompositionChange) {
		//TODO could it be possible to have different paths for each dimension??? or even transformation??
		this.scoreManager.addAlteration(this.foregroundComposition, this.backgroundComposition, this.alterationCoordinates, this.startDegree, this.endDegree, sendCompositionChange);
		return pathDifferences;
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing
	}

}
