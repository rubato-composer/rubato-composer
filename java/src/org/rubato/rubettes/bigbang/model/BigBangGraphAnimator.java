package org.rubato.rubettes.bigbang.model;

import java.util.List;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

public class BigBangGraphAnimator extends Thread {
	
	private final int SLEEP_LENGTH = 10;
	
	private BigBangTransformationGraph graph;
	private UndoRedoModel model;
	private boolean running;
	
	public BigBangGraphAnimator(BigBangTransformationGraph graph, UndoRedoModel model) {
		this.graph = graph;
		this.model = model;
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
	
	/*private void animate() throws InterruptedException {
		List<AbstractOperationEdit> shortestPath = this.graph.getCurrentShortestPath();
		int stepsPerEdge = MILISECONDS_PER_EDGE/FRAME_LENGTH;
		//System.out.println(shortestPath);
		//first reset edges
		for (AbstractOperationEdit currentEdit : shortestPath) {
			currentEdit.modify(0);
			this.graph.updateComposition(true);
		}
		//then animate
		for (AbstractOperationEdit currentEdit : shortestPath) {
			for (int i = 0; i < stepsPerEdge; i++) {
				System.out.println(currentEdit + " " + i);
				currentEdit.modify(((double)i)/stepsPerEdge);
				this.graph.updateComposition(true);
				//this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, currentEdit);
				Thread.sleep(this.FRAME_LENGTH);
			}
		}
		//this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, null);
	}*/
	
	private void animate() throws InterruptedException {
		this.running = true;
		this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, true);
		List<AbstractOperationEdit> shortestPath = this.graph.getCurrentShortestPath();
		//first reset edges
		for (AbstractOperationEdit currentEdit : shortestPath) {
			currentEdit.modify(0);
			this.graph.updateComposition(true);
		}
		//then animate
		long time = System.currentTimeMillis();
		double nextTime = time;
		for (AbstractOperationEdit currentEdit : shortestPath) {
			if (currentEdit.isAnimatable()) {
				int edgeDuration = (int)Math.round(currentEdit.getDuration()*1000);
				nextTime += edgeDuration;
				while (System.currentTimeMillis() < nextTime) {
					if (!this.running) {
						this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, false);
						return;
					}
					double currentRatio = 1.0-((nextTime-System.currentTimeMillis())/edgeDuration);
					//System.out.println(nextTime + " " + currentRatio);
					currentEdit.modify(currentRatio);
					this.graph.updateComposition(true);
					this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, currentEdit);
					Thread.sleep(this.SLEEP_LENGTH);
				}
				currentEdit.modify(1);
			}
		}
		this.model.firePropertyChange(BigBangController.MODIFY_OPERATION, null, null);
		this.model.firePropertyChange(BigBangController.TOGGLE_GRAPH_ANIMATION, null, false);
	}

}
