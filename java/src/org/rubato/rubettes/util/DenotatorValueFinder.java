package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.math.module.Module;
import org.rubato.math.module.ProductRing;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;

public class DenotatorValueFinder {
	
	private List<String> valueNamesInFoundOrder;
	private List<DenotatorPath> pathsInFoundOrder;
	private Map<String,DenotatorPath> valueNamesAndPaths;
	private List<ColimitForm> colimitsFoundInOrder;
	private Map<ColimitForm,DenotatorPath> colimitFormsAndPaths;
	private boolean containsPowerset;
	private final int MAX_SEARCH_DEPTH = 5;
	
	public DenotatorValueFinder(Form form, boolean searchThroughPowersets) {
		this.valueNamesInFoundOrder = new ArrayList<String>();
		this.pathsInFoundOrder = new ArrayList<DenotatorPath>();
		this.valueNamesAndPaths = new TreeMap<String,DenotatorPath>();
		this.colimitsFoundInOrder = new ArrayList<ColimitForm>();
		this.colimitFormsAndPaths = new TreeMap<ColimitForm,DenotatorPath>();
		this.containsPowerset = false;
		this.findValues(new DenotatorPath(form), searchThroughPowersets);
	}
	
	public List<String> getValueNamesInFoundOrder() {
		return this.valueNamesInFoundOrder;
	}
	
	public List<DenotatorPath> getValuePathsInFoundOrder() {
		return this.pathsInFoundOrder; 
	}
	
	public Map<String,DenotatorPath> getValueNamesAndPaths() {
		return this.valueNamesAndPaths;
	}
	
	public List<ColimitForm> getColimitsFoundInOrder() {
		return this.colimitsFoundInOrder;
	}
	
	public Map<ColimitForm, DenotatorPath> getColimitFormsAndPaths() {
		return this.colimitFormsAndPaths;
	}
	
	public boolean formContainsPowerset() {
		return this.containsPowerset;
	}
	
	public boolean formContainsColimit() {
		return this.colimitsFoundInOrder.size() > 0;
	}
	
	//recursive depth search, has to be the same as the one in DenotatorValueExtractor...
	private void findValues(DenotatorPath currentPath, boolean searchThroughPowersets) {
		Form currentForm = currentPath.getForm();
		if (currentForm.getType() == Form.SIMPLE) {
			this.addValueNames(currentForm.getNameString(), ((SimpleForm)currentForm).getModule(), currentPath, "");
		} else if (currentForm.getType() == Form.LIMIT || currentForm.getType() == Form.COLIMIT) {
			if (currentForm.getType() == Form.COLIMIT) {
				this.colimitsFoundInOrder.add((ColimitForm)currentForm);
				this.colimitFormsAndPaths.put((ColimitForm)currentForm, currentPath);
			}
			for (int i = 0; i < currentForm.getForms().size(); i++) {
				this.findValues(currentPath.getChildPath(i), searchThroughPowersets);
			}
		} else if (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST) {
			this.containsPowerset = true;
			/*if (searchThroughPowersets) {
				this.findValues(currentPath.getChildPath(0), searchThroughPowersets);
			}*/
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
			String currentValueName = simpleName + " " + DenotatorValueFinder.makeModuleName(currentModule, indexString);
			this.valueNamesInFoundOrder.add(currentValueName);
			this.pathsInFoundOrder.add(currentPath);
			this.valueNamesAndPaths.put(currentValueName, currentPath);
		}
	}
	
	public static String makeModuleName(Module module, String indexString) {
		String moduleName = indexString;
		if (!indexString.isEmpty()) {
			moduleName += " ";
		}
		moduleName += module.toVisualString();
		return moduleName;
	}

}
