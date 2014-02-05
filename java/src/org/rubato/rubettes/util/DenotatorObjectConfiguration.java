package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

public class DenotatorObjectConfiguration {
	
	private DenotatorPath longestColimitPath;
	private List<String> valueNames;
	private List<DenotatorPath> valuePaths;
	
	public DenotatorObjectConfiguration(DenotatorPath longestColimitPath) {
		this.longestColimitPath = longestColimitPath;
		this.valueNames = new ArrayList<String>();
		this.valuePaths = new ArrayList<DenotatorPath>();
	}
	
	/**
	 * adds a value's name and path, path should be relative to the object
	 */
	public void addValue(String name, DenotatorPath path) {
		this.valueNames.add(name);
		this.valuePaths.add(path);
	}
	
	public DenotatorPath getLongestColimitPath() {
		return this.longestColimitPath;
	}
	
	public List<String> getValueNames() {
		return this.valueNames;
	}
	
	public List<DenotatorPath> getValuePaths() {
		return this.valuePaths;
	}
	
	public DenotatorPath getPathOfNthInstanceOfValueName(String valueName, int n) {
		int index = this.getIndexOfNthInstanceOfValueName(valueName, n);
		if (index > -1) {
			return this.valuePaths.get(index);
		}
		return null;
	}
	
	public int getOccurrencesOfValueName(String valueName) {
		int occurrences = 0;
		for (String currentName : this.valueNames) {
			if (currentName.equals(valueName)) {
				occurrences++;
			}
		}
		return occurrences;
	}
	
	public List<Integer> getIndicesOfValueName(String valueName) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < this.valueNames.size(); i++) {
			if (this.valueNames.get(i).equals(valueName)) {
				indices.add(i);
			}
		}
		return indices;
	}
	
	/*
	 * 0 <= n
	 */
	public int getIndexOfNthInstanceOfValueName(String valueName, int n) {
		List<String> names = this.valueNames;
		int previousIndex = -1;
		for (int i = 0; i < n; i++) {
			int currentIndex = names.indexOf(valueName);
			if (currentIndex > -1 && currentIndex < names.size()-1) {
				names = names.subList(currentIndex+1, names.size());
				previousIndex = currentIndex;
			} else {
				return -1;
			}
		}
		return names.indexOf(valueName)+previousIndex+1;
	}
	
}
