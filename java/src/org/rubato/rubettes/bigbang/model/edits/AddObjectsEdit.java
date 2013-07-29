package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> powersetPaths;
	private List<List<Map<DenotatorPath,Double>>> pathsWithValues;
	private Form objectForm;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, DenotatorPath powersetPath, Map<DenotatorPath,Double> pathsWithValues) {
		super(scoreManager);
		this.powersetPaths = new ArrayList<DenotatorPath>();
		this.powersetPaths.add(powersetPath);
		this.pathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		List<Map<DenotatorPath,Double>> pathsWithValuesList = new ArrayList<Map<DenotatorPath,Double>>();
		pathsWithValuesList.add(pathsWithValues);
		this.pathsWithValues.add(pathsWithValuesList);
		if (powersetPath != null) {
			this.objectForm = powersetPath.getChildPath(0).getEndForm();
		} else {
			this.objectForm = this.scoreManager.getComposition().getForm();
		}
	}
	
	public Form getObjectForm() {
		return this.objectForm;
	}
	
	public boolean addObject(Map<DenotatorPath,Double> pathsWithValues, DenotatorPath powersetPath) {
		if (powersetPath.getChildPath(0).getEndForm().equals(this.objectForm)) {
			if (this.powersetPaths.get(this.powersetPaths.size()-1).equals(powersetPath)) {
				this.pathsWithValues.get(this.pathsWithValues.size()-1).add(pathsWithValues);
			} else {
				this.powersetPaths.add(powersetPath);
				List<Map<DenotatorPath,Double>> pathsWithValuesList = new ArrayList<Map<DenotatorPath,Double>>();
				pathsWithValuesList.add(pathsWithValues);
				this.pathsWithValues.add(pathsWithValuesList);
			}
			return true;
			//} else {
				//no powerset, so replace object..
				//this.pathsWithValues.set(0, pathsWithValues);
			//}
		}
		return false;
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		for (int i = 0; i < this.powersetPaths.size(); i++) {
			List<DenotatorPath> objectPaths = this.scoreManager.addObjects(this.powersetPaths.get(i), this.pathsWithValues.get(i), fireCompositionChange);
			for (DenotatorPath currentPath : objectPaths) {
				//pathDifferences.get(0).put(null, currentPath);
			}
		}
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
		String presentationName = "Add " + this.objectForm.getNameString();
		if (this.pathsWithValues.size() > 1) {
			presentationName += "s";
		}
		return presentationName;
	}

}
