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

class JShuffleArea extends JPanel implements MouseListener, MouseMotionListener {        

    public JShuffleArea(int l, int r) {
        numleft = l;
        numright = r;
        maxnum = Math.max(l, r);
        left = new int[numleft];
        right = new int[numright];
        for (int i = 0; i < numleft; i++) left[i] = -1;
        for (int i = 0; i < numright; i++) right[i] = -1;
        addMouseListener(this);
        addMouseMotionListener(this);
        setOpaque(true);
    }
    
    
    public int[] getShuffle() {
        return left;
    }
    
    
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintBoxes(g2d);
    }
    
    
    private void paintBoxes(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(BORDER2, BORDER2, getWidth()-2*BORDER2, getHeight()-2*BORDER2);
        g.setColor(Color.BLACK);
        g.drawRect(BORDER2, BORDER2, getWidth()-2*BORDER2, getHeight()-2*BORDER2);
        for (int i = 0; i < numleft; i++) {
            drawLeftBox(g, i);
        }
        for (int i = 0; i < numright; i++) {
            drawRightBox(g, i);
        }
        for (int i = 0; i < numleft; i++) {
            if (left[i] >= 0) {
                drawLine(g, i, left[i]);
            }
        }
        if (lastLeftBox >= 0) {
            g.drawLine(drawX, drawY, lastX, lastY);
        }
    }
    
    
    private void drawLeftBox(Graphics2D g, int i) {
        if (left[i] >= 0) {
            g.setColor(Color.RED);
            g.fillRect(BORDER, BORDER+i*(BOX_SIZE+BOX_GAP), BOX_SIZE, BOX_SIZE);
        }
        g.setColor(Color.BLACK);
        g.drawRect(BORDER, BORDER+i*(BOX_SIZE+BOX_GAP), BOX_SIZE, BOX_SIZE);
    }
     
    
    private void drawRightBox(Graphics2D g, int i) {
        if (right[i] >= 0) {
            g.setColor(Color.RED);
            g.fillRect(getWidth()-BORDER-BOX_SIZE, BORDER+i*(BOX_SIZE+BOX_GAP), BOX_SIZE, BOX_SIZE);
        }
        g.setColor(Color.BLACK);
        g.drawRect(getWidth()-BORDER-BOX_SIZE, BORDER+i*(BOX_SIZE+BOX_GAP), BOX_SIZE, BOX_SIZE);
    }
    
    
    private void drawLine(Graphics2D g, int i, int j) {
        g.drawLine(BORDER+BOX_SIZE, BORDER+i*(BOX_SIZE+BOX_GAP)+BOX_SIZE/2,
                   getWidth()-BORDER-BOX_SIZE, BORDER+j*(BOX_SIZE+BOX_GAP)+BOX_SIZE/2);
    }
    
    
    private int inLeftBox(int x, int y) {
        if (x >= BORDER && x < BORDER+BOX_SIZE) {
            for (int i = 0; i < numleft; i++) {
                if (y >= BORDER+i*(BOX_SIZE+BOX_GAP) &&
                    y < BOX_SIZE+BORDER+i*(BOX_SIZE+BOX_GAP)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    
    private int inRightBox(int x, int y) {
        if (x >= getWidth()-BORDER-BOX_SIZE && x < getWidth()-BORDER) {
            for (int i = 0; i < numright; i++) {
                if (y >= BORDER+i*(BOX_SIZE+BOX_GAP) &&
                    y < BOX_SIZE+BORDER+i*(BOX_SIZE+BOX_GAP)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            int i = inLeftBox(e.getX(), e.getY());
            if (i >= 0) {
                if (left[i] >= 0) {
                    right[left[i]] = -1;
                    left[i] = -1;
                    fireActionEvent();
                    repaint();
                }
            }
            else {
                i = inRightBox(e.getX(), e.getY());
                if (i >= 0) {
                    if (right[i] >= 0) {
                        left[right[i]] = -1;
                        right[i] = -1;
                        fireActionEvent();
                        repaint();
                    }
                }
            }
        }
    }

    
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            lastLeftBox = inLeftBox(e.getX(), e.getY());
            if (lastLeftBox >= 0) {
                drawX = BORDER+BOX_SIZE;
                drawY = BORDER+lastLeftBox*(BOX_SIZE+BOX_GAP)+BOX_SIZE/2;
            }
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        if (lastLeftBox >= 0) {
            int i = inRightBox(e.getX(), e.getY());
            if (i >= 0) {
                if (left[lastLeftBox] >= 0) {
                    right[left[lastLeftBox]] = -1;
                }
                for (int j = 0; j < numleft; j++) {
                    if (left[j] == i) {
                        left[j] = -1;
                    }
                }
                left[lastLeftBox] = i;
                right[i] = lastLeftBox;
                fireActionEvent();
            }
            lastLeftBox = -1;
            repaint();
        }
    }

    
    public void mouseEntered(MouseEvent e) {
        updateToolTip(e.getX(), e.getY());
    }

    
    public void mouseExited(MouseEvent e) {}

    
    public void mouseDragged(MouseEvent e) {
        if (lastLeftBox >= 0) {
            lastX = e.getX();
            lastY = e.getY();
            repaint();
        }
    }

    
    public void mouseMoved(MouseEvent e) {
        updateToolTip(e.getX(), e.getY());
    }
    
    
    private void updateToolTip(int x, int y) {
        int i = inLeftBox(x, y);
        if (i >= 0) {
            setToolTipText("<html>Left component #"+i+ //$NON-NLS-1$
                           "<br>Press mouse button 1 to create link"+ //$NON-NLS-1$
                           "<br>Click mouse button 3 to remove link</html>"); //$NON-NLS-1$
        }
        else {
            i = inRightBox(x, y);
            if (i >= 0) {
                setToolTipText("<html>Right component #"+i+ //$NON-NLS-1$
                               "<br>Click mouse button 3 to remove link</html>"); //$NON-NLS-1$
            }
            else {
                setToolTipText(null);
            }
        }
    }

    
    public Dimension getPreferredSize() {
        return new Dimension(BORDER+BOX_SIZE+GAP+BOX_SIZE+BORDER, 2*BORDER+maxnum*BOX_SIZE+(maxnum-1)*BOX_GAP);
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
           
    private int numleft;
    private int numright;
    private int maxnum;
    private int[] left;
    private int[] right;
    private int drawX;
    private int drawY;
    private int lastX;
    private int lastY;
    private int lastLeftBox = -1;
    
    private final static int BOX_SIZE = 25;
    private final static int BOX_GAP = 20;
    private final static int GAP = 50;
    private final static int BORDER = 30;
    private final static int BORDER2 = BORDER/2;
}