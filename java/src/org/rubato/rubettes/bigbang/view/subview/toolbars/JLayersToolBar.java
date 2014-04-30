package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangLayers;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.PlayButtonAction;
import org.rubato.rubettes.bigbang.view.controller.general.RecordButtonAction;
import org.rubato.rubettes.bigbang.view.controller.score.TempoListener;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;

public class JLayersToolBar extends JToolBar implements ActionListener, View {
	
	private BigBangController bbController;
	private ViewController controller;
	private JButton playButton, recordButton;
	private JCheckBox synthCheckBox, isLoopingCheckBox;
	private JSlider tempoSlider;
	private List<JLayerButton> layerButtons;
	
	public JLayersToolBar(BigBangController bbController, ViewController controller) {
		this.controller = controller;
		controller.addView(this);
		this.bbController = bbController;
		bbController.addView(this);
		this.playButton = new JButton(new PlayButtonAction(controller));
		this.recordButton = new JButton(new RecordButtonAction(controller));
		this.add(this.playButton);
		this.add(this.recordButton);
		this.synthCheckBox = new JCheckBox("Synth");
		this.synthCheckBox.addActionListener(this);
		this.add(this.synthCheckBox);
		this.isLoopingCheckBox = new JCheckBox("Loop");
		this.isLoopingCheckBox.addActionListener(this);
		this.add(this.isLoopingCheckBox);
		this.addTempoSlider();
		this.layerButtons = new ArrayList<JLayerButton>();
		this.setAlignmentX(LEFT_ALIGNMENT);
	}
	
	private void addTempoSlider() {
		this.tempoSlider = new JSlider(this.convertToSliderValue(BigBangPlayer.MIN_BPM), this.convertToSliderValue(BigBangPlayer.MAX_BPM));
		this.tempoSlider.addChangeListener(new TempoListener(this.controller));
		this.tempoSlider.setPaintTicks(true);
		this.tempoSlider.setPaintLabels(true);
		this.tempoSlider.setMaximumSize(new Dimension(270, 100));
		this.add(this.tempoSlider);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.LAYER_SELECTED)) {
			JLayerButton button = this.layerButtons.get((Integer)event.getNewValue());
			button.setSelected(!button.isSelected());
		} else if (propertyName.equals(BigBangController.LAYERS)) {
			this.updateLayerButtons(event);
		} else if (propertyName.equals(ViewController.PLAY_MODE)) {
			this.playButton.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.RECORD_MODE)) {
			this.recordButton.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.IS_LOOPING)) {
			this.isLoopingCheckBox.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.TEMPO)) {
			this.tempoSlider.setValue(this.convertToSliderValue((Integer)event.getNewValue()));
		}
	}
	
	private void updateLayerButtons(PropertyChangeEvent event) {
		BigBangLayers layers = (BigBangLayers)event.getNewValue();
		this.updatePanelCount(layers);
		for (int i = 0; i < this.layerButtons.size(); i++) {
			this.layerButtons.get(i).update(layers.get(i));
		}
		this.repaint();
	}
	
	private void updatePanelCount(BigBangLayers layers) {
		while (this.layerButtons.size() > layers.size()) {
			this.remove(this.layerButtons.remove(this.layerButtons.size()-1));
		}
		while (this.layerButtons.size() < layers.size()) {
			JLayerButton newPanel = new JLayerButton(this.layerButtons.size(), this.controller, this.bbController);
			this.layerButtons.add(newPanel);
			this.add(newPanel);
		}
	}
	
	private int convertToSliderValue(int value) {
		return (int)Math.round(Math.sqrt((value)));
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.synthCheckBox)) {
			this.controller.setSynthActive(this.synthCheckBox.isSelected());
		} else if (event.getSource().equals(this.isLoopingCheckBox)) {
			this.controller.setIsLooping(this.isLoopingCheckBox.isSelected());
		}
	}

}
