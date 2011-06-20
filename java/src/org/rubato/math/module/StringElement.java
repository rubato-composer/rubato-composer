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

import java.util.HashMap;

import org.rubato.math.arith.RingString;

/**
 * Elements in a string ring.
 * @see org.rubato.math.module.StringRing
 * 
 * @author Gérard Milmeister
 */
abstract public class StringElement extends RingElement {

    /**
     * Convenience method for getting the string in a <code>StringElement</code>.
     * Only useful if the <code>RingString</code> has only one term.
     */
    public String getString() {
        return getRingString().getString();
    }

    
    /**
     * Returns the underlying <code>RingString</code>.
     */
    abstract public RingString getRingString();

    
    public boolean isInvertible() {
        return isOne();
    }
    
    
    /**
     * Returns the terms of the string as a map from strings to factors.
     */
    public abstract HashMap<String,RingElement> getTerms();

    
    /**
     * Folds the RingString elements in elements.
     */
    public double[] fold(ModuleElement[] elements) {
        RingString[] relements = new RingString[elements.length];
        for (int i = 0; i < elements.length; i++) {
            relements[i] = ((ZStringElement)elements[i]).getRingString();
        }
        return RingString.fold(relements);
    }
    

    public int hashCode() {
        return getRingString().hashCode();
    }
}
