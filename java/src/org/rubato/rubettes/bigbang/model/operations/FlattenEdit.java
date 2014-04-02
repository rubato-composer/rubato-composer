package org.rubato.rubettes.bigbang.model.operations;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class FlattenEdit extends AbstractPathBasedOperation {
	
	public FlattenEdit(BigBangModel model, Set<BigBangObject> objectList) {
		super(model, objectList);
	}
	
	@Override
	public OperationPathResults execute() {
		return this.model.getDenotatorManager().flattenObjects(this.getObjectPaths(this.modifiedObjects));
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Flatten";
	}

}
