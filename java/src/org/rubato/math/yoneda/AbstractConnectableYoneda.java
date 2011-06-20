/*
 * Copyright (C) 2001, 2005 Gérard Milmeister
 * Copyright (C) 2001 Stefan Müller
 * Copyright (C) 2001 Stefan Göller
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

/**
 * Abstract base class for Form and Denotator.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public abstract class AbstractConnectableYoneda implements Connectable, Yoneda {
 
    public final Object getConnector() {
        return connector;
    }
    
    public final void setConnector(Object c) {
        connector = c;
    }
    
    public abstract Object clone();
    
    private transient Object connector;
}
