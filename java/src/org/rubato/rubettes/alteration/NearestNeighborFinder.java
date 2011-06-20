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

package org.rubato.rubettes.alteration;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.base.RubatoException;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.alteration.CG.KDTree;
import org.rubato.rubettes.alteration.CG.KeyDuplicateException;
import org.rubato.rubettes.alteration.CG.KeySizeException;

public class NearestNeighborFinder {
	
	private KDTree kdTree;
	private Set<Denotator> neighbors;
	private int[][] elementPaths;
	
	public NearestNeighborFinder(int[][] elementPaths) {
		this.neighbors = new TreeSet<Denotator>();
		this.elementPaths = elementPaths;
	}
	
	public NearestNeighborFinder(PowerDenotator neighbors, int[][] elementPaths) {
		this.elementPaths = elementPaths;
		this.fillKDTree(neighbors.iterator());
	}
	
	public void addNeighbor(Denotator neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public Denotator findNearestNeighbor(Denotator denotator) {
		double[] key = this.generateKey(denotator);
		try {
			return (Denotator) this.kdTree.nearest(key);
		} catch (KeySizeException e) {
			return null;
		}
	}
	
	public void fillKDTree() {
		this.fillKDTree(this.neighbors.iterator());
	}
	
	protected void fillKDTree(Iterator<Denotator> neighbors) {
		this.kdTree = new KDTree(this.elementPaths.length);
		while (neighbors.hasNext()) {
			Denotator currentNeighbor = neighbors.next();
			try {
				double[] key = this.generateKey(currentNeighbor);
				this.kdTree.insert(key, currentNeighbor);
			} catch (KeyDuplicateException e) {
				//e.printStackTrace();
			} catch (KeySizeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private double[] generateKey(Denotator denotator) {
		double[] key = new double[this.elementPaths.length];
		for (int i = 0; i < key.length; i++) {
			//getElementPath verschnellern!!
			int[] currentPath = this.elementPaths[i];
			try {
				key[i] = ((RElement) denotator.getElement(currentPath).cast(RRing.ring)).getValue();
			} catch (RubatoException e) { e.printStackTrace(); }
		}
		return key;
	}
	
}
