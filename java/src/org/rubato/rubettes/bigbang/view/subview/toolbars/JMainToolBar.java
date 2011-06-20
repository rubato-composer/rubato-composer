package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.WallpaperRangeListener;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddWallpaperDimensionAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddWindowAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.StopWallpaperAction;

public class JMainToolBar extends JToolBar implements View {
	
	private ViewController viewController;
	private BigBangController bbController;
	private BasicModeButtons modeButtons;
	private JButton startWallpaperButton;
	private List<JSpinner> rangeSpinners;
	private JButton alterationButton;
	private JAlterationPanel alterationPanel;
	
	public JMainToolBar(ViewController viewController, BigBangController bbController) {
		this.viewController = viewController;
		this.bbController = bbController;
		this.viewController.addView(this);
		this.bbController.addView(this);
		this.initModeButtons(true);
		this.rangeSpinners = new ArrayList<JSpinner>();
		this.alterationPanel = new JAlterationPanel(viewController, bbController);
	}
	
	private void initModeButtons(boolean multiTouch) {
		if (multiTouch) {
			this.modeButtons = new MultiTouchModeButtons(this.viewController);
		} else {
			this.modeButtons = new MouseModeButtons(this.viewController);
		}
		this.startWallpaperButton = new JButton(new AddWallpaperDimensionAction(this.viewController));
		this.alterationButton = new JAlterationModeButton(this.viewController, this.bbController);
		this.resetButtons();
	}
	
	private void resetButtons() {
		this.removeAll();
		this.add(new JButton(new AddWindowAction(this.viewController)));
		for (JButton currentModeButton: this.modeButtons) {
			this.add(currentModeButton);
		}
		this.add(this.startWallpaperButton);
		this.add(new JButton(new StopWallpaperAction(this.viewController)));
		this.add(this.alterationButton);
	}

	@SuppressWarnings("unchecked")
    public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.DISPLAY_MODE)) {
			this.selectModeButton(event);
		} else if (propertyName.equals(ViewController.START_WALLPAPER)) {
			this.startWallpaperButton.setSelected(true);
			this.addNewRangeSpinners();
			this.modeButtons.enableSelectionAndDrawingModes(false);
		} else if (propertyName.equals(ViewController.ADD_WP_DIMENSION)) {
			this.addNewRangeSpinners();
		} else if (propertyName.equals(ViewController.END_WALLPAPER)) {
			this.startWallpaperButton.setSelected(false);
			this.modeButtons.enableSelectionAndDrawingModes(true);
			this.resetRangeSpinners();
		} else if (propertyName.equals(ViewController.RANGE)) {
			this.setRanges((List<Integer>)event.getNewValue());
		} else if (propertyName.equals(BigBangController.ENTER_ALTERATION_MODE)) {
			this.alterationButton.setSelected(true);
			this.add(this.alterationPanel);
			this.repaint();
		} else if (propertyName.equals(BigBangController.EXIT_ALTERATION_MODE)) {
			this.alterationButton.setSelected(false);
			this.remove(this.alterationPanel);
			this.repaint();
		} else if (propertyName.equals(BigBangController.MULTITOUCH)) {
			this.initModeButtons((Boolean)event.getNewValue());
			this.repaint();
		}
	}
	
	private void selectModeButton(PropertyChangeEvent event) {
		DisplayModeAdapter mode = (DisplayModeAdapter)event.getNewValue();
		for (JDisplayModeButton currentModeButton: this.modeButtons) {
			Class<? extends DisplayModeAdapter> adapterClass = currentModeButton.getAdapter().getClass();
			currentModeButton.setSelected(adapterClass.isInstance(mode));
		}
	}
	
	private void resetRangeSpinners() {
		for (JSpinner currentSpinner: this.rangeSpinners) {
			this.remove(currentSpinner);
			this.repaint();
		}
		this.rangeSpinners = new ArrayList<JSpinner>();
	}
	
	private void addNewRangeSpinners() {
		int nextDimension = this.rangeSpinners.size()/2+1;
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, nextDimension, false));
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, nextDimension, true));
	}
	
	private void addNewRangeSpinner(ChangeListener listener) {
		JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
		int sizeUnit = this.startWallpaperButton.getHeight();
		newSpinner.setMaximumSize(new Dimension(2*sizeUnit, sizeUnit-3));
		newSpinner.addChangeListener(listener);
		this.rangeSpinners.add(newSpinner);
		this.add(newSpinner);
	}
	
	private void setRanges(List<Integer> ranges) {
		for (int i = 0; i < this.rangeSpinners.size(); i++) {
			this.rangeSpinners.get(i).setValue(ranges.get(i));
		}
	}

}
