package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;

public class DenotatorObject {
	
	private Form form;
	private DenotatorPath path;
	private List<String> valueNames;
	private List<DenotatorPath> valuePaths;
	private List<ColimitForm> colimits;
	private List<DenotatorPath> colimitPaths;
	private Map<List<Integer>,DenotatorObjectConfiguration> colimitConfigurations;
	
	public DenotatorObject(Form form, DenotatorPath path) {
		this.form = form;
		this.path = path;
		this.valueNames = new ArrayList<String>();
		this.valuePaths = new ArrayList<DenotatorPath>();
		this.colimits = new ArrayList<ColimitForm>();
		this.colimitPaths = new ArrayList<DenotatorPath>();
	}
	
	/**
	 * adds a value's name and path, adjusts the path to a path relative to the object
	 */
	public void addValue(String name, DenotatorPath path) {
		this.valueNames.add(name);
		this.valuePaths.add(path.subPath(this.path.size()));
	}
	
	public void addColimit(ColimitForm form, DenotatorPath path) {
		this.colimits.add(form);
		//TODO: also adjust path??
		this.colimitPaths.add(path);
	}
	
	private void updateColimitConfigurations() {
		if (this.colimitConfigurations == null) {
			this.colimitConfigurations = new HashMap<List<Integer>,DenotatorObjectConfiguration>();
			if (this.colimits.size() == 0) {
				DenotatorObjectConfiguration singleConfiguration = new DenotatorObjectConfiguration(new DenotatorPath(this.form));
				this.colimitConfigurations.put(new ArrayList<Integer>(), singleConfiguration);
			} else {
				this.generateColimitConfigurations(new ArrayList<Integer>(), 0, new DenotatorPath(this.form));
			}
			
			for (List<Integer> currentConfiguration : this.colimitConfigurations.keySet()) {
				for (int i = 0; i < this.valueNames.size(); i++) {
					DenotatorPath currentValuePath = this.valuePaths.get(i);
					DenotatorPath longestPathInCurrentConfiguration = this.colimitConfigurations.get(currentConfiguration).getLongestColimitPath();
					if (!currentValuePath.inConflictingColimitPositions(longestPathInCurrentConfiguration)) {
						this.colimitConfigurations.get(currentConfiguration).addValue(this.valueNames.get(i), currentValuePath);
					}
				}
			}
		}
	}
	
	//recursive method!!
	private void generateColimitConfigurations(List<Integer> currentConfiguration, int currentIndex, DenotatorPath currentLongestPath) {
		if (currentIndex <= this.colimits.size()-1) {
			ColimitForm currentForm = this.colimits.get(currentIndex);
			DenotatorPath currentPath = this.colimitPaths.get(currentIndex);
			if (!currentPath.inConflictingColimitPositions(currentLongestPath)) {
				if (currentLongestPath.size() < currentPath.size()) {
					currentLongestPath = currentPath;
				}
				for (int i = 0; i < currentForm.getForms().size(); i++) {
					List<Integer> currentChildConfiguration = new ArrayList<Integer>(currentConfiguration);
					currentChildConfiguration.add(i);
					if (currentIndex == this.colimits.size()-1) {
						DenotatorPath configurationPath = currentLongestPath.getChildPath(i).subPath(this.path.size());
						this.colimitConfigurations.put(currentChildConfiguration, new DenotatorObjectConfiguration(configurationPath));
					} else {
						this.generateColimitConfigurations(currentChildConfiguration, currentIndex+1, currentLongestPath);
					}
				}
			}
		}
	}
	
	public Set<List<Integer>> getColimitConfigurations() {
		this.updateColimitConfigurations();
		return this.colimitConfigurations.keySet();
	}
	
	public DenotatorObjectConfiguration getColimitConfiguration(DenotatorPath longestColimitPath) {
		for (DenotatorObjectConfiguration currentConfiguration : this.colimitConfigurations.values()) {
			if (currentConfiguration.getLongestColimitPath().equals(longestColimitPath)) {
				return currentConfiguration;
			}
		}
		return null;
	}
	
	public DenotatorObjectConfiguration getStandardConfiguration() {
		return this.colimitConfigurations.values().iterator().next();
	}
	
	public List<String> getColimitConfigurationValueNames(List<Integer> colimitConfiguration) {
		this.updateColimitConfigurations();
		//System.out.println(this.colimitConfigurations + " " + this.colimitConfigurationValueNames + " " + this.colimitConfigurationValuePaths);
		return this.colimitConfigurations.get(colimitConfiguration).getValueNames();
	}
	
	public List<DenotatorPath> getAllConfigurationsValuePathsOfNthInstance(String valueName, int n) {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (DenotatorObjectConfiguration currentConfiguration : this.colimitConfigurations.values()) {
			DenotatorPath currentPath = currentConfiguration.getPathOfNthInstanceOfValueName(valueName, n);
			if (currentPath != null) {
				paths.add(currentPath);
			}
		}
		return paths;
	}
	
	/*
	 * 0 <= n
	 */
	public int getIndexOfNthInstanceOfConfigurationValueName(List<Integer> colimitConfiguration, String name, int n) {
		return this.colimitConfigurations.get(colimitConfiguration).getIndexOfNthInstanceOfValueName(name, n);
	}
	
	public List<DenotatorPath> getColimitConfigurationValuePaths(List<Integer> colimitConfiguration) {
		this.updateColimitConfigurations();
		return this.colimitConfigurations.get(colimitConfiguration).getValuePaths();
	}
	
	public List<DenotatorPath> getStandardColimitConfigurationValuePaths() {
		//TODO: improve... remember which config is really first (0 or -1 in all colimits)
		return this.colimitConfigurations.get(this.colimitConfigurations.keySet().iterator().next()).getValuePaths();
	}
	
	public int getMaxInstancesInConfigurations(String valueName) {
		this.updateColimitConfigurations();
		int maxInstancesInConfigurations = 0;
		for (DenotatorObjectConfiguration currentConfiguration : this.colimitConfigurations.values()) {
			List<String> currentValueNames = currentConfiguration.getValueNames();
			int instancesInCurrentConfiguration = Collections.frequency(currentValueNames, valueName);
			maxInstancesInConfigurations = Math.max(instancesInCurrentConfiguration, maxInstancesInConfigurations);
		}
		return maxInstancesInConfigurations;
	}
	
	public Form getForm() {
		return this.form;
	}
	
	public DenotatorPath getPath() {
		return this.path;
	}
	
	public List<ColimitForm> getColimits() {
		return this.colimits;
	}
	
	public List<DenotatorPath> getColimitPaths() {
		return this.colimitPaths;
	}

}
