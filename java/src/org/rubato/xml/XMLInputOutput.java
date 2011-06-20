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

package org.rubato.xml;

import org.w3c.dom.Element;


/**
 * Interface for classes that can input and output their XML representation.
 * 
 * @author Gérard Milmeister
 */
public interface XMLInputOutput<T> {

    /**
     * Outputs XML representation to <code>writer</code>.
     */
    public void toXML(XMLWriter writer);    

    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     * 
     * @return an object of type T or null if parsing failed
     */
    public T fromXML(XMLReader reader, Element element);
    

    /**
     * Returns the value of the type attribute for this class.
     */
    public String getElementTypeName();
}
