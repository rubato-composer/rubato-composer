package org.rubato.rubettes.bigbang.model;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangScore score;
	protected TransformationPaths transformationPaths;
	
	public BigBangScoreManipulator(BigBangScore score, TransformationPaths transformationPaths) {
		this.score = score;
		this.transformationPaths = transformationPaths;
	}

}
