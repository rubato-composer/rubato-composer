package org.rubato.rubettes.bigbang.view.controller.display;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.ZoomChange;

public class ZoomListener implements MouseWheelListener {
	
	private ViewController controller;
	
	public ZoomListener(ViewController controller) {
		this.controller = controller;
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		ZoomChange zoomChange = new ZoomChange(event.getUnitsToScroll(), event.getX(), event.getY());
		this.controller.changeZoomFactors(zoomChange);
	}

}
