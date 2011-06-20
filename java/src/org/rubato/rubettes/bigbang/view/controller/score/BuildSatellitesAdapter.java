package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.TemporaryDisplayMode;
import org.rubato.rubettes.bigbang.view.model.DisplayNote;
import org.rubato.rubettes.bigbang.view.model.tools.ConnectorsTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.util.NotePath;

public class BuildSatellitesAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private TemporaryDisplayMode mode;
	private ConnectorsTool satellitesTool;
	
	public BuildSatellitesAdapter(ViewController controller, TemporaryDisplayMode mode) {
		this.controller = controller;
		this.mode = mode;
		this.satellitesTool = new ConnectorsTool(NotePath.SATELLITE);
	}
	
	public void mouseClicked(MouseEvent event) {
		Point location = event.getPoint();
		DisplayNote noteInLocation = ((JBigBangDisplay)event.getSource()).getContents().getNotes().getNoteAt(location);
		if (noteInLocation != null) {
			this.controller.addSelectedNotesAsSatellitesTo(noteInLocation);
			this.mode.goBackToPreviousMode();
		}
	}
	
	public void mouseMoved(MouseEvent event) {
		this.satellitesTool.setPoint(event.getPoint());
		this.controller.changeDisplayTool(this.satellitesTool);
	}

}
