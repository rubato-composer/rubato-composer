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

import static org.rubato.xml.XMLConstants.FORM;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.util.JPropertiesTable;
import org.rubato.rubettes.util.SimpleFormFinder;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * A table in which alteration dimensions are represented as rows.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class JAlterationDimensionsTable extends JPropertiesTable {
	
	private AlterationRubette rubette;
	private SimpleFormFinder simpleFormFinder;
	private JComboBox simpleFormsBox;
	
	private List<SimpleForm> selectedForms;
	private List<Double> startPercentages;
	private List<Double> endPercentages;
	private List<SimpleForm> relativeToForms;
	private boolean global;
	private List<Object> startDegreesBuffer;
	private List<Object> endDegreesBuffer;
	
	private static final String[] columnNames = new String[]{"form","start degree","end degree","relative to"};
	
	/**
	 * Creates a table where the alteration dimensions of the specified AlterationRubette are
	 * saved.
	 * 
	 * @param rubette the corresponding AlterationRubette 
	 */
	public JAlterationDimensionsTable(AlterationRubette rubette) {
		super(columnNames);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.rubette = rubette;
		this.simpleFormFinder = new SimpleFormFinder(null);
		
		this.selectedForms = new ArrayList<SimpleForm>();
		this.startPercentages = new ArrayList<Double>();
		this.endPercentages = new ArrayList<Double>();
		this.relativeToForms = new ArrayList<SimpleForm>();
		this.global = false;
	}

	/**
	 * Sets the form of the denotators to be altered.
	 */
	public void setParentForm(PowerForm form) {
		this.simpleFormFinder.setParentForm(form);
		this.updateComboBoxesInTable();
	}
	
	private void updateComboBoxesInTable() {
		this.stopEditing();
		List<SimpleForm> simpleForms = this.simpleFormFinder.getSimpleForms();
		if (simpleForms != null && simpleForms.size() > 0) {
			this.simpleFormsBox = new JComboBox(simpleForms.toArray());
			this.rubette.setStatuslineText("");
		} else {
			this.simpleFormsBox = new JComboBox();
			this.rubette.setStatuslineText("The selected input form contains no subforms of type SimpleForm");
		}
		this.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(this.simpleFormsBox));
		this.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(this.simpleFormsBox));
	}
	
	/**
	 * Updates the local degrees for each alteration dimension according to the specified
	 * global degrees. The previous local degrees are buffered for restoring.
	 * 
	 * @param startDegree - the global start degree
	 * @param endDegree - the global end degree
	 */
	public void setGlobalDegree(double startDegree, double endDegree) {
		this.global = true;
		this.updateAndBufferDegreeFields(startDegree, endDegree);
	}
	
	/**
	 * Restores the previously buffered local degrees.
	 */
	public void removeGlobalDegree() {
		this.global = false;
		this.resetDegreeFieldsToBufferedValues();
	}
	
	/**
	 * Defines that the local degree cells are not editable, if a global degree is defined.
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == 1 || column == 2) {
			return !this.global;
		} else {
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateAndBufferDegreeFields(double startDegree, double endDegree) {
		this.stopEditing();
		this.startDegreesBuffer = new ArrayList();
		this.endDegreesBuffer = new ArrayList();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			this.startDegreesBuffer.add(model.getValueAt(i, 1));
			this.endDegreesBuffer.add(model.getValueAt(i, 2));
			model.setValueAt(new Double(startDegree).toString(), i, 1);
			model.setValueAt(new Double(endDegree).toString(), i, 2);
		}
	}
	
	private void resetDegreeFieldsToBufferedValues() {
		int resetCount = Math.min(this.startDegreesBuffer.size(), this.getModel().getRowCount());
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		for (int i = 0; i < resetCount; i++) {
			model.setValueAt(this.startDegreesBuffer.get(i), i, 1);
			model.setValueAt(this.endDegreesBuffer.get(i), i, 2);
		}
	}
	
	/*
	 * Adds a row with default values to the table.
	 */
	protected void addDimension() {
		this.addDimension(null, 0.0, 1.0, null);
	}
	
	/*
	 * Adds a row containing the specified parameters to the table.
	 */
	protected void addDimension(SimpleForm form, double degree) {
		this.addDimension(form, degree, degree, form);
	}
	
	/*
	 * Adds a row containing the specified parameters to the table.
	 */
	protected void addDimension(SimpleForm form, double startPercentage, double endPercentage, SimpleForm relative) {
		if (this.simpleFormsBox != null) {
			String start = new Double(startPercentage).toString();
			String end = new Double(endPercentage).toString();
			Object[] newRow = new Object[] {form, start, end, relative};
			DefaultTableModel model = (DefaultTableModel)this.getModel();
			model.addRow(newRow);
		} else {
			this.rubette.setStatuslineText("Please select input form first");
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean applyChanges() {
		this.stopEditing();
		List<SimpleForm> tempSimpleForms = new ArrayList<SimpleForm>();
		List<Double> tempStartPercentages = new ArrayList<Double>();
		List<Double> tempEndPercentages = new ArrayList<Double>();
		List<SimpleForm> tempRelativeToForms = new ArrayList<SimpleForm>();
		//List<Boolean> tempDoubtUpFlags = new ArrayList<Boolean>();
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			SimpleForm currentForm = (SimpleForm) model.getValueAt(i, 0);
			if (currentForm == null) {
				this.rubette.setStatuslineText("Not all SimpleForms selected in the first column");
			}
			tempSimpleForms.add(currentForm);
			
			Double currentStartPercentage = new Double((String) model.getValueAt(i, 1));
			/*if (currentStartPercentage < 0 || currentStartPercentage > 1) {
				this.rubette.setStatuslineText("Start degree in row "+i+" not between 0 and 1");
				return false;
			}*/
			tempStartPercentages.add(currentStartPercentage);
			
			Double currentEndPercentage = new Double((String) model.getValueAt(i, 2));
			/*if (currentEndPercentage < 0 || currentEndPercentage > 1) {
				this.rubette.setStatuslineText("End degree in row "+i+" not between 0 and 1");
				return false;
			}*/
			tempEndPercentages.add(currentEndPercentage);
			
			currentForm = (SimpleForm) model.getValueAt(i, 3);
			if (currentForm == null) {
				this.rubette.setStatuslineText("Not all SimpleForms selected in the last column");
			}
			tempRelativeToForms.add(currentForm);
			
			//tempDoubtUpFlags.add((Boolean) model.getValueAt(i, 4)); 
		}
		//checken, dass nicht mehrmals gleiche form gew�hlt
		HashSet simpleFormSet = new HashSet(tempSimpleForms);
		if (simpleFormSet.size() != tempSimpleForms.size()) {
			this.rubette.setStatuslineText("The selected SimpleForms in the left column are not all different");
			return false;
		}
		this.selectedForms = tempSimpleForms;
		this.startPercentages = tempStartPercentages;
		this.endPercentages = tempEndPercentages;
		this.relativeToForms = tempRelativeToForms;
		//this.doubtUpFlags = tempDoubtUpFlags;
		return true;
	}
	
	public void revertChanges() {
		this.stopEditing();
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		int appliedRowCount = this.selectedForms.size();
		model.setNumRows(appliedRowCount);
		for (int i = 0; i < appliedRowCount; i++) {
			model.setValueAt(this.selectedForms.get(i), i, 0);
			model.setValueAt(this.startPercentages.get(i).toString(), i, 1);
			model.setValueAt(this.endPercentages.get(i).toString(), i, 2);
			model.setValueAt(this.relativeToForms.get(i), i, 3);
			//model.setValueAt(this.doubtUpFlags.get(i), i, 4);
		}
	}
	
	/**
	 * Returns the number of alteration dimensions.
	 */
	public int dimensionCount() {
		return this.selectedForms.size();
	}
	
	/**
	 * Returns the SimpleForm to be altered by the specified dimension.
	 * @param index - the dimension index
	 */
	public SimpleForm getForm(int index) {
		return this.selectedForms.get(index);
	}
	
	/**
	 * Returns the path of the SimpleForm to be altered by the specified dimension.
	 * @param index - the dimension index
	 */
	public int[] getPath(int index) {
		return this.simpleFormFinder.getPath(this.getForm(index));
	}
	
	public int[][] getPaths() {
		int dimensions = this.dimensionCount();
		int[][] paths = new int[dimensions][];
		for (int i = 0; i < dimensions; i++) {
			paths[i] = this.getPath(i);
		}
		return paths;
	}
	
	/**
	 * Returns the path of the element of the SimpleForm to be altered by the specified dimension.
	 * @param index - the dimension index
	 */
	public int[] getElementPath(int index) {
		return this.simpleFormFinder.getPathForElement(this.selectedForms.get(index));
	}
	
	/**
	 * Returns an array with the paths of the elements of all the SimpleForms to be altered.
	 */
	public int[][] getElementPaths() {
		int dimensions = this.dimensionCount();
		int[][] paths = new int[dimensions][];
		for (int i = 0; i < dimensions; i++) {
			paths[i] = this.getElementPath(i);
		}
		return paths;
	}
	
	/**
	 * Returns the start degree of the specified alteration dimension.
	 * @param index - the dimension index
	 */
	public double getStartPercentage(int index) {
		return this.startPercentages.get(index).doubleValue();
	}
	
	/**
	 * Returns the end degree of the specified alteration dimension.
	 * @param index - the dimension index
	 */
	public double getEndPercentage(int index) {
		return this.endPercentages.get(index).doubleValue();
	}
	
	/**
	 * Returns an array with the local start degrees of all alteration dimensions.
	 */
	public double[] getStartDegrees() {
		double[] degrees = new double[this.startPercentages.size()];
		for (int i = 0; i < degrees.length; i++) {
			degrees[i] = this.startPercentages.get(i).doubleValue();
		}
		return degrees;
	}
	
	/**
	 * Returns an array with the local end degrees of all alteration dimensions.
	 */
	public double[] getEndDegrees() {
		double[] degrees = new double[this.endPercentages.size()];
		for (int i = 0; i < degrees.length; i++) {
			degrees[i] = this.endPercentages.get(i).doubleValue();
		}
		return degrees;
	}
	
	/**
	 * Returns an array with the paths of the SimpleForms relative to which the alteration
	 * takes place (one path for each alteration dimension).
	 */
	public int[][] getRelativeToFormPaths() {
		int[][] paths = new int[this.relativeToForms.size()][];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = this.simpleFormFinder.getPathForElement(relativeToForms.get(i));
		}
		return paths;
	}
	
	/**
	 * Returns an array with the paths of the SimpleForms relative to which the alteration
	 * takes place (one path for each different form).
	 */
	public int[][] getDifferentRelativeToFormPaths() {
		Set<SimpleForm> differentForms = this.getDifferentRelativeToForms();
		Iterator<SimpleForm> differentFormsIterator = this.getDifferentRelativeToForms().iterator();
		int formCount = differentForms.size();
		int[][] paths = new int[formCount][];
		for (int i = 0; i < formCount; i++) {
			paths[i] = this.simpleFormFinder.getPathForElement(differentFormsIterator.next());
		}
		return paths;
	}
	
	private Set<SimpleForm> getDifferentRelativeToForms() {
		Set<SimpleForm> differentRelativeToForms = new HashSet<SimpleForm>(); 
		for (int i = 0; i < this.relativeToForms.size(); i++) {
			differentRelativeToForms.add(this.relativeToForms.get(i));
		}
		return differentRelativeToForms;
	}
	
	private static final String DIMENSION = "Dimension";
	private static final String START = "startDegree";
	private static final String END = "endDegree";
	
	protected void toXML(XMLWriter writer) {
		for (int i = 0; i < this.dimensionCount(); i++) {
			writer.openBlock(DIMENSION,
				START, this.startPercentages.get(i),
				END, this.endPercentages.get(i));
			writer.writeFormRef(this.selectedForms.get(i));
			writer.writeFormRef(this.relativeToForms.get(i));
			writer.closeBlock();
		}
	}
	
	protected void fromXML(XMLReader reader, Element element) {
		Element nextSibling = XMLReader.getNextSibling(element, DIMENSION);
		
		while (nextSibling != null) {
			double startDegree = XMLReader.getRealAttribute(nextSibling, START, 0);
			double endDegree = XMLReader.getRealAttribute(nextSibling, END, 1);
			
			Element child = XMLReader.getChild(nextSibling, FORM);
			SimpleForm selectedForm = (SimpleForm) reader.parseAndResolveForm(child);
			
			child = XMLReader.getNextSibling(child, FORM);
			SimpleForm relativeToForm = (SimpleForm) reader.parseAndResolveForm(child);
			
			this.addDimension(selectedForm, startDegree, endDegree, relativeToForm);
			nextSibling = XMLReader.getNextSibling(nextSibling, DIMENSION);
		}
		
		this.applyChanges();
	}

}
