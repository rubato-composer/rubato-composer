package org.rubato.rubettes.bigbang.view.controller.display;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;

public class DisplayPositionAdapter extends MouseInputAdapter {
	
	private boolean movingDisplay;
	private ViewController controller;
	private Point previousPosition;
	
	public DisplayPositionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			Point startingPoint = event.getPoint();
			JBigBangDisplay source = ((JBigBangDisplay)event.getSource());
			this.movingDisplay = !source.getContents().getDisplayObjects().hasSelectedObjectAt(startingPoint);
			if (this.movingDisplay) {
				this.previousPosition = event.getPoint();
			}
		}
	}

	public void mouseDragged(MouseEvent event) {
		this.changePosition(event);
	}

	public void mouseReleased(MouseEvent event) {
		this.changePosition(event);
	}
	
	private void changePosition(MouseEvent event) {
		if (this.movingDisplay) {
			Point currentPosition = event.getPoint();
			Dimension difference = new Dimension(currentPosition.x-this.previousPosition.x,
				currentPosition.y-this.previousPosition.y);
			this.previousPosition = currentPosition;
			this.controller.changeDisplayPosition(difference);
		}
	}

}
