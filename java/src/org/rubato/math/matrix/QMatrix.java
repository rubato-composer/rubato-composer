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

import org.rubato.math.arith.Rational;

/**
 * Matrixes over rational numbers.
 */
public class QMatrix extends Matrix {
    
    /**
     * Creates a rational <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to 0.
     */
    public QMatrix(int rows, int cols) {
        super(rows, cols);
        coefficients = makeArray(rows, columns);
    }
    
    
    /**
     * Creates a rational <code>rows</code> x <code>cols</code> matrix
     * with all coefficients set to <code>value</code>.
     */
    public QMatrix(int rows, int cols, Rational value) {
        this(rows, cols);
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }
    
    
    /**
     * Creates a copy of the rational matrix <code>m</code>.
     */
    public QMatrix(QMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = new Rational(m.coefficients[r][c]);
            }
        }
    }
    
    
    /**
     * Creates a rational matrix copy of the integer matrix <code>m</code>.
     */
    public QMatrix(ZMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = new Rational(m.get(r,c));
            }
        }
    }
    
    
    /**
     * Creates a rational matrix copy of the modular integer matrix <code>m</code>.
     */
    public QMatrix(ZnMatrix m) {
        this(m.getRowCount(), m.getColumnCount());
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                coefficients[r][c] = new Rational(m.get(r,c));
            }
        }
    }
    
    
    /**
     * Creates a <i>n</i>x1 rational matrix from the vector <code>v</code>,
     * where <i>n</i> is the length of <code>v</code>.
     */
    public QMatrix(Rational[] v) {
        this(v.length, 1);
        for (int r = 0; r < v.length; r++) {
            coefficients[r][0] = v[r];
        }
    }

    
    /**
     * Creates a rational matrix from the two-dimensional array <code>c</code>.
     */
    public QMatrix(Rational[][] c) {
        super(c.length, c[0].length);
        for (int i = 1; i < rows; i++) {
            if (c[i].length != columns) {
                throw new IllegalArgumentException("Rows are not all of the same length.");
            }
        }
        this.coefficients = c;
    }
    
    
    /**
     * Returns the unit complex matrix of the given <code>size</code>.
     */
    public static QMatrix getUnitMatrix(int size) {
        QMatrix res = new QMatrix(size, size);
        for (int i = 0; i < size; i++) {
            res.set(i, i, Rational.getOne());
        }
        return res;
    }

    
    /**
     * Returns the value at index <code>row</code>,<code>col</code>.
     */
    public Rational get(int row, int col) {
        return coefficients[row][col];
    }
    
    
    /**
     * Sets index <code>row</code>,<code>col</code> to <code>value</code>.
     */
    public void set(int row, int col, Rational value) {
        coefficients[row][col] = value;
    }
    
    
    public void setToZero(int row, int col) {
        coefficients[row][col] = Rational.getZero();
    }

    
    public void setToOne(int row, int col) {
        coefficients[row][col] = Rational.getOne();
    }

    
    public void setRowCount(int rows) {
        if (this.rows == rows) {
            return;
        }
        Rational[][] coeffs = makeArray(rows, columns);
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
        Rational[][] coeffs = makeArray(rows, cols);
        int min_cols = cols < this.columns ? cols : this.columns;
        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.coefficients[r], 0, coeffs[r], 0, min_cols);
        }
        this.columns = cols;
        this.coefficients = coeffs;
    }
    

    public void setToZeroMatrix() {
        setToElementaryMatrix(Rational.getZero());
    }

    
    public void setToUnitMatrix() {
        if (rows > columns) {
            columns = rows;
        }
        else if (rows < columns) {
            rows = columns;
        }
        coefficients = makeArray(rows, rows);
        for (int rc = 0; rc < rows; rc++) {
            coefficients[rc][rc] = Rational.getOne();
        }
    }
    
    
    /**
     * Sets all values of this matrix to <code>value</code>.
     */
    public void setToElementaryMatrix(Rational value) {
        for (int r = 0; r < rows; r++) {
            Arrays.fill(coefficients[r], value);
        }
    }


    /**
     * Returns the submatrix containing all rows from <code>fromRow</code>
     * to <code>toRow</code> inclusive, and from <code>fromCol</code> to
     * <code>toCol</code> inclusive.
     */
    public QMatrix getSubMatrix(int fromRow, int toRow, int fromCol, int toCol) {
        int nrows = toRow-fromRow+1;
        int ncols = toCol-fromCol+1;
        QMatrix m = new QMatrix(nrows, ncols);
        for (int r = 0; r < nrows; r++) {
            System.arraycopy(coefficients[r+fromRow], fromCol, m.coefficients[r], 0, ncols);
        }
        return m;
    }
    
    
    /**
     * Returns the matrix containing all rows and all columns of
     * this matrix except <code>row</code> and <code>col</code>.
     */
    public QMatrix getMinorMatrix(int row, int col) {
        QMatrix m = new QMatrix(rows-1, columns-1);
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
    public QMatrix transposed() {
        QMatrix m = new QMatrix(columns, rows);
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
    public QMatrix inverse() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        int n = rows;
        int i, j, k, t;
        Rational maxval;
        int maxpos;
        int[] p = new int[n];
        for (i = 0; i < n; i++) { 
            p[i] = i;
        }
        
        Rational[][] m = makeArray(n, 2*n);
        for (i = 0; i < n; i++) {
            System.arraycopy(coefficients[i], 0, m[i], 0, n);
            for (j = n; j < 2*n; j++) m[i][j] = Rational.getZero();
            m[i][n+i] = Rational.getOne();
        }

        for (i = 0; i < n; i++) {
            maxpos = i;
            maxval = m[p[i]][i].abs();
            for (j = i+1; j < n; j++) {
                if (m[p[j]][i].abs().compareTo(maxval) > 0) {
                    maxval = m[p[j]][i].abs();
                    maxpos = j;
                }
            }
            if (maxval.isZero()) { 
                throw new ArithmeticException("Matrix is not invertible.");
            }
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
            }
            
            for (j = i+1; j < n; j++) {
                Rational f = m[p[j]][i].quotient(m[p[i]][i]);
                for (k = i; k < 2*n; k++) {
                    m[p[j]][k].subtract(f.product(m[p[i]][k]));
                }
            }
        }
        
        if (m[p[n-1]][n-1].isZero()) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
            
        for (i = n-1; i >= 0; i--) {
            Rational f = m[p[i]][i];
            for (k = i; k < 2*n; k++) {
                m[p[i]][k].divide(f);
            }
            for (j = i-1; j >= 0; j--) {
                f = m[p[j]][i];
                for (k = i; k < 2*n; k++) {
                    m[p[j]][k].subtract(f.product(m[p[i]][k]));
                }
            }
        }

        QMatrix rm = new QMatrix(n, n);
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                rm.coefficients[i][j] = m[p[i]][j+n];
        
        return rm;
    }

    
    /**
     * Returns the adjoint of this matrix.
     */
    public QMatrix adjoint() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        QMatrix m = new QMatrix(rows, columns);
        if (rows == 1) {
            m.coefficients[0][0] = Rational.getOne();
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

    
    public QMatrix affineDifference() {
        if (columns == 1) {
            return this;
        }
        
        QMatrix m = new QMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = coefficients[r][c].difference(coefficients[r][0]);
            }
        }
        return m;
    }
    
    
    /**
     * Returns the quadratic form of this matrix.
     */
    public QMatrix quadraticForm() {
        QMatrix m = transposed();
        return m.product(this);
    }
    
    
    /**
     * Returns this matrix scaled by <code>scalar</code>.
     */
    public QMatrix scaled(Rational scalar) {
        QMatrix m = new QMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                m.coefficients[r][c] = scalar.product(coefficients[r][c]);
            }
        }
        return m;
    }
    
    
    /**
     * Returns this matrix raised to <code>exponent</code>.
     * 
     * @throws ArithmeticException if the matrix is not square
     */
    public QMatrix power(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }

        if (exponent == 1) {
            return this;
        }
            
        QMatrix m = null;
        
        if (exponent == 0) {
            m = new QMatrix(rows, columns);
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
    
    
    public QMatrix taylor(int exponent) {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        QMatrix m = null;
        if (exponent < 0) {
            exponent *= -1;
            m = inverse();
        }
        else {
            m = this;
        }
        
        QMatrix n = new QMatrix(rows, columns);
        n.setToUnitMatrix();
        
        for (int i = 1, fac = 1; i <= exponent; i++, fac*=i) {
            n = n.sum(m.power(i)).scaled(Rational.getOne().quotient(fac));
        }
        return n;
    }
    

    public int rank() {
        QMatrix m;        
        if (getRowCount() < getColumnCount()) {
            m = transposed();
        }
        else {
            m = new QMatrix(this);
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
        Rational coeff = coefficients[0][0];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!coefficients[r][c].equals(coeff)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    public boolean isZero() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!coefficients[r][c].isZero()) { 
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
                    if (!coefficients[r][c].isOne()) {
                        return false;
                    }
                }
                else {
                    if (!coefficients[r][c].isZero()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    
    public boolean isRegular() {
        return determinant().equals(Rational.getZero());
    }

    
    public boolean isZero(int row, int col) {
        return coefficients[row][col].isZero();
    }
    
    
    public boolean isOne(int row, int col) {
        return coefficients[row][col].isOne();
    }
    

    public Rational dotProduct(QMatrix m) {
        if (columns != 1 || m.columns != 1) {
            throw new ArithmeticException("Matrix is not a vector.");
        }
        
        Rational dot = Rational.getZero();
        for (int r = 0; r < rows; r++) {
            dot.add(coefficients[r][0].product(m.coefficients[r][0]));
        }
        return dot;
    }
    

    public boolean equals(Object object) {
        if (object instanceof QMatrix) {
            QMatrix m = (QMatrix)object;
            if (sameSize(m)) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (!coefficients[i][j].equals(m.coefficients[i][j])) {
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
        if (object instanceof QMatrix) {
            QMatrix m = (QMatrix)object;
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
                    int cmp = coefficients[r][c].compareTo(coefficients[r][c]);
                    if (cmp != 0) {
                        return cmp;
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
    public void setSubMatrix(int row, int col, QMatrix m) {
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
            Rational max_val = coefficients[i][j];
            int max_ind = i;
            for (int k = i+1; k < m; k++) {
                Rational val = coefficients[k][j];
                if (val.compareTo(max_val) == 1) {
                    max_val = val;
                    max_ind = k;
                }
            }
            if (!max_val.isZero()) {
                // switch rows i and max_ind
                Rational[] tmp = coefficients[i];
                coefficients[i] = coefficients[max_ind];
                coefficients[max_ind] = tmp;
                // divide row i by max_val
                for (int k = 0; k < n; k++) {
                    coefficients[i][k].divide(max_val);
                }
                for (int u = 0; u < m; u++) {
                    if (u != i) {
                        Rational v = new Rational(coefficients[u][j]);
                        for (int k = 0; k < n; k++) {
                            coefficients[u][k].subtract(v.product(coefficients[i][k]));
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
    public QMatrix sum(QMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        QMatrix sum = new QMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c]= coefficients[r][c].sum(m.coefficients[r][c]);
            }
        }
        return sum;
    }

    
    /**
     * Returns the difference of this matrix and <code>m</code>.
     */
    public QMatrix difference(QMatrix m) {
        if (!sameSize(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        QMatrix sum = new QMatrix(rows, columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.coefficients[r][c] = coefficients[r][c].difference(m.coefficients[r][c]);
            }
        }
        return sum;
    }
    
    
    /**
     * Returns the product of this matrix and <code>m</code>.
     */
    public QMatrix product(QMatrix m) {
        if (!productPossible(m)) {
            throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        
        QMatrix product = new QMatrix(rows, m.columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < m.columns; c++) {
                Rational sum = Rational.getZero();
                for (int i = 0; i < columns; i++) {
                    sum.add(coefficients[r][i].product(m.coefficients[i][c]));
                }
                product.coefficients[r][c] = sum;
            }
        }
        return product;
    }
    

    /**
     * Returns the product of this matrix with <code>vector</code>.
     */
    public Rational[] product(Rational[] vector) {
        if (columns != vector.length) {
	       throw new ArithmeticException("Unmatched matrix dimensions.");
        }
        Rational[] res = new Rational[rows];
        Rational sum;
        for (int r = 0; r < rows; r++) {
	        sum = Rational.getZero();
	        for (int c = 0; c < columns; c++) {
		        sum.add(coefficients[r][c].product(vector[c]));
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
    public Rational determinant() {
        if (!isSquare()) {
            throw new ArithmeticException("Matrix is not square.");
        }
        
        if (rows == 1) {
            return coefficients[0][0];
        }
                
        int i, j, t;
        Rational maxval;
        int maxpos;
        Rational factor = Rational.getOne();
        int[] p = new int[rows];
        for (i = 0; i < rows; i++) p[i] = i;
        
        Rational[][] m = makeArray(rows, columns);
        for (i = 0; i < rows; i++)
            System.arraycopy(coefficients[i], 0, m[i], 0, columns);

        for (i = 0; i < rows; i++) {
            maxpos = i;
            maxval = m[p[i]][i].abs();
            for (j = i+1; j < rows; j++) {
                if (m[p[j]][i].abs().compareTo(maxval) > 0) {
                    maxval = m[p[j]][i].abs();
                    maxpos = j;
                }
            }
            if (maxval.isZero()) return Rational.getZero();
            if (maxpos != i) {
                t = p[maxpos]; p[maxpos] = p[i]; p[i] = t;
                factor.multiply(new Rational(-1));
            }
            
            for (j = i+1; j < rows; j++) {
                Rational f = m[p[j]][i].quotient(m[p[i]][i]);
                for (int k = i; k < columns; k++) {
                    m[p[j]][k].subtract(f.product(m[p[i]][k]));
                }
            }
        }
        
        Rational det = Rational.getOne();
        for (i = 0; i < rows; i++) {
            det.multiply(m[p[i]][i]);
        }
        
        return factor.product(det);
    }
    
    
    /**
     * Returns the minor at <code>row<code>,<code>col</code>.
     */
    public Rational minor(int row, int col) {
        QMatrix m = getMinorMatrix(row, col);
        if ((row+col)%2 == 0) {
            return m.determinant();
        }
        else {
            return m.determinant().negated();
        }
    }
    
    
    /**
     * Returns the Euclidean norm of this matrix.
     */
    public Rational euclidean() {
        Rational euclidean = Rational.getZero();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Rational s = coefficients[r][c];
                euclidean.add(s.product(s));
            }
        }        
        return euclidean;
    }
    
    
    /**
     * Returns the 1-norm of this matrix.
     */
    public Rational sum() {
        Rational sum = Rational.getZero();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                sum.add(coefficients[r][c]);
            }
        }
        return sum;
    }
    

    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("QMatrix[");
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
    
    
    private static Rational[][] makeArray(int rows, int columns) {
        Rational[][] res = new Rational[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                res[r][c] = Rational.getZero();
            }
        }
        return res;
    }
    

    private Rational[][] coefficients;
}
