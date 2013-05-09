package org.rubato.rubettes.bigbang.view.subview.multitouch;

import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;

public class MTDisplayContents extends DisplayContents {
	
	public MTDisplayContents(BigBangPlayer player) {
		super(player);
	}
	
	@Override
	public double getMinVisibleX() {
		return this.viewParameters.get(0).translateDisplayValue(this.xPosition);
	}
	
	@Override
	public double getMaxVisibleX() {
		double value = this.xPosition+(this.currentWidth/this.xZoomFactor);
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	@Override
	public double getMinVisibleY() {
		double value = this.yPosition;
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	@Override
	public double getMaxVisibleY() {
		double value = this.yPosition+(this.currentHeight/this.yZoomFactor);
		//System.out.println(-1*this.viewParameters.get(1).translateDisplayValue(value));
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	@Override
	public double translateXDenotatorValue(double value) {
		value = this.viewParameters.get(0).translateDenotatorValue(value);
		return (value-this.xPosition)*this.xZoomFactor;
	}
	
	@Override
	public double translateYDenotatorValue(double value) {
		value = this.viewParameters.get(1).translateDenotatorValue(value);
		return this.currentHeight-((value-this.yPosition)*this.yZoomFactor);
	}
	
	@Override
	protected void updateNoteBounds() {
		if (this.notes != null) {
			this.notes.updateBounds(1, 1, 0, 0);
		}
	}

}
