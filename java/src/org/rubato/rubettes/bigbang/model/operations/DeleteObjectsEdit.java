package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class DeleteObjectsEdit extends AbstractPathBasedOperation {
	
	public DeleteObjectsEdit(BigBangModel model, Set<BigBangObject> objectList) {
		super(model, objectList);
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths(this.modifiedObjects);
		this.model.getDenotatorManager().removeObjects(new ArrayList<DenotatorPath>(objectPaths));
		return this.model.getDenotatorManager().getPathResults();
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Delete Objects";
	}

}
