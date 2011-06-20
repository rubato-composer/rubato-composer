package org.rubato.rubettes.bigbang.view.controller.display.multitouch;

import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomEvent;
import org.mt4j.util.camera.Icamera;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class BigBangZoomAction extends DefaultZoomAction {
	
	private ViewController controller;
	
	public BigBangZoomAction(ViewController controller) {
		this.controller = controller;
	}
	
	@Override
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof ZoomEvent){
			ZoomEvent ze = (ZoomEvent)g;
			//float oldDistance = this.getCameraDistance(ze.getCamera());
			super.processGestureEvent(g);
			float newDistance = this.getCameraDistance(ze.getCamera());		
			//float zoomFactor = oldDistance/newDistance;
			float zoomFactor = 1/(newDistance/665); //TODO: not literal 665!!!
			
			/*InputCursor cursor1 = ze.getFirstCursor();
			InputCursor cursor2 = ze.getSecondCursor();
			int x = Math.round((cursor1.getCurrentEvtPosX()+cursor2.getCurrentEvtPosX())/2);
			int y = Math.round((cursor1.getCurrentEvtPosY()+cursor2.getCurrentEvtPosY())/2);
			System.out.println(x + " " + y);
			this.controller.changeZoomFactors(new ZoomChange(zoomFactor, new Point(x, y)));*/
			this.controller.setZoomFactors(zoomFactor);
		}
		return false;
	}
	
	private float getCameraDistance(Icamera camera) {
		return camera.getPosition().getSubtracted(camera.getViewCenterPos()).length();
	}

}
