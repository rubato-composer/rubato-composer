package org.rubato.rubettes.bigbang.model.operations;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public class FlattenOperation extends AbstractObjectBasedOperation {
	
	//used for cloning
	protected FlattenOperation(BigBangModel model, FlattenOperation other) {
		super(model, other);
	}
	
	public FlattenOperation(BigBangModel model, Set<BigBangObject> objectList) {
		super(model, objectList);
	}
	
	public FlattenOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		//TODO IMPLEMENT
	}
	
	@Override
	public OperationPathResults execute() {
		return this.model.getDenotatorManager().flattenObjects(this.getObjectPaths());
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Flatten";
	}

}
