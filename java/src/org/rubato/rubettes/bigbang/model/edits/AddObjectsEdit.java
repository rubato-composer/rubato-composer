package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	private DenotatorPath powersetPath;
	private List<TreeMap<DenotatorPath,Double>> pathsWithValues;
	private List<DenotatorPath> objectPaths;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, DenotatorPath powersetPath, TreeMap<DenotatorPath,Double> pathsWithValues) {
		super(scoreManager);
		this.powersetPath = powersetPath;
		this.pathsWithValues = new ArrayList<TreeMap<DenotatorPath,Double>>();
		this.pathsWithValues.add(pathsWithValues);
		//this.execute();
	}
	
	public DenotatorPath getPowersetPath() {
		return this.powersetPath;
	}
	
	public void addObject(TreeMap<DenotatorPath,Double> pathsWithValues) {
		if (this.powersetPath != null) {
			this.pathsWithValues.add(pathsWithValues);
		} else {
			//no powerset, so replace object..
			this.pathsWithValues.set(0, pathsWithValues);
		}
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now
	}
	
	public Map<DenotatorPath,DenotatorPath> execute(Map<DenotatorPath,DenotatorPath> pathDifferences, boolean fireCompositionChange) {
		this.objectPaths = this.scoreManager.addObjects(this.powersetPath, this.pathsWithValues, fireCompositionChange);
		//TODO: think about this!!!!!
		//pathDifferences.put(null, this.objectPath);
		return pathDifferences;
	}
	
	/*public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.scoreManager.removeObject(this.objectPath);
	}*/
	
	public String getPresentationName() {
		String presentationName = "Add ";
		if (this.powersetPath == null) {
			presentationName += this.scoreManager.getComposition().getForm().getNameString();
		} else {
			presentationName += this.powersetPath.getForm().getForm(0).getNameString();
		}
		if (this.pathsWithValues.size() > 1) {
			presentationName += "s";
		}
		return presentationName;
	}

}
