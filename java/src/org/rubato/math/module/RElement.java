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

import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in the field of reals.
 * @see org.rubato.math.module.RRing
 * 
 * @author Gérard Milmeister
 */
public final class RElement extends RingElement implements RFreeElement {

    /**
     * Constructs an RElement with real number <code>value</code>.
     */
    public RElement(double value) {
        this.value = value;
    }

    
    public boolean isOne() {
        return value == 1.0;
    }
    
    
    public boolean isZero() {
        return value == 0.0;
    }
    
    
    public RElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof RElement) {
            return sum((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }

    
    public RElement sum(RElement element) {
        return new RElement(value+element.getValue());            
    }


    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof RElement) {
            add((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }

    
    public final void add(RElement element) {
        value += element.getValue();        
    }


    public RElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof RElement) {
            return difference((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }


    public final RElement difference(RElement element) {
        return new RElement(value-element.getValue());        
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof RElement) {
            subtract((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }

    
    public final void subtract(RElement element) {
        value -= element.getValue();        
    }
    
    
    public RElement negated() {
        return new RElement(-value);
    }

    
    public void negate() {
        value = -value;
    }

    
    public RElement scaled(RingElement element)
            throws DomainException {
        return product(element);
    }
    

    public void scale(RingElement element)
            throws DomainException {
        multiply(element);
    }
    

    public RElement product(RingElement element)
            throws DomainException {
        if (element instanceof RElement) {
            return product((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }

    
    public final RElement product(RElement element) {
        return new RElement(value*element.getValue());
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof RElement) {
            multiply((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }

    
    public final void multiply(RElement element) {
        value *= element.getValue();
    }

    
    public boolean isInvertible() {
        return value != 0.0;
    }
    
    
    public RElement inverse() {
        return new RElement(1/value);
    }

    
    public void invert() {
        value = 1/value;
    }


    public RingElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof RElement) {
            return quotient((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }
    
    
    public RElement quotient(RElement element)
            throws DivisionException {
        double v = element.getValue();
        if (v != 0.0) {
            return new RElement(getValue()/element.getValue());
        }
        else {
            throw new DivisionException(this, element);
        }
    }
    
    
    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof RElement) {
            divide((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }
    
    
    public void divide(RElement element)
            throws DivisionException {
        double v = element.getValue();
        if (v != 0.0) {
            value /= element.getValue();
        }
        else {
            throw new DivisionException(this, element);
        }
    }
    
    
    public boolean divides(RingElement element) {
        return element instanceof RElement && getValue() != 0.0;
    }

    
    public RingElement power(int n) {
        if (n == 0) {
            return RRing.ring.getOne();
        }
        
        double factor;
        
        if (n < 0) {
            if (isInvertible()) {
                factor = 1/value;
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

        double result = 1.0;
        while (bpos >= 0) {
            result = result * result;
            if ((n & (1 << bpos)) != 0) {
                result = result * factor;
            }
            bpos--;
        }

        return new RElement(result);
    }
        

    public Module getModule() {
        return RRing.ring;
    }

    
    public RElement getRingElement(int i) {
        return this;
    }
    
    
    public RElement getComponent(int i) {
        return this;
    }
    
    
    public double getValue() {
        return value;
    }

    
    public RFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return RProperFreeElement.make(new double[0]);
        }
        else {
            double[] values = new double[n];
            values[0] = value;
            for (int i = 1; i < n; i++) {
                values[i] = 0;
            }
            return RProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof RElement) {
            return Math.abs(value - ((RElement)object).value) < getEpsilon();
        }
        else {
            return false;
        }
    }

    
    public int compareTo(ModuleElement object) {
        if (object instanceof RElement) {
            double x = ((RElement)object).value;
            if (equals(object)) {
                return 0;
            }
            else if (value < x) {
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


    public RElement clone() {
        return new RElement(value);
    }

    
    public String stringRep(boolean ... parens) {
        if (parens.length > 0 && value < 0) {
            return TextUtils.parenthesize(Double.toString(value));
        }
        else {
            return Double.toString(value);
        }
    }
    
    
    public String toString() {
        return "RElement["+getValue()+"]";
    }

    
    public double[] fold(ModuleElement[] elements) {
        double[] res = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            RElement e = (RElement)elements[i];
            res[i] = e.getValue();
        }
        return res;
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULEELEMENT, getElementTypeName(), VALUE_ATTR, value);
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (element.hasAttribute(VALUE_ATTR)) {
            try {
                double val = Double.parseDouble(element.getAttribute(VALUE_ATTR));
                return new RElement(val);
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 of type %%2 must be a real number.", VALUE_ATTR, getElementTypeName());
                return null;                    
            }
        }
        else {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), VALUE_ATTR);
            return null;                
        }
    }

    
    private final static XMLInputOutput<ModuleElement> xmlIO = new RElement(0);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "RElement";
    }
    
    
    public int hashCode() {
        long v = Double.doubleToLongBits(value);
        return (int) (v ^ (v >>> 32));
    }

    
    private double value;

    
    public static double getEpsilon() {
        return epsilon;
    }
    
    
    public static void setEpsilon(double e) {
        epsilon =  e;
    }
    
    
    private static double epsilon = 4*Double.MIN_VALUE;
}
