package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.PerformanceCheck;

public class NoteTranslationAdapter extends MouseInputAdapter {
	
	private boolean movingNotes;
	private ViewController controller;
	private Point startingPoint;
	//private Point previousPoint;
	
	public NoteTranslationAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.startingPoint = event.getPoint();
			JBigBangDisplay source = ((JBigBangDisplay)event.getSource());
			this.movingNotes = source.getContents().getNotes().hasSelectedNoteAt(this.startingPoint);
			if (this.movingNotes) {
				PerformanceCheck.startTask("mouse");
				//this.previousPoint = this.startingPoint;
			}
		}
	}

	public void mouseDragged(MouseEvent event) {
		this.translateSelectedNotes(event, true);
	}

	public void mouseReleased(MouseEvent event) {
		this.translateSelectedNotes(event, false);
		PerformanceCheck.printPercentages();
		PerformanceCheck.reset();
		System.out.println();
	}
	
	private void translateSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		if (this.movingNotes) {
			boolean copyAndTranslate = event.isAltDown();
			Dimension difference = this.getDifference(this.startingPoint, event.getPoint());
			this.controller.translateSelectedNotes(difference, copyAndTranslate, inPreviewMode);
		}
	}
	
	private Dimension getDifference(Point position0, Point position1) {
		return new Dimension(position1.x-position0.x, position1.y-position0.y);
	}

}
