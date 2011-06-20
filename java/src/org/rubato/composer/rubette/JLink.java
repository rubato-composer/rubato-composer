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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class JLink extends JPanel {

    public JLink(Link link) {
        setPoints(new Point(0,0), new Point(0,0));
        setOpaque(false);
        this.link = link;
        if (link != null) {
            this.type = link.getType();
        }
    }
    
    
    public void setSrc(JRubette jrubette, int pos) {
        srcRubette = jrubette;
        srcPos = pos;
    }
    

    public JRubette getSrc() {
        return srcRubette;
    }

    
    public int getSrcPos() {
        return srcPos;
    }

    
    public void setDest(JRubette jrubette, int pos) {
        destRubette = jrubette;
        destPos = pos;
    }
    

    public JRubette getDest() {
        return srcRubette;
    }

    
    public int getDestPos() {
        return destPos;
    }

    
    public void moveSrc(Point srcPt) {
        setPoints(srcPt, this.destPoint);
    }

    
    public void moveDest(Point destPt) {
        setPoints(this.srcPoint, destPt);
    }
    
    
    public void setPoints(Point srcPt, Point destPt) {
        this.srcPoint = srcPt;
        this.destPoint = destPt;
        int x = Math.min(srcPt.x, destPt.x);
        int y = Math.min(srcPt.y, destPt.y);
        int w = Math.abs(destPt.x-srcPt.x);
        int h = Math.abs(destPt.y-srcPt.y);
        if (type == LINE || type == ZIGZAG) {
            srcX = 5+((srcPt.x < destPt.x)?0:w);
            srcY = (srcPt.y < destPt.y)?0:h;
            destX = 5+((srcPt.x > destPt.x)?0:w);
            destY = (srcPt.y > destPt.y)?0:h;
            setBounds(x-5, y, w+10, h+1);
        }
        else if (type == CURVE) {
            int dy = Math.abs(srcPt.y-destPt.y);
            int curve_len = (dy > CURVE_LEN)?CURVE_LEN:CURVE_LEN-(CURVE_LEN-dy);
            y -= curve_len;
            h += 2*curve_len;
            srcX = (srcPt.x < destPt.x)?0:w;
            srcY = (srcPt.y < destPt.y)?0:(srcPt.y-destPt.y);
            destX = (srcPt.x > destPt.x)?0:w;
            destY = (srcPt.y > destPt.y)?curve_len:h-1-curve_len;
            srcY += curve_len;
            path = new GeneralPath();
            path.moveTo(srcX, srcY);
            path.curveTo(srcX, srcY+curve_len, destX, destY-curve_len, destX, destY);
            setBounds(x, y, w+1, h+1);
        }
        repaint();
    }
          
    
    public void setLink(Link link) {
        this.link = link;
        this.link.setType(getType());
    }
    
    
    public Link getLink() {
        return link;
    }
    
    
    public boolean isNear(Point point) {
        int x = point.x-getX();
        int y = point.y-getY();
        if (type == ZIGZAG) {
            int midY = (srcY+destY)/2;
            return isNearLine(srcX, srcY, srcX, midY, x, y) ||
                   isNearLine(srcX, midY, destX, midY, x, y) ||
                   isNearLine(destX, midY, destX, destY, x, y);
        }
        else if (type == LINE) {
            return isNearLine(srcX, srcY, destX, destY, x, y);
        }
        else if (type == CURVE) {
            return path.intersects(x-2, y-2, 4, 4);
        }
        else {
            return false;
        }
    }
    
    
    private boolean isNearLine(int srcX, int srcY, int destX, int destY, int x, int y) {
        Line2D.Double line = new Line2D.Double(srcX, srcY, destX, destY);
        if (line.ptLineDist(x,y) < 5) {
            return true;
        }
        else {
            return false;
        }        
    }
    
    
    public int getType() {
        return type;
    }
    

    public void setType(int t) {
        if (t < LINE) {
            t = LINE;
        }
        if (t > CURVE) {
            t = CURVE;
        }
        type = t;
        if (link != null) {
            link.setType(t);
        }
        repaint();
    }
    
    
    public void detach() {
        srcRubette.removeLink(this);
        destRubette.removeLink(this);
        link.detach();
    }
    
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        if (type == ZIGZAG) {
            int midY = (srcY+destY)/2;
            g2d.drawLine(srcX, srcY, srcX, midY);
            g2d.drawLine(srcX, midY, destX, midY);
            g2d.drawLine(destX, midY, destX, destY);
        }
        else if (type == LINE) {
            g2d.drawLine(srcX, srcY, destX, destY);
        }
        else {
            if (path != null) {
                g2d.setStroke(stroke);
                g2d.draw(path);
            }
        }
    }
    

    private Link  link;
    private Point srcPoint;
    private Point destPoint;

    private JRubette srcRubette  = null;
    private JRubette destRubette = null;
    private int srcPos;
    private int destPos;
    
    private int srcX  = 0;
    private int srcY  = 0;
    private int destX = 0;
    private int destY = 0;
    
    private GeneralPath path = null;
    private Stroke stroke = new BasicStroke(1);
    private int CURVE_LEN = 80;
        
    private int type = CURVE;
    
    public static int LINE   = 0;
    public static int ZIGZAG = 1;
    public static int CURVE  = 2;
}
