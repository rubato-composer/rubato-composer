package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.rubato.rubettes.bigbang.controller.AbstractBigBangAction;
import org.rubato.rubettes.bigbang.controller.BigBangController;

public class AlterationAction extends AbstractBigBangAction {

	public AlterationAction(BigBangController controller) {
		super("Alt", controller);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.controller.toggleAlterationMode();
		if (!((JButton) event.getSource()).isSelected()) {
			this.controller.endAlteration();
		}
	}

}
