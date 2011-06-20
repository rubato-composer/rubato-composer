package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class RotationEdit extends AbstractLocalTransformationEdit {
	
	private double angle;
	
	public RotationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double angle) {
		super(scoreLayers, properties);
		this.angle = angle;
		this.execute();
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.angle);
		double cos = Math.cos(this.angle);
		return new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
	}
	
	public String getPresentationName() {
		return "Rotation";
	}

}
