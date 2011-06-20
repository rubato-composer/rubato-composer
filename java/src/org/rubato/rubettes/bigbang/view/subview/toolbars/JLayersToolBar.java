package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.ModFilterButtonAction;
import org.rubato.rubettes.bigbang.view.controller.general.PlayButtonAction;
import org.rubato.rubettes.bigbang.view.controller.score.ModFilterSpinnersListener;
import org.rubato.rubettes.bigbang.view.controller.score.TempoListener;
import org.rubato.rubettes.bigbang.view.model.LayerState;
import org.rubato.rubettes.bigbang.view.model.LayerStates;

public class JLayersToolBar extends JToolBar implements View {
	
	private ViewController controller;
	private JButton playButton;
	private JSlider tempoSlider;
	private JButton modFilterButton;
	private List<JSpinner> modFilterSpinners;
	private List<JLayerButton> layerButtons;
	
	public JLayersToolBar(ViewController controller, BigBangController bbController) {
		this.controller = controller;
		controller.addView(this);
		bbController.addView(this);
		this.playButton = new JButton(new PlayButtonAction(bbController));
		this.add(this.playButton);
		this.addTempoSlider(bbController);
		this.addModFilterButtonAndSpinners();
		this.layerButtons = new ArrayList<JLayerButton>();
	}
	
	private void addTempoSlider(BigBangController bbController) {
		this.tempoSlider = new JSlider(BigBangPlayer.MIN_BPM, BigBangPlayer.MAX_BPM);
		this.tempoSlider.addChangeListener(new TempoListener(bbController));
		this.tempoSlider.setPaintTicks(true);
		this.tempoSlider.setPaintLabels(true);
		this.tempoSlider.setMaximumSize(new Dimension(270, 100));
		this.add(this.tempoSlider);
	}
	
	private void addModFilterButtonAndSpinners() {
		this.modFilterButton = new JButton(new ModFilterButtonAction(this.controller));
		this.add(this.modFilterButton);
		this.modFilterSpinners = new ArrayList<JSpinner>();
		this.addNewModFilterSpinner();
		this.addNewModFilterSpinner();
		ModFilterSpinnersListener listener = new ModFilterSpinnersListener(this.controller, this.modFilterSpinners);
		this.modFilterSpinners.get(0).addChangeListener(listener);
		this.modFilterSpinners.get(1).addChangeListener(listener);
	}
	
	private void addNewModFilterSpinner() {
		JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(0, -1, 10, 1));
		int sizeUnit = 25;
		newSpinner.setMaximumSize(new Dimension((int)(1.6*sizeUnit), sizeUnit-3));
		this.modFilterSpinners.add(newSpinner);
		this.add(newSpinner);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.LAYERS)) {
			this.updateLayerButtons(event);
		} else if (propertyName.equals(BigBangController.PLAY_MODE)) {
			this.playButton.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(BigBangController.TEMPO)) {
			this.tempoSlider.setValue((Integer)event.getNewValue());
		} else if (propertyName.equals(ViewController.TOGGLE_MOD_FILTER)) {
			this.modFilterButton.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.MOD_FILTER_VALUES)) {
			this.updateModFilterSpinners(event);
		}
	}
	
	private void updateModFilterSpinners(PropertyChangeEvent event) {
		int[] values = (int[])event.getNewValue();
		for (int i = 0; i < this.modFilterSpinners.size(); i++) {
			this.modFilterSpinners.get(i).setValue(values[i]);
		}
		this.repaint();
	}
	
	private void updateLayerButtons(PropertyChangeEvent event) {
		LayerStates states = (LayerStates)event.getNewValue();
		this.updateButtonCount(states);
		for (int i = 0; i < this.layerButtons.size(); i++) {
			LayerState currentState = states.get(i);
			this.layerButtons.get(i).setState(currentState);
		}
		this.repaint();
	}
	
	private void updateButtonCount(LayerStates states) {
		while (this.layerButtons.size() > states.size()) {
			JButton removedButton = this.layerButtons.remove(this.layerButtons.size()-1);
			this.remove(removedButton);
		}
		while (this.layerButtons.size() < states.size()) {
			JLayerButton newButton = new JLayerButton(this.controller, this.layerButtons.size());
			this.layerButtons.add(newButton);
			this.add(newButton);
		}
	}

}
