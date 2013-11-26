package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> definitePowersetPaths;
	private List<Map<DenotatorPath,Double>> definitePathsWithValues;
	private List<DenotatorPath> previewedPowersetPaths;
	private List<Map<DenotatorPath,Double>> previewedPathsWithValues;
	private List<DenotatorPath> modifiedPowersetPaths;
	private List<List<Map<DenotatorPath,Double>>> modifiedPathsWithValues;
	private Form objectForm;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths) {
		super(scoreManager);
		this.definitePowersetPaths = new ArrayList<DenotatorPath>();
		this.previewedPowersetPaths = new ArrayList<DenotatorPath>();
		this.definitePathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		this.previewedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
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
	 * adds the adjusted the number of objects according to this.modificationRatio
	 */
	protected void updateOperation() {
		this.modifiedPowersetPaths = new ArrayList<DenotatorPath>();
		this.modifiedPathsWithValues = new ArrayList<List<Map<DenotatorPath,Double>>>();
		int totalNumberOfObjects = this.definitePathsWithValues.size() + this.previewedPathsWithValues.size();
		int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*totalNumberOfObjects);
		for (int i = 0; i < modifiedNumberOfObjects; i++) {
			if (i < this.definitePathsWithValues.size()) {
				this.addPathsWithValuesToModifiedList(this.definitePowersetPaths.get(i), this.definitePathsWithValues.get(i));
			} else {
				int index = i-this.definitePathsWithValues.size();
				this.addPathsWithValuesToModifiedList(this.previewedPowersetPaths.get(index), this.previewedPathsWithValues.get(index));
			}
		}
	}

	private void addPathsWithValuesToModifiedList(DenotatorPath powersetPath, Map<DenotatorPath,Double> pathsWithValues) {
		if (!this.modifiedPowersetPaths.contains(powersetPath)) {
			this.modifiedPowersetPaths.add(powersetPath);
			this.modifiedPathsWithValues.add(new ArrayList<Map<DenotatorPath,Double>>());
		}
		this.modifiedPathsWithValues.get(this.modifiedPowersetPaths.indexOf(powersetPath)).add(pathsWithValues);
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
		this.previewedPowersetPaths = new ArrayList<DenotatorPath>();
		this.previewedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		if (powersetPaths.size() > 0 && powersetPaths.get(0) == null) {
			this.definitePowersetPaths = new ArrayList<DenotatorPath>();
			this.definitePathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		}
		if (pathsWithValues.isEmpty() || (powersetPaths.get(0) == null || powersetPaths.get(0).getChildPath(0).getEndForm().equals(this.objectForm))) {
			if (inPreviewMode) {
				this.addObjects(pathsWithValues, powersetPaths, this.previewedPathsWithValues, this.previewedPowersetPaths);
			} else {
				this.addObjects(pathsWithValues, powersetPaths, this.definitePathsWithValues, this.definitePowersetPaths);
			}
			this.updateOperation();
			return true;
		}
		return false;
	}
	
	private void addObjects(List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths, List<Map<DenotatorPath,Double>> thisPathsWithValues, List<DenotatorPath> thisPowersetPaths) {
		for (int i = 0; i < pathsWithValues.size(); i++) {
			thisPowersetPaths.add(powersetPaths.get(i));
			thisPathsWithValues.add(pathsWithValues.get(i));
		}
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now... preview mode works directly with add...
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		if (this.modifiedPowersetPaths.isEmpty() && fireCompositionChange) {
			this.scoreManager.fireCompositionChange();
		}
		for (int i = 0; i < this.modifiedPowersetPaths.size(); i++) {
			//only fire composition change with last powerset
			boolean update = fireCompositionChange && i == this.modifiedPowersetPaths.size()-1;
			List<DenotatorPath> objectPaths = this.scoreManager.addObjects(this.modifiedPowersetPaths.get(i), this.modifiedPathsWithValues.get(i), update);
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
