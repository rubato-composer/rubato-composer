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

import org.rubato.math.arith.Complex;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The field of complex numbers.
 * @see org.rubato.math.module.CElement
 * 
 * @author Gérard Milmeister
 */
public final class CRing extends NumberRing implements CFreeModule {

    /**
     * The unique instance of the ring of complex numbers.
     */
    public static final CRing ring = new CRing();

    public CElement getZero() {
        return new CElement(0);
    }


    public CElement getOne() {
        return new CElement(1);
    }
    
    
    public CElement getUnitElement(int i) {
        return getOne();
    }
    
    
    public CFreeModule getNullModule() {
        return CProperFreeModule.nullModule;
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
        return (element instanceof CElement);
    }
    

    public CFreeModule getFreeModule(int dimension) {
        return CProperFreeModule.make(dimension);
    }

    
    public boolean equals(Object object) {
        return (this == object);
    }

    
    public int compareTo(Module object) {
        if (this == object) {
            return 0;
        }
        else {
            return super.compareTo(object);
        }
    }


    public CElement createElement(List<ModuleElement> elements) {        
        if (!elements.isEmpty()) {
            return (CElement)elements.get(0).cast(this);
        }
        else {
            return null;
        }
    }
    

    public CElement cast(ModuleElement element) {
        if (element instanceof ZElement) {
            return cast((ZElement)element);
        }
        else if (element instanceof ZnElement) {
            return cast((ZnElement)element);
        }
        else if (element instanceof QElement) {
            return cast((QElement)element);
        }
        else if (element instanceof RElement) {
            return cast((RElement)element);
        }
        else if (element instanceof CElement) {
            return cast((CElement)element);
        }
        else if (element instanceof DirectSumElement) {
            return (CElement)element.cast(this);
        }
        else {
            return null;
        }
    }

    
    public CElement cast(ZElement element) {
        return new CElement(element.getValue());
    }

    
    public CElement cast(ZnElement element) {
        return new CElement(element.getValue());
    }
    
    
    public CElement cast(QElement element) {
        return new CElement(element.getValue().doubleValue());
    }

    
    public CElement cast(RElement element) {
        return new CElement(element.getValue());
    }

    
    public CElement cast(CElement element) {
        return element;
    }

    
    public String toString() {
        return "CRing";
    }

    
    public String toVisualString() {
        return "C";
    }
    
    
    public CElement parseString(String string) {
    	try {
    		Complex value = Complex.parseComplex(TextUtils.unparenthesize(string));
        	return new CElement(value);
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
        return CRing.ring;
    }


    public static XMLInputOutput<Module> getXMLInputOutput() {
        return CRing.ring;
    }


    public String getElementTypeName() {
        return "CRing";
    }
    
    
    public static XMLInputOutput<Module> getXMLInput() {
        return CRing.ring;
    }
    

    public int hashCode() {
        return basicHash;
    }

    
    protected int getNumberRingOrder() {
        return 400;
    }
    

    private final static int basicHash = "CRing".hashCode();

    private CRing() { /* not allowed */ }
}
