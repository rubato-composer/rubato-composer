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
import java.util.Iterator;
import java.util.LinkedList;

import org.rubato.base.Internal;
import org.rubato.base.RubatoException;
import org.rubato.base.Unsafe;
import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Rational;
import org.rubato.math.arith.ZString;
import org.rubato.math.module.*;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Simple denotator class.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class SimpleDenotator extends Denotator {

    /**
     * Constructs a simple addressed denotator of the specified form.
     * The address is the domain of the specified module morphism.
     * 
     * @param name the name of the denotator
     * @param form the form of the denotator, must be of type simple
     * @param morphism the module morphism contained in the denotator
     * @throws DomainException if the codomain of the module morphism
     *                         is not equal to the codomain required by the form   
     */
    public SimpleDenotator(NameDenotator name, SimpleForm form, ModuleMorphism morphism)
            throws DomainException {
        super(name, form);

        Module address = morphism.getDomain();
        Module module = form.getModule();
        if (!module.equals(morphism.getCodomain())) {
            throw new DomainException(module, morphism.getCodomain());
        }

        setCoordinates(new CompoundMorphism(address, module, ModuleMorphismMap.make(morphism)));
    }


    /**
     * Constructs a simple null-addressed denotator of the specified form.
     *
     * @param name the name of the denotator
     * @param form the form of the denotator, must be of type simple
     * @param element the module element contained in the denotator 
     * @throws DomainException if the element is not contained
     *                         in the codomain required by the form
     */
    public SimpleDenotator(NameDenotator name, SimpleForm form, ModuleElement element)
            throws DomainException {
        super(name, form);
        
        Module address = element.getModule().getNullModule();
        Module module = form.getModule();
        if (!module.hasElement(element)) {
            throw new DomainException(module, element.getModule());
        }
        
        setCoordinates(new CompoundMorphism(address, module, new ConstantModuleMorphismMap(element)));
    }

    
    /**
     * Constructs a simple (constant) addressed denotator of the specified form.
     *
     * @param name the name of the denotator
     * @param form the form of the denotator, must be of type simple
     * @param address the address of the denotator
     * @param element the module element contained in the denotator 
     * @throws DomainException if the element is not contained
     *                         in the codomain required by the form
     */
    public SimpleDenotator(NameDenotator name, SimpleForm form, Module address, ModuleElement element)
            throws DomainException {
        super(name, form);
        
        Module module = form.getModule();
        if (!module.hasElement(element)) {
            throw new DomainException(module, element.getModule());
        }
        
        setCoordinates(new CompoundMorphism(address, module, new ConstantModuleMorphismMap(address, element)));
    }

    
    /**
     * Constructs a simple addressed denotator of the specified form.
     * The address is the domain of the specified module morphism map.
     * 
     * @param name the name of the denotator
     * @param form the form of the denotator, must be of type simple
     * @param map the module morphism map contained in the denotator
     * @throws DomainException if the codomain of the module morphism
     *         is not equal to the codomain required by the form   
     */
    public SimpleDenotator(NameDenotator name, SimpleForm form, ModuleMorphismMap map) 
            throws DomainException {
        super(name, form);

        Module address = map.getDomain();
        Module module = form.getModule();
        if (!module.equals(map.getCodomain())) {
            throw new DomainException(module, map.getCodomain()); 
        }

        setCoordinates(new CompoundMorphism(address, module, map));
    }


    /**
     * Returns the type of the denotator.
     */
    @Override
    public int getType() {
        return SIMPLE;
    }
    
    
    /**
     * Returns the simple form of the denotator.
     */
    public SimpleForm getSimpleForm() {
        return (SimpleForm)getForm();
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
        return new SimpleDenotator(name, getSimpleForm(), coord, frameCoord);
    }
    
    
    /**
     * returns a new denotator evaluated at address <code>element</code>,
     * if this denotator is not null addressed,
     * otherwise returns self, whatever <code>element</code> is.
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
                    return new SimpleDenotator(null, getSimpleForm(), newCoord, newCoord);
                }
            }
            else {
                Morphism newCoord = getCoordinate().at(element);
                Morphism newFCoord = getFrameCoordinate().at(element);
                if (newCoord == getCoordinate() && newFCoord == getFrameCoordinate()) {
                    return this;
                }
                else {
                    return new SimpleDenotator(null, getSimpleForm(), newCoord, newFCoord);
                }
            }
        }
    }

    
    @Override
    public Denotator changeAddress(Module newAddress) {
        if (getAddress().equals(newAddress)) {
            return this;
        }
        boolean coordSame = (getCoordinate() == getFrameCoordinate());
        ModuleMorphismMap newMap;
        newMap = getModuleMorphismMap().changeDomain(newAddress);
        if (newMap == null) {
            return null;
        }
        if (coordSame) {
            if (newMap == getModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, cm);
            }
        }
        else {
            ModuleMorphismMap newFrameMap;
            newFrameMap = getFrameModuleMorphismMap().changeDomain(newAddress);
            if (newFrameMap == null) {
                return null;
            }
            if (newMap == getModuleMorphismMap() &&
                newFrameMap == getFrameModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                CompoundMorphism fcm = new CompoundMorphism(newFrameMap.getDomain(), newFrameMap.getCodomain(), newFrameMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, fcm);
            }
        }       
    }
    

    @Override
    public Denotator changeAddress(ModuleMorphism morphism) {
        if (!getAddress().equals(morphism.getCodomain())) {
            return null;
        }
        boolean coordSame = (getCoordinate() == getFrameCoordinate());
        ModuleMorphismMap newMap;
        newMap = getModuleMorphismMap().changeDomain(morphism);
        if (newMap == null) {
            return null;
        }
        if (coordSame) {
            if (newMap == getModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, cm);
            }
        }
        else {
            ModuleMorphismMap newFrameMap;
            newFrameMap = getFrameModuleMorphismMap().changeDomain(morphism);
            if (newFrameMap == null) {
                return null;
            }
            if (newMap == getModuleMorphismMap() &&
                newFrameMap == getFrameModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                CompoundMorphism fcm = new CompoundMorphism(newFrameMap.getDomain(), newFrameMap.getCodomain(), newFrameMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, fcm);
            }
        }       
    }

    
    @Override
    protected Denotator get(int[] path, int curpos)
            throws RubatoException {
        if (curpos == path.length) {
            return this;
        }
        else {
            throw new RubatoException("SimpleDenotator.get: Incompatible path, "+
                                      "expected path length == %1, but got length %2",
                                      curpos, path.length);
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
                        throw new RubatoException("SimpleDenotator.replace: Could not change address "+
                                                  "from %1 to %2", d.getAddress(), getAddress());
                    }
                }
                return res;
            }
            else {
                throw new RubatoException("SimpleDenotator.replace: Expected denotator of "+
                                          "form %1, but got %2", getForm(), d.getForm());
            }
        }
        else {
            throw new RubatoException("SimpleDenotator.replace: Incompatible path, "+
                                      "expected length == %1, but got length %2",
                                      curpos, path.length);
        }
    }

    @Override
    protected Denotator map(int[] path, int curpos, ModuleMorphism morphism)
            throws RubatoException {
        if (path.length != curpos) {
            throw new RubatoException("SimpleDenotator.map: Incompatible path, "+
                                      "expected length %1, but got length %2",
                                      curpos, path.length);
        }
        else {
            return map(morphism);
        }
    }
    
    
    /**
     * Maps the simple denotator using the given <code>morphism</code>.
     * If the denotator contains a module element, the new denotator
     * contains the mapped element.
     * If the denotator contains a morphism <i>f</i>, the new denotator
     * contains the composed morphism <i>f</i>.<code>morphism</code>.
     * @param morphism must have domain and codomain equal to
     *                 the module of the denotator
     */    
    public SimpleDenotator map(ModuleMorphism morphism)
            throws RubatoException {
        boolean coordSame = (getCoordinate() == getFrameCoordinate());
        ModuleMorphismMap newMap;
        newMap = getModuleMorphismMap().map(morphism);
        if (newMap == null) {
            return null;
        }
        if (coordSame) {
            if (newMap == getModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, cm);
            }
        }
        else {
            ModuleMorphismMap newFrameMap;
            newFrameMap = getFrameModuleMorphismMap().map(morphism);
            if (newFrameMap == null) {
                return null;
            }
            if (newMap == getModuleMorphismMap() &&
                newFrameMap == getFrameModuleMorphismMap()) {
                return this;
            }
            else {
                CompoundMorphism cm = new CompoundMorphism(newMap.getDomain(), newMap.getCodomain(), newMap);
                CompoundMorphism fcm = new CompoundMorphism(newFrameMap.getDomain(), newFrameMap.getCodomain(), newFrameMap);
                return new SimpleDenotator(null, getSimpleForm(), cm, fcm);
            }
        }       
    }
    
    
    @Override
    protected ModuleElement getElement(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("SimpleDenotator.getElement: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        return getModuleMorphismMap().getElement(path, curpos);
    }

    
    @Override
    protected ModuleMorphism getModuleMorphism(int[] path, int curpos)
            throws RubatoException {
        if (curpos >= path.length) {
            throw new RubatoException("SimpleDenotator.getModuleMorphism: Incompatible path, "+
                                      "expected length >= %1, but got length %2",
                                      curpos, path.length);
        }
        return getModuleMorphismMap().getMorphism();
    }
    

    /**
     * Returns an iterator over the coordinate denotators.
     * There are no coordinate denotators in a simple denotator.
     */
    @Override
    public Iterator<Denotator> iterator() {
        return new LinkedList<Denotator>().iterator();
    }

    
    /**
     * Returns the module element.
     */
    public ModuleElement getElement() {
        return getModuleMorphismMap().getElement();
    }


    /**
     * Returns the module element.
     * This is the case of a non-null addressed denotator.
     * 
     * @param element the address
     */
    public ModuleElement getElement(ModuleElement element)
            throws MappingException {
        return getModuleMorphism().map(element);
    }
    
    
    /**
     * Returns the integer contained in a ZElement denotator.
     */
    public int getInteger() {
        return ((ZElement)getElement()).getValue();
    }


    /**
     * Returns the modular integer contained in a ZnElement denotator.
     */
    public int getModInteger() {
        return ((ZnElement)getElement()).getValue();
    }    
    
    
    /**
     * Returns the double contained in an RElement denotator.
     */
    public double getReal() {
        return ((RElement)getElement()).getValue();
    }
    
    
    /**
     * Returns the rational contained in a QElement denotator.
     */
    public Rational getRational() {
        return ((QElement)getElement()).getValue();
    }
    
    
    /**
     * Returns the complex number contained in a CElement denotator.
     */
    public Complex getComplex() {
        return ((CElement)getElement()).getValue();
    }

    
    /**
     * Returns the ZString contained in a ZStringElement denotator.
     */
    public ZString getZString() {
        return ((ZStringElement)getElement()).getValue();
    }


    /**
     * Returns the string contained in a ZStringElement denotator.
     */
    public String getString() {
        return getZString().getString();
    }

    
    public SimpleDenotator sum(SimpleDenotator d)
            throws DomainException {
        return new SimpleDenotator(null, getSimpleForm(),
                                   getModuleMorphismMap().sum(d.getModuleMorphismMap()));
    }

    
    public SimpleDenotator difference(SimpleDenotator d) 
            throws DomainException {
        return new SimpleDenotator(null, getSimpleForm(),
                                   getModuleMorphismMap().difference(d.getModuleMorphismMap()));
    }

    
    /**
     * Returns the module morphism map.
     */
    public ModuleMorphismMap getModuleMorphismMap() {
        return (ModuleMorphismMap)getCoordinate().getMap();
    }
    
    
    /**
     * Returns the module morphism map.
     * Uses the frame coordinate instead of the coordinate.
     */
    public ModuleMorphismMap getFrameModuleMorphismMap() {
        return (ModuleMorphismMap)getFrameCoordinate().getMap();
    }
    
    
    /**
     * Returns the module morphism.
     */
    public ModuleMorphism getModuleMorphism() {
        return getModuleMorphismMap().getMorphism();
    }
    
    
    /**
     * Returns the module morphism.
     * Uses the frame coordinate instead of the coordinate.
     */
    public ModuleMorphism getFrameModuleMorphism() {
        return getFrameModuleMorphismMap().getMorphism();
    }
    
    
    @Override
    public int compareTo(Denotator object) {
        int c = 0;
        if (this != object) {
            if (object instanceof SimpleDenotator) {
                c = compareTo((SimpleDenotator)object);
            }
            else {
                c = getForm().compareTo(object.getForm());
            }
        }
        return c;
    }


    /**
     * Compares two simple denotators.
     */
    public int compareTo(SimpleDenotator other) {
        int c = getForm().compareTo(other.getForm());
        if (c == 0) {
            c = nameCompareTo(other);
            if (c == 0) {
                c = getModuleMorphismMap().compareTo(other.getModuleMorphismMap());
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
        else if (object instanceof SimpleDenotator) {
            return equals((SimpleDenotator)object);
        }
        else {
            return false;
        }
    }


    /**
     * Checks denotators of the same type for equality.
     */
    public boolean equals(SimpleDenotator other) {
        if (formEquals(other) && nameEquals(other)) {
            return getModuleMorphismMap().equals(other.getModuleMorphismMap());
        }
        else {
            return false;
        }
    }


    @Override
    public boolean isConstant() {
        return getModuleMorphismMap().isConstant();
    }

    
    @Override
    public SimpleDenotator copy() {
        Morphism coord;
        Morphism frameCoord;
        if (getCoordinate() == getFrameCoordinate()) {
            coord = frameCoord = getCoordinate().copy();
        }
        else {
            coord = getCoordinate().copy();
            frameCoord = getCoordinate().copy();
        }
        return new SimpleDenotator(getName(), getSimpleForm(), coord, frameCoord);
    }

    
    @Override
    public boolean check() {
        // any exception indicates inconsistency
        try {
            // is the form of type simple?
            if (getForm() instanceof SimpleForm) {
                SimpleForm f = (SimpleForm)getForm();
                // is the address equal to the domain of the module morphism?
                if (getAddress().equals(getModuleMorphism().getDomain())) {
                    // is the form module equal to the codomain of the module morphism?
                    if (f.getModule().equals(getModuleMorphism().getCodomain())) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    
    @Override
    protected LinkedList<Denotator> getDependencies(LinkedList<Denotator> list) {
        if (!list.contains(this)) {
            list.add(this);
        }
        return list;
    }
    
    
    private static final String TYPE_VALUE = "simple";
    
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

        // write the coordinate
        getModuleMorphismMap().toXML(writer);
        if (getCoordinate() != getFrameCoordinate()) {
            // write the frame coordinate, if not identical to the coordinate
            getFrameModuleMorphismMap().toXML(writer);
        }
        
        writer.closeBlock();
    }

    
    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     * 
     * @return a simple denotator or null if parsing failed
     */
    public static SimpleDenotator fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));

        // read the form
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
        if (!(form instanceof SimpleForm)) {
            reader.setError("Form with name %%1 is not a form of type %%2.", formName, TYPE_VALUE);
            return null;
        }

        // read the name
        NameDenotator name = null;
        if (element.hasAttribute(NAME_ATTR)) {
            String nameString = element.getAttribute(NAME_ATTR);
            name = NameDenotator.make(nameString);            
        }
        
        // read the coordinate (MorphismMap)
        MorphismMap map;
        CompoundMorphism cm;
        CompoundMorphism fcm;
        Element childElement = XMLReader.getChild(element, MORPHISMMAP);
        if (childElement != null) {
            map = reader.parseMorphismMap(childElement);
            
            if (map == null) {
                return null;
            }
            if (map instanceof ModuleMorphismMap) {
                ModuleMorphismMap mm = (ModuleMorphismMap)map; 
                fcm = cm = new CompoundMorphism(mm.getDomain(), mm.getCodomain(), mm);
            }
            else {
                reader.setError("Morphism map in a simple denotator must be a module morphism map.");
                return null;
            }

            // read the frame coordinate, if present
            childElement = XMLReader.getChild(childElement, MORPHISMMAP);
            if (childElement != null) {
                map = reader.parseMorphismMap(childElement);
                
                if (map == null) {
                    return null;
                }
                if (map instanceof ModuleMorphismMap) {
                    ModuleMorphismMap mm = (ModuleMorphismMap)map; 
                    fcm = new CompoundMorphism(mm.getDomain(), mm.getCodomain(), mm);
                }
                else {
                    reader.setError("Morphism map in a simple denotator must be a module morphism map.");
                    return null;
                }
            }

            return new SimpleDenotator(name, (SimpleForm)form, cm, fcm);
        }
        else {
            reader.setError("Missing element <%1>.", MORPHISMMAP);
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
            indent(out, indent);
            ModuleMorphismMap moduleMorphismMap = getModuleMorphismMap();
            if (moduleMorphismMap.isConstant()) {
                out.println(moduleMorphismMap.getElement());
            }
            else {
                out.println(moduleMorphismMap.getMorphism());
            }
        }        
    }    

    
    /**
     * Creates a new simple denotator without checking whatsoever.
     * The arguments must result in a correct denotator, otherwise
     * there may be nasty consequences.
     */
    @Unsafe
    @Internal
    public static SimpleDenotator _make_unsafe(NameDenotator name, SimpleForm form,
                                               Module address, ModuleElement element) {
        CompoundMorphism coordinate = new CompoundMorphism(address, form.getModule(), new ConstantModuleMorphismMap(address, element));
        SimpleDenotator res = new SimpleDenotator(name, form, coordinate, coordinate);
        assert(res._is_valid());
        return res;
    }

    
    /**
     * Creates a new simple denotator without checking whatsoever.
     * The arguments must result in a correct denotator, otherwise
     * there may be nasty consequences.
     */
    @Unsafe
    @Internal
    public static SimpleDenotator _make_unsafe(NameDenotator name, SimpleForm form,
                                               ModuleMorphism morphism) {
        CompoundMorphism coordinate = new CompoundMorphism(morphism.getDomain(), form.getModule(), new ModuleMorphismMap(morphism));
        SimpleDenotator res = new SimpleDenotator(name, form, coordinate, coordinate);
        assert(res._is_valid());
        return res;
    }

    
    /**
     * Returns true iff this denotator is correctly built.
     */
    @Internal
    public boolean _is_valid() {
        ModuleMorphismMap map = getModuleMorphismMap();
        return (map.getDomain().equals(getAddress()) &&
                map.getCodomain().equals(getSimpleForm().getModule()));
    }
    
    
    private SimpleDenotator(NameDenotator name, SimpleForm form, 
                            Morphism coordinate, Morphism frameCoordinate) {
        super(name, form, coordinate, frameCoordinate);
    }
}
