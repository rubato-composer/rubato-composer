package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public abstract class AbstractViewAction extends AbstractAction {

	protected ViewController controller;
	
	public AbstractViewAction(String name, ViewController controller) {
		super(name);
		this.controller = controller;
	}
	
	public abstract void actionPerformed(ActionEvent event);

}
