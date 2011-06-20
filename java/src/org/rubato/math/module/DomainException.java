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

import org.rubato.base.RubatoException;

/**
 * This exception is thrown whenever a module element operation fails due
 * to a wrong domain. The exception contains two pieces of information.
 * The <i>expected</i> module indicates the domain that the element was to be
 * part of. The <i>received</i> module is the domain that the actual element is
 * part of.
 */
public final class DomainException extends RubatoException {
    
    /**
     * Creates a DomainException.
     * 
     * @param message  indicates the reason for the exception
     * @param expected the module (or element of that module) that was required
     * @param received the actual module (or element of that module)
     */
    public DomainException(String message, Module expected, Module received) {
        super(message);
        this.expected = expected;
        this.received = received;
    }
    
    
    /**
     * Creates a DomainException.
     * A message is generated from <code>expected</code> and <code>received</code>.
     * 
     * @param expected the module (or element of that module) that was required
     * @param received the actual module (or element of that module)
     */
    public DomainException(Module expected, Module received) {
        this("Expected domain "+expected+", got "+received+".", expected, received);
    }


    /**
     * Returns the module that was required.
     */
    public Module getExpectedDomain() {
        return expected;
    }
    
    
    /**
     * Returns the actual module.
     */
    public Module getReceivedDomain() {
        return received;
    }
    
    
    private Module expected;
    private Module received;
}
