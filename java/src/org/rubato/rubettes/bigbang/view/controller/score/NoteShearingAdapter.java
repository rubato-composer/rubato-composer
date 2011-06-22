package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ShearingTool;

public class NoteShearingAdapter extends MouseInputAdapter {
	
	private final Dimension REFERENCE = new Dimension(100, 100);
	private ViewController controller;
	private Point2D.Double center;
	private ShearingTool shearingTool;
	
	public NoteShearingAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.center = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			this.shearingTool = new ShearingTool(this.center, this.REFERENCE);
			this.controller.changeDisplayTool(this.shearingTool);
		}
	}

	public void mouseDragged(MouseEvent event) {
		this.updateShearingTool(event);
	}

	public void mouseReleased(MouseEvent event) {
		this.shearSelectedNotes(event);
	}
	
	private void updateShearingTool(MouseEvent event) {
		if (this.shearingTool != null) {
			double[] shearingFactors = this.shear(event, true);
			this.shearingTool.setShearingFactors(shearingFactors);
			this.controller.changeDisplayTool(this.shearingTool);
		}
	}
	
	private void shearSelectedNotes(MouseEvent event) {
		if (this.shearingTool != null) {
			this.shear(event, false);
			this.shearingTool = null;
			this.controller.clearDisplayTool();
		}
	}
	
	private double[] shear(MouseEvent event, boolean inPreviewMode) {
		double[] shearingFactors = this.calculateShearingFactors(event);
		this.controller.shearSelectedNotes(this.center, shearingFactors, event.isAltDown(), inPreviewMode);
		return shearingFactors;
	}
	
	private double[] calculateShearingFactors(MouseEvent event) {
		Point endPoint = event.getPoint();
		double xDifference = endPoint.x-this.center.x;
		double yDifference = endPoint.y-this.center.y;
		double xFactor = 2*xDifference/this.REFERENCE.getWidth();
		double yFactor = -2*yDifference/this.REFERENCE.getHeight();
		if (event.isShiftDown()) {
			if (Math.abs(xDifference) >= Math.abs(yDifference)) {
				return new double[]{xFactor, 0};
			}
			return new double[]{0, yFactor};
		}
		return new double[]{xFactor, yFactor};
	}

}
