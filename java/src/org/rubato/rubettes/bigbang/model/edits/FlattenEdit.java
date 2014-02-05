package org.rubato.rubettes.bigbang.model.edits;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class FlattenEdit extends AbstractPathBasedOperationEdit {
	
	public FlattenEdit(BigBangDenotatorManager denotatorManager, Set<BigBangObject> objectList) {
		super(denotatorManager, objectList);
	}
	
	@Override
	public OperationPathResults execute() {
		return this.denotatorManager.flattenObjects(this.getObjectPaths(this.modifiedObjects));
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Flatten";
	}

}
