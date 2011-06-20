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

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.controller.ToggleMainOptionsAction;
import org.rubato.rubettes.bigbang.view.controller.ToggleViewParametersAction;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JLayersToolBar;
import org.rubato.rubettes.bigbang.view.subview.toolbars.JMainToolBar;

public class JBigBangPanel extends JPanel {
	
	private JMainOptionsPanel mainOptionsPanel;
	private JViewParametersTable viewParametersTable;
	
	public JBigBangPanel(ViewController controller, BigBangController bbController) {
		this.setLayout(new BorderLayout());
		this.add(this.createToolBarsPanel(controller, bbController), BorderLayout.NORTH);
		JBigBangDisplay display = new JBigBangDisplay(controller);
		this.initMenuComponents(display, controller);
		this.add(display, BorderLayout.CENTER);
		this.add(this.makeButtonPanel(controller), BorderLayout.EAST);
		new JWindowPreferencesDialog(controller, bbController);
		JBigBangPopupMenu popup = new JBigBangPopupMenu(controller);
		this.setComponentPopupMenu(popup);
	}
	
	private JPanel createToolBarsPanel(ViewController controller, BigBangController bbController) {
		JPanel toolBarsPanel = new JPanel();
		toolBarsPanel.setLayout(new GridLayout(2, 1));
		toolBarsPanel.add(new JMainToolBar(controller, bbController));
		toolBarsPanel.add(new JLayersToolBar(controller, bbController));
		return toolBarsPanel;
	}
	
	private void initMenuComponents(JBigBangDisplay display, ViewController controller) {
		display.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.mainOptionsPanel = new JMainOptionsPanel(controller);
		controller.addView(this.mainOptionsPanel);
		display.add(this.mainOptionsPanel);
		this.viewParametersTable = new JViewParametersTable(controller);
		controller.addView(this.viewParametersTable);
		//display.add(this.viewParametersTable.getTableHeader());
		display.add(this.viewParametersTable);
	}
	
	private JPanel makeButtonPanel(ViewController controller) {
		JPanel buttonPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
        buttonPanel.setLayout(layout);

        buttonPanel.add(this.makeMenuButton(this.mainOptionsPanel, new ToggleMainOptionsAction(controller), layout));
		buttonPanel.add(this.makeMenuButton(this.viewParametersTable, new ToggleViewParametersAction(controller), layout));
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

}
