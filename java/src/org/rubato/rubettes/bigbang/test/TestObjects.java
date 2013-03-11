package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.module.DomainException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ProductElement;
import org.rubato.math.module.QElement;
import org.rubato.math.module.RElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangScore;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class TestObjects {
	
	public final Form SOUND_SCORE_FORM = Repository.systemRepository().getForm("SoundScore");
	public final Form SOUND_NODE_FORM = Repository.systemRepository().getForm("SoundNode");
	
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
	public LimitDenotator node0, node1Absolute, node1Relative, node2Absolute, node2Relative;
	public PowerDenotator flatMacroScore;
	public PowerDenotator multiLevelMacroScore;
	public PowerDenotator realTriples, rationalTriples;
	
	public TestObjects() {
		this.generator = new SoundNoteGenerator();
		this.score = new BigBangScore(this.generator.SOUND_SCORE_FORM);
		this.scoreManager = new BigBangScoreManager(new BigBangController());
		this.note0 = this.generator.createNoteDenotator(new double[]{0,60,120,1,0,0});
		this.note1Absolute = this.generator.createNoteDenotator(new double[]{1,63,116,1,0,0});
		this.note1Relative = this.generator.createNoteDenotator(new double[]{1,3,-4,0,0,0});
		this.note2Absolute = this.generator.createNoteDenotator(new double[]{2,60,121,1,1,0});
		this.note2Relative = this.generator.createNoteDenotator(new double[]{1,-3,5,0,1,0});
		this.node0 = this.generator.createNodeDenotator(note0);
		this.node1Absolute = this.generator.createNodeDenotator(note1Absolute);
		this.node1Relative = this.generator.createNodeDenotator(note1Relative);
		this.node2Absolute = this.generator.createNodeDenotator(note2Absolute);
		this.node2Relative = this.generator.createNodeDenotator(note2Relative);
		this.flatMacroScore = this.generator.createFlatSoundScore(this.ABSOLUTE);
		this.multiLevelMacroScore = this.generator.createMultiLevelSoundScore(this.RELATIVE);
		try {
			this.createComplexSoundScore();
			this.createProductRingRealTriples();
			this.createFreeRationalTriples();
		} catch (RubatoException err) {
			err.printStackTrace();
		}
	}
	
	private void createComplexSoundScore() {
		this.score.setComposition(this.multiLevelMacroScore.copy());
		List<Denotator> notes = new ArrayList<Denotator>();
		notes.add(this.generator.createNoteDenotator(this.NOTE2_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE1_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE0_VALUES));
		List<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,0}));
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,0}));
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,1,0,1,0,0}));
		int[] powersetIndices = new int[]{1, 1, 1};
		this.score.addObjects(notes, parentPaths, powersetIndices);
		
		notes = new ArrayList<Denotator>();
		notes.add(this.generator.createNoteDenotator(this.NOTE2_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE1_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE0_VALUES));
		parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,0,6,1}));
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,1,0,0}));
		parentPaths.add(new DenotatorPath(this.generator.SOUND_SCORE_FORM, new int[]{0,1,0,1,0,0,6,0}));
		powersetIndices = new int[]{0, 1, 0};
		this.score.addObjects(notes, parentPaths, powersetIndices);
	}
	
	private void createProductRingRealTriples() throws DomainException, RubatoException {
		SimpleForm realTripleForm = (SimpleForm) Repository.systemRepository().getForm("RealTriple");
		ProductElement element1 = ProductElement.make(new RElement(1), new RElement(2), new RElement(3));
		ProductElement element2 = ProductElement.make(new RElement(4), new RElement(3), new RElement(1));
		ProductElement element3 = ProductElement.make(new RElement(2), new RElement(1), new RElement(5));
		List<Denotator> triples = new ArrayList<Denotator>();
		triples.add(new SimpleDenotator(NameDenotator.make(""), realTripleForm, element1));
		triples.add(new SimpleDenotator(NameDenotator.make(""), realTripleForm, element2));
		triples.add(new SimpleDenotator(NameDenotator.make(""), realTripleForm, element3));
		PowerForm realTriplesForm = new PowerForm(NameDenotator.make("RealTriples"), realTripleForm);
		//Repository.systemRepository().register(realTriplesForm);
		this.realTriples = new PowerDenotator(NameDenotator.make(""), realTriplesForm, triples);
	}
	
	private void createFreeRationalTriples() throws DomainException, RubatoException {
		Module rationalTriples = Repository.systemRepository().getModule("Triples of rationals");
		SimpleForm rationalTripleForm = new SimpleForm(NameDenotator.make("RationalTriple"), rationalTriples); 
		//Repository.systemRepository().register(rationalTripleForm);
		ModuleElement element1 = rationalTriples.createElement(Arrays.<ModuleElement>asList(new QElement(1), new QElement(2), new QElement(3)));
		ModuleElement element2 = rationalTriples.createElement(Arrays.<ModuleElement>asList(new QElement(4), new QElement(3), new QElement(1)));
		ModuleElement element3 = rationalTriples.createElement(Arrays.<ModuleElement>asList(new QElement(2), new QElement(1), new QElement(5)));
		ModuleElement element4 = rationalTriples.createElement(Arrays.<ModuleElement>asList(new QElement(3), new QElement(4), new QElement(2)));
		List<Denotator> triples = new ArrayList<Denotator>();
		triples.add(new SimpleDenotator(NameDenotator.make(""), rationalTripleForm, element1));
		triples.add(new SimpleDenotator(NameDenotator.make(""), rationalTripleForm, element2));
		triples.add(new SimpleDenotator(NameDenotator.make(""), rationalTripleForm, element3));
		triples.add(new SimpleDenotator(NameDenotator.make(""), rationalTripleForm, element4));
		PowerForm realTriplesForm = new PowerForm(NameDenotator.make("RationalTriples"), rationalTripleForm);
		this.rationalTriples = new PowerDenotator(NameDenotator.make(""), realTriplesForm, triples);
	}

}
