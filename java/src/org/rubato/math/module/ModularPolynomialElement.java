/*
 * Copyright (C) 2007 Gérard Milmeister
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

import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Elements in a ring of polynomials.
 * @see org.rubato.math.module.PolynomialRing
 * 
 * @author Gérard Milmeister
 */
public final class ModularPolynomialElement 
        extends RingElement
        implements ModularPolynomialFreeElement {

    /**
     * Constructs a modular polynomial in a specified ring with the given coefficents.
     * The array of <code>coefficients</code> contains the coefficient of power <code>n</code>
     * at index <code>n</code>.
     * @param ring the ring of modular polynomials
     * @param coefficients elements of the coefficient ring
     */
    public ModularPolynomialElement(ModularPolynomialRing ring, RingElement ... coefficients) {
        this(ring, new PolynomialElement(ring.getModulusRing(), coefficients));
    }

    
    /**
     * Constructs a modular polynomial in a specified ring from he given polynomial.
     */
    public ModularPolynomialElement(ModularPolynomialRing ring, PolynomialElement polynomial) {
        if (ring.getModulusRing().hasElement(polynomial)) {
            this.ring       = ring;
            this.modulus    = ring.getModulus();
            this.polynomial = polynomial;
            this.one        = ring.getModulusRing().getOne();
            normalize();
        }
        else {
            throw new IllegalArgumentException(polynomial+" is not an element of "+ring.getModulusRing());
        }
    }


    public boolean isOne() {
        return polynomial.isOne();
    }  
    
    
    public boolean isZero() {
        return polynomial.isZero();
    }  
    
    
    public ModularPolynomialElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            return sum((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ModularPolynomialElement sum(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            PolynomialElement p = polynomial.sum(element.polynomial);
            return new ModularPolynomialElement(ring, modulus, p, one);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            add((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            polynomial = polynomial.sum(element.polynomial);
            normalize();
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public ModularPolynomialElement difference(ModuleElement element) 
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            return difference((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ModularPolynomialElement difference(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            PolynomialElement p = polynomial.difference(element.polynomial);
            return new ModularPolynomialElement(ring, modulus, p, one);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            subtract((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void subtract(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            polynomial = polynomial.difference(element.polynomial);
            normalize();
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public ModularPolynomialElement negated() {
        PolynomialElement p = polynomial.negated();
        return new ModularPolynomialElement(ring, modulus, p, one);
    }

    
    public void negate() {
        polynomial = polynomial.negated();
        normalize();
    }
    
    
    public ModularPolynomialElement scaled(RingElement element)
            throws DomainException {
        if (ring.getCoefficientRing().hasElement(element)) {
            return product(element);
        }
        else {
            throw new DomainException(ring.getCoefficientRing(), element.getRing());
        }
    }
    

    public void scale(RingElement element)
            throws DomainException {
        if (ring.getCoefficientRing().hasElement(element)) {
            multiply(element);
        }
        else {
            throw new DomainException(ring.getCoefficientRing(), element.getRing());
        }
    }
    

    public ModularPolynomialElement product(RingElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            return product((ModularPolynomialElement)element);
        }
        else {
            try {
                PolynomialElement p = polynomial.product(element);
                return new ModularPolynomialElement(ring, modulus, p, one);
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
    }

    
    public ModularPolynomialElement product(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            PolynomialElement p = polynomial.product(element.polynomial);
            return new ModularPolynomialElement(ring, modulus, p, one);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof ModularPolynomialElement) {
            multiply((ModularPolynomialElement)element);
        }
        else {
            try {
                polynomial = polynomial.product(element);
                normalize();
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
    }

    
    public void multiply(ModularPolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            polynomial = polynomial.product(element.polynomial);
            normalize();
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public boolean isInvertible() {        
        try {
            PolynomialElement g = polynomial.gcd(modulus);
            return g.getDegree() == 0;
        }
        catch (DomainException e) {
            return false;
        }
        catch (DivisionException e) {
            return false;
        }
    }
    
    
    public ModularPolynomialElement inverse() {
        try {
            PolynomialElement res[] = new PolynomialElement[2];
            PolynomialElement g = polynomial.exgcd(modulus, res);
            if (g.getDegree() == 0) {
                return new ModularPolynomialElement(ring, modulus, res[0], one);
            }
        }
        catch (Exception e) {}
        throw new InverseException("Inverse of "+this+" does not exist");
    }

    
    public void invert() {
        try {
            PolynomialElement res[] = new PolynomialElement[2];
            PolynomialElement g = polynomial.exgcd(modulus, res);
            if (g.getDegree() == 0) {
                polynomial = res[0];
                return;
            }
        }
        catch (Exception e) {}
        throw new InverseException("Inverse of "+this+" does not exist");
    }
    

    public ModularPolynomialElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ModularPolynomialElement) {
            return quotient((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }

    
    public ModularPolynomialElement quotient(ModularPolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().equals(element.getRing())) {
            try {
                ModularPolynomialElement p = element.inverse();
                return product(p);
            }
            catch (InverseException ex) {}
            throw new DivisionException(this, element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    
    
    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof ModularPolynomialElement) {
            divide((ModularPolynomialElement)element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    

    public void divide(ModularPolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().equals(element.getRing())) {
            try {
                PolynomialElement res[] = new PolynomialElement[2];
                PolynomialElement g = element.polynomial.exgcd(modulus, res);
                if (g.getDegree() == 0) {
                    polynomial = polynomial.product(res[0]);
                    normalize();
                    return;
                }
            }
            catch (Exception e) {}
            throw new DivisionException(this, element);
        }
        throw new DomainException(getRing(), element.getRing());
    }


    public boolean divides(RingElement element) {
        if (element instanceof ModularPolynomialElement) {
            return isInvertible();
        }
        else {
            return false;
        }
    }

    
    public RingElement evaluate(RingElement element)
            throws DomainException {
        if (ring.getCoefficientRing().hasElement(element)) {
            return polynomial.evaluate(element);
        }
        else {
            throw new DomainException(ring.getCoefficientRing(), element.getRing());
        }
    }
    
    
    public ModularPolynomialRing getModule() {
        return ring;
    }

    
    public ModularPolynomialRing getRing() {
        return ring;
    }

    
    public RingElement[] getCoefficients() {
        return polynomial.getCoefficients();
    }
    
    
    public RingElement getCoefficient(int power) {
        return polynomial.getCoefficient(power);
    }
      

    public RingElement getLeadingCoefficient() {
        return polynomial.getLeadingCoefficient();
    }
    

    public int getDegree() {
        return polynomial.getDegree();
    }

    
    public Ring getCoefficientRing() {
        return getRing().getCoefficientRing();
    }
    
    
    public String getIndeterminate() {
        return getRing().getIndeterminate();
    }
    
    
    public PolynomialElement getModulus() {
        return ring.getModulus();
    }
    
    
    public ModularPolynomialFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return ModularPolynomialProperFreeElement.make(getRing(), new ModularPolynomialElement[0]);
        }
        else {
            ModularPolynomialElement[] values = new ModularPolynomialElement[n];
            values[0] = this;
            for (int i = 1; i < n; i++) {
                values[i] = getRing().getZero();
            }
            return ModularPolynomialProperFreeElement.make(getRing(), values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ModularPolynomialElement) {
            ModularPolynomialElement p = (ModularPolynomialElement)object;
            return ring.equals(p.ring) && polynomial.equals(p.polynomial); 
        }
        else {
            return false;
        }
    }

    
    public int compareTo(ModuleElement object) {
        if (object instanceof ModularPolynomialElement) {
            ModularPolynomialElement p = (ModularPolynomialElement)object;
            int c = ring.compareTo(p.ring);
            if (c == 0) {
                c = polynomial.compareTo(p.polynomial);
            }
            return c;
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public ModularPolynomialElement clone() {
        return new ModularPolynomialElement(ring, modulus, polynomial.clone(),one); 
    }
    
    
    public String stringRep(boolean ... parens) {
        return polynomial.stringRep(parens);
    }

    
    public String toString() {
        return "ModularPolynomial["+polynomial.stringRep()+","+modulus.stringRep()+"]";
    }
    
    
    public double[] fold(ModuleElement[] elements) {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName());        ring.toXML(writer);
        ring.toXML(writer);
        polynomial.toXML(writer);
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        element = XMLReader.getChild(element, MODULEELEMENT);
        if (element == null) {
            reader.setError("Type %%1 is missing child of type %%1.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
        Module m = reader.parseModule(element);
        if (m != null && m instanceof ModularPolynomialRing) {
            element = XMLReader.getNextSibling(element, MODULEELEMENT);
            ModuleElement e = reader.parseModuleElement(element);
            if (e != null && e instanceof PolynomialElement) {
                try {
                    return new ModularPolynomialElement((ModularPolynomialRing)m, (PolynomialElement)e);                    
                }
                catch (IllegalArgumentException ex) {
                    reader.setError(ex.getMessage());
                }
            }
            else {
                reader.setError("Type %%1 is missing child of type %%1.", getElementTypeName(), "ModularPolynomialRing");
            }
        }
        else {
            reader.setError("Type %%1 is missing child of type %%1.", getElementTypeName(), "PolynomialElement");
        }
        return null;
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO =
        new ModularPolynomialElement(ModularPolynomialRing.make(PolynomialRing.make(QRing.ring,"X").getOne()), new QElement(0));
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "ModularPolynomialElement";
    }

    
    public int hashCode() {
        int hashCode = basicHash;
        hashCode ^= polynomial.hashCode();
        hashCode ^= modulus.hashCode();
        return hashCode;
    }
    
    
    private void normalize() {
        try {
            polynomial = polynomial.rem(modulus);
        }
        catch (Exception e) {}
    }

    
    private ModularPolynomialElement(ModularPolynomialRing ring, PolynomialElement polynomial, PolynomialElement modulus, PolynomialElement one) {
        this.ring       = ring;
        this.modulus    = modulus;
        this.polynomial = polynomial;
        this.one        = one;
        normalize();
    }


    private ModularPolynomialRing ring;
    private PolynomialElement     modulus;

    private PolynomialElement     polynomial;
    private PolynomialElement     one;

    private final static int basicHash = "ModularPolynomialElement".hashCode();

    public static void main(String[] args) {
        PolynomialRing pr = PolynomialRing.make(QRing.ring, "X");
        ModularPolynomialRing mpr = ModularPolynomialRing.make(pr.parseString("X^2+1"));
        ModularPolynomialElement mp = mpr.parseString("3*X^2+X+(-1)");
        System.out.println(mp);
    }
}
