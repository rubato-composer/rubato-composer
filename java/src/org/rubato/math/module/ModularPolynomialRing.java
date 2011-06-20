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

import static org.rubato.xml.XMLConstants.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The ring of polynomials with coefficients in a specified ring
 * modulo another polynomial.
 * @see org.rubato.math.module.PolynomialElement
 * 
 * @author Gérard Milmeister
 */
public final class ModularPolynomialRing
        extends Ring
        implements ModularPolynomialFreeModule {

    public static ModularPolynomialRing make(PolynomialElement modulus) {
        if (modulus.getCoefficientRing().isField()) {
            return new ModularPolynomialRing(modulus);
        }
        else {
            return null;
        }
    }
    
    
    public String getIndeterminate() {
        return indeterminate;
    }
    
    
    public boolean hasIndeterminate(String indet) {
        if (getIndeterminate().equals(indet)) {
            return true;
        }
        else if (coefficientRing instanceof ModularPolynomialRing) {
            return ((ModularPolynomialRing)coefficientRing).hasIndeterminate(indet);
        }
        else if (coefficientRing instanceof PolynomialRing) {
            return ((PolynomialRing)coefficientRing).hasIndeterminate(indet);
        }
        else {
            return false;
        }
    }
    
    
    public List<String> getIndeterminates() {
        if (coefficientRing instanceof ModularPolynomialRing) {
            List<String> indeterminates = ((ModularPolynomialRing)coefficientRing).getIndeterminates();
            indeterminates.add(0, getIndeterminate());
            return indeterminates;
        }
        else if (coefficientRing instanceof PolynomialRing) {
            List<String> indeterminates = ((PolynomialRing)coefficientRing).getIndeterminates();
            indeterminates.add(0, getIndeterminate());
            return indeterminates;
        }
        else {
            List<String> indeterminates = new LinkedList<String>();
            indeterminates.add(getIndeterminate());
            return indeterminates;
        }
    }
    
    
    public Ring getCoefficientRing() {
        return coefficientRing;
    }
    
    
    public Ring getBaseRing() {
        return baseRing;
    }
    
    
    public PolynomialElement getModulus() {
        return modulus;
    }
    
    
    public PolynomialRing getModulusRing() {
        return modulusRing;
    }
    
    
    public ModularPolynomialElement getZero() {
        return new ModularPolynomialElement(this, new RingElement[] { getCoefficientRing().getZero() } );
    }

    
    public ModularPolynomialElement getOne() {
        return new ModularPolynomialElement(this, new RingElement[] { getCoefficientRing().getOne() } );
    }

    
    public ModularPolynomialFreeModule getNullModule() {
        return ModularPolynomialProperFreeModule.make(getModulus(), 0);
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
        if (element instanceof ModularPolynomialElement) {
            return element.getModule().equals(this);
        }
        else {
            return false;
        }
    }

    
    public ModularPolynomialFreeModule getFreeModule(int dimension) {
        return ModularPolynomialProperFreeModule.make(getModulus(), dimension);
    }

    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ModularPolynomialRing) {
            ModularPolynomialRing r = (ModularPolynomialRing)object;
            return getModulus().equals(r.getModulus());
        }
        else {
            return false;
        }
    }

    
    public int compareTo(Module object) {
        if (object instanceof ModularPolynomialRing) {
            ModularPolynomialRing p = (ModularPolynomialRing)object;
            int c;
            if ((c = getCoefficientRing().compareTo(p.getCoefficientRing())) != 0) {
                return c;
            }
            else {
                return getIndeterminate().compareTo(p.getIndeterminate());
            }
        }
        else {
            return super.compareTo(object);
        }
    }


    public ModularPolynomialElement createElement(List<ModuleElement> elements) {
        if (elements.size() == 1) {
            if (hasElement(elements.get(0))) {
                return (ModularPolynomialElement)elements.get(0);
            }
        }
        RingElement[] coeffs = new RingElement[elements.size()];
        int i = 0;
        for (ModuleElement e : elements) {
            coeffs[i] = (RingElement)getCoefficientRing().cast(e);
            if (coeffs[i] == null) {
                return null;
            }
        }
        return new ModularPolynomialElement(this, coeffs);
    }

    
    public ModularPolynomialElement cast(ModuleElement element) {
        if (this.equals(element.getModule())) {
            return (ModularPolynomialElement)element;
        }
        else if (element instanceof PolynomialElement) {
            PolynomialElement p = (PolynomialElement)element;
            RingElement[] coeffs = p.getCoefficients();
            RingElement[] newCoeffs = new RingElement[coeffs.length];
            Ring ring = getCoefficientRing();
            for (int i = 0; i < coeffs.length; i++) {
                newCoeffs[i] = (RingElement)ring.cast(coeffs[i]);
                if (newCoeffs[i] == null) {
                    return null;
                }                
            }
            return new ModularPolynomialElement(this, newCoeffs);
        }
        else if (element instanceof RingElement) {
            RingElement newCoeff = (RingElement)getCoefficientRing().cast(element);
            if (newCoeff == null) {
                return null;
            }
            else {
                return new ModularPolynomialElement(this, new RingElement[] { newCoeff });
            }
        }
        else {
            return null;
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(40);
        buf.append("ModularPolynomialRing(");
        buf.append(getBaseRing());
        buf.append(")[");
        buf.append(modulus.stringRep());
        buf.append("]");
        return buf.toString();
    }

    
    public String toVisualString() {
        Ring baseRing = getBaseRing();
        String s = "";
        if (baseRing instanceof ProductRing) {
            s += "(";
            s += baseRing.toVisualString();
            s += ")";
        }
        else {
            s += baseRing.toVisualString();
        }
        s += "[";
        Iterator<String> iter = getIndeterminates().iterator();
        String in = iter.next();
        s += in;
        while (iter.hasNext()) {
            s += ","+iter.next();
        }
        s += "]/(";
        s += getModulus().stringRep();
        s += ")";
        return s;
    }
    
    
    public ModularPolynomialElement parseString(String string) {
        PolynomialElement p = modulusRing.parseString(string);
        if (p != null) {
            try {
                return new ModularPolynomialElement(this, p);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULE, getElementTypeName());
        modulus.toXML(writer);
        writer.closeBlock();
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        element = XMLReader.getChild(element, MODULEELEMENT);
        if (element != null) {
            ModuleElement m = reader.parseModuleElement(element);
            if (m != null && m instanceof PolynomialElement) {
                return new ModularPolynomialRing((PolynomialElement)m);
            }
        }
        reader.setError("Type %%1 is missing child of type %%2.", getElementTypeName(), "PolynomialElement");
        return null;
    }
    
    
    private final static XMLInputOutput<Module> xmlIO = ModularPolynomialRing.make(PolynomialRing.make(QRing.ring,"X").getOne());
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return xmlIO;
    }

    
    public String getElementTypeName() {
        return "ModularPolynomialRing";
    }

    
    public int hashCode() {
        int hashCode = basicHash;
        hashCode ^= modulus.hashCode();
        return hashCode;
    }

    
    private final static int basicHash = "ModularPolynomialRing".hashCode();


    private ModularPolynomialRing(PolynomialElement modulus) {
        this.modulus         = modulus;
        this.indeterminate   = modulus.getIndeterminate();
        this.coefficientRing = modulus.getCoefficientRing();
        this.baseRing        = coefficientRing;
        this.modulusRing     = modulus.getRing();
    }
    
    
    private Ring              coefficientRing;
    private Ring              baseRing;
    private String            indeterminate;
    private PolynomialElement modulus;
    private PolynomialRing    modulusRing;
}
