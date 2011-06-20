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

import java.text.DecimalFormat;
import java.util.Arrays;

import org.rubato.math.arith.NumberTheory;

/**
 * Matrixes over modular integers.
 */
public final class ZnMatrix extends Matrix {
    
    /**
     * Creates an integer mod <code>modulus</code>
     * <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to 0.
     */
    public ZnMatrix(int rows, int cols, int modulus) {
        super(rows, cols);
        this.modulus = modulus;
        coefficients = new int[rows][cols];
    }
    
    
    /**
     * Creates an integer mod <code>modulus</code>
     * <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to <code>value</code>.
     */
    public ZnMatrix(int rows, int cols, int modulus, int value) {
        this(rows, cols, modulus);
        int v = NumberTheory.mod(value, modulus);
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], v);
        }
    }
    
    
    /**
     * Creates a copy of the modular integer matrix <code>m</code>.
     */
    public ZnMatrix(ZnMatrix m) {
        this(m.getRowCount(), m.getColumnCount(), m.getModulus());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.coefficients[r][c];
            }
        }
    }
    
    
    /**
     * Creates an integer mod <code>modulus</code>
     * copy of the integer matrix <code>m</code>.
     */
    public ZnMatrix(ZMatrix m, int modulus) {
        this(m.getRowCount(), m.getColumnCount(), modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = NumberTheory.mod(m.get(r, c), modulus);
            }
        }
    }
    
    
    /**
     * Creates an integer mod <code>modulus</code>
     * copy of the modular integer matrix <code>m</code>.
     */
    public ZnMatrix(ZnMatrix m, int modulus) {
        this(m.getRowCount(), m.getColumnCount(), modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = NumberTheory.mod(m.get(r, c), modulus);
            }
        }
    }
    
    
    /**
     * Creates a <i>n</i>x1 integer modular <code>modulus</code>
     * matrix from the vector <code>v</code>,
     * where <i>n</i> is the length of <code>v</code>.
     */
    public ZnMatrix(int[] v, int modulus) {
        this(v.length, 1, modulus);
        for (int r = 0; r < v.length; r++) {
            coefficients[r][0] = NumberTheory.mod(v[r], modulus);
        }
    }

    
    /**
     * Creates a <i>m</i>x<i>n</i> integer mod <code>modulus</code>
     * matrix from the 2-dimensional array <code>c</code> of integers.
     */
    public ZnMatrix(int[][] c, int modulus) {
        this(c.length, c[0].length, modulus);
        for (int i = 1; i < rows; i++) {
            if (c[i].length != columns) {
                throw new IllegalArgumentException("Rows are not all of the same length.");
            }
        }
        this.coefficients = c;
    }
    
    
    /**
     * Returns the modulus of the coefficient modular integer ring.
     */
    public int getModulus() {
        return modulus;
    }
    
    
    /**
     * Returns the unit integer mod <code>modulus</code>
     * matrix of the given <code>size</code>.
     */
    public static ZnMatrix getUnitMatrix(int size, int modulus) {
        ZnMatrix res = new ZnMatrix(size, size, modulus);
        for (int i = 0; i < size; i++) {
            res.coefficients[i][i] = 1;
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
        coefficients[row][col] = NumberTheory.mod(value, modulus);
    }
    
    
    public void setToZero(int row, int col) {
        coefficients[row][col] = 0;
    }

    
    public void setToOne(int row, int col) {
        coefficients[row][col] = 1;
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
        int v = NumberTheory.mod(value, modulus);
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], v);
        }
    }


    public ZnMatrix getSubMatrix(int fromRow, int toRow, int fromCol, int toCol) {
        int nrows = toRow-fromRow+1;
        int ncols = toCol-fromCol+1;
        ZnMatrix m = new ZnMatrix(nrows, ncols, modulus);
        for (int r = 0; r < nrows; r++) {
            System.arraycopy(coefficients[r+fromRow], fromCol, m.coefficients[r], 0, ncols);
        }
        return m;
    }
    
    
    public ZnMatrix getMinorMatrix(int row, int col) {
        ZnMatrix m = new ZnMatrix(rows-1, columns-1, modulus);
        for (int r = 0, rOffset = 0; r < rows-1; r++) {
            if (r == row) rOffset = 1;
            for (int c = 0, cOffset = 0; c < columns-1; c++) {
                if (c == col) cOffset = 1;
                m.coefficients[r][c] = coefficients[r+rOffset][c+cOffset];
            }
        }
        return m;
    }
    

    public ZnMatrix transposed() {
        ZnMatrix m = new ZnMatrix(columns, rows, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[c][r] = coefficients[r][c];
            }
        }
        return m;
    }

    
    public ZnMatrix inverse() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        int n = rows;
        int i, j, k, t;
        int maxval;
        int maxpos;
        int[] p = new int[n];
        for (i = 0; i < n; i++) p[i] = i;
        
        int[][] m = new int[n][2*n];
        for (i = 0; i < n; i++) {
            System.arraycopy(coefficients[i], 0, m[i], 0, n);
            for (j = n; j < 2*n; j++) {
                m[i][j] = 0;
            }
            m[i][n+i] = 1;
        }

        for (i = 0; i < n; i++) {
            maxpos = i;
            maxval = Math.abs(m[p[i]][i]);
            for (j = i+1; j < n; j++) {
                if (Math.abs(m[p[j]][i]) > maxval) {
                    maxval = Math.abs(m[p[j]][i]);
                    maxpos = j;
                }
            }
            if (maxval == 0) { 
                throw new ArithmeticException("Matrix is not invertible.");
            }
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
            }
            
            for (j = i+1; j < n; j++) {
                int f = NumberTheory.divideMod(m[p[j]][i], m[p[i]][i], modulus);
                for (k = i; k < 2*n; k++) m[p[j]][k] -= f*m[p[i]][k];
            }
        }
        
        if (m[p[n-1]][n-1] == 0) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
            
        for (i = n-1; i >= 0; i--) {
            int f = m[p[i]][i];
            for (k = i; k < 2*n; k++) {
                m[p[i]][k] = NumberTheory.divideMod(m[p[i]][k], f, modulus);
            }
            for (j = i-1; j >= 0; j--) {
                f = m[p[j]][i];
                for (k = i; k < 2*n; k++) m[p[j]][k] -= f*m[p[i]][k];
            }
        }

        ZnMatrix rm = new ZnMatrix(n, n, modulus);
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                rm.coefficients[i][j] = m[p[i]][j+n];
            }
        }
        
        return rm;
    }

    
    public ZnMatrix adjoint() {
        if (!isSquare()) {
            throw new IllegalStateException("Matrix is not square.");
        }
        
        ZnMatrix m = new ZnMatrix(rows, columns, modulus);
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

    
    public ZnMatrix affineDifference() {
        if (columns == 1) {
            return this;
        }
        
        ZnMatrix m = new ZnMatrix(rows, columns, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = coefficients[r][c] - coefficients[r][0];
            }
        }
        return m;
    }
    
    
    public ZnMatrix quadraticForm() {
        ZnMatrix m = transposed();
        return m.product(this);
    }
    
    
    public ZnMatrix scaled(int scalar) {
        ZnMatrix m = new ZnMatrix(rows, columns, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = NumberTheory.mod(scalar*coefficients[r][c], modulus);
            }
        }
        return m;
    }
    
    
    public ZnMatrix power(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        if (exponent == 1) {
            return this;
        }
            
        ZnMatrix m = null;
        
        if (exponent == 0) {
            m = new ZnMatrix(rows, columns, modulus);
            m.setToUnitMatrix();
            return m;
        }
        
        if (exponent < 0) {
            exponent *= -1;
            m = inverse();
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
    
    
    public ZnMatrix taylor(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        ZnMatrix m = null;
        if (exponent < 0) {
            exponent *= -1;
            m = inverse();
        }
        else {
            m = this;
        }
        
        ZnMatrix n = new ZnMatrix(rows, columns, modulus);
        n.setToUnitMatrix();
        
        for (int i = 1, fac = 1; i <= exponent; i++, fac*=i) {
            n = n.sum(m.power(i)).scaled(NumberTheory.divideMod(1, fac, modulus));
        }
        return n;
    }
    

    public int rank() {
        if (!NumberTheory.isPrime(getModulus())) {
            throw new ArithmeticException("Z_"+getModulus()+" is not a field.");
        }
        ZnMatrix m;        
        if (getRowCount() < getColumnCount()) {
            m = transposed();
        }
        else {
            m = new ZnMatrix(this);
        }
        
        m.computeRREF();
        
        int rank = m.getRowCount();
        int i = m.getRowCount()-1;
        while (i >= 0 && m.isZeroRow(i)) {
            rank--;
            i--;
        }
        return rank;
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
        return determinant() > 0;
    }

    
    public boolean isZero(int row, int col) {
        return coefficients[row][col] == 0;
    }
    
    
    public boolean isOne(int row, int col) {
        return coefficients[row][col] == 1;
    }
    

    public int dotProduct(ZnMatrix m) {
        if (columns != 1 || m.columns != 1) {
            throw new ArithmeticException("Matrix is not a vector.");
        }
        
        int dot = 0;
        for (int r = 0; r < rows; r++) {
            dot  = NumberTheory.mod(dot+coefficients[r][0]*m.coefficients[r][0], modulus);
        }
        return dot;
    }
    

    public boolean equals(Object object) {
        if (object instanceof ZnMatrix) {
            ZnMatrix m = (ZnMatrix)object;
            if (sameSize(m) && modulus == m.modulus) {
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
        if (object instanceof ZnMatrix) {
            ZnMatrix m = (ZnMatrix)object;
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
    

    public void setSubMatrix(int row, int col, ZnMatrix m) {
        for (int r = 0; r < m.rows; r++) {
            System.arraycopy(m.coefficients[r], 0, coefficients[row+r], col, m.columns);
        }
    }
    
    
    /**
     * Compute the reducted row echelon form of this matrix.
     * This operation is destructive, i.e., the contents are replaced.
     */
    public void computeRREF() {
        int i = 0;
        int j = 0;
        int m = getRowCount();
        int n = getColumnCount();
        while (i < m && j < n) {
            // Find pivot in column j, starting in row i:
            int max_val = coefficients[i][j];
            int max_ind = i;
            for (int k = i+1; k < m; k++) {
                int val = coefficients[k][j];
                if (Math.abs(val) > Math.abs(max_val)) {
                    max_val = val;
                    max_ind = k;
                }
            }
            if (Math.abs(max_val) != 0) {
                // switch rows i and max_ind
                int[] tmp = coefficients[i];
                coefficients[i] = coefficients[max_ind];
                coefficients[max_ind] = tmp;
                // divide row i by max_val
                for (int k = 0; k < n; k++) {
                    coefficients[i][k] = NumberTheory.divideMod(coefficients[i][k], max_val, getModulus());
                }
                for (int u = 0; u < m; u++) {
                    if (u != i) {
                        int v = coefficients[u][j];
                        for (int k = 0; k < n; k++) {
                            coefficients[u][k] = NumberTheory.mod(coefficients[u][k]-v*coefficients[i][k], getModulus());                            
                        }
                    }
                }
                i++;
            }
            j++;
        }
    }
    
    
    public ZnMatrix sum(ZnMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        if (modulus != m.modulus) {
            throw new ArithmeticException("Unmatched matrix modulus.");            
        }
        ZnMatrix sum = new ZnMatrix(rows, columns, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = NumberTheory.mod(coefficients[r][c] + m.coefficients[r][c], modulus);
            }
        }
        return sum;
    }

    
    public ZnMatrix difference(ZnMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        if (modulus != m.modulus) {
            throw new ArithmeticException("Unmatched matrix modulus.");            
        }
        ZnMatrix sum = new ZnMatrix(rows, columns, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = NumberTheory.mod(coefficients[r][c] - m.coefficients[r][c], modulus);
            }
        }
        return sum;
    }
    
    
    public ZnMatrix product(ZnMatrix m) {
        if (!productPossible(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        if (modulus != m.modulus) {
            throw new ArithmeticException("Unmatched matrix modulus.");            
        }
        ZnMatrix product = new ZnMatrix(rows, m.columns, modulus);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < m.columns; c++) {
                int sum = 0;
                for (int i = 0; i < columns; i++) {
                    sum = NumberTheory.mod(sum + coefficients[r][i]*m.coefficients[i][c], modulus);
                }
                product.coefficients[r][c] = sum;
            }
        }
        return product;
    }
    

    public int[] product(int[] vector) {
        if (columns != vector.length) {
	       throw new ArithmeticException("Unmatched matrix dimensions");
        }
        int[] res = new int[rows];
        int sum;
        for (int r = 0; r < rows; r++) {
	        sum = 0;
	        for (int c = 0; c < columns; c++) {
		        sum = NumberTheory.mod(sum + coefficients[r][c] * vector[c], modulus);
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
                
        int i, j, t;
        int maxval;
        int maxpos;
        int factor = 1;
        int[] p = new int[rows];
        for (i = 0; i < rows; i++) p[i] = i;
        
        int[][] m = new int[rows][columns];
        for (i = 0; i < rows; i++)
            System.arraycopy(coefficients[i], 0, m[i], 0, columns);

        for (i = 0; i < rows; i++) {
            maxpos = i;
            maxval = m[p[i]][i];
            for (j = i+1; j < rows; j++) {
                if (m[p[j]][i] > maxval) {
                    maxval = m[p[j]][i];
                    maxpos = j;
                }
            }
            if (maxval == 0) return 0;
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
                factor = NumberTheory.mod(factor * -1, modulus);
            }
            
            for (j = i+1; j < rows; j++) {
                int f = NumberTheory.divideMod(m[p[j]][i], m[p[i]][i], modulus);
                for (int k = i; k < columns; k++) {
                    m[p[j]][k] = NumberTheory.mod(m[p[j]][k] - f*m[p[i]][k], modulus);
                }
            }
        }
        
        int det = 1;
        for (i = 0; i < rows; i++) {
            det = NumberTheory.mod(det * m[p[i]][i], modulus);
        }
        
        return NumberTheory.mod(factor*det, modulus);
    }
    
    
    /**
     * Returns the minor at <code>row<code>,<code>col</code>.
     */
    public int minor(int row, int col) {
        ZnMatrix m = getMinorMatrix(row, col);
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
                euclidean = NumberTheory.mod(euclidean+s*s, modulus);
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
                sum = NumberTheory.mod(sum + coefficients[r][c], modulus);
            }
        }
        return sum;
    }
    

    public String toString() {
        DecimalFormat format = new DecimalFormat("###.####");
        StringBuilder buf = new StringBuilder(30);
        buf.append("RMatrix[");
        buf.append(rows);
        buf.append(",");
        buf.append(columns);
        buf.append("][");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                buf.append(format.format(coefficients[i][j]));
                if (j < columns-1) { buf.append(" "); }
            }
            if (i < rows-1) { buf.append("; "); }
        }
        buf.append("]");
        return buf.toString();
    }
    

    private int[][] coefficients;
    private int modulus;
}
