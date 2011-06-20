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
 * Elements in the ring of integers.
 * @see org.rubato.math.module.ZRing
 * 
 * @author Gérard Milmeister
 */
public final class ZElement extends RingElement implements ZFreeElement {

    /**
     * Constructs a ZElement with integer <code>value</code>.
     */
    public ZElement(int value) {
        this.value = value;
    }

    
    public boolean isOne() {
        return value == 1;
    }
    
    
    public boolean isZero() {
        return value == 0;
    }
    
    
    public ZElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            return sum((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZElement sum(ZElement element) {
        return new ZElement(value+element.getValue());        
    }
    
    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            add((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ZElement element) {
        value += element.getValue();        
    }
    
    
    public ZElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            return difference((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZElement difference(ZElement element) {
        return new ZElement(value-element.getValue());        
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            subtract((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void subtract(ZElement element) {
        value -= element.getValue();
    }

    
    public ZElement negated() {
        return new ZElement(-value);
    }

    
    public void negate() {
        value = -value;
    }
    
    
    public ZElement scaled(RingElement element)
            throws DomainException{
        return product(element);
    }
    

    public void scale(RingElement element)
            throws DomainException {
        multiply(element);
    }
    

    public ZElement product(RingElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            return product((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZElement product(ZElement element) {
        return new ZElement(getValue()*element.getValue());
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            multiply((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void multiply(ZElement element) {
        value *= element.getValue();
    }

    
    public boolean isInvertible() {
        return (value == 1 || value == -1);
    }
    
    
    public RingElement inverse() {
    	if (isInvertible()) {
    		return this;
    	}
    	else {
    	    throw new InverseException("Inverse of "+this+" does not exist.");
        }
    }

    
    public void invert() {
        if (isInvertible()) {
            return;
        }
        else {
    	    throw new InverseException("Inverse of "+this+" does not exist.");
        }
    }
    
    
    public ZElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZElement) {
            return quotient((ZElement)element);
        }
        else {
            throw new DomainException(ZRing.ring, element.getRing());
        }
    }


    public ZElement quotient(ZElement element)
            throws DivisionException {
        int p = getValue();
        int q = element.getValue();
        if (q != 0 && p % q == 0) {
            return new ZElement(p/q);
        }
        else {
            throw new DivisionException(this, element);
        }
    }


    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZElement) {
            divide((ZElement)element);
        }
        else {
            throw new DomainException(ZRing.ring, element.getRing());
        }
    }

    public void divide(ZElement element)
            throws DivisionException {
        int p = getValue();
        int q = element.getValue();
        if (p % q == 0) {
            value /= q;
        }
        else {
            throw new DivisionException(this, element);
        }
    }

    
    public boolean divides(RingElement element) {
        return element instanceof ZElement &&
               getValue() != 0 &&
               ((ZElement)element).getValue() % getValue() == 0;
    }

    
    public ZElement power(int n) {
        if (n == 0) {
            return ZRing.ring.getOne();
        }
        
        int factor;
        
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

        int result = 1;
        while (bpos >= 0) {
            result = result * result;
            if ((n & (1 << bpos)) != 0) {
                result = result * factor;
            }
            bpos--;
        }
        
        return new ZElement(result);
    }

    
    public ZRing getModule() {
        return ZRing.ring;
    }

    
    public ZRing getRing() {
        return ZRing.ring;
    }
    
    
    public ZElement getRingElement(int i) {
        return this;
    }
    
    
    public ZElement getComponent(int i) {
        return this;
    }
    
    
    public int getValue() {
        return value;
    }

    
    public ZFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return ZProperFreeElement.make(new int[0]);
        }
        else {
            int[] values = new int[n];
            values[0] = value;
            for (int i = 1; i < n; i++) {
                values[i] = 0;
            }
            return ZProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
	    if (this == object) {
	        return true;
	    }
	    else if (object instanceof ZElement) {
	        return value == ((ZElement)object).value;
	    }
	    else {
	        return false;
	    }
    }

    
    public int compareTo(ModuleElement object) {
        if (object instanceof ZElement) {
            int x = ((ZElement)object).value;
            return value-x;
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ZElement clone() {
        return new ZElement(value);
    }
    
    
    public String stringRep(boolean ... parens) {
        if (parens.length > 0 && value < 0) {
            return TextUtils.parenthesize(Integer.toString(value));
        }
        else {
            return Integer.toString(value);
        }
    }

    
    public String toString() {
        return "ZElement["+value+"]";
    }
    
    
    public double[] fold(ModuleElement[] elements) {
        double[] res = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            ZElement e = (ZElement)elements[i];
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
                int val = Integer.parseInt(element.getAttribute(VALUE_ATTR));
                return new ZElement(val);
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 of type %%2 must be an integer.", VALUE_ATTR, getElementTypeName());
                return null;                    
            }
        }
        else {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), VALUE_ATTR);
            return null;                
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new ZElement(0);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ZElement";
    }
    
    
    public int hashCode() {
        return value;
    }

    
    private int value;
}
