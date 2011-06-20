package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.RedoAction;
import org.rubato.rubettes.bigbang.view.controller.general.UndoAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.BuildModulatorsAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.BuildSatellitesAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.CopyToLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.CopyToNewLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.FlattenAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.MoveToLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.MoveToNewLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.NoteDeletionAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.RemoveModulatorsAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.ShowWindowPreferencesAction;
import org.rubato.rubettes.bigbang.view.model.LayerStates;

public class JBigBangPopupMenu extends JPopupMenu implements View {
	
	private JMenuItem undoItem;
	private JMenuItem redoItem;
	//private JMenuItem flattenCompletelyItem;
	private JMenu copyToMenu;
	private JMenu moveToMenu;
	private JMenuItem deleteItem;
	private JMenuItem buildSatellitesItem;
	private JMenuItem flattenItem;
	private JMenuItem buildModulatorItem;
	private JMenuItem removeModulatorItem;
	private ViewController controller;
	
	public JBigBangPopupMenu(ViewController controller) {
		controller.addView(this);
		this.controller = controller;
		this.initItems();
		this.add(this.undoItem);
	    this.add(this.redoItem);
	    this.add(new JSeparator());
	    this.add(this.copyToMenu);
	    this.add(this.moveToMenu);
	    this.add(this.deleteItem);
	    this.add(this.buildSatellitesItem);
	    this.add(this.flattenItem);
	    this.add(this.buildModulatorItem);
	    this.add(this.removeModulatorItem);
	    this.add(new JSeparator());
	    this.add(this.createShortcutItem("Preferences", KeyEvent.VK_P, new ShowWindowPreferencesAction(this.controller)));
	}
	
	private void initItems() {
		this.undoItem = this.createShortcutItem("Undo", KeyEvent.VK_Z, new UndoAction(this.controller));
		this.redoItem = this.createShortcutItem("Redo", KeyEvent.VK_Y, new RedoAction(this.controller));
		this.copyToMenu = this.createJLayerMenu("Copy To", new CopyToNewLayerAction(this.controller));
		this.moveToMenu = this.createJLayerMenu("Move To", new MoveToNewLayerAction(this.controller));
		this.deleteItem = this.createKeyItem("Delete", new NoteDeletionAction(this.controller), KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE);
		this.buildSatellitesItem = this.createShortcutItem("Build Satellites", KeyEvent.VK_PERIOD, new BuildSatellitesAction(this.controller));
		this.flattenItem = this.createShortcutItem("Flatten", KeyEvent.VK_COMMA, new FlattenAction(this.controller));
		this.buildModulatorItem = this.createShortcutItem("Build Modulators", KeyEvent.VK_M, new BuildModulatorsAction(this.controller));
		this.removeModulatorItem = this.createShortcutItem("Disconnect Modulators", KeyEvent.VK_R, new RemoveModulatorsAction(this.controller));
		this.enableEditItems(false);
	}
	
	private JMenu createJLayerMenu(String name, Action newLayerAction) {
		JMenu layerMenu = new JMenu(name);
		layerMenu.add(new JMenuItem(newLayerAction));
		layerMenu.addSeparator();
		return layerMenu;
	}
	
	private JMenuItem createKeyItem(String name, Action action, int... keys) {
		JMenuItem item = this.createItem(name, keys[0], 0, action);
		return item;
	}
	
	private JMenuItem createShortcutItem(String name, int shortcut, Action action) {
		int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		return this.createItem(name, shortcut, keyMask, action);
	}
	
	private JMenuItem createItem(String name, int shortcut, int keyMask, Action action) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(action);
		
		KeyStroke ks = KeyStroke.getKeyStroke(shortcut, keyMask);
		item.setAccelerator(ks);
	    return item;
	}
	
	public void setNoteMode(boolean noteMode) {
		//this.deleteItem.setVisible(noteMode);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.UNDO)) {
			this.updateUndoRedoItems(event);
		} else if (propertyName.equals(ViewController.REDO)) {
			this.updateUndoRedoItems(event);
		} else if (propertyName.equals(ViewController.NOTE_SELECTION)) {
			this.enableEditItems((Integer)event.getNewValue() > 0);
		} else if (propertyName.equals(ViewController.LAYERS)) {
			this.updateLayerMenus((LayerStates)event.getNewValue());
		}
	}
	
	private void updateUndoRedoItems(PropertyChangeEvent event) {
		UndoManager manager = (UndoManager)event.getNewValue();
		this.undoItem.setText(manager.getUndoPresentationName());
		this.undoItem.setEnabled(manager.canUndo());
		this.redoItem.setText(manager.getRedoPresentationName());
		this.redoItem.setEnabled(manager.canRedo());
	}
	
	private void enableEditItems(boolean enabled) {
		this.deleteItem.setEnabled(enabled);
		this.buildSatellitesItem.setEnabled(enabled);
		this.flattenItem.setEnabled(enabled);
	}
	
	private void updateLayerMenus(LayerStates states) {
		while (this.copyToMenu.getItemCount()-2 < states.size()) {
			int newIndex = this.copyToMenu.getItemCount()-2;
			this.copyToMenu.add(new JMenuItem(new CopyToLayerAction(this.controller, newIndex)));
			this.moveToMenu.add(new JMenuItem(new MoveToLayerAction(this.controller, newIndex)));
		}
		while (this.copyToMenu.getItemCount()-2 > states.size()) {
			int lastIndex = this.copyToMenu.getItemCount()-1;
			this.copyToMenu.remove(lastIndex);
			this.moveToMenu.remove(lastIndex);
		}
	}
	
	private void enableSoundMenus(boolean enabled) {
		this.buildModulatorItem.setEnabled(enabled);
		this.removeModulatorItem.setEnabled(enabled);
	}

}
