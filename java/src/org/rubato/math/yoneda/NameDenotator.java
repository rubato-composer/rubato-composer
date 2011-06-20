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

import static org.rubato.logeo.DenoFactory.makeDenotator;

import java.util.HashMap;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.math.module.ZProperFreeModule;


/**
 * Special list denotator for representing names of denotators and forms.
 * Name denotators are created using the static <code>make</code> methods
 * instead of explicit constructors. This is so in order to guarantee
 * that name denotators for a given name are unique.
 *
 * @author Gérard Milmeister
 */
public final class NameDenotator extends ListDenotator {

    /**
     * Creates a name denotator with the given name.
     * @return null if <code>name</code> is null or the empty string
     */
    public static NameDenotator make(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        else {
            return make(NameEntry.lookup(name));
        }
    }
    

    /**
     * Creates a name denotator with the given pair of names.
     */
    public static NameDenotator make(String name1, String name2) {
        return make(NameEntry.lookup(name1, name2));
    }


    /**
     * Creates a name denotator composed of the given list of names.
     */
    public static NameDenotator make(List<String> names) {
        return make(new NameEntry(names));
    }


    /**
     * Creates a name denotator from the given name entry.
     */
    public static NameDenotator make(NameEntry name) {
        NameEntry nameEntry = NameEntry.lookup(name);
        NameDenotator d = (NameDenotator)nameTab.get(nameEntry);
        if (d == null) {
            d = new NameDenotator(nameEntry);
        }
        return d;
    }

    
    /**
     * Creates a name of the form "op(dn)", where dn is the name
     * of the denotator d.
     * 
     * @return a new name denotator, or null if the name of dn is empty
     */
    public static NameDenotator make(String op, Denotator d) {
        String name = d.getNameString();
        if (name.length() > 0) {
            return NameDenotator.make(op+"("+name+")");
        }
        else {
            return null;
        }
    }


    /**
     * Creates name of the form "op(dn1,dn2)", where dn1 and dn2 are the
     * names of the denotators d1 and d2 resp.
     * 
     * @return a new name denotator, or null if one of dn1 or dn2 is empty
     */
    public static NameDenotator make(String op, Denotator d1, Denotator d2) {
        String dn1 = d1.getNameString();
        String dn2 = d2.getNameString();
        if (dn1.length() > 0 && dn2.length() > 0) {
            return NameDenotator.make(op+"("+dn1+","+dn2+")");
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Creates a name from operation and argument list.
     * The name is of the form "op(dn1,...,dnm)", where dn1 to dnm are the
     * names of the denotators from the argument list of denotators.
     */
    public static NameDenotator make(String op, Denotator[] denolist) {
        StringBuilder str = new StringBuilder(30);
        str.append(op);
        str.append("(");
        if (denolist.length > 0) {
            str.append(denolist[0].getNameString());
            for (int i = 1; i < denolist.length; i++) {
                str.append(",");
                str.append(denolist[i].getNameString());
            }
        }
        str.append(")");
        return NameDenotator.make(str.toString());
    }


    /**
     * Creates a name of the form "op(fn)".
     * fn is the name of the form f.
     */
    public static NameDenotator make(String op, Form f) {
        return NameDenotator.make(op+"("+f.getNameString()+")"); 
    }


    /**
     * Creates a name of the form "op(fn1,fn2)".
     * fn1 and fn2 are the names of the forms f1 and f2 resp.
     */
    public static NameDenotator make(String op, Form f1, Form f2) {
        return NameDenotator.make(op+"("+f1.getNameString()+","+f2.getNameString()+")"); 
    }
    
    
    /**
     * Creates a name of the form "op(fn1,..,fnm)".
     * fn1 to fnm are the names of the forms from the argument list of forms.
     */
    public static NameDenotator make(String op, Form[] formlist) {
        StringBuilder str = new StringBuilder(30);
        str.append(op);
        str.append("(");
        if (formlist.length > 0) {
            str.append(formlist[0].getNameString());
            for (int i = 1; i < formlist.length; i++) {
                str.append(",");
                str.append(formlist[i].getNameString());
            }
        }
        str.append(")");
        return NameDenotator.make(str.toString());
    }
    
   
    /**
     * Returns the name of the denotator as a denotator.
     */
    @Override
    public NameDenotator getName() {
        return this;
    }
    
    
    /**
     * Returns the name of the denotator converted to a string.
     */
    @Override
    public String getNameString() {
        return nameEntry.getString();
    }


    /**
     * Returns the name entry in this name denotator.
     */
    public NameEntry getNameEntry() {
        return nameEntry;
    }

        
    /**
     * Returns the form of the name denotator.
     */
    @Override
    public Form getForm() {
        return NameForm.getNameForm();
    }
    
    
    @Override
    public Morphism getFrameCoordinate() {
        return createCoordinates();
    }
    

    /**
     * Returns the number of coordinates of the denotator.
     */
    @Override
    public int getFactorCount() {
        return nameEntry.getLength();
    }


    /**
     * Returns the factor in position <code>i</code>.
     */
    @Override
    public Denotator getFactor(int i) {
        return super.getFactor(i);
    }


    /**
     * Name denotators are immutable.
     */
    @Override
    public void setFactor(int i, Denotator d) {
        throw new UnsupportedOperationException("Name denotators are immutable");
    }


    /**
     * Name denotators are immutable.
     */
    public ListDenotator appendFactor(NameDenotator d) {
        throw new UnsupportedOperationException("Name denotators are immutable");
    }


    /**
     * Name denotators are immutable.
     */
    @Override
    public void replaceFactors(List<Denotator> denoList) {
        throw new UnsupportedOperationException("Name denotators are immutable");
    }
    

    @Override
    public int compareTo(Denotator object) {
        if (object instanceof NameDenotator) {
            return nameEntry.compareTo(((NameDenotator)object).nameEntry);
        }
        else {
            return -object.compareTo(this);
        }
    }


    /**
     * Compares two name denotators.
     */
    public int compareTo(NameDenotator otherName) {
        return nameEntry.compareTo(otherName.nameEntry);
    }


    /**
     * Checks for equality.
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        else if (object == null) {
            return false;
        }
        else if (object instanceof NameDenotator) {
            return nameEntry.equals(((NameDenotator)object).nameEntry);
        }
        else {
            return object.equals(this);
        }
    }


    public boolean equals(NameDenotator nd) {
        if (nd == this) {
            return true;
        }
        else if (nd == null) {
            return false;
        }
        else {
            return nameEntry.equals(nd.nameEntry);
        }
    } 


    @Override
    public NameDenotator copy() {
        return this;
    }


    @Override
    public int hashCode() {
        return nameEntry.hashCode();
    }


    private NameDenotator(NameEntry nameEntry) {
        super();
        setName(this);
        this.nameEntry = nameEntry;
    }

    
    private Morphism createCoordinates() {
        if (stringForm == null) {
            stringForm = (SimpleForm)Repository.systemRepository().getForm("String");
        }
        if (diagram == null) {
            diagram = (FormDiagram)NameForm.getNameForm().getIdentifier().getCodomainDiagram();
        }

        ListMorphismMap map = new ListMorphismMap(nameEntry.getLength());
        for (int i = 0; i < nameEntry.getLength(); i++) {
            Denotator deno = makeDenotator(stringForm, nameEntry.getString(i)); 
            map.appendFactor(deno);
        }
        return new CompoundMorphism(ZProperFreeModule.nullModule, new ProperIdentityMorphism(diagram, LIST), map);
    }
    
    private        NameEntry   nameEntry  = null;
    private static SimpleForm  stringForm = null;
    private static FormDiagram diagram    = null;

    private static HashMap<String,Denotator> nameTab    = null;    

    static {
        nameTab = new HashMap<String,Denotator>();
    }
}
