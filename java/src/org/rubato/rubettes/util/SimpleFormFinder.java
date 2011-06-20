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
import java.util.List;

import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;

/**
 * Searches the subforms of the given parent form for Simple forms and keeps track of their
 * shortest possible paths, e.g. for Pitch in MacroScore: {0,1}.
 * 
 * @author Florian Thalmann
 *
 */
public class SimpleFormFinder {
	
	private int maxRecursion;
	private PowerForm parentForm;
    private List<SimpleForm> simpleForms;
    private List<List<Integer>> simpleFormPaths;
	
    public SimpleFormFinder() {
    }
    
    /**
     * Creates a SimpleFormFinder for the specified parent form.
     * 
     * @param parentForm - the form, the tree of which has to be searched
     */
	public SimpleFormFinder(PowerForm parentForm) {
		this.setParentForm(parentForm);
		this.setMaxRecursion(10);
	}
	
	public SimpleFormFinder(PowerForm parentForm, int maxRecursion) {
		this.setParentForm(parentForm);
		this.setMaxRecursion(maxRecursion);
	}
	
	/**
	 * Sets a new parent form and searches its tree for Simple forms.
	 * 
	 * @param parentForm - the new parent form
	 */
	public void setParentForm(PowerForm parentForm) {
		this.parentForm = parentForm;
		this.updateSimpleFormsAndPaths();
	}
	
	/**
	 * Returns the parent form currently set.
	 */
	public PowerForm getParentForm() {
		return this.parentForm;
	}
	
	/**
	 * Sets the maximum number of recursion levels to be searched to the specified value.
	 * 
	 * @param maxRecursion - the maximum number of recursion levels
	 */
	public void setMaxRecursion(int maxRecursion) {
		if (maxRecursion != this.maxRecursion) {
			this.maxRecursion = maxRecursion;
			this.updateSimpleFormsAndPaths();
		}
	}
	
	/*
	 * Resets forms and paths and calls the recursive method starting at this.parentForm 
	 */
	private void updateSimpleFormsAndPaths() {
		if (this.parentForm != null) {
			this.simpleForms = new ArrayList<SimpleForm>();
			this.simpleFormPaths = new ArrayList<List<Integer>>();
			this.updateSimpleFormsAndPaths(this.parentForm.getForm(), new ArrayList<Integer>(), 0);
		}
	}
	
	/*
	 * Recursive method. Adds the given form to this.simpleForms, if it is of type Simple.
	 * Then if this.maxRecursion is not yet reached, it calls the method for all subForms. 
	 */
	private void updateSimpleFormsAndPaths(Form form, List<Integer> path, int recursion) {
		if (form.getClass() == SimpleForm.class) {
			this.simpleForms.add((SimpleForm) form);
			this.simpleFormPaths.add(path);
		}
		if (recursion < this.maxRecursion) {
			List<Form> subForms = form.getForms();
			for (int i = 0; i < subForms.size(); i++) {
				Form currentSubForm = subForms.get(i);
				List<Integer> currentPath = new ArrayList<Integer>(path);
				currentPath.add(new Integer(i));
				this.updateSimpleFormsAndPaths(currentSubForm, currentPath, recursion+1);
			}
		}
	}
	
	/**
	 * Returns a List containing the found SimpleForms.
	 */
	public List<SimpleForm> getSimpleForms() {
		return this.simpleForms;
	}
	
	/**
	 * Returns a List with the paths of the found SimpleForms. Same order as getSimpleForms().
	 */
	public List<List<Integer>> getSimpleFormPaths() {
		return this.simpleFormPaths;
	}
	
	public int[][] getSimpleFormArrayPaths() {
		int[][] paths = new int[this.simpleForms.size()][];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = this.getPath(this.simpleForms.get(i));
		}
		return paths;
	}
	
	public int[][] getElementPaths() {
		int[][] elementPaths = new int[this.simpleForms.size()][];
		for (int i = 0; i < elementPaths.length; i++) {
			elementPaths[i] = this.getPathForElement(this.simpleForms.get(i));
		}
		return elementPaths;
	}
	
	/**
	 * Returns the path for the specified SimpleForm, if it has been found in the parent form's
	 * tree.
	 * 
	 * @param form - the form to be found
	 */
	public int[] getPath(SimpleForm form) {
		int index = this.simpleForms.indexOf(form);
		if (index >= 0) {
			return this.pathToArray(this.simpleFormPaths.get(index));
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the path for the ModuleElement inside the specified SimpleForm,
	 * e.g. {...,0}, if the form has been found in the parent form's tree.
	 * 
	 * @param form the Simple form, the path of element of which has to be returned
	 */
	public int[] getPathForElement(SimpleForm form) {
		int index = this.simpleForms.indexOf(form);
		if (index >= 0) {
			List<Integer> path = new ArrayList<Integer>(this.simpleFormPaths.get(index));
			path.add(new Integer(0));
			return this.pathToArray(path);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns an array conversion of a List of List paths
	 */
	public int[][] pathsToArray(List<List<Integer>> paths) {
		int[][] arrayPaths = new int[paths.size()][];
		for (int i = 0; i < arrayPaths.length; i++) {
			arrayPaths[i] = this.pathToArray(paths.get(i));
		}
		return arrayPaths;
	}
	
	/**
	 * Returns an array conversion of a List path
	 */
	public int[] pathToArray(List<Integer> listPath) {
		int pathLength = listPath.size();
		int[] arrayPath = new int[pathLength];
		for (int i = 0; i < pathLength; i++) {
			arrayPath[i] = listPath.get(i).intValue();
		}
		return arrayPath;
	}

}
