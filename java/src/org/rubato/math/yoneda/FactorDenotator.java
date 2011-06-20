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

package org.rubato.math.yoneda;

import java.util.Iterator;
import java.util.List;

import org.rubato.base.RubatoException;

/**
 * Interface for denotators that have factors.
 * These are all types of denotators except simple denotators.
 * 
 * @author Gérard Milmeister
 */
public interface FactorDenotator extends Iterable<Denotator> {
    /**
     * Returns the number of coordinates of the denotator.
     * @return number of coordinates
     */
    public int getFactorCount();

    /**
     * Returns the factor in position <code>i</code>.
     * @param i the position of the factor
     */
    public Denotator getFactor(int i);

    /**
     * Sets the factor <code>d</code> in position <code>i</code>.
     * This is a destructive operation; beware of aliasing.
     */
    public void setFactor(int i, Denotator d)
        throws RubatoException;

    /**
     * Appends, if possible, the factor <code>d</code>.
     * This is a destructive operation; beware of aliasing.
     * @return true iff the append is successful
     */
    public boolean appendFactor(Denotator d)
        throws RubatoException;
    
    /**
     * Returns a (new) list of the coordinates of the denotator.
     */
    public List<Denotator> getFactors();

    /**
     * Returns an iterator over the coordinates.
     */
    public Iterator<Denotator> iterator();
}