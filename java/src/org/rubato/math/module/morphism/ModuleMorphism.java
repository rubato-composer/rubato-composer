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

import java.io.Serializable;

import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RingElement;
import org.rubato.xml.XMLInputOutput;

/**
 * The abstract base class for morphisms in modules.
 * Any new type (subclass) of ModuleMorphism must be registered
 * with a dispatcher for reading from XML
 * (in the init method of {@link org.rubato.xml.Dispatcher}). 
 * 
 * @author Gérard Milmeister
 */
public abstract class ModuleMorphism
    implements Comparable<ModuleMorphism>, Cloneable, Serializable, XMLInputOutput<ModuleMorphism> {

    /**
     * Creates a new morphism with <code>domain</code>
     * and <code>codomain<code> as indicated.
     */
    public ModuleMorphism(Module domain, Module codomain) {
        this.domain = domain;
        this.codomain = codomain;
    }

    
    /**
     * Maps the element <code>x</code>.
     * This must be implemented for each specific morphism type.
     * 
     * @return the result of mapping element <code>x</code>
     * @throws MappingException if mapping of <code>element<code> fails
     */
    public abstract ModuleElement map(ModuleElement x)
        throws MappingException;

    
    /**
     * Returns the composition this*<code>morphism</code>.
     * 
     * @throws CompositionException if composition could not be performed
     */
    public ModuleMorphism compose(ModuleMorphism morphism)
        	throws CompositionException {
        return CompositionMorphism.make(this, morphism);
    }

    
    /**
     * Returns the sum of this module morphism and <code>morphism</code>.
     * 
     * @throws CompositionException if sum could not be performed
     */
    public ModuleMorphism sum(ModuleMorphism morphism) 
    		throws CompositionException {
        return SumMorphism.make(this, morphism);
    }

    
    /**
     * Returns the difference of this module morphism and <code>morphism</code>.
     * 
     * @throws CompositionException if difference could not be performed
     */
    public ModuleMorphism difference(ModuleMorphism morphism)
        	throws CompositionException {
        return DifferenceMorphism.make(this, morphism);
    }

    
    /**
     * Returns this module morphism scaled by <code>element</code>.
     */
    public ModuleMorphism scaled(RingElement element)
            throws CompositionException {
        ModuleMorphism m = ScaledMorphism.make(this, element);
        if (m == null) {
            throw new CompositionException("ModuleMorphism.scaled: "+this+" cannot be scaled by "+element);
        }
        return m;
    }
    
    
    /**
     * Returns the value of the morphism evaluated at the zero of the domain.
     */
    public ModuleElement atZero() {
        try {
            return map(getDomain().getZero());
        }
        catch (MappingException e) {
            throw new AssertionError("This should never happen!");
        }
    }
    
    
    /**
     * Returns this module morphism raise to the power <code>n</code>.
     * The power must be non-negative and the domain must be equal
     * to the codomain. 
     * 
     * @throws CompositionException if power could not be performed
     */
    public ModuleMorphism power(int n)
    		throws CompositionException {
        return PowerMorphism.make(this, n);
    }

    
    /**
     * Returns the identity morphism in <code>module</code>.
     */
    public static ModuleMorphism getIdentityMorphism(Module module) {
        return new IdentityMorphism(module);
    }


    /**
     * Returns the constant <code>value</code> morphism in <code>module</code>.
     */
    public static ModuleMorphism getConstantMorphism(Module module, ModuleElement value) {
        return new ConstantMorphism(module, value);
    }


    /**
     * Returns a constant morphism with the domain of this
     * morphism that returns the specified constant <code>value</code>.
     */
    public ModuleMorphism getConstantMorphism(ModuleElement value) {
        return new ConstantMorphism(getDomain(), value);
    }

    
    /**
     * Returns true iff this morphism is the identity morphism.
     */
    public boolean isIdentity() {
        return false;
    }

    
    /**
     * Returns true iff this morphism is constant.
     */
    public boolean isConstant() {
        return false;
    }

    
    /**
     * Returns the domain of this morphism.
     */
    public final Module getDomain() {
        return domain;
    }

    
    /**
     * Returns the codomain of this morphism.
     */
    public final Module getCodomain() {
        return codomain;
    }

    
    /**
     * If true, then this is a module homomorphism.
     */
    public boolean isModuleHomomorphism() {
        return false;
    }
    

    /**
     * If true, then this is a ring homomorphism.
     */
    public boolean isRingHomomorphism() {
        return false;
    }

    
    /**
     * If true, then this is a morphism between rings;
     */
    public boolean isRingMorphism() {
        return getDomain().isRing() && getCodomain().isRing();
    }

    
    /**
     * Returns the the ring morphism that transforms between
     * the rings of the domain and codomain modules. 
     */
    public abstract ModuleMorphism getRingMorphism();
    
    
    /**
     * Returns true iff this is a linear morphism.
     */
    public boolean isLinear() {
        return false;
    }

    
    /**
     * Returns true iff the composition <code>f</code>*<code>g</code>
     * is possible.
     */
    public final static boolean composable(ModuleMorphism f, ModuleMorphism g) {
        return f.getDomain().equals(g.getCodomain());
    }
    
    
    /**
     * Returns true iff element <code>x</code> is in the domain of the morphism.
     */
    public boolean inDomain(ModuleElement x) {
        return domain.hasElement(x);
    }

    
    /**
     * Compares two module morphisms.
     * Checks first for equality.
     * The default comparison is on names, subclasses may implement
     * a more meaningful comparison.
     */
    public int compareTo(ModuleMorphism morphism) {
        if (this.equals(morphism)) {
            return 0;
        }
        else {
            return toString().compareTo(morphism.toString());
        }
    }
    
    
    /**
     * Returns true iff this morphism is equal to <code>object</code>.
     * In general it is not possible to determine whether to functions
     * are the same, so this returns true iff both morphisms have
     * the same structure.
     */
    public abstract boolean equals(Object object);
    
    
    /**
     * ModuleMorphism objects cannot be changed, so clone
     * returns the object itself.
     */
    public Object clone() {
        return this;
    }

    
    /**
     * Returns a string representation of this morphism.
     * This string is used for generic comparison.
     */
    public abstract String toString();
    
    
    private final Module domain;
    private final Module codomain;
}
