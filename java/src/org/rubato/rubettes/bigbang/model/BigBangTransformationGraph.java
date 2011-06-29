package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;

import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;

public class BigBangTransformationGraph extends ArrayList<AbstractTransformationEdit> {
	
	public BigBangTransformationGraph() {
		
	}
	
	@Override
	public boolean add(AbstractTransformationEdit edit) {
		boolean added = super.add(edit);
		if (added) {
			this.updateScore();
		}
		return added;
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit);
		this.removeLastWithoutUpdate();
	}
	
	public void updateScore() {
		if (this.size()>0) {
			//System.out.println("updateScore");
			this.get(0).getScoreManager().resetFactualScore();
			for (AbstractTransformationEdit edit: this) {
				//System.out.println(edit.getPresentationName());
				edit.map();
			}
		}
	}
	
	public AbstractTransformationEdit removeLast() {
		AbstractTransformationEdit removed = this.removeLastWithoutUpdate();
		this.updateScore();
		return removed;
	}
	
	private AbstractTransformationEdit removeLastWithoutUpdate() {
		return super.remove(this.size()-1);
	}

}
