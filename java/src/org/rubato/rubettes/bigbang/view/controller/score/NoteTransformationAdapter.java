package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;

public abstract class NoteTransformationAdapter extends MouseInputAdapter {
	
	protected ViewController controller;
	protected Point2D.Double startingPoint;
	protected DisplayTool displayTool;
	protected boolean inModificationMode;
	protected boolean dragging;
	
	public NoteTransformationAdapter(ViewController controller) {
		this.init(controller);
	}
	
	public NoteTransformationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		this.init(controller);
		this.updateStartingPoint(startingPoint[0], startingPoint[1]);
		Point2D.Double endingPoint2D = new Point2D.Double(endingPoint[0], endingPoint[1]);
		this.updateEndingPoint(endingPoint2D);
	}
	
	private void init(ViewController controller) {
		this.controller = controller;
		this.dragging = false;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
		}
	}
	
	public void mouseDragged(MouseEvent event) {
		this.updateEndingPoint(event);
		this.transformSelectedNotes(event, true);
		this.dragging = true;
	}

	public void mouseReleased(MouseEvent event) {
		if (this.displayTool != null && this.dragging) {
			this.transformSelectedNotes(event, false);
			this.displayTool = null;
			this.controller.clearDisplayTool();
			this.dragging = false;
		}
	}
	
	private void updateStartingPoint(double x, double y) {
		this.startingPoint = new Point2D.Double(x, y);
		this.initDisplayTool();
		this.controller.changeDisplayTool(this.displayTool);
	}
	
	protected void updateEndingPoint(MouseEvent event) {
		Point2D.Double endingPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.updateEndingPoint(endingPoint);
	}
	
	private void updateEndingPoint(Point2D.Double endPoint) {
		this.displayTool.setEndingPoint(endPoint);
		this.controller.changeDisplayTool(this.displayTool);
	}
	
	protected abstract void transformSelectedNotes(MouseEvent event, boolean inPreviewMode);
	
	protected abstract void initDisplayTool();

}
