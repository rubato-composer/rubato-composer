package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.ListDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class SetOrAddCompositionEdit extends AbstractOperationEdit {
	
	private Denotator composition;
	private Denotator modifiedComposition;
	
	public SetOrAddCompositionEdit(BigBangModel model, Denotator composition) {
		super(model);
		this.composition = composition;
		this.isAnimatable = true;
		this.isSplittable = false;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.updateOperation();
	}
	
	public void setOrAddComposition(Denotator composition) {
		this.composition = composition;
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

}
