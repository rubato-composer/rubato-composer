package org.rubato.rubettes.bigbang.model;

import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.DenotatorPath;

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
	
	protected LimitDenotator getNode(DenotatorPath nodePath) {
		return this.score.getAbsoluteNote(nodePath);
	}

}
