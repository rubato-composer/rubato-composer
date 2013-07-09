package org.rubato.rubettes.bigbang.model;

import java.util.List;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangScore score;
	protected List<TransformationPaths> transformationPaths;
	
	public BigBangScoreManipulator(BigBangScore score, List<TransformationPaths> transformationPaths) {
		this.score = score;
		this.transformationPaths = transformationPaths;
	}

}
