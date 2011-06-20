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

import org.rubato.math.matrix.ZMatrix;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in a free <i>Z</i> module.
 * The morphism <i>h</i> is such that <i>h(x) = A*x+b</i>
 * where <i>A</i> is an integer matrix and <i>b</i> is an integer vector.
 * 
 * @author Gérard Milmeister
 */
public final class ZFreeAffineMorphism extends ZFreeAbstractMorphism {

    public static ModuleMorphism make(ZMatrix A, int[] b) {
        if (A.getColumnCount() == 1 && A.getRowCount() == 1 && b.length == 1) {
            return new ZAffineMorphism(A.get(0, 0), b[0]);
        }
        else {
            return new ZFreeAffineMorphism(A, b);
        }
    }

    
    private ZFreeAffineMorphism(ZMatrix A, int[] b) {
        super(A.getColumnCount(), A.getRowCount());
        if (A.getRowCount() != b.length) {
            throw new IllegalArgumentException("Rows of A ("
                                        	   +A.getRowCount()
                    		                   +") don't match length of b ("
                    		                   +b.length
                    		                   +")");
        }
        this.A = A;
        this.b = b;
    }


    public int[] mapValue(int[] x) {
        int[] res;
        res = A.product(x);
        for (int i = 0; i < res.length; i++) {
            res[i] += b[i];
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
        if (morphism instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism zmorphism = (ZFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == zmorphism.getCodomain().getDimension()) {
                ZMatrix resA = A.product(zmorphism.A);
                int[] resb = A.product(zmorphism.b);
                for (int i = 0; i < resb.length; i++) {
                    resb[i] += b[i];
                }
                return new ZFreeAffineMorphism(resA, resb);
            }
        }
        return super.compose(morphism);
    }

    
    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism zmorphism = (ZFreeAffineMorphism)morphism;
            if (getDomain().getDimension() == zmorphism.getDomain().getDimension() &&
                    getCodomain().getDimension() == zmorphism.getCodomain().getDimension()) {
                ZMatrix resA = A.sum(zmorphism.A);
                int[] resb = new int[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i] + zmorphism.b[i];
                }
                return new ZFreeAffineMorphism(resA, resb);
            }
        }
        return super.sum(morphism);
    }
    

    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism zmorphism = (ZFreeAffineMorphism) morphism;
            if (getDomain().getDimension() == zmorphism.getDomain().getDimension() &&
                    getCodomain().getDimension() == zmorphism.getCodomain().getDimension()) {
                ZMatrix resA = A.sum(zmorphism.A);
                int[] resb = new int[b.length];
                for (int i = 0; i < resb.length; i++) {
                    resb[i] = b[i] - zmorphism.b[i];
                }
                return new ZFreeAffineMorphism(resA, resb);
            }
        }
        return super.difference(morphism);
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism morphism = (ZFreeAffineMorphism)object;
            int comp = A.compareTo(morphism.A);
            if (comp == 0) {
                for (int i = 0; i < b.length; i++) {
                    int d = b[i]-morphism.b[i];
                    if (d != 0) {
                        return d;
                    }
                }
                return 0;
            }
            else {
                return comp;
            }
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism morphism = (ZFreeAffineMorphism)object;
            return A.equals(morphism.A) && Arrays.equals(b, morphism.b);
        }
        else {
            return false;
        }
    }
    
    
    private final static String A_ATTR = "A";
    private final static String B_ATTR = "b";

    
    public String toString() {
        return "ZFreeAffineMorphism["+getDomain().getDimension()+","+getCodomain().getDimension()+"]";
    }

    
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

        ZMatrix A0 = new ZMatrix(rows, columns);
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
        
        return new ZFreeAffineMorphism(A0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = ZFreeAffineMorphism.make(ZMatrix.getUnitMatrix(0), new int[0]);
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ZFreeAffineMorphism";
    }
    
    
    /**
     * Returns the linear part.
     */
    public ZMatrix getMatrix() {
        return A;
    }
    

    /**
     * Returns the translation part.
     */
    public int[] getVector() {
        return b;
    }
    
    
    private final ZMatrix A;
    private final int[] b;
}
