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

import org.rubato.math.arith.NumberTheory;
import org.rubato.math.matrix.ZnMatrix;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in a free <i>Zn</i>-module.
 * The morphism <i>h</i> is such that <i>h(x) = A*x+b mod n</i>
 * where </i>A</i> is an integer matrix and <i>b</i> is an integer vector.
 * 
 * @author Gérard Milmeister
 */
public final class ZnFreeAffineMorphism extends ZnFreeAbstractMorphism {

    public static ModuleMorphism make(ZnMatrix A, int[] b) {
        if (A.getColumnCount() == 1 && A.getRowCount() == 1 && b.length == 1) {
            return new ZnAffineMorphism(A.get(0, 0), b[0], A.getModulus());
        }
        else {
            return new ZnFreeAffineMorphism(A, b);
        }
    }

    
    private ZnFreeAffineMorphism(ZnMatrix A, int[] b) {
        super(A.getColumnCount(), A.getRowCount(), A.getModulus());
        if (A.getRowCount() != b.length) {
            throw new IllegalArgumentException("Rows of A don't match length of b");
        }
        this.A = A;
        this.b = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            this.b[i] = NumberTheory.mod(b[i], getModulus());
        }
    }

    
    public int[] mapValue(int[] x) {
        int[] res;
        res = A.product(x);
        for (int i = 0; i < res.length; i++) {
            res[i] = NumberTheory.mod(res[i] + b[i], getModulus());
        }
        return res;
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }

    
    public boolean isLinear() {
        for (int i = 0; i < b.length; i++) {
            if (b[i] != 0) {
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
        if (morphism instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism zpm = (ZnFreeAffineMorphism) morphism;
            if (getModulus() == zpm.getModulus()) {
                if (getDomain().getDimension() == zpm.getCodomain().getDimension()) {
                    ZnMatrix resA = A.product(zpm.A);
                    int[] resb = A.product(zpm.b);
                    for (int i = 0; i < resb.length; i++) {
                        resb[i] = NumberTheory.mod(resb[i] + b[i], getModulus());
                    }
                    return new ZnFreeAffineMorphism(resA, resb);
                }
            }
        }
        return super.compose(morphism);
    }
    

    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism zpm = (ZnFreeAffineMorphism) morphism;
            if (getModulus() == zpm.getModulus()) {
                if (getDomain().getDimension() == zpm.getDomain().getDimension() &&
                        getCodomain().getDimension() == zpm.getCodomain().getDimension()) {
                    ZnMatrix resA = A.sum(zpm.A);
                    int[] resb = new int[b.length];
                    for (int i = 0; i < resb.length; i++) {
                        resb[i] = NumberTheory.mod(b[i] + zpm.b[i], getModulus());
                    }
                    return new ZnFreeAffineMorphism(resA, resb);
                }
            }
        }
        return super.sum(morphism);
    }

    
    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism zpm = (ZnFreeAffineMorphism) morphism;
            if (getModulus() == zpm.getModulus()) {
                if (getDomain().getDimension() == zpm.getDomain().getDimension() &&
                        getCodomain().getDimension() == zpm.getCodomain().getDimension()) {
                    ZnMatrix resA = A.sum(zpm.A);
                    int[] resb = new int[b.length];
                    for (int i = 0; i < resb.length; i++) {
                        resb[i] = NumberTheory.mod(b[i] - zpm.b[i], getModulus());
                    }
                    return new ZnFreeAffineMorphism(resA, resb);
                }
            }
        }
        return super.difference(morphism);
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism m = (ZnFreeAffineMorphism)object;
            int res = A.compareTo(m.A);
            if (res == 0) {
                for (int i = 0; i < b.length; i++) {
                    int d = b[i]-m.b[i];
                    if (d != 0) {
                        return d;
                    }
                }
                return 0;
            }
            else {
                return res;
            }
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism m = (ZnFreeAffineMorphism)object;
            return A.equals(m.A) && Arrays.equals(b, m.b);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "ZnFreeAffineMorphism["+getModulus()+"]["
               +getDomain().getDimension()+","
               +getCodomain().getDimension()+"]";
    }

    
    private final static String A_ATTR = "A";
    private final static String B_ATTR = "b";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName(),
                                 ROWS_ATTR, A.getRowCount(),
                                 COLUMNS_ATTR, A.getColumnCount(),
                                 MODULUS_ATTR, getModulus());
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
        s = Integer.toString(b[0]);
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
        
        int modulus;
        try {
            modulus = Integer.parseInt(element.getAttribute(MODULUS_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", MODULUS_ATTR, getElementTypeName());
            return null;
        }
        if (modulus < 2) {
            reader.setError("Attribute %%1 of type %%2 must be an integer > 1.", MODULUS_ATTR, getElementTypeName());
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
            reader.setError("Element <%1> must have a comma-separated list with %2 integers.", A_ATTR, numberCount);
            return null;
        }

        ZnMatrix A0 = new ZnMatrix(rows, columns, modulus);
        try {
            int n = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    A0.set(i, j, Integer.parseInt(numbers[n]));
                    n++;
                }
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 integers.", A_ATTR, numberCount);
            return null;            
        }

        Element bElement = XMLReader.getChild(element, B_ATTR);
        if (bElement == null) {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), B_ATTR);
            return null;            
        }
        numbers = bElement.getTextContent().trim().split(",");
        if (numbers.length != rows) {
            reader.setError("Element <%1> must have a comma-separated list with %2 integers.", B_ATTR, rows);
            return null;
        }
        
        int b0[]= new int[rows];
        try {
            for (int i = 0; i < rows; i++) {
                b0[i] = Integer.parseInt(numbers[i]);
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Element <%1> must have a comma-separated list with %2 integers.", B_ATTR, rows);
            return null;            
        }
        
        return new ZnFreeAffineMorphism(A0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = ZnFreeAffineMorphism.make(ZnMatrix.getUnitMatrix(0, 2), new int[0]);
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ZnFreeAffineMorphism";
    }
    
    
    /**
     * Returns the linear part.
     */
    public ZnMatrix getMatrix() {
        return A;
    }
    

    /**
     * Returns the translation part.
     */
    public int[] getVector() {
        return b;
    }
    
    
    private ZnMatrix A;
    private int[]    b;
}
