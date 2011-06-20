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

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.module.morphism.MappingException;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a restricted module. Instances are created using
 * the static {@link #make(RestrictedModule,ModuleElement)} method.
 * 
 * @author Gérard Milmeister
 */
public class RestrictedElement implements ModuleElement {

    public static RestrictedElement make(RestrictedModule module, ModuleElement element)
            throws DomainException {
        if (module.getUnrestrictedModule().hasElement(element)) {
            return new RestrictedElement(module, element);
        }
        else {
            throw new DomainException(module.getUnrestrictedModule(), element.getModule());
        }
    }

    
    public static RestrictedElement getZero(RestrictedModule module) {
        return new RestrictedElement(module, module.getUnrestrictedModule().getZero());
    }
    
    
    public boolean isZero() {
        return moduleElement.isZero();
    }

    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        if (module.getRestrictingMorphism().getDomain().hasElement(element)) {            
            ModuleElement res = null;
            try {
                res = module.getRestrictingMorphism().map(element);
            }
            catch (MappingException e) {}
            return new RestrictedElement(module, this.moduleElement.scaled((RingElement)res));
        }
        else {
            throw new DomainException(module.getRestrictingMorphism().getDomain().getRing(), element.getRing());
        }
    }

    
    public void scale(RingElement element)
            throws DomainException {
        if (module.getRestrictingMorphism().getDomain().hasElement(element)) {            
            ModuleElement res = null;
            try {
                res = module.getRestrictingMorphism().map(element);
            }
            catch (MappingException e) {}
            this.moduleElement.scale((RingElement)res);
        }
        else {
            throw new DomainException(module.getRestrictingMorphism().getDomain().getRing(), element.getRing());
        }
    }

    
    public int getLength() {
        return moduleElement.getLength();
    }

    
    public ModuleElement getComponent(int i) {
        return new RestrictedElement((RestrictedModule)module.getComponentModule(i), moduleElement.getComponent(i));
    }

    
    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (module.equals(element.getModule())) {
            RestrictedElement relement = (RestrictedElement)element;
            return new RestrictedElement(module, this.moduleElement.sum(relement.getUnrestrictedElement()));
        }
        else {
            throw new DomainException(module, element.getModule());
        }
    }

    
    public void add(ModuleElement element)
            throws DomainException {
        if (module.equals(element.getModule())) {
            this.moduleElement.add(((RestrictedElement)element).getUnrestrictedElement());
        }
        else {
            throw new DomainException(module, element.getModule());
        }
    }

    
    public ModuleElement difference(ModuleElement element)
            throws DomainException {
        if (module.equals(element.getModule())) {            
            return new RestrictedElement(module, this.moduleElement.difference(((RestrictedElement)element).getUnrestrictedElement()));
        }
        else {
            throw new DomainException(module, element.getModule());
        }
    }

    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (module.equals(element.getModule())) {
            this.moduleElement.subtract(((RestrictedElement)element).getUnrestrictedElement());
        }
        else {
            throw new DomainException(module, element.getModule());
        }
    }

    
    public ModuleElement negated() {
        return new RestrictedElement(module, moduleElement.negated());
    }

    
    public void negate() {
        moduleElement.negate();
    }

    
    public double[] fold(ModuleElement[] elements) {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    public Module getModule() {
        return module;
    }

    
    public ModuleElement getUnrestrictedElement() {
        return moduleElement;
    }
    
    
    public ModuleElement cast(Module module) {
        ModuleElement res = moduleElement.cast(module);
        if (res == null) {
            return res;
        }
        else {
            return new RestrictedElement(this.module, res);
        }
    }

    
    public String stringRep(boolean ... parens) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not implemented");
    }


    public boolean equals(Object object) {
        if (object instanceof RestrictedElement) {
            RestrictedElement relement = (RestrictedElement)object;
            return module.equals(relement.module)
                   && moduleElement.equals(relement.moduleElement);
        }
        else {
            return false;
        }
    }
    
    
    public int compareTo(ModuleElement object) {
        if (object instanceof RestrictedElement) {
            RestrictedElement relement = (RestrictedElement)object;
            int comp = module.compareTo(relement.module);
            if (comp == 0) {
                return moduleElement.compareTo(relement.moduleElement);
            }
            else {
                return comp;
            }
        }
        else {
            return getModule().compareTo(object.getModule());
        }
    }

    
    public RestrictedElement clone() {
        return new RestrictedElement(module, moduleElement.clone());
    }
    
    
    public String toString() {
        return "RestrictedElement["+getModule()+","+moduleElement+"]";
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        module.toXML(writer);
        moduleElement.toXML(writer);
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            Module module0 = reader.parseModule(childElement);
            if (module0 == null) {
                return null;
            }
            if (!(module0 instanceof RestrictedModule)) {
                reader.setError("Module in type %%1 must be of type %%2.", getElementTypeName(), "RestrictedModule");
                return null;
            }
            RestrictedModule rmodule = (RestrictedModule)module0;
            childElement = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            if (childElement != null) {
                ModuleElement el = reader.parseModuleElement(childElement);
                if (el == null) {
                    return null;
                }
                RestrictedElement relement = null;
                try {
                    relement = make(rmodule, el);
                }
                catch (DomainException e) {
                    reader.setError(e);
                }
                return relement;
            }
            else {
                reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULEELEMENT);
                return null;                
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULE);
            return null;
        }
    }

    
    private final static XMLInputOutput<ModuleElement> xmlIO = new RestrictedElement(null, null);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "RestrictedElement";
    }
    

    private RestrictedElement(RestrictedModule module, ModuleElement element) {
        this.module = module;
        this.moduleElement = element;
    }
    
    
    private RestrictedModule module;
    private ModuleElement    moduleElement;
}
