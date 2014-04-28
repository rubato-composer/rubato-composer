/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.composer;

/**
 * Add the class names of all builtin rubettes to this list.
 * 
 * @author Gérard Milmeister
 */
public final class BuiltinRubettes {

    /**
     * All builtin Rubettes specified by their complete class names.
     */
    @SuppressWarnings("nls")
    public static final String[] classes = {
        "org.rubato.rubettes.builtin.DisplayRubette",
        "org.rubato.rubettes.builtin.SourceRubette",
        "org.rubato.rubettes.builtin.LatchRubette",
        "org.rubato.rubettes.builtin.SimpleRubette",
        "org.rubato.rubettes.builtin.MacroInputRubette",
        "org.rubato.rubettes.builtin.MacroOutputRubette",
        "org.rubato.rubettes.builtin.ConstructorRubette",
        "org.rubato.rubettes.builtin.BooleanRubette",
        "org.rubato.rubettes.builtin.RegisterRubette",
        "org.rubato.rubettes.builtin.RealArithRubette",
        "org.rubato.rubettes.builtin.SelectFormRubette",
        "org.rubato.rubettes.builtin.SplitRubette",
        "org.rubato.rubettes.builtin.SetRubette",
        "org.rubato.rubettes.builtin.ListRubette",
        "org.rubato.rubettes.builtin.StatRubette",
        "org.rubato.rubettes.builtin.MuxRubette",
        "org.rubato.rubettes.builtin.ModuleMapRubette",
        "org.rubato.rubettes.builtin.ReformRubette",
        "org.rubato.rubettes.builtin.SchemeRubette",
        "org.rubato.rubettes.builtin.address.AddressEvalRubette",
        "org.rubato.rubettes.score.MidiFileInRubette",
        "org.rubato.rubettes.score.MidiFileOutRubette",
        "org.rubato.rubettes.score.ScorePlayRubette",
        "org.rubato.rubettes.select2d.Select2DRubette",
        "org.rubato.rubettes.score.ScoreToCsoundRubette",
        "org.rubato.rubettes.score.MelodyRubette",
        "org.rubato.rubettes.score.RhythmizeRubette",
        "org.rubato.rubettes.score.ScaleRubette",
        "org.rubato.rubettes.score.QuantizeRubette",
        "org.rubato.rubettes.score.LilyPondOutRubette",
        "org.rubato.rubettes.texture.TexturalizeRubette",
        "org.rubato.rubettes.wallpaper.WallpaperRubette",
        "org.rubato.rubettes.alteration.AlterationRubette",
        "org.rubato.rubettes.morphing.MorphingRubette",
        "org.rubato.rubettes.bigbang.BigBangRubette",
        "org.rubato.rubettes.image.ImageFileInRubette",
        "org.rubato.rubettes.image.ImageFileOutRubette",
        "org.rubato.rubettes.sound.OscillatorRubette"
        //"org.rubato.rubettes.image.ImageDisplayRubette"
    };

    
    private BuiltinRubettes() { /* this is a pure static class */ }
}
