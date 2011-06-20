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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.module.*;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * This class handles affine morphisms between free modules
 * over a given ring, that are not covered by specialized
 * classes such as ZFreeAfineMorphism, etc.
 * 
 * @author Gérard Milmeister
 */
public final class GenericAffineMorphism extends ModuleMorphism {

    /**
     * Creates an affine morphism from a free <code>ring</code>-module
     * of dimension <code>dim</code> to a free <code>ring</code>-module
     * of dimension <code>codim</code>. The morphism is the null
     * morphism by default.
     */
    public GenericAffineMorphism(Ring ring, int dim, int codim) {
        this(ring.getFreeModule(dim), ring.getFreeModule(codim));
    }
    

    /**
     * Sets the <code>i</code>,</code>j</code>-element of the
     * matrix to the specified <code>element</code>.
     */
    public void setMatrix(int i, int j, RingElement element) {
        if (ring.hasElement(element)) {
            A[i][j] = element;
        }
    }
    

    /**
     * Sets the </code>i</code>-th element of the translation
     * vector to </code>element</code>.
     */
    public void setVector(int i, RingElement element) {
        if (ring.hasElement(element)) {
            b[i] = element;
        }
    }
    

    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            try {
                RingElement[] v = new RingElement[dim];
                List<ModuleElement> res = new LinkedList<ModuleElement>();
                for (int i = 0; i < v.length; i++) {
                    v[i] = (RingElement)x.getComponent(i);                
                }
                for (int i = 0; i < codim; i++) {
                    ModuleElement r = ring.getZero();
                    for (int j = 0; j < dim; j++) {
                        r.add(A[i][j].product(v[j]));
                    }
                    r.add(b[i]);
                    res.add(r);
                }
                return getCodomain().createElement(res);
            }
            catch (DomainException e) {
                throw new AssertionError("This should never happen!");
            }
        }
        else {
            throw new MappingException("GenericAffineMorphism.map: ", x, this);
        }
    }

    
    public boolean isModuleHomomorphism() {
        return true;
    }
    

    public boolean isRingHomomorphism() {
        if (dim == 1 && codim == 1) {
            return b[0].isZero() && (A[0][0].isOne() || A[0][0].isZero());
        }
        else {
            return false;
        }
    }
    
    
    public boolean isLinear() {
        for (int i = 0; i < b.length; i++) {
            if (!b[i].isZero()) {
                return false;
            }
        }
        return true;
    }

    
    public boolean isIdentity() {
        if (dim != codim) {
            return false;
        }
        else if (isLinear()) {
            for (int i = 0; i < codim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i == j) {
                        if (!A[i][j].isOne()) {
                            return false;
                        }
                    }
                    else {
                        if (!A[i][j].isZero()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    
    public boolean isConstant() {
        for (int i = 0; i < codim; i++) {
            for (int j = 0; j < dim; j++) {
                if (!A[i][j].isZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    
    @Override
    public ModuleMorphism compose(ModuleMorphism morphism)
            throws CompositionException {
        if (morphism instanceof GenericAffineMorphism) {
            GenericAffineMorphism m = (GenericAffineMorphism)morphism;
            if (getDomain().equals(m.getCodomain())) {
                GenericAffineMorphism res = new GenericAffineMorphism(m.getDomain(), getCodomain());
                try {
                    int k = res.getCodomain().getDimension();
                    int l = res.getDomain().getDimension();
                    int o = getDomain().getDimension();
                    for (int i = 0; i < k; i++) {
                        for (int j = 0; j < l; j++) {
                            for (int n = 0; n < o; n++) {
                                res.A[i][j].add(A[i][n].sum(m.A[n][j]));
                            }
                        }
                    }
                    k = getCodomain().getDimension();
                    l = m.getCodomain().getDimension();
                    for (int i = 0; i < k; i++) {
                        for (int j = 0; j < l; j++) {
                            res.b[i].add(A[i][j].product(m.b[j]));
                        }
                        res.b[i].add(b[i]);
                    }
                }
                catch (DomainException e) {
                    throw new AssertionError("This should never happen!");
                }
                return res;
            }
            else {
                throw new CompositionException("GenericAffineMorphism.compose: ", this, m);
            }
        }
        else {
            return super.compose(morphism);
        }
    }
    
    
    public ModuleMorphism sum(ModuleMorphism morphism) 
            throws CompositionException {
        if (morphism instanceof GenericAffineMorphism) {
            GenericAffineMorphism m = (GenericAffineMorphism)morphism;
            if (getDomain().equals(m.getDomain()) && getCodomain().equals(m.getCodomain())) {
                GenericAffineMorphism res = new GenericAffineMorphism(getDomain(), getCodomain());
                try {
                    for (int i = 0; i < codim; i++) {
                        for (int j = 0; j < dim; j++) {
                            res.A[i][j] = A[i][j].sum(m.A[i][j]);
                        }
                        res.b[i] = b[i].sum(m.b[i]);
                    }
                }
                catch (DomainException e) {
                    throw new AssertionError("This should never happen!");
                }
                return res;
            }
            else {
                throw new CompositionException("GenericAffineMorphism.sum: ", this, m);
            }
        }
        else {
            return super.sum(morphism);
        }
    }
    
    
    public ModuleMorphism difference(ModuleMorphism morphism)
            throws CompositionException {
        if (morphism instanceof GenericAffineMorphism) {
            GenericAffineMorphism m = (GenericAffineMorphism)morphism;
            if (getDomain().equals(m.getDomain()) && getCodomain().equals(m.getCodomain())) {
                GenericAffineMorphism res = new GenericAffineMorphism(getDomain(), getCodomain());
                try {
                    for (int i = 0; i < codim; i++) {
                        for (int j = 0; j < dim; j++) {
                            res.A[i][j] = A[i][j].difference(m.A[i][j]);
                        }
                        res.b[i] = b[i].difference(m.b[i]);
                    }
                }
                catch (DomainException e) {
                    throw new AssertionError("This should never happen!");
                }
                return res;
            }
            else {
                throw new CompositionException("GenericAffineMorphism.difference: ", this, m);
            }
        }
        else {
            return super.sum(morphism);
        }
    }    
    
    
    public ModuleMorphism getRingMorphism() {
        return ring.getIdentityMorphism();
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof GenericAffineMorphism) {
            GenericAffineMorphism m = (GenericAffineMorphism)object;
            if (getDomain() != m.getDomain() && getCodomain() != m.getCodomain()) {
                return false;
            }
            for (int i = 0; i < codim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (!A[i][j].equals(m.A[i][j])) {
                        return false;
                    }
                }
                if (!b[i].equals(m.b[i])) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }


    public String toString() {
        return "GenericAffineMorphism["+getDomain()+","+getCodomain()+"]";
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        getDomain().toXML(writer);
        getCodomain().toXML(writer);
        for (int i = 0; i < codim; i++) {
            for (int j = 0; j < dim; j++) {
                A[i][j].toXML(writer);
            }
        }
        for (int i = 0; i < codim; i++) {
            b[i].toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        Element m = XMLReader.getChild(element, MODULE);
        if (m == null) {
            reader.setError("Type %%1 is missing child of type <%2>.", getElementTypeName(), MODULE);
            return null;            
        }
        Module domain = reader.parseModule(m);
        if (domain == null) {
            return null;
        }
        m = XMLReader.getNextSibling(m, MODULE);
        if (m == null) {
            reader.setError("Type %%1 is missing second child of type <%2>.", getElementTypeName(), MODULE);
            return null;            
        }
        Module codomain = reader.parseModule(m);
        if (codomain == null) {
            return null;
        }
        if (!codomain.getRing().equals(domain.getRing())) {
            reader.setError("Domain and codomain must be modules over the same ring.");
            return null;
        }
        Ring ring0 = domain.getRing();
        int dim0 = domain.getDimension();
        int codim0 = codomain.getDimension();
        int n = 0;
        int count = dim0*codim0+codim0;
        LinkedList<RingElement> ringElements = new LinkedList<RingElement>();
        m = XMLReader.getNextSibling(m, MODULEELEMENT);
        while (m != null) {
            ModuleElement e = reader.parseModuleElement(m);
            if (e == null || !ring0.hasElement(e)) {
                reader.setError("Wrong element type.");
                return null;
            }
            ringElements.add((RingElement)e);
            m = XMLReader.getNextSibling(m, MODULEELEMENT);
            n++;
        }
        if (n != count) {
            reader.setError("Wrong number of elements.");
            return null;
        }
        GenericAffineMorphism res = new GenericAffineMorphism(ring0, dim0, codim0);
        Iterator<RingElement> iter = ringElements.iterator();
        for (int i = 0; i < codim0; i++) {
            for (int j = 0; j < dim0; j++) {
                res.setMatrix(i, j, iter.next());
            }
        }
        for (int i = 0; i < codim0; i++) {
            res.setVector(i, iter.next());
        }
        return res;
    }

    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new GenericAffineMorphism(ZRing.ring, ZRing.ring);

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "GenericAffineMorphism";
    }
    

    private GenericAffineMorphism(Module domain, Module codomain) {
        super(domain, codomain);
        ring = domain.getRing();
        dim = domain.getDimension();
        codim = codomain.getDimension();
        A = new RingElement[codim][dim];
        b = new RingElement[codim];
        for (int i = 0; i < codim; i++) {
            for (int j = 0 ; j < dim; j++) {
                A[i][j] = ring.getZero();                
            }
            b[i] = ring.getZero();
        }
    }

    
    private Ring ring;
    private int  dim;
    private int  codim;
    private RingElement[][] A;
    private RingElement[]   b;
}
