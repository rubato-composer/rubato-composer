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

import java.util.LinkedList;
import java.util.List;

import org.rubato.math.arith.Folding;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements with components from arbitrary modules.
 * @see org.rubato.math.module.DirectSumModule
 * 
 * @author Gérard Milmeister
 */
public final class DirectSumElement implements ModuleElement {

    public static DirectSumElement make(ModuleElement[] components) {
        if (components.length < 1) {
            throw new IllegalArgumentException("There must be at least one component module.");
        }
        else if (!checkComponents(components)) {
            throw new IllegalArgumentException("Component elements must all be in the same ring.");            
        }
        else {
            return new DirectSumElement(components);
        }
    }
    
    
    public static DirectSumElement makeNullElement(Ring ring) {
        return new DirectSumElement(ring);
    }
    
    
    public boolean isNullElement() {
        return components == null;
    }

    
    public boolean isZero() {
        for (int i = 0; i < getLength(); i++) {
            if (!components[i].isZero()) {
                return false;
            }
        }
        return true;
    }
    
    
    public DirectSumElement sum(ModuleElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            if (isNullElement()) {
                if (getModule().hasElement(element)) {
                    return this;
                }
                else {
                    throw new DomainException(this.getModule(), element.getModule());
                }
            }
            try {
                ModuleElement[] c = new ModuleElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    c[i] = components[i].sum(element.getComponent(i));
                }
                return new DirectSumElement(c);
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
        if (getLength() == element.getLength()) {
            if (isNullElement()) {
                if (getModule().hasElement(element)) {
                    return;
                }
                else {
                    throw new IllegalArgumentException("Cannot sum "+this+" and "+element+".");                    
                }
            }
            try {
                ModuleElement[] newComponents = new ModuleElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    newComponents[i] = components[i].clone();
                    newComponents[i].add(element.getComponent(i));
                }
                components = newComponents;
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public DirectSumElement difference(ModuleElement element)
            throws DomainException {
        if (getLength() == element.getLength()) {
            if (isNullElement()) {
                if (getModule().hasElement(element)) {
                    return this;
                }
                else {
                    throw new DomainException(this.getModule(), element.getModule());
                }
            }
            try {
                ModuleElement[] c = new ModuleElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    c[i] = getComponent(i).difference(element.getComponent(i));
                }
                return new DirectSumElement(c);
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
        if (getLength() == element.getLength()) {
            if (isNullElement()) {
                if (getModule().hasElement(element)) {
                    return;
                }
                else {
                    throw new IllegalArgumentException("Cannot subtract "+element+" from "+this+".");                    
                }
            }
            try {
                ModuleElement[] newComponents = new ModuleElement[getLength()];
                for (int i = 0; i < getLength(); i++) {
                    newComponents[i] = components[i].clone();
                    newComponents[i].subtract(element.getComponent(i));
                }
                components = newComponents;
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public DirectSumElement negated() {
        if (isNullElement()) {
            return this;
        }
        ModuleElement[] res = new ModuleElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = components[i].negated();
        }
        return new DirectSumElement(res);
    }

    
    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            components[i].negate();
        }
    }

    
    public DirectSumElement scaled(RingElement element)
            throws DomainException {
        if (isNullElement()) {
            return this;
        }
        ModuleElement res[] = new ModuleElement[getLength()];
        try {
            for (int i = 0; i < getLength(); i++) {
                res[i] = components[i].scaled(element);
            }
        }
        catch (DomainException e) {
            throw new DomainException(this.getRing(), element.getModule());
        }
        return new DirectSumElement(res);
    }

    
    public void scale(RingElement element)
            throws DomainException {
        try {
            for (int i = 0; i < getLength(); i++) {
                components[i] = components[i].scaled(element);
            }
        }
        catch (DomainException e) {
            throw new DomainException(this.getRing(), element.getModule());
        }
    }

    
    public ModuleElement getComponent(int i) {
        return components[i];
    }

    
    public int getLength() {
        if (components == null) {
            return 0;
        }
        else {
            return components.length;
        }
    }

    
    public DirectSumModule getModule() {
        if (module == null) {
            Module[] res = new Module[getLength()];
            for (int i = 0; i < getLength(); i++) {
                res[i] = components[i].getModule();
            }
            module = DirectSumModule.make(res);
        }
        return module;
    }
    
    
    public Ring getRing() {
        return ring;
    }
    

    public boolean equals(Object object) {
        if (object instanceof ModuleElement) {
            ModuleElement element = (ModuleElement) object;
            if (!getRing().equals(element.getModule().getRing())) {
                return false;
            }
            if (getLength() == element.getLength()) {
                for (int i = 0; i < getLength(); i++) {
                    if (!components[i].equals(element.getComponent(i))) {
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
        if (object instanceof DirectSumElement) {
            DirectSumElement element = (DirectSumElement)object;
            if (!getRing().equals(element.getRing())) {
                return getRing().compareTo(element.getRing());
            }
            int d = getLength()-element.getLength();
            if (d != 0) {
                return d;
            }
            else {
	            for (int i = 0; i < getLength(); i++) {
	                int comp = getComponent(i).compareTo(element.getComponent(i));
	                if (comp != 0) {
	                    return comp;
	                }
	            }
	            return 0;
            }
        }
        else {
            return getModule().compareTo(object.getModule());
        }
    }

    
    public DirectSumElement clone() {
        if (isNullElement()) {
            return makeNullElement(getModule().getRing());
        }
        ModuleElement[] newComponents = new ModuleElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            newComponents[i] = components[i].clone();
        }
        return new DirectSumElement(newComponents);
    }

    
    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null["+getRing()+"]";
        }
        else {
            StringBuilder res = new StringBuilder("("+components[0]+")");
            for (int i = 1; i < getLength(); i++) {
                res.append(',');
                res.append('(');
                res.append(components[i]);
                res.append(')');
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
        buf.append("DirectSumElement[");
        buf.append(getLength());
        buf.append(",");
        buf.append(getModule().getRing());
        buf.append("][");
        if (getLength() > 0) {
            buf.append(components[0]);
            for (int i = 1; i < getLength(); i++) {
                buf.append(",");
                buf.append(components[i]);
            }
        }
        buf.append("]");
        return buf.toString();
    }

    
    public double[] fold(ModuleElement[] elements) {
        int eltnr = elements.length;
        int eltlen = getLength();
        double[][] res = new double[eltnr][eltlen];
        for (int i = 0; i < eltlen; i++) {
            double[] x;
            x = foldOne(elements, i);
            for (int j = 0; j < eltnr; i++) {
                res[j][i] = x[j];
            }
        }
        return Folding.fold(res);
    }

    
    public double[] foldOne(ModuleElement[] elements, int index) {
        ModuleElement[] x = new ModuleElement[elements.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = elements[i].getComponent(index);
        }
        return x[0].fold(x);
    }

    
    public ModuleElement cast(Module module) {
        List<ModuleElement> flattenedList = flatten();
        if (module instanceof DirectSumModule) {
            return ((DirectSumModule)module).fill(flattenedList);
        }
        else {
            return module.createElement(flattenedList);
        }
    }

    
    public List<ModuleElement> flatten() {
        LinkedList<ModuleElement> flattenedList = new LinkedList<ModuleElement>();        
        flatten(flattenedList);
        return flattenedList;
    }
    
    
    private void flatten(LinkedList<ModuleElement> list) {
        for (int i = 0; i < getLength(); i++) {
            ModuleElement component = getComponent(i);
            if (component instanceof DirectSumElement) {
                ((DirectSumElement)component).flatten(list);
            }
            else {
                for (int j = 0; j < component.getLength(); j++) {
                    list.add(component.getComponent(j));
                }
            }
        }
    }

    
    private static boolean checkComponents(ModuleElement[] components) {
        Ring ring = components[0].getModule().getRing();
        for (int i = 1; i < components.length; i++) {
            if (!ring.equals(components[i].getModule().getRing())) {
                return false;
            }
        }
        return true;
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        for (int i = 0; i < components.length; i++) {
            components[i].toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ModuleElement> elements = new LinkedList<ModuleElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            elements.add(moduleElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                elements.add(moduleElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ModuleElement[] coefficients = new ModuleElement[elements.size()];
            int i = 0;
            for (ModuleElement e : elements) {
                coefficients[i++] = e;
            }
            DirectSumElement result = new DirectSumElement(coefficients);
            return result;
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new DirectSumElement(ZRing.ring);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "DirectSumElement";
    }
    
    
    public int hashCode() {
        int hash = 7*"DirectSumElement".hashCode();
        hash = 37*hash+getRing().hashCode();
        for (int i = 0; i < getLength(); i++) {
            hash = 37*hash+getComponent(i).hashCode();
        }
        return hash;
    }

    
    private DirectSumElement(ModuleElement[] components) {
        this.components = components;
        this.ring = components[0].getModule().getRing();
    }

    
    private DirectSumElement(Ring ring) {
        this.components = null;
        this.module = DirectSumModule.makeNullModule(ring);
        this.ring = ring;
    }

    
    private ModuleElement[] components;
    private DirectSumModule module = null;
    private Ring            ring = null;
}
