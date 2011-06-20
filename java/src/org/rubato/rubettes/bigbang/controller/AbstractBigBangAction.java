package org.rubato.rubettes.bigbang.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class AbstractBigBangAction extends AbstractAction {

	protected BigBangController controller;
	
	public AbstractBigBangAction(String name, BigBangController controller) {
		super(name);
		this.controller = controller;
	}
	
	public abstract void actionPerformed(ActionEvent event);

}
