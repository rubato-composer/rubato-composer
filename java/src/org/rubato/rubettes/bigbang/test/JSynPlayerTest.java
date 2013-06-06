package org.rubato.rubettes.bigbang.test;

import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.JSynPlayer;
import org.rubato.rubettes.bigbang.view.player.SmoothOscillator;

import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;

import junit.framework.TestCase;


public class JSynPlayerTest extends TestCase {
	
	TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testSmoothOscillator() throws InterruptedException {
		JSynPlayer player = new JSynPlayer(new BigBangPlayer());
		Synthesizer synth = player.getSynth();
		synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
		LineOut lineOut = new LineOut();
		player.addToSynth(lineOut);
		
		SmoothOscillator carrier = new SmoothOscillator(player);
		carrier.getOutput().connect(0, lineOut.input, 0);
	 	carrier.getOutput().connect(0, lineOut.input, 1);
	 	carrier.setFrequency(2000);
		carrier.setAmplitude(0.2);
		
		carrier.addModulator();
		SmoothOscillator modulator = carrier.getModulators().get(0);
		modulator.setFrequency(2);
		modulator.setAmplitude(100);
		
		lineOut.start();
		carrier.queueEnvelope(1, player.getCurrentSynthTime(), true);
		modulator.queueEnvelope(1, player.getCurrentSynthTime(), true);
		synth.sleepFor(0.5);
		carrier.setFrequency(1000);
		carrier.setAmplitude(0.5);
		modulator.setFrequency(10);
		
		synth.sleepFor(0.5);
		lineOut.stop();
		synth.sleepFor(0.5);
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
		/*COMMENTED OUT BECAUSE TAKES TIME FOR TESTING....
		 *
		JSynPlayer player = new JSynPlayer();
		
		Denotator score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80,60,1.5,0,0},{1,90,50,1,0,0}});
		player.play(new DenotatorValueExtractor(score).getJSynScore());
		for (int i = 1; i < 20; i++) {
			Thread.sleep(100);
			score = new SoundNoteGenerator().createFlatSoundScore(new double[][]{{0,80-i,60,1.5,0,0},{1,90-(2*i),50,1,0,0}});
			player.replaceScore(new DenotatorValueExtractor(score).getJSynScore());
		}
		Thread.sleep(1000);

		player.stopPlaying();*/
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
