package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.BigBangRubette;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.model.edits.AlterationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.AddWindowAction;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.WallpaperRangeListener;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddWallpaperDimensionAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.StopWallpaperAction;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;

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
		if (propertyName.equals(ViewController.FORM)) {
			this.alterationPanel.updateCoordinateBoxes((DisplayObjects)event.getNewValue());
		} else if (propertyName.equals(ViewController.DISPLAY_MODE)) {
			this.selectModeButton(event);
		} else if (propertyName.equals(ViewController.SELECT_OPERATION)) {
			if (event.getNewValue() == null) {
				this.reset();
			} else if (event.getNewValue() instanceof AddWallpaperDimensionEdit) {
				AddWallpaperDimensionEdit edit = (AddWallpaperDimensionEdit)event.getNewValue();
				this.wallpaperDimensionSelected(edit.getRangeFrom(), edit.getRangeTo());
			} else if (event.getNewValue() instanceof AlterationEdit) {
				this.enterAlterationMode((AlterationEdit)event.getNewValue());
			}
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
	
	private void wallpaperDimensionSelected(int rangeFrom, int rangeTo) {
		this.resetWallpaperModeIfNecessary();
		this.startWallpaperButton.setSelected(true);
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, false));
		this.addNewRangeSpinner(new WallpaperRangeListener(this.viewController, true));
		//update values
		this.rangeSpinners.get(0).setValue(rangeFrom);
		this.rangeSpinners.get(1).setValue(rangeTo);
	}
	
	private void addNewRangeSpinner(ChangeListener listener) {
		JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
		int sizeUnit = this.startWallpaperButton.getHeight();
		newSpinner.setMaximumSize(new Dimension(2*sizeUnit, sizeUnit-3));
		newSpinner.addChangeListener(listener);
		this.rangeSpinners.add(newSpinner);
		this.add(newSpinner);
	}
	
	private void enterAlterationMode(AlterationEdit edit) {
		if (!Arrays.asList(this.getComponents()).contains(this.alterationPanel)) {
			this.alterationButton.setSelected(true);
			this.alterationPanel.updateValues(edit);
			this.add(this.alterationPanel);
			this.repaint();
		}
	}
	
	private void reset() {
		this.resetWallpaperModeIfNecessary();
		this.resetAlterationIfNecessary();
	}
	
	private void resetWallpaperModeIfNecessary() {
		if (this.startWallpaperButton.isSelected()) {
			this.startWallpaperButton.setSelected(false);
			for (JSpinner currentSpinner: this.rangeSpinners) {
				this.remove(currentSpinner);
				this.repaint();
			}
			this.rangeSpinners = new ArrayList<JSpinner>();
		}
	}
	
	private void resetAlterationIfNecessary() {
		if (this.alterationButton.isSelected()) {
			this.alterationButton.setSelected(false);
			this.remove(this.alterationPanel);
			this.repaint();
		}
	}

}
