package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.logeo.FormFactory;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitForm;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;

public class CoolFormRegistrant {
	
	public static final String ONSET = "Onset";
	
	private final Repository REPOSITORY = Repository.systemRepository();
	private final SimpleForm ONSET_FORM = (SimpleForm)this.REPOSITORY.getForm(ONSET);
	private final SimpleForm PITCH_FORM = (SimpleForm)this.REPOSITORY.getForm("Pitch");
	private final SimpleForm LOUDNESS_FORM = (SimpleForm)this.REPOSITORY.getForm("Loudness");
	private final SimpleForm DURATION_FORM = (SimpleForm)this.REPOSITORY.getForm("Duration");
	private final SimpleForm VOICE_FORM = (SimpleForm)this.REPOSITORY.getForm("Voice");
	
	public CoolFormRegistrant() {
	}
	
	public void registerAllTheCoolStuff() {
		this.registerImageForms();
		this.registerMusicForms();
	}
	
	public void registerMusicForms() {
		//PitchClassSet
		SimpleForm pitchClass = this.registerZnModuleForm("PitchClass", 12);
		this.registerPowerForm("PitchClassSet", pitchClass);
		LimitForm pitchClassNote = this.registerLimitForm("PitchClassNote", this.ONSET_FORM, pitchClass, this.LOUDNESS_FORM, this.DURATION_FORM, this.VOICE_FORM);
		this.registerPowerForm("PitchClassScore", pitchClassNote);
		
		//SoundSpectrum
		LimitForm overtone = this.registerLimitForm("Overtone", this.PITCH_FORM, this.LOUDNESS_FORM);
		this.registerPowerForm("Spectrum", overtone);
		
		//HarmonicSpectrum
		SimpleForm index = this.registerZModuleForm("OvertoneIndex");
		LimitForm harmonicOvertone = this.registerLimitForm("HarmonicOvertone", index, this.LOUDNESS_FORM);
		PowerForm harmonicOvertones = this.registerPowerForm("HarmonicOvertones", harmonicOvertone);
		this.registerLimitForm("HarmonicSpectrum", this.PITCH_FORM, harmonicOvertones);
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
		return this.REPOSITORY.register(form);
	}

}
