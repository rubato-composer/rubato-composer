/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
 * Copyright (C) 2002 Stefan Müller
 * Copyright (C) 2002 Stefan Göller
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

package org.rubato.math.yoneda;

import java.io.Serializable;

/**
 * Base interface for Form, Morphism, Diagram and Denotator.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public interface Yoneda extends Cloneable, Serializable {

    static final int SIMPLE  = 0;
    static final int LIMIT   = 1;
    static final int COLIMIT = 2;
    static final int POWER   = 3;
    static final int LIST    = 4;
                                           
    /**
     * Clone object. All yoneda classes must support cloning, thus clone() is
     * overridden at this point without the "CloneNotSupported" exception.
     * The clone operation has different semantics for different yoneda 
     * subclasses.
     */
    public Object clone();
}
