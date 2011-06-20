/*
 * Copyright (C) 2002 Gérard Milmeister
 * Copyright (C) 2002 Stefan Müller
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

package org.rubato.rubettes.denotex;

import java.util.*;

import org.rubato.math.module.Module;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.NameEntry;

public final class Symboltable {
    public Symboltable(Map<NameEntry,Form> forms,
                       Map<NameEntry,Denotator> namedDenotators,
                       List<Denotator> anonymousDenotators,
                       Map<String,Module> modules) {
        this.forms = forms;
        this.namedDenotators = namedDenotators;
        this.anonymousDenotators = anonymousDenotators;
        this.modules = modules;
    }

    public Symboltable() {
        forms = new Hashtable<NameEntry,Form>();
        namedDenotators = new Hashtable<NameEntry,Denotator>();
        anonymousDenotators = new LinkedList<Denotator>();
        modules = new Hashtable<String,Module>();
    }

    public Map<NameEntry,Form> forms() { return forms; }
    public Map<NameEntry,Denotator> ndenos() { return namedDenotators; }
    public List<Denotator> adenos() { return anonymousDenotators; }
    public Map<String,Module> modules() { return modules; }

    private Map<NameEntry,Form>      forms;
    private Map<NameEntry,Denotator> namedDenotators;
    private List<Denotator>          anonymousDenotators;
    private Map<String,Module>       modules;
}