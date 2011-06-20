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

package org.rubato.math.module.morphism;

import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.DomainException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZRing;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents the difference of two arbitrary morphisms.
 *
 * @author Gérard Milmeister
 */
public final class DifferenceMorphism extends ModuleMorphism {
    
    /**
     * Creates a morphism from <code>f</code> and <code>g</code>.
     * The resulting morphism h is such that h(x) = f(x)-g(x).
     * 
     * @throws CompositionException if difference is not valid
     */
    public static ModuleMorphism make(ModuleMorphism f, ModuleMorphism g)
            throws CompositionException {
        if (!f.getDomain().equals(g.getDomain()) ||
            !f.getCodomain().equals(g.getCodomain())) {
            throw new CompositionException("DifferenceMorphism.make: Cannot subtract "+g+" from "+f);
        }
        else if (f.isConstant() && g.isConstant()) {
            try {
                ModuleElement zero = f.getDomain().getZero();
                ModuleElement fe = f.map(zero);
                ModuleElement ge = g.map(zero);
                return new ConstantMorphism(fe.difference(ge));
            }
            catch (DomainException e) {                
                throw new AssertionError("This should never happen!");
            }
            catch (MappingException e) {
                throw new AssertionError("This should never happen!");
            }
        }
        else {
            return new DifferenceMorphism(f, g);
        }
    }
    
    
    private DifferenceMorphism(ModuleMorphism f, ModuleMorphism g) {
        super(f.getDomain(), f.getCodomain());
        this.f = f;
        this.g = g;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        try {
            return f.map(x).difference(g.map(x));
        }
        catch (DomainException e) {
            throw new MappingException("DifferenceMorphism.map: ", x, this);
        }
    }

    
    public boolean isModuleHomomorphism() {
        return f.isModuleHomomorphism() && g.isModuleHomomorphism();
    }
    

    public boolean isRingHomomorphism() {
        return f.isRingHomomorphism() && g.isRingHomomorphism();
    }
    
    
    public boolean isLinear() {
        return f.isLinear() && g.isLinear();
    }
    
    
    /**
     * Returns the morphism <i>f</i> of the difference <i>f-g</i>.
     */
    public ModuleMorphism getFirstMorphism() {
        return f;
    }
    
    
    /**
     * Returns the morphism <i>g</i> of the difference <i>f-g</i>.
     */
    public ModuleMorphism getSecondMorphism() {
        return g;
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return f.getRingMorphism();
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof DifferenceMorphism) {
            DifferenceMorphism morphism = (DifferenceMorphism)object;
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
        if (object instanceof DifferenceMorphism) {
            DifferenceMorphism m = (DifferenceMorphism)object;
            return f.equals(m.f) && g.equals(m.g);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "DifferenceMorphism["+f+","+g+"]";
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
                ModuleMorphism morphism = DifferenceMorphism.make(f0, g0);
                return morphism;
            }
            catch (CompositionException e) {
                reader.setError("Cannot take the difference of the two morphisms %1 and %2.", f0.toString(), g0.toString());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEMORPHISM);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new DifferenceMorphism(getIdentityMorphism(ZRing.ring), getIdentityMorphism(ZRing.ring));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "DifferenceMorphism";
    }
    
    
    private ModuleMorphism f;
    private ModuleMorphism g;
}
