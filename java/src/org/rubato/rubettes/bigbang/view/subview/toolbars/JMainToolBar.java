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

import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.AddWindowAction;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.WallpaperRangeListener;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddWallpaperDimensionAction;
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
		this.viewController.addView(this);
		this.bbController = bbController;
		this.bbController.addView(this);
		this.initModeButtons();
		this.rangeSpinners = new ArrayList<JSpinner>();
		this.alterationPanel = new JAlterationPanel(viewController, bbController);
	}
	
	private void initModeButtons() {
		if (BigBangRubette.IS_MULTITOUCH) {
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
		this.add(new JButton(new StopWallpaperAction(this.bbController)));
		this.add(this.alterationButton);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.DISPLAY_MODE)) {
			this.selectModeButton(event);
		} /*else if (propertyName.equals(BigBangController.WALLPAPER)) {
			this.updateWallpaper((BigBangWallpaper)event.getNewValue());
		} else if (propertyName.equals(BigBangController.END_WALLPAPER)) {
			this.endWallpaper();
		} */else if (propertyName.equals(ViewController.SELECT_OPERATION)) {
			if (event.getNewValue() == null) {
				this.resetRangeSpinners();
			} else if (event.getNewValue() instanceof AddWallpaperDimensionEdit) {
				this.wallpaperDimensionSelected((AddWallpaperDimensionEdit)event.getNewValue());
			}
		} else if (propertyName.equals(BigBangController.ENTER_ALTERATION_MODE)) {
			this.alterationButton.setSelected(true);
			this.add(this.alterationPanel);
			this.repaint();
		} else if (propertyName.equals(BigBangController.EXIT_ALTERATION_MODE)) {
			this.alterationButton.setSelected(false);
			this.remove(this.alterationPanel);
			this.repaint();
		} /*else if (propertyName.equals(BigBangController.MULTITOUCH)) {
			this.initModeButtons((Boolean)event.getNewValue());
			this.repaint();
		}*/
	}
	
	private void selectModeButton(PropertyChangeEvent event) {
		DisplayModeAdapter mode = (DisplayModeAdapter)event.getNewValue();
		for (JDisplayModeButton currentModeButton: this.modeButtons) {
			Class<? extends DisplayModeAdapter> adapterClass = currentModeButton.getAdapter().getClass();
			currentModeButton.setSelected(adapterClass.isInstance(mode));
		}
	}
	
	private void wallpaperDimensionSelected(AddWallpaperDimensionEdit edit) {
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, false));
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, true));
		//update values
		this.rangeSpinners.get(0).setValue(edit.getRangeFrom());
		this.rangeSpinners.get(1).setValue(edit.getRangeTo());
	}
	
	/*private void updateWallpaper(BigBangWallpaper wallpaper) {
		this.startWallpaperButton.setSelected(true);
		this.modeButtons.enableSelectionAndDrawingModes(false);
		this.updateRangeSpinners(wallpaper);
	}
	
	private void endWallpaper() {
		this.startWallpaperButton.setSelected(false);
		this.modeButtons.enableSelectionAndDrawingModes(true);
		this.resetRangeSpinners();
	}*/
	
	private void resetRangeSpinners() {
		for (JSpinner currentSpinner: this.rangeSpinners) {
			this.remove(currentSpinner);
			this.repaint();
		}
		this.rangeSpinners = new ArrayList<JSpinner>();
	}
	
	/*private void updateRangeSpinners(BigBangWallpaper wallpaper) {
		//add spinners if not enough
		while (wallpaper.getDimensions().size() > this.rangeSpinners.size()/2) {
			int nextDimension = this.rangeSpinners.size()/2;
			this.addNewRangeSpinner(new WallpaperRangeListener(this.bbController, nextDimension, false));
			this.addNewRangeSpinner(new WallpaperRangeListener(this.bbController, nextDimension, true));
		}
		//update values
		for (int i = 0; i < wallpaper.getDimensions().size(); i++) {
			BigBangWallpaperDimension currentDimension = wallpaper.getDimensions().get(i);
			this.rangeSpinners.get(i*2).setValue(currentDimension.getRangeFrom());
			this.rangeSpinners.get(i*2+1).setValue(currentDimension.getRangeTo());
		}
	}*/
	
	private void addNewRangeSpinner(ChangeListener listener) {
		JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
		int sizeUnit = this.startWallpaperButton.getHeight();
		newSpinner.setMaximumSize(new Dimension(2*sizeUnit, sizeUnit-3));
		newSpinner.addChangeListener(listener);
		this.rangeSpinners.add(newSpinner);
		this.add(newSpinner);
	}

}
