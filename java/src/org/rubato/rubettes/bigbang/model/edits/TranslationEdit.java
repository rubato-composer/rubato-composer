package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class TranslationEdit extends AbstractTransformationEdit {
	
	private double[] startingPoint;
	private double[] endPoint;
	private double[] shift;
	
	public TranslationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties) {
		super(scoreLayers, properties);
		this.startingPoint = properties.getCenter();
		this.endPoint = properties.getEndPoint();
		this.shift = new double[]{this.endPoint[0]-this.startingPoint[0], this.endPoint[1]-this.startingPoint[1]};
		this.execute();
	}
	
	public void execute() {
		this.translateNotes(this.shift);
	}
	
	public void undo() {
		super.undo();
		double[] invertedShift = new double[]{-1*this.shift[0], -1*this.shift[1]};
		this.translateNotes(invertedShift);
	}
	
	private void translateNotes(double[] shift) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		this.map(identity, shift);
	}
	
	public String getPresentationName() {
		return "Translation";
	}
	
	public double[] getStartingPoint() {
		return this.startingPoint;
	}
	
	public double[] getEndPoint() {
		return this.endPoint;
	}

}
