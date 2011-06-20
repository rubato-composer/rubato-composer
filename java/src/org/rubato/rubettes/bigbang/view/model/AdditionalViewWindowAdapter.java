package org.rubato.rubettes.bigbang.view.model;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdditionalViewWindowAdapter extends WindowAdapter {
	
	private BigBangView view;
	
	public AdditionalViewWindowAdapter(BigBangView view) {
		this.view = view;
	}
	
	public void windowClosing(WindowEvent event) {
		this.view.dispose();
	}

}
