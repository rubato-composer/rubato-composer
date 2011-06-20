package org.rubato.rubettes.bigbang.view.subview.toolbars;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.NavigationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.actions.AlterationAction;

public class JAlterationModeButton extends JDisplayModeButton {

	public JAlterationModeButton(ViewController viewController, BigBangController bbController) {
		super(viewController, "Alt", new NavigationModeAdapter(viewController));
		this.addActionListener(new AlterationAction(bbController));
	}

}
