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


public final class WindowZoomAction implements Action2D {

    public WindowZoomAction(View2D view) {
        this.view = view;
    }
    
    
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
    }

    
    public void mouseClicked(MouseEvent e) {}

    
    public void mousePressed(MouseEvent e) {
        // begin window zooming
        firstX = lastX = e.getX();
        firstY = lastY = e.getY();
        view.drawZoomRectangle(firstX, firstY, lastX, lastY);
    }

    
    public void mouseDragged(MouseEvent e) {
        view.drawZoomRectangle(firstX, firstY, lastX, lastY);
        lastX = e.getX();
        lastY = e.getY();
        view.drawZoomRectangle(firstX, firstY, lastX, lastY);
    }
    
    
    public void mouseReleased(MouseEvent e) {
        // finish zooming
        view.drawZoomRectangle(firstX, firstY, lastX, lastY);
        View2DModel model = view.getModel();
        double x0 = model.screenToWorldX(firstX > lastX?lastX:firstX);
        double x1 = model.screenToWorldX(firstX > lastX?firstX:lastX);
        double y0 = model.screenToWorldY(lastY > firstY?lastY:firstY);
        double y1 = model.screenToWorldY(lastY > firstY?firstY:lastY);
        // set extent resulting from zooming
        view.setWindow(x0, x1, y0, y1);
    }

    
    public void draw(Graphics2D g) {}

    
    private final View2D view;
    private int firstX, firstY;
    private int lastX, lastY;
}
