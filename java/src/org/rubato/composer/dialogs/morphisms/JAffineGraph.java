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
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.rubato.math.arith.Complex;
import org.rubato.math.arith.NumberTheory;
import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.*;
import org.rubato.math.module.*;

class JAffineGraph
        extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener {

    public JAffineGraph(Ring ring) {
        setOpaque(true);
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        this.ring = ring;
        if (ring instanceof ZRing) {
            config = new ZConfiguration();
        }
        else if (ring instanceof QRing) {
            config = new QConfiguration();
        }
        else if (ring instanceof RRing) {
            config = new RConfiguration();
        }
        else if (ring instanceof CRing) {
            config = new RConfiguration();
        }
        else if (ring instanceof ZnRing) {
            config = new ZConfiguration();
        }
        else {
            config = new RConfiguration();
        }
    }

    
    public boolean hasCurrent() {
        return current > -1;
    }
    
    
    public boolean isMoving() {
        return moving;
    }
    
    
    public String getCurrentX() {
        if (current >= 0) {
            return config.getX(current).toString();
        }
        else {
            return ""; //$NON-NLS-1$
        }
    }
    
    
    public String getCurrentY() {
        if (current >= 0) {
            return config.getY(current).toString();
        }
        else {
            return ""; //$NON-NLS-1$
        }
    }
    
    public RMatrix getRMatrix() {
        return ((RConfiguration)config).getRMatrix();
        
    }
    
    
    public double[] getRVector() {
        return ((RConfiguration)config).getRVector();
    }

    
    public ZMatrix getZMatrix() {
        return ((ZConfiguration)config).getZMatrix();
        
    }
    
    
    public int[] getZVector() {
        return ((ZConfiguration)config).getZVector();
    }

    
    public ZnMatrix getZnMatrix() {
        ZMatrix m = ((ZConfiguration)config).getZMatrix();
        ZnMatrix res = new ZnMatrix(2, 2, ((ZnRing)ring).getModulus());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; i < 2; i++) {
                res.set(i, j, m.get(i, j));
            }
        }
        return res;
    }
    
    
    public int[] getZnVector() {
        int n = ((ZnRing)ring).getModulus();
        int[] v = ((ZConfiguration)config).getZVector();
        for (int i = 0; i < 2; i++) {
            v[i] = NumberTheory.mod(v[i], n);
        }
        return v;
    }

    
    public QMatrix getQMatrix() {
        return ((QConfiguration)config).getQMatrix();
        
    }
    
    
    public Rational[] getQVector() {
        return ((QConfiguration)config).getQVector();
    }

    
    public CMatrix getCMatrix() {
        RMatrix m = ((RConfiguration)config).getRMatrix();
        CMatrix res = new CMatrix(2, 2);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; i < 2; i++) {
                res.set(i, j, new Complex(m.get(i, j)));
            }
        }
        return res;
    }
    
    
    public Complex[] getCVector() {
        double[] v = ((RConfiguration)config).getRVector();
        Complex[] res = new Complex[2];
        for (int i = 0; i < 2; i++) {
            res[i] = new Complex(v[i]);
        }
        return res;
    }

    
    public void zoomIn(double f) {
        x_min = x_min*f;
        x_max = x_max*f;
        y_min = y_min*f;
        y_max = y_max*f;
        repaint();
    }
    
    
    public void zoomOut(double f) {
        zoomIn(1/f);
    }

    
    public void setTrafo(double[][] A, double[] b) {
        config.setVertex(0, b[0], b[1]);
        config.setVertex(1, A[0][0]+b[0], A[1][0]+b[1]);
        config.setVertex(2, A[0][1]+b[0], A[1][1]+b[1]);
    }
    
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAxes(g2d);
        drawUnitTriangle(g2d);
        drawTriangle(g2d);
    }
    
    
    private void drawAxes(Graphics2D g) {
        int x0 = worldToScreenX(0.0);
        int y0 = worldToScreenY(0.0);
        g.setColor(axesColor);
        g.drawLine(x0, 0, x0, getHeight());
        g.drawLine(0, y0, getWidth(), y0);
        int x1 = worldToScreenX(1.0);
        int y1 = worldToScreenY(1.0);
        g.drawLine(x0-5, y1, x0+5, y1);
        g.drawLine(x1, y0-5, x1, y0+5);
        double xf = Math.floor(x_min);
        double yf = Math.floor(y_min);
        for (double x = xf; x <= x_max; x += 1.0) {
            for (double y = yf; y <= y_max; y+= 1.0) {
                g.fillRect(worldToScreenX(x), worldToScreenY(y), 2, 2);
            }
        }
    }
    
    
    private void drawUnitTriangle(Graphics2D g) {
        int x0 = worldToScreenX(0.0);
        int y0 = worldToScreenY(0.0);
        int x1 = worldToScreenX(1.0);
        int y1 = worldToScreenY(1.0);
        int[] x = { x0, x1, x0 };
        int[] y = { y0, y0, y1 };
        int[] px = { x[0], x[1], x[1]+x[2]-x[0], x[2] };
        int[] py = { y[0], y[1], y[1]+y[2]-y[0], y[2] };
        g.setColor(unitTriangleColor);
        g.fillPolygon(px, py, px.length);
        for (int i = 0; i < x.length; i++) {
            g.setColor(handleColor[i]);
            g.fillOval(x[i]-2, y[i]-2, 5, 5);
        }
    }
    
    
    private void drawTriangle(Graphics2D g) {
        int[] x = { config.getScreenX(0), config.getScreenX(1), config.getScreenX(2) };  
        int[] y = { config.getScreenY(0), config.getScreenY(1), config.getScreenY(2) };
        double vx1 = config.px[1]-config.px[0];
        double vy1 = config.py[1]-config.py[0];
        double vx2 = config.px[2]-config.px[0];
        double vy2 = config.py[2]-config.py[0];
        if (vx1*vy2-vx2*vy1 > 0) {
            g.setColor(triangleColor);
        }
        else {
            g.setColor(mirrorColor);
        }
        int[] px = { x[0], x[1], x[1]+x[2]-x[0], x[2] };
        int[] py = { y[0], y[1], y[1]+y[2]-y[0], y[2] };
        g.fillPolygon(px, py, px.length);
        for (int i = 0; i < x.length; i++) {
            g.setColor(handleColor[i]);
            g.fillOval(x[i]-3, y[i]-3, 7, 7);
        }
    }
    
    
    private int getNear(int x, int y) {
        for (int i = 0; i < 3; i++) {
            int dx = x-config.getScreenX(i);
            int dy = y-config.getScreenY(i);
            if (dx*dx+dy*dy < 10) {
                return i;
            }
        }
        return -1;
    }
    
    
    private double screenToWorldX(int x) {
        return x_min+(x*(x_max-x_min)/getWidth());
    }
    
    
    private double screenToWorldY(int y) {
        y = (getHeight()-1)-y;
        return y_min+(y*(y_max-y_min)/getHeight());
    }
    
    
    protected int worldToScreenX(double x) {
        return (int)Math.round((x-x_min)*getWidth()/(x_max-x_min));
    }
    

    protected int worldToScreenY(double y) {
        int res = (int)Math.round((y-y_min)*getHeight()/(y_max-y_min));
        res = (getHeight()-1)-res;
        return res;
    }
    

    public void mouseClicked(MouseEvent e) {}


    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            current = getNear(e.getX(), e.getY());
            if (current < 0) {
                if (inShape(e.getX(), e.getY())) {
                    lastX = e.getX();
                    lastY = e.getY();
                    for (int i = 0; i < 3; i++) {
                        lastXp[i] = config.getScreenX(i);
                        lastYp[i] = config.getScreenY(i);
                    }
                    moving = true;
                    fireActionEvent();
                }
            }
            else {
                fireActionEvent();
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON2) {
            panning = true;
            lastX = e.getX();
            lastY = e.getY();
            lastXmin = x_min;
            lastXmax = x_max;
            lastYmin = y_min;
            lastYmax = y_max;
        }
    }


    public void mouseReleased(MouseEvent e) {
        current = -1;
        panning = false;
        moving = false;
    }


    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            zoomOut(1.2);
        }
        else if (e.getWheelRotation() > 0) {
            zoomIn(1.2);
        }
    }


    public void mouseEntered(MouseEvent e) {}


    public void mouseExited(MouseEvent e) {}

    
    public void mouseDragged(MouseEvent e) {
        if (current >= 0) {
            double x = screenToWorldX(e.getX()); 
            double y = screenToWorldY(e.getY());
            config.setVertex(current, x, y);
            fireActionEvent();
            repaint();
        }
        else if (panning) {
            double dx = screenToWorldX(e.getX())-screenToWorldX(lastX);
            double dy = screenToWorldY(e.getY())-screenToWorldY(lastY);
            x_min = lastXmin-dx;
            x_max = lastXmax-dx;
            y_min = lastYmin-dy;
            y_max = lastYmax-dy;
            repaint();
        }
        else if (moving) {
            int dx = e.getX()-lastX;
            int dy = e.getY()-lastY;
            for (int i = 0; i < 3; i++) {
                config.setVertex(i, screenToWorldX(lastXp[i]+dx), screenToWorldY(lastYp[i]+dy)); 
            }
            repaint();
        }
    }

    
    public void mouseMoved(MouseEvent e) {}

    
    public Dimension getPreferredSize() {
        return MINIMUM_SIZE;
    }
    
    
    public Dimension getMinimumSize() {
        return MINIMUM_SIZE;
    }

    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    
    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, ""); //$NON-NLS-1$
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }

    
    private boolean inShape(int x, int y) {
        int[] qx = { config.getScreenX(0), config.getScreenX(1), config.getScreenX(2) };  
        int[] qy = { config.getScreenY(0), config.getScreenY(1), config.getScreenY(2) };
        int[] px = { qx[0], qx[1], qx[1]+qx[2]-qx[0], qx[2] };
        int[] py = { qy[0], qy[1], qy[1]+qy[2]-qy[0], qy[2] };
        Polygon p = new Polygon(px, py, px.length);
        return p.contains(x, y);
    }
    
    
    public abstract class Configuration {
        double[] px = { 0.0, 1.0, 0.0 };
        double[] py = { 0.0, 0.0, 1.0 };
        
        public int getScreenX(int i) {
            return worldToScreenX(px[i]);
        }
        public int getScreenY(int i) {
            return worldToScreenY(py[i]);
        }
        public void setVertex(int i, double x, double y) {
            px[i] = x;
            py[i] = y;
        }
        public abstract Number getX(int i);
        public abstract Number getY(int i);
    }

    public class RConfiguration extends Configuration {
        public RMatrix getRMatrix() {
            double a = px[1]-px[0];
            double c = py[1]-py[0];
            double b = px[2]-px[0];
            double d = py[2]-py[0];
            double[][] v = {{a, b}, {c, d}}; 
            return new RMatrix(v);
        }
        public double[] getRVector() {
            double[] v = { px[0], py[0]};
            return v;
        }
        public Number getX(int i) {
            return Double.valueOf(px[i]);
        }
        public Number getY(int i) {
            return Double.valueOf(py[i]);
        }
    }
    
    
    public class ZConfiguration extends Configuration {
        int[] ipx = { 0, 1, 0 };
        int[] ipy = { 0, 0, 1 };
        public void setVertex(int i, double x, double y) {
            ipx[i] = (int)Math.round(x);
            ipy[i] = (int)Math.round(y);
            super.setVertex(i, ipx[i], ipy[i]);
        }
        public ZMatrix getZMatrix() {
            int a = ipx[1]-ipx[0];
            int c = ipy[1]-ipy[0];
            int b = ipx[2]-ipx[0];
            int d = ipy[2]-ipy[0];
            int[][] v = {{a, b}, {c, d}}; 
            return new ZMatrix(v);
        }
        public int[] getZVector() {
            int[] v = { ipx[0], ipy[0]};
            return v;
        }
        public Number getX(int i) {
            return Integer.valueOf(ipx[i]);
        }
        public Number getY(int i) {
            return Integer.valueOf(ipy[i]);
        }
    }
    
    
    public class QConfiguration extends Configuration {
        Rational[] qpx = { Rational.getZero(), Rational.getOne(), Rational.getZero() };
        Rational[] qpy = { Rational.getZero(), Rational.getZero(), Rational.getOne() };
        public void setVertex(int i, double x, double y) {
            qpx[i] = new Rational(x);
            qpy[i] = new Rational(y);
            super.setVertex(i, qpx[i].doubleValue(), qpy[i].doubleValue());
        }
        public QMatrix getQMatrix() {
            Rational a = qpx[1].difference(qpx[0]);
            Rational c = qpy[1].difference(qpy[0]);
            Rational b = qpx[2].difference(qpx[0]);
            Rational d = qpy[2].difference(qpy[0]);
            Rational[][] v = {{a, b}, {c, d}}; 
            return new QMatrix(v);
        }
        public Rational[] getQVector() {
            Rational[] v = { qpx[0], qpy[0]};
            return v;
        }
        public Number getX(int i) {
            return qpx[i];
        }
        public Number getY(int i) {
            return qpy[i];
        }
    }
    
    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
           
    private double x_min = X_MIN;
    private double x_max = X_MAX;
    private double y_min = Y_MIN;
    private double y_max = Y_MAX;
    
    private Configuration config = null;
    private Ring ring = null;
    
    int     current = -1;
    boolean panning = false;
    boolean moving = false;
    int     lastX;
    int     lastY;
    double  lastXmin;
    double  lastYmin;
    double  lastXmax;
    double  lastYmax;
    int[]   lastXp = { 0, 0, 0 };
    int[]   lastYp = { 0, 0, 0 };

    private static final Dimension MINIMUM_SIZE  = new Dimension(300, 300);
    private static final Color   axesColor = Color.GRAY;
    private static final Color   unitTriangleColor = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    private static final Color   triangleColor = new Color(0.0f, 1.0f, 0.0f, 0.5f);
    private static final Color   mirrorColor = new Color(1.0f, 0.0f, 0.0f, 0.5f);
    private static final Color[] handleColor = new Color[] { Color.BLUE, Color.YELLOW, Color.MAGENTA }; 

    private static final double X_MIN = -1;
    private static final double X_MAX =  3;
    private static final double Y_MIN = -1;
    private static final double Y_MAX =  3;
}
