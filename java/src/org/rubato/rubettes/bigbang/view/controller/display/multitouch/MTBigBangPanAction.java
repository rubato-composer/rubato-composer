package org.rubato.rubettes.bigbang.view.controller.display.multitouch;

import java.awt.Point;

import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanEvent;
import org.mt4j.util.math.Vector3D;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class MTBigBangPanAction extends DefaultPanAction {
	
	private ViewController controller;
	
	public MTBigBangPanAction(ViewController controller) {
		this.controller = controller;
	}
	
	@Override
	public boolean processGestureEvent(MTGestureEvent g) {
		super.processGestureEvent(g);
		if (g instanceof PanEvent){
			Vector3D position = ((PanEvent)g).getCamera().getViewCenterPos();
			int x = Math.round(position.getX());
			int y = Math.round(position.getY());
			this.controller.setDisplayPosition(new Point(x, y));
		}
		return false;
	}

}
