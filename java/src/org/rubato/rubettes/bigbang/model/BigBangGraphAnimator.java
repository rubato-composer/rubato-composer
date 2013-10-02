package org.rubato.rubettes.bigbang.model;

import java.util.List;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

public class BigBangGraphAnimator extends Thread {
	
	private final int FRAME_LENGTH = 50;
	private final int MILISECONDS_PER_EDGE = 1000; 
	
	private BigBangTransformationGraph graph;
	private UndoRedoModel model;
	
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
	
	private void animate() throws InterruptedException {
		List<AbstractOperationEdit> shortestPath = this.graph.getCurrentShortestPath();
		int stepsPerEdge = MILISECONDS_PER_EDGE/FRAME_LENGTH;
		System.out.println(shortestPath);
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
	}

}
