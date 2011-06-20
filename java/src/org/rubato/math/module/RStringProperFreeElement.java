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

import org.rubato.math.arith.RString;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free module over RString.
 * @see org.rubato.math.module.RStringProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class RStringProperFreeElement
		extends ProperFreeElement
		implements RStringFreeElement {

    public static RStringFreeElement nullElement = new RStringProperFreeElement(new RString[0]);

    public static RStringFreeElement make(RString[] v) {
        assert(v != null);
        if (v.length == 0) {
            return nullElement;
        }
        else if (v.length == 1) {
            return new RStringElement(v[0]);
        }
        else {
            return new RStringProperFreeElement(v);
        }
    }

    
    public boolean isZero() {
        RString zero = RString.getZero();
        for (int i = 0; i < getLength(); i++) {
            if (!value[i].equals(zero)) {
                return false;
            }
        }
        return true;
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof RStringProperFreeElement) {
            return sum((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public RStringProperFreeElement sum(RStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            RString res[] = new RString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (RString)value[i].sum(element.value[i]);
            }
            return new RStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof RStringProperFreeElement) {
            add((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(RStringProperFreeElement element)
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
        if (element instanceof RStringProperFreeElement) {
            return difference((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public RStringProperFreeElement difference(RStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            RString res[] = new RString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (RString)value[i].difference(element.value[i]);
            }
            return new RStringProperFreeElement(res);        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof RStringProperFreeElement) {
            subtract((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(RStringProperFreeElement element)
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
        if (element instanceof RStringProperFreeElement) {
            return productCW((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public RStringProperFreeElement productCW(RStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            RString res[] = new RString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (RString)value[i].product(element.value[i]);
            }
            return new RStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof RStringProperFreeElement) {
            multiplyCW((RStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(RStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            for (int i = 0; i < getLength(); i++) {
                value[i].multiply(element.value[i]);
            }        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public ModuleElement negated() {
        RString[] res = new RString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (RString)value[i].negated();
        }
        return new RStringProperFreeElement(res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    
    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof RStringElement) {
            return scaled((RStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public RStringProperFreeElement scaled(RStringElement element) {
        RString val = element.getValue();
        RString res[] = new RString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (RString)value[i].product(val);
        }
        return new RStringProperFreeElement(res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof RStringElement) {
            scale((RStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(RStringElement element) {
        RString val = element.getValue();
        for (int i = 0; i < getLength(); i++) {
            value[i].multiply(val);
        }
    }

    
    public ModuleElement getComponent(int i) {
        assert(i < getLength());
        return new RStringElement(value[i]);
    }
    

    public RingElement getRingElement(int i) {
        assert(i < getLength());
        return new RStringElement(value[i]);
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
    

    public RString[] getValue() {
        return value;
    }
    

    public RString getValue(int i) {
        return value[i];
    }
    

    public FreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            RString[] values = new RString[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = RString.getZero();
            }
            return RStringProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof RStringProperFreeElement) {
            RStringProperFreeElement e = (RStringProperFreeElement) object;
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
        if (object instanceof RProperFreeElement) {
            RStringProperFreeElement element = (RStringProperFreeElement)object;
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

    
    public RStringProperFreeElement clone() {
        RString[] v = new RString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = (RString)value[i].clone();
        }
        return new RStringProperFreeElement(v);
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
        StringBuilder buf = new StringBuilder(30);
        buf.append("RStringFreeElement[");
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
            new RStringElement(value[i]).toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<RStringElement> elements = new LinkedList<RStringElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof RStringElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "RStringElement");
                return null;                    
            }
            RStringElement ringElement = (RStringElement)moduleElement;
            elements.add(ringElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof RStringElement)) {
                    reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "RStringElement");
                    return null;                    
                }
                ringElement = (RStringElement)moduleElement;
                elements.add(ringElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            RString[] coefficients = new RString[elements.size()];
            Iterator<RStringElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                coefficients[i++] = iter.next().getValue();
            }
            return RStringProperFreeElement.make(coefficients);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return RStringProperFreeElement.nullElement;
    }
       

    public String getElementTypeName() {
        return "RStringFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i].hashCode();
        }
        return val;
    }
    

    private RStringProperFreeElement(RString[] value) {
        this.value = value;
    }


    private RString[]   value;
    private RFreeModule module = null;
}
