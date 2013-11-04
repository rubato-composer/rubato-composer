package org.rubato.rubettes.bigbang.view.input.leap;

import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;
import org.rubato.rubettes.util.PointND;

import com.leapmotion.leap.Vector;

public class LeapSpace {
	private final int AREA_WIDTH = 400;
	private final int AREA_HEIGHT = 300;
	private final int AREA_BOTTOM_EDGE = 50;
	
	private int leftEdge, rightEdge, topEdge;
	
	public LeapSpace() {
		this.leftEdge = -this.AREA_WIDTH/2;
		this.rightEdge = this.AREA_WIDTH/2;
		this.topEdge = this.AREA_BOTTOM_EDGE+this.AREA_HEIGHT;
	}
	
	public Boolean OnScreen(Vector v) {
		if (v.getX() <= this.rightEdge && 
				v.getX() >= this.leftEdge &&
				v.getY() >= this.AREA_BOTTOM_EDGE && 
				v.getY() <= this.topEdge && 
				v.getZ() <= this.rightEdge && 
				v.getZ() >= this.leftEdge) {
			return true;
		}
		return false;
	}
	
	public PointND ToScreenPoint(Vector v) {
		double x = (v.getX()+(this.AREA_WIDTH/2))/this.AREA_WIDTH*JBigBangDisplay.DISPLAY_WIDTH;
		double y = (this.AREA_HEIGHT-(v.getY()-this.AREA_BOTTOM_EDGE))/this.AREA_HEIGHT*JBigBangPanel.CENTER_PANEL_HEIGHT;
		double z = (v.getZ()+(this.AREA_WIDTH/2))/this.AREA_WIDTH*JBigBangDisplay.DISPLAY_WIDTH;
		return new PointND(x, y, z);
	}
}
