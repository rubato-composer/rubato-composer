package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.AffineTool;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.LeapUtil.Axis;
import org.rubato.rubettes.util.LeapUtil.Operation;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectAffineAdapter extends Listener {
	
	private AffineTool affineTool;
	private ViewController controller;
	
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
	}
	
	@Override
	public void onFrame(Controller controller) {
		List<Finger> fingers = LeapUtil.removeOffscreenFingers(LeapUtil.FingerListToJavaList(controller.frame().fingers()));
		fingers = LeapUtil.keepFingerIf(fingers, Axis.Z_AXIS, Operation.LESS_THAN, 0);
		fingers = LeapUtil.getFrontmostFingers(fingers, 3);
		
		if (fingers.size() < 3) {
			if (isActive) {
				this.controller.clearDisplayTool();
				isActive = false;
			}
			return;
		}
		
		if (!isActive) {
			p1id = fingers.get(0).id();
			p1 = ndToDouble(LeapUtil.fingerToScreenPoint(fingers.get(0)));
			p2id = fingers.get(1).id();
			p2 = ndToDouble(LeapUtil.fingerToScreenPoint(fingers.get(1)));
			p3id = fingers.get(2).id();
			p3 = ndToDouble(LeapUtil.fingerToScreenPoint(fingers.get(2)));
			startingPoint = new Point2D.Double((p1.x+p2.x+p3.x)/3, (p1.y+p2.y+p3.y)/3);
			p1 = startToOrigin(p1);
			p2 = startToOrigin(p2);
			p3 = startToOrigin(p3);
			affineTool.setStartingPoint(startingPoint);
			double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
			RMatrix identity = new RMatrix(t);
			affineTool.setTransform(identity);
			updateView(true);
			isActive = true;
		}
		else {
			
			int fingersFound = 0;
			Point2D.Double t1 = null,t2 = null,t3 = null;
			for (Finger f : fingers) {
				if (f.id() == p1id) {
					t1 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
					fingersFound++;
				}
				else if (f.id() == p2id) {
					t2 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
					fingersFound++;
				}
				else if (f.id() == p3id) {
					t3 = startToOrigin(ndToDouble(LeapUtil.fingerToScreenPoint(f)));
					fingersFound++;
				}
			}
			if (fingersFound < 3) {
				this.controller.clearDisplayTool();
				isActive = false;
				return;
			}
			double[][] m1 = {
					{p1.x, 	p2.x, 	p3.x},
					{p1.y, 	p2.y, 	p3.y},
					{1,		1,		1}
					};
			double[][] m2 = {
					{t1.x, 	t2.x, 	t3.x},
					{t1.y, 	t2.y, 	t3.y},
					{1,		1,		1}
					};
			RMatrix start = new RMatrix(m1);
			RMatrix end = new RMatrix(m2);
			this.transform = end.product(start.inverse());
			affineTool.setTransform(transform);
			this.updateView(true);
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
		System.out.println("" + shift[0]+ "," + shift[1]);
//		System.out.println(transform);
		
		this.controller.affineTransformSelectedObjects(startingPoint, p1, shift, transform2x2, copyAndTransform, inPreviewMode);
		this.controller.changeDisplayTool(this.affineTool);
	}
	
	private Point2D.Double startToOrigin(Point2D.Double p)
	{
		p.x -= startingPoint.x;
		p.y -= startingPoint.y;
		return p;
	}
	
	private Point2D.Double originToStart(Point2D.Double p)
	{
		p.x += startingPoint.x;
		p.y += startingPoint.y;
		return p;
	}
	
	private RMatrix reflectAcrossX(RMatrix t)
	{		
		RMatrix reflect = new RMatrix(3,3);
		reflect.set(0, 0, 1);
		reflect.set(1, 1, -1);
		reflect.set(2, 2, 1);
		
		return reflect.product(t);
	}
}
