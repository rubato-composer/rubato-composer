package org.rubato.rubettes.bigbang.view.subview.toolbars;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DrawingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.NavigationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ReflectionModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.RotationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ScalingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.SelectionModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ShapingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ShearingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.TranslationModeAdapter;

public class MultiTouchModeButtons extends BasicModeButtons {

	public MultiTouchModeButtons(ViewController viewController) {
		super(viewController);
	}

	@Override
	protected void initModeButtons() {
		/*this.add(new JDisplayModeButton(this.viewController, "Nav", new MTNavigationModeAdapter(this.viewController)));
		this.drawingModeButton = new JDisplayModeButton(this.viewController, "Dra", new MTDrawingModeAdapter(this.viewController));
		this.add(this.drawingModeButton);
		this.add(new JDisplayModeButton(this.viewController, "Gen", new MTGeneralModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Sma", new MTSmartModeAdapter(this.viewController)));
		this.add(new JDisplayModeButton(this.viewController, "Ref", new MTReflectionModeAdapter(this.viewController)));
		*/this.add(new JDisplayModeButton(this.viewController, "Sha", new ShapingModeAdapter(this.viewController)));
	}

}
