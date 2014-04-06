package org.rubato.rubettes.bigbang.test;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.JSynObject;
import org.rubato.rubettes.bigbang.view.player.JSynScore;
import org.rubato.rubettes.bigbang.view.player.SmoothOscillator;
import org.rubato.rubettes.util.SoundNoteGenerator;

import com.jsyn.unitgen.LineOut;

import junit.framework.TestCase;


public class JSynPlayerTest extends TestCase {
	
	TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testSmoothOscillatorOffline() {
		//create player and line out
		BigBangPlayer player = new BigBangPlayer(false);
		player.startSynth();
		LineOut lineOut = new LineOut();
		player.addToSynth(lineOut);
		//lineOut.start();
		
		SmoothOscillator carrier = new SmoothOscillator(player, lineOut.input);
		
		//TODO TEST WITH FREQUENCIES!!!!
		
		//test with additive synthesis
		carrier.addSatellite(JSynObject.ADDITIVE);
		carrier.addSatellite(JSynObject.ADDITIVE);
		TestCase.assertEquals(2, carrier.getSatellites().size());
		
		//test with frequency modulation
		carrier.setSatelliteType(1, JSynObject.FREQUENCY_MODULATION);
		carrier.addSatellite(JSynObject.FREQUENCY_MODULATION);
		TestCase.assertEquals(3, carrier.getSatellites().size());
		
		//test with ring modulation
		carrier.setSatelliteType(2, JSynObject.RING_MODULATION);
		TestCase.assertEquals(3, carrier.getSatellites().size());
		carrier.removeLastSatellite();
		carrier.removeLastSatellite();
		carrier.removeLastSatellite();
		TestCase.assertEquals(0, carrier.getSatellites().size());
		
		//lineOut.stop();
	}
	
	public void testPlayerWithSoundScore() throws InterruptedException {
		//TODO test with both modulators and normal satellites
		BigBangPlayer player = new BigBangPlayer(false);
		player.startSynth();
		LineOut lineOut = new LineOut();
		player.addToSynth(lineOut);
		lineOut.start();
		SoundNoteGenerator generator = new SoundNoteGenerator();
		
		//create and verify score
		JSynScore testScore = this.extractJSynScore(generator.createMultiLevelSoundScore(new double[][]{{0,60,120,1,0,0},{0,1,-4,0,0,0}}));
		TestCase.assertEquals(1, testScore.getObjects().size());
		TestCase.assertEquals(1, testScore.getObjects().iterator().next().getSatellites().size());
		TestCase.assertEquals(JSynObject.ADDITIVE, testScore.getObjects().iterator().next().getSatellites().get(0).getSatelliteType());
		
		//System.out.println(testScore);
		player.setScore(testScore);
		player.togglePlayMode();
		player.getSynth().sleepFor(1.5);
		lineOut.stop();
	}
	
	/*public void testSmoothOscillatorWhilePlaying() throws InterruptedException {
		JSynPlayer player = new JSynPlayer(new BigBangPlayer());
		Synthesizer synth = player.getSynth();
		player.startSynth();
		LineOut lineOut = new LineOut();
		player.addToSynth(lineOut);
		
		FilterStateVariable filter = new FilterStateVariable();
		//filter.amplitude.set(200);
		player.addToSynth(filter);
		filter.start();
		SmoothOscillator carrier = new SmoothOscillator(player, filter.input);
		filter.lowPass.connect(0, lineOut.input, 0);
	 	filter.lowPass.connect(0, lineOut.input, 1);
	 	
	 	carrier.setFrequency(2000);
		carrier.setAmplitude(0.2);
		lineOut.start();
		carrier.queueEnvelope(5, player.getCurrentSynthTime(), true);
		
		carrier.addSatellite(JSynObject.FREQUENCY_MODULATION);
		SmoothOscillator modulator = carrier.getSatellites().get(0).getOscillator();
		modulator.setFrequency(5);
		modulator.setAmplitude(1000);
		modulator.queueEnvelope(2, player.getCurrentSynthTime(), true);
		TestCase.assertEquals(1, carrier.getSatellites().size());
		synth.sleepFor(1);
		modulator.setFrequency(10);
		synth.sleepFor(1);
		
		carrier.removeLastSatellite();
		TestCase.assertEquals(0, carrier.getSatellites().size());
		synth.sleepFor(1);
		
		carrier.addSatellite(JSynObject.RING_MODULATION);
		modulator = carrier.getSatellites().get(0).getOscillator();
		modulator.setFrequency(5);
		modulator.setAmplitude(1);
		modulator.queueEnvelope(3, player.getCurrentSynthTime(), true);
		synth.sleepFor(.3);
		carrier.setSatelliteType(0, JSynObject.FREQUENCY_MODULATION);
		System.out.println(carrier);
		modulator.setAmplitude(1000);
		TestCase.assertEquals(1, carrier.getSatellites().size());
		synth.sleepFor(.3);
		modulator.setAmplitude(1);
		carrier.setSatelliteType(0, JSynObject.RING_MODULATION);
		synth.sleepFor(.3);
		carrier.setSatelliteType(0, JSynObject.ADDITIVE);
		modulator.setAmplitude(1);
		synth.sleepFor(.3);
		carrier.setSatelliteType(0, JSynObject.FREQUENCY_MODULATION);
		modulator.setAmplitude(1);
		synth.sleepFor(.3);
		modulator.setFrequency(20);
		//TestCase.assertEquals(2000.0, carrier.getFrequency());
		TestCase.assertEquals(1, carrier.getSatellites().size());
		modulator.queueEnvelope(2, player.getCurrentSynthTime(), true);
		TestCase.assertEquals(1, carrier.getSatellites().size());
		//TestCase.assertEquals(1, carrier.getModulators().get(0).getModulators().size());
		
		synth.sleepFor(1);
		lineOut.stop();
		synth.sleepFor(1);
	}*/
	
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
	
	/*public void testPlayAndReplaceScore() throws InterruptedException {
		COMMENTED OUT BECAUSE TAKES TIME FOR TESTING....
		
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
	}*/
	
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
	
	private JSynScore extractJSynScore(Denotator denotator) {
		BigBangModel model = new BigBangModel(new BigBangController());
		model.setOrAddComposition(denotator);
		new DenotatorValueExtractor(model.getObjects(), model.getComposition());
		return new JSynScore(model.getObjects().getObjectsAt(null), model.getObjects().getBaseForm(), false);
	}
	
}
