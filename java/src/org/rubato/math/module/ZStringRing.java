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

import org.rubato.math.arith.RingString;
import org.rubato.math.arith.ZString;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The ring of ZString.
 * @see org.rubato.math.module.ZStringElement
 * 
 * @author Gérard Milmeister
 */
public final class ZStringRing extends StringRing implements ZStringFreeModule {

    public static final ZStringRing ring = new ZStringRing();

    public ZStringElement getZero() {
        return new ZStringElement(ZString.getZero());
    }

    
    public ZStringElement getOne() {
        return new ZStringElement(ZString.getOne());
    }

    
    public ZStringProperFreeModule getNullModule() {
        return ZStringProperFreeModule.nullModule;
    }
    
    
    public boolean isField() {
        return false;
    }
    
    
    public boolean isVectorspace() {
        return false;
    }

    
    public ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }

    
    public boolean hasElement(ModuleElement element) {
        return element instanceof ZStringElement;
    }

    
    public ZStringFreeModule getFreeModule(int dimension) {
        return ZStringProperFreeModule.make(dimension);
    }

    
    public ZRing getFactorRing() {
        return ZRing.ring;
    }

    
    public boolean equals(Object object) {
        return this == object;
    }

    
    public ModuleElement cast(ModuleElement element) {
        if (element instanceof ZStringElement) {
            return element;
        }
        else if (element instanceof StringElement) {
            RingString rs = ((StringElement)element).getRingString();
            return new ZStringElement(new ZString(rs));
        }
        else {
            ZElement e = ZRing.ring.cast(element);
            if (e == null) {
                return null;
            }
            else {
                return new ZStringElement(new ZString(e.getValue()));
            }
        }       
    }

    
    public String toString() {
        return "ZStringRing";
    }
    

    public String toVisualString() {
        return "Z-String";
    }

    
    public ZStringElement parseString(String string) {
        try {
            return new ZStringElement(ZString.parseZString(TextUtils.unparenthesize(string)));
        }
        catch (Exception e) {
            return new ZStringElement(string);
        }
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        return ZStringRing.ring;
    }
    
    
    public String getElementTypeName() {
        return "ZStringRing";
    }

    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return ZStringRing.ring;
    }
    

    public int hashCode() {
        return basicHash;
    }

    
    private final static int basicHash = "ZStringRing".hashCode();

    private ZStringRing() { /* not allowed */ }
}
