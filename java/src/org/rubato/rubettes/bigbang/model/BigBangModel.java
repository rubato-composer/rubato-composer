package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.yoneda.PowerDenotator;
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
import org.rubato.rubettes.util.NotePath;

public class BigBangModel extends Model {
	
	private UndoRedoModel undoRedoModel;
	private boolean inputActive;
	private boolean multiTouch;
	private BigBangScoreManager score;
	
	public BigBangModel(BigBangController controller) {
		controller.addModel(this);
		this.undoRedoModel = new UndoRedoModel(controller);
		this.score = new BigBangScoreManager(controller);
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
	
	public boolean setComposition(PowerDenotator newComposition) {
		this.undoRedoModel.reset();
		return this.score.setComposition(newComposition);
	}
	
	public PowerDenotator getComposition() {
		return this.score.getComposition();
	}
	
	public void addNote(double[] denotatorValues) {
		this.undoRedoModel.postEdit(new AddNoteEdit(this.score, denotatorValues));
	}
	
	public void deleteNotes(ArrayList<NotePath> nodePaths) {
		this.undoRedoModel.postEdit(new DeleteNotesEdit(this.score, nodePaths));
	}
	
	public void copyNotes(TreeSet<NotePath> nodePaths, Integer layerIndex) {
		this.undoRedoModel.postEdit(new CopyNotesEdit(this.score, nodePaths, layerIndex));
	}
	
	public void moveNotes(TreeSet<NotePath> nodePaths, Integer layerIndex) {
		this.undoRedoModel.postEdit(new MoveNotesEdit(this.score, nodePaths, layerIndex));
	}
	
	public void translateNotes(TransformationProperties properties) {
		this.doTransformation(properties, new TranslationEdit(this.score, properties));
	}
	
	public void rotateNotes(TransformationProperties properties, Double angle) {
		this.doTransformation(properties, new RotationEdit(this.score, properties, angle));
	}
	
	public void scaleNotes(TransformationProperties properties, double[] scaleFactors) {
		this.doTransformation(properties, new ScalingEdit(this.score, properties, scaleFactors));
	}
	
	public void reflectNotes(TransformationProperties properties, double[] reflectionVector) {
		this.doTransformation(properties, new ReflectionEdit(this.score, properties, reflectionVector));
	}
	
	public void shearNotes(TransformationProperties properties, double[] shearingFactors) {
		this.doTransformation(properties, new ShearingEdit(this.score, properties, shearingFactors));
	}
	
	public void shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.doTransformation(properties, new ShapingEdit(this.score, properties, shapingLocations));
	}
	
	public void affineTransformNotes(TransformationProperties properties, double[] shift, Double angle, double[] scaleFactors) {
		this.doTransformation(properties, new AffineTransformationEdit(this.score, properties, shift, angle, scaleFactors));
	}
	
	private void doTransformation(TransformationProperties properties, AbstractUndoableEdit edit) {
		if (!properties.inPreviewMode()) {
			this.undoRedoModel.postEdit(edit);
		}
	}
	
	public void buildSatellites(TreeSet<NotePath> nodePaths, NotePath parentNotePath) {
		this.undoRedoModel.postEdit(new SatelliteBuildingEdit(this.score, nodePaths, parentNotePath));
	}
	
	public void flattenNotes(TreeSet<NotePath> nodePaths) {
		this.undoRedoModel.postEdit(new FlattenNotesEdit(this.score, nodePaths));
	}
	
	public void buildModulators(TreeSet<NotePath> nodePaths, NotePath carrierNotePath) {
		this.undoRedoModel.postEdit(new ModulatorBuildingEdit(this.score, nodePaths, carrierNotePath));
	}
	
	public void removeNotesFromCarrier(TreeSet<NotePath> nodePaths) {
		this.undoRedoModel.postEdit(new RemoveNotesFromCarrierEdit(this.score, nodePaths));
	}
	
	public void addWallpaperDimension(Integer rangeFrom, Integer rangeTo) {
		this.undoRedoModel.postEdit(new AddWallpaperDimensionEdit(this.score, rangeFrom, rangeTo));
	}

}
