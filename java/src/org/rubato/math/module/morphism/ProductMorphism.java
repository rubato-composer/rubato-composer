/*
 * Copyright (C) 2006 Gérard Milmeister
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

import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents the product of two morphisms with
 * the same domains and codomains, respectively,
 * provided that the codomain is a ring.
 * 
 * @author Gérard Milmeister
 */
public final class ProductMorphism extends ModuleMorphism {

    /**
     * Creates a morphism from <code>f</code> and <code>g</code>.
     * The resulting morphism <i>h</i> is such that <i>h(x) = f(x)*g(x)</i>.
     * The codomain must be a ring
     *
     * @throws CompositionException if the product is not valid
     */
    public static ModuleMorphism make(ModuleMorphism f, ModuleMorphism g)
            throws CompositionException {
        if (!f.getDomain().equals(g.getDomain()) ||
            !f.getCodomain().equals(g.getCodomain()) ||
            !f.getCodomain().isRing()) {
            throw new CompositionException("ProductMorphism.make: Cannot multiply "+g+" to "+f);
        }
        else if (f.isIdentity()) {
            return g;
        }
        else if (g.isIdentity()) {
            return f;
        }
        else if (f.isConstant() && g.isConstant()) {
            try {
                RingElement fe = (RingElement)((ConstantMorphism)f).getValue();
                RingElement ge = (RingElement)((ConstantMorphism)g).getValue();
                return ModuleMorphism.getConstantMorphism(f.getDomain(), fe.product(ge));
            }
            catch (DomainException e) {
                throw new AssertionError("Should never happen!");
            }
        }
        else {
            return new ProductMorphism(f, g);
        }
    }
    
    
    private ProductMorphism(ModuleMorphism f, ModuleMorphism g) {
        super(f.getDomain(), f.getCodomain());
        this.f = f;
        this.g = g;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        try {
            return ((RingElement)f.map(x)).product((RingElement)g.map(x));
        }
        catch (DomainException e) {
            throw new MappingException("ProductMorphism.map: ", x, this);
        }
    }
    
    
    public boolean isModuleHomomorphism() {
        return false;
    }

    
    public boolean isRingHomomorphism() {
        return f.isRingHomomorphism() && g.isRingHomomorphism();
    }
    
    
    public boolean isLinear() {
        return false;
    }
    
    
    public boolean isConstant() {
        return f.isConstant() && g.isConstant();
    }
    
    
    /**
     * Returns the first morphism <i>f</i> of the product <i>f*g</i>.
     */
    public ModuleMorphism getFirstMorphism() {
        return f;
    }
    
    
    /**
     * Returns the second morphism <i>g</i> of the sum <i>f*g</i>.
     */
    public ModuleMorphism getSecondMorphism() {
        return g;
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return f.getRingMorphism();
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ProductMorphism) {
            ProductMorphism morphism = (ProductMorphism)object;
            int comp = f.compareTo(morphism.f);
            if (comp == 0) {
                return g.compareTo(morphism.g);
            }
            else {
                return comp;
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public boolean equals(Object object) {
        if (object instanceof ProductMorphism) {
            ProductMorphism m = (ProductMorphism)object;
            return f.equals(m.f) && g.equals(m.g);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "ProductMorphism["+f+","+g+"]";
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        f.toXML(writer);
        g.toXML(writer);
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEMORPHISM);
        if (childElement != null) {
            ModuleMorphism f0 = reader.parseModuleMorphism(childElement);
            Element el = XMLReader.getNextSibling(childElement, MODULEMORPHISM);
            if (el == null) {
                reader.setError("Type %%1 is missing second child of type <%2>.", getElementTypeName(), MODULEMORPHISM);
                return null;                
            }
            ModuleMorphism g0 = reader.parseModuleMorphism(el);
            if (f0 == null || g0 == null) {
                return null;
            }
            try {
                ModuleMorphism morphism = ProductMorphism.make(f0, g0);
                return morphism;
            }
            catch (CompositionException e) {
                reader.setError("Cannot take the product of the two morphisms %1 and %2.", f0, g0);
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEMORPHISM);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new ProductMorphism(getIdentityMorphism(ZRing.ring), getIdentityMorphism(ZRing.ring));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "SumMorphism";
    }
    
    
    private ModuleMorphism f;
    private ModuleMorphism g;
}
