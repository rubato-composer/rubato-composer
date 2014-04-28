package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.module.Module;
import org.rubato.math.module.ProductRing;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.ListForm;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;

public class FormValueFinder {
	
	private List<String> distinctValueNames;
	private List<String> coordinateSystemValueNames;
	//objects are the top-level denotator (if it is not a powerset) as well as elements of powersets
	private List<DenotatorObject> objectsInFoundOrder;
	private List<Form> objectForms;
	private List<Boolean> objectsCanBeSatellites;
	private boolean formContainsColimits;
	private boolean allowsForSatellites;
	private final int MAX_SEARCH_DEPTH = 10;
	
	public FormValueFinder(Form form, boolean searchThroughPowersets) {
		this.distinctValueNames = new ArrayList<String>();
		this.objectsInFoundOrder = new ArrayList<DenotatorObject>();
		this.objectForms = new ArrayList<Form>();
		this.objectsCanBeSatellites = new ArrayList<Boolean>();
		this.allowsForSatellites = false;
		this.formContainsColimits = false;
		DenotatorPath formPath = new DenotatorPath(form);
		this.findValues(formPath, searchThroughPowersets, 0, new DenotatorObject(form, formPath));
		this.updateCoordinateSystemValueNames();
	}
	
	public List<String> getCoordinateSystemValueNames() {
		return this.coordinateSystemValueNames;
	}
	
	public int getObjectCount() {
		return this.objectsInFoundOrder.size();
	}
	
	public List<Form> getObjectForms() {
		return this.objectForms;
	}
	
	public int indexOf(Form objectForm) {
		return this.objectForms.indexOf(objectForm);
	}
	
	public DenotatorObject getObjectAt(int objectIndex) {
		return this.objectsInFoundOrder.get(objectIndex);
	}
	
	public List<Boolean> getObjectsCanBeSatellites() {
		return this.objectsCanBeSatellites;
	}
	
	public DenotatorObjectConfiguration getConfiguration(Form objectForm, DenotatorPath longestColimitPath) {
		//System.out.println(objectForm + " " + longestColimitPath + " " + this.objectsInFoundOrder.get(this.objectForms.indexOf(objectForm)).getColimitConfigurations());
		return this.objectsInFoundOrder.get(this.objectForms.indexOf(objectForm)).getColimitConfiguration(longestColimitPath);
	}
	
	public DenotatorObjectConfiguration getStandardConfiguration(Form objectForm) {
		return this.objectsInFoundOrder.get(this.objectForms.indexOf(objectForm)).getStandardConfiguration();
	}
	
	/**
	 * @return all paths of the instances of the value at the given coordinateSystemValueIndex, separated by objects
	 * and then by configurations
	 */
	public List<List<DenotatorPath>> getAllObjectConfigurationsValuePathsAt(int coordinateSystemValueIndex) {
		if (coordinateSystemValueIndex >= 0) {
			List<List<DenotatorPath>> paths = new ArrayList<List<DenotatorPath>>();
			String nameInCoordinateSystem = this.getCoordinateSystemValueNames().get(coordinateSystemValueIndex);
			int previousOccurrencesInCoordinateSystem = this.getPreviousOccurrencesInCoordinateSystem(nameInCoordinateSystem, coordinateSystemValueIndex);
			for (DenotatorObject currentObjectType : this.objectsInFoundOrder) {
				paths.add(currentObjectType.getAllConfigurationsValuePathsOfNthInstance(nameInCoordinateSystem, previousOccurrencesInCoordinateSystem));
			}
			return paths;
		}
		return null;
	}
	
	/**
	 * @return all paths of the instances of the value at the given coordinateSystemValueIndex, separated by objects
	 * and then by configurations
	 */
	public List<DenotatorPath> getAllObjectConfigurationsValuePathsAt(int objectIndex, int coordinateSystemValueIndex) {
		if (coordinateSystemValueIndex >= 0) {
			String nameInCoordinateSystem = this.getCoordinateSystemValueNames().get(coordinateSystemValueIndex);
			int previousOccurrencesInCoordinateSystem = this.getPreviousOccurrencesInCoordinateSystem(nameInCoordinateSystem, coordinateSystemValueIndex);
			return this.objectsInFoundOrder.get(objectIndex).getAllConfigurationsValuePathsOfNthInstance(nameInCoordinateSystem, previousOccurrencesInCoordinateSystem);
		}
		return null;
	}
	
	public int getActiveObjectValueIndex(int coordinateSystemValueIndex, int objectIndex, List<Integer> colimitCofiguration) {
		if (coordinateSystemValueIndex >= 0) {
			String nameInCoordinateSystem = this.getCoordinateSystemValueNames().get(coordinateSystemValueIndex);
			int previousOccurrencesInCoordinateSystem = this.getPreviousOccurrencesInCoordinateSystem(nameInCoordinateSystem, coordinateSystemValueIndex);
			return this.objectsInFoundOrder.get(objectIndex).getIndexOfNthInstanceOfConfigurationValueName(colimitCofiguration, nameInCoordinateSystem, previousOccurrencesInCoordinateSystem);
		}
		return -1;
	}
	
	public int getActiveObjectFirstValueIndex(SimpleForm form, int objectIndex, List<Integer> colimitCofiguration) {
		String coordinateSystemValueName = FormValueFinder.makeValueName(form.getNameString(), form.getModule(), "");
		return this.objectsInFoundOrder.get(objectIndex).getIndexOfNthInstanceOfConfigurationValueName(colimitCofiguration, coordinateSystemValueName, 0);
	}
	
	public int getInstanceNumberOfCoordinateValueName(int coordinateSystemValueIndex) {
		String nameInCoordinateSystem = this.getCoordinateSystemValueNames().get(coordinateSystemValueIndex);
		return this.getPreviousOccurrencesInCoordinateSystem(nameInCoordinateSystem, coordinateSystemValueIndex);
	}
	
	private int getPreviousOccurrencesInCoordinateSystem(String nameInCoordinateSystem, int coordinateSystemValueIndex) {
		int previousOccurrencesInCoordinateSystem = 0;
		for (int i = 0; i < coordinateSystemValueIndex; i++) {
			if (this.getCoordinateSystemValueNames().get(i).equals(nameInCoordinateSystem)) {
				previousOccurrencesInCoordinateSystem++;
			}
		}
		return previousOccurrencesInCoordinateSystem;
	}
	
	public boolean formAllowsForSatellites() {
		return this.allowsForSatellites;
	}
	
	public boolean formContainsColimits() {
		return this.formContainsColimits;
	}
	
	//recursive depth search, has to be the same as the one in DenotatorValueExtractor...
	private void findValues(DenotatorPath currentPath, boolean searchThroughPowersets, int currentSearchDepth, DenotatorObject currentObject) {
		if (currentSearchDepth < this.MAX_SEARCH_DEPTH) {
			Form currentForm = currentPath.getEndForm();
			System.out.println(currentPath + " " + currentForm);
			if (currentPath.size() == 0 && currentForm.getType() != Form.POWER && currentForm.getType() != Form.LIST) {
				this.addObject(currentObject);
			}
			if (currentForm.getType() == Form.SIMPLE) {
				this.addValueNames(currentForm.getNameString(), ((SimpleForm)currentForm).getModule(), currentPath, "", currentObject);
			} else if (currentForm.getType() == Form.LIMIT || currentForm.getType() == Form.COLIMIT) {
				if (currentForm.getType() == Form.COLIMIT) {
					this.formContainsColimits = true;
					currentObject.addColimit((ColimitForm)currentForm, currentPath);
				}
				for (int i = 0; i < currentForm.getForms().size(); i++) {
					this.findValues(currentPath.getChildPath(i), searchThroughPowersets, currentSearchDepth+1, currentObject);
				}
			} else if (searchThroughPowersets && (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST)) {
				Form childForm;
				if (currentForm instanceof PowerForm) {
					childForm = ((PowerForm)currentForm).getForm();
				} else {
					childForm = ((ListForm)currentForm).getForm();
				}
				DenotatorPath childPath = currentPath.getChildPath(0);
				if (!this.objectForms.contains(childForm)) {
					currentObject = new DenotatorObject(childForm, childPath);
					this.addObject(currentObject);
					this.findValues(childPath, searchThroughPowersets, currentSearchDepth+1, currentObject);
				} else {
					this.objectsCanBeSatellites.set(this.objectForms.indexOf(childForm), true);
				}
				if (currentSearchDepth > 0) {
					//TODO: actually, this should check if there are simples in the top object. otherwise they are
					//technically not satellites...
					this.allowsForSatellites = true;
				}
			}
		}
	}
	
	private void addObject(DenotatorObject object) {
		this.objectsInFoundOrder.add(object);
		this.objectForms.add(object.getForm());
		this.objectsCanBeSatellites.add(false);
	}
	
	//recursively finds all values and their names
	private void addValueNames(String simpleName, Module currentModule, DenotatorPath currentPath, String indexString, DenotatorObject currentObject) {
		if (currentModule instanceof ProductRing) {
			ProductRing productRing = (ProductRing)currentModule;
			for (int i = 0; i < productRing.getFactorCount(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.addValueNames(simpleName, productRing.getFactor(i), currentPath.getChildPath(i), indexString+(i+1), currentObject);
			}
		} else if (currentModule.getDimension() > 1) {
			for (int i = 0; i < currentModule.getDimension(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				//System.out.println(currentModule + " " + currentModule.getComponentModule(i) + " " + currentPath.getChildPath(i));
				this.addValueNames(simpleName, currentModule.getComponentModule(i), currentPath.getChildPath(i), indexString+(i+1), currentObject);
			}
		} else {
			String currentValueName = FormValueFinder.makeValueName(simpleName, currentModule, indexString);
			currentObject.addValue(currentValueName, currentPath);
			if (!this.distinctValueNames.contains(currentValueName)) {
				this.distinctValueNames.add(currentValueName);
			}
		}
	}
	
	private void updateCoordinateSystemValueNames() {
		this.coordinateSystemValueNames = new ArrayList<String>();
		for (String currentValueName : this.distinctValueNames) {
			int currentMaxInstances = 0;
			for (DenotatorObject currentObject : this.objectsInFoundOrder) {
				currentMaxInstances = Math.max(currentObject.getMaxInstancesInConfigurations(currentValueName), currentMaxInstances);
			}
			for (int i = 0; i < currentMaxInstances; i++) {
				this.coordinateSystemValueNames.add(currentValueName);
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
