package org.rubato.rubettes.bigbang.view.controller.mode.multitouch;

import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.multitouch.MTNoteTransformationAdapter;
import org.rubato.rubettes.bigbang.view.subview.multitouch.BigBangScene;

public class MTTransformationModeAdapter extends MTDisplayModeAdapter {
	
	public MTTransformationModeAdapter(ViewController controller, BigBangScene scene) {
		super(controller);
		this.addProcessorAndListener(new ZoomProcessor(scene.getMTApplication()), new MTNoteTransformationAdapter(controller));
	}

}
