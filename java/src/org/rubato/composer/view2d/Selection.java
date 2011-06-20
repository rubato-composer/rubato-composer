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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Selection {

    public Selection(WindowConfig config) {
        this.config = config;
        points = new ArrayList<Point>();
    }
    
    
    public Rectangle getBounds() {
        int x0 = Integer.MAX_VALUE;
        int y0 = Integer.MAX_VALUE;
        int x1 = Integer.MIN_VALUE;
        int y1 = Integer.MIN_VALUE;
        for (Point pt : points) {
            x0 = Math.min(pt.screenX, x0);
            y0 = Math.min(pt.screenY, y0);
            x1 = Math.max(pt.screenX, x1);
            y1 = Math.max(pt.screenY, y1);
        }
        bounds.x = x0-10;
        bounds.y = y0-10;
        bounds.width = x1-x0+20;
        bounds.height = y1-y0+20;
        return bounds;
    }
    
    
    public int getPointCount() {
        return points.size();
    }
    
    
    public List<Point> getPoints() {
        return points;
    }
    
    
    public void addScreenPoint(int x, int y) {
        double realX = config.screenToWorldX(x);
        double realY = config.screenToWorldY(y);
        addRealPoint(realX, realY);
    }
    
    
    public void addRealPoint(double x, double y) {
        Point pt = new SelectionPoint(x, y);
        pt.setColor(POINT_COLOR);
        pt.setSize(POINT_SIZE);        
        points.add(pt);
        pt.recalcScreenCoords();
    }
    
    
    public boolean removeScreenPoint(int x, int y) {
        int i = 0;
        for (Point pt : points) {
            if (Math.abs(pt.screenX-x) <= POINT_SIZE/2 &&
                Math.abs(pt.screenY-y) <= POINT_SIZE/2) {
                points.remove(i);
                recalcScreenCoords();
                return true;
            }
            i++;
        }
        return false;
    }
    
    
    public void moveSelectedPoint(int screenX, int screenY) {
        if (selectedPoint >= 0 && selectedPoint < points.size()) {
            Point pt = points.get(selectedPoint);
            pt.screenX = screenX;
            pt.screenY = screenY;
            pt.recalcWorldCoords();
        }
        recalcScreenCoords();
    }
    
    
    public void translate(int screenDx, int screenDy) {
        for (Point pt : points) {
            pt.screenX += screenDx;
            pt.screenY += screenDy;
            pt.recalcWorldCoords();
        }
        recalcScreenCoords();
    }
    
    
    public boolean selectPointNear(int x, int y) {
        int i = 0;
        for (Point pt : points) {
            if (Math.abs(pt.screenX-x) <= POINT_SIZE/2 &&
                Math.abs(pt.screenY-y) <= POINT_SIZE/2) {
                selectedPoint = i;
                return true;
            }
            i++;
        }
        return false;
    }

    
    public void recalcScreenCoords() {
        polygon = new Polygon();
        for (Point pt : points) {
            pt.recalcScreenCoords();
            polygon.addPoint(pt.screenX, pt.screenY);
        }
        // if polygon has at least 3 points, close it
        if (polygon.npoints > 2) {
            polygon.addPoint(polygon.xpoints[0], polygon.ypoints[0]);
        }
    }

    
    public void refresh() {
        selection = new Polygon2D();
        for (Point pt : points) {
            selection.addPoint(pt.realX, pt.realY);
        }
        if (selection.nPoints > 2) {
            selection.addPoint(selection.xPoints[0], selection.yPoints[0]);
        }
    }
    
    
    public boolean contains(Point pt) {
        return selection.contains(pt);
    }
    
    
    public boolean contains(int screenX, int screenY) {
        return selection.contains(config.screenToWorldX(screenX),
                                  config.screenToWorldY(screenY));
    }
    
    
    public void select(boolean s) {
        selected = s;
    }
    
    
    public void drawBackground(Graphics2D g) {
        if (polygon.npoints > 0) {
            if (selected) {
                g.setColor(SELECTED_BG_COLOR);
                g.fillPolygon(polygon);
                g.setColor(SELECTED_FG_COLOR);
                g.drawPolygon(polygon);
            }
            else {
                g.setColor(BG_COLOR);
                g.fillPolygon(polygon);
                g.setColor(FG_COLOR);
                g.drawPolygon(polygon);
            }
        }
    }

    
    public void drawPoints(Graphics2D g) {
        for (Point pt : points) {
            pt.draw(g);
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("Selection[");
        for (Point pt : points) {
            buf.append(pt);
        }
        buf.append("]");
        return buf.toString();
    }
    
    
    private class SelectionPoint extends Point {
        public SelectionPoint(double x, double y) {
            super(x, y, config);
            this.realX = x;
            this.realY = y;
        }
        public void draw(Graphics2D g) {
            g.setColor(POINT_COLOR);
            g.fillRect(screenX-POINT_SIZE/2, screenY-POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
        }
    }

    
    private ArrayList<Point> points;
    private Polygon          polygon;
    private Polygon2D        selection;
    protected WindowConfig   config;
    private int              selectedPoint = -1;
    private Rectangle        bounds = new Rectangle(0, 0, 0, 0);
    private boolean          selected = false;
    
    protected final static Color POINT_COLOR = Color.BLACK;
    private final static Color BG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.2f);
    private final static Color FG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.6f);
    private final static Color SELECTED_BG_COLOR = new Color(0.5f, 0.0f, 0.0f, 0.2f);
    private final static Color SELECTED_FG_COLOR = new Color(0.5f, 0.0f, 0.0f, 0.6f);
    private final static int POINT_SIZE = 7;
}
