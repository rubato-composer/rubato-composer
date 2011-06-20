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

class JSplitArea extends JPanel implements MouseListener {        

    public JSplitArea(int dim) {
        this.dim = dim;
        splits = new int[dim-1];
        for (int i = 0; i < splits.length; i++) { splits[i] = 0; }
        computeBoxWidth();
        addMouseListener(this);
        setOpaque(true);
    }
    
    
    public int[] getSplitLengths() {
        int splitCount = 1;
        for (int i = 0; i < splits.length; i++) {
            if (splits[i] == 1) {
                splitCount++;
            }
        }
        int splitLengths[] = new int[splitCount];
        splitLengths[0] = 1;
        int j = 0;
        int i = 0;
        while (j < splits.length) {
            if (splits[j] == 1) {
                i++;
            }
            splitLengths[i]++;
            j++;
        }
        return splitLengths;
    }

    
    public void setSplitLengths(int[] sl) {
        int k = 0;
        for (int i = 0; i < sl.length-1; i++) {
            k += sl[i];
            splits[k] = 1;
        }
        splits = new int[] { dim };
        fireActionEvent();
    }
    
    
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintBoxes(g2d);
    }
    
    
    private void paintBoxes(Graphics2D g) {
        int x = getBoxesStartX();
        g.setColor(Color.WHITE);
        g.fillRect(x, BORDER, boxWidth*dim, BOX_HEIGHT);
        g.setColor(Color.BLACK);
        for (int i = 0; i < dim; i++) {
            g.drawRect(x+i*boxWidth, BORDER, boxWidth, BOX_HEIGHT);
        }
        g.setColor(Color.RED);
        g.setStroke(stroke);
        g.drawRect(x, BORDER, boxWidth*dim, BOX_HEIGHT);
        for (int i = 0; i < splits.length; i++) {
            if (splits[i] == 1) {
                g.drawLine(x+(i+1)*boxWidth, BORDER, x+(i+1)*boxWidth, BORDER+BOX_HEIGHT);
            }
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {
        int x = e.getX()-getBoxesStartX();
        int p = (int)Math.round(x/(double)boxWidth)-1;
        if (p < 0 || p > dim-2) {
            return;
        }
        splits[p] = 1-splits[p];
        repaint();
        fireActionEvent();
    }

    
    public void mousePressed(MouseEvent e) {}

    
    public void mouseReleased(MouseEvent e) {}

    
    public void mouseEntered(MouseEvent e) {}

    
    public void mouseExited(MouseEvent e) {}
    
    
    public Dimension getPreferredSize() {
        return new Dimension(BORDER+boxWidth*dim+BORDER, BORDER+BOX_HEIGHT+BORDER);
    }
    
    
    public void computeBoxWidth() {
        boxWidth = 200/dim;
        if (boxWidth < BOX_MIN_WIDTH) {
            boxWidth = BOX_MIN_WIDTH;
        }
        else if (boxWidth > BOX_MAX_WIDTH) {
            boxWidth = BOX_MAX_WIDTH;
        }
    }
    
    
    public int getBoxesStartX() {
        return (getWidth()-dim*boxWidth)/2;
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

    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
           
    private int   dim;
    private int[] splits;
    private int   boxWidth;
    
    private final static int BOX_HEIGHT     = 25;
    private final static int BOX_MIN_WIDTH  = 10;
    private final static int BOX_MAX_WIDTH  = BOX_HEIGHT;
    private final static int BORDER         = 15;
    
    private final static Stroke stroke = new BasicStroke(2f);
}