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

import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.MODULUS_ATTR;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.*;

import org.rubato.math.arith.RingString;
import org.rubato.math.arith.ZnString;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements of the ring of strings with integer mod <i>n</i> factors.
 * @see org.rubato.math.module.ZnStringRing
 * 
 * @author Gérard Milmeister
 */
public final class ZnStringElement
		extends StringElement
		implements ZnStringFreeElement {

    /**
     * Constructs a ZnStringElement from a <code>string</code> mod <code>modulus</code>.
     * The result is a ZnStringElement of the form 1*value.
     */
    public ZnStringElement(String string, int modulus) {
        this.value = new ZnString(string, modulus);
        this.modulus = modulus;
    }

    
    /**
     * Constructs a ZnStringElement from a ZnString <code>value</code>.
     */
    public ZnStringElement(ZnString value) {
        this.value = value;
        this.modulus = value.getModulus();
    }


    /**
     * Constructs a ZnStringElement from the array of objects <code>objs</code>.
     * @param objs an array of objects where strings alternate with
     *             integers, the array should therefore be of even
     *             length
     */
    public ZnStringElement(int modulus, Object ... objs) {
        int len = objs.length/2;
        String[] words = new String[len];
        int[] factors = new int[len];
        for (int i = 0; i < len*2; i += 2) {
            if (objs[i] instanceof String && objs[i+1] instanceof Integer) {
                words[i/2] = (String)objs[i];
                factors[i/2] = (Integer)objs[i+1];
            }
            else {
                words[i/2] = "";
                factors[i/2] = 0;
            }
        }
        this.value = new ZnString(words, factors, modulus);
    }
    

    public boolean isOne() {
        return value.equals(ZnString.getOne(getModulus()));
    }
       

    public boolean isZero() {
        return value.equals(ZnString.getZero(getModulus()));
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            return sum((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    

    public ZnStringElement sum(ZnStringElement element) {
        return new ZnStringElement((ZnString)value.sum(element.getValue()));        
    }

    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            add((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ZnStringElement element) {
        value.add(element.getValue());
    }

    
    public ModuleElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            return difference((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ZnStringElement difference(ZnStringElement element) {
        return new ZnStringElement((ZnString)value.difference(element.getValue()));
    }

    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            subtract((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void subtract(ZnStringElement element) {
        value.subtract(element.getValue());
    }


    public ModuleElement negated() {
        return new ZnStringElement((ZnString)value.negated());
    }

    
    public void negate() {
        value.negate();
    }

    
    public ModuleElement scaled(RingElement element)
            throws DomainException {
        return product(element);
    }
    

    public void scale(RingElement element)
            throws DomainException {
        multiply(element);
    }
    

    public RingElement product(RingElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            return product((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public ZnStringElement product(ZnStringElement element) {
        return new ZnStringElement((ZnString)getValue().product(element.getValue()));
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof ZnStringElement) {
            multiply((ZnStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void multiply(ZnStringElement element) {
        value.multiply(element.getValue());
    }

    
    public RingElement inverse() {
        throw new InverseException("Inverse of "+this+" does not exist");
    }

    
    public void invert() {
        throw new InverseException("Inverse of "+this+" does not exist");
    }
    

    public RingElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZnStringElement) {
            // TODO: implement division where possible
            throw new DivisionException(this, element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }


    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZnStringElement) {
            // TODO: implement division where possible
            throw new DivisionException(this, element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }


    public boolean divides(RingElement element) {
        // TODO: implement division where possible
        return false;
    }


    public Module getModule() {
        if (module == null) {
            module = ZnStringRing.make(modulus);
        }
        return module;
    }

    
    public ZnString getValue() {
        return value;
    }
    

    public RingString getRingString() {
        return value;
    }

    
    public FreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return ZnStringProperFreeElement.make(new ZnString[0], modulus);
        }
        else {
            ZnString[] values = new ZnString[n];
            values[0] = value;
            for (int i = 1; i < n; i++) {
                values[i] = ZnString.getZero(modulus);
            }
            return ZnStringProperFreeElement.make(values, modulus);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ZnStringElement) {
            return (value.equals(((ZnStringElement)object).getValue()) &&
                    	value.getModulus() == ((ZnStringElement)object).getModulus());
        }
        else {
            return false;
        }
    }


    public int compareTo(ModuleElement object) {
        if (object instanceof ZnStringElement) {
            ZnStringElement element = (ZnStringElement)object;
            return value.compareTo(element.getValue());
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ZnStringElement clone() {
        return new ZnStringElement((ZnString)value.clone());
    }

    
    public int getModulus() {
        return modulus;
    }

    
    public String toString() {
        return "ZnStringElement("+getModulus()+")["+getValue()+"]";
    }

    
    public String stringRep(boolean ... parens) {
    	return value.stringRep();
    }
    
    
    public HashMap<String,RingElement> getTerms() {
        HashMap<String,RingElement> map = new HashMap<String,RingElement>();
        Set<String> strings = value.getStrings();
        for (String s : strings) {
            map.put(s, new ZElement(((Integer)value.getFactorForString(s))));
        }
        return map;
    }
    

    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    private final static String WORD        = "Word";
    private final static String FACTOR_ATTR = "factor";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName(),
                                 MODULUS_ATTR, getModulus());
        for (String word : value.getStrings()) {
            int factor = ((Integer)value.getFactorForString(word)).intValue();
            writer.openInline(WORD, FACTOR_ATTR, factor);
            writer.text(word);
            writer.closeInline();                
        }
        writer.closeBlock();
    }    
    
    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(MODULUS_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), MODULUS_ATTR);
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

        Element childElement = XMLReader.getChild(element, WORD);
        if (childElement != null) {
            LinkedList<Integer> factors = new LinkedList<Integer>();
            LinkedList<String> words = new LinkedList<String>();
            String factor;
            Integer integer;

            if (!childElement.hasAttribute(FACTOR_ATTR)) {
                reader.setError("Element <%1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                return null;
            }                        
            factor = childElement.getAttribute(FACTOR_ATTR);
            try {
                integer = Integer.valueOf(Integer.parseInt(factor));
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 must be an integer.", FACTOR_ATTR);
                return null;                
            }
            factors.add(integer);
            words.add(childElement.getTextContent());
            Element next = XMLReader.getNextSibling(childElement, WORD);
            while (next != null) {
                if (!next.hasAttribute(FACTOR_ATTR)) {
                    reader.setError("Element <%1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                    return null;
                }            
                factor = childElement.getAttribute(FACTOR_ATTR);
                try {
                    integer = Integer.valueOf(Integer.parseInt(factor));
                }
                catch (NumberFormatException e) {
                    reader.setError("Attribute %%1 must be a real number.", FACTOR_ATTR);
                    return null;                
                }
                factors.add(integer);
                words.add(next.getTextContent());
                next = XMLReader.getNextSibling(next, WORD);
            }
            int[] factorArray = new int[factors.size()];
            String[] wordArray = new String[factors.size()];
            int i = 0;
            Iterator<Integer> fiter = factors.iterator();
            Iterator<String> witer = words.iterator();
            while (fiter.hasNext()) {
                factorArray[i] = fiter.next().intValue();
                wordArray[i] = witer.next();
                i++;
            }
            ZnString znstring = new ZnString(wordArray, factorArray, mod);
            return new ZnStringElement(znstring);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), WORD);
            return null;            
        }        
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new ZnStringElement("", 2);
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ZnStringElement";
    }
    
    
    public int hashCode() {
        return value.hashCode();
    }
     
   
    private ZnString value;
    private int      modulus;
    private Module   module = null;
}
