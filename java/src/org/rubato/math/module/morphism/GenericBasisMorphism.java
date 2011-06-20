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

import static org.rubato.xml.XMLConstants.*;

import java.util.ArrayList;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * A generic basis morphism is defined by the values at the basis
 * elements of the domain module.
 * 
 * @author Gérard Milmeister
 */
public class GenericBasisMorphism extends ModuleMorphism {

    /**
     * Creates generic basis morphism with the free module <code>domain</code>
     * and any <code>codomain</code>. Both modules must be over the same ring.
     * The array <code>fi</code> of module elements contains the element in
     * the codomain that the vectors
     * (0,0,0,0...), (1,0,0,0,...), (0,1,0,0,...), ...
     * in this order are mapped to. 
     * 
     * @throws DomainException if rings don't match
     */
    public static GenericBasisMorphism make(FreeModule domain, Module codomain, ModuleElement[] fi)
            throws DomainException {
        if (domain.getRing().equals(codomain.getRing())) {
            ModuleElement[] fis = new ModuleElement[domain.getDimension()+1];
            int i;
            for (i = 0; i < fis.length; i++) {
                fis[i] = codomain.getZero();
            }
            for (i = 0; i < Math.min(fis.length, fi.length); i++) {
                if (codomain.hasElement(fi[i])) {
                    fis[i] = fi[i];
                }
                else {
                    throw new DomainException(codomain, fi[i].getModule());
                }
            }
            return new GenericBasisMorphism(domain, codomain, fis);
        }
        else {
            throw new DomainException(domain.getRing(), codomain.getRing());
        }
    }
    
    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (inDomain(x)) {
            FreeElement v = (FreeElement)x;
            ModuleElement res = fit[0].clone();
            try {
                for (int i = 0; i < v.getLength(); i++) {
                    ModuleElement tmp = fit[i+1].scaled(v.getRingElement(i));
                    res.add(tmp);
                }
            }
            catch (DomainException e) {
                throw new AssertionError("Should never happen");
            }
            return res;
        }
        else {
            throw new MappingException("GenericBasisMorphism.map: ", x, this);
        }
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }
    
    
    public boolean isRingHomomorphism() {
        return false;
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }
    
    
    public boolean isLinear() {
        return fi[0].isZero();
    }

    
    public boolean equals(Object object) {
        if (object instanceof GenericBasisMorphism) {
            GenericBasisMorphism morphism = (GenericBasisMorphism)object;
            if (getDomain().equals(morphism.getDomain()) &&
                getCodomain().equals(morphism.getCodomain())) {
                for (int i = 0; i < fit.length; i++) {
                    if (!fit[i].equals(morphism.fit[i])) {
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

    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("GenericBasisMorphism[");
        buf.append(getDomain());
        buf.append(",");
        buf.append(getCodomain());
        buf.append(",");
        buf.append("(");
        buf.append(fi[0]);
        for (int i = 1; i < fi.length; i++) {
            buf.append(",");
            buf.append(fi[i]);
        }
        buf.append(")]");
        return buf.toString();
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        getDomain().toXML(writer);
        getCodomain().toXML(writer);
        for (int i = 0; i < fi.length; i++) {
            fi[i].toXML(writer);
        }
        writer.closeBlock();
    }

    
    public GenericBasisMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Module domain = null;
        Module codomain = null;
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            domain = reader.parseModule(childElement);
            if (domain == null) {
                return null;
            }
            if (!(domain instanceof FreeModule)) {
                reader.setError("Domain of type %%1 must be a free module.", getElementTypeName());
                return null;
            }
            childElement = XMLReader.getNextSibling(childElement, MODULE);
            if (childElement != null) {
                codomain = reader.parseModule(childElement);
                if (codomain == null) {
                    return null;
                }
                ArrayList<ModuleElement> elementList = new ArrayList<ModuleElement>();
                childElement = XMLReader.getNextSibling(childElement, MODULEELEMENT);
                while (childElement != null) {
                    ModuleElement melement = reader.parseModuleElement(childElement);
                    if (melement == null) {
                        return null;
                    }
                    elementList.add(melement);
                }
                ModuleElement[] elements = new ModuleElement[elementList.size()];
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = elementList.get(i);
                }
                try {
                    GenericBasisMorphism morphism = make((FreeModule)domain, codomain, elements);
                    return morphism;
                }
                catch (DomainException e) {
                    reader.setError(e);
                    return null;
                }
            }
            else {
                reader.setError("Type %%1 is missing second element <%2>.", getElementTypeName(), MODULE);
                return null;                            
            }
        }
        else {
            reader.setError("Type %%1 is missing element <%2>.", getElementTypeName(), MODULE);
            return null;            
        }
    }
    

    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new GenericBasisMorphism(ZRing.ring, ZRing.ring, new ZElement[] { new ZElement(0) });

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "GenericBasisMorphism";
    }
    
    
    private GenericBasisMorphism(FreeModule domain, Module codomain, ModuleElement[] fi) {
        super(domain, codomain);
        this.fi = fi;
        fit = new ModuleElement[fi.length];
        fit[0] = fi[0];
        try {
            for (int i = 1; i < fi.length; i++) {
                fit[i] = fi[i].difference(fi[0]);
            }
        }
        catch (DomainException e) {
            throw new AssertionError("Should never happen");            
        }
    }
    
    
    private ModuleElement[] fi;
    private ModuleElement[] fit;
}
