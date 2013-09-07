package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.GraphListener;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;

public class JGraphPanel extends JPanel implements View {
	
	private ViewController controller;
	private FRLayout2<Integer,AbstractOperationEdit> layout;
	private VisualizationViewer<Integer,AbstractOperationEdit> graphViewer;
	
	public JGraphPanel(ViewController controller, BigBangController bbController) {
		controller.addView(this);
		bbController.addView(this);
		this.controller = controller;
	}
	
	private void initLayoutAndViewer() {
		//init layout and viewer
		this.layout = new FRLayout2<Integer,AbstractOperationEdit>(new DirectedSparseGraph<Integer,AbstractOperationEdit>());
		this.layout.setSize(new Dimension(300-50,JBigBangPanel.CENTER_PANEL_HEIGHT-50));
		//this.layout.setForceMultiplier(.02);
		//this.layout.setRepulsionRange(30);
		this.layout.setAttractionMultiplier(15);
		Relaxer relaxer = new VisRunner(this.layout);
		relaxer.stop();
		relaxer.prerelax();
		//Layout<Integer,AbstractOperationEdit> staticLayout = new StaticLayout<Integer,AbstractOperationEdit>(g, this.layout);
		this.graphViewer = new VisualizationViewer<Integer,AbstractOperationEdit>(this.layout);
		this.graphViewer.setPreferredSize(new Dimension(300-25,JBigBangPanel.CENTER_PANEL_HEIGHT-25));
		this.graphViewer.getModel().getRelaxer().setSleepTime(10);
		
		//init labels
		this.graphViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		this.graphViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<AbstractOperationEdit>());
		this.graphViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		//init mouse commands
		DefaultModalGraphMouse<Integer,AbstractOperationEdit> graphMouse = new DefaultModalGraphMouse<Integer,AbstractOperationEdit>();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		this.graphViewer.setGraphMouse(graphMouse);
		GraphListener graphListener = new GraphListener(controller, this.graphViewer);
		this.graphViewer.getPickedVertexState().addItemListener(graphListener);
		this.graphViewer.getPickedEdgeState().addItemListener(graphListener);
		
		//this.add(this.graphViewer, BorderLayout.CENTER);
		/*this.setPreferredSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		Box horizontalBox = Box.createHorizontalBox(); 
		horizontalBox.add(Box.createHorizontalGlue()); 
		horizontalBox.add(this.graphViewer); 
		horizontalBox.add(Box.createHorizontalGlue()); 
		Box verticalBox = Box.createVerticalBox(); 
		verticalBox.add(Box.createVerticalGlue()); 
		verticalBox.add(horizontalBox); // one inside the other
		verticalBox.add(Box.createVerticalGlue());*/
		//add to center
		this.setPreferredSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT));
		this.setLayout(new GridBagLayout());
		this.add(this.graphViewer, new GridBagConstraints());
	}
	
	private void updateGraph(Graph<Integer,AbstractOperationEdit> graph) {
		if (graph != null) {
			if (this.layout == null) {
				this.initLayoutAndViewer();
			}
			this.layout.setGraph(graph);
			//this.layout.initialize();
			Relaxer relaxer = new VisRunner(layout);
			relaxer.stop();
			relaxer.prerelax();
			StaticLayout<Integer,AbstractOperationEdit> staticLayout = new StaticLayout<Integer,AbstractOperationEdit>(graph, this.layout);
			LayoutTransition<Integer,AbstractOperationEdit> transition = new LayoutTransition<Integer,AbstractOperationEdit>(this.graphViewer, this.graphViewer.getGraphLayout(), staticLayout);
			Animator animator = new Animator(transition);
			animator.start();
			this.graphViewer.getRenderContext().getMultiLayerTransformer().setToIdentity();
			this.graphViewer.repaint();
		}
	}
	
	

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.GRAPH)) {
			Graph<Integer,AbstractOperationEdit> graph = ((Graph<Integer,AbstractOperationEdit>)event.getNewValue());
			this.updateGraph(graph);
		} else if (propertyName.equals(ViewController.SELECT_OPERATION)) {
			//TODO never called except for deselection and wpdim...
			AbstractOperationEdit transformation = (AbstractOperationEdit)event.getNewValue();
			this.selectOperation(transformation);
		} else if (propertyName.equals(ViewController.SELECT_COMPOSITION_STATE)) {
			this.selectVertex((Integer)event.getNewValue());
		} else if (propertyName.equals(ViewController.DESELECT_COMPOSITION_STATES)) {
			this.selectVertex(null);
		}
	}
	
	private void selectOperation(AbstractOperationEdit operation) {
		if (operation != null) {
			this.graphViewer.getPickedEdgeState().pick(operation, true);
		} else {
			this.graphViewer.getPickedEdgeState().clear();
		}
	}
	
	private void selectVertex(Integer vertex) {
		if (vertex != null) {
			this.graphViewer.getPickedVertexState().pick(vertex, true);
		} else {
			this.graphViewer.getPickedVertexState().clear();
		}
	}

}
