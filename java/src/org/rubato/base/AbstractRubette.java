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

import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.rubato.composer.RunInfo;
import org.rubato.composer.rubette.Link;
import org.rubato.composer.rubette.RubetteModel;
import org.rubato.math.yoneda.Denotator;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Abstract base class for Rubettes.
 * Any implementation of a Rubette must derive from this class.
 * The methods are divided into four class:
 * <ol>
 * <li>Methods that must be implemented</li>
 * <li>Methods that can overridden, and provide default implementations</li>
 * <li>Methods that can be used, but cannot be overridden</li>
 * <li>Methods that are for internal use, and must not be used by an implementation</li>
 * </ol>
 * 
 * @author Gérard Milmeister
 */

public abstract class AbstractRubette implements Rubette {
    
    // These methods must be implemented by a concrete Rubette. 

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
    public abstract void run(RunInfo runInfo);
    

    /**
     * Returns the name of the rubette.
     */
    public abstract String getName();
    

    /**
     * Creates a rubette from an XML description starting with <code>element</code>.
     */
    public abstract Rubette fromXML(XMLReader reader, Element element);
    

    /**
     * Writes an XML description of this rubette.
     * This method should write XML for the configuration of this
     * Rubette.
     */
    public abstract void toXML(XMLWriter writer);
    
    
    // These methods can be overridden by a concrete Rubette

    /**
     * Initializes Rubette.
     * This method is called when a Rubette is instantiated
     * as a prototype. It does nothing by default.
     */
    public void init() { /* default does nothing */ }
    
    
    /**
     * Creates a duplicate from this rubette instance.
     * All properties are copied if possible.
     * This must be correctly implemented in order that many functions
     * work as expected.
     */
    public abstract Rubette duplicate();;
    
    
    /**
     * Returns the group this Rubette belongs to.
     * The default group is "Other".
     */
    public String getGroup() {
        return RubatoConstants.OTHER_GROUP;
    }

    
    /**
     * Returns an icon for this Rubette.
     */
    public ImageIcon getIcon() {
        return null;
    }


    /**
     * Returns true iff this rubette has a properties dialog.
     * Properties reflect the configuration of this Rubette,
     * any changes in the properties dialog may affect the
     * computation.
     */
    public boolean hasProperties() {
        return false;
    }
    

    /**
     * Returns the Swing component for the properties dialog.
     * If this rubette has no properties dialog, simply return null.
     */
    public JComponent getProperties() {
        return null;
    }
    
    
    /**
     * Makes changes in the properties dialog permanent.
     * @return true iff the values in the properties dialog are correct
     */
    public boolean applyProperties() {
        return true;
    }
    
    
    /**
     * Reverts values in the properties dialog to the values in the Rubette.
     */
    public void revertProperties() { /* default does nothing */ }

    
    /**
     * Returns true iff this Rubette has a view.
     * A view should provide a visual (or aural) representation of the
     * configuration (resp. values) of the Rubette, but must never
     * affect the computation.
     */
    public boolean hasView() {
        return false;
    }

    
    /**
     * Returns the Swing component for the view.
     * If this Rubette has no view, this simply returns null.
     */
    public JComponent getView() {
        return null;
    }
    
    
    /**
     * Updates the view reflecting the changes of the values in the Rubette.
     * If there is no view, do nothing.
     */
    public void updateView() { /* default does nothing */ }

    
    /**
     * Returns true iff this Rubette has an info label.
     * The info label is a short string that is displayed
     * in the JRubette.
     */
    public boolean hasInfo() {
        return false;
    }

    
    /**
     * Returns the info string for the info label.
     * If this Rubette has no info label, this simply returns null.
     */
    public String getInfo() {
        return null;
    }
    

    /**
     * Returns a short description.
     * The short description is shown as a tooltip over the JRubette.
     * The default text is the name of the rubette.
     */
    public String getShortDescription() {
        return getName();
    }
    
    
    /**
     * Returns a long description.
     * The long description is shown in the text area below the
     * Rubette list, if this Rubette is selected in the list.
     */
    public String getLongDescription() {
        return Messages.getString("AbstractRubette.nodescription")+getName()+"."; //$NON-NLS-1$//$NON-NLS-2$
    }
    
    
    /**
     * Returns the tooltip for the input connector number <code>i</code>. 
     */
    public String getInTip(int i) {
        return "Input #"+i; //$NON-NLS-1$
    }


    /**
     * Returns the tooltip for the output connector number <code>i</code>. 
     */
    public String getOutTip(int i) {
        return "Output #"+i; //$NON-NLS-1$
    }

    
    /**
     * The default constructor must/should perform initializations specfic to
     * each rubette instance, for example set the number of inputs and outputs, or
     * initializing the rubette state. 
     */
    public AbstractRubette() { /* default does nothing */ }
    
    
    // These methods cannot be overridden but can be used by a concrete rubette. 

    /**
     * Creates a new instance from a protoype.
     */
    public Rubette newInstance() {
        try {
            return getClass().newInstance();
        }
        catch (InstantiationException e) {
            System.out.println(e.getStackTrace());
            throw new Error("Unexpected internal error: consult the console log"); //$NON-NLS-1$
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getStackTrace());
            throw new Error("Unexpected internal error: consult the console log"); //$NON-NLS-1$
        }
    }

    
    /**
     * Sets the number of input connectors.
     * This method should be called with default value in the init() method.
     * It can be called afterwards if the number of connectors changes.
     */
    public final void setInCount(int n) {
        inCount = n;
    }
    

    /**
     * Returns the current number of input connectors.
     */
    public final int getInCount() {
        return inCount;
    }


    /**
     * Returns the input denotator at input connector number <code>i</code>. 
     * This is usually called at the beginning of the run() method
     * to get the input values. The run() method must check the
     * return value which may be null.
     */
    public final Denotator getInput(int i) {
        Link inLink = model.getInLink(i);
        if (inLink == null) {
            return null;
        }
        return inLink.getSrcModel().getRubette().getOutput(inLink.getSrcPos());
    }
    
    
    /**
     * Sets the number of output connectors.
     * This method should be called with default value in the init() method.
     * It can be called afterwards if the number of connectors changes.
     */
    public final void setOutCount(int n) {
        outCount = n;
        output = new Denotator[outCount];
        for (int j = 0; j < outCount; j++) {
            output[j] = null;
        }        
    }
    
    
    /**
     * Returns the current number of output connectors.
     */
    public final int getOutCount() {
        return outCount;
    }


    /**
     * Stores the output denotator <code>d</code> for output connector number <code>i</code>.
     * This is usually called at the end of the run() method to store
     * the result of the computation.
     */
    public final void setOutput(int i, Denotator d) {
        output[i] = d;
    }
    
    
    /**
     * Returns the output denotator of connectir number <code>i</code>.
     * This is usually called by the Runner.
     */
    public final Denotator getOutput(int i) {
        return output[i];
    }
    
    
    /**
     * Returns the RubetteModel, that this Rubette is attached to.
     * This may be useful to retrieve information about the
     * environment.
     */
    public final RubetteModel getModel() {
        return model;
    }
    
    
    /**
     * Adds an error string to the current error state.
     * If an error occurs in the run() method, this method
     * should be called with a short description of the error.
     */
    public final void addError(String msg, Object ... objects) {
        errors.add(TextUtils.replaceStrings(msg, objects));
    }


    /**
     * Adds an exception message to the current error state.
     * A period (".") is appended if there is none in the message.
     */
    public final void addError(Exception e) {
        String s = e.getMessage().trim();
        if (!s.substring(s.length()-1).equals(".")) { //$NON-NLS-1$
            s += "."; //$NON-NLS-1$
        }
        errors.add(s);
    }


    /**
     * Returns a list of the current errors.
     * This is called by the Runner. If no error has occurred, it
     * returns an empty list, but never null.
     */
    public final List<String> getErrors() {
        return errors;
    }


    /**
     * Removes all errors from the error list.
     * This is called by the Runner, before the run() method is
     * executed.
     */
    public final void clearErrors() {
        errors.clear();
    }


    /**
     * Returns true iff any error has occurred.
     * This is called by the Runner, after the run() method has
     * been executed.
     */
    public final boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    
    // These methods should not be used by a concrete Rubette.
    
    /**
     * Sets the model this Rubette is attached to.
     * This is called by the Composer, and should, in general,
     * never be used by a Rubette implementation.
     */
    public final void setModel(RubetteModel model) {
        this.model = model;
    }
    
    
    private RubetteModel model    = null;
    private Denotator[]  output;
    private List<String> errors   = new LinkedList<String>();
    private int          inCount  = 1;
    private int          outCount = 1;
}
