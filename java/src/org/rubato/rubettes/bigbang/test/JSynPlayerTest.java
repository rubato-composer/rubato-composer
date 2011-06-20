package org.rubato.rubettes.bigbang.test;

import org.rubato.rubettes.bigbang.model.player.BigBangPlayer;

import junit.framework.TestCase;


public class JSynPlayerTest extends TestCase {
	
	TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testGetLimitedValue() {
		/*JSynPlayer player = new JSynPlayer();
		double[][] values = new double[20][];
		for (int i = 0; i < values.length; i++) {
			values[i] = new double[]{i,80-i,60,5,0};
		}
		
		values = new double[][]{{10,80,60,20,0},{11,82,60,30,0},{13,83,60,1,0}};
		
		player.play(new JSynScore(values, 1000));
		Thread.sleep(500);
		player.stop();*/
	}
	
	/*public void testPlaySingleNote() throws InterruptedException {
		BigBangPlayer player = new BigBangPlayer();
		player.playSingleNote(this.objects.generator.createNodeDenotator(this.objects.NOTE0_VALUES));
		Thread.sleep(2000);
	}
	
	public void testPlayScore() throws InterruptedException {
		BigBangPlayer player = new BigBangPlayer();
		player.playComposition(this.objects.score.getComposition());
		Thread.sleep(5000);
	}*/
}
