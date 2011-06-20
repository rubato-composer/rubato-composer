package org.rubato.rubettes.bigbang.model.edits;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.NotePath;

public class ShapingEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private TransformationProperties properties;
	private TreeMap<Double,Double> shapingLocations;
	private Map<NotePath,Double> newPathsAndOldYValues;
	
	public ShapingEdit(BigBangScoreManager score, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.score = score;
		this.properties = properties;
		this.shapingLocations = shapingLocations;
		this.execute();
	}

	public void execute() {
		this.newPathsAndOldYValues = this.score.shapeNotes(this.properties, this.shapingLocations);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		if (this.properties.copyAndTransform()) {
			this.score.removeNotes(this.newPathsAndOldYValues.keySet());
		} else {
			this.score.undoShapeNotes(this.newPathsAndOldYValues);
		}
	}
	
	public String getPresentationName() {
		return "Shaping";
	}
	
}
