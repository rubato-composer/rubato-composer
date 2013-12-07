package org.rubato.rubettes.bigbang.model;

import java.util.List;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangComposition score;
	protected List<TransformationPaths> transformationPaths;
	
	public BigBangScoreManipulator(BigBangComposition score, List<TransformationPaths> transformationPaths) {
		this.score = score;
		this.transformationPaths = transformationPaths;
	}

}
