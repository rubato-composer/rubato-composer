package org.rubato.rubettes.bigbang.model;

public class BigBangLayer {
	
	private int index;
	private String name;
	private boolean isActive;
	private boolean isAudible;
	private boolean isVisible;
	
	public BigBangLayer(int index) {
		this.setIndex(index);
		this.isActive = true;
		this.isAudible = true;
		this.isVisible = true;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (this.name != null) {
			return this.name;
		}
		return Integer.toString(this.index);
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setAudible(boolean isAudible) {
		this.isAudible = isAudible;
	}

	public boolean isAudible() {
		return this.isAudible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		this.isActive = isVisible;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

}
