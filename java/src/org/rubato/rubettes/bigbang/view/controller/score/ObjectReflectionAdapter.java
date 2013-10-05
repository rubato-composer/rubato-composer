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
	protected void transformSelectedObjects(MouseEvent event, boolean inPreviewMode) {
		Point currentPoint = event.getPoint();
		double[] reflectionVector = this.calculateReflectionVector(currentPoint);
		Point2D.Double currentEndPoint = new Point2D.Double(currentPoint.x, currentPoint.y);
		this.controller.reflectSelectedObjects(this.startingPoint, currentEndPoint, reflectionVector, event.isAltDown(), inPreviewMode);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event, boolean inPreviewMode) {
		Point currentPoint = event.getPoint();
		double[] reflectionVector = this.calculateReflectionVector(currentPoint);
		this.controller.modifySelectedTransformation(reflectionVector, inPreviewMode);
	}
	
	private double[] calculateReflectionVector(Point endPoint) {
		double x = endPoint.x-this.startingPoint.x;
		double y = -1*(endPoint.y-this.startingPoint.y);
		return new double[]{x, y};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ReflectionTool();
	}

}