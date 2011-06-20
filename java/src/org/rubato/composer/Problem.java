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

import org.rubato.composer.network.JNetwork;
import org.rubato.composer.rubette.JRubette;

/**
 * An instance of this class describes a problem occurring during running.
 * 
 * @author Gérard Milmeister
 */
public class Problem {

    /**
     * Creates a Problem with the given message.
     * @param jnetwork the JNetwork where the problem occurred
     * @param jrubette the JRubette where the problem occurred
     */
    public Problem(String msg, JNetwork jnetwork, JRubette jrubette) {
        this.msg = msg;
        this.jrubette = jrubette;
        this.jnetwork = jnetwork;
    }
    

    /**
     * Returns the JRubette where the problem occurred.
     */
    public JRubette getJRubette() {
        return jrubette;
    }
    
    
    /**
     * Returns the JNetwork where the problem occurred.
     */
    public JNetwork getJNetwork() {
        return jnetwork;
    }
    
    
    public String toString() {
        return jnetwork+":"+jrubette+": "+msg; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    private String   msg;
    private JRubette jrubette;
    private JNetwork jnetwork;
}
