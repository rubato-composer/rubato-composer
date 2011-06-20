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

import java.util.LinkedList;
import java.util.List;

import org.rubato.math.arith.Folding;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a product ring.
 * @see org.rubato.math.module.ProductRing
 * 
 * @author Gérard Milmeister
 */
public class ProductElement extends RingElement implements ProductFreeElement {

    /**
     * Creates a new product element <i>e1</i>x<i>e1</i>.
     */
    public static ProductElement make(RingElement e1, RingElement e2) {
        return new ProductElement(new RingElement[] { e1, e2 });
    }

    
    /**
     * Creates a new product element <i>e1</i>x<i>e1</i>x<i>e3</i>.
     */
    public static ProductElement make(RingElement e1, RingElement e2, RingElement e3) {
        return new ProductElement(new RingElement[] { e1, e2, e3 });
    }

    
    /**
     * Creates a new product element from the array <code>factors</code>.
     */
    public static ProductElement make(RingElement[] factors) {
        if (factors.length < 2) {
            throw new IllegalArgumentException("A ProductRing must have at least 2 factors.");
        }
        else {
            return new ProductElement(factors);
        }
    }
        
    
    /**
     * Creates a new product element from the collection <code>factors</code>.
     */
    public static ProductElement make(List<RingElement> factors) {
        RingElement[] f = new RingElement[factors.size()];
        int i = 0;
        for (RingElement factor : factors) {
            f[i++] = factor;
        }
        return make(f);
    }
        
    
    public boolean isOne() {
        for (int i = 0; i < factors.length; i++) {
            if (!factors[i].isOne()) {
                return false;
            }
        }
        return true;
    }
    
    
    public boolean isZero() {
        for (int i = 0; i < factors.length; i++) {
            if (!factors[i].isZero()) {
                return false;
            }
        }
        return true;
    }
    
    
    public ProductElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            return sum((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    /**
     * Returns the sum of this element and <code>element</code>.
     */
    public ProductElement sum(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        RingElement newFactors[] = new RingElement[getFactorCount()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = getValue(i).sum(element.getValue(i));
        }
        return new ProductElement(newFactors);            
    }


    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            add((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    /**
     * Adds <code>element</code> to this element.
     */
    public void add(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        for (int i = 0; i < getFactorCount(); i++) {
            factors[i].add(element.getValue(i));
        }
    }


    public ProductElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            return difference((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    /**
     * Returns the difference of this element and <code>element</code>.
     */
    public ProductElement difference(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        RingElement newFactors[] = new RingElement[getFactorCount()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = getValue(i).difference(element.getValue(i));
        }
        return new ProductElement(newFactors);            
    }
    

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            subtract((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    /**
     * Subtracts <code>element</code> from this element.
     */
    public void subtract(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        for (int i = 0; i < getFactorCount(); i++) {
            factors[i].subtract(element.getValue(i));
        }
    }
    
    
    public ProductElement negated() {
        RingElement[] newFactors = new RingElement[getFactorCount()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = (RingElement)factors[i].negated();
        }
        return new ProductElement(newFactors);
    }

    
    public void negate() {
        for (int i = 0; i < getFactorCount(); i++) {
            factors[i].negate();
        }
    }

    
    public ProductElement scaled(RingElement element)
            throws DomainException {
        return product(element);
    }
    

    public void scale(RingElement element)
            throws DomainException {
        multiply(element);
    }
    

    public ProductElement product(RingElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            return product((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    /**
     * Returns the product of this element and <code>element</code>.
     */
    public ProductElement product(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        RingElement newFactors[] = new RingElement[getFactorCount()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = getValue(i).product(element.getValue(i));
        }
        return new ProductElement(newFactors);            
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof ProductElement) {
            multiply((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    /**
     * Multiplies this element with <code>element</code>.
     */
    public void multiply(ProductElement element)
            throws DomainException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        for (int i = 0; i < getFactorCount(); i++) {
            factors[i].multiply(element.getValue(i));
        }
    }

    
    public boolean isInvertible() {
        for (int i = 0; i < factors.length; i++) {
            if (!factors[i].isInvertible()) {
                return false;
            }
        }
        return true;
    }
    
    
    public ProductElement inverse() {
        RingElement newFactors[] = new RingElement[getFactorCount()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = getValue(i).inverse();
        }
        return ProductElement.make(newFactors);            
    }

    
    public void invert() {
        for (int i = 0; i < getFactorCount(); i++) {
            factors[i].invert();
        }
    }
    

    public ProductElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ProductElement) {
            return quotient((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public ProductElement quotient(ProductElement element)
            throws DomainException, DivisionException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        RingElement newFactors[] = new RingElement[getFactorCount()];
        try {
            for (int i = 0; i < newFactors.length; i++) {
                newFactors[i] = getValue(i).quotient(element.getValue(i));
            }
        }
        catch (DivisionException e) {
            throw new DivisionException(this, element);
        }
        return new ProductElement(newFactors);            
    }


    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ProductElement) {
            divide((ProductElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void divide(ProductElement element)
            throws DomainException, DivisionException {
        if (!getModule().equals(element.getModule())) {
            throw new DomainException(this.getModule(), element.getModule());
        }
        try {
            for (int i = 0; i < getFactorCount(); i++) {
                factors[i].divide(element.getValue(i));
            }
        }
        catch (DivisionException e) {
            throw new DivisionException(this, element);
        }
    }
    
    
    public boolean divides(RingElement element) {        
        if (getModule().equals(element.getModule())) {
            ProductElement e = (ProductElement)element;
            for (int i = 0; i < getFactorCount(); i++) {
                if (!getValue(i).divides(e.getValue(i))) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }


    public ProductElement power(int n) {
        if (n == 0) {
            return getRing().getOne();
        }
        
        ProductElement factor;
        
        if (n < 0) {
            if (isInvertible()) {
                factor = inverse();
                n = -n;
            }
            else {
                throw new InverseException("Inverse of "+this+" does not exist.");
            }
        }
        else {
            factor = clone();
        }
        
        // Finding leading bit in the exponent n
        int bpos = 31; // bits per int
        while ((n & (1 << bpos)) == 0) {
            bpos--;
        }

        ProductElement result = getRing().getOne();
        try {
            while (bpos >= 0) {
                result = result.product(result);
                if ((n & (1 << bpos)) != 0) {
                    result = result.product(factor);
                }
                bpos--;
            }
        }
        catch (DomainException e) {}

        return result;
    }
        

    public ProductRing getModule() {
        if (ring == null) {
            Ring[] rings = new Ring[factors.length];
            for (int i = 0; i < factors.length; i++) {
                rings[i] = factors[i].getRing();
            }
            ring = ProductRing.make(rings);
        }
        return ring;
    }

    
    public ProductRing getRing() {
        return getModule();
    }
    
    
    /**
     * Returns the number of factors of this product element.
     */
    public int getFactorCount() {
        return factors.length;
    }
    
    
    /**
     * Returns the factor at position <code>i</code> of this product element.
     */
    public RingElement getFactor(int i) {
        return getValue(i);
    }

    
    /**
     * Returns the factor at position <code>i</code> of this product element.
     */
    public RingElement getValue(int i) {
        return factors[i];
    }

    
    public ProductFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return ProductProperFreeElement.make(getModule(), new ProductElement[0]);
        }
        else {
            ProductElement[] values = new ProductElement[n];
            values[0] = this;
            for (int i = 1; i < n; i++) {
                values[i] = getRing().getZero();
            }
            return ProductProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ProductElement) {
            ProductElement element = (ProductElement)object;
            if (element.getFactorCount() != getFactorCount()) {
                return false;
            }
            for (int i = 0; i < getFactorCount(); i++) {
                if (!getValue(i).equals(element.getValue(i))) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    
    public int compareTo(ModuleElement object) {
        if (object instanceof ProductElement) {
            ProductElement element = (ProductElement)object;
            int d = getLength()-element.getLength();
            if (d != 0) {
                return d;
            }
            else {
                for (int i = 0; i < getFactorCount(); i++) {
                    int comp = getValue(i).compareTo(element.getValue(i));
                    if (comp != 0) {
                        return comp;
                    }
                }
                return 0;
            }
        }
        else {
            return super.compareTo(object);
        }
    }


    public ProductElement clone() {
        RingElement[] newFactors = new RingElement[factors.length];
        for (int i = 0; i < factors.length; i++) {
            newFactors[i] = factors[i].clone();
        }
        return ProductElement.make(newFactors);
    }

    
    public String stringRep(boolean ... parens) {
        StringBuilder buf = new StringBuilder(30);
        buf.append("(");
        if (factors.length > 0) {
            buf.append(factors[0].stringRep());
        }
        for (int i = 1; i < factors.length; i++) {
            buf.append(",");
            buf.append(factors[i].stringRep());
        }
        buf.append(")");
        return buf.toString();
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("ProductElement[");
        if (factors.length > 0) {
            buf.append(factors[0]);
        }
        for (int i = 1; i < factors.length; i++) {
            buf.append(",");
            buf.append(factors[i]);
        }
        buf.append("]");
        return buf.toString();
    }

    
    public double[] fold(ModuleElement[] elements) {
        double[][] res = new double[elements.length][];
        for (int i = 0; i < elements.length; i++) {
            res[i] = elements[i].fold(new ModuleElement[] { elements[i] } );
        }
        return Folding.fold(res);
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        for (int i = 0; i < getFactorCount(); i++) {
            getValue(i).toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            // ProductElement has children (i.e., factors)
            LinkedList<RingElement> elements = new LinkedList<RingElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof RingElement)) {
                reader.setError("Type %%1 must have ring element children.", getElementTypeName());
                return null;
            }
            elements.add((RingElement)moduleElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof RingElement)) {
                    reader.setError("Type %%1 must have ring element children.", getElementTypeName());
                    return null;
                }
                elements.add((RingElement)moduleElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            RingElement[] factors0 = new RingElement[elements.size()];
            int i = 0;
            for (RingElement e : elements) {
                factors0[i++] = e;
            }
            ProductElement result = new ProductElement(factors0);
            return result;
        }
        else {
            reader.setError("There must be at least 2 <%1> elements in type %%2.", MODULE, getElementTypeName());
            return null;
        }
        
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = ProductElement.make(new ZElement(0), new ZElement(0));
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ProductElement";
    }
    
    
    public int hashCode() {
        int hash = 7*basicHash;
        for (int i = 0; i < getLength(); i++) {
            hash = 37*hash+getValue(i).hashCode();
        }
        return hash;
    }
    
    
    protected ProductElement(RingElement[] factors) {
        this.factors = factors;
    }

    
    private RingElement[] factors;
    private ProductRing   ring = null;

    private final static int basicHash = "ProductElement".hashCode();
}
