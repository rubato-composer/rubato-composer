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
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.*;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Products over rings.
 * Product rings in this class always have at least 2 factors.
 * This is ensured through the use of <code>make</code> constructors.
 * If a product ring with one factor should be requested, the factor
 * ring is returned instead.
 * @see org.rubato.math.module.ProductElement
 * 
 * @author Gérard Milmeister
 */
public final class ProductRing
        extends Ring
        implements ProductFreeModule {
    
    /**
     * Creates a new product ring <i>r1</i>x<i>r2</i>.
     */
    public static ProductRing make(Ring r1, Ring r2) {
        return lookup(new Ring[] {r1, r2});
    }
    

    /**
     * Creates a new product ring <i>r1</i>x<i>r2</i>x<i>r3</i>.
     */
    public static ProductRing make(Ring r1, Ring r2, Ring r3) {
        return lookup(new Ring[] {r2, r2, r3});
    }
    

    /**
     * Creates a new product ring from the factors in specified array.
     * 
     * @param factors factors[i] must not be null
     * @throws IllegalArgumentException if there are less than 2 factors
     */
    public static ProductRing make(Ring[] factors) {
        if (factors.length < 2) {
            throw new IllegalArgumentException("A ProductRing must have at least 2 factors.");
        }
        else {
            return lookup(factors);
        }
    }

    
    /**
     * Creates a new product ring from the factors in the specified collection.
     */
    public static ProductRing make(List<Ring> rings) {
        return make(rings.toArray(new Ring[rings.size()]));
    }
    

    public static ProductRing product(ProductRing r1, ProductRing r2) {
        int n = r1.getFactorCount()+r2.getFactorCount();
        Ring[] factors = new Ring[n];
        int r1Count = r1.getFactorCount();
        int r2Count = r2.getFactorCount();
        int i;
        for (i = 0; i < r1Count; i++) {
            factors[i] = r1.getFactor(i);
        }
        for (i = 0; i < r2Count; i++) {
            factors[i+r1Count] = r2.getFactor(i);
        }
        return make(factors);
    }

    
    public static ProductRing product(ProductRing r1, Ring r2) {
        int n = r1.getFactorCount()+1;
        Ring[] factors = new Ring[n];
        int r1Count = r1.getFactorCount();
        int i;
        for (i = 0; i < r1Count; i++) {
            factors[i] = r1.getFactor(i);
        }
        factors[n-1] = r2;
        return make(factors);
    }

    
    public static ProductRing product(Ring r1, ProductRing r2) {
        int n = r2.getFactorCount()+1;
        Ring[] factors = new Ring[n];
        int r2Count = r2.getFactorCount();
        factors[0] = r1;
        int i;
        for (i = 0; i < r2Count; i++) {
            factors[i+1] = r2.getFactor(i);
        }
        return make(factors);
    }

    
    public ProductElement getZero() {
        RingElement[] val = new RingElement[getFactorCount()];
        for (int i = 0; i < getFactorCount(); i++) {
            val[i] = getFactor(i).getZero();
        }
        return ProductElement.make(val);
    }


    public ProductElement getOne() {
        RingElement[] val = new RingElement[getFactorCount()];
        for (int i = 0; i < getFactorCount(); i++) {
            val[i] = getFactor(i).getOne();
        }
        return ProductElement.make(val);
    }


    public ProductFreeModule getNullModule() {
        return ProductProperFreeModule.make(this, 0);
    }
    
    
    public int getFactorCount() {
        return factors.length;
    }

    
    public Ring[] getFactors() {
        return factors;
    }

    
    public Ring getFactor(int i) {
        return factors[i];
    }

    
    public boolean isField() {
        return false;
    }
    
    
    public boolean isVectorspace() {
        return false;
    }


    public ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }

    
    public boolean hasElement(ModuleElement element) {
        return equals(element.getModule());
    }

    
    public ProductFreeModule getFreeModule(int dimension) {
        return ProductProperFreeModule.make(this, dimension);
    }

    
    public boolean equals(Object object) {
        return this == object;
    }

    
    public int compareTo(Module object) {
        if (this == object) {
            return 0;
        }
        else if (object instanceof ProductRing) {
            ProductRing r = (ProductRing)object;
            int len = Math.max(getFactorCount(), r.getFactorCount());
            for (int i = 0; i < len; i++) {
                int c = getFactor(i).compareTo(r.getFactor(i));
                if (c != 0) {
                    return c;
                }                
            }
            return getFactorCount()-r.getFactorCount();
        }
        else  {
            return super.compareTo(this);
        }
    }


    public ProductElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getFactorCount()) {
            return null;
        }
        RingElement[] ringElements = new RingElement[getFactorCount()];
        Iterator<ModuleElement> iter = elements.iterator();        
        for (int i = 0; i < getFactorCount(); i++) {
            ModuleElement object = iter.next();
            if (object instanceof RingElement) {
                RingElement element = (RingElement)object;
                element = (RingElement)getFactor(i).cast(element);
                if (element == null) {
                    return null;
                }
                ringElements[i] = element;
                
            }
            else {
                return null;
            }
        }
        return ProductElement.make(ringElements);
    }

    
    public ProductElement cast(ModuleElement element) {
        if (element instanceof ProductElement) {
            ProductElement pe = (ProductElement)element;
            if (pe.getFactorCount() < getFactorCount()) {
                return null;
            }
            RingElement[] factors0 = new RingElement[getFactorCount()];
            for (int i = 0; i < getFactorCount(); i++) {
                RingElement factor = (RingElement)getFactor(i).cast(pe.getFactor(i));
                if (factor == null) {
                    return null;
                }
                else {
                    factors0[i] = factor;
                }
            }
            return ProductElement.make(factors0);
        }
        else {
            return null;
        }
    }


    public String toString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("ProductRing[");
        if (getFactorCount() > 0) {
            buf.append(getFactor(0));
        }
        for (int i = 1; i < getFactorCount(); i++) {
            buf.append(",");
            buf.append(getFactor(i));
        }
        buf.append("]");
        return buf.toString();
    }

    
    public String toVisualString() {
        StringBuilder buf = new StringBuilder(30);
        if (getFactorCount() > 0) {
            buf.append(getFactor(0).toVisualString());
        }
        for (int i = 1; i < getFactorCount(); i++) {
            buf.append("x");
            buf.append(getFactor(i).toVisualString());
        }
        return buf.toString();
    }
    
    
    public ProductElement parseString(String string) {
        String s = string.trim();
        String re = "\\(.*\\)";
        if (s.matches(re)) {
            ArrayList<String> m = parse(s.substring(1, s.length()-1));
            if (m.size() != getFactorCount()) {
                return null;
            }            
            RingElement factors0[] = new RingElement[getFactorCount()];
            for (int i = 0; i < m.size(); i++) {
                factors0[i] = getFactor(i).parseString(m.get(i).trim());
                if (factors0[i] == null) {
                    return null;
                }
            }
            return ProductElement.make(factors0);
        }
        return null;
    }

    
    private ArrayList<String> parse(String s) {
        int pos = 0;
        int lastpos = 0;
        int level = 0;
        ArrayList<String> m = new ArrayList<String>();
        while (pos < s.length()) {
            if (s.charAt(pos) == '(') {
                pos++;
                level++;
            }
            else if (s.charAt(pos) == ')') {
                pos++;
                level--;
            }
            else if (s.charAt(pos) == ',' && level == 0) {
                m.add(s.substring(lastpos, pos));                
                pos++;
                lastpos = pos;
            }
            else {
                pos++;
            }
        }
        m.add(s.substring(lastpos,pos));
        return m;
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULE, getElementTypeName());
        for (int i = 0; i < getFactorCount(); i++) {
            getFactor(i).toXML(writer);
        }
        writer.closeBlock();
    }
        
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement == null) {
            reader.setError("Missing element <%1> in type %%2.", MODULE, getElementTypeName());
        }
        LinkedList<Ring> factors0 = new LinkedList<Ring>();
        while (childElement != null) {
            Module module = reader.parseModule(childElement);
            if (module == null || !(module instanceof Ring)) {
                reader.setError("Module in %%1 must be a ring.", getElementTypeName());
                return null;
            }
            factors0.add((Ring)module);
            childElement = XMLReader.getNextSibling(childElement, MODULE);
        }
        if (factors0.size() < 2) {
            reader.setError("There must be at least 2 elements <%1> in type %%2.", MODULE, getElementTypeName());
            return null;
        }
        else {
            return ProductRing.make(factors0);
        }
    }

    
    public String getElementTypeName() {
        return "ProductRing";
    }
    

    public static XMLInputOutput<Module> getXMLInputOutput() {
        return new ProductRing(null);
    }
    

    public int hashCode() {
        if (hashcode == 0) {
            hashcode = basicHash;
            for (int i = 0; i < getFactorCount(); i++) {
                hashcode = 7*hashcode+getFactor(i).hashCode();
            }
        }
        return hashcode;
    }

    
    protected ProductRing(Ring[] factors) {
        this.factors = factors;
    }
    
    
    private static ProductRing lookup(Ring[] factors) {
        List<Ring> factorList = Arrays.asList(factors);
        ProductRing r = cache.get(factorList);
        if (r == null) {
            r = new ProductRing(factors);
            cache.put(factorList, r);
        }
        return r;
    }
       
    
    private Ring[] factors;

    private final static int basicHash = "ProductRing".hashCode();
    private int hashcode = 0;
    private static HashMap<List<Ring>,ProductRing> cache = new HashMap<List<Ring>,ProductRing>();
}
