package org.rubato.rubettes.bigbang.view.controller.score;

import java.util.List;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ModFilterSpinnersListener implements ChangeListener {
	
	private ViewController controller;
	private JSpinner modLevelSpinner, modNumberSpinner;
	
	public ModFilterSpinnersListener(ViewController controller, List<JSpinner> modFilterSpinners) {
		this.controller = controller;
		this.modLevelSpinner = modFilterSpinners.get(0);
		this.modNumberSpinner = modFilterSpinners.get(1);
	}

	public void stateChanged(ChangeEvent e) {
		this.controller.changeModFilter((Integer)this.modLevelSpinner.getValue(), (Integer)this.modNumberSpinner.getValue());
	}

}
