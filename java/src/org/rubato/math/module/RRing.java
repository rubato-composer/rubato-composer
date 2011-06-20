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

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The field of real numbers.
 * @see org.rubato.math.module.RElement
 * 
 * @author Gérard Milmeister
 */
public final class RRing extends NumberRing implements RFreeModule {

    /**
     * The unique instance of the ring of reals.
     */
    public static final RRing ring = new RRing();

    public RElement getZero() {
        return new RElement(0);
    }


    public RElement getOne() {
        return new RElement(1);
    }


    public RElement getUnitElement(int i) {
        return getOne();
    }

    
    public Module getNullModule() {
        return RProperFreeModule.nullModule;
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
        return (element instanceof RElement);
    }

    
    public FreeModule getFreeModule(int dimension) {
        return RProperFreeModule.make(dimension);
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


    public RElement createElement(List<ModuleElement> elements) {
        if (!elements.isEmpty()) {
            return (RElement)elements.get(0).cast(this);
        }
        else {
            return null;
        }
    }

    
    public ModuleElement cast(ModuleElement element) {
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
            return element;
        }
        else if (element instanceof CElement) {
            return cast((CElement)element);
        }
        else if (element instanceof DirectSumElement) {
            return element.cast(this);
        }
        else {
            return null;
        }
    }

    
    public RElement cast(ZElement element) {
        return new RElement(element.getValue());
    }

    
    public RElement cast(ZnElement element) {
        return new RElement(element.getValue());
    }
    
    
    public RElement cast(QElement element) {
        return new RElement(element.getValue().doubleValue());
    }

    
    public RElement cast(CElement element) {
        return new RElement(element.getValue().getReal());
    }

    
    public String toString() {
        return "RRing";
    }


    public String toVisualString() {
        return "R";
    }
    
    
    public RingElement parseString(String string) {
    	try {
    		double value = Double.parseDouble(TextUtils.unparenthesize(string));
        	return new RElement(value);
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
        return RRing.ring;
    }
    
    
    public String getElementTypeName() {
        return "RRing";
    }
    

    public static XMLInputOutput<Module> getXMLInputOuput() {
        return RRing.ring;
    }
    

    public int hashCode() {
        return basicHash;
    }
    
    
    protected int getNumberRingOrder() {
        return 300;
    }

    
    private final static int basicHash = "RRing".hashCode();

    private RRing() { /* not allowed */ }
}
