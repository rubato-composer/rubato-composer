package org.rubato.rubettes.bigbang.view.controller.mode.multitouch;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;

public class MTDisplayModeAdapter extends DisplayModeAdapter {
	
	protected List<AbstractComponentProcessor> inputProcessors;
	protected List<IGestureEventListener> gestureListeners;
	
	public MTDisplayModeAdapter(ViewController controller) {
		super(controller);
		this.inputProcessors = new ArrayList<AbstractComponentProcessor>();
		this.gestureListeners = new ArrayList<IGestureEventListener>();
	}
	
	public void addProcessorAndListener(AbstractComponentProcessor processor, IGestureEventListener listener) {
		this.inputProcessors.add(processor);
		this.gestureListeners.add(listener);
	}
	
	public void addTo(MTCanvas canvas) {
		for (int i = 0; i < this.inputProcessors.size(); i++) {
			canvas.registerInputProcessor(this.inputProcessors.get(i));
			canvas.addGestureListener(this.inputProcessors.get(i).getClass(), this.gestureListeners.get(i));
		}
	}
	
	public void removeFrom(MTCanvas canvas) {
		canvas.unregisterAllInputProcessors();
		canvas.removeAllGestureEventListeners();
	}

}
