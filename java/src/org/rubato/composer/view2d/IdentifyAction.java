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

import java.awt.*;
import java.awt.event.MouseEvent;


public final class IdentifyAction implements Action2D {

    public IdentifyAction(View2D view) {
        this.view = view;
    }
    
    
    public Cursor getCursor() {
        return infoCursor;
    }

    
    @SuppressWarnings("all")
    public void mouseClicked(MouseEvent e) {}

    
    public void mousePressed(MouseEvent e) {
        identifyPoint(e.getX(), e.getY());
    }

    
    public void mouseDragged(MouseEvent e) {
        identifyPoint(e.getX(), e.getY());
    }

    
    public void mouseReleased(MouseEvent e) {
        if (lastPoint != null) {
            // remove identifying cross and stop identifying
            View2DModel model = view.getModel();
            drawIdentifyPoint(lastPoint.screenX, lastPoint.screenY);
            lastPoint = null;
            view.updatePointer(model.screenToWorldX(e.getX()), model.screenToWorldY(e.getY()));
        }
    }

    
    public void draw(Graphics2D g) { /* do nothing */ }

    
    /**
     * Draws a mark to identify the point at the
     * given screen coordinates.
     */
    private void drawIdentifyPoint(int screenX, int screenY) {
        Graphics2D g = (Graphics2D)view.getGraphics();
        g.setColor(Color.BLACK);
        g.setXORMode(Color.WHITE);
        g.setStroke(dashedStroke);

        g.drawLine(screenX-5, screenY, screenX+5, screenY);
        g.drawLine(screenX, screenY-5, screenX, screenY+5);

        g.drawLine(0, screenY, screenX-5-1, screenY);
        g.drawLine(screenX+5+1, screenY, view.getWidth(), screenY);
        g.drawLine(screenX, 0, screenX, screenY-5-1);
        g.drawLine(screenX, screenY+5+1, screenX, view.getHeight());        
    }

    
    /**
     * Identify the point nearest to the given screen
     * coordinates (of the pointer).
     */
    private void identifyPoint(int screenX, int screenY) {
        // remove previous mark
        if (lastPoint != null) {
            drawIdentifyPoint(lastPoint.screenX, lastPoint.screenY);
        }

        // find point nearest to pointer
        int dist = Integer.MAX_VALUE;
        Point nextPt = null;
        for (Point pt : view.getPoints()) {
            int d = pt.dist2(screenX, screenY);
            if (d < dist) {
                dist = d;
                nextPt = pt;
            }
        }
        
        // draw identifying cross if there exist any point at all
        if (nextPt != null) {
            drawIdentifyPoint(nextPt.screenX, nextPt.screenY);
            lastPoint = nextPt;
            view.updatePointer(nextPt.realX, nextPt.realY);
        }
    }
    
    
    private final View2D view;
    private Point lastPoint  = null;
    private static final Stroke dashedStroke = 
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {9}, 0);

    private final Cursor infoCursor = View2D.loadCursor(IdentifyAction.class, "infocursor.png", 7, 7);
}
