package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.model.edits.AffineTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AlterationEdit;
import org.rubato.rubettes.bigbang.model.edits.DeleteObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.EndWallpaperEdit;
import org.rubato.rubettes.bigbang.model.edits.FlattenEdit;
import org.rubato.rubettes.bigbang.model.edits.ReflectionEdit;
import org.rubato.rubettes.bigbang.model.edits.RotationEdit;
import org.rubato.rubettes.bigbang.model.edits.BuildSatellitesEdit;
import org.rubato.rubettes.bigbang.model.edits.ScalingEdit;
import org.rubato.rubettes.bigbang.model.edits.SetOrAddCompositionEdit;
import org.rubato.rubettes.bigbang.model.edits.ShapingEdit;
import org.rubato.rubettes.bigbang.model.edits.ShearingEdit;
import org.rubato.rubettes.bigbang.model.edits.TranslationEdit;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.PerformanceCheck;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

public class BigBangModel extends Model {
	
	private boolean inputActive;
	private boolean isMultiTouch;
	private BigBangController controller;
	private BigBangDenotatorManager denotators;
	private BigBangObjects objects; //object-oriented representation of the denotator composition
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	private BigBangTransformationGraph transformationGraph;
	private List<AbstractOperationEdit> undoneOperations;
	private BigBangGraphAnimator animator;
	
	public BigBangModel(BigBangController controller) {
		this.controller = controller;
		controller.addModel(this);
		this.denotators = new BigBangDenotatorManager();
		this.setInputActive(true);
		this.objects = new BigBangObjects(this.denotators.getForm(), controller);
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.reset();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
		this.fireCompositionChange();
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.transformationGraph = new BigBangTransformationGraph();
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public void setInputActive(Boolean inputActive) {
		this.inputActive = inputActive;
		this.firePropertyChange(BigBangController.INPUT_ACTIVE, null, this.inputActive);
	}
	
	public boolean isInputActive() {
		return this.inputActive;
	}
	
	public void setMultiTouch(Boolean multiTouch) {
		this.isMultiTouch = multiTouch;
		this.firePropertyChange(BigBangController.MULTITOUCH, null, this.isMultiTouch);
	}
	
	public void setForm(Form form) {
		if (!form.equals(this.denotators.getForm())) {
			this.reset();
			this.denotators.setForm(form);
			this.objects = new BigBangObjects(this.denotators.getForm(), this.controller);
			this.fireCompositionChange();
		}
	}
	
	public void newWindowAdded() {
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
		//TODO ALSO FIRE COMPOSITION CHANGE
	}
	
	/**
	 * if the currently selected operation is a SetOrAddCompositionEdit, replace its composition.
	 * else post a new SetOrAddCompositionEdit
	 */
	public void setOrAddComposition(Denotator composition) {
		if (!this.denotators.isFormCompatibleWithCurrentForm(composition.getForm())) {
			this.setForm(composition.getForm());
		}
		if (this.transformationGraph.getSelectedOperation() instanceof SetOrAddCompositionEdit) {
			((SetOrAddCompositionEdit)this.transformationGraph.getSelectedOperation()).setOrAddComposition(composition);
			this.operationModified();
		} else {
			this.postEdit(new SetOrAddCompositionEdit(this, composition));
		}
	}
	
	public Denotator getComposition() {
		return this.denotators.getComposition();
	}
	
	public BigBangDenotatorManager getDenotatorManager() {
		return this.denotators;
	}
	
	public void addObjects(ArrayList<Map<DenotatorPath,Double>> pathsWithValues, ArrayList<DenotatorPath> powersetPaths, Boolean inPreviewMode) {
		if (this.denotators != null) { //needs to be checked for milmeister ghost rubette reacting to leap motion 
			AbstractOperationEdit lastEdit = this.transformationGraph.getLastAddedOperation();
			if (lastEdit != null && lastEdit instanceof AddObjectsEdit) {
				AddObjectsEdit addEdit = (AddObjectsEdit) lastEdit;
				if (addEdit.addObjects(pathsWithValues, powersetPaths, inPreviewMode)) {
					this.operationModified();
					return;
				}
			}
			this.postEdit(new AddObjectsEdit(this, pathsWithValues, powersetPaths, inPreviewMode));
		}
	}
	
	public BigBangObjects getObjects() {
		return this.objects;
	}
	
	public void deleteObjects(TreeSet<BigBangObject> objects) {
		this.postEdit(new DeleteObjectsEdit(this, objects));
	}
	
	public void translateObjects(TransformationProperties properties) {
		if (properties.startNewTransformation()) {
			this.postEdit(new TranslationEdit(this, properties));
		} else if (this.updateTransformation(properties, TranslationEdit.class)) {
			this.updateComposition();
		}
	}
	
	public void rotateObjects(TransformationProperties properties, double[] startingPoint, Double angle) {
		if (properties.startNewTransformation()) {
			this.postEdit(new RotationEdit(this, properties, startingPoint, angle));
		} else if (this.updateTransformation(properties, RotationEdit.class)) {
			RotationEdit lastRotation = (RotationEdit)this.transformationGraph.getLastAddedOperation();
			lastRotation.setParameters(startingPoint, angle);
			this.updateComposition();
		}
	}
	
	public void scaleObjects(TransformationProperties properties, double[] scaleFactors) {
		if (properties.startNewTransformation()) {
			this.postEdit(new ScalingEdit(this, properties, scaleFactors));
		} else if (this.updateTransformation(properties, ScalingEdit.class)) {
			this.modifyLastTransformation(scaleFactors);
		}
	}
	
	public void reflectObjects(TransformationProperties properties, double[] reflectionVector) {
		if (properties.startNewTransformation()) {
			this.postEdit(new ReflectionEdit(this, properties, reflectionVector));
		} else if (this.updateTransformation(properties, ReflectionEdit.class)) {
			this.modifyLastTransformation(reflectionVector);
		}
	}
	
	public void shearObjects(TransformationProperties properties, double[] shearingFactors) {
		if (properties.startNewTransformation()) {
			this.postEdit(new ShearingEdit(this, properties, shearingFactors));
		} else if (this.updateTransformation(properties, ShearingEdit.class)) {
			this.modifyLastTransformation(shearingFactors);
		}
	}
	
	private boolean updateTransformation(TransformationProperties properties, Class<?> transformationClass) {
		AbstractOperationEdit lastOperation = this.transformationGraph.getLastAddedOperation();
		if (transformationClass.isInstance(lastOperation)) {
			((AbstractTransformationEdit)lastOperation).updateProperties(properties);
			return true;
		}
		return false;
	}
	
	private void modifyLastTransformation(double[] newValues) {
		AbstractTransformationEdit lastTransformation = (AbstractTransformationEdit)this.transformationGraph.getLastAddedOperation();
		lastTransformation.modify(newValues);
		this.updateComposition();
	}
	
	public void shapeObjects(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		ShapingEdit edit = null;
		if (this.transformationGraph.getLastAddedOperation() instanceof ShapingEdit) {
			edit = (ShapingEdit)this.transformationGraph.getLastAddedOperation();
		} else if (this.transformationGraph.getSelectedOperation() instanceof ShapingEdit) {
			edit = (ShapingEdit)this.transformationGraph.getSelectedOperation();
		}
		if (edit != null && edit.getShapingPaths().equals(properties.getTransformationPaths())) {
			edit.addShapingLocations(shapingLocations);
			this.updateComposition();
		} else {	
			this.postEdit(new ShapingEdit(this, properties, shapingLocations));
		}
	}
	
	public void affineTransformObjects(TransformationProperties properties, double[] shift, RMatrix transform) {
		this.postEdit(new AffineTransformationEdit(this, properties, shift, transform));
	}
	
	public void buildSatellites(TreeSet<BigBangObject> objects, BigBangObject anchorObject, Integer powersetIndex) {
		this.postEdit(new BuildSatellitesEdit(this, objects, anchorObject, powersetIndex));
	}
	
	public void flattenObjects(TreeSet<BigBangObject> objects) {
		this.postEdit(new FlattenEdit(this, objects));
	}
	
	public void addWallpaperDimension(TreeSet<BigBangObject> objectPaths, Integer rangeFrom, Integer rangeTo) {
		this.postEdit(new AddWallpaperDimensionEdit(this, objectPaths, rangeFrom, rangeTo));
	}
	
	public void endWallpaper() {
		this.postEdit(new EndWallpaperEdit(this));
	}
	
	public void addAlteration() {
		this.postEdit(new AlterationEdit(this));
		this.firePropertyChange(BigBangController.MODIFY_OPERATION, null, this.transformationGraph.getLastAddedOperation());
	}
	
	public void fireAlterationComposition(Integer index) {
		if (this.transformationGraph.getSelectedOperation() instanceof AlterationEdit) {
			//this.alteration.resetDegrees();
			this.fireObjectSelectionChange(((AlterationEdit)this.transformationGraph.getSelectedOperation()).getAlterationComposition(index));
			this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, index);
		}
	}
	
	public void setAlterationStartDegree(Double startDegree) {
		if (this.transformationGraph.getSelectedOperation() instanceof AlterationEdit) {
			((AlterationEdit)this.transformationGraph.getSelectedOperation()).setStartDegree(startDegree);
		}
	}
	
	public void setAlterationEndDegree(Double endDegree) {
		if (this.transformationGraph.getSelectedOperation() instanceof AlterationEdit) {
			((AlterationEdit)this.transformationGraph.getSelectedOperation()).setEndDegree(endDegree);
		}
	}
	
	private void postEdit(AbstractOperationEdit edit) {
		//this.undoSupport.postEdit(edit);
		this.transformationGraph.addOrInsertOperation(edit, false);
		this.updateComposition();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public void operationModified() {
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public void modifyOperation(Integer operationIndex, Double ratio) {
		if (operationIndex >= 0 && this.transformationGraph.getEdgeCount() > operationIndex) {
			DijkstraShortestPath<Integer,AbstractOperationEdit> dijkstra = new DijkstraShortestPath<Integer,AbstractOperationEdit>(this.transformationGraph);
		    List<AbstractOperationEdit> shortestPath = dijkstra.getPath(0, this.transformationGraph.getLastState());
		    AbstractOperationEdit operation = shortestPath.get(operationIndex);
		    operation.modify(ratio);
			this.updateComposition();
			this.firePropertyChange(BigBangController.MODIFY_OPERATION, null, operation);
		}
	}
	
	public void setOperationDurations(double duration) {
		this.transformationGraph.setDurations(duration);
	}
	
	public void setInsertionState(Integer state) {
		this.transformationGraph.setInsertionState(state);
	}
	
	public void removeOperation(AbstractOperationEdit operation) {
		this.internalRemoveOperation(operation);
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	private void internalRemoveOperation(AbstractOperationEdit operation) {
		this.transformationGraph.removeOperation(operation);
		this.objects.removeOperation(operation);
	}
	
	public void undo() {
		this.undoneOperations.add(this.transformationGraph.removeLastAddedOperation());
		this.updateComposition();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public void redo() {
		this.transformationGraph.addOrInsertOperation(this.undoneOperations.remove(this.undoneOperations.size()-1), false);
		this.updateComposition();
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public void selectCompositionState(Integer vertex) {
		this.transformationGraph.selectCompositionState(vertex);
		this.updateComposition();
		this.firePropertyChange(BigBangController.SELECT_COMPOSITION_STATE, null, vertex);
	}
	
	public void deselectCompositionStates() {
		this.transformationGraph.deselectCompositionStates();
		this.updateComposition();
		this.firePropertyChange(BigBangController.DESELECT_COMPOSITION_STATES, null, null);
	}
	
	public void selectOperation(AbstractOperationEdit edge) {
		this.transformationGraph.selectOperation(edge);
		this.firePropertyChange(BigBangController.SELECT_OPERATION, null, edge);
	}
	
	public void deselectOperations() {
		this.transformationGraph.selectOperation(null);
		this.firePropertyChange(BigBangController.DESELECT_OPERATIONS, null, null);
	}
	
	public void setOperationDuration(AbstractOperationEdit operation, Double duration) {
		operation.setDuration(duration);
		this.transformationGraph.update();
	}
	
	public void toggleGraphAnimation() {
		if (this.animator != null && this.animator.isAlive()) {
			this.animator.end();
		} else {
			if (this.animator == null) {
				this.animator = new BigBangGraphAnimator(this.transformationGraph, this);
			} else {
				double previousPosition = this.animator.getPositionInPercent();
				this.animator = new BigBangGraphAnimator(this.transformationGraph, this);
				if (previousPosition < 1) {
					this.animator.setPosition(previousPosition);
				}
			}
			this.animator.start();
		}
	}
	
	/**
	 * @param position between 0 and 1
	 */
	public void setGraphAnimationPosition(Double position) {
		if (this.animator == null || !this.animator.isAlive()) {
			this.animator = new BigBangGraphAnimator(this.transformationGraph, this);
		}
		this.animator.setPosition(position);
	}
	
	/**
	 * Splits the currently selected operation at the current position of the animator if it is splittable
	 */
	public void splitOperation() {
		this.transformationGraph.splitSelectedAndParallelOperations(this.animator.getPositionInSeconds());
		this.animator.setGraph(this.transformationGraph);
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationGraph);
	}
	
	public BigBangTransformationGraph getTransformationGraph() {
		return this.transformationGraph;
	}
	
	private void updateComposition(boolean inPreviewMode, int stateToBeShown) {
		//select right composition state so that new transformation is shown!!
		Integer previouslySelectedState = this.transformationGraph.getSelectedCompositionState();
		if (inPreviewMode && previouslySelectedState != null && previouslySelectedState < stateToBeShown) {
			this.transformationGraph.selectCompositionState(stateToBeShown);
		}
		this.updateComposition();
		//deselect state after operation in case it was selected for preview purposes
		if (inPreviewMode && previouslySelectedState != null && previouslySelectedState < stateToBeShown) {
			this.transformationGraph.selectCompositionState(previouslySelectedState);
		//if state was selected before, select the one after the inserted operation 
		} else if (previouslySelectedState != null && previouslySelectedState == stateToBeShown-1) {
			this.transformationGraph.selectCompositionState(stateToBeShown);
		}
	}
	
	public void updateComposition() {
		if (this.transformationGraph.getEdgeCount() > 0) {
			List<AbstractOperationEdit> operationsToBeExecuted = this.transformationGraph.getCurrentlyExecutedOperationsInOrder();
			this.denotators.reset();
			
			OperationPathResults currentPathResults = null;
			for (int i = 0; i < operationsToBeExecuted.size(); i++) {
				AbstractOperationEdit currentOperation = operationsToBeExecuted.get(i);
				AbstractOperationEdit nextOperation = null;
				if (i < operationsToBeExecuted.size()-1) {
					nextOperation = operationsToBeExecuted.get(i+1);
				}
				PerformanceCheck.startTask("execute");
				currentPathResults = currentOperation.execute();
				PerformanceCheck.startTask("update paths");
				this.objects.updatePaths(currentOperation, nextOperation, currentPathResults);
			}
			//System.out.println("BO "+this.objects.getAllObjects() + "\n");
			//System.out.println(operationsToBeExecuted + " " + this.objects.getObjects());
			
			PerformanceCheck.startTask("extract");
			new DenotatorValueExtractor(this.objects, this.denotators.getComposition());
			
			//TODO reconsider how to select paths. will be taken from bbobjects or saved in visualobjects..
			PerformanceCheck.startTask("fire");
			this.fireCompositionChange();
		} else {
			this.objects.clearObjects();
			this.fireCompositionChange();
		}
	}
	
	private void fireObjectSelectionChange(Set<BigBangObject> newSelection) {
		this.firePropertyChange(BigBangController.OBJECT_SELECTION, null, newSelection);
	}
	
	private void fireCompositionChange() {
		//TODO REPOLACE BY JUST SENDING ADDED/CHANGED OR REMOVED OBJECTS!!!!
		this.firePropertyChange(BigBangController.COMPOSITION, null, this.objects);
	}

}
