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

import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents a scaled arbitrary morphism.
 * 
 * @author Gérard Milmeister
 */
public final class ScaledMorphism extends ModuleMorphism {

    /**
     * Create a morphism from <code>f</code> and scalar <code>value</code>.
     * The resulting morphism <i>h</i> is such that <i>h(x) = value*f(x)</i>.
     * This is a virtual constructor so that simplifications can be made.
     * 
     * @return null if <code>f</code> cannot be scaled by <code>value</code> 
     */
    public static ModuleMorphism make(ModuleMorphism f, RingElement scalar) {
        if (!f.getCodomain().getRing().hasElement(scalar)) {
            return null;
        }
        if (scalar.isOne()) {
            return f;
        }
        else if (scalar.isZero()) {
            return getConstantMorphism(f.getCodomain(), f.getCodomain().getZero());
        }
        else if (f.isConstant()) {
            try {
                return new ConstantMorphism(f.getDomain(), f.map(f.getDomain().getZero()).scaled(scalar));
            }
            catch (DomainException e) {
                throw new AssertionError("This should never happen!");
            }
            catch (MappingException e) {
                throw new AssertionError("This should never happen!");
            }
        }
        else {
            return new ScaledMorphism(f, scalar);
        }
    }
    
    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        try {
            return f.map(x).scaled(scalar);
        }
        catch (DomainException e) {
            throw new MappingException("ScaledMorphism.map: ", x, this);
        }
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ScaledMorphism) {
            ScaledMorphism m = (ScaledMorphism)object;
            int comp = f.compareTo(m.f);
            if (comp == 0) {
                return scalar.compareTo(m.scalar);
            }
            else {
                return comp;
            }
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean isModuleHomomorphism() {
        return f.isModuleHomomorphism();
    }

    
    public boolean isLinear() {
        return f.isLinear();
    }

    
    public boolean isConstant() {
        return f.isConstant();
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return f.getRingMorphism();
    }

    
    /**
     * Returns the morphism <i>f</i> from <i>a*f</i>.
     */
    public ModuleMorphism getMorphism() {
        return f;
    }
    

    /**
     * Returns the scalar <i>a</i> from <i>a*f</i>.
     */
    public RingElement getScalar() {
        return scalar;
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof ScaledMorphism) {
            ScaledMorphism morphism = (ScaledMorphism)object;
            return f.equals(morphism.f) && scalar.equals(morphism.scalar);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "ScaledMorphism["+f+","+scalar+"]";
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        f.toXML(writer);
        scalar.toXML(writer);
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEMORPHISM);
        if (childElement != null) {
            ModuleMorphism f0 = reader.parseModuleMorphism(childElement);
            Element el = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            if (el == null) {
                reader.setError("Type %%1 is missing second child of type <%2>.", getElementTypeName(), MODULEELEMENT);
                return null;                
            }
            ModuleElement value = reader.parseModuleElement(el);
            if (f0 == null || value == null) {
                return null;
            }
            if (value instanceof RingElement) {
                ModuleMorphism m = make(f0, (RingElement)value);
                if (m == null) {
                    reader.setError("Cannot scale %1 by %2.", f0, value);
                }
                return m;
            }
            else {
                reader.setError("The ModuleElement in type %%1 must be a ring element.", getElementTypeName());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEMORPHISM);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new ScaledMorphism(getIdentityMorphism(ZRing.ring), new ZElement(0));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ScaledMorphism";
    }
    
    
    private ScaledMorphism(ModuleMorphism f, RingElement scalar) {
        super(f.getDomain(), f.getCodomain());
        this.f = f;
        this.scalar = scalar;
    }

    
    private ModuleMorphism f;
    private RingElement    scalar;
}
