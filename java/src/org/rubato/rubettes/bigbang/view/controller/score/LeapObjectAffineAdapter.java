package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.AffineTool;
import org.rubato.rubettes.util.GeometryTools;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.LeapUtil.Axis;
import org.rubato.rubettes.util.LeapUtil.Operation;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectAffineAdapter extends Listener {
	
	private class MatchPoint {
		public Point2D.Double p;
		public int id;
	}
	
	private AffineTool affineTool;
	private ViewController controller;
	
//	private ArrayList<MatchPoint> prevPoints;
	private HashMap<Integer, Point2D.Double> prevPoints;
	
	private Point2D.Double startingPoint;
	private Point2D.Double p1;
	private int p1id;
	private Point2D.Double p2;
	private int p2id;
	private Point2D.Double p3;
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
		this.prevPoints = new HashMap<Integer, Point2D.Double>();
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
			HashMap<Integer, Point2D.Double> fHash = fingersToHash(fingers);
			affineTool.setStartingPoint(startingPoint);
			double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
			RMatrix identity = new RMatrix(t);
			affineTool.setTransform(identity);
			updateView(true);
			isActive = true;
			prevPoints = fHash;
		}
		else {
			HashMap<Integer, Point2D.Double> fHash = fingersToHash(fingers);
			ArrayList<Integer> matches = findMatches(prevPoints, fHash);
			RMatrix newTransform = getIdentity();
			ArrayList<Point2D.Double> p1 = new ArrayList<Point2D.Double>();
			ArrayList<Point2D.Double> p2 = new ArrayList<Point2D.Double>();
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
			this.updateView(true);
			this.prevPoints = fHash;
			return;
			
			
			
//			int fingersFound = 0;
//			Point2D.Double t1 = null,t2 = null,t3 = null;
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
//					{p1.x, 	p2.x, 	p3.x},
//					{p1.y, 	p2.y, 	p3.y},
//					{1,		1,		1}
//					};
//			double[][] m2 = {
//					{t1.x, 	t2.x, 	t3.x},
//					{t1.y, 	t2.y, 	t3.y},
//					{1,		1,		1}
//					};
//			RMatrix start = new RMatrix(m1);
//			RMatrix end = new RMatrix(m2);
//			this.transform = end.product(start.inverse());
//			affineTool.setTransform(transform);
//			this.updateView(true);
		}
		
		
	}
	
	private Point2D.Double ndToDouble(PointND p) {
		return new Point2D.Double(p.getCoord(0), p.getCoord(1));
	}
	
	private void updateView(boolean inPreviewMode) {
		RMatrix t = reflectAcrossX(this.transform);
		double[] shift = {t.get(0, 2), t.get(1, 2)};
		double[][] t2x2 = {{t.get(0, 0), t.get(0, 1)}, {t.get(1,0), t.get(1, 1)}};
		RMatrix transform2x2 = new RMatrix(t2x2);
		boolean copyAndTransform = false; //TODO:read ALT key!!!!
//		System.out.println(transform2x2);
//		System.out.println("" + shift[0]+ "," + shift[1]);
//		System.out.println(transform);
		
		this.controller.affineTransformSelectedObjects(startingPoint, startingPoint, shift, transform2x2, copyAndTransform, inPreviewMode);
		this.controller.changeDisplayTool(this.affineTool);
	}
	
	private RMatrix getIdentity() {
		double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
		return new RMatrix(t);
	}
	
	private RMatrix findT1Match(ArrayList<Point2D.Double> start, ArrayList<Point2D.Double> end) {
		RMatrix t = getIdentity();
		t.set(0, 2, end.get(0).x - start.get(0).x);
		t.set(1, 2, end.get(0).y - start.get(0).y);
		return t;
	}
	
	private RMatrix findT2Match(ArrayList<Point2D.Double> start, ArrayList<Point2D.Double> end) {
		
		Point2D.Double sCenter = new Point2D.Double((start.get(0).x+start.get(1).x)/2, (start.get(0).y+start.get(1).y)/2);
		Point2D.Double eCenter = new Point2D.Double((end.get(0).x+end.get(1).x)/2, (end.get(0).y+end.get(1).y)/2);
		double xTrans = eCenter.x - sCenter.x;
		double yTrans = eCenter.y - sCenter.y;
		
		double sDist = start.get(0).distance(start.get(1));
		double eDist = end.get(0).distance(end.get(1));
		double scale = eDist/sDist;
		
		double sAngle = GeometryTools.calculateAngle(sCenter, start.get(0));
		double eAngle = GeometryTools.calculateAngle(eCenter, end.get(0));
		double angle = eAngle - sAngle;
		
		RMatrix t1 = getIdentity();
		t1.set(0, 2, sCenter.x * -1);
		t1.set(1, 2, sCenter.y * -1);
		
		RMatrix r = getIdentity();
		r.set(0, 0, Math.cos(angle));
		r.set(1, 1, Math.cos(angle));
		r.set(0, 1, Math.sin(angle));
		r.set(1,0,-1 * Math.sin(angle));
		RMatrix s = getIdentity();
		s.set(0, 0, scale);
		s.set(1, 1, scale);
		RMatrix t2 = getIdentity();
		t2.set(0, 2, xTrans + sCenter.x);
		t2.set(1, 2, yTrans + sCenter.y);
		
		return t2.product(s.product(r.product(t1)));
	}
	
	private RMatrix first3Matcher(ArrayList<Point2D.Double> start, ArrayList<Point2D.Double> end) {
		RMatrix sPoints = getIdentity();
		RMatrix ePoints = getIdentity();
		for (int i = 0; i < 3; i++) {
			sPoints.set(0, i, start.get(i).x);
			sPoints.set(1, i, start.get(i).y);
			sPoints.set(2, i, 1);
			ePoints.set(0, i, end.get(i).x);
			ePoints.set(1, i, end.get(i).y);
			ePoints.set(2, i, 1);
		}
		return ePoints.product(sPoints.inverse());
	}
	
	private ArrayList<Integer> findMatches(HashMap<Integer, Point2D.Double> s1, HashMap<Integer, Point2D.Double> s2) {
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
			Point2D.Double p = ndToDouble(LeapUtil.fingerToScreenPoint(f));
			xAccum += p.x;
			yAccum += p.y;
			count++;
		}
		this.startingPoint = new Point2D.Double(xAccum/count, yAccum/count);
	}
	
	private HashMap<Integer, Point2D.Double> fingersToHash(List<Finger> fingers) {
		HashMap<Integer, Point2D.Double> hash = new HashMap<Integer, Point2D.Double>();
		for ( Finger f : fingers) {
			hash.put(f.id(), startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f))));
		}
		return hash;
	}
	
	private Point2D.Double startToOrigin(Point2D.Double p) {
		p.x -= startingPoint.x;
		p.y -= startingPoint.y;
		return p;
	}
	
	private Point2D.Double originToStart(Point2D.Double p) {
		p.x += startingPoint.x;
		p.y += startingPoint.y;
		return p;
	}
	
	private RMatrix reflectAcrossX(RMatrix t) {		
		RMatrix reflect = new RMatrix(3,3);
		reflect.set(0, 0, 1);
		reflect.set(1, 1, -1);
		reflect.set(2, 2, 1);
		
		return reflect.product(t);
	}
}
