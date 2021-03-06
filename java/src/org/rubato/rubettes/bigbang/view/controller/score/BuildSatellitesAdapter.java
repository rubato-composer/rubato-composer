package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.TemporaryDisplayMode;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.tools.ConnectorsTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.Point2D;

public class BuildSatellitesAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private TemporaryDisplayMode mode;
	private ConnectorsTool satellitesTool;
	private int powerset;
	
	public BuildSatellitesAdapter(ViewController controller, TemporaryDisplayMode mode, int powerset) {
		this.controller = controller;
		this.mode = mode;
		this.satellitesTool = new ConnectorsTool();
		this.powerset = powerset;
	}
	
	public void mouseClicked(MouseEvent event) {
		Point2D location = new Point2D(event.getPoint().getX(), event.getPoint().getY());
		DisplayObject noteInLocation = ((JBigBangDisplay)event.getSource()).getContents().getDisplayObjects().getObjectAt(location);
		if (noteInLocation != null) {
			this.controller.addSelectedObjectsAsSatellitesTo(noteInLocation, this.powerset);
			this.mode.goBackToPreviousMode();
		}
	}
	
	public void mouseMoved(MouseEvent event) {
		this.satellitesTool.setPoint(event.getPoint());
		this.controller.changeDisplayTool(this.satellitesTool);
	}

}
