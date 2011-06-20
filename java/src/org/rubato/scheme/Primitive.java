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

/**
 * The interface that all primitive functions must implement.
 * 
 * @author Gérard Milmeister
 */
public abstract class Primitive {

    /**
     * Returns the name of the function.
     */
    public abstract String getName();

    /**
     * The code of the function is executed using this method
     * with argument list <code>args</code> and evaluator <code>eval</code>.
     */
    public abstract SExpr call(SExpr args, Evaluator eval);
}
