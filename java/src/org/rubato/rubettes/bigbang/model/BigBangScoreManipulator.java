package org.rubato.rubettes.bigbang.model;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangScore score;
	protected int[][] coordinatePaths;
	
	public BigBangScoreManipulator(BigBangScore score, int[][] coordinatePaths) {
		this.score = score;
		this.coordinatePaths = coordinatePaths;
	}

}
