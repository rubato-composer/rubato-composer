package org.rubato.rubettes.bigbang.test;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.JSynPlayer;
import org.rubato.rubettes.bigbang.view.player.JSynScore;
import org.rubato.rubettes.util.ObjectGenerator;
import org.rubato.rubettes.util.SoundNoteGenerator;

import junit.framework.TestCase;


public class JSynPlayerTest extends TestCase {
	
	TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	/*public void testMoveScoreInTime() throws InterruptedException {
		JSynPlayer player = new JSynPlayer();
		
		Denotator score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80,60,1.5,0,0},{1,90,50,1,0,0}});
		player.play(new DenotatorValueExtractor(score).getJSynScore());
		Thread.sleep(100);
		score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{2,80,60,1.5,0,0},{3,90,50,1,0,0}});
		Thread.sleep(100);
		score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80,60,1.5,0,0},{1,90,50,1,0,0}});
		player.replaceScore(new DenotatorValueExtractor(score).getJSynScore());
		Thread.sleep(2500);

		player.stopPlaying();
	}*/
	
	public void testPlayAndReplaceScore() throws InterruptedException {
		JSynPlayer player = new JSynPlayer();
		
		Denotator score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80,60,1.5,0,0},{1,90,50,1,0,0}});
		player.play(new DenotatorValueExtractor(score).getJSynScore());
		for (int i = 1; i < 20; i++) {
			Thread.sleep(100);
			score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80-i,60,1.5,0,0},{1,90-(2*i),50,1,0,0}});
			player.replaceScore(new DenotatorValueExtractor(score).getJSynScore());
		}
		Thread.sleep(1000);

		player.stopPlaying();
	}
	
	/*public void testGetLimitedValue() throws InterruptedException {
		JSynPlayer player = new JSynPlayer();
		double[][] values = new double[20][];
		for (int i = 0; i < values.length; i++) {
			values[i] = new double[]{i,80-i,60,5,0,0};
		}
		
		//values = new double[][]{{10,80,60,20,0},{11,82,60,30,0},{13,83,60,1,0}};
		
		player.play(new DenotatorValueExtractor(new SoundNoteGenerator().createFlatSoundScore(values)).getJSynScore());
		Thread.sleep(500);
		player.stopPlaying();
	}
	
	public void testPlaySingleNote() throws InterruptedException {
		JSynPlayer player = new JSynPlayer();
		player.play(new DenotatorValueExtractor(this.objects.generator.createNodeDenotator(this.objects.NOTE0_VALUES)).getJSynScore());
		Thread.sleep(500);
	}
	
	public void testPlayScore() throws InterruptedException {
		JSynPlayer player = new JSynPlayer();
		player.play(new DenotatorValueExtractor(this.objects.score.getComposition()).getJSynScore());
		Thread.sleep(5000);
	}*/
}
