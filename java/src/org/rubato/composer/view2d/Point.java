/*
 * Copyright (C) 2005 Gérard Milmeister
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

import java.awt.Color;
import java.awt.Graphics2D;

import org.rubato.math.yoneda.Denotator;


public class Point {
    
    public Point(double x, double y, WindowConfig config) {
        this.realX = x;
        this.realY = y;
        this.config = config;
    }

    
    public void setDenotator(Denotator d) {
        denotator = d;
    }
    
    
    public Denotator getDenotator() {
        return denotator;
    }
    
    
    public void setColor(Color color) {
        ptColor = color;
    }
    
    
    public void setSize(int size) {
        if (size % 2 == 0) { size++; }
        ptSize = size;
        ptSize2 = size/2;
    }
    
    
    public int dist2(int x, int y) {
        int dx = screenX-x;
        int dy = screenY-y;
        return dx*dx+dy*dy;
    }
    
    
    public boolean isOn(int x, int y) {
        int dx = Math.abs(x-screenX);
        int dy = Math.abs(y-screenY);
        return dx*dx+dy*dy <= ptSize2*ptSize2;
    }
    
    
    public void worldMove(double x, double y) {
        this.realX = x;
        this.realY = y;
        recalcScreenCoords();
    }
    
    
    public void screenMove(int x, int y) {
        this.screenX = x;
        this.screenY = y;
        recalcWorldCoords();
    }
    
    
    public void draw(Graphics2D g) {
        g.setColor(ptColor);
        g.fillOval(screenX-ptSize2, screenY-ptSize2, ptSize, ptSize);
    }
    
    
    public void recalcScreenCoords() {
        screenX = config.worldToScreenX(realX);
        screenY = config.worldToScreenY(realY);
    }

    
    public void recalcWorldCoords() {
        realX = config.screenToWorldX(screenX);
        realY = config.screenToWorldY(screenY);
    }
    
    
    @SuppressWarnings("nls")
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("Point[");
        buf.append("realX=");
        buf.append(realX);
        buf.append(",realY=");
        buf.append(realY);
        buf.append(",screenX=");
        buf.append(screenX);
        buf.append(",screenY=");
        buf.append(screenY);
        buf.append("]");
        return buf.toString();
    }

    
    public double realX;
    public double realY;
    
    public int screenX;
    public int screenY;
    
    private WindowConfig config;

    private Denotator denotator = null;
    
    private static final Color POINT_COLOR = Color.RED;
    private Color ptColor = POINT_COLOR;

    private static final int POINT_SIZE = 5;
    private int ptSize = POINT_SIZE;
    private int ptSize2 = POINT_SIZE/2;
}
