package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynObject implements Comparable<JSynObject> {
	
	public static final int ADDITIVE = 0;
	public static final int RING_MODULATION = 1;
	public static final int FREQUENCY_MODULATION = 2;
	
	private BigBangObject bbObject;
	
	private JSynObject parent;
	private double frequencyTranspositionRatio;
	private double loudnessRatio;
	private Integer satelliteType;
	private List<JSynObject> satellites;
	
	public JSynObject(JSynObject parent, BigBangObject bbObject, int satelliteType) {
		this(bbObject, satelliteType);
		this.parent = parent;
		if (parent != null) {
			parent.addSatellite(this);
		}
	}
	
	//used during cloning
	protected JSynObject(BigBangObject bbObject, int satelliteType) {
		this.bbObject = bbObject;
		this.satellites = new ArrayList<JSynObject>();
		this.satelliteType = satelliteType;
	}
	
	public BigBangObject getBigBangObject() {
		return this.bbObject;
	}
	
	/**
	 * @return true if the object is worth being played. for now this is only the case if it contains at least
	 * one frequency. 
	 */
	public boolean isAudible() {
		return this.bbObject.isAudible() && this.getFrequencies().size() > 0;
	}
	
	
	//TIME
	
	public double getOnset() {
		return this.getFirstValue(0.0, CoolFormRegistrant.ONSET_NAME, CoolFormRegistrant.BEAT_CLASS_NAME);
	}
	
	public double getDuration() {
		return this.getFirstValue(Double.MAX_VALUE, CoolFormRegistrant.DURATION_NAME);
	}
	
	public Double getOffset() {
		return this.getOnset() + this.getDuration();
	}
	
	public boolean playsAt(double onset, double offset) {
		boolean noIntersection = this.getOffset() < onset || this.getOnset() > offset; 
		return !noIntersection;
	}
	
	
	//FREQUENCY
	
	public double getMainFrequency() {
		return this.getFrequencies().get(0);
	}

	public List<Double> getFrequencies() {
		//get all possible forms of pitches
		List<Double> pitches = this.getAllKindsOfPitches();
		//get overtones at this point since they are built on parent frequencies if there are no pitches present here
		List<Double> frequencies = this.getOvertoneFrequencies(pitches);
		//middle c in case there are no pitches
		if (pitches.isEmpty()) {
			pitches.add(60.0);
		}
		//create interval structure with first pitch
		this.addIntervalStructure(pitches);
		//convert all pitches into frequencies and add to where potentially the overtones are
		for (double currentPitch : pitches) {
			frequencies.add(this.midiToFrequency(currentPitch)*this.frequencyTranspositionRatio);
		}
		return frequencies;
	}
	
	private List<Double> getAllKindsOfPitches() {
		//get pitches and chromatic pitches
		List<Double> pitches = this.bbObject.getValues(CoolFormRegistrant.PITCH_NAME);
		pitches.addAll(this.bbObject.getValues(CoolFormRegistrant.CHROMATIC_PITCH_NAME));
		//get pitch classes and make them start at 60
		List<Double> pitchClasses = this.bbObject.getValues(CoolFormRegistrant.PITCH_CLASS_NAME);
		for (int i = 0; i < pitchClasses.size(); i++) {
			pitchClasses.set(i, 60+pitchClasses.get(i));
		}
		pitches.addAll(pitchClasses);
		return pitches;
	}
	
	//if there is an interval structure, builds it on the first pitch in the given list
	private void addIntervalStructure(List<Double> pitches) {
		List<Integer> intervalStructure = this.getIntervalStructure();
		double currentPitch = pitches.get(0);
		for (double currentInterval : intervalStructure) {
			currentPitch += currentInterval;
			pitches.add(currentPitch);
		}
	}
	
	private List<Integer> getIntervalStructure() {
		List<Integer> intervalStructure = new ArrayList<Integer>();
		Double quality = this.getFirstValue(null, CoolFormRegistrant.TRIAD_QUALITY_NAME);
		if (quality != null) {
			if (quality < 2) {
				intervalStructure.add(3);
			} else {
				intervalStructure.add(4);
			}
			if (quality % 2 == 0) {
				intervalStructure.add(3);
			} else {
				intervalStructure.add(4);
			}
		}
		return intervalStructure;
	}
	
	/*
	 * if there are overtones build them from the first of the given pitches
	 * if there are none, from the parent object
	 * @return a list of overtone frequencies if there are some, an empty list if there are none
	 */
	private List<Double> getOvertoneFrequencies(List<Double> pitches) {
		if (!pitches.isEmpty()) {
			return this.getOvertoneFrequencies(this.midiToFrequency(pitches.get(0)));
		} else if (this.parent != null) {
			Double parentMainFrequency = this.parent.getMainFrequency();
			if (parentMainFrequency != null) {
				return this.getOvertoneFrequencies(parentMainFrequency);
			}
		}
		return this.getOvertoneFrequencies(this.midiToFrequency(60));
	}
	
	private List<Double> getOvertoneFrequencies(Double baseFrequency) {
		List<Double> overtoneFrequencies = new ArrayList<Double>();
		Double overtoneIndex = this.getFirstValue(null, CoolFormRegistrant.OVERTONE_INDEX_NAME);
		if (overtoneIndex != null) {
			double overtoneFrequency = baseFrequency;
			while (overtoneIndex > 0) {
				overtoneFrequency = overtoneFrequency*(overtoneIndex+1)/overtoneIndex;
				overtoneIndex--;
			}
			overtoneFrequencies.add(overtoneFrequency);
		}
		return overtoneFrequencies;
	}
	
	/**
	 * Sets a transposition interval by which all frequencies of the object and satellites will be transposed.  
	 * @param interval an interval in half steps (can be microtonal)
	 */
	public void setTranspositionInterval(double interval) {
		this.frequencyTranspositionRatio = this.intervalToRatio(interval);
		for (JSynObject currentModulator : this.satellites) {
			currentModulator.setTranspositionInterval(interval);
		}
	}
	
	
	//AMPLITUDE

	public double getAmplitude() {
		double amplitude = this.midiToAmplitude(this.getLoudness());
		if (this.parent != null && this.satelliteType == JSynObject.FREQUENCY_MODULATION) {
			//higher amplitude so that FM audible
			return amplitude*2000;
		}
		return amplitude;
	}
	
	public int getLoudness() {
		return (int)Math.round(this.getFirstValue(100.0, CoolFormRegistrant.LOUDNESS_NAME)*this.loudnessRatio);
	}
	
	public void setLoudnessRatio(double ratio) {
		this.loudnessRatio = ratio;
		for (JSynObject currentModulator : this.satellites) {
			currentModulator.setLoudnessRatio(ratio);
		}
	}
	
	
	//VOICE
	
	public int getVoice() {
		return this.getFirstValue(0.0, CoolFormRegistrant.VOICE_NAME).intValue();
	}
	
	
	//PAN
	public double getPan() {
		return this.midiToPan(this.getFirstValue(63.5, CoolFormRegistrant.PAN_NAME));
	}
	
	
	//SATELLITES
	
	public void addSatellite(JSynObject satellite) {
		this.satellites.add(satellite);
	}
	
	public List<JSynObject> getSatellites() {
		return this.satellites;
	}
	
	public int getSatelliteType() {
		if (this.satelliteType != null) {
			if (this.satelliteType == 1) {
				return JSynObject.RING_MODULATION;
			} else if (this.satelliteType == 2) {
				return JSynObject.FREQUENCY_MODULATION;
			}
		}
		return this.getFirstValue(new Double(JSynObject.ADDITIVE), CoolFormRegistrant.OPERATION_NAME).intValue();
	}
	
	
	//UTIL
	
	private Double getFirstValue(Double standardValue, String... formNames) {
		if (this.bbObject != null) {
			for (String currentFormName : formNames) {
				Double currentFirstValue = this.bbObject.getNthValue(currentFormName, 0);
				if (currentFirstValue != null) {
					return currentFirstValue;
				}
			}
		}
		return standardValue;
	}
	
	protected double midiToFrequency(double midiPitch) {
		return BigBangPlayer.BASE_A4*Math.pow(2, (midiPitch-57)/12);
	}
	
	public int frequencyToMidi(double frequency) {
		return (int)Math.round((Math.log(frequency/BigBangPlayer.BASE_A4)/Math.log(2))*12+57);
	}
	
	protected double midiToAmplitude(double loudness) {
		loudness = Math.min(loudness, 127);
		loudness = Math.max(loudness, 0);
		return loudness/127;
	}
	
	private double midiToPan(double pan) {
		pan = Math.min(pan, 127);
		pan = Math.max(pan, 0);
		return (pan/63.5)-1;
	}
	
	private double intervalToRatio(double interval) {
		return Math.pow(Math.sqrt(Math.sqrt(Math.cbrt(2))), interval); //12th root of two
	}
	
	public JSynObject clone() {
		JSynObject clone = new JSynObject(this.bbObject, this.satelliteType);
		if (this.satellites.size() > 0) {
			List<JSynObject> clonedSatellites = new ArrayList<JSynObject>();
			for (JSynObject currentSatellite : this.satellites) {
				JSynObject satelliteClone = currentSatellite.clone();
				satelliteClone.parent = clone;
				clonedSatellites.add(satelliteClone);
			}
			//System.out.println(clonedSatellites + " " + satelliteClone.);
			clone.satellites = clonedSatellites;
		}
		return clone;
	}
	
	public String toString() {
		return "(" + this.getOnset() + " " + this.getFrequencies() + " " + this.getAmplitude() + " " + this.getDuration() + ")";
	}
	
	public int compareTo(JSynObject other) {
		return this.bbObject.compareTo(other.bbObject);
	}

	/*//TODO NOT COMPLETE YET!
	public int compareTo(JSynObject other) {
		if (this.getOnset() < other.getOnset()) {
			return -1;
		} else if (this.getOnset() > other.getOnset()) {
			return 1;
		} else if (this.getMainFrequency() < other.getMainFrequency()) {
			return -1;
		} else if (this.getMainFrequency() > other.getMainFrequency()) {
			return 1;
		} else if (this.getAmplitude() < other.getAmplitude()) {
			return -1;
		} else if (this.getAmplitude() > other.getAmplitude()) {
			return 1;
		} else if (this.getDuration() < other.getDuration()) {
			return -1;
		} else if (this.getDuration() > other.getDuration()) {
			return 1;
		}
		return 0;
	}*/

}
