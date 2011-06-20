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

import java.util.*;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * The ring of polynomials with coefficients in a specified ring.
 * @see org.rubato.math.module.PolynomialElement
 * 
 * @author Gérard Milmeister
 */
public final class PolynomialRing
        extends Ring
        implements PolynomialFreeModule {

    public static PolynomialRing make(Ring coefficientRing, String indeterminate) {
        if (coefficientRing instanceof PolynomialRing) {
            PolynomialRing r = (PolynomialRing)coefficientRing;
            if (!r.hasIndeterminate(indeterminate)) {
                return new PolynomialRing((PolynomialRing)coefficientRing, indeterminate);
            }
            else {
                throw new IllegalArgumentException("Indeterminate "+indeterminate+
                                                   " occurs in "+coefficientRing);
            }
        }
        else {
            return new PolynomialRing(coefficientRing, indeterminate);
        }
    }
    
    
    public String getIndeterminate() {
        return indeterminate;
    }
    
    
    public boolean hasIndeterminate(String indet) {
        if (getIndeterminate().equals(indet)) {
            return true;
        }
        else if (coefficientRing instanceof PolynomialRing) {
            return ((PolynomialRing)coefficientRing).hasIndeterminate(indet);
        }
        else {
            return false;
        }
    }
    
    
    public List<String> getIndeterminates() {
        if (coefficientRing instanceof PolynomialRing) {
            List<String> indeterminates = ((PolynomialRing)coefficientRing).getIndeterminates();
            indeterminates.add(0, getIndeterminate());
            return indeterminates;
        }
        else {
            List<String> indeterminates = new LinkedList<String>();
            indeterminates.add(getIndeterminate());
            return indeterminates;
        }
    }
    
    
    public Ring getCoefficientRing() {
        return coefficientRing;
    }
    
    
    public Ring getBaseRing() {
        return baseRing;
    }
    
    
    public PolynomialElement getZero() {
        return new PolynomialElement(this, new RingElement[] { getCoefficientRing().getZero() } );
    }

    
    public PolynomialElement getOne() {
        return new PolynomialElement(this, new RingElement[] { getCoefficientRing().getOne() } );
    }

    
    public PolynomialFreeModule getNullModule() {
        return PolynomialProperFreeModule.make(this, 0);
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
        if (element instanceof PolynomialElement) {
            return element.getModule().equals(this);
        }
        else {
            return false;
        }
    }

    
    public PolynomialFreeModule getFreeModule(int dimension) {
        return PolynomialProperFreeModule.make(this, dimension);
    }

    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof PolynomialRing) {
            PolynomialRing r = (PolynomialRing)object;
            return (getCoefficientRing().equals(r.getCoefficientRing()) &&
                        getIndeterminate().equals(r.getIndeterminate()));
        }
        else {
            return false;
        }
    }

    
    public int compareTo(Module object) {
        if (object instanceof PolynomialRing) {
            PolynomialRing p = (PolynomialRing)object;
            int c;
            if ((c = getCoefficientRing().compareTo(p.getCoefficientRing())) != 0) {
                return c;
            }
            else {
                return getIndeterminate().compareTo(p.getIndeterminate());
            }
        }
        else {
            return super.compareTo(object);
        }
    }


    public PolynomialElement createElement(List<ModuleElement> elements) {
        if (elements.size() == 1) {
            if (hasElement(elements.get(0))) {
                return (PolynomialElement)elements.get(0);
            }
        }
        RingElement[] coeffs = new RingElement[elements.size()];
        int i = 0;
        for (ModuleElement e : elements) {
            coeffs[i] = (RingElement)getCoefficientRing().cast(e);
            if (coeffs[i] == null) {
                return null;
            }
        }
        return new PolynomialElement(this, coeffs);
    }

    
    public PolynomialElement cast(ModuleElement element) {
        if (this.equals(element.getModule())) {
            return (PolynomialElement)element;
        }
        else if (element instanceof PolynomialElement) {
            PolynomialElement p = (PolynomialElement)element;
            RingElement[] coeffs = p.getCoefficients();
            RingElement[] newCoeffs = new RingElement[coeffs.length];
            Ring ring = getCoefficientRing();
            for (int i = 0; i < coeffs.length; i++) {
                newCoeffs[i] = (RingElement)ring.cast(coeffs[i]);
                if (newCoeffs[i] == null) {
                    return null;
                }                
            }
            return new PolynomialElement(this, newCoeffs);
        }
        else if (element instanceof RingElement) {
            RingElement newCoeff = (RingElement)getCoefficientRing().cast(element);
            if (newCoeff == null) {
                return null;
            }
            else {
                return new PolynomialElement(this, new RingElement[] { newCoeff });
            }
        }
        else {
            return null;
        }
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(40);
        buf.append("PolynomialRing(");
        buf.append(getBaseRing());
        buf.append(")[");
        Iterator<String> iter = getIndeterminates().iterator();
        String in = iter.next();
        buf.append(in);
        while (iter.hasNext()) {
            buf.append(",");
            buf.append(iter.next());
        }
        buf.append("]");
        return buf.toString();
    }

    
    public String toVisualString() {
        Ring baseRing = getBaseRing();
        String s = "";
        if (baseRing instanceof ProductRing) {
            s += "(";
            s += baseRing.toVisualString();
            s += ")";
        }
        else {
            s += baseRing.toVisualString();
        }
        s += "[";
        Iterator<String> iter = getIndeterminates().iterator();
        String in = iter.next();
        s += in;
        while (iter.hasNext()) {
            s += ","+iter.next();
        }
        return s+"]";
    }
    
    
    public PolynomialElement parseString(String string) {
        string = string.trim();
        if (string.equals("0")) {
            return getZero();
        }
        if (string.equals("1")) {
            return getOne();
        }
        
        ArrayList<String> m = parse(TextUtils.unparenthesize(string));
        int[] exp = new int[1];
        ModuleElement[] factor = new ModuleElement[1];
        Vector<ModuleElement> terms = new Vector<ModuleElement>(30);
        terms.setSize(1);
        int maxexp = 0;
        try {
            for (int i = 0; i < m.size(); i++) {
                if (splitTerm(m.get(i), exp, factor)) {
                    if (maxexp < exp[0]) {
                        maxexp = exp[0];
                        terms.setSize(exp[0] + 1);
                    }
                    if (terms.get(exp[0]) != null) {
                        ((RingElement) terms.get(exp[0])).add(factor[0]);
                    } else {
                        terms.set(exp[0], factor[0]);
                    }
                } else {
                    return null;
                }
            }
        }
        catch (DomainException e) {
            return null;
        }
        
        RingElement[] ringElements = new RingElement[terms.size()];
        for (int i = 0; i < ringElements.length; i++) {
            RingElement element = (RingElement)terms.get(i);
            if (element == null) {
                element = getCoefficientRing().getZero();
            }
            ringElements[i] = element;
        }
        
        return new PolynomialElement(this, ringElements);
    }
    
    
    private static ArrayList<String> parse(String s) {
        int pos = 0;
        int lastpos = 0;
        int level = 0;
        ArrayList<String> m = new ArrayList<String>();
        while (pos < s.length()) {
            if (s.charAt(pos) == '(') {
                pos++;
                level++;
            }
            else if (s.charAt(pos) == ')') {
                pos++;
                level--;
            }
            else if (s.charAt(pos) == '+' && level == 0) {
                m.add(s.substring(lastpos, pos));                
                pos++;
                lastpos = pos;
            }
            else {
                pos++;
            }
        }
        m.add(s.substring(lastpos,pos).trim());
        return m;
    }

    
    private boolean splitTerm(String s, int[] exp, ModuleElement[] factor) {
        String[] timesSplit = splitTimes(s);
        if (timesSplit == null) {
            return false;
        }
        
        String[] indSplit = splitInd(timesSplit[1]);
        if (indSplit == null) {
            return false;
        }

        // get coefficient
        factor[0] = getCoefficientRing().parseString(timesSplit[0]);
        if (factor[0] == null) {
            return false;
        }
        
        // check indeterminate
        if (!getIndeterminate().equals(indSplit[0])) {
            return false;
        }
        
        // get exponent
        try {
            exp[0] = Integer.parseInt(indSplit[1]);
        }
        catch (NumberFormatException e) {
            return false;
        }
        if (exp[0] < 0) {
            return false;
        }
        
        return true;
    }
    
    
    private String[] splitTimes(String s) {
        int pos = 0;
        int lastpos = 0;
        int level = 0;
        boolean seenTimes = false;
        String m[] = new String[2];        
        while (pos < s.length()) {
            if (s.charAt(pos) == '(') {
                pos++;
                level++;
            }
            else if (s.charAt(pos) == ')') {
                pos++;
                level--;
            }
            else if (s.charAt(pos) == '*' && level == 0) {
                m[0] = s.substring(lastpos, pos);         
                seenTimes = true;
                pos++;
                lastpos = pos;
            }
            else {
                pos++;
            }
        }
        if (seenTimes) {
            m[1] = s.substring(lastpos,pos).trim();
        }
        else if (s.startsWith(getIndeterminate())) {
            m[0] = "1";
            m[1] = s;
        }
        else {
            m[0] = s.trim();
            m[1] = getIndeterminate()+"^0";
        }
        return m;
    }
    
    
    private static String[] splitInd(String s) {
        String[] strings = s.split("\\^");
        if (strings.length == 2) {
            return strings;
        }
        else if (strings.length == 1) {
            return new String[] { strings[0], "1" };
        }
        else {
            return null;
        }
    }
    
    
    private final static String INDETERMINATE_ATTR = "indeterminate";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULE, getElementTypeName(),
                                 INDETERMINATE_ATTR, indeterminate);
        getCoefficientRing().toXML(writer);
        writer.closeBlock();
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(INDETERMINATE_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), INDETERMINATE_ATTR);
            return null;                
        }
        String indeterminate0 = element.getAttribute(INDETERMINATE_ATTR);
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            Module module = reader.parseModule(childElement);
            if (module == null) {
                return null;
            }
            if (!(module instanceof Ring)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "Ring");
                return null;                    
            }
            Ring ring = (Ring)module;
            return new PolynomialRing(ring, indeterminate0);
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULE);
            return null;
        }
    }
    
    
    private final static XMLInputOutput<Module> xmlIO = PolynomialRing.make(ZRing.ring, "X");
    
    public static XMLInputOutput<Module> getXMLInputOutput() {
        return xmlIO;
    }

    
    public String getElementTypeName() {
        return "PolynomialRing";
    }

    
    public int hashCode() {
        int hashCode = basicHash;
        hashCode ^= coefficientRing.hashCode();
        hashCode ^= indeterminate.hashCode();
        return hashCode;
    }

    
    private final static int basicHash = "PolynomialRing".hashCode();


    private PolynomialRing(Ring coefficientRing, String indeterminate) {
        this.indeterminate = indeterminate;
        this.coefficientRing = coefficientRing;
        this.baseRing = coefficientRing;
    }
    
    
    private PolynomialRing(PolynomialRing coefficientRing, String indeterminate) {
        this.indeterminate = indeterminate;
        this.coefficientRing = coefficientRing;
        this.baseRing = ((PolynomialRing)this.coefficientRing).getBaseRing();            
    }
    
    
    private Ring   coefficientRing;
    private Ring   baseRing;
    private String indeterminate;
}
