package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.JToolBar;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.GraphListener;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class JGraphToolBar extends JToolBar implements View {
	
	private Layout<Integer,AbstractOperationEdit> layout;
	private VisualizationViewer<Integer,AbstractOperationEdit> operationGraph;
	
	public JGraphToolBar(ViewController controller, BigBangController bbController) {
		controller.addView(this);
		bbController.addView(this);
		this.layout = new SpringLayout2<Integer,AbstractOperationEdit>(new DirectedSparseGraph<Integer,AbstractOperationEdit>());
		this.layout.setSize(new Dimension(300,300));
		//this.layout.setForceMultiplier(0.01);
		this.operationGraph = new VisualizationViewer<Integer,AbstractOperationEdit>(this.layout);
		this.operationGraph.setPreferredSize(new Dimension(300,300));
		this.operationGraph.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		this.operationGraph.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<AbstractOperationEdit>());
		this.operationGraph.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		DefaultModalGraphMouse<Integer,AbstractOperationEdit> graphMouse = new DefaultModalGraphMouse<Integer,AbstractOperationEdit>();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		this.operationGraph.setGraphMouse(graphMouse);
		GraphListener graphListener = new GraphListener(controller, this.operationGraph);
		this.operationGraph.getPickedVertexState().addItemListener(graphListener);
		this.operationGraph.getPickedEdgeState().addItemListener(graphListener);
		this.add(this.operationGraph);
	}
	
	private void updateGraph(Graph<Integer,AbstractOperationEdit> graph) {
		this.layout.setGraph(graph);
		this.operationGraph.repaint();
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.GRAPH)) {
			Graph<Integer,AbstractOperationEdit> graph = ((Graph<Integer,AbstractOperationEdit>)event.getNewValue());
			this.updateGraph(graph);
		} else if (propertyName.equals(ViewController.SELECT_TRANSFORMATION)) {
			//TODO never called except for deselection...
			AbstractTransformationEdit transformation = (AbstractTransformationEdit)event.getNewValue();
			this.selectTransformation(transformation);
		} else if (propertyName.equals(ViewController.SELECT_COMPOSITION_STATE)) {
			this.selectVertex((Integer)event.getNewValue());
		}
	}
	
	private void selectTransformation(AbstractTransformationEdit transformation) {
		if (transformation != null) {
			this.operationGraph.getPickedEdgeState().pick(transformation, true);
		} else {
			this.operationGraph.getPickedEdgeState().clear();
		}
	}
	
	private void selectVertex(Integer vertex) {
		if (vertex != null) {
			this.operationGraph.getPickedVertexState().pick(vertex, true);
		} else {
			this.operationGraph.getPickedVertexState().clear();
		}
	}

}
