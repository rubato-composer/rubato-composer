/*
 * Copyright (C) 2004 Florian Thalmann
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
import java.util.TreeMap;

/**
 * A utility class for benchmark tests. Example:
 * 
 * <code>PerformanceCheck.reset();
 * for (int i = 0; i < 10; i++) {
 * 	PerformanceCheck.startTask("task1");
 *  ... do something ...
 *  PerformanceCheck.startTask("task2");
 *  ... do something else ...
 * }
 * PerformanceCheck.print();</code>
 * 
 * @author Florian Thalmann
 */
public class PerformanceCheck {
	
	private static ArrayList<String> taskNames = new ArrayList<String>();
	private static TreeMap<String, Double> taskDurations = new TreeMap<String, Double>();
	private static String previousTaskName;
	private static double previousTaskStartTime;
	
	/**
	 * Resets all timers.
	 */
	public static void reset() {
		taskNames = new ArrayList<String>();
		taskDurations = new TreeMap<String, Double>();
		previousTaskName = null;
		previousTaskStartTime = 0;
	}
	
	/**
	 * Starts the timer for the specified task and stops the timer for the previous task. If the
	 * timer for the specified task does not exist yet, it is generated.
	 * 
	 * @param currentTaskName - the name of the started task 
	 */
	public static void startTask(String currentTaskName) {
		if (!taskDurations.containsKey(currentTaskName)) {
			taskNames.add(currentTaskName);
			taskDurations.put(currentTaskName, new Double(0));
		}
		double currentTaskStartTime = System.currentTimeMillis();
		if (previousTaskName != null && taskDurations.containsKey(previousTaskName) && taskDurations.get(previousTaskName) != null) {
			double previousTaskDuration = currentTaskStartTime - previousTaskStartTime;
			double previousTaskSum = taskDurations.get(previousTaskName).doubleValue();
			previousTaskSum += previousTaskDuration;
			taskDurations.put(previousTaskName, new Double(previousTaskSum));
		}
		previousTaskName = currentTaskName;
		previousTaskStartTime = currentTaskStartTime;
	}
	
	/**
	 * Prints the timer states for all known tasks.
	 */
	public static void print() {
		for (int i = 0; i < taskNames.size(); i++) {
			String currentTaskName = taskNames.get(i);
			double taskDuration = taskDurations.get(currentTaskName).doubleValue();
			System.out.println(currentTaskName + ": " + taskDuration/1000 + "s");
		}
	}
	
	/**
	 * Prints the percentage of each timer states for all known tasks.
	 */
	public static void printPercentages() {
		double total = getTotalDuration();
		for (int i = 0; i < taskNames.size(); i++) {
			String currentTaskName = taskNames.get(i);
			double taskDuration = taskDurations.get(currentTaskName).doubleValue();
			System.out.println(currentTaskName + ": " + Math.round(taskDuration/total*100) + "%");
		}
	}
	
	private static double getTotalDuration() {
		double sum = 0;
		for (double currentDuration: taskDurations.values()) {
			sum += currentDuration;
		}
		return sum;
	}

}
