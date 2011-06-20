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

package org.rubato.xml;

import static org.rubato.util.TextUtils.replaceStrings;
import static org.rubato.xml.XMLConstants.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.base.Rubette;
import org.rubato.composer.network.NetworkModel;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.builtin.MacroRubette;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * A reader for the XML-based Rubato file format.
 * 
 * @author Gérard Milmeister
 */
public final class XMLReader implements RubatoDictionary {

    /**
     * Creates an XMLReader from the given <code>file</code>.
     */
    public XMLReader(File file)
            throws IOException {
        this(new FileReader(file));
        this.file = file;
        try {
            // try to find out if the file is compressed
            InputStream in = new GZIPInputStream(new FileInputStream(file));
            reader = new InputStreamReader(in, "UTF-8");
        } catch (IOException e) {
            // not compressed
        }
    }
    
    
    /**
     * Creates an XMLReader using the global repository.
     */
    public XMLReader(Reader reader) {
        this(reader, Repository.systemRepository());
    }
    

    /**
     * Creates an XMLReader using the specified repository.
     */
    public XMLReader(Reader reader, Repository repository) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
           builder = factory.newDocumentBuilder();
           builder.setErrorHandler(errorHandler);
        }
        catch (ParserConfigurationException e) {
           throw new RuntimeException(e.getMessage());
        }
        dispatcher = Dispatcher.getDispatcher();
        this.repository = repository;
        this.reader = reader;
    }
    

    /**
     * Parses an XML file from the specified reader.
     */
    private void parse(Reader r) {
        reset();
        try {
            Document document = builder.parse(new InputSource(r));
            parseStart(document.getDocumentElement());
            resolveReferences();
        }
        catch (SAXException e) {
            setError(e);
        }
        catch (Exception e) {
            setError(e);
        }
    }
    
    
    /**
     * Parses an XML file.
     */
    public void parse() {
        parse(reader);
    }
    
    
    /**
     * Parses an XML file with the specified file name.
     * @throws FileNotFoundException
     */
    public void parseFile(String fileName)
            throws FileNotFoundException {
        parse(new BufferedReader(new FileReader(fileName)));        
    }
    

    /**
     * Parses an XML file contained in the specified string.
     */
    public void parseString(String string) {
        parse(new StringReader(string));
    }

    
    /**
     * Returns true if parsing produced any error.
     */
    public boolean hasError() {
        return error;
    }
    

    /**
     * Returns the list of errors occurred during parsing.
     * @return a list of error strings
     */
    public List<String> getErrors() {
        return errors;
    }
    
    
    /**
     * Appends the specified error string to the list of errors.
     */
    public void setError(String string) {
        if (errors == null) {
            errors = new LinkedList<String>();
        }
        errors.add(string);
        error = true;
    }
    
    
    /**
     * Appends the specified error string to the list of errors.
     */
    public void setError(String string, Object ... objects) {
        setError(replaceStrings(string, objects));
    }

    
    /**
     * Appends the error contained in the given exception to the list of errors.
     */
    public void setError(Exception e) {
        setError(e.getMessage());
    }
    
    
    /**
     * Resets the XML reader.
     * The XML reader is ready to be used again for a new XML file.
     */
    private void reset() {
        modules         = new HashMap<String,Module>();
        elements        = new HashMap<String,ModuleElement>();
        moduleMorphisms = new HashMap<String,ModuleMorphism>();
        forms           = new HashMap<String,Form>();
        denotators      = new HashMap<String,Denotator>();
        networks        = new LinkedList<NetworkModel>();
        rubettes        = new LinkedList<Rubette>();
        schemeCode      = ""; //$NON-NLS-1$

        formsToBeResolved = new LinkedList<Form>();
        denosToBeResolved = new LinkedList<Denotator>();
        errors            = new LinkedList<String>();
        error             = false;
    }
    
    
    /**
     * Start parsing at the root <code>element</code>.
     */
    private void parseStart(Element element) {
        if (element.getNodeName().equals(ROOT_ELEMENT)) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                parseRoot(childNodes.item(i));
            }
        }
        else {
            setError("Document root element is not %%1", ROOT_ELEMENT);
        }       
    }
    

    /**
     * Parse a toplevel <code>node</node>, i.e., an element
     * directly below the root element.
     */
    private void parseRoot(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            if (name.equals(DEFINE_MODULE)) {
                parseModuleDefinition((Element)node);
            }
            else if (name.equals(DEFINE_MODULEELEMENT)) {
                parseElementDefinition((Element)node);
            }
            else if (name.equals(DEFINE_MODULEMORPHISM)) {
                parseModuleMorphismDefinition((Element)node);                
            }
            else if (name.equals(DENOTATOR)) {
                parseDenotatorDefinition((Element)node);                
            }
            else if (name.equals(FORM)) {
                parseFormDefinition((Element)node);                
            }
            else if (name.equals(NETWORK)) {
                parseNetworkDefinition((Element)node);                
            }
            else if (name.equals(RUBETTE)) {
                parseRubetteDefinition((Element)node);                
            }
            else if (name.equals(SCHEME)) {
                parseSchemeCode((Element)node);                
            }
            else {
                setError("Toplevel element <%1> not recognized", name);
            }
        }
    }
    
    
    /**
     * Parses toplevel module definition.
     * The parsed module is put into the <i>modules</i> hashtable.
     */
    private void parseModuleDefinition(Element moduleDefinitionNode) {
        if (!moduleDefinitionNode.hasAttribute(NAME_ATTR)) {
            // definition must have a name
            setError("<%1> is missing attribute %%2", DEFINE_MODULE, NAME_ATTR);
            return;
        }

        String name = moduleDefinitionNode.getAttribute(NAME_ATTR);
        Element moduleNode = getChild(moduleDefinitionNode, MODULE);
        if (moduleNode != null) {
            Module module = parseModule(moduleNode);
            if (module != null) {
                modules.put(name, module);
            }
        }
        else {
            // module itself is missing                
            setError("<%1> does not contain <%2>", DEFINE_MODULE, MODULE);
        }
    }
    
    
    /**
     * Parses the module starting from the given XML element.
     * @return the parsed module or null if parsing failed
     */
    public Module parseModule(Element moduleNode) {
        // case 1: a reference is given, try to resolve it
        if (moduleNode.hasAttribute(REF_ATTR)) {
            Module module = modules.get(moduleNode.getAttribute(REF_ATTR));
            if (module == null) {
                // could not resolve the reference
                setError("Module reference %%1 is not defined", moduleNode.getAttribute(REF_ATTR));
            }
            return module;
        }
    
        // case 2: a type is given, use the dispatcher
        if (moduleNode.hasAttribute(TYPE_ATTR)){
            return dispatcher.resolveModule(this, moduleNode);
        }

        // case 3: a class is given, use reflection
        if (moduleNode.hasAttribute(CLASS_ATTR)) {
            String moduleClass = moduleNode.getAttribute(CLASS_ATTR);
            try {
                Class<?> cls = Class.forName(moduleClass);
                Method method = cls.getMethod("fromXML", new Class[] { XMLReader.class, Element.class }); //$NON-NLS-1$
                return (Module)method.invoke(cls, new Object[] { this, moduleNode });
            }
            catch (ClassNotFoundException e) {
                setError("Module class %%1 not found", moduleClass);                        
            }
            catch (NoSuchMethodException e) {
                setError("Cannot build module from class %%1", moduleClass);
            }
            catch (Exception e) {
                setError(e);
            }
            return null;
        }
        
        // not enough information: parsing fails
        setError("<%1> is missing one of attributes %%2, %%3, %%4",
                 MODULE, REF_ATTR, TYPE_ATTR, CLASS_ATTR);
        return null;
    }

    
    /**
     * Parses toplevel module element definition.
     * The parsed element is put into the <i>elements</i> hashtable.
     */
    private void parseElementDefinition(Element elementDefinitionNode) {
        if (!elementDefinitionNode.hasAttribute(NAME_ATTR)) {
            // definition must have a name
            setError("<%1> is missing attribute %%2", DEFINE_MODULEELEMENT, NAME_ATTR);
            return;
        }

        String name = elementDefinitionNode.getAttribute(NAME_ATTR);
        Element elementNode = getChild(elementDefinitionNode, MODULEELEMENT);
        if (elementNode != null) {
            ModuleElement moduleElement = parseModuleElement(elementNode);
            if (moduleElement != null) {
                elements.put(name, moduleElement);
            }
        }
        else {
            // module element itself is missing                
            setError("<%1> does not contain <%2>", DEFINE_MODULEELEMENT, MODULEELEMENT);
        }
    }       


    /**
     * Parses the module element starting from the given XML element.
     * @return the parsed module element or null if parsing failed
     */
    public ModuleElement parseModuleElement(Element elementNode) {
        // case 1: a reference is given, try to resolve it
        if (elementNode.hasAttribute(REF_ATTR)) {
            ModuleElement moduleElement = elements.get(elementNode.getAttribute(REF_ATTR));
            if (moduleElement == null) {
                // could not resolve the reference
                setError("ModuleElement reference %%1 is not defined", elementNode.getAttribute(REF_ATTR));
            }
            return moduleElement;
        }
        
        // case 2: a type is given, use the dispatcher
        if (elementNode.hasAttribute(TYPE_ATTR)) {
            return dispatcher.resolveElement(this, elementNode);
        }

        // case 3: a class is given, use reflection
        if (elementNode.hasAttribute(CLASS_ATTR)) {
            String elementClass = elementNode.getAttribute(CLASS_ATTR);
            try {
                Class<?> cls = Class.forName(elementClass);
                Method method = cls.getMethod("fromXML", new Class[] { XMLReader.class, Element.class });
                return (ModuleElement)method.invoke(cls, new Object[] { this, elementNode });
            }
            catch (ClassNotFoundException e) {
                setError("ModuleElement class %%1 not found", elementClass);                        
            }
            catch (NoSuchMethodException e) {
                setError("Cannot build module element from class %%1", elementClass);
            }
            catch (Exception e) {
                setError(e);
            }
            return null;
        }
        
        // not enough information: parsing fails
        setError("<%1> is missing one of attributes %%2, %%3, %%4",
                MODULEELEMENT, REF_ATTR, TYPE_ATTR, CLASS_ATTR);
        return null;
    }
    
    
    /**
     * Parses toplevel module morphism definition.
     * The parsed element is put into the <i>elements</i> hashtable.
     */
    private void parseModuleMorphismDefinition(Element morphismDefinitionNode) {
        if (!morphismDefinitionNode.hasAttribute(NAME_ATTR)) {
            // definition must have a name
            setError("<%1> is missing attribute %%2", DEFINE_MODULEMORPHISM, NAME_ATTR);
            return;
        }

        String name = morphismDefinitionNode.getAttribute(NAME_ATTR);
        Element moduleMorphismNode = getChild(morphismDefinitionNode, MODULEMORPHISM);
        if (moduleMorphismNode != null) {
            ModuleMorphism moduleMorphism = parseModuleMorphism(moduleMorphismNode);
            if (moduleMorphism != null) {
                moduleMorphisms.put(name, moduleMorphism);
            }
        }
        else {
            // module morphism itself is missing                
            setError("<%1> does not contain <%2>", DEFINE_MODULEMORPHISM, MODULEMORPHISM);
        }
    }
    
    
    /**
     * Parses the module morphism starting from the given XML element.
     * @return the parsed module morphism or null if parsing failed
     */
    public ModuleMorphism parseModuleMorphism(Element morphismNode) {
        // case 1: a reference is given, try to resolve it
        if (morphismNode.hasAttribute(REF_ATTR)) {
            ModuleMorphism morphism = moduleMorphisms.get(morphismNode.getAttribute(REF_ATTR));
            if (morphism == null) {
                setError("ModuleMorphism reference %%1 is not defined", morphismNode.getAttribute(REF_ATTR));
            }
            return morphism;
        }
        
        // case 2: a type is given, use the dispatcher
        if (morphismNode.hasAttribute(TYPE_ATTR)) {
            return dispatcher.resolveModuleMorphism(this, morphismNode);
        }

        // case 3: a class is given, use reflection
        if (morphismNode.hasAttribute(CLASS_ATTR)) {
            String morphismClass = morphismNode.getAttribute(CLASS_ATTR);
            try {
                Class<?> c = Class.forName(morphismClass);
                Method m = c.getMethod("fromXML", new Class[] { XMLReader.class, Element.class }); //$NON-NLS-1$
                return (ModuleMorphism)m.invoke(c, new Object[] { this, morphismNode });
            }
            catch (ClassNotFoundException e) {
                setError("ModuleMorphism class %%1 not found", morphismClass);                        
            }
            catch (NoSuchMethodException e) {
                setError("Cannot build module morphism from class %%1", morphismClass);
            }
            catch (Exception e) {
                setError(e);
            }
            return null;
        }
        
        // not enough information: parsing fails
        setError("<%1> is missing one of attributes %%2, %%3, %%4",
                 MODULEMORPHISM, REF_ATTR, TYPE_ATTR, CLASS_ATTR);
        return null;
    }

    
    /**
     * Parses the morphism map starting from the given XML element.
     * @return the parsed morphism map or null if parsing failed
     */
    public MorphismMap parseMorphismMap(Element morphismMapNode) {
        // case 1: a reference is given, try to resolve it
        if (morphismMapNode.hasAttribute(TYPE_ATTR)) {
            return dispatcher.resolveMorphismMap(this, morphismMapNode);
        }

        // case 2: a class is given, use reflection
        if (morphismMapNode.hasAttribute(CLASS_ATTR)) {
            String morphismMapClass = morphismMapNode.getAttribute(CLASS_ATTR);
            try {
                Class<?> cls = Class.forName(morphismMapClass);
                Method method = cls.getMethod("fromXML", new Class[] { XMLReader.class, Element.class });
                return (MorphismMap)method.invoke(cls, new Object[] { this, morphismMapNode });
            }
            catch (ClassNotFoundException e) {
                setError("MorphismMap class %%1 not found", morphismMapClass);                        
            }
            catch (NoSuchMethodException e) {
                setError("Cannot build morphism map from class %%1", morphismMapClass);
            }
            catch (Exception e) {
                setError(e);
            }
            return null;
        }
        
        // not enough information: parsing fails
        setError("<%1> is missing one of attributes %%2. %%3",
                 MORPHISMMAP, TYPE_ATTR, CLASS_ATTR);
        return null;
    }

    
    /**
     * Parses toplevel denotator definition.    public boolean hasRubetteByName(String name) {
        return nameMap.get(name) != null;        
    }
    
    

     * If the parsed denotator has a name, it is put
     * into the <i>denotators</i> hashtable,
     */
    private void parseDenotatorDefinition(Element denotatorNode) {
        Denotator denotator = parseDenotator(denotatorNode);
        if (denotator != null && !(denotator instanceof DenotatorReference)) {
            String name = denotator.getNameString();
            if (name.length() != 0) {
                denotators.put(name, denotator);
            }
        }
    }
    

    /**
     * Parses the denotator starting from the given XML element.
     * @return the parsed denotator or null if parsing failed
     */
    public Denotator parseDenotator(Element denotatorNode) {
        // make sure that all form references are resolved
        // before parsing any denotator
        if (!formsToBeResolved.isEmpty()) {
            if (!resolveFormReferences()) {
                // error has already been set
                return null;
            }
        }
        // case 1: a reference is given, try to resolve it
        if (denotatorNode.hasAttribute(REF_ATTR)) {
            String name = denotatorNode.getAttribute(REF_ATTR);
            Denotator denotator = denotators.get(name);
            // if it could not be resolved, create a reference 
            if (denotator == null) {
                denotator = new DenotatorReference(name);
            }
            return denotator;
        }

        if (!denotatorNode.hasAttribute(TYPE_ATTR)) {
            setError("<%1> is missing one of attributes %%2, %%3",
                     DENOTATOR, REF_ATTR, TYPE_ATTR);
            return null;
        }
              
        // case 2: a type is given
        String type = denotatorNode.getAttribute(TYPE_ATTR);
        Denotator denotator;
        // dispatch according to type 
        if (type.equals("simple")) { //$NON-NLS-1$
            denotator = SimpleDenotator.fromXML(this, denotatorNode);
        }
        else if (type.equals("limit")) { //$NON-NLS-1$
            denotator = LimitDenotator.fromXML(this, denotatorNode);
        }
        else if (type.equals("colimit")) { //$NON-NLS-1$
            denotator = ColimitDenotator.fromXML(this, denotatorNode);
        }
        else if (type.equals("list")) { //$NON-NLS-1$
            denotator = ListDenotator.fromXML(this, denotatorNode);
        }
        else if (type.equals("power")) { //$NON-NLS-1$
            denotator = PowerDenotator.fromXML(this, denotatorNode);                
        }
        else {
            // not a known type
            setError("Attribute %%1 of element <%2> has invalid value %%3",
                     TYPE_ATTR, DENOTATOR, type);
            return null;
        }
        
        return denotator;
    }


    /**
     * Parses toplevel denotator definition.
     * Forms are put into the <code>forms</code> hashtable
     * immediately as they are parsed.
     */
    private void parseFormDefinition(Element formNode) {
        parseForm(formNode);
    }
    

    /**
     * Parses the form starting from the given XML element.
     * Forms are put into the <code>forms</code> hashtable
     * immediately as they are parsed.
     * In an XML file, only one form definition per name
     * must occur.
     * @return the parsed form or null if parsing failed
     */
    public Form parseForm(Element formNode) {
        // case 1: a reference is given, try to resolve it
        if (formNode.hasAttribute(REF_ATTR)) {
            String name = formNode.getAttribute(REF_ATTR);
            Form form = forms.get(name);
            if (form == null) {
                // if it could not be resolved, create a reference 
                form = new FormReference(name);
            }
            return form;
        }

        if (!formNode.hasAttribute(TYPE_ATTR)) {
            setError("<%1> is missing one of attributes %%2, %%3",
                     FORM, REF_ATTR, TYPE_ATTR);
            return null;
        }
              
        // case 2: a type is given
        String type = formNode.getAttribute(TYPE_ATTR);
        Form form;
        // dispatch according to type 
        if (type.equals("simple")) { //$NON-NLS-1$
            form = SimpleForm.fromXML(this, formNode);
        }
        else if (type.equals("limit")) { //$NON-NLS-1$
            form = LimitForm.fromXML(this, formNode);
        }
        else if (type.equals("colimit")) { //$NON-NLS-1$
            form = ColimitForm.fromXML(this, formNode);
        }
        else if (type.equals("list")) { //$NON-NLS-1$
            form = ListForm.fromXML(this, formNode);
        }
        else if (type.equals("power")) { //$NON-NLS-1$
            form = PowerForm.fromXML(this, formNode);                
        }
        else {
            setError("Attribute %%1 of element <%2> has invalid value %%3",
                     TYPE_ATTR, FORM, type);
            return null;
        }
        
        // put the parsed form into the <code>forms</code> hashtable
        if (form != null) {
            String name = form.getNameString();
            if (name.length() == 0) {
                setError("Form must have a name");
                return null;
            }
            else if (forms.get(name) != null) {
                setError("Form with name %%1 is already defined", name);
                return null;
            }
            else {
                forms.put(name, form);
            }
        }
        
        return form;
    }
    
    
    /**
     * Parses the form starting from the given XML element, then,
     * if the form is a reference, resolve the reference. 
     * @return the parsed form or null if parsing failed
     */
    public Form parseAndResolveForm(Element formNode) {
        Form f = parseForm(formNode);
        if (f instanceof FormReference) {
            String s = ((FormReference)f).getNameString();
            f = getForm(s);
        }
        return f;
    }
    
    
    /**
     * Parses toplevel network definition.
     * The parsed network is put into the <i>networks</i> list.
     */
    private void parseNetworkDefinition(Element networkNode) {
        NetworkModel model = NetworkModel.fromXML(this, networkNode);
        if (model != null) {
            networks.add(model);
        }
    }
    

    /**
     * Parses toplevel (network) rubette definition.
     * The parsed rubette is put into the <i>rubette</i> list.
     */
    private void parseRubetteDefinition(Element rubetteNode) {
        String name = rubetteNode.getAttribute("name"); //$NON-NLS-1$
        if (name.length() == 0) {
            setError("Rubette must have a name");
        }
        else {
            MacroRubette rubette = new MacroRubette();
            MacroRubette nrubette = (MacroRubette)rubette.fromXML(this, rubetteNode);
            nrubette.setName(name);
            rubettes.add(nrubette);
        }
    }
    

    /**
     * Parses toplevel Scheme code.
     * The scheme code is appended to the <i>schemeCode</i> string.
     */
    private void parseSchemeCode(Element schemeNode) {
        schemeCode += getText(schemeNode).trim();
    }
    

    /**
     * Returns the first child of the specified element that has the given name.
     * @return the first child with the given name or null
     *         if there is no such child.
     */
    public static Element getChild(Element element, String name) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(name)) {
                return (Element)childNode;
            }
        }
        return null;
    }
    

    /**
     * Returns the next (sibling) element after the specified element
     * that has the given name.
     * @return the next element with the given name or null if there is no such
     *         element
     */
    public static Element getNextSibling(Element element, String name) {
        if (element == null) return null;
        Node next = element.getNextSibling();
        while (next != null) {
            if (next.getNodeType() == Node.ELEMENT_NODE && next.getNodeName().equals(name)) {
                return (Element)next;
            }
            next = next.getNextSibling();
        }
        return null;
    }

    
    /**
     * Returns the text content of <code>element</code>.
     */
    public static String getText(Element element) {
        return element.getTextContent();
    }
    
    
    /**
     * Returns the integer value of the given attribute in the specified element.
     * @param element the element containing the attribute
     * @param attr the attribute whose value is to be returned
     * @param def the default value to return if the attribute has wrong format
     */
    public static int getIntAttribute(Element element, String attr, int def) {
       String value = element.getAttribute(attr);
       int res;
       try {
           res = Integer.parseInt(value);
       }
       catch (NumberFormatException e) {
           res = def;
       }
       return res;
    }
    
    
    /**
     * Returns the integer value of the given attribute in the specified element.
     * @param element the element containing the attribute
     * @param attr the attribute whose value is to be returned
     * @param min the minimum value allowed
     * @param max the maximum value allowed
     * @param def the default value to return if the attribute has wrong format
     */
    public static int getIntAttribute(Element element, String attr, int min, int max, int def) {
        String value = element.getAttribute(attr);
        int res;
        try {
            res = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            res = def;
        }
        if (res < min) res = min;
        if (res > max) res = max;
        return res;
     }
     
     
    /**
     * Returns the string value of the given attribute in the specified element.
     * Some escaped characters are replaced by their real values.
     * @param element the element containing the attribute
     * @param attr the attribute whose value is to be returned
     */
    public static String getStringAttribute(Element element, String attr) {
        String value = element.getAttribute(attr);
        return value.replaceAll("&lt;", "<").replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    
    /**
     * Returns the double value of the given attribute in the specified element.
     * @param element the element containing the attribute
     * @param attr the attribute whose value is to be returned
     * @param def the default value to return if the attribute has wrong format
     */
    public static double getRealAttribute(Element element, String attr, double def) {
        String value = element.getAttribute(attr);
        double res;
        try {
            res = Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            res = def;
        }
        return res;
    }
     
    
    /**
     * Displays the modules that have been parsed.
     */
    public void printModules() {
        for (Map.Entry<String,Module> entry : modules.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue()); //$NON-NLS-1$
        }
    }
    
    
    /**
     * Displays the module elements that have been parsed.
     */
    public void printElements() {
        for (Map.Entry<String,ModuleElement> entry : elements.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue()); //$NON-NLS-1$
        }
    }
    
    
    /**
     * Displays the forms that have been parsed.
     */
    public void printForms() {
        for (Map.Entry<String,Form> entry : forms.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue()); //$NON-NLS-1$
        }
    }
    
    
    /**
     * Displays the denotators that have been parsed.
     */
    public void printDenotators() {
        for (Map.Entry<String,Denotator> entry : denotators.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue()); //$NON-NLS-1$
        }
    }

    
    /**
     * Returns the parsed module elements as a map from names to elements. 
     */
    public Map<String,ModuleElement> getModuleElements() {
        return elements;
    }
    

    /**
     * Returns the names of all parsed module elements.
     */
    public Set<String> getModuleElementNames() {
        return elements.keySet();
    }
    
    
    /**
     * Returns the module element for the given name.
     * @return null if no such element exists
     */
    public ModuleElement getModuleElement(String name) {
        return elements.get(name);
    }
    

    /**
     * Returns the parsed modules as a map from names to modules. 
     */
    public Map<String,Module> getModules() {
        return modules;
    }
    
    
    /**
     * Returns the names of all parsed modules.
     */
    public Set<String> getModuleNames() {
        return modules.keySet();
    }
    
    
    /**
     * Returns the module for the given name.
     * First try to resolve using the parsed modules then using the
     * global repository.
     * @return null if no such module exists
     */
    public Module getModule(String name) {
        Module module = modules.get(name);
        if (module == null) {
            module = repository.getModule(name);
        }
        return module;
    }
    

    /**
     * Returns the parsed module morphisms as a map from names to morphisms. 
     */
    public Map<String,ModuleMorphism> getModuleMorphisms() {
        return moduleMorphisms;
    }
    
    
    /**
     * Returns the names of all parsed module morphisms.
     */
    public Set<String> getModuleMorphismNames() {
        return moduleMorphisms.keySet();
    }
    
    
    /**
     * Returns the module morphism for the given name.
     * First try to resolve using the parsed morphisms then using the
     * global repository.
     * @return null if no such morphism exists
     */
    public ModuleMorphism getModuleMorphism(String name) {
        ModuleMorphism morphism = moduleMorphisms.get(name);
        if (morphism == null) {
            morphism = repository.getModuleMorphism(name);
        }
        return morphism;
    }
    

    /**
     * Returns a list of all denotators parsed from XML.
     */
    public List<Denotator> getDenotators() {
        return new LinkedList<Denotator>(denotators.values());
    }

    
    /**
     * Returns the denotator with the given name.
     * First try to resolve using the parsed denotators then using the
     * global repository.
     * @return null if no such denotator exists
     */
    public Denotator getDenotator(String name) {
        Denotator denotator = denotators.get(name);
        if (denotator == null) {
            denotator = repository.getDenotator(name);
        }
        return denotator;
    }
    

    /**
     * Returns a list of all forms parsed from XML.
     */
    public List<Form> getForms() {
        return new LinkedList<Form>(forms.values());
    }
    
    
    /**
     * Returns the form with the given name.
     * First try to resolve using the parsed forms then using the
     * global repository.
     * @return null if no such form exists
     */
    public Form getForm(String name) {
        Form form = forms.get(name);
        if (form == null) {
            form = repository.getForm(name);
        }
        return form;
    }
    

    /**
     * Returns the parsed networks as a list.
     */
    public List<NetworkModel> getNetworks() {
        return networks;
    }
    
    
    /**
     * Returns the parsed rubettes as a list.
     */
    public List<Rubette> getRubettes() {
        return rubettes;
    }
    
    
    /**
     * Returns the Scheme code.
     */
    public String getSchemeCode() {
        return schemeCode;
    }
    
    
    /**
     * Adds the given form to the list of forms
     * that have to be resolved later.
     */
    public void addFormToBeResolved(Form f) {
        formsToBeResolved.add(f);
    }
    
    
    /**
     * Adds the given denotator to the list of denotators
     * that have to be resolved later.
     */
    public void addDenoToBeResolved(Denotator d) {
        denosToBeResolved.add(d);
    }
    
    
    /**
     * Returns an absolute path based on the specified (relative) path.
     */
    public String toAbsolutePath(String path) {
        String res = path;
        if (path.length() > 0) {
            if (path.charAt(0) != '/') {
                if (file != null) {
                    res = file.getParent()+"/"+path; //$NON-NLS-1$
                }
            }
        }
        return res;
    }
    
    
    private boolean resolveReferences() {
        return resolveFormReferences() &&
               resolveDenoReferences();
    }

    
    private boolean resolveFormReferences() {
        boolean success = true;
        for (Form form : formsToBeResolved) {
            if (!form.resolveReferences(this)) {
                setError("Could not resolve references in form %1", form);
                success = false;
            }
        }
        formsToBeResolved = new LinkedList<Form>();
        return success;
    }

    
    private boolean resolveDenoReferences() {
        boolean success = true;
        for (Denotator deno : denosToBeResolved) {
            if (!deno.resolveReferences(this)) {
                setError("Could not resolve references in denotator %1", deno);
                success = false;
            }
        }
        denosToBeResolved = new LinkedList<Denotator>();
        return success;
    }
    
    
    private static final ErrorHandler errorHandler = new ErrorHandler() {
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };
    
    
    private HashMap<String,Denotator>      denotators;
    private HashMap<String,Form>           forms;
    private HashMap<String,Module>         modules;
    private HashMap<String,ModuleMorphism> moduleMorphisms;
    private HashMap<String,ModuleElement>  elements;
    private LinkedList<NetworkModel>       networks;
    private LinkedList<Rubette>            rubettes;
    private String                         schemeCode;

    private boolean            error = false;
    private LinkedList<String> errors;

    private DocumentBuilder       builder;
    private Dispatcher            dispatcher;
    private LinkedList<Form>      formsToBeResolved;
    private LinkedList<Denotator> denosToBeResolved;
    private Repository            repository;

    private Reader reader = null;
    private File   file   = null;
}
