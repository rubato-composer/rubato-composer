/*
 * Copyright (C) 2001 Gérard Milmeister
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

package org.rubato.math.matrix;

/**
 * Abstract base class for matrixes.
 * Contains only some common methods that are independent of the
 * underlying ring.
 * 
 * @author Gérard Milmeister
 */
public abstract class Matrix implements Comparable<Matrix> {

    /**
     * Returns the number of rows of this matrix.
     */
    public final int getRowCount() {
        return rows;
    }

    /**
     * Resizes this matrix by changing the number of rows.
     * If new rows are created, their values are set to 0.
     */
    public abstract void setRowCount(int rows);

    /**
     * Returns the number of columns of this matrix.
     */
    public final int getColumnCount() {
        return columns;
    }

    /**
     * Resizes this matrix by changing the number of columns.
     * If new columns are created, the new coefficients are all set to zero.
     */
    public abstract void setColumnCount(int cols);

    /**
     * Sets all coefficients to zero.
     */
    public abstract void setToZeroMatrix();
    
    /**
     * Sets the coefficient at <code>row</code>,<code>col</code> to zero.
     */
    public abstract void setToZero(int row, int col);
    
    /**
     * Sets the coefficient at <code>row</code>,<code>col</code> to one.
     */
    public abstract void setToOne(int row, int col);
    
    /**
     * Makes this matrix unit square.
     * If necessary, the matrix is resized, its final size being the larger
     * of the number of rows and the number of columns.
     */
    public abstract void setToUnitMatrix();
    
    /**
     * Returns the rank of this matrix.
     */
    public abstract int rank();

    /**
     * Returns true iff the coefficients of this matrix are equal
     * to a constant value.
     */
    public abstract boolean isConstant();
    
    /**
     * Returns true iff this is a zero matrix.
     */
    public abstract boolean isZero();

    /**
     * Returns true iff this is a unit matrix.
     */
    public abstract boolean isUnit();

    /**
     * Returns true iff this is a square matrix.
     */
    public final boolean isSquare() {
        return rows == columns;
    }

    /**
     * Returns true iff this matrix is regular.
     */
    public abstract boolean isRegular();
    
    /**
     * Returns true iff the coefficient at <code>row</code>,<code>col</code> is zero.
     */
    public abstract boolean isZero(int row, int col);
        
    /**
     * Returns true iff the coefficient at <code>row</code>,<code>col</code> is one.
     */
    public abstract boolean isOne(int row, int col);
        
    /**
     * Returns true iff row <code>r</code> is zero.
     */
    public boolean isZeroRow(int r) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (!isZero(r, i)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Returns true iff column <code>c</code> is zero.
     */
    public boolean isZeroColumn(int c) {
        for (int j = 0; j < getRowCount(); j++) {
            if (!isZero(j, c)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Returns true iff this matrix and <code>m</code> have the same size.
     */
    public final boolean sameSize(Matrix m) {
        return rows == m.rows && columns == m.columns;
    }
    
    
    /**
     * Returns true iff <code>m1</code> and <code>m2</code> have the same size.
     */        
    public final static boolean sameSize(Matrix m1, Matrix m2) {
        return m1.sameSize(m2);
    }
    
    
    /**
     * Returns true iff the product of this matrix and <code>m</code> is possible.
     */
    public final boolean productPossible(Matrix m) {
        return columns == m.rows;
    }
    
    
    /**
     * Returns true iff the product of <code>m1</code> and <code>m2</code> is possible.
     */
    public final static boolean productPossible(Matrix m1, Matrix m2) {
        return m1.productPossible(m2);
    }
    
    
    /**
     * Returns a string representation of this matrix.
     */
    public abstract String toString();
    
    
    public abstract boolean equals(Object object);
    
    
    /**
     * Compares this matrix to <code>object</code>.
     * If <code>object</code> is a matrix, but not of the same
     * type, then the strings representation are compared.
     */
    public int compareTo(Matrix object) {
        return toString().compareTo(toString());
    }
    
    
    protected Matrix(int rows, int columns) {
        assert(rows >= 0);
        assert(columns >= 0);
        this.rows = rows;
        this.columns = columns;
    }
    
    protected int rows;
    protected int columns;
}