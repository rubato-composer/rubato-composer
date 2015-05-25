package org.rubato.rubettes.bigbang.view.controller.score.multitouch;

import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoEvent;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.util.Point2D;

public class MTObjectSelectionAdapter implements IGestureEventListener {
	
	private ViewController controller;
	private SelectionTool selectionTool;
	private Point2D startingPoint;
	
	public MTObjectSelectionAdapter(ViewController controller) {
		this.controller = controller;
	}

	public boolean processGestureEvent(MTGestureEvent ge) {
		if (ge instanceof LassoEvent) {
			LassoEvent event = (LassoEvent)ge;
			Point2D finger = this.getPoint(event.getCursor());
			if (event.getId() == MTGestureEvent.GESTURE_ENDED) {
				this.updateSelection(finger, false);
			} else if (event.getId() == MTGestureEvent.GESTURE_DETECTED) {
				this.startingPoint = finger;
				this.selectionTool = new SelectionTool();
				this.selectionTool.setStartingPoint(this.startingPoint);
			} else {
				this.updateSelection(finger, true);
			}
		}
		return false;
	}
	
	private Point2D getPoint(InputCursor cursor) {
		return new Point2D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
	}
	
	private void updateSelection(Point2D finger, boolean stillSelecting) {
		if (this.startingPoint != null) {
			this.selectionTool.setEndingPoint(finger);
			this.controller.selectObjects(this.selectionTool, stillSelecting);
			if (!stillSelecting) {
				this.startingPoint = null;
			}
		}
	}
	
}