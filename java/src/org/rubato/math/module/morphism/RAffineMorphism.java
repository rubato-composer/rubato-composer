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

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RingElement;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Affine morphism in <i>R</i>.
 * The morphism <i>h</i> is such that <i>h(x) = a*x+b</i>
 * where <i>a</i> and <i>b</i> are reals.
 * 
 * @author Gérard Milmeister
 */
public final class RAffineMorphism extends RAbstractMorphism {

    public RAffineMorphism(double a, double b) {
        super();
        this.a = a;
        this.b = b;
    }

    
    public double mapValue(double x) {
        return a*x+b;
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }

    
    public boolean isRingHomomorphism() {
        return (b == 0) && (a == 1 || a == 0);
    }
    
    
    public boolean isLinear() {
        return b == 0;
    }

    
    public boolean isIdentity() {
        return (a == 1) && (b == 0);
    }
    
    
    public boolean isConstant() {
        return a == 0;
    }
    
    
    public ModuleMorphism compose(ModuleMorphism morphism)
        	throws CompositionException {
        if (morphism instanceof RAffineMorphism) {
            RAffineMorphism rmorphism = (RAffineMorphism) morphism;
            return new RAffineMorphism(a * rmorphism.a, a * rmorphism.b + b);
        }
        else {
            return super.compose(morphism);
        }
    }

    
    public ModuleMorphism sum(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof RAffineMorphism) {
            RAffineMorphism rmorphism = (RAffineMorphism) morphism;
            return new RAffineMorphism(a + rmorphism.a, b + rmorphism.b);
        }
        else {
            return super.sum(morphism);
        }
    }

    
    public ModuleMorphism difference(ModuleMorphism morphism)
    		throws CompositionException {
        if (morphism instanceof RAffineMorphism) {
            RAffineMorphism rmophism = (RAffineMorphism)morphism;
            return new RAffineMorphism(a - rmophism.a, b - rmophism.b);
        }
        else {
            return super.difference(morphism);
        }
    }

    
    public ModuleMorphism scaled(RingElement element)
        throws CompositionException {
        if (element instanceof RElement) {
            double s = ((RElement)element).getValue();
            if (s == 0.0) {
                return getConstantMorphism(element);
            }
            else {
                return new RAffineMorphism(getA()*s, getB()*s);
            }
        }
        else {
            throw new CompositionException("RAffineMorphism.scaled: Cannot scale "+this+" by "+element);
        }
    }


    public ModuleElement atZero() {
        return new RElement(getB());
    }


    public int compareTo(ModuleMorphism object) {
        if (object instanceof RAffineMorphism) {
            RAffineMorphism morphism = (RAffineMorphism)object;
            if (a == morphism.a) {
                if (b < morphism.b) {
                    return -1;
                }
                else if (b > morphism.b) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
            else if (a < morphism.a) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public boolean equals(Object object) {
        if (object instanceof RAffineMorphism) {
            RAffineMorphism morphism = (RAffineMorphism)object;
            return a == morphism.a && b == morphism.b;
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "RAffineMorphism["+a+","+b+"]";
    }

    
    private final static String A_ATTR = "a";
    private final static String B_ATTR = "b";

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULEMORPHISM, getElementTypeName(), A_ATTR, a, B_ATTR, b);
    }

    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(A_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), A_ATTR);
            return null;            
        }
        if (!element.hasAttribute(B_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), B_ATTR);
            return null;            
        }

        double a0;
        double b0;
        try {
            a0 = Double.parseDouble(element.getAttribute(A_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be a real number.", A_ATTR, getElementTypeName());
            return null;            
        }
        try {
            b0 = Double.parseDouble(element.getAttribute(B_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be a real number.", B_ATTR, getElementTypeName());
            return null;            
        }
        
        return new RAffineMorphism(a0, b0);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = new RAffineMorphism(0, 0);
    
    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "RAffineMorphism";
    }
    
    
    /**
     * Returns the linear part.
     */
    public double getA() {
        return a;
    }
    

    /**
     * Returns the translation part.
     */
    public double getB() {
        return b;
    }
    
    
    private double a;
    private double b;
}
