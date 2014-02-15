package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.util.CoolFormRegistrant;
import org.rubato.rubettes.util.DenotatorObject;
import org.rubato.rubettes.util.DenotatorObjectConfiguration;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;
import org.rubato.rubettes.util.PerformanceCheck;

public class BigBangObjects {
	
	//active object is the one selected for being drawn etc
	private Form baseForm;
	private FormValueFinder finder;
	private List<String> coordinateSystemValueNames;
	//number for each object type, null if no satellites possible for the type
	private List<Integer> maxSatelliteLevels;
	private TreeSet<BigBangObject> objects;
	private HashMap<AbstractOperationEdit,Map<DenotatorPath,BigBangObject>> removedObjects;
	private HashMap<AbstractOperationEdit,Map<DenotatorPath,BigBangObject>> objectsMaps;
	private List<Double> minValues, maxValues;
	
	public BigBangObjects(Form baseForm) {
		this.baseForm = baseForm;
		
		this.finder = new FormValueFinder(this.baseForm, true);
		this.maxSatelliteLevels = new ArrayList<Integer>();
		for (Boolean currentObjectCanBeSatellite : this.finder.getObjectsCanBeSatellites()) {
			if (currentObjectCanBeSatellite) {
				this.maxSatelliteLevels.add(-1);
			} else {
				this.maxSatelliteLevels.add(null);
			}
		}
		
		List<String> coordinateSystemValueNames = new ArrayList<String>(finder.getCoordinateSystemValueNames());
		if (this.finder.formAllowsForSatellites()) {
			coordinateSystemValueNames.add(DenotatorValueExtractor.SATELLITE_LEVEL);
			coordinateSystemValueNames.add(DenotatorValueExtractor.SIBLING_NUMBER);
		}
		if (this.finder.formContainsColimits()) {
			coordinateSystemValueNames.add(DenotatorValueExtractor.COLIMIT_INDEX);
		}
		this.coordinateSystemValueNames = coordinateSystemValueNames;
		
		this.clearObjects();
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
	}
	
	public void clearObjects() {
		this.objects = new TreeSet<BigBangObject>();
		this.removedObjects = new HashMap<AbstractOperationEdit,Map<DenotatorPath,BigBangObject>>();
		this.objectsMaps = new HashMap<AbstractOperationEdit,Map<DenotatorPath,BigBangObject>>();
	}
	
	public Integer getMaxSatelliteLevel(int objectIndex) {
		return this.maxSatelliteLevels.get(objectIndex);
	}
	
	public List<String> getCoordinateSystemValueNames() {
		return this.coordinateSystemValueNames;
	}
	
	public int getNumberOfNonAnalyticalCoordinateSystemValues() {
		return this.finder.getCoordinateSystemValueNames().size();
	}
	
	public void setMinMaxValues(List<Double> minValues, List<Double> maxValues) {
		this.minValues = minValues;
		this.maxValues = maxValues;
	}
	
	public List<Double> getMinValues() {
		return this.minValues;
	}
	
	public List<Double> getMaxValues() {
		return this.maxValues;
	}
	
	public void clearValues() {
		for (BigBangObject currentObject : this.objects) {
			currentObject.clearValues();
		}
	}
	
	public Form getBaseForm() {
		return this.baseForm;
	}
	
	public boolean baseFormAllowsForSatellites() {
		return this.finder.formAllowsForSatellites();
	}
	
	public boolean baseFormContainsColimits() {
		return this.finder.formContainsColimits();
	}
	
	public int getNumberOfColimits(int objectIndex) {
		return this.finder.getObjectAt(objectIndex).getColimits().size();
	}
	
	public void updateMaxSatelliteLevels(BigBangObject object) {
		int objectIndex = this.finder.indexOf(object.getTopDenotatorPath().getEndForm());
		Integer currentLevel = this.maxSatelliteLevels.get(objectIndex);
		if (currentLevel != null) {
			int currentMax = Math.max(currentLevel, (int)object.getNthValue(DenotatorValueExtractor.SATELLITE_LEVEL, 0).doubleValue());
			this.maxSatelliteLevels.set(objectIndex, currentMax);
		}
	}
	
	public Set<BigBangObject> getObjects() {
		return this.objects;
	}
	
	public Set<BigBangObject> getObjectsAt(AbstractOperationEdit operation) {
		if (this.objectsMaps.containsKey(operation)) {
			return new TreeSet<BigBangObject>(this.objectsMaps.get(operation).values());
		}
		return null;
	}
	
	public Set<BigBangObject> getRemovedObjectsAt(AbstractOperationEdit operation) {
		return new TreeSet<BigBangObject>(this.removedObjects.get(operation).values());
	}
	
	//returns the object that has the given path at the given operation, null if there is none
	private BigBangObject getObject(AbstractOperationEdit operation, DenotatorPath path) {
		if (this.objectsMaps.containsKey(operation)) {
			return this.objectsMaps.get(operation).get(path);
		}
		return null;
	}
	
	//returns the object that has the given path at the final state, null if there is none
	public BigBangObject getObject(DenotatorPath path) {
		if (this.objectsMaps.containsKey(null)) {
			return this.objectsMaps.get(null).get(path);
		}
		return null;
	}
	
	public void removeOperation(AbstractOperationEdit operation) {
		for (BigBangObject currentObject : this.objects) {
			currentObject.removeOperation(operation);
		}
		this.objectsMaps.remove(operation);
	}
	
	/**
	 * Updates the paths of all objects that existed at previousOperation and were changed according to the given
	 * changedPaths. Sets the new paths at the given operation.
	 */
	public void updatePaths(AbstractOperationEdit previousOperation, AbstractOperationEdit operation, OperationPathResults pathResults) {
		//update existing objects
		PerformanceCheck.startTask("changedpaths");
		//System.out.println("UP " + previousOperation + " " + operation + " " + pathResults.getNewPaths() + " " + pathResults.getChangedPaths() + " " + pathResults.getRemovedPaths());
		Set<DenotatorPath> removedPaths = new TreeSet<DenotatorPath>(pathResults.getRemovedPaths());
		Set<DenotatorPath> newPaths = new TreeSet<DenotatorPath>(pathResults.getNewPaths());
		Map<DenotatorPath,DenotatorPath> changedPaths = pathResults.getChangedPaths();
		Set<BigBangObject> objectsToBeRemoved = new TreeSet<BigBangObject>();
		
		//go through all existing objects and remove ones that need to be
		for (BigBangObject currentObject : this.objects) {
			DenotatorPath previousPath = currentObject.getTopDenotatorPathAt(previousOperation);
			if (previousPath != null && removedPaths.contains(previousPath)) {
				objectsToBeRemoved.add(currentObject);
			}
		}
		
		//remove marked objects and reset objectsToBeRemoved
		this.removeObjects(operation, objectsToBeRemoved);
		objectsToBeRemoved = new TreeSet<BigBangObject>();
		
		//go through all existing objects and change ones that need to be
		for (BigBangObject currentObject : this.objects) {
			PerformanceCheck.startTask("getpaths");
			DenotatorPath previousPath = currentObject.getTopDenotatorPathAt(previousOperation);
			DenotatorPath currentPath = currentObject.getTopDenotatorPathAt(operation);
			DenotatorPath finalPath = currentObject.getTopDenotatorPathAt(null);
			
			//System.out.println("TTT " + previousPath + " "  + currentPath + " " + finalPath + " " + currentObject);
			if (previousPath != null) {
				//object did already exist at previous state
				if (changedPaths.keySet().contains(previousPath)) {
					//change path if in changedPaths
					DenotatorPath currentChangedPath = changedPaths.get(previousPath);
					BigBangObject parent = this.getObject(operation, currentChangedPath.getAnchorPath());
					//System.out.println(".TT " + previousOperation + " " + previousPath + " "  + operation + " " + currentPath + " " + finalPath);
					this.updateObject(currentObject, parent, operation, currentChangedPath);
				} else if (!removedPaths.contains(previousPath)){
					//keep path the same if not in changedPath (may have been changed before)
					BigBangObject previousParent = currentObject.getParentAt(previousOperation);
					this.updateObject(currentObject, previousParent, operation, previousPath);
				}
			} else {
				//object did not exist at previous state and may have been added
				if (currentPath != null) {
					//System.out.println(".. " + currentPath + " " +newPaths);
					if (newPaths.contains(currentPath)) {
						//remove path from newPaths so not added later
						newPaths.remove(currentPath);
					} else if (!newPaths.isEmpty()) {
						//give the object the next unused path from new paths
						//previously new objects will keep their order!! 
						DenotatorPath newPath = newPaths.iterator().next();
						BigBangObject parent = this.getObject(operation, newPath.getAnchorPath());
						this.updateObject(currentObject, parent, operation, newPath);
						newPaths.remove(newPath);
					} else {
						//object does not exist as new object anymore and needs to be removed
						objectsToBeRemoved.add(currentObject);
					}
				//object was added by previousOperation but maybe before knowing currentOperation
				} else if (currentObject.getCreatingOperation() == previousOperation && finalPath != null && newPaths.contains(finalPath)) {
					//update path in case previous path was final
					currentObject.concretizeFinalPath(operation);
					//remove path from newPaths so not added later
					newPaths.remove(finalPath);
				}
			}
		}
		
		//remove marked objects
		this.removeObjects(operation, objectsToBeRemoved);
		
		PerformanceCheck.startTask("newpaths");
		//go through remaining newPaths and add corresponding objects
		for (DenotatorPath currentNewPath : newPaths) {
			DenotatorPath parentPath = currentNewPath.getAnchorPath();
			if (parentPath != null && parentPath.size() > 0) {
				BigBangObject parent = this.getObject(operation, parentPath);
				this.addObject(previousOperation, operation, parent, currentNewPath);
			} else {
				this.addObject(previousOperation, operation, null, currentNewPath);
			}
		}
		//System.out.println("END " + this.objects + " " + this.objectsMaps);
	}
	
	private void addObject(AbstractOperationEdit previousOperation, AbstractOperationEdit operation, BigBangObject parent, DenotatorPath path) {
		BigBangObject object;
		if (this.removedObjects.get(operation) != null && this.removedObjects.get(operation).containsKey(path)) {
			object = this.removedObjects.get(operation).remove(path);
			object.setParent(operation, parent);
		} else {
			object = new BigBangObject(previousOperation, operation, parent, path);
		}
		this.objects.add(object);
		//System.out.println("ADD "+object + " " + this.objects);
		this.addToObjectsMaps(object, operation, path);
	}
	
	private void updateObject(BigBangObject object, BigBangObject parent, AbstractOperationEdit operation, DenotatorPath newPath) {
		this.updateParents(object, parent, operation);
		//update path and maps
		object.updatePath(operation, newPath);
		this.addToObjectsMaps(object, operation, newPath);
		//System.out.println("UP " +newPath + " " +this.objectsMaps.get(null));
	}
	
	private void updateParents(BigBangObject object, BigBangObject parent, AbstractOperationEdit operation) {
		//remove from parent if there was one, then add new parent if not null
		BigBangObject previousParent = object.getParentAt(operation);
		if (previousParent != null && previousParent != parent) {
			previousParent.removeChild(operation, object);
		}
		object.setParent(operation, parent);
		if (parent != null) {
			parent.addChild(operation, object);
		}
	}
	
	private void addToObjectsMaps(BigBangObject object, AbstractOperationEdit operation, DenotatorPath newPath) {
		if (!this.objectsMaps.containsKey(operation)) {
			this.objectsMaps.put(operation, new TreeMap<DenotatorPath,BigBangObject>());
		}
		this.objectsMaps.get(operation).put(newPath, object);
	}
	
	private void removeObjects(AbstractOperationEdit operation, Set<BigBangObject> objectsToBeRemoved) {
		for (BigBangObject currentObject : objectsToBeRemoved) {
			this.updateParents(currentObject, null, operation);
			DenotatorPath previousPath = currentObject.getTopDenotatorPathAt(operation);
			if (previousPath != null) {
				if (this.objectsMaps.containsKey(operation)) {
					this.objectsMaps.get(operation).remove(previousPath);
				}
				this.objects.remove(currentObject);
				this.addToRemovedObjects(currentObject, operation, previousPath);
			} else {
				currentObject.updatePath(operation, null);
				if (currentObject.isGhost()) {
					this.objects.remove(currentObject);
				}
				/*if (this.objectsMaps.containsKey(operation)) {
					System.out.println("REM2 " + operation +  " " +this.objectsMaps);
					this.objectsMaps.get(operation).remove(previousPath);
				}*/
				//System.out.println("REM " +this.objectsMaps.get(null));
			}
			//System.out.println("REM " + previousPath + " " + operation+ " " + objectsToBeRemoved + " " +  this.objects);
		}
	}
	
	private void addToRemovedObjects(BigBangObject object, AbstractOperationEdit operation, DenotatorPath newPath) {
		if (!this.removedObjects.containsKey(operation)) {
			this.removedObjects.put(operation, new TreeMap<DenotatorPath,BigBangObject>());
		}
		this.removedObjects.get(operation).put(newPath, object);
	}
	
	/**
	 * @return the number of the instance of the valueName of the value at the given index (number of previous
	 * instances + the instance itself. 1 if it is the first)
	 */
	public int getInstanceNumberOfCoordinateValueName(int coordinateSystemValueIndex) {
		if (coordinateSystemValueIndex < this.finder.getCoordinateSystemValueNames().size()) {
			return this.finder.getInstanceNumberOfCoordinateValueName(coordinateSystemValueIndex);
		}
		//in case of satellitelevel/siblingnumber
		return 0;
	}
	
	public DenotatorObjectConfiguration getObjectType(Form objectForm, DenotatorPath longestColimitPath) {
		return this.finder.getConfiguration(objectForm, longestColimitPath);
	}
	
	public DenotatorObjectConfiguration getStandardObjectType(Form objectForm) {
		return this.finder.getStandardConfiguration(objectForm);
	}
	
	public List<List<DenotatorPath>> getAllObjectConfigurationsValuePathsAt(int coordinateSystemValueIndex) {
		return this.finder.getAllObjectConfigurationsValuePathsAt(coordinateSystemValueIndex);
	}
	
	/**
	 * @return the path of the closest powerset at the given coordinateSystemValueIndex, if it is closer to the given
	 * currentClosestPowersetPath
	 */
	public BigBangObject getClosestObject(int[] coordinateSystemValueIndices, double[] values, DenotatorPath examplePowersetPath) {
		Form formOfObjectInExamplePowerset = examplePowersetPath.getTopPath().getEndForm();
		BigBangObject closestObject = null;
		double shortestDistance = Double.MAX_VALUE;
			
		//if (this.indexOfActiveObjectType >= 0) {
			for (BigBangObject currentObject : this.objects) {
				if (currentObject.getTopDenotatorPath().getEndForm().equals(formOfObjectInExamplePowerset)
						&& currentObject.getTopDenotatorPath().size() == examplePowersetPath.getTopPath().size()) {
					//calculate Euclidean distance
					double currentDistance = 0;
					for (int i = 0; i < values.length; i++) {
						String valueName = this.coordinateSystemValueNames.get(coordinateSystemValueIndices[i]);
						int nameIndex = this.getInstanceNumberOfCoordinateValueName(coordinateSystemValueIndices[i]);
						
						Double currentValue = currentObject.getNthValue(valueName, nameIndex);
						if (currentValue != null) {
							currentDistance += Math.pow(currentValue-values[i], 2);
						}
					}
					currentDistance = Math.sqrt(currentDistance);
					if (currentDistance < shortestDistance) {
						shortestDistance = currentDistance;
						closestObject = currentObject;
					}
				}
			}
		//}
		return closestObject;
	}
	
	public List<Form> getObjectTypes() {
		return this.finder.getObjectForms();
	}
	
	public DenotatorObject getObjectType(int objectIndex) {
		return this.finder.getObjectAt(objectIndex);
	}
	
	public int getObjectValueIndex(int coordinateSystemValueIndex, int objectIndex, List<Integer> colimitCoordinates) {
		return this.finder.getActiveObjectValueIndex(coordinateSystemValueIndex, objectIndex, colimitCoordinates);
	}
	
	public int getObjectFirstValueIndex(SimpleForm form, int objectIndex, List<Integer> colimitCoordinates) {
		return this.finder.getActiveObjectFirstValueIndex(form, objectIndex, colimitCoordinates);
	}
	
	//TODO: pretty lame, but used in two places: BigBangView and DisplayContents
	public int getTimeAxisIndex(ViewParameters viewParameters) {
		int timeValueIndex = this.getCoordinateSystemValueNames().indexOf(CoolFormRegistrant.ONSET_NAME);
		if (timeValueIndex == -1) {
			timeValueIndex = this.getCoordinateSystemValueNames().indexOf(CoolFormRegistrant.BEAT_CLASS_NAME);
		}
		if (timeValueIndex != -1) {
			int onsetParameterIndex = viewParameters.getFirstIndexOfValue(timeValueIndex);
			if (onsetParameterIndex < 2) {
				return onsetParameterIndex;
			}
		}
		return -1;
	}
	
	public BigBangObject first() {
		return this.objects.first();
	}
	
	public BigBangObject last() {
		return this.objects.last();
	}
	
	public int size() {
		return this.objects.size();
	}

}
