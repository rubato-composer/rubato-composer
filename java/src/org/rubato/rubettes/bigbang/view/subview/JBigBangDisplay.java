package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;

public class JBigBangDisplay extends JPanel implements View {
	
	private DisplayContents contents;
	private DisplayModeAdapter modeAdapter;
	
	public JBigBangDisplay(ViewController controller) {
		controller.addView(this);
		this.contents = new DisplayContents();
		JBigBangPopupMenu popup = new JBigBangPopupMenu(controller);
		this.setComponentPopupMenu(popup);
	}
	
	public DisplayContents getContents() {
		return this.contents;
	}
	
	private void setModeAdapter(DisplayModeAdapter adapter) {
		if (this.modeAdapter != null) {
			this.modeAdapter.removeFrom(this);
		}
		this.modeAdapter = adapter;
		this.modeAdapter.addTo(this);
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.contents.paint(new AWTPainter(g), this.getWidth(), this.getHeight());
    }
	
	public Dimension getPreferredSize() {
        return new Dimension(920, 575);
    }

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.DISPLAY_MODE)) {
			this.setModeAdapter((DisplayModeAdapter)event.getNewValue());
		} else if (propertyName.equals(ViewController.VIEW_PARAMETERS)) {
			this.contents.setViewParameters((ViewParameters)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.DISPLAY_NOTES)) {
			this.contents.setNotes((DisplayNoteList)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.DISPLAY_TOOL)) {
			this.contents.setTool((DisplayTool)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.ZOOM_FACTORS)) {
			this.contents.setZoomFactors((double[])event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.DISPLAY_POSITION)) {
			this.contents.setPosition((Point)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.SELECTED_VIEW_PARAMETERS)) {
			this.contents.setSelectedViewParameters((int[])event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.NOTE_SELECTION)) {
			this.repaint();
		} else if (propertyName.equals(ViewController.ANCHOR_NOTE_SELECTION)) {
			this.repaint();
		} else if (propertyName.equals(ViewController.SATELLITES_CONNECTED)) {
			this.contents.setSatellitesConnected((Boolean)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.LAYERS)) {
			this.repaint();
		} else if (propertyName.equals(ViewController.TOGGLE_MOD_FILTER)) {
			this.repaint();
		} else if (propertyName.equals(ViewController.MOD_FILTER_VALUES)) {
			this.repaint();
		}
	}

}
