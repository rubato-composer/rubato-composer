package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;

public abstract class ObjectTransformationAdapter extends MouseInputAdapter {
	
	protected ViewController controller;
	protected Point2D.Double startingPoint;
	protected DisplayTool displayTool;
	protected boolean inModificationMode;
	protected boolean dragging;
	
	public ObjectTransformationAdapter(ViewController controller) {
		this.init(controller);
		this.inModificationMode = false;
	}
	
	public ObjectTransformationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		this.init(controller);
		this.updateStartingPoint(startingPoint[0], startingPoint[1]);
		Point2D.Double endingPoint2D = new Point2D.Double(endingPoint[0], endingPoint[1]);
		this.updateEndingPoint(endingPoint2D);
		this.inModificationMode = true;
	}
	
	private void init(ViewController controller) {
		this.controller = controller;
		this.dragging = false;
		this.initDisplayTool();
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !this.inModificationMode) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
		}
	}
	
	public void mouseDragged(MouseEvent event) {
		this.updateEndingPoint(event);
		this.transformOrModifyTransformation(event, true);
		this.dragging = true;
	}

	public void mouseReleased(MouseEvent event) {
		if (this.dragging) {
			this.transformOrModifyTransformation(event, false);
			this.dragging = false;
			if (!this.inModificationMode) {
				this.initDisplayTool();
				this.controller.clearDisplayTool();
			}
		}
	}
	
	protected void updateStartingPoint(double x, double y) {
		this.startingPoint = new Point2D.Double(x, y);
		this.displayTool.setStartingPoint(this.startingPoint);
		this.updateDisplayTool();
	}
	
	protected void updateEndingPoint(MouseEvent event) {
		Point2D.Double endingPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.updateEndingPoint(endingPoint);
	}
	
	private void updateEndingPoint(Point2D.Double endingPoint) {
		this.displayTool.setEndingPoint(endingPoint);
		this.updateDisplayTool();
	}
	
	private void transformOrModifyTransformation(MouseEvent event, boolean inPreviewMode) {
		if (this.inModificationMode) {
			this.modifySelectedTransformation(event, inPreviewMode);
		} else {
			this.transformSelectedObjects(event, inPreviewMode);
		}
	}
	
	protected abstract void transformSelectedObjects(MouseEvent event, boolean inPreviewMode);
	
	protected void modifySelectedTransformation(MouseEvent event, boolean inPreviewMode) {
		Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.controller.modifySelectedTransformation(currentEndPoint, inPreviewMode);
	}
	
	protected abstract void initDisplayTool();
	
	protected void updateDisplayTool() {
		this.controller.changeDisplayTool(this.displayTool);
	}

}
