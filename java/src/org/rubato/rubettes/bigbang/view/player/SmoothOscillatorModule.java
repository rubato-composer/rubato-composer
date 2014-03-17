package org.rubato.rubettes.bigbang.view.player;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.UnitBinaryOperator;

/**
 * A synthesizer module that can easily be plugged together with others. Input B is where the oscillator is plugged
 * into and input A serves to potentially connect to other modules. Plugging can be done using the insertAfter,
 * insertBetween, and remove methods.
 * 
 * A B
 * |/
 * O
 * 
 * @author florian thalmann
 *
 */
public class SmoothOscillatorModule {
	
	private BigBangPlayer player;
	private UnitBinaryOperator operator;
	private UnitOutputPort thingInInputA;
	private SmoothOscillator oscillatorInInputB;
	private UnitInputPort thingInOutput;
	private int type;
	
	public SmoothOscillatorModule(BigBangPlayer player, int type) {
		this.player = player;
		this.oscillatorInInputB = new SmoothOscillator(this.player, null);
		this.setType(type);
	}
	
	/**
	 * Sets the type of this module and creates or replaces the operator unit if needed.
	 */
	public void setType(int moduleType) {
		if ((moduleType == JSynObject.FREQUENCY_MODULATION || moduleType == JSynObject.ADDITIVE)
				&& (this.operator == null || !(this.operator instanceof Add))) {
			this.setOperatorUnit(new Add());
		} else if (moduleType == JSynObject.RING_MODULATION
				&& (this.operator == null || !(this.operator instanceof Multiply))) {
			this.setOperatorUnit(new Multiply());
		}
		this.type = moduleType;
	}
	
	/*
	 * Replaces the operator unit with a new one and connects everything appropriately.
	 */
	private void setOperatorUnit(UnitBinaryOperator newOperator) {
		//System.out.println(this.thingInInputA + " " + this.oscillatorInInputB + " " + this.thingInOutput);
		if (this.operator != null) {
			this.disconnectOperator();
		}
		if (this.thingInInputA != null) {
			this.thingInInputA.connect(newOperator.inputA);
		}
		this.oscillatorInInputB.getOutput().connect(newOperator.inputB);
		if (this.thingInOutput != null) {
			this.thingInOutput.connect(newOperator.output);
		}
		this.operator = newOperator;
		this.player.addToSynth(newOperator);
	}
	
	/**
	 * Inserts this module after (or before) the given one by disconnecting and reconnecting a and o as shown below.
	 * 
	 * 			a
	 * a b		|
	 * |/	->	A B
	 * o		|/
	 * 			O b
	 * 			|/
	 * 			o
	 * 
	 * @param module any module
	 */
	public void insertAfter(SmoothOscillatorModule module) {
		this.insertBetween(module.getThingInInputA(), module.getThingInOutput());
	}
	
	/**
	 * Disconnects the given out from the in and inserts this module between them. the given out ends up in inputA
	 * and the output of the module goes to the given in.
	 * 
	 * 			i
	 * o		|
	 * |	->	A B
	 * i		|/
	 * 			O
	 * 			|
	 * 			i
	 * 
	 * @param out a unit connected to the given in
	 * @param in a unit connected that the given out is connected to
	 */
	public void insertBetween(UnitOutputPort out, UnitInputPort in) {
		out.disconnect(in);
		out.connect(this.operator.inputA);
		this.operator.output.connect(in);
		this.thingInInputA = out;
		this.thingInOutput = in;
	}
	
	/**
	 * Disconnects this module and reconnects the thing in inputA to the thing in the output. 
	 */
	public void disconnect() {
		this.disconnectOperator();
		if (this.thingInInputA != null && this.thingInOutput != null) {
			this.thingInInputA.connect(this.thingInOutput);
		}
		this.thingInInputA = null;
		this.thingInOutput = null;
	}
	
	/*
	 * Disconnects the operator from all connected units
	 */
	private void disconnectOperator() {
		if (this.thingInOutput != null) {
			this.operator.output.disconnect(this.thingInOutput);
		}
		if (this.thingInInputA != null) {
			this.thingInInputA.disconnect(this.operator.inputA);
		}
		this.oscillatorInInputB.getOutput().disconnect(this.operator.inputB);
	}
	
	public UnitInputPort getThingInOutput() {
		return this.thingInOutput;
	}
	
	public UnitOutputPort getThingInInputA() {
		return this.thingInInputA;
	}
	
	public SmoothOscillator getOscillator() {
		return this.oscillatorInInputB;
	}
	
	public UnitOutputPort getOutput() {
		return this.operator.output;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String toString() {
		return this.type + " " + this.operator + " " + this.thingInInputA + " " + this.oscillatorInInputB + " " + this.thingInOutput;
	}
	
	public void removeFromSynthAndStop() {
		this.disconnect();
		this.player.removeFromSynthAndStop(this.operator);
		this.oscillatorInInputB.removeFromSynthAndStop();
	}

}
