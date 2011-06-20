package org.rubato.rubettes.bigbang.model.player;

import java.util.ArrayList;

public class JSynThreadGroup extends ArrayList<JSynThread> {
	
	private boolean isRunning = false;
	
	public boolean add(JSynThread thread) {
		boolean added = super.add(thread);
		thread.setGroup(this);
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
	}

}
