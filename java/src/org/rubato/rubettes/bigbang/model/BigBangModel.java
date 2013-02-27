package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AddNoteEdit;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.model.edits.AffineTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.CopyNotesEdit;
import org.rubato.rubettes.bigbang.model.edits.DeleteNotesEdit;
import org.rubato.rubettes.bigbang.model.edits.FlattenNotesEdit;
import org.rubato.rubettes.bigbang.model.edits.ModulatorBuildingEdit;
import org.rubato.rubettes.bigbang.model.edits.MoveNotesEdit;
import org.rubato.rubettes.bigbang.model.edits.ReflectionEdit;
import org.rubato.rubettes.bigbang.model.edits.RemoveNotesFromCarrierEdit;
import org.rubato.rubettes.bigbang.model.edits.RotationEdit;
import org.rubato.rubettes.bigbang.model.edits.SatelliteBuildingEdit;
import org.rubato.rubettes.bigbang.model.edits.ScalingEdit;
import org.rubato.rubettes.bigbang.model.edits.ShapingEdit;
import org.rubato.rubettes.bigbang.model.edits.ShearingEdit;
import org.rubato.rubettes.bigbang.model.edits.TranslationEdit;
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
	
	public boolean setComposition(Denotator newComposition) {
		this.undoRedoModel.reset();
		return this.scoreManager.setComposition(newComposition);
	}
	
	public Denotator getComposition() {
		return this.scoreManager.getComposition();
	}
	
	public void addNote(double[] denotatorValues) {
		this.undoRedoModel.postEdit(new AddNoteEdit(this.scoreManager, denotatorValues));
	}
	
	public void deleteNotes(ArrayList<DenotatorPath> nodePaths) {
		this.undoRedoModel.postEdit(new DeleteNotesEdit(this.scoreManager, nodePaths));
	}
	
	public void copyNotes(TreeSet<DenotatorPath> nodePaths, Integer layerIndex) {
		this.undoRedoModel.postEdit(new CopyNotesEdit(this.scoreManager, nodePaths, layerIndex));
	}
	
	public void moveNotes(TreeSet<DenotatorPath> nodePaths, Integer layerIndex) {
		this.undoRedoModel.postEdit(new MoveNotesEdit(this.scoreManager, nodePaths, layerIndex));
	}
	
	public void translateNotes(TransformationProperties properties) {
		this.doTransformation(properties, new TranslationEdit(this.scoreManager, properties));
	}
	
	public void rotateNotes(TransformationProperties properties, Double angle) {
		this.doTransformation(properties, new RotationEdit(this.scoreManager, properties, angle));
	}
	
	public void scaleNotes(TransformationProperties properties, double[] scaleFactors) {
		this.doTransformation(properties, new ScalingEdit(this.scoreManager, properties, scaleFactors));
	}
	
	public void reflectNotes(TransformationProperties properties, double[] reflectionVector) {
		this.doTransformation(properties, new ReflectionEdit(this.scoreManager, properties, reflectionVector));
	}
	
	public void shearNotes(TransformationProperties properties, double[] shearingFactors) {
		this.doTransformation(properties, new ShearingEdit(this.scoreManager, properties, shearingFactors));
	}
	
	public void shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.doTransformation(properties, new ShapingEdit(this.scoreManager, properties, shapingLocations));
	}
	
	public void affineTransformNotes(TransformationProperties properties, double[] shift, Double angle, double[] scaleFactors) {
		this.doTransformation(properties, new AffineTransformationEdit(this.scoreManager, properties, shift, angle, scaleFactors));
	}
	
	private void doTransformation(TransformationProperties properties, AbstractUndoableEdit edit) {
		if (!properties.inPreviewMode() && !properties.getNodePaths().isEmpty()) {
			this.undoRedoModel.postEdit(edit);
		} else {
			this.undoRedoModel.previewTransformationAtEnd(edit);
		}
	}
	
	public void buildSatellites(TreeSet<DenotatorPath> nodePaths, DenotatorPath parentNotePath) {
		this.undoRedoModel.postEdit(new SatelliteBuildingEdit(this.scoreManager, nodePaths, parentNotePath));
	}
	
	public void flattenNotes(TreeSet<DenotatorPath> nodePaths) {
		this.undoRedoModel.postEdit(new FlattenNotesEdit(this.scoreManager, nodePaths));
	}
	
	public void buildModulators(TreeSet<DenotatorPath> nodePaths, DenotatorPath carrierNotePath) {
		this.undoRedoModel.postEdit(new ModulatorBuildingEdit(this.scoreManager, nodePaths, carrierNotePath));
	}
	
	public void removeNotesFromCarrier(TreeSet<DenotatorPath> nodePaths) {
		this.undoRedoModel.postEdit(new RemoveNotesFromCarrierEdit(this.scoreManager, nodePaths));
	}
	
	public void addWallpaperDimension(Integer rangeFrom, Integer rangeTo) {
		this.undoRedoModel.postEdit(new AddWallpaperDimensionEdit(this.scoreManager, rangeFrom, rangeTo));
	}

}
