/*
 * Copyright (C) 2013 Florian Thalmann
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.rubettes.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.util.ContrapuntalVoice;
import org.rubato.rubettes.util.VoiceSplitter;
import org.rubato.rubettes.util.Quantizer;
import org.rubato.rubettes.util.SoundNoteGenerator;

/**
 * A rubette that converts a Score denotator to LilyPond code and writes it to a file
 * 
 * @author Florian Thalmann
 */
public class LilyPondOutRubette extends SimpleAbstractRubette {
	
	private final double TIME_QUANTIZATION_UNIT = .015625; //a sixtyfourth!
	private final double SMALLEST_DOT_FACTOR = .015625;
	private File lilyPondFile; 
	private final String lilyPondFileKey = "lilyPondFile";
	private SoundNoteGenerator noteGenerator; 
	
	private static final ImageIcon icon = Icons.loadIcon(LilyPondOutRubette.class, "lilypondouticon.png");
	
	/**
	 * Creates a basic LilyPondOutRubette.
	 */
    public LilyPondOutRubette() {
        this.setInCount(1);
        this.setOutCount(0);
        this.putProperty(new FileProperty(this.lilyPondFileKey, "LilyPond file", new String[]{".ly"}, true));
        this.noteGenerator = new SoundNoteGenerator();
    }

    public void run(RunInfo runInfo) {
    	Denotator input = this.getInput(0);
    	if (input == null) {
            this.addError("Input denotator is null.");
    	} else if (input.hasForm(this.noteGenerator.getScoreForm())) {
    		this.writeToLilyPondFile(this.getLilyPondString((PowerDenotator)input.copy(), runInfo));
    	} else {
    		this.addError("Input denotator is not of form \"Score\".");
    	}
    }
    
    private void writeToLilyPondFile(String content) {
    	if (this.lilyPondFile != null) {
    		try {
    			FileWriter writer = new FileWriter(this.lilyPondFile);
    			writer.write(content);
    			writer.close();
    		} catch (IOException iox) {
    			this.addError("File %%1 could not be written.", lilyPondFile.getName());
    		}
    	} else {
    		this.addError("No file selected.");
    	}
    }
    
    private String getLilyPondString(PowerDenotator score, RunInfo runInfo) {
    	StringBuilder lilyPondCode = new StringBuilder(); 
    	lilyPondCode.append(this.getLilyPondHeader(score));
    	
    	lilyPondCode.append("{\n");
    	lilyPondCode.append("\t<< ");
    	
		score = new Quantizer(this.TIME_QUANTIZATION_UNIT, 1).getQuantizedScore(score, runInfo);
		VoiceSplitter splitter = new VoiceSplitter(score);
		Iterator<ContrapuntalVoice> contrapuntalVoices = splitter.getVoices().iterator();
		
		while (contrapuntalVoices.hasNext()) {
			this.addVoiceToCode(lilyPondCode, contrapuntalVoices.next(), splitter.getFirstOnset(), runInfo);
			if (contrapuntalVoices.hasNext()) {
				lilyPondCode.append(" \\\\ ");
			}
		}
    	
    	lilyPondCode.append(" >>");
		lilyPondCode.append("\n}\n\n");
		
		lilyPondCode.append(this.getVersionString());
		return lilyPondCode.toString();
    }
    
    private void addVoiceToCode(StringBuilder lilyPondCode, ContrapuntalVoice voice, double firstOnset, RunInfo runInfo) {
    	Iterator<List<LimitDenotator>> simultaneities = this.getNoteSimultaneities(voice).iterator();
    	double currentLastOffset = firstOnset;
    	
    	double lastDuration = 0;
		
		lilyPondCode.append("{");
		
		while (simultaneities.hasNext()) {
			List<LimitDenotator> currentSimultaneity = simultaneities.next();
    		double currentOnset = this.noteGenerator.getDoubleValue(currentSimultaneity.get(0), new int[]{0,0});
    		double currentDuration = this.noteGenerator.getDoubleValue(currentSimultaneity.get(0), new int[]{3,0});
    		
    		if (currentOnset > currentLastOffset) {
    			lilyPondCode.append("r");
    			if (currentDuration != lastDuration) {
    				lilyPondCode.append(this.getDurationString(currentOnset-currentLastOffset));
    			}
    			lilyPondCode.append(" ");
    		}
    		if (currentSimultaneity.size() > 1) {
				lilyPondCode.append("<");
			}
			
    		Iterator<LimitDenotator> notes = currentSimultaneity.iterator();
			while (notes.hasNext()) {
				//TODO: add voice!!!! and loudness..
				LimitDenotator currentNote = notes.next();
				double currentPitch = this.noteGenerator.getDoubleValue(currentNote, new int[]{1,0});
				currentLastOffset = currentOnset+currentPitch;
				lilyPondCode.append(this.midiPitchToName(currentPitch));
				if (notes.hasNext()) {
					lilyPondCode.append(" ");
				}
			}
			if (currentSimultaneity.size() > 1) {
				lilyPondCode.append(">");
			}
			if (currentDuration != lastDuration) {
				lilyPondCode.append(this.getDurationString(currentDuration));
				lastDuration = currentDuration;
			}
			if (simultaneities.hasNext()) {
				lilyPondCode.append(" ");
			}
			
			if (runInfo.stopped()) {
				break;
			}
		}
    	lilyPondCode.append("}");
    }
    
    private List<List<LimitDenotator>> getNoteSimultaneities(ContrapuntalVoice voice) {
    	Iterator<LimitDenotator> notes = voice.getNotes().iterator();
    	List<List<LimitDenotator>> simultaneities = new ArrayList<List<LimitDenotator>>();
    	List<LimitDenotator> currentSimultaneity = new ArrayList<LimitDenotator>();
    	LimitDenotator currentNote = notes.next();
    	double firstOnsetOfSimultaneity = this.noteGenerator.getDoubleValue(currentNote, new int[]{0,0});
    	currentSimultaneity.add(currentNote);
    	while (notes.hasNext()) {
    		currentNote = notes.next();
			double currentOnset = this.noteGenerator.getDoubleValue(currentNote, new int[]{0,0});
			if (currentOnset != firstOnsetOfSimultaneity) {
				simultaneities.add(currentSimultaneity);
				firstOnsetOfSimultaneity = currentOnset;
				currentSimultaneity = new ArrayList<LimitDenotator>();
			}
			currentSimultaneity.add(currentNote);
		}
    	simultaneities.add(currentSimultaneity);
    	return simultaneities;
    }
    
    private String midiPitchToName(double midiValue) {
    	int intMidiValue = (int)Math.round(midiValue);
    	return this.getPitchName(intMidiValue) + this.getOctaveSign(intMidiValue);
    }
    
    private String getPitchName(int midiValue) {
    	int pitchClass = midiValue%12;
    	if (pitchClass == 0) return "c";
    	else if (pitchClass == 1) return "cis";
    	else if (pitchClass == 2) return "d";
    	else if (pitchClass == 3) return "ees";
    	else if (pitchClass == 4) return "e";
    	else if (pitchClass == 5) return "f";
    	else if (pitchClass == 6) return "fis";
    	else if (pitchClass == 7) return "g";
    	else if (pitchClass == 8) return "aes";
    	else if (pitchClass == 9) return "a";
    	else if (pitchClass == 10) return "bes";
    	else return "b";
    }
    
    private String getOctaveSign(int midiValue) {
    	int octave = midiValue/12;
    	if (octave == 0) return ",,,,";
    	else if (octave == 1) return ",,,";
    	else if (octave == 2) return ",,";
    	else if (octave == 3) return ",";
    	else if (octave == 4) return "";
    	else if (octave == 5) return "'";
    	else if (octave == 6) return "''";
    	else if (octave == 7) return "'''";
    	else if (octave == 8) return "''''";
    	else if (octave == 9) return "'''''";
    	else return "'''''";
    }
    
    private String getDurationString(double duration) {
    	//TODO: implement triplets etc!!
    	StringBuilder durationString = new StringBuilder();
    	int currentLilyPondDuration = 1;
    	while (duration < 4.0/currentLilyPondDuration) {
    		currentLilyPondDuration *= 2;
    	}
    	durationString.append(currentLilyPondDuration);
    	
    	//apply dots if necessary
    	if (duration > 4.0/currentLilyPondDuration) {
    		duration /= (4.0/currentLilyPondDuration);
    		double currentDotFactor = .5;
        	duration -= currentDotFactor;
        	while (duration >= 1 && currentDotFactor >= this.SMALLEST_DOT_FACTOR) {
        		durationString.append(".");
        		currentDotFactor /= 2;
            	duration = duration-currentDotFactor;
        	}
    	}
    	
    	return durationString.toString();
    }
    
    private String getLilyPondHeader(PowerDenotator score) {
    	return
    		"% Generated with Rubato Composer's LilyPondOut Rubette %\n" +
    		"\n" +
    		"\\header{\n" +
    		"\ttitle = \"" + score.getNameString() + "\"\n" +
    		"}\n" +
    		"\n" +
    		"\\new Voice \\with {\n" +
    		"\t\\remove \"Note_heads_engraver\"\n" +
    		"\t\\consists \"Completion_heads_engraver\"\n" +
    		"}\n" +
    		"\n";
    }
    
    private String getVersionString() {
    	return "\\version \"2.16.2\"  % necessary for upgrading to future LilyPond versions.";
    }
    
    //has to be overriden so that the info sets!!
	@Override
	public boolean applyProperties() {
        super.applyProperties();
        this.lilyPondFile = ((FileProperty)this.getProperty(this.lilyPondFileKey)).getFile();
        return true;
    }
    
	public boolean hasInfo() {
        return true;
    }
	
	public String getInfo() {
        if (this.lilyPondFile != null) {
        	return this.lilyPondFile.getName();
        }
        return "File not set";
    }  
	
    public String getGroup() {
        return "Score";
    }
    
    public String getName() {
        return "LilyPondOut";
    }
	
    public String getShortDescription() {
        return "Generates a LilyPond file from a score denotator";
    }
    
    public ImageIcon getIcon() {
        return LilyPondOutRubette.icon;
    }

    public String getLongDescription() {
        return "The LilyPondOut rubette writes a score denotator"+
               " to the specified LilyPond file";
    }

    public String getInTip(int i) {
        return "Input score denotator";
    }
    
}
