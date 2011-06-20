/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.scheme;

import java.io.*;


/**
 * The class wrapping an input port as a Scheme value.
 * 
 * @author Gérard Milmeister
 */
public final class SInPort extends SExpr {

    /**
     * Creates an input port from an input stream.
     */
    public SInPort(InputStream port) {
        this.port = port;
        this.reader = new BufferedReader(new InputStreamReader(port));
    }
    
    
    public boolean eq_p(SExpr sexpr) {
        return sexpr == this;
    }
    
    
    public boolean eqv_p(SExpr sexpr) {
        return sexpr == this;
    }
    
    
    public boolean equal_p(SExpr sexpr) {
        return sexpr == this;
    }
    
    
    public boolean equals(Object obj) {
        return obj == this;
    }
    
    
    public String toString() {
        return "#<input-port:"+((port == System.in)?"stdin":port.hashCode())+">";
    }
    
    
    public String display() {
        return "#<input-port:"+((port == System.in)?"stdin":port.hashCode())+">";
    }
    
    
    /**
     * Closes the port.
     */
    public void close() {
        try {
            reader.close();
            port.close();
        }
        catch (IOException e) {
            // do nothing
        }
    }
    
    
    /**
     * Returns the input stream of this input port.
     */
    public InputStream getPort() {
        return port;
    }
    
    
    /**
     * Returns the reader of this input port.
     */
    public BufferedReader getReader() {
        return reader;
    }
    
    
    private InputStream    port;
    private BufferedReader reader;
}