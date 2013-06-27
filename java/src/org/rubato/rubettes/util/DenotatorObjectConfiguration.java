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
		return this.valuePaths.get(this.getIndexOfNthInstanceOfValueName(valueName, n));
	}
	
	public int getOccurrencesOfValueNameBefore(String valueName, int index) {
		int occurrences = 0;
		for (String currentName : this.valueNames.subList(0, index)) {
			if (currentName.equals(valueName)) {
				occurrences++;
			}
		}
		return occurrences;
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
				System.out.println("HELLO!!!");
				return -1;
			}
		}
		System.out.println(valueName + " " + n + " " + previousIndex);
		return names.indexOf(valueName)+previousIndex+1;
	}
	
}
