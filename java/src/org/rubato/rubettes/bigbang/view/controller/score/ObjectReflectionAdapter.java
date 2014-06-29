package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ReflectionTool;

public class ObjectReflectionAdapter extends ObjectTransformationAdapter {
	
	public ObjectReflectionAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectReflectionAdapter(ViewController controller, double[] startingPoint, double[] reflectionVector) {
		super(controller, startingPoint, new double[]{startingPoint[0]+reflectionVector[0], startingPoint[1]-reflectionVector[1]});
	}
	
	@Override
	protected void updateEndingPoint(MouseEvent event) {
		double[] reflectionVector = this.calculateReflectionVector(event);
		Point2D.Double endingPoint = new Point2D.Double(this.startingPoint.x+reflectionVector[0],
				this.startingPoint.y-reflectionVector[1]);
		((ReflectionTool)this.displayTool).setEndingPoint(endingPoint);
		//super.updateEndingPoint(event);
		this.updateDisplayTool();
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		double[] reflectionVector = this.calculateReflectionVector(event);
		Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.controller.reflectSelectedObjects(this.startingPoint, currentEndPoint, reflectionVector, event.isAltDown(), startNewTransformation);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		double[] reflectionVector = this.calculateReflectionVector(event);
		this.controller.modifySelectedTransformation(reflectionVector);
	}
	
	private double[] calculateReflectionVector(MouseEvent event) {
		Point endPoint = event.getPoint();
		double x = endPoint.x-this.startingPoint.x;
		double y = -1*(endPoint.y-this.startingPoint.y);
		if (event.isShiftDown()) {
			if (Math.abs(x) > Math.abs(y)) {
				return new double[]{x, 0};
			}
			return new double[]{0, y};
		}
		return new double[]{x, y};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ReflectionTool();
	}

}