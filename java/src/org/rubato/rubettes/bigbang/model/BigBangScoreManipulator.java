package org.rubato.rubettes.bigbang.model;

import java.util.List;

import org.rubato.rubettes.util.DenotatorPath;

/**
 * Manipulates satellite and sound node hierarchies
 * @author flo
 *
 */
public class BigBangScoreManipulator {
	
	protected BigBangScore score;
	protected List<DenotatorPath> valuePaths;
	
	public BigBangScoreManipulator(BigBangScore score, List<DenotatorPath> valuePaths) {
		this.score = score;
		this.valuePaths = valuePaths;
	}

}
