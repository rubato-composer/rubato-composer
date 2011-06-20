/*
 * Copyright (C) 2007 Gérard Milmeister
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

import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free module of modular polynomials.
 * 
 * @author Gérard Milmeister
 */
public final class ModularPolynomialProperFreeElement
		extends ProperFreeElement
		implements ModularPolynomialFreeElement {

    public static ModularPolynomialFreeElement make(ModularPolynomialRing ring, ModularPolynomialElement[] v) {
        if (v.length == 1) {
            return v[0];
        }
        else {
            return new ModularPolynomialProperFreeElement(ring, v);
        }
    }

    
    public boolean isZero() {
        for (int i = 0; i < value.length; i++) {
            if (!value[i].isZero()) {
                return false;
            }
        }
        return true;
    }
    
    
    public ModularPolynomialProperFreeElement sum(ModuleElement element) 
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            return sum((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ModularPolynomialProperFreeElement sum(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                ModularPolynomialElement res[] = new ModularPolynomialElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    res[i] = value[i].sum(element.value[i]);
                }
                return new ModularPolynomialProperFreeElement(ring, res);
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            add((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                for (int i = 0; i < getLength(); i++) {
                    value[i].add(element.value[i]);
                }
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ModularPolynomialProperFreeElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            return difference((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModularPolynomialProperFreeElement difference(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                ModularPolynomialElement res[] = new ModularPolynomialElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    res[i] = value[i].difference(element.value[i]);
                }
                return new ModularPolynomialProperFreeElement(ring, res);
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            subtract((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                for (int i = 0; i < getLength(); i++) {
                    value[i].subtract(element.value[i]);
                }
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }        
    }
    
    
    public ModularPolynomialProperFreeElement productCW(ModuleElement element) 
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            return productCW((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModularPolynomialProperFreeElement productCW(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                ModularPolynomialElement res[] = new ModularPolynomialElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    res[i] = value[i].product(element.value[i]);
                }
                return new ModularPolynomialProperFreeElement(ring, res);
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialProperFreeElement) {
            multiplyCW((ModularPolynomialProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModularPolynomialProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            try {
                for (int i = 0; i < getLength(); i++) {
                    value[i].multiply(element.value[i]);
                }
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ModularPolynomialProperFreeElement negated() {
        ModularPolynomialElement[] res = new ModularPolynomialElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].negated();
        }
        return new ModularPolynomialProperFreeElement(ring, res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    
    
    public ModularPolynomialProperFreeElement scaled(RingElement element)
            throws DomainException {
        try {
            ModularPolynomialElement res[] = new ModularPolynomialElement[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i].scaled(element);
            }
            return new ModularPolynomialProperFreeElement(ring, res);
        }
        catch (DomainException e) {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public void scale(RingElement element)
            throws DomainException {
        try {
            for (int i = 0; i < getLength(); i++) {
                value[i].scale(element);
            }
        }
        catch (DomainException e) {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public ModularPolynomialElement getComponent(int i) {
        return value[i];
    }
    

    public ModularPolynomialElement getRingElement(int i) {
        return value[i];
    }
    

    public int getLength() {
        return value.length;
    }
    

    public ModularPolynomialFreeModule getModule() {
        if (module == null) {
            module = ModularPolynomialProperFreeModule.make(ring.getModulus(), getLength());
        }
        return module;
    }
    

    public ModularPolynomialElement[] getValue() {
        return value;
    }
    

    public ModularPolynomialElement getValue(int i) {
        return value[i];
    }
    
    
    public Ring getCoefficientRing() {
        return ring.getCoefficientRing();
    }
    

    public String getIndeterminate() {
        return ring.getIndeterminate();
    }
    
    
    public PolynomialElement getModulus() {
        return ring.getModulus();
    }
    

    public ModularPolynomialFreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            ModularPolynomialElement[] values = new ModularPolynomialElement[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = ring.getZero();
            }
            return ModularPolynomialProperFreeElement.make(ring, values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ModularPolynomialProperFreeElement) {
            ModularPolynomialProperFreeElement e = (ModularPolynomialProperFreeElement) object;
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
        if (object instanceof ModularPolynomialProperFreeElement) {
            ModularPolynomialProperFreeElement element = (ModularPolynomialProperFreeElement)object;
            int c = ring.compareTo(element.ring);
            if (c != 0) {
                return c;
            }
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

    
    public ModularPolynomialProperFreeElement clone() {
        ModularPolynomialElement[] v = new ModularPolynomialElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = value[i].clone();
        }
        return new ModularPolynomialProperFreeElement(ring, v);
    }
    

    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null";
        }
        else {
            StringBuilder buf = new StringBuilder(30);
            buf.append(getValue(0).stringRep());
            for (int i = 1; i < getLength(); i++) {
                buf.append(",");
                buf.append(getValue(i).stringRep());
            }
            if (parens.length > 0) {
                return TextUtils.parenthesize(buf.toString());
            }
            else {
                return buf.toString();
            }
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("ModularPolynomialFreeElement[");
        buf.append(getCoefficientRing());
        buf.append("][");
        if (getLength() > 0) {
            buf.append(getValue(0));
            for (int i = 1; i < getLength(); i++) {
                buf.append(",");
                buf.append(getValue(i));
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
            value[i].toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ModularPolynomialElement> elements = new LinkedList<ModularPolynomialElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof ModularPolynomialElement)) {
                reader.setError("Children of type %%1 must be of type %%2.", getElementTypeName(), "ModularPolynomialElement");
                return null;
            }
            elements.add((ModularPolynomialElement)moduleElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof ModularPolynomialElement)) {
                    reader.setError("Children of type %%1 must be of type %%2.", getElementTypeName(), "ModularPolynomialElement");
                    return null;
                }
                elements.add((ModularPolynomialElement)moduleElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ModularPolynomialElement[] values = new ModularPolynomialElement[elements.size()];
            Iterator<ModularPolynomialElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                values[i++] = iter.next();
            }
            ModularPolynomialRing rng = values[0].getRing();
            ModularPolynomialFreeElement result = ModularPolynomialProperFreeElement.make(rng, values);
            return result;
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO =
        new ModularPolynomialProperFreeElement(null, null); 
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ModularPolynomialFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i].hashCode();
        }
        return val;
    }
    

    private ModularPolynomialProperFreeElement(ModularPolynomialRing ring, ModularPolynomialElement[] value) {
        this.value = value;
        this.ring = ring;
    }


    private ModularPolynomialElement[]  value;
    private ModularPolynomialFreeModule module = null;
    private ModularPolynomialRing       ring = null;
}
