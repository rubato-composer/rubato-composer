package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.TranslationTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;

public class NoteTranslationAdapter extends MouseInputAdapter {
	
	private boolean movingNotes;
	private ViewController controller;
	private Point2D.Double startingPoint;
	private TranslationTool translationTool;
	//private Point previousPoint;
	
	public NoteTranslationAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public NoteTranslationAdapter(ViewController controller, double[] startingPoint, double[] endPoint) {
		this.controller = controller;
		this.updateStartingPoint(startingPoint[0], startingPoint[1]);
		Point2D.Double endPoint2D = new Point2D.Double(endPoint[0], endPoint[1]);
		this.updateToolEndingPoint(endPoint2D);
	}
	
	private void updateStartingPoint(double x, double y) {
		this.startingPoint = new Point2D.Double(x, y);
		this.translationTool = new TranslationTool(this.startingPoint);
		this.controller.changeDisplayTool(this.translationTool);
	}
	
	private void updateToolEndingPoint(Point2D.Double endPoint) {
		this.translationTool.setEndPoint(endPoint);
		this.controller.changeDisplayTool(this.translationTool);
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
			JBigBangDisplay source = ((JBigBangDisplay)event.getSource());
			this.movingNotes = source.getContents().getNotes().hasSelectedNoteAt(event.getPoint());
			if (this.movingNotes) {
				//this.previousPoint = this.startingPoint;
			}
		}
	}

	public void mouseDragged(MouseEvent event) {
		Point2D.Double endPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		this.updateToolEndingPoint(endPoint);
		this.translateSelectedNotes(event, true);
	}

	public void mouseReleased(MouseEvent event) {
		if (this.translationTool != null) {
			this.translateSelectedNotes(event, false);
			this.translationTool = null;
			this.controller.clearDisplayTool();
		}
	}
	
	private void translateSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		if (this.movingNotes) {
			boolean copyAndTranslate = event.isAltDown();
			Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			this.controller.translateSelectedNotes(this.startingPoint, currentEndPoint, copyAndTranslate, inPreviewMode);
		}
	}

}
