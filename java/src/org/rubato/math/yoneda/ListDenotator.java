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

import static org.rubato.xml.XMLConstants.DENOTATOR;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Internal;
import org.rubato.base.RubatoException;
import org.rubato.base.Unsafe;
import org.rubato.logeo.DenoFactory;
import org.rubato.logeo.Lists;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZProperFreeModule;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * List denotator class.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public class ListDenotator 
        extends Denotator        
        implements FactorDenotator {

    /**
     * Creates a new list denotator.
     * 
     * @param name    the name of the denotator, null if denotator is anomymous
     * @param address the address of the denotator, null if null-addressed
     * @param form    the form of the denotator
     * @param cds     a list of coordinate denotators
     * @throws RubatoFormException
     */
    public ListDenotator(NameDenotator name, Module address, ListForm form, List<Denotator> cds)
            throws RubatoException {
        super(name, form);
        
        FormDiagram diagram = form.getFormDiagram();
        Form baseForm = form.getForm();
        ListMorphismMap map = new ListMorphismMap(cds.size());
        for (Denotator d : cds) {
            checkDenotator(d, baseForm, address);
            map.appendFactor(d);
        }
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIST), map));
    }


    /**
     * Creates a new list denotator that takes its address from the coordinates.
     *
     * @param name the name of the denotator, null if denotator is anomymous
     * @param form the form of the denotator
     * @param cds  a list of coordinate denotators
     * @throws RubatoFormException
     */
    public ListDenotator(NameDenotator name, ListForm form, List<Denotator> cds)
            throws RubatoException {
        super(name, form);
        
        FormDiagram diagram = form.getFormDiagram();
        Form baseForm = form.getForm();
        ListMorphismMap map = new ListMorphismMap(cds.size());
        Module address;
        
        if (cds.size() > 0) {
            address = cds.get(0).getAddress();
            for (Denotator d : cds) {
	            checkDenotator(d, baseForm, address);
	            map.appendFactor(d);
	        }
        }
        else {
            address = ZProperFreeModule.nullModule;
        }
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIST), map));
    }
    

    protected ListDenotator() {
        super(null, null);
    }
    
    
    /**
     * Returns the type of the denotator.
     * 
     * @return type as an integer
     */
    @Override
    public int getType() {
        return LIST;
    }
    
    
    @Override
    public Denotator namedCopy(NameDenotator name) {
        Morphism coord;
        Morphism frameCoord;
        if (getCoordinate() == getFrameCoordinate()) {
            coord = frameCoord = (Morphism)getCoordinate().clone();
        }
        else {
            coord = (Morphism)getCoordinate().clone();
            frameCoord = (Morphism)getCoordinate().clone();
        }
        return new ListDenotator(name, getListForm(), coord, frameCoord);
    }

    
    /**
     * If not null-addressed returns a new denotator evaluated at address element.
     * If null-addressed, returns itself.
     * 
     * @param element address
     * @return evaluated denotator
     * @throws MappingException if evaluation fails
     */
    @Override
    public Denotator at(ModuleElement element)
            throws MappingException {
        if (nullAddressed()) {
            return this;
        }
        else {
            if (getCoordinate() == getFrameCoordinate()) {
                Morphism newCoord = getCoordinate().at(element);
                if (newCoord == getCoordinate()) {
                    return this;
                }
                else {
                    return new ListDenotator(null, getListForm(), newCoord, newCoord);
                }
            }
            else {
                Morphism newCoord = getCoordinate().at(element);
                Morphism newFCoord = getFrameCoordinate().at(element);
                if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                    return this;
                }
                else {
                    return new ListDenotator(null, getListForm(), newCoord, newFCoord);
                }
            }
        }
    }
    
    
    @Override
    public Denotator changeAddress(Module newAddress) {
        if (getAddress().equals(newAddress)) {
            return this;
        }
        else if (getCoordinate() == getFrameCoordinate()) {
            Morphism newCoord = getCoordinate().changeAddress(newAddress);
            if (newCoord == getCoordinate()) {
                return this;
            }
            else {
                return new ListDenotator(null, getListForm(), newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(newAddress);
            Morphism newFCoord = getFrameCoordinate().changeAddress(newAddress);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new ListDenotator(null, getListForm(), newCoord, newFCoord);
            }
        }
    }
    
    
    @Override
    public Denotator changeAddress(ModuleMorphism morphism) {
        if (getCoordinate() == getFrameCoordinate()) {
            Morphism newCoord = getCoordinate().changeAddress(morphism);
            if (newCoord == getCoordinate()) {
                return this;
            }
            else {
                return new ListDenotator(null, getListForm(), newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(morphism);
            Morphism newFCoord = getFrameCoordinate().changeAddress(morphism);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new ListDenotator(null, getListForm(), newCoord, newFCoord);
            }
        }
    }

    
    @Override
    protected Denotator get(int[] path, int curpos)
            throws RubatoException {
        if (curpos == path.length) {
            return this;
        }
        else if (curpos > path.length) {
            throw new RubatoException("ListDenotator.get: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else if (getFactorCount() == 1 && path[curpos] == 0) {
            return getFactor(0).get(path, curpos+1);
        }
        //added by florian. must have been a bug.
        else if (getFactorCount() > path[curpos]) {
        	return getFactor(path[curpos]).get(path, curpos+1);
        }
        else {
            return null;
        }
    }
    
    
    @Override
    protected Denotator replace(int[] path, int curpos, Denotator d)
            throws RubatoException {
        if (curpos == path.length) {
            if (d.hasForm(getForm())) {
                Denotator res = d;
                if (!d.getAddress().equals(getAddress())) {
                    res = d.changeAddress(getAddress());
                    if (res == null) {
                        throw new RubatoException("ListDenotator.replace: Could not change address "+
                                                  "from %1 to %2", d.getAddress(), getAddress());
                    }
                }
                return res;
            }
            else {
                throw new RubatoException("ListDenotator.replace: Expected denotator of "+
                                          "form %1, but got %2", getForm(), d.getForm());
            }
        }
        else if (curpos > path.length) {
            throw new RubatoException("LimitDenotator.replace: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else if (curpos == path.length-1 && path[curpos] == 0) {
            if (d.hasForm(getListForm().getForm())) {
                Denotator res = d;
                if (!d.getAddress().equals(getAddress())) {
                    res = d.changeAddress(getAddress());
                    if (res == null) {
                        throw new RubatoException("ListDenotator.replace: Could not change address "+
                                                  "from %1 to %2", d.getAddress(), getAddress());
                    }
                }
                List<Denotator> denoList = new LinkedList<Denotator>();
                denoList.add(res);
                return _make_unsafe(null, getAddress(), getListForm(), denoList);
            }
            throw new RubatoException("ListDenotator.replace: Expected denotator of "+
                                      "form %1, but got %2", getListForm().getForm(), d.getForm());
        }
        else {
            LinkedList<Denotator> denoList = new LinkedList<Denotator>();
            for (Denotator deno : getFactors()) {
                denoList.add(deno.replace(path, curpos+1, d));
            }
            return _make_unsafe(null, getAddress(), getListForm(), denoList);
        }
    }    
    
    
    @Override
    protected Denotator map(int[] path, int curpos, ModuleMorphism morphism)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("ListDenotator.map: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        if (path[curpos] == 0) {
            boolean changed = false;
            LinkedList<Denotator> newFactors = new LinkedList<Denotator>();        
            for (Denotator factor : getFactors()) {
                Denotator newFactor = factor.map(path, curpos+1, morphism);
                if (newFactor != factor) {
                    changed = true;
                }
                newFactors.add(newFactor);
                
            }
            if (changed) {
                return DenoFactory.makeDenotator(getForm(), newFactors);
            }
            else {
                return this;
            }
        }
        return this;
    }


    @Override
    protected ModuleElement getElement(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("ListDenotator.getElement: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        if (getFactorCount() == 1 && path[curpos] == 0) {
            return getFactor(0).getElement(path, curpos+1);
        }
        else {
            return null;
        }
    }
    
    
    @Override
    protected ModuleMorphism getModuleMorphism(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("ListDenotator.getModuleMorphism: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        if (getFactorCount() == 1 && path[curpos] == 0) {
            return getFactor(0).getModuleMorphism(path, curpos+1);
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Returns the form of this denotator.
     */
    public ListForm getListForm() {
        return (ListForm)getForm();
    }

    
    /**
     * Returns the number of coordinates of the denotator.
     * 
     * @return number of coordinates
     */
    public int getFactorCount() {
        return getListMorphismMap().getFactorCount();
    }


    /**
     * Returns the factor at position <code>i</code>.
     * 
     * @param i denotator at position <code>i</code>
     */
    public Denotator getFactor(int i) {
        return getListMorphismMap().getFactor(i);
    }


    /**
     * Sets the factor <code>d</code> in position <code>i</code>.
     * This is a destructive operation; beware of aliasing.
     * 
     * @throws RubatoException if <code>d</code> is not of the required form
     *         or <code>i</code> is out of range
     */
    public void setFactor(int i, Denotator d)
            throws RubatoException {
        ListMorphismMap listmap = getListMorphismMap();
        if (i < 0 || i >= listmap.getFactorCount()) {
            throw new RubatoException("ListDenotator.setFactor: Expected index "+
                                      "0 <= index < %1, but got %2", listmap.getFactorCount(), i);
        }
        checkDenotator(d, getListForm().getForm(), getAddress());
        listmap.setFactor(i, d);
    }


    /**
     * Appends a factor.
     * This is a destructive operation; beware of aliasing.
     * 
     * @throws RubatoFormException if <code>d</code> is not of the required form
     */
    public boolean appendFactor(Denotator d)
            throws RubatoException {
        checkDenotator(d, getListForm().getForm(), getAddress());
        getListMorphismMap().appendFactor(d);
        return true;
    }


    /**
     * Prepends a factor.
     * This is a destructive operation; beware of aliasing.
     * 
     * @throws RubatoFormException if <code>d</code> is not of the required form
     */
    public void prependFactor(Denotator d)
            throws RubatoException {
        checkDenotator(d, getForm().getForm(0), getAddress());
        getListMorphismMap().prependFactor(d);
    }

    
    /**
     * Sets the factor list to a new list of denotators.
     * 
     * @param denoList the list of factor denotators
     * @throws RubatoException if the list of denotators doesn't match
     *         the form of the denotator.
     */
    public void replaceFactors(List<Denotator> denoList)
            throws RubatoException {
        Form baseForm = getListForm().getForm();
        ListMorphismMap newMap = new ListMorphismMap(denoList.size());

        for (Denotator d : denoList) {
            checkDenotator(d, baseForm, getAddress());
            newMap.appendFactor(d);
        }
        getCoordinate().setMap(newMap);
    }
    
    
    /**
     * Returns a new denotator with <code>d</code> appended to this list denotator.
     * @throws RubatoException
     */
    public ListDenotator concat(ListDenotator d)
            throws RubatoException {
        return Lists.concat(this, d);
    }

    
    /**
     * Appends <code>d</code> to this denotator.
     * This is a destructive operation.
     * @throws RubatoException
     */
    public void appendTo(ListDenotator d) 
            throws RubatoException {
        Lists.appendTo(this, d);
    }

    
    /**
     * Returns a new denotator with <code>d</code> prepended to this list denotator.
     * @throws RubatoException
     */
    public ListDenotator prepend(ListDenotator d)
            throws RubatoException {
        return Lists.concat(this, d);
    }

    
    /**
     * Prepends <code>d</code> to this denotator.
     * This is a destructive operation.
     * @throws RubatoException
     */
    public void prependTo(ListDenotator d)
            throws RubatoException {
        Lists.prependTo(this, d);
    }

    
    /**
     * Returns a list of the coordinates of the denotator.
     */
    public List<Denotator> getFactors() {
        return new LinkedList<Denotator>(getListMorphismMap().getFactors());
    }
    
    /**
     * Returns the internal index of denotator <code>d</code>.
     */
    public int indexOf(Denotator d) {
    	return getListMorphismMap().indexOf(d);
    }


    /**
     * Returns an iterator over the factors.
     */
    @Override
    public Iterator<Denotator> iterator() {
        return getListMorphismMap().iterator();
    }


    public ListMorphismMap getListMorphismMap() {
        return (ListMorphismMap)getCoordinate().getMap();
    }
    
    
    public ListMorphismMap getFrameListMorphismMap() {
        return (ListMorphismMap)getFrameCoordinate().getMap();
    }

    
    @Override
    public boolean isConstant() {
        return getListMorphismMap().isConstant();
    }
    
    
    @Override
    public int compareTo(Denotator object) {
        int c = 0;
        if (this != object) {
            if (object instanceof ListDenotator) {
                c = compareTo((ListDenotator)object);
            }
            else {
                c = getForm().compareTo(object.getForm());
            }
        }
        return c;
    }


    /**
     * Compares two list denotators.
     */
    public int compareTo(ListDenotator other) {
        int c = getForm().compareTo(other.getForm());
        if (c == 0) {
            c = nameCompareTo(other);
            if (c == 0) {
                c = getListMorphismMap().compareTo(other.getListMorphismMap());
            }
        }
        return c;
    }


    /**
     * Checks for equality.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ListDenotator){
            return equals((ListDenotator)object);
        }
        else {
            return false;
        }
    }


    /**
     * Checks denotators of the same form for equality.
     */
    public boolean equals(ListDenotator other) {
        if (formEquals(other) && nameEquals(other)) {
            return getListMorphismMap().equals(other.getListMorphismMap());
        }
        else {
            return false;
        }
    }


    @Override
    public ListDenotator copy() {
        Morphism coord;
        Morphism frameCoord;
        if (getCoordinate() == getFrameCoordinate()) {
            coord = frameCoord = getCoordinate().copy();
        }
        else {
            coord = getCoordinate().copy();
            frameCoord = getCoordinate().copy();
        }
        return new ListDenotator(getName(), getListForm(), coord, frameCoord);
    }


    @Override
    public boolean check() {
        // any exception indicates inconsistency
        try {
            // is the form of type list?
            if (getForm() instanceof ListForm) {
                ListForm f = (ListForm)getForm();
                Form factorForm = f.getForm();
                Module address = getAddress();
                List<Denotator> factors = getFactors();
                // check consistency of the factors                
                for (Denotator d : factors) {
                    if (!d.check()) {
                        // the current factor is inconsistent
                        return false;
                    }
                    else {
                        // has the current factor the required form?
                        if (!d.hasForm(factorForm)) {
                            return false;
                        }
                        // is the address of the current factor correct?
                        else if (!d.getAddress().equals(address)) {
                            return false;
                        }
                        else {
                            return true;
                        }
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    
    @Override
    protected LinkedList<Denotator> getDependencies(LinkedList<Denotator> list) {
        if (!list.contains(this)) {
            list.add(this);
            list = getCoordinate().getDenotatorDependencies(list);
            if (getCoordinate() != getFrameCoordinate()) {
                list = getFrameCoordinate().getDenotatorDependencies(list);
            }
        }
        return list;
    }
    
    
    private static final String FORM_ATTR  = "form";
    private static final String NAME_ATTR  = "name";
    private static final String TYPE_VALUE = "list";
    
    @Override
    public void toXML(XMLWriter writer) {
        Object[] attrs = new Object[4+(getName()!=null?2:0)];
        attrs[0] = TYPE_ATTR;
        attrs[1] = TYPE_VALUE;
        attrs[2] = FORM_ATTR;
        attrs[3] = getForm().getNameString();
        if (getName() != null) {
            attrs[4] = NAME_ATTR;
            attrs[5] = getNameString();
        }
        writer.openBlock(DENOTATOR, attrs);

        for (Denotator d : getFactors()) {
            writer.writeDenotatorRef(d);
        }
        
        writer.closeBlock();
    }

    
    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     * 
     * @return a list denotator or null if parsing failed
     */
    public static ListDenotator fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));
        
        if (!element.hasAttribute(FORM_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3.", TYPE_VALUE, DENOTATOR, FORM_ATTR);
            return null;                                                
        }
        String formName = element.getAttribute(FORM_ATTR);
        Form form = reader.getForm(formName);
        if (form == null) {
            reader.setError("Form with name %%1 does not exist.", formName);
            return null;
        }
        if (!(form instanceof ListForm)) {
            reader.setError("Form with name %%1 is not a form of type %%2.", formName, TYPE_VALUE);
            return null;
        }
        ListForm listForm = (ListForm)form;
        
        NameDenotator name = null;
        if (element.hasAttribute(NAME_ATTR)) {
            String nameString = element.getAttribute(NAME_ATTR);
            name = NameDenotator.make(nameString);            
        }
        
        LinkedList<Denotator> factorList = new LinkedList<Denotator>();
        Element childElement = XMLReader.getChild(element, DENOTATOR);

        while (childElement != null) {
            Denotator denotator = reader.parseDenotator(childElement);
            if (denotator == null) {
                return null;
            }
            factorList.add(denotator);
            childElement = XMLReader.getNextSibling(childElement, DENOTATOR);
        }
        
        try {
            ListDenotator denotator = new ListDenotator(name, listForm, factorList);
            return denotator;
        }
        catch (Exception e) {
            reader.setError(e.getMessage());
            return null;
        }
    }
    
    
    @SuppressWarnings("nls")
    protected void display(PrintStream out, LinkedList<Denotator> recursionCheckStack, int indent) {
        indent(out, indent);
        out.print("Name: \""+getNameString()+"\"");
        out.print("; Form: \""+getForm().getNameString()+"\"");
        out.print("; Type: "+Form.typeToString(getForm().getType()));
        out.println("; Address: "+getAddress());

        if (recursionCheck(recursionCheckStack)) {
            indent(out, indent+4);
            out.println("...");
            return;            
        }

        recursionCheckStack.addFirst(this);
        for (Denotator d : this) {
            d.display(out, recursionCheckStack, indent+4);
        }
        recursionCheckStack.removeFirst();
    }

    
    /**
     * Creates a new list denotator without checking whatsoever.
     * The arguments must result in a correct denotator, otherwise
     * there may be nasty consequences.
     */
    @Unsafe
    @Internal
    public static ListDenotator _make_unsafe(NameDenotator name, Module address, 
                                             ListForm form, List<Denotator> denoList) {
        ListMorphismMap map = new ListMorphismMap(denoList);
        FormDiagram diagram = form.getFormDiagram();
        CompoundMorphism coordinate = new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIST), map);
        ListDenotator res = new ListDenotator(name, form, coordinate, coordinate);
        assert(res._is_valid());
        return res;
    }

    
    /**
     * Returns true iff this denotator is correctly built.
     */
    @Internal
    public boolean _is_valid() {
        for (Denotator d : this) {
            if (d == null) {
                return false;
            }
            try {
                checkDenotator(d, getListForm().getForm(), getAddress());
            }
            catch (RubatoAddressException e) {
                return false;
            }
            catch (RubatoFormException e) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Generic list denotator constructor.
     */
    private ListDenotator(NameDenotator name, ListForm form, 
                          Morphism coordinate, Morphism frameCoordinate) {
        super(name, form, coordinate, frameCoordinate);
    }
}
