package org.rubato.rubettes.bigbang.view.controller.mode;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MouseInputListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.display.ZoomListener;

//import com.leapmotion.leap.Controller;	
//import com.leapmotion.leap.Listener;

//public class DisplayModeAdapter extends Listener implements MouseInputListener, MouseWheelListener, KeyEventDispatcher {
public class DisplayModeAdapter implements MouseInputListener, MouseWheelListener, KeyEventDispatcher {
	
	private ViewController controller;
	protected List<MouseListener> mouseListeners;
	protected List<MouseInputListener> mouseInputListeners;
	protected List<MouseWheelListener> mouseWheelListeners;
	//protected List<Listener> leapListeners;
	
	//private Controller leapController;
	private int addCount = 0;
	
	public DisplayModeAdapter(ViewController controller) {
		this.controller = controller;
		this.mouseListeners = new ArrayList<MouseListener>();
		this.mouseInputListeners = new ArrayList<MouseInputListener>();
		this.mouseWheelListeners = new ArrayList<MouseWheelListener>();
		//this.leapListeners = new ArrayList<Listener>();
		//this.leapController = new Controller();
		this.activateMouseWheel();
	}
	
	public void addTo(Component component) {
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
		if (addCount == 0) {
			KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			focusManager.addKeyEventDispatcher(this);
			//leapController.addListener(this);
		}
		addCount++;
	}
	
	public void removeFrom(Component component) {
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);
		component.removeMouseWheelListener(this);
		if (addCount <= 1) {
			KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			focusManager.removeKeyEventDispatcher(this);
			//leapController.removeListener(this);
		}
		addCount--;
	}

	public void mouseClicked(MouseEvent event) {
		for (MouseListener currentAdapter: this.mouseListeners) {
			currentAdapter.mouseClicked(event);
		}
		for (MouseListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseClicked(event);
		}
	}

	public void mouseEntered(MouseEvent event) {
		for (MouseListener currentAdapter: this.mouseListeners) {
			currentAdapter.mouseEntered(event);
		}
		for (MouseListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseEntered(event);
		}
	}

	public void mouseExited(MouseEvent event) {
		for (MouseListener currentAdapter: this.mouseListeners) {
			currentAdapter.mouseExited(event);
		}
		for (MouseListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseExited(event);
		}
	}

	public void mousePressed(MouseEvent event) {
		for (MouseListener currentAdapter: this.mouseListeners) {
			currentAdapter.mousePressed(event);
		}
		for (MouseListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mousePressed(event);
		}
	}

	public void mouseReleased(MouseEvent event) {
		for (MouseListener currentAdapter: this.mouseListeners) {
			currentAdapter.mouseReleased(event);
		}
		for (MouseListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseReleased(event);
		}
	}

	public void mouseDragged(MouseEvent event) {
		//do this because of multitouch trackpad
		this.deactivateMouseWheel();
		for (MouseMotionListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseDragged(event);
		}
		this.activateMouseWheel();
	}

	public void mouseMoved(MouseEvent event) {
		for (MouseMotionListener currentAdapter: this.mouseInputListeners) {
			currentAdapter.mouseMoved(event);
		}
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		for (MouseWheelListener currentAdapter: this.mouseWheelListeners) {
			currentAdapter.mouseWheelMoved(event);
		}
	}
	
	/*@Override
	public void onFrame(Controller controller) {
		for (Listener leapListener : this.leapListeners) {
			leapListener.onFrame(controller);
		}
	}*/
	
	protected void activateMouseWheel() {
		this.mouseWheelListeners.add(new ZoomListener(this.controller));
	}
	
	protected void deactivateMouseWheel() {
		if (this.mouseWheelListeners.size() > 0) {
			this.mouseWheelListeners.remove(0);
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent e) {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) 
        {
        	char c = e.getKeyChar();
        	//System.out.println("Pressed test: " + c);
        }
        return false;
    }

}
