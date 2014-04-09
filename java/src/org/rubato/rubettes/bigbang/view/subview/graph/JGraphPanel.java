package org.rubato.rubettes.bigbang.view.subview.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.AnimationPositionListener;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.AnimateButtonAction;
import org.rubato.rubettes.bigbang.view.controller.general.SplitButtonAction;
import org.rubato.rubettes.bigbang.view.controller.score.GraphListener;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;

public class JGraphPanel extends JPanel implements View, ActionListener {
	
	private final int NORTHPANEL_HEIGHT = 100;
	
	private BigBangController bbController;
	private ViewController controller;
	private FRLayout2<CompositionState,AbstractOperation> layout;
	private VisualizationViewer<CompositionState,AbstractOperation> graphViewer;
	private EditingModalGraphMouse<CompositionState,AbstractOperation> graphMouse;
	private JPanel northPanel;
	private JButton animateButton;
	private JButton splitButton;
	private JSlider animateSlider;
	private JComboBox modeSelektor;
	private JLabel statusBar;
	private CompositionState pickedState;
	private AbstractOperation pickedOperation;
	
	public JGraphPanel(ViewController controller, BigBangController bbController) {
		controller.addView(this);
		bbController.addView(this);
		this.bbController = bbController;
		this.controller = controller;
		this.initNorthPanel();
		this.statusBar = new JLabel();
	}
	
	private void initNorthPanel() {
		this.northPanel = new JPanel(new GridLayout(3,1));
		this.animateButton = new JButton(new AnimateButtonAction(this.bbController));
		this.splitButton = new JButton(new SplitButtonAction(this.bbController));
		this.splitButton.setEnabled(false);
		JPanel buttonPanel = new JPanel(new GridLayout(1,2));
		buttonPanel.add(this.animateButton);
		buttonPanel.add(this.splitButton);
		this.northPanel.add(buttonPanel);
		
		this.animateSlider = new JSlider(0, 10000, 0);
		this.animateSlider.addChangeListener(new AnimationPositionListener(this.bbController));
		this.northPanel.add(this.animateSlider);
		
		this.modeSelektor = new JComboBox(new Mode[]{Mode.TRANSFORMING,Mode.PICKING});
		this.modeSelektor.addActionListener(this);
		this.northPanel.add(this.modeSelektor);
	}
	
	private void setMode(Mode mode) {
		this.graphMouse.setMode(mode);
		if (!this.modeSelektor.getSelectedItem().equals(mode)) {
			this.modeSelektor.setSelectedItem(mode);
		}
	}
	
	private void updateGraph(Graph<CompositionState,AbstractOperation> graph) {
		if (graph != null) {
			if (this.layout == null) {
				this.initLayoutAndViewer();
			}
			this.layout.setGraph(graph);
			//this.layout.initialize();
			Relaxer relaxer = new VisRunner(this.layout);
			relaxer.stop();
			relaxer.prerelax();
			StaticLayout<CompositionState,AbstractOperation> staticLayout = new StaticLayout<CompositionState,AbstractOperation>(graph, this.layout);
			LayoutTransition<CompositionState,AbstractOperation> transition = new LayoutTransition<CompositionState,AbstractOperation>(this.graphViewer, this.graphViewer.getGraphLayout(), staticLayout);
			Animator animator = new Animator(transition);
			animator.start();
			this.graphViewer.getRenderContext().getMultiLayerTransformer().setToIdentity();
			this.graphViewer.repaint();
		}
	}
	
	private void initLayoutAndViewer() {
		//init layout and viewer
		this.layout = new FRLayout2<CompositionState,AbstractOperation>(new DirectedSparseGraph<CompositionState,AbstractOperation>());
		this.layout.setSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT-this.NORTHPANEL_HEIGHT));
		//this.layout.setForceMultiplier(.02);
		//this.layout.setRepulsionRange(30);
		//this.layout.setAttractionMultiplier(1);
		Relaxer relaxer = new VisRunner(this.layout);
		relaxer.stop();
		relaxer.prerelax();
		//Layout<Integer,AbstractOperationEdit> staticLayout = new StaticLayout<Integer,AbstractOperationEdit>(g, this.layout);
		this.graphViewer = new VisualizationViewer<CompositionState,AbstractOperation>(this.layout);
		this.graphViewer.setMinimumSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT-this.NORTHPANEL_HEIGHT));
		//this.graphViewer.getModel().getRelaxer().setSleepTime(10);
		
		//init labels
		this.graphViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<CompositionState>());
		this.graphViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<AbstractOperation>());
		this.graphViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		//init mouse commands
		VertexFactory vf = new VertexFactory(this.layout);
		EdgeFactory ef = new EdgeFactory(this.layout);
		this.graphMouse = new EditingModalGraphMouse<CompositionState,AbstractOperation>(this.graphViewer.getRenderContext(), vf, ef);
		//this.setMode(Mode.PICKING);
		
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin(this.bbController);
		//this.graphMouse.remove(this.graphMouse.get
        this.graphMouse.remove(this.graphMouse.getPopupEditingPlugin());  // Removes the existing popup editing plugin
        //this.graphMouse.remove(this.graphMouse.getAnnotatingPlugin());
        //this.graphMouse.remove(this.graphMouse.getLabelEditingPlugin());
        this.graphMouse.remove(this.graphMouse.getEditingPlugin()); // strange editing plugin that relocates last edge...
        this.graphMouse.add(myPlugin);   // Add our new plugin to the mouse
        
		this.graphViewer.setGraphMouse(this.graphMouse);
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
		
		//strangely, popups appear in picking mode if selected here. exactly what we want!
		this.setMode(Mode.PICKING);
		
		this.setPreferredSize(new Dimension(300,JBigBangPanel.CENTER_PANEL_HEIGHT));
		this.setLayout(new BorderLayout());
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.graphViewer, BorderLayout.CENTER);
		this.add(this.statusBar, BorderLayout.SOUTH);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.GRAPH)) {
			Graph<CompositionState,AbstractOperation> graph = ((Graph<CompositionState,AbstractOperation>)event.getNewValue());
			this.updateGraph(graph);
			this.updateStatusBar();
		} else if (propertyName.equals(ViewController.SELECT_OPERATION)) {
			//TODO never called except for deselection and wpdim...
			AbstractOperation transformation = (AbstractOperation)event.getNewValue();
			this.selectOperation(transformation);
		} else if (propertyName.equals(BigBangController.TOGGLE_GRAPH_ANIMATION)) {
			this.animateButton.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(BigBangController.GRAPH_ANIMATION_POSITION)) {
			this.animateSlider.setValue((int)Math.round((Double)event.getNewValue()*(this.animateSlider.getMaximum()-this.animateSlider.getMinimum())));
		} else if (propertyName.equals(ViewController.SELECT_COMPOSITION_STATE)) {
			this.selectState((CompositionState)event.getNewValue());
		} else if (propertyName.equals(ViewController.DESELECT_COMPOSITION_STATES)) {
			this.selectState(null);
		}
	}
	
	private void selectOperation(AbstractOperation operation) {
		if (operation != null) {
			if (this.pickedOperation == null || !this.pickedOperation.equals(operation)) {
				if (this.pickedOperation != null) {
					this.graphViewer.getPickedEdgeState().pick(this.pickedOperation, false);
				}
				this.graphViewer.getPickedEdgeState().pick(operation, true);
				this.pickedOperation = operation;
				this.splitButton.setEnabled(this.pickedOperation.isSplittable());
			}
		} else {
			if (this.graphViewer != null) {
				this.graphViewer.getPickedEdgeState().clear();
			}
			this.pickedOperation = null;
		}
		this.updateStatusBar();
	}
	
	private void selectState(CompositionState state) {
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
