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

import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.QMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.QFreeAffineMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Free modules over rationals.
 * @see org.rubato.math.module.QProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public final class QProperFreeModule
		extends ProperFreeModule
		implements QFreeModule {

    public static final QProperFreeModule nullModule = new QProperFreeModule(0);

    public static QFreeModule make(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        if (dimension == 0) {
            return nullModule;
        }
        else if (dimension == 1) {
            return QRing.ring;
        }
        else {
            return new QProperFreeModule(dimension);
        }
    }

    
    public QProperFreeElement getZero() {
        Rational[] res = new Rational[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            res[i] = new Rational(0);
        }
        return (QProperFreeElement)QProperFreeElement.make(res);
    }

    
    public QProperFreeElement getUnitElement(int i) {
        Rational[] v = new Rational[getDimension()];
        for (int j = 0; j < getDimension(); j++) {
            v[j] = Rational.getZero();
        }
        v[i] = Rational.getOne();
        return (QProperFreeElement)QProperFreeElement.make(v);
    }
    

    public QProperFreeModule getNullModule() {
        return nullModule;
    }
    
    
    public boolean isNullModule() {
        return this == nullModule;
    }

    
    public QRing getComponentModule(int i) {
        return QRing.ring;
    }
    

    public QRing getRing() {
        return QRing.ring;
    }


    public boolean isVectorspace() {
        return true;
    }
   

    public boolean hasElement(ModuleElement element) {
        return (element instanceof QProperFreeElement &&
                element.getLength() == getDimension());
    }

    
    public int compareTo(Module object) {
        if (object instanceof QProperFreeModule) {
            QProperFreeModule module = (QProperFreeModule)object;
            return getDimension()-module.getDimension();
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public QProperFreeElement createElement(List<ModuleElement> elements) {
        if (elements.size() < getDimension()) {
            return null;
        }

        Iterator<ModuleElement> iter = elements.iterator();
        Rational[] values = new Rational[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement castElement = iter.next().cast(QRing.ring);
            if (castElement == null) {
                return null;
            }
            values[i] = ((QElement)castElement).getValue();
        }

        return (QProperFreeElement)QProperFreeElement.make(values);
    }


    public ModuleElement cast(ModuleElement element) {
        if (element.getLength() == getDimension()) {
            if (element instanceof DirectSumElement) {
                return element.cast(this);
            }
            else if (element instanceof QProperFreeElement) {
                return element;
            }
            else {   
                Rational[] elements = new Rational[getDimension()];
                for (int i = 0; i < getDimension(); i++) {
                    ModuleElement castElement = QRing.ring.cast(element.getComponent(i));
                    if (castElement == null) {
                        return null;
                    }
                    elements[i] = ((QElement)castElement).getValue();
                }
                return QProperFreeElement.make(elements);
            }
        }
        else {
            return null;
        }
    }


    public boolean equals(Object object) {
        return (object instanceof QProperFreeModule &&
                	getDimension() == ((QProperFreeModule)object).getDimension());
    }

    
    public ModuleElement parseString(String string) {
        string = TextUtils.unparenthesize(string);
        String[] components = string.split(",");
        if (components.length != getDimension()) {
            return null;
        }
        else {
            Rational[] values = new Rational[components.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    values[i] = Rational.parseRational(components[i]);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            return QProperFreeElement.make(values);
        }
    }
    
    
    public String toString() {
        return "QFreeModule["+getDimension()+"]";
    }

    
    public String toVisualString() {
        return "Q^"+getDimension();
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
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of type %%2 must be an integer.", DIMENSION_ATTR, getElementTypeName());
            return null;                                    
        }
        if (dimension < 0) {
            reader.setError("Attribute %%1 of type %%2 must be an integer >= 0.", DIMENSION_ATTR, getElementTypeName());
            return null;                                                    
        }

        return QProperFreeModule.make(dimension);
    }
    
    
    public String getElementTypeName() {
        return "QFreeModule";
    }
    
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return QProperFreeModule.nullModule;
    }
    

    public int hashCode() {
        return 37*basicHash + getDimension();
    }
    

    protected ModuleMorphism _getProjection(int index) {
        QMatrix A = new QMatrix(1, getDimension());
        A.set(0, index, Rational.getOne());
        return QFreeAffineMorphism.make(A, new Rational[] { Rational.getZero() });
    }
    
    
    protected ModuleMorphism _getInjection(int index) {
        QMatrix A = new QMatrix(getDimension(), 1);
        A.set(index, 0, Rational.getOne());
        Rational[] b = new Rational[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            b[i] = Rational.getZero();
        }
        return QFreeAffineMorphism.make(A, b);
    }
    
    
    private QProperFreeModule(int dimension) {
        super(dimension);
    }


    private final static int basicHash = "QFreeModule".hashCode();
}
