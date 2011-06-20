package org.rubato.rubettes.bigbang.model;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoAdaptor implements UndoableEditListener {
	
	private UndoManager manager;
	
	public UndoAdaptor(UndoManager manager) {
		this.manager = manager;
	}
	
	public void undoableEditHappened(UndoableEditEvent event) {
		this.manager.addEdit(event.getEdit());
	}

}
