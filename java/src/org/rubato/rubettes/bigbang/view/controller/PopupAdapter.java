package org.rubato.rubettes.bigbang.view.controller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPopupMenu;

public class PopupAdapter extends MouseAdapter {
	
	private JBigBangPopupMenu popup;
	
	public PopupAdapter(JBigBangPopupMenu popup) {
		this.popup = popup;
	}

	public void mousePressed(MouseEvent event) {
        this.maybeShowPopup(event);
    }

    public void mouseReleased(MouseEvent event) {
        this.maybeShowPopup(event);
    }

    private void maybeShowPopup(MouseEvent event) {
    	Point location = event.getPoint();
        if (event.isPopupTrigger()) {
        	this.popup.setNoteMode(((JBigBangDisplay)event.getSource()).getContents().getDisplayObjects().hasSelectedNoteAt(location));
            this.popup.show(event.getComponent(), event.getX(), event.getY());
        }
    }
	
}
