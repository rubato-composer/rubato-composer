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

import static org.rubato.xml.XMLConstants.DEFINE_MODULE;
import static org.rubato.xml.XMLConstants.DEFINE_MODULEELEMENT;
import static org.rubato.xml.XMLConstants.DEFINE_MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.DENOTATOR;
import static org.rubato.xml.XMLConstants.DQUOTE;
import static org.rubato.xml.XMLConstants.EQUALS;
import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.NAME_ATTR;
import static org.rubato.xml.XMLConstants.REF_ATTR;
import static org.rubato.xml.XMLConstants.SPACE;
import static org.rubato.xml.XMLConstants.TAG_CLOSE;
import static org.rubato.xml.XMLConstants.TAG_OPEN;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.rubato.base.Repository;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;


/**
 * A writer for the XML-based Rubato file format.
 * 
 * @author Gérard Milmeister
 */
public class XMLWriter {

    /**
     * Creates a non-compressing writer to the specified file.
     */
    public XMLWriter(File file) throws IOException {
        this(file, false);
    }

    
    /**
     * Creates a writer to the specified file.
     * 
     * @param file the file to save to
     * @param compressed if <code>true</code> the output is compressed with GZIP
     * @throws IOException iff the file cannot be opened
     *         for writing
     */
    public XMLWriter(File file, boolean compressed)
            throws IOException {
        OutputStream fileOut;
        if (compressed)
            fileOut = new GZIPOutputStream(new FileOutputStream(file));
        else 
            fileOut = new FileOutputStream(file);
        try {
            this.out = new PrintStream(fileOut, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.file = file;
        this.indent = 0;
        denoOccurrences = new HashSet<String>();
        elementStack = new LinkedList<String>();
    }
    
    
    /**
     * Creates a writer to the specified PrintStream with the
     * given initial indent.
     */
    public XMLWriter(PrintStream out, int indent) {
        this.out = out;
        this.indent = indent;
        denoOccurrences = new HashSet<String>();
        elementStack = new LinkedList<String>();
    }
    
    
    /**
     * Creates a writer to the specified PrintStream with the
     * initial indent 0.
     */
    public XMLWriter(PrintStream out) {
        this(out, 0);
    }
    
    
    /**
     * Starts the XML file. This must be called before anything
     * else is done.
     */
    public void open() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
        out.println("<Rubato>"); //$NON-NLS-1$
        indent++;
    }
    
    
    /**
     * Ends the XML file. This must be called at the end.
     */
    public void close() {
        indent--;
        out.println("</Rubato>"); //$NON-NLS-1$
        out.close();
    }
    

    /**
     * Sets the number of spaces for each indent level.
     */
    public void setIndentSize(int size) {
        this.indentSize = size;
    }
    

    private void openBlockWithAttributes(String element, String attributes) {
        printIndent();
        out.print(TAG_OPEN+element);
        if (attributes != null) {
            out.print(SPACE);
            out.print(attributes.trim().replaceAll("&", "&amp;").replaceAll("<", "&lt;")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        out.println(TAG_CLOSE);
        indent++;
        elementStack.add(0, element);
    }
    
    
    /**
     * Opens an XML block with the given tag name.
     */    
    public void openBlock(String element) {
        printIndent();
        out.println(TAG_OPEN+element+TAG_CLOSE);
        indent++;
        elementStack.add(0, element);
    }
    
    
    /**
     * Opens an XML block with the given tag name and the specified
     * attributes which are given as alternating name/value pairs.
     */    
    public void openBlock(String element, Object ... attrs) {
        StringBuilder buf = new StringBuilder(attrs.length*15);
        int i = 0;
        while (i < attrs.length) {
            String attr = attrs[i].toString();
            String value = attrs[i+1].toString();
            buf.append(SPACE);
            buf.append(attr);
            buf.append(EQUALS);
            buf.append(DQUOTE);
            buf.append(value);
            buf.append(DQUOTE);
            i += 2;
        }
        openBlockWithAttributes(element, buf.toString());
    }
    
    
    /**
     * Opens an XML block with the given tag name and the specified
     * attributes which are given as alternating name/value pairs.
     * An additional attribute "type" is added with the specified value.
     */    
    public void openBlockWithType(String element, String type, Object ... attrs) {
        StringBuilder buf = new StringBuilder(attrs.length*15);
        buf.append("type"+EQUALS+DQUOTE); //$NON-NLS-1$
        buf.append(type);
        buf.append(DQUOTE);
        int i = 0;
        while (i < attrs.length) {
            String attr = attrs[i].toString();
            String value = attrs[i+1].toString();
            buf.append(SPACE);
            buf.append(attr);
            buf.append(EQUALS);
            buf.append(DQUOTE);
            buf.append(value);
            buf.append(DQUOTE);
            i += 2;
        }
        openBlockWithAttributes(element, buf.toString());
    }
    
    
    /**
     * Creates an inline XML element with the given tag. Inline
     * means, that there is no newline after the start tag and
     * before the end tag. 
     */
    public void openInline(String element) {
        printIndent();
        out.print(TAG_OPEN+element+TAG_CLOSE);
        elementStack.add(0, element);
    }

    
    /**
     * Creates an inline XML element with the given tag and
     * attributes.
     */
    public void openInline(String element, Object ... attrs) {
        printIndent();
        StringBuilder buf = new StringBuilder(attrs.length*5);
        int i = 0;
        while (i < attrs.length) {
            String attr = attrs[i].toString();
            String value = attrs[i+1].toString();
            buf.append(SPACE);
            buf.append(attr);
            buf.append(EQUALS);
            buf.append(DQUOTE);
            buf.append(value);
            buf.append(DQUOTE);
            i += 2;
        }
        String attributes = buf.toString().trim();
        out.print(TAG_OPEN+element);
        if (attributes.length() > 0) {
            out.print(SPACE);
            out.print(attributes.replaceAll("&", "&amp;").replaceAll("<", "&lt;")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
        }
        out.print(TAG_CLOSE);
        elementStack.add(0, element);
    }
    
    
    /**
     * Closes the XML block opened with openBlock.
     */
    public void closeBlock() {
        indent--;
        printIndent();
        String element = elementStack.removeFirst();
        out.println("</"+element+">"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    /**
     * Closes the XML element opened with openInline.
     */
    public void closeInline() {
        String element = elementStack.removeFirst();
        out.println("</"+element+">"); //$NON-NLS-1$ //$NON-NLS-2$
    }
        

    /**
     * Creates an empty XML element "<.../>" with the given tag name. 
     */
    public void empty(String element) {
        printIndent();
        out.println("<"+element+"/>"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    /**
     * Creates an empty XML element "<.../>" with the given tag name
     * and the given attributes. 
     */
    public void empty(String element, Object ... attrs) {
        printIndent();
        StringBuilder buf = new StringBuilder(attrs.length*15);
        int i = 0;
        while (i < attrs.length) {
            String attr = attrs[i].toString();
            String value = attrs[i+1].toString();
            buf.append(" "); //$NON-NLS-1$
            buf.append(attr);
            buf.append("="); //$NON-NLS-1$
            buf.append("\""); //$NON-NLS-1$
            buf.append(toXMLText(value));
            buf.append("\""); //$NON-NLS-1$
            i += 2;
        }
        String attributes = buf.toString().trim();
        out.print("<"+element); //$NON-NLS-1$
        if (attributes.length() > 0) {
            out.print(" "); //$NON-NLS-1$
            out.print(attributes);
        }
        out.println("/>"); //$NON-NLS-1$
    }
    
    
    /**
     * Creates an empty XML element "<.../>" with the given tag name
     * and the given attributes. An additional attribute "type" with
     * the given value is added. 
     */
    public void emptyWithType(String element, String type, Object ... attrs) {
        printIndent();
        StringBuilder buf = new StringBuilder(attrs.length*15);
        buf.append(TYPE_ATTR+EQUALS);
        buf.append(DQUOTE);
        buf.append(type);
        buf.append(DQUOTE);
        int i = 0;
        while (i < attrs.length) {
            String attr = attrs[i].toString();
            String value = attrs[i+1].toString();
            buf.append(SPACE);
            buf.append(attr);
            buf.append(EQUALS);
            buf.append(DQUOTE);
            buf.append(value);
            buf.append(DQUOTE);
            i += 2;
        }
        String attributes = buf.toString().trim();
        out.print("<"+element);
        if (attributes.length() > 0) {
            out.print(SPACE);
            out.print(attributes.replaceAll("&", "&amp;").replaceAll("<", "&lt;"));
        }
        out.println("/>");
    }

    
    /**
     * Adds the specified text to the XML file, with & and <
     * converted to XML entities.
     */
    public void writeTextNode(String text) {
        out.println(toXMLText(text));
    }
    

    /**
     * Converts the specified string for use with XML.
     */
    public String toXMLText(String text) {
        String res = text;
        res = res.replaceAll("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
        res = res.replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
        res = res.replaceAll("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
        return res;
    }
    
    
    /**
     * Adds the specified text to the XML file, with no conversion
     * done on special characters. This is only to be used, if it is
     * certain that the text does contain & or <. 
     */
    public void text(String string) {
        out.print(string);
    }
    
    
    /**
     * Writes the XML representation of the given module under the
     * given name.
     */
    public void writeModule(String name, Module module) {
        openBlock(DEFINE_MODULE, NAME_ATTR, name);
        module.toXML(this);
        closeBlock();
    }
    
    
    /**
     * Writes the XML representation of the given module element
     * under the given name.
     */
    public void writeModuleElement(String name, ModuleElement moduleElement) {
        openBlock(DEFINE_MODULEELEMENT, NAME_ATTR, name);
        moduleElement.toXML(this);
        closeBlock();
    }
    
    
    /**
     * Writes the XML representation of the given module morphism
     * under the given name.
     */
    public void writeModuleMorphism(String name, ModuleMorphism morphism) {
        openBlock(DEFINE_MODULEMORPHISM, NAME_ATTR, name);
        morphism.toXML(this);
        closeBlock();
    }
    
    
    /**
     * Writes the XML representation of the specified form.
     */
    public void writeForm(Form form) {
        if (!rep.isBuiltin(form)) {
            // Only write the form itself
            // Subforms are automatically written, if they have been
            // registered as non-builtin
            form.toXML(this);
//            Object[] forms = form.getDependencies().toArray();
//            for (int i = forms.length-1; i >= 0; i--) {
//                Form f = (Form)forms[i];
//                if (!formOccurrences.contains(f.getNameString())) {
//                    formOccurrences.add(f.getNameString());
//                    f.toXML(this);
//                }
//            }
        }
    }

    
    /**
     * Writes a reference to the specified form.
     */
    public void writeFormRef(Form form) {
        String name = form.getNameString();
        empty(FORM, REF_ATTR, name);
    }
    
    
    /**
     * Writes the XML representations of the specified list of forms.
     */
    public void writeForms(List<Form> forms) {
        for (Form f : forms) {
            f.toXML(this);
        }
    }

    
    /**
     * Writes the XML representation of the specified denotator.
     */
    public void writeDenotator(Denotator denotator) {
        if (!rep.isBuiltin(denotator)) {
            Object[] denotators = denotator.getDependencies().toArray();
            for (int i = denotators.length-1; i >= 0; i--) {
                Denotator d = (Denotator)denotators[i];            
                if (d.getName() != null && !denoOccurrences.contains(d.getNameString())) {
                    denoOccurrences.add(d.getNameString());
                    if (!rep.isBuiltin(denotator)) {
                        d.toXML(this);
                    }
                }
            }
        }
    }


    /**
     * Writes the XML representations of the specified list of denotators.
     */
    public void writeDenotators(List<Denotator> denotators) {
        for (Denotator d : denotators) {
            writeDenotator(d);
        }
    }
    
        
    /**
     * Writes a reference to the specified denotator.
     */
    public void writeDenotatorRef(Denotator d) {
        if (d.getName() != null) {
            String name = d.getNameString();
            empty(DENOTATOR, REF_ATTR, name);
        }
        else {
            d.toXML(this);
        }
    }    
    

    /**
     * Strips the directory name of the current file from <code>path</code>
     * if possible to make <code>path</code> relative to the current file.
     */
    public String toRelativePath(String path) {
        String res = path;
        if (file != null) {
            String s = file.getParent();
            if (path.startsWith(s)) {
                res = path.substring(s.length()+1);
            }
        }
        return res;
    }
    
    
    private void printIndent() {
        for (int i = 0; i < indent*indentSize; i++) {
            out.print(SPACE);
        }
    }
    
    
    private int                indentSize = 2;
    private int                indent;
    private PrintStream        out;
    private File               file = null;
    private LinkedList<String> elementStack;    
    private HashSet<String>    denoOccurrences;
    private Repository         rep = Repository.systemRepository();
}
