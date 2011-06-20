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

import org.rubato.base.RubatoException;

/**
 * Exception thrown in cases of mismatched forms.
 * This exception is thrown, among others, when a denotator
 * of a given form is expected, but does not have that form.
 * 
 * @author Gérard Milmeister
 */
public final class RubatoFormException extends RubatoException {

    /**
     * Creates a standard RubatoFormException with a message.
     * 
     * @param expected the form that was required
     * @param received the actual form
     * @param msg message of the exception
     */
    public RubatoFormException(String msg, Form expected, Form received) {
        super(msg);
        this.expected = expected;
        this.received = received;
    }

    
    /**
     * Creates a standard RubatoFormException with an automatically
     * generated message.
     * 
     * @param expected the form that was required
     * @param received the actual form
     */
    public RubatoFormException(Form expected, Form received, String src) {
        this(src+": expected form "+expected+", got "+received+".", expected, received);
    }
    
    
    /**
     * Returns the actual (received) form. 
     */
    public Form getReceivedForm() {
        return received;
    }
    

    /**
     * Returns the expected form. 
     */
    public Form getExpectedForm() {
        return expected;
    }
    
    
    private Form expected;
    private Form received;
}
