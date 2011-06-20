package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.GeometryTools;

public class NoteSelectionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private Point2D.Double startingPoint;
	private SelectionTool selectionTool;
	
	public NoteSelectionAdapter(ViewController controller) {
		this.controller = controller;
		this.selectionTool = new SelectionTool();
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			if (event.isAltDown()) {
				this.controller.toggleAnchorNoteSelection(event.getPoint());
			} else {
				this.controller.toggleNoteSelection(event.getPoint());
			}
		}
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			Point location = event.getPoint();
			if (!((JBigBangDisplay)event.getSource()).getContents().getNotes().hasSelectedNoteAt(location)) {
				this.startingPoint = new Point2D.Double(location.x, location.y);
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
			this.selectionTool.setArea(GeometryTools.getRectangle(this.startingPoint, new Point2D.Double(currentPoint.x, currentPoint.y)));
			this.controller.selectNotes(this.selectionTool, stillSelecting);
			if (!stillSelecting) {
				this.startingPoint = null;
			}
		}
	}

}
