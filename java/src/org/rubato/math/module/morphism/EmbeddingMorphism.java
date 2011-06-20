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

import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Rational;
import org.rubato.math.module.*;
import org.rubato.util.Pair;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that embeds one module into another.
 * The following embeddings are covered:<br>
 * - embeddings within the number hierarchy Z -> Q -> R -> C<br> 
 * - embeddings of number rings in polynomial rings<br>
 * - embeddings between polynomial rings<br>
 * - embeddings of number rings in string rings<br>
 * - embeddings between string rings<br>
 * - embeddings of rings in product rings<br>
 * - embeddings between product rings<br>
 * - embeddings between free modules if their rings embed<br>
 * 
 * @author Gérard Milmeister
 */
public abstract class EmbeddingMorphism extends ModuleMorphism {

    /**
     * Creates an embedding from a module <code>domain</code>
     * into a module <code>codomain</code>.
     * 
     * @return null if no embedding of the requested kind can be built
     */
    public static ModuleMorphism make(Module domain, Module codomain) {
        ModuleMorphism m = null;
        
        // check if the requested embedding is in the cache
        Pair<Module,Module> pair = Pair.makePair(domain, codomain);
        if ((m = embeddings.get(pair)) == null) {
            // try to create the embedding
            if (domain instanceof FreeModule && codomain instanceof FreeModule) {
                // case of embeddings between free modules
                m = make((FreeModule)domain, (FreeModule)codomain);
            }
            else {
                // other embeddings
                // TODO: embeddings for modules other than free modules
            }
            if (m != null) {
                // put the morphism into the cache
                embeddings.put(pair, m);
            }
        }
        
        return m;
    }
    
    
    /**
     * Creates an embedding from a free module <code>domain</code>
     * into a free module <code>codomain</code>.
     * 
     * @return null if no embedding of the requested kind can be built
     */
    private static ModuleMorphism make(FreeModule domain, FreeModule codomain) {
        ModuleMorphism m = null;
        
        if (domain.equals(codomain)) {
            // identity for equal domain and codomain
            m = new IdentityMorphism(domain);
        }
        else if (domain instanceof Ring && codomain instanceof Ring) {
            // ring embedding
            m = make((Ring)domain, (Ring)codomain);
        }
        else {
            // free module (non-ring) embedding
            m = makeFreeModuleEmbedding(domain, codomain);                
        }
        return m;
    }
    
    
    /**
     * Creates an embedding from a ring <code>domain</code>
     * into a ring <code>codomain</code>.
     * 
     * @return null if no embedding of the requested kind can be built
     */
    private static ModuleMorphism make(Ring domain, Ring codomain) {
        ModuleMorphism m = null;
        
        if (domain.equals(codomain)) {
            // identity for equal domain and codomain
            m = new IdentityMorphism(domain);
        }
        else if (codomain instanceof StringRing) {
            // embedding of a ring in a string ring
            m = makeStringEmbedding(domain, (StringRing)codomain);
        }
        else if (codomain instanceof ProductRing) {
            // embedding of a ring in a product ring
            m = makeProductRingEmbedding(domain, (ProductRing)codomain);
        }
        else if (codomain instanceof PolynomialRing) {
            // embedding of a ring in a polynomial ring
            m = makePolynomialEmbedding(domain, (PolynomialRing)codomain);
        }
        else {
            // common ring embedding (e.g., Q -> C, etc.)
            m = makeRingEmbedding(domain, codomain);
        }
        
        return m;
    }

        
    public final ModuleElement map(ModuleElement x)
            throws MappingException {
        ModuleElement res = null;
        
        if (getDomain().hasElement(x)) {
            res = mapValue(x);
        }
        
        if (res == null) {
            throw new MappingException("EmbeddingMorphism.map: ", x, this);
        }
        else {
            return res;
        }
    }
        
    /**
     * The low-level map method.
     * This must be implemented by subclasses.
     */
    public abstract ModuleElement mapValue(ModuleElement element);    
    
    /**
     * Embeddings are always module homomorphisms, except for
     * embeddings of a Z_n ring into another ring.
     */
    public boolean isModuleHomomorphism() {
        return true;
    }

    
    public boolean isRingHomomorphism() {
        return isRingMorphism();
    }

    
    public ModuleMorphism getRingMorphism() {
        if (getDomain().isRing() && getCodomain().isRing()) {
            return this;
        }
        else {
            return make(getDomain().getRing(), getCodomain().getRing());
        }
    }
    
    
    public final int compareTo(ModuleMorphism object) {
        if (object instanceof EmbeddingMorphism) {
            EmbeddingMorphism m = (EmbeddingMorphism)object;
            int comp = getDomain().compareTo(m.getDomain());
            if (comp == 0) {
                comp = getCodomain().compareTo(m.getCodomain());
            }
            return comp;
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public final boolean equals(Object object) {
        if (object instanceof EmbeddingMorphism) {
            EmbeddingMorphism m = (EmbeddingMorphism)object;
            return getDomain().equals(m.getDomain()) &&
                   getCodomain().equals(m.getCodomain());
        }
        else {
            return false;
        }
    }
    
    
    public final String toString() {
        return "EmbeddingMorphism["+getDomain()+","+getCodomain()+"]";
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
                if (m1 instanceof FreeModule && m2 instanceof FreeModule) {
                    ModuleMorphism m = make((FreeModule)m1, (FreeModule)m2);
                    if (m == null) {
                        reader.setError("Cannot create an embedding morphism from "+m1+" to "+m2+".");                        
                    }
                    return m;
                }
                else {
                    reader.setError("Modules in %%1 must be free modules.", getElementTypeName());
                    return null;
                }
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
        EmbeddingMorphism.makeRingEmbedding(ZRing.ring, RRing.ring);

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "EmbeddingMorphism";
    }
    
    
    protected EmbeddingMorphism(final FreeModule domain, final FreeModule codomain) {
        super(domain, codomain);
    }
    
    
    // Embeddings are inner classes 
    
    /**
     * Creates an embedding of a ring in another ring.
     */
    private final static EmbeddingMorphism makeRingEmbedding(final Ring domain, final Ring codomain) {
        EmbeddingMorphism m = null;
        // domain is ZnRing
        if (domain instanceof ZnRing) {
            return makeZnRingEmbeeding((ZnRing)domain, codomain);
        }
        // domain is ZRing
        else if (domain == ZRing.ring) {
            // Z -> ?
            if (codomain == QRing.ring) {
                // Z -> Q
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new QElement(((ZElement)element).getValue());
                    }
                };
            }
            else if (codomain == RRing.ring) {
                // Z -> R
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new RElement(((ZElement)element).getValue());
                    }
                };                
            }
            else if (codomain == CRing.ring) {
                // Z -> C
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new CElement(((ZElement)element).getValue());
                    }
                };
            }
        }
        // domain is QRing
        else if (domain == QRing.ring) {
            // Q -> ?
            if (codomain == RRing.ring) {
                // Q -> R
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new RElement(((QElement)element).getValue().doubleValue());
                    }
                };
            }
            else if (codomain == CRing.ring) {
                // Q -> C
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new CElement(((QElement)element).getValue().doubleValue());
                    }
                };
            }
        }
        // domain is RRing
        else if (domain == RRing.ring) {
            // R -> ?
            if (codomain == CRing.ring) {
                // R -> C
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return new CElement(((RElement)element).getValue());
                    }
                };
            }
        }
        
        return m;
    }

    
    /**
     * Creates an embedding of a Z_n ring in another ring.
     * These are neither ring nor module homomorphisms.
     */
    private final static EmbeddingMorphism makeZnRingEmbeeding(final ZnRing domain, final Ring codomain) {
        EmbeddingMorphism m = null;
        if (codomain instanceof ZnRing) {
            // Zn -> Zm
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new ZnElement(((ZnElement)element).getValue(), ((ZnRing)codomain).getModulus());                
                }
                public boolean isRingHomomorphism() { return false; }
                public boolean isModuleHomomorphism() { return false; }
            };
        }
        else if (codomain == ZRing.ring) {
            // Zn -> Z
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new ZElement(((ZnElement)element).getValue());
                }
                public boolean isRingHomomorphism() { return false; }
                public boolean isModuleHomomorphism() { return false; }
            };
        }
        else if (codomain == QRing.ring) {
            // Zn -> Q
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new QElement(((ZnElement)element).getValue());
                }
                public boolean isRingHomomorphism() { return false; }
                public boolean isModuleHomomorphism() { return false; }
            };
        }
        else if (codomain == RRing.ring) {
            // Zn -> R
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new RElement(((ZnElement)element).getValue());
                }
                public boolean isRingHomomorphism() { return false; }
                public boolean isModuleHomomorphism() { return false; }
            };                
        }
        else if (codomain == CRing.ring) {
            // Zn -> C
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new CElement(((ZnElement)element).getValue());
                }
                public boolean isRingHomomorphism() { return false; }
                public boolean isModuleHomomorphism() { return false; }
            };
        }
        return m;
    }

    
    /**
     * Creates an embedding of a free module in another free module.
     */
    private final static EmbeddingMorphism makeFreeModuleEmbedding(final FreeModule domain, final FreeModule codomain) {
        // embeddings of free modules only if the dimension m of the codomain is
        // greater or equal than the dimension n of the domain.
        if (domain.getDimension() > codomain.getDimension()) {            
            return null;
        }

        final int codim = codomain.getDimension();
        EmbeddingMorphism m = null;
        
        // Free modules over number rings        
        if (domain instanceof ZFreeModule) {
            // Z^n -> ?
            if (codomain instanceof ZProperFreeModule) {
                // Z^n -> Z^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return ((ZFreeElement)element).resize(codim);
                    }
                };
            }
            else if (codomain instanceof QProperFreeModule) {
                // Z^n to Q^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        ZProperFreeElement e = (ZProperFreeElement)((ZFreeElement)element).resize(codim);
                        return QProperFreeElement.make(e.getValue());
                    }
                };
            }
            else if (codomain instanceof RProperFreeModule) {
                // Z^n to R^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        ZProperFreeElement e = (ZProperFreeElement)((ZFreeElement)element).resize(codim);
                        int[] v_from = e.getValue();
                        double[] v_to = new double[v_from.length];
                        for (int i = 0; i < v_from.length; i++) {
                            v_to[i] = v_from[i];
                        }
                        return RProperFreeElement.make(v_to);
                    }
                };                
            }
            else if (codomain instanceof CProperFreeModule) {
                // Z^n to C^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        ZProperFreeElement e = (ZProperFreeElement)((ZFreeElement)element).resize(codim);
                        int[] v_from = e.getValue();
                        Complex[] v_to = new Complex[v_from.length];
                        for (int i = 0; i < v_from.length; i++) {
                            v_to[i] = new Complex(v_from[i]);
                        }
                        return CProperFreeElement.make(v_to);
                    }
                };
            }
        }
        else if (domain instanceof QFreeModule) {
            // Q^n -> ?
            if (codomain instanceof QProperFreeModule) {
                // Q^n -> Q^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return ((QFreeElement)element).resize(codim);
                    }
                };
            }
            else if (codomain instanceof RProperFreeModule) {
                // Q^n -> R^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        QProperFreeElement e = (QProperFreeElement)((QFreeElement)element).resize(codim);
                        Rational[] v_from = e.getValue();
                        double[] v_to = new double[v_from.length];
                        for (int i = 0; i < v_from.length; i++) {
                            v_to[i] = v_from[i].doubleValue();
                        }
                        return RProperFreeElement.make(v_to);
                    }
                };
            }
            else if (codomain instanceof CProperFreeModule) {
                // Q^n -> C^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        QProperFreeElement e = (QProperFreeElement)((QFreeElement)element).resize(codim);
                        Rational[] v_from = e.getValue();
                        Complex[] v_to = new Complex[v_from.length];
                        for (int i = 0; i < v_from.length; i++) {
                            v_to[i] = new Complex(v_from[i].doubleValue());
                        }
                        return CProperFreeElement.make(v_to);
                    }
                };
            }
        }
        else if (domain instanceof RFreeModule) {
            // R^n -> ?
            if (codomain instanceof RProperFreeModule) {
                // R^n to R^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return ((RFreeElement)element).resize(codim);
                    }
                };
            }
            else if (codomain instanceof CProperFreeModule) {
                // R^n to C^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        RProperFreeElement e = (RProperFreeElement)((RFreeElement)element).resize(codim);
                        double[] v_from = e.getValue();
                        Complex[] v_to = new Complex[v_from.length];
                        for (int i = 0; i < v_from.length; i++) {
                            v_to[i] = new Complex(v_from[i]);
                        }
                        return CProperFreeElement.make(v_to);
                    }
                };
            }
        }
        else if (domain instanceof CFreeModule) {
            // C^n -> ?
            if (codomain instanceof CProperFreeModule) {
                // C^n to C^m
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        return ((CFreeElement)element).resize(codim);
                    }
                };
            }
        }
        
        // Other free modules
        if (m == null && domain.getDimension() < codomain.getDimension()) {
            Ring domainRing = domain.getRing();
            Ring codomainRing = codomain.getRing();
            final ModuleMorphism ringMorphism = make(domainRing, codomainRing);
            if (ringMorphism != null) {
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        try {
                            FreeElement fe = ((FreeElement)element).resize(codim);
                            LinkedList<ModuleElement> elements = new LinkedList<ModuleElement>();
                            for (RingElement e : fe) {
                                elements.add(ringMorphism.map(e));
                            }
                            return codomain.createElement(elements);
                        }
                        catch (MappingException e) {
                            throw new AssertionError("This should never happen!");
                        }
                    }
                    public boolean isModuleHomomorphism() {
                        return ringMorphism.isRingHomomorphism();
                    }
                };
            }
        }
        return m;
    }

    
    /**
     * Creates an embedding of a ring in a product ring.
     * 
     * @param domain a ring
     * @param codomain a product ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static EmbeddingMorphism makeProductRingEmbedding(final Ring domain, final ProductRing codomain) {
        if (domain instanceof ProductRing) {
            return makeProductRingEmbedding((ProductRing)domain, codomain);
        }
        else {
            Ring[] factors = codomain.getFactors();
            final int len = factors.length;
            final ModuleMorphism[] morphisms = new ModuleMorphism[len];
            for (int i = 0; i < len; i++) {
                morphisms[i] = make(domain, factors[i]);
                if (morphisms[i] == null) {
                    return null;
                }
            }
            return new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    RingElement r = (RingElement)element;
                    RingElement[] factors0 = new RingElement[len];
                    try {
                        for (int i = 0; i < len; i++) {
                            factors0[i] = (RingElement)morphisms[i].map(r);
                        }
                        return ProductElement.make(factors0);
                    }
                    catch (MappingException e) {
                        throw new AssertionError("This should never happen!");
                    }
                }
            };
        }
    }
    
    
    /**
     * Creates an embedding of a product ring in a product ring.
     * 
     * @param domain a product ring
     * @param codomain a product ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static EmbeddingMorphism makeProductRingEmbedding(ProductRing domain, ProductRing codomain) {
        EmbeddingMorphism m = null;
        if (domain.getFactorCount() == codomain.getFactorCount()) {
            final ModuleMorphism[] ems = new ModuleMorphism[domain.getFactorCount()];
            for (int i = 0; i < domain.getFactorCount(); i++) {
                Ring d = domain.getFactor(i);
                Ring c = codomain.getFactor(i);
                ModuleMorphism em = make(d, c);
                if (em == null) {
                    return null;
                }
                ems[i] = em;
            }
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement x) {
                    ProductElement p = (ProductElement)x;
                    RingElement[] factors = new RingElement[p.getFactorCount()];
                    try {
                        for (int i = 0; i < p.getFactorCount(); i++) {
                            factors[i] = (RingElement) embeds[i].map(p.getFactor(i));
                        }
                        return ProductElement.make(factors);
                    }
                    catch (MappingException e) {
                        throw new AssertionError("This should never happen!");
                    }
                }
                private ModuleMorphism[] embeds = ems;
            };
        }
        return m;
    }

    
    /**
     * Creates an embedding of a ring in a polynomial ring.
     * 
     * @param domain a ring
     * @param codomain a polynomial ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static EmbeddingMorphism makePolynomialEmbedding(final Ring domain, final PolynomialRing codomain) {
        EmbeddingMorphism m = null;
        Ring coeffRing = codomain.getCoefficientRing();
        if (domain instanceof PolynomialRing) {
            m = makePolynomialEmbedding((PolynomialRing)domain, codomain); 
        }
        else if (coeffRing.equals(domain)) {
            m = new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return new PolynomialElement(codomain, new RingElement[] {(RingElement)element});
                }
            };
        }
        else {
            final ModuleMorphism ringMorphism = make(domain, coeffRing);
            if (ringMorphism != null) {
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        try {
                            RingElement coeff = (RingElement)ringMorphism.map(element);
                            return new PolynomialElement(codomain, new RingElement[] {coeff});
                        }
                        catch (MappingException e) {
                            throw new AssertionError("This should never happen!");
                        }
                    }
                };
            }
        }
        return m;
    }

    
    /**
     * Creates an embedding of polynomial ring in a polynomial ring.
     * 
     * @param domain a polynomial ring
     * @param codomain a polynomial ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static EmbeddingMorphism makePolynomialEmbedding(final PolynomialRing ring, final PolynomialRing polyRing) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not implemented");
    }

    
    /**
     * Creates an embedding of ring in a string ring.
     * 
     * @param domain a ring
     * @param codomain a string ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static ModuleMorphism makeStringEmbedding(final Ring domain, final StringRing codomain) {
        ModuleMorphism m = null;        
        if (domain instanceof StringRing) {
            m = makeStringEmbedding((StringRing)domain, codomain);
        }
        else {
            final Ring coeffRing = codomain.getFactorRing();
            final ModuleMorphism ringMorphism = make(domain, coeffRing);
            if (ringMorphism != null) {
                m = new EmbeddingMorphism(domain, codomain) {
                    public ModuleElement mapValue(ModuleElement element) {
                        try {
                            RingElement coeff = (RingElement)ringMorphism.map(element);
                            return codomain.cast(coeff);
                        }
                        catch (MappingException e) {
                            throw new AssertionError("This should never happen!");
                        }
                    }
                };
            }
        }
        return m;
    }

    
    /**
     * Creates an embedding of string ring in a string ring.
     * 
     * @param domain a string ring
     * @param codomain a string ring
     * @return an embedding or null if such an embedding cannot be constructed
     */
    private final static EmbeddingMorphism makeStringEmbedding(final StringRing domain, final StringRing codomain) {
        if (make(domain.getFactorRing(), codomain.getFactorRing()) != null) {
            return new EmbeddingMorphism(domain, codomain) {
                public ModuleElement mapValue(ModuleElement element) {
                    return codomain.cast(element);
                }
            };
        }
        else {
            return null;
        }
    }

    
    private static HashMap<Pair<Module,Module>,ModuleMorphism> embeddings = new HashMap<Pair<Module,Module>,ModuleMorphism>();
}
