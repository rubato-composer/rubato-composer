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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


import sun.awt.geom.Crossings;


public class Polygon2D {

    public int    nPoints;
    public double xPoints[];
    public double yPoints[];
    
    protected Rectangle2D.Double bounds;
    
    public Polygon2D() {
        xPoints = new double[4];
        yPoints = new double[4];
    }

    
    public Polygon2D(double xpoints[], double ypoints[], int npoints) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        this.nPoints = npoints;
        this.xPoints = new double[npoints];
        this.yPoints = new double[npoints];
        System.arraycopy(xpoints, 0, this.xPoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.yPoints, 0, npoints); 
    }


    public void reset() {
        nPoints = 0;
        bounds = null;
    }


    public void invalidate() {
        bounds = null;
    }

    
    public void translate(double deltaX, double deltaY) {
        for (int i = 0; i < nPoints; i++) {
            xPoints[i] += deltaX;
            yPoints[i] += deltaY;
        }
        if (bounds != null) {
            bounds.x += deltaX;
            bounds.y += deltaY;
        }
    }


    void calculateBounds(double xpoints[], double ypoints[], double npoints) {
        double boundsMinX = Double.MAX_VALUE;
        double boundsMinY = Double.MAX_VALUE;
        double boundsMaxX = Double.MIN_VALUE;
        double boundsMaxY = Double.MIN_VALUE;
        
        for (int i = 0; i < npoints; i++) {
            double x = xpoints[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            double y = ypoints[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        bounds = new Rectangle2D.Double(boundsMinX, boundsMinY,
                                        boundsMaxX - boundsMinX,
                                        boundsMaxY - boundsMinY);
    }


    void updateBounds(double x, double y) {
        if (x < bounds.x) {
            bounds.width = bounds.width + (bounds.x - x);
            bounds.x = x;
        }
        else {
            bounds.width = Math.max(bounds.width, x - bounds.x);
            // bounds.x = bounds.x;
        }
        
        if (y < bounds.y) {
            bounds.height = bounds.height + (bounds.y - y);
            bounds.y = y;
        }
        else {
            bounds.height = Math.max(bounds.height, y - bounds.y);
            // bounds.y = bounds.y;
        }
    }   


    public void addPoint(double x, double y) {
        if (nPoints == xPoints.length) {
            double tmp[];
            
            tmp = new double[nPoints * 2];
            System.arraycopy(xPoints, 0, tmp, 0, nPoints);
            xPoints = tmp;
            
            tmp = new double[nPoints * 2];
            System.arraycopy(yPoints, 0, tmp, 0, nPoints);
            yPoints = tmp;
        }
        xPoints[nPoints] = x;
        yPoints[nPoints] = y;
        nPoints++;
        if (bounds != null) {
            updateBounds(x, y);
        }
    }


    public Rectangle getBounds() {
        return getBoundingBox();
    }


    public Rectangle getBoundingBox() {
        if (nPoints == 0) {
            return new Rectangle();
        }
        if (bounds == null) {
            calculateBounds(xPoints, yPoints, nPoints);
        }
        return bounds.getBounds();
    }


    public boolean contains(Point p) {
        return contains(p.realX, p.realY);
    }


    public boolean contains(double x, double y) {
        if (nPoints <= 2 || !getBoundingBox().contains(x, y)) {
            return false;
        }
        int hits = 0;
        
        double lastx = xPoints[nPoints - 1];
        double lasty = yPoints[nPoints - 1];
        double curx, cury;
        
        // Walk the edges of the polygon
        for (int i = 0; i < nPoints; lastx = curx, lasty = cury, i++) {
            curx = xPoints[i];
            cury = yPoints[i];
            
            if (cury == lasty) {
                continue;
            }
            
            double leftx;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            }
            else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }
            
            double test1, test2;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }
            
            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    private Crossings getCrossings(double xlo, double ylo,
                                   double xhi, double yhi) {
        Crossings cross = new Crossings.EvenOdd(xlo, ylo, xhi, yhi);
        double lastx = xPoints[nPoints - 1];
        double lasty = yPoints[nPoints - 1];
        double curx, cury;
        
        // Walk the edges of the polygon
        for (int i = 0; i < nPoints; i++) {
            curx = xPoints[i];
            cury = yPoints[i];
            if (cross.accumulateLine(lastx, lasty, curx, cury)) {
                return null;
            }
            lastx = curx;
            lasty = cury;
        }
        
        return cross;
    }


    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }


    public boolean intersects(double x, double y, double w, double h) {
        if (nPoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
            return false;
        }
        
        Crossings cross = getCrossings(x, y, x+w, y+h);
        return (cross == null || !cross.isEmpty());
    }

    
    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }


    public boolean contains(double x, double y, double w, double h) {
        if (nPoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
            return false;
        }
        
        Crossings cross = getCrossings(x, y, x+w, y+h);
        return (cross != null && cross.covers(y, y+h));
    }


    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
}
