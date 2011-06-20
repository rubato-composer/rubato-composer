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

import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.math.arith.ZString;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * Elements in a free module of ZString.
 * @see org.rubato.math.module.ZStringProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class ZStringProperFreeElement
		extends ProperFreeElement
		implements ZStringFreeElement {

    public static ZStringFreeElement nullElement = new ZStringProperFreeElement(new ZString[0]);

    public static ZStringFreeElement make(ZString[] v) {
        assert(v != null);
        if (v.length == 0) {
            return nullElement;
        }
        else if (v.length == 1) {
            return new ZStringElement(v[0]);
        }
        else {
            return new ZStringProperFreeElement(v);
        }
    }

    
    public boolean isZero() {
        ZString zero = ZString.getZero();
        for (int i = 0; i < getLength(); i++) {
            if (!value[i].equals(zero)) {
                return false;
            }
        }
        return true;
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringProperFreeElement) {
            return sum((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZStringProperFreeElement sum(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            ZString res[] = new ZString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZString)value[i].sum(element.value[i]);
            }
            return new ZStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringProperFreeElement) {
            add((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
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
        if (element instanceof ZStringProperFreeElement) {
            return difference((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZStringProperFreeElement difference(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            ZString res[] = new ZString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZString)value[i].difference(element.value[i]);
            }
            return new ZStringProperFreeElement(res);        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringProperFreeElement) {
            subtract((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
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
        if (element instanceof ZStringProperFreeElement) {
            return productCW((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public ZStringProperFreeElement productCW(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            ZString res[] = new ZString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (ZString)value[i].product(element.value[i]);
            }
            return new ZStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringProperFreeElement) {
            multiplyCW((ZStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void multiplyCW(ZStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i].add(element.value[i]);
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModuleElement negated() {
        ZString[] res = new ZString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (ZString)value[i].negated();
        }
        return new ZStringProperFreeElement(res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    
    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            return scaled((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public ZStringProperFreeElement scaled(ZStringElement element) {
        ZString val = element.getValue();
        ZString res[] = new ZString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (ZString)value[i].product(val);
        }
        return new ZStringProperFreeElement(res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            scale((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(ZStringElement element) {
        ZString val = element.getValue();
        for (int i = 0; i < getLength(); i++) {
            value[i].multiply(val);
        }
    }

    
    public ModuleElement getComponent(int i) {
        assert(i < getLength());
        return new ZStringElement(value[i]);
    }
    

    public RingElement getRingElement(int i) {
        assert(i < getLength());
        return new ZStringElement(value[i]);
    }
    

    public int getLength() {
        return value.length;
    }
    

    public Module getModule() {
        if (module == null) {
            module = ZProperFreeModule.make(getLength());
        }
        return module;
    }
    

    public ZString[] getValue() {
        return value;
    }
    

    public ZString getValue(int i) {
        return value[i];
    }
    

    public FreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            ZString[] values = new ZString[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = ZString.getZero();
            }
            return ZStringProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ZStringProperFreeElement) {
            ZStringProperFreeElement e = (ZStringProperFreeElement) object;
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
            ZStringProperFreeElement element = (ZStringProperFreeElement)object;
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

    
    public ZStringProperFreeElement clone() {
        ZString[] v = new ZString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = (ZString)value[i].clone();
        }
        return new ZStringProperFreeElement(v);
    }
    

    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null";
        }
        else {
            StringBuilder res = new StringBuilder(30);
            res.append(value[0].stringRep());
            for (int i = 1; i < getLength(); i++) {
                res.append(',');
                res.append(value[i].stringRep());
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
        StringBuilder buf = new StringBuilder(50);
        buf.append("ZStringFreeElement[");
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
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        for (int i = 0; i < value.length; i++) {
            new ZStringElement(value[i]).toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ZStringElement> elements = new LinkedList<ZStringElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof ZStringElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ZStringElement");
                return null;                    
            }
            ZStringElement ringElement = (ZStringElement)moduleElement;
            elements.add(ringElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof ZStringElement)) {
                    reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ZStringElement");
                    return null;                    
                }
                ringElement = (ZStringElement)moduleElement;
                elements.add(ringElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ZString[] coefficients = new ZString[elements.size()];
            Iterator<ZStringElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                coefficients[i++] = iter.next().getValue();
            }
            return ZStringProperFreeElement.make(coefficients);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return ZStringProperFreeElement.nullElement;
    }
       

    public String getElementTypeName() {
        return "ZStringFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i].hashCode();
        }
        return val;
    }
    

    private ZStringProperFreeElement(ZString[] value) {
        this.value = value;
    }


    private ZString[]   value;
    private ZFreeModule module = null;
}
