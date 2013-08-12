package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;

public class JSynThreadGroup extends ArrayList<JSynThread> {
	
	private boolean playInNextLoop;
	private boolean isRunning;
	
	public JSynThreadGroup(boolean playInNextLoop) {
		this.playInNextLoop = playInNextLoop;
		this.isRunning = false;
	}
	
	public boolean add(JSynThread thread) {
		boolean added = super.add(thread);
		thread.setGroup(this);
		thread.setPlayInNextLoop(this.playInNextLoop);
		if (this.isRunning) {
			thread.start();
		}
		return added;
	}
	
	public synchronized void start() {
		//System.out.println("start " +this);
		this.isRunning = true;
		for (JSynThread currentThread : this) {
			if (currentThread.getState().equals(Thread.State.NEW)) {
				currentThread.start();
			}
		}
	}
	
	public boolean isRunning() {
		//System.out.println("isRunning " +this);
		return this.isRunning;
	}
	
	public void stop() {
		//System.out.println("stop " +this);
		this.isRunning = false;
		this.interrupt();
	}
	
	public void interrupt() {
		//System.out.println("interrupt " + this);
		for (JSynThread currentThread : this) {
			currentThread.interrupt();
		}
	}

}
