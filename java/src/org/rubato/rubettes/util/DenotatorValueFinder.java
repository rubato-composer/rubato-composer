package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.math.module.Module;
import org.rubato.math.module.ProductRing;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;

public class DenotatorValueFinder {
	
	private List<String> valueNamesInFoundOrder;
	private List<DenotatorPath> pathsInFoundOrder;
	private Map<String,DenotatorPath> valueNamesAndPaths;
	private List<ColimitForm> colimitsInFoundOrder;
	private Map<ColimitForm,DenotatorPath> colimitsAndPaths;
	//objects are the top-level denotator (if it is not a powerset) as well as elements of powersets
	private List<Form> objectsInFoundOrder;
	private Map<Form,DenotatorPath> objectsAndPaths;
	private boolean allowsForSatellites;
	private final int MAX_SEARCH_DEPTH = 10;
	
	public DenotatorValueFinder(Form form, boolean searchThroughPowersets) {
		this.valueNamesInFoundOrder = new ArrayList<String>();
		this.pathsInFoundOrder = new ArrayList<DenotatorPath>();
		this.valueNamesAndPaths = new TreeMap<String,DenotatorPath>();
		this.colimitsInFoundOrder = new ArrayList<ColimitForm>();
		this.colimitsAndPaths = new TreeMap<ColimitForm,DenotatorPath>();
		this.objectsInFoundOrder = new ArrayList<Form>();
		this.objectsAndPaths = new TreeMap<Form,DenotatorPath>();
		this.allowsForSatellites = false;
		this.findValues(new DenotatorPath(form), searchThroughPowersets, 0);
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
	
	public List<Form> getObjectsInFoundOrder() {
		return this.objectsInFoundOrder;
	}
	
	public Map<Form,DenotatorPath> getObjectsAndPaths() {
		return this.objectsAndPaths;
	}
	
	public List<ColimitForm> getColimitsInFoundOrder() {
		return this.colimitsInFoundOrder;
	}
	
	public Map<ColimitForm, DenotatorPath> getColimitsAndPaths() {
		return this.colimitsAndPaths;
	}
	
	public boolean formAllowsForSatellites() {
		return this.allowsForSatellites;
	}
	
	public boolean formContainsColimit() {
		return !this.colimitsInFoundOrder.isEmpty();
	}
	
	//recursive depth search, has to be the same as the one in DenotatorValueExtractor...
	private void findValues(DenotatorPath currentPath, boolean searchThroughPowersets, int currentSearchDepth) {
		if (currentSearchDepth < this.MAX_SEARCH_DEPTH) {
			Form currentForm = currentPath.getForm();
			if (currentPath.size() == 0 && currentForm.getType() != Form.POWER && currentForm.getType() != Form.LIST) {
				this.objectsInFoundOrder.add(currentForm);
				this.objectsAndPaths.put(currentForm, currentPath);
			}
			if (currentForm.getType() == Form.SIMPLE) {
				this.addValueNames(currentForm.getNameString(), ((SimpleForm)currentForm).getModule(), currentPath, "");
			} else if (currentForm.getType() == Form.LIMIT || currentForm.getType() == Form.COLIMIT) {
				if (currentForm.getType() == Form.COLIMIT) {
					this.colimitsInFoundOrder.add((ColimitForm)currentForm);
					this.colimitsAndPaths.put((ColimitForm)currentForm, currentPath);
				}
				for (int i = 0; i < currentForm.getForms().size(); i++) {
					this.findValues(currentPath.getChildPath(i), searchThroughPowersets, currentSearchDepth+1);
				}
			} else if (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST) {
				Form childForm = ((PowerForm)currentForm).getForm();
				DenotatorPath childPath = currentPath.getChildPath(0);
				if (!this.objectsInFoundOrder.contains(childForm)) {
					this.objectsInFoundOrder.add(childForm);
					this.objectsAndPaths.put(childForm, childPath);
				}
				if (currentSearchDepth > 0) {
					//TODO: actually, this should check if there are simples in the top object. otherwise they are
					//technically not satellites...
					this.allowsForSatellites = true;
				}
				if (searchThroughPowersets) {
					this.findValues(childPath, searchThroughPowersets, currentSearchDepth+1);
				}
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
			String currentValueName = DenotatorValueFinder.makeValueName(simpleName, currentModule, indexString);
			if (!this.valueNamesInFoundOrder.contains(currentValueName)) {
				this.valueNamesInFoundOrder.add(currentValueName);
				this.pathsInFoundOrder.add(currentPath);
				this.valueNamesAndPaths.put(currentValueName, currentPath);
			}
		}
	}
	
	public static String makeValueName(String simpleName, Module module, String indexString) {
		String moduleName = indexString;
		if (!indexString.isEmpty()) {
			moduleName += " ";
		}
		moduleName += module.toVisualString();
		return simpleName + " " + moduleName;
	}

}
