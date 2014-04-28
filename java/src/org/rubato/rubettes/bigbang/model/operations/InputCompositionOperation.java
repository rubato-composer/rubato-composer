package org.rubato.rubettes.bigbang.model.operations;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.ListDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.xml.XMLConstants;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class InputCompositionOperation extends AbstractOperation {
	
	private Denotator composition;
	private Denotator modifiedComposition;
	
	protected InputCompositionOperation(BigBangModel model, InputCompositionOperation other) {
		super(model);
		this.init(other.composition);
	}
	
	public InputCompositionOperation(BigBangModel model, Denotator composition) {
		super(model);
		this.init(composition);
	}
	
	public InputCompositionOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(reader, element);
	}
	
	private void init(Denotator composition) {
		this.isAnimatable = true;
		this.isSplittable = false;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.setOrAddComposition(composition);
	}
	
	public void setOrAddComposition(Denotator composition) {
		this.composition = composition;
		this.updateOperation();
	}

	/*
	 * if the toplevel denotator is a powerset or a list, adjust number of factors
	 */
	@Override
	protected void updateOperation() {
		if (this.composition instanceof PowerDenotator) {
			PowerDenotator clone = ((PowerDenotator)this.composition).copy();
			int modifiedNumberOfElements = (int)Math.round(clone.getFactorCount()*this.modificationRatio);
			try {
				clone.replaceFactors(clone.getFactors().subList(0, modifiedNumberOfElements));
			} catch (RubatoException e) { }
			this.modifiedComposition = clone;
		} else if (this.composition instanceof ListDenotator) {
			ListDenotator clone = ((ListDenotator)this.composition).copy();
			int modifiedNumberOfElements = (int)Math.round(clone.getFactorCount()*this.modificationRatio);
			try {
				clone.replaceFactors(clone.getFactors().subList(0, modifiedNumberOfElements));
			} catch (RubatoException e) { }
			this.modifiedComposition = clone; 
		} else {
			this.modifiedComposition = this.composition; 
		}
	}
	
	public OperationPathResults execute() {
		return this.model.getDenotatorManager().setOrAddComposition(this.modifiedComposition);
	}
	
	@Override
	protected String getSpecificPresentationName() {
		if (this.composition != null) {
			return "Input " + this.composition.getForm().getNameString();
		}
		return "Input";
	}
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		this.composition.toXML(writer);
	}
	
	private void fromXML(XMLReader reader, Element element) {
		this.setOrAddComposition(reader.parseDenotator(XMLReader.getChild(element, XMLConstants.DENOTATOR)));
	}

}
