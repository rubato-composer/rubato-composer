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

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.math.arith.Folding;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in the free module over a product ring.
 * @see org.rubato.math.module.ProductProperFreeModule
 * 
 * @author Gérard Milmeister
 */
public final class ProductProperFreeElement
		extends ProperFreeElement
		implements ProductFreeElement {

    /**
     * Creates a new product free element over the specified ring with given components.
     */
    public static ProductFreeElement make(ProductRing ring, ProductElement[] v) {
        if (v.length == 1) {
            return v[0];
        }
        else {
            return new ProductProperFreeElement(ring, v);
        }
    }
    

    /**
     * Creates a new product free element over the specified ring with given components.
     * In this case <code>v</code> must be of length > 0.
     */
    public static ProductFreeElement make(ProductElement[] v) {
        if (v.length == 1) {
            return v[0];
        }
        else {
            return new ProductProperFreeElement(v[0].getRing(), v);
        }
    }

    
    public boolean isZero() {
        for (int i = 0; i < getLength(); i++) {
            if (!getComponent(i).isZero()) {
                return false;
            }
        }
        return true;
    }
    
    
    public ProductProperFreeElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            return sum((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    /**
     * Returns the sum of this element with <code>element</code>.
     */
    public ProductProperFreeElement sum(ProductProperFreeElement element)
            throws DomainException {
        if (getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        ProductElement[] res = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].sum(element.value[i]);
        }
        return new ProductProperFreeElement(getRing(), res);        
    }
    

    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            add((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    /**
     * Adds <code>element</code> to this this element.
     */
    public void add(ProductProperFreeElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        for (int i = 0; i < getLength(); i++) {
            value[i].add(element.value[i]);
        }        
    }
    
    
    public ProductProperFreeElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            return difference((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    /**
     * Returns the difference of this element and <code>element</code>.
     */
    public ProductProperFreeElement difference(ProductProperFreeElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        ProductElement[] res = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].difference(element.value[i]);
        }
        return new ProductProperFreeElement(getRing(), res);
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            subtract((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    /**
     * Subtracts <code>element</code> from this element.
     */
    public void subtract(ProductProperFreeElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        for (int i = 0; i < getLength(); i++) {
            value[i].subtract(element.value[i]);
        }        
    }
    
    
    public ProductProperFreeElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            return productCW((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ProductProperFreeElement productCW(ProductProperFreeElement element)
            throws DomainException {
        if (getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        ProductElement[] res = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].product(element.value[i]);
        }
        return new ProductProperFreeElement(getRing(), res);        
    }
    
    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductProperFreeElement) {
            multiplyCW((ProductProperFreeElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void multiplyCW(ProductProperFreeElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }        
        for (int i = 0; i < getLength(); i++) {
            value[i].multiply(element.value[i]);
        }        
    }


    public ProductFreeElement negated() {
        ProductElement[] res = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].negated();
        }
        return new ProductProperFreeElement(getRing(), res);
    }
    

    public void negate() {
        for (int i = 0; i < getLength(); i++) {
            value[i].negate();
        }
    }
    

    public ProductProperFreeElement scaled(RingElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            return scaled((ProductElement)element);
        }
        else {
            throw new DomainException(ring, element.getRing());
        }
    }
    

    /**
     * Returns this element scaled by <code>element</code>.
     */
    public ProductProperFreeElement scaled(ProductElement element)
            throws DomainException {
        if (!element.getRing().equals(getRing())) {
            throw new DomainException(ring, element.getRing());
        }
        ProductElement res[] = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            res[i] = value[i].scaled(element);
        }
        return new ProductProperFreeElement(getRing(), res);        
    }
    
    
    public void scale(RingElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            scale((ProductElement)element);
        }
        else {
            throw new DomainException(ring, element.getRing());
        }
    }

    
    /**
     * Scales this element by </i>element</i>.
     */
    public void scale(ProductElement element)
            throws DomainException {
        if (!element.getRing().equals(getRing())) {
            throw new DomainException(ring, element.getRing());
        }
        for (int i = 0; i < getLength(); i++) {
            value[i].scale(element);
        }
    }
    
    
    /**
     * Returns the number of factors of the underlying product ring.
     */
    public int getFactorCount() {
        return ring.getFactorCount();
    }

    
    public ProductElement getComponent(int i) {
        assert(i < getLength());
        return value[i]; 
    }
    

    public ProductElement getRingElement(int i) {
        assert(i < getLength());
        return value[i]; 
    }
    

    public int getLength() {
        return value.length;
    }
    

    public ProductFreeModule getModule() {
        if (module == null) {
            module = ProductProperFreeModule.make(getRing(), getLength());
        }
        return module;
    }
    

    public ProductRing getRing() {
        return ring;
    }
    

    /**
     * Returns the product element components as an array.
     */
    public ProductElement[] getValue() {
        return value;
    }
    
    
    /**
     * Returns the <code>i</code>-th product element component.
     */
    public ProductElement getValue(int i) {
        return value[i];
    }
    

    public ProductFreeElement resize(int n) {
        if (n == getLength()) {
            return this;
        }
        else {
            int minlen = Math.min(n, getLength());
            ProductElement[] values = new ProductElement[n];
            for (int i = 0; i < minlen; i++) {
                values[i] = getValue(i);
            }
            for (int i = minlen; i < n; i++) {
                values[i] = getRing().getZero();
            }
            return ProductProperFreeElement.make(getRing(), values);
        }
    }
    

    public boolean equals(Object object) {
        if (object instanceof ProductProperFreeElement) {
            ProductProperFreeElement e = (ProductProperFreeElement)object;
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
        if (object instanceof ProductProperFreeElement) {
            ProductProperFreeElement element = (ProductProperFreeElement)object;
            int l = getLength()-element.getLength();
            if (l < 0) {
                return -1;
            }
            else if (l > 0) {
                return 1;
            }
            else {
                for (int i = 0; i < getLength(); i++) {
                    int comp = value[i].compareTo(element.value[i]);
                    if (comp < 0) {
                        return -1;
                    }
                    else if (comp > 0) {
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

    
    public ProductProperFreeElement clone() {
        ProductElement[] v = new ProductElement[getLength()];
        for (int i = 0; i < getLength(); i++) {
            v[i] = value[i].clone();
        }
        return new ProductProperFreeElement(getRing(), v);
    }
    

    public String stringRep(boolean ... parens) {
        if (getLength() == 0) {
            return "Null";
        }
        else {
            StringBuilder res = new StringBuilder(value[0].stringRep());
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
        buf.append("ProductFreeElement[");
        buf.append(getLength());
        buf.append("][");
        if (getLength() > 0) {
            buf.append(value[0].toString());
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
        for (int i = 0; i < elements.length; i++) {
            res[i] = elements[i].fold(new ModuleElement[] { elements[i] });
        }
        return Folding.fold(res);
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        getRing().toXML(writer);
        for (int i = 1; i < getLength(); i++) {
            value[i].toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement;
        
        // get product ring
        childElement = XMLReader.getChild(element, MODULE);
        if (childElement == null) {
            reader.setError("Type %%1 must have a first child of type <%2>.", getElementTypeName(), MODULE);
            return null;
        }
        Module module0 = reader.parseModule(childElement);
        if (module0 == null || !(module0 instanceof ProductRing)) {
            reader.setError("Module in %%1 must be a product ring.", getElementTypeName());
            return null;
        }
        ProductRing productRing = (ProductRing)module0;
        
        // get components
        childElement = XMLReader.getNextSibling(childElement, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ProductElement> elements = new LinkedList<ProductElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof ProductElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ProductElement");
                return null;                    
            }
            ProductElement productElement = (ProductElement)moduleElement;
            elements.add(productElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof ProductElement)) {
                    reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "ProductElement");
                    return null;                    
                }
                productElement = (ProductElement)moduleElement;
                elements.add(productElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ProductElement[] components = new ProductElement[elements.size()];
            Iterator<ProductElement> iter = elements.iterator();
            int i = 0;
            while (iter.hasNext()) {
                components[i++] = iter.next();
            }
            return ProductProperFreeElement.make(productRing, components);            
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;            
        }
    }

    
    private final static XMLInputOutput<ModuleElement> xmlIO = new ProductProperFreeElement(null, null);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ProductFreeElement";
    }
    
    
    public int hashCode() {
        int val = 31;
        for (int i = 0; i < getLength(); i++) {
            val = 7*val + value[i].hashCode();
        }
        return val;
    }

    
    private ProductProperFreeElement(ProductRing ring, ProductElement[] value) {
        this.value = value;
        this.ring = ring;
    }

    
    private ProductElement[]  value;
    private ProductFreeModule module = null;
    private ProductRing       ring   = null;
}
