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
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.arith.RString;
import org.rubato.math.module.morphism.GenericAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over RStringRing.
 * @see org.rubato.math.module.RStringProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class RStringProperFreeModule
		extends ProperFreeModule
		implements RStringFreeModule {

    public static final RStringProperFreeModule nullModule = new RStringProperFreeModule(0);

    public static RStringFreeModule make(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return nullModule;
        }
        else if (dimension == 1) {
            return RStringRing.ring;
        }
        else {
            return new RStringProperFreeModule(dimension);
        }
    }
    
    
    public ModuleElement getZero() {
        RString[] res = new RString[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = RString.getZero();
        }
        return RStringProperFreeElement.make(res);
    }
    
    
    public ModuleElement getUnitElement(int i) {
        RString[] v = new RString[getDimension()];
        for (int j = 0; j < getDimension(); j++) {
            v[j] = RString.getZero();
        }
        v[i] = RString.getOne();
        return RStringProperFreeElement.make(v);
    }
    

    public Module getNullModule() {
        return nullModule;
    }
    
    
    public boolean isNullModule() {
        return this == nullModule;
    }

    
    public Module getComponentModule(int i) {
        return RStringRing.ring;
    }

    
    public Ring getRing() {
        return RStringRing.ring;
    }


    public boolean isVectorspace() {
        return false;
    }

    
    public boolean hasElement(ModuleElement element) {
        return (element instanceof RStringProperFreeElement &&
                element.getLength() == getDimension());
    }


    public int compareTo(Module object) {
        if (object instanceof RStringProperFreeModule) {
            RStringProperFreeModule module = (RStringProperFreeModule)object;
            return getDimension()-module.getDimension();
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
        RString[] values = new RString[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement object = iter.next();
            if (object instanceof RStringElement) {
                values[i] = ((RStringElement)object).getValue();
            }
            else {
                return null;
            }
        }

        return RStringProperFreeElement.make(values);
    }
    
   
    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() > getDimension()) {
            RStringRing ring = RStringRing.ring;
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

    
    public boolean equals(Object object) {
        return (object instanceof RStringProperFreeModule &&
                getDimension() == ((RStringProperFreeModule)object).getDimension());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        if (string.equals("Null")) {
            return RStringProperFreeElement.make(new RString[0]);
        }
        if (string.charAt(0) == '(' && string.charAt(string.length()-1) == ')') {
            string = string.substring(1, string.length()-1);
            String[] strings = TextUtils.split(string, ',');
            if (strings.length != getDimension()) {
                return null;
            }
            else {
                RString[] rstrings = new RString[getDimension()];
                for (int i = 0; i < strings.length; i++) {
                    rstrings[i] = RString.parseRString(strings[i]);
                    if (rstrings[i] == null) {
                        return null;
                    }
                }
                return RStringProperFreeElement.make(rstrings);
            }            
        }
        else {
            return null;
        }
    }
    
    
    public String toString() {
        return "RStringFreeModule["+getDimension()+"]";
    }


    public String toVisualString() {
        return "(R-String)^"+getDimension();
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
            dimension = Integer.parseInt(element.getAttribute(DIMENSION_ATTR));
            if (dimension < 0) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer >= 0.", DIMENSION_ATTR, getElementTypeName());
            return null;                                    
        }

        return RStringProperFreeModule.make(dimension);
    }

    
    public String getElementTypeName() {
        return "RStringFreeModule";
    }
    
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return RStringProperFreeModule.nullModule;
    }
    

    public int hashCode() {
        return 37*basicHash + getDimension();
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

    
    private RStringProperFreeModule(int dimension) {
        super(dimension);
    }

    
    private final static int basicHash = "RStringFreeModule".hashCode();    
}
