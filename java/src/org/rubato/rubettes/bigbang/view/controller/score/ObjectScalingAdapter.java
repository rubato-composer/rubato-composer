package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ScalingTool;
import org.rubato.rubettes.util.Point2D;

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
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		double[] scaleFactors = this.calculateScaleFactors(event);
		Point2D currentEndPoint = new Point2D(event.getPoint().getX(), event.getPoint().getY());
		this.controller.scaleSelectedObjects(this.startingPoint, currentEndPoint, scaleFactors, event.isAltDown(), startNewTransformation);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		double[] scaleFactors = this.calculateScaleFactors(event);
		this.controller.modifySelectedTransformation(scaleFactors);
	}
	
	private double[] calculateScaleFactors(MouseEvent event) {
		Point endPoint = event.getPoint();
		double referenceWidth = ((ScalingTool)this.displayTool).REFERENCE.getWidth();
		double xFactor = Math.abs(endPoint.getX()-this.startingPoint.getX()+(referenceWidth/2))*2/referenceWidth;
		if (event.isShiftDown()) {
			return new double[]{xFactor, xFactor};
		}
		double referenceHeight = ((ScalingTool)this.displayTool).REFERENCE.getHeight();
		double yFactor = Math.abs((referenceHeight/2)-(endPoint.getY()-this.startingPoint.getY()))*2/referenceHeight;
		return new double[]{xFactor, yFactor};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ScalingTool();
	}

}
