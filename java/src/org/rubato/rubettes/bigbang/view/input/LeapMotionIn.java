package org.rubato.rubettes.bigbang.view.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.input.leap.DrawingAdapter;
import org.rubato.rubettes.bigbang.view.input.leap.LeapSpace;
import org.rubato.rubettes.bigbang.view.input.leap.LeapTransformAdapter;
import org.rubato.rubettes.bigbang.view.input.leap.SelectionAdapter;
import org.rubato.rubettes.bigbang.view.input.leap.TranslationAdapter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

public class LeapMotionIn extends Listener implements KeyListener {
	
	public enum GestureMode { DRAW, SELECT, TRANSLATE }
	
	private final LeapSpace leapSpace = new LeapSpace();
	
	private LeapTransformAdapter adapter;
	
	private GestureMode currentState = GestureMode.DRAW;
	
	ViewController viewController;
	
	public LeapMotionIn(ViewController viewController) {
		this.viewController = viewController;
		this.adapter = new DrawingAdapter(this.viewController, this.leapSpace);
		System.out.println("Created Leap Motion");
	}
	
	@Override
	public void onInit(Controller controller) {
		System.out.println("Leap Motion initialized. Devices count: " + controller.devices().count());
	}

	public void onFrame(Controller controller) {
		adapter.update(controller);
	}

	@Override
	public void keyPressed(KeyEvent key) {
	}

	@Override
	public void keyReleased(KeyEvent key) {
	}

	@Override
	public void keyTyped(KeyEvent key) {
		int id = key.getID();
		if (id == KeyEvent.KEY_TYPED) {
			char c = key.getKeyChar();
			if (c == 'c') {
				adapter.capture();
			}
			else if (c == 'n') {
				if (this.currentState == GestureMode.DRAW) {
					this.currentState = GestureMode.SELECT;
					adapter = new SelectionAdapter(viewController, leapSpace);
				}
				else if (this.currentState == GestureMode.SELECT) {
					this.currentState = GestureMode.TRANSLATE;
					adapter = new TranslationAdapter(viewController, leapSpace);
				}
				else if (this.currentState == GestureMode.TRANSLATE) {
					this.currentState = GestureMode.DRAW;
					adapter = new DrawingAdapter(viewController, leapSpace);
				}
			}
		}
		
	}
	
}
