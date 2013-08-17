package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.TranslationTool;

public class ObjectTranslationAdapter extends ObjectTransformationAdapter {
	
	public ObjectTranslationAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectTranslationAdapter(ViewController controller, double[] startingPoint, double[] endingPoint) {
		super(controller, startingPoint, endingPoint);
	}

	/*@Override
	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.isTouchingObjects = ((JBigBangDisplay)event.getSource()).getContents().getDisplayObjects().hasSelectedObjectAt(event.getPoint());
		}
	}*/
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean inPreviewMode) {
		//if (this.isTouchingObjects) {
			boolean copyAndTranslate = event.isAltDown();
			Point2D.Double currentEndPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			this.controller.translateSelectedObjects(this.startingPoint, currentEndPoint, copyAndTranslate, inPreviewMode);
		//}
	}
	
	@Override
	protected void initDisplayTool() {
		this.displayTool = new TranslationTool();
	}

}
