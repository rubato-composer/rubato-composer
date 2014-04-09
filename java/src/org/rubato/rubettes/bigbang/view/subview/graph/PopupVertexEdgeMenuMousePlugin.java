package org.rubato.rubettes.bigbang.view.subview.graph;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.view.controller.general.InsertOperationAction;
import org.rubato.rubettes.bigbang.view.controller.general.RemoveOperationAction;

/**
 * A GraphMousePlugin that brings up distinct popup menus when an edge or vertex is
 * appropriately clicked in a graph.  If these menus contain components that implement
 * either the EdgeMenuListener or VertexMenuListener then the corresponding interface
 * methods will be called prior to the display of the menus (so that they can display
 * context sensitive information for the edge or vertex).
 * @author Dr. Greg M. Bernstein and Florian Thalmann
 */
public class PopupVertexEdgeMenuMousePlugin extends AbstractPopupGraphMousePlugin {

	private BigBangController controller;
	private JPopupMenu vertexPopup;
	private JMenuItem insertEdgeItem;
	private JPopupMenu edgePopup;
	private JMenuItem setDurationItem;
	private JMenuItem removeEdgeItem;
	
	public PopupVertexEdgeMenuMousePlugin(BigBangController controller) {
		this.controller = controller;
		this.initVertexPopup();
		this.initEdgePopup();
	}
	
	private void initVertexPopup() {
		this.vertexPopup = new JPopupMenu("Composition State Menu");
		this.insertEdgeItem = new JMenuItem(); 
		this.insertEdgeItem.setText("Insert operation");
		this.vertexPopup.add(this.insertEdgeItem);
	}
	
	private void initEdgePopup() {
		this.edgePopup = new JPopupMenu("Operation Menu");
		this.setDurationItem = new JMenuItem(); 
		this.setDurationItem.setText("Edit duration");
		this.edgePopup.add(this.setDurationItem);
		this.removeEdgeItem = new JMenuItem(); 
		this.removeEdgeItem.setText("Remove");
		this.edgePopup.add(this.removeEdgeItem);
	}
    
    /**
     * Implementation of the AbstractPopupGraphMousePlugin method. This is where the 
     * work gets done. You shouldn't have to modify unless you really want to...
     * @param e 
     */
	protected void handlePopup(MouseEvent event) {
		VisualizationViewer<CompositionState,AbstractOperation> viewer =
    		(VisualizationViewer<CompositionState,AbstractOperation>)event.getSource();
    	Point2D point = event.getPoint();
        
        GraphElementAccessor<CompositionState,AbstractOperation> pickSupport = viewer.getPickSupport();
        if (pickSupport != null) {
        	CompositionState vertex = pickSupport.getVertex(viewer.getGraphLayout(), point.getX(), point.getY());
        	if (vertex != null) {
        		this.updateVertexMenu(vertex, viewer, point);
        		this.vertexPopup.show(viewer, event.getX(), event.getY());
        	} else {
        		AbstractOperation edge = pickSupport.getEdge(viewer.getGraphLayout(), point.getX(), point.getY());
            	if (edge != null) {
            		this.updateEdgeMenu(edge, viewer, point);
            		this.edgePopup.show(viewer, event.getX(), event.getY());
            	}
        	}
        }
	}
	
	private void updateVertexMenu(final CompositionState vertex, final VisualizationViewer<CompositionState,AbstractOperation> viewer, final Point2D point) {
		this.removeAllActionListeners(this.insertEdgeItem);
		this.insertEdgeItem.addActionListener(new InsertOperationAction(this.controller, vertex));
    }
	
	private void updateEdgeMenu(final AbstractOperation edge, final VisualizationViewer<CompositionState,AbstractOperation> viewer, final Point2D point) {
		this.removeAllActionListeners(this.setDurationItem);
		this.setDurationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String answer = (String)JOptionPane.showInputDialog(
						SwingUtilities.getRoot(viewer),
	                    "Duration: ",
	                    edge.getPresentationName(),
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    edge.getDuration());
				try {
					double duration = Double.parseDouble(answer);
					controller.setOperationDuration(edge, duration);
				} catch (NumberFormatException err) {
					return;
				};
			}
		});
		this.removeAllActionListeners(this.removeEdgeItem);
		this.removeEdgeItem.addActionListener(new RemoveOperationAction(this.controller, edge));
    }
	
	private void removeAllActionListeners(AbstractButton button) {
		for (ActionListener currentListener : button.getActionListeners()) {
			button.removeActionListener(currentListener);
		}
	}
		
}