package org.rubato.rubettes.util;

import java.util.List;
import java.util.ArrayList;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.module.DomainException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.QElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.LimitForm;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleDenotator;

public class SoundNoteGenerator extends NoteGenerator {
	
	private LimitForm soundNodeForm = (LimitForm) Repository.systemRepository().getForm("SoundNode");
	private LimitForm soundNoteForm = (LimitForm) Repository.systemRepository().getForm("SoundNote");
	private PowerForm modulatorsForm = (PowerForm) Repository.systemRepository().getForm("Modulators");
	public static final String[] FM_MODELS = {"Carrier only", "Double modulator", "Nested modulator"};
	
	private String fmModel = FM_MODELS[0];
	
	public PowerDenotator createEmptyScore() {
		return this.createSoundScore(new ArrayList<Denotator>());
	}
	
	public Denotator convertScore(Denotator input) {
		Form inputForm = input.getForm();
		if (inputForm.equals(this.soundScoreForm)) {
			return input;
		} else if (inputForm.equals(this.macroScoreForm)) {
			return this.convertToSpecificScore((PowerDenotator)input);
		} else if (inputForm.equals(this.scoreForm)) {
			return this.convertScoreToSoundScore((PowerDenotator)input);
		} else return input;
	}
	
	private PowerDenotator convertScoreToSoundScore(PowerDenotator score) {
		List<Denotator> soundNodes = new ArrayList<Denotator>();
		try {
			for (Denotator currentNote: score.getFactors()) {
				Denotator currentSoundNote = this.convertToSoundNote((LimitDenotator)currentNote, true);
				this.setLayerToVoice((LimitDenotator)currentSoundNote);
				soundNodes.add(this.createNodeDenotator(currentSoundNote));
			}
		} catch (RubatoException e) { e.printStackTrace(); }
		return this.createSoundScore(soundNodes);
	}
	
	public PowerDenotator createFlatSoundScore(double[][] values) {
		List<Denotator> nodes = new ArrayList<Denotator>();
		for (double[] currentValues: values) {
			Denotator currentNote = this.createNoteDenotator(currentValues);
			nodes.add(this.createNodeDenotator(currentNote));
		}
		return this.createSoundScore(nodes);
	}
	
	public PowerDenotator createMultiLevelSoundScore(double[][] values) {
		PowerDenotator soundScore = this.createEmptyScore();
		for (int i = values.length-1; i >= 0; i--) {
			double[] currentValues = values[i];
			Denotator currentNote = this.createNoteDenotator(currentValues);
			List<Denotator> currentNodes = new ArrayList<Denotator>();
			currentNodes.add(this.createNodeDenotator(currentNote, soundScore));
			soundScore = this.createSoundScore(currentNodes);
		}
		return soundScore;
	}
	
	private PowerDenotator createSoundScore(List<Denotator> nodes) {
		try {
			return new PowerDenotator(this.emptyName, this.soundScoreForm, nodes);
		} catch (RubatoException e) {
			return null;
		}
	}
	
	public LimitDenotator createSpecificNodeDenotator(List<Denotator> coordinates) {
		try {
			return new LimitDenotator(this.emptyName, this.soundNodeForm, coordinates);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private LimitDenotator convertToSoundNote(LimitDenotator note, boolean withModulators) {
		List<Denotator> coordinates = note.getFactors();
		try {
			coordinates.add(new SimpleDenotator(this.emptyName, this.layerForm, new ZElement(0)));
		} catch (DomainException e) { e.printStackTrace(); }
		return this.createSpecificNoteDenotator(coordinates, withModulators);
	}
	
	public LimitDenotator createSpecificNoteDenotator(List<Denotator> coordinates) {
		return this.createSpecificNoteDenotator(coordinates, false);
	}
	
	public LimitDenotator createSpecificNoteDenotator(List<Denotator> coordinates, boolean withModulators) {
		try {
			if (coordinates.size() < 7) {
				if (withModulators) {
					coordinates.add(this.createStandardModulators());
				} else {
					coordinates.add(this.createEmptyModulators());
				}
			}
			//this takes a lot of time compared to the other operations
			return new LimitDenotator(this.emptyName, this.soundNoteForm, coordinates);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public LimitDenotator createFMNodeDenotator(double[] noteValues) {
		try {
			LimitDenotator note = this.createNoteDenotator(noteValues);
			note.setFactor(6, this.createStandardModulators());
			//note.setFactor(6, this.createEmptyModulators());
			return this.createNodeDenotator(note);
			//this takes a lot of time compared to the other operations
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private PowerDenotator createStandardModulators() {
		try {
			List<Denotator> modulators = new ArrayList<Denotator>();
			if (!this.fmModel.equals(SoundNoteGenerator.FM_MODELS[0])) {
				modulators.add(this.createModulatorNoteDenotator(20, 10));
				if (this.fmModel.equals(SoundNoteGenerator.FM_MODELS[1])) {
					modulators.add(this.createModulatorNoteDenotator(50, 10));
				} else if (this.fmModel.equals(SoundNoteGenerator.FM_MODELS[2])) {
					((LimitDenotator)modulators.get(0)).setFactor(6, this.createSecondLevelModulators());
				}
			}
			return new PowerDenotator(this.emptyName, this.modulatorsForm, modulators);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Denotator createSecondLevelModulators() {
		try {
			List<Denotator> modulators = new ArrayList<Denotator>();
			modulators.add(this.createModulatorNoteDenotator(20, 10));
			return new PowerDenotator(this.emptyName, this.modulatorsForm, modulators);
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private LimitDenotator createModulatorNoteDenotator(int relativeFrequency, int relativeAmplitude) throws RubatoException {
		ModuleElement modulatorPitch = new QElement(relativeFrequency);
		ModuleElement modulatorLoudness = new ZElement(relativeAmplitude);
		List<Denotator> coordinates = new ArrayList<Denotator>();
		coordinates.add(this.createSimpleDenotator(this.onsetForm, new RElement(0)));
		coordinates.add(this.createSimpleDenotator(this.pitchForm, modulatorPitch));
		coordinates.add(this.createSimpleDenotator(this.loudnessForm, modulatorLoudness));
		coordinates.add(this.createSimpleDenotator(this.durationForm, new RElement(0)));
		coordinates.add(this.createSimpleDenotator(this.voiceForm, new ZElement(0)));
		coordinates.add(this.createSimpleDenotator(this.layerForm, new ZElement(0)));
		coordinates.add(this.createEmptyModulators());
		//this takes a lot of time compared to the other operations
		return new LimitDenotator(this.emptyName, this.soundNoteForm, coordinates);
	}
	
	private PowerDenotator createEmptyModulators() throws RubatoException {
		List<Denotator> modulators = new ArrayList<Denotator>();
		return new PowerDenotator(this.emptyName, this.modulatorsForm, modulators);
	}
	
	public LimitDenotator convertModulatorToNode(LimitDenotator modulator) {
		return this.createNodeDenotator(modulator);
	}
	
	public LimitDenotator convertNodeToModulator(LimitDenotator node) {
		//TODO: convert satellites to modulators!!
		return (LimitDenotator)node.getFactor(0);
	}
	
	public void setFMModel(String fmModel) {
		this.fmModel = fmModel;
	}
	
	/**
	 * Returns the SoundScore form from the system repository.
	 */
	public PowerForm getSoundScoreForm() { return this.soundScoreForm; }
	
	/**
	 * Returns the SoundNote form from the system repository.
	 */
	public LimitForm getSoundNoteForm() { return this.soundNoteForm; }

}
