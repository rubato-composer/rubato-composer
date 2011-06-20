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

/**
 * The interface for elements in free modules over reals.
 * @see org.rubato.math.module.RFreeModule
 * 
 * @author Gérard Milmeister
 */
public interface RFreeElement extends FreeElement {

    public RFreeElement sum(ModuleElement element)
        throws DomainException;

    public RFreeElement difference(ModuleElement element)
        throws DomainException;

    public RFreeElement negated();

    public RFreeElement scaled(RingElement element)
        throws DomainException;

    public RFreeElement resize(int n);

    public RElement getComponent(int i);

    public RElement getRingElement(int i);

    public RFreeElement clone();
}