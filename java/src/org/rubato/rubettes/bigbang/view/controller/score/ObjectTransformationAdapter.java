package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.util.Point2D;

public abstract class ObjectTransformationAdapter extends MouseInputAdapter {
	
	protected ViewController controller;
	protected Point2D startingPoint;
	protected DisplayTool displayTool;
	protected boolean inModificationMode;
	protected boolean startNewTransformation;
	
	public ObjectTransformationAdapter(ViewController controller) {
		this.init(controller);
		this.inModificationMode = false;
	}
	
	public ObjectTransformationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		this.init(controller);
		this.updateStartingPoint(startingPoint[0], startingPoint[1]);
		Point2D endingPoint2D = new Point2D(endingPoint[0], endingPoint[1]);
		this.updateEndingPoint(endingPoint2D);
		this.inModificationMode = true;
	}
	
	private void init(ViewController controller) {
		this.controller = controller;
		this.startNewTransformation = true;
		this.initDisplayTool();
	}
	
	public void mouseClicked(MouseEvent event) {
		Point clickedPoint = event.getPoint();
		this.updateStartingPoint(clickedPoint.x, clickedPoint.y);
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !this.inModificationMode) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
		}
	}
	
	public void mouseDragged(MouseEvent event) {
		this.updateEndingPoint(event);
		this.transformOrModifyTransformation(event);
		this.startNewTransformation = false;
	}

	public void mouseReleased(MouseEvent event) {
		if (!this.startNewTransformation) {
			this.transformOrModifyTransformation(event);
			this.startNewTransformation = true;
			if (!this.inModificationMode) {
				this.initDisplayTool();
				this.controller.clearDisplayTool();
			}
		}
	}
	
	protected void updateStartingPoint(double x, double y) {
		this.startingPoint = new Point2D(x, y);
		this.displayTool.setStartingPoint(this.startingPoint);
		this.updateDisplayTool();
		if (this.inModificationMode) {
			this.controller.modifyCenterOfSelectedTransformation(this.startingPoint);
		}
	}
	
	protected void updateEndingPoint(MouseEvent event) {
		Point2D endingPoint = new Point2D(event.getPoint().x, event.getPoint().y);
		this.updateEndingPoint(endingPoint);
	}
	
	protected void updateEndingPoint(Point2D endingPoint) {
		this.displayTool.setEndingPoint(endingPoint);
		this.updateDisplayTool();
	}
	
	private void transformOrModifyTransformation(MouseEvent event) {
		if (this.inModificationMode) {
			this.modifySelectedTransformation(event);
		} else {
			this.transformSelectedObjects(event, this.startNewTransformation);
		}
	}
	
	protected abstract void transformSelectedObjects(MouseEvent event, boolean startNewTransformation);
	
	protected abstract void modifySelectedTransformation(MouseEvent event);
	
	protected abstract void initDisplayTool();
	
	protected void updateDisplayTool() {
		this.controller.changeDisplayTool(this.displayTool);
	}

}
