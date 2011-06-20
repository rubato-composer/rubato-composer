package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class AlterationDegreeListener implements ChangeListener {
	
	private BigBangController controller;
	private boolean isStartDegree;
	
	public AlterationDegreeListener(BigBangController controller, boolean isStartDegree) {
		this.controller = controller;
		this.isStartDegree = isStartDegree;
	}

	public void stateChanged(ChangeEvent e) {
		JSlider degreeSlider = (JSlider)e.getSource();
		double value = degreeSlider.getValue();
		double totalValues = degreeSlider.getMaximum()-degreeSlider.getMinimum();
		if (this.isStartDegree) {
			this.controller.changeAlterationStartDegree(value/totalValues);
		} else {
			this.controller.changeAlterationEndDegree(value/totalValues);
		}
	}

}
