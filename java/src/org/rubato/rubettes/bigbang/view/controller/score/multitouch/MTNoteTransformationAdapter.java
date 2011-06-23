package org.rubato.rubettes.bigbang.view.controller.score.multitouch;

import java.awt.geom.Point2D;

import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomEvent;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.TransformationTool;
import org.rubato.rubettes.util.GeometryTools;

public class MTNoteTransformationAdapter implements IGestureEventListener {
	
	private ViewController controller;
	private TransformationTool transformationTool;
	private Point2D.Double startingFinger1;
	private double startingAngle;
	private double startingDistance;
	private Point2D.Double currentFinger1, currentFinger2;
	
	public MTNoteTransformationAdapter(ViewController controller) {
		this.controller = controller;
	}

	public boolean processGestureEvent(MTGestureEvent ge) {
		if (ge instanceof ZoomEvent || ge instanceof RotateEvent) {
			ZoomEvent event = (ZoomEvent)ge;
			Point2D.Double finger1 = this.getPoint(event.getFirstCursor());
			Point2D.Double finger2 = this.getPoint(event.getSecondCursor());
			if (event.getId() == MTGestureEvent.GESTURE_ENDED) {
				//need the fingers from UPDATED or DETECTED, since in ENDED they are sometimes
				//randomly switched!!
				this.transform(this.currentFinger1, this.currentFinger2, false);
				this.transformationTool = null;
				this.controller.clearDisplayTool();
			} else {
				this.currentFinger1 = finger1;
				this.currentFinger2 = finger2;
				this.updateTransformationTool(finger1, finger2);
				if (event.getId() == MTGestureEvent.GESTURE_DETECTED) {
					this.startingFinger1 = finger2; //VERY ODD that the fingers are always switched in GESTURE_DETECTED
					this.startingAngle = GeometryTools.calculateAngle(finger2, finger1);
					this.startingDistance = this.calculateDistance(finger2, finger1);
				} else {
					this.transform(finger1, finger2, true);
				}
			}
		}
		return false;
	}
	
	private Point2D.Double getPoint(InputCursor cursor) {
		return new Point2D.Double(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
	}
	
	private void updateTransformationTool(Point2D.Double finger1, Point2D.Double finger2) {
		if (this.transformationTool == null) {
			this.transformationTool = new TransformationTool(finger1, finger2);
		} else {
			this.transformationTool.setCurrentPoints(finger1, finger2);
		}
		this.controller.changeDisplayTool(this.transformationTool);
	}
	
	private void transform(Point2D.Double finger1, Point2D.Double finger2, boolean inPreviewMode) {
		double[] shift = this.calculateShift(finger1);
		double arcAngle = GeometryTools.calculateArcAngle(finger1, this.startingAngle, finger2);
		double[] scaleFactors = this.calculateScaleFactors(finger1, finger2);
		boolean copyAndTransform = false; //TODO:read ALT key!!!!
		//System.out.println(shift[0] + " " + shift[1] + " " + arcAngle + " " + scaleFactors[0] + " " + scaleFactors[1]);
		Point2D.Double center = new Point2D.Double(this.startingFinger1.x, this.startingFinger1.y);
		Point2D.Double endPoint = new Point2D.Double(finger2.x, finger2.y);
		this.controller.affineTransformSelectedNotes(center, endPoint, shift, arcAngle, scaleFactors, copyAndTransform, inPreviewMode);
	}
	
	private double[] calculateShift(Point2D.Double currentFinger1) {
		//System.out.println(currentFinger1 + " " + currentFinger2 + " " + this.startingFinger1);
		return new double[]{currentFinger1.x-this.startingFinger1.x, this.startingFinger1.y-currentFinger1.y};
	}
	
	private double[] calculateScaleFactors(Point2D.Double finger1, Point2D.Double finger2) {
		double factor = this.calculateDistance(finger1, finger2)/this.startingDistance;
		return new double[]{factor, factor};
	}
	
	private double calculateDistance(Point2D.Double finger1, Point2D.Double finger2) {
		return Math.abs(finger1.distance(finger2));
	}
	
}