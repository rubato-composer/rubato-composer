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

import org.rubato.math.arith.QString;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in the free module of QString.
 * @see org.rubato.math.module.QStringProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class QStringProperFreeElement
		extends ProperFreeElement
		implements QStringFreeElement {

    public static QStringFreeElement nullElement = new QStringProperFreeElement(new QString[0]);

    public static QStringFreeElement make(QString[] v) {
        assert(v != null);
        if (v.length == 0) {
            return nullElement;
        }
        else if (v.length == 1) {
            return new QStringElement(v[0]);
        }
        else {
            return new QStringProperFreeElement(v);
        }
    }

    
    public boolean isZero() {
        QString zero = QString.getZero();
        for (int i = 0; i < getLength(); i++) {
            if (!value[i].equals(zero)) {
                return false;
            }
        }
        return true;
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringProperFreeElement) {
            return sum((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public QStringProperFreeElement sum(QStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            QString res[] = new QString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (QString)value[i].sum(element.value[i]);
            }
            return new QStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringProperFreeElement) {
            add((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(QStringProperFreeElement element)
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
        if (element instanceof QStringProperFreeElement) {
            return difference((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public QStringProperFreeElement difference(QStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            QString res[] = new QString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (QString)value[i].difference(element.value[i]);
            }
            return new QStringProperFreeElement(res);        
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringProperFreeElement) {
            subtract((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public void subtract(QStringProperFreeElement element)
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
        if (element instanceof QStringProperFreeElement) {
            return productCW((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public QStringProperFreeElement productCW(QStringProperFreeElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            QString res[] = new QString[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = (QString)value[i].product(element.value[i]);
            }
            return new QStringProperFreeElement(res);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringProperFreeElement) {
            multiplyCW((QStringProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(QStringProperFreeElement element)
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
        QString[] res = new QString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (QString)value[i].negated();
        }
        return new QStringProperFreeElement(res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    
    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            return scaled((QStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }
    

    public QStringProperFreeElement scaled(QStringElement element) {
        QString val = element.getValue();
        QString res[] = new QString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = (QString)value[i].product(val);
        }
        return new QStringProperFreeElement(res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            scale((QStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void scale(QStringElement element) {
        QString val = element.getValue();
        for (int i = 0; i < getLength(); i++) {
            value[i].multiply(val);
        }
    }

    
    public ModuleElement getComponent(int i) {
        assert(i < getLength());
        return new QStringElement(value[i]);
    }
    

    public RingElement getRingElement(int i) {
        assert(i < getLength());
        return new QStringElement(value[i]);
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
    

    public QString[] getValue() {
        return value;
    }
    

    public QString getValue(int i) {
        return value[i];
    }
    

    public FreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            QString[] values = new QString[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = QString.getZero();
            }
            return QStringProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof QStringProperFreeElement) {
            QStringProperFreeElement e = (QStringProperFreeElement) object;
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
            QStringProperFreeElement element = (QStringProperFreeElement)object;
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

    
    public QStringProperFreeElement clone() {
        QString[] v = new QString[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = (QString)value[i].clone();
        }
        return new QStringProperFreeElement(v);
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
        StringBuilder res = new StringBuilder(30);
        res.append("QStringFreeElement[");
        res.append(getLength());
        res.append("][");
        if (getLength() > 0) {
            res.append(value[0]);
            for (int i = 1; i < getLength(); i++) {
                res.append(",");
                res.append(value[i]);
            }
        }
        res.append("]");
        return res.toString();
    }
    

    public double[] fold(ModuleElement[] elements) {
        throw new UnsupportedOperationException("Not implemented");
    }
    

    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        for (int i = 0; i < value.length; i++) {
            new QStringElement(value[i]).toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<QStringElement> elements = new LinkedList<QStringElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof QStringElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "QStringElement");
                return null;                    
            }
            QStringElement ringElement = (QStringElement)moduleElement;
            elements.add(ringElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof QStringElement)) {
                    reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "QStringElement");
                    return null;                    
                }
                ringElement = (QStringElement)moduleElement;
                elements.add(ringElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            QString[] coefficients = new QString[elements.size()];
            Iterator<QStringElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                coefficients[i++] = iter.next().getValue();
            }
            return QStringProperFreeElement.make(coefficients);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return QStringProperFreeElement.nullElement;
    }
       

    public String getElementTypeName() {
        return "QStringFreeElement";
    }
    
    
    public int hashCode() {
        int val = 0;
        for (int i = 0; i < getLength(); i++) {
            val ^= value[i].hashCode();
        }
        return val;
    }
    

    private QStringProperFreeElement(QString[] value) {
        this.value = value;
    }


    private QString[]   value;
    private ZFreeModule module = null;
}
