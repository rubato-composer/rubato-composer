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

package org.rubato.rubettes.builtin.address;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Rational;
import org.rubato.math.module.*;

/**
 * 
 * @author Gérard Milmeister
 */
class JGraphSelect
        extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener {

    public JGraphSelect(Ring ring, List<ModuleElement> elements) {
        setOpaque(true);
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        if (ring instanceof ZRing) {
            config = new ZConfiguration();
            for (ModuleElement m : elements) {
                int[] p = ((ZProperFreeElement)m).getValue();
                config.addPoint(p[0], p[1]);
            }
        }
        else if (ring instanceof QRing) {
            QConfiguration qconfig = new QConfiguration();
            config = qconfig;
            for (ModuleElement m : elements) {
                Rational[] p = ((QProperFreeElement)m).getValue();
                qconfig.addPoint(p[0], p[1]);
            }
        }
        else if (ring instanceof RRing) {
            config = new RConfiguration();
            for (ModuleElement m : elements) {
                double[] p = ((RProperFreeElement)m).getValue();
                config.addPoint(p[0], p[1]);
            }
        }
        else if (ring instanceof CRing) {
            config = new RConfiguration();
            for (ModuleElement m : elements) {
                Complex[] p = ((CProperFreeElement)m).getValue();
                config.addPoint(p[0].abs(), p[1].abs());
            }
        }
        else if (ring instanceof ZnRing) {
            config = new ZConfiguration();
            for (ModuleElement m : elements) {
                int[] p = ((ZProperFreeElement)m).getValue();
                config.addPoint(p[0], p[1]);
            }
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
    
    
    public String getCurrentXString() {
        if (current >= 0) {
            return config.getX(current).toString();
        }
        else {
            return ""; //$NON-NLS-1$
        }
    }
    
    
    public String getCurrentYString() {
        if (current >= 0) {
            return config.getY(current).toString();
        }
        else {
            return ""; //$NON-NLS-1$
        }
    }
    
    
    public Configuration getConfiguration() {
        return config;
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

    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAxes(g2d);
        drawPoints(g2d);
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
    
    
    private void drawPoints(Graphics2D g) {
        if (config.getSize() > 0) {
            int x = config.getScreenX(0);
            int y = config.getScreenY(0);
            int lx = x;
            int ly = y;
            for (int i = 1; i < config.getSize(); i++) {
                x = config.getScreenX(i);
                y = config.getScreenY(i);
                g.setColor(lineColor);
                g.drawLine(lx, ly, x, y);
                g.setColor(pointColor);
                g.fillOval(lx-POINT_SIZE2, ly-POINT_SIZE2, POINT_SIZE, POINT_SIZE);
                lx = x;
                ly = y;
            }
            g.setColor(pointColor);
            g.fillOval(x-POINT_SIZE2, y-POINT_SIZE2, POINT_SIZE, POINT_SIZE);
        }
    }
    
    
    private int getNear(int x, int y) {
        for (int i = 0; i < config.getSize(); i++) {
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
    

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) { //control-click for mac...
            int i = getNear(e.getX(), e.getY());
            if (i >= 0) {
                config.removePoint(i);
                repaint();
                fireActionEvent();
            }
        }
    }


    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            current = getNear(e.getX(), e.getY());
            if (current < 0) {
                double x = screenToWorldX(e.getX()); 
                double y = screenToWorldY(e.getY());
                config.addPoint(x, y);
                current = config.getSize()-1;
                repaint();
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
            config.setPoint(current, x, y);
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

    
    public abstract class Configuration {
        ArrayList<Double> px = new ArrayList<Double>();
        ArrayList<Double> py = new ArrayList<Double>();

        public int getScreenX(int i) {
            return worldToScreenX(px.get(i));
        }
        public int getScreenY(int i) {
            return worldToScreenY(py.get(i));
        }
        public void setPoint(int i, double x, double y) {
            px.set(i, x);
            py.set(i, y);
        }
        public void addPoint(double x, double y) {
            px.add(x);
            py.add(y);
        }
        public void removePoint(int i) {
            px.remove(i);
            py.remove(i);
        }
        public int getSize() {
            return px.size();
        }
        public abstract Number getX(int i);
        public abstract Number getY(int i);
    }

    public class RConfiguration extends Configuration {
        public Number getX(int i) {
            return Double.valueOf(px.get(i));
        }
        public Number getY(int i) {
            return Double.valueOf(py.get(i));
        }
    }
    
    
    public class ZConfiguration extends Configuration {
        ArrayList<Integer> ipx = new ArrayList<Integer>();
        ArrayList<Integer> ipy = new ArrayList<Integer>();
        
        public void setPoint(int i, double x, double y) {
            int ix = (int)Math.round(x);
            int iy = (int)Math.round(y);
            ipx.set(i, ix);
            ipy.set(i, iy);
            super.setPoint(i, ix, iy);
        }
        public void addPoint(double x, double y) {
            int ix = (int)Math.round(x);
            int iy = (int)Math.round(y);
            ipx.add(ix);
            ipy.add(iy);
            super.addPoint(ix, iy);
        }
        public void removePoint(int i) {
            ipx.remove(i);
            ipy.remove(i);
            super.removePoint(i);
        }
        public Number getX(int i) {
            return ipx.get(i);
        }
        public Number getY(int i) {
            return ipy.get(i);
        }
    }
    
    
    public class QConfiguration extends Configuration {
        ArrayList<Rational> qpx = new ArrayList<Rational>();
        ArrayList<Rational> qpy = new ArrayList<Rational>();
        
        public void setPoint(int i, double x, double y) {
            Rational qx = new Rational(x);
            Rational qy = new Rational(y);
            qpx.set(i, qx);
            qpy.set(i, qy);
            super.setPoint(i, qx.doubleValue(), qy.doubleValue());
        }
        public void addPoint(double x, double y) {
            Rational qx = new Rational(x);
            Rational qy = new Rational(y);
            qpx.add(qx);
            qpy.add(qy);
            super.addPoint(qx.doubleValue(), qy.doubleValue());
        }
        public void addPoint(Rational x, Rational y) {
            qpx.add(x);
            qpy.add(y);
            super.addPoint(x.doubleValue(), y.doubleValue());
        }
        public void removePoint(int i) {
            qpx.remove(i);
            qpy.remove(i);
            super.removePoint(i);
        }
        public Number getX(int i) {
            return qpx.get(i);
        }
        public Number getY(int i) {
            return qpy.get(i);
        }
    }
    
    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
           
    private double x_min = X_MIN;
    private double x_max = X_MAX;
    private double y_min = Y_MIN;
    private double y_max = Y_MAX;
    
    private Configuration config = null;
    
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

    private static final Color axesColor  = Color.GRAY;
    private static final Color pointColor = Color.RED;
    private static final Color lineColor  = Color.BLUE;

    private static final double X_MIN = -1;
    private static final double X_MAX =  3;
    private static final double Y_MIN = -1;
    private static final double Y_MAX =  3;
    
    private static final int POINT_SIZE = 7;
    private static final int POINT_SIZE2 = POINT_SIZE/2;
}
