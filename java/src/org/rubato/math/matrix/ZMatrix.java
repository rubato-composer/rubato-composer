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

import java.util.Arrays;

/**
 * Matrixes over integers.
 */
public final class ZMatrix extends Matrix {
    
    /**
     * Creates an integer <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to 0.
     */
    public ZMatrix(int rows, int cols) {
        super(rows, cols);
        coefficients = new int[rows][cols];
    }
    
    
    /**
     * Creates an integer <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to <code>value</code>.
     */
    public ZMatrix(int rows, int cols, int value) {
        this(rows, cols);
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }
    
    
    /**
     * Creates a copy of the integer matrix <code>m</code>.
     */
    public ZMatrix(ZMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.coefficients[r][c];
            }
        }
    }
    
    
    /**
     * Creates an integer copy of the modular integer matrix <code>m</code>.
     */
    public ZMatrix(ZnMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.get(r, c);
            }
        }
    }
    
    
    /**
     * Creates a <i>n</i>x1 integer matrix from the vector <code>v</code>,
     * where <i>n</i> is the length of <code>v</code>.
     */
    public ZMatrix(int[] v) {
        this(v.length, 1);
        coefficients = new int[v.length][1];
        for (int r = 0; r < v.length; r++) {
            coefficients[r][0] = v[r];
        }
    }

    
    /**
     * Creates a <i>m</i>x<i>n</i> integer matrix from the 2-dimensional
     * array <code>c</code> of integers.
     */
    public ZMatrix(int[][] c) {
        super(c.length, c[0].length);
        for (int i = 1; i < rows; i++) {
            if (c[i].length != columns) {
                throw new IllegalArgumentException("Rows are not all of the same length.");
            }
        }
        this.coefficients = c;
    }
    
    
    /**
     * Returns the unit integer matrix of the given <code>size</code>.
     */
    public static ZMatrix getUnitMatrix(int size) {
        ZMatrix res = new ZMatrix(size, size);
        for (int i = 0; i < size; i++) {
            res.set(i, i, 1);
        }
        return res;
    }

    
    /**
     * Returns the value at index <code>row</code>,<code>col</code>.
     */
    public int get(int row, int col) {
        return coefficients[row][col];
    }
    
    
    /**
     * Sets index <code>row</code>,<code>col</code> to <code>value</code>.
     */
    public void set(int row, int col, int value) {
        coefficients[row][col] = value;
    }
    
    
    public void setRowCount(int rows) {
        if (this.rows == rows) {
            return;
        }
        int[][] coeffs = new int[rows][columns];
        int min_rows = rows < this.rows ? rows : this.rows;
        for (int r = 0; r < min_rows; r++) {
            System.arraycopy(this.coefficients[r], 0, coeffs[r], 0, columns);
        }
        this.rows = rows;
        this.coefficients = coeffs;
    }
         
    
    public void setColumnCount(int cols) {
        if (this.columns == cols) {
            return;
        }
        int[][] coeffs = new int[rows][cols];
        int min_cols = cols < this.columns ? cols : this.columns;
        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.coefficients[r], 0, coeffs[r], 0, min_cols);
        }
        this.columns = cols;
        this.coefficients = coeffs;
    }
    

    public void setToZeroMatrix() {
        setToElementaryMatrix(0);
    }

    
    public void setToZero(int row, int col) {
        coefficients[row][col] = 0;
    }
    
    
    public void setToOne(int row, int col) {
        coefficients[row][col] = 1;
    }
    

    public void setToUnitMatrix() {
        if (rows > columns) {
            columns = rows;
        }
        else if (rows < columns) {
            rows = columns;
        }
        coefficients = new int[rows][rows];
        for (int rc = 0; rc < rows; rc++) {
            coefficients[rc][rc] = 1;
        }
    }
    
    
    /**
     * Sets all values of this matrix to <code>value</code>.
     */
    public void setToElementaryMatrix(int value) {
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }


    public ZMatrix getSubMatrix(int fromRow, int toRow, int fromCol, int toCol) {
        int nrows = toRow-fromRow+1;
        int ncols = toCol-fromCol+1;
        ZMatrix m = new ZMatrix(nrows, ncols);
        for (int r = 0; r < nrows; r++) {
            System.arraycopy(coefficients[r+fromRow], fromCol, m.coefficients[r], 0, ncols);
        }
        return m;
    }
    
    
    public ZMatrix getMinorMatrix(int row, int col) {
        ZMatrix m = new ZMatrix(rows-1, columns-1);
        for (int r = 0, rOffset = 0; r < rows-1; r++) {
            if (r == row) rOffset = 1;
            for (int c = 0, cOffset = 0; c < columns-1; c++) {
                if (c == col) cOffset = 1;
                m.coefficients[r][c] = coefficients[r+rOffset][c+cOffset];
            }
        }
        return m;
    }
    

    public ZMatrix transposed() {
        ZMatrix m = new ZMatrix(columns, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[c][r] = coefficients[r][c];
            }
        }
        return m;
    }

    
    public ZMatrix adjoint() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        ZMatrix m = new ZMatrix(rows, columns);
        if (rows == 1) {
            m.coefficients[0][0] = 1;
            return m;
        }
        
        // take care of the transposition (swap r and c)!
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[c][r] = minor(r + 1, c + 1);
            }
        }
        return m;
    }

    
    public ZMatrix affineDifference() {
        if (columns == 1) {
            return this;
        }
        
        ZMatrix m = new ZMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = coefficients[r][c] - coefficients[r][0];
            }
        }
        return m;
    }
    
    
    public ZMatrix quadraticForm() {
        ZMatrix m = transposed();
        return m.product(this);
    }
    
    
    public ZMatrix scaled(int scalar) {
        ZMatrix m = new ZMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = scalar*coefficients[r][c];
            }
        }
        return m;
    }
    
    
    public ZMatrix power(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        if (exponent == 1) {
            return this;
        }
            
        ZMatrix m = null;
        
        if (exponent == 0) {
            m = new ZMatrix(rows, columns);
            m.setToUnitMatrix();
            return m;
        }
        
        if (exponent < 0) {
            throw new ArithmeticException("Cannot raise to negative powers.");
        }
        else {
            m = this;
        }
        
        int k;
        for (k = 2; (k <= exponent) && (exponent % k != 0); k++);
        if (k == exponent) {
            return product(power(k-1));
        }
        else {
            return power(exponent/k).power(k);
        }
    }
    
    
    public int rank() {
        throw new ArithmeticException("Cannot compute the rank of an integer matrix.");
    }

    
    public boolean isConstant() {
        int coeff = coefficients[0][0];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (coefficients[r][c] != coeff) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    public boolean isZero() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (coefficients[r][c] != 0) { 
                    return false;
                }
            }
        }
        return true;
    }

    
    public boolean isUnit() {
        if (rows != columns) {
            return false;
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (r == c) {
                    if (coefficients[r][c] != 1) {
                        return false;
                    }
                }
                else {
                    if (coefficients[r][c] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    
    public boolean isRegular() {
        return determinant() != 0;
    }

    
    public boolean isZero(int row, int col) {
        return coefficients[row][col] == 0; 
    }

    
    public boolean isOne(int row, int col) {
        return coefficients[row][col] == 1; 
    }

    
    public int dotProduct(ZMatrix m) {
        if (columns != 1 || m.columns != 1) {
            throw new ArithmeticException("Matrix is not a vector");
        }
        
        int dot = 0;
        for (int r = 0; r < rows; r++) {
            dot += coefficients[r][0] * m.coefficients[r][0];
        }
        return dot;
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof ZMatrix) {
            ZMatrix m = (ZMatrix)object;
            if (sameSize(m)) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (coefficients[i][j] != m.coefficients[i][j]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    

    public int compareTo(Matrix object) {
        if (object instanceof ZMatrix) {
            ZMatrix m = (ZMatrix)object;
            if (rows < m.rows) {
                return -1;
            }
            else if (rows > m.rows) {
                return 1;
            }
            else if (columns < m.columns) {
                return -1;
            }
            else if (columns > m.columns) {
                return 1;
            }
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    int cmp = coefficients[r][c]-m.coefficients[r][c];
                    if (cmp < 0) {
                        return -1;
                    }
                    else if (cmp > 0) {
                        return 1;
                    }
                }
            }
            return 0;
        }        
        else {
            return super.compareTo(object);
        }
    }
    

    public void setSubMatrix(int row, int col, ZMatrix m) {
        for (int r = 0; r < m.rows; r++) {
            System.arraycopy(m.coefficients[r], 0, coefficients[row+r], col, m.columns);
        }
    }
    
    
    public ZMatrix sum(ZMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        ZMatrix sum = new ZMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = coefficients[r][c] + m.coefficients[r][c];
            }
        }
        return sum;
    }

    
    public ZMatrix difference(ZMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        ZMatrix sum = new ZMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = coefficients[r][c] - m.coefficients[r][c];
            }
        }
        return sum;
    }
    
    
    public ZMatrix product(ZMatrix m) {
        if (!productPossible(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        
        ZMatrix product = new ZMatrix(rows, m.columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < m.columns; c++) {
                int sum = 0;
                for (int i = 0; i < columns; i++) {
                    sum += coefficients[r][i]*m.coefficients[i][c];
                }
                product.coefficients[r][c] = sum;
            }
        }
        return product;
    }
    

    public int[] product(int[] vector) {
        if (columns != vector.length) {
	       throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        int[] res = new int[rows];
        int sum;
        for (int r = 0; r < rows; r++) {
	        sum = 0;
	        for (int c = 0; c < columns; c++) {
		        sum = sum + coefficients[r][c] * vector[c];
            }
	        res[r] = sum;
        }
        return res;
    }

    
    public int determinant() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        if (rows == 1) {
            return coefficients[0][0];
        }
        
        RMatrix m = new RMatrix(this);
        
        return (int)m.determinant();
    }
    
    
    /**
     * Returns the minor at <code>row<code>,<code>col</code>.
     */
    public int minor(int row, int col) {
        ZMatrix m = getMinorMatrix(row, col);
        if ((row+col)%2 == 0) {
            return m.determinant();
        }
        else {
            return -m.determinant();
        }
    }
    
    
    /**
     * Returns the Euclidean norm of this matrix.
     */
    public int euclidean() {
        int euclidean = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int s = coefficients[r][c];
                euclidean += s*s;
            }
        }        
        return euclidean;
    }
    
    
    /**
     * Returns the 1-norm of this matrix.
     */
    public int sum() {
        int sum = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum += coefficients[r][c];
            }
        }
        return sum;
    }
    

    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("ZMatrix[");
        buf.append(rows);
        buf.append(",");
        buf.append(columns);
        buf.append("][");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                buf.append(coefficients[i][j]);
                if (j < columns-1) { buf.append(" "); }
            }
            if (i < rows-1) { buf.append("; "); }
        }
        buf.append("]");
        return buf.toString();
    }
    

    private int[][] coefficients;
}
