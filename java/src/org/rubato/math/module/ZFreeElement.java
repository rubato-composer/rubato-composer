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
 * The interface for elements in a free module of integers.
 * @see org.rubato.math.module.ZFreeModule
 * 
 * @author Gérard Milmeister
 */
public interface ZFreeElement extends FreeElement {

    public ZFreeElement sum(ModuleElement element)
        throws DomainException;

    public ZFreeElement difference(ModuleElement element)
        throws DomainException;

    public ZFreeElement negated();

    public ZFreeElement scaled(RingElement element)
        throws DomainException;

    public ZFreeElement resize(int n);

    public ZElement getComponent(int i);

    public ZElement getRingElement(int i);

    public ZFreeElement clone();
}
