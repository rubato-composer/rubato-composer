package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class DeleteObjectsEdit extends AbstractPathBasedOperationEdit {
	
	public DeleteObjectsEdit(BigBangDenotatorManager denotatorManager, Set<BigBangObject> objectList) {
		super(denotatorManager, objectList);
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths(this.modifiedObjects);
		this.denotatorManager.removeObjects(new ArrayList<DenotatorPath>(objectPaths));
		return this.denotatorManager.getPathResults();
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Delete Objects";
	}

}
