/*
 * Copyright (C) 2006 Florian Thalmann
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

package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds a number of musical scales as values, defined as series of intervals (double[]). The
 * corresponding keys are the scale's names (String).
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class ScaleMap extends HashMap<String, double[]> {
	
	private ArrayList<Object[]> definitions;
	
	public ScaleMap() {
		//order is important, could also be read from a file...
		this.definitions = new ArrayList<Object[]>();
		this.definitions.add(new Object[] {"ionian", new double[] {2,2,1,2,2,2,1}});
		this.definitions.add(new Object[] {"dorian", new double[] {2,1,2,2,2,1,2}});
		this.definitions.add(new Object[] {"phrygian", new double[] {1,2,2,2,1,2,2}});
		this.definitions.add(new Object[] {"lydian", new double[] {2,2,2,1,2,2,1}});
		this.definitions.add(new Object[] {"mixolydian", new double[] {2,2,1,2,2,1,2}});
		this.definitions.add(new Object[] {"aeolian", new double[] {2,1,2,2,1,2,2}});
		this.definitions.add(new Object[] {"locrian", new double[] {1,2,2,1,2,2,2}});
		this.definitions.add(new Object[] {"whole tone", new double[] {2,2,2,2,2,2}});
		this.definitions.add(new Object[] {"diminished1", new double[] {1,2,1,2,1,2,1,2}});
		this.definitions.add(new Object[] {"diminished2", new double[] {2,1,2,1,2,1,2,1}});
		this.definitions.add(new Object[] {"major pentatonic", new double[] {2,2,3,2,3}});
		this.definitions.add(new Object[] {"minor pentatonic", new double[] {3,2,2,3,2}});
		this.definitions.add(new Object[] {"japanese", new double[] {2,1,4,1,4}});
		this.initMap();
	}
	
	private void initMap() {
		for (int i = 0; i < this.definitions.size(); i++) {
			Object[] currentDefinition = this.definitions.get(i);
			String currentName = (String) currentDefinition[0];
			double[] currentIntervals = (double[]) currentDefinition[1];
			this.put(currentName, currentIntervals);
		}
	}
	
	/**
	 * Returns an array with the names of all scales with the specified number of intervals. The
	 * names can then be used as keys for getting the values (double[]) saved in the map.
	 * 
	 * @param numberOfIntervals - the number of intervals in a scale
	 */
	public Object[] getScaleNames(int numberOfIntervals) {
		ArrayList<String> scaleNames = new ArrayList<String>();
		scaleNames.add("custom");
		for (int i = 0; i < this.definitions.size(); i++) {
			String currentName = (String) this.definitions.get(i)[0];
			double[] currentScale = this.get(currentName);
			if (currentScale.length == numberOfIntervals) {
				scaleNames.add(currentName);
			}
		}
		return scaleNames.toArray();
	}

}
