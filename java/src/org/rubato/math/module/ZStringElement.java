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
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.*;

import org.rubato.math.arith.RingString;
import org.rubato.math.arith.ZString;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements of the ring of strings with integer factors.
 * @see org.rubato.math.module.ZStringRing
 * 
 * @author Gérard Milmeister
 */
public final class ZStringElement
		extends StringElement
		implements ZStringFreeElement {

    /**
     * Constructs a ZStringElement from an ordinary String <code>string</code>.
     * The result is a ZStringElement of the form 1*value.
     */
    public ZStringElement(String string) {
        simpleString = string;
    }


    /**
     * Constructs a ZStringElement from a ZString <code>value</code>.
     */
    public ZStringElement(ZString value) {
        this.value = value;
    }


    /**
     * Constructs a ZStringElement from the array of objects <code>objs</code>.
     * @param objs an array of objects where strings alternate with
     *             integers, the array should therefore be of even
     *             length
     */
    public ZStringElement(Object ... objs) {
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
        this.value = new ZString(words, factors);
    }
    

    public boolean isOne() {
        if (value != null) {
            return value.equals(ZString.getOne());
        }
        else {
            return false;
        }
    }
       

    public boolean isZero() {
        if (value != null) {
            return value.equals(ZString.getZero());
        }
        else {
            return false;
        }
    }
    
    
    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            return sum((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public ZStringElement sum(ZStringElement element) {
        return new ZStringElement((ZString)getValue().sum(element.getValue()));        
    }


    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            add((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void add(ZStringElement element) {
        zstringify();
        value.add(element.getValue());        
    }


    public ModuleElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            return difference((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
        

    public ZStringElement difference(ZStringElement element) {
        return new ZStringElement((ZString)getValue().difference(element.getValue()));
    }
        

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            subtract((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void subtract(ZStringElement element) {
        zstringify();
        value.subtract(element.getValue());
    }


    public ModuleElement negated() {
        return new ZStringElement((ZString)getValue().negated());
    }

    
    public void negate() {
        zstringify();
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
            return product((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public ZStringElement product(ZStringElement element) {
        return new ZStringElement((ZString)getValue().product(element.getValue()));
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof ZStringElement) {
            multiply((ZStringElement)element);
        }
        else {
            throw new DomainException(this.getModule().getRing(), element.getRing());
        }
    }

    
    public void multiply(ZStringElement element) {
        value.multiply(element.getValue());
    }

    
    public RingElement inverse() {
        throw new InverseException("Inverse of "+this+" does not exist.");
    }

    
    public void invert() {
        throw new InverseException("Inverse of "+this+" does not exist.");
    }


    public RingElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZStringElement) {
            // TODO: implement division where possible
            throw new DivisionException(this, element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    
    
    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ZStringElement) {
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
        return ZStringRing.ring;
    }


    public ZString getValue() {
        if (simpleString != null) {
            return new ZString(simpleString);
        }
        else {
            return value;
        }
    }


    public RingString getRingString() {
        return getValue();
    }


    public FreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return ZStringProperFreeElement.make(new ZString[0]);
        }
        else {
            ZString[] values = new ZString[n];
            values[0] = new ZString(value);
            for (int i = 1; i < n; i++) {
                values[i] = ZString.getZero();
            }
            return ZStringProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ZStringElement) {
            ZStringElement element = (ZStringElement)object;
            if (simpleString != null && element.simpleString != null) {
                return simpleString.equals(element.simpleString);
            }
            else {
                return getValue().equals(element.getValue());
            }
        }
        else {
            return false;
        }
    }


    public int compareTo(ModuleElement object) {
        if (object instanceof ZStringElement) {
	        ZStringElement element = (ZStringElement)object;
	        if (simpleString != null && element.simpleString != null) {
	            return simpleString.compareTo(element.simpleString);
	        }
	        else {
	            return getValue().compareTo(element.getValue());
	        }
        }
        else {
            return super.compareTo(object);
        }
    }


    public ZStringElement clone() {
        if (simpleString != null) {
            return new ZStringElement(simpleString);
        }
        else {
            return new ZStringElement((ZString)getValue().clone());
        }
    }


    public String toString() {
        if (isSimple()) {
            return "ZStringElement[[1*\""+simpleString+"\"]]";
        }
        else {
            return "ZStringElement["+value+"]";
        }
    }

    
    public String stringRep(boolean ... parens) {
    	if (isSimple()) {
    		return "\""+simpleString+"\"";
    	}
    	else {
    		return getValue().stringRep();
    	}
    }

    
    public String getString() {
        if (isSimple()) {
            return simpleString;
        }
        else {
            return getValue().getString();
        }
    }

    
    public HashMap<String,RingElement> getTerms() {
        HashMap<String,RingElement> map = new HashMap<String,RingElement>();
        if (value != null) {
            Set<String> strings = value.getStrings();
            for (String s : strings) {
                map.put(s, new ZElement(((Integer)value.getFactorForString(s))));
            }
        }
        else {
            map.put(simpleString, new ZElement(1));
        }
        return map;
    }
    

    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    private static final String WORD        = "Word";
    private static final String FACTOR_ATTR = "factor";
    private static final String KIND_ATTR   = "kind";
    
    
    public void toXML(XMLWriter writer) {
        if (simpleString == null) {
            writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
            for (String word : value.getStrings()) {
                int factor = ((Integer)value.getFactorForString(word)).intValue();
                writer.openInline(WORD, FACTOR_ATTR, factor);
                writer.text(word);
                writer.closeInline();                
            }
            writer.closeBlock();
        }
        else {
            writer.openInline(MODULEELEMENT,
                              TYPE_ATTR, getElementTypeName(),
                              KIND_ATTR, "simple");
            writer.text(simpleString);
            writer.closeInline();
        }
    }
    
    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, WORD);
        if (childElement != null) {
            LinkedList<Integer> factors = new LinkedList<Integer>();
            LinkedList<String> words = new LinkedList<String>();
            String factor;
            Integer i;

            if (!childElement.hasAttribute(FACTOR_ATTR)) {
                reader.setError("Element <%1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                return null;
            }                        
            factor = childElement.getAttribute(FACTOR_ATTR);
            try {
                i = Integer.valueOf(Integer.parseInt(factor));
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%2 must be an integer.", FACTOR_ATTR);
                return null;                
            }
            factors.add(i);
            words.add(childElement.getTextContent());
            Element next = XMLReader.getNextSibling(childElement, WORD);
            while (next != null) {
                if (!next.hasAttribute(FACTOR_ATTR)) {
                    reader.setError("Element <%1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                    return null;
                }            
                factor = childElement.getAttribute(FACTOR_ATTR);
                try {
                    i = Integer.valueOf(Integer.parseInt(factor));
                }
                catch (NumberFormatException e) {
                    reader.setError("Attribute %%1 must be an integer.", FACTOR_ATTR);
                    return null;                
                }
                factors.add(i);
                words.add(next.getTextContent());
                next = XMLReader.getNextSibling(next, WORD);
            }
            int[] factorArray = new int[factors.size()];
            String[] wordArray = new String[factors.size()];
            int j = 0;
            Iterator<Integer> fiter = factors.iterator();
            Iterator<String> witer = words.iterator();
            while (fiter.hasNext()) {
                factorArray[j] = fiter.next();
                wordArray[j] = witer.next();
                j++;
            }
            ZString zstring = new ZString(wordArray, factorArray);
            return new ZStringElement(zstring);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), WORD);
            return null;            
        }        
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new ZStringElement("");
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ZStringElement";
    }

    
    public int hashCode() {
        return value.hashCode();
    }
    
    
    private boolean isSimple() {
        return (simpleString != null);
    }
    
    
    private void zstringify() {
        if (simpleString != null) {
            value = new ZString(simpleString);
        }
        simpleString = null;
    }


    private ZString value = null;
    private String  simpleString = null;
}
