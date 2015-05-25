package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ReflectionTool;
import org.rubato.rubettes.util.Point2D;

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
		Point2D endingPoint = new Point2D(this.startingPoint.getX()+reflectionVector[0],
				this.startingPoint.getY()-reflectionVector[1]);
		((ReflectionTool)this.displayTool).setEndingPoint(endingPoint);
		//super.updateEndingPoint(event);
		this.updateDisplayTool();
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		double[] reflectionVector = this.calculateReflectionVector(event);
		Point2D currentEndPoint = new Point2D(event.getPoint().getX(), event.getPoint().getY());
		this.controller.reflectSelectedObjects(this.startingPoint, currentEndPoint, reflectionVector, event.isAltDown(), startNewTransformation);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		double[] reflectionVector = this.calculateReflectionVector(event);
		this.controller.modifySelectedTransformation(reflectionVector);
	}
	
	private double[] calculateReflectionVector(MouseEvent event) {
		Point endPoint = event.getPoint();
		double x = endPoint.getX()-this.startingPoint.getX();
		double y = -1*(endPoint.getY()-this.startingPoint.getY());
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