package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ScalingTool;

public class NoteScalingAdapter extends NoteTransformationAdapter {
	
	public NoteScalingAdapter(ViewController controller) {
		super(controller);
	}
	
	public NoteScalingAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] scaleFactors) {
		super(controller, startingPoint, endingPoint);
		((ScalingTool)this.displayTool).setScalingFactors(scaleFactors);
	}
	
	@Override
	protected void updateEndingPoint(MouseEvent event) {
		((ScalingTool)this.displayTool).setScalingFactors(this.calculateScaleFactors(event));
		super.updateEndingPoint(event);
	}
	
	@Override
	protected void transformSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		double[] scaleFactors = this.calculateScaleFactors(event);
		Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.controller.scaleSelectedNotes(this.startingPoint, currentEndPoint, scaleFactors, event.isAltDown(), inPreviewMode);
	}
	
	private double[] calculateScaleFactors(MouseEvent event) {
		Point endPoint = event.getPoint();
		double xFactor = Math.abs(endPoint.x-this.startingPoint.x)*2/((ScalingTool)this.displayTool).REFERENCE.getWidth();
		if (event.isShiftDown()) {
			return new double[]{xFactor, xFactor};
		}
		double yFactor = Math.abs(endPoint.y-this.startingPoint.y)*2/((ScalingTool)this.displayTool).REFERENCE.getHeight();
		return new double[]{xFactor, yFactor};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ScalingTool();
	}

}
