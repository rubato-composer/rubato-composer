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

/**
 * Matrixes over real numbers.
 */
public class RMatrix extends Matrix {

    /**
     * Creates a real <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to 0.
     */
    public RMatrix(int rows, int cols) {
        super(rows, cols);
        coefficients = new double[rows][cols];
    }
    
    
    /**
     * Creates a real <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to <code>value</code>.
     */
    public RMatrix(int rows, int cols, double value) {
        this(rows, cols);
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }
    
    
    /**
     * Creates a copy of the real matrix <code>m</code>.
     */
    public RMatrix(RMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.coefficients[r][c];
            }
        }
    }
    
    
    /**
     * Creates a real matrix copy of the integer matrix <code>m</code>.
     */
    public RMatrix(ZMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.get(r, c);
            }
        }
    }
    

    /**
     * Creates a real matrix copy of the modular integer matrix <code>m</code>.
     */
    public RMatrix(ZnMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = m.get(r, c);
            }
        }
    }
    

    /**
     * Creates a <i>n</i>x1 real matrix from the vector <code>v</code>,
     * where <i>n</i> is the length of <code>v</code>.
     */
    public RMatrix(double[] v) {
        this(v.length, 1);
        for (int r = 0; r < v.length; r++) {
            coefficients[r][0] = v[r];
        }
    }

    
    /**
     * Creates a <i>m</i>⨉<i>n</i> real matrix from the 2-dimensional
     * array <code>c</code> of doubles.
     */
    public RMatrix(double[][] c) {
        this(c.length, c[0].length);
        for (int i = 1; i < rows; i++) {
            if (c[i].length != columns) {
                throw new IllegalArgumentException("Rows are not all of the same length");
            }
        }
        this.coefficients = c;
    }
    

    /**
     * Returns the unit real matrix of the given <code>size</code>.
     */
    public static RMatrix getUnitMatrix(int size) {
        RMatrix res = new RMatrix(size, size);
        for (int i = 0; i < size; i++) {
            res.set(i, i, 1.0);
        }
        return res;
    }


    /**
     * Returns the value at index <code>row</code>,<code>col</code>.
     */
    public double get(int row, int col) {
        return coefficients[row][col];
    }
    
    
    /**
     * Sets index <code>row</code>,<code>col</code> to <code>value</code>.
     */
    public void set(int row, int col, double value) {
        coefficients[row][col] = value;
    }

    
    public void setRowCount(int rows) {
        if (this.rows == rows) {
            return;
        }
        double[][] coeffs = new double[rows][columns];
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
        double[][] coeffs = new double[rows][cols];
        int min_cols = cols < this.columns ? cols : this.columns;
        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.coefficients[r], 0, coeffs[r], 0, min_cols);
        }
        this.columns = cols;
        this.coefficients = coeffs;
    }
    

    public void setToZeroMatrix() {
        setToElementaryMatrix(0.0);
    }


    public void setToUnitMatrix() {
        if (rows > columns) {
            columns = rows;
        }
        else if (rows < columns) {
            rows = columns;
        }
        coefficients = new double[rows][rows];
        for (int rc = 0; rc < rows; rc++) {
            coefficients[rc][rc] = 1.0;
        }
    }
    
    
    public void setToZero(int row, int col) {
        coefficients[row][col] = 0;
    }


    public void setToOne(int row, int col) {
        coefficients[row][col] = 1;
    }

    
    /**
     * Sets all values of this matrix to <code>value</code>.
     */
    public void setToElementaryMatrix(double value) {
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }


    /**
     * Returns the submatrix containing all rows from <code>fromRow</code>
     * to <code>toRow</code> inclusive, and from <code>fromCol</code> to
     * <code>toCol</code> inclusive.
     */
    public RMatrix getSubMatrix(int fromRow, int toRow, int fromCol, int toCol) {
        int nrows = toRow-fromRow+1;
        int ncols = toCol-fromCol+1;
        RMatrix m = new RMatrix(nrows, ncols);
        for (int r = 0; r < nrows; r++) {
            System.arraycopy(coefficients[r+fromRow], fromCol, m.coefficients[r], 0, ncols);
        }
        return m;
    }
    
    
    /**
     * Returns the matrix containing all rows and all columns of
     * this matrix except <code>row</code> and <code>col</code>.
     */
    public RMatrix getMinorMatrix(int row, int col) {
        RMatrix m = new RMatrix(rows-1, columns-1);
        for (int r = 0, rOffset = 0; r < rows-1; r++) {
            if (r == row) rOffset = 1;
            for (int c = 0, cOffset = 0; c < columns-1; c++) {
                if (c == col) cOffset = 1;
                m.coefficients[r][c] = coefficients[r+rOffset][c+cOffset];
            }
        }
        return m;
    }
    

    /**
     * Returns the transpose of this matrix.
     */
    public RMatrix transposed() {
        RMatrix m = new RMatrix(columns, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[c][r] = coefficients[r][c];
            }
        }
        return m;
    }

    
    /**
     * Returns the inverse of this matrix, if it exists.
     * 
     * @throws ArithmeticException if inverse does not exist
     */
    public RMatrix inverse() {
        if (rows != columns) {
            throw new ArithmeticException("Matrix is not square.");
        }

        int n = rows;
        int i, j, k, t;
        double maxval;
        int maxpos;
        int[] p = new int[n];
        for (i = 0; i < n; i++) p[i] = i;
        
        double[][] m = new double[n][2*n];
        for (i = 0; i < n; i++) {
            System.arraycopy(coefficients[i], 0, m[i], 0, n);
            for (j = n; j < 2*n; j++) m[i][j] = 0.0;
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
            if (maxval == 0.0) { 
                throw new ArithmeticException("Matrix is not invertible.");
            }
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
            }
            
            for (j = i+1; j < n; j++) {
                double f = m[p[j]][i]/m[p[i]][i];
                for (k = i; k < 2*n; k++) m[p[j]][k] -= f*m[p[i]][k];
            }
        }
        
        if (m[p[n-1]][n-1] == 0.0) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
            
        for (i = n-1; i >= 0; i--) {
            double f = m[p[i]][i];
            for (k = i; k < 2*n; k++) m[p[i]][k] /= f;
            for (j = i-1; j >= 0; j--) {
                f = m[p[j]][i];
                for (k = i; k < 2*n; k++) m[p[j]][k] -= f*m[p[i]][k];
            }
        }

        RMatrix rm = new RMatrix(n, n);
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                rm.coefficients[i][j] = m[p[i]][j+n];
        
        return rm;
    }

    
    /**
     * Returns the adjoint of this matrix.
     */
    public RMatrix adjoint() {
        if (rows != columns) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        RMatrix m = new RMatrix(rows, columns);
        if (rows == 1) {
            m.coefficients[0][0] = 1.0;
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

    
    public RMatrix affineDifference() {
        if (columns == 1) {
            return this;
        }
        
        RMatrix m = new RMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = coefficients[r][c] - coefficients[r][0];
            }
        }
        return m;
    }
    
    
    /**
     * Returns the quadratic form of this matrix.
     */
    public RMatrix quadraticForm() {
        RMatrix m = transposed();
        return m.product(this);
    }
    

    /**
     * Returns this matrix scaled by <code>scalar</code>.
     */
    public RMatrix scaled(double scalar) {
        RMatrix m = new RMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = scalar*coefficients[r][c];
            }
        }
        return m;
    }
    

    /**
     * Returns this matrix raised to <code>exponent</code>.
     * 
     * @throws ArithmeticException if the matrix is not square
     */
    public RMatrix power(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        if (exponent == 1) {
            return this;
        }
            
        RMatrix m = null;
        
        if (exponent == 0) {
            m = new RMatrix(rows, columns);
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
        for (k = 2; (k <= exponent) && (exponent % k != 0); k++) {}
        if (k == exponent) {
            return product(power(k-1));
        }
        else {
            return power(exponent/k).power(k);
        }
    }
    
    
    public RMatrix taylor(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        RMatrix m = null;
        if (exponent < 0) {
            exponent *= -1;
            m = inverse();
        }
        else {
            m = this;
        }
        
        RMatrix n = new RMatrix(rows, columns);
        n.setToUnitMatrix();
        
        for (int i = 1, fac = 1; i <= exponent; i++, fac*=i) {
            n = n.sum(m.power(i)).scaled(1.0/fac);
        }
        return n;
    }
    

    public int rank() {
        RMatrix m;        
        if (getRowCount() < getColumnCount()) {
            m = transposed();
        }
        else {
            m = new RMatrix(this);
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
        double coeff = coefficients[0][0];
        
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
                if (java.lang.Math.abs(coefficients[r][c]) > EPSILON) { 
                    return false;
                }
            }
        }
        return true;
    }
    
    
    public boolean isUnit() {
        if (!isSquare()) {
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
        return Math.abs(determinant()) > EPSILON;
    }
    
    
    public boolean isZero(int row, int col) {
        return coefficients[row][col] == 0;
    }

    
    public boolean isOne(int row, int col) {
        return coefficients[row][col] == 1;
    }
    

    /**
     * Returns this matrix normalized, in case of a column matrix.
     * 
     * @throws ArithmeticException if this matrix is not a column matrix.
     */
    public RMatrix normalized() {
        if (columns != 1) {
            throw new ArithmeticException("Matrix is not a vector.");
        }
        double length = java.lang.StrictMath.sqrt(dotProduct(this));
        RMatrix m =  new RMatrix(rows, 1);
        for (int r = 0; r < rows; r++) {
            m.coefficients[r][0] = coefficients[r][0] / length;
        }
        return m;
    }
    
    
    /**
     * Returns the dot product of this matrix and <code>m</code>.
     * Both matrixes must be column matrixes of the same size.
     */
    public double dotProduct(RMatrix m) {
        if (columns != 1 || m.columns != 1) {
            throw new ArithmeticException("Matrix is not a vector.");
        }
        if (getRowCount() != m.getRowCount()) {
            throw new ArithmeticException("Matrixes do not have the same size.");
        }
        
        double dot = 0.0;
        for (int r = 0; r < rows; r++) {
            dot += coefficients[r][0] * m.coefficients[r][0];
        }
        return dot;
    }
    

    public boolean equals(Object object) {
        if (object instanceof RMatrix) {
            RMatrix m = (RMatrix)object;
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
        if (object instanceof RMatrix) {
            RMatrix m = (RMatrix)object;
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
                    double cmp = coefficients[r][c]-m.coefficients[r][c];
                    if (cmp < 0.0) {
                        return -1;
                    }
                    else if (cmp > 0.0) {
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
    

    /**
     * Sets the submatrix starting at <code>row</code>,<code>col</code>
     * to the matrix <code>m</code>.
     */
    public void setSubMatrix(int row, int col, RMatrix m) {
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
            double max_val = coefficients[i][j];
            int max_ind = i;
            for (int k = i+1; k < m; k++) {
                double val = coefficients[k][j];
                if (Math.abs(val) > Math.abs(max_val)) {
                    max_val = val;
                    max_ind = k;
                }
            }
            if (Math.abs(max_val) > EPSILON) {
                // switch rows i and max_ind
                double[] tmp = coefficients[i];
                coefficients[i] = coefficients[max_ind];
                coefficients[max_ind] = tmp;
                // divide row i by max_val
                for (int k = 0; k < n; k++) {
                    coefficients[i][k] /= max_val;
                }
                for (int u = 0; u < m; u++) {
                    if (u != i) {
                        double v = coefficients[u][j];
                        for (int k = 0; k < n; k++) {
                            coefficients[u][k] -= v*coefficients[i][k];
                        }
                    }
                }
                i++;
            }
            j++;
        }
    }
    
    
    /**
     * Returns the sum of this matrix and <code>m</code>.
     */
    public RMatrix sum(RMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        RMatrix sum = new RMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = coefficients[r][c] + m.coefficients[r][c];
            }
        }
        return sum;
    }

    
    /**
     * Returns the difference of this matrix and <code>m</code>.
     */
    public RMatrix difference(RMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        RMatrix sum = new RMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = coefficients[r][c] - m.coefficients[r][c];
            }
        }
        return sum;
    }
    
    
    /**
     * Returns the product of this matrix and <code>m</code>.
     */
    public RMatrix product(RMatrix m) {
        if (!productPossible(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        
        RMatrix product = new RMatrix(rows, m.columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < m.columns; c++) {
                double sum = 0.0;
                for (int i = 0; i < columns; i++) {
                    sum += coefficients[r][i]*m.coefficients[i][c];
                }
                product.coefficients[r][c] = sum;
            }
        }
        return product;
    }
    

    /**
     * Returns the product of this matrix with <code>vector</code>.
     */
    public double[] product(double[] vector) {
        if (columns != vector.length) {
	       throw new ArithmeticException("Unmatched matrix dimensions");
        }
        double[] res = new double[rows];
        double sum;
        for (int r = 0; r < rows; r++) {
	        sum = 0;
	        for (int c = 0; c < columns; c++) {
		        sum = sum + coefficients[r][c] * vector[c];
            }
	        res[r] = sum;
        }
        return res;
    }

    
    /**
     * Returns the determinant of this matrix.
     * 
     * @throws ArithmeticException if this matrix is not square
     */
    public double determinant() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        if (rows == 1) {
            return coefficients[0][0];
        }
                
        int i, j, t;
        double maxval;
        int maxpos;
        double det = 0.0;
        double factor = 1.0;
        int[] p = new int[rows];
        for (i = 0; i < rows; i++) p[i] = i;
        
        double[][] m = new double[rows][columns];
        for (i = 0; i < rows; i++)
            System.arraycopy(coefficients[i], 0, m[i], 0, columns);

        for (i = 0; i < rows; i++) {
            maxpos = i;
            maxval = Math.abs(m[p[i]][i]);
            for (j = i+1; j < rows; j++) {
                if (Math.abs(m[p[j]][i]) > maxval) {
                    maxval = Math.abs(m[p[j]][i]);
                    maxpos = j;
                }
            }
            if (maxval == 0.0) return 0.0;
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
                factor *= -1.0;
            }
            
            for (j = i+1; j < rows; j++) {
                double f = m[p[j]][i]/m[p[i]][i];
                for (int k = i; k < columns; k++) m[p[j]][k] -= f*m[p[i]][k];
            }
        }
        
        det = 1.0;
        for (i = 0; i < rows; i++) det *= m[p[i]][i];
        
        return factor*det;
    }
    
    
    /**
     * Returns the minor at <code>row<code>,<code>col</code>.
     */
    public double minor(int row, int col) {
        RMatrix m = getMinorMatrix(row, col);
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
    public double euclidean() {
        double euclidean = 0.0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                double s = coefficients[r][c];
                euclidean += s*s;
            }
        }        
        return euclidean;
    }
    
    
    /**
     * Returns the 1-norm of this matrix. 
     */
    public double sum() {
        double sum = 0.0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum += coefficients[r][c];
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
    

    private double[][] coefficients;
    
    static final double EPSILON = 1.0e-6;
}
