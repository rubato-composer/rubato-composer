package org.rubato.rubettes.bigbang.view.controller.display;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.Dimension;
import org.rubato.rubettes.util.Point2D;

public class DisplayPositionAdapter extends MouseInputAdapter {
	
	private boolean movingDisplay;
	private ViewController controller;
	private Point previousPosition;
	
	public DisplayPositionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			Point2D startingPoint = new Point2D(event.getPoint().x, event.getPoint().y);
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
