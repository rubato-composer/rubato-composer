package org.rubato.rubettes.bigbang.view.subview.toolbars;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.AffineModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.DrawingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.NavigationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ReflectionModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.RotationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ScalingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.SelectionModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ShapingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ShearingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.TranslationModeAdapter;

public class MouseModeButtons extends BasicModeButtons {
	
	private JDisplayModeButton selectionModeButton;
	
	public MouseModeButtons(ViewController viewController) {
		super(viewController);
	}
	
	@Override
	protected void initModeButtons() {
		this.add(new JDisplayModeButton(this.viewController, "Nav", new NavigationModeAdapter(this.viewController)));
		this.selectionModeButton = new JDisplayModeButton(this.viewController, "Sel", new SelectionModeAdapter(this.viewController));
		this.add(this.selectionModeButton);
		this.drawingModeButton = new JDisplayModeButton(this.viewController, "Dra", new DrawingModeAdapter(this.viewController));
		this.add(this.drawingModeButton);
		this.add(new JDisplayModeButton(this.viewController, "Tra", new TranslationModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Rot", new RotationModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Sca", new ScalingModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Ref", new ReflectionModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "She", new ShearingModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Aff", new AffineModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Sha", new ShapingModeAdapter(this.viewController)));
	}
	
	@Override
	public void enableSelectionAndDrawingModes(boolean enabled) {
		super.enableSelectionAndDrawingModes(enabled);
		this.selectionModeButton.setEnabled(enabled);
	}

}
