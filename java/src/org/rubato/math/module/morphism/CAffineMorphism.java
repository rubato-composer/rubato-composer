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

import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.arith.Complex;
import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in <i>C</i>.
 * The morphism <code>h</code> is such that <i>h(x) = a*x+b</i>
 * where <code>a</code> and <code>b</code> are complex numbers.
 * 
 * @author Gérard Milmeister
 */
public final class CAffineMorphism extends CAbstractMorphism {

    /**
     * Constructs an affine morphism <i>h(x) = a*x+b</i>.
     */
    public CAffineMorphism(Complex a, Complex b) {
        super();
        this.a = a;
        this.b = b;
    }

    
    public Complex mapValue(Complex c) {
        return a.product(c).sum(b);
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }

    
    public boolean isRingHomomorphism() {
        return b.isZero() && (a.isOne() || b.isZero());
    }
    
    
    public boolean isLinear() {
        return b.isZero();
    }
    
    
    public boolean isIdentity() {
        return a.isOne() && b.isZero();
    }

    
    public boolean isConstant() {
        return a.isZero();
    }
    
    
    public ModuleMorphism compose(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof CAffineMorphism) {
            CAffineMorphism rm = (CAffineMorphism)morphism;
            return new CAffineMorphism(a.product(rm.a), a.product(rm.b).sum(b));
        }
        else {
            return super.compose(morphism);
        }
    }

    
    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof CAffineMorphism) {
            CAffineMorphism rm = (CAffineMorphism) morphism;
            return new CAffineMorphism(a.sum(rm.a), b.sum(rm.b));
        }
        else {
            return super.sum(morphism);
        }
    }
    

    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof CAffineMorphism) {
            CAffineMorphism rm = (CAffineMorphism) morphism;
            return new CAffineMorphism(a.difference(rm.a),b.difference(rm.b));
        }
        else {
            return super.difference(morphism);
        }
    }

    
    public ModuleMorphism scaled(RingElement element)
            throws CompositionException {
        if (element instanceof CElement) {
            Complex s = ((CElement)element).getValue();
            if (s.isZero()) {
                return getConstantMorphism(element);
            }
            else {
                return new CAffineMorphism(getA().product(s), getB().product(s));
            }
        }
        else {
            throw new CompositionException("CAffineMorphism.scaled: Cannot scale "+this+" by "+element);
        }
    }

    
    public ModuleElement atZero() {
        return new CElement(getB());
    }
    
    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof CAffineMorphism) {
            CAffineMorphism morphism = (CAffineMorphism)object;
            int comp = a.compareTo(morphism.a);
            if (comp == 0) {
                return b.compareTo(morphism.b);
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
        if (object instanceof CAffineMorphism) {
            CAffineMorphism morphism = (CAffineMorphism)object;
            return a.equals(morphism.a) && b.equals(morphism.b);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "CAffineMorphism["+a+","+b+"]";
    }
    
    
    private final static String A_ATTR = "a";
    private final static String B_ATTR = "b";

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULEMORPHISM, getElementTypeName(), A_ATTR, a, B_ATTR, b);
    }

    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute("a")) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), A_ATTR);
            return null;            
        }
        if (!element.hasAttribute("b")) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), B_ATTR);
            return null;            
        }

        Complex a0;
        Complex b0;
        try {
            a0 = Complex.parseComplex(element.getAttribute(A_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be a complex number.", A_ATTR, getElementTypeName());
            return null;            
        }
        try {
            b0 = Complex.parseComplex(element.getAttribute(B_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be a complex number.", B_ATTR, getElementTypeName());
            return null;            
        }
        
        return new CAffineMorphism(a0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = new CAffineMorphism(new Complex(0), new Complex(0));
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "CAffineMorphism";
    }

    
    /**
     * Returns the linear part.
     */
    public Complex getA() {
        return a;
    }
    

    /**
     * Returns the translation part.
     */
    public Complex getB() {
        return b;
    }
    
    
    private Complex a;
    private Complex b;
}
