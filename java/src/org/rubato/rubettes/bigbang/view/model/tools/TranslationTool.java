package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class TranslationTool extends DisplayTool {

	private final int SIZE = 20;

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		painter.fillOval(this.startingPoint.x-this.SIZE/2, this.startingPoint.y-this.SIZE/2, this.SIZE, this.SIZE);
		if (this.endingPoint != null) {
			painter.setColor(this.DARK);
			painter.drawOval(this.endingPoint.x-this.SIZE/2, this.endingPoint.y-this.SIZE/2, this.SIZE, this.SIZE);
		}
	}

}
