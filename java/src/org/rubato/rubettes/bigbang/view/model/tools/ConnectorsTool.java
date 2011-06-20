package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Point;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayNoteList;

public class ConnectorsTool extends DisplayTool {

	private int relation;
	private Point point;
	
	public ConnectorsTool(int relation) {
		this.relation = relation;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	@Override
	public void paint(AbstractPainter painter) {
		if (this.display != null && this.point != null) {
			DisplayNoteList notes = this.display.getNotes();
			notes.paintSelectedNotesConnectors(painter, this.point.x, this.point.y, this.relation);
			notes.paintSelectedNotes(painter);
		}
	}

}
