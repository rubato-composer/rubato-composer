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
import static org.rubato.xml.XMLConstants.VALUES_ATTR;

import org.rubato.math.arith.Folding;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free module over reals.
 * @see org.rubato.math.module.RProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class RProperFreeElement
		extends ProperFreeElement
		implements RFreeElement {

    public static RFreeElement nullElement = new RProperFreeElement(new double[0]);
    
    public static RFreeElement make(double[] v) {
        assert(v != null);
        if (v.length == 0) {
            return nullElement;
        }
        else if (v.length == 1) {
            return new RElement(v[0]);
        }
        else {
            return new RProperFreeElement(v);
        }
    }
    

    public boolean isZero() {
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0.0) {
                return false;
            }
        }
        return true;
    }
    
    
    public RProperFreeElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            return sum((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public RProperFreeElement sum(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            double[] res = new double[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] + element.value[i];
            }
            return new RProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            add((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] += element.value[i];
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public RProperFreeElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            return difference((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public RProperFreeElement difference(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            double[] res = new double[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] - element.value[i];
            }
            return new RProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            subtract((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] -= element.value[i];
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModuleElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            return productCW((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public RProperFreeElement productCW(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            double[] res = new double[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] * element.value[i];
            }
            return new RProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof RProperFreeElement) {
            multiplyCW((RProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(RProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] *= element.value[i];
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public RProperFreeElement negated() {
        double[] res = new double[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = -value[i];
        }
        return new RProperFreeElement(res);
    }
    

    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i] = -value[i];
        }
    }
    

    public RProperFreeElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof RElement) {
            return scaled((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }
    

    public RProperFreeElement scaled(RElement element) {
        double val = element.getValue();
        double res[] = new double[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i]*val;
        }
        return new RProperFreeElement(res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof RElement) {
            scale((RElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getRing());
        }
    }

    
    public void scale(RElement element) {
        double val = element.getValue();
        for (int i = 0; i < getLength(); i++) {
            value[i] *= val;
        }
    }

    
    public RElement getComponent(int i) {
        return new RElement(value[i]); 
    }
    

    public RElement getRingElement(int i) {
        return new RElement(value[i]); 
    }
    

    public int getLength() {
        return value.length;
    }
    

    public Module getModule() {
        if (module == null) {
            module = RProperFreeModule.make(getLength());
        }
        return module;
    }
    

    public double[] getValue() {
        return value;
    }
    
    
    public double getValue(int i) {
        return value[i];
    }
    

    public RFreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            double[] values = new double[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = 0;
            }
            return RProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof RProperFreeElement) {
            RProperFreeElement e = (RProperFreeElement)object;
            if (getLength() == e.getLength()) {
                for (int i = 0; i < getLength(); i++) {
                    if (value[i] != e.value[i]) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    

    public int compareTo(ModuleElement object) {
        if (object instanceof RProperFreeElement) {
            RProperFreeElement element = (RProperFreeElement)object;
            int l = getLength()-element.getLength();
            if (l < 0) {
                return -1;
            }
            else if (l > 0) {
                return 1;
            }
            else {
                for (int i = 0; i < getLength(); i++) {
                    double d = value[i]-element.value[i];
                    if (d < 0) {
                        return -1;
                    }
                    else if (d > 0) {
                        return 1;
                    }
                }
                return 0;
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public RProperFreeElement clone() {
        double[] v = new double[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = value[i];
        }
        return new RProperFreeElement(v);
    }
    

    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null";
        }
        else {
            StringBuilder res = new StringBuilder(30);
            res.append(value[0]);
            for (int i = 1; i < getLength(); i++) {
                res.append(',');
                res.append(value[i]);
            }
            if (parens.length > 0) {
                return TextUtils.parenthesize(res.toString());
            }
            else {
                return res.toString();
            }
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("RFreeElement[");
        buf.append(getLength());
        buf.append("][");
        if (getLength() > 0) {
            buf.append(value[0]);
            for (int i = 1; i < getLength(); i++) {
                buf.append(",");
                buf.append(value[i]);
            }
        }
        buf.append("]");
        return buf.toString();
    }

    
    public double[] fold(ModuleElement[] elements) {
        double[][] res = new double[elements.length][];
        // Create an array of double arrays corresponding
        // to the array of RFreeElements
        for (int i = 0; i < elements.length; i++) {
            res[i] = ((RProperFreeElement)elements[i]).getValue();
        }
        return Folding.fold(res);
    }

    
    public void toXML(XMLWriter writer) {
        String s = "";
        if (value.length > 0) {
            s += value[0];
            for (int i = 1; i < value.length; i++) {
              s += ","+value[i];
            }
        }
        writer.emptyWithType(MODULEELEMENT, getElementTypeName(), VALUES_ATTR, s);
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(VALUES_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), VALUES_ATTR);
            return null;
        }
        
        String[] values = element.getAttribute(VALUES_ATTR).split(",");
        double[] doubleValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                doubleValues[i] = Double.parseDouble(values[i]);
            }
            catch (NumberFormatException e) {
                reader.setError("Values in type %%1 must be a comma-separated list of reals.", getElementTypeName());
                return null;
            }
        }
        
        return RProperFreeElement.make(doubleValues);
    }
    
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return RProperFreeElement.nullElement;
    }
       

    public String getElementTypeName() {
        return "RFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        long v;
        for (int i = 0; i < getLength(); i++) {
            v = Double.doubleToLongBits(value[i]);
            val ^= (int) (v ^ (v >>> 32));
        }
        return val;
    }

    
    private RProperFreeElement(double[] value) {
        this.value = value;
    }

    
    private double[]    value;
    private RFreeModule module = null;
}
