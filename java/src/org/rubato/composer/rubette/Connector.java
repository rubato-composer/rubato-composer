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

package org.rubato.composer.rubette;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Connectors are the the little boxes on rubettes where links
 * come in and go out.
 * There are two types of connectors: input and output.
 * Each connector can be in one of two states, namely
 * connected or unconnected, which is indicated by the
 * use of different colors.
 * 
 * @author Gérard Milmeister
 */
public class Connector {
    
    public final static int CONNECTED   = 0;
    public final static int UNCONNECTED = 1;
    
    public final static int INPUT  = 0;
    public final static int OUTPUT = 1;
    
    public final static String[] stateString = { "INPUT", "OUTPUT" }; //$NON-NLS-1$ //$NON-NLS-2$

    
    /**
     * Creates a new connector.
     * @param jrubette the rubette this connector is part of
     * @param pos      the position of the connector (1..8)
     * @param r        the size of the connector
     * @param state    the initial state of the connector
     * @param type the type of the connector
     */
    public Connector(JRubette jrubette, int pos, Rectangle r, int state, int type) {
        this.pos      = pos;
        this.r        = r;
        this.state    = state;
        this.type     = type;
        this.jrubette = jrubette;
    }
    
    
    public JRubette getJRubette() {
        return jrubette;
    }
    
    
    public int getPos() {
        return pos;
    }
    
    
    public int getState() {
        return state;
    }
    
    public int getType() {
        return type;
    }
    
    
    public void setState(int state) {
        this.state = state;
    }
    
    
    public int getLinkCount() {
        return links.size();
    }
    
    
    public JLink getLink(int i) {
        return links.get(i); 
    }
    
    
    /**
     * Adds a new link to the connector.
     * The start of end position of the link is set
     * according to the type of the connector.
     */
    public boolean addLink(JLink link) {
        if (type == OUTPUT) {
            link.moveSrc(getConnectPoint());
        }
        else {
            link.moveDest(getConnectPoint());
        }
        links.add(link);
        state = CONNECTED;
        return true;
    }
    
    
    public boolean setLink(JLink link) {
        if (type == OUTPUT) {
            links = new ArrayList<JLink>();
            link.moveSrc(getConnectPoint());
            links.add(link);
            return true;
        }
        else {
            return false;
        }
    }
        
    
    public void removeLink(JLink link) {
        links.remove(link);
        if (links.isEmpty()) {
            state = UNCONNECTED;
        }
    }
    
    
    public List<JLink> getLinks() {
        return links;
    }
    
    
    public Point getConnectPoint() {
        Point point = new Point();
        point.x = jrubette.getX()+r.x+r.width/2;
        if (type == INPUT) {
            point.y = jrubette.getY();
        }
        else {
            point.y = jrubette.getY()+jrubette.getHeight();
        }
        return point;
    }
    
    
    public boolean contains(int x, int y) {
        return r.contains(x, y);
    }
    
    
    public void setRectangle(Rectangle r) {
        this.r = r;
    }
    
    
    public void paint(Graphics2D g) {
        g.setColor(stateColor[state]);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);
       
    }
    
    
    public void refresh() {
        if (type == INPUT) {
            for (JLink jlink : links) {
                jlink.moveDest(getConnectPoint());
            }
        }
        else {
            for (JLink jlink : links) {
                jlink.moveSrc(getConnectPoint());
            }
        }
    }
    
    
    public String toString() {
        return "Connector["+jrubette+","+stateString[type]+","+pos+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
    
    
    private JRubette  jrubette;
    private int       pos;
    private Rectangle r;
    private int       state;
    private int       type;
    
    private ArrayList<JLink> links = new ArrayList<JLink>();
    
    private final static Color stateColor[] = new Color[] { Color.BLUE, Color.RED };
}
