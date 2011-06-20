/*
 * Copyright (C) 2007 Florian Thalmann
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

import java.util.*;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * Morphism map containing a set of morphisms (for power types).
 *
 * @author Florian Thalmann
 */
public final class AutoListMorphismMap implements MorphismMap {
    
	private TreeMap<Denotator,Integer> indexMap;
	private ArrayList<Denotator> currentFactors;
    private boolean indexUpdateNecessary;
    private boolean listUpdateNecessary;
	
    /**
     * Creates an empty FastListMorphismMap.
     */
    public AutoListMorphismMap() {
        this.indexMap = new TreeMap<Denotator,Integer>();
        this.currentFactors = new ArrayList<Denotator>();
    }
    

    /**
     * Creates a FastListMorphismMap.
     */
    public AutoListMorphismMap(Collection<Denotator> denotators) {
    	this.initMap(denotators);
    	this.currentFactors = new ArrayList<Denotator>(this.indexMap.keySet());
    }
    
    private void initMap(Collection<Denotator> denotators) {
    	this.indexMap = new TreeMap<Denotator,Integer>();
    	int currentIndex = 0;
    	for (Denotator currentDenotator: denotators) {
    		this.indexMap.put(currentDenotator, currentIndex);
    		currentIndex++;
    	}
    }

    /**
     * Returns the factor at position <code>index</code>.
     */
    public Denotator getFactor(int index) {
    	return this.getFactors().get(index);
    }
    

    /**
     * Appends the factor <code>d</code>.
     */
    public void appendFactor(Denotator d) {
    	this.indexMap.put(d, null);
    	this.indexUpdateNecessary = true;
    	this.listUpdateNecessary = true;
    }
    
    
    public void replaceFactor(int index, Denotator newD) {
    	this.removeFactor(index);
    	this.appendFactor(newD);
    }
    
    
    public void replaceFactor(Denotator oldD, Denotator newD) {
    	this.indexMap.remove(oldD);
    	this.appendFactor(newD);
    }
    
    
    /**
     * Removes the factor at <code>index</code>.
     */
    public Denotator removeFactor(int index) {
    	if (index < this.indexMap.size()) {
    		Denotator removed = this.getFactors().remove(index);
    		this.indexMap.remove(removed);
    		this.indexUpdateNecessary = true;
    		return removed;
    	} else throw new IndexOutOfBoundsException(index+" > "+(this.indexMap.size()-1));
    }
    

    /**
     * Returns the number of factors.
     */
    public int getFactorCount() {
        return this.indexMap.size();
    }

    
    /**
     * Returns the list of factors.
     */
    public ArrayList<Denotator> getFactors() {
    	if (this.listUpdateNecessary) {
    		this.currentFactors = new ArrayList<Denotator>(this.indexMap.keySet());
    		this.listUpdateNecessary = false;
    	}
    	return this.currentFactors;
    }
    
    /**
     * Returns the position index of factor <code>d</code>
     */
    public int indexOf(Denotator d) {
    	this.updateIndices();
    	Integer index = this.indexMap.get(d);
    	if (index != null) {
    		return index;
    	} else return -1;
    }
    
    private void updateIndices() {
    	if (this.indexUpdateNecessary) {
    		this.initMap(this.indexMap.keySet());
    		this.indexUpdateNecessary = false;
    	}
    }


    /**
     * Returns an iterator over the factors.
     */
    public Iterator<Denotator> iterator() {
        return this.getFactors().listIterator();    
    }


    public int compareTo(MorphismMap object) {
        if (this == object) {
            return 0;
        }
        else if (object instanceof AutoListMorphismMap) {
            return compareTo((AutoListMorphismMap)object);
        }
        else {
            return getClass().toString().compareTo(object.getClass().toString());
        }
    }
    
    
    public int compareTo(AutoListMorphismMap other) {
        int aCount = getFactorCount();
        int bCount = other.getFactorCount();   
        ArrayList<Denotator> aList = this.getFactors();
        ArrayList<Denotator> bList = other.getFactors();
        int c = 0;
        for (int i = 0; i < Math.min(aCount, bCount); i++) {
            if ((c = aList.get(i).compareTo(bList.get(i))) != 0) {
                return c;
            }
        }
        return bCount-aCount;
    }
    

    /**
     * Returns the list morphism map evaluated at address <code>element</code>.
     * 
     * @throws MappingException if evaluation fails
     */
    public MorphismMap at(ModuleElement element)
            throws MappingException {
        ArrayList<Denotator> newList = this.getFactors();
        boolean changed = false;
        for (int i = 0; i < newList.size(); i++) {            
            Denotator oldD = newList.get(i);
            Denotator newD = oldD.at(element);
            if (oldD != newD) {
                changed = true;
            }
            newList.set(i, newD);
        }
        if (changed) {
            return new AutoListMorphismMap(newList);
        }
        else {
            return this;
        }
    }

    
    public MorphismMap changeAddress(Module address) {
        ArrayList<Denotator> newList = new ArrayList<Denotator>();
        for (Denotator d : this.getFactors()) {
            Denotator newD = d.changeAddress(address);
            if (newD == null) {
                return null;
            }
            newList.add(newD);
        }
        return new AutoListMorphismMap(newList);
    }
    
    
    public MorphismMap changeAddress(ModuleMorphism morphism) {
        ArrayList<Denotator> newList = new ArrayList<Denotator>();
        for (Denotator d : this.getFactors()) {
            Denotator newD = d.changeAddress(morphism);
            if (newD == null) {
                return null;
            }
            newList.add(newD);
        }
        return new AutoListMorphismMap(newList);
    }
    
    
    public boolean isConstant() {
        for (Denotator d : this.getFactors()) {
            if (!d.isConstant()) {
                return false;
            }
        }
        return true;
    }
    
    
    public void toXML(XMLWriter writer) {        
        writer.openBlockWithType(MORPHISMMAP, getElementTypeName());
        for (Denotator d : this.getFactors()) {
            d.toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public MorphismMap fromXML(XMLReader reader, Element element) {
        ArrayList<Denotator> newList = new ArrayList<Denotator>();
        Element child = XMLReader.getChild(element, DENOTATOR);
        while (child != null) {
            Denotator denotator = reader.parseDenotator(child);
            if (denotator != null) {
                newList.add(denotator);
            }
            child = XMLReader.getNextSibling(element, DENOTATOR);
        }
        return new AutoListMorphismMap(newList);
    }
    
    
    public String getElementTypeName() {
        return "ListMorphismMap";
    }
    
    
    /**
     * Returns a copy of this list morphism map.
     */
    public Object clone() {
        return copy();
    }
    

    /**
     * Returns a copy of this list morphism map.
     */
    public AutoListMorphismMap copy() {
    	ArrayList<Denotator> copiedFactors = new ArrayList<Denotator>();
        for (Denotator d : this.getFactors()) {
        	copiedFactors.add(d.copy());
        }
        return new AutoListMorphismMap(copiedFactors);
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof AutoListMorphismMap) {
        	AutoListMorphismMap m = (AutoListMorphismMap)object;
            if (getFactorCount() != m.getFactorCount()) {
                return false;
            }
            else {
                for (int i = 0; i < getFactorCount(); i++) {
                    if (!getFactor(i).equals(m.getFactor(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        else {
            return false;
        }
    }
    
    
    public boolean fullEquals(MorphismMap map, IdentityHashMap<Object,Object> s) {
        if (this == map) {
            return true;
        }
        else if (map instanceof AutoListMorphismMap) {
        	AutoListMorphismMap lm = (AutoListMorphismMap)map;
            if (getFactorCount() != lm.getFactorCount()) {
                return false;
            }
            for (int i = 0; i < getFactorCount(); i++) {
                if (!(getFactor(i).equals(lm.getFactor(i)))) {
                   return false;
                }
            }
            return true;      
        }
        else {
            return false;
        }
    }


    public LinkedList<Form> getFormDependencies(LinkedList<Form> dependencyList) {
        return dependencyList;
    }

    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> depList) {
        for (Denotator d : this.getFactors()) {
            depList = d.getDependencies(depList);
        }
        return depList;
    }
    
    
    /**
     * Resolves all references.
     * @return true iff all references have been resolved.
     */
    public boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history) {
        for (int i = 0; i < getFactorCount(); i++) {
            Denotator d = getFactor(i);
            if (d instanceof DenotatorReference) {
                Denotator newDenotator = dict.getDenotator(d.getNameString());
                if (newDenotator == null) {
                    return false;
                }
                replaceFactor(d, newDenotator);
            }
            else {
                return d.resolveReferences(dict, history);
            }
        }
        return true;
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("AutoListMorphismMap[");
        List<Denotator> list = this.getFactors();
        if (list.size() > 0) {
           buf.append(list.get(0)); 
           for (int i = 1; i < list.size(); i++) {
               buf.append(",");
               buf.append(list.get(i));
           }
        }
        buf.append("]");
        return buf.toString();
    }

    
    /**
     * Returns a hash code of this list morphism map.
     */
    public int hashCode() {
        int hash = 7;
        for (Denotator d : this.getFactors()) {
            hash = 37*hash + d.hashCode();
        }
        return hash;
    }
    
}
