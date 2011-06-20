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

import static org.rubato.xml.XMLConstants.*;

import java.util.Iterator;
import java.util.List;

import org.rubato.math.matrix.ZnMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.ZnFreeAffineMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over integers mod <i>n</i>.
 * @see org.rubato.math.module.ZnProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class ZnProperFreeModule
		extends ProperFreeModule
		implements ZnFreeModule { 

    public static ZnFreeModule make(int dimension, int modulus) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return new ZnProperFreeModule(0, modulus);
        }
        else if (dimension == 1) {
            return ZnRing.make(modulus);
        }
        else {
            return new ZnProperFreeModule(dimension, modulus);
        }
    }

    
    public ZnProperFreeElement getZero() {
        int[] res = new int[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = 0;
        }
        return (ZnProperFreeElement)ZnProperFreeElement.make(res, modulus);
    }
    

    public ZnProperFreeElement getUnitElement(int i) {
        int[] v = new int[getDimension()];
        v[i] = 1;
        return (ZnProperFreeElement)ZnProperFreeElement.make(v, getModulus());
    }

    
    public Module getNullModule() {
        return make(0, modulus);
    }
    
    
    public boolean isNullModule() {
        return getDimension() == 0;
    }


    public Module getComponentModule(int i) {
        if (componentModule == null) {
            componentModule = ZnRing.make(modulus);
        }
        return componentModule;
    }


    public Ring getRing() {
        return ZnRing.make(modulus);
    }


    public boolean isVectorspace() {
        return getRing().isField();
    }
    
    
    public boolean hasElement(ModuleElement element) {
        return (element instanceof ZnProperFreeElement &&
                element.getLength() == getDimension() &&
                ((ZnProperFreeElement)element).getModulus() == getModulus());
    }


    public int compareTo(Module object) {
        if (object instanceof ZnProperFreeModule) {
            ZnProperFreeModule module = (ZnProperFreeModule)object;
            int m = getModulus()-module.getModulus();
            if (m != 0) {
                return m;
            }
            else {
	            return getDimension()-module.getDimension();
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ZnFreeElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getDimension()) {
            return null;
        }

        Iterator<ModuleElement> iter = elements.iterator();
        int[] values = new int[getDimension()];        
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement castElement = iter.next().cast(getRing());
            if (castElement == null) {
                return null;
            }
            values[i] = ((ZnElement)castElement).getValue();
        }

        return ZnProperFreeElement.make(values, getModulus());
    }


    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() == getDimension()) {
            if (element instanceof DirectSumElement) {
                return element.cast(this);
            }
            else if (element instanceof ZProperFreeElement &&
                     ((ZnProperFreeElement)element).getModulus() == modulus) {
                return element;
            }
            else {   
                int[] elements = new int[getDimension()];
                for (int i = 0; i < getDimension(); i++) {
                    ModuleElement castElement = getRing().cast(element.getComponent(i));
                    if (castElement == null) {
                        return null;
                    }
                    elements[i] = ((ZnElement)castElement).getValue();
                }
                return ZnProperFreeElement.make(elements, modulus);
            }
        }
        else {
            return null;
        }
    }

    
    public boolean equals(Object object) {
        return (object instanceof ZnProperFreeModule &&
                getDimension() == ((ZnProperFreeModule)object).getDimension() &&
                modulus == ((ZnProperFreeModule)object).getModulus());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        String[] components = string.trim().split(",");
        if (components.length != getDimension()) {
            return null;
        }
        else {
            int[] values = new int[components.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    values[i] = Integer.parseInt(components[i]);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            return ZnProperFreeElement.make(values, getModulus());
        }
    }
    
    
    public String toString() {
        return "ZnFreeModule("+getModulus()+")["+getDimension()+"]";
    }

    
    public String toVisualString() {
        return "Z_"+getModulus()+"^"+getDimension();
    }

    
    public final int getModulus() {
        return modulus;
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(),
                             DIMENSION_ATTR, getDimension(),
                             MODULUS_ATTR, getModulus());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));

        if (!element.hasAttribute(DIMENSION_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), DIMENSION_ATTR);
            return null;                
        }
        if (!element.hasAttribute(MODULUS_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), MODULUS_ATTR);
            return null;                
        }

        int dimension;
        try {
            dimension = Integer.parseInt(element.getAttribute(DIMENSION_ATTR));                
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", DIMENSION_ATTR, getElementTypeName());
            return null;                                    
        }
        if (dimension < 0) {
            reader.setError("Attribute %%1 of type %%2 must be an integer >= 0.", DIMENSION_ATTR, getElementTypeName());
            return null;                                                    
        }

        int mod;
        try {
            mod = Integer.parseInt(element.getAttribute(MODULUS_ATTR));
            if (mod < 2) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer > 1.", MODULUS_ATTR, getElementTypeName());
            return null;                                    
        }

        return ZnProperFreeModule.make(dimension, mod);
    }
    
    
    public String getElementTypeName() {
        return "ZnFreeElement";
    }

    
    private final static XMLInputOutput<Module> xmlIO = ZnProperFreeModule.make(0, 2); 
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return xmlIO;
    }
    

    public int hashCode() {
        return 37*37*basicHash+37*modulus+getDimension();
    }
    
    
    protected ModuleMorphism _getProjection(int index) {
        ZnMatrix A = new ZnMatrix(1, getDimension(), getModulus());
        A.set(0, index, 1);
        return ZnFreeAffineMorphism.make(A, new int[] { 0 });
    }
    
    
    protected ModuleMorphism _getInjection(int index) {
        ZnMatrix A = new ZnMatrix(getDimension(), 1, getModulus());
        A.set(index, 0, 1);
        int[] b = new int[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            b[i] = 0;
        }
        return ZnFreeAffineMorphism.make(A, b);
    }
    
    
    private ZnProperFreeModule(int dimension, int modulus) {
        super(dimension);
        this.modulus = modulus;
    }


    private final static int basicHash = "ZnFreeModule".hashCode();

    private int    modulus;
    private ZnRing componentModule = null;
}
