package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class SetOrAddCompositionEdit extends AbstractOperationEdit {
	
	private Denotator composition;
	
	public SetOrAddCompositionEdit(BigBangDenotatorManager denotatorManager, Denotator composition) {
		super(denotatorManager);
		this.composition = composition;
		this.isAnimatable = false;
		this.isSplittable = false;
	}
	
	public OperationPathResults execute() {
		return this.denotatorManager.setOrAddComposition(this.composition);
	}
	
	public void setOrAddComposition(Denotator composition) {
		this.composition = composition;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		if (this.composition != null) {
			return "Input " + this.composition.getForm().getNameString();
		}
		return "Input";
	}

	@Override
	protected void updateOperation() {
		//do nothing for now
	}

}
