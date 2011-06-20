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
import org.rubato.util.Base64;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a free module over integers.
 * @see org.rubato.math.module.ZProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class ZProperFreeElement
		extends ProperFreeElement
		implements ZFreeElement {

    public static ZFreeElement nullElement = new ZProperFreeElement(new int[0]);

    public static ZFreeElement make(int[] v) {
        assert(v != null);
        if (v.length == 0) {
            return nullElement;
        }
        else if (v.length == 1) {
            return new ZElement(v[0]);
        }
        else {
            return new ZProperFreeElement(v);
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
    
    
    public ZProperFreeElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            return sum((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZProperFreeElement sum(ZProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] + element.value[i];
            }
            return new ZProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            add((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ZProperFreeElement element)
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
    

    public ZProperFreeElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            return difference((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ZProperFreeElement difference(ZProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i] - element.value[i];
            }
            return new ZProperFreeElement(res);        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            subtract((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ZProperFreeElement element)
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
    
    
    public ZProperFreeElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            return productCW((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public ZProperFreeElement productCW(ZProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            int res[] = new int[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = value[i]*element.value[i];
            }
            return new ZProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ZProperFreeElement) {
            multiplyCW((ZProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ZProperFreeElement element)
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
    
    
    public ZProperFreeElement negated() {
        int[] res = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = -value[i];
        }
        return new ZProperFreeElement(res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i] = -value[i];
        }
    }
    
    
    public ZProperFreeElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            return scaled((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public ZProperFreeElement scaled(ZElement element) {
        int val = element.getValue();
        int res[] = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i]*val;
        }
        return new ZProperFreeElement(res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof ZElement) {
            scale((ZElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(ZElement element) {
        int val = element.getValue();
        for (int i = 0; i < getLength(); i++) {
            value[i] *= val;
        }
    }

    
    public ZElement getComponent(int i) {
        assert(i < getLength());
        return new ZElement(value[i]);
    }
    

    public ZElement getRingElement(int i) {
        assert(i < getLength());
        return new ZElement(value[i]);
    }
    

    public int getLength() {
        return value.length;
    }
    

    public ZProperFreeModule getModule() {
        if (module == null) {
            module = (ZProperFreeModule)ZProperFreeModule.make(getLength());
        }
        return module;
    }
    

    public int[] getValue() {
        return value;
    }
    

    public int getValue(int i) {
        return value[i];
    }
    

    public ZFreeElement resize(int n) {
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
            return ZProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ZProperFreeElement) {
            ZProperFreeElement e = (ZProperFreeElement) object;
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
        if (object instanceof ZProperFreeElement) {
            ZProperFreeElement element = (ZProperFreeElement)object;
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
        else {
            return super.compareTo(object);
        }
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public ZProperFreeElement clone() {
        int[] v = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = value[i];
        }
        return new ZProperFreeElement(v);
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
        buf.append("ZFreeElement[");
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
        int len = ((ZProperFreeElement)elements[0]).getLength();
        // Create an array of double arrays corresponding
        // to the array of RFreeElements
        for (int i = 0; i < elements.length; i++) {
            res[i] = new double[len];
            for (int j = 0; j < len; j++) {
                res[i][j] = ((ZProperFreeElement)elements[i]).getValue()[j];
            }
        }
        return Folding.fold(res);
    }

    
    private final static String BASE64 = "Base64";
    
    
    public void toXML(XMLWriter writer) {
        if (value.length <= 16) {
            String s = "";
            if (value.length > 0) {
                s += value[0];
                for (int i = 1; i < value.length; i++) {
                  s += ","+value[i];
                }
            }
            writer.emptyWithType(MODULEELEMENT, getElementTypeName(), VALUES_ATTR, s);
        }
        else {
            writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
            writer.openBlock(BASE64);
            writer.writeTextNode("\n");
            writer.writeTextNode(Base64.encodeIntArray(value));
            writer.writeTextNode("\n");
            writer.closeBlock();
            writer.closeBlock();
        }
    }

    
    public ModuleElement fromXML(XMLReader xmlReader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(VALUES_ATTR)) {
            Element child = XMLReader.getChild(element, BASE64);
            if (child == null) {
                xmlReader.setError("Type %%1 is missing attribute %%2 or child element <%3>.", getElementTypeName(), VALUES_ATTR, BASE64);
                return null;
            }

            String s = XMLReader.getText(child);
            int[] intValues = Base64.decodeIntArray(s);
            return ZProperFreeElement.make(intValues);                
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
        
        return ZProperFreeElement.make(intValues);
    }
    
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return ZProperFreeElement.nullElement;
    }
       

    public String getElementTypeName() {
        return "ZFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i];
        }
        return val;
    }
    

    private ZProperFreeElement(int[] value) {
        this.value = value;
    }


    private int[]             value;
    private ZProperFreeModule module = null;
}
