/*
 * Copyright (C) 2001, 2005 Gérard Milmeister
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

package org.rubato.math.module;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * The abstract base class for elements in <i>proper</i> free modules.
 * @see org.rubato.math.module.ProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public abstract class ProperFreeElement implements FreeElement {

    public int compareTo(ModuleElement object) {
        return getModule().compareTo(object.getModule());
    }
    
    
    public Iterator<RingElement> iterator() {
        LinkedList<RingElement> elements = new LinkedList<RingElement>();
        for (int i = 0; i < getLength(); i++) {
            elements.add(getRingElement(i));
        }
        return elements.iterator();
    }
    
    
    public abstract ProperFreeElement clone();
}
