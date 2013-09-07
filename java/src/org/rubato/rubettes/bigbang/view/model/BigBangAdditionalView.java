package org.rubato.rubettes.bigbang.view.model;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class BigBangAdditionalView extends JDialog {
	
	public BigBangAdditionalView(BigBangController controller, Frame parentFrame) {
		//network name, rubette name, view count!!!
		super(parentFrame);
		BigBangView newView = new BigBangView(controller);
		this.setTitle("Big Bang - View "+controller.getViewCount(newView.getClass()));
		JPanel viewPanel = newView.getPanel();
		//this.setSize(viewPanel.getPreferredSize());
		this.add(viewPanel);
		this.addWindowListener(new AdditionalViewWindowAdapter(newView));
	}

}
