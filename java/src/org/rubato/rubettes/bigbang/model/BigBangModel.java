package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.model.edits.AffineTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AlterationEdit;
import org.rubato.rubettes.bigbang.model.edits.CopyObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.DeleteObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.EndWallpaperEdit;
import org.rubato.rubettes.bigbang.model.edits.FlattenEdit;
import org.rubato.rubettes.bigbang.model.edits.MoveNotesEdit;
import org.rubato.rubettes.bigbang.model.edits.ReflectionEdit;
import org.rubato.rubettes.bigbang.model.edits.RotationEdit;
import org.rubato.rubettes.bigbang.model.edits.BuildSatellitesEdit;
import org.rubato.rubettes.bigbang.model.edits.ScalingEdit;
import org.rubato.rubettes.bigbang.model.edits.SetOrAddCompositionEdit;
import org.rubato.rubettes.bigbang.model.edits.ShapingEdit;
import org.rubato.rubettes.bigbang.model.edits.ShearingEdit;
import org.rubato.rubettes.bigbang.model.edits.TranslationEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangModel extends Model {
	
	private UndoRedoModel undoRedoModel;
	private boolean inputActive;
	private boolean multiTouch;
	private BigBangScoreManager scoreManager;
	
	public BigBangModel(BigBangController controller) {
		controller.addModel(this);
		this.undoRedoModel = new UndoRedoModel(controller);
		this.scoreManager = new BigBangScoreManager(controller);
		this.setInputActive(true);
	}
	
	public void setInputActive(Boolean inputActive) {
		this.inputActive = inputActive;
		this.firePropertyChange(BigBangController.INPUT_ACTIVE, null, this.inputActive);
	}
	
	public boolean isInputActive() {
		return this.inputActive;
	}
	
	public void setMultiTouch(Boolean multiTouch) {
		this.multiTouch = multiTouch;
		this.firePropertyChange(BigBangController.MULTITOUCH, null, this.multiTouch);
	}
	
	public void setOrAddComposition(Denotator composition) {
		if (!this.scoreManager.isFormCompatibleWithCurrentForm(composition.getForm())) {
			this.undoRedoModel.reset();
		} else if (this.undoRedoModel.getLastAddedEdit() instanceof SetOrAddCompositionEdit) {
			((SetOrAddCompositionEdit)this.undoRedoModel.getLastAddedEdit()).setComposition(composition);
		} else if (this.undoRedoModel.getTransformationGraph().getSelectedOperation() instanceof SetOrAddCompositionEdit) {
			((SetOrAddCompositionEdit)this.undoRedoModel.getTransformationGraph().getSelectedOperation()).setComposition(composition);
		} else {
			this.undoRedoModel.postEdit(new SetOrAddCompositionEdit(this.scoreManager, composition));
		}
	}
	
	public Denotator getComposition() {
		return this.scoreManager.getComposition();
	}
	
	public void addObjects(ArrayList<Map<DenotatorPath,Double>> pathsWithValues, ArrayList<DenotatorPath> powersetPaths, Boolean inPreviewMode) {
		if (this.scoreManager != null) { //needs to be checked for milmeister ghost rubette reacting to leap motion 
			AbstractOperationEdit lastEdit = this.undoRedoModel.getLastAddedEdit();
			if (lastEdit != null && lastEdit instanceof AddObjectsEdit) {
				AddObjectsEdit addEdit = (AddObjectsEdit) lastEdit;
				if (addEdit.addObjects(pathsWithValues, powersetPaths, inPreviewMode)) {
					this.undoRedoModel.modifiedOperation(false);
					return;
				}
			}
			this.undoRedoModel.postEdit(new AddObjectsEdit(this.scoreManager, pathsWithValues, powersetPaths));
		}
	}
	
	public void deleteObjects(ArrayList<DenotatorPath> objectPaths) {
		this.undoRedoModel.postEdit(new DeleteObjectsEdit(this.scoreManager, objectPaths));
	}
	
	public void copyObjects(TreeSet<DenotatorPath> nodePaths) {
		this.undoRedoModel.postEdit(new CopyObjectsEdit(this.scoreManager, nodePaths));
	}
	
	public void moveObjects(TreeSet<DenotatorPath> nodePaths, Integer layerIndex) {
		this.undoRedoModel.postEdit(new MoveNotesEdit(this.scoreManager, nodePaths, layerIndex));
	}
	
	public void translateObjects(TransformationProperties properties) {
		this.doTransformation(properties, new TranslationEdit(this.scoreManager, properties));
	}
	
	public void rotateObjects(TransformationProperties properties, double[] startingPoint, Double angle) {
		this.doTransformation(properties, new RotationEdit(this.scoreManager, properties, startingPoint, angle));
	}
	
	public void scaleObjects(TransformationProperties properties, double[] scaleFactors) {
		this.doTransformation(properties, new ScalingEdit(this.scoreManager, properties, scaleFactors));
	}
	
	public void reflectObjects(TransformationProperties properties, double[] reflectionVector) {
		this.doTransformation(properties, new ReflectionEdit(this.scoreManager, properties, reflectionVector));
	}
	
	public void shearObjects(TransformationProperties properties, double[] shearingFactors) {
		this.doTransformation(properties, new ShearingEdit(this.scoreManager, properties, shearingFactors));
	}
	
	public void shapeObjects(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.doTransformation(properties, new ShapingEdit(this.scoreManager, properties, shapingLocations));
	}
	
	public void affineTransformObjects(TransformationProperties properties, double[] shift, Double angle, double[] scaleFactors) {
		this.doTransformation(properties, new AffineTransformationEdit(this.scoreManager, properties, shift, angle, scaleFactors));
	}
	
	private void doTransformation(TransformationProperties properties, AbstractUndoableEdit edit) {
		if (!properties.inPreviewMode() && properties.getObjectsPaths().totalObjectPaths() > 0) {
			this.undoRedoModel.postEdit(edit);
		} else {
			this.undoRedoModel.previewTransformation((AbstractTransformationEdit)edit);
		}
	}
	
	public void buildSatellites(ArrayList<DenotatorPath> nodePaths, DenotatorPath parentNotePath, Integer powersetIndex) {
		this.undoRedoModel.postEdit(new BuildSatellitesEdit(this.scoreManager, nodePaths, parentNotePath, powersetIndex));
	}
	
	public void flattenObjects(ArrayList<DenotatorPath> nodePaths) {
		this.undoRedoModel.postEdit(new FlattenEdit(this.scoreManager, nodePaths));
	}
	
	public void addWallpaperDimension(SelectedObjectsPaths objectPaths, Integer rangeFrom, Integer rangeTo) {
		this.undoRedoModel.postEdit(new AddWallpaperDimensionEdit(this.scoreManager, objectPaths, rangeFrom, rangeTo));
	}
	
	public void endWallpaper() {
		this.undoRedoModel.postEdit(new EndWallpaperEdit(this.scoreManager));
	}
	
	public void addAlteration() {
		this.undoRedoModel.postEdit(new AlterationEdit(this.scoreManager));
		this.firePropertyChange(BigBangController.MODIFY_OPERATION, null, this.undoRedoModel.getLastAddedEdit());
	}
	
	public UndoRedoModel getUndoRedoModel() {
		return this.undoRedoModel;
	}

}
