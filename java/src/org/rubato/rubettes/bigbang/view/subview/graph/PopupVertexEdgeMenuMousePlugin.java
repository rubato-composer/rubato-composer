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
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.view.controller.general.RemoveOperationAction;

/**
 * A GraphMousePlugin that brings up distinct popup menus when an edge or vertex is
 * appropriately clicked in a graph.  If these menus contain components that implement
 * either the EdgeMenuListener or VertexMenuListener then the corresponding interface
 * methods will be called prior to the display of the menus (so that they can display
 * context sensitive information for the edge or vertex).
 * @author Dr. Greg M. Bernstein
 */
public class PopupVertexEdgeMenuMousePlugin extends AbstractPopupGraphMousePlugin {

	private BigBangController controller;
	private JPopupMenu edgePopup;
	private JMenuItem setDurationItem;
	private JMenuItem removeEdgeItem;
	
	public PopupVertexEdgeMenuMousePlugin(BigBangController controller) {
		this.controller = controller;
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
		VisualizationViewer<Integer,AbstractOperationEdit> viewer =
    		(VisualizationViewer<Integer,AbstractOperationEdit>)event.getSource();
    	Point2D point = event.getPoint();
        
        GraphElementAccessor<Integer,AbstractOperationEdit> pickSupport = viewer.getPickSupport();
        if (pickSupport != null) {
        	AbstractOperationEdit edge = pickSupport.getEdge(viewer.getGraphLayout(), point.getX(), point.getY());
        	if (edge != null) {
        		this.updateEdgeMenu(edge, viewer, point);
        		this.edgePopup.show(viewer, event.getX(), event.getY());
        	}
        }
	}
	
	private void updateEdgeMenu(final AbstractOperationEdit edge, final VisualizationViewer<Integer,AbstractOperationEdit> viewer, final Point2D point) {
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
					edge.setDuration(duration);
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