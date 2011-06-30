package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.ShearingTool;

public class NoteShearingAdapter extends NoteTransformationAdapter {
	
	public NoteShearingAdapter(ViewController controller) {
		super(controller);
	}
	
	public NoteShearingAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] shearingFactors) {
		super(controller, startingPoint, endingPoint);
		((ShearingTool)this.displayTool).setShearingFactors(shearingFactors);
	}
	
	@Override
	protected void updateEndingPoint(MouseEvent event) {
		((ShearingTool)this.displayTool).setShearingFactors(this.calculateShearingFactors(event));
		super.updateEndingPoint(event);
	}
	
	@Override
	protected void transformSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		double[] shearingFactors = this.calculateShearingFactors(event);
		Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.controller.shearSelectedNotes(this.startingPoint, currentEndPoint, shearingFactors, event.isAltDown(), inPreviewMode);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event, boolean inPreviewMode) {
		double[] shearingFactors = this.calculateShearingFactors(event);
		this.controller.modifySelectedTransformation(shearingFactors, inPreviewMode);
	}
	
	private double[] calculateShearingFactors(MouseEvent event) {
		Point endingPoint = event.getPoint();
		double xDifference = endingPoint.x-this.startingPoint.x;
		double yDifference = endingPoint.y-this.startingPoint.y;
		double xFactor = 2*xDifference/((ShearingTool)this.displayTool).REFERENCE.getWidth();
		double yFactor = -2*yDifference/((ShearingTool)this.displayTool).REFERENCE.getHeight();
		if (event.isShiftDown()) {
			if (Math.abs(xDifference) >= Math.abs(yDifference)) {
				return new double[]{xFactor, 0};
			}
			return new double[]{0, yFactor};
		}
		return new double[]{xFactor, yFactor};
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new ShearingTool();
	}

}
