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

package org.rubato.composer.dialogs.morphisms;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import org.rubato.math.matrix.RMatrix;

public class JGeometryView extends JPanel implements ComponentListener {

    public JGeometryView() {
        setOpaque(true);
        addComponentListener(this);
    }

    
    public void setMatrix(RMatrix matrix) {
        double[] p = new double[3];

        p[0] = 0.0; p[1] = 0.0; p[2] = 1.0;
        p = matrix.product(p);
        qx[0] = p[0]; qy[0] = p[1];

        p[0] = 1.0; p[1] = 0.0; p[2] = 1.0;
        p = matrix.product(p);
        qx[1] = p[0]; qy[1] = p[1];
        
        p[0] = 1.0; p[1] = 1.0; p[2] = 1.0;
        p = matrix.product(p);
        qx[2] = p[0]; qy[2] = p[1];
        
        p[0] = 0.0; p[1] = 1.0;
        p = matrix.product(p); p[2] = 1.0;
        qx[3] = p[0]; qy[3] = p[1];
        
        repaint();
    }
    
    
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAll(g2d);
    }
    
    
    private void drawAll(Graphics2D g) {
        computeBounds();
        int x0 = worldToScreenX(0.0);
        int y0 = worldToScreenY(0.0);
        int x1 = worldToScreenX(1.0);
        int y1 = worldToScreenY(1.0);
        g.setColor(Color.LIGHT_GRAY);
        int[] xs = { x0, x1, x1, x0 };
        int[] ys = { y0, y0, y1, y1 };
        g.fillPolygon(xs, ys, xs.length);
        g.setColor(Color.BLACK);
        g.drawPolygon(xs, ys, xs.length);
        g.drawLine(0, y0, getWidth(), y0);
        g.drawLine(x0, 0, x0, getHeight());
        for (int i = 0; i < 4; i++) {
            g.setColor(ovalColors[i]);
            g.fillOval(xs[i]-2, ys[i]-2, 5, 5);
        }
        
        xs = new int[4];
        ys = new int[4];
        for (int i = 0; i < 4; i++) {
            xs[i] = worldToScreenX(qx[i]);
            ys[i] = worldToScreenY(qy[i]);
        }
        double vx1 = xs[1]-xs[0];
        double vy1 = ys[1]-ys[0];
        double vx2 = xs[3]-xs[0];
        double vy2 = ys[3]-ys[0];
        if (vx1*vy2-vx2*vy1 > 0) {
            g.setColor(mirrorColor);
        }
        else {
            g.setColor(rectColor);
        }
        g.fillPolygon(xs, ys, xs.length);
        g.setColor(Color.BLACK);
        g.drawPolygon(xs, ys, xs.length);
        for (int i = 0; i < 4; i++) {
            g.setColor(ovalColors[i]);
            g.fillOval(xs[i]-3, ys[i]-3, 7, 7);
            g.drawOval(xs[i]-3, ys[i]-3, 7, 7);            
        }
    }

    
    private void computeBounds() {
        x_min = 0.0;
        for (int i = 0; i < 4; i++) {
            if (qx[i] < x_min) {
                x_min = qx[i];
            }
        }
        y_min = 0.0;
        for (int i = 0; i < 4; i++) {
            if (qy[i] < y_min) {
                y_min = qy[i];
            }
        }
        x_max = 1.0;
        for (int i = 0; i < 4; i++) {
            if (qx[i] > x_max) {
                x_max = qx[i];
            }
        }
        y_max = 1.0;
        for (int i = 0; i < 4; i++) {
            if (qy[i] > y_max) {
                y_max = qy[i];
            }
        }

        double rx = 0.2*(x_max-x_min);
        double ry = 0.2*(y_max-y_min);
        x_max += rx;
        x_min -= rx;
        y_max += ry;
        y_min -= ry;
        double rw = (x_max-x_min)/(y_max-y_min);
        double rs = getWidth()/(double)getHeight();
        if (rw > rs) {
            double dy = (x_max-x_min)/rs;
            double edy = dy-(y_max-y_min);
            y_min -= edy/2;
            y_max += edy/2;
        }
        else if (rw < rs) {
            double dx = (y_max-y_min)*rs;
            double edx = dx-(x_max-x_min);
            x_min -= edx/2;
            x_max += edx/2;
        }
    }
    
    
    private int worldToScreenX(double x) {
        return (int)Math.round((x-x_min)*getWidth()/(x_max-x_min));
    }
    

    private int worldToScreenY(double y) {
        int res = (int)Math.round((y-y_min)*getHeight()/(y_max-y_min));
        res = (getHeight()-1)-res;
        return res;
    }
    

    public Dimension getPreferredSize() {
        return currentSize;
    }


    public void componentResized(ComponentEvent e) {
        currentSize = getSize();
    }

    public void componentMoved(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}
        
    
    private Dimension PREFERRED_SIZE = new Dimension(150, 170);
    private Dimension currentSize = PREFERRED_SIZE;
    
    private double[] qx = { 0.0, 1.0, 1.0, 0.0 }; 
    private double[] qy = { 0.0, 0.0, 1.0, 1.0 };
    private double x_min, y_min, x_max, y_max;
    
    private final static Color[] ovalColors  = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
    private final static Color   rectColor   = new Color(0.0f, 1.0f, 0.0f, 0.5f);
    private final static Color   mirrorColor = new Color(1.0f, 0.0f, 0.0f, 0.5f);
}
