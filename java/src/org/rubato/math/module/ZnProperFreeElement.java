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

import static org.rubato.xml.XMLConstants.*;

import org.rubato.math.arith.Folding;
import org.rubato.math.arith.NumberTheory;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free module over integers mod <i>n</i>.
 * @see org.rubato.math.module.ZnProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public class ZnProperFreeElement
		extends ProperFreeElement
		implements ZnFreeElement {

    public static ZnFreeElement make(int[] v, int modulus) {
        assert(v != null);
        assert(modulus > 1);
        if (v.length == 0) {
            return new ZnProperFreeElement(v, modulus); 
        }
        else if (v.length == 1) {
            return new ZnElement(v[0], modulus);
        }
        else {
            return new ZnProperFreeElement(v, modulus);
        }
    }
    

    public boolean isZero() {
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    
    public ZnProperFreeElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            return sum((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZnProperFreeElement sum(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
            getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] + element.value[i];
            }
            return new ZnProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            add((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void add(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
            getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] = NumberTheory.mod(value[i]+element.value[i], modulus);
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZnProperFreeElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            return difference((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZnProperFreeElement difference(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
            getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i]-element.value[i];
            }
            return new ZnProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            subtract((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void subtract(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
            getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] = NumberTheory.mod(value[i]-element.value[i], modulus);
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ModuleElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            return productCW((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZnProperFreeElement productCW(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
                getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] * element.value[i];
            }
            return new ZnProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnProperFreeElement) {
            multiplyCW((ZnProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ZnProperFreeElement element)
            throws DomainException {
        if (getModulus() == element.getModulus() &&
                getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i] = NumberTheory.mod(value[i]*element.value[i], modulus);
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ZnProperFreeElement negated() {
        int[] res = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = -value[i];
        }
        return new ZnProperFreeElement(res, modulus);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i] = NumberTheory.mod(-value[i], modulus);
        }
    }

    
    public ZnProperFreeElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof ZnElement) {
            return scaled((ZnElement)element);
        }
        else {
            throw new DomainException(getModule().getRing(), element.getRing());
        }
    }
    

    public ZnProperFreeElement scaled(ZnElement element)
            throws DomainException {
        if (getModulus() == element.getModulus()) {
            int val = element.getValue();
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i]*val;
            }
            return new ZnProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(getModule().getRing(), element.getRing());
        }
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof ZnElement) {
            scale((ZnElement)element);
        }
        else {
            throw new DomainException(getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(ZnElement element)
            throws DomainException {
        if (getModulus() == element.getModulus()) {
            int val = element.getValue();
            for (int i = 0; i < getLength(); i++) {
                value[i] *= val;
            }
        }
        else {
            throw new DomainException(getModule().getRing(), element.getRing());
        }
    }

    
    public ZnElement getComponent(int i) {
        return new ZnElement(value[i], modulus);
    }

    
    public ZnElement getRingElement(int i) {
        return new ZnElement(value[i], modulus);
    }

    
    public int getLength() {
        return value.length;
    }

    
    public Module getModule() {
        if (module == null) {
            module = ZnProperFreeModule.make(getLength(), modulus);
        }
        return module;
    }

    
    public int[] getValue() {
        return value;
    }

    
    public int getValue(int i) {
        return value[i];
    }
    

    public int getModulus() {
        return modulus;
    }
    

    public ZnFreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            int[] values = new int[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = 0;
            }
            return ZnProperFreeElement.make(values, modulus);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ZnProperFreeElement) {
            ZnProperFreeElement e = (ZnProperFreeElement)object;
            if (modulus == e.modulus && getLength() == e.getLength()) {
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
        if (object instanceof ZnProperFreeElement) {
            ZnProperFreeElement element = (ZnProperFreeElement)object;
            int m = getModulus()-element.getModulus();
            if (m < 0) {
                return -1;
            }
            else if (m > 0) {
                return 1;
            }
            else {
	            int l = getLength()-element.getLength();
                if (l != 0) {
                    return l;
                }
	            else {
	                for (int i = 0; i < getLength(); i++) {
	                    int d = value[i]-element.value[i];
                        if (d != 0) {
                            return d;
                        }
	                }
	                return 0;
	            }
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public ZnProperFreeElement clone() {
        int[] v = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = value[i];
        }
        return new ZnProperFreeElement(v, modulus);
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
        buf.append("ZnFreeElement(");
        buf.append(getModulus());
        buf.append(")[");
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
        assert(elements.length > 0);
        double[][] res = new double[elements.length][];
        int len = ((ZnProperFreeElement)elements[0]).getLength();
        // Create an array of double arrays corresponding
        // to the array of RFreeElements
        for (int i = 0; i < elements.length; i++) {
            res[i] = new double[len];
            for (int j = 0; j < len; j++) {
                res[i][j] = ((ZnProperFreeElement)elements[i]).getValue()[j];
            }
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
        s += "\"";
        writer.emptyWithType(MODULEELEMENT, getElementTypeName(),
                             MODULUS_ATTR, getModulus(),
                             VALUES_ATTR, s);
    }

    
    public ModuleElement fromXML(XMLReader xmlReader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(VALUES_ATTR)) {
            xmlReader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), VALUES_ATTR);
            return null;
        }
        if (!element.hasAttribute(MODULUS_ATTR)) {
            xmlReader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), MODULUS_ATTR);            
            return null;
        }
        
        int mod;
        try {
            mod = Integer.parseInt(element.getAttribute(MODULUS_ATTR));
            if (mod < 2) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            xmlReader.setError("Attribute %%1 of type %%2 must be an integer > 1.", MODULUS_ATTR, getElementTypeName());
            return null;                                    
        }

        String[] values = element.getAttribute(VALUES_ATTR).split(",");
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                intValues[i] = Integer.parseInt(values[i]);
            }
            catch (NumberFormatException e) {
                xmlReader.setError("Values in type %%1 must be a comma-separated list of integers.", getElementTypeName());
                return null;
            }
        }
        
        return ZnProperFreeElement.make(intValues, mod);
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = ZnProperFreeElement.make(new int[0], 2);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ZnFreeElement";
    }

    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i];
        }
        return val;
    }

    
    private ZnProperFreeElement(int[] value, int modulus) {
        this.value = new int[value.length];
        this.modulus = modulus;
        for (int i = 0; i < getLength(); i++) {
            this.value[i] = NumberTheory.mod(value[i], modulus);
        }
    }
   

    private int[]        value;
    private int          modulus;
    private ZnFreeModule module = null;
}
