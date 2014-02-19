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

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZRing;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents the composition of two arbitrary morphisms.
 * This class should in general not be used directly. The member function
 * <code>compose</code> of <code>ModuleMorphism</code> should be used instead.
 * Instances of this class are usually created as a last resort, if the
 * composition of two morphisms doesn't fit in any of the other classes. 
 * 
 * @author Gérard Milmeister
 */
public final class CompositionMorphism extends ModuleMorphism {

    /**
     * Constructs a morphism from <code>f</code> and <code>g</code>.
     * The resulting morphism <code>h</code> is such that <i>h(x) = f(g(x))</i>.
     * This is used instead of a constructor, so that simplifications
     * can be made.
     * 
     * @throws CompositionException if composition is not valid
     */
    static ModuleMorphism make(ModuleMorphism f, ModuleMorphism g)
        	throws CompositionException {
        if (!composable(f, g)) {
            throw new CompositionException("CompositionMorphism.make: Cannot compose "+f+" with "+g);
        }
        else if (f.isIdentity() && g.isIdentity()) {
            return ModuleMorphism.getIdentityMorphism(f.getDomain());
        }
        else if (f.isIdentity()) {
            return g;
        }
        else if (g.isIdentity()) {
            return f;
        }
        else if (f.isConstant()) {
        	return ModuleMorphism.getConstantMorphism(f.getCodomain(), f.atZero());
        }
        else if (g.isConstant()) {
            try {
                return ModuleMorphism.getConstantMorphism(f.getCodomain(), f.map(g.atZero()));
            }
            catch (MappingException e) {
                throw new CompositionException("CompositionMorphism.make: Cannot not compose "+f+" and "+g);
            }
        }
        else {
            return new CompositionMorphism(f, g);
        }
    }


    public ModuleElement map(ModuleElement x)
            throws MappingException {
        return f.map(g.map(x));
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

    
    public boolean isIdentity() {
        // this should never return true, because of simplifications
        // made in the virtual constructor
        return f.isIdentity() && g.isIdentity();
    }
    
    
    public boolean isConstant() {
        // this should never return true, because of simplifications
        // made in the virtual constructor
        return f.isConstant() || g.isConstant();
    }
    
    
    public ModuleMorphism getRingMorphism() {
        try {
            return f.getRingMorphism().compose(g.getRingMorphism());
        }
        catch (CompositionException e) {
            // this should never occur
            throw new AssertionError(e);
        }
    }
    
    
    
    /**
     * Returns the morphism <i>f</i> of the composition <i>f.g</i>.
     */
    public ModuleMorphism getFirstMorphism() {
        return f;
    }
    
    
    /**
     * Returns the morphism <i>g</i> of the composition <i>f.g</i>.
     */
    public ModuleMorphism getSecondMorphism() {
        return g;
    }
    

    public int compareTo(ModuleMorphism object) {
        if (object instanceof CompositionMorphism) {
            CompositionMorphism m = (CompositionMorphism)object;
            int res = f.compareTo(m.f);
            if (res == 0) {
                return g.compareTo(m.g);
            }
            else {
                return res;
            }
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof CompositionMorphism) {
            CompositionMorphism morphism = (CompositionMorphism)object;
            return f.equals(morphism.f) && g.equals(morphism.g);
        }
        else {
            return false;
        }        
    }

    
    public String toString() {
        return "CompositionMorphism["+f.toString()+","+g.toString()+"]";
    }
    
    
    private CompositionMorphism(ModuleMorphism f, ModuleMorphism g) {
        super(g.getDomain(), f.getCodomain());
        this.f = f;
        this.g = g;
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
                ModuleMorphism morphism = make(f0, g0);
                return morphism;
            }
            catch (CompositionException e) {
                reader.setError("Cannot compose morphism %%1 with %%2", f0.toString(), g0.toString());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>", getElementTypeName(), MODULEMORPHISM);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new CompositionMorphism(getIdentityMorphism(ZRing.ring), getIdentityMorphism(ZRing.ring));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "CompositionMorphism";
    }
    
    
    private ModuleMorphism f;
    private ModuleMorphism g;
}
