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

package org.rubato.math.module.morphism;

import static org.rubato.xml.XMLConstants.*;

import java.util.Arrays;

import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.QMatrix;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in a free <i>Q</i>-module.
 * The morphism <i>h</i> is such that <i>h(x) = A*x+b</i> where <i>A</i> is a 
 * rational matrix and <i>b</i> is a rational vector.
 * 
 * @author Gérard Milmeister
 */
public final class QFreeAffineMorphism extends QFreeAbstractMorphism {

    public static ModuleMorphism make(QMatrix A, Rational[] b) {
        if (A.getColumnCount() == 1 && A.getRowCount() == 1 && b.length == 1) {
            return new QAffineMorphism(A.get(0, 0), b[0]);
        }
        else {
            return new QFreeAffineMorphism(A, b);
        }
    }

    
    private QFreeAffineMorphism(QMatrix A, Rational[] b) {
        super(A.getColumnCount(), A.getRowCount());
        if (A.getRowCount() != b.length) {
            throw new IllegalArgumentException("Rows of A don't match length of b");
        }
        this.A = A;
        this.b = b;
    }

    
    public Rational[] mapValue(Rational[] rv) {
        Rational[] res;
        res = A.product(rv);
        for (int i = 0; i < res.length; i++) {
            res[i].add(b[i]);
        }
        return res;
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }
    
    
    public boolean isLinear() {
        for (int i = 0; i < b.length; i++) {
            if (!b[i].isZero()) {
                return false;
            }
        }
        return true;
    }
    
    
    public boolean isIdentity() {
        return isLinear() && A.isUnit();
    }

    
    public boolean isConstant() {
        return A.isZero();
    }
    
    
    public ModuleMorphism compose(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism qm = (QFreeAffineMorphism)morphism;
            if (getDomain().getDimension() == qm.getCodomain().getDimension()) {
                QMatrix resA = A.product(qm.A);
                Rational[] resb = A.product(qm.b);
                for (int i = 0; i < resb.length; i++) {
                    resb[i].add(b[i]);
                }
                return new QFreeAffineMorphism(resA, resb);
            }
        }
        return super.compose(morphism);
    }

    
    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism qm = (QFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == qm.getDomain().getDimension() &&
                    getCodomain().getDimension() == qm.getCodomain().getDimension()) {
                QMatrix resA = A.sum(qm.A);
                Rational[] resb = new Rational[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i].sum(qm.b[i]);
                }
                return new QFreeAffineMorphism(resA, resb);
            }
        }
        return super.sum(morphism);
    }

    
    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism qm = (QFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == qm.getDomain().getDimension() &&
                    getCodomain().getDimension() == qm.getCodomain().getDimension()) {
                QMatrix resA = A.difference(qm.A);
                Rational[] resb = new Rational[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i].difference(qm.b[i]);
                }
                return new QFreeAffineMorphism(resA, resb);
            }
        }
        return super.difference(morphism);
    }


    public int compareTo(ModuleMorphism object) {
        if (object instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism morphism = (QFreeAffineMorphism)object;
            int comp = A.compareTo(morphism.A);
            if (comp == 0) {
                for (int i = 0; i < b.length; i++) {
                    int comp1 = b[i].compareTo(morphism.b[i]);
                    if (comp1 != 0) {
                        return comp1;
                    }
                }
                return 0;
            }
            else
                return comp;
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism morphism = (QFreeAffineMorphism)object;
            return A.equals(morphism.A) && Arrays.equals(b, morphism.b);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "QFreeAffineMorphism["+getDomain().getDimension()+","+getCodomain().getDimension()+"]";
    }
    
    
    private final static String A_ATTR = "A";
    private final static String B_ATTR = "b";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName(),
                                 ROWS_ATTR, A.getRowCount(),
                                 COLUMNS_ATTR, A.getColumnCount());
        String s = "";
        for (int i = 0; i < A.getRowCount(); i++) {
            for (int j = 0; j < A.getColumnCount(); j++) {
                if (i != 0 || j != 0) {
                    s += ",";
                }
                s += A.get(i, j);
            }
        }
        writer.openInline(A_ATTR);
        writer.text(s);
        writer.closeInline();
        s = b[0].toString();
        for (int i = 1; i < b.length; i++) {
            s += ","+b[i];
        }
        writer.openInline(B_ATTR);
        writer.text(s);
        writer.closeInline();        
        writer.closeBlock();
    }
        
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));

        if (!element.hasAttribute(ROWS_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), ROWS_ATTR);
            return null;
        }
        int rows;
        try {
            rows = Integer.parseInt(element.getAttribute(ROWS_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", ROWS_ATTR, getElementTypeName());
            return null;
        }

        if (!element.hasAttribute(COLUMNS_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), COLUMNS_ATTR);
            return null;
        }
        int columns;
        try {
            columns = Integer.parseInt(element.getAttribute(COLUMNS_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", COLUMNS_ATTR, getElementTypeName());
            return null;
        }
        
        final int numberCount = rows*columns;
        Element aElement = XMLReader.getChild(element, A_ATTR);
        if (aElement == null) {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), A_ATTR);
            return null;            
        }
        String[] numbers = aElement.getTextContent().trim().split(",");
        if (numbers.length != numberCount) {
            reader.setError("Element <%1> must have a comma-separated list with %2 rational numbers.", A_ATTR, numberCount);
            return null;
        }

        QMatrix A0 = new QMatrix(rows, columns);
        try {
            int n = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    A0.set(i, j, Rational.parseRational(numbers[n]));
                    n++;
                }
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 rational numbers.", A_ATTR, numberCount);
            return null;            
        }

        Element bElement = XMLReader.getChild(element, B_ATTR);
        if (bElement == null) {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), B_ATTR);
            return null;            
        }
        numbers = bElement.getTextContent().trim().split(",");
        if (numbers.length != rows) {
            reader.setError("Element <%1> must have a comma-separated list with %2 rational numbers.", B_ATTR, rows);
            return null;
        }
        
        Rational b0[]= new Rational[rows];
        try {
            for (int i = 0; i < rows; i++) {
                b0[i] = Rational.parseRational(numbers[i]);
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 rational numbers.", B_ATTR, rows);
            return null;            
        }
        
        return new QFreeAffineMorphism(A0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = QFreeAffineMorphism.make(QMatrix.getUnitMatrix(0), new Rational[0]);
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "QFreeAffineMorphism";
    }
    
    
    /**
     * Returns the linear part.
     */
    public QMatrix getMatrix() {
        return A;
    }
    

    /**
     * Returns the translation part.
     */
    public Rational[] getVector() {
        return b;
    }
    
    
    private QMatrix    A;
    private Rational[] b;
}
