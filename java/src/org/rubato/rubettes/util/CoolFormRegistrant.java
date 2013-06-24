package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.logeo.FormFactory;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.FormReference;
import org.rubato.math.yoneda.LimitForm;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;

public class CoolFormRegistrant {
	
	public static final String ONSET = "Onset";
	
	public static final Repository REPOSITORY = Repository.systemRepository();
	public static final SimpleForm ONSET_FORM = (SimpleForm)REPOSITORY.getForm("Onset");
	public static final SimpleForm PITCH_FORM = (SimpleForm)REPOSITORY.getForm("Pitch");
	public static final SimpleForm LOUDNESS_FORM = (SimpleForm)REPOSITORY.getForm("Loudness");
	public static final SimpleForm DURATION_FORM = (SimpleForm)REPOSITORY.getForm("Duration");
	public static final SimpleForm VOICE_FORM = (SimpleForm)REPOSITORY.getForm("Voice");
	public static final LimitForm NOTE_FORM = (LimitForm)REPOSITORY.getForm("Note");
	
	public static SimpleForm PITCH_CLASS_FORM;
	public static SimpleForm CHROMATIC_PITCH_FORM;
	public static SimpleForm QUALITY_FORM;
	public static PowerForm DYADS_FORM;
	public static SimpleForm BEAT_CLASS_FORM;
	public static LimitForm REST_FORM;
	public static ColimitForm NOTE_OR_REST_FORM;
	public static SimpleForm OVERTONE_INDEX_FORM;
	public static LimitForm FM_NODE_FORM;
	public static LimitForm SOUND_NOTE_FORM;
	
	public CoolFormRegistrant() {
	}
	
	public void registerAllTheCoolStuff() {
		if (Repository.systemRepository().getForm("HarmonicSpectrum") == null) {
			this.registerImageForms();
			this.registerMusicTheoryForms();
			this.registerSoundForms();
		}
	}
	
	public void registerMusicTheoryForms() {
		//PitchClass
		PITCH_CLASS_FORM = this.registerZnModuleForm("PitchClass", 12);
		this.registerPowerForm("PitchClassSet", PITCH_CLASS_FORM);
		LimitForm pitchClassNote = this.registerLimitForm("PitchClassNote", ONSET_FORM, PITCH_CLASS_FORM, LOUDNESS_FORM, DURATION_FORM, VOICE_FORM);
		this.registerPowerForm("PitchClassScore", pitchClassNote);
		
		//BeatClass
		BEAT_CLASS_FORM = this.registerZnModuleForm("BeatClass", 16);
		LimitForm beatclassNote = this.registerLimitForm("BeatClassNote", BEAT_CLASS_FORM, PITCH_FORM, LOUDNESS_FORM, DURATION_FORM, VOICE_FORM);
		this.registerPowerForm("BeatClassScore", beatclassNote);
		
		//Triad
		CHROMATIC_PITCH_FORM = this.registerZModuleForm("ChromaticPitch");
		QUALITY_FORM = this.registerZnModuleForm("TriadQuality", 4);
		LimitForm triad = this.registerLimitForm("Triad", CHROMATIC_PITCH_FORM, QUALITY_FORM);
		this.registerPowerForm("Triads", triad);
		
		//Dyads
		LimitForm dyad = this.registerLimitForm("Dyad", PITCH_FORM, PITCH_FORM);
		DYADS_FORM = this.registerPowerForm("Dyads", dyad);
		
		//GeneralScore
		REST_FORM = this.registerLimitForm("Rest", ONSET_FORM, DURATION_FORM, VOICE_FORM);
		NOTE_OR_REST_FORM = this.registerColimitForm("NoteOrRest", NOTE_FORM, REST_FORM);
		this.registerPowerForm("GeneralScore", NOTE_OR_REST_FORM);
	}
	
	public void registerSoundForms() {
		//SoundSpectrum
		LimitForm partial = this.registerLimitForm("Partial", LOUDNESS_FORM, PITCH_FORM);
		this.registerPowerForm("Spectrum", partial);
		
		//HarmonicSpectrum
		OVERTONE_INDEX_FORM = this.registerZModuleForm("OvertoneIndex");
		LimitForm harmonicOvertone = this.registerLimitForm("HarmonicOvertone", OVERTONE_INDEX_FORM, LOUDNESS_FORM);
		PowerForm harmonicOvertones = this.registerPowerForm("HarmonicOvertones", harmonicOvertone);
		this.registerLimitForm("HarmonicSpectrum", PITCH_FORM, harmonicOvertones);
		
		//FM
		Form fmSet = new FormReference("FMSet", Form.POWER);
		FM_NODE_FORM = this.registerLimitForm("FMNode", partial, fmSet);
		fmSet = this.registerPowerForm("FMSet", FM_NODE_FORM);
		fmSet.resolveReferences(REPOSITORY);
		
		//SoundNote
		SimpleForm layer = this.registerZModuleForm("Layer");
		Form modulators = new FormReference("Modulators", Form.POWER);
		SOUND_NOTE_FORM = this.registerLimitForm("SoundNote", ONSET_FORM, PITCH_FORM, LOUDNESS_FORM, DURATION_FORM, VOICE_FORM, layer, modulators);
		modulators = this.registerPowerForm("Modulators", SOUND_NOTE_FORM);
		modulators.resolveReferences(REPOSITORY);
		
		//SoundScore
		Form soundScore = new FormReference("SoundScore", Form.POWER);
		LimitForm soundNode = this.registerLimitForm("SoundNode", SOUND_NOTE_FORM, soundScore);
		soundScore = this.registerPowerForm("SoundScore", soundNode);
		soundScore.resolveReferences(REPOSITORY);
	}
	
	public void registerImageForms() {
		//Image
		SimpleForm x = this.registerRModuleForm("X");
		SimpleForm y = this.registerRModuleForm("Y");
		SimpleForm red = this.registerRModuleForm("Red");
		SimpleForm green = this.registerRModuleForm("Green");
		SimpleForm blue = this.registerRModuleForm("Blue");
		SimpleForm alpha = this.registerRModuleForm("Alpha");
		LimitForm pixel = this.registerLimitForm("Pixel", x, y, red, green, blue, alpha);
		this.registerPowerForm("Image", pixel);
		
		//VariableSizePixelImage
		SimpleForm width = this.registerRModuleForm("Width");
		SimpleForm height = this.registerRModuleForm("Height");
		LimitForm variableSizePixel = this.registerLimitForm("VariableSizePixel", x, y, width, height, red, green, blue, alpha);
		this.registerPowerForm("VSPixelImage", variableSizePixel);
	}
	
	private PowerForm registerPowerForm(String name, Form coordinateForm) {
		return (PowerForm)this.register(FormFactory.makePowerForm(name, coordinateForm));
	}
	
	private LimitForm registerLimitForm(String name, Form... coordinateForms) {
		List<String> labels = this.generateLabels(coordinateForms);
		LimitForm newForm = FormFactory.makeLimitForm(name, coordinateForms);
		newForm.setLabels(labels);
		return (LimitForm)this.register(newForm);
	}
	
	private ColimitForm registerColimitForm(String name, Form... coordinateForms) {
		List<String> labels = this.generateLabels(coordinateForms);
		ColimitForm newForm = FormFactory.makeColimitForm(name, coordinateForms);
		newForm.setLabels(labels);
		return (ColimitForm)this.register(newForm);
	}
	
	private List<String> generateLabels(Form... coordinateForms) {
		List<String> labels = new ArrayList<String>();
		for (Form currentCoordinate : coordinateForms) {
			labels.add(currentCoordinate.getNameString().toLowerCase());
		}
		return labels;
	}
	
	private SimpleForm registerRModuleForm(String name) {
		return (SimpleForm)this.register(FormFactory.makeRModuleForm(name));
	}
	
	private SimpleForm registerZModuleForm(String name) {
		return (SimpleForm)this.register(FormFactory.makeZModuleForm(name));
	}
	
	private SimpleForm registerZnModuleForm(String name, int modulus) {
		return (SimpleForm)this.register(FormFactory.makeZnModuleForm(name, modulus));
	}
	
	private Form register(Form form) {
		return REPOSITORY.register(form);
	}

}
