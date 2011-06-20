package org.rubato.rubettes.bigbang;

import javax.swing.JComponent;

import org.rubato.base.AbstractRubette;
import org.rubato.base.RubatoConstants;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.view.model.BigBangView;
import org.rubato.rubettes.bigbang.view.model.MTBigBangView;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class BigBangRubette extends AbstractRubette {
	
	//ban model from here!?
	private BigBangModel model;
	private BigBangView view;
	private BigBangController controller;
	
	/**
	 * Creates a basic BigBangRubette.
	 */
	public BigBangRubette() {
		//this.initLayeredForms();
        this.setInCount(1);
        this.setOutCount(1);
        this.controller = new BigBangController();
        //this.view = new BigBangView(this.controller);
        this.view = new MTBigBangView(this.controller);
        this.model = new BigBangModel(this.controller);
        this.model.setMultiTouch(false);
	}

	@Override
	public Rubette duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rubette fromXML(XMLReader reader, Element element) {
		BigBangRubette loadedRubette = new BigBangRubette();
		return loadedRubette;
	}

	@Override
	public String getName() {
		return "Big Bang";
	}
	
	/**
     * Returns the fact that BigBangRubette belongs to the core rubettes
     */
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

	@Override
	public void run(RunInfo runInfo) {
		Denotator output = this.model.getComposition();
		if (this.model.isInputActive()) {
			this.verifyAndSetInput();
		}
		this.setOutput(0, output);
	}
	
	private void verifyAndSetInput() {
		PowerDenotator input = (PowerDenotator) this.getInput(0);
		if (!this.model.setComposition(input)) {
			this.addError("Input denotator not of form Score or MacroScore.");
		}
	}
	
	public boolean hasView() {
		return true;
	}
	
	public JComponent getView() {
		return this.view.getPanel();
	}

	@Override
	public void toXML(XMLWriter writer) {
		// TODO Auto-generated method stub
		
	}

}
