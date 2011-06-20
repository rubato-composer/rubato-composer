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

import static org.rubato.xml.XMLConstants.*;

import org.rubato.xml.XMLWriter;


public class Link {

    public Link(RubetteModel src, int srcPos, RubetteModel dest, int destPos) {
        this.src     = src;
        this.srcPos  = srcPos;
        this.dest    = dest;
        this.destPos = destPos;
    }
    

    public RubetteModel getSrcModel() {
        return src;
    }
    
    
    public RubetteModel getDestModel() {
        return dest;
    }
    
    
    public int getSrcPos() {
        return srcPos;
    }
    

    public int getDestPos() {
        return destPos;
    }

    
    public int getType() {
        return type;
    }
    
    
    public void setType(int t) {
        type = t;
    }
    
    
    public void detach() {
        src.removeOutLink(this);
        dest.removeInLink(this);
    }
    

    public void toXML(XMLWriter writer) {
        writer.empty(LINK,
                     SRC_ATTR, src.getSerial(),
                     SRCPOS_ATTR, srcPos,
                     DEST_ATTR, dest.getSerial(),
                     DESTPOS_ATTR, destPos,
                     TYPE_ATTR, type);
    }
    
    
    public String toString() {
        return "Link["+src+","+srcPos+","+dest+","+destPos+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    
    private RubetteModel src;
    private int          srcPos;
    private RubetteModel dest;
    private int          destPos;   
    private int          type = JLink.LINE;
}
