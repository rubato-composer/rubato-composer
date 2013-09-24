package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ObjectAdditionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private int mouseButton;
	
	public ObjectAdditionAdapter(ViewController controller, int mouseButton) {
		this.controller = controller;
		this.mouseButton = mouseButton;
	}
	
	public void mouseClicked(MouseEvent event) {
		this.drawNote(event);
	}
	
	public void mouseDragged(MouseEvent event) {
		this.drawNote(event);
	}
	
	private void drawNote(MouseEvent event) {
		if (event.getButton() == this.mouseButton) {
			ArrayList<Point2D.Double> pointList = new ArrayList<Point2D.Double>(); 
			pointList.add(new Point2D.Double(event.getPoint().x, event.getPoint().y));
			this.controller.addObjects(pointList, false);
		}
	}

}
