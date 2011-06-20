package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangScore;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class TestObjects {
	
	public final double[][] ABSOLUTE = new double[][]{
			{0,60,120,1,0,0},{1,63,116,1,0,0},{2,60,121,1,1,0}};
	public final double[][] RELATIVE = new double[][]{
			{0,60,120,1,0,0},{1,3,-4,0,0,0},{1,-3,5,0,1,0}};
	
	public final double[] NOTE0_VALUES = new double[]{0,60,120,1,0,0};
	public final double[] NOTE1_ABSOLUTE_VALUES = new double[]{1,63,116,1,0,0};
	public final double[] NOTE1_RELATIVE_VALUES = new double[]{1,3,-4,0,0,0};
	public final double[] NOTE2_ABSOLUTE_VALUES = new double[]{2,60,121,1,1,0};
	public final double[] NOTE2_RELATIVE_VALUES = new double[]{1,-3,5,0,1,0};
	
	public BigBangScore score;
	public BigBangScoreManager scoreManager;
	public SoundNoteGenerator generator;
	public LimitDenotator note0, note1Absolute, note1Relative, note2Absolute, note2Relative; 
	public PowerDenotator flatMacroScore;
	public PowerDenotator multiLevelMacroScore;
	
	public TestObjects() {
		this.generator = new SoundNoteGenerator();
		this.score = new BigBangScore(this.generator);
		this.scoreManager = new BigBangScoreManager(new BigBangController());
		this.note0 = this.generator.createNoteDenotator(new double[]{0,60,120,1,0,0});
		this.note1Absolute = this.generator.createNoteDenotator(new double[]{1,63,116,1,0,0});
		this.note1Relative = this.generator.createNoteDenotator(new double[]{1,3,-4,0,0,0});
		this.note2Absolute = this.generator.createNoteDenotator(new double[]{2,60,121,1,1,0});
		this.note2Relative = this.generator.createNoteDenotator(new double[]{1,-3,5,0,1,0});
		this.flatMacroScore = this.generator.createFlatSoundScore(this.ABSOLUTE);
		this.multiLevelMacroScore = this.generator.createMultiLevelSoundScore(this.RELATIVE);
		this.createComplexSoundScore();
	}
	
	private void createComplexSoundScore() {
		this.score.setComposition(this.multiLevelMacroScore.copy());
		List<LimitDenotator> notes = new ArrayList<LimitDenotator>();
		notes.add(this.generator.createNoteDenotator(this.NOTE2_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE1_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE0_VALUES));
		List<NotePath> parentPaths = new ArrayList<NotePath>();
		parentPaths.add(new NotePath(new int[]{0,0}));
		parentPaths.add(new NotePath(new int[]{0,0}));
		parentPaths.add(new NotePath(new int[]{0,1,0,1,0,0}));
		int[] functions = new int[]{NotePath.MODULATOR, NotePath.MODULATOR, NotePath.MODULATOR};
		this.score.addNotes(notes, parentPaths, functions);
		
		notes = new ArrayList<LimitDenotator>();
		notes.add(this.generator.createNoteDenotator(this.NOTE2_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE1_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE0_VALUES));
		parentPaths = new ArrayList<NotePath>();
		parentPaths.add(new NotePath(new int[]{0,0,6,1}));
		parentPaths.add(new NotePath(new int[]{0,1,0,0}));
		parentPaths.add(new NotePath(new int[]{0,1,0,1,0,0,6,0}));
		this.score.addNotes(notes, parentPaths, functions);
	}

}
