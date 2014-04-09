package org.rubato.rubettes.bigbang.model.graph;

public class CompositionState {// implements Comparable<CompositionState> {
	
	private int index;
	
	public CompositionState(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void incrementIndex() {
		this.index++;
	}
	
	public void decrementIndex() {
		this.index--;
	}
	
	/*@Override
	public boolean equals(Object other) {
		if (other instanceof CompositionState) {
			return ((CompositionState)other).getIndex() == this.index;
		}
		return false;
	}

	public int compareTo(CompositionState other) {
		return this.index - other.getIndex();
	}
	
	public int hashCode() {
		return this.index;
	}*/
	
	public String toString() {
		return Integer.toString(this.index);
	}

}
