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
import org.rubato.math.module.Module;

/**
 * Exception thrown in cases of mismatched addresses.
 * This exception is thrown, among others, when a denotator
 * with a given address is expected, but does not have that address.
 * 
 * @author Gérard Milmeister
 */
public final class RubatoAddressException extends RubatoException {

    /**
     * Creates a standard RubatoAddressException.
     * 
     * @param s description of the exception
     * @param receivedAddress the actual address
     * @param expectedAddress the expected address
     */
    public RubatoAddressException(String s, Module receivedAddress, Module expectedAddress) {
        super(s);
        this.receivedAddress = receivedAddress;
        this.expectedAddress = expectedAddress;
    }

    
    /**
     * Returns the actual (received) address. 
     */
    public Module getReceivedAddress() {
        return receivedAddress;
    }
    
    
    /**
     * Returns the expected (required) address. 
     */
    public Module getExpectedAddress() {
        return expectedAddress;
    }
    

    private Module receivedAddress;    
    private Module expectedAddress;    
}
