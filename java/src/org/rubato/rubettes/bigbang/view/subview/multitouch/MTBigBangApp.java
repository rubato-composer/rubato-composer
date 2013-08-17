package org.rubato.rubettes.bigbang.view.subview.multitouch;

import java.awt.Point;
import java.beans.PropertyChangeEvent;

import org.mt4j.MTApplication;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.multitouch.MTDisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;

public class MTBigBangApp extends MTApplication implements View {
	
	private static final long serialVersionUID = 1L;
	
	private ViewController controller;
	private DisplayContents contents;
	private BigBangScene scene;
	private MTPainter painter;
	private final int REPAINTS_TO_SKIP = 50;
	private int repaintNumber;
	private MTDisplayModeAdapter modeAdapter;
	
	public MTBigBangApp(ViewController controller, BigBangPlayer player) {
		this.controller = controller;
		this.controller.addView(this);
		this.contents = new MTDisplayContents(player);
		this.repaintNumber = 0;
	}
	
	@Override
	public void startUp() {
		this.scene = new BigBangScene(this, this.controller);
		this.addScene(this.scene);
		this.painter = new MTPainter(this.scene.getMTApplication(), this.scene.getCanvas(), this.scene.getLassoProcessor());
		this.repaintAxesAndTool();
	}
	
	public void repaint(boolean force) {
		if (force || (this.repaintNumber == 0 && this.scene != null)) {
			this.repaintAxesAndTool();
			this.repaintNotes();
		}
		this.repaintNumber = (this.repaintNumber+1)%this.REPAINTS_TO_SKIP;
    }
	
	private void repaintNotes() {
		if (this.scene != null) {
			this.scene.getNoteLayer().removeAllChildren();
			this.painter.setComponent(this.scene.getNoteLayer());
			this.contents.paintNotes(this.painter);
		}
    }
	
	private void repaintAxesAndTool() {
		this.scene.getAxesLayer().removeAllChildren();
		MTApplication mtApp = this.scene.getMTApplication();
		this.painter.setComponent(this.scene.getAxesLayer());
		this.contents.setCurrentSize(mtApp.width, mtApp.height);
		this.contents.paintAxes(this.painter);
		this.contents.paintTool(this.painter);
	}
	
	private void setModeAdapter(MTDisplayModeAdapter adapter) {
		if (this.modeAdapter != null) {
			this.modeAdapter.removeFrom(this.scene.getCanvas());
		}
		this.modeAdapter = adapter;
		this.modeAdapter.addTo(this.scene.getCanvas());
	}
	
	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.DISPLAY_MODE)) {
			this.setModeAdapter((MTDisplayModeAdapter)event.getNewValue());
		} else if (propertyName.equals(ViewController.VIEW_PARAMETERS)) {
			this.contents.setViewParameters((ViewParameters)event.getNewValue());
			this.repaint();
		} else if (propertyName.equals(ViewController.DISPLAY_OBJECTS)) {
			this.contents.setNotes((DisplayObjects)event.getNewValue());
			this.repaint(false);
		} else if (propertyName.equals(ViewController.DISPLAY_TOOL)) {
			this.contents.setTool((DisplayTool)event.getNewValue());
			this.repaint(event.getNewValue() == null);
		} else if (propertyName.equals(ViewController.ZOOM_FACTORS)) {
			this.contents.setZoomFactors((double[])event.getNewValue());
			this.repaint(false);
		} else if (propertyName.equals(ViewController.DISPLAY_POSITION)) {
			this.contents.setPosition((Point)event.getNewValue());
			this.repaint(false);
		} else if (propertyName.equals(ViewController.SELECTED_VIEW_PARAMETERS)) {
			this.contents.updateNoteBounds();
			this.repaint();
		} else if (propertyName.equals(ViewController.OBJECT_SELECTION)) {
			this.repaint(false);
		} else if (propertyName.equals(ViewController.ANCHOR_OBJECT_SELECTION)) {
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
