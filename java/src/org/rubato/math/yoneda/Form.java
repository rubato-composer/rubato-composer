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

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.math.module.Module;
import org.rubato.xml.XMLWriter;

/**
 * Abstract base class for forms.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public abstract class Form
    extends AbstractConnectableYoneda
    implements Comparable<Form> {

    /**
     * Returns the name of the form as a denotator.
     */
    public NameDenotator getName() {
        return name;
    }
    

    /**
     * Returns the name of the form as a string.
     */
    public String getNameString() {
        return name.getNameString();
    }

    /**
     * Returns the type of the form.
     */
    public abstract int getType();


    /**
     * Returns the type of the form as a string.
     */
    public String getTypeString() {
        return typeToString(getType());
    }
    

    /**
     * Returns the identifier of the form.
     */
    public Morphism getIdentifier() {
        return identifier;
    }


    /**
     * Sets the name of the form as a denotator.
     */
    public void setName(NameDenotator name) {
        this.name = name;
        this.hasHashcode = false;
    }


    /**
     * Sets the name of the form as a denotator.
     */
    public void setName(String name) {
        setName(NameDenotator.make(name));
    }


    /**
     * Sets the identifier of the form.
     */
    public void setIdentifier(Morphism identifier) {
        this.identifier = identifier;
        this.hasHashcode = false;
    }


    /**
     * Returns the dimension of a form using default maximal depth.
     */
    public double getDimension() {
        return getDimension(DEFAULT_MAX_DEPTH, 0);
    }


    /**
     * Returns the dimension of a form.
     */
    public double getDimension(int maxDepth) {
        return getDimension(maxDepth, 0);
    }

    
    protected abstract double getDimension(int maxDepth, int depth);

    /**
     * Returns a default denotator of this form.
     */
    public abstract Denotator createDefaultDenotator();
    
    /**
     * Returns a default denotator of this form with the given address.
     */
    public abstract Denotator createDefaultDenotator(Module address);

    /**
     * Compares two forms.
     * Comparison is done on the names of two forms.
     */
    public int compareTo(Form other) {
        if (equals(other)) {
            return 0;
        }
        else if (getType() == other.getType()) {
            return getName().compareTo(other.getName());
        }
        else {
            return getType()-other.getType();
        }
    }
    

    /**
     * Form object cannot be cloned.
     */
    public Object clone() {
        throw new UnsupportedOperationException("Form objects cannot be cloned");
    }


    /**
     * Returns true iff this form is equal to the specified object.
     */
    public abstract boolean equals(Object object);


    /**
     * Returns true iff this form is equal to the specified form.
     */
    public boolean equals(Form f) {
        if (registered && f.registered) {
            return getName().equals(f.getName());
        }
        else {
            return fullEquals(f);
        }
    }


    /**
     * Returns true iff this form is <i>structurally</i> equal to <code>f</code>.
     */
    public boolean fullEquals(Form f) {
        return fullEquals(f, new IdentityHashMap<Object,Object>());
    }


    /**
     * Returns true iff this form is <i>structurally</i> equal to <code>f</code>.
     * @param f the form to compare to
     * @param s a map containing a history of already encountered forms,
     *          used to break recursion
     */
    public boolean fullEquals(Form f, IdentityHashMap<Object,Object> s) {
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
    public abstract int getFormCount();
    

    /**
     * Returns a coordinate form.
     * 
     * @param i the coordinate position
     * @return the form at coordinate position i
     */
    public abstract Form getForm(int i);
    

    /**
     * Returns a list of the coordinate forms.
     */
    public List<Form> getForms() {
        LinkedList<Form> list = new LinkedList<Form>();
        for (int i = 0; i < getFormCount(); i++) {
            list.add(getForm(i));
        }
        
        return list;
    }


    /**
     * Returns the space type as a String.
     */
    public static final String typeToString(int type) {
        return types[type];
    }
                                    
    
    /**
     * Returns the space type as an integer.
     */
    public static final int stringToType(String s) {
        for (int i = 0; ; i++) {
            if (s.equalsIgnoreCase(types[i])) return i;
        }
    }
    

    /**
     * Returns a string representation of this form.
     * This string is not parseable and does not contain
     * all information. It is only meant for information purposes.
     */
    public abstract String toString();

    
    public abstract void toXML(XMLWriter writer);
    
    
    /**
     * Print form to stdout.
     */
    public void display() {
        display(System.out);        
    }


    /**
     * Print form to a stream.
     * 
     * @param out the stream to print to
     */
    public void display(PrintStream out) {
        display(out, new LinkedList<Form>(), 0);
    }


    protected abstract void display(PrintStream out, LinkedList<Form> recursionCheckStack, int indent);
    

    /**
     * Returns true if this form is in the recursion stack.
     */
    protected final boolean recursionCheck(LinkedList<Form> recursionCheckStack) {
        for (Form f : recursionCheckStack) {
	        if (f == this) {
	            return true;
	        }
	    }
	    return false;
    }
    
    
    /**
     * Returns a list of the forms that this form depends on.
     * A form always depends on itself.
     */
    public final LinkedList<Form> getDependencies() {
        return getDependencies(new LinkedList<Form>());
    }
    
    
    /**
     * Adds the the forms that this form depends on to <code>list</code>.
     * @param list the dependency list, updated by this method.
     * 
     * @return the changed list
     */
    protected abstract LinkedList<Form> getDependencies(LinkedList<Form> list);

    
    /**
     * Print out a number of white spaces to a stream.
     * 
     * @param out the stream to print to
     * @param n the number of spaces to print
     */
    protected final void indent(PrintStream out, int n) {
        for (int i = 0; i < n; i++) {
            out.print(" ");
        }
    }

    
    /**
     * Returns a hash code for this form.
     */
    public int hashCode() {
        if (!hasHashcode) {
            computeHashcode();
        }
        return hashcode;
    }
    
    
    int _shallowHash() {
        int hash= getNameString().hashCode();
        hash = 37*hash+(getType()+1);
        return hash;
    }
    

    /**
     * Registers this form with the specified repository.
     * This is for internal use only. Forms must be registered
     * by using the {@link Repository#register(Form)} method.
     */
    public Form _register(Repository repository, boolean builtin) {
        if (registered) {
            return this;
        }
        else {
            registered = true;
            Form form = repository.register(this, builtin);
            if (form == null) {
                registered = false;
            }
            return form;
        }
    }
    
    
    /**
     * Returns true iff this form is already registered.
     */
    public boolean isRegistered() {
        return registered;
    }
    

    private void computeHashcode() {
        hasHashcode = true;
        hashcode = getNameString().hashCode();
        hashcode = 37*hashcode + (getType()+1);
        if (identifier != null) {
            hashcode = 37*hashcode + identifier.hashCode();
        }
    }

    
    /**
     * Resolves the references resulting from parsing.
     * 
     * @return true iff all references have been resolved.
     */
    public boolean resolveReferences(RubatoDictionary dict) {
        IdentityHashMap<?,?> history = new IdentityHashMap<Object,Object>();
        return resolveReferences(dict, history);
    }
    
    
    @SuppressWarnings("unchecked")
    boolean resolveReferences(RubatoDictionary dict, IdentityHashMap history) {
        if (history.containsKey(this)) {
            return true;
        }
        else {
            history.put(this, this);
            return identifier.resolveReferences(dict, history);
        }
    }
    
    
    /**
     * Generic form constructor.
     */
    protected Form(NameDenotator name, Morphism identifier) {
        this.name = name;
        this.identifier = identifier;
    }


    protected NameDenotator name;
    protected Morphism 	    identifier;
    
    private   boolean       hasHashcode = false;
    private   int     	    hashcode    = 0;
    protected boolean 	    registered  = false;
    
    private static final String[] types = { "simple" ,
                                            "limit",
                                            "colimit",
                                            "power",
                                            "list" };
    
    private static final int DEFAULT_MAX_DEPTH = 10;
}
