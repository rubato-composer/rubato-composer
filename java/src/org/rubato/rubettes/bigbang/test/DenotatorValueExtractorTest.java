package org.rubato.rubettes.bigbang.test;

import junit.framework.TestCase;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.bigbang.model.DenotatorValueExtractor;

public class DenotatorValueExtractorTest extends TestCase {
	
	private BigBangModel model;
	private TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.model = new BigBangModel(new BigBangController());
	}
	
	public void testExtractDisplayObjectsWithMultilevelSoundScore() {
		this.model.setOrAddComposition(this.objects.multiLevelSoundScore);
		new DenotatorValueExtractor(this.model.getObjects(), this.model.getComposition());
		BigBangObjects nodes = this.model.getObjects();
		TestCase.assertEquals(3, nodes.size());
		TestCase.assertEquals(2.0, nodes.last().getNthValue(nodes.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(60.0, nodes.last().getNthValue(nodes.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(121.0, nodes.last().getNthValue(nodes.getCoordinateSystemValueNames().get(2), 0));
		TestCase.assertEquals(1.0, nodes.last().getNthValue(nodes.getCoordinateSystemValueNames().get(3), 0));
		TestCase.assertEquals(1.0, nodes.last().getNthValue(nodes.getCoordinateSystemValueNames().get(4), 0));
		TestCase.assertEquals(2.0, nodes.last().getNthValue(DenotatorValueExtractor.SATELLITE_LEVEL, 0));
		TestCase.assertEquals(0.0, nodes.last().getNthValue(DenotatorValueExtractor.SIBLING_NUMBER, 0));
	}
	
	public void testExtractDisplayObjectsWithQ3() {
		this.model.setOrAddComposition(this.objects.rationalTriples);
		new DenotatorValueExtractor(this.model.getObjects(), this.model.getComposition());
		BigBangObjects triples = this.model.getObjects();
		TestCase.assertEquals(4, triples.size());
		TestCase.assertEquals(4.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(1.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(2), 0));
	}
	
	public void testExtractDisplayObjectsWithProductRing() {
		this.model.setOrAddComposition(this.objects.realTriples);
		new DenotatorValueExtractor(this.model.getObjects(), this.model.getComposition());
		BigBangObjects triples = this.model.getObjects();
		TestCase.assertEquals(3, triples.size());
		TestCase.assertEquals(4.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(1.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(2), 0));
	}
	
	public void testExtractDisplayObjectsWithColimit() {
		this.model.setOrAddComposition(this.objects.integerOrReals);
		new DenotatorValueExtractor(this.model.getObjects(), this.model.getComposition());
		BigBangObjects integerOrReals = this.model.getObjects();
		TestCase.assertEquals(4, integerOrReals.size());
		TestCase.assertEquals("Integer Z", integerOrReals.getCoordinateSystemValueNames().get(0));
		TestCase.assertEquals("Real R", integerOrReals.getCoordinateSystemValueNames().get(1));
		
		//check first element
		TestCase.assertEquals(4.0, integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertNull(integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(1), 0));
		//colimit index
		TestCase.assertEquals(0.0, integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(2), 0));
		
		//check last element
		TestCase.assertNull(integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.5, integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(1), 0));
		//colimit index
		TestCase.assertEquals(1.0, integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(2), 0));
	}

}
