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
 * Morphism that represents an iterated arbitrary morphism.
 * 
 * @author Gérard Milmeister
 */
public final class PowerMorphism extends ModuleMorphism {

    /**
     * Creates a morphism from <code>f</code> raised to <code>power</code>.
     * The resulting morphism h is such that h(x) = f(f(...(f(x))...)),
     * where there are <code>exp</code> repetitions of <code>f</code>.
     * This is a virtual constructor, so that simplifications can be made.
     */
    public static ModuleMorphism make(ModuleMorphism f, int exp)
        	throws CompositionException {
        if (!f.getDomain().equals(f.getCodomain())) {
            throw new CompositionException("PowerMorphism.make: Cannot raise "+f+" to power "+exp);
        }
        else if (exp < 0) {
            throw new CompositionException("PowerMorphism.make: Cannot raise "+f+" to a negative power "+exp);
        }
        else if (exp == 0) {
            return ModuleMorphism.getIdentityMorphism(f.getDomain());
        }
        else if (exp == 1) {
            return f;
        }
        else if (f.isIdentity()) {
            return f;
        }
        else if (f.isConstant()) {
            return f;
        }
        else {
            return new PowerMorphism(f, exp);
        }
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        ModuleElement res = x;
        for (int i = 1; i < exponent; i++) {
            res = f.map(res);
        }
        return res;
    }

    
    public boolean isModuleHomomorphism() {
        return f.isModuleHomomorphism();
    }

    
    public boolean isRingHomomorphism() {
        return f.isRingHomomorphism();        
    }
    
    
    public boolean isLinear() {
        return f.isLinear();
    }
    
    
    public boolean isIdentity() {
        return exponent == 0 || f.isIdentity();
    }
    
    
    public boolean isConstant() {
        return f.isConstant();
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    /**
     * Returns the base morphism <i>f</i> of the power <i>f^n</i>.
     */
    public ModuleMorphism getBaseMorphism() {
        return f;
    }
    
    /**
     * Returns the exponent <i>n</i> of the power <i>f^n</i>. 
     */
    public int getExponent() {
        return exponent;
    }
    
    
    public ModuleMorphism power(int n)
            throws CompositionException {
        return make(f, exponent+n);
    }
    
    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof PowerMorphism) {
            PowerMorphism morphism = (PowerMorphism)object;
            int res = f.compareTo(morphism.f);
            if (res == 0) {
                return exponent-morphism.exponent;
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
        if (object instanceof PowerMorphism) {
            PowerMorphism morphism = (PowerMorphism)object;
            return (f.equals(morphism.f) && exponent == morphism.exponent);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "PowerMorphism["+f+","+exponent+"]";
    }
    
    
    private final static String POWER_ATTR = "power";

    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName(), POWER_ATTR, exponent);
        f.toXML(writer);
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(POWER_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), POWER_ATTR);
            return null;
        }
        int power;
        try {
            power = Integer.parseInt(element.getAttribute(POWER_ATTR));
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", POWER_ATTR, getElementTypeName());
            return null;
        }
        Element childElement = XMLReader.getChild(element, MODULEMORPHISM);
        if (childElement != null) {
            ModuleMorphism f0 = reader.parseModuleMorphism(childElement);
            if (f0 == null) {
                return null;
            }
            try {
                return make(f0, power);
            }
            catch (CompositionException e) {
                reader.setError(e.getMessage());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULEMORPHISM);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new PowerMorphism(getIdentityMorphism(ZRing.ring), 0);

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "PowerMorphism";
    }
    
    
    private PowerMorphism(ModuleMorphism f, int exp) {
        super(f.getDomain(), f.getCodomain());
        this.f = f;
        this.exponent = exp;
    }

    
    private ModuleMorphism f;
    private int            exponent;
}
