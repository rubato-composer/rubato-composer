/*
 * Copyright (C) 2005 Gérard Milmeister
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

package org.rubato.math.module.morphism;

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * These are generalized projections, i.e., factors of a product ring
 * are reordered, deleted or duplicated. A common example is the twisting
 * morphism that switches positions in pairs.
 * An ordering is an array of integers <i>a</i>:
 * The codomain product ring as a factor for each entry </i>a<sub>i</sub></i>
 * which gives the factor of domain.
 * Instances are created using the {@link #make(ProductRing,int[])} 
 * and {@link #make(Ring,int)} methods.
 * 
 * @see ProductRing
 * @author Gérard Milmeister
 */
public class ReorderMorphism extends ModuleMorphism {

    /**
     * Creates a reordering of the <code>domain</code> using the
     * <code>ordering</code> specification.
     * If ordering is null or the length of ordering is 0, an
     * identity morphism is returned.
     */
    public static ModuleMorphism make(ProductRing domain, int[] ordering) {
        if (ordering == null || ordering.length < 1) {
            return getIdentityMorphism(domain);
        }
        for (int i = 0; i < ordering.length; i++) {
            if (ordering[i] < 0 || ordering[i] >= domain.getFactorCount()) {
                ordering[i] = 0;
            }
        }
        if (ordering.length == 1) {
            return ProjectionMorphism.make(domain, ordering[0]);
        }
        else {
            Ring[] rings = new Ring[ordering.length];
            for (int i = 0; i < rings.length; i++) {
                rings[i] = domain.getFactor(ordering[i]);
            }
            return new ReorderMorphism(domain, ProductRing.make(rings), ordering);
        }
    }
    
    
    /**
     * Creates a morphism that "blows up" a <code>ring</code> into a product ring
     * with <code>fc</code> factors equal to <code>ring</code>. This simply
     * copies the value in the domain to the factors of the codomain.
     */
    public static ModuleMorphism make(Ring ring, int fc) {
        if (fc == 1) {
            return getIdentityMorphism(ring);
        }
        if (fc < 2) { fc = 2; }
        final int c = fc;
        int ordering[] = new int[fc];
        for (int i = 0; i < fc; i++) { ordering[i] = 0; }
        ModuleMorphism m = new ReorderMorphism(ring, ring, ordering) {
            public ModuleElement map(ModuleElement x)
            throws MappingException {
                if (!getDomain().hasElement(x)) {
                    throw new MappingException("ReorderMorphism.map: ", x, this);
                }
                RingElement p = (RingElement)x;
                RingElement[] factors = new RingElement[c];
                for (int i = 0; i < c; i++) {
                    factors[i] = p;
                }
                return ProductElement.make(factors);
            }
        };
        return m;
    }

    
    /**
     * Returns ordering specifcation.
     */
    public final int[] getOrdering() {
        return ordering;
    }
    
    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (!getDomain().hasElement(x)) {
            throw new MappingException(x, this);
        }
        ProductElement p = (ProductElement)x;
        RingElement[] factors = new RingElement[ordering.length];
        for (int i = 0; i < ordering.length; i++) {
            factors[i] = p.getFactor(ordering[i]);
        }
        return ProductElement.make(factors);
    }

    
    public boolean isRingHomomorphism() {
        return true;
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return this;
    }
    
    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ReorderMorphism) {
            ReorderMorphism m = (ReorderMorphism)object;
            if (getDomain().equals(m.getDomain())) {
                for (int i = 0; i < ordering.length; i++) {
                    if (ordering[i] != m.ordering[i]) {
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

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ReorderMorphism) {
            ReorderMorphism m = (ReorderMorphism)object;
            int res = getDomain().compareTo(m.getDomain());
            if (res == 0) {
                for (int i = 0; i < ordering.length; i++) {
                    if (ordering[i] < m.ordering[i]) {
                        return -1;
                    }
                    else if (ordering[i] > m.ordering[i]) {
                        return 1;
                    }
                }
                return 0;
            }
            else {
                return res;
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public String toString() {
        StringBuilder res = new StringBuilder(30);
        res.append("ReorderMorphism[");
        res.append(getDomain());
        res.append(",");
        res.append("{");
        res.append(ordering[0]);
        for (int i = 1; i < ordering.length; i++) {
            res.append(",");
            res.append(ordering[i]);
        }
        res.append("}]");
        return res.toString();
    }

    
    private static final String ORDERING_ATTR = "ordering";
    
    
    public void toXML(XMLWriter writer) {
        StringBuilder res = new StringBuilder();
        res.append(ordering[0]);
        for (int i = 1; i < ordering.length; i++) {
            res.append(",");
            res.append(ordering[i]);
        }
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName(), ORDERING_ATTR, res);
        getDomain().toXML(writer);
        writer.closeBlock();
    }

    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(ORDERING_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), ORDERING_ATTR);
            return null;
        }
        String[] values = element.getAttribute(ORDERING_ATTR).split(",");
        int[] order = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                order[i] = Integer.parseInt(values[i]);
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 in type %%2 must be a comma-separated list of integers.", ORDERING_ATTR, getElementTypeName());
                return null;
            }
        }
        
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            Module m = reader.parseModule(childElement);
            if (m == null) {
                return null;
            }
            if (m instanceof ProductRing) {
                return make((ProductRing)m, order);
            }
            else if (m instanceof Ring) {
                return make((Ring)m, order.length);
            }
            else {
                reader.setError("The module in type %%2 must be a ring.", getElementTypeName());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULE);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new ReorderMorphism(ZRing.ring, ZRing.ring, new int[] {0});

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ReorderMorphism";
    }
    
    
    protected ReorderMorphism(Ring domain, Ring codomain, int[] ordering) {
        super(domain, codomain);
        this.ordering = ordering;
    }
    
    
    private int[] ordering;
}
