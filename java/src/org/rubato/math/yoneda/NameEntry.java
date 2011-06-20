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

import java.util.*;

/**
 * The unique respresention of names.
 * 
 * @author Gérard Milmeister
 */
public final class NameEntry implements Comparable<NameEntry> {

    public NameEntry() {
        names = new ArrayList<String>();
    }
    
    
    public NameEntry(String name) {
        String[] s = name.split("/");
        names = new ArrayList<String>();
        for (int i = 0; i < s.length; i++) {
            names.add(s[i]);
        }
    }


	public NameEntry(String name1, String name2) {
        names = new ArrayList<String>();
        String[] s;
        int i;
        s = name1.split("/");
        for (i = 0; i < s.length; i++) {
            names.add(s[i]);
        }
        s = name2.split("/");
        for (i = 0; i < s.length; i++) {
            names.add(s[i]);
        }
    }

    
    public NameEntry(List<String> nameList) {
        names = new ArrayList<String>();
        names.addAll(nameList);
    }

    
    public void add(String name) {
        names.add(name);
    }
    
    
    public String getString() {
        StringBuilder nameString = new StringBuilder(30);
        Iterator<String> iter = names.iterator();
        if (iter.hasNext()) {
            nameString.append(iter.next());
            while (iter.hasNext()) {
                nameString.append("/");                
                nameString.append(iter.next());                    
            }
        }
        return nameString.toString(); 
    }


    public String getString(int i) {
        return names.get(i);
    }

    
    public int getLength() {
        return names.size();
    }

    
    public boolean equals(Object object) {
        return names.equals(((NameEntry)object).names);
    }

    
    public int compareTo(NameEntry entry) {
        ArrayList<String> names1 = entry.names;
        int size = names.size();
        int size1 = names1.size();
        
        for (int i = 0; i < Math.min(size, size1); i++) {
            int c = names.get(i).compareTo(names1.get(i));
            if (c != 0) {
                return c;
            }
        }
        return size-size1;
    }


    public String toString() {
        return getString();        
    }


    public int hashCode() {
        return names.hashCode();
    }
    

    private ArrayList<String> names = null;

    //
    // Static lookup methods.
    //

    public static NameEntry lookup(NameEntry e) {
        NameEntry ne = nameTable.get(e);
        if (ne == null) {
            nameTable.put(e, e);
            return e;
        }
        else {
            return ne;
        }
    }


    public static NameEntry lookup(String name) {
        NameEntry e = new NameEntry(name);
        NameEntry ne = nameTable.get(e);
        if (ne == null) {
            nameTable.put(e, e);
            return e;
        }
        else {
            return ne;
        }
    }


    public static NameEntry lookup(String name1, String name2) {
        NameEntry e = new NameEntry(name1, name2);
        NameEntry ne = nameTable.get(e);
        if (ne == null) {
            nameTable.put(e, e);
            return e;
        }
        else {
            return ne;
        }
    }


    public static NameEntry lookup(List<String> names) {
        NameEntry e = new NameEntry(names);
        NameEntry ne = nameTable.get(e);
        if (ne == null) {
            nameTable.put(e, e);
            return e;
        }
        else {
            return ne;
        }
    }

    
    public static NameEntry concat(NameEntry a, NameEntry b) {
        LinkedList<String> names = new LinkedList<String>(a.names);
        names.addAll(b.names);
        NameEntry res = lookup(names);
        return res;
    }


    public static NameEntry concat(NameEntry a, String b) {
        LinkedList<String> names = new LinkedList<String>(a.names);
        names.add(b);
        return lookup(names);
    }


    public static NameEntry concat(String a, NameEntry b) {
        LinkedList<String> names = new LinkedList<String>();
        names.add(a);
        names.addAll(b.names);
        return lookup(names);
    }

    
    private static HashMap<NameEntry,NameEntry> nameTable = new HashMap<NameEntry,NameEntry>();           
}