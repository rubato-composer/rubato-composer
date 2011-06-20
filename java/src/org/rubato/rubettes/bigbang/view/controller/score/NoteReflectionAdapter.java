package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ReflectionTool;

public class NoteReflectionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private Point startPoint;
	private ReflectionTool reflectionTool;
	
	public NoteReflectionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.startPoint = event.getPoint();
			this.reflectionTool = new ReflectionTool(this.startPoint);
			this.controller.changeDisplayTool(this.reflectionTool);
		}
	}

	public void mouseDragged(MouseEvent event) {
		this.changeReflectionToolPosition(event);
	}

	public void mouseReleased(MouseEvent event) {
		this.reflectSelectedNotes(event);
	}
	
	private void changeReflectionToolPosition(MouseEvent event) {
		if (this.reflectionTool != null) {
			this.reflectionTool.setEndPoint(event.getPoint());
			this.controller.changeDisplayTool(this.reflectionTool);
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
		double[] reflectionVector = this.calculateReflectionVector(event.getPoint());
		this.controller.reflectSelectedNotes(this.startPoint, reflectionVector, event.isAltDown(), inPreviewMode);
	}
	
	private double[] calculateReflectionVector(Point endPoint) {
		double x = endPoint.x-this.startPoint.x;
		double y = -1*(endPoint.y-this.startPoint.y);
		return new double[]{x, y};
	}

}