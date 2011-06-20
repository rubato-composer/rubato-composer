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

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.HashMap;
import java.util.LinkedList;

import org.rubato.math.module.*;
import org.rubato.util.Pair;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * Canonical morphisms are the "simplest" morphisms that map
 * an element from the domain to the codomain, e.g, identities,
 * embeddings or casts.
 * The {@link #make} method is used to create a canonical morphism.
 * 
 * @author Gérard Milmeister
 */
public abstract class CanonicalMorphism extends ModuleMorphism {

    /**
     * Creates a canonical morphism from <code>domain</code>
     * to </code>codomain</code>.
     * 
     * @return null iff no such morphism could be created
     */
    public static ModuleMorphism make(Module domain, Module codomain) {
        // check if the required morphism is in the cache
        Pair<Module,Module> pair = Pair.makePair(domain, codomain);
        ModuleMorphism morphism = canonicalMorphisms.get(pair);
        if (morphism == null) {
            // if not, try to create it
            morphism = makeCanonicalMorphism(domain, codomain);
            if (morphism != null) {
                // put it into the cache
                canonicalMorphisms.put(pair, morphism);
            }
        }
        return morphism;
    }
    
    
    /**
     * Creates a canonical morphism from <code>domain</code>
     * to <code>codomain</code>.
     * 
     * @return null iff noch such morphism could be created
     */
    private static ModuleMorphism makeCanonicalMorphism(final Module domain, final Module codomain) {
        ModuleMorphism morphism = null;
        if (domain.isNullModule()) {
            // if the domain is a null module, a canonical morphism maps
            // the unique null element to the zero in the codomain
            morphism = new CanonicalMorphism(domain, codomain) {
                public ModuleElement map(ModuleElement x) throws MappingException {
                    if (!domain.hasElement(x)) {
                        throw new MappingException("CanonicalMorphism.map: ", x, this);
                    }
                    return zero;
                }
                public ModuleMorphism getRingMorphism() {
                    return makeRingMorphism(domain.getRing(), codomain.getRing());
                }
                private final ModuleElement zero = codomain.getZero();
            };
        }
        else if (codomain.isNullModule()) {
            // if the codomain is a null module, a canonical morphism maps
            // every element from the domain to the unique null element
            // in the codomain
            morphism = new CanonicalMorphism(domain, codomain) {
                public ModuleElement map(ModuleElement x) throws MappingException {
                    if (!domain.hasElement(x)) {
                        throw new MappingException("CanonicalMorphism.map: ", x, this);
                    }
                    return zero;
                }
                public ModuleMorphism getRingMorphism() {
                    return makeRingMorphism(domain.getRing(), codomain.getRing());
                }
                private final ModuleElement zero = codomain.getZero();
            };
        }
        else if (domain instanceof Ring && codomain instanceof Ring) {
            // handle canonical mappings from rings to rings separately
            // including embeddings
            morphism = makeRingMorphism((Ring)domain, (Ring)codomain);
        }
        else {
            // try to create a non-ring, e.g., free module, embedding
            morphism = EmbeddingMorphism.make(domain, codomain);
        }
        
        if (morphism == null && domain instanceof FreeModule && codomain instanceof FreeModule) {
            // nothing from the above has succeeded
            // now try to create a canonical morphism on
            // the underlying rings of the domain and codomain modules
            // provided that the domain and codomain are free modules
            Ring domainRing = domain.getRing();
            Ring codomainRing = codomain.getRing();
            ModuleMorphism ringMorphism = makeRingMorphism(domainRing, codomainRing);
            if (ringMorphism != null) {
                // now extend this mapping to the free modules
                morphism = makeFreeModuleMorphism(domainRing, codomainRing, ringMorphism,
                                                  domain.getDimension(), codomain.getDimension());
            }
        }

        // other types of morphisms, for example non-free modules, are
        // not yet supported
        
        return morphism;
    }
    
    
    /**
     * Creates a canonical morphism from the domain ring to the
     * codomain ring.
     * 
     * @return null iff no such morphism could be created
     */
    protected static ModuleMorphism makeRingMorphism(Ring domainRing, Ring codomainRing) {
        ModuleMorphism morphism = null;
        
        if (domainRing instanceof ZRing && codomainRing instanceof ZnRing) {
            // the special case of the mapping of integers to modular integers
            // this must be handled seperately, since it is not an embedding
            return ModuloMorphism.make(((ZnRing)codomainRing).getModulus());
        }
        else {
            // first try to create an embedding between the rings
            morphism = EmbeddingMorphism.make(domainRing, codomainRing);
            if (morphism == null) {
                // if this doesn't work create a casting morphism as a last resort
                morphism = CastMorphism.make(domainRing, codomainRing); 
            }
        }
        
        return morphism;
    }
    
    
    /**
     * Creates a canonical morphism between the free module of dimension <code>dim</code>
     * over the ring <code>domainRing</code> to the free module of dimension <code>codim</code>
     * over the ring <code>codomainRing</code> using a provided canonical morphism
     * <code>ringMorphism</code> between the rings.
     * If the domain dimension is less than the codomain dimension, the mapped value
     * is filled up with zeros.
     * If the domain dimension is greater then the codomain dimension, the
     * excess components are omitted.  
     */
    private static ModuleMorphism makeFreeModuleMorphism(final Ring domainRing,
                                                         final Ring codomainRing,
                                                         final ModuleMorphism ringMorphism,
                                                         final int dim,
                                                         final int codim) {
        final FreeModule domain = domainRing.getFreeModule(dim);
        final FreeModule codomain = codomainRing.getFreeModule(codim);
        return new CanonicalMorphism(domain, codomain) {
            public ModuleElement map(ModuleElement x) throws MappingException {
                if (!domain.hasElement(x)) {
                    throw new MappingException("CanonicalMorphism.map: ", x, this);
                }
                FreeElement e = ((FreeElement)x).resize(codim);
                LinkedList<ModuleElement> elements = new LinkedList<ModuleElement>();
                for (RingElement element : e) {
                    elements.add(ringMorphism.map(element));
                }
                return codomain.createElement(elements);
            }
            public ModuleMorphism getRingMorphism() {
                return ringMorphism;
            }
        };
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }
    
    
    public boolean isRingHomomorphism() {
        return isRingMorphism();
    }

    
    public boolean equals(Object object) {
        if (object instanceof CanonicalMorphism) {
            CanonicalMorphism morphism = (CanonicalMorphism)object;
            return getDomain().equals(morphism.getDomain()) &&
                   getCodomain().equals(morphism.getCodomain());
        }
        else {
            return false;
        }
    }

    
    public String toString() {
        return "CanonicalMorphism["+getDomain()+","+getCodomain()+"]";
    }


    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        getDomain().toXML(writer);
        getCodomain().toXML(writer);
        writer.closeBlock();
    }

    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Module m1;
        Module m2;
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            m1 = reader.parseModule(childElement);
            if (m1 == null) {
                return null;
            }
            childElement = XMLReader.getNextSibling(childElement, MODULE);
            if (childElement != null) {
                m2 = reader.parseModule(childElement);
                if (m2 == null) {
                    return null;
                }
                ModuleMorphism m = make(m1, m2);
                if (m == null) {
                    reader.setError("Cannot create an canonical morphism from %1 to %2.", m1.toString(), m2.toString());                        
                }
                return m;
            }
            else {
                reader.setError("Type %%1 is missing second child <%2>.", getElementTypeName(), MODULE);
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing element <%2>.", getElementTypeName(), MODULE);
            return null;            
        }
    }

    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        CanonicalMorphism.makeCanonicalMorphism(ZProperFreeModule.nullModule, ZProperFreeModule.nullModule);

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "CanonicalMorphism";
    }

    
    protected CanonicalMorphism(Module domain, Module codomain) {
        super(domain, codomain);
    }
    

    private static HashMap<Pair<Module,Module>,ModuleMorphism> canonicalMorphisms = new HashMap<Pair<Module,Module>,ModuleMorphism>();
}
