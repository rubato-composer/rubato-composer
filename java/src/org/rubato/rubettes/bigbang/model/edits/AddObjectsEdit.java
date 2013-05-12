package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectsEdit extends AbstractOperationEdit {
	
	private List<TreeMap<DenotatorPath,Double>> pathsWithValues;
	private DenotatorPath objectPath;
	
	public AddObjectsEdit(BigBangScoreManager scoreManager, TreeMap<DenotatorPath,Double> pathsWithValues) {
		super(scoreManager);
		this.pathsWithValues = new ArrayList<TreeMap<DenotatorPath,Double>>();
		this.pathsWithValues.add(pathsWithValues);
		//this.execute();
	}
	
	public void addObject(TreeMap<DenotatorPath,Double> pathsWithValues) {
		this.pathsWithValues.add(pathsWithValues);
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing for now
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
	}
	
	public Map<DenotatorPath,DenotatorPath> execute(Map<DenotatorPath,DenotatorPath> pathDifferences, boolean sendCompositionChange) {
		for (TreeMap<DenotatorPath,Double> currentValues : this.pathsWithValues) {
			this.objectPath = this.scoreManager.addObject(currentValues);
		}
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
		return "Add Objects";
	}

}
