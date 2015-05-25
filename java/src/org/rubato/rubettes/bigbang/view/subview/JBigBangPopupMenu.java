package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import org.rubato.rubettes.bigbang.model.undo.UndoManager;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangLayers;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.RedoAction;
import org.rubato.rubettes.bigbang.view.controller.general.UndoAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.BuildSatellitesAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddToLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AddToNewLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.FlattenAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.MoveToLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.MoveToNewLayerAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.DeleteObjectsAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.ShowWindowPreferencesAction;

public class JBigBangPopupMenu extends JPopupMenu implements View {
	
	private JMenuItem undoItem;
	private JMenuItem redoItem;
	//private JMenuItem flattenCompletelyItem;
	private JMenu addToMenu;
	private JMenu moveToMenu;
	private JMenuItem deleteItem;
	private JMenu buildSatellitesMenu;
	private JMenuItem flattenItem;
	private boolean satellitesAllowed;
	private ViewController controller;
	
	public JBigBangPopupMenu(BigBangController bbController, ViewController controller) {
		bbController.addView(this);
		controller.addView(this);
		this.controller = controller;
		this.initItems();
		this.add(this.undoItem);
	    this.add(this.redoItem);
	    this.add(new JSeparator());
	    this.add(this.addToMenu);
	    this.add(this.moveToMenu);
	    this.add(this.deleteItem);
	    this.add(this.buildSatellitesMenu);
	    this.add(this.flattenItem);
	    //this.add(this.buildModulatorItem);
	    //this.add(this.removeModulatorItem);
	    this.add(new JSeparator());
	    this.add(this.createShortcutItem("Preferences", KeyEvent.VK_P, new ShowWindowPreferencesAction(this.controller)));
	}
	
	private void initItems() {
		this.undoItem = this.createShortcutItem("Undo", KeyEvent.VK_Z, new UndoAction(this.controller));
		this.redoItem = this.createShortcutItem("Redo", KeyEvent.VK_Y, new RedoAction(this.controller));
		this.addToMenu = this.createJLayerMenu("Add to", new AddToNewLayerAction(this.controller));
		this.moveToMenu = this.createJLayerMenu("Move to", new MoveToNewLayerAction(this.controller));
		this.deleteItem = this.createKeyItem("Delete", new DeleteObjectsAction(this.controller), KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE);
		this.buildSatellitesMenu = new JMenu("Build satellites");
		this.flattenItem = this.createShortcutItem("Flatten", KeyEvent.VK_COMMA, new FlattenAction(this.controller));
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
		if (propertyName.equals(ViewController.FORM)) {
			DisplayObjects displayObjects = (DisplayObjects)event.getNewValue();
			this.allowSatelliteItems(displayObjects.baseFormAllowsForSatellites());
			this.updateSatelliteMenu(displayObjects.getObjectTypes());
		} else if (propertyName.equals(ViewController.UNDO)) {
			this.updateUndoRedoItems(event);
		} else if (propertyName.equals(ViewController.REDO)) {
			this.updateUndoRedoItems(event);
		} else if (propertyName.equals(ViewController.OBJECT_SELECTION)) {
			this.enableEditItems((Integer)event.getNewValue() > 0);
		} else if (propertyName.equals(BigBangController.LAYERS)) {
			this.updateLayerMenus((BigBangLayers)event.getNewValue());
		}
	}
	
	private void updateUndoRedoItems(PropertyChangeEvent event) {
		UndoManager manager = (UndoManager)event.getNewValue();
		this.undoItem.setText(manager.getUndoPresentationName());
		this.undoItem.setEnabled(manager.canUndo());
		this.redoItem.setText(manager.getRedoPresentationName());
		this.redoItem.setEnabled(manager.canRedo());
	}
	
	private void updateSatelliteMenu(List<Form> satelliteObjectForms) {
		this.buildSatellitesMenu.removeAll();
		for (int i = 0; i < satelliteObjectForms.size(); i++) {
			Form currentObjectForm = satelliteObjectForms.get(i);
			this.buildSatellitesMenu.add(new JMenuItem(new BuildSatellitesAction(this.controller, currentObjectForm.getNameString(), i))); 
		}
	}
	
	private void enableEditItems(boolean enabled) {
		this.deleteItem.setEnabled(enabled);
		if (this.satellitesAllowed || enabled == false) {
			this.buildSatellitesMenu.setEnabled(enabled);
			this.flattenItem.setEnabled(enabled);
		}
	}
	
	private void allowSatelliteItems(boolean allowed) {
		this.satellitesAllowed = allowed;
	}
	
	private void updateLayerMenus(BigBangLayers layers) {
		while (this.addToMenu.getItemCount()-2 < layers.size()) {
			int newIndex = this.addToMenu.getItemCount()-2;
			this.addToMenu.add(new JMenuItem(new AddToLayerAction(this.controller, newIndex)));
			this.moveToMenu.add(new JMenuItem(new MoveToLayerAction(this.controller, newIndex)));
		}
		while (this.addToMenu.getItemCount()-2 > layers.size()) {
			int lastIndex = this.addToMenu.getItemCount()-1;
			this.addToMenu.remove(lastIndex);
			this.moveToMenu.remove(lastIndex);
		}
	}
	
	/*private void enableSoundMenus(boolean enabled) {
		this.buildModulatorItem.setEnabled(enabled);
		this.removeModulatorItem.setEnabled(enabled);
	}*/

}
