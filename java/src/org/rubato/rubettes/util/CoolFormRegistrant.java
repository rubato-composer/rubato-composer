package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.logeo.FormFactory;
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
	
	public static SimpleForm PITCH_CLASS_FORM;
	public static SimpleForm OVERTONE_INDEX_FORM;
	public static LimitForm FM_NODE_FORM;
	
	public CoolFormRegistrant() {
	}
	
	public void registerAllTheCoolStuff() {
		if (Repository.systemRepository().getForm("HarmonicSpectrum") == null) {
			this.registerImageForms();
			this.registerMusicForms();
		}
	}
	
	public void registerMusicForms() {
		//PitchClassSet
		PITCH_CLASS_FORM = this.registerZnModuleForm("PitchClass", 12);
		this.registerPowerForm("PitchClassSet", PITCH_CLASS_FORM);
		LimitForm pitchClassNote = this.registerLimitForm("PitchClassNote", ONSET_FORM, PITCH_CLASS_FORM, LOUDNESS_FORM, DURATION_FORM, VOICE_FORM);
		this.registerPowerForm("PitchClassScore", pitchClassNote);
		
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
		LimitForm soundNote = this.registerLimitForm("SoundNote", ONSET_FORM, PITCH_FORM, LOUDNESS_FORM, DURATION_FORM, VOICE_FORM, layer, modulators);
		modulators = this.registerPowerForm("Modulators", soundNote);
		modulators.resolveReferences(REPOSITORY);
		
		//SoundScore
		Form soundScore = new FormReference("SoundScore", Form.POWER);
		LimitForm soundNode = this.registerLimitForm("SoundNode", soundNote, soundScore);
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
		List<String> labels = new ArrayList<String>();
		for (Form currentCoordinate : coordinateForms) {
			labels.add(currentCoordinate.getNameString().toLowerCase());
		}
		LimitForm newForm = FormFactory.makeLimitForm(name, coordinateForms);
		newForm.setLabels(labels);
		return (LimitForm)this.register(newForm);
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
