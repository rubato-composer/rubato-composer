package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.module.Module;
import org.rubato.math.module.ProductRing;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;

public class DenotatorValueFinder {
	
	private List<String> valueNames;
	private List<DenotatorPath> valuePaths;
	private final int MAX_SEARCH_DEPTH = 10;
	
	public DenotatorValueFinder(Denotator denotator, boolean searchThroughPowersets) {
		this.valueNames = new ArrayList<String>();
		this.valuePaths = new ArrayList<DenotatorPath>();
		DenotatorPath formPath = new DenotatorPath(denotator.getForm());
		this.findValues(formPath, searchThroughPowersets, 0);
	}
	
	public List<DenotatorPath> getValuePaths() {
		return this.valuePaths;
	}
	
	public DenotatorPath getValuePathAt(int valueIndex) {
		return this.valuePaths.get(valueIndex);
	}
	
	//recursive depth search, has to be the same as the one in DenotatorValueExtractor...
	private void findValues(DenotatorPath currentPath, boolean searchThroughPowersets, int currentSearchDepth) {
		if (currentSearchDepth < this.MAX_SEARCH_DEPTH) {
			Form currentForm = currentPath.getEndForm();
			if (currentForm.getType() == Form.SIMPLE) {
				this.addValueNames(currentForm.getNameString(), ((SimpleForm)currentForm).getModule(), currentPath, "");
			} else if (currentForm.getType() == Form.LIMIT || currentForm.getType() == Form.COLIMIT) {
				for (int i = 0; i < currentForm.getForms().size(); i++) {
					this.findValues(currentPath.getChildPath(i), searchThroughPowersets, currentSearchDepth+1);
				}
			} else if (searchThroughPowersets && (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST)) {
				this.findValues(currentPath.getChildPath(0), searchThroughPowersets, currentSearchDepth+1);
			}
		}
	}
	
	//recursively finds all values and their names
	private void addValueNames(String simpleName, Module currentModule, DenotatorPath currentPath, String indexString) {
		if (currentModule instanceof ProductRing) {
			ProductRing productRing = (ProductRing)currentModule;
			for (int i = 0; i < productRing.getFactorCount(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.addValueNames(simpleName, productRing.getFactor(i), currentPath.getChildPath(i), indexString+(i+1));
			}
		} else if (currentModule.getDimension() > 1) {
			for (int i = 0; i < currentModule.getDimension(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				//System.out.println(currentModule + " " + currentModule.getComponentModule(i) + " " + currentPath.getChildPath(i));
				this.addValueNames(simpleName, currentModule.getComponentModule(i), currentPath.getChildPath(i), indexString+(i+1));
			}
		} else {
			String currentValueName = FormValueFinder.makeValueName(simpleName, currentModule, indexString);
			this.valueNames.add(currentValueName);
			this.valuePaths.add(currentPath);
		}
	}

}
