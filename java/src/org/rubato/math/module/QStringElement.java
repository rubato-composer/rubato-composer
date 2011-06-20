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

import org.rubato.math.arith.*;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in the ring of strings with rational factors.
 * @see org.rubato.math.module.QStringRing
 * 
 * @author Gérard Milmeister
 */
public final class QStringElement
		extends StringElement
		implements QStringFreeElement {

    /**
     * Constructs a QStringElement from a QString <code>value</code>.
     */
    public QStringElement(QString value) {
        this.value = value;
    }


    /**
     * Constructs a QStringElement from a simple string <code>value</code>.
     * The result is a QStringElement of the form 1/1*value.
     */
    public QStringElement(String value) {
        this.value = new QString(value);
    }

    
    /**
     * Constructs a QStringElement from the array of objects <code>objs</code>.
     * @param objs an array of objects where strings alternate with
     *             rationals, the array should therefore be of even
     *             length
     */
    public QStringElement(Object ... objs) {
        int len = objs.length/2;
        String[] words = new String[len];
        Rational[] factors = new Rational[len];
        for (int i = 0; i < len*2; i += 2) {
            if (objs[i] instanceof String && objs[i+1] instanceof Rational) {
                words[i/2] = (String)objs[i];
                factors[i/2] = (Rational)objs[i+1];
            }
            else {
                words[i/2] = "";
                factors[i/2] = Rational.getZero();
            }
        }
        this.value = new QString(words, factors);
    }

    
    public boolean isOne() {
        return value.equals(QString.getOne());
    }
       

    public boolean isZero() {
        return value.equals(QString.getZero());
    }
       

    public ModuleElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            return sum((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getModule());
        }
    }


    public final QStringElement sum(QStringElement element) {
        return new QStringElement((QString)getValue().sum(element.getValue()));        
    }


    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            add((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getModule());
        }
    }
    
    
    public final void add(QStringElement element) {
        value.add(element.getValue());        
    }


    public ModuleElement difference(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            return difference((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getModule());
        }
    }
        

    public final QStringElement difference(QStringElement element) {
        return new QStringElement((QString)getValue().difference(element.getValue()));
    }
        

    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            subtract((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getModule());
        }
    }


    public final void subtract(QStringElement element) {
        value.subtract(element.getValue());
    }


    public ModuleElement negated() {
        return new QStringElement((QString)getValue().negated());
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
        if (element instanceof QStringElement) {
            return product((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getRing());
        }
    }

    
    public final QStringElement product(QStringElement element) {
        return new QStringElement((QString)getValue().product(element.getValue()));
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof QStringElement) {
            multiply((QStringElement)element);
        }
        else {
            throw new DomainException(QStringRing.ring, element.getRing());
        }
    }

    
    public final void multiply(QStringElement element) {
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
        if (element instanceof QStringElement) {
            // TODO: implement division where possible
            throw new DivisionException(this, element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }


    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof QStringElement) {
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
        return RStringRing.ring;
    }


    public QString getValue() {
        return value;
    }


    public RingString getRingString() {
        return getValue();
    }


    public FreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return QStringProperFreeElement.make(new QString[0]);
        }
        else {
            QString[] values = new QString[n];
            values[0] = new QString(value);
            for (int i = 1; i < n; i++) {
                values[i] = QString.getZero();
            }
            return QStringProperFreeElement.make(values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof QStringElement) {
            return getValue().equals(((QStringElement)object).getValue());
        }
        else {
            return false;
        }
    }


    public int compareTo(ModuleElement object) {
        if (object instanceof QStringElement) {
            return getValue().compareTo(((QStringElement)object).getValue());
        }
        else {
            return super.compareTo(object);
        }
    }


    public QStringElement clone() {
        return new QStringElement((QString)getValue().clone());
    }


    public String toString() {
        return "QStringElement["+value+"]";
    }

    
    public String stringRep(boolean ... parens) {
        if (parens.length > 0) {
            return TextUtils.parenthesize(getValue().stringRep());
        }
        else {
            return getValue().stringRep();
        }
    }

    
    public String getString() {
        return getValue().getString();
    }

    
    public HashMap<String,RingElement> getTerms() {
        HashMap<String,RingElement> map = new HashMap<String,RingElement>();
        Set<String> strings = value.getStrings();
        for (String s : strings) {
            map.put(s, new QElement(((Rational)value.getFactorForString(s))));
        }
        return map;
    }
    

    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    private static final String WORD        = "Word";
    private static final String FACTOR_ATTR = "factor";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());
        for (String word : value.getStrings()) {
            Rational factor = ((Rational)value.getFactorForString(word));
            writer.openInline(WORD, FACTOR_ATTR, factor);
            writer.text(word);
            writer.closeInline();                
        }
        writer.closeBlock();
    }
    
    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, WORD);
        if (childElement != null) {
            LinkedList<Rational> factors = new LinkedList<Rational>();
            LinkedList<String> words = new LinkedList<String>();
            String factor;
            Rational r;

            if (!childElement.hasAttribute(FACTOR_ATTR)) {
                reader.setError("Element <%1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                return null;
            }                        
            factor = childElement.getAttribute(FACTOR_ATTR);
            try {
                r = Rational.parseRational(factor);
            }
            catch (NumberFormatException e) {
                reader.setError("Attribute %%1 must be a rational number.", FACTOR_ATTR);
                return null;                
            }
            factors.add(r);
            words.add(childElement.getTextContent());
            Element next = XMLReader.getNextSibling(childElement, WORD);
            while (next != null) {
                if (!next.hasAttribute(FACTOR_ATTR)) {
                    reader.setError("Element <$1> is missing attribute %%2.", WORD, FACTOR_ATTR);
                    return null;
                }            
                factor = childElement.getAttribute(FACTOR_ATTR);
                try {
                    r = Rational.parseRational(factor);
                }
                catch (NumberFormatException e) {
                    reader.setError("Attribute %%1 must be a rational number.", FACTOR_ATTR);
                    return null;                
                }
                factors.add(r);
                words.add(next.getTextContent());
                next = XMLReader.getNextSibling(next, WORD);
            }
            Rational[] factorArray = new Rational[factors.size()];
            String[] wordArray = new String[factors.size()];
            int i = 0;
            Iterator<Rational> fiter = factors.iterator();
            Iterator<String> witer = words.iterator();
            while (fiter.hasNext()) {
                factorArray[i] = fiter.next();
                wordArray[i] = witer.next();
                i++;
            }
            QString qstring = new QString(wordArray, factorArray);
            return new QStringElement(qstring);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), WORD);
            return null;            
        }        
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO = new QStringElement("");
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "QStringElement";
    }
    
    
    public int hashCode() {
        return value.hashCode();
    }
    
    
    private QString value = null;
}
