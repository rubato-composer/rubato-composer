package org.rubato.rubettes.bigbang.model;

import static org.rubato.xml.XMLConstants.FORM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.denotators.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AddOrInsertOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.RemoveOperationEdit;
import org.rubato.rubettes.bigbang.model.graph.BigBangGraphAnimator;
import org.rubato.rubettes.bigbang.model.graph.BigBangOperationGraph;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.model.operations.AbstractTransformation;
import org.rubato.rubettes.bigbang.model.operations.AddObjectsOperation;
import org.rubato.rubettes.bigbang.model.operations.AddWallpaperDimensionOperation;
import org.rubato.rubettes.bigbang.model.operations.AffineTransformation;
import org.rubato.rubettes.bigbang.model.operations.AlterationOperation;
import org.rubato.rubettes.bigbang.model.operations.BuildSatellitesOperation;
import org.rubato.rubettes.bigbang.model.operations.DeleteObjectsOperation;
import org.rubato.rubettes.bigbang.model.operations.EndWallpaperOperation;
import org.rubato.rubettes.bigbang.model.operations.FlattenOperation;
import org.rubato.rubettes.bigbang.model.operations.ReflectionTransformation;
import org.rubato.rubettes.bigbang.model.operations.RotationTransformation;
import org.rubato.rubettes.bigbang.model.operations.ScalingTransformation;
import org.rubato.rubettes.bigbang.model.operations.InputCompositionOperation;
import org.rubato.rubettes.bigbang.model.operations.ShapingOperation;
import org.rubato.rubettes.bigbang.model.operations.ShearingTransformation;
import org.rubato.rubettes.bigbang.model.operations.TranslationTransformation;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.PerformanceCheck;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

public class BigBangModel extends Model {
	
	private boolean inputActive;
	private boolean isMultiTouch;
	private BigBangController controller;
	private BigBangDenotatorManager denotators;
	private BigBangObjects objects; //object-oriented representation of the denotator composition
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	private BigBangOperationGraph operationGraph;
	private BigBangGraphAnimator animator;
	
	public BigBangModel() {
		this.denotators = new BigBangDenotatorManager();
		this.setInputActive(true);
		this.objects = new BigBangObjects(this.denotators.getForm());
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.reset();
	}
	
	public void setController(BigBangController controller) {
		this.controller = controller;
		controller.addModel(this);
		this.objects.setController(controller);
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
		this.fireCompositionChange();
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.operationGraph = new BigBangOperationGraph();
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	private void setGraph(BigBangOperationGraph graph) {
		this.operationGraph = graph;
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
		//TODO strange, full comparison does not work....
		if (!form.getName().equals(this.denotators.getForm().getName())) {
			this.reset();
			this.denotators.setForm(form);
			this.objects = new BigBangObjects(this.denotators.getForm());
			if (this.controller != null) {
				this.objects.setController(this.controller);
			}
			this.fireCompositionChange();
		}
	}
	
	public void newWindowAdded() {
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
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
		if (this.operationGraph.getSelectedOperation() instanceof InputCompositionOperation) {
			((InputCompositionOperation)this.operationGraph.getSelectedOperation()).setOrAddComposition(composition);
			this.operationModified();
		} else {
			this.addOperation(new InputCompositionOperation(this, composition));
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
			AbstractOperation lastEdit = this.operationGraph.getLastAddedOperation();
			if (lastEdit != null && lastEdit instanceof AddObjectsOperation) {
				AddObjectsOperation addEdit = (AddObjectsOperation) lastEdit;
				if (addEdit.addObjects(pathsWithValues, powersetPaths, inPreviewMode)) {
					this.operationModified();
					return;
				}
			}
			this.addOperation(new AddObjectsOperation(this, pathsWithValues, powersetPaths, inPreviewMode));
		}
	}
	
	public void unAddObjects(TreeSet<Map<DenotatorPath,Double>> pathsWithValues) {
		AddObjectsOperation edit = null;
		if (this.operationGraph.getSelectedOperation() instanceof AddObjectsOperation) {
			edit = (AddObjectsOperation)this.operationGraph.getSelectedOperation();
		} else if (this.operationGraph.getLastAddedOperation() != null && this.operationGraph.getLastAddedOperation() instanceof AddObjectsOperation) {
			edit = (AddObjectsOperation)this.operationGraph.getLastAddedOperation();
		}
		if (edit != null) {
			edit.unAddObjects(pathsWithValues);
			this.operationModified();
		}
	}
	
	public BigBangObjects getObjects() {
		return this.objects;
	}
	
	public void deleteObjects(TreeSet<BigBangObject> objects) {
		AbstractOperation lastEdit = this.operationGraph.getLastAddedOperation();
		if (lastEdit != null && lastEdit instanceof DeleteObjectsOperation) {
			((DeleteObjectsOperation)lastEdit).addObjects(objects);
			this.operationModified();
			return;
		}
		this.addOperation(new DeleteObjectsOperation(this, objects));
	}
	
	public void translateObjects(TransformationProperties properties) {
		if (properties.startNewTransformation()) {
			this.addOperation(new TranslationTransformation(this, properties));
		} else if (this.updateTransformation(properties, TranslationTransformation.class)) {
			this.updateComposition();
		}
	}
	
	public void rotateObjects(TransformationProperties properties, double[] startingPoint, Double angle) {
		if (properties.startNewTransformation()) {
			this.addOperation(new RotationTransformation(this, properties, startingPoint, angle));
		} else if (this.updateTransformation(properties, RotationTransformation.class)) {
			RotationTransformation lastRotation = (RotationTransformation)this.operationGraph.getLastAddedOperation();
			lastRotation.setParameters(startingPoint, angle);
			this.updateComposition();
		}
	}
	
	public void scaleObjects(TransformationProperties properties, double[] scaleFactors) {
		if (properties.startNewTransformation()) {
			this.addOperation(new ScalingTransformation(this, properties, scaleFactors));
		} else if (this.updateTransformation(properties, ScalingTransformation.class)) {
			this.modifyLastTransformation(scaleFactors);
		}
	}
	
	public void reflectObjects(TransformationProperties properties, double[] reflectionVector) {
		if (properties.startNewTransformation()) {
			this.addOperation(new ReflectionTransformation(this, properties, reflectionVector));
		} else if (this.updateTransformation(properties, ReflectionTransformation.class)) {
			this.modifyLastTransformation(reflectionVector);
		}
	}
	
	public void shearObjects(TransformationProperties properties, double[] shearingFactors) {
		if (properties.startNewTransformation()) {
			this.addOperation(new ShearingTransformation(this, properties, shearingFactors));
		} else if (this.updateTransformation(properties, ShearingTransformation.class)) {
			this.modifyLastTransformation(shearingFactors);
		}
	}
	
	private boolean updateTransformation(TransformationProperties properties, Class<?> transformationClass) {
		AbstractOperation lastOperation = this.operationGraph.getLastAddedOperation();
		if (transformationClass.isInstance(lastOperation)) {
			((AbstractTransformation)lastOperation).updateProperties(properties);
			return true;
		}
		return false;
	}
	
	private void modifyLastTransformation(double[] newValues) {
		AbstractTransformation lastTransformation = (AbstractTransformation)this.operationGraph.getLastAddedOperation();
		lastTransformation.modify(newValues);
		this.updateComposition();
	}
	
	public void shapeObjects(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		ShapingOperation edit = null;
		if (this.operationGraph.getSelectedOperation() instanceof ShapingOperation) {
			edit = (ShapingOperation)this.operationGraph.getSelectedOperation();
		} else if (this.operationGraph.getLastAddedOperation() instanceof ShapingOperation) {
			edit = (ShapingOperation)this.operationGraph.getLastAddedOperation();
		}
		if (edit != null && edit.getShapingPaths().equals(properties.getTransformationPaths())) {
			edit.addShapingLocations(shapingLocations);
			this.updateComposition();
		} else {	
			this.addOperation(new ShapingOperation(this, properties, shapingLocations));
		}
	}
	
	public void affineTransformObjects(TransformationProperties properties, double[] shift, RMatrix transform) {
		this.addOperation(new AffineTransformation(this, properties, shift, transform));
	}
	
	public void buildSatellites(TreeSet<BigBangObject> objects, BigBangObject anchorObject, Integer powersetIndex) {
		this.addOperation(new BuildSatellitesOperation(this, objects, anchorObject, powersetIndex));
	}
	
	public void flattenObjects(TreeSet<BigBangObject> objects) {
		this.addOperation(new FlattenOperation(this, objects));
	}
	
	public void addWallpaperDimension(TreeSet<BigBangObject> objectPaths, Integer rangeFrom, Integer rangeTo) {
		this.addOperation(new AddWallpaperDimensionOperation(this, objectPaths, rangeFrom, rangeTo));
	}
	
	public void endWallpaper() {
		this.addOperation(new EndWallpaperOperation(this));
	}
	
	public void addAlteration(DenotatorPath degreesDimensionPath) {
		this.addOperation(new AlterationOperation(this, degreesDimensionPath));
		this.firePropertyChange(BigBangController.MODIFY_OPERATION, null, this.operationGraph.getLastAddedOperation());
	}
	
	public void fireAlterationComposition(Integer index) {
		if (this.operationGraph.getSelectedOperation() instanceof AlterationOperation) {
			//this.alteration.resetDegrees();
			this.fireObjectSelectionChange(((AlterationOperation)this.operationGraph.getSelectedOperation()).getAlterationComposition(index));
			this.firePropertyChange(BigBangController.FIRE_ALTERATION_COMPOSITION, null, index);
		}
	}
	
	public void setAlterationStartDegree(Double startDegree) {
		if (this.operationGraph.getSelectedOperation() instanceof AlterationOperation) {
			((AlterationOperation)this.operationGraph.getSelectedOperation()).setStartDegree(startDegree);
		}
	}
	
	public void setAlterationEndDegree(Double endDegree) {
		if (this.operationGraph.getSelectedOperation() instanceof AlterationOperation) {
			((AlterationOperation)this.operationGraph.getSelectedOperation()).setEndDegree(endDegree);
		}
	}
	
	public void setAlterationDegreesDimension(DenotatorPath path) {
		if (this.operationGraph.getSelectedOperation() instanceof AlterationOperation) {
			((AlterationOperation)this.operationGraph.getSelectedOperation()).setDegreesDimensionPath(path);
		}
	}
	
	private void addOperation(AbstractOperation operation) {
		AddOrInsertOperationEdit edit = new AddOrInsertOperationEdit(operation, this.operationGraph);
		edit.execute();
		this.postEdit(edit);
	}
	
	public void removeOperation(AbstractOperation operation) {
		RemoveOperationEdit edit = new RemoveOperationEdit(operation, this.operationGraph);
		edit.execute();
		this.postEdit(edit);
		this.objects.removeOperation(operation);
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	private void postEdit(AbstractUndoableEdit edit) {
		this.undoSupport.postEdit(edit);
		this.updateComposition();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
		//this.firePropertyChange(BigBangController.SELECT_COMPOSITION_STATE, null, this.transformationGraph.getSelectedCompositionState());
	}
	
	public void operationModified() {
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	public void modifyOperation(Integer operationIndex, Double ratio) {
		if (operationIndex >= 0 && this.operationGraph.getEdgeCount() > operationIndex) {
			DijkstraShortestPath<CompositionState,AbstractOperation> dijkstra = new DijkstraShortestPath<CompositionState,AbstractOperation>(this.operationGraph);
		    List<AbstractOperation> shortestPath = dijkstra.getPath(this.operationGraph.getFirstState(), this.operationGraph.getLastState());
		    AbstractOperation operation = shortestPath.get(operationIndex);
		    operation.modify(ratio);
			this.updateComposition();
			this.firePropertyChange(BigBangController.MODIFY_OPERATION, null, operation);
		}
	}
	
	public void setOperationDurations(double duration) {
		this.operationGraph.setDurations(duration);
	}
	
	public void setInsertionState(Integer stateIndex) {
		this.operationGraph.setInsertionState(stateIndex);
	}
	
	public void undo() {
		AbstractOperation lastAddedOperation = this.operationGraph.getLastAddedOperation();
		CompositionState state = this.operationGraph.getSource(lastAddedOperation);
		//this.undoneOperations.add(this.transformationGraph.removeLastAddedOperation());
		this.undoManager.undo();
		this.updateComposition();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	public void redo() {
		//this.transformationGraph.addOrInsertOperation(this.undoneOperations.remove(this.undoneOperations.size()-1), false);
		this.undoManager.redo();
		this.updateComposition();
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	public void selectPreviousCompositionState() {
		if (this.operationGraph.getSelectedCompositionState() != null) {
			if (this.operationGraph.getSelectedCompositionState().getIndex() > 0) {
				this.selectCompositionStateAt(this.operationGraph.getSelectedCompositionState().getIndex()-1);
			}
		} else if (this.operationGraph.getVertexCount() > 1) {
			this.selectCompositionStateAt(this.operationGraph.getVertexCount()-1);
		} else {
			this.selectCompositionStateAt(0);
		}
	}
	
	public void selectNextCompositionState() {
		if (this.operationGraph.getSelectedCompositionState() != null) {
			if (this.operationGraph.getSelectedCompositionState().getIndex() < this.operationGraph.getVertexCount()-1) {
				this.selectCompositionStateAt(this.operationGraph.getSelectedCompositionState().getIndex()+1);
			}
		} else {
			this.selectCompositionStateAt(0);
		}
	}
	
	public void selectCompositionStateAt(Integer stateIndex) {
		this.operationGraph.selectCompositionStateAt(stateIndex);
		this.updateComposition();
		this.firePropertyChange(BigBangController.SELECT_COMPOSITION_STATE, null, this.operationGraph.getSelectedCompositionState());
	}
	
	public void selectCompositionState(CompositionState vertex) {
		this.operationGraph.selectCompositionState(vertex);
		this.updateComposition();
		this.firePropertyChange(BigBangController.SELECT_COMPOSITION_STATE, null, vertex);
	}
	
	public void deselectCompositionStates() {
		this.operationGraph.deselectCompositionStates();
		this.updateComposition();
		this.firePropertyChange(BigBangController.DESELECT_COMPOSITION_STATES, null, null);
	}
	
	public void selectOperation(AbstractOperation edge) {
		this.operationGraph.selectOperation(edge);
		this.firePropertyChange(BigBangController.SELECT_OPERATION, null, edge);
	}
	
	public void deselectOperations() {
		this.operationGraph.selectOperation(null);
		this.firePropertyChange(BigBangController.DESELECT_OPERATIONS, null, null);
	}
	
	public void setOperationDuration(AbstractOperation operation, Double duration) {
		operation.setDuration(duration);
		this.operationGraph.update();
	}
	
	public void toggleGraphAnimation() {
		if (this.animator != null && this.animator.isAlive()) {
			this.animator.end();
		} else {
			if (this.animator == null) {
				this.animator = new BigBangGraphAnimator(this.operationGraph, this);
			} else {
				double previousPosition = this.animator.getPositionInPercent();
				this.animator = new BigBangGraphAnimator(this.operationGraph, this);
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
			this.animator = new BigBangGraphAnimator(this.operationGraph, this);
		}
		this.animator.setPosition(position);
	}
	
	/**
	 * Splits the currently selected operation at the current position of the animator if it is splittable
	 */
	public void splitOperation() {
		this.operationGraph.splitSelectedAndParallelOperations(this.animator.getPositionInSeconds());
		this.animator.setGraph(this.operationGraph);
		this.updateComposition();
		this.firePropertyChange(BigBangController.GRAPH, null, this.operationGraph);
	}
	
	public BigBangOperationGraph getTransformationGraph() {
		return this.operationGraph;
	}
	
	public void updateComposition() {
		if (this.operationGraph.getEdgeCount() > 0) {
			List<AbstractOperation> operationsToBeExecuted = this.operationGraph.getCurrentlyExecutedOperationsInOrder();
			this.denotators.reset();
			
			OperationPathResults currentPathResults = null;
			for (int i = 0; i < operationsToBeExecuted.size(); i++) {
				AbstractOperation currentOperation = operationsToBeExecuted.get(i);
				AbstractOperation nextOperation = null;
				if (i < operationsToBeExecuted.size()-1) {
					nextOperation = operationsToBeExecuted.get(i+1);
				}
				//System.out.println(currentOperation);
				PerformanceCheck.startTask("execute");
				currentPathResults = currentOperation.execute();
				PerformanceCheck.startTask("update paths");
				this.objects.updatePaths(currentOperation, nextOperation, currentPathResults);
			}
			//System.out.println("BO "+this.objects.getObjectsAt(null).size());
			//System.out.println(operationsToBeExecuted + " " + this.objects.getObjects());
			
			PerformanceCheck.startTask("extract");
			new DenotatorValueExtractor(this.objects, this.denotators.getComposition());
			
			//TODO reconsider how to select paths. will be taken from bbobjects or saved in visualobjects..
			PerformanceCheck.startTask("fire");
			this.fireCompositionChange();
		} else {
			this.objects.clearObjects();
			this.denotators.reset();
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
	
	public BigBangModel clone() {
		BigBangModel clonedModel = new BigBangModel();
		clonedModel.setForm(this.denotators.getForm());
		clonedModel.operationGraph = this.operationGraph.clone(clonedModel);
		clonedModel.updateComposition();
		return clonedModel;
	}

	public void toXML(XMLWriter writer) {
		writer.writeFormRef(this.denotators.getForm());
		//System.out.println("TOXML " +this.denotators.getForm());
		this.operationGraph.toXML(writer);
	}
	
	public static BigBangModel fromXML(XMLReader reader, Element element) {
		BigBangModel loadedModel = new BigBangModel();
		try {
			Form form = reader.parseAndResolveForm(XMLReader.getChild(element, FORM));
			loadedModel.setForm(form);
			loadedModel.setGraph(BigBangOperationGraph.fromXML(loadedModel, reader, element));
			//initiates bbobjects and all
			loadedModel.updateComposition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadedModel;
	}

}
