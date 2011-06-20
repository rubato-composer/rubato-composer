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
import static org.rubato.xml.XMLConstants.MODULUS_ATTR;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import org.rubato.math.arith.RingString;
import org.rubato.math.arith.ZnString;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The ring of ZnString.
 * @see org.rubato.math.module.ZnStringElement
 * 
 * @author Gérard Milmeister
 */
public final class ZnStringRing extends StringRing implements ZnStringFreeModule {
    
    public static ZnStringRing make(int modulus) {
        assert(modulus > 1);
        return new ZnStringRing(modulus);
    }
    

    public ZnStringElement getZero() {
        return new ZnStringElement(ZnString.getZero(modulus));
    }

    
    public ZnStringElement getOne() {
        return new ZnStringElement(ZnString.getOne(modulus));
    }

    
    public ZnStringProperFreeModule getNullModule() {
        return (ZnStringProperFreeModule)ZnStringProperFreeModule.make(0, modulus);
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
        return (element instanceof ZnStringElement &&
                ((ZnStringElement)element).getModulus() == modulus);
    }

    
    public FreeModule getFreeModule(int dimension) {
        return ZnStringProperFreeModule.make(dimension, modulus);
    }

    
    public ZnRing getFactorRing() {
        return ZnRing.make(getModulus());
    }

    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        else if (object instanceof ZnStringRing) {
            return ((ZnStringRing)object).getModulus() == modulus;
        }
        else {
            return false;
        }
    }

    
    public ModuleElement cast(ModuleElement element) {
        if (element instanceof StringElement) {
            RingString rs = ((StringElement)element).getRingString();
            return new ZnStringElement(new ZnString(rs, modulus));
        }
        else {
            ZElement e = ZRing.ring.cast(element);
            if (e == null) {
                return null;
            }
            else {
                return new ZnStringElement(new ZnString(e.getValue(), getModulus()));
            }
        }       
    }

    
    public int getModulus() {
        return modulus;
    }

    
    public String toString() {
        return "ZnStringRing("+getModulus()+")";
    }

    
    public String toVisualString() {
        return "Z_"+getModulus()+"-String";
    }

    
    public ZnStringElement parseString(String string) {
        try {
            return new ZnStringElement(ZnString.parseZnString(TextUtils.unparenthesize(string), getModulus()));
        }
        catch (Exception e) {
            return null;
        }
    }

    
    public void toXML(XMLWriter writer) {
        writer.emptyWithType(MODULE, getElementTypeName(), MODULUS_ATTR, getModulus());
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        int modulus0 = XMLReader.getIntAttribute(element, MODULUS_ATTR, 2, Integer.MAX_VALUE, 2);
        return new ZnStringRing(modulus0);
    }
    
    
    public String getElementTypeName() {
        return "ZnStringRing";
    }

    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return new ZnStringRing(2);
    }
    

    public int hashCode() {
        return 37*basicHash + modulus;
    }

    
    private ZnStringRing(int modulus) {
        this.modulus = modulus;
    }

    
    private final static int basicHash = "ZStringRing".hashCode();

    private int modulus;
}
