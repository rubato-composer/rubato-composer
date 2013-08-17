package org.rubato.rubettes.bigbang.view.subview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.controller.ChangeOctaveAction;
import org.rubato.rubettes.bigbang.view.controller.KeyToMidiAction;
import org.rubato.rubettes.bigbang.view.controller.ToggleMainOptionsAction;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JLayersToolBar;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JMainToolBar;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JGraphToolBar;

public class JBigBangPanel extends JPanel {
	
	private JMainOptionsPanel mainOptionsPanel;
	private JBigBangDisplay display;
	private int currentOctave;
	
	public JBigBangPanel(ViewController controller, BigBangController bbController, ViewParameters viewParameters, BigBangPlayer player) {
		this.setLayout(new BorderLayout());
		this.add(this.createToolBarsPanel(controller, bbController), BorderLayout.NORTH);
		this.display = new JBigBangDisplay(controller, player);
		this.initMenuComponents(this.display, controller, bbController, viewParameters);
		this.add(this.display, BorderLayout.CENTER);
		this.add(this.makeButtonPanel(controller), BorderLayout.EAST);
		new JWindowPreferencesDialog(controller);
		JBigBangPopupMenu popup = new JBigBangPopupMenu(controller);
		this.setComponentPopupMenu(popup);
		this.initMidiKeys(controller);
	}
	
	private JPanel createToolBarsPanel(ViewController controller, BigBangController bbController) {
		JPanel toolBarsPanel = new JPanel();
		toolBarsPanel.setLayout(new GridLayout(3, 1));
		toolBarsPanel.add(new JMainToolBar(controller, bbController));
		toolBarsPanel.add(new JLayersToolBar(controller));
		toolBarsPanel.add(new JGraphToolBar(controller, bbController));
		return toolBarsPanel;
	}
	
	private void initMenuComponents(JBigBangDisplay display, ViewController controller, BigBangController bbController, ViewParameters viewParameters) {
		display.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.mainOptionsPanel = new JMainOptionsPanel(controller, bbController, viewParameters);
		display.add(this.mainOptionsPanel);
	}
	
	private JPanel makeButtonPanel(ViewController controller) {
		JPanel buttonPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
        buttonPanel.setLayout(layout);

        buttonPanel.add(this.makeMenuButton(this.mainOptionsPanel, new ToggleMainOptionsAction(controller), layout));
		//buttonPanel.add(this.makeMenuButton(this.viewParametersScrollPane, new ToggleViewParametersAction(controller), layout));
		return buttonPanel;
	}
	
	private JButton makeMenuButton(JComponent menu, AbstractAction action, GridBagLayout layout) {
		JButton menuButton = new JButton(action);
		int height = (int) menu.getPreferredSize().getHeight();
		menuButton.setPreferredSize(new Dimension(20, height));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weighty = height;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		layout.setConstraints(menuButton, constraints);
		return menuButton;
	}
	
	public void toggleTimedRepaint() {
		this.display.toggleTimedRepaint();
	}
	
	private void initMidiKeys(ViewController controller) {
		char[] midiKeys = new char[]{'A','W','S','E','D','F','T','G','Z','H','U','J','K','O','L','P','Ö'};
		int currentMidiValue = 60;
		for (char currentKey : midiKeys) {
			this.addKeyToMidiActions(controller, currentKey, currentMidiValue);
			currentMidiValue++;
		}
		this.addChangeOctaveActions(controller);
		this.currentOctave = 0;
	}
	
	private void addKeyToMidiActions(ViewController controller, char key, int pitch) {
		String pressedString = "pressed " + key;
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(pressedString), pressedString);
		this.getActionMap().put(pressedString, new KeyToMidiAction(this, controller, pitch, true));
		String releasedString = "released " + key;
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(releasedString), releasedString);
		this.getActionMap().put(releasedString, new KeyToMidiAction(this, controller, pitch, false));
	}
	
	private void addChangeOctaveActions(ViewController controller) {
		String pressedString = "pressed Y";
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(pressedString), pressedString);
		this.getActionMap().put(pressedString, new ChangeOctaveAction(this, controller, false));
		pressedString = "pressed X"; 
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(pressedString), pressedString);
		this.getActionMap().put(pressedString, new ChangeOctaveAction(this, controller, true));
	}
	
	public void changeOctave(boolean up) {
		if (up) {
			this.currentOctave++;
		} else {
			this.currentOctave--;
		}
	}
	
	public int getCurrentOctave() {
		return this.currentOctave;
	}

}
