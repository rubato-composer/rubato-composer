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

package org.rubato.math.module;

import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;
import static org.rubato.xml.XMLConstants.VALUE_ATTR;

import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Folding;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in the field of complex numbers.
 * @see org.rubato.math.module.CRing
 * 
 * @author Gérard Milmeister
 */
public final class CElement extends RingElement implements CFreeElement {

    /**
     * Constructs a CElement with complex number <code>value</code>.
     */
    public CElement(Complex value) {
        this.value = value;
    }


    /**
     * Constructs a CElement with complex (real) number <code>value</code>.
     */
    public CElement(double value) {
        this.value = new Complex(value);
    }

    
    /**
     * Constructs a CElement with complex number <code>x</code> + i <code>y</code>.
     */
    public CElement(double x, double y) {
        this.value = new Complex(x, y);
    }

    
    public boolean isOne() {
        return value.isOne();
    }
    
    
    public boolean isZero() {
        return value.isZero();
    }

    
    public CElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof CElement) {
            return sum((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }

    
    public CElement sum(CElement element) {
        return new CElement(value.sum(element.getValue()));
    }

    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof CElement) {
            add((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }

    
    public void add(CElement element) {
        value.add(element.getValue());
    }
    

    public CElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof CElement) {
            return difference((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }


    public CElement difference(CElement element) {
        return new CElement(value.difference(element.getValue()));
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof CElement) {
            subtract((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }


    public void subtract(CElement element) {
        value.subtract(element.getValue());        
    }
    
    
    public CElement negated() {
        return new CElement(value.negated());
    }


    public void negate() {
        value.negate();
    }
    

    public CElement conjugated() {
        return new CElement(value.conjugated());
    }


    public void conjugate() {
        value.conjugate();
    }


    public CElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof CElement) {
            return product(element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }
    

    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof CElement) {
            multiply(element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }
    

    public CElement product(RingElement element)
            throws DomainException {
        if (element instanceof CElement) {
            return product((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }

    
    public CElement product(CElement element) {
        return new CElement(value.product(element.getValue()));
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof CElement) {
            multiply((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getModule());
        }
    }

    
    public void multiply(CElement element) {
        value.multiply(element.getValue());
    }

    
    public boolean isInvertible() {
        return !isZero();
    }
    
    
    public CElement inverse() {
    	return new CElement(value.inverse());
    }
    

    public void invert() {
        value.invert();
    }
    
    
    public CElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof CElement) {
            return quotient((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getRing());
        }
    }


    public CElement quotient(CElement element)
            throws DivisionException {
        Complex c = element.getValue();
        if (!c.isZero()) {
            return new CElement(getValue().quotient(c));
        }
        else {
            throw new DivisionException(this, element);
        }
    }


    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof CElement) {
            divide((CElement)element);
        }
        else {
            throw new DomainException(CRing.ring, element.getRing());
        }
    }


    public void divide(CElement element)
            throws DivisionException {
        Complex c = element.getValue();
        if (!c.isZero()) {
            value.divide(c);
        }
        else {
            throw new DivisionException(this, element);
        }
    }


    public boolean divides(RingElement element) {
        return element instanceof CElement && !getValue().isZero();
    }

    
    public CElement power(int n) {
        if (n == 0) {
            return CRing.ring.getOne();
        }
        
        Complex factor;
        
        if (n < 0) {
            if (isInvertible()) {
                factor = value.inverse();
                n = -n;
            }
            else {
                throw new InverseException("Inverse of "+this+" does not exist.");
            }
        }
        else {
            factor = value;
        }
        
        // Finding leading bit in the exponent n
        int bpos = 31; // bits per int
        while ((n & (1 << bpos)) == 0) {
            bpos--;
        }

        Complex result = Complex.getOne();
        while (bpos >= 0) {
            result = result.product(result);
            if ((n & (1 << bpos)) != 0) {
                result = result.product(factor);
            }
            bpos--;
        }

        return new CElement(result);
    }
    
    
    public CRing getModule() {
        return CRing.ring;
    }

    
    public CElement getComponent(int i) {
        return this;
    }
    
    
    public CElement getRingElement(int i) {
        return this;
    }
    

    /**
     * Returns the complex value contained in this element.
     */
    public Complex getValue() {
        return value;
    }


    public CFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return CProperFreeElement.make(new Complex[0]);
        }
        else {
            Complex[] values = new Complex[n];
            values[0] = new Complex(value);
            for (int i = 1; i < n; i++) {
                values[i] = Complex.getZero();
            }
            return CProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof CElement) {
            return value.equals(((CElement)object).value);
        }
        else {
            return false;
        }
    }


    public int compareTo(ModuleElement object) {
        if (object instanceof CElement) {
            return value.compareTo(((CElement)object).value);
        }
        else {
            return super.compareTo(object);
        }
    }


    public CElement clone() {
        return new CElement((Complex)value.clone());
    }


    public String stringRep(boolean ... parens) {
        if (parens.length > 0) {
            return TextUtils.parenthesize(value.toString());
        }
        else {
            return value.toString();
        }
    }


    public String toString() {
        return "CElement["+value+"]";
    }


    public double[] fold(ModuleElement[] elements) {
        double[][] res = new double[elements.length][2];
        for (int i = 0; i < elements.length; i++) {
            CElement c = (CElement)elements[i];           
            res[i][0] = c.getValue().getReal();
            res[i][1] = c.getValue().getImag();
        }
        return Folding.fold(res);
    }


    public ModuleElement cast(Module module) {
        return module.cast(this);
    }


    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULEELEMENT, getElementTypeName(),
                             VALUE_ATTR, value.toString());
    }
    

    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (element.hasAttribute(VALUE_ATTR)) {
            try {
                Complex val = Complex.parseComplex(element.getAttribute(VALUE_ATTR));
                return new CElement(val);
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 of type %%2 must be a complex number.", VALUE_ATTR, getElementTypeName());
                return null;                    
            }
        }
        else {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), VALUE_ATTR);
            return null;                
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new CElement(0);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "CElement";
    }
    
    
    public int hashCode() {
        return value.hashCode();
    }

    
    private Complex value;
}
