package org.rubato.rubettes.util;

import java.util.List;
import java.util.ArrayList;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.module.ZElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.LimitForm;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.PowerForm;

public class MacroNoteGenerator extends NoteGenerator {
	
	private LimitForm nodeForm = (LimitForm) Repository.systemRepository().getForm("Knot");
	private LimitForm noteForm = (LimitForm) Repository.systemRepository().getForm("Note");
	
	public MacroNoteGenerator() {
		super();
	}
	
	public PowerDenotator createEmptyScore() {
		return this.createMacroScore(new ArrayList<Denotator>());
	}
	
	public Denotator convertScore(Denotator input) {
		Form inputForm = input.getForm();
		if (inputForm.equals(this.macroScoreForm)) {
			return this.splitVoicesToLayers((PowerDenotator)input);
		} else if (inputForm.equals(this.scoreForm)) {
			return this.convertScoreToMacroScore((PowerDenotator)input);
		} else if (inputForm.equals(this.soundScoreForm)) {
			return this.convertToSpecificScore((PowerDenotator)input);
		} else return input;
	}
	
	/**
	 * Splits the anchor notes to different layers depending on their voice. All satellites
	 * are moved to the layer of their anchor note.
	 * @param macroScore
	 */
	private PowerDenotator splitVoicesToLayers(PowerDenotator macroScore) {
		int[] voicePath = new int[]{4,0};
		PowerDenotator newMacroScore = this.createEmptyScore();
		try {
			List<Denotator> factors = macroScore.getFactors();
			for (int i = 0; i < factors.size(); i++) {
				LimitDenotator currentNode = (LimitDenotator)factors.get(i);
				LimitDenotator currentNote = (LimitDenotator)currentNode.getFactor(0);
				ZElement voiceElement = (ZElement)currentNote.getElement(voicePath).clone();
				Denotator currentLayer = this.createSimpleDenotator(this.layerForm, voiceElement);
				currentNote.setFactor(5, currentLayer);
				PowerDenotator currentMacroScore = this.moveToLayer((PowerDenotator)currentNode.getFactor(1), voiceElement.getValue());
				newMacroScore.appendFactor(this.createNodeDenotator(currentNote, currentMacroScore));
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return newMacroScore;
	}
	
	private PowerDenotator convertScoreToMacroScore(PowerDenotator score) {
		List<Denotator> nodes = new ArrayList<Denotator>();
		try {
			for (Denotator currentNote: score.getFactors()) {
				this.setLayerToVoice((LimitDenotator)currentNote);
				nodes.add(this.createNodeDenotator(currentNote));
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return this.createMacroScore(nodes);
	}
	
	public PowerDenotator createFlatMacroScore(double[][] values) {
		List<Denotator> nodes = new ArrayList<Denotator>();
		for (double[] currentValues: values) {
			Denotator currentNote = this.createNoteDenotator(currentValues);
			nodes.add(this.createNodeDenotator(currentNote));
		}
		return this.createMacroScore(nodes);
	}
	
	public PowerDenotator createMultiLevelMacroScore(double[][] values) {
		PowerDenotator macroScore = this.createEmptyScore();
		for (int i = values.length-1; i >= 0; i--) {
			double[] currentValues = values[i];
			Denotator currentNote = this.createNoteDenotator(currentValues);
			List<Denotator> currentNodes = new ArrayList<Denotator>();
			currentNodes.add(this.createNodeDenotator(currentNote, macroScore));
			macroScore = this.createMacroScore(currentNodes);
		}
		return macroScore;
	}
	
	private PowerDenotator createMacroScore(List<Denotator> nodes) {
		try {
			return new PowerDenotator(this.emptyName, this.macroScoreForm, nodes);
		} catch (RubatoException e) {
			return null;
		}
	}
	
	public LimitDenotator createSpecificNodeDenotator(List<Denotator> coordinates) {
		try {
			return new LimitDenotator(this.emptyName, this.nodeForm, coordinates);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public LimitDenotator createSpecificNoteDenotator(List<Denotator> coordinates) {
		try {
			return new LimitDenotator(this.emptyName, this.noteForm, coordinates);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the MacroScore form from the system repository.
	 */
	public PowerForm getMacroScoreForm() { return this.macroScoreForm; }
	
}
