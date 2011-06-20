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

package org.rubato.composer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Rubette;
import org.rubato.rubettes.builtin.MacroRubette;

/**
 * The RubetteManager contains all available Rubette prototypes.
 * There is only one systemwide instance, retrievable using
 * the static getManager() method.
 * 
 * @author Gérard Milmeister
 */
public class RubetteManager {

    /**
     * Returns the systemwide RubetteManager instance.
     */
    public static RubetteManager getManager() {
        if (manager == null) {
            manager = new RubetteManager();
        }
        return manager;
    }
    
    
    public void setComposer(JComposer composer) {
        this.composer = composer;
    }
    
    
    public void addRubette(Rubette rubette) {
        String className = rubette.getClass().getCanonicalName();
        String rubName = rubette.getName();
        if (!classMap.containsKey(className)) {
            classMap.put(className, rubette);
        }
        if (!nameMap.containsKey(rubName)) {
            nameMap.put(rubName, rubette);
        }
        composer.addRubettePrototype(rubette);
    }
    
    
    public void addRubette(String className, Rubette rubette) {
        String rubName = rubette.getName();
        if (!classMap.containsKey(className)) {
            classMap.put(className, rubette);
        }
        if (!nameMap.containsKey(rubName)) {
            nameMap.put(rubName, rubette);
        }
        composer.addRubettePrototype(rubette);
    }
    
    
    public void addRubette(String className) {
        try {
            Class<?> cls = getClass().getClassLoader().loadClass(className);
            Object obj = cls.newInstance();
            if (obj instanceof Rubette) {
                addRubette(className, (Rubette)obj);
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    
    public void removeRubette(Rubette rubette) {
        if (rubette instanceof MacroRubette) {
            nameMap.remove(rubette.getName());
            composer.removeRubettePrototype(rubette);
        }
    }

    
    public void clear() {
        LinkedList<Rubette> toClear = new LinkedList<Rubette>();
        for (Rubette arubette : nameMap.values()) {
            if (arubette instanceof MacroRubette) {
                toClear.add(arubette);
            }
        }
        for (Rubette arubette : toClear) {
            removeRubette(arubette);
        }
    }
    
    
    public boolean hasRubetteByName(String name) {
        return nameMap.get(name) != null;        
    }
    
    
    public Rubette getRubetteByName(String name) {
        return nameMap.get(name);
    }
    

    public boolean hasRubetteByClassName(String className) {
        return classMap.get(className) != null;        
    }
    
    
    public Rubette getRubetteByClassName(String className) {
        return classMap.get(className);
    }
    
    
    public List<Rubette> getRubettes() {
        return new LinkedList<Rubette>(nameMap.values());
    }
    
    
    private RubetteManager() {
        MacroRubette rubette = new MacroRubette();
        String className = rubette.getClass().getCanonicalName();
        classMap.put(className, rubette);
    }
    
    
    private HashMap<String,Rubette> classMap = new HashMap<String,Rubette>();
    private HashMap<String,Rubette> nameMap =  new HashMap<String,Rubette>();
    
    private JComposer composer;
    private static RubetteManager manager;    
}
