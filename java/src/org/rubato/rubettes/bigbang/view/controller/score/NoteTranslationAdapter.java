package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.TranslationTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;

public class NoteTranslationAdapter extends NoteTransformationAdapter {
	
	public NoteTranslationAdapter(ViewController controller) {
		super(controller);
	}
	
	public NoteTranslationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		super(controller, startingPoint, endingPoint);
	}

	private boolean movingNotes;
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.updateStartingPoint(event.getPoint().x, event.getPoint().y);
			JBigBangDisplay source = ((JBigBangDisplay)event.getSource());
			this.movingNotes = source.getContents().getNotes().hasSelectedNoteAt(event.getPoint());
		}
	}
	
	@Override
	protected void transformSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		if (this.movingNotes) {
			boolean copyAndTranslate = event.isAltDown();
			Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			this.controller.translateSelectedNotes(this.startingPoint, currentEndPoint, copyAndTranslate, inPreviewMode);
		}
	}
	
	@Override
	protected void initDisplayTool() {
		this.displayTool = new TranslationTool(this.startingPoint);
	}

}
