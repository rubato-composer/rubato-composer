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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;


public final class MoveAction implements Action2D {

    public MoveAction(View2D view) {
        this.view = view;
    }

    
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    
    public void mouseClicked(MouseEvent e) {}

    
    public void mousePressed(MouseEvent e) {
        // move points or selection
        Selection currentSelection = view.getCurrentSelection();
        if (currentSelection != null) {
            if (currentSelection.selectPointNear(e.getX(), e.getY())) {
                movePoint = true;
            }
            else if (currentSelection.contains(e.getX(), e.getY())) {
                movePoint = false;
                lastX = e.getX();
                lastY = e.getY();
            }
        }
    }

    
    public void mouseDragged(MouseEvent e) {
        Selection currentSelection = view.getCurrentSelection();
        if (movePoint) {
            // moving a point of the selection
            if (currentSelection != null) {
                Rectangle clip = new Rectangle(currentSelection.getBounds());
                currentSelection.moveSelectedPoint(e.getX(), e.getY());
                clip.add(currentSelection.getBounds());
                view.updatePointer(view.getModel().getWindowConfig().screenToWorldX(e.getX()),
                        view.getModel().getWindowConfig().screenToWorldY(e.getY()));
                view.repaint(clip);
            }
        }
        else {
            // moving the selection itself
            if (currentSelection != null) {
                Rectangle clip = new Rectangle(currentSelection.getBounds());
                int dx = e.getX()-lastX;
                int dy = e.getY()-lastY;
                currentSelection.translate(dx, dy);
                lastX = e.getX();
                lastY = e.getY();
                clip.add(currentSelection.getBounds());
                view.repaint(clip);
            }
        }
        
    }
    
    
    public void mouseReleased(MouseEvent e) {}
    
    public void draw(Graphics2D g) {}
   
    
    private final View2D view;
    private boolean movePoint = false;
    private int lastX, lastY;
}
