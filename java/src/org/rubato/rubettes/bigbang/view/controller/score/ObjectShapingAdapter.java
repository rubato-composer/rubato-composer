package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.TreeMap;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ObjectShapingAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private TreeMap<Integer,Integer> currentPoints;
	
	public ObjectShapingAdapter(ViewController controller) {
		this.controller = controller;
		this.resetCurrentPoints();
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			this.updatePoints(event.getPoint());
			this.controller.shapeSelectedObjects(this.currentPoints, event.isAltDown(), false);
			this.resetCurrentPoints();
		}
	}
	
	public void mouseDragged(MouseEvent event) {
		this.updatePoints(event.getPoint());
		this.controller.shapeSelectedObjects(this.currentPoints, event.isAltDown(), true);
	}
	
	public void mouseReleased(MouseEvent event) {
		this.updatePoints(event.getPoint());
		this.controller.shapeSelectedObjects(this.currentPoints, event.isAltDown(), false);
		this.resetCurrentPoints();
	}
	
	private void updatePoints(Point location) {
		this.currentPoints.put((int)location.getX(), (int)location.getY());
	}
	
	private void resetCurrentPoints() {
		this.currentPoints = new TreeMap<Integer,Integer>();
	}

}
