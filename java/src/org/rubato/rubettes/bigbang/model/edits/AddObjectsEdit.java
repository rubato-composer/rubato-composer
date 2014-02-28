package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	//TODO these paths will be replaced by BigBangObjects and powerset indices
	private List<DenotatorPath> definitePowersetPaths;
	private List<Map<DenotatorPath,Double>> definitePathsWithValues;
	private List<DenotatorPath> previewedPowersetPaths;
	private List<Map<DenotatorPath,Double>> previewedPathsWithValues;
	private List<DenotatorPath> modifiedPowersetPaths;
	private List<List<Map<DenotatorPath,Double>>> modifiedPathsWithValues;
	private Form objectForm;
	
	public AddObjectsEdit(BigBangModel model, List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths) {
		this(model, pathsWithValues, powersetPaths, false);
	}
	
	public AddObjectsEdit(BigBangModel model, List<Map<DenotatorPath,Double>> pathsWithValues, List<DenotatorPath> powersetPaths, boolean inPreviewMode) {
		super(model);
		this.definitePowersetPaths = new ArrayList<DenotatorPath>();
		this.previewedPowersetPaths = new ArrayList<DenotatorPath>();
		this.definitePathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		this.previewedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		if (powersetPaths != null && !powersetPaths.isEmpty()) {
			this.setObjectForm(powersetPaths.get(0));
			this.addObjects(pathsWithValues, powersetPaths, inPreviewMode);
		}
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.isSplittable = true;
		this.updateOperation();
	}
	
	private void setObjectForm(DenotatorPath powersetPath) {
		if (powersetPath != null) {
			this.objectForm = powersetPath.getChildPath(0).getEndForm();
		} else {
			this.objectForm = this.model.getComposition().getForm();
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
	
	public List<AbstractOperationEdit> getSplitOperations(double ratio) {
		List<AbstractOperationEdit> splitOperations = new ArrayList<AbstractOperationEdit>();
		int firstNumberOfObjects = (int)Math.round(ratio*this.definitePathsWithValues.size());
		splitOperations.add(new AddObjectsEdit(this.model,
				this.definitePathsWithValues.subList(0, firstNumberOfObjects),
				this.definitePowersetPaths.subList(0, firstNumberOfObjects)));
		splitOperations.add(new AddObjectsEdit(this.model,
				this.definitePathsWithValues.subList(firstNumberOfObjects, this.definitePathsWithValues.size()),
				this.definitePowersetPaths.subList(firstNumberOfObjects, this.definitePathsWithValues.size())));
		return splitOperations;
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
		//reset previewed objects and even remove the definite object if the new object is on the topmost level
		this.previewedPowersetPaths = new ArrayList<DenotatorPath>();
		this.previewedPathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		if (powersetPaths.size() > 0 && powersetPaths.get(0) == null) {
			this.definitePowersetPaths = new ArrayList<DenotatorPath>();
			this.definitePathsWithValues = new ArrayList<Map<DenotatorPath,Double>>();
		}
		//if on top level or same form, add objects
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
	
	public OperationPathResults execute() {
		OperationPathResults pathResults = new OperationPathResults();
		for (int i = 0; i < this.modifiedPowersetPaths.size(); i++) {
			pathResults.addPaths(this.model.getDenotatorManager().addObjects(this.modifiedPowersetPaths.get(i), this.modifiedPathsWithValues.get(i)));
		}
		return pathResults;
	}
	
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
