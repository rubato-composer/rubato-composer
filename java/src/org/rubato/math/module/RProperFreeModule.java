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

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over real numbers.
 * @see org.rubato.math.module.RProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class RProperFreeModule
		extends ProperFreeModule
		implements RFreeModule {

    public static final RProperFreeModule nullModule = new RProperFreeModule(0);  
    
    public static RFreeModule make(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return nullModule;
        }
        else if (dimension == 1) {
            return RRing.ring;
        }
        else {
            return new RProperFreeModule(dimension);
        }
    }

    
    public RProperFreeElement getZero() {
        double[] res = new double[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = 0;
        }
        return (RProperFreeElement)RProperFreeElement.make(res);
    }


    public RProperFreeElement getUnitElement(int i) {
        double[] v = new double[getDimension()];
        v[i] = 1;
        return (RProperFreeElement)RProperFreeElement.make(v);
    }
    
    
    public Module getNullModule() {
        return nullModule;
    }
    
    
    public boolean isNullModule() {
        return (this == nullModule);
    }

    
    public Module getComponentModule(int i) {
        return RRing.ring;
    }


    public Ring getRing() {
        return RRing.ring;
    }


    public boolean isVectorspace() {
        return true;
    }

    
    public boolean hasElement(ModuleElement element) {
        return (element instanceof RProperFreeElement &&
                element.getLength() == getDimension());
    }


    public int compareTo(Module object) {
        if (object instanceof RProperFreeModule) {
            RProperFreeModule module = (RProperFreeModule)object;
            return getDimension()-module.getDimension();
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public RFreeElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getDimension()) {
            return null;
        }

        Iterator<ModuleElement> iter = elements.iterator();
        double[] values = new double[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement castElement = iter.next().cast(RRing.ring);
            if (castElement == null) {
                return null;
            }
            values[i] = ((RElement)castElement).getValue();
        }

        return RProperFreeElement.make(values);
    }

    
    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() == getDimension()) {
            if (element instanceof DirectSumElement) {
                return element.cast(this);
            }
            else if (element instanceof RProperFreeElement) {
                return element;
            }
            else {   
                double[] elements = new double[getDimension()];
                for (int i = 0; i < getDimension(); i++) {
                    ModuleElement castElement = RRing.ring.cast(element.getComponent(i));
                    if (castElement == null) {
                        return null;
                    }
                    elements[i] = ((RElement)castElement).getValue();
                }
                return RProperFreeElement.make(elements);
            }
        }
        else {
            return null;
        }
    }


    public boolean equals(Object object) {
        return (object instanceof RProperFreeModule &&
                	getDimension() == ((RProperFreeModule)object).getDimension());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        String[] components = string.split(",");
        if (components.length != getDimension()) {
            return null;
        }
        else {
            double[] values = new double[components.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    values[i] = Double.parseDouble(components[i]);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            return RProperFreeElement.make(values);
        }
    }
    
    
    public String toString() {
        return "RFreeModule["+getDimension()+"]";
    }


    public String toVisualString() {
        return "R^"+getDimension();
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(), DIMENSION_ATTR, getDimension());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));

        if (!element.hasAttribute(DIMENSION_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), DIMENSION_ATTR);
            return null;                
        }

        int dimension;
        try {
            dimension = Integer.parseInt(element.getAttribute("dimension"));                
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", DIMENSION_ATTR, getElementTypeName());
            return null;                                    
        }
        if (dimension < 0) {
            reader.setError("Attribute %%1 of type %%2 must be an integer >= 0.", DIMENSION_ATTR, getElementTypeName());
            return null;                                                    
        }

        return RProperFreeModule.make(dimension);
    }

    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return RProperFreeModule.nullModule;
    }

    
    public String getElementTypeName() {
        return "RFreeModule";
    }
    
    
    public int hashCode() {
        return 37*basicHash + getDimension();
    }
    
    
    protected ModuleMorphism _getProjection(int index) {
        RMatrix A = new RMatrix(1, getDimension());
        A.set(0, index, 1);
        return RFreeAffineMorphism.make(A, new double[] { 0.0 });
    }
    
    
    protected ModuleMorphism _getInjection(int index) {
        RMatrix A = new RMatrix(getDimension(), 1);
        A.set(index, 0, 1.0);
        double[] b = new double[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            b[i] = 0.0;
        }
        return RFreeAffineMorphism.make(A, b);
    }
    
    
    private RProperFreeModule(int dimension) {
        super(dimension);
    }


    private final static int basicHash = "RFreeModule".hashCode();
}
