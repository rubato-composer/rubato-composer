package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ScalingTool extends DisplayTool {
	
	private Point2D.Double center;
	private Dimension reference;
	private double[] scaleFactors;
	
	public ScalingTool(Point2D.Double center, Dimension reference) {
		this.center = center;
		this.reference = reference;
	}
	
	public void setScaleFactors(double[] scaleFactors) {
		this.scaleFactors = scaleFactors;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		double width = this.reference.getWidth();
		double height = this.reference.getHeight();
		painter.fillRect(this.center.x-width/2, this.center.y-height/2, width, height);
		if (this.scaleFactors != null) {
			painter.setColor(this.DARK);
			width = this.scaleFactors[0]*this.reference.getWidth();
			height = this.scaleFactors[1]*this.reference.getHeight();
			painter.drawRect(this.center.x-width/2, this.center.y-height/2, width, height);
		}
	}

}
