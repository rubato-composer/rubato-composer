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

import java.util.LinkedList;

import org.rubato.util.TextUtils;
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
public final class PolynomialElement 
        extends RingElement
        implements PolynomialFreeElement {

    /**
     * Constructs a polynomial in a specified ring with given coefficents.
     * The array of <code>coefficients</code> contains the coefficient of power <code>n</code>
     * at index <code>n</code>.
     * @param ring the ring of polynomials
     * @param coefficients elements of the coefficient ring
     */
    public PolynomialElement(PolynomialRing ring, RingElement ... coefficients) {
        assert(coefficients.length > 0);
        setPolynomialRing(ring);
        Ring coefficientRing = ring.getCoefficientRing();
        for (int i = 0; i < coefficients.length; i++) {
            if (!coefficientRing.hasElement(coefficients[i])) {
                throw new IllegalArgumentException(coefficients[i]+" is not a coefficient of "+ring);
            }
        }
        setCoefficients(coefficients);
        normalize();
    }

    
    /**
     * Constructs a polynomial with given coefficents.
     * The array of <code>coefficients</code> contains the coefficient of power <code>n</code>
     * at index <code>n</code>.
     * The polynomial ring is inferred from the coefficients, which must all
     * be elements of the the same ring.
     * @param coefficients elements of the coefficient ring
     */
    public PolynomialElement(String indeterminate, RingElement ... coefficients) {
        assert(coefficients.length > 0);
        Ring coefficientRing = coefficients[0].getRing();
        setPolynomialRing(PolynomialRing.make(coefficientRing, indeterminate));
        for (int i = 0; i < coefficients.length; i++) {
            if (!coefficientRing.hasElement(coefficients[i])) {
                throw new IllegalArgumentException(coefficients[i]+" is not a coefficient of "+ring);
            }
        }
        setCoefficients(coefficients);
        normalize();
    }


    public boolean isOne() {
        return (coefficients.length == 1 && coefficients[0].isOne());
    }  
    
    
    public boolean isZero() {
        return (coefficients.length == 1 && coefficients[0].isZero());
    }  
    
    
    public PolynomialElement sum(ModuleElement element)
            throws DomainException {
        if (element instanceof PolynomialElement) {
            return sum((PolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public PolynomialElement sum(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            try {
                RingElement[] otherCoefficients = element.getCoefficients();
                int len = getCoefficients().length;
                int otherLen = otherCoefficients.length;
                int newLen = Math.max(len, otherLen);
                RingElement[] newCoefficients = new RingElement[newLen];
                for (int i = 0; i < len; i++) {
                    newCoefficients[i] = coefficients[i].clone();
                }
                if (newLen > len) {
                    for (int i = len; i < newLen; i++) {
                        newCoefficients[i] = otherCoefficients[i].clone();
                    }
                }
                for (int i = 0; i < Math.min(len, otherLen); i++) {
                    newCoefficients[i].add(otherCoefficients[i]);
                }
                PolynomialElement res = new PolynomialElement();
                res.setPolynomialRing(ring);
                res.setCoefficients(newCoefficients);
                res.normalize();
                return res;
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void add(ModuleElement element)
            throws DomainException {
        if (element instanceof PolynomialElement) {
            add((PolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void add(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            try {
                RingElement[] otherCoefficients = element.getCoefficients();
                int len = getCoefficients().length;
                int otherLen = otherCoefficients.length;
                int newLen = Math.max(len, otherLen);
                RingElement[] newCoefficients;
                if (newLen > len) {
                    newCoefficients = new RingElement[newLen];
                    for (int i = 0; i < len; i++) {
                        newCoefficients[i] = coefficients[i];
                    }
                    for (int i = len; i < newLen; i++) {
                        newCoefficients[i] = otherCoefficients[i];
                    }
                }
                else {
                    newCoefficients = coefficients;
                }
                for (int i = 0; i < Math.min(len, otherLen); i++) {
                    newCoefficients[i].add(otherCoefficients[i]);
                }
                coefficients = newCoefficients;
                normalize();
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public PolynomialElement difference(ModuleElement element) 
            throws DomainException {
        if (element instanceof PolynomialElement) {
            return difference((PolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public PolynomialElement difference(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            try {
                RingElement[] otherCoefficients = element.getCoefficients();
                int len = getCoefficients().length;
                int otherLen = otherCoefficients.length;
                int newLen = Math.max(len, otherLen);
                RingElement[] newCoefficients = new RingElement[newLen];
                for (int i = 0; i < len; i++) {
                    newCoefficients[i] = coefficients[i].clone();
                }
                if (newLen > len) {
                    for (int i = len; i < newLen; i++) {
                        newCoefficients[i] = otherCoefficients[i].clone();
                        newCoefficients[i].negate();
                    }
                }
                for (int i = 0; i < Math.min(len, otherLen); i++) {
                    newCoefficients[i].subtract(otherCoefficients[i]);
                }
                PolynomialElement res = new PolynomialElement();
                res.setPolynomialRing(ring);
                res.setCoefficients(newCoefficients);
                res.normalize();
                return res;
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }
    
    
    public void subtract(ModuleElement element)
            throws DomainException {
        if (element instanceof PolynomialElement) {
            subtract((PolynomialElement)element);
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public void subtract(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            try {
                RingElement[] otherCoefficients = element.getCoefficients();
                int len = getCoefficients().length;
                int otherLen = otherCoefficients.length;
                int newLen = Math.max(len, otherLen);
                RingElement[] newCoefficients;
                if (newLen > len) {
                    newCoefficients = new RingElement[newLen];
                    for (int i = 0; i < len; i++) {
                        newCoefficients[i] = coefficients[i];
                    }
                    for (int i = len; i < newLen; i++) {
                        newCoefficients[i] = (RingElement)otherCoefficients[i].negated();
                    }
                }
                else {
                    newCoefficients = coefficients;
                }
                for (int i = 0; i < Math.min(len, otherLen); i++) {
                    newCoefficients[i].subtract(otherCoefficients[i]);
                }
                coefficients = newCoefficients;
                normalize();
            }
            catch (DomainException e) {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public PolynomialElement negated() {
        RingElement[] newCoefficients = new RingElement[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            newCoefficients[i] = (RingElement)coefficients[i].negated();
        }
        PolynomialElement res = new PolynomialElement();
        res.setPolynomialRing(ring);
        res.setCoefficients(newCoefficients);
        return res;
    }

    
    public void negate() {
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i].negate();
        }
    }
    
    
    public PolynomialElement scaled(RingElement element)
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
    

    public PolynomialElement product(RingElement element)
            throws DomainException {
        if (element instanceof PolynomialElement) {
            return product((PolynomialElement)element);
        }
        else {
            if (getRing().getCoefficientRing().hasElement(element)) {                
                RingElement[] newCoefficients = new RingElement[coefficients.length];
                for (int i = 0; i < coefficients.length; i++) {
                    newCoefficients[i] = coefficients[i].product(element);                    
                }
                PolynomialElement res = new PolynomialElement();
                res.setPolynomialRing(getRing());
                res.setCoefficients(newCoefficients);
                res.normalize();
                return res;
            }
            else {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
    }

    
    public PolynomialElement product(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            if (isZero()) { return this; }
            if (element.isZero()) { return element; }
            RingElement[] newCoefficients = new RingElement[getDegree()+element.getDegree()+1];
            int i, j;
            for (i = 0; i < newCoefficients.length; i++) {
                newCoefficients[i] = ring.getCoefficientRing().getZero();
            }
            for (i = 0; i <= getDegree(); i++) {
                for (j = 0; j <= element.getDegree(); j++) {
                    RingElement p = getCoefficient(i).product(element.getCoefficient(j));
                    newCoefficients[i+j].add(p);
                }
            }
            PolynomialElement res = new PolynomialElement();
            res.setPolynomialRing(ring);
            res.setCoefficients(newCoefficients);
            res.normalize();
            return res;
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }


    public void multiply(RingElement element)
            throws DomainException {
        if (element instanceof PolynomialElement) {
            multiply((PolynomialElement)element);
        }
        else {
            if (getRing().getCoefficientRing().hasElement(element)) {                
                for (int i = 0; i < coefficients.length; i++) {
                    coefficients[i].multiply(element);                    
                }
                normalize();
            }
            else {
                throw new DomainException(this.getModule(), element.getModule());
            }
        }
    }

    
    public void multiply(PolynomialElement element)
            throws DomainException {
        if (getRing().equals(element.getRing())) {
            int d0 = Math.max(getDegree(), 0);
            int d1 = Math.max(element.getDegree(), 0);
            RingElement[] newCoefficients = new RingElement[d0+d1+1];
            int i, j;
            for (i = 0; i < newCoefficients.length; i++) {
                newCoefficients[i] = ring.getCoefficientRing().getZero();
            }
            for (i = 0; i <= d0; i++) {
                for (j = 0; j <= d1; j++) {
                    RingElement p = getCoefficient(i).product(element.getCoefficient(j));
                    newCoefficients[i+j].add(p);
                }
            }
            setPolynomialRing(ring);
            setCoefficients(newCoefficients);
            normalize();
        }
        else {
            throw new DomainException(this.getModule(), element.getModule());
        }
    }

    
    public boolean isInvertible() {
        return getDegree() == 0 && getCoefficient(0).isInvertible();
    }
    
    
    public PolynomialElement inverse() {
        if (isInvertible()) {
            return new PolynomialElement(getRing(), getCoefficient(0).inverse());
        }
        else {
            throw new InverseException("Inverse of "+this+" does not exist");
        }
    }

    
    public void invert() {
        if (isInvertible()) {
            setCoefficients(new RingElement[] {getCoefficient(0).inverse()});
        }
        else {
            throw new InverseException("Inverse of "+this+" does not exist");
        }
    }
    

    public PolynomialElement quotient(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof PolynomialElement) {
            return quotient((PolynomialElement)element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }

    
    public PolynomialElement quotient(PolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().equals(element.getRing())) {
            PolynomialElement[] remainder = new PolynomialElement[1];
            PolynomialElement q = quorem(element, remainder);
            if (remainder[0].getDegree() <= 0) {
                return q;
            }
            else {
                throw new DivisionException(this, element);
            }
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    
    
    public void divide(RingElement element)
            throws DomainException, DivisionException {
        if (element instanceof PolynomialElement) {
            divide((PolynomialElement)element);
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    

    public void divide(PolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().equals(element.getRing())) {
            PolynomialElement[] remainder = new PolynomialElement[1];
            PolynomialElement q = quorem(element, remainder);
            if (remainder[0].getDegree() <= 0) {
                coefficients = q.coefficients;
            }
            else {
                throw new DivisionException(this, element);
            }
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }


    public boolean divides(RingElement element) {
        if (element instanceof PolynomialElement) {
            PolynomialElement pol = (PolynomialElement)element;
            try {
                PolynomialElement remainder = pol.rem(this);
                if (remainder.getDegree() <= 0) {
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (DomainException e) {
                return false;
            }
            catch (DivisionException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    
    public PolynomialElement quorem(PolynomialElement element, PolynomialElement[] remainder)
            throws DomainException, DivisionException {
        if (getRing().hasElement(element)) {
            if (element.isZero()) {
                throw new DivisionException(this, element);
            }
            else if (getDegree() < element.getDegree()) {
                remainder[0] = this;
                return getRing().getZero();
            }
            try {
                RingElement[] p = new RingElement[coefficients.length];
                int pdeg = p.length-1;
                for (int i = 0; i <= pdeg; i++) { p[i] = coefficients[i]; }

                RingElement[] q = element.coefficients;
                int qdeg = q.length-1;


                RingElement[] quotient = new RingElement[pdeg-qdeg+1];
                int quotientdeg = quotient.length-1;

                int pi = pdeg;
                while (pi >= qdeg) {
                    RingElement q1 = p[pi].quotient(q[qdeg]);
                    quotient[quotientdeg] = q1;
                    for (int i = pi, j = qdeg; j >= 0; i--, j--) {
                        p[i] = p[i].difference(q1.product(q[j]));
                    }
                    pi--;
                    quotientdeg--;
                }
                remainder[0] = new PolynomialElement(getRing(), p);
                return new PolynomialElement(getRing(), quotient);
            }
            catch (DivisionException e) {
                throw new DivisionException(this, element);
            }
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    
    
    public PolynomialElement quo(PolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().hasElement(element)) {
            if (element.isZero()) {
                throw new DivisionException(this, element);
            }
            else if (getDegree() < element.getDegree()) {
                return getRing().getZero();
            }
            try {
                RingElement[] p = new RingElement[coefficients.length];
                int pdeg = p.length-1;
                for (int i = 0; i <= pdeg; i++) { p[i] = coefficients[i]; }

                RingElement[] q = element.coefficients;
                int qdeg = q.length-1;


                RingElement[] quotient = new RingElement[pdeg-qdeg+1];
                int quotientdeg = quotient.length-1;

                int pi = pdeg;
                while (pi >= qdeg) {
                    RingElement q1 = p[pi].quotient(q[qdeg]);
                    quotient[quotientdeg] = q1;
                    for (int i = pi, j = qdeg; j >= 0; i--, j--) {
                        p[i] = p[i].difference(q1.product(q[j]));
                    }
                    pi--;
                    quotientdeg--;
                }
                return new PolynomialElement(getRing(), quotient);
            }
            catch (DivisionException e) {
                throw new DivisionException(this, element);
            }
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }

    
    public PolynomialElement rem(PolynomialElement element)
            throws DomainException, DivisionException {
        if (getRing().hasElement(element)) {
            if (element.isZero()) {
                throw new DivisionException(this, element);
            }
            else if (getDegree() < element.getDegree()) {
                return this;
            }
            try {
                RingElement[] p = new RingElement[coefficients.length];
                int pdeg = p.length-1;
                for (int i = 0; i <= pdeg; i++) { p[i] = coefficients[i]; }

                RingElement[] q = element.coefficients;
                int qdeg = q.length-1;

                int pi = pdeg;
                while (pi >= qdeg) {
                    RingElement q1 = p[pi].quotient(q[qdeg]);
                    for (int i = pi, j = qdeg; j >= 0; i--, j--) {
                        p[i] = p[i].difference(q1.product(q[j]));
                    }
                    pi--;
                }
                return new PolynomialElement(getRing(), p);
            }
            catch (DivisionException e) {
                throw new DivisionException(this, element);
            }
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }

    
    /**
     * Returns the greatest common divisior
     * of <code>this</code> and <code>element</code>.
     */
    public PolynomialElement gcd(PolynomialElement element)
            throws DomainException, DivisionException {
        if (getModule().equals(element.getModule())) {
            PolynomialElement r0 = this;
            PolynomialElement r1 = element;
            PolynomialElement remainder;
            
            while (!r1.isZero()) {
                remainder = r0.rem(r1);
                r0 = r1;
                r1 = remainder;
            }
            RingElement lc = r0.coefficients[r0.coefficients.length-1];
            if (!lc.isOne()) {
                for (int i = 0; i < r0.coefficients.length; i++) {
                    r0.coefficients[i] = r0.coefficients[i].quotient(lc);
                }
            }
            return r0;
        }
        else {
            throw new DomainException(getRing(), element.getRing());
        }
    }
    
    
    /**
     * The extended Euclidean algorithm.
     * 
     * @param res is an array of polynomials that will contain two values such that:
     *            res[0]*x + res[1]*y = gcd(x,y)
     * @return the greatest common divisor of x and y, always non negative
     */
    public PolynomialElement exgcd(PolynomialElement y, PolynomialElement res[])
            throws DomainException, DivisionException {
        PolynomialElement x = this;
        PolynomialElement q, r, s, t;
        PolynomialElement r0 = x;
        PolynomialElement s0 = getRing().getOne();
        PolynomialElement t0 = getRing().getZero();
        PolynomialElement r1 = y;
        PolynomialElement s1 = getRing().getZero();
        PolynomialElement t1 = getRing().getOne();

        PolynomialElement remainder[] = new PolynomialElement[1];
        while (!r1.isZero()) {
            q = r0.quorem(r1, remainder);
            r = r1;
            s = s1;
            t = t1;
            r1 = r0.difference(q.product(r1));
            s1 = s0.difference(q.product(s1));
            t1 = t0.difference(q.product(t1));
            r0 = r;
            s0 = s;
            t0 = t;
        }

        RingElement lc = r0.coefficients[r0.coefficients.length-1];
        if (!lc.isOne()) {
            for (int i = 0; i < r0.coefficients.length; i++) {
                r0.coefficients[i] = r0.coefficients[i].quotient(lc);
            }
            for (int i = 0; i < s0.coefficients.length; i++) {
                s0.coefficients[i] = s0.coefficients[i].quotient(lc);
            }
            for (int i = 0; i < t0.coefficients.length; i++) {
                t0.coefficients[i] = t0.coefficients[i].quotient(lc);
            }
        }

        res[0] = s0;
        res[1] = t0;
        return r0;
    }

    
    public RingElement evaluate(RingElement element)
            throws DomainException {
        if (ring.getCoefficientRing().hasElement(element)) {
            if (getDegree() <= 0) {
                return getCoefficient(0);
            }
            else {
                RingElement result = getLeadingCoefficient().clone();
                for (int i = getDegree()-1; i >= 0; i--) {
                    result.multiply(element);
                    result.add(getCoefficient(i));
                }
                return result;
            }
        }
        else {
            throw new DomainException(ring.getCoefficientRing(), element.getRing());
        }
    }
    
    
    public PolynomialRing getModule() {
        return ring;
    }

    
    public PolynomialRing getRing() {
        return ring;
    }

    
    public RingElement[] getCoefficients() {
        return coefficients;
    }
    
    
    public RingElement getCoefficient(int power) {
        return coefficients[power];
    }
      

    public RingElement getLeadingCoefficient() {
        return coefficients[coefficients.length-1];
    }
    

    public int getDegree() {
        return isZero()?Integer.MIN_VALUE:coefficients.length-1;
    }

    
    public Ring getCoefficientRing() {
        return getRing().getCoefficientRing();
    }
    
    
    public String getIndeterminate() {
        return getRing().getIndeterminate();
    }
    
    
    public PolynomialFreeElement resize(int n) {
        if (n == 1) {
            return this;
        }
        else if (n == 0) {
            return PolynomialProperFreeElement.make(getRing(), new PolynomialElement[0]);
        }
        else {
            PolynomialElement[] values = new PolynomialElement[n];
            values[0] = this;
            for (int i = 1; i < n; i++) {
                values[i] = getRing().getZero();
            }
            return PolynomialProperFreeElement.make(getRing(), values);
        }
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof PolynomialElement) {
            PolynomialElement p = (PolynomialElement)object;
            if (getDegree() != p.getDegree()) {
                return false;
            }
            else {
                for (int i = 0; i <= getDegree(); i++) {
                    if (!getCoefficient(i).equals(p.getCoefficient(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        else {
            return false;
        }
    }

    
    public int compareTo(ModuleElement object) {
        if (object instanceof PolynomialElement) {
            PolynomialElement p = (PolynomialElement)object;
            int d0 = coefficients.length-1;
            int d1 = p.coefficients.length-1; 
            if (getRing().equals(p.getRing())) {
                for (int i = 0; i <= Math.min(d0, d1); i++) {
                    int c = getCoefficient(i).compareTo(p.getCoefficient(i));
                    if (c != 0) {
                        return c;
                    }
                }
                int d = d0-d1;
                return d;
            }
            else {
                return getRing().compareTo(p.getRing());
            }
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public PolynomialElement clone() {
        RingElement[] newCoefficients = new RingElement[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            newCoefficients[i] = coefficients[i].clone();
        }
        PolynomialElement res = new PolynomialElement();
        res.setPolynomialRing(getRing());
        res.setCoefficients(newCoefficients);
        return res;
    }
    
    
    public String stringRep(boolean ... parens) {
        if (isZero()) {
            return "0";
        }
        else if (isOne()) {
            return "1";
        }
        else {
            boolean paren = false;
            if (getRing().getCoefficientRing() instanceof PolynomialRing ||
                getRing().getCoefficientRing() instanceof CRing) {
                paren = true;
            }
            StringBuilder buf = new StringBuilder(30);
            String ind = ring.getIndeterminate();
            if (paren) {
                buf.append("(");
            }
            buf.append(coefficients[coefficients.length-1].stringRep(true));
            if (paren) {
                buf.append(")");
            }
            if (coefficients.length-1 > 1) {
                buf.append("*");
                buf.append(ind);
                buf.append("^");
                buf.append(coefficients.length-1);                        
            }
            else if (coefficients.length-1 == 1) {
                buf.append("*");
                buf.append(ind);
            }
            for (int i = coefficients.length-2; i >= 0; i--) {
                if (!coefficients[i].isZero()) {
                    buf.append("+");
                    if (paren) {
                        buf.append("(");
                    }
                    buf.append(coefficients[i].stringRep(true));
                    if (paren) {
                        buf.append(")");
                    }
                    if (i > 1) {
                        buf.append("*");
                        buf.append(ind);
                        buf.append("^");
                        buf.append(i);                        
                    }
                    else if (i == 1) {
                        buf.append("*");
                        buf.append(ind);
                    }
                }
            }
            if (parens.length > 0) {
                return TextUtils.parenthesize(buf.toString());
            }
            else {
                return buf.toString();
            }
        }
    }

    
    public String toString() {
        return "Polynomial["+stringRep()+"]";
    }
    
    
    public double[] fold(ModuleElement[] elements) {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    public ModuleElement cast(Module module) {
        return module.cast(this);
    }

    
    private final static String INDETERMINATE_ATTR = "indeterminate";
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEELEMENT, getElementTypeName(),
                                 INDETERMINATE_ATTR, ring.getIndeterminate());
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i].toXML(writer);
        }
        writer.closeBlock();
    }

    
    public ModuleElement fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        if (!element.hasAttribute(INDETERMINATE_ATTR)) {
            reader.setError("Type %%1 is missing attribute %%2.", getElementTypeName(), INDETERMINATE_ATTR);
            return null;                
        }
        String indeterminate = element.getAttribute(INDETERMINATE_ATTR);
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<RingElement> elements = new LinkedList<RingElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            if (!(moduleElement instanceof RingElement)) {
                reader.setError("Type %%1 must have children of type %%2.", getElementTypeName(), "RingElement");
                return null;                    
            }
            RingElement ringElement = (RingElement)moduleElement;
            Ring ring0 = ringElement.getRing();
            elements.add(ringElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                if (!(moduleElement instanceof RingElement)) {
                    reader.setError("Type %%1 must have children of a subtype of %%2.", getElementTypeName(), "RingElement");
                    return null;                    
                }
                ringElement = (RingElement)moduleElement;
                if (!ring0.hasElement(ringElement)) {
                    reader.setError("Type %%1 must have children all of the same type.", getElementTypeName());
                    return null;                                            
                }
                elements.add(ringElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            RingElement[] coeffs = new RingElement[elements.size()];
            int i = 0;
            for (RingElement e : elements) {
                coeffs[i++] = e;
            }
            PolynomialElement result = new PolynomialElement(indeterminate, coeffs);
            return result;
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private final static XMLInputOutput<ModuleElement> xmlIO =
        new PolynomialElement("X", new ZElement(0));
    
    public static XMLInputOutput<ModuleElement> getXMLInputOutput() {
        return xmlIO;
    }
       

    public String getElementTypeName() {
        return "PolynomialElement";
    }

    
    public int hashCode() {
        int hashCode = basicHash;
        for (int i = 0; i < coefficients.length; i++) {
            hashCode ^= coefficients[i].hashCode();
        }
        return hashCode;
    }
    
    
    private void setPolynomialRing(PolynomialRing ring) {
        this.ring = ring;
    }
    
    
    private void setCoefficients(RingElement[] coefficients) {
        this.coefficients = coefficients;
    }
    
    
    private void normalize() {
        if (getDegree() > 0) {
            int i = getDegree();
            while (getCoefficient(i).isZero() && i > 0) {
                i--;
            }
            if (i != getDegree()) {
                RingElement[] newCoefficients = new RingElement[i+1];
                for (int j = 0; j <= i; j++) {
                    newCoefficients[j] = getCoefficient(j);
                }
                setCoefficients(newCoefficients);
            }
        }
    }

    
    private PolynomialElement() {}

    
    private PolynomialRing ring;
    private RingElement[]  coefficients;
    
    private final static int basicHash = "PolynomialElement".hashCode();
}
