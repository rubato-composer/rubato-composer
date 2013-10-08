package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformationGraphTest extends TestCase {
	
	private BigBangModel model;
	private TestObjects objects;
	private TransformationPaths nodePaths;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.model = new BigBangModel(new BigBangController());
		this.nodePaths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NODE_FORM, new int[][]{{0,0},{0,1}});
	}
	
	public void testModifyLeadingToTooManyPaths() {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		this.model.addObjects(this.createNodePathAndValuesMapList(new double[]{0,1,2,3}, new double[]{60,65,66,67}),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		TransformationProperties properties = new TransformationProperties(this.createSelectedObjectsPaths(), Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.rotateObjects(properties, new double[]{1,0}, Math.PI/2);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		this.model.getUndoRedoModel().modifyOperation(0, 0.5);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testModifyWithSatellites() {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		this.model.addObjects(this.createNodePathAndValuesMapList(new double[]{0,1,2,3}, new double[]{60,65,66,67}),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		ArrayList<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		DenotatorPath parentNotePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{3});
		this.model.buildSatellites(paths, parentNotePath, 0);
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.getUndoRedoModel().modifyOperation(0, 0.5);
		//anchor tone not there anymore so satellites should not be built. all we get are two of the four initial notes
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	private ArrayList<Map<DenotatorPath,Double>> createNodePathAndValuesMapList(double[] onsets, double[] pitches) {
		ArrayList<Map<DenotatorPath,Double>> list = new ArrayList<Map<DenotatorPath,Double>>();
		for (int i = 0; i < onsets.length; i++) {
			list.add(this.createNodePathAndValuesMap(onsets[i], pitches[i]));
		}
		return list;
	}
	
	private ArrayList<DenotatorPath> createPathsList(DenotatorPath path, int amount) {
		ArrayList<DenotatorPath> list = new ArrayList<DenotatorPath>();
		for (int i = 0; i < amount; i++) {
			list.add(path);
		}
		return list;
	}
	
	private Map<DenotatorPath,Double> createNodePathAndValuesMap(double onset, double pitch) {
		Map<DenotatorPath,Double> valuesMap = new TreeMap<DenotatorPath,Double>();
		valuesMap.put(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,0}), onset);
		valuesMap.put(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,1}), pitch);
		return valuesMap;
	}
	
	private SelectedObjectsPaths createSelectedObjectsPaths() {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{3}));
		return new SelectedObjectsPaths(paths, null);
	}

}
