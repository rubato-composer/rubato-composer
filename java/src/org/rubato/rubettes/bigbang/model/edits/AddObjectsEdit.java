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
	private List<List<Map<DenotatorPath,Double>>> modifiedPathsWithValues;
	private Form objectForm;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, DenotatorPath powersetPath, Map<DenotatorPath,Double> pathsWithValues) {
		super(scoreManager);
		this.powersetPaths = new ArrayList<DenotatorPath>();
		this.pathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		this.setObjectForm(powersetPath);
		this.addObject(pathsWithValues, powersetPath);
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.updateOperation();
	}
	
	private void setObjectForm(DenotatorPath powersetPath) {
		if (powersetPath != null) {
			this.objectForm = powersetPath.getChildPath(0).getEndForm();
		} else {
			this.objectForm = this.scoreManager.getComposition().getForm();
		}
	}
	
	//adjusts the number of objects to be added according to this.modificationRatio
	protected void updateOperation() {
		this.modifiedPathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		for (List<Map<DenotatorPath,Double>> currentPowersetPathsWithValues : this.pathsWithValues) {
			int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*currentPowersetPathsWithValues.size());
			List<Map<DenotatorPath,Double>> currentModifiedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
			//add only as many of the objects as are needed
			for (int i = 0; i < modifiedNumberOfObjects; i++) {
				currentModifiedPathsWithValues.add(currentPowersetPathsWithValues.get(i));
			}
			this.modifiedPathsWithValues.add(currentModifiedPathsWithValues);
		}
	}
	
	/*would have been cool, but can be done later...
	private List<Map<DenotatorPath,Double>> lagrangePredictValues(List<Map<DenotatorPath,Double>> pathsWithValues, int numberOfValues) {
		List<Map<DenotatorPath,Double>> predictedValues = new ArrayList<Map<DenotatorPath,Double>>();
		//add necessary maps
		for (int i = 0; i < numberOfValues; i++) {
			predictedValues.add(new TreeMap<DenotatorPath,Double>());
		}
		//fill with predicted values
		for (DenotatorPath currentPath : pathsWithValues.get(0).keySet()) {
			List<Double> currentValues = new ArrayList<Double>();
			for (Map<DenotatorPath,Double> currentPathsWithValues : pathsWithValues) {
				currentValues.add(currentPathsWithValues.get(currentPath));
			}
			List<Double> currentPredictedValues = GeometryTools.lagrangePredictValues(currentValues, numberOfValues);
			//add values to maps
			for (int i = 0; i < currentPredictedValues.size(); i++) {
				predictedValues.get(i).put(currentPath, currentPredictedValues.get(i));
			}
		}
		return predictedValues;
	}*/
	
	public Form getObjectForm() {
		return this.objectForm;
	}
	
	
	public boolean addObject(Map<DenotatorPath,Double> pathsWithValues, DenotatorPath powersetPath) {
		if (powersetPath == null || powersetPath.getChildPath(0).getEndForm().equals(this.objectForm)) {
			if (this.powersetPaths.size() > 0
					&& (this.powersetPaths.get(this.powersetPaths.size()-1) == null
							|| this.powersetPaths.get(this.powersetPaths.size()-1).equals(powersetPath))) {
				this.pathsWithValues.get(this.pathsWithValues.size()-1).add(pathsWithValues);
			} else {
				this.powersetPaths.add(powersetPath);
				List<Map<DenotatorPath,Double>> pathsWithValuesList = new ArrayList<Map<DenotatorPath,Double>>();
				pathsWithValuesList.add(pathsWithValues);
				this.pathsWithValues.add(pathsWithValuesList);
			}
			this.updateOperation();
			return true;
		}
		return false;
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		for (int i = 0; i < this.powersetPaths.size(); i++) {
			List<DenotatorPath> objectPaths = this.scoreManager.addObjects(this.powersetPaths.get(i), this.modifiedPathsWithValues.get(i), fireCompositionChange);
			/*for (DenotatorPath currentPath : objectPaths) {
				//pathDifferences.get(0).put(null, currentPath);
			}*/
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
