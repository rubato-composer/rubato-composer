package org.rubato.rubettes.bigbang.view.subview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.io.BigBangMidiReceiver;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;

public class JWindowPreferencesDialog extends JDialog implements View, ItemListener {
	
	private ViewController controller;
	private JPanel viewParametersPanel;
	private JComboBox waveform, midiIn, midiOut;
	private JCheckBox multiTouch;
	
	public JWindowPreferencesDialog(ViewController controller) {
		this.controller = controller;
		this.controller.addView(this);
		this.setTitle("Preferences");
		this.setLayout(new BorderLayout());
		this.initViewParametersPanel();
		this.initControlsPanel();
		this.initSoundPanel();
		this.setAlwaysOnTop(true);
		this.pack();
	}
	
	private void initViewParametersPanel() {
		this.viewParametersPanel = new JPanel();
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		this.viewParametersPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "View parameters"));
		this.add(BorderLayout.NORTH, this.viewParametersPanel);
	}
	
	private void initSoundPanel() {
		JPanel soundPanel = new JPanel();
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		soundPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Sound/Midi"));
		soundPanel.setLayout(new BoxLayout(soundPanel, BoxLayout.Y_AXIS));
		soundPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.midiIn = this.addLabeledJComboBoxPanel(soundPanel, "Midi input", BigBangMidiReceiver.getMidiInDeviceNames());
		this.midiOut = this.addLabeledJComboBoxPanel(soundPanel, "Midi output", BigBangPlayer.getMidiOutDeviceNames());
		this.waveform = this.addLabeledJComboBoxPanel(soundPanel, "Waveform", BigBangPlayer.WAVEFORMS);
		this.add(BorderLayout.CENTER, soundPanel);
	}
	
	private JComboBox addLabeledJComboBoxPanel(JPanel parentPanel, String labelString, Object[] options) {
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.add(new JLabel(labelString));
		JComboBox comboBox = new JComboBox();
		if (options != null) {
			comboBox = new JComboBox(options);
		}
		comboBox.addItemListener(this);
		comboBoxPanel.add(comboBox);
		parentPanel.add(comboBoxPanel);
		comboBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return comboBox;
	}
	
	private void initControlsPanel() {
		JPanel controlsPanel = new JPanel();
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		controlsPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Controls"));
		controlsPanel.add(new JLabel("Multi-touch"));
		this.multiTouch = new JCheckBox();
		this.multiTouch.addItemListener(this);
		controlsPanel.add(this.multiTouch);
		this.add(BorderLayout.SOUTH, controlsPanel);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.SHOW_WINDOW_PREFERENCES)) {
			this.setVisible(true);
		} else if (propertyName.equals(ViewController.VIEW_PARAMETERS)) {
			this.updateViewParametersPanel((ViewParameters)event.getNewValue());
		} else if (propertyName.equals(ViewController.MIDI_IN)) {
			this.midiIn.setSelectedItem(event.getNewValue());
		} else if (propertyName.equals(ViewController.MIDI_OUT)) {
			this.midiOut.setSelectedItem(event.getNewValue());
		} else if (propertyName.equals(ViewController.WAVEFORM)) {
			this.waveform.setSelectedItem(event.getNewValue());
		} else if (propertyName.equals(BigBangController.MULTITOUCH)) {
			this.multiTouch.setSelected((Boolean)event.getNewValue());
		}
	}
	
	private void updateViewParametersPanel(ViewParameters parameters) {
		this.updateViewParameterCount(parameters);
		for (int i = 0; i < parameters.size(); i++) {
			JViewParameterPanel currentPanel = (JViewParameterPanel)this.viewParametersPanel.getComponent(i);
			currentPanel.updateValues(parameters.get(i));
		}
	}
	
	private void updateViewParameterCount(ViewParameters parameters) {
		this.viewParametersPanel.setLayout(new GridLayout(parameters.size(), 1));
		while (parameters.size() > this.viewParametersPanel.getComponentCount()) {
			this.viewParametersPanel.add(new JViewParameterPanel(this.viewParametersPanel.getComponentCount(), this.controller));
		}
		while (parameters.size() < this.viewParametersPanel.getComponentCount()) {
			this.viewParametersPanel.remove(this.viewParametersPanel.getComponentCount()-1);
		}
		this.pack();
	}
	
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source == this.midiIn) {
			this.controller.setMidiIn((String)this.midiIn.getSelectedItem());
		} else if (source == this.midiOut) {
			this.controller.setMidiOut((String)this.midiOut.getSelectedItem());
		} else if (source == this.waveform) {
			this.controller.changeWaveform(this.waveform.getSelectedItem());
		}/* else if (source == this.multiTouch) {
			this.bbController.setMultiTouch(this.multiTouch.isSelected());
		}*/
	}

}
