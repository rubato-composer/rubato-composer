/*
 * Copyright (C) 2005 Gérard Milmeister
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

package org.rubato.base;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.rubato.composer.RunInfo;
import org.rubato.composer.rubette.RubetteModel;
import org.rubato.math.yoneda.Denotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Interface for Rubettes.
 * Any implementation of a Rubette should derive AbstractRubette.
 * @see AbstractRubette
 * 
 * @author Gérard Milmeister
 */

public interface Rubette {

    /**
     * Initializes Rubette.
     * This method is called when a Rubette is instantiated
     * as a prototype.
     */
    public void init();

    /**
     * Runs the Rubette.
     * This is the heart of the Rubette and implements the
     * actual computation. Input values are retrieved with the
     * getInput method and output values are stored using the
     * setOutput method. The runInfo parameter object has a method
     * stopped() which should be called regularly, and, in the case
     * it returns false, the run() method should exit gracefully.
     * @param runInfo contains information about the Runner that executes
     *        the network
     */
    public void run(RunInfo runInfo);

    /**
     * Returns the group this Rubette belongs to.
     */
    public String getGroup();
    
    /**
     * Returns the name of the Rubette.
     */
    public String getName();

    /**
     * Creates a new instance from a protoype.
     */
    public Rubette newInstance();

    /**
     * Creates a duplicate from this rubette instance.
     * All properties are copied if possible.
     */
    public Rubette duplicate();
    
    /**
     * Creates a Rubette from an XML description starting with <code>element</code>.
     */
    public Rubette fromXML(XMLReader reader, Element element);

    /**
     * Writes an XML description of this rubette.
     * This method should write XML for the configuration of this
     * Rubette.
     */
    public void toXML(XMLWriter writer);

    /**
     * Returns an icon for this Rubette.
     */
    public ImageIcon getIcon();

    /**
     * Returns true iff this Rubette has a properties dialog.
     * Properties reflect the configuration of this Rubette,
     * any changes in the properties dialog may affect the
     * computation.
     */
    public boolean hasProperties();

    /**
     * Returns the Swing component for the properties dialog.
     * If this Rubette has no properties dialog, simply return null.
     */
    public JComponent getProperties();

    /**
     * Makes changes in the properties dialog permanent.
     * @return true iff the values in the properties dialog are correct
     */
    public boolean applyProperties();

    /**
     * Reverts values in the properties dialog to the values in the Rubette.
     */
    public void revertProperties();

    /**
     * Returns true iff this Rubette has a view.
     * A view should provide a visual (or aural) representation of the
     * configuration (resp. values) of the Rubette, but must never
     * affect the computation.
     */
    public boolean hasView();

    /**
     * Returns the Swing component for the view.
     * If this Rubette has no view, simply return null.
     */
    public JComponent getView();

    /**
     * Updates the view reflecting the changes of the values in the Rubette.
     * If there is no view, do nothing.
     */
    public void updateView();

    /**
     * Returns true iff this Rubette has an info label.
     * The info label is a short string that is displayed
     * in the JRubette.
     */
    public boolean hasInfo();

    /**
     * Returns the info string for the info label.
     * If this Rubette has no info label, simply return null.
     */
    public String getInfo();

    /**
     * Returns a short description.
     * The short description is shown as a tooltip over the JRubette.
     */
    public String getShortDescription();

    /**
     * Returns a long description.
     * The long description is shown in the text area below the
     * Rubette list, if this Rubette is selected in the list.
     */
    public String getLongDescription();

    /**
     * Returns the tooltip for the input connector number <code>i</code>. 
     */
    public String getInTip(int i);

    /**
     * Returns the tooltip for the output connector number <code>i</code>. 
     */
    public String getOutTip(int i);

    /**
     * Sets the number of input connectors.
     * This method should be called with default value in the init() method.
     * It can be called afterwards if the number of connectors changes.
     */
    public void setInCount(int n);

    /**
     * Returns the current number of input connectors.
     */
    public int getInCount();

    /**
     * Returns the input denotator at input connector number <code>i</code>. 
     * This is usually called at the beginning of the run() method
     * to get the input values. The run() method must check the
     * return value which may be null.
     */
    public Denotator getInput(int i);

    /**
     * Sets the number of output connectors.
     * This method should be called with default value in the init() method.
     * It can be called afterwards if the number of connectors changes.
     */
    public void setOutCount(int n);

    /**
     * Returns the current number of output connectors.
     */
    public int getOutCount();

    /**
     * Stores the output denotator <code>d</code> for output connector number <code>i</code>.
     * This is usually called at the end of the run() method to store
     * the result of the computation.
     */
    public void setOutput(int i, Denotator d);

    /**
     * Returns the output denotator of connectir number <code>i</code>.
     * This is usually called by the Runner.
     */
    public Denotator getOutput(int i);

    /**
     * Returns the RubetteModel, that this Rubette is attached to.
     * This may be useful to retrieve information about the
     * environment.
     */
    public RubetteModel getModel();

    /**
     * Adds an error string to the current error state.
     * If an error occurs in the run() method, this method
     * should be called with a short description of the error.
     */
    public void addError(String msg, Object ... objects);

    /**
     * Returns a list of the current errors.
     * This is called by the Runner. If no error has occurred, it
     * returns an empty list, but never null.
     */
    public List<String> getErrors();

    /**
     * Removes all errors from the error list.
     * This is called by the Runner, before the run() method is
     * executed.
     */
    public void clearErrors();

    /**
     * Returns true iff any error has occurred.
     * This is called by the Runner, after the run() method has
     * been executed.
     */
    public boolean hasErrors();

    /**
     * Sets the model this Rubette is attached to.
     * This is called by the Composer, and should, in general,
     * never be used by a Rubette implementation.
     */
    public void setModel(RubetteModel model);
}