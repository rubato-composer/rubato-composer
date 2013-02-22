package org.rubato.rubettes.bigbang.model.player;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;

public class JSynScore {
	
	private List<JSynNote> notes;
	
	/**
	 * Creates a JSynScore from a flattened Rubato MacroScore or SoundScore.
	 * TODO: Other types will have to be added!! first step: anything that contains notes.... 
	 * @param rubatoScore a flattened MacroScore
	 */
	public JSynScore(Denotator rubatoScore, int bpm) {
		this.notes = new ArrayList<JSynNote>();
		
		if (rubatoScore.getType() == Denotator.POWER) {
			DenotatorValueExtractor extractor = new DenotatorValueExtractor();
			for (Denotator currentNode : ((PowerDenotator)rubatoScore).getFactors()) {
				this.notes.add(extractor.extractValues(currentNode, bpm));
			}
		}
	}
	
	/*private JSynScore(Denotator node, int bpm) {
		this.notes = new ArrayList<JSynNote>();
		DenotatorValueExtractor extractor = new DenotatorValueExtractor();
		this.notes.add(extractor.extractValues(node, bpm));
	}*/
	
	public List<JSynNote> getNotes() {
		return this.notes;
	}

}
