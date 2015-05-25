package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ScalingTool extends DisplayTool {

	public final Dimension REFERENCE = new Dimension(100, 100);
	private double[] scalingFactors;
	
	public void setScalingFactors(double[] scalingFactors) {
		this.scalingFactors = scalingFactors;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		double width = this.REFERENCE.getWidth();
		double height = this.REFERENCE.getHeight();
		painter.fillRect(this.startingPoint.getX()-width/2, this.startingPoint.getY()-height/2, width, height);
		if (this.scalingFactors != null) {
			painter.setColor(this.DARK);
			width = this.scalingFactors[0]*this.REFERENCE.getWidth();
			height = this.scalingFactors[1]*this.REFERENCE.getHeight();
			painter.drawRect(this.startingPoint.getX()-width/2, this.startingPoint.getY()-height/2, width, height);
		}
	}

}
