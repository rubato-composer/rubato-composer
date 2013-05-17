package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.TemporaryDisplayMode;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.tools.ConnectorsTool;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;

public class BuildModulatorsAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private TemporaryDisplayMode mode;
	private ConnectorsTool modulatorsTool;
	
	public BuildModulatorsAdapter(ViewController controller, TemporaryDisplayMode mode) {
		this.controller = controller;
		this.mode = mode;
		this.modulatorsTool = new ConnectorsTool();
	}
	
	public void mouseClicked(MouseEvent event) {
		Point location = event.getPoint();
		DisplayObject noteInLocation = ((JBigBangDisplay)event.getSource()).getContents().getDisplayObjects().getNoteAt(location);
		if (noteInLocation != null) {
			this.controller.addSelectedNotesAsModulatorsTo(noteInLocation);
			this.mode.goBackToPreviousMode();
		}
	}
	
	public void mouseMoved(MouseEvent event) {
		this.modulatorsTool.setPoint(event.getPoint());
		this.controller.changeDisplayTool(this.modulatorsTool);
	}

}
