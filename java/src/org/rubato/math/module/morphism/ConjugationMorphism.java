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

import static org.rubato.xml.XMLConstants.DIMENSION_ATTR;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The function that takes a complex number (or vector) to its conjugate.
 * 
 * @author Gérard Milmeister
 */
public final class ConjugationMorphism extends ModuleMorphism {

    public ConjugationMorphism(int dimension) {
        super(CProperFreeModule.make(dimension), CProperFreeModule.make(dimension));
        this.dimension = dimension;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (x instanceof CFreeElement) {
            CFreeElement c = (CFreeElement)x;
            if (c.getLength() == dimension) {
                return c.conjugated();
            }
        }
        throw new MappingException("ConjugationMorphism.map: ", x, this);
    }

    
    public boolean isRingHomomorphism() {
        return getDomain().isRing();
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    public ModuleMorphism compose(ModuleMorphism morphism)
            throws CompositionException {
        if (morphism instanceof ConjugationMorphism) {
            ConjugationMorphism m = (ConjugationMorphism)morphism;
            if (m.dimension == dimension) {
                return getIdentityMorphism(getDomain());
            }
            else {
                throw new CompositionException("ConjugationMorphism.compose: ", this, morphism);
            }
        }
        else {
            return super.compose(morphism);
        }
    }
    
    
    public ModuleElement atZero() {
        return getCodomain().getZero();
    }
    
    
    public ModuleMorphism power(int n)
            throws CompositionException {
        if (n % 2 == 0) {
            return getIdentityMorphism(getDomain());
        }
        else {
            return this;
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof ConjugationMorphism) {
            return getDomain().getDimension() == ((ConjugationMorphism)object).getDomain().getDimension(); 
        }
        else {
            return false;
        }
    }
    
    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof ConjugationMorphism) {
            return getDomain().getDimension()-((ConjugationMorphism)object).getDomain().getDimension();
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public String toString() {
        return "ConjugationMorphism["+getDomain().getDimension()+"]";
    }

    
    public void toXML(XMLWriter writer) {
        writer.openInline(MODULEMORPHISM, TYPE_ATTR, getElementTypeName(),
                          DIMENSION_ATTR, getDomain().getDimension());
    }

    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(DIMENSION_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), DIMENSION_ATTR);
            return null;
        }
        int dim = XMLReader.getIntAttribute(element, DIMENSION_ATTR, 0);
        return new ConjugationMorphism(dim);
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO = new ConjugationMorphism(0);

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ConjugationMorphism";
    }
    
    
    private int dimension;
}
