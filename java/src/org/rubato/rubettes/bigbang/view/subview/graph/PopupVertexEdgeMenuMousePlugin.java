package org.rubato.rubettes.bigbang.view.subview.graph;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

/**
 * A GraphMousePlugin that brings up distinct popup menus when an edge or vertex is
 * appropriately clicked in a graph.  If these menus contain components that implement
 * either the EdgeMenuListener or VertexMenuListener then the corresponding interface
 * methods will be called prior to the display of the menus (so that they can display
 * context sensitive information for the edge or vertex).
 * @author Dr. Greg M. Bernstein
 */
public class PopupVertexEdgeMenuMousePlugin extends AbstractPopupGraphMousePlugin {

	private JPopupMenu edgePopup;
	
	public PopupVertexEdgeMenuMousePlugin() {
		//super(MouseEvent.BUTTON2);
		this.edgePopup = new JPopupMenu("Operation Menu");
		JMenuItem setDurationItem = new JMenuItem(); 
		setDurationItem.setText("Edit duration");
		this.edgePopup.add(setDurationItem);
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
		JMenuItem setDurationItem = (JMenuItem)this.edgePopup.getComponent(0);
		this.removeAllActionListeners(setDurationItem);
		setDurationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String answer = JOptionPane.showInputDialog(
						SwingUtilities.getRoot(viewer),
	                    "Duration: ",
	                    edge.getPresentationName(),
	                    //TODO: SET PREVIOUS VALUE!!!!!!!
	                    JOptionPane.PLAIN_MESSAGE);
				try {
					double duration = Double.parseDouble(answer);
					edge.setDuration(duration);
				} catch (NumberFormatException err) {
					return;
				};
			}
		});
    }
	
	private void removeAllActionListeners(AbstractButton button) {
		for (ActionListener currentListener : button.getActionListeners()) {
			button.removeActionListener(currentListener);
		}
	}
		
}