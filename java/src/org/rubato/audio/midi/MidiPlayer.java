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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.sound.midi.*;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;


/**
 * This class provides various methods for playing denotators
 * representing scores to MIDI output. Presently the MIDI device
 * used is the Java software synthesizer.
 * There are also methods for writing a score to a MIDI file.
 *  
 * @author Gérard Milmeister
 */
public class MidiPlayer implements MetaEventListener {

    /**
     * Creates a new MIDI player.
     * To actually play, the player must first be opened.
     */
    public MidiPlayer() {}        


    /**
     * Returns the names of the instruments provided by the
     * synthesizer.
     */
    public static String[] getInstruments() {
        String[] instruments = new String[0];
        try {
            Synthesizer s = MidiSystem.getSynthesizer();
            if (s != null) {
                s.open();
                Soundbank sb = s.getDefaultSoundbank();
                if (sb != null) {
                    Instrument[] ins = sb.getInstruments();
                    instruments = new String[ins.length];
                    for (int i = 0; i < ins.length; i++) {                        
                        instruments[i] = ins[i].getName();
                    }
                }
                s.close();
            }
        }
        catch (Exception e) {}
        return instruments;
    }
    
    
    /**
     * Opens the MIDI player.
     * @throws MidiUnavailableException
     */
    public void open()
            throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer();
        sequencer.addMetaEventListener(this);
        sequencer.open();
        
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        latency = synthesizer.getLatency();
        
        receiver = synthesizer.getReceiver();
        transmitter = sequencer.getTransmitter();
        transmitter.setReceiver(receiver);
        
        for (Receiver r : receivers) {
            sequencer.getTransmitter().setReceiver(r);
        }
        
        opened = true;
    }


    /**
     * Closes the MIDI player.
     */
    public void close() {
        if (receiver != null) {
            receiver.close();
        }
        if (transmitter != null) {
            transmitter.close();
        }
        if (synthesizer != null) {
            synthesizer.close();
        }
        if (sequencer != null) {
            sequencer.close();
        }
        for (Receiver r : receivers) {
            r.close();
        }
        opened = false;
    }
    
   
    /**
     * Sets the resolution in ticks per quarter note.
     */
    public void setResolution(int resolution) {
        this.resolution = resolution; 
    }
    
    
    /**
     * Returns the resolution in ticks per quarter note.
     */
    public int getResolution() {
        return resolution;
    }
    
    /**
     * Sets the default tempo in microseconds per quarter note.
     */
    public void setTempo(int mspq) {
        this.mspq = mspq;
    }
    
    
    /**
     * Returns the default tempo in microseconds per quarter note.
     */
    public int getTempo() {
        return mspq;
    }

    
    /**
     * Sets the initial programs for the channels given by
     * the array <code>voices</codes>.
     * @param voices the program of channel <code>i</code> is
     *        <code>voices[i]</code>
     */
    public void setPrograms(int[] voices) {
    	System.out.println(voices.length);
        for (int i = 0; i < voices.length; i++) {
            channelProgram.put(i, voices[i]);
        }
        updateControlTrack();
    }
    
    
    /**
     * Sets the initial program for a channel.
     */
    public void setProgram(int channel, int program) {
        channelProgram.put(channel, program);
        updateControlTrack();
    }
    
    
    private void updateControlTrack() {
        // program changes and initial tempo are 
        // all put into a separate control track
        if (controlTrack != null) {
            // remove old control track
            sequence.deleteTrack(controlTrack);
        }
        controlTrack = sequence.createTrack();
        // set the initial tempo
        controlTrack.add(makeTempoEvent(mspq, 0));
        // add all initial program changes
        System.out.println(channelProgram.entrySet().size());
        System.out.println(channelProgram);
        for (Map.Entry<Integer,Integer> entry : channelProgram.entrySet()) {
            controlTrack.add(makeProgramChangeEvent(entry.getKey(), 0, entry.getValue()));
        }        
    }
    
    
    /**
     * Sets the Score denotator that is to be played by MidiPlayer. 
     */
    public void setScore(Denotator score) {
        if (!score.hasForm(scoreForm)) {
            throw new IllegalArgumentException("Argument denotator is not of form Score.");
        }
        this.score = score;
        newSequence();
        // the program for the 1st channel is 0 by default
        setProgram(0, 0);
        /*setProgram(1, 1);
        setProgram(2, 2);
        setProgram(3, 3);
        setProgram(4, 4);
        setProgram(5, 5);
        setProgram(6, 6);*/
        /*
        msg.setMessage(0x59, new byte[] { 0, 0 }, 2 );
        controlTrack.add(new MidiEvent(msg, 0));
        msg.setMessage(0x58, new byte[] { 0x04, 0x02, 0x18, 0x08 }, 4);
        controlTrack.add(new MidiEvent(msg, 0));
        */
        ArrayList<MidiChange> midiChanges = scoreToMidiNotes();        
        try {
            createTracksFromNotes(midiChanges);
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }        
    }
    

    /**
     * Creates a new sequence that can be filled with tracks.
     */
    public void newSequence() {
        try {
            sequence = new Sequence(Sequence.PPQ, resolution);
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }            
    }


    /**
     * Plays the current sequence.
     */
    public void play() {
        if (!opened) {
            throw new IllegalStateException("MidiPlayer is not open.");
        }

        if (sequence != null) {
            try {
                sequencer.setSequence(sequence);
                sequencer.start();
            }
            catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * Pause the currently playing sequence.
     */
    public void pause() {
        if (opened) {
            sequencer.stop();
        }
    }


    /**
     * Resumes the currently playing sequence.
     */
    public void resume() {
        if (opened) {
            sequencer.start();
        }
    }


    /**
     * Sets the tempo scale.
     * 1.0 is normal playing tempo.
     * 2.0 is twice as fast.
     * 0.5 is half as fast.
     */
    public void setPlayTempoFactor(float f) {
        if (sequencer != null) {
            sequencer.setTempoFactor(f);
        }
    }
    
    
    /**
     * Returns the current tick position of the playing sequence.
     */
    public long getTickPosition() {
        return sequencer.getTickPosition();
    }
    
    
    /**
     * Sets the current tick position of the playing sequence.
     */
    public void setTickPosition(long tick) {
        sequencer.setTickPosition(tick);
    }
    
    
    /**
     * Returns the current position of the playing sequence.
     * The unit is quarter notes.
     */
    public double getPosition() {
        return getTickPosition()/(double)resolution;
    }
    
    
    /**
     * Sets the current position of the playing sequence.
     * The unit is quarter notes.
     */
    public void setPosition(double pos) {
        setTickPosition((int)pos*resolution);
    }
    
    
    public double getLatency() {
        return latency;
    }
    
    
    public void addReceiver(Receiver r) {
        receivers.add(r);
    }
    
    
    /**
     * Returns true iff the sequencer is open and ready to play.
     */
    public boolean isOpen() {
        return sequencer != null && sequencer.isOpen();
    }
    
    
    /**
     * Returns true iff the sequencer is playing.
     */
    public boolean isRunning() {
        return sequencer != null && sequencer.isRunning();
    }
    
    
    public void setStopListener(ActionListener stopListener) {
        this.stopListener = stopListener; 
    }
    
    
    /**
     * Writes the sequence created by newSequence to an output stream.
     */
    public void writeSequence(OutputStream out)
            throws IOException {
        if (sequence != null) {
            MidiSystem.write(sequence, 1, out);
        }
    }
    

    public void meta(MetaMessage event) {
        if (event.getType() == 47 && stopListener != null) {
            stopListener.actionPerformed(new ActionEvent(MidiPlayer.this, 0, ""));
        }
    }

    
    /**
     * Converts the denotator of Score form to a list of MIDI changes
     * sorted by onsets.
     */
    private ArrayList<MidiChange> scoreToMidiNotes() {
        List<Denotator> noteList = ((FactorDenotator)score).getFactors();
        ArrayList<MidiChange> midiChanges = new ArrayList<MidiChange>(noteList.size()*3);
        
        int begin = Integer.MAX_VALUE;
        // process note on/off events
        for (Denotator d : noteList) {
            MidiChange midiChange = new MidiChange((FactorDenotator)d, resolution);
            begin = Math.min(midiChange.getOnset(), begin);
            midiChanges.add(midiChange);
            midiChanges.add(midiChange.getNoteOff());
        }
        
        /*
        // process pedal events
        List pedalList = Select.select(pedalForm, DenoAccess.getIdFactor(score, 5));
        Iterator iter = pedalList.iterator();
        while (iter.hasNext()) {
            MidiChange midiChange = MidiChange.getPedalOn((FactorDenotator) iter.next(), resolution);
            midiChanges.add(midiChange);
            midiChange = midiChange.getPedalOff();
            midiChanges.add(midiChange);
        }
        */
        
        //
        // sort the generated events by onsets
        //
        
        // first shift all notes, so that the first note starts at 1
        int shift = -begin+1;        
        for (MidiChange c : midiChanges) {
            c.shiftOnset(shift);
        }
        Collections.sort(midiChanges);
        
        return midiChanges;
    }


    /**
     * Creates MIDI tracks based on the list of MidiChange.
     * From each MidiChange a MIDI event is created within a track. 
     */
    private void createTracksFromNotes(ArrayList<MidiChange> midiChanges)
            throws InvalidMidiDataException {
        Track[] tracks = new Track[30];
        int channel  = 0;
        int pitch    = 0;
        int loudness = 0;
        int onset    = 0;
        int track    = 0;
        
        for (MidiChange midiChange : midiChanges) {
            onset    = midiChange.getOnset();
            pitch    = midiChange.getPitch();
            loudness = midiChange.getLoudness();
            channel  = midiChange.getChannel();
            track    = midiChange.getTrack();
            
            ShortMessage msg = new ShortMessage();
            switch (midiChange.getType()) {
                case MidiChange.NOTE_ON: {
                    msg.setMessage(ShortMessage.NOTE_ON, channel, pitch, loudness); 
                    break;
                }
                case MidiChange.NOTE_OFF: {
                    msg.setMessage(ShortMessage.NOTE_OFF, channel, pitch, loudness); 
                    break;
                }
                case MidiChange.CONTROL_CHANGE: {
                    msg.setMessage(ShortMessage.CONTROL_CHANGE, channel, pitch, loudness);
                    break;
                }
            }
            
            // clamp values for loudness and pitch to allowed MIDI values
            loudness = Math.max(0, Math.min(loudness, 127));
            pitch = Math.max(0, Math.min(pitch, 127));
            
            if (tracks[track] == null) {
                // Track does not yet exist, create it
                tracks[track] = sequence.createTrack();
            }
            
            // add MIDI event to the track
            tracks[track].add(new MidiEvent(msg, onset));
        }
        
        // create a stop message in the first track a little after the last note
        // this is necessary, so that the sequencer is not stopped
        // in the midst of the last note fading out
        if (tracks[0] == null) {
            tracks[0] = sequence.createTrack();            
        }
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.STOP);
        tracks[0].add(new MidiEvent(msg, onset+1500));
    }

    
    /**
     * Creates a MIDI program change event at the specified onset in the given
     * channel.
     */
    private MidiEvent makeProgramChangeEvent(int channel, int onset, int program) {
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program, 0);
            return new MidiEvent(msg, onset);
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    /**
     * Creates a MIDI tempo event at the given tick setting the
     * specified microseconds per quarter (mspq).
     */
    private MidiEvent makeTempoEvent(int mspq, long tick) {
        MetaMessage msg = new MetaMessage();
        byte[] data = new byte[3];
        data[2] = (byte)(mspq & 0xff);
        mspq >>= 8;
        data[1] = (byte)(mspq & 0xff);
        mspq >>= 8;
        data[0] = (byte)(mspq & 0xff);         
        try {
            msg.setMessage(0x51, data, 3);
            return new MidiEvent(msg, tick);
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
            return null;
        }
    }


    private ActionListener stopListener = null;
    
    private Receiver    receiver    = null;
    private Transmitter transmitter = null;
    private Synthesizer synthesizer = null;
    private Sequencer   sequencer   = null;
    private boolean     opened      = false;
    private double      latency;

    private List<Receiver> receivers = new LinkedList<Receiver>();
    
    private Sequence  sequence   = null;
    private int       resolution = 480;    // ticks per quarter note
    private int       mspq       = 500000; // microseconds per quarter note
    private Denotator score      = null;
    
    private Track controlTrack = null;
    private HashMap<Integer,Integer> channelProgram = new HashMap<Integer,Integer>();

    private static Repository rep = Repository.systemRepository();
    private static Form scoreForm = rep.getForm("Score");
    /*
    private static Form noteForm  = rep.getForm("Note");
    private static Form pedalForm = rep.getForm("Pedal");
    */
}
