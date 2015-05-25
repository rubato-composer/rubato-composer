package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.TranslationTool;
import org.rubato.rubettes.util.Point2D;

public class ObjectTranslationAdapter extends ObjectTransformationAdapter {
	
	public ObjectTranslationAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectTranslationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		super(controller, startingPoint, endingPoint);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		Point2D currentEndPoint = new Point2D(event.getPoint().x, event.getPoint().y);
		this.controller.modifyEndPointOfSelectedTransformation(currentEndPoint);
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		//if (this.isTouchingObjects) {
			boolean copyAndTranslate = event.isAltDown();
			Point2D currentEndPoint = new Point2D(event.getPoint().x, event.getPoint().y);
			this.controller.translateSelectedObjects(this.startingPoint, currentEndPoint, copyAndTranslate, startNewTransformation);
		//}
	}
	
	@Override
	protected void initDisplayTool() {
		this.displayTool = new TranslationTool();
	}

}
