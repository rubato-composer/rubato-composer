package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ReflectionTool;

public class NoteReflectionAdapter extends NoteTransformationAdapter {
	
	public NoteReflectionAdapter(ViewController controller) {
		super(controller);
	}
	
	public NoteReflectionAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		super(controller, startingPoint, endingPoint);
	}
	
	@Override
	protected void transformSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		Point currentPoint = event.getPoint();
		double[] reflectionVector = this.calculateReflectionVector(currentPoint);
		Point2D.Double currentEndPoint = new Point2D.Double(currentPoint.x, currentPoint.y);
		this.controller.reflectSelectedNotes(this.startingPoint, currentEndPoint, reflectionVector, event.isAltDown(), inPreviewMode);
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