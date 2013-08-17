package org.rubato.rubettes.bigbang.view.controller.mode.multitouch;

import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.display.multitouch.BigBangPanAction;
import org.rubato.rubettes.bigbang.view.controller.display.multitouch.BigBangZoomAction;
import org.rubato.rubettes.bigbang.view.controller.score.multitouch.MTObjectSelectionAdapter;
import org.rubato.rubettes.bigbang.view.subview.multitouch.BigBangScene;

public class MTNavigationModeAdapter extends MTDisplayModeAdapter {
	
	public MTNavigationModeAdapter(ViewController controller, BigBangScene scene) {
		super(controller);
		
		//add background zoom
		this.addProcessorAndListener(new ZoomProcessor(scene.getMTApplication()), new BigBangZoomAction(controller));
		
		//add background pan
		this.addProcessorAndListener(new PanProcessorTwoFingers(scene.getMTApplication()), new BigBangPanAction(controller));
		
		//add lasso
		//this.addProcessorAndListener(scene.getLassoProcessor(), new DefaultLassoAction(scene.getMTApplication(), scene.getCanvas().getClusterManager(), scene.getCanvas()));
		
		//add note selector for now...
		this.addProcessorAndListener(scene.getLassoProcessor(), new MTObjectSelectionAdapter(controller));
	}

}
