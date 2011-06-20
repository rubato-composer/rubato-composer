/*
 * Copyright (C) 2004 Gérard Milmeister
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

package org.rubato.audio.midi;

import static org.rubato.logeo.DenoFactory.makeDenotator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.sound.midi.*;

import org.rubato.base.Repository;
import org.rubato.math.arith.Rational;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;

/**
 * This class reads in a MIDI file an converts it to a denotator of form Score.
 *  
 * @author Gérard Milmeister
 */

public class MidiReader {

    /**
     * Creates a MidiReader that reads from a file.
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public MidiReader(String fileName)
            throws InvalidMidiDataException, IOException {
        readSequence(fileName);
    }


    /**
     * Creates a MidiReader that reads from an InputStream.
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public MidiReader(InputStream stream)
            throws InvalidMidiDataException, IOException {
        readSequence(stream);
    }


    /**
     * Reads the MIDI file and returns a denotator of form Score.
     */
    public Denotator getDenotator() {
        processSequence();
        return score;
    }

    
    /**
     * The the factor that each time value is multiplied with.
     * The default is 1.0;
     */
    public void setTempoFactor(double f) {
        tempoFactor = f;
    }
    

    /**
     * Reads a MIDI sequence from a file.
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    private void readSequence(String fileName)
            throws InvalidMidiDataException, IOException {
        File midiFile = new File(fileName);
        sequence = MidiSystem.getSequence(midiFile);
        initialize();
    }


    /**
     * Reads a MIDI sequence from an InputStream.
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    private void readSequence(InputStream stream)
            throws InvalidMidiDataException, IOException {
        sequence = MidiSystem.getSequence(stream);
        initialize();
    }


    /**
     * Initializes various parameters from the sequence.
     */
    private void initialize() {
        float divisionType = sequence.getDivisionType();
        if (divisionType != Sequence.PPQ) {
            throw new IllegalStateException("Only division type PPQ supported");
        }
        ticksPerQuarter = sequence.getResolution();
        tracks          = sequence.getTracks();
//        seqLength       = sequence.getTickLength();
    }


//    /**
//     * Processes the sequence that has been read in and construct the denotator.
//     */
//    private void processSequence() {
//        LinkedList generalNotes = new LinkedList();
//        LinkedList articulations = new LinkedList();
//        tempoList = new LinkedList();
//        timeList = new LinkedList();
//        keyList = new LinkedList();
//        
//        // Process all events in all tracks
//        for (int i = 0; i < tracks.length; i++) {
//            initTrack(i);
//            for (int j = 0; j < tracks[i].size(); j++) {
//                processEvent(tracks[i].get(j));
//            }
//            ListIterator iter = notes.listIterator();
//            while (iter.hasNext()) {
//                generalNotes.add(makeGeneralNote((Key)iter.next()));
//            }
//            iter = pedals.listIterator();
//            while (iter.hasNext()) {
//                articulations.add(makePedal((Key)iter.next()));
//            }
//        }
//
//        // Process global events like time and key signatures, tempi
//        Denotator keySigs = DenoFactory.make(keySignaturesForm, keyList);
//        Denotator timeSigs = DenoFactory.make(timeSignaturesForm, timeList);
//
//        score = scoreForm.createDefaultDenotator();
//        FactorDenotator signatures = (FactorDenotator)DenoAccess.getIdFactor(score, 1);
//        try {
//            signatures.setIdFactor(0, keySigs);
//            signatures.setIdFactor(1, timeSigs);
//            DenoAccess.setIdFactor(score, 4, DenoFactory.make(generalNotesForm, generalNotes));
//            DenoAccess.setIdFactor(score, 2, DenoFactory.make(tempiForm, tempoList));
//            DenoAccess.setIdFactor(score, 5, DenoFactory.make(groupArticulationsForm, articulations));
//        }
//        catch (RubatoException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * Processes the sequence that has been read in
     * and construct the Score denotator.
     */
    private void processSequence() {
        LinkedList<Denotator> noteList = new LinkedList<Denotator>();
        
        // Process all events in all tracks
        for (int i = 0; i < tracks.length; i++) {
            initTrack(i);
            for (int j = 0; j < tracks[i].size(); j++) {
                processEvent(tracks[i].get(j));
            }
            for (Key key : notes) {
                noteList.add(makeNote(key));
            }
        }

        score = makeDenotator(scoreForm, noteList);
    }


    /**
     * Processes a MIDI event in a track.
     * According to the type of message, dispatches to processShortMessage
     * or processMetaMessage.
     */
    private void processEvent(MidiEvent event) {
        MidiMessage msg = event.getMessage();
        currentTick = event.getTick();
        if (msg instanceof ShortMessage) {
            processShortMessage((ShortMessage)msg);
        }
        else if (msg instanceof MetaMessage) {
            processMetaMessage((MetaMessage)msg);
        }
    }


    /**
     * Processes a short message from a track.
     * Currently only note on, note off and program change messages are considered.
     */
    private void processShortMessage(ShortMessage msg) {
        switch (msg.getCommand()) {
            case 0x80: {
                // Note Off
                processNoteOffEvent(msg.getData1(), msg.getChannel());
                break;
            }
            case 0x90: {
                // Note On
                if (msg.getData2() == 0) {
                    // "Note On" with velocity 0 is equivalent to Note Off
                    processNoteOffEvent(msg.getData1(), msg.getChannel());
                }
                else {
                    processNoteOnEvent(msg.getData1(), msg.getData2(), msg.getChannel());
                }
                break;
            }
            case 0xa0: {
                // Polyphonic key pressure
                // TODO: not yet implemented
                break;
            }
            case 0xb0: {
                // Control change
                processControlChange(msg.getData1(), msg.getData2(), msg.getChannel());
                break;
            }
            case 0xc0: {
                // Program change
                currentProgram = msg.getData1();
                break;
            }
            case 0xd0: {
                // Key pressure
                // TODO: not yet implemented
                break;
            }
            case 0xe0: {
                // Pitch wheel change
                // TODO: not yet implemented
                break;
            }
            case 0xF0 : {
                // ???
                // TODO: not yet implemented
                break;
            }
        }
    }


    /**
     * Processes a meta message from a track.
     * Currently only tempo, time signature and key signature changes are considered.
     */
    private void processMetaMessage(MetaMessage msg) {
//        byte[] message = msg.getMessage();
//        byte[] data = msg.getData();
//        int dataLength = msg.getLength();
        
        switch (msg.getType()) {
            case 0: {
                // Sequence Number
//                currentTrackNr = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
                break;
            }
            case 1: {
                // Text Event
                // String text = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 2: {
                // Copyright Notice
                // String copyrightText = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 3: {
                // Track Name
                // String trackName = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 4: {
                // Instrument Name
                // String instrumentName = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 5: {
                // Lyrics
                // String lyrics = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 6: {
                // Marker
                // String markerText = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 7: {
                // Cue Point
                // String cuePointText = new String(data);
                // TODO: not yet implemented
                break;
            }
            case 0x20: {
                // Channel Prefix
                // int cannelPrefix = data[0] & 0xFF;
                // TODO: not yet implemented
                break;
            }
            case 0x2F: {
                // End of Track
                break;
            }
            case 0x51: {
                // Change Tempo
//                int tempo = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
//                changeTempo(tempo);
                break;
            }
            case 0x54: {
                // SMTPE Offset
                // TODO: not yet implemented
                break;
            }
            case 0x58: {
                // Time Signature
//                int numerator = data[0] & 0xFF;
//                int denominator = 1 << (data[1] & 0xFF);
//                changeTimeSig(numerator, denominator);
                break;
            }
            case 0x59: {
                // Key Signature
                // int gender = data[1];
//                int signature = data[0];
//                changeKeySig(signature);
                break;
            }
            case 0x7F: {
                // Sequencer-Specific Meta-event
                // TODO: not yet implemented
                break;
            }
        }
    }


    /**
     * Processes a note on event.
     */
    private void processNoteOnEvent(int key, int velocity, int channel) {
        Key k = keys[channel][key] = new Key();
        k.key      = key;
        k.channel  = channel;
        k.velocity = velocity;
        k.tick     = currentTick;
        k.program  = currentProgram;
        notes.add(k);
    }


    /**
     * Processes a note off event.
     */
    private void processNoteOffEvent(int key, int channel) {
        Key k = keys[channel][key];
        if (k != null) {
            k.duration = currentTick-k.tick;
        }
    }


    /**
     * Processes a control change event.
     * Currently only the damper pedal is considered.
     */
    private void processControlChange(int controller, int value, int channel) {
        switch (controller) {
            case 0x40: {
                // Damper Pedal
                if (value > 0) {
                    Key k = controls[channel][controller] = new Key();
                    k.key      = controller;
                    k.channel  = channel;
                    k.velocity = value;
                    k.tick     = currentTick;
                    k.program  = currentProgram;
                    pedals.add(k);
                }
                else {
                    Key k = controls[channel][controller];
                    if (k != null) {
                        k.duration = currentTick-k.tick;
                    }
                }
            }
        }
    }


    /**
     * This private class represents a key event.
     */
    protected class Key {
        int  velocity;
        long tick;
        long duration;
        int  channel;
        int  program;
        int  key;
    }


    /**
     * Initializes various data structures before a track is processed.
     */
    private void initTrack(int tracknr) {
        keys     = new Key[NR_CHANNELS][NR_KEYS];
        controls = new Key[NR_CHANNELS][NR_CONTROLS];
        notes    = new LinkedList<Key>();
        pedals   = new LinkedList<Key>();
        currentProgram = 0;
        currentTick    = 0;
//        currentTrackNr = tracknr;
    }


//    /**
//     * Creates a GeneralNote denotator from a key event.
//     */
//    private Denotator makeGeneralNote(Key key) {
//        Denotator simpleNote = simpleNoteForm.createDefaultDenotator();
//        try {
//            DenoAccess.setIdFactor(simpleNote, 0, makeVoice(key));
//            DenoAccess.setIdFactor(simpleNote, 1, makeOnset(key.tick));
//            DenoAccess.setIdFactor(simpleNote, 2, makePitch(key.key));
//            DenoAccess.setIdFactor(simpleNote, 3, makeLoudness(key.velocity));
//            DenoAccess.setIdFactor(simpleNote, 4, makeDuration(key.duration));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        return DenoFactory.make(generalNoteForm, 0, simpleNote); 
//    }

    private Denotator makeNote(Key key) {
        LinkedList<Denotator> factors = new LinkedList<Denotator>();
        factors.add(makeOnset(key.tick));
        factors.add(makePitch(key.key));
        factors.add(makeLoudness(key.velocity));
        factors.add(makeDuration(key.duration));
        factors.add(makeVoice(key.channel));
        return makeDenotator(noteForm, factors);
    }


//    /**
//     * Creates a Voice denotator from a key event.
//     */
//    private Denotator makeVoice(Key key) {
//        ZString zstr = new ZString(new String[] {"Track", "Channel", "Program"},
//                                   new int[] { currentTrackNr+1, key.channel+1, key.program+1 });
//        try {
//            return new SimpleDenotator(null, (SimpleForm)voiceForm, new ZStringElement(zstr));
//        }
//        catch (RubatoModuleException e) {
//            return null;
//        }
//    }


    /**
     * Creates an Onset denotator from a key event.
     */
    private Denotator makeOnset(long tick) {
        double onset = tempoFactor*tick/ticksPerQuarter;
        return makeDenotator(onsetForm, onset);        
    }

    
    /**
     * Creates a Pitch denotator from a key event.
     */
    private Denotator makePitch(int key) {
        return makeDenotator(pitchForm, new Rational(key));
    }
    

    /**
     * Creates a Loudness denotator from a key event.
     */
    private Denotator makeLoudness(int velocity) {
        return makeDenotator(loudnessForm, velocity);
    }
    

    /**
     * Creates a Duration denotator from a key event.
     */
    private Denotator makeDuration(long duration) {
        double d = tempoFactor*duration/ticksPerQuarter;
        return makeDenotator(durationForm, d);
    }


    /**
     * Creates a Voice denotator from a key event.
     */
    private Denotator makeVoice(int channel) {
        return makeDenotator(voiceForm, channel);
    }
    
    
//    private Denotator makePedal(Key key) {
//        LinkedList denoList = new LinkedList();
//        denoList.add(makeVoice(key));
//        denoList.add(makeOnset(key.tick));
//        denoList.add(makeDuration(key.duration));
//        denoList.add(DenoFactory.makeString("", pedalSymbolForm, "Sustain"));
//        Denotator pedal = DenoFactory.make(pedalForm, denoList);
//        return DenoFactory.make(groupArticulationForm, 1, pedal);
//    }
    

//    /**
//     * Registers a tempo change at the current tick.
//     */
//    private void changeTempo(int tempo) {
//        tempoList.add(makeTempo(currentTick, tempo));
//    }

    
//    /**
//     * Registers a time signature change at the current tick.
//     */
//    private void changeTimeSig(int numerator, int denominator) {
//        timeList.add(makeTimeSig(currentTick, numerator, denominator));
//    }
    

//    /**
//     * Registers a key signature change at the current tick.
//     */
//    private void changeKeySig(int signature) {
//        keyList.add(makeKeySig(currentTick, signature));
//    }

    
//    /**
//     * Creates a Tempo denotator.
//     * @param onset the tick at which the tempo changes
//     * @param mspq the tempo in microseconds per quarter
//     */
//    private Denotator makeTempo(long onset, int mspq) {
//        LinkedList denoList = new LinkedList();
//        int maelzel = Math.round((float)1000000.0/mspq*60);
//        denoList.add(DenoFactory.makeString(voiceForm, "All"));
//        denoList.add(makeOnset(onset));
//        ZStringElement symstr = new ZStringElement(new ZString("Maelzel", maelzel));
//        denoList.add(DenoFactory.makeSimple(absoluteTempoSymbolForm, symstr));
//        Denotator atempo = DenoFactory.make(absoluteTempoForm, denoList);
//        return DenoFactory.make(tempoForm, 0, atempo);
//    }


//    /**
//     * Creates a TimeSignature denotator.
//     * @param onset the tick at which the time signature changes
//     */
//    private Denotator makeTimeSig(long onset, int numerator, int denominator) {
//        LinkedList denoList = new LinkedList();
//        denoList.add(DenoFactory.makeString(voiceForm, "All"));
//        denoList.add(makeOnset(onset));
//        ZFreeElement e = ZProperFreeElement.make(new int[] { numerator, denominator });
//        denoList.add(DenoFactory.makeSimple(timeSymbolForm, e));
//        return DenoFactory.make(timeSignatureForm, denoList);
//    }


//    /**
//     * Creates a KeySignature denotator.
//     * @param onset the tick at which the key signature changes
//     * @param signature (-7 = Cb, 0 = C, 7 = C#)
//     */
//    private Denotator makeKeySig(long onset, int signature) {
//        LinkedList denoList = new LinkedList();
//        denoList.add(DenoFactory.makeString(voiceForm, "All"));
//        denoList.add(makeOnset(onset));
//        ZnFreeElement e = ZnProperFreeElement.make(sigs[signature+7], 3);
//        denoList.add(DenoFactory.makeSimple(keySymbolForm, e));
//        return DenoFactory.make(keySignatureForm, denoList);
//    }


    private final int NR_CHANNELS = 16;
    private final int NR_KEYS = 128;
    private final int NR_CONTROLS = 128;

    private Sequence   sequence;
    private Track[]    tracks;
    private double     tempoFactor = 1.0;
    private int        ticksPerQuarter;
//    private long       seqLength;
    private long       currentTick;
//    private int        currentTrackNr;
    private int        currentProgram;
    private Key[][]    keys;
    private Key[][]    controls;
    private LinkedList<Key> notes;
    private LinkedList<Key> pedals;
//    private LinkedList tempoList;
//    private LinkedList timeList;
//    private LinkedList keyList;

    private Denotator  score;


    //
    // Forms
    //
    
    private static Repository rep = Repository.systemRepository();
    private static Form onsetForm    = rep.getForm("Onset");
    private static Form pitchForm    = rep.getForm("Pitch");
    private static Form loudnessForm = rep.getForm("Loudness");
    private static Form durationForm = rep.getForm("Duration");
    private static Form voiceForm    = rep.getForm("Voice");
    private static Form noteForm     = rep.getForm("Note");
    private static Form scoreForm    = rep.getForm("Score");
//    private static Form simpleNoteForm = rep.getForm("SimpleNote");
//    private static Form generalNoteForm = rep.getForm("GeneralNote");
//    private static Form generalNotesForm = rep.getForm("GeneralNotes");
//    private static Form tempiForm = rep.getForm("Tempi");
//    private static Form tempoForm = rep.getForm("Tempo");
//    private static Form absoluteTempoForm = rep.getForm("AbsoluteTempo");
//    private static Form absoluteTempoSymbolForm = rep.getForm("AbsoluteTempoSymbol");
//    private static Form keySymbolForm = rep.getForm("KeySymbol");
//    private static Form keySignatureForm = rep.getForm("KeySignature");
//    private static Form keySignaturesForm = rep.getForm("KeySignatures");
//    private static Form timeSymbolForm = rep.getForm("TimeSymbol");
//    private static Form timeSignatureForm = rep.getForm("TimeSignature");
//    private static Form timeSignaturesForm = rep.getForm("TimeSignatures");
//    private static Form signatureForm = rep.getForm("Signatures");
//    private static Form pedalSymbolForm = rep.getForm("PedalSymbol");
//    private static Form pedalForm = rep.getForm("Pedal");
//    private static Form groupArticulationForm = rep.getForm("GroupArticulation");    
//    private static Form groupArticulationsForm = rep.getForm("GroupArticulations");    
//    private static Form scoreForm = rep.getForm("Score");

    @SuppressWarnings("unused")
    private static int[][] sigs = {
        { 2, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2 }, // -7  Cb
        { 2, 0, 2, 0, 2, 0, 0, 2, 0, 2, 0, 2 }, // -6  Gb
        { 0, 0, 2, 0, 2, 0, 0, 2, 0, 2, 0, 2 }, // -5  Db
        { 0, 0, 2, 0, 2, 0, 0, 0, 0, 2, 0, 2 }, // -4  Ab
        { 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 2 }, // -3  Eb
        { 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2 }, // -2  B
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2 }, // -1  F
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //  0  C   
        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, //  1  G
        { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, //  2  D
        { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 }, //  3  A
        { 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0 }, //  4  E
        { 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0 }, //  5  H
        { 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0 }, //  6  F#
        { 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1 }, //  7  C#
    };
}
