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

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.List;

import org.rubato.math.arith.Rational;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The field of rationals.
 * @see org.rubato.math.module.QElement
 * 
 * @author Gérard Milmeister
 */
public final class QRing extends NumberRing implements QFreeModule {

    /**
     * The unique instance of the ring of rationals.
     */
    public static final QRing ring = new QRing();

    public QElement getZero() {
        return new QElement(new Rational(0));
    }
    

    public QElement getOne() {
        return new QElement(new Rational(1));
    }

    
    public QElement getUnitElement(int i) {
        return getOne();
    }
    
    
    public QProperFreeModule getNullModule() {
        return QProperFreeModule.nullModule;
    }
    
    
    public QRing getComponentModule(int i) {
        return this;
    }
    
    
    public QRing getRing() {
        return this;
    }
    
    
    public boolean isField() {
        return true;
    }
    
    
    public boolean isVectorspace() {
        return true;
    }

    
    public ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }
    

    public boolean hasElement(ModuleElement element) {
        return element instanceof QElement;
    }
    

    public QFreeModule getFreeModule(int dimension) {
        return QProperFreeModule.make(dimension);
    }


    public boolean equals(Object object) {
        return this == object;
    }

    
    public int compareTo(Module object) {
        if (this == object) {
            return 0;
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public QElement createElement(List<ModuleElement> elements) {
        if (!elements.isEmpty()) {
            return (QElement)elements.get(0).cast(this);
        }
        else {
            return null;
        }
    }


    public QElement cast(ModuleElement element) {
        if (element instanceof ZElement) {
            return cast((ZElement)element);
        }
        else if (element instanceof ZnElement) {
            return cast((ZnElement)element);
        }
        else if (element instanceof QElement) {
            return (QElement)element;
        }
        else if (element instanceof RElement) {
            return cast((RElement)element);
        }
        else if (element instanceof CElement) {
            return cast((CElement)element);
        }
        else if (element instanceof DirectSumElement) {
            return (QElement)element.cast(this);
        }
        else {
            return null;
        }
    }

    
    public QElement cast(ZElement element) {
        return new QElement(element.getValue(), 1);
    }

    
    public QElement cast(ZnElement element) {
        return new QElement(element.getValue(), 1);
    }
    
    
    public QElement cast(RElement element) {
        return new QElement(new Rational(element.getValue()));
    }

    
    public QElement cast(CElement element) {
        return new QElement(new Rational(element.getValue().getReal()));
    }

    
    public String toString() {
        return "QRing";
    }
    
    
    public String toVisualString() {
        return "Q";
    }

    
    public QElement parseString(String string) {
    	try {
    		Rational value = Rational.parseRational(TextUtils.unparenthesize(string));
        	return new QElement(value);
    	}
    	catch (NumberFormatException e) {
    		return null;
    	}
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        return QRing.ring;
    }
    
    
    public String getElementTypeName() {
        return "QRing";
    }
    

    public static XMLInputOutput<Module> getXMLInputOutput() {
        return QRing.ring;
    }
    

    public int hashCode() {
        return basicHash;
    }
    
    
    protected int getNumberRingOrder() {
        return 200;
    }
    

    private final static int basicHash = "QRing".hashCode();

    private QRing() { /* not allowed */ }
}
