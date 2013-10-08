package org.rubato.rubettes.bigbang.view.subview.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.AnimateButtonAction;
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
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;

public class JGraphPanel extends JPanel implements View, ActionListener {
	
	private ViewController controller;
	private FRLayout2<Integer,AbstractOperationEdit> layout;
	private VisualizationViewer<Integer,AbstractOperationEdit> graphViewer;
	private EditingModalGraphMouse<Integer,AbstractOperationEdit> graphMouse;
	private JPanel northPanel;
	private JButton animateButton;
	private JComboBox modeSelektor;
	private JLabel statusBar;
	private Integer pickedState;
	private AbstractOperationEdit pickedOperation;
	
	public JGraphPanel(ViewController controller, BigBangController bbController) {
		controller.addView(this);
		bbController.addView(this);
		this.controller = controller;
		this.animateButton = new JButton(new AnimateButtonAction(bbController));
		this.modeSelektor = new JComboBox(new Mode[]{Mode.TRANSFORMING,Mode.PICKING,Mode.EDITING});
		this.modeSelektor.addActionListener(this);
		this.northPanel = new JPanel(new GridLayout(2,1));
		this.northPanel.add(this.animateButton);
		this.northPanel.add(this.modeSelektor);
		this.statusBar = new JLabel();
	}
	
	private void initLayoutAndViewer() {
		//init layout and viewer
		this.layout = new FRLayout2<Integer,AbstractOperationEdit>(new DirectedSparseGraph<Integer,AbstractOperationEdit>());
		this.layout.setSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT-40));
		//this.layout.setForceMultiplier(.02);
		//this.layout.setRepulsionRange(30);
		//this.layout.setAttractionMultiplier(1);
		Relaxer relaxer = new VisRunner(this.layout);
		relaxer.stop();
		relaxer.prerelax();
		//Layout<Integer,AbstractOperationEdit> staticLayout = new StaticLayout<Integer,AbstractOperationEdit>(g, this.layout);
		this.graphViewer = new VisualizationViewer<Integer,AbstractOperationEdit>(this.layout);
		this.graphViewer.setMinimumSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT-40));
		//this.graphViewer.getModel().getRelaxer().setSleepTime(10);
		
		//init labels
		this.graphViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		this.graphViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<AbstractOperationEdit>());
		this.graphViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		//init mouse commands
		VertexFactory vf = new VertexFactory(this.layout);
		EdgeFactory ef = new EdgeFactory(this.layout);
		this.graphMouse = new EditingModalGraphMouse<Integer,AbstractOperationEdit>(this.graphViewer.getRenderContext(), vf, ef);
		this.setMode(Mode.PICKING);
		
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
		//graphMouse.get
        graphMouse.remove(graphMouse.getPopupEditingPlugin());  // Removes the existing popup editing plugin
        graphMouse.remove(graphMouse.getEditingPlugin()); // strange editing plugin that relocates last edge...
        graphMouse.add(myPlugin);   // Add our new plugin to the mouse
        
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
		
		this.setPreferredSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT));
		this.setLayout(new BorderLayout());
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.graphViewer, BorderLayout.CENTER);
		this.add(this.statusBar, BorderLayout.SOUTH);
	}
	
	private void setMode(Mode mode) {
		this.graphMouse.setMode(mode);
		if (!this.modeSelektor.getSelectedItem().equals(mode)) {
			this.modeSelektor.setSelectedItem(mode);
		}
	}
	
	private void updateGraph(Graph<Integer,AbstractOperationEdit> graph) {
		if (graph != null) {
			if (this.layout == null) {
				this.initLayoutAndViewer();
			}
			this.layout.setGraph(graph);
			//this.layout.initialize();
			Relaxer relaxer = new VisRunner(this.layout);
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
			this.updateStatusBar();
		} else if (propertyName.equals(ViewController.SELECT_OPERATION)) {
			//TODO never called except for deselection and wpdim...
			AbstractOperationEdit transformation = (AbstractOperationEdit)event.getNewValue();
			this.selectOperation(transformation);
		} else if (propertyName.equals(BigBangController.TOGGLE_GRAPH_ANIMATION)) {
			this.animateButton.setSelected((Boolean)event.getNewValue());
		}	
		else if (propertyName.equals(ViewController.SELECT_COMPOSITION_STATE)) {
			this.selectState((Integer)event.getNewValue());
		} else if (propertyName.equals(ViewController.DESELECT_COMPOSITION_STATES)) {
			this.selectState(null);
		}
	}
	
	private void selectOperation(AbstractOperationEdit operation) {
		if (operation != null) {
			if (this.pickedOperation == null || !this.pickedOperation.equals(operation)) {
				if (this.pickedOperation != null) {
					this.graphViewer.getPickedEdgeState().pick(this.pickedOperation, false);
				}
				this.graphViewer.getPickedEdgeState().pick(operation, true);
				this.pickedOperation = operation;
			}
		} else {
			if (this.graphViewer != null) {
				this.graphViewer.getPickedEdgeState().clear();
			}
			this.pickedOperation = null;
		}
		this.updateStatusBar();
	}
	
	private void selectState(Integer state) {
		if (state != null) {
			if (this.pickedState != state) {
				if (this.pickedState != null) {
					this.graphViewer.getPickedVertexState().pick(this.pickedState, false);
				}
				this.graphViewer.getPickedVertexState().pick(state, true);
				this.pickedState = state;
			}
		} else {
			this.graphViewer.getPickedVertexState().clear();
			this.pickedState = null;
		}
		this.updateStatusBar();
	}
	
	private void updateStatusBar() {
		StringBuilder text = new StringBuilder();
		if (this.pickedState != null) {
			text.append("State: " + this.pickedState);
			if (this.pickedOperation != null) {
				text.append(", ");
			}
		}
		if (this.pickedOperation != null) {
			text.append("Operation: " + this.pickedOperation.toString());
		}
		this.statusBar.setText(text.toString());
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.modeSelektor)) {
			this.setMode((Mode)this.modeSelektor.getSelectedItem());
		}
		
	}

}