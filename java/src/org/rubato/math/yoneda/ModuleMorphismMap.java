/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
 * Copyright (C) 2002 Stefan Müller
 * Copyright (C) 2002 Stefan Göller
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

package org.rubato.math.yoneda;

import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.MORPHISMMAP;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoDictionary;
import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZRing;
import org.rubato.math.module.morphism.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism map containing a module morphism (for type simple).
 * Domain and codomain are modules.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public class ModuleMorphismMap implements MorphismMap {
    
    public static ModuleMorphismMap make(ModuleMorphism morphism) {
        Module domain = morphism.getDomain();
        if (domain.isNullModule() || morphism.isConstant()) {
            try {
                return new ConstantModuleMorphismMap(domain, morphism.map(domain.getZero()));
            }
            catch (MappingException e) {
                // should never reach this point
                throw new AssertionError(e);
            }
        }
        else {
            return new ModuleMorphismMap(morphism);
        }
    }
    
    
    /**
     * Returns the element resulting from applying the morphism to zero.
     */
    public ModuleElement getElement() {
        try {
            return moduleMorphism.map(getDomain().getZero());
        }
        catch (MappingException e) {
            throw new AssertionError("Should never reach this point!");
        }
    }
    

    /**
     * Returns the module morphism.
     */
    public ModuleMorphism getMorphism() {
        return moduleMorphism;
    }
    

    /**
     * Returns the domain of the map.
     */
    public Module getDomain() {
        return getMorphism().getDomain();
    }
    

    /**
     * Returns the codomain of the map.
     */
    public Module getCodomain() {
        return getMorphism().getCodomain();
    }
    
    
    public int compareTo(MorphismMap object) {
        if (object instanceof ConstantModuleMorphismMap) {
            return 1;
        }
        else if (object instanceof ModuleMorphismMap){
            return moduleMorphism.compareTo(((ModuleMorphismMap)object).getMorphism());
        }
        else {
            return -1;
        }
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ConstantModuleMorphismMap) {
            return false;
        }
        else if (object instanceof ModuleMorphismMap) {
            return moduleMorphism.equals(((ModuleMorphismMap)object).moduleMorphism);
        }
        else {
            return false;
        }
    }
   
   
    public boolean fullEquals(MorphismMap map, IdentityHashMap<Object,Object> s) {
        return equals(map);
    }


    /**
     * Maps the specified module element using the module morphism.
     */
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        return moduleMorphism.map(x);
    }

    
    public ModuleElement getElement(int[] path, int curpos) {
        ModuleElement x = getElement();
        for (int i = curpos; i < path.length; i++) {
            if (x != null) {
                x = x.getComponent(path[i]);
            }
        }
        return x;
    }

    
    /**
     * Creates a constant morphism from this morphism by
     * evaluating it at the specified element.
     * 
     * @return a ConstantModuleMorphismMap
     * @throws MappingException if evaluation fails
     */
    public MorphismMap at(ModuleElement element)
            throws MappingException {
        return new ConstantModuleMorphismMap(getDomain().getNullModule(), map(element));
    }


    /**
     * Returns a module morphism map with the given new domain.
     * 
     * @return null if a new module morphism map could not be created
     */
    public ModuleMorphismMap changeDomain(Module newDomain) {
        ModuleMorphism m = CanonicalMorphism.make(newDomain, getDomain());
        if (m != null) {
            return changeDomain(m);
        }
        else {
            return null;
        }
    }

    
    /**
     * Compose <code>morphism</code> with the morphism contained
     * in this ModuleMorphismMap.
     * 
     * @return a new ModuleMorphismMap or null if composition failed
     */    
    public ModuleMorphismMap changeDomain(ModuleMorphism morphism) {
        try {
            ModuleMorphism newMorphism = getMorphism().compose(morphism);
            return new ModuleMorphismMap(newMorphism);
        }
        catch (CompositionException e) {
            return null;
        }
    }

    
    public MorphismMap changeAddress(Module address) {
        return changeDomain(address);
    }
    
    
    public MorphismMap changeAddress(ModuleMorphism morphism) {
        return changeDomain(morphism);
    }

    
    /**
     * Composes the morphism contained in this map with the
     * given <code>morphism</code>. The resulting morphism
     * is f(x) = this.morphism(morphism(x)).
     */
    public ModuleMorphismMap map(ModuleMorphism morphism)
            throws RubatoException {
        if (morphism.getDomain().equals(morphism.getCodomain())) {
            ModuleMorphism res = morphism.compose(this.moduleMorphism);
            return new ModuleMorphismMap(res);
        }
        throw new RubatoException("ModuleMorphismMap.map: Domain and codomain of morphism must be equal");
    }


    /**
     * Returns the sum of this map with the given map.
     * 
     * @return null if the sum fails
     */
    public ModuleMorphismMap sum(ModuleMorphismMap map) {
        try {
            return new ModuleMorphismMap(moduleMorphism.sum(map.getMorphism()));
        }
        catch (CompositionException e) {
            return null;
        }            
    }
    
    
    /**
     * Returns the difference of this map with the given map.
     * 
     * @return null if the difference fails
     */
    public ModuleMorphismMap difference(ModuleMorphismMap map) {
        try {
            return new ModuleMorphismMap(moduleMorphism.difference(map.getMorphism()));
        }
        catch (CompositionException e) {
            return null;
        }                        
    }

    
    public void toXML(XMLWriter writer) {        
        writer.openBlockWithType(MORPHISMMAP, getElementTypeName());
        moduleMorphism.toXML(writer);
        writer.closeBlock();
    }
    
    
    public MorphismMap fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        
        Element childElement = XMLReader.getChild(element, MODULEMORPHISM);
        if (childElement == null) {
            reader.setError("<%1> of type %%2 is missing child element <%3>.", MORPHISMMAP, getElementTypeName(), MODULEMORPHISM);
            return null;
        }
        ModuleMorphism morphism = reader.parseModuleMorphism(childElement);
        if (morphism == null) {
            return null;
        }
        return new ModuleMorphismMap(morphism);
    }
    
    
    private static final XMLInputOutput<MorphismMap> xmlIO =
        new ModuleMorphismMap(ModuleMorphism.getIdentityMorphism(ZRing.ring));

    public static XMLInputOutput<MorphismMap> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ModuleMorphismMap";
    }

    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list) {
        return list;
    }
    
    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list) {
        return list;
    }
    
    
    /**
     * Resolves the references resulting from parsing.
     * @return true iff all references have been resolved.
     */
    public boolean resolveReferences(RubatoDictionary reader, IdentityHashMap<?,?> history) {
        return true;
    }
    
    
    public boolean isConstant() {
        return moduleMorphism.isConstant();
    }
    
    
    /**
     * Returns a copy of this module morphism map.
     */
    public Object clone() {
        return copy();
    }

    
    /**
     * Returns a copy of this module morphism map.
     */
    public ModuleMorphismMap copy() {
        return new ModuleMorphismMap(moduleMorphism);
    }

    
    public String toString() {
        return "ModuleMorphismMap["+moduleMorphism+"]";
    }

    
    /**
     * Returns a hash code of this module morphism map.
     */
    public int hashCode() {
        return moduleMorphism.hashCode();
    }

  
    /**
     * Creates a module morphism map with the specified module morphism.
     */
    protected ModuleMorphismMap(ModuleMorphism morphism) {
        this.moduleMorphism = morphism;
    }
    

    protected ModuleMorphismMap() {
        moduleMorphism = null;
    }
    
    
    protected ModuleMorphism moduleMorphism;
}
