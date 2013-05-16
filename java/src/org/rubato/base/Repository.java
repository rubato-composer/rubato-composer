/*
 * Copyright (C) 2002 Gérard Milmeister
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

package org.rubato.base;

import static org.rubato.logeo.DenoFactory.makeDenotator;
import static org.rubato.xml.XMLConstants.SCHEME;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.rubato.logeo.FormFactory;
import org.rubato.math.module.*;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.denotex.DenotexReader;
import org.rubato.scheme.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;

/**
 * A repository of forms and denotators, retrievable by their names.
 * There is a global repository, that should contain all forms
 * and named denotators used troughout the Rubato system.
 * 
 * @author Gérard Milmeister
 */
@SuppressWarnings("nls")
public class Repository
        extends Observable
        implements RubatoDictionary {

    /**
     * Registers a form with repository.
     * Performs a rollback, if a different form already exists with the same name.
     * @return the form in the repository 
     */
    public synchronized Form register(Form form) {
        Form f = register(form, false);
        if (f == null) {
            rollback();
        }
        else {
            setChanged();
            notifyObservers();
            reset();
        }
        return f;
    }
    
    
    /**
     * Registers a form as builtin with repository.
     * @return the form in the repository 
     */
    public synchronized Form registerBuiltin(Form form) {
        return form._register(this, true);
    }
    
    
    /**
     * Returns true iff the given form is a builtin.
     */
    public boolean isBuiltin(Form form) {
        return builtinForms.containsKey(form);
    }
        

    /**
     * Registers a denotator with repository.
     * @param d register d only if it has a non-null name
     * @return the registered denotator
     */
    public synchronized Denotator register(Denotator d) {
        Denotator deno = register(d, false);
        reset();
        setChanged();
        notifyObservers();
        return deno;
    }
    
    
    /**
     * Registers a denotator as builtin with repository.
     * @return the denotator in the repository 
     */
    public synchronized Denotator registerBuiltin(Denotator denotator) {
        return register(denotator, true);
    }
    
    
    /**
     * Returns true iff the given denotator is a builtin.
     */
    public boolean isBuiltin(Denotator denotator) {
        return builtinDenotators.containsKey(denotator);
    }
        

    /**
     * Registers a denotator with repository.
     * Adds the registered denotator to the temporaries.
     * @return the registered denotator
     */
    private Denotator register(Denotator denotator, boolean builtin) {
        if (denotator.getName() != null) {
            NameEntry nameEntry = denotator.getName().getNameEntry();
            denotators.put(nameEntry, new DenotatorItem(denotator, builtin));
            tmpDenos.add(denotator);
            if (builtin) {
                builtinDenotators.put(denotator, denotator);
            }
        }
        return denotator;        
    }

    
    /**
     * Registers the objects in the XML file of the reader.
     * @return true if successful
     */
    public synchronized boolean registerXML(Reader reader) { 
        XMLReader xmlReader = new XMLReader(reader, this);
        xmlReader.parse();
        if (xmlReader.hasError()) {
            return false;
        }

        if (register(xmlReader.getForms(), xmlReader.getDenotators())) {
            return true;
        }

        return false;
    }
    
    
    /**
     * Registers the objects in the XML file with the specified file name.
     * @return true if successful
     * @throws IOException
     */
    public synchronized boolean registerXMLFile(String filename) 
            throws IOException {
        XMLReader xmlReader = new XMLReader(new File(filename));
        xmlReader.parse();
        if (xmlReader.hasError()) {
            return false;
        }

        return register(xmlReader.getForms(), xmlReader.getDenotators());
    }
    
    
    public synchronized void registerDenotexResource(String denotex)
            throws RubatoException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        InputStream ins = loader.getResourceAsStream(denotex);
        DenotexReader reader = new DenotexReader(ins, this);
        reader.read();
        if (reader.hasError()) {
            throw new RubatoException(reader.getErrorMsg());
        }

        register(reader.getForms(), reader.getDenotators());
    }


    public synchronized void registerDenotexFile(String filename)
            throws RubatoException, IOException {
        FileInputStream fs = new FileInputStream(filename);
        BufferedInputStream bs = new BufferedInputStream(fs);
        DenotexReader reader = new DenotexReader(bs, this);
        reader.read();
        fs.close();
            
        if (reader.hasError()) {
            throw new RubatoException(reader.getErrorMsg());
        }

        register(reader.getForms(), reader.getDenotators());
    }

    
    /**
     * Registers a collection of forms and a collection of denotators.
     * Performs a rollback, if the registration fails
     * @param formSet a collection of forms
     * @param denotatorSet a collection of denotators
     * @return true if the registration succeeds
     */
    public synchronized boolean register(Collection<Form> formSet, Collection<Denotator> denotatorSet) {
        for (Form f : formSet) {
            Form form = f._register(this, false);
            if (form == null) {
                // registration failed
                rollback();
                reset();
                return false;
            }                    
        }
        
        for (Denotator deno : denotatorSet) {
            Denotator d = register(deno, false);
            if (d == null) {
                // registration failed
                rollback();
                reset();
                return false;
            }
        }

        // registration successful
        reset();
        return true;
    }


    /**
     * Registers a form.
     * Does <code>not</code> perform a rollback, if the registration fails
     */
    public Form register(Form form, boolean builtin) {
        if (form instanceof FormReference) {
            // do not register a form reference
            return form;
        }
        NameEntry name = form.getName().getNameEntry();
        FormItem item = forms.get(name);
        if (item == null) {
            // form not yet in repository
            if (form.getType() != Form.SIMPLE) {
                FormDiagram diagram = (FormDiagram)form.getIdentifier().getCodomainDiagram();
                if (!diagram.registerForms(this, builtin)) {
                    // registration failed
                    return null;
                }
            }
            forms.put(name, new FormItem(form, builtin));
            tmpForms.add(form);
            if (builtin) {
                builtinForms.put(form, form);
            }
            return form;
        }
        else if (item.getForm() == form) {
            // (pointer)-identical form already in repository
            return item.getForm();
        }
        else if (!item.getForm().fullEquals(form))  {
            // different form with same name in repository --> registration failed
            return null;
        }
        else {
            // same form already registered in repository 
            return item.getForm();
        }        
    }


    /**
     * Returns the form with the given name if it exists, otherwise null.
     */
    public synchronized Form getForm(NameDenotator name) {        
        FormItem item = forms.get(name.getNameEntry());
        return (item == null)?null:item.getForm();
    }
    

    /**
     * Returns the form with the given name if it exists, otherwise null.
     */
    public synchronized Form getForm(String name) {
        NameEntry nameEntry = NameEntry.lookup(name);        
        FormItem item = forms.get(nameEntry);
        return (item == null)?null:item.getForm();
    }
    
    
    /**
     * Returns the form with the given name if it exists, otherwise null.
     */
    public synchronized Form getForm(NameEntry name) {
        NameEntry nameEntry = NameEntry.lookup(name);        
        FormItem item = forms.get(nameEntry);
        return (item == null)?null:item.getForm();
    }

    
    public synchronized List<Form> getForms() {
        LinkedList<Form> res = new LinkedList<Form>();
        for (FormItem item : forms.values()) {
            res.add(item.getForm());
        }
        return res;
    }


    /**
     * Returns a power form with the given base form.
     * The new form is given the name "_Power(baseFormName)".
     */
    public PowerForm autogenPowerForm(Form baseForm) {
        PowerForm powerForm = autogenPowerForms.get(baseForm);
        if (powerForm == null) {
            String name = "_Power("+baseForm.getNameString()+")";
            powerForm = FormFactory.makePowerForm(name, baseForm);
            autogenPowerForms.put(baseForm, powerForm);
            register(powerForm);
        }
        return powerForm;
    }
    
    
    /**
     * Returns a list form with the given base form.
     * The new form is given the name "_List(baseFormName)".
     */
    public ListForm autogenListForm(Form baseForm) {
        ListForm listForm = autogenListForms.get(baseForm);
        if (listForm == null) {
            String name = "_List("+baseForm.getNameString()+")";
            listForm = FormFactory.makeListForm(name, baseForm);
            autogenListForms.put(baseForm, listForm);
            register(listForm);
        }
        return listForm;
    }
    
    
    /**
     * Returns a limit form with the given factors.
     * The new form is given the name "_Limit(factor0Name,factor1Name,...)".
     */
    public LimitForm autogenLimitForm(ArrayList<Form> factors) {
        LimitForm limitForm = autogenLimitForms.get(factors);
        if (limitForm == null) {
            StringBuilder buf = new StringBuilder();
            buf.append("_Limit(");
            buf.append(factors.get(0));
            for (int i = 0; i < factors.size(); i++) {
                buf.append(",");
                buf.append(factors.get(i));
            }
            buf.append(")");
            limitForm = FormFactory.makeLimitForm(buf.toString(), factors);
            autogenLimitForms.put(factors, limitForm);
            register(limitForm);
        }
        return limitForm;
    }
    
    
    /**
     * Returns a colimit form with the given factors.
     * The new form is given the name "_Colimit(factor0Name,factor1Name,...)".
     */
    public ColimitForm autogenColimitForm(ArrayList<Form> factors) {
        ColimitForm colimitForm = autogenColimitForms.get(factors);
        if (colimitForm == null) {
            StringBuilder buf = new StringBuilder();
            buf.append("_Colimit(");
            buf.append(factors.get(0));
            for (int i = 0; i < factors.size(); i++) {
                buf.append(",");
                buf.append(factors.get(i));
            }
            buf.append(")");
            colimitForm = FormFactory.makeColimitForm(buf.toString(), factors);
            autogenColimitForms.put(factors, colimitForm);
            register(colimitForm);
        }
        return colimitForm;
    }
    
    
    /**
     * Returns a simple form with the given module.
     * The new form is given the name "_Simple(moduleName)".
     */
    public SimpleForm autogenSimpleForm(Module module) {
        SimpleForm simpleForm = autogenSimpleForms.get(module);
        if (simpleForm == null) {
            String name = "_Simple("+module.toString()+")";
            simpleForm = FormFactory.makeModuleForm(name, module);
            autogenSimpleForms.put(module, simpleForm);
            register(simpleForm);
        }
        return simpleForm;
    }
    
    
    /**
     * Removes the form with given name in the repository.
     */
    public synchronized void removeForm(NameDenotator name) {
        forms.remove(name.getNameEntry());
        setChanged();
        notifyObservers();
    }
    
    
    /**
     * Removes the given form from the repository.
     * @param form the form to be removed
     */
    public void removeForm(Form form) {
        removeForm(form.getName());
    }


    /**
     * Removes the forms in the list from the repository.
     */
    private void removeForms(Collection<Form> formSet) {
        for (Form f : formSet) {
            removeForm(f);
        }
    }


    /**
     * Remove the denotator with the given name from the repository.
     * @param name the name for the denotator to be removed
     */
    public synchronized void removeDenotator(NameDenotator name) {
        denotators.remove(name.getNameEntry());
        setChanged();
        notifyObservers();
    }


    /**
     * Removes the given denotator from the repository.
     * @param d the denotator to be removed
     */
    public void removeDenotator(Denotator d) {
        removeDenotator(d.getName());
    }


    /**
     * Removes the denotators in the list from the repository.
     */
    private void removeDenotators(Collection<Denotator> denos) {
        for (Denotator d : denos) {
            removeDenotator(d);
        }
    }
    

    /**
     * Removes all temporary forms and denotators from the repository.
     */
    public void rollback() {
        removeForms(tmpForms);
        removeDenotators(tmpDenos);
        reset();
    }
    
    
    /**
     * Returns the system Scheme environment.
     */
    public Env getSchemeEnvironment() {
        return env;
    }
    
    
    /**
     * Returns the Scheme code.
     */
    public String getSchemeCode() {
        return code;
    }
    
    
    /**
     * Sets the Scheme code.
     * 
     * @return error as a string, and null if no error occurred
     */
    public String setSchemeCode(String s) {
        Parser parser = new Parser();
        List<SExpr> sexprList = parser.parse(s);
        if (parser.hasError()) {
            return parser.getError();
        }
        Evaluator evaluator = new Evaluator(env);
        evaluator.eval(sexprList);
        setChanged();
        notifyObservers();
        if (evaluator.hasErrors()) {
            return evaluator.getErrors().get(0);
        }
        code = s;
        return null;
    }
    

    /**
     * Clears the collections of temporary forms and denotators.
     */
    private void reset() {
        tmpForms.clear();
        tmpDenos.clear();
    }
    
    
    /**
     * Removes all content from the repository. 
     */
    public void clear() {
        reset();
        
        forms      = new HashMap<NameEntry,FormItem>(256);
        denotators = new HashMap<NameEntry,DenotatorItem>(1024);
        modules    = new HashMap<String,ModuleItem>(256);
        
        moduleElements  = new HashMap<String,ModuleElementItem>(1024);
        moduleMorphisms = new HashMap<String,ModuleMorphismItem>(1024);
        
        builtinForms      = new IdentityHashMap<Form,Form>(256);
        builtinDenotators = new IdentityHashMap<Denotator,Denotator>(256);
        
        autogenPowerForms   = new HashMap<Form,PowerForm>(256);
        autogenListForms    = new HashMap<Form,ListForm>(256);
        autogenLimitForms   = new HashMap<ArrayList<Form>,LimitForm>(256);
        autogenColimitForms = new HashMap<ArrayList<Form>,ColimitForm>(256);
        autogenSimpleForms  = new HashMap<Module,SimpleForm>(256);
        
        env = Env.makeGlobalEnvironment();
        code = "";
        
        setChanged();
        notifyObservers();
    }

    
    /**
     * Returns denotator with the given name if it exists, otherwise null.
     * @param name the name of the denotator to be returned
     */    
    public synchronized Denotator getDenotator(NameDenotator name) {
        DenotatorItem item = denotators.get(name.getNameEntry());
        return (item == null)?null:item.getDenotator();
    }

    
    /**
     * Returns denotator with the given name if it exists, otherwise null.
     * @param name the name of the denotator to be returned
     */    
    public synchronized Denotator getDenotator(NameEntry name) {
        NameEntry nameEntry = NameEntry.lookup(name);
        DenotatorItem item = denotators.get(nameEntry);
        return (item == null)?null:item.getDenotator();
    }

    
    /**
     * Returns denotator with the given name if it exists, otherwise null.
     * @param name the name of the denotator to be returned
     */    
    public synchronized Denotator getDenotator(String name) {
        NameEntry nameEntry = NameEntry.lookup(name);
        DenotatorItem item = denotators.get(nameEntry);
        return (item == null)?null:item.getDenotator();
    }


    public synchronized List<Denotator> getDenotators() {
        LinkedList<Denotator> res = new LinkedList<Denotator>();
        for (DenotatorItem item : denotators.values()) {
            res.add(item.getDenotator());
        }
        return res;
    }

    
    public void setNamespace(NameEntry ns) {
        namespace = ns;        
    }
    
    
    public NameEntry getCurrentNamespace() {
        return namespace;
    }
    
    
    public void registerBuiltinModule(String name, Module module) {
        modules.put(name, new ModuleItem(name, module, true));
    }
    
    
    public void registerModule(String name, Module module) {
        modules.put(name, new ModuleItem(name, module, false));
        setChanged();
        notifyObservers();
    }
    
    
    public Module getModule(String name) {
        ModuleItem item = modules.get(name);
        return (item == null)?null:item.getModule();
    }
    
    
    public List<String> getModuleNames() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(modules.keySet());
        return list;
    }
    
    
    public void registerModuleElement(String name, ModuleElement element) {
        moduleElements.put(name, new ModuleElementItem(name, element, false));
        setChanged();
        notifyObservers();
    }
    
    
    public ModuleElement getModuleElement(String name) {
        ModuleElementItem item = moduleElements.get(name);
        return (item == null)?null:item.getModuleElement();
    }
    
    
    public List<String> getModuleElementNames() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(moduleElements.keySet());
        return list;
    }
    
    
    public void registerModuleMorphism(String name, ModuleMorphism morphism) {
        moduleMorphisms.put(name, new ModuleMorphismItem(name, morphism, false));
        setChanged();
        notifyObservers();
    }
    
    
    public ModuleMorphism getModuleMorphism(String name) {
        ModuleMorphismItem item = moduleMorphisms.get(name);
        return (item == null)?null:item.getModuleMorphism();
    }
    
    
    public List<String> getModuleMorphismNames() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(moduleMorphisms.keySet());
        return list;
    }
    
    
    public List<String> getModuleMorphismNames(Module domain, Module codomain) {
        LinkedList<String> list = new LinkedList<String>();
        for (Entry<String,ModuleMorphismItem> entry : moduleMorphisms.entrySet()) {
            ModuleMorphism m = entry.getValue().getModuleMorphism();
            if (domain == null || domain.equals(m.getDomain())) {
                if (codomain == null || codomain.equals(m.getCodomain())) {
                    list.add(entry.getKey());
                }
            }
        }
        return list;
    }
    
    
    public void toXML(XMLWriter writer) {
        if (code.length() > 0) {
            writer.openBlock(SCHEME);
            writer.writeTextNode(code.trim()+"\n");
            writer.closeBlock();
        }        
        for (Entry<String,ModuleItem> entry : modules.entrySet()) {
            String name = entry.getKey();
            ModuleItem item = entry.getValue();
            if (!item.isBuiltin()) {
                writer.writeModule(name, item.getModule());
            }
        }
        for (Entry<String,ModuleElementItem> entry : moduleElements.entrySet()) {
            String name = entry.getKey();
            ModuleElementItem item = entry.getValue();
            if (!item.isBuiltin()) {
                writer.writeModuleElement(name, item.getModuleElement());
            }
        }
        for (Entry<String,ModuleMorphismItem> entry : moduleMorphisms.entrySet()) {
            String name = entry.getKey();
            ModuleMorphismItem item = entry.getValue();
            if (!item.isBuiltin()) {
                writer.writeModuleMorphism(name, item.getModuleMorphism());
            }
        }
        for (Entry<?,FormItem> entry : forms.entrySet()) {
            FormItem item = entry.getValue();
            if (!item.isBuiltin()) {
                writer.writeForm(item.getForm());
            }
        }
        for (Entry<?,DenotatorItem> entry : denotators.entrySet()) {
            DenotatorItem item = entry.getValue();
            if (!item.isBuiltin()) {
                writer.writeDenotator(item.getDenotator());
            }
        }
    }
    

    /**
     * Prints a tabular representation of the repository on stdout.
     */
    public void display() {
        display(System.out);
    }


    /**
     * Prints a tabular representation of the repository.
     * @param out print to this PrintStream
     */
    public void display(PrintStream out) {
        int width = 70;
        out.print("┍"); repeat(out, "━", width-2); out.println("┑");
        out.print("│Forms"); repeat(out, " ", width-7); out.println("│");
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        TreeSet<FormItem> formSet = new TreeSet<FormItem>(forms.values());
        for (FormItem item : formSet) {
            Form f = item.getForm();
            String s = f.getName().getNameEntry()+": "+f; 
            out.print("│"+s); repeat(out, " ", width-s.length()-2); out.println("│");
        }
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        out.print("│Denotators"); repeat(out, " ", width-12); out.println("│");
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        TreeSet<DenotatorItem> denoSet = new TreeSet<DenotatorItem>(denotators.values());
        for (DenotatorItem item : denoSet) {
            Denotator d = item.getDenotator();
            String s = d.getName().getNameEntry()+": "+d.getForm().getName().getNameEntry();
            out.print("│"+s); repeat(out, " ", width-s.length()-2); out.println("│");
        }
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        out.print("│Modules"); repeat(out, " ", width-9); out.println("│");
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        TreeSet<ModuleItem> moduleSet = new TreeSet<ModuleItem>(modules.values());
        for (ModuleItem item : moduleSet) {
            String s = item.getName()+": "+item.getModule();
            out.print("│"+s); repeat(out, " ", width-s.length()-2); out.println("│");
        }
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        out.print("│Module Elements"); repeat(out, " ", width-17); out.println("│");
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        TreeSet<ModuleElementItem> moduleElementSet = new TreeSet<ModuleElementItem>(moduleElements.values());
        for (ModuleElementItem item : moduleElementSet) {
            String s = item.getName()+": "+item.getModuleElement();
            out.print("│"+s); repeat(out, " ", width-s.length()-2); out.println("│");
        }
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        out.print("│Module Morphisms"); repeat(out, " ", width-18); out.println("│");
        out.print("├"); repeat(out, "─", width-2); out.println("┤");
        TreeSet<ModuleMorphismItem> moduleMorphismSet = new TreeSet<ModuleMorphismItem>(moduleMorphisms.values());
        for (ModuleMorphismItem item : moduleMorphismSet) {
            String s = item.getName()+": "+item.getModuleMorphism();
            out.print("│"+s); repeat(out, " ", width-s.length()-2); out.println("│");
        }
        out.print("┕");
        repeat(out, "━", width-2);
        out.print("┙");
        out.println();
    }

    
    /**
     * Print out the string <code>c</code> repeated <code>n</code> times.
     */
    private void repeat(PrintStream out, String c, int n) {
        for (int i = 0; i < n; i++) {
            out.print(c);
        }
    }
    

    /**
     * Returns the global system-wide repository.
     */    
    public static Repository systemRepository() {
        if (globalRepository == null) {
            globalRepository = makeGlobalRepository();
        }
        return globalRepository;
    }


    /**
     * Creates an empty repository.
     */
    private Repository() {
        clear();
    }
    

    /**
     * Creates the system repository with builtin forms and denotators.
     */
    private static synchronized Repository makeGlobalRepository() {
        Repository rep = new Repository();
        rep.initGlobalRepository();
        return rep;
    }
    
    
    private class Item {
        public Item(boolean builtin) {
            this.builtin = builtin;
        }
        public final boolean isBuiltin() {
            return builtin;
        }
        private boolean builtin;
    }
    
    
    private final class FormItem 
            extends Item
            implements Comparable<FormItem> {
        public FormItem(Form form, boolean builtin) {
            super(builtin);
            this.form = form;
        }
        public Form getForm() {
            return form;
        }
        public int compareTo(FormItem o) {
            return form.getNameString().compareTo(o.form.getNameString());
        }
        private Form form;
    }

    
    private final class DenotatorItem 
            extends Item
            implements Comparable<DenotatorItem> {
        public DenotatorItem(Denotator denotator, boolean builtin) {
            super(builtin);
            this.denotator = denotator;
        }
        public Denotator getDenotator() {
            return denotator;
        }
        public int compareTo(DenotatorItem o) {
            return denotator.getNameString().compareTo(o.denotator.getNameString());
        }
        private Denotator denotator;
    }

    
    private final class ModuleItem
            extends Item
            implements Comparable<ModuleItem> {
        public ModuleItem(String name, Module module, boolean builtin) {
            super(builtin);
            this.name = name;
            this.module = module;
        }
        public Module getModule() {
            return module;
        }
        public String getName() {
            return name;
        }
        public int compareTo(ModuleItem o) {
            return name.compareTo(o.name);
        }
        private String name;
        private Module module;
    }
    
    
    private final class ModuleElementItem
            extends Item
            implements Comparable<ModuleElementItem> {
        public ModuleElementItem(String name, ModuleElement element, boolean builtin) {
            super(builtin);
            this.name = name;
            this.element = element;
        }
        public ModuleElement getModuleElement() {
            return element;
        }
        public String getName() {
            return name;
        }
        public int compareTo(ModuleElementItem o) {
            return name.compareTo(o.name);
        }
        protected String        name;
        private   ModuleElement element;
    }
    
    
    private final class ModuleMorphismItem
            extends Item
            implements Comparable<ModuleMorphismItem> {
        public ModuleMorphismItem(String name, ModuleMorphism morphism, boolean builtin) {
            super(builtin);
            this.name = name;
            this.morphism = morphism;
        }
        public ModuleMorphism getModuleMorphism() {
            return morphism;
        }
        public int compareTo(ModuleMorphismItem o) {
            return morphism.compareTo(o.morphism);
        }
        public String getName() {
            return name;
        }
        public int compareTo(ModuleElementItem o) {
            return name.compareTo(o.name);
        }
        private String         name;
        private ModuleMorphism morphism;
    }
    
    
    public void initGlobalRepository() {
        clear();
        
        // Register simple types
        SimpleForm integerForm = FormFactory.makeZModuleForm("Integer"); 
        registerBuiltin(integerForm);
        SimpleForm realForm = FormFactory.makeRModuleForm("Real");
        registerBuiltin(realForm);
        SimpleForm rationalForm = FormFactory.makeQModuleForm("Rational");
        registerBuiltin(rationalForm);
        SimpleForm complexForm = FormFactory.makeCModuleForm("Complex");
        registerBuiltin(complexForm);
        SimpleForm stringForm = FormFactory.makeZStringModuleForm("String");
        registerBuiltin(stringForm);
        
        // Name form is handled separately
        registerNameForm(NameForm.getNameForm());
        
        // 2D and 3D vectors
        RFreeModule m2d = RProperFreeModule.make(2);
        registerBuiltin(FormFactory.makeModuleForm("Vector2D", m2d));
        RFreeModule m3d = RProperFreeModule.make(3);
        registerBuiltin(FormFactory.makeModuleForm("Vector3D", m3d));
        RFreeModule m4d = RProperFreeModule.make(4);
        registerBuiltin(FormFactory.makeModuleForm("Vector4D", m4d));
        RFreeModule m5d = RProperFreeModule.make(5);
        registerBuiltin(FormFactory.makeModuleForm("Vector5D", m5d));
        RFreeModule m6d = RProperFreeModule.make(6);
        registerBuiltin(FormFactory.makeModuleForm("Vector6D", m6d));
        RFreeModule m7d = RProperFreeModule.make(7);
        registerBuiltin(FormFactory.makeModuleForm("Vector7D", m7d));
        RFreeModule m8d = RProperFreeModule.make(8);
        registerBuiltin(FormFactory.makeModuleForm("Vector8D", m8d));
        RFreeModule m9d = RProperFreeModule.make(9);
        registerBuiltin(FormFactory.makeModuleForm("Vector9D", m9d));
        RFreeModule m10d = RProperFreeModule.make(10);
        registerBuiltin(FormFactory.makeModuleForm("Vector10D", m10d));
        RFreeModule m11d = RProperFreeModule.make(11);
        registerBuiltin(FormFactory.makeModuleForm("Vector11D", m11d));
        RFreeModule m12d = RProperFreeModule.make(12);
        registerBuiltin(FormFactory.makeModuleForm("Vector12D", m12d));
        
        // Polynomials
        PolynomialRing rpol = PolynomialRing.make(RRing.ring, "X");
        registerBuiltin(FormFactory.makeModuleForm("Real[X]", rpol));
        PolynomialRing zpol = PolynomialRing.make(ZRing.ring, "X");
        registerBuiltin(FormFactory.makeModuleForm("Integer[X]", zpol));
        PolynomialRing qpol = PolynomialRing.make(QRing.ring, "X");
        registerBuiltin(FormFactory.makeModuleForm("Rational[X]", qpol));
        PolynomialRing cpol = PolynomialRing.make(CRing.ring, "X");
        registerBuiltin(FormFactory.makeModuleForm("Complex[X]", cpol));
        
        // Tuples
        ProductRing realPair = ProductRing.make(RRing.ring, RRing.ring);
        registerBuiltin(FormFactory.makeModuleForm("RealPair", realPair));
        ProductRing realTriple = ProductRing.make(RRing.ring, RRing.ring, RRing.ring);
        registerBuiltin(FormFactory.makeModuleForm("RealTriple", realTriple));
        
        // Register booleans
        SimpleForm boolForm = FormFactory.makeZnModuleForm("Boolean", 2);
        registerBuiltin(boolForm);
        registerBuiltin(makeDenotator("False", boolForm, 0, 2));
        registerBuiltin(makeDenotator("True", boolForm, 1, 2));
        
        // Several non simple forms
        LimitForm boolPairForm = FormFactory.makeLimitForm("BooleanPair", boolForm, boolForm);
        registerBuiltin(boolPairForm);
        ListForm boolListForm = FormFactory.makeListForm("BooleanList", boolForm);
        registerBuiltin(boolListForm);
        
        ColimitForm intOrRealForm = FormFactory.makeColimitForm("IntegerOrReal", integerForm, realForm);
        registerBuiltin(intOrRealForm);
        
        // Sets
        PowerForm integerSetForm = FormFactory.makePowerForm("IntegerSet", integerForm);
        registerBuiltin(integerSetForm);
        PowerForm realSetForm = FormFactory.makePowerForm("RealSet", realForm);
        registerBuiltin(realSetForm);
        
        // Note and Score
        SimpleForm onsetForm = FormFactory.makeRModuleForm("Onset");
        registerBuiltin(onsetForm);
        SimpleForm pitchForm = FormFactory.makeQModuleForm("Pitch");
        registerBuiltin(pitchForm);
        SimpleForm loudnessForm = FormFactory.makeZModuleForm("Loudness");
        registerBuiltin(loudnessForm);
        SimpleForm durationForm = FormFactory.makeRModuleForm("Duration");
        registerBuiltin(durationForm);
        SimpleForm voiceForm = FormFactory.makeZModuleForm("Voice");
        registerBuiltin(voiceForm);
        
        List<Form> noteFormList = new LinkedList<Form>();
        List<String> noteFormLabelList = new LinkedList<String>();
        noteFormList.add(onsetForm);
        noteFormLabelList.add("onset");
        noteFormList.add(pitchForm);
        noteFormLabelList.add("pitch");
        noteFormList.add(loudnessForm);
        noteFormLabelList.add("loudness");
        noteFormList.add(durationForm);
        noteFormLabelList.add("duration");
        noteFormList.add(voiceForm);
        noteFormLabelList.add("voice");
        LimitForm noteForm = FormFactory.makeLimitForm("Note", noteFormList);
        noteForm.setLabels(noteFormLabelList);
        registerBuiltin(noteForm);
        
        PowerForm scoreForm = FormFactory.makePowerForm("Score", noteForm);
        registerBuiltin(scoreForm);
        
        // Karim's registration of the MacroScore forms
        FormReference formRef = new FormReference("MacroScore", Form.POWER);
	
		LimitForm nodeForm = FormFactory.makeLimitForm("Knot", noteForm, formRef);		
		registerBuiltin(nodeForm);
		PowerForm macroScoreForm = FormFactory.makePowerForm("MacroScore", nodeForm);			
		registerBuiltin(macroScoreForm);

		macroScoreForm.resolveReferences(this);
		
		// registration of the SoundNote form
		SimpleForm layerForm = FormFactory.makeZModuleForm("Layer");
		registerBuiltin(layerForm);
		
		formRef = new FormReference("Modulators", Form.POWER);
		
		List<Form> soundNoteFormList = new LinkedList<Form>();
        List<String> soundNoteFormLabelList = new LinkedList<String>();
        soundNoteFormList.add(onsetForm);
        soundNoteFormLabelList.add("onset");
        soundNoteFormList.add(pitchForm);
        soundNoteFormLabelList.add("pitch");
        soundNoteFormList.add(loudnessForm);
        soundNoteFormLabelList.add("loudness");
        soundNoteFormList.add(durationForm);
        soundNoteFormLabelList.add("duration");
        soundNoteFormList.add(voiceForm);
        soundNoteFormLabelList.add("voice");
        //Layer now is a coordinate of a note!!!!
        soundNoteFormList.add(layerForm);
        soundNoteFormLabelList.add("layer");
        soundNoteFormList.add(formRef);
        soundNoteFormLabelList.add("modulators");
        LimitForm soundNoteForm = FormFactory.makeLimitForm("SoundNote", soundNoteFormList);
        soundNoteForm.setLabels(soundNoteFormLabelList);
        registerBuiltin(soundNoteForm);
        
        PowerForm modulatorsForm = FormFactory.makePowerForm("Modulators", soundNoteForm);			
		registerBuiltin(modulatorsForm);
		
		modulatorsForm.resolveReferences(this);
		
		// registration of the SoundScore form
		formRef = new FormReference("SoundScore", Form.POWER);
	
		LimitForm soundNodeForm = FormFactory.makeLimitForm("SoundNode", soundNoteForm, formRef);
		registerBuiltin(soundNodeForm);
		PowerForm soundScoreForm = FormFactory.makePowerForm("SoundScore", soundNodeForm);			
		registerBuiltin(soundScoreForm);

		soundScoreForm.resolveReferences(this);
        
        // register modules
        registerBuiltinModule("Integers", ZRing.ring);
        registerBuiltinModule("Reals", RRing.ring);
        registerBuiltinModule("Rationals", QRing.ring);
        registerBuiltinModule("Complexes", CRing.ring);

        registerBuiltinModule("Integers modulo 2", ZnRing.make(2));
        registerBuiltinModule("Integers modulo 12", ZnRing.make(12));
        
        registerBuiltinModule("Pairs of integers", ZProperFreeModule.make(2));
        registerBuiltinModule("Pairs of rationals", QProperFreeModule.make(2));
        registerBuiltinModule("Pairs of reals", RProperFreeModule.make(2));
        registerBuiltinModule("Pairs of complexes", CProperFreeModule.make(2));

        registerBuiltinModule("Triples of integers", ZProperFreeModule.make(3));
        registerBuiltinModule("Triples of rationals", QProperFreeModule.make(3));
        registerBuiltinModule("Triples of reals", RProperFreeModule.make(3));
        registerBuiltinModule("Triples of complexes", CProperFreeModule.make(3));
        
        // Scheme environment and code
        env = Env.makeGlobalEnvironment();
        code = "";
   }


    private void registerNameForm(NameForm form) {
        NameEntry name = form.getName().getNameEntry();
        forms.put(name, new FormItem(form, true));
        builtinForms.put(form, form);
    }


    // Dictionary for forms
    private Map<NameEntry,FormItem> forms;
    
    // Dictionary for denotators
    private Map<NameEntry,DenotatorItem> denotators;
    
    // Dictionary for modules
    private Map<String,ModuleItem> modules;

    // Dictionary for module elements
    private Map<String,ModuleElementItem> moduleElements;

    // Dictionary for module morphisms
    private Map<String,ModuleMorphismItem> moduleMorphisms;

    // The current namespace for lookup
    private NameEntry namespace;
    
    private IdentityHashMap<Form,Form> builtinForms;
    private IdentityHashMap<Denotator,Denotator> builtinDenotators;
        
    // caching tables for autogenerated forms
    private HashMap<Form,PowerForm>              autogenPowerForms;
    private HashMap<Form,ListForm>               autogenListForms;
    private HashMap<ArrayList<Form>,LimitForm>   autogenLimitForms;
    private HashMap<ArrayList<Form>,ColimitForm> autogenColimitForms;
    private HashMap<Module,SimpleForm>           autogenSimpleForms;
    
    // Containers for temporary forms and denotators
    private LinkedList<Form> tmpForms = new LinkedList<Form>();
    private LinkedList<Denotator> tmpDenos = new LinkedList<Denotator>();
    
    // Scheme environment and code
    private Env env;
    private String code;
    
    // The unique global repository
    private static Repository globalRepository = null;
}
