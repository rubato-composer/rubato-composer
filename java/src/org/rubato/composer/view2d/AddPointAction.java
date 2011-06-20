/*
 * Copyright (C) 2006 Gérard Milmeister
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.composer.view2d;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;


public final class AddPointAction implements Action2D {

    public AddPointAction(View2D view) {
        this.view = view;
    }

    
    public Cursor getCursor() {
        return addCursor;
    }

    
    public void mouseClicked(MouseEvent e) {
//        if (dragMode == DragMode.NONE) {
        view.addPointToSelection(e.getX(), e.getY());
//        }
    }

    
    public void mousePressed(MouseEvent e) { /* do nothing */ }

    public void mouseDragged(MouseEvent e) { /* do nothing */ }

    public void mouseReleased(MouseEvent e) { /* do nothing */ }

    public void draw(Graphics2D g) { /* do nothing */ }

    private final View2D view;
    
    private final Cursor addCursor = View2D.loadCursor(AddPointAction.class, "addcursor.png", 7, 7);
}
