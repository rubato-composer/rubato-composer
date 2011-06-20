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

import static org.rubato.xml.XMLConstants.DIMENSION_ATTR;
import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.Iterator;
import java.util.List;

import org.rubato.math.arith.Complex;
import org.rubato.math.matrix.CMatrix;
import org.rubato.math.module.morphism.CFreeAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The free modules over complex numbers.
 * @see org.rubato.math.module.CProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class CProperFreeModule
		extends ProperFreeModule
		implements CFreeModule {

    public static final CProperFreeModule nullModule = new CProperFreeModule(0);  
       
    /**
     * Constructs a free module over complex numbers with given <code>dimension</code>.
     * 
     * @param dimension the dimension of the free module over C,
     *                  if < 0, assumed to be 0
     */
    public static CFreeModule make(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return nullModule;
        }
        else if (dimension == 1) {
            return CRing.ring;
        }
        else {
            return new CProperFreeModule(dimension);
        }
    }

    
    public CFreeElement getZero() {
        Complex[] res = new Complex[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = Complex.getZero();
        }
        return CProperFreeElement.make(res);
    }

    
    public CFreeElement getUnitElement(int i) {
        Complex[] v = new Complex[getDimension()];
        assert(i >= 0 && i < getDimension());
        for (int j = 0; j < getDimension(); j++) {
            v[j] = Complex.getZero();
        }
        v[i] = Complex.getOne();
        return CProperFreeElement.make(v);
    }
    

    public CProperFreeModule getNullModule() {
        return nullModule;
    }
    
    
    public boolean isNullModule() {
        return this == nullModule;
    }

    
    public CRing getComponentModule(int i) {
        return CRing.ring;
    }

    
    public CRing getRing() {
        return CRing.ring;
    }


    public boolean isVectorspace() {
        return true;
    }
    

    public boolean hasElement(ModuleElement element) {
        return (element instanceof CProperFreeElement &&
                element.getLength() == getDimension());
    }
    

    public int compareTo(Module object) {
        if (object instanceof CProperFreeModule) {
            CProperFreeModule module = (CProperFreeModule)object;
            return getDimension()-module.getDimension();
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public CFreeElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getDimension()) {
            return null;
        }

        Complex[] values = new Complex[getDimension()];        
        Iterator<ModuleElement> iter = elements.iterator();
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement castElement = iter.next().cast(CRing.ring);
            if (castElement == null) {
                return null;
            }
            values[i] = ((CElement)castElement).getValue();
        }

        return CProperFreeElement.make(values);
    }

    
    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() == getDimension()) {
            if (element instanceof DirectSumElement) {
                return element.cast(this);
            }
            else if (element instanceof CProperFreeElement) {
                return element;
            }
            else {   
                Complex[] elements = new Complex[getDimension()];
                for (int i = 0; i < getDimension(); i++) {
                    ModuleElement castElement = CRing.ring.cast(element.getComponent(i));
                    if (castElement == null) {
                        return null;
                    }
                    elements[i] = ((CElement)castElement).getValue();
                }
                return CProperFreeElement.make(elements);
            }
        }
        else {
            return null;
        }
    }

    
    public boolean equals(Object object) {
        return (object instanceof CProperFreeModule &&
                getDimension() == ((CProperFreeModule)object).getDimension());
    }

    
    public CFreeElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        String[] components = string.split(",");
        if (components.length != getDimension()) {
            return null;
        }
        else {
            Complex[] values = new Complex[components.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    values[i] = Complex.parseComplex(components[i]);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            return CProperFreeElement.make(values);
        }
    }
    
    
    public String toString() {
        return "CFreeModule["+getDimension()+"]";
    }
    
    
    public String toVisualString() {
        return "C^"+getDimension();
    }


    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(), DIMENSION_ATTR, getDimension());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        int dimension = XMLReader.getIntAttribute(element, DIMENSION_ATTR, 0, Integer.MAX_VALUE, 0);
        return CProperFreeModule.make(dimension);
    }
    
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return CProperFreeModule.nullModule;
    }

    
    public String getElementTypeName() {
        return "CFreeModule";
    }
    
    
    public static XMLInputOutput<Module> getXMLInput() {
        return CProperFreeModule.nullModule;
    }
    

    public int hashCode() {
        return 37*basicHash + getDimension();
    }
    

    protected ModuleMorphism _getProjection(int index) {
        CMatrix A = new CMatrix(1, getDimension());
        A.set(0, index, Complex.getOne());
        return CFreeAffineMorphism.make(A, new Complex[] { Complex.getZero() });
    }
    
    
    protected ModuleMorphism _getInjection(int index) {
        CMatrix A = new CMatrix(getDimension(), 1);
        A.set(index, 0, Complex.getOne());
        Complex[] b = new Complex[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            b[i] = Complex.getZero();
        }
        return CFreeAffineMorphism.make(A, b);
    }
    
    
    private CProperFreeModule(int dimension) {
        super(dimension);
    }

    
    private final static int basicHash = "CFreeModule".hashCode();
}
