/*
 * Copyright (C) 2002 Gérard Milmeister
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

/**
 * Interface for classes that have a connector attached.
 * A connector is a transient additional field that is of arbitrary type
 * and can be used for general purpose (i.e. tree walk algorithms). 
 * The code using the connector is responsible for themselves to
 * avoid interferences with other code.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public interface Connectable {
    
    public Object getConnector();
    
    void setConnector(Object connector);
}
