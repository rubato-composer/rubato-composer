package org.rubato.rubettes.bigbang.model.denotators;

import java.util.List;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangManipulator {
	
	protected BigBangDenotatorManager denotatorManager;
	protected List<TransformationPaths> transformationPaths;
	
	public BigBangManipulator(BigBangDenotatorManager denotatorManager, List<TransformationPaths> transformationPaths) {
		this.denotatorManager = denotatorManager;
		this.transformationPaths = transformationPaths;
	}

}
