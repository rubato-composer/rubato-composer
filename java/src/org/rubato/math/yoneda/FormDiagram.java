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

import static org.rubato.xml.XMLConstants.DIAGRAM;
import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.REF_ATTR;

import java.util.*;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Diagram of forms.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */

public final class FormDiagram extends Diagram {

    /**
     * Creates a diagram with the given <code>form</code> as its single vertex.
     */
    public FormDiagram(Form form) {
        allocate(1);
        forms[0] = form;
    }


    /**
     * Creates a diagram using the given list of forms as vertexes.
     */
    public FormDiagram(List<Form> forms) {
        allocate(forms.size());
        this.forms = forms.toArray(this.forms);
    }
    

    public Form getForm(int i) {
        return forms[i];
    }


    public int getFormCount() {
        return forms.length;
    }

    
    public void setForm(int i, Form form) {
        forms[i] = form;
    }
    

    public void insertForm(int i, Form form) {
        Form[] oldForms = forms;
        ArrayList<Morphism>[][] oldMorphisms = morphisms;
        allocate(oldForms.length+1);
        for (int j = 0; j < i; j++) {
            forms[j] = oldForms[j];
        }
        forms[i] = oldForms[i];
        for (int j = i; j < oldForms.length; j++) {
            forms[j+1] = oldForms[j];
        }
        for (int j = 0; j < oldForms.length; j++) {
            for (int k = 0; k < oldForms.length; k++) {
                int x = (j < i)?j:j+1;
                int y = (k < i)?k:k+1;
                morphisms[x][y] = oldMorphisms[j][k];
            }
        }
    }
    

    public void appendForm(Form form) {
        insertForm(getFormCount(), form);
    }
    

    public void deleteForm(int i) {
        Form[] oldForms = forms;
        ArrayList<Morphism>[][] oldMorphisms = morphisms;
        allocate(oldForms.length-1);
        for (int j = 0; j < forms.length; j++) {
            int x = (j < i)?j:j+1;
            forms[j] = oldForms[x];
        }
        for (int j = 0; j < forms.length; j++) {
            for (int k = 0; k < forms.length; k++) {
                int x = (j < i)?j:j+1;
                int y = (k < i)?k:k+1;
                morphisms[j][k] = oldMorphisms[x][y];
            }
        }
    }
    

    public Yoneda getVertex(int i) {
        return getForm(i);
    }
    

    public void deleteVertex(int i) {
        deleteForm(i);
    }
        

    public int getVertexCount() {
        return getFormCount();
    }

    
    public Morphism getArrow(int i, int j, int n) {
        return morphisms[i][j].get(n);
    }


    public int getArrowCount(int i, int j) {
        if (morphisms[i][j] == null) {
            return 0;
        }
        else {
            return morphisms[i][j].size();
        }
    }


    public void insertArrow(int i, int j, int n, Morphism morphism) {
        morphisms[i][j].add(n, morphism);
        
    }


    public void deleteArrow(int i, int j, int n) {
        morphisms[i][j].remove(n);
        if (morphisms[i][j].size() == 0) {
            morphisms[i][j] = null;
        }
    }

    
    public void appendArrow(int i, int j, Morphism morphism) {
        if (morphisms[i][j] == null) {
            morphisms[i][j] = new ArrayList<Morphism>();
        }
        morphisms[i][j].add(morphism);
    }
    

    public int compareTo(Yoneda object) {
        if (this == object) {
            return 0;
        }
        else if (object instanceof FormDiagram) {
            FormDiagram dia = (FormDiagram)object;
            int fc = getFormCount()-dia.getFormCount();
            if (fc != 0) {
                return fc;
            }
            for (int i = 0; i < getFormCount(); i++) {
                int c = getForm(i).compareTo(dia.getForm(i));
                if (c != 0) {
                    return c;
                }
            }
            for (int i = 0; i < getFormCount(); i++) {
                for (int j = 0; j < getFormCount(); j++) {
                    int c1 = getArrowCount(i, j);
                    int c2 = dia.getArrowCount(i, j);
                    if (c1 == c2) {
                        for (int k = 0; k < c1; k++) {
                            int ac = getArrow(i, j, k).compareTo(dia.getArrow(i, j, k));
                            if (ac != 0) {
                                return ac;
                            }
                        }
                    }
                    else {
                        return c1-c2;
                    }
                }
            }
            return 0;
        }
        else {
            throw new ClassCastException();
        }
    }

    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof FormDiagram) {
            FormDiagram dia = (FormDiagram)object;
            if (getFormCount() == dia.getFormCount()) {
                for (int i = 0; i < getFormCount(); i++) {
                    if (!getForm(i).equals(dia.getForm(i))) {
                        return false;
                    }
                }
                for (int i = 0; i < getFormCount(); i++) {
                    for (int j = 0; j < getFormCount(); j++) {
                        int c1 = getArrowCount(i, j);
                        int c2 = dia.getArrowCount(i, j);
                        if (c1 == c2) {
                            for (int k = 0; k < c1; k++) {
                                if (!getArrow(i, j, k).equals(dia.getArrow(i, j, k))) {
                                    return false;
                                }
                            }
                        }
                        else {
                            return false;
                        }
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    
    /**
     * Returns a shallow copy of this diagram.
     * Forms and morphisms themselves are not cloned.
     */
    public Object clone() {
        FormDiagram diagram = new FormDiagram();
        diagram.allocate(forms.length);
        for (int i = 0; i < forms.length; i++) {
            diagram.forms[i] = forms[i];
        }
        for (int i = 0; i < forms.length; i++) {
            for (int j = 0; j < forms.length; j++) {
                diagram.morphisms[i][j] = new ArrayList<Morphism>(morphisms[i][j]);
            }
        }
        return diagram;
    }


    public boolean fullEquals(Diagram diagram, IdentityHashMap<Object,Object> history) {
        if (this == diagram) {
            return true;
        }
        else if (!(diagram instanceof FormDiagram)) {
            return false;
        }
        else {
            FormDiagram formDiagram = (FormDiagram)diagram;
            final int formCount = getFormCount();
            if (formCount != formDiagram.getFormCount()) {
                return false;
            }
            for (int i = 0; i < formCount; i++) {
                Form f1 = getForm(i);
                Form f2 = formDiagram.getForm(i);
                Object object = history.get(f1);
                if (object != null) {
                    if (object != f2) {
                        return false;
                    }
                }
                else if (!f1.fullEquals(f2, history)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    public boolean registerForms(Repository rep, boolean builtin) {
        for (int i = 0; i < getFormCount(); i++) {
            if (getForm(i)._register(rep, builtin) == null) {
                return false;
            }
        }
        return true;
    } 
    
    
    boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history) {
        for (int i = 0; i < getFormCount(); i++) {
            Form form = getForm(i);
            if (form instanceof FormReference) {
                Form newForm = dict.getForm(form.getNameString());
                if (newForm == null) {
                    return false;
                }
                setForm(i, newForm);
            }
            else {
                form.resolveReferences(dict, history);
            }
        }
        return true;
    }

    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list) {
        for (int i = 0; i < getFormCount(); i++) {
            list = getForm(i).getDependencies(list);
        }
        return list;
    }

    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list) {
        return list;
    }

    
    private final static String ARROW     = "Arrow";
    private final static String FROM_ATTR = "from";
    private final static String TO_ATTR   = "to";

    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(DIAGRAM, getElementTypeName());
        for (int i = 0; i < getFormCount(); i++) {
            writer.empty(FORM, REF_ATTR, getForm(i).getNameString());
        }
        for (int i = 0; i < getFormCount(); i++) {
            for (int j = 0; j < getFormCount(); j++) {
                if (getArrowCount(i, j) > 0) {
                    for (int k = 0; k < getArrowCount(i, j); k++) {
                        writer.openBlock(ARROW, FROM_ATTR, i, TO_ATTR, j);
                        getArrow(i, j, k).toXML(writer);
                        writer.closeBlock();
                    }
                }
            }
        }
        writer.closeBlock();
    }
    
    
    public Diagram fromXML(XMLReader reader, Element element) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not implemented");
    }
    
    
    public String getElementTypeName() {
        return "FormDiagram";
    }
    

    public int hashCode() {
        int hash = "FormDiagram".hashCode();
        for (int i = 0; i < getFormCount(); i++) {
            hash = 37*hash+getForm(i).hashCode();
        }
        for (int i = 0; i < getFormCount(); i++) {
            for (int j = 0; j < getFormCount(); j++) {
                if (morphisms[i][j] != null) {
                    hash = 37*hash+morphisms[i][j].hashCode();
                }
            }            
        }
        return hash;
    }

    
    @SuppressWarnings("unchecked")
    private void allocate(int formCount) {
        forms = new Form[formCount];
        morphisms = new ArrayList[formCount][formCount];
    }
    
    
    private Form[] forms;
    private ArrayList<Morphism>[][] morphisms;
    
    private FormDiagram() { /* not allowed */ }
    
    public static FormDiagram emptyFormDiagram;
    
    static {
        emptyFormDiagram = new FormDiagram();
    }
}
