package org.rubato.rubettes.bigbang.view.controller;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class AnimationPositionListener implements ChangeListener {
	
	private BigBangController controller;
	
	public AnimationPositionListener(BigBangController controller) {
		this.controller = controller;
	}
	
	public void stateChanged(ChangeEvent e) {
		JSlider animationSlider = (JSlider)e.getSource(); 
		double position = ((double)animationSlider.getValue())/(animationSlider.getMaximum()-animationSlider.getMinimum());
		this.controller.setAnimationPosition(position);
	}

}
