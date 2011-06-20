package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Color;

import javax.swing.JButton;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.LayerButtonAction;
import org.rubato.rubettes.bigbang.view.model.LayerState;

public class JLayerButton extends JButton {
	
	public JLayerButton(ViewController controller, int layerIndex) {
		super(new LayerButtonAction(controller, layerIndex));
	}
	
	public void setState(LayerState state) {
		Color foregroundColor = Color.red;
		if (state.equals(LayerState.active)) {
			foregroundColor = Color.green;
		} else if (state.equals(LayerState.inactive)) {
			foregroundColor = Color.orange;
		}
		this.setForeground(foregroundColor);
	}

}
