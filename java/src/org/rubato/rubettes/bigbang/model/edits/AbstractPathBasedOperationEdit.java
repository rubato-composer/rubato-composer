package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractPathBasedOperationEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> objectPaths;
	protected List<DenotatorPath> modifiedObjectPaths;
	
	public AbstractPathBasedOperationEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths) {
		super(scoreManager);
		this.objectPaths = objectPaths;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.updateOperation();
	}
	
	//adjusts the number of objects to be handled according to this.modificationRatio
	protected void updateOperation() {
		this.modifiedObjectPaths = new ArrayList<DenotatorPath>();
		int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*this.objectPaths.size());
		for (int i = 0; i < modifiedNumberOfObjects; i++) {
			this.modifiedObjectPaths.add(this.objectPaths.get(i));
		}
	}

}
