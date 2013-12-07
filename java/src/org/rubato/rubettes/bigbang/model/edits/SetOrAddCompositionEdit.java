package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class SetOrAddCompositionEdit extends AbstractOperationEdit {
	
	private Denotator composition;
	
	public SetOrAddCompositionEdit(BigBangScoreManager scoreManager, Denotator composition) {
		super(scoreManager);
		this.composition = composition;
		this.isAnimatable = false;
		this.isSplittable = false;
	}
	
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		this.scoreManager.setOrAddComposition(this.composition);
		return pathDifferences;
	}
	
	public void setComposition(Denotator composition) {
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
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing
	}

	@Override
	protected void updateOperation() {
		//do nothing for now
	}

}
