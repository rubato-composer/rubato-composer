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

import org.rubato.math.arith.Complex;
import org.rubato.math.matrix.CMatrix;
import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in a free <i>C</i>-module.
 * The morphism h is such that <i>h(x) = A*x+b</i> where <i>A</i>
 * is a complex matrix and <i>b</i> is a complex vector.
 * 
 * @author Gérard Milmeister
 */
public final class CFreeAffineMorphism extends CFreeAbstractMorphism {

    public static ModuleMorphism make(CMatrix A, Complex[] b) {
        if (A.getColumnCount() == 1 && A.getRowCount() == 1 && b.length == 1) {
            return new CAffineMorphism(A.get(0, 0), b[0]);
        }
        else {
            return new CFreeAffineMorphism(A, b);
        }
    }

    
    private CFreeAffineMorphism(CMatrix A, Complex[] b) {
        super(A.getColumnCount(), A.getRowCount());
        if (A.getRowCount() != b.length) {
            throw new IllegalArgumentException("Rows of A don't match length of b.");
        }
        this.A = A;
        this.b = b;
    }

    
    public Complex[] mapValue(Complex[] rv) {
        Complex[] res;
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
        if (morphism instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism qm = (CFreeAffineMorphism)morphism;
            if (getDomain().getDimension() == qm.getCodomain().getDimension()) {
                CMatrix resA = A.product(qm.A);
                Complex[] resb = A.product(qm.b);
                for (int i = 0; i < resb.length; i++) {
                    resb[i].add(b[i]);
                }
                return new CFreeAffineMorphism(resA, resb);
            }
        }
        return super.compose(morphism);
    }

    
    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism qm = (CFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == qm.getDomain().getDimension() &&
                    getCodomain().getDimension() == qm.getCodomain().getDimension()) {
                CMatrix resA = A.sum(qm.A);
                Complex[] resb = new Complex[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i].sum(qm.b[i]);
                }
                return new CFreeAffineMorphism(resA, resb);
            }
        }
        return super.sum(morphism);
    }

    
    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism qm = (CFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == qm.getDomain().getDimension() &&
                    getCodomain().getDimension() == qm.getCodomain().getDimension()) {
                CMatrix resA = A.difference(qm.A);
                Complex[] resb = new Complex[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i].difference(qm.b[i]);
                }
                return new CFreeAffineMorphism(resA, resb);
            }
        }
        return super.difference(morphism);
    }


    public ModuleMorphism scaled(RingElement element)
            throws CompositionException {
        if (element instanceof CElement) {
            Complex s = ((CElement)element).getValue();
            if (s.isZero()) {
                return getConstantMorphism(getCodomain().getZero());
            }
            else {
                Complex[] oldv = getVector();
                Complex[] newv = new Complex[oldv.length];
                for (int i = 0; i < oldv.length; i++) {
                    newv[i] = oldv[i].product(s);
                }
                return new CFreeAffineMorphism(getMatrix().scaled(s), newv);
            }
        }
        else {
            throw new CompositionException("CAffineMorphism.scaled: Cannot scale "+this+" by "+element);
        }
    }


    public ModuleElement atZero() {
        return CProperFreeElement.make(getVector());
    }


    public int compareTo(ModuleMorphism object) {
        if (object instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism morphism = (CFreeAffineMorphism)object;
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
        if (object instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism morphism = (CFreeAffineMorphism)object;
            return A.equals(morphism.A) && Arrays.equals(b, morphism.b);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "CFreeAffineMorphism["+getDomain().getDimension()+","+getCodomain().getDimension()+"]";
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
            reader.setError("Element <%1> must have a comma-separated list with %2 complex numbers.", A_ATTR, numberCount);
            return null;
        }

        CMatrix A0 = new CMatrix(rows, columns);
        try {
            int n = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    A0.set(i, j, Complex.parseComplex(numbers[n]));
                    n++;
                }
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 complex numbers.", A_ATTR, numberCount);
            return null;            
        }

        Element bElement = XMLReader.getChild(element, "b");
        if (bElement == null) {
            reader.setError("Type %%1 is missing child of type <b>.", getElementTypeName());
            return null;            
        }
        numbers = bElement.getTextContent().trim().split(",");
        if (numbers.length != rows) {
            reader.setError("Element <%1> must have a comma-separated list with %2 complex numbers.", B_ATTR, rows);
            return null;
        }
        
        Complex b0[]= new Complex[rows];
        try {
            for (int i = 1; i <= rows; i++) {
                b0[i] = Complex.parseComplex(numbers[i]);
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 complex numbers.", B_ATTR, rows);
            return null;            
        }
        
        return new CFreeAffineMorphism(A0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = CFreeAffineMorphism.make(CMatrix.getUnitMatrix(0), new Complex[0]);
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "CFreeAffineMorphism";
    }
    
    
    /**
     * Returns the linear part.
     */
    public CMatrix getMatrix() {
        return A;
    }
    

    /**
     * Returns the translation part. 
     */
    public Complex[] getVector() {
        return b;
    }
    
    
    private CMatrix   A;
    private Complex[] b;
}
