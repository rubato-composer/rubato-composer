/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
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

import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Repository;

/**
 * Special list form for names of forms and denotators.
 *
 * @author Gérard Milmeister
 */
public final class NameForm extends ListForm {

    public static NameForm getNameForm() {
        createNameForm();
        return nameForm;
    }


    /**
     * Returns the name of the form as a denotator.
     */
    public NameDenotator getName() {
        if (name == null) {
            name = NameDenotator.make("Name");
        }
        return name;
    }
    

    /**
     * Returns the name of the form as a string.
     */
    public String getNameString() {
        return "Name";
    }


    /**
     * Returns the type of the form.
     */
    public int getType() {
        return LIST;
    }
    

    /**
     * Returns the identifier of the form.
     */
    public Morphism getIdentifier() {
        if (identifier == null) {
            identifier = new ProperIdentityMorphism(new FormDiagram(stringForm), LIST);        
        }
        return identifier;
    }


    /**
     * Sets the name of the form as a denotator.
     */
    public void setName(Denotator name) {
        throw new UnsupportedOperationException("Name form is immutable");
    }


    /**
     * Sets the identifier of the form.
     */
    public void setIdentifier(Morphism identifier) {
        throw new UnsupportedOperationException("Name form is immutable");
    }


    /**
     * Returns the dimension of a form using default maximal depth.
     */
    public double getDimension() {
        return 1.0;
    }


    /**
     * Returns the dimension of a form.
     */
    public double getDimension(int maxDepth) {
        return 1.0;
    }


    public Object clone() {
        return this;
    }


    public boolean equals(Object object) {
        return (this == object || object instanceof NameForm);
    }


    public boolean iscomplete() {
        return true;
    }


    /**
     * Returns the number of coordinate forms.
     */
    public int getFormCount() {
        return 1;
    }
    

    /**
     * Returns a coordinate form.
     * @param i the coordinate position
     * @return the form at coordinate position i
     */
    public Form getForm(int i) {
        return stringForm;
    }
    

    /**
     * Returns a list of the coordinate forms.
     */
    public List<Form> getForms() {
        return formList;
    }


    protected LinkedList<Form> getDependencies(LinkedList<Form> list) {
        return list;
    }    

    
    private static void createNameForm() {
        if (nameForm == null) {
            nameForm = new NameForm();        
            stringForm = Repository.systemRepository().getForm("String");
            formList = new LinkedList<Form>();
            formList.add(stringForm);
        }
    }
        
    
    private NameForm() {
        super(null, (Morphism)null);
    }
    
    
    private static NameForm   nameForm = null;
    private static Form       stringForm = null;
    private static LinkedList<Form> formList = null;
}
