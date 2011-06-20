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

import static org.rubato.xml.XMLConstants.*;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents a translation in an arbitrary module.
 * 
 * @author Gérard Milmeister
 */
public final class TranslationMorphism extends ModuleMorphism {

    /**
     * Create a morphism in module <code>module</code> translated by <code>element</code>.
     * The resulting morphism <i>h</i> is such that <i>h(x) = x+element</i>.
     * 
     * @return null if translation is not valid
     */
    static public ModuleMorphism make(Module module, ModuleElement element) {
        if (!module.hasElement(element)) {
            return null;
        }
        else {
            return new TranslationMorphism(module, element);
        }
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            try {
                return x.sum(translate);
            }
            catch (DomainException e) {
                throw new MappingException("TranslationMorphism.map: ", x, this);
            }
        }
        else {
            throw new MappingException("TranslationMorphism.map: ", x, this);
        }
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }

    
    public boolean isRingHomomorphism() {
        return getDomain().isRing() && translate.isZero(); 
    }
    
    
    public boolean isLinear() {
        return translate.isZero();
    }
    
    
    public boolean isIdentity() {
        return translate.isZero();
    }
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    /**
     * Returns the translate <i>t</i> of <i>h(x) = x+t</i>.
     */
    public ModuleElement getTranslate() {
        return translate;
    }
    

    public int compareTo(ModuleMorphism object) {
        if (object instanceof TranslationMorphism) {
            TranslationMorphism morphism = (TranslationMorphism)object;
            return translate.compareTo(morphism.translate);
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof TranslationMorphism) {
            TranslationMorphism morphism = (TranslationMorphism)object;
            return translate.equals(morphism.translate);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        return "TranslationMorphism["+translate+"]";
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        getDomain().toXML(writer);
        translate.toXML(writer);
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            Module f = reader.parseModule(childElement);
            Element el = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            if (el == null) {
                reader.setError("Type %%1 is missing second child of type <%2>.", getElementTypeName(), MODULEELEMENT);
                return null;                
            }
            ModuleElement trslte = reader.parseModuleElement(el);
            if (f == null || trslte == null) {
                return null;
            }
            try {
                ModuleMorphism morphism = make(f, trslte);
                return morphism;
            }
            catch (IllegalArgumentException e) {
                reader.setError(e.getMessage());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULE);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new TranslationMorphism(ZRing.ring, new ZElement(0));

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "TranslationMorphism"; 
    }
    
    
    private TranslationMorphism(Module domain, ModuleElement translate) {
        super(domain, domain);
        this.translate = translate;
    }

    
    private ModuleElement translate;
}
