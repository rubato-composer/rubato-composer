package org.rubato.rubettes.bigbang.view.subview.multitouch;

import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;

public class MTDisplayContents extends DisplayContents {
	
	public MTDisplayContents(BigBangPlayer player) {
		super(player);
	}
	
	@Override
	public double getMinVisibleX() {
		return this.viewParameters.get(0).translateDisplayValue(this.position.getX());
	}
	
	@Override
	public double getMaxVisibleX() {
		double value = this.position.getX()+(this.currentWidth/this.xZoomFactor);
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	@Override
	public double getMinVisibleY() {
		double value = this.position.getY();
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	@Override
	public double getMaxVisibleY() {
		double value = this.position.getY()+(this.currentHeight/this.yZoomFactor);
		//System.out.println(-1*this.viewParameters.get(1).translateDisplayValue(value));
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	@Override
	public double translateXDenotatorValue(double value) {
		value = this.viewParameters.get(0).translateDenotatorValue(value);
		return (value-this.position.getX())*this.xZoomFactor;
	}
	
	@Override
	public double translateYDenotatorValue(double value) {
		value = this.viewParameters.get(1).translateDenotatorValue(value);
		return this.currentHeight-((value-this.position.getY())*this.yZoomFactor);
	}
	
	@Override
	public void updateNoteBounds() {
		if (this.displayObjects != null) {
			this.displayObjects.updateBounds(1, 1, 0, 0);
		}
	}

}
