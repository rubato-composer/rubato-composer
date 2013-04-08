package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

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
import org.rubato.rubettes.util.DenotatorValueFinder;
import org.rubato.rubettes.util.ObjectGenerator;
import org.rubato.rubettes.util.SoundNoteGenerator;

public class TestObjects {
	
	public final Form SOUND_SCORE_FORM = Repository.systemRepository().getForm("SoundScore");
	public final Form SOUND_NODE_FORM = Repository.systemRepository().getForm("SoundNode");
	public final Form SOUND_NOTE_FORM = Repository.systemRepository().getForm("SoundNote");
	public final Module RATIONAL_TRIPLE_MODULE = Repository.systemRepository().getModule("Triples of rationals"); 
	public final SimpleForm RATIONAL_TRIPLE_FORM = new SimpleForm(NameDenotator.make("RationalTriple"), RATIONAL_TRIPLE_MODULE);
	public final PowerForm RATIONAL_TRIPLES_FORM = new PowerForm(NameDenotator.make("RationalTriples"), RATIONAL_TRIPLE_FORM);
	public final SimpleForm REAL_TRIPLE_FORM = (SimpleForm) Repository.systemRepository().getForm("RealTriple");
	public final PowerForm REAL_TRIPLES_FORM = new PowerForm(NameDenotator.make("RealTriples"), REAL_TRIPLE_FORM);
	
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
	private ObjectGenerator objectGenerator;
	
	public LimitDenotator note0, note1Absolute, note1Relative, note2Absolute, note2Relative;
	public LimitDenotator node0, node1Absolute, node1Relative, node2Absolute, node2Relative;
	public PowerDenotator flatMacroScore;
	public PowerDenotator multiLevelMacroScore;
	public PowerDenotator realTriples, rationalTriples;
	
	public TestObjects() {
		this.generator = new SoundNoteGenerator();
		this.objectGenerator = new ObjectGenerator();
		this.score = new BigBangScore(this.generator.getSoundScoreForm());
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
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,0}));
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,0}));
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,1,0,1,0,0}));
		int[] powersetIndices = new int[]{1, 1, 1};
		this.score.addObjects(notes, parentPaths, powersetIndices);
		
		notes = new ArrayList<Denotator>();
		notes.add(this.generator.createNoteDenotator(this.NOTE2_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE1_ABSOLUTE_VALUES));
		notes.add(this.generator.createNoteDenotator(this.NOTE0_VALUES));
		parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,0,6,1}));
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,1,0,0}));
		parentPaths.add(new DenotatorPath(this.generator.getSoundScoreForm(), new int[]{0,1,0,1,0,0,6,0}));
		powersetIndices = new int[]{0, 1, 0};
		this.score.addObjects(notes, parentPaths, powersetIndices);
	}
	
	private void createProductRingRealTriples() throws DomainException, RubatoException {
		ProductElement element1 = ProductElement.make(new RElement(1), new RElement(2), new RElement(3));
		ProductElement element2 = ProductElement.make(new RElement(4), new RElement(3), new RElement(1));
		ProductElement element3 = ProductElement.make(new RElement(2), new RElement(1), new RElement(5));
		List<Denotator> triples = new ArrayList<Denotator>();
		triples.add(new SimpleDenotator(NameDenotator.make(""), REAL_TRIPLE_FORM, element1));
		triples.add(new SimpleDenotator(NameDenotator.make(""), REAL_TRIPLE_FORM, element2));
		triples.add(new SimpleDenotator(NameDenotator.make(""), REAL_TRIPLE_FORM, element3));
		
		//Repository.systemRepository().register(realTriplesForm);
		this.realTriples = new PowerDenotator(NameDenotator.make(""), REAL_TRIPLES_FORM, triples);
	}
	
	private void createFreeRationalTriples() throws DomainException, RubatoException {
		ModuleElement element1 = RATIONAL_TRIPLE_MODULE.createElement(Arrays.<ModuleElement>asList(new QElement(1), new QElement(2), new QElement(3)));
		ModuleElement element2 = RATIONAL_TRIPLE_MODULE.createElement(Arrays.<ModuleElement>asList(new QElement(4), new QElement(3), new QElement(1)));
		ModuleElement element3 = RATIONAL_TRIPLE_MODULE.createElement(Arrays.<ModuleElement>asList(new QElement(2), new QElement(1), new QElement(5)));
		ModuleElement element4 = RATIONAL_TRIPLE_MODULE.createElement(Arrays.<ModuleElement>asList(new QElement(3), new QElement(4), new QElement(2)));
		List<Denotator> triples = new ArrayList<Denotator>();
		triples.add(new SimpleDenotator(NameDenotator.make(""), RATIONAL_TRIPLE_FORM, element1));
		triples.add(new SimpleDenotator(NameDenotator.make(""), RATIONAL_TRIPLE_FORM, element2));
		triples.add(new SimpleDenotator(NameDenotator.make(""), RATIONAL_TRIPLE_FORM, element3));
		triples.add(new SimpleDenotator(NameDenotator.make(""), RATIONAL_TRIPLE_FORM, element4));
		this.rationalTriples = new PowerDenotator(NameDenotator.make(""), RATIONAL_TRIPLES_FORM, triples);
	}
	
	public Denotator createRationalTriple(double[] values) {
		return this.objectGenerator.createDenotator(this.RATIONAL_TRIPLE_FORM, values);
	}
	
	public Denotator createRealTriple(double[] values) {
		return this.objectGenerator.createDenotator(this.REAL_TRIPLE_FORM, values);
	}
	
	public void assertEqualDenotators(Denotator d1, Denotator d2) {
		TestCase.assertEquals(d1.getForm(), d2.getForm());
		List<DenotatorPath> allValuePaths = new ArrayList<DenotatorPath>(new DenotatorValueFinder(d1.getForm(), true).getValueNamesAndPaths().values());
		allValuePaths.addAll(new DenotatorValueFinder(d2.getForm(), true).getValueNamesAndPaths().values());
		for (DenotatorPath currentPath : allValuePaths) {
			TestCase.assertEquals(this.objectGenerator.getDoubleValue(d1, currentPath), this.objectGenerator.getDoubleValue(d2, currentPath));
		}
	}

}
