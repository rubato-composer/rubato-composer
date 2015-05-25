package org.rubato.rubettes.bigbang.view.model;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdditionalViewWindowAdapter extends WindowAdapter {
	
	private BigBangSwingView view;
	
	public AdditionalViewWindowAdapter(BigBangSwingView view) {
		this.view = view;
	}
	
	public void windowClosing(WindowEvent event) {
		this.view.dispose();
	}

}
