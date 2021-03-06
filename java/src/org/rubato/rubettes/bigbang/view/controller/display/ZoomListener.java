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
		int activeCoordinates = ZoomChange.BOTH;
		if (event.isAltDown()) {
			activeCoordinates = ZoomChange.X_ONLY;
		} else if (event.isShiftDown()) {
			activeCoordinates = ZoomChange.Y_ONLY;
		}
		ZoomChange zoomChange = new ZoomChange(event.getUnitsToScroll(), event.getX(), event.getY(), activeCoordinates);
		this.controller.changeZoomFactors(zoomChange);
	}

}
