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
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.arith.ZnString;
import org.rubato.math.module.morphism.GenericAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over ZnStringRing.
 * @see org.rubato.math.module.ZnStringProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class ZnStringProperFreeModule
		extends ProperFreeModule
		implements ZnStringFreeModule {

    public static ZnStringFreeModule make(int dimension, int modulus) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 1) {
            return ZnStringRing.make(modulus);
        }
        else {
            return new ZnStringProperFreeModule(dimension, modulus);
        }
    }
    
    
    public ModuleElement getZero() {
        ZnString[] res = new ZnString[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = ZnString.getZero(modulus);
        }
        return ZnStringProperFreeElement.make(res, modulus);
    }
    
    
    public ModuleElement getUnitElement(int i) {
        ZnString[] v = new ZnString[getDimension()];
        for (int j = 0; j < getDimension(); j++) {
            v[j] = ZnString.getZero(modulus);
        }
        v[i] = ZnString.getOne(modulus);
        return ZnStringProperFreeElement.make(v, modulus);
    }
    

    public Module getNullModule() {
        return ZnStringProperFreeModule.make(0, modulus);
    }
    
    
    public boolean isNullModule() {
        return getDimension() == 0;
    }

    
    public Module getComponentModule(int i) {
        return ZnStringRing.make(modulus);
    }

    
    public Ring getRing() {
        return ZnStringRing.make(modulus);
    }


    public boolean isVectorspace() {
        return false;
    }

    
    public boolean hasElement(ModuleElement element) {
        return (element instanceof ZnStringProperFreeElement &&
                element.getLength() == getDimension() &&
                ((ZnStringProperFreeElement)element).getModulus() == modulus);
    }


    public int compareTo(Module object) {
        if (object instanceof ZnStringProperFreeModule) {
            ZnStringProperFreeModule module = (ZnStringProperFreeModule)object;
            int m = getModulus()-module.getModulus();
            if (m == 0) {
                return getDimension()-module.getDimension();
            }
            else {
                return m;
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ModuleElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getDimension()) {
            return null;
        }

        Iterator<ModuleElement> iter = elements.iterator();
        ZnString[] values = new ZnString[getDimension()];        
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement object = iter.next();
            if (object instanceof ZnStringElement) {
                values[i] = ((ZnStringElement)object).getValue();
            }
            else {
                return null;
            }
        }

        return ZnStringProperFreeElement.make(values, modulus);
    }
    
   
    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() > getDimension()) {
            Ring ring = getRing();
            List<ModuleElement> elementList = new LinkedList<ModuleElement>();
            for (int i = 0; i < getDimension(); i++) {
                ModuleElement e = ring.cast(element.getComponent(i));
                if (e != null) {
                    elementList.add(e);
                }
                else {
                    return null;
                }
            }
            return createElement(elementList);
        }
        return null;
    }

    
    public int getModulus() {
        return modulus;
    }
    
    
    public boolean equals(Object object) {
        return (object instanceof ZnStringProperFreeModule &&
                getDimension() == ((ZnStringProperFreeModule)object).getDimension() &&
                getModulus() == ((ZnStringProperFreeModule)object).getModulus());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        if (string.equals("Null")) {
            return ZnStringProperFreeElement.make(new ZnString[0], getModulus());
        }
        if (string.charAt(0) == '(' && string.charAt(string.length()-1) == ')') {
            string = string.substring(1, string.length()-1);
            String[] strings = TextUtils.split(string, ',');
            if (strings.length != getDimension()) {
                return null;
            }
            else {
                ZnString[] zstrings = new ZnString[getDimension()];
                for (int i = 0; i < strings.length; i++) {
                    zstrings[i] = ZnString.parseZnString(strings[i], getModulus());
                    if (zstrings[i] == null) {
                        return null;
                    }
                }
                return ZnStringProperFreeElement.make(zstrings, getModulus());
            }            
        }
        else {
            return null;
        }
    }
    
    
    public String toString() {
        return "ZnStringFreeModule["+getDimension()+"]";
    }


    public String toVisualString() {
        return "Z_"+getModulus()+"-String)^"+getDimension();
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(),
                             DIMENSION_ATTR, getDimension(),
                             MODULUS_ATTR, getModulus());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        int dimension = XMLReader.getIntAttribute(element, DIMENSION_ATTR, 0, Integer.MAX_VALUE, 0);
        int modulus0 = XMLReader.getIntAttribute(element, MODULUS_ATTR, 2, Integer.MAX_VALUE, 2);
        return ZnStringProperFreeModule.make(dimension, modulus0);
    }
    
    
    private static final XMLInputOutput<Module> xmlIO = ZnStringProperFreeModule.make(0, 2);
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "ZnStringFreeModule";
    }
    
    
    public static XMLInputOutput<Module> getXMLInputOuput() {
        return ZnStringProperFreeModule.make(0, 2);
    }
    

    public int hashCode() {
        return 11*(37*basicHash + getDimension())+getModulus();        
    }
    
    
    protected ModuleMorphism _getProjection(int index) {
        GenericAffineMorphism m = new GenericAffineMorphism(getRing(), getDimension(), 1);
        m.setMatrix(0, index, getRing().getOne());
        return m;
    }
    
    
    protected ModuleMorphism _getInjection(int index) {
        GenericAffineMorphism m = new GenericAffineMorphism(getRing(), 1, getDimension());
        m.setMatrix(index, 0, getRing().getOne());
        return m;
    }

    
    private ZnStringProperFreeModule(int dimension, int modulus) {
        super(dimension);
        this.modulus = modulus;
    }

    private int modulus;
    
    private final static int basicHash = "ZnStringFreeModule".hashCode();    
}
