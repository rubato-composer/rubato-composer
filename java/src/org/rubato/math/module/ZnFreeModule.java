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

import java.util.List;

/**
 * The interface for free modules over integers mod <i>n</i>.
 * @see org.rubato.math.module.ZnFreeElement
 * 
 * @author Gérard Milmeister
 */
public interface ZnFreeModule extends FreeModule {
    
    public int getModulus();

    public ZnFreeElement createElement(List<ModuleElement> elements);
    
    public ZnFreeElement getZero();

    public ZnFreeElement getUnitElement(int i);
}