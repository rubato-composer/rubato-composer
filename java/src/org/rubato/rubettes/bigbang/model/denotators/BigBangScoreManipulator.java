package org.rubato.rubettes.bigbang.model.denotators;

import java.util.List;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangDenotatorManager denotatorManager;
	protected List<TransformationPaths> transformationPaths;
	
	public BigBangScoreManipulator(BigBangDenotatorManager denotatorManager, List<TransformationPaths> transformationPaths) {
		this.denotatorManager = denotatorManager;
		this.transformationPaths = transformationPaths;
	}

}
