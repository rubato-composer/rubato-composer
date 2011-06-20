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

import static org.rubato.xml.XMLConstants.*;

import java.io.PrintStream;
import java.util.*;

import org.rubato.base.Internal;
import org.rubato.base.RubatoException;
import org.rubato.base.Unsafe;
import org.rubato.logeo.DenoFactory;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Limit denotator class.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class LimitDenotator 
        extends Denotator 
        implements FactorDenotator {

    /**
     * Creates a limit denotator.
     * 
     * @param name the name of the denotator
     * @param address the address this denotator must have
     * @param form the form of this denotator
     * @param coordinates the factors of this limit denotator
     * @throws RubatoException
     */
    public LimitDenotator(NameDenotator name, Module address, LimitForm form,
                          List<Denotator> coordinates)
            throws RubatoException {
        super(name, form);
        
        FormDiagram diagram = form.getFormDiagram();        
        ListMorphismMap map = new ListMorphismMap(coordinates.size());
        int s = Math.min(coordinates.size(), form.getFormCount());
        try {
            for (int i = 0; i < s; i++) {
                Denotator d = coordinates.get(i);
                if (!address.equals(d.getAddress())) {
                    d = d.changeAddress(address);                        
                }
                checkDenotator(d, diagram.getForm(i));
                map.appendFactor(d);
            }
            for (int i = s; i < form.getFormCount(); i++) {
                map.appendFactor(form.getForm(i).createDefaultDenotator(address));
            }
        }
        catch (RubatoException e) {
            throw e;
        }
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIMIT), map));
    }
    

    /**
     * Creates a limit denotator. The address is inferred from the addresses
     * of the factors.
     * @param name the name of the denotator
     * @param form the form of this denotator
     * @param coordinates the factors of this limit denotator
     * @throws RubatoException
     */
    public LimitDenotator(NameDenotator name, LimitForm form, List<Denotator> coordinates)
            throws RubatoException {
        super(name, form);
        FormDiagram diagram = form.getFormDiagram();        
        ListMorphismMap map = new ListMorphismMap(coordinates.size());
        Module address;        
        int s = Math.min(coordinates.size(), form.getFormCount());
        address = Address.getCommonDenotatorModule(coordinates);
        if (address == null) {
            throw new RubatoException("LimitDenotator.new: Cannot infer a common address.");
        }
        try {
            for (int i = 0; i < s; i++) {
                Denotator d = coordinates.get(i);
                checkDenotator(d, diagram.getForm(i));
                if (!address.equals(d.getAddress())) {
                    d = d.changeAddress(address);
                }
                map.appendFactor(d);
            }
            for (int i = s; i < form.getFormCount(); i++) {
                map.appendFactor(form.getForm(i).createDefaultDenotator(address));
            }
        }
        catch (RubatoException e) {
            throw e;
        }
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIMIT), map));
    }

    
    /**
     * Returns the type of the denotator.
     * 
     * @return type as an integer
     */
    @Override
    public int getType() {
        return LIMIT;
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
        return new LimitDenotator(name, getLimitForm(), coord, frameCoord);
    }

    
    /**
     * If not null-addressed returns a new denotator evaluated at address element.
     * If null-addressed, returns self.
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
                    return new LimitDenotator(null, getLimitForm(), newCoord, newCoord);
                }
            }
            else {
                Morphism newCoord = getCoordinate().at(element);
                Morphism newFCoord = getFrameCoordinate().at(element);
                if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                    return this;
                }
                else {
                    return new LimitDenotator(null, getLimitForm(), newCoord, newFCoord);
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
                return new LimitDenotator(null, getLimitForm(), newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(newAddress);
            Morphism newFCoord = getFrameCoordinate().changeAddress(newAddress);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new LimitDenotator(null, getLimitForm(), newCoord, newFCoord);
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
                return new LimitDenotator(null, getLimitForm(), newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(morphism);
            Morphism newFCoord = getFrameCoordinate().changeAddress(morphism);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new LimitDenotator(null, getLimitForm(), newCoord, newFCoord);
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
            throw new RubatoException("LimitDenotator.get: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else {
            int i = path[curpos];
            if (i >= 0 && i < getFactorCount()) {
                return getFactor(i).get(path, curpos+1);
            }
            else {
                return null;
            }
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
                        throw new RubatoException("LimitDenotator.replace: Could not change address "+
                                                  "from %1 to %2", d.getAddress(), getAddress());
                    }
                }
                return res;
            }
            else {
                throw new RubatoException("LimitDenotator.replace: Expected denotator of "+
                                          "form %1, but got %2", getForm(), d.getForm());
            }
        }
        else if (curpos > path.length) {
            throw new RubatoException("LimitDenotator.replace: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else {
            int i = path[curpos];
            if (i >= 0 && i < getFactorCount()) {
                ArrayList<Denotator> denoList = new ArrayList<Denotator>(getFactorCount());
                for (Denotator factor : getFactors()) {
                    denoList.add(factor);
                }
                denoList.set(i, getFactor(i).replace(path, curpos+1, d));
                return _make_unsafe(null, getAddress(), getLimitForm(), denoList);
            }
            else {
                throw new RubatoException("LimitDenotator.replace: Incompatible index in path,"+
                                          "expected 0 <= index < %1, but got %2", getFactorCount(), i); 
            }
        }
    }
    
    
    @Override
    protected Denotator map(int[] path, int curpos, ModuleMorphism morphism)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("LimitDenotator.map: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        boolean changed = false;
        int i = 0;
        LinkedList<Denotator> newFactors = new LinkedList<Denotator>();        
        for (Denotator factor : getFactors()) {
            if (path[curpos] == i) {
                Denotator newFactor = factor.map(path, curpos+1, morphism);
                if (newFactor != factor) {
                    changed = true;
                }
                newFactors.add(newFactor);
            }
            else {
                newFactors.add(factor);
            }
            i++;
        }
        if (changed) {
            return DenoFactory.makeDenotator(getForm(), newFactors);
        }
        else {
            return this;
        }
    }
    
    
    @Override
    protected ModuleElement getElement(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("LimitDenotator.getElement: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        int i = path[curpos];
        if (i >= 0 && i < getFactorCount()) {
            return getFactor(i).getElement(path, curpos+1);
        }
        else {
            return null;
        }
    }
    
    
    @Override
    protected ModuleMorphism getModuleMorphism(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("LimitDenotator.getModuleMorphism: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        int i = path[curpos];
        if (i >= 0 && i < getFactorCount()) {
            return getFactor(i).getModuleMorphism(path, curpos+1);
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Returns the limit form of this denotator.
     */
    public LimitForm getLimitForm() {
        return (LimitForm)getForm();
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
     * Returns the factor in position <code>i</code>.
     * 
     * @param i factor <code>i</code> denotator
     */
    public Denotator getFactor(int i) {
        return getListMorphismMap().getFactor(i);
    }


    /**
     * Returns the factor with the given label.
     * 
     * @throws RubatoException
     */
    public Denotator getFactor(String label) throws RubatoException {
        int i = getLimitForm().labelToIndex(label);
        if (i < 0) {
            throw new RubatoException("LimitDenotator.getFactor: Label \""+label+"\" does not exist.");
        }
        return getFactor(i);
    }


    /**
     * Sets the factor <code>d</code> in position <code>i</code>.
     * This is a destructive operation; beware of aliasing.
     * 
     * @throws RubatoException if <code>d</code> is not of the required form,
     *                         or <code>i</code> is not in range
     */
    public void setFactor(int i, Denotator d)
            throws RubatoException {
        ListMorphismMap listmap = getListMorphismMap();
        if (i < 0 || i >= listmap.getFactorCount()) {
            throw new RubatoException("LimitDenotator.setFactor: Expected "+
                                      "0 <= index < %1, but got ", listmap.getFactorCount(), i);
        }
        checkDenotator(d, getForm().getForm(i), getAddress());
        listmap.setFactor(i, d);
    }
    

    /**
     * Returns a list of the coordinates of the denotator.
     */
    public List<Denotator> getFactors() {
        LinkedList<Denotator> list = new LinkedList<Denotator>();
        for (int i = 0; i < getFactorCount(); i++) {
            list.add(getFactor(i));
        }
        return list;
    }


    public boolean appendFactor(Denotator d) {
        return false;
    }

    
    /**
     * Returns an iterator over the coordinates.
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
            if (object instanceof LimitDenotator) {
                c = compareTo((LimitDenotator)object);
            }
            else {
                c = getForm().compareTo(object.getForm());
            }
        }
        return c;
    }


    /**
     * Compares two limit denotators.
     */
    public int compareTo(LimitDenotator other) {
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
        else if (object instanceof LimitDenotator) {
            return equals((LimitDenotator)object);
        }
        else {
            return false;
        }
    }


    /**
     * Checks denotators of the same type for equality.
     */
    public boolean equals(LimitDenotator other) {
        if (formEquals(other) && nameEquals(other) &&
            getFactorCount() == other.getFactorCount()) {
            return getListMorphismMap().equals(other.getListMorphismMap());
        }
        else {
            return false;
        }
    }


    @Override
    public LimitDenotator copy() {
        Morphism coord;
        Morphism frameCoord;
        if (getCoordinate() == getFrameCoordinate()) {
            coord = frameCoord = getCoordinate().copy();
        }
        else {
            coord = getCoordinate().copy();
            frameCoord = getCoordinate().copy();
        }
        return new LimitDenotator(getName(), getLimitForm(), coord, frameCoord);
    }


    @Override
    public boolean check() {
        // any exception indicates inconsistency
        try {
            // is the form of type limit?
            if (getForm() instanceof LimitForm) {
                LimitForm f = (LimitForm)getForm();
                Module address = getAddress();
                if (f.getFormCount() == getFactorCount()) {
                    // check consistency of the factors
                    for (int i = 0; i < getFactorCount(); i++) {
                        Denotator d = getFactor(i);
                        Form factorForm = f.getForm(i);
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
    
    
    private static final String TYPE_VALUE = "limit";
        
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
     * @return a limit denotator or null if parsing failed
     */
    public static LimitDenotator fromXML(XMLReader reader, Element element) {
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
        if (!(form instanceof LimitForm)) {
            reader.setError("Form with name %%1 is not a form of type %%2.", formName, TYPE_VALUE);
            return null;
        }
        LimitForm limitForm = (LimitForm)form;
        
        NameDenotator name = null;
        if (element.hasAttribute(NAME_ATTR)) {
            String nameString = element.getAttribute(NAME_ATTR);
            name = NameDenotator.make(nameString);            
        }
        
        Element childElement = XMLReader.getChild(element, DENOTATOR);
        if (childElement != null) {
            LinkedList<Denotator> factorList = new LinkedList<Denotator>();
            while (childElement != null) {
                Denotator denotator = reader.parseDenotator(childElement);
                if (denotator == null) {
                    return null;
                }
                else {
                    factorList.add(denotator);
                }
                childElement = XMLReader.getNextSibling(childElement, DENOTATOR);
            }
            try {
                LimitDenotator denotator = new LimitDenotator(name, limitForm, factorList);
                return denotator;
            }
            catch (Exception e) {
                reader.setError(e.getMessage());
                return null;
            }
        }
        else {
            reader.setError("Denotator type %%1 is missing child element <%2>.", TYPE_VALUE, DENOTATOR);
            return null;
        }
    }
    
    
    @Override
    protected void display(PrintStream out, LinkedList<Denotator> recursionCheckStack, int indent) {
        indent(out, indent);
        out.print("Name: \""+getNameString()+"\"");
        out.print("; Form: \""+getForm().getNameString()+"\"");
        out.print("; Type: "+Form.typeToString(getForm().getType()));
        out.println("; Address: "+getAddress());

        indent += 4;
        
        if (recursionCheck(recursionCheckStack)) {
            indent(out, indent);
            out.println("...");
            return;            
        }
        else {
	        ListMorphismMap map = (ListMorphismMap)getCoordinate().getMap();
	        recursionCheckStack.addFirst(this);
	        for (int i = 0; i < map.getFactorCount(); i++) {
	            map.getFactor(i).display(out, recursionCheckStack, indent);
	        }
	        recursionCheckStack.removeFirst();
        }
    }

    
    /**
     * Creates a new limit denotator without checking whatsoever.
     * The arguments must result in a correct denotator, otherwise
     * there may be nasty consequences.
     */
    @Unsafe
    @Internal
    public static LimitDenotator _make_unsafe(NameDenotator name, Module address,
                                              LimitForm form, List<Denotator> denoList) {
        ListMorphismMap map = new ListMorphismMap(denoList);
        FormDiagram diagram = form.getFormDiagram();
        CompoundMorphism coordinate = new CompoundMorphism(address, new ProperIdentityMorphism(diagram, LIMIT), map);
        LimitDenotator res = new LimitDenotator(name, form, coordinate, coordinate);
        assert(res._is_valid());
        return res;
    }

    
    /**
     * Returns true iff this denotator is correctly built.
     */
    @Internal
    public boolean _is_valid() {
        if (getLimitForm().getFormCount() != getListMorphismMap().getFactorCount()) {
            return false;
        }
        else {
            int i = 0;
            for (Denotator d : this) {
                try {
                    checkDenotator(d, getLimitForm().getForm(i++), getAddress());
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
    }
    
    
    /**
     * Generic limit denotator constructor.
     */
    private LimitDenotator(NameDenotator name, LimitForm form, 
                           Morphism coordinate, Morphism frameCoordinate) {
        super(name, form, coordinate, frameCoordinate);
    }
}
