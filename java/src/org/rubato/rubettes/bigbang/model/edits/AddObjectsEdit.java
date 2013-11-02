package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> powersetPaths;
	private List<List<Map<DenotatorPath,Double>>> definitePathsWithValues;
	private List<List<Map<DenotatorPath,Double>>> previewedPathsWithValues;
	private List<List<Map<DenotatorPath,Double>>> modifiedPathsWithValues;
	private Form objectForm;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths) {
		super(scoreManager);
		this.powersetPaths = new ArrayList<DenotatorPath>();
		this.definitePathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		this.previewedPathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		if (!powersetPaths.isEmpty()) {
			this.setObjectForm(powersetPaths.get(0));
		}
		this.addObjects(pathsWithValues, powersetPaths, false);
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
	
	/*
	 * adds the adjusted the number of objects according to this.modificationRatio (for now for each powerset
	 * independently)
	 */
	protected void updateOperation() {
		this.modifiedPathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		for (int i = 0; i < this.powersetPaths.size(); i++) {
			List<Map<DenotatorPath,Double>> currentDefinitePathsWithValues = this.definitePathsWithValues.get(i);
			List<Map<DenotatorPath,Double>> currentPreviewedPathsWithValues = this.previewedPathsWithValues.get(i);
			int totalNumberOfObjects = currentDefinitePathsWithValues.size() + currentPreviewedPathsWithValues.size();
			int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*totalNumberOfObjects);
			List<Map<DenotatorPath,Double>> currentModifiedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
			//add only as many of the objects as are needed
			for (int j = 0; j < modifiedNumberOfObjects; j++) {
				if (j < currentDefinitePathsWithValues.size()) {
					currentModifiedPathsWithValues.add(currentDefinitePathsWithValues.get(j));
				} else {
					currentModifiedPathsWithValues.add(currentPreviewedPathsWithValues.get(j-currentDefinitePathsWithValues.size()));
				}
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
	
	public boolean addObjects(List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths, boolean inPreviewMode) {
		if (this.objectForm == null && !powersetPaths.isEmpty()) {
			this.setObjectForm(powersetPaths.get(0));
		}
		//reset previewed objects and remove the previous object if the new object is on the topmost level
		this.previewedPathsWithValues = this.getNewPathsWithValuesList();
		if (powersetPaths.size() > 0 && powersetPaths.get(0) == null) {
			this.definitePathsWithValues = this.getNewPathsWithValuesList();
		}
		if (pathsWithValues.isEmpty() || (powersetPaths.get(0) == null || powersetPaths.get(0).getChildPath(0).getEndForm().equals(this.objectForm))) {
			if (inPreviewMode) {
				this.addObjects(pathsWithValues, powersetPaths, this.previewedPathsWithValues);
			} else {
				this.addObjects(pathsWithValues, powersetPaths, this.definitePathsWithValues);
			}
			this.updateOperation();
			return true;
		}
		return false;
	}
	
	private void addObjects(List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths, List<List<Map<DenotatorPath,Double>>> thisPathsWithValues) {
		for (int i = 0; i < pathsWithValues.size(); i++) {
			DenotatorPath currentPowersetPath = powersetPaths.get(i);
			Map<DenotatorPath,Double> currentPathsWithValues = pathsWithValues.get(i);
			if (!this.powersetPaths.contains(currentPowersetPath)) {
				this.addNewPowersetPathAndAdjustPathsWithValues(currentPowersetPath);
			}
			thisPathsWithValues.get(this.powersetPaths.indexOf(currentPowersetPath)).add(currentPathsWithValues);
		}
	}
	
	/*
	 * generates a new pathsWithValues list to include the same amount of empty lists as this.powersetPaths
	 */
	private List<List<Map<DenotatorPath,Double>>> getNewPathsWithValuesList() {
		List<List<Map<DenotatorPath,Double>>> pathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		while (pathsWithValues.size() < this.powersetPaths.size()) {
			pathsWithValues.add(new ArrayList<Map<DenotatorPath,Double>>());
		}
		return pathsWithValues;
	}
	
	private void addNewPowersetPathAndAdjustPathsWithValues(DenotatorPath powersetPath) {
		this.powersetPaths.add(powersetPath);
		this.definitePathsWithValues.add(new ArrayList<Map<DenotatorPath,Double>>());
		this.previewedPathsWithValues.add(new ArrayList<Map<DenotatorPath,Double>>());
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now... preview mode works directly with add...
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		if (this.powersetPaths.isEmpty() && fireCompositionChange) {
			this.scoreManager.fireCompositionChange();
		}
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
	
	@Override
	protected String getSpecificPresentationName() {
		if (this.objectForm != null) {
			String presentationName = "Add " + this.objectForm.getNameString();
			if (this.previewedPathsWithValues.size() > 1) {
				presentationName += "s";
			}
			return presentationName;
		}
		return "Add";
	}

}
