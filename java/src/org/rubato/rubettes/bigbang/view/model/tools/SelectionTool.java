package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Rectangle2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class SelectionTool extends DisplayTool {
	
	private Rectangle2D.Double area;
	
	public SelectionTool() {
		
	}
	
	public void setArea(Rectangle2D.Double area) {
		this.area = area;
	}
	
	public Rectangle2D.Double getArea() {
		return this.area;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.area != null) {
			painter.setColor(this.BRIGHT);
			painter.fillRect(this.area.x, this.area.y, this.area.width, this.area.height);
			painter.setColor(this.DARK);
			painter.drawRect(this.area.x, this.area.y, this.area.width, this.area.height);
		}
	}

}
