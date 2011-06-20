/*
 * Copyright (C) 2005 Gérard Milmeister
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rubato.math.arith.Rational;
import org.rubato.math.yoneda.*;

public class Note implements Comparable<Note> {
    
    Rational pitch;
    int      pitch_int;
    int      loudness;
    double   duration;
    double   onset;
    int      voice;

    public Note(LimitDenotator note) {
        onset     = ((SimpleDenotator)note.getFactor(0)).getReal();
        pitch     = ((SimpleDenotator)note.getFactor(1)).getRational();
        pitch_int = ((SimpleDenotator)note.getFactor(1)).getRational().intValue();
        loudness  = ((SimpleDenotator)note.getFactor(2)).getInteger();
        duration  = ((SimpleDenotator)note.getFactor(3)).getReal();
        voice     = ((SimpleDenotator)note.getFactor(4)).getInteger();
    }
    
    public int compareTo(Note obj) {
        double c = onset-obj.onset;
        if (c < 0) {
            return -1;
        }
        else if (c > 0) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
    public String toString() {
        return "onset: "+onset+"; pitch: "+pitch+"; duration: "+duration+"; loudness: "+loudness+"; voice: "+voice;
    }
    
    
    public static ArrayList<Note> scoreToNotes(Denotator score) {
        List<Denotator> scoreNotes = ((FactorDenotator)score).getFactors();
        ArrayList<Note> notes = new ArrayList<Note>(scoreNotes.size());
        double duration = 0;
        double begin = Integer.MAX_VALUE;
        for (Denotator d : scoreNotes) {
            LimitDenotator n = (LimitDenotator) d;
            Note note = new Note(n);
            if (note.onset + note.duration > duration) {
                duration = note.onset + note.duration;
            }
            if (note.onset < begin) {
                begin = note.onset;
            }
            notes.add(note);
        }
        
        // shift all notes, so that the first note starts at 0
        for (Note n : notes) {
            n.onset -= begin;
        }
        // also shift duration
        duration -= begin;

        Collections.sort(notes);
        return notes;
    }
}