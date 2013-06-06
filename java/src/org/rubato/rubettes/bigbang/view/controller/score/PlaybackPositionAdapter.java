package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class PlaybackPositionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	
	public PlaybackPositionAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !event.isPopupTrigger()) {
			Point location = event.getPoint();
			this.controller.setPlaybackPosition(new Point2D.Double(location.x, location.y));
		}
	}

}
