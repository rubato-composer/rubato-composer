package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;

public class JSynThreadGroup extends ArrayList<JSynThread> {
	
	private boolean isRunning = false;
	
	public boolean add(JSynThread thread) {
		boolean added = super.add(thread);
		thread.setGroup(this);
		if (this.isRunning) {
			thread.start();
		}
		return added;
	}
	
	public void start() {
		this.isRunning = true;
		for (JSynThread currentThread : this) {
			currentThread.start();
		}
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	public void stop() {
		this.isRunning = false;
		for (JSynThread currentThread : this) {
			currentThread.interrupt();
		}
	}

}
