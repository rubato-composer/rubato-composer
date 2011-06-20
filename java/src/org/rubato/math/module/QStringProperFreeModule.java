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

import org.rubato.math.arith.QString;
import org.rubato.math.module.morphism.GenericAffineMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over QStringRing.
 * @see org.rubato.math.module.QStringRing
 * 
 * @author Gérard Milmeister
 */
public final class QStringProperFreeModule
		extends ProperFreeModule
		implements QStringFreeModule {

    public static final QStringProperFreeModule nullModule = new QStringProperFreeModule(0);

    public static QStringFreeModule make(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return nullModule;
        }
        else if (dimension == 1) {
            return QStringRing.ring;
        }
        else {
            return new QStringProperFreeModule(dimension);
        }
    }
    
    
    public ModuleElement getZero() {
        QString[] res = new QString[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = QString.getZero();
        }
        return QStringProperFreeElement.make(res);
    }
    
    
    public ModuleElement getUnitElement(int i) {
        QString[] v = new QString[getDimension()];
        assert(i >= 0 && i < getDimension());
        for (int j = 0; j < getDimension(); j++) {
            v[j] = QString.getZero();
        }
        v[i] = QString.getOne();
        return QStringProperFreeElement.make(v);
    }
    

    public Module getNullModule() {
        return nullModule;
    }
    
    
    public boolean isNullModule() {
        return this == nullModule;
    }

    
    public Module getComponentModule(int i) {
        return QStringRing.ring;
    }

    
    public Ring getRing() {
        return QStringRing.ring;
    }


    public boolean isVectorspace() {
        return false;
    }

    
    public boolean hasElement(ModuleElement element) {
        return (element instanceof QStringProperFreeElement &&
                element.getLength() == getDimension());
    }


    public int compareTo(Module object) {
        if (object instanceof QStringProperFreeModule) {
            QStringProperFreeModule module = (QStringProperFreeModule)object;
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
        QString[] values = new QString[getDimension()];        
        for (int i = 0; i < getDimension(); i++) {
            Object object = iter.next();
            if (object instanceof QStringElement) {
                values[i] = ((QStringElement)object).getValue();
            }
            else {
                return null;
            }
            i++;
        }

        return QStringProperFreeElement.make(values);
    }
    
   
    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() > getDimension()) {
            QStringRing ring = QStringRing.ring;
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
        return (object instanceof QStringProperFreeModule &&
                getDimension() == ((QStringProperFreeModule)object).getDimension());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        if (string.equals("Null")) {
            return QStringProperFreeElement.make(new QString[0]);
        }
        if (string.charAt(0) == '(' && string.charAt(string.length()-1) == ')') {
            string = string.substring(1, string.length()-1);
            String[] strings = TextUtils.split(string, ',');
            if (strings.length != getDimension()) {
                return null;
            }
            else {
                QString[] qstrings = new QString[getDimension()];
                for (int i = 0; i < strings.length; i++) {
                    qstrings[i] = QString.parseQString(strings[i]);
                    if (qstrings[i] == null) {
                        return null;
                    }
                }
                return QStringProperFreeElement.make(qstrings);
            }            
        }
        else {
            return null;
        }
    }
    
    
    public String toString() {
        return "QStringFreeModule["+getDimension()+"]";
    }


    public String toVisualString() {
        return "(C-String)^"+getDimension();
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(), DIMENSION_ATTR, getDimension());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        int dimension = XMLReader.getIntAttribute(element, DIMENSION_ATTR, 0, Integer.MAX_VALUE, 0);
        return QStringProperFreeModule.make(dimension);
    }
    
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return QStringProperFreeModule.nullModule;
    }
    

    public String getElementTypeName() {
        return "QStringFreeModule";
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

    
    private QStringProperFreeModule(int dimension) {
        super(dimension);
    }

    
    private final static int basicHash = "QStringFreeModule".hashCode();    
}
