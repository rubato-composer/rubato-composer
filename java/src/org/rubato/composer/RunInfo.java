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

package org.rubato.composer;

import org.rubato.composer.rubette.RubetteModel;

/**
 * A RunInfo provides information about the current running network.
 * 
 * @author Gérard Milmeister
 */
public interface RunInfo {
   
    /**
     * Returns true iff the current running network has been stopped.
     */
    public boolean stopped();
    
    
    /**
     * Adds a progress message for the specified rubette.
     */
    public void addMessage(RubetteModel rubette, String msg);
}
