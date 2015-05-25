package org.rubato.rubettes.bigbang.view.subview;

import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;

public class DisplayPlaybackLine {
	
	private DisplayContents display;
	private BigBangPlayer player;
	
	public DisplayPlaybackLine(DisplayContents display, BigBangPlayer player) {
		this.display = display;
		this.player = player;
	}
	
	public void paint(AbstractPainter painter) {
		if (this.player.isPlaying()) {
			painter.setColor(new float[]{0.78125f,0.78125f,0.78125f,0.78125f});
			int timeAxisIndex = this.display.getTimeAxisIndex();
			if (timeAxisIndex == 0) {
				double x = this.display.translateXDenotatorValue(this.player.getCurrentSymbolicTime());
				painter.drawLine(x, 0, x, this.display.getCurrentHeight()-1);
			} else if (timeAxisIndex == 1) {
				double y = this.display.translateYDenotatorValue(this.player.getCurrentSymbolicTime());
				painter.drawLine(0, y, this.display.getCurrentWidth()-1, y);
			}
		}
	}

}
