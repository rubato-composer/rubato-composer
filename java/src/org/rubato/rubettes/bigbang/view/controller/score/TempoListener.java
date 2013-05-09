package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class TempoListener implements ChangeListener {
	
	private ViewController controller;
	
	public TempoListener(ViewController controller) {
		this.controller = controller;
	}
	
	public void stateChanged(ChangeEvent e) {
		JSlider tempoSlider = (JSlider)e.getSource();
		int newTempo = (int)Math.round(Math.pow(tempoSlider.getValue(), 2));
		this.controller.setTempo(newTempo);
	}

}
