package org.rubato.rubettes.bigbang.view.controller.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.AffineTool;
import org.rubato.rubettes.util.GeometryTools;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.LeapUtil.Axis;
import org.rubato.rubettes.util.LeapUtil.Operation;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectAffineAdapter extends Listener {
	
	private class MatchPoint {
		public Point2D p;
		public int id;
	}
	
	private AffineTool affineTool;
	private ViewController controller;
	
//	private ArrayList<MatchPoint> prevPoints;
	private HashMap<Integer, Point2D> prevPoints;
	
	private Point2D startingPoint;
	private Point2D p1;
	private int p1id;
	private Point2D p2;
	private int p2id;
	private Point2D p3;
	private int p3id;
	
	private RMatrix transform;
	
	private Boolean isActive = false;
	
	public LeapObjectAffineAdapter(ViewController controller) {
		this.controller = controller;
		this.affineTool = new AffineTool();
		double[][] id = {
				{1,0,0},
				{0,1,0},
				{0,0,1}
			};
		this.transform = new RMatrix(id);
		this.prevPoints = new HashMap<Integer, Point2D>();
	}
	
	@Override
	public void onFrame(Controller controller) {
		List<Finger> fingers = LeapUtil.removeOffscreenFingers(LeapUtil.FingerListToJavaList(controller.frame().fingers()));
		fingers = LeapUtil.keepFingerIf(fingers, Axis.Z_AXIS, Operation.LESS_THAN, 0);
		
		if (fingers.size() < 1) {
			if (isActive) {
				this.controller.clearDisplayTool();
				isActive = false;
				this.transform = getIdentity();
			}
			return;
		}
		
		if (!isActive) {
			findStartingPoint(fingers);
			HashMap<Integer, Point2D> fHash = fingersToHash(fingers);
			affineTool.setStartingPoint(startingPoint);
			double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
			RMatrix identity = new RMatrix(t);
			affineTool.setTransform(identity);
			updateView(true);
			isActive = true;
			prevPoints = fHash;
		}
		else {
			HashMap<Integer, Point2D> fHash = fingersToHash(fingers);
			ArrayList<Integer> matches = findMatches(prevPoints, fHash);
			RMatrix newTransform = getIdentity();
			ArrayList<Point2D> p1 = new ArrayList<Point2D>();
			ArrayList<Point2D> p2 = new ArrayList<Point2D>();
			for (Integer i : matches) {
				p1.add(this.prevPoints.get(i));
				p2.add(fHash.get(i));
			}
			switch (matches.size()) {
			case 0:
				break;
			case 1:
				newTransform = findT1Match(p1, p2);
				break;
			case 2:
				newTransform = findT2Match(p1, p2);
				break;
			default:
				newTransform = first3Matcher(p1, p2);
				break;
			}
//			System.out.println("Number of matches: " + matches.size());
			this.transform = newTransform.product(this.transform);
			affineTool.setTransform(transform);
			this.updateView(false);
			this.prevPoints = fHash;
			return;
			
			
			
//			int fingersFound = 0;
//			Point2D t1 = null,t2 = null,t3 = null;
//			for (Finger f : fingers) {
//				if (f.id() == p1id) {
//					t1 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
//					fingersFound++;
//				}
//				else if (f.id() == p2id) {
//					t2 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
//					fingersFound++;
//				}
//				else if (f.id() == p3id) {
//					t3 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
//					fingersFound++;
//				}
//			}
//			if (fingersFound < 3) {
//				this.controller.clearDisplayTool();
//				isActive = false;
//				return;
//			}
//			double[][] m1 = {
//					{p1.getX(), 	p2.getX(), 	p3.getX()},
//					{p1.getY(), 	p2.getY(), 	p3.getY()},
//					{1,		1,		1}
//					};
//			double[][] m2 = {
//					{t1.getX(), 	t2.getX(), 	t3.getX()},
//					{t1.getY(), 	t2.getY(), 	t3.getY()},
//					{1,		1,		1}
//					};
//			RMatrix start = new RMatrix(m1);
//			RMatrix end = new RMatrix(m2);
//			this.transform = end.product(start.inverse());
//			affineTool.setTransform(transform);
//			this.updateView(true);
		}
		
		
	}
	
	private Point2D ndToDouble(PointND p) {
		return new Point2D(p.getCoord(0), p.getCoord(1));
	}
	
	private void updateView(boolean startNewTransformation) {
		RMatrix t = reflectAcrossX(this.transform);
		double[] shift = {t.get(0, 2), t.get(1, 2)};
		double[][] t2x2 = {{t.get(0, 0), t.get(0, 1)}, {t.get(1,0), t.get(1, 1)}};
		RMatrix transform2x2 = new RMatrix(t2x2);
		boolean copyAndTransform = false; //TODO:read ALT key!!!!
//		System.out.println(transform2x2);
//		System.out.println("" + shift[0]+ "," + shift[1]);
//		System.out.println(transform);
		
		this.controller.affineTransformSelectedObjects(startingPoint, startingPoint, shift, transform2x2, copyAndTransform, startNewTransformation);
		this.controller.changeDisplayTool(this.affineTool);
	}
	
	private RMatrix getIdentity() {
		double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
		return new RMatrix(t);
	}
	
	private RMatrix findT1Match(ArrayList<Point2D> start, ArrayList<Point2D> end) {
		RMatrix t = getIdentity();
		t.set(0, 2, end.get(0).getX() - start.get(0).getX());
		t.set(1, 2, end.get(0).getY() - start.get(0).getY());
		return t;
	}
	
	private RMatrix findT2Match(ArrayList<Point2D> start, ArrayList<Point2D> end) {
		
		Point2D sCenter = new Point2D((start.get(0).getX()+start.get(1).getX())/2, (start.get(0).getY()+start.get(1).getY())/2);
		Point2D eCenter = new Point2D((end.get(0).getX()+end.get(1).getX())/2, (end.get(0).getY()+end.get(1).getY())/2);
		double xTrans = eCenter.getX() - sCenter.getX();
		double yTrans = eCenter.getY() - sCenter.getY();
		
		double sDist = start.get(0).distance(start.get(1));
		double eDist = end.get(0).distance(end.get(1));
		double scale = eDist/sDist;
		
		double sAngle = GeometryTools.calculateAngle(sCenter, start.get(0));
		double eAngle = GeometryTools.calculateAngle(eCenter, end.get(0));
		double angle = eAngle - sAngle;
		
		RMatrix t1 = getIdentity();
		t1.set(0, 2, sCenter.getX() * -1);
		t1.set(1, 2, sCenter.getY() * -1);
		
		RMatrix r = getIdentity();
		r.set(0, 0, Math.cos(angle));
		r.set(1, 1, Math.cos(angle));
		r.set(0, 1, Math.sin(angle));
		r.set(1,0,-1 * Math.sin(angle));
		RMatrix s = getIdentity();
		s.set(0, 0, scale);
		s.set(1, 1, scale);
		RMatrix t2 = getIdentity();
		t2.set(0, 2, xTrans + sCenter.getX());
		t2.set(1, 2, yTrans + sCenter.getY());
		
		return t2.product(s.product(r.product(t1)));
	}
	
	private RMatrix first3Matcher(ArrayList<Point2D> start, ArrayList<Point2D> end) {
		RMatrix sPoints = getIdentity();
		RMatrix ePoints = getIdentity();
		for (int i = 0; i < 3; i++) {
			sPoints.set(0, i, start.get(i).getX());
			sPoints.set(1, i, start.get(i).getY());
			sPoints.set(2, i, 1);
			ePoints.set(0, i, end.get(i).getX());
			ePoints.set(1, i, end.get(i).getY());
			ePoints.set(2, i, 1);
		}
		return ePoints.product(sPoints.inverse());
	}
	
	private ArrayList<Integer> findMatches(HashMap<Integer, Point2D> s1, HashMap<Integer, Point2D> s2) {
		Set<Integer> keySet = s2.keySet();
		ArrayList<Integer> matches = new ArrayList<Integer>();
		for (Integer i : keySet) {
			if (s1.containsKey(i)) {
				matches.add(i);
			}
		}
		return matches;
	}
	
	private void findStartingPoint(List<Finger> fingers) {
		double xAccum = 0;
		double yAccum = 0;
		int count = 0;
		for (Finger f : fingers) {
			Point2D p = ndToDouble(LeapUtil.fingerToScreenPoint(f));
			xAccum += p.getX();
			yAccum += p.getY();
			count++;
		}
		this.startingPoint = new Point2D(xAccum/count, yAccum/count);
	}
	
	private HashMap<Integer, Point2D> fingersToHash(List<Finger> fingers) {
		HashMap<Integer, Point2D> hash = new HashMap<Integer, Point2D>();
		for ( Finger f : fingers) {
			hash.put(f.id(), startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f))));
		}
		return hash;
	}
	
	private Point2D startToOrigin(Point2D p) {
		return p.minus(startingPoint);
	}
	
	private Point2D originToStart(Point2D p) {
		return p.plus(startingPoint);
	}
	
	private RMatrix reflectAcrossX(RMatrix t) {		
		RMatrix reflect = new RMatrix(3,3);
		reflect.set(0, 0, 1);
		reflect.set(1, 1, -1);
		reflect.set(2, 2, 1);
		
		return reflect.product(t);
	}
}
