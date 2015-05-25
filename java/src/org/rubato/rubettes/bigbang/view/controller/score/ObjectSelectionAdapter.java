package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.Point2D;

public class ObjectSelectionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private Point2D startingPoint;
	private SelectionTool selectionTool;
	
	public ObjectSelectionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			if (event.isAltDown()) {
				this.controller.toggleAnchorObjectSelection(event.getPoint());
			} else {
				this.controller.toggleObjectSelection(event.getPoint());
			}
		}
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			Point2D location = new Point2D(event.getPoint().x, event.getPoint().y);
			if (!((JBigBangDisplay)event.getSource()).getContents().getDisplayObjects().hasSelectedObjectAt(location)) {
				this.startingPoint = location;
				this.selectionTool = new SelectionTool();
				this.selectionTool.setStartingPoint(this.startingPoint);
			}
		}
	}
	
	public void mouseDragged(MouseEvent event) {
		this.updateSelection(event, true);
	}
	
	public void mouseReleased(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			this.updateSelection(event, false);
		}
	}
	
	private void updateSelection(MouseEvent event, boolean stillSelecting) {
		if (this.startingPoint != null) {
			Point currentPoint = event.getPoint();
			this.selectionTool.setEndingPoint(new Point2D(currentPoint.x, currentPoint.y));
			this.controller.selectObjects(this.selectionTool, stillSelecting);
			if (!stillSelecting) {
				this.startingPoint = null;
			}
		}
	}

}
