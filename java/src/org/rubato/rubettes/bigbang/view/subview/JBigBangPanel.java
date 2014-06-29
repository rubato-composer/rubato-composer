package org.rubato.rubettes.bigbang.view.subview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.InputEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.controller.ChangeOctaveAction;
import org.rubato.rubettes.bigbang.view.controller.KeyToMidiAction;
import org.rubato.rubettes.bigbang.view.controller.KeyToStateAction;
import org.rubato.rubettes.bigbang.view.controller.ToggleMainOptionsAction;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.RedoAction;
import org.rubato.rubettes.bigbang.view.controller.general.UndoAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.DeleteObjectsAction;
import org.rubato.rubettes.bigbang.view.controller.score.actions.ShowWindowPreferencesAction;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.subview.graph.JGraphPanel;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JLayersToolBar;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JMainToolBar;
import org.rubato.rubettes.util.PointND;

public class JBigBangPanel extends JPanel {
	
	public static final int CENTER_PANEL_HEIGHT = 675;
	
	private JMainOptionsPanel mainOptionsPanel;
	private JBigBangDisplay display;
	private int currentOctave;
	
	public JBigBangPanel(ViewController controller, BigBangController bbController, ViewParameters viewParameters, BigBangPlayer player) {
		this.setLayout(new BorderLayout());
		this.add(this.createToolBarsPanel(controller, bbController), BorderLayout.NORTH);
		this.display = new JBigBangDisplay(bbController, controller, player);
		this.initMenuComponents(this.display, controller, bbController, viewParameters);
		this.add(this.display, BorderLayout.CENTER);
		this.add(this.makeButtonPanel(controller), BorderLayout.EAST);
		this.add(new JGraphPanel(controller, bbController), BorderLayout.WEST);
		new JWindowPreferencesDialog(controller);
		JBigBangPopupMenu popup = new JBigBangPopupMenu(bbController, controller);
		this.setComponentPopupMenu(popup);
		this.initMidiKeys(controller);
		this.initStateKeys(controller);
		this.initShortcuts(controller);
		this.setFocusable(true);
	}
	
	private JPanel createToolBarsPanel(ViewController controller, BigBangController bbController) {
		JPanel toolBarsPanel = new JPanel();
		toolBarsPanel.setLayout(new GridLayout(2, 1));
		toolBarsPanel.add(new JMainToolBar(controller, bbController));
		toolBarsPanel.add(new JLayersToolBar(bbController, controller));
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
	
	public Point getDisplayPosition() {
		return this.display.getContents().getPosition();
	}
	
	public double[] getXYDisplayValues(double[] denotatorValues) {
		return this.display.getContents().getXYDisplayValues(denotatorValues);
	}
	
	public double[] getXYZDenotatorValues(PointND location) {
		return this.display.getContents().getXYZDenotatorValues(location);
	}
	
	public double getDenotatorValue(double displayValue, int parameterIndex) {
		return this.display.getContents().getDenotatorValue(displayValue, parameterIndex);
	}
	
	public void toggleTimedRepaint() {
		this.display.toggleTimedRepaint();
	}
	
	private void initMidiKeys(ViewController controller) {
		char[] midiKeys = new char[]{'A','W','S','E','D','F','T','G','Z','H','U','J','K','O','L','P'};
		int currentMidiValue = 60;
		for (char currentKey : midiKeys) {
			this.addKeyToMidiActions(controller, currentKey, currentMidiValue);
			currentMidiValue++;
		}
		this.addChangeOctaveActions(controller);
		this.currentOctave = 0;
	}
	
	private void initStateKeys(ViewController controller) {
		for (int currentStateIndex = 0; currentStateIndex <= 9; currentStateIndex++) {
			this.addKeyToStateAction(controller, (char)(currentStateIndex+48), currentStateIndex);
		}
		this.addChangeOctaveActions(controller);
		this.currentOctave = 0;
	}
	
	private void initShortcuts(ViewController controller) {
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('Z', InputEvent.META_DOWN_MASK), "META_Z");
		this.getActionMap().put("META_Z", new UndoAction(controller));
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('Y', InputEvent.META_DOWN_MASK), "META_Y");
		this.getActionMap().put("META_Y", new RedoAction(controller));
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('P', InputEvent.META_DOWN_MASK), "META_P");
		this.getActionMap().put("META_P", new ShowWindowPreferencesAction(controller));
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("BACK_SPACE"), "BACK_SPACE");
		this.getActionMap().put("BACK_SPACE", new DeleteObjectsAction(controller));
	}
	
	private void addKeyToMidiActions(ViewController controller, char key, int pitch) {
		String pressedString = "pressed " + key;
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(pressedString), pressedString);
		this.getActionMap().put(pressedString, new KeyToMidiAction(this, controller, pitch, true));
		String releasedString = "released " + key;
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(releasedString), releasedString);
		this.getActionMap().put(releasedString, new KeyToMidiAction(this, controller, pitch, false));
	}
	
	private void addKeyToStateAction(ViewController controller, char key, int stateIndex) {
		String pressedString = "pressed " + key;
		this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(pressedString), pressedString);
		this.getActionMap().put(pressedString, new KeyToStateAction(controller, stateIndex));
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
