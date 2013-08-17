package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Point;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;

public class ConnectorsTool extends DisplayTool {

	private Point point;
	
	public ConnectorsTool() {
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	@Override
	public void paint(AbstractPainter painter) {
		if (this.display != null && this.point != null) {
			DisplayObjects notes = this.display.getDisplayObjects();
			notes.paintSelectedObjectsConnectors(painter, this.point.x, this.point.y);
			//notes.paintSelectedNotes(painter);
		}
	}

}
