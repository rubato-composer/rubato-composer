package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ReflectionTool;

public class NoteReflectionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private Point2D.Double startingPoint;
	private ReflectionTool reflectionTool;
	
	public NoteReflectionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public NoteReflectionAdapter(ViewController controller, double[] startingPoint, double[] endPoint) {
		this.controller = controller;
		this.updateStartingPoint(startingPoint[0], startingPoint[1]);
		Point2D.Double endPoint2D = new Point2D.Double(endPoint[0], endPoint[1]);
		this.updateToolEndingPoint(endPoint2D);
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
		}
	}
	
	private void updateStartingPoint(double x, double y) {
		this.startingPoint = new Point2D.Double(x, y);
		this.reflectionTool = new ReflectionTool(this.startingPoint);
		this.controller.changeDisplayTool(this.reflectionTool);
	}
	
	private void updateToolEndingPoint(Point2D.Double endPoint) {
		this.reflectionTool.setEndingPoint(endPoint);
		this.controller.changeDisplayTool(this.reflectionTool);
	}

	public void mouseDragged(MouseEvent event) {
		this.changeReflectionToolPosition(event);
	}

	public void mouseReleased(MouseEvent event) {
		this.reflectSelectedNotes(event);
	}
	
	private void changeReflectionToolPosition(MouseEvent event) {
		if (this.reflectionTool != null) {
			Point2D.Double endPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			this.updateToolEndingPoint(endPoint);
			this.reflect(event, true);
		}
	}
	
	private void reflectSelectedNotes(MouseEvent event) {
		if (this.reflectionTool != null) {
			this.reflect(event, false);
			this.reflectionTool = null;
			this.controller.clearDisplayTool();
		}
	}
	
	private void reflect(MouseEvent event, boolean inPreviewMode) {
		Point currentPoint = event.getPoint();
		double[] reflectionVector = this.calculateReflectionVector(currentPoint);
		Point2D.Double currentEndPoint = new Point2D.Double(currentPoint.x, currentPoint.y);
		this.controller.reflectSelectedNotes(this.startingPoint, currentEndPoint, reflectionVector, event.isAltDown(), inPreviewMode);
	}
	
	private double[] calculateReflectionVector(Point endPoint) {
		double x = endPoint.x-this.startingPoint.x;
		double y = -1*(endPoint.y-this.startingPoint.y);
		return new double[]{x, y};
	}

}