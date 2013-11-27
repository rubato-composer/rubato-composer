package org.rubato.rubettes.bigbang.model;

import java.util.List;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

public class BigBangGraphAnimator extends Thread {
	
	private final int SLEEP_LENGTH = 10;
	
	private BigBangTransformationGraph graph;
	private List<AbstractOperationEdit> animatedPath;
	private double totalPathTime;
	private UndoRedoModel model;
	private boolean running;
	private double currentPosition;
	
	public BigBangGraphAnimator(BigBangTransformationGraph graph, UndoRedoModel model) {
		this.graph = graph;
		this.animatedPath = this.graph.getCurrentShortestPath();
		this.totalPathTime = this.calculateTotalPathTime();
		this.model = model;
	}
	
	private double calculateTotalPathTime() {
		double totalTime = 0;
		for (AbstractOperationEdit currentEdit : this.animatedPath) {
			totalTime += currentEdit.getDuration();
		}
		return totalTime;
	}
	
	/**
	 * @param position between 0 and 1
	 */
	public void setPosition(double position) {
		this.currentPosition = position*this.totalPathTime;
		this.updateAnimationToPosition();
	}
	
	public double getPosition() {
		return this.currentPosition/this.totalPathTime;
	}
	
	public void run() {
		try {
			this.animate();
		} catch (InterruptedException e) {
			//this.run();
		}
	}
	
	public void end() {
		this.running = false;
	}
	
	private void updateAnimationToPosition() {
		double currentTime = 0;
		for (AbstractOperationEdit currentEdit : this.animatedPath) {
			if (currentTime < this.currentPosition) {
				//all edits after to 1
				if (currentTime + currentEdit.getDuration() < this.currentPosition) {
					currentEdit.modify(1);
				//the edit during which the current position occurs to the respective value
				} else {
					double modValue = (this.currentPosition-currentTime)/currentEdit.getDuration();
					currentEdit.modify(modValue);
					this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, currentEdit);
				}
			//all edits before to 0
			} else {
				currentEdit.modify(0);
			}
			currentTime += currentEdit.getDuration();
		}
		this.model.firePropertyChange(BigBangController.GRAPH_ANIMATION_POSITION, null, this.getPosition());
		this.graph.updateComposition(true);
	}
	
	private void animate() throws InterruptedException {
		this.running = true;
		this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, true);
		
		double previousTime = System.currentTimeMillis();
		while (this.currentPosition < this.totalPathTime) {
			if (!this.running) {
				this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, false);
				return;
			}
			double currentTime = System.currentTimeMillis();
			double timeDifferenceInSeconds = (currentTime-previousTime)/1000;
			previousTime = currentTime;
			this.currentPosition += timeDifferenceInSeconds;
			this.updateAnimationToPosition();
			Thread.sleep(this.SLEEP_LENGTH);
		}
		
		this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, null);
		this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, false);
	}

}
