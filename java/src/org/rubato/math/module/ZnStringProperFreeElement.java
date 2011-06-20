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
import static org.rubato.xml.XMLConstants.MODULUS_ATTR;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.math.arith.ZnString;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free modules over ZnString.
 * @see org.rubato.math.module.ZnStringProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class ZnStringProperFreeElement
		extends ProperFreeElement
		implements ZnStringFreeElement {

    public static ZnStringFreeElement make(ZnString[] v, int modulus) {
        if (v == null || v.length == 0) {
            return new ZnStringProperFreeElement(new ZnString[0], modulus);
        }
        else if (v.length == 1) {
            if (v[0].getModulus() == modulus) {
                return new ZnStringElement(v[0]);
            }
            else {
                return null;
            }
        }
        else {
            for (int i = 0; i < v.length; i++) {
                if (v[i].getModulus() != modulus) {
                    return null;
                }
            }
            return new ZnStringProperFreeElement(v, modulus);
        }
    }

    
    public boolean isZero() {
        ZnString zero = ZnString.getZero(modulus);
        for (int i = 0; i < getLength(); i++) {
            if (!value[i].equals(zero)) {
                return false;
            }
        }
        return true;
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            return sum((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZnStringProperFreeElement sum(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            ZnString res[] = new ZnString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZnString)value[i].sum(element.value[i]);
            }
            return new ZnStringProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            add((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            for (int i = 0; i < getLength(); i++) {
                value[i].add(element.value[i]);
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ModuleElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            return difference((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZnStringProperFreeElement difference(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            ZnString res[] = new ZnString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZnString)value[i].difference(element.value[i]);
            }
            return new ZnStringProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            subtract((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            for (int i = 0; i < getLength(); i++) {
                value[i].subtract(element.value[i]);
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModuleElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            return productCW((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZnStringProperFreeElement productCW(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            ZnString res[] = new ZnString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZnString)value[i].product(element.value[i]);
            }
            return new ZnStringProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringProperFreeElement) {
            multiplyCW((ZnStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ZnStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength() && modulus == element.getModulus()) {
            for (int i = 0; i < getLength(); i++) {
                value[i].multiply(element.value[i]);
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ModuleElement negated() {
        ZnString[] res = new ZnString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (ZnString)value[i].negated();
        }
        return new ZnStringProperFreeElement(res, modulus);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    
    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            return scaled((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public ZnStringProperFreeElement scaled(ZnStringElement element)
            throws DomainException {
        if (element.getModulus() == modulus) {
            ZnString val = element.getValue();
            ZnString res[] = new ZnString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZnString)value[i].product(val);
            }
            return new ZnStringProperFreeElement(res, modulus);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            scale((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(ZnStringElement element)
            throws DomainException {
        if (element.getModulus() == modulus) {
            ZnString val = element.getValue();
            for (int i = 0; i < getLength(); i++) {
                value[i].multiply(val);
            }
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public ModuleElement getComponent(int i) {
        assert(i < getLength());
        return new ZnStringElement(value[i]);
    }
    

    public RingElement getRingElement(int i) {
        assert(i < getLength());
        return new ZnStringElement(value[i]);
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
    

    public ZnString[] getValue() {
        return value;
    }
    

    public ZnString getValue(int i) {
        return value[i];
    }
    

    public int getModulus() {
        return modulus;
    }
    
    
    public FreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            ZnString[] values = new ZnString[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = ZnString.getZero(modulus);
            }
            return ZnStringProperFreeElement.make(values, modulus);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ZnStringProperFreeElement) {
            ZnStringProperFreeElement e = (ZnStringProperFreeElement) object;
            if (getLength() == e.getLength()) {
                for (int i = 0; i < getLength(); i++) {
                    if (!value[i].equals(e.value[i])) {
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
        if (object instanceof ZProperFreeElement) {
            ZnStringProperFreeElement element = (ZnStringProperFreeElement)object;
            int l = getLength()-element.getLength();
            if (l != 0) {
                return l;
            }
            else {
                for (int i = 0; i < getLength(); i++) {
                    int d = value[i].compareTo(element.value[i]);
                    if (d != 0) {
                        return d;
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

    
    public ZnStringProperFreeElement clone() {
        ZnString[] v = new ZnString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = (ZnString)value[i].clone();
        }
        return new ZnStringProperFreeElement(v, modulus);
    }
    

    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null";
        }
        else {
            StringBuilder res = new StringBuilder(30);
            res.append("(");
            res.append(value[0].stringRep());
            for (int i = 1; i < getLength(); i++) {
                res.append(',');
                res.append(value[i].stringRep());
            }
            res.append(")");
            if (parens.length > 0) {
                return TextUtils.parenthesize(res.toString());
            }
            else {
                return res.toString();
            }
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(50);
        buf.append("ZnStringFreeElement[");
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
        throw new UnsupportedOperationException("Not implemented");
    }
    

    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName(),
                                 MODULUS_ATTR, getModulus());
        for (int i = 0; i < value.length; i++) {
            new ZnStringElement(value[i]).toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        int modulus0 = XMLReader.getIntAttribute(element, MODULUS_ATTR, 2, Integer.MAX_VALUE, 2);
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ZnStringElement> elements = new LinkedList<ZnStringElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof ZnStringElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ZnStringElement");
                return null;                    
            }
            ZnStringElement ringElement = (ZnStringElement)moduleElement;
            elements.add(ringElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof ZnStringElement)) {
                    reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ZnStringElement");
                    return null;                    
                }
                ringElement = (ZnStringElement)moduleElement;
                elements.add(ringElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ZnString[] coefficients = new ZnString[elements.size()];
            Iterator<ZnStringElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                coefficients[i++] = iter.next().getValue();
            }
            return ZnStringProperFreeElement.make(coefficients, modulus0);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = ZnStringProperFreeElement.make(new ZnString[0], 2);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ZnStringFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i].hashCode();
        }
        return val;
    }
    

    private ZnStringProperFreeElement(ZnString[] value, int modulus) {
        this.value = value;
        this.modulus = modulus;
    }


    private ZnString[]   value;
    private int          modulus;
    private ZnFreeModule module = null;
}
