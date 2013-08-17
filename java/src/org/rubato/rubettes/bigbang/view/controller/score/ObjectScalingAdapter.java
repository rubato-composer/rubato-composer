package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ScalingTool;

public class ObjectScalingAdapter extends ObjectTransformationAdapter {
	
	public ObjectScalingAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectScalingAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] scaleFactors) {
		super(controller, startingPoint, endingPoint);
		((ScalingTool)this.displayTool).setScalingFactors(scaleFactors);
	}
	
	@Override
	protected void updateEndingPoint(MouseEvent event) {
		((ScalingTool)this.displayTool).setScalingFactors(this.calculateScaleFactors(event));
		super.updateEndingPoint(event);
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean inPreviewMode) {
		double[] scaleFactors = this.calculateScaleFactors(event);
		Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.controller.scaleSelectedObjects(this.startingPoint, currentEndPoint, scaleFactors, event.isAltDown(), inPreviewMode);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event, boolean inPreviewMode) {
		double[] scaleFactors = this.calculateScaleFactors(event);
		this.controller.modifySelectedTransformation(scaleFactors, inPreviewMode);
	}
	
	private double[] calculateScaleFactors(MouseEvent event) {
		Point endPoint = event.getPoint();
		double referenceWidth = ((ScalingTool)this.displayTool).REFERENCE.getWidth();
		double xFactor = Math.abs(endPoint.x-this.startingPoint.x+(referenceWidth/2))*2/referenceWidth;
		if (event.isShiftDown()) {
			return new double[]{xFactor, xFactor};
		}
		double referenceHeight = ((ScalingTool)this.displayTool).REFERENCE.getHeight();
		double yFactor = Math.abs((referenceHeight/2)-(endPoint.y-this.startingPoint.y))*2/referenceHeight;
		return new double[]{xFactor, yFactor};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ScalingTool();
	}

}
