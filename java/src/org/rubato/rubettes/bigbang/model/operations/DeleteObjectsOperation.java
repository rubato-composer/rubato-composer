package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public class DeleteObjectsOperation extends AbstractObjectBasedOperation {
	
	//used for cloning
	protected DeleteObjectsOperation(BigBangModel model, DeleteObjectsOperation other) {
		super(model, other);
	}
	
	public DeleteObjectsOperation(BigBangModel model, Set<BigBangObject> objects) {
		super(model, objects);
	}
	
	public DeleteObjectsOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		//TODO IMPLEMENT
	}
	
	public void addObjects(Set<BigBangObject> objects) {
		super.addObjects(objects);
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths();
		return this.model.getDenotatorManager().removeObjects(new ArrayList<DenotatorPath>(objectPaths));
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Delete Objects";
	}

}
