package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class SetOrAddCompositionEdit extends AbstractOperationEdit {
	
	private Denotator composition;
	
	public SetOrAddCompositionEdit(BigBangModel model, Denotator composition) {
		super(model);
		this.composition = composition;
		this.isAnimatable = false;
		this.isSplittable = false;
	}
	
	public OperationPathResults execute() {
		return this.model.getDenotatorManager().setOrAddComposition(this.composition);
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
