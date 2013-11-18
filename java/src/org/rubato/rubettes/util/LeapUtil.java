package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;

public class LeapUtil {
	
	public enum Axis {X_AXIS(0), Y_AXIS(1), Z_AXIS(2);
		private int axis;
		Axis(int a) {
			this.axis = a;
		}
		public double getAxisVal(Finger f) {
			return f.tipPosition().get(axis);
		}
	};
	
	public enum Operation {GREATER_THAN(1), LESS_THAN(-1);
		private int val;
		Operation(int o) {
			val = o;
		}
		public Boolean compare(double v1, double v2) {
			return ((v1 - v2) * val) > 0;
		}
	};
	
	private static LeapSpace leapSpace = new LeapSpace();
	
	public static Boolean onScreen(Finger finger) {
		return leapSpace.OnScreen(finger.tipPosition());
	}
	
	// Keeps all points if for example, z position is greater than 0
	public static List<Finger> keepFingerIf(List<Finger> fingers, Axis axis, Operation op, double val) {
		List<Finger> temp = new ArrayList<Finger>();
		for (Finger f : fingers) {
			if (op.compare(axis.getAxisVal(f), val)) {
				temp.add(f);
			}
		}
		return temp;
	}
	
	public static PointND fingerToScreenPoint(Finger f) {
		return leapSpace.ToScreenPoint(f.tipPosition());
	}
	
	public static List<Finger> removeOffscreenFingers(List<Finger> fingers) {
		List<Finger> temp = new ArrayList<Finger>();
		for (Finger f : fingers) {
			if (onScreen(f)) {
				temp.add(f);
			}
		}
		return temp;
	}
	
	public static List<Finger> FingerListToJavaList(FingerList fingers) {
		List<Finger> fingersOut = new ArrayList<Finger>();
		Iterator<Finger> it = fingers.iterator();
		while (it.hasNext()) {
			Finger f = it.next();
			fingersOut.add(f);
		}
		return fingersOut;
	}
	
	// returns fingers sorted by decreasing z until either count number of fingers are found or we run out of fingers
	public static List<Finger> getFrontmostFingers(List<Finger> fingers, int count) {
		List<Finger> maxFingers = new ArrayList<Finger>();
		while (count > 0 && fingers.size() > 0) {
			List<Finger> temp = new ArrayList<Finger>();
			Finger maxFinger = new Finger();
			for (Finger f : fingers) {
				if (!maxFinger.isValid() || maxFinger.tipPosition().getZ() < f.tipPosition().getZ()) {
					if (maxFinger.isValid()) {
						temp.add(maxFinger);
					}
					maxFinger = f;
				}
				else {
					temp.add(f);
				}
			}
			maxFingers.add(maxFinger);
			fingers = temp;
			count--;
		}
		
		return maxFingers;
	}
}
