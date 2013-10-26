package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AlterationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.AlterationCompositionSelectionMode;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.TemporaryDisplayMode;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;

public class JAlterationPanel extends JPanel implements View, ActionListener, ItemListener, ChangeListener {
	
	private final int SLIDER_MAX = 100;
	private List<JButton> compositionButtons;
	private JSlider startSlider, endSlider;
	private JPanel coordinateBoxes;
	private ViewController viewController;
	private BigBangController bbController;
	private TemporaryDisplayMode selectionMode;
	private AlterationEdit edit;
	
	public JAlterationPanel(ViewController viewController, BigBangController bbController) {
		viewController.addView(this);
		bbController.addView(this);
		this.viewController = viewController;
		this.bbController = bbController;
		this.compositionButtons = new ArrayList<JButton>();
		this.addCompositionButton();
		this.addCompositionButton();
		this.addCoordinateBoxes();
		this.addDegreeSliders();
	}
	
	private void addCompositionButton() {
		JButton newButton = new JButton("Co"+this.compositionButtons.size());
		newButton.addActionListener(this);
		this.add(newButton);
		this.compositionButtons.add(newButton);
	}
	
	public void addCoordinateBoxes() {
		this.coordinateBoxes = new JPanel();
		this.add(this.coordinateBoxes);
	}
	
	public void updateCoordinateBoxes(DisplayObjects displayObjects) {
		this.coordinateBoxes.removeAll();
		for (int i = 0; i < displayObjects.getNumberOfNonAnalyticalCoordinateSystemValues(); i++) {
			JCheckBox currentBox = new JCheckBox();
			currentBox.addItemListener(this);
			this.coordinateBoxes.add(currentBox);
		}
		this.repaint();
	}
	
	private void addDegreeSliders() {
		this.startSlider = new JSlider(0,this.SLIDER_MAX,0);
		this.startSlider.addChangeListener(this);
		this.add(this.startSlider);
		this.endSlider = new JSlider(0,this.SLIDER_MAX,0);
		this.endSlider.addChangeListener(this);
		this.add(this.endSlider);
	}

	@SuppressWarnings("unchecked")
	//TODO get rid of all this!!!!!!
    public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.ALTERATION_START_DEGREE)) {
			this.updateSliderValue(this.startSlider, (Double)event.getNewValue());
		} else if (propertyName.equals(BigBangController.ALTERATION_END_DEGREE)) {
			this.updateSliderValue(this.endSlider, (Double)event.getNewValue());
		} else if (propertyName.equals(BigBangController.FIRE_ALTERATION_COMPOSITION)) {
			this.selectCompositionButton((Integer)event.getNewValue());
		} else if (propertyName.equals(BigBangController.ALTERATION_COORDINATES)) {
			this.updateCoordinateSelections((List<Integer>)event.getNewValue());
		}
	}
	
	public void setEdit(AlterationEdit edit) {
		this.edit = edit;
		//TODO MAKE POSSIBLE AGAIN this.updateCoordinateSelections(edit.getAlterationCoordinates());
		this.updateSliderValue(this.startSlider, edit.getStartDegree());
		this.updateSliderValue(this.endSlider, edit.getEndDegree());
	}
	
	private void updateSliderValue(JSlider slider, double value) {
		slider.setValue((int)Math.round(value*this.SLIDER_MAX));
	}
	
	private void selectCompositionButton(int index) {
		if (index == -1) {
			this.goBackToPreviousDisplayMode();
		} else {
			this.goToSelectionMode();
		}
		for (JButton currentButton: this.compositionButtons) {
			currentButton.setSelected(this.compositionButtons.indexOf(currentButton) == index);
		}
	}
	
	private void goToSelectionMode() {
		if (this.selectionMode == null) {
			this.selectionMode = new AlterationCompositionSelectionMode(this.viewController);
			this.viewController.changeDisplayMode(this.selectionMode);
		}
	}
	
	private void goBackToPreviousDisplayMode() {
		if (this.selectionMode != null) {
			this.selectionMode.goBackToPreviousMode();
			this.selectionMode = null;
		}
	}

	public void actionPerformed(ActionEvent event) {
		int buttonIndex = this.compositionButtons.indexOf(event.getSource());
		int oldSelectionIndex = this.selectedCompositionButtonIndex();
		if (oldSelectionIndex != -1) {
			this.viewController.changeAlterationComposition(oldSelectionIndex);
		}
		if (buttonIndex == oldSelectionIndex) {
			this.edit.fireAlterationComposition(-1);
		} else {
			this.edit.fireAlterationComposition(buttonIndex);
		}
	}
	
	public void stateChanged(ChangeEvent event) {
		JSlider degreeSlider = (JSlider)event.getSource();
		double value = degreeSlider.getValue();
		double totalValues = degreeSlider.getMaximum()-degreeSlider.getMinimum();
		if (degreeSlider.equals(this.startSlider)) {
			this.edit.setStartDegree(value/totalValues);
		} else {
			this.edit.setEndDegree(value/totalValues);
		}
		this.bbController.modifiedOperation(false);
	}
	
	public void itemStateChanged(ItemEvent event) {
		List<Integer> selectedCoordinates = new ArrayList<Integer>();
		for (int i = 0; i < this.coordinateBoxes.getComponentCount(); i++) {
			if (((JCheckBox)this.coordinateBoxes.getComponent(i)).isSelected()) {
				selectedCoordinates.add(i);
			}
		}
		this.viewController.setAlterationCoordinates(selectedCoordinates);
	}
	
	private int selectedCompositionButtonIndex() {
		for (JButton currentButton: this.compositionButtons) {
			if (currentButton.isSelected()) {
				return this.compositionButtons.indexOf(currentButton);
			}
		}
		return -1;
	}
	
	public void updateCoordinateSelections(List<Integer> selectedCoordinates) {
		for (int i = 0; i < this.coordinateBoxes.getComponentCount(); i++) {
			((JCheckBox)this.coordinateBoxes.getComponent(i)).setSelected(selectedCoordinates.contains(i));
		}
	}

}
