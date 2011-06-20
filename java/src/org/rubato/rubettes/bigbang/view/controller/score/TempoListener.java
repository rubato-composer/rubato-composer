package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class TempoListener implements ChangeListener {
	
	private BigBangController controller;
	
	public TempoListener(BigBangController controller) {
		this.controller = controller;
	}
	
	public void stateChanged(ChangeEvent e) {
		JSlider tempoSlider = (JSlider)e.getSource();
		int newTempo = tempoSlider.getValue();
		this.controller.setTempo(newTempo);
	}

}
