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

package org.rubato.math.yoneda;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLWriter;

/**
 * A placeholder for denotators that are not yet known.
 * Mainly used during parsing.
 * 
 * @author Gérard Milmeister
 */
public final class DenotatorReference extends Denotator {

    public DenotatorReference(String name) {
        super(NameDenotator.make(name), null);
    }
    
    
    public DenotatorReference(NameDenotator name) {
        super(name, null);
    }
    
    
    @Override
    public int getType() {
        throw new Error("Fatal error: DenotatorReference.getType should never be called.");
    }
    
    
    @Override
    public Denotator namedCopy(NameDenotator name) {
        return new DenotatorReference(name);
    }

    
    @Override
    public DenotatorReference copy() {
        throw new Error("Fatal error: DenotatorReference.clone should never be called.");
    }

    
    @Override
    public boolean equals(Object object) {
        throw new Error("Fatal error: DenotatorReference.equals should never be called.");
    }
    
    
    @Override
    public int compareTo(Denotator object) {
        throw new Error("Fatal error: DenotatorReference.compareTo should never be called.");
    }
    
    
    @Override
    public Denotator at(ModuleElement element) {
        throw new Error("Fatal error: DenotatorReference.at should never be called.");
    }

    
    @Override
    public Denotator changeAddress(Module newAddress) {
        throw new Error("Fatal error: DenotatorReference.changeAddress should never be called");
    }
    
    
    @Override
    public Denotator changeAddress(ModuleMorphism morphism) {
        throw new Error("Fatal error: DenotatorReference.changeAddress should never be called");
    }

    
    @Override
    protected Denotator get(int[] path, int curpos)
            throws RubatoException {
        throw new Error("Fatal error: DenotatorReference.get should never be called");
    }
    
    @Override
    protected Denotator replace(int[] path, int curpos, Denotator d) throws RubatoException {
        throw new Error("Fatal error: DenotatorReference.replace should never be called");
    }
    
    @Override
    protected Denotator map(int[] path, int curpos, ModuleMorphism morphism)
            throws RubatoException {
        throw new Error("Fatal error: DenotatorReference.map should never be called");
    }

    
    @Override
    protected ModuleElement getElement(int[] path, int curpos)
            throws RubatoException {
        throw new Error("Fatal error: DenotatorReference.getElement should never be called");
    }
    
    
    @Override
    protected ModuleMorphism getModuleMorphism(int[] path, int curpos)
            throws RubatoException {
        throw new Error("Fatal error: DenotatorReference.getModuleMorphism should never be called");
    }
    
    
    @Override
    public boolean isConstant() {
        throw new Error("Fatal error: DenotatorReference.isConstant should never be called");
    }

    
    @Override
    public Iterator<Denotator> iterator() {
        throw new Error("Fatal error: DenotatorReference.iterator should never be called");
    }
    
    
    @Override
    protected void display(PrintStream out, LinkedList<Denotator> recursionCheckStack, int indent) {
        throw new Error("Fatal error: DenotatorReference.display should never be called");
    }

    
    @Override
    public boolean check() {
        // there must be no DenotatorReference in a consistent denotator
        return false;
    }
    
    
    @Override
    protected LinkedList<Denotator> getDependencies(LinkedList<Denotator> list) {
        throw new Error("Fatal error: DenotatorReference.getDependencies never be called");
    }

    
    @Override
    public void toXML(XMLWriter writer) {
        throw new Error("Fatal error: DenotatorReference.toXML should never be called");
    }
    
    
    @Override
    public String toString() {
        return "["+getNameString()+":.reference]";
    }
}
