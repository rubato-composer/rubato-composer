/*
 * Copyright (C) 2007 Gérard Milmeister
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
 * The interface for free modules over modular polynomials.
 * 
 * @author Gérard Milmeister
 */
public interface ModularPolynomialFreeModule extends FreeModule {
    
    /**
     * Returns the ring of the coefficients of the polynomials.
     */
    public Ring getCoefficientRing();
    
    /**
     * Returns the indeterminate of this modular polynomial module.
     */
    public String getIndeterminate();
    
    /**
     * Returns the modulus of this modular polynomial module.
     * 
     */
    public PolynomialElement getModulus();
    
    /**
     * Returns the ring of the modulus.
     */
    public PolynomialRing getModulusRing();
}