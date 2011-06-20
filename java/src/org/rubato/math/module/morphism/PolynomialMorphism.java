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
 * Polynomial mappings.
 * 
 * @author Gérard Milmeister
 */
public class PolynomialMorphism extends ModuleMorphism {
    
    /**
     * Creates a mapping which evaluates the given <code>polynomial</code>.
     */
    public PolynomialMorphism(PolynomialElement polynomial) {
        super(polynomial.getRing().getCoefficientRing(),
              polynomial.getRing().getCoefficientRing());
        this.polynomial = polynomial;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (x instanceof RingElement) {
            try {
                return polynomial.evaluate((RingElement)x);
            }
            catch (DomainException e) {
                throw new MappingException("PolynomialMorphism.map: ", x, this);
            }
        }
        else {
            throw new MappingException("PolynomialMorphism.map: ", x, this);
        }
    }

    
    public boolean isModuleHomomorphism() {
        return polynomial.getDegree() == 1;
    }

    
    public boolean isRingHomomorphism() {
        return isIdentity() || polynomial.isZero();
    }
    
    
    public boolean isLinear() {
        return polynomial.getDegree() == 1
               && polynomial.getCoefficient(0).isZero();
    }
    

    public boolean isIdentity() {
        return polynomial.getDegree() == 1
               && polynomial.getCoefficient(0).isZero()
               && polynomial.getCoefficient(1).isOne();
    }
    

    public boolean isConstant() {
        return polynomial.getDegree() == 0;
    }
    
    
    /**
     * Returns the mapping's polynomial.
     */
    public PolynomialElement getPolynomial() {
        return polynomial;
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof PolynomialMorphism) {
            PolynomialMorphism morphism = (PolynomialMorphism)object;
            return polynomial.compareTo(morphism.polynomial);
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        else if (object instanceof PolynomialMorphism) {
            PolynomialMorphism morphism = (PolynomialMorphism)object;
            return polynomial.equals(morphism.polynomial);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "PolynomialMorphism["+polynomial+"]";
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        getPolynomial().toXML(writer);
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (moduleElement instanceof PolynomialElement) {
                return new PolynomialMorphism((PolynomialElement)moduleElement);
            }
            else {
                reader.setError("Type %%1 is missing child of type %%2.", getElementTypeName(), "PolynomialElement");
                return null;                
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new PolynomialMorphism(new PolynomialElement("X", new ZElement(0)));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "PolynomialMorphism";
    }
    
    
    private PolynomialElement polynomial;
}
