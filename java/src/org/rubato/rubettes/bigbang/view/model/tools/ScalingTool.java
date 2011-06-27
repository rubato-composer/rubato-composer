package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ScalingTool extends DisplayTool {

	public final Dimension REFERENCE = new Dimension(100, 100);
	private double[] scalingFactors;
	
	public ScalingTool(Point2D.Double startingPoint) {
		super(startingPoint);
	}
	
	public void setScalingFactors(double[] scalingFactors) {
		this.scalingFactors = scalingFactors;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		double width = this.REFERENCE.getWidth();
		double height = this.REFERENCE.getHeight();
		painter.fillRect(this.startingPoint.x-width/2, this.startingPoint.y-height/2, width, height);
		if (this.scalingFactors != null) {
			painter.setColor(this.DARK);
			width = this.scalingFactors[0]*this.REFERENCE.getWidth();
			height = this.scalingFactors[1]*this.REFERENCE.getHeight();
			painter.drawRect(this.startingPoint.x-width/2, this.startingPoint.y-height/2, width, height);
		}
	}

}
