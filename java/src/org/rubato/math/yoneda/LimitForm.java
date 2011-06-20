/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
 * Copyright (C) 2002 Stefan Müller
 * Copyright (C) 2002 Stefan Göller
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

package org.rubato.math.yoneda;

import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.io.PrintStream;
import java.util.*;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Limit form class.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class LimitForm extends Form {

    /**
     * Generic form constructor.
     */
    public LimitForm(NameDenotator name, Morphism identifier) {
        super(name, identifier);
    }


    /**
     * Builds a limit identity form using a list of forms.
     */
    public LimitForm(NameDenotator name, List<Form> forms) {
        super(name, new ProperIdentityMorphism(new FormDiagram(forms), LIMIT));
    }


    /**
     * Builds a limit identity form using a list of forms.
     */
    public LimitForm(NameDenotator name, List<Form> forms, List<String> labels) {
        super(name, new ProperIdentityMorphism(new FormDiagram(forms), LIMIT));
        setLabels(labels);
    }

    
    /**
     * Builds a limit identity form using a diagram.
     */
    public LimitForm(NameDenotator name, Diagram diagram) {
        super(name, new ProperIdentityMorphism(diagram, LIMIT));
    }

    
    public int getType() {
        return LIMIT;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof LimitForm) {
            return equals((LimitForm)object);
        }
        else {
            return false;
        }
    }


    public boolean equals(LimitForm f) {
        if (registered && f.registered) {
            return getName().equals(f.getName());
        }
        else {
            return fullEquals(f);
        }
    }


    public boolean fullEquals(LimitForm f) {
        return fullEquals(f, new IdentityHashMap<Object,Object>());
    }


    public boolean fullEquals(LimitForm f, IdentityHashMap<Object,Object> s) {
        if (this == f) {
            return true;
        }
        else if (!getName().equals(f.getName())) {
            return false;
        }
        s.put(this, f);
        return identifier.fullEquals(f.identifier, s);
    }
    

    /**
     * Returns the number of coordinate forms.
     */
    public int getFormCount() {
        return getFormDiagram().getFormCount();
    }
    

    /**
     * Returns a coordinate form.
     * @param i the coordinate position
     * @return the form at coordinate position i
     */
    public Form getForm(int i) {
        return getFormDiagram().getForm(i);
    }
    
    
    /**
     * Returns a coordinate form.
     * @param label the name of the coordinate form
     * @return the form at the coordinate with the given label  
     */
    public Form getForm(String label)
            throws RubatoException {
        int i = labelToIndex(label);
        if (i < 0) {
            throw new RubatoException("LimitForm.getForm: Label %%1 does not exist", label);
        }
        return getForm(i);
    }

    
    public FormDiagram getFormDiagram() {
        return (FormDiagram)getIdentifier().getCodomainDiagram();
    }

    
    /**
     * Sets the labels for the factors of the form.
     * Labels are assigned in the order they occur in the list <code>labels</code>.
     */
    public void setLabels(List<String> labels) {
        if (labels == null) {
            labelMap = null;
            reverseLabelMap = null;
        }
        else {
            labelMap = new HashMap<String,Integer>();
            reverseLabelMap = new String[labels.size()];
            int i = 0;
            for (String label : labels.subList(0, getFormCount())) {
                labelMap.put(label, i);
                reverseLabelMap[i] = label;
                i++;
            }
        }
    }

    
    private void setLabels(HashMap<String,Integer> labels) {
        if (labels == null) {
            labelMap = null;
            reverseLabelMap = null;
        }
        else {
            labelMap = labels;
            reverseLabelMap = new String[labels.size()];
            for (String label : labelMap.keySet()) {
                reverseLabelMap[labelMap.get(label)] = label;
            }
        }
    }
    
    
    /**
     * Returns the index corresponding to the given label.
     * @return the index, or -1 if the label does not exist
     */
    public int labelToIndex(String label) {
        if (labelMap == null) {
            int i;
            try {
                i = Integer.parseInt(label);
                if (i >= getFormCount()) {
                    return -1;
                }
                else {
                    return i;
                }
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }
        Integer i = labelMap.get(label);
        if (i == null) {
            return -1;
        }
        else {
            return i;
        }
    }

    
    /**
     * Returns the label corresponding to the given index <code>i</code>.
     */
    public String indexToLabel(int i) {
        if (reverseLabelMap == null) {
            return Integer.toString(i);
        }
        else if (i >= reverseLabelMap.length) {
            if (i < getFormCount()) {
                return Integer.toString(i);
            }
            else {
                return null;
            }
        }
        else {
            return reverseLabelMap[i];
        }
    }

    
    /**
     * Returns true iff this form has labels.
     */
    public boolean hasLabels() {
        return labelMap != null;
    }
    
    
    protected LinkedList<Form> getDependencies(LinkedList<Form> list) {
        if (!list.contains(this)) {
            list.add(this);
            return identifier.getFormDependencies(list);
        }
        return list;
    }
    
    
    private static final String NAME_ATTR  = "name";
    private static final String TYPE_VALUE = "limit";
    private static final String LABELS     = "Labels";
    private static final String LABEL      = "Label";
    private static final String POS_ATTR   = "pos";
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(FORM, TYPE_ATTR, TYPE_VALUE, NAME_ATTR, getNameString());
        
        if (labelMap != null) {
            writer.openBlock(LABELS);
            for (String label : labelMap.keySet()) {
                int i = labelMap.get(label).intValue();
                writer.empty(LABEL, NAME_ATTR, label, POS_ATTR, i);
            }
            writer.closeBlock();
        }
        
        if (identifier instanceof ProperIdentityMorphism) {
            ProperIdentityMorphism im = (ProperIdentityMorphism)identifier;
            if (im.getDiagram() instanceof FormDiagram) {
                for (Form f : getForms()) {
                    writer.writeFormRef(f);
                }
                writer.closeBlock();
                return;
            }
        }

        identifier.toXML(writer);
        writer.closeBlock();
    }


    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     *
     * @return a limit form or null if parsing failed
     */
    public static LimitForm fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));
        if (!element.hasAttribute(NAME_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3.", TYPE_VALUE, FORM, NAME_ATTR);
            return null;                                                
        }

        HashMap<String,Integer> labels = null;
        Element childElement;
        
        childElement = XMLReader.getChild(element, LABELS);
        if (childElement != null) {
            labels = new HashMap<String,Integer>();
            childElement = XMLReader.getChild(childElement, LABEL);
            while (childElement != null) {
                String label = XMLReader.getStringAttribute(childElement, NAME_ATTR);
                int pos = XMLReader.getIntAttribute(childElement, POS_ATTR, 0);
                labels.put(label, pos);
                childElement = XMLReader.getNextSibling(childElement, LABEL);
            }
        }
        
        childElement = XMLReader.getChild(element, FORM);
        if (childElement == null) {
            reader.setError("Type %%1 of element <%2> is missing elements of type <%2>.", TYPE_VALUE, FORM);
            return null;
        }
        
        LinkedList<Form> forms = new LinkedList<Form>();
        boolean references = false;
        while (childElement != null) {
            Form form = reader.parseForm(childElement);
            if (form == null) {
                return null;
            }
            forms.add(form);
            if (form instanceof FormReference) {
                references = true;
            }
            childElement = XMLReader.getNextSibling(childElement, FORM);
        }
                
        LimitForm limitForm = new LimitForm(NameDenotator.make(element.getAttribute("name")), forms);
        limitForm.setLabels(labels);
        if (references) {
            reader.addFormToBeResolved(limitForm);
        }
        return limitForm;
    }
    
        
    /**
     * Returns a default denotator of this limit form.
     */
    public Denotator createDefaultDenotator() {
        LinkedList<Denotator> cds = new LinkedList<Denotator>();
        for (int i = 0; i < getFormCount(); i++) {
            cds.add(getForm(i).createDefaultDenotator());
        }
        LimitDenotator res = null;
        try {
            res = new LimitDenotator(null, this, cds);
        }
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    /**
     * Returns a default denotator of this limit form with the given address.
     */
    public Denotator createDefaultDenotator(Module address) {
        LinkedList<Denotator> cds = new LinkedList<Denotator>();
        for (int i = 0; i < getFormCount(); i++) {
            cds.add(getForm(i).createDefaultDenotator(address));
        }
        LimitDenotator res = null;
        try {
            res = new LimitDenotator(null, this, cds);
        }
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    public String toString() {
        StringBuilder buf = new StringBuilder(50);
        buf.append("[");
        buf.append(getNameString());
        buf.append(":.limit(");
        buf.append(getForm(0).getNameString());
        for (int i = 1; i < getFormCount(); i++) {
            buf.append(",");
            buf.append(getForm(i).getNameString());
        }
        buf.append(")]");
        return buf.toString();
    }
    
    
    protected void display(PrintStream out, LinkedList<Form> recursionCheckStack, int indent) {
        indent(out, indent);
        out.print("Name: \""+getNameString()+"\"");
        out.println("; Type: limit");

        indent += 4;
    
        if (recursionCheck(recursionCheckStack)) {
            indent(out, indent);
            out.println("...");
        }
        else {
	        recursionCheckStack.addFirst(this);
	        FormDiagram d = (FormDiagram)getIdentifier().getCodomainDiagram();
	        for (int i = 0; i < d.getFormCount(); i++) {
	            d.getForm(i).display(out, recursionCheckStack, indent);
	        }
	        recursionCheckStack.removeFirst();
        }
    }    

    
    protected double getDimension(int maxDepth, int depth) {
        double dimension = 0.0;
        double one_by_n = 1.0;
        
        if (depth > maxDepth) return 1.0;
        
        dimension = getFormCount();
        one_by_n = 1.0 / dimension;
        for (int i = 0; i < dimension; i++) {
            dimension += one_by_n * (1.0 - 1.0 / getForm(0).getDimension(maxDepth, depth + 1));
        }
        
        return dimension;
    }
    
    
    private HashMap<String,Integer> labelMap = null;
    private String[] reverseLabelMap = null;
}
