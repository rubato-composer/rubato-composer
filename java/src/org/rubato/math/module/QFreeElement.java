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
 * The interface for elements in a free module of rationals.
 * @see org.rubato.math.module.QFreeModule
 * 
 * @author Gérard Milmeister
 */
public interface QFreeElement extends FreeElement {

    public QFreeElement sum(ModuleElement element)
        throws DomainException;
    
    public QFreeElement difference(ModuleElement element)
        throws DomainException;
    
    public QFreeElement negated();

    public QFreeElement scaled(RingElement element)
        throws DomainException;

    public QFreeElement resize(int n);
    
    public QElement getComponent(int i);
    
    public QElement getRingElement(int i);
    
    public QFreeElement clone();
}
