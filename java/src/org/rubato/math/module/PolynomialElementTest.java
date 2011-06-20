package org.rubato.math.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PolynomialElementTest {

    private PolynomialRing intPolRing;
    private PolynomialRing realPolRing;
    private PolynomialRing ratPolRing;
    
    private PolynomialElement i0, i1;
    private PolynomialElement r0, r1;
    private PolynomialElement q0, q1;
    
    private PolynomialElement ia, ib, ic, id;
    private PolynomialElement ra, rb, rc, rd;
    private PolynomialElement qa, qb, qc, qd;
    
    @Before
    public void setUp() throws Exception {
        intPolRing  = PolynomialRing.make(ZRing.ring, "X");
        realPolRing = PolynomialRing.make(RRing.ring, "Y");
        ratPolRing  = PolynomialRing.make(QRing.ring, "Z");
        
        i0 = intPolRing.parseString("0");
        i1 = intPolRing.parseString("1");
        r0 = realPolRing.parseString("0");
        r1 = realPolRing.parseString("1");
        q0 = ratPolRing.parseString("0");
        q1 = ratPolRing.parseString("1");
        
        ia = intPolRing.parseString("8*X^5+2*X^3+20");
        ib = intPolRing.parseString("3*X^3+-8*X^2+-2");
        ic = intPolRing.parseString("24*X^8+-64*X^7+-32*X^5+6*X^6+56*X^3+-160*X^2+-40");
        id = intPolRing.parseString("10");

        ra = realPolRing.parseString("8*Y^5+2*Y^3+20");
        rb = realPolRing.parseString("3*Y^3+-8*Y^2+-2");
        rc = realPolRing.parseString("24*Y^8+-64*Y^7+-32*Y^5+6*Y^6+56*Y^3+-160*Y^2+-40");
        rd = realPolRing.parseString("2.3");

        qa = ratPolRing.parseString("8*Z^5+2*Z^3+20");
        qb = ratPolRing.parseString("3*Z^3+-8*Z^2+-2");
        qc = ratPolRing.parseString("24*Z^8+-64*Z^7+-32*Z^5+6*Z^6+56*Z^3+-160*Z^2+-40");
        qd = ratPolRing.parseString("3/2");
    }

    @Test
    public final void testIsOne() {
        assertFalse(i0.isOne());
        assertTrue(i1.isOne());
        assertFalse(r0.isOne());
        assertTrue(r1.isOne());
        assertFalse(q0.isOne());
        assertTrue(q1.isOne());
    }

    @Test
    public final void testIsZero() {
        assertTrue(i0.isZero());
        assertFalse(i1.isZero());
        assertTrue(r0.isZero());
        assertFalse(r1.isZero());
        assertTrue(q0.isZero());
        assertFalse(q1.isZero());
    }

    @Test
    public final void testProductRingElement() {
        try {
            assertEquals(ia.product(ib), ic);
            assertEquals(ia.product(i0), i0);
            assertEquals(i0.product(ia), i0);
            assertEquals(ia.product(i1), ia);
            assertEquals(i1.product(ia), ia);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
        
        try {
            assertEquals(ra.product(rb), rc);
            assertEquals(ra.product(r0), r0);
            assertEquals(r0.product(ra), r0);
            assertEquals(ra.product(r1), ra);
            assertEquals(r1.product(ra), ra);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(qa.product(qb), qc);
            assertEquals(qa.product(q0), q0);
            assertEquals(q0.product(qa), q0);
            assertEquals(qa.product(q1), qa);
            assertEquals(q1.product(qa), qa);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testMultiplyRingElement() {
        try {
            ia.multiply(ib);
            assertEquals(ia, ic);
            ia.multiply(i0);
            assertEquals(ia, i0);
            ic.multiply(i1);
            assertEquals(ic, ic);
            i1.multiply(ic);
            assertEquals(i1, ic);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            ra.multiply(rb);
            assertEquals(ra, rc);

            ra.multiply(r0);
            assertEquals(ra, r0);
            rc.multiply(r1);
            assertEquals(rc, rc);
            r1.multiply(rc);
            assertEquals(r1, rc);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            qa.multiply(qb);
            assertEquals(qa, qc);
            qa.multiply(q0);
            assertEquals(qa, q0);
            qc.multiply(q1);
            assertEquals(qc, qc);
            q1.multiply(qc);
            assertEquals(q1, qc);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testIsInvertible() {
        assertTrue(i1.isInvertible());
        assertFalse(i0.isInvertible());
        assertFalse(ia.isInvertible());
        assertFalse(ib.isInvertible());
        assertFalse(ic.isInvertible());
        assertFalse(id.isInvertible());

        assertTrue(r1.isInvertible());
        assertFalse(r0.isInvertible());
        assertFalse(ra.isInvertible());
        assertFalse(rb.isInvertible());
        assertFalse(rc.isInvertible());
        assertTrue(rd.isInvertible());

        assertTrue(q1.isInvertible());
        assertFalse(q0.isInvertible());
        assertFalse(qa.isInvertible());
        assertFalse(qb.isInvertible());
        assertFalse(qc.isInvertible());
        assertTrue(qd.isInvertible());
    }

    @Test
    public final void testInverse() {
        assertEquals(i1.inverse(), i1);
        assertEquals(r1.inverse(), r1);
        assertEquals(q1.inverse(), q1);
        assertEquals(qd.inverse(), new PolynomialElement(ratPolRing, new QElement(2, 3)));
    }

    @Test
    public final void testInvert() {
        i1.invert();
        assertEquals(i1, new PolynomialElement(intPolRing, new ZElement(1)));
        r1.invert();
        assertEquals(r1, new PolynomialElement(realPolRing, new RElement(1)));
        q1.invert();
        assertEquals(q1, new PolynomialElement(ratPolRing, new QElement(1)));
        qd.invert();
        assertEquals(qd, new PolynomialElement(ratPolRing, new QElement(2, 3)));
    }

    @Test
    public final void testClone() {
        assertEquals(i0, i0.clone());
        assertEquals(i1, i1.clone());
        assertEquals(ia, ia.clone());
        assertEquals(ib, ib.clone());
        assertEquals(ic, ic.clone());
        assertEquals(id, id.clone());

        assertEquals(r0, r0.clone());
        assertEquals(r1, r1.clone());
        assertEquals(ra, ra.clone());
        assertEquals(rb, rb.clone());
        assertEquals(rc, rc.clone());
        assertEquals(rd, rd.clone());

        assertEquals(q0, q0.clone());
        assertEquals(q1, q1.clone());
        assertEquals(qa, qa.clone());
        assertEquals(qb, qb.clone());
        assertEquals(qc, qc.clone());
        assertEquals(qd, qd.clone());
    }

    @Test
    public final void testScaled() {
        try {
            assertEquals(i0.scaled(new ZElement(12)), i0);
            assertEquals(i1.scaled(new ZElement(12)), new PolynomialElement(intPolRing, new ZElement(12)));
            assertEquals(ia.scaled(new ZElement(0)), i0);
            assertEquals(ia.scaled(new ZElement(1)), ia);
            assertEquals(ib.scaled(new ZElement(3)), new PolynomialElement(intPolRing, new ZElement(-6), new ZElement(0), new ZElement(-24), new ZElement(9)));
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(r0.scaled(new RElement(12)), r0);
            assertEquals(r1.scaled(new RElement(12)), new PolynomialElement(realPolRing, new RElement(12)));
            assertEquals(ra.scaled(new RElement(0)), r0);
            assertEquals(ra.scaled(new RElement(1)), ra);
            assertEquals(rb.scaled(new RElement(1.5)), new PolynomialElement(realPolRing, new RElement(-3), new RElement(0), new RElement(-12), new RElement(4.5)));
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(q0.scaled(new QElement(12)), q0);
            assertEquals(q1.scaled(new QElement(12)), new PolynomialElement(ratPolRing, new QElement(12)));
            assertEquals(qa.scaled(new QElement(0)), q0);
            assertEquals(qa.scaled(new QElement(1)), qa);
            assertEquals(qb.scaled(new QElement(3, 2)), new PolynomialElement(ratPolRing, new QElement(-3), new QElement(0), new QElement(-12), new QElement(9, 2)));
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testScale() {
        try {
            ib.scale(new ZElement(3));
            assertEquals(ib, new PolynomialElement(intPolRing, new ZElement(-6), new ZElement(0), new ZElement(-24), new ZElement(9)));
            ib.scale(new ZElement(0));
            assertEquals(ib, i0);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            rb.scale(new RElement(1.5));
            assertEquals(rb, new PolynomialElement(realPolRing, new RElement(-3), new RElement(0), new RElement(-12), new RElement(4.5)));
            rb.scale(new RElement(0));
            assertEquals(rb, r0);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }

        try {
            qb.scale(new QElement(3, 2));
            assertEquals(qb, new PolynomialElement(ratPolRing, new QElement(-3), new QElement(0), new QElement(-12), new QElement(9, 2)));
            qb.scale(new QElement(0));
            assertEquals(qb, q0);
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testEvaluate() {
        try {
            assertEquals(i0.evaluate(new ZElement(23)), new ZElement(0));
            assertEquals(i1.evaluate(new ZElement(-12)), new ZElement(1));
            assertEquals(ib.evaluate(new ZElement(-3)), new ZElement(-155));

            assertEquals(r0.evaluate(new RElement(23)), new RElement(0));
            assertEquals(r1.evaluate(new RElement(-12)), new RElement(1));
            assertEquals(rb.evaluate(new RElement(-3.5)), new RElement( -228.625));

            assertEquals(q0.evaluate(new QElement(23)), new QElement(0));
            assertEquals(q1.evaluate(new QElement(-12)), new QElement(1));
            assertEquals(qb.evaluate(new QElement(1, 3)), new QElement(-25, 9));
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testGetModule() {
        assertEquals(i0.getModule(), intPolRing);
        assertEquals(i1.getModule(), intPolRing);
        assertEquals(ia.getModule(), intPolRing);
        assertEquals(ib.getModule(), intPolRing);
        assertEquals(ic.getModule(), intPolRing);
        assertEquals(id.getModule(), intPolRing);
        assertNotSame(ia.getModule(), realPolRing);

        assertEquals(r0.getModule(), realPolRing);
        assertEquals(r1.getModule(), realPolRing);
        assertEquals(ra.getModule(), realPolRing);
        assertEquals(rb.getModule(), realPolRing);
        assertEquals(rc.getModule(), realPolRing);
        assertEquals(rd.getModule(), realPolRing);
        assertNotSame(ra.getModule(), ratPolRing);

        assertEquals(q0.getModule(), ratPolRing);
        assertEquals(q1.getModule(), ratPolRing);
        assertEquals(qa.getModule(), ratPolRing);
        assertEquals(qb.getModule(), ratPolRing);
        assertEquals(qc.getModule(), ratPolRing);
        assertEquals(qd.getModule(), ratPolRing);
        assertNotSame(qa.getModule(), intPolRing);
    }

    @Test
    public final void testGetPolynomialRing() {
        assertEquals(i0.getRing(), intPolRing);
        assertEquals(i1.getRing(), intPolRing);
        assertEquals(ia.getRing(), intPolRing);
        assertEquals(ib.getRing(), intPolRing);
        assertEquals(ic.getRing(), intPolRing);
        assertEquals(id.getRing(), intPolRing);
        assertNotSame(ia.getRing(), realPolRing);

        assertEquals(r0.getRing(), realPolRing);
        assertEquals(r1.getRing(), realPolRing);
        assertEquals(ra.getRing(), realPolRing);
        assertEquals(rb.getRing(), realPolRing);
        assertEquals(rc.getRing(), realPolRing);
        assertEquals(rd.getRing(), realPolRing);
        assertNotSame(ra.getRing(), ratPolRing);

        assertEquals(q0.getRing(), ratPolRing);
        assertEquals(q1.getRing(), ratPolRing);
        assertEquals(qa.getRing(), ratPolRing);
        assertEquals(qb.getRing(), ratPolRing);
        assertEquals(qc.getRing(), ratPolRing);
        assertEquals(qd.getRing(), ratPolRing);
        assertNotSame(qa.getRing(), intPolRing);
    }

    @Test
    public final void testGetCoefficient() {
        assertEquals(ia.getCoefficient(3), new ZElement(2));
        assertEquals(ic.getCoefficient(4), new ZElement(0));
        
        assertEquals(ra.getCoefficient(3), new RElement(2));
        assertEquals(rc.getCoefficient(4), new RElement(0));

        assertEquals(qa.getCoefficient(3), new QElement(2));
        assertEquals(qc.getCoefficient(4), new QElement(0));
    }

    @Test
    public final void testGetLeadingCoefficient() {
        assertEquals(i0.getLeadingCoefficient(), new ZElement(0));
        assertEquals(i1.getLeadingCoefficient(), new ZElement(1));
        assertEquals(ia.getLeadingCoefficient(), new ZElement(8));
        assertEquals(ib.getLeadingCoefficient(), new ZElement(3));
        assertEquals(ic.getLeadingCoefficient(), new ZElement(24));
        assertEquals(id.getLeadingCoefficient(), new ZElement(10));

        assertEquals(r0.getLeadingCoefficient(), new RElement(0));
        assertEquals(r1.getLeadingCoefficient(), new RElement(1));
        assertEquals(ra.getLeadingCoefficient(), new RElement(8));
        assertEquals(rb.getLeadingCoefficient(), new RElement(3));
        assertEquals(rc.getLeadingCoefficient(), new RElement(24));
        assertEquals(rd.getLeadingCoefficient(), new RElement(2.3));

        assertEquals(q0.getLeadingCoefficient(), new QElement(0));
        assertEquals(q1.getLeadingCoefficient(), new QElement(1));
        assertEquals(qa.getLeadingCoefficient(), new QElement(8));
        assertEquals(qb.getLeadingCoefficient(), new QElement(3));
        assertEquals(qc.getLeadingCoefficient(), new QElement(24));
        assertEquals(qd.getLeadingCoefficient(), new QElement(3, 2));
    }

    @Test
    public final void testGetDegree() {
        assertEquals(i0.getDegree(), Integer.MIN_VALUE);
        assertEquals(i1.getDegree(), 0);
        assertEquals(ia.getDegree(), 5);
        assertEquals(ib.getDegree(), 3);
        assertEquals(ic.getDegree(), 8);
        assertEquals(id.getDegree(), 0);

        assertEquals(r0.getDegree(), Integer.MIN_VALUE);
        assertEquals(r1.getDegree(), 0);
        assertEquals(ra.getDegree(), 5);
        assertEquals(rb.getDegree(), 3);
        assertEquals(rc.getDegree(), 8);
        assertEquals(rd.getDegree(), 0);

        assertEquals(q0.getDegree(), Integer.MIN_VALUE);
        assertEquals(q1.getDegree(), 0);
        assertEquals(qa.getDegree(), 5);
        assertEquals(qb.getDegree(), 3);
        assertEquals(qc.getDegree(), 8);
        assertEquals(qd.getDegree(), 0);
    }

    @Test
    public final void testCast() {
        assertEquals(ia.cast(intPolRing), ia);
        assertEquals(ia.cast(realPolRing), ra);
        assertEquals(ia.cast(ratPolRing), qa);

        assertEquals(ra.cast(intPolRing), ia);
        assertEquals(ra.cast(realPolRing), ra);
        assertEquals(ra.cast(ratPolRing), qa);

        assertEquals(qa.cast(intPolRing), ia);
        assertEquals(qa.cast(realPolRing), ra);
        assertEquals(qa.cast(ratPolRing), qa);
    }

    @Test
    public final void testPower() {
        try {
            assertEquals(i0.power(10), i0);
            assertEquals(i1.power(10), i1);
            assertEquals(ia.power(3), ia.product(ia).product(ia));
            assertEquals(id.power(3), id.product(id).product(id));
            
            assertEquals(r0.power(10), r0);
            assertEquals(r1.power(10), r1);
            assertEquals(ra.power(3), ra.product(ra).product(ra));
            assertEquals(rd.power(3), rd.product(rd).product(rd));

            assertEquals(q0.power(10), q0);
            assertEquals(q1.power(10), q1);
            assertEquals(qa.power(3), qa.product(qa).product(qa));
            assertEquals(qd.power(3), qd.product(qd).product(qd));
        }
        catch (DomainException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public final void testGetRing() {
        assertEquals(i0.getRing(), intPolRing);
        assertEquals(i1.getRing(), intPolRing);
        assertEquals(ia.getRing(), intPolRing);
        assertEquals(ib.getRing(), intPolRing);
        assertEquals(ic.getRing(), intPolRing);
        assertEquals(id.getRing(), intPolRing);
        assertNotSame(ia.getRing(), realPolRing);

        assertEquals(r0.getRing(), realPolRing);
        assertEquals(r1.getRing(), realPolRing);
        assertEquals(ra.getRing(), realPolRing);
        assertEquals(rb.getRing(), realPolRing);
        assertEquals(rc.getRing(), realPolRing);
        assertEquals(rd.getRing(), realPolRing);
        assertNotSame(ra.getRing(), ratPolRing);

        assertEquals(q0.getRing(), ratPolRing);
        assertEquals(q1.getRing(), ratPolRing);
        assertEquals(qa.getRing(), ratPolRing);
        assertEquals(qb.getRing(), ratPolRing);
        assertEquals(qc.getRing(), ratPolRing);
        assertEquals(qd.getRing(), ratPolRing);
        assertNotSame(qa.getRing(), intPolRing);
    }
}
