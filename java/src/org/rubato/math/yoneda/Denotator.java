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
import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.base.RubatoDictionary;
import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLWriter;

/**
 * Abstract base class for denotators.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public abstract class Denotator 
    extends AbstractConnectableYoneda
    implements Comparable<Denotator>, Iterable<Denotator> {

    
    /**
     * Makes a shallow copy this denotator and gives
     * it the specified <code>name</code>.
     */
    public abstract Denotator namedCopy(NameDenotator nameDeno);
    
    /**
     * Returns the name of the denotator as a denotator.
     * A null name indicates an anonymous denotator.
     */
    public NameDenotator getName() {
        return name;
    }
    
    
    /**
     * Returns the name of the denotator converted to a string.
     * An empty string indicates an anonymous denotator.
     */
    public String getNameString() {
        return name == null ? "" : name.getNameString();
    }


    /**
     * Sets the name of the denotator as a NameDenotator.
     * To make an anonymous denotator, pass a null name.
     */
    public final void setName(NameDenotator name) {
        this.name = name;
    }


    /**
     * Sets the name of the denotator as a string.
     * To make an anonymous denotator, pass an empty string.
     */
    public final void setNameString(String nameString) {
        if (nameString.length() == 0) {
            name = null;
        }
        else {
            name = NameDenotator.make(nameString);
        }
    }

        
    /**
     * Returns the form of the denotator.
     */
    public Form getForm() {
        return form;
    }
    
    
    /**
     * Returns the type of the denotator.
     * @return type as an integer
     */
    public abstract int getType();
    
    
    /**
     * Returns the coordinate of the denotator.
     */
    public final Morphism getCoordinate() {
        return coordinate;
    } 
    
    
    /**
     * Sets the coordinate of the denotator.
     */
    protected final void setCoordinate(Morphism morphism) {
        coordinate = morphism;
    } 

    
    /**
     * Returns the frame coordinate of the denotator.
     */
    public Morphism getFrameCoordinate() {
        return frameCoordinate;
    }
    

    /**
     * Sets the frame coordinate of the denotator.
     */
    protected final void setFrameCoordinate(Morphism morphism) {
        frameCoordinate = morphism;
    }
    

    /**
     * Sets both coordinates of the denotator.
     */
    protected final void setCoordinates(Morphism morphism) {
        coordinate = frameCoordinate = morphism;
    }

    
    /**
     * Returns an iterator over the coordinates of this denotator.
     */
    public abstract Iterator<Denotator> iterator();
    
    
    /**
     * If not null-addressed returns a new denotator evaluated at address element.
     * If null-addressed, returns self.
     * 
     * @param element address
     * @return evaluated denotator
     * @throws MappingException if evaluation fails
     */
    public abstract Denotator at(ModuleElement element)
        throws MappingException;
    
    
    /**
     * Returns denotator evaluated at null address.
     * 
     * @return evaluated denotator
     */
    public final Denotator atNull() {
	    if (nullAddressed()) {
	        return this;
	    }
	    else {
            try {
                return at(getAddress().getZero());
            }
            catch (MappingException e) {
                throw new Error("Fatal error: Denotator.atNull should always succeed");
            }
	    }
    }
    

    /**
     * Returns the address of the denotator.
     */
    public final Module getAddress() {
        return getCoordinate().getDomainModule();
    }
        

    /**
     * Return true iff denotator is null-addressed.
     */
    public final boolean nullAddressed() {
        return getCoordinate().getDomainModule().isNullModule();
    }
    

    /**
     * Makes an address change.
     * 
     * @param newAddress the new address of the denotator
     * @return a copy of this denotator with the new address
     *         or null if address change fails
     */
    public abstract Denotator changeAddress(Module newAddress);
    
    
    /**
     * Makes an address change using a module morphism.
     * 
     * @param morphism the address changing morphism
     * @return a copy of this denotator with the new address
     *         or null if address change fails
     */
    public abstract Denotator changeAddress(ModuleMorphism morphism);
    
    
    /**
     * Retrieves a denotator by traversing the tree rooted
     * at this denotator.
     * 
     * @return null if denotator could not be retrieved along the path
     * @throws RubatoException
     */
    public final Denotator get(int[] path)
            throws RubatoException {
        return get(path, 0);
    }

    
    /**
     * Retrieves a denotator by traversing the tree rooted
     * at this denotator.
     * 
     * @param curpos the current position in the path
     * @return null if denotator could not be retrieved along the path
     * @throws RubatoException
     */
    protected abstract Denotator get(int[] path, int curpos)
        throws RubatoException;

    /**
     * Returns a denotator by replacing the denotator
     * at the given path in this denotator with <code>d</code>.
     * 
     * @param path the path at the end of which the denotator is replaced
     * @param d the denotator to put at the path 
     * @throws RubatoException if the specified denotator
     *         is of the wrong form or address
     */
    public final Denotator replace(int[] path, Denotator d)
            throws RubatoException {
        return replace(path, 0, d);
    }

    
    /**
     * Returns a denotator by replacing the denotator
     * at the given path in this denotator with <code>d</code>.
     * 
     * @param path the path at the end of which the denotator is replaced
     * @param curpos the current position in the path 
     * @param d the denotator to put at the path 
     * @throws RubatoException if the specified denotator
     *         is of the wrong form or address
     */
    protected abstract Denotator replace(int[] path, int curpos, Denotator d)
        throws RubatoException;
    
    
    /**
     * Maps this denotator using the given <code>morphism</code>
     * along the specified <code>path</code>.
     * 
     * @throws RubatoException
     */
    public final Denotator map(int[] path, ModuleMorphism morphism)
            throws RubatoException {
        return map(path, 0, morphism);
    }
    
    
    /**
     * Maps this denotator using the given <code>morphism</code>
     * along the specified <code>path</code> given that the
     * current position is <code>curpos</code>.
     * 
     * @param curpos the current position in the path
     * @return null if element could not be retrieved along the path
     * @throws RubatoException
     */
    protected abstract Denotator map(int[] path, int curpos, ModuleMorphism morphism)
        throws RubatoException;

    
    /**
     * Retrieves the module element by traversing the denotator
     * tree. A denotator of type simple must be reached somewhere
     * along the path.
     * 
     * @return null if element could not be retrieved along the path
     * @throws RubatoException
     */
    public final ModuleElement getElement(int[] path)
            throws RubatoException {
        return getElement(path, 0);
    }
    
    
    /**
     * Retrieves the module element by traversing the denotator
     * tree. A denotator of type simple must be reached somewhere
     * along the path.
     * 
     * @param curpos the current position in the path
     * @return null if element could not be retrieved along the path
     * @throws RubatoException
     */
    protected abstract ModuleElement getElement(int[] path, int curpos)
        throws RubatoException;
    
    
    /**
     * Retrieves the module morphism by traversing the denotator
     * tree. A denotator of type simple must be reached somewhere
     * along the path.
     * 
     * @return null if morphism could not be retrieved along the path
     * @throws RubatoException
     */
    public final ModuleMorphism getModuleMorphism(int[] path)
            throws RubatoException {
        return getModuleMorphism(path, 0);
    }
    
    
    /**
     * Retrieves the module morphism by traversing the denotator
     * tree. A denotator of type simple must be reached somewhere
     * along the path.
     * 
     * @param curpos the current position in the path
     * @return null if morphism could not be retrieved along the path
     * @throws RubatoException
     */
    protected abstract ModuleMorphism getModuleMorphism(int[] path, int curpos)
        throws RubatoException;
    
    
    public abstract int compareTo(Denotator object);

    
    /**
     * Compares the names of two denotators.
     * The comparison is string comparison, where a null
     * name is equal to another null name, otherwise less
     * than every other name.
     */
    public int nameCompareTo(Denotator d) {
        if (getName() == null) {
            return (d.getName() == null)?0:-1;
        }
        else if (d.getName() == null) {
            return 1;
        }
        else {
            return getName().compareTo(d.getName());
        }
    }

    /**
     * Checks for equality.
     */
    @Override
    public abstract boolean equals(Object object);
    
    
    /**
     * Returns true iff this denotator has the same name
     * as the other denotator.
     */
    public boolean nameEquals(Denotator other) {
        if (getName() == null) {
            return other.getName() == null;
        }
        else {
            return getName().equals(other.getName());
        }
    }
    
    
    /**
     * Returns true iff this denotator has the same form
     * as the other denotator.
     */
    public boolean formEquals(Denotator other) {
        return getForm().equals(other.getForm());
    }

    
    /**
     * Returns true iff this is ultimately a constant denotator,
     * regardless of its address.
     */
    public abstract boolean isConstant();
    

    /**
     * Checks if the form of the denotator matches <code>f</code>.
     * A form matches <code>f</code> if they are equal.
     * 
     * @return boolean true iff form matches <code>f</code>
     */
    public final boolean hasForm(Form f) {
        return getForm().equals(f);
    }


    /**
     * The cloning of a denotator is the same as copying it.
     */
    public final Object clone() {
        return copy();
    }
    
    
    /**
     * Returns a deep copy of this denotator.
     * The copy is given the same name as the original.
     */
    public abstract Denotator copy();

    
    /**
     * Returns true iff this denotator is consistent.
     * This can be used in assertions to validate a denotator,
     * for example if its constructed using unsafe methods
     * or changed inplace.
     */
    public abstract boolean check();
    
    
    /**
     * Returns a hash code for this denotator.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37*hash+getNameString().hashCode();
        hash = 37*hash+getCoordinate().hashCode();
        if (getCoordinate() != getFrameCoordinate()) {
            hash = 37*hash+getFrameCoordinate().hashCode();
        }
        return hash;
    }


    /**
     * Returns a string representation of this denotator.
     * This string is not parseable and does not contain
     * all information. It is only meant for information purposes.
     */
    @Override
    public String toString() {
        if (nullAddressed()) { 
            return "[0@"+getNameString()+":"+getForm()+"]";
        }
        else {
            return "["+getAddress()+"@"+getNameString()+":"+getForm()+"]";
        }
    }

    
    public abstract void toXML(XMLWriter writer);
    
    
    /**
     * Print denotator to stdout.
     */
    public final void display() {
        display(System.out);
    }


    /**
     * Print denotator to a stream.
     * 
     * @param out the stream to print to
     */
    public final void display(PrintStream out) {
        display(out, new LinkedList<Denotator>(), 0);
    }
    
    
    protected abstract void display(PrintStream out, LinkedList<Denotator> recursionCheckStack, int indent);

    
    /**
     * Returns true if this denotator is in the recursion stack.
     */
    protected final boolean recursionCheck(LinkedList<Denotator> recursionCheckStack) {
        for (Denotator d : recursionCheckStack) {
            if (d == this) {
                return true;
            }
        }
	    return false;
    }
    

    /**
     * Print out a number of white spaces to a stream.
     * 
     * @param out the stream to print to
     * @param n the number of spaces to print
     */
    protected final static void indent(PrintStream out, int n) {
        for (int i = 0; i < n; i++) {
            out.print(" ");
        }
    }
    
    
    /**
     * Returns a list of the named denotators that this denotator depends on.
     * A named denotator always depends on itself.
     */
    public final LinkedList<Denotator> getDependencies() {
        return getDependencies(new LinkedList<Denotator>());
    }
    
    
    protected abstract LinkedList<Denotator> getDependencies(LinkedList<Denotator> list);


    /**
     * Replaces reference denotators by respective name denotators.
     *  
     * @param dict contains the lookup table
     * @return true if all references have been resolved
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
            if (!getCoordinate().resolveReferences(dict, history)) {
                return false;
            }
            if (getCoordinate()!= getFrameCoordinate()) {
                return getFrameCoordinate().resolveReferences(dict, history);
            }
            return true;
        }
    }

    
    /**
     * Generic constructor for denotators.
     */
    protected Denotator(NameDenotator name, Form form,
                        Morphism coordinate, Morphism frameCoordinate) {
        this.name = name;
        this.form = form;
        this.coordinate = coordinate;
        this.frameCoordinate = frameCoordinate;
    }
	    

    /**
     * Generic constructor for denotators.
     */
    protected Denotator(NameDenotator name, Form form) {
        this.name = name;
        this.form = form;
    }
    
    
    protected final static RubatoAddressException addressMismatchException(Denotator d, Module address) {
        String s = TextUtils.replaceStrings("Denotator has address %%1, but required address is %%2",
                                            d.getAddress(), address);
        return new RubatoAddressException(s, d.getAddress(), address);        
    }

    
    protected final static void checkDenotator(Denotator d, Form f) 
            throws RubatoFormException {
        if (!d.hasForm(f)) {
            throw new RubatoFormException(d.getForm(), f, "Denotator.checkDenotator");
        }
    }


    protected final static void checkDenotator(Denotator d, Form f, Module address) 
            throws RubatoFormException, RubatoAddressException {
        if (!d.hasForm(f)) {
            throw new RubatoFormException(d.getForm(), f, "Denotator.checkDenotator");
        }
        else if (d.nullAddressed() && address.isNullModule()) {
            return;
        }
        else if (!address.equals(d.getAddress())) {
            throw addressMismatchException(d, address);
        }
        
    }

    
    private NameDenotator name;
    private Form          form;
    private Morphism      coordinate;
    private Morphism      frameCoordinate;
}
