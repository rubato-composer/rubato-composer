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
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Colimit denotator class.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class ColimitDenotator
        extends Denotator
        implements FactorDenotator {

    /**
     * Creates a colimit denotator.
     * 
     * @param name    the name of the denotator
     * @param address the address of the denotator
     * @param form    the form of the denotator
     * @param index   the index within the colimit form
     * @param deno    the factor of the denotator at the given index
     * @throws RubatoFormException
     */
    public ColimitDenotator(NameDenotator name, Module address, ColimitForm form, int index, Denotator deno)
            throws RubatoException {
        super(name, form);
        setIndex(index);
        
        FormDiagram diagram = (FormDiagram)form.getIdentifier().getCodomainDiagram();
        
        checkDenotator(deno, diagram.getForm(index), address);

        IndexMorphismMap map = new IndexMorphismMap(index, deno);
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, COLIMIT), map));
    }
    

    /**
     * Creates a colimit denotator that gets its address from the factor denotator.
     * 
     * @param name  the name of the denotator
     * @param form  the form of the denotator
     * @param index the index within the colimit form
     * @param deno  the factor of the denotator at the given index
     * @throws RubatoFormException
     */
    public ColimitDenotator(NameDenotator name, ColimitForm form, int index, Denotator deno)
            throws RubatoException {
        super(name, form);
        setIndex(index);
        
        FormDiagram diagram = (FormDiagram)form.getIdentifier().getCodomainDiagram();
        
        Module address = deno.getAddress();
        checkDenotator(deno, diagram.getForm(index), address);

        IndexMorphismMap map = new IndexMorphismMap(index, deno);
        setCoordinates(new CompoundMorphism(address, new ProperIdentityMorphism(diagram, COLIMIT), map));
    }
       
    
    /**
     * Returns the type of the denotator.
     * 
     * @return type as an integer
     */
    @Override
    public int getType() {
        return COLIMIT;
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
        return new ColimitDenotator(name, getColimitForm(), getIndex(),
                                    coord, frameCoord);
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
        assert(element != null);
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
                    return new ColimitDenotator(null, getColimitForm(), getIndex(), newCoord, newCoord);
                }
            }
            else {
                Morphism newCoord = getCoordinate().at(element);
                Morphism newFCoord = getFrameCoordinate().at(element);
                if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                    return this;
                }
                else {
                    return new ColimitDenotator(null, getColimitForm(), getIndex(), newCoord, newFCoord);
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
                return new ColimitDenotator(null, getColimitForm(), index, newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(newAddress);
            Morphism newFCoord = getFrameCoordinate().changeAddress(newAddress);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new ColimitDenotator(null, getColimitForm(), index, newCoord, newFCoord);
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
                return new ColimitDenotator(null, getColimitForm(), index, newCoord, newCoord);
            }
        }
        else {
            Morphism newCoord = getCoordinate().changeAddress(morphism);
            Morphism newFCoord = getFrameCoordinate().changeAddress(morphism);
            if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                return this;
            }
            else {
                return new ColimitDenotator(null, getColimitForm(), index, newCoord, newFCoord);
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
            throw new RubatoException("ColimitDenotator.get: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else {
            int i = path[curpos];
            if (i == getIndex()) {
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
        	//this.getForm().getForms().contains(d.getForm())
            if (d.hasForm(getForm())) {
                Denotator res = d;
                if (!d.getAddress().equals(getAddress())) {
                    res = d.changeAddress(getAddress());
                    if (res == null) {
                        throw new RubatoException("ColimitDenotator.replace: Could not change address "+
                                                  "from %1 to %2", d.getAddress(), getAddress());
                    }
                }
                return res;
            }
            else {
                throw new RubatoException("ColimitDenotator.replace: Expected denotator of "+
                                          "form %1, but got %2", getForm(), d.getForm());
            }
        }
        else if (curpos > path.length) {
            throw new RubatoException("ColimitDenotator.replace: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        else {
            int i = path[curpos];
            //florian replaced getFactorCount with getFormCount
            if (i >= 0 && i < this.getColimitForm().getFormCount()) {
                Denotator res = getFactor(i).replace(path, curpos+1, d);
                return _make_unsafe(null, getAddress(), getColimitForm(), i, res);
            }
            else {
                throw new RubatoException("ColimitDenotator.replace: Incompatible index in path,"+
                                          "expected 0 <= index < %1, but got %2", getFactorCount(), i); 
            }
        }
    }

    
    @Override
    protected Denotator map(int[] path, int curpos, ModuleMorphism morphism)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("ColimitDenotator.map: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        if (path[curpos] == getIndex()) {
            Denotator factor = getFactor();
            Denotator newFactor = factor.map(path, curpos+1, morphism);
            if (newFactor != factor) {
                return DenoFactory.makeDenotator(getForm(), getIndex(), newFactor);
            }
        }
        return this;
    }
    
    
    @Override
    protected ModuleElement getElement(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("ColimitDenotator.getElement: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        int i = path[curpos];
        if (i == getIndex()) {
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
            throw new RubatoException("ColimitDenotator.getModuleMorphism: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        int i = path[curpos];
        if (i == getIndex()) {
            return getFactor(i).getModuleMorphism(path, curpos+1);
        }
        else {
            return null;
        }
    }

    
    /**
     * Returns the form of the denotator.
     */
    public ColimitForm getColimitForm() {
        return (ColimitForm)getForm();
    }

    
    /**
     * Returns the number of coordinates of the denotator.
     * 
     * @return number of coordinates
     */
    public int getFactorCount() {
        return 1;
    }


    /**
     * Returns the factor in position <code>i</code>.
     * In this case, always returns the sole factor of the colimit denotator.
     * 
     * @param i factor <code>i</code> denotator
     * @return the sole factor
     */
    public Denotator getFactor(int i) {
        return getFactor();
    }


    /**
     * Returns the sole factor of the colimit denotator.
     */
    public Denotator getFactor() {
        return getIndexMorphismMap().getFactor();
    }
    

    /**
     * Returns an iterator over the factors.
     * In this case there is only one factor.
     */
    @Override
    public Iterator<Denotator> iterator() {
        LinkedList<Denotator> list = new LinkedList<Denotator>();
        list.add(getFactor());
        return list.iterator();
    }


    /**
     * Sets the factor <code>d</code> in position <code>i</code>.
     * This is a destructive operation; beware of aliasing.
     * @throws IllegalStateException if the position i is not in the required range
     * @throws RubatoFormException if d is not of the required form
     */
    public void setFactor(int i, Denotator d)
            throws RubatoException {
        IndexMorphismMap indexmap = (IndexMorphismMap)getCoordinate().getMap();
        if (i >= getForm().getFormCount()) {
            throw new IllegalStateException(""+i+" >= "+getForm().getFormCount());
        }
        checkDenotator(d, getForm().getForm(i), getAddress());
        indexmap.setFactor(i, d);
        setIndex(i);
    }

    
    /**
     * Sets the factor <code>d</code> at the given label.
     * This is a destructive operation; beware of aliasing.
     *
     * @throws RubatoException if d is not of the required form
     *         or the label does not exist
     */
    public void setFactor(String label, Denotator d)
            throws RubatoException {
        int i = getColimitForm().labelToIndex(label);
        if (i < 0) {
            throw new RubatoException("ColimitDenotator.setFactor: Label %%1 does not exist", label);            
        }
        IndexMorphismMap indexmap = (IndexMorphismMap)getCoordinate().getMap();
        if (i >= getForm().getFormCount()) {
            throw new RubatoException("ColimitDenotator.setFactor: Expected index "+
                                      "< %1, but got %2", getForm().getFormCount(), i);
        }
        checkDenotator(d, getForm().getForm(i), getAddress());
        indexmap.setFactor(i, d);
    }
    

    public boolean appendFactor(Denotator d) {
        return false;
    }

    
    /**
     * Returns a list of the factors of the denotator.
     * In this case there is only one factor.
     */
    public List<Denotator> getFactors() {
        LinkedList<Denotator> list = new LinkedList<Denotator>();
        list.add(getFactor());
        return list;
    }


    /**
     * Returns the index of the factor contained in a colimit denotator.
     */
    public final int getIndex() {
        return index;
    }
    
    
    /**
     * Returns the index label of the factor contained in a colimit denotator.
     */
    public final String getLabel() {
        return getColimitForm().indexToLabel(getIndex());
    }

    
    /**
     * Sets the index of the factor contained in a colimit denotator.
     */
    private final void setIndex(int index) {
        this.index = index;
    }

       
    @Override
    public int compareTo(Denotator object) {
        int c = 0;
        if (this != object) {
            if (object instanceof ColimitDenotator) {
                c = compareTo((ColimitDenotator)object);
            }
            else {
                c = getForm().compareTo(object.getForm());
            }
        }
        return c;
    }


    /**
     * Compares two colimit denotators.
     */
    public int compareTo(ColimitDenotator other) {
        int c = getForm().compareTo(other.getForm());
        if (c == 0) {
            c = nameCompareTo(other);
            if (c == 0) {
                IndexMorphismMap aMap = this.getIndexMorphismMap();
                IndexMorphismMap bMap = other.getIndexMorphismMap();
                c = aMap.compareTo(bMap);
            }
        }
        return c;
    }


    /**
     * Checks for equality.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ColimitDenotator){
            return equals((ColimitDenotator)object);
        }
        else {
            return false;
        }
    }


    /**
     * Checks denotators of the same type for equality.
     */
    public boolean equals(ColimitDenotator other) {
        if (formEquals(other) && nameEquals(other)) {
            return getIndexMorphismMap().equals(other.getIndexMorphismMap());
        }
        else {
            return false;
        }
    }
    
    
    public IndexMorphismMap getIndexMorphismMap() {
        return (IndexMorphismMap)getCoordinate().getMap();
    }

    
    public IndexMorphismMap getFrameIndexMorphismMap() {
        return (IndexMorphismMap)getFrameCoordinate().getMap();
    }
    

    @Override
    public boolean isConstant() {
        return getIndexMorphismMap().isConstant();
    }


    @Override
    public ColimitDenotator copy() {
        Morphism coord;
        Morphism frameCoord;
        if (getCoordinate() == getFrameCoordinate()) {
            coord = frameCoord = getCoordinate().copy();
        }
        else {
            coord = getCoordinate().copy();
            frameCoord = getCoordinate().copy();
        }
        return new ColimitDenotator(getName(), getColimitForm(), getIndex(),
                                    coord, frameCoord);
    }


    @Override
    public boolean check() {
        // any exception indicates inconsistency
        try {
            // is the form of type colimit?
            if (getForm() instanceof ColimitForm) {
                ColimitForm f = (ColimitForm)getForm();
                Module address = getAddress();
                if (getIndex() < f.getFormCount()) {
                    // check consistency of the factor
                    Denotator d = getFactor();
                    Form factorForm = f.getForm(getIndex());
                    if (!d.check()) {
                        // the factor is inconsistent
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

    
    private static final String FORM_ATTR  = "form";
    private static final String INDEX_ATTR = "index";
    private static final String NAME_ATTR  = "name";
    private static final String TYPE_VALUE = "colimit";    
    
    @Override
    public void toXML(XMLWriter writer) {
        Object[] attrs = new Object[6+(getName()!=null?2:0)];
        attrs[0] = TYPE_ATTR;
        attrs[1] = TYPE_VALUE;
        attrs[2] = FORM_ATTR;
        attrs[3] = getForm().getNameString();
        attrs[4] = INDEX_ATTR;
        attrs[5] = index;
        if (getName() != null) {
            attrs[6] = NAME_ATTR;
            attrs[7] = getNameString();
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
     * @return a colimit denotator or null if parsing failed
     */
    public static ColimitDenotator fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));
        
        if (!element.hasAttribute(FORM_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3", TYPE_VALUE, DENOTATOR, FORM_ATTR);
            return null;                                                
        }
        String formName = element.getAttribute(FORM_ATTR);
        Form form = reader.getForm(formName);
        if (form == null) {
            reader.setError("Form with name %%1 does not exist", formName);
            return null;
        }
        if (!(form instanceof ColimitForm)) {
            reader.setError("Form with name %%1 is not a form of type %%2", formName, TYPE_VALUE);
            return null;
        }
        ColimitForm colimitForm = (ColimitForm)form;
        
        int index;
        if (!element.hasAttribute(INDEX_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3", TYPE_VALUE, DENOTATOR, INDEX_ATTR);
            return null;                                                
        }
        try {
            index = Integer.parseInt(element.getAttribute(INDEX_ATTR));
            if (index < 0) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            reader.setError("Attribute %%1 of element <%2> must be an integer >= 0", INDEX_ATTR, DENOTATOR);
            return null;
        }
        
        NameDenotator name = null;
        if (element.hasAttribute(NAME_ATTR)) {
            String nameString = element.getAttribute(NAME_ATTR);
            name = NameDenotator.make(nameString);            
        }
        
        Element childElement = XMLReader.getChild(element, DENOTATOR);
        if (childElement != null) {
            Denotator denotator = reader.parseDenotator(childElement);
            if (denotator == null) {
                return null;
            }
            try {
                return new ColimitDenotator(name, colimitForm, index, denotator);
            }
            catch (Exception e) {
                reader.setError(e.getMessage());
                return null;
            }
        }
        else {
            reader.setError("Denotator type %%1 is missing child element <%2>", TYPE_VALUE, DENOTATOR);
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
            IndexMorphismMap map = (IndexMorphismMap)getCoordinate().getMap();
            recursionCheckStack.addFirst(this);
            map.getFactor().display(out, recursionCheckStack, indent);
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
    public static ColimitDenotator _make_unsafe(NameDenotator name, Module address,
                                                ColimitForm form, int index, Denotator d) {
        IndexMorphismMap map = new IndexMorphismMap(index, d);
        FormDiagram diagram = (FormDiagram)form.getIdentifier().getCodomainDiagram();
        CompoundMorphism coordinate = new CompoundMorphism(address, new ProperIdentityMorphism(diagram, COLIMIT), map);
        ColimitDenotator res = new ColimitDenotator(name, form, index, coordinate, coordinate);
        assert(res._is_valid());
        return res;
    }

    
    /**
     * Returns true iff this denotator is correctly built.
     */
    @Internal
    public boolean _is_valid() {
        if (getIndex() >= getColimitForm().getFormCount()) {
            return false;
        }
        else {
            try {
                checkDenotator(getFactor(), getColimitForm().getForm(getIndex()), getAddress());
            }
            catch (RubatoAddressException e) {
                return false;
            }
            catch (RubatoFormException e) {
                return false;
            }
            return true;
        }
    }
    
    
    /**
     * Generic colimit denotator constructor.
     */
    private ColimitDenotator(NameDenotator name, ColimitForm form, int index, 
                             Morphism coordinate, Morphism frameCoordinate) {
        super(name, form, coordinate, frameCoordinate);
        setIndex(index);
    }
    
    
    private int index;
}
