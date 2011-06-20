package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class TranslationEdit extends AbstractTransformationEdit {
	
	private double[] shift;
	
	public TranslationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] shift) {
		super(scoreLayers, properties);
		this.shift = shift;
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

}
