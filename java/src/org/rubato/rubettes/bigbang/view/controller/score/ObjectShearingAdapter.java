package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ShearingTool;
import org.rubato.rubettes.util.Point2D;

public class ObjectShearingAdapter extends ObjectTransformationAdapter {
	
	public ObjectShearingAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectShearingAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] shearingFactors) {
		super(controller, startingPoint, endingPoint);
		((ShearingTool)this.displayTool).setShearingFactors(shearingFactors);
	}
	
	@Override
	protected void updateEndingPoint(MouseEvent event) {
		((ShearingTool)this.displayTool).setShearingFactors(this.calculateShearingFactors(event));
		super.updateEndingPoint(event);
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		double[] shearingFactors = this.calculateShearingFactors(event);
		Point2D currentEndPoint = new Point2D(event.getPoint().getX(), event.getPoint().getY());
		this.controller.shearSelectedObjects(this.startingPoint, currentEndPoint, shearingFactors, event.isAltDown(), startNewTransformation);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		double[] shearingFactors = this.calculateShearingFactors(event);
		this.controller.modifySelectedTransformation(shearingFactors);
	}
	
	private double[] calculateShearingFactors(MouseEvent event) {
		Point endingPoint = event.getPoint();
		double xDifference = endingPoint.getX()-this.startingPoint.getX();
		double yDifference = endingPoint.getY()-this.startingPoint.getY();
		double xFactor = 2*xDifference/((ShearingTool)this.displayTool).REFERENCE.getWidth();
		double yFactor = -2*yDifference/((ShearingTool)this.displayTool).REFERENCE.getHeight();
		if (event.isShiftDown()) {
			if (Math.abs(xDifference) >= Math.abs(yDifference)) {
				return new double[]{xFactor, 0};
			}
			return new double[]{0, yFactor};
		}
		return new double[]{xFactor, yFactor};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ShearingTool();
	}

}
